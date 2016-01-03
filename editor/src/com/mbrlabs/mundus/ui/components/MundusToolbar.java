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

package com.mbrlabs.mundus.ui.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.UiImages;
import com.mbrlabs.mundus.ui.widgets.FaTextButton;
import com.mbrlabs.mundus.ui.widgets.Toolbar;
import com.mbrlabs.mundus.utils.Fa;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class MundusToolbar extends Toolbar {

    private FaTextButton saveBtn;
    private FaTextButton importBtn;
    private FaTextButton runBtn;
    private FaTextButton exportBtn;

    @Inject
    private ProjectManager projectManager;
    @Inject
    private ProjectContext projectContext;

    public MundusToolbar() {
        super();
        Mundus.inject(this);
        saveBtn = new FaTextButton(Fa.SAVE);
        saveBtn.pad(5).padRight(10);

        importBtn = new FaTextButton(Fa.DOWNLOAD);
        importBtn.pad(5).padRight(10);

        runBtn = new FaTextButton(Fa.PLAY);
        runBtn.pad(5).padRight(10);

        exportBtn = new FaTextButton(Fa.UPLOAD);
        exportBtn.pad(5);

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

    public FaTextButton getSaveBtn() {
        return saveBtn;
    }

    public FaTextButton getImportBtn() {
        return importBtn;
    }

    public FaTextButton getRunBtn() {
        return runBtn;
    }

}
