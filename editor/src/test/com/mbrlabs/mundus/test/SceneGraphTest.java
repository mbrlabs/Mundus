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

package com.mbrlabs.mundus.test;

import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Marcus Brummer
 * @version 22-02-2016
 */
public class SceneGraphTest {

    private static final Vector3 v0 = new Vector3();
    private static final Vector3 v1 = new Vector3();

    @Test
    public void rootTranslateTest() {
        SceneGraph sg = new SceneGraph(null);
        GameObject root0 = new GameObject(sg);

        sg.getGameObjects().add(root0);

        // both must be 0
        root0.getTransRel(v0);
        assertEquals(v0, root0.position);

        // set relative translation, then translate -> both must be same
        root0.setTransRel(10, 20, 30);
        root0.trans(10, 10, 10);
        root0.getTransRel(v0);
        assertEquals(v0, root0.position);

        // reset to 0
        root0.setTrans(0,0,0);
        root0.getTransRel(v0);
        assertEquals(v0, root0.position);
    }

    @Test
    public void parentingTranslation() {
        SceneGraph sg = new SceneGraph(null);
        GameObject root0 = new GameObject(sg);
        sg.getGameObjects().add(root0);

        GameObject c0 = new GameObject(sg);
        root0.addChild(c0);

        // translate parent, child must move too
        root0.setTrans(10, 20, 30);
        assertEquals(root0.position, c0.position);

        // move child relative to parent
        c0.trans(10, 10, 10);
        c0.setTransRel(5, 5, 5);
        assertEquals(c0.getTransRel(v0), v1.set(5, 5, 5));
        assertEquals(c0.position, v1.set(15, 25, 35));

        // add child to child
        GameObject c1 = new GameObject(sg);
        c0.addChild(c1);
        c1.setTransRel(10, 10, 10);
        assertEquals(c1.position, v1.set(25, 35, 45));

        // move root0, children must move as well
        root0.setTrans(0, 0, 0);
        assertEquals(c0.position, v0.set(5, 5, 5));
        assertEquals(c1.position, v0.set(15, 15, 15));
        assertEquals(c0.getTransRel(v0), v1.set(5, 5, 5));
        assertEquals(c1.getTransRel(v0), v1.set(10, 10, 10)); // the rel trans of this should stay the same

        // move inner c1, others should stay untouched
        c1.setTransRel(-100, -100, -100);
        assertEquals(root0.position, v0.set(0,0,0));
        assertEquals(c0.position, v0.set(5, 5, 5));
        assertEquals(c0.getTransRel(v0), v1.set(5, 5, 5));
        assertEquals(c1.getTransRel(v0), v1.set(-100, -100, -100));
        assertEquals(c1.position, v0.set(-95, -95, -95));
    }

    @Test
    public void testScaling() {
        SceneGraph sg = new SceneGraph(null);
        GameObject root = new GameObject(sg);
        sg.getGameObjects().add(root);


        GameObject child = new GameObject(sg);
        root.addChild(child);

        // check initial rel scale & abs scale
        v0.set(1, 1, 1);
        assertEquals(v0, child.getSclRel(v1));
        assertEquals(v0, child.scale);
        assertEquals(v0, root.getSclRel(v1));
        assertEquals(v0, root.scale);

        // scl root
        root.scl(2, 3, 4);
        v0.set(2, 3, 4);
        assertEquals(v0, root.scale);
        assertEquals(v0, root.getSclRel(v1));
        assertEquals(v0, child.scale);
        assertEquals(v0.set(1, 1, 1), child.getSclRel(v1));

        // scl child
        child.scl(2, 2, 2);
        v0.set(2, 3, 4);
        assertEquals(v0, root.scale);
        assertEquals(v0, root.getSclRel(v1));
        assertEquals(v0.set(4, 6, 8), child.scale);
        assertEquals(v0.set(2, 2, 2), child.getSclRel(v1));

        // scl root
        root.setSclRel(4, 4, 4);
        v0.set(4, 4, 4);
        assertEquals(v0, root.scale);
        assertEquals(v0, root.getSclRel(v1));
        assertEquals(v0.set(8, 8, 8), child.scale);
        assertEquals(v0.set(2, 2, 2), child.getSclRel(v1));
    }

    @Test
    public void testRotation() {
        SceneGraph sg = new SceneGraph(null);
        GameObject root = new GameObject(sg);
        sg.getGameObjects().add(root);

        GameObject child = new GameObject(sg);
        root.addChild(child);

        // check initial rel rot & abs rot
        v0.set(0, 0, 0);
        assertEquals(v0, child.getRotRel(v1));
        assertEquals(v0, child.rotation);
        assertEquals(v0, root.getRotRel(v1));
        assertEquals(v0, root.rotation);

        // set root rotation
        root.setRotRel(5, 5, 5);
        v0.set(5, 5, 5);
        assertEquals(v0, root.getRotRel(v1));
        assertEquals(v0, root.rotation);
        assertEquals(v0, child.rotation);
        assertEquals(v0.scl(0, 0, 0), child.getRotRel(v1));

        // rotate root
        root.rot(10, 20, 30);
        v0.set(15, 25, 35);
        assertEquals(v0, root.getRotRel(v1));
        assertEquals(v0, root.rotation);
        assertEquals(v0, child.rotation);
        assertEquals(v0.scl(0, 0, 0), child.getRotRel(v1));

        // set child rot rel
        child.setRotRel(-10, -20, -30);
        v0.set(15, 25, 35);
        assertEquals(v0, root.getRotRel(v1));
        assertEquals(v0, root.rotation);
        assertEquals(v0.set(5, 5, 5), child.rotation);
        assertEquals(v0.set(-10, -20, -30), child.getRotRel(v1));
    }

}
