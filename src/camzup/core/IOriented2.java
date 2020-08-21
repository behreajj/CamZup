package camzup.core;

/**
 * Maintains consistent behavior for 2D objects that can be rotated.
 */
public interface IOriented2 {

   /**
    * Gets the object's rotation.
    *
    * @return the rotation
    */
   float getRotation ( );

   /**
    * Rotates this object to an angle.
    *
    * @param rotNew the rotation
    *
    * @return this object
    */
   IOriented2 rotateTo ( final float rotNew );

   /**
    * Rotates this object to an angle over a step in time.
    *
    * @param rotNew the angle
    * @param step   the step
    *
    * @return this object
    */
   IOriented2 rotateTo ( final float rotNew, final float step );

   /**
    * Rotates this object by an angle around the z axis.
    *
    * @param radians the angle
    *
    * @return this object
    */
   IOriented2 rotateZ ( final float radians );

}
