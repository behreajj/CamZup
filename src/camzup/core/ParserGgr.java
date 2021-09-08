package camzup.core;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * A <a href="http://gimp.org/">GIMP</a> (GNU Image Manipulation Program)
 * gradient file (.ggr) parser class.
 */
public abstract class ParserGgr {

   /**
    * Private constructor for abstract class.
    */
   private ParserGgr ( ) {}

   /**
    * Gradient blend mode flag curved.
    */
   public static final int BLEND_CURVED = 1;

   /**
    * Gradient blend mode flag linear.
    */
   public static final int BLEND_LINEAR = 0;

   /**
    * Gradient blend mode flag sine.
    */
   public static final int BLEND_SINE = 2;

   /**
    * Gradient blend mode flag sphere decreasing.
    */
   public static final int BLEND_SPHERE_DECR = 4;

   /**
    * Gradient blend mode flag sphere increasing.
    */
   public static final int BLEND_SPHERE_INCR = 3;

   /**
    * Gradient color space hue saturation brightness, counter-clockwise.
    */
   public static final int SPACE_HSB_CCW = 1;

   /**
    * Gradient color space hue saturation brightness, clockwise.
    */
   public static final int SPACE_HSB_CW = 2;

   /**
    * Gradient color space red green blue.
    */
   public static final int SPACE_RGB = 0;

   /**
    * The natural logarithm of 0.5, approximately {@value ParserGgr#LOG_HALF}.
    */
   protected static final float LOG_HALF = -0.6931472f;

   /**
    * The natural logarithm of 0.5, approximately
    * {@value ParserGgr#LOG_HALF_D}.
    */
   protected static final double LOG_HALF_D = -0.6931471805599453d;

