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
 * @version 26-10-2016
 */
public class JsonScene {

    // basics
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String GAME_OBJECTS = "gos";

    // GameObject
    public static final String GO_ID = "i";
    public static final String GO_NAME = "n";
    public static final String GO_ACTIVE = "a";
    public static final String GO_TRANSFORM = "t";
    public static final String GO_TAGS = "g";
    public static final String GO_CHILDREN = "c";
    public static final String GO_MODEL_COMPONENT = "cm";
    public static final String GO_TERRAIN_COMPONENT = "ct";

    // ModelComponent
    public static final String MODEL_COMPONENT_MODEL_ID = "i";
    public static final String MODEL_COMPONENT_MATERIALS = "m";

    // TerrainComponent
    public static final String TERRAIN_COMPONENT_TERRAIN_ID = "i";


}
