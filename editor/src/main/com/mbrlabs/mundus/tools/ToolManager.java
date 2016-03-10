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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.history.CommandHistory;
import com.mbrlabs.mundus.input.InputManager;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.tools.brushes.*;
import com.mbrlabs.mundus.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.tools.picker.ToolHandlePicker;

/**
 * @author Marcus Brummer
 * @version 25-12-2015
 */
public class ToolManager extends InputAdapter implements Disposable {

    private static final int KEY_DEACTIVATE = Input.Keys.ESCAPE;

    private Tool activeTool;

    public Array<TerrainBrush> terrainBrushes;

    public ModelPlacementTool modelPlacementTool;
    public SelectionTool selectionTool;
    public TranslateTool translateTool;
    public RotateTool rotateTool;
    public ScaleTool scaleTool;

    private InputManager inputManager;


    public ToolManager(InputManager inputManager, ProjectContext projectContext, GameObjectPicker goPicker,
                       ToolHandlePicker toolHandlePicker, ModelBatch modelBatch, Shaders shaders, ShapeRenderer shapeRenderer,
                       CommandHistory history) {
        this.inputManager = inputManager;
        this.activeTool = null;

        terrainBrushes = new Array<>();
        terrainBrushes.add(new SmoothCircleBrush(projectContext, shaders.wireframeShader, modelBatch, history));
        terrainBrushes.add(new CircleBrush(projectContext, shaders.wireframeShader, modelBatch, history));
        terrainBrushes.add(new StarBrush(projectContext, shaders.wireframeShader, modelBatch, history));
        terrainBrushes.add(new ConfettiBrush(projectContext, shaders.wireframeShader, modelBatch, history));

        modelPlacementTool = new ModelPlacementTool(projectContext, shaders.entityShader, modelBatch, history);
        selectionTool = new SelectionTool(projectContext, goPicker, shaders.wireframeShader, modelBatch, history);
        translateTool = new TranslateTool(projectContext, goPicker, toolHandlePicker, shaders.wireframeShader, modelBatch, history);
        rotateTool = new RotateTool(projectContext, goPicker, toolHandlePicker, shaders.wireframeShader, shapeRenderer, modelBatch, history);
        scaleTool = new ScaleTool(projectContext, goPicker, toolHandlePicker, shaders.wireframeShader, modelBatch, history);
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

    public Tool getActiveTool() {
        return activeTool;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == KEY_DEACTIVATE) {
            if(activeTool != null) {
                activeTool.reset();
            }
            return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        for(TerrainBrush brush : terrainBrushes) {
            brush.dispose();
        }
        translateTool.dispose();
    }

}
