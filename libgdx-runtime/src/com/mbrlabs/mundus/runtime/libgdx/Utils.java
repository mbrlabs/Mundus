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

package com.mbrlabs.mundus.runtime.libgdx;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.model.MModel;
import com.mbrlabs.mundus.commons.model.MTexture;
import com.mbrlabs.mundus.commons.terrain.Terrain;

/**
 * @author Marcus Brummer
 * @version 10-02-2016
 */
public class Utils {

    public static MTexture findTextureById(Array<MTexture> textures, Long id) {
        if (id == null) return null;
        for (MTexture t : textures) {
            if (t.getId() == id) return t;
        }

        return null;
    }

    public static Terrain findTerrainById(Array<Terrain> terrains, Long id) {
        if (id == null) return null;
        for (Terrain t : terrains) {
            if (t.id == id) return t;
        }

        return null;
    }

    // TODO save models in map to remove the search every time
    public static Model findModelById(Array<MModel> models, Long id) {
        if (id == null) return null;
        for (MModel m : models) {
            if (m.id == id) return m.getModel();
        }

        return null;
    }

}
