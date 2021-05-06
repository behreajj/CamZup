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

      // TODO: Add delta time or elapsed time.
      // https://github.com/processing/processing/issues/6070

      // final Mesh3 m3 = new Mesh3();
      // Mesh3.uvSphere(16, 8, PolyType.QUAD, m3);
      // final MeshEntity3 me3 = new MeshEntity3(m3);
      // final String str = me3.toBlenderCode();
      // System.out.println(str);

      // final Gradient ryb = new Gradient(0xff0000ff, 0xff3800ff, 0xff8000ff,
      // 0xffba00ff, 0xffde00ff, 0xffff00ff);
      // final String str = ryb.toSvgString("ryb", 0.0f, 0.5f, 1.0f, 0.5f, 768,
      // 64,
      // 1.0f / 2.2f, false);
      // System.out.println(1 / 2.2f);
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
