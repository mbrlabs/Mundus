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

package com.mbrlabs.mundus.scene3d;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public class GameObject {

    private long id;
    private String name;

    private Array<String> tags;
    private Array<Component> components;
    private Array<GameObject> childs;
    private GameObject parent;

    private Matrix4 transform;

    public SceneGraph sceneGraph;

    public GameObject(SceneGraph sceneGraph) {
        this.name = "Game Object";
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

    public Array<Component> getComponents() {
        return this.components;
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    public Array<GameObject> getChilds() {
        return this.childs;
    }

    public void addChild(GameObject child) {
        if(this.childs == null) {
            childs = new Array<>();
        }
        childs.add(child);
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

    public void setTransform(Matrix4 transform, boolean copy) {
        if(copy) {
            this.transform.set(transform);
        } else {
            this.transform = transform;
        }
    }

    public void render(float delta) {
        for(Component component : this.components) {
            component.render(delta);
        }

        if(childs != null) {
            for (GameObject node : this.childs) {
                node.render(delta);
            }
        }
    }

    public void update(float delta) {
        for(Component component : this.components) {
            component.update(delta);
        }

        if(childs != null) {
            for(GameObject node : this.childs) {
                node.update(delta);
            }
        }
    }

}
