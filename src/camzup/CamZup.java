package camzup;

import java.util.HashSet;

import processing.core.PApplet;

import camzup.core.Gradient;
import camzup.core.Mesh2;
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
    for (int i = 0; i < sourceLen; ++i) {
      totalLen += arr[i].length;
    }

    final float[] result = new float[totalLen];
    for (int j = 0, i = 0; i < sourceLen; ++i) {
      final float[] arrInner = arr[i];
      final int len = arrInner.length;
      System.arraycopy(arrInner, 0, result, j, len);
      j += len;
    }

    return result;
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
    for (int i = 0; i < flen0; ++i) {
      final int[][] f = fs[i];
      final int flen1 = f.length;
      sb.append("this.beginShape(PConstants.POLYGON);\n");
      sb.append("this.texture(txtr);\n");
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

        sb.append("this.vertex(\n");

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

    final Random rng = new Random();
    final Gradient grd = new Gradient();

    final Mesh2 m2 = new Mesh2();
    Mesh2.polygon(5, m2);
    final Mesh3 m3 = new Mesh3(m2);
    m3.extrudeFace(0, 1.0f);

    // System.out.println(m);
    final MeshEntity3 me = new MeshEntity3();
    me.appendMesh(m3);
    System.out.println(me.toBlenderCode());
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
