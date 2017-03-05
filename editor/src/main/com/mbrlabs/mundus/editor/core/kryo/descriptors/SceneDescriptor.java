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

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;

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
    private List<GameObjectDescriptor> gameObjects;
    @Tag(3)
    private FogDescriptor fog;
    @Tag(4)
    private BaseLightDescriptor ambientLight;
    @Tag(5)
    private float camPosX;
    @Tag(6)
    private float camPosY;
    @Tag(7)
    private float camPosZ;
    @Tag(8)
    private float camDirX = 0;
    @Tag(9)
    private float camDirY = 0;
    @Tag(10)
    private float camDirZ = 0;

    public SceneDescriptor() {
        gameObjects = new ArrayList<>();
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

    public FogDescriptor getFog() {
        return fog;
    }

    public void setFog(FogDescriptor fog) {
        this.fog = fog;
    }

    public BaseLightDescriptor getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(BaseLightDescriptor ambientLight) {
        this.ambientLight = ambientLight;
    }

    public List<GameObjectDescriptor> getGameObjects() {
        return gameObjects;
    }

    public void setGameObjects(List<GameObjectDescriptor> gameObjects) {
        this.gameObjects = gameObjects;
    }
}
