package com.mbrlabs.mundus.data;

import com.mbrlabs.mundus.data.projects.ProjectManager;
import com.mbrlabs.mundus.data.settings.SettingsManager;
import com.mbrlabs.mundus.utils.Log;

import java.io.File;

/**
 * @author Marcus Brummer
 * @version 25-11-2015
 */
public class Bootstrap {

    private static void createMundusHome() {
        final String mundusHome = SettingsManager.MUNDUS_HOME;
        new File(mundusHome).mkdirs();
    }

    private static void loadProjects() {
        ProjectManager.getInstance();
    }

    private static void loadSettings() {
        SettingsManager.getInstance();
    }

    private static void initLogging() {
        Log.init();
    }

    public static void bootstrap() {
        createMundusHome();
        initLogging();
        loadProjects();
        loadSettings();
        Log.info("Bootstrap finished");
    }

}
