/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.runtime.libgdx.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMesh;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pool;
import com.mbrlabs.mundus.commons.importer.TerrainDTO;
import com.mbrlabs.mundus.commons.model.MTexture;
import com.mbrlabs.mundus.commons.terrain.BaseTerrain;
import com.mbrlabs.mundus.runtime.libgdx.Utils;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * @author Marcus Brummer
 * @version 09-02-2016
 */
public class Terrain extends BaseTerrain implements RenderableProvider {

    private TerrainTexture texture;

    private Model model;
    private ModelInstance mi;

    public Terrain(String assetsFolder, TerrainDTO dto, Array<MTexture> textures) {
        super(dto.getVertexRes());
        terrainWidth = dto.getWidth();
        terrainDepth = dto.getDepth();
        id = dto.getId();

        // TODO right textures
        texture = new TerrainTexture();
        texture.terrain = this;
        texture.base = Utils.findTextureById(textures, dto.getTexBase());
        texture.r = Utils.findTextureById(textures, dto.getTexR());
        texture.g = Utils.findTextureById(textures, dto.getTexG());
        texture.b = Utils.findTextureById(textures, dto.getTexB());
        texture.a = Utils.findTextureById(textures, dto.getTexA());
        if(dto.getSplatmapPath() != null) {
            texture.splatmap = new Texture(assetsFolder + Gdx.files.internal(dto.getSplatmapPath()));
        }

        TerrainTextureAttribute tex = new TerrainTextureAttribute(TerrainTextureAttribute.ATTRIBUTE_SPLAT0, texture);
        Material material = new Material();
        material.set(tex);

        heightData = readTerra(Gdx.files.internal(assetsFolder + dto.getTerraPath()));
        build(material);
    }

    private void build(Material material) {
        final int numVertices = this.vertexResolution * vertexResolution;
        final int numIndices = (this.vertexResolution - 1) * (vertexResolution - 1) * 6;

        Mesh mesh = new Mesh(true, numVertices, numIndices, attribs);
        this.vertices = new float[numVertices * stride];
        mesh.setIndices(buildIndices());
        buildVertices();
        mesh.setVertices(vertices);

        MeshPart meshPart = new MeshPart(null, mesh, 0, numIndices, GL20.GL_TRIANGLES);
        meshPart.update();
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.part(meshPart, material);
        model = mb.end();

        mi = new ModelInstance(model);
    }

    public void setTexture(TerrainTexture texture) {
        this.texture = texture;
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        mi.getRenderables(renderables, pool);
    }

    @Override
    public void dispose() {

    }

    public static float[] readTerra(FileHandle terra) {
        FloatArray floatArray = new FloatArray();

        DataInputStream is = null;
        try {
            is = new DataInputStream(new BufferedInputStream(
                    new GZIPInputStream(terra.read())));
            while (is.available() > 0) {
                floatArray.add(is.readFloat());
            }
            is.close();
        } catch (EOFException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return floatArray.toArray();
    }

}
