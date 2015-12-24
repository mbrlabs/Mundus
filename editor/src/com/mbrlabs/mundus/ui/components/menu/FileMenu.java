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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.ui.Ui;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class FileMenu extends Menu {

    private MenuItem newProject;
    private MenuItem openProject;
    private MenuItem importProject;
    private MenuItem saveProject;
    private MenuItem exit;

    public FileMenu() {
        super("File");

        newProject = new MenuItem("New Project");
        newProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.N);
        openProject = new MenuItem("Open Project");
        openProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.O);
        importProject = new MenuItem("Import Project");
        saveProject = new MenuItem("Save Project");
        saveProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.S);
        exit = new MenuItem("Exit");

        addItem(newProject);
        addItem(openProject);
        addItem(importProject);
        addItem(saveProject);
        addSeparator();
        addItem(exit);

        setupListeners();
    }

    private void setupListeners() {
        newProject.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Ui.getInstance().showDialog(Ui.getInstance().getNewProjectDialog());
            }
        });

        importProject.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO
            }
        });

    }

    public MenuItem getNewProject() {
        return newProject;
    }

    public MenuItem getOpenProject() {
        return openProject;
    }

    public MenuItem getSaveProject() {
        return saveProject;
    }

}
