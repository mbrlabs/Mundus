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

package com.mbrlabs.mundus.core.kryo.descriptors;

import java.util.ArrayList;
import java.util.List;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;

/**
 * @author Marcus Brummer
 * @version 17-12-2015
 */
public class ProjectDescriptor {

    @Tag(0)
    private List<ModelDescriptor> models;
    @Tag(1)
    private List<TerrainDescriptor> terrains;
    @Tag(2)
    private List<SceneDescriptor> scenes;
    @Tag(3)
    private long currentSceneID;
    @Tag(4)
    private String name;
    @Tag(5)
    private String id;
    @Tag(6)
    private long nextAvailableID;

    public ProjectDescriptor() {
        models = new ArrayList<>();
        terrains = new ArrayList<>();
        scenes = new ArrayList<>();
    }

    public List<ModelDescriptor> getModels() {
        return models;
    }

    public List<TerrainDescriptor> getTerrains() {
        return terrains;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNextAvailableID() {
        return nextAvailableID;
    }

    public void setNextAvailableID(long nextAvailableID) {
        this.nextAvailableID = nextAvailableID;
    }

    public List<SceneDescriptor> getScenes() {
        return scenes;
    }

    public long getCurrentSceneID() {
        return currentSceneID;
    }

    public void setCurrentSceneID(long currentSceneID) {
        this.currentSceneID = currentSceneID;
    }

}
