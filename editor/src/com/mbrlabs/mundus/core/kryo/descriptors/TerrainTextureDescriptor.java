/*
 * Copyright (c) 2016. See AUTHORS file.
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

/**
 * @author Marcus Brummer
 * @version 31-01-2016
 */
public class TerrainTextureDescriptor {

    @Tag(0)
    private long textureChanR;
    @Tag(1)
    private long textureChanG;
    @Tag(2)
    private long textureChanB;
    @Tag(3)
    private long textureChanA;
    @Tag(4)
    private long splatmap;

    public long getTextureChanR() {
        return textureChanR;
    }

    public void setTextureChanR(long textureChanR) {
        this.textureChanR = textureChanR;
    }

    public long getTextureChanG() {
        return textureChanG;
    }

    public void setTextureChanG(long textureChanG) {
        this.textureChanG = textureChanG;
    }

    public long getTextureChanB() {
        return textureChanB;
    }

    public void setTextureChanB(long textureChanB) {
        this.textureChanB = textureChanB;
    }

    public long getTextureChanA() {
        return textureChanA;
    }

    public void setTextureChanA(long textureChanA) {
        this.textureChanA = textureChanA;
    }

    public long getSplatmap() {
        return splatmap;
    }

    public void setSplatmap(long splatmap) {
        this.splatmap = splatmap;
    }

}
