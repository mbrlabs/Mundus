package com.mbrlabs.mundus.utils;

import com.badlogic.gdx.files.FileHandle;

/**
 * @author Marcus Brummer
 * @version 29-11-2015
 */
public class FileFormatUtils {

    public static final String FORMAT_3D_G3DB = "g3db";
    public static final String FORMAT_3D_G3DJ = "g3dj";
    public static final String FORMAT_3D_COLLADA = "dae";
    public static final String FORMAT_3D_WAVEFONT = "obj";
    public static final String FORMAT_3D_FBX = "fbx";

    public static final String FORMAT_IMG_PNG = "png";
    public static final String FORMAT_IMG_JPG = "jpg";
    public static final String FORMAT_IMG_JPEG = "jpeg";

    public static boolean isG3DB(String filename) {
        return filename != null && filename.endsWith(FORMAT_3D_G3DB);
    }

    public static boolean isG3DB(FileHandle file) {
        return file != null && isG3DB(file.name());
    }

    public static boolean isWavefont(String filename) {
        return filename != null && filename.endsWith(FORMAT_3D_WAVEFONT);
    }

    public static boolean isWavefont(FileHandle file) {
        return file != null && isWavefont(file.name());
    }

    public static boolean isCollada(String filename) {
        return filename != null && filename.endsWith(FORMAT_3D_COLLADA);
    }

    public static boolean isCollada(FileHandle file) {
        return file != null && isCollada(file.name());
    }

    public static boolean isFBX(String filename) {
        return filename != null && filename.endsWith(FORMAT_3D_FBX);
    }

    public static boolean isFBX(FileHandle file) {
        return file != null && isFBX(file.name());
    }

    public static boolean isG3DJ(String filename) {
        return filename != null && filename.endsWith(FORMAT_3D_G3DJ);
    }

    public static boolean isG3DJ(FileHandle file) {
        return file != null && isG3DJ(file.name());
    }

    public static boolean isPNG(FileHandle file) {
        return file != null && isPNG(file.name());
    }

    public static boolean isPNG(String filename) {
        return filename != null && filename.endsWith(FORMAT_IMG_PNG);
    }

    public static boolean isJPG(String filename) {
        return filename != null && (filename.endsWith(FORMAT_IMG_JPG)
                || filename.endsWith(FORMAT_IMG_JPEG));
    }

    public static boolean isJPG(FileHandle file) {
        return file != null && isJPG(file.name());
    }

    public static boolean is3DFormat(String filename) {
        return filename != null &&
                (filename.endsWith(FORMAT_3D_WAVEFONT)  || filename.endsWith(FORMAT_3D_COLLADA) ||
                        filename.endsWith(FORMAT_3D_G3DB) || filename.endsWith(FORMAT_3D_G3DJ) ||
                        filename.endsWith(FORMAT_3D_FBX));
    }

    public static boolean is3DFormat(FileHandle file) {
        return file != null && is3DFormat(file.name());
    }

    public static boolean isImage(String filename) {
        return filename != null && (isJPG(filename) || isPNG(filename));
    }

    public static boolean isImage(FileHandle file) {
        return file != null && isImage(file.name());
    }

}
