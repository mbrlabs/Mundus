package com.mbrlabs.mundus;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.utils.UsefulMeshs;

/**
 * @author Marcus Brummer
 * @version 27-11-2015
 */
public class World implements Disposable {

    public Array<ModelInstance> entities;
    public Array<Model> models;

    // axes
    public Model axesModel;
    public ModelInstance axesInstance;

    public PointLight light;
    public Environment environment = new Environment();


    public World() {
        entities = new Array<>();
        models = new Array<>();

        axesModel = UsefulMeshs.createAxes();
        axesInstance = new ModelInstance(axesModel);

        light = new PointLight();
        light.setPosition(0,10,-10);
        light.setIntensity(1);
        environment.add(light);
    }



    @Override
    public void dispose() {
        for(Model model : models) {
            model.dispose();
        }
        models = null;

        axesModel.dispose();
        axesModel = null;
    }

}
