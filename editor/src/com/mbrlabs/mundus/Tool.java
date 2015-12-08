package com.mbrlabs.mundus;

import com.badlogic.gdx.InputProcessor;
import com.mbrlabs.mundus.input.UpdatableInputProcessor;

/**
 * @author Marcus Brummer
 * @version 07-12-2015
 */
public interface Tool {

    public UpdatableInputProcessor getInputProcessor();

}
