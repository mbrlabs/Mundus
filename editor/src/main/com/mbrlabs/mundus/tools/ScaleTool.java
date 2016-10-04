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

package com.mbrlabs.mundus.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
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
import com.mbrlabs.mundus.utils.Fa;
import com.mbrlabs.mundus.utils.Log;
import org.lwjgl.opengl.GL11;

/**
 * @author codenigma
 * @version 03-10-2016
 */
public class ScaleTool extends TransformTool {

    public static final String NAME = "Scale Tool";

    private final ScaleHandle xHandle;
    private final ScaleHandle yHandle;
    private final ScaleHandle zHandle;
    private final ScaleHandle xyzHandle;
    private final ScaleHandle[] handles;

    private final Matrix4 shapeRenderMat = new Matrix4();

    private final Vector3 temp0 = new Vector3();
    private final Vector3 temp1 = new Vector3();
    private final Vector3 tempScale = new Vector3();
    private float temp100ScaleDst = 0;


    private ShapeRenderer shapeRenderer;
    private ProjectContext projectContext;

    private TransformState state = TransformState.IDLE;
    

       public ScaleTool(ProjectManager projectManager, GameObjectPicker goPicker, ToolHandlePicker handlePicker, Shader shader, 
               ShapeRenderer shapeRenderer, ModelBatch batch, CommandHistory history) {
        super(projectManager, goPicker, handlePicker, shader, batch, history);
    
        this.shapeRenderer = shapeRenderer;
        this.projectContext = projectManager.current();
        
        ModelBuilder modelBuilder = new ModelBuilder();
        
        Model xPlaneHandleModel = modelBuilder.createBox(1, 1, 1,
                new Material(ColorAttribute.createDiffuse(COLOR_X)),
                VertexAttributes.Usage.Position);
        Model yPlaneHandleModel = modelBuilder.createBox(1, 1, 1,
                new Material(ColorAttribute.createDiffuse(COLOR_Y)),
                VertexAttributes.Usage.Position);
        Model zPlaneHandleModel = modelBuilder.createBox(1, 1, 1,
                new Material(ColorAttribute.createDiffuse(COLOR_Z)),
                VertexAttributes.Usage.Position);      
        Model xyzPlaneHandleModel = modelBuilder.createBox(1, 1, 1,
                new Material(ColorAttribute.createDiffuse(COLOR_XYZ)),
                VertexAttributes.Usage.Position);
        
        xHandle = new ScaleHandle(X_HANDLE_ID, xPlaneHandleModel);
        yHandle = new ScaleHandle(Y_HANDLE_ID, yPlaneHandleModel);
        zHandle = new ScaleHandle(Z_HANDLE_ID, zPlaneHandleModel);
        xyzHandle = new ScaleHandle(XYZ_HANDLE_ID, xyzPlaneHandleModel);
        
        handles = new ScaleHandle[]{xHandle, yHandle, zHandle, xyzHandle};
    }

