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

import com.kotcrab.vis.ui.widget.MenuBar;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class MundusMenuBar extends MenuBar {

    private FileMenu fileMenu;
    private EditMenu editMenu;
    private WindowMenu windowMenu;
    private TerrainMenu terrainMenu;
    private ModelsMenu modelsMenu;
    private EnvironmentMenu environmentMenu;
    private SceneMenu sceneMenu;

    public MundusMenuBar() {
        super();
        fileMenu = new FileMenu();
        editMenu = new EditMenu();
        modelsMenu = new ModelsMenu();
        terrainMenu = new TerrainMenu();
        windowMenu = new WindowMenu();
        environmentMenu = new EnvironmentMenu();
        sceneMenu = new SceneMenu();
        addMenu(fileMenu);
        addMenu(editMenu);
        addMenu(modelsMenu);
        addMenu(terrainMenu);
        addMenu(environmentMenu);
        addMenu(sceneMenu);
        addMenu(windowMenu);
    }

    public FileMenu getFileMenu() {
        return fileMenu;
    }

    public EditMenu getEditMenu() {
        return editMenu;
    }

    public WindowMenu getWindowMenu() {
        return windowMenu;
    }

    public TerrainMenu getTerrainMenu() {
        return terrainMenu;
    }

    public ModelsMenu getModelsMenu() {
        return modelsMenu;
    }

    public EnvironmentMenu getEnvironmentMenu() {
        return environmentMenu;
    }

}
