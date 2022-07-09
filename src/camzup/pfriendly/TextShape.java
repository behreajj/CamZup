package camzup.pfriendly;

import java.util.ArrayList;
import java.util.List;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import camzup.core.Curve2;
import camzup.core.CurveEntity2;
import camzup.core.IUtils;
import camzup.core.Knot2;
import camzup.core.Utils;
import camzup.core.Vec2;

import processing.core.PFont;

import processing.awt.PGraphicsJava2D;

/**
 * Converts AWT font glyphs to curves or meshes.
 */
public abstract class TextShape {

   /**
    * Discourage overriding with a private constructor.
    */
   private TextShape ( ) {}

   /**
    * One of two characters sampled from a font to establish an appropriate
    * line height, {@value TextShape#LINE_HEIGHT_SAMPLE_A}.
    */
   public static final char LINE_HEIGHT_SAMPLE_A = 'l';

   /**
    * One of two characters sampled from a font to establish an appropriate
    * line height, {@value TextShape#LINE_HEIGHT_SAMPLE_B}.
    */
   public static final char LINE_HEIGHT_SAMPLE_B = 'd';

   /**
    * Character used to establish the space of a width for a given font,
    * {@value TextShape#SPACE_WIDTH_SAMPLE}.
    */
   public static final char SPACE_WIDTH_SAMPLE = '-';

   /**
    * Converts a list of characters to a an array of curve entities. When the
    * level of detail is 0, uses Bezier curves; when the detail is non-zero,
    * approximates the glyph with a series of straight line segments. The
    * PFont should have been created with createFont, not loadFont.
    *
    * @param pfont      the PFont
    * @param scale      the curve scale
    * @param detail     the level of detail
    * @param characters the characters
    * @param separate   separate curve per char
    *
    * @return the array
    */
   public static CurveEntity2[] glyphCurve ( final PFont pfont,
      final float scale, final float detail, final boolean separate,
      final char... characters ) {

      final Font font = ( Font ) pfont.getNative();
      if ( font != null ) {
         @SuppressWarnings ( "deprecation" )
         final FontRenderContext frc = Toolkit.getDefaultToolkit()
            .getFontMetrics(font).getFontRenderContext();
         return TextShape.processGlyphCe(frc, pfont, scale, detail, separate,
            characters);
      }
      return new CurveEntity2[] {};
   }

   /**
    * Converts a string to a an array of curve entities. When the level of
    * detail is 0, uses Bezier curves; when the detail is non-zero,
    * approximates the glyph with a series of straight line segments. The
    * PFont should have been created with createFont, not loadFont.
    *
    * @param pfont    the PFont
    * @param scale    the curve scale
    * @param detail   the level of detail
    * @param separate separate curve per char
    * @param str      the string
    *
    * @return the array
    */
   public static CurveEntity2[] glyphCurve ( final PFont pfont,
      final float scale, final float detail, final boolean separate,
      final String str ) {

      return TextShape.glyphCurve(pfont, scale, detail, separate, str
         .toCharArray());
   }

   /**
    * Converts a list of characters to a an array of curve entities. When the
    * level of detail is 0, uses Bezier curves; when the detail is non-zero,
    * approximates the glyph with a series of straight line segments. The
    * PFont should have been created with createFont, not loadFont.
    *
    * @param rndr       the renderer
    * @param pfont      the PFont
    * @param scale      the curve scale
    * @param detail     the level of detail
    * @param characters the characters
    * @param separate   separate curve per char
    *
    * @return the array
    */
   public static CurveEntity2[] glyphCurve ( final PGraphicsJava2D rndr,
      final PFont pfont, final float scale, final float detail,
      final boolean separate, final char... characters ) {

      return TextShape.processGlyphCe( ( ( Graphics2D ) rndr.getNative() )
         .getFontRenderContext(), pfont, scale, detail, separate, characters);
   }

   /**
    * Converts a string to a an array of curve entities. When the level of
    * detail is 0, uses Bezier curves; when the detail is non-zero,
    * approximates the glyph with a series of straight line segments. The
    * PFont should have been created with createFont, not loadFont.
    *
    * @param rndr     the renderer
    * @param pfont    the PFont
    * @param scale    the curve scale
    * @param detail   the level of detail
    * @param separate separate curve per char
    * @param str      the string
    *
    * @return the array
    */
   public static CurveEntity2[] glyphCurve ( final PGraphicsJava2D rndr,
      final PFont pfont, final float scale, final float detail,
      final boolean separate, final String str ) {

      return TextShape.glyphCurve(rndr, pfont, scale, detail, separate, str
         .toCharArray());
   }

