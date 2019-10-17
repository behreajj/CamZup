package camzup.core;

import java.io.Serializable;

/**
 * Maintains consistent behavior for transforms.
 */
public interface ITransform extends IUtils, Cloneable, Serializable {

   /**
    * The order in which affine transformations -- translation,
    * rotation, scale -- are applied.
    */
   public enum Order {

      /**
       * Rotation, Scale, Translation
       */
      RST ( "Rotation, Scale, Translation" ),

      /**
       * Rotation, Translation, Scale
       */
      RTS ( "Rotation, Translation, Scale" ),

      /**
       * Scale, Rotation, Translation
       */
      SRT ( "Scale, Rotation, Translation" ),

      /**
       * Scale, Translation, Rotation
       */
      STR ( "Scale, Translation, Rotation" ),

      /**
       * Translation, Rotation, Scale
       */
      TRS ( "Translation, Rotation, Scale" ),

      /**
       * Translation, Scale, Rotation
       */
      TSR ( "Translation, Scale, Rotation" );

      private final String name;

      private Order ( final String name ) {

         this.name = name;
      }

      @Override
      public String toString () {

         return this.name;
      }
   }
}
