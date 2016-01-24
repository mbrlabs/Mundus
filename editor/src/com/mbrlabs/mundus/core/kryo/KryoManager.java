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

package com.mbrlabs.mundus.core.kryo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;
import com.mbrlabs.mundus.core.HomeManager;
import com.mbrlabs.mundus.core.Scene;
import com.mbrlabs.mundus.core.kryo.descriptors.*;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import org.apache.commons.io.FilenameUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class KryoManager {

    private Kryo kryo;

    public KryoManager() {
        kryo = new Kryo();
        kryo.setDefaultSerializer(TaggedFieldSerializer.class);
        // !!!!! DO NOT CHANGE THIS, OTHERWISE ALREADY SERIALIZED OBJECTS WILL BE UNREADABLE !!!!
        kryo.register(ArrayList.class, 0);
        kryo.register(Date.class, 1);

        kryo.register(HomeDescriptor.class, 2);
        kryo.register(HomeDescriptor.ProjectRef.class, 3);
        kryo.register(HomeDescriptor.Settings.class, 4);
        kryo.register(ProjectDescriptor.class, 5);
        kryo.register(TerrainDescriptor.class, 6);
        kryo.register(ModelDescriptor.class, 7);
        kryo.register(TextureDescriptor.class, 8);
        kryo.register(FogDescriptor.class, 9);

        kryo.register(SceneDescriptor.class, 10);
        kryo.register(GameObjectDescriptor.class, 11);
        kryo.register(ModelComponentDescriptor.class, 12);
        kryo.register(TerrainComponentDescriptor.class, 13);
    }

    public HomeDescriptor loadHomeDescriptor() {
        try {
            Input input = new Input(new FileInputStream(HomeManager.HOME_DATA_FILE));
            HomeDescriptor homeDescriptor = kryo.readObjectOrNull(input, HomeDescriptor.class);
            if(homeDescriptor == null) {
                homeDescriptor = new HomeDescriptor();
            }
            return homeDescriptor;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return new HomeDescriptor();
    }

    public void saveHomeDescriptor(HomeDescriptor homeDescriptor) {
        try {
            Output output = new Output(new FileOutputStream(HomeManager.HOME_DATA_FILE));
            kryo.writeObject(output, homeDescriptor);
            output.flush();
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveProjectContext(ProjectContext context) {
        try {
            Output output = new Output(new FileOutputStream(context.absolutePath + "/" + context.name + ".pro"));

            ProjectDescriptor descriptor = DescriptorConverter.convert(context);
            kryo.writeObject(output, descriptor);

            output.flush();
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ProjectContext loadProjectContext(HomeDescriptor.ProjectRef ref) throws FileNotFoundException {
        // find .pro file
        System.out.println(ref.getAbsolutePath());
        FileHandle projectFile = null;
        for(FileHandle f : Gdx.files.absolute(ref.getAbsolutePath()).list()) {
            if(f.extension().equals("pro")) {
                projectFile = f;
                break;
            }
        }

        System.out.println(projectFile);

        if(projectFile != null) {
            Input input = new Input(new FileInputStream(projectFile.path()));
            ProjectDescriptor projectDescriptor = kryo.readObjectOrNull(input, ProjectDescriptor.class);
            return DescriptorConverter.convert(projectDescriptor);
        }

        return null;
    }

    public void saveScene(ProjectContext context, Scene scene) {
        try {
            String sceneDir = FilenameUtils.concat(context.absolutePath + "/" + ProjectManager.PROJECT_SCENES_DIR,
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

    public SceneDescriptor loadScene(ProjectContext context, String sceneName) throws FileNotFoundException {
        String sceneDir = FilenameUtils.concat(context.absolutePath + "/" + ProjectManager.PROJECT_SCENES_DIR,
                sceneName + ProjectManager.PROJECT_SCENE_EXTENSION);

        Input input = new Input(new FileInputStream(sceneDir));
        SceneDescriptor sceneDescriptor = kryo.readObjectOrNull(input, SceneDescriptor.class);
        return sceneDescriptor;
    }

}
