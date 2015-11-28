package com.mbrlabs.mundus.ui.handler.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mbrlabs.mundus.ui.Ui;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class OpenProjectHandler extends ChangeListener {

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        Ui.getInstance().showDialog(Ui.getInstance().getOpenProjectDialog());
    }

}
