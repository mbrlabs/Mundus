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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.input.FreeCamController;
import com.mbrlabs.mundus.core.kryo.KryoManager;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.EventBus;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.Main;
import com.mbrlabs.mundus.input.InputManager;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.utils.Colors;
import com.mbrlabs.mundus.utils.Fa;
import com.mbrlabs.mundus.utils.Log;
import com.mbrlabs.mundus.utils.ReflectionUtils;

import java.io.File;
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

    private static InputManager input;
    private static FreeCamController camController;
    private static Shaders shaders;

    private static KryoManager kryoManager;
    private static ProjectManager projectManager;
    private static HomeManager homeManager;
    private static ImportManager importManager;

    public static Array<Model> testModels = new Array<>();

    public static Array<ModelInstance> testInstances = new Array<>();
    public static BitmapFont fa;
    public static ModelBatch modelBatch;
    public static EventBus eventBus;

    /**
     * Loads & initializes everything.
     *
     * This includes editor specific resources but also project specific
     * resources (see ProjectContext).
     *
     */
    public static void init() {
        File homeDir = new File(Files.HOME_DIR);
        if(!homeDir.exists()) {
            homeDir.mkdir();
        }

        initStyle();

        // init logging
        Log.init();
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
        projectManager = new ProjectManager(projectContext, kryoManager, homeManager, toolManager, modelBatch, shaders);
        importManager = new ImportManager(homeManager);

        Fa faBuilder = new Fa(Gdx.files.internal("fonts/fa45.ttf"));
        faBuilder.getGeneratorParameter().size = (int)(Gdx.graphics.getHeight() * 0.025f);
        fa = faBuilder
                .addIcon(Fa.SAVE)
                .addIcon(Fa.DOWNLOAD)
                .addIcon(Fa.GIFT)
                .addIcon(Fa.PLAY)
                .addIcon(Fa.MOUSE_POINTER)
                .addIcon(Fa.ARROWS)
                .addIcon(Fa.CIRCLE_O)
                .addIcon(Fa.MINUS)
                .addIcon(Fa.CARET_DOWN)
                .addIcon(Fa.CARET_UP)
                .addIcon(Fa.TIMES)
                .build();
    }

    private static void initStyle() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/open-sans/OpenSans-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 14;
        params.kerning = true;
        params.borderStraight = false;
        BitmapFont font = generator.generateFont(params);
        generator.dispose();

        Skin skin = new Skin();
        skin.add("opensans-regular", font, BitmapFont.class);
        skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/skin/uiskin.atlas")));
        skin.load(Gdx.files.internal("ui/skin/uiskin.json"));
        VisUI.load(skin);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Colors.TURQUOISE);
        pixmap.drawPixel(0,0);
        Drawable d = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        VisUI.getSkin().add("accent-color", d, Drawable.class);

        FileChooser.setFavoritesPrefsName(Main.class.getPackage().getName());
    }

    public static void postEvent(Object event) {
        eventBus.post(event);
    }

    public static void registerEventListener(Object listener) {
        eventBus.register(listener);
    }

    public static void unregisterEventListener(Object listener) {
        eventBus.unregister(listener);
    }

    public static void inject(Object o) {
        // get fields that are annotated with @Inject
        List<Field> injectableFields = new ArrayList<>();
        Class clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for(Field f : fields) {
            if(ReflectionUtils.hasFieldAnnotation(f, Inject.class)) {
                injectableFields.add(f);
                Log.debug("DI: found injectable field: " + f.getName());
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
        toolManager.dispose();
        fa.dispose();
        //brushManager.dispose();
        for(Model model : testModels) {
            model.dispose();
        }
        projectContext.dispose();

    }

}
