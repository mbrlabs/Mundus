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

package com.mbrlabs.mundus.core;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.commons.FreeCamController;
import com.mbrlabs.mundus.core.kryo.KryoManager;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.EventBus;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.Main;
import com.mbrlabs.mundus.input.InputManager;
import com.mbrlabs.mundus.tools.ToolManager;
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
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class Mundus {

    private static ProjectContext projectContext;

    private static ToolManager toolManager;

    private static ModelBatch modelBatch;

    private static InputManager input;
    private static FreeCamController camController;
    private static Shaders shaders;

    private static KryoManager kryoManager;
    private static ProjectManager projectManager;
    private static HomeManager homeManager;
    private static ImportManager importManager;

    private static EventBus eventBus;

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
        // model batch
        modelBatch = new ModelBatch();
        // shaders
        shaders = new Shaders();
        // project context
        projectContext = new ProjectContext(-1);
        projectContext.loaded = false;

        input = new InputManager();
        toolManager = new ToolManager(input, projectContext, modelBatch, shaders);

        eventBus = new EventBus();
        kryoManager = new KryoManager();
        homeManager = new HomeManager(kryoManager);
        projectManager = new ProjectManager(projectContext, kryoManager, homeManager, eventBus, toolManager);
        importManager = new ImportManager(homeManager);

        // input
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
        //brushManager.dispose();
        for(Model model : testModels) {
            model.dispose();
        }
        projectContext.dispose();

    }

}
