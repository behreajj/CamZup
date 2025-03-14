package camzup.core;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

/**
 * An image class for images in the LAB color format. The bytes per pixel
 * is 64, a long. The bytes per channel is 16, a short. The channel format
 * is 0xTTTTLLLLAAAABBBB. The alpha channel is abbreviated to 'T', since
 * 'A' is already taken.
 */
public class Img {

   /**
    * The image height.
    */
   protected int height;

   /**
    * The image pixels.
    */
   protected long[] pixels;

   /**
    * The image width.
    */
   protected int width;

   /**
    * Constructs an image with a {@link Img#DEFAULT_WIDTH} and
    * {@link Img#DEFAULT_HEIGHT}.
    */
   public Img ( ) {

      // TODO: Function to set all zero alpha pixels to clear pixel? What to
      // call it? clearToClearBlack?

      // TODO: swap alpha to light, light to alpha methods?

      // TODO: Is a mask function still necessary? If so, then offer
      // to pull from either the alpha channel or the lightness. Where
      // a mask and shift would be outside the for loop, e.g.,
      // mask = useLight ? L_MASK : T_MASK .

      // TODO: Option to quantize image? Maybe in rgb or maybe the light
      // channel only?

      this(Img.DEFAULT_WIDTH, Img.DEFAULT_HEIGHT, Img.CLEAR_PIXEL);
   }

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

      return y >= 0 && y < this.height && x >= 0 && x < this.width
         ? this.pixels[y * this.width + x] : defaultPixel;
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

      return i >= 0 && i < this.pixels.length ? this.pixels[i] : defaultPixel;
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

