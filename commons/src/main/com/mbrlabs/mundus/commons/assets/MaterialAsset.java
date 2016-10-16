package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;

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

    private Color diffuseColor;
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
        if(diffuseTexture != null) {
            this.diffuseTextureID = diffuseTexture.getID();
        } else {
            this.diffuseTextureID = null;
        }
    }

    public Color getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Color diffuseColor) {
        this.diffuseColor = diffuseColor;
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
