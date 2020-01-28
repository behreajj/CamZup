package camzup.pfriendly;

import java.awt.geom.AffineTransform;

import camzup.core.Utils;
import camzup.core.Vec3;
import camzup.core.Vec4;
import processing.core.PApplet;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;
import processing.core.PVector;

/**
 * The PMatrix class is marked final and hence cannot be
 * extended and augmented. Due to its use in PGraphicsOpenGL
 * it cannot be ignored. This class stores static functions
 * which alter and/or supplement PMatrix functions.
 */
public abstract class PMatAux {

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

      return PMatAux.frustum(
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

      target.m00 = detInv * PMatAux.det3x3(
            m.m11, m.m12, m.m13,
            m.m21, m.m22, m.m23,
            m.m31, m.m32, m.m33);
      target.m01 = detInv * -PMatAux.det3x3(
            m.m01, m.m02, m.m03,
            m.m21, m.m22, m.m23,
            m.m31, m.m32, m.m33);
      target.m02 = detInv * PMatAux.det3x3(
            m.m01, m.m02, m.m03,
            m.m11, m.m12, m.m13,
            m.m31, m.m32, m.m33);
      target.m03 = detInv * -PMatAux.det3x3(
            m.m01, m.m02, m.m03,
            m.m11, m.m12, m.m13,
            m.m21, m.m22, m.m23);

      target.m10 = detInv * -PMatAux.det3x3(
            m.m10, m.m12, m.m13,
            m.m20, m.m22, m.m23,
            m.m30, m.m32, m.m33);
      target.m11 = detInv * PMatAux.det3x3(
            m.m00, m.m02, m.m03,
            m.m20, m.m22, m.m23,
            m.m30, m.m32, m.m33);
      target.m12 = detInv * -PMatAux.det3x3(
            m.m00, m.m02, m.m03,
            m.m10, m.m12, m.m13,
            m.m30, m.m32, m.m33);
      target.m13 = detInv * PMatAux.det3x3(
            m.m00, m.m02, m.m03,
            m.m10, m.m12, m.m13,
            m.m20, m.m22, m.m23);

      target.m20 = detInv * PMatAux.det3x3(
            m.m10, m.m11, m.m13,
            m.m20, m.m21, m.m23,
            m.m30, m.m31, m.m33);
      target.m21 = detInv * -PMatAux.det3x3(
            m.m00, m.m01, m.m03,
            m.m20, m.m21, m.m23,
            m.m30, m.m31, m.m33);
      target.m22 = detInv * PMatAux.det3x3(
            m.m00, m.m01, m.m03,
            m.m10, m.m11, m.m13,
            m.m30, m.m31, m.m33);
      target.m23 = detInv * -PMatAux.det3x3(
            m.m00, m.m01, m.m03,
            m.m10, m.m11, m.m13,
            m.m20, m.m21, m.m23);

      target.m30 = detInv * -PMatAux.det3x3(
            m.m10, m.m11, m.m12,
            m.m20, m.m21, m.m22,
            m.m30, m.m31, m.m32);
      target.m31 = detInv * PMatAux.det3x3(
            m.m00, m.m01, m.m02,
            m.m20, m.m21, m.m22,
            m.m30, m.m31, m.m32);
      target.m32 = detInv * -PMatAux.det3x3(
            m.m00, m.m01, m.m02,
            m.m10, m.m11, m.m12,
            m.m30, m.m31, m.m32);
      target.m33 = detInv * PMatAux.det3x3(
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

      // TODO: Finish comments.
      return PMatAux.invRotate(
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

      return PMatAux.invRotateX(radians, (PMatrix3D) null);
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

      return PMatAux.invRotateY(radians, (PMatrix3D) null);
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

      return PMatAux.invRotateZ(radians, (PMatrix3D) null);
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

      return PMatAux.mul(a, b, (PMatrix3D) null);
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
    * Multiplies a matrix with a four dimensional vector. This
    * is useful when the w component needs to be stored for
    * diagnostic purposes (rather than demoting the vector to a
    * 3D point).
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

      return PMatAux.mulPoint(m, v, (PVector) null);
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

      return PMatAux.orthographic(
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

      return PMatAux.perspective(
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

      final float cotfov = Utils.cot(fov * 0.5f);
      final float d = Utils.div(1.0f, far - near);
      target.set(
            Utils.div(cotfov, aspect), 0.0f, 0.0f, 0.0f,
            0.0f, cotfov, 0.0f, 0.0f,
            0.0f, 0.0f, (far + near) * -d, (near + near) * far * -d,
            0.0f, 0.0f, -1.0f, 0.0f);

      return target;
   }

   static PMatrix3D rotate (
         final float radians,
         final float xAxis,
         final float yAxis,
         final float zAxis ) {

      return PMatAux.rotate(radians,
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
         return target;
      }

      if (mSq != 1.0f) {
         final float mInv = Utils.invSqrtUnchecked(mSq);
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

      return PMatAux.rotateX(radians, (PMatrix3D) null);
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

      // TODO: Replace all PApplet.cos and sin.
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

      return PMatAux.rotateY(radians, (PMatrix3D) null);
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

      return PMatAux.rotateZ(radians, (PMatrix3D) null);
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

   public static PMatrix2D fromAwt (
         final AffineTransform tr,
         PMatrix2D target ) {

      if (target == null) {
         target = new PMatrix2D();
      }

      target.set(
            (float) tr.getScaleX(),
            (float) tr.getShearX(),
            (float) tr.getTranslateX(),
            (float) tr.getShearY(),
            (float) tr.getScaleY(),
            (float) tr.getTranslateY());
      return target;
   }

   public static PMatrix3D fromAwt ( final AffineTransform tr,
         PMatrix3D target ) {

      if (target == null) {
         target = new PMatrix3D();
      }

      target.set(
            (float) tr.getScaleX(),
            (float) tr.getShearX(),
            0.0f,
            (float) tr.getTranslateX(),

            (float) tr.getShearY(),
            (float) tr.getScaleY(),
            0.0f,
            (float) tr.getTranslateY(),

            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
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
   public static String toString (
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
}
