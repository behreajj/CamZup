package camzup.core;

/**
 * Generates a Voronoi noise in two to four dimensions.
 */
public class Voronoi extends Generative {

   /**
    * Discourage overriding with a private constructor.
    */
   private Voronoi ( ) {}

   /**
    * Generates 2D Voronoi noise. Returns the minimum Euclidean distance; the
    * Voronoi point is stored in an output vector.
    *
    * @param coord  the coordinate
    * @param seed   the seed
    * @param scale  the scale
    * @param target the output vector
    *
    * @return the distance
    */
   public static float eval ( final Vec2 coord, final int seed,
      final float scale, final Vec2 target ) {

      // TODO: Is it worth investing in a smooth voronoi function which reduces
      // discontinuities? Search for this, it was addressed by Bourke or Quilez.

      /*
       * As many functions as is reasonable are inlined for performance purposes
       * (both to avoid function call overhead and to avoid creating new Vec3
       * s).
       */

      if ( scale == 0.0f ) {
         target.reset();
         return 0.0f;
      }

      final float scaleInv = 1.0f / scale;
      final float xScaled = coord.x * scaleInv;
      final float yScaled = coord.y * scaleInv;

      final float xCell = xScaled > 0.0f ? ( int ) xScaled : xScaled < 0.0f
         ? ( int ) xScaled - 1.0f : 0.0f;
      final float yCell = yScaled > 0.0f ? ( int ) yScaled : yScaled < 0.0f
         ? ( int ) yScaled - 1.0f : 0.0f;

      final float xLocal = xScaled - xCell;
      final float yLocal = yScaled - yCell;

      float minDistSq = Float.MAX_VALUE;
      float xHsh = 0.0f;
      float yHsh = 0.0f;

      for ( float j = -1.0f; j < 2.0f; ++j ) {
         final float sumy = yCell + j;
         final float sumysq = sumy * sumy;
         final int vybit = Float.floatToIntBits(sumy);

         for ( float i = -1.0f; i < 2.0f; ++i ) {
            final float sumx = xCell + i;
            final float sumxsq = sumx * sumx;

            /*
             * Calculate an offset step for the vector. This has to be done with
             * all three sums within the for loop.
             */
            final float st = Utils.sqrtUnchecked(sumxsq + sumysq)
               * Simplex.STEP_2;

            /* Create a hash for the x component. */
            final int ahsh = ( IUtils.MUL_BASE ^ Float.floatToIntBits(sumx
               + st) ) * IUtils.HASH_MUL ^ vybit;

            /* Create a hash for the y component. */
            final int bhsh = ( IUtils.MUL_BASE ^ Float.floatToIntBits(sumx) )
               * IUtils.HASH_MUL ^ Float.floatToIntBits(sumy + st);

            /*
             * Create a random vector in the range [0.0, 1.0] . Add the cell
             * offset.
             */
            xHsh = i + Float.intBitsToFloat(Generative.hash(ahsh, seed, 0)
               & 0x007fffff | 0x3f800000) - 1.0f;
            yHsh = j + Float.intBitsToFloat(Generative.hash(bhsh, seed, 0)
               & 0x007fffff | 0x3f800000) - 1.0f;

            /*
             * Find the Euclidean distance between the local coordinate and the
             * random point.
             */
            final float xDist = xLocal - xHsh;
            final float yDist = yLocal - yHsh;
            final float distSq = xDist * xDist + yDist * yDist;

            /* Reassign minimums. */
            if ( distSq < minDistSq ) {
               minDistSq = distSq;
               target.x = xHsh;
               target.y = yHsh;
            }
         }
      }

      target.x += xCell;
      target.y += yCell;

      target.x *= scale;
      target.y *= scale;

      return Utils.sqrtUnchecked(minDistSq);
   }

