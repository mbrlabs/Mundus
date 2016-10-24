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
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.utils.MathUtils;
import com.mbrlabs.mundus.editor.Mundus;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.history.commands.ScaleCommand;
import com.mbrlabs.mundus.editor.shader.Shaders;
import com.mbrlabs.mundus.editor.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.editor.tools.picker.ToolHandlePicker;
import com.mbrlabs.mundus.editor.ui.UI;
import com.mbrlabs.mundus.editor.utils.Fa;
import com.mbrlabs.mundus.editor.utils.UsefulMeshs;

import org.lwjgl.opengl.GL11;

/**
 * @author codenigma
 * @version 07-10-2016
 */
public class ScaleTool extends TransformTool {

    public static final String NAME = "Scale Tool";

    private final ScaleHandle xHandle;
    private final ScaleHandle yHandle;
    private final ScaleHandle zHandle;
    private final ScaleHandle xyzHandle;
    private final ScaleHandle[] handles;

    private final Matrix4 shapeRenderMat = new Matrix4();
    private Viewport viewport3d = null;

    private final Vector3 temp0 = new Vector3();
    private final Vector3 temp1 = new Vector3();
    private final Vector3 tempScale = new Vector3();
    private final Vector3 tempScaleDst = new Vector3();

    private ShapeRenderer shapeRenderer;

    private TransformState state = TransformState.IDLE;
    private ScaleCommand command;

    public ScaleTool(ProjectManager projectManager, GameObjectPicker goPicker, ToolHandlePicker handlePicker,
            ShapeRenderer shapeRenderer, ModelBatch batch, CommandHistory history) {
        super(projectManager, goPicker, handlePicker, batch, history);

        this.shapeRenderer = shapeRenderer;

        ModelBuilder modelBuilder = new ModelBuilder();

        Model xPlaneHandleModel = UsefulMeshs.createArrowStub(new Material(ColorAttribute.createDiffuse(COLOR_X)),
                Vector3.Zero, new Vector3(15, 0, 0));
        Model yPlaneHandleModel = UsefulMeshs.createArrowStub(new Material(ColorAttribute.createDiffuse(COLOR_Y)),
                Vector3.Zero, new Vector3(0, 15, 0));
        Model zPlaneHandleModel = UsefulMeshs.createArrowStub(new Material(ColorAttribute.createDiffuse(COLOR_Z)),
                Vector3.Zero, new Vector3(0, 0, 15));
        Model xyzPlaneHandleModel = modelBuilder.createBox(3, 3, 3,
                new Material(ColorAttribute.createDiffuse(COLOR_XYZ)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        xHandle = new ScaleHandle(X_HANDLE_ID, xPlaneHandleModel);
        yHandle = new ScaleHandle(Y_HANDLE_ID, yPlaneHandleModel);
        zHandle = new ScaleHandle(Z_HANDLE_ID, zPlaneHandleModel);
        xyzHandle = new ScaleHandle(XYZ_HANDLE_ID, xyzPlaneHandleModel);

        handles = new ScaleHandle[] { xHandle, yHandle, zHandle, xyzHandle };
    }

    @Override
    public void render() {
        super.render();

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        ProjectContext projectContext = getProjectManager().current();
        if (projectContext.currScene.currentSelection != null) {
            getBatch().begin(projectContext.currScene.cam);
            xHandle.render(getBatch());
            yHandle.render(getBatch());
            zHandle.render(getBatch());
            xyzHandle.render(getBatch());
            getBatch().end();

            GameObject go = projectContext.currScene.currentSelection;
            go.getTransform().getTranslation(temp0);
            if (viewport3d == null) {
                viewport3d = UI.INSTANCE.getSceneWidget().getViewport();
            }

            Vector3 pivot = projectContext.currScene.cam.project(temp0, viewport3d.getScreenX(),
                    viewport3d.getScreenY(), viewport3d.getWorldWidth(), viewport3d.getWorldHeight());

            shapeRenderMat.setToOrtho2D(viewport3d.getScreenX(), viewport3d.getScreenY(), viewport3d.getScreenWidth(),
                    viewport3d.getScreenHeight());
            switch (state) {
            case TRANSFORM_X:
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(COLOR_X);
                shapeRenderer.setProjectionMatrix(shapeRenderMat);
                shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(),
                        2);
                shapeRenderer.end();
                break;
            case TRANSFORM_Y:
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(COLOR_Y);
                shapeRenderer.setProjectionMatrix(shapeRenderMat);
                shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(),
                        2);
                shapeRenderer.end();
                break;
            case TRANSFORM_Z:
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(COLOR_Z);
                shapeRenderer.setProjectionMatrix(shapeRenderMat);
                shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(),
                        2);
                shapeRenderer.end();
                break;
            case TRANSFORM_XYZ:
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(COLOR_XYZ);
                shapeRenderer.setProjectionMatrix(shapeRenderMat);
                shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(),
                        2);
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
        ProjectContext projectContext = getProjectManager().current();
        if (projectContext.currScene.currentSelection != null) {
            translateHandles();
            if (state == TransformState.IDLE) {
                return;
            }
            float dst = getCurrentDst();

            boolean modified = false;
            if (null != state) {
                switch (state) {
                case TRANSFORM_X:
                    tempScale.x = (100 / tempScaleDst.x * dst) / 100;
                    projectContext.currScene.currentSelection.setLocalScale(tempScale.x, tempScale.y, tempScale.z);
                    modified = true;
                    break;
                case TRANSFORM_Y:
                    tempScale.y = (100 / tempScaleDst.y * dst) / 100;
                    projectContext.currScene.currentSelection.setLocalScale(tempScale.x, tempScale.y, tempScale.z);
                    modified = true;
                    break;
                case TRANSFORM_Z:
                    tempScale.z = (100 / tempScaleDst.z * dst) / 100;
                    projectContext.currScene.currentSelection.setLocalScale(tempScale.x, tempScale.y, tempScale.z);
                    modified = true;
                    break;
                case TRANSFORM_XYZ:
                    tempScale.x = (100 / tempScaleDst.x * dst) / 100;
                    tempScale.y = (100 / tempScaleDst.y * dst) / 100;
                    tempScale.z = (100 / tempScaleDst.z * dst) / 100;
                    projectContext.currScene.currentSelection.setLocalScale(tempScale.x, tempScale.y, tempScale.z);
                    modified = true;
                    break;
                default:
                    break;
                }
            }
            if (modified) {
                gameObjectModifiedEvent.setGameObject(projectContext.currScene.currentSelection);
                Mundus.INSTANCE.postEvent(gameObjectModifiedEvent);
            }
        }
    }

