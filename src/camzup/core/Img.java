package camzup.core;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.TreeMap;
import java.util.TreeSet;

import processing.core.PImage;

/**
 * An image class for images in the LAB color format. The bytes per pixel
 * is 64, a long. The bytes per channel is 16, a short. The channel format
 * is 0xTTTTLLLLAAAABBBB. The alpha channel is abbreviated to 'T', since
 * 'A' is already taken.
 */
public class Img implements Iterable < Long > {

   /**
    * The image height.
    */
   protected int height;

   /**
    * The image pixels.
    */
   protected final long[] pixels;

   /**
    * The image width.
    */
   protected int width;

   /**
    * Constructs an image from a source.
    *
    * @param source the source image.
    */
   public Img ( final Img source ) {

      this.width = source.width;
      this.height = source.height;
      this.pixels = source.getPixels();
   }

   /**
    * Constructs an image from width and height. The image is filled with
    * {@link Img#CLEAR_PIXEL}, {@value Img#CLEAR_PIXEL}.
    *
    * @param width  the width
    * @param height the height
    */
   public Img ( final int width, final int height ) {

      // TODO: The final width height choice will make image rotate, skew and
      // resize a bitch. For now implement protected static methods that only
      // work on long[] arrays of pixels.

      // TODO: Function to set all zero alpha pixels to clear pixel?

      // TODO: Mark out of gamut pixels with a color. Problem is that you have
      // to assume srgb.

      // TODO: swap alpha to light, light to alpha methods?

      this(width, height, Img.CLEAR_PIXEL);
   }

   /**
    * Constructs an image from width and height. The image is filled with the
    * provided color.
    *
    * @param width  the width
    * @param height the height
    * @param fill   the color
    */
   public Img ( final int width, final int height, final Lab fill ) {

      this(width, height, fill.toHexLongSat());
   }

   /**
    * Constructs an image from width and height. The image is filled with the
    * provided color. The absolute value of width and height are used. Width
    * and height are expected to be at least 1 and at most
    * {@link Img#MAX_DIMENSION}, {@value Img#MAX_DIMENSION}.
    *
    * @param width  the width
    * @param height the height
    * @param fill   the color
    */
   public Img ( final int width, final int height, final long fill ) {

      this.width = Utils.clamp(Math.abs(width), 1, Img.MAX_DIMENSION);
      this.height = Utils.clamp(Math.abs(height), 1, Img.MAX_DIMENSION);

      final int area = this.width * this.height;
      this.pixels = new long[area];
      for ( int i = 0; i < area; ++i ) { this.pixels[i] = fill; }
   }

   /**
    * Constructs a new image with no verification. The pixels array is
    * assigned by reference, not copied by value.
    *
    * @param width  the width
    * @param height the height
    * @param pixels the pixels
    */
   protected Img ( final int width, final int height, final long[] pixels ) {

      this.width = width;
      this.height = height;
      this.pixels = pixels;
   }

   /**
    * Tests this image for equivalence with another.
    *
    * @param other the image
    *
    * @return the equivalence
    */
   public boolean equals ( final Img other ) {

      return this.height == other.height && Arrays.equals(this.pixels,
         other.pixels) && this.width == other.width;
   }

   /**
    * Tests this image for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) return true;
      if ( obj == null || this.getClass() != obj.getClass() ) return false;
      final Img other = ( Img ) obj;
      return this.equals(other);
   }

   /**
    * Gets the image height.
    *
    * @return the width
    */
   public final int getHeight ( ) { return this.height; }

   /**
    * Gets a pixel at an index.
    *
    * @param i the index
    *
    * @return the pixel
    */
   public long getPixel ( final int i ) {

      return this.getPixelOmit(i);
   }

   /**
    * Gets a pixel at local coordinates x and y.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    *
    * @return the pixel
    */
   public long getPixel ( final int x, final int y ) {

      return this.getPixelOmit(x, y);
   }

   /**
    * Gets a pixel at local coordinates x and y. If the index is out of
    * bounds, then returns {@link Img#CLEAR_PIXEL}, {@value Img#CLEAR_PIXEL}.
    *
    * @param i the index
    *
    * @return the pixel
    */
   public final long getPixelOmit ( final int i ) {

      return this.getPixelOmit(i, Img.CLEAR_PIXEL);
   }

   /**
    * Gets a pixel at local coordinates x and y. If the coordinates are out of
    * bounds, then returns {@link Img#CLEAR_PIXEL}, {@value Img#CLEAR_PIXEL}.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    *
    * @return the pixel
    */
   public final long getPixelOmit ( final int x, final int y ) {

      return this.getPixelOmit(x, y, Img.CLEAR_PIXEL);
   }

   /**
    * Gets a pixel at local coordinates x and y. If the coordinates are out of
    * bounds, then returns the default pixel.
    *
    * @param x            the x coordinate
    * @param y            the y coordinate
    * @param defaultPixel the default pixel
    *
    * @return the pixel
    */
   public final long getPixelOmit ( final int x, final int y,
      final long defaultPixel ) {

      if ( y >= 0 && y < this.height && x >= 0 && x < this.width ) {
         return this.pixels[y * this.width + x];
      }
      return defaultPixel;
   }

   /**
    * Gets a pixel at an index. If the index is out of bounds, then returns
    * the default pixel.
    *
    * @param i            the index
    * @param defaultPixel the default pixel
    *
    * @return the pixel
    */

   public final long getPixelOmit ( final int i, final long defaultPixel ) {

      if ( i >= 0 && i < this.pixels.length ) { return this.pixels[i]; }
      return defaultPixel;
   }

   /**
    * Gets a copy of the pixels array.
    *
    * @return the pixels array
    */
   public final long[] getPixels ( ) {

      final int len = this.pixels.length;
      final long[] arr = new long[len];
      System.arraycopy(this.pixels, 0, arr, 0, len);
      return arr;
   }

   /**
    * Gets a pixel at an index. Does not check the index for validity.
    *
    * @param i the index
    *
    * @return the pixel
    */

   public final long getPixelUnchecked ( final int i ) {

      return this.pixels[i];
   }

   /**
    * Gets a pixel at local coordinates x and y. Does not check the
    * coordinates for validity.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    *
    * @return the pixel
    */
   public final long getPixelUnchecked ( final int x, final int y ) {

      return this.pixels[y * this.width + x];
   }

   /**
    * Gets a pixel at an index. Wraps the index around the pixels array
    * length.
    *
    * @param i the index
    *
    * @return the pixel
    */
   public final long getPixelWrap ( final int i ) {

      return this.pixels[Utils.mod(i, this.pixels.length)];
   }

   /**
    * Gets a pixel at local coordinates x and y. Wraps the coordinates around
    * the image boundaries.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    *
    * @return the pixel
    */
   public final long getPixelWrap ( final int x, final int y ) {

      final int xwr = Utils.mod(x, this.width);
      final int ywr = Utils.mod(y, this.height);
      final int i = ywr * this.width + xwr;

      return this.pixels[i];
   }

   /**
    * Gets the image width.
    *
    * @return the width
    */
   public final int getWidth ( ) { return this.width; }

   @Override
   public int hashCode ( ) {

      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(this.pixels);
      return prime * result + Objects.hash(this.height, this.width);
   }

   /**
    * Gets a pixel iterator.
    */
   @Override
   public ImageIterator iterator ( ) {

      return new ImageIterator(this);
   }

   /**
    * Gets the image pixel length.
    *
    * @return the pixel length
    */
   public final int length ( ) { return this.pixels.length; }

   /**
    * Sets a pixel at local coordinates x and y.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    * @param c the color
    */
   public void setPixel ( final int x, final int y, final long c ) {

      this.setPixelOmit(x, y, c);
   }

   /**
    * Sets a pixel at local coordinates x and y.
    *
    * @param i the index
    * @param c the color
    */
   public void setPixel ( final int i, final long c ) {

      this.setPixelOmit(i, c);
   }

   /**
    * If local coordinates x and y are within image bounds, then sets the
    * pixel to the given color.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    * @param c the color
    */
   public final void setPixelOmit ( final int x, final int y, final long c ) {

      if ( y >= 0 && y < this.height && x >= 0 && x < this.width ) {
         this.pixels[y * this.width + x] = c;
      }
   }

   /**
    * If the index is in bounds, then sets the pixel to the given color.
    *
    * @param i the index
    * @param c the color
    */
   public final void setPixelOmit ( final int i, final long c ) {

      if ( i >= 0 && i < this.pixels.length ) { this.pixels[i] = c; }
   }

   /**
    * Sets a pixel at local coordinates x and y. Does not check the
    * coordinates for validity.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    * @param c the color
    */
   public final void setPixelUnchecked ( final int x, final int y,
      final long c ) {

      this.pixels[y * this.width + x] = c;
   }

   /**
    * Sets a pixel at an index. Does not check the index for validity.
    *
    * @param i the index
    * @param c the color
    */
   public final void setPixelUnchecked ( final int i, final long c ) {

      this.pixels[i] = c;
   }

   /**
    * Sets a pixel at local coordinates x and y. Wraps the coordinates around
    * the image boundaries.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    * @param c the color
    */
   public final void setPixelWrap ( final int x, final int y, final long c ) {

      final int xwr = Utils.mod(x, this.width);
      final int ywr = Utils.mod(y, this.height);
      final int i = ywr * this.width + xwr;
      this.pixels[i] = c;
   }

   /**
    * Sets a pixel at an index. Wraps the index around the pixel boundaries.
    *
    * @param i the index
    * @param c the color
    */
   public final void setPixelWrap ( final int i, final long c ) {

      this.pixels[Utils.mod(i, this.pixels.length)] = c;
   }

