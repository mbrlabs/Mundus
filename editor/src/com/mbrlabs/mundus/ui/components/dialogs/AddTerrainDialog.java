package com.mbrlabs.mundus.ui.components.dialogs;

import com.badlogic.gdx.graphics.VertexAttributes;
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
import com.mbrlabs.mundus.terrain.Terrain;
import com.mbrlabs.mundus.ui.components.MinimapWidget;
import com.mbrlabs.mundus.utils.Log;

/**
 * @author Marcus Brummer
 * @version 01-12-2015
 */
public class AddTerrainDialog extends BaseDialog {

    private static final String TAG = AddTerrainDialog.class.getSimpleName();

    // UI elements
    private VisTextField name = new VisTextField();
    private MinimapWidget minimap;

    private VisTextField vertexResolution = new VisTextField("64");

    private VisTextField terrainWidth = new VisTextField("50");
    private VisTextField terrainDepth = new VisTextField("50");
    private VisTextField positionX = new VisTextField();
    private VisTextField positionZ = new VisTextField();

    private VisTextButton minimapZoomIn = new VisTextButton("+ zoom");
    private VisTextButton minimapZoomOut = new VisTextButton("- zoom");

    private VisTextButton generateBtn = new VisTextButton("GENERATE TERRAIN");

    @Inject
    private ProjectContext projectContext;

    public AddTerrainDialog() {
        super("Add Terrain");
        Mundus.inject(this);
        setupUI();
        setupListeners();
    }

    private void setupUI() {
        Table root = new Table();
        //root.debugAll();
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        minimap = new MinimapWidget(projectContext.terrains);

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
        rightTable.add(minimap).expand().fill().padBottom(5).colspan(2).row();
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


                    Terrain terrain = generateTerrain(nom, posX, posZ, width, depth, res);
                    projectContext.terrains.add(terrain);

                } catch (NumberFormatException nfe) {
                    Log.error(TAG, nfe.getMessage());
                }

            }
        });

        minimapZoomIn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                minimap.zoom(-0.03f);
            }
        });

        minimapZoomOut.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                minimap.zoom(0.03f);
            }
        });

    }

    private Terrain generateTerrain(String name, float posX, float posZ, int terrainWidth, int terrainDepth,
                                 int res) {
        Terrain terrain = new Terrain(res, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        terrain.position.x = posX;
        terrain.position.z = posZ;
        terrain.terrainWidth = terrainWidth;
        terrain.terrainDepth = terrainDepth;
        terrain.update();

        return terrain;
    }

}
