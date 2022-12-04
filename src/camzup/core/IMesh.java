package camzup.core;

/**
 * Maintains consistent behavior for meshes of different dimensions.
 */
public interface IMesh extends IEntityData {

   /**
    * Default count of sectors in a regular convex polygon, so as to
    * approximate a circle.
    */
   int DEFAULT_CIRCLE_SECTORS = 32;

   /**
    * Default oculus for rings, sqrt(2.0) / 4.0 , approximately
    * {@value IMesh#DEFAULT_OCULUS} .
    */
   float DEFAULT_OCULUS = 0.35355338f;

}
