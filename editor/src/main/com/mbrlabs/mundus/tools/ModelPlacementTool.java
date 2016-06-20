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
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.mbrlabs.mundus.commons.model.MModel;
import com.mbrlabs.mundus.commons.model.MModelInstance;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.InvalidComponentException;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.SceneGraphChangedEvent;
import com.mbrlabs.mundus.history.CommandHistory;
import com.mbrlabs.mundus.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.modules.inspector.terrain.TerrainUpDownTab;
import com.mbrlabs.mundus.utils.TerrainUtils;

/**
 * @author Marcus Brummer
 * @version 25-12-2015
 */
public class ModelPlacementTool extends Tool {

    public static final String NAME = "Placement Tool";

    private Vector3 tempV3 = new Vector3();

    // DO NOT DISPOSE THIS
    private MModel model;
    private MModelInstance curEntity;

    public ModelPlacementTool(ProjectManager projectManager, Shader shader, ModelBatch batch, CommandHistory history) {
        super(projectManager, shader, batch, history);
        this.model = null;
        this.curEntity = null;
    }

    public void setModel(MModel model) {
        this.model = model;
        this.curEntity = new MModelInstance(model);
        ProjectContext context = projectManager.current();
        System.out.println(context.terrains.size);
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

    @Override
    public void reset() {
        dispose();
    }

    @Override
    public void render() {
        if(curEntity != null) {
            batch.begin(projectManager.current().currScene.cam);
            batch.render(curEntity.modelInstance, projectManager.current().currScene.environment, shader);
            batch.end();
        }
    }

    @Override
    public void act() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if(curEntity != null && button == Input.Buttons.LEFT) {
            int id = projectManager.current().obtainID();
            GameObject modelGo = new GameObject(projectManager.current().currScene.sceneGraph, model.name, id);
            projectManager.current().currScene.sceneGraph.getGameObjects().add(modelGo);

            curEntity.modelInstance.transform.getTranslation(tempV3);
            modelGo.translate(tempV3);
            ModelComponent modelComponent = new ModelComponent(modelGo);
            modelComponent.setShader(shader);
            modelComponent.setModelInstance(curEntity);
            modelComponent.encodeRaypickColorId();

            try {
                modelGo.addComponent(modelComponent);
            } catch (InvalidComponentException e) {
                Dialogs.showErrorDialog(Ui.getInstance(), e.getMessage());
                return false;
            }

            Mundus.postEvent(new SceneGraphChangedEvent());

            curEntity = new MModelInstance(model);
            mouseMoved(screenX, screenY);
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if(this.model == null || curEntity == null) return false;

        final ProjectContext context = projectManager.current();
        final Ray ray = projectManager.current().currScene.viewport.getPickRay(screenX, screenY);
        if(context.terrains.size > 0 && curEntity != null) {
            TerrainUtils.getRayIntersection(context.terrains, ray, tempV3);
        } else {
            tempV3.set(projectManager.current().currScene.cam.position);
            tempV3.add(ray.direction.nor().scl(200));
        }
        curEntity.modelInstance.transform.setTranslation(tempV3);
        return false;
    }

    @Override
    public void dispose() {
        this.model = null;
        this.curEntity = null;
    }

}
