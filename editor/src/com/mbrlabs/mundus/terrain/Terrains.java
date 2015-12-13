package com.mbrlabs.mundus.terrain;

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

    public void addTerrain(Terrain terrain) {
        this.terrains.add(terrain);
    }

    public List<Terrain> getTerrains() {
        return this.terrains;
    }

    public Terrain getTerrainAt(float worldX, float worldZ) {
        return null;
    }

    public float getHeightAt(float worldX, float worldZ) {
        return 0;
    }

}
