package camzup.core;

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
   public int toHexInt ( ) {

      return this.toHexIntWrap();
   }

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
    * Converts an array of integers that represent colors in hexadecimal into
    * an array of colors
    *
    * @param hexes the colors
    *
    * @return the array
    *
    * @see Rgb#fromHex(int, Rgb)
    */
   public static Rgb[] fromHex ( final int[] hexes ) {

      final int len = hexes.length;
      final Rgb[] result = new Rgb[len];
      for ( int i = 0; i < len; ++i ) {
         result[i] = Rgb.fromHex(hexes[i], new Rgb());
      }
      return result;
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
    * Returns the relative luminance of the linear RGB color, based on
    * <a href="https://www.wikiwand.com/en/Rec._709#/Luma_coefficients"> Rec.
    * 709 relative luminance</a>.<br>
    * <br>
    * Due to single precision, this may not yield the same result as
    * {@link Rgb#lRgbLuminance(Rgb)} .
    *
    * @param c the input color
    *
    * @return the luminance
    */
   public static float lRgbLuminance ( final int c ) {

      /*
       * Coefficients: 0.21264935 / 255.0, 0.71516913 / 255.0, 0.07218152 /
       * 255.0, 0.000833919019607843, 0.002804584823529412,
       * 0.0002830647843137255
       */

      /* @formatter:off */
      return (( c >> 0x10 & 0xff ) *  83391901.0f +
              ( c >> 0x08 & 0xff ) * 280458482.0f +
              ( c         & 0xff ) *  28306478.0f) * 10E-12f;
      /* @formatter:on */
   }

   /**
    * Returns the relative luminance of the linear RGB color, based on
    * <a href="https://www.wikiwand.com/en/Rec._709#/Luma_coefficients"> Rec.
    * 709 relative luminance</a> coefficients: <code>0.2126</code> for red,
    * <code>0.7152</code> for green and <code>0.0722</code> for blue.
    *
    * @param c the input color
    *
    * @return the luminance
    */
   public static float lRgbLuminance ( final Rgb c ) {

      return 0.21264935f * c.r + 0.71516913f * c.g + 0.07218152f * c.b;
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
    * Returns the relative luminance of a color; assumes the color is in sRGB.
    *
    * @param c the color
    *
    * @return the luminance
    *
    * @see Rgb#sRgbLuminance(Rgb)
    */
   public static float luminance ( final Rgb c ) {

      return Rgb.sRgbLuminance(c);
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
    * Convert a color to gray-scale based on its perceived luminance.
    *
    * @param c      the input color
    * @param target the output color
    *
    * @return the gray scale color
    *
    * @see Rgb#sRgbLuminance(Rgb)
    * @see Math#pow(double, double)
    */
   public static Rgb rgbToGray ( final Rgb c, final Rgb target ) {

      final float lum = Rgb.sRgbLuminance(c);
      final float vf = lum <= 0.0031308f ? lum * 12.92f : ( float ) ( Math.pow(
         lum, 0.4166666666666667d) * 1.055d - 0.055d );
      return target.set(vf, vf, vf, c.alpha);
   }

   /**
    * Returns the relative luminance of the standard RGB color, based on
    * <a href="https://www.wikiwand.com/en/Rec._709#/Luma_coefficients">Rec.
    * 709 relative luminance</a> coefficients.
    *
    * @param c the input color
    *
    * @return the luminance
    *
    * @see Math#pow(double, double)
    */
   public static float sRgbLuminance ( final Rgb c ) {

      final double lr = c.r <= 0.04045f ? c.r * 0.07739938080495357d : Math.pow(
         ( c.r + 0.055d ) * 0.9478672985781991d, 2.4d);
      final double lg = c.g <= 0.04045f ? c.g * 0.07739938080495357d : Math.pow(
         ( c.g + 0.055d ) * 0.9478672985781991d, 2.4d);
      final double lb = c.b <= 0.04045f ? c.b * 0.07739938080495357d : Math.pow(
         ( c.b + 0.055d ) * 0.9478672985781991d, 2.4d);

      return ( float ) ( 0.21264934272065283d * lr + 0.7151691357059038d * lg
         + 0.07218152157344333d * lb );
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
    * Converts a color from standard RGB (sRGB) to SR LCH.
    *
    * @param srgb the sRGB color
    * @param lch  the LCH color
    * @param lab  the LAB color
    * @param xyz  the XYZ color
    * @param lrgb linear sRGB color
    *
    * @return the output color
    *
    * @see Lch#fromLab(Lab, Lch)
    * @see Rgb#sRgbToSrLab2(Rgb, Lab, Vec4, Rgb)
    */
   public static final Lch sRgbToSrLch ( final Rgb srgb, final Lch lch,
      final Lab lab, final Vec4 xyz, final Rgb lrgb ) {

      return Lch.fromLab(Rgb.sRgbToSrLab2(srgb, lab, xyz, lrgb), lch);
   }

   /**
    * Converts an array of colors from standard RGB (sRGB) to SR LCH.
    *
    * @param source the sRGB color array
    * @param target the LCH color array
    * @param lab    the LAB color
    * @param xyz    the XYZ color
    * @param lrgb   the linear sRGB color
    *
    * @return the output color
    *
    * @see Rgb#sRgbToSrLch(Rgb, Lch, Lab, Vec4, Rgb)
    */
   public static final Lch[] sRgbToSrLch ( final Rgb[] source,
      final Lch[] target, final Lab lab, final Vec4 xyz, final Rgb lrgb ) {

      final int srcLen = source.length;
      for ( int i = 0; i < srcLen; ++i ) {
         target[i] = Rgb.sRgbToSrLch(source[i], new Lch(), lab, xyz, lrgb);
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
    * Converts a color from SR LCH to standard RBG (sRGB).
    *
    * @param lch  the LCH color
    * @param srgb the sRGB color
    * @param lrgb the linear sRGB color
    * @param xyz  the XYZ color
    * @param lab  the LAB color
    *
    * @return the output color
    *
    * @see Rgb#srLab2TosRgb(Lab, Rgb, Rgb, Vec4)
    * @see Lab#fromLch(Lch, Lab)
    */
   public static final Rgb srLchTosRgb ( final Lch lch, final Rgb srgb,
      final Rgb lrgb, final Vec4 xyz, final Lab lab ) {

      return Rgb.srLab2TosRgb(Lab.fromLch(lch, lab), srgb, lrgb, xyz);
   }

   /**
    * Converts an array of colors from SR LCH to standard RBG (sRGB).
    *
    * @param source the LCH color array
    * @param target the sRGB color array
    * @param lrgb   the linear sRGB color
    * @param xyz    the XYZ color
    * @param lab    the LAB color
    *
    * @return the output color
    *
    * @see Rgb#srLchTosRgb(Lch, Rgb, Rgb, Vec4, Lab)
    */
   public static final Rgb[] srLchTosRgb ( final Lch[] source,
      final Rgb[] target, final Rgb lrgb, final Vec4 xyz, final Lab lab ) {

      final int srcLen = source.length;
      for ( int i = 0; i < srcLen; ++i ) {
         target[i] = Rgb.srLchTosRgb(source[i], new Rgb(), lrgb, xyz, lab);
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
      if ( Character.isDigit(name.charAt(0)) ) { sb.append("id"); }
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
    * Mixes two colors by a step in the range [0.0, 1.0] with linear
    * interpolation in standard RGB.
    *
    * @param orig   the origin color
    * @param dest   the destination color
    * @param step   the step
    * @param target the output color
    *
    * @return the mixed color
    */
   static Rgb mix ( final Rgb orig, final Rgb dest, final float step,
      final Rgb target ) {

      if ( step <= 0.0f ) { return target.set(orig); }
      if ( step >= 1.0f ) { return target.set(dest); }

      final float u = 1.0f - step;
      return target.set(u * orig.r + step * dest.r, u * orig.g + step * dest.g,
         u * orig.b + step * dest.b, u * orig.alpha + step * dest.alpha);
   }

   /**
    * An abstract class to facilitate the creation of color easing functions.
    */
   public abstract static class AbstrEasing implements Utils.EasingFuncObj <
      Rgb > {

      /**
       * The default constructor.
       */
      protected AbstrEasing ( ) {

         // TODO: Delete.
      }

      /**
       * A clamped interpolation between the origin and destination. Defers to
       * an unclamped interpolation, which is to be defined by sub-classes of
       * this class.
       *
       * @param orig   the origin color
       * @param dest   the destination color
       * @param step   a factor in [0.0, 1.0]
       * @param target the output color
       *
       * @return the eased color
       */
      @Override
      public Rgb apply ( final Rgb orig, final Rgb dest, final Float step,
         final Rgb target ) {

         final float tf = step;
         if ( Float.isNaN(tf) ) {
            return this.applyUnclamped(orig, dest, 0.5f, target);
         }
         if ( tf <= 0.0f ) { return target.set(orig); }
         if ( tf >= 1.0f ) { return target.set(dest); }
         return this.applyUnclamped(orig, dest, step, target);
      }

      /**
       * The interpolation to be defined by subclasses.
       *
       * @param orig   the origin color
       * @param dest   the destination color
       * @param step   a factor in [0, 1]
       * @param target the output color
       *
       * @return the eased color
       */
      public abstract Rgb applyUnclamped ( final Rgb orig, final Rgb dest,
         final Float step, final Rgb target );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * Converts two colors from
    * <a href="https://www.wikiwand.com/en/SRGB">standard RGB</a> to linear
    * RGB, mixes them with linear interpolation, then converts back to
    * standard RGB.
    */
   public static final class MixLrgb extends AbstrEasing {

      /**
       * Whether or not to include the alpha in the adjustment.
       */
      final boolean inclAlpha;

      /**
       * The mixed color in linear RGB.
       */
      protected final Rgb cLinear = new Rgb();

      /**
       * The destination color in linear RGB.
       */
      protected final Rgb dLinear = new Rgb();

      /**
       * The origin color in linear RGB.
       */
      protected final Rgb oLinear = new Rgb();

      /**
       * Construct a new linear color mixer. Defaults to not including alpha, or
       * transparency, in the adjustment.
       */
      public MixLrgb ( ) { this(false); }

      /**
       * Construct a new linear color mixer. The flag specifies whether or not
       * alpha should be included in the adjustment.
       *
       * @param useAlpha flag to adjust alpha
       */
      public MixLrgb ( final boolean useAlpha ) {

         // TODO: Delete.
         this.inclAlpha = useAlpha;
      }

      /**
       * Applies the function.
       *
       * @param orig   the origin color
       * @param dest   the destination color
       * @param step   the step in a range 0 to 1
       * @param target the output color
       *
       * @return the eased color
       *
       * @see Rgb#sRgbTolRgb(Rgb, boolean, Rgb)
       * @see Rgb#lRgbTosRgb(Rgb, boolean, Rgb)
       */
      @Override
      public Rgb applyUnclamped ( final Rgb orig, final Rgb dest,
         final Float step, final Rgb target ) {

         Rgb.sRgbTolRgb(orig, this.inclAlpha, this.oLinear);
         Rgb.sRgbTolRgb(dest, this.inclAlpha, this.dLinear);

         final float t = step;
         final float u = 1.0f - t;
         this.cLinear.set(u * this.oLinear.r + t * this.dLinear.r, u
            * this.oLinear.g + t * this.dLinear.g, u * this.oLinear.b + t
               * this.dLinear.b, u * this.oLinear.alpha + t
                  * this.dLinear.alpha);

         return Rgb.lRgbTosRgb(this.cLinear, this.inclAlpha, target);
      }

   }

   /**
    * Eases between two colors in sRGB, i.e., with no gamma correction.
    */
   public static final class MixSrgb extends AbstrEasing {

      /**
       * The default constructor.
       */
      public MixSrgb ( ) {

         // TODO: Delete
      }

      /**
       * Applies the function.
       *
       * @param orig   the origin color
       * @param dest   the destination color
       * @param step   the step in a range 0 to 1
       * @param target the output color
       *
       * @return the eased color
       */
      @Override
      public Rgb applyUnclamped ( final Rgb orig, final Rgb dest,
         final Float step, final Rgb target ) {

         final float t = step;
         final float u = 1.0f - t;
         return target.set(u * orig.r + t * dest.r, u * orig.g + t * dest.g, u
            * orig.b + t * dest.b, u * orig.alpha + t * dest.alpha);
      }

   }

   /**
    * Eases between two colors in SR LAB 2 color space. May return colors
    * outside the range [0.0, 1.0] .
    */
   public static class MixSrLab2 extends AbstrEasing {

      /**
       * The mixed color in LAB.
       */
      protected final Lab cLab = new Lab();

      /**
       * The mixed color in linear RGB.
       */
      protected final Rgb cLinear = new Rgb();

      /**
       * The mixed color in XYZ.
       */
      protected final Vec4 cXyz = new Vec4();

      /**
       * The destination color in LAB.
       */
      protected final Lab dLab = new Lab();

      /**
       * The destination color in linear RGB.
       */
      protected final Rgb dLinear = new Rgb();

      /**
       * The destination color in XYZ.
       */
      protected final Vec4 dXyz = new Vec4();

      /**
       * The origin color in LAB.
       */
      protected final Lab oLab = new Lab();

      /**
       * The origin color in linear RGB.
       */
      protected final Rgb oLinear = new Rgb();

      /**
       * The origin color in XYZ.
       */
      protected final Vec4 oXyz = new Vec4();

      /**
       * The default constructor.
       */
      public MixSrLab2 ( ) {

         // TODO: Delete.
      }

      /**
       * Applies the function.
       *
       * @param orig   the origin color
       * @param dest   the destination color
       * @param step   the step in a range 0 to 1
       * @param target the output color
       *
       * @return the eased color
       *
       * @see Rgb#sRgbToSrLab2(Rgb, Lab, Vec4, Rgb)
       * @see Rgb#srLab2TosRgb(Lab, Rgb, Rgb, Vec4)
       * @see Vec4#mix(Vec4, Vec4, float, Vec4)
       */
      @Override
      public Rgb applyUnclamped ( final Rgb orig, final Rgb dest,
         final Float step, final Rgb target ) {

         Rgb.sRgbToSrLab2(orig, this.oLab, this.oXyz, this.oLinear);
         Rgb.sRgbToSrLab2(dest, this.dLab, this.dXyz, this.dLinear);
         Lab.mix(this.oLab, this.dLab, step, this.cLab);
         Rgb.srLab2TosRgb(this.cLab, target, this.cLinear, this.cXyz);

         return target;
      }

   }

   /**
    * Eases between two colors in SR LCH color space. May return colors
    * outside the range [0.0, 1.0] .
    */
   public static final class MixSrLch extends MixSrLab2 {

      /**
       * The new LCH color.
       */
      protected final Lch cLch = new Lch();

      /**
       * The destination color in LCH.
       */
      protected final Lch dLch = new Lch();

      /**
       * The hue easing function.
       */
      protected IColor.HueEasing hueFunc;

      /**
       * The origin color in LCH.
       */
      protected final Lch oLch = new Lch();

      /**
       * The default constructor. Creates a mixer with nearest hue
       * interpolation.
       */
      public MixSrLch ( ) { this(new IColor.HueNear()); }

      /**
       * Creates a color SR LCH mixing function with the given easing functions
       * for hue.
       *
       * @param hueFunc the hue easing function
       */
      public MixSrLch ( final IColor.HueEasing hueFunc ) {

         // TODO: Delete.
         this.hueFunc = hueFunc;
      }

      /**
       * Applies the function.
       *
       * @param orig   the origin color
       * @param dest   the destination color
       * @param step   the step in a range 0 to 1
       * @param target the output color
       *
       * @return the eased color
       *
       * @see Rgb#sRgbToSrLch(Rgb, Lch, Lab, Vec4, Rgb)
       * @see Rgb#srLchTosRgb(Lch, Rgb, Rgb, Vec4, Lab)
       */
      @Override
      public Rgb applyUnclamped ( final Rgb orig, final Rgb dest,
         final Float step, final Rgb target ) {

         Rgb.sRgbToSrLch(orig, this.oLch, this.oLab, this.oXyz, this.oLinear);
         Rgb.sRgbToSrLch(dest, this.dLch, this.dLab, this.dXyz, this.dLinear);
         Lch.mix(this.oLch, this.dLch, step, this.hueFunc, this.cLch);
         Rgb.srLchTosRgb(this.cLch, target, this.cLinear, this.cXyz, this.cLab);

         return target;
      }

   }

}
