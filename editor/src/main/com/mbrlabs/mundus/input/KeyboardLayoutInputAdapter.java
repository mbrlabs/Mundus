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

package com.mbrlabs.mundus.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

/**
 * Workaround for LWJGL3's (or GLFW's) ignorance of keyboard layouts for raw keyboard events.
 *
 * @author Marcus Brummer
 * @version 07-02-2016
 */
public class KeyboardLayoutInputAdapter extends InputAdapter {

    /**
     *
     */
    public static enum KeyboardLayout {
        QWERTY, QWERTZ
    }

    public KeyboardLayout layout = KeyboardLayout.QWERTZ;

    protected int convertKeycode(int code) {
        if(layout == KeyboardLayout.QWERTZ) {
            if(code == Input.Keys.Z) {
                return Input.Keys.Y;
            } else if(code ==  Input.Keys.Y) {
                return Input.Keys.Z;
            }
        }

        return code;
    }

}
