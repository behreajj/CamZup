package camzup.pfriendly;

import camzup.core.IUtils;
import camzup.core.Quaternion;
import camzup.core.Utils;
import camzup.core.Vec3;

import processing.core.PMatrix2D;
import processing.core.PMatrix3D;

/**
 * The PMatrix class is marked final and hence cannot be extended and
 * augmented. Due to its use in PGraphicsOpenGL it cannot be ignored. This
 * class stores static functions which alter and/or supplement PMatrix
 * functions.<br>
 * <br>
 * This class is <em>ad hoc</em>, and primarily focused on assisting
 * functions in OpenGL renderers; therefore, it might not follow all style
 * guidelines (e.g., it may mutate matrices in place).
 */
public abstract class PMatAux {

   /**
    * Discourage overriding with a private constructor.
    */
   private PMatAux ( ) {}

   /**
    * Returns a matrix set to the Bezier curve basis inverse:
    *
    * <pre>
    * 0.0,        0.0,        0.0, 1.0,
    * 0.0,        0.0, 0.33333334, 1.0,
    * 0.0, 0.33333334, 0.66666666, 1.0,
    * 1.0,        1.0,        1.0, 1.0
    * </pre>
    *
    * @param target the output matrix
    *
    * @return the Bezier basis
    */
   public static PMatrix3D bezierBasisInverse ( final PMatrix3D target ) {

      target.set(0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.33333334f, 1.0f, 0.0f,
         0.33333334f, 0.66666666f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);

      return target;
   }

   /**
    * Returns a matrix set to the Catmull-Rom basis, according to a curve
    * tightness.
    *
    * @param s      the curve tightness
    * @param target the output matrix
    *
    * @return the basis
    */
   public static PMatrix3D catmullBasis ( final float s,
      final PMatrix3D target ) {

      final float u = 1.0f - s;
      final float sh = s * 0.5f;
      final float th = sh - 0.5f;
      final float uh = u * 0.5f;
      final float v = sh + 1.5f;

      /* @formatter:off */
      target.set(
           th,          v,       -v,   uh,
            u, -2.5f - sh, s + 2.0f,   th,
           th,       0.0f,       uh, 0.0f,
         0.0f,       1.0f,     0.0f, 0.0f);
      return target;
      /* @formatter:on */
   }

   /**
    * Rotates a matrix and its inverse together by an axis and the cosine and
    * sine of an angle. Does not check whether the axis is normalized or
    * valid.
    *
    * @param c     cosine of the angle
    * @param s     sine of the angle
    * @param xAxis the axis x
    * @param yAxis the axis y
    * @param zAxis the axis z
    * @param m     the matrix
    * @param mInv  the matrix inverse
    *
    * @return the matrix
    */
   public static PMatrix3D compoundRotate ( final float c, final float s,
      final float xAxis, final float yAxis, final float zAxis,
      final PMatrix3D m, final PMatrix3D mInv ) {

      final float sax = s * xAxis;
      final float say = s * yAxis;
      final float saz = s * zAxis;

      final float t = 1.0f - c;
      final float tax = t * xAxis;
      final float tay = t * yAxis;
      final float taz = t * zAxis;

      final float n00 = tax * xAxis + c;
      final float n11 = tay * yAxis + c;
      final float n22 = taz * zAxis + c;
      final float n01 = tax * yAxis - saz;
      final float n10 = tay * xAxis + saz;
      final float n02 = tax * zAxis + say;
      final float n20 = taz * xAxis - say;
      final float n12 = tay * zAxis - sax;
      final float n21 = taz * yAxis + sax;

      /* @formatter:off */
      m.set(
         m.m00 * n00 + m.m01 * n01 + m.m02 * n02,
         m.m00 * n10 + m.m01 * n11 + m.m02 * n12,
         m.m00 * n20 + m.m01 * n21 + m.m02 * n22,
         m.m03,

         m.m10 * n00 + m.m11 * n01 + m.m12 * n02,
         m.m10 * n10 + m.m11 * n11 + m.m12 * n12,
         m.m10 * n20 + m.m11 * n21 + m.m12 * n22,
         m.m13,

         m.m20 * n00 + m.m21 * n01 + m.m22 * n02,
         m.m20 * n10 + m.m21 * n11 + m.m22 * n12,
         m.m20 * n20 + m.m21 * n21 + m.m22 * n22,
         m.m23,

         m.m30 * n00 + m.m31 * n01 + m.m32 * n02,
         m.m30 * n10 + m.m31 * n11 + m.m32 * n12,
         m.m30 * n20 + m.m31 * n21 + m.m32 * n22,
         m.m33);

      mInv.set(
         n00 * mInv.m00 + n01 * mInv.m10 + n02 * mInv.m20,
         n00 * mInv.m01 + n01 * mInv.m11 + n02 * mInv.m21,
         n00 * mInv.m02 + n01 * mInv.m12 + n02 * mInv.m22,
         n00 * mInv.m03 + n01 * mInv.m13 + n02 * mInv.m23,

         n10 * mInv.m00 + n11 * mInv.m10 + n12 * mInv.m20,
         n10 * mInv.m01 + n11 * mInv.m11 + n12 * mInv.m21,
         n10 * mInv.m02 + n11 * mInv.m12 + n12 * mInv.m22,
         n10 * mInv.m03 + n11 * mInv.m13 + n12 * mInv.m23,

         n20 * mInv.m00 + n21 * mInv.m10 + n22 * mInv.m20,
         n20 * mInv.m01 + n21 * mInv.m11 + n22 * mInv.m21,
         n20 * mInv.m02 + n21 * mInv.m12 + n22 * mInv.m22,
         n20 * mInv.m03 + n21 * mInv.m13 + n22 * mInv.m23,
         mInv.m30, mInv.m31, mInv.m32, mInv.m33);
      /* @formatter:on */

      return m;
   }

