package com.mbrlabs.mundus.core.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.mbrlabs.mundus.Main;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.data.home.MundusHome;
import com.mbrlabs.mundus.core.data.home.ProjectRef;
import com.mbrlabs.mundus.utils.Callback;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * @author Marcus Brummer
 * @version 25-11-2015
 */
public class ProjectManager {

    public static final String PROJECT_MODEL_DIR = "models/";

    public ProjectRef createProject(String name, String folder) {
        ProjectRef ref = Mundus.home.createProjectRef(name, folder);
        String path = ref.getPath();
        new File(path).mkdirs();
        new File(path, PROJECT_MODEL_DIR).mkdirs();

        return ref;
    }

    public ProjectRef importExistingProject(String path) {
        // TODO
        return null;
    }

    private ProjectContext loadProject(ProjectRef ref) {
        ProjectContext context = new ProjectContext();
        context.setRef(ref);

        return context;
    }

    /**
     * Loads project asynchronously.
     *
     * This method is totally self contained. It does not change global data in
     * {@link com.mbrlabs.mundus.core.Mundus} in any way.
     * The callback should update the global refrences & the UI.
     *
     * @param ref
     * @param callback
     */
    public void loadProject(ProjectRef ref, Callback<ProjectContext> callback) {
        new Thread() {
            @Override
            public void run() {
                ProjectContext context = loadProject(ref);
                Gdx.app.postRunnable(() -> callback.done(context));
            }
        }.start();
    }

    public void changeProject(ProjectContext context) {
        Mundus.projectContext = context;
        Mundus.ui.getSidebar().getEntityTab().reloadData();
        Mundus.ui.getSidebar().getTerrainTab().reloadData();
        Mundus.ui.getSidebar().getModelTab().reloadData();
        Gdx.graphics.setTitle(Mundus.projectContext.getRef().getName() + " - " + Main.TITLE);
    }

    public void saveProject(ProjectContext projectContext) {

    }


}
