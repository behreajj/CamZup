# Welcome to Cam Z up

The main feature of the Cam Z up library is that it flips Processing's default projection so that the positive z axis, (0.0, 0.0, 1.0), is the world up axis, and the positive y axis, (0.0, 1.0, 0.0), is forward. This library supports 2D, 2.5D and 3D. It is split into two packages: `pfriendly` and `core`. The `pfriendly` package contains code (mostly) compatible with Processing's API. Inside it, you'll find

- `Zup3`, which extends `PGraphics3D`, a.k.a. `P3D`;
- `Yup3`, which also extends `PGraphics3D`.
- `YupJ2`, which extends `PGraphicsJava2D`, a.k.a. `JAVA2D`, the default Processing renderer based on the Java AWT library;
- `Yup2`, which extends `PGraphics2D`, a.k.a. `P2D`, an OpenGL "2.5D" renderer;

The `FX2D` renderer, based on Java FX, is not supported.

This library's `core` package includes basic math utilities that were used to modify the Processing renderer. In this package, you'll find classes like `Vec2`, `Vec3`, `Quaternion` and so on.

## Differences, Issues

Here is a brief list of differences and issues with this library.

- Flipping the axes changes the default rotational direction of a positive angle from clockwise to counter-clockwise.
- The [arc](https://processing.org/reference/arc_.html) implmentation has been changed to `mod` the start and stop angles. It will not have rounded corners in OpenGL renderers, no matter which [strokeJoin](https://processing.org/reference/strokeJoin_.html) and [strokeCap](https://processing.org/reference/strokeCap_.html) methods you specify.
- A z-up axis changes the relationship between a 2D vector's polar coordinates and a 3D vector's spherical coordinates.
- As a consequence of the above, `P3D`'s UV [sphere](https://processing.org/reference/sphere_.html) is tipped on its side with a z-up renderer.
- `CORNER` is supported for [rectMode](https://processing.org/reference/rectMode_.html), [ellipseMode](https://processing.org/reference/ellipseMode_.html) and [imageMode](https://processing.org/reference/imageMode_.html). However it is less intuitive with these renderers. For that reason, `CENTER` is the default alignment.
- [textureMode](https://processing.org/reference/textureMode_.html) `IMAGE` is not supported; `NORMAL` is the default. This is because many redundant operations on UV coordinates interfered with [textureWrap](https://processing.org/reference/textureWrap_.html) `REPEAT`. In making this conversion, support for high density pixel displays may be lost; I cannot test this at the moment, so please report issues with `image`.
- [textMode](https://processing.org/reference/textMode_.html) `SHAPE` is not supported. However you can retrieve glyph outlines from a [PFont](https://processing.org/reference/PFont.html) with the `CurveEntity2` class from the `core` package. (The `PFont` needs to be loaded with [createFont](https://processing.org/reference/createFont_.html)).
- The [PShape](https://processing.org/reference/PShape.html) interface has many problems (among them: attempting to represent both 2D and 3D shapes, a longstanding `rotateZ` bug, difficulty accessing vertices and UV coordinates). This library uses `Curve` and `Mesh` objects instead.
- When you create a new sketch, you extend a `PApplet` class; when run, this creates a primary renderer, [PGraphics](https://processing.org/reference/PGraphics.html). When use a function like `line` or `point` in the sketch's `draw` function, the `PApplet` calls the renderer's `line` or `point`. If you use one of the renderers above, and you want to make a line, you can just type `line`, because both the applet and renderer have that function. However, if you want to use a function that the renderer has but the applet doesn't have, you have to first get a reference to the renderer with [createGraphics](https://processing.org/reference/createGraphics_.html) or `getGraphics`, then call `renderer.specialLine`.

Many core Processing functions are marked `final`, meaning they cannot be extended and modified by libraries; similarly, many fields are marked `private` meaning they cannot be changed. This is the primary reason for differences noted above.

## Coding Style

The following explains some decisions behind the library's code base.

### Mutable vs. Immutable

Classes in the `core` package attempt to compromise between the style of Processing classes like [PVector](https://processing.org/reference/PVector.html) --

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

  // Overload static method with a version that requires no target.
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
  public float getX() {
    return this.x;
  }

  public float getY() {
    return this.y;
  }

  public static Vec2 add(final Vec2 a, final Vec2 b) {
    return new Vec2(a.x + b.x, a.y + b.y);
  }
}
```

Immutable classes have many benefits, but beginning programmers are unlikely to take advantage of them. On the down-side, the immutable `Vec2` is `final` and so cannot be customized with a sub-class; `private final` fields force extra verbiage, as `getX` and `getY` must be typed to get `x` and `y`.

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
Vec2 a = new Vec2(3.0, 4.0);
Vec2 b = new Vec2(4.0, 5.0);
Vec2 sum = new Vec2();
Vec2 prod = new Vec2();

for(int i = 0; i < 15; ++i) {
    println(Vec2.mul(3.0, Vec2.add(a, b, sum), prod));
}
```

where the assignee of an operation's result is declared in advance. You could argue that this is the worst of both worlds, but you're welcome to write your own library.

I have tried to make these classes as extensible as possible. In detail, this means that methods are not marked `private` or `final`. Primitive fields are not `final`; fields that are `Object`s are. Sensitive fields are `protected`, not `private`, so they can be accessed by child classes. So, for example, from within the Processing IDE, you can create

```java
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

### Errata

- The [linear interpolation](https://en.wikipedia.org/wiki/Linear_interpolation) (`lerp`) method in this library uses the formula `(1.0 - t) * a + t * b`, not `a + t * (b - a)`. Processing uses the latter. Furthermore, Processing's `lerp` is unclamped by default. This library Includes a clamped and unclamped version of `lerp`, but clamped is assumed to be the default.
- The formula used for [spherical coordinates](https://en.wikipedia.org/wiki/Spherical_coordinate_system) in 3D is `(rho * cos(theta) * cos(phi), rho * sin(theta) * cos(phi), -rho * sin(phi))`, such that phi, the inclination, is in the range `-PI / 2` to `PI / 2`. At an inclination of zero, a point will lie on the sphere's equator.
- For [modulo operations](https://en.wikipedia.org/wiki/Modulo_operation), the GLSL convention of distinguishing `mod` from `fmod` is followed. `fmod` is based on `trunc`, where `fmod(a, b) := a - b * trunc(a / b)`; `mod`, on `floor`, where `mod(a, b) := a - b * floor(a / b)`. In Java, the `%`  operator uses `fmod`. The Java `Math` library supports `floorMod` for `int`s.
- Processing pre-calculates the values of cosine and sine, then stores them in a [lookup table](https://en.wikipedia.org/wiki/Lookup_table#Computing_sines) (LUT); this is a common technique to speed up the `cos` and `sin` functions in the draw loop. The `pfriendly` package uses `PApplet.cos` and `sin`; the `core` uses the `Math` library `cos` and `sin` functions.
