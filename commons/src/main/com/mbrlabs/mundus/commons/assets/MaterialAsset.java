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

    // property keys
    private static final String PROP_DIFFUSE_COLOR = "diffuse.color";
    private static final String PROP_DIFFUSE_TEXTURE = "diffuse.texture";
    private static final String PROP_MAP_NORMAL = "map.normal";
    private static final String PROP_SHININESS = "shininess";
    private static final String PROP_OPACITY = "opacity";

    // ids of dependent assets
    private String diffuseTextureID;
    private String normalMapID;

    private Color diffuseColor;
    private TextureAsset diffuseTexture;
    private TextureAsset normalMap;
    private float shininess;
    private float opacity;

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
