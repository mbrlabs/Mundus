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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * Meta files contain additional information about a single asset file.
 *
 * @author Marcus Brummer
 * @version 01-10-2016
 */
public class MetaFile {

    private static final String TAG = MetaFile.class.getSimpleName();

    public static final String META_EXTENSION = "meta";
    public static final int CURRENT_VERSION = 1;

    private static final String COMMENT = "# !!! WARNING, DO NOT MODIFY OR DELETE !!! \n " +
            "This file is machine generated. If you delete or modify this, Mundus might not work anymore.\n";

    private static final String PROP_VERSION            = "version";
    private static final String PROP_UUID               = "uuid";
    private static final String PROP_LAST_MODIFIED      = "last_modified";
    private static final String PROP_TYPE               = "type";

    private static final String PROP_MATERIAL_DIFFUSE_COLOR         = "mat.diffuse.color";
    private static final String PROP_MATERIAL_DIFFUSE_TEXTURE       = "mat.diffuse.texture";

    private FileHandle file;
    private Properties props;

    // general stuff
    private int version;
    private AssetType type;
    private String uuid;
    private Date lastModified;

    // model specific
    private Color diffuseColor = null;
    private String diffuseTexture = null;

    public MetaFile(FileHandle file) {
        this.file = file;
        this.props = new Properties();
    }

    public void save() throws IOException {
        props.clear();
        props.setProperty(PROP_VERSION, String.valueOf(this.version));
        props.setProperty(PROP_TYPE, this.type.name());
        props.setProperty(PROP_UUID, this.uuid);
        props.setProperty(PROP_LAST_MODIFIED, String.valueOf(this.lastModified.getTime()));

        // model specific
        if(type == AssetType.MODEL) {
            if(diffuseColor != null) {
                props.setProperty(PROP_MATERIAL_DIFFUSE_COLOR, diffuseColor.toString());
            }
            if(diffuseTexture != null) {
                props.setProperty(PROP_MATERIAL_DIFFUSE_TEXTURE, diffuseTexture);
            }
        }

        props.store(new FileOutputStream(file.file()), COMMENT);
    }

    public void load() throws MetaFileParseException {
        try {
            props.clear();
            props.load(new FileInputStream(file.file()));

            this.version = Integer.valueOf(props.getProperty(PROP_VERSION));
            this.type = AssetType.valueOf(props.getProperty(PROP_TYPE));
            this.uuid = props.getProperty(PROP_UUID);
            this.lastModified = new Date(Long.valueOf(props.getProperty(PROP_LAST_MODIFIED)));

            // model specific
            if(type == AssetType.MODEL) {
                String color = props.getProperty(PROP_MATERIAL_DIFFUSE_COLOR, null);
                if(color != null) {
                    this.diffuseColor = Color.valueOf(color);
                }
                this.diffuseTexture = props.getProperty(PROP_MATERIAL_DIFFUSE_TEXTURE, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new MetaFileParseException();
        }
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public AssetType getType() {
        return type;
    }

    public void setType(AssetType type) {
        this.type = type;
    }

    public FileHandle getFile() {
        return file;
    }

}
