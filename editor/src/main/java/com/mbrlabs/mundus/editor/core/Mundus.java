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

package com.mbrlabs.mundus.editor.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.editor.Main;
import com.mbrlabs.mundus.editor.assets.ModelImporter;
import com.mbrlabs.mundus.editor.core.kryo.KryoManager;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.input.FreeCamController;
import com.mbrlabs.mundus.editor.input.InputManager;
import com.mbrlabs.mundus.editor.input.ShortcutController;
import com.mbrlabs.mundus.editor.shader.Shaders;
import com.mbrlabs.mundus.editor.tools.ToolManager;
import com.mbrlabs.mundus.editor.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.editor.tools.picker.ToolHandlePicker;
import com.mbrlabs.mundus.editor.utils.Fa;
import com.mbrlabs.mundus.editor.utils.GLFWIconSetter;
import com.mbrlabs.mundus.editor.utils.ReflectionUtils;

import org.apache.commons.io.FilenameUtils;

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
            homeDir.mkdirs();
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
        commandHistory = new CommandHistory(CommandHistory.Companion.getDEFAULT_LIMIT());

        modelImporter = new ModelImporter(registry);
        projectManager = new ProjectManager(kryoManager, registry, shaders);
        toolManager = new ToolManager(input, projectManager, goPicker, handlePicker, modelBatch, shaders, shapeRenderer,
                commandHistory);
        shortcutController = new ShortcutController(registry, projectManager, commandHistory);
    }

    /**
     * Sets the application icon.
     */
    public static void setAppIcon() {
        File iconCache = new File(FilenameUtils.concat(Registry.HOME_DIR, "cache/"));
        if (!iconCache.exists()) {
            iconCache.mkdirs();
        }

        FileHandle cache = new FileHandle(iconCache);
        FileHandle iconIco = Gdx.files.internal("icon.ico");
        FileHandle iconPng = Gdx.files.internal("icon.png");
        GLFWIconSetter.newInstance().setIcon(cache, iconIco, iconPng);
    }

    private static void initStyle() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/OpenSans/OpenSans-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.kerning = true;
        params.borderStraight = false;
        params.genMipMaps = true;
        params.hinting = FreeTypeFontGenerator.Hinting.Full;

        // font norm
        params.size = 12;
        BitmapFont fontNorm = generator.generateFont(params);

        // font small
        params.size = 11;
        BitmapFont fontSmall = generator.generateFont(params);

        // font small
        params.size = 10;
        BitmapFont fontTiny = generator.generateFont(params);
        generator.dispose();

        // skin
        Skin skin = new Skin();
        skin.add("font-norm", fontNorm, BitmapFont.class);
        skin.add("font-small", fontSmall, BitmapFont.class);
        skin.add("font-tiny", fontTiny, BitmapFont.class);

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
        fa = faBuilder.addIcon(Fa.Companion.getSAVE()).addIcon(Fa.Companion.getDOWNLOAD())
                .addIcon(Fa.Companion.getGIFT()).addIcon(Fa.Companion.getPLAY())
                .addIcon(Fa.Companion.getMOUSE_POINTER()).addIcon(Fa.Companion.getARROWS())
                .addIcon(Fa.Companion.getCIRCLE_O()).addIcon(Fa.Companion.getCIRCLE()).addIcon(Fa.Companion.getMINUS())
                .addIcon(Fa.Companion.getCARET_DOWN()).addIcon(Fa.Companion.getCARET_UP())
                .addIcon(Fa.Companion.getTIMES()).addIcon(Fa.Companion.getSORT()).addIcon(Fa.Companion.getHASHTAG())
                .addIcon(Fa.Companion.getPAINT_BRUSH()).addIcon(Fa.Companion.getSTAR())
                .addIcon(Fa.Companion.getREFRESH()).addIcon(Fa.Companion.getEXPAND()).build();
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
