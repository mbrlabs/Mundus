package com.mbrlabs.mundus.core.project;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;

import java.util.Date;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class ProjectRef {
    @Tag(0)
    private String name;
    @Tag(1)
    private String path;
    @Tag(2)
    private Date created;
    @Tag(3)
    private Date lastOpened;
    @Tag(4)
    private String id;

    public Date getLastOpened() {
        return lastOpened;
    }

    public void setLastOpened(Date lastOpened) {
        this.lastOpened = lastOpened;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
