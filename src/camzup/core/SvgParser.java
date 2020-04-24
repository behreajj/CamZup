package camzup.core;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A very basic scalable vector graphics (SVG) parser class.
 */
@Experimental
public abstract class SvgParser {

   /**
    * Ratio to convert from centimeters to units,
    * {@value SvgParser#CM_TO_UNIT}. In Processing, the value was 35.43307 .
    */
   public static final float CM_TO_UNIT = 37.795f;

   /**
    * Regular expression to find path commands in an SVG path data attribute.
    */
   public static final String CMD_PATTERN
      = "[^A|a|C|c|H|h|L|l|M|m|Q|q|S|sT|t|V|v|Z|z]++";

   /**
    * Regular expression to find data elements in an SVG path data attribute.
    */
   public static final String DATA_PATTERN
      = "[A|a|C|c|H|h|L|l|M|m|Q|q|S|sT|t|V|v|Z|z|,|\u0020]";

   /**
    * Ratio to convert from gradians to radians,
    * {@value SvgParser#GRAD_TO_RAD}.
    */
   public static final float GRAD_TO_RAD = 0.015708f;

   /**
    * Handle magnitude of curve knots used to approximate an ellipse,
    * {@value SvgParser#HANDLE_MAG}.
    */
   public static final float HANDLE_MAG = 0.552285f;

   /**
    * Ratio to convert from inches to units, {@value SvgParser#IN_TO_UNIT}. In
    * Processing, the value was 90.0 .
    */
   public static final float IN_TO_UNIT = 96.0f;

   /**
    * Ratio to convert from millimeters to units,
    * {@value SvgParser#MM_TO_UNIT}. In Processing, the value was 3.543307 .
    */
   public static final float MM_TO_UNIT = 3.7795f;

   /**
    * Ratio to convert from picas to units, {@value SvgParser#PC_TO_UNIT}. In
    * Processing, the value was 15.0 .
    */
   public static final float PC_TO_UNIT = 16.0f;

   /**
    * Ratio to convert from points to units, {@value SvgParser#PT_TO_UNIT}. In
    * Processing the value was 1.25 .
    */
   public static final float PT_TO_UNIT = IUtils.FOUR_THIRDS;

   /**
    * Ratio to convert from pixels to units, {@value SvgParser#PX_TO_UNIT}.
    */
   public static final float PX_TO_UNIT = 1.0f;

   /**
    * Attempts to parse an SVG file to a curve entity.
    *
    * @param fileName the SVG file name
    *
    * @return the curve entity
    */
   public static CurveEntity2 parse ( final String fileName ) {

      final CurveEntity2 result = new CurveEntity2();

      try {
         final DocumentBuilder db
            = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         final File file = new File(fileName);
         final Document doc = db.parse(file);
         doc.normalizeDocument();

         final Node header = doc.getFirstChild();
         final NodeList nodes = header.getChildNodes();
         final int nodeLen = nodes.getLength();
         final Mat3 root = new Mat3();
         for ( int i = 0; i < nodeLen; ++i ) {
            final Node node = nodes.item(i);
            SvgParser.parseNode(node, result, root);
         }

         final NamedNodeMap attr = header.getAttributes();

         final Node width = attr.getNamedItem("width");
         final String widthStr = width != null ? width.getTextContent() : "0.0";
         final float widpx = SvgParser.parseFloat(widthStr);

         final Node height = attr.getNamedItem("height");
         final String heightStr
            = height != null ? height.getTextContent() : "0.0";
         final float hghpx = SvgParser.parseFloat(heightStr);

         final Node viewbox = attr.getNamedItem("viewBox");
         final Vec4 vbVec = new Vec4(0.0f, 0.0f, 0.0f, 0.0f);
         if ( viewbox != null ) { SvgParser.parseViewBox(viewbox, vbVec); }
         final float widvb = Utils.diff(vbVec.z, vbVec.x);
         final float hghvb = Utils.diff(vbVec.w, vbVec.y);

         final Vec2 shift = new Vec2();
         shift.set(Utils.max(widpx, widvb) * -0.5f,
            Utils.max(hghpx, hghvb) * -0.5f);

         if ( Vec2.any(shift) ) {
            for ( final Curve2 curve : result ) {
               curve.translate(shift);
            }

            Vec2.negate(shift, shift);
            result.moveTo(shift);
         }

      } catch ( final Exception e ) {
         System.err.print(e);
         e.printStackTrace();
      }

      return result;
   }

   /**
    * A helper function to parse an angle to radians. The default is assumed
    * to be degrees.
    *
    * @param v the input value
    *
    * @return the angle in radians
    */
   public static float parseAngle ( final String v ) {

      return SvgParser.parseAngle(v, 0.0f);
   }

   /**
    * A helper function to parse an angle to radians. The default is assumed
    * to be degrees.
    *
    * @param v   the input value
    * @param def the default
    *
    * @return the angle in radians
    */
   public static float parseAngle ( final String v, final float def ) {

      float x = def;
      final int len = v.length() - 3;

      try {

         if ( v.endsWith("deg") ) {

            x = Float.parseFloat(v.substring(0, len)) * IUtils.DEG_TO_RAD;

         } else if ( v.endsWith("grad") ) {

            x = Float.parseFloat(v.substring(0, len - 1))
               * SvgParser.GRAD_TO_RAD;

         } else if ( v.endsWith("rad") ) {

            x = Float.parseFloat(v.substring(0, len));

         } else if ( v.endsWith("turn") ) {

            x = Float.parseFloat(v.substring(0, len - 1)) * IUtils.TAU;

         } else {

            /* Degrees is the SVG default? */
            x = Float.parseFloat(v) * IUtils.DEG_TO_RAD;

         }

      } catch ( final NumberFormatException e ) {
         x = def;
      }

      return x;
   }

