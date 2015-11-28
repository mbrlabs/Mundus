package com.mbrlabs.mundus.data.home;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 25-11-2015
 */
public class ProjectRefs {

    private List<ProjectRef> projectRefs;

    public ProjectRefs() {
        projectRefs = new ArrayList<>();
    }

    public List<ProjectRef> getProjectRefs() {
        return projectRefs;
    }

    public void setProjectRefs(List<ProjectRef> projectRefs) {
        this.projectRefs = projectRefs;
    }

}
