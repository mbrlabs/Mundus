package com.mbrlabs.mundus.settings.global;

import java.util.Date;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class Project {

    private String name;
    private String path;
    private long created;
    private long lastOpened;

    public long getLastOpened() {
        return lastOpened;
    }

    public void setLastOpend(long lastOpened) {
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

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

}
