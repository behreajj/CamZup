package camzup.pfriendly;

import java.awt.Image;

import camzup.core.Color;
import camzup.core.Gradient;
import camzup.core.Utils;
import camzup.core.Vec2;
import processing.core.PImage;

public class ZImage extends PImage {

   private static final Color clr = new Color();

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
      for (int i = 0, y = 0; y < h; ++y) {
         final int green = (int) (y * hInv + 0.5f) << 0x8;
         for (int x = 0; x < w; ++x, ++i) {
            final int red = (int) (x * wInv + 0.5f) << 0x10;
            px[i] = 0xff00007f | red | green;
         }
      }
      target.updatePixels();
      return target;
   }

   public static PImage sdfLine (
         final Vec2 origin,
         final Vec2 dest,
         final float rounding,
         final Gradient grd,
         final PImage target ) {

      /* Denominator: b - a */
      final float bax = dest.x - origin.x;
      final float bay = dest.y - origin.y;

      /* 1.0 / dot(ba, ba) */
      final float baba = 1.0f / Utils.max(bax * bax + bay * bay, Utils.EPSILON);

      target.loadPixels();

      final int h = target.height;
      final int w = target.width;
      final int[] pixels = target.pixels;

      final float hInv = 1.0f / (h - 1.0f);
      final float wInv = 1.0f / (w - 1.0f);

      for (int i = 0, y = 0; y < h; ++y) {

         final float yn = y * hInv;
         final float py = 1.0f - (yn + yn);
         final float pay = py - origin.y;
         final float paybay = pay * bay;

         for (int x = 0; x < w; ++x, ++i) {

            final float xn = x * wInv;
            final float px = xn + xn - 1.0f;
            final float pax = px - origin.x;

            final float paba = pax * bax + paybay;
            final float k = Utils.clamp01(paba * baba);
            final float fac = Utils.hypot(pax - k * bax, pay - k * bay);

            grd.eval(fac, ZImage.clr);
            pixels[i] = Color.toHexInt(ZImage.clr);
         }
      }

      target.updatePixels();
      return target;
   }

   public static int[] wrap (
         final int[] target, final int wTarget, final int hTarget,
         final int[] source, final int wSource, final int hSource,
         final int dx, final int dy ) {

      for (int i = 0, y = 0; y < hTarget; ++y) {
         final int ny = wSource * Utils.mod(y + dy, hSource);

         for (int x = 0; x < wTarget; ++x, ++i) {
            final int nx = Utils.mod(x + dx, wSource);
            target[i] = source[nx + ny];
         }
      }
      return target;
   }

   public static PImage wrap (
         final PImage target,
         final PImage source,
         final float dx, final float dy ) {

      return ZImage.wrap(target, source, (int) dx, (int) dy);
   }

   public static PImage wrap (
         final PImage target,
         final PImage source,
         final int dx, final int dy ) {

      source.loadPixels();
      target.loadPixels();
      ZImage.wrap(
            target.pixels, target.width, target.height,
            source.pixels, source.width, source.height,
            dx, dy);
      target.updatePixels();
      source.updatePixels();
      return target;
   }

   public static PImage wrap (
         final PImage target,
         final PImage source,
         final Vec2 d ) {

      return ZImage.wrap(target, source, (int) d.x, (int) d.y);
   }

   public ZImage () {

      super();
   }

   public ZImage ( final Image img ) {

      super(img);
   }

   public ZImage ( final int width, final int height ) {

      super(width, height);
   }

   public ZImage ( final int width, final int height, final int format ) {

      super(width, height, format, 1);
   }

   public ZImage ( final int width, final int height, final int format,
         final int factor ) {

      super(width, height, format, factor);
   }
}
