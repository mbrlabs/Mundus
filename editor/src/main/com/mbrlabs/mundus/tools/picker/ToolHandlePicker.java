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

package com.mbrlabs.mundus.tools.picker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.core.EditorScene;
import com.mbrlabs.mundus.tools.ToolHandle;

/**
 * @author Marcus Brummer
 * @version 07-03-2016
 */
public class ToolHandlePicker extends BasePicker {

    public ToolHandlePicker() {
        super();
    }

    public ToolHandle pick(Array<ToolHandle> handles, EditorScene scene, int screenX, int screenY) {
        begin(scene.viewport);
        renderPickableScene(handles, scene.sceneGraph.batch, scene.cam);
        end();
        Pixmap pm = getFrameBufferPixmap(scene.viewport);

        int x = screenX - scene.viewport.getScreenX();
        int y = screenY - (Gdx.graphics.getHeight() - (scene.viewport.getScreenY() + scene.viewport.getScreenHeight()));

        int id = PickerColorEncoder.decode(pm.getPixel(x, y));
        System.out.println(id);

        for(ToolHandle handle : handles) {
            if(handle.getId() == id) {
                return handle;
            }
        }

        return null;
    }

    private void renderPickableScene(Array<ToolHandle> handles, ModelBatch batch, PerspectiveCamera cam) {
        batch.begin(cam);
        for(ToolHandle handle : handles) {
            handle.renderPick();
        }
        batch.end();
    }


    @Override
    public void dispose() {
        fbo.dispose();
    }

}
