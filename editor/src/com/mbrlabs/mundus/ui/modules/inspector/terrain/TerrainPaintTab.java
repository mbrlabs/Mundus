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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.commons.model.MTexture;
import com.mbrlabs.mundus.commons.terrain.SplatTexture;
import com.mbrlabs.mundus.commons.terrain.TerrainTexture;
import com.mbrlabs.mundus.commons.terrain.SplatMap;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.tools.brushes.TerrainBrush;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.modules.dialogs.TextureBrowser;
import com.mbrlabs.mundus.ui.widgets.TextureGrid;
import com.mbrlabs.mundus.commons.utils.TextureProvider;

/**
 * @author Marcus Brummer
 * @version 30-01-2016
 */
public class TerrainPaintTab extends Tab {

    private TerrainComponentWidget parent;

    private VisTable table;
    private VisTextButton addTextureBtn;
    private TextureGrid<TextureProvider> textureGrid;

    private TextureBrowser textureBrowser;

    @Inject
    private ToolManager toolManager;

    public TerrainPaintTab(final TerrainComponentWidget parent) {
        super(false, false);
        Mundus.inject(this);
        this.parent = parent;
        table = new VisTable();
        table.align(Align.left);
        table.add(new TerrainBrushTable(parent, TerrainBrush.BrushMode.PAINT)).expand().fill().padBottom(5).row();
        table.addSeparator().height(1);

        textureGrid = new TextureGrid<>(40, 5);
        textureGrid.setBackground(VisUI.getSkin().getDrawable("menu-bg"));
        table.add(textureGrid).expand().fill().pad(5).row();
        table.addSeparator().height(1);

        addTextureBtn = new VisTextButton("Add Texture");
        table.add(addTextureBtn).right().row();

        addTextureBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Ui.getInstance().showDialog(textureBrowser);
            }
        });

        textureGrid.setListener(new TextureGrid.OnTextureClickedListener() {
            @Override
            public void onTextureSelected(TextureProvider texture) {
                SplatTexture tex = (SplatTexture) texture;
                toolManager.sphereBrushTool.setSplatChannel(tex.channel);
            }
        });

        setupTextureBrowser();
        setSplatTexturesForTerrain();
    }

    public void setupTextureBrowser() {
        textureBrowser = new TextureBrowser();
        textureBrowser.setTextureListener(new TextureGrid.OnTextureClickedListener() {
            @Override
            public void onTextureSelected(TextureProvider texture) {
                MTexture mTexture = (MTexture) texture;

                TerrainTexture terrainTexture = TerrainPaintTab.this.parent.component.getTerrain().getTerrainTexture();

                // set base
                if(terrainTexture.getTexture(SplatTexture.Channel.BASE).texture.getId() == -1) {
                    SplatTexture st = new SplatTexture(SplatTexture.Channel.BASE, mTexture);
                    terrainTexture.setSplatTexture(st);
                    textureGrid.addTexture(st);
                    textureBrowser.fadeOut();
                    return;
                }

                // set textures in terrainTexture
                final int texCount = terrainTexture.countTextures();
                final SplatTexture st = new SplatTexture();
                st.texture = mTexture;

                if(texCount == 1) {

                    // create empty splat map
                    SplatMap sm = new SplatMap(SplatMap.DEFAULT_SIZE, SplatMap.DEFAULT_SIZE);
                    sm.setPath(ProjectManager.PROJECT_TERRAIN_DIR + parent.component.getTerrain().id + "_splat.png");
                    terrainTexture.setSplatmap(sm);

                    st.channel = SplatTexture.Channel.R;
                    terrainTexture.setSplatTexture(st);
                } else if(texCount == 2) {
                    st.channel = SplatTexture.Channel.G;
                    terrainTexture.setSplatTexture(st);
                } else if(texCount == 3) {
                    st.channel = SplatTexture.Channel.B;
                    terrainTexture.setSplatTexture(st);
                } else if(texCount == 4) {
                    st.channel = SplatTexture.Channel.A;
                    terrainTexture.setSplatTexture(st);
                } else {
                    DialogUtils.showErrorDialog(Ui.getInstance(), "Not more than 5 textures per terrain please :)");
                    return;
                }

                textureBrowser.fadeOut();
                textureGrid.addTexture(st);
            }
        });
    }

    private void setSplatTexturesForTerrain() {
        TerrainTexture terrainTexture = parent.component.getTerrain().getTerrainTexture();
        if(terrainTexture.getTexture(SplatTexture.Channel.BASE).texture.getId() > -1) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.BASE));
        }
        if(terrainTexture.getTexture(SplatTexture.Channel.R) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.R));
        }
        if(terrainTexture.getTexture(SplatTexture.Channel.G) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.G));
        }
        if(terrainTexture.getTexture(SplatTexture.Channel.B) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.B));
        }
        if(terrainTexture.getTexture(SplatTexture.Channel.A) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.A));
        }
    }

    @Override
    public String getTabTitle() {
        return "Paint";
    }

    @Override
    public Table getContentTable() {
        return table;
    }

}
