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

package com.mbrlabs.mundus.commons.scene3d;

import com.badlogic.gdx.math.MathUtils;
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
    private static Vector3 tempVec0 = new Vector3();
    private static Quaternion tempQuat = new Quaternion();

    private long id;

    private String name;
    private boolean active;
    private Array<String> tags;
    private Array<Component> components;
    private Array<GameObject> childs;

    private Matrix4 transform;
    public Vector3 position;
    public Quaternion rotation;
    public Vector3 scale;

    private GameObject parent;

    public SceneGraph sceneGraph;

    public GameObject(SceneGraph sceneGraph) {
        this.name = DEFAULT_NAME;
        this.active = true;
        this.id = -1;
        this.tags = null;
        this.childs = null;
        this.components = new Array<Component>(3);
        this.sceneGraph = sceneGraph;

        this.transform = new Matrix4();
        this.position = new Vector3(0, 0, 0);
        this.scale = new Vector3(1, 1, 1);
        this.rotation = new Quaternion();
    }

    public GameObject(SceneGraph sceneGraph, String name, long id) {
        this(sceneGraph);
        this.name = name;
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
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
        // no component for root
        if(id == -1) throw new InvalidComponentException("Can't add component to the root.");

        // check for component of the same type
        for(Component c : components) {
            if(c.getType() == component.getType()) {
                throw new InvalidComponentException("One Game object can't have more then 1 component of type " + c.getType());
            }
        }

        // TerrainComponents only in GO, directly under root
        if(getParent().id != -1 && component.getType() == Component.Type.TERRAIN) {
            throw new InvalidComponentException("Terrain components can only be applied to direct children of the root");
        }
    }

    public Array<GameObject> getChilds() {
        return this.childs;
    }

    public void addChild(GameObject child) {
        if(this.childs == null) {
            childs = new Array<GameObject>();
        }
        child.setParent(this);
        childs.add(child);
    }

    public boolean remove() {
        if(parent != null) {
            parent.getChilds().removeValue(this, true);
            return true;
        }

        return false;
    }

    public GameObject getParent() {
        return this.parent;
    }

    public void setParent(GameObject parent) {
        this.parent = parent;
    }

    public Matrix4 getTransform() {
        return this.transform;
    }

    public void setTransform(Matrix4 transform) {
        this.transform = transform;
        transform.getTranslation(position);
        transform.getRotation(rotation);
        transform.getScale(scale);
    }

    public void setTranslation(float x, float y, float z, boolean globalSpace) {
        this.position.set(x, y, z);
        calculateTransform();

//        transform.setToScaling(scale);
//        transform.rotate(rot);
//        transform.trn(x, y, z);
//
//        if(globalSpace) {
//            transform.trn(x, y, z);
//        } else {
//            transform.translate(x, y, z);
//        }

        if (childs != null) {
            for (GameObject node : this.childs) {
                node.setTranslation(x, y, z, globalSpace);
            }
        }
    }

    public void translate(float x, float y, float z, boolean globalSpace) {
        position.add(x, y, z);
        transform.trn(x, y, z);

//        if(globalSpace) {
//            transform.trn(x, y, z);
//        } else {
//            transform.translate(x, y, z);
//        }

        if (childs != null) {
            for (GameObject node : this.childs) {
                node.translate(x, y, z, globalSpace);
            }
        }
    }

    public void setRotation(float x, float y, float z) {
        rotation.setEulerAngles(y, x, z);
        calculateTransform();

//        transform.setToScaling(scale);
//        transform.rotate(rot);
//        transform.trn(pos);

        if (childs != null) {
            for (GameObject node : this.childs) {
                node.setRotation(x, y, z);
            }
        }
    }

    public void rotate(float x, float y, float z) {
        tempQuat.setEulerAngles(y, x, z);
        rotation.add(tempQuat);
        transform.rotate(tempQuat);

        if (childs != null) {
            for (GameObject node : this.childs) {
                node.rotate(x, y, z);
            }
        }
    }

    public void scale(float x, float y, float z) {

    }

    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
        transform.setToTranslation(position);
        transform.rotate(rotation);
        transform.scl(scale);

//        transform.setToScaling(x, y, z);
//        transform.rotate(rot);
//        transform.trn(pos);

        if (childs != null) {
            for (GameObject node : this.childs) {
                node.setScale(x, y, z);
            }
        }
    }

    public void calculateTransform() {
//        transform.setToTranslation(position);
//        transform.rotate(rotation);
//        transform.scl(scale);
        transform.set(position, rotation, scale);
    }

    public Vector3 calculateMedium(Vector3 out) {
        transform.getTranslation(out);
        if(childs == null) return out;

        for(GameObject go : childs) {
            go.transform.getTranslation(tempVec0);
            out.add(tempVec0);
        }
        return out.scl(1f / (childs.size+1));
    }

    public void render(float delta) {
        if(active) {
            for (Component component : this.components) {
                component.render(delta);
            }

            if (childs != null) {
                for (GameObject node : this.childs) {
                    node.render(delta);
                }
            }
        }
    }

    public void update(float delta) {
        if(active) {
            for (Component component : this.components) {
                component.update(delta);
            }

            if (childs != null) {
                for (GameObject node : this.childs) {
                    node.update(delta);
                }
            }
        }
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
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        return result;
    }

}
