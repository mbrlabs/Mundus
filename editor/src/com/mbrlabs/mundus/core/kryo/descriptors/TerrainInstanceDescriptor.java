package com.mbrlabs.mundus.core.kryo.descriptors;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;


/**
 * @author Marcus Brummer
 * @version 23-12-2015
 */
public class TerrainInstanceDescriptor {

    @Tag(0)
    private long id;
    @Tag(1)
    private String name;
    @Tag(2)
    private long terrainID;
    @Tag(3)
    private float posX;
    @Tag(4)
    private float posZ;

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

    public long getTerrainID() {
        return terrainID;
    }

    public void setTerrainID(long terrainID) {
        this.terrainID = terrainID;
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

}
