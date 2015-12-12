package com.mbrlabs.mundus.events;

import com.mbrlabs.mundus.core.model.PersistableModel;

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class ModelImportEvent {

    private PersistableModel model;

    public ModelImportEvent(PersistableModel model) {
        this.model = model;
    }

    public PersistableModel getModel() {
        return model;
    }

    public void setModel(PersistableModel model) {
        this.model = model;
    }

}
