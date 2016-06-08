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

package com.mbrlabs.mundus.tools.picker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.core.EditorScene;
import com.mbrlabs.mundus.scene3d.components.PickableComponent;

/**
 * Renders a scene graph to an offscreen FBO, encodes the game object's id in the game object's
 * render color (see GameObjectPickerShader) and does mouse picking by decoding the picked color.
 *
 * See also: http://www.opengl-tutorial.org/miscellaneous/clicking-on-objects/picking-with-an-opengl-hack/
 *
 * @author Marcus Brummer
 * @version 20-02-2016
 */
public class GameObjectPicker extends BasePicker {

    public GameObjectPicker() {
        super();
    }

    public GameObject pick(EditorScene scene, int screenX, int screenY) {
        begin(scene.viewport);
        renderPickableScene(scene.sceneGraph);
        end();
        Pixmap pm = getFrameBufferPixmap(scene.viewport);

        int x = screenX - scene.viewport.getScreenX();
        int y = screenY - (Gdx.graphics.getHeight() - (scene.viewport.getScreenY() + scene.viewport.getScreenHeight()));

        int id = PickerColorEncoder.decode(pm.getPixel(x, y));
        System.out.println(id);
        for(GameObject go : scene.sceneGraph.getGameObjects()) {
            if(id == go.getId()) return go;
            for(GameObject child : go) {
                if(id == child.getId()) return child;
            }
        }

        return null;
    }

    private void renderPickableScene(SceneGraph sceneGraph) {
        sceneGraph.batch.begin(sceneGraph.scene.cam);
        for(GameObject go : sceneGraph.getGameObjects()) {
            renderPickableGameObject(go);
        }
        sceneGraph.batch.end();
    }

    private void renderPickableGameObject(GameObject go) {
        for(Component c : go.getComponents()) {
            if(c instanceof PickableComponent) {
                ((PickableComponent) c).renderPick();
            }
        }

        if(go.getChilds() != null) {
            for(GameObject goc : go.getChilds()) {
                renderPickableGameObject(goc);
            }
        }
    }

}
