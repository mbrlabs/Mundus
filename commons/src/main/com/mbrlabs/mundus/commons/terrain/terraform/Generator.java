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

package com.mbrlabs.mundus.commons.terrain.terraform;

import com.mbrlabs.mundus.commons.terrain.Terrain;

/**
 *
 * @author Marcus Brummer
 * @version 20-06-2016
 */
public abstract class Generator<T extends Generator<T>> {

    protected Terrain terrain;
    protected float minHeight = 0;
    protected float maxHeight = 50;

    Generator(Terrain terrain) {
        this.terrain = terrain;
    }

    public T minHeight(float min) {
        this.minHeight = min;
        return (T) this;
    }

    public T maxHeight(float max) {
        this.maxHeight = max;
        return (T) this;
    }

    public abstract void terraform();

}
