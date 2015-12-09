package com.mbrlabs.mundus.ui.components.docker.assets;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * @author Marcus Brummer
 * @version 09-12-2015
 */
public class AssetItem extends VisTable {

    private VisLabel nameLabel;

    public AssetItem(String name) {
        super();
        setBackground("menu-bg");
        nameLabel = new VisLabel(name);
        add(nameLabel).row();
    }

}
