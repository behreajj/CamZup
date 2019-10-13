package camzup.core;

/**
 * Based on OpenSimplex Noise in Java by Kurt Spencer. This
 * has been modified to use single precision floats rather
 * than double precision.
 * 
 * @author Kurt Spencer
 */
public class SimplexNoise {

   /*
    * Gradients for 2D. They approximate the directions to the
    * vertices of an octagon from the center.
    */
   protected static final byte[] gradients2D = new byte[] {
         5, 2, 2, 5,
         -5, 2, -2, 5,
         5, -2, 2, -5,
         -5, -2, -2, -5
   };

   /*
    * Gradients for 3D. They approximate the directions to the
    * vertices of a rhombicuboctahedron from the center, skewed
    * so that the triangular and square facets can be inscribed
    * inside circles of the same radius.
    */
   protected static final byte[] gradients3D = new byte[] {
         -11, 4, 4, -4, 11, 4, -4, 4, 11,
         11, 4, 4, 4, 11, 4, 4, 4, 11,
         -11, -4, 4, -4, -11, 4, -4, -4, 11,
         11, -4, 4, 4, -11, 4, 4, -4, 11,
         -11, 4, -4, -4, 11, -4, -4, 4, -11,
         11, 4, -4, 4, 11, -4, 4, 4, -11,
         -11, -4, -4, -4, -11, -4, -4, -4, -11,
         11, -4, -4, 4, -11, -4, 4, -4, -11
   };

   /*
    * Gradients for 4D. They approximate the directions to the
    * vertices of a disprismatotesseractihexadecachoron from
    * the center, skewed so that the tetrahedral and cubic
    * facets can be inscribed inside spheres of the same
    * radius.
    */
   protected static final byte[] gradients4D = new byte[] {
         3, 1, 1, 1, 1, 3, 1, 1, 1, 1, 3, 1, 1, 1, 1, 3,
         -3, 1, 1, 1, -1, 3, 1, 1, -1, 1, 3, 1, -1, 1, 1, 3,
         3, -1, 1, 1, 1, -3, 1, 1, 1, -1, 3, 1, 1, -1, 1, 3,
         -3, -1, 1, 1, -1, -3, 1, 1, -1, -1, 3, 1, -1, -1, 1, 3,
         3, 1, -1, 1, 1, 3, -1, 1, 1, 1, -3, 1, 1, 1, -1, 3,
         -3, 1, -1, 1, -1, 3, -1, 1, -1, 1, -3, 1, -1, 1, -1, 3,
         3, -1, -1, 1, 1, -3, -1, 1, 1, -1, -3, 1, 1, -1, -1, 3,
         -3, -1, -1, 1, -1, -3, -1, 1, -1, -1, -3, 1, -1, -1, -1, 3,
         3, 1, 1, -1, 1, 3, 1, -1, 1, 1, 3, -1, 1, 1, 1, -3,
         -3, 1, 1, -1, -1, 3, 1, -1, -1, 1, 3, -1, -1, 1, 1, -3,
         3, -1, 1, -1, 1, -3, 1, -1, 1, -1, 3, -1, 1, -1, 1, -3,
         -3, -1, 1, -1, -1, -3, 1, -1, -1, -1, 3, -1, -1, -1, 1, -3,
         3, 1, -1, -1, 1, 3, -1, -1, 1, 1, -3, -1, 1, 1, -1, -3,
         -3, 1, -1, -1, -1, 3, -1, -1, -1, 1, -3, -1, -1, 1, -1, -3,
         3, -1, -1, -1, 1, -3, -1, -1, 1, -1, -3, -1, 1, -1, -1, -3,
         -3, -1, -1, -1, -1, -3, -1, -1, -1, -1, -3, -1, -1, -1, -1, -3
   };

   /* 1.0d / 47.0d; */
   public static final float INV_NORM_CONST_2 = 0.021276596f;

   /* 1.0d / 103.0d; */
   public static final float INV_NORM_CONST_3 = 0.009708738f;

   /* 1.0d / 30.0d; */
   public static final float INV_NORM_CONST_4 = 0.033333333f;

   /* (Math.sqrt(3.0d) - 1.0d) / 2.0d; */
   public static final float SQUISH_CONSTANT_2 = 0.3660254037844386f;

   public static final float SQUISH_CONSTANT_2_2 = 0.7320508075688773f;

   /* (Math.sqrt(4.0d) - 1.0d) / 3.0d; */
   public static final float SQUISH_CONSTANT_3 = 0.3333333333333333f;

   public static final float SQUISH_CONSTANT_3_2 = 0.6666666666666667f;

   public static final float SQUISH_CONSTANT_3_3 = 1.0f;

   /* (Math.sqrt(5.0d) - 1.0d) / 4.0d; */
   public static final float SQUISH_CONSTANT_4 = 0.30901699437494745f;

   public static final float SQUISH_CONSTANT_4_2 = 0.61803398874989485f;

   public static final float SQUISH_CONSTANT_4_3 = 0.92705098312484227f;

   public static final float SQUISH_CONSTANT_4_4 = 1.23606797749978970f;

   /* (1.0d / Math.sqrt(3.0d) - 1.0d) / 2.0d */
   public static final float STRETCH_CONSTANT_2 = -0.21132486540518708f;

   /* (1.0d / Math.sqrt(4.0d) - 1.0d) / 3.0d */
   public static final float STRETCH_CONSTANT_3 = -0.16666666666666667f;

   /* (1.0d / Math.sqrt(5.0d) - 1.0d) / 4.0d */
   public static final float STRETCH_CONSTANT_4 = -0.13819660112501053f;

   protected final short[] perm;

   protected final short[] permGradIndex3D;

   public SimplexNoise () {

      this(System.currentTimeMillis());
   }

   public SimplexNoise ( long seed ) {

      /*
       * Initializes the class using a permutation array generated
       * from a 64-bit seed. Generates a proper permutation (i.e.
       * doesn't merely perform N successive pair swaps on a base
       * array) Uses a simple 64-bit LCG.
       */
      this.perm = new short[256];
      this.permGradIndex3D = new short[256];
      final short[] source = new short[256];
      for (short i = 0; i < 256; ++i) {
         source[i] = i;
      }

      seed = seed * 6364136223846793005l + 1442695040888963407l;
      seed = seed * 6364136223846793005l + 1442695040888963407l;
      seed = seed * 6364136223846793005l + 1442695040888963407l;
      final int modulator = SimplexNoise.gradients3D.length / 3;
      for (int i = 255; i >= 0; i--) {
         seed = seed * 6364136223846793005l + 1442695040888963407l;
         int r = (int) ((seed + 31) % (i + 1));
         if (r < 0) {
            r += i + 1;
         }
         this.perm[i] = source[r];
         this.permGradIndex3D[i] = (short) (this.perm[i] % modulator * 3);
         source[r] = source[i];
      }
   }

   public SimplexNoise ( final short[] perm ) {

      this.perm = perm;
      this.permGradIndex3D = new short[256];

      final int modulator = SimplexNoise.gradients3D.length / 3;
      for (int i = 0; i < 256; ++i) {

         /*
          * Since 3D has 24 gradients, simple bitmask won't work, so
          * precompute modulo array.
          */
         this.permGradIndex3D[i] = (short) (perm[i] % modulator * 3);
      }
   }

   public float eval (
         final float x,
         final float y ) {

      /* Place input coordinates onto grid. */
      final float stretchOffset = (x + y) * SimplexNoise.STRETCH_CONSTANT_2;
      final float xs = x + stretchOffset;
      final float ys = y + stretchOffset;

      /**
       * Floor to get grid coordinates of rhombus (stretched
       * square) super-cell origin.
       */
      int xsb = Utils.floorToInt(xs);
      int ysb = Utils.floorToInt(ys);

      /*
       * Skew out to get actual coordinates of rhombus origin.
       * We'll need these later.
       */
      final float squishOffset = (xsb + ysb) * SimplexNoise.SQUISH_CONSTANT_2;
      final float xb = xsb + squishOffset;
      final float yb = ysb + squishOffset;

      /* Compute grid coordinates relative to rhombus origin. */
      final float xins = xs - xsb;
      final float yins = ys - ysb;

      /*
       * Sum those together to get a value that determines which
       * region we're in.
       */
      final float inSum = xins + yins;

      /* Positions relative to origin point. */
      float dx0 = x - xb;
      float dy0 = y - yb;

      /*
       * We'll be defining these inside the next block and using
       * them afterwards.
       */
      float dx_ext;
      float dy_ext;
      int xsv_ext;
      int ysv_ext;

      float value = 0;

      /* Contribution (1, 0) */
      final float dx1 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_2;
      final float dy1 = dy0 - SimplexNoise.SQUISH_CONSTANT_2;
      float attn1 = 2 - dx1 * dx1 - dy1 * dy1;
      if (attn1 > 0) {
         attn1 *= attn1;
         value += attn1 * attn1 * this.extrapolate(
            xsb + 1, ysb,
            dx1, dy1);
      }

      /* Contribution (0, 1) */
      final float dx2 = dx0 - SimplexNoise.SQUISH_CONSTANT_2;
      final float dy2 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_2;
      float attn2 = 2 - dx2 * dx2 - dy2 * dy2;
      if (attn2 > 0) {
         attn2 *= attn2;
         value += attn2 * attn2 * this.extrapolate(
            xsb, ysb + 1,
            dx2, dy2);
      }

      if (inSum <= 1) {

         /* We're inside the triangle (2-Simplex) at (0, 0) */
         final float zins = 1 - inSum;
         if (zins > xins || zins > yins) {

            /*
             * (0, 0) is one of the closest two triangular vertices
             */
            if (xins > yins) {
               xsv_ext = xsb + 1;
               ysv_ext = ysb - 1;
               dx_ext = dx0 - 1;
               dy_ext = dy0 + 1;
            } else {
               xsv_ext = xsb - 1;
               ysv_ext = ysb + 1;
               dx_ext = dx0 + 1;
               dy_ext = dy0 - 1;
            }
         } else {
            /* (1, 0) and (0, 1) are the closest two vertices. */
            xsv_ext = xsb + 1;
            ysv_ext = ysb + 1;
            dx_ext = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_2_2;
            dy_ext = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_2_2;
         }
      } else {
         /* We're inside the triangle (2-Simplex) at (1, 1) */
         final float zins = 2 - inSum;
         if (zins < xins || zins < yins) { 
            // (0,0) is one of the closest two
                                           // triangular vertices
            if (xins > yins) {
               xsv_ext = xsb + 2;
               ysv_ext = ysb;
               dx_ext = dx0 - 2 - SimplexNoise.SQUISH_CONSTANT_2_2;
               dy_ext = dy0 - SimplexNoise.SQUISH_CONSTANT_2_2;
            } else {
               xsv_ext = xsb;
               ysv_ext = ysb + 2;
               dx_ext = dx0 - SimplexNoise.SQUISH_CONSTANT_2_2;
               dy_ext = dy0 - 2 - SimplexNoise.SQUISH_CONSTANT_2_2;
            }
         } else { 
            // (1,0) and (0,1) are the closest two vertices.
            dx_ext = dx0;
            dy_ext = dy0;
            xsv_ext = xsb;
            ysv_ext = ysb;
         }
         xsb += 1;
         ysb += 1;
         dx0 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_2_2;
         dy0 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_2_2;
      }

      // Contribution (0,0) or (1,1)
      float attn0 = 2 - dx0 * dx0 - dy0 * dy0;
      if (attn0 > 0) {
         attn0 *= attn0;
         value += attn0 * attn0 * this.extrapolate(
            xsb, ysb,
            dx0, dy0);
      }

      // Extra Vertex
      float attn_ext = 2 - dx_ext * dx_ext - dy_ext * dy_ext;
      if (attn_ext > 0) {
         attn_ext *= attn_ext;
         value += attn_ext * attn_ext * this.extrapolate(
            xsv_ext, ysv_ext,
            dx_ext, dy_ext);
      }

      return value * SimplexNoise.INV_NORM_CONST_2;
   }

