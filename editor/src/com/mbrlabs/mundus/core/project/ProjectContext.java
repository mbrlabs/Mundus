package com.mbrlabs.mundus.core.project;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.core.Scene;
import com.mbrlabs.mundus.model.MModel;
import com.mbrlabs.mundus.terrain.Terrains;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class ProjectContext implements Disposable {

    public Environment environment;

    public String path;
    public String name;
    public String id;

    public List<Scene> scenes;
    public Scene currScene;

    public List<MModel> models;
    public Terrains terrains;
    private long uuidProvider;
    public boolean loaded = false;

    public ProjectContext(long uuidProvider) {
        models = new ArrayList<MModel>();
        scenes = new ArrayList<>();
        terrains = new Terrains();
        this.uuidProvider = uuidProvider;

        environment = new Environment();
        PointLight pointLight = new PointLight();
        pointLight.setIntensity(1);
        pointLight.setPosition(0, 400, 0);
        pointLight.setColor(1,1,1,1);
        environment.add(pointLight);
    }

    public void copyFrom(ProjectContext other) {
        path = other.path;
        name = other.name;
        environment = other.environment;
        terrains = other.terrains;
        id = other.id;
        models = other.models;
        uuidProvider = other.uuidProvider;
    }

    public synchronized long obtainUUID() {
        uuidProvider += 1;
        return uuidProvider;
    }

    public synchronized long getCurrentUUID() {
        return uuidProvider;
    }

    @Override
    public void dispose() {
        for(MModel model : models) {
            model.getModel().dispose();
        }
        models = null;
    }

}
