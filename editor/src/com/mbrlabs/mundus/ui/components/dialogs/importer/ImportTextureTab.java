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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
import com.mbrlabs.mundus.model.MTexture;
import com.mbrlabs.mundus.ui.widgets.ImageChooserField;
import com.mbrlabs.mundus.utils.FileFormatUtils;

/**
 * @author Marcus Brummer
 * @version 11-01-2016
 */
public class ImportTextureTab extends Tab {

    private ImportTextureTable importTextureTable;
    private ImportDialog dialog;

    @Inject
    private HomeManager homeManager;
    @Inject
    private ProjectContext projectContext;
    @Inject
    private ProjectManager projectManager;

    public ImportTextureTab(ImportDialog dialog) {
        super(false, false);
        Mundus.inject(this);
        this.dialog = dialog;
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

    @Override
    public void dispose() {
        super.dispose();
        importTextureTable.dispose();
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
            importBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    FileHandle texture = imageChooserField.getFile();
                    String texName = name.getText();
                    if(texName != null && texName.length() > 0 && texture.exists() && FileFormatUtils.isImage(texture)) {
                        MTexture tex = projectManager.importTexture(texName, texture);
                        dialog.close();
                    } else {
                        // TODO show error msg
                    }
                }
            });
        }


        @Override
        public void dispose() {

        }
    }
}
