package camzup;

import camzup.core.Mesh3;
import camzup.core.MeshEntity3;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;
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

       MeshEntity3 me = new MeshEntity3();
       Mesh3 m = new Mesh3();
       me.appendMesh(Mesh3.dodecahedron(m));
       System.out.println(me.toBlenderCode());

      // int count = 10;
      // Random rng = new Random();
      // float angle = 0.0f;
      // Vec3 axis = new Vec3();
      // PMatrix3D test = new PMatrix3D();
      // PMatrix3D control = new PMatrix3D();
      // Quaternion inv = new Quaternion();
      // Vec3 axisInv = new Vec3();
      // for (int i = 0; i < count; ++i) {
      // control.reset();
      // test.reset();
      //
      // Quaternion q = Quaternion.random(rng, new Quaternion());
      // angle = Quaternion.toAxisAngle(q, axis);
      // System.out.println(IUtils.RAD_TO_DEG * angle);
      // System.out.println(axis);
      // System.out.println("");
      //
      // Quaternion.inverse(q, inv);
      // float angleInv = Quaternion.toAxisAngle(inv, axisInv);
      // System.out.println(IUtils.RAD_TO_DEG * angleInv);
      // System.out.println(axisInv);
      // System.out.println("");
      //
      // PMatAux.invRotate(angle, axis.x, axis.y, axis.z,
      // control);
      // System.out.println(PMatAux.toString(control, 4));
      //
      // PMatAux.invRotate(q, test);
      // // PMatAux.rotate(inv, test);
      // System.out.println(PMatAux.toString(test, 4));
      // System.out.println("");
      // }
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
