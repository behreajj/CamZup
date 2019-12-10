package camzup.core;

import java.util.Iterator;

public class Mat3 extends Matrix {

   public static final class M3Iterator implements Iterator < Float > {

      private int index = 0;

      private final Mat3 mtx;

      public M3Iterator ( final Mat3 mtx ) {

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

   private static final long serialVersionUID = -1737245169747444488L;

   public static Mat3 add (
         final Mat3 a,
         final Mat3 b,
         final Mat3 target ) {

      return target.set(
            a.m00 + b.m00, a.m01 + b.m01, a.m02 + b.m02,
            a.m10 + b.m10, a.m11 + b.m11, a.m12 + b.m12,
            a.m20 + b.m20, a.m21 + b.m21, a.m22 + b.m22);
   }

   public static float determinant ( final Mat3 m ) {

      // TODO: Double check that this is not transposed...
      return m.m00 * (m.m22 * m.m11 - m.m12 * m.m21) +
            m.m01 * (-m.m22 * m.m10 + m.m12 * m.m20) +
            m.m02 * (m.m21 * m.m10 - m.m11 * m.m20);
   }

   public static Mat3 div (
         final Mat3 a,
         final float b,
         final Mat3 target ) {

      if (b == 0.0f) {
         return Mat3.identity(target);
      }

      final float bInv = 1.0f / b;
      return target.set(
            a.m00 * bInv, a.m01 * bInv, a.m02 * bInv,
            a.m10 * bInv, a.m11 * bInv, a.m12 * bInv,
            a.m20 * bInv, a.m21 * bInv, a.m22 * bInv);
   }

   public static float frobenius ( final Mat3 m ) {

      return (float) Math.sqrt(m.m00 * m.m00 +
            m.m01 * m.m01 +
            m.m02 * m.m02 +

            m.m10 * m.m10 +
            m.m11 * m.m11 +
            m.m12 * m.m12 +

            m.m20 * m.m20 +
            m.m21 * m.m21 +
            m.m22 * m.m22);
   }

   public static Mat3 fromAxes (
         final Vec2 right,
         final Vec2 forward,
         final Mat3 target ) {

      return target.set(
            right.x, forward.x, 0.0f,
            right.y, forward.y, 0.0f,
            0.0f, 0.0f, 1.0f);
   }

   public static Mat3 fromAxes (
         final Vec2 right,
         final Vec2 forward,
         final Vec2 translation,
         final Mat3 target ) {

      return target.set(
            right.x, forward.x, translation.x,
            right.y, forward.y, translation.y,
            0.0f, 0.0f, 1.0f);
   }

   public static Mat3 fromAxes (
         final Vec3 right,
         final Vec3 forward,
         final Mat3 target ) {

      return target.set(
            right.x, forward.x, 0.0f,
            right.y, forward.y, 0.0f,
            right.z, forward.z, 1.0f);
   }

   public static Mat3 fromAxes (
         final Vec3 right,
         final Vec3 forward,
         final Vec3 translation,
         final Mat3 target ) {

      return target.set(
            right.x, forward.x, translation.x,
            right.y, forward.y, translation.y,
            right.z, forward.z, translation.z);
   }

   public static Mat3 fromRotZ (
         final float cosa,
         final float sina,
         final Mat3 target ) {

      return target.set(
            cosa, -sina, 0.0f,
            sina, cosa, 0.0f,
            0.0f, 0.0f, 1.0f);
   }

   public static Mat3 fromRotZ (
         final float radians,
         final Mat3 target ) {

      return Mat3.fromRotZ(
            (float) Math.cos(radians),
            (float) Math.sin(radians),
            target);
   }

   public static Mat3 fromScale (
         final float scalar,
         final Mat3 target ) {

      return target.set(
            scalar, 0.0f, 0.0f,
            0.0f, scalar, 0.0f,
            0.0f, 0.0f, 1.0f);
   }

   public static Mat3 fromScale (
         final Vec2 scalar,
         final Mat3 target ) {

      return target.set(
            scalar.x, 0.0f, 0.0f,
            0.0f, scalar.y, 0.0f,
            0.0f, 0.0f, 1.0f);
   }

   public static Mat3 fromTranslation (
         final Vec2 translation,
         final Mat3 target ) {

      return target.set(
            1.0f, 0.0f, translation.x,
            0.0f, 1.0f, translation.y,
            0.0f, 0.0f, 1.0f);
   }

   public static Mat3 identity ( final Mat3 target ) {

      return target.set(
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f);
   }

   public static boolean isIdentity ( final Mat3 m ) {

      return m.m22 == 1.0f && m.m11 == 1.0f && m.m00 == 1.0f &&
            m.m01 == 0.0f && m.m02 == 0.0f && m.m10 == 0.0f &&
            m.m12 == 0.0f && m.m20 == 0.0f && m.m21 == 0.0f;
   }

   public static Mat3 mult (
         final float a,
         final Mat3 b,
         final Mat3 target ) {

      return target.set(
            a * b.m00, a * b.m01, a * b.m02,
            a * b.m10, a * b.m11, a * b.m12,
            a * b.m20, a * b.m21, a * b.m22);
   }

   public static Mat3 mult (
         final Mat3 a,
         final float b,
         final Mat3 target ) {

      return target.set(
            a.m00 * b, a.m01 * b, a.m02 * b,
            a.m10 * b, a.m11 * b, a.m12 * b,
            a.m20 * b, a.m21 * b, a.m22 * b);
   }

   public static Mat3 mult (
         final Mat3 a,
         final Mat3 b,
         final Mat3 target ) {

      return target.set(
            a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20,
            a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21,
            a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22,

            a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20,
            a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21,
            a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22,

            a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20,
            a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21,
            a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22);
   }

   public static Mat3 mult (
         final Mat3 a,
         final Mat3 b,
         final Mat3 c,
         final Mat3 target ) {

      final float n00 = a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20;
      final float n01 = a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21;
      final float n02 = a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22;

      final float n10 = a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20;
      final float n11 = a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21;
      final float n12 = a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22;

      final float n20 = a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20;
      final float n21 = a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21;
      final float n22 = a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22;

      return target.set(
            n00 * c.m00 + n01 * c.m10 + n02 * c.m20,
            n00 * c.m01 + n01 * c.m11 + n02 * c.m21,
            n00 * c.m02 + n01 * c.m12 + n02 * c.m22,

            n10 * c.m00 + n11 * c.m10 + n12 * c.m20,
            n10 * c.m01 + n11 * c.m11 + n12 * c.m21,
            n10 * c.m02 + n11 * c.m12 + n12 * c.m22,

            n20 * c.m00 + n21 * c.m10 + n22 * c.m20,
            n20 * c.m01 + n21 * c.m11 + n22 * c.m21,
            n20 * c.m02 + n21 * c.m12 + n22 * c.m22);
   }

   public static Vec3 mult (
         final Mat3 m,
         final Vec3 source,
         final Vec3 target ) {

      return target.set(
            m.m00 * source.x + m.m01 * source.y + m.m02 * source.z,
            m.m10 * source.x + m.m11 * source.y + m.m12 * source.z,
            m.m20 * source.x + m.m21 * source.y + m.m22 * source.z);
   }

   public static Mat3 sub (
         final Mat3 a,
         final Mat3 b,
         final Mat3 target ) {

      return target.set(
            a.m00 - b.m00, a.m01 - b.m01, a.m02 - b.m02,
            a.m10 - b.m10, a.m11 - b.m11, a.m12 - b.m12,
            a.m20 - b.m20, a.m21 - b.m21, a.m22 - b.m22);
   }

   public static Mat3 transpose (
         final Mat3 m,
         final Mat3 target ) {

      return target.set(
            m.m00, m.m10, m.m20,
            m.m01, m.m11, m.m21,
            m.m02, m.m12, m.m22);
   }

   public float m00 = 1.0f;
   public float m01 = 0.0f;
   public float m02 = 0.0f;

   public float m10 = 0.0f;
   public float m11 = 1.0f;
   public float m12 = 0.0f;

   public float m20 = 0.0f;
   public float m21 = 0.0f;
   public float m22 = 1.0f;

   public Mat3 () {

      super(9);
   }

   public Mat3 (
         final float m00, final float m01,
         final float m10, final float m11 ) {

      super(9);
      this.set(m00, m01, m10, m11);
   }

   public Mat3 (
         final float m00, final float m01, final float m02,
         final float m10, final float m11, final float m12 ) {

      super(9);
      this.set(
            m00, m01, m02,
            m10, m11, m12);
   }

   public Mat3 (
         final float m00, final float m01, final float m02,
         final float m10, final float m11, final float m12,
         final float m20, final float m21, final float m22 ) {

      super(9);
      this.set(
            m00, m01, m02,
            m10, m11, m12,
            m20, m21, m22);
   }

   public Mat3 ( final Mat3 source ) {

      super(9);
      this.set(source);
   }

   protected boolean equals ( final Mat3 m ) {

      if (Float.floatToIntBits(this.m00) != Float.floatToIntBits(m.m00)) {
         return false;
      }
      if (Float.floatToIntBits(this.m01) != Float.floatToIntBits(m.m01)) {
         return false;
      }
      if (Float.floatToIntBits(this.m02) != Float.floatToIntBits(m.m02)) {
         return false;
      }

      if (Float.floatToIntBits(this.m10) != Float.floatToIntBits(m.m10)) {
         return false;
      }
      if (Float.floatToIntBits(this.m11) != Float.floatToIntBits(m.m11)) {
         return false;
      }
      if (Float.floatToIntBits(this.m12) != Float.floatToIntBits(m.m12)) {
         return false;
      }

      if (Float.floatToIntBits(this.m20) != Float.floatToIntBits(m.m20)) {
         return false;
      }
      if (Float.floatToIntBits(this.m21) != Float.floatToIntBits(m.m21)) {
         return false;
      }
      if (Float.floatToIntBits(this.m22) != Float.floatToIntBits(m.m22)) {
         return false;
      }

      return true;
   }

   @Override
   public Mat3 clone () {

      return new Mat3(
            this.m00, this.m01, this.m02,
            this.m10, this.m11, this.m12,
            this.m20, this.m21, this.m22);
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

      return this.equals((Mat3) obj);
   }

   @Override
   public float get ( final int index ) {

      switch (index) {

         /* Row 0 */

         case 0:
         case -9:
            return this.m00;
         case 1:
         case -8:
            return this.m01;
         case 2:
         case -7:
            return this.m02;

         /* Row 1 */

         case 3:
         case -6:
            return this.m10;
         case 4:
         case -5:
            return this.m11;
         case 5:
         case -4:
            return this.m12;

         /* Row 2 */

         case 6:
         case -3:
            return this.m20;
         case 7:
         case -2:
            return this.m21;
         case 8:
         case -1:
            return this.m22;

         default:
            return 0.0f;
      }
   }

   @Override
   public float get ( final int i, final int j ) {

      switch (i) {
         case 0:
         case -3:

            switch (j) {
               case 0:
               case -3:
                  return this.m00;
               case 1:
               case -2:
                  return this.m01;
               case 2:
               case -1:
                  return this.m02;
               default:
                  return 0.0f;
            }

         case 1:
         case -2:

            switch (j) {
               case 0:
               case -3:
                  return this.m10;
               case 1:
               case -2:
                  return this.m11;
               case 2:
               case -1:
                  return this.m12;
               default:
                  return 0.0f;
            }

         case 2:
         case -1:

            switch (j) {
               case 0:
               case -3:
                  return this.m20;
               case 1:
               case -2:
                  return this.m21;
               case 2:
               case -1:
                  return this.m22;
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

      result = prime * result + Float.floatToIntBits(this.m10);
      result = prime * result + Float.floatToIntBits(this.m11);
      result = prime * result + Float.floatToIntBits(this.m12);

      result = prime * result + Float.floatToIntBits(this.m20);
      result = prime * result + Float.floatToIntBits(this.m21);
      result = prime * result + Float.floatToIntBits(this.m22);

      return result;
   }

   @Override
   public Iterator < Float > iterator () {

      return new M3Iterator(this);
   }

   public Mat3 reset () {

      return this.set(
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f);
   }

   public Mat3 set (
         final float m00, final float m01,
         final float m10, final float m11 ) {

      return this.set(
            m00, m01, 0.0f,
            m10, m11, 0.0f,
            0.0f, 0.0f, 1.0f);
   }

   public Mat3 set (
         final float m00, final float m01, final float m02,
         final float m10, final float m11, final float m12 ) {

      return this.set(
            m00, m01, m02,
            m10, m11, m12,
            0.0f, 0.0f, 1.0f);
   }

   public Mat3 set (
         final float m00, final float m01, final float m02,
         final float m10, final float m11, final float m12,
         final float m20, final float m21, final float m22 ) {

      this.m00 = m00;
      this.m01 = m01;
      this.m02 = m02;

      this.m10 = m10;
      this.m11 = m11;
      this.m12 = m12;

      this.m20 = m20;
      this.m21 = m21;
      this.m22 = m22;

      return this;
   }

   public Mat3 set ( final Mat3 source ) {

      return this.set(
            source.m00, source.m01, source.m02,
            source.m10, source.m11, source.m12,
            source.m20, source.m21, source.m22);
   }

   @Override
   public float[] toArray () {

      return new float[] {
            this.m00, this.m01, this.m02,
            this.m10, this.m11, this.m12,
            this.m20, this.m21, this.m22 };
   }

   @Override
   public String toString () {

      return this.toString(4);
   }

   public String toString ( final int places ) {

      return new StringBuilder()
            .append("{ m00: ")
            .append(Utils.toFixed(this.m00, places))
            .append(", m01: ")
            .append(Utils.toFixed(this.m01, places))
            .append(", m02: ")
            .append(Utils.toFixed(this.m02, places))

            .append(", m10: ")
            .append(Utils.toFixed(this.m10, places))
            .append(", m11: ")
            .append(Utils.toFixed(this.m11, places))
            .append(", m12: ")
            .append(Utils.toFixed(this.m12, places))

            .append(", m20: ")
            .append(Utils.toFixed(this.m20, places))
            .append(", m21: ")
            .append(Utils.toFixed(this.m21, places))
            .append(", m22: ")
            .append(Utils.toFixed(this.m22, places))

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

            .append('\n')
            .append(Utils.toFixed(this.m10, places))
            .append('\t')
            .append(Utils.toFixed(this.m11, places))
            .append('\t')
            .append(Utils.toFixed(this.m12, places))

            .append('\n')
            .append(Utils.toFixed(this.m20, places))
            .append('\t')
            .append(Utils.toFixed(this.m21, places))
            .append('\t')
            .append(Utils.toFixed(this.m22, places))

            .append('\n')
            .toString();
   }
}
