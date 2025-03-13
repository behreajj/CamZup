package camzup.core;

import java.util.Random;
import java.util.function.Function;

/**
 * A mutable, extensible color class that represents colors in a perceptual
 * color space, such as CIE LAB, SR LAB 2 or OK LAB. The <em>a</em> and
 * <em>b</em> axes are signed, unbounded values. Negative <em>a</em>
 * indicates a green hue; positive, magenta. Negative <em>b</em> indicates
 * a blue hue; positive, yellow. Lightness falls in the range [0.0, 100.0]
 * . For a and b, the practical range is roughly [-111.0, 111.0] . Alpha is
 * expected to be in [0.0, 1.0] .
 */
public class Lab implements IColor {

   /**
    * The green-magenta component.
    */
   public float a = 0.0f;

   /**
    * The alpha channel, which governs transparency.
    */
   public float alpha = 1.0f;

   /**
    * The blue-yellow component.
    */
   public float b = 0.0f;

   /**
    * The light component.
    */
   public float l = 100.0f;

   /**
    * The default constructor. Creates a white color.
    */
   public Lab ( ) {

      // TODO: Define some comparators for light, a, b, alpha, etc?
   }

   /**
    * Creates a color from bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] . The alpha channel
    * defaults to 1.0 .
    *
    * @param l the light component
    * @param a the green-magenta component
    * @param b the blue-yellow component
    */
   public Lab ( final byte l, final byte a, final byte b ) {

      this.set(l, a, b);
   }

   /**
    * Creates a color from bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param l     the light component
    * @param a     the green-magenta component
    * @param b     the blue-yellow component
    * @param alpha the alpha channel
    */
   public Lab ( final byte l, final byte a, final byte b, final byte alpha ) {

      this.set(l, a, b, alpha);
   }

   /**
    * Creates a color from l, a, and b components. The alpha channel defaults
    * to 1.0 .
    *
    * @param l the light component
    * @param a the green-magenta component
    * @param b the blue-yellow component
    */
   public Lab ( final float l, final float a, final float b ) {

      this.set(l, a, b);
   }

   /**
    * Creates a color from l, a, b and alpha components.
    *
    * @param l     the light component
    * @param a     the green-magenta component
    * @param b     the blue-yellow component
    * @param alpha the alpha channel
    */
   public Lab ( final float l, final float a, final float b,
      final float alpha ) {

      this.set(l, a, b, alpha);
   }

   /**
    * Creates a color from a source.
    *
    * @param source the source color
    */
   public Lab ( final Lab source ) { this.set(source); }

   /**
    * Creates a color from shorts. In Java, shorts are signed, within the
    * range [{@value Short#MIN_VALUE}, {@value Short#MAX_VALUE}] . The alpha
    * channel defaults to 1.0 .
    *
    * @param l the light component
    * @param a the green-magenta component
    * @param b the blue-yellow component
    */
   public Lab ( final short l, final short a, final short b ) {

      this.set(l, a, b);
   }

   /**
    * Creates a color from shorts. In Java, shorts are signed, within the
    * range [{@value Short#MIN_VALUE}, {@value Short#MAX_VALUE}] .
    *
    * @param l     the light component
    * @param a     the green-magenta component
    * @param b     the blue-yellow component
    * @param alpha the alpha channel
    */
   public Lab ( final short l, final short a, final short b,
      final short alpha ) {

      this.set(l, a, b, alpha);
   }

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
    * @see Lab#equals(Lab)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Lab ) obj);
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
   public Lab reset ( ) { return this.set(100.0f, 0.0f, 0.0f, 1.0f); }

   /**
    * Sets a color with bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] . The alpha channel
    * defaults to 1.0 .
    *
    * @param l the light component
    * @param a the green-magenta component
    * @param b the blue-yellow component
    *
    * @return this color
    */
   public Lab set ( final byte l, final byte a, final byte b ) {

      return this.set(l, a, b, ( byte ) -1);
   }

   /**
    * Sets a color with bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param l     the light component
    * @param a     the green-magenta component
    * @param b     the blue-yellow component
    * @param alpha the alpha channel
    *
    * @return this color
    */
   public Lab set ( final byte l, final byte a, final byte b,
      final byte alpha ) {

      /* @formatter:off */
      return this.set(
         ( l & 0xff ) * Lab.L_FROM_BYTE,
         ( a & 0xff ) - 0x80,
         ( b & 0xff ) - 0x80,
         ( alpha & 0xff ) * IUtils.ONE_255);
      /* @formatter:on */
   }

   /**
    * Sets the l, a and b color channels of this color. The alpha channel
    * defaults to 1.0 .
    *
    * @param l the light component
    * @param a the green-magenta component
    * @param b the blue-yellow component
    *
    * @return this color
    */
   public Lab set ( final float l, final float a, final float b ) {

      return this.set(l, a, b, 1.0f);
   }

