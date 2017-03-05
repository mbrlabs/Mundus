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

package com.mbrlabs.mundus.editor.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ArrowShapeBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Disposable

/**
 * @author Marcus Brummer
 * @version 05-12-2015
 */
class Compass(private var worldCam: PerspectiveCamera?) : Disposable {

    private val ARROW_LENGTH = 0.05f
    private val ARROW_THIKNESS = 0.4f
    private val ARROW_CAP_SIZE = 0.2f
    private val ARROW_DIVISIONS = 5

    private val ownCam: PerspectiveCamera = PerspectiveCamera()
    private val compassModel: Model
    private val compassInstance: ModelInstance

    private val tempV3 = Vector3()

    init {

        val modelBuilder = ModelBuilder()
        modelBuilder.begin()

        val builder = modelBuilder.part("compass", GL20.GL_TRIANGLES,
                (VertexAttributes.Usage.Position or VertexAttributes.Usage.ColorUnpacked).toLong(), Material())
        builder.setColor(Color.RED)
        ArrowShapeBuilder.build(builder, 0f, 0f, 0f, ARROW_LENGTH, 0f, 0f, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS)
        builder.setColor(Color.GREEN)
        ArrowShapeBuilder.build(builder, 0f, 0f, 0f, 0f, ARROW_LENGTH, 0f, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS)
        builder.setColor(Color.BLUE)
        ArrowShapeBuilder.build(builder, 0f, 0f, 0f, 0f, 0f, ARROW_LENGTH, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS)
        compassModel = modelBuilder.end()
        compassInstance = ModelInstance(compassModel)

        // translate to top left corner
        compassInstance.transform.translate(0.93f, 0.94f, 0f)
    }

    fun setWorldCam(cam: PerspectiveCamera) {
        this.worldCam = cam
    }

    fun render(batch: ModelBatch) {
        update()
        batch.begin(ownCam)
        batch.render(compassInstance)
        batch.end()
    }

    private fun update() {
        compassInstance.transform.getTranslation(tempV3)
        compassInstance.transform.set(worldCam!!.view)
        compassInstance.transform.setTranslation(tempV3)
    }

    override fun dispose() {
        compassModel.dispose()
    }

}
