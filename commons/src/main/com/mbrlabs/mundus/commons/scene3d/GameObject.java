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

    public String name;
    public boolean active;
    private Array<String> tags;
    private Array<Component> components;

    public final SceneGraph sceneGraph;

    /**
     * @param sceneGraph    scene graph
     * @param name          game object name; can be null
     * @param id            game object id
     */
    public GameObject(SceneGraph sceneGraph, String name, int id) {
        super(id);
        this.sceneGraph = sceneGraph;
        this.name = (name == null) ? DEFAULT_NAME : name;
        this.active = true;
        this.tags = null;
        this.components = new Array<Component>(3);
    }
    
    /**
     * Make copy with existing gameObject and new id
     *
     * @param gameObject    game object for clone
     */
    public GameObject(GameObject gameObject, int id) {
        super(gameObject, id);
        this.sceneGraph = gameObject.sceneGraph;

        // set name _copy
        this.name = gameObject.name + "_copy";
        this.active = gameObject.active;

        // copy tags
        if (tags != null) {
            Array<String> newTags = new Array<String>();
            for (String t : gameObject.tags) {
                newTags.add(t);
            }
            this.tags = newTags;
        }

        // copy components
        this.components = new Array<Component>();
        for(Component c : gameObject.components) {
            this.components.add(c.clone(this));
        }
        setParent(gameObject.parent);
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

    /**
     * Returns the tags
     * @return  tags or null if none available
     */
    public Array<String> getTags() {
        return this.tags;
    }

    /**
     * Adds a tag.
     * @param tag tag to add
     */
    public void addTag(String tag) {
        if(this.tags == null) {
            this.tags = new Array<String>(2);
        }

        this.tags.add(tag);
    }

    /**
     * Finds all component by a given type.
     *
     * @param out           output array
     * @param type          component type
     * @param includeChilds search in node children of this game object as well?
     * @return              components found
     */
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

    /**
     * Finds one component by type.
     *
     * @param type  component type
     * @return      component if found or null
     */
    public Component findComponentByType(Component.Type type) {
        for(Component c : components) {
            if(c.getType() == type) return c;
        }

        return null;
    }

    /**
     * Returns all components of this go.
     * @return  components
     */
    public Array<Component> getComponents() {
        return this.components;
    }

    /**
     * Removes a component.
     * @param component component to remove
     */
    public void removeComponent(Component component) {
        components.removeValue(component, true);
    }

    /**
     * Adds a component.
     *
     * @param component component to add
     * @throws InvalidComponentException
     */
    public void addComponent(Component component) throws InvalidComponentException {
        isComponentAddable(component);
        components.add(component);
    }

    /**
     *
     * @param component
     * @throws InvalidComponentException
     */
    public void isComponentAddable(Component component) throws InvalidComponentException {
        // check for component of the same type
        for(Component c : components) {
            if(c.getType() == component.getType()) {
                throw new InvalidComponentException("One Game object can't have more then 1 component of type " + c.getType());
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
        int result = (id ^ (id >>> 16));
        result = 31 * result + name.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return name;
    }

}
