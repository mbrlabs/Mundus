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

package com.mbrlabs.mundus.ui.modules.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.widgets.FileChooserField;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class NewProjectDialog extends BaseDialog {

    private VisTextField projectName;
    private VisTextButton createBtn;
    private FileChooserField locationPath;

    @Inject
    private ProjectManager projectManager;

    public NewProjectDialog() {
        super("Create New Project");
        Mundus.inject(this);
        setModal(true);

        VisTable root = new VisTable();
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        root.add(new VisLabel("Project Name:")).right().padRight(5);
        this.projectName = new VisTextField();
        root.add(this.projectName).height(21).width(300).fillX();
        root.row().padTop(10);
        root.add(new VisLabel("Location:")).right().padRight(5);
        locationPath = new FileChooserField(300);
        locationPath.setFileMode(false);
        root.add(locationPath).row();

        createBtn = new VisTextButton("Create");
        root.add(createBtn).width(93).height(25).colspan(2);

        setupListeners();

    }

    private void setupListeners() {

        createBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = projectName.getText();
                String path = locationPath.getPath();
                if(validateInput(name, path)) {
                    if(!path.endsWith("/")) {
                        path += "/";
                    }
                    ProjectContext projectContext = projectManager.createProject(name, path);
                    close();
                    Ui.getInstance().getLoadingProjectDialog().loadProjectAsync(projectContext);
                }

            }
        });

    }

    private boolean validateInput(String name, String path) {
        return name != null && name.length() > 0 && path != null && path.length() > 0;
    }

}
