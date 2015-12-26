/*
 * Copyright (c) 2015. See AUTHORS file.
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

package com.mbrlabs.mundus.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class UiImages {

    public static Drawable saveIcon;
    public static Drawable importIcon;
    public static Drawable runIcon;
    public static Drawable exportIcon;

    public static void load() {
        saveIcon = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/toolbar/save.png"))));
        importIcon = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/toolbar/import.png"))));
        runIcon = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/toolbar/run.png"))));
        exportIcon = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/toolbar/export.png"))));
    }

    public static void dispose() {

    }

}