   /**
    * Rotates a matrix and its inverse together by an axis an angle. Does not
    * check whether the axis is normalized or valid.
    *
    * @param radians the angle
    * @param xAxis   the axis x
    * @param yAxis   the axis y
    * @param zAxis   the axis z
    * @param m       the matrix
    * @param mInv    the matrix inverse
    *
    * @return the matrix
    */
   public static PMatrix3D compoundRotate ( final float radians,
      final float xAxis, final float yAxis, final float zAxis,
      final PMatrix3D m, final PMatrix3D mInv ) {

      final float normRad = -radians * IUtils.ONE_TAU;
      return PMatAux.compoundRotate(Utils.scNorm(normRad), Utils.scNorm(normRad
         - 0.25f), xAxis, yAxis, zAxis, m, mInv);
   }

   /**
    * Rotates a matrix and its inverse around the x axis by the cosine and
    * sine of an angle.
    *
    * @param c    the cosine
    * @param s    the sine
    * @param m    the matrix
    * @param mInv the matrix inverse
    *
    * @return the rotated matrix
    */
   public static PMatrix3D compoundRotateX ( final float c, final float s,
      final PMatrix3D m, final PMatrix3D mInv ) {

      float t1 = m.m01;
      float t2 = m.m02;
      m.m01 = t1 * c + t2 * s;
      m.m02 = t2 * c - t1 * s;

      t1 = m.m11;
      t2 = m.m12;
      m.m11 = t1 * c + t2 * s;
      m.m12 = t2 * c - t1 * s;

      t1 = m.m21;
      t2 = m.m22;
      m.m21 = t1 * c + t2 * s;
      m.m22 = t2 * c - t1 * s;

      t1 = m.m31;
      m.m31 = t1 * c + m.m32 * s;
      m.m32 = m.m32 * c - t1 * s;

      /* @formatter:off */
      mInv.set(
         mInv.m00, mInv.m01, mInv.m02, mInv.m03,
         c * mInv.m10 + s * mInv.m20,
         c * mInv.m11 + s * mInv.m21,
         c * mInv.m12 + s * mInv.m22,
         c * mInv.m13 + s * mInv.m23,
         c * mInv.m20 - s * mInv.m10,
         c * mInv.m21 - s * mInv.m11,
         c * mInv.m22 - s * mInv.m12,
         c * mInv.m23 - s * mInv.m13,
         mInv.m30, mInv.m31, mInv.m32, mInv.m33);

      return m;
      /* @formatter:on */
   }

