package camzup.core;

import java.io.Serializable;
import java.util.Iterator;

public interface IMatrix
      extends IUtils, Cloneable, Iterable < Float >, Serializable {

   float get ( final int index );

   float get ( final int i, final int j );

   @Override
   Iterator < Float > iterator ();

   int size ();

   float[] toArray ();
}
