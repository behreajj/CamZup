package camzup;

import camzup.core.IUtils;
import camzup.core.Mesh;
import camzup.core.Mesh.PolyType;
import camzup.core.Mesh2;
import camzup.core.Mesh3;
import camzup.core.MeshEntity3;
import camzup.core.Rng;
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
   public final static String VERSION = "##library.prettyVersion##";

   public static Mesh3 capsule ( final int longitudes, final int latitudes,
      final int rings, final float depth, final float radius,
      final Mesh.PolyType poly, final Mesh3 target ) {

      /* Validate input arguments. */
      final int v_lons = longitudes < 3 ? 3 : longitudes;
      final int v_lats = latitudes < 2 ? 2 : latitudes % 2 != 0 ? latitudes + 1
         : latitudes;
      final int v_sections = rings < 0 ? 0 : rings;
      final float v_depth = Utils.max(IUtils.DEFAULT_EPSILON, depth);
      final float v_rad = Utils.max(IUtils.DEFAULT_EPSILON, radius);

      /* Boolean flags that change number of faces and vertices. */
      final boolean use_quads = poly == PolyType.QUAD;
      final boolean calc_mid = v_sections > 0;

      final int half_lat = v_lats / 2;
      final int half_lat_n2 = half_lat - 2;
      final int v_sections_p1 = v_sections + 1;

      /* Find index offsets for coordinates. */
      final int idx_v_north = 1 + v_lons;
      final int idx_v_n_equator = idx_v_north + v_lons * half_lat_n2;
      final int idx_v_cyl = idx_v_n_equator + v_lons;
      int idx_v_s_equator = idx_v_cyl;
      if ( calc_mid ) { idx_v_s_equator += v_lons * v_sections; }
      final int idx_v_south = idx_v_s_equator + v_lons;
      final int idx_v_south_cap = idx_v_south + v_lons * half_lat_n2;
      final int idx_v_south_pole = idx_v_south_cap + v_lons;

      /* Resize coordinates to new length. */
      final int len_vs = idx_v_south_pole + 1;
      final Vec3[] vs = target.coords = Vec3.resize(target.coords, len_vs);

      /* Find index offsets for normals. */
      final int idx_vn_north = 1 + v_lons;
      final int idx_vn_equator = idx_vn_north + v_lons * half_lat_n2;
      final int idx_vn_south = idx_vn_equator + v_lons;
      final int idx_vn_south_cap = idx_vn_south + v_lons * half_lat_n2;
      final int idx_vn_south_pole = idx_vn_south_cap + v_lons;

      /* Resize normals to new length. */
      final int len_vns = idx_vn_south_pole + 1;
      final Vec3[] vns = target.normals = Vec3.resize(target.normals, len_vns);

      /* Set North and South pole. */
      final float half_depth = v_depth * 0.5f;
      final float summit = half_depth + v_rad;
      vs[0].set(0.0f, 0.0f, summit);
      vs[idx_v_south_pole].set(0.0f, 0.0f, -summit);

      /* Set North and South Normals. */
      vns[0].set(0.0f, 0.0f, 1.0f);
      vns[idx_vn_south_pole].set(0.0f, 0.0f, -1.0f);

      /* Calculate theta and equators. */
      final float[] sin_t = new float[v_lons];
      final float[] cos_t = new float[v_lons];
      final float to_theta = IUtils.TAU / v_lons;
      for ( int j = 0; j < v_lons; ++j ) {
         final float theta = j * to_theta;

         final float sin_theta = Utils.sin(theta);
         final float cos_theta = Utils.cos(theta);

         sin_t[j] = sin_theta;
         cos_t[j] = cos_theta;

         final float x = v_rad * cos_theta;
         final float y = v_rad * sin_theta;

         /* Set equatorial coordinates. */
         vs[idx_v_n_equator + j].set(x, y, half_depth);
         vs[idx_v_s_equator + j].set(x, y, -half_depth);

         /* Set equatorial normals. */
         vns[idx_vn_equator + j].set(cos_theta, sin_theta, 0.0f);
      }

      /* Divide latitudes into hemispheres. */
      final int half_lat_n1 = half_lat - 1;
      final float quarter_tau = IUtils.HALF_PI;
      final float to_phi = IUtils.PI / v_lats;
      for ( int k = 0, i = 0; i < half_lat_n1; ++i ) {
         final int i_p1 = i + 1;

         /* North. */
         final float phi_n = -quarter_tau + i_p1 * to_phi;
         final float sin_phi_n = Utils.sin(phi_n);
         final float cos_phi_n = Utils.cos(phi_n);

         /* For North coordinates, multiply by radius and offset. */
         final float rho_cos_phi_n = v_rad * cos_phi_n;
         final float rho_sin_phi_n = v_rad * sin_phi_n;
         final float offset_north = half_depth - rho_sin_phi_n;

         /* South. */
         final float phi_s = -quarter_tau + ( i_p1 + half_lat ) * to_phi;
         final float sin_phi_s = Utils.sin(phi_s);
         final float cos_phi_s = Utils.cos(phi_s);

         /* For South coordinates, multiply by radius and offset. */
         final float rho_cos_phi_s = v_rad * cos_phi_s;
         final float rho_sin_phi_s = v_rad * sin_phi_s;
         final float offset_south = -half_depth - rho_sin_phi_s;

         for ( int j = 0; j < v_lons; ++j, ++k ) {
            final float sin_theta = sin_t[j];
            final float cos_theta = cos_t[j];

            /* North coordinate. */
            vs[1 + k].set(rho_cos_phi_n * cos_theta, rho_cos_phi_n * sin_theta,
               offset_north);

            /* South coordinate. */
            vs[idx_v_south + k].set(rho_cos_phi_s * cos_theta, rho_cos_phi_s
               * sin_theta, offset_south);

            /* North normal. */
            vns[1 + k].set(cos_phi_n * cos_theta, cos_phi_n * sin_theta,
               -rho_sin_phi_n);

            /* South normal. */
            vns[idx_vn_south + k].set(cos_phi_s * cos_theta, cos_phi_s
               * sin_theta, -rho_sin_phi_s);
         }
      }

      /* Calculate sections of cylinder in middle. */
      if ( calc_mid ) {
         final float to_fac = 1.0f / v_sections_p1;
         for ( int k = 0, m = 0; m < v_sections; ++m ) {
            final float fac = ( m + 1.0f ) * to_fac;
            final float cmpl_fac = 1.0f - fac;

            for ( int j = 0; j < v_lons; ++j, ++k ) {
               final Vec3 v_north = vs[idx_v_n_equator + j];
               final Vec3 v_south = vs[idx_v_s_equator + j];

               vs[idx_v_cyl + k].set(cmpl_fac * v_north.x + fac * v_south.x,
                  cmpl_fac * v_north.y + fac * v_south.y, cmpl_fac * v_north.z
                     + fac * v_south.z);
            }
         }
      }

      final int v_lons_half_lat_n1 = ( half_lat - 1 ) * v_lons;
      final int v_lons_v_sections_p1 = ( v_sections + 1 ) * v_lons;

      /* Find index offsets for face indices. */
      int idx_fs_cyl = v_lons + v_lons_half_lat_n1;
      if ( !use_quads ) { idx_fs_cyl += v_lons_half_lat_n1; }
      int idx_fs_south_equat = idx_fs_cyl + v_lons_v_sections_p1;
      if ( !use_quads ) { idx_fs_south_equat += v_lons_v_sections_p1; }
      int idx_fs_south_hemi = idx_fs_south_equat + v_lons_half_lat_n1;
      if ( !use_quads ) { idx_fs_south_hemi += v_lons_half_lat_n1; }
      final int idx_fs_south_cap = idx_fs_south_hemi + v_lons;

      /* Resize face indices to new length. */
      final int len_v_indices = idx_fs_south_cap;
      final int[][][] fs = target.faces = new int[len_v_indices][][];

      /* North & South cap indices (always triangles). */
      for ( int j = 0; j < v_lons; ++j ) {
         final int j_next = ( j + 1 ) % v_lons;

         /* North coordinate indices. */
         final int[][] north_tri = fs[j] = new int[3][3];
         north_tri[0][0] = 1 + j_next;
         north_tri[1][0] = 1 + j;
         north_tri[2][0] = 0;

         /* South coordinates indices. */
         final int[][] south_tri = fs[idx_fs_south_hemi + j] = new int[3][3];
         south_tri[0][0] = idx_v_south_pole;
         south_tri[1][0] = idx_v_south_cap + j;
         south_tri[2][0] = idx_v_south_cap + j_next;

         /* North normals. */
         north_tri[0][2] = 1 + j_next;
         north_tri[1][2] = 1 + j;
         north_tri[2][2] = 0;

         /* South normals. */
         south_tri[0][2] = idx_vn_south_pole;
         south_tri[1][2] = idx_vn_south_cap + j;
         south_tri[2][2] = idx_vn_south_cap + j_next;
      }

      // TODO: Fix normal indices in this next section...

      /* Hemisphere indices. */
      for ( int k = 0, i = 0; i < half_lat_n1; ++i ) {
         final int i_v_lons = i * v_lons;

         /* North coordinate index offset. */
         final int v_curr_lat_n = 1 + i_v_lons;
         final int v_next_lat_n = v_curr_lat_n + v_lons;

         /* South coordinate index offset. */
         final int v_curr_lat_s = idx_v_s_equator + i_v_lons;
         final int v_next_lat_s = v_curr_lat_s + v_lons;

         /* North normal index offset. */
         // final int vn_curr_lat_n = 1 + i_v_lons;
         // final int vn_next_lat_n = vn_curr_lat_n + v_lons;

         /* South normal index offset. */
         // final int vn_curr_lat_s = idx_vn_equator + i_v_lons;
         // final int vn_next_lat_s = vn_curr_lat_s + v_lons;

         for ( int j = 0; j < v_lons; ++j ) {
            final int v_next_lon = ( j + 1 ) % v_lons;

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

            /* North normal indices. */
            // final int nn00 = vn_curr_lat_n + j;
            // final int nn10 = vn_curr_lat_n + v_next_lon;
            // final int nn11 = vn_next_lat_n + v_next_lon;
            // final int nn01 = vn_next_lat_n + j;

            /* South normal indices. */
            // final int sn00 = vn_curr_lat_s + j;
            // final int sn10 = vn_curr_lat_s + v_next_lon;
            // final int sn11 = vn_next_lat_s + v_next_lon;
            // final int sn01 = vn_next_lat_s + j;

            if ( use_quads ) {
               final int[][] quad_n = fs[v_lons + k] = new int[4][3];
               final int[][] quad_s = fs[idx_fs_south_equat + k]
                  = new int[4][3];

               /* North coordinate quad. */
               quad_n[0][0] = n00;
               quad_n[1][0] = n10;
               quad_n[2][0] = n11;
               quad_n[3][0] = n01;

               /* South coordinate quad. */
               quad_s[0][0] = s00;
               quad_s[1][0] = s10;
               quad_s[2][0] = s11;
               quad_s[3][0] = s01;

               /* North normal quad. */
               // quad_n[0][2] = nn00;
               // quad_n[1][2] = nn10;
               // quad_n[2][2] = nn11;
               // quad_n[3][2] = nn01;

               /* South normal quad. */
               // quad_s[0][2] = sn00;
               // quad_s[1][2] = sn10;
               // quad_s[2][2] = sn11;
               // quad_s[3][2] = sn01;

               k += 1;
            } else {
               final int k_next = k + 1;

               /* North coordinate triangle 0. */
               final int[][] tri_n0 = fs[v_lons + k] = new int[3][3];
               tri_n0[0][0] = n00;
               tri_n0[1][0] = n10;
               tri_n0[2][0] = n11;

               /* North coordinate triangle 1. */
               final int[][] tri_n1 = fs[v_lons + k_next] = new int[3][3];
               tri_n1[0][0] = n00;
               tri_n1[1][0] = n11;
               tri_n1[2][0] = n01;

               /* South coordinate triangle 0. */
               final int[][] tri_s0 = fs[idx_fs_south_equat + k]
                  = new int[3][3];
               tri_s0[0][0] = s00;
               tri_s0[1][0] = s10;
               tri_s0[2][0] = s11;

               /* South coordinate triangle 1. */
               final int[][] tri_s1 = fs[idx_fs_south_equat + k_next]
                  = new int[3][3];
               tri_s1[0][0] = s00;
               tri_s1[1][0] = s11;
               tri_s1[2][0] = s01;

               k += 2;
            }
         }
      }

      /* Cylinder indices. */
      for ( int k = 0, m = 0; m < v_sections_p1; ++m ) {
         final int v_curr_ring = idx_v_n_equator + m * v_lons;
         final int v_next_ring = v_curr_ring + v_lons;

         for ( int j = 0; j < v_lons; ++j ) {
            final int v_next_lon = ( j + 1 ) % v_lons;

            /* Coordinate corners. */
            final int v00 = v_curr_ring + j;
            final int v10 = v_curr_ring + v_next_lon;
            final int v11 = v_next_ring + v_next_lon;
            final int v01 = v_next_ring + j;

            if ( use_quads ) {
               final int[][] quad = fs[idx_fs_cyl + k] = new int[4][3];

               quad[0][0] = v00;
               quad[1][0] = v10;
               quad[2][0] = v11;
               quad[3][0] = v01;

               k += 1;
            } else {
               final int k_next = k + 1;

               final int[][] tri_0 = fs[idx_fs_cyl + k] = new int[3][3];
               final int[][] tri_1 = fs[idx_fs_cyl + k_next] = new int[3][3];

               tri_0[0][0] = v00;
               tri_0[1][0] = v10;
               tri_0[2][0] = v11;

               tri_1[0][0] = v00;
               tri_1[1][0] = v11;
               tri_1[2][0] = v01;

               k += 2;
            }
         }
      }

      /* Calculate texture coordinates. */
      final int idx_vt_north_cap = 0;
      final int idx_vt_north = idx_vt_north_cap + v_lons;
      final int idx_vt_n_equator = idx_vt_north + ( v_lons + 1 ) * half_lat_n2;
      final int idx_vt_cyl = idx_vt_n_equator + v_lons + 1;
      int idx_vt_s_equator = idx_vt_cyl;
      if ( calc_mid ) { idx_vt_s_equator += ( v_lons + 1 ) * v_sections; }
      final int idx_vt_south = idx_vt_s_equator + v_lons + 1;
      final int idx_vt_south_cap = idx_vt_south + ( v_lons + 1 ) * half_lat_n2;
      final int idx_vt_south_pole = idx_vt_south_cap + v_lons;

      /* Resize texture coordinates array. */
      final int len_vts = idx_vt_south_pole + 1;
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
         len_vts);

      /* Horizontal. */
      final int v_lons_p1 = v_lons + 1;
      final float to_tex_s = 1.0f / v_lons;
      final float[] tc_s = new float[v_lons_p1];
      for ( int j = 0; j < v_lons_p1; ++j ) {
         tc_s[j] = j * to_tex_s;
      }

      /* Vertical. */
      final int v_lats_p1 = v_lats + 1;
      final float to_tex_t = 1.0f / ( v_lats + 1 );
      final float[] tc_t = new float[v_lats_p1];
      for ( int i = 0; i < v_lats_p1; ++i ) {
         tc_t[i] = ( 1.0f + i ) * to_tex_t;
      }

      /* Calculate polar uvs. */
      for ( int j = 0; j < v_lons; ++j ) {
         final float s_tex = ( j + 0.5f ) * to_tex_s;
         vts[j].set(s_tex, 0.0f);
         vts[idx_vt_south_cap + j].set(s_tex, 1.0f);
      }

      /* Calculate equatorial uvs. */
      for ( int j = 0; j < v_lons_p1; ++j ) {
      }

      return target;
   }

   /**
    * The main function.
    *
    * @param args the string of arguments
    */
   public static void main ( final String[] args ) {

      final Rng rng = new Rng();

      final Mesh2 m = new Mesh2();
      Mesh2.plane(16, m);
      m.triangulate();

      final Mesh3 m3 = new Mesh3(m);
      // CamZup.capsule(32, 16, 1, 1.0f, 0.5f, Mesh.PolyType.QUAD, m);
      // Mesh3.dodecahedron(m);
      final MeshEntity3 me = new MeshEntity3();
      me.append(m3);
      final String pyCd = me.toBlenderCode();
      System.out.println(pyCd);

   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
