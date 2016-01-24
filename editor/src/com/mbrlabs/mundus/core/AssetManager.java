/*
 * Copyright (c) 2016. See AUTHORS file.
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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.commons.model.MModel;
import com.mbrlabs.mundus.commons.model.MTexture;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Marcus Brummer
 * @version 24-01-2016
 */
public class AssetManager {

    private ProjectContext projectContext;
    private ProjectManager projectManager;

    public AssetManager(ProjectContext projectContext, ProjectManager projectManager) {
        this.projectContext = projectContext;
        this.projectManager = projectManager;
    }

    /**
     *
     * @param importedModel
     * @return
     */
    public MModel importG3dbModel(ImportManager.ImportedModel importedModel) {
        long id = projectContext.obtainUUID();

        String folder = projectContext.absolutePath + "/" + ProjectManager.PROJECT_MODEL_DIR + id + "/";
        String g3dbFilename = importedModel.name + ".g3db";
        String textureFilename = importedModel.textureFile.name();

        FileHandle absoluteG3dbImportPath = Gdx.files.absolute(folder + g3dbFilename);
        FileHandle absoluteTextureImportPath = Gdx.files.absolute(folder + textureFilename);

        importedModel.g3dbFile.copyTo(absoluteG3dbImportPath);
        importedModel.textureFile.copyTo(absoluteTextureImportPath);

        // load model
        G3dModelLoader loader = new G3dModelLoader(new UBJsonReader());
        Model model = loader.loadModel(absoluteG3dbImportPath);

        // create model
        MModel mModel = new MModel();
        mModel.setModel(model);
        mModel.name = importedModel.name;
        mModel.id = id;
        mModel.g3dbFilename = absoluteG3dbImportPath.name();
        mModel.textureFilename = absoluteTextureImportPath.name();
        projectContext.models.add(mModel);

        // save whole project
        projectManager.saveProject(projectContext);

        return mModel;
    }

    /**
     *
     * @param name
     * @param textureFile
     * @return
     */
    public MTexture importTexture(String name, FileHandle textureFile) {
        long id = projectContext.obtainUUID();

        String absoluteImportPath = FilenameUtils.concat(projectContext.absolutePath, ProjectManager.PROJECT_TEXTURE_DIR + textureFile.name());
        FileHandle absoluteImportFile = Gdx.files.absolute(absoluteImportPath);

        textureFile.copyTo(absoluteImportFile);

        MTexture tex = new MTexture();
        tex.setId(id);
        tex.setFilename(absoluteImportFile.name());
        tex.texture = new Texture(absoluteImportFile);

        projectContext.textures.add(tex);

        // save whole project
        projectManager.saveProject(projectContext);

        return tex;
    }

}
