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
import camzup.core.Curve2.Knot2;
import camzup.core.CurveEntity2;
import camzup.core.IUtils;
import camzup.core.Transform;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;
import camzup.core.Vec4;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PMatrix3D;
import processing.core.PVector;

/**
 * Maintains consistent behavior across renderers in the
 * CamZup library.
 *
 * Secondarily, supports core to Processing conversions
 * (e.g., PVector to Vec3) and altered PMatrix3D functions.
 * (PMatrix3D is marked final and thus cannot be extended.)
 */
public interface IUp {

   /**
    * Assumes 1920 / 1080, 1.7778 .
    */
   float DEFAULT_ASPECT = 1.7777778f;

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
    * Assumes height of 1080 . 1080 / 2 = 540 .
    */
   float DEFAULT_HALF_HEIGHT = 540.0f;

   /**
    * Assumes width of 1920 . 1920 / 2 = 960 .
    */
   float DEFAULT_HALF_WIDTH = 960.0f;

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
   float DEFAULT_STROKE_WEIGHT = 1.0f;

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
    * Helper function for inverting a PMatrix3D. Finds the
    * determinant for a 3x3 section of a 4x4 matrix.
    *
    * @param t00
    *           row 0, column 0
    * @param t01
    *           row 0, column 1
    * @param t02
    *           row 0, column 2
    * @param t10
    *           row 1, column 0
    * @param t11
    *           row 1, column 1
    * @param t12
    *           row 1, column 2
    * @param t20
    *           row 2, column 0
    * @param t21
    *           row 2, column 1
    * @param t22
    *           row 2, column 2
    * @return the determinant
    */
   static float det3x3 (
         final float t00, final float t01, final float t02,
         final float t10, final float t11, final float t12,
         final float t20, final float t21, final float t22 ) {

      return t00 * (t11 * t22 - t12 * t21) +
            t01 * (t12 * t20 - t10 * t22) +
            t02 * (t10 * t21 - t11 * t20);
   }

   /**
    * Creates a view frustum given the edges of the view port.
    *
    * @param left
    *           the left edge of the window
    * @param right
    *           the right edge of the window
    * @param bottom
    *           the bottom edge of the window
    * @param top
    *           the top edge of the window
    * @param near
    *           the near clip plane
    * @param far
    *           the far clip plane
    * @return the view frustum
    */
   static PMatrix3D frustum (
         final float left, final float right,
         final float bottom, final float top,
         final float near, final float far ) {

      return IUp.frustum(
            left, right,
            bottom, top,
            near, far,
            (PMatrix3D) null);
   }

