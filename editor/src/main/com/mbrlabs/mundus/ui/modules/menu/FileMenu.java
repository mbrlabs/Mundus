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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;
import com.mbrlabs.mundus.core.HomeManager;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.kryo.descriptors.HomeDescriptor;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.exceptions.ProjectAlreadyImportedException;
import com.mbrlabs.mundus.exceptions.ProjectOpenException;
import com.mbrlabs.mundus.ui.Ui;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class FileMenu extends Menu {

    private MenuItem newProject;
    private MenuItem importProject;
    private MenuItem recentProjects;
    private MenuItem saveProject;
    private MenuItem exit;

    private FileChooser fileChooser;

    @Inject
    private HomeManager homeManager;
    @Inject
    private ProjectManager projectManager;

    public FileMenu() {
        super("File");
        Mundus.inject(this);

        fileChooser = new FileChooser(FileChooser.Mode.OPEN);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.DIRECTORIES);

        newProject = new MenuItem("New Project");
        newProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.N);
        importProject = new MenuItem("Import Project");
        importProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.O);
        recentProjects = new MenuItem("Recent Projects");
        saveProject = new MenuItem("Save Project");
        saveProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.S);
        exit = new MenuItem("Exit");

        // setup recent projects
        PopupMenu recentPrjectsPopup = new PopupMenu();
        for(final HomeDescriptor.ProjectRef ref : homeManager.homeDescriptor.projects) {
            MenuItem pro = new MenuItem(ref.getName() + " - [" + ref.getAbsolutePath() + "]");
            pro.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    try {
                        ProjectContext projectContext = projectManager.loadProject(ref);
                        projectManager.changeProject(projectContext);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Dialogs.showErrorDialog(Ui.getInstance(), "Could not open project");
                    }
                }
            });
            recentPrjectsPopup.addItem(pro);
        }
        recentProjects.setSubMenu(recentPrjectsPopup);

        addItem(newProject);
        addItem(importProject);
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

        importProject.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Ui.getInstance().addActor(fileChooser.fadeIn());
            }
        });

        // file chooser
        fileChooser.setListener(new SingleFileChooserListener() {
            public void selected(FileHandle file) {
                importNewProject(file);
            }
        });
    }

    public void importNewProject(FileHandle projectDir) {
        try {
            ProjectContext context = projectManager.importProject(projectDir.path());
            projectManager.changeProject(context);
        } catch (ProjectAlreadyImportedException e) {
            e.printStackTrace();
            Dialogs.showErrorDialog(Ui.getInstance(), "This Project is already imported.");
        } catch (ProjectOpenException e) {
            e.printStackTrace();
            Dialogs.showErrorDialog(Ui.getInstance(), "This Project can't be opened.");
        }
    }

}
