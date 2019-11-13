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
public class Curve2 extends Curve
      implements Iterable < Curve2.Knot2 > {

   /**
    * Groups together vectors which shape a Bezier curve into a
    * coordinate (or anchor point), fore handle (the following
    * control point) and rear handle (the preceding control
    * point).
    */
   public static class Knot2 implements Comparable < Knot2 > {

      /**
       * Creates a knot from polar coordinates, where the knot's
       * fore handle is tangent to the radius.
       *
       * @param angle
       *           the angle in radians
       * @param radius
       *           the radius
       * @param handleMag
       *           the magnitude of the handles
       * @param target
       *           the output knot
       * @return the knot
       */
      public static Knot2 fromPolar (
            final float angle,
            final float radius,
            final float handleMag,
            final Knot2 target ) {

         final float cosa = (float) Math.cos(angle);
         final float sina = (float) Math.sin(angle);

         final Vec2 coord = target.coord;
         coord.set(
               cosa * radius,
               sina * radius);

         final float hmsina = sina * handleMag;
         final float hmcosa = cosa * handleMag;

         target.foreHandle.set(
               coord.x - hmsina,
               coord.y + hmcosa);
         target.rearHandle.set(
               coord.x + hmsina,
               coord.y - hmcosa);

         return target;
      }

      /**
       * The spatial coordinate of the knot.
       */
      public final Vec2 coord = new Vec2();

      /**
       * The handle which warps the curve segment heading away
       * from the knot along the direction of the curve.
       */
      public final Vec2 foreHandle = new Vec2();

      /**
       * The handle which warps the curve segment heading towards
       * the knot along the direction of the curve.
       */
      public final Vec2 rearHandle = new Vec2();

      /**
       * The default constructor.
       */
      public Knot2 () {

      }

      /**
       * Creates a knot from a coordinate.
       *
       * @param xCoord
       *           the x coordinate
       * @param yCoord
       *           the y coordinate
       */
      public Knot2 (
            final float xCoord,
            final float yCoord ) {

         this.set(xCoord, yCoord);
      }

      /**
       * Creates a knot from real numbers.
       *
       * @param xCoord
       *           the x coordinate
       * @param yCoord
       *           the y coordinate
       * @param xFore
       *           the fore handle x
       * @param yFore
       *           the fore handle y
       */
      public Knot2 (
            final float xCoord,
            final float yCoord,
            final float xFore,
            final float yFore ) {

         this.set(
               xCoord, yCoord,
               xFore, yFore);
      }

      /**
       * Creates a knot from real numbers.
       *
       * @param xCoord
       *           the x coordinate
       * @param yCoord
       *           the y coordinate
       * @param xFore
       *           the fore handle x
       * @param yFore
       *           the fore handle y
       * @param xRear
       *           the rear handle x
       * @param yRear
       *           the rear handle y
       */
      public Knot2 (
            final float xCoord,
            final float yCoord,
            final float xFore,
            final float yFore,
            final float xRear,
            final float yRear ) {

         this.set(
               xCoord, yCoord,
               xFore, yFore,
               xRear, yRear);
      }

      /**
       * Creates a knot from a source knot.
       *
       * @param source
       *           the source
       */
      public Knot2 ( final Knot2 source ) {

         this.set(source);
      }

      /**
       * Attempts to create a knot from Strings.
       *
       * @param xCoord
       *           the x coordinate string
       * @param yCoord
       *           the y coordinate string
       * @param xFore
       *           the x fore handle string
       * @param yFore
       *           the y fore handle string
       * @param xRear
       *           the x rear handle string
       * @param yRear
       *           the y rear handle string
       */
      public Knot2 (
            final String xCoord,
            final String yCoord,
            final String xFore,
            final String yFore,
            final String xRear,
            final String yRear ) {

         this.set(xCoord, yCoord, xFore, yFore, xRear, yRear);
      }

      /**
       * Creates a knot from a coordinate.
       *
       * @param coord
       *           the coordinate
       */
      public Knot2 ( final Vec2 coord ) {

         this.set(coord);
      }

      /**
       * Creates a knot from a coordinate and fore handle. The
       * rear handle is a mirror of the fore.
       *
       * @param coord
       *           the coordinate
       * @param foreHandle
       *           the fore handle
       */
      public Knot2 (
            final Vec2 coord,
            final Vec2 foreHandle ) {

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
      public Knot2 (
            final Vec2 coord,
            final Vec2 foreHandle,
            final Vec2 rearHandle ) {

         this.set(coord, foreHandle, rearHandle);
      }

      /**
       * Tests to see if this knot equals another.
       *
       * @param other
       *           the other knot
       * @return the evaluation
       */
      protected boolean equals ( final Knot2 other ) {

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
       * @param foreDir
       *           a temporary vector
       * @param rearDir
       *           a temporary vector
       * @param foreScaled
       *           a temporary vector
       * @return this knot
       */
      @Chainable
      public Knot2 alignHandles (
            final Vec2 foreDir,
            final Vec2 rearDir,
            final Vec2 foreScaled ) {

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
      public Knot2 alignHandlesBackward (
            final Vec2 foreDir,
            final Vec2 rearDir,
            final Vec2 foreScaled ) {

         Vec2.sub(this.rearHandle, this.coord, rearDir);
         Vec2.sub(this.foreHandle, this.coord, foreDir);
         Vec2.rescale(rearDir, -Vec2.mag(foreDir), foreScaled);
         Vec2.add(foreScaled, this.coord, this.foreHandle);
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
      public Knot2 alignHandlesForward (
            final Vec2 rearDir,
            final Vec2 foreDir,
            final Vec2 rearScaled ) {

         /*
          * The rear handle is a point in space. Subtract the coord
          * from the rear handle to create a rear handle direction
          * relative to the coordinate, which now serves as an
          * origin, or pivot.
          */
         Vec2.sub(this.rearHandle, this.coord, rearDir);

         /*
          * Subtract the coord from the foreHandle to get the
          * foreHandle direction.
          */
         Vec2.sub(this.foreHandle, this.coord, foreDir);

         /*
          * Find the magnitude of the rear direction.
          */
         final float rearMag = Vec2.mag(rearDir);

         /*
          * Align the rear handle with the fore by changing its
          * direction while preserving its magnitude. The negative
          * sign indicates that the rear handle is 180 degrees
          * opposite the forehandle.
          */
         Vec2.rescale(foreDir, -rearMag, rearScaled);

         /*
          * Add the coord back to the new rear direction to convert
          * it from a direction to a point.
          */
         Vec2.add(rearScaled, this.coord, this.rearHandle);

         return this;
      }

      /**
       * Creates a new knot with the coordinate and handles of
       * this knot.
       *
       * @return a new knot
       */
      @Override
      public Knot2 clone () {

         return new Knot2(
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
      public int compareTo ( final Knot2 knot ) {

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
         return this.equals((Knot2) obj);
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
      public Knot2 mirrorHandles () {

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
      public Knot2 mirrorHandles ( final Vec2 temp ) {

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
      public Knot2 mirrorHandlesBackward () {

         this.foreHandle.set(
               this.coord.x - (this.rearHandle.x - this.coord.x),
               this.coord.y - (this.rearHandle.y - this.coord.y));
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
       * @return the knot
       */
      @Chainable
      public Knot2 mirrorHandlesBackward ( final Vec2 rearDir ) {

         Vec2.sub(this.rearHandle, this.coord, rearDir);
         Vec2.sub(this.coord, rearDir, this.foreHandle);
         return this;
      }

      /**
       * Sets the rear-facing handle to mirror the forward-facing
       * handle: the rear will have the same magnitude and negated
       * direction of the fore.
       *
       * @return this knot
       */
      public Knot2 mirrorHandlesForward () {

         this.rearHandle.set(
               this.coord.x - (this.foreHandle.x - this.coord.x),
               this.coord.y - (this.foreHandle.y - this.coord.y));
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
      public Knot2 mirrorHandlesForward ( final Vec2 foreDir ) {

         Vec2.sub(this.foreHandle, this.coord, foreDir);
         Vec2.sub(this.coord, foreDir, this.rearHandle);
         return this;
      }

      /**
       * Reverses the knot's direction by swapping the fore- and
       * rear-handles.
       *
       * @return this knot
       */
      @Chainable
      public Knot2 reverse () {

         final float tx = this.foreHandle.x;
         final float ty = this.foreHandle.y;
         this.foreHandle.set(this.rearHandle);
         this.rearHandle.set(tx, ty);
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
      public Knot2 rotateZ ( final float radians ) {

         final float cosa = (float) Math.cos(radians);
         final float sina = (float) Math.sin(radians);

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
      public Knot2 rotateZ (
            final float cosa,
            final float sina ) {

         Vec2.rotateZ(this.coord, cosa, sina, this.coord);
         Vec2.rotateZ(this.foreHandle, cosa, sina, this.foreHandle);
         Vec2.rotateZ(this.rearHandle, cosa, sina, this.rearHandle);
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
      public Knot2 scale ( final float scale ) {

         Vec2.mult(this.coord, scale, this.coord);
         Vec2.mult(this.foreHandle, scale, this.foreHandle);
         Vec2.mult(this.rearHandle, scale, this.rearHandle);
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
      public Knot2 scale ( final Vec2 scale ) {

         Vec2.mult(this.coord, scale, this.coord);
         Vec2.mult(this.foreHandle, scale, this.foreHandle);
         Vec2.mult(this.rearHandle, scale, this.rearHandle);
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
      public Knot2 scaleForeHandleBy (
            final float scalar,
            final Vec2 temp0,
            final Vec2 temp1 ) {

         Vec2.sub(this.foreHandle, this.coord, temp0);
         Vec2.mult(temp0, scalar, temp1);
         Vec2.add(temp1, this.coord, this.foreHandle);
         return this;
      }

      /**
       * Scales the fore handle to a magnitude.
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
      public Knot2 scaleForeHandleTo (
            final float magnitude,
            final Vec2 temp0,
            final Vec2 temp1 ) {

         Vec2.subNorm(this.foreHandle, this.coord, temp0);
         Vec2.mult(temp0, magnitude, temp1);
         Vec2.add(temp1, this.coord, this.foreHandle);
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
      public Knot2 scaleHandlesBy (
            final float scalar,
            final Vec2 temp0,
            final Vec2 temp1 ) {

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
      public Knot2 scaleHandlesTo (
            final float magnitude,
            final Vec2 temp0,
            final Vec2 temp1 ) {

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
      public Knot2 scaleRearHandleBy (
            final float scalar,
            final Vec2 temp0,
            final Vec2 temp1 ) {

         Vec2.sub(this.rearHandle, this.coord, temp0);
         Vec2.mult(temp0, scalar, temp1);
         Vec2.add(temp1, this.coord, this.rearHandle);
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
      public Knot2 scaleRearHandleTo (
            final float magnitude,
            final Vec2 temp0,
            final Vec2 temp1 ) {

         Vec2.subNorm(this.rearHandle, this.coord, temp0);
         Vec2.mult(temp0, magnitude, temp1);
         Vec2.add(temp1, this.coord, this.rearHandle);
         return this;
      }

      /**
       * Sets the coordinate, fore and rear handles to the input
       * coordinate.
       *
       * @param xCoord
       *           the x coordinate
       * @param yCoord
       *           the y coordinate
       * @return this knot
       */
      @Chainable
      public Knot2 set (
            final float xCoord,
            final float yCoord ) {

         final float xOff = Math.copySign(Utils.EPSILON,
               xCoord);
         final float yOff = Math.copySign(Utils.EPSILON,
               yCoord);

         return this.set(
               xCoord, yCoord,

               xCoord + xOff,
               yCoord + yOff,

               xCoord - xOff,
               yCoord - yOff);
      }

      /**
       * Sets the knot's coordinates and fore handle. The rear
       * handle is a mirror of the forehandle.
       *
       * @param xCoord
       *           the x coordinate
       * @param yCoord
       *           the y coordinate
       * @param xFore
       *           the fore handle x
       * @param yFore
       *           the fore handle y
       * @return the knot
       */
      @Chainable
      public Knot2 set (
            final float xCoord,
            final float yCoord,
            final float xFore,
            final float yFore ) {

         this.coord.set(xCoord, yCoord);
         this.foreHandle.set(xFore, yFore);
         this.rearHandle.set(
               xCoord - (xFore - xCoord),
               yCoord - (yFore - yCoord));
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
       * @param xFore
       *           the fore handle x
       * @param yFore
       *           the fore handle y
       * @param xRear
       *           the rear handle x
       * @param yRear
       *           the rear handle y
       * @return this knot
       */
      @Chainable
      public Knot2 set (
            final float xCoord,
            final float yCoord,
            final float xFore,
            final float yFore,
            final float xRear,
            final float yRear ) {

         this.coord.set(xCoord, yCoord);
         this.foreHandle.set(xFore, yFore);
         this.rearHandle.set(xRear, yRear);

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
      public Knot2 set ( final Knot2 source ) {

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
       * @param xFore
       *           the x fore handle string
       * @param yFore
       *           the y fore handle string
       * @param xRear
       *           the x rear handle string
       * @param yRear
       *           the y rear handle string
       * @return this knot
       */
      @Chainable
      public Knot2 set (
            final String xCoord,
            final String yCoord,
            final String xFore,
            final String yFore,
            final String xRear,
            final String yRear ) {

         this.coord.set(xCoord, yCoord);
         this.foreHandle.set(xFore, yFore);
         this.rearHandle.set(xRear, yRear);

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
      public Knot2 set ( final Vec2 coord ) {

         return this.set(coord.x, coord.y);
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
      public Knot2 set (
            final Vec2 coord,
            final Vec2 foreHandle ) {

         return this.set(
               coord.x,
               coord.y,
               foreHandle.x,
               foreHandle.y);
      }

      /**
       * Sets this knot from a series of vectors.
       *
       * @param coord
       *           the coordinate
       * @param foreHandle
       *           the fore handle
       * @param rearHandle
       *           the rear handle
       * @return this knot
       */
      @Chainable
      public Knot2 set (
            final Vec2 coord,
            final Vec2 foreHandle,
            final Vec2 rearHandle ) {

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
       *           the number of decimal places
       * @return the string
       */
      public String toString ( final int places ) {

         return new StringBuilder(256)
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
       */
      @Chainable
      public Knot2 translate ( final Vec2 v ) {

         Vec2.add(this.coord, v, this.coord);
         Vec2.add(this.foreHandle, v, this.foreHandle);
         Vec2.add(this.rearHandle, v, this.rearHandle);
         return this;
      }
   }

   /**
    * A utility function which defines two target knots as a
    * rounded corner given a point as input. It is assumed
    * here that another function will replace an old knot at
    * the corner with the two generated knots.
    *
    * @param corner
    *           the corner
    * @param radius
    *           the rounding radius
    * @param prevKnot
    *           the knot preceding the corner
    * @param nextKnot
    *           the knot following the corner
    * @param target0
    *           the new knot preceding the corner
    * @param target1
    *           the new knot following the corner
    */
   private static void roundCorner (
         final Vec2 corner,
         final float radius,
         final Knot2 prevKnot,
         final Knot2 nextKnot,
         final Knot2 target0,
         final Knot2 target1 ) {

      /* Cache knot coordinates. */
      final Vec2 prevCoord = prevKnot.coord;
      final Vec2 nextCoord = nextKnot.coord;

      /*
       * The corner is the coord of a knot; the knot will be split
       * into two knots; it is expected that the old corner knot
       * will be replaced by the two new corners.
       */
      final Vec2 corner0 = target0.coord;
      final Vec2 corner1 = target1.coord;

      /*
       * These vectors do not have to be set until the end of the
       * function, with lerp 1 / 3. For that reason, they are used
       * as temp placeholders so as to not have to create new
       * vectors.
       */
      final Vec2 fh0 = target0.foreHandle;
      final Vec2 rh0 = target0.rearHandle;
      final Vec2 fh1 = target1.foreHandle;
      final Vec2 rh1 = target1.rearHandle;

      /*
       * Find the vectors from previous point to corner, and from
       * corner to next point.
       */
      final Vec2 diff0 = Vec2.subNorm(corner, prevCoord, fh0);
      final Vec2 diff1 = Vec2.subNorm(corner, nextCoord, rh0);

      /*
       * Find angle between vectors - simplified because they are
       * already normalized - then divide radius by tangent.
       */
      final float halfAng = 0.5f * Utils.acos(Vec2.dot(diff0, diff1));
      final float rtanHalf = (float) (radius / Math.tan(halfAng));

      /*
       * Multiply the differences (legs of the angle formed at the
       * corner) by the radius-tangent. Then, subtract the product
       * from the corner.
       */
      Vec2.sub(corner, Vec2.mult(diff0, rtanHalf, fh1), corner0);
      Vec2.sub(corner, Vec2.mult(diff1, rtanHalf, rh1), corner1);

      /* Set rounded-corner handles. */
      Curve2.lerp13(corner, corner0, fh0);
      Curve2.lerp13(corner, corner1, rh1);

      /* Flatten edge handles */

      // TODO: These are incorrect.
//      Curve2.lerp13(corner0, prevCoord, rh0);
//      Curve2.lerp13(corner1, nextCoord, fh1);
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
   static Vec2 lerp13 (
         final Vec2 a,
         final Vec2 b,
         final Vec2 target ) {

      return target.set(
            0.6666667f * a.x + IUtils.ONE_THIRD * b.x,
            0.6666667f * a.y + IUtils.ONE_THIRD * b.y);
   }

   /**
    * Creates an arc from a stop angle. The start angle is
    * presumed to be 0.0 degrees.
    *
    * @param stopAngle
    *           the stop angle
    * @param target
    *           the output curve
    * @return the arc
    */
   public static Curve2 arc (
         final float stopAngle,
         final Curve2 target ) {

      return Curve2.arc(0.0f, stopAngle, target);
   }

   /**
    * Creates an arc from a start and stop angle.
    *
    * @param startAngle
    *           the start angle
    * @param stopAngle
    *           the stop angle
    * @param target
    *           the output curve
    * @return the arc
    */
   public static Curve2 arc (
         final float startAngle,
         final float stopAngle,
         final Curve2 target ) {

      return Curve2.arc(
            startAngle, stopAngle, 0.5f, target);
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
   public static Curve2 arc (
         final float startAngle,
         final float stopAngle,
         final float radius,
         final ArcMode arcMode,
         final Curve2 target ) {

      /* Case where arc is used as a progress bar. */
      if (Utils.approxFast(stopAngle - startAngle, IUtils.TAU)) {
         return Curve2.circle(startAngle, target);
      }

      final float a = Utils.modRadians(startAngle);
      final float b = Utils.modRadians(stopAngle);
      final float arcLength = Utils.modRadians(b - a);
      final float destAngle = a + arcLength;

      /* Arc represented as a value in [0.0, 1.0]. */
      final float arcFac = arcLength * IUtils.ONE_TAU;

      /*
       * Finds minimal amount of knots to represent an arc.
       * Assumes that at least 1 knot is needed and that 4 knots
       * accurately represent a complete circle.
       */
      final int knotCount = Utils.ceilToInt(1 + 4 * arcFac);

      /* Find the step for each knot to progress. */
      final float toStep = 1.0f / (knotCount - 1.0f);
      final float handleMag = (float) (radius * IUtils.FOUR_THIRDS_D
            * Math.tan(IUtils.HALF_PI_D * toStep * arcFac));

      /* Calculate knots along arc. */
      target.clear();
      for (int i = 0; i < knotCount; ++i) {
         final float step = i * toStep;
         final float angle = Utils.lerpUnclamped(a, destAngle, step);
         final Knot2 knot = Knot2.fromPolar(
               angle, radius, handleMag, new Knot2());
         target.append(knot);
      }

      /* Depending on arc mode, calculate chord or legs. */
      target.closedLoop = arcMode != ArcMode.OPEN;
      if (target.closedLoop) {
         if (arcMode == ArcMode.CHORD) {

            final Knot2 first = target.getFirst();
            final Knot2 last = target.getLast();

            /* Flatten the first to last handles. */
            Curve2.lerp13(last.coord, first.coord, last.foreHandle);
            Curve2.lerp13(first.coord, last.coord, first.rearHandle);

         } else if (arcMode == ArcMode.PIE) {

            final Knot2 first = target.getFirst();
            final Knot2 last = target.getLast();

            /* Add a center knot. */
            final Knot2 center = new Knot2();
            target.append(center);
            final Vec2 coCenter = center.coord;

            /* Flatten center handles. */
            Curve2.lerp13(coCenter, last.coord, center.rearHandle);
            Curve2.lerp13(coCenter, first.coord, center.foreHandle);

            /* Flatten handle from first to center. */
            Curve2.lerp13(first.coord, coCenter, first.rearHandle);

            /* Flatten handle from last to center. */
            Curve2.lerp13(last.coord, coCenter, last.foreHandle);
         }
      }

      target.name = "Arc";
      return target;
   }

   /**
    * Creates an arc from a start and stop angle. The arc is an
    * open arc.
    *
    * @param startAngle
    *           the start angle
    * @param stopAngle
    *           the stop angle
    * @param radius
    *           the arc radius
    * @param target
    *           the output curve
    * @return the arc
    */
   public static Curve2 arc (
         final float startAngle,
         final float stopAngle,
         final float radius,
         final Curve2 target ) {

      return Curve2.arc(
            startAngle, stopAngle,
            radius, ArcMode.OPEN, target);
   }

   /**
    * Creates a curve which approximates a circle of radius 0.5
    * using four knots.
    *
    * @param target
    *           the output curve
    * @return the circle
    */
   public static Curve2 circle (
         final Curve2 target ) {

      return Curve2.circle(
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
   public static Curve2 circle (
         final float offsetAngle,
         final Curve2 target ) {

      return Curve2.circle(
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
   public static Curve2 circle (
         final float offsetAngle,
         final float radius,
         final Curve2 target ) {

      return Curve2.circle(
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
   public static Curve2 circle (
         final float offsetAngle,
         final float radius,
         final int knotCount,
         final Curve2 target ) {

      target.clear();
      target.closedLoop = true;
      final int vknct = knotCount < 3 ? 3 : knotCount;
      final float invKnCt = 1.0f / vknct;
      final float toAngle = IUtils.TAU * invKnCt;
      final float handleMag = radius * (float) (IUtils.FOUR_THIRDS_D
            * Math.tan(IUtils.HALF_PI_D * invKnCt));
      LinkedList < Knot2 > knots = target.knots;
      for (int i = 0; i < vknct; ++i) {
         final float angle = offsetAngle + i * toAngle;
         final Knot2 knot = Knot2.fromPolar(
               angle, radius, handleMag, new Knot2());
         knots.add(knot);
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
   public static Curve2 fromPoints (
         final boolean closedLoop,
         final Vec2[] points,
         final Curve2 target ) {

      final int knotCount = points.length;
      target.clear();
      target.closedLoop = closedLoop;
      LinkedList < Knot2 > knots = target.knots;
      for (int i = 0; i < knotCount; ++i) {
         final Vec2 point = points[i];
         final Knot2 knot = new Knot2(point, point, point);
         knots.add(knot);
      }
      return Curve2.smoothHandles(target);
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
   public static Curve2 polygon (
         final float offsetAngle,
         final float radius,
         final int knotCount,
         final Curve2 target ) {

      target.clear();
      target.closedLoop = true;
      final int vknct = knotCount < 3 ? 3 : knotCount;
      final float toAngle = IUtils.TAU / vknct;
      final LinkedList < Knot2 > knots = target.knots;
      for (int i = 0; i < vknct; ++i) {
         final float angle = offsetAngle + i * toAngle;
         final Knot2 knot = new Knot2();
         Vec2.fromPolar(angle, radius, knot.coord);
         knots.add(knot);
      }

      target.name = "Polygon";
      return Curve2.straightenHandles(target);
   }

   /**
    * Creates a regular convex polygon with rounded corners.
    * The rounding factor is limited to half the radius.
    *
    * @param offsetAngle
    *           the offset angle
    * @param radius
    *           the radius
    * @param knotCount
    *           the number of knots
    * @param rounding
    *           corner rounding factor
    * @param target
    *           the output curve
    * @return the polygon
    */
   public static Curve2 polygon (
         final float offsetAngle,
         final float radius,
         final int knotCount,
         final float rounding,
         final Curve2 target ) {

      Curve2.polygon(offsetAngle, radius, knotCount, target);

      /* Limit scope of rounding factor. */
      final float valRound = Utils.clamp(rounding, Utils.EPSILON,
            radius * 0.5f);

      /*
       * Old knots will be replaced by new knots, which will be
       * twice the length of the old.
       */
      final LinkedList < Knot2 > oldKn = target.knots;
      // final LinkedList < Knot2 > newKn = new LinkedList <>();
      final int len = oldKn.size();
      Knot2[] newKn = new Knot2[len * 2];

      // for (int i = 0; i < len; ++i) {
      for (int i = 0, j = 0; i < len; ++i, j += 2) {

         /* Acquire corner and surrounding knots. */
         final Knot2 prevKnot = oldKn.get(Utils.mod(i - 1, len));
         final Knot2 cornerKnot = oldKn.get(i);
         final Knot2 nextKnot = oldKn.get((i + 1) % len);

         /* Create outputs for roundCorner. */
         final Knot2 new0 = new Knot2();
         final Knot2 new1 = new Knot2();

         /* Round corners */
         Curve2.roundCorner(
               cornerKnot.coord, valRound,
               prevKnot, nextKnot,
               new0, new1);

         /* Add new knots to the new list. */
         // newKn.add(new0);
         // newKn.add(new1);
         newKn[j] = new0;
         newKn[j + 1] = new1;
      }

      /* Replace old knots with new. */
      oldKn.clear();
      // oldKn.addAll(newKn);
      target.append(newKn);
      return target;
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
   public static Curve2 random (
         final Random rng,
         final int count,
         final float lowerBound,
         final float upperBound,
         final boolean closedLoop,
         final Curve2 target ) {

      final int valCount = count < 3 ? 3 : count;
      final Vec2[] points = new Vec2[valCount];
      for (int i = 0; i < valCount; ++i) {
         points[i] = Vec2.randomCartesian(rng,
               lowerBound, upperBound,
               new Vec2());
      }
      return Curve2.fromPoints(closedLoop, points, target);
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
   public static Curve2 random (
         final Random rng,
         final int count,
         final Vec2 lowerBound,
         final Vec2 upperBound,
         final boolean closedLoop,
         final Curve2 target ) {

      final int valCount = count < 3 ? 3 : count;
      final Vec2[] points = new Vec2[valCount];
      for (int i = 0; i < valCount; ++i) {
         points[i] = Vec2.randomCartesian(rng,
               lowerBound, upperBound, new Vec2());
      }
      target.closedLoop = closedLoop;
      return Curve2.fromPoints(closedLoop, points, target);
   }

   /**
    * Creates a rectangle. The first coordinate, x0 and y0,
    * specifies the top left corner; the second coordinate, x1
    * and y1, specifies the bottom right corner.
    *
    * @param x0
    *           top left corner x
    * @param y0
    *           top left corner y
    * @param x1
    *           bottom right corner x
    * @param y1
    *           bottom right corner y
    * @param target
    *           the output curve
    * @return the rectangle
    */
   public static Curve2 rect (
         final float x0,
         final float y0,
         final float x1,
         final float y1,
         final Curve2 target ) {

      target.clear();
      target.closedLoop = true;
      target.name = "Rect";

      final LinkedList < Knot2 > knots = target.knots;
      knots.add(new Knot2(x0, y0));
      knots.add(new Knot2(x1, y0));
      knots.add(new Knot2(x1, y1));
      knots.add(new Knot2(x0, y1));

      return Curve2.straightenHandles(target);
   }

   /**
    * Creates a rounded rectangle. The first coordinate, x0 and
    * y0, specifies the top left corner; the second coordinate,
    * x1 and y1, specifies the bottom right corner. The fifth
    * parameter specifies the corner rounding factor.
    *
    * @param x0
    *           top left corner x
    * @param y0
    *           top left corner y
    * @param x1
    *           bottom right corner x
    * @param y1
    *           bottom right corner y
    * @param corner
    *           the rounding factor
    * @param target
    *           the output curve
    * @return the rounded rectangle
    */
   public static Curve2 rect (
         final float x0,
         final float y0,
         final float x1,
         final float y1,
         final float corner,
         final Curve2 target ) {

      return Curve2.rect(
            x0, y0, x1, y1,
            corner, corner,
            corner, corner,
            target);
   }

   /**
    * Creates a rounded rectangle. The first coordinate, x0 and
    * y0, specifies the top left corner; the second coordinate,
    * x1 and y1, specifies the bottom right corner. The next
    * four parameters specify the rounding factor for the top
    * left, top right, bottom right and bottom left corners.
    *
    * @param x0
    *           top left corner x
    * @param y0
    *           top left corner y
    * @param x1
    *           bottom right corner x
    * @param y1
    *           bottom right corner y
    * @param tl
    *           rounding top left corner
    * @param tr
    *           rounding top right corner
    * @param br
    *           rounding bottom right corner
    * @param bl
    *           rounding bottom left corner
    * @param target
    *           the output curve
    * @return the rounded rectangle
    */
   public static Curve2 rect (
         final float x0,
         final float y0,
         final float x1,
         final float y1,
         final float tl,
         final float tr,
         final float br,
         final float bl,
         final Curve2 target ) {

      /* Validate corner values. */
      final float vtl = Utils.clamp(Utils.abs(tl),
            Utils.EPSILON, IUtils.ONE_THIRD);
      final float vtr = Utils.clamp(Utils.abs(tr),
            Utils.EPSILON, IUtils.ONE_THIRD);
      final float vbr = Utils.clamp(Utils.abs(br),
            Utils.EPSILON, IUtils.ONE_THIRD);
      final float vbl = Utils.clamp(Utils.abs(bl),
            Utils.EPSILON, IUtils.ONE_THIRD);

      /* Top edge. */
      final Knot2 k0 = new Knot2(x0 + vtl, y0, 0.0f, 0.0f, 0.0f, 0.0f);
      final Knot2 k1 = new Knot2(x1 - vtr, y0, 0.0f, 0.0f, 0.0f, 0.0f);

      /* Right edge. */
      final Knot2 k2 = new Knot2(x1, y0 - vtr, 0.0f, 0.0f, 0.0f, 0.0f);
      final Knot2 k3 = new Knot2(x1, y1 + vbr, 0.0f, 0.0f, 0.0f, 0.0f);

      /* Bottom edge. */
      final Knot2 k4 = new Knot2(x1 - vbr, y1, 0.0f, 0.0f, 0.0f, 0.0f);
      final Knot2 k5 = new Knot2(x0 + vbl, y1, 0.0f, 0.0f, 0.0f, 0.0f);

      /* Left edge. */
      final Knot2 k6 = new Knot2(x0, y1 + vbl, 0.0f, 0.0f, 0.0f, 0.0f);
      final Knot2 k7 = new Knot2(x0, y0 - vtl, 0.0f, 0.0f, 0.0f, 0.0f);

      /* Cache knot coord shortcuts . */
      final Vec2 k0co = k0.coord;
      final Vec2 k1co = k1.coord;
      final Vec2 k2co = k2.coord;
      final Vec2 k3co = k3.coord;
      final Vec2 k4co = k4.coord;
      final Vec2 k5co = k5.coord;
      final Vec2 k6co = k6.coord;
      final Vec2 k7co = k7.coord;

      /* Cache even knot rear handle shortcuts. */
      final Vec2 k0rh = k0.rearHandle;
      final Vec2 k2rh = k2.rearHandle;
      final Vec2 k4rh = k4.rearHandle;
      final Vec2 k6rh = k6.rearHandle;

      /* Cache odd knot fore handle shortcuts. */
      final Vec2 k1fh = k1.foreHandle;
      final Vec2 k3fh = k3.foreHandle;
      final Vec2 k5fh = k5.foreHandle;
      final Vec2 k7fh = k7.foreHandle;

      /* Straighten fore handles of each edge. */
      Curve2.lerp13(k0co, k1co, k0.foreHandle);
      Curve2.lerp13(k2co, k3co, k2.foreHandle);
      Curve2.lerp13(k4co, k5co, k4.foreHandle);
      Curve2.lerp13(k6co, k7co, k6.foreHandle);

      /* Straighten rear handles of each edge. */
      Curve2.lerp13(k1co, k0co, k1.rearHandle);
      Curve2.lerp13(k3co, k2co, k3.rearHandle);
      Curve2.lerp13(k5co, k4co, k5.rearHandle);
      Curve2.lerp13(k7co, k6co, k7.rearHandle);

      /* Top Right Corner. */
      if (tr < 0.0f) {
         final float ix1 = x1 - vtr;
         final float iy0 = y0 - vtr;

         k1fh.x = (k1co.x + ix1) * 0.5f;
         k1fh.y = (k1co.y + iy0) * 0.5f;

         k2rh.x = (k2co.x + ix1) * 0.5f;
         k2rh.y = (k2co.y + iy0) * 0.5f;
      } else {
         k1fh.x = (k1co.x + x1) * 0.5f;
         k1fh.y = (k1co.y + y0) * 0.5f;

         k2rh.x = (k2co.x + x1) * 0.5f;
         k2rh.y = (k2co.y + y0) * 0.5f;
      }

      /* Bottom Right Corner. */
      if (br < 0.0f) {
         final float ix1 = x1 - vbr;
         final float iy1 = y1 + vbr;

         k3fh.x = (k3co.x + ix1) * 0.5f;
         k3fh.y = (k3co.y + iy1) * 0.5f;

         k4rh.x = (k4co.x + ix1) * 0.5f;
         k4rh.y = (k4co.y + iy1) * 0.5f;
      } else {
         k3fh.x = (k3co.x + x1) * 0.5f;
         k3fh.y = (k3co.y + y1) * 0.5f;

         k4rh.x = (k4co.x + x1) * 0.5f;
         k4rh.y = (k4co.y + y1) * 0.5f;
      }

      /* Bottom Left Corner. */
      if (bl < 0.0f) {
         final float ix0 = x0 + vbl;
         final float iy1 = y1 + vbl;

         k5fh.x = (k5co.x + ix0) * 0.5f;
         k5fh.y = (k5co.y + iy1) * 0.5f;

         k6rh.x = (k6co.x + ix0) * 0.5f;
         k6rh.y = (k6co.y + iy1) * 0.5f;
      } else {
         k5fh.x = (k5co.x + x0) * 0.5f;
         k5fh.y = (k5co.y + y1) * 0.5f;

         k6rh.x = (k6co.x + x0) * 0.5f;
         k6rh.y = (k6co.y + y1) * 0.5f;
      }

      /* Top Left Corner. */
      if (tl < 0.0f) {
         final float ix0 = x0 + vtl;
         final float iy0 = y0 - vtl;

         k7fh.x = (k7co.x + ix0) * 0.5f;
         k7fh.y = (k7co.y + iy0) * 0.5f;

         k0rh.x = (k0co.x + ix0) * 0.5f;
         k0rh.y = (k0co.y + iy0) * 0.5f;
      } else {
         k7fh.x = (k7co.x + x0) * 0.5f;
         k7fh.y = (k7co.y + y0) * 0.5f;

         k0rh.x = (k0co.x + x0) * 0.5f;
         k0rh.y = (k0co.y + y0) * 0.5f;
      }

      /* Clear old data from target. */
      target.clear();
      target.closedLoop = true;
      target.name = "Rect";

      /* Add knots to the target. */
      final LinkedList < Knot2 > knots = target.knots;
      knots.add(k0);
      knots.add(k1);
      knots.add(k2);
      knots.add(k3);
      knots.add(k4);
      knots.add(k5);
      knots.add(k6);
      knots.add(k7);

      return target;
   }

   /**
    * Creates a rectangle. The first coordinate specifies the
    * top left corner; the second coordinate specifies the
    * bottom right corner.
    *
    * @param tl
    *           the top left corner
    * @param br
    *           the bottom right corner
    * @param target
    *           the output curve
    * @return the rectangle
    */
   public static Curve2 rect (
         final Vec2 tl,
         final Vec2 br,
         final Curve2 target ) {

      return Curve2.rect(
            tl.x, tl.y,
            br.x, br.y, target);
   }

   /**
    * Creates a rounded rectangle. The first coordinate
    * specifies the top left corner; the second coordinate
    * specifies the bottom right corner. The third parameter
    * specifies the corner rounding factor.
    *
    * @param tl
    *           the top left corner
    * @param br
    *           the bottom right corner
    * @param corner
    *           the rounding factor
    * @param target
    *           the output curve
    * @return the rounded rectangle
    */
   public static Curve2 rect (
         final Vec2 tl,
         final Vec2 br,
         final float corner,
         final Curve2 target ) {

      return Curve2.rect(
            tl.x, tl.y,
            br.x, br.y,
            corner, target);
   }

   /**
    * Creates a rounded rectangle. The first coordinate
    * specifies the top left corner; the second coordinate,
    * specifies the bottom right corner. The next four
    * parameters specify the rounding factor for the top left,
    * top right, bottom right and bottom left corners.
    *
    * @param tl
    *           the top left corner
    * @param br
    *           the bottom right corner
    * @param tlCorner
    *           rounding top left corner
    * @param trCorner
    *           rounding top right corner
    * @param brCorner
    *           rounding bottom right corner
    * @param blCorner
    *           rounding bottom left corner
    * @param target
    *           the output curve
    * @return the rounded rectangle
    */
   public static Curve2 rect (
         final Vec2 tl,
         final Vec2 br,
         final float tlCorner,
         final float trCorner,
         final float brCorner,
         final float blCorner,
         final Curve2 target ) {

      return Curve2.rect(
            tl.x, tl.y, br.x, br.y,
            tlCorner, trCorner,
            brCorner, blCorner, target);
   }

   /**
    * Adjusts knot handles so as to create a smooth, continuous
    * curve.
    *
    * @param target
    *           the output curve
    * @return the curve
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    * @see Vec2#normalize(Vec2, Vec2)
    * @see Vec2#add(Vec2, Vec2, Vec2)
    * @see Vec2#mult(Vec2, float, Vec2)
    * @see Vec2#rescale(Vec2, float, Vec2)
    */
   public static Curve2 smoothHandles ( final Curve2 target ) {

      final LinkedList < Knot2 > knots = target.knots;
      final int knotLength = knots.size();
      if (knotLength < 3) {
         return target;
      }

      // TODO: Can this be optimized to use fewer temp vectors?
      final Vec2 back = new Vec2();
      final Vec2 backNorm = new Vec2();
      final Vec2 backRescale = new Vec2();

      final Vec2 forward = new Vec2();
      final Vec2 forNorm = new Vec2();
      final Vec2 forRescale = new Vec2();

      final Vec2 dir0 = new Vec2();
      final Vec2 dir1 = new Vec2();
      final Vec2 dir2 = new Vec2();

      final boolean closedLoop = target.closedLoop;

      for (int i = 0; i < knotLength; ++i) {
         final Knot2 knot = knots.get(i);
         final Vec2 currCoord = knot.coord;

         float backDist = 0.0f;
         float foreDist = 0.0f;

         if (closedLoop) {

            final int prevIndex = Utils.mod(i - 1, knotLength);
            final Knot2 prev = knots.get(prevIndex);

            Vec2.sub(prev.coord, currCoord, back);
            backDist = Vec2.mag(back);
            Vec2.normalize(back, backNorm);
            Vec2.add(dir0, backNorm, dir1);

            final int nextIndex = (i + 1) % knotLength;
            final Knot2 next = knots.get(nextIndex);

            Vec2.sub(next.coord, currCoord, forward);
            foreDist = -Vec2.mag(forward);
            Vec2.normalize(forward, forNorm);
            Vec2.sub(dir1, forNorm, dir2);

         } else {

            final int prevIndex = i - 1;
            if (prevIndex >= 0) {
               final Knot2 prev = knots.get(prevIndex);

               Vec2.sub(prev.coord, currCoord, back);
               backDist = Vec2.mag(back);
               Vec2.normalize(back, backNorm);
               Vec2.add(dir0, backNorm, dir1);
            }

            final int nextIndex = i + 1;
            if (nextIndex < knotLength) {
               final Knot2 next = knots.get(nextIndex);

               Vec2.sub(next.coord, currCoord, forward);
               foreDist = -Vec2.mag(forward);
               Vec2.normalize(forward, forNorm);
               Vec2.sub(dir1, forNorm, dir2);
            }
         }

         Vec2.rescale(dir2, IUtils.ONE_THIRD, dir0);

         Vec2.mult(dir0, backDist, backRescale);
         Vec2.add(backRescale, currCoord, knot.rearHandle);

         Vec2.mult(dir0, foreDist, forRescale);
         Vec2.add(forRescale, currCoord, knot.foreHandle);
      }

      /*
       * Match fore and rear handles of first and last knots if
       * the curve is not closed.
       */
      if (!closedLoop) {
         final Knot2 first = target.knots.getFirst();
         first.mirrorHandlesForward();
         final Knot2 last = target.knots.getLast();
         last.mirrorHandlesBackward();
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
   public static Curve2 straightenHandles ( final Curve2 target ) {

      final LinkedList < Knot2 > knots = target.knots;
      final int knotLength = knots.size();
      if (knotLength < 2) {
         return target;
      }

      if (knotLength == 2) {
         final Knot2 first = knots.getFirst();
         final Knot2 last = knots.getLast();

         Curve2.lerp13(first.coord, last.coord, first.foreHandle);
         first.mirrorHandlesForward();

         Curve2.lerp13(last.coord, first.coord, last.rearHandle);
         last.mirrorHandlesBackward();

         return target;
      }

      final Iterator < Knot2 > itr = knots.iterator();
      Knot2 prev = null;
      Knot2 curr = itr.next();
      while (itr.hasNext()) {
         prev = curr;
         curr = itr.next();
         Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
         Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);
      }

      if (target.closedLoop) {
         final Knot2 first = knots.getFirst();
         final Knot2 last = knots.getLast();
         Curve2.lerp13(first.coord, last.coord, first.rearHandle);
         Curve2.lerp13(last.coord, first.coord, last.foreHandle);
      } else {
         knots.getFirst().mirrorHandlesForward();
         knots.getLast().mirrorHandlesBackward();
      }

      return target;
   }

   /**
    * The list of knots contained by the curve.
    */
   private final LinkedList < Knot2 > knots = new LinkedList <>();

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
   public Curve2 () {

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
   public Curve2 (
         final boolean closedLoop,
         final Collection < Knot2 > knots ) {

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
   public Curve2 (
         final boolean closedLoop,
         final Knot2... knots ) {

      this(closedLoop, Arrays.asList(knots));
   }

   /**
    * Creates a named curve with two default knots.
    *
    * @param name
    *           the name
    */
   public Curve2 ( final String name ) {

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
   public Curve2 (
         final String name,
         final boolean closedLoop,
         final Collection < Knot2 > knots ) {

      super(name);
      this.closedLoop = closedLoop;
      this.knots.addAll(knots);
   }

   /**
    * Creates a named curve from a comma-separated list of
    * knots.
    *
    * @param name
    *           the name
    * @param closedLoop
    *           whether or not the curve is closed
    * @param knots
    *           the list of knots
    */
   public Curve2 (
         final String name,
         final boolean closedLoop,
         final Knot2... knots ) {

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
    * @see Vec2#bezierPoint(Vec2, Vec2, Vec2, Vec2, float,
    *      Vec2)
    * @see Vec2#bezierTangent(Vec2, Vec2, Vec2, Vec2, float,
    *      Vec2)
    * @see Knot2#mirrorHandlesForward()
    */
   protected Knot2 bezierKnot (
         final Knot2 a,
         final Knot2 b,
         final float step,
         final Knot2 target ) {

      Vec2.bezierPoint(
            a.coord, a.foreHandle,
            b.rearHandle, b.coord,
            step, target.coord);

      Vec2.bezierTangent(
            a.coord, a.foreHandle,
            b.rearHandle, b.coord,
            step, target.foreHandle);

      Vec2.negate(
            target.foreHandle,
            target.rearHandle);

      Vec2.add(
            target.coord,
            target.foreHandle,
            target.foreHandle);

      Vec2.add(
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
    * @see Vec2#bezierPoint(Vec2, Vec2, Vec2, Vec2, float,
    *      Vec2)
    */
   protected Vec2 bezierPoint (
         final Knot2 a,
         final Knot2 b,
         final float step,
         final Vec2 target ) {

      return Vec2.bezierPoint(
            a.coord, a.foreHandle,
            b.rearHandle, b.coord,
            step, target);
   }

   /**
    * A helper function for eval. Returns a tangent given two
    * knots and a stpe. Assumes the step has already been
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
    * @see Vec2#bezierTangent(Vec2, Vec2, Vec2, Vec2, float,
    *      Vec2)
    */
   protected Vec2 bezierTangent (
         final Knot2 a,
         final Knot2 b,
         final float step,
         final Vec2 target ) {

      return Vec2.bezierTangent(
            a.coord, a.foreHandle,
            b.rearHandle, b.coord,
            step, target);
   }

   /**
    * A helper function for eval. Returns a normalized tangent
    * given two knots. Assumes the step has already been
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
    * @return the normalized tangent
    * @see Vec2#bezierTanUnit(Vec2, Vec2, Vec2, Vec2, float,
    *      Vec2)
    */
   protected Vec2 bezierTanUnit (
         final Knot2 a,
         final Knot2 b,
         final float step,
         final Vec2 target ) {

      return Vec2.bezierTanUnit(
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
   public boolean append ( final Collection < ? extends Knot2 > knots ) {

      return this.knots.addAll(knots);
   }

   /**
    * Append an array of knots to the curve's list of knots.
    *
    * @param knots
    *           the array of knots
    * @return this curve.
    */
   @Chainable
   public Curve2 append ( final Knot2... knots ) {

      final int len = knots.length;
      for (int i = 0; i < len; ++i) {
         final Knot2 knot = knots[i];
         if (knot != null) {
            this.knots.add(knot);
         }
      }
      return this;
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
   public Curve2 append ( final Knot2 knot ) {

      this.knots.add(knot);
      return this;
   }

   /**
    * Calculates the approximate length of a curve to a given
    * level of precision.
    *
    * @param precision
    *           the precision
    * @return the length
    * @see Curve2#evalRange(int)
    */
   public float calcLength ( final int precision ) {

      // TODO: Is there a way to use distSq instead of dist when
      // summing the distances and then scale sum?
      float sum = 0.0f;
      final Vec2[][] segments = this.evalRange(precision + 1);
      final int len = segments.length;
      for (int i = 1, j = 0; i < len; ++i, ++j) {
         sum += Vec2.dist(
               segments[j][0],
               segments[i][0]);
      }

      return sum;
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
   public Knot2 eval (
         final float step,
         final Knot2 target ) {

      final int knotLength = this.knots.size();

      float tScaled = 0.0f;
      int i = 0;
      Knot2 a = null;
      Knot2 b = null;
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
    * @see Vec2#bezierPoint(Vec2, Vec2, Vec2, Vec2, float,
    *      Vec2)
    * @see Vec2#bezierTanUnit(Vec2, Vec2, Vec2, Vec2, float,
    *      Vec2)
    */
   public Vec2 eval (
         final float step,
         final Vec2 coord,
         final Vec2 tangent ) {

      final int knotLength = this.knots.size();

      float tScaled = 0.0f;
      int i = 0;
      Knot2 a = null;
      Knot2 b = null;
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
    * @see Vec2#subNorm(Vec2, Vec2, Vec2)
    */
   public Vec2 evalFirst (
         final Vec2 coord,
         final Vec2 tangent ) {

      final Knot2 kFirst = this.knots.getFirst();
      coord.set(kFirst.coord);
      Vec2.subNorm(kFirst.foreHandle, coord, tangent);
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
    * @see Vec2#subNorm(Vec2, Vec2, Vec2)
    */
   public Vec2 evalLast (
         final Vec2 coord,
         final Vec2 tangent ) {

      final Knot2 kLast = this.knots.getLast();
      coord.set(kLast.coord);
      Vec2.subNorm(coord, kLast.rearHandle, tangent);

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
   public Vec2[][] evalRange ( final int count ) {

      final int vcount = count < 3 ? 3 : count;
      final Vec2[][] result = new Vec2[vcount][2];
      final int last = this.closedLoop ? vcount : vcount - 1;
      final float toPercent = 1.0f / last;
      Vec2 coord = null;
      Vec2 tangent = null;
      for (int i = 0; i < vcount; ++i) {
         coord = result[i][0] = new Vec2();
         tangent = result[i][1] = new Vec2();
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
   public Knot2 get ( final int i ) {

      final int index = this.closedLoop ? Utils.mod(i, this.knots.size()) : i;
      return this.knots.get(index);
   }

   /**
    * Gets the first knot in the curve.
    *
    * @return the knot
    * @see LinkedList#getFirst()
    */
   public Knot2 getFirst () {

      return this.knots.getFirst();
   }

   /**
    * Gets the last knot in the curve.
    *
    * @return the knot
    * @see LinkedList#getLast()
    */
   public Knot2 getLast () {

      return this.knots.getLast();
   }

   /**
    * Returns an iterator, which allows an enhanced for-loop to
    * access the knots in a curve.
    *
    * @return the iterator
    * @see LinkedList#iterator()
    */
   @Override
   public Iterator < Curve2.Knot2 > iterator () {

      return this.knots.iterator();
   }

   /**
    * The number of knots in the curve.
    *
    * @return the size
    * @see LinkedList#size()
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
   public Knot2 removeFirst () {

      return this.knots.removeFirst();
   }

   /**
    * Removes and returns the last knot in the curve.
    *
    * @return the knot
    * @see LinkedList#removeLast()
    */
   public Knot2 removeLast () {

      return this.knots.removeLast();
   }

   /**
    * Resets the curve, leaving two default knots.
    *
    * @return this curve
    */
   @Chainable
   public Curve2 reset () {

      this.knots.clear();

      final Knot2 knot0 = new Knot2(
            -0.5f, 0.0f,
            -0.25f, 0.25f,
            -0.75f, -0.25f);

      final Knot2 knot1 = new Knot2(
            0.5f, 0.0f,
            1.0f, 0.0f,
            0.0f, 0.0f);

      this.knots.add(knot0);
      this.knots.add(knot1);

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
   public Curve2 reverse () {

      Collections.reverse(this.knots);

      final Iterator < Knot2 > itr = this.knots.iterator();
      while (itr.hasNext()) {
         itr.next().reverse();
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
    * @see Knot2#rotateZ(float)
    */
   @Chainable
   public Curve2 rotateZ ( final float radians ) {

      final float cosa = (float) Math.cos(radians);
      final float sina = (float) Math.sin(radians);

      final Iterator < Knot2 > itr = this.knots.iterator();
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
    * @see Knot2#scale(float)
    */
   @Chainable
   public Curve2 scale ( final float scale ) {

      final Iterator < Knot2 > itr = this.knots.iterator();
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
    * @see Knot2#scale(Vec2)
    */
   @Chainable
   public Curve2 scale ( final Vec2 scale ) {

      final Iterator < Knot2 > itr = this.knots.iterator();
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
      final Iterator < Knot2 > itr = this.knots.iterator();
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
   public String toObjString ( final int precision ) {

      final StringBuilder result = new StringBuilder();
      final Vec2[][] segments = this.evalRange(precision);
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
         final Vec2 coord = segments[i][0];
         result.append('v')
               .append(' ')
               .append(coord.toObjString())
               .append(" 0.0\n");
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

      final StringBuilder sb = new StringBuilder()
            .append("{ closedLoop: ")
            .append(this.closedLoop)
            .append(", \n  knots: [ \n");

      final Iterator < Knot2 > itr = this.knots.iterator();
      while (itr.hasNext()) {
         sb.append(itr.next());
         if (itr.hasNext()) {
            sb.append(',').append('\n');
         }
      }

      sb.append(" ] }");
      return sb.toString();
   }

   /**
    * Renders the curve as a string containing an SVG path.
    *
    * @return the SVG string
    */
   public String toSvgString () {

      final int knotLength = this.knots.size();

      if (knotLength < 2) {
         return "";
      }

      final int end = this.closedLoop ? knotLength + 1 : knotLength;
      Knot2 currKnot = null;
      Knot2 prevKnot = this.getFirst();

      final StringBuilder result = new StringBuilder();
      result.append("<path d=\"M ")
            .append(prevKnot.coord.toSvgString());

      for (int i = 1; i < end; ++i) {
         // TODO: Could be updated to match the drawing of the curve,
         // i.e., to avoid using the modulo operation.
         currKnot = this.knots.get(i % knotLength);

         result.append(' ')
               .append('C')
               .append(' ')
               .append(prevKnot.foreHandle.toSvgString())
               .append(',')
               .append(currKnot.rearHandle.toSvgString())
               .append(',')
               .append(currKnot.coord.toSvgString());

         prevKnot = currKnot;
      }

      if (this.closedLoop) {
         result.append(' ').append('Z');
      }
      result.append("\"></path>");
      return result.toString();
   }

   /**
    * Translates all knots in the curve by a vector.
    *
    * @param v
    *           the vector
    * @return this curve
    * @see Knot2#translate(Vec2)
    */
   @Chainable
   public Curve2 translate ( final Vec2 v ) {

      final Iterator < Knot2 > itr = this.knots.iterator();
      while (itr.hasNext()) {
         itr.next().translate(v);
      }
      return this;
   }
}
