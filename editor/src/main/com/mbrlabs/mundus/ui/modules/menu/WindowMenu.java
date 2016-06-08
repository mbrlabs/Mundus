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

package com.mbrlabs.mundus.ui.modules.menu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.modules.dialogs.settings.SettingsDialog;

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

        settings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final Ui ui = Ui.getInstance();
                SettingsDialog dialog = ui.getSettingsDialog();
                ui.showDialog(dialog);
            }
        });
    }

    public MenuItem getSettings() {
        return settings;
    }


}
