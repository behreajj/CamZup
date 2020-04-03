package camzup.core;

/**
 * Maintains consistent behavior for 3D animated objects with transforms.
 */
public interface ISpatial3 {

   /**
    * Moves this object by a vector.
    *
    * @param dir the vector
    *
    * @return this object
    */
   @Chainable
   ISpatial3 moveBy ( final Vec3 dir );

   /**
    * Moves this object to a location.
    *
    * @param locNew the location
    *
    * @return this object
    */
   @Chainable
   ISpatial3 moveTo ( final Vec3 locNew );

   /**
    * Moves this object to a location over a step in time.
    *
    * @param locNew the location
    * @param step   the step
    *
    * @return this object
    */
   @Chainable
   ISpatial3 moveTo ( final Vec3 locNew, final float step );

   /**
    * Rotates the object by an axis and angle in radians.
    *
    * @param radians the angle in radians
    * @param axis    the axis
    *
    * @return this object
    */
   @Chainable
   ISpatial3 rotateBy ( final float radians, final Vec3 axis );

   /**
    * Rotates this object by a quaternion.
    *
    * @param rot the quaternion
    *
    * @return this object
    */
   @Chainable
   ISpatial3 rotateBy ( final Quaternion rot );

   /**
    * Rotates this object to a quaternion.
    *
    * @param rotNew the new rotation
    *
    * @return this object
    */
   @Chainable
   ISpatial3 rotateTo ( final Quaternion rotNew );

   /**
    * Rotates this object to a quaternion over a step in time.
    *
    * @param rotNew the new rotation
    * @param step   the step
    *
    * @return this object
    */
   @Chainable
   ISpatial3 rotateTo ( final Quaternion rotNew, final float step );

   /**
    * Rotates this object by an angle around the x axis.
    *
    * @param radians the angle
    *
    * @return this object
    */
   @Chainable
   ISpatial3 rotateX ( final float radians );

   /**
    * Rotates this object by an angle around the y axis.
    *
    * @param radians the angle
    *
    * @return this object
    */
   @Chainable
   ISpatial3 rotateY ( final float radians );

   /**
    * Rotates this object by an angle around the z axis.
    *
    * @param radians the angle
    *
    * @return this object
    */
   @Chainable
   ISpatial3 rotateZ ( final float radians );

}
