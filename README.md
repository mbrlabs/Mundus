# Mundus
Mundus is a 3D world editor, built with Java and LibGDX.
This project is at a very early stage in development, so expect large
changes in the future.

## Runtime
Currently there is no runtime. The plan is, that the world, you create with Mundus can be exported
into a unified format (such as JSON + assets). The exported data can then be imported into any Game Engine,
if someone writes a runtime/parser for that engine.
Since Mundus is built on top of the awesome libGDX framework, the first runtime that i'm going to write
will be for libGDX.

## Dev Platforms
Since this is Java (+ libGDX) it should run on Windows, Mac & Linux. It is currently only tested on Linux tough.
Android & iOS are obviously not supported as development platform (but of course as target platform). 