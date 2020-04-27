package camzup.core;

import java.util.Iterator;

/**
 * Maintains consistent behavior between matrices of different dimensions.
 */
public interface IMatrix extends IUtils, Cloneable, Iterable < Float > {

   /**
    * Returns a matrix element by index, as though the matrix were a 1D array.
    *
    * @param index the index
    *
    * @return the value
    */
   float getElm ( final int index );

   /**
    * Retrieves a matrix element by two indices, as though the matrix were a
    * 2D array.
    *
    * @param i the row
    * @param j the column
    *
    * @return the value
    */
   float getElm ( final int i, final int j );

   /**
    * Returns an iterator for the matrix, which allows its components to be
    * accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   Iterator < Float > iterator ( );

   /**
    * Returns the number of elements in the matrix.
    *
    * @return the length
    */
   int length ( );

   /**
    * Converts the matrix to a 1D array.
    *
    * @return the array.
    */
   float[] toArray ( );

}
