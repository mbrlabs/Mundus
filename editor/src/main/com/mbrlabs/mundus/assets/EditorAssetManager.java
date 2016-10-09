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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.MetaFile;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.assets.PixmapTextureAsset;
import com.mbrlabs.mundus.commons.assets.TerrainAsset;
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.commons.terrain.SplatMap;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.events.AssetImportEvent;
import com.mbrlabs.mundus.utils.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

/**
 * @author Marcus Brummer
 * @version 24-01-2016
 */
public class EditorAssetManager extends AssetManager {

    private static final String TAG = EditorAssetManager.class.getSimpleName();

    public static final String STANDARD_ASSET_TEXTURE_CHESSBOARD = "chessboard";

    /**
     * Editor asset manager constructor.
     *
     * @param assetsRoot
     *            assets root folder.
     */
    public EditorAssetManager(FileHandle assetsRoot) {
        super(assetsRoot);
        if (rootFolder != null && (!rootFolder.exists() || !rootFolder.isDirectory())) {
            Log.fatal(TAG, "Root asset folder is not a directory");
        }
    }

    /**
     * Imports a new asset.
     *
     * @param asset
     *            handle to asset file
     * @param clazz
     *            asset type
     * @return asset or not if type not supported
     */
    public Asset importAsset(FileHandle asset, Class clazz) {
        // import asset
        Asset newAsset = null;
        try {
            if (clazz == TextureAsset.class) {
                newAsset = importTextureAsset(asset);
            } else if (clazz == PixmapTextureAsset.class) {
                newAsset = importPixmapTextureAsset(asset);
            } else if (clazz == TerrainAsset.class) {
                newAsset = importTerrainAsset(asset);
            } else if (clazz == ModelAsset.class) {
                newAsset = importModelAsset(asset);
            }
        } catch (IOException ioe) {
            Log.exception(TAG, ioe);
            return null;
        }

        // add to list
        if (newAsset != null) {
            addAsset(newAsset);
        }

        return newAsset;
    }

