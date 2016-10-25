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

package com.mbrlabs.mundus.editor.exporter

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Json
import com.kotcrab.vis.ui.util.async.AsyncTask
import com.kotcrab.vis.ui.util.async.AsyncTaskListener
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.editor.core.kryo.KryoManager
import com.mbrlabs.mundus.editor.core.kryo.descriptors.SceneDescriptor
import com.mbrlabs.mundus.editor.core.project.ProjectContext
import org.apache.commons.io.FilenameUtils
import java.io.File

/**
 * @author Marcus Brummer
 * @version 26-10-2016
 */
class Exporter(val kryo: KryoManager, val project: ProjectContext) {

    /**
     *
     */
    fun exportAsync(outputFolder: FileHandle, listener: AsyncTaskListener) {
        val task = object: AsyncTask("export_${project.name}") {
            override fun doInBackground() {
                val assetManager = project.assetManager
                val step = 100f / (assetManager.assets.size + project.scenes.size)
                var progress = 0f

                // create folder structure
                createFolders(outputFolder)

                // copy assets
                val assetFolder = FileHandle(FilenameUtils.concat(outputFolder.path(), "assets/"))
                for(asset in assetManager.assets) {
                    exportAsset(asset, assetFolder)
                    progress += step
                    setProgressPercent(progress.toInt())
                }

                // load, convert & copy scenes
                for(sceneName in project.scenes) {
                    val scene = kryo.loadScene(project, sceneName)
                    exportScene(scene)
                    progress += step
                    setProgressPercent(progress.toInt())
                }
            }
        }

        task.addListener(listener)
        task.execute()
    }

    private fun createFolders(exportRootFolder: FileHandle) {
        // root/assets
        val assets = File(FilenameUtils.concat(exportRootFolder.path(), "assets/"))
        assets.mkdirs()

        // root/scenes
        val scenes = File(FilenameUtils.concat(exportRootFolder.path(), "scenes/"))
        scenes.mkdirs()
    }

    private fun exportAsset(asset: Asset, folder: FileHandle) {
        asset.file.copyTo(folder)
        asset.meta.file.copyTo(folder)
    }

    private fun exportScene(scene: SceneDescriptor) {
        val json = Json()
        // TODO implement
    }

}