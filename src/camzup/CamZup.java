package camzup;

import camzup.core.Color;
import camzup.core.Utils;

import processing.core.PApplet;

/**
 * The main class of this library. This is not needed to use the library
 * and is for development and debugging only.
 */
public class CamZup {

   /**
    * The PApplet referenced by this class.
    */
   public final PApplet parent;

   /**
    * Constructs a new instance of this library with the PApplet as a
    * reference.
    *
    * @param parent the parent applet
    */
   public CamZup ( final PApplet parent ) { this.parent = parent; }

   /**
    * Returns a string representation of the CamZup class.
    *
    * @return the string
    */
   @Override
   public String toString ( ) {

      final StringBuilder sb = new StringBuilder(64);
      sb.append("{ version: ");
      sb.append(CamZup.VERSION);
      sb.append(", parent: ");
      sb.append(this.parent);
      sb.append(" }");
      return sb.toString();
   }

   static Color blend ( final Color origin, final Color dest,
      final BlendMode op, final Color target ) {

      // Cr result color
      // B formula
      // Cs source color
      // Cb backdrop color
      // ab backdrop alpha
      // B the blending function
      // Cr = (1 - ab) * Cs + ab * B(Cb, Cs)

      switch ( op ) {
         case COLOR_BURN:
            // if(Cb == 1)
            // B(Cb, Cs) = 1
            // else if(Cs == 0)
            // B(Cb, Cs) = 0
            // else
            // B(Cb, Cs) = 1 - min(1, (1 - Cb) / Cs)
            break;

         case COLOR_DODGE:
            // if(Cb == 0)
            // B(Cb, Cs) = 0
            // else if(Cs == 1)
            // B(Cb, Cs) = 1
            // else
            // B(Cb, Cs) = min(1, Cb / (1 - Cs))
            break;

         case DARKEN:
            // B(Cb, Cs) = min(Cb, Cs)
            break;

         case DIFFERENCE:
            // B(Cb, Cs) = | Cb - Cs |
            break;

         case EXCLUSION:
            // B(Cb, Cs) = Cb + Cs - 2 x Cb x Cs
            break;

         case HARD_LIGHT:
            // if(Cs <= 0.5)
            // B(Cb, Cs) = Multiply(Cb, 2 x Cs)
            // else
            // B(Cb, Cs) = Screen(Cb, 2 x Cs -1)
            break;

         case LIGHTEN:
            // B(Cb, Cs) = max(Cb, Cs)
            break;

         case MULTIPLY:
            // B(Cb, Cs) = Cb x Cs
            break;

         case NORMAL:
            // B(Cb, Cs) = Cs
            break;

         case OVERLAY:
            // B(Cb, Cs) = HardLight(Cs, Cb)
            break;

         case SCREEN:
            // B(Cb, Cs) = 1 - [(1 - Cb) x (1 - Cs)]
            // = Cb + Cs -(Cb x Cs)
            break;

         case SOFT_LIGHT:
            // if(Cs <= 0.5)
            // B(Cb, Cs) = Cb - (1 - 2 x Cs) x Cb x (1 - Cb)
            // else
            // B(Cb, Cs) = Cb + (2 x Cs - 1) x (D(Cb) - Cb)
            // with
            // if(Cb <= 0.25)
            // D(Cb) = ((16 * Cb - 12) x Cb + 4) x Cb
            // else
            // D(Cb) = sqrt(Cb)
            break;

         default:
            break;

      }

      return target;
   }


