package camzup.core;

/**
 * The order in which affine transformations -- translation,
 * rotation, scale -- are applied.
 */
public enum TransformOrder {

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
   public static TransformOrder reverse ( final TransformOrder order ) {

      switch (order) {
         case RST:
            return TransformOrder.TSR;
         case RTS:
            return TransformOrder.STR;
         case SRT:
            return TransformOrder.TRS;
         case STR:
            return TransformOrder.RTS;
         case TRS:
            return TransformOrder.SRT;
         case TSR:
         default:
            return TransformOrder.RST;
      }
   }

   /**
    * The longform name displayed in the console.
    */
   private String name;

   /**
    * The default constrctor
    *
    * @param name
    *           the longform name
    */
   private TransformOrder ( final String name ) {

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
