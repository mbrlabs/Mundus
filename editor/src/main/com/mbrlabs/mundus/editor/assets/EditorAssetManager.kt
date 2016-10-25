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
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.utils.ObjectSet
import com.mbrlabs.mundus.commons.assets.*
import com.mbrlabs.mundus.commons.assets.meta.MetaFile
import com.mbrlabs.mundus.editor.utils.Log
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import java.io.*
import java.util.*
import java.util.zip.GZIPOutputStream

/**
 * @author Marcus Brummer
 * @version 24-01-2016
 */
class EditorAssetManager(assetsRoot: FileHandle) : AssetManager(assetsRoot) {

    companion object {
        private val TAG = EditorAssetManager::class.java.simpleName
        val STANDARD_ASSET_TEXTURE_CHESSBOARD = "chessboard"
    }

    /** Modified assets that need to be saved.  */
    private val dirtyAssets: ObjectSet<Asset>

    init {
        if (rootFolder != null && (!rootFolder.exists() || !rootFolder.isDirectory)) {
            Log.fatal(TAG, "Root asset folder is not a directory")
        }
        dirtyAssets = ObjectSet<Asset>()
    }

    fun addDirtyAsset(asset: Asset) {
        dirtyAssets.add(asset)
    }

    fun getDirtyAssets(): ObjectSet<Asset> {
        return dirtyAssets
    }

    /**
     * Creates a new meta file and saves it at the given location.
     *
     * @param file
     *            save location
     * @param type
     *            asset type
     * @return saved meta file
     *
     * @throws IOException
     */
    @Throws(IOException::class, AssetAlreadyExistsException::class)
    fun createNewMetaFile(file: FileHandle, type: AssetType): MetaFile {
        if (file.exists()) throw AssetAlreadyExistsException()

        val meta = MetaFile(file)
        meta.setUuid(newUUID())
        meta.version = MetaFile.CURRENT_VERSION
        meta.lastModified = Date()
        meta.type = type
        meta.save()

        return meta
    }

    private fun newUUID(): String {
        return UUID.randomUUID().toString().replace("-".toRegex(), "")
    }

    /**
     * Creates a couple of standard assets.
     *
     * Creates a couple of standard assets in the current project, that should
     * be included in every project.
     */
    fun createStandardAssets() {
        try {
            // chessboard
            val chessboard = createTextureAsset(Gdx.files.internal("standardAssets/chessboard.png"))
            assetIndex.remove(chessboard.id)
            chessboard.meta.id = STANDARD_ASSET_TEXTURE_CHESSBOARD
            assetIndex.put(chessboard.id, chessboard)
            chessboard.meta.save()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Creates a new model asset.
     *
     * Creates a new model asset in the current project and adds it to this
     * asset manager.
     *
     * @param model imported model
     * @return model asset
     *
     * @throws IOException
     */
    @Throws(IOException::class, AssetAlreadyExistsException::class)
    fun createModelAsset(model: ModelImporter.ImportedModel): ModelAsset {
        val modelFilename = model.g3dbFile!!.name()
        val metaFilename = modelFilename + ".meta"

        // create meta file
        val metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename)
        val meta = createNewMetaFile(FileHandle(metaPath), AssetType.MODEL)

        // copy model file
        val assetFile = FileHandle(FilenameUtils.concat(rootFolder.path(), modelFilename))
        model.g3dbFile!!.copyTo(assetFile)

        // load & return asset
        val asset = ModelAsset(meta, assetFile)
        asset.load()

        addAsset(asset)
        return asset
    }

    /**
     * Creates a new terrainAsset asset.
     *
     * This creates a .terra file (height data) and a pixmap texture (splatmap).
     * The asset will be added to this asset manager.
     *
     * @param vertexResolution
     *            vertex resolution of the terrainAsset
     * @param size
     *            terrainAsset size
     * @return new terrainAsset asset
     * @throws IOException
     */
    @Throws(IOException::class, AssetAlreadyExistsException::class)
    fun createTerraAsset(name: String, vertexResolution: Int, size: Int): TerrainAsset {
        val terraFilename = name + ".terra"
        val metaFilename = terraFilename + ".meta"

        // create meta file
        val metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename)
        val meta = createNewMetaFile(FileHandle(metaPath), AssetType.TERRAIN)
        meta.terrainSize = size
        meta.save()

        // create terra file
        val terraPath = FilenameUtils.concat(rootFolder.path(), terraFilename)
        val terraFile = File(terraPath)
        FileUtils.touch(terraFile)

        // create initial height data
        val data = FloatArray(vertexResolution * vertexResolution)
        for (i in data.indices) {
            data[i] = 0f
        }

        // write terra file
        val outputStream = DataOutputStream(
                BufferedOutputStream(GZIPOutputStream(FileOutputStream(terraFile))))
        for (f in data) {
            outputStream.writeFloat(f)
        }
        outputStream.flush()
        outputStream.close()

        // load & apply standard chessboard texture
        val asset = TerrainAsset(meta, FileHandle(terraFile))
        asset.load()

        // set base texture
        val chessboard = findAssetByID(STANDARD_ASSET_TEXTURE_CHESSBOARD)
        if (chessboard != null) {
            asset.splatBase = chessboard as TextureAsset
            asset.applyDependencies()
            asset.meta.save()
        }

        addAsset(asset)
        return asset
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
    @Throws(IOException::class, AssetAlreadyExistsException::class)
    fun createPixmapTextureAsset(size: Int): PixmapTextureAsset {
        val pixmapFilename = newUUID().substring(0, 5) + ".png"
        val metaFilename = pixmapFilename + ".meta"

        // create meta file
        val metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename)
        val meta = createNewMetaFile(FileHandle(metaPath), AssetType.PIXMAP_TEXTURE)

        // create pixmap
        val pixmapPath = FilenameUtils.concat(rootFolder.path(), pixmapFilename)
        val pixmap = Pixmap(size, size, Pixmap.Format.RGBA8888)
        val pixmapAssetFile = FileHandle(pixmapPath)
        PixmapIO.writePNG(pixmapAssetFile, pixmap)
        pixmap.dispose()

        // load & return asset
        val asset = PixmapTextureAsset(meta, pixmapAssetFile)
        asset.load()

        addAsset(asset)
        return asset
    }

