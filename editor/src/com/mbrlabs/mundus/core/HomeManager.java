package com.mbrlabs.mundus.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.core.kryo.KryoManager;
import com.mbrlabs.mundus.core.kryo.descriptors.HomeDescriptor;
import com.mbrlabs.mundus.core.project.ProjectRef;
import org.apache.commons.io.FilenameUtils;

import java.util.Date;
import java.util.UUID;

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class HomeManager {

    public HomeDescriptor homeDescriptor;

    private KryoManager kryoManager;

    public HomeManager(KryoManager kryoManager) {
        this.kryoManager = kryoManager;
        homeDescriptor = kryoManager.loadHomeDescriptor();
    }

    public void save() {
        kryoManager.saveHomeDescriptor(this.homeDescriptor);
    }

    public FileHandle createTempFolder() {
        String tempFolderPath = FilenameUtils.concat(
                Files.TEMP_DIR, UUID.randomUUID().toString()) + "/";
        FileHandle tempFolder = Gdx.files.absolute(tempFolderPath);
        tempFolder.mkdirs();

        return tempFolder;
    }

    public void purgeModelCache() {
        for(FileHandle f : Gdx.files.absolute(Files.TEMP_DIR).list()) {
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
        homeDescriptor.projects.add(projectRef);
        save();

        return projectRef;
    }

    public ProjectRef getLastOpenedProject() {
        if(homeDescriptor.lastProject != null) {
            return findProjectById(homeDescriptor.lastProject);
        }
        return null;
    }

    public ProjectRef findProjectById(String id) {
        for(ProjectRef projectRef : homeDescriptor.projects) {
            if(projectRef.getId().endsWith(id)) {
                return projectRef;
            }
        }
        return null;
    }

}
