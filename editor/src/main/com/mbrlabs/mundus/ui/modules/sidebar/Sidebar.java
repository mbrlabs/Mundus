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

package com.mbrlabs.mundus.ui.modules.sidebar;

import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;

/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
public class Sidebar extends VisTable implements TabbedPaneListener {

    private TabbedPane tabbedPane;
    private VisTable contentContainer;

    private ModelTab modelTab;
    private OutlineTab outlineTab;

    public Sidebar() {
        super();
        TabbedPane.TabbedPaneStyle style = VisUI.getSkin().get(TabbedPane.TabbedPaneStyle.class);
        style.vertical = false;
        tabbedPane = new TabbedPane(style);
        outlineTab = new OutlineTab();
        modelTab = new ModelTab();

        setupUi();

        tabbedPane.switchTab(outlineTab);
        switchedTab(outlineTab);
        tabbedPane.addListener(this);
    }

    public void setupUi() {
        contentContainer = new VisTable();
        contentContainer.setBackground("window-bg");
        contentContainer.align(Align.topLeft);

        tabbedPane.add(outlineTab);
        tabbedPane.add(modelTab);

        add(tabbedPane.getTable()).width(300).top().left().row();
        add(contentContainer).width(300).top().left().expandY().fillY().row();
    }

    @Override
    public void switchedTab(Tab tab) {
        contentContainer.clear();
        contentContainer.add(tab.getContentTable()).fill().expand();
    }

    @Override
    public void removedTab(Tab tab) {
        // we don't remove tabs from the sidebar
    }

    @Override
    public void removedAllTabs() {
        // we don't remove tabs from the sidebar
    }

}
