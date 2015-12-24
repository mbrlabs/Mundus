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

package com.mbrlabs.mundus.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.mbrlabs.mundus.utils.TextureUtils;

/**
 * @author Marcus Brummer
 * @version 01-12-2015
 */
public class TerrainTest {

    public Terrain terrain;

    public TerrainTest() {
        terrain = new Terrain(180);
        Pixmap heightMap = new Pixmap(Gdx.files.internal("heightmaps/heightmap180.png"));
        terrain.loadHeightMap(heightMap, 190);
        terrain.update();
        Texture tex = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/stone_hr.jpg"));
        terrain.setTexture(tex);
        heightMap.dispose();
    }

}
