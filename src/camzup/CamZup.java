package camzup;

import java.util.HashSet;

import processing.core.PApplet;

import camzup.core.Mesh3;
import camzup.core.MeshEntity3;
import camzup.core.Random;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;

/**
 * The main class of this library. This is not needed to use the
 * library and is for development and debugging only.
 */
@SuppressWarnings ( "unused" )
public class CamZup {

  static final float ONE_PI = 0.31830987f;

  /**
   * The library's current version.
   */
  public final static String VERSION = "##library.prettyVersion##";

  /**
   * The PApplet referenced by this class.
   */
  public final PApplet parent;

  /**
   * Constructs a new instance of this library with the PApplet as a
   * reference.
   *
   * @param parent the parent applet
   */
  public CamZup ( final PApplet parent ) {

    this.parent = parent;
  }

  private static float[] flat ( final float[][] arr ) {

    final int sourceLen = arr.length;
    int totalLen = 0;
    for ( int i = 0; i < sourceLen; ++i ) {
      totalLen += arr[i].length;
    }

    final float[] result = new float[totalLen];
    for ( int j = 0, i = 0; i < sourceLen; ++i ) {
      final float[] arrInner = arr[i];
      final int len = arrInner.length;
      System.arraycopy(arrInner, 0, result, j, len);
      j += len;
    }

    return result;
  }

  private static int getNextActive (
      final int x,
      final int vertexCount,
      final boolean[] active ) {

    int xMut = x;
    for ( ;; ) {
      if ( ++xMut == vertexCount ) { xMut = 0; }
      if ( active[xMut] ) { return xMut; }
    }
  }

  private static int getPrevActive (
      final int x,
      final int vertexCount,
      final boolean[] active ) {

    int xMut = x;
    for ( ;; ) {
      if ( --xMut == -1 ) { xMut = vertexCount - 1; }
      if ( active[xMut] ) { return xMut; }
    }
  }

  private static Vec2[] permute ( final Vec2 source ) {

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

  private static Vec3[] permute ( final Vec3 source ) {

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

  private static String toHardCode (
      final Mesh3 mesh,
      final float radius ) {

    final StringBuilder sb = new StringBuilder()
        .append("float radius = ")
        .append(radius)
        .append("f;\n\n");
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
          sb.append("f * radius, ");
        }

        sb.append(Utils.toFixed(v.y, 5));
        if ( v.y == 0.0f ) {
          sb.append("f, ");
        } else {
          sb.append("f * radius, ");
        }

        sb.append(Utils.toFixed(v.z, 5));
        if ( v.z == 0.0f ) {
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

  private static Mesh3 toSphere ( final Mesh3 m ) {

    for ( int i = 0; i < m.coords.length; ++i ) {
      Vec3.rescale(m.coords[i], 0.5f, m.coords[i]);
    }
    m.calcNormals();
    return m;
  }

  private static int triangulate (
      final Vec3[] vertex,
      final Vec3 normal,
      final int[][] triangle ) {

    final int vertexCount = vertex.length;
    final boolean[] active = new boolean[vertexCount];
    for ( int i = 0; i < vertexCount; ++i ) {
      active[i] = true;
    }
    int triangleCount = 0;
    final int start = 0;
    final int p1 = 0;
    final int p2 = 1;
    final int m1 = vertexCount - 1;
    final int m2 = vertexCount - 2;
    final boolean lastPositive = false;

    final Vec3 n1 = new Vec3();
    final Vec3 n2 = new Vec3();
    final Vec3 n3 = new Vec3();
    final Vec3 diffp1p2 = new Vec3();
    final Vec3 diffp1m1 = new Vec3();
    final Vec3 diffp2p1 = new Vec3();
    final Vec3 diffvp2 = new Vec3();
    final Vec3 diffvm1 = new Vec3();
    final Vec3 diffvp1 = new Vec3();
    final Vec3 epsilon = Vec3.epsilon(new Vec3());
    final Vec3 negEps = Vec3.negate(epsilon, new Vec3());
    final Vec3 lcmp = new Vec3();
    final Vec3 gteps = new Vec3();

    for ( ;; ) {
      if ( p2 == m2 ) {
        triangle[0][0] = m1;
        triangle[1][0] = p1;
        triangle[2][0] = p2;
        triangleCount++;
        break;
      }

      final Vec3 vp1 = vertex[p1];
      final Vec3 vp2 = vertex[p2];
      final Vec3 vm1 = vertex[m1];
      final Vec3 vm2 = vertex[m2];

      boolean positive = false;
      final boolean negative = false;

      Vec3.sub(vm1, vp2, n1);
      Vec3.normalize(n1, n1);
      Vec3.fmod(normal, n1, n1);

      Vec3.sub(vp1, vp2, diffp1p2);
      Vec3.mul(n1, diffp1p2, lcmp);
      Vec3.gt(lcmp, epsilon, gteps);

      if ( Vec3.all(gteps) ) {

        positive = true;

        Vec3.sub(vp1, vm1, diffp1m1);
        Vec3.normalize(diffp1m1, n2);
        Vec3.fmod(normal, n2, n2);

        Vec3.sub(vp2, vp1, diffp2p1);
        Vec3.normalize(diffp2p1, n3);
        Vec3.fmod(normal, n3, n3);

        for ( int a = 0; a < vertexCount; a++ ) {
          if ( active[a] && a != p1 && a != p2 && a != m1 ) {
            final Vec3 v = vertex[a];

            Vec3.sub(v, vp2, diffvp2);
            Vec3.normalize(diffvp2, diffvp2);
            Vec3.mul(n1, diffvp2, diffvp2);
            Vec3.gt(diffvp2, negEps, diffvp2);

            if ( Vec3.all(diffvp2) ) {
              Vec3.sub(v, vm1, diffvm1);
              Vec3.normalize(diffvm1, diffvm1);
              Vec3.mul(n2, diffvm1, diffvm1);
              Vec3.gt(diffvm1, negEps, diffvm1);

              if ( Vec3.all(diffvm1) ) {
                Vec3.sub(v, vp1, diffvp1);
                Vec3.normalize(diffvp1, diffvp1);
                Vec3.mul(n3, diffvp1, diffvp1);
                Vec3.gt(diffvp1, negEps, diffvp1);

                if ( Vec3.all(diffvp1) ) {
                  positive = false;
                  break;
                }
              }
            }

            Vec3.sub(vm2, vp1, n1);
            Vec3.normalize(n1, n1);
            Vec3.fmod(normal, n1, n1);

          }
        }
      }
    }

    return 0;
  }

  public static void main ( final String[] args ) {

    final Random rng = new Random();

    // final Random rng = new Random();
    // final Gradient grd = new Gradient();

    // final Mesh2 m2 = new Mesh2();
    // Mesh2.polygon(8, PolyType.NGON, m2);
    // m2.subdivFacesFan(1);
    // System.out.println(m2);
    // PApplet.printArray(m2.texCoords);

    // final MeshEntity2 me2 = new MeshEntity2();
    // me2.appendMesh(m2);
    // System.out.println(me2.toBlenderCode());

    final Mesh3 m3 = new Mesh3();
    Mesh3.cube(m3);
    final MeshEntity3 me = new MeshEntity3();
    me.appendMesh(m3);
    // System.out.println(me.toBlenderCode());
  }

  /**
   * Gets the version of the library.
   *
   * @return the version
   */
  public static String version ( ) {

    return CamZup.VERSION;
  }
}
