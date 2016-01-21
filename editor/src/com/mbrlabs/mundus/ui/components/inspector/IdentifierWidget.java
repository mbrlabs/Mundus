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

package com.mbrlabs.mundus.ui.components.inspector;


import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.scene3d.GameObject;

/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
public class IdentifierWidget extends VisTable {

    private VisCheckBox active;
    private VisTextField name;
    private VisTextField tag;

    public IdentifierWidget() {
        super();
        init();
        setupUI();
        setupListeners();
    }

    private void init() {
        active = new VisCheckBox("", true);
        name = new VisTextField("Name");
        tag = new VisTextField("Untagged");
        pad(10);
    }

    private void setupUI() {
        add(active).padBottom(4).left().top();
        add(name).padBottom(4).left().top().expandX().fillX().row();
        add(new VisLabel("Tag: ")).left().top();
        add(tag).top().left().expandX().fillX().row();
    }

    private void setupListeners() {

    }

    public void setValues(GameObject go) {
        active.setChecked(go.isActive());
        name.setText(go.getName());
    }

}
