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

package com.mbrlabs.mundus.ui.modules.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.mbrlabs.mundus.ui.Ui;

/**
 * @author Marcus Brummer
 * @version 20-12-2015
 */
public class EnvironmentMenu extends Menu {

    private MenuItem ambientLight;
    private MenuItem skybox;
    private MenuItem fog;

    public EnvironmentMenu() {
        super("Environment");

        ambientLight = new MenuItem("Ambient Light");
        fog = new MenuItem("Fog");
        skybox = new MenuItem("Skybox");

        addItem(ambientLight);
        addItem(skybox);
        addItem(fog);

        setupListeners();
    }

    private void setupListeners() {

        // ambient light
        ambientLight.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Ui.getInstance().showDialog(Ui.getInstance().getAmbientLightDialog());
            }
        });

        // fog
        fog.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Ui.getInstance().showDialog(Ui.getInstance().getFogDialog());
            }
        });

        // skybox
        skybox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Ui.getInstance().showDialog(Ui.getInstance().getSkyboxDialog());
            }
        });

    }

    public MenuItem getAmbientLight() {
        return ambientLight;
    }

    public MenuItem getFog() {
        return fog;
    }

    public MenuItem getSkybox() {
        return skybox;
    }

}
