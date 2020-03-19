package camzup;

import processing.core.PApplet;

import camzup.core.IUtils;
import camzup.core.Mesh3;
import camzup.core.Quaternion;
import camzup.core.Random;
import camzup.core.Transform2;
import camzup.core.Transform3;
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

  /**
   * Returns a string representation of the CamZup class.
   *
   * @return the string
   */
  @Override
  public String toString ( ) {

    return new StringBuilder()
        .append("{ version: ")
        .append(CamZup.VERSION)
        .append(", parent: ")
        .append(this.parent)
        .append(" }")
        .toString();
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

  /**
   * The main function.
   *
   * @param args the string of arguments
   */
  public static void main ( final String[] args ) {

    // final Ray2 r = new Ray2();
    // Ray2.fromPoints(new Vec2(1, 1), new Vec2(-2, 1), r);
    // System.out.println(r);

    // final Random rng = new Random();
    //
    // Mesh2 m = new Mesh2();
    // Mesh2.circle(m);
    //
    // Vec2 lb = new Vec2(-1.0f, -1.0f);
    // Vec2 ub = new Vec2(1.0f, 1.0f);
    // int count = 16;
    // for ( int i = 0; i < count; ++i ) {
    // Vec2 p = Vec2.randomCartesian(rng, lb, ub, new Vec2());
    // boolean eval = Mesh2.contains(m, p);
    // if ( eval ) {
    // System.out.println(p);
    // System.out.println(Vec2.mag(p));
    // }
    // }

    final Random rng = new Random();
    final Transform2 tr = new Transform2(
        Vec2.random(rng, new Vec2()),
        rng.uniform(IUtils.TAU),
        Vec2.random(rng, new Vec2()));
    System.out.println(tr);
    final Vec2 a = Vec2.random(rng, new Vec2());
    final Vec2 b = new Vec2();
    final Vec2 c = new Vec2();
    Transform2.mulPoint(tr, a, b);
    Transform2.invMulPoint(tr, b, c);
    System.out.println(a);
    System.out.println(b);
    System.out.println(c);

    final Quaternion q = Quaternion.random(rng, new Quaternion());
    final Transform3 tr3 = new Transform3(
        Vec3.random(rng, new Vec3()),
        q,
        Vec3.random(rng, new Vec3()));
    System.out.println(tr3);
    final Vec3 a3 = Vec3.random(rng, new Vec3());
    final Vec3 b3 = new Vec3();
    final Vec3 c3 = new Vec3();
    Transform3.mulPoint(tr3, a3, b3);
    Transform3.invMulPoint(tr3, b3, c3);
    System.out.println(a3.toString(7));
    System.out.println(b3);
    System.out.println(c3.toString(7));
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
