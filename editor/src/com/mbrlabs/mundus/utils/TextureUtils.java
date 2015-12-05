package com.mbrlabs.mundus.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

/**
 * @author Marcus Brummer
 * @version 05-12-2015
 */
public class TextureUtils {

    public static Texture loadMipmapTexture(FileHandle fileHandle) {
        Texture texture = new Texture(fileHandle, true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        return texture;
    }

}
