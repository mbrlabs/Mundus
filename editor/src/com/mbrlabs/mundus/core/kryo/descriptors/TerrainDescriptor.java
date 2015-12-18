package com.mbrlabs.mundus.core.kryo.descriptors;


import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;


/**
 * @author Marcus Brummer
 * @version 17-12-2015
 */
public class TerrainDescriptor {

    @Tag(0)
    private long id;
    @Tag(1)
    private String name;
    @Tag(2)
    private int width;
    @Tag(3)
    private int depth;
    @Tag(4)
    private float posX;
    @Tag(5)
    private float posZ;
    @Tag(6)
    private int vertexResolution;
    @Tag(7)
    private String path;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosZ() {
        return posZ;
    }

    public void setPosZ(float posZ) {
        this.posZ = posZ;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getVertexResolution() {
        return vertexResolution;
    }

    public void setVertexResolution(int vertexResolution) {
        this.vertexResolution = vertexResolution;
    }
}
