package com.mbrlabs.mundus.core.registry;

/**
 * A reference to a Mundus project, stored in the registry.
 *
 * @author Marcus Brummer
 * @version 07-06-2016
 */
public class ProjectRef {

    private String name;
    private String path;

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

}
