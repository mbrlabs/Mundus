# Mundus
Mundus is a 3D world editor, built with Java and LibGDX.
The plan is, that the world you create with Mundus can be exported
into a unified format (such as JSON + assets). The exported data can then be imported into any Game Engine,
if someone writes a runtime/parser for that engine.

This project is at a very early stage in development, so expect large
changes in the future.

## Current features
Please note, that a lot of the UI contains dummy controls for functionality that still needs to be
implemented.
Below are some of the more interesting features, that already work

- Creation of multiple terrains
- Basic support for loading heightmaps
- Editing terrain height with brushes
- A skybox (not exportable yet)
- Loading of g3db files
- Loading of obj/fbx/dae files (note, that the [fbx-conv](https://github.com/libgdx/fbx-conv) binary must be set in the settings)
- Placement of models in the world
- Basic export of the project into a json format + assets

## Runtime
Currently there is no runtime.
Since Mundus is built on top of the awesome libGDX framework, the first runtime that i'm going to write
will be for libGDX.

## Dev Platforms
Since this is Java (+ libGDX with some native libs) it should run on Windows, Mac & Linux 
(all platforms must support at least OpenGL ES 2). 
I develop on a Linux machine, so the only tested OS is Linux so far.
Android & iOS are obviously not supported as development platforms (but of course as target platform). 

## Building
Gradle is used as build tool. You can simply import this project into Eclipse/Netbeans/IntelliJ as Gradle project
and execute the editor:run task. Dependencies will be fetched automagically.
If you're more of a terminal guy, type "gradle editor:run" and hit enter.