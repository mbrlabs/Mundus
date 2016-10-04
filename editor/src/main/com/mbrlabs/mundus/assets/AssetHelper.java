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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.MetaFile;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.assets.PixmapTextureAsset;
import com.mbrlabs.mundus.commons.assets.TerraAsset;

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
 * @version 02-10-2016
 */
public class AssetHelper {

    /**
     * Creates a new meta file and saves it at the given location.
     *
     * @param file  save location
     * @param type  asset type
     * @return      saved meta file
     * @throws IOException
     */
    public static MetaFile createNewMetaFile(FileHandle file, AssetType type) throws IOException {
        final MetaFile meta = new MetaFile(file);
        meta.setUuid(UUID.randomUUID().toString());
        meta.setVersion(MetaFile.CURRENT_VERSION);
        meta.setLastModified(new Date());
        meta.setType(type);
        meta.save();

        return meta;
    }

    public static TerraAsset createTerraAsset(FileHandle assetRoot, int vertexResolution) throws IOException {
        String terraFilename = "terrain_" + UUID.randomUUID().toString() + ".terra";
        String metaFilename = terraFilename + ".meta";

        // create meta file
        String metaPath = FilenameUtils.concat(assetRoot.path(), metaFilename);
        MetaFile meta = createNewMetaFile(new FileHandle(metaPath), AssetType.TERRA);

        // create terra file
        String terraPath = FilenameUtils.concat(assetRoot.path(), terraFilename);
        File terraFile = new File(terraPath);
        FileUtils.touch(terraFile);

        // create initial height data
        float[] data = new float[vertexResolution * vertexResolution];
        for(int i = 0; i < data.length; i++) {
            data[i] = 0;
        }

        // write terra file
        DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(terraFile))));
        for(float f : data) {
            outputStream.writeFloat(f);
        }
        outputStream.flush();
        outputStream.close();

        // load & return asset
        TerraAsset terra = new TerraAsset(meta, new FileHandle(terraFile));
        terra.load();
        return terra;
    }

    public static PixmapTextureAsset createPixmapTextureAsset(FileHandle assetRoot, int size) throws IOException {
        String pixmapFilename = "pixmap_" + UUID.randomUUID().toString() + ".png";
        String metaFilename = pixmapFilename + ".meta";

        // create meta file
        String metaPath = FilenameUtils.concat(assetRoot.path(), metaFilename);
        MetaFile meta = createNewMetaFile(new FileHandle(metaPath), AssetType.PIXMAP_TEXTURE);

        // create pixmap
        String pixmapPath = FilenameUtils.concat(assetRoot.path(), pixmapFilename);
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        FileHandle pixmapAssetFile = new FileHandle(pixmapPath);
        PixmapIO.writePNG(pixmapAssetFile, pixmap);
        pixmap.dispose();

        // load & return asset
        PixmapTextureAsset asset = new PixmapTextureAsset(meta, pixmapAssetFile);
        asset.load();
        return asset;
    }

    public static ModelAsset createModelAsset(ModelImporter.ImportedModel model) {
        return null;
    }

}
