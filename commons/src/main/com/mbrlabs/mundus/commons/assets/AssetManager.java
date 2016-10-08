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

    public void addAsset(Asset asset) {
        if (asset == null) return;
        if (assetIndex.get(asset.getUUID()) == null) {
            assets.add(asset);
            assetIndex.put(asset.getUUID(), asset);
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
                return file.getName().endsWith(MetaFile.META_EXTENSION);
            }
        };

        // load assets
        FileHandle[] metaFiles = rootFolder.list(metaFileFilter);
        for (FileHandle meta : metaFiles) {
            Asset asset = loadAsset(new MetaFile(meta));
            listener.onLoad(asset, assets.size, metaFiles.length);
        }

        // resolve dependencies
        resolveAssetDependencies();

        listener.onFinish(assets.size);
    }

    private void resolveAssetDependencies() {
        for (Asset asset : assets) {
            MetaFile meta = asset.getMeta();

            // model asset
            if (asset instanceof ModelAsset) {
                String diffuseTexture = meta.getDiffuseTexture();
                if (diffuseTexture != null) {
                    TextureAsset tex = (TextureAsset) findAssetByID(diffuseTexture);
                    if (tex != null) {
                        // Log.error(TAG, diffuseTexture);
                        ((ModelAsset) asset).setDiffuseTexture(tex);
                    }
                }
            }

            // terrain asset
            if (asset instanceof TerrainAsset) {
                TerrainAsset terrain = (TerrainAsset) asset;
                terrain.setSplatmap((PixmapTextureAsset) findAssetByID(meta.getTerrainSplatmap()));
                terrain.setSplatBase((TextureAsset) findAssetByID(meta.getTerrainSplatBase()));
                terrain.setSplatR((TextureAsset) findAssetByID(meta.getTerrainSplatR()));
                terrain.setSplatG((TextureAsset) findAssetByID(meta.getTerrainSplatG()));
                terrain.setSplatB((TextureAsset) findAssetByID(meta.getTerrainSplatB()));
                terrain.setSplatA((TextureAsset) findAssetByID(meta.getTerrainSplatA()));
            }

            asset.applyDependencies();
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
    public Asset loadAsset(MetaFile meta) throws MetaFileParseException, AssetNotFoundException {
        // get handle to asset
        String assetPath = meta.getFile().pathWithoutExtension();
        FileHandle assetFile = new FileHandle(assetPath);

        // check if asset exists
        if (!assetFile.exists()) {
            throw new AssetNotFoundException("Meta file found, but asset does not exist: " + meta.getFile().path());
        }

        meta.load();

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
        default:
            return null;
        }

        addAsset(asset);
        return asset;
    }

    private TextureAsset loadTextureAsset(MetaFile meta, FileHandle assetFile) {
        TextureAsset asset = new TextureAsset(meta, assetFile);
        asset.setTileable(true);
        asset.generateMipmaps(true);
        asset.load();
        return asset;
    }

    private TerrainAsset loadTerrainAsset(MetaFile meta, FileHandle assetFile) {
        TerrainAsset asset = new TerrainAsset(meta, assetFile);
        asset.load();
        return asset;
    }

    private PixmapTextureAsset loadPixmapTextureAsset(MetaFile meta, FileHandle assetFile) {
        PixmapTextureAsset asset = new PixmapTextureAsset(meta, assetFile);
        asset.load();
        return asset;
    }

    private ModelAsset loadModelAsset(MetaFile meta, FileHandle assetFile) {
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