   /**
    * Generates 3D voronoi noise. Returns the minimum Euclidean distance; the
    * voronoi point is stored in an output vector.
    *
    * @param coord  the coordinate
    * @param seed   the seed
    * @param scale  the scale
    * @param target the output vector
    *
    * @return the distance
    */
   public static float eval ( final Vec3 coord, final int seed,
      final float scale, final Vec3 target ) {

      /*
       * As many functions as is reasonable are inlined for performance purposes
       * (both to avoid function call overhead and to avoid creating new Vec3
       * s).
       */

      if ( scale == 0.0f ) {
         target.reset();
         return 0.0f;
      }

      final float scaleInv = 1.0f / scale;
      final float xScaled = coord.x * scaleInv;
      final float yScaled = coord.y * scaleInv;
      final float zScaled = coord.z * scaleInv;

      final float xCell = xScaled > 0.0f ? ( int ) xScaled : xScaled < 0.0f
         ? ( int ) xScaled - 1.0f : 0.0f;
      final float yCell = yScaled > 0.0f ? ( int ) yScaled : yScaled < 0.0f
         ? ( int ) yScaled - 1.0f : 0.0f;
      final float zCell = zScaled > 0.0f ? ( int ) zScaled : zScaled < 0.0f
         ? ( int ) zScaled - 1.0f : 0.0f;

      final float xLocal = xScaled - xCell;
      final float yLocal = yScaled - yCell;
      final float zLocal = zScaled - zCell;

      float minDistSq = Float.MAX_VALUE;
      float xHsh = 0.0f;
      float yHsh = 0.0f;
      float zHsh = 0.0f;

      for ( float k = -1.0f; k < 2.0f; ++k ) {
         final float sumz = zCell + k;
         final float sumzsq = sumz * sumz;
         final int vzbit = Float.floatToIntBits(sumz);

         for ( float j = -1.0f; j < 2.0f; ++j ) {
            final float sumy = yCell + j;
            final float sumysq = sumy * sumy;
            final int vybit = Float.floatToIntBits(sumy);

            for ( float i = -1.0f; i < 2.0f; ++i ) {
               final float sumx = xCell + i;
               final float sumxsq = sumx * sumx;
               final int mulvx = IUtils.MUL_BASE ^ Float.floatToIntBits(sumx);

               /*
                * Calculate an offset step for the vector. This has to be done
                * with all three sums within the for loop.
                */
               final float st = Simplex.STEP_3 * Utils.sqrtUnchecked(sumxsq
                  + sumysq + sumzsq);

               /* Create a hash for the x component. */
               final int ahsh = ( ( IUtils.MUL_BASE ^ Float.floatToIntBits(sumx
                  + st) ) * IUtils.HASH_MUL ^ vybit ) * IUtils.HASH_MUL ^ vzbit;

               /* Create a hash for the y component. */
               final int bhsh = ( mulvx * IUtils.HASH_MUL ^ Float
                  .floatToIntBits(sumy + st) ) * IUtils.HASH_MUL ^ vzbit;

               /* Create a hash for the z component. */
               final int chsh = ( mulvx * IUtils.HASH_MUL ^ vybit )
                  * IUtils.HASH_MUL ^ Float.floatToIntBits(sumz + st);

               /*
                * Create a random vector in the range [0.0, 1.0] . Add the cell
                * offset.
                */
               xHsh = i + Float.intBitsToFloat(Generative.hash(ahsh, seed, 0)
                  & 0x007fffff | 0x3f800000) - 1.0f;
               yHsh = j + Float.intBitsToFloat(Generative.hash(bhsh, seed, 0)
                  & 0x007fffff | 0x3f800000) - 1.0f;
               zHsh = k + Float.intBitsToFloat(Generative.hash(chsh, seed, 0)
                  & 0x007fffff | 0x3f800000) - 1.0f;

               /*
                * Find the Euclidean distance between the local coordinate and
                * the random point.
                */
               final float xDist = xLocal - xHsh;
               final float yDist = yLocal - yHsh;
               final float zDist = zLocal - zHsh;
               final float distSq = xDist * xDist + yDist * yDist + zDist
                  * zDist;

               /* Reassign minimums. */
               if ( distSq < minDistSq ) {
                  minDistSq = distSq;
                  target.x = xHsh;
                  target.y = yHsh;
                  target.z = zHsh;
               }
            }
         }
      }

      target.x += xCell;
      target.y += yCell;
      target.z += zCell;

      target.x *= scale;
      target.y *= scale;
      target.z *= scale;

      return Utils.sqrtUnchecked(minDistSq);
   }

