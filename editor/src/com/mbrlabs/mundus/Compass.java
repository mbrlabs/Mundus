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
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

/**
 * @author Marcus Brummer
 * @version 05-12-2015
 */
public class Compass implements Disposable {

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

        MeshPartBuilder builder = modelBuilder.part("compass", GL20.GL_LINES, VertexAttributes.Usage.Position
                | VertexAttributes.Usage.ColorUnpacked, new Material());

        builder.setColor(Color.RED);
        builder.line(0, 0, 0, 0.1f, 0, 0);
        builder.setColor(Color.GREEN);
        builder.line(0, 0, 0, 0, 0.1f, 0);
        builder.setColor(Color.BLUE);
        builder.line(0, 0, 0, 0, 0, 0.1f);
        compassModel =  modelBuilder.end();
        compassInstance = new ModelInstance(compassModel);
    }

    public void render(ModelBatch batch) {
        update();
        batch.begin(ownCam);
        batch.render(compassInstance);
        batch.end();
    }

    private void update() {
       // compassInstance.transform.setTranslation(ownCam.position.x, ownCam.position.y-1, ownCam.position.z);
        //ownCam.direction.set(worldCam.direction);
      //  ownCam.update();
        compassInstance.transform.getTranslation(tempV3);
        compassInstance.transform.set(worldCam.view);
        compassInstance.transform.setTranslation(tempV3);
    }

    @Override
    public void dispose() {

    }
}
