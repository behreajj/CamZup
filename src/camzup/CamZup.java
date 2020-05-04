package camzup;

import camzup.core.Mesh3;

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
   public final static String VERSION = "##library.prettyVersion##";

   /**
    * The main function.
    *
    * @param args the string of arguments
    */
   public static void main ( final String[] args ) {

      // new Rng();

      // for ( int i = 0; i < 10; ++i ) {
      // float var12 = rng.nextFloat();
      // float var45 = var12 - 1.0F + 0.5527864F;
      // float var46 = var12 + x;
      // System.out.println(var45);
      // System.out.println(var46);
      // }

      // final Img img = new Img();
      // PngParser.parsePng("data/diagnostic.png", img);

      Mesh3 m = new Mesh3();
      Mesh3.tetrahedron(m);
      float[] b = Mesh3.floatArrCoords(m);
      System.out.println(Integer.toHexString(Float.floatToIntBits(-0.0f)));
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
