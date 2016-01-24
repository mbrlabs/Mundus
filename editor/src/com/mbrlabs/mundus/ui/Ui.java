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

package com.mbrlabs.mundus.ui;

import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.ui.components.dialogs.*;
import com.mbrlabs.mundus.ui.components.MundusToolbar;
import com.mbrlabs.mundus.ui.components.StatusBar;
import com.mbrlabs.mundus.ui.components.dialogs.importer.ImportDialog;
import com.mbrlabs.mundus.ui.components.inspector.Inspector;
import com.mbrlabs.mundus.ui.components.menu.MundusMenuBar;
import com.mbrlabs.mundus.ui.components.sidebar.Sidebar;

/**
 * @author Marcus Brummer
 * @version 27-11-2015
 */
public class Ui extends MyStage {

    private VisTable root;
    private MundusMenuBar menuBar;
    private MundusToolbar toolbar;
    private FileChooser fileChooser;
    private StatusBar statusBar;
    private Sidebar sidebar;
    private Inspector inspector;

    //private DockBar docker;

    private SettingsDialog settingsDialog;
    private NewProjectDialog newProjectDialog;
   // private OpenProjectDialog openProjectDialog;
    private ImportDialog importDialog;
    private AddTerrainDialog addTerrainDialog;
    private LoadingProjectDialog loadingProjectDialog;
    private ExportDialog exportDialog;
    private FogDialog fogDialog;
    private SkyboxDialog skyboxDialog;

    private static Ui INSTANCE;

    public static Ui getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Ui();
        }

        return INSTANCE;
    }

    private Ui() {
        super(new ScreenViewport());
        // create root table
        root = new VisTable();
        root.setFillParent(true);
        root.align(Align.left | Align.top);
        //root.setDebug(true);
        addActor(this.root);

        // row 1: add menu
        menuBar = new MundusMenuBar();
        root.add(menuBar.getTable()).fillX().expandX().row();

        // row 2: toolbar
        toolbar = new MundusToolbar();
        root.add(toolbar.getRoot()).fillX().expandX().row();

        // row 3: sidebar & inspector
        VisTable sidebarAndInspector = new VisTable();
        sidebar = new Sidebar();
        inspector = new Inspector();
        sidebarAndInspector.add(sidebar).width(300).top().left().expand().fill();
        sidebarAndInspector.add(inspector).width(300).top().right().expand().padRight(5).padTop(5).row();
        root.add(sidebarAndInspector).top().left().expand().fill().row();

        // row 4: DOCKER
        //docker = new DockBar();
        //root.add(docker).expandX().fillX().row();

        // row 5: status bar
        statusBar = new StatusBar();
        root.add(statusBar).expandX().fillX().height(20).row();

        // settings dialog
        settingsDialog = new SettingsDialog();
        newProjectDialog = new NewProjectDialog();
       // openProjectDialog = new OpenProjectDialog();
        importDialog = new ImportDialog();
        addTerrainDialog = new AddTerrainDialog();
        loadingProjectDialog = new LoadingProjectDialog();
        exportDialog = new ExportDialog();
        fogDialog = new FogDialog();
        skyboxDialog = new SkyboxDialog();

        fileChooser = new FileChooser(FileChooser.Mode.OPEN);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
    }

    public void showDialog(VisDialog dialog) {
        dialog.show(this);
    }

    public MundusMenuBar getMenuBar() {
        return menuBar;
    }

    public MundusToolbar getToolbar() {
        return toolbar;
    }

    public FileChooser getFileChooser() {
        return fileChooser;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    public SettingsDialog getSettingsDialog() {
        return settingsDialog;
    }

    public NewProjectDialog getNewProjectDialog() {
        return newProjectDialog;
    }

//    public OpenProjectDialog getOpenProjectDialog() {
//        return openProjectDialog;
//    }

    public ImportDialog getImportDialog() {
        return importDialog;
    }

    public AddTerrainDialog getAddTerrainDialog() {
        return addTerrainDialog;
    }

    public LoadingProjectDialog getLoadingProjectDialog() {
        return loadingProjectDialog;
    }

    public ExportDialog getExportDialog() {
        return exportDialog;
    }

    public Sidebar getSidebar() {
        return sidebar;
    }

    public FogDialog getFogDialog() {
        return fogDialog;
    }

    public SkyboxDialog getSkyboxDialog() {
        return skyboxDialog;
    }
}
