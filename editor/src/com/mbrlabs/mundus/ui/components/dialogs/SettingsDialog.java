package com.mbrlabs.mundus.ui.components.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class SettingsDialog extends BaseDialog {

    private VisSplitPane splitPane;
    private VerticalGroup settingsSelection;
    private VisTable content;

    public SettingsDialog() {
        super("Settings");
        settingsSelection = new VerticalGroup();
        settingsSelection.addActor(new VisLabel("General"));
        settingsSelection.addActor(new VisLabel("Terrain"));
        settingsSelection.addActor(new VisLabel("Objects"));
        settingsSelection.addActor(new VisLabel("Export Settings"));

        content = new VisTable();

        splitPane = new VisSplitPane(settingsSelection, content, false);
        getContentTable().add(splitPane).fill().expand();
    }

}
