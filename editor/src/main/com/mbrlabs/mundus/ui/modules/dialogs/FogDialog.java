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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.commons.env.Fog;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.events.SceneChangedEvent;
import com.mbrlabs.mundus.ui.widgets.ColorPickerField;

/**
 * @author Marcus Brummer
 * @version 06-01-2016
 */
public class FogDialog extends BaseDialog implements ProjectChangedEvent.ProjectChangedListener, SceneChangedEvent.SceneChangedListener {

    private VisCheckBox useFog = new VisCheckBox("Use fog");
    private VisTextField density = new VisTextField("0");
    private VisTextField gradient = new VisTextField("0");

    private ColorPickerField colorPickerField = new ColorPickerField("Color: ");

    @Inject
    private ProjectManager projectManager;

    public FogDialog() {
        super("Fog");
        Mundus.inject(this);
        Mundus.registerEventListener(this);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        Table root = new Table();
        //root.debugAll();
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        root.add(useFog).left().padBottom(10).colspan(2).row();
        root.add(new VisLabel("Density: ")).left().padBottom(10);
        root.add(density).fillX().expandX().padBottom(10).row();
        root.add(new VisLabel("Gradient: ")).left().padBottom(10);
        root.add(gradient).fillX().expandX().padBottom(10).row();
        root.add(colorPickerField).left().fillX().expandX().colspan(2).row();
        resetValues();
    }

    private void setupListeners() {
        final ProjectContext projectContext = projectManager.current();

        // use fog checkbox
        useFog.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(useFog.isChecked()) {
                    if(projectContext.currScene.environment.getFog() == null) {
                        Fog fog = new Fog();
                        projectContext.currScene.environment.setFog(fog);
                        density.setText(String.valueOf(fog.density));
                        gradient.setText(String.valueOf(fog.gradient));
                    }
                    density.setDisabled(false);
                    gradient.setDisabled(false);
                    colorPickerField.setDisabled(false);
                } else {
                    projectContext.currScene.environment.setFog(null);
                    density.setDisabled(true);
                    gradient.setDisabled(true);
                    colorPickerField.setDisabled(true);
                }
            }
        });

        // gradient
        gradient.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Float g = convert(gradient.getText());
                if(g != null) {
                    projectContext.currScene.environment.getFog().gradient = g;
                }
            }
        });

        // density
        density.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Float d = convert(density.getText());
                if(d != null) {
                    projectContext.currScene.environment.getFog().density = d;
                }
            }
        });

        // color
        colorPickerField.setCallback(new ColorPickerField.ColorSelected() {
            @Override
            public void selected(Color color) {
                projectContext.currScene.environment.getFog().color.set(color);
            }
        });

    }

    private void resetValues() {
        Fog fog = projectManager.current().currScene.environment.getFog();
        if(fog == null) {
            density.setDisabled(true);
            gradient.setDisabled(true);
        } else {
            useFog.setChecked(true);
            density.setText(String.valueOf(fog.density));
            gradient.setText(String.valueOf(fog.gradient));
            colorPickerField.setColor(fog.color);
        }
    }

    private Float convert(String input) {
        try {
            if(input.length() == 0) return null;
            return Float.valueOf(input);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onProjectChanged(ProjectChangedEvent projectChangedEvent) {
        resetValues();
    }

    @Override
    public void onSceneChanged(SceneChangedEvent sceneChangedEvent) {
        resetValues();
    }

}
