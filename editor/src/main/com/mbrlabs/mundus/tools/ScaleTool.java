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

package com.mbrlabs.mundus.tools;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.history.CommandHistory;
import com.mbrlabs.mundus.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.tools.picker.ToolHandlePicker;
import com.mbrlabs.mundus.utils.Fa;

/**
 * @author Marcus Brummer
 * @version 19-02-2016
 */
public class ScaleTool extends TransformTool {

    public static final String NAME = "Scale Tool";

    public ScaleTool(ProjectContext projectContext, GameObjectPicker goPicker, ToolHandlePicker handlePicker, Shader shader,
                     ModelBatch batch, CommandHistory history) {
        super(projectContext, goPicker, handlePicker, shader, batch, history);
    }

    @Override
    protected void scaleHandles() {

    }

    @Override
    protected void translateHandles() {

    }

    @Override
    protected void rotateHandles() {

    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Drawable getIcon() {
        return null;
    }

    @Override
    public String getIconFont() {
        return Fa.EXPAND;
    }

}
