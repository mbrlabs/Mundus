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

package com.mbrlabs.mundus.editor.ui.modules.dialogs.settings;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.editor.core.Inject;
import com.mbrlabs.mundus.editor.core.Mundus;
import com.mbrlabs.mundus.editor.core.kryo.KryoManager;
import com.mbrlabs.mundus.editor.core.registry.KeyboardLayout;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.events.SettingsChangedEvent;
import com.mbrlabs.mundus.editor.ui.Ui;
import com.mbrlabs.mundus.editor.ui.widgets.FileChooserField;

/**
 * @author Marcus Brummer
 * @version 29-02-2016
 */
public class GeneralSettingsTable extends VisTable {

    private FileChooserField fbxBinary;
    private VisSelectBox<KeyboardLayout> keyboardLayouts;
    private VisTextButton save;

    private Registry registry;

    @Inject
    private KryoManager kryoManager;

    public GeneralSettingsTable(Registry registry) {
        super();
        Mundus.inject(this);

        this.registry = registry;
        top().left();
        padTop(6).padRight(6).padLeft(6).padBottom(22);

        add(new VisLabel("General Settings")).left().colspan(2).row();
        addSeparator().colspan(2).padBottom(15);
        add(new VisLabel("fbx-conv:")).left().padRight(5);
        fbxBinary = new FileChooserField(300);
        add(fbxBinary).left().row();

        keyboardLayouts = new VisSelectBox<>();
        keyboardLayouts.setItems(KeyboardLayout.QWERTY, KeyboardLayout.QWERTZ);
        keyboardLayouts.setSelected(registry.getSettings().getKeyboardLayout());

        add(new VisLabel("Keyboard Layout:")).left();
        add(keyboardLayouts).left().row();

        save = new VisTextButton("Save");
        save.align(Align.bottom);
        add(save).expandX().fillX().expandY().height(25).bottom().colspan(2).row();

        addHandlers();
        reloadSettings();
    }

    public void reloadSettings() {
        fbxBinary.setText(registry.getSettings().getFbxConvBinary());
    }

    private void addHandlers() {
        save.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                String fbxPath = fbxBinary.getPath();
                registry.getSettings().setFbxConvBinary(fbxPath);
                kryoManager.saveRegistry(registry);
                Mundus.postEvent(new SettingsChangedEvent(registry.getSettings()));
                Ui.getInstance().getToaster().success("Settings saved");
            }
        });

        keyboardLayouts.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                KeyboardLayout selection = keyboardLayouts.getSelected();
                registry.getSettings().setKeyboardLayout(selection);
            }
        });
    }

}
