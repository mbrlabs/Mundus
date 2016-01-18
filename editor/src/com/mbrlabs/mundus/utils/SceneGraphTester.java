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

package com.mbrlabs.mundus.utils;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mbrlabs.mundus.core.Scene;
import com.mbrlabs.mundus.scene3d.GameObject;
import com.mbrlabs.mundus.scene3d.SceneGraph;
import com.mbrlabs.mundus.core.Mundus;

/**
 * // TODO remove
 * This is just to test the scene graph.
 * Do not use except for testing
 *
 * @author Marcus Brummer
 * @version 16-01-2016
 */
@Deprecated
public class SceneGraphTester extends SceneGraph {

    public SceneGraphTester(Scene scene, ModelBatch modelBatch) {
        super(scene);
        setBatch(modelBatch);
        root.setName("ROOT");

        root.addChild(new GameObject(this, "c1", 0));
        root.addChild(new GameObject(this, "c2", 0));
        root.addChild(new GameObject(this, "c3", 0));

        GameObject go = new GameObject(this, "c4", 0);
        root.addChild(go);
        go.addChild(new GameObject(this, "adsf", 0));
        go.addChild(new GameObject(this, "ad333sdsfgf", 0));
        go.addChild(new GameObject(this, "adsdfgfghaa3456f", 0));

        GameObject nested = new GameObject(this, "2345245", 0);
        go.addChild(nested);
        nested.addChild(new GameObject(this, "3sdfgsfg", 9));
        nested.addChild(new GameObject(this, "325", 9));
        nested.addChild(new GameObject(this, "dsfsssgdfgh", 9));

        go.addChild(new GameObject(this, "asdf", 0));
        go.addChild(new GameObject(this, "212a", 0));

        root.addChild(new GameObject(this, "wertwsaerf", 0));
        root.addChild(new GameObject(this, "ddas", 0));
        root.addChild(nested);

        GameObject nested2 = new GameObject(this, "ghj7645", 0);
        root.addChild(nested2);
        nested2.addChild(new GameObject(this, "#1234", 23));
        nested2.addChild(new GameObject(this, "sfger", 23));
        nested2.addChild(new GameObject(this, "345sss", 23));
        nested2.addChild(new GameObject(this, "dfgdds", 23));
        nested2.addChild(new GameObject(this, "gjsffghjdcv", 23));
        nested2.addChild(new GameObject(this, "dfgsssds", 23));

        root.addChild(new GameObject(this, "wefrslfd", 3));
        root.addChild(nested2);
        root.addChild(new GameObject(this, "das#adfs", 3));
        root.addChild(nested);


    }

}
