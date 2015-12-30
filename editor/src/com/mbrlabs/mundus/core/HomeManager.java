/*
 * Copyright (c) 2015. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        if(homeDescriptor.settings.fbxConvBinary == null) {
            homeDescriptor.settings.fbxConvBinary = "";
        }
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
        projectRef.setPath(path);
        homeDescriptor.projects.add(projectRef);
        save();

        return projectRef;
    }

    public ProjectRef getLastOpenedProject() {
        return homeDescriptor.lastProject;
    }

//    public ProjectRef findProjectById(String id) {
//        for(ProjectRef projectRef : homeDescriptor.projects) {
//            if(projectRef.getId().endsWith(id)) {
//                return projectRef;
//            }
//        }
//        return null;
//    }

}
