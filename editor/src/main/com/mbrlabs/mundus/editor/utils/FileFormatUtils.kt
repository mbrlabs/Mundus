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
@file:JvmName("FileFormatUtils")

package com.mbrlabs.mundus.editor.utils

import com.badlogic.gdx.files.FileHandle

const val FORMAT_3D_G3DB = "g3db"
const val FORMAT_3D_G3DJ = "g3dj"
const val FORMAT_3D_COLLADA = "dae"
const val FORMAT_3D_WAVEFONT = "obj"
const val FORMAT_3D_FBX = "fbx"

const val FORMAT_IMG_PNG = "png"
const val FORMAT_IMG_JPG = "jpg"
const val FORMAT_IMG_JPEG = "jpeg"
const val FORMAT_IMG_TGA = "tga"

fun isG3DB(filename: String) = filename.toLowerCase().endsWith(FORMAT_3D_G3DB)
fun isG3DB(file: FileHandle) = isG3DB(file.name())
fun isWavefont(filename: String) = filename.toLowerCase().endsWith(FORMAT_3D_WAVEFONT)
fun isWavefont(file: FileHandle) = isWavefont(file.name())
fun isCollada(filename: String) = filename.toLowerCase().endsWith(FORMAT_3D_COLLADA)
fun isCollada(file: FileHandle) = isCollada(file.name())
fun isFBX(filename: String) = filename.toLowerCase().endsWith(FORMAT_3D_FBX)
fun isFBX(file: FileHandle) = isFBX(file.name())
fun isG3DJ(filename: String) = filename.toLowerCase().endsWith(FORMAT_3D_G3DJ)
fun isG3DJ(file: FileHandle) = isG3DJ(file.name())
fun isPNG(file: FileHandle) = isPNG(file.name())
fun isPNG(filename: String) = filename.toLowerCase().endsWith(FORMAT_IMG_PNG)
fun isJPG(file: FileHandle) = isJPG(file.name())
fun isTGA(filename: String) = filename.toLowerCase().endsWith(FORMAT_IMG_TGA)
fun isTGA(file: FileHandle) = isTGA(file.name())
fun is3DFormat(file: FileHandle) = is3DFormat(file.name())
fun isImage(file: FileHandle) = isImage(file.name())

fun isJPG(filename: String): Boolean {
    val fn = filename.toLowerCase()
    return (fn.endsWith(FORMAT_IMG_JPG) || fn.endsWith(FORMAT_IMG_JPEG))
}

fun is3DFormat(filename: String): Boolean {
    val fn = filename.toLowerCase()
    return fn.endsWith(FORMAT_3D_WAVEFONT) || fn.endsWith(FORMAT_3D_COLLADA) || fn.endsWith(FORMAT_3D_G3DB)
            || fn.endsWith(FORMAT_3D_G3DJ) || fn.endsWith(FORMAT_3D_FBX)
}

fun isImage(filename: String): Boolean {
    val fn = filename.toLowerCase()
    return fn.endsWith(FORMAT_IMG_TGA) || fn.endsWith(FORMAT_IMG_JPEG)
        || fn.endsWith(FORMAT_IMG_JPG) || fn.endsWith(FORMAT_IMG_PNG)
}

