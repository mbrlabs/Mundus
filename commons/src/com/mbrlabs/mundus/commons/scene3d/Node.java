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

package com.mbrlabs.mundus.commons.scene3d;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * @author Marcus Brummer
 * @version 09-06-2016
 */
public interface Node<T extends Node> {

    void addChild(T child);
    Array<T> getChildren();
    T getParent();
    void setParent(T parent);
    void remove();

    Vector3 getLocalPosition(Vector3 out);
    Quaternion getLocalRotation(Quaternion out);
    Vector3 getLocalScale(Vector3 out);

    Vector3 getPosition(Vector3 out);
    Quaternion getRotation(Quaternion out);
    Vector3 getScale(Vector3 out);

    Matrix4 toMatrix();

    void translate(Vector3 v);
    void translate(float x, float y, float z);
    void rotate(Quaternion q);
    void rotate(float x, float y, float z, float w);
    void scale(Vector3 v);
    void scale(float x, float y, float z);


    void setLocalPosition(float x, float y, float z);

}
