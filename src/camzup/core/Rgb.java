package camzup.core;

import java.util.function.BiFunction;

/**
 * A mutable, extensible color class that stores red, green, blue and alpha
 * in the range [0.0, 1.0]. Assumes color channels are in the standard RGB
 * (sRGB) color space. Converts to and from integers where color channels
 * are in the format 0xAARRGGBB.
 */
public class Rgb implements IColor {

   /**
    * The alpha channel, which governs transparency.
    */
   public float alpha = 1.0f;

   /**
    * The blue channel.
    */
   public float b = 1.0f;

   /**
    * The green channel.
    */
   public float g = 1.0f;

   /**
    * The red channel.
    */
   public float r = 1.0f;

   /**
    * The default constructor. Creates a white color.
    */
   public Rgb ( ) {}

   /**
    * Creates a color from bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .The alpha channel
    * defaults to 1.0 .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    */
   public Rgb ( final byte red, final byte green, final byte blue ) {

      this.set(red, green, blue);
   }

   /**
    * Creates a color from bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    * @param alpha the alpha channel
    */
   public Rgb ( final byte red, final byte green, final byte blue,
      final byte alpha ) {

      this.set(red, green, blue, alpha);
   }

   /**
    * Creates a color out of red, green and blue channels. The alpha channel
    * defaults to 1.0 .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    */
   public Rgb ( final float red, final float green, final float blue ) {

      this.set(red, green, blue);
   }

   /**
    * Creates a color out of red, green, blue and alpha channels. The expected
    * range for each channel is [0.0, 1.0], however these bounds are not
    * checked so as to facilitate color mixing in other color spaces.
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    * @param alpha the alpha channel
    */
   public Rgb ( final float red, final float green, final float blue,
      final float alpha ) {

      this.set(red, green, blue, alpha);
   }

   /**
    * Creates a color from a source.
    *
    * @param c the source color
    */
   public Rgb ( final Rgb c ) { this.set(c); }

   /**
    * Tests this color for equivalence to another based on its hexadecimal
    * representation.
    *
    * @param other the color integer
    *
    * @return the equivalence
    */
   public boolean equals ( final int other ) {

      return this.toHexInt() == other;
   }

   /**
    * Tests this color for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    *
    * @see Rgb#equals(Rgb)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Rgb ) obj);
   }

   /**
    * Returns a hash code for this color based on its hexadecimal value.
    *
    * @return the hash code
    */
   @Override
   public int hashCode ( ) { return this.toHexInt(); }

   /**
    * Resets this color to opaque white.
    *
    * @return this color
    */
   public Rgb reset ( ) { return this.set(1.0f, 1.0f, 1.0f, 1.0f); }

   /**
    * Sets a color with bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] . The alpha channel
    * defaults to 1.0 .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    *
    * @return this color
    */
   public Rgb set ( final byte red, final byte green, final byte blue ) {

      return this.set(red, green, blue, ( byte ) -1);
   }

   /**
    * Sets a color with bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    * @param alpha the alpha channel
    *
    * @return this color
    */
   public Rgb set ( final byte red, final byte green, final byte blue,
      final byte alpha ) {

      /* @formatter:off */
      return this.set(
         IUtils.ONE_255 * ( red & 0xff ),
         IUtils.ONE_255 * ( green & 0xff ),
         IUtils.ONE_255 * ( blue & 0xff ),
         IUtils.ONE_255 * ( alpha & 0xff ));
      /* @formatter:on */
   }

   /**
    * Sets the red, green and blue color channels of this color. The alpha
    * channel defaults to 1.0 .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    *
    * @return this color
    */
   public Rgb set ( final float red, final float green, final float blue ) {

      return this.set(red, green, blue, 1.0f);
   }

   /**
    * Sets the red, green, blue and alpha color channels of this color. The
    * expected range for each channel is [0.0, 1.0], however these bounds are
    * not checked so as to facilitate color mixing in other color spaces.
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    * @param alpha the alpha channel
    *
    * @return this color
    */
   public Rgb set ( final float red, final float green, final float blue,
      final float alpha ) {

      this.r = red;
      this.g = green;
      this.b = blue;
      this.alpha = alpha;
      return this;
   }

   /**
    * Sets this color to the source color.
    *
    * @param source the source color
    *
    * @return this color
    */
   public Rgb set ( final Rgb source ) {

      return this.set(source.r, source.g, source.b, source.alpha);
   }

   /**
    * Converts a color to an integer where hexadecimal represents the color
    * channels as 0xAARRGGBB . Defaults to modular arithmetic.
    *
    * @return the color in hexadecimal
    */
   @Override
   public int toHexInt ( ) { return this.toHexIntWrap(); }

   /**
    * Converts a color to an integer where hexadecimal represents the color
    * channels as 0xAARRGGBB . Uses saturation arithmetic. Two colors with
    * unequal values beyond [0.0, 1.0] may yield equal integers.
    *
    * @return the color in hexadecimal
    *
    * @see Utils#clamp01(float)
    */
   @Override
   public int toHexIntSat ( ) {

      /* @formatter:off */
      return ( int ) ( Utils.clamp01(this.alpha) * 0xff + 0.5f ) << 0x18
           | ( int ) ( Utils.clamp01(this.r) * 0xff + 0.5f ) << 0x10
           | ( int ) ( Utils.clamp01(this.g) * 0xff + 0.5f ) << 0x08
           | ( int ) ( Utils.clamp01(this.b) * 0xff + 0.5f );
      /* @formatter:on */
   }

   /**
    * Converts a color to an integer where hexadecimal represents the color
    * channels as 0xAARRGGBB . Uses modular arithmetic, so out-of-gamut colors
    * may cause overflow and unexpected hexadecimal colors.
    *
    * @return the color in hexadecimal
    */
   @Override
   public int toHexIntWrap ( ) {

      /* @formatter:off */
      return ( int ) ( this.alpha * 0xff + 0.5f ) << 0x18
           | ( int ) ( this.r * 0xff + 0.5f ) << 0x10
           | ( int ) ( this.g * 0xff + 0.5f ) << 0x08
           | ( int ) ( this.b * 0xff + 0.5f );
      /* @formatter:on */
   }

   /**
    * Returns a string representation of this color.
    *
    * @return the string
    */
   @Override
   public String toString ( ) {

      return this.toString(new StringBuilder(96), IUtils.FIXED_PRINT)
         .toString();
   }

