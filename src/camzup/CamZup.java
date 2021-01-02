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

      // final Mesh3 m30 = new Mesh3();
      // Mesh3.icosahedron(m30);
      // m30.insetExtrudeFaces(0.5f, 0.25f, 0.95f);
      // System.out.println(new MeshEntity3().append(m30).toBlenderCode());

      // final Mesh2 m20 = new Mesh2();
      // Mesh2.polygon(5, PolyType.NGON, m20);
      // m20.deleteVerts(1, 0, 3);
      // final MeshEntity2 me2 = new MeshEntity2("Test Obj");
      // me2.append(m20);
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
