package camzup;

import camzup.core.Curve2;
import camzup.core.CurveEntity2;
import camzup.core.IUtils;
import camzup.core.Mat3;
import camzup.core.Mat4;
import camzup.core.Mesh3;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;
import processing.core.PApplet;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;

public class CamZup {

   public final static String VERSION = "##library.prettyVersion##";

   static float round ( final float value, final int places ) {

      final double n = Math.pow(10, places);
      return (float) (Math.round(value * n) / n);
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
      // SVGParser.parse("data/v2.svg");

      // final CurveEntity2 ce = new CurveEntity2();
      // final Curve2 curve = Curve2.polygon(IUtils.HALF_PI, 0.5f,
      // 6,
      // new Curve2());
      // ce.appendCurve(curve);
      // System.out.println(ce.toBlenderCode());

//      float ang = Utils.TAU * (float)Math.random();
//      
//      PMatrix3D m = new PMatrix3D();
//      m.scale(4);
//      m.rotateZ(ang);
//      m.translate(1,  2, 3);
//      
//      m.print();
//      
//      Mat4 t = Mat4.fromTranslation(new Vec3(1, 2, 3), new Mat4());
//      Mat4 r = Mat4.fromRotZ(ang, new Mat4());
//      Mat4 s = Mat4.fromScale(4, new Mat4());
//      
//      Mat4 n = Mat4.mult(s, r, t, new Mat4());
//      System.out.println(n.toStringTab());
   }

   public static String version () {

      return CamZup.VERSION;
   }

   public final PApplet parent;

   public CamZup ( final PApplet parent ) {

      this.parent = parent;

   }
}
