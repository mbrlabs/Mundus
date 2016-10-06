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

package com.mbrlabs.mundus.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.Main;
import com.mbrlabs.mundus.assets.ModelImporter;
import com.mbrlabs.mundus.core.kryo.KryoManager;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.core.registry.Registry;
import com.mbrlabs.mundus.events.EventBus;
import com.mbrlabs.mundus.history.CommandHistory;
import com.mbrlabs.mundus.input.FreeCamController;
import com.mbrlabs.mundus.input.InputManager;
import com.mbrlabs.mundus.input.ShortcutController;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.tools.picker.ToolHandlePicker;
import com.mbrlabs.mundus.utils.Fa;
import com.mbrlabs.mundus.utils.ReflectionUtils;

import java.io.File;
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

    private static ToolManager toolManager;
    private static InputManager input;
    private static FreeCamController freeCamController;
    private static ShortcutController shortcutController;
    private static Shaders shaders;
    private static ShapeRenderer shapeRenderer;
    private static KryoManager kryoManager;
    private static ProjectManager projectManager;
    private static Registry registry;
    private static ModelImporter modelImporter;
    private static CommandHistory commandHistory;
    private static GameObjectPicker goPicker;
    private static ToolHandlePicker handlePicker;

    public static EventBus eventBus;

    public static BitmapFont fa;
    public static ModelBatch modelBatch;

    /**
     * Loads & initializes everything.
     *
     * This includes editor specific resources but also project specific
     * resources (see ProjectContext).
     *
     */
    public static void init() {
        File homeDir = new File(Registry.HOME_DIR);
        if (!homeDir.exists()) {
            homeDir.mkdir();
        }

        initStyle();
        initFontAwesome();

        shapeRenderer = new ShapeRenderer();
        modelBatch = new ModelBatch();
        shaders = new Shaders();
        input = new InputManager();
        goPicker = new GameObjectPicker();
        handlePicker = new ToolHandlePicker();
        eventBus = new EventBus();
        kryoManager = new KryoManager();
        registry = kryoManager.loadRegistry();
        freeCamController = new FreeCamController();
        commandHistory = new CommandHistory(CommandHistory.DEFAULT_LIMIT);

        modelImporter = new ModelImporter(registry);
        projectManager = new ProjectManager(kryoManager, registry, shaders);
        toolManager = new ToolManager(input, projectManager, goPicker, handlePicker, modelBatch, shaders, shapeRenderer,
                commandHistory);
        shortcutController = new ShortcutController(registry, projectManager, commandHistory);
    }

    private static void initStyle() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/open-sans/OpenSans-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.kerning = true;
        params.borderStraight = false;

        // font norm
        params.size = 13;
        BitmapFont fontNorm = generator.generateFont(params);

        // font small
        params.size = 12;
        BitmapFont fontSmall = generator.generateFont(params);
        generator.dispose();

        // skin
        Skin skin = new Skin();
        skin.add("font-norm", fontNorm, BitmapFont.class);
        skin.add("font-small", fontSmall, BitmapFont.class);

        skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/skin/uiskin.atlas")));
        skin.load(Gdx.files.internal("ui/skin/uiskin.json"));
        VisUI.load(skin);

        FileChooser.setFavoritesPrefsName(Main.class.getPackage().getName());
    }

    private static void initFontAwesome() {
        Fa faBuilder = new Fa(Gdx.files.internal("fonts/fa45.ttf"));
        faBuilder.getGeneratorParameter().size = (int) (Gdx.graphics.getHeight() * 0.02f);
        faBuilder.getGeneratorParameter().kerning = true;
        faBuilder.getGeneratorParameter().borderStraight = false;
        fa = faBuilder.addIcon(Fa.SAVE).addIcon(Fa.DOWNLOAD).addIcon(Fa.GIFT).addIcon(Fa.PLAY).addIcon(Fa.MOUSE_POINTER)
                .addIcon(Fa.ARROWS).addIcon(Fa.CIRCLE_O).addIcon(Fa.CIRCLE).addIcon(Fa.MINUS).addIcon(Fa.CARET_DOWN)
                .addIcon(Fa.CARET_UP).addIcon(Fa.TIMES).addIcon(Fa.SORT).addIcon(Fa.HASHTAG).addIcon(Fa.PAINT_BRUSH)
                .addIcon(Fa.STAR).addIcon(Fa.REFRESH).addIcon(Fa.EXPAND).build();
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
        for (Field f : fields) {
            if (ReflectionUtils.hasFieldAnnotation(f, Inject.class)) {
                injectableFields.add(f);
                // Log.debug("DI: found injectable field: {}", f.getName());
            }
        }

        // inject
        try {
            for (Field f : injectableFields) {
                injectField(o, f);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * Looks at own static fields and injects value into object if found.
     *
     * @param o
     *            object, in which field should be injected
     * @param field
     *            the injectable field
     *
     * @throws IllegalAccessException
     */
    private static void injectField(Object o, Field field) throws IllegalAccessException {
        for (Field f : Mundus.class.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) && Modifier.isPrivate(f.getModifiers())) {
                if (f.getType().equals(field.getType())) {
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
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (goPicker != null) goPicker.dispose();
        if (modelBatch != null) modelBatch.dispose();
        if (shaders != null) shaders.dispose();
        if (toolManager != null) toolManager.dispose();
        if (commandHistory != null) commandHistory.clear();
        if (fa != null) fa.dispose();
        if (projectManager != null) projectManager.dispose();
    }

}
