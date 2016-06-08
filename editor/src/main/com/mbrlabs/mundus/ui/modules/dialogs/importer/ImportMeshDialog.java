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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.UBJsonReader;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.commons.model.MModel;
import com.mbrlabs.mundus.core.AssetManager;
import com.mbrlabs.mundus.core.ImportManager;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.registry.Registry;
import com.mbrlabs.mundus.events.ModelImportEvent;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.modules.dialogs.BaseDialog;
import com.mbrlabs.mundus.ui.widgets.FileChooserField;
import com.mbrlabs.mundus.ui.widgets.RenderWidget;
import com.mbrlabs.mundus.utils.FileFormatUtils;

/**
 * @author Marcus Brummer
 * @version 07-06-2016
 */
public class ImportMeshDialog extends BaseDialog implements Disposable {

    private ImportModelTable importMeshTable;

    @Inject
    private Registry registry;
    @Inject
    private ImportManager importManager;
    @Inject
    private AssetManager assetManager;

    public ImportMeshDialog() {
        super("Import Mesh");
        Mundus.inject(this);
        setModal(true);
        setMovable(true);

        Table root = new VisTable();
        add(root).expand().fill();
        importMeshTable = new ImportModelTable();

        root.add(importMeshTable).minWidth(600).expand().fill().left().top();
    }

    @Override
    public void dispose() {
        importMeshTable.dispose();
    }

    /**
     *
     */
    private class ImportModelTable extends VisTable implements Disposable {
        // UI elements
        private RenderWidget renderWidget;
        private VisTextField name = new VisTextField();
        private VisTextButton importBtn = new VisTextButton("IMPORT");
        private FileChooserField modelInput = new FileChooserField(300);
        private FileChooserField textureInput = new FileChooserField(300);

        // preview model + instance
        private Model previewModel;
        private ModelInstance previewInstance;

        private ImportManager.ImportedModel importedModel;

        private ModelBatch modelBatch;
        private PerspectiveCamera cam;
        private Environment env;

        public ImportModelTable() {
            super();
            modelBatch = new ModelBatch();

            cam = new PerspectiveCamera();
            cam.position.set(0, 5f, 0);
            cam.lookAt(0,0,0);
            cam.near = 0.1f;
            cam.far = 100f;
            cam.update();

            env = new Environment();
            env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
            env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

            this.setupUI();
            this.setupListener();
        }

        private void setupUI() {
            Table root = new Table();
            //root.debugAll();
            root.padTop(6).padRight(6).padBottom(22);
            add(root);


            VisTable inputTable = new VisTable();
            renderWidget = new RenderWidget(cam);
            renderWidget.setRenderer(new RenderWidget.Renderer() {
                @Override
                public void render(Camera cam) {
                    if(previewInstance != null) {
                        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
                        previewInstance.transform.rotate(0, 0, 1, -1f);
                        modelBatch.begin(cam);
                        modelBatch.render(previewInstance, env);
                        modelBatch.end();
                    }
                }
            });

            root.add(inputTable).width(300).height(300).padRight(10);
            root.add(renderWidget).width(300).height(300).expand().fill();

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
                        close();
                        Ui.getInstance().getToaster().success("Mesh imported");
                    } else {
                        Ui.getInstance().getToaster().error("There is nothing to import");
                    }
                }
            });
        }

        private void loadAndShowPreview(FileHandle model, FileHandle texture) {
            this.importedModel = importManager.importToTempFolder(model, texture);

            if(importedModel == null) {
                if(FileFormatUtils.isCollada(model) || FileFormatUtils.isFBX(model) || FileFormatUtils.isWavefont(model)) {
                    Dialogs.showErrorDialog(getStage(), "Import error\nPlease make sure you specified the right " +
                            "files & have set the correct fbc-conv binary in the settings menu.");
                } else {
                    Dialogs.showErrorDialog(getStage(), "Import error\nPlease make sure you specified the right files");
                }
            }

            // load and show preview
            if(importedModel != null) {
                try {
                    previewModel = new G3dModelLoader(new UBJsonReader()).loadModel(importedModel.g3dbFile);
                    previewInstance = new ModelInstance(previewModel);
                    showPreview();
                } catch (GdxRuntimeException e) {
                    Dialogs.showErrorDialog(getStage(), e.getMessage());
                }
            }
        }

        private void showPreview() {
            previewInstance = new ModelInstance(previewModel);

            // scale to 2 open gl units
            BoundingBox boundingBox = previewInstance.calculateBoundingBox(new BoundingBox());
            Vector3 max = boundingBox.getMax(new Vector3());
            float maxDim = 0;
            if(max.x > maxDim) maxDim = max.x;
            if(max.y > maxDim) maxDim = max.y;
            if(max.z > maxDim) maxDim = max.z;
            previewInstance.transform.scl(2f / maxDim);
        }

        @Override
        public void dispose() {
            if(previewModel != null) {
                previewModel.dispose();
                previewModel = null;
                previewInstance = null;
            }
            modelInput.clear();
            textureInput.clear();
        }
    }

}
