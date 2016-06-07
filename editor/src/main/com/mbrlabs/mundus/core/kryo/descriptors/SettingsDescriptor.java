package com.mbrlabs.mundus.core.kryo.descriptors;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;
import com.mbrlabs.mundus.core.registry.KeyboardLayout;

/**
 * Created by marcus on 07/06/16.
 */
public class SettingsDescriptor {

    @TaggedFieldSerializer.Tag(0)
    private String fbxConvBinary = "";

    @TaggedFieldSerializer.Tag(1)
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