   /**
    * Appends curve entities to an array list based on a font rendering
    * context, font and array of characters.
    *
    * @param frc          the font rendering context
    * @param pfont        the Processing font
    * @param displayScale the display scale
    * @param detail       the curve detail
    * @param characters   the characters
    * @param separate     separate curve per char
    *
    * @return the array of glyphs
    */
   protected static CurveEntity2[] processGlyphCe ( final FontRenderContext frc,
      final PFont pfont, final float displayScale, final float detail,
      final boolean separate, final char... characters ) {

      final ArrayList < CurveEntity2 > entities = new ArrayList <>();
      final Font font = ( Font ) pfont.getNative();
      if ( font != null ) {

         final float valDispScl = displayScale != 0.0f ? displayScale : 1.0f;

         if ( separate ) {

            float xCursor = 0.0f;
            float yCursor = 0.0f;
            final Vec2 tr = new Vec2();

            final float scalar = valDispScl / pfont.getSize();
            final float kerning = 0.25f;
            final float leading = 1.0f;
            final float spaceWidth = pfont.getGlyph(
               TextShape.SPACE_WIDTH_SAMPLE).width;
            final float lineHeight = Utils.max(pfont.getGlyph(
               TextShape.LINE_HEIGHT_SAMPLE_A).height, pfont.getGlyph(
                  TextShape.LINE_HEIGHT_SAMPLE_B).height);
            boolean newLineFlag = false;

            final int len = characters.length;
            for ( int i = 0; i < len; ++i ) {
               final char character = characters[i];

               if ( character == '\n' || character == '\r' ) {
                  yCursor -= ( lineHeight + leading ) * scalar;
                  xCursor = 0.0f;
                  newLineFlag = true;
               } else if ( character == ' ' || character == '\t' ) {
                  xCursor += spaceWidth * scalar;
                  newLineFlag = false;
               } else {

                  final CurveEntity2 entity = new CurveEntity2(Character
                     .toString(character));
                  entities.add(entity);

                  TextShape.processGlyphCurve(font, frc, null, valDispScl,
                     detail, character, entity.curves);

                  tr.set(xCursor, yCursor);
                  entity.moveTo(tr);

                  final PFont.Glyph glyph = pfont.getGlyph(character);
                  xCursor += ( glyph.width + kerning ) * scalar;
                  if ( !newLineFlag ) { xCursor += glyph.leftExtent * scalar; }
                  newLineFlag = false;
               }
            }

         } else {

            final String name = new String(characters);
            final CurveEntity2 entity = new CurveEntity2(name);
            TextShape.processGlyphCurve(font, frc, null, valDispScl, detail,
               characters, entity.curves);
            entities.add(entity);

         }
      }
      return entities.toArray(new CurveEntity2[entities.size()]);
   }

   /**
    * Converts a character to an list of curve entities. A helper function for
    * other variants of getGlyph.<br>
    * <br>
    * When multiple characters are provided, the kerning between characters is
    * better; when one character is supplied, glyphs with multiple curves (i,
    * j, p, etc.) are easier to organize.
    *
    * @param font      the AWT font
    * @param frc       the font render context
    * @param transform the AWT affine transform
    * @param scale     the glyph scale
    * @param detail    the detail
    * @param character the character
    * @param curves    the list of curves
    *
    * @return the list of curves
    *
    * @see TextShape#processGlyphCurve(Font, FontRenderContext,
    *      AffineTransform, float, char[], List)
    */
   protected static ArrayList < Curve2 > processGlyphCurve ( final Font font,
      final FontRenderContext frc, final AffineTransform transform,
      final float scale, final float detail, final char character,
      final ArrayList < Curve2 > curves ) {

      return TextShape.processGlyphCurve(font, frc, transform, scale, detail,
         new char[] { character }, curves);
   }

