package camzup.pfriendly;

import java.awt.Image;
import java.util.function.Function;

import processing.core.PApplet;
import processing.core.PImage;

import camzup.core.Chainable;
import camzup.core.Color;
import camzup.core.Gradient;
import camzup.core.IUtils;
import camzup.core.Sdf;
import camzup.core.Utils;
import camzup.core.Vec2;

/**
 * An extension of PImage, designed primarily around static methods
 * that generate color gradients similar to those provided in HTML5
 * canvas and CSS.
 */
public class ZImage extends PImage {

  /**
   * A temporary color used for converting color data to and from
   * hexadecimal values.
   */
  private static final Color clr = new Color();

  /**
   * The default constructor.
   */
  protected ZImage ( ) { super(); }

  /**
   * Constructs an image from the native AWT image.
   *
   * @param img the image
   */
  public ZImage ( final Image img ) { super(img); }

  /**
   * Constructs an image from its dimensions, width and height.
   *
   * @param width  the image width
   * @param height the image height
   */
  public ZImage (
      final int width,
      final int height ) {

    super(width, height);
  }

  /**
   * Constructs an image from the dimensions and format.
   *
   * @param width  the image width
   * @param height the image height
   * @param format the format
   */
  public ZImage (
      final int width,
      final int height,
      final int format ) {

    super(width, height, format);
  }

  /**
   * Constructs an image from the dimensions, format and pixel density.
   *
   * @param width        the image width
   * @param height       the image height
   * @param format       the format
   * @param pixelDensity the pixel density
   */
  public ZImage (
      final int width,
      final int height,
      final int format,
      final int pixelDensity ) {

    super(width, height, format, pixelDensity);
  }

  /**
   * Gets the parent applet of this PImage.
   *
   * @return the parent
   */
  PApplet getParent ( ) { return this.parent; }

  /**
   * Sets the parent of this PImage. The parent reference is needed for
   * the save function.
   *
   * @param parent the PApplet
   * @return this image
   */
  @Chainable
  PImage setParent ( final PApplet parent ) {

    this.parent = parent;
    return this;
  }

  /**
   * Finds the aspect ratio of an image, it's width divided by its
   * height.
   *
   * @return the aspect ratio
   */
  public float aspect ( ) {

    return Utils.div(this.width, this.height);
  }

  /**
   * Returns a string representation of an image, including its format,
   * width, height and pixel density.
   *
   * @return the string
   */
  @Override
  public String toString ( ) {

    return ZImage.toString(this);
  }

  /**
   * Update the pixels[] buffer to the PImage.
   *
   * The overridden functionality eliminates unnecessary checks.
   */
  @Override
  public void updatePixels ( ) {

    if ( !this.modified ) {

      this.mx1 = 0;
      this.mx2 = this.pixelWidth;
      this.my1 = 0;
      this.my2 = this.pixelHeight;
      this.modified = true;

    } else {

      if ( 0 < this.mx1 ) { this.mx1 = 0; }
      if ( 0 > this.mx2 ) {
        this.mx2 = this.pixelWidth <= 0 ? this.pixelWidth : 0;
      }

      if ( 0 < this.my1 ) { this.my1 = 0; }
      if ( 0 > this.my2 ) {
        this.my2 = this.pixelHeight <= 0 ? this.pixelHeight : 0;
      }

      if ( this.pixelWidth < this.mx1 ) {
        this.mx1 = 0 >= this.pixelWidth ? 0 : this.pixelWidth;
      }
      if ( this.pixelWidth > this.mx2 ) { this.mx2 = this.pixelWidth; }

      if ( this.pixelHeight < this.my1 ) {
        this.my1 = 0 >= this.pixelHeight ? 0 : this.pixelHeight;
      }
      if ( this.pixelHeight > this.my2 ) { this.my2 = this.pixelHeight; }
    }
  }

