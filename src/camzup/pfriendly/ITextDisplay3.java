package camzup.pfriendly;

import camzup.core.Utils;
import camzup.core.Vec3;

/**
 * Maintains consistent behavior between 3D renderers which display text to the
 * screen.
 */
public interface ITextDisplay3 extends ITextDisplay {

   /**
    * Displays a character at a location.
    *
    * @param c the character
    * @param x the x coordinate
    * @param y the y coordinate
    */
   @Override
   default void text (
      final char c,
      final float x,
      final float y ) {

      this.text(c, x, y, 0.0f);
   }

   /**
    * Displays a character at a location.
    *
    * @param c the character
    * @param x the x coordinate
    * @param y the y coordinate
    * @param z the z coordinate
    */
   void text (
      final char c,
      final float x,
      final float y,
      final float z );

   /**
    * Displays a character at a location.
    *
    * @param c the character
    * @param v the location
    */
   default void text ( final char c, final Vec3 v ) {

      this.text(c, v.x, v.y, v.z);
   }

   /**
    * Displays an array of characters as text at a location.
    *
    * @param chars the character array
    * @param start the start index, inclusive
    * @param stop  the stop index, exclusive
    * @param x     the x coordinate
    * @param y     the y coordinate
    */
   @Override
   default void text (
      final char[] chars,
      final int start,
      final int stop,
      final float x,
      final float y ) {

      this.text(chars, start, stop, x, y, 0.0f);
   }

   /**
    * Displays an array of characters as text at a location.
    *
    * @param chars the character array
    * @param start the start index, inclusive
    * @param stop  the stop index, exclusive
    * @param x     the x coordinate
    * @param y     the y coordinate
    * @param z     the z coordinate
    */
   void text (
      final char[] chars,
      final int start,
      final int stop,
      final float x,
      final float y,
      final float z );

   /**
    * Displays an array of characters as text at a location.
    *
    * @param chars the character array
    * @param start the start index, inclusive
    * @param stop  the stop index, exclusive
    * @param v     the location
    */
   default void text (
      final char[] chars,
      final int start,
      final int stop,
      final Vec3 v ) {

      this.text(chars, 0, chars.length, v.x, v.y, v.z);
   }

   /**
    * Displays an array of characters as text at a location.
    *
    * @param chars the character array
    * @param v     the location
    */
   default void text (
      final char[] chars,
      final Vec3 v ) {

      this.text(chars, 0, chars.length, v.x, v.y, v.z);
   }

   /**
    * Displays a real number as text at a location.
    *
    * @param real the real number
    * @param x    the x coordinate
    * @param y    the y coordinate
    * @param z    the z coordinate
    *
    * @see Utils#toFixed(float, int)
    */
   default void text (
      final float real,
      final float x,
      final float y,
      final float z ) {

      this.text(Utils.toFixed(real, 4), x, y, z);
   }

   /**
    * Displays a number as text at a location. Registers up to four decimal
    * places.
    *
    * @param real the real number
    * @param v    the location
    *
    * @see Utils#toFixed(float, int)
    */
   default void text (
      final float real,
      final Vec3 v ) {

      this.text(Utils.toFixed(real, 4), v.x, v.y, v.z);
   }

   /**
    * Displays an integer as text at a location. Registers up to four decimal
    * places.
    *
    * @param i the integer
    * @param x the x coordinate
    * @param y the y coordinate
    * @param z the z coordinate
    */
   default void text (
      final int i,
      final float x,
      final float y,
      final float z ) {

      this.text(i, x, y, z);
   }

   /**
    * Displays an integer as text at a location.
    *
    * @param i the integer
    * @param v the location
    *
    * @see Utils#toFixed(float, int)
    */
   default void text (
      final int i,
      final Vec3 v ) {

      this.text(i, v.x, v.y, v.z);
   }

   /**
    * Displays a string of text at a location.
    *
    * @param str the string
    * @param x   the x coordinate
    * @param y   the y coordinate
    * @param z   the z coordinate
    */
   default void text (
      final String str,
      final float x,
      final float y,
      final float z ) {

      this.text(str, x, y, z);
   }

   /**
    * Displays a string at a location.
    *
    * @param str the string
    * @param v   the location
    *
    * @see Utils#toFixed(float, int)
    */
   default void text (
      final String str,
      final Vec3 v ) {

      this.text(str, v.x, v.y, v.z);
   }

}