    @Override
    public void render() {
        super.render();

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        if(state == TransformState.IDLE && projectContext.currScene.currentSelection != null) {
            batch.begin(projectContext.currScene.cam);
            xHandle.render(batch);
            yHandle.render(batch);
            zHandle.render(batch);
            xyzHandle.render(batch);
            batch.end();
        } else if(projectContext.currScene.currentSelection != null) {
            Viewport vp = projectContext.currScene.viewport;

            GameObject go = projectContext.currScene.currentSelection;
            go.getTransform().getTranslation(temp0);
            Vector3 pivot = projectContext.currScene.cam.project(temp0);

            shapeRenderMat.setToOrtho2D(vp.getScreenX(), vp.getScreenY(), vp.getScreenWidth(), vp.getScreenHeight());
            switch(state) {
                case TRANSFORM_X : 
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    shapeRenderer.setColor(Color.BLACK);
                    shapeRenderer.setProjectionMatrix(shapeRenderMat);
                    shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 2);
                    shapeRenderer.setColor(COLOR_X);
                    shapeRenderer.box(pivot.x, pivot.y, 0, 10, 10, 10);
                    shapeRenderer.end();
                    break;
                case TRANSFORM_Y : 
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    shapeRenderer.setColor(Color.BLACK);
                    shapeRenderer.setProjectionMatrix(shapeRenderMat);
                    shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 2);
                    shapeRenderer.setColor(COLOR_Y);
                    shapeRenderer.box(pivot.x, pivot.y, 0, 10, 10, 10);
                    shapeRenderer.end();
                    break;
                case TRANSFORM_Z : 
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    shapeRenderer.setColor(Color.BLACK);
                    shapeRenderer.setProjectionMatrix(shapeRenderMat);
                    shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 2);
                    shapeRenderer.setColor(COLOR_Z);
                    shapeRenderer.box(pivot.x, pivot.y, 0, 10, 10, 10);
                    shapeRenderer.end();
                    break;
                case TRANSFORM_XYZ : 
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    shapeRenderer.setColor(Color.BLACK);
                    shapeRenderer.setProjectionMatrix(shapeRenderMat);
                    shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 2);
                    shapeRenderer.setColor(COLOR_XYZ);
                    shapeRenderer.box(pivot.x, pivot.y, 0, 10, 10, 10);
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

        if(projectContext.currScene.currentSelection != null) {
            translateHandles();
            if(state == TransformState.IDLE) return;
            float dst, scale;

            dst = getCurrentDst();
            // calculate relation to 100% scale dst
            scale = (100 / temp100ScaleDst * dst) / 100;
            boolean modified = false;
            if(null != state) { switch (state) { 
                case TRANSFORM_X:
                    tempScale.x = scale;
                    projectContext.currScene.currentSelection.setLocalScale(tempScale.x, tempScale.y, tempScale.z);
                    modified = true;
                    break;
                case TRANSFORM_Y:
                    tempScale.y = scale;
                    projectContext.currScene.currentSelection.setLocalScale(tempScale.x, tempScale.y, tempScale.z);
                    modified = true;
                    break;
                case TRANSFORM_Z:
                    tempScale.z = scale;
                    projectContext.currScene.currentSelection.setLocalScale(tempScale.x, tempScale.y, tempScale.z);
                    modified = true;
                    break;
                case TRANSFORM_XYZ:
                        tempScale.x = scale; 
                        tempScale.y = scale;
                        tempScale.z = scale;
                    projectContext.currScene.currentSelection.setLocalScale(tempScale.x, tempScale.y, tempScale.z);
                    modified = true;
                    break;
                default:
                    break;
            }
            }
            if(modified) {
                gameObjectModifiedEvent.setGameObject(projectContext.currScene.currentSelection);
                Mundus.postEvent(gameObjectModifiedEvent);
            }
        }
    }

    private float getCurrentDst() {
        if(projectContext.currScene.currentSelection != null) {
            projectContext.currScene.currentSelection.getPosition(temp0);
            Vector3 pivot = projectContext.currScene.cam.project(temp0);
            Vector3 mouse = temp1.set(Gdx.input.getX(), Gdx.graphics.getHeight() -  Gdx.input.getY(), 0);

            return MathUtils.dst(pivot.x, pivot.y, mouse.x, mouse.y);
        }

        return 0;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if(button == Input.Buttons.LEFT && projectContext.currScene.currentSelection != null) {
            
            ScaleHandle handle = (ScaleHandle) handlePicker.pick(handles, projectContext.currScene, screenX, screenY);
            if(handle == null) {
                state = TransformState.IDLE;
                return false;
            }
            // current scale 
            projectContext.currScene.currentSelection.getScale(tempScale);
            // setting current scale as 100% value
            switch (handle.getId()) {
                case X_HANDLE_ID:
                    state = TransformState.TRANSFORM_X;
                    temp100ScaleDst = getCurrentDst() / tempScale.x;
                    break;
                case Y_HANDLE_ID:
                    state = TransformState.TRANSFORM_Y;
                    temp100ScaleDst = getCurrentDst() / tempScale.y;
                    break;
                case Z_HANDLE_ID:
                    state = TransformState.TRANSFORM_Z;
                    temp100ScaleDst = getCurrentDst() / tempScale.y;
                    break;
                case XYZ_HANDLE_ID:
                    state = TransformState.TRANSFORM_XYZ;
                    float avg = (tempScale.x + tempScale.y + tempScale.z) / 3;
                    temp100ScaleDst = getCurrentDst() / avg;
                    break;
                default:
                    break;
            }
        }
        
        // scale command here

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        state = TransformState.IDLE;
        
        // scale command here
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
        //not needed
    }

    @Override
    protected void translateHandles() {
        final Vector3 pos = projectContext.currScene.currentSelection.getTransform().getTranslation(temp0);
        xHandle.position.set(pos);
        xHandle.position.x += 10;
        xHandle.applyTransform();
        yHandle.position.set(pos);
        yHandle.position.y += 10;
        yHandle.applyTransform();
        zHandle.position.set(pos);
        zHandle.position.z += 10;
        zHandle.applyTransform();
        xyzHandle.position.set(pos);
        xyzHandle.applyTransform();
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

        xyzHandle.scale.set(scaleFactor*0.13f,scaleFactor*0.13f, scaleFactor*0.13f);
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
        return Fa.EXPAND;
    }

    @Override
    public void dispose() {
        super.dispose();
        xHandle.dispose();
        yHandle.dispose();
        zHandle.dispose();
        xyzHandle.dispose();
    }

    /**
     *
     */
    private class ScaleHandle extends ToolHandle {
        private Model model;
        private ModelInstance modelInstance;

        public ScaleHandle(int id, Model model) {
            super(id);
                    
            modelInstance = new ModelInstance(model);
            modelInstance.materials.first().set(idAttribute);
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
            modelInstance.transform.set(position, rotation, scale);
        }

        @Override
        public void dispose() {
            model.dispose();
        }
    }

}
