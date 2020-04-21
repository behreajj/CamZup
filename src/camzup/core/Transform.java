package camzup.core;

/**
 * An abstract class to serve as a parent for transforms.
 */
public abstract class Transform implements ITransform {

   /**
    * The default constructor.
    */
   protected Transform ( ) {}

   /**
    * Updates the local axes of the transform based on its rotation.
    */
   protected abstract void updateAxes ( );

}
