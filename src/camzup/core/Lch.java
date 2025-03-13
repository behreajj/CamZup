package camzup.core;

import java.util.Random;
import java.util.function.Function;

/**
 * A mutable, extensible color class that represents colors in the polar
 * form of a perceptual color space. Lightness falls in the range [0.0,
 * 100.0] . Hue is within the range [0.0, 1.0] . Chroma's minimum bound is
 * 0.0 but it has no upper bound. Alpha is expected to be in [0.0, 1.0] .
 */
public class Lch implements IColor {

   /**
    * The alpha channel, which governs transparency.
    */
   public float alpha = 1.0f;

   /**
    * The chroma, or vividness of the hue.
    */
   public float c = 0.0f;

   /**
    * The hue, normalized to [0.0, 1.0].
    */
   public float h = Lch.SR_HUE_LIGHT;

   /**
    * The light component.
    */
   public float l = 100.0f;

   /**
    * The default constructor. Creates a white color.
    */
   public Lch ( ) {}

   /**
    * Creates a color from bytes In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] . The alpha channel
    * defaults to 1.0 .
    *
    * @param l the light component
    * @param c the chroma
    * @param h the hue
    */
   public Lch ( final byte l, final byte c, final byte h ) {

      this.set(l, c, h);
   }

   /**
    * Creates a color from bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param l     the light component
    * @param c     the chroma
    * @param h     the hue
    * @param alpha the alpha channel
    */
   public Lch ( final byte l, final byte c, final byte h, final byte alpha ) {

      this.set(l, c, h, alpha);
   }

   /**
    * Creates a color from lightness, chroma and hue. The alpha channel
    * defaults to 1.0 .
    *
    * @param l the light component
    * @param c the chroma
    * @param h the hue
    */
   public Lch ( final float l, final float c, final float h ) {

      this.set(l, c, h);
   }

   /**
    * Creates a color from lightness, chroma, hue and alpha.
    *
    * @param l     the light component
    * @param c     the chroma
    * @param h     the hue
    * @param alpha the alpha channel
    */
   public Lch ( final float l, final float c, final float h,
      final float alpha ) {

      this.set(l, c, h, alpha);
   }

