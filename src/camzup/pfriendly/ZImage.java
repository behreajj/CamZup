package camzup.pfriendly;

import java.awt.Image;

import camzup.core.Color;
import camzup.core.Gradient;
import camzup.core.SDF;
import camzup.core.Utils;
import camzup.core.Vec2;
import processing.core.PImage;

public class ZImage extends PImage {

   private static final Color clr = new Color();

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

      for (int i = 0, y = 0; y < h; ++y) {

         final float yn = y * hInv;
         final float t = 1.0f - (yn + yn + yOrigin);

         for (int x = 0; x < w; ++x, ++i) {

            final float xn = x * wInv;
            final float s = xn + xn - xOrigin - 1.0f;

            grd.eval(SDF.conic(s, t, radians), ZImage.clr);
            pixels[i] = Color.toHexInt(ZImage.clr);
         }
      }
      target.updatePixels();
      return target;
   }

   public static PImage conic (
         final Vec2 origin,
         final float radians,
         final Gradient grd,
         final PImage target ) {

      return ZImage.conic(origin.x, origin.y, radians, grd, target);
   }

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
      final float bbInv = Utils.div(1.0f, bx * bx + by * by);

      for (int i = 0, y = 0; y < h; ++y) {

         final float yn = y * hInv;
         final float ay = yOrigin - (1.0f - (yn + yn));

         for (int x = 0; x < w; ++x, ++i) {

            final float xn = x * wInv;
            final float ax = xOrigin - (xn + xn - 1.0f);

            grd.eval(Utils.clamp01((ax * bx + ay * by) * bbInv), ZImage.clr);
            pixels[i] = Color.toHexInt(ZImage.clr);
         }
      }

      target.updatePixels();
      return target;
   }

   public static PImage linear (
         final Vec2 origin,
         final Vec2 dest,
         final Gradient grd,
         final PImage target ) {

      return ZImage.linear(origin.x, origin.y, dest.x, dest.y, grd, target);
   }

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
      final float invrsq = 1.0f / Utils.max(Utils.EPSILON, r2 * r2);

      for (int i = 0, y = 0; y < h; ++y) {

         final float yn = y * hInv;
         final float ay = yOrigin - (1.0f - (yn + yn));
         final float aysq = ay * ay;

         for (int x = 0; x < w; ++x, ++i) {

            final float xn = x * wInv;
            final float ax = xOrigin - (xn + xn - 1.0f);

            grd.eval(1.0f - (ax * ax + aysq) * invrsq, ZImage.clr);
            pixels[i] = Color.toHexInt(ZImage.clr);
         }
      }

      target.updatePixels();
      return target;
   }

   public static PImage radial (
         final Vec2 origin,
         final float radius,
         final Gradient grd,
         final PImage target ) {

      return ZImage.radial(origin.x, origin.y, radius, grd, target);
   }

   /**
    * Generates a diagnostic image where a pixel's location on
    * the x-axis correlates to the color red; on the y-axis, to
    * green.
    *
    * @param target
    *           the output image
    * @return the image
    */
   public static PImage rgb ( final PImage target ) {

      target.loadPixels();

      final int[] px = target.pixels;
      final int h = target.height;
      final int w = target.width;

      final float hInv = 0xff / (h - 1.0f);
      final float wInv = 0xff / (w - 1.0f);

      for (int i = 0, y = h - 1; y > -1; --y) {

         final int grbl = 0xff00007f | (int) (y * hInv + 0.5f) << 0x8;

         for (int x = 0; x < w; ++x, ++i) {

            final int red = (int) (x * wInv + 0.5f) << 0x10;
            px[i] = red | grbl;
         }
      }

      target.updatePixels();
      return target;
   }

   /**
    * Blits a source image's pixels onto a target image's
    * pixels, using integer floor modulo to wrap the source
    * image. The source image can be offset horizontally and/or
    * vertically, creating the illusion of parallax.
    * 
    * @param source
    *           source pixel array
    * @param wSource
    *           source image width
    * @param hSource
    *           source image height
    * @param target
    *           target pixel array
    * @param wTarget
    *           target image width
    * @param hTarget
    *           target image height
    * @param dx
    *           horizontal pixel offset
    * @param dy
    *           vertical pixel offset
    * @return the target pixels
    */
   public static int[] wrap (
         final int[] source, final int wSource, final int hSource,
         final int[] target, final int wTarget, final int hTarget,
         final int dx, final int dy ) {

      for (int i = 0, y = 0; y < hTarget; ++y) {
         final int ny = wSource * Utils.mod(y - dy, hSource);
         for (int x = 0; x < wTarget; ++x, ++i) {
            target[i] = source[Utils.mod(x + dx, wSource) + ny];
         }
      }
      return target;
   }

   /**
    * Blits a source image's pixels onto a target image's
    * pixels, using integer floor modulo to wrap the source
    * image. The source image can be offset horizontally and/or
    * vertically, creating the illusion of parallax.
    * 
    * @param source
    *           the source image
    * @param target
    *           the target image
    * @param dx
    *           horizontal pixel offset
    * @param dy
    *           vertical pixel offset
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
    * Blits a source image's pixels onto a target image's
    * pixels, using integer floor modulo to wrap the source
    * image. The source image can be offset horizontally and/or
    * vertically, creating the illusion of parallax.
    * 
    * @param source
    *           the source image
    * @param target
    *           the target image
    * @param dx
    *           horizontal pixel offset
    * @param dy
    *           vertical pixel offset
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
      source.updatePixels();
      return target;
   }

   /**
    * Blits a source image's pixels onto a target image's
    * pixels, using integer floor modulo to wrap the source
    * image. The source image can be offset horizontally and/or
    * vertically, creating the illusion of parallax.
    * 
    * @param source
    *           the source image
    * @param target
    *           the target image
    * @param d
    *           pixel offset
    * @return the image
    */
   public static PImage wrap (
         final PImage source,
         final PImage target,
         final Vec2 d ) {

      return ZImage.wrap(source, target, (int) d.x, (int) d.y);
   }

   /**
    * The default constructor.
    */
   public ZImage () {

      super();
   }

   /**
    * Constructs an image from the native AWT image.
    * 
    * @param img
    *           the image
    */
   public ZImage ( final Image img ) {

      super(img);
   }

   /**
    * Constructs an image from its dimensions, width and
    * height.
    * 
    * @param width
    *           the image width
    * @param height
    *           the image height
    */
   public ZImage ( final int width, final int height ) {

      super(width, height);
   }

   /**
    * Constructs an image from the dimensions and format.
    * 
    * @param width
    *           the image width
    * @param height
    *           the image height
    * @param format
    *           the format
    */
   public ZImage (
         final int width,
         final int height,
         final int format ) {

      super(width, height, format, 1);
   }

   /**
    * Constructs an image from the dimensions, format and pixel
    * density.
    * 
    * @param width
    *           the image width
    * @param height
    *           the image height
    * @param format
    *           the format
    * @param pixelDensity
    *           the pixel density
    */
   public ZImage (
         final int width,
         final int height,
         final int format,
         final int pixelDensity ) {

      super(width, height, format, pixelDensity);
   }

   /**
    * Returns a string representation of an image, including
    * its format, width, height and pixel density.
    */
   @Override
   public String toString () {

      return new StringBuilder(64)
            .append("{ format: ")
            .append(this.format)
            .append(", width: ")
            .append(this.width)
            .append(", height: ")
            .append(this.height)
            .append(", pixelDensity: ")
            .append(this.pixelDensity)
            .append(' ').append('}')
            .toString();
   }
}
