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

package com.mbrlabs.mundus.editor.events;

import com.mbrlabs.mundus.commons.Scene;

/**
 * @author Marcus Brummer
 * @version 21-01-2016
 */
public class SceneAddedEvent {

    private Scene scene;

    public SceneAddedEvent(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public static interface SceneAddedListener {
        @Subscribe
        public void onSceneAdded(SceneAddedEvent sceneAddedEvent);
    }

}
