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

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.ui.modules.MundusToolbar;
import com.mbrlabs.mundus.ui.modules.StatusBar;
import com.mbrlabs.mundus.ui.modules.dialogs.*;
import com.mbrlabs.mundus.ui.modules.dialogs.importer.ImportDialog;
import com.mbrlabs.mundus.ui.modules.dialogs.settings.SettingsDialog;
import com.mbrlabs.mundus.ui.modules.inspector.Inspector;
import com.mbrlabs.mundus.ui.modules.menu.MundusMenuBar;
import com.mbrlabs.mundus.ui.modules.sidebar.Sidebar;
import com.mbrlabs.mundus.ui.widgets.RenderWidget;
import com.mbrlabs.mundus.utils.Toaster;

/**
 * @author Marcus Brummer
 * @version 27-11-2015
 */
public class Ui extends Stage {

    private VisTable root;
    private Toaster toaster;
    private MundusMenuBar menuBar;
    private MundusToolbar toolbar;
    private FileChooser fileChooser;
    private StatusBar statusBar;
    private Sidebar sidebar;
    private Inspector inspector;

    //private DockBar docker;

    private SettingsDialog settingsDialog;
    private NewProjectDialog newProjectDialog;
    private ImportDialog importDialog;
    private AddTerrainDialog addTerrainDialog;
    private LoadingProjectDialog loadingProjectDialog;
    private ExportDialog exportDialog;
    private FogDialog fogDialog;
    private SkyboxDialog skyboxDialog;
    private AmbientLightDialog ambientLightDialog;

    private RenderWidget widget3D;

    private static Ui INSTANCE;

    public static Ui getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Ui();
        }

        return INSTANCE;
    }

    private Ui() {
        super(new ScreenViewport());
        toaster = new Toaster(this);
        root = new VisTable();
        //root.setDebug(true);
        root.setFillParent(true);
        root.align(Align.center | Align.top);
        addActor(root);

        // row 1: add menu
        menuBar = new MundusMenuBar();
        root.add(menuBar.getTable()).fillX().expandX().row();

        // row 2: toolbar
        toolbar = new MundusToolbar();
        root.add(toolbar.getRoot()).fillX().expandX().row();

        // row 3: sidebar & 3d viewport & inspector
        VisTable center = new VisTable();
        sidebar = new Sidebar();
        inspector = new Inspector();
        widget3D = new RenderWidget();

        center.add(sidebar).width(300).top().left().expandY().fillY();
        center.add(widget3D).pad(2).expand().fill();
        center.add(inspector).width(300).top().right().expandY().fillY();
        root.add(center).top().left().expand().fill().row();

        // row 4: DOCKER
        //docker = new DockBar();
        //root.add(docker).expandX().fillX().row();

        // row 5: status bar
        statusBar = new StatusBar();
        root.add(statusBar).expandX().fillX().height(25).row();

        // settings dialog
        settingsDialog = new SettingsDialog();
        newProjectDialog = new NewProjectDialog();
        importDialog = new ImportDialog();
        addTerrainDialog = new AddTerrainDialog();
        loadingProjectDialog = new LoadingProjectDialog();
        exportDialog = new ExportDialog();
        fogDialog = new FogDialog();
        skyboxDialog = new SkyboxDialog();
        ambientLightDialog = new AmbientLightDialog();

        fileChooser = new FileChooser(FileChooser.Mode.OPEN);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
    }

    public void showDialog(VisDialog dialog) {
        dialog.show(this);
    }

    public Toaster getToaster() {
        return toaster;
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

    public AmbientLightDialog getAmbientLightDialog() {
        return ambientLightDialog;
    }

    public SkyboxDialog getSkyboxDialog() {
        return skyboxDialog;
    }

    public RenderWidget getWidget3D() {
        return widget3D;
    }
}
