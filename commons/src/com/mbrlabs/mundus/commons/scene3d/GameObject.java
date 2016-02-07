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

import com.badlogic.gdx.math.Matrix4;
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
    private static Vector3 tempVec1 = new Vector3();

    private long id;

    private String name;
    private boolean active;
    private Array<String> tags;
    private Array<Component> components;
    private Array<GameObject> childs;

    private GameObject parent;

    public Matrix4 transform;

    public SceneGraph sceneGraph;

    public GameObject(SceneGraph sceneGraph) {
        this.name = DEFAULT_NAME;
        this.active = true;
        this.id = -1;
        this.tags = null;
        this.childs = null;
        this.components = new Array<>(3);
        this.transform = new Matrix4();
        this.sceneGraph = sceneGraph;
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
            this.tags = new Array<>(2);
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
            childs = new Array<>();
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

    public void setTranslation(float x, float y, float z) {
        transform.getTranslation(tempVec0);
        float offsetX = x - tempVec0.x;
        float offsetY = y - tempVec0.y;
        float offsetZ = z - tempVec0.z;
        transform.translate(offsetX, offsetY, offsetZ);

        if (childs != null) {
            for (GameObject node : this.childs) {
                node.translate(offsetX, offsetY, offsetZ);
            }
        }
    }

    public void translate(float x, float y, float z) {
        transform.translate(x, y, z);

        if (childs != null) {
            for (GameObject node : this.childs) {
                node.translate(x, y, z);
            }
        }
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
