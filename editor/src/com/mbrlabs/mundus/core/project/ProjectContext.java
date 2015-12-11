package com.mbrlabs.mundus.core.project;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.terrain.Terrain;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class ProjectContext implements Disposable {

    private ProjectRef ref = null;

    public Environment environment = new Environment();
    public Array<ModelInstance> entities;
    public Array<Model> models;
    public PointLight light;

    public Array<Terrain> terrains;

    public ProjectContext() {
        entities = new Array<>();
        models = new Array<>();
        terrains = new Array<Terrain>();

        light = new PointLight();
        light.setPosition(400,300,400);
        light.setIntensity(1);
        environment.add(light);
    }

    public void copyFrom(ProjectContext other) {
        ref = other.ref;
        environment = other.environment;
        entities = other.entities;
        models = other.models;
        light = other.light;
        terrains = other.terrains;
    }

    public ProjectRef getRef() {
        return ref;
    }

    public void setRef(ProjectRef ref) {
        this.ref = ref;
    }

    @Override
    public void dispose() {
        for(Model model : models) {
            model.dispose();
        }
        models = null;
    }

}
