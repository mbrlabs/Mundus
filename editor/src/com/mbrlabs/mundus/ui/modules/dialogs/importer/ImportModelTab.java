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

package com.mbrlabs.mundus.ui.modules.dialogs.importer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.UBJsonReader;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.commons.model.MModel;
import com.mbrlabs.mundus.core.*;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.events.ModelImportEvent;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.widgets.FileChooserField;
import com.mbrlabs.mundus.utils.FileFormatUtils;

/**
 * @author Marcus Brummer
 * @version 11-01-2016
 */
public class ImportModelTab extends Tab {

    private ImportModelTable importModelTable;

    private ImportDialog dialog;

    @Inject
    private HomeManager homeManager;
    @Inject
    private ProjectContext projectContext;
    @Inject
    private ImportManager importManager;
    @Inject
    private AssetManager assetManager;

    public ImportModelTab(ImportDialog dialog) {
        super(false, false);
        Mundus.inject(this);
        this.dialog = dialog;
        importModelTable = new ImportModelTable();
    }

    @Override
    public String getTabTitle() {
        return "3D Model";
    }

    @Override
    public Table getContentTable() {
        return importModelTable;
    }

    @Override
    public void dispose() {
        super.dispose();
        importModelTable.dispose();
    }

    /**
     *
     */
    private class ImportModelTable extends VisTable implements Disposable {
        // UI elements
        private Container fake3dViewport;
        private VisTextField name = new VisTextField();
        private VisTextButton importBtn = new VisTextButton("IMPORT");
        private FileChooserField modelInput = new FileChooserField(300);
        private FileChooserField textureInput = new FileChooserField(300);

        // preview model + instance
        private Model previewModel;
        private ModelInstance previewInstance;

        private ImportManager.ImportedModel importedModel;

        public ImportModelTable() {
            super();
            this.setupUI();
            this.setupListener();
        }

        private void setupUI() {
            Table root = new Table();
            //root.debugAll();
            root.padTop(6).padRight(6).padBottom(22);
            add(root);

            VisTable inputTable = new VisTable();
            fake3dViewport = new Container();
            fake3dViewport.setBackground(VisUI.getSkin().getDrawable("default-pane"));
            fake3dViewport.setActor(new VisLabel("PREVIEW"));

            root.add(inputTable).width(300).height(300).padRight(10);
            root.add(fake3dViewport).width(300).height(300);

            inputTable.left().top();
            inputTable.add(new VisLabel("Model File")).left().padBottom(5).row();
            inputTable.add(modelInput).fillX().expandX().padBottom(10).row();
            inputTable.add(new VisLabel("Texture File")).left().padBottom(5).row();
            inputTable.add(textureInput).fillX().expandX().row();
            inputTable.add(new VisLabel("Name")).left().padBottom(5).row();
            inputTable.add(name).fillX().expandX().padBottom(10).row();
            inputTable.add(importBtn).fillX().expand().bottom();

            modelInput.setEditable(false);
            textureInput.setEditable(false);
        }

        private void setupListener() {

            // texture chooser
            textureInput.setCallback(new FileChooserField.FileSelected() {
                @Override
                public void selected(FileHandle fileHandle) {
                    if(fileHandle.exists()) {
                        if(modelInput.getFile() != null && modelInput.getFile().exists()) {
                            loadAndShowPreview(modelInput.getFile(), textureInput.getFile());
                        }
                    }
                }
            });

            // model chooser
            modelInput.setCallback(new FileChooserField.FileSelected() {
                @Override
                public void selected(FileHandle fileHandle) {
                    if(fileHandle.exists()) {
                        if(textureInput.getFile() != null && textureInput.getFile().exists()) {
                            loadAndShowPreview(modelInput.getFile(), textureInput.getFile());
                        }
                    }
                }
            });

            // import btn
            importBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(previewModel != null && previewInstance != null && name.getText().length() > 0) {
                        // create model
                        importedModel.name = name.getText();
                        MModel mModel = assetManager.importG3dbModel(importedModel);
                        Mundus.postEvent(new ModelImportEvent(mModel));
                        dispose();
                        dialog.close();
                    }
                }
            });
        }

        private void loadAndShowPreview(FileHandle model, FileHandle texture) {
            this.importedModel = importManager.importToTempFolder(model, texture);

            if(importedModel == null) {
                if(FileFormatUtils.isCollada(model) || FileFormatUtils.isFBX(model) || FileFormatUtils.isWavefont(model)) {
                    DialogUtils.showErrorDialog(getStage(), "Import error\nPlease make sure you specified the right " +
                            "files & have set the correct fbc-conv binary in the settings menu.");
                } else {
                    DialogUtils.showErrorDialog(getStage(), "Import error\nPlease make sure you specified the right files");
                }
            }

            // load and show preview
            if(importedModel != null) {
                try {
                    previewModel = new G3dModelLoader(new UBJsonReader()).loadModel(importedModel.g3dbFile);
                    previewInstance = new ModelInstance(previewModel);
                    showPreview();
                } catch (GdxRuntimeException e) {
                    DialogUtils.showErrorDialog(getStage(), e.getMessage());
                }
            }
        }

        private void showPreview() {
            if(fake3dViewport.getActor() != null) {
                fake3dViewport.removeActor(fake3dViewport.getActor());
            }

            previewInstance = new ModelInstance(previewModel);
            Ui.getInstance().wireModelToActor(fake3dViewport, previewInstance);
        }

        @Override
        public void dispose() {
            System.out.println("wgfsdfgfsfsdfsdfsdfsdfsdfs");
            Ui.getInstance().unwire(fake3dViewport);
            if(previewModel != null) {
                previewModel.dispose();
                previewModel = null;
            }
            modelInput.clear();
            textureInput.clear();
        }
    }
}
