package camzup.pfriendly;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import camzup.core.Color;
import camzup.core.Curve2;
import camzup.core.CurveEntity2;
import camzup.core.IUtils;
import camzup.core.Knot2;
import camzup.core.Transform;
import camzup.core.Vec2;
import processing.core.PApplet;
import processing.core.PFont;

/**
 * Maintains consistent behavior across renderers in the
 * CamZup library.
 */
public interface IUp {

   /**
    * Default camera aspect ratio used by perspective camera
    * when size is less than 128. Assumes 1:1.
    */
   float DEFAULT_ASPECT = 1.0f;

   /**
    * An off-white background color, 255, 245, 215 in RGB.
    */
   int DEFAULT_BKG_COLOR = 0xfffff7d5;

   /**
    * Default scalar by which the height of the sketch is
    * multiplied when the default camera function is called.
    * sqrt ( 3.0 ) / 2.0 .
    */
   float DEFAULT_CAM_DIST_FAC = IUtils.SQRT_3_2;

   /**
    * The Processing default color max.
    */
   float DEFAULT_COLOR_MAX = 255.0f;

   /**
    * Default far-clip when orthographic or perspective
    * functions are called without the near and far arguments.
    * 1000.0 .
    */
   float DEFAULT_FAR_CLIP = 1000.0f;

   /**
    * Default fill color, a light blue.
    */
   int DEFAULT_FILL_COLOR = 0xff9ad8e2;

   /**
    * Default field of view for a perspective camera
    * projection. PI / 3.0 .
    */
   float DEFAULT_FOV = IUtils.THIRD_PI;

   /**
    * When a non-zero value is supplied, a font glyph is
    * flattened to line segments.
    */
   float DEFAULT_GLYPH_DETAIL = 0.01f;

   /**
    * Default used by orthographic camera when sketch size is
    * less than 128.
    */
   float DEFAULT_HALF_HEIGHT = 64.0f;

   /**
    * Default used by orthographic camera when sketch size is
    * less than 128.
    */
   float DEFAULT_HALF_WIDTH = 64.0f;

   /**
    * Color for the lines connected the forehandle, coord and
    * rearhandle of a curve knot. Currently diagnostic only,
    * and so not adjustable.
    */
   int DEFAULT_HANDLE_COLOR = 0xff080708;

   /**
    * Color for the curve knot coordinate. Currently diagnostic
    * only, and so not adjustable.
    */
   int DEFAULT_HANDLE_COORD_COLOR = 0xffff2828;

   /**
    * Color for the curve knot forehandle. Currently diagnostic
    * only, and so not adjustable.
    */
   int DEFAULT_HANDLE_FORE_COLOR = 0xff3772ff;

   /**
    * Color for the curve knot rearhandle. Currently diagnostic
    * only, and so not adjustable.
    */
   int DEFAULT_HANDLE_REAR_COLOR = 0xfffdca40;

   /**
    * Default color of the x axis when displayed by a camera's
    * origin function.
    */
   int DEFAULT_I_COLOR = 0xffff2929;

   /**
    * The scalar by which a sketch's dimensions are multiplied
    * to find an appropriate line length.
    */
   float DEFAULT_IJK_LINE_FAC = 0.35f;

   /**
    * Default stroke-weight of an origin's lines.
    */
   float DEFAULT_IJK_SWEIGHT = 1.25f;

   /**
    * Default color of the y axis when displayed by a camera's
    * origin function.
    */
   int DEFAULT_J_COLOR = 0xff00b333;

   /**
    * Default color of the z axis when displayed by a camera's
    * origin function.
    */
   int DEFAULT_K_COLOR = 0xff1475b3;

   /**
    * Default camera location on the horizontal axis.
    */
   float DEFAULT_LOC_X = 0.0f;

   /**
    * Default camera location on the vertical axis.
    */
   float DEFAULT_LOC_Y = 0.0f;

   /**
    * Default camera location on the depth axis.
    */
   float DEFAULT_LOC_Z = -623.53827f;

   /**
    * Default near-clip when orthographic or perspective
    * functions are called without the near and far arguments.
    * 0.01 .
    */
   float DEFAULT_NEAR_CLIP = 0.01f;

   /**
    * Default transform order when converting a transform to a
    * matrix. TRS is short for Translation-Rotation-Scale.
    */
   Transform.Order DEFAULT_ORDER = Transform.Order.TRS;

   /**
    * Default stroke color.
    */
   int DEFAULT_STROKE_COLOR = 0xff202020;

