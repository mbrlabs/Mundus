package com.mbrlabs.mundus.ui.handler.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.components.dialogs.AddTerrainDialog;

/**
 * @author Marcus Brummer
 * @version 01-12-2015
 */
public class AddTerrainHandler extends ChangeListener {

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        final Ui ui = Ui.getInstance();
        AddTerrainDialog dialog = ui.getAddTerrainDialog();
        ui.showDialog(dialog);
    }

}
