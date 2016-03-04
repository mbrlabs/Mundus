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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.widgets.FileChooserField;
import com.mbrlabs.mundus.utils.FileFormatUtils;

/**
 * @author Marcus Brummer
 * @version 04-03-2016
 */
public class TerrainGenTab extends Tab {

    private TerrainComponentWidget parent;
    private VisTable root;

    private FileChooserField hmInput;
    private VisTextButton loadHeightmpBtn;

    @Inject
    private ToolManager toolManager;

    public TerrainGenTab(final TerrainComponentWidget parent) {
        super(false, false);
        Mundus.inject(this);
        this.parent = parent;
        root = new VisTable();
        root.align(Align.left);

        hmInput = new FileChooserField(250);
        loadHeightmpBtn = new VisTextButton("Load heightmap");

        root.add(new VisLabel("Load Heightmap")).padLeft(5).left().row();
        root.add(hmInput).left().row();
        root.add(loadHeightmpBtn).row();

        setupListeners();
    }

    private void setupListeners() {
        loadHeightmpBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                FileHandle hm = hmInput.getFile();
                if(hm != null && hm.exists() && FileFormatUtils.isImage(hm)) {
                    loadHeigtmap(hm);
                } else {
                    Dialogs.showErrorDialog(Ui.getInstance(), "Please select a heightmap image");
                }
            }
        });
    }

    public void loadHeigtmap(FileHandle heightMap) {
        Terrain terrain = parent.component.getTerrain();
        Pixmap originalMap = new Pixmap(heightMap);

        // scale pixmap if it doesn't fit the terrain
        if(originalMap.getWidth() != terrain.vertexResolution || originalMap.getHeight() != terrain.vertexResolution) {
            Pixmap scaledPixmap = new Pixmap(terrain.vertexResolution, terrain.vertexResolution, originalMap.getFormat());
            // 	public void drawPixmap (Pixmap pixmap, int srcx, int srcy, int srcWidth, int srcHeight, int dstx, int dsty, int dstWidth,
            scaledPixmap.drawPixmap(originalMap, 0, 0, originalMap.getWidth(),
                    originalMap.getHeight(), 0, 0, scaledPixmap.getWidth(), scaledPixmap.getHeight());

            originalMap.dispose();
            terrain.loadHeightMap(scaledPixmap, terrain.terrainWidth * 0.17f);
            scaledPixmap.dispose();
        } else {
            terrain.loadHeightMap(originalMap, terrain.terrainWidth * 0.17f);
            originalMap.dispose();
        }

        terrain.update();
    }

    @Override
    public String getTabTitle() {
        return "Gen";
    }

    @Override
    public Table getContentTable() {
        return root;
    }

}
