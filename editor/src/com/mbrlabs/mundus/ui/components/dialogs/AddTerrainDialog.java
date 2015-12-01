package com.mbrlabs.mundus.ui.components.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

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

    }

}
