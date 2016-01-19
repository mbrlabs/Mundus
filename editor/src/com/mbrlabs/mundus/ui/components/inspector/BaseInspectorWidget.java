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
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.ui.widgets.FaTextButton;
import com.mbrlabs.mundus.utils.Fa;

/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
public abstract class BaseInspectorWidget extends VisTable {

    private static final String COLLAPSE_BTN_DOWN = Fa.CARET_DOWN;
    private static final String COLLAPSE_BTN_UP = Fa.CARET_UP;

    private String title;

    protected VisTable collapsibleContent;
    private CollapsibleWidget collapsibleWidget;
    private VisLabel titleLabel;
    private FaTextButton collapseBtn;

    public BaseInspectorWidget(String title) {
        super();
        collapsibleContent = new VisTable();
        titleLabel = new VisLabel();
        collapsibleWidget = new CollapsibleWidget(collapsibleContent);
        collapseBtn = new FaTextButton(Fa.CARET_DOWN);
        pad(7);

        setupUI();
        setupListeners();

        setTitle(title);
    }

    private void setupListeners() {
        collapseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                collapse(!isCollapsed());
            }
        });
    }

    private void setupUI() {
        // header
        final VisTable header = new VisTable();
        header.add(titleLabel).left().top();
        header.add(collapseBtn).right().top().width(20).height(20).expand().row();
        header.addSeparator().colspan(2).padBottom(4).row();

        // add everything to root
        add(header).expand().fill().row();
        add(collapsibleWidget).expand().fill().row();
    }

    public boolean isCollapsed() {
        return collapsibleWidget.isCollapsed();
    }

    public void collapse(boolean collapse) {
        collapsibleWidget.setCollapsed(collapse);
        if(collapse) {
            collapseBtn.setText(COLLAPSE_BTN_DOWN);
        } else {
            collapseBtn.setText(COLLAPSE_BTN_UP);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        titleLabel.setText(title);
    }

    public VisTable getCollapsibleContent() {
        return collapsibleContent;
    }

}
