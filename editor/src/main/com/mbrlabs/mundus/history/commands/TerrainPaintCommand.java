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

import com.badlogic.gdx.graphics.Pixmap;
import com.mbrlabs.mundus.commons.terrain.SplatMap;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.history.Command;

/**
 * @author Marcus Brummer
 * @version 07-02-2016
 */
public class TerrainPaintCommand implements Command {

    private Terrain terrain;

    private Pixmap after;
    private Pixmap before;

    public TerrainPaintCommand(Terrain terrain) {
        this.terrain = terrain;
    }

    public void setAfter(Pixmap data) {
        after = new Pixmap(data.getWidth(), data.getHeight(), data.getFormat());
        after.drawPixmap(data, 0, 0);
    }

    public void setBefore(Pixmap data) {
        before = new Pixmap(data.getWidth(), data.getHeight(), data.getFormat());
        before.drawPixmap(data, 0, 0);
    }

    @Override
    public void execute() {
        SplatMap sm = terrain.getTerrainTexture().getSplatmap();
        if(sm != null) {
            sm.getPixmap().drawPixmap(after, 0, 0);
            sm.updateTexture();
        }
    }

    @Override
    public void undo() {
        SplatMap sm = terrain.getTerrainTexture().getSplatmap();
        if(sm != null) {
            sm.getPixmap().drawPixmap(before, 0, 0);
            sm.updateTexture();
        }
    }

    @Override
    public void dispose() {
        after.dispose();
        before.dispose();
        after = null;
        before = null;
        terrain = null;
    }

}
