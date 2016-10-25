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

package com.mbrlabs.mundus.commons.assets.meta;

/**
 *
 * @author Marcus Brummer
 * @version 26-10-2016
 */
public class MetaTerrain {

    public static final String JSON_SIZE = "size";
    public static final String JSON_SPLATMAP = "map";
    public static final String JSON_SPLAT_BASE = "base";
    public static final String JSON_SPLAT_R = "r";
    public static final String JSON_SPLAT_G = "g";
    public static final String JSON_SPLAT_B = "b";
    public static final String JSON_SPLAT_A = "a";

    private int size;
    private String splatmap;
    private String splatBase;
    private String splatR;
    private String splatG;
    private String splatB;
    private String splatA;

    public String getSplatmap() {
        return splatmap;
    }

    public void setSplatmap(String splatmap) {
        this.splatmap = splatmap;
    }

    public String getSplatBase() {
        return splatBase;
    }

    public void setSplatBase(String splatBase) {
        this.splatBase = splatBase;
    }

    public String getSplatR() {
        return splatR;
    }

    public void setSplatR(String splatR) {
        this.splatR = splatR;
    }

    public String getSplatG() {
        return splatG;
    }

    public void setSplatG(String splatG) {
        this.splatG = splatG;
    }

    public String getSplatB() {
        return splatB;
    }

    public void setSplatB(String splatB) {
        this.splatB = splatB;
    }

    public String getSplatA() {
        return splatA;
    }

    public void setSplatA(String splatA) {
        this.splatA = splatA;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
