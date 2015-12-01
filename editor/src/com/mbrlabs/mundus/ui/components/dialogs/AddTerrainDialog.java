package com.mbrlabs.mundus.ui.components.dialogs;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.Mundus;
import com.mbrlabs.mundus.terrain.Terrain;
import com.mbrlabs.mundus.utils.Log;

/**
 * @author Marcus Brummer
 * @version 01-12-2015
 */
public class AddTerrainDialog extends BaseDialog {

    private static final String TAG = AddTerrainDialog.class.getSimpleName();

    // UI elements
    private Container terrainsOverviewContainer;
    private VisTextField name = new VisTextField();

    private VisTextField verticesOnX = new VisTextField();
    private VisTextField verticesOnZ = new VisTextField();

    private VisTextField corner00 = new VisTextField();
    private VisTextField corner01 = new VisTextField();
    private VisTextField corner10 = new VisTextField();
    private VisTextField corner11 = new VisTextField();

    private VisTextButton generateBtn = new VisTextButton("GENERATE TERRAIN");

    public AddTerrainDialog() {
        super("Add Terrain");
        setupUI();
        setupListeners();
    }

    private void setupUI() {
        Table root = new Table();
        //root.debugAll();
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        VisTable inputTable = new VisTable();
        terrainsOverviewContainer = new Container();
        terrainsOverviewContainer.setBackground(VisUI.getSkin().getDrawable("default-pane"));
        terrainsOverviewContainer.setActor(new VisLabel("TERRAINS OVERVIEW"));

        root.add(inputTable).width(500).height(400).padRight(10);
        root.add(terrainsOverviewContainer).width(500).height(400);

        inputTable.left().top();
        inputTable.add(new VisLabel("Name: ")).left().padBottom(10);
        inputTable.add(name).fillX().expandX().row();
        inputTable.add(new VisLabel("Vertices on x-axis: ")).left().padBottom(10);
        inputTable.add(verticesOnX).fillX().expandX().row();
        inputTable.add(new VisLabel("Vertices on z-axis: ")).left().padBottom(10);
        inputTable.add(verticesOnZ).fillX().expandX().row();
        inputTable.add(new VisLabel("corner 00: ")).left().padBottom(10);
        inputTable.add(corner00).fillX().expandX().row();
        inputTable.add(new VisLabel("corner 01: ")).left().padBottom(10);
        inputTable.add(corner01).fillX().expandX().row();
        inputTable.add(new VisLabel("corner 10: ")).left().padBottom(10);
        inputTable.add(corner10).fillX().expandX().row();
        inputTable.add(new VisLabel("corner 11: ")).left().padBottom(10);
        inputTable.add(corner11).fillX().expandX().row();
        inputTable.add(generateBtn).fillX().expand().colspan(2).bottom();
    }

    private void setupListeners() {

        // generate btn
        generateBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                try {
                    String nom = name.getText();
                    int vertsX = Integer.valueOf(verticesOnX.getText());
                    int vertsZ = Integer.valueOf(verticesOnZ.getText());

                    Vector3 c00 = parseCorner(corner00.getText());
                    Vector3 c01 = parseCorner(corner01.getText());
                    Vector3 c10 = parseCorner(corner10.getText());
                    Vector3 c11 = parseCorner(corner11.getText());

                    if(c00 != null && c01 != null && c10 != null && c11 != null) {
                        Terrain terrain = generateTerrain(nom, c00, c01, c10, c11, vertsX, vertsZ);
                        Mundus.projectContext.terrains.add(terrain);
                    }

                } catch (NumberFormatException nfe) {
                    Log.error(TAG, nfe.getMessage());
                }

                //Terrain terrain = new Terrain();
            }
        });

    }

    private Vector3 parseCorner(String input) {
        if(input == null || input.isEmpty())  return null;

        String[] split = input.split(",");
        if(split.length != 3) return null;

        try {
            float x = Float.valueOf(split[0]);
            float y = Float.valueOf(split[1]);
            float z = Float.valueOf(split[2]);
            return new Vector3(x, y, z);
        } catch (NumberFormatException nfe) {
            Log.error(TAG, nfe.getMessage());
            return null;
        }
    }

    private Terrain generateTerrain(String name, Vector3 c00, Vector3 c01, Vector3 c10, Vector3 c11,
                                 int vertCountX, int vertCountZ) {
        Terrain terrain = new Terrain(vertCountX, vertCountZ, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        terrain.corner00.set(c00);
        terrain.corner01.set(c01);
        terrain.corner10.set(c10);
        terrain.corner11.set(c11);
        terrain.update();

        return terrain;
    }

}
