package com.mbrlabs.mundus.settings.global;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class GlobalSettingsData {

    private List<Project> projects;

    public GlobalSettingsData() {
        projects = new ArrayList<>();
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

}
