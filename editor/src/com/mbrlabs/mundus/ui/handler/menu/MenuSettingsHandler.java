package com.mbrlabs.mundus.ui.handler.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.components.dialogs.SettingsDialog;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class MenuSettingsHandler extends ChangeListener {

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        final Ui ui = Ui.getInstance();
        SettingsDialog dialog = ui.getSettingsDialog();
        dialog.reloadSettings();
        ui.showDialog(dialog);
    }

}