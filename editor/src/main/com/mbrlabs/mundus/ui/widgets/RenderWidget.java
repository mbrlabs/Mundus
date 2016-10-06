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

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mbrlabs.mundus.ui.Ui;

/**
 * @author Marcus Brummer
 * @version 27-01-2016
 */
public class RenderWidget extends Widget {

    private static Vector2 vec = new Vector2();

    private ScreenViewport viewport;
    private PerspectiveCamera cam;
    private Renderer renderer;

    public RenderWidget(PerspectiveCamera cam) {
        super();
        this.cam = cam;
        viewport = new ScreenViewport(this.cam);
    }

    public RenderWidget() {
        super();
        viewport = new ScreenViewport();
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setCam(PerspectiveCamera cam) {
        this.cam = cam;
        viewport.setCamera(cam);
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (renderer == null || cam == null) return;

        // render part of the ui & pause rest
        batch.end();

        vec.set(getOriginX(), getOriginY());
        vec = localToStageCoordinates(vec);
        final int width = (int) getWidth();
        final int height = (int) getHeight();

        // apply widget viewport
        viewport.setScreenBounds((int) vec.x, (int) vec.y, width, height);
        viewport.setWorldSize(width * viewport.getUnitsPerPixel(), height * viewport.getUnitsPerPixel());
        viewport.apply();

        // render 3d scene
        renderer.render(cam);

        // re-apply stage viewport
        Ui.getInstance().getViewport().apply();

        // proceed ui rendering
        batch.begin();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
    }

    /**
     * Used to render the 3d scene within this widget.
     */
    public static interface Renderer {
        public void render(Camera cam);
    }

}
