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

package com.mbrlabs.mundus.commons.importer;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
public class TerrainDTO {

    private long id;
    private String name;
    private String terraPath;
    private String splatmapPath;

    private long texBase;
    private long texR;
    private long texG;
    private long texB;
    private long texA;

    private int width;
    private int depth;
    private int vertexRes;

    public TerrainDTO() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTerraPath() {
        return terraPath;
    }

    public void setTerraPath(String terraPath) {
        this.terraPath = terraPath;
    }

    public String getSplatmapPath() {
        return splatmapPath;
    }

    public void setSplatmapPath(String splatmapPath) {
        this.splatmapPath = splatmapPath;
    }

    public long getTexBase() {
        return texBase;
    }

    public void setTexBase(long texBase) {
        this.texBase = texBase;
    }

    public long getTexR() {
        return texR;
    }

    public void setTexR(long texR) {
        this.texR = texR;
    }

    public long getTexG() {
        return texG;
    }

    public void setTexG(long texG) {
        this.texG = texG;
    }

    public long getTexB() {
        return texB;
    }

    public void setTexB(long texB) {
        this.texB = texB;
    }

    public long getTexA() {
        return texA;
    }

    public void setTexA(long texA) {
        this.texA = texA;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getVertexRes() {
        return vertexRes;
    }

    public void setVertexRes(int vertexRes) {
        this.vertexRes = vertexRes;
    }


}
