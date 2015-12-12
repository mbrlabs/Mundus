package com.mbrlabs.mundus.ui.components.toolbar;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.ui.UiImages;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class MundusToolbar extends Toolbar {

    private VisImageButton saveBtn;
    private VisImageButton importBtn;
    private VisImageButton runBtn;

    @Inject
    private ProjectManager projectManager;
    @Inject
    private ProjectContext projectContext;

    public MundusToolbar() {
        super();
        Mundus.inject(this);
        saveBtn = new VisImageButton(UiImages.saveIcon);
        saveBtn.pad(7);
        importBtn = new VisImageButton(UiImages.importIcon);
        importBtn.pad(7);
        runBtn = new VisImageButton(UiImages.runIcon);
        runBtn.pad(7);

        addItem(saveBtn);
        addItem(importBtn);
        addItem(runBtn);

        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(projectContext.ref != null) {
                    projectManager.saveProject(projectContext);
                }
            }
        });
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
