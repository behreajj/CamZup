package camzup.core;

/**
 * An abstract class to serve as a parent for transforms.
 */
public abstract class Transform implements ITransform {

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = -8374280920178305152L;

   /**
    * The default constructor.
    */
   protected Transform () {

   }

   /**
    * Updates the local axes of the transform based on its
    * rotation.
    */
   protected abstract void updateAxes ();
}
