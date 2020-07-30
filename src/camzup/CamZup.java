package camzup;

import camzup.core.IUtils;
import camzup.core.Mesh3;
import camzup.core.MeshEntity3;
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

      final Mesh3 m = new Mesh3();
      capsule(32, 16, 0, 1.0f, 0.5f, m);
      // Mesh3.uvSphere(16, 8, m);
      final MeshEntity3 me = new MeshEntity3();
      me.append(m);
      final String pyCd = me.toBlenderCode();
      System.out.println(pyCd);
   }

   protected static Mesh3 capsule ( final int latitudes, final int longitudes,
      final int rings, final float depth, final float radius, Mesh3 target ) {
      
      final int lons = longitudes < 3 ? 3 : longitudes;
      final int lats = latitudes < 1 ? 1 : latitudes;
      final int midSections = rings < 0 ? 0 : rings;
      final float vDepth = Utils.max(IUtils.DEFAULT_EPSILON, depth);
      final float vRad = Utils.max(IUtils.DEFAULT_EPSILON, radius);

      return target;
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
