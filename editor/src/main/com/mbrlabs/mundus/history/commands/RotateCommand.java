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

package com.mbrlabs.mundus.history.commands;

import com.badlogic.gdx.math.Quaternion;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.events.GameObjectModifiedEvent;
import com.mbrlabs.mundus.history.Command;

/**
 * @author Marcus Brummer
 * @version 16-02-2016
 */
public class RotateCommand implements Command {

    private static GameObjectModifiedEvent modEvent = new GameObjectModifiedEvent();

    private Quaternion before;
    private Quaternion after;
    private GameObject go;

    public RotateCommand(GameObject go) {
        this.before = new Quaternion();
        this.after = new Quaternion();
        this.go = go;
    }

    public void setBefore(Quaternion before) {
        this.before.set(before);
    }

    public void setAfter(Quaternion after) {
        this.after.set(after);
    }

    public void setGo(GameObject go) {
        this.go = go;
    }

    @Override
    public void execute() {
        go.setLocalRotation(after.x, after.y, after.z, after.w);
        modEvent.setGameObject(go);
        Mundus.postEvent(modEvent);
    }

    @Override
    public void undo() {
        go.setLocalRotation(before.x, before.y, before.z, before.w);
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
