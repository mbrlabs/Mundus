/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mbrlabs.mundus.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.utils.MathUtils;
import com.mbrlabs.mundus.editor.core.Mundus;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.shader.Shaders;
import com.mbrlabs.mundus.editor.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.editor.tools.picker.ToolHandlePicker;
import com.mbrlabs.mundus.editor.utils.Fa;
import com.mbrlabs.mundus.editor.utils.UsefulMeshs;

import org.lwjgl.opengl.GL11;

/**
 * Rotate tool for game objects
 *
 * @author Marcus Brummer, codengima
 * @version 30-09-2016
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
    private Quaternion tempQuat = new Quaternion();

    private ShapeRenderer shapeRenderer;
    private ProjectContext projectContext;

    private TransformState state = TransformState.IDLE;
    private float lastRot = 0;

    public RotateTool(ProjectManager projectManager, GameObjectPicker goPicker, ToolHandlePicker handlePicker,
            Shader shader, ShapeRenderer shapeRenderer, ModelBatch batch, CommandHistory history) {
        super(projectManager, goPicker, handlePicker, shader, batch, history);
        this.shapeRenderer = shapeRenderer;
        this.projectContext = projectManager.current();
        xHandle = new RotateHandle(X_HANDLE_ID, COLOR_X);
        yHandle = new RotateHandle(Y_HANDLE_ID, COLOR_Y);
        zHandle = new RotateHandle(Z_HANDLE_ID, COLOR_Z);
        handles = new RotateHandle[] { xHandle, yHandle, zHandle };
    }

    @Override
    public void render() {
        super.render();

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        if (state == TransformState.IDLE && projectContext.currScene.currentSelection != null) {
            batch.begin(projectContext.currScene.cam);
            xHandle.render(batch);
            yHandle.render(batch);
            zHandle.render(batch);
            batch.end();
        } else if (projectContext.currScene.currentSelection != null) {
            Viewport vp = projectContext.currScene.viewport;

            GameObject go = projectContext.currScene.currentSelection;
            go.getTransform().getTranslation(temp0);
            Vector3 pivot = projectContext.currScene.cam.project(temp0);

            shapeRenderMat.setToOrtho2D(vp.getScreenX(), vp.getScreenY(), vp.getScreenWidth(), vp.getScreenHeight());
            switch (state) {
            case TRANSFORM_X:
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(Color.BLACK);
                shapeRenderer.setProjectionMatrix(shapeRenderMat);
                shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(),
                        2);
                shapeRenderer.setColor(COLOR_X);
                shapeRenderer.circle(pivot.x, pivot.y, 7);
                shapeRenderer.end();
                break;
            case TRANSFORM_Y:
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(Color.BLACK);
                shapeRenderer.setProjectionMatrix(shapeRenderMat);
                shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(),
                        2);
                shapeRenderer.setColor(COLOR_Y);
                shapeRenderer.circle(pivot.x, pivot.y, 7);
                shapeRenderer.end();
                break;
            case TRANSFORM_Z:
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(Color.BLACK);
                shapeRenderer.setProjectionMatrix(shapeRenderMat);
                shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(),
                        2);
                shapeRenderer.setColor(COLOR_Z);
                shapeRenderer.circle(pivot.x, pivot.y, 7);
                shapeRenderer.end();
                break;
            default:
                break;
            }
        }

    }

    @Override
    public void act() {
        super.act();

        if (projectContext.currScene.currentSelection != null) {
            translateHandles();
            if (state == TransformState.IDLE) {
                return;
            }

            float angle = getCurrentAngle();
            float rot = angle - lastRot;

            boolean modified = false;
            if (null != state) {
                switch (state) {
                case TRANSFORM_X:
                    tempQuat.setEulerAngles(0, -rot, 0);
                    projectContext.currScene.currentSelection.rotate(tempQuat);
                    modified = true;
                    break;
                case TRANSFORM_Y:
                    tempQuat.setEulerAngles(-rot, 0, 0);
                    projectContext.currScene.currentSelection.rotate(tempQuat);
                    modified = true;
                    break;
                case TRANSFORM_Z:
                    tempQuat.setEulerAngles(0, 0, -rot);
                    projectContext.currScene.currentSelection.rotate(tempQuat);
                    modified = true;
                    break;
                default:
                    break;
                }
            }

            if (modified) {
                gameObjectModifiedEvent.setGameObject(projectContext.currScene.currentSelection);
                Mundus.postEvent(gameObjectModifiedEvent);
            }

            lastRot = angle;

        }
    }

    private float getCurrentAngle() {
        if (projectContext.currScene.currentSelection != null) {
            projectContext.currScene.currentSelection.getPosition(temp0);
            Vector3 pivot = projectContext.currScene.cam.project(temp0);
            Vector3 mouse = temp1.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 0);

            return MathUtils.angle(pivot.x, pivot.y, mouse.x, mouse.y);
        }

        return 0;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if (button == Input.Buttons.LEFT && projectContext.currScene.currentSelection != null) {
            lastRot = getCurrentAngle();

            RotateHandle handle = (RotateHandle) handlePicker.pick(handles, projectContext.currScene, screenX, screenY);
            if (handle == null) {
                state = TransformState.IDLE;
                return false;
            }

            switch (handle.getId()) {
            case X_HANDLE_ID:
                state = TransformState.TRANSFORM_X;
                break;
            case Y_HANDLE_ID:
                state = TransformState.TRANSFORM_Y;
                break;
            case Z_HANDLE_ID:
                state = TransformState.TRANSFORM_Z;
                break;
            default:
                break;
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        state = TransformState.IDLE;
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
        final Vector3 pos = projectContext.currScene.currentSelection.getTransform().getTranslation(temp0);
        xHandle.position.set(pos);
        xHandle.applyTransform();
        yHandle.position.set(pos);
        yHandle.applyTransform();
        zHandle.position.set(pos);
        zHandle.applyTransform();
    }

    @Override
    protected void scaleHandles() {

        Vector3 pos = projectContext.currScene.currentSelection.getPosition(temp0);
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
            switch (id) {
            case X_HANDLE_ID:
                this.rotationEuler.y = 90;
                this.scale.x = 0.9f;
                this.scale.y = 0.9f;
                this.scale.z = 0.9f;
                break;
            case Y_HANDLE_ID:
                this.rotationEuler.x = 90;
                break;
            case Z_HANDLE_ID:
                this.rotationEuler.z = 90;
                this.scale.x = 1.1f;
                this.scale.y = 1.1f;
                this.scale.z = 1.1f;
                break;
            }
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

        @Override
        public void dispose() {
            model.dispose();
        }
    }

}
