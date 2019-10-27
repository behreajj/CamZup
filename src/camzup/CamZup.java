package camzup;

import camzup.core.Mesh3;
import camzup.core.Quaternion;
import camzup.core.Random;
import camzup.core.Transform2;
import camzup.core.Transform3;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;
import camzup.pfriendly.Convert;
import processing.core.PApplet;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;
import processing.core.PVector;
import camzup.core.ITransform;

@SuppressWarnings("unused")
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

      Random rng = new Random();
      Transform3 tr = new Transform3(
            Vec3.randomCartesian(rng, 0.0f, 1.0f, new Vec3()),
            Quaternion.random(rng, new Quaternion()),
            Vec3.randomCartesian(rng, 0.1f, 1.0f, new Vec3()));
      System.out.println(tr);
      
      Transform3 trInv = Transform3.inverse(tr, new Transform3());
      
      
      Vec3 pt = Vec3.randomCartesian(rng, -1.0f, 1.0f, new Vec3());
      System.out.println(pt);
      Vec3 pttr = Transform3.multPoint(tr, pt, new Vec3());
      System.out.println(pttr);
      
      // Do you need a multPoint inv?
//      Vec3 pttrinv = Transform3.invMultPoint(tr, pt, new Vec3());
//      System.out.println(pttrinv);
      
      System.out.print("\n");
      PMatrix3D m = Convert.toPMatrix3D(tr, ITransform.Order.TSR);
      PVector pv = Convert.toPVector(pt);
      System.out.println(pv);
      PVector pvtr = m.mult(pv, new PVector());
      System.out.println(pvtr);
      m.invert();
      PVector pvtrinv = m.mult(pv, new PVector());
      System.out.println(pvtrinv);
   }

   public static String version () {

      return CamZup.VERSION;
   }

   public final PApplet parent;

   public CamZup ( final PApplet parent ) {

      this.parent = parent;

   }
}
