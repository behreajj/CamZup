package camzup.core;

/**
 * Organizes LAB Blend modes.
 */
public interface BlendMode {
   /**
    * A and B chroma axes blend mode operations.
    */
   public enum AB {
      /* Adds over chroma to under. */
      ADD,

      /* Adds over and under chroma, then halves the sum. */
      AVERAGE,

      /* Blends over and under layer chroma per alpha. */
      BLEND,

      /* Adopts the over layer chroma. */
      OVER,

      /* Subtracts the over layer chroma from the under. */
      SUBTRACT,

      /* Adopts the under layer chroma. */
      UNDER
   }

   /**
    * Alpha blend mode operations.
    */
   public enum Alpha {

      /* Default blending operation. */
      BLEND,

      /* Adopts the greater alpha. */
      MAX,

      /* Adopts the lesser alpha. */
      MIN,

      /* Multiplies the over and under layer alpha. */
      MULTIPLY,

      /* Adopts the over layer alpha. */
      OVER,

      /* Adopts the under layer alpha. */
      UNDER
   }

   /**
    * Chroma blend mode operations.
    */
   public enum C {

      /* Blends over and under layer chroma per alpha. */
      BLEND,

      /* Adopts the over layer chroma. */
      OVER,

      /* Adopts the under layer chroma. */
      UNDER
   }

   /**
    * Hue blend mode operations. Gray colors are exception cases.
    */
   public enum H {

      /* Blends from under to over in the counter clockwise hue direction. */
      CCW,

      /* Blends from under to over in the clockwise hue direction. */
      CW,

      /* Blends from under to over in the nearest hue direction. */
      NEAR,

      /* Adopts the over layer hue. */
      OVER,

      /* Adopts the under layer hue. */
      UNDER
   }

   /**
    * Lightness blend mode operations.
    */
   public enum L {

      /* Adds over lightness to under. */
      ADD,

      /* Adds over and under lightness, then halves the sum. */
      AVERAGE,

      /* Blends over and under layer lightness per alpha. */
      BLEND,

      /* Divides under lightness by over. */
      DIVIDE,

      /* Multiplies over and under lightness. */
      MULTIPLY,

      /* Adopts the over layer lightness. */
      OVER,

      /*
       * Multiplies the inverse of over and of under lightness, then inverts the
       * product.
       */
      SCREEN,

      /* Subtracts the over layer lightness from the under. */
      SUBTRACT,

      /* Adopts the under layer lightness. */
      UNDER
   }

   /**
    * Color space in which to mix color.
    */
   public enum Space {

      /* Lightness a and b */
      LAB,

      /* Lightness chroma hue */
      LCH
   }

}
