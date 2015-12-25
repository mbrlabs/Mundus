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

    @Tag(4)
    private float camPosX;
    @Tag(5)
    private float camPosY;
    @Tag(6)
    private float camPosZ;
    @Tag(7)
    private float camDirX = 0;
    @Tag(8)
    private float camDirY = 0;
    @Tag(9)
    private float camDirZ = 0;

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

    public float getCamPosX() {
        return camPosX;
    }

    public void setCamPosX(float camPosX) {
        this.camPosX = camPosX;
    }

    public float getCamPosY() {
        return camPosY;
    }

    public void setCamPosY(float camPosY) {
        this.camPosY = camPosY;
    }

    public float getCamPosZ() {
        return camPosZ;
    }

    public void setCamPosZ(float camPosZ) {
        this.camPosZ = camPosZ;
    }

    public float getCamDirX() {
        return camDirX;
    }

    public void setCamDirX(float camDirX) {
        this.camDirX = camDirX;
    }

    public float getCamDirY() {
        return camDirY;
    }

    public void setCamDirY(float camDirY) {
        this.camDirY = camDirY;
    }

    public float getCamDirZ() {
        return camDirZ;
    }

    public void setCamDirZ(float camDirZ) {
        this.camDirZ = camDirZ;
    }
}
