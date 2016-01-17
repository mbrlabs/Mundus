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

package com.mbrlabs.mundus.ui.components.dock.assets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.events.*;
import com.mbrlabs.mundus.model.MModel;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.utils.Log;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
@Deprecated
public class AssetsDock {

    private VisTable root;
    private VisTable filesViewContextContainer;
    private GridGroup filesView;
    private AssetsTab assetsTab;

    @Inject
    private ProjectContext projectContext;
    @Inject
    private ToolManager toolManager;

    public AssetsDock() {
        Mundus.inject(this);
        Mundus.registerEventListener(this);
        initUi();
    }

    public void initUi () {
        root = new VisTable();
        filesViewContextContainer = new VisTable(false);
        filesView = new GridGroup(92, 4);
        filesView.setTouchable(Touchable.enabled);

        VisTable contentsTable = new VisTable(false);
        contentsTable.add(new VisLabel("Assets")).left().padLeft(3).row();
        contentsTable.add(new Separator()).padTop(3).expandX().fillX();
        contentsTable.row();
        contentsTable.add(filesViewContextContainer).expandX().fillX();
        contentsTable.row();
        contentsTable.add(createScrollPane(filesView, true)).expand().fill();

        VisSplitPane splitPane = new VisSplitPane(new VisLabel("file tree here"), contentsTable, false);
        splitPane.setSplitAmount(0.2f);

        root = new VisTable();
        root.setBackground("window-bg");
        root.add(splitPane).expand().fill();

        assetsTab = new AssetsTab();


    }

    @Subscribe
    public void modelImported(ModelImportEvent modelImportEvent) {
        AssetItem assetItem = new AssetItem(modelImportEvent.getModel());
        filesView.addActor(assetItem);
    }

    @Subscribe
    public void reloadAllModels(ProjectChangedEvent projectChangedEvent) {
        filesView.clearChildren();
        for(MModel model : projectContext.models) {
            AssetItem assetItem = new AssetItem(model);
            filesView.addActor(assetItem);
        }
    }

    private VisScrollPane createScrollPane (Actor actor, boolean disableX) {
        VisScrollPane scrollPane = new VisScrollPane(actor);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(disableX, false);
        return scrollPane;
    }


    public AssetsTab getAssetsTab() {
        return assetsTab;
    }

    public VisTable getRoot() {
        return root;
    }

    /**
     * Assets Tab in the dock.
     */
    private class AssetsTab extends Tab {

        public AssetsTab() {
            super(false, false);
        }

        @Override
        public String getTabTitle() {
            return "Assets";
        }

        @Override
        public Table getContentTable() {
            return root;
        }
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
