package camzup;

import processing.core.PApplet;

/**
 * The main class of this library. This is not needed to use the library
 * and is for development and debugging only.
 */
public class CamZup {

   /**
    * The PApplet referenced by this class.
    */
   public final PApplet parent;

   /**
    * Constructs a new instance of this library with the PApplet as a
    * reference.
    *
    * @param parent the parent applet
    */
   public CamZup ( final PApplet parent ) { this.parent = parent; }

   /**
    * Returns a string representation of the CamZup class.
    *
    * @return the string
    */
   @Override
   public String toString ( ) {

      final StringBuilder sb = new StringBuilder(64);
      sb.append("{ version: ");
      sb.append(CamZup.VERSION);
      sb.append(", parent: ");
      sb.append(this.parent);
      sb.append(" }");
      return sb.toString();
   }

   /**
    * The library's current version.
    */
   public static final String VERSION = "##library.prettyVersion##";

   /**
    * The main function.
    *
    * @param args the string of arguments
    */
   public static void main ( final String[] args ) {

      // CurveEntity2 ce2 = ParserSvg2.parse("data/hexGrid.svg");
      // String pyCd = ce2.toBlenderCode();
      // System.out.println("");
      // System.out.println(pyCd);

//      char[] arr = "87352.1234".toCharArray();
//      System.out.println(charArrToFloat(arr));
   }

   static float charArrToFloat ( char[] arr ) {

      return charArrToFloat(arr, 0, arr.length);
   }

   public static float charArrToFloat ( char[] arr, int start, int end ) {
      
      int dpidx = end;
      boolean negate = false;
      for ( int i = start; i < end; ++i ) {
         char c = arr[i];
         if ( c == '-' ) {
            negate = true;
         } else if ( c == '.' ) {
            dpidx = i;
         }
      }
      
      if ( negate ) --dpidx;

      float exponent = 1.0f;
      for ( int k = 1; k < dpidx; ++k ) { exponent *= 10.0f; }

      float result = 0.0f;
      for ( int j = start; j < end; ++j ) {
         int digit = arr[j] - '0';
         if ( digit > -1 && digit < 10 ) {
            result += exponent * digit;
            exponent *= 0.1f;
         }
      }
      
      return negate ? -result : result;
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
