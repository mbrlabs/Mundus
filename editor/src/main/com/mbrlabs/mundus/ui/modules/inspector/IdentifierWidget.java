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

package com.mbrlabs.mundus.ui.modules.inspector;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;

/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
public class IdentifierWidget extends VisTable {

    private VisCheckBox active;
    private VisTextField name;
    private VisTextField tag;

    @Inject
    private ProjectManager projectManager;

    public IdentifierWidget() {
        super();
        Mundus.inject(this);
        init();
        setupUI();
        setupListeners();
    }

    private void init() {
        active = new VisCheckBox("", true);
        name = new VisTextField("Name");
        tag = new VisTextField("Untagged");
    }

    private void setupUI() {
        add(active).padBottom(4).left().top();
        add(name).padBottom(4).left().top().expandX().fillX().row();
        add(new VisLabel("Tag: ")).left().top();
        add(tag).top().left().expandX().fillX().row();
    }

    private void setupListeners() {
        final ProjectContext projectContext = projectManager.current();

        active.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(projectContext.currScene.currentSelection == null) return;
                projectContext.currScene.currentSelection.active = active.isChecked();
            }
        });

        name.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(projectContext.currScene.currentSelection == null) return;
                projectContext.currScene.currentSelection.name = name.getText();
            }
        });

    }

    public void setValues(GameObject go) {
        active.setChecked(go.active);
        name.setText(go.name);
    }

}
