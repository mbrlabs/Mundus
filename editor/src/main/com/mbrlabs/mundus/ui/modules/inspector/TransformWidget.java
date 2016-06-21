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

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.history.CommandHistory;
import com.mbrlabs.mundus.history.commands.RotateCommand;
import com.mbrlabs.mundus.history.commands.ScaleCommand;
import com.mbrlabs.mundus.history.commands.TranslateCommand;
import com.mbrlabs.mundus.ui.widgets.FloatFieldWithLabel;
import com.mbrlabs.mundus.utils.StringUtils;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public class TransformWidget extends BaseInspectorWidget {

    private static final Vector3 tempV3 = new Vector3();
    private static final Quaternion tempQuat = new Quaternion();

    private FloatFieldWithLabel posX;
    private FloatFieldWithLabel posY;
    private FloatFieldWithLabel posZ;

    private FloatFieldWithLabel rotX;
    private FloatFieldWithLabel rotY;
    private FloatFieldWithLabel rotZ;

    private FloatFieldWithLabel scaleX;
    private FloatFieldWithLabel scaleY;
    private FloatFieldWithLabel scaleZ;

    @Inject
    private CommandHistory history;
    @Inject
    private ProjectManager projectManager;

    public TransformWidget() {
        super("TransformWidget");
        Mundus.inject(this);
        setDeletable(false);
        init();
        setupUI();
        setupListeners();
    }

    private void init() {
        int size = 65;
        posX = new FloatFieldWithLabel("x", size);
        posY = new FloatFieldWithLabel("y", size);
        posZ = new FloatFieldWithLabel("z", size);
        rotX = new FloatFieldWithLabel("x", size);
        rotY = new FloatFieldWithLabel("y", size);
        rotZ = new FloatFieldWithLabel("z", size);
        scaleX = new FloatFieldWithLabel("x", size);
        scaleY = new FloatFieldWithLabel("y", size);
        scaleZ = new FloatFieldWithLabel("z", size);
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

        collapsibleContent.add(new VisLabel("Scale: ")).padRight(5).padBottom(pad).left();
        collapsibleContent.add(scaleX).padBottom(pad).padRight(pad);
        collapsibleContent.add(scaleY).padBottom(pad).padRight(pad);
        collapsibleContent.add(scaleZ).padBottom(pad).row();
    }

    private void setupListeners() {
        final ProjectContext projectContext = projectManager.current();

        // position
        posX.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go == null) return;
                TranslateCommand command = new TranslateCommand(go);
                Vector3 pos = go.getLocalPosition(tempV3);
                command.setBefore(pos);
                go.setLocalPosition(posX.getFloat(), pos.y, pos.z);
                command.setAfter(go.getLocalPosition(tempV3));
                history.add(command);
            }
        });
        posY.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go == null) return;
                TranslateCommand command = new TranslateCommand(go);
                Vector3 pos = go.getLocalPosition(tempV3);
                command.setBefore(pos);
                go.setLocalPosition(pos.x, posY.getFloat(), pos.z);
                command.setAfter(go.getLocalPosition(tempV3));
                history.add(command);
            }
        });
        posZ.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go == null) return;
                TranslateCommand command = new TranslateCommand(go);
                Vector3 pos = go.getLocalPosition(tempV3);
                command.setBefore(pos);
                go.setLocalPosition(pos.x, pos.y, posZ.getFloat());
                command.setAfter(go.getLocalPosition(tempV3));
                history.add(command);
            }
        });

        // rotation
        rotX.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go == null) return;
                Quaternion rot = go.getLocalRotation(tempQuat);
                RotateCommand rotateCommand = new RotateCommand(go);
                rotateCommand.setBefore(rot);
                rot.setEulerAngles(rot.getYaw(), rotX.getFloat(), rot.getRoll());
                go.setLocalRotation(rot.x, rot.y, rot.z, rot.w);
                rotateCommand.setAfter(go.getLocalRotation(tempQuat));
                history.add(rotateCommand);
            }
        });
        rotY.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go == null) return;
                Quaternion rot = go.getLocalRotation(tempQuat);
                RotateCommand rotateCommand = new RotateCommand(go);
                rotateCommand.setBefore(rot);
                rot.setEulerAngles(rotY.getFloat(), rot.getPitch(), rot.getRoll());
                go.setLocalRotation(rot.x, rot.y, rot.z, rot.w);
                rotateCommand.setAfter(go.getLocalRotation(tempQuat));
                history.add(rotateCommand);
            }
        });
        rotZ.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go == null) return;
                Quaternion rot = go.getLocalRotation(tempQuat);
                RotateCommand rotateCommand = new RotateCommand(go);
                rotateCommand.setBefore(rot);
                rot.setEulerAngles(rot.getYaw(), rot.getPitch(), rotZ.getFloat());
                go.setLocalRotation(rot.x, rot.y, rot.z, rot.w);
                rotateCommand.setAfter(go.getLocalRotation(tempQuat));
                history.add(rotateCommand);
            }
        });

        // scale
        scaleX.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go != null && scaleX.getFloat() > 0f) {
                    ScaleCommand command = new ScaleCommand(go);
                    Vector3 scl = go.getLocalScale(tempV3);
                    command.setBefore(scl);
                    go.setLocalScale(scaleX.getFloat(), scl.y, scl.z);
                    command.setAfter(go.getLocalScale(tempV3));
                    history.add(command);
                }
            }
        });
        scaleY.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go != null && scaleY.getFloat() > 0f) {
                    ScaleCommand command = new ScaleCommand(go);
                    Vector3 scl = go.getLocalScale(tempV3);
                    command.setBefore(scl);
                    go.setLocalScale(scl.x, scaleY.getFloat(), scl.z);
                    command.setAfter(go.getLocalScale(tempV3));
                    history.add(command);
                }
            }
        });
        scaleZ.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go != null && scaleZ.getFloat() > 0f) {
                    ScaleCommand command = new ScaleCommand(go);
                    Vector3 scl = go.getLocalScale(tempV3);
                    command.setBefore(scl);
                    go.setLocalScale(scl.x, scl.y, scaleZ.getFloat());
                    command.setAfter(go.getLocalScale(tempV3));
                    history.add(command);
                }
            }
        });

    }

    @Override
    public void setValues(GameObject go) {
        Vector3 pos = go.getLocalPosition(tempV3);
        posX.setText(StringUtils.formatFloat(pos.x, 2));
        posY.setText(StringUtils.formatFloat(pos.y, 2));
        posZ.setText(StringUtils.formatFloat(pos.z, 2));

        Quaternion rot = go.getLocalRotation(tempQuat);
        rotX.setText(StringUtils.formatFloat(rot.getPitch(), 2));
        rotY.setText(StringUtils.formatFloat(rot.getYaw(), 2));
        rotZ.setText(StringUtils.formatFloat(rot.getRoll(), 2));

        Vector3 scl = go.getLocalScale(tempV3);
        scaleX.setText(StringUtils.formatFloat(scl.x, 2));
        scaleY.setText(StringUtils.formatFloat(scl.y, 2));
        scaleZ.setText(StringUtils.formatFloat(scl.z, 2));
    }

    @Override
    public void onDelete() {
        // The transform component can't be deleted.
        // Every game object has a transformation
    }

}
