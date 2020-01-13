package camzup;

import camzup.core.Gradient;
import camzup.core.Mat3;
import camzup.core.Mat4;
import camzup.core.Mesh3;
import camzup.core.Quaternion;
import camzup.core.Random;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;
import camzup.core.Vec4;
import processing.core.PApplet;

/**
 * The main class of this library. This is not needed to use
 * the library and is for development and debugging only.
 */
public class CamZup {

   /**
    * The library's current version.
    */
   public final static String VERSION = "##library.prettyVersion##";

   static String toHardCode ( final Mesh3 mesh ) {

      final StringBuilder sb = new StringBuilder();
      final int[][][] fs = mesh.faces;
      final Vec3[] vs = mesh.coords;
      final Vec2[] vts = mesh.texCoords;
      final Vec3[] vns = mesh.normals;

      final int flen0 = fs.length;
      for (int i = 0; i < flen0; ++i) {
         final int[][] f = fs[i];
         final int flen1 = f.length;
         sb.append("this.beginShape(PConstants.POLYGON);\n");
         for (int j = 0; j < flen1; ++j) {
            final int[] data = f[j];

            final int vIndex = data[0];
            final Vec3 v = vs[vIndex];

            final int vtIndex = data[1];
            final Vec2 vt = vts[vtIndex];

            final int vnIndex = data[2];
            final Vec3 vn = vns[vnIndex];

            sb.append("this.normal(");
            sb.append(Utils.toFixed(vn.x, 5));
            sb.append("f, ");
            sb.append(Utils.toFixed(vn.y, 5));
            sb.append("f, ");
            sb.append(Utils.toFixed(vn.z, 5));
            sb.append("f);\n");

            sb.append("this.vertexImpl(\n");

            sb.append(Utils.toFixed(v.x, 5));
            if (v.x == 0.0f) {
               sb.append("f, ");
            } else {
               sb.append("f * radius, ");
            }

            sb.append(Utils.toFixed(v.y, 5));
            if (v.y == 0.0f) {
               sb.append("f, ");
            } else {
               sb.append("f * radius, ");
            }

            sb.append(Utils.toFixed(v.z, 5));
            if (v.z == 0.0f) {
               sb.append("f,\n");
            } else {
               sb.append("f * radius,\n");
            }

            sb.append(Utils.toFixed(vt.x, 5));
            sb.append("f, ");

            sb.append(Utils.toFixed(vt.y, 5));
            sb.append("f);\n");
         }
         sb.append("this.endShape(PConstants.CLOSE);\n\n");
      }
      return sb.toString();
   }

   public static void main ( final String[] args ) {

      Random rng = new Random();

      // Gradient gr = Gradient.paletteMagma(new Gradient());
      // System.out.println(gr.toBlenderCode("Magma", 8));
      // PApplet.printArray(gr.evalRange(2));

      // Vec2 tin = Vec2.randomCartesian(rng, 0.0f, 1.0f, new
      // Vec2());
      // Mat3 t = Mat3.fromTranslation(tin, new Mat3());
      // System.out.println(tin);
      // System.out.println(t.toStringCol());
      //
      // float ain = rng.uniform(Utils.TAU);
      // Mat3 r = Mat3.fromRotZ(ain, new Mat3());
      // System.out.println(ain);
      // System.out.println(r.toStringCol());
      //
      //
      // Vec2 sin = Vec2.randomCartesian(rng, 1.0f, 5.0f, new
      // Vec2());
      // Mat3 s = Mat3.fromScale(
      // sin, new Mat3());
      // System.out.println(sin);
      // System.out.println(s.toStringCol());
      //
      // Mat3 m = Mat3.mul(t, r, s, new Mat3());
      // System.out.println(m.toStringCol());
      //
      // Vec2 td = new Vec2();
      // Vec2 sd = new Vec2();
      //
      // System.out.println(Mat3.decompose(m, td, sd));
      // System.out.println(td);
      // System.out.println(sd);

      Vec3 tin = Vec3.randomCartesian(rng, -1.0f, 1.0f, new Vec3());
      Quaternion rin = Quaternion.random(rng, new Quaternion());
      Vec3 sin = Vec3.randomCartesian(rng, 1.0f, 5.0f, new Vec3());
      System.out.println(tin);
      System.out.println(rin);
      System.out.println(sin);

      Mat4 t = Mat4.fromTranslation(tin, new Mat4());
      Mat4 r = Mat4.fromRotation(rin, new Mat4());
      Mat4 s = Mat4.fromScale(sin, new Mat4());
      Mat4 m = Mat4.mul(t, r, s, new Mat4());
      System.out.println(m.toStringCol());
      
      Vec3 tout = new Vec3();
      Quaternion rout = new Quaternion();
      Vec3 sout = new Vec3();
      Mat4.decompose(m, tout, rout, sout);
      System.out.println(tout);
      System.out.println(rout);
      System.out.println(sout);
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version () {

      return CamZup.VERSION;
   }

   /**
    * The PApplet referenced by this class.
    */
   public final PApplet parent;

   /**
    * Constructs a new instance of this library with the
    * PApplet as a reference.
    *
    * @param parent
    *           the parent applet
    */
   public CamZup ( final PApplet parent ) {

      this.parent = parent;

   }
}
