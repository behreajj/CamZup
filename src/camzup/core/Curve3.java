package camzup.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Organizes a Bezier curve into a list of knots. Provides a
 * function to retrieve a point and tangent on a curve from
 * a step in the range [0.0, 1.0].
 */
public class Curve3 extends Curve
      implements Iterable < Curve3.Knot3 > {

   /**
    * Groups together vectors which shape a Bezier curve into a
    * coordinate (or anchor point), fore handle (the following
    * control point) and rear handle (the preceding control
    * point).
    */
   public static class Knot3 implements Comparable < Knot3 > {

      /**
       * Creates a knot from polar coordinates, where the knot's
       * fore handle is tangent to the radius.
       *
       * @param cosa
       *           the cosine of the angle
       * @param sina
       *           the sine of the angle
       * @param radius
       *           the radius
       * @param handleMag
       *           the length of the handles
       * @param target
       *           the output knot
       * @return the knot
       */
      public static Knot3 fromPolar (
            final float cosa,
            final float sina,
            final float radius,
            final float handleMag,
            final Knot3 target ) {

         final Vec3 coord = target.coord;
         coord.set(
               cosa * radius,
               sina * radius,
               0.0f);

         final float hmsina = sina * handleMag;
         final float hmcosa = cosa * handleMag;

         target.foreHandle.set(
               coord.x - hmsina,
               coord.y + hmcosa,
               coord.z);

         target.rearHandle.set(
               coord.x + hmsina,
               coord.y - hmcosa,
               coord.z);

         return target;
      }

      /**
       * Creates a knot from polar coordinates, where the knot's
       * fore handle is tangent to the radius.
       *
       * @param angle
       *           the angle in radians
       * @param radius
       *           the radius
       * @param handleMag
       *           the length of the handles
       * @param target
       *           the output knot
       * @return the knot
       */
      public static Knot3 fromPolar (
            final float angle,
            final float radius,
            final float handleMag,
            final Knot3 target ) {

         final float nrm = IUtils.ONE_TAU * angle;
         return Knot3.fromPolar(
               SinCos.eval(nrm),
               SinCos.eval(nrm - 0.25f),
               radius, handleMag, target);
      }

      /**
       * The spatial coordinate of the knot.
       */
      public final Vec3 coord = new Vec3();

      /**
       * The handle which warps the curve segment heading away
       * from the knot along the direction of the curve.
       */
      public final Vec3 foreHandle = new Vec3();

      /**
       * The handle which warps the curve segment heading towards
       * the knot along the direction of the curve.
       */
      public final Vec3 rearHandle = new Vec3();

      /**
       * The default constructor.
       */
      public Knot3 () {

      }

      /**
       * Creates a knot from a coordinate.
       *
       * @param xCoord
       *           the x coordinate
       * @param yCoord
       *           the y coordinate
       * @param zCoord
       *           the z coordinate
       */
      public Knot3 (
            final float xCoord,
            final float yCoord,
            final float zCoord ) {

         this.set(xCoord, yCoord, zCoord);
      }

      /**
       * Creates a knot from real numbers.
       *
       * @param xCoord
       *           the x coordinate
       * @param yCoord
       *           the y coordinate
       * @param zCoord
       *           the z coordinate
       * @param xFore
       *           the fore handle x
       * @param yFore
       *           the fore handle y
       * @param zFore
       *           the fore handle z
       */
      public Knot3 (
            final float xCoord,
            final float yCoord,
            final float zCoord,
            final float xFore,
            final float yFore,
            final float zFore ) {

         this.set(
               xCoord, yCoord, zCoord,
               xFore, yFore, zFore);
      }

      /**
       * Creates a knot from real numbers.
       *
       * @param xCoord
       *           the x coordinate
       * @param yCoord
       *           the y coordinate
       * @param zCoord
       *           the z coordinate
       * @param xFore
       *           the fore handle x
       * @param yFore
       *           the fore handle y
       * @param zFore
       *           the fore handle z
       * @param xRear
       *           the rear handle x
       * @param yRear
       *           the rear handle y
       * @param zRear
       *           the rear handle z
       */
      public Knot3 (
            final float xCoord,
            final float yCoord,
            final float zCoord,
            final float xFore,
            final float yFore,
            final float zFore,
            final float xRear,
            final float yRear,
            final float zRear ) {

         this.set(
               xCoord, yCoord, zCoord,
               xFore, yFore, zFore,
               xRear, yRear, zRear);
      }

      /**
       * Creates a knot from a source knot.
       *
       * @param source
       *           the source
       */
      public Knot3 ( final Knot3 source ) {

         this.set(source);
      }

      /**
       * Attempts to create a knot from Strings.
       *
       * @param xCoord
       *           the x coordinate string
       * @param yCoord
       *           the y coordinate string
       * @param zCoord
       *           the z coordinate string
       * @param xFore
       *           the x fore handle string
       * @param yFore
       *           the y fore handle string
       * @param zFore
       *           the z fore handle string
       * @param xRear
       *           the x rear handle string
       * @param yRear
       *           the y rear handle string
       * @param zRear
       *           the z rear handle string
       */
      public Knot3 (
            final String xCoord,
            final String yCoord,
            final String zCoord,
            final String xFore,
            final String yFore,
            final String zFore,
            final String xRear,
            final String yRear,
            final String zRear ) {

         this.set(
               xCoord, yCoord, zCoord,
               xFore, yFore, zFore,
               xRear, yRear, zRear);
      }

      /**
       * Creates a knot from a coordinate.
       *
       * @param coord
       *           the coordinate
       */
      public Knot3 ( final Vec3 coord ) {

         this.set(coord);
      }

      /**
       * Creates a knot from a coordinate and fore-handle. The
       * rear handle is a mirror of the fore.
       *
       * @param coord
       *           the coordinate
       * @param foreHandle
       *           the fore handle
       */
      public Knot3 (
            final Vec3 coord,
            final Vec3 foreHandle ) {

         this.set(coord, foreHandle);
      }

      /**
       * Creates a knot from a series of vectors.
       *
       * @param coord
       *           the coordinate
       * @param foreHandle
       *           the fore handle
       * @param rearHandle
       *           the rear handle
       */
      public Knot3 (
            final Vec3 coord,
            final Vec3 foreHandle,
            final Vec3 rearHandle ) {

         this.set(coord, foreHandle, rearHandle);
      }

      /**
       * Tests to see if this knot equals another.
       *
       * @param other
       *           the other knot
       * @return the evaluation
       */
      protected boolean equals ( final Knot3 other ) {

         if (this.coord == null) {
            if (other.coord != null) {
               return false;
            }
         } else if (!this.coord.equals(other.coord)) {
            return false;
         }

         if (this.foreHandle == null) {
            if (other.foreHandle != null) {
               return false;
            }
         } else if (!this.foreHandle.equals(other.foreHandle)) {
            return false;
         }

         if (this.rearHandle == null) {
            if (other.rearHandle != null) {
               return false;
            }
         } else if (!this.rearHandle.equals(other.rearHandle)) {
            return false;
         }

         return true;
      }

      /**
       * Aligns this knot's handles in the same direction while
       * preserving their magnitude.
       *
       * @return this knot
       * @see Knot3#alignHandlesForward()
       */
      @Chainable
      public Knot3 alignHandles () {

         return this.alignHandlesForward();
      }

      /**
       * Aligns this knot's fore handle to its rear handle while
       * preserving magnitude.
       *
       * @return this knot
       * @see Utils#hypot(float, float, float)
       * @see Utils#invHypot(float, float, float)
       */
      @Chainable
      public Knot3 alignHandlesBackward () {

         final float cox = this.coord.x;
         final float coy = this.coord.y;
         final float coz = this.coord.z;

         final float rearDirx = this.rearHandle.x - cox;
         final float rearDiry = this.rearHandle.y - coy;
         final float rearDirz = this.rearHandle.z - coz;

         final float foreDirx = this.foreHandle.x - cox;
         final float foreDiry = this.foreHandle.y - coy;
         final float foreDirz = this.foreHandle.z - coz;

         final float flipRescale = -Utils.hypot(foreDirx, foreDiry, foreDirz)
               * Utils.invHypot(rearDirx, rearDiry, rearDirz);

         this.foreHandle.x = rearDirx * flipRescale + cox;
         this.foreHandle.y = rearDiry * flipRescale + coy;
         this.foreHandle.z = rearDirz * flipRescale + coz;

         return this;
      }

      /**
       * Aligns this knot's rear handle to its fore handle while
       * preserving magnitude.
       *
       * @return this knot
       * @see Utils#hypot(float, float, float)
       * @see Utils#invHypot(float, float, float)
       */
      @Chainable
      public Knot3 alignHandlesForward () {

         final float cox = this.coord.x;
         final float coy = this.coord.y;
         final float coz = this.coord.z;

         final float rearDirx = this.rearHandle.x - cox;
         final float rearDiry = this.rearHandle.y - coy;
         final float rearDirz = this.rearHandle.z - coz;

         final float foreDirx = this.foreHandle.x - cox;
         final float foreDiry = this.foreHandle.y - coy;
         final float foreDirz = this.foreHandle.z - coz;

         final float flipRescale = -Utils.hypot(rearDirx, rearDiry, rearDirz)
               * Utils.invHypot(foreDirx, foreDiry, foreDirz);

         this.rearHandle.x = foreDirx * flipRescale + cox;
         this.rearHandle.y = foreDiry * flipRescale + coy;
         this.rearHandle.z = foreDirz * flipRescale + coz;

         return this;
      }

      /**
       * Creates a new knot with the coordinate and handles of
       * this knot.
       *
       * @return a new knot
       */
      @Override
      public Knot3 clone () {

         return new Knot3(
               this.coord,
               this.foreHandle,
               this.rearHandle);
      }

      /**
       * Compares this knot to another based on a comparison
       * between coordinates.
       *
       * @param knot
       *           the other knot
       * @return the evaluation
       */
      @Override
      public int compareTo ( final Knot3 knot ) {

         return this.coord.compareTo(knot.coord);
      }

      /**
       * Tests to see if this knot equals an object
       *
       * @param obj
       *           the object
       * @return the evaluation
       */
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
         return this.equals((Knot3) obj);
      }

      /**
       * Returns the knot's hash code based on those of its three
       * constituent vectors.
       *
       * @return the hash code
       */
      @Override
      public int hashCode () {

         int hash = IUtils.HASH_BASE;
         hash = hash * IUtils.HASH_MUL
               ^ (this.coord == null ? 0 : this.coord.hashCode());
         hash = hash * IUtils.HASH_MUL
               ^ (this.foreHandle == null ? 0 : this.foreHandle.hashCode());
         hash = hash * IUtils.HASH_MUL
               ^ (this.rearHandle == null ? 0 : this.rearHandle.hashCode());
         return hash;
      }

      /**
       * Mirrors this knot's handles. Defaults to mirroring in the
       * forward direction.
       *
       * @return this knot
       * @see Knot3#mirrorHandlesForward()
       */
      @Chainable
      public Knot3 mirrorHandles () {

         return this.mirrorHandlesForward();
      }

      /**
       * Sets the forward-facing handle to mirror the rear-facing
       * handle: the fore will have the same magnitude and negated
       * direction of the rear.
       *
       * @return this knot
       */
      @Chainable
      public Knot3 mirrorHandlesBackward () {

         this.foreHandle.set(
               this.coord.x - (this.rearHandle.x - this.coord.x),
               this.coord.y - (this.rearHandle.y - this.coord.y),
               this.coord.z - (this.rearHandle.z - this.coord.z));
         return this;
      }

      /**
       * Sets the rear-facing handle to mirror the forward-facing
       * handle: the rear will have the same magnitude and negated
       * direction of the fore.
       *
       * @return this knot
       */
      @Chainable
      public Knot3 mirrorHandlesForward () {

         this.rearHandle.set(
               this.coord.x - (this.foreHandle.x - this.coord.x),
               this.coord.y - (this.foreHandle.y - this.coord.y),
               this.coord.z - (this.foreHandle.z - this.coord.z));
         return this;
      }

      /**
       * Reverses the knot's direction by swapping the fore- and
       * rear-handles.
       *
       * @return this knot
       */
      @Chainable
      public Knot3 reverse () {

         final float tx = this.foreHandle.x;
         final float ty = this.foreHandle.y;
         final float tz = this.foreHandle.z;
         this.foreHandle.set(this.rearHandle);
         this.rearHandle.set(tx, ty, tz);

         return this;
      }

      /**
       * Rotates this knot around an axis by an angle in radians.
       * The axis is assumed to be of unit length. Accepts
       * pre-calculated sine and cosine of an angle, so that
       * collections of knots can be efficiently rotated without
       * repeatedly calling cos and sin.
       *
       * @param cosa
       *           cosine of the angle
       * @param sina
       *           sine of the angle
       * @param axis
       *           the axis of rotation
       * @return this knot
       * @see Vec3#rotate(Vec3, float, float, Vec3, Vec3)
       */
      @Chainable
      public Knot3 rotate (
            final float cosa,
            final float sina,
            final Vec3 axis ) {

         Vec3.rotate(this.coord, cosa, sina, axis, this.coord);
         Vec3.rotate(this.foreHandle, cosa, sina, axis, this.foreHandle);
         Vec3.rotate(this.rearHandle, cosa, sina, axis, this.rearHandle);

         return this;
      }

      /**
       * Rotates this knot around an arbitrary axis by an angle in
       * radians.
       *
       * @param radians
       *           the angle
       * @param axis
       *           the axis of rotation
       * @return this knot
       */
      @Chainable
      public Knot3 rotate ( final float radians, final Vec3 axis ) {

         final float nrm = IUtils.ONE_TAU * radians;
         final float cosa = SinCos.eval(nrm);
         final float sina = SinCos.eval(nrm - 0.25f);

         return this.rotate(cosa, sina, axis);
      }

      /**
       * Rotates this knot around the x axis by an angle in
       * radians.
       *
       * @param radians
       *           the angle
       * @return this knot
       */
      @Chainable
      public Knot3 rotateX ( final float radians ) {

         final float nrm = IUtils.ONE_TAU * radians;
         final float cosa = SinCos.eval(nrm);
         final float sina = SinCos.eval(nrm - 0.25f);

         return this.rotateX(cosa, sina);
      }

      /**
       * Rotates a knot around the x axis. Accepts pre-calculated
       * sine and cosine of an angle, so that collections of knots
       * can be efficiently rotated without repeatedly calling cos
       * and sin.
       *
       * @param cosa
       *           cosine of the angle
       * @param sina
       *           sine of the angle
       * @return this knot
       */
      @Chainable
      public Knot3 rotateX ( final float cosa, final float sina ) {

         Vec3.rotateX(this.coord, cosa, sina, this.coord);
         Vec3.rotateX(this.foreHandle, cosa, sina, this.foreHandle);
         Vec3.rotateX(this.rearHandle, cosa, sina, this.rearHandle);

         return this;
      }

      /**
       * Rotates this knot around the y axis by an angle in
       * radians.
       *
       * @param radians
       *           the angle
       * @return this knot
       */
      @Chainable
      public Knot3 rotateY ( final float radians ) {

         final float nrm = IUtils.ONE_TAU * radians;
         final float cosa = SinCos.eval(nrm);
         final float sina = SinCos.eval(nrm - 0.25f);

         return this.rotateY(cosa, sina);
      }

      /**
       * Rotates a knot around the y axis. Accepts pre-calculated
       * sine and cosine of an angle, so that collections of knots
       * can be efficiently rotated without repeatedly calling cos
       * and sin.
       *
       * @param cosa
       *           cosine of the angle
       * @param sina
       *           sine of the angle
       * @return this knot
       */
      @Chainable
      public Knot3 rotateY ( final float cosa, final float sina ) {

         Vec3.rotateY(this.coord, cosa, sina, this.coord);
         Vec3.rotateY(this.foreHandle, cosa, sina, this.foreHandle);
         Vec3.rotateY(this.rearHandle, cosa, sina, this.rearHandle);

         return this;
      }

      /**
       * Rotates this knot around the z axis by an angle in
       * radians.
       *
       * @param radians
       *           the angle
       * @return this knot
       */
      @Chainable
      public Knot3 rotateZ ( final float radians ) {

         final float nrm = IUtils.ONE_TAU * radians;
         final float cosa = SinCos.eval(nrm);
         final float sina = SinCos.eval(nrm - 0.25f);

         return this.rotateZ(cosa, sina);
      }

      /**
       * Rotates a knot around the z axis. Accepts pre-calculated
       * sine and cosine of an angle, so that collections of knots
       * can be efficiently rotated without repeatedly calling cos
       * and sin.
       *
       * @param cosa
       *           cosine of the angle
       * @param sina
       *           sine of the angle
       * @return this knot
       */
      @Chainable
      public Knot3 rotateZ ( final float cosa, final float sina ) {

         Vec3.rotateZ(this.coord, cosa, sina, this.coord);
         Vec3.rotateZ(this.foreHandle, cosa, sina, this.foreHandle);
         Vec3.rotateZ(this.rearHandle, cosa, sina, this.rearHandle);

         return this;
      }

      /**
       * Scales this knot by a factor.
       *
       * @param scale
       *           the factor
       * @return this knot
       */
      @Chainable
      public Knot3 scale ( final float scale ) {

         Vec3.mul(this.coord, scale, this.coord);
         Vec3.mul(this.foreHandle, scale, this.foreHandle);
         Vec3.mul(this.rearHandle, scale, this.rearHandle);

         return this;
      }

      /**
       * Scales this knot by a non-uniform scalar.
       *
       * @param scale
       *           the non-uniform scalar
       * @return this knot
       */
      @Chainable
      public Knot3 scale ( final Vec3 scale ) {

         Vec3.mul(this.coord, scale, this.coord);
         Vec3.mul(this.foreHandle, scale, this.foreHandle);
         Vec3.mul(this.rearHandle, scale, this.rearHandle);

         return this;
      }

      /**
       * Scales the fore handle by a factor.
       *
       * @param scalar
       *           the scalar
       * @param temp0
       *           a temporary vector
       * @param temp1
       *           a temporary vector
       * @return this knot
       */
      @Chainable
      public Knot3 scaleForeHandleBy (
            final float scalar,
            final Vec3 temp0,
            final Vec3 temp1 ) {

         Vec3.sub(this.foreHandle, this.coord, temp0);
         Vec3.mul(temp0, scalar, temp1);
         Vec3.add(temp1, this.coord, this.foreHandle);

         return this;
      }

      /**
       * Scales the fore handle to a magnitude
       *
       * @param magnitude
       *           the magnitude
       * @param temp0
       *           a temporary vector
       * @param temp1
       *           a temporary vector
       * @return this knot
       */
      @Chainable
      public Knot3 scaleForeHandleTo (
            final float magnitude,
            final Vec3 temp0,
            final Vec3 temp1 ) {

         Vec3.subNorm(this.foreHandle, this.coord, temp0);
         Vec3.mul(temp0, magnitude, temp1);
         Vec3.add(temp1, this.coord, this.foreHandle);

         return this;
      }

      /**
       * Scales both the fore and rear handle by a factor.
       *
       * @param scalar
       *           the scalar
       * @param temp0
       *           a temporary vector
       * @param temp1
       *           a temporary vector
       * @return this knot
       */
      @Chainable
      public Knot3 scaleHandlesBy (
            final float scalar,
            final Vec3 temp0,
            final Vec3 temp1 ) {

         this.scaleForeHandleBy(
               scalar,
               temp0, temp1);
         this.scaleRearHandleBy(
               scalar,
               temp0, temp1);

         return this;
      }

      /**
       * Scales both the fore and rear handle to a magnitude.
       *
       * @param magnitude
       *           the magnitude
       * @param temp0
       *           a temporary vector
       * @param temp1
       *           a temporary vector
       * @return this knot
       * @see Knot3#scaleForeHandleTo(float, Vec3, Vec3)
       * @see Knot3#scaleRearHandleTo(float, Vec3, Vec3)
       */
      @Chainable
      public Knot3 scaleHandlesTo (
            final float magnitude,
            final Vec3 temp0,
            final Vec3 temp1 ) {

         this.scaleForeHandleTo(
               magnitude,
               temp0, temp1);
         this.scaleRearHandleTo(
               magnitude,
               temp0, temp1);

         return this;
      }

      /**
       * Scales the rear handle by a factor.
       *
       * @param scalar
       *           the scalar
       * @param temp0
       *           a temporary vector
       * @param temp1
       *           a temporary vector
       * @return this knot
       * @see Vec3#sub(Vec3, Vec3, Vec3)
       * @see Vec3#mul(Vec3, float, Vec3)
       * @see Vec3#add(Vec3, Vec3, Vec3)
       */
      @Chainable
      public Knot3 scaleRearHandleBy (
            final float scalar,
            final Vec3 temp0,
            final Vec3 temp1 ) {

         Vec3.sub(this.rearHandle, this.coord, temp0);
         Vec3.mul(temp0, scalar, temp1);
         Vec3.add(temp1, this.coord, this.rearHandle);

         return this;
      }

      /**
       * Scales the rear handle to a magnitude
       *
       * @param magnitude
       *           the magnitude
       * @param temp0
       *           a temporary vector
       * @param temp1
       *           a temporary vector
       * @return this knot
       * @see Vec3#subNorm(Vec3, Vec3, Vec3)
       * @see Vec3#mul(Vec3, float, Vec3)
       * @see Vec3#add(Vec3, Vec3, Vec3)
       */
      @Chainable
      public Knot3 scaleRearHandleTo (
            final float magnitude,
            final Vec3 temp0,
            final Vec3 temp1 ) {

         Vec3.subNorm(this.rearHandle, this.coord, temp0);
         Vec3.mul(temp0, magnitude, temp1);
         Vec3.add(temp1, this.coord, this.rearHandle);

         return this;
      }

      /**
       * Sets the coordinate, fore- and rear-handles to the input
       * coordinate.
       *
       * @param xCoord
       *           the x coordinate
       * @param yCoord
       *           the y coordinate
       * @param zCoord
       *           the z coordinate
       * @return this knot
       * @see Math#copySign(float, float)
       */
      @Chainable
      public Knot3 set (
            final float xCoord,
            final float yCoord,
            final float zCoord ) {

         final float xOff = Math.copySign(Utils.EPSILON, xCoord);
         final float yOff = Math.copySign(Utils.EPSILON, yCoord);
         final float zOff = Math.copySign(Utils.EPSILON, zCoord);

         return this.set(
               xCoord, yCoord, zCoord,

               xCoord + xOff,
               yCoord + yOff,
               zCoord + zOff,

               xCoord - xOff,
               yCoord - yOff,
               zCoord - zOff);
      }

      /**
       * Sets the knot's coordinates and fore handle. The rear
       * handle is a mirror of the forehandle.
       *
       * @param xCoord
       *           the x coordinate
       * @param yCoord
       *           the y coordinate
       * @param zCoord
       *           the z coordinate
       * @param xFore
       *           the forehandle x
       * @param yFore
       *           the forehandle y
       * @param zFore
       *           the forehandle z
       * @return this knot
       */
      @Chainable
      public Knot3 set (
            final float xCoord,
            final float yCoord,
            final float zCoord,
            final float xFore,
            final float yFore,
            final float zFore ) {

         this.coord.set(xCoord, yCoord, zCoord);
         this.foreHandle.set(xFore, yFore, zFore);
         this.rearHandle.set(
               xCoord - (xFore - xCoord),
               yCoord - (yFore - yCoord),
               zCoord - (zFore - zCoord));
         return this;
      }

      /**
       * Sets the knot's coordinate, forehandle and rearhandle by
       * component.
       *
       * @param xCoord
       *           the x coordinate
       * @param yCoord
       *           the y coordinate
       * @param zCoord
       *           the z coordinate
       * @param xFore
       *           the forehandle x
       * @param yFore
       *           the forehandle y
       * @param zFore
       *           the forehandle z
       * @param xRear
       *           the rearhandle x
       * @param yRear
       *           the rearhandle y
       * @param zRear
       *           the rearhandle z
       * @return this knot
       */
      @Chainable
      public Knot3 set (
            final float xCoord,
            final float yCoord,
            final float zCoord,
            final float xFore,
            final float yFore,
            final float zFore,
            final float xRear,
            final float yRear,
            final float zRear ) {

         this.coord.set(xCoord, yCoord, zCoord);
         this.foreHandle.set(xFore, yFore, zFore);
         this.rearHandle.set(xRear, yRear, zRear);

         return this;
      }

      /**
       * Sets this knot from a source knot.
       *
       * @param source
       *           the source
       * @return this knot
       */
      @Chainable
      public Knot3 set ( final Knot3 source ) {

         return this.set(
               source.coord,
               source.foreHandle,
               source.rearHandle);
      }

      /**
       * Attempts to set the components of this knot from Strings
       * using {@link Float#parseFloat(String)} . If a
       * NumberFormatException is thrown, the component is set to
       * zero.
       *
       * @param xCoord
       *           the x coordinate string
       * @param yCoord
       *           the y coordinate string
       * @param zCoord
       *           the z coordinate string
       * @param xFore
       *           the x fore handle string
       * @param yFore
       *           the y fore handle string
       * @param zFore
       *           the z fore handle string
       * @param xRear
       *           the x rear handle string
       * @param yRear
       *           the y rear handle string
       * @param zRear
       *           the z rear handle string
       * @return this knot
       */
      @Chainable
      public Knot3 set (
            final String xCoord,
            final String yCoord,
            final String zCoord,
            final String xFore,
            final String yFore,
            final String zFore,
            final String xRear,
            final String yRear,
            final String zRear ) {

         this.coord.set(xCoord, yCoord, zCoord);
         this.foreHandle.set(xFore, yFore, zFore);
         this.rearHandle.set(xRear, yRear, zRear);

         return this;
      }

      /**
       * Sets the coordinate, fore- and rear-handles to the input
       * coordinate.
       *
       * @param coord
       *           the coordinate
       * @return this knot
       */
      public Knot3 set ( final Vec3 coord ) {

         return this.set(coord.x, coord.y, coord.z);
      }

      /**
       * Sets the knot's coordinates and fore handle. The rear
       * handle is a mirror of the forehandle.
       *
       * @param coord
       *           the coordinate
       * @param foreHandle
       *           the fore handle
       * @return this knot
       */
      @Chainable
      public Knot3 set (
            final Vec3 coord,
            final Vec3 foreHandle ) {

         return this.set(
               coord.x,
               coord.y,
               coord.z,
               foreHandle.x,
               foreHandle.y,
               foreHandle.z);
      }

      /**
       * Sets this knot from a series of vectors.
       *
       * @param coord
       *           the coord
       * @param foreHandle
       *           the fore handle
       * @param rearHandle
       *           the rear handle
       * @return this knot
       */
      @Chainable
      public Knot3 set (
            final Vec3 coord,
            final Vec3 foreHandle,
            final Vec3 rearHandle ) {

         this.coord.set(coord);
         this.foreHandle.set(foreHandle);
         this.rearHandle.set(rearHandle);
         return this;
      }

      /**
       * Returns a 2D array representation of this knot. The
       * coordinate is the first element; fore handle, the second;
       * rear handle, the third.
       *
       * @return the array
       * @see Vec3#toArray()
       */
      public float[][] toArray () {

         return new float[][] {
               this.coord.toArray(),
               this.foreHandle.toArray(),
               this.rearHandle.toArray()
         };
      }

      /**
       * Returns a string representation of this knot.
       *
       * @return the string
       */
      @Override
      public String toString () {

         return this.toString(4);
      }

      /**
       * Returns a string representation of this knot.
       *
       * @param places
       *           the number of places
       * @return the string
       */
      public String toString ( final int places ) {

         return new StringBuilder(320)
               .append("{ coord: ")
               .append(this.coord.toString(places))
               .append(", foreHandle: ")
               .append(this.foreHandle.toString(places))
               .append(", rearHandle: ")
               .append(this.rearHandle.toString(places))
               .append(' ').append('}')
               .toString();
      }

      /**
       * Translates this knot by a vector.
       *
       * @param v
       *           the vector
       * @return this knot
       * @see Vec3#add(Vec3, Vec3, Vec3)
       */
      @Chainable
      public Knot3 translate ( final Vec3 v ) {

         Vec3.add(this.coord, v, this.coord);
         Vec3.add(this.foreHandle, v, this.foreHandle);
         Vec3.add(this.rearHandle, v, this.rearHandle);
         return this;
      }
   }

   /**
    * A utility function for setting the handles of knots on
    * straight curve segments. Finds unclamped linear
    * interpolation from origin to destination by a step of 1.0
    * / 3.0 .
    *
    * @param a
    *           the origin
    * @param b
    *           the destination
    * @param target
    *           the target
    * @return the result
    */
   static Vec3 lerp13 (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target ) {

      return target.set(
            0.6666667f * a.x + IUtils.ONE_THIRD * b.x,
            0.6666667f * a.y + IUtils.ONE_THIRD * b.y,
            0.6666667f * a.z + IUtils.ONE_THIRD * b.z);
   }

   /**
    * Creates an arc from a start and stop angle. The arc can
    * be open, transversed by a chord, or pie-shaped.
    *
    * @param startAngle
    *           the start angle
    * @param stopAngle
    *           the stop angle
    * @param radius
    *           the arc radius
    * @param arcMode
    *           the arc mode
    * @param target
    *           the output curve
    * @return the arc
    */
   public static Curve3 arc (
         final float startAngle,
         final float stopAngle,
         final float radius,
         final ArcMode arcMode,
         final Curve3 target ) {

      /*
       * Optimized where possible. See Curve2's arc function for
       * more detailed comments.
       */

      if (Utils.approx(stopAngle - startAngle, IUtils.TAU, 0.00139f)) {
         return Curve3.circle(startAngle, radius, 4, target);
      }

      final float a1 = Utils.mod1(startAngle * IUtils.ONE_TAU);
      final float b1 = Utils.mod1(stopAngle * IUtils.ONE_TAU);
      final float arcLen1 = Utils.mod1(b1 - a1);
      final float destAngle1 = a1 + arcLen1;
      final int knotCount = Utils.ceilToInt(1 + 4 * arcLen1);
      final float toStep = 1.0f / (knotCount - 1.0f);
      final float hndtn = 0.25f * toStep * arcLen1;
      final float cost = SinCos.eval(hndtn);
      final float handleMag = cost == 0.0f ? 0.0f
            : SinCos.eval(hndtn - 0.25f) / cost
                  * radius * IUtils.FOUR_THIRDS;

      final LinkedList < Knot3 > knots = target.knots;
      knots.clear();
      for (int i = 0; i < knotCount; ++i) {
         final float angle1 = Utils.lerpUnclamped(
               a1, destAngle1, i * toStep);
         knots.addLast(
               Knot3.fromPolar(
                     SinCos.eval(angle1),
                     SinCos.eval(angle1 - 0.25f),
                     radius, handleMag,
                     new Knot3()));
      }

      target.closedLoop = arcMode != ArcMode.OPEN;
      if (target.closedLoop) {
         if (arcMode == ArcMode.CHORD) {

            final Knot3 first = knots.getFirst();
            final Knot3 last = knots.getLast();

            Curve3.lerp13(last.coord, first.coord, last.foreHandle);
            Curve3.lerp13(first.coord, last.coord, first.rearHandle);

         } else if (arcMode == ArcMode.PIE) {

            final Knot3 first = knots.getFirst();
            final Knot3 last = knots.getLast();
            final Knot3 center = new Knot3();

            knots.addLast(center);

            final Vec3 coCenter = center.coord;
            Curve3.lerp13(coCenter, last.coord, center.rearHandle);
            Curve3.lerp13(coCenter, first.coord, center.foreHandle);
            Curve3.lerp13(first.coord, coCenter, first.rearHandle);
            Curve3.lerp13(last.coord, coCenter, last.foreHandle);
         }
      }

      target.name = "Arc";
      return target;
   }

   /**
    * Creates a curve which approximates a circle of radius 0.5
    * using four knots.
    *
    * @param target
    *           the output curve
    * @return the circle
    */
   public static Curve3 circle (
         final Curve3 target ) {

      return Curve3.circle(
            0.0f, 0.5f, 4, target);
   }

   /**
    * Creates a curve which approximates a circle of radius 0.5
    * using four knots.
    *
    * @param offsetAngle
    *           the angular offset
    * @param target
    *           the output curve
    * @return the circle
    */
   public static Curve3 circle (
         final float offsetAngle,
         final Curve3 target ) {

      return Curve3.circle(
            offsetAngle, 0.5f, 4, target);
   }

   /**
    * Creates a curve which approximates a circle using four
    * knots.
    *
    * @param offsetAngle
    *           the angular offset
    * @param radius
    *           the radius
    * @param target
    *           the output curve
    * @return the circle
    */
   public static Curve3 circle (
         final float offsetAngle,
         final float radius,
         final Curve3 target ) {

      return Curve3.circle(
            offsetAngle, radius, 4, target);
   }

   /**
    * Creates a curve which approximates a circle.
    *
    * @param offsetAngle
    *           the angular offset
    * @param radius
    *           the radius
    * @param knotCount
    *           the knot count
    * @param target
    *           the output curve
    * @return the circle
    */
   public static Curve3 circle (
         final float offsetAngle,
         final float radius,
         final int knotCount,
         final Curve3 target ) {

      target.clear();
      target.closedLoop = true;

      final float offset1 = offsetAngle * IUtils.ONE_TAU;
      final int vknct = knotCount < 3 ? 3 : knotCount;
      final float invKnCt = 1.0f / vknct;
      final float hndtn = 0.25f * invKnCt;
      final float cost = SinCos.eval(hndtn);
      final float handleMag = cost == 0.0f ? 0.0f
            : SinCos.eval(hndtn - 0.25f) / cost
                  * radius * IUtils.FOUR_THIRDS;

      final LinkedList < Knot3 > knots = target.knots;
      for (int i = 0; i < vknct; ++i) {
         final float angle1 = offset1 + i * invKnCt;
         knots.add(
               Knot3.fromPolar(
                     SinCos.eval(angle1),
                     SinCos.eval(angle1 - 0.25f),
                     radius, handleMag,
                     new Knot3()));
      }

      target.name = "Circle";
      return target;
   }

   /**
    * Creates a curve from a series of points. Smooths the
    * fore- and rear-handles of each knot.
    *
    * @param closedLoop
    *           whether the curve is a closed loop
    * @param points
    *           the array of points
    * @param target
    *           the output curve
    * @return the curve
    */
   public static Curve3 fromPoints (
         final boolean closedLoop,
         final Vec3[] points,
         final Curve3 target ) {

      final int knotCount = points.length;
      target.clear();
      target.closedLoop = closedLoop;
      final LinkedList < Knot3 > knots = target.knots;
      for (int i = 0; i < knotCount; ++i) {
         final Vec3 point = points[i];
         knots.addLast(new Knot3(point, point, point));
      }
      return Curve3.smoothHandles(target);
   }

   /**
    * Creates a regular convex polygon.
    *
    * @param offsetAngle
    *           the offset angle
    * @param radius
    *           the radius
    * @param knotCount
    *           the number of knots
    * @param target
    *           the output curve
    * @return the polygon
    */
   public static Curve3 polygon (
         final float offsetAngle,
         final float radius,
         final int knotCount,
         final Curve3 target ) {

      target.clear();
      target.closedLoop = true;
      final int vknct = knotCount < 3 ? 3 : knotCount;
      final float invKnCt = 1.0f / vknct;
      final float toAngle = IUtils.TAU * invKnCt;
      final LinkedList < Knot3 > knots = target.knots;
      for (int i = 0; i < vknct; ++i) {
         final float angle = offsetAngle + i * toAngle;
         final Knot3 knot = new Knot3();
         Vec3.fromPolar(angle, radius, knot.coord);
         knots.addLast(knot);
      }

      target.name = "Polygon";
      return Curve3.straightenHandles(target);
   }

   /**
    * Creates a random curve. Generates random points, creates
    * a curve from those points, then smooths the knots'
    * handles.
    *
    * @param rng
    *           the random number generator
    * @param count
    *           the number of knots to generate
    * @param lowerBound
    *           the lower bound
    * @param upperBound
    *           the upper bound
    * @param closedLoop
    *           whether the curve is a closed loop
    * @param target
    *           the target curve
    * @return the random curve
    */
   public static Curve3 random (
         final Random rng,
         final int count,
         final float lowerBound,
         final float upperBound,
         final boolean closedLoop,
         final Curve3 target ) {

      final int valCount = count < 3 ? 3 : count;
      final Vec3[] points = new Vec3[valCount];
      for (int i = 0; i < valCount; ++i) {
         points[i] = Vec3.randomCartesian(rng,
               lowerBound, upperBound,
               new Vec3());
      }
      return Curve3.fromPoints(closedLoop, points, target);
   }

   /**
    * Creates a random curve. Generates random points, creates
    * a curve from those points, then smooths the knots'
    * handles.
    *
    * @param rng
    *           the random number generator
    * @param count
    *           the number of knots to generate
    * @param lowerBound
    *           the lower bound
    * @param upperBound
    *           the upper bound
    * @param closedLoop
    *           whether the curve is a closed loop
    * @param target
    *           the target curve
    * @return the random curve
    */
   public static Curve3 random (
         final Random rng,
         final int count,
         final Vec3 lowerBound,
         final Vec3 upperBound,
         final boolean closedLoop,
         final Curve3 target ) {

      final int valCount = count < 3 ? 3 : count;
      final Vec3[] points = new Vec3[valCount];
      for (int i = 0; i < valCount; ++i) {
         points[i] = Vec3.randomCartesian(rng,
               lowerBound, upperBound,
               new Vec3());
      }
      target.closedLoop = closedLoop;

      return Curve3.fromPoints(closedLoop, points, target);
   }

   /**
    * Adjusts knot handles so as to create a smooth, continuous
    * curve.
    *
    * @param target
    *           the output curve
    * @return the curve
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    * @see Vec3#normalize(Vec3, Vec3)
    * @see Vec3#add(Vec3, Vec3, Vec3)
    * @see Vec3#mul(Vec3, float, Vec3)
    * @see Vec3#rescale(Vec3, float, Vec3)
    */
   public static Curve3 smoothHandles ( final Curve3 target ) {

      final LinkedList < Knot3 > knots = target.knots;
      final int knotLength = knots.size();
      if (knotLength < 3) {
         return target;
      }

      // TODO: Can this be optimized to use fewer temp vectors?
      final Vec3 back = new Vec3();
      final Vec3 backNorm = new Vec3();
      final Vec3 backRescale = new Vec3();

      final Vec3 forward = new Vec3();
      final Vec3 forNorm = new Vec3();
      final Vec3 forRescale = new Vec3();

      final Vec3 dir0 = new Vec3();
      final Vec3 dir1 = new Vec3();
      final Vec3 dir2 = new Vec3();

      final boolean closedLoop = target.closedLoop;

      for (int i = 0; i < knotLength; ++i) {
         final Knot3 knot = knots.get(i);
         final Vec3 currCoord = knot.coord;

         float backDist = 0.0f;
         float foreDist = 0.0f;

         if (closedLoop) {

            final int prevIndex = Math.floorMod(i - 1, knotLength);
            final Knot3 prev = knots.get(prevIndex);

            Vec3.sub(prev.coord, currCoord, back);
            backDist = Vec3.mag(back);
            Vec3.normalize(back, backNorm);
            Vec3.add(dir0, backNorm, dir1);

            final int nextIndex = (i + 1) % knotLength;
            final Knot3 next = knots.get(nextIndex);

            Vec3.sub(next.coord, currCoord, forward);
            foreDist = -Vec3.mag(forward);
            Vec3.normalize(forward, forNorm);
            Vec3.sub(dir1, forNorm, dir2);

         } else {

            final int prevIndex = i - 1;
            if (prevIndex > -1) {
               final Knot3 prev = knots.get(prevIndex);

               Vec3.sub(prev.coord, currCoord, back);
               backDist = Vec3.mag(back);
               Vec3.normalize(back, backNorm);
               Vec3.add(dir0, backNorm, dir1);
            }

            final int nextIndex = i + 1;
            if (nextIndex < knotLength) {
               final Knot3 next = knots.get(nextIndex);

               Vec3.sub(next.coord, currCoord, forward);
               foreDist = -Vec3.mag(forward);
               Vec3.normalize(forward, forNorm);
               Vec3.sub(dir1, forNorm, dir2);
            }
         }

         Vec3.rescale(dir2, IUtils.ONE_THIRD, dir0);

         Vec3.mul(dir0, backDist, backRescale);
         Vec3.add(backRescale, currCoord, knot.rearHandle);

         Vec3.mul(dir0, foreDist, forRescale);
         Vec3.add(forRescale, currCoord, knot.foreHandle);
      }

      /*
       * Match fore and rear handles of first and last knots if
       * the curve is not closed.
       */
      if (!closedLoop) {
         knots.getFirst().mirrorHandlesForward();
         knots.getLast().mirrorHandlesBackward();
      }

      return target;
   }

   /**
    * Adjusts knot handles so as to create straight line
    * segments.
    *
    * @param target
    *           the output curve
    * @return the curve
    */
   public static Curve3 straightenHandles ( final Curve3 target ) {

      final LinkedList < Knot3 > knots = target.knots;
      final int knotLength = knots.size();
      if (knotLength < 2) {
         return target;
      }

      if (knotLength == 2) {
         final Knot3 first = knots.getFirst();
         final Knot3 last = knots.getLast();

         Curve3.lerp13(first.coord, last.coord, first.foreHandle);
         first.mirrorHandlesForward();

         Curve3.lerp13(last.coord, first.coord, last.rearHandle);
         last.mirrorHandlesBackward();

         return target;
      }

      final Iterator < Knot3 > itr = knots.iterator();
      Knot3 prev = null;
      Knot3 curr = itr.next();
      while (itr.hasNext()) {
         prev = curr;
         curr = itr.next();
         Curve3.lerp13(prev.coord, curr.coord, prev.foreHandle);
         Curve3.lerp13(curr.coord, prev.coord, curr.rearHandle);
      }

      if (target.closedLoop) {
         final Knot3 first = knots.getFirst();
         final Knot3 last = knots.getLast();
         Curve3.lerp13(first.coord, last.coord, first.rearHandle);
         Curve3.lerp13(last.coord, first.coord, last.foreHandle);
      } else {
         knots.getFirst().mirrorHandlesForward();
         knots.getLast().mirrorHandlesBackward();
      }

      return target;
   }

   /**
    * The list of knots contained by the curve.
    */
   private final LinkedList < Knot3 > knots = new LinkedList <>();

   /**
    * Whether or not the curve is a closed loop.
    */
   public boolean closedLoop = false;

   /**
    * The material associated with this curve in a curve
    * entity.
    */
   public int materialIndex = 0;

   /**
    * Creates a curve with two default knots.
    */
   public Curve3 () {

      super();
      this.reset();
   }

   /**
    * Creates a curve from a collection of knots
    *
    * @param closedLoop
    *           whether or not the curve is closed
    * @param knots
    *           the collection of knots
    */
   public Curve3 (
         final boolean closedLoop,
         final Collection < Knot3 > knots ) {

      super();
      this.closedLoop = closedLoop;
      this.knots.addAll(knots);
   }

   /**
    * Creates a curve from a comma-separated list of knots.
    *
    * @param closedLoop
    *           whether or not the curve is closed
    * @param knots
    *           the list of knots
    */
   public Curve3 (
         final boolean closedLoop,
         final Knot3... knots ) {

      this(closedLoop, Arrays.asList(knots));
   }

   /**
    * Creates a named curve with two default knots.
    *
    * @param name
    *           the name
    */
   public Curve3 ( final String name ) {

      super(name);
      this.reset();
   }

   /**
    * Creates a named curve from a collection of knots
    *
    * @param name
    *           the name
    * @param closedLoop
    *           whether or not the curve is closed
    * @param knots
    *           the collection of knots
    */
   public Curve3 (
         final String name,
         final boolean closedLoop,
         final Collection < Knot3 > knots ) {

      super(name);
      this.closedLoop = closedLoop;
      this.knots.addAll(knots);
   }

   /**
    * Creates a curve from a comma-separated list of knots.
    *
    * @param name
    *           the name
    * @param closedLoop
    *           whether or not the curve is closed
    * @param knots
    *           the list of knots
    */
   public Curve3 (
         final String name,
         final boolean closedLoop,
         final Knot3... knots ) {

      this(name, closedLoop, Arrays.asList(knots));
   }

   /**
    * A helper function. Returns a knot given two knots and a
    * step. Assumes the step has already been vetted, and that
    * the knots are in sequence along the curve. The knot's
    * rear handle is a mirror of the fore handle.
    *
    * @param a
    *           the origin knot
    * @param b
    *           the destination knot
    * @param step
    *           the step
    * @param target
    *           the output knot
    * @return the knot
    */
   protected Knot3 bezierKnot (
         final Knot3 a,
         final Knot3 b,
         final float step,
         final Knot3 target ) {

      Vec3.bezierPoint(
            a.coord, a.foreHandle,
            b.rearHandle, b.coord,
            step, target.coord);

      Vec3.bezierTangent(
            a.coord, a.foreHandle,
            b.rearHandle, b.coord,
            step, target.foreHandle);

      Vec3.negate(
            target.foreHandle,
            target.rearHandle);

      Vec3.add(
            target.coord,
            target.foreHandle,
            target.foreHandle);

      Vec3.add(
            target.coord,
            target.rearHandle,
            target.rearHandle);

      return target;
   }

   /**
    * A helper function for eval. Returns a coordinate given
    * two knots and a step. Assumes the step has already been
    * vetted, and that the knots are in sequence along the
    * curve.
    *
    * @param a
    *           the origin knot
    * @param b
    *           the destination knot
    * @param step
    *           the step
    * @param target
    *           the output coordinate
    * @return the coordinate
    * @see Vec3#bezierPoint(Vec3, Vec3, Vec3, Vec3, float,
    *      Vec3)
    */
   protected Vec3 bezierPoint (
         final Knot3 a,
         final Knot3 b,
         final float step,
         final Vec3 target ) {

      return Vec3.bezierPoint(
            a.coord, a.foreHandle,
            b.rearHandle, b.coord,
            step, target);
   }

   /**
    * A helper function for eval. Returns a tangent given two
    * knots and a step. Assumes the step has already been
    * vetted, and that the knots are in sequence along the
    * curve.
    *
    * @param a
    *           the origin knot
    * @param b
    *           the destination knot
    * @param step
    *           the step
    * @param target
    *           the output tangent
    * @return the tangent
    * @see Vec3#bezierTangent(Vec3, Vec3, Vec3, Vec3, float,
    *      Vec3)
    */
   protected Vec3 bezierTangent (
         final Knot3 a,
         final Knot3 b,
         final float step,
         final Vec3 target ) {

      return Vec3.bezierTangent(
            a.coord, a.foreHandle,
            b.rearHandle, b.coord,
            step, target);
   }

   /**
    * A helper function for eval. Returns a normalized tangent
    * given two knots and a step. Assumes the step has already
    * been vetted, and that the knots are in sequence along the
    * curve.
    *
    * @param a
    *           the origin knot
    * @param b
    *           the destination knot
    * @param step
    *           the step
    * @param target
    *           the output tangent
    * @return the normalized tangent
    * @see Vec3#bezierTanUnit(Vec3, Vec3, Vec3, Vec3, float,
    *      Vec3)
    */
   protected Vec3 bezierTanUnit (
         final Knot3 a,
         final Knot3 b,
         final float step,
         final Vec3 target ) {

      return Vec3.bezierTanUnit(
            a.coord, a.foreHandle,
            b.rearHandle, b.coord,
            step, target);
   }

   /**
    * Clears the list of knots and sets the closedLoop flag to
    * false. Unlike the public reset, this does not add two
    * default knots to the list.
    *
    * @see List#clear()
    */
   protected void clear () {

      this.closedLoop = false;
      this.name = this.hashIdentityString();
      this.knots.clear();
   }

   /**
    * Append a collection of knots to the curve's list of
    * knots. Returns true if the operation was successful.
    *
    * @param knots
    *           the collection of knots
    * @return success
    * @see LinkedList#addAll(Collection)
    */
   public boolean append ( final Collection < ? extends Knot3 > knots ) {

      // TODO: Is it possible to add null knots to the list this
      // way?
      return this.knots.addAll(knots);
   }

   /**
    * Append a knot to the curve's list of knots.
    *
    * @param knot
    *           the knot
    * @return the curve
    * @see LinkedList#add(Object)
    */
   @Chainable
   public Curve3 append ( final Knot3 knot ) {

      if (knot != null) {
         this.knots.addLast(knot);
      }
      return this;
   }

   /**
    * Append an array of knots to the curve's list of knots.
    *
    * @param knots
    *           the array of knots
    * @return this curve.
    */
   @Chainable
   public Curve3 append ( final Knot3... knots ) {

      final int len = knots.length;
      for (int i = 0; i < len; ++i) {
         final Knot3 knot = knots[i];
         if (knot != null) {
            this.knots.addLast(knot);
         }
      }
      return this;
   }

   /**
    * Calculates the approximate length of a curve to a given
    * level of precision.
    *
    * @param precision
    *           the precision
    * @return the length
    * @see Curve3#evalRange(int)
    */
   public float calcLength ( final int precision ) {

      // Is there a way to use distSq instead of dist when
      // summing the distances and then scale sum?
      float sum = 0.0f;
      final Vec3[][] segments = this.evalRange(precision + 1);
      final int len = segments.length;
      for (int i = 1, j = 0; i < len; ++i, ++j) {
         sum += Vec3.dist(
               segments[j][0],
               segments[i][0]);
      }

      return sum;
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

      final Curve3 other = (Curve3) obj;

      if (this.closedLoop != other.closedLoop) {
         return false;
      }

      if (this.knots == null) {
         if (other.knots != null) {
            return false;
         }
      } else if (!this.knots.equals(other.knots)) {
         return false;
      }

      return true;
   }

   /**
    * Evaluates a step in the range [0.0, 1.0], returning a
    * knot on the curve. The knot's fore handle and rear handle
    * are mirrored.
    *
    * @param step
    *           the step
    * @param target
    *           the output knot
    * @return the knot
    */
   public Knot3 eval (
         final float step,
         final Knot3 target ) {

      final int knotLength = this.knots.size();

      float tScaled = 0.0f;
      int i = 0;
      Knot3 a = null;
      Knot3 b = null;
      if (this.closedLoop) {
         tScaled = knotLength * Utils.mod1(step);
         i = (int) tScaled;
         a = this.knots.get(i);
         b = this.knots.get((i + 1) % knotLength);
      } else {
         if (knotLength == 1 || step <= 0.0f) {
            return target.set(this.getFirst());
         }
         if (step >= 1.0f) {
            return target.set(this.getLast());
         }

         tScaled = step * (knotLength - 1);
         i = (int) tScaled;
         a = this.knots.get(i);
         b = this.knots.get(i + 1);
      }

      return this.bezierKnot(a, b, tScaled - i, target);
   }

   /**
    * Evaluates a step in the range [0.0, 1.0], returning a
    * coordinate on the curve and a tangent. The tangent will
    * be normalized, to be of unit length.
    *
    * @param step
    *           the step
    * @param coord
    *           the output coordinate
    * @param tangent
    *           the output tangent
    * @return the coordinate
    * @see Vec3#bezierPoint(Vec3, Vec3, Vec3, Vec3, float,
    *      Vec3)
    * @see Vec3#bezierTanUnit(Vec3, Vec3, Vec3, Vec3, float,
    *      Vec3)
    */
   public Vec3 eval (
         final float step,
         final Vec3 coord,
         final Vec3 tangent ) {

      final int knotLength = this.knots.size();

      float tScaled = 0.0f;
      int i = 0;
      Knot3 a = null;
      Knot3 b = null;
      if (this.closedLoop) {
         tScaled = knotLength * Utils.mod1(step);
         i = (int) tScaled;
         a = this.knots.get(i);
         b = this.knots.get((i + 1) % knotLength);
      } else {
         if (knotLength == 1 || step <= 0.0f) {
            return this.evalFirst(coord, tangent);
         }
         if (step >= 1.0f) {
            return this.evalLast(coord, tangent);
         }

         tScaled = step * (knotLength - 1);
         i = (int) tScaled;
         a = this.knots.get(i);
         b = this.knots.get(i + 1);
      }

      final float t = tScaled - i;

      this.bezierPoint(a, b, t, coord);
      this.bezierTanUnit(a, b, t, tangent);

      return coord;
   }

   /**
    * Evaluates the first knot in the curve. The tangent will
    * be normalized, to be of unit length.
    *
    * @param coord
    *           the output coordinate
    * @param tangent
    *           the output tangent
    * @return the coordinate
    * @see Vec3#subNorm(Vec3, Vec3, Vec3)
    */
   public Vec3 evalFirst (
         final Vec3 coord,
         final Vec3 tangent ) {

      final Knot3 kFirst = this.knots.getFirst();
      coord.set(kFirst.coord);
      Vec3.subNorm(kFirst.foreHandle, coord, tangent);
      return coord;
   }

   /**
    * Evaluates the last knot in the curve. The tangent will be
    * normalized, to be of unit length.
    *
    * @param coord
    *           the output coordinate
    * @param tangent
    *           the output tangent
    * @return the coordinate
    * @see Vec3#subNorm(Vec3, Vec3, Vec3)
    */
   public Vec3 evalLast (
         final Vec3 coord,
         final Vec3 tangent ) {

      final Knot3 kLast = this.knots.getLast();
      coord.set(kLast.coord);
      Vec3.subNorm(coord, kLast.rearHandle, tangent);

      return coord;
   }

   /**
    * Evaluates an array of vectors given a supplied length.
    * The array is two-dimensional, where the first element of
    * the minor dimension is the coordinate and the second is
    * the tangent.
    *
    * @param count
    *           the count
    * @return the array
    */
   public Vec3[][] evalRange ( final int count ) {

      final int vcount = count < 3 ? 3 : count;
      final Vec3[][] result = new Vec3[vcount][2];
      final int last = this.closedLoop ? vcount : vcount - 1;
      final float toPercent = 1.0f / last;
      Vec3 coord = null;
      Vec3 tangent = null;
      for (int i = 0; i < vcount; ++i) {
         coord = result[i][0] = new Vec3();
         tangent = result[i][1] = new Vec3();
         this.eval(i * toPercent, coord, tangent);
      }
      return result;
   }

   /**
    * Gets a knot from the curve by an index. When the curve is
    * a closed loop, the index wraps around.
    *
    * @param i
    *           the index
    * @return the knot
    * @see LinkedList#get(int)
    * @see Utils#mod(int, int)
    */
   public Knot3 get ( final int i ) {

      final int j = this.closedLoop ? Utils.mod(i, this.knots.size()) : i;
      return this.knots.get(j);
   }

   /**
    * Gets the first knot in the curve.
    *
    * @return the knot
    * @see LinkedList#getFirst()
    */
   public Knot3 getFirst () {

      return this.knots.getFirst();
   }

   /**
    * Gets the last knot in the curve.
    *
    * @return the knot
    * @see LinkedList#getLast()
    */
   public Knot3 getLast () {

      return this.knots.getLast();
   }

   @Override
   public int hashCode () {

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL ^ (this.closedLoop ? 1231 : 1237);
      hash = hash * IUtils.HASH_MUL
            ^ (this.knots == null ? 0 : this.knots.hashCode());
      return hash;
   }

   /**
    * Inserts a knot at a given index. When the curve is a
    * closed loop, the index wraps around; this means negative
    * indices are accepted.
    *
    * @param i
    *           the index
    * @param knot
    *           the knot
    * @return the curve
    */
   public Curve3 insert ( final int i, final Knot3 knot ) {

      if (knot != null) {
         final int j = this.closedLoop ? Utils.mod(i, this.knots.size()) : i;
         this.knots.add(j, knot);
      }
      return this;
   }

   /**
    * Returns an iterator, which allows an enhanced for-loop to
    * access the knots in a curve.
    *
    * @return the iterator
    * @see LinkedList#iterator()
    */
   @Override
   public Iterator < Curve3.Knot3 > iterator () {

      return this.knots.iterator();
   }

   /**
    * Gets the number of knots in the curve.
    *
    * @return the knot count
    * @see LinkedList#size()
    */
   @Override
   public int length () {

      return this.knots.size();
   }

   /**
    * Returns and removes the first knot in the curve.
    *
    * @return the knot
    * @see LinkedList#removeFirst()
    */
   public Knot3 removeFirst () {

      return this.knots.removeFirst();
   }

   /**
    * Removes and returns the last knot in the curve.
    *
    * @return the knot
    */
   public Knot3 removeLast () {

      return this.knots.removeLast();
   }

   /**
    * Resets the curve, leaving two default knots.
    *
    * @return this curve
    */
   @Chainable
   public Curve3 reset () {

      this.knots.clear();
      this.knots.addLast(
            new Knot3(
                  -0.5f, 0.0f, 0.0f,
                  -0.25f, 0.25f, 0.0f,
                  -0.75f, -0.25f, 0.0f));
      this.knots.addLast(
            new Knot3(
                  0.5f, 0.0f, 0.0f,
                  1.0f, 0.0f, 0.0f,
                  0.0f, 0.0f, 0.0f));

      this.closedLoop = false;
      this.name = this.hashIdentityString();
      return this;
   }

   /**
    * Reverses the curve. This is done by reversing the list of
    * knots and swapping the fore- and rear-handle of each
    * knot.
    *
    * @return this curve
    */
   @Chainable
   public Curve3 reverse () {

      Collections.reverse(this.knots);

      final Iterator < Knot3 > itr = this.knots.iterator();
      while (itr.hasNext()) {
         itr.next().reverse();
      }
      return this;
   }

   /**
    * Rotates all knots in the curve by an angle in radians
    * around an axis.
    *
    * @param radians
    *           the angle
    * @param axis
    *           the axis
    * @return this curve
    * @see Knot3#rotate(float, Vec3)
    */
   @Chainable
   public Curve3 rotate ( final float radians, final Vec3 axis ) {

      final float nrm = IUtils.ONE_TAU * radians;
      final float cosa = SinCos.eval(nrm);
      final float sina = SinCos.eval(nrm - 0.25f);

      final Iterator < Knot3 > itr = this.knots.iterator();
      while (itr.hasNext()) {
         itr.next().rotate(cosa, sina, axis);
      }
      return this;
   }

   /**
    * Rotates all knots in the curve by an angle in radians
    * around the x axis.
    *
    * @param radians
    *           the angle
    * @return this curve
    * @see Knot3#rotateX(float)
    */
   @Chainable
   public Curve3 rotateX ( final float radians ) {

      final float nrm = IUtils.ONE_TAU * radians;
      final float cosa = SinCos.eval(nrm);
      final float sina = SinCos.eval(nrm - 0.25f);

      final Iterator < Knot3 > itr = this.knots.iterator();
      while (itr.hasNext()) {
         itr.next().rotateX(cosa, sina);
      }
      return this;
   }

   /**
    * Rotates all knots in the curve by an angle in radians
    * around the y axis.
    *
    * @param radians
    *           the angle
    * @return this curve
    * @see Knot3#rotateY(float)
    */
   @Chainable
   public Curve3 rotateY ( final float radians ) {

      final float nrm = IUtils.ONE_TAU * radians;
      final float cosa = SinCos.eval(nrm);
      final float sina = SinCos.eval(nrm - 0.25f);

      final Iterator < Knot3 > itr = this.knots.iterator();
      while (itr.hasNext()) {
         itr.next().rotateY(cosa, sina);
      }
      return this;
   }

   /**
    * Rotates all knots in the curve by an angle in radians
    * around the z axis.
    *
    * @param radians
    *           the angle
    * @return this curve
    * @see Knot3#rotateZ(float)
    */
   @Chainable
   public Curve3 rotateZ ( final float radians ) {

      final float nrm = IUtils.ONE_TAU * radians;
      final float cosa = SinCos.eval(nrm);
      final float sina = SinCos.eval(nrm - 0.25f);

      final Iterator < Knot3 > itr = this.knots.iterator();
      while (itr.hasNext()) {
         itr.next().rotateZ(cosa, sina);
      }
      return this;
   }

   /**
    * Scales all knots in the curve by a scalar.
    *
    * @param scale
    *           the scale
    * @return this curve
    * @see Knot3#scale(float)
    */
   @Chainable
   public Curve3 scale ( final float scale ) {

      final Iterator < Knot3 > itr = this.knots.iterator();
      while (itr.hasNext()) {
         itr.next().scale(scale);
      }
      return this;
   }

   /**
    * Scales all knots in the curve by a vector.
    *
    * @param scale
    *           the scale
    * @return this curve
    * @see Knot3#scale(Vec3)
    */
   @Chainable
   public Curve3 scale ( final Vec3 scale ) {

      final Iterator < Knot3 > itr = this.knots.iterator();
      while (itr.hasNext()) {
         itr.next().scale(scale);
      }
      return this;
   }

   /**
    * Returns a 3D array representation of this curve.
    *
    * @return the array
    */
   public float[][][] toArray () {

      final float[][][] result = new float[this.knots.size()][][];
      final Iterator < Knot3 > itr = this.knots.iterator();
      int index = 0;
      while (itr.hasNext()) {
         result[index++] = itr.next().toArray();
      }
      return result;
   }

   /**
    * Writes a Wavefront OBJ file format string by converting
    * this curve to line segments. The points will not be
    * evenly distributed along the curve.
    *
    * @param precision
    *           the precision
    * @return the string
    */
   @Experimental
   public String toObjString ( final int precision ) {

      // TODO: Create a toPolyLine function based on parameterized
      // curve which provide a better result for this function.

      final StringBuilder result = new StringBuilder();
      final Vec3[][] segments = this.evalRange(precision);
      final int len = segments.length;

      result.append("# v: ")
            .append(len)
            .append('\n')
            .append('\n');

      result.append('o')
            .append(' ')
            .append(this.name)
            .append('\n')
            .append('\n');

      for (int i = 0; i < len; ++i) {
         final Vec3 coord = segments[i][0];
         result.append('v')
               .append(' ')
               .append(coord.toObjString())
               .append('\n');
      }

      /*
       * Create a line linking the prior segment to the next.
       * Indices in an .obj file start at 1, not 0.
       */
      for (int i = 1, j = 2; i < len; ++i, ++j) {
         result.append('l')
               .append(' ')
               .append(i)
               .append(' ')
               .append(j)
               .append('\n');
      }

      if (this.closedLoop) {
         result.append('l')
               .append(' ')
               .append(len)
               .append(' ')
               .append(1)
               .append('\n');
      }

      return result.toString();
   }

   /**
    * Returns a string representation of the curve.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   /**
    * Returns a string representation of the curve.
    *
    * @param places
    *           the number of places
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder()
            .append("{ closedLoop: ")
            .append(this.closedLoop)
            .append(", \n  knots: [ \n");

      final Iterator < Knot3 > itr = this.knots.iterator();
      while (itr.hasNext()) {
         sb.append(itr.next().toString(places));
         if (itr.hasNext()) {
            sb.append(',').append('\n');
         }
      }

      sb.append(" ] }");
      return sb.toString();
   }

   /**
    * Translates all knots in the curve by a vector.
    *
    * @param v
    *           the vector
    * @return this curve
    * @see Knot3#translate(Vec3)
    */
   @Chainable
   public Curve3 translate ( final Vec3 v ) {

      final Iterator < Knot3 > itr = this.knots.iterator();
      while (itr.hasNext()) {
         itr.next().translate(v);
      }

      return this;
   }
}
