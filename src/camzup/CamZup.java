package camzup;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import processing.core.PApplet;

import camzup.core.IUtils;
import camzup.core.Mesh2;
import camzup.core.Mesh3;
import camzup.core.MeshEntity2;
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

  /**
   * The library's current version.
   */
  public final static String VERSION = "##library.prettyVersion##";

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

  private static Mesh3 toSphere ( final Mesh3 m ) {

    for ( int i = 0; i < m.coords.length; ++i ) {
      Vec3.rescale(m.coords[i], 0.5f, m.coords[i]);
    }
    m.calcNormals();
    return m;
  }

  private static Vec2[] unique ( final Vec2[] v ) {

    final SortedSet < Vec2 > vsUnique = new TreeSet <>(new SortQuantized2());
    final int vlen = v.length;
    for ( int i = 0; i < vlen; ++i ) {
      vsUnique.add(v[i]);
    }
    return vsUnique.toArray(new Vec2[vsUnique.size()]);
  }

  /**
   * The main function.
   *
   * @param args the string of arguments
   */
  public static void main ( final String[] args ) {

    final Random rng = new Random();
    // Vec2[] x = new Vec2[] {
    // Vec2.random(rng, new Vec2()),
    // Vec2.right(new Vec2()),
    // Vec2.right(new Vec2()),
    // Vec2.random(rng, new Vec2()) };
    // System.out.println(Utils.toString(x));
    // x = CamZup.unique(x);
    // System.out.println(Utils.toString(x));

    final Mesh2 polygon = new Mesh2();
    Mesh2.polygon(6, polygon);
    final MeshEntity2 me2 = new MeshEntity2();
    me2.append(polygon);
    final String str = me2.toBlenderCode();
    System.out.println(str);
  }

  /**
   * Gets the version of the library.
   *
   * @return the version
   */
  public static String version ( ) { return CamZup.VERSION; }

  /**
   * Compares two vectors by their quantized y component, then by their
   * x component.
   */
  private static final class SortQuantized2 implements Comparator < Vec2 > {

    /**
     * Internal vector to hold quantized left operand.
     */
    final private Vec2 qa;

    /**
     * Internal vector to hold quantized right operand.
     */
    final private Vec2 qb;

    /**
     * Quantization level.
     */
    final public int levels;

    {
      this.qa = new Vec2();
      this.qb = new Vec2();
    }

    /**
     * The default constructor.
     */
    public SortQuantized2 ( ) {

      this((int) (1.0f / IUtils.DEFAULT_EPSILON));
    }

    /**
     * Creates a quantized sorter with the specified number of levels.
     *
     * @param levels quantization levels
     */
    public SortQuantized2 ( final int levels ) {

      this.levels = levels;
    }

    /**
     * Compares the quantized y and x components of the comparisand
     * vectors.
     *
     * @param a the left comparisand
     * @param b the right comparisand
     * @return the evaluation
     */
    @Override
    public int compare ( final Vec2 a, final Vec2 b ) {

      Vec2.quantize(a, this.levels, this.qa);
      Vec2.quantize(b, this.levels, this.qb);
      return this.qa.compareTo(this.qb);
    }

    /**
     * Returns the simple name of this class.
     *
     * @return the string
     */
    @Override
    public String toString ( ) {

      return this.getClass().getSimpleName();
    }
  }
}