   public float eval (
         final float x,
         final float y,
         final float z ) {

      // Place input coordinates on simplectic honeycomb.
      final float stretchOffset = (x + y + z) * SimplexNoise.STRETCH_CONSTANT_3;
      final float xs = x + stretchOffset;
      final float ys = y + stretchOffset;
      final float zs = z + stretchOffset;

      // Floor to get simplectic honeycomb coordinates of
      // rhombohedron (stretched cube) super-cell origin.
      final int xsb = Utils.floorToInt(xs);
      final int ysb = Utils.floorToInt(ys);
      final int zsb = Utils.floorToInt(zs);

      // Skew out to get actual coordinates of rhombohedron
      // origin. We'll need these later.
      final float squishOffset = (xsb + ysb + zsb) * SimplexNoise.SQUISH_CONSTANT_3;
      final float xb = xsb + squishOffset;
      final float yb = ysb + squishOffset;
      final float zb = zsb + squishOffset;

      /*
       * Compute simplectic honeycomb coordinates relative to
       * rhombohedral origin.
       */
      final float xins = xs - xsb;
      final float yins = ys - ysb;
      final float zins = zs - zsb;

      // Sum those together to get a value that determines which
      // region we're in.
      final float inSum = xins + yins + zins;

      // Positions relative to origin point.
      float dx0 = x - xb;
      float dy0 = y - yb;
      float dz0 = z - zb;

      // We'll be defining these inside the next block and using
      // them afterwards.
      float dx_ext0, dy_ext0, dz_ext0;
      float dx_ext1, dy_ext1, dz_ext1;
      int xsv_ext0, ysv_ext0, zsv_ext0;
      int xsv_ext1, ysv_ext1, zsv_ext1;

      float value = 0;
      if (inSum <= 1) { 
         // We're inside the tetrahedron (3-Simplex) at (0, 0, 0)

         // Determine which two of (0, 0, 1), (0, 1, 0), (1, 0, 0) are
         // closest.
         byte aPoint = 0x01;
         float aScore = xins;
         byte bPoint = 0x02;
         float bScore = yins;
         if (aScore >= bScore && zins > bScore) {
            bScore = zins;
            bPoint = 0x04;
         } else if (aScore < bScore && zins > aScore) {
            aScore = zins;
            aPoint = 0x04;
         }

         // Now we determine the two lattice points not part of the
         // tetrahedron that may contribute.
         // This depends on the closest two tetrahedral vertices,
         // including (0,0,0)
         final float wins = 1 - inSum;
         if (wins > aScore || wins > bScore) { 
            // (0,0,0) is one of the closest
            // two tetrahedral vertices.
            final byte c = bScore > aScore ? bPoint : aPoint; 
            // Our other
            // closest
            // vertex is the
            // closest out of a
            // and b.

            if ((c & 0x01) == 0) {
               xsv_ext0 = xsb - 1;
               xsv_ext1 = xsb;
               dx_ext0 = dx0 + 1;
               dx_ext1 = dx0;
            } else {
               xsv_ext0 = xsv_ext1 = xsb + 1;
               dx_ext0 = dx_ext1 = dx0 - 1;
            }

            if ((c & 0x02) == 0) {
               ysv_ext0 = ysv_ext1 = ysb;
               dy_ext0 = dy_ext1 = dy0;
               if ((c & 0x01) == 0) {
                  ysv_ext1 -= 1;
                  dy_ext1 += 1;
               } else {
                  ysv_ext0 -= 1;
                  dy_ext0 += 1;
               }
            } else {
               ysv_ext0 = ysv_ext1 = ysb + 1;
               dy_ext0 = dy_ext1 = dy0 - 1;
            }

            if ((c & 0x04) == 0) {
               zsv_ext0 = zsb;
               zsv_ext1 = zsb - 1;
               dz_ext0 = dz0;
               dz_ext1 = dz0 + 1;
            } else {
               zsv_ext0 = zsv_ext1 = zsb + 1;
               dz_ext0 = dz_ext1 = dz0 - 1;
            }
         } else { 
            // (0,0,0) is not one of the closest two tetrahedral
            // vertices.
            
            final byte c = (byte) (aPoint | bPoint); 
            
            // Our two extra vertices
            // are
            // determined by the closest two.

            if ((c & 0x01) == 0) {
               xsv_ext0 = xsb;
               xsv_ext1 = xsb - 1;
               dx_ext0 = dx0 - SimplexNoise.SQUISH_CONSTANT_3_2;
               dx_ext1 = dx0 + 1 - SimplexNoise.SQUISH_CONSTANT_3;
            } else {
               xsv_ext0 = xsv_ext1 = xsb + 1;
               dx_ext0 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_2;
               dx_ext1 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
            }

            if ((c & 0x02) == 0) {
               ysv_ext0 = ysb;
               ysv_ext1 = ysb - 1;
               dy_ext0 = dy0 - SimplexNoise.SQUISH_CONSTANT_3_2;
               dy_ext1 = dy0 + 1 - SimplexNoise.SQUISH_CONSTANT_3;
            } else {
               ysv_ext0 = ysv_ext1 = ysb + 1;
               dy_ext0 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_2;
               dy_ext1 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
            }

            if ((c & 0x04) == 0) {
               zsv_ext0 = zsb;
               zsv_ext1 = zsb - 1;
               dz_ext0 = dz0 - SimplexNoise.SQUISH_CONSTANT_3_2;
               dz_ext1 = dz0 + 1 - SimplexNoise.SQUISH_CONSTANT_3;
            } else {
               zsv_ext0 = zsv_ext1 = zsb + 1;
               dz_ext0 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_2;
               dz_ext1 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
            }
         }

         // Contribution (0, 0, 0)
         float attn0 = 2 - dx0 * dx0 - dy0 * dy0 - dz0 * dz0;
         if (attn0 > 0) {
            attn0 *= attn0;
            value += attn0 * attn0 * this.extrapolate(
               xsb, ysb, zsb,
               dx0, dy0, dz0);
         }

         // Contribution (1, 0, 0)
         final float dx1 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
         final float dy1 = dy0 - SimplexNoise.SQUISH_CONSTANT_3;
         final float dz1 = dz0 - SimplexNoise.SQUISH_CONSTANT_3;
         float attn1 = 2 - dx1 * dx1 - dy1 * dy1 - dz1 * dz1;
         if (attn1 > 0) {
            attn1 *= attn1;
            value += attn1 * attn1 * this.extrapolate(
                     xsb + 1, ysb, zsb,
                     dx1, dy1, dz1);
         }

         // Contribution (0, 1, 0)
         final float dx2 = dx0 - SimplexNoise.SQUISH_CONSTANT_3;
         final float dy2 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
         final float dz2 = dz1;
         float attn2 = 2 - dx2 * dx2 - dy2 * dy2 - dz2 * dz2;
         if (attn2 > 0) {
            attn2 *= attn2;
            value += attn2 * attn2 * this.extrapolate(
               xsb, ysb + 1, zsb,
               dx2, dy2, dz2);
         }

         // Contribution (0, 0, 1)
         final float dx3 = dx2;
         final float dy3 = dy1;
         final float dz3 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
         float attn3 = 2 - dx3 * dx3 - dy3 * dy3 - dz3 * dz3;
         if (attn3 > 0) {
            attn3 *= attn3;
            value += attn3 * attn3 * this.extrapolate(
               xsb, ysb, zsb + 1,
               dx3, dy3, dz3);
         }
      } else if (inSum >= 2) {
          // We're inside the tetrahedron (3-Simplex) at
          // (1, 1, 1)

         // Determine which two tetrahedral vertices are the closest,
         // out of (1, 1, 0), (1, 0, 1), (0, 1, 1) but not (1, 1, 1).
         byte aPoint = 0x06;
         float aScore = xins;
         byte bPoint = 0x05;
         float bScore = yins;
         if (aScore <= bScore && zins < bScore) {
            bScore = zins;
            bPoint = 0x03;
         } else if (aScore > bScore && zins < aScore) {
            aScore = zins;
            aPoint = 0x03;
         }

         /**
         * Now we determine the two lattice points not part of the
         * tetrahedron that may contribute.
         * This depends on the closest two tetrahedral vertices,
         * including (1, 1, 1)
         */
         final float wins = 3 - inSum;
         if (wins < aScore || wins < bScore) {
             // (1,1,1) is one of the closest
             // two tetrahedral vertices.
            final byte c = bScore < aScore ? bPoint : aPoint; 
            // Our other
            // closest
            // vertex is the
            // closest out of a
            // and b.

            if ((c & 0x01) != 0) {
               xsv_ext0 = xsb + 2;
               xsv_ext1 = xsb + 1;
               dx_ext0 = dx0 - 2 - SimplexNoise.SQUISH_CONSTANT_3_3;
               dx_ext1 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_3;
            } else {
               xsv_ext0 = xsv_ext1 = xsb;
               dx_ext0 = dx_ext1 = dx0 - SimplexNoise.SQUISH_CONSTANT_3_3;
            }

            if ((c & 0x02) != 0) {
               ysv_ext0 = ysv_ext1 = ysb + 1;
               dy_ext0 = dy_ext1 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_3;
               if ((c & 0x01) != 0) {
                  ysv_ext1 += 1;
                  dy_ext1 -= 1;
               } else {
                  ysv_ext0 += 1;
                  dy_ext0 -= 1;
               }
            } else {
               ysv_ext0 = ysv_ext1 = ysb;
               dy_ext0 = dy_ext1 = dy0 - SimplexNoise.SQUISH_CONSTANT_3_3;
            }

            if ((c & 0x04) != 0) {
               zsv_ext0 = zsb + 1;
               zsv_ext1 = zsb + 2;
               dz_ext0 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_3;
               dz_ext1 = dz0 - 2 - SimplexNoise.SQUISH_CONSTANT_3_3;
            } else {
               zsv_ext0 = zsv_ext1 = zsb;
               dz_ext0 = dz_ext1 = dz0 - SimplexNoise.SQUISH_CONSTANT_3_3;
            }
         } else { 
            // (1,1,1) is not one of the closest two tetrahedral
                  // vertices.
            final byte c = (byte) (aPoint & bPoint); 
            // Our two extra vertices
            // are
            // determined by the closest two.

            if ((c & 0x01) != 0) {
               xsv_ext0 = xsb + 1;
               xsv_ext1 = xsb + 2;
               dx_ext0 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
               dx_ext1 = dx0 - 2 - SimplexNoise.SQUISH_CONSTANT_3_2;
            } else {
               xsv_ext0 = xsv_ext1 = xsb;
               dx_ext0 = dx0 - SimplexNoise.SQUISH_CONSTANT_3;
               dx_ext1 = dx0 - SimplexNoise.SQUISH_CONSTANT_3_2;
            }

            if ((c & 0x02) != 0) {
               ysv_ext0 = ysb + 1;
               ysv_ext1 = ysb + 2;
               dy_ext0 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
               dy_ext1 = dy0 - 2 - SimplexNoise.SQUISH_CONSTANT_3_2;
            } else {
               ysv_ext0 = ysv_ext1 = ysb;
               dy_ext0 = dy0 - SimplexNoise.SQUISH_CONSTANT_3;
               dy_ext1 = dy0 - SimplexNoise.SQUISH_CONSTANT_3_2;
            }

            if ((c & 0x04) != 0) {
               zsv_ext0 = zsb + 1;
               zsv_ext1 = zsb + 2;
               dz_ext0 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
               dz_ext1 = dz0 - 2 - SimplexNoise.SQUISH_CONSTANT_3_2;
            } else {
               zsv_ext0 = zsv_ext1 = zsb;
               dz_ext0 = dz0 - SimplexNoise.SQUISH_CONSTANT_3;
               dz_ext1 = dz0 - SimplexNoise.SQUISH_CONSTANT_3_2;
            }
         }

         // Contribution (1,1,0)
         final float dx3 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_2;
         final float dy3 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_2;
         final float dz3 = dz0 - SimplexNoise.SQUISH_CONSTANT_3_2;
         float attn3 = 2 - dx3 * dx3 - dy3 * dy3 - dz3 * dz3;
         if (attn3 > 0) {
            attn3 *= attn3;
            value += attn3 * attn3 * this.extrapolate(
               xsb + 1, ysb + 1, zsb,
               dx3, dy3, dz3);
         }

         // Contribution (1,0,1)
         final float dx2 = dx3;
         final float dy2 = dy0 - SimplexNoise.SQUISH_CONSTANT_3_2;
         final float dz2 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_2;
         float attn2 = 2 - dx2 * dx2 - dy2 * dy2 - dz2 * dz2;
         if (attn2 > 0) {
            attn2 *= attn2;
            value += attn2 * attn2 * this.extrapolate(
               xsb + 1, ysb, zsb + 1,
               dx2, dy2, dz2);
         }

         // Contribution (0,1,1)
         final float dx1 = dx0 - SimplexNoise.SQUISH_CONSTANT_3_2;
         final float dy1 = dy3;
         final float dz1 = dz2;
         float attn1 = 2 - dx1 * dx1 - dy1 * dy1 - dz1 * dz1;
         if (attn1 > 0) {
            attn1 *= attn1;
            value += attn1 * attn1 * this.extrapolate(
               xsb, ysb + 1, zsb + 1,
               dx1, dy1, dz1);
         }

         // Contribution (1,1,1)
         dx0 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_3;
         dy0 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_3;
         dz0 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_3;
         float attn0 = 2 - dx0 * dx0 - dy0 * dy0 - dz0 * dz0;
         if (attn0 > 0) {
            attn0 *= attn0;
            value += attn0 * attn0 * this.extrapolate(
               xsb + 1, ysb + 1, zsb + 1,
               dx0, dy0, dz0);
         }
      } else { 
         // We're inside the octahedron (Rectified 3-Simplex) in
         // between.
         float aScore;
         byte aPoint;
         boolean aIsFurtherSide;
         float bScore;
         byte bPoint;
         boolean bIsFurtherSide;

         // Decide between point (0,0,1) and (1,1,0) as closest
         final float p1 = xins + yins;
         if (p1 > 1) {
            aScore = p1 - 1;
            aPoint = 0x03;
            aIsFurtherSide = true;
         } else {
            aScore = 1 - p1;
            aPoint = 0x04;
            aIsFurtherSide = false;
         }

         // Decide between point (0,1,0) and (1,0,1) as closest
         final float p2 = xins + zins;
         if (p2 > 1) {
            bScore = p2 - 1;
            bPoint = 0x05;
            bIsFurtherSide = true;
         } else {
            bScore = 1 - p2;
            bPoint = 0x02;
            bIsFurtherSide = false;
         }

         // The closest out of the two (1,0,0) and (0,1,1) will
         // replace the furthest out of the two decided above, if
         // closer.
         final float p3 = yins + zins;
         if (p3 > 1) {
            final float score = p3 - 1;
            if (aScore <= bScore && aScore < score) {
               aScore = score;
               aPoint = 0x06;
               aIsFurtherSide = true;
            } else if (aScore > bScore && bScore < score) {
               bScore = score;
               bPoint = 0x06;
               bIsFurtherSide = true;
            }
         } else {
            final float score = 1 - p3;
            if (aScore <= bScore && aScore < score) {
               aScore = score;
               aPoint = 0x01;
               aIsFurtherSide = false;
            } else if (aScore > bScore && bScore < score) {
               bScore = score;
               bPoint = 0x01;
               bIsFurtherSide = false;
            }
         }

         // Where each of the two closest points are determines how
         // the extra two vertices are calculated.
         if (aIsFurtherSide == bIsFurtherSide) {
            if (aIsFurtherSide) { 
               // Both closest points on (1,1,1) side

               // One of the two extra points is (1,1,1)
               dx_ext0 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_3;
               dy_ext0 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_3;
               dz_ext0 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_3;
               xsv_ext0 = xsb + 1;
               ysv_ext0 = ysb + 1;
               zsv_ext0 = zsb + 1;

               // Other extra point is based on the shared axis.
               final byte c = (byte) (aPoint & bPoint);
               if ((c & 0x01) != 0) {
                  dx_ext1 = dx0 - 2 - SimplexNoise.SQUISH_CONSTANT_3_2;
                  dy_ext1 = dy0 - SimplexNoise.SQUISH_CONSTANT_3_2;
                  dz_ext1 = dz0 - SimplexNoise.SQUISH_CONSTANT_3_2;
                  xsv_ext1 = xsb + 2;
                  ysv_ext1 = ysb;
                  zsv_ext1 = zsb;
               } else if ((c & 0x02) != 0) {
                  dx_ext1 = dx0 - SimplexNoise.SQUISH_CONSTANT_3_2;
                  dy_ext1 = dy0 - 2 - SimplexNoise.SQUISH_CONSTANT_3_2;
                  dz_ext1 = dz0 - SimplexNoise.SQUISH_CONSTANT_3_2;
                  xsv_ext1 = xsb;
                  ysv_ext1 = ysb + 2;
                  zsv_ext1 = zsb;
               } else {
                  dx_ext1 = dx0 - SimplexNoise.SQUISH_CONSTANT_3_2;
                  dy_ext1 = dy0 - SimplexNoise.SQUISH_CONSTANT_3_2;
                  dz_ext1 = dz0 - 2 - SimplexNoise.SQUISH_CONSTANT_3_2;
                  xsv_ext1 = xsb;
                  ysv_ext1 = ysb;
                  zsv_ext1 = zsb + 2;
               }
            } else {
               // Both closest points on (0,0,0) side
               // One of the two extra points is (0,0,0)
               dx_ext0 = dx0;
               dy_ext0 = dy0;
               dz_ext0 = dz0;
               xsv_ext0 = xsb;
               ysv_ext0 = ysb;
               zsv_ext0 = zsb;

               // Other extra point is based on the omitted axis.
               final byte c = (byte) (aPoint | bPoint);
               if ((c & 0x01) == 0) {
                  dx_ext1 = dx0 + 1 - SimplexNoise.SQUISH_CONSTANT_3;
                  dy_ext1 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
                  dz_ext1 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
                  xsv_ext1 = xsb - 1;
                  ysv_ext1 = ysb + 1;
                  zsv_ext1 = zsb + 1;
               } else if ((c & 0x02) == 0) {
                  dx_ext1 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
                  dy_ext1 = dy0 + 1 - SimplexNoise.SQUISH_CONSTANT_3;
                  dz_ext1 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
                  xsv_ext1 = xsb + 1;
                  ysv_ext1 = ysb - 1;
                  zsv_ext1 = zsb + 1;
               } else {
                  dx_ext1 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
                  dy_ext1 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
                  dz_ext1 = dz0 + 1 - SimplexNoise.SQUISH_CONSTANT_3;
                  xsv_ext1 = xsb + 1;
                  ysv_ext1 = ysb + 1;
                  zsv_ext1 = zsb - 1;
               }
            }
         } else {
             // One point on (0,0,0) side, one point on (1,1,1) side
            byte c1;
            byte c2;
            if (aIsFurtherSide) {
               c1 = aPoint;
               c2 = bPoint;
            } else {
               c1 = bPoint;
               c2 = aPoint;
            }

            // One contribution is a permutation of (1,1,-1)
            if ((c1 & 0x01) == 0) {
               dx_ext0 = dx0 + 1 - SimplexNoise.SQUISH_CONSTANT_3;
               dy_ext0 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
               dz_ext0 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
               
               xsv_ext0 = xsb - 1;
               ysv_ext0 = ysb + 1;
               zsv_ext0 = zsb + 1;
            
            } else if ((c1 & 0x02) == 0) {
            
               dx_ext0 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
               dy_ext0 = dy0 + 1 - SimplexNoise.SQUISH_CONSTANT_3;
               dz_ext0 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
               
               xsv_ext0 = xsb + 1;
               ysv_ext0 = ysb - 1;
               zsv_ext0 = zsb + 1;
            
            } else {
            
               dx_ext0 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
               dy_ext0 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
               dz_ext0 = dz0 + 1 - SimplexNoise.SQUISH_CONSTANT_3;
               
               xsv_ext0 = xsb + 1;
               ysv_ext0 = ysb + 1;
               zsv_ext0 = zsb - 1;
            }

            // One contribution is a permutation of (0,0,2)
            dx_ext1 = dx0 - SimplexNoise.SQUISH_CONSTANT_3_2;
            dy_ext1 = dy0 - SimplexNoise.SQUISH_CONSTANT_3_2;
            dz_ext1 = dz0 - SimplexNoise.SQUISH_CONSTANT_3_2;
            
            xsv_ext1 = xsb;
            ysv_ext1 = ysb;
            zsv_ext1 = zsb;
            
            if ((c2 & 0x01) != 0) {
               dx_ext1 -= 2;
               xsv_ext1 += 2;
            } else if ((c2 & 0x02) != 0) {
               dy_ext1 -= 2;
               ysv_ext1 += 2;
            } else {
               dz_ext1 -= 2;
               zsv_ext1 += 2;
            }
         }

         /* Contribution (1, 0, 0) */
         final float dx1 = dx0 - 1.0f - SimplexNoise.SQUISH_CONSTANT_3;
         final float dy1 = dy0 - SimplexNoise.SQUISH_CONSTANT_3;
         final float dz1 = dz0 - SimplexNoise.SQUISH_CONSTANT_3;
         float attn1 = 2 - dx1 * dx1 - dy1 * dy1 - dz1 * dz1;
         if (attn1 > 0) {
            attn1 *= attn1;
            value += attn1 * attn1 * this.extrapolate(
               xsb + 1, ysb, zsb,
               dx1, dy1, dz1);
         }

         // Contribution (0,1,0)
         final float dx2 = dx0 - SimplexNoise.SQUISH_CONSTANT_3;
         final float dy2 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
         final float dz2 = dz1;
         float attn2 = 2 - dx2 * dx2 - dy2 * dy2 - dz2 * dz2;
         if (attn2 > 0) {
            attn2 *= attn2;
            value += attn2 * attn2 * this.extrapolate(
               xsb, ysb + 1, zsb,
               dx2, dy2, dz2);
         }

         // Contribution (0,0,1)
         final float dx3 = dx2;
         final float dy3 = dy1;
         final float dz3 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_3;
         float attn3 = 2 - dx3 * dx3 - dy3 * dy3 - dz3 * dz3;
         if (attn3 > 0) {
            attn3 *= attn3;
            value += attn3 * attn3 * this.extrapolate(
               xsb, ysb, zsb + 1,
               dx3, dy3, dz3);
         }

         // Contribution (1,1,0)
         final float dx4 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_2;
         final float dy4 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_2;
         final float dz4 = dz0 - SimplexNoise.SQUISH_CONSTANT_3_2;
         float attn4 = 2 - dx4 * dx4 - dy4 * dy4 - dz4 * dz4;
         if (attn4 > 0) {
            attn4 *= attn4;
            value += attn4 * attn4 * this.extrapolate(
               xsb + 1, ysb + 1, zsb,
               dx4, dy4, dz4);
         }

         // Contribution (1,0,1)
         final float dx5 = dx4;
         final float dy5 = dy0 - SimplexNoise.SQUISH_CONSTANT_3_2;
         final float dz5 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_3_2;
         float attn5 = 2 - dx5 * dx5 - dy5 * dy5 - dz5 * dz5;
         if (attn5 > 0) {
            attn5 *= attn5;
            value += attn5 * attn5 * this.extrapolate(
               xsb + 1, ysb, zsb + 1,
               dx5, dy5, dz5);
         }

         // Contribution (0,1,1)
         final float dx6 = dx0 - SimplexNoise.SQUISH_CONSTANT_3_2;
         final float dy6 = dy4;
         final float dz6 = dz5;
         float attn6 = 2 - dx6 * dx6 - dy6 * dy6 - dz6 * dz6;
         if (attn6 > 0) {
            attn6 *= attn6;
            value += attn6 * attn6 * this.extrapolate(
               xsb, ysb + 1, zsb + 1,
               dx6, dy6, dz6);
         }
      }

      // First extra vertex
      float attn_ext0 = 2 - dx_ext0 * dx_ext0 - dy_ext0 * dy_ext0
            - dz_ext0 * dz_ext0;
      if (attn_ext0 > 0) {
         attn_ext0 *= attn_ext0;
         value += attn_ext0 * attn_ext0 * this.extrapolate(
            xsv_ext0, ysv_ext0, zsv_ext0,
            dx_ext0, dy_ext0, dz_ext0);
      }

      // Second extra vertex
      float attn_ext1 = 2 - dx_ext1 * dx_ext1 - dy_ext1 * dy_ext1
            - dz_ext1 * dz_ext1;
      if (attn_ext1 > 0) {
         attn_ext1 *= attn_ext1;
         value += attn_ext1 * attn_ext1 * this.extrapolate(
            xsv_ext1, ysv_ext1, zsv_ext1,
            dx_ext1, dy_ext1, dz_ext1);
      }

      return value * SimplexNoise.INV_NORM_CONST_3;
   }

