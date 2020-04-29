package camzup;

import camzup.core.Rng;

import camzup.pfriendly.PMatAux;

import processing.core.PApplet;
import processing.core.PMatrix3D;

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

      final Rng rng = new Rng();
      // final Vec2 p = Vec2.randomPolar(rng, 0.5f, 0.5f, new Vec2());
      // System.out.println(p);
      // System.out.println(" ");
      // final Mesh2 m = Mesh2.polygon(5, Mesh2.PolyType.NGON, new Mesh2());
      // final TreeMap < Float, Vert2 > tm = Mesh2.proximity(m, p);
      //
      // int i = 0;
      // for ( final Map.Entry < Float, Vert2 > entry : tm.entrySet() ) {
      // final Vert2 v = entry.getValue();
      //
      // System.out.println(i++);
      // System.out.println(v);
      //
      // final float realDist = Vec2.dist(v.coord, p);
      // System.out.println(entry.getKey());
      // System.out.println(realDist);
      // System.out.println("");
      // }

      PMatrix3D x = new PMatrix3D(1, 2, 3, 0, 5, 0, 7, 0, 9, 0, 11, 12,
         13,
         14,
         15, 16);
      x.print();

      PMatrix3D n = new PMatrix3D(x);
      n.invert();
      n.print();

      PMatrix3D o = new PMatrix3D(x);
      o.print();
      PMatAux.inverse(x, o);
      o.print();
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
