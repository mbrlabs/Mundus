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

    public TerrainMenu() {
        super("Terrain");
        addTerrain = new MenuItem("Add Terrain");
        loadHeightmap = new MenuItem("Load Heightmap");

        addItem(addTerrain);
        addItem(loadHeightmap);
    }

    public MenuItem getAddTerrain() {
        return addTerrain;
    }

    public MenuItem getLoadHeightmap() {
        return loadHeightmap;
    }

}
