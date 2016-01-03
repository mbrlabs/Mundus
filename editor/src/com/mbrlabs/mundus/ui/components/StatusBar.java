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

package com.mbrlabs.mundus.ui.components;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.mbrlabs.mundus.utils.StringUtils;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class StatusBar extends Container {

    private HorizontalGroup group;

    private VisLabel verticesLabel;
    private VisLabel fpsLabel;
    private VisLabel camPos;

    public StatusBar() {
        super();
        setBackground(VisUI.getSkin().getDrawable("menu-bg"));
        align(Align.right | Align.center);
        group = new HorizontalGroup();
        group.space(10);
        group.padRight(10);
        setActor(group);

        verticesLabel = new VisLabel();
        fpsLabel = new VisLabel();
        camPos = new VisLabel();
        setFps(60);
        setVertexCount(0);

        group.addActor(camPos);
        group.addActor(verticesLabel);
        group.addActor(fpsLabel);
    }

    public void setFps(int fps) {
        this.fpsLabel.setText("fps: " + fps);
    }

    public void setVertexCount(long vertexCount) {
        this.verticesLabel.setText("vertices: " + vertexCount);
    }

    public void setCamPos(Vector3 pos) {
        camPos.setText("cam x,y,z: " + StringUtils.formatFloat(pos.x, 3) + "," +
                StringUtils.formatFloat(pos.y, 3) + "," +
                StringUtils.formatFloat(pos.z, 3));
    }


}
