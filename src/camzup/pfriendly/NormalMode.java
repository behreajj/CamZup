package camzup.pfriendly;

import processing.core.PGraphics;

/**
 * Duplicates and clarifies the normal mode options available to
 * Processing's OpenGL renderers (P2D and P3D). The integer value of each
 * is intended to match with integer constants
 * {@link PGraphics#NORMAL_MODE_AUTO}, {@link PGraphics#NORMAL_MODE_SHAPE}
 * or {@link PGraphics#NORMAL_MODE_VERTEX}. The enum's ordinal should not
 * be used.
 */
public enum NormalMode {

   /**
    * Automatic, {@link PGraphics#NORMAL_MODE_AUTO}.
    */
   AUTO ( 0 ),

   /**
    * Per shape (or face), {@link PGraphics#NORMAL_MODE_SHAPE}.
    */
   SHAPE ( 1 ),

   /**
    * Per vertex, {@link PGraphics#NORMAL_MODE_VERTEX}.
    */
   VERTEX ( 2 );

   /**
    * The integer code of the constant.
    */
   private final int val;

   /**
    * The enumeration constructor.
    *
    * @param val the integer value
    */
   private NormalMode ( final int val ) { this.val = val; }

   /**
    * Gets the integer code of the constant.
    *
    * @return the integer
    */
   public int getVal ( ) { return this.val; }

   /**
    * Get a normal mode from an integer value.
    *
    * @param i the integer
    *
    * @return the constant
    */
   public static NormalMode fromValue ( final int i ) {

      switch ( i ) {
         case 2:
            return VERTEX;

         case 1:
            return SHAPE;

         case 0:
         default:
            return AUTO;
      }
   }

}
