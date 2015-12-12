package com.mbrlabs.mundus.core.model;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class MundusModelInstance extends ModelInstance {

    private long id;
    private long modelId;

    public MundusModelInstance(MundusModel model) {
        super(model.getModel());
        modelId = model.getId();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getModelId() {
        return modelId;
    }

    public void setModelId(long modelId) {
        this.modelId = modelId;
    }

}