   /**
    * Default stroke weight.
    */
   float DEFAULT_STROKE_WEIGHT = 1.125f;

   /**
    * Default text leading.
    */
   float DEFAULT_TEXT_LEADING = 14.0f;

   /**
    * Default text size.
    */
   float DEFAULT_TEXT_SIZE = 12.0f;

   /**
    * A cached interpolator for HSB colors.
    */
   Color.MixHsba MIXER_HSB = new Color.MixHsba();

   /**
    * A cached interpolator for RGB colors.
    */
   Color.SmoothStepRgba MIXER_RGB = new Color.SmoothStepRgba();

   /**
    * A function to convert a character to an list of curve
    * entities. A helper function for other variants of
    * getGlyph.
    *
    * @param font
    *           the awt font
    * @param frc
    *           the font render context
    * @param transform
    *           the AWT affine transform
    * @param detail
    *           the detail
    * @param character
    *           the character
    * @param curves
    *           the list of curves
    * @return the list of curves
    * @see Font#createGlyphVector(FontRenderContext, char[])
    * @see GlyphVector#getOutline()
    * @see Shape#getPathIterator(java.awt.geom.AffineTransform)
    * @see Shape#getPathIterator(java.awt.geom.AffineTransform,
    *      double)
    * @see Font#getSize()
    * @see PathIterator#currentSegment(float[])
    * @see Vec2#mix(Vec2, Vec2, float, Vec2)
    */
   static List < Curve2 > getGlyph (
         final Font font,
         final FontRenderContext frc,
         final AffineTransform transform,
         final float detail,
         final char character,
         final List < Curve2 > curves ) {

      /*
       * A GlyphVector can handle multiple characters at a time,
       * but individual characters are supplied instead, so that
       * glyphs with multiple curves (i, j, p, etc.) can be
       * grouped together.
       */
      final GlyphVector gv = font.createGlyphVector(frc,
            new char[] {
                  character
            });
      final Shape shp = gv.getOutline();

      /*
       * Acquire an iterator, run through it in a while loop, and
       * deal with 5 possible cases: the 'pen' moves to a point;
       * the 'pen' draws a straight line to the next point; the
       * 'pen' draws a curved line where the fore- and
       * rear-handles are the same; the 'pen' draws a curved line
       * with different fore- and rear-handles; the pen lifts and
       * stops drawing.
       */
      final PathIterator iter = detail == 0 ? shp.getPathIterator(transform)
            : shp.getPathIterator(transform, detail);

      /*
       * A float array is filled with values by the iterator when
       * currentSegment is called.
       */
      final float[] itrpts = new float[6];

      final int fontSize = font.getSize();
      final float invScalar = fontSize == 0 ? 1.0f : 1.0f / fontSize;
      Curve2 currCurve = null;
      Knot2 prevKnot = null;
      Knot2 currKnot = null;

      while (!iter.isDone()) {

         /*
          * y-axis is flipped in all cases, for y-up.
          */

         final int segType = iter.currentSegment(itrpts);
         switch (segType) {
            case PathIterator.SEG_MOVETO:

               /*
                * Create a new curve, then move to a point. The first three
                * or so points from this PathIterator seem to be garbage
                * (i.e., they linger at the origin). The only reason the
                * knot is created is to avoid null pointers for currKnot
                * and prevKnot.
                */

               currCurve = new Curve2();

               final float movx = itrpts[0] * invScalar;
               final float movy = -itrpts[1] * invScalar;

               currKnot = new Knot2(movx, movy);
               prevKnot = currKnot;

               break;

            case PathIterator.SEG_LINETO:

               /*
                * For straight lines, create a new knot from just a point.
                * The handles will have no influence, so the previous
                * knot's forehandle and the current knot's rear handle
                * should lie on the straight line between them.
                */

               final float linex = itrpts[0] * invScalar;
               final float liney = -itrpts[1] * invScalar;

               currKnot = new Knot2(
                     linex, liney,
                     0.0f, 0.0f,
                     0.0f, 0.0f);

               Vec2.mix(prevKnot.coord,
                     currKnot.coord,
                     IUtils.ONE_THIRD,
                     prevKnot.foreHandle);

               Vec2.mix(currKnot.coord,
                     prevKnot.coord,
                     IUtils.ONE_THIRD,
                     currKnot.rearHandle);

               currCurve.append(currKnot);
               prevKnot = currKnot;

               break;

            case PathIterator.SEG_QUADTO:

               /*
                * The ordering of the points is (1) a shared handle, or
                * control point, between the previous and next coordinate;
                * (2) the next coordinate.
                */

               final float qforex = itrpts[0] * invScalar;
               final float qforey = -itrpts[1] * invScalar;

               final float qcoordx = itrpts[2] * invScalar;
               final float qcoordy = -itrpts[3] * invScalar;

               prevKnot.foreHandle.set(qforex, qforey);
               currKnot = new Knot2(
                     qcoordx, qcoordy,
                     0.0f, 0.0f,
                     qforex, qforey);

               currCurve.append(currKnot);
               prevKnot = currKnot;

               break;

            case PathIterator.SEG_CUBICTO:

               /*
                * The order of a continuing curve is (1) prev knot fore
                * handle; (2) curr knot rear handle; (3) curr knot point.
                * This is different from a knot constructor's parameter
                * order.
                */

               final float cforex = itrpts[0] * invScalar;
               final float cforey = -itrpts[1] * invScalar;

               final float crearx = itrpts[2] * invScalar;
               final float creary = -itrpts[3] * invScalar;

               final float ccoordx = itrpts[4] * invScalar;
               final float ccoordy = -itrpts[5] * invScalar;

               prevKnot.foreHandle.set(cforex, cforey);
               currKnot = new Knot2(
                     ccoordx, ccoordy,
                     0.0f, 0.0f,
                     crearx, creary);

               currCurve.append(currKnot);
               prevKnot = currKnot;

               break;

            case PathIterator.SEG_CLOSE:
               prevKnot = currKnot;

               /*
                * The first two knots don't seem to be useful.
                */
               currCurve.removeFirst();
               currCurve.removeFirst();

               currKnot = currCurve.getFirst();

               /*
                * Resolve first and last handles.
                */

               Vec2.mix(
                     prevKnot.coord,
                     currKnot.coord,

                     IUtils.THIRD_PI,

                     prevKnot.foreHandle);

               Vec2.mix(
                     currKnot.coord,
                     prevKnot.coord,

                     IUtils.THIRD_PI,

                     currKnot.rearHandle);

               currCurve.closedLoop = true;
               curves.add(currCurve);

               break;

            default:

         }

         iter.next();
      }

      return curves;
   }

