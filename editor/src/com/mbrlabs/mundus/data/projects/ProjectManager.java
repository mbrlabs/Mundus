package com.mbrlabs.mundus.data.projects;

import com.badlogic.gdx.utils.Json;
import com.mbrlabs.mundus.Mundus;
import com.mbrlabs.mundus.data.home.MundusHome;
import com.mbrlabs.mundus.data.home.Project;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * @author Marcus Brummer
 * @version 25-11-2015
 */
public class ProjectManager {


    public static Project createProject(String name, String folder) {
        Project project = new Project();
        project.setName(name);
        project.setPath(FilenameUtils.concat(folder, name));
        project.setCreated(System.currentTimeMillis());
        project.setLastOpened(System.currentTimeMillis());
        MundusHome.getInstance().getProjects().getProjects().add(project);
        MundusHome.getInstance().save();

        File dir = new File(folder, name);
        dir.mkdirs();

        File modelDir = new File(dir.getAbsolutePath(), "models");
        modelDir.mkdirs();

        return project;
    }


}
