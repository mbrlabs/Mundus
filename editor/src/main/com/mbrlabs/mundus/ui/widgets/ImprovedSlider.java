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

package com.mbrlabs.mundus.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.utils.StringUtils;

/**
 * Can be used inside a scroll pane & has the current value displayed on the
 * right.
 *
 * @author Marcus Brummer
 * @version 04-02-2016
 */
public class ImprovedSlider extends VisTable {

    private VisLabel currentValue;
    private ScrollPaneSlider slider;

    public ImprovedSlider(float min, float max, float step) {
        super();
        currentValue = new VisLabel("0");
        slider = new ScrollPaneSlider(min, max, step, false);

        add(slider).expandX().fillX().left();
        add(currentValue).padLeft(10).right();

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentValue.setText(String.format(StringUtils.formatFloat(slider.getValue(), 2)));
            }
        });
    }

    public float getValue() {
        return slider.getValue();
    }

    public void setValue(float value) {
        slider.setValue(value);
        currentValue.setText(StringUtils.formatFloat(value, 2));
    }

}
