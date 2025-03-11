package camzup.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

import processing.core.PImage;

/**
 * An image class for images in the LAB color format. The bytes per pixel
 * is 64, a long. The bytes per channel is 16, a short. The channel format
 * is 0xTTTTLLLLAAAABBBB. The alpha channel is abbreviated to 'T', since
 * 'A' is already taken.
 */
public class LabImage {

   /**
    * The image height.
    */
   protected final int height;

   /**
    * The image pixels.
    */
   protected final long[] pixels;

   /**
    * The image width.
    */
   protected final int width;

   /**
    * Constructs an image from width and height. The image is filled with
    * {@link LabImage#CLEAR_PIXEL}, {@value LabImage#CLEAR_PIXEL}.
    *
    * @param width  the width
    * @param height the height
    */
   public LabImage ( final int width, final int height ) {

      // TODO: The final width height choice will make image rotate, skew and
      // resize a bitch. For now implement protected static methods that only
      // work on long[] arrays of pixels.

      // TODO: Function to set all zero alpha pixels to clear pixel?

      // TODO: Separate lab method
      // TODO: Separate rgb method

      // TODO: Mark out of gamut pixels with a color. Problem is that you have
      // to assume srgb.

      // TODO: Normalize light method.

      this(width, height, LabImage.CLEAR_PIXEL);
   }

   /**
    * Constructs an image from width and height. The image is filled with the
    * provided color.
    *
    * @param width  the width
    * @param height the height
    * @param fill   the color
    */
   public LabImage ( final int width, final int height, final Lab fill ) {

      this(width, height, fill.toHexLongSat());
   }

   /**
    * Constructs an image from width and height. The image is filled with the
    * provided color. The absolute value of width and height are used. Width
    * and height are expected to be at least 1 and at most
    * {@link LabImage#MAX_DIMENSION}, {@value LabImage#MAX_DIMENSION}.
    *
    * @param width  the width
    * @param height the height
    * @param fill   the color
    */
   public LabImage ( final int width, final int height, final long fill ) {

      this.width = Utils.clamp(Math.abs(width), 1, LabImage.MAX_DIMENSION);
      this.height = Utils.clamp(Math.abs(height), 1, LabImage.MAX_DIMENSION);

      final int area = this.width * this.height;
      this.pixels = new long[area];
      for ( int i = 0; i < area; ++i ) { this.pixels[i] = fill; }
   }

   /**
    * Constructs an image from a source.
    *
    * @param source the source image.
    */
   public LabImage ( final LabImage source ) {

      this.width = source.width;
      this.height = source.height;
      this.pixels = source.getPixels();
   }

