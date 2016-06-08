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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.utils.Colors;

/**
 * @author Marcus Brummer
 * @version 03-01-2016
 */
public class FaTextButton extends VisTextButton {

    public final static TextButton.TextButtonStyle styleNoBg = new TextButton.TextButtonStyle();
    static {
        styleNoBg.font = Mundus.fa;
        styleNoBg.fontColor = Color.WHITE;
        styleNoBg.pressedOffsetX = 1;
        styleNoBg.unpressedOffsetX = 0;
        styleNoBg.pressedOffsetY = -1;
        styleNoBg.fontColor = Colors.TEAL;
    }

    public final static TextButton.TextButtonStyle styleBg = new TextButton.TextButtonStyle();
    static {
        styleBg.font = Mundus.fa;
        styleBg.pressedOffsetX = 1;
        styleBg.unpressedOffsetX = 0;
        styleBg.pressedOffsetY = -1;
        styleBg.fontColor = Colors.TEAL;
        styleBg.up = VisUI.getSkin().getDrawable("menu-bg");
        styleBg.down = VisUI.getSkin().getDrawable("menu-bg");
    }

    public final static TextButton.TextButtonStyle styleActive = new TextButton.TextButtonStyle();
    static {
        styleActive.font = Mundus.fa;
        styleActive.pressedOffsetX = 1;
        styleActive.unpressedOffsetX = 0;
        styleActive.pressedOffsetY = -1;
        styleActive.fontColor = Color.WHITE;
    }

    public FaTextButton(String text) {
        this(text, styleBg);
    }


    public FaTextButton(String text, TextButtonStyle style) {
        super(text);
        setStyle(style);
        setFocusBorderEnabled(false);
    }

}
