package camzup;

import camzup.core.Quaternion;
import camzup.core.Rng;

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

      // final MeshDirect md = new MeshDirect();
      // final Mesh3 mesh3 = new Mesh3();
      // Mesh3.cube(mesh3);
      // MeshDirect.fromMesh3(mesh3, md);
      // System.out.println(mesh3);
      // System.out.println("");
      // System.out.println(md);

      final Rng rng = new Rng();
      final Quaternion targeta = new Quaternion();
      final Quaternion targetb = new Quaternion();
      final Quaternion temp0 = new Quaternion();
      final Quaternion temp1 = new Quaternion();
      final Quaternion temp2 = new Quaternion();
      final Quaternion temp3 = new Quaternion();
      final Quaternion temp4 = new Quaternion();

      for ( int i = 0; i < 10; ++i ) {
         final float t = rng.nextFloat();
         final Quaternion a = Quaternion.random(rng, new Quaternion());
         final Quaternion b = Quaternion.random(rng, new Quaternion());
         final Quaternion c = Quaternion.random(rng, new Quaternion());
         final Quaternion d = Quaternion.random(rng, new Quaternion());

         Quaternion.squad(a, b, c, d, t, targeta, temp0, temp1, temp2, temp3,
            temp4);

         CamZup.squad(a, b, c, d, t, targetb, temp0, temp1);

         System.out.println(targeta);
         System.out.println(targetb);
         System.out.println(Quaternion.approx(targeta, targetb));
      }
   }

   public static Quaternion squad ( final Quaternion q1, final Quaternion q2,
      final Quaternion s1, final Quaternion s2, final float step,
      final Quaternion target, final Quaternion a, final Quaternion b ) {

      Quaternion.mix(q1, q2, step, a);
      Quaternion.mix(s1, s2, step, b);
      Quaternion.mix(a, b, 2 * step * ( 1 - step ), target);
      return target;
   }

   public static Quaternion squadHelper0 ( final Quaternion qin1,
      final Quaternion qi, final Quaternion qip1, final Quaternion target ) {

      // https://gist.github.com/usefulslug/c59d5f7d35240733b80b
      // https://www.3dgep.com/understanding-quaternions/#Quotient_of_Two_Complex_Numbers

      final Quaternion qiInverse = Quaternion.inverse(qi, new Quaternion());

      final Quaternion a = Quaternion.mul(qip1, qiInverse, new Quaternion());
      final Quaternion b = Quaternion.mul(qin1, qiInverse, new Quaternion());

      final Quaternion loga = Quaternion.log(a, new Quaternion());
      final Quaternion logb = Quaternion.log(b, new Quaternion());

      final Quaternion sum = Quaternion.add(loga, logb, new Quaternion());
      final Quaternion divn4 = Quaternion.mul(sum, -0.25f, new Quaternion());
      final Quaternion expc = Quaternion.exp(divn4, new Quaternion());

      // Looks like its qi first, not expc...
      Quaternion.mul(qi, expc, target);

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