    private float getCurrentDst() {
        ProjectContext projectContext = getProjectManager().current();
        if (projectContext.currScene.currentSelection != null) {
            projectContext.currScene.currentSelection.getTransform().getTranslation(temp0);
            Vector3 pivot = projectContext.currScene.cam.project(temp0, viewport3d.getScreenX(),
                    viewport3d.getScreenY(), viewport3d.getWorldWidth(), viewport3d.getWorldHeight());
            Vector3 mouse = temp1.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 0);

            return MathUtils.dst(pivot.x, pivot.y, mouse.x, mouse.y);
        }
        return 0;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        ProjectContext projectContext = getProjectManager().current();
        super.touchDown(screenX, screenY, pointer, button);
        if (button == Input.Buttons.LEFT && projectContext.currScene.currentSelection != null) {
            ScaleHandle handle = (ScaleHandle) handlePicker.pick(handles, projectContext.currScene, screenX, screenY);
            if (handle == null) {
                state = TransformState.IDLE;
                return false;
            }
            // current scale
            projectContext.currScene.currentSelection.getScale(tempScale);

            // set tempScaleDst
            tempScaleDst.x = getCurrentDst() / tempScale.x;
            tempScaleDst.y = getCurrentDst() / tempScale.y;
            tempScaleDst.z = getCurrentDst() / tempScale.z;

            switch (handle.getId()) {
            case X_HANDLE_ID:
                state = TransformState.TRANSFORM_X;
                xHandle.changeColor(COLOR_SELECTED);
                break;
            case Y_HANDLE_ID:
                state = TransformState.TRANSFORM_Y;
                yHandle.changeColor(COLOR_SELECTED);
                break;
            case Z_HANDLE_ID:
                state = TransformState.TRANSFORM_Z;
                zHandle.changeColor(COLOR_SELECTED);
                break;
            case XYZ_HANDLE_ID:
                state = TransformState.TRANSFORM_XYZ;
                xyzHandle.changeColor(COLOR_SELECTED);
                break;
            default:
                break;
            }
        }

