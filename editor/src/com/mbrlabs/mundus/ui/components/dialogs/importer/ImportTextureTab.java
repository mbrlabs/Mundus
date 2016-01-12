/*
 * Copyright (c) 2016. See AUTHORS file.
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

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.core.HomeManager;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.EventBus;
import com.mbrlabs.mundus.ui.widgets.ImageChooserField;

/**
 * @author Marcus Brummer
 * @version 11-01-2016
 */
public class ImportTextureTab extends Tab {

    private ImportTextureTable importTextureTable;

    @Inject
    private HomeManager homeManager;
    @Inject
    private ProjectContext projectContext;
    @Inject
    private ProjectManager projectManager;
    @Inject
    private EventBus eventBus;

    public ImportTextureTab() {
        super(false, false);
        Mundus.inject(this);
        importTextureTable = new ImportTextureTable();

    }

    @Override
    public String getTabTitle() {
        return "Texture";
    }

    @Override
    public Table getContentTable() {
        return importTextureTable;
    }

    /**
     *
     */
    private class ImportTextureTable extends VisTable implements Disposable {
        // UI elements
        private VisTextField name = new VisTextField();
        private VisTextButton importBtn = new VisTextButton("IMPORT");
        private ImageChooserField imageChooserField = new ImageChooserField(200);


        public ImportTextureTable() {
            super();
            this.setupUI();
            this.setupListener();

            align(Align.topLeft);
        }

        private void setupUI() {
            padTop(6).padRight(6).padBottom(22);

            VisTable left = new VisTable();
            VisTable right = new VisTable();

            left.add(new VisLabel("Name: ")).left().row();
            left.add(name).expandX().fillX().row();
            left.add(importBtn).fillX().expand().bottom();

            right.add(imageChooserField);

            add(left).width(300).top().left().expandY().fillY();
            add(right).width(300);

        }

        private void setupListener() {



        }


        @Override
        public void dispose() {

        }
    }
}
