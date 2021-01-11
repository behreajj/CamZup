package camzup.core;

/**
 * Command that describes how to render a path in the <code>d</code>
 * attribute of a <code>path</code> node in scalable vector graphics (SVG).
 */
public enum PathCommand {

   /**
    * Arc absolute ('A').
    */
   ARC_TO_ABS ( 'A', false, 7 ),

   /**
    * Arc relative ('a').
    */
   ARC_TO_REL ( 'a', true, 7 ),

   /**
    * Close path ('Z').
    */
   CLOSE_PATH ( 'Z', false, 0 ),

   /**
    * Cubic Bezier Curve absolute ('C').
    */
   CUBIC_TO_ABS ( 'C', false, 6 ),

   /**
    * Cubic Bezier Curve relative ('c').
    */
   CUBIC_TO_REL ( 'c', true, 6 ),

   /**
    * Horizontal line absolute ('H').
    */
   HORIZ_ABS ( 'H', false, 1 ),

   /**
    * Horizontal line relative ('h').
    */
   HORIZ_REL ( 'h', true, 1 ),

   /**
    * Line to absolute ('L').
    */
   LINE_TO_ABS ( 'L', false, 2 ),

   /**
    * Line to relative ('l).
    */
   LINE_TO_REL ( 'l', true, 2 ),

   /**
    * Move to absolute ('M'). Data following the initial coordinate pair are
    * treated implicitly as line-to commands.
    */
   MOVE_TO_ABS ( 'M', false, 2 ),

   /**
    * Move to relative ('m'). Data following the initial coordinate pair are
    * treated implicitly as line-to commands.
    */
   MOVE_TO_REL ( 'm', true, 2 ),

   /**
    * Quadratic Bezier curve absolute ('Q').
    */
   QUADRATIC_TO_ABS ( 'Q', false, 4 ),

   /**
    * Quadratic Bezier curve relative ('q').
    */
   QUADRATIC_TO_REL ( 'q', true, 4 ),

   /**
    * Reflect cubic Bezier curve absolute ('S').
    */
   REFLECT_CUBIC_ABS ( 'S', false, 4 ),

   /**
    * Reflect cubic Bezier curve relative ('s').
    */
   REFLECT_CUBIC_REL ( 's', true, 4 ),

   /**
    * Reflect quadratic Bezier curve absolute ('T').
    */
   REFLECT_QUADRATIC_ABS ( 'T', false, 2 ),

   /**
    * Reflect quadratic Bezier curve relative ('t').
    */
   REFLECT_QUADRATIC_REL ( 't', true, 2 ),

   /**
    * Vertical line absolute ('V').
    */
   VERT_ABS ( 'V', false, 1 ),

   /**
    * Vertical line to relative ('v').
    */
   VERT_REL ( 'v', true, 1 );

   /**
    * The single character code.
    */
   private final char code;

   /**
    * Number of data per a given code.
    */
   private final int dataCount;

   /**
    * Is the command in absolute coordinates, or relative to previously
    * specified coordinates.
    */
   private final boolean isRelative;

   /**
    * The enumeration constant constructor.
    *
    * @param code       the character code
    * @param isRelative is the command relative
    * @param dataCount  the parameter count
    */
   PathCommand ( final char code, final boolean isRelative,
      final int dataCount ) {

      this.code = code;
      this.isRelative = isRelative;
      this.dataCount = dataCount;
   }

   /**
    * Gets the command's character code.
    *
    * @return the character
    */
   public char getCode ( ) { return this.code; }

   /**
    * Gets the number of parameters.
    *
    * @return the parameter number.
    */
   public int getDataCount ( ) { return this.dataCount; }

   /**
    * Is the command relative (true) or absolute (false).
    *
    * @return the boolean
    */
   public boolean isRelative ( ) { return this.isRelative; }

   /**
    * Returns a string representation of the path command.
    */
   @Override
   public String toString ( ) {

      // return String.valueOf(this.code);
      return super.toString();
   }

   /**
    * Returns a path command given a character. In cases where the character
    * is not a command, returns close path by default.
    *
    * @param c the character
    *
    * @return the path command
    */
   public static PathCommand fromChar ( final char c ) {

      /* @formatter:off */
      switch ( c ) {
         case 'A': return PathCommand.ARC_TO_ABS;
         case 'C': return PathCommand.CUBIC_TO_ABS;
         case 'H': return PathCommand.HORIZ_ABS;
         case 'L': return PathCommand.LINE_TO_ABS;
         case 'M': return PathCommand.MOVE_TO_ABS;
         case 'Q': return PathCommand.QUADRATIC_TO_ABS;
         case 'S': return PathCommand.REFLECT_CUBIC_ABS;
         case 'T': return PathCommand.REFLECT_QUADRATIC_ABS;
         case 'V': return PathCommand.VERT_ABS;
         // case 'Z': return PathCommand.CLOSE_PATH;

         case 'a': return PathCommand.ARC_TO_REL;
         case 'c': return PathCommand.CUBIC_TO_REL;
         case 'h': return PathCommand.HORIZ_REL;
         case 'l': return PathCommand.LINE_TO_REL;
         case 'm': return PathCommand.MOVE_TO_REL;
         case 'q': return PathCommand.QUADRATIC_TO_REL;
         case 's': return PathCommand.REFLECT_CUBIC_REL;
         case 't': return PathCommand.REFLECT_QUADRATIC_REL;
         case 'v': return PathCommand.VERT_REL;
         // case 'z': return PathCommand.CLOSE_PATH;
         default: return PathCommand.CLOSE_PATH;
      }
      /* @formatter:on */
   }

}