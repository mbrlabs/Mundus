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

package com.mbrlabs.mundus.editor.ui.modules.dock;

import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import com.mbrlabs.mundus.editor.core.Inject;
import com.mbrlabs.mundus.editor.core.Mundus;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.input.FreeCamController;
import com.mbrlabs.mundus.editor.ui.modules.dock.assets.AssetsDock;
import com.mbrlabs.mundus.editor.ui.widgets.MundusSplitPane;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class DockBar extends VisTable implements TabbedPaneListener {

    private AssetsDock assetsDock;
    private TabbedPane tabbedPane;

    @Inject
    private FreeCamController freeCamController;
    @Inject
    private ProjectManager projectManager;

    private MundusSplitPane splitPane;

    public DockBar(MundusSplitPane splitPane) {
        super();
        this.splitPane = splitPane;
        Mundus.inject(this);

        TabbedPane.TabbedPaneStyle style = new TabbedPane.TabbedPaneStyle(
                VisUI.getSkin().get(TabbedPane.TabbedPaneStyle.class));
        style.buttonStyle = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().get("toggle", VisTextButton.VisTextButtonStyle.class));

        tabbedPane = new TabbedPane(style);
        tabbedPane.setAllowTabDeselect(true);
        tabbedPane.addListener(this);

        assetsDock = new AssetsDock();
        tabbedPane.add(assetsDock);
        add(tabbedPane.getTable()).expandX().fillX().left().bottom().height(30).row();
    }

    @Override
    public void switchedTab(Tab tab) {
        if (tab != null) {
            splitPane.setSecondWidget(tab.getContentTable());
            splitPane.setSplitAmount(0.8f);
        } else {
            splitPane.setSecondWidget(null);
            splitPane.setSplitAmount(1f);
        }
        splitPane.invalidate();
    }

    @Override
    public void removedTab(Tab tab) {
        // user can't do that
    }

    @Override
    public void removedAllTabs() {
        // user can't do that
    }

}