   /**
    * Rotates a matrix and its inverse around the y axis by the cosine and
    * sine of an angle.
    *
    * @param c    the cosine
    * @param s    the sine
    * @param m    the matrix
    * @param mInv the matrix inverse
    *
    * @return the rotated matrix
    */
   public static PMatrix3D compoundRotateY ( final float c, final float s,
      final PMatrix3D m, final PMatrix3D mInv ) {

      float t0 = m.m00;
      float t2 = m.m02;
      m.m00 = t0 * c - t2 * s;
      m.m02 = t0 * s + t2 * c;

      t0 = m.m10;
      t2 = m.m12;
      m.m10 = t0 * c - t2 * s;
      m.m12 = t0 * s + t2 * c;

      t0 = m.m20;
      t2 = m.m22;
      m.m20 = t0 * c - t2 * s;
      m.m22 = t0 * s + t2 * c;

      t0 = m.m30;
      m.m30 = t0 * c - m.m32 * s;
      m.m32 = t0 * s + m.m32 * c;

      /* @formatter:off */
      mInv.set(
         c * mInv.m00 - s * mInv.m20,
         c * mInv.m01 - s * mInv.m21,
         c * mInv.m02 - s * mInv.m22,
         c * mInv.m03 - s * mInv.m23,
         mInv.m10, mInv.m11, mInv.m12, mInv.m13,
         c * mInv.m20 + s * mInv.m00,
         c * mInv.m21 + s * mInv.m01,
         c * mInv.m22 + s * mInv.m02,
         c * mInv.m23 + s * mInv.m03,
         mInv.m30, mInv.m31, mInv.m32, mInv.m33);

      return m;
      /* @formatter:on */
   }

   /**
    * Rotates a matrix and its inverse around the z axis by the cosine and
    * sine of an angle.
    *
    * @param c    the cosine
    * @param s    the sine
    * @param m    the matrix
    * @param mInv the matrix inverse
    *
    * @return the rotated matrix
    */
   public static PMatrix3D compoundRotateZ ( final float c, final float s,
      final PMatrix3D m, final PMatrix3D mInv ) {

      float t0 = m.m00;
      float t1 = m.m01;
      m.m00 = t0 * c + t1 * s;
      m.m01 = t1 * c - t0 * s;

      t0 = m.m10;
      t1 = m.m11;
      m.m10 = t0 * c + t1 * s;
      m.m11 = t1 * c - t0 * s;

      t0 = m.m20;
      t1 = m.m21;
      m.m20 = t0 * c + t1 * s;
      m.m21 = t1 * c - t0 * s;

      t0 = m.m30;
      m.m30 = t0 * c + m.m31 * s;
      m.m31 = m.m31 * c - t0 * s;

      /* @formatter:off */
      mInv.set(
         c * mInv.m00 + s * mInv.m10,
         c * mInv.m01 + s * mInv.m11,
         c * mInv.m02 + s * mInv.m12,
         c * mInv.m03 + s * mInv.m13,
         c * mInv.m10 - s * mInv.m00,
         c * mInv.m11 - s * mInv.m01,
         c * mInv.m12 - s * mInv.m02,
         c * mInv.m13 - s * mInv.m03,
         mInv.m20, mInv.m21, mInv.m22, mInv.m23,
         mInv.m30, mInv.m31, mInv.m32, mInv.m33);

      return m;
      /* @formatter:on */
   }

   /**
    * Creates a view frustum given the edges of the view port.
    *
    * @param left   the left edge of the window
    * @param right  the right edge of the window
    * @param bottom the bottom edge of the window
    * @param top    the top edge of the window
    * @param near   the near clip plane
    * @param far    the far clip plane
    * @param target the output matrix
    *
    * @return the view frustum
    */
   public static PMatrix3D frustum ( final float left, final float right,
      final float bottom, final float top, final float near, final float far,
      final PMatrix3D target ) {

      final float n2 = near + near;

      float w = right - left;
      float h = top - bottom;
      float d = far - near;

      w = w != 0.0f ? 1.0f / w : 1.0f;
      h = h != 0.0f ? 1.0f / h : 1.0f;
      d = d != 0.0f ? 1.0f / d : 1.0f;

      /* @formatter:off */
      target.set(
         n2 * w,   0.0f, ( right + left ) * w,          0.0f,
           0.0f, n2 * h, ( top + bottom ) * h,          0.0f,
           0.0f,   0.0f,  ( far + near ) * -d, n2 * far * -d,
           0.0f,   0.0f,                -1.0f,           0.0f);
      return target;
      /* @formatter:on */
   }

