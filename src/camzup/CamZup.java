package camzup;

import camzup.core.Img;
import camzup.core.PngParser;
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

      /* @formatter:off */
      return new StringBuilder(64)
         .append("{ version: ")
         .append(CamZup.VERSION)
         .append(", parent: ")
         .append(this.parent)
         .append(" }")
         .toString();
      /* @formatter:on */
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

      final Img img = new Img();
      PngParser.parsePng("data/diagnostic2.png", img);

      System.out.println(Utils.toString(PngParser.sigIdatBytes()));

   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
