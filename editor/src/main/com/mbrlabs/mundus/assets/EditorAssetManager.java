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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.assets.AssetNotFoundException;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.MetaFile;
import com.mbrlabs.mundus.commons.assets.MetaFileParseException;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.assets.PixmapTextureAsset;
import com.mbrlabs.mundus.commons.assets.TerraAsset;
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.commons.model.MTexture;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.AssetImportEvent;
import com.mbrlabs.mundus.utils.Log;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Marcus Brummer
 * @version 24-01-2016
 */
public class EditorAssetManager extends AssetManager {

    private static final String TAG = EditorAssetManager.class.getSimpleName();

    /**
     *
     * @param assetsRoot
     */
    public EditorAssetManager(FileHandle assetsRoot) {
        super(assetsRoot);
        if(rootFolder != null && (!rootFolder.exists() || !rootFolder.isDirectory())) {
            Log.fatal(TAG, "Root asset folder is not a directory");
        }
    }

    /**
     *
     * @param asset
     * @param clazz
     * @return
     */
    public Asset importAsset(FileHandle asset, Class clazz) {

        // import asset
        Asset newAsset = null;
        try {
            if (clazz == TextureAsset.class) {
                newAsset = importTextureAsset(asset);
            } else if (clazz == PixmapTextureAsset.class) {
                newAsset = importPixmapTextureAsset(asset);
            } else if (clazz == TerraAsset.class) {
                newAsset = importTerraAsset(asset);
            } else if (clazz == ModelAsset.class) {
                newAsset = importModelAsset(asset);
            }
        } catch (IOException ioe) {
            Log.exception(TAG, ioe);
            return null;
        }

        // add to list
        if(newAsset != null) {
            assets.add(newAsset);
            assetIndex.put(newAsset.getUUID(), newAsset);
            Mundus.postEvent(new AssetImportEvent(newAsset));
        }

        return newAsset;
    }

    private MetaFile createMetaFileFromAsset(FileHandle assetFile, AssetType type) throws IOException {
        String metaName = assetFile.name() + "." + MetaFile.META_EXTENSION;
        String metaPath = FilenameUtils.concat(rootFolder.path(), metaName);
        return AssetHelper.createNewMetaFile(new FileHandle(metaPath), type);
    }

    private FileHandle copyToAssetFolder(FileHandle file) {
        FileHandle copy = new FileHandle(FilenameUtils.concat(rootFolder.path(), file.name()));
        file.copyTo(copy);
        return copy;
    }

    private TextureAsset importTextureAsset(FileHandle assetFile) throws IOException {
        MetaFile meta = createMetaFileFromAsset(assetFile, AssetType.TEXTURE);
        FileHandle importedAssetFile = copyToAssetFolder(assetFile);

        TextureAsset asset = new TextureAsset(meta, importedAssetFile);
        // TODO parse special texture properties and apply
        asset.load();

        return asset;
    }

    private TextureAsset importPixmapTextureAsset(FileHandle assetFile) throws IOException {
        MetaFile meta = createMetaFileFromAsset(assetFile, AssetType.PIXMAP_TEXTURE);
        // TODO implement
        return null;
    }

    private TextureAsset importTerraAsset(FileHandle assetFile) throws IOException {
        MetaFile meta = createMetaFileFromAsset(assetFile, AssetType.TERRA);
        // TODO implement
        return null;
    }

    private TextureAsset importModelAsset(FileHandle assetFile) throws IOException {
        MetaFile meta = createMetaFileFromAsset(assetFile, AssetType.MODEL);
        // TODO implement
        return null;
    }

    /**
     *
     * @param importedModel
     * @return
     */
    public ModelAsset importG3dbModel(ModelImporter.ImportedModel importedModel) {
//        long id = projectManager.current().obtainID();
//
//        String relativeImportFolder = ProjectManager.PROJECT_MODEL_DIR + id + "/";
//        String absoluteImportFolder = projectManager.current().path + "/" + relativeImportFolder;
//
//        String g3dbFilename = importedModel.name + ".g3db";
//        FileHandle absoluteG3dbImportPath = Gdx.files.absolute(absoluteImportFolder + g3dbFilename);
//        importedModel.g3dbFile.copyTo(absoluteG3dbImportPath);
//
//        // load model
//        MG3dModelLoader loader = new MG3dModelLoader(new UBJsonReader());
//        Model model = loader.loadModel(absoluteG3dbImportPath);
//
//        // create model
//        MModel mModel = new MModel();
//        mModel.setModel(model);
//        mModel.name = importedModel.name;
//        mModel.id = id;
//        mModel.g3dbPath = relativeImportFolder + g3dbFilename;
//        projectManager.current().models.add(mModel);
//
//        // save whole project
//        projectManager.saveCurrentProject();

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
