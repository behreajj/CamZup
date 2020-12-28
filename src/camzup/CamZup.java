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

      // final Mesh2 m20 = new Mesh2();
      // Mesh2.polygon(5, PolyType.QUAD, m20);
      //
      // final Mesh2 m21 = new Mesh2(m20);
      // m21.flipX();
      //
      // m21.translate(new Vec2(1.0f, 0.0f));
      // m20.translate(new Vec2(-1.0f, 0.0f));
      //
      // final MeshEntity2 me2 = new MeshEntity2("Test Obj");
      // me2.append(m20);
      // me2.append(m21);
      //
      // final String str = me2.toBlenderCode();
      // System.out.println(str);
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
