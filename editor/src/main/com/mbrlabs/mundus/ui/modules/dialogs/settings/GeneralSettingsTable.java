/*
 * Copyright (c) 2016. See AUTHORS file.
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

package com.mbrlabs.mundus.ui.modules.dialogs.settings;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.core.HomeManager;
import com.mbrlabs.mundus.ui.widgets.FileChooserField;

/**
 * @author Marcus Brummer
 * @version 29-02-2016
 */
public class GeneralSettingsTable extends VisTable {

    private FileChooserField fbxBinary;
    private VisTextButton save;

    private HomeManager homeManager;

    public GeneralSettingsTable(HomeManager homeManager) {
        super();
        this.homeManager = homeManager;
        top().left();
        padTop(6).padRight(6).padLeft(6).padBottom(22);

        add(new VisLabel("General Settings")).left().colspan(2).row();
        addSeparator().colspan(2).padBottom(15);
        add(new VisLabel("fbx-conv:")).left().padRight(5);
        fbxBinary = new FileChooserField(300);
        add(fbxBinary).left().row();

        save = new VisTextButton("Save");
        save.align(Align.bottom);
        add(save).expandX().fillX().expandY().height(25).bottom().colspan(2);

        addHandlers();
        reloadSettings();
    }

    public void reloadSettings() {
        fbxBinary.setText(homeManager.homeDescriptor.settingsDescriptor.fbxConvBinary);
    }

    private void addHandlers() {
        save.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                String fbxPath = fbxBinary.getPath();
                homeManager.homeDescriptor.settingsDescriptor.fbxConvBinary = fbxPath;
                homeManager.save();
            }
        });
    }

}
