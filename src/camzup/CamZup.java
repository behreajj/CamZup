package camzup;

import java.text.DecimalFormat;

import camzup.core.Mesh3;
import camzup.core.Random;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;
import camzup.core.SimplexNoise;
import processing.core.PApplet;

public class CamZup {

   public final static String VERSION = "##library.prettyVersion##";

   public static void main ( final String[] args ) {
   }

   public static String version () {

      return CamZup.VERSION;
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

   public final PApplet parent;

   public CamZup ( final PApplet parent ) {

      this.parent = parent;

   }
}
