package camzup;

import camzup.core.Mesh3;
import camzup.core.Random;
import camzup.core.Vec2;
import camzup.core.Vec3;
import processing.core.PApplet;

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
            sb.append(vn.x);
            sb.append("f, ");
            sb.append(vn.y);
            sb.append("f, ");
            sb.append(vn.z);
            sb.append("f);\n");

            sb.append("this.vertexImpl(\n");

            sb.append(String.format("%.5f", v.x));
            if (v.x == 0.0f) {
               sb.append("f, ");
            } else {
               sb.append("f * radius, ");
            }

            sb.append(String.format("%.5f", v.y));
            if (v.y == 0.0f) {
               sb.append("f, ");
            } else {
               sb.append("f * radius, ");
            }

            sb.append(String.format("%.5f", v.z));
            if (v.z == 0.0f) {
               sb.append("f,\n");
            } else {
               sb.append("f * radius,\n");
            }

            sb.append(vt.x);
            sb.append("f, ");

            sb.append(vt.y);
            sb.append("f);\n");
         }
         sb.append("this.endShape(PConstants.CLOSE);\n\n");
      }
      return sb.toString();
   }

   public static void main ( final String[] args ) {

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
