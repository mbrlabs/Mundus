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

package com.mbrlabs.mundus.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.modules.dialogs.assets.AssetSelectionDialog;

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
public class AssetSelectionField extends VisTable {

    private VisTextField textField;
    private VisTextButton btn;

    private AssetSelectionDialog.AssetSelectionListener listener;
    private AssetSelectionDialog.AssetFilter filter;

    private AssetSelectionDialog.AssetSelectionListener internalListener;

    public AssetSelectionField() {
        super();

        textField = new VisTextField();
        textField.setDisabled(true);
        btn = new VisTextButton("Select");

        add(textField).grow();
        add(btn).padLeft(5).row();

        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Ui.getInstance().getAssetSelectionDialog().show(true, filter, internalListener);
            }
        });

        internalListener = asset -> {
            setAsset(asset);
            if (listener != null) {
                listener.onSelected(asset);
            }
        };
    }

    public AssetSelectionField setListener(AssetSelectionDialog.AssetSelectionListener listener) {
        this.listener = listener;
        return this;
    }

    public AssetSelectionField setFilter(AssetSelectionDialog.AssetFilter filter) {
        this.filter = filter;
        return this;
    }

    public void setAsset(Asset asset) {
        textField.setText(asset == null ? "None" : asset.getName());
    }

}
