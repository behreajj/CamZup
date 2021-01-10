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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;

@Experimental
public abstract class ParserSvg2 {

   private ParserSvg2 ( ) {}

   /**
    * Ratio to convert from centimeters to units,
    * {@value ParserSvg2#CM_TO_UNIT}. In Processing, the value was 35.43307 .
    */
   public static final float CM_TO_UNIT = 37.795f;

   /**
    * The floating point epsilon, cast to a double.
    */
   public static final double EPS_D = 0.000001d;

   /**
    * Ratio to convert from gradians to radians,
    * {@value ParserSvg2#GRAD_TO_RAD}.
    */
   public static final float GRAD_TO_RAD = 0.015708f;

   /**
    * Ratio to convert from inches to units, {@value ParserSvg2#IN_TO_UNIT}.
    * In Processing, the value was 90.0 .
    */
   public static final float IN_TO_UNIT = 96.0f;

   /**
    * Ratio to convert from millimeters to units,
    * {@value ParserSvg2#MM_TO_UNIT}. In Processing, the value was 3.543307 .
    */
   public static final float MM_TO_UNIT = 3.7795f;

   /**
    * Ratio to convert from picas to units, {@value ParserSvg2#PC_TO_UNIT}. In
    * Processing, the value was 15.0 .
    */
   public static final float PC_TO_UNIT = 16.0f;

   /**
    * Ratio to convert from points to units, {@value ParserSvg2#PT_TO_UNIT}.
    * In Processing the value was 1.25 .
    */
   public static final float PT_TO_UNIT = IUtils.FOUR_THIRDS;

   /**
    * Regular expression to find path commands in an SVG path data attribute.
    */
   public static final String PTRN_STR_CMD
      = "[^A|a|C|c|H|h|L|l|M|m|Q|q|S|s|T|t|V|v|Z|z]++";

   /**
    * Regular expression to find data elements in an SVG path data attribute.
    */
   public static final String PTRN_STR_DATA
      = "[A|a|C|c|H|h|L|l|M|m|Q|q|S|s|T|t|V|v|Z|z|,|\\u0020]";

   /**
    * Ratio to convert from pixels to units, {@value ParserSvg2#PX_TO_UNIT}.
    */
   public static final float PX_TO_UNIT = 1.0f;

   /**
    * For use in parsing arcs to between points, <code>2.0 / PI</code>,
    * approximately {@value ParserSvg2#TWO_DIV_PI} .
    */
   public static final float TWO_DIV_PI = 0.63661975f;

   /**
    * For use in parsing arcs to between points, <code>2.0 / PI</code>,
    * approximately {@value ParserSvg2#TWO_DIV_PI_D} .
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

   /** Characters used to represent a command in an SVG path. */
   private static final char[] CMDS = { 'A', 'C', 'H', 'L', 'M', 'Q', 'S', 'T',
      'V', 'Z', 'a', 'c', 'h', 'l', 'm', 'q', 's', 't', 'v', 'z' };

