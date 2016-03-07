/*
 * Copyright (c) 2016. See AUTHORS file.
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

package com.mbrlabs.mundus.tools.picker;

import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.tools.picker.PickerIDAttribute;

/**
 * Encodes/Decides game object ids to rgb color values.
 *
 * @author Marcus Brummer
 * @version 21-02-2016
 */
public class PickerColorEncoder {

    /**
     * Decodes a rgba8888 color code to a game object id.
     *
     * @param   rgba8888Code  rgba8888 color code
     * @return  game object id
     */
    public static int decode(int rgba8888Code) {
        int id = (rgba8888Code & 0xFF000000) >>> 24;
        id += ((rgba8888Code & 0x00FF0000) >>> 16) * 256;
        id += ((rgba8888Code & 0x0000FF00) >>> 8) * 256 * 256;

        return id;
    }

    /**
     * Encodes a game object id to a GameObjectIdAttribute with rgb channels.
     *
     * @param   go  game object, who's id must be encoded
     * @return  the game object id, encoded as rgb values
     */
    public static PickerIDAttribute encodeRaypickColorId(GameObject go) {
        PickerIDAttribute goIDa = new PickerIDAttribute();
        encodeRaypickColorId(go.getId(), goIDa);
        return goIDa;
    }

    /**
     * Encodes a id to a GameObjectIdAttribute with rgb channels.
     *
     * @param id        id
     * @param out       encoded id as attribute
     */
    public static void encodeRaypickColorId(int id, PickerIDAttribute out) {
        out.r = id & 0x000000FF;
        out.g = (id & 0x0000FF00) >>> 8;
        out.b = (id & 0x00FF0000) >>> 16;
    }


}
