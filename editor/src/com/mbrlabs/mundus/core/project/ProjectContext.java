package com.mbrlabs.mundus.core.project;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.core.model.MundusModel;
import com.mbrlabs.mundus.core.model.MundusModelInstance;
import com.mbrlabs.mundus.terrain.Terrain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class ProjectContext implements Disposable {

    public ProjectRef ref = null;

    public Environment environment = new Environment();
    public List<MundusModelInstance> entities;
    public List<MundusModel> models;

    public List<Terrain> terrains;

    private long nextAvailableID = 0;

    public ProjectContext() {
        entities = new ArrayList<>();
        models = new ArrayList<>();
        terrains = new ArrayList<Terrain>();

        PointLight light = new PointLight();
        light.setPosition(400,300,400);
        light.setIntensity(1);
        environment.add(light);
    }

    public void copyFrom(ProjectContext other) {
        ref = other.ref;
        environment = other.environment;
        entities = other.entities;
        models = other.models;
        terrains = other.terrains;
    }

    public synchronized long requestUniqueID() {
        return nextAvailableID++;
    }

    @Override
    public void dispose() {
        for(MundusModel model : models) {
            model.getModel().dispose();
        }
        models = null;
    }

}
