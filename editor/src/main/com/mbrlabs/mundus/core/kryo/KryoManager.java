/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.core.kryo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.core.kryo.descriptors.BaseLightDescriptor;
import com.mbrlabs.mundus.core.kryo.descriptors.FogDescriptor;
import com.mbrlabs.mundus.core.kryo.descriptors.GameObjectDescriptor;
import com.mbrlabs.mundus.core.kryo.descriptors.ModelComponentDescriptor;
import com.mbrlabs.mundus.core.kryo.descriptors.ModelDescriptor;
import com.mbrlabs.mundus.core.kryo.descriptors.ProjectDescriptor;
import com.mbrlabs.mundus.core.kryo.descriptors.ProjectRefDescriptor;
import com.mbrlabs.mundus.core.kryo.descriptors.RegistryDescriptor;
import com.mbrlabs.mundus.core.kryo.descriptors.SceneDescriptor;
import com.mbrlabs.mundus.core.kryo.descriptors.SettingsDescriptor;
import com.mbrlabs.mundus.core.kryo.descriptors.TerrainComponentDescriptor;
import com.mbrlabs.mundus.core.kryo.descriptors.TerrainDescriptor;
import com.mbrlabs.mundus.core.kryo.descriptors.TerrainTextureDescriptor;
import com.mbrlabs.mundus.core.kryo.descriptors.TextureDescriptor;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.core.registry.KeyboardLayout;
import com.mbrlabs.mundus.core.registry.ProjectRef;
import com.mbrlabs.mundus.core.registry.Registry;

import org.apache.commons.io.FilenameUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * Manages descriptor object <-> file io.
 *
 * This provides only method for loading the serialized data into POJOs. It does
 * not load or initialize any data (like for example it does not load meshes or
 * textures). This has to be done separately (ProjectManager).
 *
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class KryoManager {

    private Kryo kryo;

    public KryoManager() {
        // setup kryo
        kryo = new Kryo();
        kryo.setDefaultSerializer(TaggedFieldSerializer.class);
        kryo.getTaggedFieldSerializerConfig().setOptimizedGenerics(true);

        // !!!!! DO NOT CHANGE THIS, OTHERWISE ALREADY SERIALIZED OBJECTS WILL
        // BE UNREADABLE !!!!

        // core stuff
        kryo.register(ArrayList.class, 0);
        kryo.register(Date.class, 1);
        kryo.register(RegistryDescriptor.class, 2);
        kryo.register(ProjectRefDescriptor.class, 3);
        kryo.register(SettingsDescriptor.class, 4);
        kryo.register(KeyboardLayout.class, 5);
        kryo.register(ProjectDescriptor.class, 6);
        kryo.register(SceneDescriptor.class, 7);

        // basic building blocks
        kryo.register(TerrainDescriptor.class, 8);
        kryo.register(ModelDescriptor.class, 9);
        kryo.register(TextureDescriptor.class, 10);
        kryo.register(FogDescriptor.class, 11);
        kryo.register(GameObjectDescriptor.class, 12);
        kryo.register(BaseLightDescriptor.class, 13);

        // components
        kryo.register(ModelComponentDescriptor.class, 14);
        kryo.register(TerrainComponentDescriptor.class, 15);
        kryo.register(TerrainTextureDescriptor.class, 16);
    }

    /**
     * Loads the registry.
     *
     * Save to use afterwards, nothing else needs to be loaded.
     *
     * @return mundus registry
     */
    public Registry loadRegistry() {
        try {
            Input input = new Input(new FileInputStream(Registry.HOME_DATA_FILE));
            RegistryDescriptor registryDescriptor = kryo.readObjectOrNull(input, RegistryDescriptor.class);
            if (registryDescriptor == null) {
                registryDescriptor = new RegistryDescriptor();
            }
            return DescriptorConverter.convert(registryDescriptor);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return new Registry();
    }

    /**
     * Saves the registry
     *
     * @param registry
     *            mundus registry
     */
    public void saveRegistry(Registry registry) {
        try {
            Output output = new Output(new FileOutputStream(Registry.HOME_DATA_FILE));
            RegistryDescriptor descriptor = DescriptorConverter.convert(registry);
            kryo.writeObject(output, descriptor);
            output.flush();
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the project context.
     *
     * Saves only the project's .pro file, not the individual scenes.
     *
     * @param context
     *            project context to save
     */
    public void saveProjectContext(ProjectContext context) {
        try {
            Output output = new Output(new FileOutputStream(context.path + "/" + context.name + ".pro"));

            ProjectDescriptor descriptor = DescriptorConverter.convert(context);
            kryo.writeObject(output, descriptor);

            output.flush();
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the project context .pro.
     *
     * Does however not load the scenes (only the scene names as reference) or
     * meshes/textures (see ProjectManager).
     *
     * @param ref
     *            project to load
     * @return loaded project context without scenes
     * @throws FileNotFoundException
     */
    public ProjectContext loadProjectContext(ProjectRef ref) throws FileNotFoundException {
        // find .pro file
        FileHandle projectFile = null;
        for (FileHandle f : Gdx.files.absolute(ref.getPath()).list()) {
            if (f.extension().equals("pro")) {
                projectFile = f;
                break;
            }
        }

        if (projectFile != null) {
            Input input = new Input(new FileInputStream(projectFile.path()));
            ProjectDescriptor projectDescriptor = kryo.readObjectOrNull(input, ProjectDescriptor.class);
            ProjectContext context = DescriptorConverter.convert(projectDescriptor);
            context.activeSceneName = projectDescriptor.getCurrentSceneName();
            return context;
        }

        return null;
    }

    /**
     * Saves a scene.
     *
     * @param context
     *            project context of the scene
     * @param scene
     *            scene to save
     */
    public void saveScene(ProjectContext context, Scene scene) {
        try {
            String sceneDir = FilenameUtils.concat(context.path + "/" + ProjectManager.PROJECT_SCENES_DIR,
                    scene.getName() + ProjectManager.PROJECT_SCENE_EXTENSION);

            Output output = new Output(new FileOutputStream(sceneDir));

            SceneDescriptor descriptor = DescriptorConverter.convert(scene);
            kryo.writeObject(output, descriptor);

            output.flush();
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a scene.
     *
     * Does however not initialize ModelInstances, Terrains, ... ->
     * ProjectManager
     *
     * @param context
     *            project context of the scene
     * @param sceneName
     *            name of the scene to load
     * @return loaded scene
     * @throws FileNotFoundException
     */
    public SceneDescriptor loadScene(ProjectContext context, String sceneName) throws FileNotFoundException {
        String sceneDir = FilenameUtils.concat(context.path + "/" + ProjectManager.PROJECT_SCENES_DIR,
                sceneName + ProjectManager.PROJECT_SCENE_EXTENSION);

        Input input = new Input(new FileInputStream(sceneDir));
        SceneDescriptor sceneDescriptor = kryo.readObjectOrNull(input, SceneDescriptor.class);
        return sceneDescriptor;
    }

}
