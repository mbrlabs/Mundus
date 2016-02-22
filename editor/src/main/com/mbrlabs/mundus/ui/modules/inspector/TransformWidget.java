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
                TranslateCommand command = new TranslateCommand(go);
                command.setBefore(go.position);
                go.setTrans(posX.getFloat(), go.position.y, go.position.z);
                command.setAfter(go.position);
                history.add(command);
            }
        });

        posY.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go == null) return;
                TranslateCommand command = new TranslateCommand(go);
                command.setBefore(go.position);
                go.setTrans(go.position.x, posY.getFloat(), go.position.z);
                command.setAfter(go.position);
                history.add(command);
            }
        });

        posZ.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go == null) return;
                TranslateCommand command = new TranslateCommand(go);
                command.setBefore(go.position);
                go.setTrans(go.position.x, go.position.y, posZ.getFloat());
                command.setAfter(go.position);
                history.add(command);
            }
        });

        // rotation
        ChangeListener rotateListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go == null) return;
                RotateCommand rotateCommand = new RotateCommand(go);
                rotateCommand.setBefore(go.rotation);
                go.setRot(rotX.getFloat(), rotY.getFloat(), rotZ.getFloat());
                rotateCommand.setAfter(go.rotation);
                history.add(rotateCommand);
            }
        };
        rotX.addListener(rotateListener);
        rotY.addListener(rotateListener);
        rotZ.addListener(rotateListener);

        scaleX.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(scaleX.getFloat() > 0f) {
                    ScaleCommand scaleCommand = new ScaleCommand(go);
                    scaleCommand.setBefore(go.scale);
                    go.setScl(scaleX.getFloat(), go.scale.y, go.scale.z);
                    scaleCommand.setAfter(go.scale);
                    history.add(scaleCommand);
                }
            }
        });

        scaleY.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(scaleY.getFloat() > 0f) {
                    ScaleCommand scaleCommand = new ScaleCommand(go);
                    scaleCommand.setBefore(go.scale);
                    go.setScl(go.scale.x, scaleY.getFloat(), go.scale.z);
                    scaleCommand.setAfter(go.scale);
                    history.add(scaleCommand);
                }
            }
        });

        scaleZ.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(scaleZ.getFloat() > 0f) {
                    ScaleCommand scaleCommand = new ScaleCommand(go);
                    scaleCommand.setBefore(go.scale);
                    go.setScl(go.scale.x, go.scale.y, scaleZ.getFloat());
                    scaleCommand.setAfter(go.scale);
                    history.add(scaleCommand);
                }
            }
        });

    }

    @Override
    public void setValues(GameObject go) {
        Vector3 pos = go.getTransRel(tempV3);
        posX.setText(StringUtils.formatFloat(pos.x, 2));
        posY.setText(StringUtils.formatFloat(pos.y, 2));
        posZ.setText(StringUtils.formatFloat(pos.z, 2));

        rotX.setText(StringUtils.formatFloat(go.rotation.getPitch(), 2));
        rotY.setText(StringUtils.formatFloat(go.rotation.getYaw(), 2));
        rotZ.setText(StringUtils.formatFloat(go.rotation.getRoll(), 2));

        Vector3 scl = go.getSclRel(tempV3);
        scaleX.setText(StringUtils.formatFloat(scl.x, 2));
        scaleY.setText(StringUtils.formatFloat(scl.y, 2));
        scaleZ.setText(StringUtils.formatFloat(scl.z, 2));
    }

//    @Override
//    public void setValues(GameObject go) {
//        posX.setText(StringUtils.formatFloat(go.position.x, 2));
//        posY.setText(StringUtils.formatFloat(go.position.y, 2));
//        posZ.setText(StringUtils.formatFloat(go.position.z, 2));
//
//        rotX.setText(StringUtils.formatFloat(go.rotation.getPitch(), 2));
//        rotY.setText(StringUtils.formatFloat(go.rotation.getYaw(), 2));
//        rotZ.setText(StringUtils.formatFloat(go.rotation.getRoll(), 2));
//
//        scaleX.setText(StringUtils.formatFloat(go.scale.x, 2));
//        scaleY.setText(StringUtils.formatFloat(go.scale.y, 2));
//        scaleZ.setText(StringUtils.formatFloat(go.scale.z, 2));
//    }

    @Override
    public void onDelete() {
        // The transform component can't be deleted.
        // Every game object has a transformation
    }

}
