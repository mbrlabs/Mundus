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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.modules.dialogs.assets.AssetSelectionDialog;
import com.mbrlabs.mundus.utils.Log;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class AssetsMenu extends Menu {

    private MenuItem importMesh;
    private MenuItem assetBrowser;

    public AssetsMenu() {
        super("Assets");

        importMesh = new MenuItem("Import Mesh");
        assetBrowser = new MenuItem("Asset Browser");
        addItem(importMesh);
        addItem(assetBrowser);

        addListeners();
    }

    private void addListeners() {
        importMesh.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Ui ui = Ui.getInstance();
                ui.showDialog(ui.getImportMeshDialog());
            }
        });

        assetBrowser.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Ui ui = Ui.getInstance();

                ui.getAssetSelectionDialog().show(new AssetSelectionDialog.AssetFilter() {
                    @Override
                    public boolean ignore(Asset asset) {
                        return false;
                    }
                }, new AssetSelectionDialog.AssetSelectionListener() {
                    @Override
                    public void onSelected(Array<Asset> assets) {
                        for (Asset asset : assets) {
                            Log.trace("AssetMenu", asset.toString());
                        }
                    }
                });

            }
        });
    }

}
