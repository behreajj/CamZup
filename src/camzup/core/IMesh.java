package camzup.core;

/**
 * Maintains consistent behavior for meshes of different
 * dimensions.
 */
public interface IMesh extends Cloneable, IEntityData {

   /**
    * Default count of sectors in a regular convex polygon, so
    * as to approximate a circle.
    */
   int DEFAULT_CIRCLE_SECTORS = 32;
}