   /**
    * Inverts the input matrix. This is an expensive operation, and so matrix
    * inverses should be cached when needed.
    *
    * @param m      the matrix
    * @param target the output matrix
    *
    * @return the inverted matrix
    */
   public static PMatrix3D inverse ( final PMatrix3D m,
      final PMatrix3D target ) {

      final float b00 = m.m00 * m.m11 - m.m01 * m.m10;
      final float b01 = m.m00 * m.m12 - m.m02 * m.m10;
      final float b02 = m.m00 * m.m13 - m.m03 * m.m10;
      final float b03 = m.m01 * m.m12 - m.m02 * m.m11;
      final float b04 = m.m01 * m.m13 - m.m03 * m.m11;
      final float b05 = m.m02 * m.m13 - m.m03 * m.m12;
      final float b06 = m.m20 * m.m31 - m.m21 * m.m30;
      final float b07 = m.m20 * m.m32 - m.m22 * m.m30;
      final float b08 = m.m20 * m.m33 - m.m23 * m.m30;
      final float b09 = m.m21 * m.m32 - m.m22 * m.m31;
      final float b10 = m.m21 * m.m33 - m.m23 * m.m31;
      final float b11 = m.m22 * m.m33 - m.m23 * m.m32;

      /* @formatter:off */
      final float det = b00 * b11 - b01 * b10 +
                        b02 * b09 + b03 * b08 -
                        b04 * b07 + b05 * b06;
      if ( det != 0.0f ) {
         final float detInv = 1.0f / det;
         target.set(
            ( m.m11 * b11 - m.m12 * b10 + m.m13 * b09 ) * detInv,
            ( m.m02 * b10 - m.m01 * b11 - m.m03 * b09 ) * detInv,
            ( m.m31 * b05 - m.m32 * b04 + m.m33 * b03 ) * detInv,
            ( m.m22 * b04 - m.m21 * b05 - m.m23 * b03 ) * detInv,
            ( m.m12 * b08 - m.m10 * b11 - m.m13 * b07 ) * detInv,
            ( m.m00 * b11 - m.m02 * b08 + m.m03 * b07 ) * detInv,
            ( m.m32 * b02 - m.m30 * b05 - m.m33 * b01 ) * detInv,
            ( m.m20 * b05 - m.m22 * b02 + m.m23 * b01 ) * detInv,
            ( m.m10 * b10 - m.m11 * b08 + m.m13 * b06 ) * detInv,
            ( m.m01 * b08 - m.m00 * b10 - m.m03 * b06 ) * detInv,
            ( m.m30 * b04 - m.m31 * b02 + m.m33 * b00 ) * detInv,
            ( m.m21 * b02 - m.m20 * b04 - m.m23 * b00 ) * detInv,
            ( m.m11 * b07 - m.m10 * b09 - m.m12 * b06 ) * detInv,
            ( m.m00 * b09 - m.m01 * b07 + m.m02 * b06 ) * detInv,
            ( m.m31 * b01 - m.m30 * b03 - m.m32 * b00 ) * detInv,
            ( m.m20 * b03 - m.m21 * b01 + m.m22 * b00 ) * detInv);
         return target;
      }
      /* @formatter:on */

      /* When the determinant is zero, Processing uses input matrix. */
      target.set(m);
      return target;
   }

   /**
    * Inverse-rotates a matrix by a quaternion in place. Does so by inverting
    * the matrix, converting it to a matrix, then multiplying the conversion
    * and the input matrix. (I.e., unlike rotation, the quaternion is the left
    * hand operand.)
    *
    * @param q      the quaternion
    * @param target the output matrix
    *
    * @return the matrix
    */
   public static PMatrix3D invRotate ( final Quaternion q,
      final PMatrix3D target ) {

      float w = 1.0f;
      float x = 0.0f;
      float y = 0.0f;
      float z = 0.0f;

      /* Find quaternion inverse. */
      final Vec3 qi = q.imag;
      final float mSq = q.real * q.real + Vec3.magSq(qi);
      if ( mSq != 0.0f ) {
         final float mSqInv = 1.0f / mSq;
         w = q.real * mSqInv;
         x = -qi.x * mSqInv;
         y = -qi.y * mSqInv;
         z = -qi.z * mSqInv;
      }

      final float x2 = x + x;
      final float y2 = y + y;
      final float z2 = z + z;
      final float xsq2 = x * x2;
      final float ysq2 = y * y2;
      final float zsq2 = z * z2;
      final float xy2 = x * y2;
      final float xz2 = x * z2;
      final float yz2 = y * z2;
      final float wx2 = w * x2;
      final float wy2 = w * y2;
      final float wz2 = w * z2;

      final float am00 = 1.0f - ysq2 - zsq2;
      final float am01 = xy2 - wz2;
      final float am02 = xz2 + wy2;

      final float am10 = xy2 + wz2;
      final float am11 = 1.0f - xsq2 - zsq2;
      final float am12 = yz2 - wx2;

      final float am20 = xz2 - wy2;
      final float am21 = yz2 + wx2;
      final float am22 = 1.0f - xsq2 - ysq2;

      /* @formatter:off */
      target.set(
         am00 * target.m00 + am01 * target.m10 + am02 * target.m20,
         am00 * target.m01 + am01 * target.m11 + am02 * target.m21,
         am00 * target.m02 + am01 * target.m12 + am02 * target.m22,
         am00 * target.m03 + am01 * target.m13 + am02 * target.m23,
         am10 * target.m00 + am11 * target.m10 + am12 * target.m20,
         am10 * target.m01 + am11 * target.m11 + am12 * target.m21,
         am10 * target.m02 + am11 * target.m12 + am12 * target.m22,
         am10 * target.m03 + am11 * target.m13 + am12 * target.m23,
         am20 * target.m00 + am21 * target.m10 + am22 * target.m20,
         am20 * target.m01 + am21 * target.m11 + am22 * target.m21,
         am20 * target.m02 + am21 * target.m12 + am22 * target.m22,
         am20 * target.m03 + am21 * target.m13 + am22 * target.m23,
         target.m30, target.m31, target.m32, target.m33);
      return target;
      /* @formatter:on */
   }

