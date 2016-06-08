/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.ui.modules.dialogs.settings;

import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.registry.Registry;
import com.mbrlabs.mundus.ui.modules.dialogs.BaseDialog;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class SettingsDialog extends BaseDialog {

    private VisSplitPane splitPane;
    private VerticalGroup settingsSelection;

    private GeneralSettingsTable generalSettings;

    @Inject
    private Registry registry;

    public SettingsDialog() {
        super("Settings");
        Mundus.inject(this);
        VisTable root = new VisTable();
        root.padTop(6).padRight(6).padBottom(22);
        add(root);
        //root.debug();

        settingsSelection = new VerticalGroup();
        settingsSelection.addActor(new VisLabel("General"));
        settingsSelection.addActor(new VisLabel("Appearance"));
        settingsSelection.addActor(new VisLabel("Export"));

        generalSettings = new GeneralSettingsTable(registry);

        splitPane = new VisSplitPane(settingsSelection, generalSettings, false);
        splitPane.setSplitAmount(0.3f);
        root.add(splitPane).width(700).minHeight(400).fill().expand();
    }

}
