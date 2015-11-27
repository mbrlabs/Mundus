package com.mbrlabs.mundus.data.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mbrlabs.mundus.data.JsonManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class SettingsManager implements JsonManager {

    public static final String MUNDUS_HOME = FilenameUtils.concat(FileUtils.getUserDirectoryPath(), ".mundus/");

    public static final String JSON_FILE_NAME = "settings.json";

    private static SettingsManager INSTANCE;

    private Json json;
    private Settings settings;

    private SettingsManager() {
        json = new Json();
        load();
    }

    public static SettingsManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SettingsManager();
        }
        return INSTANCE;
    }

    public void load() {
        final String path = SettingsManager.MUNDUS_HOME + JSON_FILE_NAME;
        FileHandle settingsFile = Gdx.files.absolute(path);
        if(settingsFile.exists()) {
            settings = json.fromJson(Settings.class, settingsFile);
        } else {
            settings = new Settings();
        }
    }

    public void save() {
        final String path = MUNDUS_HOME + "/" + JSON_FILE_NAME;
        FileHandle settingsFile = Gdx.files.absolute(path);
        json.toJson(settings, settingsFile);
    }

    public Settings getSettings() {
        return settings;
    }


}
