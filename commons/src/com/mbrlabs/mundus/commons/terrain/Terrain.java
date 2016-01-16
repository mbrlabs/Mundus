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

package com.mbrlabs.mundus.commons.terrain;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.nio.ByteBuffer;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class Terrain {

    public long id;
    public String name;

    public String terraPath;
    //public String texturePath;

    public int terrainWidth = 1200;
    public int terrainDepth = 1200;

    public float[] heightData;
    public int vertexResolution;
    public Mesh mesh;
    public Renderable renderable;

    private VertexAttributes attribs;
    private float vertices[];
    private int stride;


    private int posPos;
    private int norPos;
    private int uvPos;

    private final Vector2 uvScale = new Vector2(30, 30);

    private final VertexInfo tempVInfo = new VertexInfo();

    private Texture texture;

    public Terrain(int vertexResolution) {
        attribs = MeshBuilder.createAttributes(VertexAttributes.Usage.Position |
                VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        this.posPos = attribs.getOffset(VertexAttributes.Usage.Position, -1);
        this.norPos = attribs.getOffset(VertexAttributes.Usage.Normal, -1);
        this.uvPos = attribs.getOffset(VertexAttributes.Usage.TextureCoordinates, -1);
        this.stride = attribs.vertexSize / 4;
        this.vertexResolution = vertexResolution;
    }

    public void init() {
        final int numVertices = this.vertexResolution * vertexResolution;
        final int numIndices = (this.vertexResolution - 1) * (vertexResolution - 1) * 6;

        this.heightData = new float[this.vertexResolution * vertexResolution];
        this.mesh = new Mesh(false, numVertices, numIndices, attribs);
        this.vertices = new float[numVertices * stride];
        buildIndices();
    }

    public void loadHeightMap(Pixmap map, float maxHeight) {
        if (map.getWidth() != vertexResolution || map.getHeight() != vertexResolution) throw new GdxRuntimeException("Incorrect map size");
        heightData = heightColorsToMap(map.getPixels(), map.getFormat(), this.vertexResolution, this.vertexResolution, maxHeight);
    }

    private void buildIndices() {
        final int x = vertexResolution - 1;
        final int z = vertexResolution - 1;
        short indices[] = new short[x * z * 6];
        int index = 0;
        for (int curX = 0; curX < x; curX++) {
            for (int curZ = 0; curZ < z; curZ++) {
                int topLeft = curZ * vertexResolution + curX;
                int topRight = topLeft + 1;
                int bottomLeft = (curZ+1) * vertexResolution + curX;
                int bottomRight = bottomLeft + 1;

                indices[index++] = (short) topLeft;
                indices[index++] = (short) bottomLeft;
                indices[index++] = (short) bottomRight;
                indices[index++] = (short) bottomRight;
                indices[index++] = (short) topLeft;
                indices[index++] = (short) topRight;
            }
        }

        mesh.setIndices(indices);
    }

    private void buildVertices() {
        for (int x = 0; x < vertexResolution; x++) {
            for (int z = 0; z < vertexResolution; z++) {
                calculateVertexAt(tempVInfo, x, z);
                calculateSimpleNormalAt(tempVInfo, x, z);
                setVertex(z * vertexResolution + x, tempVInfo);
            }
        }
    }


    public void update () {
        buildVertices();
        mesh.setVertices(vertices);
        renderable = new Renderable();
        renderable.meshPart.mesh = mesh;
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        renderable.meshPart.offset = 0;
        renderable.meshPart.size = mesh.getNumIndices();
        renderable.material = new Material();
        if(this.texture != null) {
            renderable.material.set(TextureAttribute.createDiffuse(this.texture));
        }

        renderable.meshPart.update();
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
        renderable.material.set(TextureAttribute.createDiffuse(this.texture));
    }

    /**
     * Calculates normal of a vertex at x,y based on the verticesOnZ of the surrounding vertices
     */
    private VertexInfo calculateSimpleNormalAt(VertexInfo out, int x, int y) {
        // handle edges of terrain
        int xP1 = (x+1 >= vertexResolution) ? vertexResolution -1 : x+1;
        int yP1 = (y+1 >= vertexResolution) ? vertexResolution -1 : y+1;
        int xM1 = (x-1 < 0) ? 0 : x-1;
        int yM1 = (y-1 < 0) ? 0 : y-1;

        float hL = heightData[y * vertexResolution + xM1];
        float hR = heightData[y * vertexResolution + xP1];
        float hD = heightData[yM1 * vertexResolution + x];
        float hU = heightData[yP1 * vertexResolution + x];
        out.normal.x = hL - hR;
        out.normal.y = 2;
        out.normal.z = hD - hU;
        out.normal.nor();

        return out;
    }


    public VertexInfo calculateVertexAt(VertexInfo out, int x, int z) {
        final float dx = (float)x / (float)(vertexResolution - 1);
        final float dz = (float)z / (float)(vertexResolution - 1);
        final float height = heightData[z * vertexResolution + x];

        out.position.set(dx * this.terrainWidth, height, dz * this.terrainDepth);
        out.uv.set(dx, dz).scl(uvScale);

        return out;
    }

    public Vector3 getVertexPosition(Vector3 out, int x, int z) {
        final float dx = (float)x / (float)(vertexResolution - 1);
        final float dz = (float)z / (float)(vertexResolution - 1);
        final float height = heightData[z * vertexResolution + x];
        out.set(dx * this.terrainWidth, height, dz * this.terrainDepth);
        return out;
    }

    private void setVertex (int index, VertexInfo info) {
        index *= stride;
        if (posPos >= 0) {
            vertices[index + posPos + 0] = info.position.x;
            vertices[index + posPos + 1] = info.position.y;
            vertices[index + posPos + 2] = info.position.z;
        }
        if (uvPos >= 0) {
            vertices[index + uvPos + 0] = info.uv.x;
            vertices[index + uvPos + 1] = info.uv.y;
        }
        if (norPos >= 0) {
            vertices[index + norPos + 0] = info.normal.x;
            vertices[index + norPos + 1] = info.normal.y;
            vertices[index + norPos + 2] = info.normal.z;
        }
    }

    /** Simply creates an array containing only all the red components of the heightData. */
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



}
