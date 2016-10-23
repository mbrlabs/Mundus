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

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.MaterialAsset
import com.mbrlabs.mundus.commons.assets.TextureAsset
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.assets.AssetMaterialFilter
import com.mbrlabs.mundus.editor.assets.AssetTextureFilter
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.scene3d.components.ModelComponent
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetSelectionDialog

/**
 * @author Marcus Brummer
 * *
 * @version 13-10-2016
 */
class MaterialWidget(private val changedListener: MaterialWidget.MaterialChangedListener?) : VisTable() {

    private var assetSelectionDialog: AssetSelectionDialog? = null
    private var materialFilter: AssetMaterialFilter? = null
    private var materialChangeBtn: VisTextButton? = null
    private var assetSelectionListener: AssetSelectionDialog.AssetSelectionListener? = null

    private val label: VisLabel
    private val diffuseColorField: ColorPickerField
    private val diffuseAssetField: AssetSelectionField

    var material: MaterialAsset? = null
        set(material) {
            if (material != null) {
                field = material
                diffuseColorField.color = material.diffuseColor
                diffuseAssetField.setAsset(material.diffuseTexture)
                label.setText(material.name)
            }
        }

    private val projectManager: ProjectManager = Mundus.inject()

    init {
        align(Align.topLeft)
        label = VisLabel()
        if (changedListener != null) {
            assetSelectionDialog = AssetSelectionDialog()
            materialFilter = AssetMaterialFilter()
            materialChangeBtn = VisTextButton("change")

            assetSelectionListener = object: AssetSelectionDialog.AssetSelectionListener {
                override fun onSelected(asset: Asset?) {
                    material = asset as MaterialAsset
                    changedListener.materialChanged(asset)
                }
            }
        }
        diffuseAssetField = AssetSelectionField()
        diffuseColorField = ColorPickerField()

        label.setWrap(true)

        if (changedListener != null) {
            val table = VisTable()
            table.add(label).grow()
            table.add<VisTextButton>(materialChangeBtn).right().row()
            add(table).grow().row()
        } else {
            add(label).grow().row()
        }
        addSeparator().growX().row()
        add(VisLabel("Diffuse texture")).grow().row()
        add(diffuseAssetField).growX().row()
        add(VisLabel("Diffuse color")).grow().row()
        add(diffuseColorField).growX().row()

        setupWidgets()
    }

    private fun setupWidgets() {
        if (materialChangeBtn != null) {
            materialChangeBtn!!.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    assetSelectionDialog!!.show(false, materialFilter!!, assetSelectionListener!!)
                }
            })
        }

        // diffuse texture
        diffuseAssetField.setFilter(AssetTextureFilter())
        diffuseAssetField.setListener(object: AssetSelectionDialog.AssetSelectionListener {
            override fun onSelected(asset: Asset?) {
                material!!.diffuseTexture = asset as TextureAsset
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addDirtyAsset(material!!)
            }
        })

        // diffuse color
        diffuseColorField.setCallback { color ->
            this.material!!.diffuseColor = color
            applyMaterialToModelAssets()
            applyMaterialToModelComponents()
            projectManager.current().assetManager.addDirtyAsset(this.material!!)
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

     */
    interface MaterialChangedListener {
        fun materialChanged(materialAsset: MaterialAsset)
    }


}
