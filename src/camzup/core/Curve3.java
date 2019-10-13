package camzup.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
       * The knot's default string format.
       */
      public static String DEFAULT_STRING_FORMAT = new StringBuilder()
            .append("{ coord:      %s, %n")
            .append("  foreHandle: %s, %n")
            .append("  rearHandle: %s }")
            .toString();

      /**
       * The knot's string format.
       */
      private static String STRING_FORMAT = "";

      static {
         Knot3.STRING_FORMAT = Knot3.DEFAULT_STRING_FORMAT;
      }

      /**
       * Creates a knot from polar coordinates, where the knot's
       * forehandle is tangent to the radius.
       *
       * @param angle
       *           the angle in radians
       * @param radius
       *           the radius
       * @param handleMag
       *           the magnitude of the handles
       * @param target
       *           the output knot
       * @param temp0
       *           temporary vector 1
       * @param temp1
       *           temporary vector 2
       * @return the knot
       * @see Vec3#mult(Vec3, float, Vec3)
       * @see Vec3#negate(Vec3, Vec3)
       * @see Vec3#add(Vec3, Vec3, Vec3)
       */
      public static Knot3 fromPolar (
            final float angle,
            final float radius,
            final float handleMag,
            final Knot3 target,
            final Vec3 temp0,
            final Vec3 temp1 ) {

         Vec3.fromPolar(angle, 1.0f, temp0);

         // Use perpendicular CCW.
         temp1.set(-temp0.y, temp0.x, 0.0f);
         Vec3.mult(temp0, radius, target.coord);
         Vec3.mult(temp1, handleMag, temp0);
         Vec3.negate(temp0, temp1);
         Vec3.add(temp0, target.coord, target.foreHandle);
         Vec3.add(temp1, target.coord, target.rearHandle);

         return target;
      }

      /**
       * Gets the format for the string representation of this
       * knot.
       *
       * @return the format
       */
      public static String getStringFormat () {

         return Knot3.STRING_FORMAT;
      }

      /**
       * Sets the format for the string representation of the
       * knot.
       *
       * @param stringFormat
       *           the format
       */
      public static void setStringFormat ( final String stringFormat ) {

         if (stringFormat != null) {
            Knot3.STRING_FORMAT = stringFormat;
         }
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
       * Aligns this knot's handles in the same direction while
       * preserving their magnitude.
       *
       * @param foreDir
       *           a temporary vector
       * @param rearDir
       *           a temporary vector
       * @param foreScaled
       *           a temporary vector
       * @return this knot
       */
      @Chainable
      public Knot3 alignHandles (
            final Vec3 foreDir,
            final Vec3 rearDir,
            final Vec3 foreScaled ) {

         return this.alignHandlesForward(
               foreDir,
               rearDir,
               foreScaled);
      }

      /**
       * Aligns this knot's fore handle to its rear handle while
       * preserving magnitude.
       *
       * @param foreDir
       *           a temporary vector
       * @param rearDir
       *           a temporary vector
       * @param foreScaled
       *           a temporary vector
       * @return this knot
       */
      @Chainable
      public Knot3 alignHandlesBackward (
            final Vec3 foreDir,
            final Vec3 rearDir,
            final Vec3 foreScaled ) {

         Vec3.sub(this.rearHandle, this.coord, rearDir);
         Vec3.sub(this.foreHandle, this.coord, foreDir);
         Vec3.rescale(rearDir, -Vec3.mag(foreDir), foreScaled);
         Vec3.add(foreScaled, this.coord, this.foreHandle);
         return this;
      }

      /**
       * Aligns this knot's rear handle to its fore handle while
       * preserving magnitude.
       *
       * @param rearDir
       *           a temporary vector
       * @param foreDir
       *           a temporary vector
       * @param rearScaled
       *           a temporary vector
       * @return this knot
       */
      @Chainable
      public Knot3 alignHandlesForward (
            final Vec3 rearDir,
            final Vec3 foreDir,
            final Vec3 rearScaled ) {

         Vec3.sub(this.rearHandle, this.coord, rearDir);
         Vec3.sub(this.foreHandle, this.coord, foreDir);
         Vec3.rescale(foreDir, -Vec3.mag(rearDir), rearScaled);
         Vec3.add(rearScaled, this.coord, this.rearHandle);
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

         final int prime = 31;
         int result = 1;
         result = prime * result
               + (this.coord == null ? 0 : this.coord.hashCode());
         result = prime * result
               + (this.foreHandle == null ? 0 : this.foreHandle.hashCode());
         result = prime * result
               + (this.rearHandle == null ? 0 : this.rearHandle.hashCode());
         return result;
      }

      /**
       * Mirrors this knot's handles. Defaults to mirroring in the
       * forward direction.
       *
       * @return this knot
       */
      @Chainable
      public Knot3 mirrorHandles () {

         return this.mirrorHandlesForward();
      }

      /**
       * Mirrors this knot's handles. Defaults to mirroring in the
       * forward direction.
       *
       * @param temp
       *           a temporary vector
       * @return this knot
       */
      @Chainable
      public Knot3 mirrorHandles ( final Vec3 temp ) {

         return this.mirrorHandlesForward(temp);
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
       * Sets the forward-facing handle to mirror the rear-facing
       * handle: the fore will have the same magnitude and negated
       * direction of the rear. A temporary vector is required for
       * the swap.
       *
       * @param rearDir
       *           a temp vector
       * @return this knot
       */
      @Chainable
      public Knot3 mirrorHandlesBackward ( final Vec3 rearDir ) {

         Vec3.sub(this.rearHandle, this.coord, rearDir);
         Vec3.sub(this.coord, rearDir, this.foreHandle);
         return this;
      }

      /**
       * Sets the rear-facing handle to mirror the forward-facing
       * handle: the rear will have the same magnitude and negated
       * direction of the fore.
       *
       * @return this knot
       */
      public Knot3 mirrorHandlesForward () {

         this.rearHandle.set(
               this.coord.x - (this.foreHandle.x - this.coord.x),
               this.coord.y - (this.foreHandle.y - this.coord.y),
               this.coord.z - (this.foreHandle.z - this.coord.z));
         return this;
      }

      /**
       * Sets the rear-facing handle to mirror the forward-facing
       * handle: the rear will have the same magnitude and negated
       * direction of the fore. A temporary vector is required for
       * the swap.
       *
       * @param foreDir
       *           a temp vector
       * @return this knot
       */
      @Chainable
      public Knot3 mirrorHandlesForward ( final Vec3 foreDir ) {

         Vec3.sub(this.foreHandle, this.coord, foreDir);
         Vec3.sub(this.coord, foreDir, this.rearHandle);
         return this;
      }

      /**
       * Reverses the knot's direction by swapping the fore- and
       * rear-handles.
       *
       * @param temp
       *           a temporary vector
       * @return this knot
       */
      @Chainable
      public Knot3 reverse ( final Vec3 temp ) {

         temp.set(this.foreHandle);
         this.foreHandle.set(this.rearHandle);
         this.rearHandle.set(temp);
         return this;
      }

      /**
       * Rotates this knot around an arbitrary axis by an angle in
       * radians.
       *
       * @param radians
       *           the angle
       * @param axis
       *           the axis
       * @return this knot
       */
      @Chainable
      public Knot3 rotate ( final float radians, final Vec3 axis ) {

         Vec3.rotate(this.coord, radians, axis, this.coord);
         Vec3.rotate(this.foreHandle, radians, axis, this.foreHandle);
         Vec3.rotate(this.rearHandle, radians, axis, this.rearHandle);
         return this;
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

         Vec3.rotateX(this.coord, radians, this.coord);
         Vec3.rotateX(this.foreHandle, radians, this.foreHandle);
         Vec3.rotateX(this.rearHandle, radians, this.rearHandle);
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

         Vec3.rotateY(this.coord, radians, this.coord);
         Vec3.rotateY(this.foreHandle, radians, this.foreHandle);
         Vec3.rotateY(this.rearHandle, radians, this.rearHandle);
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

         Vec3.rotateZ(this.coord, radians, this.coord);
         Vec3.rotateZ(this.foreHandle, radians, this.foreHandle);
         Vec3.rotateZ(this.rearHandle, radians, this.rearHandle);
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

         Vec3.mult(this.coord, scale, this.coord);
         Vec3.mult(this.foreHandle, scale, this.foreHandle);
         Vec3.mult(this.rearHandle, scale, this.rearHandle);
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

         Vec3.mult(this.coord, scale, this.coord);
         Vec3.mult(this.foreHandle, scale, this.foreHandle);
         Vec3.mult(this.rearHandle, scale, this.rearHandle);
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
         Vec3.mult(temp0, scalar, temp1);
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
         Vec3.mult(temp0, magnitude, temp1);
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
       */
      @Chainable
      public Knot3 scaleRearHandleBy (
            final float scalar,
            final Vec3 temp0,
            final Vec3 temp1 ) {

         Vec3.sub(this.rearHandle, this.coord, temp0);
         Vec3.mult(temp0, scalar, temp1);
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
       */
      @Chainable
      public Knot3 scaleRearHandleTo (
            final float magnitude,
            final Vec3 temp0,
            final Vec3 temp1 ) {

         Vec3.subNorm(this.rearHandle, this.coord, temp0);
         Vec3.mult(temp0, magnitude, temp1);
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
       */
      @Chainable
      public Knot3 set (
            final float xCoord,
            final float yCoord,
            final float zCoord ) {

         return this.set(
               xCoord, yCoord, zCoord,

               xCoord + Utils.EPSILON,
               yCoord + Utils.EPSILON,
               zCoord + Utils.EPSILON,

               xCoord - Utils.EPSILON,
               yCoord - Utils.EPSILON,
               zCoord - Utils.EPSILON);
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
       * Returns a string representation of this knot.
       *
       * @return the string
       */
      @Override
      public String toString () {

         // return String.format(
         // Knot3.STRING_FORMAT,
         // this.coord.toString(),
         // this.foreHandle.toString(),
         // this.rearHandle.toString());

         return this.toString(4);
      }

      public String toString ( final int places ) {

         return new StringBuilder()
               .append("{ coord: ")
               .append(this.coord.toString(places))
               .append("{, foreHandle: ")
               .append(this.foreHandle.toString(places))
               .append("{, rearHandle: ")
               .append(this.rearHandle.toString(places))
               .append(" }")
               .toString();
      }

      /**
       * Translates this knot by a vector.
       *
       * @param v
       *           the vector
       * @return this knot
       */
      @Chainable
      public Knot3 translate ( final Vec3 v ) {

         Vec3.add(this.coord, v, this.coord);
         Vec3.add(this.foreHandle, v, this.foreHandle);
         Vec3.add(this.rearHandle, v, this.rearHandle);
         return this;
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
   }

   /**
    * Creates a curve which approximates a circle of radius 0.5
    * using four knots.
    *
    * @param target
    *           the output curve
    * @param temp0
    *           a temporary vector
    * @param temp1
    *           a temporary vector
    * @return the circle
    */
   public static Curve3 circle (
         final Curve3 target,
         final Vec3 temp0,
         final Vec3 temp1 ) {

      return Curve3.circle(
            0.0f, 0.5f, 4,
            target,
            temp0, temp1);
   }

   /**
    * Creates a curve which approximates a circle of radius 0.5
    * using four knots.
    *
    * @param offsetAngle
    *           the angular offset
    * @param target
    *           the output curve
    * @param temp0
    *           a temporary vector
    * @param temp1
    *           a temporary vector
    * @return the circle
    */
   public static Curve3 circle (
         final float offsetAngle,
         final Curve3 target,
         final Vec3 temp0,
         final Vec3 temp1 ) {

      return Curve3.circle(
            offsetAngle, 0.5f, 4,
            target,
            temp0, temp1);
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
    * @param temp0
    *           a temporary vector
    * @param temp1
    *           a temporary vector
    * @return the circle
    */
   public static Curve3 circle (
         final float offsetAngle,
         final float radius,
         final Curve3 target,
         final Vec3 temp0,
         final Vec3 temp1 ) {

      return Curve3.circle(
            offsetAngle, radius, 4,
            target,
            temp0, temp1);
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
    * @param temp0
    *           a temporary vector
    * @param temp1
    *           a temporary vector
    * @return the circle
    */
   public static Curve3 circle (
         final float offsetAngle,
         final float radius,
         final int knotCount,
         final Curve3 target,
         final Vec3 temp0,
         final Vec3 temp1 ) {

      target.clear();
      target.closedLoop = true;
      final int vknct = knotCount < 3 ? 3 : knotCount;
      final float invKnCt = 1.0f / vknct;
      final float toAngle = IUtils.TAU * invKnCt;
      final float handleMag = radius * (float) (IUtils.FOUR_THIRDS_D
            * Math.tan(IUtils.HALF_PI_D * invKnCt));

      for (int i = 0; i < knotCount; ++i) {
         final float angle = offsetAngle + i * toAngle;
         final Knot3 knot = Knot3.fromPolar(
               angle, radius, handleMag,
               new Knot3(),
               temp0, temp1);
         target.append(knot);
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
      for (int i = 0; i < knotCount; ++i) {
         final Vec3 point = points[i];
         final Knot3 knot = new Knot3(point, point, point);
         target.append(knot);
      }
      return Curve3.smoothHandles(target);
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
    * @see Vec3#mult(Vec3, float, Vec3)
    * @see Vec3#rescale(Vec3, float, Vec3)
    */
   public static Curve3 smoothHandles ( final Curve3 target ) {

      final List < Knot3 > knots = target.knots;
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

            final int prevIndex = Utils.mod(i - 1, knotLength);
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
            if (prevIndex >= 0) {
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

         Vec3.mult(dir0, backDist, backRescale);
         Vec3.add(backRescale, currCoord, knot.rearHandle);

         Vec3.mult(dir0, foreDist, forRescale);
         Vec3.add(forRescale, currCoord, knot.foreHandle);
      }

      /*
       * Match fore and rear handles of first and last knots if
       * the curve is not closed.
       */
      if (!closedLoop) {
         final Knot3 first = target.knots.getFirst();
         first.mirrorHandlesForward();
         final Knot3 last = target.knots.getLast();
         last.mirrorHandlesBackward();
      }

      return target;
   }

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
    * The list of knots contained by the curve.
    */
   private final LinkedList < Knot3 > knots = new LinkedList <>();

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
    * Append a collection of knots to the curve's list of
    * knots. Returns true if the operation was successful.
    *
    * @param knots
    *           the collection of knots
    * @return success
    * @see List#addAll(Collection)
    */
   public boolean append ( final Collection < ? extends Knot3 > knots ) {

      return this.knots.addAll(knots);
   }

   /**
    * Append a knot to the curve's list of knots.
    *
    * @param knot
    *           the knot
    * @return the curve
    * @see List#add(Object)
    */
   @Chainable
   public Curve3 append ( final Knot3 knot ) {

      this.knots.add(knot);
      return this;
   }

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
    * Gets a knot from the curve by an index. When the curve is
    * a closed loop, the index wraps around.
    *
    * @param i
    *           the index
    * @return the knot
    * @see List#get(int)
    * @see Utils#mod(int, int)
    */
   public Knot3 get ( final int i ) {

      final int index = this.closedLoop ? Utils.mod(i, this.knots.size()) : i;
      return this.knots.get(index);
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

   /**
    * Returns an iterator, which allows an enhanced for-loop to
    * access the knots in a curve.
    *
    * @return the iterator
    * @see List#iterator()
    */
   @Override
   public Iterator < Curve3.Knot3 > iterator () {

      return this.knots.iterator();
   }

   /**
    * The number of knots in the curve.
    *
    * @return the size
    * @see List#size()
    */
   public int knotCount () {

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

      final Knot3 knot0 = new Knot3(
            -0.5f, 0.0f, 0.0f,
            -0.25f, 0.25f, 0.0f,
            -0.75f, -0.25f, 0.0f);

      final Knot3 knot1 = new Knot3(
            0.5f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f);

      this.knots.add(knot0);
      this.knots.add(knot1);

      this.closedLoop = false;

      return this;
   }

   /**
    * Reverses the curve. This is done by reversing the list of
    * knots and swapping the fore- and rear-handle of each
    * knot.
    *
    * @param temp
    *           a temporary vector
    * @return this curve
    * @see Collections#reverse(List)
    * @see Knot3#reverse(Vec3)
    */
   @Chainable
   public Curve3 reverse ( final Vec3 temp ) {

      Collections.reverse(this.knots);
      for (final Knot3 knot : this.knots) {
         knot.reverse(temp);
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

      for (final Knot3 knot : this.knots) {
         knot.rotate(radians, axis);
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

      for (final Knot3 knot : this.knots) {
         knot.rotateX(radians);
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

      for (final Knot3 knot : this.knots) {
         knot.rotateY(radians);
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

      for (final Knot3 knot : this.knots) {
         knot.rotateZ(radians);
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

      for (final Knot3 knot : this.knots) {
         knot.scale(scale);
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

      for (final Knot3 knot : this.knots) {
         knot.scale(scale);
      }
      return this;
   }

   /**
    * Sorts the curve's list of knots according to the default
    * knot comparator.
    *
    * @return this curve
    * @see Collections#sort(List)
    */
   @Chainable
   public Curve3 sort () {

      Collections.sort(this.knots);
      return this;
   }

   /**
    * Sorts the curve's list of knots according to a
    * comparator.
    *
    * @param comparator
    *           the comparator
    * @return this curve
    * @see List#sort(Comparator)
    */
   @Chainable
   public Curve3 sort ( final Comparator < Knot3 > comparator ) {

      this.knots.sort(comparator);
      return this;
   }

   /**
    * Returns a string representation of the curve.
    *
    * @return the string
    */
   @Override
   public String toString () {

      final StringBuilder sb = new StringBuilder();
      sb.append("{ closedLoop: ");
      sb.append(this.closedLoop);
      sb.append(", \n  knots: [ \n");

      for (final Iterator < Curve3.Knot3 > itr = this.knots.iterator(); itr
            .hasNext();) {
         sb.append(itr.next());
         if (itr.hasNext()) {
            sb.append(", \n");
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

      for (final Knot3 knot : this.knots) {
         knot.translate(v);
      }
      return this;
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
      this.knots.clear();
   }
}
