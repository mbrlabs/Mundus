package com.mbrlabs.mundus.events;

import com.mbrlabs.mundus.model.MModel;

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class ModelImportEvent {

    private MModel model;

    public ModelImportEvent(MModel model) {
        this.model = model;
    }

    public MModel getModel() {
        return model;
    }

    public void setModel(MModel model) {
        this.model = model;
    }

}
