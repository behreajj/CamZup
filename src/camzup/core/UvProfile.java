package camzup.core;

/**
 * Organizes texture coordinate profiles for various meshes.
 */
public interface UvProfile {

   /**
    * Texture coordinate patterns for an arc.
    */
   public enum Arc {

      /**
       * Pattern follows the rectangular bounding area around the arc.
       */
      BOUNDS,

      /**
       * Pattern curves with the arc path, proportioned to the arc-length.
       */
      CLIP,

      /**
       * Pattern curves with the arc path. Covers the entire UV space.
       */
      STRETCH
   }

   /**
    * Texture coordinate patterns for a cube.
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
