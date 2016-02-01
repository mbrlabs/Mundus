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

package com.mbrlabs.mundus.ui.modules.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.commons.model.MTexture;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.commons.terrain.TerrainTexture;
import com.mbrlabs.mundus.commons.utils.TextureUtils;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.SceneGraphChangedEvent;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.terrain.TerrainIO;
import com.mbrlabs.mundus.utils.Log;

/**
 * @author Marcus Brummer
 * @version 01-12-2015
 */
public class AddTerrainDialog extends BaseDialog {

    private static final String TAG = AddTerrainDialog.class.getSimpleName();

    // UI elements
    private VisTextField name = new VisTextField("Terrain");

    private VisTextField vertexResolution = new VisTextField("180");

    private VisTextField terrainWidth = new VisTextField("1200");
    private VisTextField terrainDepth = new VisTextField("1200");
    private VisTextField positionX = new VisTextField("0");
    private VisTextField positionZ = new VisTextField("0");

    private VisTextButton generateBtn = new VisTextButton("GENERATE TERRAIN");

    @Inject
    private ProjectContext projectContext;
    @Inject
    private Shaders shaders;

    public AddTerrainDialog() {
        super("Add Terrain");
        Mundus.inject(this);
        setResizable(true);
        setupUI();
        setupListeners();
    }

    private void setupUI() {
        Table root = new Table();
        //root.debugAll();
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        // left table
        VisTable content = new VisTable();
        content.left().top();
        content.add(new VisLabel("Name: ")).left().padBottom(10);
        content.add(name).fillX().expandX().row();
        content.add(new VisLabel("Vertex resolution: ")).left().padBottom(10);
        content.add(vertexResolution).fillX().expandX().row();
        content.add(new VisLabel("Position on x-axis:")).left().padBottom(10);
        content.add(positionX).fillX().expandX().row();
        content.add(new VisLabel("Position on z-axis: ")).left().padBottom(10);
        content.add(positionZ).fillX().expandX().row();
        content.add(new VisLabel("Terrain width: ")).left().padBottom(10);
        content.add(terrainWidth).fillX().expandX().row();
        content.add(new VisLabel("Terrain depth")).left().padBottom(10);
        content.add(terrainDepth).fillX().expandX().row();
        content.add(generateBtn).fillX().expand().colspan(2).bottom();
        root.add(content).width(500).height(400);
    }

    private void setupListeners() {

        // generate btn
        generateBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                try {
                    String nom = name.getText();

                    int res = Integer.valueOf(vertexResolution.getText());
                    int width = Integer.valueOf(terrainWidth.getText());
                    int depth = Integer.valueOf(terrainDepth.getText());
                    float posX = Float.valueOf(positionX.getText());
                    float posZ = Float.valueOf(positionZ.getText());

                    // create model
                    Terrain terrain = generateTerrain(width, depth, res);
                    terrain.name = nom;
                    terrain.id = projectContext.obtainUUID();
                    terrain.terraPath = ProjectManager.PROJECT_TERRAIN_DIR + terrain.id + "." + TerrainIO.FILE_EXTENSION;
                    terrain.transform.setTranslation(posX, 0, posZ);

                    projectContext.terrains.add(terrain);
                    projectContext.currScene.terrainGroup.add(terrain);

                    SceneGraph sceneGraph = projectContext.currScene.sceneGraph;

                    GameObject terrainGO = new GameObject(sceneGraph);
                    terrainGO.setId(projectContext.obtainUUID());
                    terrainGO.setName(name.getText());
                    terrainGO.transform = terrain.transform;
                    terrainGO.setParent(sceneGraph.getRoot());
                    sceneGraph.getRoot().addChild(terrainGO);

                    TerrainComponent terrainComponent = new TerrainComponent(terrainGO);
                    terrainComponent.setTerrain(terrain);
                    terrainGO.getComponents().add(terrainComponent);
                    terrainComponent.setShader(shaders.terrainShader);

                    Mundus.postEvent(new SceneGraphChangedEvent());

                } catch (NumberFormatException nfe) {
                    Log.error(TAG, nfe.getMessage());
                }

            }
        });

    }

    private Terrain generateTerrain(int terrainWidth, int terrainDepth, int res) {
        Terrain terrain = new Terrain(res);
        terrain.terrainWidth = terrainWidth;
        terrain.terrainDepth = terrainDepth;
        terrain.init();
        terrain.update();

        TerrainTexture splat = terrain.getTerrainTexture();
        MTexture base = new MTexture();
        base.setId(-1);
        base.texture = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/terrain/chess.png"));
        splat.setBase(base);
//        terrainTexture.chanR = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/terrain/red_soil.jpg"));
//        terrainTexture.chanG = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/terrain/pebble.jpg"));
//        terrainTexture.chanB = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/terrain/grass.jpg"));
//        terrainTexture.chanA = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/terrain/stone_path.jpg"));
//        terrainTexture.terrainTexture = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/terrain/splat_map.png"));

        return terrain;
    }

}
