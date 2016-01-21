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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.model.MModelInstance;
import com.mbrlabs.mundus.scene3d.components.Component;
import com.mbrlabs.mundus.scene3d.GameObject;
import com.mbrlabs.mundus.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.utils.Fa;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
public class SelectionTool extends Tool {

    public static final String NAME = "Selection Tool";
    private Drawable icon;

    protected MModelInstance selectedEntity;
    private Model boxOutlineModel;
    private ModelInstance outlineInstance;

    private Vector3 tempV3 = new Vector3();

    public SelectionTool(ProjectContext projectContext, Shader shader, ModelBatch batch) {
        super(projectContext, shader, batch);
        icon = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/selectionTool.png"))));

        ModelBuilder modelBuilder = new ModelBuilder();
        boxOutlineModel = modelBuilder.createBox(1, 1, 1, new Material(), VertexAttributes.Usage.Position);
        outlineInstance = new ModelInstance(boxOutlineModel);
    }

    private MModelInstance getEntity(int screenX, int screenY) {
        Ray ray = projectContext.currScene.cam.getPickRay(screenX, screenY);
        MModelInstance modelInstance = null;
        float distance = -1;

        for (GameObject go : projectContext.currScene.sceneGraph) {
            Component component = go.findComponentByType(Component.Type.MODEL);
            if(component == null) {
                continue;
            }

            MModelInstance entity = ((ModelComponent) component).getModel();
            entity.modelInstance.transform.getTranslation(tempV3);
            tempV3.add(entity.center);
            float dist2 = ray.origin.dst2(tempV3);
            if (distance >= 0f && dist2 > distance) continue;

            entity.modelInstance.transform.getTranslation(tempV3);
            tempV3.add(entity.center);

            if(Intersector.intersectRayBoundsFast(ray, tempV3, entity.dimensions)) {
                modelInstance = entity;
                distance = dist2;
            }

        }
        return modelInstance;
    }

    public void modelSelected(MModelInstance selection) {
        selectedEntity = selection;
        outlineInstance.transform.set(selectedEntity.modelInstance.transform);
        outlineInstance.transform.translate(selectedEntity.center);
        outlineInstance.transform.scl(selectedEntity.dimensions);
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
        return Fa.MOUSE_POINTER;
    }

    @Override
    public void reset() {
        selectedEntity = null;
    }

    @Override
    public void render() {
        if(selectedEntity != null) {
            batch.begin(projectContext.currScene.cam);
           // batch.render(outlineInstance, shader);
            batch.render(selectedEntity.modelInstance, shader);
            batch.end();
        }
    }

    @Override
    public void act() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(button == Input.Buttons.RIGHT) {
            MModelInstance selection = getEntity(screenX, screenY);
            if(selection != null && !selection.equals(selectedEntity)) {
                modelSelected(selection);
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public void dispose() {
        boxOutlineModel.dispose();
    }

}
