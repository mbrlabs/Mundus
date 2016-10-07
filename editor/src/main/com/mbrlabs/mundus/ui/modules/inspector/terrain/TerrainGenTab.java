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
import com.mbrlabs.mundus.commons.terrain.terraform.Terraformer;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.history.CommandHistory;
import com.mbrlabs.mundus.history.commands.TerrainHeightCommand;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.widgets.FileChooserField;
import com.mbrlabs.mundus.ui.widgets.FloatFieldWithLabel;
import com.mbrlabs.mundus.ui.widgets.IntFieldWithLabel;
import com.mbrlabs.mundus.utils.FileFormatUtils;

/**
 * @author Marcus Brummer
 * @version 04-03-2016
 */
public class TerrainGenTab extends Tab {

    private TerrainComponentWidget parent;
    private VisTable root;

    private FileChooserField hmInput;
    private VisTextButton loadHeightMapBtn;

    private VisTextButton perlinNoiseBtn;
    private IntFieldWithLabel perlinNoiseSeed;
    private FloatFieldWithLabel perlinNoiseMinHeight;
    private FloatFieldWithLabel perlinNoiseMaxHeight;

    @Inject
    private ToolManager toolManager;
    @Inject
    private CommandHistory history;

    public TerrainGenTab(final TerrainComponentWidget parent) {
        super(false, false);
        Mundus.inject(this);
        this.parent = parent;
        root = new VisTable();
        root.align(Align.left);

        hmInput = new FileChooserField();
        loadHeightMapBtn = new VisTextButton("Load heightmap");
        perlinNoiseBtn = new VisTextButton("Generate Perlin noise");
        perlinNoiseSeed = new IntFieldWithLabel("Seed", -1, false);
        perlinNoiseMinHeight = new FloatFieldWithLabel("Min height", -1, true);
        perlinNoiseMaxHeight = new FloatFieldWithLabel("Max height", -1, true);

        root.add(new VisLabel("Load Heightmap")).pad(5).left().row();
        root.add(hmInput).left().expandX().fillX().row();
        root.add(loadHeightMapBtn).padLeft(5).left().row();

        root.add(new VisLabel("Perlin Noise")).pad(5).padTop(10).left().row();
        root.add(perlinNoiseSeed).pad(5).left().fillX().expandX().row();
        root.add(perlinNoiseMinHeight).pad(5).left().fillX().expandX().row();
        root.add(perlinNoiseMaxHeight).pad(5).left().fillX().expandX().row();
        root.add(perlinNoiseBtn).pad(5).left().row();

        setupListeners();
    }

    private void setupListeners() {
        loadHeightMapBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                FileHandle hm = hmInput.getFile();
                if (hm != null && hm.exists() && FileFormatUtils.isImage(hm)) {
                    loadHeightMap(hm);
                } else {
                    Dialogs.showErrorDialog(Ui.getInstance(), "Please select a heightmap image");
                }
            }
        });

        perlinNoiseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int seed = perlinNoiseSeed.getInt();
                float min = perlinNoiseMinHeight.getFloat();
                float max = perlinNoiseMaxHeight.getFloat();
                generatePerlinNoise(seed, min, max);
            }
        });
    }

    private void loadHeightMap(FileHandle heightMap) {
        Terrain terrain = parent.component.getTerrain();
        TerrainHeightCommand command = new TerrainHeightCommand(terrain);
        command.setHeightDataBefore(terrain.heightData);

        Pixmap originalMap = new Pixmap(heightMap);

        // scale pixmap if it doesn't fit the terrain
        if (originalMap.getWidth() != terrain.vertexResolution || originalMap.getHeight() != terrain.vertexResolution) {
            Pixmap scaledPixmap = new Pixmap(terrain.vertexResolution, terrain.vertexResolution,
                    originalMap.getFormat());
            scaledPixmap.drawPixmap(originalMap, 0, 0, originalMap.getWidth(), originalMap.getHeight(), 0, 0,
                    scaledPixmap.getWidth(), scaledPixmap.getHeight());

            originalMap.dispose();
            Terraformer.heightMap(terrain).maxHeight(terrain.terrainWidth * 0.17f).map(scaledPixmap).terraform();
            scaledPixmap.dispose();
        } else {
            Terraformer.heightMap(terrain).maxHeight(terrain.terrainWidth * 0.17f).map(originalMap).terraform();
            originalMap.dispose();
        }

        command.setHeightDataAfter(terrain.heightData);
        history.add(command);
    }

    private void generatePerlinNoise(int seed, float min, float max) {
        Terrain terrain = parent.component.getTerrain();
        TerrainHeightCommand command = new TerrainHeightCommand(terrain);
        command.setHeightDataBefore(terrain.heightData);

        Terraformer.perlin(terrain).minHeight(min).maxHeight(max).seed(seed).terraform();

        command.setHeightDataAfter(terrain.heightData);
        history.add(command);
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