   /**
    * Applies the inverse scale to a matrix in-place. Equivalent to creating a
    * matrix from 1.0 divided by the scale, then pre-applying the scalar
    * matrix to the input.
    *
    * @param xScale the scale x
    * @param yScale the scale y
    * @param zScale the scale z
    * @param target the matrix
    *
    * @return the scaled matrix
    */
   public static PMatrix3D invScale ( final float xScale, final float yScale,
      final float zScale, final PMatrix3D target ) {

      final float x = xScale != 0.0f ? 1.0f / xScale : 1.0f;
      final float y = yScale != 0.0f ? 1.0f / yScale : 1.0f;
      final float z = zScale != 0.0f ? 1.0f / zScale : 1.0f;

      /* @formatter:off */
      target.set(
         x * target.m00, x * target.m01, x * target.m02, x * target.m03,
         y * target.m10, y * target.m11, y * target.m12, y * target.m13,
         z * target.m20, z * target.m21, z * target.m22, z * target.m23,
         target.m30, target.m31, target.m32, target.m33);
      return target;
      /* @formatter:on */
   }

   /**
    * Applies the inverse scale to a matrix in-place. Equivalent to creating a
    * matrix from 1.0 divided by the scale, then pre-applying the scalar
    * matrix to the input.
    *
    * @param sx the scale x
    * @param sy the scale y
    * @param b  the matrix
    *
    * @return the scaled matrix
    */
   public static PMatrix3D invScale ( final float sx, final float sy,
      final PMatrix3D b ) {

      return PMatAux.invScale(sx, sy, 1.0f, b);
   }

   /**
    * Applies the inverse scale to a matrix in-place. Equivalent to creating a
    * matrix from 1.0 divided by the scale, then pre-applying the scalar
    * matrix to the input.
    *
    * @param scalar the scalar
    * @param target the matrix
    *
    * @return the scaled matrix
    */
   public static PMatrix3D invScale ( final float scalar,
      final PMatrix3D target ) {

      return PMatAux.invScale(scalar, scalar, scalar, target);
   }

   /**
    * Applies the inverse translation to a matrix in-place. Equivalent to
    * creating a matrix from the negative translation, then pre-applying the
    * translation matrix to the input.
    *
    * @param tx     the translation x
    * @param ty     the translation y
    * @param tz     the translation z
    * @param target the matrix
    *
    * @return the translated matrix
    */
   public static PMatrix3D invTranslate ( final float tx, final float ty,
      final float tz, final PMatrix3D target ) {

      /* @formatter:off */
      target.set(
         target.m00 - tx * target.m30,
         target.m01 - tx * target.m31,
         target.m02 - tx * target.m32,
         target.m03 - tx * target.m33,
         target.m10 - ty * target.m30,
         target.m11 - ty * target.m31,
         target.m12 - ty * target.m32,
         target.m13 - ty * target.m33,
         target.m20 - tz * target.m30,
         target.m21 - tz * target.m31,
         target.m22 - tz * target.m32,
         target.m23 - tz * target.m33,
         target.m30, target.m31,
         target.m32, target.m33);
      return target;
      /* @formatter:on */
   }

