package com.mbrlabs.mundus.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class UiImages {

    public static Drawable saveIcon;
    public static Drawable importIcon;
    public static Drawable runIcon;



    public static void load() {
        saveIcon = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/save.png"))));
        importIcon = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/import.png"))));
        runIcon = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/run.png"))));
    }

    public static void dispose() {

    }

}
