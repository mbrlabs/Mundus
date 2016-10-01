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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mbrlabs.mundus.commons.model.MModel;
import com.mbrlabs.mundus.commons.model.MTexture;
import com.mbrlabs.mundus.commons.utils.TextureUtils;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.core.registry.ProjectRef;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.utils.Log;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Marcus Brummer
 * @version 24-01-2016
 */
public class AssetManager implements Disposable {

    private static final String TAG = AssetManager.class.getSimpleName();

    private Array<Asset> assets;

    private FileHandle rootFolder;

    public AssetManager(String path) {
        this.assets = new Array<>();
        rootFolder = new FileHandle(path);
        if(!rootFolder.exists() || !rootFolder.isDirectory()) {
            Log.fatal("Root asset folder is not a directory");
        }
    }

    public void loadAssets() {
        // create meta file filter
        FileFilter metaFileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(MetaFile.META_EXTENSION);
            }
        };

        // load assets
        for(FileHandle meta : rootFolder.list(metaFileFilter)) {
            loadAsset(new MetaFile(meta));
        }
    }

    private void loadAsset(MetaFile meta) {
        // get handle to asset
        String assetPath = meta.getFile().pathWithoutExtension();
        FileHandle assetFile = new FileHandle(assetPath);

        // check if asset exists
        if(!assetFile.exists()) {
            Log.warnTag(TAG, "Meta file found, but asset does not exist: {}", meta.getFile().path());
            return;
        }

        // load & parse meta file
        boolean success = meta.load();
        if(!success) return;

        // load actual asset
        switch (meta.getType()) {
            case TEXTURE:
                loadTextureAsset(meta, assetFile);
                break;
            default:
                Log.warnTag(TAG, "Assets of type {} can't be loaded right now" , meta.getType());
                return;
        }
    }

    private void loadTextureAsset(MetaFile meta, FileHandle assetFile) {
        TextureAsset asset = new TextureAsset(meta, assetFile);
        asset.setTileable(true);
        asset.generateMipmaps(true);
        asset.load();
        assets.add(asset);

        Log.debugTag(TAG, "Loaded texture asset: {}" , asset.file.path());
    }

    /**
     *
     * @param importedModel
     * @return
     */
    public MModel importG3dbModel(ModelImporter.ImportedModel importedModel) {
//        long id = projectManager.current().obtainID();
//
//        String relativeImportFolder = ProjectManager.PROJECT_MODEL_DIR + id + "/";
//        String absoluteImportFolder = projectManager.current().path + "/" + relativeImportFolder;
//
//        String g3dbFilename = importedModel.name + ".g3db";
//        String textureFilename = importedModel.textureFile.name();
//
//        FileHandle absoluteG3dbImportPath = Gdx.files.absolute(absoluteImportFolder + g3dbFilename);
//        FileHandle absoluteTextureImportPath = Gdx.files.absolute(absoluteImportFolder + textureFilename);
//
//        importedModel.g3dbFile.copyTo(absoluteG3dbImportPath);
//        importedModel.textureFile.copyTo(absoluteTextureImportPath);
//
//        // load model
//        G3dModelLoader loader = new G3dModelLoader(new UBJsonReader());
//        Model model = loader.loadModel(absoluteG3dbImportPath);
//
//        // create model
//        MModel mModel = new MModel();
//        mModel.setModel(model);
//        mModel.name = importedModel.name;
//        mModel.id = id;
//        mModel.g3dbPath = relativeImportFolder + g3dbFilename;
//        mModel.texturePath = relativeImportFolder + textureFilename;
//        projectManager.current().models.add(mModel);
//
//        // save whole project
//        projectManager.saveCurrentProject();
//
//        return mModel;
        return null;
    }

    /**
     *
     * @param textureFile
     * @param mipMap
     * @return
     */
    public MTexture importTexture(FileHandle textureFile, boolean mipMap) {
//        long id = projectManager.current().obtainID();
//
//        String relativeImportPath = ProjectManager.PROJECT_TEXTURE_DIR + textureFile.name();
//        String absoluteImportPath = FilenameUtils.concat(projectManager.current().path, relativeImportPath);
//        FileHandle absoluteImportFile = Gdx.files.absolute(absoluteImportPath);
//
//        textureFile.copyTo(absoluteImportFile);
//
//        MTexture tex = new MTexture();
//        tex.setId(id);
//        tex.setPath(relativeImportPath);
//        if(mipMap) {
//            tex.texture = TextureUtils.loadMipmapTexture(absoluteImportFile, true);
//        } else {
//            tex.texture = new Texture(absoluteImportFile);
//        }
//
//        projectManager.current().textures.add(tex);
//
//        // save whole project
//        projectManager.saveCurrentProject();
//
//        return tex;

        return null;
    }

    @Override
    public void dispose() {
        for(Asset asset : assets) {
            asset.dispose();
        }
    }

}
