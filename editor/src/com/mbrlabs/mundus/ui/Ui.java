package com.mbrlabs.mundus.ui;

import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.MyStage;
import com.mbrlabs.mundus.ui.components.dialogs.*;
import com.mbrlabs.mundus.ui.components.docker.DockBar;
import com.mbrlabs.mundus.ui.components.toolbar.MundusToolbar;
import com.mbrlabs.mundus.ui.components.StatusBar;
import com.mbrlabs.mundus.ui.components.menu.MundusMenuBar;
import com.mbrlabs.mundus.ui.components.sidebar.Sidebar;
import com.mbrlabs.mundus.ui.handler.menu.*;
import com.mbrlabs.mundus.ui.handler.ModelImportHandler;

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

    private DockBar docker;

    private SettingsDialog settingsDialog;
    private NewProjectDialog newProjectDialog;
    private OpenProjectDialog openProjectDialog;
    private ImportModelDialog importModelDialog;
    private AddTerrainDialog addTerrainDialog;
    private LoadingProjectDialog loadingProjectDialog;

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
        root.add(toolbar).fillX().expandX().row();

        // row 3: sidebar
        sidebar = new Sidebar();
        root.add(sidebar.getTable()).width(300).top().left().row();

        root.add(sidebar.getContentContainer()).width(300).top().left().expandY().fillY().row();

        // row 4: DOCKER
        docker = new DockBar();
        root.add(docker).expandX().fillX().row();

        // row 5: status bar
        statusBar = new StatusBar();
        root.add(statusBar).expandX().fillX().height(20).row();

        // settings dialog
        settingsDialog = new SettingsDialog();
        newProjectDialog = new NewProjectDialog();
        openProjectDialog = new OpenProjectDialog();
        importModelDialog = new ImportModelDialog();
        addTerrainDialog = new AddTerrainDialog();
        loadingProjectDialog = new LoadingProjectDialog();

        fileChooser = new FileChooser(FileChooser.Mode.OPEN);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);

        setHandlers();
    }

    private void setHandlers() {
        ModelImportHandler modelImportHandler = new ModelImportHandler();

        // Menu
        menuBar.getFileMenu().getNewProject().addListener(new MenuNewProjectHandler());
        menuBar.getWindowMenu().getSettings().addListener(new MenuSettingsHandler());
        menuBar.getFileMenu().getOpenProject().addListener(new OpenProjectHandler());
        menuBar.getTerrainMenu().getAddTerrain().addListener(new AddTerrainHandler());
        menuBar.getModelsMenu().getImportModel().addListener(modelImportHandler);

        // Toolbar
        toolbar.getImportBtn().addListener(modelImportHandler);
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

    public OpenProjectDialog getOpenProjectDialog() {
        return openProjectDialog;
    }

    public ImportModelDialog getImportModelDialog() {
        return importModelDialog;
    }

    public AddTerrainDialog getAddTerrainDialog() {
        return addTerrainDialog;
    }

    public LoadingProjectDialog getLoadingProjectDialog() {
        return loadingProjectDialog;
    }

    public Sidebar getSidebar() {
        return sidebar;
    }
}
