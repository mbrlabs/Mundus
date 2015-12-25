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

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.model.MModel;
import com.mbrlabs.mundus.model.MModelInstance;

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

    public ModelPlacementTool(ProjectContext projectContext, PerspectiveCamera cam, Shader shader, ModelBatch batch) {
        super(projectContext, cam, shader, batch);
        model = null;
        curEntity = null;
    }

    public void setModel(MModel model) {
        this.model = model;
        curEntity = new MModelInstance(model);
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
    public void render() {
        if(curEntity != null) {
            batch.begin(cam);
            batch.render(curEntity, projectContext.currScene.environment, shader);
            batch.end();
        }
    }

    @Override
    public void act() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        System.out.println("TOUCH DOWN");
        if(curEntity != null) {
            projectContext.currScene.entities.add(curEntity);
            curEntity = new MModelInstance(model);
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if(projectContext.currScene.terrainGroup.size() > 0 && curEntity != null) {
            Ray ray = cam.getPickRay(screenX, screenY);
            projectContext.currScene.terrainGroup.getRayIntersection(tempV3, ray);
            curEntity.transform.setTranslation(tempV3);
        }
        return false;
    }

    @Override
    public void dispose() {

    }

}
