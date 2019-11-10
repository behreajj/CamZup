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

   private static float parseFloat ( final String v, final float def ) {

      // TODO: Does this need to handle different SVG units, such
      // as "%" for percentages, etc.
      float x = def;
      try {
         x = Float.parseFloat(v);
      } catch (final NumberFormatException e) {
         x = def;
      }
      return x;
   }

   @SuppressWarnings("unused")
   private static Curve2 parsePolygon ( final Node polygonNode ) {

      final NamedNodeMap attributes = polygonNode.getAttributes();
      final Node ptsnode = attributes.getNamedItem("points");
      final String ptsstr = ptsnode != null ? ptsnode.getTextContent() : "0,0";
      final String[] coords = ptsstr.split("\\s*");
      int clen = coords.length;
      Knot2[] knots = new Knot2[clen];
      for(int i = 0; i < clen; ++i) {
         String coord = coords[i];
         String[] xystr = coord.split(",");
         float x = parseFloat(xystr[0], 0.0f);
         float y = parseFloat(xystr[1], 0.0f);
         Knot2 knot = new Knot2(x, y);
         knots[i] = knot;
      }
      
      Curve2 result = new Curve2(true, knots);
      return Curve2.straightenHandles(result);
   }
   
   private static Curve2 parsePath ( final Node path ) {

      final NamedNodeMap attributes = path.getAttributes();
      final Node pathData = attributes.getNamedItem("d");
      Curve2 result = new Curve2();
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

         String cox = "0.0";
         String coy = "0.0";
         String rhx = "0.0";
         String rhy = "0.0";
         String fhx = "0.0";
         String fhy = "0.0";
         String mhx = "0.0";
         String mhy = "0.0";

         Knot2 curr = null;
         Knot2 prev = null;
         
         Vec2 relative = new Vec2();
         Vec2 mh = new Vec2();
         Vec2 coord = relative;
         float xOff = 0.0f;
         float yOff = 0.0f;

         boolean closedLoop = false;
         final LinkedList < Knot2 > knots = new LinkedList <>();
         for (int l = 0, cursor = 0; l < cmdLen; ++l) {
            final PathCommand command = commands[l];
            System.out.println(command);

            switch (command) {
               case ArcToAbs:
                  break;
               case ArcToRel:
                  break;

               case CubicToAbs:

                  fhx = dataTokens[cursor++]; /* 1 */
                  fhy = dataTokens[cursor++]; /* 2 */
                  rhx = dataTokens[cursor++]; /* 3 */
                  rhy = dataTokens[cursor++]; /* 4 */
                  cox = dataTokens[cursor++]; /* 5 */
                  coy = dataTokens[cursor++]; /* 6 */

                  prev = curr;
                  curr = new Knot2();
                  knots.add(curr);

                  prev.foreHandle.set(fhx, fhy);
                  curr.rearHandle.set(rhx, rhy);
                  curr.coord.set(cox, coy);

                  relative.set(curr.coord);

                  break;

               case CubicToRel:

                  fhx = dataTokens[cursor++]; /* 1 */
                  fhy = dataTokens[cursor++]; /* 2 */
                  rhx = dataTokens[cursor++]; /* 3 */
                  rhy = dataTokens[cursor++]; /* 4 */
                  cox = dataTokens[cursor++]; /* 5 */
                  coy = dataTokens[cursor++]; /* 6 */

                  prev = curr;
                  curr = new Knot2();
                  knots.add(curr);

                  prev.foreHandle.set(fhx, fhy);
                  curr.rearHandle.set(rhx, rhy);
                  curr.coord.set(cox, coy);

                  Vec2.add(relative, prev.foreHandle, prev.foreHandle);
                  Vec2.add(relative, curr.rearHandle, curr.rearHandle);
                  Vec2.add(relative, curr.coord, curr.coord);

                  relative.set(curr.coord);

                  break;

               case HorizAbs:
                  
                  cox = dataTokens[cursor++]; /* 1 */
                  
                  prev = curr;
                  curr = new Knot2();
                  knots.add(curr);

                  curr.coord.x = parseFloat(cox, 0.0f);
                  curr.coord.y = prev.coord.y;
                  Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                  Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                  relative.set(curr.coord);
                  
                  break;
               
               case HorizRel:
                  
                  cox = dataTokens[cursor++]; /* 1 */
                  
                  prev = curr;
                  curr = new Knot2();
                  knots.add(curr);

                  curr.coord.x = parseFloat(cox, 0.0f);
                  curr.coord.y = prev.coord.y;
                  Vec2.add(relative, coord, coord);
                  Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                  Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                  relative.set(curr.coord);
                  
                  break;

               case LineToAbs:

                  cox = dataTokens[cursor++]; /* 1 */
                  coy = dataTokens[cursor++]; /* 2 */

                  prev = curr;
                  curr = new Knot2();
                  knots.add(curr);

                  curr.coord.set(cox, coy);
                  Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                  Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                  relative.set(curr.coord);

                  break;

               case LineToRel:

                  cox = dataTokens[cursor++]; /* 1 */
                  coy = dataTokens[cursor++]; /* 2 */

                  prev = curr;
                  curr = new Knot2();
                  knots.add(curr);

                  curr.coord.set(cox, coy);
                  Vec2.add(relative, coord, coord);
                  Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                  Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                  relative.set(curr.coord);

                  break;

               case MoveToAbs:

                  cox = dataTokens[cursor++]; /* 1 */
                  coy = dataTokens[cursor++]; /* 2 */

                  curr = new Knot2();
                  knots.add(curr);
                  coord.set(cox, coy);

                  xOff = Math.copySign(Utils.EPSILON, coord.x);
                  yOff = Math.copySign(Utils.EPSILON, coord.y);

                  curr.foreHandle.set(
                        coord.x + xOff,
                        coord.y + yOff);
                  curr.rearHandle.set(
                        coord.x - xOff,
                        coord.y - yOff);

                  relative.set(curr.coord);

                  break;

               case MoveToRel:

                  cox = dataTokens[cursor++]; /* 1 */
                  coy = dataTokens[cursor++]; /* 2 */

                  curr = new Knot2();
                  knots.add(curr);
                  coord = curr.coord;
                  coord.set(cox, coy);
                  Vec2.add(relative, coord, coord);

                  xOff = Math.copySign(Utils.EPSILON, coord.x);
                  yOff = Math.copySign(Utils.EPSILON, coord.y);

                  curr.foreHandle.set(
                        coord.x + xOff,
                        coord.y + yOff);
                  curr.rearHandle.set(
                        coord.x - xOff,
                        coord.y - yOff);

                  relative.set(curr.coord);

                  break;

               case QuadraticToAbs:

                  mhx = dataTokens[cursor++]; /* 1 */
                  mhy = dataTokens[cursor++]; /* 2 */
                  cox = dataTokens[cursor++]; /* 3 */
                  coy = dataTokens[cursor++]; /* 4 */

                  prev = curr;
                  curr = new Knot2();
                  knots.add(curr);

                  mh.set(mhx, mhy);
                  curr.coord.set(cox, coy);
                  Curve2.lerp13(mh, prev.coord, prev.foreHandle);
                  Curve2.lerp13(mh, curr.coord, curr.rearHandle);

                  relative.set(curr.coord);

                  break;

               case QuadraticToRel:

                  mhx = dataTokens[cursor++]; /* 1 */
                  mhy = dataTokens[cursor++]; /* 2 */
                  cox = dataTokens[cursor++]; /* 3 */
                  coy = dataTokens[cursor++]; /* 4 */

                  prev = curr;
                  curr = new Knot2();
                  knots.add(curr);

                  mh.set(mhx, mhy);
                  curr.coord.set(cox, coy);

                  Vec2.add(relative, mh, mh);
                  Vec2.add(relative, coord, coord);

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
                  
                  coy = dataTokens[cursor++]; /* 1 */
                  
                  prev = curr;
                  curr = new Knot2();
                  knots.add(curr);

                  curr.coord.x = prev.coord.x;
                  curr.coord.y = parseFloat(coy, 0.0f);
                  Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                  Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                  relative.set(curr.coord);
                  
                  break;
               
               case VertRel:
                  
                  coy = dataTokens[cursor++]; /* 1 */
                                    
                  prev = curr;
                  curr = new Knot2();
                  knots.add(curr);

                  curr.coord.x = prev.coord.x;
                  curr.coord.y = parseFloat(coy, 0.0f);
                  Vec2.add(relative, coord, coord);
                  Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
                  Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);

                  relative.set(curr.coord);
               
                  break;
               
               case ClosePath:
               default:
                  closedLoop = true;
            }
         }

         if (closedLoop) {
            knots.getFirst().rearHandle.set(knots.getLast().rearHandle);
            knots.removeLast();
         } else {
            knots.getFirst().mirrorHandlesForward();
            knots.getLast().mirrorHandlesBackward();
         }
         
         result.append(knots);
      }
      return result;
   }

   @SuppressWarnings("unused")
   private static Curve2 parseRect ( final Node rectNode ) {

      final NamedNodeMap attributes = rectNode.getAttributes();

      final Node xnode = attributes.getNamedItem("x");
      final Node ynode = attributes.getNamedItem("y");
      final Node wnode = attributes.getNamedItem("width");
      final Node hnode = attributes.getNamedItem("height");
      final Node rxnode = attributes.getNamedItem("rx");
      final Node rynode = attributes.getNamedItem("ry");

      final String xstr = xnode != null ? xnode.getTextContent() : "0.0";
      final String ystr = ynode != null ? ynode.getTextContent() : "0.0";
      final String wstr = wnode != null ? wnode.getTextContent() : "1.0";
      final String hstr = hnode != null ? hnode.getTextContent() : "1.0";
      final String rxstr = rxnode != null ? rxnode.getTextContent() : "0.0";
      final String rystr = rynode != null ? rynode.getTextContent() : "0.0";

      final float x = SVGParser.parseFloat(xstr, 0.0f);
      final float y = SVGParser.parseFloat(ystr, 0.0f);
      final float w = SVGParser.parseFloat(wstr, 1.0f);
      final float h = SVGParser.parseFloat(hstr, 1.0f);
      final float rx = SVGParser.parseFloat(rxstr, 0.0f);
      final float ry = SVGParser.parseFloat(rystr, 0.0f);

      return Curve2.rect(x, y, x + w, y + h, (rx + ry) * 0.5f, new Curve2());
   }

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

   public static CurveEntity2 parse ( final String fileName ) {

      final CurveEntity2 result = new CurveEntity2();
      try {
         final DocumentBuilder db = SVGParser.dbf.newDocumentBuilder();
         final File file = new File(fileName);
         final Document doc = db.parse(file);
         doc.normalizeDocument();

         final LinkedList < Curve2 > curves = result.curves;

         // TODO: What about rect, ellipse, etc.
         
         final NodeList paths = doc.getElementsByTagName("path");
         final int nodeLen = paths.getLength();
         for (int i = 0; i < nodeLen; ++i) {
            final Node path = paths.item(i);
            Curve2 curve = parsePath(path);
            curves.add(curve);
         }
      }catch(

   final Exception e)
   {
         System.err.print(e);
         e.printStackTrace();
      }

   return result;
}}
