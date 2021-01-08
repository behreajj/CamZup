package camzup.core;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.regex.Pattern;

@Experimental
public abstract class ParserSvg2 {

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
      ParserSvg2.PTRN_STR_CMD);

   /**
    * The compiled pattern for SVG data elements.
    */
   protected static final Pattern PATTERN_DATA = Pattern.compile(
      ParserSvg2.PTRN_STR_DATA);

   public static CurveEntity2 parse ( final String fileName ) {

      final CurveEntity2 result = new CurveEntity2();

      try {
         final DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
         df.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
         df.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
         final DocumentBuilder db = df.newDocumentBuilder();
         final File file = new File(fileName);
         final Document doc = db.parse(file);
         doc.normalizeDocument();

         // Temporary matrices used when parsing a transform.
         final Mat3 curr = new Mat3();
         final Mat3 delta = new Mat3();
         final Deque < Mat3 > matStack = new LinkedList <>();
         matStack.push(Mat3.identity(new Mat3()));

         final ArrayList < Curve2 > curves = new ArrayList <>();

         final Node header = doc.getFirstChild();
         final NodeList nodes = header.getChildNodes();
         final int nodeLen = nodes.getLength();
         for ( int i = 0; i < nodeLen; ++i ) {
            final Node node = nodes.item(i);
            ParserSvg2.parseNode(node, matStack, curr, delta, curves);
         }

         result.appendAll(curves);

      } catch ( final Exception e ) {
         e.printStackTrace();
      }

      return result;
   }

   /**
    * A helper function to parse an angle to radians. The default is assumed
    * to be degrees.
    *
    * @param u   the input value
    * @param def the default
    *
    * @return the angle in radians
    */
   protected static float parseAngle ( final String u, final float def ) {

      float x = def;
      final String v = u.trim();
      final int len = v.length();
      final int lens3 = len - 3;
      final int lens4 = len - 4;

      try {

         if ( v.startsWith("deg", lens3) ) {

            x = Float.parseFloat(v.substring(0, lens3)) * IUtils.DEG_TO_RAD;

         } else if ( v.startsWith("grad", lens4) ) {

            x = Float.parseFloat(v.substring(0, lens4))
               * ParserSvg2.GRAD_TO_RAD;

         } else if ( v.startsWith("rad", lens3) ) {

            x = Float.parseFloat(v.substring(0, lens3));

         } else if ( v.startsWith("turn", lens4) ) {

            x = Float.parseFloat(v.substring(0, lens4)) * IUtils.TAU;

         } else {

            /* Degrees is the SVG default? */
            x = Float.parseFloat(v) * IUtils.DEG_TO_RAD;

         }

      } catch ( final Exception e ) { /* Do nothing. */ }

      return x;
   }

   /**
    * Parses a SVG node and returns a Curve2 approximating a circle.
    *
    * @param ellipseNode the ellipse node
    * @param target      the output curve
    *
    * @return the ellipse curve
    */
   protected static Curve2 parseEllipse ( final Node ellipseNode,
      final Curve2 target ) {

      final NamedNodeMap attributes = ellipseNode.getAttributes();
      if ( attributes != null ) {

         /* Search for attribute nodes. May return null. */
         final Node cxnode = attributes.getNamedItem("cx");
         final Node cynode = attributes.getNamedItem("cy");
         Node rxnode = attributes.getNamedItem("rx");
         Node rynode = attributes.getNamedItem("ry");

         /*
          * One or other of the ellipse's axes could be missing, or this node
          * could be circle.
          */
         if ( rynode != null && rxnode == null ) { rxnode = rynode; }
         if ( rxnode != null && rynode == null ) { rynode = rxnode; }
         if ( rxnode == null && rynode == null ) {
            rxnode = rynode = attributes.getNamedItem("r");
         }

         /* Acquire text content from the node if it exists. */
         final String cxstr = cxnode != null ? cxnode.getTextContent() : "0";
         final String cystr = cynode != null ? cynode.getTextContent() : "0";
         final String rxstr = rxnode != null ? rxnode.getTextContent() : "0.5";
         final String rystr = rynode != null ? rynode.getTextContent() : "0.5";

         /* Parse string or default. */
         final float cx = ParserSvg2.parseFloat(cxstr, 0.0f);
         final float cy = ParserSvg2.parseFloat(cystr, 0.0f);
         final float rx = ParserSvg2.parseFloat(rxstr, 0.5f);
         final float ry = ParserSvg2.parseFloat(rystr, 0.5f);

         /* Find cardinal control points. */
         final float right = cx + rx;
         final float top = cy + ry;
         final float left = cx - rx;
         final float bottom = cy - ry;

         final float horizHandle = rx * ICurve.HNDL_MAG_ORTHO;
         final float vertHandle = ry * ICurve.HNDL_MAG_ORTHO;

         final float xHandlePos = cx + horizHandle;
         final float xHandleNeg = cx - horizHandle;

         final float yHandlePos = cy + vertHandle;
         final float yHandleNeg = cy - vertHandle;

         /* Resize and acquire four knots. */
         target.resize(4);
         final Knot2 kn0 = target.get(0);
         final Knot2 kn1 = target.get(1);
         final Knot2 kn2 = target.get(2);
         final Knot2 kn3 = target.get(3);

         kn0.coord.set(right, cy);
         kn0.foreHandle.set(right, yHandlePos);
         kn0.rearHandle.set(right, yHandleNeg);

         kn1.coord.set(cx, top);
         kn1.foreHandle.set(xHandleNeg, top);
         kn1.rearHandle.set(xHandlePos, top);

         kn2.coord.set(left, cy);
         kn2.foreHandle.set(left, yHandleNeg);
         kn2.rearHandle.set(left, yHandlePos);

         kn3.coord.set(cx, bottom);
         kn3.foreHandle.set(xHandlePos, bottom);
         kn3.rearHandle.set(xHandleNeg, bottom);

         target.closedLoop = true;
      }

      return target;
   }

   /**
    * A helper function to parse units of measurement in an SVG element. Uses
    * the following conversions:
    * <ul>
    * <li>1 centimeter (cm) = {@value ParserSvg2#CM_TO_UNIT} units</li>
    * <li>1 inch (in) = {@value ParserSvg2#IN_TO_UNIT} units</li>
    * <li>1 millimeter (mm) = {@value ParserSvg2#MM_TO_UNIT} units</li>
    * <li>1 pica (pc) = {@value ParserSvg2#PC_TO_UNIT} units</li>
    * <li>1 point (pt) = {@value ParserSvg2#PT_TO_UNIT} units</li>
    * <li>1 pixel (px) = {@value ParserSvg2#PX_TO_UNIT} units</li>
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
   protected static float parseFloat ( final String u, final float def ) {

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

   @Recursive
   protected static ArrayList < Curve2 > parseNode ( final Node node,
      final Deque < Mat3 > matStack, final Mat3 curr, final Mat3 delta,
      final ArrayList < Curve2 > curves ) {

      final NamedNodeMap attributes = node.getAttributes();
      boolean containsTransform = false;
      Mat3 cumulative = curr;
      if ( attributes != null ) {
         final Node transform = attributes.getNamedItem("transform");
         if ( transform != null ) {
            containsTransform = true;
            ParserSvg2.parseTransform(transform, curr, delta);
            final Mat3 prev = matStack.peek();
            cumulative = Mat3.mul(prev, curr, new Mat3());
            matStack.push(cumulative);
         }

         // TODO: Implement.
         final String name = node.getNodeName().toLowerCase();
         final int hsh = name.hashCode();
         Curve2 result = null;

         switch ( hsh ) {
            case -1360216880:
               /* "circle" */

            case -1656480802:
               /* "ellipse" */

               result = ParserSvg2.parseEllipse(node, new Curve2());
               break;

            case 103:
               /* "g" */

               final NodeList children = node.getChildNodes();
               final int childLen = children.getLength();
               for ( int i = 0; i < childLen; ++i ) {
                  final Node child = children.item(i);
                  ParserSvg2.parseNode(child, matStack, cumulative, delta,
                     curves);
               }

               break;

            case 3496420:
               /* "rect" */

               result = ParserSvg2.parseRect(node, new Curve2());
               break;

            default:
         }

         if ( containsTransform ) { matStack.pop(); }

         /* Node may be a group node, "g", where result is null. */
         if ( result != null ) {
            result.transform(cumulative);
            curves.add(result);
         }
      }

      return curves;
   }

   /**
    * Parses a SVG node and returns a Curve2 representing a rectangle.
    *
    * @param rectNode the rectangle node
    * @param target   the output curve
    *
    * @return the rectangle curve
    */
   protected static Curve2 parseRect ( final Node rectNode,
      final Curve2 target ) {

      final NamedNodeMap attributes = rectNode.getAttributes();
      if ( attributes != null ) {

         /* Search for property nodes. May return null. */
         final Node xnode = attributes.getNamedItem("x");
         final Node ynode = attributes.getNamedItem("y");
         final Node wnode = attributes.getNamedItem("width");
         final Node hnode = attributes.getNamedItem("height");

         /* One or other of the rounding tags may be missing. */
         Node rxnode = attributes.getNamedItem("rx");
         Node rynode = attributes.getNamedItem("ry");
         if ( rynode != null && rxnode == null ) { rxnode = rynode; }
         if ( rxnode != null && rynode == null ) { rynode = rxnode; }

         /* Acquire text content from the node if it exists. */
         final String xstr = xnode != null ? xnode.getTextContent() : "0";
         final String ystr = ynode != null ? ynode.getTextContent() : "0";
         final String wstr = wnode != null ? wnode.getTextContent() : "1";
         final String hstr = hnode != null ? hnode.getTextContent() : "1";
         final String rxstr = rxnode != null ? rxnode.getTextContent() : "0";
         final String rystr = rynode != null ? rynode.getTextContent() : "0";

         /* Parse string or default. */
         final float x = ParserSvg2.parseFloat(xstr, 0.0f);
         final float y = ParserSvg2.parseFloat(ystr, 0.0f);
         final float w = ParserSvg2.parseFloat(wstr, 1.0f);
         final float h = ParserSvg2.parseFloat(hstr, 1.0f);
         final float rx = ParserSvg2.parseFloat(rxstr, 0.0f);
         final float ry = ParserSvg2.parseFloat(rystr, 0.0f);

         /*
          * Corner rounding differs between APIs, so average horizontal and
          * vertical rounding.
          */
         final float rAvg = ( rx + ry ) * 0.5f;
         if ( rAvg < IUtils.EPSILON ) {
            Curve2.rect(x, y, x + w, y + h, target);
         } else {
            Curve2.rect(x, y, x + w, y + h, rAvg, target);
         }
      }
      return target;
   }

   /**
    * Parses an SVG node containing transform data and converts it to a 3x3
    * matrix. The delta matrix contains individual transform commands such as
    * "translate", "rotate" and "scale."
    *
    * @param trNode the transform node
    * @param target the output matrix
    * @param delta  a temporary matrix
    *
    * @return the matrix
    */
   protected static Mat3 parseTransform ( final Node trNode, final Mat3 target,
      final Mat3 delta ) {

      final String v = trNode.getTextContent().trim().toLowerCase();

      final String[] segStrs = v.split("\\),*");
      final int segLen = segStrs.length;
      for ( int i = 0; i < segLen; ++i ) {
         final String seg = segStrs[i];

         /* Find the command section of the String. */
         final int openParenIdx = seg.indexOf('(', 0);
         final String cmd = seg.substring(0, openParenIdx).trim();
         final int hsh = cmd.hashCode();

         /* Find the data section of the String. */
         final String dataBlock = seg.substring(openParenIdx + 1);
         final String[] data = dataBlock.split(",\\s*");
         final int dataLen = data.length;

         switch ( hsh ) {

            case -1081239615:
               /* "matrix" */

               /* Column major. */
               final String m00 = data[0];
               final String m10 = data[1];
               final String m01 = data[2];
               final String m11 = data[3];
               final String m02 = data[4];
               final String m12 = data[5];

               /* @formatter:off */
               delta.set(
                  ParserSvg2.parseFloat(m00, 1.0f),
                  ParserSvg2.parseFloat(m01, 0.0f),
                  ParserSvg2.parseFloat(m02, 0.0f),
                  ParserSvg2.parseFloat(m10, 0.0f),
                  ParserSvg2.parseFloat(m11, 1.0f),
                  ParserSvg2.parseFloat(m12, 0.0f),
                  0.0f, 0.0f, 1.0f);
               /* @formatter:on */
               // Mat3.mul(target, delta, target);
               Mat3.mul(delta, target, target);

               break;

            case -925180581:
               /* "rotate" */
               final String ang = data[0];
               Mat3.fromRotZ(ParserSvg2.parseAngle(ang, 0.0f), delta);
               // Mat3.mul(target, delta, target);
               Mat3.mul(delta, target, target);

               break;

            case 109250890:
               /* "scale" */

               final String scx = data[0];
               final String scy = dataLen > 1 ? data[1] : scx;
               Mat3.fromScale(ParserSvg2.parseFloat(scx, 1.0f), ParserSvg2
                  .parseFloat(scy, 1.0f), delta);
               // Mat3.mul(target, delta, target);
               Mat3.mul(delta, target, target);

               break;

            case 109493422:
               /* "skewx" */

               final String skx = data[0];
               Mat3.fromSkewX(ParserSvg2.parseAngle(skx, 0.0f), delta);
               // Mat3.mul(target, delta, target);
               Mat3.mul(delta, target, target);

               break;

            case 109493423:
               /* "skewy" */

               final String sky = data[0];
               Mat3.fromSkewY(ParserSvg2.parseAngle(sky, 0.0f), delta);
               // Mat3.mul(target, delta, target);
               Mat3.mul(delta, target, target);

               break;

            case 1052832078:
               /* "translate" */

               final String tx = data[0];
               final String ty = dataLen > 1 ? data[1] : "0";
               Mat3.fromTranslation(ParserSvg2.parseFloat(tx, 0.0f), ParserSvg2
                  .parseFloat(ty, 0.0f), delta);
               // Mat3.mul(target, delta, target);
               Mat3.mul(delta, target, target);

               break;

            default:

         }

      }

      return target;
   }

}