   /**
    * Attempts to parse a string representing a Cascading Style Sheet (CSS)
    * representation of an RGBA color. The input string is expected to be in
    * one of the formats:
    * <ul>
    * <li>"rgb(255, 127, 54)"</li>
    * <li>"rgb(100%, 50%, 25%)"</li>
    * <li>"rgba(255, 127, 54, 0.125)"</li>
    * <li>"rgba(100%, 50%, 25%, 0.125)"</li>
    * </ul>
    * The default color is (0.0, 0.0, 0.0, 1.0).
    *
    * @param str    the string
    * @param target the output color
    *
    * @return the color
    */
   public static Color parseCssRgba ( final String str, final Color target ) {

      final String vstr = str.toLowerCase().trim();
      final int lParen = vstr.indexOf('(');
      final int rParen = vstr.indexOf(')');
      final String[] tokens = vstr.substring(lParen + 1, rParen).split(",\\s+");
      final int tokensLen = tokens.length;

      float r = 0.0f;
      float g = 0.0f;
      float b = 0.0f;
      float a = 1.0f;
      String currToken = "";

      /* Parse alpha. */
      if ( tokensLen > 3 ) {
         currToken = tokens[3];
         if ( currToken.endsWith("%") ) {
            currToken = currToken.substring(0, currToken.length() - 1);
            a = Float.parseFloat(currToken) * 0.01f;
         } else {
            a = Float.parseFloat(currToken);
         }
         a = Utils.clamp01(a);
      }

      /* Parse blue. */
      if ( tokensLen > 2 ) {
         currToken = tokens[2];
         if ( currToken.endsWith("%") ) {
            currToken = currToken.substring(0, currToken.length() - 1);
            b = Float.parseFloat(currToken) * 0.01f;
         } else {
            b = Float.parseFloat(currToken) * IUtils.ONE_255;
         }
         b = Utils.clamp01(b);
      }

      /* Parse green. */
      if ( tokensLen > 1 ) {
         currToken = tokens[1];
         if ( currToken.endsWith("%") ) {
            currToken = currToken.substring(0, currToken.length() - 1);
            g = Float.parseFloat(currToken) * 0.01f;
         } else {
            g = Float.parseFloat(currToken) * IUtils.ONE_255;
         }
         g = Utils.clamp01(g);
      }

      /* Parse red. */
      if ( tokensLen > 0 ) {
         currToken = tokens[0];
         if ( currToken.endsWith("%") ) {
            currToken = currToken.substring(0, currToken.length() - 1);
            r = Float.parseFloat(currToken) * 0.01f;
         } else {
            r = Float.parseFloat(currToken) * IUtils.ONE_255;
         }
         r = Utils.clamp01(r);
      }

      return target.set(r, g, b, a);
   }

