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

package com.mbrlabs.mundus.ui.components.toolbar;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.UiImages;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class MundusToolbar extends Toolbar {

    private VisImageButton saveBtn;
    private VisImageButton importBtn;
    private VisImageButton runBtn;
    private VisImageButton exportBtn;

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

        exportBtn = new VisImageButton(UiImages.exportIcon);
        exportBtn.pad(7);

        addItem(saveBtn);
        addItem(importBtn);
        addItem(exportBtn);
        addItem(runBtn);

        // save btn
        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(projectContext.loaded) {
                    projectManager.saveProject(projectContext);
                }
            }
        });

        // export btn
        exportBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final Ui ui = Ui.getInstance();
                ui.showDialog(ui.getExportDialog());
            }
        });

        // import btn
        importBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final Ui ui = Ui.getInstance();
                ui.showDialog(ui.getImportModelDialog());
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
