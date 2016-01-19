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

package com.mbrlabs.mundus.ui.components.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.commons.terrain.TerrainInstance;
import com.mbrlabs.mundus.events.SceneGraphModified;
import com.mbrlabs.mundus.scene3d.GameObject;
import com.mbrlabs.mundus.scene3d.SceneGraph;
import com.mbrlabs.mundus.scene3d.TerrainComponent;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.utils.Log;
import com.mbrlabs.mundus.commons.utils.TextureUtils;

/**
 * @author Marcus Brummer
 * @version 01-12-2015
 */
public class AddTerrainDialog extends BaseDialog {

    private static final String TAG = AddTerrainDialog.class.getSimpleName();

    // UI elements
    private VisTextField name = new VisTextField("Name");

    private VisTextField vertexResolution = new VisTextField("180");

    private VisTextField terrainWidth = new VisTextField("1200");
    private VisTextField terrainDepth = new VisTextField("1200");
    private VisTextField positionX = new VisTextField("0");
    private VisTextField positionZ = new VisTextField("0");

    private VisTextButton minimapZoomIn = new VisTextButton("+ zoom");
    private VisTextButton minimapZoomOut = new VisTextButton("- zoom");

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
        VisTable leftTable = new VisTable();
        leftTable.left().top();
        leftTable.add(new VisLabel("Name: ")).left().padBottom(10);
        leftTable.add(name).fillX().expandX().row();
        leftTable.add(new VisLabel("Vertex resolution: ")).left().padBottom(10);
        leftTable.add(vertexResolution).fillX().expandX().row();
        leftTable.add(new VisLabel("Position on x-axis:")).left().padBottom(10);
        leftTable.add(positionX).fillX().expandX().row();
        leftTable.add(new VisLabel("Position on z-axis: ")).left().padBottom(10);
        leftTable.add(positionZ).fillX().expandX().row();
        leftTable.add(new VisLabel("Terrain width: ")).left().padBottom(10);
        leftTable.add(terrainWidth).fillX().expandX().row();
        leftTable.add(new VisLabel("Terrain depth")).left().padBottom(10);
        leftTable.add(terrainDepth).fillX().expandX().row();
        leftTable.add(generateBtn).fillX().expand().colspan(2).bottom();

        // right table
        VisTable rightTable = new VisTable();
        rightTable.top();
        // TODO add minimap again
        rightTable.add(new VisLabel("minmap")).expand().fill().padBottom(5).colspan(2).row();
        rightTable.add(minimapZoomIn).fillX().expandX().padRight(5);
        rightTable.add(minimapZoomOut).fillX().expandX().padLeft(5);


        root.add(leftTable).width(500).height(400).padRight(10);
        root.add(rightTable).width(500).height(400).expand().fill();
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
                    projectContext.terrains.add(terrain);

                    // create Instance
                    TerrainInstance terrainInstance = new TerrainInstance(terrain);
                    terrainInstance.transform.setTranslation(posX, 0, posZ);
                    projectContext.currScene.terrainGroup.add(terrainInstance);

                    SceneGraph sceneGraph = projectContext.currScene.sceneGraph;

                    GameObject terrainGO = new GameObject(sceneGraph);
                    terrainGO.setId(projectContext.obtainUUID());
                    terrainGO.setName("Terrain");
                    terrainGO.transform.translate(posX, 0, posZ);
                    terrainGO.setParent(sceneGraph.getRoot());
                    sceneGraph.getRoot().addChild(terrainGO);

                    TerrainComponent terrainComponent = new TerrainComponent(terrainGO);
                    terrainComponent.setTerrainInstance(terrainInstance);
                    terrainGO.getComponents().add(terrainComponent);
                    terrainComponent.setShader(shaders.terrainShader);

                    Mundus.postEvent(new SceneGraphModified());

                } catch (NumberFormatException nfe) {
                    Log.error(TAG, nfe.getMessage());
                }

            }
        });

    }

    private Terrain generateTerrain(int terrainWidth, int terrainDepth, int res) {
        Texture tex = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/chess.png"));
        Terrain terrain = new Terrain(res);
        terrain.terrainWidth = terrainWidth;
        terrain.terrainDepth = terrainDepth;
        terrain.init();
        terrain.update();
        terrain.setTexture(tex);

        return terrain;
    }

}
