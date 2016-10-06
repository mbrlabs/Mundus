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
 * Represents a node in a scene graph.
 *
 * Nodes can have child nodes. Each child node defines position, rotation &
 * scale relative to it's parent.
 *
 * @author Marcus Brummer
 * @version 09-06-2016
 */
public interface Node<T extends Node> {

    /**
     * Adds a child to this node.
     *
     * Adds a child to this node and sets this node as parent of the child.
     *
     * @param child
     *            child node to add
     */
    void addChild(T child);

    /**
     * Returns all children.
     *
     * @return all children of this node
     */
    Array<T> getChildren();

    /**
     * Tests if this game object is a child of the other one.
     *
     * @return true if this is a child of other, false otherwise
     */
    boolean isChildOf(GameObject other);

    /**
     * Initializes the array, that holds the child nodes, which would be null
     * otherwise.
     */
    void initChildrenArray();

    /**
     * Returns the parent of this node.
     *
     * @return the parent node of this node
     */
    T getParent();

    /**
     * Sets the parent of this node.
     *
     * @param parent
     *            the parent of this node
     */
    void setParent(T parent);

    /**
     * Removes this node from it's parent.
     */
    void remove();

    /**
     * Returns the position relative to the parent node.
     *
     * @param out
     *            used for storing the result
     * @return local position
     */
    Vector3 getLocalPosition(Vector3 out);

    /**
     * Returns the rotation relative to the parent node.
     *
     * @param out
     *            used for storing the result
     * @return local rotation
     */
    Quaternion getLocalRotation(Quaternion out);

    /**
     * Returns the scale relative to the parent node.
     *
     * @param out
     *            used for storing the result
     * @return local scale
     */
    Vector3 getLocalScale(Vector3 out);

    /**
     * Returns the position in world coordinates.
     *
     * @param out
     *            used for storing the result
     * @return position in world space
     */
    Vector3 getPosition(Vector3 out);

    /**
     * Returns the rotation in world coordinates.
     *
     * @param out
     *            used for storing the result
     * @return rotation in world space
     */
    Quaternion getRotation(Quaternion out);

    /**
     * Returns the scale in world coordinates.
     *
     * @param out
     *            used for storing the result
     * @return scale in world space
     */
    Vector3 getScale(Vector3 out);

    /**
     * Returns the transformation matrix in world space.
     *
     * The matrix is computed by multiplying the transformation matrix of all
     * children with this node.
     *
     * This looks like this: node0 * node1 * node2 * .. * thisNode =
     * transformation matrix of this node in world space
     *
     * @return Accumulated transformation matrix of the scene graph
     */
    Matrix4 getTransform();

    /**
     * Translates the position of this node.
     *
     * @param v
     *            translation vector
     */
    void translate(Vector3 v);

    /**
     * Translates the position of this node.
     *
     * @param x
     *            translation on x axis
     * @param y
     *            translation on y axis
     * @param z
     *            translation on z axis
     */
    void translate(float x, float y, float z);

    /**
     * Rotates this node.
     *
     * @param q
     *            rotation to be applied
     */
    void rotate(Quaternion q);

    /**
     * Rotates this node.
     *
     * @param x
     *            x component of a quaternion
     * @param y
     *            y component of a quaternion
     * @param z
     *            z component of a quaternion
     * @param w
     *            w component of a quaternion
     */
    void rotate(float x, float y, float z, float w);

    /**
     * Scales this node.
     *
     * @param v
     *            scale vector
     */
    void scale(Vector3 v);

    /**
     * Scales this node.
     *
     * @param x
     *            scale on x axis
     * @param y
     *            scale on y axis
     * @param z
     *            scale on z axis
     */
    void scale(float x, float y, float z);

    /**
     * Sets the position relative to the parent node.
     *
     * @param x
     *            translation on the x axis
     * @param y
     *            translation on the y axis
     * @param z
     *            translation on the z axis
     */
    void setLocalPosition(float x, float y, float z);

    /**
     * Sets the rotation relative to the parent node.
     *
     * @param x
     *            x component of a quaternion
     * @param y
     *            y component of a quaternion
     * @param z
     *            z component of a quaternion
     * @param w
     *            w component of a quaternion
     */
    void setLocalRotation(float x, float y, float z, float w);

    /**
     * Sets the scale relative to the parent node.
     *
     * @param x
     *            scale on the x axis
     * @param y
     *            scale on the y axis
     * @param z
     *            scale on the z axis
     */
    void setLocalScale(float x, float y, float z);

}
