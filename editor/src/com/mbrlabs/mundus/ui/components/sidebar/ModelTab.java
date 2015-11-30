package com.mbrlabs.mundus.ui.components.sidebar;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class ModelTab extends Tab {

    private static final String TITLE = "Models";

    private VisTable content;

    public ModelTab() {
        super(false, false);
        content = new VisTable();
        content.add(new VisLabel("Model tab"));
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
