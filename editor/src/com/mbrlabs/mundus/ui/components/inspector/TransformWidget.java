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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.*;
import com.mbrlabs.mundus.ui.widgets.FaTextButton;
import com.mbrlabs.mundus.utils.Fa;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public class TransformWidget extends VisTable {

    private CollapsibleWidget collapsibleWrapper;
    private VisTable collapableTable;
    private FaTextButton collapseBtn;

    private VisCheckBox active;
    private VisTextField name;
    private VisTextField tag;

    public TransformWidget() {
        super();
        init();
        setupUI();
        setupListeners();
    }

    private void init() {
        collapableTable = new VisTable();
        collapsibleWrapper = new CollapsibleWidget();
        collapseBtn = new FaTextButton(Fa.CARET_UP);

        active = new VisCheckBox("", true);
        name = new VisTextField("Name");
        tag = new VisTextField("Untagged");
        pad(7);

    }

    private void setupUI() {
        final VisTable header = new VisTable();
        header.add(new VisLabel("Transform")).left().top();
        header.add(collapseBtn).right().top().width(20).height(20).expand().row();
        header.addSeparator().colspan(2).padBottom(4).row();

        collapableTable.add(active).padBottom(4).left().top();
        collapableTable.add(name).padBottom(4).left().top().expand().fill().row();
        collapableTable.add(new VisLabel("Tag: ")).left().top();
        collapableTable.add(tag).top().left().expand().fill().row();
        collapsibleWrapper.setTable(collapableTable);

        add(header).left().top().expand().fill().row();
        add(collapsibleWrapper).left().top().expandX().fillX().row();
    }

    private void setupListeners() {
        collapseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(collapsibleWrapper.isCollapsed()) {
                    collapsibleWrapper.setCollapsed(false);
                    collapseBtn.setText(Fa.CARET_UP);
                } else {
                    collapsibleWrapper.setCollapsed(true);
                    collapseBtn.setText(Fa.CARET_DOWN);
                }
            }
        });
    }

}
