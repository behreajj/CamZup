package camzup.core;

/**
 * Coordinates texture coordinate profiles for various meshes.
 */
public interface UvProfile {

   /**
    * Texture coordinate patterns when making a cube.
    */
   public enum Cube {

      /**
       * Cross pattern that wraps around cube.
       */
      CROSS,

      /**
       * Each cube face repeats the same square texture.
       */
      PER_FACE
   }

}
