package camzup.core;

import java.util.Iterator;

public class Mat4 extends Matrix {

   public static final class M4Iterator implements Iterator < Float > {

      private int index = 0;

      private final Mat4 mtx;

      public M4Iterator ( final Mat4 mtx ) {

         this.mtx = mtx;
      }

      @Override
      public boolean hasNext () {

         return this.index < this.mtx.size();
      }

      @Override
      public Float next () {

         return this.mtx.get(this.index++);
      }

      @Override
      public String toString () {

         return this.getClass().getSimpleName();
      }

   }

   private static final long serialVersionUID = 4394235117465746059L;

   public static Mat4 add (
         final Mat4 a,
         final Mat4 b,
         final Mat4 target ) {

      return target.set(
            a.m00 + b.m00, a.m01 + b.m01, a.m02 + b.m02, a.m03 + b.m03,
            a.m10 + b.m10, a.m11 + b.m11, a.m12 + b.m12, a.m13 + b.m13,
            a.m20 + b.m20, a.m21 + b.m21, a.m22 + b.m22, a.m23 + b.m23,
            a.m30 + b.m30, a.m31 + b.m31, a.m32 + b.m32, a.m33 + b.m33);
   }

   public static Mat4 div (
         final Mat4 a,
         final float b,
         final Mat4 target ) {

      if (b == 0.0f) {
         return Mat4.identity(target);
      }

      final float bInv = 1.0f / b;
      return target.set(
            a.m00 * bInv, a.m01 * bInv, a.m02 * bInv, a.m03 * bInv,
            a.m10 * bInv, a.m11 * bInv, a.m12 * bInv, a.m13 * bInv,
            a.m20 * bInv, a.m21 * bInv, a.m22 * bInv, a.m23 * bInv,
            a.m30 * bInv, a.m31 * bInv, a.m32 * bInv, a.m33 * bInv);
   }

