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
package com.mbrlabs.mundus.assets;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.utils.Log;

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

    public static final String META_EXTENSION = "meta";
    private static final String TAG = MetaFile.class.getSimpleName();

    private static final String COMMENT = " !!! WARNING, DO NOT MODIFY OR DELETE !!! \n " +
            "This file is machine generated. If you delete or modify this, Mundus might not work anymore.\n";
    private static final String PROP_VERSION = "version";
    private static final String PROP_UUID = "uuid";
    private static final String PROP_LAST_MODIFIED = "last_modified";

    private int version;
    private String uuid;
    private Date lastModified;

    private FileHandle file;
    private Properties props;

    public MetaFile(FileHandle file) {
        this.file = file;
        this.props = new Properties();
    }

    public void save() {
        props.clear();
        props.setProperty(PROP_VERSION, String.valueOf(this.version));
        props.setProperty(PROP_UUID, this.uuid);
        props.setProperty(PROP_LAST_MODIFIED, String.valueOf(this.lastModified.getTime()));
        try {
            props.store(new FileOutputStream(file.file()), COMMENT);
        } catch (IOException e) {
            Log.exception(e);
        }
    }

    public void load() {
        try {
            props.clear();
            props.load(new FileInputStream(file.file()));

            this.version = Integer.valueOf(props.getProperty(PROP_VERSION));
            this.uuid = props.getProperty(PROP_UUID);
            this.lastModified = new Date(Long.valueOf(props.getProperty(PROP_LAST_MODIFIED)));
        } catch (Exception e) {
            Log.exception(e);
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

    public FileHandle getFile() {
        return file;
    }

}
