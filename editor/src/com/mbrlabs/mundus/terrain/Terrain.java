package com.mbrlabs.mundus.terrain;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.nio.ByteBuffer;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class Terrain {

    public final Vector3 magnitude = new Vector3(0, 5, 0);
    public final Vector3 position = new Vector3(-10, 0, -10);
    public int terrainWidth = 20;
    public int terrainDepth = 20;

    public float[] heightData;
    public int verticesOnX;
    public int verticesOnZ;
    public Mesh mesh;
    public Renderable renderable;

    private float vertices[];
    private int stride;

    private int posPos;
    private int norPos;

    private final VertexInfo tempVInfo = new VertexInfo();
    private final Vector3 tmpV1 = new Vector3();
    private final Vector3 tmpV2 = new Vector3();

    public Terrain(int verticesOnX, int verticesOnZ, int attributes) {
        VertexAttributes attribs = MeshBuilder.createAttributes(attributes);
        this.posPos = attribs.getOffset(VertexAttributes.Usage.Position, -1);
        this.norPos = attribs.getOffset(VertexAttributes.Usage.Normal, -1);

        this.verticesOnX = verticesOnX;
        this.verticesOnZ = verticesOnZ;
        this.heightData = new float[verticesOnX * verticesOnZ];
        this.stride = attribs.vertexSize / 4;

        final int numVertices = verticesOnX * verticesOnZ;
        final int numIndices = (verticesOnX - 1) * (verticesOnZ - 1) * 6;
        this.mesh = new Mesh(false, numVertices, numIndices, attribs);
        this.vertices = new float[numVertices * stride];
        setIndices();
        update();
    }

    public void loadHeightMap(Pixmap map) {
        if (map.getWidth() != verticesOnX || map.getHeight() != verticesOnZ) throw new GdxRuntimeException("Incorrect map size");
        heightData = heightColorsToMap(map.getPixels(), map.getFormat(), this.verticesOnX, this.verticesOnZ);
        update();
    }

    private void setIndices () {
        final int w = verticesOnX - 1;
        final int h = verticesOnZ - 1;
        short indices[] = new short[w * h * 6];
        int i = -1;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                final int c00 = y * verticesOnX + x;
                final int c10 = c00 + 1;
                final int c01 = c00 + verticesOnX;
                final int c11 = c10 + verticesOnX;
                indices[++i] = (short)c11;
                indices[++i] = (short)c10;
                indices[++i] = (short)c00;
                indices[++i] = (short)c00;
                indices[++i] = (short)c01;
                indices[++i] = (short)c11;
            }
        }
        mesh.setIndices(indices);
    }

    public void update () {
        for (int x = 0; x < verticesOnX; ++x) {
            for (int y = 0; y < verticesOnZ; ++y) {
                VertexInfo v = calculateVertexAt(tempVInfo, x, y);
                setVertex(y * verticesOnX + x, v);
            }
        }
        mesh.setVertices(vertices);
        renderable = new Renderable();
        renderable.meshPart.mesh = mesh;
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        renderable.meshPart.offset = 0;
        renderable.meshPart.size = mesh.getNumIndices();
        renderable.meshPart.update();
    }

    private VertexInfo calculateVertexAt(final VertexInfo out, int x, int y) {
        calculatePositionAt(out.position, x, y);
        calculateSimpleNormalAt(out.normal, x, y);
        return out;
    }

    /**
     * Calculates normal of a vertex at x,y based on the verticesOnZ of the surrounding vertices
     */
    private Vector3 calculateSimpleNormalAt(Vector3 out, int x, int y) {
        // handle edges of terrain
        int xP1 = (x+1 >= verticesOnX) ? verticesOnX -1 : x+1;
        int yP1 = (y+1 >= verticesOnZ) ? verticesOnZ -1 : y+1;
        int xM1 = (x-1 < 0) ? 0 : x-1;
        int yM1 = (y-1 < 0) ? 0 : y-1;

        float hL = calculatePositionAt(tmpV2, xM1, y).y;
        float hR = calculatePositionAt(tmpV2, xP1, y).y;
        float hD = calculatePositionAt(tmpV2, x, yM1).y;
        float hU = calculatePositionAt(tmpV2, x, yP1).y;
        out.x = hL - hR;
        out.y = 2;
        out.z = hD - hU;
        out.nor();

        return out;
    }


    private Vector3 calculatePositionAt(Vector3 out, int x, int y) {
        final float dx = (float)x / (float)(verticesOnX - 1);
        final float dz = (float)y / (float)(verticesOnZ - 1);
        final float height = heightData[y * verticesOnX + x];

        out.set(position.x + dx*this.terrainWidth, 0, position.z + dz*this.terrainDepth);
        out.add(tmpV1.set(magnitude).scl(height));

        return out;
    }

    private void setVertex (int index, VertexInfo info) {
        index *= stride;
        if (posPos >= 0) {
            vertices[index + posPos + 0] = info.position.x;
            vertices[index + posPos + 1] = info.position.y;
            vertices[index + posPos + 2] = info.position.z;
        }
        if (norPos >= 0) {
            vertices[index + norPos + 0] = info.normal.x;
            vertices[index + norPos + 1] = info.normal.y;
            vertices[index + norPos + 2] = info.normal.z;
        }
    }

    /** Simply creates an array containing only all the red components of the heightData. */
    private static float[] heightColorsToMap (final ByteBuffer data, final Pixmap.Format format, int width, int height) {
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
            dest[i] = (float)v / 255f;
        }

        return dest;
    }


}
