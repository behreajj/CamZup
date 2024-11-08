package camzup.core;

import java.util.Random;

/**
 * A mutable, extensible color class that represents colors in a perceptual
 * color space, such as CIE LAB, SR LAB 2 or OK LAB. The <em>a</em> and
 * <em>b</em> axes are signed, unbounded values. Negative <em>a</em>
 * indicates a green hue; positive, magenta. Negative <em>b</em> indicates
 * a blue hue; positive, yellow. Lightness falls in the range [0.0, 100.0]
 * . For a and b, the practical range is roughly [-111.0, 111.0] . Alpha is
 * expected to be in [0.0, 1.0] .
 */
public class Lab implements Comparable < Lab > {

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
   public Lab ( ) {}

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
    * Returns -1 when this color is less than the comparisand; 1 when it is
    * greater than; 0 when the two are 'equal'. The implementation of this
    * method allows collections of colors to be sorted.
    *
    * @param d the comparisand
    *
    * @return the numeric code
    *
    * @see Lab#toHexIntWrap(Lab)
    */
   @Override
   public int compareTo ( final Lab d ) {

      final int left = Lab.toHexIntWrap(this);
      final int right = Lab.toHexIntWrap(d);
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
    * @see Lab#toHexIntWrap(Lab)
    */
   public boolean equals ( final int other ) {

      return Lab.toHexIntWrap(this) == other;
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
    *
    * @see Lab#toHexIntWrap(Lab)
    */
   @Override
   public int hashCode ( ) { return Lab.toHexIntWrap(this); }

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

      sb.append("{ l: ");
      Utils.toFixed(sb, this.l, places);
      sb.append(", a: ");
      Utils.toFixed(sb, this.a, places);
      sb.append(", b: ");
      Utils.toFixed(sb, this.b, places);
      sb.append(", alpha: ");
      Utils.toFixed(sb, this.alpha, places);
      sb.append(' ');
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
    * The default alpha scalar when finding the distance between two colors,
    * {@value Lab#DEFAULT_ALPHA_SCALAR}.
    */
   public static final float DEFAULT_ALPHA_SCALAR = 100.0f;

   /**
    * The default alpha scalar when finding the distance between two colors,
    * {@value Lab#DEFAULT_ALPHA_SCALAR_D}.
    */
   public static final double DEFAULT_ALPHA_SCALAR_D = 100.0d;

   /**
    * A scalar to convert a number in [0, 255] to lightness in [0.0, 100.0].
    * Equivalent to 100.0 / 255.0.
    */
   public static final float L_FROM_BYTE = 0.39215687f;

   /**
    * A scalar to convert lightness in [0.0, 100.0] to a number in [0, 255].
    * Equivalent to 255.0 / 100.0.
    */
   public static final float L_TO_BYTE = 2.55f;

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
   public static Lab black ( final Lab target ) {

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
   public static float chroma ( final Lab o ) {

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
   public static float chromaSq ( final Lab o ) {

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
   public static Lab clearBlack ( final Lab target ) {

      return target.set(0.0f, 0.0f, 0.0f, 0.0f);
   }

   /**
    * Returns the color clear white, ( 100.0, 0.0, 0.0, 0.0 ) .
    *
    * @param target the output color
    *
    * @return clear white
    */
   public static Lab clearWhite ( final Lab target ) {

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
    *
    * @see Math#abs(double)
    * @see Math#sqrt(double)
    */
   public static float dist ( final Lab o, final Lab d ) {

      /*
       * https://github.com/svgeesus/svgeesus.github.io/blob/master/Color/OKLab-
       * notes.md
       */

      final double da = d.a - o.a;
      final double db = d.b - o.b;
      return ( float ) ( Math.abs(Lab.DEFAULT_ALPHA_SCALAR_D * ( d.alpha
         - o.alpha )) + Math.abs(d.l - o.l) + Math.sqrt(da * da + db * db) );
   }

   /**
    * Finds the Euclidean distance between two colors. Includes the colors'
    * alpha channel in the calculation.
    *
    * @param o the left operand
    * @param d the right operand
    *
    * @return the distance
    *
    * @see Lab#distEuclideanAlpha(Lab, Lab, float)
    */
   public static float distEuclideanAlpha ( final Lab o, final Lab d ) {

      return Lab.distEuclideanAlpha(o, d, Lab.DEFAULT_ALPHA_SCALAR);
   }

   /**
    * Finds the Euclidean distance between two colors. Includes the colors'
    * alpha channel in the calculation. Since the alpha range is less than
    * that of the other channels, a scalar is provided to increase its weight.
    *
    * @param o           the left operand
    * @param d           the right operand
    * @param alphaScalar the alpha scalar
    *
    * @return the distance
    *
    * @see Math#sqrt(double)
    */
   public static float distEuclideanAlpha ( final Lab o, final Lab d,
      final float alphaScalar ) {

      final double dt = alphaScalar * ( d.alpha - o.alpha );
      final double dl = d.l - o.l;
      final double da = d.a - o.a;
      final double db = d.b - o.b;
      return ( float ) Math.sqrt(dt * dt + dl * dl + da * da + db * db);
   }

   /**
    * Finds the Euclidean distance between two colors. Does not include the
    * colors' alpha channel in the calculation.
    *
    * @param o the left operand
    * @param d the right operand
    *
    * @return the distance
    *
    * @see Math#sqrt(double)
    */
   public static float distEuclideanNoAlpha ( final Lab o, final Lab d ) {

      final double dl = d.l - o.l;
      final double da = d.a - o.a;
      final double db = d.b - o.b;
      return ( float ) Math.sqrt(dl * dl + da * da + db * db);
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
   public static boolean eqAlphaSatArith ( final Lab o, final Lab d ) {

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
   public static Lab[] flat ( final Lab[][] arr ) {

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
   public static Lab[] flat ( final Lab[][][] arr ) {

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

      final int t = hex >> 0x18 & 0xff;
      final int l = hex >> 0x10 & 0xff;
      final int a = hex >> 0x08 & 0xff;
      final int b = hex & 0xff;
      return target.set(l * Lab.L_FROM_BYTE, a - 128.0f, b - 128.0f, t
         * IUtils.ONE_255);
   }

   /**
    * Converts an array of integers that represent colors in hexadecimal into
    * an array of colors
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
    * Creates a color in LCH to a color in LAB.
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
   public static Lab fromLch ( final float l, final float c, final float h,
      final float alpha, final Lab target ) {

      final double cd = Math.max(0.0d, c);
      final double hd = h * IUtils.TAU_D;
      return target.set(l, ( float ) ( cd * Math.cos(hd) ), ( float ) ( cd
         * Math.sin(hd) ), alpha);
   }

   /**
    * Creates a color in LCH to a color in LAB.
    *
    * @param source the source color
    * @param target the target color
    *
    * @return the LAB color
    *
    * @see Lab#fromLch(float, float, float, float, Lab)
    */
   public static Lab fromLch ( final Lch source, final Lab target ) {

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
   public static Lab fromSrXyz ( final float x, final float y, final float z,
      final float w, final Lab target ) {

      double xd = x;
      double yd = y;
      double zd = z;

      final double comparisand = 216.0d / 24389.0d;
      final double scalar = 24389.0d / 2700.0d;

      xd = xd <= comparisand ? xd * scalar : Math.pow(xd, IUtils.ONE_THIRD_D)
         * 1.16d - 0.16d;
      yd = yd <= comparisand ? yd * scalar : Math.pow(yd, IUtils.ONE_THIRD_D)
         * 1.16d - 0.16d;
      zd = zd <= comparisand ? zd * scalar : Math.pow(zd, IUtils.ONE_THIRD_D)
         * 1.16d - 0.16d;

      return target.set(( float ) ( 37.0950d * xd + 62.9054d * yd - 0.0008d
         * zd ), ( float ) ( 663.4684d * xd - 750.5078d * yd + 87.0328d * zd ),
         ( float ) ( 63.9569d * xd + 108.4576d * yd - 172.4152d * zd ), w);
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
   public static Lab fromSrXyz ( final Vec4 v, final Lab target ) {

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
   public static Lab gray ( final Lab o, final Lab target ) {

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
   public static Lab[][][] grid ( final int res ) {

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
   public static Lab[][][] grid ( final int cols, final int rows,
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
   public static Lab[][][] grid ( final int cols, final int rows,
      final int layers, final Lab lowerBound, final Lab upperBound ) {

      return Lab.grid(cols, rows, layers, lowerBound.l, lowerBound.a,
         lowerBound.b, upperBound.l, upperBound.a, upperBound.b, 1.0f);
   }

   /**
    * Finds the analogous harmonies for the key color, plus and minus 30
    * degrees from the key hue. Returns an array containing 2 colors.
    *
    * @param o the key color
    *
    * @return the harmonies
    */
   public static Lab[] harmonyAnalogous ( final Lab o ) {

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

   /**
    * Finds the complementary harmony for the key color, 180 degrees from the
    * key hue. Returns an array containing 1 color.
    *
    * @param o the key color
    *
    * @return the harmony
    */
   public static Lab[] harmonyComplement ( final Lab o ) {

      /* @formatter:off */
      return new Lab[] {
         new Lab(( float ) ( 100.0d - o.l ), -o.a, -o.b, o.alpha)
      };
      /* @formatter:on */
   }

   /**
    * Finds the split-analogous harmonies for the key color, plus and minus
    * 150 degrees from the key hue. Returns an array containing 2 colors.
    *
    * @param o the key color
    *
    * @return the harmonies
    */
   public static Lab[] harmonySplit ( final Lab o ) {

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

   /**
    * Finds the square harmonies for the key color, at 90, 180 and 270 degrees
    * away from the key hue. Returns an array containing 3 colors.
    *
    * @param o the key color
    *
    * @return the harmonies
    */
   public static Lab[] harmonySquare ( final Lab o ) {

      /* @formatter:off */
      return new Lab[] {
         new Lab(50.0f, -o.b, o.a, o.alpha),
         new Lab(( float ) ( 100.0d - o.l ), -o.a, -o.b, o.alpha),
         new Lab(50.0f, o.b, -o.a, o.alpha)
      };
      /* @formatter:on */
   }

   /**
    * Finds the tetradic harmonies for the key color, at 120, 180 and 300
    * degrees from the key hue. Returns an array containing 3 colors.
    *
    * @param o the key color
    *
    * @return the harmonies
    */
   public static Lab[] harmonyTetradic ( final Lab o ) {

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

   /**
    * Finds the triadic harmonies for the key color, plus and minus 120
    * degrees from the key hue. Returns an array containing 2 colors.
    *
    * @param o the key color
    *
    * @return the harmonies
    */
   public static Lab[] harmonyTriadic ( final Lab o ) {

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

   /**
    * Finds a color's hue.
    *
    * @param o the color
    *
    * @return the hue
    *
    * @see Math#atan2(double, double)
    */
   public static float hue ( final Lab o ) {

      final double hueSigned = Math.atan2(o.b, o.a);
      final double hueUnsigned = hueSigned < 0.0d ? hueSigned + IUtils.TAU_D
         : hueSigned;
      return ( float ) ( hueUnsigned * IUtils.ONE_TAU_D );
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
   public static Lab mix ( final Lab orig, final Lab dest, final float step,
      final Lab target ) {

      final float u = 1.0f - step;
      return target.set(u * orig.l + step * dest.l, u * orig.a + step * dest.a,
         u * orig.b + step * dest.b, u * orig.alpha + step * dest.alpha);
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
      return target.set( ( 1.0f - rl ) * Lab.RNG_L_MIN + rl * Lab.RNG_L_MAX,
         ( 1.0f - ra ) * Lab.SR_A_MIN + ra * Lab.SR_A_MAX, ( 1.0f - rb )
            * Lab.SR_B_MIN + rb * Lab.SR_B_MAX, 1.0f);
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
      return target.set( ( 1.0f - rl ) * lowerBound.l + rl * upperBound.l,
         ( 1.0f - ra ) * lowerBound.a + ra * upperBound.a, ( 1.0f - rb )
            * lowerBound.b + rb * upperBound.b, ( 1.0f - rt ) * lowerBound.alpha
               + rt * upperBound.alpha);
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
   public static Lab rescaleChroma ( final Lab o, final float scalar,
      final Lab target ) {

      final double ad = o.a;
      final double bd = o.b;
      final double cSq = ad * ad + bd * bd;
      if ( cSq > IUtils.EPSILON_D ) {
         final double scInv = scalar / Math.sqrt(cSq);
         return target.set(o.l, ( float ) ( ad * scInv ), ( float ) ( bd
            * scInv ), o.alpha);
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
   public static Lab rotateHue ( final Lab o, final float amount,
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
   public static Lab scaleChroma ( final Lab o, final float scalar,
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
    * Converts a color to an integer where hexadecimal represents the color
    * components as 0xTTLLAABB, T being alpha. Defaults to modular arithmetic.
    *
    * @param o the input color
    *
    * @return the color in hexadecimal
    *
    * @see Lab#toHexIntWrap(Lab)
    */
   public static int toHexInt ( final Lab o ) {

      return Lab.toHexIntWrap(o);
   }

   /**
    * Converts a color to an integer where hexadecimal represents the color
    * components as 0xTTLLAABB, T being alpha. Uses saturation arithmetic,
    * i.e., clamps the a and b components to [-127.5, 127.5], floors, then and
    * adds 128. Scales lightness from [0.0, 100.0] to [0, 255]. Scales alpha
    * from [0.0, 1.0] to [0, 255].
    *
    * @param o the input color
    *
    * @return the color in hexadecimal
    *
    * @see Utils#clamp(float, float, float)
    * @see Utils#clamp01(float)
    * @see Utils#floor(float)
    */
   public static int toHexIntSat ( final Lab o ) {

      final int t = ( int ) ( Utils.clamp01(o.alpha) * 0xff + 0.5f );
      final int l = ( int ) ( Utils.clamp(o.l, 0.0f, 100.0f) * Lab.L_TO_BYTE
         + 0.5f );
      final int a = 128 + Utils.floor(Utils.clamp(o.a, -127.5f, 127.5f));
      final int b = 128 + Utils.floor(Utils.clamp(o.b, -127.5f, 127.5f));
      return t << 0x18 | l << 0x10 | a << 0x08 | b;
   }

   /**
    * Converts a color to an integer where hexadecimal represents the color
    * components as 0xTTLLAABB, T being alpha. Uses modular arithmetic. Scales
    * lightness from [0.0, 100.0] to [0, 255]. Scales alpha from [0.0, 1.0] to
    * [0, 255]. Assumes a and b are in [-127.5, 127.5]; adds 128 to each.
    *
    * @param o the input color
    *
    * @return the color in hexadecimal
    *
    * @see Utils#floor(float)
    */
   public static int toHexIntWrap ( final Lab o ) {

      final int t = ( int ) ( o.alpha * 0xff + 0.5f );
      final int l = ( int ) ( o.l * Lab.L_TO_BYTE + 0.5f );
      final int a = 128 + Utils.floor(o.a);
      final int b = 128 + Utils.floor(o.b);
      return t << 0x18 | l << 0x10 | a << 0x08 | b;
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
   public static Vec4 toSrXyz ( final float l, final float a, final float b,
      final float t, final Vec4 target ) {

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
   public static Vec4 toSrXyz ( final Lab o, final Vec4 target ) {

      return Lab.toSrXyz(o.l, o.a, o.b, o.alpha, target);
   }

   /**
    * Returns the color white, ( 100.0, 0.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return white
    */
   public static Lab white ( final Lab target ) {

      return target.set(100.0f, 0.0f, 0.0f, 1.0f);
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
   protected static Lab[][][] grid ( final int cols, final int rows,
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

}
