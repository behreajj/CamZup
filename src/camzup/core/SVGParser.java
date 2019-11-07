package camzup.core;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import camzup.core.Curve2.Knot2;

public abstract class SVGParser {

   public enum PathCommand {

      ArcToAbs ( 'A', false, 7 ),
      ArcToRel ( 'a', true, 7 ),
      ClosePath ( 'Z', false, 0 ),
      CubicToAbs ( 'C', false, 6 ),
      CubicToRel ( 'c', true, 6 ),
      HorizAbs ( 'H', false, 1 ),
      HorizRel ( 'h', true, 1 ),
      LineToAbs ( 'L', false, 2 ),
      LineToRel ( 'l', true, 2 ),
      MoveToAbs ( 'M', false, 2 ),
      MoveToRel ( 'm', true, 2 ),
      QuadraticToAbs ( 'Q', false, 4 ),
      QuadraticToRel ( 'q', true, 4 ),
      ReflectCubicAbs ( 'S', false, 4 ),
      ReflectCubicRel ( 's', true, 4 ),
      ReflectQuadraticAbs ( 'T', false, 2 ),
      ReflectQuadraticRel ( 't', true, 2 ),
      VertAbs ( 'V', false, 1 ),
      VertRel ( 'v', true, 1 ),;

      public static PathCommand fromChar ( final char c ) {

         switch (c) {
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

      private final char code;

      private final int dataCount;

      private final boolean isRelative;

      private PathCommand ( final char code, final boolean isRelative,
            final int dataCount ) {

         this.code = code;
         this.isRelative = isRelative;
         this.dataCount = dataCount;
      }

      public char getCode () {

         return this.code;
      }

      public int getDataCount () {

         return this.dataCount;
      }

      public boolean isRelative () {

         return this.isRelative;
      }
   }

   private static final String cmdPattern = "[^A|a|C|c|H|h|L|l|M|m|Q|q|S|sT|t|V|v|Z|z]++";

   private static final String dataPattern = "[A|a|C|c|H|h|L|l|M|m|Q|q|S|sT|t|V|v|Z|z|,|\u0020]";

   private static final DocumentBuilderFactory dbf = DocumentBuilderFactory
         .newDefaultInstance();

   private static String[] stripEmptyTokens ( final String[] tokens ) {

      final int len = tokens.length;
      final ArrayList < String > list = new ArrayList <>(len);
      for (int i = 0; i < len; ++i) {
         final String token = tokens[i].trim();
         if (token.length() > 0) {
            list.add(token);
         }
      }
      return list.toArray(new String[list.size()]);
   }

   private static float parseFloat ( String v ) {

      float x = 0.0f;
      try {
         x = Float.parseFloat(v);
      } catch (final NumberFormatException e) {
         x = 0.0f;
      }
      return x;
   }

   @SuppressWarnings("unused")
   private static Curve2 parseRect ( final Node rectNode ) {

      // TODO: It's also possible to have rounded rectangles.
      // TODO: Refactor to have curve create a rounded rect, then
      // just have this defer to that function. Reference the PApplet rounded rect impl.
      final NamedNodeMap attributes = rectNode.getAttributes();
      final String xstr = attributes.getNamedItem("x").getTextContent();
      final String ystr = attributes.getNamedItem("y").getTextContent();
      final String wstr = attributes.getNamedItem("width").getTextContent();
      final String hstr = attributes.getNamedItem("height").getTextContent();

      float x = parseFloat(xstr);
      float y = parseFloat(ystr);
      float w = parseFloat(wstr);
      float h = parseFloat(hstr);

      /* Create knots, set coordinates. */
      Knot2 tl = new Knot2(x, y);
      Knot2 tr = new Knot2(x + w, y);
      Knot2 br = new Knot2(x + w, y + h);
      Knot2 bl = new Knot2(x, y + h);

      /* Lerp fore handles. */
      Vec2.mix(
            tl.coord,
            tr.coord,
            IUtils.ONE_THIRD,
            tl.foreHandle);

      Vec2.mix(
            tr.coord,
            br.coord,
            IUtils.ONE_THIRD,
            tr.foreHandle);

      Vec2.mix(
            br.coord,
            bl.coord,
            IUtils.ONE_THIRD,
            br.foreHandle);

      Vec2.mix(
            bl.coord,
            tl.coord,
            IUtils.ONE_THIRD,
            tl.foreHandle);

      /* Lerp rear handles. */
      Vec2.mix(
            tr.coord,
            tl.coord,
            IUtils.ONE_THIRD,
            tr.rearHandle);

      Vec2.mix(
            br.coord,
            tr.coord,
            IUtils.ONE_THIRD,
            br.rearHandle);

      Vec2.mix(
            bl.coord,
            br.coord,
            IUtils.ONE_THIRD,
            bl.rearHandle);

      Vec2.mix(
            tl.coord,
            bl.coord,
            IUtils.ONE_THIRD,
            tl.rearHandle);

      /* Add knots to a list. */
      LinkedList < Knot2 > knots = new LinkedList <>();
      knots.add(tl);
      knots.add(tr);
      knots.add(br);
      knots.add(bl);

      return new Curve2(true, knots);
   }

   public static CurveEntity2 parse ( final String fileName ) {

      final CurveEntity2 result = new CurveEntity2();
      try {
         final DocumentBuilder db = SVGParser.dbf.newDocumentBuilder();
         final File file = new File(fileName);
         final Document doc = db.parse(file);
         doc.normalizeDocument();

         // TODO: What about rect, ellipse, etc.
         final NodeList paths = doc.getElementsByTagName("path");
         final int nodeLen = paths.getLength();
         LinkedList < Curve2 > curves = result.curves;
         for (int i = 0; i < nodeLen; ++i) {
            final Node path = paths.item(i);
            final NamedNodeMap attributes = path.getAttributes();
            final Node pathData = attributes.getNamedItem("d");
            if (pathData != null) {
               final String pdStr = pathData.getTextContent();

               String[] cmdTokens = pdStr.split(SVGParser.cmdPattern);
               cmdTokens = SVGParser.stripEmptyTokens(cmdTokens);
               final int cmdLen = cmdTokens.length;
               final PathCommand[] commands = new PathCommand[cmdLen];
               for (int j = 0; j < cmdLen; ++j) {
                  final String cmdToken = cmdTokens[j];
                  final char cmdCode = cmdToken.charAt(0);
                  final PathCommand cmd = PathCommand.fromChar(cmdCode);
                  commands[j] = cmd;
                  // System.out.println(cmd);
               }

               String[] dataTokens = pdStr.split(SVGParser.dataPattern);
               dataTokens = SVGParser.stripEmptyTokens(dataTokens);
               // final int dataLen = dataTokens.length;
               // for (int k = 0; k < dataLen; ++k) {
               // System.out.println(dataTokens[k]);
               // }

               String cox, coy, rhx, rhy, fhx, fhy;
               Knot2 curr = null;
               Knot2 prev = null;
               boolean closedLoop = false;
               LinkedList < Knot2 > knots = new LinkedList <>();
               for (int l = 0, cursor = 0; l < cmdLen; ++l) {
                  final PathCommand command = commands[l];
                  System.out.println(command);

                  switch (command) {
                     case ArcToAbs:
                        break;
                     case ArcToRel:
                        break;

                     case ClosePath:

                        closedLoop = true;

                        break;

                     case CubicToAbs:

                        fhx = dataTokens[cursor++];
                        fhy = dataTokens[cursor++];
                        rhx = dataTokens[cursor++];
                        rhy = dataTokens[cursor++];
                        cox = dataTokens[cursor++];
                        coy = dataTokens[cursor++];

                        // System.out.println(fhx);
                        // System.out.println(fhy);
                        // System.out.println(rhx);
                        // System.out.println(rhy);
                        // System.out.println(cox);
                        // System.out.println(coy);

                        prev = curr;
                        curr = new Knot2();
                        knots.add(curr);

                        prev.foreHandle.set(fhx, fhy);
                        curr.rearHandle.set(rhx, rhy);
                        curr.coord.set(cox, coy);

                        // System.out.println(prev);
                        // System.out.println(curr);

                        break;

                     case CubicToRel:
                        break;
                     case HorizAbs:
                        break;
                     case HorizRel:
                        break;

                     case LineToAbs:

                        cox = dataTokens[cursor++];
                        coy = dataTokens[cursor++];

                        // System.out.println(cox);
                        // System.out.println(coy);

                        prev = curr;
                        curr = new Knot2();
                        knots.add(curr);

                        curr.coord.set(cox, coy);
                        Vec2.mix(
                              prev.coord,
                              curr.coord,
                              IUtils.ONE_THIRD,
                              prev.foreHandle);
                        Vec2.mix(
                              curr.coord,
                              prev.coord,
                              IUtils.ONE_THIRD,
                              curr.rearHandle);

                        break;

                     case LineToRel:
                        break;

                     case MoveToAbs:

                        cox = dataTokens[cursor++];
                        coy = dataTokens[cursor++];

                        // System.out.println(cox);
                        // System.out.println(coy);

                        curr = new Knot2();
                        knots.add(curr);
                        Vec2 coord = curr.coord;
                        coord.set(cox, coy);

                        final float xOff = Math.copySign(Utils.EPSILON,
                              coord.x);
                        final float yOff = Math.copySign(Utils.EPSILON,
                              coord.y);

                        curr.foreHandle.set(
                              coord.x + xOff,
                              coord.y + yOff);
                        curr.rearHandle.set(
                              coord.x - xOff,
                              coord.y - yOff);

                        // System.out.println(curr);

                        break;

                     case MoveToRel:
                        break;
                     case QuadraticToAbs:
                        break;
                     case QuadraticToRel:
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
                        break;
                     case VertRel:
                        break;
                     default:
                        break;
                  }
               }

               if (closedLoop) {
                  knots.getFirst().rearHandle.set(knots.getLast().rearHandle);
                  knots.removeLast();
               } else {
                  knots.getFirst().mirrorHandlesForward();
                  knots.getLast().mirrorHandlesBackward();
               }

               Curve2 curve = new Curve2(closedLoop, knots);
               System.out.println(curve);
               curves.add(curve);
            }
         }
      } catch (final Exception e) {
         System.err.print(e);
         e.printStackTrace();
      }

      return result;
   }
}
