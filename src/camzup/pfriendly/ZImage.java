package camzup.pfriendly;

import java.awt.Image;

import camzup.core.Color;
import camzup.core.Gradient;
import camzup.core.Utils;
import camzup.core.Vec2;
import processing.core.PImage;

public class ZImage extends PImage {

   private static final Color clr = new Color();

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