   /**
    * Returns a string representation of this color.
    *
    * @param places number of decimal places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      return this.toString(new StringBuilder(96), places).toString();
   }

   /**
    * An internal helper function to format a color as a Python tuple, then
    * append it to a {@link StringBuilder}. Used for testing purposes to
    * compare results with Blender 2.9x.<br>
    * <br>
    * This is formatted as a tuple where red, green and blue channels have
    * been raised to the power of gamma (usually 2.2, Blender's default sRGB
    * color management setting).<br>
    * <br>
    * If alpha is included, then a four tuple is appended, where alpha is the
    * last element; if not then a three tuple is appended.
    *
    * @param pyCd      the string builder
    * @param gamma     the exponent
    * @param inclAlpha include the alpha channel
    *
    * @return the string
    *
    * @see Utils#clamp01(float)
    * @see Utils#toFixed(StringBuilder, float, int)
    */
   StringBuilder toBlenderCode ( final StringBuilder pyCd, final float gamma,
      final boolean inclAlpha ) {

      final double gd = gamma;

      pyCd.append('(');
      Utils.toFixed(pyCd, ( float ) Math.pow(Utils.clamp01(this.r), gd), 6);
      pyCd.append(',');
      pyCd.append(' ');
      Utils.toFixed(pyCd, ( float ) Math.pow(Utils.clamp01(this.g), gd), 6);
      pyCd.append(',');
      pyCd.append(' ');
      Utils.toFixed(pyCd, ( float ) Math.pow(Utils.clamp01(this.b), gd), 6);

      if ( inclAlpha ) {
         pyCd.append(',');
         pyCd.append(' ');
         Utils.toFixed(pyCd, Utils.clamp01(this.alpha), 6);
      }

      pyCd.append(')');
      return pyCd;
   }

   /**
    * Returns a String representation of the color compatible with .ggr (GIMP
    * gradient) file formats. Each channel, including alpha, is represented as
    * a float in [0.0, 1.0] separated by a space. Out of bounds values are
    * clamped to the range.
    *
    * @return the string
    *
    * @see Utils#clamp01(float)
    * @see Utils#toFixed(StringBuilder, float, int)
    */
   String toGgrString ( ) {

      /*
       * This does not append to a pre-existing StringBuilder because
       * differences between GGR and core gradients mean multiple String
       * conversions of one color.
       */

      final StringBuilder ggr = new StringBuilder(96);
      Utils.toFixed(ggr, Utils.clamp01(this.r), 6);
      ggr.append(' ');
      Utils.toFixed(ggr, Utils.clamp01(this.g), 6);
      ggr.append(' ');
      Utils.toFixed(ggr, Utils.clamp01(this.b), 6);
      ggr.append(' ');
      Utils.toFixed(ggr, Utils.clamp01(this.alpha), 6);
      return ggr.toString();
   }

   /**
    * Appends a representation of the color compatible with .gpl (GIMP
    * palette) file formats to a {@link StringBuilder}. Each channel,
    * including alpha, is represented an unsigned byte in [0, 255] separated
    * by a space. Saturation arithmetic is used.
    *
    * @param gpl the string builder
    *
    * @return the string builder
    *
    * @see Utils#clamp01(float)
    */
   StringBuilder toGplString ( final StringBuilder gpl ) {

      gpl.append(( int ) ( Utils.clamp01(this.r) * 0xff + 0.5f ));
      gpl.append(' ');
      gpl.append(( int ) ( Utils.clamp01(this.g) * 0xff + 0.5f ));
      gpl.append(' ');
      gpl.append(( int ) ( Utils.clamp01(this.b) * 0xff + 0.5f ));
      return gpl;
   }

   /**
    * Internal helper function to assist with methods that need to print many
    * color. Appends to an existing {@link StringBuilder}.
    *
    * @param sb     the string builder
    * @param places the number of places
    *
    * @return the string builder
    *
    * @see Utils#toFixed(StringBuilder, float, int)
    */
   StringBuilder toString ( final StringBuilder sb, final int places ) {

      sb.append("{\"r\":");
      Utils.toFixed(sb, this.r, places);
      sb.append(",\"g\":");
      Utils.toFixed(sb, this.g, places);
      sb.append(",\"b\":");
      Utils.toFixed(sb, this.b, places);
      sb.append(",\"alpha\":");
      Utils.toFixed(sb, this.alpha, places);
      sb.append('}');
      return sb;
   }

   /**
    * Tests equivalence between this and another color. Converts both to
    * hexadecimal integers.
    *
    * @param c the color
    *
    * @return the evaluation
    *
    * @see Rgb#eqSatArith(Rgb, Rgb)
    */
   protected boolean equals ( final Rgb c ) {

      return Rgb.eqSatArith(this, c);
   }

   /**
    * Tests to see if all color channels are greater than zero.
    *
    * @param c the color
    *
    * @return the evaluation
    */
   public static boolean all ( final Rgb c ) {

      return c.alpha > 0.0f && c.r > 0.0f && c.g > 0.0f && c.b > 0.0f;
   }

   /**
    * Tests to see if the alpha channel of the color is greater than zero,
    * i.e. if it has some opacity.
    *
    * @param c the color
    *
    * @return the evaluation
    */
   public static boolean any ( final Rgb c ) { return c.alpha > 0.0f; }

