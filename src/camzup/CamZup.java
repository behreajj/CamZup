package camzup;

import camzup.core.IUtils;
import camzup.core.Mesh3;
import camzup.core.MeshEntity3;
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

      /*
       * Require that latitudes be even so that they can be split
       * about the equator.
       */
      final int verif_lats = latitudes < 2 ? 2 : latitudes % 2 != 0 ? latitudes
         + 1 : latitudes;
      final int verif_lons = longitudes < 3 ? 3 : longitudes;
      final int verif_rings = rings < 0 ? 0 : rings;
      final float verif_depth = Utils.max(IUtils.EPSILON, depth);
      final float verif_rad = Utils.max(IUtils.EPSILON, radius);

      /* Boolean flags that change number of faces and vertices. */
      final boolean use_quads = poly == PolyType.QUAD;
      final boolean calc_mid = verif_rings > 0;

      /* Intermediary calculations. */
      final int half_lats = verif_lats / 2;
      final int half_lats_n1 = half_lats - 1;
      final int half_lats_n2 = half_lats - 2;
      final int verif_rings_p1 = verif_rings + 1;
      final int verif_lons_p1 = verif_lons + 1;

      final int v_lons_half_lat_n1 = half_lats_n1 * verif_lons;
      final int v_lons_v_sections_p1 = verif_rings_p1 * verif_lons;

      final float half_depth = verif_depth * 0.5f;
      final float summit = half_depth + verif_rad;

      /* Index offsets for coordinates. */
      final int idx_v_n_equator = verif_lons_p1 + verif_lons * half_lats_n2;
      final int idx_v_cyl = idx_v_n_equator + verif_lons;
      final int idx_v_s_equator = calc_mid ? idx_v_cyl + verif_lons
         * verif_rings : idx_v_cyl;
      final int idx_v_south = idx_v_s_equator + verif_lons;
      final int idx_v_south_cap = idx_v_south + verif_lons * half_lats_n2;
      final int idx_v_south_pole = idx_v_south_cap + verif_lons;

      /* Index offsets for texture coordinates. */
      final int idx_vt_n_equator = verif_lons + verif_lons_p1 * half_lats_n1;
      final int idx_vt_cyl = idx_vt_n_equator + verif_lons_p1;
      final int idx_vt_s_equator = calc_mid ? idx_vt_cyl + verif_lons_p1
         * verif_rings : idx_vt_cyl;
      final int idx_vt_s_hemi = idx_vt_s_equator + verif_lons_p1;
      final int idx_vt_s_polar = idx_vt_s_hemi + verif_lons_p1 * half_lats_n2;
      final int idx_vt_s_cap = idx_vt_s_polar + verif_lons_p1;

      /* Index offsets for normals. */
      final int idx_vn_south = idx_v_n_equator + verif_lons;
      final int idx_vn_south_cap = idx_vn_south + verif_lons * half_lats_n2;
      final int idx_vn_south_pole = idx_vn_south_cap + verif_lons;

      /* Array lengths. */
      final int len_vs = idx_v_south_pole + 1;
      final int len_vts = idx_vt_s_cap + verif_lons;
      final int len_vns = idx_vn_south_pole + 1;

      /* Allocate mesh data. */
      final Vec3[] vs = target.coords = Vec3.resize(target.coords, len_vs);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
         len_vts);
      final Vec3[] vns = target.normals = Vec3.resize(target.normals, len_vns);

      /* Set North pole: coordinate, normal. */
      vs[0].set(0.0f, 0.0f, summit);
      vns[0].set(0.0f, 0.0f, 1.0f);

      /* Set South pole: coordinate, normal. */
      vs[idx_v_south_pole].set(0.0f, 0.0f, -summit);
      vns[idx_vn_south_pole].set(0.0f, 0.0f, -1.0f);

      /*
       * Calculate polar texture coordinates. UVs form a triangle at the poles,
       * where the polar vertex is centered between the other two vertices. That
       * is why j is offset by 0.5 . There is one fewer column of UVs at the
       * poles, so the for loop uses the coordinate longitude range. Calculate
       * theta and equators. Cache sine and cosine of theta.
       */
      final float[] sin_theta_cache = new float[verif_lons];
      final float[] cos_theta_cache = new float[verif_lons];
      // final float[] rho_sin_theta_cache = new float[verif_lons];
      // final float[] rho_cos_theta_cache = new float[verif_lons];
      final float to_theta = IUtils.TAU / verif_lons;
      final float to_phi = IUtils.PI / verif_lats;
      final float to_tex_horizontal = 1.0f / verif_lons;
      final float to_tex_vertical = 1.0f / half_lats;

      for ( int j = 0; j < verif_lons; ++j ) {
         final float jf = j;

         /* Coordinates. */
         final float theta = jf * to_theta;
         final float sin_theta = Utils.sin(theta);
         final float cos_theta = Utils.cos(theta);
         sin_theta_cache[j] = sin_theta;
         cos_theta_cache[j] = cos_theta;

         /* Texture coordinates at North and South pole. */
         final float s_tex = ( jf + 0.5f ) * to_tex_horizontal;
         vts[j].set(s_tex, 0.0f);
         vts[idx_vt_s_cap + j].set(s_tex, 1.0f);

         /* Multiply by radius to get equatorial x and y. */
         final float x = verif_rad * cos_theta;
         final float y = verif_rad * sin_theta;

         // rho_sin_theta_cache[j] = x;
         // rho_cos_theta_cache[j] = y;

         /* Set equatorial coordinates. Offset by cylinder depth. */
         vs[idx_v_n_equator + j].set(x, y, half_depth);
         vs[idx_v_s_equator + j].set(x, y, -half_depth);

         /* Set equatorial normals. */
         vns[idx_v_n_equator + j].set(cos_theta, sin_theta, 0.0f);
      }

      /* Calculate equatorial texture coordinates. Cache horizontal measure. */
      final float[] s_tex_cache = new float[verif_lons_p1];

      /* Simplistic UV aspect ratio: uses 1/3 and 2/3. */
      // final float vt_aspect_north = 1.0f / 3.0f;
      // final float vt_aspect_north = ( float ) half_lats / ( verif_rings_p1
      // + verif_lats );
      final float vt_aspect_north = verif_rad / ( verif_depth + verif_rad
         + verif_rad );
      final float vt_aspect_south = 1.0f - vt_aspect_north;
      for ( int j = 0; j < verif_lons_p1; ++j ) {
         final float s_tex = j * to_tex_horizontal;
         s_tex_cache[j] = s_tex;
         vts[idx_vt_n_equator + j].set(s_tex, vt_aspect_north);
         vts[idx_vt_s_equator + j].set(s_tex, vt_aspect_south);
      }

      /* Divide latitudes into hemispheres. Start at i = 1 due to the poles. */
      int v_hemi_offset_north = 1;
      int v_hemi_offset_south = idx_v_south;

      int vt_hemi_offset_north = verif_lons;
      int vt_hemi_offset_south = idx_vt_s_hemi;

      int vn_hemi_offset_south = idx_vn_south;

      for ( int i = 1; i < half_lats; ++i ) {

         final float phi = i * to_phi;

         /*
          * Use trigonometric symmetries to avoid calculating another sine and
          * cosine for phi North.
          */
         final float sin_phi_south = Utils.sin(phi);
         final float cos_phi_south = Utils.cos(phi);

         final float sin_phi_north = -cos_phi_south;
         final float cos_phi_north = sin_phi_south;

         /* For North coordinates, multiply by radius and offset. */
         final float rho_cos_phi_north = verif_rad * cos_phi_north;
         final float rho_sin_phi_north = verif_rad * sin_phi_north;
         final float offset_z_north = half_depth - rho_sin_phi_north;

         /* For South coordinates, multiply by radius and offset. */
         final float rho_cos_phi_south = verif_rad * cos_phi_south;
         final float rho_sin_phi_south = verif_rad * sin_phi_south;
         final float offset_z_south = -half_depth - rho_sin_phi_south;

         /* Coordinates */
         for ( int j = 0; j < verif_lons; ++j ) {
            final float sin_theta = sin_theta_cache[j];
            final float cos_theta = cos_theta_cache[j];
            // final float rho_sin_theta = rho_sin_theta_cache[j];
            // final float rho_cos_theta = rho_cos_theta_cache[j];

            /* @formatter:off */

            /* North coordinate. */
            vs[v_hemi_offset_north].set(
               rho_cos_phi_north * cos_theta,
               rho_cos_phi_north * sin_theta,
               // cos_phi_north * rho_cos_theta,
               // cos_phi_north * rho_sin_theta,
               offset_z_north);

            /* North normal. */
            vns[v_hemi_offset_north].set(
               cos_phi_north * cos_theta,
               cos_phi_north * sin_theta,
               -sin_phi_north);

            /* South coordinate. */
            vs[v_hemi_offset_south].set(
               rho_cos_phi_south * cos_theta,
               rho_cos_phi_south * sin_theta,
               // cos_phi_south * rho_cos_theta,
               // cos_phi_south * rho_sin_theta,
               offset_z_south);

            /* South normal. */
            vns[vn_hemi_offset_south].set(
               cos_phi_south * cos_theta,
               cos_phi_south * sin_theta,
               -sin_phi_south);

            /* @formatter:on */

            v_hemi_offset_north += 1;
            v_hemi_offset_south += 1;
            vn_hemi_offset_south += 1;
         }

         /*
          * For UVs, linear interpolation from North pole (0.0) to North aspect
          * ratio (1.0 / 3.0 default); and from South pole (1.0) to South aspect
          * ratio (2.0 / 3.0 default).
          */
         final float t_tex_fac = i * to_tex_vertical;
         final float t_tex_north = t_tex_fac * vt_aspect_north;
         final float t_tex_south = ( 1.0f - t_tex_fac ) * vt_aspect_south
            + t_tex_fac;

         /* Texture coordinates. */
         for ( int j = 0; j < verif_lons_p1; ++j ) {
            final float s_tex = s_tex_cache[j];
            vts[vt_hemi_offset_north].set(s_tex, t_tex_north);
            vts[vt_hemi_offset_south].set(s_tex, t_tex_south);

            vt_hemi_offset_north += 1;
            vt_hemi_offset_south += 1;
         }
      }

      /* Calculate sections of cylinder in middle. */
      if ( calc_mid ) {

         /*
          * Linear interpolation must exclude the origin (North equator) and the
          * destination (South equator), so step must never equal 0.0 or 1.0 .
          */
         final float to_fac = 1.0f / verif_rings_p1;
         int v_cyl_offset = idx_v_cyl;
         int vt_cyl_offset = idx_vt_cyl;
         for ( int m = 1; m < verif_rings_p1; ++m ) {
            final float fac = m * to_fac;
            final float cmpl_fac = 1.0f - fac;

            /* Coordinates. */
            for ( int j = 0; j < verif_lons; ++j ) {

               final Vec3 v_equator_north = vs[idx_v_n_equator + j];
               final Vec3 v_equator_south = vs[idx_v_s_equator + j];

               /*
                * xy should be the same for both North and South. North z should
                * equal half_depth while South z should equal -half_depth.
                * However this is kept as a linear interpolation for clarity.
                */
               vs[v_cyl_offset].set(cmpl_fac * v_equator_north.x + fac
                  * v_equator_south.x, cmpl_fac * v_equator_north.y + fac
                     * v_equator_south.y, cmpl_fac * v_equator_north.z + fac
                        * v_equator_south.z);

               ++v_cyl_offset;
            }

            /* Texture coordinates. */
            final float t_tex = cmpl_fac * vt_aspect_north + fac
               * vt_aspect_south;
            for ( int j = 0; j < verif_lons_p1; ++j ) {
               final float s_tex = s_tex_cache[j];
               vts[vt_cyl_offset].set(s_tex, t_tex);
               ++vt_cyl_offset;
            }
         }
      }

      /* Find index offsets for face indices. */
      final int idx_fs_cyl = use_quads ? verif_lons + v_lons_half_lat_n1
         : verif_lons + v_lons_half_lat_n1 * 2;
      final int idx_fs_south_equat = use_quads ? idx_fs_cyl
         + v_lons_v_sections_p1 : idx_fs_cyl + v_lons_v_sections_p1 * 2;
      final int idx_fs_south_hemi = use_quads ? idx_fs_south_equat
         + v_lons_half_lat_n1 : idx_fs_south_equat + v_lons_half_lat_n1 * 2;


      /* Resize face indices to new length. */
      final int len_indices = idx_fs_south_hemi + verif_lons;
      final int[][][] fs = target.faces = new int[len_indices][][];

      /* North & South cap indices (always triangles). */
      for ( int j = 0; j < verif_lons; ++j ) {
         final int j_next_vt = j + 1;
         final int j_next_v = j_next_vt % verif_lons;

         /* North coordinate indices. */
         final int[][] tri_north = fs[j] = new int[3][3];

         final int[] north0 = tri_north[0];
         north0[0] = 0;
         north0[1] = j;
         north0[2] = 0;

         final int[] north1 = tri_north[1];
         north1[0] = 1 + j;
         north1[1] = verif_lons + j;
         north1[2] = 1 + j;

         final int[] north2 = tri_north[2];
         north2[0] = 1 + j_next_v;
         north2[1] = verif_lons + j_next_vt;
         north2[2] = 1 + j_next_v;

         /* South coordinates indices. */
         final int[][] tri_south = fs[idx_fs_south_hemi + j] = new int[3][3];

         final int[] south0 = tri_south[0];
         south0[0] = idx_v_south_pole;
         south0[1] = idx_vt_s_cap + j;
         south0[2] = idx_vn_south_pole;

         final int[] south1 = tri_south[1];
         south1[0] = idx_v_south_cap + j_next_v;
         south1[1] = idx_vt_s_polar + j_next_vt;
         south1[2] = idx_vn_south_cap + j_next_v;

         final int[] south2 = tri_south[2];
         south2[0] = idx_v_south_cap + j;
         south2[1] = idx_vt_s_polar + j;
         south2[2] = idx_vn_south_cap + j;
      }

      /* Hemisphere indices. */
      int f_hemi_offset_north = verif_lons;
      int f_hemi_offset_south = idx_fs_south_equat;
      for ( int i = 0; i < half_lats_n1; ++i ) {
         final int i_v_lons = i * verif_lons;

         /* North coordinate index offset. */
         final int v_curr_lat_n = 1 + i_v_lons;
         final int v_next_lat_n = v_curr_lat_n + verif_lons;

         /* South coordinate index offset. */
         final int v_curr_lat_s = idx_v_s_equator + i_v_lons;
         final int v_next_lat_s = v_curr_lat_s + verif_lons;

         /* North texture coordinate index offset. */
         final int vt_curr_lat_n = verif_lons + i * verif_lons_p1;
         final int vt_next_lat_n = vt_curr_lat_n + verif_lons_p1;

         /* South texture coordinate index offset, */
         final int vt_curr_lat_s = idx_vt_s_equator + i * verif_lons_p1;
         final int vt_next_lat_s = vt_curr_lat_s + verif_lons_p1;

         /* North normal index offset. */
         final int vn_curr_lat_n = 1 + i_v_lons;
         final int vn_next_lat_n = vn_curr_lat_n + verif_lons;

         /* South normal index offset. */
         final int vn_curr_lat_s = idx_v_n_equator + i_v_lons;
         final int vn_next_lat_s = vn_curr_lat_s + verif_lons;

         for ( int j = 0; j < verif_lons; ++j ) {
            final int j_next_vt = j + 1;
            final int j_next_v = j_next_vt % verif_lons;

            /* North coordinate indices. */
            final int n00 = v_curr_lat_n + j;
            final int n01 = v_next_lat_n + j;
            final int n11 = v_next_lat_n + j_next_v;
            final int n10 = v_curr_lat_n + j_next_v;

            /* South coordinate indices. */
            final int s00 = v_curr_lat_s + j;
            final int s01 = v_next_lat_s + j;
            final int s11 = v_next_lat_s + j_next_v;
            final int s10 = v_curr_lat_s + j_next_v;

            /* North texture coordinate indices. */
            final int vtn00 = vt_curr_lat_n + j;
            final int vtn01 = vt_next_lat_n + j;
            final int vtn11 = vt_next_lat_n + j_next_vt;
            final int vtn10 = vt_curr_lat_n + j_next_vt;

            /* South texture coordinate indices. */
            final int vts00 = vt_curr_lat_s + j;
            final int vts01 = vt_next_lat_s + j;
            final int vts11 = vt_next_lat_s + j_next_vt;
            final int vts10 = vt_curr_lat_s + j_next_vt;

            /* North normal indices. */
            final int vnn00 = vn_curr_lat_n + j;
            final int vnn01 = vn_next_lat_n + j;
            final int vnn11 = vn_next_lat_n + j_next_v;
            final int vnn10 = vn_curr_lat_n + j_next_v;

            /* South normal indices. */
            final int vns00 = vn_curr_lat_s + j;
            final int vns01 = vn_next_lat_s + j;
            final int vns11 = vn_next_lat_s + j_next_v;
            final int vns10 = vn_curr_lat_s + j_next_v;

            if ( use_quads ) {
               final int[][] north_quad = fs[f_hemi_offset_north]
                  = new int[4][3];

               final int[] nq0 = north_quad[0];
               nq0[0] = n00;
               nq0[1] = vtn00;
               nq0[2] = vnn00;

               final int[] nq1 = north_quad[1];
               nq1[0] = n01;
               nq1[1] = vtn01;
               nq1[2] = vnn01;

               final int[] nq2 = north_quad[2];
               nq2[0] = n11;
               nq2[1] = vtn11;
               nq2[2] = vnn11;

               final int[] nq3 = north_quad[3];
               nq3[0] = n10;
               nq3[1] = vtn10;
               nq3[2] = vnn10;

               final int[][] south_quad = fs[f_hemi_offset_south]
                  = new int[4][3];

               final int[] sq0 = south_quad[0];
               sq0[0] = s00;
               sq0[1] = vts00;
               sq0[2] = vns00;

               final int[] sq1 = south_quad[1];
               sq1[0] = s01;
               sq1[1] = vts01;
               sq1[2] = vns01;

               final int[] sq2 = south_quad[2];
               sq2[0] = s11;
               sq2[1] = vts11;
               sq2[2] = vns11;

               final int[] sq3 = south_quad[3];
               sq3[0] = s10;
               sq3[1] = vts10;
               sq3[2] = vns10;

               f_hemi_offset_north += 1;
               f_hemi_offset_south += 1;

            } else {

               /* North triangle 0. */
               final int[][] north_tri0 = fs[f_hemi_offset_north + 1]
                  = new int[3][3];

               final int[] n_tri00 = north_tri0[0];
               n_tri00[0] = n00;
               n_tri00[1] = vtn00;
               n_tri00[2] = vnn00;

               final int[] n_tri01 = north_tri0[1];
               n_tri01[0] = n01;
               n_tri01[1] = vtn01;
               n_tri01[2] = vnn01;

               final int[] n_tri02 = north_tri0[2];
               n_tri02[0] = n11;
               n_tri02[1] = vtn11;
               n_tri02[2] = vnn11;

               /* North triangle 1. */
               final int[][] n_tri1 = fs[f_hemi_offset_north]
                  = new int[3][3];

               final int[] n_tri10 = n_tri1[0];
               n_tri10[0] = n00;
               n_tri10[1] = vtn00;
               n_tri10[2] = vnn00;

               final int[] n_tri11 = n_tri1[1];
               n_tri11[0] = n11;
               n_tri11[1] = vtn11;
               n_tri11[2] = vnn11;

               final int[] n_tri12 = n_tri1[2];
               n_tri12[0] = n10;
               n_tri12[1] = vtn10;
               n_tri12[2] = vnn10;

               /* South triangle 0. */
               final int[][] south_tri0 = fs[f_hemi_offset_south + 1]
                  = new int[3][3];

               final int[] s_tri00 = south_tri0[0];
               s_tri00[0] = s00;
               s_tri00[1] = vts00;
               s_tri00[2] = vns00;

               final int[] s_tri01 = south_tri0[1];
               s_tri01[0] = s01;
               s_tri01[1] = vts01;
               s_tri01[2] = vns01;

               final int[] s_tri02 = south_tri0[2];
               s_tri02[0] = s11;
               s_tri02[1] = vts11;
               s_tri02[2] = vns11;

               /* South triangle 1. */
               final int[][] s_tri1 = fs[f_hemi_offset_south]
                  = new int[3][3];

               final int[] s_tri10 = s_tri1[0];
               s_tri10[0] = s00;
               s_tri10[1] = vts00;
               s_tri10[2] = vns00;

               final int[] s_tri11 = s_tri1[1];
               s_tri11[0] = s11;
               s_tri11[1] = vts11;
               s_tri11[2] = vns11;

               final int[] s_tri12 = s_tri1[2];
               s_tri12[0] = s10;
               s_tri12[1] = vts10;
               s_tri12[2] = vns10;

               f_hemi_offset_north += 2;
               f_hemi_offset_south += 2;
            }
         }
      }

      /* Cylinder face indices. */
      int f_cyl_offset = idx_fs_cyl;
      for ( int m = 0; m < verif_rings_p1; ++m ) {

         final int v_curr_ring = idx_v_n_equator + m * verif_lons;
         final int v_next_ring = v_curr_ring + verif_lons;

         final int vt_curr_ring = idx_vt_n_equator + m * verif_lons_p1;
         final int vt_next_ring = vt_curr_ring + verif_lons_p1;

         for ( int j = 0; j < verif_lons; ++j ) {

            final int j_next_vt = j + 1;
            final int j_next_v = j_next_vt % verif_lons;

            /* Coordinate corners. */
            final int v00 = v_curr_ring + j;
            final int v01 = v_next_ring + j;
            final int v11 = v_next_ring + j_next_v;
            final int v10 = v_curr_ring + j_next_v;

            /* Texture coordinate corners. */
            final int vt00 = vt_curr_ring + j;
            final int vt01 = vt_next_ring + j;
            final int vt11 = vt_next_ring + j_next_vt;
            final int vt10 = vt_curr_ring + j_next_vt;

            /* Normal corners. */
            final int vn0 = idx_v_n_equator + j;
            final int vn1 = idx_v_n_equator + j_next_v;

            if ( use_quads ) {

               final int[][] quad = fs[f_cyl_offset] = new int[4][3];

               final int[] quad0 = quad[0];
               quad0[0] = v00;
               quad0[1] = vt00;
               quad0[2] = vn0;

               final int[] quad1 = quad[1];
               quad1[0] = v01;
               quad1[1] = vt01;
               quad1[2] = vn0;

               final int[] quad2 = quad[2];
               quad2[0] = v11;
               quad2[1] = vt11;
               quad2[2] = vn1;

               final int[] quad3 = quad[3];
               quad3[0] = v10;
               quad3[1] = vt10;
               quad3[2] = vn1;

               f_cyl_offset += 1;

            } else {

               final int[][] tri0 = fs[f_cyl_offset + 1]
                  = new int[3][3];

               final int[] tri00 = tri0[0];
               tri00[0] = v00;
               tri00[1] = vt00;
               tri00[2] = vn0;

               final int[] tri01 = tri0[1];
               tri01[0] = v01;
               tri01[1] = vt01;
               tri01[2] = vn0;

               final int[] tri02 = tri0[2];
               tri02[0] = v11;
               tri02[1] = vt11;
               tri02[2] = vn1;

               final int[][] tri1 = fs[f_cyl_offset]
                  = new int[3][3];

               final int[] tri10 = tri1[0];
               tri10[0] = v00;
               tri10[1] = vt00;
               tri10[2] = vn0;

               final int[] tri11 = tri1[1];
               tri11[0] = v11;
               tri11[1] = vt11;
               tri11[2] = vn1;

               final int[] tri12 = tri1[2];
               tri12[0] = v10;
               tri12[1] = vt10;
               tri12[2] = vn1;

               f_cyl_offset += 2;
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

      final Mesh3 m3 = new Mesh3();
      final int longitudes = 16;
      final int latitudes = 8;
      final int rings = 2;
      final float depth = 2.0f;
      final float radius = 1.5f;
      final PolyType poly = PolyType.TRI;
      CamZup.capsule(longitudes, latitudes, rings, depth, radius, poly, m3);
      // System.out.println(m3);
      final MeshEntity3 entity3 = new MeshEntity3().append(m3);
      final String pyCd = entity3.toBlenderCode();
      System.out.println(pyCd);
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