  /**
   * Generates a conic gradient, where the factor rotates on the z axis
   * around an origin point.
   *
   * @param xOrigin the origin x coordinate
   * @param yOrigin the origin y coordinate
   * @param radians the angular offset
   * @param grd     the gradient
   * @param target  the output image
   * @return the image
   */
  public static PImage conic (
      final float xOrigin,
      final float yOrigin,
      final float radians,
      final Gradient grd,
      final PImage target ) {

    target.loadPixels();

    final int h = target.height;
    final int w = target.width;
    final int[] pixels = target.pixels;

    final float hInv = 1.0f / (h - 1.0f);
    final float wInv = 1.0f / (w - 1.0f);

    for ( int i = 0, y = 0; y < h; ++y ) {

      final float yn = y * hInv;
      final float t = 1.0f - (yn + yn + yOrigin);

      for ( int x = 0; x < w; ++x, ++i ) {

        final float xn = x * wInv;
        final float s = xn + xn - xOrigin - 1.0f;

        grd.eval(Sdf.conic(s, t, radians), ZImage.clr);
        pixels[i] = Color.toHexInt(ZImage.clr);
      }
    }
    target.updatePixels();
    return target;
  }

  /**
   * Generates a conic gradient, where the factor rotates on the z axis
   * around an origin point.
   *
   * @param origin  the origin
   * @param radians the angular offset
   * @param grd     the gradient
   * @param target  the output image
   * @return the image
   */
  public static PImage conic (
      final Vec2 origin,
      final float radians,
      final Gradient grd,
      final PImage target ) {

    return ZImage.conic(
        origin.x, origin.y,
        radians, grd, target);
  }

  /**
   * Recolors an image in-place with a color gradient. The color is
   * converted to a factor in [0.0, 1.0] by an evaluation function.
   *
   * @param grd     the color gradient
   * @param clrEval the color evaluator
   * @param target  the target image
   * @return the augmented image
   */
  public static PImage falseColor (
      final Gradient grd,
      final Function < Integer, Float > clrEval,
      final PImage target ) {

    target.loadPixels();
    final int[] px = target.pixels;
    final int len = px.length;
    for ( int i = 0; i < len; ++i ) {
      grd.eval(
          clrEval.apply(px[i]),
          ZImage.clr);
      px[i] = Color.toHexInt(ZImage.clr);
    }
    target.updatePixels();
    return target;
  }

  /**
   * Recolors an image in-place with a color gradient. The image's
   * luminance is used as the factor.
   *
   * @param grd    the color gradient
   * @param target the target image
   * @return the augmented image
   */
  public static PImage falseColor (
      final Gradient grd,
      final PImage target ) {

    target.loadPixels();
    final int[] px = target.pixels;
    final int len = px.length;
    for ( int i = 0; i < len; ++i ) {
      grd.eval(
          Color.luminance(px[i]),
          ZImage.clr);
      px[i] = Color.toHexInt(ZImage.clr);
    }
    target.updatePixels();
    return target;
  }

  /**
   * Fills an image in place with a color.
   *
   * @param target the target image
   * @param clr    the fill color
   * @return the image
   */
  public static PImage fill (
      final Color clr,
      final PImage target ) {

    return ZImage.fill(Color.toHexInt(clr), target);
  }

  /**
   * Fills an image with a gradient in place. The gradient is
   * horizontal.
   *
   * @param grd    the gradient
   * @param target the target image
   * @return the image
   */
  public static PImage fill (
      final Gradient grd,
      final PImage target ) {

    target.loadPixels();

    final int h = target.height;
    final int w = target.width;
    final int[] pixels = target.pixels;

    final float wInv = 1.0f / (w - 1.0f);
    for ( int i = 0, y = 0; y < h; ++y ) {
      for ( int x = 0; x < w; ++x, ++i ) {
        grd.eval(x * wInv, ZImage.clr);
        pixels[i] = Color.toHexInt(ZImage.clr);
      }
    }

    target.updatePixels();
    return target;
  }

  /**
   * Fills an image with a color in place.
   *
   * @param clr    the fill color
   * @param target the target image
   * @return the image
   */
  public static PImage fill (
      final int clr,
      final PImage target ) {

    target.loadPixels();
    final int[] px = target.pixels;
    final int len = px.length;
    for ( int i = 0; i < len; ++i ) {
      px[i] = clr;
    }
    target.updatePixels();
    return target;
  }

