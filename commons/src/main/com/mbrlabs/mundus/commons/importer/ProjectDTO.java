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

package com.mbrlabs.mundus.commons.importer;

/**
 * @author Marcus Brummer
 * @version 25-01-2016
 */
public class ProjectDTO {

    private String name;

    private ModelDTO[] models;
    private TextureDTO[] textures;
    private TerrainDTO[] terrains;
    private SceneDTO[] scenes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModelDTO[] getModels() {
        return models;
    }

    public void setModels(ModelDTO[] models) {
        this.models = models;
    }

    public TextureDTO[] getTextures() {
        return textures;
    }

    public void setTextures(TextureDTO[] textures) {
        this.textures = textures;
    }

    public TerrainDTO[] getTerrains() {
        return terrains;
    }

    public void setTerrains(TerrainDTO[] terrains) {
        this.terrains = terrains;
    }

    public SceneDTO[] getScenes() {
        return scenes;
    }

    public void setScenes(SceneDTO[] scenes) {
        this.scenes = scenes;
    }

}
