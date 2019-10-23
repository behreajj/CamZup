package camzup;

import camzup.core.Curve3;
import camzup.core.CurveEntity3;
import camzup.core.Mesh3;
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

//      final Random rng = new Random();
//
//      final Curve3 curve1 = Curve3.random(rng, 10, -1, 1, false, new Curve3());
//      final CurveEntity3 ce = new CurveEntity3();
//      ce.appendCurve(curve1);
//      ce.appendCurve(Curve3.circle(new Curve3(), new Vec3(), new Vec3()));
//      System.out.println(ce.toBlenderCode());
   }

   public static String version () {

      return CamZup.VERSION;
   }

   public final PApplet parent;

   public CamZup ( final PApplet parent ) {

      this.parent = parent;

   }
}