    /**
     * Creates a new texture asset using the given texture file.
     *
     * @param texture
     * @return
     * @throws IOException
     */
    @Throws(IOException::class, AssetAlreadyExistsException::class)
    fun createTextureAsset(texture: FileHandle): TextureAsset {
        val meta = createMetaFileFromAsset(texture, AssetType.TEXTURE)
        val importedAssetFile = copyToAssetFolder(texture)

        val asset = TextureAsset(meta, importedAssetFile)
        // TODO parse special texture instead of always setting them
        asset.setTileable(true)
        asset.generateMipmaps(true)
        asset.load()

        addAsset(asset)
        return asset
    }

    /**
     * Creates a new & empty material asset.
     *
     * @return new material asset
     * @throws IOException
     */
    @Throws(IOException::class, AssetAlreadyExistsException::class)
    fun createMaterialAsset(name: String): MaterialAsset {
        // create empty material file
        val path = FilenameUtils.concat(rootFolder.path(), name) + MaterialAsset.EXTENSION
        val matFile = Gdx.files.absolute(path)
        FileUtils.touch(matFile.file())

        val meta = createMetaFileFromAsset(matFile, AssetType.MATERIAL)
        val asset = MaterialAsset(meta, matFile)
        asset.load()

        addAsset(asset)
        return asset
    }

    /**
     * @param asset
     * @throws IOException
     */
    @Throws(IOException::class)
    fun saveAsset(asset: Asset) {
        if (asset is MaterialAsset) {
            saveMaterialAsset(asset)
        } else if (asset is TerrainAsset) {
            saveTerrainAsset(asset)
        } else if (asset is ModelAsset) {
            saveModelAsset(asset)
        }
        // TODO other assets ?
    }

    /**
     * @param asset
     */
    @Throws(IOException::class)
    fun saveModelAsset(asset: ModelAsset) {
        for (g3dbMatID in asset.defaultMaterials.keys) {
            asset.meta.defaultModelMaterials.put(g3dbMatID, asset.defaultMaterials[g3dbMatID]!!.id)
        }
        asset.meta.save()
    }

    /**
     * Saves an existing terrainAsset asset.
     *
     * This updates all modifiable assets and the meta file.
     *
     * @param terrain
     *             terrainAsset asset
     * @throws IOException
     */
    @Throws(IOException::class)
    fun saveTerrainAsset(terrain: TerrainAsset) {
        // save .terra file
        val outputStream = DataOutputStream(
                BufferedOutputStream(GZIPOutputStream(FileOutputStream(terrain.file.file()))))
        for (f in terrain.data) {
            outputStream.writeFloat(f)
        }
        outputStream.flush()
        outputStream.close()

        // save splatmap
        val splatmap = terrain.splatmap
        if (splatmap != null) {
            PixmapIO.writePNG(splatmap.file, splatmap.pixmap)
        }

        // save meta file
        terrain.meta.save()
    }

    @Throws(IOException::class)
    fun saveMaterialAsset(mat: MaterialAsset) {
        // save .mat
        val props = Properties()
        if (mat.diffuseColor != null) {
            props.setProperty(MaterialAsset.PROP_DIFFUSE_COLOR, mat.diffuseColor.toString())
        }
        if (mat.diffuseTexture != null) {
            props.setProperty(MaterialAsset.PROP_DIFFUSE_TEXTURE, mat.diffuseTexture.id)
        }
        if (mat.normalMap != null) {
            props.setProperty(MaterialAsset.PROP_MAP_NORMAL, mat.normalMap.id)
        }
        props.setProperty(MaterialAsset.PROP_OPACITY, mat.opacity.toString())
        props.setProperty(MaterialAsset.PROP_SHININESS, mat.shininess.toString())
        props.store(FileOutputStream(mat.file.file()), null)

        // save meta file
        mat.meta.save()
    }

    @Throws(IOException::class, AssetAlreadyExistsException::class)
    private fun createMetaFileFromAsset(assetFile: FileHandle, type: AssetType): MetaFile {
        val metaName = assetFile.name() + "." + MetaFile.META_EXTENSION
        val metaPath = FilenameUtils.concat(rootFolder.path(), metaName)
        return createNewMetaFile(FileHandle(metaPath), type)
    }

    private fun copyToAssetFolder(file: FileHandle): FileHandle {
        val copy = FileHandle(FilenameUtils.concat(rootFolder.path(), file.name()))
        file.copyTo(copy)
        return copy
    }

}
