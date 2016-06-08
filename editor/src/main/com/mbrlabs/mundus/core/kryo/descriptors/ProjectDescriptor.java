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

package com.mbrlabs.mundus.core.kryo.descriptors;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 17-12-2015
 */
public class ProjectDescriptor {

    @Tag(0)
    private String name;
    @Tag(1)
    private int nextAvailableID;
    @Tag(2)
    private List<ModelDescriptor> models;
    @Tag(3)
    private List<TextureDescriptor> textures;
    @Tag(4)
    private List<TerrainDescriptor> terrains;
    @Tag(5)
    private List<String> sceneNames;
    @Tag(6)
    private String currentSceneName;

    public ProjectDescriptor() {
        models = new ArrayList<>();
        terrains = new ArrayList<>();
        sceneNames = new ArrayList<>();
        textures = new ArrayList<>();
    }

    public List<ModelDescriptor> getModels() {
        return models;
    }

    public List<TerrainDescriptor> getTerrains() {
        return terrains;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNextAvailableID() {
        return nextAvailableID;
    }

    public void setNextAvailableID(int nextAvailableID) {
        this.nextAvailableID = nextAvailableID;
    }

    public List<String> getSceneNames() {
        return sceneNames;
    }

    public void setSceneNames(List<String> sceneNames) {
        this.sceneNames = sceneNames;
    }

    public String getCurrentSceneName() {
        return currentSceneName;
    }

    public void setCurrentSceneName(String currentSceneName) {
        this.currentSceneName = currentSceneName;
    }

    public List<TextureDescriptor> getTextures() {
        return textures;
    }

}
