/*
 * Copyright (c) 2016. See AUTHORS file.
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

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.shaders.EntityShader;
import com.mbrlabs.mundus.commons.shaders.TerrainShader;
import com.mbrlabs.mundus.commons.terrain.TerrainInstance;

/**
 * @author Marcus Brummer
 * @version 04-01-2016
 */
public class MundusRenderer {

    private TerrainShader terrainShader;
    private EntityShader entityShader;

    private RenderContext renderContext;

    public MundusRenderer(RenderContext renderContext) {
        terrainShader = new TerrainShader();
        terrainShader.init();
        entityShader = new EntityShader();
        entityShader.init();

        this.renderContext = renderContext;
    }

    public void render(PerspectiveCamera camera, ModelBatch modelBatch, Scene scene) {
        modelBatch.begin(camera);
        modelBatch.render(scene.modelInstances, scene.environment, entityShader);
        modelBatch.end();

        // render terrains
        terrainShader.begin(camera, renderContext);
        for(TerrainInstance terrain : scene.terrainGroup.getTerrains()) {
            terrain.terrain.renderable.environment = scene.environment;
            terrain.terrain.renderable.worldTransform.set(terrain.transform);
            terrainShader.render(terrain.terrain.renderable);
        }
        terrainShader.end();
    }

}
