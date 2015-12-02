package com.mbrlabs.mundus;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.awt.*;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class Main {

    public static void main (String[] arg) {

        final Rectangle maximumWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int width = (int) maximumWindowBounds.getWidth();
        int height = (int) maximumWindowBounds.getHeight();

        System.out.println("verticesOnX: " + height);

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = width;
        config.height = (int) (height - height * .05);
        config.backgroundFPS = 0;
        new LwjglApplication(new Mundus(), config);
    }

}
