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

package com.mbrlabs.mundus.editor.ui.modules.inspector;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.editor.core.Inject;
import com.mbrlabs.mundus.editor.core.Mundus;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.AssetSelectedEvent;
import com.mbrlabs.mundus.editor.events.GameObjectModifiedEvent;
import com.mbrlabs.mundus.editor.events.GameObjectSelectedEvent;
import com.mbrlabs.mundus.editor.ui.Ui;
import com.mbrlabs.mundus.editor.utils.Log;

/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
public class Inspector extends VisTable implements GameObjectSelectedEvent.GameObjectSelectedListener,
        GameObjectModifiedEvent.GameObjectModifiedListener, AssetSelectedEvent.AssetSelectedListener {

    private static final String TAG = Inspector.class.getSimpleName();

    public enum InspectorMode {
        GAME_OBJECT, ASSET, EMPTY
    }

    private InspectorMode mode = InspectorMode.EMPTY;
    private VisTable root;
    private ScrollPane scrollPane;

    private GameObjectInspector goInspector;
    private AssetInspector assetInspector;

    @Inject
    private ProjectManager projectManager;

    public Inspector() {
        super();
        Mundus.inject(this);
        Mundus.registerEventListener(this);

        goInspector = new GameObjectInspector();
        assetInspector = new AssetInspector();

        init();
    }

    public void init() {
        setBackground("window-bg");
        add(new VisLabel("Inspector")).expandX().fillX().pad(3).row();
        addSeparator().row();
        root = new VisTable();
        root.align(Align.top);
        scrollPane = new VisScrollPane(root);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Ui.getInstance().setScrollFocus(scrollPane);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Ui.getInstance().setScrollFocus(null);
            }
        });

        add(scrollPane).expand().fill().top();
    }

    @Override
    public void onGameObjectSelected(GameObjectSelectedEvent event) {
        if (mode != InspectorMode.GAME_OBJECT) {
            mode = InspectorMode.GAME_OBJECT;
            root.clear();
            root.add(goInspector).grow().row();
        }
        goInspector.setGameObject(event.getGameObject());
    }

    @Override
    public void onGameObjectModified(GameObjectModifiedEvent event) {
        goInspector.updateGameObject();
    }

    @Override
    public void onAssetSelected(AssetSelectedEvent event) {
        Log.debug(TAG, event.getAsset().toString());
        if (mode != InspectorMode.ASSET) {
            mode = InspectorMode.ASSET;
            root.clear();
            root.add(assetInspector).grow().row();
        }
        assetInspector.setAsset(event.getAsset());
    }

}