   /**
    * Gets an array of curves from a PFont. The Graphics2D
    * input can be retrieved from a PGraphicsJava2D renderer.
    * The AffineTransform can be null.
    *
    * @param graphics
    *           the AWT Graphics2D
    * @param transform
    *           the AWT affine transform
    * @param pfont
    *           the PFont
    * @param detail
    *           the glyph detail
    * @param characters
    *           the characteres
    * @return the array of Curve2s
    */
   static CurveEntity2[] getGlyph (
         final Graphics2D graphics,
         final AffineTransform transform,
         final PFont pfont, final float detail,
         final char... characters ) {

      final List < CurveEntity2 > entities = new ArrayList <>();
      final Font font = (Font) pfont.getNative();
      if (font != null) {
         final FontRenderContext frc = graphics.getFontRenderContext();
         // FontMetrics fm = graphics.getFontMetrics();
         final int len = characters.length;
         for (int i = 0; i < len; ++i) {
            final char character = characters[i];
            // final float w = fm.charWidth(character);
            final CurveEntity2 entity = new CurveEntity2();
            IUp.getGlyph(font, frc, transform, detail,
                  character, entity.curves);
            entities.add(entity);
         }
      }
      return entities.toArray(new CurveEntity2[entities.size()]);
   }

   /**
    * To convert a character to a glyph with OpenGL- based
    * renderers, Processing uses a deprecated method from an
    * AWT function. That method call has been isolated to this
    * function.
    *
    * @param pfont
    *           the Processing font
    * @param detail
    *           the curve detail
    * @param characters
    *           the characters
    * @return the array of glyphs
    * @see Toolkit#getDefaultToolkit()
    */
   static CurveEntity2[] getGlyph (
         final PFont pfont,
         final float detail,
         final char... characters ) {

      final List < CurveEntity2 > entities = new ArrayList <>();
      final Font font = (Font) pfont.getNative();
      if (font != null) {

         /*
          * To avoid having to get an AWT graphics context, the
          * deprecated method is used.
          */

         @SuppressWarnings("deprecation")
         final FontRenderContext frc = Toolkit.getDefaultToolkit()
               .getFontMetrics(font).getFontRenderContext();

         final int len = characters.length;
         for (int i = 0; i < len; ++i) {
            final char character = characters[i];
            final CurveEntity2 entity = new CurveEntity2();
            IUp.getGlyph(font, frc, null, detail,
                  character, entity.curves);
            entities.add(entity);
         }
      }
      return entities.toArray(new CurveEntity2[entities.size()]);
   }

