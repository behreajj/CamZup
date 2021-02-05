package camzup;

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

      // Mesh3 mesh = new Mesh3();
      // Mesh3.icosphere(mesh);
      // MeshEntity3 me3 = new MeshEntity3(mesh);
      // String pycd = me3.toBlenderCode();
      // System.out.println(pycd);

      // Mesh2 mesh = new Mesh2();
      // Mesh2.gridHex(5, 0.5f, 0.1f, mesh);
      // MeshEntity2 me2 = new MeshEntity2(mesh);
      // String pycd = me2.toBlenderCode();
      // System.out.println(pycd);

      // Quaternion.Slerp mixer = new Quaternion.Slerp();
      //
      // Quaternion a = new Quaternion();
      // Quaternion b = new Quaternion();
      // Quaternion c = new Quaternion();
      // Quaternion d = new Quaternion();
      // Rng rng = new Rng();
      // for ( int i = 0; i < 20; ++i ) {
      // Quaternion.random(rng, a);
      // Quaternion.random(rng, b);
      // Quaternion.mul(a, rng.uniform(-5, 5), a);
      // Quaternion.mul(b, rng.uniform(1, 6), b);
      // float t = rng.nextFloat();
      //
      // Quaternion.mix(a, b, t, c);
      // mixer.apply(a, b, t, d);
      //
      //
      // boolean valid = Quaternion.approx(c, d, 0.00001f);
      // if ( !valid ) {
      // System.out.println("CONTROL:");
      // System.out.println(d.real);
      // System.out.println(d.imag.x);
      // System.out.println(d.imag.y);
      // System.out.println(d.imag.z);
      //
      // System.out.println("\nTEST:");
      // System.out.println(c.real);
      // System.out.println(c.imag.x);
      // System.out.println(c.imag.y);
      // System.out.println(c.imag.z);
      // System.out.println("");
      // }
      //
      //
      // }
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
