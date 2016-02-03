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

package com.mbrlabs.mundus.ui.modules.inspector.terrain;

import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.ui.modules.inspector.ComponentWidget;

/**
 * @author Marcus Brummer
 * @version 29-01-2016
 */
public class TerrainComponentWidget extends ComponentWidget<TerrainComponent> implements TabbedPaneListener {

    private TabbedPane tabbedPane;
    private VisTable tabContainer = new VisTable();

    private TerrainUpDownTab raiseLowerTab;
    private TerrainFlattenTab flattenTab;
    private TerrainPaintTab paintTab;
    private TerrainSettingsTab settingsTab;

    @Inject
    private ToolManager toolManager;
    @Inject
    private ProjectManager projectManager;
    @Inject
    private ProjectContext projectContext;

    public TerrainComponentWidget(TerrainComponent terrainComponent) {
        super("Terrain Component", terrainComponent);
        Mundus.inject(this);

        tabbedPane = new TabbedPane();
        tabbedPane.addListener(this);

        raiseLowerTab = new TerrainUpDownTab(this);
        flattenTab = new TerrainFlattenTab(this);
        paintTab = new TerrainPaintTab(this);
        settingsTab = new TerrainSettingsTab();

        tabbedPane.add(raiseLowerTab);
        tabbedPane.add(flattenTab);
        tabbedPane.add(paintTab);
        tabbedPane.add(settingsTab);

        setupUI();

        tabbedPane.switchTab(0);
    }

    private void setupUI() {
        collapsibleContent.add(tabbedPane.getTable()).padBottom(7).row();
        collapsibleContent.add(tabContainer).expand().fill().row();
    }

    @Override
    public void setValues(GameObject go) {
        Component c = go.findComponentByType(Component.Type.TERRAIN);
        if(c != null) {
            this.component = (TerrainComponent) c;
        }
    }

    @Override
    public void switchedTab(Tab tab) {
        tabContainer.clearChildren();
        tabContainer.add(tab.getContentTable()).expand().fill();
    }

    @Override
    public void removedTab(Tab tab) {
        // no
    }

    @Override
    public void removedAllTabs() {
        // nope
    }

}
