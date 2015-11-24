package com.mbrlabs.mundus.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.mbrlabs.mundus.Colors;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class GlUtils {

    public static void clearScreen(Color color) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(color.r, color.g, color.b, 1);
    }

}