   /**
    * Returns a string representation of an image, including its format,
    * width, height and pixel density.
    *
    * @return the string
    */
   @Override
   public String toString ( ) {

      final StringBuilder sb = new StringBuilder(64);

      sb.append("{\"width\":");
      sb.append(this.width);
      sb.append(",\"height\":");
      sb.append(this.height);
      sb.append(",\"pixels\":[");

      final int lenn1 = this.pixels.length - 1;
      for ( int i = 0; i < lenn1; ++i ) {
         sb.append(this.pixels[i]);
         sb.append(',');
      }
      sb.append(this.pixels[lenn1]);
      sb.append(']');
      sb.append('}');

      return sb.toString();
   }

   /**
    * The amount to shift the a channel to the left or right when packing and
    * unpacking a pixel.
    */
   public static final long A_SHIFT = 0x10L;

   /**
    * The amount to shift the b channel to the left or right when packing and
    * unpacking a pixel.
    */
   public static final long B_SHIFT = 0x00L;

   /**
    * Default color for dark checker squares, {@value Img#CHECKER_DARK}, or
    * 1.0 / 3.0 of 65535 lightness.
    */
   public static final long CHECKER_DARK = 0xffff_5555_8000_8000L;

   /**
    * Default color for light checker squares, {@value Img#CHECKER_LIGHT}, or
    * 2.0 / 3.0 of 65535 lightness.
    */
   public static final long CHECKER_LIGHT = 0xffff_aaaa_8000_8000L;

   /**
    * The value of a pixel with zero light, zero a, zero b and zero alpha.
    * Because a and b are signed floating point numbers that are converted to
    * unsigned shorts, this number is not equal to zero.
    */
   public static final long CLEAR_PIXEL = 0x0000_0000_8000_8000L;

   /**
    * Default alpha value when creating a diagnostic RGB image.
    */
   public final static float DEFAULT_ALPHA = 1.0f;

   /**
    * Default blue value when creating a diagnostic RGB image.
    */
   public final static float DEFAULT_BLUE = 0.5f;

   /**
    * The default policy on gray colors when adjusting by LCH.
    */
   public static final GrayPolicy DEFAULT_GRAY_POLICY = GrayPolicy.OMIT;

   /**
    * The default policy on pivots when adjusting chroma by a factor.
    */
   public static final PivotPolicy DEFAULT_PIVOT_POLICY = PivotPolicy.MEAN;

   /**
    * Mask to isolate the a channel.
    */
   public static final long ISOLATE_A_MASK = 0x0000_0000_ffff_0000L;

   /**
    * Mask to isolate the a and b channels.
    */
   public static final long ISOLATE_AB_MASK = Img.ISOLATE_A_MASK
      | Img.ISOLATE_B_MASK;

   /**
    * Mask to isolate the b channel.
    */
   public static final long ISOLATE_B_MASK = 0x0000_0000_0000_ffffL;

   /**
    * Mask to isolate the lightness channel.
    */
   public static final long ISOLATE_L_MASK = 0x0000_ffff_0000_0000L;

   /**
    * Mask to isolate the l, a and b channels.
    */
   public static final long ISOLATE_LAB_MASK = Img.ISOLATE_L_MASK
      | Img.ISOLATE_A_MASK | Img.ISOLATE_B_MASK;

   /**
    * Mask to isolate the alpha channel.
    */
   public static final long ISOLATE_T_MASK = 0xffff_0000_0000_0000L;

   /**
    * Mask to isolate the alpha and l channels.
    */
   public static final long ISOLATE_TL_MASK = Img.ISOLATE_T_MASK
      | Img.ISOLATE_L_MASK;

   /**
    * Mask for all color channels.
    */
   public static final long ISOLATE_TLAB_MASK = Img.ISOLATE_T_MASK
      | Img.ISOLATE_L_MASK | Img.ISOLATE_A_MASK | Img.ISOLATE_B_MASK;

   /**
    * The amount to shift the lightness channel to the left or right when
    * packing and unpacking a pixel.
    */
   public static final long L_SHIFT = 0x20L;

   /**
    * Max image width and height allowed.
    */
   public static final int MAX_DIMENSION = Short.MAX_VALUE;

   /**
    * The minimum difference between minimum and maximum light needed when
    * normalizing light.
    */
   public static final float MIN_LIGHT_DIFF = 0.07f;

   /**
    * The amount to shift the alpha channel to the left or right when packing
    * and unpacking a pixel.
    */
   public static final long T_SHIFT = 0x30L;

   /**
    * Adjusts an image's lightness and saturation contrast by a factor. The
    * adjustment factor is expected to be in [-1.0, 1.0].
    *
    * @param source the source pixels
    * @param sFac   the saturation contrast factor
    * @param lFac   the lightness contrast factor
    * @param target the target pixels
    *
    * @return the adjusted pixels
    */
   public static Img adjustContrast ( final Img source, final float sFac,
      final float lFac, final Img target ) {

      return Img.adjustContrast(source, sFac, lFac, Img.DEFAULT_PIVOT_POLICY,
         target);
   }

   /**
    * Adjusts an image's lightness and saturation contrast by a factor. The
    * adjustment factor is expected to be in [-1.0, 1.0]. See
    * https://en.wikipedia.org/wiki/Colorfulness#Saturation .
    *
    * @param source the source pixels
    * @param sFac   the saturation contrast factor
    * @param lFac   the lightness contrast factor
    * @param policy the pivot policy
    * @param target the target pixels
    *
    * @return the adjusted pixels
    */
   @Experimental
   public static Img adjustContrast ( final Img source, final float sFac,
      final float lFac, final PivotPolicy policy, final Img target ) {

      if ( !Img.similar(source, target) ) { return target; }

      final int len = source.pixels.length;

      final float sNoNanFac = Float.isNaN(sFac) ? 0.5f : sFac;
      final float sAdjVerif = 1.0f + Utils.clamp(sNoNanFac, -1.0f, 1.0f);

      final float lNoNanFac = Float.isNaN(lFac) ? 0.5f : lFac;
      final float lAdjVerif = 1.0f + Utils.clamp(lNoNanFac, -1.0f, 1.0f);

      if ( Utils.approx(sAdjVerif, 1.0f) && Utils.approx(lAdjVerif, 1.0f) ) {
         System.arraycopy(source.pixels, 0, target.pixels, 0, len);
         return target;
      }

      final HashMap < Long, Lab > uniques = new HashMap <>();

      double minSat = Double.MAX_VALUE;
      double maxSat = -Double.MAX_VALUE;
      double sumSat = 0.0f;

      double minLight = Double.MAX_VALUE;
      double maxLight = -Double.MAX_VALUE;
      double sumLight = 0.0f;

      int sumTally = 0;

      for ( int i = 0; i < len; ++i ) {
         final long srcPixel = source.pixels[i];
         final Long srcPixelObj = srcPixel;
         if ( !uniques.containsKey(srcPixelObj) ) {
            final Lab lab = Lab.fromHex(srcPixel, new Lab());
            if ( lab.alpha > 0.0f ) {
               final double l = lab.l;
               final double a = lab.a;
               final double b = lab.b;

               final double csq = a * a + b * b;
               final double mcpl = Math.sqrt(csq + l * l);
               final double sat = mcpl != 0.0d ? Math.sqrt(csq) / mcpl : 0.0d;

               if ( sat > maxSat ) maxSat = sat;
               if ( sat < minSat ) minSat = sat;
               sumSat += sat;

               if ( l > maxLight ) maxLight = l;
               if ( l < minLight ) minLight = l;
               sumLight += l;

               ++sumTally;
            }
            uniques.put(srcPixelObj, lab);
         }
      }

      if ( sumTally == 0 || minSat >= maxSat && minLight >= maxLight ) {
         System.arraycopy(source.pixels, 0, target.pixels, 0, len);
         return target;
      }

      final double sadjd = sAdjVerif;
      final double ladjd = lAdjVerif;
      double pivotSat = sumSat / sumTally;
      double pivotLight = sumLight / sumTally;
      switch ( policy ) {
         case RANGE:
            pivotSat = ( minSat + maxSat ) * 0.5d;
            pivotLight = ( minLight + maxLight ) * 0.5d;
            break;

         case FIXED:
            pivotSat = sumSat / sumTally;
            pivotLight = 50.0d;
            break;

         case MEAN:
         default:
            pivotSat = sumSat / sumTally;
            pivotLight = sumLight / sumTally;
      }

      final Lab defLab = Lab.clearBlack(new Lab());
      final HashMap < Long, Long > convert = new HashMap <>();
      convert.put(Img.CLEAR_PIXEL, Img.CLEAR_PIXEL);

      for ( int j = 0; j < len; ++j ) {
         final long srcPixel = source.pixels[j];
         final Long srcPixelObj = srcPixel;
         long trgPixel = Img.CLEAR_PIXEL;

         if ( convert.containsKey(srcPixelObj) ) {
            trgPixel = convert.get(srcPixelObj);
         } else {
            final Lab lab = uniques.getOrDefault(srcPixelObj, defLab);

            final double lSrc = lab.l;
            final double aSrc = lab.a;
            final double bSrc = lab.b;

            final double lAdj = ( lSrc - pivotLight ) * ladjd + pivotLight;

            final double csqSrc = aSrc * aSrc + bSrc * bSrc;
            final double cSrc = Math.sqrt(csqSrc);
            final double mcplSrc = Math.sqrt(csqSrc + lSrc * lSrc);
            final double mcplAdj = Math.sqrt(csqSrc + lAdj * lAdj);
            final double sSrc = mcplSrc != 0.0d ? cSrc / mcplSrc : 0.0d;
            // final double sSrc = mcplAdj != 0.0d ? cSrc / mcplAdj : 0.0d;

            final double sAdj = ( sSrc - pivotSat ) * sadjd + pivotSat;
            final double cAdj = sAdj * mcplAdj;

            final double abScalar = cSrc != 0.0d ? cAdj / cSrc : 0.0d;
            final double aAdj = aSrc * abScalar;
            final double bAdj = bSrc * abScalar;

            lab.l = ( float ) lAdj;
            lab.a = ( float ) aAdj;
            lab.b = ( float ) bAdj;
            trgPixel = lab.toHexLongSat();
            convert.put(srcPixelObj, trgPixel);
         }

         target.pixels[j] = trgPixel;
      }

      return target;
   }

