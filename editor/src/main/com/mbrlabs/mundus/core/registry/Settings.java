package com.mbrlabs.mundus.core.registry;

/**
 * Global Mundus settings.
 *
 * @author Marcus Brummer
 * @version 07-06-2016
 */
public class Settings {

    private String fbxConvBinary = "";
    private KeyboardLayout keyboardLayout;

    public String getFbxConvBinary() {
        return fbxConvBinary;
    }

    public void setFbxConvBinary(String fbxConvBinary) {
        this.fbxConvBinary = fbxConvBinary;
    }

    public KeyboardLayout getKeyboardLayout() {
        return keyboardLayout;
    }

    public void setKeyboardLayout(KeyboardLayout keyboardLayout) {
        this.keyboardLayout = keyboardLayout;
    }

}
