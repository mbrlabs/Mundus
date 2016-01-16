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

import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;

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

    public SceneGraphTester() {
        super();
        root.setName("root");

        root.addChild(new GameObject("c1", 0));
        root.addChild(new GameObject("c2", 0));
        root.addChild(new GameObject("c3", 0));

        GameObject go = new GameObject("c4", 0);
        root.addChild(go);
        go.addChild(new GameObject("adsf", 0));
        go.addChild(new GameObject("ad333sdsfgf", 0));
        go.addChild(new GameObject("adsdfgfghaa3456f", 0));

        GameObject nested = new GameObject("2345245", 0);
        go.addChild(nested);
        nested.addChild(new GameObject("3sdfgsfg", 9));
        nested.addChild(new GameObject("325", 9));
        nested.addChild(new GameObject("dsfsssgdfgh", 9));

        go.addChild(new GameObject("asdf", 0));
        go.addChild(new GameObject("212a", 0));

        root.addChild(new GameObject("wertwsaerf", 0));
        root.addChild(new GameObject("ddas", 0));
        root.addChild(nested);

        GameObject nested2 = new GameObject("ghj7645", 0);
        root.addChild(nested2);
        nested2.addChild(new GameObject("#1234", 23));
        nested2.addChild(new GameObject("sfger", 23));
        nested2.addChild(new GameObject("345sss", 23));
        nested2.addChild(new GameObject("dfgdds", 23));
        nested2.addChild(new GameObject("gjsffghjdcv", 23));
        nested2.addChild(new GameObject("dfgsssds", 23));

        root.addChild(new GameObject("wefrslfd", 3));
        root.addChild(nested2);
        root.addChild(new GameObject("das#adfs", 3));
        root.addChild(nested);


    }

}
