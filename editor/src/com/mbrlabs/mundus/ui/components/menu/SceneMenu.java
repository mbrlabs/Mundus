package com.mbrlabs.mundus.ui.components.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.Scene;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.EventBus;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.events.Subscribe;
import com.mbrlabs.mundus.model.MModel;

/**
 * @author Marcus Brummer
 * @version 23-12-2015
 */
public class SceneMenu extends Menu {

    @Inject
    private ProjectContext projectContext;
    @Inject
    private ProjectManager projectManager;

    @Inject
    private EventBus eventBus;

    private Array<MenuItem> sceneItems = new Array<>();

    public SceneMenu() {
        super("Scenes");
        Mundus.inject(this);
        eventBus.register(this);

        buildSceneUi();
    }

    @Subscribe
    public void projectChanged(ProjectChangedEvent projectChangedEvent) {
        buildSceneUi();
    }

    private void buildSceneUi() {
        // remove old items
        for(MenuItem item : sceneItems) {
            removeActor(item);
        }
        // add new items
        for(Scene scene : projectContext.scenes) {
            MenuItem menuItem = new MenuItem(scene.getName());
            menuItem.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    projectManager.changeScene(scene);
                }
            });
            addItem(menuItem);
        }
    }




}
