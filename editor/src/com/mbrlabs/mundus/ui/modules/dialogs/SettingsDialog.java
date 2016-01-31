/*
 * Copyright (c) 2015. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.ui.modules.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.core.HomeManager;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.ui.widgets.FileChooserField;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class SettingsDialog extends BaseDialog {

    private VisSplitPane splitPane;
    private VerticalGroup settingsSelection;
    private VisTable content;

    private FileChooserField fbxBinary;

    private VisTextButton save;

    @Inject
    private HomeManager homeManager;

    public SettingsDialog() {
        super("Settings");
        Mundus.inject(this);
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
        fbxBinary = new FileChooserField(300);
        content.add(fbxBinary).row();

        save = new VisTextButton("Save");
        content.add(save).width(93).height(25).padTop(15).colspan(2);

        addHandlers();
    }

    public void reloadSettings() {
        fbxBinary.setText(homeManager.homeDescriptor.settings.fbxConvBinary);
    }

    private void addHandlers() {
        save.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                String fbxPath = fbxBinary.getPath();
                homeManager.homeDescriptor.settings.fbxConvBinary = fbxPath;
                homeManager.save();
            }
        });
    }

}
