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

package com.mbrlabs.mundus.commons.env;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.mbrlabs.mundus.commons.env.lights.BaseLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLightsAttribute;
import com.mbrlabs.mundus.commons.env.lights.SunLight;
import com.mbrlabs.mundus.commons.env.lights.SunLightsAttribute;

/**
 * @author Marcus Brummer
 * @version 04-01-2016
 */
public class MundusEnvironment extends Environment {

    private Fog fog;
    private BaseLight ambientLight;

    public MundusEnvironment() {
        super();
        ambientLight = new BaseLight();
        fog = null;
    }

    public MundusEnvironment add(SunLight light) {
        SunLightsAttribute sunLights = ((SunLightsAttribute) get(SunLightsAttribute.Type));
        if (sunLights == null) set(sunLights = new SunLightsAttribute());
        sunLights.lights.add(light);

        return this;
    }

    public MundusEnvironment add(DirectionalLight light) {
        DirectionalLightsAttribute dirLights = ((DirectionalLightsAttribute) get(DirectionalLightsAttribute.Type));
        if (dirLights == null) set(dirLights = new DirectionalLightsAttribute());
        dirLights.lights.add(light);

        return this;
    }

    public BaseLight getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(BaseLight ambientLight) {
        this.ambientLight = ambientLight;
    }

    public Fog getFog() {
        return fog;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

}
