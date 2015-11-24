package com.mbrlabs.mundus.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTree;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class EntityPerspectiveTable extends Table {

    public EntityPerspectiveTable() {
        super();
        add(new Label("entities", VisUI.getSkin())).width(200).expandY();
    }

}
