package camzup.core;

/**
 * Maintains consistent behavior between entities.
 */
public interface IEntity extends IUtils {

   /**
    * Default capacity for array lists of data that a given entity
    * implementation may hold, e.g., of curves or meshes.
    */
   int DEFAULT_CAPACITY = 4;

}