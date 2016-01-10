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
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.env.Env;
import com.mbrlabs.mundus.commons.env.Fog;
import com.mbrlabs.mundus.commons.env.SunLight;
import com.mbrlabs.mundus.commons.env.SunLightsAttribute;
import com.mbrlabs.mundus.commons.skybox.Skybox;
import com.mbrlabs.mundus.model.MModelInstance;
import com.mbrlabs.mundus.commons.terrain.TerrainGroup;

/**
 * @author Marcus Brummer
 * @version 22-12-2015
 */
public class Scene implements Disposable {

    private String name;
    private long id;

    public Array<MModelInstance> entities;
    public TerrainGroup terrainGroup;
    public Env environment;
    public Skybox skybox;

    public PerspectiveCamera cam;

    public Scene() {
        entities = new Array<>();
        terrainGroup = new TerrainGroup();
        environment = new Env();
        //environment.setFog(new Fog());

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 1, -3);
        cam.lookAt(0,1,-1);
        cam.near = 0.2f;
        cam.far = 10000;

        SunLight sunLight = new SunLight();
        sunLight.setColor(1, 1, 1, 1);
        sunLight.position.set(600, 400, 600);
        environment.add(sunLight);

        skybox = new Skybox(
                Gdx.files.internal("textures/skybox/cloudy/right.png"),
                Gdx.files.internal("textures/skybox/cloudy/left.png"),
                Gdx.files.internal("textures/skybox/cloudy/top.jpg"),
                Gdx.files.internal("textures/skybox/cloudy/bottom.png"),
                Gdx.files.internal("textures/skybox/cloudy/back.png"),
                Gdx.files.internal("textures/skybox/cloudy/front.png"));
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
        skybox.dispose();
    }
}
