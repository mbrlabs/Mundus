/*
 * Copyright (c) 2015. See AUTHORS file.
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

package com.mbrlabs.mundus.utils;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

/**
 * @author Marcus Brummer
 * @version 05-12-2015
 */
public class Compass implements Disposable {

    private final float ARROW_LENGTH = 0.05f;
    private final float ARROW_THIKNESS = 0.4f;
    private final float ARROW_CAP_SIZE = 0.2f;
    private final int ARROW_DIVISIONS = 5;

    private PerspectiveCamera ownCam;
    private PerspectiveCamera worldCam;
    private Model compassModel;
    private ModelInstance compassInstance;

    private Vector3 tempV3 = new Vector3();

    public Compass(PerspectiveCamera worldCam) {
        this.worldCam = worldCam;
        this.ownCam = new PerspectiveCamera();

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        MeshPartBuilder builder = modelBuilder.part("compass", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position
                | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.RED);
        builder.arrow(0, 0, 0, ARROW_LENGTH, 0, 0, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS);
        builder.setColor(Color.GREEN);
        builder.arrow(0, 0, 0, 0, ARROW_LENGTH, 0, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS);
        builder.setColor(Color.BLUE);
        builder.arrow(0, 0, 0, 0, 0, ARROW_LENGTH, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS);
        compassModel =  modelBuilder.end();
        compassInstance = new ModelInstance(compassModel);

        // translate to top right corner
        compassInstance.transform.translate(0.95f,0.8f,0);
    }

    public void setWorldCam(PerspectiveCamera cam) {
        this.worldCam = cam;
    }

    public void render(ModelBatch batch) {
        update();
        batch.begin(ownCam);
        batch.render(compassInstance);
        batch.end();
    }

    private void update() {
        compassInstance.transform.getTranslation(tempV3);
        compassInstance.transform.set(worldCam.view);
        compassInstance.transform.setTranslation(tempV3);
    }

    @Override
    public void dispose() {
        compassModel.dispose();
    }

}
