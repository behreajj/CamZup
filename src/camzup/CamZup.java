package camzup;

import java.util.ArrayList;

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

  private static int[] convertIntegers (
      final ArrayList < Integer > integers ) {

    final int[] ret = new int[integers.size()];
    for ( int i = 0; i < ret.length; i++ ) {
      ret[i] = integers.get(i).intValue();
    }
    return ret;
  }

  private static void detectbadUvs ( final Mesh3 mesh ) {

    /* Cf. http://mft-dev.dk/uv-mapping-sphere/ */
    final int[][][] faces = mesh.faces;
    final int fsLen = faces.length;
    final Vec2[] vts = mesh.texCoords;

    final Vec2 ba = new Vec2();
    final Vec2 ca = new Vec2();

    for ( int i = 0; i < fsLen; ++i ) {
      final int[][] face = faces[i];
      final int fLen = face.length;
      for ( int j = 0; j < fLen; ++j ) {
        final int k = (j + 1) % fLen;
        final int l = (j + 2) % fLen;

        final int[] a = face[j];
        final int[] b = face[k];
        final int[] c = face[l];

        final int ai = a[1];
        final int bi = b[1];
        final int ci = c[1];

        final Vec2 vta = vts[ai];
        final Vec2 vtb = vts[bi];
        final Vec2 vtc = vts[ci];

        Vec2.sub(vtb, vta, ba);
        Vec2.sub(vtc, vta, ca);

        final float nz = Vec2.cross(ba, ca);
        if ( nz < 0.0f ) {
          // Add, this uv is problematic...
        }
      }
    }
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

  /**
   * The main function.
   *
   * @param args the string of arguments
   */
  public static void main ( final String[] args ) {

    final Random rng = new Random();

    // Vec2[] coordsSort = {
    // new Vec2(-0.409091f, -0.23583901f),
    // new Vec2(-0.22727299f, -0.23583901f),
    // new Vec2(-0.04545501f, -0.23583901f),
    // new Vec2(0.13636398f, -0.23583901f),
    // new Vec2(0.31818098f, -0.23583901f),
    // new Vec2(-0.5f, -0.07861298f),
    // new Vec2(-0.318182f, -0.07861298f),
    // new Vec2(-0.13636401f, -0.07861298f),
    // new Vec2(0.04545498f, -0.07861298f),
    // new Vec2(0.22727299f, -0.07861298f),
    // new Vec2(0.409091f, -0.07861298f),
    // new Vec2(-0.409091f, 0.07861301f),
    // new Vec2(-0.22727299f, 0.07861301f),
    // new Vec2(-0.04545501f, 0.07861301f),
    // new Vec2(0.13636398f, 0.07861301f),
    // new Vec2(0.318182f, 0.07861301f),
    // new Vec2(0.5f, 0.07861301f),
    // new Vec2(-0.318182f, 0.23583901f),
    // new Vec2(-0.13636401f, 0.23583901f),
    // new Vec2(0.04545498f, 0.23583901f),
    // new Vec2(0.22727299f, 0.23583901f),
    // new Vec2(0.409091f, 0.23583901f)
    // };

    // Vec2[] texCoordsSort = {
    // new Vec2(0.090909f, 1.0f),
    // new Vec2(0.272727f, 1.0f),
    // new Vec2(0.454545f, 1.0f),
    // new Vec2(0.636364f, 1.0f),
    // new Vec2(0.818181f, 1.0f),
    // new Vec2(0.0f, 0.66666667f),
    // new Vec2(0.181818f, 0.66666667f),
    // new Vec2(0.363636f, 0.66666667f),
    // new Vec2(0.545455f, 0.66666667f),
    // new Vec2(0.727273f, 0.66666667f),
    // new Vec2(0.909091f, 0.66666667f),
    // new Vec2(0.090909f, 0.3333334f),
    // new Vec2(0.272727f, 0.3333334f),
    // new Vec2(0.454545f, 0.3333334f),
    // new Vec2(0.636364f, 0.3333334f),
    // new Vec2(0.818182f, 0.3333334f),
    // new Vec2(1.0f, 0.3333334f),
    // new Vec2(0.181818f, 0.0f),
    // new Vec2(0.363636f, 0.0f),
    // new Vec2(0.545455f, 0.0f),
    // new Vec2(0.727273f, 0.0f),
    // new Vec2(0.909091f, 0.0f)
    // };

    // int[][][] facesSort = {
    // { { 0, 0 }, { 6, 6 }, { 5, 5 } },
    // { { 1, 1 }, { 7, 7 }, { 6, 6 } },
    // { { 2, 2 }, { 8, 8 }, { 7, 7 } },
    // { { 3, 3 }, { 9, 9 }, { 8, 8 } },
    // { { 4, 4 }, { 10, 10 }, { 9, 9 } },
    // { { 6, 6 }, { 11, 11 }, { 5, 5 } },
    // { { 7, 7 }, { 12, 12 }, { 6, 6 } },
    // { { 8, 8 }, { 13, 13 }, { 7, 7 } },
    // { { 9, 9 }, { 14, 14 }, { 8, 8 } },
    // { { 10, 10 }, { 15, 15 }, { 9, 9 } },
    // { { 6, 6 }, { 12, 12 }, { 11, 11 } },
    // { { 7, 7 }, { 13, 13 }, { 12, 12 } },
    // { { 8, 8 }, { 14, 14 }, { 13, 13 } },
    // { { 9, 9 }, { 15, 15 }, { 14, 14 } },
    // { { 10, 10 }, { 16, 16 }, { 15, 15 } },
    // { { 12, 12 }, { 17, 17 }, { 11, 11 } },
    // { { 13, 13 }, { 18, 18 }, { 12, 12 } },
    // { { 14, 14 }, { 19, 19 }, { 13, 13 } },
    // { { 15, 15 }, { 20, 20 }, { 14, 14 } },
    // { { 16, 16 }, { 21, 21 }, { 15, 15 } }
    // };

    // Mesh2 icosaMap = new Mesh2(facesSort, coordsSort, texCoordsSort);
    // icosaMap.sort();
    // final MeshEntity2 entity2 = new MeshEntity2();
    // entity2.append(icosaMap);
    // final String str2 = entity2.toBlenderCode();
    // System.out.println(str2);
    // System.out.println(mesh3.toString(8));

    final Mesh3 mesh3 = new Mesh3();
    Mesh3.icosahedron(mesh3);
    // mesh3.sort();

    // for ( int i = 0; i < mesh3.coords.length; ++i ) {
    // Vec3 co = mesh3.coords[i];
    // System.out.println("target.coords[" + i + "].set(" + co.x + "f, " +
    // co.y
    // + "f, " + co.z + "f);");
    // }
    //
    // System.out.println("");
    // for ( int i = 0; i < mesh3.texCoords.length; ++i ) {
    // Vec2 st = mesh3.texCoords[i];
    // System.out
    // .println("target.texCoords[" + i + "].set(" + st.x + "f, " + st.y
    // + "f);");
    // }
    //
    // System.out.println("");
    // for ( int i = 0; i < mesh3.normals.length; ++i ) {
    // Vec3 nm = mesh3.normals[i];
    // System.out.println("target.normals[" + i + "].set(" + nm.x + "f, "
    // + nm.y
    // + "f, " + nm.z + "f);");
    // }
    //
    // System.out.println("");
    // for ( int i = 0; i < mesh3.faces.length; ++i ) {
    // System.out.print("{ ");
    // for ( int j = 0; j < mesh3.faces[i].length; ++j ) {
    // System.out.print("{ ");
    // System.out.print(mesh3.faces[i][j][0]);
    // System.out.print(", ");
    // System.out.print(mesh3.faces[i][j][1]);
    // System.out.print(", ");
    // System.out.print(mesh3.faces[i][j][2]);
    // System.out.print(" }");
    // if ( j < mesh3.faces[i].length - 1 ) { System.out.print(", "); }
    // }
    // System.out.println(" },");
    // }

    final MeshEntity3 entity3 = new MeshEntity3();
    entity3.append(mesh3);
    final String str3 = entity3.toBlenderCode();
    // System.out.println(str3);

    // System.out.println(toHardCode(mesh3, 4));
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