  /**
   * Generates a linear gradient from an origin point to a destination
   * point. The value is clamped to a range [0.0, 1.0] .
   *
   * @param xOrigin the origin x coordinate
   * @param yOrigin the origin y coordinate
   * @param xDest   the destination x coordinate
   * @param yDest   the destination y coordinate
   * @param grd     the gradient
   * @param target  the output image
   * @return the image
   */
  public static PImage linear (
      final float xOrigin,
      final float yOrigin,
      final float xDest,
      final float yDest,
      final Gradient grd,
      final PImage target ) {

    target.loadPixels();

    final int h = target.height;
    final int w = target.width;
    final int[] pixels = target.pixels;

    final float hInv = 1.0f / (h - 1.0f);
    final float wInv = 1.0f / (w - 1.0f);

    final float bx = xOrigin - xDest;
    final float by = yOrigin - yDest;
    final float bbInv = 1.0f
        / Utils.max(IUtils.DEFAULT_EPSILON, bx * bx + by * by);

    for ( int i = 0, y = 0; y < h; ++y ) {

      final float yn = y * hInv;
      final float ay = yOrigin - (1.0f - (yn + yn));

      for ( int x = 0; x < w; ++x, ++i ) {

        final float xn = x * wInv;
        final float ax = xOrigin - (xn + xn - 1.0f);

        grd.eval(Utils.clamp01((ax * bx + ay * by) * bbInv), ZImage.clr);
        pixels[i] = Color.toHexInt(ZImage.clr);
      }
    }

    target.updatePixels();
    return target;
  }

  /**
   * Generates a linear gradient from an origin point to a destination
   * point. The value is clamped to a range [0.0, 1.0] .
   *
   * @param origin the origin
   * @param dest   the destination
   * @param grd    the gradient
   * @param target the output image
   * @return the image
   */
  public static PImage linear (
      final Vec2 origin,
      final Vec2 dest,
      final Gradient grd,
      final PImage target ) {

    return ZImage.linear(
        origin.x, origin.y,
        dest.x, dest.y,
        grd, target);
  }

  /**
   * Generates a radial gradient.
   *
   * This does not account for aspect ratio, so an image that is not 1:1
   * will result in an ellipsoid.
   *
   * @param xOrigin the x coordinate
   * @param yOrigin the y coordinate
   * @param radius  the radius
   * @param grd     the gradient
   * @param target  the output image
   * @return the image
   */
  public static PImage radial (
      final float xOrigin,
      final float yOrigin,
      final float radius,
      final Gradient grd,
      final PImage target ) {

    target.loadPixels();

    final int h = target.height;
    final int w = target.width;
    final int[] pixels = target.pixels;

    final float hInv = 1.0f / (h - 1.0f);
    final float wInv = 1.0f / (w - 1.0f);

    final float r2 = radius + radius;
    final float invrsq = 1.0f / Utils.max(IUtils.DEFAULT_EPSILON, r2 * r2);

    for ( int i = 0, y = 0; y < h; ++y ) {

      final float yn = y * hInv;
      final float ay = yOrigin - (1.0f - (yn + yn));
      final float aysq = ay * ay;

      for ( int x = 0; x < w; ++x, ++i ) {

        final float xn = x * wInv;
        final float ax = xOrigin - (xn + xn - 1.0f);

        grd.eval(1.0f - (ax * ax + aysq) * invrsq, ZImage.clr);
        pixels[i] = Color.toHexInt(ZImage.clr);
      }
    }

    target.updatePixels();
    return target;
  }

  /**
   * Generates a radial gradient.
   *
   * This does not account for aspect ratio, so an image that is not 1:1
   * will result in an ellipsoid.
   *
   * @param origin the origin
   * @param radius the radius
   * @param grd    the gradient
   * @param target the output image
   * @return the image
   */
  public static PImage radial (
      final Vec2 origin,
      final float radius,
      final Gradient grd,
      final PImage target ) {

    return ZImage.radial(origin.x, origin.y, radius, grd, target);
  }

