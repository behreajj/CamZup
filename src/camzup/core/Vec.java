package camzup.core;

/**
 * An abstract class to serve as a parent for vectors;
 * stores the size of the vector.
 */
public abstract class Vec implements IVec {

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = -1470281552689148893L;

   /**
    * Number of components held by the vector.
    */
   protected final int length;

   /**
    * The default constructor.
    *
    * @param length
    *           the number of components
    */
   protected Vec ( final int length ) {

      this.length = length;
   }

   /**
    * Gets the number of components held by the vector.
    *
    * @return the length
    */
   @Override
   public int length () {

      return this.length;
   }
}
