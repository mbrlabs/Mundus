package com.mbrlabs.mundus.core;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.mbrlabs.mundus.model.MModelInstance;
import com.mbrlabs.mundus.terrain.TerrainGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 22-12-2015
 */
public class Scene {

    private String name;
    private long id;

    public List<MModelInstance> entities;
    public TerrainGroup terrainGroup;
    public Environment environment;

    public Scene() {
        entities = new ArrayList<>();
        terrainGroup = new TerrainGroup();
        environment = new Environment();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
