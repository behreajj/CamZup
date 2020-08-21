package camzup.core;

/**
 * Maintains consistent behavior for 2D animated objects that move in
 * space.
 */
public interface ISpatial2 {

   /**
    * Gets the spatial object's location.
    *
    * @param target the output vector
    *
    * @return the location
    */
   Vec2 getLocation ( final Vec2 target );

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

}