   public static Mat4 fromAxes (
         final Vec2 right,
         final Vec2 forward,
         final Mat4 target ) {

      return target.set(
            right.x, forward.x, 0.0f, 0.0f,
            right.y, forward.y, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
   }

   public static Mat4 fromAxes (
         final Vec2 right,
         final Vec2 forward,
         final Vec2 translation,
         final Mat4 target ) {

      return target.set(
            right.x, forward.x, 0.0f, translation.x,
            right.y, forward.y, 0.0f, translation.y,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
   }

   public static Mat4 fromAxes (
         final Vec3 right,
         final Vec3 forward,
         final Vec3 up,
         final Mat4 target ) {

      return target.set(
            right.x, forward.x, up.x, 0.0f,
            right.y, forward.y, up.y, 0.0f,
            right.z, forward.z, up.z, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
   }

   public static Mat4 fromAxes (
         final Vec3 right,
         final Vec3 forward,
         final Vec3 up,
         final Vec3 translation,
         final Mat4 target ) {

      return target.set(
            right.x, forward.x, up.x, translation.x,
            right.y, forward.y, up.y, translation.y,
            right.z, forward.z, up.z, translation.z,
            0.0f, 0.0f, 0.0f, 1.0f);
   }

   public static Mat4 fromAxes (
         final Vec4 right,
         final Vec4 forward,
         final Vec4 up,
         final Mat4 target ) {

      return target.set(
            right.x, forward.x, up.x, 0.0f,
            right.y, forward.y, up.y, 0.0f,
            right.z, forward.z, up.z, 0.0f,
            right.w, forward.w, up.w, 1.0f);
   }

   public static Mat4 fromAxes (
         final Vec4 right,
         final Vec4 forward,
         final Vec4 up,
         final Vec4 translation,
         final Mat4 target ) {

      return target.set(
            right.x, forward.x, up.x, translation.x,
            right.y, forward.y, up.y, translation.y,
            right.z, forward.z, up.z, translation.z,
            right.w, forward.w, up.w, translation.w);
   }

   public static Mat4 fromRotX (
         final float cosa,
         final float sina,
         final Mat4 target ) {

      return target.set(
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, cosa, -sina, 0.0f,
            0.0f, sina, cosa, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
   }

   public static Mat4 fromRotX (
         final float radians,
         final Mat4 target ) {

      return Mat4.fromRotX(
            (float) Math.cos(radians),
            (float) Math.sin(radians),
            target);
   }

   public static Mat4 fromRotY (
         final float cosa,
         final float sina,
         final Mat4 target ) {

      return target.set(
            cosa, 0.0f, sina, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            -sina, 0.0f, cosa, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
   }

   public static Mat4 fromRotY (
         final float radians,
         final Mat4 target ) {

      return Mat4.fromRotY(
            (float) Math.cos(radians),
            (float) Math.sin(radians),
            target);
   }

   public static Mat4 fromRotZ (
         final float cosa,
         final float sina,
         final Mat4 target ) {

      return target.set(
            cosa, -sina, 0.0f, 0.0f,
            sina, cosa, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
   }

   public static Mat4 fromRotZ (
         final float radians,
         final Mat4 target ) {

      return Mat4.fromRotZ(
            (float) Math.cos(radians),
            (float) Math.sin(radians),
            target);
   }

   public static Mat4 fromScale (
         final float scalar,
         final Mat4 target ) {

      return target.set(
            scalar, 0.0f, 0.0f, 0.0f,
            0.0f, scalar, 0.0f, 0.0f,
            0.0f, 0.0f, scalar, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
   }

   public static Mat4 fromScale (
         final Vec2 scalar,
         final Mat4 target ) {

      return target.set(
            scalar.x, 0.0f, 0.0f, 0.0f,
            0.0f, scalar.y, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
   }

   public static Mat4 fromScale (
         final Vec3 scalar,
         final Mat4 target ) {

      return target.set(
            scalar.x, 0.0f, 0.0f, 0.0f,
            0.0f, scalar.y, 0.0f, 0.0f,
            0.0f, 0.0f, scalar.z, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
   }

   public static Mat4 fromTranslation (
         final Vec2 translation,
         final Mat4 target ) {

      return target.set(
            1.0f, 0.0f, 0.0f, translation.x,
            0.0f, 1.0f, 0.0f, translation.y,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
   }

   public static Mat4 fromTranslation (
         final Vec3 translation,
         final Mat4 target ) {

      return target.set(
            1.0f, 0.0f, 0.0f, translation.x,
            0.0f, 1.0f, 0.0f, translation.y,
            0.0f, 0.0f, 1.0f, translation.z,
            0.0f, 0.0f, 0.0f, 1.0f);
   }

   public static Mat4 frustum (
         final float left, final float right,
         final float bottom, final float top,
         final float near, final float far,
         final Mat4 target ) {

      // TODO: Needs testing...
      final float n2 = near + near;
      final float w = Utils.div(1.0f, right - left);
      final float h = Utils.div(1.0f, top - bottom);
      final float d = Utils.div(1.0f, far - near);

      return target.set(
            n2 * w, 0.0f, (right + left) * w, 0.0f,
            0.0f, n2 * h, (top + bottom) * h, 0.0f,
            0.0f, 0.0f, (far + near) * -d, n2 * far * -d,
            0.0f, 0.0f, -1.0f, 0.0f);
   }

   public static Mat4 identity ( final Mat4 target ) {

      return target.set(
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
   }

   public static boolean isIdentity ( final Mat4 m ) {

      return m.m33 == 1.0f && m.m22 == 1.0f && m.m11 == 1.0f && m.m00 == 1.0f &&
            m.m01 == 0.0f && m.m02 == 0.0f && m.m03 == 0.0f && m.m10 == 0.0f &&
            m.m12 == 0.0f && m.m13 == 0.0f && m.m20 == 0.0f && m.m21 == 0.0f &&
            m.m23 == 0.0f && m.m30 == 0.0f && m.m31 == 0.0f && m.m32 == 0.0f;
   }

   public static Mat4 mult (
         final float a,
         final Mat4 b,
         final Mat4 target ) {

      return target.set(
            a * b.m00, a * b.m01, a * b.m02, a * b.m03,
            a * b.m10, a * b.m11, a * b.m12, a * b.m13,
            a * b.m20, a * b.m21, a * b.m22, a * b.m23,
            a * b.m30, a * b.m31, a * b.m32, a * b.m33);
   }

   public static Mat4 mult (
         final Mat4 a,
         final float b,
         final Mat4 target ) {

      return target.set(
            a.m00 * b, a.m01 * b, a.m02 * b, a.m03 * b,
            a.m10 * b, a.m11 * b, a.m12 * b, a.m13 * b,
            a.m20 * b, a.m21 * b, a.m22 * b, a.m23 * b,
            a.m30 * b, a.m31 * b, a.m32 * b, a.m33 * b);
   }

   public static Mat4 mult (
         final Mat4 a,
         final Mat4 b,
         final Mat4 target ) {

      return target.set(
            a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20 + a.m03 * b.m30,
            a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21 + a.m03 * b.m31,
            a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22 + a.m03 * b.m32,
            a.m00 * b.m03 + a.m01 * b.m13 + a.m02 * b.m23 + a.m03 * b.m33,

            a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20 + a.m13 * b.m30,
            a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21 + a.m13 * b.m31,
            a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22 + a.m13 * b.m32,
            a.m10 * b.m03 + a.m11 * b.m13 + a.m12 * b.m23 + a.m13 * b.m33,

            a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20 + a.m23 * b.m30,
            a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21 + a.m23 * b.m31,
            a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22 + a.m23 * b.m32,
            a.m20 * b.m03 + a.m21 * b.m13 + a.m22 * b.m23 + a.m23 * b.m33,

            a.m30 * b.m00 + a.m31 * b.m10 + a.m32 * b.m20 + a.m33 * b.m30,
            a.m30 * b.m01 + a.m31 * b.m11 + a.m32 * b.m21 + a.m33 * b.m31,
            a.m30 * b.m02 + a.m31 * b.m12 + a.m32 * b.m22 + a.m33 * b.m32,
            a.m30 * b.m03 + a.m31 * b.m13 + a.m32 * b.m23 + a.m33 * b.m33);
   }

   public static Mat4 mult (
         final Mat4 a,
         final Mat4 b,
         final Mat4 c,
         final Mat4 target ) {

      final float n00 = a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20
            + a.m03 * b.m30;
      final float n01 = a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21
            + a.m03 * b.m31;
      final float n02 = a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22
            + a.m03 * b.m32;
      final float n03 = a.m00 * b.m03 + a.m01 * b.m13 + a.m02 * b.m23
            + a.m03 * b.m33;

      final float n10 = a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20
            + a.m13 * b.m30;
      final float n11 = a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21
            + a.m13 * b.m31;
      final float n12 = a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22
            + a.m13 * b.m32;
      final float n13 = a.m10 * b.m03 + a.m11 * b.m13 + a.m12 * b.m23
            + a.m13 * b.m33;

      final float n20 = a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20
            + a.m23 * b.m30;
      final float n21 = a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21
            + a.m23 * b.m31;
      final float n22 = a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22
            + a.m23 * b.m32;
      final float n23 = a.m20 * b.m03 + a.m21 * b.m13 + a.m22 * b.m23
            + a.m23 * b.m33;

      final float n30 = a.m30 * b.m00 + a.m31 * b.m10 + a.m32 * b.m20
            + a.m33 * b.m30;
      final float n31 = a.m30 * b.m01 + a.m31 * b.m11 + a.m32 * b.m21
            + a.m33 * b.m31;
      final float n32 = a.m30 * b.m02 + a.m31 * b.m12 + a.m32 * b.m22
            + a.m33 * b.m32;
      final float n33 = a.m30 * b.m03 + a.m31 * b.m13 + a.m32 * b.m23
            + a.m33 * b.m33;

      return target.set(
            n00 * c.m00 + n01 * c.m10 + n02 * c.m20 + n03 * c.m30,
            n00 * c.m01 + n01 * c.m11 + n02 * c.m21 + n03 * c.m31,
            n00 * c.m02 + n01 * c.m12 + n02 * c.m22 + n03 * c.m32,
            n00 * c.m03 + n01 * c.m13 + n02 * c.m23 + n03 * c.m33,

            n10 * c.m00 + n11 * c.m10 + n12 * c.m20 + n13 * c.m30,
            n10 * c.m01 + n11 * c.m11 + n12 * c.m21 + n13 * c.m31,
            n10 * c.m02 + n11 * c.m12 + n12 * c.m22 + n13 * c.m32,
            n10 * c.m03 + n11 * c.m13 + n12 * c.m23 + n13 * c.m33,

            n20 * c.m00 + n21 * c.m10 + n22 * c.m20 + n23 * c.m30,
            n20 * c.m01 + n21 * c.m11 + n22 * c.m21 + n23 * c.m31,
            n20 * c.m02 + n21 * c.m12 + n22 * c.m22 + n23 * c.m32,
            n20 * c.m03 + n21 * c.m13 + n22 * c.m23 + n23 * c.m33,

            n30 * c.m00 + n31 * c.m10 + n32 * c.m20 + n33 * c.m30,
            n30 * c.m01 + n31 * c.m11 + n32 * c.m21 + n33 * c.m31,
            n30 * c.m02 + n31 * c.m12 + n32 * c.m22 + n33 * c.m32,
            n30 * c.m03 + n31 * c.m13 + n32 * c.m23 + n33 * c.m33);
   }

   public static Vec4 mult (
         final Mat4 m,
         final Vec4 source,
         final Vec4 target ) {

      return target.set(
            m.m00 * source.x + m.m01 * source.y + m.m02 * source.z
                  + m.m03 * source.w,
            m.m10 * source.x + m.m11 * source.y + m.m12 * source.z
                  + m.m13 * source.w,
            m.m20 * source.x + m.m21 * source.y + m.m22 * source.z
                  + m.m23 * source.w,
            m.m30 * source.x + m.m31 * source.y + m.m32 * source.z
                  + m.m33 * source.w);
   }

   public static Mat4 orthographic (
         final float left, final float right,
         final float bottom, final float top,
         final float near, final float far,
         final Mat4 target ) {

      final float w = Utils.div(1.0f, right - left);
      final float h = Utils.div(1.0f, top - bottom);
      final float d = Utils.div(1.0f, far - near);

      return target.set(
            w + w, 0.0f, 0.0f, w * (left + right),
            0.0f, h + h, 0.0f, h * (top + bottom),
            0.0f, 0.0f, -(d + d), -d * (far + near),
            0.0f, 0.0f, 0.0f, 1.0f);
   }

   public static Mat4 perspective (
         final float fov,
         final float aspect,
         final float near,
         final float far,
         final Mat4 target ) {

      final float tanfov = (float) Math.tan(fov * 0.5d);
      final float d = Utils.div(1.0f, far - near);
      return target.set(
            Utils.div(1.0f, tanfov * aspect), 0.0f, 0.0f, 0.0f,
            0.0f, Utils.div(1.0f, tanfov), 0.0f, 0.0f,
            0.0f, 0.0f, (far + near) * -d, (near + near) * far * -d,
            0.0f, 0.0f, -1.0f, 0.0f);
   }

   public static Mat4 sub (
         final Mat4 a,
         final Mat4 b,
         final Mat4 target ) {

      return target.set(
            a.m00 - b.m00, a.m01 - b.m01, a.m02 - b.m02, a.m03 - b.m03,
            a.m10 - b.m10, a.m11 - b.m11, a.m12 - b.m12, a.m13 - b.m13,
            a.m20 - b.m20, a.m21 - b.m21, a.m22 - b.m22, a.m23 - b.m23,
            a.m30 - b.m30, a.m31 - b.m31, a.m32 - b.m32, a.m33 - b.m33);
   }

   public static Mat4 transpose (
         final Mat4 m,
         final Mat4 target ) {

      return target.set(
            m.m00, m.m10, m.m20, m.m30,
            m.m01, m.m11, m.m21, m.m31,
            m.m02, m.m12, m.m22, m.m32,
            m.m03, m.m13, m.m23, m.m33);
   }

   public float m00 = 1.0f;
   public float m01 = 0.0f;
   public float m02 = 0.0f;
   public float m03 = 0.0f;

   public float m10 = 0.0f;
   public float m11 = 1.0f;
   public float m12 = 0.0f;
   public float m13 = 0.0f;

   public float m20 = 0.0f;
   public float m21 = 0.0f;
   public float m22 = 1.0f;
   public float m23 = 0.0f;

   public float m30 = 0.0f;
   public float m31 = 0.0f;
   public float m32 = 0.0f;
   public float m33 = 1.0f;

   public Mat4 () {

      super(16);
   }

   public Mat4 (
         final float m00, final float m01, final float m02,
         final float m10, final float m11, final float m12,
         final float m20, final float m21, final float m22 ) {

      super(16);
      this.set(
            m00, m01, m02,
            m10, m11, m12,
            m20, m21, m22);
   }

   public Mat4 (
         final float m00, final float m01, final float m02, final float m03,
         final float m10, final float m11, final float m12, final float m13,
         final float m20, final float m21, final float m22, final float m23 ) {

      super(16);
      this.set(
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23);
   }

   public Mat4 (
         final float m00, final float m01, final float m02, final float m03,
         final float m10, final float m11, final float m12, final float m13,
         final float m20, final float m21, final float m22, final float m23,
         final float m30, final float m31, final float m32, final float m33 ) {

      super(16);
      this.set(
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            m30, m31, m32, m33);
   }

   public Mat4 ( final Mat4 source ) {

      super(16);
      this.set(source);
   }

   protected boolean equals ( final Mat4 other ) {

      if (Float.floatToIntBits(this.m00) != Float.floatToIntBits(other.m00)) {
         return false;
      }
      if (Float.floatToIntBits(this.m01) != Float.floatToIntBits(other.m01)) {
         return false;
      }
      if (Float.floatToIntBits(this.m02) != Float.floatToIntBits(other.m02)) {
         return false;
      }
      if (Float.floatToIntBits(this.m03) != Float.floatToIntBits(other.m03)) {
         return false;
      }

      if (Float.floatToIntBits(this.m10) != Float.floatToIntBits(other.m10)) {
         return false;
      }
      if (Float.floatToIntBits(this.m11) != Float.floatToIntBits(other.m11)) {
         return false;
      }
      if (Float.floatToIntBits(this.m12) != Float.floatToIntBits(other.m12)) {
         return false;
      }
      if (Float.floatToIntBits(this.m13) != Float.floatToIntBits(other.m13)) {
         return false;
      }

      if (Float.floatToIntBits(this.m20) != Float.floatToIntBits(other.m20)) {
         return false;
      }
      if (Float.floatToIntBits(this.m21) != Float.floatToIntBits(other.m21)) {
         return false;
      }
      if (Float.floatToIntBits(this.m22) != Float.floatToIntBits(other.m22)) {
         return false;
      }
      if (Float.floatToIntBits(this.m23) != Float.floatToIntBits(other.m23)) {
         return false;
      }

      if (Float.floatToIntBits(this.m30) != Float.floatToIntBits(other.m30)) {
         return false;
      }
      if (Float.floatToIntBits(this.m31) != Float.floatToIntBits(other.m31)) {
         return false;
      }
      if (Float.floatToIntBits(this.m32) != Float.floatToIntBits(other.m32)) {
         return false;
      }
      if (Float.floatToIntBits(this.m33) != Float.floatToIntBits(other.m33)) {
         return false;
      }
      return true;
   }

   @Override
   public Mat4 clone () {

      return new Mat4(
            this.m00, this.m01, this.m02, this.m03,
            this.m10, this.m11, this.m12, this.m13,
            this.m20, this.m21, this.m22, this.m23,
            this.m30, this.m31, this.m32, this.m33);
   }

   @Override
   public boolean equals ( final Object obj ) {

      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (this.getClass() != obj.getClass()) {
         return false;
      }

      return this.equals((Mat4) obj);
   }

   @Override
   public float get ( final int index ) {

      switch (index) {

         /* Row 0 */

         case 0:
         case -16:
            return this.m00;
         case 1:
         case -15:
            return this.m01;
         case 2:
         case -14:
            return this.m02;
         case 3:
         case -13:
            return this.m03;

         /* Row 1 */

         case 4:
         case -12:
            return this.m10;
         case 5:
         case -11:
            return this.m11;
         case 6:
         case -10:
            return this.m12;
         case 7:
         case -9:
            return this.m13;

         /* Row 2 */

         case 8:
         case -8:
            return this.m20;
         case 9:
         case -7:
            return this.m21;
         case 10:
         case -6:
            return this.m22;
         case 11:
         case -5:
            return this.m23;

         /* Row 3 */

         case 12:
         case -4:
            return this.m30;
         case 13:
         case -3:
            return this.m31;
         case 14:
         case -2:
            return this.m32;
         case 15:
         case -1:
            return this.m33;

         default:
            return 0.0f;
      }
   }

   @Override
   public float get ( final int i, final int j ) {

      switch (i) {
         case 0:
         case -4:

            switch (j) {
               case 0:
               case -4:
                  return this.m00;
               case 1:
               case -3:
                  return this.m01;
               case 2:
               case -2:
                  return this.m02;
               case 3:
               case -1:
                  return this.m03;
               default:
                  return 0.0f;
            }

         case 1:
         case -3:

            switch (j) {
               case 0:
               case -4:
                  return this.m10;
               case 1:
               case -3:
                  return this.m11;
               case 2:
               case -2:
                  return this.m12;
               case 3:
               case -1:
                  return this.m13;
               default:
                  return 0.0f;
            }

         case 2:
         case -2:

            switch (j) {
               case 0:
               case -4:
                  return this.m20;
               case 1:
               case -3:
                  return this.m21;
               case 2:
               case -2:
                  return this.m22;
               case 3:
               case -1:
                  return this.m23;
               default:
                  return 0.0f;
            }

         case 3:
         case -1:

            switch (j) {
               case 0:
               case -4:
                  return this.m30;
               case 1:
               case -3:
                  return this.m31;
               case 2:
               case -2:
                  return this.m32;
               case 3:
               case -1:
                  return this.m33;
               default:
                  return 0.0f;
            }

         default:
            return 0.0f;
      }
   }

   @Override
   public int hashCode () {

      final int prime = 31;
      int result = 1;

      result = prime * result + Float.floatToIntBits(this.m00);
      result = prime * result + Float.floatToIntBits(this.m01);
      result = prime * result + Float.floatToIntBits(this.m02);
      result = prime * result + Float.floatToIntBits(this.m03);

      result = prime * result + Float.floatToIntBits(this.m10);
      result = prime * result + Float.floatToIntBits(this.m11);
      result = prime * result + Float.floatToIntBits(this.m12);
      result = prime * result + Float.floatToIntBits(this.m13);

      result = prime * result + Float.floatToIntBits(this.m20);
      result = prime * result + Float.floatToIntBits(this.m21);
      result = prime * result + Float.floatToIntBits(this.m22);
      result = prime * result + Float.floatToIntBits(this.m23);

      result = prime * result + Float.floatToIntBits(this.m30);
      result = prime * result + Float.floatToIntBits(this.m31);
      result = prime * result + Float.floatToIntBits(this.m32);
      result = prime * result + Float.floatToIntBits(this.m33);

      return result;
   }

   @Override
   public Iterator < Float > iterator () {

      return new M4Iterator(this);
   }

   public Mat4 reset () {

      return this.set(
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
   }

   public Mat4 set (
         final float m00, final float m01, final float m02,
         final float m10, final float m11, final float m12,
         final float m20, final float m21, final float m22 ) {

      return this.set(
            m00, m01, m02, 0.0f,
            m10, m11, m12, 0.0f,
            m20, m21, m22, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
   }

   public Mat4 set (
         final float m00, final float m01, final float m02, final float m03,
         final float m10, final float m11, final float m12, final float m13,
         final float m20, final float m21, final float m22, final float m23 ) {

      return this.set(
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            0.0f, 0.0f, 0.0f, 1.0f);
   }

   public Mat4 set (
         final float m00, final float m01, final float m02, final float m03,
         final float m10, final float m11, final float m12, final float m13,
         final float m20, final float m21, final float m22, final float m23,
         final float m30, final float m31, final float m32, final float m33 ) {

      this.m00 = m00;
      this.m01 = m01;
      this.m02 = m02;
      this.m03 = m03;

      this.m10 = m10;
      this.m11 = m11;
      this.m12 = m12;
      this.m13 = m13;

      this.m20 = m20;
      this.m21 = m21;
      this.m22 = m22;
      this.m23 = m23;

      this.m30 = m30;
      this.m31 = m31;
      this.m32 = m32;
      this.m33 = m33;

      return this;
   }

   public Mat4 set ( final Mat4 source ) {

      return this.set(
            source.m00, source.m01, source.m02, source.m03,
            source.m10, source.m11, source.m12, source.m13,
            source.m20, source.m21, source.m22, source.m23,
            source.m30, source.m31, source.m32, source.m33);
   }

   /**
    * Returns a float array of length 16 containing this
    * matrix's components.
    *
    * @return the array
    */
   @Override
   public float[] toArray () {

      return new float[] {
            this.m00, this.m01, this.m02, this.m03,
            this.m10, this.m11, this.m12, this.m13,
            this.m20, this.m21, this.m22, this.m23,
            this.m30, this.m31, this.m32, this.m33 };
   }

   /**
    * Returns a string representation of this matrix.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   /**
    * Returns a string representation of this matrix.
    *
    * @param places
    *           number of decimal places
    * @return the string
    */
   public String toString ( final int places ) {

      return new StringBuilder()
            .append("{ m00: ")
            .append(Utils.toFixed(this.m00, places))
            .append(", m01: ")
            .append(Utils.toFixed(this.m01, places))
            .append(", m02: ")
            .append(Utils.toFixed(this.m02, places))
            .append(", m03: ")
            .append(Utils.toFixed(this.m03, places))

            .append(", m10: ")
            .append(Utils.toFixed(this.m10, places))
            .append(", m11: ")
            .append(Utils.toFixed(this.m11, places))
            .append(", m12: ")
            .append(Utils.toFixed(this.m12, places))
            .append(", m13: ")
            .append(Utils.toFixed(this.m13, places))

            .append(", m20: ")
            .append(Utils.toFixed(this.m20, places))
            .append(", m21: ")
            .append(Utils.toFixed(this.m21, places))
            .append(", m22: ")
            .append(Utils.toFixed(this.m22, places))
            .append(", m23: ")
            .append(Utils.toFixed(this.m23, places))

            .append(", m30: ")
            .append(Utils.toFixed(this.m30, places))
            .append(", m31: ")
            .append(Utils.toFixed(this.m31, places))
            .append(", m32: ")
            .append(Utils.toFixed(this.m32, places))
            .append(", m33: ")
            .append(Utils.toFixed(this.m33, places))

            .append(' ')
            .append('}')
            .toString();
   }

   public String toStringTab () {

      return this.toStringTab(4);
   }

   public String toStringTab ( final int places ) {

      return new StringBuilder()
            // .append(this.hashIdentityString())
            .append('\n')
            .append(Utils.toFixed(this.m00, places))
            .append('\t')
            .append(Utils.toFixed(this.m01, places))
            .append('\t')
            .append(Utils.toFixed(this.m02, places))
            .append('\t')
            .append(Utils.toFixed(this.m03, places))

            .append('\n')
            .append(Utils.toFixed(this.m10, places))
            .append('\t')
            .append(Utils.toFixed(this.m11, places))
            .append('\t')
            .append(Utils.toFixed(this.m12, places))
            .append('\t')
            .append(Utils.toFixed(this.m13, places))

            .append('\n')
            .append(Utils.toFixed(this.m20, places))
            .append('\t')
            .append(Utils.toFixed(this.m21, places))
            .append('\t')
            .append(Utils.toFixed(this.m22, places))
            .append('\t')
            .append(Utils.toFixed(this.m23, places))

            .append('\n')
            .append(Utils.toFixed(this.m30, places))
            .append('\t')
            .append(Utils.toFixed(this.m31, places))
            .append('\t')
            .append(Utils.toFixed(this.m32, places))
            .append('\t')
            .append(Utils.toFixed(this.m33, places))

            .append('\n')
            .toString();
   }
}
