/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    public void addTexture(MTexture texture) {
        grid.addActor(new TextureItem(texture));
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
