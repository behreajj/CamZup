package camzup.core;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Maintains consistent behavior for vectors of different sizes.
 */
public interface IVec
    extends IUtils, Cloneable, Iterable < Float >, Serializable {

  /**
   * Simulates bracket subscript access in an array.
   *
   * @param index the index
   * @return the component at that index
   */
  float get ( final int index );

  /**
   * Returns an iterator for this vector, which allows its components to
   * be accessed in an enhanced for-loop.
   *
   * @return the iterator
   */
  @Override
  Iterator < Float > iterator ( );

  /**
   * Gets the number of components held by the vector.
   *
   * @return the length
   */
  int length ( );

  /**
   * Returns a float array containing this vector's components.
   *
   * @return the array
   */
  float[] toArray ( );
}
