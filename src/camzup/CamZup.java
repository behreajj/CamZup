package camzup;

import java.util.HashSet;

import camzup.core.Color;
import camzup.core.IUtils;
import camzup.core.Mesh2;
import camzup.core.Mesh3;
import camzup.core.MeshEntity2;
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

   static Vec2[] permute ( final Vec2 source ) {

      final HashSet < Vec2 > result = new HashSet <>();

      /* All positive. */
      result.add(new Vec2(source.x, source.y));
      result.add(new Vec2(source.y, source.x));

      /* All negative. */
      result.add(new Vec2(-source.x, -source.y));
      result.add(new Vec2(-source.y, -source.x));

      /* X positive. */
      result.add(new Vec2(-source.x, source.y));
      result.add(new Vec2(-source.y, source.x));

      /* X negative. */
      result.add(new Vec2(source.x, -source.y));
      result.add(new Vec2(source.y, -source.x));

      return result.toArray(new Vec2[result.size()]);
   }

   static Vec3[] permute ( final Vec3 source ) {

      final HashSet < Vec3 > result = new HashSet <>();

      /* All positive. */
      result.add(new Vec3(+source.x, +source.y, +source.z));
      result.add(new Vec3(+source.x, +source.z, +source.y));
      result.add(new Vec3(+source.y, +source.x, +source.z));
      result.add(new Vec3(+source.y, +source.z, +source.x));
      result.add(new Vec3(+source.z, +source.x, +source.y));
      result.add(new Vec3(+source.z, +source.y, +source.z));

      /* All negative. */
      result.add(new Vec3(-source.x, -source.y, -source.z));
      result.add(new Vec3(-source.x, -source.z, -source.y));
      result.add(new Vec3(-source.y, -source.x, -source.z));
      result.add(new Vec3(-source.y, -source.z, -source.x));
      result.add(new Vec3(-source.z, -source.x, -source.y));
      result.add(new Vec3(-source.z, -source.y, -source.z));

      /* X all negative. */
      result.add(new Vec3(-source.x, +source.y, +source.z));
      result.add(new Vec3(-source.x, +source.z, +source.y));
      result.add(new Vec3(-source.y, +source.x, +source.z));
      result.add(new Vec3(-source.y, +source.z, +source.x));
      result.add(new Vec3(-source.z, +source.x, +source.y));
      result.add(new Vec3(-source.z, +source.y, +source.z));

      /* Y all negative. */
      result.add(new Vec3(+source.x, -source.y, +source.z));
      result.add(new Vec3(+source.x, -source.z, +source.y));
      result.add(new Vec3(+source.y, -source.x, +source.z));
      result.add(new Vec3(+source.y, -source.z, +source.x));
      result.add(new Vec3(+source.z, -source.x, +source.y));
      result.add(new Vec3(+source.z, -source.y, +source.z));

      /* Z all negative. */
      result.add(new Vec3(+source.x, +source.y, -source.z));
      result.add(new Vec3(+source.x, +source.z, -source.y));
      result.add(new Vec3(+source.y, +source.x, -source.z));
      result.add(new Vec3(+source.y, +source.z, -source.x));
      result.add(new Vec3(+source.z, +source.x, -source.y));
      result.add(new Vec3(+source.z, +source.y, -source.z));

      /* X all positive. */
      result.add(new Vec3(+source.x, -source.y, -source.z));
      result.add(new Vec3(+source.x, -source.z, -source.y));
      result.add(new Vec3(+source.y, -source.x, -source.z));
      result.add(new Vec3(+source.y, -source.z, -source.x));
      result.add(new Vec3(+source.z, -source.x, -source.y));
      result.add(new Vec3(+source.z, -source.y, -source.z));

      /* Y all positive. */
      result.add(new Vec3(-source.x, +source.y, -source.z));
      result.add(new Vec3(-source.x, +source.z, -source.y));
      result.add(new Vec3(-source.y, +source.x, -source.z));
      result.add(new Vec3(-source.y, +source.z, -source.x));
      result.add(new Vec3(-source.z, +source.x, -source.y));
      result.add(new Vec3(-source.z, +source.y, -source.z));

      /* Z all positive. */
      result.add(new Vec3(-source.x, -source.y, +source.z));
      result.add(new Vec3(-source.x, -source.z, +source.y));
      result.add(new Vec3(-source.y, -source.x, +source.z));
      result.add(new Vec3(-source.y, -source.z, +source.x));
      result.add(new Vec3(-source.z, -source.x, +source.y));
      result.add(new Vec3(-source.z, -source.y, +source.z));

      return result.toArray(new Vec3[result.size()]);
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

      MeshEntity2 me = new MeshEntity2();
      Mesh2 m = new Mesh2();
      me.appendMesh(Mesh2.arc(0f, Utils.PI, 0.75f,
            5, Mesh2.PolyType.TRI, m));

      // System.out.println(me.getMesh(0).toString());
      System.out.println(me);

      // int sectors = 10;
      // float arcLen = Utils.TAU / sectors;
      // float arcMargin = Utils.radians(2.5f);
      // float halfMarg = arcMargin * 0.5f;
      //
      // for(int i = 0; i < sectors; ++i) {
      // float start = i * arcLen + halfMarg;
      // float stop = (i + 1) * arcLen - halfMarg;
      // Mesh2 m = new Mesh2();
      // Mesh2.arc(start, stop, 0.75f, 64, Mesh2.PolyType.NGON,
      // m);
      // me.appendMesh(m);
      // }
      //
      // System.out.println(me.toBlenderCode());
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
