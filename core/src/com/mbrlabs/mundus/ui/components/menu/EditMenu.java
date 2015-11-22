package com.mbrlabs.mundus.ui.components.menu;

import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class EditMenu extends Menu {

    private MenuItem copy;
    private MenuItem paste;
    private MenuItem undo;
    private MenuItem redo;

    public EditMenu() {
        super("Edit");

        copy = new MenuItem("Copy");
        paste = new MenuItem("Paste");
        undo = new MenuItem("Undo");
        redo = new MenuItem("Redo");

        addItem(copy);
        addItem(paste);
        addItem(undo);
        addItem(redo);

    }

    public MenuItem getRedo() {
        return redo;
    }

    public MenuItem getCopy() {
        return copy;
    }

    public MenuItem getPaste() {
        return paste;
    }

    public MenuItem getUndo() {
        return undo;
    }
}
