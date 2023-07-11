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

      // double maxDist = Double.MIN_VALUE;
      // for ( int i = 0; i < 256; ++i ) {
      // float b = i / 255.0f;
      // for ( int j = 0; j < 256; ++j ) {
      // float g = j / 255.0f;
      // for ( int k = 0; k < 256; ++k ) {
      // float r = k / 255.0f;
      // Color c = new Color(r, g, b, 1.0f);
      // Vec4 lab = Color.sRgbToSrLab2(c, new Vec4(), new Vec4(),
      // new Color());
      // double ad = lab.x;
      // double bd = lab.y;
      // double ld = lab.z;
      //
      // double dist = Math.abs(ld) + Math.sqrt(ad * ad + bd * bd);
      // if ( dist > maxDist ) maxDist = dist;
      // }
      // }
      // }
      //
      // System.out.println(maxDist);
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
