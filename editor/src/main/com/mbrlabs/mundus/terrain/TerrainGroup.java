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

package com.mbrlabs.mundus.terrain;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.terrain.Terrain;

/**
 * @author Marcus Brummer
 * @version 13-12-2015
 */
public class TerrainGroup {

    private Array<Terrain> terrains;

    private Vector3 tv3 = new Vector3();

    public TerrainGroup() {
        this.terrains = new Array<>();
    }

    public void add(Terrain terrain) {
        this.terrains.add(terrain);
    }

    public Array<Terrain> getTerrains() {
        return this.terrains;
    }

    public int size() {
        return terrains.size;
    }

    public Terrain get(int index) {
        return terrains.get(index);
    }

    public Terrain first() {
        return get(0);
    }

    public Vector3 getRayIntersection(Vector3 out, Ray ray) {
        for(Terrain terrain : terrains) {
            terrain.getRayIntersection(out, ray);
            if(terrain.isOnTerrain(out.x, out.z)) {
                return out;
            }
        }
        return null;
    }

    public Terrain getTerrain(float x, float z) {
        for(Terrain terrain : terrains) {
            if(terrain.isOnTerrain(x, z)) {
                return terrain;
            }
        }

        return null;
    }


}