   /**
    * Adjusts an image's lightness and saturation contrast by a factor. The
    * adjustment factor is expected to be in [-1.0, 1.0].
    *
    * @param source the source pixels
    * @param fac    the contrast factor
    * @param target the target pixels
    *
    * @return the adjusted pixels
    */
   public static Img adjustContrast ( final Img source, final float fac,
      final Img target ) {

      return Img.adjustContrast(source, fac, fac, Img.DEFAULT_PIVOT_POLICY,
         target);
   }

   /**
    * Adjusts an image's lightness and saturation contrast by a factor. The
    * adjustment factor is expected to be in [-1.0, 1.0].
    *
    * @param source the source pixels
    * @param fac    the contrast factor
    * @param target the target pixels
    *
    * @return the adjusted pixels
    */
   public static Img adjustContrast ( final Img source, final Vec2 fac,
      final Img target ) {

      return Img.adjustContrast(source, fac.x, fac.y, Img.DEFAULT_PIVOT_POLICY,
         target);
   }

   /**
    * Adjusts the chroma contrast of colors from a source pixels array by a
    * factor. The adjustment factor is expected to be in [-1.0, 1.0].
    *
    * @param source the source pixels
    * @param fac    the contrast factor
    * @param target the target pixels
    *
    * @return the adjusted pixels
    */
   public static final Img adjustContrastChroma ( final Img source,
      final float fac, final Img target ) {

      return Img.adjustContrastChroma(source, fac, Img.DEFAULT_PIVOT_POLICY,
         target);
   }

   /**
    * Adjusts an image's he chroma contrast by a factor. The adjustment factor
    * is expected to be in [-1.0, 1.0].
    *
    * @param source the source pixels
    * @param fac    the contrast factor
    * @param policy the pivot policy
    * @param target the target pixels
    *
    * @return the adjusted pixels
    */
   public static final Img adjustContrastChroma ( final Img source,
      final float fac, final PivotPolicy policy, final Img target ) {

      /* Tested March 11 2025. */

      if ( !Img.similar(source, target) ) { return target; }

      final int len = source.pixels.length;
      final float noNanFac = Float.isNaN(fac) ? 0.5f : fac;
      final float adjVerif = 1.0f + Utils.clamp(noNanFac, -1.0f, 1.0f);

      if ( Utils.approx(adjVerif, 1.0f) ) {
         System.arraycopy(source.pixels, 0, target.pixels, 0, len);
         return target;
      }

      final Lab lab = new Lab();
      final HashMap < Long, Lch > uniques = new HashMap <>();

      float minChroma = Float.MAX_VALUE;
      float maxChroma = -Float.MAX_VALUE;
      float sumChroma = 0.0f;
      int sumTally = 0;

      for ( int i = 0; i < len; ++i ) {
         final long srcPixel = source.pixels[i];
         final Long srcPixelObj = srcPixel;
         if ( !uniques.containsKey(srcPixelObj) ) {
            Lab.fromHex(srcPixel, lab);
            if ( lab.alpha > 0.0f ) {
               final float chroma = Lab.chroma(lab);
               if ( chroma > maxChroma ) maxChroma = chroma;
               if ( chroma < minChroma ) minChroma = chroma;
               sumChroma += chroma;
               ++sumTally;
            }
            uniques.put(srcPixelObj, Lch.fromLab(lab, new Lch()));
         }
      }

      if ( sumTally == 0 || minChroma >= maxChroma ) {
         System.arraycopy(source.pixels, 0, target.pixels, 0, len);
         return target;
      }

      float pivotChroma = sumChroma / sumTally;
      switch ( policy ) {
         case RANGE:
            pivotChroma = ( minChroma + maxChroma ) * 0.5f;
            break;

         case FIXED:
         case MEAN:
         default:
      }

      final Lch defLch = Lch.clearBlack(new Lch());
      final HashMap < Long, Long > convert = new HashMap <>();
      convert.put(Img.CLEAR_PIXEL, Img.CLEAR_PIXEL);

      for ( int j = 0; j < len; ++j ) {
         final long srcPixel = source.pixels[j];
         final Long srcPixelObj = srcPixel;
         long trgPixel = Img.CLEAR_PIXEL;

         if ( convert.containsKey(srcPixelObj) ) {
            trgPixel = convert.get(srcPixelObj);
         } else {
            final Lch lch = uniques.getOrDefault(srcPixelObj, defLch);
            lch.c = ( lch.c - pivotChroma ) * adjVerif + pivotChroma;
            Lab.fromLch(lch, lab);
            trgPixel = lab.toHexLongSat();
            convert.put(srcPixelObj, trgPixel);
         }

         target.pixels[j] = trgPixel;
      }

      return target;
   }

   /**
    * Adjusts an image's light contrast by a factor. The adjustment factor is
    * expected to be in [-1.0, 1.0].
    *
    * @param source the source pixels
    * @param fac    the contrast factor
    * @param target the target pixels
    *
    * @return the adjusted pixels
    */
   public static final Img adjustContrastLight ( final Img source,
      final float fac, final Img target ) {

      /* Tested March 11 2025. */

      if ( !Img.similar(source, target) ) { return target; }

      final int len = source.pixels.length;
      final float noNanFac = Float.isNaN(fac) ? 0.5f : fac;
      final float adjVerif = 1.0f + Utils.clamp(noNanFac, -1.0f, 1.0f);

      if ( Utils.approx(adjVerif, 1.0f) ) {
         System.arraycopy(source.pixels, 0, target.pixels, 0, len);
         return target;
      }

      final float pivotLight = 50.0f;

      final Lab lab = new Lab();
      final HashMap < Long, Long > convert = new HashMap <>();
      convert.put(Img.CLEAR_PIXEL, Img.CLEAR_PIXEL);

      for ( int i = 0; i < len; ++i ) {
         final long srcPixel = source.pixels[i];
         final Long srcPixelObj = srcPixel;
         long trgPixel = Img.CLEAR_PIXEL;

         if ( convert.containsKey(srcPixelObj) ) {
            trgPixel = convert.get(srcPixelObj);
         } else {
            Lab.fromHex(srcPixel, lab);
            lab.l = ( lab.l - pivotLight ) * adjVerif + pivotLight;
            trgPixel = lab.toHexLongSat();
            convert.put(srcPixelObj, trgPixel);
         }

         target.pixels[i] = trgPixel;
      }

      return target;
   }

   /**
    * Adjusts a source image's pixels in LAB.
    *
    * @param source the source image
    * @param adjust the adjustment
    * @param target the target image
    *
    * @return the adjusted image
    */
   public static final Img adjustLab ( final Img source, final Lab adjust,
      final Img target ) {

      /* Tested March 11 2025. */

      if ( !Img.similar(source, target) ) { return target; }

      final int len = source.pixels.length;

      /* @formatter:off */
      if ( Utils.approx(adjust.l, 0.0f)
         && Utils.approx(adjust.a, 0.0f)
         && Utils.approx(adjust.b, 0.0f)
         && Utils.approx(adjust.alpha, 0.0f) ) {

         System.arraycopy(source.pixels, 0, target.pixels, 0, len);
         return target;
      }
      /* @formatter:on */

      final Lab lab = new Lab();
      final HashMap < Long, Long > convert = new HashMap <>();
      /* Do not put in clear pixel right away, as with other functions. */

      for ( int i = 0; i < len; ++i ) {
         final long srcPixel = source.pixels[i];
         final Long srcPixelObj = srcPixel;
         long trgPixel = Img.CLEAR_PIXEL;

         if ( convert.containsKey(srcPixelObj) ) {
            trgPixel = convert.get(srcPixelObj);
         } else {
            Lab.fromHex(srcPixel, lab);
            if ( lab.alpha <= 0.0f ) {
               Lab.clearBlack(lab);
            } else {
               lab.l += adjust.l;
               lab.a += adjust.a;
               lab.b += adjust.b;
               lab.alpha += adjust.alpha;
            }
            trgPixel = lab.toHexLongSat();
            convert.put(srcPixelObj, trgPixel);
         }

         target.pixels[i] = trgPixel;
      }

      return target;
   }

