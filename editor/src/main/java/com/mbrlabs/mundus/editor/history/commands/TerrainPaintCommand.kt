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

package com.mbrlabs.mundus.editor.history.commands

import com.badlogic.gdx.graphics.Pixmap
import com.mbrlabs.mundus.commons.terrain.Terrain
import com.mbrlabs.mundus.editor.history.DisposableCommand

/**
 * @author Marcus Brummer
 * @version 07-02-2016
 */
class TerrainPaintCommand(private var terrain: Terrain?) : DisposableCommand {

    private var after: Pixmap? = null
    private var before: Pixmap? = null

    fun setAfter(data: Pixmap) {
        after = Pixmap(data.width, data.height, data.format)
        after!!.drawPixmap(data, 0, 0)
    }

    fun setBefore(data: Pixmap) {
        before = Pixmap(data.width, data.height, data.format)
        before!!.drawPixmap(data, 0, 0)
    }

    override fun execute() {
        val sm = terrain!!.terrainTexture.splatmap
        if (sm != null) {
            sm.pixmap.drawPixmap(after!!, 0, 0)
            sm.updateTexture()
        }
    }

    override fun undo() {
        val sm = terrain!!.terrainTexture.splatmap
        if (sm != null) {
            sm.pixmap.drawPixmap(before!!, 0, 0)
            sm.updateTexture()
        }
    }

    override fun dispose() {
        after!!.dispose()
        before!!.dispose()
    }

}
