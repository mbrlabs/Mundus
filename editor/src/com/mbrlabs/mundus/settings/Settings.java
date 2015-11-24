package com.mbrlabs.mundus.settings;

import com.mbrlabs.mundus.settings.global.GlobalSettings;
import org.apache.commons.io.FileUtils;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class Settings {

    public static final String HOME_DIR = FileUtils.getUserDirectoryPath();
    public static final String MUNDUS_DIR = HOME_DIR + "/.mundus";

    // settings
    public static final String SETTINGS_GLOBAL = MUNDUS_DIR + "/settings.json";

    // FBX
    public static final String FBX_DIR = MUNDUS_DIR + "/fbx-conv";
    public static final String FBX_LINUX = FBX_DIR + "/fbx-conv-lin64";
    public static final String FBX_MAC= FBX_DIR + "/fbx-conv-mac";
    public static final String FBX_WINDOWS = FBX_DIR + "/fbx-conv-win32.exe";

    public static GlobalSettings global = GlobalSettings.getInstance();




}
