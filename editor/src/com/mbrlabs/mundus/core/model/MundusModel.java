package com.mbrlabs.mundus.core.model;

import com.badlogic.gdx.graphics.g3d.Model;

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class MundusModel {

    private Model model;

    private long id;
    private String name;
    private String g3dbPath;

    public MundusModel() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getG3dbPath() {
        return g3dbPath;
    }

    public void setG3dbPath(String g3dbPath) {
        this.g3dbPath = g3dbPath;
    }


}
