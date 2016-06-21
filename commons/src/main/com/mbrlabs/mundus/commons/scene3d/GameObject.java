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

import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.traversal.DepthFirstIterator;

import java.util.Iterator;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public class GameObject extends SimpleNode<GameObject> implements Iterable<GameObject> {

    public static final String DEFAULT_NAME = "GameObject";

    private int id;
    private String name;
    private boolean active;
    private Array<String> tags;
    private Array<Component> components;

    public final SceneGraph sceneGraph;

    public GameObject(SceneGraph sceneGraph) {
        super();
        this.sceneGraph = sceneGraph;
        this.name = DEFAULT_NAME;
        this.active = true;
        this.id = -1;
        this.tags = null;
        this.components = new Array<Component>(3);
    }

    public GameObject(SceneGraph sceneGraph, String name, int id) {
        this(sceneGraph);
        this.name = name;
        this.id = id;
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

            if (getChildren() != null) {
                for (GameObject node : getChildren()) {
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

            if (getChildren() != null) {
                for (GameObject node : getChildren()) {
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
        // check for component of the same type
        for(Component c : components) {
            if(c.getType() == component.getType()) {
                throw new InvalidComponentException("One Game object can't have more then 1 component of type " + c.getType());
            }
        }
    }

    /**
     * Tests if this game object is a child of the other one.
     *
     * @return  true if this is a child of other, false otherwise
     */
    public boolean isChildOf(GameObject other) {
        for(GameObject go : other) {
            if(go.getId() == this.id) return true;
        }

        return false;
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
