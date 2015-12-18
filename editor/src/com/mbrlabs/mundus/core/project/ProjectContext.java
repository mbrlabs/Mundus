package com.mbrlabs.mundus.core.project;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.model.MModel;
import com.mbrlabs.mundus.model.MModelInstance;
import com.mbrlabs.mundus.terrain.Terrains;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class ProjectContext implements Disposable {

    public String path;
    public String name;
    public String id;

    public Environment environment = new Environment();
    public List<MModelInstance> entities;

    public List<MModel> models;

    public Terrains terrains;

    private long nextAvailableID;

    public boolean loaded = false;

    public ProjectContext(long nextAvailableID) {
        entities = new ArrayList<>();
        models = new ArrayList<>();
        terrains = new Terrains();
        this.nextAvailableID = nextAvailableID;

        PointLight light = new PointLight();
        light.setPosition(400,300,400);
        light.setIntensity(1);
        environment.add(light);
    }

    public void copyFrom(ProjectContext other) {
        path = other.path;
        name = other.name;
        id = other.id;
        environment = other.environment;
        entities = other.entities;
        models = other.models;
        terrains = other.terrains;
        nextAvailableID = other.nextAvailableID;
    }

    public synchronized long obtainAvailableID() {
        nextAvailableID += 1;
        return nextAvailableID - 1;
    }

    public synchronized long getNextAvailableID() {
        return nextAvailableID;
    }

    @Override
    public void dispose() {
        for(MModel model : models) {
            model.getModel().dispose();
        }
        models = null;
    }

}
