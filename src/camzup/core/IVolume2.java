package camzup.core;

/**
 * Maintains consistent behavior between 2D objects with volume.
 */
public interface IVolume2 extends IVolume {

   /**
    * Scales the object by a non-uniform scalar.
    *
    * @param scalar the scalar
    *
    * @return the object
    */
   IVolume2 scaleBy ( final Vec2 scalar );

   /**
    * Scales the object to a non-uniform size.
    *
    * @param scalar the size
    *
    * @return the object
    */
   IVolume2 scaleTo ( final Vec2 scalar );

   /**
    * Eases the object to a scale by a step over time.
    *
    * @param scalar the scalar
    * @param step   the step
    *
    * @return this object
    */
   IVolume2 scaleTo ( final Vec2 scalar, final float step );

}
