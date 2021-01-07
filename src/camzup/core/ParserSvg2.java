package camzup.core;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.regex.Pattern;

@Experimental
abstract class ParserSvg2 {

   private ParserSvg2 ( ) {}

   /**
    * Ratio to convert from centimeters to units,
    * {@value ParserSvg#CM_TO_UNIT}. In Processing, the value was 35.43307 .
    */
   public static final float CM_TO_UNIT = 37.795f;

   /**
    * The floating point epsilon, cast to a double.
    */
   public static final double EPS_D = 0.000001d;

   /**
    * Ratio to convert from gradians to radians,
    * {@value ParserSvg#GRAD_TO_RAD}.
    */
   public static final float GRAD_TO_RAD = 0.015708f;

   /**
    * Ratio to convert from inches to units, {@value ParserSvg#IN_TO_UNIT}. In
    * Processing, the value was 90.0 .
    */
   public static final float IN_TO_UNIT = 96.0f;

   /**
    * Ratio to convert from millimeters to units,
    * {@value ParserSvg#MM_TO_UNIT}. In Processing, the value was 3.543307 .
    */
   public static final float MM_TO_UNIT = 3.7795f;

   /**
    * Ratio to convert from picas to units, {@value ParserSvg#PC_TO_UNIT}. In
    * Processing, the value was 15.0 .
    */
   public static final float PC_TO_UNIT = 16.0f;

   /**
    * Ratio to convert from points to units, {@value ParserSvg#PT_TO_UNIT}. In
    * Processing the value was 1.25 .
    */
   public static final float PT_TO_UNIT = IUtils.FOUR_THIRDS;

   /**
    * Regular expression to find path commands in an SVG path data attribute.
    */
   public static final String PTRN_STR_CMD
      = "[^A|a|C|c|H|h|L|l|M|m|Q|q|S|sT|t|V|v|Z|z]++";

   /**
    * Regular expression to find data elements in an SVG path data attribute.
    */
   public static final String PTRN_STR_DATA
      = "[A|a|C|c|H|h|L|l|M|m|Q|q|S|sT|t|V|v|Z|z|,|\u0020]";

   /**
    * Ratio to convert from pixels to units, {@value ParserSvg#PX_TO_UNIT}.
    */
   public static final float PX_TO_UNIT = 1.0f;

   /**
    * For use in parsing arcs to between points, <code>2.0 / Math.PI</code>,
    * approximately {@value ParserSvg#TWO_DIV_PI_D} .
    */
   public static final double TWO_DIV_PI_D = 0.6366197723675814d;

   /**
    * The compiled pattern for SVG path commands.
    */
   protected static final Pattern PATTERN_CMD = Pattern.compile(
      ParserSvg.PTRN_STR_CMD);

   /**
    * The compiled pattern for SVG data elements.
    */
   protected static final Pattern PATTERN_DATA = Pattern.compile(
      ParserSvg.PTRN_STR_DATA);

   public static void parse ( final String fileName ) {

      try {
         final DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
         df.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
         df.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
         final DocumentBuilder db = df.newDocumentBuilder();
         final File file = new File(fileName);
         final Document doc = db.parse(file);
         doc.normalizeDocument();

         final Node header = doc.getFirstChild();
         final NamedNodeMap hdrAttr = header.getAttributes();

         final Node width = hdrAttr.getNamedItem("width");
         // nodeGetValue instead?
         final String widthStr = width != null ? width.getTextContent() : "0";
         final float widpx = ParserSvg2.parseFloat(widthStr, 0);

         final Node height = hdrAttr.getNamedItem("height");
         final String heightStr = height != null ? height.getTextContent()
            : "0";
         final float hghpx = ParserSvg2.parseFloat(heightStr, 0);

         System.out.println(widpx);
         System.out.println(hghpx);

         // final NodeList nodes = header.getChildNodes();
         // final int nodeLen = nodes.getLength();

      } catch ( final Exception e ) {
         e.printStackTrace();
      } finally {

      }
   }

