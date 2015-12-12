package com.mbrlabs.mundus.core.model;

import com.badlogic.gdx.graphics.g3d.Model;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class PersistableModel {

    @Tag(0)
    private long id;
    @Tag(1)
    private String name;
    @Tag(2)
    private String relG3dbPath;

    private Model model;

    public PersistableModel() {

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

    public String getRelG3dbPath() {
        return relG3dbPath;
    }

    public void setRelG3dbPath(String g3dbPath) {
        this.relG3dbPath = g3dbPath;
    }


}
