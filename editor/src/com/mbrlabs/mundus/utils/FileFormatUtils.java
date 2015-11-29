package com.mbrlabs.mundus.utils;

import com.badlogic.gdx.files.FileHandle;

/**
 * @author Marcus Brummer
 * @version 29-11-2015
 */
public class FileFormatUtils {

    public static final String FORMAT_3D_G3DB = "g3db";
    public static final String FORMAT_3D_G3DJ = "g3dj";
    public static final String FORMAT_3D_FBX = "fbx";

    public static final String FORMAT_IMG_PNG = "png";
    public static final String FORMAT_IMG_JPG = "jpg";

    public static boolean isG3DB(String filename) {
        if(filename == null) return false;
        return filename.endsWith(FORMAT_3D_G3DB);
    }

    public static boolean isG3DB(FileHandle file) {
        if(file == null) return false;
        return isG3DB(file.name());
    }

    public static boolean isFBX(String filename) {
        if(filename == null) return false;
        return filename.endsWith(FORMAT_3D_FBX);
    }

    public static boolean isFBX(FileHandle file) {
        if(file == null) return false;
        return isFBX(file.name());
    }

    public static boolean isG3DJ(String filename) {
        if(filename == null) return false;
        return filename.endsWith(FORMAT_3D_G3DJ);
    }

    public static boolean isG3DJ(FileHandle file) {
        if(file == null) return false;
        return isG3DJ(file.name());
    }

    public static boolean isPNG(FileHandle file) {
        if(file == null) return false;
        return isPNG(file.name());
    }

    public static boolean isPNG(String filename) {
        if(filename == null) return false;
        return filename.endsWith(FORMAT_IMG_PNG);
    }

}
