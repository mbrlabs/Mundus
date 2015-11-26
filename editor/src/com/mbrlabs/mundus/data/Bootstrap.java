package com.mbrlabs.mundus.data;

import com.mbrlabs.mundus.data.projects.ProjectManager;
import com.mbrlabs.mundus.data.settings.SettingsManager;

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

    public static void bootstrap() {
        createMundusHome();
        loadProjects();
    }

}