   /**
    * Constructs a new image with no verification. The pixels array is
    * assigned by reference, not copied by value.
    *
    * @param width  the width
    * @param height the height
    * @param pixels the pixels
    */
   protected LabImage ( final int width, final int height,
      final long[] pixels ) {

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
   public boolean equals ( final LabImage other ) {

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
      final LabImage other = ( LabImage ) obj;
      return this.equals(other);
   }

   /**
    * Gets the image height.
    *
    * @return the width
    */
   public final int getHeight ( ) { return this.height; }

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
    * Gets a pixel at local coordinates x and y. Clamps the coordinates to fit
    * within image bounds.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    *
    * @return the pixel
    */
   public final long getPixelClamp ( final int x, final int y ) {

      final int xcl = Utils.clamp(x, 0, this.width - 1);
      final int ycl = Utils.clamp(y, 0, this.height - 1);
      final int i = ycl * this.width + xcl;
      return this.pixels[i];
   }

   /**
    * Gets a pixel at local coordinates x and y. If the coordinates are out of
    * bounds, then returns {@link LabImage#CLEAR_PIXEL},
    * {@value LabImage#CLEAR_PIXEL}.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    *
    * @return the pixel
    */
   public final long getPixelOmit ( final int x, final int y ) {

      if ( y >= 0 && y < this.height && x >= 0 && x < this.width ) {
         return this.pixels[y * this.width + x];
      }
      return LabImage.CLEAR_PIXEL;
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
    * Gets a pixel at local coordinates x and y. Wraps the pixels around the
    * image boundaries.
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
    * Sets a pixel at local coordinates x and y. Clamps the coordinates to fit
    * within image bounds.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    * @param c the color
    */
   public final void setPixelClamp ( final int x, final int y, final long c ) {

      final int xcl = Utils.clamp(x, 0, this.width - 1);
      final int ycl = Utils.clamp(y, 0, this.height - 1);
      final int i = ycl * this.width + xcl;
      this.pixels[i] = c;
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
    * Sets a pixel at local coordinates x and y. Wraps the pixels around the
    * image boundaries.
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
    * Default color for dark checker squares, {@value LabImage#CHECKER_DARK},
    * or 1.0 / 3.0 of 65535 lightness.
    */
   public static final long CHECKER_DARK = 0xffff_5555_8000_8000L;

   /**
    * Default color for light checker squares,
    * {@value LabImage#CHECKER_LIGHT}, or 2.0 / 3.0 of 65535 lightness.
    */
   public static final long CHECKER_LIGHT = 0xffff_aaaa_8000_8000L;

   /**
    * The value of a pixel with zero light, zero a, zero b and zero alpha.
    * Because a and b are signed floating point numbers that are converted to
    * unsigned shorts, this number is not equal to zero.
    */
   public static final long CLEAR_PIXEL = 0x0000_0000_8000_8000L;

   /**
    * The default policy on gray colors when adjusting by LCH.
    */
   public static final GrayPolicy DEFAULT_GRAY_POLICY = GrayPolicy.OMIT;

   /**
    * The default policy on pivots when adjusting chroma by a factor.
    */
   public static final PivotPolicy DEFAULT_PIVOT_POLICY_CHROMA
      = PivotPolicy.MEAN;

   /**
    * Mask to isolate the a channel.
    */
   public static final long ISOLATE_A_MASK = 0x0000_0000_ffff_0000L;

   /**
    * Mask to isolate the a and b channels.
    */
   public static final long ISOLATE_AB_MASK = LabImage.ISOLATE_A_MASK
      | LabImage.ISOLATE_B_MASK;

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
   public static final long ISOLATE_LAB_MASK = LabImage.ISOLATE_L_MASK
      | LabImage.ISOLATE_A_MASK | LabImage.ISOLATE_B_MASK;

   /**
    * Mask to isolate the alpha channel.
    */
   public static final long ISOLATE_T_MASK = 0xffff_0000_0000_0000L;

   /**
    * Mask to isolate the alpha and l channels.
    */
   public static final long ISOLATE_TL_MASK = LabImage.ISOLATE_T_MASK
      | LabImage.ISOLATE_L_MASK;

   /**
    * Mask for all color channels.
    */
   public static final long ISOLATE_TLAB_MASK = LabImage.ISOLATE_T_MASK
      | LabImage.ISOLATE_L_MASK | LabImage.ISOLATE_A_MASK
      | LabImage.ISOLATE_B_MASK;

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
    * The amount to shift the alpha channel to the left or right when packing
    * and unpacking a pixel.
    */
   public static final long T_SHIFT = 0x30L;

   /**
    * Adjusts the contrast of colors from a source pixels array by a factor.
    * The adjustment factor is expected to be in [-1.0, 1.0].
    *
    * @param source the source pixels
    * @param fac    the contrast factor
    * @param target the target pixels
    *
    * @return the adjusted pixels
    */
   public static LabImage adjustContrast ( final LabImage source,
      final float fac, final LabImage target ) {

      return LabImage.adjustContrastLight(source, fac, target);
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
   public static final LabImage adjustContrastChroma ( final LabImage source,
      final float fac, final LabImage target ) {

      return LabImage.adjustContrastChroma(source, fac,
         LabImage.DEFAULT_PIVOT_POLICY_CHROMA, target);
   }

   /**
    * Adjusts the chroma contrast of colors from a source pixels array by a
    * factor. The adjustment factor is expected to be in [-1.0, 1.0]. The use
    * range option chooses the average of the minimum and maximum chroma
    * rather than the mean chroma.
    *
    * @param source the source pixels
    * @param fac    the contrast factor
    * @param policy the pivot policy
    * @param target the target pixels
    *
    * @return the adjusted pixels
    */
   public static final LabImage adjustContrastChroma ( final LabImage source,
      final float fac, final PivotPolicy policy, final LabImage target ) {

      /* Tested March 11 2025. */

      if ( !LabImage.similar(source, target) ) { return target; }

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
      convert.put(LabImage.CLEAR_PIXEL, LabImage.CLEAR_PIXEL);

      for ( int j = 0; j < len; ++j ) {
         final long srcPixel = source.pixels[j];
         final Long srcPixelObj = srcPixel;
         long trgPixel = LabImage.CLEAR_PIXEL;

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
    * Adjusts the light contrast of colors from a source pixels array by a
    * factor. The adjustment factor is expected to be in [-1.0, 1.0].
    *
    * @param source the source pixels
    * @param fac    the contrast factor
    * @param target the target pixels
    *
    * @return the adjusted pixels
    */
   public static final LabImage adjustContrastLight ( final LabImage source,
      final float fac, final LabImage target ) {

      /* Tested March 11 2025. */

      if ( !LabImage.similar(source, target) ) { return target; }

      final int len = source.pixels.length;
      final float noNanFac = Float.isNaN(fac) ? 0.5f : fac;
      final float adjVerif = 1.0f + Utils.clamp(noNanFac, -1.0f, 1.0f);

      if ( Utils.approx(adjVerif, 1.0f) ) {
         System.arraycopy(source.pixels, 0, target.pixels, 0, len);
         return target;
      }

      // TODO: Provide an int flag or enum for options: AVERAGE, RANGE, 50?
      // If an enum is created, it should replace the boolean flag in chroma.
      final float pivotLight = 50.0f;

      final Lab lab = new Lab();
      final HashMap < Long, Long > convert = new HashMap <>();
      convert.put(LabImage.CLEAR_PIXEL, LabImage.CLEAR_PIXEL);

      for ( int i = 0; i < len; ++i ) {
         final long srcPixel = source.pixels[i];
         final Long srcPixelObj = srcPixel;
         long trgPixel = LabImage.CLEAR_PIXEL;

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
   public static final LabImage adjustLab ( final LabImage source,
      final Lab adjust, final LabImage target ) {

      /* Tested March 11 2025. */

      if ( !LabImage.similar(source, target) ) { return target; }

      final int len = source.pixels.length;

      /* @formatter:off */
      if ( Float.floatToIntBits(adjust.l) == 0
         && Float.floatToIntBits(adjust.a) == 0
         && Float.floatToIntBits(adjust.b) == 0
         && Float.floatToIntBits(adjust.alpha) == 0 ) {

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
         long trgPixel = LabImage.CLEAR_PIXEL;

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
   public static final LabImage adjustLch ( final LabImage source,
      final Lch adjust, final GrayPolicy policy, final LabImage target ) {

      if ( !LabImage.similar(source, target) ) { return target; }

      final int len = source.pixels.length;

      /* @formatter:off */
      if ( Float.floatToIntBits(adjust.l) == 0
         && Float.floatToIntBits(adjust.c) == 0
         && Float.floatToIntBits(Utils.mod1(adjust.h)) == 0
         && Float.floatToIntBits(adjust.alpha) == 0 ) {

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
         long trgPixel = LabImage.CLEAR_PIXEL;

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
   public static final LabImage adjustLch ( final LabImage source,
      final Lch adjust, final LabImage target ) {

      return LabImage.adjustLch(source, adjust, LabImage.DEFAULT_GRAY_POLICY,
         target);
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
   public static byte[] byteslm ( final LabImage source ) {

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
   public static byte[] bytesml ( final LabImage source ) {

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
   public static LabImage checker ( final int sizeCheck,
      final LabImage target ) {

      return LabImage.checker(LabImage.CHECKER_DARK, LabImage.CHECKER_LIGHT,
         sizeCheck, sizeCheck, target);
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
   public static LabImage checker ( final Lab a, final Lab b, final int wCheck,
      final int hCheck, final LabImage target ) {

      return LabImage.checker(a.toHexLongSat(), b.toHexLongSat(), wCheck,
         hCheck, target);
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
   public static LabImage checker ( final Lab a, final Lab b,
      final int sizeCheck, final LabImage target ) {

      return LabImage.checker(a.toHexLongSat(), b.toHexLongSat(), sizeCheck,
         sizeCheck, target);
   }

   /**
    * Creates a checker pattern in an image. For making a background canvas
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
   public static LabImage checker ( final long a, final long b,
      final int wCheck, final int hCheck, final LabImage target ) {

      /* Tested March 11 2025. */

      final int w = target.width;
      final int h = target.height;
      final int shortEdge = w < h ? w : h;
      final int wcVerif = Utils.clamp(wCheck, 1, shortEdge / 2);
      final int hcVerif = Utils.clamp(hCheck, 1, shortEdge / 2);

      final long va = a != b && a != LabImage.CLEAR_PIXEL ? a
         : LabImage.CHECKER_DARK;
      final long vb = a != b && b != LabImage.CLEAR_PIXEL ? b
         : LabImage.CHECKER_LIGHT;

      final int len = target.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         target.pixels[i] = ( i % w / wcVerif + i / w / hcVerif & 1 ) == 0 ? va
            : vb;
      }

      return target;
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
   public static LabImage checker ( final long a, final long b,
      final int sizeCheck, final LabImage target ) {

      return LabImage.checker(a, b, sizeCheck, sizeCheck, target);
   }

   /**
    * Clears all pixels in an image to {@link LabImage#CLEAR_PIXEL},
    * {@value LabImage#CLEAR_PIXEL}.
    *
    * @param target the output image
    *
    * @return the filled image
    */
   public static final LabImage clear ( final LabImage target ) {

      return LabImage.fill(LabImage.CLEAR_PIXEL, target);
   }

   /**
    * Fills an image with a color.
    *
    * @param fill   the fill color
    * @param target the output image
    *
    * @return the filled image
    */
   public static final LabImage fill ( final Lab fill, final LabImage target ) {

      return LabImage.fill(fill.toHexLongSat(), target);
   }

   /**
    * Fills an image with a color.
    *
    * @param fill   the fill color
    * @param target the output image
    *
    * @return the filled image
    */
   public static final LabImage fill ( final long fill,
      final LabImage target ) {

      final int len = target.pixels.length;
      for ( int i = 0; i < len; ++i ) { target.pixels[i] = fill; }
      return target;
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
   public static final LabImage fromArgb32 ( final int width, final int height,
      final int[] argb32s ) {

      /* Tested March 11 2025. */

      final int wVerif = Utils.clamp(Math.abs(width), 1,
         LabImage.MAX_DIMENSION);
      final int hVerif = Utils.clamp(Math.abs(height), 1,
         LabImage.MAX_DIMENSION);

      final int area = wVerif * hVerif;
      final int len = argb32s.length;
      if ( area != len ) {
         System.err.println("Pixel length does not match image area.");
         return new LabImage(width, height);
      }

      final Rgb srgb = new Rgb();
      final Rgb lrgb = new Rgb();
      final Vec4 xyz = new Vec4();
      final Lab lab = new Lab();

      final long[] tlab64s = new long[len];
      final HashMap < Integer, Long > convert = new HashMap <>();
      convert.put(0, LabImage.CLEAR_PIXEL);

      for ( int i = 0; i < len; ++i ) {
         final int argb32 = argb32s[i];
         final Integer argb32Obj = argb32;
         long tlab64 = LabImage.CLEAR_PIXEL;

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

      return new LabImage(wVerif, hVerif, tlab64s);
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
   public static final LabImage grayscale ( final LabImage source,
      final float fac, final LabImage target ) {

      /* Tested March 11 2025. */

      if ( Float.isNaN(fac) || fac >= 1.0f ) {
         return LabImage.grayscale(source, target);
      }

      if ( !LabImage.similar(source, target) ) { return target; }

      final int len = source.pixels.length;

      if ( fac <= 0.0f ) {
         System.arraycopy(source.pixels, 0, target.pixels, 0, len);
         return target;
      }

      final float u = 1.0f - fac;
      final Lab lab = new Lab();
      final HashMap < Long, Long > convert = new HashMap <>();
      convert.put(LabImage.CLEAR_PIXEL, LabImage.CLEAR_PIXEL);

      for ( int i = 0; i < len; ++i ) {
         final long srcPixel = source.pixels[i];
         final Long srcPixelObj = srcPixel;
         long trgPixel = LabImage.CLEAR_PIXEL;

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
   public static final LabImage grayscale ( final LabImage source,
      final LabImage target ) {

      if ( !LabImage.similar(source, target) ) { return target; }

      final int len = source.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         target.pixels[i] = source.pixels[i] & LabImage.ISOLATE_TL_MASK
            | LabImage.CLEAR_PIXEL;
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
   public static LabImage invert ( final LabImage source,
      final LabImage target ) {

      return LabImage.invert(source, LabImage.ISOLATE_TLAB_MASK, target);
   }

   /**
    * Inverts the chroma in an image.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the inverted image
    */
   public static final LabImage invertAB ( final LabImage source,
      final LabImage target ) {

      return LabImage.invert(source, LabImage.ISOLATE_AB_MASK, target);
   }

   /**
    * Inverts the transparency in an image.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the inverted image
    */
   public static final LabImage invertAlpha ( final LabImage source,
      final LabImage target ) {

      return LabImage.invert(source, LabImage.ISOLATE_T_MASK, target);
   }

   /**
    * Inverts the l, a and b channels in an image.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the inverted image
    */
   public static final LabImage invertLAB ( final LabImage source,
      final LabImage target ) {

      return LabImage.invert(source, LabImage.ISOLATE_LAB_MASK, target);
   }

   /**
    * Inverts the lightness in an image.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the inverted image
    */
   public static final LabImage invertLight ( final LabImage source,
      final LabImage target ) {

      return LabImage.invert(source, LabImage.ISOLATE_L_MASK, target);
   }

   /**
    * Tests whether an image contains all clear pixels.
    *
    * @param source the source image
    *
    * @return the evaluation
    */
   public static boolean isClear ( final LabImage source ) {

      return LabImage.isClearCasual(source);
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
   public static final boolean isClearCasual ( final LabImage source ) {

      final int len = source.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         if ( ( source.pixels[i] & LabImage.ISOLATE_T_MASK ) != 0 ) {
            return false;
         }
      }
      return true;
   }

   /**
    * Tests whether an image contains all clear pixels. Pixels must be equal
    * to {@link LabImage#CLEAR_PIXEL}, {@value LabImage#CLEAR_PIXEL}, to be
    * considered clear.
    *
    * @param source the source image
    *
    * @return the evaluation
    */
   public static final boolean isClearStrict ( final LabImage source ) {

      final int len = source.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         if ( source.pixels[i] != LabImage.CLEAR_PIXEL ) { return false; }
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
   public static LabImage mix ( final LabImage orig, final LabImage dest,
      final float fac, final LabImage target ) {

      /* Tested March 11 2025. */

      if ( !LabImage.similar(orig, dest) || !LabImage.similar(dest, target) ) {
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
   public static final LabImage mulAlpha ( final LabImage target,
      final float alpha ) {

      return LabImage.mulAlpha(target, Utils.round(Utils.abs(alpha) * 0xffff));
   }

   /**
    * Sets all pixels in an image to opaque.
    *
    * @param target the output image
    *
    * @return the opaque image
    */
   public static final LabImage opaque ( final LabImage target ) {

      final int len = target.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         target.pixels[i] = target.pixels[i] | LabImage.ISOLATE_T_MASK;
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
   public static final LabImage removeTranslucency ( final LabImage target ) {

      final int len = target.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         final long c = target.pixels[i];
         final long t16Src = c >> LabImage.T_SHIFT & 0xffffL;
         final long t16Trg = t16Src < 0x80000L ? 0x0000L : 0xffffL;
         target.pixels[i] = t16Trg << LabImage.T_SHIFT | c
            & LabImage.ISOLATE_LAB_MASK;
      }
      return target;
   }

   /**
    * Converts a LAB image to an array of 32-bit AARRGGBB pixels.
    *
    * @param source the source image
    *
    * @return the pixels
    */
   public static final int[] toArgb32 ( final LabImage source ) {

      /* Tested March 11 2025. */

      final int len = source.pixels.length;
      final int[] argb32s = new int[len];
      final HashMap < Long, Integer > convert = new HashMap <>();
      convert.put(LabImage.CLEAR_PIXEL, 0);

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
   public static final long[] uniques ( final LabImage source ) {

      final TreeSet < Long > u = LabImage.toTreeSet(source, new TreeSet <>());
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
   protected static final LabImage invert ( final LabImage source,
      final long mask, final LabImage target ) {

      if ( !LabImage.similar(source, target) ) { return target; }

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
   protected static final LabImage mulAlpha ( final LabImage target,
      final long alpha ) {

      if ( alpha <= 0x0000L ) { return LabImage.clear(target); }
      if ( alpha == 0xffffL ) { return target; }

      final int len = target.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         final long c = target.pixels[i];
         final long t16Src = c >> LabImage.T_SHIFT & 0xffffL;
         final long t16Trg = t16Src * alpha / 0xffffL;
         final long t16TrgCl = t16Trg > 0xffffL ? 0xffffL : t16Trg;
         target.pixels[i] = t16TrgCl << LabImage.T_SHIFT | c
            & LabImage.ISOLATE_LAB_MASK;
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
   protected static boolean similar ( final LabImage a, final LabImage b ) {

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
      final LabImage source, final TreeMap < Long, ArrayList <
         Integer > > target ) {

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
   protected static final TreeSet < Long > toTreeSet ( final LabImage source,
      final TreeSet < Long > target ) {

      final int len = source.pixels.length;
      for ( int i = 0; i < len; ++i ) { target.add(source.pixels[i]); }
      return target;
   }

   /**
    * Policy for handling gray colors when adjusting by LCH.
    */
   public enum GrayPolicy { COOL, OMIT, WARM, ZERO }

   /**
    * Policy for handling the pivot when adjusting contrast.
    */
   public enum PivotPolicy { FIXED, MEAN, RANGE }

}
