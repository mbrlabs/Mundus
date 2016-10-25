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

package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.mbrlabs.mundus.commons.assets.meta.MetaFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * @author Marcus Brummer
 * @version 09-10-2016
 */
public class MaterialAsset extends Asset {

    public static final String EXTENSION = ".mat";

    // property keys
    public static final String PROP_DIFFUSE_COLOR = "diffuse.color";
    public static final String PROP_DIFFUSE_TEXTURE = "diffuse.texture";
    public static final String PROP_MAP_NORMAL = "map.normal";
    public static final String PROP_SHININESS = "shininess";
    public static final String PROP_OPACITY = "opacity";

    // ids of dependent assets
    private String diffuseTextureID;
    private String normalMapID;

    private Color diffuseColor = Color.WHITE.cpy();
    private TextureAsset diffuseTexture;
    private TextureAsset normalMap;
    private float shininess = 1f;
    private float opacity = 1f;

    public MaterialAsset(MetaFile meta, FileHandle assetFile) {
        super(meta, assetFile);
    }

    @Override
    public void load() {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(file.file()));

            // shininess & opacity
            try {
                String value = props.getProperty(PROP_SHININESS, null);
                if (value != null) {
                    shininess = Float.valueOf(value);
                }
                value = props.getProperty(PROP_OPACITY, null);
                if (value != null) {
                    opacity = Float.valueOf(value);
                }
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }

            // diffuse color
            String diffuseHex = props.getProperty(PROP_DIFFUSE_COLOR);
            if (diffuseHex != null) {
                diffuseColor = Color.valueOf(diffuseHex);
            }

            // asset dependencies
            diffuseTextureID = props.getProperty(PROP_DIFFUSE_TEXTURE, null);
            normalMapID = props.getProperty(PROP_MAP_NORMAL, null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Applies this model asset to the libGDX material.
     *
     * @param material
     * @return
     */
    public Material applyToMaterial(Material material) {
        if (diffuseColor != null) {
            material.set(new ColorAttribute(ColorAttribute.Diffuse, diffuseColor));
        }
        if (diffuseTexture != null) {
            material.set(new TextureAttribute(TextureAttribute.Diffuse, diffuseTexture.getTexture()));
        } else {
            material.remove(TextureAttribute.Diffuse);
        }
        material.set(new FloatAttribute(FloatAttribute.Shininess, shininess));

        return material;
    }

    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public TextureAsset getNormalMap() {
        return normalMap;
    }

    public void setNormalMap(TextureAsset normalMap) {
        this.normalMap = normalMap;
        normalMapID = normalMap.getID();
    }

    public TextureAsset getDiffuseTexture() {
        return diffuseTexture;
    }

    public void setDiffuseTexture(TextureAsset diffuseTexture) {
        this.diffuseTexture = diffuseTexture;
        if (diffuseTexture != null) {
            this.diffuseTextureID = diffuseTexture.getID();
        } else {
            this.diffuseTextureID = null;
        }
    }

    public Color getDiffuseColor() {
        return diffuseColor;
    }

    @Override
    public void resolveDependencies(Map<String, Asset> assets) {
        if (diffuseTextureID != null && assets.containsKey(diffuseTextureID)) {
            diffuseTexture = (TextureAsset) assets.get(diffuseTextureID);
        }
        if (normalMapID != null && assets.containsKey(normalMapID)) {
            normalMap = (TextureAsset) assets.get(normalMapID);
        }
    }

    @Override
    public void applyDependencies() {
        // nothing to apply
    }

    @Override
    public void dispose() {
        // nothing to dispose
    }

}
