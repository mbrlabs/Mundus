package com.mbrlabs.mundus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
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
