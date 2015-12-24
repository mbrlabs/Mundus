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

package com.mbrlabs.mundus.ui.components.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.*;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.HomeManager;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.core.project.ProjectRef;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.components.RadioButtonGroup;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class OpenProjectDialog extends BaseDialog {

    private VisTextField path;
    private VisTextButton openBtn;
    private RadioButtonGroup<ProjectRef> projectList;

    @Inject
    private HomeManager homeManager;
    @Inject
    private ProjectManager projectManager;

    public OpenProjectDialog() {
        super("Open Project");
        Mundus.inject(this);
        setModal(true);

        VisTable root = new VisTable();
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        projectList = new RadioButtonGroup<>();
        projectList.left();
        ScrollPane scrollPane = new VisScrollPane(projectList);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        root.add(scrollPane).minWidth(350).maxHeight(400).left().row();

        for(ProjectRef project : homeManager.homeDescriptor.projects) {
            String text = project.getName() + " [" + project.getPath() + "]";
            RadioButtonGroup.RadioButton btn = new RadioButtonGroup.RadioButton(text, project);
            projectList.add(btn);
        }

        openBtn = new VisTextButton("Open");
        root.add(openBtn).width(93).height(25).padTop(15);

        setupListeners();
    }

    private void setupListeners() {

        openBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RadioButtonGroup.RadioButton selected = projectList.getButtonGroup().getChecked();
                ProjectRef projectRef = (ProjectRef)selected.getRefObject();
                ProjectContext projectContext = projectManager.loadProject(projectRef);
                close();
                Ui.getInstance().getLoadingProjectDialog().loadProjectAsync(projectContext);
            }
        });

    }



}