        // scale command before
        if (state != TransformState.IDLE) {
            command = new ScaleCommand(getProjectManager().current().currScene.currentSelection);
            command.setBefore(tempScale);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        ProjectContext projectContext = getProjectManager().current();
        if (state != TransformState.IDLE) {
            xHandle.changeColor(COLOR_X);
            yHandle.changeColor(COLOR_Y);
            zHandle.changeColor(COLOR_Z);
            xyzHandle.changeColor(COLOR_XYZ);

            // scale command after
            projectContext.currScene.currentSelection.getScale(tempScale);
            command.setAfter(tempScale);
            getHistory().add(command);
            command = null;
            state = TransformState.IDLE;
        }
        return false;
    }

    @Override
    public void gameObjectSelected(GameObject selection) {
        super.gameObjectSelected(selection);
        // configure handles
        scaleHandles();
        rotateHandles();
        translateHandles();
    }

    @Override
    protected void rotateHandles() {
        // not needed
    }

    @Override
    protected void translateHandles() {
        ProjectContext projectContext = getProjectManager().current();
        final Vector3 pos = projectContext.currScene.currentSelection.getTransform().getTranslation(temp0);
        xHandle.getPosition().set(pos);
        xHandle.applyTransform();
        yHandle.getPosition().set(pos);
        yHandle.applyTransform();
        zHandle.getPosition().set(pos);
        zHandle.applyTransform();
        xyzHandle.getPosition().set(pos);
        xyzHandle.applyTransform();
    }

    @Override
    protected void scaleHandles() {
        Vector3 pos = getProjectManager().current().currScene.currentSelection.getPosition(temp0);
        float scaleFactor = getProjectManager().current().currScene.cam.position.dst(pos) * 0.01f;
        xHandle.getScale().set(scaleFactor, scaleFactor, scaleFactor);

        xHandle.applyTransform();

        yHandle.getScale().set(scaleFactor, scaleFactor, scaleFactor);
        yHandle.applyTransform();

        zHandle.getScale().set(scaleFactor, scaleFactor, scaleFactor);
        zHandle.applyTransform();

        xyzHandle.getScale().set(scaleFactor, scaleFactor, scaleFactor);
        xyzHandle.applyTransform();
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
        return Fa.Companion.getEXPAND();
    }

    @Override
    public void dispose() {
        super.dispose();
        xHandle.dispose();
        yHandle.dispose();
        zHandle.dispose();
        xyzHandle.dispose();
    }

    private class ScaleHandle extends ToolHandle {

        private Model model;
        private ModelInstance modelInstance;

        public ScaleHandle(int id, Model model) {
            super(id);
            this.model = model;
            this.modelInstance = new ModelInstance(model);
            modelInstance.materials.first().set(getIdAttribute());

        }

        public void changeColor(Color color) {
            ColorAttribute diffuse = (ColorAttribute) modelInstance.materials.get(0).get(ColorAttribute.Diffuse);
            diffuse.color.set(color);
        }

        @Override
        public void render(ModelBatch batch) {
            batch.render(modelInstance);
        }

        @Override
        public void renderPick(ModelBatch modelBatch) {
            getBatch().render(modelInstance, Shaders.INSTANCE.getPickerShader());
        }

        @Override
        public void act() {
        }

        @Override
        public void applyTransform() {
            modelInstance.transform.set(getPosition(), getRotation(), getScale());
        }

        @Override
        public void dispose() {
            model.dispose();
        }
    }

}
