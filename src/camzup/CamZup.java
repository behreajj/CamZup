package camzup;

import camzup.core.Color;
import camzup.core.IUtils;
import camzup.core.Utils;
import camzup.core.Vec3;

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

   /**
    * The library's current version.
    */
   public static final String VERSION = "##library.prettyVersion##";

   public static int[] bicubicResize3 ( final int[] src, final int srcWidth,
      final int srcHeight, final int destWidth, final int destHeight ) {

      final float tx = srcWidth / ( float ) destWidth;
      final float ty = srcHeight / ( float ) destHeight;

      final int rowStride = destWidth * 4;

      final int[] result = new int[rowStride * destHeight];
      final int[] krnl = new int[] { 0, 0, 0, 0 };

      final int len3 = destWidth * 16;
      final int len4 = destHeight * len3;

      for ( int k = 0; k < len4; ++k ) {
         final int g = k / len3; /* row index */
         final int m = k - g * len3; /* temporary */
         final int h = m / 16; /* column index */
         final int n = m - h * 16; /* temporary */
         final int i = n / 4; /* channel index */
         final int j = n % 4; /* kernel index */

         // row index
         final int y = ( int ) ( ty * g );
         final float dy = ty * g - y;
         final float dysq = dy * dy;

         // column index
         final int x = ( int ) ( tx * h );
         final float dx = tx * h - x;
         final float dxsq = dx * dx;

         int a0 = 0;
         int d0 = 0;
         int d2 = 0;
         int d3 = 0;

         // Bounds check z.
         final int z = y - 1 + j; // kernel index
         if ( z > -1 && z < srcHeight ) {
            final int zw = z * srcWidth;
            final int i8 = i * 8;

            final int xn1 = x - 1;
            final int xp1 = x + 1;
            final int xp2 = x + 2;

            // Bounds check x offsets.
            a0 = x > -1 && x < srcWidth ? src[zw + x] >> i8 & 0xff : 0;
            d0 = xn1 > -1 && xn1 < srcWidth ? src[zw + xn1] >> i8 & 0xff : 0;
            d2 = xp1 > -1 && xp1 < srcWidth ? src[zw + xp1] >> i8 & 0xff : 0;
            d3 = xp2 > -1 && xp2 < srcWidth ? src[zw + xp2] >> i8 & 0xff : 0;

            d0 -= a0;
            d2 -= a0;
            d3 -= a0;
         }

         final float d3_6 = IUtils.ONE_SIX * d3;

         // kernel index
         krnl[j] = Utils.clamp(( int ) ( a0 + ( -IUtils.ONE_THIRD * d0 + d2
            - d3_6 ) * dx + 0.5f * ( d0 + d2 ) * dxsq + ( -IUtils.ONE_SIX * d0
               - 0.5f * d2 + d3_6 ) * ( dx * dxsq ) ), 0, 255);

         d0 = krnl[0] - krnl[1];
         d2 = krnl[2] - krnl[1];

         final float d3_6_2 = IUtils.ONE_SIX * ( krnl[3] - krnl[1] );

         result[g * rowStride + h * 4 + i] = Utils.clamp(( int ) ( krnl[1]
            + ( -IUtils.ONE_THIRD * d0 + d2 - d3_6_2 ) * dy + 0.5f * ( d0 + d2 )
               * dysq + ( -IUtils.ONE_SIX * d0 - 0.5f * d2 + d3_6_2 ) * ( dy
                  * dysq ) ), 0, 255);
      }

      return result;
   }

   /**
    * The main function.
    *
    * @param args the string of arguments
    */
   public static void main ( final String[] args ) {

      // TODO: Add delta time or elapsed time.
      // https://github.com/processing/processing/issues/6070

      // Mesh2 m = Mesh2.plane(8, 3, PolyType.QUAD, new Mesh2());
      // MeshEntity2 me = new MeshEntity2(m);
      // String str = me.toBlenderCode();
      // System.out.println(str);
      // System.out.println(m);

      // Mesh3 m3 = new Mesh3();
      // Mesh3.torus(0.5f, 12, 8, PolyType.QUAD, m3);
      // MeshEntity3 me3 = new MeshEntity3(m3);
      // String str = me3.toBlenderCode();
      // System.out.println(str);

      // Vec4[][][][] grid = Vec4.grid(2, 2, 2, 2);
      // Vec4[] flat = Vec4.flat(grid);
      // String str = Vec4.toString(flat);
      // System.out.println(str);

      System.out.println(new Vec3(123.45f, 67.8901f, 234.567f).hashCode());
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

   static Color blend ( final Color origin, final Color dest,
      final BlendMode op, final Color target ) {

      // RESEARCH https://www.w3.org/TR/compositing-1/#blending

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
            return target.set(Utils.min(origin.r, dest.r), Utils.min(origin.g,
               dest.g), Utils.min(origin.b, dest.b), Utils.min(origin.a,
                  dest.a));

         case DIFFERENCE:
            return target.set(Utils.diff(origin.r, dest.r), Utils.diff(origin.g,
               dest.g), Utils.diff(origin.b, dest.b), Utils.diff(origin.a,
                  dest.a));

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
            return target.set(Utils.max(origin.r, dest.r), Utils.max(origin.g,
               dest.g), Utils.max(origin.b, dest.b), Utils.max(origin.a,
                  dest.a));

         case MULTIPLY:
            return target.set(origin.r * dest.r, origin.g * dest.g, origin.b
               * dest.b, origin.a * dest.a);

         case NORMAL:
            return target.set(dest);

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
      final PorterDuff op, final Color target ) {

      // RESEARCH https://www.w3.org/TR/compositing-1/#blending

      float fa;
      float fb;
      final float as = origin.a;
      final float ab = dest.a;

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

   enum BlendMode {
      COLOR_BURN, COLOR_DODGE, DARKEN, DIFFERENCE, EXCLUSION, HARD_LIGHT,
      LIGHTEN, MULTIPLY, NORMAL, OVERLAY, SCREEN, SOFT_LIGHT;

      BlendMode ( ) {}

   }

   enum PorterDuff {
      CLEAR, DEST, DEST_ATOP, DEST_IN, DEST_OUT, DEST_OVER, PLUS, SOURCE,
      SOURCE_ATOP, SOURCE_IN, SOURCE_OUT, SOURCE_OVER, XOR;

      PorterDuff ( ) {

      }

   }

}
