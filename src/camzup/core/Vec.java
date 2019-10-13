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
   protected final int size;

   /**
    * The default constructor.
    *
    * @param size
    *           the number of components
    */
   protected Vec ( final int size ) {

      this.size = size;
   }

   /**
    * Gets the number of components held by the vector.
    * 
    * @return the size
    */
   @Override
   public int size () {

      return this.size;
   }
}
