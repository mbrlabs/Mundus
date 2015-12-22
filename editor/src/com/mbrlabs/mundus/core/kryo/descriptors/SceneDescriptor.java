package com.mbrlabs.mundus.core.kryo.descriptors;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;


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

}