   public float eval (
         final float x,
         final float y,
         final float z,
         final float w ) {

      // Place input coordinates on simplectic honeycomb.
      final float stretchOffset = (x + y + z + w) * SimplexNoise.STRETCH_CONSTANT_4;
      final float xs = x + stretchOffset;
      final float ys = y + stretchOffset;
      final float zs = z + stretchOffset;
      final float ws = w + stretchOffset;

      // Floor to get simplectic honeycomb coordinates of
      // rhombo-hypercube super-cell origin.
      final int xsb = Utils.floorToInt(xs);
      final int ysb = Utils.floorToInt(ys);
      final int zsb = Utils.floorToInt(zs);
      final int wsb = Utils.floorToInt(ws);

      // Skew out to get actual coordinates of stretched
      // rhombo-hypercube origin. We'll need these later.
      final float squishOffset = (xsb + ysb + zsb + wsb) * SimplexNoise.SQUISH_CONSTANT_4;
      final float xb = xsb + squishOffset;
      final float yb = ysb + squishOffset;
      final float zb = zsb + squishOffset;
      final float wb = wsb + squishOffset;

      // Compute simplectic honeycomb coordinates relative to
      // rhombo-hypercube origin.
      final float xins = xs - xsb;
      final float yins = ys - ysb;
      final float zins = zs - zsb;
      final float wins = ws - wsb;

      // Sum those together to get a value that determines which
      // region we're in.
      final float inSum = xins + yins + zins + wins;

      // Positions relative to origin point.
      float dx0 = x - xb;
      float dy0 = y - yb;
      float dz0 = z - zb;
      float dw0 = w - wb;

      // We'll be defining these inside the next block and using
      // them afterwards.
      float dx_ext0;
      float dy_ext0;
      float dz_ext0;
      float dw_ext0;

      float dx_ext1;
      float dy_ext1;
      float dz_ext1;
      float dw_ext1;
      
      float dx_ext2;
      float dy_ext2;
      float dz_ext2;
      float dw_ext2;
      
      int xsv_ext0;
      int ysv_ext0;
      int zsv_ext0;
      int wsv_ext0;
      
      int xsv_ext1;
      int ysv_ext1;
      int zsv_ext1;
      int wsv_ext1;
      
      int xsv_ext2;
      int ysv_ext2;
      int zsv_ext2;
      int wsv_ext2;

      float value = 0;
      if (inSum <= 1) { 
         // We're inside the pentachoron (4-Simplex) at (0,0,0,0)

         // Determine which two of (0,0,0,1), (0,0,1,0), (0,1,0,0),
         // (1,0,0,0) are closest.
         byte aPoint = 0x01;
         float aScore = xins;
         byte bPoint = 0x02;
         float bScore = yins;
         if (aScore >= bScore && zins > bScore) {
            bScore = zins;
            bPoint = 0x04;
         } else if (aScore < bScore && zins > aScore) {
            aScore = zins;
            aPoint = 0x04;
         }
         if (aScore >= bScore && wins > bScore) {
            bScore = wins;
            bPoint = 0x08;
         } else if (aScore < bScore && wins > aScore) {
            aScore = wins;
            aPoint = 0x08;
         }

         /* Now we determine the three lattice points not part of the
          * pentachoron that may contribute.
          * This depends on the closest two pentachoron vertices,
          * including (0, 0, 0, 0) */
         final float uins = 1 - inSum;
         if (uins > aScore || uins > bScore) { // (0,0,0,0) is one of the
                                               // closest two pentachoron
                                               // vertices.
            final byte c = bScore > aScore ? bPoint : aPoint; // Our other
                                                              // closest
            // vertex is the
            // closest out of a
            // and b.
            if ((c & 0x01) == 0) {
               xsv_ext0 = xsb - 1;
               xsv_ext1 = xsv_ext2 = xsb;
               dx_ext0 = dx0 + 1;
               dx_ext1 = dx_ext2 = dx0;
            } else {
               xsv_ext0 = xsv_ext1 = xsv_ext2 = xsb + 1;
               dx_ext0 = dx_ext1 = dx_ext2 = dx0 - 1;
            }

            if ((c & 0x02) == 0) {
               ysv_ext0 = ysv_ext1 = ysv_ext2 = ysb;
               dy_ext0 = dy_ext1 = dy_ext2 = dy0;
               if ((c & 0x01) == 0x01) {
                  ysv_ext0 -= 1;
                  dy_ext0 += 1;
               } else {
                  ysv_ext1 -= 1;
                  dy_ext1 += 1;
               }
            } else {
               ysv_ext0 = ysv_ext1 = ysv_ext2 = ysb + 1;
               dy_ext0 = dy_ext1 = dy_ext2 = dy0 - 1;
            }

            if ((c & 0x04) == 0) {
               zsv_ext0 = zsv_ext1 = zsv_ext2 = zsb;
               dz_ext0 = dz_ext1 = dz_ext2 = dz0;
               if ((c & 0x03) != 0) {
                  if ((c & 0x03) == 0x03) {
                     zsv_ext0 -= 1;
                     dz_ext0 += 1;
                  } else {
                     zsv_ext1 -= 1;
                     dz_ext1 += 1;
                  }
               } else {
                  zsv_ext2 -= 1;
                  dz_ext2 += 1;
               }
            } else {
               zsv_ext0 = zsv_ext1 = zsv_ext2 = zsb + 1;
               dz_ext0 = dz_ext1 = dz_ext2 = dz0 - 1;
            }

            if ((c & 0x08) == 0) {
               wsv_ext0 = wsv_ext1 = wsb;
               wsv_ext2 = wsb - 1;
               dw_ext0 = dw_ext1 = dw0;
               dw_ext2 = dw0 + 1;
            } else {
               wsv_ext0 = wsv_ext1 = wsv_ext2 = wsb + 1;
               dw_ext0 = dw_ext1 = dw_ext2 = dw0 - 1;
            }
         } else { // (0,0,0,0) is not one of the closest two pentachoron
                  // vertices.
            final byte c = (byte) (aPoint | bPoint); // Our three extra vertices
                                                     // are
            // determined by the closest two.

            if ((c & 0x01) == 0) {
               xsv_ext0 = xsv_ext2 = xsb;
               xsv_ext1 = xsb - 1;
               dx_ext0 = dx0 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dx_ext1 = dx0 + 1 - SimplexNoise.SQUISH_CONSTANT_4;
               dx_ext2 = dx0 - SimplexNoise.SQUISH_CONSTANT_4;
            } else {
               xsv_ext0 = xsv_ext1 = xsv_ext2 = xsb + 1;
               dx_ext0 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dx_ext1 = dx_ext2 = dx0 - 1
                     - SimplexNoise.SQUISH_CONSTANT_4;
            }

            if ((c & 0x02) == 0) {
               ysv_ext0 = ysv_ext1 = ysv_ext2 = ysb;
               dy_ext0 = dy0 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dy_ext1 = dy_ext2 = dy0 - SimplexNoise.SQUISH_CONSTANT_4;
               if ((c & 0x01) == 0x01) {
                  ysv_ext1 -= 1;
                  dy_ext1 += 1;
               } else {
                  ysv_ext2 -= 1;
                  dy_ext2 += 1;
               }
            } else {
               ysv_ext0 = ysv_ext1 = ysv_ext2 = ysb + 1;
               dy_ext0 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dy_ext1 = dy_ext2 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4;
            }

            if ((c & 0x04) == 0) {
               zsv_ext0 = zsv_ext1 = zsv_ext2 = zsb;
               dz_ext0 = dz0 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dz_ext1 = dz_ext2 = dz0 - SimplexNoise.SQUISH_CONSTANT_4;
               if ((c & 0x03) == 0x03) {
                  zsv_ext1 -= 1;
                  dz_ext1 += 1;
               } else {
                  zsv_ext2 -= 1;
                  dz_ext2 += 1;
               }
            } else {
               zsv_ext0 = zsv_ext1 = zsv_ext2 = zsb + 1;
               dz_ext0 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dz_ext1 = dz_ext2 = dz0 - 1
                     - SimplexNoise.SQUISH_CONSTANT_4;
            }

            if ((c & 0x08) == 0) {
               wsv_ext0 = wsv_ext1 = wsb;
               wsv_ext2 = wsb - 1;
               dw_ext0 = dw0 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dw_ext1 = dw0 - SimplexNoise.SQUISH_CONSTANT_4;
               dw_ext2 = dw0 + 1 - SimplexNoise.SQUISH_CONSTANT_4;
            } else {
               wsv_ext0 = wsv_ext1 = wsv_ext2 = wsb + 1;
               dw_ext0 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dw_ext1 = dw_ext2 = dw0 - 1
                     - SimplexNoise.SQUISH_CONSTANT_4;
            }
         }

         // Contribution (0,0,0,0)
         float attn0 = 2 - dx0 * dx0 - dy0 * dy0 - dz0 * dz0 - dw0 * dw0;
         if (attn0 > 0) {
            attn0 *= attn0;
            value += attn0 * attn0 * this.extrapolate(
               xsb, ysb, zsb, wsb,
               dx0, dy0, dz0, dw0);
         }

         // Contribution (1,0,0,0)
         final float dx1 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4;
         final float dy1 = dy0 - SimplexNoise.SQUISH_CONSTANT_4;
         final float dz1 = dz0 - SimplexNoise.SQUISH_CONSTANT_4;
         final float dw1 = dw0 - SimplexNoise.SQUISH_CONSTANT_4;
         float attn1 = 2 - dx1 * dx1 - dy1 * dy1 - dz1 * dz1 - dw1 * dw1;
         if (attn1 > 0) {
            attn1 *= attn1;
            value += attn1 * attn1 * this.extrapolate(
               xsb + 1, ysb, zsb, wsb,
               dx1, dy1, dz1, dw1);
         }

         // Contribution (0, 1, 0, 0)
         final float dx2 = dx0 - 0 - SimplexNoise.SQUISH_CONSTANT_4;
         final float dy2 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4;
         final float dz2 = dz1;
         final float dw2 = dw1;
         float attn2 = 2 - dx2 * dx2 - dy2 * dy2 - dz2 * dz2 - dw2 * dw2;
         if (attn2 > 0) {
            attn2 *= attn2;
            value += attn2 * attn2 * this.extrapolate(
               xsb, ysb + 1, zsb, wsb,
               dx2, dy2, dz2, dw2);
         }

         // Contribution (0,0,1,0)
         final float dx3 = dx2;
         final float dy3 = dy1;
         final float dz3 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4;
         final float dw3 = dw1;
         float attn3 = 2 - dx3 * dx3 - dy3 * dy3 - dz3 * dz3 - dw3 * dw3;
         if (attn3 > 0) {
            attn3 *= attn3;
            value += attn3 * attn3 * this.extrapolate(
               xsb, ysb, zsb + 1, wsb,
               dx3, dy3, dz3, dw3);
         }

         // Contribution (0,0,0,1)
         final float dx4 = dx2;
         final float dy4 = dy1;
         final float dz4 = dz1;
         final float dw4 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4;
         float attn4 = 2 - dx4 * dx4 - dy4 * dy4 - dz4 * dz4 - dw4 * dw4;
         if (attn4 > 0) {
            attn4 *= attn4;
            value += attn4 * attn4 * this.extrapolate(
               xsb, ysb, zsb, wsb + 1,
               dx4, dy4, dz4, dw4);
         }
      } else if (inSum >= 3) {
         // We're inside the pentachoron (4-Simplex) at
         // (1,1,1,1)
         // Determine which two of (1,1,1,0), (1,1,0,1), (1,0,1,1),
         // (0,1,1,1) are closest.
         byte aPoint = 0x0E;
         float aScore = xins;
         byte bPoint = 0x0D;
         float bScore = yins;
         if (aScore <= bScore && zins < bScore) {
            bScore = zins;
            bPoint = 0x0B;
         } else if (aScore > bScore && zins < aScore) {
            aScore = zins;
            aPoint = 0x0B;
         }
         if (aScore <= bScore && wins < bScore) {
            bScore = wins;
            bPoint = 0x07;
         } else if (aScore > bScore && wins < aScore) {
            aScore = wins;
            aPoint = 0x07;
         }

         /**  
          * Now we determine the three lattice points not part of the
          * pentachoron that may contribute.
          * This depends on the closest two pentachoron vertices,
          * including (0,0,0,0) 
          */
         final float uins = 4 - inSum;
         if (uins < aScore || uins < bScore) {
            // (1,1,1,1) is one of the
            // closest two pentachoron
            // vertices.
            final byte c = bScore < aScore ? bPoint : aPoint; // Our other
                                                              // closest
            // vertex is the
            // closest out of a
            // and b.

            if ((c & 0x01) != 0) {
               xsv_ext0 = xsb + 2;
               xsv_ext1 = xsv_ext2 = xsb + 1;
               dx_ext0 = dx0 - 2 - SimplexNoise.SQUISH_CONSTANT_4_4;
               dx_ext1 = dx_ext2 = dx0 - 1
                     - SimplexNoise.SQUISH_CONSTANT_4_4;
            } else {
               xsv_ext0 = xsv_ext1 = xsv_ext2 = xsb;
               dx_ext0 = dx_ext1 = dx_ext2 = dx0
                     - SimplexNoise.SQUISH_CONSTANT_4_4;
            }

            if ((c & 0x02) != 0) {
               ysv_ext0 = ysv_ext1 = ysv_ext2 = ysb + 1;
               dy_ext0 = dy_ext1 = dy_ext2 = dy0 - 1
                     - SimplexNoise.SQUISH_CONSTANT_4_4;
               if ((c & 0x01) != 0) {
                  ysv_ext1 += 1;
                  dy_ext1 -= 1;
               } else {
                  ysv_ext0 += 1;
                  dy_ext0 -= 1;
               }
            } else {
               ysv_ext0 = ysv_ext1 = ysv_ext2 = ysb;
               dy_ext0 = dy_ext1 = dy_ext2 = dy0
                     - SimplexNoise.SQUISH_CONSTANT_4_4;
            }

            if ((c & 0x04) != 0) {
               zsv_ext0 = zsv_ext1 = zsv_ext2 = zsb + 1;
               dz_ext0 = dz_ext1 = dz_ext2 = dz0 - 1
                     - SimplexNoise.SQUISH_CONSTANT_4_4;
               if ((c & 0x03) != 0x03) {
                  if ((c & 0x03) == 0) {
                     zsv_ext0 += 1;
                     dz_ext0 -= 1;
                  } else {
                     zsv_ext1 += 1;
                     dz_ext1 -= 1;
                  }
               } else {
                  zsv_ext2 += 1;
                  dz_ext2 -= 1;
               }
            } else {
               zsv_ext0 = zsv_ext1 = zsv_ext2 = zsb;
               dz_ext0 = dz_ext1 = dz_ext2 = dz0
                     - SimplexNoise.SQUISH_CONSTANT_4_4;
            }

            if ((c & 0x08) != 0) {
               wsv_ext0 = wsv_ext1 = wsb + 1;
               wsv_ext2 = wsb + 2;
               dw_ext0 = dw_ext1 = dw0 - 1
                     - SimplexNoise.SQUISH_CONSTANT_4_4;
               dw_ext2 = dw0 - 2 - SimplexNoise.SQUISH_CONSTANT_4_4;
            } else {
               wsv_ext0 = wsv_ext1 = wsv_ext2 = wsb;
               dw_ext0 = dw_ext1 = dw_ext2 = dw0
                     - SimplexNoise.SQUISH_CONSTANT_4_4;
            }
         } else { 
            // (1,1,1,1) is not one of the closest two pentachoron
                  // vertices.
            final byte c = (byte) (aPoint & bPoint); 
            // Our three extra vertices
            // are
            // determined by the closest two.

            if ((c & 0x01) != 0) {
               xsv_ext0 = xsv_ext2 = xsb + 1;
               xsv_ext1 = xsb + 2;
               dx_ext0 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dx_ext1 = dx0 - 2 - SimplexNoise.SQUISH_CONSTANT_4_3;
               dx_ext2 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
            } else {
               xsv_ext0 = xsv_ext1 = xsv_ext2 = xsb;
               dx_ext0 = dx0 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dx_ext1 = dx_ext2 = dx0 - SimplexNoise.SQUISH_CONSTANT_4_3;
            }

            if ((c & 0x02) != 0) {
               ysv_ext0 = ysv_ext1 = ysv_ext2 = ysb + 1;
               dy_ext0 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dy_ext1 = dy_ext2 = dy0 - 1
                     - SimplexNoise.SQUISH_CONSTANT_4_3;
               if ((c & 0x01) != 0) {
                  ysv_ext2 += 1;
                  dy_ext2 -= 1;
               } else {
                  ysv_ext1 += 1;
                  dy_ext1 -= 1;
               }
            } else {
               ysv_ext0 = ysv_ext1 = ysv_ext2 = ysb;
               dy_ext0 = dy0 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dy_ext1 = dy_ext2 = dy0
                     - SimplexNoise.SQUISH_CONSTANT_4_3;
            }

            if ((c & 0x04) != 0) {
               zsv_ext0 = zsv_ext1 = zsv_ext2 = zsb + 1;
               dz_ext0 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dz_ext1 = dz_ext2 = dz0 - 1
                     - SimplexNoise.SQUISH_CONSTANT_4_3;
               if ((c & 0x03) != 0) {
                  zsv_ext2 += 1;
                  dz_ext2 -= 1;
               } else {
                  zsv_ext1 += 1;
                  dz_ext1 -= 1;
               }
            } else {
               zsv_ext0 = zsv_ext1 = zsv_ext2 = zsb;
               dz_ext0 = dz0 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dz_ext1 = dz_ext2 = dz0
                     - SimplexNoise.SQUISH_CONSTANT_4_3;
            }

            if ((c & 0x08) != 0) {
               wsv_ext0 = wsv_ext1 = wsb + 1;
               wsv_ext2 = wsb + 2;
               dw_ext0 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dw_ext1 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
               dw_ext2 = dw0 - 2 - SimplexNoise.SQUISH_CONSTANT_4_3;
            } else {
               wsv_ext0 = wsv_ext1 = wsv_ext2 = wsb;
               dw_ext0 = dw0 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dw_ext1 = dw_ext2 = dw0
                     - SimplexNoise.SQUISH_CONSTANT_4_3;
            }
         }

         // Contribution (1,1,1,0)
         final float dx4 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
         final float dy4 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
         final float dz4 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
         final float dw4 = dw0 - SimplexNoise.SQUISH_CONSTANT_4_3;
         float attn4 = 2 - dx4 * dx4 - dy4 * dy4 - dz4 * dz4 - dw4 * dw4;
         if (attn4 > 0) {
            attn4 *= attn4;
            value += attn4 * attn4 * this.extrapolate(
               xsb + 1, ysb + 1, zsb + 1, wsb,
               dx4, dy4, dz4, dw4);
         }

         // Contribution (1,1,0,1)
         final float dx3 = dx4;
         final float dy3 = dy4;
         final float dz3 = dz0 - SimplexNoise.SQUISH_CONSTANT_4_3;
         final float dw3 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
         float attn3 = 2 - dx3 * dx3 - dy3 * dy3 - dz3 * dz3 - dw3 * dw3;
         if (attn3 > 0) {
            attn3 *= attn3;
            value += attn3 * attn3 * this.extrapolate(
               xsb + 1, ysb + 1, zsb, wsb + 1,
               dx3, dy3, dz3, dw3);
         }

         // Contribution (1,0,1,1)
         final float dx2 = dx4;
         final float dy2 = dy0 - SimplexNoise.SQUISH_CONSTANT_4_3;
         final float dz2 = dz4;
         final float dw2 = dw3;
         float attn2 = 2 - dx2 * dx2 - dy2 * dy2 - dz2 * dz2 - dw2 * dw2;
         if (attn2 > 0) {
            attn2 *= attn2;
            value += attn2 * attn2 * this.extrapolate(
               xsb + 1, ysb, zsb + 1, wsb + 1,
               dx2, dy2, dz2, dw2);
         }

         // Contribution (0,1,1,1)
         final float dx1 = dx0 - SimplexNoise.SQUISH_CONSTANT_4_3;
         final float dz1 = dz4;
         final float dy1 = dy4;
         final float dw1 = dw3;
         float attn1 = 2 - dx1 * dx1 - dy1 * dy1 - dz1 * dz1 - dw1 * dw1;
         if (attn1 > 0) {
            attn1 *= attn1;
            value += attn1 * attn1 * this.extrapolate(
               xsb, ysb + 1, zsb + 1, wsb + 1,
               dx1, dy1, dz1, dw1);
         }

         // Contribution (1,1,1,1)
         dx0 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_4;
         dy0 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_4;
         dz0 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_4;
         dw0 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_4;
         float attn0 = 2 - dx0 * dx0 - dy0 * dy0 - dz0 * dz0 - dw0 * dw0;
         if (attn0 > 0) {
            attn0 *= attn0;
            value += attn0 * attn0 * this.extrapolate(
               xsb + 1, ysb + 1, zsb + 1, wsb + 1,
               dx0, dy0, dz0, dw0);
         }
      } else if (inSum <= 2) { // We're inside the first dispentachoron
                               // (Rectified 4-Simplex)
         float aScore;
         byte aPoint;
         boolean aIsBiggerSide = true;
         float bScore;
         byte bPoint;
         boolean bIsBiggerSide = true;

         // Decide between (1,1,0,0) and (0,0,1,1)
         if (xins + yins > zins + wins) {
            aScore = xins + yins;
            aPoint = 0x03;
         } else {
            aScore = zins + wins;
            aPoint = 0x0C;
         }

         // Decide between (1,0,1,0) and (0,1,0,1)
         if (xins + zins > yins + wins) {
            bScore = xins + zins;
            bPoint = 0x05;
         } else {
            bScore = yins + wins;
            bPoint = 0x0A;
         }

         // Closer between (1,0,0,1) and (0,1,1,0) will replace the
         // further of a and b, if closer.
         if (xins + wins > yins + zins) {
            final float score = xins + wins;
            if (aScore >= bScore && score > bScore) {
               bScore = score;
               bPoint = 0x09;
            } else if (aScore < bScore && score > aScore) {
               aScore = score;
               aPoint = 0x09;
            }
         } else {
            final float score = yins + zins;
            if (aScore >= bScore && score > bScore) {
               bScore = score;
               bPoint = 0x06;
            } else if (aScore < bScore && score > aScore) {
               aScore = score;
               aPoint = 0x06;
            }
         }

         // Decide if (1,0,0,0) is closer.
         final float p1 = 2 - inSum + xins;
         if (aScore >= bScore && p1 > bScore) {
            bScore = p1;
            bPoint = 0x01;
            bIsBiggerSide = false;
         } else if (aScore < bScore && p1 > aScore) {
            aScore = p1;
            aPoint = 0x01;
            aIsBiggerSide = false;
         }

         // Decide if (0,1,0,0) is closer.
         final float p2 = 2 - inSum + yins;
         if (aScore >= bScore && p2 > bScore) {
            bScore = p2;
            bPoint = 0x02;
            bIsBiggerSide = false;
         } else if (aScore < bScore && p2 > aScore) {
            aScore = p2;
            aPoint = 0x02;
            aIsBiggerSide = false;
         }

         // Decide if (0,0,1,0) is closer.
         final float p3 = 2 - inSum + zins;
         if (aScore >= bScore && p3 > bScore) {
            bScore = p3;
            bPoint = 0x04;
            bIsBiggerSide = false;
         } else if (aScore < bScore && p3 > aScore) {
            aScore = p3;
            aPoint = 0x04;
            aIsBiggerSide = false;
         }

         // Decide if (0,0,0,1) is closer.
         final float p4 = 2 - inSum + wins;
         if (aScore >= bScore && p4 > bScore) {
            bScore = p4;
            bPoint = 0x08;
            bIsBiggerSide = false;
         } else if (aScore < bScore && p4 > aScore) {
            aScore = p4;
            aPoint = 0x08;
            aIsBiggerSide = false;
         }

         // Where each of the two closest points are determines how
         // the extra three vertices are calculated.
         if (aIsBiggerSide == bIsBiggerSide) {
            if (aIsBiggerSide) { // Both closest points on the bigger side
               final byte c1 = (byte) (aPoint | bPoint);
               final byte c2 = (byte) (aPoint & bPoint);
               if ((c1 & 0x01) == 0) {
                  xsv_ext0 = xsb;
                  xsv_ext1 = xsb - 1;
                  dx_ext0 = dx0 - SimplexNoise.SQUISH_CONSTANT_4_3;
                  dx_ext1 = dx0 + 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               } else {
                  xsv_ext0 = xsv_ext1 = xsb + 1;
                  dx_ext0 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
                  dx_ext1 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               }

               if ((c1 & 0x02) == 0) {
                  ysv_ext0 = ysb;
                  ysv_ext1 = ysb - 1;
                  dy_ext0 = dy0 - SimplexNoise.SQUISH_CONSTANT_4_3;
                  dy_ext1 = dy0 + 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               } else {
                  ysv_ext0 = ysv_ext1 = ysb + 1;
                  dy_ext0 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
                  dy_ext1 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               }

               if ((c1 & 0x04) == 0) {
                  zsv_ext0 = zsb;
                  zsv_ext1 = zsb - 1;
                  dz_ext0 = dz0 - SimplexNoise.SQUISH_CONSTANT_4_3;
                  dz_ext1 = dz0 + 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               } else {
                  zsv_ext0 = zsv_ext1 = zsb + 1;
                  dz_ext0 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
                  dz_ext1 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               }

               if ((c1 & 0x08) == 0) {
                  wsv_ext0 = wsb;
                  wsv_ext1 = wsb - 1;
                  dw_ext0 = dw0 - SimplexNoise.SQUISH_CONSTANT_4_3;
                  dw_ext1 = dw0 + 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               } else {
                  wsv_ext0 = wsv_ext1 = wsb + 1;
                  dw_ext0 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
                  dw_ext1 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               }

               // One combination is a permutation of (0,0,0,2) based on c2
               xsv_ext2 = xsb;
               ysv_ext2 = ysb;
               zsv_ext2 = zsb;
               wsv_ext2 = wsb;
               dx_ext2 = dx0 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dy_ext2 = dy0 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dz_ext2 = dz0 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dw_ext2 = dw0 - SimplexNoise.SQUISH_CONSTANT_4_2;
               if ((c2 & 0x01) != 0) {
                  xsv_ext2 += 2;
                  dx_ext2 -= 2;
               } else if ((c2 & 0x02) != 0) {
                  ysv_ext2 += 2;
                  dy_ext2 -= 2;
               } else if ((c2 & 0x04) != 0) {
                  zsv_ext2 += 2;
                  dz_ext2 -= 2;
               } else {
                  wsv_ext2 += 2;
                  dw_ext2 -= 2;
               }

            } else { // Both closest points on the smaller side
               // One of the two extra points is (0,0,0,0)
               xsv_ext2 = xsb;
               ysv_ext2 = ysb;
               zsv_ext2 = zsb;
               wsv_ext2 = wsb;
               dx_ext2 = dx0;
               dy_ext2 = dy0;
               dz_ext2 = dz0;
               dw_ext2 = dw0;

               // Other two points are based on the omitted axes.
               final byte c = (byte) (aPoint | bPoint);

               if ((c & 0x01) == 0) {
                  xsv_ext0 = xsb - 1;
                  xsv_ext1 = xsb;
                  dx_ext0 = dx0 + 1 - SimplexNoise.SQUISH_CONSTANT_4;
                  dx_ext1 = dx0 - SimplexNoise.SQUISH_CONSTANT_4;
               } else {
                  xsv_ext0 = xsv_ext1 = xsb + 1;
                  dx_ext0 = dx_ext1 = dx0 - 1
                        - SimplexNoise.SQUISH_CONSTANT_4;
               }

               if ((c & 0x02) == 0) {
                  ysv_ext0 = ysv_ext1 = ysb;
                  dy_ext0 = dy_ext1 = dy0 - SimplexNoise.SQUISH_CONSTANT_4;
                  if ((c & 0x01) == 0x01) {
                     ysv_ext0 -= 1;
                     dy_ext0 += 1;
                  } else {
                     ysv_ext1 -= 1;
                     dy_ext1 += 1;
                  }
               } else {
                  ysv_ext0 = ysv_ext1 = ysb + 1;
                  dy_ext0 = dy_ext1 = dy0 - 1
                        - SimplexNoise.SQUISH_CONSTANT_4;
               }

               if ((c & 0x04) == 0) {
                  zsv_ext0 = zsv_ext1 = zsb;
                  dz_ext0 = dz_ext1 = dz0 - SimplexNoise.SQUISH_CONSTANT_4;
                  if ((c & 0x03) == 0x03) {
                     zsv_ext0 -= 1;
                     dz_ext0 += 1;
                  } else {
                     zsv_ext1 -= 1;
                     dz_ext1 += 1;
                  }
               } else {
                  zsv_ext0 = zsv_ext1 = zsb + 1;
                  dz_ext0 = dz_ext1 = dz0 - 1
                        - SimplexNoise.SQUISH_CONSTANT_4;
               }

               if ((c & 0x08) == 0) {
                  wsv_ext0 = wsb;
                  wsv_ext1 = wsb - 1;
                  dw_ext0 = dw0 - SimplexNoise.SQUISH_CONSTANT_4;
                  dw_ext1 = dw0 + 1 - SimplexNoise.SQUISH_CONSTANT_4;
               } else {
                  wsv_ext0 = wsv_ext1 = wsb + 1;
                  dw_ext0 = dw_ext1 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4;
               }

            }
         } else { 
            // One point on each "side"
            byte c1;
            byte c2;
            if (aIsBiggerSide) {
               c1 = aPoint;
               c2 = bPoint;
            } else {
               c1 = bPoint;
               c2 = aPoint;
            }

            // Two contributions are the bigger-sided point with each 0
            // replaced with -1.
            if ((c1 & 0x01) == 0) {
               xsv_ext0 = xsb - 1;
               xsv_ext1 = xsb;
               dx_ext0 = dx0 + 1 - SimplexNoise.SQUISH_CONSTANT_4;
               dx_ext1 = dx0 - SimplexNoise.SQUISH_CONSTANT_4;
            } else {
               xsv_ext0 = xsv_ext1 = xsb + 1;
               dx_ext0 = dx_ext1 = dx0 - 1
                     - SimplexNoise.SQUISH_CONSTANT_4;
            }

            if ((c1 & 0x02) == 0) {
               ysv_ext0 = ysv_ext1 = ysb;
               dy_ext0 = dy_ext1 = dy0 - SimplexNoise.SQUISH_CONSTANT_4;
               if ((c1 & 0x01) == 0x01) {
                  ysv_ext0 -= 1;
                  dy_ext0 += 1;
               } else {
                  ysv_ext1 -= 1;
                  dy_ext1 += 1;
               }
            } else {
               ysv_ext0 = ysv_ext1 = ysb + 1;
               dy_ext0 = dy_ext1 = dy0 - 1
                     - SimplexNoise.SQUISH_CONSTANT_4;
            }

            if ((c1 & 0x04) == 0) {
               zsv_ext0 = zsv_ext1 = zsb;
               dz_ext0 = dz_ext1 = dz0 - SimplexNoise.SQUISH_CONSTANT_4;
               if ((c1 & 0x03) == 0x03) {
                  zsv_ext0 -= 1;
                  dz_ext0 += 1;
               } else {
                  zsv_ext1 -= 1;
                  dz_ext1 += 1;
               }
            } else {
               zsv_ext0 = zsv_ext1 = zsb + 1;
               dz_ext0 = dz_ext1 = dz0 - 1
                     - SimplexNoise.SQUISH_CONSTANT_4;
            }

            if ((c1 & 0x08) == 0) {
               wsv_ext0 = wsb;
               wsv_ext1 = wsb - 1;
               dw_ext0 = dw0 - SimplexNoise.SQUISH_CONSTANT_4;
               dw_ext1 = dw0 + 1 - SimplexNoise.SQUISH_CONSTANT_4;
            } else {
               wsv_ext0 = wsv_ext1 = wsb + 1;
               dw_ext0 = dw_ext1 = dw0 - 1
                     - SimplexNoise.SQUISH_CONSTANT_4;
            }

            // One contribution is a permutation of (0,0,0,2) based on
            // the smaller-sided point
            xsv_ext2 = xsb;
            ysv_ext2 = ysb;
            zsv_ext2 = zsb;
            wsv_ext2 = wsb;
            dx_ext2 = dx0 - SimplexNoise.SQUISH_CONSTANT_4_2;
            dy_ext2 = dy0 - SimplexNoise.SQUISH_CONSTANT_4_2;
            dz_ext2 = dz0 - SimplexNoise.SQUISH_CONSTANT_4_2;
            dw_ext2 = dw0 - SimplexNoise.SQUISH_CONSTANT_4_2;
            if ((c2 & 0x01) != 0) {
               xsv_ext2 += 2;
               dx_ext2 -= 2;
            } else if ((c2 & 0x02) != 0) {
               ysv_ext2 += 2;
               dy_ext2 -= 2;
            } else if ((c2 & 0x04) != 0) {
               zsv_ext2 += 2;
               dz_ext2 -= 2;
            } else {
               wsv_ext2 += 2;
               dw_ext2 -= 2;
            }
         }

         // Contribution (1,0,0,0)
         final float dx1 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4;
         final float dy1 = dy0 - SimplexNoise.SQUISH_CONSTANT_4;
         final float dz1 = dz0 - SimplexNoise.SQUISH_CONSTANT_4;
         final float dw1 = dw0 - SimplexNoise.SQUISH_CONSTANT_4;
         float attn1 = 2 - dx1 * dx1 - dy1 * dy1 - dz1 * dz1 - dw1 * dw1;
         if (attn1 > 0) {
            attn1 *= attn1;
            value += attn1 * attn1 * this.extrapolate(
               xsb + 1, ysb, zsb, wsb, dx1, dy1, dz1, dw1);
         }

         // Contribution (0,1,0,0)
         final float dx2 = dx0 - SimplexNoise.SQUISH_CONSTANT_4;
         final float dy2 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4;
         final float dz2 = dz1;
         final float dw2 = dw1;
         float attn2 = 2 - dx2 * dx2 - dy2 * dy2 - dz2 * dz2 - dw2 * dw2;
         if (attn2 > 0) {
            attn2 *= attn2;
            value += attn2 * attn2 * this.extrapolate(
               xsb, ysb + 1, zsb, wsb,
               dx2, dy2, dz2, dw2);
         }

         // Contribution (0,0,1,0)
         final float dx3 = dx2;
         final float dy3 = dy1;
         final float dz3 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4;
         final float dw3 = dw1;
         float attn3 = 2 - dx3 * dx3 - dy3 * dy3 - dz3 * dz3 - dw3 * dw3;
         if (attn3 > 0) {
            attn3 *= attn3;
            value += attn3 * attn3 * this.extrapolate(
               xsb, ysb, zsb + 1, wsb,
               dx3, dy3, dz3, dw3);
         }

         // Contribution (0,0,0,1)
         final float dx4 = dx2;
         final float dy4 = dy1;
         final float dz4 = dz1;
         final float dw4 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4;
         float attn4 = 2 - dx4 * dx4 - dy4 * dy4 - dz4 * dz4 - dw4 * dw4;
         if (attn4 > 0) {
            attn4 *= attn4;
            value += attn4 * attn4 * this.extrapolate(
               xsb, ysb, zsb, wsb,
               dx4, dy4, dz4, dw4);
         }

         // Contribution (1,1,0,0)
         final float dx5 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dy5 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dz5 = dz0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dw5 = dw0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         float attn5 = 2 - dx5 * dx5 - dy5 * dy5 - dz5 * dz5 - dw5 * dw5;
         if (attn5 > 0) {
            attn5 *= attn5;
            value += attn5 * attn5 * this.extrapolate(
               xsb + 1, ysb + 1, zsb, wsb,
               dx5, dy5, dz5, dw5);
         }

         // Contribution (1,0,1,0)
         final float dx6 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dy6 = dy0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dz6 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dw6 = dw0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         float attn6 = 2 - dx6 * dx6 - dy6 * dy6 - dz6 * dz6 - dw6 * dw6;
         if (attn6 > 0) {
            attn6 *= attn6;
            value += attn6 * attn6 * this.extrapolate(
               xsb + 1, ysb, zsb + 1, wsb, 
               dx6, dy6, dz6, dw6);
         }

         // Contribution (1,0,0,1)
         final float dx7 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dy7 = dy0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dz7 = dz0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dw7 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         float attn7 = 2 - dx7 * dx7 - dy7 * dy7 - dz7 * dz7 - dw7 * dw7;
         if (attn7 > 0) {
            attn7 *= attn7;
            value += attn7 * attn7 * this.extrapolate(
               xsb + 1, ysb, zsb, wsb + 1, 
               dx7, dy7, dz7, dw7);
         }

         // Contribution (0,1,1,0)
         final float dx8 = dx0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dy8 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dz8 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dw8 = dw0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         float attn8 = 2 - dx8 * dx8 - dy8 * dy8 - dz8 * dz8 - dw8 * dw8;
         if (attn8 > 0) {
            attn8 *= attn8;
            value += attn8 * attn8 * this.extrapolate(
               xsb, ysb + 1, zsb + 1, wsb,
               dx8, dy8, dz8, dw8);
         }

         // Contribution (0,1,0,1)
         final float dx9 = dx0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dy9 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dz9 = dz0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dw9 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         float attn9 = 2 - dx9 * dx9 - dy9 * dy9 - dz9 * dz9 - dw9 * dw9;
         if (attn9 > 0) {
            attn9 *= attn9;
            value += attn9 * attn9 * this.extrapolate(
               xsb, ysb + 1, zsb, wsb + 1,
               dx9, dy9, dz9, dw9);
         }

         // Contribution (0,0,1,1)
         final float dx10 = dx0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dy10 = dy0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dz10 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dw10 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         float attn10 = 2 - dx10 * dx10 - dy10 * dy10 - dz10 * dz10
               - dw10 * dw10;
         if (attn10 > 0) {
            attn10 *= attn10;
            value += attn10 * attn10 * this.extrapolate(
                     xsb, ysb, zsb + 1, wsb + 1,
                     dx10, dy10, dz10, dw10);
         }
      } else { 
         // We're inside the second dispentachoron (Rectified
               // 4-Simplex)
         float aScore;
         byte aPoint;
         boolean aIsBiggerSide = true;
         float bScore;
         byte bPoint;
         boolean bIsBiggerSide = true;

         // Decide between (0,0,1,1) and (1,1,0,0)
         if (xins + yins < zins + wins) {
            aScore = xins + yins;
            aPoint = 0x0c;
         } else {
            aScore = zins + wins;
            aPoint = 0x03;
         }

         // Decide between (0,1,0,1) and (1,0,1,0)
         if (xins + zins < yins + wins) {
            bScore = xins + zins;
            bPoint = 0x0a;
         } else {
            bScore = yins + wins;
            bPoint = 0x05;
         }

         // Closer between (0,1,1,0) and (1,0,0,1) will replace the
         // further of a and b, if closer.
         if (xins + wins < yins + zins) {
            final float score = xins + wins;
            if (aScore <= bScore && score < bScore) {
               bScore = score;
               bPoint = 0x06;
            } else if (aScore > bScore && score < aScore) {
               aScore = score;
               aPoint = 0x06;
            }
         } else {
            final float score = yins + zins;
            if (aScore <= bScore && score < bScore) {
               bScore = score;
               bPoint = 0x09;
            } else if (aScore > bScore && score < aScore) {
               aScore = score;
               aPoint = 0x09;
            }
         }

         // Decide if (0,1,1,1) is closer.
         final float p1 = 3 - inSum + xins;
         if (aScore <= bScore && p1 < bScore) {
            bScore = p1;
            bPoint = 0x0e;
            bIsBiggerSide = false;
         } else if (aScore > bScore && p1 < aScore) {
            aScore = p1;
            aPoint = 0x0e;
            aIsBiggerSide = false;
         }

         // Decide if (1,0,1,1) is closer.
         final float p2 = 3 - inSum + yins;
         if (aScore <= bScore && p2 < bScore) {
            bScore = p2;
            bPoint = 0x0d;
            bIsBiggerSide = false;
         } else if (aScore > bScore && p2 < aScore) {
            aScore = p2;
            aPoint = 0x0d;
            aIsBiggerSide = false;
         }

         // Decide if (1,1,0,1) is closer.
         final float p3 = 3 - inSum + zins;
         if (aScore <= bScore && p3 < bScore) {
            bScore = p3;
            bPoint = 0x0b;
            bIsBiggerSide = false;
         } else if (aScore > bScore && p3 < aScore) {
            aScore = p3;
            aPoint = 0x0B;
            aIsBiggerSide = false;
         }

         // Decide if (1,1,1,0) is closer.
         final float p4 = 3 - inSum + wins;
         if (aScore <= bScore && p4 < bScore) {
            bScore = p4;
            bPoint = 0x07;
            bIsBiggerSide = false;
         } else if (aScore > bScore && p4 < aScore) {
            aScore = p4;
            aPoint = 0x07;
            aIsBiggerSide = false;
         }

         // Where each of the two closest points are determines how
         // the extra three vertices are calculated.
         if (aIsBiggerSide == bIsBiggerSide) {
            if (aIsBiggerSide) { 
               // Both closest points on the bigger side
               final byte c1 = (byte) (aPoint & bPoint);
               final byte c2 = (byte) (aPoint | bPoint);

               // Two contributions are permutations of (0,0,0,1) and
               // (0,0,0,2) based on c1
               xsv_ext0 = xsv_ext1 = xsb;
               ysv_ext0 = ysv_ext1 = ysb;
               zsv_ext0 = zsv_ext1 = zsb;
               wsv_ext0 = wsv_ext1 = wsb;
               
               dx_ext0 = dx0 - SimplexNoise.SQUISH_CONSTANT_4;
               dy_ext0 = dy0 - SimplexNoise.SQUISH_CONSTANT_4;
               dz_ext0 = dz0 - SimplexNoise.SQUISH_CONSTANT_4;
               dw_ext0 = dw0 - SimplexNoise.SQUISH_CONSTANT_4;
               
               dx_ext1 = dx0 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dy_ext1 = dy0 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dz_ext1 = dz0 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dw_ext1 = dw0 - SimplexNoise.SQUISH_CONSTANT_4_2;
               
               if ((c1 & 0x01) != 0) {
                  xsv_ext0 += 1;
                  dx_ext0 -= 1;
                  xsv_ext1 += 2;
                  dx_ext1 -= 2;
               } else if ((c1 & 0x02) != 0) {
                  ysv_ext0 += 1;
                  dy_ext0 -= 1;
                  ysv_ext1 += 2;
                  dy_ext1 -= 2;
               } else if ((c1 & 0x04) != 0) {
                  zsv_ext0 += 1;
                  dz_ext0 -= 1;
                  zsv_ext1 += 2;
                  dz_ext1 -= 2;
               } else {
                  wsv_ext0 += 1;
                  dw_ext0 -= 1;
                  wsv_ext1 += 2;
                  dw_ext1 -= 2;
               }

               // One contribution is a permutation of (1,1,1,-1) based on
               // c2
               xsv_ext2 = xsb + 1;
               ysv_ext2 = ysb + 1;
               zsv_ext2 = zsb + 1;
               wsv_ext2 = wsb + 1;
               dx_ext2 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dy_ext2 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dz_ext2 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               dw_ext2 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
               if ((c2 & 0x01) == 0) {
                  xsv_ext2 -= 2;
                  dx_ext2 += 2;
               } else if ((c2 & 0x02) == 0) {
                  ysv_ext2 -= 2;
                  dy_ext2 += 2;
               } else if ((c2 & 0x04) == 0) {
                  zsv_ext2 -= 2;
                  dz_ext2 += 2;
               } else {
                  wsv_ext2 -= 2;
                  dw_ext2 += 2;
               }
            } else { 
               // Both closest points on the smaller side
               // One of the two extra points is (1,1,1,1)
               
               xsv_ext2 = xsb + 1;
               ysv_ext2 = ysb + 1;
               zsv_ext2 = zsb + 1;
               wsv_ext2 = wsb + 1;
               
               dx_ext2 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_4;
               dy_ext2 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_4;
               dz_ext2 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_4;
               dw_ext2 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_4;

               // Other two points are based on the shared axes.
               final byte c = (byte) (aPoint & bPoint);

               if ((c & 0x01) != 0) {
                  xsv_ext0 = xsb + 2;
                  xsv_ext1 = xsb + 1;
                  dx_ext0 = dx0 - 2 - SimplexNoise.SQUISH_CONSTANT_4_3;
                  dx_ext1 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
               } else {
                  xsv_ext0 = xsv_ext1 = xsb;
                  dx_ext0 = dx_ext1 = dx0 - SimplexNoise.SQUISH_CONSTANT_4_3;
               }

               if ((c & 0x02) != 0) {
                  ysv_ext0 = ysv_ext1 = ysb + 1;
                  dy_ext0 = dy_ext1 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
                  if ((c & 0x01) == 0) {
                     ysv_ext0 += 1;
                     dy_ext0 -= 1;
                  } else {
                     ysv_ext1 += 1;
                     dy_ext1 -= 1;
                  }
               } else {
                  ysv_ext0 = ysv_ext1 = ysb;
                  dy_ext0 = dy_ext1 = dy0
                        - SimplexNoise.SQUISH_CONSTANT_4_3;
               }

               if ((c & 0x04) != 0) {
                  zsv_ext0 = zsv_ext1 = zsb + 1;
                  dz_ext0 = dz_ext1 = dz0 - 1
                        - SimplexNoise.SQUISH_CONSTANT_4_3;
                  if ((c & 0x03) == 0) {
                     zsv_ext0 += 1;
                     dz_ext0 -= 1;
                  } else {
                     zsv_ext1 += 1;
                     dz_ext1 -= 1;
                  }
               } else {
                  zsv_ext0 = zsv_ext1 = zsb;
                  dz_ext0 = dz_ext1 = dz0
                        - SimplexNoise.SQUISH_CONSTANT_4_3;
               }

               if ((c & 0x08) != 0) {
                  wsv_ext0 = wsb + 1;
                  wsv_ext1 = wsb + 2;
                  dw_ext0 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
                  dw_ext1 = dw0 - 2 - SimplexNoise.SQUISH_CONSTANT_4_3;
               } else {
                  wsv_ext0 = wsv_ext1 = wsb;
                  dw_ext0 = dw_ext1 = dw0
                        - SimplexNoise.SQUISH_CONSTANT_4_3;
               }
            }
         } else { 
            // One point on each "side"
            
            byte c1;
            byte c2;
            
            if (aIsBiggerSide) {
               c1 = aPoint;
               c2 = bPoint;
            } else {
               c1 = bPoint;
               c2 = aPoint;
            }

            // Two contributions are the bigger-sided point with each 1
            // replaced with 2.
            if ((c1 & 0x01) != 0) {
               xsv_ext0 = xsb + 2;
               xsv_ext1 = xsb + 1;
               dx_ext0 = dx0 - 2 - SimplexNoise.SQUISH_CONSTANT_4_3;
               dx_ext1 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
            } else {
               xsv_ext0 = xsv_ext1 = xsb;
               dx_ext0 = dx_ext1 = dx0
                     - SimplexNoise.SQUISH_CONSTANT_4_3;
            }

            if ((c1 & 0x02) != 0) {
               ysv_ext0 = ysv_ext1 = ysb + 1;
               dy_ext0 = dy_ext1 = dy0 - 1
                     - SimplexNoise.SQUISH_CONSTANT_4_3;
               if ((c1 & 0x01) == 0) {
                  ysv_ext0 += 1;
                  dy_ext0 -= 1;
               } else {
                  ysv_ext1 += 1;
                  dy_ext1 -= 1;
               }
            } else {
               ysv_ext0 = ysv_ext1 = ysb;
               dy_ext0 = dy_ext1 = dy0
                     - SimplexNoise.SQUISH_CONSTANT_4_3;
            }

            if ((c1 & 0x04) != 0) {
               zsv_ext0 = zsv_ext1 = zsb + 1;
               dz_ext0 = dz_ext1 = dz0 - 1
                     - SimplexNoise.SQUISH_CONSTANT_4_3;
               if ((c1 & 0x03) == 0) {
                  zsv_ext0 += 1;
                  dz_ext0 -= 1;
               } else {
                  zsv_ext1 += 1;
                  dz_ext1 -= 1;
               }
            } else {
               zsv_ext0 = zsv_ext1 = zsb;
               dz_ext0 = dz_ext1 = dz0
                     - SimplexNoise.SQUISH_CONSTANT_4_3;
            }

            if ((c1 & 0x08) != 0) {
               wsv_ext0 = wsb + 1;
               wsv_ext1 = wsb + 2;
               dw_ext0 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
               dw_ext1 = dw0 - 2 - SimplexNoise.SQUISH_CONSTANT_4_3;
            } else {
               wsv_ext0 = wsv_ext1 = wsb;
               dw_ext0 = dw_ext1 = dw0
                     - SimplexNoise.SQUISH_CONSTANT_4_3;
            }

            // One contribution is a permutation of (1,1,1,-1) based on
            // the smaller-sided point
            xsv_ext2 = xsb + 1;
            ysv_ext2 = ysb + 1;
            zsv_ext2 = zsb + 1;
            wsv_ext2 = wsb + 1;
            
            dx_ext2 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
            dy_ext2 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
            dz_ext2 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
            dw_ext2 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
            
            if ((c2 & 0x01) == 0) {
               xsv_ext2 -= 2;
               dx_ext2 += 2;
            } else if ((c2 & 0x02) == 0) {
               ysv_ext2 -= 2;
               dy_ext2 += 2;
            } else if ((c2 & 0x04) == 0) {
               zsv_ext2 -= 2;
               dz_ext2 += 2;
            } else {
               wsv_ext2 -= 2;
               dw_ext2 += 2;
            }
         }

         // Contribution (1,1,1,0)
         final float dx4 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
         final float dy4 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
         final float dz4 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
         final float dw4 = dw0 - SimplexNoise.SQUISH_CONSTANT_4_3;
         float attn4 = 2 - dx4 * dx4 - dy4 * dy4 - dz4 * dz4 - dw4 * dw4;
         if (attn4 > 0) {
            attn4 *= attn4;
            value += attn4 * attn4 * this.extrapolate(
               xsb + 1, ysb + 1, zsb + 1, wsb,
               dx4, dy4, dz4, dw4);
         }

         // Contribution (1,1,0,1)
         final float dx3 = dx4;
         final float dy3 = dy4;
         final float dz3 = dz0 - SimplexNoise.SQUISH_CONSTANT_4_3;
         final float dw3 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_3;
         float attn3 = 2 - dx3 * dx3 - dy3 * dy3 - dz3 * dz3 - dw3 * dw3;
         if (attn3 > 0) {
            attn3 *= attn3;
            value += attn3 * attn3 * this.extrapolate(
               xsb + 1, ysb + 1, zsb, wsb + 1,
               dx3, dy3, dz3, dw3);
         }

         // Contribution (1,0,1,1)
         final float dx2 = dx4;
         final float dy2 = dy0 - SimplexNoise.SQUISH_CONSTANT_4_3;
         final float dz2 = dz4;
         final float dw2 = dw3;
         float attn2 = 2 - dx2 * dx2 - dy2 * dy2 - dz2 * dz2 - dw2 * dw2;
         if (attn2 > 0) {
            attn2 *= attn2;
            value += attn2 * attn2 * this.extrapolate(
               xsb + 1, ysb, zsb + 1, wsb + 1,
               dx2, dy2, dz2, dw2);
         }

         // Contribution (0,1,1,1)
         final float dx1 = dx0 - SimplexNoise.SQUISH_CONSTANT_4_3;
         final float dz1 = dz4;
         final float dy1 = dy4;
         final float dw1 = dw3;
         float attn1 = 2 - dx1 * dx1 - dy1 * dy1 - dz1 * dz1 - dw1 * dw1;
         if (attn1 > 0) {
            attn1 *= attn1;
            value += attn1 * attn1 * this.extrapolate(
               xsb, ysb + 1, zsb + 1, wsb + 1,
               dx1, dy1, dz1, dw1);
         }

         // Contribution (1,1,0,0)
         final float dx5 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dy5 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dz5 = dz0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dw5 = dw0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         float attn5 = 2 - dx5 * dx5 - dy5 * dy5 - dz5 * dz5 - dw5 * dw5;
         if (attn5 > 0) {
            attn5 *= attn5;
            value += attn5 * attn5 * this.extrapolate(
               xsb + 1, ysb + 1, zsb, wsb,
               dx5, dy5, dz5, dw5);
         }

         // Contribution (1,0,1,0)
         final float dx6 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dy6 = dy0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dz6 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dw6 = dw0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         float attn6 = 2 - dx6 * dx6 - dy6 * dy6 - dz6 * dz6 - dw6 * dw6;
         if (attn6 > 0) {
            attn6 *= attn6;
            value += attn6 * attn6 * this.extrapolate(
               xsb + 1, ysb, zsb + 1, wsb,
               dx6, dy6, dz6, dw6);
         }

         // Contribution (1,0,0,1)
         final float dx7 = dx0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dy7 = dy0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dz7 = dz0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dw7 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         float attn7 = 2 - dx7 * dx7 - dy7 * dy7 - dz7 * dz7 - dw7 * dw7;
         if (attn7 > 0) {
            attn7 *= attn7;
            value += attn7 * attn7 * this.extrapolate(
               xsb + 1, ysb, zsb, wsb + 1,
               dx7, dy7, dz7, dw7);
         }

         // Contribution (0,1,1,0)
         final float dx8 = dx0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dy8 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dz8 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dw8 = dw0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         float attn8 = 2 - dx8 * dx8 - dy8 * dy8 - dz8 * dz8 - dw8 * dw8;
         if (attn8 > 0) {
            attn8 *= attn8;
            value += attn8 * attn8 * this.extrapolate(
               xsb, ysb + 1, zsb + 1, wsb,
               dx8, dy8, dz8, dw8);
         }

         // Contribution (0,1,0,1)
         final float dx9 = dx0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dy9 = dy0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dz9 = dz0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dw9 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         float attn9 = 2 - dx9 * dx9 - dy9 * dy9 - dz9 * dz9 - dw9 * dw9;
         if (attn9 > 0) {
            attn9 *= attn9;
            value += attn9 * attn9 * this.extrapolate(
               xsb, ysb + 1, zsb, wsb + 1,
               dx9, dy9, dz9, dw9);
         }

         // Contribution (0,0,1,1)
         final float dx10 = dx0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dy10 = dy0 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dz10 = dz0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         final float dw10 = dw0 - 1 - SimplexNoise.SQUISH_CONSTANT_4_2;
         float attn10 = 2 - dx10 * dx10 - dy10 * dy10 - dz10 * dz10
               - dw10 * dw10;
         if (attn10 > 0) {
            attn10 *= attn10;
            value += attn10 * attn10 * this.extrapolate(
                     xsb, ysb, zsb + 1, wsb + 1,
                     dx10, dy10, dz10, dw10);
         }
      }

      // First extra vertex
      float attn_ext0 = 2 - dx_ext0 * dx_ext0 - dy_ext0 * dy_ext0
            - dz_ext0 * dz_ext0 - dw_ext0 * dw_ext0;
      if (attn_ext0 > 0) {
         attn_ext0 *= attn_ext0;
         value += attn_ext0 * attn_ext0 * this.extrapolate(
            xsv_ext0, ysv_ext0, zsv_ext0, wsv_ext0,
            dx_ext0, dy_ext0, dz_ext0, dw_ext0);
      }

      // Second extra vertex
      float attn_ext1 = 2 - dx_ext1 * dx_ext1 - dy_ext1 * dy_ext1
            - dz_ext1 * dz_ext1 - dw_ext1 * dw_ext1;
      if (attn_ext1 > 0) {
         attn_ext1 *= attn_ext1;
         value += attn_ext1 * attn_ext1 * this.extrapolate(xsv_ext1, ysv_ext1,
               zsv_ext1, wsv_ext1, dx_ext1, dy_ext1, dz_ext1, dw_ext1);
      }

      // Third extra vertex
      float attn_ext2 = 2 - dx_ext2 * dx_ext2 - dy_ext2 * dy_ext2
            - dz_ext2 * dz_ext2 - dw_ext2 * dw_ext2;
      if (attn_ext2 > 0) {
         attn_ext2 *= attn_ext2;
         value += attn_ext2 * attn_ext2 * this.extrapolate(xsv_ext2, ysv_ext2,
               zsv_ext2, wsv_ext2, dx_ext2, dy_ext2, dz_ext2, dw_ext2);
      }

      return value * SimplexNoise.INV_NORM_CONST_4;
   }

