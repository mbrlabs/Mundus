package com.mbrlabs.mundus.core.home;

import com.mbrlabs.mundus.core.project.ProjectRef;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 25-11-2015
 */
public class ProjectRefs {

    private List<ProjectRef> projects;
    private ProjectRef curProject;

    public ProjectRefs() {
        projects = new ArrayList<>();
    }

    public List<ProjectRef> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectRef> projects) {
        this.projects = projects;
    }

    public ProjectRef getCurProject() {
        return curProject;
    }

    public void setCurProject(ProjectRef curProject) {
        this.curProject = curProject;
    }

}
