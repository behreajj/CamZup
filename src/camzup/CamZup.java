package camzup;

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
   public final static String VERSION = "##library.prettyVersion##";

   /**
    * The main function.
    *
    * @param args the string of arguments
    */
   public static void main ( final String[] args ) {
      // final Color a = new Color(0.8f, 0.6f, 0.4f, 0.2f);
      // new Color(0.9f, 0.1f, 0.3f, 0.5f);
      // final Color target = new Color();
      //
      // Color.fromHex(~Color.toHexInt(a), target);
      //
      // System.out.println(target);
      // System.out.println(Color.bitNot(a, target));

      // final Rng rng = new Rng();
      // final Vec3 dir = Vec3.random(rng, new Vec3());
      // System.out.println(dir);
      // new Transform3();

      System.out.println(Utils.floor(0.0f));
      System.out.println(Utils.floor(-0.0f));
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