  /**
   * Generates a diagnostic image where a pixel's location on the x-axis
   * correlates to the color red; on the y-axis, to green.
   *
   * @param target the output image
   * @return the image
   */
  public static PImage rgb ( final PImage target ) {

    target.loadPixels();

    final int[] px = target.pixels;
    final int h = target.height;
    final int w = target.width;

    final float hInv = 0xff / (h - 1.0f);
    final float wInv = 0xff / (w - 1.0f);

    for ( int i = 0, y = h - 1; y > -1; --y ) {
      final int grbl = 0xff000080 | (int) (y * hInv + 0.5f) << 0x8;
      for ( int x = 0; x < w; ++x, ++i ) {
        px[i] = (int) (x * wInv + 0.5f) << 0x10 | grbl;
      }
    }

    target.updatePixels();
    return target;
  }

  /**
   * Returns a string representation of an image, including its format,
   * width, height and pixel density.
   *
   * @param pi the PImage
   * @return the string
   */
  public static String toString ( final PImage pi ) {

    return new StringBuilder(64)
        .append("{ format: ")
        .append(pi.format)
        .append(", width: ")
        .append(pi.width)
        .append(", height: ")
        .append(pi.height)
        .append(", pixelDensity: ")
        .append(pi.pixelDensity)
        .append(' ').append('}')
        .toString();
  }

  /**
   * Blits a source image's pixels onto a target image's pixels, using
   * integer floor modulo to wrap the source image. The source image can
   * be offset horizontally and/or vertically, creating the illusion of
   * parallax.
   *
   * @param source  source pixel array
   * @param wSource source image width
   * @param hSource source image height
   * @param target  target pixel array
   * @param wTarget target image width
   * @param hTarget target image height
   * @param dx      horizontal pixel offset
   * @param dy      vertical pixel offset
   * @return the target pixels
   */
  public static int[] wrap (
      final int[] source,
      final int wSource,
      final int hSource,
      final int[] target,
      final int wTarget,
      final int hTarget,
      final int dx,
      final int dy ) {

    if ( wSource < 1 || hSource < 1 ) { return target; }

    for ( int i = 0, y = 0; y < hTarget; ++y ) {

      // final int ny = wSource * Math.floorMod(y - dy, hSource);
      int ymod = (y - dy) % hSource;
      if ( (ymod ^ hSource) < 0 && ymod != 0 ) { ymod += hSource; }
      final int ny = wSource * ymod;

      for ( int x = 0; x < wTarget; ++x, ++i ) {

        // target[i] = source[Math.floorMod(x + dx, wSource) + ny];
        int xmod = (x + dx) % wSource;
        if ( (xmod ^ wSource) < 0 && xmod != 0 ) { xmod += wSource; }
        target[i] = source[xmod + ny];
      }
    }
    return target;
  }

  /**
   * Blits a source image's pixels onto a target image's pixels, using
   * integer floor modulo to wrap the source image. The source image can
   * be offset horizontally and/or vertically, creating the illusion of
   * parallax.
   *
   * @param source the source image
   * @param target the target image
   * @param dx     horizontal pixel offset
   * @param dy     vertical pixel offset
   * @return the image
   */
  public static PImage wrap (
      final PImage source,
      final PImage target,
      final float dx,
      final float dy ) {

    return ZImage.wrap(source, target, (int) dx, (int) dy);
  }

  /**
   * Blits a source image's pixels onto a target image's pixels, using
   * integer floor modulo to wrap the source image. The source image can
   * be offset horizontally and/or vertically, creating the illusion of
   * parallax.
   *
   * @param source the source image
   * @param target the target image
   * @param dx     horizontal pixel offset
   * @param dy     vertical pixel offset
   * @return the image
   */
  public static PImage wrap (
      final PImage source,
      final PImage target,
      final int dx,
      final int dy ) {

    source.loadPixels();
    target.loadPixels();
    ZImage.wrap(
        source.pixels, source.width, source.height,
        target.pixels, target.width, target.height,
        dx, dy);
    target.updatePixels();
    // source.updatePixels();
    return target;
  }

  /**
   * Blits a source image's pixels onto a target image's pixels, using
   * integer floor modulo to wrap the source image. The source image can
   * be offset horizontally and/or vertically, creating the illusion of
   * parallax.
   *
   * @param source the source image
   * @param target the target image
   * @param d      pixel offset
   * @return the image
   */
  public static PImage wrap (
      final PImage source,
      final PImage target,
      final Vec2 d ) {

    return ZImage.wrap(source, target, (int) d.x, (int) d.y);
  }
}
