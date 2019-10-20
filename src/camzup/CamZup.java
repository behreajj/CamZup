package camzup;

import camzup.core.Mesh3;
import camzup.core.Quaternion;
import camzup.core.Random;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;
import processing.core.PApplet;

@SuppressWarnings("unused")
public class CamZup {

   public final static String VERSION = "##library.prettyVersion##";

   static float fastersqrt ( final float f ) {

      return f * Float.intBitsToFloat(0x5f375a86
            - (Float.floatToIntBits(f) >> 1));
   }

   static float fastsqrt ( final float f ) {

      float y = Float.intBitsToFloat(0x5f375a86
            - (Float.floatToIntBits(f) >> 1));
      final float xhalf = f * 0.5f;
      y = y * (1.5f - xhalf * y * y);
      y = y * (1.5f - xhalf * y * y);
      return f * y;
   }

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

      final Random rng = new Random();

      // Vec3 from = Vec3.forward(new Vec3());
      // Vec3 to = Vec3.up(new Vec3());
      // Quaternion q = Quaternion.fromTo(from, to, new
      // Quaternion());
      // Vec3 axis = new Vec3();
      // float angle = Quaternion.toAxisAngle(q, axis);
      // System.out.println(q);
      // System.out.println(IUtils.RAD_TO_DEG * angle);
      // System.out.println(axis);

      final Quaternion a = new Quaternion(-0.70495f, 0.45353f, -0.54178f,
            -0.06192f);
      final Quaternion b = new Quaternion(0.1099f, 0.43388f, 0.89415f,
            -0.01317f);
      System.out.println(Quaternion.mult(a, b, new Quaternion()));

      // Curve3 curve1 = Curve3.random(rng, 10, -1, 1, true, new
      // Curve3());
      // Curve3 curve2 = Curve3.circle(
      // 0, 1, 4,
      // new Curve3(), new Vec3(), new Vec3());
      // CurveEntity3 ce = new CurveEntity3();
      // ce.appendCurve(curve1, curve2);
      // System.out.println(ce.toObjString(20));

      // Vec3 v = Vec3.random(rng, new Vec3());
      // Color c = Color.fromDir3(v, new Color());
      // System.out.println(v);
      // System.out.println(c);

      // Quaternion q = Quaternion.random(rng, new Quaternion());
      // Quaternion r = new Quaternion();
      //
      // Vec3 i = new Vec3();
      // Vec3 j = new Vec3();
      // Vec3 k = new Vec3();
      //
      // Vec3 i1 = new Vec3();
      // Vec3 j1 = new Vec3();
      // Vec3 k1 = new Vec3();
      //
      // PMatrix3D m = new PMatrix3D();
      // PMatrix3D o = new PMatrix3D();
      //
      // for (int n = 0; n < 32; ++n) {
      // Quaternion.random(rng, q);
      // Quaternion.toAxes(q, i, j, k);
      //
      // System.out.println(q);
      // System.out.println("");
      // Convert.toPMatrix3D(q, m);
      // m.print();
      // Convert.toQuaternion(m, r);
      // System.out.println(r);
      //
      // Convert.toPMatrix3D(r, o);
      // o.print();
      //
      // }

      // final int count = 1500;
      // final float[] vals = new float[count];
      // for (int i = 0; i < count; ++i) {
      // vals[i] = (float) (10.0d + Math.random() * 150.0d);
      // }
      //
      // float out = 0.0f;
      // long start = 0l;
      // long stop = 0l;
      //
      // start = System.nanoTime();
      // for (int i = 0; i < count; ++i) {
      // out = (float) Math.sqrt(vals[i]);
      // }
      // stop = System.nanoTime();
      // System.out.println(out);
      // System.out.println(stop - start);
      //
      // start = System.nanoTime();
      // for (int i = 0; i < count; ++i) {
      // out = CamZup.fastersqrt(vals[i]);
      // }
      // stop = System.nanoTime();
      // System.out.println(out);
      // System.out.println(stop - start);

   }

   public static String version () {

      return CamZup.VERSION;
   }

   public final PApplet parent;

   public CamZup ( final PApplet parent ) {

      this.parent = parent;

   }
}
