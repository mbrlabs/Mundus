# Mundus [![](https://travis-ci.org/mbrlabs/Mundus.svg?branch=master)](https://travis-ci.org/mbrlabs/Mundus)
Mundus is a 3D world editor, built with Java and LibGDX + VisUI.
The plan is, that the world you create with Mundus can be exported
into a unified format (such as JSON + assets). The exported data can then be imported into any Game Engine,
if someone writes a runtime/parser for that engine.

![Screenshot](https://raw.githubusercontent.com/mbrlabs/Mundus/master/screenshot.png)

This project is at a very early stage in development, so expect large
changes in the future.

## Current features
Please note, that a lot of the UI contains dummy controls for functionality that still needs to be
implemented.
Below are some of the more interesting features, that already work

- Creation of multiple terrains
- Height map loader for terrains
- Procedural terrain generation
- Texture splatting for the terrain texture (max 5 textures per terrain)
- A complete terrain editing system with texture & height brushes
- 4 different brushes (brush form can be an arbitrary image, like in Blender or Gimp)
- 3 brush modes for each brush: Raise/Lower, Flatten & texture paint
- A skybox (not exportable yet)
- Loading of g3db files
- Loading of obj/fbx/dae files (note, that the [fbx-conv](https://github.com/libgdx/fbx-conv) binary must be set in the settings)
- A visual translation & rotation tool (scaling coming soon)
- Multiple scenes in one project
- A component based scene graph (not fully implemented yet)
- Basic export of the project into a json format + assets
- Undo/Redo system for most operations
- Highly accurate game object picking system, based on id color coding & offscreen framebuffer rendering.
  Basic concept: http://www.opengl-tutorial.org/miscellaneous/clicking-on-objects/picking-with-an-opengl-hack/

## Things to consider
- Mundus is constantly changing. Especially the internal representation of save files. At this stage of the project i don't care 
very much for backward compatibility, so don't fall to much in love with your creations if you want to stay up to date ;)
- Depending on your keyboard layout some key shortcuts might be twisted (especially CTRL+Z and CTRL+Y for QWERTZ and QWERTY layouts) 
because of the default GLFW keycode mapping. You can change the layout mapping in the settings dialog under Window -> Settings.

## Runtime
Currently i'm in the progress of implementing the libGDX runtime, which is included in this repository.
Runtimes for other engines/frameworks are not planed in the near future.

## Dev Platforms
Since this is Java (+ libGDX with some native libs) it should run on Windows, Mac & Linux 
(all platforms must support at least OpenGL 3.2). 
I develop on a Linux machine, so the only tested OS is Linux so far.
Android & iOS are obviously not supported as development platforms (but of course as target platform). 

## Building
Gradle is used as build tool. You can simply import this project into Eclipse/Netbeans/IntelliJ as Gradle project
and execute the editor:run task. Dependencies will be fetched automatically.
If you're more of a terminal guy, type "./gradlew editor:run" and hit enter.