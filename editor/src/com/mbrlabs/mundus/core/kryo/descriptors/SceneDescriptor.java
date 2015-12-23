package com.mbrlabs.mundus.core.kryo.descriptors;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 22-12-2015
 */
public class SceneDescriptor {

    @Tag(0)
    private long id;
    @Tag(1)
    private String name;
    @Tag(2)
    private List<ModelInstanceDescriptor> entities;
    @Tag(3)
    private List<TerrainInstanceDescriptor> terrains;

    public SceneDescriptor() {
        entities = new ArrayList<>();
        terrains = new ArrayList<>();
    }

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

    public List<ModelInstanceDescriptor> getEntities() {
        return entities;
    }

    public void setEntities(List<ModelInstanceDescriptor> entities) {
        this.entities = entities;
    }

    public List<TerrainInstanceDescriptor> getTerrains() {
        return terrains;
    }

    public void setTerrains(List<TerrainInstanceDescriptor> terrains) {
        this.terrains = terrains;
    }

}
