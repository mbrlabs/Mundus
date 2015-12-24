package com.mbrlabs.mundus.core.project;

import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.core.Scene;
import com.mbrlabs.mundus.model.MModel;
import com.mbrlabs.mundus.terrain.Terrain;

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

    public List<Scene> scenes;
    public Scene currScene;

    public List<MModel> models;
    public List<Terrain> terrains;

    private long uuidProvider;
    public boolean loaded = false;

    public ProjectContext(long uuidProvider) {
        models = new ArrayList<>();
        scenes = new ArrayList<>();
        currScene = null;
        terrains = new ArrayList<>();
        this.uuidProvider = uuidProvider;

    }

    public void copyFrom(ProjectContext other) {
        path = other.path;
        name = other.name;
        terrains = other.terrains;
        currScene = other.currScene;
        scenes = other.scenes;
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