      return this.pixels[Utils.mod(y, this.height) * this.width + Utils.mod(x,
         this.width)];
   }

   /**
    * Gets the image's size as a vector.
    *
    * @param target the output size
    *
    * @return the size
    */
   public final Vec2 getSize ( final Vec2 target ) {

      return target.set(this.width, this.height);
   }

   /**
    * Gets the image width.
    *
    * @return the width
    */
   public final int getWidth ( ) { return this.width; }

   /**
    * Gets the image's hash code.
    *
    * @return the hash code.
    */
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

      this.pixels[Utils.mod(y, this.height) * this.width + Utils.mod(x,
         this.width)] = c;
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
    * Mask to isolate the a channel.
    */
   public static final long A_MASK = 0x0000_0000_ffff_0000L;

   /**
    * The amount to shift the a channel to the left or right when packing and
    * unpacking a pixel.
    */
   public static final long A_SHIFT = 0x10L;

   /**
    * Mask to isolate the a and b channels.
    */
   public static final long AB_MASK = Img.A_MASK | Img.B_MASK;

   /**
    * Mask to isolate the b channel.
    */
   public static final long B_MASK = 0x0000_0000_0000_ffffL;

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
   public static final float DEFAULT_ALPHA = 1.0f;

   /**
    * Default blue value when creating a diagnostic RGB image.
    */
   public static final float DEFAULT_BLUE = 0.5f;

   /**
    * The default blend mode for the a and b channels.
    */
   public static final BlendMode.AB DEFAULT_BM_AB = BlendMode.AB.BLEND;

   /**
    * The default blend mode for the alpha channel.
    */
   public static final BlendMode.Alpha DEFAULT_BM_ALPHA = BlendMode.Alpha.BLEND;

   /**
    * The default blend mode for the lightness channel.
    */
   public static final BlendMode.L DEFAULT_BM_L = BlendMode.L.BLEND;

   /**
    * The default policy on gray colors when adjusting by LCH.
    */
   public static final GrayPolicy DEFAULT_GRAY_POLICY = GrayPolicy.OMIT;

   /**
    * The default height when none is given.
    */
   public static final int DEFAULT_HEIGHT = 128;

   /**
    * Default radius when querying an octree in palette mapping.
    */
   public static final float DEFAULT_OCTREE_QUERY_RADIUS = 175.0f;

   /**
    * Default maximum number for a palette extracted from an image.s
    */
   public static final int DEFAULT_PALETTE_THRESHOLD = 256;

   /**
    * The default policy on pivots when adjusting chroma by a factor.
    */
   public static final PivotPolicy DEFAULT_PIVOT_POLICY = PivotPolicy.MEAN;

   /**
    * The default width when none is given.
    */
   public static final int DEFAULT_WIDTH = 128;

   /**
    * Mask to isolate the lightness channel.
    */
   public static final long L_MASK = 0x0000_ffff_0000_0000L;

   /**
    * The amount to shift the lightness channel to the left or right when
    * packing and unpacking a pixel.
    */
   public static final long L_SHIFT = 0x20L;

   /**
    * Mask to isolate the l, a and b channels.
    */
   public static final long LAB_MASK = Img.L_MASK | Img.A_MASK | Img.B_MASK;

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
    * Mask to isolate the alpha channel.
    */
   public static final long T_MASK = 0xffff_0000_0000_0000L;

   /**
    * The amount to shift the alpha channel to the left or right when packing
    * and unpacking a pixel.
    */
   public static final long T_SHIFT = 0x30L;

   /**
    * Mask to isolate the alpha and l channels.
    */
   public static final long TL_MASK = Img.T_MASK | Img.L_MASK;

   /**
    * Mask for all color channels.
    */
   public static final long TLAB_MASK = Img.T_MASK | Img.L_MASK | Img.A_MASK
      | Img.B_MASK;

   /**
    * Adjusts an image's lightness and saturation contrast by a factor. The
    * adjustment factor is expected to be in [-1.0, 1.0].
    *
    * @param source the source image
    * @param sFac   the saturation contrast factor
    * @param lFac   the lightness contrast factor
    * @param target the target image
    *
    * @return the adjusted image
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
    * @param source the source image
    * @param sFac   the saturation contrast factor
    * @param lFac   the lightness contrast factor
    * @param policy the pivot policy
    * @param target the target image
    *
    * @return the adjusted pixels
    */
   @Experimental
   public static Img adjustContrast ( final Img source, final float sFac,
      final float lFac, final PivotPolicy policy, final Img target ) {

      if ( !Img.similar(source, target) ) {
         target.width = source.width;
         target.height = source.height;
         target.pixels = new long[source.pixels.length];
      }

      final int len = source.pixels.length;
      final float sAdjVerif = Float.isNaN(sFac) ? 1.0f : 1.0f + Utils.clamp(
         sFac, -1.0f, 1.0f);
      final float lAdjVerif = Float.isNaN(lFac) ? 1.0f : 1.0f + Utils.clamp(
         lFac, -1.0f, 1.0f);

      if ( Utils.approx(sAdjVerif, 1.0f) && Utils.approx(lAdjVerif, 1.0f) ) {
         System.arraycopy(source.pixels, 0, target.pixels, 0, len);
         return target;
      }

      final HashMap < Long, Lab > uniques = new HashMap <>();

      double minSat = Double.MAX_VALUE;
      double maxSat = -Double.MAX_VALUE;
      double sumSat = 0.0d;

      double minLight = Double.MAX_VALUE;
      double maxLight = -Double.MAX_VALUE;
      double sumLight = 0.0d;

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
            pivotSat = 0.5d;
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
    * @param source the source image
    * @param fac    the contrast factor
    * @param target the target image
    *
    * @return the adjusted image
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
    * @param source the source image
    * @param fac    the contrast factor
    * @param target the target image
    *
    * @return the adjusted image
    */
   public static Img adjustContrast ( final Img source, final Vec2 fac,
      final Img target ) {

      return Img.adjustContrast(source, fac.x, fac.y, Img.DEFAULT_PIVOT_POLICY,
         target);
   }

   /**
    * Adjusts the chroma contrast of colors from a source image by a factor.
    * The adjustment factor is expected to be in [-1.0, 1.0].
    *
    * @param source the source image
    * @param fac    the contrast factor
    * @param target the target image
    *
    * @return the adjusted image
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
    * @param source the source image
    * @param fac    the contrast factor
    * @param policy the pivot policy
    * @param target the target image
    *
    * @return the adjusted image
    */
   public static final Img adjustContrastChroma ( final Img source,
      final float fac, final PivotPolicy policy, final Img target ) {

      if ( !Img.similar(source, target) ) {
         target.width = source.width;
         target.height = source.height;
         target.pixels = new long[source.pixels.length];
      }

      final int len = source.pixels.length;
      final float adjVerif = Float.isNaN(fac) ? 1.0f : 1.0f + Utils.clamp(fac,
         -1.0f, 1.0f);

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
            pivotChroma = Lch.SR_CHROMA_MEAN;
            break;

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
    * @param source the source image
    * @param fac    the contrast factor
    * @param target the target image
    *
    * @return the adjusted image
    */
   public static final Img adjustContrastLight ( final Img source,
      final float fac, final Img target ) {

      if ( !Img.similar(source, target) ) {
         target.width = source.width;
         target.height = source.height;
         target.pixels = new long[source.pixels.length];
      }

      final int len = source.pixels.length;
      final float adjVerif = Float.isNaN(fac) ? 1.0f : 1.0f + Utils.clamp(fac,
         -1.0f, 1.0f);

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

      /*
       * It's possible to take a long adjust, and then add and subtract 0x8000L
       * for a and b. The problem, though, is the l channel, since an adjustment
       * has to support negative light and negative alpha.
       */

      if ( !Img.similar(source, target) ) {
         target.width = source.width;
         target.height = source.height;
         target.pixels = new long[source.pixels.length];
      }

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

      if ( !Img.similar(source, target) ) {
         target.width = source.width;
         target.height = source.height;
         target.pixels = new long[source.pixels.length];
      }

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
   public static final float aspect ( final Img image ) {

      return Utils.div(( float ) image.width, ( float ) image.height);
   }

   /**
    * Blends an under and over image.
    *
    * @param imgUnder the under image
    * @param imgOver  the over image
    *
    * @return the blended image
    */
   public static final Img blend ( final Img imgUnder, final Img imgOver ) {

      final int aw = imgUnder.width;
      final int ah = imgUnder.height;
      final int bw = imgOver.width;
      final int bh = imgOver.height;

      final int wLrg = aw > bw ? aw : bw;
      final int hLrg = ah > bh ? ah : bh;

      /* The 0.5 is to bias the rounding. */
      final float cx = 0.5f + wLrg * 0.5f;
      final float cy = 0.5f + hLrg * 0.5f;

      final int ax = aw == wLrg ? 0 : ( int ) ( cx - aw * 0.5f );
      final int ay = ah == hLrg ? 0 : ( int ) ( cy - ah * 0.5f );
      final int bx = bw == wLrg ? 0 : ( int ) ( cx - bw * 0.5f );
      final int by = bh == hLrg ? 0 : ( int ) ( cy - bh * 0.5f );

      return Img.blend(imgUnder, ax, ay, imgOver, bx, by, Img.DEFAULT_BM_ALPHA,
         Img.DEFAULT_BM_L, Img.DEFAULT_BM_AB, null);
   }

   /**
    * Blends an under and over image.
    *
    * @param imgUnder the under image
    * @param xUnder   the under x offset
    * @param yUnder   the under y offset
    * @param imgOver  the over image
    * @param xOver    the over x offset
    * @param yOver    the over y offset
    *
    * @return the blended image
    */
   public static final Img blend ( final Img imgUnder, final int xUnder,
      final int yUnder, final Img imgOver, final int xOver, final int yOver ) {

      return Img.blend(imgUnder, xUnder, yUnder, imgOver, xOver, yOver,
         Img.DEFAULT_BM_ALPHA, Img.DEFAULT_BM_L, Img.DEFAULT_BM_AB, null);
   }

   /**
    * Blends an under and over image.
    *
    * @param imgUnder the under image
    * @param xUnder   the under x offset
    * @param yUnder   the under y offset
    * @param imgOver  the over image
    * @param xOver    the over x offset
    * @param yOver    the over y offset
    * @param bmAlpha  the alpha blend mode
    *
    * @return the blended image
    */
   public static final Img blend ( final Img imgUnder, final int xUnder,
      final int yUnder, final Img imgOver, final int xOver, final int yOver,
      final BlendMode.Alpha bmAlpha ) {

      return Img.blend(imgUnder, xUnder, yUnder, imgOver, xOver, yOver, bmAlpha,
         Img.DEFAULT_BM_L, Img.DEFAULT_BM_AB, null);
   }

   /**
    * Blends an under and over image.
    *
    * @param imgUnder the under image
    * @param xUnder   the under x offset
    * @param yUnder   the under y offset
    * @param imgOver  the over image
    * @param xOver    the over x offset
    * @param yOver    the over y offset
    * @param bmAlpha  the alpha blend mode
    * @param bmLight  the light blend mode
    *
    * @return the blended image
    */
   public static final Img blend ( final Img imgUnder, final int xUnder,
      final int yUnder, final Img imgOver, final int xOver, final int yOver,
      final BlendMode.Alpha bmAlpha, final BlendMode.L bmLight ) {

      return Img.blend(imgUnder, xUnder, yUnder, imgOver, xOver, yOver, bmAlpha,
         bmLight, Img.DEFAULT_BM_AB, null);
   }

   /**
    * Blends an under and over image.
    *
    * @param imgUnder the under image
    * @param xUnder   the under x offset
    * @param yUnder   the under y offset
    * @param imgOver  the over image
    * @param xOver    the over x offset
    * @param yOver    the over y offset
    * @param bmAlpha  the alpha blend mode
    * @param bmLight  the light blend mode
    * @param bmAb     the ab blend mode
    *
    * @return the blended image
    */
   public static final Img blend ( final Img imgUnder, final int xUnder,
      final int yUnder, final Img imgOver, final int xOver, final int yOver,
      final BlendMode.Alpha bmAlpha, final BlendMode.L bmLight,
      final BlendMode.AB bmAb ) {

      return Img.blend(imgUnder, xUnder, yUnder, imgOver, xOver, yOver, bmAlpha,
         bmLight, bmAb, null);
   }

   /**
    * Blends an under and over image.
    *
    * @param imgUnder the under image
    * @param xUnder   the under x offset
    * @param yUnder   the under y offset
    * @param imgOver  the over image
    * @param xOver    the over x offset
    * @param yOver    the over y offset
    * @param bmAlpha  the alpha blend mode
    * @param bmLight  the light blend mode
    * @param bmAb     the ab blend mode
    * @param tl       the composite offset
    *
    * @return the blended image
    */
   public static final Img blend ( final Img imgUnder, final int xUnder,
      final int yUnder, final Img imgOver, final int xOver, final int yOver,
      final BlendMode.Alpha bmAlpha, final BlendMode.L bmLight,
      final BlendMode.AB bmAb, final Vec2 tl ) {

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
       * Based on alpha blend, find the union or intersection.
       */
      int dx = ax < bx ? ax : bx;
      int dy = ay < by ? ay : by;
      int dbrx = abrx > bbrx ? abrx : bbrx;
      int dbry = abry > bbry ? abry : bbry;

      switch ( bmAlpha ) {
         case MIN:
         case MULTIPLY: {
            dx = ax > bx ? ax : bx;
            dy = ay > by ? ay : by;
            dbrx = abrx < bbrx ? abrx : bbrx;
            dbry = abry < bbry ? abry : bbry;
         }
            break;

         /* @formatter:off */
         case OVER: {
            dx = bx; dy = by; dbrx = bbrx; dbry = bbry;
         }
            break;

         case UNDER: {
            dx = ax; dy = ay; dbrx = abrx; dbry = abry;
         }
            break;
         /* @formatter:on */

         case BLEND:
         case MAX:
         default:
      }

      final HashMap < Long, Lab > dict = new HashMap <>();
      final Lab clearLab = Lab.clearBlack(new Lab());
      dict.put(Img.CLEAR_PIXEL, clearLab);

      final int dw = 1 + dbrx - dx;
      final int dh = 1 + dbry - dy;
      if ( dw > 0 && dh > 0 ) {
         final int axid = ax - dx;
         final int ayid = ay - dy;
         final int bxid = bx - dx;
         final int byid = by - dy;

         final int dLen = dw * dh;
         for ( int h = 0; h < dLen; ++h ) {
            final int x = h % dw;
            final int y = h / dw;

            final int axs = x - axid;
            final int ays = y - ayid;
            if ( ays >= 0 && ays < ah && axs >= 0 && axs < aw ) {
               final long hexUnder = pxUnder[axs + ays * aw];
               dict.put(hexUnder, Lab.fromHex(hexUnder, new Lab()));
            }

            final int bxs = x - bxid;
            final int bys = y - byid;
            if ( bys >= 0 && bys < bh && bxs >= 0 && bxs < bw ) {
               final long hexOver = pxOver[bxs + bys * bw];
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

         /* @formatter:off */
         switch ( bmAlpha ) {
            case MAX: { tuv = t > v ? t : v; } break;
            case MIN: { tuv = t < v ? t : v; } break;
            case MULTIPLY: { tuv = t * v; } break;
            case OVER: { tuv = t; } break;
            case UNDER: { tuv = v; } break;
            case BLEND: default:
         }
         /* @formatter:on */

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
                        final double s = Math.sqrt(aOver * aOver + bOver
                           * bOver) / Math.sqrt(csqUnder);
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

                        final double s = Math.sqrt(aUnder * aUnder + bUnder
                           * bUnder) / Math.sqrt(csqOver);
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

      if ( tl != null ) { tl.set(dx, dy); }
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
   public static final Img checker ( final long a, final long b,
      final int sizeCheck, final Img target ) {

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
   public static final Img checker ( final long a, final long b,
      final int wCheck, final int hCheck, final Img target ) {

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
    * Flips the pixels source image vertically, on the y axis, and stores the
    * result in the target image.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the flipped image
    */
   public static final Img flipX ( final Img source, final Img target ) {

      final int w = source.width;
      final int h = source.height;
      final int len = source.pixels.length;

      if ( !Img.similar(source, target) ) {
         target.width = w;
         target.height = h;
         target.pixels = new long[len];
      }

      final int wn1 = w - 1;
      for ( int i = 0; i < len; ++i ) {
         target.pixels[i / w * w + wn1 - i % w] = source.pixels[i];
      }

      return target;
   }

   /**
    * Flips the pixels source image vertically, on the y axis, and stores the
    * result in the target image.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the flipped image
    */
   public static final Img flipY ( final Img source, final Img target ) {

      final int w = source.width;
      final int h = source.height;
      final int len = source.pixels.length;

      if ( !Img.similar(source, target) ) {
         target.width = w;
         target.height = h;
         target.pixels = new long[len];
      }

      final int hn1 = h - 1;
      for ( int i = 0; i < len; ++i ) {
         target.pixels[ ( hn1 - i / w ) * w + i % w] = source.pixels[i];
      }

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
    * @param target  the output image
    *
    * @return the image
    */
   public static final Img fromArgb32 ( final int width, final int height,
      final int[] argb32s, final Img target ) {

      final int wVerif = Utils.clamp(Math.abs(width), 1, Img.MAX_DIMENSION);
      final int hVerif = Utils.clamp(Math.abs(height), 1, Img.MAX_DIMENSION);

      final int area = wVerif * hVerif;
      final int len = argb32s.length;
      if ( area != len ) {
         System.err.println("Pixel length does not match image area.");
         return target;
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

      target.width = wVerif;
      target.height = hVerif;
      target.pixels = tlab64s;

      return target;
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
    * @param target the output image
    *
    * @return the gradient image
    */
   public static final Img gradientLinear ( final Gradient grd,
      final float xOrig, final float yOrig, final float xDest,
      final float yDest, final Img target ) {

      return Img.gradientLinear(grd, xOrig, yOrig, xDest, yDest,
         new Lab.MixLab(), target);
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
   public static final Img gradientLinear ( final Gradient grd,
      final float xOrig, final float yOrig, final float xDest,
      final float yDest, final Lab.AbstrEasing easing, final Img target ) {

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
    * Generates a linear gradient from an origin point to a destination point.
    * The scalar projection is clamped to [0.0, 1.0].
    *
    * @param grd    the gradient
    * @param target the output image
    *
    * @return the gradient image
    */
   public static final Img gradientLinear ( final Gradient grd,
      final Img target ) {

      return Img.gradientLinear(grd, -1.0f, 0.0f, 1.0f, 0.0f, target);
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The scalar projection is clamped to [0.0, 1.0].
    *
    * @param grd    the gradient
    * @param easing the easing function
    * @param target the output image
    *
    * @return the gradient image
    */
   public static final Img gradientLinear ( final Gradient grd,
      final Lab.AbstrEasing easing, final Img target ) {

      return Img.gradientLinear(grd, -1.0f, 0.0f, 1.0f, 0.0f, easing, target);
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The origin and destination should be in the range [-1.0, 1.0]. The
    * scalar projection is clamped to [0.0, 1.0].
    *
    * @param grd    the gradient
    * @param orig   the origin
    * @param dest   the destination
    * @param target the output image
    *
    * @return the gradient image
    */
   public static final Img gradientLinear ( final Gradient grd, final Vec2 orig,
      final Vec2 dest, final Img target ) {

      return Img.gradientLinear(grd, orig, dest, new Lab.MixLab(), target);
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The origin and destination should be in the range [-1.0, 1.0]. The
    * scalar projection is clamped to [0.0, 1.0].
    *
    * @param grd    the gradient
    * @param orig   the origin
    * @param dest   the destination
    * @param easing the easing function
    * @param target the output image
    *
    * @return the gradient image
    */
   public static final Img gradientLinear ( final Gradient grd, final Vec2 orig,
      final Vec2 dest, final Lab.AbstrEasing easing, final Img target ) {

      return Img.gradientLinear(grd, orig.x, orig.y, dest.x, dest.y, easing,
         target);
   }

   /**
    * Maps the colors of a source image to a color gradient. Retains the
    * original color's transparency.
    *
    * @param grd    the gradient
    * @param source the input image
    * @param target the output image
    *
    * @return the mapped image
    */
   public static final Img gradientMap ( final Gradient grd, final Img source,
      final Img target ) {

      return Img.gradientMap(grd, source, new Lab.MixLab(), MapChannel.L, true,
         target);
   }

   /**
    * Maps the colors of a source image to a color gradient. Retains the
    * original color's transparency.
    *
    * @param grd    the gradient
    * @param source the input image
    * @param easing the easing function
    * @param target the output image
    *
    * @return the mapped image
    */
   public static final Img gradientMap ( final Gradient grd, final Img source,
      final Lab.AbstrEasing easing, final Img target ) {

      return Img.gradientMap(grd, source, easing, MapChannel.L, true, target);
   }

   /**
    * Maps the colors of a source image to a color gradient. Retains the
    * original color's transparency.
    *
    * @param grd          the gradient
    * @param source       the input image
    * @param easing       the easing function
    * @param channel      the color channel
    * @param useNormalize normalize channel range
    * @param target       the output image
    *
    * @return the mapped image
    */
   public static final Img gradientMap ( final Gradient grd, final Img source,
      final Lab.AbstrEasing easing, final Img.MapChannel channel,
      final boolean useNormalize, final Img target ) {

      if ( !Img.similar(source, target) ) {
         target.width = source.width;
         target.height = source.height;
         target.pixels = new long[source.pixels.length];
      }

      final HashMap < Long, Long > convert = new HashMap <>();
      convert.put(Img.CLEAR_PIXEL, Img.CLEAR_PIXEL);

      final Lab lab = new Lab();
      final int len = source.pixels.length;
      final HashMap < Long, Lch > uniques = new HashMap <>();

      float minChannel = Float.MAX_VALUE;
      float maxChannel = -Float.MAX_VALUE;
      int sumTally = 0;

      for ( int i = 0; i < len; ++i ) {
         final long srcPixel = source.pixels[i];
         final Long srcPixelObj = srcPixel;
         if ( !uniques.containsKey(srcPixelObj) ) {
            Lab.fromHex(srcPixel, lab);
            final Lch lch = Lch.fromLab(lab, new Lch());
            if ( lch.alpha > 0.0f ) {
               switch ( channel ) {
                  case C: {
                     final float c = lch.c;
                     if ( c > maxChannel ) maxChannel = c;
                     if ( c < minChannel ) minChannel = c;
                  }
                     break;

                  case L:
                  default: {
                     final float l = lch.l;
                     if ( l > maxChannel ) maxChannel = l;
                     if ( l < minChannel ) minChannel = l;
                  }
               } // Channel switch.
               ++sumTally;
            } // Alpha greater than zero.
            uniques.put(srcPixelObj, lch);
         } // Doesn't contain pixel.
      } // Pixels loop.

      final boolean useNormVerif = useNormalize && sumTally != 0 && maxChannel
         > minChannel;
      final float dff = Utils.diff(maxChannel, minChannel);
      final float denom = Utils.div(1.0f, dff);
      final Lch defLch = Lch.clearBlack(new Lch());

      for ( int j = 0; j < len; ++j ) {
         final long srcPixel = source.pixels[j];
         final Long srcPixelObj = srcPixel;
         long trgPixel = Img.CLEAR_PIXEL;
         if ( convert.containsKey(srcPixelObj) ) {
            trgPixel = convert.get(srcPixelObj);
         } else {
            final Lch lch = uniques.getOrDefault(srcPixelObj, defLch);

            float fac = 0.5f;
            switch ( channel ) {
               case C: {}
                  fac = useNormVerif ? ( lch.c - minChannel ) * denom : lch.c
                     / Lch.SR_CHROMA_MAX;
                  break;

               case L:
               default: {
                  fac = useNormVerif ? ( lch.l - minChannel ) * denom : lch.l
                     * 0.01f;
               }
            }

            Gradient.eval(grd, fac, easing, lab);
            trgPixel = lab.toHexLongSat();
            convert.put(srcPixelObj, trgPixel);
         }

         target.pixels[j] = trgPixel;
      }

      return target;
   }

   /**
    * Maps the colors of a source image to a color gradient. Retains the
    * original color's transparency.
    *
    * @param grd     the gradient
    * @param source  the input image
    * @param easing  the easing function
    * @param channel the color channel
    * @param target  the output image
    *
    * @return the mapped image
    */
   public static final Img gradientMap ( final Gradient grd, final Img source,
      final Lab.AbstrEasing easing, final Img.MapChannel channel,
      final Img target ) {

      return Img.gradientMap(grd, source, easing, channel, false, target);
   }

   /**
    * Generates a radial gradient from an origin point. The origin should be
    * in the range [-1.0, 1.0].
    *
    * @param grd    the gradient
    * @param xOrig  the origin x coordinate
    * @param yOrig  the origin y coordinate
    * @param radius the radius
    * @param target the output image
    *
    * @return the gradient image
    *
    * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
    * @see Utils#max(float, float)
    */
   public static final Img gradientRadial ( final Gradient grd,
      final float xOrig, final float yOrig, final float radius,
      final Img target ) {

      return Img.gradientRadial(grd, xOrig, yOrig, radius, new Lab.MixLab(),
         target);
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
   public static final Img gradientRadial ( final Gradient grd,
      final float xOrig, final float yOrig, final float radius,
      final Lab.AbstrEasing easing, final Img target ) {

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
    * Generates a radial gradient.
    *
    * @param grd    the gradient
    * @param target the output image
    *
    * @return the gradient image
    *
    * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
    * @see Utils#max(float, float)
    */
   public static final Img gradientRadial ( final Gradient grd,
      final Img target ) {

      return Img.gradientRadial(grd, 0.0f, 0.0f, 0.5f, new Lab.MixLab(),
         target);
   }

   /**
    * Generates a radial gradient from an origin point. The origin should be
    * in the range [-1.0, 1.0].
    *
    * @param grd    the gradient
    * @param orig   the origin
    * @param radius the radius
    * @param target the output image
    *
    * @return the gradient image
    *
    * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
    * @see Utils#max(float, float)
    */
   public static final Img gradientRadial ( final Gradient grd, final Vec2 orig,
      final float radius, final Img target ) {

      return Img.gradientRadial(grd, orig.x, orig.y, radius, new Lab.MixLab(),
         target);
   }

   /**
    * Generates a conic gradient, where the factor rotates on the z axis
    * around an origin point.
    *
    * @param grd     the gradient
    * @param xOrig   the origin x coordinate
    * @param yOrig   the origin y coordinate
    * @param radians the angle in radians
    * @param target  the target image
    *
    * @return the gradient image
    *
    * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
    * @see Utils#mod1(float)
    */
   public static final Img gradientSweep ( final Gradient grd,
      final float xOrig, final float yOrig, final float radians,
      final Img target ) {

      return Img.gradientSweep(grd, xOrig, yOrig, radians, new Lab.MixLab(),
         target);
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
   public static final Img gradientSweep ( final Gradient grd,
      final float xOrig, final float yOrig, final float radians,
      final Lab.AbstrEasing easing, final Img target ) {

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
    * Generates a conic gradient, where the factor rotates on the z axis
    * around an origin point.
    *
    * @param grd    the gradient
    * @param xOrig  the origin x coordinate
    * @param yOrig  the origin y coordinate
    * @param target the target image
    *
    * @return the gradient image
    *
    * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
    * @see Utils#mod1(float)
    */
   public static final Img gradientSweep ( final Gradient grd,
      final float xOrig, final float yOrig, final Img target ) {

      return Img.gradientSweep(grd, xOrig, yOrig, IUtils.HALF_PI,
         new Lab.MixLab(), target);
   }

   /**
    * Generates a conic gradient, where the factor rotates on the z axis
    * around an origin point.
    *
    * @param grd    the gradient
    * @param orig   the origin
    * @param angle  the angle
    * @param target the target image
    *
    * @return the gradient image
    *
    * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
    * @see Utils#mod1(float)
    */
   public static final Img gradientSweep ( final Gradient grd, final Vec2 orig,
      final float angle, final Img target ) {

      return Img.gradientSweep(grd, orig.x, orig.y, angle, new Lab.MixLab(),
         target);
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

      if ( Float.isNaN(fac) || fac >= 1.0f ) {
         return Img.grayscale(source, target);
      }

      if ( !Img.similar(source, target) ) {
         target.width = source.width;
         target.height = source.height;
         target.pixels = new long[source.pixels.length];
      }

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

      if ( !Img.similar(source, target) ) {
         target.width = source.width;
         target.height = source.height;
         target.pixels = new long[source.pixels.length];
      }

      final int len = source.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         target.pixels[i] = source.pixels[i] & Img.TL_MASK | Img.CLEAR_PIXEL;
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
   public static final Img invert ( final Img source, final Img target ) {

      return Img.invert(source, Img.TLAB_MASK, target);
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

      return Img.invert(source, Img.AB_MASK, target);
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

      return Img.invert(source, Img.T_MASK, target);
   }

   /**
    * Inverts the l, a and b channels in an image.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the inverted image
    */
   public static final Img invertLab ( final Img source, final Img target ) {

      return Img.invert(source, Img.LAB_MASK, target);
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

      return Img.invert(source, Img.L_MASK, target);
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
         if ( ( source.pixels[i] & Img.T_MASK ) != 0 ) { return false; }
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
    * Mirrors, or reflects, pixels from a source image across the axis
    * described by an origin and destination. Coordinates are expected to be
    * in the range [-1.0, 1.0]. Out-of-bounds pixels are omitted from the
    * mirror.
    *
    * @param source the source image
    * @param xOrig  the origin x
    * @param yOrig  the origin y
    * @param xDest  the destination x
    * @param yDest  the destination y
    * @param flip   the flip reflection flag
    * @param target the target image
    *
    * @return the mirrored image
    */
   public static final Img mirror ( final Img source, final float xOrig,
      final float yOrig, final float xDest, final float yDest,
      final boolean flip, final Img target ) {

      final int wSrc = source.width;
      final int hSrc = source.height;
      final int srcLen = source.pixels.length;

      if ( !Img.similar(source, target) ) {
         target.width = wSrc;
         target.height = hSrc;
         target.pixels = new long[srcLen];
      }

      final float wfn1 = wSrc - 1.0f;
      final float hfn1 = hSrc - 1.0f;
      final float wfp1Half = ( wSrc + 1.0f ) * 0.5f;
      final float hfp1Half = ( hSrc + 1.0f ) * 0.5f;

      final float ax = ( xOrig + 1.0f ) * wfp1Half - 0.5f;
      final float bx = ( xDest + 1.0f ) * wfp1Half - 0.5f;
      final float ay = ( yOrig - 1.0f ) * -hfp1Half - 0.5f;
      final float by = ( yDest - 1.0f ) * -hfp1Half - 0.5f;

      final float dx = bx - ax;
      final float dy = by - ay;
      final boolean dxZero = Utils.approx(dx, 0.0f, 0.5f);
      final boolean dyZero = Utils.approx(dy, 0.0f, 0.5f);

      final int trgLen = target.pixels.length;
      if ( dxZero && dyZero ) {
         System.arraycopy(source.pixels, 0, target, 0, srcLen < trgLen ? srcLen
            : trgLen);
         return target;
      }

      if ( dxZero ) {
         return Img.mirrorX(source, Utils.round(bx), flip ? ay > by : by > ay,
            target);
      }

      if ( dyZero ) {
         return Img.mirrorY(source, Utils.round(by), flip ? bx > ax : ax > bx,
            target);
      }

      final float dMagSqInv = 1.0f / ( dx * dx + dy * dy );
      final float flipSign = flip ? -1.0f : 1.0f;

      for ( int k = 0; k < trgLen; ++k ) {
         final float cy = k / wSrc;
         final float ey = cy - ay;
         final float cx = k % wSrc;
         final float ex = cx - ax;

         final float cross = ex * dy - ey * dx;
         if ( flipSign * cross < 0.0f ) {
            target.pixels[k] = source.pixels[k];
         } else {
            final float t = ( ex * dx + ey * dy ) * dMagSqInv;
            final float u = 1.0f - t;

            final float pyProj = u * ay + t * by;
            final float pyOpp = pyProj + pyProj - cy;

            final float pxProj = u * ax + t * bx;
            final float pxOpp = pxProj + pxProj - cx;

            /*
             * Default to omitting pixels that are out-of-bounds, rather than
             * wrapping with floor modulo or clamping.
             */
            if ( pyOpp >= 0.0f && pyOpp <= hfn1 && pxOpp >= 0.0f && pxOpp
               <= wfn1 ) {
               target.pixels[k] = Img.sampleBilinear(source, pxOpp, pyOpp);
            } else {
               target.pixels[k] = Img.CLEAR_PIXEL;
            }
         }
      }

      return target;
   }

   /**
    * Mirrors, or reflects, pixels from a source image across the axis
    * described by an origin and destination. Coordinates are expected to be
    * in the range [-1.0, 1.0].
    *
    * @param source the source image
    * @param xOrig  the origin x
    * @param yOrig  the origin y
    * @param xDest  the destination x
    * @param yDest  the destination y
    * @param target the target image
    *
    * @return the mirrored image
    */
   public static final Img mirror ( final Img source, final float xOrig,
      final float yOrig, final float xDest, final float yDest,
      final Img target ) {

      return Img.mirror(source, xOrig, yOrig, xDest, yDest, false, target);
   }

   /**
    * Mirrors, or reflects, pixels from a source image across the axis
    * described by an origin and destination. Coordinates are expected to be
    * in the range [-1.0, 1.0].
    *
    * @param source the source image
    * @param orig   the origin
    * @param dest   the destination
    * @param target the target image
    *
    * @return the mirrored image
    */
   public static final Img mirror ( final Img source, final Vec2 orig,
      final Vec2 dest, final Img target ) {

      return Img.mirror(source, orig.x, orig.y, dest.x, dest.y, false, target);
   }

   /**
    * Mirrors, or reflects, pixels from a source image horizontally across a
    * pivot. The pivot is expected to be in [-1, width + 1].
    *
    * @param source the input image
    * @param pivot  the x pivot
    * @param flip   the flip reflection flag
    * @param target the output image
    *
    * @return the mirrored image
    */
   public static final Img mirrorX ( final Img source, final int pivot,
      final boolean flip, final Img target ) {

      final int w = source.width;
      final int h = source.height;
      final int srcLen = source.pixels.length;

      if ( !Img.similar(source, target) ) {
         target.width = w;
         target.height = h;
         target.pixels = new long[srcLen];
      }

      final int flipSign = flip ? 1 : -1;
      for ( int k = 0; k < srcLen; ++k ) {
         final int cross = k % w - pivot;
         if ( flipSign * cross < 0 ) {
            target.pixels[k] = source.pixels[k];
         } else {
            final int pxOpp = pivot - cross;
            if ( pxOpp >= 0 && pxOpp < w ) {
               target.pixels[k] = source.pixels[k / w * w + pxOpp];
            } else {
               target.pixels[k] = Img.CLEAR_PIXEL;
            }
         }
      }

      return target;
   }

   /**
    * Mirrors, or reflects, pixels from a source image vertically across a
    * pivot. The pivot is expected to be in [-1, height + 1]. Positive y
    * points down to the bottom of the image.
    *
    * @param source the input image
    * @param pivot  the y pivot
    * @param flip   the flip reflection flag
    * @param target the output image
    *
    * @return the mirrored image
    */
   public static final Img mirrorY ( final Img source, final int pivot,
      final boolean flip, final Img target ) {

      final int w = source.width;
      final int h = source.height;
      final int srcLen = source.pixels.length;

      if ( !Img.similar(source, target) ) {
         target.width = w;
         target.height = h;
         target.pixels = new long[srcLen];
      }

      final int flipSign = flip ? 1 : -1;
      for ( int k = 0; k < srcLen; ++k ) {
         final int cross = k / w - pivot;
         if ( flipSign * cross < 0 ) {
            target.pixels[k] = source.pixels[k];
         } else {
            final int pyOpp = pivot - cross;
            if ( pyOpp > -1 && pyOpp < h ) {
               target.pixels[k] = source.pixels[pyOpp * w + k % w];
            } else {
               target.pixels[k] = Img.CLEAR_PIXEL;
            }
         }
      }

      return target;
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
   public static final Img mix ( final Img orig, final Img dest,
      final float fac, final Img target ) {

      if ( !Img.similar(orig, dest) ) {
         System.err.println("Cannot mix between two images of unequal sizes.");
         return target;
      }

      if ( !Img.similar(orig, target) ) {
         target.width = orig.width;
         target.height = orig.height;
         target.pixels = new long[orig.pixels.length];
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
         // TODO: You might be able to get away with mixing with just longs.
         // See the sample method for reference.
         Lab.fromHex(dest.pixels[i], dLab);
         Lab.fromHex(orig.pixels[i], oLab);
         Lab.mix(oLab, dLab, t, tLab);
         target.pixels[i] = tLab.toHexLongSat();
      }

      return target;
   }

   /**
    * Multiplies the image's alpha by the scalar. Expected range is within [0,
    * 65535]. Clears the image if the alpha is less than or equal to zero.
    *
    * @param source the input image
    * @param a01    the alpha scalar
    * @param target the output image
    *
    * @return the multiplied alpha
    */
   public static final Img mulAlpha ( final Img source, final float a01,
      final Img target ) {

      if ( !Img.similar(source, target) ) {
         target.width = source.width;
         target.height = source.height;
         target.pixels = new long[source.pixels.length];
      }

      if ( a01 <= 0.0f ) { return Img.clear(target); }

      final int len = source.pixels.length;
      if ( a01 == 1.0f ) {
         System.arraycopy(source.pixels, 0, target.pixels, 0, len);
         return target;
      }

      final long alpha = Utils.round(Utils.abs(a01) * 0xffff);
      for ( int i = 0; i < len; ++i ) {
         final long c = source.pixels[i];
         final long t16Src = c >> Img.T_SHIFT & 0xffffL;
         final long t16Trg = t16Src * alpha / 0xffffL;
         final long t16TrgCl = t16Trg > 0xffffL ? 0xffffL : t16Trg;
         target.pixels[i] = t16TrgCl << Img.T_SHIFT | c & Img.LAB_MASK;
      }

      return target;
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

      if ( !Img.similar(source, target) ) {
         target.width = source.width;
         target.height = source.height;
         target.pixels = new long[source.pixels.length];
      }

      final int len = source.pixels.length;
      final float facVerif = Float.isNaN(fac) ? 1.0f : Utils.clamp(fac, -1.0f,
         1.0f);

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

      final float dff = Utils.diff(maxLight, minLight);
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

      return Img.normalizeLight(source, 1.0f, target);
   }

   /**
    * Sets all pixels in an image to opaque.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the opaque image
    */
   public static final Img opaque ( final Img source, final Img target ) {

      final int len = source.pixels.length;
      if ( !Img.similar(source, target) ) {
         target.width = source.width;
         target.height = source.height;
         target.pixels = new long[len];
      }

      for ( int i = 0; i < len; ++i ) {
         target.pixels[i] = source.pixels[i] | Img.T_MASK;
      }
      return target;
   }

   /**
    * Extracts a palette from an image. If there are more colors than the
    * threshold, engages an octree to reduce the number of colors.
    *
    * @param source the input image
    *
    * @return the palette
    */
   public static Lab[] paletteExtract ( final Img source ) {

      return Img.paletteExtract(source, Img.DEFAULT_PALETTE_THRESHOLD,
         Octree.DEFAULT_CAPACITY, false);
   }

   /**
    * Extracts a palette from an image. If there are more colors than the
    * threshold, engages an octree to reduce the number of colors.
    *
    * @param source    the input image
    * @param threshold the threshold
    *
    * @return the palette
    */
   public static Lab[] paletteExtract ( final Img source,
      final int threshold ) {

      return Img.paletteExtract(source, threshold, Octree.DEFAULT_CAPACITY,
         false);
   }

   /**
    * Extracts a palette from an image. If there are more colors than the
    * threshold, engages an octree to reduce the number of colors.
    *
    * @param source    the input image
    * @param threshold the threshold
    * @param capacity  the octree capacity
    *
    * @return the palette
    */
   public static Lab[] paletteExtract ( final Img source, final int threshold,
      final int capacity ) {

      return Img.paletteExtract(source, threshold, capacity, false);
   }

   /**
    * Extracts a palette from an image. If there are more colors than the
    * threshold, engages an octree to reduce the number of colors. Alpha is no
    * longer supported once the octree is engaged.
    *
    * @param source    the input image
    * @param threshold the threshold
    * @param capacity  the octree capacity
    * @param inclAlpha whether to include alpha
    *
    * @return the palette
    */
   public static Lab[] paletteExtract ( final Img source, final int threshold,
      final int capacity, final boolean inclAlpha ) {

      final long mask = inclAlpha ? 0 : Img.T_MASK;
      final long[] srcPixels = source.pixels;
      final int srcLen = srcPixels.length;
      final TreeMap < Long, Integer > uniqueOpaques = new TreeMap <>();

      /* Account for alpha at index 0, so less than threshold. */
      int tally = 0;
      for ( int i = 0; i < srcLen; ++i ) {
         final long tlab64 = srcPixels[i];
         if ( ( tlab64 & Img.T_MASK ) != 0 ) {
            final Long masked = mask | tlab64;
            if ( !uniqueOpaques.containsKey(masked) ) {
               ++tally;
               uniqueOpaques.put(masked, tally);
            }
         }
      }

      final int uniquesLen = uniqueOpaques.size();
      final Iterator < Entry < Long, Integer > > uniquesItr = uniqueOpaques
         .entrySet().iterator();

      final int valThresh = threshold < 3 ? 3 : threshold;
      if ( uniquesLen < valThresh ) {
         final Lab[] arr = new Lab[1 + uniquesLen];
         while ( uniquesItr.hasNext() ) {
            final Entry < Long, Integer > entry = uniquesItr.next();
            arr[entry.getValue()] = Lab.fromHex(entry.getKey(), new Lab());
         }
         arr[0] = Lab.clearBlack(new Lab());
         return arr;
      }

      final Octree oct = new Octree(Bounds3.lab(new Bounds3()), capacity);
      final Lab lab = new Lab();

      /* Place colors in octree. */
      while ( uniquesItr.hasNext() ) {
         final Entry < Long, Integer > entry = uniquesItr.next();
         Lab.fromHex(entry.getKey(), lab);
         oct.insert(new Vec3(lab.a, lab.b, lab.l));
      }
      oct.cull();

      /* Trying to use package level with an array list throws an exception. */
      final Vec3[] centers = Octree.centersMean(oct, false);
      final int centersLen = centers.length;
      final Lab[] arr = new Lab[1 + centersLen];

      for ( int i = 0; i < centersLen; ++i ) {
         final Vec3 center = centers[i];
         arr[1 + i] = new Lab(center.z, center.x, center.y, 1.0f);
      }

      arr[0] = Lab.clearBlack(new Lab());
      return arr;
   }

   /**
    * Applies a palette to an image. Uses an Octree to find the nearest match
    * in Euclidean space. Retains the original color's transparency.
    *
    * @param source  the source pixels
    * @param palette the color palette
    * @param target  the target pixels
    *
    * @return the mapped image
    */
   public static Img paletteMap ( final Img source, final Lab[] palette,
      final Img target ) {

      return Img.paletteMap(source, palette, Octree.DEFAULT_CAPACITY,
         Img.DEFAULT_OCTREE_QUERY_RADIUS, target);
   }

   /**
    * Applies a palette to an image. Uses an Octree to find the nearest match
    * in Euclidean space. Retains the original color's transparency.
    *
    * @param source   the source pixels
    * @param palette  the color palette
    * @param capacity the octree capacity
    * @param radius   the query radius
    * @param target   the target pixels
    *
    * @return the mapped image
    */
   public static Img paletteMap ( final Img source, final Lab[] palette,
      final int capacity, final float radius, final Img target ) {

      final int len = source.pixels.length;
      if ( !Img.similar(source, target) ) {
         target.width = source.width;
         target.height = source.height;
         target.pixels = new long[len];
      }

      final Octree oct = new Octree(Bounds3.lab(new Bounds3()), capacity);
      oct.subdivide(1, capacity);

      final HashMap < Vec3, Long > lookup = new HashMap <>(256, 0.75f);
      final int palLen = palette.length;
      for ( int i = 0; i < palLen; ++i ) {
         final Lab swatch = palette[i];
         if ( Lab.any(swatch) ) {
            final Vec3 point = new Vec3(swatch.a, swatch.b, swatch.l);
            oct.insert(point);
            lookup.put(point, swatch.toHexLongSat());
         }
      }

      oct.cull();

      final float rVrf = Utils.max(IUtils.EPSILON, Utils.abs(radius));
      final float rsq = rVrf * rVrf;
      final Lab lab = new Lab();
      final Vec3 query = new Vec3();
      final TreeMap < Float, Vec3 > found = new TreeMap <>();
      final HashMap < Long, Long > convert = new HashMap <>();
      convert.put(Img.CLEAR_PIXEL, Img.CLEAR_PIXEL);

      for ( int j = 0; j < len; ++j ) {
         final long tlab64Src = source.pixels[j];
         final long alphaSrc = tlab64Src & Img.T_MASK;
         final Long tlab64SrcObj = tlab64Src;
         long tlab64Trg = Img.CLEAR_PIXEL;
         if ( convert.containsKey(tlab64SrcObj) ) {
            tlab64Trg = convert.get(tlab64SrcObj);
         } else {
            Lab.fromHex(tlab64Src, lab);
            query.set(lab.a, lab.b, lab.l);
            found.clear();
            Octree.query(oct, query, rsq, 0, found);
            if ( found.size() > 0 ) {
               final Vec3 near = found.values().iterator().next();
               if ( near != null && lookup.containsKey(near) ) {
                  tlab64Trg = lookup.get(near);
               }
            }
            convert.put(tlab64SrcObj, tlab64Trg);
         }
         target.pixels[j] = alphaSrc | tlab64Trg & Img.LAB_MASK;
      }

      return target;
   }

   /**
    * Applies a palette to an image. Uses an Octree to find the nearest match
    * in Euclidean space. Retains the original color's transparency.
    *
    * @param source   the source pixels
    * @param palette  the color palette
    * @param capacity the octree capacity
    * @param target   the target pixels
    *
    * @return the mapped image
    */
   public static Img paletteMap ( final Img source, final Lab[] palette,
      final int capacity, final Img target ) {

      return Img.paletteMap(source, palette, capacity,
         Img.DEFAULT_OCTREE_QUERY_RADIUS, target);
   }

   /**
    * Generates an image with random pixels for diagnostic purposes.
    *
    * @param rng       the random number generator
    * @param inclAlpha whether to include alpha
    * @param target    the output image
    *
    * @return the image
    */
   public static Img random ( final Rng rng, final boolean inclAlpha,
      final Img target ) {

      final long mask = inclAlpha ? 0 : Img.T_MASK;
      final int len = target.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         target.pixels[i] = mask | rng.nextLong();
      }
      return target;
   }

   /**
    * Removes translucent pixels from an image, so colors are either
    * transparent or opaque.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the binary alpha image
    */
   public static final Img removeTranslucency ( final Img source,
      final Img target ) {

      final int len = source.pixels.length;
      if ( !Img.similar(source, target) ) {
         target.width = source.width;
         target.height = source.height;
         target.pixels = new long[len];
      }

      for ( int i = 0; i < len; ++i ) {
         final long c = source.pixels[i];
         final long t16Src = c >> Img.T_SHIFT & 0xffffL;
         final long t16Trg = t16Src < 0x80000L ? 0x0000L : 0xffffL;
         target.pixels[i] = t16Trg << Img.T_SHIFT | c & Img.LAB_MASK;
      }
      return target;
   }

   /**
    * Resizes a source image and places the pixels into a target image.
    *
    * @param source the source image
    * @param wTrg   the target width
    * @param hTrg   the target height
    * @param target the target image
    *
    * @return the resized image
    */
   public static final Img resizeBilinear ( final Img source, final int wTrg,
      final int hTrg, final Img target ) {

      final int wTrgVerif = Utils.clamp(Math.abs(wTrg), 1, Img.MAX_DIMENSION);
      final int hTrgVerif = Utils.clamp(Math.abs(hTrg), 1, Img.MAX_DIMENSION);

      final int wSrc = source.width;
      final int hSrc = source.height;
      final int srcLen = source.pixels.length;

      if ( wSrc == wTrgVerif && hSrc == hTrgVerif ) {
         target.width = wSrc;
         target.height = hSrc;
         target.pixels = new long[srcLen];
         System.arraycopy(source.pixels, 0, target.pixels, 0, srcLen);
         return target;
      }

      final float wDenom = wTrgVerif - 1.0f;
      final float hDenom = hTrgVerif - 1.0f;
      final float tx = wDenom != 0.0f ? ( wSrc - 1.0f ) / wDenom : 0.0f;
      final float ty = hDenom != 0.0f ? ( hSrc - 1.0f ) / hDenom : 0.0f;
      final float ox = wDenom != 0.0f ? 0.0f : 0.5f;
      final float oy = hDenom != 0.0f ? 0.0f : 0.5f;

      final int trgLen = wTrgVerif * hTrgVerif;
      final long[] trgPixels = new long[trgLen];

      for ( int i = 0; i < trgLen; ++i ) {
         trgPixels[i] = Img.sampleBilinear(source, tx * ( i % wTrgVerif ) + ox,
            ty * ( i / wTrgVerif ) + oy);
      }

      target.width = wTrgVerif;
      target.height = hTrgVerif;
      target.pixels = trgPixels;

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
       * If user wants to make a zero alpha image that has rgb channels, let
       * them.
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
    * Rotates the pixels of a source image around the image center by an angle
    * in radians.
    *
    * @param source the source image
    * @param angle  the angle in radians
    * @param target the target image
    *
    * @return rotated image
    *
    * @see Utils#mod(int, int)
    * @see Utils#round(float)
    */
   public static final Img rotate ( final Img source, final float angle,
      final Img target ) {

      return Img.rotateBilinear(source, angle, target);
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

      final int len = source.pixels.length;
      if ( !Img.similar(source, target) ) {
         target.width = source.width;
         target.height = source.height;
         target.pixels = new long[len];
      }

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

      final int len = source.pixels.length;
      if ( !Img.similar(source, target) ) { target.pixels = new long[len]; }

      final int w = source.width;
      final int h = source.height;
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

      final int len = source.pixels.length;
      if ( !Img.similar(source, target) ) { target.pixels = new long[len]; }

      final int w = source.width;
      final int h = source.height;
      final int lennh = len - h;
      for ( int i = 0; i < len; ++i ) {
         target.pixels[lennh + i / w - i % w * h] = source.pixels[i];
      }

      target.width = h;
      target.height = w;

      return target;
   }

   /**
    * Rotates the pixels of a source image around the image center by an angle
    * in radians. Where the angle is approximately 0, 90, 180 and 270 degrees,
    * resorts to faster methods. Uses bilinear filtering.
    *
    * @param source the source pixels
    * @param angle  the angle in radians
    * @param target the target pixels
    *
    * @return rotated pixels
    *
    * @see Utils#mod(int, int)
    * @see Utils#round(float)
    */
   public static final Img rotateBilinear ( final Img source, final float angle,
      final Img target ) {

      final long[] srcPixels = source.pixels;
      final int srcLen = srcPixels.length;
      final int deg = Utils.mod(Utils.round(angle * IUtils.RAD_TO_DEG), 360);
      switch ( deg ) {
         case 0: {
            final long[] trgPixels = new long[srcLen];
            System.arraycopy(srcPixels, 0, trgPixels, 0, srcLen);

            target.width = source.width;
            target.height = source.height;
            target.pixels = trgPixels;

            return target;
         }

         /* @formatter:off */
         case 90: { return Img.rotate90(source, target); }
         case 180: { return Img.rotate180(source, target); }
         case 270: { return Img.rotate270(source, target); }
         /* @formatter:on */

         default: {
            final double avd = angle;
            final float cosa = ( float ) Math.cos(avd);
            final float sina = ( float ) Math.sin(avd);
            return Img.rotateBilinear(source, cosa, sina, target);
         }
      }
   }

   /**
    * Scales an image by a percentage of its original width and height.
    * Percentages are expected to be within [0.0, 1.0].
    *
    * @param source the source image
    * @param wPrc   the width percentage
    * @param hPrc   the height percentage
    * @param target the target image
    *
    * @return the scaled image
    *
    * @see Utils#round(float)
    */
   public static final Img scaleBilinear ( final Img source, final float wPrc,
      final float hPrc, final Img target ) {

      return Img.resizeBilinear(source, Utils.round(wPrc * source.width), Utils
         .round(hPrc * source.width), target);
   }

   /**
    * Scales an image by a percentage of its original width and height.
    * Percentages are expected to be within [0.0, 1.0].
    *
    * @param source the source image
    * @param prc    the percentage
    * @param target the target image
    *
    * @return the scaled image
    */
   public static final Img scaleBilinear ( final Img source, final float prc,
      final Img target ) {

      return Img.scaleBilinear(source, prc, prc, target);
   }

   /**
    * Scales an image by a percentage of its original width and height.
    * Percentages are expected to be within [0.0, 1.0].
    *
    * @param source the source image
    * @param prc    the percentage
    * @param target the target image
    *
    * @return the scaled image
    */
   public static final Img scaleBilinear ( final Img source, final Vec2 prc,
      final Img target ) {

      return Img.scaleBilinear(source, prc.x, prc.y, target);
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
         final long tIso = srcPixel & Img.T_MASK;
         final long lIso = srcPixel & Img.L_MASK;
         final long aIso = srcPixel & Img.A_MASK;
         final long bIso = srcPixel & Img.B_MASK;

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
   public static final Img[] sepRgb ( final Img source,
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

         rIso.set(srgb.r, 0.0f, 0.0f, 0.0f);
         gIso.set(0.0f, srgb.g, 0.0f, 0.0f);
         bIso.set(0.0f, 0.0f, srgb.b, 0.0f);

         final long rLab64 = Rgb.sRgbToSrLab2(rIso, lab, xyz, lrgb)
            .toHexLongSat();
         final long gLab64 = Rgb.sRgbToSrLab2(gIso, lab, xyz, lrgb)
            .toHexLongSat();
         final long bLab64 = Rgb.sRgbToSrLab2(bIso, lab, xyz, lrgb)
            .toHexLongSat();

         final long tIso = tlab64 & Img.T_MASK;

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
    * Skews the pixels of a source image vertically. If the angle is
    * approximately zero, copies the source array. If the angle is
    * approximately {@link IUtils#HALF_PI} ({@value IUtils#HALF_PI}, returns a
    * clear image.
    *
    * @param source the source image
    * @param angle  the angle in radians
    * @param target the target image
    *
    * @return the skewed array
    */
   public static final Img skewXBilinear ( final Img source, final float angle,
      final Img target ) {

      final int wSrc = source.width;
      final int hSrc = source.height;
      final int srcLen = source.pixels.length;

      if ( !Img.similar(source, target) ) {
         target.width = wSrc;
         target.height = hSrc;
         target.pixels = new long[srcLen];
      }

      final float wSrcf = wSrc;
      final float hSrcf = hSrc;
      final int deg = Utils.round(angle * IUtils.RAD_TO_DEG);
      final int deg180 = Utils.mod(deg, 180);

      switch ( deg180 ) {
         case 0: {
            System.arraycopy(source.pixels, 0, target.pixels, 0, srcLen);
            return target;
         }

         case 88:
         case 89:
         case 90:
         case 91:
         case 92: {
            return Img.clear(target);
         }

         default: {
            final float tana = ( float ) Math.tan(angle);
            final int wTrg = ( int ) ( 0.5f + wSrcf + Utils.abs(tana) * hSrcf );
            final float wTrgf = wTrg;
            final float yCenter = hSrcf * 0.5f;
            final float xDiff = ( wSrcf - wTrgf ) * 0.5f;

            final int trgLen = wTrg * hSrc;
            final long[] trgPixels = new long[trgLen];
            for ( int i = 0; i < trgLen; ++i ) {
               final float yTrg = i / wTrg;
               trgPixels[i] = Img.sampleBilinear(source, xDiff + i % wTrg + tana
                  * ( yTrg - yCenter ), yTrg);
            }

            target.width = wTrg;
            target.pixels = trgPixels;

            return target;
         }
      }
   }

   /**
    * Skews the pixels of a source image vertically. If the angle is
    * approximately zero, copies the source array. If the angle is
    * approximately {@link IUtils#HALF_PI} ({@value IUtils#HALF_PI}, returns a
    * clear image.
    *
    * @param source the source image
    * @param angle  the angle in radians
    * @param target the target image
    *
    * @return the skewed array
    */
   public static final Img skewYBilinear ( final Img source, final float angle,
      final Img target ) {

      final int wSrc = source.width;
      final int hSrc = source.height;
      final int srcLen = source.pixels.length;

      if ( !Img.similar(source, target) ) {
         target.width = wSrc;
         target.height = hSrc;
         target.pixels = new long[srcLen];
      }

      final float wSrcf = wSrc;
      final float hSrcf = hSrc;
      final int deg = Utils.round(angle * IUtils.RAD_TO_DEG);
      final int deg180 = Utils.mod(deg, 180);

      switch ( deg180 ) {
         case 0: {
            System.arraycopy(source.pixels, 0, target.pixels, 0, srcLen);
            return target;
         }

         case 88:
         case 89:
         case 90:
         case 91:
         case 92: {
            return Img.clear(target);
         }

         default: {
            final float tana = ( float ) Math.tan(angle);
            final int hTrg = ( int ) ( 0.5f + hSrcf + Utils.abs(tana) * wSrcf );
            final float hTrgf = hTrg;
            final float xCenter = wSrcf * 0.5f;
            final float yDiff = ( hSrcf - hTrgf ) * 0.5f;

            final int trgLen = wSrc * hTrg;
            final long[] trgPixels = new long[trgLen];
            for ( int i = 0; i < trgLen; ++i ) {
               final float xTrg = i % wSrc;
               trgPixels[i] = Img.sampleBilinear(source, xTrg, yDiff + i / wSrc
                  + tana * ( xTrg - xCenter ));
            }

            target.height = hTrg;
            target.pixels = trgPixels;

            return target;
         }
      }
   }

   /**
    * Converts a LAB image to an array of 32-bit AARRGGBB pixels.
    *
    * @param source  the source image
    * @param mapFunc the tone mapping function
    *
    * @return the pixels
    */
   public static final int[] toArgb32 ( final Img source,
      final Rgb.AbstrToneMap mapFunc ) {

      final int len = source.pixels.length;
      final int[] argb32s = new int[len];
      final HashMap < Long, Integer > convert = new HashMap <>();
      convert.put(Img.CLEAR_PIXEL, 0);

      final Rgb mapped = new Rgb();
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
            mapFunc.apply(srgb, mapped);
            argb32 = mapped.toHexIntWrap();
            convert.put(tlab64Obj, argb32);
         }

         argb32s[i] = argb32;
      }

      return argb32s;
   }

   /**
    * Transposes the source image, stores the transposition in the target
    * image.
    *
    * @param source the source image
    * @param w      the image width
    * @param h      the image height
    * @param target the target image
    *
    * @return the transposed image
    */
   public static final Img transpose ( final Img source, final int w,
      final int h, final Img target ) {

      /**
       * See https://en.wikipedia.org/wiki/In-place_matrix_transposition and
       * https://johnloomis.org/ece563/notes/geom/basic/geom.htm for notes on
       * transposing in place.
       */

      final int len = source.pixels.length;
      if ( !Img.similar(source, target) ) { target.pixels = new long[len]; }

      for ( int i = 0; i < len; ++i ) {
         target.pixels[i % w * h + i / w] = source.pixels[i];
      }

      target.width = h;
      target.height = w;

      return target;
   }

   /**
    * Removes excess transparent pixels from an array of pixels.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the trimmed image
    */
   public static final Img trimAlpha ( final Img source, final Img target ) {

      return Img.trimAlpha(source, null, target);
   }

   /**
    * Removes excess transparent pixels from an array of pixels. Adapted from
    * the implementation by Oleg Mikhailov: <a href=
    * "https://stackoverflow.com/a/36938923">https://stackoverflow.com/a/36938923</a>.
    * <br>
    * <br>
    * Emits the new image dimensions to a {@link Vec2}.
    *
    * @param source the source image
    * @param tl     top left
    * @param target the target image
    *
    * @return the trimmed image
    *
    * @author Oleg Mikhailov
    */
   public static final Img trimAlpha ( final Img source, final Vec2 tl,
      final Img target ) {

      final long[] srcPixels = source.pixels;
      final int srcLen = srcPixels.length;
      final int wSrc = source.width;
      final int hSrc = source.height;

      if ( wSrc < 2 && hSrc < 2 ) {
         final long[] trgPixels = new long[srcLen];
         System.arraycopy(source, 0, trgPixels, 0, srcLen);
         target.width = wSrc;
         target.height = hSrc;
         target.pixels = trgPixels;
         return target;
      }

      final int wn1 = wSrc > 1 ? wSrc - 1 : 0;
      final int hn1 = hSrc > 1 ? hSrc - 1 : 0;

      int minRight = wn1;
      int minBottom = hn1;

      /* Top search. y is outer loop, x is inner loop. */
      int top = -1;
      boolean goTop = true;
      while ( goTop && top < hn1 ) {
         ++top;
         final int wtop = wSrc * top;
         int x = -1;
         while ( goTop && x < wn1 ) {
            ++x;
            if ( ( srcPixels[wtop + x] & Img.T_MASK ) != 0L ) {
               minRight = x;
               minBottom = top;
               goTop = false;
            }
         }
      }

      /* Left search. x is outer loop, y is inner loop. */
      int left = -1;
      boolean goLeft = true;
      while ( goLeft && left < minRight ) {
         ++left;
         int y = hSrc;
         while ( goLeft && y > top ) {
            --y;
            if ( ( srcPixels[y * wSrc + left] & Img.T_MASK ) != 0 ) {
               minBottom = y;
               goLeft = false;
            }
         }
      }

      /* Bottom search. y is outer loop, x is inner loop. */
      int bottom = hSrc;
      boolean goBottom = true;
      while ( goBottom && bottom > minBottom ) {
         --bottom;
         final int wbottom = wSrc * bottom;
         int x = wSrc;
         while ( goBottom && x > left ) {
            --x;
            if ( ( srcPixels[wbottom + x] & Img.T_MASK ) != 0 ) {
               minRight = x;
               goBottom = false;
            }
         }
      }

      /* Right search. x is outer loop, y is inner loop. */
      int right = wSrc;
      boolean goRight = true;
      while ( goRight && right > minRight ) {
         --right;
         int y = bottom + 1;
         while ( goRight && y > top ) {
            --y;
            if ( ( srcPixels[y * wSrc + right] & Img.T_MASK ) != 0 ) {
               goRight = false;
            }
         }
      }

      final int wTrg = 1 + right - left;
      final int hTrg = 1 + bottom - top;
      if ( wTrg < 1 || hTrg < 1 ) {
         final long[] trgPixels = new long[srcLen];
         System.arraycopy(source, 0, trgPixels, 0, srcLen);
         target.width = wSrc;
         target.height = hSrc;
         target.pixels = trgPixels;
         return target;
      }

      final int trgLen = wTrg * hTrg;
      final long[] trgPixels = new long[trgLen];
      for ( int i = 0; i < trgLen; ++i ) {
         trgPixels[i] = srcPixels[wSrc * ( top + i / wTrg ) + left + i % wTrg];
      }

      if ( tl != null ) { tl.set(left, top); }

      target.width = wSrc;
      target.height = hSrc;
      target.pixels = trgPixels;

      return target;
   }

   /**
    * Blits a source image's pixels onto a target image, using integer floor
    * modulo to wrap the source image. The source image can be offset
    * horizontally and/or vertically, creating the illusion of infinite
    * background.
    *
    * @param source the source image
    * @param dx     the horizontal pixel offset
    * @param dy     the vertical pixel offset
    * @param target the target image
    *
    * @return the wrapped pixels
    */
   public static final Img wrap ( final Img source, final int dx, final int dy,
      final Img target ) {

      final int len = target.pixels.length;
      final int wTrg = target.width;
      final int wSrc = source.width;
      final int hSrc = source.height;

      for ( int i = 0; i < len; ++i ) {
         int yMod = ( i / wTrg + dy ) % hSrc;
         if ( ( yMod ^ hSrc ) < 0 && yMod != 0 ) { yMod += hSrc; }

         int xMod = ( i % wTrg - dx ) % wSrc;
         if ( ( xMod ^ wSrc ) < 0 && xMod != 0 ) { xMod += wSrc; }

         target.pixels[i] = source.pixels[xMod + wSrc * yMod];
      }

      return target;
   }

   /**
    * Inverts an image colors per a mask.
    *
    * @param source the input image
    * @param target the output image
    *
    * @return the inverted image
    */
   protected static final Img invert ( final Img source, final long mask,
      final Img target ) {

      final int len = source.pixels.length;
      if ( !Img.similar(source, target) ) {
         target.width = source.width;
         target.height = source.height;
         target.pixels = new long[len];
      }

      for ( int i = 0; i < len; ++i ) {
         target.pixels[i] = source.pixels[i] ^ mask;
      }

      return target;
   }

   /**
    * Rotates the pixels of a source image around the image center by an angle
    * in radians. Assumes that the sine and cosine of the angle have already
    * been calculated and simple cases (0, 90, 180, 270 degrees) have been
    * filtered out.
    *
    * @param source the source image
    * @param cosa   the cosine of the angle
    * @param sina   the sine of the angle
    * @param target the target image
    *
    * @return rotated pixels
    *
    * @see Utils#abs(float)
    */
   protected static final Img rotateBilinear ( final Img source,
      final float cosa, final float sina, final Img target ) {

      final int wSrc = source.width;
      final int hSrc = source.height;
      final float wSrcf = wSrc;
      final float hSrcf = hSrc;
      final float absCosa = Utils.abs(cosa);
      final float absSina = Utils.abs(sina);

      final int wTrg = ( int ) ( 0.5f + hSrcf * absSina + wSrcf * absCosa );
      final int hTrg = ( int ) ( 0.5f + hSrcf * absCosa + wSrcf * absSina );
      final float wTrgf = wTrg;
      final float hTrgf = hTrg;

      final float xSrcCenter = wSrcf * 0.5f;
      final float ySrcCenter = hSrcf * 0.5f;
      final float xTrgCenter = wTrgf * 0.5f;
      final float yTrgCenter = hTrgf * 0.5f;

      final int trgLen = wTrg * hTrg;
      final long[] trgPixels = new long[trgLen];

      for ( int i = 0; i < trgLen; ++i ) {
         final float ySgn = i / wTrg - yTrgCenter;
         final float xSgn = i % wTrg - xTrgCenter;
         trgPixels[i] = Img.sampleBilinear(source, xSrcCenter + cosa * xSgn
            - sina * ySgn, ySrcCenter + cosa * ySgn + sina * xSgn);
      }

      target.width = wTrg;
      target.height = hTrg;
      target.pixels = trgPixels;

      return target;
   }

   /**
    * Internal helper function to sample a source image with a bilinear color
    * mix. Returns a hexadecimal integer color.
    *
    * @param img  the source image
    * @param xSrc the x coordinate
    * @param ySrc the y coordinate
    *
    * @return the color
    */
   protected static final long sampleBilinear ( final Img img, final float xSrc,
      final float ySrc ) {

      final long[] source = img.pixels;
      final int wSrc = img.width;
      final int hSrc = img.height;

      /*
       * Find truncation, floor and ceiling. The ceiling cannot use yc = yf + 1
       * as a shortcut due to the case where y = 0.
       */
      final int yi = ( int ) ySrc;
      final int yf = ySrc > 0.0f ? yi : ySrc < -0.0f ? yi - 1 : 0;
      final int yc = ySrc > 0.0f ? yi + 1 : ySrc < -0.0f ? yi : 0;

      final boolean yfInBounds = yf >= 0 && yf < hSrc;
      final boolean ycInBounds = yc >= 0 && yc < hSrc;

      final int xi = ( int ) xSrc;
      final int xf = xSrc > 0.0f ? xi : xSrc < -0.0f ? xi - 1 : 0;
      final int xc = xSrc > 0.0f ? xi + 1 : xSrc < -0.0f ? xi : 0;

      /* Pixel corners colors. */
      final boolean xfInBounds = xf >= 0 && xf < wSrc;
      final boolean xcInBounds = xc >= 0 && xc < wSrc;

      /* @formatter:off */
      final long c00 = yfInBounds && xfInBounds ?
         source[yf * wSrc + xf] : Img.CLEAR_PIXEL;
      final long c10 = yfInBounds && xcInBounds ?
         source[yf * wSrc + xc] : Img.CLEAR_PIXEL;
      final long c11 = ycInBounds && xcInBounds ?
         source[yc * wSrc + xc] : Img.CLEAR_PIXEL;
      final long c01 = ycInBounds && xfInBounds ?
         source[yc * wSrc + xf] : Img.CLEAR_PIXEL;
      /* @formatter:on */

      final float xErr = xSrc - xf;

      float t0 = 0.0f;
      float l0 = 0.0f;
      float a0 = 0.0f;
      float b0 = 0.0f;

      final long t00 = c00 >> Img.T_SHIFT & 0xffffL;
      final long t10 = c10 >> Img.T_SHIFT & 0xffffL;
      if ( t00 > 0 || t10 > 0 ) {
         final float u = 1.0f - xErr;
         t0 = u * t00 + xErr * t10;
         if ( t0 > 0.0f ) {
            // TODO: Do you need to subtract 0x8000L from a and b?

            /* @formatter:off */
            l0 =    u * ( c00 >> Img.L_SHIFT & 0xffffL )
               + xErr * ( c10 >> Img.L_SHIFT & 0xffffL );
            a0 =    u * ( c00 >> Img.A_SHIFT & 0xffffL )
               + xErr * ( c10 >> Img.A_SHIFT & 0xffffL );
            b0 =    u * ( c00 >> Img.B_SHIFT & 0xffffL )
               + xErr * ( c10 >> Img.B_SHIFT & 0xffffL );
            /* @formatter:on */
         }
      }

      float t1 = 0.0f;
      float l1 = 0.0f;
      float a1 = 0.0f;
      float b1 = 0.0f;

      final long t01 = c01 >> Img.T_SHIFT & 0xffffL;
      final long t11 = c11 >> Img.T_SHIFT & 0xffffL;
      if ( t01 > 0 || t11 > 0 ) {
         final float u = 1.0f - xErr;
         t1 = u * t01 + xErr * t11;
         if ( t1 > 0.0f ) {
            // TODO: Do you need to subtract 0x8000L from a and b?

            /* @formatter:off */
            l1 =    u * ( c01 >> Img.L_SHIFT & 0xffffL )
               + xErr * ( c11 >> Img.L_SHIFT & 0xffffL );
            a1 =    u * ( c01 >> Img.A_SHIFT & 0xffffL )
               + xErr * ( c11 >> Img.A_SHIFT & 0xffffL );
            b1 =    u * ( c01 >> Img.B_SHIFT & 0xffffL )
               + xErr * ( c11 >> Img.B_SHIFT & 0xffffL );
            /* @formatter:on */
         }
      }

      if ( t0 > 0.0f || t1 > 0.0f ) {
         final float yErr = ySrc - yf;
         final float u = 1.0f - yErr;
         final float t2 = u * t0 + yErr * t1;
         if ( t2 > 0.0f ) {
            final float l2 = u * l0 + yErr * l1;
            final float a2 = u * a0 + yErr * a1;
            final float b2 = u * b0 + yErr * b1;

            // TODO: Do you need to add 0x8000L to a and b here?
            long ti = ( long ) ( 0.5f + t2 );
            long li = ( long ) ( 0.5f + l2 );
            long ai = ( long ) ( 0.5f + a2 );
            long bi = ( long ) ( 0.5f + b2 );

            if ( ti > 0xffffL ) { ti = 0xffffL; }
            if ( li > 0xffffL ) { li = 0xffffL; }
            if ( ai > 0xffffL ) { ai = 0xffffL; }
            if ( bi > 0xffffL ) { bi = 0xffffL; }

            return ti << Img.T_SHIFT | li << Img.L_SHIFT | ai << Img.A_SHIFT
               | bi << Img.B_SHIFT;
         }
      }

      return Img.CLEAR_PIXEL;
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
   protected static final TreeMap < Long, ArrayList < Integer > > toDict (
      final Img source, final TreeMap < Long, ArrayList < Integer > > target ) {

      // TODO: What would the public facing version of this method look like?
      // Maybe it would be TreeMap < Lab, integer[] >.
      // TODO: Sort according to first entry in array.

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
    * Policy for handling gray colors when adjusting by LCH.
    */
   public enum GrayPolicy {

      /**
       * Gray colors have a hue on the cool side of the color wheel.
       */
      COOL,

      /**
       * Do not saturate gray colors.
       */
      OMIT,

      /**
       * Gray colors have a hue on the warm side of the color wheel.
       */
      WARM,

      /**
       * Gray colors have zero hue.
       */
      ZERO

   }

   /**
    * Channel to use in the gradient map.
    */
   public enum MapChannel {

      /**
       * The chroma channel.
       */
      C,

      /**
       * The lightness channel.
       */
      L

   }

   /**
    * Policy for handling the pivot when adjusting contrast.
    */
   public enum PivotPolicy {

      /**
       * Pivot around a fixed number.
       */
      FIXED,

      /**
       * Pivot around the arithmetic mean (average).
       */
      MEAN,

      /**
       * Pivot around the average of the minimum and maximum.
       */
      RANGE

   }

}
