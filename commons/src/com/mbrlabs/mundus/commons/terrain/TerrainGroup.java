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

package com.mbrlabs.mundus.commons.terrain;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

/**
 * @author Marcus Brummer
 * @version 13-12-2015
 */
public class TerrainGroup {

    private Array<TerrainInstance> terrains;

    private Vector3 tv3 = new Vector3();

    public TerrainGroup() {
        this.terrains = new Array<>();
    }

    public void add(TerrainInstance terrain) {
        this.terrains.add(terrain);
    }

    public Array<TerrainInstance> getTerrains() {
        return this.terrains;
    }

    public int size() {
        return terrains.size;
    }

    public TerrainInstance get(int index) {
        return terrains.get(index);
    }

    public TerrainInstance first() {
        return get(0);
    }

    public Vector3 getRayIntersection(Vector3 out, Ray ray) {
        for(TerrainInstance terrain : terrains) {
            terrain.getRayIntersection(out, ray);
            if(isPointOnTerrain(terrain, out.x, out.z)) {
                return out;
            }
        }
        return null;
    }

    public TerrainInstance getTerrain(float x, float z) {
        for(TerrainInstance terrain : terrains) {
            if(isPointOnTerrain(terrain, x, z)) {
                return terrain;
            }
        }

        return null;
    }

    public boolean isPointOnTerrain(TerrainInstance terrain, float x, float z) {
        tv3 = terrain.getPosition();
        return x >= tv3.x && x <= tv3.x + terrain.terrain.terrainWidth
                && z >= tv3.z && z <= tv3.z + terrain.terrain.terrainDepth;
    }


}