   /**
    * Adjusts a source image's pixels in LCH.
    *
    * @param source the source image
    * @param adjust the adjustment
    * @param policy the gray policy
    * @param target the target image
    *
    * @return the adjusted image
    */
   public static final Img adjustLch ( final Img source, final Lch adjust,
      final GrayPolicy policy, final Img target ) {

      if ( !Img.similar(source, target) ) { return target; }

      final int len = source.pixels.length;

      /* @formatter:off */
      if ( Utils.approx(adjust.l, 0.0f)
         && Utils.approx(adjust.c, 0.0f)
         && Utils.approx(Utils.mod1(adjust.h), 0.0f)
         && Utils.approx(adjust.alpha, 0.0f) ) {

         System.arraycopy(source.pixels, 0, target.pixels, 0, len);
         return target;
      }
      /* @formatter:on */

      final float hZero = 0.0f;
      final float hVio = Lch.SR_HUE_SHADE;
      final float hYel = Lch.SR_HUE_LIGHT;

      final Lab lab = new Lab();
      final Lch lch = new Lch();
      final HashMap < Long, Long > convert = new HashMap <>();
      /* Do not put in clear pixel right away, as with other functions. */

      for ( int i = 0; i < len; ++i ) {
         final long srcPixel = source.pixels[i];
         final Long srcPixelObj = srcPixel;
         long trgPixel = Img.CLEAR_PIXEL;

         if ( convert.containsKey(srcPixelObj) ) {
            trgPixel = convert.get(srcPixelObj);
         } else {
            Lab.fromHex(srcPixel, lab);
            if ( lab.alpha <= 0.0f ) {
               Lab.clearBlack(lab);
            } else {
               Lch.fromLab(lab, lch);

               float cTrg = 0.0f;
               float hTrg = 0.0f;
               final boolean isGray = lch.c < IUtils.EPSILON;
               if ( isGray ) {
                  // TODO: This needs to be tested.

                  switch ( policy ) {
                     case COOL: {
                        final float t = lch.l * 0.01f;
                        final float u = 1.0f - t;
                        final float hg = u * hVio + t * hYel;
                        cTrg = lch.c + adjust.c;
                        hTrg = hg + adjust.h;
                     }
                        break;

                     case WARM: {
                        final float t = lch.l * 0.01f;
                        final float u = 1.0f - t;
                        final float hg = u * hVio + t * ( hYel + 1.0f );
                        cTrg = lch.c + adjust.c;
                        hTrg = hg + adjust.h;
                     }
                        break;

                     case ZERO: {
                        cTrg = lch.c + adjust.c;
                        hTrg = hZero + adjust.h;
                     }
                        break;

                     case OMIT:

                     default: {
                        cTrg = 0.0f;
                        hTrg = 0.0f;
                     }
                  }
               } else {
                  cTrg = lch.c + adjust.c;
                  hTrg = lch.h + adjust.h;
               }

               lch.l += adjust.l;
               lch.c = cTrg;
               lch.h = hTrg;
               lch.alpha += adjust.alpha;

               Lab.fromLch(lch, lab);
            }
            trgPixel = lab.toHexLongSat();
            convert.put(srcPixelObj, trgPixel);
         }

         target.pixels[i] = trgPixel;
      }

      return target;
   }

   /**
    * Adjusts a source image's pixels in LCH.
    *
    * @param source the source image
    * @param adjust the adjustment
    * @param target the target image
    *
    * @return the adjusted image
    */
   public static final Img adjustLch ( final Img source, final Lch adjust,
      final Img target ) {

      return Img.adjustLch(source, adjust, Img.DEFAULT_GRAY_POLICY, target);
   }

   /**
    * Finds the aspect ratio of an image, its width divided by its height.
    *
    * @param image the image
    *
    * @return the aspect ratio
    *
    * @see Utils#div(float, float)
    */
   public static final float aspect ( final PImage image ) {

      return Utils.div(( float ) image.width, ( float ) image.height);
   }

   public static final Img blend (
   /* @formatter:off */
      final Img imgUnder, final int xUnder, final int yUnder,
      final Img imgOver, final int xOver, final int yOver,

      final BlendMode.Alpha bmAlpha,
      final BlendMode.L bmLight,
      final BlendMode.AB bmAb
   /* @formatter:on */
   ) {

      // TODO: This may have to be generalized to accept an array of images.

      // TODO: Maybe the blend space and LCH options can be omitted
      // if you fold them into the AB blend mode, and do CH conversions
      // with the Lab class.

      /*
       * Images do not need to be similar to each other.
       */

      final int ax = xUnder;
      final int ay = yUnder;
      final int aw = imgUnder.width;
      final int ah = imgUnder.height;
      final long[] pxUnder = imgUnder.pixels;

      final int bx = xOver;
      final int by = yOver;
      final int bw = imgOver.width;
      final int bh = imgOver.height;
      final long[] pxOver = imgOver.pixels;

      /* Find the bottom right corner for a and b. */
      final int abrx = ax + aw - 1;
      final int abry = ay + ah - 1;
      final int bbrx = bx + bw - 1;
      final int bbry = by + bh - 1;

      /*
       * Find the union of a and b. This used to be an intersection, but
       * different alpha blend modes necessitate a union.
       */

      // TODO: Adjust this based on the alpha comp type?

      final int dx = ax < bx ? ax : bx;
      final int dy = ay < by ? ay : by;
      final int dbrx = abrx > bbrx ? abrx : bbrx;
      final int dbry = abry > bbry ? abry : bbry;
      final int dw = 1 + dbrx - dx;
      final int dh = 1 + dbry - dy;

      final HashMap < Long, Lab > dict = new HashMap <>();
      final Lab clearLab = Lab.clearBlack(new Lab());
      dict.put(Img.CLEAR_PIXEL, clearLab);

      if ( dw > 0 && dh > 0 ) {

         /*
          * Find difference between the intersection top left and the top left
          * of a and b.
          */
         final int axid = ax - dx;
         final int ayid = ay - dy;
         final int bxid = bx - dx;
         final int byid = by - dy;

         final int dLen = dw * dh;
         for ( int h = 0; h < dLen; ++h ) {
            final int x = h % dw;
            final int y = h / dw;

            long hexUnder = Img.CLEAR_PIXEL;
            final int axs = x - axid;
            final int ays = y - ayid;
            if ( ays >= 0 && ays < ah && axs >= 0 && axs < aw ) {
               hexUnder = pxUnder[axs + ays * aw];
               dict.put(hexUnder, Lab.fromHex(hexUnder, new Lab()));
            }

            long hexOver = Img.CLEAR_PIXEL;
            final int bxs = x - bxid;
            final int bys = y - byid;
            if ( bys >= 0 && bys < bh && bxs >= 0 && bxs < bw ) {
               hexOver = pxOver[bxs + bys * bw];
               dict.put(hexOver, Lab.fromHex(hexOver, new Lab()));
            }
         }
      }

      /* The result dimensions are the union of a and b. */
      final int cx = ax < bx ? ax : bx;
      final int cy = ay < by ? ay : by;
      final int cbrx = abrx > bbrx ? abrx : bbrx;
      final int cbry = abry > bbry ? abry : bbry;
      final int cw = 1 + cbrx - cx;
      final int ch = 1 + cbry - cy;
      final int cLen = cw * ch;

      /* Find difference between the union top left and top left of a and b. */
      final int axud = ax - cx;
      final int ayud = ay - cy;
      final int bxud = bx - cx;
      final int byud = by - cy;

      final Lab cLab = new Lab();
      final long[] trgPixels = new long[cLen];
      for ( int i = 0; i < cLen; ++i ) {
         final int x = i % cw;
         final int y = i / cw;

         long hexUnder = Img.CLEAR_PIXEL;
         final int axs = x - axud;
         final int ays = y - ayud;
         if ( ays >= 0 && ays < ah && axs >= 0 && axs < aw ) {
            hexUnder = pxUnder[axs + ays * aw];
         }

         long hexOver = Img.CLEAR_PIXEL;
         final int bxs = x - bxud;
         final int bys = y - byud;
         if ( bys >= 0 && bys < bh && bxs >= 0 && bxs < bw ) {
            hexOver = pxOver[bxs + bys * bw];
         }

         final Lab labUnder = dict.getOrDefault(hexUnder, clearLab);
         final Lab labOver = dict.getOrDefault(hexOver, clearLab);

         final double t = labOver.alpha;
         final double v = labUnder.alpha;
         final double u = 1.0d - t;
         double tuv = t + u * v;

         switch ( bmAlpha ) {
            case MAX: {
               tuv = t > v ? t : v;
            }
               break;

            case MIN: {
               tuv = t < v ? t : v;
            }
               break;

            case MULTIPLY: {
               tuv = t * v;
            }
               break;

            case OVER: {
               tuv = t;
            }
               break;

            case UNDER: {
               tuv = v;
            }
               break;

            case BLEND:
            default:

         } // End alpha blend mode.

         long hexComp = Img.CLEAR_PIXEL;
         if ( tuv > 0.0d ) {
            // Does lab over need to default to lab under if it has zero alpha
            // and vice versa?
            final boolean vgt0 = v > 0.0d;
            final boolean tgt0 = t > 0.0d;

            final double lOver = labOver.l;
            final double aOver = labOver.a;
            final double bOver = labOver.b;

            final double lUnder = labUnder.l;
            final double aUnder = labUnder.a;
            final double bUnder = labUnder.b;

            double lComp = u * lUnder + t * lOver;
            double aComp = u * aUnder + t * aOver;
            double bComp = u * bUnder + t * bOver;

            switch ( bmLight ) {
               case ADD: {
                  final double sum = vgt0 ? lUnder + lOver : lOver;
                  lComp = u * lUnder + t * sum;
               }
                  break;

               case AVERAGE: {
                  final double avg = vgt0 ? ( lUnder + lOver ) * 0.5d : lOver;
                  lComp = u * lUnder + t * avg;
               }
                  break;

               case DIVIDE: {
                  final double quo = vgt0 ? lOver != 0.0d ? lUnder / lOver
                     * 100.0d : 100.0d : lOver;
                  lComp = u * lUnder + t * quo;
               }
                  break;

               case MULTIPLY: {
                  final double prod = vgt0 ? lUnder * lOver * 0.01d : lOver;
                  lComp = u * lUnder + t * prod;
               }
                  break;

               case OVER: {
                  lComp = lOver;
               }
                  break;

               case SCREEN: {
                  final double scr = vgt0 ? lUnder + lOver - lUnder * lOver
                     * 0.01 : lOver;
                  lComp = u * lUnder + t * scr;
               }
                  break;

               case SUBTRACT: {
                  final double dff = vgt0 ? lUnder - lOver : lOver;
                  lComp = u * lUnder + t * dff;
               }
                  break;

               case UNDER: {
                  lComp = lUnder;
               }
                  break;

               case BLEND:
               default:

            } // End light blend mode.

            switch ( bmAb ) {
               case ADD: {
                  final double aSum = vgt0 ? aUnder + aOver : aOver;
                  final double bSum = vgt0 ? bUnder + bOver : bOver;
                  aComp = u * aUnder + t * aSum;
                  bComp = u * bUnder + t * bSum;
               }
                  break;

               case AVERAGE: {
                  final double aAvg = vgt0 ? ( aUnder + aOver ) * 0.5d : aOver;
                  final double bAvg = vgt0 ? ( bUnder + bOver ) * 0.5d : bOver;
                  aComp = u * aUnder + t * aAvg;
                  bComp = u * bUnder + t * bAvg;
               }
                  break;

               case CHROMA: {

                  if ( vgt0 && tgt0 ) {

                     final double csqUnder = aUnder * aUnder + bUnder * bUnder;

                     if ( csqUnder > IUtils.EPSILON_D ) {
                        final double csqOver = aOver * aOver + bOver * bOver;
                        final double s = Math.sqrt(csqOver) / Math.sqrt(
                           csqUnder);
                        aComp = s * aUnder;
                        bComp = s * bUnder;

                     } else {

                        aComp = 0.0d;
                        bComp = 0.0d;
                     } // End chroma under is greater than zero.

                  } // End under alpha is greater than zero.
               }
                  break;

               case HUE: {

                  if ( vgt0 && tgt0 ) {

                     final double csqOver = aOver * aOver + bOver * bOver;

                     if ( csqOver > IUtils.EPSILON_D ) {

                        final double csqUnder = aUnder * aUnder + bUnder
                           * bUnder;
                        final double s = Math.sqrt(csqUnder) / Math.sqrt(
                           csqOver);
                        aComp = s * aOver;
                        bComp = s * bOver;

                     } else {

                        aComp = 0.0d;
                        bComp = 0.0d;

                     } // End chroma over is greater than zero.

                  } // End under alpha is greater than zero.

               }
                  break;

               case OVER: {
                  aComp = aOver;
                  bComp = bOver;
               }
                  break;

               case SUBTRACT: {
                  // TODO: Should default case for aDiff be -aOver, -bOver, not
                  // positive?
                  final double aDff = vgt0 ? aUnder - aOver : aOver;
                  final double bDff = vgt0 ? bUnder - bOver : bOver;
                  aComp = u * aUnder + t * aDff;
                  bComp = u * bUnder + t * bDff;
               }
                  break;

               case UNDER: {
                  aComp = aUnder;
                  bComp = bUnder;
               }
                  break;

               case BLEND:
               default:

            } // End ab blend mode.

            cLab.set(( float ) lComp, ( float ) aComp, ( float ) bComp,
               ( float ) tuv);
            hexComp = cLab.toHexLongSat();

         } // End alpha is greater than zero.

         trgPixels[i] = hexComp;

      } // End pixels loop.

      return new Img(cw, ch, trgPixels);
   }

