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
import camzup.core.Experimental;
import camzup.core.IUtils;
import camzup.core.Knot2;
import camzup.core.Mesh2;
import camzup.core.MeshEntity2;
import camzup.core.Utils;
import camzup.core.Vec2;

import processing.core.PFont;

import processing.awt.PGraphicsJava2D;

/**
 * Converts AWT font glyphs to curves or meshes.
 */
public abstract class TextShape {

   /**
    * Converts a list of characters to a an array of curve entities. When the
    * level of detail is 0, uses Bezier curves; when the detail is non-zero,
    * approximates the glyph with a series of straight line segments. The
    * PFont should have been created with createFont, not loadFont.
    *
    * @param pfont      the PFont
    * @param detail     the level of detail
    * @param characters the characters
    * @param separate   separate curve per char
    *
    * @return the array
    */
   public static CurveEntity2[] glyphCurve ( final PFont pfont,
      final float detail, final boolean separate, final char... characters ) {

      return TextShape.processGlyphCe(pfont, detail, separate, characters);
   }

   /**
    * Converts a string to a an array of curve entities. When the level of
    * detail is 0, uses Bezier curves; when the detail is non-zero,
    * approximates the glyph with a series of straight line segments. The
    * PFont should have been created with createFont, not loadFont.
    *
    * @param pfont    the PFont
    * @param detail   the level of detail
    * @param separate separate curve per char
    * @param str      the string
    *
    * @return the array
    */
   public static CurveEntity2[] glyphCurve ( final PFont pfont,
      final float detail, final boolean separate, final String str ) {

      return TextShape.processGlyphCe(pfont, detail, separate, str
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
    * @param detail     the level of detail
    * @param characters the characters
    * @param separate   separate curve per char
    *
    * @return the array
    */
   public static CurveEntity2[] glyphCurve ( final PGraphicsJava2D rndr,
      final PFont pfont, final float detail, final boolean separate,
      final char... characters ) {

      return TextShape.processGlyphCe(rndr.g2, pfont, detail, separate,
         characters);
   }

   /**
    * Converts a string to a an array of curve entities. When the level of
    * detail is 0, uses Bezier curves; when the detail is non-zero,
    * approximates the glyph with a series of straight line segments. The
    * PFont should have been created with createFont, not loadFont.
    *
    * @param rndr     the renderer
    * @param pfont    the PFont
    * @param detail   the level of detail
    * @param separate separate curve per char
    * @param str      the string
    *
    * @return the array
    */
   public static CurveEntity2[] glyphCurve ( final PGraphicsJava2D rndr,
      final PFont pfont, final float detail, final boolean separate,
      final String str ) {

      return TextShape.processGlyphCe(rndr.g2, pfont, detail, separate, str
         .toCharArray());
   }

   /**
    * Converts a list of characters to an array of mesh entities. The PFont
    * should have been created with createFont, not loadFont.
    *
    * @param pfont      the PFont
    * @param detail     the level of detail
    * @param separate   separate curve per char
    * @param characters the characters
    *
    * @return the array
    */
   public static MeshEntity2[] glyphMesh ( final PFont pfont,
      final float detail, final boolean separate, final char... characters ) {

      return TextShape.processGlyphMe(pfont, detail, separate, characters);
   }

   /**
    * Converts a string to a an array of mesh entities. The PFont should have
    * been created with createFont, not loadFont.
    *
    * @param pfont    the PFont
    * @param detail   the level of detail
    * @param separate separate curve per char
    * @param str      the string
    *
    * @return the array
    */
   public static MeshEntity2[] glyphMesh ( final PFont pfont,
      final float detail, final boolean separate, final String str ) {

      return TextShape.processGlyphMe(pfont, detail, separate, str
         .toCharArray());
   }

   /**
    * Gets an array of curves from a PFont. The Graphics2D input can be
    * retrieved from a PGraphicsJava2D renderer.
    *
    * @param graphics   the AWT Graphics2D
    * @param pfont      the PFont
    * @param detail     the glyph detail
    * @param characters the characters
    * @param separate   separate curve per char
    *
    * @return the array of Curve2s
    */
   protected static CurveEntity2[] processGlyphCe ( final Graphics2D graphics,
      final PFont pfont, final float detail, final boolean separate,
      final char... characters ) {

      final List < CurveEntity2 > entities = new ArrayList <>();
      final Font font = ( Font ) pfont.getNative();
      if ( font != null ) {

         final FontRenderContext frc = graphics.getFontRenderContext();
         // final FontMetrics fm = graphics.getFontMetrics();
         // final LineMetrics lm = fm.getLineMetrics(
         // characters, 0, characters.length, graphics);

         if ( separate ) {

            final int len = characters.length;

            // float xoff = 0.0f;
            for ( int i = 0; i < len; ++i ) {
               final char character = characters[i];
               final CurveEntity2 entity = new CurveEntity2();
               TextShape.processGlyphCurve(font, frc, ( AffineTransform ) null,
                  detail, character, entity.curves);
               entities.add(entity);
               // final float w = fm.charWidth(character);
               // xoff += w;
            }

         } else {

            final CurveEntity2 entity = new CurveEntity2();
            TextShape.processGlyphCurve(font, frc, ( AffineTransform ) null,
               detail, characters, entity.curves);
            entities.add(entity);
         }

      }

      return entities.toArray(new CurveEntity2[entities.size()]);
   }

   /**
    * To convert a character to a glyph with OpenGL- based renderers,
    * Processing uses a deprecated method from an AWT function. That method
    * call has been isolated to this function.
    *
    * @param pfont      the Processing font
    * @param detail     the curve detail
    * @param characters the characters
    * @param separate   separate curve per char
    *
    * @return the array of glyphs
    *
    * @see Toolkit#getDefaultToolkit()
    */
   protected static CurveEntity2[] processGlyphCe ( final PFont pfont,
      final float detail, final boolean separate, final char... characters ) {

      final List < CurveEntity2 > entities = new ArrayList <>();
      final Font font = ( Font ) pfont.getNative();
      if ( font != null ) {

         @SuppressWarnings ( "deprecation" )
         final FontRenderContext frc = Toolkit.getDefaultToolkit()
            .getFontMetrics(font).getFontRenderContext();

         if ( separate ) {

            final int len = characters.length;

            for ( int i = 0; i < len; ++i ) {
               final char character = characters[i];
               final CurveEntity2 entity = new CurveEntity2();
               TextShape.processGlyphCurve(font, frc, ( AffineTransform ) null,
                  detail, character, entity.curves);
               entities.add(entity);
            }

         } else {

            final CurveEntity2 entity = new CurveEntity2();
            TextShape.processGlyphCurve(font, frc, ( AffineTransform ) null,
               detail, characters, entity.curves);
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
    * @param detail    the detail
    * @param character the character
    * @param curves    the list of curves
    *
    * @return the list of curves
    *
    * @see TextShape#processGlyphCurve(Font, FontRenderContext,
    *      AffineTransform, float, char[], List)
    */
   protected static List < Curve2 > processGlyphCurve ( final Font font,
      final FontRenderContext frc, final AffineTransform transform,
      final float detail, final char character, final List < Curve2 > curves ) {

      return TextShape.processGlyphCurve(font, frc, transform, detail,
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
   protected static List < Curve2 > processGlyphCurve ( final Font font,
      final FontRenderContext frc, final AffineTransform transform,
      final float detail, final char[] characters, final List <
         Curve2 > curves ) {

      final GlyphVector gv = font.createGlyphVector(frc, characters);
      final Shape shp = gv.getOutline();

      /*
       * Acquire an iterator, run through it in a while loop, and deal with 5
       * possible cases: the 'pen' moves to a point; the 'pen' draws a straight
       * line to the next point; the 'pen' draws a curved line where the fore-
       * and rear-handles share a mid point; the 'pen' draws a curved line with
       * different fore- and rear-handles; the pen lifts and stops drawing.
       */
      final PathIterator itr = detail < IUtils.DEFAULT_EPSILON ? shp
         .getPathIterator(transform) : shp.getPathIterator(transform, detail);

      /*
       * A double precision array is filled with values by the iterator when
       * currentSegment is called. A single precision array can also be used.
       */
      final double[] itrpts = new double[6];
      // final float[] itrpts = new float[6];

      /* Neutralize the font size so output is close to unit scale. */
      final float fontSize = font.getSize2D();
      final double invScalar = fontSize == 0.0f ? 1.0d : 1.0d / fontSize;

      Curve2 currCurve = null;
      Knot2 prevKnot = null;
      Knot2 currKnot = null;

      while ( !itr.isDone() ) {

         /* The y-axis is flipped in all cases, for y-up. */

         final int segType = itr.currentSegment(itrpts);
         switch ( segType ) {

            case PathIterator.SEG_MOVETO:

               /*
                * Create a new curve, then move to a point. The first two or so
                * points from this PathIterator seem to be garbage (i.e., they
                * linger at the origin). The first knot of a shape apparently
                * duplicates the last; its foreHandle will be copied to the last
                * knot and then it will be removed.
                */

               currCurve = new Curve2();
               currCurve.closedLoop = true;

               final double movx = itrpts[0] * invScalar;
               final double movy = -itrpts[1] * invScalar;

               currKnot = new Knot2(( float ) movx, ( float ) movy);
               currCurve.append(currKnot);
               prevKnot = currKnot;

               break;

            case PathIterator.SEG_LINETO:

               /*
                * For straight lines, create a new knot from a point. The
                * handles will have no influence, so the previous knot's
                * forehandle and the current knot's rear handle should lie on
                * the straight line between them.
                */

               final double linex = itrpts[0] * invScalar;
               final double liney = -itrpts[1] * invScalar;

               currKnot = new Knot2();
               Knot2.fromSegLinear(( float ) linex, ( float ) liney, prevKnot,
                  currKnot);

               currCurve.append(currKnot);
               prevKnot = currKnot;

               break;

            case PathIterator.SEG_QUADTO:

               /*
                * The order of a quadratic curve is: (0, 1) a shared handle, or
                * control point, between the previous and next coordinate; (2,
                * 3) the next coordinate. To convert to two cubic control
                * points, interpolate from the quadratic control point to the
                * respective anchor point by one third.
                */

               final double qmidx = itrpts[0] * invScalar;
               final double qmidy = -itrpts[1] * invScalar;
               final double qcoordx = itrpts[2] * invScalar;
               final double qcoordy = -itrpts[3] * invScalar;

               currKnot = new Knot2();
               Knot2.fromSegQuadratic(( float ) qmidx, ( float ) qmidy,
                  ( float ) qcoordx, ( float ) qcoordy, prevKnot, currKnot);

               currCurve.append(currKnot);
               prevKnot = currKnot;

               break;

            case PathIterator.SEG_CUBICTO:

               /*
                * The order of a cubic curve is: (0, 1) previous knot fore
                * handle; (2, 3) current knot rear handle; (4, 5) current knot
                * point.
                */

               final double cforex = itrpts[0] * invScalar;
               final double cforey = -itrpts[1] * invScalar;
               final double crearx = itrpts[2] * invScalar;
               final double creary = -itrpts[3] * invScalar;
               final double ccoordx = itrpts[4] * invScalar;
               final double ccoordy = -itrpts[5] * invScalar;

               currKnot = new Knot2();
               Knot2.fromSegCubic(( float ) cforex, ( float ) cforey,
                  ( float ) crearx, ( float ) creary, ( float ) ccoordx,
                  ( float ) ccoordy, prevKnot, currKnot);

               currCurve.append(currKnot);
               prevKnot = currKnot;

               break;

            case PathIterator.SEG_CLOSE:

               prevKnot = currKnot;

               /* The first two knots don't seem to be useful. */
               currCurve.removeAt(0);
               currCurve.removeAt(0);

               /* The knot appended at move-to duplicates the last knot. */
               currKnot = currCurve.removeAt(0);
               prevKnot.foreHandle.set(currKnot.foreHandle);

               /* The y-down to y-up flips the proper winding order (CCW). */
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

   /**
    * To convert a character to a glyph with OpenGL- based renderers,
    * Processing uses a deprecated method from an AWT function. That method
    * call has been isolated to this function.
    *
    * @param pfont      the Processing font
    * @param detail     the curve detail
    * @param characters the characters
    * @param separate   separate curve per char
    *
    * @return the array of glyphs
    *
    * @see Toolkit#getDefaultToolkit()
    */
   protected static MeshEntity2[] processGlyphMe ( final PFont pfont,
      final float detail, final boolean separate, final char... characters ) {

      final List < MeshEntity2 > entities = new ArrayList <>();
      final Font font = ( Font ) pfont.getNative();
      if ( font != null ) {

         @SuppressWarnings ( "deprecation" )
         final FontRenderContext frc = Toolkit.getDefaultToolkit()
            .getFontMetrics(font).getFontRenderContext();

         if ( separate ) {

            final int len = characters.length;

            for ( int i = 0; i < len; ++i ) {
               final char character = characters[i];
               final MeshEntity2 entity = new MeshEntity2();
               TextShape.processGlyphMesh(font, frc, null, detail, character,
                  entity.meshes);
               entities.add(entity);
            }

         } else {

            final MeshEntity2 entity = new MeshEntity2();
            TextShape.processGlyphMesh(font, frc, null, detail, characters,
               entity.meshes);
            entities.add(entity);

         }
      }
      return entities.toArray(new MeshEntity2[entities.size()]);
   }

   /**
    * Converts a character to an list of meshes. A helper function for other
    * variants of getGlyph.<br>
    * <br>
    * When multiple characters are provided, the kerning between characters is
    * better; when one character is supplied, glyphs with multiple curves (i,
    * j, p, etc.) are easier to organize.
    *
    * @param font      the AWT font
    * @param frc       the font render context
    * @param transform the AWT affine transform
    * @param detail    the detail
    * @param character the character
    * @param curves    the list of curves
    *
    * @return the list of curves
    *
    * @see TextShape#processGlyphMesh(Font, FontRenderContext,
    *      AffineTransform, float, char[], List)
    */
   @Experimental
   protected static List < Mesh2 > processGlyphMesh ( final Font font,
      final FontRenderContext frc, final AffineTransform transform,
      final float detail, final char character, final List < Mesh2 > meshes ) {

      return TextShape.processGlyphMesh(font, frc, transform, detail,
         new char[] { character }, meshes);
   }

   /**
    * Converts an array of characters to an list of meshes. A helper function
    * for other variants of getGlyph.<br>
    * <br>
    * When multiple characters are provided, the kerning between characters is
    * better; when one character is supplied, glyphs with multiple curves (i,
    * j, p, etc.) are easier to organize.
    *
    * @param font       the AWT font
    * @param frc        the font render context
    * @param transform  the AWT affine transform
    * @param detail     the detail
    * @param characters the characters array
    * @param meshes     the list of meshes
    *
    * @return the list of meshes
    *
    * @see Font#createGlyphVector(FontRenderContext, char[])
    * @see GlyphVector#getOutline()
    * @see Shape#getPathIterator(java.awt.geom.AffineTransform)
    * @see Shape#getPathIterator(java.awt.geom.AffineTransform, double)
    * @see Font#getSize()
    * @see PathIterator#currentSegment(float[])
    */
   @Experimental
   protected static List < Mesh2 > processGlyphMesh ( final Font font,
      final FontRenderContext frc, final AffineTransform transform,
      final float detail, final char[] characters, final List <
         Mesh2 > meshes ) {

      final GlyphVector gv = font.createGlyphVector(frc, characters);
      final Shape shp = gv.getOutline();
      final float vdetail = Utils.min(IUtils.DEFAULT_EPSILON, detail);
      final PathIterator itr = shp.getPathIterator(transform, vdetail);
      final double[] itrpts = new double[6];
      final float fontSize = font.getSize2D();
      final double invScalar = fontSize == 0.0f ? 1.0d : 1.0d / fontSize;

      Mesh2 currMesh = null;
      final ArrayList < Vec2 > currMeshPts = new ArrayList <>();
      Vec2 currPt = null;

      /* For texture coordinate calculation. */
      float lbx = Float.MAX_VALUE;
      float lby = Float.MAX_VALUE;
      float ubx = Float.MIN_VALUE;
      float uby = Float.MIN_VALUE;

      while ( !itr.isDone() ) {
         final int segType = itr.currentSegment(itrpts);

         switch ( segType ) {

            case PathIterator.SEG_MOVETO:

               currMesh = new Mesh2();

               final double movx = itrpts[0] * invScalar;
               final double movy = -itrpts[1] * invScalar;

               currPt = new Vec2(( float ) movx, ( float ) movy);
               currMeshPts.add(currPt);

               break;

            case PathIterator.SEG_CUBICTO:

               currPt = new Vec2(( float ) ( itrpts[4] * invScalar ),
                  ( float ) ( -itrpts[5] * invScalar ));
               currMeshPts.add(currPt);

               break;

            case PathIterator.SEG_QUADTO:

               currPt = new Vec2(( float ) ( itrpts[2] * invScalar ),
                  ( float ) ( -itrpts[3] * invScalar ));
               currMeshPts.add(currPt);

               break;

            case PathIterator.SEG_LINETO:

               currPt = new Vec2(( float ) ( itrpts[0] * invScalar ),
                  ( float ) ( -itrpts[1] * invScalar ));
               currMeshPts.add(currPt);

               break;

            case PathIterator.SEG_CLOSE:

               currMeshPts.remove(0);

               final int len = currMeshPts.size();
               final Vec2[] vs = currMeshPts.toArray(new Vec2[len]);
               final Vec2[] vts = new Vec2[len];
               final int[][][] fs = new int[1][len + 1][2];
               final int[][] f = fs[0];

               final float dimx = ubx - lbx;
               final float dimy = uby - lby;
               final float xInv = dimx == 0.0f ? 1.0f : 1.0f / dimx;
               final float yInv = dimy == 0.0f ? 1.0f : 1.0f / dimy;

               /* Calculate UVs as between lower and upper bound. */
               for ( int i = 0; i < len; ++i ) {
                  f[i][0] = i;
                  f[i][1] = i;
                  final Vec2 v = vs[i];
                  vts[i] = new Vec2( ( v.x - lbx ) * xInv, 1.0f - ( v.y - lby )
                     * yInv);
               }
               f[len][0] = 0;
               f[len][1] = 0;

               currMesh.set(fs, vs, vts);
               currMesh.reverseFace(0);
               meshes.add(currMesh);

               /*
                * Clear array list and reset lower and upper bound so that UV
                * coordinates can be calculated for the next mesh.
                */
               currMeshPts.clear();
               lbx = Float.MAX_VALUE;
               lby = Float.MAX_VALUE;
               ubx = Float.MIN_VALUE;
               uby = Float.MIN_VALUE;

               break;

            default:

         }

         if ( currPt.x < lbx ) { lbx = currPt.x; }
         if ( currPt.x > ubx ) { ubx = currPt.x; }
         if ( currPt.y < lby ) { lby = currPt.y; }
         if ( currPt.y > uby ) { uby = currPt.y; }

         itr.next();
      }

      return meshes;
   }

}
