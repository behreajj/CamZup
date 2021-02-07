package camzup;

import camzup.core.IUtils;
import camzup.core.Mat4;
import camzup.core.Quaternion;
import camzup.core.Rng;
import camzup.core.Vec3;
import camzup.core.Vec4;

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

      // Mesh3 uvSphere = new Mesh3();
      // Mesh3.uvSphere(uvSphere);
      // MeshEntity3 me3 = new MeshEntity3(uvSphere);
      // String pycd = me3.toBlenderCode();
      // System.out.println(pycd);

      // Vec3[] arr = Vec3.flat(Vec3.gridSpherical(12, 3));
      // System.out.println(Vec3.toString(arr, 3));

      // CurveSphere cs = new CurveSphere();
      // Rng rng = new Rng();
      // int x = rng.nextInt(16);
      // int y = rng.nextInt(8);
      // CurveSphere.helix(x, y, 0.0f, -IUtils.HALF_PI, IUtils.HALF_PI, cs);

      // Mat4 m = Mat4.fromSpherical(IUtils.HALF_PI * 0.5f, 0f, 1f,
      // new Mat4());
      //

      //
      // Quaternion q = new Quaternion();
      // Quaternion.fromAxes(r, u, f, q);

      // double cosa = 0.5f * Math.cos(IUtils.HALF_PI * 0.5f);
      // double sina = 0.5f * Math.sin(IUtils.HALF_PI * 0.5f);
      // System.out.println(cosa);
      // System.out.println(sina);
      // System.out.println(q);

      System.out.println();

      final Rng rng = new Rng();
      for ( int i = 0; i < 40; ++i ) {
         final float t = rng.uniform(-IUtils.PI, IUtils.PI);
         final float p = rng.uniform(-IUtils.HALF_PI, IUtils.HALF_PI);
         final Mat4 m = Mat4.fromSpherical(t, p, 1, new Mat4());

         final Vec4 right = new Vec4();
         final Vec4 up = new Vec4();
         final Vec4 forward = new Vec4();

         m.getCol(0, right);
         m.getCol(1, up);
         m.getCol(2, forward);

         final Vec3 r = new Vec3(right.x, right.y, right.z);
         final Vec3 u = new Vec3(up.x, up.y, up.z);
         final Vec3 f = new Vec3(forward.x, forward.y, forward.z);

         final Quaternion q = new Quaternion();
         Quaternion.fromAxes(r, u, f, q);
         // System.out.println("q: " + q);

         final Quaternion s = new Quaternion();
         Quaternion.fromSpherical(t, p, s);
         // System.out.println("s: " + s);

         // System.out.println(Quaternion.approx(q, s));
         // System.out.println("");

         final Vec3 v = new Vec3();
         Vec3.fromSpherical(t, p, 1f, v);
         System.out.println(v);

         final Vec3 x = new Vec3(1, 0, 0);
         Quaternion.mulVector(q, new Vec3(1, 0, 0), x);
         System.out.println(x);

         System.out.println(Vec3.approx(x, v));
      }
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
