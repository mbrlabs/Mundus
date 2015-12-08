package com.mbrlabs.mundus.ui.components.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.*;
import com.mbrlabs.mundus.core.data.home.MundusHome;
import com.mbrlabs.mundus.core.data.home.Settings;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class SettingsDialog extends BaseDialog {

    private VisSplitPane splitPane;
    private VerticalGroup settingsSelection;
    private VisTable content;

    private VisTextField path;

    private VisTextButton save;

    public SettingsDialog() {
        super("Settings");

        VisTable root = new VisTable();
        root.padTop(6).padRight(6).padBottom(22);
        add(root);
        //root.debug();

        settingsSelection = new VerticalGroup();
        settingsSelection.addActor(new VisLabel("General"));
        settingsSelection.addActor(new VisLabel("Terrain"));
        settingsSelection.addActor(new VisLabel("Objects"));
        settingsSelection.addActor(new VisLabel("Export Settings"));

        content = new VisTable();
        content.top().left();

        content.padTop(6).padRight(6).padLeft(6).padBottom(22);

        splitPane = new VisSplitPane(settingsSelection, content, false);
        splitPane.setSplitAmount(0.3f);
        root.add(splitPane).width(700).minHeight(400).fill().expand();

        content.add(new VisLabel("fbx-conv:")).padRight(5);
        path = new VisTextField();
        content.add(path).width(300).row();

        save = new VisTextButton("Save");
        content.add(save).width(93).height(25).padTop(15).colspan(2);

        addHandlers();
    }

    public void reloadSettings() {
        Settings settings = MundusHome.getInstance().getSettings();
        path.setText(settings.getFbxConvBinary());
    }

    private void addHandlers() {
        save.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                String fbxPath = path.getText();
                MundusHome.getInstance().getSettings().setFbxConvBinary(fbxPath);
                MundusHome.getInstance().save();
            }
        });
    }

}
