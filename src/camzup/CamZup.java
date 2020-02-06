package camzup;

import java.util.HashSet;

import camzup.core.IUtils;
import camzup.core.Mesh2;
import camzup.core.Mesh3;
import camzup.core.MeshEntity2;
import camzup.core.MeshEntity3;
import camzup.core.Quaternion;
import camzup.core.Random;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;
import camzup.pfriendly.PMatAux;
import processing.core.PApplet;
import processing.core.PMatrix3D;

/**
 * The main class of this library. This is not needed to use
 * the library and is for development and debugging only.
 */
public class CamZup {

   private static int createLowerStrip (
         final int steps,
         int vTop,
         int vBottom,
         int t,
         final int[][][] triangles ) {

      for (int i = 1; i < steps; i++) {

         triangles[t++] = new int[][] {
               { vBottom, vBottom, vBottom },
               { vTop - 1, vTop - 1, vTop - 1 },
               { vTop, vTop, vTop }, };

         triangles[t++] = new int[][] {
               { vBottom, vBottom, vBottom++ },
               { vTop, vTop, vTop++ },
               { vBottom, vBottom, vBottom }, };
      }

      triangles[t++] = new int[][] {
            { vBottom, vBottom, vBottom },
            { vTop - 1, vTop - 1, vTop - 1 },
            { vTop, vTop, vTop }, };

      return t;
   }

   private static int createUpperStrip (
         final int steps,
         int vTop,
         int vBottom,
         int t,
         final int[][][] triangles ) {

      triangles[t++] = new int[][] {
            { vBottom, vBottom, vBottom },
            { vTop - 1, vTop - 1, vTop - 1 },
            { ++vBottom, vBottom, vBottom }
      };

      for (int i = 1; i <= steps; i++) {

         triangles[t++] = new int[][] {
               { vTop - 1, vTop - 1, vTop - 1 },
               { vTop, vTop, vTop },
               { vBottom, vBottom, vBottom }
         };

         triangles[t++] = new int[][] {
               { vBottom, vBottom, vBottom },
               { vTop, vTop, vTop++ },
               { ++vBottom, vBottom, vBottom }
         };
      }
      return t;
   }

   private static int createVertexLine (
         final Vec3 from,
         final Vec3 to,
         final int steps,
         int v,
         final Vec3[] vertices ) {

      final float toStep = 1.0f / steps;
      for (int i = 1; i <= steps; i++) {
         vertices[v++] = Vec3.mix(from, to, i * toStep, new Vec3());
      }
      return v;
   }

   /**
    * Cf.
    * https://catlikecoding.com/unity/tutorials/octahedron-sphere/
    *
    * @param div
    *           divisions
    * @param target
    *           the output mesh
    * @return the sphere
    * @author Jasper Flick
    */
   public static Mesh3 sphere1 ( final int div, final Mesh3 target ) {

      final int subdivisions = Utils.clamp(div, 0, 6);

      /* Raise 2 to the power of subdivisions. */
      final int resolution = 1 << subdivisions;
      final int res1 = resolution + 1;
      final Vec3[] coords = new Vec3[res1 * res1 * 4
            - (resolution * 2 - 1) * 3];
      final int[][][] faces = new int[1 << subdivisions * 2 + 3][3][3];

      int v = 0;
      int vBottom = 0;
      int t = 0;

      /* Bottom cap. */
      coords[v++] = Vec3.down(new Vec3());
      coords[v++] = Vec3.down(new Vec3());
      coords[v++] = Vec3.down(new Vec3());
      coords[v++] = Vec3.down(new Vec3());

      final Vec3 down = Vec3.down(new Vec3());
      final Vec3 forward = Vec3.forward(new Vec3());
      final Vec3 up = Vec3.up(new Vec3());

      Vec3 from;
      Vec3 to;

      final float toPercent = 1.0f / resolution;

      final Vec3[] directions = new Vec3[] {
            Vec3.left(new Vec3()),
            Vec3.back(new Vec3()),
            Vec3.right(new Vec3()),
            Vec3.forward(new Vec3())
      };

      for (int i = 1; i <= resolution; ++i) {
         final float prc = i * toPercent;
         coords[v++] = to = Vec3.mix(down, forward, prc, new Vec3());

         for (int d = 0; d < 4; ++d) {
            from = to;
            to = Vec3.mix(down, directions[d], prc, new Vec3());
            t = createLowerStrip(i, v, vBottom, t, faces);
            v = createVertexLine(from, to, i, v, coords);
            vBottom += i > 1 ? i - 1 : 1;
         }
         vBottom = v - 1 - i * 4;
      }

      for (int i = resolution - 1; i >= 1; i--) {
         final float prc = i * toPercent;
         coords[v++] = to = Vec3.mix(up, forward, prc, new Vec3());
         for (int d = 0; d < 4; d++) {
            from = to;
            to = Vec3.mix(up, directions[d], prc, new Vec3());
            t = createUpperStrip(i, v, vBottom, t, faces);
            v = createVertexLine(from, to, i, v, coords);
            vBottom += i + 1;
         }
         vBottom = v - 1 - i * 4;
      }

      /* Top cap. */
      for (int i = 0; i < 4; ++i) {
         faces[t++] = new int[][] {
               { vBottom, vBottom, vBottom },
               { v, v, v },
               { ++vBottom, vBottom, vBottom }
         };

         coords[v++] = Vec3.up(new Vec3());
      }

      /* Normalize vertices and create a copy for normals. */
      final Vec3[] normals = new Vec3[coords.length];
      for (int i = 0; i < coords.length; ++i) {
         normals[i] = new Vec3(Vec3.normalize(coords[i], coords[i]));
      }

      final Vec2[] texCoords = new Vec2[coords.length];
      float xPrev = 1.0f;
      for (int i = 0; i < coords.length; ++i) {
         final Vec3 v1 = coords[i];
         if (v1.x == xPrev) {
            texCoords[i - 1].x = 1.0f;
         }
         xPrev = v1.x;
         final Vec2 st = new Vec2();
         st.x = Utils.atan2(v1.x, v1.z) * -IUtils.ONE_TAU;
         if (st.x < 0.0f) {
            st.x += 1.0f;
         }
         st.y = Utils.asin(v1.y) / IUtils.PI + 0.5f;
         texCoords[i] = st;
      }
      texCoords[coords.length - 4].x = texCoords[0].x = 0.125f;
      texCoords[coords.length - 3].x = texCoords[1].x = 0.375f;
      texCoords[coords.length - 2].x = texCoords[2].x = 0.625f;
      texCoords[coords.length - 1].x = texCoords[3].x = 0.875f;

      /* Scale vertices to radius. */
      for (int i = 0; i < coords.length; i++) {
         Vec3.mul(coords[i], 0.5f, coords[i]);
      }

      target.name = "Sphere";
      return target.set(faces, coords, texCoords, normals);
   }

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

      // MeshEntity3 me = new MeshEntity3();
      // Mesh3 m = new Mesh3();
      // me.appendMesh(m);
      // String str = me.toBlenderCode();
      // System.out.println(str);
      
//      Mesh2 m = new Mesh2();
//      Mesh2.polygon(5,Mesh2.PolyType.TRI, m);
//      MeshEntity2 me = new MeshEntity2().appendMesh(m);
//      System.out.println(me.toUnityCode());
      
      Vec3 i = new Vec3(1,2,3);
      Vec3 n = new Vec3(0.0f, 0.6f, 0.8f);
      Vec3 r = Vec3.reflect(i, n, new Vec3());
      System.out.println(r);
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
