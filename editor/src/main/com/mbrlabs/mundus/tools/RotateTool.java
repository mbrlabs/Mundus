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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.history.CommandHistory;
import com.mbrlabs.mundus.history.commands.TranslateCommand;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.tools.picker.ToolHandlePicker;
import com.mbrlabs.mundus.utils.Fa;
import com.mbrlabs.mundus.utils.UsefulMeshs;
import org.lwjgl.opengl.GL11;

/**
 * @author Marcus Brummer
 * @version 19-02-2016
 */
public class RotateTool extends SelectionTool {

    private enum State {
        ROTATE_X, ROTATE_Y, ROTATE_Z, IDLE
    }

    public static final String NAME = "Rotate Tool";

    private static Color COLOR_X = Color.RED;
    private static Color COLOR_Y = Color.GREEN;
    private static Color COLOR_Z = Color.BLUE;
    private static Color COLOR_SELECTED = Color.YELLOW;

    private RotateHandle xHandle;
    private RotateHandle yHandle;
    private RotateHandle zHandle;
    private RotateHandle[] handles;

    private Vector3 temp0 = new Vector3();

    private State state = State.IDLE;
    private boolean initRotate = true;

    private ToolHandlePicker handlePicker;

    public RotateTool(ProjectContext projectContext, GameObjectPicker goPicker,
                      ToolHandlePicker handlePicker, Shader shader, ModelBatch batch, CommandHistory history) {
        super(projectContext, goPicker, shader, batch, history);
        this.handlePicker = handlePicker;

        xHandle = new RotateHandle(RotateHandle.X_HANDLE_ID, COLOR_X);
        yHandle = new RotateHandle(RotateHandle.Y_HANDLE_ID, COLOR_Y);
        zHandle = new RotateHandle(RotateHandle.Z_HANDLE_ID, COLOR_Z);
        handles = new RotateHandle[]{xHandle, yHandle, zHandle};
    }

    @Override
    public void render() {
        super.render();
        if(projectContext.currScene.currentSelection != null) {
            batch.begin(projectContext.currScene.cam);
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            xHandle.render(batch);
            yHandle.render(batch);
            zHandle.render(batch);
            batch.end();
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if(button == Input.Buttons.LEFT && projectContext.currScene.currentSelection != null) {
            RotateHandle handle = (RotateHandle) handlePicker.pick(handles, projectContext.currScene, screenX, screenY);
            if(handle == null) {
                state = State.IDLE;
                return false;
            }

            if(handle.getId() == RotateHandle.X_HANDLE_ID) {
                state = State.ROTATE_X;
                initRotate = true;
                xHandle.changeColor(COLOR_SELECTED);
            } else if(handle.getId() == RotateHandle.Y_HANDLE_ID) {
                state = State.ROTATE_Y;
                initRotate = true;
                yHandle.changeColor(COLOR_SELECTED);
            } else if(handle.getId() == RotateHandle.Z_HANDLE_ID) {
                state = State.ROTATE_Z;
                initRotate = true;
                zHandle.changeColor(COLOR_SELECTED);
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        if(state != State.IDLE) {
            xHandle.changeColor(COLOR_X);
            yHandle.changeColor(COLOR_Y);
            zHandle.changeColor(COLOR_Z);

            state = State.IDLE;
        }
        return false;
    }

    @Override
    public void gameObjectSelected(GameObject selection) {
        super.gameObjectSelected(selection);
        scaleHandles();
        rotateHandles();
        positionHandles();
    }

    private void rotateHandles() {
        final GameObject go = projectContext.currScene.currentSelection;
        xHandle.rotationEuler.set(go.rotation.x, go.rotation.y + 90, go.rotation.z);
        xHandle.applyTransform();
        yHandle.rotationEuler.set(go.rotation.x - 90, go.rotation.y, go.rotation.z);
        yHandle.applyTransform();
        zHandle.rotationEuler.set(go.rotation.x, go.rotation.y, go.rotation.z);
        zHandle.applyTransform();
    }

    private void positionHandles() {
        final Vector3 medium = projectContext.currScene.currentSelection.calculateMedium(temp0);
        xHandle.position.set(medium);
        xHandle.applyTransform();
        yHandle.position.set(medium);
        yHandle.applyTransform();
        zHandle.position.set(medium);
        zHandle.applyTransform();
    }

    private void scaleHandles() {
        Vector3 pos = projectContext.currScene.currentSelection.position;
        float scaleFactor = projectContext.currScene.cam.position.dst(pos) * 0.01f;
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

        public static final int X_HANDLE_ID = 0;
        public static final int Y_HANDLE_ID = 1;
        public static final int Z_HANDLE_ID = 2;

        private Model model;
        private ModelInstance modelInstance;

        public RotateHandle(int id, Color color) {
            super(id);
            model = UsefulMeshs.torus(new Material(ColorAttribute.createDiffuse(color)), 20, 1f, 50, 50);
            modelInstance = new ModelInstance(model);
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
