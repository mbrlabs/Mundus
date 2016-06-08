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

package com.mbrlabs.mundus.runtime.libgdx;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.FloatArray;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * @author Marcus Brummer
 * @version 31-01-2016
 */
public class TerraLoader {

    public static float[] readTerraFile(FileHandle terra) {
        FloatArray floatArray = new FloatArray();

        DataInputStream is = null;
        try {
            is = new DataInputStream(new BufferedInputStream(
                    new GZIPInputStream(terra.read())));
            while (is.available() > 0) {
                floatArray.add(is.readFloat());
            }
            is.close();
        } catch (EOFException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return floatArray.toArray();
    }

}