   /**
    * Returns the color black, ( 0.0, 0.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return black
    */
   public static final Rgb black ( final Rgb target ) {

      return target.set(0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Returns the color blue, ( 0.0, 0.0, 1.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return blue
    */
   public static final Rgb blue ( final Rgb target ) {

      return target.set(0.0f, 0.0f, 1.0f, 1.0f);
   }

   /**
    * Clamps all color channels to the range [0.0, 1.0] .
    *
    * @param c      the color
    * @param target the output color
    *
    * @return the clamped color
    *
    * @see Utils#clamp01(float)
    */
   public static final Rgb clamp01 ( final Rgb c, final Rgb target ) {

      return target.set(Utils.clamp01(c.r), Utils.clamp01(c.g), Utils.clamp01(
         c.b), Utils.clamp01(c.alpha));
   }

   /**
    * Returns the color clear black, ( 0.0, 0.0, 0.0, 0.0 ) .
    *
    * @param target the output color
    *
    * @return clear black
    */
   public static final Rgb clearBlack ( final Rgb target ) {

      return target.set(0.0f, 0.0f, 0.0f, 0.0f);
   }

   /**
    * Returns the color clear white, ( 1.0, 1.0, 1.0, 0.0 ) .
    *
    * @param target the output color
    *
    * @return clear white
    */
   public static final Rgb clearWhite ( final Rgb target ) {

      return target.set(1.0f, 1.0f, 1.0f, 0.0f);
   }

   /**
    * Returns the color cyan, ( 0.0, 1.0, 1.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return cyan
    */
   public static Rgb cyan ( final Rgb target ) {

      return target.set(0.0f, 1.0f, 1.0f, 1.0f);
   }

   /**
    * Checks if two colors have equivalent alpha channels when converted to
    * bytes in [0, 255]. Uses saturation arithmetic.
    *
    * @param o the left comparisand
    * @param d the right comparisand
    *
    * @return the equivalence
    *
    * @see Utils#clamp01(float)
    */
   public static final boolean eqAlphaSatArith ( final Rgb o, final Rgb d ) {

      return ( int ) ( Utils.clamp01(o.alpha) * 0xff + 0.5f ) == ( int ) ( Utils
         .clamp01(d.alpha) * 0xff + 0.5f );
   }

   /**
    * Checks if two colors have equivalent red, green and blue channels when
    * converted to bytes in [0, 255]. Uses saturation arithmetic.
    *
    * @param o the left comparisand
    * @param d the right comparisand
    *
    * @return the equivalence
    *
    * @see Utils#clamp01(float)
    */
   public static final boolean eqRgbSatArith ( final Rgb o, final Rgb d ) {

      /* @formatter:off */
      return ( int ) ( Utils.clamp01(o.b) * 0xff + 0.5f )
          == ( int ) ( Utils.clamp01(d.b) * 0xff + 0.5f )
          && ( int ) ( Utils.clamp01(o.g) * 0xff + 0.5f )
          == ( int ) ( Utils.clamp01(d.g) * 0xff + 0.5f )
          && ( int ) ( Utils.clamp01(o.r) * 0xff + 0.5f )
          == ( int ) ( Utils.clamp01(d.r) * 0xff + 0.5f );
      /* @formatter:on */
   }

   /**
    * Checks if two colors have equivalent red, green, blue and alpha channels
    * when converted to bytes in [0, 255]. Uses saturation arithmetic.
    *
    * @param o the left comparisand
    * @param d the right comparisand
    *
    * @return the equivalence
    *
    * @see Rgb#eqAlphaSatArith(Rgb, Rgb)
    * @see Rgb#eqRgbSatArith(Rgb, Rgb)
    */
   public static final boolean eqSatArith ( final Rgb o, final Rgb d ) {

      return Rgb.eqAlphaSatArith(o, d) && Rgb.eqRgbSatArith(o, d);
   }

   /**
    * Converts a direction to a color. Normalizes the direction, multiplies it
    * by 0.5, then adds 0.5 . Returns (0.5, 0.5, 1.0, 1.0) if the direction
    * has no magnitude.
    *
    * @param v      the direction
    * @param target the output color
    *
    * @return the color
    *
    * @see Utils#invSqrtUnchecked(float)
    */
   public static Rgb fromDir ( final Vec2 v, final Rgb target ) {

      final float mSq = v.x * v.x + v.y * v.y;
      if ( mSq > 0.0f ) {
         final float mInv = 0.5f * Utils.invSqrtUnchecked(mSq);
         return target.set(v.x * mInv + 0.5f, v.y * mInv + 0.5f, 0.5f, 1.0f);
      }
      return target.set(0.5f, 0.5f, 1.0f, 1.0f);
   }

   /**
    * Converts a direction to a color. Normalizes the direction, multiplies it
    * by 0.5, then adds 0.5 . Returns (0.5, 0.5, 1.0, 1.0) if the direction
    * has no magnitude.
    *
    * @param v      the direction
    * @param target the output color
    *
    * @return the color
    *
    * @see Utils#invSqrtUnchecked(float)
    */
   public static Rgb fromDir ( final Vec3 v, final Rgb target ) {

      final float mSq = v.x * v.x + v.y * v.y + v.z * v.z;
      if ( mSq > 0.0f ) {
         final float mInv = 0.5f * Utils.invSqrtUnchecked(mSq);
         return target.set(v.x * mInv + 0.5f, v.y * mInv + 0.5f, v.z * mInv
            + 0.5f, 1.0f);
      }
      return target.set(0.5f, 0.5f, 1.0f, 1.0f);
   }

   /**
    * Converts a hexadecimal representation of a color stored as 0xAARRGGBB
    * into a color. Does so by dividing each color channel by 255.
    *
    * @param hex    the color in hexadecimal
    * @param target the output color
    *
    * @return the color
    */
   public static Rgb fromHex ( final int hex, final Rgb target ) {

      /* @formatter:off */
      return target.set(
         ( hex >> 0x10 & 0xff ) * IUtils.ONE_255,
         ( hex >> 0x08 & 0xff ) * IUtils.ONE_255,
         ( hex         & 0xff ) * IUtils.ONE_255,
         ( hex >> 0x18 & 0xff ) * IUtils.ONE_255);
      /* @formatter:on */
   }

   /**
    * Attempts to convert a hexadecimal String to a color. Recognized formats
    * include:
    * <ul>
    * <li>"abc" - RGB, one digit per channel.</li>
    * <li>"aabbcc" - RRGGBB, two digits per channel.</li>
    * <li>"abcd" - 5 bits per red, 6 bits per green, 5 bits per blue.</li>
    * <li>"aabbccdd" - AARRGGBB, two digits per channel.</li>
    * </ul>
    * The output color will be reset if no suitable format is recognized.
    * Checks for and removes "#" and "0x" prefixes.
    *
    * @param c      the input String
    * @param target the output color
    *
    * @return the color
    *
    * @see Integer#parseInt(String, int)
    * @see Long#parseLong(String, int)
    */
   public static Rgb fromHex ( final String c, final Rgb target ) {

      String cVerif = c;
      if ( c.charAt(0) == '#' ) {
         cVerif = c.substring(1);
      } else if ( c.substring(0, 2).equals("0x") ) { cVerif = c.substring(2); }

      final int len = cVerif.length();

      try {
         switch ( len ) {
            case 3: {
               /* RGB 444. */
               final int c16 = Integer.parseInt(cVerif, 16);
               return target.set( ( c16 >> 0x8 & 0xf ) / 15.0f, ( c16 >> 0x4
                  & 0xf ) / 15.0f, ( c16 & 0xf ) / 15.0f, 1.0f);
            }

            case 4: {
               /* RGB 565. */
               final int c16 = Integer.parseInt(cVerif, 16);
               return target.set( ( c16 >> 0xb & 0x1f ) / 31.0f, ( c16 >> 0x5
                  & 0x3f ) / 63.0f, ( c16 & 0x1f ) / 31.0f, 1.0f);
            }

            case 6: {
               /* RGB 888. */
               final int c16 = Integer.parseInt(cVerif, 16);
               return target.set( ( c16 >> 0x10 & 0xff ) / 255.0f, ( c16 >> 0x08
                  & 0xff ) / 255.0f, ( c16 & 0xff ) / 255.0f, 1.0f);
            }

            case 8: {
               /* RGBA 8888. */
               final long c16 = Long.parseLong(cVerif, 16);
               return target.set( ( c16 >> 0x10L & 0xffL ) / 255.0f, ( c16
                  >> 0x08L & 0xffL ) / 255.0f, ( c16 & 0xffL ) / 255.0f, ( c16
                     >> 0x18L & 0xffL ) / 255.0f);
            }

            default:
               return target.reset();
         }
      } catch ( final Exception e ) {
         System.err.println("Could not parse input String \"" + c + "\".");
      }

      return target.reset();
   }

   /**
    * Returns the color lime green, ( 0.0, 1.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return green
    */
   public static final Rgb green ( final Rgb target ) {

      return target.set(0.0f, 1.0f, 0.0f, 1.0f);
   }

   /**
    * Evaluates whether the color is within the standard RGB gamut of [0.0,
    * 1.0] .
    *
    * @param c color
    *
    * @return the evaluation
    */
   public static final boolean isInGamut ( final Rgb c ) {

      return Rgb.isInGamut(c, 0.0f);
   }

   /**
    * Evaluates whether the color is within the standard RGB gamut of [0.0,
    * 1.0], according to a tolerance.
    *
    * @param c   color
    * @param tol tolerance
    *
    * @return the evaluation
    */
   public static final boolean isInGamut ( final Rgb c, final float tol ) {

      /* @formatter:off */
      final float oneptol = 1.0f + tol;
      return c.r >= -tol && c.r <= oneptol
         && c.g >= -tol && c.g <= oneptol
         && c.b >= -tol && c.b <= oneptol;
      /* @formatter:on */
   }

   /**
    * Converts a color from linear RGB to standard RGB (sRGB).
    *
    * @param source    the linear color
    * @param inclAlpha adjust the alpha channel
    * @param target    the output color
    *
    * @return the standard color
    */
   public static final Rgb lRgbTosRgb ( final Rgb source,
      final boolean inclAlpha, final Rgb target ) {

      /* pow(x, y) := exp(y * ln(x)) does not lead to better performance. */

      /* @formatter:off */
      return target.set(
         source.r <= 0.0031308f ?
         source.r * 12.92f :
         ( float ) ( Math.pow(source.r, 0.4166666666666667d) * 1.055d - 0.055d ),

         source.g <= 0.0031308f ?
         source.g * 12.92f :
         ( float ) ( Math.pow(source.g, 0.4166666666666667d) * 1.055d - 0.055d ),

         source.b <= 0.0031308f ?
         source.b * 12.92f :
         ( float ) ( Math.pow(source.b, 0.4166666666666667d) * 1.055d - 0.055d ),

         inclAlpha ?
         source.alpha <= 0.0031308f ?
         source.alpha * 12.92f :
         ( float ) ( Math.pow(source.alpha, 0.4166666666666667d) * 1.055d - 0.055d ) :
         source.alpha);
      /* @formatter:on */
   }

   /**
    * Converts a color from linear RGB to the XYZ coordinates for SR LAB 2.
    * The values returned are in the range [0.0, 1.0]. See <a href=
    * "https://www.magnetkern.de/srlab2.html">https://www.magnetkern.de/srlab2.html</a>.
    *
    * @param r      the red component
    * @param g      the green component
    * @param b      the blue component
    * @param a      the alpha component
    * @param target the output vector
    *
    * @return the XYZ values
    *
    * @author Jan Behrens
    */
   public static final Vec4 lRgbToSrXyz ( final float r, final float g,
      final float b, final float a, final Vec4 target ) {

      final double rd = r;
      final double gd = g;
      final double bd = b;

      /* @formatter:off */
      return target.set(
         ( float ) ( 0.32053d * rd + 0.63692d * gd + 0.04256d * bd ),
         ( float ) ( 0.161987d * rd + 0.756636d * gd + 0.081376d * bd ),
         ( float ) ( 0.017228d * rd + 0.10866d * gd + 0.874112d * bd ), a);
      /* @formatter:on */
   }

   /**
    * Converts a color from linear RGB to the XYZ coordinates for SR LAB 2.
    * The values returned are in the range [0.0, 1.0].
    *
    * @param c      the color
    * @param target the output vector
    *
    * @return the XYZ values
    *
    * @see Rgb#lRgbToSrXyz(Rgb, Vec4)
    */
   public static final Vec4 lRgbToSrXyz ( final Rgb c, final Vec4 target ) {

      return Rgb.lRgbToSrXyz(c.r, c.g, c.b, c.alpha, target);
   }

   /**
    * Returns the color magenta, ( 1.0, 0.0, 1.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return magenta
    */
   public static final Rgb magenta ( final Rgb target ) {

      return target.set(1.0f, 0.0f, 1.0f, 1.0f);
   }

   /**
    * Tests to see if the alpha channel of this color is less than or equal to
    * zero, i.e., if it is completely transparent.
    *
    * @param c the color
    *
    * @return the evaluation
    */
   public static boolean none ( final Rgb c ) { return c.alpha <= 0.0f; }

   /**
    * Multiplies the red, green and blue color channels of a color by the
    * alpha channel.
    *
    * @param c      the input color
    * @param target the output color
    *
    * @return the multiplied color
    */
   public static final Rgb premul ( final Rgb c, final Rgb target ) {

      if ( c.alpha <= 0.0f ) { return target.set(0.0f, 0.0f, 0.0f, 0.0f); }
      if ( c.alpha >= 1.0f ) { return target.set(c.r, c.g, c.b, 1.0f); }
      return target.set(c.r * c.alpha, c.g * c.alpha, c.b * c.alpha, c.alpha);
   }

   /**
    * Creates a quantized version of the color. Uses unsigned quantization. A
    * level of 1 or -1 will copy the input color to the target.
    *
    * @param c      the color
    * @param levels the levels
    * @param target the output color
    *
    * @return the quantized color
    *
    * @see Utils#abs(float)
    * @see Utils#quantizeUnsigned(float, float, float)
    */
   public static final Rgb quantize ( final Rgb c, final int levels,
      final Rgb target ) {

      if ( levels == 1 || levels == -1 ) { return target.set(c); }
      final float levf = Utils.abs(levels);
      final float delta = 1.0f / ( levf - 1.0f );
      return target.set(Utils.quantizeUnsigned(c.r, levf, delta), Utils
         .quantizeUnsigned(c.g, levf, delta), Utils.quantizeUnsigned(c.b, levf,
            delta), Utils.quantizeUnsigned(c.alpha, levf, delta));
   }

   /**
    * Returns the color red, ( 1.0, 0.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return red
    */
   public static final Rgb red ( final Rgb target ) {

      return target.set(1.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Converts a color from standard RGB (sRGB) to linear RGB.
    *
    * @param source the standard color
    * @param alpha  adjust the alpha channel
    * @param target the output color
    *
    * @return the linear color
    */
   public static final Rgb sRgbTolRgb ( final Rgb source, final boolean alpha,
      final Rgb target ) {

      /*
       * pow(x, y) := exp(y * log(x)) does not lead to better performance.
       * pow(x, 2.4) := x * x * pow(x, 0.4) needs more testing...
       */

      return target.set(source.r <= 0.04045f ? source.r * 0.07739938f
         : ( float ) Math.pow( ( source.r + 0.055d ) * 0.9478672985781991d,
            2.4d), source.g <= 0.04045f ? source.g * 0.07739938f
               : ( float ) Math.pow( ( source.g + 0.055d )
                  * 0.9478672985781991d, 2.4d), source.b <= 0.04045f ? source.b
                     * 0.07739938f : ( float ) Math.pow( ( source.b + 0.055d )
                        * 0.9478672985781991d, 2.4d), alpha ? source.alpha
                           <= 0.04045f ? source.alpha * 0.07739938f
                              : ( float ) Math.pow( ( source.alpha + 0.055d )
                                 * 0.9478672985781991d, 2.4d) : source.alpha);
   }

   /**
    * Converts a color from standard RGB (sRGB) to SR LAB 2.
    *
    * @param srgb the sRGB color
    * @param lab  the LAB color
    * @param xyz  the XYZ color
    * @param lrgb the linear sRGB color
    *
    * @return the output color
    *
    * @see Rgb#lRgbToSrXyz(Rgb, Vec4)
    * @see Rgb#sRgbTolRgb(Rgb, boolean, Rgb)
    */
   public static final Lab sRgbToSrLab2 ( final Rgb srgb, final Lab lab,
      final Vec4 xyz, final Rgb lrgb ) {

      return Lab.fromSrXyz(Rgb.lRgbToSrXyz(Rgb.sRgbTolRgb(srgb, false, lrgb),
         xyz), lab);
   }

   /**
    * Converts an array of colors from standard RGB (sRGB) to SR LAB 2.
    *
    * @param source the sRGB color array
    * @param target the LAB color array
    * @param xyz    the XYZ color
    * @param lrgb   the linear sRGB color
    *
    * @return the output color
    *
    * @see Rgb#sRgbToSrLab2(Rgb, Lab, Vec4, Rgb)
    */
   public static final Lab[] sRgbToSrLab2 ( final Rgb[] source,
      final Lab[] target, final Vec4 xyz, final Rgb lrgb ) {

      final int srcLen = source.length;
      for ( int i = 0; i < srcLen; ++i ) {
         target[i] = Rgb.sRgbToSrLab2(source[i], new Lab(), xyz, lrgb);
      }
      return target;
   }

   /**
    * Converts a color from SR LAB 2 to standard RGB (sRGB).
    *
    * @param lab  the LAB color
    * @param srgb the sRGB color
    * @param lrgb the linear sRGB color
    * @param xyz  the XYZ color
    *
    * @return the output color
    *
    * @see Lab#toSrXyz(Lab, Vec4)
    * @see Rgb#lRgbTosRgb(Rgb, boolean, Rgb)
    * @see Rgb#srXyzTolRgb(Vec4, Rgb)
    */
   public static final Rgb srLab2TosRgb ( final Lab lab, final Rgb srgb,
      final Rgb lrgb, final Vec4 xyz ) {

      return Rgb.lRgbTosRgb(Rgb.srXyzTolRgb(Lab.toSrXyz(lab, xyz), lrgb), false,
         srgb);
   }

   /**
    * Converts an array of colors from SR LAB 2 to standard RGB (sRGB).
    *
    * @param source the LAB color array
    * @param target the sRGB color array
    * @param lrgb   the linear sRGB color
    * @param xyz    the XYZ color
    *
    * @return the output color
    *
    * @see Rgb#srLab2TosRgb(Lab, Rgb, Rgb, Vec4)
    */
   public static final Rgb[] srLab2TosRgb ( final Lab[] source,
      final Rgb[] target, final Rgb lrgb, final Vec4 xyz ) {

      final int srcLen = source.length;
      for ( int i = 0; i < srcLen; ++i ) {
         target[i] = Rgb.srLab2TosRgb(source[i], new Rgb(), lrgb, xyz);
      }
      return target;
   }

   /**
    * Converts a color from SR XYZ to linear RGB. Expects input values to be
    * in the range [0.0, 1.0].
    *
    * @param x      the x coordinate
    * @param y      the y coordinate
    * @param z      the z coordinate
    * @param a      the alpha component
    * @param target the output color
    *
    * @return the color
    *
    * @author Jan Behrens
    */
   public static final Rgb srXyzTolRgb ( final float x, final float y,
      final float z, final float a, final Rgb target ) {

      final double xd = x;
      final double yd = y;
      final double zd = z;

      /* @formatter:off */
      return target.set(
         ( float ) ( 5.435679d * xd - 4.599131d * yd + 0.163593d * zd ),
         ( float ) ( -1.16809d * xd + 2.327977d * yd - 0.159798d * zd ),
         ( float ) ( 0.03784d * xd - 0.198564d * yd + 1.160644d * zd ), a);
      /* @formatter:on */
   }

   /**
    * Converts a color from SR XYZ to linear RGB.
    *
    * @param source the XYZ vector
    * @param target the output color
    *
    * @return the color
    *
    * @see Rgb#srXyzTolRgb(float, float, float, float, Rgb)
    */
   public static final Rgb srXyzTolRgb ( final Vec4 source, final Rgb target ) {

      return Rgb.srXyzTolRgb(source.x, source.y, source.z, source.w, target);
   }

   /**
    * Writes a color's data as a 40 byte entry in an Adobe Swatch Exchange
    * (ase) palette file beginning at the cursor index.
    *
    * @param c      the color
    * @param target the byte array
    * @param cursor the cursor
    *
    * @return the bytes
    *
    * @see Rgb#toHexWeb(Rgb)
    * @see String#toCharArray()
    * @see Utils#bytesml(float, byte[], int)
    */
   public static byte[] toAseBytes ( final Rgb c, final byte[] target,
      final int cursor ) {

      /* Color entry code (2 bytes). */
      target[cursor + 0] = 0;
      target[cursor + 1] = 1;

      /* Block length (4 bytes). */
      target[cursor + 2] = 0;
      target[cursor + 3] = 0;
      target[cursor + 4] = 0;
      target[cursor + 5] = 34;

      /* Name length (2 bytes). */
      target[cursor + 6] = 0;
      target[cursor + 7] = 7;

      /* Name (14 bytes). */
      final String hexWeb = Rgb.toHexWeb(c);
      final char[] hexChars = hexWeb.toCharArray();
      for ( int i = 1; i < 7; ++i ) {
         final int i2 = ( i - 1 ) * 2;
         target[cursor + 8 + i2] = 0;
         target[cursor + 9 + i2] = ( byte ) hexChars[i];
      }

      /* Name must be terminated by a zero. */
      target[cursor + 20] = 0;
      target[cursor + 21] = 0;

      /* color format (4 bytes). */
      target[cursor + 22] = 'R';
      target[cursor + 23] = 'G';
      target[cursor + 24] = 'B';
      target[cursor + 25] = ' ';

      Utils.bytesml(c.r, target, cursor + 26);
      Utils.bytesml(c.g, target, cursor + 30);
      Utils.bytesml(c.b, target, cursor + 34);

      /* Normal color mode, as opposed to spot or global. */
      target[cursor + 38] = 0;
      target[cursor + 39] = 2;

      return target;
   }

   /**
    * Returns a byte array representing the colors in the Adobe Swatch
    * Exchange (ase) palette format.
    *
    * @param arr the color array
    *
    * @return the byte array
    *
    * @see Rgb#toAseBytes(Rgb, byte[], int)
    * @see Utils#bytesml(int, byte[], int)
    * @see Utils#bytesml(short, byte[], int)
    */
   public static byte[] toAseBytes ( final Rgb[] arr ) {

      final int numColors = arr.length;
      final int bufLen = 42 + numColors * 40;
      final byte[] target = new byte[bufLen];

      /* Write ASEF signature (4 bytes). */
      Utils.bytesml(0x41534546, target, 0);

      /* Write version (4 bytes). */
      Utils.bytesml(0x00010000, target, 4);

      /* Write number of blocks, not colors (4 bytes). */
      Utils.bytesml(numColors + 2, target, 8);

      /* Write open group block (2 bytes). */
      Utils.bytesml(( short ) 0xc001, target, 12);

      /* Write open group block length (4 bytes). */
      Utils.bytesml(12, target, 14);

      /* Write name string length plus terminal zero. */
      Utils.bytesml(( short ) 8, target, 18);

      target[21] = 'P';
      target[23] = 'a';
      target[25] = 'l';
      target[27] = 'e';
      target[29] = 't';
      target[31] = 't';
      target[33] = 'e';
      target[35] = 0;

      int cursor = 36;
      for ( int i = 0; i < numColors; ++i ) {
         Rgb.toAseBytes(arr[i], target, cursor);
         cursor += 40;
      }

      /* Write close group block. */
      Utils.bytesml(( short ) 0xc002, target, cursor);

      /* Write close group block length. */
      Utils.bytesml(0, target, cursor + 2);

      return target;
   }

   /**
    * Returns a String representing the color array in the GIMP palette file
    * format.
    *
    * @param arr the array
    *
    * @return the string
    */
   public static String toGplString ( final Rgb[] arr ) {

      return Rgb.toGplString(arr, "Palette");
   }

   /**
    * Returns a String representing the color array in the GIMP palette file
    * format. The number of columns defaults to the square root of the array's
    * length; for example, 16 colors would have 4 columns.
    *
    * @param arr  the array
    * @param name the palette name
    *
    * @return the string
    *
    * @see Utils#ceil(float)
    * @see Utils#sqrt(float)
    */
   public static String toGplString ( final Rgb[] arr, final String name ) {

      return Rgb.toGplString(arr, name, Utils.ceil(Utils.sqrt(arr.length)));
   }

   /**
    * Returns a String representing the color array in the GIMP palette file
    * format. The number of columns is for displaying the palette.
    *
    * @param arr  the array
    * @param name the palette name
    * @param cols the display columns
    *
    * @return the string
    */
   public static String toGplString ( final Rgb[] arr, final String name,
      final int cols ) {

      return Rgb.toGplString(arr, name, cols, false);
   }

   /**
    * Returns a String representing the color array in the GIMP palette file
    * format. The number of columns is for displaying the palette. A flag
    * indicates whether or not to append the color's index as the final column
    * of each row. Indices begin at 1, not 0.
    *
    * @param arr    the array
    * @param name   the palette name
    * @param cols   the display columns
    * @param useIdx append the index
    *
    * @return the string
    *
    * @see Rgb#toGplString(StringBuilder)
    * @see Rgb#toHexWeb(Rgb)
    */
   public static String toGplString ( final Rgb[] arr, final String name,
      final int cols, final boolean useIdx ) {

      final StringBuilder sb = new StringBuilder(1024);
      sb.append("GIMP Palette");
      sb.append("\nName: ");
      sb.append("id");
      sb.append(name);
      sb.append("\nColumns: ");
      sb.append(cols);
      sb.append("\n# https://github.com/behreajj/CamZup");

      final int len = arr.length;
      for ( int i = 0; i < len; ++i ) {
         final Rgb c = arr[i];
         sb.append('\n');
         c.toGplString(sb);
         sb.append(' ');
         sb.append(Rgb.toHexWeb(c).substring(1).toUpperCase());
         if ( useIdx ) {
            sb.append(' ');
            sb.append(i + 1);
         }
      }

      return sb.toString();
   }

   /**
    * Returns a representation of the color as a hexadecimal code, preceded by
    * a '0x', in the format AARRGGBB.
    *
    * @param c the color
    *
    * @return the string
    *
    * @see Rgb#toHexString(StringBuilder, Rgb)
    */
   public static String toHexString ( final int c ) {

      return Rgb.toHexString(new StringBuilder(10), c).toString();
   }

   /**
    * Returns a representation of the color as a hexadecimal code, preceded by
    * a '0x', in the format AARRGGBB.
    *
    * @param c the color
    *
    * @return the string
    *
    * @see Rgb#toHexString(StringBuilder, Rgb)
    */
   public static String toHexString ( final Rgb c ) {

      return Rgb.toHexString(new StringBuilder(10), c).toString();
   }

   /**
    * Returns a representation of the color as a hexadecimal code, preceded by
    * a '0x', in the format AARRGGBB. Appends to an existing
    * {@link StringBuilder}.
    *
    * @param sb the string builder
    * @param c  the color
    *
    * @return the string builder
    *
    * @see Rgb#toHexString(StringBuilder, int, int, int, int)
    */
   public static StringBuilder toHexString ( final StringBuilder sb,
      final int c ) {

      return Rgb.toHexString(sb, c >> 0x18 & 0xff, c >> 0x10 & 0xff, c >> 0x08
         & 0xff, c & 0xff);
   }

   /**
    * Returns a representation of the color as a hexadecimal code, preceded by
    * a '0x', in the format AARRGGBB. Appends to an existing
    * {@link StringBuilder}.
    *
    * @param sb the string builder
    * @param a  the alpha byte
    * @param r  the red byte
    * @param g  the green byte
    * @param b  the blue byte
    *
    * @return the string builder
    *
    * @see Utils#toHexDigit(StringBuilder, int)
    */
   public static StringBuilder toHexString ( final StringBuilder sb,
      final int a, final int r, final int g, final int b ) {

      sb.append("0x");
      Utils.toHexDigit(sb, a);
      Utils.toHexDigit(sb, r);
      Utils.toHexDigit(sb, g);
      Utils.toHexDigit(sb, b);
      return sb;
   }

   /**
    * Returns a representation of the color as a hexadecimal code, preceded by
    * a '0x', in the format AARRGGBB. Appends to an existing
    * {@link StringBuilder}.
    *
    * @param sb the string builder
    * @param c  the color
    *
    * @return the string builder
    *
    * @see Rgb#toHexString(StringBuilder, int, int, int, int)
    * @see Utils#clamp01(float)
    */
   public static StringBuilder toHexString ( final StringBuilder sb,
      final Rgb c ) {

      return Rgb.toHexString(sb, ( int ) ( Utils.clamp01(c.alpha) * 0xff
         + 0.5f ), ( int ) ( Utils.clamp01(c.r) * 0xff + 0.5f ), ( int ) ( Utils
            .clamp01(c.g) * 0xff + 0.5f ), ( int ) ( Utils.clamp01(c.b) * 0xff
               + 0.5f ));
   }

   /**
    * Returns a web-friendly representation of the color as a hexadecimal
    * code, preceded by a hash tag, '#', with no alpha. Assumes the number
    * will be formatted as <code>0xAARRGGBB</code> , where alpha is the first
    * channel, followed by red, green and blue.
    *
    * @param c the color
    *
    * @return the string
    *
    * @see Rgb#toHexWeb(StringBuilder, int)
    */
   public static String toHexWeb ( final int c ) {

      return Rgb.toHexWeb(new StringBuilder(7), c).toString();
   }

   /**
    * Creates a web-friendly representation of the color as a hexadecimal
    * code, preceded by a hash tag, '#', with no alpha.
    *
    * @param c the color
    *
    * @return the string
    *
    * @see Rgb#toHexWeb(StringBuilder, Rgb)
    */
   public static String toHexWeb ( final Rgb c ) {

      return Rgb.toHexWeb(new StringBuilder(7), c).toString();
   }

   /**
    * Creates a web-friendly representation of the color as a hexadecimal
    * code, preceded by a hash tag, '#', with no alpha. Appends to an existing
    * {@link StringBuilder}.<br>
    * <br>
    * Assumes the number will be formatted as <code>0xAARRGGBB</code> , where
    * alpha is the first channel, followed by red, green and blue.
    *
    * @param sb the string builder
    * @param c  the color
    *
    * @return the string builder
    *
    * @see Rgb#toHexWeb(StringBuilder, int, int, int)
    */
   public static StringBuilder toHexWeb ( final StringBuilder sb,
      final int c ) {

      return Rgb.toHexWeb(sb, c >> 0x10 & 0xff, c >> 0x08 & 0xff, c & 0xff);
   }

   /**
    * Creates a web-friendly representation of the color as a hexadecimal
    * code, preceded by a hash tag, '#', with no alpha. Appends to an existing
    * {@link StringBuilder}.
    *
    * @param sb the string builder
    * @param r  the red byte
    * @param g  the green byte
    * @param b  the blue byte
    *
    * @return the string builder
    *
    * @see Utils#toHexDigit(StringBuilder, int)
    */
   public static StringBuilder toHexWeb ( final StringBuilder sb, final int r,
      final int g, final int b ) {

      sb.append('#');
      Utils.toHexDigit(sb, r);
      Utils.toHexDigit(sb, g);
      Utils.toHexDigit(sb, b);
      return sb;
   }

   /**
    * Creates a web-friendly representation of the color as a hexadecimal
    * code, preceded by a hash tag, '#', with no alpha. Appends to an existing
    * {@link StringBuilder}.
    *
    * @param sb the string builder
    * @param c  color
    *
    * @return the string builder
    *
    * @see Utils#clamp01(float)
    * @see Rgb#toHexWeb(StringBuilder, int, int, int)
    */
   public static StringBuilder toHexWeb ( final StringBuilder sb,
      final Rgb c ) {

      /* @formatter:off */
      return Rgb.toHexWeb(sb,
         ( int ) ( Utils.clamp01(c.r) * 0xff + 0.5f ),
         ( int ) ( Utils.clamp01(c.g) * 0xff + 0.5f ),
         ( int ) ( Utils.clamp01(c.b) * 0xff + 0.5f ));
      /* @formatter:on */
   }

   /**
    * Returns a String representing the color array in the JASC-PAL palette
    * file format.
    *
    * @param arr the array
    *
    * @return the string
    *
    * @see Rgb#toGplString(StringBuilder)
    */
   public static String toPalString ( final Rgb[] arr ) {

      final int len = arr.length;
      final StringBuilder sb = new StringBuilder(1024);
      sb.append("JASC-PAL\n0100\n");
      sb.append(len);
      for ( int i = 0; i < len; ++i ) {
         sb.append('\n');
         arr[i].toGplString(sb);
      }

      return sb.toString();
   }

   /**
    * Returns a string representation of an array of Colors.
    *
    * @param arr the array
    *
    * @return the string
    */
   public static String toString ( final Rgb[] arr ) {

      return Rgb.toString(arr, IUtils.FIXED_PRINT);
   }

   /**
    * Returns a string representation of an array of colors.
    *
    * @param arr    the array
    * @param places the print precision
    *
    * @return the string
    */
   public static String toString ( final Rgb[] arr, final int places ) {

      final StringBuilder sb = new StringBuilder(1024);
      sb.append('[');

      if ( arr != null ) {
         final int len = arr.length;
         final int last = len - 1;

         for ( int i = 0; i < last; ++i ) {
            final Rgb c = arr[i];
            c.toString(sb, places);
            sb.append(',');
         }

         final Rgb cl = arr[last];
         cl.toString(sb, places);
      }

      sb.append(']');
      return sb.toString();
   }

   /**
    * Divides the red, green and blue color channels of a color by the alpha
    * channel; reverses {@link Rgb#premul(Rgb, Rgb)}.
    *
    * @param c      the input color
    * @param target the output color
    *
    * @return the divided color
    */
   public static final Rgb unpremul ( final Rgb c, final Rgb target ) {

      if ( c.alpha <= 0.0f ) { return target.set(0.0f, 0.0f, 0.0f, 0.0f); }
      if ( c.alpha >= 1.0f ) { return target.set(c.r, c.g, c.b, 1.0f); }
      final float aInv = 1.0f / c.alpha;
      return target.set(c.r * aInv, c.g * aInv, c.b * aInv, c.alpha);
   }

   /**
    * Returns the color white, ( 1.0, 1.0, 1.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return white
    */
   public static final Rgb white ( final Rgb target ) {

      return target.set(1.0f, 1.0f, 1.0f, 1.0f);
   }

   /**
    * Returns the color yellow, ( 1.0, 1.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return yellow
    */
   public static final Rgb yellow ( final Rgb target ) {

      return target.set(1.0f, 1.0f, 0.0f, 1.0f);
   }

   /**
    * Mixes two colors by a step in the range [0.0, 1.0] with linear
    * interpolation, then returns an integer.<br>
    * <br>
    * Internal helper function for {@link Gradient} so that a new color does
    * not need to be created as a target.
    *
    * @param orig   the origin color
    * @param dest   the destination color
    * @param step   the step
    * @param target the output color
    *
    * @return the mixed color
    *
    * @see Rgb#toHexIntWrap(Rgb)
    */
   static int mix ( final Rgb orig, final Rgb dest, final float step ) {

      if ( step <= 0.0f ) { return orig.toHexIntWrap(); }
      if ( step >= 1.0f ) { return dest.toHexIntWrap(); }
      final float u = 1.0f - step;

      /* @formatter:off */
      return ( int ) ( ( u * orig.alpha + step * dest.alpha ) * 0xff + 0.5f ) << 0x18
         | ( int ) ( ( u * orig.r + step * dest.r ) * 0xff + 0.5f ) << 0x10
         | ( int ) ( ( u * orig.g + step * dest.g ) * 0xff + 0.5f ) << 0x08
         | ( int ) ( ( u * orig.b + step * dest.b ) * 0xff + 0.5f );
      /* @formatter:on */
   }

   /**
    * An abstract class to facilitate the creation of RGB tone mapping
    * functions.
    */
   public abstract static class AbstrToneMap implements BiFunction < Rgb, Rgb,
      Rgb > {

      /**
       * The default constructor.
       */
      protected AbstrToneMap ( ) {}

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * Tone maps RGB channels using the ACES method. See
    * https://64.github.io/tonemapping/ .
    */
   public static final class ToneMapAces extends AbstrToneMap {

      /**
       * Stores conversion from gamma to linear.
       */
      protected final Rgb lrgb = new Rgb();

      /**
       * The default constructor.
       */
      public ToneMapAces ( ) {}

      /**
       * Applies a tone map.
       *
       * @param source the input color
       * @param target the output color
       *
       * @return the mapped color
       */
      @Override
      public Rgb apply ( final Rgb source, final Rgb target ) {

         Rgb.sRgbTolRgb(source, false, this.lrgb);

         final float rFrwrd = 0.59719f * this.lrgb.r + 0.35458f * this.lrgb.g
            + 0.04823f * this.lrgb.b;
         final float gFrwrd = 0.076f * this.lrgb.r + 0.90834f * this.lrgb.g
            + 0.01566f * this.lrgb.b;
         final float bFrwrd = 0.0284f * this.lrgb.r + 0.13383f * this.lrgb.g
            + 0.83777f * this.lrgb.b;

         final float ar = rFrwrd * ( rFrwrd + 0.0245786f ) - 0.000090537f;
         final float ag = gFrwrd * ( gFrwrd + 0.0245786f ) - 0.000090537f;
         final float ab = bFrwrd * ( bFrwrd + 0.0245786f ) - 0.000090537f;

         final float br = rFrwrd * ( 0.983729f * rFrwrd + 0.432951f )
            + 0.238081f;
         final float bg = gFrwrd * ( 0.983729f * gFrwrd + 0.432951f )
            + 0.238081f;
         final float bb = bFrwrd * ( 0.983729f * bFrwrd + 0.432951f )
            + 0.238081f;

         final float cr = Utils.div(ar, br);
         final float cg = Utils.div(ag, bg);
         final float cb = Utils.div(ab, bb);

         final float rBckwd = 1.60475f * cr - 0.53108f * cg - 0.07367f * cb;
         final float gBckwd = -0.10208f * cr + 1.10813f * cg - 0.00605f * cb;
         final float bBckwd = -0.00327f * cr - 0.07276f * cg + 1.07602f * cb;

         target.set(rBckwd, gBckwd, bBckwd, this.lrgb.alpha);
         return Rgb.lRgbTosRgb(Rgb.clamp01(target, target), false, target);
      }

   }

   /**
    * Clamps the RGB channels to the range [0.0, 1.0].
    */
   public static final class ToneMapClamp extends AbstrToneMap {

      /**
       * The default constructor.
       */
      public ToneMapClamp ( ) {}

      /**
       * Applies a tone map.
       *
       * @param source the input color
       * @param target the output color
       *
       * @return the mapped color
       */
      @Override
      public Rgb apply ( final Rgb source, final Rgb target ) {

         return Rgb.clamp01(source, target);
      }

   }

   /**
    * Tone maps RGB channels using the Hable method. See
    * https://64.github.io/tonemapping/ .
    */
   public static final class ToneMapHable extends AbstrToneMap {

      /**
       * Stores conversion from gamma to linear.
       */
      protected final Rgb lrgb = new Rgb();

      /**
       * The default constructor.
       */
      public ToneMapHable ( ) {}

      /**
       * Applies a tone map.
       *
       * @param source the input color
       * @param target the output color
       *
       * @return the mapped color
       */
      @Override
      public Rgb apply ( final Rgb source, final Rgb target ) {

         Rgb.sRgbTolRgb(source, false, this.lrgb);

         final float er = this.lrgb.r * ToneMapHable.exposureBias;
         final float eg = this.lrgb.g * ToneMapHable.exposureBias;
         final float eb = this.lrgb.b * ToneMapHable.exposureBias;

         final float xr = ToneMapHable.whiteScale * ( ( er * ( ToneMapHable.A
            * er + ToneMapHable.C * ToneMapHable.B ) + ToneMapHable.D
               * ToneMapHable.E ) / ( er * ( ToneMapHable.A * er
                  + ToneMapHable.B ) + ToneMapHable.D * ToneMapHable.F )
            - ToneMapHable.E / ToneMapHable.F );
         final float xg = ToneMapHable.whiteScale * ( ( eg * ( ToneMapHable.A
            * eg + ToneMapHable.C * ToneMapHable.B ) + ToneMapHable.D
               * ToneMapHable.E ) / ( eg * ( ToneMapHable.A * eg
                  + ToneMapHable.B ) + ToneMapHable.D * ToneMapHable.F )
            - ToneMapHable.E / ToneMapHable.F );
         final float xb = ToneMapHable.whiteScale * ( ( eb * ( ToneMapHable.A
            * eb + ToneMapHable.C * ToneMapHable.B ) + ToneMapHable.D
               * ToneMapHable.E ) / ( eb * ( ToneMapHable.A * eb
                  + ToneMapHable.B ) + ToneMapHable.D * ToneMapHable.F )
            - ToneMapHable.E / ToneMapHable.F );
         target.set(xr, xg, xb, this.lrgb.alpha);

         return Rgb.lRgbTosRgb(Rgb.clamp01(target, target), false, target);
      }

      /**
       * Constant A.
       */
      public static final float A = 0.15f;

      /**
       * Constant B.
       */
      public static final float B = 0.50f;

      /**
       * Constant C.
       */
      public static final float C = 0.10f;

      /**
       * Constant D.
       */
      public static final float D = 0.20f;

      /**
       * Constant E.
       */
      public static final float E = 0.02f;

      /**
       * Exposure bias.
       */
      public static final float exposureBias = 2.0f;

      /**
       * Constant F.
       */
      public static final float F = 0.30f;

      /**
       * Constant W.
       */
      public static final float W = 11.2f;

      /**
       * White scale.
       */
      public static final float whiteScale = 1.0f / ( ( ToneMapHable.W
         * ( ToneMapHable.A * ToneMapHable.W + ToneMapHable.C * ToneMapHable.B )
         + ToneMapHable.D * ToneMapHable.E ) / ( ToneMapHable.W
            * ( ToneMapHable.A * ToneMapHable.W + ToneMapHable.B )
            + ToneMapHable.D * ToneMapHable.F ) - ToneMapHable.E
               / ToneMapHable.F );

   }

}
