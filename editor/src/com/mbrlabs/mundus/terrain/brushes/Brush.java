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

package com.mbrlabs.mundus.terrain.brushes;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.terrain.TerrainGroup;

/**
 * @author Marcus Brummer
 * @version 03-12-2015
 */
public interface Brush extends Disposable {

    public void draw(TerrainGroup terrainGroup, boolean up);
    public void scale(float scale);

    public Drawable getIcon();
    public String getName();

    public void render(PerspectiveCamera cam, ModelBatch modelBatch);

    public void setTranslation(Vector3 translation);

}
