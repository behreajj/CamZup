package camzup.core;

import java.util.Arrays;

/**
 * A convenience for working in the Processing IDE: extends
 * {@link java.util.Random} so an additional import does not need to be
 * added. Its name is truncated to avoid collisions.
 */
public class Rng extends java.util.Random implements IUtils {

   /**
    * The random number generator's seed.
    */
   private long publicSeed;

   /**
    * The default constructor. Sets the seed to the system's current time in
    * milliseconds.
    *
    * @see System#currentTimeMillis()
    */
   public Rng ( ) { this(System.currentTimeMillis()); }

   /**
    * Creates a new generator with a seed value.
    *
    * @param seed the seed
    */
   public Rng ( final long seed ) {

      super(seed);
      this.publicSeed = seed;
   }

   /**
    * Tests this random number generator for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Rng ) obj);
   }

   /**
    * Gets the public seed of the random number generator.
    *
    * @return the seed
    */
   public long getSeed ( ) { return this.publicSeed; }

   /**
    * Returns a hash code for this random number generator based on its seed.
    *
    * @return the hash code
    */
   @Override
   public int hashCode ( ) { return ( int ) this.publicSeed; }

   /**
    * Returns a pseudo-random, uniformly distributed integer value between 0
    * (inclusive) and the specified value (exclusive), drawn from this random
    * number generator's sequence. Overrides parent random's functionality to
    * clamp the bound to a positive non-zero lower bound rather than throwing
    * an exception.
    *
    * @param bound the upper bound
    *
    * @return the pseudo-random integer
    *
    * @see java.util.Random#nextInt(int)
    */
   @Override
   public int nextInt ( final int bound ) {

      final int vb = bound < 1 ? 1 : bound;

      int r = this.next(31);
      final int m = vb - 1;
      if ( ( vb & m ) == 0 ) {
         r = ( int ) ( vb * ( long ) r >> 31 );
      } else {
         for ( int u = r; u - ( r = u % vb ) + m < 0; u = this.next(31) ) {

         }
      }
      return r;
   }

   /**
    * Finds an array of random numbers that sum to 1.0 . Due to floating point
    * precision, the sum of the numbers may be approximate.
    *
    * @param count the number of elements
    *
    * @return the array
    */
   public float[] segment ( final int count ) {

      return this.segment(count, 1.0f);
   }

   /**
    * Finds an array of random numbers that sum to a value. Due to floating
    * point precision, the sum of the numbers may be approximate.<br>
    * <br>
    * The function first calculates a series of segment end points on a number
    * line from 0.0 to 1.0 . The result array contains the difference between
    * an end point and its preceding neighbor multiplied by the sum.
    *
    * @param count the number of elements
    * @param sum   the sum
    *
    * @return the array
    */
   public float[] segment ( final int count, final float sum ) {

      /*
       * Doesn't have uniform distribution. final float[] result = new
       * float[count]; float trueSum = 0.0f; for ( int i = 0; i < count; ++i ) {
       * trueSum += result[i] = this.nextFloat(); } final float scalar =
       * Utils.div(sum, trueSum); for ( int i = 0; i < count; ++i ) { result[i]
       * *= scalar; }
       */

      final float[] x = new float[count + 2];
      x[0] = 0.0f;
      x[count] = 1.0f;
      for ( int i = 1; i < count; ++i ) {
         x[i] = this.nextFloat();
      }

      Arrays.sort(x);

      final float[] result = new float[count];
      for ( int i = count - 1; i > -1; --i ) {
         result[i] = sum * ( x[i + 2] - x[i + 1] );
      }
      return result;
   }

   /**
    * Sets the seed of this random number generator.
    *
    * @param seed the seed
    */
   @Override
   public void setSeed ( final long seed ) {

      super.setSeed(seed);
      this.publicSeed = seed;
   }

   /**
    * Returns this random number generator's seed as a hash string.
    */
   @Override
   public String toString ( ) {

      final StringBuilder sb = new StringBuilder(64);
      sb.append("{ seed: ");
      sb.append(Long.toString(this.publicSeed));
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Returns a double precision real number between 0.0 and the upper bound.
    *
    * @param upper the upper bound
    *
    * @return the random number
    */
   public double uniform ( final double upper ) {

      return upper * this.nextDouble();
   }

   /**
    * Returns a double precision real number between the lower and upper
    * bound.
    *
    * @param lower the lower bound
    * @param upper the upper bound
    *
    * @return the random number
    */
   public double uniform ( final double lower, final double upper ) {

      final double r = this.nextDouble();
      return ( 1.0d - r ) * lower + r * upper;
   }

   /**
    * Returns a single precision real number between 0.0 and the upper bound.
    *
    * @param upper the upper bound
    *
    * @return the random number
    */
   public float uniform ( final float upper ) {

      return upper * this.nextFloat();
   }

   /**
    * Returns a single precision real number within the lower and upper bound.
    *
    * @param lower the lower bound
    * @param upper the upper bound
    *
    * @return the random number
    */
   public float uniform ( final float lower, final float upper ) {

      final float r = this.nextFloat();
      return ( 1.0f - r ) * lower + r * upper;
   }

   /**
    * Returns an integer within the lower and upper bound: lower bound
    * inclusive, upper bound exclusive.
    *
    * @param lower the lower bound
    * @param upper the upper bound
    *
    * @return the random number
    */
   public int uniform ( final int lower, final int upper ) {

      final float r = this.nextFloat();
      return Utils.floorToInt( ( 1.0f - r ) * lower + r * upper);
   }

   /**
    * Tests this random number generator for equivalence with another.
    *
    * @param rng the other generator
    *
    * @return the equivalence
    */
   protected boolean equals ( final Rng rng ) {

      return this.publicSeed == rng.publicSeed;
   }

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = 259933270658058430L;

}
