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

package com.mbrlabs.mundus.commons.test;

import com.mbrlabs.mundus.commons.scene3d.Node;
import com.mbrlabs.mundus.commons.scene3d.SimpleNode;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Marcus Brummer
 * @version 21-06-2016
 */
public class SceneGraphTest {

    @Test
    public void basicParenting() {
        Node root = new SimpleNode<SimpleNode>(0);
        Assert.assertNull(root.getChildren());

        Node c0 = new SimpleNode<SimpleNode>(1);
        root.addChild(c0);
        Assert.assertEquals(1, root.getChildren().size);
        Assert.assertTrue(root.getChildren().first() == c0);
        Assert.assertTrue(root == c0.getParent());

        Node c1 = new SimpleNode<SimpleNode>(2);
        root.addChild(c1);
        Assert.assertEquals(2, root.getChildren().size);
        Assert.assertTrue(root == c0.getParent());

        c0.remove();
        Assert.assertEquals(1, root.getChildren().size);
        Assert.assertNull(c0.getParent());
    }

}
