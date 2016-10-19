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

package com.mbrlabs.mundus.editor.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTextButton;

/**
 * @author Marcus Brummer
 * @version 18-02-2016
 */
public class ToggleButton extends VisTextButton {

    private String textOn;
    private String textOff;

    private boolean isOn;

    public ToggleButton(String textOn, String textOff) {
        super(textOn);
        this.textOn = textOn;
        this.textOff = textOff;
        this.isOn = true;

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleState();
            }
        });
    }

    public boolean isOn() {
        return this.isOn;
    }

    public void setOn(boolean on) {
        this.isOn = on;
        if (isOn) {
            setText(textOn);
        } else {
            setText(textOff);
        }
    }

    public boolean toggleState() {
        setOn(!isOn);
        return isOn;
    }

    public void setTextOn(String textOn) {
        this.textOn = textOn;
    }

    public void setTextOff(String textOff) {
        this.textOff = textOff;
    }

}
