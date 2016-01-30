package com.mbrlabs.mundus.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.model.MTexture;

/**
 * @author Marcus Brummer
 * @version 30-01-2016
 */
public class TextureGrid extends VisTable {

    private GridGroup grid;
    private OnTextureClickedListener listener;

    public TextureGrid(int imgSize, int spacing) {
        super();
        this.grid = new GridGroup(imgSize, spacing);
        add(grid).expand().fill().row();
    }

    public TextureGrid(int imgSize, int spacing, Array<MTexture> textures) {
        this(imgSize, spacing);
        setTextures(textures);
    }

    public void setListener(OnTextureClickedListener listener) {
        this.listener = listener;
    }

    public void setTextures(Array<MTexture> textures) {
        grid.clearChildren();
        for(MTexture tex : textures) {
            grid.addActor(new TextureItem(tex));
        }
    }

    /**
     *
     */
    public interface OnTextureClickedListener {
        public void onTextureSelected(MTexture texture);
    }

    /**
     *
     */
    private class TextureItem extends VisTable {
        public TextureItem(final MTexture tex) {
            super();
            add(new VisImage(tex.texture));
            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    listener.onTextureSelected(tex);
                }
            });
        }
    }

}
