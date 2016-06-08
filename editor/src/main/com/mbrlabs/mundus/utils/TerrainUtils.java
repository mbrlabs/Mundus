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

package com.mbrlabs.mundus.utils;

import com.badlogic.gdx.Gdx;
import com.mbrlabs.mundus.commons.model.MTexture;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.terrain.SplatTexture;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.commons.terrain.TerrainShader;
import com.mbrlabs.mundus.commons.terrain.TerrainTexture;
import com.mbrlabs.mundus.commons.utils.TextureUtils;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.terrain.TerrainIO;

/**
 * @author Marcus Brummer
 * @version 27-02-2016
 */
public class TerrainUtils {

    public static GameObject createTerrainGO(SceneGraph sg, TerrainShader shader, int goID, String goName, Terrain terrain) {
        GameObject terrainGO = new GameObject(sg);
        terrainGO.setId(goID);
        terrainGO.setName(goName);
        terrainGO.setTransform(terrain.transform);
        terrainGO.setParent(null);

        terrain.setTransform(terrainGO.getTransform());

        TerrainComponent terrainComponent = new TerrainComponent(terrainGO);
        terrainComponent.setTerrain(terrain);
        terrainGO.getComponents().add(terrainComponent);
        terrainComponent.setShader(shader);


        return terrainGO;
    }

    public static Terrain createTerrain(int terrainID, String terrainName, int width, int depth, int vertexRes) {
        Terrain terrain = new Terrain(vertexRes);
        terrain.terrainWidth = width;
        terrain.terrainDepth = depth;
        terrain.id = terrainID;
        terrain.name = terrainName;
        terrain.init();
        terrain.update();

        TerrainTexture terrainTex = terrain.getTerrainTexture();
        MTexture base = new MTexture();
        base.setId(-1);
        base.texture = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/terrain/chess.png"), true);
        terrainTex.setSplatTexture(new SplatTexture(SplatTexture.Channel.BASE, base));
        terrain.terraPath = ProjectManager.PROJECT_TERRAIN_DIR + terrain.id + "." + TerrainIO.FILE_EXTENSION;

        return terrain;
    }

}
