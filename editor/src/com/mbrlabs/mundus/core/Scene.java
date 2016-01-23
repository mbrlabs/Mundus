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

package com.mbrlabs.mundus.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.env.Env;
import com.mbrlabs.mundus.commons.env.SunLight;
import com.mbrlabs.mundus.scene3d.GameObject;
import com.mbrlabs.mundus.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.skybox.Skybox;
import com.mbrlabs.mundus.commons.terrain.TerrainGroup;
import com.mbrlabs.mundus.utils.SkyboxBuilder;

/**
 * @author Marcus Brummer
 * @version 22-12-2015
 */
public class Scene implements Disposable {

    private String name;
    private long id;

    public SceneGraph sceneGraph;
    public Env environment;
    public Skybox skybox;

    public GameObject currentSelection;

    /**
     * The terrain group is just used internally to interact with the terrains efficently.
     * It holds references of the terrain instances in the scene graph.
     */
    public TerrainGroup terrainGroup;

    public PerspectiveCamera cam;

    public Scene() {
        terrainGroup = new TerrainGroup();
        environment = new Env();
        currentSelection = null;

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 1, -3);
        cam.lookAt(0,1,-1);
        cam.near = 0.2f;
        cam.far = 10000;

        SunLight sunLight = new SunLight();
        sunLight.setColor(1, 1, 1, 1);
        sunLight.intensity = 1f;
        sunLight.position.set(600, 400, 600);
        environment.add(sunLight);

        skybox = SkyboxBuilder.createDefaultSkybox();

        sceneGraph = new SceneGraph(this);
        sceneGraph.setBatch(Mundus.modelBatch);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void dispose() {
        if(skybox != null) {
            skybox.dispose();
        }
    }
}
