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

package com.mbrlabs.mundus.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.input.InputManager;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.tools.brushes.FlattenBrush;
import com.mbrlabs.mundus.tools.brushes.SphereBrush;

/**
 * @author Marcus Brummer
 * @version 25-12-2015
 */
public class ToolManager extends InputAdapter implements Disposable {

    private static final int KEY_DEACTIVATE = Input.Keys.ESCAPE;

    private Tool activeTool;

    public SphereBrush sphereBrushTool;
    public FlattenBrush flattenBrush;

    public ModelPlacementTool modelPlacementTool;
    public TranslateTool translateTool;
    public SelectionTool selectionTool;


    private ProjectContext projectContext;
    private InputManager inputManager;
    private ModelBatch modelBatch;
    private Shaders shaders;

    public ToolManager(InputManager inputManager, ProjectContext projectContext, ModelBatch modelBatch, Shaders shaders) {
        this.projectContext = projectContext;
        this.inputManager = inputManager;
        this.modelBatch = modelBatch;
        this.activeTool = null;
        this.shaders = shaders;

        sphereBrushTool = new SphereBrush(projectContext, shaders.brushShader, modelBatch);
        flattenBrush = new FlattenBrush(projectContext, shaders.brushShader, modelBatch);

        modelPlacementTool = new ModelPlacementTool(projectContext, shaders.entityShader, modelBatch);
        translateTool = new TranslateTool(projectContext, shaders.brushShader, modelBatch);
        selectionTool = new SelectionTool(projectContext, shaders.brushShader, modelBatch);

        this.inputManager.addProcessor(this);
        setDefaultTool();
    }

    public void activateTool(Tool tool) {
        deactivateTool();
        activeTool = tool;
        inputManager.addProcessor(activeTool);
    }

    public void deactivateTool() {
        if(activeTool != null) {
            activeTool.reset();
            inputManager.removeProcessor(activeTool);
            activeTool = null;
        }
    }

    public void setDefaultTool() {
        deactivateTool();
        activateTool(translateTool);
    }

    public void render() {
        if(activeTool != null) {
            activeTool.render();
        }
    }

    public void act() {
        if(activeTool != null) {
            activeTool.act();
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == KEY_DEACTIVATE) {
            if(activeTool != null) {
                activeTool.reset();
                setDefaultTool();
                activeTool.reset();
            }
            return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        sphereBrushTool.dispose();
        translateTool.dispose();
    }

}
