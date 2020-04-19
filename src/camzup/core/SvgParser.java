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
 * A draft scalable vector graphics (SVG) parser class. Currently unfinished.
 */
@Experimental
public abstract class SvgParser {

   /**
    * Ratio to convert from centimeters to units. In Processing, the value was
    * 35.43307 .
    */
   public static final float CM_TO_UNIT = 37.795f;

   /**
    * Regular expression to find path commands in an SVG path data attribute.
    */
   public static final String CMD_PATTERN = "[^A|a|C|c|H|h|L|l|M|m|Q|q|S|sT|t|V|v|Z|z]++";

   /**
    * Regular expression to find data elements in an SVG path data attribute.
    */
   public static final String DATA_PATTERN = "[A|a|C|c|H|h|L|l|M|m|Q|q|S|sT|t|V|v|Z|z|,|\u0020]";

   /**
    * Handle magnitude of curve knots used to approximate an ellipse.
    */
   public static final float HANDLE_MAG = 0.552285f;

   /**
    * Ratio to convert from inches to units. In Processing, the value was 90.0 .
    */
   public static final float IN_TO_UNIT = 96.0f;

   /**
    * Ratio to convert from millimeters to units. In Processing, the value was
    * 3.543307 .
    */
   public static final float MM_TO_UNIT = 3.7795f;

   /**
    * Ratio to convert from picas to units. In Processing, the value was 15.0 .
    */
   public static final float PC_TO_UNIT = 16.0f;

   /**
    * Ratio to convert from points to units. In Processing the value was 1.25 .
    */
   public static final float PT_TO_UNIT = IUtils.FOUR_THIRDS;

   /**
    * Ratio to convert from pixels to units.
    */
   public static final float PX_TO_UNIT = 1.0f;

   public static CurveEntity2 parse ( final String fileName ) {

      final CurveEntity2 result = new CurveEntity2();

      try {
         final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         final File file = new File(fileName);
         final Document doc = db.parse(file);
         doc.normalizeDocument();

         /* Acquire view box. */
         final Node header = doc.getFirstChild();
         final NamedNodeMap nnm = header.getAttributes();
         final Node viewbox = nnm.getNamedItem("viewBox");
         final Vec4 vbVec = new Vec4();
         SvgParser.parseViewBox(viewbox, vbVec);

//         final float w = Utils.diff(vbVec.w, vbVec.y);
//         final float h = Utils.diff(vbVec.z, vbVec.x);
//         final float x = w * 0.5f;
//         final float y = h * 0.5f;
//         final Vec2 loc = new Vec2(x, y);
//         final float scale = Utils.min(w, h);
//         final Transform2 tr = result.transform;
//         tr.moveTo(loc);
//         tr.scaleTo(scale);

         final NodeList nodes = header.getChildNodes();
         final int nodeLen = nodes.getLength();
         for ( int i = 0; i < nodeLen; ++i ) {
            final Node node = nodes.item(i);
            final String name = node.getNodeName().toLowerCase();
            switch ( name ) {

               case "circle":
                  result.append(SvgParser.parseCirc(node, new Curve2()));
                  break;

               case "ellipse":
                  result.append(SvgParser.parseEllipse(node, new Curve2()));
                  break;

               case "line":
                  result.append(SvgParser.parseLine(node, new Curve2()));
                  break;

               case "path":
                  result.appendAll(SvgParser.parsePath(node));
                  break;

               case "polygon":
               case "polyline":
                  result.append(SvgParser.parsePoly(node, new Curve2()));
                  break;

               case "rect":
                  result.append(SvgParser.parseRect(node, new Curve2()));
                  break;

            }
         }

      } catch ( final Exception e ) {
         System.err.print(e);
         e.printStackTrace();
      }

      return result;
   }

   /**
    * Parses a SVG node and returns a Curve2 approximating a circle.
    *
    * @param circNode the circle node
    * @param target   the output curve
    *
    * @return the circle curve
    */
   public static Curve2 parseCirc (
      final Node circNode,
      final Curve2 target ) {

      final NamedNodeMap attributes = circNode.getAttributes();

      /* Search for attribute nodes. May return null. */
      final Node cxnode = attributes.getNamedItem("cx");
      final Node cynode = attributes.getNamedItem("cy");
      final Node rnode = attributes.getNamedItem("r");

      /* Acquire text content from the node if it exists. */
      final String cxstr = cxnode != null ? cxnode.getTextContent() : "0.0";
      final String cystr = cynode != null ? cynode.getTextContent() : "0.0";
      final String rstr = rnode != null ? rnode.getTextContent() : "0.5";

      /* Parse string or default. */
      final float cx = SvgParser.parseFloat(cxstr, 0.0f);
      final float cy = -SvgParser.parseFloat(cystr, 0.0f);
      final float r = SvgParser.parseFloat(rstr, 0.5f);

      Curve2.circle(0.0f, r, Curve.KNOTS_PER_CIRCLE, cx, cy, target);
//      target.reverse();
      return target;
   }

