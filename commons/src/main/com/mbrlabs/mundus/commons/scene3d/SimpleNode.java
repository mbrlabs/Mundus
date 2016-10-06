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

/**
 * Very simple and incredible inefficient implementation of a scene graph node.
 *
 * Inefficient, because each call to getTransform() multiplies all parent
 * transformation matrices without caching them.
 *
 * @author Marcus Brummer
 * @version 09-06-2016
 */
public class SimpleNode<T extends SimpleNode> extends BaseNode<T> {

    static boolean WORLD_SPACE_TRANSFORM = true;
    private static Matrix4 tempMat = new Matrix4();

    private Vector3 localPosition;
    private Quaternion localRotation;
    private Vector3 localScale;

    // root * p0 * p1 * localMat = combined (absolute transfrom)
    private Matrix4 combined;

    public SimpleNode(int id) {
        super(id);
        localPosition = new Vector3();
        localRotation = new Quaternion();
        localScale = new Vector3(1, 1, 1);
        combined = new Matrix4();
    }

    /**
     * Copy construction
     * 
     * @param simpleNode
     * @param id
     */
    public SimpleNode(SimpleNode simpleNode, int id) {
        super(id);
        this.localPosition = new Vector3(simpleNode.localPosition);
        this.localRotation = new Quaternion(simpleNode.localRotation);
        this.localScale = new Vector3(simpleNode.localScale);
        this.combined = new Matrix4(simpleNode.combined);
    }

    @Override
    public Vector3 getLocalPosition(Vector3 out) {
        return out.set(localPosition);
    }

    @Override
    public Quaternion getLocalRotation(Quaternion out) {
        return out.set(localRotation);
    }

    @Override
    public Vector3 getLocalScale(Vector3 out) {
        return out.set(localScale);
    }

    @Override
    public Vector3 getPosition(Vector3 out) {
        return getTransform().getTranslation(out);
    }

    @Override
    public Quaternion getRotation(Quaternion out) {
        return getTransform().getRotation(out);
    }

    @Override
    public Vector3 getScale(Vector3 out) {
        return getTransform().getScale(out);
    }

    @Override
    public Matrix4 getTransform() {
        if (parent == null) {
            return combined.set(localPosition, localRotation, localScale);
        }

        combined.set(localPosition, localRotation, localScale);
        return combined.mulLeft(parent.getTransform());
    }

    @Override
    public void translate(Vector3 v) {
        localPosition.add(v);
    }

    @Override
    public void translate(float x, float y, float z) {
        localPosition.add(x, y, z);
    }

    @Override
    public void rotate(Quaternion q) {
        localRotation.mulLeft(q);
    }

    @Override
    public void rotate(float x, float y, float z, float w) {
        localRotation.mulLeft(x, y, z, w);
    }

    @Override
    public void scale(Vector3 v) {
        localScale.scl(v);
    }

    @Override
    public void scale(float x, float y, float z) {
        localScale.scl(x, y, z);
    }

    @Override
    public void setLocalPosition(float x, float y, float z) {
        localPosition.set(x, y, z);
    }

    @Override
    public void setLocalRotation(float x, float y, float z, float w) {
        localRotation.set(x, y, z, w);
    }

    @Override
    public void setLocalScale(float x, float y, float z) {
        localScale.set(x, y, z);
    }

}
