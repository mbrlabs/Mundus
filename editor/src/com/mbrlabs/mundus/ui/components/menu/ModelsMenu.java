package com.mbrlabs.mundus.ui.components.menu;

import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class ModelsMenu extends Menu {

    private MenuItem importModel;

    public ModelsMenu() {
        super("Models");

        importModel = new MenuItem("Import Model");

        addItem(importModel);
    }

    public MenuItem getImportModel() {
        return importModel;
    }
}
