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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
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
import com.mbrlabs.mundus.utils.Colors;
import com.mbrlabs.mundus.utils.Fa;
import org.lwjgl.opengl.GL11;

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

    private static final boolean DEBUG = false;

    private final float ARROW_THIKNESS = 0.4f;
    private final float ARROW_CAP_SIZE = 0.15f;
    private final int ARROW_DIVISIONS = 12;

    public static final String NAME = "Translate Tool";
    private Drawable icon;

    private State state = State.IDLE;
    private boolean initTranslate = true;

    private Model xHandleModel;
    private Model yHandleModel;
    private Model zHandleModel;
    private Model xzPlaneHandleModel;

    private Handle xHandle;
    private Handle yHandle;
    private Handle zHandle;
    private Handle xzPlaneHandle;

    private Vector3 lastPos = new Vector3();
    private boolean globalSpace = true;

    private Vector3 temp0 = new Vector3();
    private Vector3 temp1 = new Vector3();
    private Vector3 temp2 = new Vector3();
    private Vector3 temp3 = new Vector3();
    private Quaternion tempQuat = new Quaternion();

    private GameObjectModifiedEvent gameObjectModifiedEvent;
    private TranslateCommand command;


    public TranslateTool(ProjectContext projectContext, Shader shader, ModelBatch batch, CommandHistory history) {
        super(projectContext, shader, batch, history);
        icon = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/translateTool.png"))));

        ModelBuilder modelBuilder = new ModelBuilder();

        xHandleModel =  modelBuilder.createArrow(0, 0, 0, 1, 0, 0, ARROW_CAP_SIZE, ARROW_THIKNESS,
                ARROW_DIVISIONS, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(COLOR_X)),
                VertexAttributes.Usage.Position);
        yHandleModel =  modelBuilder.createArrow(0, 0, 0, 0, 1, 0, ARROW_CAP_SIZE, ARROW_THIKNESS,
                ARROW_DIVISIONS, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(COLOR_Y)),
                VertexAttributes.Usage.Position);
        zHandleModel =  modelBuilder.createArrow(0, 0, 0, 0, 0, 1, ARROW_CAP_SIZE, ARROW_THIKNESS,
                ARROW_DIVISIONS, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(COLOR_Z)),
                VertexAttributes.Usage.Position);
        xzPlaneHandleModel = modelBuilder.createSphere(1, 1, 1, 20, 20,
                new Material(ColorAttribute.createDiffuse(COLOR_XZ)),
                VertexAttributes.Usage.Position);

        xHandle = new Handle(xHandleModel);
        yHandle = new Handle(yHandleModel);
        zHandle = new Handle(zHandleModel);
        xzPlaneHandle = new Handle(xzPlaneHandleModel);

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
        xHandle.resetRotation();
        yHandle.resetRotation();
        zHandle.resetRotation();
        if(!global) {
            xHandle.transform.rotate(projectContext.currScene.currentSelection.rotation);
            yHandle.transform.rotate(projectContext.currScene.currentSelection.rotation);
            zHandle.transform.rotate(projectContext.currScene.currentSelection.rotation);
        }

