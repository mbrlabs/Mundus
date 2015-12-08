package com.mbrlabs.mundus.core;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mbrlabs.mundus.input.UpdatableInputProcessor;

/**
 * @author Marcus Brummer
 * @version 07-12-2015
 */
public interface Tool {

    /**
     * Returns the input processor of the tool.
     *
     * @return
     */
    public UpdatableInputProcessor getInputProcessor();

    /**
     * Renders something in case the toll has something to render.
     *
     * The model batch is fresh..meaning you have to call .begin() and start end() for
     * rendering.
     *
     * @param modelBatch
     */
    public void render(ModelBatch modelBatch);

}
