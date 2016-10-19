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

package com.mbrlabs.mundus.editor.history.commands;

import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.editor.core.Mundus;
import com.mbrlabs.mundus.editor.events.GameObjectModifiedEvent;
import com.mbrlabs.mundus.editor.history.Command;

/**
 * @author Marcus Brummer
 * @version 07-02-2016
 */
public class TranslateCommand implements Command {

    private static GameObjectModifiedEvent modEvent = new GameObjectModifiedEvent();

    private Vector3 before;
    private Vector3 after;
    private GameObject go;

    public TranslateCommand(GameObject go) {
        this.before = new Vector3();
        this.after = new Vector3();
        this.go = go;
    }

    public void setBefore(Vector3 before) {
        this.before.set(before);
    }

    public void setAfter(Vector3 after) {
        this.after.set(after);
    }

    public void setGo(GameObject go) {
        this.go = go;
    }

    @Override
    public void execute() {
        go.setLocalPosition(after.x, after.y, after.z);
        modEvent.setGameObject(go);
        Mundus.postEvent(modEvent);
    }

    @Override
    public void undo() {
        go.setLocalPosition(before.x, before.y, before.z);
        modEvent.setGameObject(go);
        Mundus.postEvent(modEvent);
    }

    @Override
    public void dispose() {
        before = null;
        after = null;
        go = null;
    }

}
