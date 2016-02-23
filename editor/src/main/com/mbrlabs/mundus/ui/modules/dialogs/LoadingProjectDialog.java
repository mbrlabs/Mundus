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

package com.mbrlabs.mundus.ui.modules.dialogs;

import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.ui.Ui;

import java.io.File;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class LoadingProjectDialog extends VisDialog {

    private VisLabel projectName;
    @Inject
    private ProjectManager projectManager;

    public LoadingProjectDialog() {
        super("Loading Project");
        Mundus.inject(this);
        setModal(true);
        setMovable(false);

        projectName = new VisLabel("Project Folder:");

        VisTable root = new VisTable();
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        root.add(projectName).right().padRight(5);
    }


    public void loadProjectAsync(ProjectContext projectContext) {
        this.projectName.setText("Loading project: " + projectContext.name);
        Ui.getInstance().showDialog(this);


        if(new File(projectContext.absolutePath).exists()) {
            projectManager.changeProject(projectContext);
            close();
        } else {
            close();
            Dialogs.showErrorDialog(Ui.getInstance(), "Faild to load project " + projectContext.absolutePath);
        }

    }


}
