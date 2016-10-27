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

package com.mbrlabs.mundus.commons.scene3d.components;

import com.mbrlabs.mundus.commons.scene3d.GameObject;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public interface Component {

    enum Type {
        MODEL, TERRAIN, LIGHT, PARTICLE_SYSTEM
    }

    GameObject getGameObject();

    void render(float delta);

    void update(float delta);

    Type getType();

    void setType(Type type);

    void remove();

    Component clone(GameObject go);
}
