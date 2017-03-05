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
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.editor.Mundus;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.GameObjectSelectedEvent;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.editor.utils.Fa;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
public class SelectionTool extends Tool {

    public static final String NAME = "Selection Tool";

    private GameObjectPicker goPicker;

    public SelectionTool(ProjectManager projectManager, GameObjectPicker goPicker, ModelBatch batch,
            CommandHistory history) {
        super(projectManager, batch, history);
        this.goPicker = goPicker;
    }

    public void gameObjectSelected(GameObject selection) {
        getProjectManager().current().currScene.currentSelection = selection;
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
        return Fa.Companion.getMOUSE_POINTER();
    }

    @Override
    public void render() {
        if (getProjectManager().current().currScene.currentSelection != null) {
            getBatch().begin(getProjectManager().current().currScene.cam);
            for (GameObject go : getProjectManager().current().currScene.currentSelection) {
                // model component
                ModelComponent mc = (ModelComponent) go.findComponentByType(Component.Type.MODEL);
                if (mc != null) {
                    getBatch().render(mc.getModelInstance(), getShader());
                }

                // terrainAsset component
                TerrainComponent tc = (TerrainComponent) go.findComponentByType(Component.Type.TERRAIN);
                if (tc != null) {
                    getBatch().render(tc.getTerrain().getTerrain(), getShader());
                }
            }
            getBatch().end();
        }
    }

    @Override
    public void act() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT) {
            GameObject selection = goPicker.pick(getProjectManager().current().currScene, screenX, screenY);
            if (selection != null && !selection.equals(getProjectManager().current().currScene.currentSelection)) {
                gameObjectSelected(selection);
                Mundus.INSTANCE.postEvent(new GameObjectSelectedEvent(selection));
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onActivated() {

    }

    @Override
    public void onDisabled() {
        getProjectManager().current().currScene.currentSelection = null;
    }

}