   /**
    * Generates 4D voronoi noise. Returns the minimum Euclidean distance; the
    * voronoi point is stored in an output vector.
    *
    * @param coord  the coordinate
    * @param seed   the seed
    * @param scale  the scale
    * @param target the output vector
    *
    * @return the distance
    */
   public static float eval ( final Vec4 coord, final int seed,
      final float scale, final Vec4 target ) {

      if ( scale == 0.0f ) {
         target.reset();
         return 0.0f;
      }

      final float scaleInv = 1.0f / scale;
      final float xScaled = coord.x * scaleInv;
      final float yScaled = coord.y * scaleInv;
      final float zScaled = coord.z * scaleInv;
      final float wScaled = coord.w * scaleInv;

      final float xCell = xScaled > 0.0f ? ( int ) xScaled : xScaled < 0.0f
         ? ( int ) xScaled - 1.0f : 0.0f;
      final float yCell = yScaled > 0.0f ? ( int ) yScaled : yScaled < 0.0f
         ? ( int ) yScaled - 1.0f : 0.0f;
      final float zCell = zScaled > 0.0f ? ( int ) zScaled : zScaled < 0.0f
         ? ( int ) zScaled - 1.0f : 0.0f;
      final float wCell = wScaled > 0.0f ? ( int ) wScaled : wScaled < 0.0f
         ? ( int ) wScaled - 1.0f : 0.0f;

      final float xLocal = xScaled - xCell;
      final float yLocal = yScaled - yCell;
      final float zLocal = zScaled - zCell;
      final float wLocal = wScaled - wCell;

      float minDist = Float.MAX_VALUE;
      float xHsh = 0.0f;
      float yHsh = 0.0f;
      float zHsh = 0.0f;
      float wHsh = 0.0f;

      for ( float m = -1.0f; m < 2.0f; ++m ) {
         final float sumw = wCell + m;
         final float sumwsq = sumw * sumw;
         final int vwbit = Float.floatToIntBits(sumw);

         for ( float k = -1.0f; k < 2.0f; ++k ) {
            final float sumz = zCell + k;
            final float sumzsq = sumz * sumz;
            final int vzbit = Float.floatToIntBits(sumz);

            for ( float j = -1.0f; j < 2.0f; ++j ) {
               final float sumy = yCell + j;
               final float sumysq = sumy * sumy;
               final int vybit = Float.floatToIntBits(sumy);

               for ( float i = -1.0f; i < 2.0f; ++i ) {
                  final float sumx = xCell + i;
                  final float sumxsq = sumx * sumx;
                  final int mulvx = IUtils.MUL_BASE ^ Float.floatToIntBits(
                     sumx);

                  /* Calculate an offset step for the vector. */
                  final float st = Simplex.STEP_4 * Utils.sqrtUnchecked(sumxsq
                     + sumysq + sumzsq + sumwsq);

                  /* Create a hash for the x component. */
                  final int ahsh = ( ( ( IUtils.MUL_BASE ^ Float.floatToIntBits(
                     sumx + st) ) * IUtils.HASH_MUL ^ vybit ) * IUtils.HASH_MUL
                     ^ vzbit ) * IUtils.HASH_MUL ^ vwbit;

                  /* Create a hash for the y component. */
                  final int bhsh = ( ( mulvx * IUtils.HASH_MUL ^ Float
                     .floatToIntBits(sumy + st) ) * IUtils.HASH_MUL ^ vzbit )
                     * IUtils.HASH_MUL ^ vwbit;

                  /* Create a hash for the z component. */
                  final int chsh = ( ( mulvx * IUtils.HASH_MUL ^ vybit )
                     * IUtils.HASH_MUL ^ Float.floatToIntBits(sumz + st) )
                     * IUtils.HASH_MUL ^ vwbit;

                  /* Create a hash for the w component. */
                  final int dhsh = ( ( mulvx * IUtils.HASH_MUL ^ vybit )
                     * IUtils.HASH_MUL ^ vzbit ) * IUtils.HASH_MUL ^ Float
                        .floatToIntBits(sumw + st);

                  /*
                   * Create a random vector in the range [0.0, 1.0] . Add the
                   * cell offset.
                   */
                  xHsh = i + Float.intBitsToFloat(Generative.hash(ahsh, seed, 0)
                     & 0x007fffff | 0x3f800000) - 1.0f;
                  yHsh = j + Float.intBitsToFloat(Generative.hash(bhsh, seed, 0)
                     & 0x007fffff | 0x3f800000) - 1.0f;
                  zHsh = k + Float.intBitsToFloat(Generative.hash(chsh, seed, 0)
                     & 0x007fffff | 0x3f800000) - 1.0f;
                  wHsh = m + Float.intBitsToFloat(Generative.hash(dhsh, seed, 0)
                     & 0x007fffff | 0x3f800000) - 1.0f;

                  /*
                   * Find the Euclidean distance between the local coordinate
                   * and the random point.
                   */
                  final float xDist = xLocal - xHsh;
                  final float yDist = yLocal - yHsh;
                  final float zDist = zLocal - zHsh;
                  final float wDist = wLocal - wHsh;
                  final float dist = xDist * xDist + yDist * yDist + zDist
                     * zDist + wDist * wDist;

                  /* Reassign minimums. */
                  if ( dist < minDist ) {
                     minDist = dist;
                     target.x = xHsh;
                     target.y = yHsh;
                     target.z = zHsh;
                     target.w = wHsh;
                  }
               }
            }
         }
      }

      target.x += xCell;
      target.y += yCell;
      target.z += zCell;
      target.w += wCell;

      target.x *= scale;
      target.y *= scale;
      target.z *= scale;
      target.w *= scale;

      return Utils.sqrtUnchecked(minDist);
   }

}
