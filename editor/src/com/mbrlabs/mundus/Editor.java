package com.mbrlabs.mundus;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.terrain.Terrain;
import com.mbrlabs.mundus.utils.*;

public class Editor implements ApplicationListener {

    // axes
    private RenderContext renderContext;

    private ModelInstance axesInstance;

	@Override
	public void create () {
        Mundus.init();

        Model axesModel = UsefulMeshs.createAxes();
        axesInstance = new ModelInstance(axesModel);
        Mundus.testModels.add(axesModel);

        renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));

        createTestModels();
    }

	@Override
	public void render () {
        GlUtils.clearScreen(Colors.GRAY_222);

        Mundus.input.update();

        // update status bar
        Mundus.ui.getStatusBar().setFps(Gdx.graphics.getFramesPerSecond());
        Mundus.ui.getStatusBar().setVertexCount(0);

        // render model instances
        Mundus.modelBatch.begin(Mundus.cam);
        Mundus.modelBatch.render(axesInstance);
        Mundus.modelBatch.render(Mundus.projectContext.entities,
                Mundus.projectContext.environment, Mundus.shaders.entityShader);
        Mundus.modelBatch.render(Mundus.testInstances,
                Mundus.projectContext.environment, Mundus.shaders.entityShader);
        Mundus.modelBatch.end();

        // render terrains
        Mundus.shaders.terrainShader.begin(Mundus.cam, renderContext);
        for(Terrain terrain : Mundus.projectContext.terrains) {
            terrain.renderable.environment = Mundus.projectContext.environment;
            Mundus.shaders.terrainShader.render(terrain.renderable);
        }
        Mundus.shaders.terrainShader.end();

        // render active brush
        if(Mundus.brushes.getActiveBrush() != null) {
            Mundus.brushes.getActiveBrush().render(Mundus.modelBatch);
        }

        // render compass
        Mundus.compass.render(Mundus.modelBatch);

        // render UI
        Mundus.ui.draw();
	}

    @Deprecated
    private void createTestModels() {
        // boxes to test terrain height
        if(Mundus.projectContext.terrains.first() != null) {
            float boxSize = 0.5f;
            Model boxModel = new ModelBuilder().createBox(boxSize, boxSize,boxSize,
                    new Material(ColorAttribute.createDiffuse(Color.RED)),
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
            Mundus.testModels.add(boxModel);
            Mundus.testInstances.addAll(TestUtils.createABunchOfModelsOnTheTerrain(1000,
                    boxModel, Mundus.projectContext.terrains.first()));
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void resize(int width, int height) {
        Mundus.ui.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        Mundus.dispose();
    }

}
