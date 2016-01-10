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

package com.mbrlabs.mundus;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.awt.*;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class Main {

    public static final String TITLE = "Mundus v0.0.4";

    public static void main (String[] arg) {

        final Rectangle maximumWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int width = (int) maximumWindowBounds.getWidth();
        int height = (int) maximumWindowBounds.getHeight();

        //Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.width = width;
        config.height = (int) (height - height * .05);
        config.backgroundFPS = 0;
        config.title = TITLE;

        //new Lwjgl3Application(new Editor(), config);
        new LwjglApplication(new Editor(), config);
    }

}
