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

package com.mbrlabs.mundus.ui.components.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.Scene;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.events.SceneAddedEvent;
import com.mbrlabs.mundus.ui.Ui;

/**
 * @author Marcus Brummer
 * @version 23-12-2015
 */
public class SceneMenu extends Menu implements
        ProjectChangedEvent.ProjectChangedListener,
        SceneAddedEvent.SceneAddedListener {

    @Inject
    private ProjectContext projectContext;
    @Inject
    private ProjectManager projectManager;

    private Array<MenuItem> sceneItems = new Array<>();
    private MenuItem addScene;

    public SceneMenu() {
        super("Scenes");
        Mundus.inject(this);
        Mundus.registerEventListener(this);

        addScene = new MenuItem("Add scene");
        addScene.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                DialogUtils.showInputDialog(Ui.getInstance(), "Add Scene", "Name:", new InputDialogAdapter() {
                    @Override
                    public void finished(String input) {
                        Scene scene = projectManager.createScene(projectContext, input);
                        projectManager.changeScene(scene);
                        Mundus.postEvent(new SceneAddedEvent(scene));
                    }
                });
            }
        });
        addItem(addScene);

        addSeparator();
        buildSceneUi();
    }

    private void buildSceneUi() {
        // remove old items
        for(MenuItem item : sceneItems) {
            removeActor(item);
        }
        // add new items
        for(final Scene scene : projectContext.scenes) {
            buildMenuItem(scene);
        }

    }

    private MenuItem buildMenuItem(final Scene scene) {
        MenuItem menuItem = new MenuItem(scene.getName());
        menuItem.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                projectManager.changeScene(scene);
            }
        });
        addItem(menuItem);
        sceneItems.add(menuItem);

        return menuItem;
    }


    @Override
    public void onProjectChanged(ProjectChangedEvent projectChangedEvent) {
        buildSceneUi();
    }

    @Override
    public void onSceneAdded(SceneAddedEvent sceneAddedEvent) {
        buildMenuItem(sceneAddedEvent.getScene());
    }

}
