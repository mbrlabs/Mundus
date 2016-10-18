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

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.adapter.SimpleListAdapter;
import com.kotcrab.vis.ui.widget.ListView;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.assets.EditorAssetManager;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.AssetImportEvent;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.modules.dialogs.BaseDialog;

/**
 * @author Marcus Brummer
 * @version 02-10-2016
 */
public class AssetSelectionDialog extends BaseDialog
        implements AssetImportEvent.AssetImportListener, ProjectChangedEvent.ProjectChangedListener {

    private static final String TAG = AssetSelectionDialog.class.getSimpleName();
    private static final String TITLE = "Select an asset";

    @Inject
    private ProjectManager projectManager;

    private VisTable root;
    private ListView<Asset> list;
    private SimpleListAdapter<Asset> listAdapter;
    private VisTextButton noneBtn;

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
        root = new VisTable();
        listAdapter = new SimpleListAdapter<>(new Array<Asset>());
        list = new ListView<>(listAdapter);
        root.add(list.getMainTable()).grow().size(300, 400).row();

        noneBtn = new VisTextButton("None / Remove old asset");
        root.add(noneBtn).grow().row();

        add(root).padRight(5).padBottom(5).grow().row();
    }

    private void setupListeners() {
        list.setItemClickListener(item -> {
            if (listener != null) {
                listener.onSelected(item);
                close();
            }
        });

        noneBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (listener != null) {
                    listener.onSelected(null);
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
        EditorAssetManager assetManager = projectManager.current().assetManager;
        listAdapter.clear();

        // filter assets
        for (Asset asset : assetManager.getAssets()) {
            if (filter != null) {
                if (filter.ignore(asset)) {
                    continue;
                }
            }
            listAdapter.add(asset);
            Pixmap.setBlending(Pixmap.Blending.None);
        }

        listAdapter.itemsDataChanged();
    }

    public void show(boolean showNoneAsset, AssetFilter filter, AssetSelectionListener listener) {
        this.listener = listener;
        this.filter = filter;
        if(showNoneAsset) {
            noneBtn.setDisabled(false);
            noneBtn.setTouchable(Touchable.enabled);
        } else {
            noneBtn.setDisabled(true);
            noneBtn.setTouchable(Touchable.disabled);
        }
        reloadData();
        Ui.getInstance().showDialog(this);
    }

    /**
     *
     */
    public interface AssetSelectionListener {
        void onSelected(Asset asset);
    }

    /**
     *
     */
    public interface AssetFilter {
        boolean ignore(Asset asset);
    }

}
