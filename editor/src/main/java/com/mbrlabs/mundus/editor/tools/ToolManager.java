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
package com.mbrlabs.mundus.editor.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.input.InputManager;
import com.mbrlabs.mundus.editor.tools.brushes.CircleBrush;
import com.mbrlabs.mundus.editor.tools.brushes.ConfettiBrush;
import com.mbrlabs.mundus.editor.tools.brushes.SmoothCircleBrush;
import com.mbrlabs.mundus.editor.tools.brushes.StarBrush;
import com.mbrlabs.mundus.editor.tools.brushes.TerrainBrush;
import com.mbrlabs.mundus.editor.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.editor.tools.picker.ToolHandlePicker;

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

    private ProjectManager projectManager;

    public ToolManager(InputManager inputManager, ProjectManager projectManager, GameObjectPicker goPicker,
            ToolHandlePicker toolHandlePicker, ModelBatch modelBatch, ShapeRenderer shapeRenderer,
            CommandHistory history) {
        this.inputManager = inputManager;
        this.projectManager = projectManager;
        this.activeTool = null;

        terrainBrushes = new Array<>();
        terrainBrushes.add(new SmoothCircleBrush(projectManager, modelBatch, history));
        terrainBrushes.add(new CircleBrush(projectManager, modelBatch, history));
        terrainBrushes.add(new StarBrush(projectManager, modelBatch, history));
        terrainBrushes.add(new ConfettiBrush(projectManager, modelBatch, history));

        modelPlacementTool = new ModelPlacementTool(projectManager, modelBatch, history);
        selectionTool = new SelectionTool(projectManager, goPicker, modelBatch, history);
        translateTool = new TranslateTool(projectManager, goPicker, toolHandlePicker, modelBatch, history);
        rotateTool = new RotateTool(projectManager, goPicker, toolHandlePicker, shapeRenderer, modelBatch, history);
        scaleTool = new ScaleTool(projectManager, goPicker, toolHandlePicker, shapeRenderer, modelBatch, history);
    }

    public void activateTool(Tool tool) {
        deactivateTool();
        activeTool = tool;
        inputManager.addProcessor(activeTool);
    }

    public void deactivateTool() {
        if (activeTool != null) {
            activeTool.reset();
            inputManager.removeProcessor(activeTool);
            activeTool = null;
        }
    }

    public void setDefaultTool() {
        if (activeTool == null || activeTool == modelPlacementTool || activeTool instanceof TerrainBrush)
            activateTool(translateTool);
        else
            activeTool.reset();

    }

    public void render() {
        if (activeTool != null) {
            activeTool.render();
        }
    }

    public void act() {
        if (activeTool != null) {
            activeTool.act();
        }
    }

    public Tool getActiveTool() {
        return activeTool;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == KEY_DEACTIVATE) {
            if (activeTool != null) {
                activeTool.reset();
            }
            setDefaultTool();
            return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        for (TerrainBrush brush : terrainBrushes) {
            brush.dispose();
        }
        translateTool.dispose();
        modelPlacementTool.dispose();
        selectionTool.dispose();
        rotateTool.dispose();
        scaleTool.dispose();
    }

}
