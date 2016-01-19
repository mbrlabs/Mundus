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

import com.kotcrab.vis.ui.widget.*;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public class TransformWidget extends BaseInspectorWidget {

    private VisCheckBox active;
    private VisTextField name;
    private VisTextField tag;

    public TransformWidget() {
        super("Transform");
        init();
        setupUI();
        setupListeners();
    }

    private void init() {
        active = new VisCheckBox("", true);
        name = new VisTextField("Name");
        tag = new VisTextField("Untagged");
    }

    private void setupUI() {
        collapsibleContent.add(active).padBottom(4).left().top();
        collapsibleContent.add(name).padBottom(4).left().top().expand().fill().row();
        collapsibleContent.add(new VisLabel("Tag: ")).left().top();
        collapsibleContent.add(tag).top().left().expand().fill().row();
    }

    private void setupListeners() {

    }

}
