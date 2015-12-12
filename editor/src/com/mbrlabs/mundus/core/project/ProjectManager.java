package com.mbrlabs.mundus.core.project;

import com.badlogic.gdx.Gdx;
import com.mbrlabs.mundus.Main;
import com.mbrlabs.mundus.core.Files;
import com.mbrlabs.mundus.core.home.HomeData;
import com.mbrlabs.mundus.core.home.HomeManager;
import com.mbrlabs.mundus.terrain.Terrain;
import com.mbrlabs.mundus.terrain.TerrainIO;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.utils.Callback;
import com.mbrlabs.mundus.utils.Log;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @author Marcus Brummer
 * @version 25-11-2015
 */
public class ProjectManager {

    public static final String PROJECT_MODEL_DIR = "models/";
    public static final String PROJECT_TERRAIN_DIR = "terrains/";

    private ProjectContext projectContext;
    private HomeManager homeManager;

    public ProjectManager(ProjectContext projectContext, HomeManager homeManager) {
        this.projectContext = projectContext;
        this.homeManager = homeManager;
    }

    public ProjectRef createProject(String name, String folder) {
        ProjectRef ref = homeManager.createProjectRef(name, folder);
        String path = ref.getPath();
        new File(path).mkdirs();
        new File(path, PROJECT_MODEL_DIR).mkdirs();
        new File(path, PROJECT_TERRAIN_DIR).mkdirs();

        return ref;
    }

    public ProjectRef importExistingProject(String path) {
        // TODO
        return null;
    }

    private ProjectContext loadProject(ProjectRef ref) {
        ProjectContext context = new ProjectContext();
        context.ref = ref;

        return context;
    }

    public void loadProject(ProjectRef ref, Callback<ProjectContext> callback) {
        new Thread() {
            @Override
            public void run() {
                ProjectContext context = loadProject(ref);
                if(new File(context.ref.getPath()).exists()) {
                    Gdx.app.postRunnable(() -> callback.done(context));
                } else {
                    Gdx.app.postRunnable(() -> callback.error("Project " + context.ref.getPath() + " not found."));
                }
            }
        }.run(); // FIXME run() is intended because of openGL context...either remove thread or find a way to run it async
    }

    public void changeProject(ProjectContext context) {
        homeManager.homeData.lastProject = context.ref.getId();
        homeManager.save();
        projectContext.dispose();
        projectContext.copyFrom(context);
        // TODO send notification to event bus for ui updates
        Gdx.graphics.setTitle(projectContext.ref.getName() + " - " + Main.TITLE);
    }

    public void saveProject(ProjectContext projectContext) {
        // TODO save

        // save terrains
        for(Terrain terrain : projectContext.terrains) {
            String path = FilenameUtils.concat(projectContext.ref.getPath(), ProjectManager.PROJECT_TERRAIN_DIR);
            path += terrain.getName() + "-" + terrain.getId() + "." + TerrainIO.FILE_EXTENSION;
            TerrainIO.exportBinary(terrain, path);
        }

        Log.debug("Saving project " + projectContext.ref.getName() + " [" + projectContext.ref.getPath() + "]");
    }


}
