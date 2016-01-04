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

package com.mbrlabs.mundus.runtime.libgdx;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.env.Env;
import com.mbrlabs.mundus.commons.env.SunLight;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.commons.terrain.TerrainInstance;

/**
 * @author Marcus Brummer
 * @version 27-12-2015
 */
public class Scene {

    public String name;

    public Array<Model> models;
    public Array<ModelInstance> modelInstances;
    public Array<Terrain> terrains;
    public Array<TerrainInstance> terrainInstances;

    public Env environment;

    public Scene() {
        models = new Array<>();
        modelInstances = new Array<>();
        terrains = new Array<>();
        terrainInstances = new Array<>();
        environment = new Env();

        SunLight sunLight = new SunLight();
        sunLight.setColor(1, 1, 1, 1);
        sunLight.position.set(600, 400, 600);
        environment.add(sunLight);
    }

}
