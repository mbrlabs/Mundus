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

package com.mbrlabs.mundus.editor.ui.modules.dialogs.importer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.UBJsonReader
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.commons.assets.ModelAsset
import com.mbrlabs.mundus.commons.assets.meta.MetaModel
import com.mbrlabs.mundus.commons.g3d.MG3dModelLoader
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.assets.AssetAlreadyExistsException
import com.mbrlabs.mundus.editor.assets.MetaSaver
import com.mbrlabs.mundus.editor.assets.ModelImporter
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.AssetImportEvent
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.dialogs.BaseDialog
import com.mbrlabs.mundus.editor.ui.widgets.FileChooserField
import com.mbrlabs.mundus.editor.ui.widgets.RenderWidget
import com.mbrlabs.mundus.editor.utils.Log
import com.mbrlabs.mundus.editor.utils.isCollada
import com.mbrlabs.mundus.editor.utils.isFBX
import com.mbrlabs.mundus.editor.utils.isWavefont
import java.io.IOException

/**
 * @author Marcus Brummer
 * @version 07-06-2016
 */
class ImportModelDialog : BaseDialog("Import Mesh"), Disposable {

    companion object {
        private val TAG = ImportModelDialog::class.java.simpleName
    }

    private val importMeshTable: ImportModelTable

    private val modelImporter: ModelImporter = Mundus.inject()
    private val projectManager: ProjectManager = Mundus.inject()

    init {
        isModal = true
        isMovable = true

        val root = VisTable()
        add<Table>(root).expand().fill()
        importMeshTable = ImportModelTable()

        root.add(importMeshTable).minWidth(600f).expand().fill().left().top()
    }

    override fun dispose() {
        importMeshTable.dispose()
    }

    /**
     */
    private inner class ImportModelTable : VisTable(), Disposable {
        // UI elements
        private var renderWidget: RenderWidget? = null
        private val importBtn = VisTextButton("IMPORT")
        private val modelInput = FileChooserField(300)

        // preview model + instance
        private var previewModel: Model? = null
        private var previewInstance: ModelInstance? = null

        private var importedModel: ModelImporter.ImportedModel? = null

        private val modelBatch: ModelBatch
        private val cam: PerspectiveCamera
        private val env: Environment

        init {
            modelBatch = ModelBatch()

            cam = PerspectiveCamera()
            cam.position.set(0f, 5f, 0f)
            cam.lookAt(0f, 0f, 0f)
            cam.near = 0.1f
            cam.far = 100f
            cam.update()

            env = Environment()
            env.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
            env.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

            this.setupUI()
            this.setupListener()
        }

        private fun setupUI() {
            val root = Table()
            // root.debugAll();
            root.padTop(6f).padRight(6f).padBottom(22f)
            add(root)

            val inputTable = VisTable()
            renderWidget = RenderWidget(cam)
            renderWidget!!.setRenderer { camera ->
                if (previewInstance != null) {
                    Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT)
                    previewInstance!!.transform.rotate(0f, 0f, 1f, -1f)
                    modelBatch.begin(camera)
                    modelBatch.render(previewInstance!!, env)
                    modelBatch.end()
                }
            }

            root.add(inputTable).width(300f).height(300f).padRight(10f)
            root.add<RenderWidget>(renderWidget).width(300f).height(300f).expand().fill()

            inputTable.left().top()
            inputTable.add(VisLabel("Model File")).left().padBottom(5f).row()
            inputTable.add(modelInput).fillX().expandX().padBottom(10f).row()
            inputTable.add(importBtn).fillX().expand().bottom()

            modelInput.setEditable(false)
        }

        private fun setupListener() {

            // model chooser
            modelInput.setCallback { fileHandle ->
                if (fileHandle.exists()) {
                    loadAndShowPreview(modelInput.file)
                }
            }

            // import btn
            importBtn.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (previewModel != null && previewInstance != null) {
                        try {
                            val modelAsset = importModel()
                            Mundus.postEvent(AssetImportEvent(modelAsset))
                            UI.toaster.success("Mesh imported")
                        } catch (e: IOException) {
                            e.printStackTrace()
                            UI.toaster.error("Error while creating a ModelAsset")
                        } catch (ee: AssetAlreadyExistsException) {
                            Log.exception(TAG, ee)
                            UI.toaster.error("Error: There already exists a model with the same name")
                        }

                        dispose()
                        close()
                    } else {
                        UI.toaster.error("There is nothing to import")
                    }
                }
            })
        }

        @Throws(IOException::class, AssetAlreadyExistsException::class)
        private fun importModel(): ModelAsset {

            // create model asset
            val assetManager = projectManager.current().assetManager
            val modelAsset = assetManager.createModelAsset(importedModel!!)

            // create materials
            modelAsset.meta.model = MetaModel()
            for (mat in modelAsset.model.materials) {
                val materialAsset = assetManager.createMaterialAsset(modelAsset.id.substring(0, 4) + "_" + mat.id)
                modelAsset.meta.model.defaultMaterials.put(mat.id, materialAsset.id)
                modelAsset.defaultMaterials.put(mat.id, materialAsset)
            }

            // save meta file
            val saver = MetaSaver()
            saver.save(modelAsset.meta)

            modelAsset.applyDependencies()

            return modelAsset
        }

        private fun loadAndShowPreview(model: FileHandle) {
            this.importedModel = modelImporter.importToTempFolder(model)

            if (importedModel == null) {
                if (isCollada(model) || isFBX(model)
                        || isWavefont(model)) {
                    Dialogs.showErrorDialog(stage, "Import error\nPlease make sure you specified the right "
                            + "files & have set the correct fbc-conv binary in the settings menu.")
                } else {
                    Dialogs.showErrorDialog(stage, "Import error\nPlease make sure you specified the right files")
                }
            }

            // load and show preview
            if (importedModel != null) {
                try {
                    previewModel = MG3dModelLoader(UBJsonReader()).loadModel(importedModel!!.g3dbFile)
                    previewInstance = ModelInstance(previewModel!!)
                    showPreview()
                } catch (e: GdxRuntimeException) {
                    Dialogs.showErrorDialog(stage, e.message)
                }

            }
        }

        private fun showPreview() {
            previewInstance = ModelInstance(previewModel!!)

            // scale to 2 open gl units
            val boundingBox = previewInstance!!.calculateBoundingBox(BoundingBox())
            val max = boundingBox.getMax(Vector3())
            var maxDim = 0f
            if (max.x > maxDim) maxDim = max.x
            if (max.y > maxDim) maxDim = max.y
            if (max.z > maxDim) maxDim = max.z
            previewInstance!!.transform.scl(2f / maxDim)
        }

        override fun dispose() {
            if (previewModel != null) {
                previewModel!!.dispose()
                previewModel = null
                previewInstance = null
            }
            modelInput.clear()
        }
    }

}
