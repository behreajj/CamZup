package camzup.core;

/**
 * Maintains consistent behavior between 3D objects with volume.
 */
public interface IVolume3 extends IVolume {

   /**
    * Scales the object by a non-uniform scalar.
    *
    * @param scalar the scalar
    *
    * @return the object
    */
   IVolume3 scaleBy ( final Vec3 scalar );

   /**
    * Scales the object to a non-uniform size.
    *
    * @param scalar the size
    *
    * @return the object
    */
   IVolume3 scaleTo ( final Vec3 scalar );

   /**
    * Eases the object to a scale by a step over time.
    *
    * @param scalar the scalar
    * @param step   the step
    *
    * @return this object
    */
   IVolume3 scaleTo ( final Vec3 scalar, final float step );

}
