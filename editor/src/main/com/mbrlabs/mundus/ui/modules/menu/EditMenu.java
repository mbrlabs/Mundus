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
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.history.CommandHistory;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class EditMenu extends Menu {

    private MenuItem copy;
    private MenuItem paste;
    private MenuItem undo;
    private MenuItem redo;

    @Inject
    private CommandHistory history;

    public EditMenu() {
        super("Edit");
        Mundus.inject(this);

        copy = new MenuItem("Copy");
        copy.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.C);
        paste = new MenuItem("Paste");
        paste.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.P);
        undo = new MenuItem("Undo");
        undo.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.Z);
        redo = new MenuItem("Redo");
        redo.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.Y);

        addItem(copy);
        addItem(paste);
        addItem(undo);
        addItem(redo);

        setupListeners();
    }

    private void setupListeners() {
        // undo
        undo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                history.goBack();
            }
        });

        // redo
        redo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                history.goForward();
            }
        });

    }

}
