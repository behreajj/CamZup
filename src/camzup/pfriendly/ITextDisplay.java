package camzup.pfriendly;

import camzup.core.Utils;
import camzup.core.Vec2;

import processing.core.PApplet;

/**
 * Maintains consistent behavior between renderers which display text to the
 * screen.
 */
public interface ITextDisplay {

   /**
    * Displays a character at a location.
    *
    * @param c the character
    * @param x the x coordinate
    * @param y the y coordinate
    */
   void text (
      final char c,
      final float x,
      final float y );

   /**
    * Displays a character at a location.
    *
    * @param c the character
    * @param v the location
    */
   default void text ( final char c, final Vec2 v ) {

      this.text(c, v.x, v.y);
   }

   /**
    * Displays an array of characters as text at a location.
    *
    * @param chars the character array
    * @param x     the x coordinate
    * @param y     the y coordinate
    */
   default void text (
      final char[] chars,
      final float x,
      final float y ) {

      this.text(chars, 0, chars.length, x, y);
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
   void text (
      final char[] chars,
      final int start,
      final int stop,
      final float x,
      final float y );

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
      final Vec2 v ) {

      this.text(chars, 0, chars.length, v.x, v.y);
   }

   /**
    * Displays an array of characters as text at a location.
    *
    * @param chars the character array
    * @param v     the location
    */
   default void text (
      final char[] chars,
      final Vec2 v ) {

      this.text(chars, v.x, v.y);
   }

   /**
    * Displays a real number as text at a location. Registers up to four decimal
    * places.
    *
    * @param real the real number
    * @param x    the x coordinate
    * @param y    the y coordinate
    *
    * @see Utils#toFixed(float, int)
    */
   default void text (
      final float real,
      final float x,
      final float y ) {

      this.text(Utils.toFixed(real, 4), x, y);
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
      final Vec2 v ) {

      this.text(Utils.toFixed(real, 4), v.x, v.y);
   }

   /**
    * Displays an integer as text at a location.
    *
    * @param i the integer
    * @param v the location
    *
    * @see Utils#toFixed(float, int)
    */
   default void text ( final int i, final Vec2 v ) {

      this.text(i, v.x, v.y);
   }

   /**
    * Displays a string at a location.
    *
    * @param str the string
    * @param x   the x coordinate
    * @param y   the y coordinate
    */
   default void text (
      final String str,
      final float x,
      final float y ) {

      this.text(str.toCharArray(), x, y);
   }

   /**
    * Displays a string at a location. This version of text is not supported, so
    * only x1 and y1 are used.
    *
    * @param str the string
    * @param x1  the x coordinate
    * @param y1  the y coordinate
    * @param x2  the second x coordinate
    * @param y2  the second y coordinate
    */
   default void text (
      final String str,
      final float x1, final float y1,
      final float x2, final float y2 ) {

      PApplet.showMissingWarning("text");
      this.text(str, x1, y1);
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
      final Vec2 v ) {

      this.text(str, v.x, v.y);
   }

}