   /**
    * Parses a .ggr file containing a GIMP gradient. Given the differences
    * between the two implementations, this may not always return a matching
    * result, for example, when the source file contains only one key.<br>
    * <br>
    * This code is adapted to Java from the public domain Python of Ned
    * Batchelder. See <a href=
    * "https://nedbatchelder.com/code/modules/ggr.html">nedbatchelder.com/</a>
    * .
    *
    * @param in      the buffered reader
    * @param samples the number of samples
    *
    * @return the gradient
    *
    * @author Ned Batchelder
    */
   public static Gradient load ( final BufferedReader in, final int samples ) {

      final Gradient result = new Gradient();

      try {

         String[] tokens;
         final ArrayList < float[] > keys = new ArrayList <>(8);
         final Pattern spacePattern = Pattern.compile("\\s+");

         try {

            for ( String ln = in.readLine(); ln != null; ln = in.readLine() ) {
               final String lnlc = ln.trim().toLowerCase();
               if ( lnlc.indexOf('#') == 0 || lnlc.equals("gimp gradient") ) {
                  /* Skip. */
               } else {

                  /*
                   * The last two tokens are integer values that represent
                   * enumeration constants. They are promoted to floats here to
                   * avoid having to create a separate integer array.
                   */
                  tokens = spacePattern.split(lnlc, 0);
                  if ( tokens.length > 12 ) {
                     final float[] key = new float[13];
                     for ( int j = 0; j < 13; ++j ) {
                        key[j] = Float.parseFloat(tokens[j]);
                     }
                     keys.add(key);
                  }
               }
            }

         } catch ( final Exception e ) {
            e.printStackTrace();
         } finally {
            in.close();
         }

         /* Cache target keys, then clear out old keys. */
         final TreeSet < ColorKey > trgKeys = result.keys;
         trgKeys.clear();

         final int keyLen = keys.size();
         final float[][] keyArr = keys.toArray(new float[keyLen][13]);

         /* Temporary variables to store left and right edge of each key. */
         final Color ltClr = new Color();
         final Color rtClr = new Color();

         /* Mixers. */
         final Color.LerpSrgba rgbaMix = new Color.LerpSrgba();
         final Color.MixHsva cwMix = new Color.MixHsva(new Color.HueCW());
         final Color.MixHsva ccwMix = new Color.MixHsva(new Color.HueCCW());

         /* The GGR file is sampled rather than transferred one-to-one. */
         final int vSamp = Utils.clamp(samples, 3, 32);
         final float toStep = 1.0f / ( vSamp - 1.0f );
         for ( int k = 0; k < vSamp; ++k ) {

            /* Create new color key data. */
            final float step = k * toStep;
            float[] seg = keyArr[0];
            final Color evalClr = new Color();

            /*
             * Deal with boundary cases: when step is less than or equal to left
             * edge of first key or when step is greater than or equal to right
             * edge of the last key. This will likely amount to step <= 0.0 or
             * step >= 1.0 .
             */
            if ( step <= keyArr[0][0] ) {

               /* If less than lower bound, set to left color of key 0. */
               seg = keyArr[0];
               evalClr.set(seg[3], seg[4], seg[5], seg[6]);
               trgKeys.add(new ColorKey(step, evalClr));

            } else if ( step >= keyArr[keyLen - 1][2] ) {

               /* If greater than upper bound, set to right color of key - 1. */
               seg = keyArr[keyLen - 1];
               evalClr.set(seg[7], seg[8], seg[9], seg[10]);
               trgKeys.add(new ColorKey(step, evalClr));

            } else {

               /* Search for segment within which the step falls. */
               boolean segFound = false;
               for ( int m = 0; !segFound && m < keyLen; ++m ) {
                  seg = keyArr[m];
                  segFound = seg[0] <= step && step <= seg[2];
               }

               /* Cache the segment's left, mid and right steps. */
               final float segl = seg[0];
               final float segm = seg[1];
               final float segr = seg[2];

               /* Find normalized step. */
               final float denom = Utils.div(1.0f, segr - segl);
               final float mid = ( segm - segl ) * denom;
               final float pos = ( step - segl ) * denom;

               float fac = 0.5f;
               if ( pos <= mid ) {
                  fac = 0.5f * Utils.div(pos, mid);
               } else {
                  fac = 0.5f + 0.5f * Utils.div(pos - mid, 1.0f - mid);
               }

               /* Adjust step based on interpolation type. */
               final int blndFunc = ( int ) seg[11];
               switch ( blndFunc ) {

                  case ParserGgr.BLEND_CURVED: /* 1 */
                     final double logMid = Math.log(mid);
                     fac = ( float ) Math.pow(pos, logMid != 0.0d
                        ? ParserGgr.LOG_HALF_D / logMid : 0.0d);

                     break;

                  case ParserGgr.BLEND_SINE: /* 2 */

                     fac = 0.5f * ( ( float ) Math.sin(IUtils.PI * fac
                        - IUtils.HALF_PI) + 1.0f );

                     break;

                  case ParserGgr.BLEND_SPHERE_INCR: /* 3 */

                     fac -= 1.0f;
                     fac = Utils.sqrt(1.0f - fac * fac);

                     break;

                  case ParserGgr.BLEND_SPHERE_DECR: /* 4 */

                     fac = 1.0f - Utils.sqrt(1.0f - fac * fac);

                     break;

                  case ParserGgr.BLEND_LINEAR: /* 0 */
                  default:

               }

               /* Cache the segment's left and right colors. */
               ltClr.set(seg[3], seg[4], seg[5], seg[6]);
               rtClr.set(seg[7], seg[8], seg[9], seg[10]);

               /*
                * Hack for case in which colors are equal. The color
                * interpolation below does not recognize multiple revolutions
                * around the color wheel where hues are equal, so an epsilon
                * shift needs to take place.
                */
               int clrSpc = ( int ) seg[12];
               if ( ltClr.equals(rtClr) ) {
                  if ( clrSpc == ParserGgr.SPACE_HSB_CW ) {
                     Color.shiftHsva(ltClr, new Vec4(-IUtils.EPSILON, 0.0f,
                        0.0f, 0.0f), ltClr, new Vec4());
                     clrSpc = ParserGgr.SPACE_HSB_CCW;
                  } else if ( clrSpc == ParserGgr.SPACE_HSB_CCW ) {
                     Color.shiftHsva(ltClr, new Vec4(IUtils.EPSILON, 0.0f, 0.0f,
                        0.0f), ltClr, new Vec4());
                     clrSpc = ParserGgr.SPACE_HSB_CW;
                  }
               }

               /* Mix color based on color space. Default to RGB. */
               switch ( clrSpc ) {
                  case ParserGgr.SPACE_HSB_CCW: /* 1 */
                     ccwMix.apply(ltClr, rtClr, fac, evalClr);
                     break;

                  case ParserGgr.SPACE_HSB_CW: /* 2 */
                     cwMix.apply(ltClr, rtClr, fac, evalClr);
                     break;

                  case ParserGgr.SPACE_RGB: /* 0 */
                  default:
                     rgbaMix.apply(ltClr, rtClr, fac, evalClr);

               }

               /* Create key and add to the tree set. */
               trgKeys.add(new ColorKey(step, evalClr));
            }

         }

      } catch ( final Exception e ) {
         e.printStackTrace();
      }

      return result;
   }

   /**
    * Parses a .ggr file containing a GIMP gradient.
    *
    * @param fileName the file name
    * @param samples  the number of samples
    *
    * @return the gradient
    */
   public static Gradient load ( final String fileName, final int samples ) {

      Gradient result = new Gradient();
      try ( BufferedReader br = new BufferedReader(new FileReader(fileName)) ) {
         result = ParserGgr.load(br, samples);
      } catch ( final Exception e ) {
         e.printStackTrace();
      }
      return result;
   }

}
