package camzup;

import camzup.core.Utils;
import camzup.core.Vec3;

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

      // final Rng rng = new Rng();
      // for ( int i = 0; i < 20; ++i ) {
      // final float var12 = rng.nextFloat();
      // final float var45 = var12 * 2.0f - 1.0f;
      // final float x = Utils.asin(var45);
      // final float y = ( float ) Math.asin(var45);
      // System.out.println(x);
      // System.out.println(y);
      // System.out.println(Utils.approx(x, y, 0.0001f));
      // }

      // final Img img = new Img();
      // PngParser.parsePng("data/diagnostic.png", img);

      // final String[] test = { "5.647-3.146 -8-16-75.1" };
      // final String[] split = CamZup.breakNeg(test);
      // for ( int i = 0; i < split.length; ++i ) {
      // System.out.println(split[i]);
      // }

      // final Mesh2 m = new Mesh2();
      // Mesh2.circle(m);
      // System.out.println(m);

      final Vec3[] grid = Vec3.flat(Vec3.grid(8));
      System.out.println(Utils.toString(grid));
      // var tree = new KdTree < Vec3 >(grid, cmps);
      // System.out.println(tree);
      // System.out.println(tree.nearest(new Vec3(), 3, null));
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
