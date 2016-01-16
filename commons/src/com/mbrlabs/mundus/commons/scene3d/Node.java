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
public interface Node {

    public long getId();
    public void setId(long id);
    public String getName();
    public void setName(String name);

    public Array<Component> getComponents();
    public void addComponent(Component component);

    public Array<Node> getChilds();
    public void addChild(Node child);

    public Node getParent();
    public void setParent(Node parent);

    public Array<String> getTags();

    public Matrix4 getTransform();
    public void setTransform(Matrix4 matrix4, boolean copy);

    public void render(float delta);
    public void update(float delta);

}
