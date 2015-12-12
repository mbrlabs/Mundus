package com.mbrlabs.mundus.core.home;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.core.Files;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.KryoManager;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectRef;
import org.apache.commons.io.FilenameUtils;

import java.util.Date;
import java.util.UUID;

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class HomeManager {

    public HomeData homeData;

    private KryoManager kryoManager;

    public HomeManager(KryoManager kryoManager) {
        this.kryoManager = kryoManager;
        homeData = kryoManager.loadHomeData();
    }

    public void save() {
        kryoManager.saveHomeData(this.homeData);
    }

    public FileHandle createTempModelFolder() {
        String tempFolderPath = FilenameUtils.concat(
                Files.MODEL_CACHE_DIR, UUID.randomUUID().toString()) + "/";
        FileHandle tempFolder = Gdx.files.absolute(tempFolderPath);
        tempFolder.mkdirs();

        return tempFolder;
    }

    public void purgeModelCache() {
        for(FileHandle f : Gdx.files.absolute(Files.MODEL_CACHE_DIR).list()) {
            f.deleteDirectory();
        }
    }

    public ProjectRef createProjectRef(String name, String folder) {
        String path = FilenameUtils.concat(folder, name);
        ProjectRef projectRef = new ProjectRef();
        projectRef.setName(name);
        projectRef.setId(UUID.randomUUID().toString());
        projectRef.setPath(path);
        projectRef.setCreated(new Date());
        projectRef.setLastOpened(new Date());
        homeData.projects.add(projectRef);
        save();

        return projectRef;
    }

    public ProjectRef getLastOpenedProject() {
        if(homeData.lastProject != null) {
            return findProjectById(homeData.lastProject);
        }
        return null;
    }

    public ProjectRef findProjectById(String id) {
        for(ProjectRef projectRef : homeData.projects) {
            if(projectRef.getId().endsWith(id)) {
                return projectRef;
            }
        }
        return null;
    }

}
