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

package com.mbrlabs.mundus.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mbrlabs.mundus.core.project.ProjectContext;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
public class TranslateTool extends Tool {

    public static final String NAME = "Translate";

    private Drawable icon;


    public TranslateTool(ProjectContext projectContext, Shader shader, ModelBatch batch) {
        super(projectContext, shader, batch);

        icon = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/translateTool.png"))));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Drawable getIcon() {
        return icon;
    }

    @Override
    public void render() {

    }

    @Override
    public void act() {

    }

    @Override
    public void dispose() {

    }

}