   /**
    * Applies the inverse translation to a matrix in-place. Equivalent to
    * creating a matrix from the negative translation, then pre-applying the
    * translation matrix to the input.
    *
    * @param tx the translation x
    * @param ty the translation y
    * @param b  the matrix
    *
    * @return the translated matrix
    */
   public static PMatrix3D invTranslate ( final float tx, final float ty,
      final PMatrix3D b ) {

      return PMatAux.invTranslate(tx, ty, 0.0f, b);
   }

   /**
    * Multiplies two matrices together. Matrix multiplication is not
    * commutative, so the PMatrix3D class calls in-place multiplication
    * "apply" and "preApply."
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output matrix
    *
    * @return the product
    *
    * @see PMatrix3D#apply(PMatrix3D)
    * @see PMatrix3D#preApply(PMatrix3D)
    */
   public static PMatrix3D mul ( final PMatrix3D a, final PMatrix3D b,
      final PMatrix3D target ) {

      target.set(a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20 + a.m03 * b.m30,
         a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21 + a.m03 * b.m31, a.m00
            * b.m02 + a.m01 * b.m12 + a.m02 * b.m22 + a.m03 * b.m32, a.m00
               * b.m03 + a.m01 * b.m13 + a.m02 * b.m23 + a.m03 * b.m33,

         a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20 + a.m13 * b.m30, a.m10
            * b.m01 + a.m11 * b.m11 + a.m12 * b.m21 + a.m13 * b.m31, a.m10
               * b.m02 + a.m11 * b.m12 + a.m12 * b.m22 + a.m13 * b.m32, a.m10
                  * b.m03 + a.m11 * b.m13 + a.m12 * b.m23 + a.m13 * b.m33,

         a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20 + a.m23 * b.m30, a.m20
            * b.m01 + a.m21 * b.m11 + a.m22 * b.m21 + a.m23 * b.m31, a.m20
               * b.m02 + a.m21 * b.m12 + a.m22 * b.m22 + a.m23 * b.m32, a.m20
                  * b.m03 + a.m21 * b.m13 + a.m22 * b.m23 + a.m23 * b.m33,

         a.m30 * b.m00 + a.m31 * b.m10 + a.m32 * b.m20 + a.m33 * b.m30, a.m30
            * b.m01 + a.m31 * b.m11 + a.m32 * b.m21 + a.m33 * b.m31, a.m30
               * b.m02 + a.m31 * b.m12 + a.m32 * b.m22 + a.m33 * b.m32, a.m30
                  * b.m03 + a.m31 * b.m13 + a.m32 * b.m23 + a.m33 * b.m33);

      return target;
   }

   /**
    * Multiplies a matrix with a three dimensional point, where its implicit
    * fourth coordinate, w, is 1.0 .
    *
    * @param m      the matrix
    * @param px     the point x
    * @param py     the point y
    * @param pz     the point z
    * @param target the output vector
    *
    * @return the product
    */
   public static Vec3 mulPoint ( final PMatrix3D m, final float px,
      final float py, final float pz, final Vec3 target ) {

      final float w = m.m30 * px + m.m31 * py + m.m32 * pz + m.m33;
      if ( w != 0.0f ) {
         final float wInv = 1.0f / w;
         return target.set( ( m.m00 * px + m.m01 * py + m.m02 * pz + m.m03 )
            * wInv, ( m.m10 * px + m.m11 * py + m.m12 * pz + m.m13 ) * wInv,
            ( m.m20 * px + m.m21 * py + m.m22 * pz + m.m23 ) * wInv);

      }
      return target.reset();
   }

   /**
    * Multiplies a matrix with a three dimensional point, where its implicit
    * fourth coordinate, w, is 1.0 .
    *
    * @param m      the matrix
    * @param p      the input vector
    * @param target the output vector
    *
    * @return the product
    */
   public static Vec3 mulPoint ( final PMatrix3D m, final Vec3 p,
      final Vec3 target ) {

      return PMatAux.mulPoint(m, p.x, p.y, p.z, target);
   }

