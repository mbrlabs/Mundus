package com.mbrlabs.mundus.ui.components.menu;

import com.badlogic.gdx.Input;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class WindowMenu extends Menu {

    private MenuItem settings;

    public WindowMenu() {
        super("Window");

        settings = new MenuItem("Settings");
        settings.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.ALT_LEFT, Input.Keys.S);

        addItem(settings);
    }

    public MenuItem getSettings() {
        return settings;
    }

}
