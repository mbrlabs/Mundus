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

package com.mbrlabs.mundus.editor.ui.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.editor.core.Inject;
import com.mbrlabs.mundus.editor.core.Mundus;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.input.FreeCamController;
import com.mbrlabs.mundus.editor.utils.StringUtils;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class StatusBar extends VisTable {

    private VisTable root;
    private VisTable left;
    private VisTable right;

    private VisLabel fpsLabel;
    private VisLabel camPos;

    private VisTextButton speed01;
    private VisTextButton speed1;
    private VisTextButton speed10;

    @Inject
    private FreeCamController freeCamController;
    @Inject
    private ProjectManager projectManager;

    public StatusBar() {
        super();
        Mundus.inject(this);
        setBackground(VisUI.getSkin().getDrawable("menu-bg"));
        root = new VisTable();
        root.align(Align.left | Align.center);
        add(root).expand().fill();

        left = new VisTable();
        left.align(Align.left);
        left.padLeft(10);
        right = new VisTable();
        right.align(Align.right);
        right.padRight(10);
        root.add(left).left().expand().fill();
        root.add(right).right().expand().fill();

        // left
        left.add(new VisLabel("camSpeed: ")).left();
        speed01 = new VisTextButton(".1");
        speed1 = new VisTextButton("1");
        speed10 = new VisTextButton("10");
        left.add(speed01);
        left.add(speed1);
        left.add(speed10);

        // right
        fpsLabel = new VisLabel();
        camPos = new VisLabel();
        right.add(camPos).right();
        right.addSeparator(true).padLeft(5).padRight(5);
        right.add(fpsLabel).right();

        setupListeners();
    }

    public void setupListeners() {
        speed01.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                freeCamController.setVelocity(FreeCamController.SPEED_01);
            }
        });

        speed1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                freeCamController.setVelocity(FreeCamController.SPEED_1);
            }
        });

        speed10.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                freeCamController.setVelocity(FreeCamController.SPEED_10);
            }
        });
    }

    @Override
    public void act(float delta) {
        setFps(Gdx.graphics.getFramesPerSecond());
        setCamPos(projectManager.current().currScene.cam.position);
        super.act(delta);
    }

    private void setFps(int fps) {
        this.fpsLabel.setText("fps: " + fps);
    }

    private void setCamPos(Vector3 pos) {
        camPos.setText("camPos: " + StringUtils.formatFloat(pos.x, 2) + ", " + StringUtils.formatFloat(pos.y, 2) + ", "
                + StringUtils.formatFloat(pos.z, 2));
    }

}
