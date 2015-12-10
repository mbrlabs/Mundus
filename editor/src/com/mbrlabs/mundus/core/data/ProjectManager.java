package com.mbrlabs.mundus.core.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.mbrlabs.mundus.Main;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.data.home.MundusHome;
import com.mbrlabs.mundus.core.data.home.ProjectRef;
import com.mbrlabs.mundus.terrain.Terrain;
import com.mbrlabs.mundus.terrain.TerrainIO;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.utils.Callback;
import com.mbrlabs.mundus.utils.Log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * @author Marcus Brummer
 * @version 25-11-2015
 */
public class ProjectManager {

    public static final String PROJECT_MODEL_DIR = "models/";
    public static final String PROJECT_TERRAIN_DIR = "terrains/";

    private ProjectContext projectContext;
    private MundusHome home;

    public ProjectManager(ProjectContext projectContext, MundusHome home) {
        this.projectContext = projectContext;
        this.home = home;
    }

    public ProjectRef createProject(String name, String folder) {
        ProjectRef ref = home.createProjectRef(name, folder);
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
        context.setRef(ref);
        context.terrains.add(TerrainIO.importBinary(FilenameUtils.concat(ref.getPath(), ProjectManager.PROJECT_TERRAIN_DIR) + "test.ter"));

        return context;
    }

    public void loadProject(ProjectRef ref, Callback<ProjectContext> callback) {
        new Thread() {
            @Override
            public void run() {
                ProjectContext context = loadProject(ref);
                if(new File(context.getRef().getPath()).exists()) {
                    Gdx.app.postRunnable(() -> callback.done(context));
                } else {
                    Gdx.app.postRunnable(() -> callback.error("Project " + context.getRef().getPath() + " not found."));
                }
            }
        }.run(); // FIXME run() is intended because of openGL context...either remove thread or find a way to run it async
    }

    public void changeProject(ProjectContext context) {
        projectContext.dispose();
        projectContext.copyFrom(context);
        Ui.getInstance().getSidebar().getEntityTab().reloadData();
        Ui.getInstance().getSidebar().getTerrainTab().reloadData();
        Ui.getInstance().getSidebar().getModelTab().reloadData();
        Gdx.graphics.setTitle(projectContext.getRef().getName() + " - " + Main.TITLE);
    }

    public void saveProject(ProjectContext projectContext) {
        Log.debug("Saving project " + projectContext.getRef().getName() + " [" + projectContext.getRef().getPath() + "]");

        for(Terrain t : projectContext.terrains) {
            TerrainIO.exportBinary(t, FilenameUtils.concat(projectContext.getRef().getPath(), ProjectManager.PROJECT_TERRAIN_DIR) + "test.ter");
        }
    }


}
