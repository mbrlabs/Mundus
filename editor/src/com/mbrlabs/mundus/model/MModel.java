package com.mbrlabs.mundus.model;

import com.badlogic.gdx.graphics.g3d.Model;

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class MModel {

    public long id;
    public String name;
    public String g3dbPath;

    private Model model;

    public MModel() {

    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

}
