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

package com.mbrlabs.mundus.editor.scene3d.components;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.editor.shader.Shaders;
import com.mbrlabs.mundus.editor.tools.picker.PickerColorEncoder;
import com.mbrlabs.mundus.editor.tools.picker.PickerIDAttribute;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Marcus Brummer
 * @version 17-01-2016
 */
public class ModelComponent extends PickableComponent {

    private ModelAsset modelAsset;
    private ModelInstance modelInstance;
    private Shader shader;

    private Map<String, MaterialAsset> materials;  // g3db material id to material asset uuid

    public ModelComponent(GameObject go) {
        super(go);
        type = Type.MODEL;
        materials = new HashMap<String, MaterialAsset>();
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public void setModel(ModelAsset model, boolean inheritMaterials) {
        this.modelAsset = model;
        modelInstance = new ModelInstance(model.getModel());
        modelInstance.transform = gameObject.getTransform();

        // apply default materials of model
        if (inheritMaterials) {
            for (String g3dbMatID : model.getDefaultMaterials().keySet()) {
                materials.put(g3dbMatID, model.getDefaultMaterials().get(g3dbMatID));
            }
        }
        applyMaterials();
    }

    public Map<String, MaterialAsset> getMaterials() {
        return materials;
    }

    public ModelAsset getModelAsset() {
        return modelAsset;
    }

    public void applyMaterials() {
        for (Material mat : modelInstance.materials) {
            MaterialAsset materialAsset = materials.get(mat.id);
            if (materialAsset == null) continue;

            materialAsset.applyToMaterial(mat);
        }
    }

    @Override
    public void encodeRaypickColorId() {
        PickerIDAttribute goIDa = PickerColorEncoder.encodeRaypickColorId(gameObject);
        this.modelInstance.materials.first().set(goIDa);
    }

    @Override
    public void renderPick() {
        gameObject.sceneGraph.batch.render(modelInstance, Shaders.pickerShader);
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    @Override
    public void render(float delta) {
        modelInstance.transform.set(gameObject.getTransform());
        gameObject.sceneGraph.batch.render(modelInstance, gameObject.sceneGraph.scene.environment, shader);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public Component clone(GameObject go) {
        ModelComponent mc = new ModelComponent(go);
        mc.modelAsset = this.modelAsset;
        mc.modelInstance = new ModelInstance(modelAsset.getModel());
        mc.shader = this.shader;
        mc.encodeRaypickColorId();
        return mc;
    }

}
