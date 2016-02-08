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

package com.mbrlabs.mundus.ui.widgets;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mbrlabs.mundus.ui.Ui;

/**
 * @author Marcus Brummer
 * @version 27-01-2016
 */
public class Actor3D extends Actor {

    private ScreenViewport viewport;
    private PerspectiveCamera cam;

    private Renderer renderer;

    public Actor3D(PerspectiveCamera cam) {
        super();
        this.cam = cam;
        viewport = new ScreenViewport(this.cam);
    }

    public Actor3D() {
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
        // render part of the ui & pause rest
        batch.end();

        viewport.setScreenPosition((int)getX(), (int) getY());
        viewport.apply();
        renderer.render(cam);

        Ui.getInstance().getViewport().apply();

        // proceed ui rendering
        batch.begin();
    }

    @Override
    protected void positionChanged () {
        super.positionChanged();
        viewport.setScreenPosition((int)getX(), (int) getY());
    }

    @Override
    protected void sizeChanged () {
        super.sizeChanged();
        viewport.update((int)getWidth(), (int)getHeight());
    }

    public static interface Renderer {
        public void render(Camera cam);
    }

}
