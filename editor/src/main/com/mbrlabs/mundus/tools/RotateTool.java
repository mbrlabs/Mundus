/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.utils.MathUtils;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.history.CommandHistory;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.tools.picker.ToolHandlePicker;
import com.mbrlabs.mundus.utils.Colors;
import com.mbrlabs.mundus.utils.Fa;
import com.mbrlabs.mundus.utils.UsefulMeshs;
import org.lwjgl.opengl.GL11;

/**
 * @author Marcus Brummer
 * @version 19-02-2016
 */
public class RotateTool extends TransformTool {

    public static final String NAME = "Rotate Tool";

    private RotateHandle xHandle;
    private RotateHandle yHandle;
    private RotateHandle zHandle;
    private RotateHandle[] handles;

    private Matrix4 shapeRenderMat = new Matrix4();

    private Vector3 temp0 = new Vector3();
    private Vector3 temp1 = new Vector3();


    private ShapeRenderer shapeRenderer;

    private TransformState state = TransformState.IDLE;
    private boolean initRotate = true;
    private float lastRot = 0;

    public RotateTool(ProjectManager projectManager, GameObjectPicker goPicker, ToolHandlePicker handlePicker,
                      Shader shader, ShapeRenderer shapeRenderer, ModelBatch batch, CommandHistory history) {
        super(projectManager, goPicker, handlePicker, shader, batch, history);
        this.shapeRenderer = shapeRenderer;

        xHandle = new RotateHandle(X_HANDLE_ID, COLOR_X);
        yHandle = new RotateHandle(Y_HANDLE_ID, COLOR_Y);
        zHandle = new RotateHandle(Z_HANDLE_ID, COLOR_Z);
        handles = new RotateHandle[]{xHandle, yHandle, zHandle};
    }

