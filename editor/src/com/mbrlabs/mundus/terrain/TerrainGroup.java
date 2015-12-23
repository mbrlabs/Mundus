package com.mbrlabs.mundus.terrain;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 13-12-2015
 */
public class TerrainGroup {

    private List<TerrainInstance> terrains;

    private Vector3 tv3 = new Vector3();

    public TerrainGroup() {
        this.terrains = new ArrayList<>();
    }

    public void add(TerrainInstance terrain) {
        this.terrains.add(terrain);
    }

    public List<TerrainInstance> getTerrains() {
        return this.terrains;
    }

    public int size() {
        return terrains.size();
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
