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

package com.mbrlabs.mundus.editor.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.MaterialAsset
import com.mbrlabs.mundus.commons.assets.TextureAsset
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.assets.AssetMaterialFilter
import com.mbrlabs.mundus.editor.assets.AssetTextureFilter
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog

/**
 * Displays all properties of a material.
 *
 * You can also edit materials and replace them with another materials by.
 *
 * @author Marcus Brummer
 * @version 13-10-2016
 */
class MaterialWidget : VisTable() {

    private val matFilter: AssetMaterialFilter = AssetMaterialFilter()
    private val matChangedBtn: VisTextButton = VisTextButton("change")
    private val matPickerListener: AssetPickerDialog.AssetPickerListener

    private val matNameLabel: VisLabel = VisLabel()
    private val diffuseColorField: ColorPickerField = ColorPickerField()
    private val diffuseAssetField: AssetSelectionField = AssetSelectionField()

    private val projectManager: ProjectManager = Mundus.inject()

    /**
     * The currently active material of the widget.
     */
    var material: MaterialAsset? = null
        set(value) {
            if (value != null) {
                field = value
                diffuseColorField.selectedColor = value.diffuseColor
                diffuseAssetField.setAsset(value.diffuseTexture)
                matNameLabel.setText(value.name)
            }
        }

    /**
     * An optional listener for changing the material. If the property is null
     * the user will not be able to change the material.
     */
    var matChangedListener: MaterialWidget.MaterialChangedListener? = null
        set(value) {
            field = value
            matChangedBtn.touchable = if(value == null) Touchable.disabled else Touchable.enabled
        }

    init {
        align(Align.topLeft)
        matNameLabel.setWrap(true)

        matPickerListener = object: AssetPickerDialog.AssetPickerListener {
            override fun onSelected(asset: Asset?) {
                material = asset as? MaterialAsset
                matChangedListener?.materialChanged(material!!)
            }
        }

        setupWidgets()
    }

    private fun setupWidgets() {
        val table = VisTable()
        table.add(matNameLabel).grow()
        table.add<VisTextButton>(matChangedBtn).padLeft(4f).right().row()
        add(table).grow().row()

        addSeparator().growX().row()

        add(VisLabel("Diffuse texture")).grow().row()
        add(diffuseAssetField).growX().row()
        add(VisLabel("Diffuse color")).grow().row()
        add(diffuseColorField).growX().row()

        matChangedBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                UI.assetSelectionDialog.show(false, matFilter, matPickerListener)
            }
        })

        // diffuse texture
        diffuseAssetField.assetFilter = AssetTextureFilter()
        diffuseAssetField.pickerListener = object: AssetPickerDialog.AssetPickerListener {
            override fun onSelected(asset: Asset?) {
                material?.diffuseTexture = asset as? TextureAsset
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addDirtyAsset(material!!)
            }
        }

        // diffuse color
        diffuseColorField.colorAdapter = object: ColorPickerAdapter() {
            override fun finished(newColor: Color) {
                material?.diffuseColor?.set(newColor)
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addDirtyAsset(material!!)
            }
        }

    }

    // TODO find better solution than iterating through all components
    private fun applyMaterialToModelComponents() {
        val sceneGraph = projectManager.current().currScene.sceneGraph
        for (go in sceneGraph.gameObjects) {
            val mc = go.findComponentByType(Component.Type.MODEL)
            if (mc != null && mc is ModelComponent) {
                mc.applyMaterials()
            }
        }
    }

    // TODO find better solution than iterating through all assets
    private fun applyMaterialToModelAssets() {
        val assetManager = projectManager.current().assetManager
        for (modelAsset in assetManager.modelAssets) {
            modelAsset.applyDependencies()
        }
    }

    /**
     *
     */
    interface MaterialChangedListener {
        fun materialChanged(materialAsset: MaterialAsset)
    }


}
