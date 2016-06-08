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

package com.mbrlabs.mundus.ui.modules.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
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
    private ProjectManager projectManager;

    private Array<MenuItem> sceneItems = new Array<>();
    private MenuItem addScene;

    public SceneMenu() {
        super("Scenes");
        Mundus.inject(this);
        Mundus.registerEventListener(this);

        final ProjectContext projectContext = projectManager.current();

        addScene = new MenuItem("Add scene");
        addScene.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialogs.showInputDialog(Ui.getInstance(), "Add Scene", "Name:", new InputDialogAdapter() {
                    @Override
                    public void finished(String input) {
                        Scene scene = projectManager.createScene(projectContext, input);
                        projectManager.changeScene(projectContext, scene.getName());
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
        for(final String scene : projectManager.current().scenes) {
            buildMenuItem(scene);
        }

    }

    private MenuItem buildMenuItem(final String sceneName) {
        MenuItem menuItem = new MenuItem(sceneName);
        menuItem.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                projectManager.changeScene(projectManager.current(), sceneName);
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
        buildMenuItem(sceneAddedEvent.getScene().getName());
    }

}