   /**
    * Parses a SVG node and returns a Curve2 approximating a circle.
    *
    * @param ellipseNode the ellipse node
    * @param target      the output curve
    *
    * @return the ellipse curve
    */
   public static Curve2 parseEllipse ( final Node ellipseNode,
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
         if ( rxnode == null && rynode != null ) {
            rxnode = rynode;
         } else if ( rxnode != null && rynode == null ) {
            rynode = rxnode;
         } else if ( rxnode == null && rynode == null ) {
            rxnode = rynode = attributes.getNamedItem("r");
         }

         /* Acquire text content from the node if it exists. */
         final String cxstr = cxnode != null ? cxnode.getTextContent() : "0.0";
         final String cystr = cynode != null ? cynode.getTextContent() : "0.0";
         final String rxstr = rxnode != null ? rxnode.getTextContent() : "0.5";
         final String rystr = rynode != null ? rynode.getTextContent() : "0.5";

         /* Parse string or default. */
         final float cx = SvgParser.parseFloat(cxstr, 0.0f);
         final float cy = SvgParser.parseFloat(cystr, 0.0f);
         final float rx = SvgParser.parseFloat(rxstr, 0.5f);
         final float ry = SvgParser.parseFloat(rystr, 0.5f);

         /* Find cardinal control points. */
         final float right = cx + rx;
         final float top = cy + ry;
         final float left = cx - rx;
         final float bottom = cy - ry;

         final float horizHandle = rx * SvgParser.HANDLE_MAG;
         final float vertHandle = ry * SvgParser.HANDLE_MAG;

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
      } catch ( final NumberFormatException e ) {
         x = 0;
      }
      return x != 0;
   }

   /**
    * A helper function to parse units of measurement in an SVG element. The
    * default value if the string cannot be parsed is zero.
    *
    * @param v the value
    *
    * @return the floating point number
    */
   public static float parseFloat ( final String v ) {

      return SvgParser.parseFloat(v, 0.0f);
   }

   /**
    * A helper function to parse units of measurement in an SVG element. Uses
    * the following conversions:
    * <ul>
    * <li>1 centimeter (cm) = {@value SvgParser#CM_TO_UNIT} units</li>
    * <li>1 inch (in) = {@value SvgParser#IN_TO_UNIT} units</li>
    * <li>1 millimeter (mm) = {@value SvgParser#MM_TO_UNIT} units</li>
    * <li>1 pica (pc) = {@value SvgParser#PC_TO_UNIT} units</li>
    * <li>1 point (pt) = {@value SvgParser#PT_TO_UNIT} units</li>
    * <li>1 pixel (px) = {@value SvgParser#PX_TO_UNIT} units</li>
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
    * @param v   the String value
    * @param def the default value
    *
    * @return the parsed float
    */
   public static float parseFloat ( final String v, final float def ) {

      float x = def;
      final int len = v.length() - 2;

      try {

         if ( v.endsWith("cm") ) {

            /* Centimeters. */
            x = Float.parseFloat(v.substring(0, len)) * SvgParser.CM_TO_UNIT;

         } else if ( v.endsWith("deg") ) {

            /* Degrees. */
            x = Float.parseFloat(v.substring(0, len - 1)) * IUtils.DEG_TO_RAD;

         } else if ( v.endsWith("in") ) {

            /* Inches. */
            x = Float.parseFloat(v.substring(0, len)) * SvgParser.IN_TO_UNIT;

         } else if ( v.endsWith("mm") ) {

            /* Millimeters. */
            x = Float.parseFloat(v.substring(0, len)) * SvgParser.MM_TO_UNIT;

         } else if ( v.endsWith("pc") ) {

            /* Pica. */
            x = Float.parseFloat(v.substring(0, len)) * SvgParser.PC_TO_UNIT;

         } else if ( v.endsWith("pt") ) {

            /* Point. */
            x = Float.parseFloat(v.substring(0, len)) * SvgParser.PT_TO_UNIT;

         } else if ( v.endsWith("px") ) {

            /* Pixel. */
            x = Float.parseFloat(v.substring(0, len)) * SvgParser.PX_TO_UNIT;

         } else if ( v.endsWith("rad") ) {

            /* Radians. */
            x = Float.parseFloat(v.substring(0, len - 1));

         } else if ( v.endsWith("em") ) {

            /* RELATIVE UNIT: To font size. Not supported in original. */
            x = Float.parseFloat(v.substring(0, len));

         } else if ( v.endsWith("ex") ) {

            /* RELATIVE UNIT: To font size. Not supported in original. */
            x = Float.parseFloat(v.substring(0, len));

         } else if ( v.endsWith("%") ) {

            /* RELATIVE UNIT: Simplified from original. */
            x = Float.parseFloat(v.substring(0, len + 1)) * 0.01f;

         } else {

            x = Float.parseFloat(v);

         }

      } catch ( final NumberFormatException e ) {
         x = def;
      }

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
   public static Curve2 parseLine ( final Node lineNode, final Curve2 target ) {

      final NamedNodeMap attributes = lineNode.getAttributes();
      if ( attributes != null ) {

         /* Search for attribute nodes. May return null. */
         final Node x1node = attributes.getNamedItem("x1");
         final Node y1node = attributes.getNamedItem("y1");
         final Node x2node = attributes.getNamedItem("x2");
         final Node y2node = attributes.getNamedItem("y2");

         /* Acquire text content from the node if it exists. */
         final String x1str = x1node != null ? x1node.getTextContent() : "-0.5";
         final String y1str = y1node != null ? y1node.getTextContent() : "0.0";
         final String x2str = x2node != null ? x2node.getTextContent() : "0.5";
         final String y2str = y2node != null ? y2node.getTextContent() : "0.0";

         /* Parse string or default. */
         final float x1 = SvgParser.parseFloat(x1str, -0.5f);
         final float y1 = -SvgParser.parseFloat(y1str, 0.0f);
         final float x2 = SvgParser.parseFloat(x2str, 0.5f);
         final float y2 = -SvgParser.parseFloat(y2str, 0.0f);

         Curve2.line(x1, y1, x2, y2, target);
      }
      return target;
   }

   /**
    * Parses an SVG node in a recursive fashion.
    *
    * @param node   the node
    * @param target the output curve entity
    * @param parent the parent matrix
    *
    * @return the output curve entity
    */
   public static ArrayList < Curve2 > parseNode ( final Node node,
      final CurveEntity2 target, final Mat3 parent ) {

      ArrayList < Curve2 > newCurves = new ArrayList <>();

      /* Check for transform. */
      final Mat3 current = new Mat3(parent);

      final NamedNodeMap attributes = node.getAttributes();
      if ( attributes != null ) {
         final Node transform = attributes.getNamedItem("transform");
         if ( transform != null ) {
            SvgParser.parseTransform(transform, current);
         }
      }

      final String name = node.getNodeName().toLowerCase();
      switch ( name ) {

         case "circle":
         case "ellipse":
            newCurves.add(SvgParser.parseEllipse(node, new Curve2()));
            break;

         case "line":
            newCurves.add(SvgParser.parseLine(node, new Curve2()));
            break;

         case "path":
            final Curve2[] cs = SvgParser.parsePath(node);
            for ( int i = 0; i < cs.length; ++i ) {
               newCurves.add(cs[i]);
            }
            break;

         case "polygon":
         case "polyline":
            newCurves.add(SvgParser.parsePoly(node, new Curve2()));
            break;

         case "rect":
            newCurves.add(SvgParser.parseRect(node, new Curve2()));
            break;

      }

      /* Apply transform to curves from this node. */
      for ( final Curve2 curve : newCurves ) {
         curve.transform(current);
      }

      /* Append new curves to target. */
      target.appendAll(newCurves);

      /* Iterate over children. */
      final NodeList children = node.getChildNodes();
      final int childLen = children.getLength();
      for ( int i = 0; i < childLen; ++i ) {
         final Node child = children.item(i);
         newCurves = SvgParser.parseNode(child, target, current);
      }

      /* Revert transform to parent. */
      current.set(parent);
      return newCurves;
   }

   /**
    * Parses a path node, returning an array of curves.<br>
    * <br>
    * Arc to commands are not currently supported.
    *
    * @param node the node
    *
    * @return the array of curves
    */
   public static Curve2[] parsePath ( final Node node ) {

      final ArrayList < Curve2 > result = new ArrayList <>(2);

      final NamedNodeMap attributes = node.getAttributes();
      if ( attributes != null ) {
         final Node pathData = attributes.getNamedItem("d");
         if ( pathData != null ) {

            /*
             * These regular expressions are imperfect, and may yield empty
             * string tokens, so there's an extra step to strip them away.
             */
            final String pdStr = pathData.getTextContent();
            String[] cmdTokens = pdStr.split(SvgParser.CMD_PATTERN);
            String[] dataTokens = pdStr.split(SvgParser.DATA_PATTERN);
            cmdTokens = SvgParser.stripEmptyTokens(cmdTokens);
            dataTokens = SvgParser.stripEmptyTokens(dataTokens);

            /* Convert command strings to path command constants. */
            final int cmdLen = cmdTokens.length;
            final PathCommand[] commands = new PathCommand[cmdLen];
            for ( int j = 0; j < cmdLen; ++j ) {
               final String cmdToken = cmdTokens[j];
               final char cmdCode = cmdToken.charAt(0);
               final PathCommand cmd = PathCommand.fromChar(cmdCode);
               commands[j] = cmd;
               // System.out.println(cmd);
            }

            /* Current, previous knots. */
            Knot2 curr = null;
            Knot2 prev = null;

            /* Coordinate token. */
            String coxStr = "0.0";
            String coyStr = "0.0";

            /* Cubic curve rear handle. */
            String rhxStr = "0.0";
            String rhyStr = "0.0";

            /* Cubic curve fore handle. */
            String fhxStr = "0.0";
            String fhyStr = "0.0";

            /* Quadratic curve midpoint. */
            String mhxStr = "0.0";
            String mhyStr = "0.0";

            /* Arc parameters. Flags are either 0 or 1. */
            @SuppressWarnings ( "unused" )
            String rxStr = "0.0";
            @SuppressWarnings ( "unused" )
            String ryStr = "0.0";
            @SuppressWarnings ( "unused" )
            String angStr = "0.0";
            @SuppressWarnings ( "unused" )
            String largeArcFlag = "0";
            @SuppressWarnings ( "unused" )
            String sweepFlag = "0";

            final Vec2 relative = new Vec2();
            final Vec2 mh = new Vec2();
            float xOff = 0.0f;
            float yOff = 0.0f;
            boolean closedLoop = false;

            /*
             * Usually, one path should have one move to command, but just in
             * case it doesn't, create a boolean to track an initial move to. If
             * there is more than one, the target curve will be added to the
             * result list and replaced by a new one.
             */
            boolean initialMove = true;
            Curve2 target = new Curve2();
            target.clear();
            result.add(target);

            for ( int l = 0, cursor = 0; l < cmdLen; ++l ) {
               final PathCommand command = commands[l];
               switch ( command ) {

                  case ClosePath:

                     closedLoop = true;
                     target.closedLoop = closedLoop;

                     break;

                  case MoveToAbs:

                     if ( !initialMove ) {
                        result.add(target);
                        target = new Curve2();
                        target.clear();
                     }
                     initialMove = false;

                     coxStr = dataTokens[cursor++]; /* 1 */
                     coyStr = dataTokens[cursor++]; /* 2 */

                     curr = new Knot2();
                     target.append(curr);
                     curr.coord.set(SvgParser.parseFloat(coxStr),
                        SvgParser.parseFloat(coyStr));

                     xOff
                        = Utils.copySign(IUtils.DEFAULT_EPSILON, curr.coord.x);
                     yOff
                        = Utils.copySign(IUtils.DEFAULT_EPSILON, curr.coord.y);

                     curr.foreHandle.set(curr.coord.x + xOff,
                        curr.coord.y + yOff);
                     curr.rearHandle.set(curr.coord.x - xOff,
                        curr.coord.y - yOff);

                     relative.set(curr.coord);

                     break;

                  case MoveToRel:

                     if ( !initialMove ) {
                        result.add(target);
                        target = new Curve2();
                        target.clear();
                     }
                     initialMove = false;

                     coxStr = dataTokens[cursor++]; /* 1 */
                     coyStr = dataTokens[cursor++]; /* 2 */

                     curr = new Knot2();
                     target.append(curr);

                     curr.coord.set(SvgParser.parseFloat(coxStr),
                        SvgParser.parseFloat(coyStr));
                     Vec2.add(relative, curr.coord, curr.coord);

                     xOff
                        = Utils.copySign(IUtils.DEFAULT_EPSILON, curr.coord.x);
                     yOff
                        = Utils.copySign(IUtils.DEFAULT_EPSILON, curr.coord.y);

                     curr.foreHandle.set(curr.coord.x + xOff,
                        curr.coord.y + yOff);
                     curr.rearHandle.set(curr.coord.x - xOff,
                        curr.coord.y - yOff);

                     relative.set(curr.coord);

                     break;

                  case LineToAbs:

                     coxStr = dataTokens[cursor++]; /* 1 */
                     coyStr = dataTokens[cursor++]; /* 2 */

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     curr.coord.set(SvgParser.parseFloat(coxStr),
                        SvgParser.parseFloat(coyStr));
                     Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                     Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                     relative.set(curr.coord);

                     break;

                  case LineToRel:

                     coxStr = dataTokens[cursor++]; /* 1 */
                     coyStr = dataTokens[cursor++]; /* 2 */

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     curr.coord.set(SvgParser.parseFloat(coxStr),
                        SvgParser.parseFloat(coyStr));
                     Vec2.add(relative, curr.coord, curr.coord);

                     Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                     Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                     relative.set(curr.coord);

                     break;

                  case HorizAbs:

                     coxStr = dataTokens[cursor++]; /* 1 */

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     curr.coord.x = SvgParser.parseFloat(coxStr, 0.0f);
                     curr.coord.y = prev.coord.y;
                     Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                     Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                     relative.set(curr.coord);

                     break;

                  case HorizRel:

                     coxStr = dataTokens[cursor++]; /* 1 */

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     curr.coord.x
                        = relative.x + SvgParser.parseFloat(coxStr, 0.0f);
                     curr.coord.y = prev.coord.y;

                     Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                     Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                     relative.set(curr.coord);

                     break;

                  case VertAbs:

                     coyStr = dataTokens[cursor++]; /* 1 */

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     curr.coord.x = prev.coord.x;
                     curr.coord.y = SvgParser.parseFloat(coyStr, 0.0f);
                     Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                     Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                     relative.set(curr.coord);

                     break;

                  case VertRel:

                     coyStr = dataTokens[cursor++]; /* 1 */

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     /* @formatter:off */
                     curr.coord.x = prev.coord.x;
                     curr.coord.y = relative.y + SvgParser.parseFloat(
                        coyStr, 0.0f);
                     /* @formatter:on */

                     Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                     Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                     relative.set(curr.coord);

                     break;

                  case QuadraticToAbs:

                     mhxStr = dataTokens[cursor++]; /* 1 */
                     mhyStr = dataTokens[cursor++]; /* 2 */
                     coxStr = dataTokens[cursor++]; /* 3 */
                     coyStr = dataTokens[cursor++]; /* 4 */

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     mh.set(SvgParser.parseFloat(mhxStr),
                        SvgParser.parseFloat(mhyStr));
                     curr.coord.set(SvgParser.parseFloat(coxStr),
                        SvgParser.parseFloat(coyStr));
                     Curve2.lerp13(mh, prev.coord, prev.foreHandle);
                     Curve2.lerp13(mh, curr.coord, curr.rearHandle);

                     relative.set(curr.coord);

                     break;

                  case QuadraticToRel:

                     mhxStr = dataTokens[cursor++]; /* 1 */
                     mhyStr = dataTokens[cursor++]; /* 2 */
                     coxStr = dataTokens[cursor++]; /* 3 */
                     coyStr = dataTokens[cursor++]; /* 4 */

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     mh.set(SvgParser.parseFloat(mhxStr),
                        SvgParser.parseFloat(mhyStr));
                     curr.coord.set(SvgParser.parseFloat(coxStr),
                        SvgParser.parseFloat(coyStr));

                     Vec2.add(relative, mh, mh);
                     Vec2.add(relative, curr.coord, curr.coord);

                     Curve2.lerp13(mh, prev.coord, prev.foreHandle);
                     Curve2.lerp13(mh, curr.coord, curr.rearHandle);

                     relative.set(curr.coord);

                     break;

                  case ReflectQuadraticAbs:

                     coxStr = dataTokens[cursor++]; /* 1 */
                     coyStr = dataTokens[cursor++]; /* 2 */

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     prev.mirrorHandlesBackward();
                     curr.coord.set(SvgParser.parseFloat(coxStr),
                        SvgParser.parseFloat(coyStr));

                     /*
                      * Convert mid-handle from point to direction, negate,
                      * convert back to point.
                      */
                     Vec2.sub(mh, prev.coord, mh);
                     Vec2.negate(mh, mh);
                     Vec2.add(mh, prev.coord, mh);
                     Curve2.lerp13(mh, curr.coord, curr.rearHandle);

                     relative.set(curr.coord);

                     break;

                  case ReflectQuadraticRel:

                     coxStr = dataTokens[cursor++]; /* 1 */
                     coyStr = dataTokens[cursor++]; /* 2 */

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     prev.mirrorHandlesBackward();
                     curr.coord.set(SvgParser.parseFloat(coxStr),
                        SvgParser.parseFloat(coyStr));
                     Vec2.add(relative, curr.coord, curr.coord);

                     Vec2.sub(mh, prev.coord, mh);
                     Vec2.negate(mh, mh);
                     Vec2.add(mh, prev.coord, mh);
                     Curve2.lerp13(mh, curr.coord, curr.rearHandle);

                     relative.set(curr.coord);

                     break;

                  case CubicToAbs:

                     fhxStr = dataTokens[cursor++]; /* 1 */
                     fhyStr = dataTokens[cursor++]; /* 2 */
                     rhxStr = dataTokens[cursor++]; /* 3 */
                     rhyStr = dataTokens[cursor++]; /* 4 */
                     coxStr = dataTokens[cursor++]; /* 5 */
                     coyStr = dataTokens[cursor++]; /* 6 */

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     prev.foreHandle.set(SvgParser.parseFloat(fhxStr),
                        SvgParser.parseFloat(fhyStr));
                     curr.rearHandle.set(SvgParser.parseFloat(rhxStr),
                        SvgParser.parseFloat(rhyStr));
                     curr.coord.set(SvgParser.parseFloat(coxStr),
                        SvgParser.parseFloat(coyStr));

                     relative.set(curr.coord);

                     break;

                  case CubicToRel:

                     fhxStr = dataTokens[cursor++]; /* 1 */
                     fhyStr = dataTokens[cursor++]; /* 2 */
                     rhxStr = dataTokens[cursor++]; /* 3 */
                     rhyStr = dataTokens[cursor++]; /* 4 */
                     coxStr = dataTokens[cursor++]; /* 5 */
                     coyStr = dataTokens[cursor++]; /* 6 */

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     prev.foreHandle.set(SvgParser.parseFloat(fhxStr),
                        SvgParser.parseFloat(fhyStr));
                     curr.rearHandle.set(SvgParser.parseFloat(rhxStr),
                        SvgParser.parseFloat(rhyStr));
                     curr.coord.set(SvgParser.parseFloat(coxStr),
                        SvgParser.parseFloat(coyStr));

                     Vec2.add(relative, prev.foreHandle, prev.foreHandle);
                     Vec2.add(relative, curr.rearHandle, curr.rearHandle);
                     Vec2.add(relative, curr.coord, curr.coord);

                     relative.set(curr.coord);

                     break;

                  case ReflectCubicAbs:

                     rhxStr = dataTokens[cursor++]; /* 1 */
                     rhyStr = dataTokens[cursor++]; /* 2 */
                     coxStr = dataTokens[cursor++]; /* 3 */
                     coyStr = dataTokens[cursor++]; /* 4 */

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     prev.mirrorHandlesBackward();
                     curr.rearHandle.set(SvgParser.parseFloat(rhxStr),
                        SvgParser.parseFloat(rhyStr));
                     curr.coord.set(SvgParser.parseFloat(coxStr),
                        SvgParser.parseFloat(coyStr));

                     relative.set(curr.coord);

                     break;

                  case ReflectCubicRel:

                     rhxStr = dataTokens[cursor++]; /* 1 */
                     rhyStr = dataTokens[cursor++]; /* 2 */
                     coxStr = dataTokens[cursor++]; /* 3 */
                     coyStr = dataTokens[cursor++]; /* 4 */

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     prev.mirrorHandlesBackward();
                     curr.rearHandle.set(SvgParser.parseFloat(rhxStr),
                        SvgParser.parseFloat(rhyStr));
                     curr.coord.set(SvgParser.parseFloat(coxStr),
                        SvgParser.parseFloat(coyStr));

                     Vec2.add(relative, curr.rearHandle, curr.rearHandle);
                     Vec2.add(relative, curr.coord, curr.coord);

                     relative.set(curr.coord);

                     break;

                  case ArcToAbs:

                     /* Unsupported. */

                     rxStr = dataTokens[cursor++]; /* 1 */
                     ryStr = dataTokens[cursor++]; /* 2 */
                     angStr = dataTokens[cursor++]; /* 3 */
                     largeArcFlag = dataTokens[cursor++]; /* 4 */
                     sweepFlag = dataTokens[cursor++]; /* 5 */
                     coxStr = dataTokens[cursor++]; /* 6 */
                     coyStr = dataTokens[cursor++]; /* 7 */

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     curr.coord.set(SvgParser.parseFloat(coxStr),
                        SvgParser.parseFloat(coyStr));
                     Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                     Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                     relative.set(curr.coord);

                     break;

                  case ArcToRel:

                     /* Unsupported. */

                     rxStr = dataTokens[cursor++]; /* 1 */
                     ryStr = dataTokens[cursor++]; /* 2 */
                     angStr = dataTokens[cursor++]; /* 3 */
                     largeArcFlag = dataTokens[cursor++]; /* 4 */
                     sweepFlag = dataTokens[cursor++]; /* 5 */
                     coxStr = dataTokens[cursor++]; /* 6 */
                     coyStr = dataTokens[cursor++]; /* 7 */

                     prev = curr;
                     curr = new Knot2();
                     target.append(curr);

                     curr.coord.set(SvgParser.parseFloat(coxStr),
                        SvgParser.parseFloat(coyStr));
                     Vec2.add(relative, curr.coord, curr.coord);

                     Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                     Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                     relative.set(curr.coord);

                     break;

                  default:

               }

            }

            /*
             * Append final curve recorded by while loop if it hasn't been
             * added; for multiple "sub-path"s which are not closed.
             */
            if ( !result.contains(target) ) { result.add(target); }

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
            }
         }
      }

      return result.toArray(new Curve2[result.size()]);
   }

   /**
    * Parses a polygon or poly-line node.
    *
    * @param polygonNode the polygon
    * @param target      the output curve
    *
    * @return the curve
    */
   public static Curve2 parsePoly ( final Node polygonNode,
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
         final String ptsstr
            = ptsnode != null ? ptsnode.getTextContent() : "0,0";
         final String[] coords = ptsstr.split(" |,");

         int i = 0;
         final int coordLen = coords.length;
         final int halfLen = coordLen / 2;
         target.resize(halfLen);
         final Iterator < Knot2 > itr = target.iterator();
         final Knot2 first = itr.next();
         first.coord.set(SvgParser.parseFloat(coords[i++], 0.0f),
            SvgParser.parseFloat(coords[i++], 0.0f));

         Knot2 prev = first;
         while ( itr.hasNext() ) {
            final String xstr = coords[i++];
            final String ystr = coords[i++];
            final float x = SvgParser.parseFloat(xstr, 0.0f);
            final float y = SvgParser.parseFloat(ystr, 0.0f);
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
   public static Curve2 parseRect ( final Node rectNode, final Curve2 target ) {

      final NamedNodeMap attributes = rectNode.getAttributes();
      if ( attributes != null ) {

         /* Search for property nodes. May return null. */
         final Node xnode = attributes.getNamedItem("x");
         final Node ynode = attributes.getNamedItem("y");
         final Node wnode = attributes.getNamedItem("width");
         final Node hnode = attributes.getNamedItem("height");
         final Node rxnode = attributes.getNamedItem("rx");
         final Node rynode = attributes.getNamedItem("ry");

         /* Acquire text content from the node if it exists. */
         final String xstr = xnode != null ? xnode.getTextContent() : "0.0";
         final String ystr = ynode != null ? ynode.getTextContent() : "0.0";
         final String wstr = wnode != null ? wnode.getTextContent() : "1.0";
         final String hstr = hnode != null ? hnode.getTextContent() : "1.0";
         final String rxstr = rxnode != null ? rxnode.getTextContent() : "0.0";
         final String rystr = rynode != null ? rynode.getTextContent() : "0.0";

         /* Parse string or default. */
         final float x = SvgParser.parseFloat(xstr, 0.0f);
         final float y = SvgParser.parseFloat(ystr, 0.0f);
         final float w = SvgParser.parseFloat(wstr, 1.0f);
         final float h = SvgParser.parseFloat(hstr, 1.0f);
         final float rx = SvgParser.parseFloat(rxstr, 0.0f);
         final float ry = SvgParser.parseFloat(rystr, 0.0f);

         /*
          * Corner rounding differs between APIs, so average horizontal and
          * vertical rounding.
          */
         final float rAvg = ( rx + ry ) * 0.5f;
         if ( rAvg < IUtils.DEFAULT_EPSILON ) {
            Curve2.rect(x, y, x + w, y + h, target);
         } else {
            Curve2.rect(x, y, x + w, y + h, rAvg, target);
         }
      }
      return target;
   }

   /**
    * Parses an SVG node containing transform data.
    *
    * @param trNode the transform node
    * @param target the output matrix
    *
    * @return the matrix
    */
   public static Mat3 parseTransform ( final Node trNode, final Mat3 target ) {

      final String v = trNode.getTextContent().trim().toLowerCase();
      final String[] transformStrs = v.split("\\)");
      final int trsLen = transformStrs.length;
      final Mat3 delta = new Mat3();

      for ( int i = 0; i < trsLen; ++i ) {
         final String transform = transformStrs[i].trim();
         final String[] trData = transform.split("\\(|,\\s*");
         final int dataLen = trData.length;
         if ( dataLen > 1 ) {
            final String cmd = trData[0];
            // System.out.println(cmd);

            switch ( cmd ) {

               case "matrix":

                  /* Column major. */
                  final String m00 = trData[1];
                  final String m10 = trData[2];
                  final String m01 = trData[3];
                  final String m11 = trData[4];
                  final String m02 = trData[5];
                  final String m12 = trData[6];

                  delta.set(SvgParser.parseFloat(m00),
                     SvgParser.parseFloat(m01), SvgParser.parseFloat(m02),
                     SvgParser.parseFloat(m10), SvgParser.parseFloat(m11),
                     SvgParser.parseFloat(m12), 0.0f, 0.0f, 1.0f);
                  Mat3.mul(target, delta, target);

                  break;

               case "rotate":

                  /*
                   * SVG rotate also features rotation about an arbitrary pivot,
                   * but that is not supported here.
                   */

                  final String ang = trData[1];

                  Mat3.fromRotZ(SvgParser.parseAngle(ang), delta);
                  Mat3.mul(target, delta, target);

                  break;

               case "scale":

                  final String scx = trData[1];
                  final String scy = dataLen > 2 ? trData[2] : scx;

                  Mat3.fromScale(SvgParser.parseFloat(scx),
                     SvgParser.parseFloat(scy), delta);
                  Mat3.mul(target, delta, target);

                  break;

               case "skewx":

                  final String skx = trData[1];

                  Mat3.fromSkewX(SvgParser.parseAngle(skx), delta);
                  Mat3.mul(target, delta, target);

                  break;

               case "skewy":

                  final String sky = trData[1];

                  Mat3.fromSkewY(SvgParser.parseAngle(sky), delta);
                  Mat3.mul(target, delta, target);

                  break;

               case "translate":

                  final String tx = trData[1];
                  final String ty = dataLen > 2 ? trData[2] : "0.0";

                  Mat3.fromTranslation(SvgParser.parseFloat(tx),
                     SvgParser.parseFloat(ty), delta);
                  Mat3.mul(target, delta, target);

                  break;

            }

         }

      }

      return target;
   }

   /**
    * Parses a view box node to a Vec4.
    *
    * @param viewbox the view box node
    * @param target  the output vector
    *
    * @return the vector
    */
   public static Vec4 parseViewBox ( final Node viewbox, final Vec4 target ) {

      float x = 0.0f;
      float y = 0.0f;
      float z = 0.0f;
      float w = 0.0f;

      final String content = viewbox.getTextContent();
      final String[] tokens = content.split(" ");

      if ( tokens.length > 3 ) { w = SvgParser.parseFloat(tokens[3], 0.0f); }
      if ( tokens.length > 2 ) { z = SvgParser.parseFloat(tokens[2], 0.0f); }
      if ( tokens.length > 1 ) { y = SvgParser.parseFloat(tokens[1], 0.0f); }
      if ( tokens.length > 0 ) { x = SvgParser.parseFloat(tokens[0], 0.0f); }

      return target.set(x, y, z, w);
   }

   /**
    * Strips empty tokens out of an array of strings.
    *
    * @param tokens the tokens
    *
    * @return the stripped tokens
    */
   public static String[] stripEmptyTokens ( final String[] tokens ) {

      final int len = tokens.length;
      final ArrayList < String > list = new ArrayList <>(len);
      for ( int i = 0; i < len; ++i ) {
         final String token = tokens[i].trim();
         if ( token.length() > 0 ) { list.add(token); }
      }
      return list.toArray(new String[list.size()]);
   }

   /**
    * Command found in a path node's d attribute.
    */
   public enum PathCommand {

      /**
       * Arc absolute ('A').
       */
      ArcToAbs ( 'A', false, 7 ),

      /**
       * Arc relative ('a').
       */
      ArcToRel ( 'a', true, 7 ),

      /**
       * Close path ('Z').
       */
      ClosePath ( 'Z', false, 0 ),

      /**
       * Cubic Bezier Curve absolute ('C').
       */
      CubicToAbs ( 'C', false, 6 ),

      /**
       * Cubic Bezier Curve relative ('c').
       */
      CubicToRel ( 'c', true, 6 ),

      /**
       * Horizontal line absolute ('H').
       */
      HorizAbs ( 'H', false, 1 ),

      /**
       * Horizontal line relative ('h').
       */
      HorizRel ( 'h', true, 1 ),

      /**
       * Line to absolute ('L').
       */
      LineToAbs ( 'L', false, 2 ),

      /**
       * Line to relative ('l).
       */
      LineToRel ( 'l', true, 2 ),

      /**
       * Move to absolute ('M').
       */
      MoveToAbs ( 'M', false, 2 ),

      /**
       * Move to relative ('m').
       */
      MoveToRel ( 'm', true, 2 ),

      /**
       * Quadratic Bezier curve absolute ('Q').
       */
      QuadraticToAbs ( 'Q', false, 4 ),

      /**
       * Quadratic Bezier curve relative ('q').
       */
      QuadraticToRel ( 'q', true, 4 ),

      /**
       * Reflect cubic Bezier curve absolute ('S').
       */
      ReflectCubicAbs ( 'S', false, 4 ),

      /**
       * Reflect cubic Bezier curve relative ('s').
       */
      ReflectCubicRel ( 's', true, 4 ),

      /**
       * Reflect quadratic Bezier curve absolute ('T').
       */
      ReflectQuadraticAbs ( 'T', false, 2 ),

      /**
       * Reflect quadratic Bezier curve relative ('t').
       */
      ReflectQuadraticRel ( 't', true, 2 ),

      /**
       * Vertical line absolute ('V').
       */
      VertAbs ( 'V', false, 1 ),

      /**
       * Vertical line to relative ('v').
       */
      VertRel ( 'v', true, 1 );

      /**
       * The single character code.
       */
      private final char code;

      /**
       * Number of parameters for a given code.
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
      private PathCommand ( final char code, final boolean isRelative,
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

         switch ( c ) {
            case 'A':
               return ArcToAbs;

            case 'a':
               return ArcToRel;

            case 'C':
               return CubicToAbs;

            case 'c':
               return CubicToRel;

            case 'H':
               return HorizAbs;

            case 'h':
               return HorizRel;

            case 'L':
               return LineToAbs;

            case 'l':
               return LineToRel;

            case 'M':
               return MoveToAbs;

            case 'm':
               return MoveToRel;

            case 'Q':
               return QuadraticToAbs;

            case 'q':
               return QuadraticToRel;

            case 'S':
               return ReflectCubicAbs;

            case 's':
               return ReflectCubicRel;

            case 'T':
               return ReflectQuadraticAbs;

            case 't':
               return ReflectQuadraticRel;

            case 'V':
               return VertAbs;

            case 'v':
               return VertRel;

            case 'Z':
            case 'z':
            default:
               return ClosePath;
         }
      }

   }

}
