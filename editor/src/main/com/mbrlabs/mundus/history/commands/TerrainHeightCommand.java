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

package com.mbrlabs.mundus.history.commands;

import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.history.Command;

/**
 * @author Marcus Brummer
 * @version 07-02-2016
 */
public class TerrainHeightCommand implements Command {

    private float[] heightDataBefore;
    private float[] heightDataAfter;

    private Terrain terrain;

    public TerrainHeightCommand(Terrain terrain) {
        this.terrain = terrain;
    }

    public void setHeightDataBefore(float[] data) {
        heightDataBefore = new float[data.length];
        System.arraycopy(data, 0, heightDataBefore, 0, data.length);
    }

    public void setHeightDataAfter(float[] data) {
        heightDataAfter = new float[data.length];
        System.arraycopy(data, 0, heightDataAfter, 0, data.length);
    }

    @Override
    public void execute() {
        terrain.heightData = heightDataAfter;
        terrain.update();
    }

    @Override
    public void undo() {
        terrain.heightData = heightDataBefore;
        terrain.update();
    }

    @Override
    public void dispose() {
        heightDataAfter = null;
        heightDataBefore = null;
        terrain = null;
    }

}