   /**
    * A helper function to parse units of measurement in an SVG element. Uses
    * the following conversions:
    * <ul>
    * <li>1 centimeter (cm) = {@value ParserSvg#CM_TO_UNIT} units</li>
    * <li>1 inch (in) = {@value ParserSvg#IN_TO_UNIT} units</li>
    * <li>1 millimeter (mm) = {@value ParserSvg#MM_TO_UNIT} units</li>
    * <li>1 pica (pc) = {@value ParserSvg#PC_TO_UNIT} units</li>
    * <li>1 point (pt) = {@value ParserSvg#PT_TO_UNIT} units</li>
    * <li>1 pixel (px) = {@value ParserSvg#PX_TO_UNIT} units</li>
    * </ul>
    * View box relative units, namely <code>%</code> is limited; all this
    * parser will do is divide the value by 100.<br>
    * <br>
    * Font-relative units, namely <code>em</code> and <code>ex</code> are not
    * supported.<br>
    * <br>
    * For polar coordinates, radians are assumed to be the default. Degrees
    * (deg) are converted to radians through multiplication by
    * {@value IUtils#DEG_TO_RAD}.
    *
    * @param u   the String value
    * @param def the default value
    *
    * @return the parsed float
    */
   public static float parseFloat ( final String u, final float def ) {

      /*
       * The string needs to be trimmed even here because of unconventional
       * formatting which could be contained within a path string.
       */

      float x = def;
      final String v = u.trim();
      final int len = v.length();
      final int lens1 = len - 1;
      final int lens2 = len - 2;
      final int lens3 = len - 3;

      try {

         if ( v.startsWith("cm", lens2) ) {

            /* Centimeters. */
            x = Float.parseFloat(v.substring(0, lens2)) * ParserSvg2.CM_TO_UNIT;

         } else if ( v.startsWith("deg", lens3) ) {

            /* Degrees. */
            x = Float.parseFloat(v.substring(0, lens3)) * IUtils.DEG_TO_RAD;

         } else if ( v.startsWith("in", lens2) ) {

            /* Inches. */
            x = Float.parseFloat(v.substring(0, lens2)) * ParserSvg2.IN_TO_UNIT;

         } else if ( v.startsWith("mm", lens2) ) {

            /* Millimeters. */
            x = Float.parseFloat(v.substring(0, lens2)) * ParserSvg2.MM_TO_UNIT;

         } else if ( v.startsWith("pc", lens2) ) {

            /* Pica. */
            x = Float.parseFloat(v.substring(0, lens2)) * ParserSvg2.PC_TO_UNIT;

         } else if ( v.startsWith("pt", lens2) ) {

            /* Point. */
            x = Float.parseFloat(v.substring(0, lens2)) * ParserSvg2.PT_TO_UNIT;

         } else if ( v.startsWith("px", lens2) ) {

            /* Pixel. */
            x = Float.parseFloat(v.substring(0, lens2)) * ParserSvg2.PX_TO_UNIT;

         } else if ( v.startsWith("rad", lens3) ) {

            /* Radians. */
            x = Float.parseFloat(v.substring(0, lens3));

         } else if ( v.startsWith("em", lens2) ) {

            /* RELATIVE UNIT: To font size. Not supported in original. */
            x = Float.parseFloat(v.substring(0, lens2));

         } else if ( v.startsWith("ex", lens2) ) {

            /* RELATIVE UNIT: To font size. Not supported in original. */
            x = Float.parseFloat(v.substring(0, lens2));

         } else if ( v.startsWith("%", lens1) ) {

            /* RELATIVE UNIT: Simplified from original. */
            x = Float.parseFloat(v.substring(0, lens1)) * 0.01f;

         } else {

            x = Float.parseFloat(v);

         }

      } catch ( final Exception e ) { /* Do nothing. */ }

      return x;
   }

}
