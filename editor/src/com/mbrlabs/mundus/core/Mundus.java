package com.mbrlabs.mundus.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.core.home.HomeManager;
import com.mbrlabs.mundus.core.kryo.KryoManager;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.terrain.brushes.BrushManager;
import com.mbrlabs.mundus.terrain.brushes.SphereBrush;
import com.mbrlabs.mundus.Main;
import com.mbrlabs.mundus.core.home.HomeData;
import com.mbrlabs.mundus.input.InputManager;
import com.mbrlabs.mundus.terrain.TerrainTest;
import com.mbrlabs.mundus.ui.UiImages;
import com.mbrlabs.mundus.utils.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Core class.
 *
 * Holds static references to shared resources & application states.
 *
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class Mundus {

    private static ProjectContext projectContext;
    private static BrushManager brushManager;

    private static PerspectiveCamera cam;
    private static ModelBatch modelBatch;

    private static ProjectManager projectManager;
    private static InputManager input;
    private static Shaders shaders;

    private static HomeManager homeManager;

    private static KryoManager kryoManager;

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
        // Camera
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 1, -3);
        cam.lookAt(0,1,-1);
        cam.near = 0.2f;
        cam.far = 10000;
        cam.update();
        // model batch
        modelBatch = new ModelBatch();
        // shaders
        shaders = new Shaders();
        // project context
        projectContext = new ProjectContext();
        //projectContext.terrains.add(new TerrainTest().terrain);


        // brushes
        brushManager = new BrushManager(projectContext, cam);
        brushManager.addBrush(new SphereBrush(shaders.brushShader));

        kryoManager = new KryoManager();
        homeManager = new HomeManager(kryoManager);
        projectManager = new ProjectManager(projectContext, homeManager);

        if(homeManager.homeData.projects.size() == 0) {
            projectManager.createProject("Skyrim", "/home/marcus/MundusProjects");
        }

        // input
        input = new InputManager();
        input.addProcessor(brushManager);
    }

    public static void inject(Object o) {
        // get fields that are annotated with @Inject
        List<Field> injectableFields = new ArrayList<>();
        Class clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for(Field f : fields) {
            Annotation[] annotations = f.getDeclaredAnnotations();
            for(Annotation a : annotations) {
                if(a instanceof Inject) {
                    injectableFields.add(f);
                    Log.debug("DI: found injectable field: " + f.getName());
                }
            }
        }

        // inject
        try {
            for(Field f : injectableFields) {
                injectField(o, f);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * Looks at own static fields and injects value into object if found.
     *
     * @param o     object, in which field should be injected
     * @param field the injectable field
     *
     * @throws IllegalAccessException
     */
    private static void injectField(Object o, Field field) throws IllegalAccessException {
        for(Field f : Mundus.class.getDeclaredFields()) {
            if(Modifier.isStatic(f.getModifiers()) && Modifier.isPrivate(f.getModifiers())) {
                if(f.getType().equals(field.getType())) {
                    field.setAccessible(true);
                    field.set(o, f.get(null));
                }
            }
        }
    }

    /**
     * Disposes everything.
     */
    public static void dispose() {
        VisUI.dispose();
        modelBatch.dispose();
        shaders.dispose();
        brushManager.dispose();
        for(Model model : testModels) {
            model.dispose();
        }
        projectContext.dispose();

    }

}
