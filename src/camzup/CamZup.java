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

      /*
       * int len = 256 * 256 * 256; Vec4[] labs = new Vec4[len]; for ( int m =
       * 0, i = 0; i < 256; ++i ) { float b = i / 255.0f; for ( int j = 0; j <
       * 256; ++j ) { float g = j / 255.0f; for ( int k = 0; k < 256; ++k, ++m )
       * { float r = k / 255.0f; Color c = new Color(r, g, b, 1.0f); Vec4 lab =
       * Color.sRgbToSrLab2(c, new Vec4(), new Vec4(), new Color()); labs[m] =
       * lab; } } } double maxCylDist = Double.MIN_VALUE; for ( int i = 0; i <
       * len; ++i ) { Vec4 o = labs[i]; double oa = o.x; double ob = o.y; double
       * ol = o.z; for ( int j = i + 1; j < len; ++j ) { Vec4 d = labs[j];
       * double da = d.x; double db = d.y; double dl = d.z; double ca = oa - da;
       * double cb = ob - db; double cl = ol - dl; double cylDist = Math.abs(cl)
       * + Math.sqrt(ca * ca + cb * cb); if ( cylDist > maxCylDist ) {
       * maxCylDist = cylDist; } } } System.out.println(maxCylDist);
       */
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
