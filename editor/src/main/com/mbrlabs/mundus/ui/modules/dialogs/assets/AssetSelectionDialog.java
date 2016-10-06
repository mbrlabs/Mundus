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

package com.mbrlabs.mundus.ui.modules.dialogs.assets;

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.adapter.SimpleListAdapter;
import com.kotcrab.vis.ui.widget.ListView;
import com.mbrlabs.mundus.assets.AssetManager;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.AssetImportEvent;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.modules.dialogs.BaseDialog;
import com.mbrlabs.mundus.utils.Log;

/**
 * @author Marcus Brummer
 * @version 02-10-2016
 */
public class AssetSelectionDialog extends BaseDialog implements
        AssetImportEvent.AssetImportListener,
        ProjectChangedEvent.ProjectChangedListener {

    private static final String TAG = AssetSelectionDialog.class.getSimpleName();
    private static final String TITLE = "Select an asset";

    @Inject
    private ProjectManager projectManager;

    private ListView<Asset> list;
    private SimpleListAdapter<Asset> listAdapter;

    private AssetFilter filter;
    private AssetSelectionListener listener;

    public AssetSelectionDialog() {
        super(TITLE);
        Mundus.inject(this);
        Mundus.registerEventListener(this);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        listAdapter = new SimpleListAdapter<>(new Array<Asset>());
        list = new ListView<>(listAdapter);
        add(list.getMainTable()).grow().size(300, 400);
    }

    private void setupListeners() {
        list.setItemClickListener(new ListView.ItemClickListener<Asset>() {
            @Override
            public void clicked (Asset item) {
                if(listener != null) {
                    Array<Asset> assets = new Array<>();
                    assets.add(item);
                    listener.onSelected(assets);
                    close();
                }
            }
        });
    }

    @Override
    public void onProjectChanged(ProjectChangedEvent projectChangedEvent) {
        reloadData();
    }

    @Override
    public void onAssetImported(AssetImportEvent event) {
        reloadData();
    }

    private void reloadData() {
        AssetManager assetManager = projectManager.current().assetManager;
        listAdapter.clear();

        // filter assets
        for(Asset asset : assetManager.getAssets()) {
            if(filter != null) {
                if(filter.ignore(asset)) {
                    continue;
                }
            }
            listAdapter.add(asset);
        }

        listAdapter.itemsDataChanged();
    }

    public void show(AssetFilter filter, AssetSelectionListener listener) {
        this.listener = listener;
        this.filter = filter;
        reloadData();
        Ui.getInstance().showDialog(this);
    }

    /**
     *
     */
    public interface AssetSelectionListener {
        void onSelected(Array<Asset> assets);
    }

    /**
     *
     */
    public interface AssetFilter {
        boolean ignore(Asset asset);
    }

}
