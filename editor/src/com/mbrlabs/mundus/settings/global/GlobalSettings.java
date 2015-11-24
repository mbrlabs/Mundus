package com.mbrlabs.mundus.settings.global;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mbrlabs.mundus.settings.Settings;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class GlobalSettings {

    private static GlobalSettings INSTANCE;

    private Json json;
    private GlobalSettingsData data;

    private GlobalSettings() {
        json = new Json();
        load();
    }

    public static GlobalSettings getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new GlobalSettings();
        }
        return INSTANCE;
    }

    public void load() {
        FileHandle settingsFile = Gdx.files.absolute(Settings.SETTINGS_GLOBAL);
        if(settingsFile.exists()) {
            data = json.fromJson(GlobalSettingsData.class, settingsFile);
        } else {
            data = new GlobalSettingsData();
        }
    }

    public void save() {
        FileHandle settingsFile = Gdx.files.absolute(Settings.SETTINGS_GLOBAL);
        json.toJson(data, settingsFile);
    }

    public GlobalSettingsData getData() {
        return data;
    }
}
