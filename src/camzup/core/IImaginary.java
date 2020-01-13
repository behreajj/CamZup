package camzup.core;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Maintains consistent behavior for complex numbers.
 */
public interface IImaginary
      extends IUtils, Cloneable, Iterable < Float >, Serializable {

   /**
    * Simulates bracket subscript access in an array.
    *
    * @param index
    *           the index
    * @return the component at that index
    */
   float get ( final int index );

   /**
    * Returns an iterator for the complex number, which allows
    * its components to be accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   Iterator < Float > iterator ();

   /**
    * Gets the number of components held by the complex number.
    *
    * @return the size
    */
   int length ();

   /**
    * Returns a float array containing the complex number's
    * components.
    *
    * @return the array
    */
   float[] toArray ();
}
