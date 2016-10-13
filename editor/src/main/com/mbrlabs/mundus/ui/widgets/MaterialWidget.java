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

package com.mbrlabs.mundus.ui.widgets;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.ui.modules.dialogs.assets.AssetTextureFilter;

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
public class MaterialWidget extends VisTable {

    private ColorPickerField diffuseColorField;
    private AssetSelectionField diffuseAssetField;

    private MaterialAsset material;

    public MaterialWidget() {
        super();

        diffuseAssetField = new AssetSelectionField();
        diffuseColorField = new ColorPickerField();

        add(new VisLabel("Diffuse texture")).row();
        add(diffuseAssetField).row();
        add(new VisLabel("Diffuse texture")).row();
        add(diffuseColorField).row();

        setupWidgets();
    }

    private void setupWidgets() {
        // diffuse texture
        diffuseAssetField.setFilter(new AssetTextureFilter());
        diffuseAssetField.setListener(asset -> material.setDiffuseTexture((TextureAsset) asset));

        // diffuse color
        diffuseColorField.setCallback(color -> material.setDiffuseColor(color));
    }

    public void setMaterial(MaterialAsset material) {
        this.material = material;
        diffuseColorField.setColor(material.getDiffuseColor());
        diffuseAssetField.setAsset(material.getDiffuseTexture());
    }

    public MaterialAsset getMaterial() {
        return material;
    }

}
