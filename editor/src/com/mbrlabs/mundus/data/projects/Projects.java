package com.mbrlabs.mundus.data.projects;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 25-11-2015
 */
public class Projects {

    private List<Project> projects;

    public Projects() {
        projects = new ArrayList<>();
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

}
