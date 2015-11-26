package com.mbrlabs.mundus.data.projects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mbrlabs.mundus.data.JsonManager;
import com.mbrlabs.mundus.data.settings.SettingsManager;

/**
 * @author Marcus Brummer
 * @version 25-11-2015
 */
public class ProjectManager implements JsonManager {

    private static ProjectManager INSTANCE;

    public static final String JSON_FILE_NAME = "projects.json";

    private Json json;
    private Projects projects;

    public static ProjectManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ProjectManager();
        }

        return INSTANCE;
    }

    private ProjectManager() {
        json = new Json();
        load();
    }

    public Project createProject(String name, String folder) {
        Project project = new Project();
        project.setName(name);
        project.setPath(folder);
        project.setCreated(System.currentTimeMillis());
        project.setLastOpened(System.currentTimeMillis());
        projects.getProjects().add(project);
        save();

        return project;
    }


    @Override
    public void load() {
        final String path = SettingsManager.MUNDUS_HOME + "/" + JSON_FILE_NAME;
        FileHandle projectsFile = Gdx.files.absolute(path);

        if(projectsFile.exists()) {
            projects = json.fromJson(Projects.class, projectsFile);
        } else {
            projects = new Projects();
        }
    }

    @Override
    public void save() {
        final String path = SettingsManager.MUNDUS_HOME + "/" + JSON_FILE_NAME;
        FileHandle projectsFile = Gdx.files.absolute(path);
        json.toJson(projects, projectsFile);
    }

}
