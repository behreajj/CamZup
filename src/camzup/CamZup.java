package camzup;

import camzup.core.IUtils;
import camzup.core.Mat4;
import camzup.core.Mesh2;
import camzup.core.Mesh3;
import camzup.core.MeshEntity2;
import camzup.core.Rng;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;

import processing.core.PApplet;

/**
 * The main class of this library. This is not needed to use the library and is
 * for development and debugging only.
 */
@SuppressWarnings ( "unused" )
public class CamZup {

   /**
    * The PApplet referenced by this class.
    */
   public final PApplet parent;

   /**
    * Constructs a new instance of this library with the PApplet as a reference.
    *
    * @param parent the parent applet
    */
   public CamZup ( final PApplet parent ) {

      this.parent = parent;
   }

   /**
    * Returns a string representation of the CamZup class.
    *
    * @return the string
    */
   @Override
   public String toString ( ) {

      return new StringBuilder().append("{ version: ").append(
         CamZup.VERSION).append(", parent: ").append(this.parent).append(
            " }").toString();
   }

   /**
    * The library's current version.
    */
   public final static String VERSION = "##library.prettyVersion##";

   /**
    * The main function.
    *
    * @param args the string of arguments
    */
   public static void main ( final String[] args ) {

      final Rng rng = new Rng();

      final Mesh2 m2 = new Mesh2();
      final Mesh3 m3 = new Mesh3();
      Mesh3.cube(m3);
      m3.rotate(IUtils.THIRD_PI,
         Vec3.normalize(Vec3.one(new Vec3()), new Vec3()));

      final Mat4 proj = new Mat4();
      final Mat4 mv = new Mat4();

      Mat4.orthographic(-0.5f, 0.5f, -0.5f, 0.5f, 0.001f, 1000.0f, proj);

      Mesh3.project(m3, proj, mv, m2);

      final MeshEntity2 me2 = new MeshEntity2();
      me2.append(m2);
      final String str = me2.toBlenderCode();
      System.out.println(str);
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

   private static String toHardCode (
      final Mesh3 mesh,
      final float radius ) {

      final StringBuilder sb = new StringBuilder();
      final int[][][] fs = mesh.faces;
      final Vec3[] vs = mesh.coords;
      final Vec2[] vts = mesh.texCoords;
      final Vec3[] vns = mesh.normals;

      final int flen0 = fs.length;
      for ( int i = 0; i < flen0; ++i ) {
         final int[][] f = fs[i];
         final int flen1 = f.length;
         sb.append("this.beginShape(PConstants.POLYGON);\n");
         sb.append("this.texture(txtr);\n");
         for ( int j = 0; j < flen1; ++j ) {
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

            sb.append("this.vertex(\n");

            sb.append(Utils.toFixed(v.x, 5));
            if ( v.x == 0.0f ) {
               sb.append("f, ");
            } else {
               sb.append("f , ");
            }

            sb.append(Utils.toFixed(v.y, 5));
            if ( v.y == 0.0f ) {
               sb.append("f, ");
            } else {
               sb.append("f , ");
            }

            sb.append(Utils.toFixed(v.z, 5));
            if ( v.z == 0.0f ) {
               sb.append("f,\n");
            } else {
               sb.append("f ,\n");
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

}
