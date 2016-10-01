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

package com.mbrlabs.mundus.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.registry.Registry;
import com.mbrlabs.mundus.events.SettingsChangedEvent;
import com.mbrlabs.mundus.utils.FbxConv;
import com.mbrlabs.mundus.utils.FileFormatUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class ModelImporter implements SettingsChangedEvent.SettingsChangedListener {

    public static class ImportedModel {
        public String name;
        public FileHandle g3dbFile;
        public FileHandle textureFile;
        public FbxConv.FbxConvResult convResult;
    }

    private FbxConv fbxConv;
    private Registry registry;

    public ModelImporter(Registry registry) {
        Mundus.registerEventListener(this);
        this.fbxConv = new FbxConv(registry.getSettings().getFbxConvBinary());
        this.registry = registry;
    }

    @Override
    public void onSettingsChanged(SettingsChangedEvent settingsChangedEvent) {
        fbxConv.setFbxBinary(settingsChangedEvent.getSettings().getFbxConvBinary());
    }

    public ImportedModel importToTempFolder(FileHandle modelFile, FileHandle textureFile) {
        if(modelFile == null || !modelFile.exists()) {
            return null;
        }
        if(textureFile == null || !textureFile.exists()) {
            return null;
        }

        ImportedModel imported = new ImportedModel();
        FileHandle tempModelCache = registry.createTempFolder();

        // copy texture file to temp folder
        textureFile.copyTo(tempModelCache);
        imported.textureFile = Gdx.files.absolute(FilenameUtils.concat(tempModelCache.path(), textureFile.name()));

        // check if copied texture file exists
        if(!imported.textureFile.exists()) {
            return null;
        }

        // copy model file
        modelFile.copyTo(tempModelCache);
        FileHandle rawModelFile = Gdx.files.absolute(FilenameUtils.concat(tempModelCache.path(), modelFile.name()));
        if(!rawModelFile.exists()) {
            return null;
        }

        // convert copied importer
        boolean convert = FileFormatUtils.isFBX(rawModelFile)
                || FileFormatUtils.isCollada(rawModelFile)
                || FileFormatUtils.isWavefont(rawModelFile);

        if(convert) {
            fbxConv.clear();
            imported.convResult = fbxConv.input(rawModelFile.path())
                    .output(tempModelCache.file().getAbsolutePath())
                    .flipTexture(true)
                    .execute();

            if(imported.convResult.isSuccess()) {
                imported.g3dbFile = Gdx.files.absolute(imported.convResult.getOutputFile());
            }
        } else if(FileFormatUtils.isG3DB(rawModelFile)) {
            imported.g3dbFile = rawModelFile;
        }

        // check if converted file exists
        if(imported.g3dbFile == null || !imported.g3dbFile.exists()) {
            return null;
        }

        return imported;
    }

}
