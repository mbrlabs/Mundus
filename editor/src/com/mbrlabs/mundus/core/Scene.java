package com.mbrlabs.mundus.core;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
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
        PointLight pointLight = new PointLight();
        pointLight.setIntensity(1);
        pointLight.setPosition(0, 400, 0);
        pointLight.setColor(1,1,1,1);
        environment.add(pointLight);
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
