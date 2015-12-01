package com.mbrlabs.mundus.ui.components.menu;

import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class TerrainMenu extends Menu {

    private MenuItem addTerrain;
    private MenuItem loadHeightmap;
    private MenuItem perlinNoise;
    private MenuItem terrainManager;

    public TerrainMenu() {
        super("Terrain");
        addTerrain = new MenuItem("Add Terrain");
        loadHeightmap = new MenuItem("Load Heightmap");
        perlinNoise = new MenuItem("Generate Perlin noise");
        terrainManager = new MenuItem("Terrain Manager");

        addItem(addTerrain);
        addItem(loadHeightmap);
        addItem(perlinNoise);
        addItem(terrainManager);
    }

    public MenuItem getAddTerrain() {
        return addTerrain;
    }

    public MenuItem getLoadHeightmap() {
        return loadHeightmap;
    }

    public MenuItem getPerlinNoise() {
        return perlinNoise;
    }

    public MenuItem getTerrainManager() {
        return terrainManager;
    }

}
