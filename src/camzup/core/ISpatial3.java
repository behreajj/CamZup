package camzup.core;

/**
 * Maintains consistent behavior for 3D animated objects that move in
 * space.
 */
public interface ISpatial3 {

   /**
    * Moves this object by a vector.
    *
    * @param dir the vector
    *
    * @return this object
    */
   ISpatial3 moveBy ( final Vec3 dir );

   /**
    * Moves this object to a location.
    *
    * @param locNew the location
    *
    * @return this object
    */
   ISpatial3 moveTo ( final Vec3 locNew );

   /**
    * Moves this object to a location over a step in time.
    *
    * @param locNew the location
    * @param step   the step
    *
    * @return this object
    */
   ISpatial3 moveTo ( final Vec3 locNew, final float step );

}
