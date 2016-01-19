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

import com.kotcrab.vis.ui.widget.VisLabel;
import com.mbrlabs.mundus.ui.widgets.TextFieldWithLabel;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public class TransformWidget extends BaseInspectorWidget {

    private TextFieldWithLabel posX;
    private TextFieldWithLabel posY;
    private TextFieldWithLabel posZ;

    private TextFieldWithLabel rotX;
    private TextFieldWithLabel rotY;
    private TextFieldWithLabel rotZ;

    private TextFieldWithLabel scaleX;
    private TextFieldWithLabel scaleY;
    private TextFieldWithLabel scaleZ;

    public TransformWidget() {
        super("Transform");
        init();
        setupUI();
        setupListeners();
    }

    private void init() {
        int size = 65;
        posX = new TextFieldWithLabel("x", size);
        posY = new TextFieldWithLabel("y", size);
        posZ = new TextFieldWithLabel("z", size);
        rotX = new TextFieldWithLabel("x", size);
        rotY = new TextFieldWithLabel("y", size);
        rotZ = new TextFieldWithLabel("z", size);
        scaleX = new TextFieldWithLabel("x", size);
        scaleY = new TextFieldWithLabel("y", size);
        scaleZ = new TextFieldWithLabel("z", size);
    }

    private void setupUI() {
        int pad = 4;
        collapsibleContent.add(new VisLabel("Position: ")).padRight(5).padBottom(pad).left();
        collapsibleContent.add(posX).padBottom(pad).padRight(pad);
        collapsibleContent.add(posY).padBottom(pad).padRight(pad);
        collapsibleContent.add(posZ).padBottom(pad).row();

        collapsibleContent.add(new VisLabel("Rotation: ")).padRight(5).padBottom(pad).left();
        collapsibleContent.add(rotX).padBottom(pad).padRight(pad);
        collapsibleContent.add(rotY).padBottom(pad).padRight(pad);
        collapsibleContent.add(rotZ).padBottom(pad).row();

        collapsibleContent.add(new VisLabel("Scale: ")).padRight(10).padBottom(pad).left();
        collapsibleContent.add(scaleX).padBottom(pad).padRight(pad);
        collapsibleContent.add(scaleY).padBottom(pad).padRight(pad);
        collapsibleContent.add(scaleZ).padBottom(pad).row();
    }

    private void setupListeners() {

    }

}
