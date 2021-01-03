package camzup;

import camzup.core.Curve2;
import camzup.core.Mesh2;
import camzup.core.MeshEntity2;
import camzup.core.Rng;

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

      Mesh2 m20 = new Mesh2();
      MeshEntity2 me20 = new MeshEntity2(m20);

      Curve2 c20 = new Curve2();
      Rng rng = new Rng();
      Curve2.random(rng, 6, -1f, 1f, true, c20);

      Mesh2.fromCurve2(c20, 6, .001f, m20);

      String pyCd = me20.toBlenderCode();
      // String pyCd = new CurveEntity2().append(c20).toBlenderCode();
      System.out.println(pyCd);
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
