package camzup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;

import camzup.core.Gradient;
import camzup.core.Gradient;
import camzup.core.Mesh3;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;
import camzup.core.Vec4;
import camzup.core.Color;
import processing.core.PApplet;

public class CamZup {

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

      // final int x = 12345;
      // final Integer y = x;
      // System.out.println(y.hashCode());
      // SVGParser.parse("data/v2.svg");

      // final CurveEntity2 ce = new CurveEntity2();
      // final Curve2 curve = Curve2.polygon(IUtils.HALF_PI, 0.5f,
      // 6,
      // new Curve2());
      // ce.appendCurve(curve);
      // System.out.println(ce.toBlenderCode());

      // final Gradient gr = Gradient.paletteViridis(new
      // Gradient());
      // gr.removeDuplicates();
      // System.out.println(gr.toString(4));
      // final int count = 32;
      // final float toPrc = 1.0f / (count - 1.0f);
      // final Color target = new Color();
      // for (int i = 0; i < count; ++i) {
      // final float prc = i * toPrc;
      // System.out.println(Color.toHexWeb(gr.eval(prc, target)));
      // }

   }

   public static String version () {

      return CamZup.VERSION;
   }

   public final PApplet parent;

   public CamZup ( final PApplet parent ) {

      this.parent = parent;

   }

   public static < T > T[] sort ( T[] arr,
         Comparator < ? super T > comparator ) {

      Arrays.sort(arr, comparator);
      return arr;
   }
}
