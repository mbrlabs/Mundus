package com.mbrlabs.mundus.ui;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.ui.components.MundusToolbar;
import com.mbrlabs.mundus.ui.components.StatusBar;
import com.mbrlabs.mundus.ui.components.dialogs.NewProjectDialog;
import com.mbrlabs.mundus.ui.components.dialogs.OpenProjectDialog;
import com.mbrlabs.mundus.ui.components.dialogs.SettingsDialog;
import com.mbrlabs.mundus.ui.components.menu.MundusMenuBar;
import com.mbrlabs.mundus.ui.dto.ModelDTO;
import com.mbrlabs.mundus.ui.handler.menu.MenuNewProjectHandler;
import com.mbrlabs.mundus.ui.handler.menu.MenuSettingsHandler;
import com.mbrlabs.mundus.ui.handler.menu.NewProjectHandler;
import com.mbrlabs.mundus.ui.handler.menu.OpenProjectHandler;
import com.mbrlabs.mundus.ui.handler.toolbar.ToolbarImportHandler;

/**
 * @author Marcus Brummer
 * @version 27-11-2015
 */
public class Ui extends Stage {

    private VisTable root;
    private MundusMenuBar menuBar;
    private MundusToolbar toolbar;
    private FileChooser fileChooser;
    private StatusBar statusBar;
    private List<Model> modelList;


    private SettingsDialog settingsDialog;
    private NewProjectDialog newProjectDialog;
    private OpenProjectDialog openProjectDialog;

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

        // row 3: content
        modelList = new VisList<Model>();
        modelList.getStyle().background = VisUI.getSkin().getDrawable("default-pane");
        VisScrollPane scrollPane = new VisScrollPane(modelList);
        root.add(scrollPane).width(300).top().left().expandY().fillY().row();

        // row 4: status bar
        statusBar = new StatusBar();
        root.add(statusBar).expandX().fillX().height(20).row();

        // settings dialog
        settingsDialog = new SettingsDialog();
        newProjectDialog = new NewProjectDialog();
        openProjectDialog = new OpenProjectDialog();

        fileChooser = new FileChooser(FileChooser.Mode.OPEN);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);

        setHandlers();
    }

    private void setHandlers() {
        // Menu
        menuBar.getFileMenu().getNewProject().addListener(new MenuNewProjectHandler());
        menuBar.getWindowMenu().getSettings().addListener(new MenuSettingsHandler());
        menuBar.getFileMenu().getNewProject().addListener(new NewProjectHandler());
        menuBar.getFileMenu().getOpenProject().addListener(new OpenProjectHandler());

        // Toolbar
        toolbar.getImportBtn().addListener(new ToolbarImportHandler());
    }

    public void showDialog(VisDialog dialog) {
        dialog.show(this);
    }

    public List<Model> getModelList() {
        return modelList;
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
}
