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

package com.mbrlabs.mundus.editor.core.project;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.editor.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.core.EditorScene;
import com.mbrlabs.mundus.editor.utils.Log;

/**
 * A project context represents an loaded and opened project.
 *
 * A project context can have many scenes, nut only one scene at a time can be
 * active.
 *
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class ProjectContext implements Disposable {

    private static final String TAG = ProjectContext.class.getSimpleName();

    public String path;
    public String name;

    public Array<String> scenes;
    public EditorScene currScene;

    public EditorAssetManager assetManager;

    private int idProvider;

    /** set by kryo when project is loaded. do not use this */
    public String activeSceneName;

    public ProjectContext(int idProvider) {
        scenes = new Array<>();
        currScene = new EditorScene();
        this.idProvider = idProvider;
    }

    public synchronized int obtainID() {
        idProvider += 1;
        return idProvider;
    }

    public synchronized int inspectCurrentID() {
        return idProvider;
    }

    @Override
    public void dispose() {
        Log.debug(TAG, "Disposing current project: {}", path);
        if (assetManager != null) {
            assetManager.dispose();
        }
    }

}
