package camzup.pfriendly;

import camzup.core.IUtils;
import camzup.core.Utils;
import processing.core.PConstants;

/**
 * Implements color conversion and mixing for Hue, Saturation and
 * Brightness.
 */
public abstract class ColorAux {

   /**
    * Discourage overriding with a private constructor.
    */
   private ColorAux ( ) {}

   /**
    * Converts from hue, saturation, brightness and alpha to a color with red,
    * green, blue and alpha channels. All arguments are expected to be in the
    * range [0.0, 1.0] .
    *
    * @param hue the hue
    * @param sat the saturation
    * @param val the value
    *
    * @return the color
    *
    * @see Utils#clamp01(float)
    * @see Utils#mod1(float)
    */
   public static float[] hsbToRgb ( final float hue, final float sat,
      final float val ) {

      final float h = Utils.mod1(hue) * 6.0f;
      final float s = Utils.clamp01(sat);
      final float v = Utils.clamp01(val);
      final int sector = ( int ) h;
      final float secf = sector;
      final float tint1 = v * ( 1.0f - s );
      final float tint2 = v * ( 1.0f - s * ( h - secf ) );
      final float tint3 = v * ( 1.0f - s * ( 1.0f + secf - h ) );
      switch ( sector ) {
         case 0:
            return new float[] { v, tint3, tint1 };

         case 1:
            return new float[] { tint2, v, tint1 };

         case 2:
            return new float[] { tint1, v, tint3 };

         case 3:
            return new float[] { tint1, tint2, v };

         case 4:
            return new float[] { tint3, tint1, v };

         case 5:
            return new float[] { v, tint1, tint2 };

         default:
            return new float[] { 0.0f, 0.0f, 0.0f };
      }
   }

   /**
    * Mixes two integers holding 32-bit standard RGB colors in 0xAARRGGBB
    * order according to a step. They are mixed according to a color mode
    * flag, {@link PConstants#HSB} or {@link PConstants#RGB}.
    *
    * @param o         the origin
    * @param d         the destination
    * @param t         the step
    * @param colorMode color mode
    *
    * @return the mixture
    *
    * @see ColorAux#lerpHsb(int, int, float)
    * @see ColorAux#lerpRgb(int, int, float)
    */
   public static int lerpColor ( final int o, final int d, final float t,
      final int colorMode ) {

      switch ( colorMode ) {
         case PConstants.HSB: /* 3 */

            return ColorAux.lerpHsb(o, d, t);

         case PConstants.RGB: /* 1 */
         default:

            return ColorAux.lerpRgb(o, d, t);
      }
   }

   /**
    * Mixes two integers holding 32-bit standard RGB colors in 0xAARRGGBB
    * order according to a step. Converts them to Hue Saturation Brightness,
    * then performs the mix. If the saturation of either color is near zero,
    * then defaults to standard RGB mix.
    *
    * @param o the origin
    * @param d the destination
    * @param t the step
    *
    * @return the mixture
    *
    * @see ColorAux#rgbToHsb(float, float, float)
    * @see ColorAux#hsbToRgb(float, float, float)
    */
   public static int lerpHsb ( final int o, final int d, final float t ) {

      /* Unpack origin color to r, g, b, a. */
      final float oa = ( o >> 0x18 & 0xff ) * IUtils.ONE_255;
      final float or = ( o >> 0x10 & 0xff ) * IUtils.ONE_255;
      final float og = ( o >> 0x08 & 0xff ) * IUtils.ONE_255;
      final float ob = ( o & 0xff ) * IUtils.ONE_255;

      /* Unpack destination color to r, g, b, a. */
      final float da = ( d >> 0x18 & 0xff ) * IUtils.ONE_255;
      final float dr = ( d >> 0x10 & 0xff ) * IUtils.ONE_255;
      final float dg = ( d >> 0x08 & 0xff ) * IUtils.ONE_255;
      final float db = ( d & 0xff ) * IUtils.ONE_255;

      /* Mix alpha. */
      final float u = 1.0f - t;
      final float ca = u * oa + t * da;

      /* Convert from sRGB to HSB. */
      final float[] oHsb = ColorAux.rgbToHsb(or, og, ob);
      final float[] dHsb = ColorAux.rgbToHsb(dr, dg, db);

      /* Check saturation. If either is near zero, default to RGB. */
      final float os = oHsb[1];
      final float ds = dHsb[1];
      if ( os < IUtils.ONE_255 || ds < IUtils.ONE_255 ) {
         final float cr = u * or + t * dr;
         final float cg = u * og + t * dg;
         final float cb = u * ob + t * db;

         return ( int ) ( ca * 0xff + 0.5f ) << 0x18 | ( int ) ( cr * 0xff
            + 0.5f ) << 0x10 | ( int ) ( cg * 0xff + 0.5f ) << 0x08
            | ( int ) ( cb * 0xff + 0.5f );
      }

      /* Mix hue according to nearest direction. */
      final float oh = oHsb[0];
      final float dh = dHsb[0];
      final float diff = dh - oh;
      float ch = oh;
      if ( diff != 0.0f ) {
         if ( oh < dh && diff > 0.5f ) {
            ch = u * ( oh + 1.0f ) + t * dh;
         } else if ( oh > dh && diff < -0.5f ) {
            ch = u * oh + t * ( dh + 1.0f );
         } else {
            ch = u * oh + t * dh;
         }
      }

      /* Convert from HSB to RGB. */
      final float[] rgb = ColorAux.hsbToRgb(ch, u * os + t * ds, u * oHsb[2] + t
         * dHsb[2]);

      /* Pack into 32-bit integer. */
      return ( int ) ( ca * 0xff + 0.5f ) << 0x18 | ( int ) ( rgb[0] * 0xff
         + 0.5f ) << 0x10 | ( int ) ( rgb[1] * 0xff + 0.5f ) << 0x08
         | ( int ) ( rgb[2] * 0xff + 0.5f );
   }