   /**
    * Converts an array of characters to an list of curve entities. A helper
    * function for other variants of getGlyph.<br>
    * <br>
    * When multiple characters are provided, the kerning between characters is
    * better; when one character is supplied, glyphs with multiple curves (i,
    * j, p, etc.) are easier to organize.
    *
    * @param font       the AWT font
    * @param frc        the font render context
    * @param transform  the AWT affine transform
    * @param scale      the glyph scale
    * @param detail     the detail
    * @param characters the characters array
    * @param curves     the list of curves
    *
    * @return the list of curves
    *
    * @see Font#createGlyphVector(FontRenderContext, char[])
    * @see GlyphVector#getOutline()
    * @see Shape#getPathIterator(java.awt.geom.AffineTransform)
    * @see Shape#getPathIterator(java.awt.geom.AffineTransform, double)
    * @see Font#getSize()
    * @see PathIterator#currentSegment(float[])
    */
   protected static ArrayList < Curve2 > processGlyphCurve ( final Font font,
      final FontRenderContext frc, final AffineTransform transform,
      final float scale, final float detail, final char[] characters,
      final ArrayList < Curve2 > curves ) {

      final GlyphVector gv = font.createGlyphVector(frc, characters);
      final String namePrefix = new String(characters) + ".";
      final Shape shp = gv.getOutline();

      /*
       * Acquire an iterator, run through it in a while loop, and deal with 5
       * possible cases: the 'pen' moves to a point; the 'pen' draws a straight
       * line to the next point; the 'pen' draws a curved line where the fore-
       * and rear-handles share a mid point; the 'pen' draws a curved line with
       * different fore- and rear-handles; the pen lifts and stops drawing.
       */
      final PathIterator itr = detail < IUtils.EPSILON ? shp.getPathIterator(
         transform) : shp.getPathIterator(transform, detail);

      /*
       * A double precision array is filled by the iterator when currentSegment
       * is called. A single precision (float) array can also be used.
       */
      final double[] itrpts = new double[6];

      /*
       * Neutralize the font size so output is close to unit scale. Multiply by
       * display scale.
       */
      final float fontSize = font.getSize2D();
      final double dispScl = scale == 0.0f ? 1.0d : scale;
      final double invScalar = fontSize == 0.0f ? dispScl : dispScl / fontSize;

      Curve2 currCurve = null;
      Knot2 prevKnot = null;
      Knot2 currKnot = null;
      int curveCount = 0;

      while ( !itr.isDone() ) {

         /* The y-axis is flipped in all cases, for y-up. */
         final int segType = itr.currentSegment(itrpts);
         switch ( segType ) {

            case PathIterator.SEG_MOVETO: /* 0 */

               /*
                * Create a new curve, move to a point. The first knot of a shape
                * copies the last; in the SEG_CLOSE case, its fore handle will
                * be copied to the last knot and then it will be removed.
                */

               final String name = namePrefix + curveCount;
               currCurve = new Curve2(name);
               ++curveCount;
               currCurve.closedLoop = true;
               currKnot = new Knot2(( float ) ( itrpts[0] * invScalar ),
                  ( float ) ( -itrpts[1] * invScalar ));
               currCurve.append(currKnot);
               prevKnot = currKnot;

               break;

            case PathIterator.SEG_LINETO: /* 1 */

               /*
                * For straight lines, create a new knot from a point. The
                * handles will have no influence, so the previous knot's
                * forehandle and the current knot's rear handle should lie on
                * the straight line between them.
                */

               currKnot = new Knot2();
               Knot2.fromSegLinear(( float ) ( itrpts[0] * invScalar ),
                  ( float ) ( -itrpts[1] * invScalar ), prevKnot, currKnot);
               currCurve.append(currKnot);
               prevKnot = currKnot;

               break;

            case PathIterator.SEG_QUADTO: /* 2 */

               /*
                * The order of a quadratic curve is: (0, 1) a shared handle, or
                * control point, between the previous and next coordinate; (2,
                * 3) the next coordinate. To convert to two cubic control
                * points, interpolate from the quadratic control point to the
                * respective anchor point by one third.
                */

               /* @formatter:off */
               currKnot = new Knot2();
               Knot2.fromSegQuadratic(
                  ( float ) (  itrpts[0] * invScalar ),
                  ( float ) ( -itrpts[1] * invScalar ),
                  ( float ) (  itrpts[2] * invScalar ),
                  ( float ) ( -itrpts[3] * invScalar ),
                  prevKnot, currKnot);
               currCurve.append(currKnot);
               prevKnot = currKnot;
               /* @formatter:on */

               break;

            case PathIterator.SEG_CUBICTO: /* 3 */

               /*
                * The order of a cubic curve: (0, 1) previous knot fore handle;
                * (2, 3) current knot rear handle; (4, 5) current knot point.
                */

               /* @formatter:off */
               currKnot = new Knot2();
               Knot2.fromSegCubic(
                  ( float ) (  itrpts[0] * invScalar ),
                  ( float ) ( -itrpts[1] * invScalar ),
                  ( float ) (  itrpts[2] * invScalar ),
                  ( float ) ( -itrpts[3] * invScalar ),
                  ( float ) (  itrpts[4] * invScalar ),
                  ( float ) ( -itrpts[5] * invScalar ),
                  prevKnot, currKnot);
               currCurve.append(currKnot);
               prevKnot = currKnot;
               /* @formatter:on */

               break;

            case PathIterator.SEG_CLOSE: /* 4 */

               prevKnot = currKnot;

               /* The knot appended at move-to duplicates the last knot. */
               currCurve.removeAt(0, currKnot);
               prevKnot.foreHandle.set(currKnot.foreHandle);

               /* The y-down to y-up flips the winding order. */
               currCurve.reverse();
               currCurve.closedLoop = true;
               curves.add(currCurve);

               break;

            default:

         }

         itr.next();
      }

      return curves;
   }

}