   /**
    * Multiplies a matrix with a three dimensional vector, where its implicit
    * fourth coordinate, w, is 0.0 .
    *
    * @param m      the matrix
    * @param vx     the vector x
    * @param vy     the vector y
    * @param vz     the vector z
    * @param target the output vector
    *
    * @return the product
    */
   public static Vec3 mulVector ( final PMatrix3D m, final float vx,
      final float vy, final float vz, final Vec3 target ) {

      final float w = m.m30 * vx + m.m31 * vy + m.m32 * vz + m.m33;
      if ( w != 0.0f ) {
         final float wInv = 1.0f / w;
         return target.set( ( m.m00 * vx + m.m01 * vy + m.m02 * vz ) * wInv,
            ( m.m10 * vx + m.m11 * vy + m.m12 * vz ) * wInv, ( m.m20 * vx
               + m.m21 * vy + m.m22 * vz ) * wInv);
      }
      return target.reset();
   }

   /**
    * Multiplies a matrix with a three dimensional vector, where its implicit
    * fourth coordinate, w, is 0.0 .
    *
    * @param m      the matrix
    * @param v      the vector
    * @param target the output vector
    *
    * @return the product
    */
   public static Vec3 mulVector ( final PMatrix3D m, final Vec3 v,
      final Vec3 target ) {

      return PMatAux.mulVector(m, v.x, v.y, v.z, target);
   }

   /**
    * Creates an orthographic projection matrix, where objects maintain their
    * size regardless of distance from the camera.
    *
    * @param left   the left edge of the window
    * @param right  the right edge of the window
    * @param bottom the bottom edge of the window
    * @param top    the top edge of the window
    * @param near   the near clip plane
    * @param far    the far clip plane
    * @param target the output matrix
    *
    * @return the orthographic projection
    */
   public static PMatrix3D orthographic ( final float left, final float right,
      final float bottom, final float top, final float near, final float far,
      final PMatrix3D target ) {

      float w = right - left;
      float h = top - bottom;
      float d = far - near;

      w = w != 0.0f ? 1.0f / w : 1.0f;
      h = h != 0.0f ? 1.0f / h : 1.0f;
      d = d != 0.0f ? 1.0f / d : 1.0f;

      /* @formatter:off */
      target.set(
         w + w,  0.0f,        0.0f, w * ( left + right ),
          0.0f, h + h,        0.0f, h * ( top + bottom ),
          0.0f,  0.0f, - ( d + d ),  -d * ( far + near ),
          0.0f,  0.0f,        0.0f,                 1.0f);
      return target;
      /* @formatter:on */
   }

   /**
    * Creates a perspective projection matrix, where objects nearer to the
    * camera appear larger than objects distant from the camera.
    *
    * @param fov    the field of view
    * @param aspect the aspect ratio, width over height
    * @param near   the near clip plane
    * @param far    the far clip plane
    * @param target the output matrix
    *
    * @return the perspective projection
    */
   public static PMatrix3D perspective ( final float fov, final float aspect,
      final float near, final float far, final PMatrix3D target ) {

      final float cotfov = Utils.cot(fov * 0.5f);
      final float d = Utils.div(1.0f, far - near);
      target.set(Utils.div(cotfov, aspect), 0.0f, 0.0f, 0.0f, 0.0f, cotfov,
         0.0f, 0.0f, 0.0f, 0.0f, ( far + near ) * -d, ( near + near ) * far
            * -d, 0.0f, 0.0f, -1.0f, 0.0f);

      return target;
   }

   /**
    * Rotates a matrix by a quaternion in place. Does so by converting the
    * quaternion to a matrix, then multiplying the input matrix and the
    * conversion.
    *
    * @param q      the quaternion
    * @param target the output matrix
    *
    * @return the matrix
    */
   public static PMatrix3D rotate ( final Quaternion q,
      final PMatrix3D target ) {

      final float w = q.real;
      final Vec3 i = q.imag;
      final float x = i.x;
      final float y = i.y;
      final float z = i.z;

      final float x2 = x + x;
      final float y2 = y + y;
      final float z2 = z + z;
      final float xsq2 = x * x2;
      final float ysq2 = y * y2;
      final float zsq2 = z * z2;
      final float xy2 = x * y2;
      final float xz2 = x * z2;
      final float yz2 = y * z2;
      final float wx2 = w * x2;
      final float wy2 = w * y2;
      final float wz2 = w * z2;

      final float bm00 = 1.0f - ysq2 - zsq2;
      final float bm01 = xy2 - wz2;
      final float bm02 = xz2 + wy2;

      final float bm10 = xy2 + wz2;
      final float bm11 = 1.0f - xsq2 - zsq2;
      final float bm12 = yz2 - wx2;

      final float bm20 = xz2 - wy2;
      final float bm21 = yz2 + wx2;
      final float bm22 = 1.0f - xsq2 - ysq2;

      /* @formatter:off */
      target.set(
         target.m00 * bm00 + target.m01 * bm10 + target.m02 * bm20,
         target.m00 * bm01 + target.m01 * bm11 + target.m02 * bm21,
         target.m00 * bm02 + target.m01 * bm12 + target.m02 * bm22,
         target.m03,

         target.m10 * bm00 + target.m11 * bm10 + target.m12 * bm20,
         target.m10 * bm01 + target.m11 * bm11 + target.m12 * bm21,
         target.m10 * bm02 + target.m11 * bm12 + target.m12 * bm22,
         target.m13,

         target.m20 * bm00 + target.m21 * bm10 + target.m22 * bm20,
         target.m20 * bm01 + target.m21 * bm11 + target.m22 * bm21,
         target.m20 * bm02 + target.m21 * bm12 + target.m22 * bm22,
         target.m23,

         target.m30 * bm00 + target.m31 * bm10 + target.m32 * bm20,
         target.m30 * bm01 + target.m31 * bm11 + target.m32 * bm21,
         target.m30 * bm02 + target.m31 * bm12 + target.m32 * bm22,
         target.m33);
      return target;
      /* @formatter:on */
   }

