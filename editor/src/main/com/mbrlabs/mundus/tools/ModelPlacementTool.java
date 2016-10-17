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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.InvalidComponentException;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.SceneGraphChangedEvent;
import com.mbrlabs.mundus.history.CommandHistory;
import com.mbrlabs.mundus.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.utils.TerrainUtils;

/**
 * @author Marcus Brummer
 * @version 25-12-2015
 */
public class ModelPlacementTool extends Tool {

    public static final String NAME = "Placement Tool";
    public static Vector3 DEFAULT_ORIENTATION = Vector3.Z.cpy();

    private Vector3 tempV3 = new Vector3();

    private boolean shouldRespectTerrainSlope = false;

    // DO NOT DISPOSE THIS
    private ModelAsset model;
    private ModelInstance modelInstance;

    public ModelPlacementTool(ProjectManager projectManager, Shader shader, ModelBatch batch, CommandHistory history) {
        super(projectManager, shader, batch, history);
        this.model = null;
        this.modelInstance = null;
    }

    public void setModel(ModelAsset model) {
        this.model = model;
        modelInstance = null;
        this.modelInstance = new ModelInstance(model.getModel());
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
        throw new UnsupportedOperationException();
    }

    public boolean isShouldRespectTerrainSlope() {
        return shouldRespectTerrainSlope;
    }

    public void setShouldRespectTerrainSlope(boolean shouldRespectTerrainSlope) {
        this.shouldRespectTerrainSlope = shouldRespectTerrainSlope;
    }

    @Override
    public void reset() {
        dispose();
    }

    @Override
    public void render() {
        if (modelInstance != null) {
            batch.begin(projectManager.current().currScene.cam);
            batch.render(modelInstance, projectManager.current().currScene.environment, shader);
            batch.end();
        }
    }

    @Override
    public void act() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (modelInstance != null && button == Input.Buttons.LEFT) {
            int id = projectManager.current().obtainID();
            GameObject modelGo = new GameObject(projectManager.current().currScene.sceneGraph, model.getName(), id);
            projectManager.current().currScene.sceneGraph.addGameObject(modelGo);

            modelInstance.transform.getTranslation(tempV3);
            modelGo.translate(tempV3);

            ModelComponent modelComponent = new ModelComponent(modelGo);
            modelComponent.setShader(shader);
            modelComponent.setModel(model, true);
            modelComponent.encodeRaypickColorId();

            try {
                modelGo.addComponent(modelComponent);
            } catch (InvalidComponentException e) {
                Dialogs.showErrorDialog(Ui.getInstance(), e.getMessage());
                return false;
            }

            Mundus.postEvent(new SceneGraphChangedEvent());

            mouseMoved(screenX, screenY);
        }
        return false;
    }


    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (this.model == null || modelInstance == null) return false;

        final ProjectContext context = projectManager.current();

        final Ray ray = projectManager.current().currScene.viewport.getPickRay(screenX, screenY);
        if (context.currScene.terrains.size > 0 && modelInstance != null) {
            MeshPartBuilder.VertexInfo vi = TerrainUtils.getRayIntersectionAndUp(context.currScene.terrains, ray);
            if (vi != null) {
                if (shouldRespectTerrainSlope) {
                    modelInstance.transform.setToLookAt(DEFAULT_ORIENTATION, vi.normal);
                }
                modelInstance.transform.setTranslation(vi.position);
            }
        } else {
            tempV3.set(projectManager.current().currScene.cam.position);
            tempV3.add(ray.direction.nor().scl(200));
            modelInstance.transform.setTranslation(tempV3);
        }

        return false;
    }

    @Override
    public void dispose() {
        this.model = null;
        this.modelInstance = null;
    }

}
