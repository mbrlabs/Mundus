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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.editor.core.Mundus;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.GameObjectModifiedEvent;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.history.commands.TranslateCommand;
import com.mbrlabs.mundus.editor.shader.Shaders;
import com.mbrlabs.mundus.editor.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.editor.tools.picker.ToolHandlePicker;
import com.mbrlabs.mundus.editor.utils.Fa;

import org.lwjgl.opengl.GL11;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
public class TranslateTool extends TransformTool {

    private final float ARROW_THIKNESS = 0.4f;
    private final float ARROW_CAP_SIZE = 0.15f;
    private final int ARROW_DIVISIONS = 12;

    public static final String NAME = "Translate Tool";

    private TransformState state = TransformState.IDLE;
    private boolean initTranslate = true;

    private TranslateHandle xHandle;
    private TranslateHandle yHandle;
    private TranslateHandle zHandle;
    private TranslateHandle xzPlaneHandle;
    private TranslateHandle[] handles;

    private Vector3 lastPos = new Vector3();
    private boolean globalSpace = true;

    private Vector3 temp0 = new Vector3();

    private TranslateCommand command;

    public TranslateTool(ProjectManager projectManager, GameObjectPicker goPicker, ToolHandlePicker handlePicker,
            Shader shader, ModelBatch batch, CommandHistory history) {

        super(projectManager, goPicker, handlePicker, shader, batch, history);

        ModelBuilder modelBuilder = new ModelBuilder();

        Model xHandleModel = modelBuilder.createArrow(0, 0, 0, 1, 0, 0, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS,
                GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(COLOR_X)),
                VertexAttributes.Usage.Position);
        Model yHandleModel = modelBuilder.createArrow(0, 0, 0, 0, 1, 0, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS,
                GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(COLOR_Y)),
                VertexAttributes.Usage.Position);
        Model zHandleModel = modelBuilder.createArrow(0, 0, 0, 0, 0, 1, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS,
                GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(COLOR_Z)),
                VertexAttributes.Usage.Position);
        Model xzPlaneHandleModel = modelBuilder.createSphere(1, 1, 1, 20, 20,
                new Material(ColorAttribute.createDiffuse(COLOR_XZ)), VertexAttributes.Usage.Position);

        xHandle = new TranslateHandle(X_HANDLE_ID, xHandleModel);
        yHandle = new TranslateHandle(Y_HANDLE_ID, yHandleModel);
        zHandle = new TranslateHandle(Z_HANDLE_ID, zHandleModel);
        xzPlaneHandle = new TranslateHandle(XZ_HANDLE_ID, xzPlaneHandleModel);
        handles = new TranslateHandle[] { xHandle, yHandle, zHandle, xzPlaneHandle };

        gameObjectModifiedEvent = new GameObjectModifiedEvent();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Drawable getIcon() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getIconFont() {
        return Fa.ARROWS;
    }

    @Override
    public void gameObjectSelected(GameObject go) {
        super.gameObjectSelected(go);
        scaleHandles();
        translateHandles();
    }

    public void setGlobalSpace(boolean global) {
        this.globalSpace = global;
        xHandle.rotation.idt();
        xHandle.applyTransform();

        yHandle.rotation.idt();
        yHandle.applyTransform();

        zHandle.rotation.idt();
        zHandle.applyTransform();
    }

    @Override
    public void render() {
        super.render();
        if (projectManager.current().currScene.currentSelection != null) {
            batch.begin(projectManager.current().currScene.cam);
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            xHandle.render(batch);
            yHandle.render(batch);
            zHandle.render(batch);
            xzPlaneHandle.render(batch);

            batch.end();
        }
    }

    @Override
    public void act() {
        super.act();

        if (projectManager.current().currScene.currentSelection != null) {
            translateHandles();
            if (state == TransformState.IDLE) return;

            Ray ray = projectManager.current().currScene.viewport.getPickRay(Gdx.input.getX(), Gdx.input.getY());
            Vector3 rayEnd = projectManager.current().currScene.currentSelection.getLocalPosition(temp0);
            float dst = projectManager.current().currScene.cam.position.dst(rayEnd);
            rayEnd = ray.getEndPoint(rayEnd, dst);

            if (initTranslate) {
                initTranslate = false;
                lastPos.set(rayEnd);
            }

            GameObject go = projectManager.current().currScene.currentSelection;

            boolean modified = false;
            Vector3 vec = new Vector3();
            if (state == TransformState.TRANSFORM_XZ) {
                vec.set(rayEnd.x - lastPos.x, 0, rayEnd.z - lastPos.z);
                modified = true;
            } else if (state == TransformState.TRANSFORM_X) {
                vec.set(rayEnd.x - lastPos.x, 0, 0);
                modified = true;
            } else if (state == TransformState.TRANSFORM_Y) {
                vec.set(0, rayEnd.y - lastPos.y, 0);
                modified = true;
            } else if (state == TransformState.TRANSFORM_Z) {
                vec.set(0, 0, rayEnd.z - lastPos.z);
                modified = true;
            }

            // TODO translation in global sapce
            // if(globalSpace) {
            // System.out.println("Before: " + vec);
            // System.out.println("After: " + vec);
            // }

            go.translate(vec);

            if (modified) {
                gameObjectModifiedEvent.setGameObject(projectManager.current().currScene.currentSelection);
                Mundus.postEvent(gameObjectModifiedEvent);
            }

            lastPos.set(rayEnd);
        }
    }

    @Override
    protected void scaleHandles() {
        Vector3 pos = projectManager.current().currScene.currentSelection.getPosition(temp0);
        float scaleFactor = projectManager.current().currScene.cam.position.dst(pos) * 0.25f;
        xHandle.scale.set(scaleFactor * 0.7f, scaleFactor / 2, scaleFactor / 2);
        xHandle.applyTransform();

        yHandle.scale.set(scaleFactor / 2, scaleFactor * 0.7f, scaleFactor / 2);
        yHandle.applyTransform();

        zHandle.scale.set(scaleFactor / 2, scaleFactor / 2, scaleFactor * 0.7f);
        zHandle.applyTransform();

        xzPlaneHandle.scale.set(scaleFactor * 0.13f, scaleFactor * 0.13f, scaleFactor * 0.13f);
        xzPlaneHandle.applyTransform();
    }

    @Override
    protected void translateHandles() {
        final Vector3 pos = projectManager.current().currScene.currentSelection.getTransform().getTranslation(temp0);
        xHandle.position.set(pos);
        xHandle.applyTransform();
        yHandle.position.set(pos);
        yHandle.applyTransform();
        zHandle.position.set(pos);
        zHandle.applyTransform();
        xzPlaneHandle.position.set(pos);
        xzPlaneHandle.applyTransform();
    }

    @Override
    protected void rotateHandles() {
        // no rotation for this one
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if (button == Input.Buttons.LEFT && projectManager.current().currScene.currentSelection != null) {
            TranslateHandle handle = (TranslateHandle) handlePicker.pick(handles, projectManager.current().currScene,
                    screenX, screenY);
            if (handle == null) {
                state = TransformState.IDLE;
                return false;
            }

            if (handle.getId() == XZ_HANDLE_ID) {
                state = TransformState.TRANSFORM_XZ;
                initTranslate = true;
                xzPlaneHandle.changeColor(COLOR_SELECTED);
            } else if (handle.getId() == X_HANDLE_ID) {
                state = TransformState.TRANSFORM_X;
                initTranslate = true;
                xHandle.changeColor(COLOR_SELECTED);
            } else if (handle.getId() == Y_HANDLE_ID) {
                state = TransformState.TRANSFORM_Y;
                initTranslate = true;
                yHandle.changeColor(COLOR_SELECTED);
            } else if (handle.getId() == Z_HANDLE_ID) {
                state = TransformState.TRANSFORM_Z;
                initTranslate = true;
                zHandle.changeColor(COLOR_SELECTED);
            }
        }

        if (state != TransformState.IDLE) {
            command = new TranslateCommand(projectManager.current().currScene.currentSelection);
            command.setBefore(projectManager.current().currScene.currentSelection.getLocalPosition(temp0));
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        if (state != TransformState.IDLE) {
            xHandle.changeColor(COLOR_X);
            yHandle.changeColor(COLOR_Y);
            zHandle.changeColor(COLOR_Z);
            xzPlaneHandle.changeColor(COLOR_XZ);

            command.setAfter(projectManager.current().currScene.currentSelection.getLocalPosition(temp0));
            history.add(command);
            command = null;
            state = TransformState.IDLE;
        }
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();
        xHandle.dispose();
        yHandle.dispose();
        zHandle.dispose();
        xzPlaneHandle.dispose();
    }

    /**
     * 
     */
    private class TranslateHandle extends ToolHandle {

        private Model model;
        private ModelInstance modelInstance;

        public TranslateHandle(int id, Model model) {
            super(id);
            this.model = model;
            this.modelInstance = new ModelInstance(model);
            modelInstance.materials.first().set(idAttribute);
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
            this.model.dispose();
        }

    }

}