   /**
    * Creates a color from a source.
    *
    * @param source the source color
    */
   public Lch ( final Lch source ) {

      this.set(source);
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
    * @see Lch#equals(Lch)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Lch ) obj);
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
   public Lch reset ( ) {

      return this.set(100.0f, 0.0f, Lch.SR_HUE_LIGHT, 1.0f);
   }

   /**
    * Sets a color with bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] . The alpha channel
    * defaults to 1.0 .
    *
    * @param l the light component
    * @param c the chroma
    * @param h the hue
    *
    * @return this color
    */
   public Lch set ( final byte l, final byte c, final byte h ) {

      return this.set(l, c, h, ( byte ) -1);
   }

   /**
    * Creates a color from bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param l     the light component
    * @param c     the chroma
    * @param h     the hue
    * @param alpha the alpha channel
    *
    * @return this color
    */
   public Lch set ( final byte l, final byte c, final byte h,
      final byte alpha ) {

      /* @formatter:off */
      return this.set(
         ( l & 0xff ) * Lab.L_FROM_BYTE,
         ( c & 0xff ) * Lch.C_FROM_BYTE,
         ( h & 0xff ) * Lch.H_FROM_BYTE,
         ( alpha & 0xff ) * IUtils.ONE_255);
      /* @formatter:on */
   }

   /**
    * Sets the l, c, h and alpha color channels of this color. The alpha
    * channel defaults to 1.0 .
    *
    * @param l the light component
    * @param c the chroma
    * @param h the hue
    *
    * @return this color
    */
   public Lch set ( final float l, final float c, final float h ) {

      return this.set(l, c, h, 1.0f);
   }

   /**
    * Sets the l, c, h and alpha color channels of this color.
    *
    * @param l     the light component
    * @param c     the chroma
    * @param h     the hue
    * @param alpha the alpha channel
    *
    * @return this color
    */
   public Lch set ( final float l, final float c, final float h,
      final float alpha ) {

      this.l = l;
      this.c = c;
      this.h = h;
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
   public Lch set ( final Lch source ) {

      return this.set(source.l, source.c, source.h, source.alpha);
   }

   /**
    * Converts a color to an integer where hexadecimal represents the color
    * components as 0xTTLLCCHH, T being alpha. Defaults to modular arithmetic.
    *
    * @return the color in hexadecimal
    */
   @Override
   public int toHexInt ( ) {

      return this.toHexIntWrap();
   }

   /**
    * Converts a color to an integer where hexadecimal represents the color
    * components as 0xTTLLCCHH, T being alpha. Uses saturation arithmetic.
    * Scales lightness from [0.0, 100.0] to [0, 255]. Scales hue and alpha
    * from [0.0, 1.0] to [0, 255].
    *
    * @return the color in hexadecimal
    *
    * @see Utils#clamp(float, float, float)
    * @see Utils#clamp01(float)
    * @see Utils#mod1(float)
    */
   @Override
   public int toHexIntSat ( ) {

      final int t8 = ( int ) ( Utils.clamp01(this.alpha) * 0xff + 0.5f );
      final int l8 = ( int ) ( Utils.clamp(this.l, 0.0f, 100.0f) * Lab.L_TO_BYTE
         + 0.5f );
      final int c8 = ( int ) ( Utils.clamp(this.c * Lch.C_TO_BYTE, 0.0f, 254.5f)
         + 0.5f );
      final int h8 = ( int ) ( Utils.mod1(this.h) * Lch.H_TO_BYTE + 0.5f );

      return t8 << 0x18 | l8 << 0x10 | c8 << 0x08 | h8;
   }

   /**
    * Converts a color to an integer where hexadecimal represents the color
    * components as 0xTTLLCCHH, T being alpha. Uses modular arithmetic. Scales
    * lightness from [0.0, 100.0] to [0, 255]. Scales hue and alpha from [0.0,
    * 1.0] to [0, 255]. Rounds chroma to an int; assumes it is less than 255.
    *
    * @return the color in hexadecimal
    */
   @Override
   public int toHexIntWrap ( ) {

      final int t8 = ( int ) ( this.alpha * 0xff + 0.5f );
      final int l8 = ( int ) ( this.l * Lab.L_TO_BYTE + 0.5f );
      final int c8 = ( int ) ( this.c * Lch.C_TO_BYTE + 0.5f );
      final int h8 = ( int ) ( this.h * Lch.H_TO_BYTE + 0.5f );

      return t8 << 0x18 | l8 << 0x10 | c8 << 0x08 | h8;
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
      sb.append(",\"c\":");
      Utils.toFixed(sb, this.c, places);
      sb.append(",\"h\":");
      Utils.toFixed(sb, this.h, places);
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
    * @see Lch#eqSatArith(Lch, Lch)
    */
   protected boolean equals ( final Lch d ) {

      return Lch.eqSatArith(this, d);
   }

   /**
    * A scalar to convert a number in [0, 255] to chroma,
    * {@value Lch#C_FROM_BYTE}.
    */
   public static final float C_FROM_BYTE = 0.5f;

   /**
    * A scalar to convert chroma, roughly, to a number in [0, 255],
    * {@value Lch#C_TO_BYTE}.
    */
   public static final float C_TO_BYTE = 2.0f;

   /**
    * A scalar to convert a number in [0, 255] to hue,
    * {@value Lch#H_FROM_BYTE}.
    */
   public static final float H_FROM_BYTE = IUtils.ONE_255;

   /**
    * A scalar to convert hue from [0.0, 1.0] to a number in [0, 255],
    * {@value Lch#H_TO_BYTE}.
    */
   public static final float H_TO_BYTE = 255.0f;

   /**
    * Maximum chroma used when generating a random color,
    * {@value Lch#RNG_C_MAX}.
    */
   public static final float RNG_C_MAX = Lch.SR_CHROMA_MAX;

   /**
    * Minimum chroma used when generating a random color,
    * {@value Lch#RNG_C_MIN}.
    */
   public static final float RNG_C_MIN = 5.0f;

   /**
    * The smallest non-zero chroma in SR LCH for a color converted from
    * standard RGB.
    */
   public static final float SR_CHROMA_EPSILON = 0.0010125733f;

   /**
    * The maximum chroma in SR LCH for a color converted from standard RGB.
    */
   public static final float SR_CHROMA_MAX = 119.431305f;

   /**
    *  The average chroma in SR LCH for a color converted from standard RGB.
    */
   public static final float SR_CHROMA_MEAN = 56.141006f;

   /**
    * Arbitrary hue in SR LCH assigned to colors with no saturation that are
    * closer to light, {@value Lch#SR_HUE_LIGHT}. Defaults to a yellow.
    */
   public static final float SR_HUE_LIGHT = 0.3092284f;

   /**
    * Arbitrary hue in SR LCH assigned to colors with no saturation that are
    * closer to shadow, {@value Lch#SR_HUE_SHADE}. Defaults to a violet.
    */
   public static final float SR_HUE_SHADE = 0.8092284f;

   /**
    * Tests to see if a color's alpha, lightness and chroma are greater than
    * zero.
    *
    * @param o the color
    *
    * @return the evaluation
    */
   public static boolean all ( final Lch o ) {

      return o.alpha > 0.0f && o.l > 0.0f && o.c > 0.0f;
   }

   /**
    * Tests to see if the alpha channel of the color is greater than zero,
    * i.e. if it has some opacity.
    *
    * @param o the color
    *
    * @return the evaluation
    */
   public static boolean any ( final Lch o ) { return o.alpha > 0.0f; }

   /**
    * Returns the color black.
    *
    * @param target the output color
    *
    * @return black
    */
   public static final Lch black ( final Lch target ) {

      return target.set(0.0f, 0.0f, Lch.SR_HUE_SHADE, 1.0f);
   }

   /**
    * Returns the color clear black.
    *
    * @param target the output color
    *
    * @return clear black
    */
   public static final Lch clearBlack ( final Lch target ) {

      return target.set(0.0f, 0.0f, Lch.SR_HUE_SHADE, 0.0f);
   }

   /**
    * Returns the color clear white.
    *
    * @param target the output color
    *
    * @return clear white
    */
   public static final Lch clearWhite ( final Lch target ) {

      return target.set(100.0f, 0.0f, Lch.SR_HUE_LIGHT, 0.0f);
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
   public static final boolean eqAlphaSatArith ( final Lch o, final Lch d ) {

      return ( int ) ( Utils.clamp01(o.alpha) * 0xff + 0.5f ) == ( int ) ( Utils
         .clamp01(d.alpha) * 0xff + 0.5f );
   }

   /**
    * Checks if two colors have equivalent l, c and h when converted to bytes
    * in [0, 255]. Uses saturation arithmetic.
    *
    * @param o the left comparisand
    * @param d the right comparisand
    *
    * @return the equivalence
    *
    * @see Utils#clamp(float, float, float)
    * @see Utils#clamp01(float)
    * @see Utils#mod1(float)
    */
   public static boolean eqLchSatArith ( final Lch o, final Lch d ) {

      /* @formatter:off */
      return ( int ) ( Utils.clamp(o.l, 0.0f, 100.0f) * Lab.L_TO_BYTE + 0.5f )
          == ( int ) ( Utils.clamp(d.l, 0.0f, 100.0f) * Lab.L_TO_BYTE + 0.5f )
          && ( int ) ( Utils.clamp(o.c * Lch.C_TO_BYTE, 0.0f, 254.5f) + 0.5f )
          == ( int ) ( Utils.clamp(d.c * Lch.C_TO_BYTE, 0.0f, 254.5f) + 0.5f )
          && ( int ) ( Utils.mod1(o.h) * Lch.H_TO_BYTE + 0.5f )
          == ( int ) ( Utils.mod1(d.h) * Lch.H_TO_BYTE + 0.5f );
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
    * @see Lch#eqAlphaSatArith(Lch, Lch)
    * @see Lch#eqLchSatArith(Lch, Lch)
    */
   public static boolean eqSatArith ( final Lch o, final Lch d ) {

      return Lch.eqAlphaSatArith(o, d) && Lch.eqLchSatArith(o, d);
   }

   /**
    * Converts a hexadecimal representation of a color stored as 0xTTLLCCHH, T
    * being alpha.
    *
    * @param hex    the color in hexadecimal
    * @param target the output color
    *
    * @return the color
    */
   public static Lch fromHex ( final int hex, final Lch target ) {

      final int t = hex >> 0x18 & 0xff;
      final int l = hex >> 0x10 & 0xff;
      final int c = hex >> 0x08 & 0xff;
      final int h = hex & 0xff;
      return target.set(l * Lab.L_FROM_BYTE, c * Lch.C_FROM_BYTE, h
         * Lch.H_FROM_BYTE, t * IUtils.ONE_255);
   }

   /**
    * Converts an array of integers that represent colors in hexadecimal into
    * an array of colors
    *
    * @param hexes the colors
    *
    * @return the array
    *
    * @see Lch#fromHex(int, Lch)
    */
   public static Lch[] fromHex ( final int[] hexes ) {

      final int len = hexes.length;
      final Lch[] result = new Lch[len];
      for ( int i = 0; i < len; ++i ) {
         result[i] = Lch.fromHex(hexes[i], new Lch());
      }
      return result;
   }

   /**
    * Creates a color in LCH from a color in LAB.
    *
    * @param l      the light component
    * @param a      the green-magenta component
    * @param b      the blue-yellow component
    * @param alpha  the alpha channel
    * @param target the target color
    *
    * @return the LCH color
    *
    * @see Math#atan2(double, double)
    * @see Math#sqrt(double)
    * @see Utils#mod1(float)
    */
   public static final Lch fromLab ( final float l, final float a,
      final float b, final float alpha, final Lch target ) {

      final double ad = a;
      final double bd = b;

      final double chrSq = ad * ad + bd * bd;
      if ( chrSq < IUtils.EPSILON_D ) {
         final float t = l * 0.01f;
         final float hGray = Utils.mod1( ( 1.0f - t ) * Lch.SR_HUE_SHADE + t
            * ( Lch.SR_HUE_LIGHT + 1.0f ));
         return target.set(l, 0.0f, hGray, alpha);
      }

      final double hueSigned = Math.atan2(bd, ad);
      final double hueUnsigned = hueSigned < 0.0d ? hueSigned + IUtils.TAU_D
         : hueSigned;

      return target.set(l, ( float ) Math.sqrt(chrSq), ( float ) ( hueUnsigned
         * IUtils.ONE_TAU_D ), alpha);
   }

   /**
    * Creates a color in LCH from a color in LAB.
    *
    * @param source the source color
    * @param target the target color
    *
    * @return the LCH color
    *
    * @see Lch#fromLab(float, float, float, float, Lch)
    */
   public static final Lch fromLab ( final Lab source, final Lch target ) {

      return Lch.fromLab(source.l, source.a, source.b, source.alpha, target);
   }

   /**
    * Returns the gray version of the color, where the chroma is set to zero.
    *
    * @param o      the color
    * @param target the output color
    *
    * @return the gray color
    *
    * @see Utils#mod1(float)
    */
   public static final Lch gray ( final Lch o, final Lch target ) {

      final float t = o.l * 0.01f;
      return target.set(o.l, 0.0f, Utils.mod1( ( 1.0f - t ) * Lch.SR_HUE_SHADE
         + t * ( Lch.SR_HUE_LIGHT + 1.0f )), o.alpha);
   }

   /**
    * Mixes two colors together by a step in [0.0, 1.0] .
    *
    * @param orig   the original color
    * @param dest   the destination color
    * @param step   the step
    * @param easing the hue easing function
    * @param target the output color
    *
    * @return the mix
    */
   public static final Lch mix ( final Lch orig, final Lch dest,
      final float step, final IColor.HueEasing easing, final Lch target ) {

      final float u = 1.0f - step;
      final float cl = u * orig.l + step * dest.l;
      final float calpha = u * orig.alpha + step * dest.alpha;

      final boolean oIsGray = orig.c < IUtils.EPSILON;
      final boolean dIsGray = dest.c < IUtils.EPSILON;
      if ( oIsGray && dIsGray ) {
         final float t = cl * 0.01f;
         return target.set(cl, 0.0f, Utils.mod1( ( 1.0f - t ) * Lch.SR_HUE_SHADE
            + t * ( Lch.SR_HUE_LIGHT + 1.0f )), calpha);
      }

      if ( oIsGray || dIsGray ) {
         double oa = 0.0d;
         double ob = 0.0d;
         if ( !oIsGray ) {
            final double ohRadians = orig.h * IUtils.TAU_D;
            final double oChroma = orig.c;
            oa = oChroma * Math.cos(ohRadians);
            ob = oChroma * Math.sin(ohRadians);
         }

         double da = 0.0d;
         double db = 0.0d;
         if ( !dIsGray ) {
            final double dhRadians = dest.h * IUtils.TAU_D;
            final double dChroma = dest.c;
            da = dChroma * Math.cos(dhRadians);
            db = dChroma * Math.sin(dhRadians);
         }

         final double stepd = step;
         final double ud = 1.0d - stepd;
         final double ca = ud * oa + stepd * da;
         final double cb = ud * ob + stepd * db;

         final double cc = Math.sqrt(ca * ca + cb * cb);

         double ch = Math.atan2(cb, ca);
         ch = ch < -0.0d ? ch + IUtils.TAU_D : ch;
         ch *= IUtils.ONE_TAU_2_D;

         return target.set(cl, ( float ) cc, ( float ) ch, calpha);
      }

      return target.set(cl, u * orig.c + step * dest.c, easing.apply(orig.h,
         dest.h, step), calpha);
   }

   /**
    * Tests to see if the alpha channel of this color is less than or equal to
    * zero, i.e., if it is completely transparent.
    *
    * @param o the color
    *
    * @return the evaluation
    */
   public static boolean none ( final Lch o ) { return o.alpha <= 0.0f; }

   /**
    * Creates a random color. The light bounds are [{@value Lab#RNG_L_MIN},
    * {@value Lab#RNG_L_MAX}] . The alpha channel defaults to 1.0 .
    *
    * @param rng    the random number generator
    * @param target the output color
    *
    * @return the random color
    */
   public static Lch random ( final Random rng, final Lch target ) {

      final float rl = rng.nextFloat();
      final float rc = rng.nextFloat();
      final float rh = rng.nextFloat();
      return target.set( ( 1.0f - rl ) * Lab.RNG_L_MIN + rl * Lab.RNG_L_MAX,
         ( 1.0f - rc ) * Lch.RNG_C_MIN + rc * Lch.RNG_C_MAX, rh, 1.0f);
   }

   /**
    * Returns the color blue in SR LCH.
    *
    * @param target the output color
    *
    * @return blue
    */
   public static Lch srBlue ( final Lch target ) {

      return target.set(30.64395f, 111.458466f, 0.73279446f, 1.0f);
   }

   /**
    * Returns the color cyan in SR LCH.
    *
    * @param target the output color
    *
    * @return cyan
    */
   public static Lch srCyan ( final Lch target ) {

      return target.set(90.6247f, 46.30219f, 0.5525401f, 1.0f);
   }

   /**
    * Returns the color green in SR LCH.
    *
    * @param target the output color
    *
    * @return green
    */
   public static Lch srGreen ( final Lch target ) {

      return target.set(87.51519f, 117.37461f, 0.3749225f, 1.0f);
   }

   /**
    * Returns the color magenta in SR LCH.
    *
    * @param target the output color
    *
    * @return magenta
    */
   public static Lch srMagenta ( final Lch target ) {

      return target.set(60.25521f, 119.431305f, 0.91468f, 1.0f);
   }

   /**
    * Returns the color red in SR LCH.
    *
    * @param target the output color
    *
    * @return red
    */
   public static Lch srRed ( final Lch target ) {

      return target.set(53.225975f, 103.43735f, 0.1135622f, 1.0f);
   }

   /**
    * Returns the color yellow in SR LCH.
    *
    * @param target the output color
    *
    * @return yellow
    */
   public static Lch srYellow ( final Lch target ) {

      return target.set(97.34526f, 102.18088f, 0.30922842f, 1.0f);
   }

   /**
    * Returns the color white.
    *
    * @param target the output color
    *
    * @return white
    */
   public static final Lch white ( final Lch target ) {

      return target.set(100.0f, 0.0f, Lch.SR_HUE_LIGHT, 1.0f);
   }

   /**
    * An abstract class to facilitate the creation of harmony functions.
    */
   public abstract static class AbstrHarmony implements Function < Lch,
      Lch[] > {

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
      public Lch[] apply ( final Lch o ) {

         final float lAna = ( o.l * 2.0f + 50.0f ) / 3.0f;

         /*
          * Since the intent is to provide hue harmonies, this doesn't seem like
          * a great solution. For many cases, all colors in the array would be
          * the same.
          */
         final float h30 = o.h + 0.083333333f;
         final float h330 = o.h - 0.083333333f;

         /*
          * if(o.c < IUtils.EPSILON) { final float t = lAna * 0.01f; h30 = h330
          * = ( 1.0f - t ) * Lch.SR_HUE_SHADE + t * ( Lch.SR_HUE_LIGHT + 1.0f );
          * }
          */

         /* @formatter:off */
         return new Lch[] {
            new Lch(lAna, o.c, Utils.mod1(h30), o.alpha),
            new Lch(lAna, o.c, Utils.mod1(h330), o.alpha)
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
      public Lch[] apply ( final Lch o ) {

         final float lCmp = 100.0f - o.l;

         final float h180 = o.h + 0.5f;

         return new Lch[] { new Lch(lCmp, o.c, Utils.mod1(h180), o.alpha) };
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
      public Lch[] apply ( final Lch o ) {

         final float lSpl = ( 250.0f - o.l * 2.0f ) / 3.0f;

         final float h150 = o.h + 0.41666667f;
         final float h210 = o.h - 0.41666667f;

         /* @formatter:off */
         return new Lch[] {
            new Lch(lSpl, o.c, Utils.mod1(h150), o.alpha),
            new Lch(lSpl, o.c, Utils.mod1(h210), o.alpha)
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
      public Lch[] apply ( final Lch o ) {

         final float lCmp = 100.0f - o.l;

         final float h90 = o.h + 0.25f;
         final float h180 = o.h + 0.5f;
         final float h270 = o.h - 0.25f;

         /* @formatter:off */
         return new Lch[] {
            new Lch(50.0f, o.c, Utils.mod1(h90), o.alpha),
            new Lch(lCmp, o.c, Utils.mod1(h180), o.alpha),
            new Lch(50.0f, o.c, Utils.mod1(h270), o.alpha)
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
      public Lch[] apply ( final Lch o ) {

         final float lTri = ( 200.0f - o.l ) / 3.0f;
         final float lCmp = 100.0f - o.l;
         final float lTet = ( 100.0f + o.l ) / 3.0f;

         final float h120 = o.h + IUtils.ONE_THIRD;
         final float h180 = o.h + 0.5f;
         final float h300 = o.h - IUtils.ONE_SIX;

         /* @formatter:off */
         return new Lch[] {
            new Lch(lTri, o.c, Utils.mod1(h120), o.alpha),
            new Lch(lCmp, o.c, Utils.mod1(h180), o.alpha),
            new Lch(lTet, o.c, Utils.mod1(h300), o.alpha) };
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
      public Lch[] apply ( final Lch o ) {

         final float lTri = ( 200.0f - o.l ) / 3.0f;

         final float h120 = o.h + IUtils.ONE_THIRD;
         final float h240 = o.h - IUtils.ONE_THIRD;

         /* @formatter:off */
         return new Lch[] {
            new Lch(lTri, o.c, Utils.mod1(h120), o.alpha),
            new Lch(lTri, o.c, Utils.mod1(h240), o.alpha)
         };
         /* @formatter:on */
      }

   }

}
