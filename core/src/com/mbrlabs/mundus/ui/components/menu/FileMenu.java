package com.mbrlabs.mundus.ui.components.menu;

import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;


/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class FileMenu extends Menu {

    private MenuItem newProject;
    private MenuItem openProject;
    private MenuItem saveProject;

    public FileMenu() {
        super("File");

        newProject = new MenuItem("New Project");
        openProject = new MenuItem("Open Project");
        saveProject = new MenuItem("Save Project");

        addItem(newProject);
        addItem(openProject);
        addItem(saveProject);
    }

    public MenuItem getNewProject() {
        return newProject;
    }

    public MenuItem getOpenProject() {
        return openProject;
    }

    public MenuItem getSaveProject() {
        return saveProject;
    }
}