    public void createStandardAssets() {
        try {
            // chessboard
            TextureAsset chessboard = (TextureAsset) importAsset(Gdx.files.internal("standardAssets/chessboard.png"),
                    TextureAsset.class);
            assetIndex.remove(chessboard.getID());
            chessboard.getMeta().setID(STANDARD_ASSET_TEXTURE_CHESSBOARD);
            assetIndex.put(chessboard.getID(), chessboard);
            chessboard.getMeta().save();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new meta file and saves it at the given location.
     *
     * @param file
     *            save location
     * @param type
     *            asset type
     * @return saved meta file
     * @throws IOException
     */
    public MetaFile createNewMetaFile(FileHandle file, AssetType type) throws IOException {
        final MetaFile meta = new MetaFile(file);
        meta.setUuid(UUID.randomUUID().toString());
        meta.setVersion(MetaFile.CURRENT_VERSION);
        meta.setLastModified(new Date());
        meta.setType(type);
        meta.save();

        return meta;
    }

    public ModelAsset createModelAsset(ModelImporter.ImportedModel model) throws IOException {
        String modelFilename = model.g3dbFile.name();
        String metaFilename = modelFilename + ".meta";

        // create meta file
        String metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename);
        MetaFile meta = createNewMetaFile(new FileHandle(metaPath), AssetType.MODEL);

        // copy model file
        FileHandle assetFile = new FileHandle(FilenameUtils.concat(rootFolder.path(), modelFilename));
        model.g3dbFile.copyTo(assetFile);

        // load & return asset
        ModelAsset asset = new ModelAsset(meta, assetFile);
        asset.load();
        addAsset(asset);

        return asset;
    }

    public TerrainAsset createTerrainAsset(int vertexResolution, int size) throws IOException {
        String terraFilename = "terrain_" + UUID.randomUUID().toString() + ".terra";
        String metaFilename = terraFilename + ".meta";

        // create meta file
        String metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename);
        MetaFile meta = createNewMetaFile(new FileHandle(metaPath), AssetType.TERRAIN);
        meta.setTerrainSize(size);
        meta.save();

        // create terra file
        String terraPath = FilenameUtils.concat(rootFolder.path(), terraFilename);
        File terraFile = new File(terraPath);
        FileUtils.touch(terraFile);

        // create initial height data
        float[] data = new float[vertexResolution * vertexResolution];
        for (int i = 0; i < data.length; i++) {
            data[i] = 0;
        }

        // write terra file
        DataOutputStream outputStream = new DataOutputStream(
                new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(terraFile))));
        for (float f : data) {
            outputStream.writeFloat(f);
        }
        outputStream.flush();
        outputStream.close();

        // load & apply standard chessboard texture
        TerrainAsset asset = new TerrainAsset(meta, new FileHandle(terraFile));
        asset.load();

        TextureAsset chessboard = (TextureAsset) findAssetByID(STANDARD_ASSET_TEXTURE_CHESSBOARD);
        if(chessboard != null) {
            // create splatmap
            PixmapTextureAsset splatmap = createPixmapTextureAsset(SplatMap.DEFAULT_SIZE);
            asset.setSplatmap(splatmap);
            asset.setSplatBase(chessboard);
            asset.applyDependencies();
            asset.getMeta().save();
        }

        addAsset(asset);
        return asset;
    }

    public void saveTerrainAssets() throws IOException {
        for (TerrainAsset terrain : getTerrainAssets()) {

            // save .terra file
            DataOutputStream outputStream = new DataOutputStream(
                    new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(terrain.getFile().file()))));
            for (float f : terrain.getData()) {
                outputStream.writeFloat(f);
            }
            outputStream.flush();
            outputStream.close();

            // save splatmap
            PixmapTextureAsset splatmap = terrain.getSplatmap();
            if (splatmap != null) {
                PixmapIO.writePNG(splatmap.getFile(), splatmap.getPixmap());
            }

            // save meta file
            terrain.getMeta().save();
        }
    }

    public PixmapTextureAsset createPixmapTextureAsset(int size) throws IOException {
        String pixmapFilename = "pixmap_" + UUID.randomUUID().toString() + ".png";
        String metaFilename = pixmapFilename + ".meta";

        // create meta file
        String metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename);
        MetaFile meta = createNewMetaFile(new FileHandle(metaPath), AssetType.PIXMAP_TEXTURE);

        // create pixmap
        String pixmapPath = FilenameUtils.concat(rootFolder.path(), pixmapFilename);
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        FileHandle pixmapAssetFile = new FileHandle(pixmapPath);
        PixmapIO.writePNG(pixmapAssetFile, pixmap);
        pixmap.dispose();

        // load & return asset
        PixmapTextureAsset asset = new PixmapTextureAsset(meta, pixmapAssetFile);
        asset.load();
        addAsset(asset);

        return asset;
    }

    private MetaFile createMetaFileFromAsset(FileHandle assetFile, AssetType type) throws IOException {
        String metaName = assetFile.name() + "." + MetaFile.META_EXTENSION;
        String metaPath = FilenameUtils.concat(rootFolder.path(), metaName);
        return createNewMetaFile(new FileHandle(metaPath), type);
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
        // TODO parse special texture instead of always setting them
        asset.setTileable(true);
        asset.generateMipmaps(true);
        asset.load();

        return asset;
    }

    private TextureAsset importPixmapTextureAsset(FileHandle assetFile) throws IOException {
        MetaFile meta = createMetaFileFromAsset(assetFile, AssetType.PIXMAP_TEXTURE);
        // TODO implement
        return null;
    }

    private TextureAsset importTerrainAsset(FileHandle assetFile) throws IOException {
        MetaFile meta = createMetaFileFromAsset(assetFile, AssetType.TERRAIN);
        // TODO implement
        return null;
    }

    private TextureAsset importModelAsset(FileHandle assetFile) throws IOException {
        MetaFile meta = createMetaFileFromAsset(assetFile, AssetType.MODEL);
        // TODO implement
        return null;
    }

    // /**
    // *
    // * @param textureFile
    // * @param mipMap
    // * @return
    // */
    // public MTexture importTexture(FileHandle textureFile, boolean mipMap) {
    // long id = projectManager.current().obtainID();
    //
    // String relativeImportPath = ProjectManager.PROJECT_TEXTURE_DIR +
    // textureFile.name();
    // String absoluteImportPath =
    // FilenameUtils.concat(projectManager.current().path, relativeImportPath);
    // FileHandle absoluteImportFile = Gdx.files.absolute(absoluteImportPath);
    //
    // textureFile.copyTo(absoluteImportFile);
    //
    // MTexture tex = new MTexture();
    // tex.setId(id);
    // tex.setPath(relativeImportPath);
    // if(mipMap) {
    // tex.texture = TextureUtils.loadMipmapTexture(absoluteImportFile, true);
    // } else {
    // tex.texture = new Texture(absoluteImportFile);
    // }
    //
    // projectManager.current().textures.add(tex);
    //
    // // save whole project
    // projectManager.saveCurrentProject();
    //
    // return tex;
    //
    // return null;
    // }

}
