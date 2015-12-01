package com.mbrlabs.mundus.data;

import com.badlogic.gdx.Gdx;
import com.mbrlabs.mundus.data.home.MundusHome;
import com.mbrlabs.mundus.data.home.ProjectRef;
import com.mbrlabs.mundus.utils.Callback;

import java.io.File;

/**
 * @author Marcus Brummer
 * @version 25-11-2015
 */
public class ProjectManager {

    public static final String PROJECT_MODEL_DIR = "models/";

    public static void createProject(String name, String folder) {
        ProjectRef ref = MundusHome.getInstance().createProjectRef(name, folder);

        String path = ref.getPath();
        new File(path).mkdirs();
        new File(path, PROJECT_MODEL_DIR).mkdirs();

    }

    public static ProjectContext loadProject(ProjectRef projectRef) {
        ProjectContext context = new ProjectContext();
        context.setRef(projectRef);

        // TODO load project heightData

        return context;
    }

    public static void loadProject(ProjectRef ref, Callback<ProjectContext> callback) {
        new Thread() {
            @Override
            public void run() {
                ProjectContext context = loadProject(ref);
                Gdx.app.postRunnable(() -> callback.done(context));
            }
        }.start();
    }


}
