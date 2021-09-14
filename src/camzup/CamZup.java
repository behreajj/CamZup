package camzup;

import camzup.core.Quaternion;

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

      // final Mesh2 mesh2 = new Mesh2();
      // Mesh2.gridHex(2, mesh2);
      // mesh2.clean();
      // final MeshEntity2 me2 = new MeshEntity2(mesh2);
      // System.out.println(me2.toBlenderCode());
      // Edge2[] edges = mesh2.getEdgesUndirected();
      // for ( Edge2 edge : edges ) { System.out.println(edge); }
      // System.out.println(mesh2.getEdgesUndirected().length);
      // System.out.println(mesh2.getEdgesDirected().length);

      // Gradient grd = new Gradient();
      // Gradient.paletteRyb(grd);
      // String s = Color.toPalString(grd.toArray());
      // System.out.print(s);
   }

   public static Quaternion squad ( final Quaternion q1, final Quaternion q2,
      final Quaternion s1, final Quaternion s2, final float step,
      final Quaternion target, final Quaternion a, final Quaternion b ) {

      Quaternion.mix(q1, q2, step, a);
      Quaternion.mix(s1, s2, step, b);
      Quaternion.mix(a, b, 2 * step * ( 1 - step ), target);
      return target;
   }

   public static Quaternion squadHelper0 ( final Quaternion prev,
      final Quaternion curr, final Quaternion next, final Quaternion target ) {

      // https://gist.github.com/usefulslug/c59d5f7d35240733b80b
      // https://www.3dgep.com/understanding-quaternions/

      final Quaternion qiInverse = Quaternion.inverse(curr, new Quaternion());

      final Quaternion a = Quaternion.mul(next, qiInverse, new Quaternion());
      final Quaternion b = Quaternion.mul(prev, qiInverse, new Quaternion());

      final Quaternion loga = Quaternion.log(a, new Quaternion());
      final Quaternion logb = Quaternion.log(b, new Quaternion());

      final Quaternion sum = Quaternion.add(loga, logb, new Quaternion());
      final Quaternion divn4 = Quaternion.mul(sum, -0.25f, new Quaternion());
      final Quaternion expc = Quaternion.exp(divn4, new Quaternion());

      // Looks like its qi first, not expc...
      Quaternion.mul(curr, expc, target);

      Quaternion.normalize(target, target);
      return target;

   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
