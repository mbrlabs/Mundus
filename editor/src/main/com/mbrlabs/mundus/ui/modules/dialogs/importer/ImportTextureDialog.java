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

package com.mbrlabs.mundus.ui.modules.dialogs.importer;

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
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.assets.EditorAssetManager;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.core.registry.Registry;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.modules.dialogs.BaseDialog;
import com.mbrlabs.mundus.ui.widgets.ImageChooserField;
import com.mbrlabs.mundus.utils.FileFormatUtils;

/**
 * @author Marcus Brummer
 * @version 07-06-2016
 */
public class ImportTextureDialog extends BaseDialog implements Disposable {

    private ImportTextureTable importTextureTable;

    @Inject
    private Registry registry;
    @Inject
    private ProjectManager projectManager;

    public ImportTextureDialog() {
        super("Import Texture");
        Mundus.inject(this);
        setModal(true);
        setMovable(true);

        Table root = new VisTable();
        add(root).expand().fill();
        importTextureTable = new ImportTextureTable();

        root.add(importTextureTable).minWidth(600).expand().fill().left().top();
    }

    @Override
    public void dispose() {
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
                    if(texture != null && texture.exists() && FileFormatUtils.isImage(texture)) {
                        EditorAssetManager assetManager = projectManager.current().assetManager;
                        assetManager.importAsset(texture, TextureAsset.class);
                        close();
                        Ui.getInstance().getToaster().success("Texture imported");
                    } else {
                        Ui.getInstance().getToaster().error("There is nothing to import");
                    }
                }
            });
        }


        @Override
        public void dispose() {

        }
    }

}
