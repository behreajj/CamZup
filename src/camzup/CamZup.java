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

   public static float charArrToFloat ( final char[] arr ) {

      final int len = arr.length;
      int dpidx = len;
      boolean negate = false;
      for ( int i = 0; i < len; ++i ) {
         final char c = arr[i];
         if ( c == '-' ) {
            negate = true;
         } else if ( c == '.' ) { dpidx = i; }
      }

      if ( negate ) { --dpidx; }

      float exponent = 1.0f;
      for ( int k = 1; k < dpidx; ++k ) { exponent *= 10.0f; }

      float result = 0.0f;
      for ( int j = 0; j < len; ++j ) {
         final int digit = arr[j] - '0';
         if ( digit > -1 && digit < 10 ) {
            result += exponent * digit;
            exponent *= 0.1f;
         }
      }

      return negate ? -result : result;
   }

   /**
    * The main function.
    *
    * @param args the string of arguments
    */
   public static void main ( final String[] args ) {

      // String str = "5 ,-68.875, 725.26+8001.26";
      // char[] chars = str.toCharArray();
      // ArrayList < String > segs = new ArrayList <>();
      // segmentChars(chars, 0, chars.length, segs);
      //
      // for ( String seg : segs ) { System.out.println(seg); }
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