   /**
    * Creates a view frustum given the edges of the view port.
    *
    * @param left
    *           the left edge of the window
    * @param right
    *           the right edge of the window
    * @param bottom
    *           the bottom edge of the window
    * @param top
    *           the top edge of the window
    * @param near
    *           the near clip plane
    * @param far
    *           the far clip plane
    * @param target
    *           the output matrix
    * @return the view frustum
    */
   static PMatrix3D frustum (
         final float left, final float right,
         final float bottom, final float top,
         final float near, final float far,

         PMatrix3D target ) {

      if (target == null) {
         target = new PMatrix3D();
      }

      final float n2 = near + near;
      final float w = 1.0f / Utils.max(Utils.EPSILON, right - left);
      final float h = 1.0f / Utils.max(Utils.EPSILON, top - bottom);
      final float d = 1.0f / Utils.max(Utils.EPSILON, far - near);

      target.set(
            n2 * w, 0.0f, (right + left) * w, 0.0f,
            0.0f, n2 * h, (top + bottom) * h, 0.0f,
            0.0f, 0.0f, (far + near) * -d, n2 * far * -d,
            0.0f, 0.0f, -1.0f, 0.0f);
      return target;
   }

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
    * Inverts the input matrix. This is an expensive operation,
    * and so matrix inverses should be cached when needed.
    *
    * @param m
    *           the matrix
    * @param target
    *           the output matrix
    * @return the inverted matrix
    * @see IUp#det3x3(float, float, float, float, float, float,
    *      float, float, float)
    * @see PMatrix3D#determinant()
    */
   static PMatrix3D invert (
         final PMatrix3D m,
         final PMatrix3D target ) {

      final float det = m.determinant();
      if (det == 0.0f) {
         target.reset();
         return target;
      }

      final float detInv = 1.0f / det;

      target.m00 = detInv * IUp.det3x3(
            m.m11, m.m12, m.m13,
            m.m21, m.m22, m.m23,
            m.m31, m.m32, m.m33);
      target.m01 = detInv * -IUp.det3x3(
            m.m01, m.m02, m.m03,
            m.m21, m.m22, m.m23,
            m.m31, m.m32, m.m33);
      target.m02 = detInv * IUp.det3x3(
            m.m01, m.m02, m.m03,
            m.m11, m.m12, m.m13,
            m.m31, m.m32, m.m33);
      target.m03 = detInv * -IUp.det3x3(
            m.m01, m.m02, m.m03,
            m.m11, m.m12, m.m13,
            m.m21, m.m22, m.m23);

      target.m10 = detInv * -IUp.det3x3(
            m.m10, m.m12, m.m13,
            m.m20, m.m22, m.m23,
            m.m30, m.m32, m.m33);
      target.m11 = detInv * IUp.det3x3(
            m.m00, m.m02, m.m03,
            m.m20, m.m22, m.m23,
            m.m30, m.m32, m.m33);
      target.m12 = detInv * -IUp.det3x3(
            m.m00, m.m02, m.m03,
            m.m10, m.m12, m.m13,
            m.m30, m.m32, m.m33);
      target.m13 = detInv * IUp.det3x3(
            m.m00, m.m02, m.m03,
            m.m10, m.m12, m.m13,
            m.m20, m.m22, m.m23);

      target.m20 = detInv * IUp.det3x3(
            m.m10, m.m11, m.m13,
            m.m20, m.m21, m.m23,
            m.m30, m.m31, m.m33);
      target.m21 = detInv * -IUp.det3x3(
            m.m00, m.m01, m.m03,
            m.m20, m.m21, m.m23,
            m.m30, m.m31, m.m33);
      target.m22 = detInv * IUp.det3x3(
            m.m00, m.m01, m.m03,
            m.m10, m.m11, m.m13,
            m.m30, m.m31, m.m33);
      target.m23 = detInv * -IUp.det3x3(
            m.m00, m.m01, m.m03,
            m.m10, m.m11, m.m13,
            m.m20, m.m21, m.m23);

      target.m30 = detInv * -IUp.det3x3(
            m.m10, m.m11, m.m12,
            m.m20, m.m21, m.m22,
            m.m30, m.m31, m.m32);
      target.m31 = detInv * IUp.det3x3(
            m.m00, m.m01, m.m02,
            m.m20, m.m21, m.m22,
            m.m30, m.m31, m.m32);
      target.m32 = detInv * -IUp.det3x3(
            m.m00, m.m01, m.m02,
            m.m10, m.m11, m.m12,
            m.m30, m.m31, m.m32);
      target.m33 = detInv * IUp.det3x3(
            m.m00, m.m01, m.m02,
            m.m10, m.m11, m.m12,
            m.m20, m.m21, m.m22);

      return target;
   }

   static PMatrix3D invRotate (
         final float radians,
         final float xAxis,
         final float yAxis,
         final float zAxis ) {

      return IUp.invRotate(
            radians,
            xAxis, yAxis, zAxis,
            (PMatrix3D) null);
   }

   static PMatrix3D invRotate (
         final float radians,
         final float xAxis,
         final float yAxis,
         final float zAxis,
         PMatrix3D target ) {

      if (target == null) {
         target = new PMatrix3D();
      }

      final float c = PApplet.cos(-radians);
      final float s = PApplet.sin(-radians);
      final float t = 1.0f - c;

      final float sv0 = s * xAxis;
      final float sv1 = s * yAxis;
      final float sv2 = s * zAxis;

      final float tv0 = t * xAxis;
      final float tv1 = t * yAxis;
      final float tv2 = t * zAxis;

      target.preApply(
            tv0 * xAxis + c,
            tv0 * yAxis - sv2,
            tv0 * zAxis + sv1,
            0.0f,

            tv1 * xAxis + sv2,
            tv1 * yAxis + c,
            tv1 * zAxis - sv0,
            0.0f,

            tv2 * xAxis - sv1,
            tv2 * yAxis + sv0,
            tv2 * zAxis + c,
            0.0f,

            0.0f, 0.0f, 0.0f, 1.0f);
      return target;
   }

   static PMatrix3D invRotateX ( final float radians ) {

      return IUp.invRotateX(radians, (PMatrix3D) null);
   }

   static PMatrix3D invRotateX (
         final float radians,
         PMatrix3D target ) {

      if (target == null) {
         target = new PMatrix3D();
      }

      final float c = PApplet.cos(-radians);
      final float s = PApplet.sin(-radians);
      target.preApply(
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, c, -s, 0.0f,
            0.0f, s, c, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
      return target;
   }

   static PMatrix3D invRotateY ( final float radians ) {

      return IUp.invRotateY(radians, (PMatrix3D) null);
   }

   static PMatrix3D invRotateY (
         final float radians,
         PMatrix3D target ) {

      if (target == null) {
         target = new PMatrix3D();
      }

      final float c = PApplet.cos(-radians);
      final float s = PApplet.sin(-radians);
      target.preApply(
            c, 0.0f, s, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            -s, 0.0f, c, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
      return target;
   }

   static PMatrix3D invRotateZ ( final float radians ) {

      return IUp.invRotateZ(radians, (PMatrix3D) null);
   }

   static PMatrix3D invRotateZ ( final float radians,
         PMatrix3D target ) {

      if (target == null) {
         target = new PMatrix3D();
      }

      final float c = PApplet.cos(-radians);
      final float s = PApplet.sin(-radians);
      target.preApply(
            c, -s, 0.0f, 0.0f,
            s, c, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
      return target;
   }

   /**
    * Multiplies two matrices together. Matrix multiplication
    * is not commutative, so the PMatrix3D class calls in-place
    * multiplication "apply" and "preApply."
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @return the product
    * @see PMatrix3D#apply(PMatrix3D)
    * @see PMatrix3D#preApply(PMatrix3D)
    */
   static PMatrix3D mul (
         final PMatrix3D a,
         final PMatrix3D b ) {

      return IUp.mul(a, b, (PMatrix3D) null);
   }

   /**
    * Multiplies two matrices together. Matrix multiplication
    * is not commutative, so the PMatrix3D class calls in-place
    * multiplication "apply" and "preApply."
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output matrix
    * @return the product
    * @see PMatrix3D#apply(PMatrix3D)
    * @see PMatrix3D#preApply(PMatrix3D)
    */
   static PMatrix3D mul (
         final PMatrix3D a,
         final PMatrix3D b,
         PMatrix3D target ) {

      if (target == null) {
         target = new PMatrix3D();
      }

      final float n00 = a.m00 * b.m00 +
            a.m01 * b.m10 +
            a.m02 * b.m20 +
            a.m03 * b.m30;
      final float n01 = a.m00 * b.m01 +
            a.m01 * b.m11 +
            a.m02 * b.m21 +
            a.m03 * b.m31;
      final float n02 = a.m00 * b.m02 +
            a.m01 * b.m12 +
            a.m02 * b.m22 +
            a.m03 * b.m32;
      final float n03 = a.m00 * b.m03 +
            a.m01 * b.m13 +
            a.m02 * b.m23 +
            a.m03 * b.m33;

      final float n10 = a.m10 * b.m00 +
            a.m11 * b.m10 +
            a.m12 * b.m20 +
            a.m13 * b.m30;
      final float n11 = a.m10 * b.m01 +
            a.m11 * b.m11 +
            a.m12 * b.m21 +
            a.m13 * b.m31;
      final float n12 = a.m10 * b.m02 +
            a.m11 * b.m12 +
            a.m12 * b.m22 +
            a.m13 * b.m32;
      final float n13 = a.m10 * b.m03 +
            a.m11 * b.m13 +
            a.m12 * b.m23 +
            a.m13 * b.m33;

      final float n20 = a.m20 * b.m00 +
            a.m21 * b.m10 +
            a.m22 * b.m20 +
            a.m23 * b.m30;
      final float n21 = a.m20 * b.m01 +
            a.m21 * b.m11 +
            a.m22 * b.m21 +
            a.m23 * b.m31;
      final float n22 = a.m20 * b.m02 +
            a.m21 * b.m12 +
            a.m22 * b.m22 +
            a.m23 * b.m32;
      final float n23 = a.m20 * b.m03 +
            a.m21 * b.m13 +
            a.m22 * b.m23 +
            a.m23 * b.m33;

      final float n30 = a.m30 * b.m00 +
            a.m31 * b.m10 +
            a.m32 * b.m20 +
            a.m33 * b.m30;
      final float n31 = a.m30 * b.m01 +
            a.m31 * b.m11 +
            a.m32 * b.m21 +
            a.m33 * b.m31;
      final float n32 = a.m30 * b.m02 +
            a.m31 * b.m12 +
            a.m32 * b.m22 +
            a.m33 * b.m32;
      final float n33 = a.m30 * b.m03 +
            a.m31 * b.m13 +
            a.m32 * b.m23 +
            a.m33 * b.m33;

      target.m00 = n00;
      target.m01 = n01;
      target.m02 = n02;
      target.m03 = n03;
      target.m10 = n10;
      target.m11 = n11;
      target.m12 = n12;
      target.m13 = n13;
      target.m20 = n20;
      target.m21 = n21;
      target.m22 = n22;
      target.m23 = n23;
      target.m30 = n30;
      target.m31 = n31;
      target.m32 = n32;
      target.m33 = n33;

      return target;
   }

   /**
    * Multiplies a matrix with a four dimensional vector.
    *
    * @param m
    *           the matrix
    * @param v
    *           the input vector
    * @param target
    *           the output vector
    * @return the product
    */
   static Vec4 mul (
         final PMatrix3D m,
         final Vec4 v,
         final Vec4 target ) {

      return target.set(
            m.m00 * v.x + m.m01 * v.y + m.m02 * v.z + m.m03 * v.w,
            m.m10 * v.x + m.m11 * v.y + m.m12 * v.z + m.m13 * v.w,
            m.m20 * v.x + m.m21 * v.y + m.m22 * v.z + m.m23 * v.w,
            m.m30 * v.x + m.m31 * v.y + m.m32 * v.z + m.m33 * v.w);
   }

   /**
    * Multiplies a matrix with a three dimensional point, where
    * its implicit fourth coordinate, w, is 1.0 .
    *
    * @param m
    *           the matrix
    * @param v
    *           the input vector
    * @return the product
    */
   static PVector mulPoint ( final PMatrix3D m, final PVector v ) {

      return IUp.mulPoint(m, v, (PVector) null);
   }

   /**
    * Multiplies a matrix with a three dimensional point, where
    * its implicit fourth coordinate, w, is 1.0 .
    *
    * @param m
    *           the matrix
    * @param v
    *           the input vector
    * @param target
    *           the output vector
    * @return the product
    */
   static PVector mulPoint (
         final PMatrix3D m,
         final PVector v,
         PVector target ) {

      if (target == null) {
         target = new PVector();
      }

      final float w = m.m30 * v.x + m.m31 * v.y + m.m32 * v.z + m.m33;
      if (w == 0.0f) {
         return target.set(0.0f, 0.0f, 0.0f);
      }
      final float wInv = 1.0f / w;
      return target.set(
            (m.m00 * v.x + m.m01 * v.y + m.m02 * v.z + m.m03) * wInv,
            (m.m10 * v.x + m.m11 * v.y + m.m12 * v.z + m.m13) * wInv,
            (m.m20 * v.x + m.m21 * v.y + m.m22 * v.z + m.m23) * wInv);
   }

   /**
    * Multiplies a matrix with a three dimensional point, where
    * its implicit fourth coordinate, w, is 1.0 .
    *
    * @param m
    *           the matrix
    * @param v
    *           the input vector
    * @param target
    *           the output vector
    * @return the product
    */
   static Vec3 mulPoint (
         final PMatrix3D m,
         final Vec3 v,
         final Vec3 target ) {

      final float w = m.m30 * v.x + m.m31 * v.y + m.m32 * v.z + m.m33;
      if (w == 0.0f) {
         return target.reset();
      }
      final float wInv = 1.0f / w;
      return target.set(
            (m.m00 * v.x + m.m01 * v.y + m.m02 * v.z + m.m03) * wInv,
            (m.m10 * v.x + m.m11 * v.y + m.m12 * v.z + m.m13) * wInv,
            (m.m20 * v.x + m.m21 * v.y + m.m22 * v.z + m.m23) * wInv);
   }

   /**
    * Creates an orthographic projection matrix, where objects
    * maintain their size regardless of distance from the
    * camera.
    *
    * @param left
    *           the left edge of the window
    * @param right
    *           the right edge of the window
    * @param bottom
    *           the bottom edge of the window
    * @param top
    *           the top edge of the window
    * @param near
    *           the near clip plane
    * @param far
    *           the far clip plane
    * @return the orthographic projection
    */
   static PMatrix3D orthographic (
         final float left, final float right,
         final float bottom, final float top,
         final float near, final float far ) {

      return IUp.orthographic(
            left, right,
            bottom, top,
            near, far,
            (PMatrix3D) null);
   }

   /**
    * Creates an orthographic projection matrix, where objects
    * maintain their size regardless of distance from the
    * camera.
    *
    * @param left
    *           the left edge of the window
    * @param right
    *           the right edge of the window
    * @param bottom
    *           the bottom edge of the window
    * @param top
    *           the top edge of the window
    * @param near
    *           the near clip plane
    * @param far
    *           the far clip plane
    * @param target
    *           the output matrix
    * @return the orthographic projection
    */
   static PMatrix3D orthographic (
         final float left, final float right,
         final float bottom, final float top,
         final float near, final float far,

         PMatrix3D target ) {

      if (target == null) {
         target = new PMatrix3D();
      }

      final float w = 1.0f / Utils.max(Utils.EPSILON, right - left);
      final float h = 1.0f / Utils.max(Utils.EPSILON, top - bottom);
      final float d = 1.0f / Utils.max(Utils.EPSILON, far - near);

      target.set(
            w + w, 0.0f, 0.0f, w * (left + right),
            0.0f, h + h, 0.0f, h * (top + bottom),
            0.0f, 0.0f, -(d + d), -d * (far + near),
            0.0f, 0.0f, 0.0f, 1.0f);
      return target;
   }

   /**
    * Creates a perspective projection matrix, where objects
    * nearer to the camera appear larger than objects distant
    * from the camera.
    *
    * @param fov
    *           the field of view
    * @param aspect
    *           the aspect ratio, width over height
    * @param near
    *           the near clip plane
    * @param far
    *           the far clip plane
    * @return the perspective projection
    */
   static PMatrix3D perspective (
         final float fov, final float aspect,
         final float near, final float far ) {

      return IUp.perspective(
            fov, aspect, near, far,
            (PMatrix3D) null);
   }

   /**
    * Creates a perspective projection matrix, where objects
    * nearer to the camera appear larger than objects distant
    * from the camera.
    *
    * @param fov
    *           the field of view
    * @param aspect
    *           the aspect ratio, width over height
    * @param near
    *           the near clip plane
    * @param far
    *           the far clip plane
    * @param target
    *           the output matrix
    * @return the perspective projection
    */
   static PMatrix3D perspective (
         final float fov,
         final float aspect,
         final float near,
         final float far,
         PMatrix3D target ) {

      if (target == null) {
         target = new PMatrix3D();
      }

      final float tanfov = (float) Math.tan(fov * 0.5d);
      final float d = Utils.div(1.0f, far - near);
      target.set(
            Utils.div(1.0f, tanfov * aspect), 0.0f, 0.0f, 0.0f,
            0.0f, Utils.div(1.0f, tanfov), 0.0f, 0.0f,
            0.0f, 0.0f, (far + near) * -d, (near + near) * far * -d,
            0.0f, 0.0f, -1.0f, 0.0f);

      return target;
   }

   static PMatrix3D rotate (
         final float radians,
         final float xAxis,
         final float yAxis,
         final float zAxis ) {

      return IUp.rotate(radians,
            xAxis, yAxis, zAxis,
            (PMatrix3D) null);
   }

   static PMatrix3D rotate (
         final float radians,
         float xAxis,
         float yAxis,
         float zAxis,
         PMatrix3D target ) {

      if (target == null) {
         target = new PMatrix3D();
      }

      final float mSq = xAxis * xAxis +
            yAxis * yAxis +
            zAxis * zAxis;
      if (mSq == 0.0f) {
         // target.reset();
         return target;
      }

      if (mSq != 1.0f) {
         final float mInv = 1.0f / PApplet.sqrt(mSq);
         xAxis *= mInv;
         yAxis *= mInv;
         zAxis *= mInv;
      }

      final float c = PApplet.cos(radians);
      final float s = PApplet.sin(radians);
      final float t = 1.0f - c;

      final float tax = t * xAxis;
      final float taz = t * zAxis;
      final float tay = t * yAxis;

      final float saz = s * zAxis;
      final float sax = s * xAxis;
      final float say = s * yAxis;

      target.apply(
            tax * xAxis + c,
            tay * xAxis - saz,
            tax * zAxis + say,
            0.0f,

            tax * yAxis + saz,
            tay * yAxis + c,
            taz * yAxis - sax,
            0.0f,

            taz * xAxis - say,
            tay * zAxis + sax,
            taz * zAxis + c,
            0.0f,

            0.0f, 0.0f, 0.0f, 1.0f);

      return target;
   }

   /**
    * PMatrix3D's instance methods for rotating around
    * orthonormal axes defer to rotation about an arbitrary
    * axis. This is not necessary for rotate X.
    *
    * @param radians
    *           the angle in radians
    * @return the rotation matrix
    */
   static PMatrix3D rotateX ( final float radians ) {

      return IUp.rotateX(radians, (PMatrix3D) null);
   }

   /**
    * PMatrix3D's instance methods for rotating around
    * orthonormal axes defer to rotation about an arbitrary
    * axis. This is not necessary for rotate X.
    *
    * @param radians
    *           the angle in radians
    * @param target
    *           the matrix
    * @return the mutated matrix
    */
   static PMatrix3D rotateX (
         final float radians,
         PMatrix3D target ) {

      if (target == null) {
         target = new PMatrix3D();
      }

      final float c = PApplet.cos(radians);
      final float s = PApplet.sin(radians);

      float t1 = target.m01;
      float t2 = target.m02;
      final float n01 = t1 * c + t2 * s;
      final float n02 = t1 * -s + t2 * c;

      t1 = target.m11;
      t2 = target.m12;
      final float n11 = t1 * c + t2 * s;
      final float n12 = t1 * -s + t2 * c;

      t1 = target.m21;
      t2 = target.m22;
      final float n21 = t1 * c + t2 * s;
      final float n22 = t1 * -s + t2 * c;

      t1 = target.m31;
      t2 = target.m32;
      final float n31 = t1 * c + t2 * s;
      final float n32 = t1 * -s + t2 * c;

      target.m01 = n01;
      target.m02 = n02;
      target.m11 = n11;
      target.m12 = n12;
      target.m21 = n21;
      target.m22 = n22;
      target.m31 = n31;
      target.m32 = n32;

      return target;
   }

   /**
    * PMatrix3D's instance methods for rotating around
    * orthonormal axes defer to rotation about an arbitrary
    * axis. This is not necessary for rotate Y.
    *
    * @param radians
    *           the angle in radians
    * @return the rotation matrix
    */
   static PMatrix3D rotateY ( final float radians ) {

      return IUp.rotateY(radians, (PMatrix3D) null);
   }

   /**
    * PMatrix3D's instance methods for rotating around
    * orthonormal axes defer to rotation about an arbitrary
    * axis. This is not necessary for rotate Y.
    *
    * @param radians
    *           the angle in radians
    * @param target
    *           the matrix
    * @return the mutated matrix
    */
   static PMatrix3D rotateY (
         final float radians,
         PMatrix3D target ) {

      if (target == null) {
         target = new PMatrix3D();
      }

      final float c = PApplet.cos(radians);
      final float s = PApplet.sin(radians);

      float t0 = target.m00;
      float t2 = target.m02;
      final float n00 = t0 * c + t2 * -s;
      final float n02 = t0 * s + t2 * c;

      t0 = target.m10;
      t2 = target.m12;
      final float n10 = t0 * c + t2 * -s;
      final float n12 = t0 * s + t2 * c;

      t0 = target.m20;
      t2 = target.m22;
      final float n20 = t0 * c + t2 * -s;
      final float n22 = t0 * s + t2 * c;

      t0 = target.m30;
      t2 = target.m32;
      final float n30 = t0 * c + t2 * -s;
      final float n32 = t0 * s + t2 * c;

      target.m00 = n00;
      target.m02 = n02;
      target.m10 = n10;
      target.m12 = n12;
      target.m20 = n20;
      target.m22 = n22;
      target.m30 = n30;
      target.m32 = n32;

      return target;
   }

   /**
    * PMatrix3D's instance methods for rotating around
    * orthonormal axes defer to rotation about an arbitrary
    * axis. This is not necessary for rotate Z.
    *
    * @param radians
    *           the angle in radians
    * @return the rotation matrix
    */
   static PMatrix3D rotateZ ( final float radians ) {

      return IUp.rotateZ(radians, (PMatrix3D) null);
   }

   /**
    * PMatrix3D's instance methods for rotating around
    * orthonormal axes defer to rotation about an arbitrary
    * axis. This is not necessary for rotate Z.
    *
    * @param radians
    *           the angle in radians
    * @param target
    *           the matrix
    * @return the mutated matrix
    */
   static PMatrix3D rotateZ (
         final float radians,
         PMatrix3D target ) {

      if (target == null) {
         target = new PMatrix3D();
      }

      final float c = PApplet.cos(radians);
      final float s = PApplet.sin(radians);

      float t0 = target.m00;
      float t1 = target.m01;
      final float n00 = t0 * c + t1 * s;
      final float n01 = t0 * -s + t1 * c;

      t0 = target.m10;
      t1 = target.m11;
      final float n10 = t0 * c + t1 * s;
      final float n11 = t0 * -s + t1 * c;

      t0 = target.m20;
      t1 = target.m21;
      final float n20 = t0 * c + t1 * s;
      final float n21 = t0 * -s + t1 * c;

      t0 = target.m30;
      t1 = target.m31;
      final float n30 = t0 * c + t1 * s;
      final float n31 = t0 * -s + t1 * c;

      target.m00 = n00;
      target.m01 = n01;
      target.m10 = n10;
      target.m11 = n11;
      target.m20 = n20;
      target.m21 = n21;
      target.m30 = n30;
      target.m31 = n31;

      return target;
   }

   /**
    * Prints a matrix with a default format.
    *
    * @param m
    *           the matrix
    * @param places
    *           number of decimal places
    * @return the string
    */
   static String toString (
         final PMatrix3D m,
         final int places ) {

      return new StringBuilder(320)
            .append("{ elms: [\n ")

            .append(Utils.toFixed(m.m00, places))
            .append(',').append(' ')
            .append(Utils.toFixed(m.m01, places))
            .append(',').append(' ')
            .append(Utils.toFixed(m.m02, places))
            .append(',').append(' ')
            .append(Utils.toFixed(m.m03, places))
            .append(',').append(' ').append('\n')

            .append(Utils.toFixed(m.m10, places))
            .append(',').append(' ')
            .append(Utils.toFixed(m.m11, places))
            .append(',').append(' ')
            .append(Utils.toFixed(m.m12, places))
            .append(',').append(' ')
            .append(Utils.toFixed(m.m13, places))
            .append(',').append(' ').append('\n')

            .append(Utils.toFixed(m.m20, places))
            .append(',').append(' ')
            .append(Utils.toFixed(m.m21, places))
            .append(',').append(' ')
            .append(Utils.toFixed(m.m22, places))
            .append(',').append(' ')
            .append(Utils.toFixed(m.m23, places))
            .append(',').append(' ').append('\n')

            .append(Utils.toFixed(m.m30, places))
            .append(',').append(' ')
            .append(Utils.toFixed(m.m31, places))
            .append(',').append(' ')
            .append(Utils.toFixed(m.m32, places))
            .append(',').append(' ')
            .append(Utils.toFixed(m.m33, places))

            .append(" ] }")
            .toString();
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
