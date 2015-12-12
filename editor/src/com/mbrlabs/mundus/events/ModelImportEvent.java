package com.mbrlabs.mundus.events;

import com.mbrlabs.mundus.core.model.MundusModel;

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class ModelImportEvent {

    private MundusModel model;

    public ModelImportEvent(MundusModel model) {
        this.model = model;
    }

    public MundusModel getModel() {
        return model;
    }

    public void setModel(MundusModel model) {
        this.model = model;
    }

}
