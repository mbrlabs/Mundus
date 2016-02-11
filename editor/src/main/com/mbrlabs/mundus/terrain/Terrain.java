/*
 * Copyright (c) 2015. See AUTHORS file.
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

package com.mbrlabs.mundus.terrain;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;
import com.mbrlabs.mundus.commons.terrain.BaseTerrain;

import java.nio.ByteBuffer;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class Terrain extends BaseTerrain {

    public String name;
    public String terraPath;

    // Textures
    private TerrainTexture terrainTexture;

    private Mesh mesh;
    public Renderable renderable;

    public Terrain(int vertexResolution) {
        super(vertexResolution);
        this.renderable = new Renderable();

        this.terrainTexture = new TerrainTexture();
        this.terrainTexture.setTerrain(this);
        this.renderable.material = new Material();
        this.renderable.material.set(new TerrainTextureAttribute(
                TerrainTextureAttribute.ATTRIBUTE_SPLAT0, terrainTexture));
    }

    public void init() {
        final int numVertices = this.vertexResolution * vertexResolution;
        final int numIndices = (this.vertexResolution - 1) * (vertexResolution - 1) * 6;

        this.mesh = new Mesh(false, numVertices, numIndices, attribs);
        this.vertices = new float[numVertices * stride];
        mesh.setIndices(buildIndices());
    }

    public void loadHeightMap(Pixmap map, float maxHeight) {
        if (map.getWidth() != vertexResolution || map.getHeight() != vertexResolution) throw new GdxRuntimeException("Incorrect map size");
        heightData = heightColorsToMap(map.getPixels(), map.getFormat(), this.vertexResolution, this.vertexResolution, maxHeight);
    }

    public TerrainTexture getTerrainTexture() {
        return terrainTexture;
    }

    public void setTerrainTexture(TerrainTexture terrainTexture) {
        terrainTexture.setTerrain(this);
        this.terrainTexture = terrainTexture;

        this.renderable.material.set(new TerrainTextureAttribute(
                TerrainTextureAttribute.ATTRIBUTE_SPLAT0, this.terrainTexture));
    }

    public void update () {
        buildVertices();
        mesh.setVertices(vertices);
        renderable.meshPart.mesh = mesh;
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        renderable.meshPart.offset = 0;
        renderable.meshPart.size = mesh.getNumIndices();
        renderable.meshPart.update();
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        renderable.worldTransform.set(transform);

        Renderable pooledRenderable = pool.obtain();
        pooledRenderable.set(renderable);
        renderables.add(pooledRenderable);
    }

    /**
     * Simply creates an array containing only all the red components of the heightData.
     */
    private float[] heightColorsToMap (final ByteBuffer data, final Pixmap.Format format, int width, int height, float maxHeight) {
        final int bytesPerColor = (format == Pixmap.Format.RGB888 ? 3 : (format == Pixmap.Format.RGBA8888 ? 4 : 0));
        if (bytesPerColor == 0) throw new GdxRuntimeException("Unsupported format, should be either RGB8 or RGBA8");
        if (data.remaining() < (width * height * bytesPerColor)) throw new GdxRuntimeException("Incorrect map size");

        final int startPos = data.position();
        byte[] source = null;
        int sourceOffset = 0;
        if (data.hasArray() && !data.isReadOnly()) {
            source = data.array();
            sourceOffset = data.arrayOffset() + startPos;
        } else {
            source = new byte[width * height * bytesPerColor];
            data.get(source);
            data.position(startPos);
        }

        float[] dest = new float[width * height];
        for (int i = 0; i < dest.length; ++i) {
            int v = source[sourceOffset + i * 3];
            v = v < 0 ? 256 + v : v;
            dest[i] = maxHeight * ((float)v / 255f);
        }

        return dest;
    }


    @Override
    public void dispose() {

    }

}
