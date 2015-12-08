package com.mbrlabs.mundus.ui.components.sidebar;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.Editor;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.terrain.brushes.Brush;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class TerrainTab extends Tab {

    private static final String TITLE = "Terrain";

    private VisTable content;

    public TerrainTab() {
        super(false, false);
        content = new VisTable();
        //content.add(new VisLabel("Terrain tab"));
        for(Brush brush : Mundus.brushes) {
            VisImageButton btn = new VisImageButton(brush.getIcon());
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Mundus.input.setCurrentToolInput(brush.getInputProcessor());
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
