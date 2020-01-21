# Welcome to Cam Z-Up

Cam Z-Up is a Java-based library for the creative coding environment [Processing](https://processing.org/). The main feature of the Cam Z-Up library is that it flips Processing's default projection so that the positive z axis, (0.0, 0.0, 1.0), is the world up axis; the positive y axis, (0.0, 1.0, 0.0), is forward. This library supports two- and three-dimensional graphics. It also supports "2.5D" graphics, where a 3D renderer is configured to appear 2D. It is split into two packages: `pfriendly` and `core`. The `pfriendly` package contains code (mostly) compatible with Processing's API. Inside it, you'll find

- `Zup3`, which extends `PGraphics3D`, a.k.a. `P3D`;
- `Yup3`, which also extends `PGraphics3D`.
- `YupJ2`, which extends `PGraphicsJava2D`, a.k.a. `JAVA2D`, the default Processing renderer based on the Java AWT library;
- `Yup2`, which extends `PGraphics2D`, a.k.a. `P2D`, an OpenGL "2.5D" renderer;

The `FX2D` renderer, based on Java FX, is not supported.

This library's `core` package includes basic utilities that were used to modify the Processing renderer. In this package, you'll find classes such as `Vec2`, `Vec3`, `Quaternion`.

## Getting Started

To get started with this library you can set up your Processing sketch like so:

```java
// Import the library
import camzup.pfriendly.*;

void setup() {
  // Supply the renderer's path to size as the
  // third argument.
  size(128, 128, "camzup.pfriendly.YupJ2");
}
```

Experienced coders may wish to use [createGraphics](https://processing.org/reference/createGraphics_.html) and/or `getGraphics` to access the renderers directly.

```java
import camzup.pfriendly.*;
import camzup.core.*;

String rpath = "camzup.pfriendly.YupJ2";
YupJ2 primary;
YupJ2 secondary;

void setup() {
  size(128, 128, rpath);
  secondary = (YupJ2)createGraphics(256, 256, rpath);
  primary = (YupJ2)getGraphics();
}
```

Both `createGraphics` and `getGraphics` return `PGraphics`, an `interface`; the result of these function calls needs to be cast to the specific renderer. The benefit of accessing these renderers directly, rather than through `PApplet` functions, is that more convenience functions are available. For example, in the following snippet,

```java
secondary.beginDraw();
secondary.background();
secondary.stroke();
secondary.ellipse(new Vec2(), new Vec2(25.0, 25.0));
secondary.endDraw();

primary.background(0xff202020);
primary.image(secondary, new Vec2(), new Vec2(50.0, 50.0));
```

`background` and `stroke` use default colors, while `ellipse` and `image` support `Vec2` arguments.

Please see the examples folder for more possibilities.

## Differences, Issues

Here is a brief list of issues with this library and differences which are not bugs but may be unexpected to new users.

- Flipping the axes changes the default rotational direction of a positive angle from clockwise to counter-clockwise.
- A z-up axis changes the relationship between a 2D vector's polar coordinates and a 3D vector's spherical coordinates.
- As a consequence of the above, `P3D`'s UV [sphere](https://processing.org/reference/sphere_.html) is tipped on its side with a z-up renderer.
- The [arc](https://processing.org/reference/arc_.html) implementation has been changed to `mod` the start and stop angles.
- `CORNER` is supported for [rectMode](https://processing.org/reference/rectMode_.html), [ellipseMode](https://processing.org/reference/ellipseMode_.html) and [imageMode](https://processing.org/reference/imageMode_.html). However it is less intuitive with these renderers. For that reason, `CENTER` is the default alignment.
-  In OpenGL renderers, an arc will not have rounded corners, no matter which [strokeJoin](https://processing.org/reference/strokeJoin_.html) and [strokeCap](https://processing.org/reference/strokeCap_.html) methods you specify.
- Using `YupJ2`'s `rotate` or `rotateZ` will cause shapes with strokes to jitter.
- `YupJ2`'s `point` supports `strokeCap(SQUARE)` at the expense of performance.
- [textureMode](https://processing.org/reference/textureMode_.html) `IMAGE` is not supported; `NORMAL` is the default. This is because many redundant operations on UV coordinates interfered with [textureWrap](https://processing.org/reference/textureWrap_.html) `REPEAT`. In making this conversion, support for high density pixel displays may be lost; I cannot test this at the moment, so please report issues with `image`.
- [textMode](https://processing.org/reference/textMode_.html) `SHAPE` is not supported. However you can retrieve glyph outlines from a [PFont](https://processing.org/reference/PFont.html) with the `CurveEntity2` class from the `core` package. (The `PFont` needs to be loaded with [createFont](https://processing.org/reference/createFont_.html)).
- The [PShape](https://processing.org/reference/PShape.html) interface has many problems (among them: attempting to represent both 2D and 3D shapes, a `rotateZ` bug as of version 3.5.x, difficulty accessing vertices and UV coordinates). This library uses `Curve` and `Mesh` objects instead.
- Processing pre-calculates the values of cosine and sine, then stores them in a [lookup table](https://en.wikipedia.org/wiki/Lookup_table#Computing_sines) (LUT); this is a common technique to speed up the `cos` and `sin` functions in the draw loop. The `pfriendly` package uses `PApplet.cos` and `sin`; the `core` uses the `Math` library `cos` and `sin` functions.

Many core Processing functions are marked `final`, meaning they cannot be extended and modified by classes in this library; similarly, many fields are marked `private` meaning they cannot be accessed and mutated. This is the one of the reasons for the differences noted above.

## Coding Style

The following explains some decisions behind how the library's code base is written.

### Mutable vs. Immutable

Classes in the `core` package attempt to compromise between the design of Processing classes like [PVector](https://processing.org/reference/PVector.html) --

```java
public class PVector {
  public float x, y, z;

  public PVector add(PVector b) {
    return this.add(b.x, b.y, b.z);
  }

  public PVector add(float x, float y, float z) {
    this.x += x; this.y += y; this.z += z;
    return this;
  }

  // Overload static method to supply a null to target.
  public static PVector add(PVector a, PVector b) {
    return PVector.add(a, b, (PVector)null);
  }

  public static PVector add(PVector a, PVector b, PVector target) {
    // Create a new object if target is null.
    if(target == null) target = new PVector();
    return target.set(a.x + b.x, a.y + b.y, a.z + b.z);
  }
}
```

-- and the current trend towards immutable classes --

```java
// Class is final.
public final class Vec2 {

  // Fields are private and final.
  private final float x, y;

  public Vec2(final float x, final float y) {
    this.x = x; this.y = y;
  }

  // Public accessor methods.
  public float x() {
    return this.x;
  }

  public float y() {
    return this.y;
  }

  public static Vec2 add(final Vec2 a, final Vec2 b) {
    return new Vec2(a.x + b.x, a.y + b.y);
  }
}
```

Immutable classes have many benefits, but beginning programmers are unlikely to take advantage of them. On the down-side, the immutable `Vec2` is `final` and so cannot be customized with a sub-class.

The mutable `PVector`, on the other hand, requires four times the maintenance for the same `add` function. The `static` version is overloaded with an out parameter pattern (or anti-pattern, depending on your opinion). This makes it easier to reuse placeholders when iterating over an array of vectors and performing multiple operations. However, it seems to me that, if a I pass a `null` vector to such a function, and the point is to learn to manage object creation, the function should _fail loudly_. For those reasons, I settled on the following:

```java
public class Vec2 {
  public float x, y;

  public static Vec2 add(final Vec2 a, final Vec2 b, final Vec2 target) {
    return target.set(a.x + b.x, a.y + b.y);
  }
}
```

Public fields are mutable, but changing a vector in-place is discouraged through the absence of instance methods. `static` methods, with some exceptions, require an output argument and will not instantiate one for you. The usage pattern for classes in the `core` package generally look like

```java
import camzup.core.*;

Vec2 a = new Vec2(3.0, 4.0);
Vec2 b = new Vec2(4.0, 5.0);
Vec2 sum = new Vec2();
Vec2 prod = new Vec2();

for(int i = 0; i < 15; ++i) {
    println(Vec2.mul(3.0, Vec2.add(a, b, sum), prod));
}
```

where the assignee of an operation's result is declared in advance. In the above example, the cost of vector operations in a for-loop are mitigated by not having to create a `new Vec2()` for `sum` and `prod` on each iteration. You could argue that this is the worst of both worlds; in that case you're welcome to write your own library.

I have tried to make these classes as extensible as possible. This means that methods are marked neither `private` nor `final`. Primitive fields are not `final`; fields that are `Object`s are `final`. Sensitive fields are `protected`, not `private`, so they can be accessed by child classes. From within the Processing IDE, you can extend a Cam Z-Up class

```java
import camzup.core.*;

// setup is needed when making your own classes.
void setup() {
}

// Classes made in the PDE should be marked static,
// as they are actually inner classes.
static class MyVec2 extends Vec2 {
  MyVec2 add(float x, float y) {
    this.x += x; this.y += y;
    return this;
  }
}
```

if you want to add instance methods, or any other functionality.

### Math & Geometry Conventions

With the exception of creating `new` objects mentioned above, the goal of this library is to create images, not throw exceptions. For that reason some liberties have been taken with mathematics.

- The [linear interpolation](https://en.wikipedia.org/wiki/Linear_interpolation) (`lerp`) method in this library uses the formula `(1.0 - t) * a + t * b`, not `a + t * (b - a)`. Processing uses the latter. Furthermore, Processing's `lerp` is unclamped by default. This library Includes a clamped and unclamped version of `lerp`, but clamped is assumed to be the default.
- I break with GLSL convention when it comes to easing functions. The step provided to easing functions is always a scalar (a `float`). There are no `step`, `smoothStep` and `linearStep` functions which generate the step to be supplied to `mix`. `mix` is, however, is defined in classes where it makes sense.
- The formula used for [spherical coordinates](https://en.wikipedia.org/wiki/Spherical_coordinate_system) in 3D is `(rho * cos(theta) * cos(phi), rho * sin(theta) * cos(phi), -rho * sin(phi))`, such that phi, the inclination, is in the range `-PI / 2` to `PI / 2`. At an inclination of zero, a point will lie on the sphere's equator. Other implementations will use the range `0.0` to `PI` for phi, where `PI / 2` is the equator.
- For [modulo operations](https://en.wikipedia.org/wiki/Modulo_operation), I follow the GLSL convention of distinguishing `mod` from `fmod`. `fmod` is based on `trunc`, where `fmod(a, b) := a - b * trunc(a / b)`; `mod`, on `floor`, where `mod(a, b) := a - b * floor(a / b)`. In Java, the `%`  operator uses `fmod`. The Java `Math` library supports `floorMod` for `int`s.
- As with shader languages, I try to protect against divide-by-zero errors whenever possible. Though mathematically incorrect, `div(x, 0.0) = 0.0` ; in consequence `fmod(x, 0.0)` and `mod(x, 0.0)` return `x`.
- Component-wise multiplication between two vectors -- again, mathematically incorrect -- is assumed to be a shorthand for the multiplication of a vector with a non-uniform scalar, which would more appropriately be stored in a matrix.
- `Utils.acos` and `Utils.asin` clamp the input value to the range `-1.0` to `1.0` so as to avoid exceptions.
- As with Python, JavaScript and OSL, `x != 0` is `true`, `true` is `1` and `false` is `0`.