# Welcome to Cam Z-Up

Table of Contents

- [Welcome to Cam Z-Up](#welcome-to-cam-z-up)
  - [Getting Started](#getting-started)
    - [Installation](#installation)
    - [Structure](#structure)
    - [Usage](#usage)
    - [Chirality](#chirality)
    - [Color](#color)
      - [Palettes](#palettes)
      - [Harmony](#harmony)
      - [Complement Mix Test](#complement-mix-test)
  - [Differences, Problems](#differences-problems)
    - [2D \& 3D](#2d--3d)
    - [2D](#2d)
    - [3D](#3d)
  - [Programming, Math Conventions](#programming-math-conventions)

Cam Z-Up is a Java-based library for the creative coding environment [Processing](https://processing.org/). Cam Z-Up flips Processing's default projection so that the positive z axis, (0.0, 0.0, 1.0), is the world up axis; the positive y axis, (0.0, 1.0, 0.0), is forward. The world origin, (0.0, 0.0, 0.0), is placed at the center of a sketch.

This library supports two- and three-dimensional graphics. In 2D, if the camera is imagined to be above the sketch looking down, the positive y axis is forward. If the camera is imagined to be looking from a sideview, the y axis is up.

If you can flip the y axis by either

  - supplying -1.0 to [scale](https://processing.org/reference/scale_.html)'s y parameter or 
  - supplying (0.0, -1.0, 0.0) to the final parameters of [camera](https://processing.org/reference/camera_.html)
  
without negative impact to your sketch, chances are you don't need this library.

While Cam Z-Up can help with more complex sketches, it is a general purpose library. It aims to make a number of small tasks easier than in vanilla Processing. It will not be as effective as specialist libraries.

Cam Z-Up is tested with Processing version [4.x](https://github.com/processing/processing4/releases). It's been around for a while, so it was overhauled to use the accepted [library template](https://discourse.processing.org/t/a-new-processing-library-template/45390/5) on March 19, 2025,  this [commit]().

## Getting Started

For more thorough information, please refer to the documentation included within the distribution. For examples, see the `examples` directory.

### Installation

If you're not familiar with git and want to acquire this library from Github, 

  1. Click on the green `Code` button in the upper right corner of this repository. 
  2. Select `Download ZIP` to start the download in your browser.
  3. Unzip the download.
  4. Navigate through the directory `build\libs\` until you find a `jar` file.
  5. Drag and drop the `jar` onto an open Processing sketch.

### Structure

Cam Z-Up is split into two packages: `core` and `pfriendly`. The `pfriendly` package contains code compatible with Processing's API. Inside it, you'll find four graphics renderers:

- `Yup3`, which extends `PGraphicsOpenGL`, similar to `P3D`;
- `Zup3`, which also extends `PGraphicsOpenGL`;
- `YupJ2`, which extends `PGraphicsJava2D`, a.k.a. `JAVA2D`, the default Processing renderer based on the Java AWT library;
- `Yup2`, which extends `PGraphicsOpenGL`, similar to `P2D`, a "2.5D" renderer;

The `FX2D` renderer, based on Java FX, is not distributed with Processing, so it's not supported here. The `Yup3` renderer treats the positive y axis, (0.0, 1.0, 0.0), as world up.

This library's `core` package includes basic utilities that were used to modify the Processing renderer. In this package, you'll find classes such as `Vec2`, `Vec3` and `Quaternion`. The division between `pfriendly` and `core` is a protective measure. The aim is to retain the library's usefulness even as bugs in `pfriendly`, or changes to the underlying `Processing` library, cause trouble.

### Usage

With the library installed, you can set up your Processing sketch like so:

```java
// Import the library
import com.behreajj.camzup.pfriendly.*;

void settings() {
  // Supply the renderer's path to size as the
  // third argument.
  size(128, 128, YupJ2.PATH_STR);
}
```

More experienced coders may wish to use [createGraphics](https://processing.org/reference/createGraphics_.html) and/or `getGraphics` to access the renderers directly.

```java
import com.behreajj.camzup.core.*;
import com.behreajj.camzup.pfriendly.*;

YupJ2 graphics;

void settings() {
  size(128, 128, YupJ2.PATH_STR);
}

void setup() {

  // For OpenGL on Macs.
  frameRate(60.0);

  // Cast from PGraphics to your renderer.
  graphics = (YupJ2)getGraphics();
}
```

Both `createGraphics` and `getGraphics` return `PGraphics`; the result needs to be cast to the specific renderer. The benefit of accessing Cam Z-Up renderers directly, rather than through `PApplet`, is that the renderers offer additional conveniences. For example, in the following snippet,

```java
graphics.beginDraw();
graphics.background();
graphics.stroke();
graphics.ellipse(new Vec2(), new Vec2(25.0, 25.0));
graphics.endDraw();
```

`background` and `stroke` use default colors, while `ellipse` and `image` support `Vec2` arguments.

### Chirality

Flipping the y axis changes the default rotational direction of a positive angle from clockwise to counter-clockwise.

![Polar Coordinates](data/polarDiagram.png)

This can be crucial when working with signed angles, such as those returned from `Vec2.headingSigned` or `Utils.atan2`, as it's important to understand the relationship between polar coordinates and the four quadrants of the Cartesian coordinate plane.

![Quadrants](data/quadrantsDiagram.png)

Signed angles can be converted to unsigned angles with [floor modulo](https://en.wikipedia.org/wiki/Modulo_operation), `mod`. This should not be confused with truncation modulo, `fmod`. In Processing's Java mode, `%` is truncation modulo; in Python mode, `%` is floor modulo.

Vertex winding is also affected. Below, a piecewise Bezier curve is used to approximate a circle. The central contour uses the opposite (clockwise) vertex winding.

![Winding](data/windingDiagram.png)

A renderer's vertex winding rule will dictate the fill of contours with the same winding. For more information, see the [non-zero](https://en.wikipedia.org/wiki/Nonzero-rule) and the [even-odd](https://en.wikipedia.org/wiki/Even%E2%80%93odd_rule) rule.

In 3D, the orientation of a spherical coordinate system depends on the up axis. For z-up, the equator of a spherical system rests on the camera's horizon; for y-up, the poles rest on the camera's horizon. The image below shows z-up.

![Spherical Coordinates](data/sphericalDiagram.png)

Negative inclinations, in the range [`-PI / 2.0`, `0.0`] will fall beneath the equator; positive inclinations, in the range [`0.0`, `PI / 2.0`], will fall above the equator. An inclination of `-PI / 2.0` will return the South pole; an inclination of `PI / 2.0` will return the North pole. A positive azimuth will head East from the prime meridian; a negative azimuth will head West.

In OpenGL renderers, texture coordinates default to `NORMAL` [textureMode](https://processing.org/reference/textureMode_.html). `IMAGE` is not supported. This is for three reasons: (1.) the belief that `IMAGE` is harder, not easier, to understand; (2.) recognition that `NORMAL` is standard; (3.) methods in `PGraphicsOpenGL` interfere with [textureWrap](https://processing.org/reference/textureWrap_.html) `REPEAT` and cannot be overidden by this library. That aside, as per usual, texture coordinates begin at (0.0, 0.0) in the top-left corner and end at (1.0, 1.0) in the bottom-right corner of the image.

### Color

I am not versed in color science, nor do I pretend to be. However, vanilla Processing's approach to color can't not be addressed, so I've introduced enough to allow users to get the job done, namely an `Rgb` and `Gradient` class. Color is not the central focus of this library and I'm not interested in debating the "correct" way to mix it. Do not use this library for advanced or photorealistic color work.

#### Palettes

The `Gradient` class allows you to create color ramps, including the following:

![Palette Diagram](data/paletteDiagram.png)

Viridis and Magma are color palettes used in data visualizations. [Sepia](https://en.wikipedia.org/wiki/Sepia_(color)) and [cyanotype](https://en.wikipedia.org/wiki/Cyanotype) replicate older photographic printing processes.

#### Harmony

RYB color is included above because popular tutorials on "Color Harmony" (or "Color Theory") often assume a subtractive red-yellow-blue color model, even in the context of digital media. Processing defaults to additive sRGB, where cyan (`#00ffff`) is the complement of red (`#ff0000`), not green. This holds regardless of whether you use the `HSB` or the `RGB` [colorMode](https://processing.org/reference/colorMode_.html).

![Triadic](data/triadicDiagram.png)

This RYB wheel's limitations should be apparent from the above. Oranges are dilated while blues are compressed. Brighter greens, cyans and magentas are not achievable. Blues and greens are desaturated. [This](https://youtu.be/YeI6Wqn4I78) tutorial on harmonies is one of the better I've found.

#### Complement Mix Test

A quick heuristic to decide if you are blending colors as you prefer is to take two complementary colors - typically red and green - which you predict will yield an ugly blend and sample them.

![Mix Diagram](data/mixDiagram.png)

## Differences, Problems

Here is a brief list of issues with this library and differences which may be unexpected. Some are unresolved bugs, some arise from the design philosophy of the library.

### 2D & 3D
  
  - Support for high density pixel displays may be lost; I cannot test this at the moment, so please report issues with `image`.
  - The [arc](https://processing.org/reference/arc_.html) implementation has been changed to `mod` the start and stop angles. It no longer responds to [ellipseMode](https://processing.org/reference/ellipseMode_.html); `RADIUS` is the default behavior. When given nonuniform scales, the minimum is taken.
  - The [PShape](https://processing.org/reference/PShape.html) class has numerous problems stemming from both its implementation and its design. I encourage using `CurveEntity` and `MeshEntity` objects excepting the case where high poly count `PShapeOpenGL`s are more performant.
  - [shapeMode](https://processing.org/reference/shapeMode_.html) is not supported.
  - [textMode](https://processing.org/reference/textMode_.html) `SHAPE` is not supported. However you can retrieve glyph outlines from a [PFont](https://processing.org/reference/PFont.html) with the `TextShape` class from the `pfriendly` package. (Reminder: the `PFont` needs to be loaded with [createFont](https://processing.org/reference/createFont_.html)).
  - Color methods no longer promote `int`s in `[0, 255]` to gray colors. Use `float`s or `Rgb` objects instead.
  
### 2D
  - The `image` function for `PGraphicsJava2D` is ineffective, both in terms of frame rate and appearance. I recommend that an OpenGL renderer be used instead. Alternatively, rescale images to display size and tint them in a raster image editor. I have made an image function which removes some of the padding around the native renderer's image function in cases where a `PImage` can be converted to a `java.awt.Image` in `setup`.
  - As a consequence of how `image` function works above, dynamic `tint`ing is no longer supported in `YupJ2`.
  - Using `YupJ2`'s `rotate` or `rotateZ` will cause shapes with strokes to jitter.
  - `CORNER` is supported for [rectMode](https://processing.org/reference/rectMode_.html), `ellipseMode` and [imageMode](https://processing.org/reference/imageMode_.html). However it is less intuitive with this library. For that reason, `CENTER` is the default alignment.
  - OpenGL renderers do not recognize contours in meshes and curves.
  
### 3D
  - Neither 3D primitive, [sphere](https://processing.org/reference/sphere_.html) and [box](https://processing.org/reference/box_.html), are supported; use `MeshEntity3`s instead.
  - A `Mesh3` material may not have both a fill and a stroke due to flickering in [perspective](https://processing.org/reference/perspective_.html) cameras.

Many core Processing functions are marked `final`, meaning they cannot be extended and modified by classes in this library; many fields are marked `private` meaning they cannot be accessed and/or mutated. This is the one of the reasons for the limitations above.

## Programming, Math Conventions

`null`-checks excepted, the goal of this library is not to throw exceptions, but to create. For that reason some liberties have been taken with mathematics.

- `acos` and `asin` clamp the input value to the range `-1.0` to `1.0` so as to avoid exceptions.
- As with Python, JavaScript and OSL, `x != 0` is `true`; `true` is `1` and `false` is `0`.
- Where possible, `Vec2`, `Vec3` and `Vec4` parallel GLSL's `bvec`. Examples include: `Vec2 c = Vec2.lt(new Vec2(false, true), new Vec2(true, true));` and `boolean d = Vec2.any(c);`.
- As with shader languages, I try to protect against divide-by-zero errors when possible. Though mathematically incorrect, `div(x, 0.0) = 0.0` ; in consequence `fmod(x, 0.0)` and `mod(x, 0.0)` return `x`.
- Unlike GLSL, `fract` is defined as `x - trunc(x)`, not `x - floor(x)`. This library refers to the latter as `mod1`.
- The [linear interpolation](https://en.wikipedia.org/wiki/Linear_interpolation) (`lerp`) method in this library uses the formula `(1.0 - t) * a + t * b`, not `a + t * (b - a)`. Processing uses the latter. Furthermore, Processing's `lerp` is unclamped by default. This library Includes a clamped and unclamped version of `lerp`; clamped is assumed to be the default.
- The step provided to easing functions is always a scalar (a `float`). There are no `step`, `smoothstep` and `linearstep` functions which generate the step to be supplied to `mix`. `mix` is, however, is defined in relevant classes.
- A quaternion's real component is assumed to be its first element, `{ w, x, y, z }`. Its imaginary components are stored in a vector. This is in contrast to other APIs, such as [Unity](https://docs.unity3d.com/ScriptReference/Quaternion.html)'s.
- The convention established by Java's `indexOf` function is to return `-1` when an array or collection does not contain a query. Some collections in this library, particularly `Mesh`s and `Curve`s, match Pythonic idiom insofar as they accept negative indices to `get` functions. For example, `curve.get(-1)` will return the last `Knot` in a curve, provided that it is a `closedLoop`. As a consequence, the reciprocity between Java's `indexOf` and `get` is broken. For example: `curve.get(curve.knots.indexOf(elmNotInCurve)) != elmNotInCurve`. For this reason, `contains` should always be preferred over `indexOf`, and no custom `contains` method should depend on `indexOf` unless `get`s definition is guaranteed.
- Between two vectors, the [Hadamard product](https://en.wikipedia.org/wiki/Hadamard_product_(matrices)), is the default multiplication associated with the `*` operator.

🇹🇼 🇺🇦