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

fun isG3DB(filename: String?): Boolean {
    return filename != null && filename.endsWith(FORMAT_3D_G3DB)
}

fun isG3DB(file: FileHandle?): Boolean {
    return file != null && isG3DB(file.name())
}

fun isWavefont(filename: String?): Boolean {
    return filename != null && filename.endsWith(FORMAT_3D_WAVEFONT)
}

fun isWavefont(file: FileHandle?): Boolean {
    return file != null && isWavefont(file.name())
}

fun isCollada(filename: String?): Boolean {
    return filename != null && filename.endsWith(FORMAT_3D_COLLADA)
}

fun isCollada(file: FileHandle?): Boolean {
    return file != null && isCollada(file.name())
}

fun isFBX(filename: String?): Boolean = filename != null && filename.endsWith(FORMAT_3D_FBX)

fun isFBX(file: FileHandle?): Boolean {
    return file != null && isFBX(file.name())
}

fun isG3DJ(filename: String?): Boolean {
    return filename != null && filename.endsWith(FORMAT_3D_G3DJ)
}

fun isG3DJ(file: FileHandle?): Boolean {
    return file != null && isG3DJ(file.name())
}

fun isPNG(file: FileHandle?): Boolean {
    return file != null && isPNG(file.name())
}

fun isPNG(filename: String?): Boolean {
    return filename != null && filename.endsWith(FORMAT_IMG_PNG)
}

fun isJPG(filename: String?): Boolean {
    return filename != null && (filename.endsWith(FORMAT_IMG_JPG) || filename.endsWith(FORMAT_IMG_JPEG))
}

fun isJPG(file: FileHandle?): Boolean {
    return file != null && isJPG(file.name())
}

fun isTGA(filename: String?): Boolean {
    return filename != null && filename.endsWith(FORMAT_IMG_TGA)
}

fun isTGA(file: FileHandle?): Boolean {
    return file != null && isTGA(file.name())
}

fun is3DFormat(filename: String?): Boolean {
    return filename != null && (filename.endsWith(FORMAT_3D_WAVEFONT) || filename.endsWith(FORMAT_3D_COLLADA)
            || filename.endsWith(FORMAT_3D_G3DB) || filename.endsWith(FORMAT_3D_G3DJ)
            || filename.endsWith(FORMAT_3D_FBX))
}

fun is3DFormat(file: FileHandle?): Boolean {
    return file != null && is3DFormat(file.name())
}

fun isImage(filename: String?): Boolean {
    return filename != null && (isJPG(filename) || isPNG(filename) || isTGA(filename))
}

fun isImage(file: FileHandle?): Boolean {
    return file != null && isImage(file.name())
}