   /**
    * Gets the image pixel data as a byte array. Bytes are ordered from least
    * to most significant digit (little endian).
    *
    * @param source the source image
    *
    * @return the byte array
    *
    * @see Utils#byteslm(long, byte[], int)
    */
   public static final byte[] byteslm ( final Img source ) {

      final int len = source.pixels.length;
      final byte[] arr = new byte[len * 8];
      for ( int i = 0, j = 0; i < len; ++i, j += 8 ) {
         Utils.byteslm(source.pixels[i], arr, j);
      }
      return arr;
   }

   /**
    * Gets the image pixel data as a byte array. Bytes are ordered from most
    * to least significant digit (big endian).
    *
    * @param source the source image
    *
    * @return the byte array
    *
    * @see Utils#bytesml(long, byte[], int)
    */
   public static final byte[] bytesml ( final Img source ) {

      final int len = source.pixels.length;
      final byte[] arr = new byte[len * 8];
      for ( int i = 0, j = 0; i < len; ++i, j += 8 ) {
         Utils.bytesml(source.pixels[i], arr, j);
      }
      return arr;
   }

   /**
    * Creates a checker pattern in an image.
    *
    * @param sizeCheck the checker size
    * @param target    the output image
    *
    * @return the checker image
    */
   public static final Img checker ( final int sizeCheck, final Img target ) {

      return Img.checker(Img.CHECKER_DARK, Img.CHECKER_LIGHT, sizeCheck,
         sizeCheck, target);
   }

   /**
    * Creates a checker pattern in an image.
    *
    * @param a         the first color
    * @param b         the second color
    * @param sizeCheck the checker size
    * @param target    the output image
    *
    * @return the checker image
    */
   public static final Img checker ( final Lab a, final Lab b,
      final int sizeCheck, final Img target ) {

      return Img.checker(a.toHexLongSat(), b.toHexLongSat(), sizeCheck,
         sizeCheck, target);
   }

   /**
    * Creates a checker pattern in an image.
    *
    * @param a      the first color
    * @param b      the second color
    * @param wCheck the checker width
    * @param hCheck the checker height
    * @param target the output image
    *
    * @return the checker image
    */
   public static final Img checker ( final Lab a, final Lab b, final int wCheck,
      final int hCheck, final Img target ) {

      return Img.checker(a.toHexLongSat(), b.toHexLongSat(), wCheck, hCheck,
         target);
   }

   /**
    * Creates a checker pattern in an image.
    *
    * @param a         the first color
    * @param b         the second color
    * @param sizeCheck the checker size
    * @param target    the output image
    *
    * @return the checker image
    */
   public static Img checker ( final long a, final long b, final int sizeCheck,
      final Img target ) {

      return Img.checker(a, b, sizeCheck, sizeCheck, target);
   }

   /**
    * Creates )a checker pattern in an image. For making a background canvas
    * that signals transparency in the pixels of an image layer(s) above it.
    *
    * @param a      the first color
    * @param b      the second color
    * @param wCheck the checker width
    * @param hCheck the checker height
    * @param target the output image
    *
    * @return the checker image
    */
   public static Img checker ( final long a, final long b, final int wCheck,
      final int hCheck, final Img target ) {

      /* Tested March 11 2025. */

      final int w = target.width;
      final int h = target.height;
      final int shortEdge = w < h ? w : h;
      final int wcVerif = Utils.clamp(wCheck, 1, shortEdge / 2);
      final int hcVerif = Utils.clamp(hCheck, 1, shortEdge / 2);

      /*
       * User may want to blend checker over a layer beneath, so clear pixel
       * should be allowed.
       */
      final long va = a != b ? a : Img.CHECKER_DARK;
      final long vb = a != b ? b : Img.CHECKER_LIGHT;

      final int len = target.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         target.pixels[i] = ( i % w / wcVerif + i / w / hcVerif & 1 ) == 0 ? va
            : vb;
      }

