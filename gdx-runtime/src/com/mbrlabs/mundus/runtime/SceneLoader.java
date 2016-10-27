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

package com.mbrlabs.mundus.runtime;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.importer.JsonScene;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;

/**
 * @author Marcus Brummer
 * @version 27-10-2016
 */
public class SceneLoader {

    private Mundus mundus;
    private AssetManager assetManager;

    private FileHandle root;

    public SceneLoader(Mundus mundus, FileHandle scenesRoot) {
        this.mundus = mundus;
        this.assetManager = mundus.getAssetManager();
        this.root = scenesRoot;
    }

    public Scene load(String name) {
        final JsonReader reader = new JsonReader();
        final JsonValue json = reader.parse(root.child(name));

        Scene scene = new Scene();
        scene.setId(json.getInt(JsonScene.ID));
        scene.setName(json.getString(JsonScene.NAME));

        // game objects
        for(JsonValue go : json.get(JsonScene.GAME_OBJECTS)) {
            scene.sceneGraph.addGameObject(convertGameObject(scene.sceneGraph, go));
        }

        return scene;
    }

    private GameObject convertGameObject(SceneGraph sceneGraph, JsonValue jsonGo) {
        final GameObject go = new GameObject(sceneGraph, jsonGo.getString(JsonScene.GO_NAME, ""),
                jsonGo.getInt(JsonScene.GO_ID));
        go.active = jsonGo.getBoolean(JsonScene.GO_ACTIVE, true);
        // TODO tags

        // model component
        JsonValue modelComp = jsonGo.get(JsonScene.GO_MODEL_COMPONENT);
        if(modelComp != null) {
            ModelComponent mc = new ModelComponent(go, mundus.getShaders().getModelShader());
            mc.setModel((ModelAsset) assetManager.findAssetByID(modelComp.getString(JsonScene.MODEL_COMPONENT_MODEL_ID)), false);

            JsonValue mats = modelComp.get(JsonScene.MODEL_COMPONENT_MATERIALS);
            for(JsonValue mat : mats.iterator()) {
                mc.getMaterials().put(mat.name, (MaterialAsset) assetManager.findAssetByID(mats.getString(mat.name)));
            }
        }

        // TODO terrain component

        // transformation
        final float[] transform = jsonGo.get(JsonScene.GO_TRANSFORM).asFloatArray();
        go.setLocalPosition(transform[0], transform[1], transform[2]);
        go.setLocalRotation(transform[3], transform[4], transform[5], transform[6]);
        go.setLocalScale(transform[7], transform[8], transform[9]);

        // children
        JsonValue children = jsonGo.get(JsonScene.GO_CHILDREN);
        if(children != null) {
            for(JsonValue c : children) {
                go.addChild(convertGameObject(sceneGraph, c));
            }
        }

        return go;
    }


}
