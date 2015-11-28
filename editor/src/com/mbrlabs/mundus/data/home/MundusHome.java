package com.mbrlabs.mundus.data.home;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mbrlabs.mundus.data.JsonManager;
import com.mbrlabs.mundus.data.projects.Projects;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class MundusHome implements JsonManager {

    public static final String HOME_DIR = FilenameUtils.concat(FileUtils.getUserDirectoryPath(), ".mundus/");
    public static final String LOGS_DIR = FilenameUtils.concat(HOME_DIR, "logs/");
    public static final String SETTINGS_JSON = FilenameUtils.concat(HOME_DIR, "settings.json");
    public static final String PROJECTS_JSON = FilenameUtils.concat(HOME_DIR, "projects.json");

    private static MundusHome instance;

    private Json json;

    private Projects projects;
    private Settings settings;

    private MundusHome() {
        json = new Json();
        load();
    }

    public static MundusHome getInstance() {
        if(instance == null) {
            instance = new MundusHome();
        }
        return instance;
    }

    public static void bootstrap() {
        new File(HOME_DIR).mkdirs();
        new File(LOGS_DIR).mkdirs();
    }

    public Projects getProjects() {
        return projects;
    }

    public Settings getSettings() {
        return settings;
    }

    public void load() {
        // settings
        FileHandle file = Gdx.files.absolute(SETTINGS_JSON);
        if(file.exists()) {
            settings = json.fromJson(Settings.class, file);
        } else {
            settings = new Settings();
        }

        // projects
        file = Gdx.files.absolute(PROJECTS_JSON);
        if(file.exists()) {
            projects = json.fromJson(Projects.class, file);
        } else {
            projects = new Projects();
        }
    }

    public void save() {
        // settings
        FileHandle file = Gdx.files.absolute(SETTINGS_JSON);
        json.toJson(settings, file);

        // projects
        file = Gdx.files.absolute(PROJECTS_JSON);
        json.toJson(projects, file);
    }

}
