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
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.model.MModelInstance;
import com.mbrlabs.mundus.commons.terrain.TerrainGroup;

/**
 * @author Marcus Brummer
 * @version 22-12-2015
 */
public class Scene {

    private String name;
    private long id;

    public Array<MModelInstance> entities;
    public TerrainGroup terrainGroup;
    public Environment environment;

    public PerspectiveCamera cam;

    public Scene() {
        entities = new Array<>();
        terrainGroup = new TerrainGroup();
        environment = new Environment();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 1, -3);
        cam.lookAt(0,1,-1);
        cam.near = 0.2f;
        cam.far = 10000;

        PointLight pointLight = new PointLight();
        pointLight.setIntensity(1);
        pointLight.setPosition(0, 400, 0);
        pointLight.setColor(1,1,1,1);
        environment.add(pointLight);
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

}
