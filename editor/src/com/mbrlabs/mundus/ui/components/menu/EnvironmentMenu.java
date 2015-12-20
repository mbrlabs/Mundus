package com.mbrlabs.mundus.ui.components.menu;

import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;

/**
 * @author Marcus Brummer
 * @version 20-12-2015
 */
public class EnvironmentMenu extends Menu {

    private MenuItem addLight;
    private MenuItem fog;

    public EnvironmentMenu() {
        super("Environment");

        addLight = new MenuItem("Add Light");
        fog = new MenuItem("Fog");

        addItem(addLight);
        addItem(fog);
    }

    public MenuItem getAddLight() {
        return addLight;
    }

    public MenuItem getFog() {
        return fog;
    }
}

