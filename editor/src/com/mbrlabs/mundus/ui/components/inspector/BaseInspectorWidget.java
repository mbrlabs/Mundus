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
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.scene3d.GameObject;
import com.mbrlabs.mundus.ui.widgets.FaTextButton;
import com.mbrlabs.mundus.utils.Fa;

/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
public abstract class BaseInspectorWidget extends VisTable {

    private static final String COLLAPSE_BTN_DOWN = Fa.CARET_UP;
    private static final String COLLAPSE_BTN_UP = Fa.CARET_DOWN;

    private String title;
    protected Inspector inspector;
    private FaTextButton collapseBtn;
    private FaTextButton deleteBtn;
    private Cell deletableBtnCell;

    protected VisTable collapsibleContent;
    private CollapsibleWidget collapsibleWidget;
    private VisLabel titleLabel;

    private boolean deletable;

    public BaseInspectorWidget(Inspector inspector, String title) {
        super();
        this.inspector = inspector;
        collapsibleContent = new VisTable();
        titleLabel = new VisLabel();
        collapsibleWidget = new CollapsibleWidget(collapsibleContent);

        collapseBtn = new FaTextButton(COLLAPSE_BTN_UP);
        collapseBtn.getLabel().setFontScale(0.7f);

        deleteBtn = new FaTextButton(Fa.TIMES);
        deleteBtn.getLabel().setFontScale(0.7f);
        deleteBtn.getStyle().up = null;

        deletable = false;
        pad(7);

        setupUI();
        setupListeners();

        setTitle(title);
    }

    private void setupListeners() {
        // collapse
        collapseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                collapse(!isCollapsed());
            }
        });

        // delete
        deleteBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onDelete();
            }
        });
    }

    private void setupUI() {
        // header
        final VisTable header = new VisTable();
        deletableBtnCell = header.add(deleteBtn).top().left();
        header.add(titleLabel);
        header.add(collapseBtn).right().top().width(20).height(20).expand().row();
        header.addSeparator().colspan(3).padBottom(4).row();

        // add everything to root
        add(header).expand().fill().row();
        add(collapsibleWidget).expand().fill().row();

        setDeletable(deletable);
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
        if(deletable) {
            deleteBtn.setVisible(true);
            deletableBtnCell.width(20).height(20).padRight(5);
        } else {
            deleteBtn.setVisible(false);
            deletableBtnCell.width(0).height(0).padRight(0);
        }
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

    public abstract void onDelete();
    public abstract void setValues(GameObject go);

}
