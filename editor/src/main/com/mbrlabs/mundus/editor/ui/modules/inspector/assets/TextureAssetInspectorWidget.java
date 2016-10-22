/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.editor.ui.modules.inspector.assets;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget;

import org.apache.commons.io.FileUtils;

/**
 * @author Marcus Brummer
 * @version 15-10-2016
 */
public class TextureAssetInspectorWidget extends BaseInspectorWidget {

    private static final String TITLE = "Texture Asset";

    private VisLabel name;
    private VisLabel width;
    private VisLabel height;
    private VisLabel fileSize;

    private TextureAsset textureAsset;

    public TextureAssetInspectorWidget() {
        super(TITLE);

        name = new VisLabel();
        width = new VisLabel();
        height = new VisLabel();
        fileSize = new VisLabel();

        collapsibleContent.add(name).growX().row();
        collapsibleContent.add(width).growX().row();
        collapsibleContent.add(height).growX().row();
    }

    public void setTextureAsset(TextureAsset texture) {
        this.textureAsset = texture;
        updateUI();
    }

    private void updateUI() {
        name.setText("Name: " + textureAsset.getName());
        width.setText("Width: " + textureAsset.getTexture().getWidth() + " px");
        height.setText("Height: " + textureAsset.getTexture().getHeight() + " px");

        float mb = (FileUtils.sizeOf(textureAsset.getFile().file()) / 1000000f);
        fileSize.setText("Size: " + mb + " mb");
    }

    @Override
    public void onDelete() {
        // nope
    }

    @Override
    public void setValues(GameObject go) {
        // nope
    }

}
