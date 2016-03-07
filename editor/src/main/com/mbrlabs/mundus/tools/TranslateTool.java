/*
 * Copyright (c) 2015. See AUTHORS file.
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
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.events.GameObjectModifiedEvent;
import com.mbrlabs.mundus.history.CommandHistory;
import com.mbrlabs.mundus.history.commands.TranslateCommand;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.tools.picker.ToolHandlePicker;
import com.mbrlabs.mundus.utils.Fa;
import org.lwjgl.opengl.GL11;
import org.omg.CORBA.ValueBaseHelper;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
public class TranslateTool extends SelectionTool {

    private enum State {
        TRANSLATE_X, TRANSLATE_Y, TRANSLATE_Z, TRANSLATE_XZ, IDLE
    }

    private static Color COLOR_X = Color.RED;
    private static Color COLOR_Y = Color.GREEN;
    private static Color COLOR_Z = Color.BLUE;
    private static Color COLOR_XZ = Color.CYAN;
    private static Color COLOR_SELECTED = Color.YELLOW;

    private final float ARROW_THIKNESS = 0.4f;
    private final float ARROW_CAP_SIZE = 0.15f;
    private final int ARROW_DIVISIONS = 12;

    public static final String NAME = "Translate Tool";
    private Drawable icon;

    private State state = State.IDLE;
    private boolean initTranslate = true;

    private TranslateHandle xHandle;
    private TranslateHandle yHandle;
    private TranslateHandle zHandle;
    private TranslateHandle xzPlaneHandle;
    private TranslateHandle[] handles;

    private Vector3 lastPos = new Vector3();
    private boolean globalSpace = true;

    private Vector3 temp0 = new Vector3();

    private ToolHandlePicker handlePicker;

    private GameObjectModifiedEvent gameObjectModifiedEvent;
    private TranslateCommand command;


    public TranslateTool(ProjectContext projectContext,
                         GameObjectPicker goPicker,
                         ToolHandlePicker handlePicker,
                         Shader shader, ModelBatch batch, CommandHistory history) {

        super(projectContext, goPicker, shader, batch, history);
        this.handlePicker = handlePicker;
        icon = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/translateTool.png"))));

        ModelBuilder modelBuilder = new ModelBuilder();

        Model xHandleModel =  modelBuilder.createArrow(0, 0, 0, 1, 0, 0, ARROW_CAP_SIZE, ARROW_THIKNESS,
                ARROW_DIVISIONS, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(COLOR_X)),
                VertexAttributes.Usage.Position);
        Model yHandleModel =  modelBuilder.createArrow(0, 0, 0, 0, 1, 0, ARROW_CAP_SIZE, ARROW_THIKNESS,
                ARROW_DIVISIONS, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(COLOR_Y)),
                VertexAttributes.Usage.Position);
        Model zHandleModel =  modelBuilder.createArrow(0, 0, 0, 0, 0, 1, ARROW_CAP_SIZE, ARROW_THIKNESS,
                ARROW_DIVISIONS, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(COLOR_Z)),
                VertexAttributes.Usage.Position);
        Model xzPlaneHandleModel = modelBuilder.createSphere(1, 1, 1, 20, 20,
                new Material(ColorAttribute.createDiffuse(COLOR_XZ)),
                VertexAttributes.Usage.Position);

        xHandle = new TranslateHandle(TranslateHandle.X_HANDLE_ID, xHandleModel);
        yHandle = new TranslateHandle(TranslateHandle.Y_HANDLE_ID, yHandleModel);
        zHandle = new TranslateHandle(TranslateHandle.Z_HANDLE_ID, zHandleModel);
        xzPlaneHandle = new TranslateHandle(TranslateHandle.XZ_HANDLE_ID, xzPlaneHandleModel);
        handles = new TranslateHandle[]{xHandle, yHandle, zHandle, xzPlaneHandle};

        gameObjectModifiedEvent = new GameObjectModifiedEvent();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Drawable getIcon() {
        return icon;
    }

    @Override
    public String getIconFont() {
        return Fa.ARROWS;
    }

    @Override
    public void gameObjectSelected(GameObject go) {
        super.gameObjectSelected(go);
        scaleHandles();
        positionHandles();
    }

    public void setGlobalSpace(boolean global) {
        this.globalSpace = global;
        xHandle.rotation.idt();
        xHandle.applyTransform();

        yHandle.rotation.idt();
        yHandle.applyTransform();

        zHandle.rotation.idt();
        zHandle.applyTransform();

//        if(!global) {
//            xHandle.transform.rotate(projectContext.currScene.currentSelection.rotation);
//            yHandle.transform.rotate(projectContext.currScene.currentSelection.rotation);
//            zHandle.transform.rotate(projectContext.currScene.currentSelection.rotation);
//        }
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
            xzPlaneHandle.render(batch);

            batch.end();
        }
    }

    @Override
    public void act() {
        super.act();

        if(projectContext.currScene.currentSelection != null) {
            positionHandles();
            if(state == State.IDLE) return;

            Ray ray = projectContext.currScene.viewport.getPickRay(Gdx.input.getX(), Gdx.input.getY());
            Vector3 rayEnd = temp0.set(projectContext.currScene.currentSelection.position);
            float dst = projectContext.currScene.cam.position.dst(rayEnd);
            rayEnd = ray.getEndPoint(rayEnd, dst);

            if(initTranslate) {
                initTranslate = false;
                lastPos.set(rayEnd);
            }

            boolean modified = false;
            if(state == State.TRANSLATE_XZ) {
                projectContext.currScene.currentSelection.trans(rayEnd.x - lastPos.x,
                        0, rayEnd.z - lastPos.z);
                modified = true;
            } else if(state == State.TRANSLATE_X) {
                projectContext.currScene.currentSelection.trans(rayEnd.x - lastPos.x,
                        0, 0);
                modified = true;
            } else if(state == State.TRANSLATE_Y) {
                projectContext.currScene.currentSelection.trans(0,
                        rayEnd.y - lastPos.y, 0);
                modified = true;
            } else if(state == State.TRANSLATE_Z) {
                projectContext.currScene.currentSelection.trans(0, 0,
                        rayEnd.z - lastPos.z);
                modified = true;
            }

            if(modified) {
                gameObjectModifiedEvent.setGameObject(projectContext.currScene.currentSelection);
                Mundus.postEvent(gameObjectModifiedEvent);
            }

            lastPos.set(rayEnd);
        }
    }

    private void scaleHandles() {
        Vector3 pos = projectContext.currScene.currentSelection.position;
        float scaleFactor = projectContext.currScene.cam.position.dst(pos) * 0.25f;
        xHandle.scale.set(scaleFactor * 0.7f, scaleFactor / 2, scaleFactor / 2);
        xHandle.applyTransform();

        yHandle.scale.set(scaleFactor / 2, scaleFactor * 0.7f, scaleFactor / 2);
        yHandle.applyTransform();

        zHandle.scale.set(scaleFactor / 2, scaleFactor / 2, scaleFactor * 0.7f);
        zHandle.applyTransform();

        xzPlaneHandle.scale.set(scaleFactor*0.13f,scaleFactor*0.13f, scaleFactor*0.13f);
        xzPlaneHandle.applyTransform();
    }

    private void positionHandles() {
        final Vector3 medium = projectContext.currScene.currentSelection.calculateMedium(temp0);
        xHandle.position.set(medium);
        xHandle.applyTransform();
        yHandle.position.set(medium);
        yHandle.applyTransform();
        zHandle.position.set(medium);
        zHandle.applyTransform();
        xzPlaneHandle.position.set(medium);
        xzPlaneHandle.applyTransform();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if(button == Input.Buttons.LEFT && projectContext.currScene.currentSelection != null) {
            TranslateHandle handle = (TranslateHandle) handlePicker.pick(handles, projectContext.currScene, screenX, screenY);
            if(handle == null) {
                state = State.IDLE;
                return false;
            }

            if(handle.getId() == TranslateHandle.XZ_HANDLE_ID) {
                state = State.TRANSLATE_XZ;
                initTranslate = true;
                xzPlaneHandle.changeColor(COLOR_SELECTED);
            } else if(handle.getId() == TranslateHandle.X_HANDLE_ID) {
                state = State.TRANSLATE_X;
                initTranslate = true;
                xHandle.changeColor(COLOR_SELECTED);
            } else if(handle.getId() == TranslateHandle.Y_HANDLE_ID) {
                state = State.TRANSLATE_Y;
                initTranslate = true;
                yHandle.changeColor(COLOR_SELECTED);
            } else if(handle.getId() == TranslateHandle.Z_HANDLE_ID) {
                state = State.TRANSLATE_Z;
                initTranslate = true;
                zHandle.changeColor(COLOR_SELECTED);
            }
        }

        if(state != State.IDLE) {
            command = new TranslateCommand(projectContext.currScene.currentSelection);
            command.setBefore(projectContext.currScene.currentSelection.position);
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
            xzPlaneHandle.changeColor(COLOR_XZ);

            command.setAfter(projectContext.currScene.currentSelection.position);
            history.add(command);
            command = null;
            state = State.IDLE;
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

        public static final int X_HANDLE_ID = 0;
        public static final int Y_HANDLE_ID = 1;
        public static final int Z_HANDLE_ID = 2;
        public static final int XZ_HANDLE_ID = 3;


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
