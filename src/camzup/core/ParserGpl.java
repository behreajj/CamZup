package camzup.core;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * A <a href="http://gimp.org/">GIMP</a> (GNU Image Manipulation Program)
 * color palette (.gpl) parser class.
 */
public abstract class ParserGpl {

   /**
    * Private constructor for abstract class.
    */
   private ParserGpl ( ) {}

   /**
    * Parses a .gpl file containing a GIMP palette.
    *
    * @param in the buffered reader
    *
    * @return the gradient
    */
   public static Color[] load ( final BufferedReader in ) {

      Color[] result = new Color[0];

      try {

         try {

            final TreeMap < Integer, Color > clrs = new TreeMap <>();
            final Pattern ptrn = Pattern.compile("\\s+");

            int i = 0;
            for ( String ln = in.readLine(); ln != null; ln = in.readLine() ) {
               final String lnlc = ln.trim().toLowerCase();

               /* Implicitly support JASC-PAL, which is similar to GPL. */
               if ( lnlc.equals("gimp palette") || lnlc.indexOf("name:") > -1
                  || lnlc.indexOf("columns:") > -1 || lnlc.indexOf('#') == 0
                  || lnlc.equals("jasc-pal") || lnlc.equals("0100") ) {
                  /* Skip. */
               } else {
                  final String[] tokens = ptrn.split(lnlc, 0);
                  final int len = tokens.length;
                  if ( len > 2 ) {

                     /*
                      * In a GPL, tokens 0, 1 and 2 are the RGB channels; token
                      * 3 is the optional name; token 4 is the optional index.
                      * We don't care whether the start index is 1 or 0, so long
                      * as the palette colors are in order.
                      */
                     final int idx = len > 4 ? Integer.parseInt(tokens[4], 10)
                        - 1 : i;

                     clrs.put(idx, new Color(Float.parseFloat(tokens[0])
                        * IUtils.ONE_255, Float.parseFloat(tokens[1])
                           * IUtils.ONE_255, Float.parseFloat(tokens[2])
                              * IUtils.ONE_255, 1.0f));

                     ++i;
                  }
               }
            }

            result = clrs.values().toArray(new Color[clrs.size()]);

         } catch ( final Exception e ) {
            e.printStackTrace();
         } finally {
            in.close();
         }

      } catch ( final Exception e ) {
         e.printStackTrace();
      }

      return result;
   }

   /**
    * Parses a .ggr file containing a GIMP gradient.
    *
    * @param fileName the file name
    *
    * @return the gradient
    */
   public static Color[] load ( final String fileName ) {

      Color[] result = new Color[0];
      try ( BufferedReader br = new BufferedReader(new FileReader(fileName)) ) {
         result = ParserGpl.load(br);
      } catch ( final Exception e ) {
         e.printStackTrace();
      }
      return result;
   }

}
