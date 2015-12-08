package com.mbrlabs.mundus.ui.components.sidebar;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.Mundus;
import com.mbrlabs.mundus.terrain.brushes.TerrainHeightBrush;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class TerrainTab extends Tab {

    private static final String TITLE = "Terrain";

    private VisTable content;

    private VisImageButton sphereBrushBtn;

    public TerrainTab() {
        super(false, false);
        content = new VisTable();
        //content.add(new VisLabel("Terrain tab"));
        for(TerrainHeightBrush brush : Mundus.brushes) {
            VisImageButton btn = new VisImageButton(brush.getIcon());
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Mundus.inputManager.setCurrentToolInput(brush.getInputProcessor());
                }
            });
            content.add(btn).row();
        }
    }

    @Override
    public String getTabTitle() {
        return TITLE;
    }

    @Override
    public Table getContentTable() {
        return content;
    }

}
