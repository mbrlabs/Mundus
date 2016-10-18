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
package com.mbrlabs.mundus;

import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.mbrlabs.mundus.utils.Log;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class Main {

    private static final String TAG = Main.class.getSimpleName();
    public static final String TITLE = "Mundus v0.1.0";

    public static WindowCloseListener closeListener = null;

    public static void main(String[] arg) {
        // Start Log instance
        Log.init();
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle(TITLE);
        Log.info(TAG, "Starting [{}]", TITLE);

        // Set initial window size. See issue #11
        DisplayMode dm = Lwjgl3ApplicationConfiguration.getDisplayMode();
        if (System.getProperties().getProperty("os.name").toLowerCase().contains("mac")) {
            config.setWindowedMode((int) (dm.width * 0.80f), (int) (dm.height * 0.80f));
        } else {
            config.setWindowedMode((int) (dm.width * 0.95f), (int) (dm.height * 0.95f));
        }
        config.setWindowPosition(-1, -1);

        config.setWindowListener(new Lwjgl3WindowAdapter() {
            @Override
            public boolean closeRequested() {
                if (closeListener != null) {
                    closeListener.onCloseRequested();
                    return false;
                }
                return true;
            }
        });
        new Lwjgl3Application(new Editor(), config);
        Log.info(TAG, "Shutting down [{}]", TITLE);
    }

    public interface WindowCloseListener {

        boolean onCloseRequested();
    }

}
