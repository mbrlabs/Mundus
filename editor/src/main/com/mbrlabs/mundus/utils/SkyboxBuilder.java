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

package com.mbrlabs.mundus.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.skybox.Skybox;

/**
 * @author Marcus Brummer
 * @version 10-01-2016
 */
public class SkyboxBuilder {

    public static Skybox createDefaultSkybox() {
        FileHandle texture = Gdx.files.internal("textures/skybox/default/skybox_default.png");
        return new Skybox(texture, texture, texture, texture, texture, texture);
    }

    public static Skybox createNightSkybox() {
        FileHandle texture = Gdx.files.internal("textures/skybox/star_night.png");
        return new Skybox(texture, texture, texture, texture, texture, texture);
    }

}
