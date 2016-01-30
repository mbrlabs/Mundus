package com.mbrlabs.mundus.events;

import com.mbrlabs.mundus.commons.model.MTexture;

/**
 * @author Marcus Brummer
 * @version 30-01-2016
 */
public class TextureImportEvent {

    private MTexture tex;

    public TextureImportEvent(MTexture tex) {
        this.tex = tex;
    }

    public MTexture getTex() {
        return tex;
    }

    public static interface TextureImportListener {
        @Subscribe
        public void onTextureImported(TextureImportEvent importEvent);
    }

}
