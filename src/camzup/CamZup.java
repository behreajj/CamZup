package camzup;

import camzup.core.IUtils;
import camzup.core.Mesh2;
import camzup.core.Quaternion;
import camzup.core.Vec2;

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

   public static Mesh2 gridTriNew ( final int count, final float margin,
      final Mesh2 target ) {

      // TODO: Finish.
      // Make each vertex coord unique.

      target.name = "Grid.Tri";

      final int cVal = count < 1 ? 1 : count;
      final int cVal1 = cVal + 1;
      final int fLen1 = cVal1 * cVal1;
      final int fLen = cVal * cVal;

      final Vec2[] vs = target.coords = Vec2.resize(target.coords, fLen1);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
         fLen1);
      final int[][][] fs = target.faces = new int[fLen + fLen][3][2];

      final float toStep = 1.0f / cVal;
      for ( int k = 0; k < fLen1; ++k ) {
         final float jStep = k % cVal1 * toStep;
         final float iStep = k / cVal1 * toStep;

         final float vx = 0.5f - jStep;
         final float vy = iStep - 0.5f;

         vs[k].set(IUtils.SQRT_3 * ( IUtils.ONE_SQRT_2 * vy - IUtils.ONE_SQRT_2
            * vx ), IUtils.ONE_SQRT_2 * vy + IUtils.ONE_SQRT_2 * vx);
         vts[k].set(jStep, 1.0f - iStep);

      }

      for ( int m = 0, k = 0; k < fLen; ++k, m += 2 ) {
         final int i = k / cVal;
         final int j = k % cVal;

         final int cOff0 = i * cVal1;
         // final int cOff1 = cOff0 + cVal1;

         final int c00 = cOff0 + j;
         final int c10 = c00 + 1;
         final int c01 = cOff0 + cVal1 + j;
         final int c11 = c01 + 1;

         final int[][] f0 = fs[m];
         final int[] vert00 = f0[0];
         vert00[0] = c00;
         vert00[1] = c00;

         final int[] vert01 = f0[1];
         vert01[0] = c10;
         vert01[1] = c10;

         final int[] vert02 = f0[2];
         vert02[0] = c01;
         vert02[1] = c01;

         final int[][] f1 = fs[m + 1];
         final int[] vert10 = f1[0];
         vert10[0] = c01;
         vert10[1] = c01;

         final int[] vert11 = f1[1];
         vert11[0] = c10;
         vert11[1] = c10;

         final int[] vert12 = f1[2];
         vert12[0] = c11;
         vert12[1] = c11;

      }

      return target;
   }

   /**
    * The main function.
    *
    * @param args the string of arguments
    */
   public static void main ( final String[] args ) {

      // TODO: Add delta time or elapsed time.
      // https://github.com/processing/processing/issues/6070

      // final Rng rng = new Rng();
      // final Color a = new Color();
      // final Vec4 lab = new Vec4();
      // final Vec4 xyz = new Vec4();
      // // Color.fromHex(0xffaabbcc, a);
      // Color.random(rng, a);
      // Color.sRgbaToLab(a, false, lab, xyz, new Color());
      // System.out.println(a);
      // System.out.println(lab);
      // final Color b = new Color();
      // Color.labTosRgba(lab, false, b, xyz, new Color());
      // System.out.println(b);

      // final Mesh2 m2 = new Mesh2();
      // CamZup.gridTriNew(8, 0f, m2);
      // final MeshEntity2 me2 = new MeshEntity2(m2);
      // final String str = me2.toBlenderCode();
      // System.out.println(str);

      // double r = new Random().nextFloat();
      // System.out.println(r);

      // double test = r * r * Math.pow(r, 0.4d);
      // System.out.println(control);
      // System.out.println(test);
      // System.out.println(Math.abs(control - test) <= 0.000001d);
      // System.out.println(compute(r, 2.4d));

   }

   public static float pow24 ( final float x ) {

      return ( float ) Math.exp(2.4d * Math.log(x));
   }

   public static float pow24Alt ( final float x ) {

      return x * x * ( float ) Math.pow(x, 0.4d);
   }

   public static float pow24Inv ( final float x ) {

      return ( float ) Math.exp(0.4166666666666667d * Math.log(x));
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
