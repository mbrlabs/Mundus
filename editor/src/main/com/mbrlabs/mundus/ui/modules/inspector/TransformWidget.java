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

package com.mbrlabs.mundus.ui.modules.inspector;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.history.CommandHistory;
import com.mbrlabs.mundus.history.commands.RotateCommand;
import com.mbrlabs.mundus.history.commands.TranslateCommand;
import com.mbrlabs.mundus.ui.widgets.TextFieldWithLabel;
import com.mbrlabs.mundus.utils.StringUtils;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public class TransformWidget extends BaseInspectorWidget {

    private static final Vector3 tempV3 = new Vector3();
    private static final Quaternion tempQuat = new Quaternion();

    private TextFieldWithLabel posX;
    private TextFieldWithLabel posY;
    private TextFieldWithLabel posZ;

    private TextFieldWithLabel rotX;
    private TextFieldWithLabel rotY;
    private TextFieldWithLabel rotZ;

    private TextFieldWithLabel scaleX;
    private TextFieldWithLabel scaleY;
    private TextFieldWithLabel scaleZ;

    @Inject
    private ProjectContext projectContext;
    @Inject
    private CommandHistory history;

    public TransformWidget() {
        super("Transform");
        Mundus.inject(this);
        setDeletable(false);
        init();
        setupUI();
        setupListeners();
    }

    private void init() {
        int size = 65;
        posX = new TextFieldWithLabel("x", size);
        posY = new TextFieldWithLabel("y", size);
        posZ = new TextFieldWithLabel("z", size);
        rotX = new TextFieldWithLabel("x", size);
        rotY = new TextFieldWithLabel("y", size);
        rotZ = new TextFieldWithLabel("z", size);
        scaleX = new TextFieldWithLabel("x", size);
        scaleY = new TextFieldWithLabel("y", size);
        scaleZ = new TextFieldWithLabel("z", size);
    }

    private void setupUI() {
        int pad = 4;
        collapsibleContent.add(new VisLabel("Position: ")).padRight(5).padBottom(pad).left();
        collapsibleContent.add(posX).padBottom(pad).padRight(pad);
        collapsibleContent.add(posY).padBottom(pad).padRight(pad);
        collapsibleContent.add(posZ).padBottom(pad).row();

        collapsibleContent.add(new VisLabel("Rotation: ")).padRight(5).padBottom(pad).left();
        collapsibleContent.add(rotX).padBottom(pad).padRight(pad);
        collapsibleContent.add(rotY).padBottom(pad).padRight(pad);
        collapsibleContent.add(rotZ).padBottom(pad).row();

        collapsibleContent.add(new VisLabel("Scale: ")).padRight(10).padBottom(pad).left();
        collapsibleContent.add(scaleX).padBottom(pad).padRight(pad);
        collapsibleContent.add(scaleY).padBottom(pad).padRight(pad);
        collapsibleContent.add(scaleZ).padBottom(pad).row();
    }

    private void setupListeners() {
        posX.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go == null) return;
                try {
                    TranslateCommand command = new TranslateCommand(go);
                    go.transform.getTranslation(tempV3);
                    command.setBefore(tempV3);
                    go.setTranslation(Float.parseFloat(posX.getText()), tempV3.y, tempV3.z, true);
                    go.transform.getTranslation(tempV3);
                    command.setAfter(tempV3);
                    history.add(command);
                } catch (NumberFormatException nfe) {
                    // blah...
                }
            }
        });

        posY.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go == null) return;
                try {
                    go.transform.getTranslation(tempV3);
                    go.setTranslation(tempV3.x, Float.parseFloat(posY.getText()), tempV3.z, true);
                    TranslateCommand command = new TranslateCommand(go);
                    go.transform.getTranslation(tempV3);
                    command.setBefore(tempV3);
                    go.setTranslation(tempV3.x, Float.parseFloat(posY.getText()), tempV3.z, true);
                    go.transform.getTranslation(tempV3);
                    command.setAfter(tempV3);
                    history.add(command);
                } catch (NumberFormatException nfe) {
                    // blah...
                }
            }
        });

        posZ.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go == null) return;
                try {
                    go.transform.getTranslation(tempV3);
                    go.setTranslation(tempV3.x, Float.parseFloat(posY.getText()), tempV3.z, true);
                    TranslateCommand command = new TranslateCommand(go);
                    go.transform.getTranslation(tempV3);
                    command.setBefore(tempV3);
                    go.setTranslation(tempV3.x, tempV3.y, Float.parseFloat(posZ.getText()), true);
                    go.transform.getTranslation(tempV3);
                    command.setAfter(tempV3);
                    history.add(command);
                } catch (NumberFormatException nfe) {
                    // blah...
                }
            }
        });

        ChangeListener rotateListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go == null) return;
                try {
                    RotateCommand rotateCommand = new RotateCommand(go);
                    rotateCommand.setBefore(go.transform.getRotation(tempQuat));

                    float x = Float.parseFloat(rotX.getText());
                    float y = Float.parseFloat(rotY.getText());
                    float z = Float.parseFloat(rotZ.getText());
                    go.setRotation(x, y, z);

                    rotateCommand.setAfter(go.transform.getRotation(tempQuat));
                    history.add(rotateCommand);
                } catch (NumberFormatException nfe) {
                    // blah...
                }
            }
        };
        rotX.addListener(rotateListener);
        rotY.addListener(rotateListener);
        rotZ.addListener(rotateListener);

    }

    @Override
    public void setValues(GameObject go) {
        go.transform.getTranslation(tempV3);
        posX.setText(StringUtils.formatFloat(tempV3.x, 2));
        posY.setText(StringUtils.formatFloat(tempV3.y, 2));
        posZ.setText(StringUtils.formatFloat(tempV3.z, 2));

        go.transform.getRotation(tempQuat);
        rotX.setText(StringUtils.formatFloat(tempQuat.getPitch(), 2));
        rotY.setText(StringUtils.formatFloat(tempQuat.getYaw(), 2));
        rotZ.setText(StringUtils.formatFloat(tempQuat.getRoll(), 2));

        go.transform.getScale(tempV3);
        scaleX.setText(StringUtils.formatFloat(tempV3.x, 2));
        scaleY.setText(StringUtils.formatFloat(tempV3.y, 2));
        scaleZ.setText(StringUtils.formatFloat(tempV3.z, 2));
    }

    @Override
    public void onDelete() {
        // The transform component can't be deleted.
        // Every game object has a transformation
    }

}
