package camzup.core;

/**
 * Command that describes how to <code>transform</code> a shape in the
 * attribute of a node in scalable vector graphics (SVG).
 */
public enum SvgTransformCmd {

   /**
    * Uses a 2x3 affine transform matrix. The data are ordered as (0.) right
    * x, (1.) right y, (2.) up x, (3.) up y, (4.) translation x, (5.)
    * translation y.
    */
   MATRIX ( "matrix", 6, 6 ),

   /**
    * A rotate command takes 1-3 data points. If the pivot x and y are not
    * defined, they are assumed to be (0, 0).
    */
   ROTATE ( "rotate", 1, 3 ),

   /**
    * A scale command takes 1-2 data points. If height is not defined, it is
    * assumed to be equal to width.
    */
   SCALE ( "scale", 1, 2 ),

   /**
    * A skew x command takes 1 data point.
    */
   SKEW_X ( "skewx", 1, 1 ),

   /**
    * A skew y command takes 1 data point.
    */
   SKEW_Y ( "skewy", 1, 1 ),

   /**
    * A translate command takes 1-2 data points. If y is not defined, it is
    * assumed to be zero.
    */
   TRANSLATE ( "translate", 1, 2 );

   /**
    * The string code.
    */
   private final String code;

   /**
    * The maximum data count.
    */
   private final int maxDataCount;

   /**
    * The minimum data count.
    */
   private final int minDataCount;

   /**
    * The enumeration constant constructor.
    *
    * @param code         the string code
    * @param minDataCount the minimum data for the command
    * @param maxDataCount the maximum data for the command
    */
   SvgTransformCmd ( final String code, final int minDataCount,
      final int maxDataCount ) {

      this.code = code;
      this.minDataCount = minDataCount;
      this.maxDataCount = maxDataCount;
   }

   /**
    * Gets the String representing the command in a transform attribute.
    *
    * @return the code
    */
   public String getCode ( ) { return this.code; }

   /**
    * Gets the maximum data count.
    *
    * @return the maxDataCount
    */
   public int getMaxDataCount ( ) { return this.maxDataCount; }

   /**
    * Gets the minimum data count.
    *
    * @return the minDataCount
    */
   public int getMinDataCount ( ) { return this.minDataCount; }

   /**
    * Returns a transform command from a String.
    * 
    * @param s the string
    * 
    * @return the command
    */
   public static SvgTransformCmd fromString ( final String s ) {

      final String val = s.toLowerCase().trim();
      switch ( val ) {
         case "matrix":
            return SvgTransformCmd.MATRIX;

         case "rotate":
            return SvgTransformCmd.ROTATE;

         case "scale":
            return SvgTransformCmd.SCALE;

         case "skewx":
            return SvgTransformCmd.SKEW_X;

         case "skewy":
            return SvgTransformCmd.SKEW_Y;

         case "translate":
            return SvgTransformCmd.TRANSLATE;

         default:
            return SvgTransformCmd.MATRIX;
      }
   }

}
