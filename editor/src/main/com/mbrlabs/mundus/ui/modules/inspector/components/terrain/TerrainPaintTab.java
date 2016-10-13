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

package com.mbrlabs.mundus.ui.modules.inspector.components.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.assets.AssetAlreadyExistsException;
import com.mbrlabs.mundus.assets.EditorAssetManager;
import com.mbrlabs.mundus.commons.assets.PixmapTextureAsset;
import com.mbrlabs.mundus.commons.assets.TerrainAsset;
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.commons.terrain.SplatTexture;
import com.mbrlabs.mundus.commons.terrain.TerrainTexture;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.AssetImportEvent;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.tools.brushes.TerrainBrush;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.modules.dialogs.assets.AssetSelectionDialog;
import com.mbrlabs.mundus.ui.modules.dialogs.assets.AssetTextureFilter;
import com.mbrlabs.mundus.ui.widgets.TextureGrid;
import com.mbrlabs.mundus.utils.Log;

import java.io.IOException;

/**
 * @author Marcus Brummer
 * @version 30-01-2016
 */
public class TerrainPaintTab extends Tab {

    private static final String TAG = TerrainPaintTab.class.getSimpleName();

    private TerrainComponentWidget parent;

    private VisTable root;
    private VisTextButton addTextureBtn;
    private TextureGrid<SplatTexture> textureGrid;

    private TextureRightClickMenu rightClickMenu;

    @Inject
    private ToolManager toolManager;
    @Inject
    private ProjectManager projectManager;

    public TerrainPaintTab(final TerrainComponentWidget parent) {
        super(false, false);
        Mundus.inject(this);
        this.parent = parent;
        root = new VisTable();
        root.align(Align.left);

        // brushes
        root.add(new TerrainBrushGrid(parent, TerrainBrush.BrushMode.PAINT)).expand().fill().padBottom(5).row();

        // textures
        root.add(new VisLabel("Textures:")).padLeft(5).left().row();
        textureGrid = new TextureGrid<>(40, 5);
        textureGrid.setBackground(VisUI.getSkin().getDrawable("menu-bg"));
        root.add(textureGrid).expand().fill().pad(5).row();

        // add texture
        addTextureBtn = new VisTextButton("Add Texture");
        root.add(addTextureBtn).padRight(5).right().row();

        rightClickMenu = new TextureRightClickMenu();

        setupAddTextureBrowser();
        setupTextureGrid();
    }

