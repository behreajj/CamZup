package camzup.core;

/**
 * Facilitates simplex noise and voronoi patterns via hashing functions.
 */
public abstract class Generative {

   /**
    * Only extensions of Noise should be able to construct it.
    */
   protected Generative ( ) {}

   /**
    * A default seed set to the system current time in milliseconds.
    */
   public static final int DEFAULT_SEED;

   static {
      DEFAULT_SEED = ( int ) System.currentTimeMillis();
   }

   /**
    * Returns a signed pseudo-random number in the range [-1.0, 1.0] given a
    * vector input and a seed, based on the vector's hash code.
    *
    * @param v    the input vector
    * @param seed the seed
    *
    * @return a random number
    *
    * @see Vec2#hashCode()
    * @see Float#intBitsToFloat(int)
    * @see Simplex#hash(int, int, int)
    */
   public static float hash ( final Vec2 v, final int seed ) {

      return ( Float.intBitsToFloat(Generative.hash(v.hashCode(), seed, 0)
         & 0x007fffff | 0x3f800000) - 1.0f ) * 2.0f - 1.0f;
   }

   /**
    * Returns a vector in the range [-1.0, 1.0] given a vector input and a
    * seed.
    *
    * @param v      the input vector
    * @param seed   the result
    * @param target the output vector
    *
    * @return the vector
    */
   public static Vec2 hash ( final Vec2 v, final int seed, final Vec2 target ) {

      final float st = Simplex.STEP_2 * Utils.sqrtUnchecked(v.x * v.x + v.y
         * v.y);

      final int ahash = ( IUtils.MUL_BASE ^ Float.floatToIntBits(v.x + st) )
         * IUtils.HASH_MUL ^ Float.floatToIntBits(v.y);

      final int bhash = ( IUtils.MUL_BASE ^ Float.floatToIntBits(v.x) )
         * IUtils.HASH_MUL ^ Float.floatToIntBits(v.y + st);

      /* @formatter:off */
      return target.set(
         ( Float.intBitsToFloat(Generative.hash(ahash, seed, 0)
            & 0x007fffff | 0x3f800000) - 1.0f ) * 2.0f - 1.0f,
         ( Float.intBitsToFloat(Generative.hash(bhash, seed, 0)
            & 0x007fffff | 0x3f800000) - 1.0f ) * 2.0f - 1.0f);
      /* @formatter:on */
   }

   /**
    * Returns a signed pseudo-random number in the range [-1.0, 1.0] given a
    * vector input and a seed, based on the vector's hash code.
    *
    * @param v    the input vector
    * @param seed the seed
    *
    * @return a random number
    *
    * @see Vec3#hashCode()
    * @see Float#intBitsToFloat(int)
    * @see Simplex#hash(int, int, int)
    */
   public static float hash ( final Vec3 v, final int seed ) {

      return ( Float.intBitsToFloat(Generative.hash(v.hashCode(), seed, 0)
         & 0x007fffff | 0x3f800000) - 1.0f ) * 2.0f - 1.0f;
   }

   /**
    * Returns a vector in the range [-1.0, 1.0] given a vector input and a
    * seed.
    *
    * @param v      the input vector
    * @param seed   the result
    * @param target the output vector
    *
    * @return the vector
    */
   public static Vec3 hash ( final Vec3 v, final int seed, final Vec3 target ) {

      final float st = Simplex.STEP_3 * Utils.sqrtUnchecked(v.x * v.x + v.y
         * v.y + v.z * v.z);
      final int mulvx = IUtils.MUL_BASE ^ Float.floatToIntBits(v.x);
      final int vybit = Float.floatToIntBits(v.y);
      final int vzbit = Float.floatToIntBits(v.z);

      final int ahash = ( ( IUtils.MUL_BASE ^ Float.floatToIntBits(v.x + st) )
         * IUtils.HASH_MUL ^ vybit ) * IUtils.HASH_MUL ^ vzbit;

      final int bhash = ( mulvx * IUtils.HASH_MUL ^ Float.floatToIntBits(v.y
         + st) ) * IUtils.HASH_MUL ^ vzbit;

      final int chash = ( mulvx * IUtils.HASH_MUL ^ vybit ) * IUtils.HASH_MUL
         ^ Float.floatToIntBits(v.z + st);

      /* @formatter:off */
      return target.set(
         ( Float.intBitsToFloat(Generative.hash(ahash, seed, 0)
            & 0x007fffff | 0x3f800000) - 1.0f ) * 2.0f - 1.0f,

         ( Float.intBitsToFloat(Generative.hash(bhash, seed, 0)
            & 0x007fffff | 0x3f800000) - 1.0f ) * 2.0f - 1.0f,

         ( Float.intBitsToFloat(Generative.hash(chash, seed, 0)
            & 0x007fffff | 0x3f800000) - 1.0f ) * 2.0f - 1.0f);
      /* @formatter:on */
   }

