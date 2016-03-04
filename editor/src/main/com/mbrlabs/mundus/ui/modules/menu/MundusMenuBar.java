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

package com.mbrlabs.mundus.ui.modules.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class MundusMenuBar extends MenuBar {

    private FileMenu fileMenu;
    private EditMenu editMenu;
    private WindowMenu windowMenu;
    private ModelsMenu modelsMenu;
    private EnvironmentMenu environmentMenu;
    private SceneMenu sceneMenu;

    public MundusMenuBar() {
        super();
        fileMenu = new FileMenu();
        editMenu = new EditMenu();
        modelsMenu = new ModelsMenu();
        windowMenu = new WindowMenu();
        environmentMenu = new EnvironmentMenu();
        sceneMenu = new SceneMenu();
        addMenu(fileMenu);
        addMenu(editMenu);
        addMenu(modelsMenu);
        addMenu(environmentMenu);
        addMenu(sceneMenu);
        addMenu(windowMenu);
    }

    @Override
    public Table getTable() {
        VisTable root = new VisTable();
        root.setBackground("menu-bg");
        Table menuTable = super.getTable();

        VisImage icon = new VisImage(new Texture(Gdx.files.internal("ui/menu_icon.png")));
        root.add(icon).center().left().pad(5);
        root.add(menuTable).expand().fill().left().center().row();
        Container sep = new Container();
        sep.setBackground(VisUI.getSkin().getDrawable("separator-green"));
        root.add(sep).expandX().fillX().height(1).colspan(2).row();

        return root;
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

    public ModelsMenu getModelsMenu() {
        return modelsMenu;
    }

    public EnvironmentMenu getEnvironmentMenu() {
        return environmentMenu;
    }

}