   static Color composite ( final Color origin, final Color dest,
      final PorterDuff op,
      final Color target ) {

      float fa = 0.0f;
      float fb = 0.0f;
      float as = origin.a;
      float ab = dest.a;

      switch ( op ) {
         case CLEAR:
            return target.set(0.0f, 0.0f, 0.0f, 0.0f);

         case DEST:
            return Color.clamp01(dest, target);

         case DEST_ATOP:
            fa = 1.0f - ab;
            fb = as;
            break;

         case DEST_IN:
            fa = 0.0f;
            fb = as;
            break;

         case DEST_OUT:
            fa = 0.0f;
            fb = 1.0f - as;
            break;

         case DEST_OVER:
            fa = 1.0f - ab;
            fb = 1.0f;
            break;

         case PLUS:
            fa = 1.0f;
            fb = 1.0f;
            break;

         case SOURCE:
            return Color.clamp01(origin, target);

         case SOURCE_ATOP:
            fa = ab;
            fb = 1.0f - as;
            break;

         case SOURCE_IN:
            fa = ab;
            fb = 0.0f;
            break;

         case SOURCE_OUT:
            fa = 1.0f - ab;
            fb = 0.0f;
            break;

         case SOURCE_OVER:
            fa = 1.0f;
            fb = 1.0f - as;
            break;

         case XOR:
            fa = 1.0f - ab;
            fb = 1.0f - as;
            break;

         default:
            fa = 0.5f;
            fb = 0.5f;
      }

      /* @formatter:off */
      return target.set(
         Utils.clamp01(as * fa * origin.r + ab * fb * dest.r),
         Utils.clamp01(as * fa * origin.g + ab * fb * dest.g),
         Utils.clamp01(as * fa * origin.b + ab * fb * dest.b),
         Utils.clamp01(as * fa + ab * fb));
      /* @formatter:on */
   }

   /**
    * The library's current version.
    */
   public static final String VERSION = "##library.prettyVersion##";

   public static float charArrToFloat ( final char[] arr ) {

      final int len = arr.length;
      int dpidx = len;
      boolean negate = false;
      for ( int i = 0; i < len; ++i ) {
         final char c = arr[i];
         if ( c == '-' ) {
            negate = true;
         } else if ( c == '.' ) { dpidx = i; }
      }

      if ( negate ) { --dpidx; }

      float exponent = 1.0f;
      for ( int k = 1; k < dpidx; ++k ) { exponent *= 10.0f; }

      float result = 0.0f;
      for ( int j = 0; j < len; ++j ) {
         final int digit = arr[j] - '0';
         if ( digit > -1 && digit < 10 ) {
            result += exponent * digit;
            exponent *= 0.1f;
         }
      }

      return negate ? -result : result;
   }

   /**
    * The main function.
    *
    * @param args the string of arguments
    */
   public static void main ( final String[] args ) {

      // final Rng rng = new Rng();
      // for ( int i = 0; i < 40; ++i ) {
      // final float t = rng.uniform(-IUtils.PI, IUtils.PI);
      // final float p = rng.uniform(-IUtils.HALF_PI, IUtils.HALF_PI);
      // final Mat4 m = Mat4.fromSpherical(t, p, 1, new Mat4());
      //
      // final Vec4 right = new Vec4();
      // final Vec4 up = new Vec4();
      // final Vec4 forward = new Vec4();
      //
      // m.getCol(0, right);
      // m.getCol(1, up);
      // m.getCol(2, forward);
      //
      // final Vec3 r = new Vec3(right.x, right.y, right.z);
      // final Vec3 u = new Vec3(up.x, up.y, up.z);
      // final Vec3 f = new Vec3(forward.x, forward.y, forward.z);
      //
      // final Quaternion q = new Quaternion();
      // Quaternion.fromAxes(r, u, f, q);
      // // System.out.println("q: " + q);
      //
      // final Quaternion s = new Quaternion();
      // Quaternion.fromSpherical(t, p, s);
      // // System.out.println("s: " + s);
      //
      // // System.out.println(Quaternion.approx(q, s));
      // // System.out.println("");
      //
      // final Vec3 v = new Vec3();
      // Vec3.fromSpherical(t, p, 1f, v);
      // System.out.println(v);
      //
      // final Vec3 x = new Vec3(1, 0, 0);
      // Quaternion.mulVector(q, new Vec3(1, 0, 0), x);
      // System.out.println(x);
      //
      // System.out.println(Vec3.approx(x, v));
      // }
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

   enum PorterDuff {
      CLEAR, DEST, DEST_ATOP, DEST_IN, DEST_OUT, DEST_OVER, PLUS, SOURCE,
      SOURCE_ATOP, SOURCE_IN, SOURCE_OUT, SOURCE_OVER, XOR;

      PorterDuff ( ) {

      }
   }

   enum BlendMode {
      NORMAL, MULTIPLY, SCREEN, OVERLAY, DARKEN, LIGHTEN, COLOR_DODGE, COLOR_BURN, HARD_LIGHT, SOFT_LIGHT, DIFFERENCE, EXCLUSION;
      BlendMode() {}
   }

}
