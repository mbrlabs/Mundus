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

package com.mbrlabs.mundus.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.assets.EditorAssetManager;
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.ui.modules.dialogs.assets.AssetMaterialFilter;
import com.mbrlabs.mundus.ui.modules.dialogs.assets.AssetSelectionDialog;
import com.mbrlabs.mundus.ui.modules.dialogs.assets.AssetTextureFilter;

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
public class MaterialWidget extends VisTable {

    private static final String TAG = MaterialWidget.class.getSimpleName();

    private AssetSelectionDialog assetSelectionDialog;
    private AssetMaterialFilter materialFilter;
    private VisTextButton materialChangeBtn;
    private AssetSelectionDialog.AssetSelectionListener assetSelectionListener;

    private VisLabel label;
    private ColorPickerField diffuseColorField;
    private AssetSelectionField diffuseAssetField;
    private VisTextField opacity;
    private VisTextField shininess;

    private MaterialAsset material;

    private MaterialChangedListener changedListener;

    @Inject
    private ProjectManager projectManager;

    public MaterialWidget(MaterialChangedListener listener) {
        super();
        Mundus.inject(this);
        align(Align.topLeft);
        label = new VisLabel();
        this.changedListener = listener;
        if (listener != null) {
            assetSelectionDialog = new AssetSelectionDialog();
            materialFilter = new AssetMaterialFilter();
            materialChangeBtn = new VisTextButton("change");
            assetSelectionListener = asset -> {
                setMaterial((MaterialAsset) asset);
                listener.materialChanged((MaterialAsset) asset);
            };
        }
        diffuseAssetField = new AssetSelectionField();
        diffuseColorField = new ColorPickerField();
        opacity = new VisTextField();
        shininess = new VisTextField();

        shininess.setTextFieldFilter(new FloatDigitsOnlyFilter(false));
        opacity.setTextFieldFilter(new FloatDigitsOnlyFilter(false));

        label.setWrap(true);

        if (listener != null) {
            VisTable table = new VisTable();
            table.add(label).grow();
            table.add(materialChangeBtn).right().row();
            add(table).grow().row();
        } else {
            add(label).grow().row();
        }
        addSeparator().growX().row();
        add(new VisLabel("Diffuse texture")).grow().row();
        add(diffuseAssetField).growX().row();
        add(new VisLabel("Diffuse color")).grow().row();
        add(diffuseColorField).growX().row();
        //        add(new VisLabel("Shininess")).growX().row();
        //        add(shininess).growX().row();
        //        add(new VisLabel("Opacity")).growX().row();
        //        add(opacity).growX().row();

        setupWidgets();
    }

    private void setupWidgets() {
        if (materialChangeBtn != null) {
            materialChangeBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    assetSelectionDialog.show(false, materialFilter, assetSelectionListener);
                }
            });
        }

        // diffuse texture
        diffuseAssetField.setFilter(new AssetTextureFilter());
        diffuseAssetField.setListener(asset -> {
            material.setDiffuseTexture((TextureAsset) asset);
            applyMaterialToModelAssets();
            applyMaterialToModelComponents();
            projectManager.current().assetManager.addDirtyAsset(material);
        });

        // diffuse color
        diffuseColorField.setCallback(color -> {
            material.setDiffuseColor(color);
            applyMaterialToModelAssets();
            applyMaterialToModelComponents();
            projectManager.current().assetManager.addDirtyAsset(material);
        });

        //        opacity.addListener(new ChangeListener() {
        //            @Override
        //            public void changed(ChangeEvent event, Actor actor) {
        //                try {
        //                    material.setOpacity(Float.valueOf(opacity.getText()));
        //                } catch (NumberFormatException e) {
        //                    e.printStackTrace();
        //                }
        //            }
        //        });
        //
        //        shininess.addListener(new ChangeListener() {
        //            @Override
        //            public void changed(ChangeEvent event, Actor actor) {
        //                try {
        //                    material.setShininess(Float.valueOf(shininess.getText()));
        //                } catch (NumberFormatException e) {
        //                    e.printStackTrace();
        //                }
        //            }
        //        });
    }

    // TODO find better solution than iterating through all components
    private void applyMaterialToModelComponents() {
        SceneGraph sceneGraph = projectManager.current().currScene.sceneGraph;
        for (GameObject go : sceneGraph.getGameObjects()) {
            ModelComponent mc = (ModelComponent) go.findComponentByType(Component.Type.MODEL);
            if (mc != null) {
                mc.applyMaterials();
            }
        }
    }

    // TODO find better solution than iterating through all assets
    private void applyMaterialToModelAssets() {
        EditorAssetManager assetManager = projectManager.current().assetManager;
        for (ModelAsset modelAsset : assetManager.getModelAssets()) {
            modelAsset.applyDependencies();
        }
    }

    public void setMaterial(MaterialAsset material) {
        this.material = material;
        diffuseColorField.setColor(material.getDiffuseColor());
        diffuseAssetField.setAsset(material.getDiffuseTexture());
        opacity.setText(String.valueOf(material.getOpacity()));
        shininess.setText(String.valueOf(material.getShininess()));
        label.setText(material.getName());
    }

    public MaterialAsset getMaterial() {
        return material;
    }

    /**
     *
     */
    public interface MaterialChangedListener {
        public void materialChanged(MaterialAsset materialAsset);
    }

}
