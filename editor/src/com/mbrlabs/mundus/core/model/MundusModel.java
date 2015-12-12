package com.mbrlabs.mundus.core.model;

import com.badlogic.gdx.graphics.g3d.Model;

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class MundusModel {

    private long id;
    private Model model;

    public MundusModel(Model model) {
        this.model = model;
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
}
