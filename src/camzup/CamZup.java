package camzup;

import camzup.core.IUtils;
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

      // final Mesh3 m3 = new Mesh3();
      // Mesh3.uvSphere(16, 8, PolyType.QUAD, m3);
      // final MeshEntity3 me3 = new MeshEntity3(m3);
      // final String str = me3.toBlenderCode();
      // System.out.println(str);
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
