# Welcome to Cam Z-Up

Cam Z-Up is a Java-based library for the creative coding environment [Processing](https://processing.org/). Cam Z-Up flips Processing's default projection so that the positive z axis, (0.0, 0.0, 1.0), is the world up axis; the positive y axis, (0.0, 1.0, 0.0), is forward. This library supports two- and three-dimensional graphics. It also supports "2.5D" graphics, where a 3D renderer is configured to appear 2D.

Cam Z-Up is split into two packages: `pfriendly` and `core`. The `pfriendly` package contains code (mostly) compatible with Processing's API. Inside it, you'll find

- `Zup3`, which extends `PGraphicsOpenGL`, like `P3D`;
- `Yup3`, which also extends `PGraphicsOpenGL`;
- `YupJ2`, which extends `PGraphicsJava2D`, a.k.a. `JAVA2D`, the default Processing renderer based on the Java AWT library;
- `Yup2`, which extends `PGraphicsOpenGL`, like `P2D`, a "2.5D" renderer;

The `FX2D` renderer, based on Java FX, is not supported.

This library's `core` package includes basic utilities that were used to modify the Processing renderer. In this package, you'll find classes such as `Vec2`, `Vec3` and `Quaternion`.

This division is a protective measure, so that the library will retain some usefulness in `core` even as bugs in `pfriendly`, or changes to the underlying `Processing` library, cause trouble.

If you can flip the y-axis by supplying `-1` to either [scale](https://processing.org/reference/scale_.html) or the final parameters of [camera](https://processing.org/reference/camera_.html) without adverse impact on your sketch, then you likely don't need this library. While Cam Z-Up may help with more complex sketches, nevertheless, it is a general purpose library: its aim is to make a number of small tasks easier than in vanilla Processing. It will not be as effective as other, more specialized libraries. For an easy mouse-controlled orbital camera with GUI support, I would recommend [peasycam](https://github.com/jdf/peasycam) instead. Other long-standing great libraries are [HE_Mesh](https://github.com/wblut/HE_Mesh) and [ToxicLibs](https://github.com/postspectacular/toxiclibs).

Cam Z-Up is tested with Processing version [4.0 alpha 1](https://github.com/processing/processing4/releases/tag/processing-1270-4.0a1).

## Getting Started

To install this library from Github, 
  1. Click on the green `Clone or download` button in the upper right corner of this repository. 
  2. Select `Download ZIP` to start the download in your browser. (If you know Git or have [Github Desktop](https://desktop.github.com/), you can do that as well.)
  3. Unzip the download.
  4. Navigate the directory `distribution/Camzup-1/download` until you find a `CamZup-1.zip` file.
  5. Extract the `.zip` file to to your `Processing/libraries/` folder.
     1. You've got the right folder if it contains the sub-folders `examples`, `library`, `reference`, `src` and the file `library.properties`.
     2. If you don't know the location of your `Processing/libraries` folder, look up the information in the Processing IDE by going to `File > Preferences`.

Alternatively, you can navigate to the the distribution `.zip` on Github and download just the file you need.

With the library installed, you can set up your Processing sketch like so:

```java
// Import the library
import camzup.pfriendly.*;

void settings() {
  // Supply the renderer's path to size as the
  // third argument.
  size(128, 128, YupJ2.PATH_STR);
}
```

More experienced coders may wish to use [createGraphics](https://processing.org/reference/createGraphics_.html) and/or `getGraphics` to access the renderers directly.

```java
import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 primary;
YupJ2 secondary;

void settings() {
  size(128, 128, YupJ2.PATH_STR);
}

void setup() {
  secondary = (YupJ2)createGraphics(128, 128, YupJ2.PATH_STR);
  primary = (YupJ2)getGraphics();
}
```

Both `createGraphics` and `getGraphics` return `PGraphics`, an `interface`; the result needs to be cast to the specific renderer. The benefit of accessing these renderers directly, rather than through `PApplet` functions, is that the renderers offer a few more conveniences. For example, in the following snippet,

```java
secondary.beginDraw();
secondary.background();
secondary.stroke();
secondary.ellipse(new Vec2(), new Vec2(25.0, 25.0));
secondary.endDraw();

// Ensure that the secondary renderer is not supplied to 
// to the primary until after beginDraw and endDraw are called.
primary.background(#202020);
primary.image(secondary, new Vec2(), new Vec2(50.0, 50.0));
```

`background` and `stroke` use default color arguments, while `ellipse` and `image` support `Vec2` arguments.

## Differences, Problems

Here is a brief list of issues with this library and differences which may be unexpected to new users. Some are unresolved bugs, some arise from the design philosophy of the library.

- 2D & 3D
  - Flipping the axes changes the default rotational direction of a positive angle from clockwise to counter-clockwise.
  - [smooth](https://processing.org/reference/smooth_.html) is disabled for OpenGL renderers.
  - `YupJ2`'s `point` supports `strokeCap(SQUARE)` at the expense of performance.
  - [textureMode](https://processing.org/reference/textureMode_.html) `IMAGE` is not supported by OpenGL renderers; `NORMAL` is the default. This is for three reasons: (1.) the belief that `IMAGE` is _harder_, not easier, to understand; (2.) recognition that `NORMAL` is the standard; (3.) redundant operations in `PGraphicsOpenGL` that interfere with [textureWrap](https://processing.org/reference/textureWrap_.html) `REPEAT` and cannot be overidden by this library.
  -  In making this conversion, support for high density pixel displays may be lost; I cannot test this at the moment, so please report issues with `image`.
  - [textMode](https://processing.org/reference/textMode_.html) `SHAPE` is not supported. However you can retrieve glyph outlines from a [PFont](https://processing.org/reference/PFont.html) with the `TextShape` class from the `pfriendly` package. (Reminder: the `PFont` needs to be loaded with [createFont](https://processing.org/reference/createFont_.html)).
  - The [PShape](https://processing.org/reference/PShape.html) interface has numerous problems stemming from both its implementation and its design. This library uses `Curve` and `Mesh` objects instead. [shapeMode](https://processing.org/reference/shapeMode_.html) is not supported.
  
- 2D
  - Using `YupJ2`'s `rotate` or `rotateZ` will cause shapes with strokes to jitter. A discussion the Processing forum about this issue can be found [here](https://discourse.processing.org/t/text-seems-jittery-when-moving-in-a-circular-pattern/19548).
  - The [arc](https://processing.org/reference/arc_.html) implementation has been changed to `mod` the start and stop angles.
  -  In OpenGL renderers, an arc will not have rounded corners, no matter which [strokeJoin](https://processing.org/reference/strokeJoin_.html) and [strokeCap](https://processing.org/reference/strokeCap_.html) methods you specify.
  - `CORNER` is supported for [rectMode](https://processing.org/reference/rectMode_.html), [ellipseMode](https://processing.org/reference/ellipseMode_.html) and [imageMode](https://processing.org/reference/imageMode_.html). However it is less intuitive with these renderers. For that reason, `CENTER` is the default alignment.
  - `Curve`s and `Mesh`es do not currently distinguish between an outline and contour shape.

- 3D
  - A z-up axis changes the relationship between a 2D vector's polar coordinates and a 3D vector's spherical coordinates: a 3D vector's azimuth matches a 2D vector's heading.
  - Neither 3D primitive, the [sphere](https://processing.org/reference/sphere_.html) nor the [box](https://processing.org/reference/box_.html), are supported; use mesh entities instead.
  - A `Mesh3` material may not have both a fill and a stroke due to flickering in [perspective](https://processing.org/reference/perspective_.html) cameras.

Many core Processing functions are marked `final`, meaning they cannot be extended and modified by classes in this library; many fields are marked `private` meaning they cannot be accessed and mutated. This is the one of the reasons for the differences noted above.

### Math & Geometry Conventions

With the exception of creating `new` objects mentioned above, the goal of this library is to create, not throw exceptions. For that reason some liberties have been taken with mathematics.

- Component-wise multiplication between two vectors -- again, mathematically incorrect -- is assumed to be a shorthand for the multiplication of a vector with a non-uniform scalar, which would more appropriately be stored in a matrix.
- `Utils.acos` and `Utils.asin` clamp the input value to the range `-1.0` to `1.0` so as to avoid exceptions.
- As with Python, JavaScript and OSL, `x != 0` is `true`; `true` is `1` and `false` is `0`.
- For [modulo operations](https://en.wikipedia.org/wiki/Modulo_operation), I follow the GLSL convention of distinguishing `mod` from `fmod`. `fmod` is based on `trunc`, where `fmod(a, b) := a - b * trunc(a / b)`; `mod`, on `floor`, where `mod(a, b) := a - b * floor(a / b)`. In Java, the `%`  operator uses `fmod`. The Java `Math` library supports `floorMod` for `int`s.
- As with shader languages, I try to protect against divide-by-zero errors whenever possible. Though mathematically incorrect, `div(x, 0.0) = 0.0` ; in consequence `fmod(x, 0.0)` and `mod(x, 0.0)` return `x`.
- The [linear interpolation](https://en.wikipedia.org/wiki/Linear_interpolation) (`lerp`) method in this library uses the formula `(1.0 - t) * a + t * b`, not `a + t * (b - a)`. Processing uses the latter. Furthermore, Processing's `lerp` is unclamped by default. This library Includes a clamped and unclamped version of `lerp`; clamped is assumed to be the default.
- I break with GLSL convention when it comes to easing functions. The step provided to easing functions is always a scalar (a `float`). There are no `step`, `smoothStep` and `linearStep` functions which generate the step to be supplied to `mix`. `mix` is, however, is defined in relevant classes.