/*
 * Copyright (c) 2015. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.core.kryo.descriptors;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;
import com.mbrlabs.mundus.core.project.ProjectRef;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 11-12-2015
 */
public class HomeDescriptor {

    @Tag(0)
    public Settings settings;
    @Tag(1)
    public List<ProjectRef> projects;
    @Tag(2)
    public ProjectRef lastProject = null;

    public HomeDescriptor() {
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
