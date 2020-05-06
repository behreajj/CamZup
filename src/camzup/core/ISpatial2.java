package camzup.core;

/**
 * Maintains consistent behavior for 2D animated objects with transforms.
 */
public interface ISpatial2 {

   /**
    * Moves this object by a vector.
    *
    * @param dir the vector
    *
    * @return this object
    */
   ISpatial2 moveBy ( final Vec2 dir );

   /**
    * Moves this object to a location.
    *
    * @param locNew the location
    *
    * @return this object
    */
   ISpatial2 moveTo ( final Vec2 locNew );

   /**
    * Moves this object to a location over a step in time.
    *
    * @param locNew the location
    * @param step   the step
    *
    * @return this object
    */
   ISpatial2 moveTo ( final Vec2 locNew, final float step );

   /**
    * Rotates this object to an angle.
    *
    * @param rotNew the rotation
    *
    * @return this object
    */
   ISpatial2 rotateTo ( final float rotNew );

   /**
    * Rotates this object to an angle over a step in time.
    *
    * @param rotNew the angle
    * @param step   the step
    *
    * @return this object
    */
   ISpatial2 rotateTo ( final float rotNew, final float step );

   /**
    * Rotates this object by an angle around the z axis.
    *
    * @param radians the angle
    *
    * @return this object
    */
   ISpatial2 rotateZ ( final float radians );

}
