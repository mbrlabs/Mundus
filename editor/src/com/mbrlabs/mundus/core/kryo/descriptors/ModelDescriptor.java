package com.mbrlabs.mundus.core.kryo.descriptors;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;

/**
 * @author Marcus Brummer
 * @version 17-12-2015
 */
public class ModelDescriptor {

    @Tag(0)
    private long id;
    @Tag(1)
    private String name;
    @Tag(2)
    private String g3dbPath;

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

    public String getG3dbPath() {
        return g3dbPath;
    }

    public void setG3dbPath(String g3dbPath) {
        this.g3dbPath = g3dbPath;
    }

}
