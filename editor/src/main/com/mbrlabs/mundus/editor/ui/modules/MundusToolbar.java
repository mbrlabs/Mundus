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

package com.mbrlabs.mundus.editor.ui.modules;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.editor.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.core.Inject;
import com.mbrlabs.mundus.editor.core.Mundus;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.AssetImportEvent;
import com.mbrlabs.mundus.editor.tools.ToolManager;
import com.mbrlabs.mundus.editor.ui.Ui;
import com.mbrlabs.mundus.editor.ui.widgets.FaTextButton;
import com.mbrlabs.mundus.editor.ui.widgets.ToggleButton;
import com.mbrlabs.mundus.editor.ui.widgets.Toolbar;
import com.mbrlabs.mundus.editor.utils.Fa;
import com.mbrlabs.mundus.editor.utils.Log;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class MundusToolbar extends Toolbar {

    private static final String TAG = MundusToolbar.class.getSimpleName();

    private FaTextButton saveBtn;
    private FaTextButton importBtn;
    private FaTextButton exportBtn;

    private FaTextButton selectBtn;
    private FaTextButton translateBtn;
    private FaTextButton rotateBtn;
    private FaTextButton scaleBtn;
    private ToggleButton globalLocalSwitch;

    private PopupMenu importMenu;
    private MenuItem importMesh;
    private MenuItem importTexture;
    private MenuItem createMaterial;

    @Inject
    private ToolManager toolManager;
    @Inject
    private ProjectManager projectManager;

    public MundusToolbar() {
        super();
        Mundus.inject(this);

        importMesh = new MenuItem("Import 3D model");
        importTexture = new MenuItem("Import texture");
        createMaterial = new MenuItem("Create material");
        importMenu = new PopupMenu();
        importMenu.addItem(importMesh);
        importMenu.addItem(importTexture);
        importMenu.addItem(createMaterial);

        saveBtn = new FaTextButton(Fa.Companion.getSAVE());
        saveBtn.padRight(7).padLeft(7);
        new Tooltip.Builder("Save project").target(saveBtn).build();

        importBtn = new FaTextButton(Fa.Companion.getDOWNLOAD());
        importBtn.padRight(7).padLeft(7);
        new Tooltip.Builder("Import model").target(importBtn).build();

        exportBtn = new FaTextButton(Fa.Companion.getGIFT());
        exportBtn.padRight(12).padLeft(7);
        new Tooltip.Builder("Export project").target(exportBtn).build();

        selectBtn = new FaTextButton(toolManager.selectionTool.getIconFont());
        selectBtn.padRight(7).padLeft(12);
        new Tooltip.Builder(toolManager.selectionTool.getName()).target(selectBtn).build();

        translateBtn = new FaTextButton(toolManager.translateTool.getIconFont());
        translateBtn.padRight(7).padLeft(7);
        new Tooltip.Builder(toolManager.translateTool.getName()).target(translateBtn).build();

        rotateBtn = new FaTextButton(toolManager.rotateTool.getIconFont());
        rotateBtn.padRight(7).padLeft(7);
        new Tooltip.Builder(toolManager.rotateTool.getName()).target(rotateBtn).build();

        scaleBtn = new FaTextButton(toolManager.scaleTool.getIconFont());
        scaleBtn.padRight(7).padLeft(7);
        new Tooltip.Builder(toolManager.scaleTool.getName()).target(scaleBtn).build();

        globalLocalSwitch = new ToggleButton("Global space", "Local space");

        addItem(saveBtn, true);
        addItem(importBtn, true);
        addItem(exportBtn, true);
        addSeperator(true);
        addItem(selectBtn, true);
        addItem(translateBtn, true);
        addItem(rotateBtn, true);
        addItem(scaleBtn, true);
        addSeperator(true);
        // addItem(globalLocalSwitch, true);

        setActive(translateBtn);

        // save btn
        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                projectManager.saveCurrentProject();
                Ui.getInstance().getToaster().success("Project saved");
            }
        });

        // export btn
        exportBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final Ui ui = Ui.getInstance();
                ui.showDialog(ui.getExportDialog());
            }
        });

        // import btn
        importBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                importMenu.showMenu(Ui.getInstance(), importBtn);
            }
        });

        // import mesh
        importMesh.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Ui.getInstance().showDialog(Ui.getInstance().getImportModelDialog());
            }
        });

        // import texture
        importTexture.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Ui.getInstance().showDialog(Ui.getInstance().getImportTextureDialog());
            }
        });

        // create material
        createMaterial.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialogs.showInputDialog(Ui.getInstance(), "Create new material", "Material name",
                        new InputDialogAdapter() {
                            @Override
                            public void finished(String input) {
                                EditorAssetManager assetManager = projectManager.current().assetManager;
                                try {
                                    MaterialAsset mat = assetManager.createMaterialAsset(input);
                                    Mundus.postEvent(new AssetImportEvent(mat));
                                } catch (Exception e) {
                                    Log.exception(TAG, e);
                                    Ui.getInstance().getToaster().error(e.toString());
                                }
                            }

                            @Override
                            public void canceled() {
                                super.canceled();
                            }
                        });
            }
        });

        // select tool
        selectBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toolManager.activateTool(toolManager.selectionTool);
                setActive(selectBtn);
            }
        });

        // translate tool
        translateBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toolManager.activateTool(toolManager.translateTool);
                setActive(translateBtn);
            }
        });

        // rotate tool
        rotateBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toolManager.activateTool(toolManager.rotateTool);
                setActive(rotateBtn);
            }
        });

        // scale tool
        scaleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toolManager.activateTool(toolManager.scaleTool);
                setActive(scaleBtn);
            }
        });

        // global / local space switching
        globalLocalSwitch.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toolManager.translateTool.setGlobalSpace(globalLocalSwitch.isOn());
            }
        });

    }

    private void setActive(FaTextButton btn) {
        selectBtn.setStyle(FaTextButton.styleNoBg);
        translateBtn.setStyle(FaTextButton.styleNoBg);
        rotateBtn.setStyle(FaTextButton.styleNoBg);
        scaleBtn.setStyle(FaTextButton.styleNoBg);
        btn.setStyle(FaTextButton.styleActive);
    }

    public FaTextButton getSaveBtn() {
        return saveBtn;
    }

    public FaTextButton getImportBtn() {
        return importBtn;
    }

}
