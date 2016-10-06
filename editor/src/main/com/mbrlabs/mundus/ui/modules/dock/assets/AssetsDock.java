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

package com.mbrlabs.mundus.ui.modules.dock.assets;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.AssetImportEvent;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.tools.ToolManager;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class AssetsDock extends Tab implements ProjectChangedEvent.ProjectChangedListener, AssetImportEvent.AssetImportListener {

    private VisTable root;
    private VisTable filesViewContextContainer;
    private GridGroup filesView;

    @Inject
    private ToolManager toolManager;
    @Inject
    private ProjectManager projectManager;

    public AssetsDock() {
        super(false, false);
        Mundus.inject(this);
        Mundus.registerEventListener(this);
        initUi();
    }

    public void initUi() {
        root = new VisTable();
        filesViewContextContainer = new VisTable(false);
        filesView = new GridGroup(60, 4);
        filesView.setTouchable(Touchable.enabled);

        VisTable contentTable = new VisTable(false);
        contentTable.add(new VisLabel("Assets")).left().padLeft(3).row();
        contentTable.add(new Separator()).padTop(3).expandX().fillX();
        contentTable.row();
        contentTable.add(filesViewContextContainer).expandX().fillX();
        contentTable.row();
        contentTable.add(createScrollPane(filesView, true)).expand().fill();

        VisSplitPane splitPane = new VisSplitPane(new VisLabel("file tree here"), contentTable, false);
        splitPane.setSplitAmount(0.2f);

        root = new VisTable();
        root.setBackground("window-bg");
        root.add(splitPane).expand().fill();
    }

    private void reloadModels() {
        filesView.clearChildren();
        ProjectContext projectContext = projectManager.current();
        for (Asset asset : projectContext.assetManager.getAssets()) {
            if (asset instanceof ModelAsset) {
                AssetsDock.AssetItem assetItem = new AssetsDock.AssetItem(asset);
                filesView.addActor(assetItem);
            }
        }
    }

    private VisScrollPane createScrollPane(Actor actor, boolean disableX) {
        VisScrollPane scrollPane = new VisScrollPane(actor);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(disableX, false);
        return scrollPane;
    }

    @Override
    public String getTabTitle() {
        return "Assets";
    }

    @Override
    public Table getContentTable() {
        return root;
    }

    @Override
    public void onProjectChanged(ProjectChangedEvent projectChangedEvent) {
        reloadModels();
    }

    @Override
    public void onAssetImported(AssetImportEvent event) {
        reloadModels();
    }

    /**
     * Asset item in the grid.
     */
    private class AssetItem extends VisTable {

        private VisLabel nameLabel;
        private final Asset asset;

        public AssetItem(Asset asset) {
            super();
            setBackground("menu-bg");
            align(Align.center);
            this.asset = asset;
            nameLabel = new VisLabel(asset.toString(), "small");
            nameLabel.setWrap(true);
            add(nameLabel).fill().expand().row();

            // active ModelPlacementTool when selecting this model
            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(AssetItem.this.asset instanceof ModelAsset) {
                        toolManager.modelPlacementTool.setModel((ModelAsset) AssetItem.this.asset);
                        toolManager.activateTool(toolManager.modelPlacementTool);
                    }
                }
            });
        }
    }
}
