package camzup.pfriendly;

import camzup.core.ITransform;
import camzup.core.Quaternion;
import camzup.core.Transform2;
import camzup.core.Transform3;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;

import processing.core.PMatrix2D;
import processing.core.PMatrix3D;
import processing.core.PVector;

/**
 * Facilitates conversions between Cam Z up objects and
 * Processing objects.
 */
public abstract class Convert {

   /**
    * Converts a 2D transform to a PMatrix2D.
    *
    * @param tr2
    *           the transform
    * @param order
    *           the transform order
    * @return the matrix
    */
   public static PMatrix2D toPMatrix2D (
         final Transform2 tr2,
         final ITransform.Order order ) {

      return Convert.toPMatrix2D(tr2, order, (PMatrix2D) null);
   }

   /**
    * Converts a 2D transform to a PMatrix2D.
    *
    * @param tr2
    *           the transform
    * @param order
    *           the transform order
    * @param target
    *           the output matrix
    * @return the matrix
    */
   public static PMatrix2D toPMatrix2D (
         final Transform2 tr2,
         final ITransform.Order order,
         PMatrix2D target ) {

      if (target == null) {
         target = new PMatrix2D();
      }

      final Vec2 dim = tr2.getScale(new Vec2());
      final Vec2 loc = tr2.getLocation(new Vec2());
      final float angle = tr2.getRotation();

      target.reset();

      switch (order) {

         case RST:

            target.rotateZ(angle);
            target.scale(dim.x, dim.y);
            target.translate(loc.x, loc.y);

            return target;

         case RTS:

            target.rotateZ(angle);
            target.translate(loc.x, loc.y);
            target.scale(dim.x, dim.y);

            return target;

         case SRT:

            target.scale(dim.x, dim.y);
            target.rotateZ(angle);
            target.translate(loc.x, loc.y);

            return target;

         case STR:

            target.scale(dim.x, dim.y);
            target.translate(loc.x, loc.y);
            target.rotateZ(angle);

            return target;

         case TSR:

            target.translate(loc.x, loc.y);
            target.scale(dim.x, dim.y);
            target.rotateZ(angle);

            return target;

         case TRS:

         default:

            target.translate(loc.x, loc.y);
            target.rotateZ(angle);
            target.scale(dim.x, dim.y);

            return target;
      }
   }

   /**
    * Converts a quaternion to a PMatrix3D.
    *
    * @param source
    *           the quaternion
    * @return the matrix
    */
   public static PMatrix3D toPMatrix3D ( final Quaternion source ) {

      return Convert.toPMatrix3D(source, (PMatrix3D) null);
   }

