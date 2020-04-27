package camzup;

import camzup.core.SvgParser;

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

      // Rng rng = new Rng();

      SvgParser.parse("data/diagnostic.svg");

      // final Mesh3 m1 = Mesh3.tetrahedron(new Mesh3());
      // final Mesh3 m2 = Mesh3.cube(new Mesh3());
      // final Mesh3 m3 = Mesh3.octahedron(new Mesh3());
      // final Mesh3 m4 = Mesh3.dodecahedron(new Mesh3());
      // final MeshEntity3 me3 = new MeshEntity3().appendAll(m1, m2, m3, m4);
      // System.out.println(me3);

      // final String circle = "circle";
      // final String ellipse = "ellipse";
      // final String line = "line";
      // final String path = "path";
      // final String polygon = "polygon";
      // final String polyline = "polyline";
      // final String rect = "rect";

      // final int circhash = circle.hashCode();
      // final int ellipsehash = ellipse.hashCode();
      // final int linehash = line.hashCode();
      // final int pathhash = path.hashCode();
      // final int polygonhash = polygon.hashCode();
      // final int polylinehash = polyline.hashCode();
      // final int recthash = rect.hashCode();

      // System.out.println(circhash);
      // System.out.println(ellipsehash);
      // System.out.println(linehash);
      // System.out.println(pathhash);
      // System.out.println(polygonhash);
      // System.out.println(polylinehash);
      // System.out.println(recthash);
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
