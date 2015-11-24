package com.mbrlabs.mundus.ui.components.menu;

import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;


/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class MundusMenuBar extends MenuBar {

    private Menu fileMenu;
    private Menu editMenu;
    private Menu windowMenu;

    public MundusMenuBar() {
        super();
        fileMenu = new FileMenu();
        editMenu = new EditMenu();
        windowMenu = new WindowMenu();

        addMenu(fileMenu);
        addMenu(editMenu);
        addMenu(windowMenu);
    }

    public Menu getFileMenu() {
        return fileMenu;
    }

    public Menu getEditMenu() {
        return editMenu;
    }

    public Menu getWindowMenu() {
        return windowMenu;
    }

}
