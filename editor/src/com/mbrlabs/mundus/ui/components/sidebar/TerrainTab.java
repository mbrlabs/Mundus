package com.mbrlabs.mundus.ui.components.sidebar;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

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
        content.add(new VisLabel("Terrain tab"));
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
