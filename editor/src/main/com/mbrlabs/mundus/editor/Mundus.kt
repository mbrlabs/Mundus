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

package com.mbrlabs.mundus.editor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.file.FileChooser
import com.mbrlabs.mundus.editor.assets.ModelImporter
import com.mbrlabs.mundus.editor.core.kryo.KryoManager
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.core.registry.Registry
import com.mbrlabs.mundus.editor.events.EventBus
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.input.FreeCamController
import com.mbrlabs.mundus.editor.input.InputManager
import com.mbrlabs.mundus.editor.input.ShortcutController
import com.mbrlabs.mundus.editor.shader.Shaders
import com.mbrlabs.mundus.editor.tools.ToolManager
import com.mbrlabs.mundus.editor.tools.picker.GameObjectPicker
import com.mbrlabs.mundus.editor.tools.picker.ToolHandlePicker
import com.mbrlabs.mundus.editor.utils.Fa
import com.mbrlabs.mundus.editor.utils.GLFWIconSetter
import ktx.inject.register
import org.apache.commons.io.FilenameUtils
import java.io.File

/**
 * Core class.
 *
 * @author Marcus Brummer
 * @version 08-12-2015
 */
object Mundus {

    val eventBus: EventBus

    var fa: BitmapFont? = null

    private val modelBatch: ModelBatch
    private val toolManager: ToolManager
    private val input: InputManager
    private val freeCamController: FreeCamController
    private val shortcutController: ShortcutController
    private val shapeRenderer: ShapeRenderer
    private val kryoManager: KryoManager
    private val projectManager: ProjectManager
    private val registry: Registry
    private val modelImporter: ModelImporter
    private val commandHistory: CommandHistory
    private val goPicker: GameObjectPicker
    private val handlePicker: ToolHandlePicker

    init {
        // create home dir
        val homeDir = File(Registry.HOME_DIR)
        if (!homeDir.exists()) {
            homeDir.mkdirs()
        }

        // init stuff
        initStyle()
        initFontAwesome()
        eventBus = EventBus()

        // DI
        shapeRenderer = ShapeRenderer()
        modelBatch = ModelBatch()
        input = InputManager()
        goPicker = GameObjectPicker()
        handlePicker = ToolHandlePicker()
        kryoManager = KryoManager()
        registry = kryoManager.loadRegistry()
        freeCamController = FreeCamController()
        commandHistory = CommandHistory(CommandHistory.DEFAULT_LIMIT)
        modelImporter = ModelImporter(registry)
        projectManager = ProjectManager(kryoManager, registry, modelBatch)
        toolManager = ToolManager(input, projectManager, goPicker, handlePicker, modelBatch, shapeRenderer,
                commandHistory)
        shortcutController = ShortcutController(registry, projectManager, commandHistory)

        register {
            bindSingleton(shapeRenderer)
            bindSingleton(modelBatch)
            bindSingleton(input)
            bindSingleton(goPicker)
            bindSingleton(handlePicker)
            bindSingleton(kryoManager)
            bindSingleton(registry)
            bindSingleton(commandHistory)
            bindSingleton(modelImporter)
            bindSingleton(projectManager)
            bindSingleton(toolManager)
            bindSingleton(shortcutController)
            bindSingleton(freeCamController)
        }
    }

    /**
     * Sets the application icon.
     */
    fun setAppIcon() {
        val iconCache = File(FilenameUtils.concat(Registry.HOME_DIR, "cache/"))
        if (!iconCache.exists()) {
            iconCache.mkdirs()
        }

        val cache = FileHandle(iconCache)
        val iconIco = Gdx.files.internal("icon.ico")
        val iconPng = Gdx.files.internal("icon.png")
        GLFWIconSetter.newInstance().setIcon(cache, iconIco, iconPng)
    }

    /**
     *
     */
    private fun initStyle() {
        val generator = FreeTypeFontGenerator(
                Gdx.files.internal("fonts/OpenSans/OpenSans-Regular.ttf"))
        val params = FreeTypeFontGenerator.FreeTypeFontParameter()
        params.kerning = true
        params.borderStraight = false
        params.genMipMaps = true
        params.hinting = FreeTypeFontGenerator.Hinting.Full

        // font norm
        params.size = 12
        val fontNorm = generator.generateFont(params)

        // font small
        params.size = 11
        val fontSmall = generator.generateFont(params)

        // font small
        params.size = 10
        val fontTiny = generator.generateFont(params)
        generator.dispose()

        // skin
        val skin = Skin()
        skin.add("font-norm", fontNorm, BitmapFont::class.java)
        skin.add("font-small", fontSmall, BitmapFont::class.java)
        skin.add("font-tiny", fontTiny, BitmapFont::class.java)

        skin.addRegions(TextureAtlas(Gdx.files.internal("ui/skin/uiskin.atlas")))
        skin.load(Gdx.files.internal("ui/skin/uiskin.json"))
        VisUI.load(skin)

        FileChooser.setFavoritesPrefsName("com.mbrlabs.mundus.editor")
    }

    /**
     *
     */
    private fun initFontAwesome() {
        val faBuilder = Fa(Gdx.files.internal("fonts/fa45.ttf"))
        faBuilder.generatorParameter!!.size = (Gdx.graphics.height * 0.02f).toInt()
        faBuilder.generatorParameter!!.kerning = true
        faBuilder.generatorParameter!!.borderStraight = false
        fa = faBuilder.addIcon(Fa.SAVE).addIcon(Fa.DOWNLOAD).addIcon(Fa.GIFT).
                addIcon(Fa.PLAY).addIcon(Fa.MOUSE_POINTER).addIcon(Fa.ARROWS).
                addIcon(Fa.CIRCLE_O).addIcon(Fa.CIRCLE).addIcon(Fa.MINUS).addIcon(Fa.CARET_DOWN).
                addIcon(Fa.CARET_UP).addIcon(Fa.TIMES).addIcon(Fa.SORT).addIcon(Fa.HASHTAG).
                addIcon(Fa.PAINT_BRUSH).addIcon(Fa.STAR).addIcon(Fa.REFRESH).addIcon(Fa.EXPAND).build()
    }

    /**
     *
     */
    inline fun <reified Type : Any> inject(): Type = ktx.inject.inject()

    /**
     *
     */
    fun postEvent(event: Any) {
        eventBus.post(event)
    }

    /**
     *
     */
    fun registerEventListener(listener: Any) {
        eventBus.register(listener)
    }

    /**
     *
     */
    fun unregisterEventListener(listener: Any) {
        eventBus.unregister(listener)
    }

    /**
     * Disposes everything.
     */
    fun dispose() {
        VisUI.dispose()
        Shaders.dispose()
        fa!!.dispose()
        shapeRenderer.dispose()
        modelBatch.dispose()
        goPicker.dispose()
        handlePicker.dispose()
    }

}
