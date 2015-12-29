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

package com.mbrlabs.mundus.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mbrlabs.mundus.commons.exporter.dto.ModelInstanceDTO;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.model.MModelInstance;
import org.lwjgl.opengl.GL11;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
public class TranslateTool extends SelectionTool {

    private final float ARROW_THIKNESS = 0.2f;
    private final float ARROW_CAP_SIZE = 0.1f;
    private final int ARROW_DIVISIONS = 10;

    public static final String NAME = "Translate";
    private Drawable icon;

    private Model xHandleModel;
    private Model yHandleModel;
    private Model zHandleModel;

    private ModelInstance xHandle;
    private ModelInstance yHandle;
    private ModelInstance zHandle;

    private Vector3 tvec3 = new Vector3();


    public TranslateTool(ProjectContext projectContext, Shader shader, ModelBatch batch) {
        super(projectContext, shader, batch);
        icon = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/translateTool.png"))));

        ModelBuilder modelBuilder = new ModelBuilder();

        xHandleModel =  modelBuilder.createArrow(0, 0, 0, 1, 0, 0, ARROW_CAP_SIZE, ARROW_THIKNESS,
                ARROW_DIVISIONS, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                VertexAttributes.Usage.Position);
        yHandleModel =  modelBuilder.createArrow(0, 0, 0, 0, 1, 0, ARROW_CAP_SIZE, ARROW_THIKNESS,
                ARROW_DIVISIONS, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position);
        zHandleModel =  modelBuilder.createArrow(0, 0, 0, 0, 0, 1, ARROW_CAP_SIZE, ARROW_THIKNESS,
                ARROW_DIVISIONS, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                VertexAttributes.Usage.Position);

        xHandle = new ModelInstance(xHandleModel);
        yHandle = new ModelInstance(yHandleModel);
        zHandle = new ModelInstance(zHandleModel);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Drawable getIcon() {
        return icon;
    }

    @Override
    public void modelSelected(MModelInstance modelInstance) {
        super.modelSelected(modelInstance);
        float radius = modelInstance.radius;
        xHandle.transform.setToScaling(radius*0.7f, radius/2, radius/2);
        yHandle.transform.setToScaling(radius/2, radius*0.7f, radius/2);
        zHandle.transform.setToScaling(radius/2, radius/2, radius*0.7f);
    }

    @Override
    public void render() {
        super.render();
        if(selectedEntity != null) {
            batch.begin(projectContext.currScene.cam);
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            batch.render(xHandle);
            batch.render(yHandle);
            batch.render(zHandle);
            batch.end();
        }
    }

    @Override
    public void act() {
        super.act();

        if(selectedEntity != null) {
            selectedEntity.modelInstance.transform.getTranslation(tvec3);
            tvec3.add(selectedEntity.center);
            xHandle.transform.setTranslation(tvec3);
            yHandle.transform.setTranslation(tvec3);
            zHandle.transform.setTranslation(tvec3);

        }
    }

    @Override
    public void dispose() {
        super.dispose();
        xHandleModel.dispose();
        yHandleModel.dispose();
        zHandleModel.dispose();
    }

}
