package com.mbrlabs.mundus.core.home;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;
import com.mbrlabs.mundus.core.project.ProjectRef;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 11-12-2015
 */
public class HomeData {

    @Tag(0)
    public Settings settings;
    @Tag(1)
    public List<ProjectRef> projects;
    @Tag(2)
    public String lastProject;

    public HomeData() {
        projects = new ArrayList<>();
        settings = new Settings();
    }

    /**
     * Settings class
     */
    public static class Settings {
        @Tag(0)
        public String fbxConvBinary;
    }

}