    public void setupAddTextureBrowser() {
        addTextureBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AssetSelectionDialog dialog = Ui.getInstance().getAssetSelectionDialog();
                dialog.show(false, new AssetTextureFilter(), asset -> {
                    try {
                        addTexture((TextureAsset) asset);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Ui.getInstance().getToaster().error("Error while creating the splatmap");
                    }
                });
            }
        });
    }

    private void addTexture(TextureAsset textureAsset) throws IOException {
        EditorAssetManager assetManager = projectManager.current().assetManager;

        TerrainAsset terrainAsset = TerrainPaintTab.this.parent.component.getTerrain();
        TerrainTexture terrainTexture = terrainAsset.getTerrain().getTerrainTexture();

        // channel base
        if (terrainAsset.getSplatBase() == null) {
            terrainAsset.setSplatBase(textureAsset);
            terrainAsset.applyDependencies();
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.BASE));
            return;
        }

        // create splatmap
        if (terrainAsset.getSplatmap() == null) {
            try {
                PixmapTextureAsset splatmap = assetManager.createPixmapTextureAsset(512);
                terrainAsset.setSplatmap(splatmap);
                terrainAsset.applyDependencies();
                terrainAsset.getMeta().save();
                Mundus.postEvent(new AssetImportEvent(splatmap));
            } catch (AssetAlreadyExistsException e) {
                Log.exception(TAG, e);
                return;
            }
        }

        // channel r
        if (terrainAsset.getSplatR() == null) {
            terrainAsset.setSplatR(textureAsset);
            terrainAsset.applyDependencies();
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.R));
            return;
        }

        // channel g
        if (terrainAsset.getSplatG() == null) {
            terrainAsset.setSplatG(textureAsset);
            terrainAsset.applyDependencies();
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.G));
            return;
        }

        // channel b
        if (terrainAsset.getSplatB() == null) {
            terrainAsset.setSplatB(textureAsset);
            terrainAsset.applyDependencies();
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.B));
            return;
        }

        // channel a
        if (terrainAsset.getSplatA() == null) {
            terrainAsset.setSplatA(textureAsset);
            terrainAsset.applyDependencies();
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.A));
            return;
        }

        Dialogs.showErrorDialog(Ui.getInstance(), "Not more than 5 textures per terrain please :)");
    }

    private void setupTextureGrid() {
        textureGrid.setListener((texture, leftClick) -> {
            SplatTexture tex = (SplatTexture) texture;
            if (leftClick) {
                TerrainBrush.setPaintChannel(tex.channel);
            } else {
                System.out.println("Texture grid listener right clicked");
                rightClickMenu.setChannel(tex.channel);
                rightClickMenu.show();
            }
        });

        setTexturesInUiGrid();
    }

    private void setTexturesInUiGrid() {
        textureGrid.removeTextures();
        TerrainTexture terrainTexture = parent.component.getTerrain().getTerrain().getTerrainTexture();
        if (terrainTexture.getTexture(SplatTexture.Channel.BASE) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.BASE));
        }
        if (terrainTexture.getTexture(SplatTexture.Channel.R) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.R));
        }
        if (terrainTexture.getTexture(SplatTexture.Channel.G) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.G));
        }
        if (terrainTexture.getTexture(SplatTexture.Channel.B) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.B));
        }
        if (terrainTexture.getTexture(SplatTexture.Channel.A) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.A));
        }
    }

    @Override
    public String getTabTitle() {
        return "Paint";
    }

    @Override
    public Table getContentTable() {
        return root;
    }

    /**
     *
     */
    private class TextureRightClickMenu extends PopupMenu {

        private MenuItem removeTexture;
        private MenuItem changeTexture;

        private SplatTexture.Channel channel;

        public TextureRightClickMenu() {
            super();

            removeTexture = new MenuItem("Remove texture");
            changeTexture = new MenuItem("Change texture");

            addItem(removeTexture);
            addItem(changeTexture);

            removeTexture.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (channel != null) {
                        TerrainAsset terrain = parent.component.getTerrain();
                        if (channel == SplatTexture.Channel.R) {
                            terrain.setSplatR(null);
                        } else if (channel == SplatTexture.Channel.G) {
                            terrain.setSplatG(null);
                        } else if (channel == SplatTexture.Channel.B) {
                            terrain.setSplatB(null);
                        } else if (channel == SplatTexture.Channel.A) {
                            terrain.setSplatA(null);
                        } else {
                            Ui.getInstance().getToaster().error("Can't remove the base texture");
                            return;
                        }

                        terrain.applyDependencies();
                        setTexturesInUiGrid();
                    }
                }
            });

            changeTexture.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (channel != null) {
                        AssetSelectionDialog dialog = Ui.getInstance().getAssetSelectionDialog();
                        dialog.show(false, new AssetTextureFilter(), asset -> {
                            if (channel != null) {
                                TerrainAsset terrain = parent.component.getTerrain();
                                TextureAsset texture = (TextureAsset) asset;
                                if (channel == SplatTexture.Channel.BASE) {
                                    terrain.setSplatBase(texture);
                                } else if (channel == SplatTexture.Channel.R) {
                                    terrain.setSplatR(texture);
                                } else if (channel == SplatTexture.Channel.G) {
                                    terrain.setSplatG(texture);
                                } else if (channel == SplatTexture.Channel.B) {
                                    terrain.setSplatB(texture);
                                } else if (channel == SplatTexture.Channel.A) {
                                    terrain.setSplatA(texture);
                                }
                                terrain.applyDependencies();
                                setTexturesInUiGrid();
                            }
                        });
                    }
                }
            });

        }

        public void setChannel(SplatTexture.Channel channel) {
            this.channel = channel;
        }

        public void show() {
            showMenu(Ui.getInstance(), Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        }

    }

}
