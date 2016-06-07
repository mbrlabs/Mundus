package com.mbrlabs.mundus.core.kryo.descriptors;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;

/***
 * @author Marcus Brummer
 * @version 07-06-2016
 */
public class ProjectRefDescriptor {

    @TaggedFieldSerializer.Tag(0)
    private String name;
    @TaggedFieldSerializer.Tag(1)
    private String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
