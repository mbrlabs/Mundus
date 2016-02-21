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
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mbrlabs.mundus.Editor;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.core.EditorScene;
import com.mbrlabs.mundus.core.Mundus;

import java.nio.ByteBuffer;

/**
 * Renders a scene graph to an offscreen FBO, encodes the game object's id in the game object's
 * render color (see GameObjectPickerShader) and does mouse picking by decoding the picked color.
 *
 * See also: http://www.opengl-tutorial.org/miscellaneous/clicking-on-objects/picking-with-an-opengl-hack/
 *
 * @author Marcus Brummer
 * @version 20-02-2016
 */
public class GameObjectPicker implements Disposable {

    private FrameBuffer fbo;
    private GameObjectPickerShader shader;

    public GameObjectPicker() {
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    public GameObject pick(EditorScene scene, int screenX, int screenY) {
        begin(scene.viewport);
        scene.sceneGraph.render();
        end();
        Pixmap pm = getFrameBufferPixmap(scene.viewport);

        int x = screenX - scene.viewport.getScreenX();
        int y = screenY - (Gdx.graphics.getHeight() - (scene.viewport.getScreenY() + scene.viewport.getScreenHeight()));

        int id = decodeID(pm.getPixel(x, y));
        for(GameObject go : scene.sceneGraph.getRoot()) {
            if(id == go.getId()) return go;
        }

        return null;
    }

    /**
     * Decodes a rgba8888 cloor code to a game object id.
     *
     * @param rgba8888Code  rgba8888 color code
     * @return
     */
    public static int decodeID(int rgba8888Code) {
        int id = (rgba8888Code & 0xFF000000) >>> 24;
        id += ((rgba8888Code & 0x00FF0000) >>> 16) * 256;
        id += ((rgba8888Code & 0x0000FF00) >>> 8) * 256 * 256;

        return id;
    }

    private void begin(Viewport viewport) {
        Mundus.RAY_PICK_RENDERING = true;
        fbo.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        HdpiUtils.glViewport(viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
    }

    private void end() {
        fbo.end();
        Mundus.RAY_PICK_RENDERING = false;
    }

    public Pixmap getFrameBufferPixmap (Viewport viewport) {
        int w = viewport.getScreenWidth();
        int h = viewport.getScreenHeight();
        int x = viewport.getScreenX();
        int y = viewport.getScreenY();
        final ByteBuffer pixelBuffer = BufferUtils.newByteBuffer(w * h * 4);

        Gdx.gl30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, fbo.getFramebufferHandle());
        Gdx.gl30.glReadPixels(x, y, w, h, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, pixelBuffer);
        Gdx.gl30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);

        final int numBytes = w * h * 4;
        byte[] imgLines = new byte[numBytes];
        final int numBytesPerLine = w * 4;
        for (int i = 0; i < h; i++) {
            pixelBuffer.position((h - i - 1) * numBytesPerLine);
            pixelBuffer.get(imgLines, i * numBytesPerLine, numBytesPerLine);
        }

        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        BufferUtils.copy(imgLines, 0, pixmap.getPixels(), imgLines.length);

        return pixmap;
    }


    @Override
    public void dispose() {

    }
}
