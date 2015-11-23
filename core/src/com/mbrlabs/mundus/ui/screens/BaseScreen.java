package com.mbrlabs.mundus.ui.screens;

import com.badlogic.gdx.Screen;
import com.mbrlabs.mundus.Mundus;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public abstract class BaseScreen implements Screen {

    protected Mundus mundus;

    public BaseScreen(Mundus mundus) {
        this.mundus = mundus;
    }

    @Override
    public abstract void show();

    @Override
    public abstract void render(float delta);

    @Override
    public abstract void resize(int width, int height);

    @Override
    public abstract void dispose();

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

}