   /**
    * Converts a quaternion to a PMatrix3D.
    *
    * @param source
    *           the quaternion
    * @param target
    *           the matrix
    * @return the matrix
    */
   public static PMatrix3D toPMatrix3D (
         final Quaternion source,
         PMatrix3D target ) {

      if (target == null) {
         target = new PMatrix3D();
      }

      final float w = source.real;
      final Vec3 i = source.imag;
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

      target.set(
            1.0f - ysq2 - zsq2, xy2 - wz2, xz2 + wy2, 0.0f,
            xy2 + wz2, 1.0f - xsq2 - zsq2, yz2 - wx2, 0.0f,
            xz2 - wy2, yz2 + wx2, 1.0f - xsq2 - ysq2, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
      return target;
   }

   /**
    * Converts a transform to a PMatrix3D.
    *
    * @param tr3
    *           the transform
    * @param order
    *           the transform order
    * @return the transform
    */
   public static PMatrix3D toPMatrix3D (
         final Transform3 tr3,
         final ITransform.Order order ) {

      return Convert.toPMatrix3D(tr3, order, (PMatrix3D) null);
   }

   /**
    * Converts a transform to a PMatrix3D.
    *
    * @param tr3
    *           the transform
    * @param order
    *           the transform order
    * @param target
    *           the output PMatrix3D
    * @return the transform
    */
   public static PMatrix3D toPMatrix3D (
         final Transform3 tr3,
         final ITransform.Order order,

         PMatrix3D target ) {

      if (target == null) {
         target = new PMatrix3D();
      }

      final Quaternion quat = tr3.getRotation(new Quaternion());
      final Vec3 axis = new Vec3();
      final float angle = Quaternion.toAxisAngle(quat, axis);

      final Vec3 dim = tr3.getScale(new Vec3());
      final Vec3 loc = tr3.getLocation(new Vec3());

      target.reset();

      switch (order) {

         case RST:

            target.rotate(angle, axis.x, axis.y, axis.z);
            target.scale(dim.x, dim.y, dim.z);
            target.translate(loc.x, loc.y, loc.z);

            return target;

         case RTS:

            target.rotate(angle, axis.x, axis.y, axis.z);
            target.translate(loc.x, loc.y, loc.z);
            target.scale(dim.x, dim.y, dim.z);

            return target;

         case SRT:

            target.scale(dim.x, dim.y, dim.z);
            target.rotate(angle, axis.x, axis.y, axis.z);
            target.translate(loc.x, loc.y, loc.z);

            return target;

         case STR:

            target.scale(dim.x, dim.y, dim.z);
            target.translate(loc.x, loc.y, loc.z);
            target.rotate(angle, axis.x, axis.y, axis.z);

            return target;

         case TSR:

            target.translate(loc.x, loc.y, loc.z);
            target.scale(dim.x, dim.y, dim.z);
            target.rotate(angle, axis.x, axis.y, axis.z);

            return target;

         case TRS:

         default:

            target.translate(loc.x, loc.y, loc.z);
            target.rotate(angle, axis.x, axis.y, axis.z);
            target.scale(dim.x, dim.y, dim.z);

            return target;
      }
   }

   /**
    * Converts a Vec2 to a PVector.
    *
    * @param source
    *           the source vector
    * @return the vector
    */
   public static PVector toPVector ( final Vec2 source ) {

      return Convert.toPVector(source, (PVector) null);
   }

   /**
    * Converts a Vec2 to a PVector.
    *
    * @param source
    *           the source vector
    * @param target
    *           the target vector
    * @return the vector
    */
   public static PVector toPVector (
         final Vec2 source,
         PVector target ) {

      if (target == null) {
         target = new PVector();
      }
      return target.set(source.x, source.y, 0.0f);
   }

   /**
    * Converts a Vec3 to a PVector.
    *
    * @param source
    *           the source vector
    * @return the vector
    */
   public static PVector toPVector ( final Vec3 source ) {

      return Convert.toPVector(source, (PVector) null);
   }

   /**
    * Converts a Vec3 to a PVector.
    *
    * @param source
    *           the source vector
    * @param target
    *           the target vector
    * @return the vector
    */
   public static PVector toPVector (
         final Vec3 source,
         PVector target ) {

      if (target == null) {
         target = new PVector();
      }
      return target.set(source.x, source.y, source.z);
   }

   /**
    * Converts a PMatrix3D, representing a rotation matrix, to
    * a quaternion.
    *
    * @param source
    *           the PMatrix3D
    * @param target
    *           the quaternion
    * @return the quaternion
    * @see Math#sqrt(double)
    * @see Utils#max(float, float)
    * @see Utils#sign(float)
    */
   public static Quaternion toQuaternion (
         final PMatrix3D source,
         final Quaternion target ) {

      final float ix = source.m00;
      final float jy = source.m11;
      final float kz = source.m22;

      final float w = (float) (Math.sqrt(
            Utils.max(0.0f, 1.0f + ix + jy + kz)) * 0.5d);
      float x = (float) (Math.sqrt(
            Utils.max(0.0f, 1.0f + ix - jy - kz)) * 0.5d);
      float y = (float) (Math.sqrt(
            Utils.max(0.0f, 1.0f - ix + jy - kz)) * 0.5d);
      float z = (float) (Math.sqrt(
            Utils.max(0.0f, 1.0f - ix - jy + kz)) * 0.5d);

      x = Math.copySign(x, source.m21 - source.m12);
      y = Math.copySign(y, source.m02 - source.m20);
      z = Math.copySign(z, source.m10 - source.m01);

      return target.set(w, x, y, z);
   }

   /**
    * Converts from a PVector to a Vec2.
    *
    * @param source
    *           the source vector
    * @param target
    *           the target vector
    * @return the vector
    */
   public static Vec2 toVec2 (
         final PVector source,
         final Vec2 target ) {

      // if(source.z != 0.0f) {
      // }
      return target.set(source.x, source.y);
   }

   /**
    * Converts from a PVector to a Vec3.
    *
    * @param source
    *           the source vector
    * @param target
    *           the target vector
    * @return the vector
    */
   public static Vec3 toVec3 (
         final PVector source,
         final Vec3 target ) {

      return target.set(source.x, source.y, source.z);
   }

}
