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

package com.mbrlabs.mundus.editor.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * @author Marcus Brummer
 * @version 03-01-2016
 */
public class Toolbar {

    private VisTable root;

    private VisTable left;
    private VisTable right;

    public Toolbar() {
        super();
        root = new VisTable();
        root.setBackground("menu-bg");
        root.align(Align.left | Align.center);

        left = new VisTable();
        left.left().top();
        root.add(left).pad(2).expandX().fillX();

        right = new VisTable();
        right.right().top();
        root.add(right).pad(2).expandX().fillX().row();
        root.addSeparator().pad(0).height(2).colspan(2);
    }

    public void addItem(Actor actor, boolean addLeft) {
        if (addLeft) {
            left.add(actor);
        } else {
            right.add(actor);
        }
    }

    public void addSeperator(boolean addLeft) {
        if (addLeft) {
            left.addSeparator(true);
        } else {
            right.addSeparator(true);
        }
    }

    public VisTable getRoot() {
        return this.root;
    }

}
