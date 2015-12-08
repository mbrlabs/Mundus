package com.mbrlabs.mundus.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.utils.Compass;
import com.mbrlabs.mundus.Main;
import com.mbrlabs.mundus.data.ProjectContext;
import com.mbrlabs.mundus.data.ProjectManager;
import com.mbrlabs.mundus.data.home.MundusHome;
import com.mbrlabs.mundus.input.InputManager;
import com.mbrlabs.mundus.input.navigation.FreeCamController;
import com.mbrlabs.mundus.terrain.TerrainTest;
import com.mbrlabs.mundus.terrain.brushes.Brush;
import com.mbrlabs.mundus.terrain.brushes.SphereBrush;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.UiImages;
import com.mbrlabs.mundus.utils.Log;

/**
 * Core class.
 *
 * Holds static references to shared resources & application states.
 *
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class Mundus {

    public static MundusHome home;

    /**
     * Main camera for 3d viewport.
     */
    public static PerspectiveCamera cam;

    public static Compass compass;

    /**
     * For batching 3d model rendering.
     */
    public static ModelBatch modelBatch;

    /**
     * The current project context.
     */
    public static ProjectContext projectContext;

    public static ProjectManager projectManager;

    /**
     * Holds all available brushes
     */
    public static Brushes brushes;

    /**
     * Holdes all shaders.
     */
    public static Shaders shaders;

    /**
     * Main UI stage.
     */
    public static Ui ui;

    public static InputManager input;

    public static Array<Model> testModels = new Array<>();
    public static Array<ModelInstance> testInstances = new Array<>();


    /**
     * Loads & initializes everything.
     *
     * This includes editor specific resources but also project specific
     * resources (see ProjectContext).
     *
     */
    public static void init() {
        // init logging
        Log.init();
        // init visUI
        VisUI.load();
        FileChooser.setFavoritesPrefsName(Main.class.getPackage().getName());
        // load images
        UiImages.load();
        // home
        home = MundusHome.getInstance();
        // Camera
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 1, -3);
        cam.lookAt(0,1,-1);
        cam.near = 0.2f;
        cam.far = 10000;
        cam.update();
        // create compass
        compass = new Compass(cam);
        // model batch
        modelBatch = new ModelBatch();
        // shaders
        shaders = new Shaders();
        // project context
        projectContext = new ProjectContext();
        projectContext.terrains.add(new TerrainTest().terrain);
        // project manager
        projectManager = new ProjectManager();

        // brushes
        brushes = new Brushes();

        if(home.getProjectRefs().getProjects().size() == 0) {
            projectManager.createProject("Skyrim", "/home/marcus/MundusProjects");
        }


        // ui
        ui = Ui.getInstance();

        // input
        input = new InputManager(ui);
        input.setWorldNavigation(new FreeCamController(cam));
    }

    /**
     * Disposes everything.
     */
    public static void dispose() {
        VisUI.dispose();
        modelBatch.dispose();
        shaders.dispose();
        brushes.sphereBrush.dispose();
        for(Model model : testModels) {
            model.dispose();
        }
        projectContext.dispose();

    }

}