   /**
    * Returns a signed pseudo-random number in the range [-1.0, 1.0] given a
    * vector input and a seed, based on the vector's hash code.
    *
    * @param v    the input vector
    * @param seed the seed
    *
    * @return a random number
    *
    * @see Vec4#hashCode()
    * @see Float#intBitsToFloat(int)
    * @see Simplex#hash(int, int, int)
    */
   public static float hash ( final Vec4 v, final int seed ) {

      return ( Float.intBitsToFloat(Generative.hash(v.hashCode(), seed, 0)
         & 0x007fffff | 0x3f800000) - 1.0f ) * 2.0f - 1.0f;
   }

   /**
    * Returns a vector in the range [-1.0, 1.0] given a vector input and a
    * seed.
    *
    * @param v      the input vector
    * @param seed   the result
    * @param target the output vector
    *
    * @return the vector
    */
   public static Vec4 hash ( final Vec4 v, final int seed, final Vec4 target ) {

      final float st = Simplex.STEP_4 * Utils.sqrtUnchecked(v.x * v.x + v.y
         * v.y + v.z * v.z + v.w * v.w);
      final int mulvx = IUtils.MUL_BASE ^ Float.floatToIntBits(v.x);
      final int vybit = Float.floatToIntBits(v.y);
      final int vzbit = Float.floatToIntBits(v.z);
      final int vwbit = Float.floatToIntBits(v.w);

      final int ahash = ( ( ( IUtils.MUL_BASE ^ Float.floatToIntBits(v.x + st) )
         * IUtils.HASH_MUL ^ vybit ) * IUtils.HASH_MUL ^ vzbit )
         * IUtils.HASH_MUL ^ vwbit;

      final int bhash = ( ( mulvx * IUtils.HASH_MUL ^ Float.floatToIntBits(v.y
         + st) ) * IUtils.HASH_MUL ^ vzbit ) * IUtils.HASH_MUL ^ vwbit;

      final int chash = ( ( mulvx * IUtils.HASH_MUL ^ vybit ) * IUtils.HASH_MUL
         ^ Float.floatToIntBits(v.z + st) ) * IUtils.HASH_MUL ^ vwbit;

      final int dhash = ( ( mulvx * IUtils.HASH_MUL ^ vybit ) * IUtils.HASH_MUL
         ^ vzbit ) * IUtils.HASH_MUL ^ Float.floatToIntBits(v.w + st);

      /* @formatter:off */
      return target.set(
         ( Float.intBitsToFloat(Generative.hash(ahash, seed, 0)
            & 0x007fffff | 0x3f800000) - 1.0f ) * 2.0f - 1.0f,

         ( Float.intBitsToFloat(Generative.hash(bhash, seed, 0)
            & 0x007fffff | 0x3f800000) - 1.0f ) * 2.0f - 1.0f,

         ( Float.intBitsToFloat(Generative.hash(chash, seed, 0)
            & 0x007fffff | 0x3f800000) - 1.0f ) * 2.0f - 1.0f,

         ( Float.intBitsToFloat(Generative.hash(dhash, seed, 0)
            & 0x007fffff | 0x3f800000) - 1.0f ) * 2.0f - 1.0f);
      /* @formatter:on */
   }

   /**
    * A helper function to the gradient functions. Performs a series of
    * bit-shifting operations to create a hash.
    *
    * @param a first input
    * @param b second input
    * @param c third input
    *
    * @return the hash
    *
    * @author Bob Jenkins
    */
   protected static int hash ( int a, int b, int c ) {

      // QUERY: Is there any way to make a 2d version of this with fewer
      // operations? Maybe instead of assuming c = 0, assume a = 0?

      c ^= b;
      c -= b << 0xe | b >> 0x20 - 0xe;
      a ^= c;
      a -= c << 0xb | c >> 0x20 - 0xb;
      b ^= a;
      b -= a << 0x19 | a >> 0x20 - 0x19;
      c ^= b;
      c -= b << 0x10 | b >> 0x20 - 0x10;
      a ^= c;
      a -= c << 0x4 | c >> 0x20 - 0x4;
      b ^= a;
      b -= a << 0xe | a >> 0x20 - 0xe;
      c ^= b;
      c -= b << 0x18 | b >> 0x20 - 0x18;
      return c;
   }

}