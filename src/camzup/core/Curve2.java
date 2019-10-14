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
       * @see Vec2#mult(Vec2, float, Vec2)
       * @see Vec2#negate(Vec2, Vec2)
       * @see Vec2#add(Vec2, Vec2, Vec2)
       */
      public static Knot2 fromPolar (
            final float angle,
            final float radius,
            final float handleMag,
            final Knot2 target,
            final Vec2 temp0,
            final Vec2 temp1 ) {

         // temp0 will hold the ideal radian.
         Vec2.fromPolar(angle, 1.0f, temp0);

         // temp1 will hold the forehandle.
         Vec2.perpendicularCCW(temp0, temp1);

         // target.coord has reached its final state;
         // temp0 will be freed.
         Vec2.mult(temp0, radius, target.coord);

         // temp0 will hold scaled forehandle.
         // temp1 will be freed.
         Vec2.mult(temp1, handleMag, temp0);

         // temp1 will hold the mirrored rearhandle.
         Vec2.negate(temp0, temp1);

         Vec2.add(temp0, target.coord, target.foreHandle);
         Vec2.add(temp1, target.coord, target.rearHandle);

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
       * Creates a knot from a coordinate.
       *
       * @param coord
       *           the coordinate
       */
      public Knot2 ( final Vec2 coord ) {

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

         /**
          * The rear handle is a point in space. Subtract the coord
          * from the rear handle to create a rear handle direction
          * relative to the coordinate, which now serves as an
          * origin, or pivot.
          */
         Vec2.sub(this.rearHandle, this.coord, rearDir);

         /**
          * Subtract the coord from the foreHandle to get the
          * foreHandle direction.
          */
         Vec2.sub(this.foreHandle, this.coord, foreDir);

         /**
          * Find the magnitude of the rear direction.
          */
         final float rearMag = Vec2.mag(rearDir);

         /**
          * Align the rear handle with the fore by changing its
          * direction while preserving its magnitude. The negative
          * sign indicates that the rear handle is 180 degrees
          * opposite the forehandle.
          */
         Vec2.rescale(foreDir, -rearMag, rearScaled);

         /**
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
       * @param temp
       *           a temporary vector
       * @return this knot
       */
      @Chainable
      public Knot2 reverse ( final Vec2 temp ) {

         temp.set(this.foreHandle);
         this.foreHandle.set(this.rearHandle);
         this.rearHandle.set(temp);
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

         Vec2.rotateZ(this.coord, radians, this.coord);
         Vec2.rotateZ(this.foreHandle, radians, this.foreHandle);
         Vec2.rotateZ(this.rearHandle, radians, this.rearHandle);
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

         return this.set(
               xCoord, yCoord,

               xCoord + Utils.EPSILON,
               yCoord + Utils.EPSILON,

               xCoord - Utils.EPSILON,
               yCoord - Utils.EPSILON);
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
       * Returns a string representation of this knot.
       *
       * @return the string
       */
      @Override
      public String toString () {

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
      public Knot2 translate ( final Vec2 v ) {

         Vec2.add(this.coord, v, this.coord);
         Vec2.add(this.foreHandle, v, this.foreHandle);
         Vec2.add(this.rearHandle, v, this.rearHandle);
         return this;
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
   }

   /**
    * Creates an arc from a stop angle. The start angle is
    * presumed to be 0.0 degrees.
    *
    * @param stopAngle
    *           the stop angle
    * @param target
    *           the output curve
    * @param temp0
    *           a temporary vector
    * @param temp1
    *           a temporary vector
    * @return the arc
    */
   public static Curve2 arc (
         final float stopAngle,
         final Curve2 target,
         final Vec2 temp0,
         final Vec2 temp1 ) {

      return Curve2.arc(0.0f, stopAngle,
            target, temp0, temp1);
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
    * @param temp0
    *           a temporary vector
    * @param temp1
    *           a temporary vector
    * @return the arc
    */
   public static Curve2 arc (
         final float startAngle,
         final float stopAngle,
         final Curve2 target,
         final Vec2 temp0,
         final Vec2 temp1 ) {

      return Curve2.arc(
            startAngle, stopAngle, 0.5f,
            target, temp0, temp1);
   }

   /**
    * Creates an arc from a start and stop angle. The arc can
    * be open, a transversed by a chord, or pie-shaped.
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
    * @param temp0
    *           a temporary vector
    * @param temp1
    *           a temporary vector
    * @return the arc
    */
   public static Curve2 arc (
         final float startAngle,
         final float stopAngle,
         final float radius,
         final ArcMode arcMode,
         final Curve2 target,
         final Vec2 temp0,
         final Vec2 temp1 ) {

      /* Case where arc is used as a progress bar. */
      if (Utils.approxFast(stopAngle - startAngle, IUtils.TAU)) {
         return Curve2.circle(startAngle, target, temp0, temp1);
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

      target.clear();
      for (int i = 0; i < knotCount; ++i) {
         final float step = i * toStep;
         final float angle = Utils.lerpUnclamped(a, destAngle, step);
         final Knot2 knot = Knot2.fromPolar(
               angle, radius, handleMag,
               new Knot2(),
               temp0, temp1);
         target.append(knot);
      }

      target.closedLoop = arcMode != ArcMode.OPEN;
      if (target.closedLoop) {
         if (arcMode == ArcMode.CHORD) {

            final Knot2 first = target.getFirst();
            final Knot2 last = target.getLast();

            /* Flatten the first to last handles. */
            Vec2.mix(
                  last.coord,
                  first.coord,
                  IUtils.ONE_THIRD,
                  last.foreHandle);

            Vec2.mix(
                  first.coord,
                  last.coord,
                  IUtils.ONE_THIRD,
                  first.rearHandle);

         } else if (arcMode == ArcMode.PIE) {

            final Knot2 first = target.getFirst();
            final Knot2 last = target.getLast();

            /* Add a center knot. */
            final Knot2 center = new Knot2();
            target.append(center);
            final Vec2 coCenter = center.coord;

            /* Flatten center handles. */
            Vec2.mix(
                  coCenter,
                  last.coord,
                  IUtils.ONE_THIRD,
                  center.rearHandle);

            Vec2.mix(
                  coCenter,
                  first.coord,
                  IUtils.ONE_THIRD,
                  center.foreHandle);

            /* Flatten handle from first to center. */
            Vec2.mix(
                  first.coord,
                  coCenter,
                  IUtils.ONE_THIRD,
                  first.rearHandle);

            /* Flatten handle from last to center. */
            Vec2.mix(
                  last.coord,
                  coCenter,
                  IUtils.ONE_THIRD,
                  last.foreHandle);
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
    * @param temp0
    *           a temporary vector
    * @param temp1
    *           a temporary vector
    * @return the arc
    */
   public static Curve2 arc (
         final float startAngle,
         final float stopAngle,
         final float radius,
         final Curve2 target,
         final Vec2 temp0,
         final Vec2 temp1 ) {

      return Curve2.arc(
            startAngle, stopAngle,
            radius, ArcMode.OPEN,
            target,
            temp0, temp1);
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
   public static Curve2 circle (
         final Curve2 target,
         final Vec2 temp0,
         final Vec2 temp1 ) {

      return Curve2.circle(
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
   public static Curve2 circle (
         final float offsetAngle,
         final Curve2 target,
         final Vec2 temp0,
         final Vec2 temp1 ) {

      return Curve2.circle(
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
   public static Curve2 circle (
         final float offsetAngle,
         final float radius,
         final Curve2 target,
         final Vec2 temp0,
         final Vec2 temp1 ) {

      return Curve2.circle(
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
   public static Curve2 circle (
         final float offsetAngle,
         final float radius,
         final int knotCount,
         final Curve2 target,
         final Vec2 temp0,
         final Vec2 temp1 ) {

      target.clear();
      target.closedLoop = true;
      final int vknct = knotCount < 3 ? 3 : knotCount;
      final float invKnCt = 1.0f / vknct;
      final float toAngle = IUtils.TAU * invKnCt;
      final float handleMag = radius * (float) (IUtils.FOUR_THIRDS_D
            * Math.tan(IUtils.HALF_PI_D * invKnCt));

      for (int i = 0; i < knotCount; ++i) {
         final float angle = offsetAngle + i * toAngle;
         final Knot2 knot = Knot2.fromPolar(
               angle, radius, handleMag,
               new Knot2(),
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
   public static Curve2 fromPoints (
         final boolean closedLoop,
         final Vec2[] points,
         final Curve2 target ) {

      final int knotCount = points.length;
      target.clear();
      target.closedLoop = closedLoop;
      for (int i = 0; i < knotCount; ++i) {
         final Vec2 point = points[i];
         final Knot2 knot = new Knot2(point, point, point);
         target.append(knot);
      }
      return Curve2.smoothHandles(target);
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
               lowerBound, upperBound,
               new Vec2());
      }
      target.closedLoop = closedLoop;

      return Curve2.fromPoints(closedLoop, points, target);
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

      final List < Knot2 > knots = target.knots;
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
   private final LinkedList < Knot2 > knots = new LinkedList <>();

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
    * Append a collection of knots to the curve's list of
    * knots. Returns true if the operation was successful.
    *
    * @param knots
    *           the collection of knots
    * @return success
    * @see List#addAll(Collection)
    */
   public boolean append ( final Collection < ? extends Knot2 > knots ) {

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
   public Curve2 append ( final Knot2 knot ) {

      this.knots.add(knot);
      return this;
   }

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
    * Gets a knot from the curve by an index. When the curve is
    * a closed loop, the index wraps around.
    *
    * @param i
    *           the index
    * @return the knot
    * @see List#get(int)
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
    * @see List#iterator()
    */
   @Override
   public Iterator < Curve2.Knot2 > iterator () {

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
    * @see Knot2#reverse(Vec2)
    */
   @Chainable
   public Curve2 reverse ( final Vec2 temp ) {

      Collections.reverse(this.knots);
      for (final Knot2 knot : this.knots) {
         knot.reverse(temp);
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

      for (final Knot2 knot : this.knots) {
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
    * @see Knot2#scale(float)
    */
   @Chainable
   public Curve2 scale ( final float scale ) {

      for (final Knot2 knot : this.knots) {
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
    * @see Knot2#scale(Vec2)
    */
   @Chainable
   public Curve2 scale ( final Vec2 scale ) {

      for (final Knot2 knot : this.knots) {
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
   public Curve2 sort () {

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
   public Curve2 sort ( final Comparator < Knot2 > comparator ) {

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

      //TODO: Switch to while itr has next
      for (final Iterator < Curve2.Knot2 > itr = this.knots.iterator(); itr
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
         currKnot = this.knots.get(i % knotLength);

         result.append(" C ")
               .append(prevKnot.foreHandle.toSvgString())
               .append(",")
               .append(currKnot.rearHandle.toSvgString())
               .append(",")
               .append(currKnot.coord.toSvgString());

         prevKnot = currKnot;
      }

      if (this.closedLoop) {
         result.append(" Z");
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

      for (final Knot2 knot : this.knots) {
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
      this.knots.clear();
   }
}
