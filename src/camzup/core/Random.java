package camzup.core;

/**
 * An extension of java.util.Random . This is mostly for convenience in the
 * Processing IDE.
 */
public class Random extends java.util.Random implements IUtils {

   /**
    * The default constructor. Sets the seed to the system's current time in
    * milliseconds.
    *
    * @see System#currentTimeMillis()
    */
   public Random ( ) { this(System.currentTimeMillis()); }

   /**
    * Creates a new generator with a seed value.
    *
    * @param seed the seed
    */
   public Random ( final long seed ) { super(seed); }

   /**
    * Returns this random number generator's seed as a hash string.
    */
   @Override
   public String toString ( ) {

      return this.getClass().getSimpleName();
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
    * Returns a double precision real number between the lower and upper bound.
    *
    * @param lower the lower bound
    * @param upper the upper bound
    *
    * @return the random number
    */
   public double uniform (
      final double lower,
      final double upper ) {

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
   public float uniform (
      final float lower,
      final float upper ) {

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
   public int uniform (
      final int lower,
      final int upper ) {

      final float r = this.nextFloat();
      return Utils.floorToInt( ( 1.0f - r ) * lower + r * upper);
   }

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = 259933270658058430L;

}