      return target;
   }

   /**
    * Clears all pixels in an image to {@link Img#CLEAR_PIXEL},
    * {@value Img#CLEAR_PIXEL}.
    *
    * @param target the output image
    *
    * @return the filled image
    */
   public static final Img clear ( final Img target ) {

      return Img.fill(Img.CLEAR_PIXEL, target);
   }

   /**
    * Fills an image with a color.
    *
    * @param fill   the fill color
    * @param target the output image
    *
    * @return the filled image
    */
   public static final Img fill ( final Lab fill, final Img target ) {

      return Img.fill(fill.toHexLongSat(), target);
   }

   /**
    * Fills an image with a color.
    *
    * @param fill   the fill color
    * @param target the output image
    *
    * @return the filled image
    */
   public static final Img fill ( final long fill, final Img target ) {

      final int len = target.pixels.length;
      for ( int i = 0; i < len; ++i ) { target.pixels[i] = fill; }
      return target;
   }

   /**
    * Hashes an image according to the <a href=
    * "https://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function">Fowler–Noll–Vo</a>
    * method.
    *
    * @param source the image
    *
    * @return the hash
    */
   public static BigInteger fnvHash ( final Img source ) {

      // TODO: TEST

      final BigInteger fnvPrime = BigInteger.valueOf(1099511628211L);
      BigInteger hash = new BigInteger("14695981039346656037");
      final long[] srcPixels = source.pixels;
      final int len = srcPixels.length;
      for ( int i = 0; i < len; ++i ) {
         hash = hash.xor(BigInteger.valueOf(srcPixels[i])).multiply(fnvPrime);
      }

      return hash;
   }

   /**
    * Converts a pixel array of 32-bit integers in the format 0xAARRGGBB to a
    * LAB image. Width and height should be in pixels. It should not be the
    * virtual width and height that accounts for pixel density.
    *
    * @param width   the width
    * @param height  the height
    * @param argb32s the pixel array
    *
    * @return the image
    */
   public static final Img fromArgb32 ( final int width, final int height,
      final int[] argb32s ) {

      /* Tested March 11 2025. */

      final int wVerif = Utils.clamp(Math.abs(width), 1, Img.MAX_DIMENSION);
      final int hVerif = Utils.clamp(Math.abs(height), 1, Img.MAX_DIMENSION);

      final int area = wVerif * hVerif;
      final int len = argb32s.length;
      if ( area != len ) {
         System.err.println("Pixel length does not match image area.");
         return new Img(width, height);
      }

      final Rgb srgb = new Rgb();
      final Rgb lrgb = new Rgb();
      final Vec4 xyz = new Vec4();
      final Lab lab = new Lab();

      final long[] tlab64s = new long[len];
      final HashMap < Integer, Long > convert = new HashMap <>();
      convert.put(0, Img.CLEAR_PIXEL);

      for ( int i = 0; i < len; ++i ) {
         final int argb32 = argb32s[i];
         final Integer argb32Obj = argb32;
         long tlab64 = Img.CLEAR_PIXEL;

         if ( convert.containsKey(argb32Obj) ) {
            tlab64 = convert.get(argb32Obj);
         } else {
            Rgb.fromHex(argb32, srgb);
            Rgb.sRgbToSrLab2(srgb, lab, xyz, lrgb);
            tlab64 = lab.toHexLongSat();
            convert.put(argb32Obj, tlab64);
         }

         tlab64s[i] = tlab64;
      }

      return new Img(wVerif, hVerif, tlab64s);
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The origin and destination should be in the range [-1.0, 1.0]. The
    * scalar projection is clamped to [0.0, 1.0].
    *
    * @param grd    the gradient
    * @param xOrig  the origin x coordinate
    * @param yOrig  the origin y coordinate
    * @param xDest  the destination x coordinate
    * @param yDest  the destination y coordinate
    * @param easing the easing function
    * @param target the output image
    *
    * @return the gradient image
    *
    * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
    * @see Utils#max(float, float)
    * @see Utils#clamp01(float)
    */
   public final static Img gradientLinear ( final Gradient grd,
      final float xOrig, final float yOrig, final float xDest,
      final float yDest, final Lab.AbstrEasing easing, final Img target ) {

      // TODO: ZImage had a lot of overloads for this method so as to simplify
      // the signature. You'll have to look at a past git commit to restore
      // them.

      final int wTrg = target.width;
      final int hTrg = target.height;

      final float bx = xOrig - xDest;
      final float by = yOrig - yDest;

      final float bbInv = 1.0f / Utils.max(IUtils.EPSILON, bx * bx + by * by);

      final float bxbbinv = bx * bbInv;
      final float bybbinv = by * bbInv;

      final float xobx = xOrig * bxbbinv;
      final float yoby = yOrig * bybbinv;
      final float bxwInv2 = 2.0f / ( wTrg - 1.0f ) * bxbbinv;
      final float byhInv2 = 2.0f / ( hTrg - 1.0f ) * bybbinv;

      final Lab trgLab = new Lab();
      final int len = target.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         final float fac = Utils.clamp01(xobx + bxbbinv - bxwInv2 * ( i % wTrg )
            + ( yoby + byhInv2 * ( i / wTrg ) - bybbinv ));
         Gradient.eval(grd, fac, easing, trgLab);
         target.pixels[i] = trgLab.toHexLongSat();
      }

      return target;
   }

   /**
    * Generates a radial gradient from an origin point. The origin should be
    * in the range [-1.0, 1.0]. Does not account for aspect ratio, so an image
    * that isn't 1:1 will result in an ellipsoid.
    *
    * @param grd    the gradient
    * @param xOrig  the origin x coordinate
    * @param yOrig  the origin y coordinate
    * @param radius the radius
    * @param easing the easing function
    * @param target the output image
    *
    * @return the gradient image
    *
    * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
    * @see Utils#max(float, float)
    */
   public static Img gradientRadial ( final Gradient grd, final float xOrig,
      final float yOrig, final float radius, final Lab.AbstrEasing easing,
      final Img target ) {

      // TODO: ZImage had a lot of overloads for this method so as to simplify
      // the signature. You'll have to look at a past git commit to restore
      // them.

      final int wTrg = target.width;
      final int hTrg = target.height;

      final float hInv2 = 2.0f / ( hTrg - 1.0f );
      final float wInv2 = 2.0f / ( wTrg - 1.0f );

      final float r2 = radius + radius;
      final float rsqInv = 1.0f / Utils.max(IUtils.EPSILON, r2 * r2);

      final float yon1 = yOrig - 1.0f;
      final float xop1 = xOrig + 1.0f;

      final Lab trgLab = new Lab();
      final int len = target.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         final float ay = yon1 + hInv2 * ( i / wTrg );
         final float ax = xop1 - wInv2 * ( i % wTrg );
         final float fac = 1.0f - ( ax * ax + ay * ay ) * rsqInv;
         Gradient.eval(grd, fac, easing, trgLab);
         target.pixels[i] = trgLab.toHexLongSat();
      }

      return target;
   }

   /**
    * Generates a conic gradient, where the factor rotates on the z axis
    * around an origin point. Best used with square images; for other aspect
    * ratios, the origin should be adjusted accordingly.
    *
    * @param grd     the gradient
    * @param xOrig   the origin x coordinate
    * @param yOrig   the origin y coordinate
    * @param radians the angle in radians
    * @param easing  the easing function
    * @param target  the target image
    *
    * @return the gradient image
    *
    * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
    * @see Utils#mod1(float)
    */
   public final static Img gradientSweep ( final Gradient grd,
      final float xOrig, final float yOrig, final float radians,
      final Lab.AbstrEasing easing, final Img target ) {

      // TODO: ZImage had a lot of overloads for this method so as to simplify
      // the signature. You'll have to look at a past git commit to restore
      // them.

      final int wTrg = target.width;
      final int hTrg = target.height;

      final double aspect = wTrg / ( double ) hTrg;
      final double wInv = aspect / ( wTrg - 1.0d );
      final double hInv = 1.0d / ( hTrg - 1.0d );
      final double xo = ( xOrig * 0.5d + 0.5d ) * aspect * 2.0d - 1.0d;
      final double yo = yOrig;
      final double rd = radians;

      final Lab trgLab = new Lab();
      final int len = target.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         final double xn = wInv * ( i % wTrg );
         final double yn = hInv * ( i / wTrg );
         final float fac = Utils.mod1(( float ) ( ( Math.atan2(1.0d - ( yn + yn
            + yo ), xn + xn - xo - 1.0d) - rd ) * IUtils.ONE_TAU_D ));
         Gradient.eval(grd, fac, easing, trgLab);
         target.pixels[i] = trgLab.toHexLongSat();
      }

      return target;
   }

   /**
    * Diminishes the vividness an image to gray by a factor in [0.0, 1.0].
    *
    * @param source the source image
    * @param fac    the factor
    * @param target the target image
    *
    * @return the gray image
    */
   public static final Img grayscale ( final Img source, final float fac,
      final Img target ) {

      /* Tested March 11 2025. */

      if ( Float.isNaN(fac) || fac >= 1.0f ) {
         return Img.grayscale(source, target);
      }

      if ( !Img.similar(source, target) ) { return target; }

      final int len = source.pixels.length;

      if ( fac <= 0.0f ) {
         System.arraycopy(source.pixels, 0, target.pixels, 0, len);
         return target;
      }

      final float u = 1.0f - fac;
      final Lab lab = new Lab();
      final HashMap < Long, Long > convert = new HashMap <>();
      convert.put(Img.CLEAR_PIXEL, Img.CLEAR_PIXEL);

      for ( int i = 0; i < len; ++i ) {
         final long srcPixel = source.pixels[i];
         final Long srcPixelObj = srcPixel;
         long trgPixel = Img.CLEAR_PIXEL;

         if ( convert.containsKey(srcPixelObj) ) {
            trgPixel = convert.get(srcPixelObj);
         } else {
            Lab.fromHex(srcPixel, lab);
            lab.a = u * lab.a;
            lab.b = u * lab.b;
            trgPixel = lab.toHexLongSat();
            convert.put(srcPixelObj, trgPixel);
         }

         target.pixels[i] = trgPixel;
      }

      return target;
   }

   /**
    * Converts an image to gray.
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the gray image
    */
   public static final Img grayscale ( final Img source, final Img target ) {

      if ( !Img.similar(source, target) ) { return target; }

      final int len = source.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         target.pixels[i] = source.pixels[i] & Img.ISOLATE_TL_MASK
            | Img.CLEAR_PIXEL;
      }

      return target;
   }

   /**
    * Inverts all channels of an image.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the inverted image
    */
   public static Img invert ( final Img source, final Img target ) {

      return Img.invert(source, Img.ISOLATE_TLAB_MASK, target);
   }

   /**
    * Inverts the chroma in an image.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the inverted image
    */
   public static final Img invertAB ( final Img source, final Img target ) {

      return Img.invert(source, Img.ISOLATE_AB_MASK, target);
   }

   /**
    * Inverts the transparency in an image.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the inverted image
    */
   public static final Img invertAlpha ( final Img source, final Img target ) {

      return Img.invert(source, Img.ISOLATE_T_MASK, target);
   }

   /**
    * Inverts the l, a and b channels in an image.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the inverted image
    */
   public static final Img invertLAB ( final Img source, final Img target ) {

      return Img.invert(source, Img.ISOLATE_LAB_MASK, target);
   }

   /**
    * Inverts the lightness in an image.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the inverted image
    */
   public static final Img invertLight ( final Img source, final Img target ) {

      return Img.invert(source, Img.ISOLATE_L_MASK, target);
   }

   /**
    * Tests whether an image contains all clear pixels.
    *
    * @param source the source image
    *
    * @return the evaluation
    */
   public static boolean isClear ( final Img source ) {

      return Img.isClearCasual(source);
   }

   /**
    * Tests whether an image contains all clear pixels. Pixels must have zero
    * alpha to be considered clear. However, they may have other non-zero
    * channels.
    *
    * @param source the source image
    *
    * @return the evaluation
    */
   public static final boolean isClearCasual ( final Img source ) {

      final int len = source.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         if ( ( source.pixels[i] & Img.ISOLATE_T_MASK ) != 0 ) { return false; }
      }
      return true;
   }

   /**
    * Tests whether an image contains all clear pixels. Pixels must be equal
    * to {@link Img#CLEAR_PIXEL}, {@value Img#CLEAR_PIXEL}, to be considered
    * clear.
    *
    * @param source the source image
    *
    * @return the evaluation
    */
   public static final boolean isClearStrict ( final Img source ) {

      final int len = source.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         if ( source.pixels[i] != Img.CLEAR_PIXEL ) { return false; }
      }
      return true;
   }

   /**
    * Mixes between two images by a factor.
    *
    * @param orig   the origin image
    * @param dest   the destination image
    * @param fac    the factor
    * @param target the output image
    *
    * @return the mixed image
    */
   public static Img mix ( final Img orig, final Img dest, final float fac,
      final Img target ) {

      /* Tested March 11 2025. */

      if ( !Img.similar(orig, dest) || !Img.similar(orig, target) || !Img
         .similar(dest, target) ) {
         return target;
      }

      final int len = target.pixels.length;
      final float t = Float.isNaN(fac) ? 0.5f : fac;

      if ( t <= 0.0f ) {
         System.arraycopy(orig.pixels, 0, target.pixels, 0, len);
         return target;
      }

      if ( t >= 1.0f ) {
         System.arraycopy(dest.pixels, 0, target.pixels, 0, len);
         return target;
      }

      final Lab oLab = new Lab();
      final Lab dLab = new Lab();
      final Lab tLab = new Lab();

      for ( int i = 0; i < len; ++i ) {
         Lab.fromHex(dest.pixels[i], dLab);
         Lab.fromHex(orig.pixels[i], oLab);
         Lab.mix(oLab, dLab, t, tLab);
         target.pixels[i] = tLab.toHexLongSat();
      }

      return target;
   }

   /**
    * Multiplies the image's alpha by the scalar.
    *
    * @param target the output image
    * @param alpha  the alpha scalar
    *
    * @return the multiplied alpha
    */
   public static final Img mulAlpha ( final Img target, final float alpha ) {

      return Img.mulAlpha(target, Utils.round(Utils.abs(alpha) * 0xffff));
   }

   /**
    * Normalizes an image's lightness so that it fills the complete range from
    * [0.0, 100.0]. Accepts a factor in [-1.0, 1.0]. If the factor is
    * negative, reduces contrast towards the average lightness.
    *
    * @param source the input image
    * @param fac    the factor
    * @param target the output image
    *
    * @return the normalized image
    */
   public static final Img normalizeLight ( final Img source, final float fac,
      final Img target ) {

      if ( !Img.similar(source, target) ) { return target; }

      final int len = source.pixels.length;
      final float noNanFac = Float.isNaN(fac) ? 1.0f : fac;
      final float facVerif = Utils.clamp(noNanFac, -1.0f, 1.0f);

      if ( Utils.approx(facVerif, 0.0f) ) {
         System.arraycopy(source.pixels, 0, target.pixels, 0, len);
         return target;
      }

      final HashMap < Long, Lab > uniques = new HashMap <>();

      float minLight = Float.MAX_VALUE;
      float maxLight = -Float.MAX_VALUE;
      float sumLight = 0.0f;
      int sumTally = 0;

      for ( int i = 0; i < len; ++i ) {
         final long srcPixel = source.pixels[i];
         final Long srcPixelObj = srcPixel;
         if ( !uniques.containsKey(srcPixelObj) ) {
            final Lab lab = Lab.fromHex(srcPixel, new Lab());
            if ( lab.alpha > 0.0f ) {
               final float light = lab.l;
               if ( light > maxLight ) maxLight = light;
               if ( light < minLight ) minLight = light;
               sumLight += light;
               ++sumTally;
            }
            uniques.put(srcPixelObj, lab);
         }
      }

      final float dff = Utils.diff(minLight, maxLight);
      if ( sumTally == 0 || dff < Img.MIN_LIGHT_DIFF ) {
         System.arraycopy(source.pixels, 0, target.pixels, 0, len);
         return target;
      }

      final float t = Utils.abs(facVerif);
      final float u = 1.0f - t;
      final boolean gtZero = facVerif > 0.0f;
      final boolean ltZero = facVerif < -0.0f;

      final float tLumAvg = t * ( sumLight / sumTally );
      final float tDenom = t * ( 100.0f / dff );
      final float lumMintDenom = minLight * tDenom;

      final Lab defLab = Lab.clearBlack(new Lab());
      final HashMap < Long, Long > convert = new HashMap <>();
      convert.put(Img.CLEAR_PIXEL, Img.CLEAR_PIXEL);

      for ( int j = 0; j < len; ++j ) {
         final long srcPixel = source.pixels[j];
         final Long srcPixelObj = srcPixel;
         long trgPixel = Img.CLEAR_PIXEL;

         if ( convert.containsKey(srcPixelObj) ) {
            trgPixel = convert.get(srcPixelObj);
         } else {
            final Lab lab = uniques.getOrDefault(srcPixelObj, defLab);

            if ( gtZero ) {
               lab.l = u * lab.l + lab.l * tDenom - lumMintDenom;
            } else if ( ltZero ) { lab.l = u * lab.l + tLumAvg; }

            trgPixel = lab.toHexLongSat();
            convert.put(srcPixelObj, trgPixel);
         }

         target.pixels[j] = trgPixel;
      }

      return target;
   }

   /**
    * Normalizes an image's lightness so that it fills the complete range from
    * [0.0, 100.0].
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the normalized image
    */
   public static final Img normalizeLight ( final Img source,
      final Img target ) {

      return Img.normalizeLight(source, 1f, target);
   }

   /**
    * Sets all pixels in an image to opaque.
    *
    * @param target the output image
    *
    * @return the opaque image
    */
   public static final Img opaque ( final Img target ) {

      final int len = target.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         target.pixels[i] = target.pixels[i] | Img.ISOLATE_T_MASK;
      }
      return target;
   }

   /**
    * Removes translucent pixels from an image, so colors are either
    * transparent or opaque.
    *
    * @param target
    *
    * @return the binary alpha image
    */
   public static final Img removeTranslucency ( final Img target ) {

      final int len = target.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         final long c = target.pixels[i];
         final long t16Src = c >> Img.T_SHIFT & 0xffffL;
         final long t16Trg = t16Src < 0x80000L ? 0x0000L : 0xffffL;
         target.pixels[i] = t16Trg << Img.T_SHIFT | c & Img.ISOLATE_LAB_MASK;
      }
      return target;
   }

   /**
    * Creates an image with a diagnostic image where the pixel's x coordinate
    * correlates to the red channel. Its y coordinate coordinates to the green
    * channel. The blue and alpha channels are expected to be in [0.0, 1.0].
    *
    * @param blue   the blue channel
    * @param alpha  the alpha channel
    * @param target the output image
    *
    * @return the RGB square
    */
   public static final Img rgb ( final float blue, final float alpha,
      final Img target ) {

      final int w = target.width;
      final int h = target.height;

      /*
       * Do not return a clear black image as with other images. If user wants
       * to make a zero alpha image that has rgb channels, let them.
       */
      final float tVerif = Utils.clamp01(alpha);
      final float bVerif = Utils.clamp01(blue);
      final float yNorm = 1.0f / ( h - 1.0f );
      final float xNorm = 1.0f / ( w - 1.0f );

      final Rgb srgb = new Rgb();
      final Rgb lrgb = new Rgb();
      final Vec4 xyz = new Vec4();
      final Lab lab = new Lab();

      final int len = target.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         final int x = i % w;
         final int y = i / w;
         srgb.set(x * xNorm, 1.0f - y * yNorm, bVerif, tVerif);
         Rgb.sRgbToSrLab2(srgb, lab, xyz, lrgb);
         target.pixels[i] = lab.toHexLongSat();
      }

      return target;
   }

   /**
    * Creates an image with a diagnostic image where the pixel's x coordinate
    * correlates to the red channel. Its y coordinate coordinates to the green
    * channel. The blue channel is expected to be in [0.0, 1.0].
    *
    * @param blue   the blue channel
    * @param target the output image
    *
    * @return the RGB square
    */
   public static final Img rgb ( final float blue, final Img target ) {

      return Img.rgb(blue, Img.DEFAULT_ALPHA, target);
   }

   /**
    * Creates an image with a diagnostic image where the pixel's x coordinate
    * correlates to the red channel. Its y coordinate coordinates to the green
    * channel.
    *
    * @param target the output image
    *
    * @return the RGB square
    */
   public static final Img rgb ( final Img target ) {

      return Img.rgb(Img.DEFAULT_BLUE, target);
   }

   /**
    * Rotates the source pixel array 180 degrees counter-clockwise. The
    * rotation is stored in the target pixel array.
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the rotated image
    */
   public static final Img rotate180 ( final Img source, final Img target ) {

      if ( !Img.similar(source, target) ) { return target; }
      final int len = source.pixels.length;
      for ( int i = 0, j = len - 1; i < len; ++i, --j ) {
         target.pixels[j] = source.pixels[i];
      }

      return target;
   }

   /**
    * Rotates the source image 270 degrees counter-clockwise (90 degrees
    * clockwise). The rotation is stored in the target. Changes the target's
    * width and height.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the rotated image
    */
   public static final Img rotate270 ( final Img source, final Img target ) {

      final int w = source.width;
      final int h = source.height;
      final int len = source.pixels.length;
      final int hn1 = h - 1;
      for ( int i = 0; i < len; ++i ) {
         target.pixels[i % w * h + hn1 - i / w] = source.pixels[i];
      }

      target.width = h;
      target.height = w;

      return target;
   }

   /**
    * Rotates the source image 90 degrees counter-clockwise. The rotation is
    * stored in the target. Changes the target's width and height.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the rotated image
    */
   public static final Img rotate90 ( final Img source, final Img target ) {

      final int w = source.width;
      final int h = source.height;
      final int len = source.pixels.length;
      final int lennh = len - h;
      for ( int i = 0; i < len; ++i ) {
         target.pixels[lennh + i / w - i % w * h] = source.pixels[i];
      }

      target.width = h;
      target.height = w;

      return target;
   }

   /**
    * Separates a source image into 3 images which emphasize the LAB
    * components. The appropriate lightness for some channels will depend on
    * whether they'll be recombined with additive blending later.
    *
    * @param source the source image
    * @param lForAb lightness for chroma channel images
    *
    * @return the separated images
    */
   @Experimental
   public static Img[] sepLab ( final Img source, final float lForAb ) {

      final long[] srcPixels = source.pixels;
      final int len = srcPixels.length;

      final long[] lPixels = new long[len];
      final long[] aPixels = new long[len];
      final long[] bPixels = new long[len];

      final long lForAbMask = ( long ) ( Utils.clamp(lForAb, 0.0f, 100.0f)
         * Lab.L_TO_SHORT + 0.5f ) << Img.L_SHIFT;

      for ( int i = 0; i < len; ++i ) {
         final long srcPixel = srcPixels[i];
         final long tIso = srcPixel & Img.ISOLATE_T_MASK;
         final long lIso = srcPixel & Img.ISOLATE_L_MASK;
         final long aIso = srcPixel & Img.ISOLATE_A_MASK;
         final long bIso = srcPixel & Img.ISOLATE_B_MASK;

         lPixels[i] = tIso | lIso;
         aPixels[i] = tIso | lForAbMask | aIso;
         bPixels[i] = tIso | lForAbMask | bIso;
      }

      final int w = source.width;
      final int h = source.height;

      /* @formatter:off */
      return new Img[] {
         new Img(w, h, lPixels),
         new Img(w, h, aPixels),
         new Img(w, h, bPixels)
      };
      /* @formatter:on */
   }

   /**
    * Separates a source image into 3 images which emphasize the LAB
    * components.
    *
    * @param source         the source image
    * @param usePreMultiply multiply color by alpha
    *
    * @return the separated images
    */
   @Experimental
   public static Img[] sepRgb ( final Img source,
      final boolean usePreMultiply ) {

      final int len = source.pixels.length;
      final long[] rPixels = new long[len];
      final long[] gPixels = new long[len];
      final long[] bPixels = new long[len];

      final Rgb srgb = new Rgb();
      final Rgb lrgb = new Rgb();
      final Vec4 xyz = new Vec4();
      final Lab lab = new Lab();

      final Rgb rIso = new Rgb();
      final Rgb gIso = new Rgb();
      final Rgb bIso = new Rgb();

      for ( int i = 0; i < len; ++i ) {
         final long tlab64 = source.pixels[i];

         Lab.fromHex(tlab64, lab);
         Rgb.srLab2TosRgb(lab, srgb, lrgb, xyz);

         if ( usePreMultiply ) { Rgb.premul(srgb, srgb); }
         Rgb.clamp01(srgb, srgb);

         // TODO: Can you set alpha to zero here and avoid the bit and below?
         rIso.set(srgb.r, 0.0f, 0.0f, 1.0f);
         gIso.set(0.0f, srgb.g, 0.0f, 1.0f);
         bIso.set(0.0f, 0.0f, srgb.b, 1.0f);

         final long rLab64 = Rgb.sRgbToSrLab2(rIso, lab, xyz, lrgb)
            .toHexLongSat() & Img.ISOLATE_LAB_MASK;
         final long gLab64 = Rgb.sRgbToSrLab2(gIso, lab, xyz, lrgb)
            .toHexLongSat() & Img.ISOLATE_LAB_MASK;
         final long bLab64 = Rgb.sRgbToSrLab2(bIso, lab, xyz, lrgb)
            .toHexLongSat() & Img.ISOLATE_LAB_MASK;

         // Not sure if this is an effective option.
         // if ( correctLight ) {
         // final long lIso = tlab64 & Image.ISOLATE_L_MASK;
         // rLab64 = lIso | rLab64 & Image.ISOLATE_AB_MASK;
         // gLab64 = lIso | gLab64 & Image.ISOLATE_AB_MASK;
         // bLab64 = lIso | bLab64 & Image.ISOLATE_AB_MASK;
         // }

         final long tIso = tlab64 & Img.ISOLATE_T_MASK;

         rPixels[i] = tIso | rLab64;
         gPixels[i] = tIso | gLab64;
         bPixels[i] = tIso | bLab64;
      }

      final int w = source.width;
      final int h = source.height;

      /* @formatter:off */
      return new Img[] {
         new Img(w, h, rPixels),
         new Img(w, h, gPixels),
         new Img(w, h, bPixels)
      };
      /* @formatter:on */
   }

   /**
    * Converts a LAB image to an array of 32-bit AARRGGBB pixels.
    *
    * @param source the source image
    *
    * @return the pixels
    */
   public static final int[] toArgb32 ( final Img source ) {

      /* Tested March 11 2025. */

      final int len = source.pixels.length;
      final int[] argb32s = new int[len];
      final HashMap < Long, Integer > convert = new HashMap <>();
      convert.put(Img.CLEAR_PIXEL, 0);

      final Rgb srgb = new Rgb();
      final Rgb lrgb = new Rgb();
      final Vec4 xyz = new Vec4();
      final Lab lab = new Lab();

      for ( int i = 0; i < len; ++i ) {
         final long tlab64 = source.pixels[i];
         final Long tlab64Obj = tlab64;
         int argb32 = 0;

         if ( convert.containsKey(tlab64Obj) ) {
            argb32 = convert.get(tlab64Obj);
         } else {
            Lab.fromHex(tlab64, lab);
            Rgb.srLab2TosRgb(lab, srgb, lrgb, xyz);
            // TODO: If you wanted to tone map the image, it'd have to be
            // done here.
            argb32 = srgb.toHexIntSat();
            convert.put(tlab64Obj, argb32);
         }

         argb32s[i] = argb32;
      }

      return argb32s;
   }

   /**
    * Gets the source image's unique pixels as an ordered set.
    *
    * @param source
    *
    * @return the dictionary
    */
   public static final long[] uniques ( final Img source ) {

      final TreeSet < Long > u = Img.toTreeSet(source, new TreeSet <>());
      final int size = u.size();
      final long[] arr = new long[size];
      int i = -1;
      for ( final Long cObj : u ) {
         ++i;
         arr[i] = cObj;
      }
      return arr;
   }

   /**
    * Inverts an image per a mask.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the inverted image
    */
   protected static final Img invert ( final Img source, final long mask,
      final Img target ) {

      if ( !Img.similar(source, target) ) { return target; }

      final int len = source.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         target.pixels[i] = source.pixels[i] ^ mask;
      }
      return target;
   }

   /**
    * Multiplies the image's alpha by the scalar. Expected range is within [0,
    * 65535]. Clears the image if the alpha is less than or equal to zero.
    *
    * @param target the output image
    * @param alpha  the alpha scalar
    *
    * @return the multiplied alpha
    */
   protected static final Img mulAlpha ( final Img target, final long alpha ) {

      if ( alpha <= 0x0000L ) { return Img.clear(target); }
      if ( alpha == 0xffffL ) { return target; }

      final int len = target.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         final long c = target.pixels[i];
         final long t16Src = c >> Img.T_SHIFT & 0xffffL;
         final long t16Trg = t16Src * alpha / 0xffffL;
         final long t16TrgCl = t16Trg > 0xffffL ? 0xffffL : t16Trg;
         target.pixels[i] = t16TrgCl << Img.T_SHIFT | c & Img.ISOLATE_LAB_MASK;
      }

      return target;
   }

   /**
    * Evaluates whether two images are similar enough that they can serve as
    * source and target in a static method. To be similar, images must have
    * the same width, height and pixel length.
    *
    * @param a the source candidate
    * @param b the target candidate
    *
    * @return the evaluation
    */
   protected static boolean similar ( final Img a, final Img b ) {

      return a == b || a.width == b.width && a.height == b.height
         && a.pixels.length == b.pixels.length;
   }

   /**
    * Gets the source image's unique pixels as a dictionary. The key is the
    * color as a 64-bit integer in LAB. The value is an array of indices at
    * which the pixel was found in the source array. These indices can be
    * converted to x,y coordinates using the source image's width.
    *
    * @param source the source image
    * @param target the tree map
    *
    * @return the dictionary
    */
   protected static final TreeMap < Long, ArrayList < Integer > > toTreeMap (
      final Img source, final TreeMap < Long, ArrayList < Integer > > target ) {

      // TODO: What would the public facing version of this method look like?
      // Maybe it would be TreeMap < Lab, integer[] >.
      // TODO: Sort according to first entry in array. If it's going to be
      // reordered anyway, use hashmap instead?

      final int len = source.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         final long c = source.pixels[i];
         final Long cObj = c;
         if ( target.containsKey(cObj) ) {
            target.get(cObj).add(i);
         } else {
            final ArrayList < Integer > coords = new ArrayList <>();
            coords.add(i);
            target.put(cObj, coords);
         }
      }

      return target;
   }

   /**
    * Gets the source image's unique pixels as an ordered set. The key is the
    * color as a 64-bit integer in LAB.
    *
    * @param source the source image
    * @param target the tree set
    *
    * @return the dictionary
    */
   protected static final TreeSet < Long > toTreeSet ( final Img source,
      final TreeSet < Long > target ) {

      final int len = source.pixels.length;
      for ( int i = 0; i < len; ++i ) { target.add(source.pixels[i]); }
      return target;
   }

   /**
    * Policy for handling gray colors when adjusting by LCH.
    */
   public enum GrayPolicy {

      /** Gray colors have a hue on the cool side of the color wheel. */
      COOL,

      /** Do not saturate gray colors. */
      OMIT,

      /** Gray colors have a hue on the warm side of the color wheel. */
      WARM,

      /** Gray colors have zero hue. */
      ZERO

   }

   /**
    * An iterator, which allows a face's edges to be accessed in an enhanced
    * for loop.
    */
   public static final class ImageIterator implements PrimitiveIterator.OfLong {

      /**
       * The image being iterated over.
       */
      private final Img image;

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The default constructor.
       *
       * @param image the image to iterate.
       */
      public ImageIterator ( final Img image ) {

         this.image = image;
      }

      /**
       * Determines whether another pixel is available in the list.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext ( ) {

         return this.index < this.image.pixels.length;
      }

      /**
       * Gets the next pixel.
       *
       * @return the pixel
       */
      @Override
      public long nextLong ( ) {

         if ( !this.hasNext() ) { throw new NoSuchElementException(); }
         return this.image.pixels[this.index++];
      }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * Policy for handling the pivot when adjusting contrast.
    */
   public enum PivotPolicy {

      /** Pivot around a fixed number. */
      FIXED,

      /** Pivot around the arithmetic mean (average). */
      MEAN,

      /** Pivot around the average of the minimum and maximum. */
      RANGE

   }

}
