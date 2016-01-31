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

package com.mbrlabs.mundus.ui.modules.menu;

import com.badlogic.gdx.Input;
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
