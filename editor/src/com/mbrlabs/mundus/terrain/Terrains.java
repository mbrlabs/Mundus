package com.mbrlabs.mundus.terrain;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 13-12-2015
 */
public class Terrains {

    private List<Terrain> terrains;

    public Terrains() {
        this.terrains = new ArrayList<Terrain>();
    }

    public void add(Terrain terrain) {
        this.terrains.add(terrain);
    }

    public List<Terrain> getTerrains() {
        return this.terrains;
    }

    public int size() {
        return terrains.size();
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
            if(isPointOnTerrain(terrain, out.x, out.z)) {
                return out;
            }
        }
        return null;
    }

    public Terrain getTerrain(float x, float z) {
        for(Terrain terrain : terrains) {
            if(isPointOnTerrain(terrain, x, z)) {
                return terrain;
            }
        }

        return null;
    }

    public boolean isPointOnTerrain(Terrain terrain, float x, float z) {
        return x >= terrain.position.x && x <= terrain.position.x + terrain.terrainWidth
                && z >= terrain.position.z && z <= terrain.position.z + terrain.terrainDepth;
    }


}