   /**
    * Uses the renderer's default background color.
    */
   default void background () {

      this.background(IUp.DEFAULT_BKG_COLOR);
   }

   /**
    * Set the renderer's background color.
    *
    * @param c
    *           the color
    */
   default void background ( final Color c ) {

      this.background(Color.toHexInt(c));
   }

   /**
    * Sets the renderer's background color to the hexadecimal
    * value.
    *
    * @param c
    *           the color hexadecimal
    */
   void background ( final int c );

   /**
    * Sets the renderer's camera with default parameters.
    */
   void camera ();

   /**
    * Uses the renderer's default fill color.
    */
   default void fill () {

      this.fill(IUp.DEFAULT_FILL_COLOR);
   }

   /**
    * Sets the renderer's current fill to the color.
    *
    * @param c
    *           the color
    */
   default void fill ( final Color c ) {

      this.fill(Color.toHexInt(c));
   }

   /**
    * Sets the renderer's current fill to the hexadecimal
    * value.
    *
    * @param c
    *           the color in hexadecimal
    */
   void fill ( final int c );

   /**
    * Gets the renderer's height.
    *
    * @return the height
    */
   float getHeight ();

   /**
    * Gets the renderer's parent applet.
    *
    * @return the applet
    */
   PApplet getParent ();

   /**
    * Gets the renderer's size.
    *
    * @param target
    *           the output vector
    * @return the size
    */
   Vec2 getSize ( final Vec2 target );

   /**
    * Gets the renderer's width.
    *
    * @return the width
    */
   float getWidth ();

   /**
    * Converts a an array of characters to a an array of curve
    * entities. When the level of detail is 0, uses Bezier
    * curves; when the detailis non-zero, approximates the
    * glyph with a series of straight line segments. The PFont
    * should have been created with createFont, not loadFont.
    *
    * @param pfont
    *           the PFont
    * @param detail
    *           the level of detail
    * @param characters
    *           the characters
    * @return the array
    */
   default CurveEntity2[] glyph (
         final PFont pfont,
         final float detail,
         final char... characters ) {

      return IUp.getGlyph(pfont, detail, characters);
   }

   /**
    * Converts a string to a an array of curve entities. When
    * the level of detail is 0, uses Bezier curves; when the
    * detailis non-zero, approximates the glyph with a series
    * of straight line segments. The PFont should have been
    * created with createFont, not loadFont.
    *
    * @param pfont
    *           the PFont
    * @param detail
    *           the level of detail
    * @param str
    *           the string
    * @return the array
    */
   default CurveEntity2[] glyph (
         final PFont pfont,
         final float detail,
         final String str ) {

      return IUp.getGlyph(pfont, detail, str.toCharArray());
   }

   /**
    * Eases from an origin color to a destination by a step.
    *
    * @param origin
    *           the origin color
    * @param dest
    *           the destination color
    * @param step
    *           the factor in [0, 1]
    * @param target
    *           the output color
    * @return the color
    */
   Color lerpColor (
         final Color origin,
         final Color dest,
         final float step,
         final Color target );

   /**
    * Eases from an origin color to a destination by a step,
    * where colors are stored in integers.
    *
    * @param origin
    *           the origin color
    * @param dest
    *           the destination color
    * @param step
    *           the factor in [0, 1]
    * @return the color
    */
   int lerpColor (
         final int origin,
         final int dest,
         final float step );

   /**
    * Draws the world origin.
    */
   void origin ();

   /**
    * Uses the renderer's default stroke color.
    */
   default void stroke () {

      this.stroke(IUp.DEFAULT_STROKE_COLOR);
   }

   /**
    * Sets the renderer's current stroke to the color.
    *
    * @param c
    *           the color
    */
   default void stroke ( final Color c ) {

      this.stroke(Color.toHexInt(c));
   }

   /**
    * Sets the renderer's current stroke to the hexadecimal
    * value.
    *
    * @param c
    *           the color in hexadecimal
    */
   void stroke ( final int c );

   /**
    * Uses the renderer's default stroke weight.
    */
   default void strokeWeight () {

      this.strokeWeight(IUp.DEFAULT_STROKE_WEIGHT);
   }

   /**
    * Sets the renderer's stroke weight.
    *
    * @param sw
    *           the weight
    */
   void strokeWeight ( final float sw );
}
