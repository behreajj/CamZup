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

   /**
    * The main function.
    *
    * @param args the string of arguments
    */

   public static void main ( final String[] args ) {

      // int[] a = new int[] { 0, 0xff_000000, 0xff_ff0000, 0xff_00ff00,
      // 0xff_000000, 0, 0 };
      // int[] a = new int[] { 0xff000000, 0, 0x00_000000, 0x00_ff0000,
      // 0x00_00ff00,
      // 0x00_000000, 0, 0, 0, 0, 0, 0 };
      // Vec2 dim = new Vec2();
      // int[] b = Pixels.trimAlpha(a, 4, 3, dim);
      // System.out.println("lenArr:" + b.length);
      // System.out.println("dim:" + dim);
      // for ( int i = 0; i < b.length; ++i ) {
      // System.out.println(Integer.toHexString(b[i]));
      // }
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
