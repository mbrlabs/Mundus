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
import com.kotcrab.vis.ui.widget.file.FileUtils;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.MetaFile;
import com.mbrlabs.mundus.commons.assets.MetaFileParseException;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.assets.PixmapTextureAsset;
import com.mbrlabs.mundus.commons.assets.TerraAsset;
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.commons.model.MModel;
import com.mbrlabs.mundus.commons.model.MTexture;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.events.AssetImportEvent;
import com.mbrlabs.mundus.utils.Log;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * @author Marcus Brummer
 * @version 24-01-2016
 */
public class AssetManager implements Disposable {

    private static final String TAG = AssetManager.class.getSimpleName();

    private Array<Asset> assets;

    private FileHandle rootFolder;

    /**
     *
     * @param path
     */
    public AssetManager(String path) {
        this.assets = new Array<>();
        rootFolder = new FileHandle(path);
        if(!rootFolder.exists() || !rootFolder.isDirectory()) {
            Log.fatal(TAG, "Root asset folder is not a directory");
        }
    }

    /**
     * Loads all imported assets in the project's asset folder.
     */
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
            Mundus.postEvent(new AssetImportEvent(newAsset));
            // TODO log msg
        }

        return newAsset;
    }

    public Array<Asset> getAssets() {
        return assets;
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

    private void loadAsset(MetaFile meta) {
        // get handle to asset
        String assetPath = meta.getFile().pathWithoutExtension();
        FileHandle assetFile = new FileHandle(assetPath);

        // check if asset exists
        if(!assetFile.exists()) {
            Log.warn(TAG, "Meta file found, but asset does not exist: {}", meta.getFile().path());
            return;
        }

        // load & parse meta file
        try {
            meta.load();
        } catch (MetaFileParseException e) {
            Log.error(TAG, "Error while parsing meta file: {}", meta.getFile().path());
            return;
        }

        // load actual asset
        Asset asset = null;
        switch (meta.getType()) {
            case TEXTURE:
                asset = loadTextureAsset(meta, assetFile);
                break;
            case PIXMAP_TEXTURE:
                asset = loadPixmapTextureAsset(meta, assetFile);
                break;
            case TERRA:
                asset = loadTerraAsset(meta, assetFile);
                break;
            default:
                Log.warn(TAG, "Assets of type {} can't be loaded right now" , meta.getType());
                return;
        }

        // add to list
        if(asset != null) {
            assets.add(asset);
        }
    }

    private TextureAsset loadTextureAsset(MetaFile meta, FileHandle assetFile) {
        TextureAsset asset = new TextureAsset(meta, assetFile);
        asset.setTileable(true);
        asset.generateMipmaps(true);
        asset.load();
        Log.debug(TAG, "Loaded texture asset: {}" , asset.getFile().path());

        return asset;
    }

    private TerraAsset loadTerraAsset(MetaFile meta, FileHandle assetFile) {
        TerraAsset asset = new TerraAsset(meta, assetFile);
        asset.load();
        Log.debug(TAG, "Loaded terra asset: {}" , asset.getFile().path());

        return asset;
    }

    private PixmapTextureAsset loadPixmapTextureAsset(MetaFile meta, FileHandle assetFile) {
        PixmapTextureAsset asset = new PixmapTextureAsset(meta, assetFile);
        asset.load();
        Log.debug(TAG, "Loaded pixmap texture asset: {}" , asset.getFile().path());

        return asset;
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
