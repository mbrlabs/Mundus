package com.mbrlabs.mundus.data.home;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mbrlabs.mundus.data.JsonManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.UUID;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class MundusHome implements JsonManager {

    public static final String HOME_DIR = FilenameUtils.concat(FileUtils.getUserDirectoryPath(), ".mundus/");
    public static final String LOGS_DIR = FilenameUtils.concat(HOME_DIR, "logs/");
    public static final String MODEL_CACHE_DIR = FilenameUtils.concat(HOME_DIR, "model_cache/");
    public static final String SETTINGS_JSON = FilenameUtils.concat(HOME_DIR, "settings.json");
    public static final String PROJECTS_JSON = FilenameUtils.concat(HOME_DIR, "projects.json");

    private static MundusHome instance;

    private Json json;

    private ProjectRefs projectRefs;
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
        new File(MODEL_CACHE_DIR).mkdirs();
    }

    public ProjectRefs getProjectRefs() {
        return projectRefs;
    }

    public Settings getSettings() {
        return settings;
    }

    public ProjectRef createProjectRef(String name, String folder) {
        String path = FilenameUtils.concat(folder, name);
        ProjectRef projectRef = new ProjectRef();
        projectRef.setName(name);
        projectRef.setPath(path);
        projectRef.setCreated(System.currentTimeMillis());
        projectRef.setLastOpened(System.currentTimeMillis());
        projectRefs.getProjects().add(projectRef);
        save();

        return projectRef;
    }

    public void purgeModelCache() {
        for(FileHandle f : Gdx.files.absolute(MundusHome.MODEL_CACHE_DIR).list()) {
            f.deleteDirectory();
        }
    }

    public FileHandle createTempModelFolder() {
        String tempFolderPath = FilenameUtils.concat(
                MundusHome.MODEL_CACHE_DIR, UUID.randomUUID().toString()) + "/";
        FileHandle tempFolder = Gdx.files.absolute(tempFolderPath);
        tempFolder.mkdirs();

        return tempFolder;
    }

    public void load() {
        // settings
        FileHandle file = Gdx.files.absolute(SETTINGS_JSON);
        if(file.exists()) {
            settings = json.fromJson(Settings.class, file);
        } else {
            settings = new Settings();
        }

        // project refs
        file = Gdx.files.absolute(PROJECTS_JSON);
        if(file.exists()) {
            projectRefs = json.fromJson(ProjectRefs.class, file);
        } else {
            projectRefs = new ProjectRefs();
        }
    }

    public void save() {
        // settings
        FileHandle file = Gdx.files.absolute(SETTINGS_JSON);
        json.toJson(settings, file);

        // project refs
        file = Gdx.files.absolute(PROJECTS_JSON);
        json.toJson(projectRefs, file);
    }

}
