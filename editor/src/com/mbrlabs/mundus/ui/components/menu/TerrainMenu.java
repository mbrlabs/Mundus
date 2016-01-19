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

package com.mbrlabs.mundus.ui.components.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.components.dialogs.AddTerrainDialog;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class TerrainMenu extends Menu {

    private MenuItem addTerrain;
    private MenuItem loadHeightmap;
    private MenuItem perlinNoise;
    private MenuItem terrainManager;

    @Inject
    private ProjectContext projectContext;

    public TerrainMenu() {
        super("Terrain");
        Mundus.inject(this);
        addTerrain = new MenuItem("Add Terrain");
        loadHeightmap = new MenuItem("Load Heightmap");
        perlinNoise = new MenuItem("Generate Perlin noise");
        terrainManager = new MenuItem("Terrain Manager");

        addItem(addTerrain);
        addItem(loadHeightmap);
        addItem(perlinNoise);
        addItem(terrainManager);

        addListeners();
    }

    private void addListeners() {
        // add terrain
        addTerrain.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final Ui ui = Ui.getInstance();
                AddTerrainDialog dialog = ui.getAddTerrainDialog();
                ui.showDialog(dialog);
            }
        });

        // load heightmap
        loadHeightmap.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("asdfadf");
                // TODO dialog for selecting terrain + heightmap
                if(projectContext.currScene.terrainGroup.size() > 0) {
                    Terrain terrain = projectContext.currScene.terrainGroup.first();
                    terrain.loadHeightMap(new Pixmap(Gdx.files.internal("heightmaps/heightmap180.png")), 200);
                    terrain.update();
                }
            }
        });


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
