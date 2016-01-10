/*
 * Copyright (c) 2016. See AUTHORS file.
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.commons.skybox.Skybox;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.events.EventBus;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.events.Subscribe;
import com.mbrlabs.mundus.ui.widgets.ImageChooserField;

/**
 * @author Marcus Brummer
 * @version 10-01-2016
 */
public class SkyboxDialog extends BaseDialog {

    private ImageChooserField positiveX;
    private ImageChooserField negativeX;
    private ImageChooserField positiveY;
    private ImageChooserField negativeY;
    private ImageChooserField positiveZ;
    private ImageChooserField negativeZ;
    private VisTextButton submitBtn;


    @Inject
    private ProjectContext projectContext;
    @Inject
    private EventBus eventBus;

    public SkyboxDialog() {
        super("Skybox");
        Mundus.inject(this);
        eventBus.register(this);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        positiveX = new ImageChooserField(100);
        positiveX.setButtonText("positiveX");
        negativeX = new ImageChooserField(100);
        negativeX.setButtonText("negativeX");
        positiveY = new ImageChooserField(100);
        positiveY.setButtonText("positiveY");
        negativeY = new ImageChooserField(100);
        negativeY.setButtonText("negativeY");
        positiveZ = new ImageChooserField(100);
        positiveZ.setButtonText("positiveZ");
        negativeZ = new ImageChooserField(100);
        negativeZ.setButtonText("negativeZ");

        submitBtn = new VisTextButton("Create skybox");

        VisTable root = new VisTable();
        // root.debugAll();
        root.padTop(6).padRight(6).padBottom(22);
        add(root).left().top();
        root.add(new VisLabel("The 6 images must be square and of equal size")).colspan(3).row();
        root.addSeparator().colspan(3).row();
        root.add(positiveX);
        root.add(negativeX);
        root.add(positiveY).row();
        root.add(negativeY);
        root.add(positiveZ);
        root.add(negativeZ).row();
        root.add(submitBtn).padTop(15).padLeft(6).padRight(6).expandX().fillX().colspan(3).row();
    }

    private void setupListeners() {

        // submit btn
        submitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Skybox oldSkybox = projectContext.currScene.skybox;
                if(oldSkybox != null) {
                    oldSkybox.dispose();
                }

                projectContext.currScene.skybox = new Skybox(positiveX.getFile(), negativeX.getFile(),
                        positiveY.getFile(), negativeY.getFile(), positiveZ.getFile(), negativeZ.getFile());

            }
        });
    }

    @Subscribe
    public void setImages(ProjectChangedEvent projectChangedEvent) {
        Skybox skybox = projectContext.currScene.skybox;
        if(skybox != null) {
            positiveX.setImage(skybox.getPositiveX());
            negativeX.setImage(skybox.getNegativeX());
            positiveY.setImage(skybox.getPositiveY());
            negativeY.setImage(skybox.getNegativeY());
            positiveZ.setImage(skybox.getPositiveY());
            negativeZ.setImage(skybox.getNegativeZ());
        }
    }

}
