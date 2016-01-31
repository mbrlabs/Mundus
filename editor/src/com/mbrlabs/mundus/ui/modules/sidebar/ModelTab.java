/*
 * Copyright (c) 2015. See AUTHORS file.
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

package com.mbrlabs.mundus.ui.modules.sidebar;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.commons.model.MModel;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.events.ModelImportEvent;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.tools.ToolManager;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
public class ModelTab extends Tab implements ProjectChangedEvent.ProjectChangedListener, ModelImportEvent.ModelImportListener {

    private static final String TITLE = "Models";

    private VisTable content;
    private GridGroup modelGrid;

    @Inject
    private ToolManager toolManager;
    @Inject
    private ProjectContext projectContext;

    public ModelTab() {
        super(false, false);
        Mundus.inject(this);
        Mundus.registerEventListener(this);

        content = new VisTable();
        content.align(Align.left | Align.top);
        modelGrid = new GridGroup(60, 4);
        modelGrid.setTouchable(Touchable.enabled);

        content.add(new VisLabel("Imported models")).left().pad(5).row();
        content.addSeparator();
        content.add(modelGrid).expandX().fillX().row();

        reloadModels();
    }

    private void reloadModels() {
        modelGrid.clearChildren();
        for(MModel model : projectContext.models) {
            AssetItem assetItem = new AssetItem(model);
            modelGrid.addActor(assetItem);
        }
    }

    @Override
    public String getTabTitle() {
        return TITLE;
    }

    @Override
    public Table getContentTable() {
        return content;
    }

    @Override
    public void onProjectChanged(ProjectChangedEvent projectChangedEvent) {
        reloadModels();
    }

    @Override
    public void onModelImported(ModelImportEvent importEvent) {
        AssetItem assetItem = new AssetItem(importEvent.getModel());
        modelGrid.addActor(assetItem);
    }

    /**
     * Asset item in the grid.
     */
    private class AssetItem extends VisTable {

        private VisLabel nameLabel;
        private MModel model;

        public AssetItem(MModel mModel) {
            super();
            setBackground("menu-bg");
            align(Align.center);
            nameLabel = new VisLabel(mModel.name, "small");
            nameLabel.setWrap(true);
            model = mModel;
            add(nameLabel).fill().expand().row();

            // active ModelPlacementTool when selecting this model
            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    toolManager.modelPlacementTool.setModel(model);
                    toolManager.activateTool(toolManager.modelPlacementTool);
                }
            });

        }

    }

}