   /**
    * Mixes two integers holding 32-bit standard RGB colors in 0xAARRGGBB
    * order according to a step.
    *
    * @param o the origin
    * @param d the destination
    * @param t the step
    *
    * @return the mixture
    */
   public static int lerpRgb ( final int o, final int d, final float t ) {

      final float oa = ( o >> 0x18 & 0xff ) * IUtils.ONE_255;
      final float or = ( o >> 0x10 & 0xff ) * IUtils.ONE_255;
      final float og = ( o >> 0x08 & 0xff ) * IUtils.ONE_255;
      final float ob = ( o & 0xff ) * IUtils.ONE_255;

      final float da = ( d >> 0x18 & 0xff ) * IUtils.ONE_255;
      final float dr = ( d >> 0x10 & 0xff ) * IUtils.ONE_255;
      final float dg = ( d >> 0x08 & 0xff ) * IUtils.ONE_255;
      final float db = ( d & 0xff ) * IUtils.ONE_255;

      final float u = 1.0f - t;
      final float ca = u * oa + t * da;
      final float cr = u * or + t * dr;
      final float cg = u * og + t * dg;
      final float cb = u * ob + t * db;

      return ( int ) ( ca * 0xff + 0.5f ) << 0x18 | ( int ) ( cr * 0xff + 0.5f )
         << 0x10 | ( int ) ( cg * 0xff + 0.5f ) << 0x08 | ( int ) ( cb * 0xff
            + 0.5f );
   }

   /**
    * Converts RGBA channels to a vector which holds hue, saturation,
    * brightness and alpha.
    *
    * @param r the red channel
    * @param g the green channel
    * @param b the blue channel
    *
    * @return the HSB values
    *
    * @see Utils#approx(float, float, float)
    */
   public static float[] rgbToHsb ( final float r, final float g,
      final float b ) {

      /*
       * Unnecessary to worry about red hues for gray colors in this case, as
       * this shouldn't be used for any serious color matching.
       */

      final float gbmx = g > b ? g : b;
      final float gbmn = g < b ? g : b;
      final float mx = gbmx > r ? gbmx : r;
      if ( mx < IUtils.ONE_255 ) { return new float[] { 0.0f, 0.0f, 0.0f }; }
      final float mn = gbmn < r ? gbmn : r;
      final float diff = mx - mn;
      if ( diff < IUtils.ONE_255 ) {
         final float light = ( mx + mn ) * 0.5f;
         if ( light > 1.0f - IUtils.ONE_255 ) {
            return new float[] { 0.0f, 0.0f, 1.0f };
         }
         return new float[] { 0.0f, 0.0f, mx };
      }
      float hue;
      if ( r == mx ) {
         hue = ( g - b ) / diff;
         if ( g < b ) { hue += 6.0f; }
      } else if ( g == mx ) {
         hue = 2.0f + ( b - r ) / diff;
      } else {
         hue = 4.0f + ( r - g ) / diff;
      }
      return new float[] { hue * IUtils.ONE_SIX, diff / mx, mx };
   }

}
