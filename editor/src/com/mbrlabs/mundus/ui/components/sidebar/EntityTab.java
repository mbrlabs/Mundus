package com.mbrlabs.mundus.ui.components.sidebar;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.ui.ReloadableData;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class EntityTab extends Tab implements ReloadableData {

    private static final String TITLE = "Entities";

    private VisTable content;

    public EntityTab() {
        super(false, false);
        content = new VisTable();
        content.add(new VisLabel("Entity tab"));
    }

    @Override
    public String getTabTitle() {
        return TITLE;
    }

    @Override
    public Table getContentTable() {
        return content;
    }

    @Override
    public void reloadData() {

    }

}

