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

package com.mbrlabs.mundus.scene3d.components;

import com.badlogic.gdx.graphics.g3d.Shader;
import com.mbrlabs.mundus.commons.assets.TerrainAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.tools.picker.PickerColorEncoder;
import com.mbrlabs.mundus.tools.picker.PickerIDAttribute;
import com.mbrlabs.mundus.utils.Log;

/**
 * @author Marcus Brummer
 * @version 18-01-2016
 */
public class TerrainComponent extends PickableComponent {

    private static final String TAG = TerrainComponent.class.getSimpleName();

    private TerrainAsset terrain;
    private Shader shader;

    public TerrainComponent(GameObject go) {
        super(go);
        type = Type.TERRAIN;
    }

    @Override
    public void encodeRaypickColorId() {
        PickerIDAttribute goIDa = PickerColorEncoder.encodeRaypickColorId(gameObject);
        terrain.getTerrain().modelInstance.materials.first().set(goIDa);
    }

    @Override
    public void renderPick() {
        gameObject.sceneGraph.batch.render(terrain.getTerrain(), Shaders.pickerShader);
    }

    public void setTerrain(TerrainAsset terrain) {
        this.terrain = terrain;
    }

    public TerrainAsset getTerrain() {
        return terrain;
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    @Override
    public void render(float delta) {
        gameObject.sceneGraph.batch.render(terrain.getTerrain(), gameObject.sceneGraph.scene.environment, shader);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public Component clone(GameObject go) {
        Log.fatal(TAG, "Terrain can not be cloned!");
        return null;
    }

}
