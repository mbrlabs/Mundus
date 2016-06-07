package com.mbrlabs.mundus.utils;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.toast.Toast;

/**
 * Displays Android-like toasts at top right corner of the screen.
 *
 * @author Marcus Brummer
 * @version 07-06-2016
 */
public class Toaster {

    private ToastManager toastManager;

    public Toaster(Stage stage) {
        this.toastManager = new ToastManager(stage);
    }

    /**
     * Amber background.
     * @param msg
     */
    public void info(String msg) {
        final VisTable table = newTable(msg);
        final Toast toast = new Toast("info", table);
        toastManager.show(toast, 3);
    }

    /**
     * Red background
     * @param msg
     */
    public void error(String msg) {
        final VisTable table = newTable(msg);
        final Toast toast = new Toast("error", table);
        toastManager.show(toast, 5);
    }

    /**
     * Teal background.
     * @param msg
     */
    public void success(String msg) {
        final VisTable table = newTable(msg);
        final Toast toast = new Toast("success", table);
        toastManager.show(toast, 3);
    }

    private VisTable newTable(String text) {
        final VisTable table = new VisTable();
        table.add(new VisLabel(text));
        table.pad(5);
        return table;
    }

}