   /**
    * Sets the l, a, b and alpha color channels of this color.
    *
    * @param l     the light component
    * @param a     the green-magenta component
    * @param b     the blue-yellow component
    * @param alpha the alpha channel
    *
    * @return this color
    */
   public Lab set ( final float l, final float a, final float b,
      final float alpha ) {

      this.l = l;
      this.a = a;
      this.b = b;
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
   public Lab set ( final Lab source ) {

      return this.set(source.l, source.a, source.b, source.alpha);
   }

   /**
    * Sets a color with shorts. In Java, shorts are signed, within the range
    * [{@value Short#MIN_VALUE}, {@value Short#MAX_VALUE}] . The alpha channel
    * defaults to 1.0 .
    *
    * @param l the light component
    * @param a the green-magenta component
    * @param b the blue-yellow component
    *
    * @return this color
    */
   public Lab set ( final short l, final short a, final short b ) {

      return this.set(l, a, b, ( short ) -1);
   }

   /**
    * Sets a color with shorts. In Java, bytes are signed, within the range
    * [{@value Short#MIN_VALUE}, {@value Short#MAX_VALUE}] .
    *
    * @param l     the light component
    * @param a     the green-magenta component
    * @param b     the blue-yellow component
    * @param alpha the alpha channel
    *
    * @return this color
    */
   public Lab set ( final short l, final short a, final short b,
      final short alpha ) {

      /* @formatter:off */
      return this.set(
         ( l & 0xffff ) * Lab.L_FROM_SHORT,
         ((a & 0xffff) - 32768) * Lab.AB_FROM_SHORT,
         ((b & 0xffff) - 32768) * Lab.AB_FROM_SHORT,
         ( alpha & 0xffff ) / 65535.0f);
      /* @formatter:on */
   }

   /**
    * Converts a color to an integer where hexadecimal represents the color
    * components as 0xTTLLAABB, T being alpha. Defaults to modular arithmetic.
    *
    * @return the color in hexadecimal
    */
   @Override
   public int toHexInt ( ) {

      return this.toHexIntWrap();
   }

   /**
    * Converts a color to a 32-bit integer where hexadecimal represents the
    * color components as 0xTTLLAABB, T being alpha. Uses saturation
    * arithmetic, i.e., clamps the a and b components to [-127.5, 127.5],
    * floors, then and adds 128. Scales lightness from [0.0, 100.0] to [0,
    * 255]. Scales alpha from [0.0, 1.0] to [0, 255].
    *
    * @return the color in hexadecimal
    *
    * @see Utils#clamp(float, float, float)
    * @see Utils#clamp01(float)
    * @see Utils#floor(float)
    */
   @Override
   public int toHexIntSat ( ) {

      final int t8 = ( int ) ( Utils.clamp01(this.alpha) * 0xff + 0.5f );
      final int l8 = ( int ) ( Utils.clamp(this.l, 0.0f, 100.0f) * Lab.L_TO_BYTE
         + 0.5f );
      final int a8 = 128 + Utils.floor(Utils.clamp(this.a, -127.5f, 127.5f));
      final int b8 = 128 + Utils.floor(Utils.clamp(this.b, -127.5f, 127.5f));

      return t8 << 0x18 | l8 << 0x10 | a8 << 0x08 | b8;
   }

   /**
    * Converts a color to an integer where hexadecimal represents the color
    * components as 0xTTLLAABB, T being alpha. Uses modular arithmetic. Scales
    * lightness from [0.0, 100.0] to [0, 255]. Scales alpha from [0.0, 1.0] to
    * [0, 255]. Assumes a and b are in [-127.5, 127.5]; adds 128 to each.
    *
    * @return the color in hexadecimal
    *
    * @see Utils#floor(float)
    */
   @Override
   public int toHexIntWrap ( ) {

      final int t8 = ( int ) ( this.alpha * 0xff + 0.5f );
      final int l8 = ( int ) ( this.l * Lab.L_TO_BYTE + 0.5f );
      final int a8 = 128 + Utils.floor(this.a);
      final int b8 = 128 + Utils.floor(this.b);

      return t8 << 0x18 | l8 << 0x10 | a8 << 0x08 | b8;
   }

   /**
    * Converts a color to a 64-bit integer where hexadecimal represents the
    * color components as 0xTTTTLLLLAAAABBBB, T being alpha. Uses saturation
    * arithmetic.
    *
    * @return the color in hexadecimal
    *
    * @see Utils#clamp(float, float, float)
    * @see Utils#clamp01(float)
    * @see Utils#floor(float)
    */
   public long toHexLongSat ( ) {

      final long t16 = ( long ) ( Utils.clamp01(this.alpha) * 0xffff + 0.5f );
      final long l16 = ( long ) ( Utils.clamp(this.l, 0.0f, 100.0f)
         * Lab.L_TO_SHORT + 0.5f );
      final long a16 = 32768L + Utils.floor(Utils.clamp(this.a
         * Lab.AB_TO_SHORT, -32767.5f, 32767.5f));
      final long b16 = 32768L + Utils.floor(Utils.clamp(this.b
         * Lab.AB_TO_SHORT, -32767.5f, 32767.5f));

      return t16 << 0x30L | l16 << 0x20L | a16 << 0x10L | b16;
   }

   /**
    * Converts a color to a 64-bit integer where hexadecimal represents the
    * color components as 0xTTTTLLLLAAAABBBB, T being alpha. Uses modular
    * arithmetic.
    *
    * @return the color in hexadecimal
    *
    * @see Utils#clamp(float, float, float)
    * @see Utils#clamp01(float)
    * @see Utils#floor(float)
    */
   public long toHexLongWrap ( ) {

      final long t16 = ( long ) ( this.alpha * 0xffff + 0.5f );
      final long l16 = ( long ) ( this.l * Lab.L_TO_SHORT + 0.5f );
      final long a16 = 32768L + Utils.floor(this.a * Lab.AB_TO_SHORT);
      final long b16 = 32768L + Utils.floor(this.b * Lab.AB_TO_SHORT);

      return t16 << 0x30L | l16 << 0x20L | a16 << 0x10L | b16;
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

      sb.append("{\"l\":");
      Utils.toFixed(sb, this.l, places);
      sb.append(",\"a\":");
      Utils.toFixed(sb, this.a, places);
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
    * @param d the color
    *
    * @return the evaluation
    *
    * @see Lab#eqSatArith(Lab, Lab)
    */
   protected boolean equals ( final Lab d ) {

      return Lab.eqSatArith(this, d);
   }

   /**
    * A scalar used when converting a number in [0, 65535] to a and b
    * components.
    */
   public static final float AB_FROM_SHORT = 0.0038910506f;

   /**
    * A scalar to convert a and b components to a number in [0, 65535].
    */
   public static final float AB_TO_SHORT = 257.0f;

   /**
    * The default alpha scalar when finding the distance between two colors,
    * {@value Lab#DEFAULT_ALPHA_SCALAR}.
    */
   public static final float DEFAULT_ALPHA_SCALAR = 0.0f;

   /**
    * The default alpha scalar when finding the distance between two colors,
    * {@value Lab#DEFAULT_ALPHA_SCALAR_D}.
    */
   public static final double DEFAULT_ALPHA_SCALAR_D = 0.0d;

   /**
    * A scalar to convert a number in [0, 255] to lightness in [0.0, 100.0].
    * Equivalent to 100.0 / 255.0.
    */
   public static final float L_FROM_BYTE = 0.39215687f;

   /**
    * A scalar to convert a number in [0, 255] to lightness in [0.0, 100.0].
    * Equivalent to 100.0 / 65535.0.
    */
   public static final float L_FROM_SHORT = 0.0015259022f;

   /**
    * A scalar to convert lightness in [0.0, 100.0] to a number in [0, 255].
    * Equivalent to 255.0 / 100.0.
    */
   public static final float L_TO_BYTE = 2.55f;

   /**
    * A scalar to convert lightness in [0.0, 100.0] to a number in [0, 65535].
    * Equivalent to 65535.0 / 100.0.
    */
   public static final float L_TO_SHORT = 655.35f;

   /**
    * Maximum light used when generating a random color,
    * {@value Lab#RNG_L_MAX}.
    */
   public static final float RNG_L_MAX = 95.0f;

   /**
    * Minimum light used when generating a random color,
    * {@value Lab#RNG_L_MIN}.
    */
   public static final float RNG_L_MIN = 5.0f;

   /**
    * The maximum value on the green-magenta axis in SR LAB 2 for a color
    * converted from standard RGB.
    */
   public static final float SR_A_MAX = 104.49946f;

   /**
    * The minimum value on the green-magenta axis in SR LAB 2 for a color
    * converted from standard RGB.
    */
   public static final float SR_A_MIN = -82.955986f;

   /**
    * The maximum value on the blue-yellow axis in SR LAB 2 for a color
    * converted from standard RGB.
    */
   public static final float SR_B_MAX = 95.18662f;

   /**
    * The minimum value on the blue-yellow axis in SR LAB 2 for a color
    * converted from standard RGB.
    */
   public static final float SR_B_MIN = -110.8078f;

   /**
    * Assigns the alpha of the destination color to the origin.
    *
    * @param u      the under color
    * @param o      the over color
    * @param target the target color
    *
    * @return the color
    *
    * @see Math#sqrt(double)
    * @see Lab#gray(Lab, Lab)
    */
   public static final Lab adoptAlpha ( final Lab u, final Lab o,
      final Lab target ) {

      return target.set(u.l, u.a, u.b, o.alpha);
   }

   /**
    * Assigns the chroma of the destination color to the origin.
    *
    * @param u      the under color
    * @param o      the over color
    * @param target the target color
    *
    * @return the color
    *
    * @see Math#sqrt(double)
    * @see Lab#gray(Lab, Lab)
    */
   public static final Lab adoptChroma ( final Lab u, final Lab o,
      final Lab target ) {

      final double ua = u.a;
      final double ub = u.b;
      final double ucSq = ua * ua + ub * ub;
      if ( ucSq > IUtils.EPSILON_D ) {
         final double oa = o.a;
         final double ob = o.b;
         final double ocSq = oa * oa + ob * ob;

         final double s = Math.sqrt(ocSq) / Math.sqrt(ucSq);
         return target.set(u.l, ( float ) ( s * ua ), ( float ) ( s * ub ),
            u.alpha);
      }
      return Lab.gray(u, target);
   }

   /**
    * Assigns the hue of the destination color to the origin.
    *
    * @param u      the under color
    * @param o      the over color
    * @param target the target color
    *
    * @return the color
    *
    * @see Math#sqrt(double)
    * @see Lab#gray(Lab, Lab)
    */
   public static final Lab adoptHue ( final Lab u, final Lab o,
      final Lab target ) {

      final double oa = o.a;
      final double ob = o.b;
      final double ocSq = oa * oa + ob * ob;
      if ( ocSq > IUtils.EPSILON_D ) {
         final double ua = u.a;
         final double ub = u.b;
         final double ucSq = ua * ua + ub * ub;

         final double s = Math.sqrt(ucSq) / Math.sqrt(ocSq);
         return target.set(u.l, ( float ) ( s * oa ), ( float ) ( s * ob ),
            u.alpha);
      }
      return Lab.gray(u, target);
   }

   /**
    * Tests to see if a color's alpha and lightness are greater than zero.
    * Tests to see if its a and b components are not zero.
    *
    * @param o the color
    *
    * @return the evaluation
    */
   public static boolean all ( final Lab o ) {

      return o.alpha > 0.0f && o.l > 0.0f && o.a != 0.0f && o.b != 0.0f;
   }

   /**
    * Tests to see if the alpha channel of the color is greater than zero,
    * i.e. if it has some opacity.
    *
    * @param o the color
    *
    * @return the evaluation
    */
   public static boolean any ( final Lab o ) { return o.alpha > 0.0f; }

   /**
    * Returns the color black, ( 0.0, 0.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return black
    */
   public static final Lab black ( final Lab target ) {

      return target.set(0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Finds the chroma of a color.
    *
    * @param o the color
    *
    * @return the chroma
    *
    * @see Math#sqrt(double)
    */
   public static final float chroma ( final Lab o ) {

      final double ad = o.a;
      final double bd = o.b;
      return ( float ) Math.sqrt(ad * ad + bd * bd);
   }

   /**
    * Finds the chroma squared of a color.
    *
    * @param o the color
    *
    * @return the chroma squared
    */
   public static final float chromaSq ( final Lab o ) {

      final double ad = o.a;
      final double bd = o.b;
      return ( float ) ( ad * ad + bd * bd );
   }

   /**
    * Returns the color clear black, ( 0.0, 0.0, 0.0, 0.0 ) .
    *
    * @param target the output color
    *
    * @return clear black
    */
   public static final Lab clearBlack ( final Lab target ) {

      return target.set(0.0f, 0.0f, 0.0f, 0.0f);
   }

   /**
    * Returns the color clear white, ( 100.0, 0.0, 0.0, 0.0 ) .
    *
    * @param target the output color
    *
    * @return clear white
    */
   public static final Lab clearWhite ( final Lab target ) {

      return target.set(100.0f, 0.0f, 0.0f, 0.0f);
   }

   /**
    * Finds the distance between two colors. Uses the Euclidean distance of A
    * and B and the Manhattan distance for L and alpha. Alpha is scaled by
    * 100.0.
    *
    * @param o the left operand
    * @param d the right operand
    *
    * @return the distance
    */
   public static final float dist ( final Lab o, final Lab d ) {

      return Lab.dist(o, d, Lab.DEFAULT_ALPHA_SCALAR);
   }

   /**
    * Finds the distance between two colors. Uses the Euclidean distance of A
    * and B and the Manhattan distance for L and alpha. Alpha is scaled by
    * 100.0. Since the alpha range is less than that of the other channels, a
    * scalar is provided to increase its weight.
    *
    * @param o           the left operand
    * @param d           the right operand
    * @param alphaScalar the alpha scalar
    *
    * @return the distance
    *
    * @see Math#abs(double)
    * @see Math#sqrt(double)
    */
   public static final float dist ( final Lab o, final Lab d,
      final float alphaScalar ) {

      /*
       * https://github.com/svgeesus/svgeesus.github.io/blob/master/Color/OKLab-
       * notes.md
       */

      final double ca = d.a - o.a;
      final double cb = d.b - o.b;
      return ( float ) ( Math.abs(alphaScalar * ( d.alpha - o.alpha )) + Math
         .abs(d.l - o.l) + Math.sqrt(ca * ca + cb * cb) );
   }

   /**
    * Finds the Euclidean distance between two colors.
    *
    * @param o the left operand
    * @param d the right operand
    *
    * @return the distance
    *
    * @see Lab#distEuclidean(Lab, Lab, float)
    */
   public static final float distEuclidean ( final Lab o, final Lab d ) {

      return Lab.distEuclidean(o, d, Lab.DEFAULT_ALPHA_SCALAR);
   }

   /**
    * Finds the Euclidean distance between two colors. Since the alpha range
    * is less than that of the other channels, a scalar is provided to
    * increase its weight.
    *
    * @param o           the left operand
    * @param d           the right operand
    * @param alphaScalar the alpha scalar
    *
    * @return the distance
    *
    * @see Math#sqrt(double)
    */
   public static final float distEuclidean ( final Lab o, final Lab d,
      final float alphaScalar ) {

      final double ct = alphaScalar * ( d.alpha - o.alpha );
      final double cl = d.l - o.l;
      final double ca = d.a - o.a;
      final double cb = d.b - o.b;
      return ( float ) Math.sqrt(ct * ct + cl * cl + ca * ca + cb * cb);
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
   public static final boolean eqAlphaSatArith ( final Lab o, final Lab d ) {

      return ( int ) ( Utils.clamp01(o.alpha) * 0xff + 0.5f ) == ( int ) ( Utils
         .clamp01(d.alpha) * 0xff + 0.5f );
   }

   /**
    * Checks if two colors have equivalent l, a and b when converted to bytes
    * in [0, 255]. Uses saturation arithmetic.
    *
    * @param o the left comparisand
    * @param d the right comparisand
    *
    * @return the equivalence
    *
    * @see Utils#clamp(float, float, float)
    * @see Utils#clamp01(float)
    * @see Utils#floor(float)
    */
   public static boolean eqLabSatArith ( final Lab o, final Lab d ) {

      /* @formatter:off */
      return ( int ) ( Utils.clamp(o.l, 0.0f, 100.0f) * Lab.L_TO_BYTE + 0.5f )
          == ( int ) ( Utils.clamp(d.l, 0.0f, 100.0f) * Lab.L_TO_BYTE + 0.5f )
          && 128 + Utils.floor(Utils.clamp(o.a, -127.5f, 127.5f))
          == 128 + Utils.floor(Utils.clamp(d.a, -127.5f, 127.5f))
          && 128 + Utils.floor(Utils.clamp(o.b, -127.5f, 127.5f))
          == 128 + Utils.floor(Utils.clamp(d.b, -127.5f, 127.5f));
      /* @formatter:on */
   }

   /**
    * Checks if two colors have equivalent components when converted to bytes
    * in [0, 255]. Uses saturation arithmetic.
    *
    * @param o the left comparisand
    * @param d the right comparisand
    *
    * @return the equivalence
    *
    * @see Lab#eqAlphaSatArith(Lab, Lab)
    * @see Lab#eqLabSatArith(Lab, Lab)
    */
   public static boolean eqSatArith ( final Lab o, final Lab d ) {

      return Lab.eqAlphaSatArith(o, d) && Lab.eqLabSatArith(o, d);
   }

   /**
    * Flattens a two dimensional array of vectors to a one dimensional array.
    *
    * @param arr the 2D array
    *
    * @return the 1D array
    *
    * @see System#arraycopy(Object, int, Object, int, int)
    */
   public static final Lab[] flat ( final Lab[][] arr ) {

      final int sourceLen = arr.length;
      int totalLen = 0;
      for ( int i = 0; i < sourceLen; ++i ) { totalLen += arr[i].length; }

      /*
       * Copy each inner array to the result array, then move the cursor by the
       * length of each array.
       */
      int j = 0;
      final Lab[] result = new Lab[totalLen];
      for ( int i = 0; i < sourceLen; ++i ) {
         final Lab[] arrInner = arr[i];
         final int len = arrInner.length;
         System.arraycopy(arrInner, 0, result, j, len);
         j += len;
      }
      return result;
   }

   /**
    * Flattens a three dimensional array of vectors to a one dimensional
    * array.
    *
    * @param arr the 3D array
    *
    * @return the 1D array
    *
    * @see System#arraycopy(Object, int, Object, int, int)
    */
   public static final Lab[] flat ( final Lab[][][] arr ) {

      int totalLen = 0;
      final int sourceLen0 = arr.length;
      for ( int i = 0; i < sourceLen0; ++i ) {
         final Lab[][] arrInner = arr[i];
         final int sourceLen1 = arrInner.length;
         for ( int j = 0; j < sourceLen1; ++j ) {
            totalLen += arrInner[j].length;
         }
      }

      int k = 0;
      final Lab[] result = new Lab[totalLen];
      for ( int i = 0; i < sourceLen0; ++i ) {
         final Lab[][] arrInner1 = arr[i];
         final int sourceLen1 = arrInner1.length;
         for ( int j = 0; j < sourceLen1; ++j ) {
            final Lab[] arrInner2 = arrInner1[j];
            final int sourceLen2 = arrInner2.length;
            System.arraycopy(arrInner2, 0, result, k, sourceLen2);
            k += sourceLen2;
         }
      }

      return result;
   }

   /**
    * Converts a hexadecimal representation of a color stored as 0xTTLLAABB, T
    * being alpha.
    *
    * @param hex    the color in hexadecimal
    * @param target the output color
    *
    * @return the color
    */
   public static Lab fromHex ( final int hex, final Lab target ) {

      final int t8 = hex >> 0x18 & 0xff;
      final int l8 = hex >> 0x10 & 0xff;
      final int a8 = hex >> 0x08 & 0xff;
      final int b8 = hex & 0xff;

      /* @formatter:off */
      return target.set(
         l8 * Lab.L_FROM_BYTE,
         a8 - 128.0f,
         b8 - 128.0f,
         t8 * IUtils.ONE_255);
      /* @formatter:on */
   }

   /**
    * Converts an array of 32 bit integers that represent colors in
    * hexadecimal into an array of colors.
    *
    * @param hexes the colors
    *
    * @return the array
    *
    * @see Lab#fromHex(int, Lab)
    */
   public static Lab[] fromHex ( final int[] hexes ) {

      final int len = hexes.length;
      final Lab[] result = new Lab[len];
      for ( int i = 0; i < len; ++i ) {
         result[i] = Lab.fromHex(hexes[i], new Lab());
      }
      return result;
   }

   /**
    * Converts a hexadecimal representation of a color stored as
    * 0xTTTTLLLLAAAABBBB, T being alpha.
    *
    * @param hex    the color in hexadecimal
    * @param target the output color
    *
    * @return the color
    */
   public static Lab fromHex ( final long hex, final Lab target ) {

      final long t16 = hex >> 0x30L & 0xffffL;
      final long l16 = hex >> 0x20L & 0xffffL;
      final long a16 = hex >> 0x10L & 0xffffL;
      final long b16 = hex & 0xffffL;

      /* @formatter:off */
      return target.set(
         l16 * Lab.L_FROM_SHORT,
         ( a16 - 32768L ) * Lab.AB_FROM_SHORT,
         ( b16 - 32768L ) * Lab.AB_FROM_SHORT,
         t16 / 65535.0f);
      /* @formatter:on */
   }

   /**
    * Converts an array of 64 bit integers that represent colors in
    * hexadecimal into an array of colors.
    *
    * @param hexes the colors
    *
    * @return the array
    *
    * @see Lab#fromHex(int, Lab)
    */
   public static Lab[] fromHex ( final long[] hexes ) {

      final int len = hexes.length;
      final Lab[] result = new Lab[len];
      for ( int i = 0; i < len; ++i ) {
         result[i] = Lab.fromHex(hexes[i], new Lab());
      }
      return result;
   }

   /**
    * Creates a color in LAB from a color in LCH.
    *
    * @param l      the light component
    * @param c      the chroma
    * @param h      the hue
    * @param alpha  the alpha channel
    * @param target the target color
    *
    * @return the LAB color
    *
    * @see Math#max(double, double)
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static final Lab fromLch ( final float l, final float c,
      final float h, final float alpha, final Lab target ) {

      final double cd = Math.max(c, 0.0d);
      final double hd = h * IUtils.TAU_D;
      return target.set(l, ( float ) ( cd * Math.cos(hd) ), ( float ) ( cd
         * Math.sin(hd) ), alpha);
   }

   /**
    * Creates a color in LAB from a color in LCH.
    *
    * @param source the source color
    * @param target the target color
    *
    * @return the LAB color
    *
    * @see Lab#fromLch(float, float, float, float, Lab)
    */
   public static final Lab fromLch ( final Lch source, final Lab target ) {

      return Lab.fromLch(source.l, source.c, source.h, source.alpha, target);
   }

   /**
    * Converts from SR XYZ to SR LAB 2. Assumes that alpha is stored in the w
    * component. See <a href=
    * "https://www.magnetkern.de/srlab2.html">https://www.magnetkern.de/srlab2.html</a>.
    *
    * @param x      the x component
    * @param y      the y component
    * @param z      the z component
    * @param w      the alpha component
    * @param target the output color
    *
    * @return the color
    *
    * @see Math#pow(double, double)
    *
    * @author Jan Behrens
    */
   public static final Lab fromSrXyz ( final float x, final float y,
      final float z, final float w, final Lab target ) {

      double xd = x;
      double yd = y;
      double zd = z;

      final double comparisand = 216.0d / 24389.0d;
      final double scalar = 24389.0d / 2700.0d;

      /* @formatter:off */
      xd = xd <= comparisand ?
         xd * scalar :
         Math.pow(xd, IUtils.ONE_THIRD_D) * 1.16d - 0.16d;
      yd = yd <= comparisand ?
         yd * scalar :
         Math.pow(yd, IUtils.ONE_THIRD_D) * 1.16d - 0.16d;
      zd = zd <= comparisand ?
         zd * scalar :
         Math.pow(zd, IUtils.ONE_THIRD_D) * 1.16d - 0.16d;

      return target.set(
         ( float ) ( 37.095d * xd + 62.9054d * yd - 0.0008d * zd ),
         ( float ) ( 663.4684d * xd - 750.5078d * yd + 87.0328d * zd ),
         ( float ) ( 63.9569d * xd + 108.4576d * yd - 172.4152d * zd ),
         w);
      /* @formatter:on */
   }

   /**
    * Converts from SR XYZ to SR LAB 2. Assumes that alpha is stored in the w
    * component.
    *
    * @param v      the vector
    * @param target the output color
    *
    * @return the color
    *
    * @see Lab#fromSrXyz(float, float, float, float, Lab)
    */
   public static final Lab fromSrXyz ( final Vec4 v, final Lab target ) {

      return Lab.fromSrXyz(v.x, v.y, v.z, v.w, target);
   }

   /**
    * Returns the gray version of the color, where a and b are zero.
    *
    * @param o      the color
    * @param target the output color
    *
    * @return the gray color
    */
   public static final Lab gray ( final Lab o, final Lab target ) {

      return target.set(o.l, 0.0f, 0.0f, o.alpha);
   }

   /**
    * Generates a 3D array of colors.
    *
    * @param res the resolution
    *
    * @return the array
    *
    * @see Lab#grid(int, int, int)
    */
   public static final Lab[][][] grid ( final int res ) {

      return Lab.grid(res, res, res);
   }

   /**
    * Generates a 3D array of colors. The result is in layer-row-major order,
    * but the parameters are supplied in reverse: columns first, then rows,
    * then layers.
    *
    * @param cols   number of columns
    * @param rows   number of rows
    * @param layers number of layers
    *
    * @return the array
    *
    * @see Lab#grid(int, int, int, float, float, float, float, float, float,
    *      float)
    */
   public static final Lab[][][] grid ( final int cols, final int rows,
      final int layers ) {

      final float absa = Utils.max(Utils.abs(Lab.SR_A_MIN), Utils.abs(
         Lab.SR_A_MAX));
      final float absb = Utils.max(Utils.abs(Lab.SR_B_MIN), Utils.abs(
         Lab.SR_B_MAX));
      return Lab.grid(cols, rows, layers, 0.0f, -absa, -absb, 100.0f, absa,
         absb, 1.0f);
   }

   /**
    * Generates a 3D array of colors. The result is in layer-row-major order,
    * but the parameters are supplied in reverse: columns first, then rows,
    * then layers.
    *
    * @param cols       number of columns
    * @param rows       number of rows
    * @param layers     number of layers
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    *
    * @return the array
    *
    * @see Lab#grid(int, int, int, float, float, float, float, float, float,
    *      float)
    */
   public static final Lab[][][] grid ( final int cols, final int rows,
      final int layers, final Lab lowerBound, final Lab upperBound ) {

      return Lab.grid(cols, rows, layers, lowerBound.l, lowerBound.a,
         lowerBound.b, upperBound.l, upperBound.a, upperBound.b, 1.0f);
   }

   /**
    * Finds a color's hue.
    *
    * @param o the color
    *
    * @return the hue
    *
    * @see Math#atan2(double, double)
    */
   public static final float hue ( final Lab o ) {

      final double hueSigned = Math.atan2(o.b, o.a);
      final double hueUnsigned = hueSigned < 0.0d ? hueSigned + IUtils.TAU_D
         : hueSigned;
      return ( float ) ( hueUnsigned * IUtils.ONE_TAU_D );
   }

   /**
    * Finds the hue distance between two colors. When either color is gray,
    * returns 0.0. Otherwise, returns a value in the range [0.0, 0.5].
    *
    * @param o the first color
    * @param d the second color
    *
    * @return hue distance
    *
    * @see Math#acos(double)
    * @see Math#sqrt(double)
    */
   public static final float hueBetween ( final Lab o, final Lab d ) {

      final double oa = o.a;
      final double ob = o.b;
      final double ocSq = oa * oa + ob * ob;

      final double da = d.a;
      final double db = d.b;
      final double dcSq = da * da + db * db;

      if ( ocSq < IUtils.EPSILON_D || dcSq < IUtils.EPSILON_D ) { return 0.0f; }

      final double num = oa * da + ob * db;
      final double denom = Math.sqrt(ocSq) * Math.sqrt(dcSq);
      return ( float ) ( Math.acos(num / denom) / IUtils.TAU_D );
   }

   /**
    * Mixes two colors together by a step in [0.0, 1.0] .
    *
    * @param orig   the original color
    * @param dest   the destination color
    * @param step   the step
    * @param target the output color
    *
    * @return the mix
    */
   public static final Lab mix ( final Lab orig, final Lab dest,
      final float step, final Lab target ) {

      /* @formatter:off */
      final float u = 1.0f - step;
      return target.set(
         u * orig.l + step * dest.l,
         u * orig.a + step * dest.a,
         u * orig.b + step * dest.b,
         u * orig.alpha + step * dest.alpha);
      /* @formatter:on */
   }

   /**
    * Tests to see if the alpha channel of this color is less than or equal to
    * zero, i.e., if it is completely transparent.
    *
    * @param o the color
    *
    * @return the evaluation
    */
   public static boolean none ( final Lab o ) { return o.alpha <= 0.0f; }

   /**
    * Creates a random color. The light bounds are [{@value Lab#RNG_L_MIN},
    * {@value Lab#RNG_L_MAX}] . The a axis bounds are [{@value Lab#SR_A_MIN},
    * {@value Lab#SR_A_MAX}] . The b axis bounds are [{@value Lab#SR_B_MIN},
    * {@value Lab#SR_B_MAX}] . The alpha channel defaults to 1.0 .
    *
    * @param rng    the random number generator
    * @param target the output color
    *
    * @return the random color
    */
   public static Lab random ( final Random rng, final Lab target ) {

      final float rl = rng.nextFloat();
      final float ra = rng.nextFloat();
      final float rb = rng.nextFloat();

      /* @formatter:off */
      return target.set(
         ( 1.0f - rl ) * Lab.RNG_L_MIN + rl * Lab.RNG_L_MAX,
         ( 1.0f - ra ) * Lab.SR_A_MIN + ra * Lab.SR_A_MAX,
         ( 1.0f - rb ) * Lab.SR_B_MIN + rb * Lab.SR_B_MAX,
         1.0f);
      /* @formatter:on */
   }

   /**
    * Creates a random color given a lower and an upper bound.
    *
    * @param rng        the random number generator
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    * @param target     the output color
    *
    * @return the random color
    */
   public static Lab random ( final Random rng, final Lab lowerBound,
      final Lab upperBound, final Lab target ) {

      final float rl = rng.nextFloat();
      final float ra = rng.nextFloat();
      final float rb = rng.nextFloat();
      final float rt = rng.nextFloat();

      /* @formatter:off */
      return target.set(
         ( 1.0f - rl ) * lowerBound.l + rl * upperBound.l,
         ( 1.0f - ra ) * lowerBound.a + ra * upperBound.a,
         ( 1.0f - rb ) * lowerBound.b + rb * upperBound.b,
         ( 1.0f - rt ) * lowerBound.alpha + rt * upperBound.alpha);
      /* @formatter:on */
   }

   /**
    * Normalizes the color's a and b components, then multiplies by a scalar,
    * in effect setting the color's chroma. If the source chroma is near zero,
    * then returns the gray color.
    *
    * @param o      the source color
    * @param scalar the scalar
    * @param target the output color
    *
    * @return the scaled chroma color
    *
    * @see Math#sqrt(double)
    * @see Lab#gray(Lab, Lab)
    */
   public static final Lab rescaleChroma ( final Lab o, final float scalar,
      final Lab target ) {

      final double oa = o.a;
      final double ob = o.b;
      final double cSq = oa * oa + ob * ob;
      if ( cSq > IUtils.EPSILON_D ) {
         final double s = scalar / Math.sqrt(cSq);
         return target.set(o.l, ( float ) ( s * oa ), ( float ) ( s * ob ),
            o.alpha);
      }
      return Lab.gray(o, target);
   }

   /**
    * Rotates a color's a and b components. Accepts a normalized hue, or
    * angle, in [0.0, 1.0].
    *
    * @param o      the color
    * @param amount the hue rotation
    * @param target the output color
    *
    * @return the rotated hue
    *
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static final Lab rotateHue ( final Lab o, final float amount,
      final Lab target ) {

      final double ad = o.a;
      final double bd = o.b;
      final double hRad = amount * IUtils.TAU_D;
      final double cosad = Math.cos(hRad);
      final double sinad = Math.sin(hRad);
      return target.set(o.l, ( float ) ( cosad * ad - sinad * bd ),
         ( float ) ( cosad * bd + sinad * ad ), o.alpha);
   }

   /**
    * Multiplies the color's a and b components by a scalar.
    *
    * @param o      the color
    * @param scalar the scalar
    * @param target the output color
    *
    * @return the scaled color
    */
   public static final Lab scaleChroma ( final Lab o, final float scalar,
      final Lab target ) {

      return target.set(o.l, o.a * scalar, o.b * scalar, o.alpha);
   }

   /**
    * Returns the color blue in SR LAB 2.
    *
    * @param target the output color
    *
    * @return blue
    */
   public static Lab srBlue ( final Lab target ) {

      return target.set(30.64395f, -12.0258045f, -110.8078f, 1.0f);
   }

   /**
    * Returns the color cyan in SR LAB 2.
    *
    * @param target the output color
    *
    * @return cyan
    */
   public static Lab srCyan ( final Lab target ) {

      return target.set(90.6247f, -43.80204f, -15.009125f, 1.0f);
   }

   /**
    * Returns the color green in SR LAB 2.
    *
    * @param target the output color
    *
    * @return green
    */
   public static Lab srGreen ( final Lab target ) {

      return target.set(87.51519f, -82.95597f, 83.03678f, 1.0f);
   }

   /**
    * Returns the color magenta in SR LAB 2.
    *
    * @param target the output color
    *
    * @return magenta
    */
   public static Lab srMagenta ( final Lab target ) {

      return target.set(60.25521f, 102.67709f, -61.002052f, 1.0f);
   }

   /**
    * Returns the color red in SR LAB 2.
    *
    * @param target the output color
    *
    * @return red
    */
   public static Lab srRed ( final Lab target ) {

      return target.set(53.225975f, 78.204285f, 67.700615f, 1.0f);
   }

   /**
    * Returns the color yellow in SR LAB 2.
    *
    * @param target the output color
    *
    * @return yellow
    */
   public static Lab srYellow ( final Lab target ) {

      return target.set(97.34526f, -37.154266f, 95.18662f, 1.0f);
   }

   /**
    * Converts from SR LAB 2 to SR XYZ. The alpha component is assigned to w.
    * See <a href=
    * "https://www.magnetkern.de/srlab2.html">https://www.magnetkern.de/srlab2.html</a>.
    *
    * @param l      the lightness
    * @param a      the green-magenta component
    * @param b      the blue-yellow component
    * @param t      the alpha channel
    * @param target the output vector
    *
    * @return the vector
    *
    * @author Jan Behrens
    */
   public static final Vec4 toSrXyz ( final float l, final float a,
      final float b, final float t, final Vec4 target ) {

      final double ld = l * 0.01d;
      final double ad = a;
      final double bd = b;

      double x = ld + 0.000904127d * ad + 0.000456344d * bd;
      double y = ld - 0.000533159d * ad - 0.000269178d * bd;
      double z = ld - 0.0058d * bd;

      /* 2700.0 / 24389.0 = 0.11070564598795 */
      /* 1.0 / 1.16 = 0.86206896551724 */
      final double ltScale = 2700.0d / 24389.0d;
      final double gtScale = 1.0d / 1.16d;
      if ( x <= 0.08d ) {
         x *= ltScale;
      } else {
         x = ( x + 0.16d ) * gtScale;
         x = x * x * x;
      }

      if ( y <= 0.08d ) {
         y *= ltScale;
      } else {
         y = ( y + 0.16d ) * gtScale;
         y = y * y * y;
      }

      if ( z <= 0.08d ) {
         z *= ltScale;
      } else {
         z = ( z + 0.16d ) * gtScale;
         z = z * z * z;
      }

      return target.set(( float ) x, ( float ) y, ( float ) z, t);
   }

   /**
    * Converts from SR LAB 2 to SR XYZ. The alpha component is assigned to w.
    *
    * @param o      the color
    * @param target the output vector
    *
    * @return the vector
    */
   public static final Vec4 toSrXyz ( final Lab o, final Vec4 target ) {

      return Lab.toSrXyz(o.l, o.a, o.b, o.alpha, target);
   }

   /**
    * Returns the color white, ( 100.0, 0.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return white
    */
   public static final Lab white ( final Lab target ) {

      return target.set(100.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Converts a scalar to the the lightness and alpha channel. For use by the
    * gradient class.
    *
    * @param scalar the scalar
    * @param target the output color
    *
    * @return the color
    */
   static final Lab fromScalar ( final float scalar, final Lab target ) {

      final float s = Utils.clamp01(scalar);
      return target.set(s * 100.0f, 0.0f, 0.0f, s);
   }

   /**
    * Generates a 3D array of vectors. The result is in layer-row-major order,
    * but the parameters are supplied in reverse: columns first, then rows,
    * then layers.<br>
    * <br>
    * This is separated to make overriding the public grid functions easier.
    * This is protected because it is too easy for integers to be quietly
    * promoted to floats if the signature parameters are confused.
    *
    * @param cols   number of columns
    * @param rows   number of rows
    * @param layers number of layers
    * @param lbl    lower bound l
    * @param lba    lower bound a
    * @param lbb    lower bound b
    * @param ubl    upper bound l
    * @param uba    upper bound a
    * @param ubb    upper bound b
    * @param alpha  the alpha channel
    *
    * @return the array
    */
   protected static final Lab[][][] grid ( final int cols, final int rows,
      final int layers, final float lbl, final float lba, final float lbb,
      final float ubl, final float uba, final float ubb, final float alpha ) {

      final float tVrf = Utils.max(IUtils.ONE_255, alpha);
      final int lVrf = layers < 1 ? 1 : layers;
      final int rVrf = rows < 1 ? 1 : rows;
      final int cVrf = cols < 1 ? 1 : cols;

      final Lab[][][] result = new Lab[lVrf][rVrf][cVrf];

      final boolean lOne = lVrf == 1;
      final boolean rOne = rVrf == 1;
      final boolean cOne = cVrf == 1;

      final float hToStep = lOne ? 0.0f : 1.0f / ( lVrf - 1.0f );
      final float iToStep = rOne ? 0.0f : 1.0f / ( rVrf - 1.0f );
      final float jToStep = cOne ? 0.0f : 1.0f / ( cVrf - 1.0f );

      final float hOff = lOne ? 0.5f : 0.0f;
      final float iOff = rOne ? 0.5f : 0.0f;
      final float jOff = cOne ? 0.5f : 0.0f;

      final int rcVal = rVrf * cVrf;
      final int len = lVrf * rcVal;
      for ( int k = 0; k < len; ++k ) {
         final int h = k / rcVal;
         final int m = k - h * rcVal;
         final int i = m / cVrf;
         final int j = m % cVrf;

         final float hStep = h * hToStep + hOff;
         final float iStep = i * iToStep + iOff;
         final float jStep = j * jToStep + jOff;

         result[h][i][j] = new Lab( ( 1.0f - hStep ) * lbl + hStep * ubl, ( 1.0f
            - jStep ) * lba + jStep * uba, ( 1.0f - iStep ) * lbb + iStep * ubb,
            tVrf);
      }

      return result;
   }

   /**
    * An abstract class to facilitate the creation of color easing functions.
    */
   public abstract static class AbstrEasing implements Utils.EasingFuncObj <
      Lab > {

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
       * @param step   a factor in [0.0, 1.0]
       * @param target the output color
       *
       * @return the eased color
       */
      @Override
      public Lab apply ( final Lab orig, final Lab dest, final Float step,
         final Lab target ) {

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
      public abstract Lab applyUnclamped ( final Lab orig, final Lab dest,
         final Float step, final Lab target );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * An abstract class to facilitate the creation of harmony functions.
    */
   public abstract static class AbstrHarmony implements Function < Lab,
      Lab[] > {

      /**
       * The default constructor.
       */
      protected AbstrHarmony ( ) {}

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * Finds the analogous harmonies for the key color, plus and minus 30
    * degrees from the key hue. Returns an array containing 2 colors.
    */
   public static final class HarmonyAnalogous extends AbstrHarmony {

      /**
       * The default constructor.
       */
      public HarmonyAnalogous ( ) {}

      /**
       * Applies the function.
       *
       * @param o the key color
       *
       * @return the harmonies
       */
      @Override
      public Lab[] apply ( final Lab o ) {

         /* 30, 330 degrees */
         final float lAna = ( float ) ( ( o.l * 2.0d + 50.0d ) / 3.0d );
         final double ad = o.a;
         final double bd = o.b;
         final float t = o.alpha;

         final double rt32ca = IUtils.SQRT_3_2_D * ad;
         final double rt32cb = IUtils.SQRT_3_2_D * bd;
         final double halfca = 0.5d * ad;
         final double halfcb = 0.5d * bd;

         /* @formatter:off */
         return new Lab[] {
            new Lab(lAna, ( float ) ( rt32ca - halfcb ), ( float ) ( rt32cb + halfca ), t),
            new Lab(lAna, ( float ) ( rt32ca + halfcb ), ( float ) ( rt32cb - halfca ), t)
         };
         /* @formatter:on */
      }

   }

   /**
    * Finds the complementary harmony for the key color, 180 degrees from the
    * key hue. Returns an array containing 1 color.
    */
   public static final class HarmonyComplement extends AbstrHarmony {

      /**
       * The default constructor.
       */
      public HarmonyComplement ( ) {}

      /**
       * Applies the function.
       *
       * @param o the key color
       *
       * @return the harmonies
       */
      @Override
      public Lab[] apply ( final Lab o ) {

         /* @formatter:off */
         return new Lab[] {
            new Lab(( float ) ( 100.0d - o.l ), -o.a, -o.b, o.alpha)
         };
         /* @formatter:on */
      }

   }

   /**
    * Finds the split-analogous harmonies for the key color, plus and minus
    * 150 degrees from the key hue. Returns an array containing 2 colors.
    */
   public static final class HarmonySplit extends AbstrHarmony {

      /**
       * The default constructor.
       */
      public HarmonySplit ( ) {}

      /**
       * Applies the function.
       *
       * @param o the key color
       *
       * @return the harmonies
       */
      @Override
      public Lab[] apply ( final Lab o ) {

         /* 150, 210 degrees */
         final float lSpl = ( float ) ( ( 250.0d - o.l * 2.0d ) / 3.0d );
         final double ad = o.a;
         final double bd = o.b;
         final float t = o.alpha;

         final double rt32ca = -IUtils.SQRT_3_2_D * ad;
         final double rt32cb = -IUtils.SQRT_3_2_D * bd;
         final double halfca = 0.5d * ad;
         final double halfcb = 0.5d * bd;

         /* @formatter:off */
         return new Lab[] {
            new Lab(lSpl, ( float ) ( rt32ca - halfcb ), ( float ) ( rt32cb + halfca ), t),
            new Lab(lSpl, ( float ) ( rt32ca + halfcb ), ( float ) ( rt32cb - halfca ), t)
         };
         /* @formatter:on */
      }

   }

   /**
    * Finds the square harmonies for the key color, at 90, 180 and 270 degrees
    * away from the key hue. Returns an array containing 3 colors.
    */
   public static final class HarmonySquare extends AbstrHarmony {

      /**
       * The default constructor.
       */
      public HarmonySquare ( ) {}

      /**
       * Applies the function.
       *
       * @param o the key color
       *
       * @return the harmonies
       */
      @Override
      public Lab[] apply ( final Lab o ) {

         /* @formatter:off */
         return new Lab[] {
            new Lab(50.0f, -o.b, o.a, o.alpha),
            new Lab(( float ) ( 100.0d - o.l ), -o.a, -o.b, o.alpha),
            new Lab(50.0f, o.b, -o.a, o.alpha)
         };
         /* @formatter:on */
      }

   }

   /**
    * Finds the tetradic harmonies for the key color, at 120, 180 and 300
    * degrees from the key hue. Returns an array containing 3 colors.
    */
   public static final class HarmonyTetradic extends AbstrHarmony {

      /**
       * The default constructor.
       */
      public HarmonyTetradic ( ) {}

      /**
       * Applies the function.
       *
       * @param o the key color
       *
       * @return the harmonies
       */
      @Override
      public Lab[] apply ( final Lab o ) {

         /* 120, 300 degrees */
         final double ld = o.l;
         final double ad = o.a;
         final double bd = o.b;
         final float t = o.alpha;

         final double rt32ca = IUtils.SQRT_3_2_D * ad;
         final double rt32cb = IUtils.SQRT_3_2_D * bd;
         final double halfca = 0.5d * ad;
         final double halfcb = 0.5d * bd;

         /* @formatter:off */
         return new Lab[] {
            new Lab(
               ( float ) ( ( 200.0d - ld ) / 3.0d ),
               ( float ) ( -halfca - rt32cb ),
               ( float ) ( -halfcb + rt32ca ), t),
            new Lab(( float ) ( 100.0d - ld ), -o.a, -o.b, t),
            new Lab(
               ( float ) ( ( 100.0d + ld ) / 3.0d ),
               ( float ) ( halfca + rt32cb ),
               ( float ) ( halfcb - rt32ca ), t)
         };
         /* @formatter:on */
      }

   }

   /**
    * Finds the triadic harmonies for the key color, plus and minus 120
    * degrees from the key hue. Returns an array containing 2 colors.
    */
   public static final class HarmonyTriadic extends AbstrHarmony {

      /**
       * The default constructor.
       */
      public HarmonyTriadic ( ) {}

      /**
       * Applies the function.
       *
       * @param o the key color
       *
       * @return the harmonies
       */
      @Override
      public Lab[] apply ( final Lab o ) {

         /* 120, 240 degrees */
         final float lTri = ( float ) ( ( 200.0d - o.l ) / 3.0d );
         final double ad = o.a;
         final double bd = o.b;
         final float t = o.alpha;

         final double rt32ca = IUtils.SQRT_3_2_D * ad;
         final double rt32cb = IUtils.SQRT_3_2_D * bd;
         final double halfca = -0.5d * ad;
         final double halfcb = -0.5d * bd;

         /* @formatter:off */
         return new Lab[] {
            new Lab(lTri, ( float ) ( halfca - rt32cb ), ( float ) ( halfcb + rt32ca ), t),
            new Lab(lTri, ( float ) ( halfca + rt32cb ), ( float ) ( halfcb - rt32ca ), t)
         };
         /* @formatter:on */
      }

   }

   /**
    * Eases between two colors in the LAB color space.
    */
   public static final class MixLab extends AbstrEasing {

      /**
       * The default constructor.
       */
      public MixLab ( ) {}

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
      public Lab applyUnclamped ( final Lab orig, final Lab dest,
         final Float step, final Lab target ) {

         final float t = step;
         final float u = 1.0f - t;

         /* @formatter:off */
         return target.set(
            u * orig.l + t * dest.l,
            u * orig.a + t * dest.a,
            u * orig.b + t * dest.b,
            u * orig.alpha + t * dest.alpha);
         /* @formatter:on */
      }

   }

   /**
    * Eases between two colors in the LCH color space.
    */
   public static final class MixLch extends AbstrEasing {

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
      public MixLch ( ) { this(new IColor.HueNear()); }

      /**
       * Creates a color SR LCH mixing function with the given easing functions
       * for hue.
       *
       * @param hueFunc the hue easing function
       */
      public MixLch ( final IColor.HueEasing hueFunc ) {

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
       * @see Lab#fromLch(Lch, Lab)
       * @see Lch#fromLab(Lab, Lch)
       * @see Lch#mix(Lch, Lch, float, camzup.core.IColor.HueEasing, Lch)
       */
      @Override
      public Lab applyUnclamped ( final Lab orig, final Lab dest,
         final Float step, final Lab target ) {

         Lch.fromLab(orig, this.oLch);
         Lch.fromLab(dest, this.dLch);
         Lch.mix(this.oLch, this.dLch, step, this.hueFunc, this.cLch);
         Lab.fromLch(this.cLch, target);

         return target;
      }

   }

   /**
    * Eases between two colors in the standard RGB. Assumes that the LAB space
    * used is SR LAB 2.
    */
   public static final class MixSrgb extends AbstrEasing {

      /**
       * The mixed color in linear RGB.
       */
      protected final Rgb cLinear = new Rgb();

      /**
       * The mixed color in standard RGB.
       */
      protected final Rgb cStandard = new Rgb();

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
       * The destination color in linear RGB.
       */
      protected final Rgb dStandard = new Rgb();

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
       * The origin color in linear RGB.
       */
      protected final Rgb oStandard = new Rgb();

      /**
       * The origin color in XYZ.
       */
      protected final Vec4 oXyz = new Vec4();

      /**
       * The default constructor.
       */
      public MixSrgb ( ) {

         // TODO: Lrgb mix class? Use individual transformation steps.
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
      public Lab applyUnclamped ( final Lab orig, final Lab dest,
         final Float step, final Lab target ) {

         Rgb.srLab2TosRgb(orig, this.oStandard, this.oLinear, this.oXyz);
         Rgb.srLab2TosRgb(dest, this.dStandard, this.dLinear, this.dXyz);
         Rgb.mix(this.oStandard, this.dStandard, step, this.cStandard);
         Rgb.sRgbToSrLab2(this.cStandard, target, this.cXyz, this.cLinear);

         return target;
      }

   }

}
