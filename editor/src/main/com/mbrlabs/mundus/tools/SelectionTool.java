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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mbrlabs.mundus.Editor;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.events.GameObjectSelectedEvent;
import com.mbrlabs.mundus.history.CommandHistory;
import com.mbrlabs.mundus.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.utils.Fa;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
public class SelectionTool extends Tool {

    public static final String NAME = "Selection Tool";
    private Drawable icon;

    private GameObjectPicker goPicker;

    public SelectionTool(ProjectContext projectContext, GameObjectPicker goPicker, Shader shader, ModelBatch batch, CommandHistory history) {
        super(projectContext, shader, batch, history);
        icon = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/selectionTool.png"))));
        this.goPicker = goPicker;
    }

    public void gameObjectSelected(GameObject selection) {
        projectContext.currScene.currentSelection = selection;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Drawable getIcon() {
        return icon;
    }

    @Override
    public String getIconFont() {
        return Fa.MOUSE_POINTER;
    }

    @Override
    public void reset() {
        projectContext.currScene.currentSelection = null;
    }

    @Override
    public void render() {
        if(projectContext.currScene.currentSelection != null) {
            batch.begin(projectContext.currScene.cam);
            for(GameObject go : projectContext.currScene.currentSelection) {
                ModelComponent mc = (ModelComponent) go.findComponentByType(Component.Type.MODEL);
                if(mc != null) {
                    batch.render(mc.getModelInstance().modelInstance, shader);
                }
            }
            batch.end();
        }
    }

    @Override
    public void act() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(button == Input.Buttons.RIGHT) {
            GameObject selection = goPicker.pick(projectContext.currScene, screenX, screenY);
            if(selection != null && !selection.equals(projectContext.currScene.currentSelection)) {
                gameObjectSelected(selection);
                Mundus.postEvent(new GameObjectSelectedEvent(selection));
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

}
