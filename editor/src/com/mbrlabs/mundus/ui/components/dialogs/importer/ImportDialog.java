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

package com.mbrlabs.mundus.ui.components.dialogs.importer;

import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.HomeManager;
import com.mbrlabs.mundus.ui.components.dialogs.BaseDialog;

/**
 * @author Marcus Brummer
 * @version 29-11-2015
 */
public class ImportDialog extends BaseDialog implements Disposable {

    private static final String TAG = ImportDialog.class.getSimpleName();

    private VisTable root;

    private TabbedPaneAdapter tabbedPaneAdapter;
    private TabbedPane tabbedPane;
    private VisTable contentContainer;

    private ImportModelTab modelImportTab;
    private ImportTextureTab importTextureTab;

    @Inject
    private HomeManager homeManager;

    public ImportDialog() {
        super("Import Assets");
        Mundus.inject(this);
        setModal(true);
        setMovable(true);

        setupUI();
        setupListener();

    }

    private void setupUI() {
        root = new VisTable();
        add(root).expand().fill();

        modelImportTab = new ImportModelTab();
        importTextureTab = new ImportTextureTab();

        tabbedPane = new TabbedPane();
        tabbedPane.add(modelImportTab);
        tabbedPane.add(importTextureTab);

        contentContainer = new VisTable();

        root.add(tabbedPane.getTable()).expandX().fillX().left().top().row();
        root.add(contentContainer).minWidth(600).expand().fill().left().top();
    }

    private void setupListener() {
        tabbedPaneAdapter= new TabbedPaneAdapter() {
            @Override
            public void switchedTab(Tab tab) {
                contentContainer.clearChildren();
                contentContainer.add(tab.getContentTable()).expand().fill().top().left();
                pack();
            }
        };

        tabbedPane.addListener(tabbedPaneAdapter);

        tabbedPane.switchTab(modelImportTab);
    }


    @Override
    protected void close() {
        dispose();
        super.close();
    }

    @Override
    public void dispose() {
        homeManager.purgeModelCache();
    }

}
