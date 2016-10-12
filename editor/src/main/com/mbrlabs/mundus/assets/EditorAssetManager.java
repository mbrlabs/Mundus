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
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.MetaFile;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.assets.PixmapTextureAsset;
import com.mbrlabs.mundus.commons.assets.TerrainAsset;
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.utils.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

/**
 * @author Marcus Brummer
 * @version 24-01-2016
 */
public class EditorAssetManager extends AssetManager {

    private static final String TAG = EditorAssetManager.class.getSimpleName();

    public static final String STANDARD_ASSET_TEXTURE_CHESSBOARD = "chessboard";
    private ModelImporter.ImportedModel model;

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
     * Creates a new meta file and saves it at the given location.
     *
     * @param file
     *            save location
     * @param type
     *            asset type
     * @return saved meta file
     * @throws IOException
     */
    public MetaFile createNewMetaFile(FileHandle file, AssetType type) throws IOException, AssetAlreadyExistsException {
        if(file.exists()) throw new AssetAlreadyExistsException();

        final MetaFile meta = new MetaFile(file);
        meta.setUuid(UUID.randomUUID().toString());
        meta.setVersion(MetaFile.CURRENT_VERSION);
        meta.setLastModified(new Date());
        meta.setType(type);
        meta.save();

        return meta;
    }

    /**
     * Creates a couple of standard assets.
     *
     * Creates a couple of standard assets in the current project, that should
     * be included in every project.
     */
    public void createStandardAssets() {
        try {
            // chessboard
            TextureAsset chessboard = createTextureAsset(Gdx.files.internal("standardAssets/chessboard.png"));
            assetIndex.remove(chessboard.getID());
            chessboard.getMeta().setID(STANDARD_ASSET_TEXTURE_CHESSBOARD);
            assetIndex.put(chessboard.getID(), chessboard);
            chessboard.getMeta().save();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new model asset.
     *
     * Creates a new model asset in the current project and adds it to this
     * asset manager.
     * 
     * @param model
     *            imported model
     * @return model asset
     * @throws IOException
     */
    public ModelAsset createModelAsset(ModelImporter.ImportedModel model) throws IOException, AssetAlreadyExistsException {
        this.model = model;
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

    /**
     * Creates a new terrain asset.
     * 
     * This creates a .terra file (height data) and a pixmap texture (splatmap).
     * The asset will be added to this asset manager.
     * 
     * @param vertexResolution
     *            vertex resolution of the terrain
     * @param size
     *            terrain size
     * @return new terrain asset
     * @throws IOException
     */
    public TerrainAsset createTerrainAsset(int vertexResolution, int size) throws IOException, AssetAlreadyExistsException {
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

        // set base texture
        TextureAsset chessboard = (TextureAsset) findAssetByID(STANDARD_ASSET_TEXTURE_CHESSBOARD);
        if (chessboard != null) {
            asset.setSplatBase(chessboard);
            asset.applyDependencies();
            asset.getMeta().save();
        }

        addAsset(asset);
        return asset;
    }

    /**
     * Creates a new pixmap texture asset.
     *
     * This creates a new pixmap texture and adds it to this asset manager.
     * 
     * @param size
     *            size of the pixmap in pixels
     * @return new pixmap asset
     * @throws IOException
     */
    public PixmapTextureAsset createPixmapTextureAsset(int size) throws IOException, AssetAlreadyExistsException {
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

    /**
     * Creates a new texture asset using the given texture file.
     *
     * @param texture
     * @return
     * @throws IOException
     */
    public TextureAsset createTextureAsset(FileHandle texture) throws IOException, AssetAlreadyExistsException {
        MetaFile meta = createMetaFileFromAsset(texture, AssetType.TEXTURE);
        FileHandle importedAssetFile = copyToAssetFolder(texture);

        TextureAsset asset = new TextureAsset(meta, importedAssetFile);
        // TODO parse special texture instead of always setting them
        asset.setTileable(true);
        asset.generateMipmaps(true);
        asset.load();

        addAsset(asset);
        return asset;
    }

    /**
     * Creates a new & empty material asset.
     *
     * @return new material asset
     * @throws IOException
     */
    public MaterialAsset createMaterialAsset(String name) throws IOException, AssetAlreadyExistsException {
        // create empty material file
        String path = FilenameUtils.concat(rootFolder.path(), name) + MaterialAsset.EXTENSION;
        FileHandle matFile = Gdx.files.absolute(path);
        FileUtils.touch(matFile.file());

        MetaFile meta = createMetaFileFromAsset(matFile, AssetType.MATERIAL);
        MaterialAsset asset = new MaterialAsset(meta, matFile);
        asset.load();

        addAsset(asset);
        return asset;
    }

    /**
     * Saves all terrains (splatmap, height data + meta files)
     *
     * @throws IOException
     */
    public void saveTerrainAssets() throws IOException {
        for (TerrainAsset terrain : getTerrainAssets()) {
            saveTerrainAsset(terrain);
        }
    }

    /**
     * Saves an existing terrain asset.
     *
     * This updates all modifiable assets and the meta file.
     *
     * @param terrain
     *            terrain asset
     * @throws IOException
     */
    public void saveTerrainAsset(TerrainAsset terrain) throws IOException {
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

    public void saveMaterialAsset(MaterialAsset mat) throws IOException {
        // save .mat
        Properties props = new Properties();
        if (mat.getDiffuseColor() != null) {
            props.setProperty(MaterialAsset.PROP_DIFFUSE_COLOR, mat.getDiffuseColor().toString());
        }
        if (mat.getDiffuseTexture() != null) {
            props.setProperty(MaterialAsset.PROP_DIFFUSE_TEXTURE, mat.getDiffuseTexture().getID());
        }
        if (mat.getNormalMap() != null) {
            props.setProperty(MaterialAsset.PROP_MAP_NORMAL, mat.getNormalMap().getID());
        }
        props.setProperty(MaterialAsset.PROP_OPACITY, String.valueOf(mat.getOpacity()));
        props.setProperty(MaterialAsset.PROP_SHININESS, String.valueOf(mat.getShininess()));
        props.store(new FileOutputStream(mat.getFile().file()), null);

        // save meta file
        mat.getMeta().save();
    }

    private MetaFile createMetaFileFromAsset(FileHandle assetFile, AssetType type) throws IOException, AssetAlreadyExistsException {
        String metaName = assetFile.name() + "." + MetaFile.META_EXTENSION;
        String metaPath = FilenameUtils.concat(rootFolder.path(), metaName);
        return createNewMetaFile(new FileHandle(metaPath), type);
    }

    private FileHandle copyToAssetFolder(FileHandle file) {
        FileHandle copy = new FileHandle(FilenameUtils.concat(rootFolder.path(), file.name()));
        file.copyTo(copy);
        return copy;
    }

}
