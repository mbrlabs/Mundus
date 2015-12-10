package com.mbrlabs.mundus.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;


/**
 * @author Marcus Brummer
 * @version 07-12-2015
 */
public class InputManager extends InputMultiplexer {

    public InputManager() {
        Gdx.input.setInputProcessor(this);
    }


}
