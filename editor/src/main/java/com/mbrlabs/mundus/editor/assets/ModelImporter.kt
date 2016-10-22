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

package com.mbrlabs.mundus.editor.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.mbrlabs.mundus.editor.core.Mundus
import com.mbrlabs.mundus.editor.core.registry.Registry
import com.mbrlabs.mundus.editor.events.SettingsChangedEvent
import com.mbrlabs.mundus.editor.utils.isCollada
import com.mbrlabs.mundus.editor.utils.isFBX
import com.mbrlabs.mundus.editor.utils.isG3DB
import com.mbrlabs.mundus.editor.utils.isWavefont
import org.apache.commons.io.FilenameUtils

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
class ModelImporter(private val registry: Registry) : SettingsChangedEvent.SettingsChangedListener {

    class ImportedModel {
        var name: String? = null
        var g3dbFile: FileHandle? = null
        var convResult: FbxConv.FbxConvResult? = null
    }

    private val fbxConv: FbxConv

    init {
        Mundus.registerEventListener(this)
        this.fbxConv = FbxConv(registry.settings.fbxConvBinary)
    }

    override fun onSettingsChanged(settingsChangedEvent: SettingsChangedEvent) {
        fbxConv.setFbxBinary(settingsChangedEvent.settings.fbxConvBinary)
    }

    fun importToTempFolder(modelFile: FileHandle?): ImportedModel? {
        if (modelFile == null || !modelFile.exists()) {
            return null
        }

        val imported = ImportedModel()
        val tempModelCache = registry.createTempFolder()

        // copy model file
        modelFile.copyTo(tempModelCache)
        val rawModelFile = Gdx.files.absolute(FilenameUtils.concat(tempModelCache.path(), modelFile.name()))
        if (!rawModelFile.exists()) {
            return null
        }

        // convert copied importer
        val convert = isFBX(rawModelFile) || isCollada(rawModelFile)
                || isWavefont(rawModelFile)

        if (convert) {
            fbxConv.clear()
            imported.convResult = fbxConv.input(rawModelFile.path()).output(tempModelCache.file().absolutePath).flipTexture(true).execute()

            if (imported.convResult!!.isSuccess) {
                imported.g3dbFile = Gdx.files.absolute(imported.convResult!!.outputFile)
            }
        } else if (isG3DB(rawModelFile)) {
            imported.g3dbFile = rawModelFile
        }

        // check if converted file exists
        if (imported.g3dbFile == null || !imported.g3dbFile!!.exists()) {
            return null
        }

        return imported
    }

}
