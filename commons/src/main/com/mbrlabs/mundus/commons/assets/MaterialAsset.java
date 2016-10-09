//package com.mbrlabs.mundus.commons.assets;
//
//import com.badlogic.gdx.files.FileHandle;
//import com.badlogic.gdx.graphics.Color;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.Properties;
//
///**
// * @author Marcus Brummer
// * @version 09-10-2016
// */
//public class MaterialAsset extends Asset {
//
//    private static final String PROP_DIFFUSE_COLOR = "diffuse.color";
//    private static final String PROP_DIFFUSE_TEXTURE = "diffuse.texture";
//
//    private static final String PROP_MAP_NORMAL = "map.normal";
//
//    private static final String PROP_SHININESS = "shininess";
//    private static final String PROP_OPACITY = "opacity";
//
//    private Color diffuseColor;
//    private TextureAsset diffuseTexture;
//
//    private TextureAsset normalMap;
//
//    private float shininess;
//    private float opacity;
//
//    public MaterialAsset(MetaFile meta, FileHandle assetFile) {
//        super(meta, assetFile);
//    }
//
//    @Override
//    public void load() {
//        Properties props = new Properties();
//        try {
//            props.load(new FileInputStream(file.file()));
//
//            // diffuse color
//            String diffuseHex = props.getProperty(PROP_DIFFUSE_COLOR);
//            if(diffuseHex != null) {
//                diffuseColor = Color.valueOf(diffuseHex);
//            }
//
//            // diffuse texture
//            diffuseTexture = props.getProperty(PROP_DIFFUSE_TEXTURE);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void applyDependencies() {
//
//    }
//
//    @Override
//    public void dispose() {
//
//    }
//
//}
