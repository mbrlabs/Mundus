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

package com.mbrlabs.mundus.core.registry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.core.kryo.DescriptorConverter;
import com.mbrlabs.mundus.core.kryo.KryoManager;
import com.mbrlabs.mundus.core.kryo.descriptors.RegistryDescriptor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Manages global settings.
 *
 * Files are stored in ~/.mundus/
 *
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class Registry {

    public static final String HOME_DIR = FilenameUtils.concat(FileUtils.getUserDirectoryPath(), ".mundus/");
    public static final String LOGS_DIR = FilenameUtils.concat(HOME_DIR, "logs/");
    public static final String TEMP_DIR = FilenameUtils.concat(HOME_DIR, "temp/");
    public static final String HOME_DATA_FILE = FilenameUtils.concat(HOME_DIR, "mundus.registry");

    private List<ProjectRef> projects;
    private ProjectRef lastProject;
    private Settings settings;

    private KryoManager kryoManager;

    public Registry() {
        projects = new ArrayList<>();
        settings = new Settings();


        // fbx conv
//        if(registryDescriptor.settingsDescriptor.fbxConvBinary == null) {
//            registryDescriptor.settingsDescriptor.fbxConvBinary = "";
//        }
//
//        // default locale / keyboard layout
//        if(registryDescriptor.settingsDescriptor.keyboardLayout == null) {
//            if(Locale.getDefault().equals(Locale.GERMAN) || Locale.getDefault().equals(Locale.GERMANY)) {
//                registryDescriptor.settingsDescriptor.keyboardLayout = RegistryDescriptor.KeyboardLayout.QWERTZ;
//            } else {
//                registryDescriptor.settingsDescriptor.keyboardLayout = RegistryDescriptor.KeyboardLayout.QWERTY;
//            }
//        }
    }

    public FileHandle createTempFolder() {
        String tempFolderPath = FilenameUtils.concat(
                TEMP_DIR, UUID.randomUUID().toString()) + "/";
        FileHandle tempFolder = Gdx.files.absolute(tempFolderPath);
        tempFolder.mkdirs();

        return tempFolder;
    }

    public void purgeTempDirectory() {
        for(FileHandle f : Gdx.files.absolute(TEMP_DIR).list()) {
            f.deleteDirectory();
        }
    }

    public ProjectRef createProjectRef(String name, String folder) {
        ProjectRef projectRef = new ProjectRef();
        projectRef.setName(name);
        projectRef.setPath(FilenameUtils.concat(folder, name));
        projects.add(projectRef);

        return projectRef;
    }

    public List<ProjectRef> getProjects() {
        return projects;
    }

    public Settings getSettings() {
        return settings;
    }

    public ProjectRef getLastOpenedProject() {
        return lastProject;
    }

    public ProjectRef getLastProject() {
        return lastProject;
    }

    public void setLastProject(ProjectRef lastProject) {
        this.lastProject = lastProject;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
