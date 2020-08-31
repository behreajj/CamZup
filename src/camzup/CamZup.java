package camzup;

import camzup.core.IUtils;
import camzup.core.Mesh3;
import camzup.core.PolyType;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;

import processing.core.PApplet;

/**
 * The main class of this library. This is not needed to use the library
 * and is for development and debugging only.
 */
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
   public CamZup ( final PApplet parent ) { this.parent = parent; }

   /**
    * Returns a string representation of the CamZup class.
    *
    * @return the string
    */
   @Override
   public String toString ( ) {

      final StringBuilder sb = new StringBuilder(64);
      sb.append("{ version: ");
      sb.append(CamZup.VERSION);
      sb.append(", parent: ");
      sb.append(this.parent);
      sb.append(" }");
      return sb.toString();
   }

   /**
    * The library's current version.
    */
   public static final String VERSION = "##library.prettyVersion##";

   public static Mesh3 capsule ( final int longitudes, final int latitudes,
      final int rings, final float depth, final float radius,
      final PolyType poly, final Mesh3 target ) {

      /* Validate input arguments. */
      final int vLons = longitudes < 3 ? 3 : longitudes;
      final int vLats = latitudes < 2 ? 2 : latitudes % 2 != 0 ? latitudes + 1
         : latitudes;
      final int vSections = rings < 0 ? 0 : rings;
      final float vDepth = Utils.max(IUtils.EPSILON, depth);
      final float vRad = Utils.max(IUtils.EPSILON, radius);

      /* Boolean flags that change number of faces and vertices. */
      final boolean useQuads = poly == PolyType.QUAD;
      final boolean calcMid = vSections > 0;

      final int halfLat = vLats / 2;
      final int halfLatn2 = halfLat - 2;
      final int vSectionsp1 = vSections + 1;
      final int vLonsp1 = vLons + 1;

      /* Find index offsets for coordinates. */
      final int idx_v_n_equator = vLonsp1 + vLons * halfLatn2;
      final int idx_v_cyl = idx_v_n_equator + vLons;
      int idx_v_s_equator = idx_v_cyl;
      if ( calcMid ) { idx_v_s_equator += vLons * vSections; }
      final int idx_v_south = idx_v_s_equator + vLons;
      final int idx_v_south_cap = idx_v_south + vLons * halfLatn2;
      final int idx_v_south_pole = idx_v_south_cap + vLons;

      // TODO: Face indices are probably linking to the wrong texture
      // coordinates -- at the equator -- when forming topmost triangle.
      final int idx_vt_n_cap = 0;
      final int idx_vt_n_hemi = idx_vt_n_cap + vLons;
      final int idx_vt_n_equator = idx_vt_n_hemi + vLonsp1 * halfLatn2;
      int idx_vt_s_equator = idx_vt_n_equator + vLonsp1;
      if ( calcMid ) { idx_vt_s_equator += vLonsp1 * vSections; }
      final int idx_vt_s_hemi = idx_vt_s_equator + vLonsp1;
      final int idx_vt_s_cap = idx_vt_s_hemi + vLonsp1 * halfLatn2;

      /* Resize coordinates to new length. */
      final int len_vs = idx_v_south_pole + 1;
      final Vec3[] vs = target.coords = Vec3.resize(target.coords, len_vs);

      /* Find index offsets for normals. */
      final int idx_vn_south = idx_v_n_equator + vLons;
      final int idx_vn_south_cap = idx_vn_south + vLons * halfLatn2;
      final int idx_vn_south_pole = idx_vn_south_cap + vLons;

      /* Resize normals to new length. */
      final int len_vns = idx_vn_south_pole + 1;
      final Vec3[] vns = target.normals = Vec3.resize(target.normals, len_vns);

      /* Set North and South pole. */
      final float half_depth = vDepth * 0.5f;
      final float summit = half_depth + vRad;
      vs[0].set(0.0f, 0.0f, summit);
      vs[idx_v_south_pole].set(0.0f, 0.0f, -summit);

      /* Set North and South Normals. */
      vns[0].set(0.0f, 0.0f, 1.0f);
      vns[idx_vn_south_pole].set(0.0f, 0.0f, -1.0f);

      /* Calculate theta and equators. */
      final float[] tSin = new float[vLons];
      final float[] tCos = new float[vLons];
      final float toTheta = IUtils.TAU / vLons;
      for ( int j = 0; j < vLons; ++j ) {
         final float theta = j * toTheta;

         final float sinTheta = Utils.sin(theta);
         final float cosTheta = Utils.cos(theta);

         tSin[j] = sinTheta;
         tCos[j] = cosTheta;

         final float x = vRad * cosTheta;
         final float y = vRad * sinTheta;

         /* Set equatorial coordinates. */
         vs[idx_v_n_equator + j].set(x, y, half_depth);
         vs[idx_v_s_equator + j].set(x, y, -half_depth);

         /* Set equatorial normals. */
         vns[idx_v_n_equator + j].set(cosTheta, sinTheta, 0.0f);
      }

      /* Divide latitudes into hemispheres. */
      final int half_lat_n1 = halfLat - 1;
      final float quarterTau = IUtils.HALF_PI;
      final float toPhi = IUtils.PI / vLats;
      for ( int k = 0, i = 0; i < half_lat_n1; ++i ) {
         final int i_p1 = i + 1;

         /* North. */
         final float nPhi = -quarterTau + i_p1 * toPhi;
         final float nSinPhi = Utils.sin(nPhi);
         final float nCosPhi = Utils.cos(nPhi);

         /* For North coordinates, multiply by radius and offset. */
         final float nRhoCosPhi = vRad * nCosPhi;
         final float nRhoSinPhi = vRad * nSinPhi;
         final float offsetNorth = half_depth - nRhoSinPhi;

         /* South. */
         final float sPhi = -quarterTau + ( i_p1 + halfLat ) * toPhi;
         final float sSinPhi = Utils.sin(sPhi);
         final float sCosPhi = Utils.cos(sPhi);

         /* For South coordinates, multiply by radius and offset. */
         final float sRhoCosPhi = vRad * sCosPhi;
         final float sRhoSinPhi = vRad * sSinPhi;
         final float offsetSouth = -half_depth - sRhoSinPhi;

         for ( int j = 0; j < vLons; ++j, ++k ) {
            final float sinTheta = tSin[j];
            final float cosTheta = tCos[j];

            /* North coordinate. */
            vs[1 + k].set(nRhoCosPhi * cosTheta, nRhoCosPhi * sinTheta,
               offsetNorth);

            /* South coordinate. */
            vs[idx_v_south + k].set(sRhoCosPhi * cosTheta, sRhoCosPhi
               * sinTheta, offsetSouth);

            /* North normal. */
            vns[1 + k].set(nCosPhi * cosTheta, nCosPhi * sinTheta, -nRhoSinPhi);

            /* South normal. */
            vns[idx_vn_south + k].set(sCosPhi * cosTheta, sCosPhi * sinTheta,
               -sRhoSinPhi);
         }
      }

      /* Calculate sections of cylinder in middle. */
      if ( calcMid ) {
         final float toFac = 1.0f / vSectionsp1;
         for ( int k = 0, m = 0; m < vSections; ++m ) {
            final float fac = ( m + 1.0f ) * toFac;
            final float cmpl_fac = 1.0f - fac;

            for ( int j = 0; j < vLons; ++j, ++k ) {
               final Vec3 vNorth = vs[idx_v_n_equator + j];
               final Vec3 vSouth = vs[idx_v_s_equator + j];

               vs[idx_v_cyl + k].set(cmpl_fac * vNorth.x + fac * vSouth.x,
                  cmpl_fac * vNorth.y + fac * vSouth.y, cmpl_fac * vNorth.z
                     + fac * vSouth.z);
            }
         }
      }

      final int v_lons_half_lat_n1 = ( halfLat - 1 ) * vLons;
      final int v_lons_v_sections_p1 = ( vSections + 1 ) * vLons;

      /* Find index offsets for face indices. */
      int idx_fs_cyl = vLons + v_lons_half_lat_n1;
      if ( !useQuads ) { idx_fs_cyl += v_lons_half_lat_n1; }
      int idx_fs_south_equat = idx_fs_cyl + v_lons_v_sections_p1;
      if ( !useQuads ) { idx_fs_south_equat += v_lons_v_sections_p1; }
      int idx_fs_south_hemi = idx_fs_south_equat + v_lons_half_lat_n1;
      if ( !useQuads ) { idx_fs_south_hemi += v_lons_half_lat_n1; }
      final int idx_fs_south_cap = idx_fs_south_hemi + vLons;

      /* Resize face indices to new length. */
      final int len_v_indices = idx_fs_south_cap;
      final int[][][] fs = target.faces = new int[len_v_indices][][];

      /* North & South cap indices (always triangles). */
      for ( int j = 0; j < vLons; ++j ) {
         final int j_next = ( j + 1 ) % vLons;

         /* North coordinate indices. */
         final int[][] triNorth = fs[j] = new int[3][3];
         triNorth[0][0] = 1 + j_next;
         triNorth[1][0] = 1 + j;
         triNorth[2][0] = 0;

         /* South coordinates indices. */
         final int[][] triSouth = fs[idx_fs_south_hemi + j] = new int[3][3];
         triSouth[0][0] = idx_v_south_pole;
         triSouth[1][0] = idx_v_south_cap + j;
         triSouth[2][0] = idx_v_south_cap + j_next;

         /* North texture coordinate indices. */
         triNorth[0][1] = vLonsp1 + j;
         triNorth[1][1] = vLons + j;
         triNorth[2][1] = j;

         /* North normals. */
         triNorth[0][2] = 1 + j_next;
         triNorth[1][2] = 1 + j;
         triNorth[2][2] = 0;

         // /* South texture coordinate indices. */
         // triSouth[0][1] = idx_vt_south_cap + j;
         // triSouth[1][1] = idx_vt_south_cap - vLonsp1 + j;
         // triSouth[2][1] = idx_vt_south_cap - vLonsp1 + j + 1;

         /* South normals. */
         triSouth[0][2] = idx_vn_south_pole;
         triSouth[1][2] = idx_vn_south_cap + j;
         triSouth[2][2] = idx_vn_south_cap + j_next;
      }

      /* Hemisphere indices. */
      for ( int k = 0, i = 0; i < half_lat_n1; ++i ) {
         final int i_v_lons = i * vLons;

         /* North coordinate index offset. */
         final int v_curr_lat_n = 1 + i_v_lons;
         final int v_next_lat_n = v_curr_lat_n + vLons;

         /* South coordinate index offset. */
         final int v_curr_lat_s = idx_v_s_equator + i_v_lons;
         final int v_next_lat_s = v_curr_lat_s + vLons;

         /* North texture coordinate index offset. */
         final int vt_curr_lat_n = idx_vt_n_hemi + i * vLonsp1;
         final int vt_next_lat_n = vt_curr_lat_n + vLonsp1;

         /* South texture coordinate index offset, */
         // final int vt_curr_lat_s = idx_vt_s_equator - vLonsp1 + i * vLonsp1;
         // final int vt_next_lat_s = vt_curr_lat_s + vLonsp1;

         /* North normal index offset. */
         final int vn_curr_lat_n = 1 + i_v_lons;
         final int vn_next_lat_n = vn_curr_lat_n + vLons;

         /* South normal index offset. */
         final int vn_curr_lat_s = idx_v_n_equator + i_v_lons;
         final int vn_next_lat_s = vn_curr_lat_s + vLons;

         for ( int j = 0; j < vLons; ++j ) {
            final int v_next_lon = ( j + 1 ) % vLons;

            /* North coordinate indices. */
            final int n00 = v_curr_lat_n + j;
            final int n10 = v_curr_lat_n + v_next_lon;
            final int n11 = v_next_lat_n + v_next_lon;
            final int n01 = v_next_lat_n + j;

            /* South coordinate indices. */
            final int s00 = v_curr_lat_s + j;
            final int s10 = v_curr_lat_s + v_next_lon;
            final int s11 = v_next_lat_s + v_next_lon;
            final int s01 = v_next_lat_s + j;

            /* North texture coordinate indices. */
            final int vtn00 = vt_curr_lat_n + j;
            final int vtn10 = vt_curr_lat_n + j + 1;
            final int vtn11 = vt_next_lat_n + j + 1;
            final int vtn01 = vt_next_lat_n + j;

            /* South texture coordinate indices. */
            // final int vts00 = vt_curr_lat_s + j;
            // final int vts10 = vt_curr_lat_s + j + 1;
            // final int vts11 = vt_next_lat_s + j + 1;
            // final int vts01 = vt_next_lat_s + j;

            /* North normal indices. */
            final int vnn00 = vn_curr_lat_n + j;
            final int vnn10 = vn_curr_lat_n + v_next_lon;
            final int vnn11 = vn_next_lat_n + v_next_lon;
            final int vnn01 = vn_next_lat_n + j;

            /* South normal indices. */
            final int vns00 = vn_curr_lat_s + j;
            final int vns10 = vn_curr_lat_s + v_next_lon;
            final int vns11 = vn_next_lat_s + v_next_lon;
            final int vns01 = vn_next_lat_s + j;

            if ( useQuads ) {
               final int[][] nQuad = fs[vLons + k] = new int[4][3];
               final int[][] sQuad = fs[idx_fs_south_equat + k] = new int[4][3];

               /* North coordinate quad. */
               nQuad[0][0] = n00;
               nQuad[1][0] = n10;
               nQuad[2][0] = n11;
               nQuad[3][0] = n01;

               /* South coordinate quad. */
               sQuad[0][0] = s00;
               sQuad[1][0] = s10;
               sQuad[2][0] = s11;
               sQuad[3][0] = s01;

               /* North texture coordinate quad. */
               nQuad[0][1] = vtn00;
               nQuad[1][1] = vtn10;
               nQuad[2][1] = vtn11;
               nQuad[3][1] = vtn01;

               /* South texture coordinate quad. */
               // sQuad[0][1] = vts00;
               // sQuad[1][1] = vts10;
               // sQuad[2][1] = vts11;
               // sQuad[3][1] = vts01;

               /* North normal quad. */
               nQuad[0][2] = vnn00;
               nQuad[1][2] = vnn10;
               nQuad[2][2] = vnn11;
               nQuad[3][2] = vnn01;

               /* South normal quad. */
               sQuad[0][2] = vns00;
               sQuad[1][2] = vns10;
               sQuad[2][2] = vns11;
               sQuad[3][2] = vns01;

               k += 1;
            } else {
               final int k_next = k + 1;

               /* North coordinate triangle 0. */
               final int[][] nTri0 = fs[vLons + k] = new int[3][3];
               nTri0[0][0] = n00;
               nTri0[1][0] = n10;
               nTri0[2][0] = n11;

               /* North coordinate triangle 1. */
               final int[][] nTri1 = fs[vLons + k_next] = new int[3][3];
               nTri1[0][0] = n00;
               nTri1[1][0] = n11;
               nTri1[2][0] = n01;

               /* South coordinate triangle 0. */
               final int[][] sTri0 = fs[idx_fs_south_equat + k] = new int[3][3];
               sTri0[0][0] = s00;
               sTri0[1][0] = s10;
               sTri0[2][0] = s11;

               /* South coordinate triangle 1. */
               final int[][] sTri1 = fs[idx_fs_south_equat + k_next]
                  = new int[3][3];
               sTri1[0][0] = s00;
               sTri1[1][0] = s11;
               sTri1[2][0] = s01;

               /* North texture coordinate triangle 0. */
               nTri0[0][1] = vtn00;
               nTri0[1][1] = vtn10;
               nTri0[2][1] = vtn11;

               /* North texture coordinate triangle 1. */
               nTri1[0][1] = vtn00;
               nTri1[1][1] = vtn11;
               nTri1[2][1] = vtn01;

               /* South texture coordinate triangle 0. */
               // sTri0[0][1] = vts00;
               // sTri0[1][1] = vts10;
               // sTri0[2][1] = vts11;

               /* South texture coordinate triangle 1. */
               // sTri1[0][1] = vts00;
               // sTri1[1][1] = vts11;
               // sTri1[2][1] = vts01;

               /* North normal triangle 0. */
               nTri0[0][2] = vnn00;
               nTri0[1][2] = vnn10;
               nTri0[2][2] = vnn11;

               /* North normal triangle 1. */
               nTri1[0][2] = vnn00;
               nTri1[1][2] = vnn11;
               nTri1[2][2] = vnn01;

               /* South normal triangle 0. */
               nTri0[0][2] = vns00;
               nTri0[1][2] = vns10;
               nTri0[2][2] = vns11;

               /* South normal triangle 1. */
               nTri1[0][2] = vns00;
               nTri1[1][2] = vns11;
               nTri1[2][2] = vns01;

               k += 2;
            }
         }
      }

      /* Cylinder indices. */
      for ( int k = 0, m = 0; m < vSectionsp1; ++m ) {
         final int v_curr_ring = idx_v_n_equator + m * vLons;
         final int v_next_ring = v_curr_ring + vLons;

         for ( int j = 0; j < vLons; ++j ) {
            final int v_next_lon = ( j + 1 ) % vLons;

            /* Normal corners. */
            final int vn0 = idx_v_n_equator + j;
            final int vn1 = idx_v_n_equator + v_next_lon;

            /* Coordinate corners. */
            final int v00 = v_curr_ring + j;
            final int v10 = v_curr_ring + v_next_lon;
            final int v11 = v_next_ring + v_next_lon;
            final int v01 = v_next_ring + j;

            if ( useQuads ) {
               final int[][] quad = fs[idx_fs_cyl + k] = new int[4][3];

               /* Coordinates. */
               quad[0][0] = v00;
               quad[1][0] = v10;
               quad[2][0] = v11;
               quad[3][0] = v01;

               /* Normals. */
               quad[0][2] = vn0;
               quad[1][2] = vn1;
               quad[2][2] = vn1;
               quad[3][2] = vn0;

               k += 1;
            } else {
               final int k_next = k + 1;

               final int[][] tri0 = fs[idx_fs_cyl + k] = new int[3][3];
               final int[][] tri1 = fs[idx_fs_cyl + k_next] = new int[3][3];

               /* Coordinates. */
               tri0[0][0] = v00;
               tri0[1][0] = v10;
               tri0[2][0] = v11;

               tri1[0][0] = v00;
               tri1[1][0] = v11;
               tri1[2][0] = v01;

               /* Normals. */
               tri0[0][2] = vn0;
               tri0[1][2] = vn1;
               tri0[2][2] = vn1;

               tri1[0][2] = vn0;
               tri1[1][2] = vn1;
               tri1[2][2] = vn0;

               k += 2;
            }
         }
      }

      /* Resize texture coordinates array. */
      final int len_vts = idx_vt_s_cap + vLons;
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
         len_vts);

      /* Horizontal. */
      final float to_tex_s = 1.0f / vLons;
      final float[] tc_s = new float[vLonsp1];
      for ( int j = 0; j < vLonsp1; ++j ) { tc_s[j] = j * to_tex_s; }

      final float vtNorthPole = 0.0f;
      final float vtNorthHemi = 0.25f;
      final float vtSouthHemi = 0.75f;
      final float vtSouthPole = 1.0f;

      /* Calculate polar texture coordinates. */
      for ( int j = 0; j < vLons; ++j ) {
         final float s_tex = ( j + 0.5f ) * to_tex_s;
         vts[idx_vt_n_cap + j].set(s_tex, vtNorthPole);
         vts[idx_vt_s_cap + j].set(s_tex, vtSouthPole);
      }

      /* Calculate hemisphere texture coordinates. */
      for ( int k = 0, i = 0; i < half_lat_n1; ++i ) {
         final float yFac = ( i + 1.0f ) / ( half_lat_n1 - 1.0f );
         final float complyFac = 1.0f - yFac;

         for ( int j = 0; j < vLonsp1; ++j, ++k ) {
            vts[idx_vt_n_hemi + k].set(tc_s[j], complyFac * vtNorthPole + yFac
               * vtNorthHemi);
            // vts[idx_vt_south + k].set(tc_s[j], complyFac * vtSouthHemi + yFac
            // * vtSouthPole);
         }
      }

      /* Calculate equatorial texture coordinates. */
      for ( int j = 0; j < vLonsp1; ++j ) {
         final float s = tc_s[j];
         vts[idx_vt_n_equator + j].set(s, vtNorthHemi);
         vts[idx_vt_s_equator + j].set(s, vtSouthHemi);
      }

      // TODO: This is wrong, between the polar caps and the equator, we have
      // the hemispheres...

      /* Calculate sections of cylinder in middle. */
      if ( calcMid ) {
         final float to_fac = 1.0f / vSectionsp1;
         for ( int k = 0, m = 0; m < vSections; ++m ) {
            final float fac = ( m + 1.0f ) * to_fac;
            final float cmpl_fac = 1.0f - fac;

            for ( int j = 0; j < vLonsp1; ++j, ++k ) {
               final Vec2 vtNorth = vts[idx_vt_n_equator + j];
               final Vec2 vtSouth = vts[idx_vt_s_equator + j];

               // vts[idx_vt_n_equator + k].set(cmpl_fac * vtNorth.x + fac
               // * vtSouth.x,
               // cmpl_fac * vtNorthHemi + fac * vtSouthHemi);
            }
         }
      }

      return target;
   }

   /**
    * The main function.
    *
    * @param args the string of arguments
    */
   public static void main ( final String[] args ) {

      // Mesh2 m2 = new Mesh2();
      // Mesh2.hexGrid(8, 1f, 0.05f, m2);
      // m2.calcUvs();
      // MeshEntity2 me2 = new MeshEntity2();
      // me2.append(m2);
      // String pyCd = me2.toBlenderCode();
      // System.out.println(pyCd);


   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
