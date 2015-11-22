package com.mbrlabs.mundus.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mbrlabs.mundus.Mundus;

import java.awt.*;

public class DesktopLauncher {
	public static void main (String[] arg) {

        // get monitor dimension
        Rectangle maximumWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int width = (int) (maximumWindowBounds.getWidth() *0.95d);
        int height = (int) (maximumWindowBounds.getHeight()*0.95d);
        System.out.println("width: " + height);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = width;
        config.height = height;
        new LwjglApplication(new Mundus(), config);
	}
}
