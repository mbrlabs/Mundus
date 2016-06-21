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
public abstract class AbstractComponent implements Component {

    public GameObject gameObject;
    protected Type type;

    public AbstractComponent(GameObject go) {
        this.gameObject = go;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public GameObject getGameObject() {
        return this.gameObject;
    }

    @Override
    public void remove() {
        gameObject.removeComponent(this);
    }

}