   public float eval ( final Vec2 v ) {

      return this.eval(v.x, v.y);
   }

   public float eval ( final Vec3 v ) {

      return this.eval(v.x, v.y, v.z);
   }

   public float eval ( final Vec4 v ) {

      return this.eval(v.x, v.y, v.z, v.w);
   }

   protected float extrapolate (
         final int xsb, final int ysb,
         final float dx, final float dy ) {

      final int index = this.perm[this.perm[xsb & 0xff] + ysb & 0xff] & 0x0e;

      return SimplexNoise.gradients2D[index] * dx
            + SimplexNoise.gradients2D[index + 1] * dy;
   }

   protected float extrapolate (
         final int xsb, final int ysb, final int zsb,
         final float dx, final float dy, final float dz ) {

      final int index = this.permGradIndex3D[this.perm[this.perm[xsb & 0xff]
            + ysb & 0xff] + zsb & 0xff];

      return SimplexNoise.gradients3D[index] * dx
            + SimplexNoise.gradients3D[index + 1] * dy
            + SimplexNoise.gradients3D[index + 2] * dz;
   }

   protected float extrapolate (
         final int xsb, final int ysb, final int zsb, final int wsb,
         final float dx, final float dy, final float dz, final float dw ) {

      final int index = this.perm[this.perm[this.perm[this.perm[xsb & 0xff]
            + ysb & 0xff] + zsb & 0xff] + wsb & 0xff] & 0xfc;

      return SimplexNoise.gradients4D[index] * dx
            + SimplexNoise.gradients4D[index + 1] * dy
            + SimplexNoise.gradients4D[index + 2] * dz
            + SimplexNoise.gradients4D[index + 3] * dw;
   }
}