   public static CurveEntity2 parse ( final String fileName ) {

      final CurveEntity2 result = new CurveEntity2();

      try {
         /* Sonarlint security complaint recommends these settings. */
         final DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
         df.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
         df.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
         final DocumentBuilder db = df.newDocumentBuilder();
         final File file = new File(fileName);
         final Document doc = db.parse(file);
         doc.normalizeDocument();

         /*
          * Because nodes are hierarchical and transformations compound upon
          * transformations, a stack is needed (a double-ended que is the
          * closest in Java).
          */
         final Mat3 curr = new Mat3();
         final Mat3 delta = new Mat3();
         final LinkedList < Mat3 > matStack = new LinkedList <>();
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
    * Parses a flag to a boolean
    *
    * @param v the flag string
    *
    * @return the boolean
    */
   public static boolean parseFlagToBool ( final String v ) {

      int x = 0;
      try {
         x = Integer.parseInt(v);
      } catch ( final Exception e ) {
         x = 0;
      }
      return x != 0;
   }

   /**
    * Re-breaks String tokens with numbers separated by negative signs.
    *
    * @param tokens the tokens
    *
    * @return the partitioned tokens
    */
   protected static String[] breakNeg ( final String[] tokens ) {

      final ArrayList < String > result = new ArrayList <>();
      final int tokLen = tokens.length;
      for ( int i = 0; i < tokLen; ++i ) {
         final String str = tokens[i];
         final char[] chrs = str.toCharArray();
         final int chrsLen = chrs.length;
         StringBuilder sb = new StringBuilder(chrsLen);
         for ( int k = 0; k < chrsLen; ++k ) {
            final char ch = chrs[k];
            if ( ch == '-' ) {
               result.add(sb.toString());
               sb = new StringBuilder(chrsLen - k);
            }
            sb.append(ch);
         }
      }
      return result.toArray(new String[result.size()]);
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
    * Parse a path arc-to command, based off the Processing implementation,
    * which references
    * <a href="http://www.spaceroots.org/documents/ellipse/node22.html">these
    * equations</a>.<br>
    * <br>
    * According to the SVG specification, the large arc flag "is 0 if an arc
    * spanning less than or equal to 180 degrees is chosen, or 1 if an arc
    * spanning greater than 180 degrees is chosen".<br>
    * <br>
    * The sweep flag "is 0 if the line joining center to arc sweeps through
    * decreasing angles, or 1 if it sweeps through increasing angles".
    *
    * @param prior    the prior knot
    * @param major    the major axis
    * @param minor    the minor axis
    * @param ang      the angle
    * @param largeArc large arc flag
    * @param sweep    sweep flag
    * @param xDest    destination x
    * @param yDest    destination y
    *
    * @return an array of knots
    */
   protected static Knot2[] parseArcTo ( final Knot2 prior, final float major,
      final float minor, final float ang, final boolean largeArc,
      final boolean sweep, final float xDest, final float yDest ) {

      if ( Utils.approx(prior.coord.x, xDest) && Utils.approx(prior.coord.y,
         yDest) || major < IUtils.EPSILON || minor < IUtils.EPSILON ) {
         return new Knot2[] { new Knot2(xDest, yDest) };
      }

      /* Valid major and minor axes. */
      float rx = Utils.abs(major);
      float ry = Utils.abs(minor);

      /* Wrap the angle into range, find sine and cosine. */
      final float phi = Utils.mod1(ang * IUtils.ONE_TAU);
      final float cosPhi = Utils.scNorm(phi);
      final float sinPhi = Utils.scNorm(phi - 0.25f);

      final float x1 = prior.coord.x;
      final float y1 = prior.coord.y;

      final float xdiff = x1 - xDest;
      final float ydiff = y1 - yDest;

      /* Apply phi rotation to difference. */
      final float x1r = 0.5f * ( cosPhi * xdiff + sinPhi * ydiff );
      final float y1r = 0.5f * ( cosPhi * ydiff - sinPhi * xdiff );

      /* Square the rotation. */
      final float x1rsq = x1r * x1r;
      final float y1rsq = y1r * y1r;

      /* Square the major and minor axes. */
      final float rxsq = rx * rx;
      final float rysq = ry * ry;

      float cxr = 0.0f;
      float cyr = 0.0f;
      final float a = x1rsq / rxsq + y1rsq / rysq;
      if ( a > 1.0f ) {
         final float sqrta = Utils.sqrtUnchecked(a);
         rx *= sqrta;
         ry *= sqrta;
      } else {
         final float denom = rxsq * y1rsq + rysq * x1rsq;
         float k = Utils.sqrtUnchecked(rxsq * rysq / denom - 1.0f);
         k = largeArc == sweep ? -k : k;
         cxr = k * rx * y1r / ry;
         cyr = -k * ry * x1r / rx;
      }

      final float cx = cosPhi * cxr - sinPhi * cyr + 0.5f * ( x1 + xDest );
      final float cy = sinPhi * cxr + cosPhi * cyr + 0.5f * ( y1 + yDest );

      final float rxInv = 1.0f / rx;
      final float ryInv = 1.0f / ry;
      final float sx = ( x1r - cxr ) * rxInv;
      final float sy = ( y1r - cyr ) * ryInv;
      final float tx = ( -x1r - cxr ) * rxInv;
      final float ty = ( -y1r - cyr ) * ryInv;

      final float phi1 = Utils.atan2(sy, sx);

      float phiDelta = Utils.modRadians(Utils.atan2(ty, tx) - phi1);
      if ( !sweep ) { phiDelta -= IUtils.TAU; }

      final float phiNorm = Utils.mod1(phi1 * IUtils.ONE_TAU);
      final float cosPhi1 = Utils.scNorm(phiNorm);
      final float sinPhi1 = Utils.scNorm(phiNorm - 0.25f);

      final int segCount = Utils.ceil(Utils.abs(phiDelta)
         * ParserSvg2.TWO_DIV_PI);
      final float incr = phiDelta / segCount;
      final float tanIncr = Utils.tan(incr * 0.5f);
      final float handle = Utils.sqrt(4.0f + 3.0f * tanIncr * tanIncr) - 1.0f;
      final float b = Utils.sin(incr) * handle * IUtils.ONE_THIRD;

      /* To determine when to wrap to initial point. */
      final int segLast = segCount - 1;

      /* Changed within for loop. */
      float rxSinEta = -rx * sinPhi1;
      float ryCosEta = ry * cosPhi1;
      float fhxPrev = b * ( rxSinEta * cosPhi - ryCosEta * sinPhi );
      float fhyPrev = b * ( rxSinEta * sinPhi + ryCosEta * cosPhi );
      float coxPrev = x1;
      float coyPrev = y1;
      float j = 1.0f;

      final Knot2[] kns = new Knot2[segCount];
      Knot2 prev = prior;

      for ( int i = 0; i < segCount; ++i ) {

         final float eta = phi1 + j * incr;
         final float cosEta = Utils.cos(eta);
         final float sinEta = Utils.sin(eta);

         rxSinEta = -rx * sinEta;
         ryCosEta = ry * cosEta;

         final float rhxCurr = b * ( rxSinEta * cosPhi - ryCosEta * sinPhi );
         final float rhyCurr = b * ( rxSinEta * sinPhi + ryCosEta * cosPhi );

         float coxCurr = xDest;
         float coyCurr = yDest;
         if ( i != segLast ) {
            final float rxCosEta = rx * cosEta;
            final float rySinEta = ry * sinEta;
            coxCurr = cx + cosPhi * rxCosEta - sinPhi * rySinEta;
            coyCurr = cy + sinPhi * rxCosEta + cosPhi * rySinEta;
         }

         prev.foreHandle.set(coxPrev + fhxPrev, coyPrev + fhyPrev);

         final Knot2 curr = new Knot2(coxCurr, coyCurr, coxCurr + rhxCurr,
            coyCurr + rhyCurr, coxCurr - rhxCurr, coyCurr - rhyCurr);
         kns[i] = curr;

         /* Update references. */
         prev = curr;
         coxPrev = coxCurr;
         fhxPrev = rhxCurr;
         coyPrev = coyCurr;
         fhyPrev = rhyCurr;
         ++j;
      }

      return kns;
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
         cxnode.getNodeValue();
         final String cxstr = cxnode != null ? cxnode.getNodeValue() : "0";
         final String cystr = cynode != null ? cynode.getNodeValue() : "0";
         final String rxstr = rxnode != null ? rxnode.getNodeValue() : "0.5";
         final String rystr = rynode != null ? rynode.getNodeValue() : "0.5";

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

   /**
    * Parses a SVG node and returns a Curve2 forming a line.
    *
    * @param lineNode the line node
    * @param target   the output curve
    *
    * @return the line curve
    */
   protected static Curve2 parseLine ( final Node lineNode,
      final Curve2 target ) {

      final NamedNodeMap attributes = lineNode.getAttributes();
      if ( attributes != null ) {

         /* Search for attribute nodes. May return null. */
         final Node x1node = attributes.getNamedItem("x1");
         final Node y1node = attributes.getNamedItem("y1");
         final Node x2node = attributes.getNamedItem("x2");
         final Node y2node = attributes.getNamedItem("y2");

         /* Acquire text content from the node if it exists. */
         final String x1str = x1node != null ? x1node.getNodeValue() : "-0.5";
         final String y1str = y1node != null ? y1node.getNodeValue() : "0";
         final String x2str = x2node != null ? x2node.getNodeValue() : "0.5";
         final String y2str = y2node != null ? y2node.getNodeValue() : "0";

         /* Parse string or default. */
         final float x1 = ParserSvg2.parseFloat(x1str, -0.5f);
         final float y1 = ParserSvg2.parseFloat(y1str, 0.0f);
         final float x2 = ParserSvg2.parseFloat(x2str, 0.5f);
         final float y2 = ParserSvg2.parseFloat(y2str, 0.0f);

         Curve2.line(x1, y1, x2, y2, target);
      }
      return target;
   }

   @Recursive
   protected static ArrayList < Curve2 > parseNode ( final Node node,
      final LinkedList < Mat3 > matStack, final Mat3 prev, final Mat3 delta,
      final ArrayList < Curve2 > curves ) {

      final NamedNodeMap attributes = node.getAttributes();
      if ( attributes != null ) {

         boolean containsTransform = false;
         boolean isGroup = false;
         final Mat3 cumulative = new Mat3(prev);
         final Node transform = attributes.getNamedItem("transform");
         if ( transform != null ) {
            containsTransform = true;
            ParserSvg2.parseTransform(transform, cumulative, delta);
            Mat3.mul(matStack.peek(), cumulative, cumulative);
            matStack.push(cumulative);
         }

         Curve2 prim = null;
         ArrayList < Curve2 > path = null;
         final String name = node.getNodeName().toLowerCase();
         final int hsh = name.hashCode();
         switch ( hsh ) {

            case -1656480802:
               /* "ellipse" */

               prim = ParserSvg2.parseEllipse(node, new Curve2());
               break;

            case -1360216880:
               /* "circle" */

               prim = ParserSvg2.parseEllipse(node, new Curve2());
               break;

            case -397519558:
               /* "polygon" */

               prim = ParserSvg2.parsePoly(node, new Curve2());
               break;

            case 103:
               /* "g" */

               isGroup = true;
               break;

            case 3321844:
               /* "line" */

               prim = ParserSvg2.parseLine(node, new Curve2());
               break;

            case 3433509:
               /* "path" */

               path = ParserSvg2.parsePath(node);
               break;

            case 3496420:
               /* "rect" */

               prim = ParserSvg2.parseRect(node, new Curve2());
               break;

            case 561938880:
               /* "polyline" */

               prim = ParserSvg2.parsePoly(node, new Curve2());
               break;

            default:
         }

         if ( containsTransform ) { matStack.pop(); }

         if ( isGroup ) {
            final NodeList children = node.getChildNodes();
            final int childLen = children.getLength();
            for ( int i = 0; i < childLen; ++i ) {
               final Node child = children.item(i);
               ParserSvg2.parseNode(child, matStack, cumulative, delta, curves);
            }
         }

         /*
          * Node may be a group node, "g", where result is null; a path, which
          * has multiple curves; or a primitive.
          */
         if ( prim != null ) {
            prim.transform(prev);
            curves.add(prim);
         }

         if ( path != null ) {
            final Iterator < Curve2 > itr = path.iterator();
            while ( itr.hasNext() ) {
               final Curve2 pathCurve = itr.next();
               pathCurve.transform(prev);
               curves.add(pathCurve);
            }
         }
      }

      return curves;
   }

   protected static ArrayList < Curve2 > parsePath ( final Node pathNode ) {

      final ArrayList < Curve2 > result = new ArrayList <>(2);

      final NamedNodeMap attributes = pathNode.getAttributes();
      if ( attributes != null ) {
         final Node pathData = attributes.getNamedItem("d");
         if ( pathData != null ) {
            final String pdStr = pathData.getNodeValue();

            /*
             * Usually, one path has one move to command, but in case it
             * doesn't, create a boolean to track an initial move to. If there
             * is more than one, the target curve will be added to the result
             * list and replaced by a new one.
             */
            boolean initialMove = true;
            boolean closedLoop = false;
            int cursor = -1;

            /* Current curve. */
            Curve2 target = new Curve2();
            result.add(target);

            /* Tracks the previous coordinate for relative commands. */
            final Vec2 relative = new Vec2();

            /* For quadratic reflections. */
            final Vec2 midHnd = new Vec2();

            /* Current, previous knots. */
            Knot2 curr = null;
            Knot2 prev = null;

            final char[] pdChars = pdStr.toCharArray();
            final int pdCharsLen = pdChars.length;
            final int cmdsLen = ParserSvg2.CMDS.length;
            final ArrayList < PathCommand > pcs = new ArrayList <>();
            final StringBuilder sb = new StringBuilder(pdCharsLen);
            int idx = 0;
            while ( idx < pdCharsLen ) {
               final char pdChar = pdChars[idx];
               boolean cmdFound = false;

               for ( int j = 0; !cmdFound && j < cmdsLen; ++j ) {
                  if ( pdChar == ParserSvg2.CMDS[j] ) {
                     /* spaces don't need to be between commands and numbers. */
                      sb.append('\n');
                     final PathCommand pc = PathCommand.fromChar(pdChar);
                     pcs.add(pc);
                     cmdFound = true;
                  }
               }

               if ( !cmdFound ) {
                  sb.append(pdChar);
               }

               ++idx;
            }

            /*
             * Split remainder string to by negative signs, but include them if
             * present. Split on commas and any spaces.
             */
            final String remainderString = sb.toString();
            String[] dataStrs = remainderString.split("(,|\\s+|(?=-))", 0);
            dataStrs = ParserSvg2.stripEmptyTokens(dataStrs);
            
            final Iterator < PathCommand > cmdItr = pcs.iterator();
            while ( cmdItr.hasNext() ) {
               final PathCommand cmd = cmdItr.next();
               switch ( cmd ) {

                  case CLOSE_PATH:

                     closedLoop = true;
                     target.closedLoop = closedLoop;

                     break;

                  case MOVE_TO_ABS:

                     /* A curve may be empty due to malformed commands. */
                     if ( !initialMove ) {
                        if ( target.length() > 1 ) { result.add(target); }
                        target = new Curve2();
                     }
                     initialMove = false;

                     curr = new Knot2(ParserSvg2.parseFloat(dataStrs[++cursor],
                        0.0f), ParserSvg2.parseFloat(dataStrs[++cursor], 0.0f));
                     target.append(curr);

                     break;

                  case MOVE_TO_REL:

                     /* A curve may be empty due to malformed commands. */
                     if ( !initialMove ) {
                        if ( target.length() > 1 ) { result.add(target); }
                        target = new Curve2();
                     }
                     initialMove = false;

                     curr = new Knot2(ParserSvg2.parseFloat(dataStrs[++cursor],
                        0.0f), ParserSvg2.parseFloat(dataStrs[++cursor], 0.0f));
                     target.append(curr);
                     curr.translate(relative);

                     break;

                  case LINE_TO_ABS:

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     Knot2.fromSegLinear(ParserSvg2.parseFloat(
                        dataStrs[++cursor], 0.0f), ParserSvg2.parseFloat(
                           dataStrs[++cursor], 0.0f), prev, curr);

                     break;

                  case LINE_TO_REL:

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     Knot2.fromSegLinear(ParserSvg2.parseFloat(
                        dataStrs[++cursor], 0.0f), ParserSvg2.parseFloat(
                           dataStrs[++cursor], 0.0f), prev, curr);
                     curr.translate(relative);

                     break;

                  case HORIZ_ABS:

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     curr.coord.set(ParserSvg2.parseFloat(dataStrs[++cursor],
                        0.0f), prev.coord.y);
                     Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                     Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                     break;

                  case HORIZ_REL:

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     curr.coord.set(relative.x + ParserSvg2.parseFloat(
                        dataStrs[++cursor], 0.0f), prev.coord.y);
                     Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                     Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                     break;

                  case VERT_ABS:

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     curr.coord.set(prev.coord.x, ParserSvg2.parseFloat(
                        dataStrs[++cursor], 0.0f));
                     Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                     Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                     break;

                  case VERT_REL:

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     curr.coord.set(prev.coord.x, relative.y + ParserSvg2
                        .parseFloat(dataStrs[++cursor], 0.0f));
                     Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                     Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                     break;

                  case QUADRATIC_TO_ABS:

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     /*
                      * Mid-handle needs to be set to record quadratic in case
                      * reflection is used next.
                      */
                     midHnd.set(ParserSvg2.parseFloat(dataStrs[++cursor], 0.0f),
                        ParserSvg2.parseFloat(dataStrs[++cursor], 0.0f));
                     curr.coord.set(ParserSvg2.parseFloat(dataStrs[++cursor],
                        0.0f), ParserSvg2.parseFloat(dataStrs[++cursor], 0.0f));

                     Curve2.lerp13(midHnd, prev.coord, prev.foreHandle);
                     Curve2.lerp13(midHnd, curr.coord, curr.rearHandle);

                     break;

                  case QUADRATIC_TO_REL:

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     /*
                      * Mid-handle needs to be set to record quadratic in case
                      * reflection is used next.
                      */
                     midHnd.set(ParserSvg2.parseFloat(dataStrs[++cursor], 0.0f),
                        ParserSvg2.parseFloat(dataStrs[++cursor], 0.0f));
                     curr.coord.set(ParserSvg2.parseFloat(dataStrs[++cursor],
                        0.0f), ParserSvg2.parseFloat(dataStrs[++cursor], 0.0f));

                     Vec2.add(relative, midHnd, midHnd);
                     Vec2.add(relative, curr.coord, curr.coord);

                     Curve2.lerp13(midHnd, prev.coord, prev.foreHandle);
                     Curve2.lerp13(midHnd, curr.coord, curr.rearHandle);

                     break;

                  case REFLECT_QUADRATIC_ABS:

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     prev.mirrorHandlesBackward();
                     curr.coord.set(ParserSvg2.parseFloat(dataStrs[++cursor],
                        0.0f), ParserSvg2.parseFloat(dataStrs[++cursor], 0.0f));

                     /*
                      * Convert mid-handle from point to direction, negate,
                      * convert back to point.
                      */
                     Vec2.sub(midHnd, prev.coord, midHnd);
                     Vec2.negate(midHnd, midHnd);
                     Vec2.add(midHnd, prev.coord, midHnd);
                     Curve2.lerp13(midHnd, curr.coord, curr.rearHandle);

                     break;

                  case REFLECT_QUADRATIC_REL:

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     prev.mirrorHandlesBackward();
                     curr.coord.set(ParserSvg2.parseFloat(dataStrs[++cursor],
                        0.0f), ParserSvg2.parseFloat(dataStrs[++cursor], 0.0f));
                     Vec2.add(relative, curr.coord, curr.coord);

                     /*
                      * Convert mid-handle from point to direction, negate,
                      * convert back to point.
                      */
                     Vec2.sub(midHnd, prev.coord, midHnd);
                     Vec2.negate(midHnd, midHnd);
                     Vec2.add(midHnd, prev.coord, midHnd);
                     Curve2.lerp13(midHnd, curr.coord, curr.rearHandle);

                     break;

                  case CUBIC_TO_ABS:

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     Knot2.fromSegCubic(ParserSvg2.parseFloat(
                        dataStrs[++cursor], 0.0f), ParserSvg2.parseFloat(
                           dataStrs[++cursor], 0.0f), ParserSvg2.parseFloat(
                              dataStrs[++cursor], 0.0f), ParserSvg2.parseFloat(
                                 dataStrs[++cursor], 0.0f), ParserSvg2
                                    .parseFloat(dataStrs[++cursor], 0.0f),
                        ParserSvg2.parseFloat(dataStrs[++cursor], 0.0f), prev,
                        curr);

                     break;

                  case CUBIC_TO_REL:

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     Knot2.fromSegCubic(ParserSvg2.parseFloat(
                        dataStrs[++cursor], 0.0f), ParserSvg2.parseFloat(
                           dataStrs[++cursor], 0.0f), ParserSvg2.parseFloat(
                              dataStrs[++cursor], 0.0f), ParserSvg2.parseFloat(
                                 dataStrs[++cursor], 0.0f), ParserSvg2
                                    .parseFloat(dataStrs[++cursor], 0.0f),
                        ParserSvg2.parseFloat(dataStrs[++cursor], 0.0f), prev,
                        curr);
                     curr.translate(relative);

                     break;

                  case REFLECT_CUBIC_ABS:

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     Knot2.fromSegCubicRefl(ParserSvg2.parseFloat(
                        dataStrs[++cursor], 0.0f), ParserSvg2.parseFloat(
                           dataStrs[++cursor], 0.0f), ParserSvg2.parseFloat(
                              dataStrs[++cursor], 0.0f), ParserSvg2.parseFloat(
                                 dataStrs[++cursor], 0.0f), prev, curr);

                     break;

                  case REFLECT_CUBIC_REL:

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     Knot2.fromSegCubicRefl(ParserSvg2.parseFloat(
                        dataStrs[++cursor], 0.0f), ParserSvg2.parseFloat(
                           dataStrs[++cursor], 0.0f), ParserSvg2.parseFloat(
                              dataStrs[++cursor], 0.0f), ParserSvg2.parseFloat(
                                 dataStrs[++cursor], 0.0f), prev, curr);
                     curr.translate(relative);

                     break;

                  case ARC_TO_ABS:

                     prev = curr;
                     target.appendAll(ParserSvg2.parseArcTo(prev, ParserSvg2
                        .parseFloat(dataStrs[++cursor], 0.0f), ParserSvg2
                           .parseFloat(dataStrs[++cursor], 0.0f), ParserSvg2
                              .parseAngle(dataStrs[++cursor], 0.0f), ParserSvg2
                                 .parseFlagToBool(dataStrs[++cursor]),
                        ParserSvg2.parseFlagToBool(dataStrs[++cursor]),
                        ParserSvg2.parseFloat(dataStrs[++cursor], 0.0f),
                        ParserSvg2.parseFloat(dataStrs[++cursor], 0.0f)));
                     curr = target.get(target.length() - 1);

                     break;

                  case ARC_TO_REL:

                     prev = curr;
                     target.appendAll(ParserSvg2.parseArcTo(prev, ParserSvg2
                        .parseFloat(dataStrs[++cursor], 0.0f), ParserSvg2
                           .parseFloat(dataStrs[++cursor], 0.0f), ParserSvg2
                              .parseAngle(dataStrs[++cursor], 0.0f), ParserSvg2
                                 .parseFlagToBool(dataStrs[++cursor]),
                        ParserSvg2.parseFlagToBool(dataStrs[++cursor]),
                        relative.x + ParserSvg2.parseFloat(dataStrs[++cursor],
                           0.0f), relative.y + ParserSvg2.parseFloat(
                              dataStrs[++cursor], 0.0f)));
                     curr = target.get(target.length() - 1);

                     break;

                  default:
               }

               relative.set(curr.coord);
            }

            /*
             * Append final curve recorded by while loop if it hasn't been
             * added; for multiple "sub-path"s which are not closed. As before,
             * final curve should have more than 2 knots.
             */
            if ( result.indexOf(target) < 0 && target.length() > 1 ) {
               result.add(target);
            }

            /* Deal with first and last knots in open versus closed loop. */
            final Iterator < Curve2 > resultItr = result.iterator();
            while ( resultItr.hasNext() ) {
               final Curve2 curve = resultItr.next();
               final Knot2 first = curve.getFirst();
               final Knot2 last = curve.getLast();

               if ( target.closedLoop ) {
                  Curve2.lerp13(first.coord, last.coord, first.rearHandle);
                  Curve2.lerp13(last.coord, first.coord, last.foreHandle);
               } else {
                  first.mirrorHandlesForward();
                  last.mirrorHandlesBackward();
               }
            } /* End while loop to fix knots for closed loops. */
         } /* End pathData null check. */
      } /* End attributes null check. */

      return result;
   }

   /**
    * Parses a polygon or poly-line node.
    *
    * @param polygonNode the polygon
    * @param target      the output curve
    *
    * @return the curve
    */
   protected static Curve2 parsePoly ( final Node polygonNode,
      final Curve2 target ) {

      final NamedNodeMap attributes = polygonNode.getAttributes();
      if ( attributes != null ) {

         /* Close loop if the node is a polygon. */
         final String name = polygonNode.getNodeName();
         if ( name == "polygon" ) {
            target.name = "Polygon";
            target.closedLoop = true;
         } else if ( name == "polyline" ) {
            target.name = "PolyLine";
            target.closedLoop = false;
         } else {
            target.closedLoop = false;
         }

         final Node ptsnode = attributes.getNamedItem("points");
         final String ptsstr = ptsnode != null ? ptsnode.getNodeValue() : "0,0";
         final String[] coords = ptsstr.split("\\s+|,", 0);

         /* x, y pairs are flattened into a 1D array, so use half length. */
         final int coordLen = coords.length;
         target.resize(coordLen / 2);

         int i = -1;
         final Iterator < Knot2 > itr = target.iterator();
         final Knot2 first = itr.next();
         first.coord.set(ParserSvg2.parseFloat(coords[++i], 0.0f), ParserSvg2
            .parseFloat(coords[++i], 0.0f));

         Knot2 prev = first;
         while ( itr.hasNext() ) {
            final String xstr = coords[++i];
            final String ystr = coords[++i];
            final float x = ParserSvg2.parseFloat(xstr, 0.0f);
            final float y = ParserSvg2.parseFloat(ystr, 0.0f);
            final Knot2 curr = itr.next();
            Knot2.fromSegLinear(x, y, prev, curr);
            prev = curr;
         }

         if ( target.closedLoop ) {
            Knot2.fromSegLinear(first.coord, prev, first);
         } else {
            first.mirrorHandlesForward();
            prev.mirrorHandlesBackward();
         }
      }
      return target;
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
         final String xstr = xnode != null ? xnode.getNodeValue() : "0";
         final String ystr = ynode != null ? ynode.getNodeValue() : "0";
         final String wstr = wnode != null ? wnode.getNodeValue() : "1";
         final String hstr = hnode != null ? hnode.getNodeValue() : "1";
         final String rxstr = rxnode != null ? rxnode.getNodeValue() : "0";
         final String rystr = rynode != null ? rynode.getNodeValue() : "0";

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

      final String v = trNode.getNodeValue().trim().toLowerCase();

      final String[] segStrs = v.split("\\),*", 0);
      final int segLen = segStrs.length;
      for ( int i = 0; i < segLen; ++i ) {
         final String seg = segStrs[i];

         /* Find the command section of the String. */
         final int openParenIdx = seg.indexOf('(', 0);
         final String cmd = seg.substring(0, openParenIdx).trim();
         final int hsh = cmd.hashCode();

         /* Find the data section of the String. */
         final String dataBlock = seg.substring(openParenIdx + 1);
         final String[] data = dataBlock.split(",\\s*", 0);
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
               Mat3.mul(target, delta, target);

               break;

            case -925180581:
               /* "rotate" */
               final String ang = data[0];
               final String xpivstr = dataLen > 1 ? data[1] : "0";
               final String ypivstr = dataLen > 2 ? data[2] : "0";
               final float xpiv = ParserSvg2.parseFloat(xpivstr, 0.0f);
               final float ypiv = ParserSvg2.parseFloat(ypivstr, 0.0f);
               final Vec2 pivot = new Vec2(xpiv, ypiv);

               Mat3.fromTranslation(pivot, delta);
               Mat3.mul(target, delta, target);

               Mat3.fromRotZ(ParserSvg2.parseAngle(ang, 0.0f), delta);
               Mat3.mul(target, delta, target);

               Mat3.fromTranslation(Vec2.negate(pivot, pivot), delta);
               Mat3.mul(target, delta, target);

               break;

            case 109250890:
               /* "scale" */

               final String scx = data[0];
               final String scy = dataLen > 1 ? data[1] : scx;
               Mat3.fromScale(ParserSvg2.parseFloat(scx, 1.0f), ParserSvg2
                  .parseFloat(scy, 1.0f), delta);
               Mat3.mul(target, delta, target);

               break;

            case 109493422:
               /* "skewx" */

               final String skx = data[0];
               Mat3.fromSkewX(ParserSvg2.parseAngle(skx, 0.0f), delta);
               Mat3.mul(target, delta, target);

               break;

            case 109493423:
               /* "skewy" */

               final String sky = data[0];
               Mat3.fromSkewY(ParserSvg2.parseAngle(sky, 0.0f), delta);
               Mat3.mul(target, delta, target);

               break;

            case 1052832078:
               /* "translate" */

               final String tx = data[0];
               final String ty = dataLen > 1 ? data[1] : "0";
               Mat3.fromTranslation(ParserSvg2.parseFloat(tx, 0.0f), ParserSvg2
                  .parseFloat(ty, 0.0f), delta);
               Mat3.mul(target, delta, target);

               break;

            default:

         }

      }

      return target;
   }

   /**
    * Strips empty tokens out of an array of strings.
    *
    * @param tokens the tokens
    *
    * @return the stripped tokens
    */
   protected static String[] stripEmptyTokens ( final String[] tokens ) {

      final int len = tokens.length;
      final ArrayList < String > list = new ArrayList <>(len);
      for ( int i = 0; i < len; ++i ) {
         final String token = tokens[i];
         if ( token.length() > 0 ) { list.add(token); }
      }
      return list.toArray(new String[list.size()]);
   }

}
