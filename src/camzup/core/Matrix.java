package camzup.core;

/**
 * An abstract class to serve as a parent for matrices; stores the size of the
 * matrix.
 */
public abstract class Matrix implements IMatrix {

   /**
    * The number of elements in the matrix.
    */
   protected final int length;

   /**
    * Constructs a matrix with a specified size.
    *
    * @param length the length
    */
   protected Matrix ( final int length ) { this.length = length; }

   /**
    * Returns the number of elements in the matrix.
    *
    * @return the size
    */
   @Override
   public int length ( ) { return this.length; }

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = -6995930494443674601L;

}
