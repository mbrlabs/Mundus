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
public class GameObjectDTO {

    private long id;
    private String name;
    private boolean active;

    /** position, rotation, scale */
    private float[] trans = new float[9];

    private ModelComponentDTO modelC;
    private TerrainComponentDTO terrC;

    private GameObjectDTO[] childs;

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

    public float[] getTrans() {
        return trans;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public GameObjectDTO[] getChilds() {
        return childs;
    }

    public void setChilds(GameObjectDTO[] childs) {
        this.childs = childs;
    }

    public ModelComponentDTO getModelC() {
        return modelC;
    }

    public void setModelC(ModelComponentDTO modelC) {
        this.modelC = modelC;
    }

    public TerrainComponentDTO getTerrC() {
        return terrC;
    }

    public void setTerrC(TerrainComponentDTO terrC) {
        this.terrC = terrC;
    }

}