   /**
    * Prints a matrix with a default format.
    *
    * @param m the matrix
    *
    * @return the string
    */
   public static String toString ( final PMatrix2D m ) {

      return PMatAux.toString(m, 4);
   }

   /**
    * Prints a matrix with a default format.
    *
    * @param m      the matrix
    * @param places number of decimal places
    *
    * @return the string
    */
   public static String toString ( final PMatrix2D m, final int places ) {

      final StringBuilder sb = new StringBuilder(320);
      sb.append('\n');
      sb.append('[');
      sb.append(' ');
      sb.append(Utils.toFixed(m.m00, places));
      sb.append(',');
      sb.append(' ');
      sb.append(Utils.toFixed(m.m01, places));
      sb.append(',');
      sb.append(' ');
      sb.append(Utils.toFixed(m.m02, places));
      sb.append(',');
      sb.append(' ');
      sb.append('\n');
      sb.append(Utils.toFixed(m.m10, places));
      sb.append(',');
      sb.append(' ');
      sb.append(Utils.toFixed(m.m11, places));
      sb.append(',');
      sb.append(' ');
      sb.append(Utils.toFixed(m.m12, places));
      sb.append(' ');
      sb.append(']');
      sb.append('\n');
      return sb.toString();

   }

   /**
    * Prints a matrix with a default format.
    *
    * @param m the matrix
    *
    * @return the string
    */
   public static String toString ( final PMatrix3D m ) {

      return PMatAux.toString(m, 4);
   }

   /**
    * Prints a matrix with a default format.
    *
    * @param m      the matrix
    * @param places number of decimal places
    *
    * @return the string
    */
   public static String toString ( final PMatrix3D m, final int places ) {

      final StringBuilder sb = new StringBuilder(320);
      sb.append('\n');
      sb.append('[');
      sb.append(' ');
      sb.append(Utils.toFixed(m.m00, places)).append(',').append(' ');
      sb.append(Utils.toFixed(m.m01, places)).append(',').append(' ');
      sb.append(Utils.toFixed(m.m02, places)).append(',').append(' ');
      sb.append(Utils.toFixed(m.m03, places)).append(',').append(' ');
      sb.append('\n');
      sb.append(Utils.toFixed(m.m10, places)).append(',').append(' ');
      sb.append(Utils.toFixed(m.m11, places)).append(',').append(' ');
      sb.append(Utils.toFixed(m.m12, places)).append(',').append(' ');
      sb.append(Utils.toFixed(m.m13, places)).append(',').append(' ');
      sb.append('\n');
      sb.append(Utils.toFixed(m.m20, places)).append(',').append(' ');
      sb.append(Utils.toFixed(m.m21, places)).append(',').append(' ');
      sb.append(Utils.toFixed(m.m22, places)).append(',').append(' ');
      sb.append(Utils.toFixed(m.m23, places)).append(',').append(' ');
      sb.append('\n');
      sb.append(Utils.toFixed(m.m30, places)).append(',').append(' ');
      sb.append(Utils.toFixed(m.m31, places)).append(',').append(' ');
      sb.append(Utils.toFixed(m.m32, places)).append(',').append(' ');
      sb.append(Utils.toFixed(m.m33, places));
      sb.append(' ');
      sb.append(']');
      sb.append('\n');
      return sb.toString();
   }

}
