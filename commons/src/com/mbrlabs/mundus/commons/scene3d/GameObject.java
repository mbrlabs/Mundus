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
import com.badlogic.gdx.utils.Array;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public class GameObject implements Node {

    private long id;
    private String name;

    private Array<String> tags;
    private Array<Component> components;
    private Array<Node> childs;
    private Node parent;

    private Matrix4 transform;

    public GameObject() {
        this.tags = new Array<>(1);
        this.childs = null;
        this.components = new Array<>(3);
        this.transform = new Matrix4();
    }

    public GameObject(String name, long id) {
        this();
        this.name = name;
        this.id = id;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Array<String> getTags() {
        return this.tags;
    }

    @Override
    public Array<Component> getComponents() {
        return this.components;
    }

    @Override
    public void addComponent(Component component) {
        components.add(component);
    }

    @Override
    public Array<Node> getChilds() {
        return this.childs;
    }

    @Override
    public void addChild(Node child) {
        if(this.childs == null) {
            childs = new Array<>();
        }
        childs.add(child);
    }

    @Override
    public Node getParent() {
        return this.parent;
    }

    @Override
    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public Matrix4 getTransform() {
        return this.transform;
    }

    @Override
    public void setTransform(Matrix4 transform, boolean copy) {
        if(copy) {
            this.transform.set(transform);
        } else {
            this.transform = transform;
        }
    }

    @Override
    public void render(float delta) {
        for(Component component : this.components) {
            component.render(delta);
        }

        for(Node node : this.childs) {
            node.render(delta);
        }
    }

    @Override
    public void update(float delta) {
        for(Component component : this.components) {
            component.update(delta);
        }

        for(Node node : this.childs) {
            node.update(delta);
        }
    }

}
