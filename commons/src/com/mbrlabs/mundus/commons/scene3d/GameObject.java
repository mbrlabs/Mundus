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
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.traversal.DepthFirstIterator;

import java.util.Iterator;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public class GameObject implements Iterable<GameObject> {

    public static final String DEFAULT_NAME = "GameObject";
    private static Quaternion tempQuat = new Quaternion();
    private static Vector3 tempVec = new Vector3();

    private int id;
    private String name;
    private boolean active;
    private Array<String> tags;
    private Array<Component> components;
    private Array<GameObject> children;
    private GameObject parent;
    public SceneGraph sceneGraph;

    private Matrix4 transform;
    public Vector3 position;
    public Vector3 rotation;
    public Vector3 scale;

    public GameObject(SceneGraph sceneGraph) {
        this.name = DEFAULT_NAME;
        this.active = true;
        this.id = -1;
        this.tags = null;
        this.children = null;
        this.components = new Array<Component>(3);
        this.sceneGraph = sceneGraph;

        this.transform = new Matrix4();
        this.position = new Vector3(0, 0, 0);
        this.scale = new Vector3(1, 1, 1);
        this.rotation = new Vector3();
    }

    public GameObject(SceneGraph sceneGraph, String name, int id) {
        this(sceneGraph);
        this.name = name;
        this.id = id;
    }

    /**
     * Returns the position, relative to the parent node.
     *
     * @param out   vector for storing the position
     * @return      vector, containing the relative position
     */
    public Vector3 getPositionRel(Vector3 out) {
        if(parent == null) return out.set(position);
        return out.set(position).sub(parent.position);
    }

    /**
     * Sets the absolute position in world coordinates.
     *
     * @param x     x axis position
     * @param y     y axis position
     * @param z     z axis position
     */
    public void setPosition(float x, float y, float z) {
        final Vector3 diff = tempVec.set(x, y, z).sub(position);
        this.position.set(x, y, z);

        calculateTransform();

        if (children != null) {
            for (GameObject node : this.children) {
                node.translate(diff.x, diff.y, diff.z);
            }
        }
    }

    /**
     * Sets the position relative to the parent node.
     *
     * @param x     x axis position
     * @param y     y axis position
     * @param z     z axis position
     */
    public void setPositionRel(float x, float y, float z) {
        if(parent == null) {
            setPosition(x, y, z);
        } else {
            setPosition(parent.position.x + x, parent.position.y + y, parent.position.z + z);
        }
    }

    /**
     * Translates the game object
     *
     * @param x     x axis translation
     * @param y     y axis translation
     * @param z     z axis translation
     */
    public void translate(float x, float y, float z) {
        position.add(x, y, z);
        //transform.trn(x, y, z);
        calculateTransform();

        if (children != null) {
            for (GameObject node : this.children) {
                node.translate(x, y, z);
            }
        }
    }

    /**
     * Returns the rotation in euler angles, relative to the parent node.
     *
     * @param out   vector for storing the rotation
     * @return      vector, containing the rotation
     */
    public Vector3 getRotationRel(Vector3 out) {
        if(parent == null) return out.set(rotation);
        return out.set(rotation).sub(parent.rotation);
    }

    /**
     * Sets the rotation (euler angles) in world space.
     *
     * @param x     x axis rotation
     * @param y     y axis rotation
     * @param z     z axis rotation
     */
    public void setRotation(float x, float y, float z) {
        final Vector3 diff = tempVec.set(x, y, z).sub(rotation);
        this.rotation.set(x, y, z);
        calculateTransform();

        if (children != null) {
            for (GameObject node : this.children) {
                node.rotate(diff.x, diff.y, diff.z);
            }
        }
    }

    /**
     * Sets the rotation (euler angles) relative to the parent node.
     *
     * @param x     x axis rotation
     * @param y     y axis rotation
     * @param z     z axis rotation
     */
    public void setRotationRel(float x, float y, float z) {
        if(parent == null) {
            setRotation(x, y, z);
        } else {
            setRotation(parent.rotation.x + x, parent.rotation.y + y, parent.rotation.z + z);
        }
    }

    /**
     * Rotates the game object.
     *
     * @param x     x axis rotation
     * @param y     y axis rotation
     * @param z     z axis rotation
     */
    public void rotate(float x, float y, float z) {
        rotation.add(x, y, z);
        calculateTransform();

        if (children != null) {
            for (GameObject node : this.children) {
                node.rotate(x, y, z);
            }
        }
    }

    /**
     * Sets the scale in world coordinates.
     *
     * @param x     x axis scale
     * @param y     y axis scale
     * @param z     z axis scale
     */
    public void setScale(float x, float y, float z) {
        final Vector3 diff = tempVec.set(x / scale.x, y / scale.y, z / scale.z);
        this.scale.set(x, y, z);
        calculateTransform();

        if (children != null) {
            for (GameObject node : this.children) {
                node.scale(diff.x, diff.y, diff.z);
            }
        }
    }

    /**
     * Sets the scaling relative to the parent node.
     *
     * @param x     x axis scale
     * @param y     y axis scale
     * @param z     z axis scale
     */
    public void setScaleRel(float x, float y, float z) {
        if(parent == null) {
            setScale(x, y, z);
        } else {
            setScale(parent.scale.x * x, parent.scale.y * y, parent.scale.z * z);
        }
    }

    /**
     * Scales the game object.
     *
     * @param x     x axis scale
     * @param y     y axis scale
     * @param z     z axis scale
     */
    public void scale(float x, float y, float z) {
        scale.scl(x, y, z);
        calculateTransform();

        if(children != null) {
            for(GameObject c : children) {
                c.scale(x, y, z);
            }
        }

    }

    /**
     * Returns the scaling relative to the parent node.
     *
     * @param out   vector for storing the scale
     * @return      vector, containing the scale
     */
    public Vector3 getScaleRel(Vector3 out) {
        if(parent == null) return out.set(scale);
        out.set(scale.x / parent.scale.x, scale.y / parent.scale.y , scale.z / parent.scale.z);

        return out;
    }

    /**
     * Sets position, rotation & scale in world coordinates.
     *
     * @param transform     transformation matrix
     */
    public void setTransform(Matrix4 transform) {
        this.transform = transform;
        transform.getTranslation(position);
        transform.getRotation(tempQuat);
        rotation.set(tempQuat.getPitch(), tempQuat.getYaw(), tempQuat.getRoll());
        transform.getScale(scale);
    }

    /**
     * Recalculates the transformation matrix.
     */
    public void calculateTransform() {
        tempQuat.setEulerAngles(rotation.y, rotation.x, rotation.z);
        transform.set(position, tempQuat, scale);
    }

    /**
     * Returns the transformation matrix.
     *
     * @return transformation matrix of this game object
     */
    public Matrix4 getTransform() {
        return this.transform;
    }

    /**
     * Calculates the unweighted medium position of this node and all it's children.
     *
     * @param out   input vector
     * @return      input vector containing the medium position
     */
    public Vector3 calculateMedium(Vector3 out) {
        out.set(position);
        if(children == null) return out;

        for(GameObject go : children) {
            out.add(go.position);
        }
        return out.scl(1f / (children.size+1));
    }

    /**
     * Calculates the weighted (by scale) medium position of this node and all it's children.
     *
     * @param out   input vector
     * @return      input vector containing the medium position
     */
    public Vector3 calculateWeightedMedium(Vector3 out) {
        out.set(position);
        if(children == null) return out;

        for(GameObject go : children) {
            tempVec.set(go.position).scl(go.scale);
            out.add(tempVec);
        }
        return out.scl(1f / (children.size+1));
    }

    /**
     * Calls the render() method for each component in this and all child nodes.
     *
     * @param delta     time since last render
     */
    public void render(float delta) {
        if(active) {
            for (Component component : this.components) {
                component.render(delta);
            }

            if (children != null) {
                for (GameObject node : this.children) {
                    node.render(delta);
                }
            }
        }
    }

    /**
     * Calls the update() method for each component in this and all child nodes.
     *
     * @param delta     time since last update
     */
    public void update(float delta) {
        if(active) {
            for (Component component : this.components) {
                component.update(delta);
            }

            if (children != null) {
                for (GameObject node : this.children) {
                    node.update(delta);
                }
            }
        }
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Array<String> getTags() {
        return this.tags;
    }

    public SceneGraph getSceneGraph() {
        return sceneGraph;
    }

    public void addTag(String tag) {
        if(this.tags == null) {
            this.tags = new Array<String>(2);
        }

        this.tags.add(tag);
    }

    public GameObject getParent() {
        return this.parent;
    }

    public void setParent(GameObject parent) {
        this.parent = parent;
    }

    public Array<Component> findComponentsByType(Array<Component> out, Component.Type type, boolean includeChilds) {
        if(includeChilds) {
            for(GameObject go : this) {
                for(Component c : go.components) {
                    if(c.getType() == type) out.add(c);
                }
            }
        } else {
            for(Component c : components) {
                if(c.getType() == type) out.add(c);
            }
        }

        return out;
    }

    public Component findComponentByType(Component.Type type) {
        for(Component c : components) {
            if(c.getType() == type) return c;
        }

        return null;
    }

    public Array<Component> getComponents() {
        return this.components;
    }

    public void removeComponent(Component component) {
        components.removeValue(component, true);
    }

    public void addComponent(Component component) throws InvalidComponentException {
        isComponentAddable(component);
        components.add(component);
    }

    public void isComponentAddable(Component component) throws InvalidComponentException {
        // check for component of the same type
        for(Component c : components) {
            if(c.getType() == component.getType()) {
                throw new InvalidComponentException("One Game object can't have more then 1 component of type " + c.getType());
            }
        }
    }

    public Array<GameObject> getChildren() {
        return this.children;
    }

    public boolean isChildOf(GameObject other) {
        for(GameObject go : other) {
            if(go.getId() == this.id) return true;
        }

        return false;
    }

    public void addChild(GameObject child) {
        if(this.children == null) {
            children = new Array<GameObject>();
        }
        child.setParent(this);
        children.add(child);
    }

    public boolean remove() {
        if(parent != null) {
            parent.getChildren().removeValue(this, true);
            return true;
        }
        sceneGraph.getGameObjects().removeValue(this, true);
        return true;
    }

    @Override
    public Iterator<GameObject> iterator() {
        return new DepthFirstIterator(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameObject that = (GameObject) o;

        if (id != that.id) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (id ^ (id >>> 16));
        result = 31 * result + name.hashCode();
        return result;
    }

}