//        xHandle.calculateBounds();
//        yHandle.calculateBounds();
//        zHandle.calculateBounds();
    }

    @Override
    public void render() {
        super.render();
        if(projectContext.currScene.currentSelection != null) {
            batch.begin(projectContext.currScene.cam);
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            batch.render(xHandle);
            batch.render(yHandle);
            batch.render(zHandle);
            batch.render(xzPlaneHandle);

            if(DEBUG) {
                batch.render(xHandle.boundingBoxModelInst, shader);
                batch.render(yHandle.boundingBoxModelInst, shader);
                batch.render(zHandle.boundingBoxModelInst, shader);
            }

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
                projectContext.currScene.currentSelection.translate(rayEnd.x - lastPos.x,
                        0, rayEnd.z - lastPos.z, globalSpace);
                modified = true;
            } else if(state == State.TRANSLATE_X) {
                projectContext.currScene.currentSelection.translate(rayEnd.x - lastPos.x,
                        0, 0, globalSpace);
                modified = true;
            } else if(state == State.TRANSLATE_Y) {
                projectContext.currScene.currentSelection.translate(0,
                        rayEnd.y - lastPos.y, 0, globalSpace);
                modified = true;
            } else if(state == State.TRANSLATE_Z) {
                projectContext.currScene.currentSelection.translate(0, 0,
                        rayEnd.z - lastPos.z, globalSpace);
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
        xHandle.setToScaling(scaleFactor * 0.7f, scaleFactor / 2, scaleFactor / 2);
        yHandle.setToScaling(scaleFactor / 2, scaleFactor * 0.7f, scaleFactor / 2);
        zHandle.setToScaling(scaleFactor / 2, scaleFactor / 2, scaleFactor * 0.7f);
        xzPlaneHandle.setToScaling(scaleFactor*0.13f,scaleFactor*0.13f, scaleFactor*0.13f);
    }

    private void positionHandles() {
        final Vector3 medium = projectContext.currScene.currentSelection.calculateMedium(temp0);
        xHandle.setTranslation(medium);
        yHandle.setTranslation(medium);
        zHandle.setTranslation(medium);
        xzPlaneHandle.setTranslation(medium);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if(button == Input.Buttons.LEFT && projectContext.currScene.currentSelection != null) {
            Ray ray = projectContext.currScene.viewport.getPickRay(screenX, screenY);
            if(xzPlaneHandle.isSelected(ray)) {
                state = State.TRANSLATE_XZ;
                initTranslate = true;
                xzPlaneHandle.changeColor(COLOR_SELECTED);
            } else if(xHandle.isSelected(ray)) {
                state = State.TRANSLATE_X;
                initTranslate = true;
                xHandle.changeColor(COLOR_SELECTED);
            } else if(yHandle.isSelected(ray)) {
                state = State.TRANSLATE_Y;
                initTranslate = true;
                yHandle.changeColor(COLOR_SELECTED);
            } else if(zHandle.isSelected(ray)) {
                state = State.TRANSLATE_Z;
                initTranslate = true;
                zHandle.changeColor(COLOR_SELECTED);
            } else {
                state = State.IDLE;
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
        xHandleModel.dispose();
        yHandleModel.dispose();
        zHandleModel.dispose();
        xzPlaneHandleModel.dispose();

        xHandle.dispose();
        yHandle.dispose();
        zHandle.dispose();
        xzPlaneHandle.dispose();
    }

    /**
     *
     * @author Marcus Brummer
     * @version 02-1-2016
     */
    private class Handle extends ModelInstance implements Disposable {

        public BoundingBox boundingBox;
        public Vector3 center;
        public Vector3 dimensions;
        public float radius;

        private Vector3 tv0;
        private Vector3 tv1;

        private Model boundingBoxModel;
        public ModelInstance boundingBoxModelInst;

        public Handle(Model model) {
            super(model);
            tv0 = new Vector3();
            tv1 = new Vector3();
            center = new Vector3();
            dimensions = new Vector3();
            boundingBox = new BoundingBox();

            calculateBounds();

            if(DEBUG) {
                boundingBoxModel = new ModelBuilder().createBox(boundingBox.getWidth(), boundingBox.getHeight(),
                        boundingBox.getDepth(), new Material(), VertexAttributes.Usage.Position);
                boundingBoxModelInst = new ModelInstance(boundingBoxModel);
            }

            transform.getTranslation(tv0);
            setTranslation(tv0);
        }

        public void calculateBounds() {
            calculateBoundingBox(boundingBox);
            boundingBox.getCenter(center);
            boundingBox.getDimensions(dimensions);
            radius = dimensions.len() / 2f;
        }

        public void setTranslation(Vector3 t) {
            tv0.set(t);
            transform.setTranslation(tv0);

            if(DEBUG) {
                boundingBoxModelInst.transform.setTranslation(tv0);
                boundingBoxModelInst.transform.translate(center);
            }
        }

        public void setToScaling(float x, float y, float z) {
            transform.setToScaling(x, y, z);
            if(DEBUG) {
                boundingBoxModelInst.transform.setToScaling(x, y, z);
            }
        }

        public void resetRotation() {
            transform.getRotation(tempQuat);
            transform.getTranslation(tv0);
            transform.getScale(tv1);

            transform.setToRotation(0,0,0,0);
            transform.translate(tv0);
            transform.scl(tv1);
        }

        public void changeColor(Color color) {
            ColorAttribute diffuse = (ColorAttribute) materials.get(0).get(ColorAttribute.Diffuse);
            diffuse.color.set(color);
        }

        public boolean isSelected(Ray ray) {
            // scale
            transform.getScale(temp0);

            // dim
            temp1.set(dimensions);
            temp1.scl(temp0);

            // center
            transform.getTranslation(temp2);
            temp2.add(temp3.set(center).scl(temp0));

            return Intersector.intersectRayBoundsFast(ray, temp2, temp1);
        }

        @Override
        public void dispose() {
            if(DEBUG) {
                boundingBoxModel.dispose();
            }
        }
    }

}
