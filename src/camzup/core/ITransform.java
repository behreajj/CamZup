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

      /**
       * Returns the reverse of a given order. For example, RTS
       * returns STR.
       *
       * @param order
       *           the order
       * @return the reverse order
       */
      public static Order reverse ( final Order order ) {

         switch (order) {
            case RST:
               return Order.TSR;
            case RTS:
               return Order.STR;
            case SRT:
               return Order.TRS;
            case STR:
               return Order.RTS;
            case TRS:
               return Order.SRT;
            case TSR:
            default:
               return Order.RST;
         }
      }

      /**
       * The longform name displayed in the console.
       */
      private final String name;

      /**
       * The default constrctor
       *
       * @param name
       *           the longform name
       */
      private Order ( final String name ) {

         this.name = name;
      }

      /**
       * Returns a string representation of the transform order.
       */
      @Override
      public String toString () {

         return this.name;
      }
   }
}
