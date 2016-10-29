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

package com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.editor.terrain.Terraformer
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.history.commands.TerrainHeightCommand
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.widgets.FileChooserField
import com.mbrlabs.mundus.editor.ui.widgets.FloatFieldWithLabel
import com.mbrlabs.mundus.editor.ui.widgets.IntegerFieldWithLabel
import com.mbrlabs.mundus.editor.utils.isImage

/**
 * @author Marcus Brummer
 * @version 04-03-2016
 */
class TerrainGenTab(private val parent: TerrainComponentWidget) : Tab(false, false) {
    private val root = VisTable()

    private val hmInput = FileChooserField()
    private val loadHeightMapBtn = VisTextButton("Load heightmap")

    private val perlinNoiseBtn = VisTextButton("Generate Perlin noise")
    private val perlinNoiseSeed = IntegerFieldWithLabel("Seed", -1, false)
    private val perlinNoiseMinHeight = FloatFieldWithLabel("Min height", -1, true)
    private val perlinNoiseMaxHeight = FloatFieldWithLabel("Max height", -1, true)

    private val history: CommandHistory = Mundus.inject()
    private val projectManager: ProjectManager = Mundus.inject()

    init {
        root.align(Align.left)

        root.add(VisLabel("Load Heightmap")).pad(5f).left().row()
        root.add(hmInput).left().expandX().fillX().row()
        root.add(loadHeightMapBtn).padLeft(5f).left().row()

        root.add(VisLabel("Perlin Noise")).pad(5f).padTop(10f).left().row()
        root.add(perlinNoiseSeed).pad(5f).left().fillX().expandX().row()
        root.add(perlinNoiseMinHeight).pad(5f).left().fillX().expandX().row()
        root.add(perlinNoiseMaxHeight).pad(5f).left().fillX().expandX().row()
        root.add(perlinNoiseBtn).pad(5f).left().row()

        setupListeners()
    }

    private fun setupListeners() {
        loadHeightMapBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val hm = hmInput.file
                if (hm != null && hm.exists() && isImage(hm)) {
                    loadHeightMap(hm)
                    projectManager.current().assetManager.addDirtyAsset(parent.component.terrain)
                } else {
                    Dialogs.showErrorDialog(UI, "Please select a heightmap image")
                }
            }
        })

        perlinNoiseBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val seed = perlinNoiseSeed.int
                val min = perlinNoiseMinHeight.float
                val max = perlinNoiseMaxHeight.float
                generatePerlinNoise(seed, min, max)
                projectManager.current().assetManager.addDirtyAsset(parent.component.terrain)
            }
        })
    }

    private fun loadHeightMap(heightMap: FileHandle) {
        val terrain = parent.component.terrain.terrain
        val command = TerrainHeightCommand(terrain)
        command.setHeightDataBefore(terrain.heightData)

        val originalMap = Pixmap(heightMap)

        // scale pixmap if it doesn't fit the terrainAsset
        if (originalMap.width != terrain.vertexResolution || originalMap.height != terrain.vertexResolution) {
            val scaledPixmap = Pixmap(terrain.vertexResolution, terrain.vertexResolution,
                    originalMap.format)
            scaledPixmap.drawPixmap(originalMap, 0, 0, originalMap.width, originalMap.height, 0, 0,
                    scaledPixmap.width, scaledPixmap.height)

            originalMap.dispose()
            Terraformer.heightMap(terrain).maxHeight(terrain.terrainWidth * 0.17f).map(scaledPixmap).terraform()
            scaledPixmap.dispose()
        } else {
            Terraformer.heightMap(terrain).maxHeight(terrain.terrainWidth * 0.17f).map(originalMap).terraform()
            originalMap.dispose()
        }

        command.setHeightDataAfter(terrain.heightData)
        history.add(command)
    }

    private fun generatePerlinNoise(seed: Int, min: Float, max: Float) {
        val terrain = parent.component.terrain.terrain
        val command = TerrainHeightCommand(terrain)
        command.setHeightDataBefore(terrain.heightData)

        Terraformer.perlin(terrain).minHeight(min).maxHeight(max).seed(seed.toLong()).terraform()

        command.setHeightDataAfter(terrain.heightData)
        history.add(command)
    }

    override fun getTabTitle(): String {
        return "Gen"
    }

    override fun getContentTable(): Table {
        return root
    }

}
