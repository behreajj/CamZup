package camzup.core;

import java.util.regex.Pattern;

/**
 * A mutable, extensible color class. Assumes red, green and blue color
 * channels are in the standard RGB (sRGB) color space. Converts to and
 * from integers where color channels are in the format 0xAARRGGBB.
 */
public class Color implements Comparable < Color > {

   /**
    * The alpha channel, which governs transparency.
    */
   public float a = 1.0f;

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
   public Color ( ) {}

   /**
    * Creates a color from bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    */
   public Color ( final byte red, final byte green, final byte blue ) {

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
   public Color ( final byte red, final byte green, final byte blue,
      final byte alpha ) {

      this.set(red, green, blue, alpha);
   }

   /**
    * Creates a color from a source.
    *
    * @param c the source color
    */
   public Color ( final Color c ) { this.set(c); }

   /**
    * Creates a color out of red, green and blue channels. The alpha channel
    * defaults to 1.0 .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    */
   public Color ( final float red, final float green, final float blue ) {

      this.set(red, green, blue, 1.0f);
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
   public Color ( final float red, final float green, final float blue,
      final float alpha ) {

      this.set(red, green, blue, alpha);
   }

   /**
    * Returns -1 when this color is less than the comparisand; 1 when it is
    * greater than; 0 when the two are 'equal'. The implementation of this
    * method allows collections of colors to be sorted.
    *
    * @param c the comparisand
    *
    * @return the numeric code
    *
    * @see Color#toHexIntWrap(Color)
    */
   @Override
   public int compareTo ( final Color c ) {

      final int left = Color.toHexIntWrap(this);
      final int right = Color.toHexIntWrap(c);
      return left < right ? -1 : left > right ? 1 : 0;
   }

   /**
    * Tests this color for equivalence to another based on its hexadecimal
    * representation.
    *
    * @param other the color integer
    *
    * @return the equivalence
    *
    * @see Color#toHexIntWrap(Color)
    */
   public boolean equals ( final int other ) {

      return Color.toHexIntWrap(this) == other;
   }

   /**
    * Tests this color for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    *
    * @see Color#equals(Color)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Color ) obj);
   }

   /**
    * Returns a hash code for this color based on its hexadecimal value.
    *
    * @return the hash code
    *
    * @see Color#toHexIntWrap(Color)
    */
   @Override
   public int hashCode ( ) { return Color.toHexIntWrap(this); }

   /**
    * Resets this color to opaque white.
    *
    * @return this color
    */
   public Color reset ( ) { return this.set(1.0f, 1.0f, 1.0f, 1.0f); }

   /**
    * Sets a color with bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    *
    * @return this color
    */
   public Color set ( final byte red, final byte green, final byte blue ) {

      return this.set(IUtils.ONE_255 * ( red & 0xff ), IUtils.ONE_255 * ( green
         & 0xff ), IUtils.ONE_255 * ( blue & 0xff ), 1.0f);
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
   public Color set ( final byte red, final byte green, final byte blue,
      final byte alpha ) {

      return this.set(IUtils.ONE_255 * ( red & 0xff ), IUtils.ONE_255 * ( green
         & 0xff ), IUtils.ONE_255 * ( blue & 0xff ), IUtils.ONE_255 * ( alpha
            & 0xff ));
   }

   /**
    * Sets this color to the source color.
    *
    * @param c the source color
    *
    * @return this color
    */
   public Color set ( final Color c ) {

      return this.set(c.r, c.g, c.b, c.a);
   }

   /**
    * Sets the red, green and blue color channels of this color. The alpha
    * channel is set to 1.0 by default.
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    *
    * @return this color
    */
   public Color set ( final float red, final float green, final float blue ) {

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
   public Color set ( final float red, final float green, final float blue,
      final float alpha ) {

      this.r = red;
      this.g = green;
      this.b = blue;
      this.a = alpha;
      return this;
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
    * An internal helper function to format a vector as a Python tuple, then
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
         Utils.toFixed(pyCd, Utils.clamp01(this.a), 6);
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
      Utils.toFixed(ggr, Utils.clamp01(this.a), 6);
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

      sb.append("{ r: ");
      Utils.toFixed(sb, this.r, places);
      sb.append(", g: ");
      Utils.toFixed(sb, this.g, places);
      sb.append(", b: ");
      Utils.toFixed(sb, this.b, places);
      sb.append(", a: ");
      Utils.toFixed(sb, this.a, places);
      sb.append(' ');
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
    * @see Color#eqSatArith(Color, Color)
    */
   protected boolean equals ( final Color c ) {

      return Color.eqSatArith(this, c);
   }

   /**
    * The maximum value on the green-magenta axis in SR LAB 2 for a color
    * converted from standard RGB.
    */
   public static final float A_MAX = 104.49946f;

   /**
    * The minimum value on the green-magenta axis in SR LAB 2 for a color
    * converted from standard RGB.
    */
   public static final float A_MIN = -82.955986f;

   /**
    * The maximum value on the blue-yellow axis in SR LAB 2 for a color
    * converted from standard RGB.
    */
   public static final float B_MAX = 95.18662f;

   /**
    * The minimum value on the blue-yellow axis in SR LAB 2 for a color
    * converted from standard RGB.
    */
   public static final float B_MIN = -110.8078f;

   /**
    * The smallest non-zero chroma in SR LCH for a color converted from
    * standard RGB.
    */
   public static final float C_EPSILON = 0.27146554f;

   /**
    * The maximum chroma in SR LCH for a color converted from standard RGB.
    */
   public static final float C_MAX = 119.431305f;

   /**
    * Arbitrary hue in SR LCH assigned to colors with no saturation that are
    * closer to light, {@value Color#SR_LCH_HUE_LIGHT}. Defaults to a yellow.
    */
   public static final float SR_LCH_HUE_LIGHT = 0.306391f;

   /**
    * Arbitrary hue in SR LCH assigned to colors with no saturation that are
    * closer to shadow, {@value Color#SR_LCH_HUE_SHADOW}. Defaults to a
    * violet.
    */
   public static final float SR_LCH_HUE_SHADOW = 0.874676f;

   /**
    * Tests to see if all color channels are greater than zero.
    *
    * @param c the color
    *
    * @return the evaluation
    */
   public static boolean all ( final Color c ) {

      return c.a > 0.0f && c.r > 0.0f && c.g > 0.0f && c.b > 0.0f;
   }

   /**
    * Tests to see if the alpha channel of the color is greater than zero,
    * i.e. if it has some opacity.
    *
    * @param c the color
    *
    * @return the evaluation
    */
   public static boolean any ( final Color c ) { return c.a > 0.0f; }

   /**
    * Returns the color black, ( 0.0, 0.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return black
    */
   public static Color black ( final Color target ) {

      return target.set(0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Returns the color blue, ( 0.0, 0.0, 1.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return blue
    */
   public static Color blue ( final Color target ) {

      return target.set(0.0f, 0.0f, 1.0f, 1.0f);
   }

   /**
    * Clamps all color channels to the range [0.0, 1.0] .
    *
    * @param a      the color
    * @param target the output color
    *
    * @return the clamped color
    *
    * @see Utils#clamp01(float)
    */
   public static Color clamp01 ( final Color a, final Color target ) {

      return target.set(Utils.clamp01(a.r), Utils.clamp01(a.g), Utils.clamp01(
         a.b), Utils.clamp01(a.a));
   }

   /**
    * Returns the color clear black, ( 0.0, 0.0, 0.0, 0.0 ) .
    *
    * @param target the output color
    *
    * @return clear black
    */
   public static Color clearBlack ( final Color target ) {

      return target.set(0.0f, 0.0f, 0.0f, 0.0f);
   }

   /**
    * Returns the color clear white, ( 1.0, 1.0, 1.0, 0.0 ) .
    *
    * @param target the output color
    *
    * @return clear white
    */
   public static Color clearWhite ( final Color target ) {

      return target.set(1.0f, 1.0f, 1.0f, 0.0f);
   }

   /**
    * Returns the color cyan, ( 0.0, 1.0, 1.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return cyan
    */
   public static Color cyan ( final Color target ) {

      return target.set(0.0f, 1.0f, 1.0f, 1.0f);
   }

   /**
    * Checks if two colors have equivalent alpha channels when converted to
    * bytes in [0, 255]. Uses saturation arithmetic.
    *
    * @param a the left comparisand
    * @param b the right comparisand
    *
    * @return the equivalence
    *
    * @see Utils#clamp01(float)
    */
   public static boolean eqAlphaSatArith ( final Color a, final Color b ) {

      return ( int ) ( Utils.clamp01(a.a) * 0xff + 0.5f ) == ( int ) ( Utils
         .clamp01(b.a) * 0xff + 0.5f );
   }

   /**
    * Checks if two colors have equivalent red, green and blue channels when
    * converted to bytes in [0, 255]. Uses saturation arithmetic.
    *
    * @param a the left comparisand
    * @param b the right comparisand
    *
    * @return the equivalence
    *
    * @see Utils#clamp01(float)
    */
   public static boolean eqRgbSatArith ( final Color a, final Color b ) {

      /* @formatter:off */
      return ( int ) ( Utils.clamp01(a.b) * 0xff + 0.5f )
          == ( int ) ( Utils.clamp01(b.b) * 0xff + 0.5f )
          && ( int ) ( Utils.clamp01(a.g) * 0xff + 0.5f )
          == ( int ) ( Utils.clamp01(b.g) * 0xff + 0.5f )
          && ( int ) ( Utils.clamp01(a.r) * 0xff + 0.5f )
          == ( int ) ( Utils.clamp01(b.r) * 0xff + 0.5f );
      /* @formatter:on */
   }

   /**
    * Checks if two colors have equivalent red, green, blue and alpha channels
    * when converted to bytes in [0, 255]. Uses saturation arithmetic.
    *
    * @param a the left comparisand
    * @param b the right comparisand
    *
    * @return the equivalence
    *
    * @see Color#eqAlphaSatArith(Color, Color)
    * @see Color#eqRgbSatArith(Color, Color)
    */
   public static boolean eqSatArith ( final Color a, final Color b ) {

      return Color.eqAlphaSatArith(a, b) && Color.eqRgbSatArith(a, b);
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
   public static Color fromDir ( final Vec2 v, final Color target ) {

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
   public static Color fromDir ( final Vec3 v, final Color target ) {

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
    * @param c      the color in hexadecimal
    * @param target the output color
    *
    * @return the color
    */
   public static Color fromHex ( final int c, final Color target ) {

      /* @formatter:off */
      return target.set(
         ( c >> 0x10 & 0xff ) * IUtils.ONE_255,
         ( c >> 0x08 & 0xff ) * IUtils.ONE_255,
         ( c         & 0xff ) * IUtils.ONE_255,
         ( c >> 0x18 & 0xff ) * IUtils.ONE_255);
      /* @formatter:on */
   }

   /**
    * Converts an array of integers that represent colors in hexadecimal into
    * an array of colors
    *
    * @param cs the colors
    *
    * @return the array
    *
    * @see Color#fromHex(int, Color)
    */
   public static Color[] fromHex ( final int[] cs ) {

      final int len = cs.length;
      final Color[] result = new Color[len];
      for ( int i = 0; i < len; ++i ) {
         result[i] = Color.fromHex(cs[i], new Color());
      }
      return result;
   }

   /**
    * Convert a hexadecimal representation of a color stored as 0xAARRGGBB
    * into a color.
    *
    * @param c      the color in hexadecimal
    * @param target the output color
    *
    * @return the color
    */
   public static Color fromHex ( final long c, final Color target ) {

      /* @formatter:off */
      return target.set(
         ( c >> 0x10 & 0xffL ) * IUtils.ONE_255,
         ( c >> 0x08 & 0xffL ) * IUtils.ONE_255,
         ( c         & 0xffL ) * IUtils.ONE_255,
         ( c >> 0x18 & 0xffL ) * IUtils.ONE_255);
      /* @formatter:on */
   }

   /**
    * Attempts to convert a hexadecimal String to a color. Recognized formats
    * include:
    * <ul>
    * <li>"abc" - RGB, one digit per channel.</li>
    * <li>"#abc" - hash tag, RGB, one digit per channel.</li>
    * <li>"aabbcc" - RRGGBB, two digits per channel.
    * <li>"#aabbcc" - hash tag, RRGGBB, two digits per channel.</li>
    * <li>"aabbccdd" - AARRGGBB, two digits per channel.</li>
    * <li>"0xaabbccdd" - '0x' prefix, AARRGGBB, two digits per channel.</li>
    * </ul>
    * The output color will be reset if no suitable format is recognized.
    *
    * @param c      the input String
    * @param target the output color
    *
    * @return the color
    *
    * @see Color#fromHex(int, Color)
    * @see Integer#parseInt(String, int)
    * @see Long#parseLong(String, int)
    * @see String#replaceAll(String, String)
    * @see String#substring(int)
    */
   public static Color fromHex ( final String c, final Color target ) {

      try {

         final int len = c.length();
         switch ( len ) {

            case 3:

               /* Example: "abc" */
               return Color.fromHex(0xff000000 | Integer.parseInt(Pattern
                  .compile("^(.)(.)(.)$").matcher(c).replaceAll("$1$1$2$2$3$3"),
                  16), target);

            case 4:

               /* Example: "#abc" */
               return Color.fromHex(0xff000000 | Integer.parseInt(Pattern
                  .compile("^#(.)(.)(.)$").matcher(c).replaceAll(
                     "#$1$1$2$2$3$3").substring(1), 16), target);

            case 6:

               /* Example: "aabbcc" */
               return Color.fromHex(0xff000000 | Integer.parseInt(c, 16),
                  target);

            case 7:

               /* Example: "#aabbcc" */
               return Color.fromHex(0xff000000 | Integer.parseInt(c.substring(
                  1), 16), target);

            case 8:

               /* Example: "aabbccdd" */
               return Color.fromHex(Long.parseLong(c, 16), target);

            case 10:

               /* Example: "0xaabbccdd" */
               return Color.fromHex(Long.parseLong(c.substring(2), 16), target);

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
   public static Color green ( final Color target ) {

      return target.set(0.0f, 1.0f, 0.0f, 1.0f);
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
   public static float lRgbLuminance ( final Color c ) {

      return 0.21264935f * c.r + 0.71516913f * c.g + 0.07218152f * c.b;
   }

   /**
    * Returns the relative luminance of the linear RGB color, based on
    * <a href="https://www.wikiwand.com/en/Rec._709#/Luma_coefficients"> Rec.
    * 709 relative luminance</a>.<br>
    * <br>
    * Due to single precision, this may not yield the same result as
    * {@link Color#lRgbLuminance(Color)} .
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
    * Converts a color from linear RGB to standard RGB (sRGB).
    *
    * @param source the linear color
    * @param alpha  adjust the alpha channel
    * @param target the output color
    *
    * @return the standard color
    */
   public static Color lRgbTosRgb ( final Color source, final boolean alpha,
      final Color target ) {

      /* pow(x, y) := exp(y * ln(x)) does not lead to better performance. */

      return target.set(source.r <= 0.0031308f ? source.r * 12.92f
         : ( float ) ( Math.pow(source.r, 0.4166666666666667d) * 1.055d
            - 0.055d ), source.g <= 0.0031308f ? source.g * 12.92f
               : ( float ) ( Math.pow(source.g, 0.4166666666666667d) * 1.055d
                  - 0.055d ), source.b <= 0.0031308f ? source.b * 12.92f
                     : ( float ) ( Math.pow(source.b, 0.4166666666666667d)
                        * 1.055d - 0.055d ), alpha ? source.a <= 0.0031308f
                           ? source.a * 12.92f : ( float ) ( Math.pow(source.a,
                              0.4166666666666667d) * 1.055d - 0.055d )
                           : source.a);
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
    * @see Color#lRgbToSrXyz(Color, Vec4)
    */
   public static Vec4 lRgbToSrXyz ( final Color c, final Vec4 target ) {

      return Color.lRgbToSrXyz(c.r, c.g, c.b, c.a, target);
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
   public static Vec4 lRgbToSrXyz ( final float r, final float g, final float b,
      final float a, final Vec4 target ) {

      /* @formatter:off */
      return target.set(
         0.32053f * r + 0.63692f * g + 0.04256f * b,
         0.161987f * r + 0.756636f * g + 0.081376f * b,
         0.017228f * r + 0.10866f * g + 0.874112f * b,
         a);
      /* @formatter:on */
   }

   /**
    * Returns the relative luminance of a color; assumes the color is in sRGB.
    *
    * @param c the color
    *
    * @return the luminance
    *
    * @see Color#sRgbLuminance(Color)
    */
   public static float luminance ( final Color c ) {

      return Color.sRgbLuminance(c);
   }

   /**
    * Returns the color magenta, ( 1.0, 0.0, 1.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return magenta
    */
   public static Color magenta ( final Color target ) {

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
   public static boolean none ( final Color c ) { return c.a <= 0.0f; }

   /**
    * Multiplies the red, green and blue color channels of a color by the
    * alpha channel.
    *
    * @param c      the input color
    * @param target the output color
    *
    * @return the multiplied color
    */
   public static Color premul ( final Color c, final Color target ) {

      if ( c.a <= 0.0f ) { return target.set(0.0f, 0.0f, 0.0f, 0.0f); }
      if ( c.a >= 1.0f ) { return target.set(c.r, c.g, c.b, 1.0f); }
      return target.set(c.r * c.a, c.g * c.a, c.b * c.a, c.a);
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
   public static Color quantize ( final Color c, final int levels,
      final Color target ) {

      if ( levels == 1 || levels == -1 ) { return target.set(c); }
      final float levf = Utils.abs(levels);
      final float delta = 1.0f / ( levf - 1.0f );
      return target.set(Utils.quantizeUnsigned(c.r, levf, delta), Utils
         .quantizeUnsigned(c.g, levf, delta), Utils.quantizeUnsigned(c.b, levf,
            delta), Utils.quantizeUnsigned(c.a, levf, delta));
   }

   /**
    * Returns the color red, ( 1.0, 0.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return red
    */
   public static Color red ( final Color target ) {

      return target.set(1.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Evaluates whether the color is within the standard RGB gamut of [0.0,
    * 1.0] .
    *
    * @param c color
    *
    * @return the evaluation
    */
   public static boolean rgbIsInGamut ( final Color c ) {

      return Color.rgbIsInGamut(c, 0.0f);
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
   public static boolean rgbIsInGamut ( final Color c, final float tol ) {

      final float oneptol = 1.0f + tol;
      return c.r >= -tol && c.r <= oneptol && c.g >= -tol && c.g <= oneptol
         && c.b >= -tol && c.b <= oneptol;

   }

   /**
    * Convert a color to gray-scale based on its perceived luminance.
    *
    * @param c      the input color
    * @param target the output color
    *
    * @return the gray scale color
    *
    * @see Color#sRgbLuminance(Color)
    * @see Math#pow(double, double)
    */
   public static Color rgbToGray ( final Color c, final Color target ) {

      final float lum = Color.sRgbLuminance(c);
      final float vf = lum <= 0.0031308f ? lum * 12.92f : ( float ) ( Math.pow(
         lum, 0.4166666666666667d) * 1.055d - 0.055d );
      return target.set(vf, vf, vf, c.a);
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
   public static float sRgbLuminance ( final Color c ) {

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
   public static Color sRgbTolRgb ( final Color source, final boolean alpha,
      final Color target ) {

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
                        * 0.9478672985781991d, 2.4d), alpha ? source.a
                           <= 0.04045f ? source.a * 0.07739938f : ( float ) Math
                              .pow( ( source.a + 0.055d ) * 0.9478672985781991d,
                                 2.4d) : source.a);
   }

   /**
    * Converts a color from standard RGB (sRGB) to SR LAB 2. The target is
    * packaged as z: L or lightness, x: a or green-red, y: b or blue-yellow,
    * w: alpha.
    *
    * @param srgb the sRGB color
    * @param lab  the LAB color
    * @param xyz  the XYZ color
    * @param lrgb the linear sRGB color
    *
    * @return the output color
    *
    * @see Color#srXyzToSrLab2(Vec4, Vec4)
    * @see Color#lRgbToSrXyz(Color, Vec4)
    * @see Color#sRgbTolRgb(Color, boolean, Color)
    */
   public static Vec4 sRgbToSrLab2 ( final Color srgb, final Vec4 lab,
      final Vec4 xyz, final Color lrgb ) {

      return Color.srXyzToSrLab2(Color.lRgbToSrXyz(Color.sRgbTolRgb(srgb, false,
         lrgb), xyz), lab);
   }

   /**
    * Converts a color from standard RGB (sRGB) to SR LCH. The output is
    * organized as z: L or lightness, y: C or chroma, x: h or hue, w: alpha.
    *
    * @param srgb the sRGB color
    * @param lch  the LCH color
    * @param lab  the LAB color
    * @param xyz  the XYZ color
    * @param lrgb linear sRGB color
    *
    * @return the output color
    *
    * @see Color#srLab2ToSrLch(Vec4, Vec4)
    * @see Color#sRgbToSrLab2(Color, Vec4, Vec4, Color)
    */
   public static Vec4 sRgbToSrLch ( final Color srgb, final Vec4 lch,
      final Vec4 lab, final Vec4 xyz, final Color lrgb ) {

      return Color.srLab2ToSrLch(Color.sRgbToSrLab2(srgb, lab, xyz, lrgb), lch);
   }

   /**
    * Converts a color from SR LAB 2 to standard RGB (sRGB). The source should
    * be organized as z: L or lightness, x: a or green-red, y: b or
    * blue-yellow, w: alpha.
    *
    * @param lab  LAB color
    * @param srgb sRGB color
    * @param lrgb linear sRGB color
    * @param xyz  XYZ color
    *
    * @return the output color
    *
    * @see Color#lRgbTosRgb(Color, boolean, Color)
    * @see Color#srLab2ToSrXyz(Vec4, Vec4)
    * @see Color#srXyzTolRgb(Vec4, Color)
    */
   public static Color srLab2TosRgb ( final Vec4 lab, final Color srgb,
      final Color lrgb, final Vec4 xyz ) {

      return Color.lRgbTosRgb(Color.srXyzTolRgb(Color.srLab2ToSrXyz(lab, xyz),
         lrgb), false, srgb);
   }

   /**
    * Converts a color from SR LAB 2 to SR LCH. The output is organized as z:
    * L or lightness, y: C or chroma, x: h or hue, w: alpha. The returned hue
    * is in the range [0.0, 1.0] .
    *
    * @param l      the lightness
    * @param a      the green to red range
    * @param b      the blue to yellow range
    * @param alpha  the alpha channel
    * @param target the output vector
    *
    * @return the LCh vector
    *
    * @see Utils#clamp01(float)
    * @see Utils#mod1(float)
    */
   public static Vec4 srLab2ToSrLch ( final float l, final float a,
      final float b, final float alpha, final Vec4 target ) {

      final float cSq = a * a + b * b;
      if ( cSq < 0.00005f ) {
         final float fac = Utils.clamp01(l * 0.01f);
         return target.set(Utils.mod1( ( 1.0f - fac ) * Color.SR_LCH_HUE_SHADOW
            + fac * ( 1.0f + Color.SR_LCH_HUE_LIGHT )), 0.0f, l, alpha);
      }
      return target.set(Utils.mod1(( float ) ( IUtils.ONE_TAU_D * Math.atan2(b,
         a) )), ( float ) Math.sqrt(cSq), l, alpha);
   }

   /**
    * Converts a color from SR LAB 2 to SR LCH. The source should be organized
    * as z: L or lightness, x: a or green-red, y: b or blue-yellow, w: alpha.
    * The output is organized as z: L or lightness, y: C or chroma, x: h or
    * hue, w: alpha. The returned hue is in the range [0.0, 1.0] .
    *
    * @param source the lab vector
    * @param target the output vector
    *
    * @return the LCh color
    */
   public static Vec4 srLab2ToSrLch ( final Vec4 source, final Vec4 target ) {

      return Color.srLab2ToSrLch(source.z, source.x, source.y, source.w,
         target);
   }

   /**
    * Converts a color from SR LAB 2 to SR XYZ. See <a href=
    * "https://www.magnetkern.de/srlab2.html">https://www.magnetkern.de/srlab2.html</a>.
    *
    * @param l      the lightness
    * @param a      the green to red range
    * @param b      the blue to yellow range
    * @param alpha  the alpha channel
    * @param target the output vector
    *
    * @return the XYZ color
    *
    * @author Jan Behrens
    */
   public static Vec4 srLab2ToSrXyz ( final float l, final float a,
      final float b, final float alpha, final Vec4 target ) {

      final double l01 = l * 0.01d;
      final double ad = a;
      final double bd = b;

      final double xd = l01 + 0.000904127d * ad + 0.000456344d * bd;
      final double yd = l01 - 0.000533159d * ad - 0.000269178d * bd;
      final double zd = l01 - 0.0058d * bd;

      /* @formatter:off */
      return target.set(
         ( float ) ( xd <= 0.08d ? xd * 2700.0d / 24389.0d :
            Math.pow( ( xd + 0.16d ) / 1.16d, 3.0d) ),
         ( float ) ( yd <= 0.08d ? yd * 2700.0d / 24389.0d :
            Math.pow( ( yd + 0.16d ) / 1.16d, 3.0d) ),
         ( float ) ( zd <= 0.08d ? zd * 2700.0d / 24389.0d :
            Math.pow( ( zd + 0.16d ) / 1.16d, 3.0d) ),
         alpha);
      /* @formatter:on */
   }

   /**
    * Converts a color from SR LAB 2 to SR XYZ.<br>
    * <br>
    * The source should be organized as z: L or lightness, x: a or green-red,
    * y: b or blue-yellow, w: alpha.
    *
    * @param source the XYZ vector
    * @param target the output vector
    *
    * @return the lab color
    *
    * @see Color#srLab2ToSrXyz(float, float, float, float, Vec4)
    */
   public static Vec4 srLab2ToSrXyz ( final Vec4 source, final Vec4 target ) {

      return Color.srLab2ToSrXyz(source.z, source.x, source.y, source.w,
         target);
   }

   /**
    * Converts a color from SR LCH to standard RBG (sRGB). The source should
    * be organized as z: L or lightness, y: C or chroma, x: h or hue, w:
    * alpha.
    *
    * @param lch  the LCH color
    * @param srgb the sRGB color
    * @param lrgb the linear sRGB color
    * @param xyz  the XYZ color
    * @param lab  the LAB color
    *
    * @return the output color
    *
    * @see Color#srLab2TosRgb(Vec4, Color, Color, Vec4)
    * @see Color#srLchToSrLab2(Vec4, Vec4)
    */
   public static Color srLchTosRgb ( final Vec4 lch, final Color srgb,
      final Color lrgb, final Vec4 xyz, final Vec4 lab ) {

      return Color.srLab2TosRgb(Color.srLchToSrLab2(lch, lab), srgb, lrgb, xyz);
   }

   /**
    * Converts a color from SR LCH to SR LAB 2. The output is organized as z:
    * L or lightness, x: a or green-red, y: b or blue-yellow, w: alpha.
    *
    * @param l      the lightness
    * @param c      the chroma
    * @param h      the hue
    * @param a      the alpha channel
    * @param target the output vector
    *
    * @return the lab vector
    *
    * @see Utils#mod1(float)
    */
   public static Vec4 srLchToSrLab2 ( final float l, final float c,
      final float h, final float a, final Vec4 target ) {

      final double hRad = Utils.mod1(h) * IUtils.TAU_D;
      return target.set(c * ( float ) Math.cos(hRad), c * ( float ) Math.sin(
         hRad), l, a);
   }

   /**
    * Converts a color from SR LCH to SR LAB 2. The source should be organized
    * as z: L or lightness, y: C or chroma, x: h or hue, w: alpha. The output
    * is organized as z: L or lightness, x: a or green-red, y: b or
    * blue-yellow, w: alpha
    *
    * @param source the LCH vector
    * @param target the output vector
    *
    * @return the lab vector
    */
   public static Vec4 srLchToSrLab2 ( final Vec4 source, final Vec4 target ) {

      return Color.srLchToSrLab2(source.z, source.y, source.x, source.w,
         target);
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
   public static Color srXyzTolRgb ( final float x, final float y,
      final float z, final float a, final Color target ) {

      /* @formatter:off */
      return target.set(
         5.435679f * x - 4.599131f * y + 0.163593f * z,
         -1.16809f * x + 2.327977f * y - 0.159798f * z,
         0.03784f * x - 0.198564f * y + 1.160644f * z,
         a);
      /* @formatter:off */
   }

   /**
    * Converts a color from SR XYZ to linear RGB.
    *
    * @param source the XYZ vector
    * @param target the output color
    *
    * @return the color
    *
    * @see Color#srXyzTolRgb(float, float, float, float, Color)
    */
   public static Color srXyzTolRgb ( final Vec4 source, final Color target ) {

      return Color.srXyzTolRgb(source.x, source.y, source.z, source.w, target);
   }

   /**
    * Converts a color from SR XYZ to SR LAB 2.<br>
    * <br>
    * The target is packaged as z: L or lightness, x: a or green-red, y: b or
    * blue-yellow, w: alpha.
    *
    * @param x      the x coordinate
    * @param y      the y coordinate
    * @param z      the z coordinate
    * @param a      the alpha component
    * @param target the output vector
    *
    * @return the Lab color
    *
    * @see Math#pow(double, double)
    *
    * @author Jan Behrens
    */
   public static Vec4 srXyzToSrLab2 ( final float x, final float y,
      final float z, final float a, final Vec4 target ) {

      final double xd = x <= 0.008856452f ? x * 9.032962962962962d : 1.16d
         * Math.pow(x, 0.3333333333333333d) - 0.16d;
      final double yd = y <= 0.008856452f ? y * 9.032962962962962d : 1.16d
         * Math.pow(y, 0.3333333333333333d) - 0.16d;
      final double zd = z <= 0.008856452f ? z * 9.032962962962962d : 1.16d
         * Math.pow(z, 0.3333333333333333d) - 0.16d;

      /* @formatter:off */
      return target.set(
         ( float ) ( 663.4684d * xd - 750.5078d * yd + 87.0328d * zd ),
         ( float ) ( 63.9569d * xd + 108.4576d * yd - 172.4152d * zd ),
         ( float ) ( 37.095d * xd + 62.9054d * yd - 0.0008d * zd ),
         a);
      /* @formatter:on */
   }

   /**
    * Converts a color from SR XYZ to SR LAB 2.<br>
    * <br>
    * The target is packaged as z: L or lightness, x: a or green-red, y: b or
    * blue-yellow, w: alpha.
    *
    * @param source the XYZ vector
    * @param target the output vector
    *
    * @return the Lab color
    *
    * @see Color#srXyzToSrLab2(float, float, float, float, Vec4)
    */
   public static Vec4 srXyzToSrLab2 ( final Vec4 source, final Vec4 target ) {

      return Color.srXyzToSrLab2(source.x, source.y, source.z, source.w,
         target);
   }

   /**
    * Returns a String representing the color array in the GIMP palette file
    * format.
    *
    * @param arr the array
    *
    * @return the string
    */
   public static String toGplString ( final Color[] arr ) {

      return Color.toGplString(arr, "Palette");
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
   public static String toGplString ( final Color[] arr, final String name ) {

      return Color.toGplString(arr, name, Utils.ceil(Utils.sqrt(arr.length)));
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
   public static String toGplString ( final Color[] arr, final String name,
      final int cols ) {

      return Color.toGplString(arr, name, cols, false);
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
    * @see Color#toGplString(StringBuilder)
    * @see Color#toHexWeb(Color)
    */
   public static String toGplString ( final Color[] arr, final String name,
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
         final Color c = arr[i];
         sb.append('\n');
         c.toGplString(sb);
         sb.append(' ');
         sb.append(Color.toHexWeb(c).substring(1).toUpperCase());
         if ( useIdx ) {
            sb.append(' ');
            sb.append(i + 1);
         }
      }

      return sb.toString();
   }

   /**
    * Converts a color to an integer where hexadecimal represents the ARGB
    * color channels: 0xAARRGGBB . Defaults to modular arithmetic.
    *
    * @param c the input color
    *
    * @return the color in hexadecimal
    *
    * @see Color#toHexIntWrap(Color)
    */
   public static int toHexInt ( final Color c ) {

      return Color.toHexIntWrap(c);
   }

   /**
    * Converts a color to an integer where hexadecimal represents the ARGB
    * color channels: 0xAARRGGBB . Uses saturation arithmetic. Two colors with
    * unequal values beyond [0.0, 1.0] may yield equal integers.
    *
    * @param c the input color
    *
    * @return the color in hexadecimal
    *
    * @see Utils#clamp01(float)
    */
   public static int toHexIntSat ( final Color c ) {

      /* @formatter:off */
      return ( int ) ( Utils.clamp01(c.a) * 0xff + 0.5f ) << 0x18
           | ( int ) ( Utils.clamp01(c.r) * 0xff + 0.5f ) << 0x10
           | ( int ) ( Utils.clamp01(c.g) * 0xff + 0.5f ) << 0x08
           | ( int ) ( Utils.clamp01(c.b) * 0xff + 0.5f );
      /* @formatter:on */
   }

   /**
    * Converts a color to an integer where hexadecimal represents the ARGB
    * color channels: 0xAARRGGBB . Uses modular arithmetic, so out-of-gamut
    * colors may cause overflow and unexpected hexadecimal colors.
    *
    * @param c the input color
    *
    * @return the color in hexadecimal
    */
   public static int toHexIntWrap ( final Color c ) {

      /* @formatter:off */
      return ( int ) ( c.a * 0xff + 0.5f ) << 0x18
           | ( int ) ( c.r * 0xff + 0.5f ) << 0x10
           | ( int ) ( c.g * 0xff + 0.5f ) << 0x08
           | ( int ) ( c.b * 0xff + 0.5f );
      /* @formatter:on */
   }

   /**
    * Returns a representation of the color as a hexadecimal code, preceded by
    * a '0x', in the format AARRGGBB.
    *
    * @param c the color
    *
    * @return the string
    *
    * @see Color#toHexString(StringBuilder, Color)
    */
   public static String toHexString ( final Color c ) {

      return Color.toHexString(new StringBuilder(10), c).toString();
   }

   /**
    * Returns a representation of the color as a hexadecimal code, preceded by
    * a '0x', in the format AARRGGBB.
    *
    * @param c the color
    *
    * @return the string
    *
    * @see Color#toHexString(StringBuilder, Color)
    */
   public static String toHexString ( final int c ) {

      return Color.toHexString(new StringBuilder(10), c).toString();
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
    * @see Color#toHexString(StringBuilder, int, int, int, int)
    * @see Utils#clamp01(float)
    */
   public static StringBuilder toHexString ( final StringBuilder sb,
      final Color c ) {

      return Color.toHexString(sb, ( int ) ( Utils.clamp01(c.a) * 0xff + 0.5f ),
         ( int ) ( Utils.clamp01(c.r) * 0xff + 0.5f ), ( int ) ( Utils.clamp01(
            c.g) * 0xff + 0.5f ), ( int ) ( Utils.clamp01(c.b) * 0xff + 0.5f ));
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
    * @see Color#toHexString(StringBuilder, int, int, int, int)
    */
   public static StringBuilder toHexString ( final StringBuilder sb,
      final int c ) {

      return Color.toHexString(sb, c >> 0x18 & 0xff, c >> 0x10 & 0xff, c >> 0x08
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
    * @see Color#toHexDigit(StringBuilder, int)
    */
   public static StringBuilder toHexString ( final StringBuilder sb,
      final int a, final int r, final int g, final int b ) {

      sb.append("0x");
      Color.toHexDigit(sb, a);
      Color.toHexDigit(sb, r);
      Color.toHexDigit(sb, g);
      Color.toHexDigit(sb, b);
      return sb;
   }

   /**
    * Creates a web-friendly representation of the color as a hexadecimal
    * code, preceded by a hash tag, '#', with no alpha.
    *
    * @param c the color
    *
    * @return the string
    *
    * @see Color#toHexWeb(StringBuilder, Color)
    */
   public static String toHexWeb ( final Color c ) {

      return Color.toHexWeb(new StringBuilder(7), c).toString();
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
    * @see Color#toHexWeb(StringBuilder, int)
    */
   public static String toHexWeb ( final int c ) {

      return Color.toHexWeb(new StringBuilder(7), c).toString();
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
    * @see Color#toHexWeb(StringBuilder, int, int, int)
    */
   public static StringBuilder toHexWeb ( final StringBuilder sb,
      final Color c ) {

      return Color.toHexWeb(sb, ( int ) ( Utils.clamp01(c.r) * 0xff + 0.5f ),
         ( int ) ( Utils.clamp01(c.g) * 0xff + 0.5f ), ( int ) ( Utils.clamp01(
            c.b) * 0xff + 0.5f ));
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
    * @see Color#toHexWeb(StringBuilder, int, int, int)
    */
   public static StringBuilder toHexWeb ( final StringBuilder sb,
      final int c ) {

      return Color.toHexWeb(sb, c >> 0x10 & 0xff, c >> 0x08 & 0xff, c & 0xff);
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
    * @see Color#toHexDigit(StringBuilder, int)
    */
   public static StringBuilder toHexWeb ( final StringBuilder sb, final int r,
      final int g, final int b ) {

      sb.append('#');
      Color.toHexDigit(sb, r);
      Color.toHexDigit(sb, g);
      Color.toHexDigit(sb, b);
      return sb;
   }

   /**
    * Returns a String representing the color array in the JASC-PAL palette
    * file format.
    *
    * @param arr the array
    *
    * @return the string
    *
    * @see Color#toGplString(StringBuilder)
    */
   public static String toPalString ( final Color[] arr ) {

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
   public static String toString ( final Color[] arr ) {

      return Color.toString(arr, IUtils.FIXED_PRINT);
   }

   /**
    * Returns a string representation of an array of colors.
    *
    * @param arr    the array
    * @param places the print precision
    *
    * @return the string
    */
   public static String toString ( final Color[] arr, final int places ) {

      final StringBuilder sb = new StringBuilder(1024);
      sb.append('[').append(' ');

      if ( arr != null ) {
         final int len = arr.length;
         final int last = len - 1;

         for ( int i = 0; i < last; ++i ) {
            final Color c = arr[i];
            c.toString(sb, places);
            sb.append(',').append(' ');
         }

         final Color cl = arr[last];
         cl.toString(sb, places);
         sb.append(' ');
      }

      sb.append(']');
      return sb.toString();
   }

   /**
    * Divides the red, green and blue color channels of a color by the alpha
    * channel; reverses {@link Color#premul(Color, Color)}.
    *
    * @param c      the input color
    * @param target the output color
    *
    * @return the divided color
    */
   public static Color unpremul ( final Color c, final Color target ) {

      if ( c.a <= 0.0f ) { return target.set(0.0f, 0.0f, 0.0f, 0.0f); }
      if ( c.a >= 1.0f ) { return target.set(c.r, c.g, c.b, 1.0f); }
      final float aInv = 1.0f / c.a;
      return target.set(c.r * aInv, c.g * aInv, c.b * aInv, c.a);
   }

   /**
    * Returns the color white, ( 1.0, 1.0, 1.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return white
    */
   public static Color white ( final Color target ) {

      return target.set(1.0f, 1.0f, 1.0f, 1.0f);
   }

   /**
    * Returns the color yellow, ( 1.0, 1.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return yellow
    */
   public static Color yellow ( final Color target ) {

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
    * @see Color#toHexIntWrap(Color)
    */
   static int mix ( final Color orig, final Color dest, final float step ) {

      if ( step <= 0.0f ) { return Color.toHexIntWrap(orig); }
      if ( step >= 1.0f ) { return Color.toHexIntWrap(dest); }

      final float u = 1.0f - step;
      return ( int ) ( ( u * orig.a + step * dest.a ) * 0xff + 0.5f ) << 0x18
         | ( int ) ( ( u * orig.r + step * dest.r ) * 0xff + 0.5f ) << 0x10
         | ( int ) ( ( u * orig.g + step * dest.g ) * 0xff + 0.5f ) << 0x08
         | ( int ) ( ( u * orig.b + step * dest.b ) * 0xff + 0.5f );
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
   static Color mix ( final Color orig, final Color dest, final float step,
      final Color target ) {

      if ( step <= 0.0f ) { return target.set(orig); }
      if ( step >= 1.0f ) { return target.set(dest); }

      final float u = 1.0f - step;
      return target.set(u * orig.r + step * dest.r, u * orig.g + step * dest.g,
         u * orig.b + step * dest.b, u * orig.a + step * dest.a);
   }

   /**
    * A helper function to translate a byte to a hexadecimal string. Does
    * <em>not</em> prefix the String with a hexadecimal indicator, '0x'; this
    * is so that Strings can be concatenated together.
    *
    * @param sb the string builder
    * @param b  the byte
    *
    * @return the string
    */
   static StringBuilder toHexDigit ( final StringBuilder sb, final int b ) {

      final int digit0 = b >> 0x4 & 0xf;
      final int digit1 = b & 0xf;

      /* @formatter:off */
      switch ( digit0 ) {
         case 0xa: sb.append('a'); break;
         case 0xb: sb.append('b'); break;
         case 0xc: sb.append('c'); break;
         case 0xd: sb.append('d'); break;
         case 0xe: sb.append('e'); break;
         case 0xf: sb.append('f'); break;
         default: sb.append(( char ) ( '0' + digit0 ));
      }

      switch ( digit1 ) {
         case 0xa: sb.append('a'); break;
         case 0xb: sb.append('b'); break;
         case 0xc: sb.append('c'); break;
         case 0xd: sb.append('d'); break;
         case 0xe: sb.append('e'); break;
         case 0xf: sb.append('f'); break;
         default: sb.append(( char ) ( '0' + digit1 ));
      }
      /* @formatter:on */

      return sb;
   }

   /**
    * An abstract class to facilitate the creation of color easing functions.
    */
   public abstract static class AbstrEasing implements Utils.EasingFuncObj <
      Color > {

      /**
       * The default constructor.
       */
      protected AbstrEasing ( ) {}

      /**
       * A clamped interpolation between the origin and destination. Defers to
       * an unclamped interpolation, which is to be defined by sub-classes of
       * this class.
       *
       * @param orig   the origin color
       * @param dest   the destination color
       * @param step   a factor in [0, 1]
       * @param target the output color
       *
       * @return the eased color
       */
      @Override
      public Color apply ( final Color orig, final Color dest, final Float step,
         final Color target ) {

         final float tf = step;
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
      public abstract Color applyUnclamped ( final Color orig, final Color dest,
         final Float step, final Color target );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * Eases between hues in the clockwise direction.
    */
   public static class HueCCW extends HueEasing {

      /**
       * The default constructor.
       */
      public HueCCW ( ) {}

      /**
       * Applies the function.
       *
       * @param step the step in a range 0 to 1
       *
       * @return the eased hue
       *
       * @see Utils#mod1(float)
       */
      @Override
      protected float applyPartial ( final float step ) {

         if ( this.diff == 0.0f ) { return this.o; }
         if ( this.oGtd ) {
            return Utils.mod1( ( 1.0f - step ) * this.o + step * ( this.d
               + 1.0f ));
         }
         return ( 1.0f - step ) * this.o + step * this.d;
      }

   }

   /**
    * Eases the hue in the counter-clockwise direction.
    */
   public static class HueCW extends HueEasing {

      /**
       * The default constructor.
       */
      public HueCW ( ) {}

      /**
       * Applies the function.
       *
       * @param step the step in a range 0 to 1
       *
       * @return the eased hue
       *
       * @see Utils#mod1(float)
       */
      @Override
      protected float applyPartial ( final float step ) {

         if ( this.diff == 0.0f ) { return this.d; }
         if ( this.oLtd ) {
            return Utils.mod1( ( 1.0f - step ) * ( this.o + 1.0f ) + step
               * this.d);
         }
         return ( 1.0f - step ) * this.o + step * this.d;
      }

   }

   /**
    * An abstract parent class for hue easing functions.
    */
   public abstract static class HueEasing implements Utils.EasingFuncPrm <
      Float > {

      /**
       * The modulated destination hue.
       */
      protected float d = 0.0f;

      /**
       * The difference between the stop and start hue.
       */
      protected float diff = 0.0f;

      /**
       * The modulated origin hue.
       */
      protected float o = 0.0f;

      /**
       * Whether or not {@link o} is greater than {@link d}.
       */
      protected boolean oGtd = false;

      /**
       * Whether or not {@link o} is less than {@link d}.
       */
      protected boolean oLtd = false;

      /**
       * The default constructor.
       */
      protected HueEasing ( ) {}

      /**
       * The clamped easing function.
       *
       * @param orig the origin hue
       * @param dest the destination hue
       * @param step the step in range 0 to 1
       *
       * @return the eased hue
       */
      @Override
      public Float apply ( final Float orig, final Float dest,
         final Float step ) {

         this.eval(orig, dest);
         final float t = step;
         if ( t <= 0.0f ) { return this.o; }
         if ( t >= 1.0f ) { return this.d; }
         return this.applyPartial(t);
      }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

      /**
       * The application function to be defined by sub-classes of this class.
       *
       * @param step the step
       *
       * @return the eased hue
       */
      protected abstract float applyPartial ( final float step );

      /**
       * A helper function to pass on to sub-classes of this class. Mutates the
       * fields {@link o}, {@link d}, {@link diff}, {@link oLtd} and
       * {@link oGtd}.
       *
       * @param orig the origin hue
       * @param dest the destination hue
       *
       * @see Utils#mod1(float)
       */
      protected void eval ( final float orig, final float dest ) {

         this.o = Utils.mod1(orig);
         this.d = Utils.mod1(dest);
         this.diff = this.d - this.o;
         this.oLtd = this.o < this.d;
         this.oGtd = this.o > this.d;
      }

   }

   /**
    * Eases between hues by the farthest rotational direction.
    */
   public static class HueFar extends HueEasing {

      /**
       * The default constructor.
       */
      public HueFar ( ) {}

      /**
       * Applies the function.
       *
       * @param step the step in a range 0 to 1
       *
       * @return the eased hue
       *
       * @see Utils#mod1(float)
       */
      @Override
      protected float applyPartial ( final float step ) {

         if ( this.diff == 0.0f || this.oLtd && this.diff < 0.5f ) {
            return Utils.mod1( ( 1.0f - step ) * ( this.o + 1.0f ) + step
               * this.d);
         }
         if ( this.oGtd && this.diff > -0.5f ) {
            return Utils.mod1( ( 1.0f - step ) * this.o + step * ( this.d
               + 1.0f ));
         }
         return ( 1.0f - step ) * this.o + step * this.d;
      }

   }

   /**
    * Eases between hues by the nearest rotational direction.
    */
   public static class HueNear extends HueEasing {

      /**
       * The default constructor.
       */
      public HueNear ( ) {}

      /**
       * Applies the function.
       *
       * @param step the step in a range 0 to 1
       *
       * @return the eased hue
       *
       * @see Utils#mod1(float)
       */
      @Override
      protected float applyPartial ( final float step ) {

         if ( this.diff == 0.0f ) { return this.o; }
         if ( this.oLtd && this.diff > 0.5f ) {
            return Utils.mod1( ( 1.0f - step ) * ( this.o + 1.0f ) + step
               * this.d);
         }
         if ( this.oGtd && this.diff < -0.5f ) {
            return Utils.mod1( ( 1.0f - step ) * this.o + step * ( this.d
               + 1.0f ));
         }
         return ( 1.0f - step ) * this.o + step * this.d;
      }

   }

   /**
    * Converts two colors from
    * <a href="https://www.wikiwand.com/en/SRGB">standard RGB</a> to linear
    * RGB, mixes them with linear interpolation, then converts back to
    * standard RGB.
    */
   public static class MixLrgb extends AbstrEasing {

      /**
       * Whether or not to include the alpha in the adjustment.
       */
      final boolean alpha;

      /**
       * The mixed color in linear RGB.
       */
      protected final Color cLinear = new Color();

      /**
       * The destination color in linear RGB.
       */
      protected final Color dLinear = new Color();

      /**
       * The origin color in linear RGB.
       */
      protected final Color oLinear = new Color();

      /**
       * Construct a new linear color mixer. Defaults to not including alpha, or
       * transparency, in the adjustment.
       */
      public MixLrgb ( ) { this(false); }

      /**
       * Construct a new linear color mixer. The flag specifies whether or not
       * alpha should be included in the adjustment.
       *
       * @param alpha flag to adjust alpha
       */
      public MixLrgb ( final boolean alpha ) {

         this.alpha = alpha;
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
       * @see Color#sRgbTolRgb(Color, boolean, Color)
       * @see Color#lRgbTosRgb(Color, boolean, Color)
       */
      @Override
      public Color applyUnclamped ( final Color orig, final Color dest,
         final Float step, final Color target ) {

         Color.sRgbTolRgb(orig, this.alpha, this.oLinear);
         Color.sRgbTolRgb(dest, this.alpha, this.dLinear);

         final float t = step;
         final float u = 1.0f - t;
         this.cLinear.set(u * this.oLinear.r + t * this.dLinear.r, u
            * this.oLinear.g + t * this.dLinear.g, u * this.oLinear.b + t
               * this.dLinear.b, u * this.oLinear.a + t * this.dLinear.a);

         return Color.lRgbTosRgb(this.cLinear, this.alpha, target);
      }

   }

   /**
    * Eases between two colors in sRGB, i.e., with no gamma correction.
    */
   public static class MixSrgb extends AbstrEasing {

      /**
       * The default constructor.
       */
      public MixSrgb ( ) {}

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
      public Color applyUnclamped ( final Color orig, final Color dest,
         final Float step, final Color target ) {

         final float t = step;
         final float u = 1.0f - t;
         return target.set(u * orig.r + t * dest.r, u * orig.g + t * dest.g, u
            * orig.b + t * dest.b, u * orig.a + t * dest.a);
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
      protected final Vec4 cLab = new Vec4();

      /**
       * The mixed color in linear RGB.
       */
      protected final Color cLinear = new Color();

      /**
       * The mixed color in XYZ.
       */
      protected final Vec4 cXyz = new Vec4();

      /**
       * The destination color in LAB.
       */
      protected final Vec4 dLab = new Vec4();

      /**
       * The destination color in linear RGB.
       */
      protected final Color dLinear = new Color();

      /**
       * The destination color in XYZ.
       */
      protected final Vec4 dXyz = new Vec4();

      /**
       * The origin color in LAB.
       */
      protected final Vec4 oLab = new Vec4();

      /**
       * The origin color in linear RGB.
       */
      protected final Color oLinear = new Color();

      /**
       * The origin color in XYZ.
       */
      protected final Vec4 oXyz = new Vec4();

      /**
       * The default constructor.
       */
      public MixSrLab2 ( ) {}

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
       * @see Color#sRgbToSrLab2(Color, Vec4, Vec4, Color)
       * @see Color#srLab2TosRgb(Vec4, Color, Color, Vec4)
       * @see Vec4#mix(Vec4, Vec4, float, Vec4)
       */
      @Override
      public Color applyUnclamped ( final Color orig, final Color dest,
         final Float step, final Color target ) {

         Color.sRgbToSrLab2(orig, this.oLab, this.oXyz, this.oLinear);
         Color.sRgbToSrLab2(dest, this.dLab, this.dXyz, this.dLinear);
         Vec4.mix(this.oLab, this.dLab, step, this.cLab);
         Color.srLab2TosRgb(this.cLab, target, this.cLinear, this.cXyz);

         return target;
      }

   }

   /**
    * Eases between two colors in SR LCH color space. May return colors
    * outside the range [0.0, 1.0] .
    */
   public static class MixSrLch extends MixSrLab2 {

      /**
       * The new LCH color.
       */
      protected final Vec4 cLch = new Vec4();

      /**
       * The hue easing function.
       */
      protected HueEasing hueFunc;

      /**
       * The default constructor. Creates a mixer with nearest hue
       * interpolation.
       */
      public MixSrLch ( ) { this(new HueNear()); }

      /**
       * Creates a color SR LCH mixing function with the given easing functions
       * for hue.
       *
       * @param hueFunc the hue easing function
       */
      public MixSrLch ( final HueEasing hueFunc ) {

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
       * @see Color#sRgbToSrLab2(Color, Vec4, Vec4, Color)
       * @see Color#srLab2TosRgb(Vec4, Color, Color, Vec4)
       * @see Color#srLchToSrLab2(Vec4, Vec4)
       * @see Vec4#mix(Vec4, Vec4, float, Vec4)
       */
      @Override
      public Color applyUnclamped ( final Color orig, final Color dest,
         final Float step, final Color target ) {

         Color.sRgbToSrLab2(orig, this.oLab, this.oXyz, this.oLinear);

         final float oa = this.oLab.x;
         final float ob = this.oLab.y;
         final float ocsq = oa * oa + ob * ob;

         Color.sRgbToSrLab2(dest, this.dLab, this.dXyz, this.dLinear);

         final float da = this.dLab.x;
         final float db = this.dLab.y;
         final float dcsq = da * da + db * db;

         if ( ocsq < Color.C_EPSILON * Color.C_EPSILON || dcsq < Color.C_EPSILON
            * Color.C_EPSILON ) {
            Vec4.mix(this.oLab, this.dLab, step, this.cLab);
         } else {
            final float t = step;
            final float u = 1.0f - t;

            float oh = Utils.atan2(ob, oa) * IUtils.ONE_TAU;
            if ( oh < -0.0f ) { ++oh; }
            float dh = Utils.atan2(db, da) * IUtils.ONE_TAU;
            if ( dh < -0.0f ) { ++dh; }

            this.cLch.set(this.hueFunc.apply(oh, dh, step), u * Utils
               .sqrtUnchecked(ocsq) + t * Utils.sqrtUnchecked(dcsq), u
                  * this.oLab.z + t * this.dLab.z, u * this.oLab.w + t
                     * this.dLab.w);

            Color.srLchToSrLab2(this.cLch, this.cLab);
         }

         Color.srLab2TosRgb(this.cLab, target, this.cLinear, this.cXyz);
         return target;
      }

   }

}
