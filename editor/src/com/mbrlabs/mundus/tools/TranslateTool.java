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

    private static final boolean DEBUG = false;

    private final float ARROW_THIKNESS = 0.2f;
    private final float ARROW_CAP_SIZE = 0.1f;
    private final int ARROW_DIVISIONS = 10;

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

    private Vector3 temp0 = new Vector3();
    private Vector3 temp1 = new Vector3();
    private Vector3 temp2 = new Vector3();
    private Vector3 temp3 = new Vector3();

    private GameObjectModifiedEvent gameObjectModifiedEvent;

    private TranslateCommand command;


    public TranslateTool(ProjectContext projectContext, Shader shader, ModelBatch batch, CommandHistory history) {
        super(projectContext, shader, batch, history);
        icon = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/translateTool.png"))));

        ModelBuilder modelBuilder = new ModelBuilder();

        xHandleModel =  modelBuilder.createArrow(0, 0, 0, 1, 0, 0, ARROW_CAP_SIZE, ARROW_THIKNESS,
                ARROW_DIVISIONS, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                VertexAttributes.Usage.Position);
        yHandleModel =  modelBuilder.createArrow(0, 0, 0, 0, 1, 0, ARROW_CAP_SIZE, ARROW_THIKNESS,
                ARROW_DIVISIONS, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position);
        zHandleModel =  modelBuilder.createArrow(0, 0, 0, 0, 0, 1, ARROW_CAP_SIZE, ARROW_THIKNESS,
                ARROW_DIVISIONS, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                VertexAttributes.Usage.Position);
        xzPlaneHandleModel = modelBuilder.createSphere(1, 1, 1, 20, 20,
                new Material(ColorAttribute.createDiffuse(Color.CYAN)),
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
            Vector3 selectionPos = projectContext.currScene.currentSelection.transform.getTranslation(temp0);

            //selectionPos.add(selectedGameObject.center);
            xHandle.setTranslation(selectionPos);
            yHandle.setTranslation(selectionPos);
            zHandle.setTranslation(selectionPos);
            xzPlaneHandle.setTranslation(selectionPos);

            if(state == State.IDLE) return;

            Ray ray = projectContext.currScene.cam.getPickRay(Gdx.input.getX(), Gdx.input.getY());
            Vector3 rayEnd = projectContext.currScene.currentSelection.transform.getTranslation(temp1);
            float dst = projectContext.currScene.cam.position.dst(rayEnd);
            rayEnd = ray.getEndPoint(rayEnd, dst);

            if(initTranslate) {
                initTranslate = false;
                lastPos.set(rayEnd);
            }

            boolean modified = false;
            if(state == State.TRANSLATE_XZ) {
                projectContext.currScene.currentSelection.translate(rayEnd.x - lastPos.x,
                        0, rayEnd.z - lastPos.z);
                modified = true;
            } else if(state == State.TRANSLATE_X) {
                projectContext.currScene.currentSelection.translate(rayEnd.x - lastPos.x,
                        0, 0);
                modified = true;
            } else if(state == State.TRANSLATE_Y) {
                projectContext.currScene.currentSelection.translate(0,
                        rayEnd.y - lastPos.y, 0);
                modified = true;
            } else if(state == State.TRANSLATE_Z) {
                projectContext.currScene.currentSelection.translate(0, 0,
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
        projectContext.currScene.currentSelection.transform.getTranslation(temp0);
        float scaleFactor = projectContext.currScene.cam.position.dst(temp0) * 0.25f;
        xHandle.setToScaling(scaleFactor * 0.7f, scaleFactor / 2, scaleFactor / 2);
        yHandle.setToScaling(scaleFactor / 2, scaleFactor * 0.7f, scaleFactor / 2);
        zHandle.setToScaling(scaleFactor / 2, scaleFactor / 2, scaleFactor * 0.7f);
        xzPlaneHandle.setToScaling(scaleFactor*0.1f,scaleFactor*0.1f, scaleFactor*0.1f);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if(button == Input.Buttons.LEFT && projectContext.currScene.currentSelection != null) {
            Ray ray = projectContext.currScene.cam.getPickRay(screenX, screenY);
            if(xzPlaneHandle.isSelected(ray)) {
                state = State.TRANSLATE_XZ;
                initTranslate = true;
            } else if(xHandle.isSelected(ray)) {
                state = State.TRANSLATE_X;
                initTranslate = true;
            } else if(yHandle.isSelected(ray)) {
                state = State.TRANSLATE_Y;
                initTranslate = true;
            } else if(zHandle.isSelected(ray)) {
                state = State.TRANSLATE_Z;
                initTranslate = true;
            } else {
                state = State.IDLE;
            }
        }

        if(state != State.IDLE) {
            command = new TranslateCommand(projectContext.currScene.currentSelection);
            projectContext.currScene.currentSelection.transform.getTranslation(temp0);
            command.setBefore(temp0);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        if(state != State.IDLE) {
            projectContext.currScene.currentSelection.transform.getTranslation(temp0);
            command.setAfter(temp0);
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

        private Vector3 tvec3;

        private Model boundingBoxModel;
        public ModelInstance boundingBoxModelInst;

        public Handle(Model model) {
            super(model);
            tvec3 = new Vector3();
            center = new Vector3();
            dimensions = new Vector3();
            boundingBox = new BoundingBox();

            calculateBounds();

            if(DEBUG) {
                boundingBoxModel = new ModelBuilder().createBox(boundingBox.getWidth(), boundingBox.getHeight(),
                        boundingBox.getDepth(), new Material(), VertexAttributes.Usage.Position);
                boundingBoxModelInst = new ModelInstance(boundingBoxModel);
            }

            transform.getTranslation(tvec3);
            setTranslation(tvec3);
        }

        public void calculateBounds() {
            calculateBoundingBox(boundingBox);
            boundingBox.getCenter(center);
            boundingBox.getDimensions(dimensions);
            radius = dimensions.len() / 2f;
        }

        public void setTranslation(Vector3 t) {
            tvec3.set(t);
            transform.setTranslation(tvec3);

            if(DEBUG) {
                boundingBoxModelInst.transform.setTranslation(tvec3);
                boundingBoxModelInst.transform.translate(center);
            }
        }

        public void setToScaling(float x, float y, float z) {
            transform.setToScaling(x, y, z);
            if(DEBUG) {
                boundingBoxModelInst.transform.setToScaling(x, y, z);
            }
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