   /**
    * Attempts to parse a string representing a Cascading Style Sheet (CSS)
    * representation of an RGBA color. The input string is expected to be in one
    * of the formats:
    * <ul>
    * <li>"rgb(255, 127, 54)"</li>
    * <li>"rgb(100%, 50%, 25%)"</li>
    * <li>"rgba(255, 127, 54, 0.125)"</li>
    * <li>"rgba(100%, 50%, 25%, 0.125)"</li>
    * </ul>
    * The default color is (0.0, 0.0, 0.0, 1.0).
    *
    * @param str
    * @param target
    *
    * @return the color
    */
   public static Color parseCssRgba (
      final String str,
      final Color target ) {

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
   public static Curve2 parseEllipse (
      final Node ellipseNode,
      final Curve2 target ) {

      final NamedNodeMap attributes = ellipseNode.getAttributes();

      /* Search for attribute nodes. May return null. */
      final Node cxnode = attributes.getNamedItem("cx");
      final Node cynode = attributes.getNamedItem("cy");
      final Node rxnode = attributes.getNamedItem("rx");
      final Node rynode = attributes.getNamedItem("ry");

      /* Acquire text content from the node if it exists. */
      final String cxstr = cxnode != null ? cxnode.getTextContent() : "0.0";
      final String cystr = cynode != null ? cynode.getTextContent() : "0.0";
      final String rxstr = rxnode != null ? rxnode.getTextContent() : "0.5";
      final String rystr = rynode != null ? rynode.getTextContent() : "0.5";

      /* Parse string or default. */
      final float cx = SvgParser.parseFloat(cxstr, 0.0f);
      final float cy = -SvgParser.parseFloat(cystr, 0.0f);
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
//      target.reverse();
      return target;
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
    * View box relative units, namely <code>%</code> is limited; all this parser
    * will do is divide the value by 100.<br>
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
   public static float parseFloat (
      final String v,
      final float def ) {

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
   public static Curve2 parseLine (
      final Node lineNode,
      final Curve2 target ) {

      final NamedNodeMap attributes = lineNode.getAttributes();

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
//      target.reverse();
      return target;
   }

   public static Curve2[] parsePath ( final Node node ) {

      final ArrayList < Curve2 > result = new ArrayList <>();
      final NamedNodeMap attributes = node.getAttributes();
      final Node pathData = attributes.getNamedItem("d");

      if ( pathData != null ) {

         /*
          * These regular expressions are imperfect, and may yield empty string
          * tokens, so there's an extra step to strip them away.
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

         // final int dataLen = dataTokens.length;
         // for (int k = 0; k < dataLen; ++k) {
         // System.out.println(dataTokens[k]);
         // }

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

         Knot2 curr = null;
         Knot2 prev = null;

         final Vec2 relative = new Vec2();
         final Vec2 mh = new Vec2();
         float xOff = 0.0f;
         float yOff = 0.0f;
         boolean closedLoop = false;

         /*
          * Usually, one path should have one move to command, but just in case
          * it doesn't, create a boolean to track an initial move to. If there
          * is more than one, the target curve will be added to the result list
          * and replaced by a new one.
          */
         boolean initialMove = true;
         Curve2 target = new Curve2();
         target.clear();
         result.add(target);

         for ( int l = 0, cursor = 0; l < cmdLen; ++l ) {
            final PathCommand command = commands[l];
            // System.out.println(command);

            switch ( command ) {

               case MoveToAbs:

                  /* Tested April 18, 2020 */

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
                  curr.coord.set(
                     SvgParser.parseFloat(coxStr),
                     SvgParser.parseFloat(coyStr));

                  xOff = Utils.copySign(IUtils.DEFAULT_EPSILON, curr.coord.x);
                  yOff = Utils.copySign(IUtils.DEFAULT_EPSILON, curr.coord.y);

                  curr.foreHandle.set(curr.coord.x + xOff, curr.coord.y + yOff);
                  curr.rearHandle.set(curr.coord.x - xOff, curr.coord.y - yOff);

                  relative.set(curr.coord);

                  break;

               case LineToAbs:

                  /* Tested April 18, 2020 */

                  coxStr = dataTokens[cursor++]; /* 1 */
                  coyStr = dataTokens[cursor++]; /* 2 */

                  prev = curr;
                  curr = new Knot2();
                  target.append(curr);

                  curr.coord.set(
                     SvgParser.parseFloat(coxStr),
                     SvgParser.parseFloat(coyStr));
                  Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                  Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                  relative.set(curr.coord);

                  break;

               case QuadraticToAbs:

                  /* Tested April 18, 2020 */

                  mhxStr = dataTokens[cursor++]; /* 1 */
                  mhyStr = dataTokens[cursor++]; /* 2 */
                  coxStr = dataTokens[cursor++]; /* 3 */
                  coyStr = dataTokens[cursor++]; /* 4 */

                  prev = curr;
                  curr = new Knot2();
                  target.append(curr);

                  mh.set(
                     SvgParser.parseFloat(mhxStr),
                     SvgParser.parseFloat(mhyStr));
                  curr.coord.set(
                     SvgParser.parseFloat(coxStr),
                     SvgParser.parseFloat(coyStr));
                  Curve2.lerp13(mh, prev.coord, prev.foreHandle);
                  Curve2.lerp13(mh, curr.coord, curr.rearHandle);

                  relative.set(curr.coord);

                  break;

               case CubicToAbs:

                  /* Tested April 18, 2020 */

                  fhxStr = dataTokens[cursor++]; /* 1 */
                  fhyStr = dataTokens[cursor++]; /* 2 */
                  rhxStr = dataTokens[cursor++]; /* 3 */
                  rhyStr = dataTokens[cursor++]; /* 4 */
                  coxStr = dataTokens[cursor++]; /* 5 */
                  coyStr = dataTokens[cursor++]; /* 6 */

                  prev = curr;
                  curr = new Knot2();
                  target.append(curr);

                  prev.foreHandle.set(
                     SvgParser.parseFloat(fhxStr),
                     SvgParser.parseFloat(fhyStr));
                  curr.rearHandle.set(
                     SvgParser.parseFloat(rhxStr),
                     SvgParser.parseFloat(rhyStr));
                  curr.coord.set(
                     SvgParser.parseFloat(coxStr),
                     SvgParser.parseFloat(coyStr));

                  relative.set(curr.coord);

                  break;

               case ArcToAbs:

                  break;

               case ArcToRel:

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

                  prev.foreHandle.set(
                     SvgParser.parseFloat(fhxStr),
                     SvgParser.parseFloat(fhyStr));
                  curr.rearHandle.set(
                     SvgParser.parseFloat(rhxStr),
                     SvgParser.parseFloat(rhyStr));
                  curr.coord.set(
                     SvgParser.parseFloat(coxStr),
                     SvgParser.parseFloat(coyStr));

                  Vec2.add(relative, prev.foreHandle, prev.foreHandle);
                  Vec2.add(relative, curr.rearHandle, curr.rearHandle);
                  Vec2.add(relative, curr.coord, curr.coord);

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

                  curr.coord.x = SvgParser.parseFloat(coxStr, 0.0f);
                  curr.coord.y = prev.coord.y;
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

                  curr.coord.set(
                     SvgParser.parseFloat(coxStr),
                     SvgParser.parseFloat(coyStr));
                  Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                  Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                  relative.set(curr.coord);

                  break;

               case MoveToRel:

                  coxStr = dataTokens[cursor++]; /* 1 */
                  coyStr = dataTokens[cursor++]; /* 2 */

                  curr = new Knot2();
                  target.append(curr);

                  curr.coord.set(
                     SvgParser.parseFloat(coxStr),
                     SvgParser.parseFloat(coyStr));
                  Vec2.add(relative, curr.coord, curr.coord);

                  xOff = Utils.copySign(IUtils.DEFAULT_EPSILON, curr.coord.x);
                  yOff = Utils.copySign(IUtils.DEFAULT_EPSILON, curr.coord.y);

                  curr.foreHandle.set(curr.coord.x + xOff, curr.coord.y + yOff);
                  curr.rearHandle.set(curr.coord.x - xOff, curr.coord.y - yOff);

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

                  mh.set(
                     SvgParser.parseFloat(mhxStr),
                     SvgParser.parseFloat(mhyStr));
                  curr.coord.set(
                     SvgParser.parseFloat(coxStr),
                     SvgParser.parseFloat(coyStr));

                  Vec2.add(relative, mh, mh);

                  Curve2.lerp13(mh, prev.coord, prev.foreHandle);
                  Curve2.lerp13(mh, curr.coord, curr.rearHandle);

                  relative.set(curr.coord);

                  break;

               case ReflectCubicAbs:

                  break;

               case ReflectCubicRel:

                  break;

               case ReflectQuadraticAbs:

                  break;

               case ReflectQuadraticRel:

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

                  curr.coord.x = prev.coord.x;
                  curr.coord.y = SvgParser.parseFloat(coyStr, 0.0f);
                  Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                  Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                  relative.set(curr.coord);

                  break;

               case ClosePath:

                  closedLoop = true;
                  target.closedLoop = closedLoop;

                  break;

               default:

            }

         }

         /* Deal with first and last knots in open versus closed loop. */
         final Iterator < Curve2 > resultItr = result.iterator();
         while ( resultItr.hasNext() ) {
            final Curve2 curve = resultItr.next();
            final Knot2 first = curve.getFirst();
            final Knot2 last = curve.getLast();
            if ( target.closedLoop ) {
               Curve2.lerp13(
                  first.coord,
                  last.coord,
                  first.rearHandle);
               Curve2.lerp13(
                  last.coord,
                  first.coord,
                  last.foreHandle);
            } else {
               first.mirrorHandlesForward();
               last.mirrorHandlesBackward();
            }
         }
      }

      return result.toArray(new Curve2[result.size()]);
   }

   public static Curve2 parsePoly (
      final Node polygonNode,
      final Curve2 target ) {

      /* Poly-lines are open, polygons are closed loop. */
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

      final NamedNodeMap attributes = polygonNode.getAttributes();
      final Node ptsnode = attributes.getNamedItem("points");
      final String ptsstr = ptsnode != null ? ptsnode.getTextContent() : "0,0";
      final String[] coords = ptsstr.split(" |,");

      int i = 0;
      final int coordLen = coords.length;
      final int halfLen = coordLen / 2;
      target.resize(halfLen);
      final Iterator < Knot2 > itr = target.iterator();
      final Knot2 first = itr.next();
      first.coord.set(
         SvgParser.parseFloat(coords[i++], 0.0f),
         -SvgParser.parseFloat(coords[i++], 0.0f));

      Knot2 prev = first;
      while ( itr.hasNext() ) {
         final String xstr = coords[i++];
         final String ystr = coords[i++];
         final float x = SvgParser.parseFloat(xstr, 0.0f);
         final float y = -SvgParser.parseFloat(ystr, 0.0f);
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

//      target.reverse();
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
   public static Curve2 parseRect (
      final Node rectNode,
      final Curve2 target ) {

      final NamedNodeMap attributes = rectNode.getAttributes();

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
      final float y = -SvgParser.parseFloat(ystr, 0.0f);
      final float w = SvgParser.parseFloat(wstr, 1.0f);
      final float h = SvgParser.parseFloat(hstr, 1.0f);
      final float rx = SvgParser.parseFloat(rxstr, 0.0f);
      final float ry = SvgParser.parseFloat(rystr, 0.0f);

      /*
       * Corner rounding differs between APIs, so average horizontal and
       * vertical rounding.
       */
      Curve2.rect(x, y - h, x + w, y, ( rx + ry ) * 0.5f, target);
//      target.reverse();
      return target;
   }

   public static Vec4 parseViewBox ( final Node viewbox, final Vec4 target ) {

      final String content = viewbox.getTextContent();
      final String[] tokens = content.split(" ");

      float x = 0.0f;
      float y = 0.0f;
      float z = 1.0f;
      float w = 1.0f;

      if ( tokens.length > 3 ) { w = SvgParser.parseFloat(tokens[3], 1.0f); }
      if ( tokens.length > 2 ) { z = SvgParser.parseFloat(tokens[2], 1.0f); }
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
    * Command found in the "d" attribute of an SVG's path element.
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
      private PathCommand (
         final char code,
         final boolean isRelative,
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

            case 'Z':
            case 'z':
            default:
               return ClosePath;
         }
      }

   }

}
