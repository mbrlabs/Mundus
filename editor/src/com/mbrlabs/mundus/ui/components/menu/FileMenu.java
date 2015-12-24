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
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.mbrlabs.mundus.core.HomeManager;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.core.project.ProjectRef;
import com.mbrlabs.mundus.ui.Ui;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class FileMenu extends Menu {

    private MenuItem newProject;
    private MenuItem openProject;
    private MenuItem recentProjects;
    private MenuItem saveProject;
    private MenuItem exit;

    @Inject
    private HomeManager homeManager;
    @Inject
    private ProjectManager projectManager;

    public FileMenu() {
        super("File");
        Mundus.inject(this);

        newProject = new MenuItem("New Project");
        newProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.N);
        openProject = new MenuItem("Open Project");
        openProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.O);
        recentProjects = new MenuItem("Recent Projects");
        saveProject = new MenuItem("Save Project");
        saveProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.S);
        exit = new MenuItem("Exit");

        // setup recent projects
        PopupMenu recentPrjectsPopup = new PopupMenu();
        for(ProjectRef ref : homeManager.homeDescriptor.projects) {
            MenuItem pro = new MenuItem(ref.getName() + " - [" + ref.getPath() + "]");
            pro.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    ProjectContext projectContext = projectManager.loadProject(ref);
                    projectManager.changeProject(projectContext);
                }
            });
            recentPrjectsPopup.addItem(pro);
        }
        recentProjects.setSubMenu(recentPrjectsPopup);

        addItem(newProject);
        addItem(openProject);
        addItem(saveProject);
        addItem(recentProjects);
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
