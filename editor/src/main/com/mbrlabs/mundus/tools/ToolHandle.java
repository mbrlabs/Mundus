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

package com.mbrlabs.mundus.tools;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.tools.picker.PickerColorEncoder;
import com.mbrlabs.mundus.tools.picker.PickerIDAttribute;

/**
 * A tool handle is the visual part of a tool (e.g. x arrow handle of the translate tool).
 *
 * Handles, can be (much like game objects) picked, using the same (color coding technique).
 *
 * @author Marcus Brummer
 * @version 07-03-2016
 */
public abstract class ToolHandle implements Disposable {

    private final int id;
    public final Vector3 position;
    public final Vector3 rotationEuler;
    public final Quaternion rotation;
    public final Vector3 scale;
    public final PickerIDAttribute idAttribute;

    public ToolHandle(int id) {
        this.id = id;
        position = new Vector3();
        rotationEuler = new Vector3();
        rotation = new Quaternion();
        scale = new Vector3();
        idAttribute = new PickerIDAttribute();
        PickerColorEncoder.encodeRaypickColorId(id, idAttribute);
    }

    public abstract void render(ModelBatch batch);
    public abstract void renderPick(ModelBatch modelBatch);

    public abstract void act();
    public abstract void applyTransform();

    public int getId() {
        return this.id;
    }

}
