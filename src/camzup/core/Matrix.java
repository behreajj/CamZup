package camzup.core;

/**
 * An abstract class to serve as a parent for matrices;
 * stores the size of the matrix.
 */
public abstract class Matrix implements IMatrix {

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = -6995930494443674601L;

   /**
    * The number of elements in the matrix.
    */
   protected final int size;

   /**
    * Constructs a matrix with a specified size.
    * 
    * @param size
    *           the size
    */
   protected Matrix ( final int size ) {

      this.size = size;
   }

   /**
    * Returns the number of elements in the matrix.
    * 
    * @return the size
    */
   @Override
   public int size () {

      return this.size;
   }
}
