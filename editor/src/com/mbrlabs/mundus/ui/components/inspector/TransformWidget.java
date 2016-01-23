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

package com.mbrlabs.mundus.ui.components.inspector;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Queue;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.scene3d.GameObject;
import com.mbrlabs.mundus.ui.widgets.TextFieldWithLabel;

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
                if(projectContext.currScene.currentSelection == null) return;
                try {
                    projectContext.currScene.currentSelection.transform.getTranslation(tempV3);
                    projectContext.currScene.currentSelection.setTranslation(
                            Float.parseFloat(posX.getText()), tempV3.y, tempV3.z);
                } catch (NumberFormatException nfe) {
                    // blah...
                }
            }
        });

        posY.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(projectContext.currScene.currentSelection == null) return;
                try {
                    projectContext.currScene.currentSelection.transform.getTranslation(tempV3);
                    projectContext.currScene.currentSelection.setTranslation(
                            tempV3.x, Float.parseFloat(posY.getText()), tempV3.z);
                } catch (NumberFormatException nfe) {
                    // blah...
                }
            }
        });

        posZ.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(projectContext.currScene.currentSelection == null) return;
                try {
                    projectContext.currScene.currentSelection.transform.getTranslation(tempV3);
                    projectContext.currScene.currentSelection.setTranslation(
                            tempV3.x, tempV3.y, Float.parseFloat(posZ.getText()));
                } catch (NumberFormatException nfe) {
                    // blah...
                }
            }
        });

    }

    @Override
    public void setValues(GameObject go) {
        go.transform.getTranslation(tempV3);
        posX.setText(String.valueOf(tempV3.x));
        posY.setText(String.valueOf(tempV3.y));
        posZ.setText(String.valueOf(tempV3.z));

        go.transform.getRotation(tempQuat);
        rotX.setText(String.valueOf(tempQuat.x));
        rotY.setText(String.valueOf(tempQuat.y));
        rotZ.setText(String.valueOf(tempQuat.z));

        go.transform.getScale(tempV3);
        scaleX.setText(String.valueOf(tempV3.x));
        scaleY.setText(String.valueOf(tempV3.y));
        scaleZ.setText(String.valueOf(tempV3.z));
    }

    @Override
    public void onDelete() {
        // The transform component can't be deleted.
        // Every game object has a transformation
    }

}
