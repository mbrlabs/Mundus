/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.Separator;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.ui.modules.MundusToolbar;
import com.mbrlabs.mundus.ui.modules.Outline;
import com.mbrlabs.mundus.ui.modules.StatusBar;
import com.mbrlabs.mundus.ui.modules.dialogs.AddTerrainDialog;
import com.mbrlabs.mundus.ui.modules.dialogs.AmbientLightDialog;
import com.mbrlabs.mundus.ui.modules.dialogs.ExitDialog;
import com.mbrlabs.mundus.ui.modules.dialogs.ExportDialog;
import com.mbrlabs.mundus.ui.modules.dialogs.FogDialog;
import com.mbrlabs.mundus.ui.modules.dialogs.LoadingProjectDialog;
import com.mbrlabs.mundus.ui.modules.dialogs.NewProjectDialog;
import com.mbrlabs.mundus.ui.modules.dialogs.SkyboxDialog;
import com.mbrlabs.mundus.ui.modules.dialogs.assets.AssetSelectionDialog;
import com.mbrlabs.mundus.ui.modules.dialogs.importer.ImportMeshDialog;
import com.mbrlabs.mundus.ui.modules.dialogs.importer.ImportTextureDialog;
import com.mbrlabs.mundus.ui.modules.dialogs.settings.SettingsDialog;
import com.mbrlabs.mundus.ui.modules.dock.DockBar;
import com.mbrlabs.mundus.ui.modules.inspector.Inspector;
import com.mbrlabs.mundus.ui.modules.menu.MundusMenuBar;
import com.mbrlabs.mundus.ui.widgets.MundusMultiSplitPane;
import com.mbrlabs.mundus.ui.widgets.MundusSplitPane;
import com.mbrlabs.mundus.ui.widgets.RenderWidget;
import com.mbrlabs.mundus.utils.Toaster;

/**
 * @author Marcus Brummer
 * @version 27-11-2015
 */
public class Ui extends Stage {

    public static Separator.SeparatorStyle greenSeperator;

    private VisTable root;
    private MundusSplitPane splitPane;
    private Toaster toaster;
    private MundusMenuBar menuBar;
    private MundusToolbar toolbar;
    private FileChooser fileChooser;
    private StatusBar statusBar;
    private Inspector inspector;
    private Outline outline;

    private DockBar docker;

    private SettingsDialog settingsDialog;
    private NewProjectDialog newProjectDialog;
    // private AddTerrainDialog addTerrainDialog;
    private LoadingProjectDialog loadingProjectDialog;
    private ExportDialog exportDialog;
    private ImportMeshDialog importMeshDialog;
    private ImportTextureDialog importTextureDialog;
    private FogDialog fogDialog;
    private SkyboxDialog skyboxDialog;
    private AmbientLightDialog ambientLightDialog;
    private AssetSelectionDialog assetSelectionDialog;
    private ExitDialog exitDialog;

    private RenderWidget widget3D;

    private static Ui INSTANCE;

    public static Ui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Ui();
        }

        return INSTANCE;
    }

    private Ui() {
        super(new ScreenViewport());

        // init custom styles
        greenSeperator = new Separator.SeparatorStyle(VisUI.getSkin().getDrawable("separator-green"), 1);

        toaster = new Toaster(this);
        root = new VisTable();
        addActor(root);
        root.setFillParent(true);

        VisTable mainContainer = new VisTable();
        splitPane = new MundusSplitPane(mainContainer, null, true);

        // row 1: add menu
        menuBar = new MundusMenuBar();
        root.add(menuBar.getTable()).fillX().expandX().row();

        // row 2: toolbar
        toolbar = new MundusToolbar();
        root.add(toolbar.getRoot()).fillX().expandX().row();

        // row 3: sidebar & 3d viewport & inspector
        outline = new Outline();
        inspector = new Inspector();
        widget3D = new RenderWidget();
        MundusMultiSplitPane multiSplit = new MundusMultiSplitPane(false);
        multiSplit.setDraggable(false);
        multiSplit.setWidgets(outline, widget3D, inspector);
        multiSplit.setSplit(0, 0.2f);
        multiSplit.setSplit(1, 0.8f);
        mainContainer.add(multiSplit).grow().row();

        root.add(splitPane).grow().row();

        // row 4: DOCKER
        docker = new DockBar(splitPane);
        root.add(docker).bottom().expandX().fillX().height(30).row();

        // row 5: status bar
        statusBar = new StatusBar();
        root.add(statusBar).expandX().fillX().height(25).row();

        // dialogs
        settingsDialog = new SettingsDialog();
        newProjectDialog = new NewProjectDialog();
        // addTerrainDialog = new AddTerrainDialog();
        loadingProjectDialog = new LoadingProjectDialog();
        exportDialog = new ExportDialog();
        fogDialog = new FogDialog();
        skyboxDialog = new SkyboxDialog();
        ambientLightDialog = new AmbientLightDialog();
        importMeshDialog = new ImportMeshDialog();
        importTextureDialog = new ImportTextureDialog();
        assetSelectionDialog = new AssetSelectionDialog();
        exitDialog = new ExitDialog();

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

//    public AddTerrainDialog getAddTerrainDialog() {
//        return addTerrainDialog;
//    }

    public LoadingProjectDialog getLoadingProjectDialog() {
        return loadingProjectDialog;
    }

    public ExportDialog getExportDialog() {
        return exportDialog;
    }

    public ImportMeshDialog getImportMeshDialog() {
        return importMeshDialog;
    }

    public ImportTextureDialog getImportTextureDialog() {
        return importTextureDialog;
    }

    public Outline getOutline() {
        return outline;
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

    public AssetSelectionDialog getAssetSelectionDialog() {
        return assetSelectionDialog;
    }

    public ExitDialog getExitDialog() {
        return exitDialog;
    }

    public RenderWidget getWidget3D() {
        return widget3D;
    }

}
