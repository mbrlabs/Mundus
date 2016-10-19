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

package com.mbrlabs.mundus.editor.ui.modules.inspector.assets;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.editor.assets.EditorAssetManager;
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.editor.core.Inject;
import com.mbrlabs.mundus.editor.core.Mundus;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.tools.ToolManager;
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget;
import com.mbrlabs.mundus.editor.ui.widgets.MaterialWidget;

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
public class ModelAssetInspectorWidget extends BaseInspectorWidget {

    private static final String TITLE = "Model Asset";

    private ModelAsset modelAsset;

    // info
    private VisLabel name;
    private VisLabel nodeCount;
    private VisLabel materialCount;
    private VisLabel vertexCount;
    private VisLabel indexCount;

    // materials
    private VisTable materialContainer;

    // actions
    private VisTextButton modelPlacement;

    @Inject
    private ToolManager toolManager;
    @Inject
    private ProjectManager projectManager;

    public ModelAssetInspectorWidget() {
        super(TITLE);
        Mundus.inject(this);
        setDeletable(false);

        materialContainer = new VisTable();

        name = new VisLabel();
        nodeCount = new VisLabel();
        materialCount = new VisLabel();
        vertexCount = new VisLabel();
        indexCount = new VisLabel();
        modelPlacement = new VisTextButton("Activate model placement tool");

        // info
        collapsibleContent.add(new VisLabel("Info")).growX().row();
        collapsibleContent.addSeparator().padBottom(5).row();
        collapsibleContent.add(name).growX().row();
        collapsibleContent.add(nodeCount).growX().row();
        collapsibleContent.add(materialCount).growX().row();
        collapsibleContent.add(vertexCount).growX().row();
        collapsibleContent.add(indexCount).growX().padBottom(15).row();

        // actions
        collapsibleContent.add(new VisLabel("Actions")).growX().row();
        collapsibleContent.addSeparator().padBottom(5).row();
        collapsibleContent.add(modelPlacement).growX().padBottom(15).row();

        // materials
        VisLabel label = new VisLabel();
        label.setText("Default model materials determine the initial materials a new model will get, if "
                + "you use the model placement tool.");
        label.setWrap(true);
        collapsibleContent.add(new VisLabel("Default model materials")).growX().row();
        collapsibleContent.addSeparator().padBottom(5).row();
        collapsibleContent.add(label).padTop(4).padBottom(15).grow().row();
        collapsibleContent.add(materialContainer).growX().padBottom(15).row();

        // model placement action
        modelPlacement.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toolManager.modelPlacementTool.setModel(modelAsset);
                toolManager.activateTool(toolManager.modelPlacementTool);
            }
        });
    }

    private void updateUI() {
        int verts = 0;
        int indices = 0;
        Model model = modelAsset.getModel();
        for (Mesh mesh : model.meshes) {
            verts += mesh.getNumVertices();
            indices += mesh.getNumIndices();
        }
        // set info
        name.setText("Name: " + modelAsset.getName());
        nodeCount.setText("Nodes: " + model.nodes.size);
        materialCount.setText("Materials: " + model.materials.size);
        vertexCount.setText("Vertices: " + verts);
        indexCount.setText("Indices: " + indices);

        materialContainer.clear();
        for (String g3dbMatID : modelAsset.getDefaultMaterials().keySet()) {
            MaterialAsset mat = modelAsset.getDefaultMaterials().get(g3dbMatID);
            MaterialWidget mw = new MaterialWidget(materialAsset -> {
                EditorAssetManager assetManager = projectManager.current().assetManager;
                modelAsset.getDefaultMaterials().put(g3dbMatID, materialAsset);
                modelAsset.applyDependencies();
                toolManager.modelPlacementTool.setModel(modelAsset);
                assetManager.addDirtyAsset(modelAsset);
            });
            mw.setMaterial(mat);
            materialContainer.add(mw).grow().padBottom(20).row();
        }
    }

    public void setModel(ModelAsset model) {
        this.modelAsset = model;
        updateUI();
    }

    @Override
    public void onDelete() {
        // can't be deleted
    }

    @Override
    public void setValues(GameObject go) {
        // nope
    }

}