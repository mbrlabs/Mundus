/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.editor.core.kryo.descriptors;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 18-01-2016
 */
public class GameObjectDescriptor {

    @Tag(0)
    private int id;
    @Tag(1)
    private String name;
    @Tag(2)
    private boolean active;

    @Tag(3)
    private float[] transform = new float[10];

    @Tag(4)
    private List<String> tags;
    @Tag(5)
    private List<GameObjectDescriptor> childs;

    @Tag(6)
    private ModelComponentDescriptor modelComponent;
    @Tag(7)
    private TerrainComponentDescriptor terrainComponent;

    public GameObjectDescriptor() {
        childs = new ArrayList<>();
        tags = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public float[] getTransform() {
        return transform;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<GameObjectDescriptor> getChilds() {
        return childs;
    }

    public void setChilds(List<GameObjectDescriptor> childs) {
        this.childs = childs;
    }

    public ModelComponentDescriptor getModelComponent() {
        return modelComponent;
    }

    public void setModelComponent(ModelComponentDescriptor modelComponent) {
        this.modelComponent = modelComponent;
    }

    public TerrainComponentDescriptor getTerrainComponent() {
        return terrainComponent;
    }

    public void setTerrainComponent(TerrainComponentDescriptor terrainComponent) {
        this.terrainComponent = terrainComponent;
    }

}
