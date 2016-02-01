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
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.tools.brushes.TerrainBrush;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.modules.dialogs.TextureBrowser;
import com.mbrlabs.mundus.ui.widgets.TextureGrid;

/**
 * @author Marcus Brummer
 * @version 30-01-2016
 */
public class TerrainPaintTab extends Tab {

    private TerrainComponentWidget parent;

    private VisTable table;
    private VisTextButton addTextureBtn;
    private TextureGrid textureGrid;

    private TextureBrowser textureBrowser;

    public TerrainPaintTab(final TerrainComponentWidget parent) {
        super(false, false);
        this.parent = parent;
        table = new VisTable();
        table.align(Align.left);
        table.add(new TerrainBrushTable(parent, TerrainBrush.BrushMode.PAINT)).expand().fill().padBottom(5).row();
        table.addSeparator().height(1);

        textureGrid = new TextureGrid(40, 5);
        textureGrid.setBackground(VisUI.getSkin().getDrawable("menu-bg"));
        table.add(textureGrid).expand().fill().pad(5).row();
        table.addSeparator().height(1);

        addTextureBtn = new VisTextButton("Add Texture");
        table.add(addTextureBtn).right().row();

        textureBrowser = new TextureBrowser();
        textureBrowser.setTextureListener(new TextureGrid.OnTextureClickedListener() {
            @Override
            public void onTextureSelected(MTexture texture) {
                TerrainTexture terrainTexture = TerrainPaintTab.this.parent.component.getTerrain().getTerrainTexture();
                int texCount = terrainTexture.countSplatChannelTextures();

                // set base
                if(terrainTexture.hasDefaultBaseTexture()) {
                    terrainTexture.setBase(texture);
                    textureGrid.addTexture(texture);
                    textureBrowser.fadeOut();
                    return;
                }

                // set textures in terrainTexture
                SplatTexture st = new SplatTexture();
                st.texture = texture;
                if(texCount == 0) {

                    // create empty splat map
                    SplatMap sm = new SplatMap(SplatMap.DEFAULT_SIZE, SplatMap.DEFAULT_SIZE);
                    sm.setPath(ProjectManager.PROJECT_TERRAIN_DIR + parent.component.getTerrain().id + "_splat.png");
                    terrainTexture.setSplatmap(sm);

                    st.channel = SplatTexture.Channel.R;
                    terrainTexture.setChanR(st);
                } else if(texCount == 1) {
                    st.channel = SplatTexture.Channel.G;
                    terrainTexture.setChanG(st);
                } else if(texCount == 2) {
                    st.channel = SplatTexture.Channel.B;
                    terrainTexture.setChanB(st);
                } else if(texCount == 3) {
                    st.channel = SplatTexture.Channel.A;
                    terrainTexture.setChanA(st);
                } else {
                    DialogUtils.showErrorDialog(Ui.getInstance(), "Not more than 5 textures per terrain please :)");
                    return;
                }

                textureBrowser.fadeOut();
                textureGrid.addTexture(texture);
            }
        });

        addTextureBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Ui.getInstance().showDialog(textureBrowser);
            }
        });

        textureGrid.setListener(new TextureGrid.OnTextureClickedListener() {
            @Override
            public void onTextureSelected(MTexture texture) {
                // TODO

            }
        });

        setTextures();
    }

    private void setTextures() {
        TerrainTexture terrainTexture = parent.component.getTerrain().getTerrainTexture();
        if(terrainTexture.getBase().getId() > -1) {
            textureGrid.addTexture(terrainTexture.getBase());
        }
        if(terrainTexture.getChanR() != null) {
            textureGrid.addTexture(terrainTexture.getChanR().texture);
        }
        if(terrainTexture.getChanG() != null) {
            textureGrid.addTexture(terrainTexture.getChanG().texture);
        }
        if(terrainTexture.getChanB() != null) {
            textureGrid.addTexture(terrainTexture.getChanB().texture);
        }
        if(terrainTexture.getChanA() != null) {
            textureGrid.addTexture(terrainTexture.getChanA().texture);
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