    @Override
    public void render() {
        super.render();

        ProjectContext projectContext = projectManager.current();

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        if(state == TransformState.IDLE && projectContext.currScene.currentSelection != null) {
            batch.begin(projectContext.currScene.cam);
            xHandle.render(batch);
            yHandle.render(batch);
            zHandle.render(batch);
            batch.end();
        } else if(projectContext.currScene.currentSelection != null) {
            Viewport vp = projectContext.currScene.viewport;

            GameObject go = projectContext.currScene.currentSelection;
            go.calculateMedium(temp0);
            Vector3 pivot = projectContext.currScene.cam.project(temp0);

            shapeRenderMat.setToOrtho2D(vp.getScreenX(), vp.getScreenY(), vp.getScreenWidth(), vp.getScreenHeight());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.setProjectionMatrix(shapeRenderMat);
            shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 2);
            shapeRenderer.setColor(Colors.TURQUOISE);
            shapeRenderer.circle(pivot.x, pivot.y, 7);
            shapeRenderer.end();
        }

    }

    @Override
    public void act() {
        super.act();

        ProjectContext projectContext = projectManager.current();

        if(projectContext.currScene.currentSelection != null) {
            translateHandles();
            if(state == TransformState.IDLE) return;

            float angle = getCurrentAngle();
            float rot = angle - lastRot;

            boolean modified = false;
            if(state == TransformState.TRANSFORM_X) {
                projectContext.currScene.currentSelection.rot(-rot, 0, 0);
                modified = true;
            } else if(state == TransformState.TRANSFORM_Y) {
                projectContext.currScene.currentSelection.rot(0, -rot, 0);
                modified = true;
            } else if(state == TransformState.TRANSFORM_Z) {
                projectContext.currScene.currentSelection.rot(0, 0, -rot);
                modified = true;
            }

            if(modified) {
                gameObjectModifiedEvent.setGameObject(projectContext.currScene.currentSelection);
                Mundus.postEvent(gameObjectModifiedEvent);
            }

            lastRot = angle;

        }
    }

    private float getCurrentAngle() {
        ProjectContext projectContext = projectManager.current();

        if(projectContext.currScene.currentSelection != null) {
            temp0.set(projectContext.currScene.currentSelection.position);
            Vector3 pivot = projectContext.currScene.cam.project(temp0);
            Vector3 mouse = temp1.set(Gdx.input.getX(), Gdx.graphics.getHeight() -  Gdx.input.getY(), 0);

            return MathUtils.angle(pivot.x, pivot.y, mouse.x, mouse.y);
        }

        return 0;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);
        ProjectContext projectContext = projectManager.current();

        if(button == Input.Buttons.LEFT && projectContext.currScene.currentSelection != null) {
            lastRot = getCurrentAngle();

            RotateHandle handle = (RotateHandle) handlePicker.pick(handles, projectContext.currScene, screenX, screenY);
            if(handle == null) {
                state = TransformState.IDLE;
                return false;
            }

            if(handle.getId() == X_HANDLE_ID) {
                state = TransformState.TRANSFORM_X;
                initRotate = true;
                xHandle.changeColor(COLOR_SELECTED);
            } else if(handle.getId() == Y_HANDLE_ID) {
                state = TransformState.TRANSFORM_Y;
                initRotate = true;
                yHandle.changeColor(COLOR_SELECTED);
            } else if(handle.getId() == Z_HANDLE_ID) {
                state = TransformState.TRANSFORM_Z;
                initRotate = true;
                zHandle.changeColor(COLOR_SELECTED);
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        if(state != TransformState.IDLE) {
            xHandle.changeColor(COLOR_X);
            yHandle.changeColor(COLOR_Y);
            zHandle.changeColor(COLOR_Z);

            state = TransformState.IDLE;
        }
        return false;
    }

    @Override
    public void gameObjectSelected(GameObject selection) {
        super.gameObjectSelected(selection);
        scaleHandles();
        rotateHandles();
        translateHandles();
    }

    @Override
    protected void rotateHandles() {
        xHandle.rotationEuler.set(0, 90, 0);
        xHandle.applyTransform();
        yHandle.rotationEuler.set(90, 0, 0);
        yHandle.applyTransform();
        zHandle.rotationEuler.set(0, 0, 0);
        zHandle.applyTransform();
    }

    @Override
    protected void translateHandles() {
        final Vector3 medium = projectManager.current().currScene.currentSelection.calculateMedium(temp0);
        xHandle.position.set(medium);
        xHandle.applyTransform();
        yHandle.position.set(medium);
        yHandle.applyTransform();
        zHandle.position.set(medium);
        zHandle.applyTransform();
    }

    @Override
    protected void scaleHandles() {
        ProjectContext projectContext = projectManager.current();

        Vector3 pos = projectContext.currScene.currentSelection.position;
        float scaleFactor = projectContext.currScene.cam.position.dst(pos) * 0.005f;
        xHandle.scale.set(scaleFactor, scaleFactor, scaleFactor);
        xHandle.applyTransform();

        yHandle.scale.set(scaleFactor, scaleFactor, scaleFactor);
        yHandle.applyTransform();

        zHandle.scale.set(scaleFactor, scaleFactor, scaleFactor);
        zHandle.applyTransform();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Drawable getIcon() {
        return null;
    }

    @Override
    public String getIconFont() {
        return Fa.REFRESH;
    }

    @Override
    public void dispose() {
        super.dispose();
        xHandle.dispose();
        yHandle.dispose();
        zHandle.dispose();
    }

    /**
     *
     */
    private class RotateHandle extends ToolHandle {
        private Model model;
        private ModelInstance modelInstance;

        public RotateHandle(int id, Color color) {
            super(id);
            model = UsefulMeshs.torus(new Material(ColorAttribute.createDiffuse(color)), 20, 1f, 50, 50);
            modelInstance = new ModelInstance(model);
            modelInstance.materials.first().set(idAttribute);
            // mi.transform.translate(0, 100, 0);
        }

        @Override
        public void render(ModelBatch batch) {
            batch.render(modelInstance);
        }

        @Override
        public void renderPick(ModelBatch modelBatch) {
            batch.render(modelInstance, Shaders.pickerShader);
        }

        @Override
        public void act() {

        }

        @Override
        public void applyTransform() {
            rotation.setEulerAngles(rotationEuler.y, rotationEuler.x, rotationEuler.z);
            modelInstance.transform.set(position, rotation, scale);
        }

        public void changeColor(Color color) {
            ColorAttribute diffuse = (ColorAttribute) modelInstance.materials.get(0).get(ColorAttribute.Diffuse);
            diffuse.color.set(color);
        }

        @Override
        public void dispose() {
            model.dispose();
        }
    }

}
