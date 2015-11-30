package com.mbrlabs.mundus.ui.components.toolbar;

import com.kotcrab.vis.ui.widget.VisImageButton;
import com.mbrlabs.mundus.ui.UiImages;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class MundusToolbar extends Toolbar {

    private VisImageButton saveBtn;
    private VisImageButton importBtn;
    private VisImageButton runBtn;

    public MundusToolbar() {
        super();
        saveBtn = new VisImageButton(UiImages.saveIcon);
        saveBtn.pad(7);
        importBtn = new VisImageButton(UiImages.importIcon);
        importBtn.pad(7);
        runBtn = new VisImageButton(UiImages.runIcon);
        runBtn.pad(7);

        addItem(saveBtn);
        addItem(importBtn);
        addItem(runBtn);
    }

    public VisImageButton getSaveBtn() {
        return saveBtn;
    }

    public VisImageButton getImportBtn() {
        return importBtn;
    }

    public VisImageButton getRunBtn() {
        return runBtn;
    }

}
