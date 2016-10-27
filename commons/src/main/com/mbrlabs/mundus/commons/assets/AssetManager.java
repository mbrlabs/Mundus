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
package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.assets.meta.MetaFileParseException;
import com.mbrlabs.mundus.commons.assets.meta.MetaLoader;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

/**
 * Read-only asset manager.
 *
 * @author Marcus Brummer
 * @version 06-10-2016
 */
public class AssetManager implements Disposable {

    private static final String TAG = AssetManager.class.getSimpleName();

    protected FileHandle rootFolder;

    protected Array<Asset> assets;
    protected Map<String, Asset> assetIndex;

    /**
     * Asset manager constructor.
     *
     * @param assetsFolder
     *            root directory of assets
     */
    public AssetManager(FileHandle assetsFolder) {
        this.rootFolder = assetsFolder;
        this.assets = new Array<Asset>();
        this.assetIndex = new HashMap<String, Asset>();
    }

    /**
     * Returns an asset by id.
     *
     * @param id
     *            id of asset
     * @return matching asset or null
     */
    public Asset findAssetByID(String id) {
        if (id == null) return null;
        return assetIndex.get(id);
    }

    public Map<String, Asset> getAssetMap() {
        return assetIndex;
    }

    public void addAsset(Asset asset) {
        if (asset == null) return;
        if (assetIndex.get(asset.getID()) == null) {
            assets.add(asset);
            assetIndex.put(asset.getID(), asset);
        }
    }

    /**
     * Returns all assets.
     *
     * @return all assets
     */
    public Array<Asset> getAssets() {
        return assets;
    }

    /**
     * Returns all assets of type MODEL.
     *
     * @return all model assets
     */
    public Array<ModelAsset> getModelAssets() {
        Array<ModelAsset> models = new Array<ModelAsset>();
        for (Asset asset : assets) {
            if (asset instanceof ModelAsset) {
                models.add((ModelAsset) asset);
            }
        }

        return models;
    }

    /**
     * Returns all assets of type TERRAIN.
     *
     * @return all model assets
     */
    public Array<TerrainAsset> getTerrainAssets() {
        Array<TerrainAsset> terrains = new Array<TerrainAsset>();
        for (Asset asset : assets) {
            if (asset instanceof TerrainAsset) {
                terrains.add((TerrainAsset) asset);
            }
        }

        return terrains;
    }

    /**
     * Returns all assets of type MATERIAL.
     *
     * @return all model assets
     */
    public Array<MaterialAsset> getMaterialAssets() {
        Array<MaterialAsset> materials = new Array<MaterialAsset>();
        for (Asset asset : assets) {
            if (asset instanceof MaterialAsset) {
                materials.add((MaterialAsset) asset);
            }
        }

        return materials;
    }

    /**
     * Loads all assets in the project's asset folder.
     *
     * @param listener
     *            informs about current loading progress
     * @throws AssetNotFoundException
     *             if a meta file points to a non existing asset
     * @throws MetaFileParseException
     *             if a meta file can't be parsed
     */
    public void loadAssets(AssetLoadingListener listener) throws AssetNotFoundException, MetaFileParseException {
        // create meta file filter
        FileFilter metaFileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(Meta.META_EXTENSION);
            }
        };

        final MetaLoader metaLoader = new MetaLoader();

        // load assets
        FileHandle[] metaFiles = rootFolder.list(metaFileFilter);
        for (FileHandle meta : metaFiles) {
            Asset asset = loadAsset(metaLoader.load(meta));
            if(listener != null) {
                listener.onLoad(asset, assets.size, metaFiles.length);
            }
        }

        // resolve material assets
        for (Asset asset : assets) {
            if (asset instanceof MaterialAsset) {
                asset.resolveDependencies(assetIndex);
                asset.applyDependencies();
            }
        }

        // resolve other assets
        for (Asset asset : assets) {
            if (asset instanceof MaterialAsset) continue;
            asset.resolveDependencies(assetIndex);
            asset.applyDependencies();
        }

        if(listener != null) {
            listener.onFinish(assets.size);
        }
    }

    /**
     * Loads an asset, given it's meta file.
     *
     * @param meta
     *            meta file of asset
     * @return asset or null
     * @throws AssetNotFoundException
     *             if a meta file points to a non existing asset
     * @throws MetaFileParseException
     *             if a meta file can't be parsed
     */
    public Asset loadAsset(Meta meta) throws MetaFileParseException, AssetNotFoundException {
        // get handle to asset
     //   String assetPath = meta.getFile().pathWithoutExtension();
        FileHandle assetFile = meta.getFile().sibling(meta.getFile().nameWithoutExtension());

        // check if asset exists
        if (!assetFile.exists()) {
            throw new AssetNotFoundException("Meta file found, but asset does not exist: " + meta.getFile().path());
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
        case TERRAIN:
            asset = loadTerrainAsset(meta, assetFile);
            break;
        case MODEL:
            asset = loadModelAsset(meta, assetFile);
            break;
        case MATERIAL:
            asset = loadMaterialAsset(meta, assetFile);
            break;
        default:
            return null;
        }

        addAsset(asset);
        return asset;
    }

    private MaterialAsset loadMaterialAsset(Meta meta, FileHandle assetFile) {
        MaterialAsset asset = new MaterialAsset(meta, assetFile);
        asset.load();
        return asset;
    }

    private TextureAsset loadTextureAsset(Meta meta, FileHandle assetFile) {
        TextureAsset asset = new TextureAsset(meta, assetFile);
        // TODO parse special texture instead of always setting them
        asset.setTileable(true);
        asset.generateMipmaps(true);
        asset.load();
        return asset;
    }

    private TerrainAsset loadTerrainAsset(Meta meta, FileHandle assetFile) {
        TerrainAsset asset = new TerrainAsset(meta, assetFile);
        asset.load();
        return asset;
    }

    private PixmapTextureAsset loadPixmapTextureAsset(Meta meta, FileHandle assetFile) {
        PixmapTextureAsset asset = new PixmapTextureAsset(meta, assetFile);
        asset.load();
        return asset;
    }

    private ModelAsset loadModelAsset(Meta meta, FileHandle assetFile) {
        ModelAsset asset = new ModelAsset(meta, assetFile);
        asset.load();
        return asset;
    }

    @Override
    public void dispose() {
        for (Asset asset : assets) {
            asset.dispose();
            Gdx.app.log(TAG, "Disposing asset: " + asset.toString());
        }
        assets.clear();
        assetIndex.clear();
    }

    /**
     * Used to inform users about the current loading status.
     */
    public interface AssetLoadingListener {
        /**
         * Called if an asset loaded
         * 
         * @param asset
         *            loaded asset
         * @param progress
         *            number of already loaded assets
         * @param assetCount
         *            total number of assets
         */
        void onLoad(Asset asset, int progress, int assetCount);

        /**
         * Called if all assets loaded.
         * 
         * @param assetCount
         *            total number of loaded assets
         */
        void onFinish(int assetCount);
    }

}
