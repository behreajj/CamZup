package camzup.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Organizes a Bezier curve into a list of knots. Provides a
 * function to retrieve a point and tangent on a curve from
 * a step in the range [0.0, 1.0].
 */
public class Curve3 extends Curve
      implements Iterable < Knot3 > {

   /**
    * Groups together vectors which shape a Bezier curve into a
    * coordinate (or anchor point), fore handle (the following
    * control point) and rear handle (the preceding control
    * point).
    */

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
      // final float cost = SinCos.eval(hndtn);
      // final float handleMag = cost == 0.0f ? 0.0f
      // : SinCos.eval(hndtn - 0.25f) / cost
      // * radius * IUtils.FOUR_THIRDS;
      final float handleMag = Utils.tan(hndtn * IUtils.TAU) * radius
            * IUtils.FOUR_THIRDS;

      final List < Knot3 > knots = target.knots;
      knots.clear();
      for (int i = 0; i < knotCount; ++i) {
         final float angle1 = Utils.lerpUnclamped(
               a1, destAngle1, i * toStep);
         knots.add(
               Knot3.fromPolar(
                     angle1 * IUtils.TAU,
                     radius, handleMag,
                     new Knot3()));
      }

      target.closedLoop = arcMode != ArcMode.OPEN;
      if (target.closedLoop) {
         if (arcMode == ArcMode.CHORD) {

            final Knot3 first = knots.get(0);
            final Knot3 last = knots.get(knots.size() - 1);

            Curve3.lerp13(last.coord, first.coord, last.foreHandle);
            Curve3.lerp13(first.coord, last.coord, first.rearHandle);

         } else if (arcMode == ArcMode.PIE) {

            final Knot3 first = knots.get(0);
            final Knot3 last = knots.get(knots.size() - 1);
            final Knot3 center = new Knot3();

            knots.add(center);

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
    * Calculates the approximate length of a curve to a given
    * level of precision.
    *
    * @param c
    *           the curve
    * @param precision
    *           the precision
    * @return the length
    * @see Curve3#evalRange(int)
    */
   public static float calcSegLength (
         final Curve3 c,
         final int precision ) {

      float sum = 0.0f;
      final Vec3[][] segments = c.evalRange(precision + 1);
      final int len = segments.length;
      for (int i = 1, j = 0; i < len; ++i, ++j) {
         sum += Vec3.dist(
               segments[j][0],
               segments[i][0]);
      }

      return sum;
   }

   /**
    * Calculates the approximates lengths of segments
    * approximating a curve to a given precision.
    *
    * @param c
    *           the curve
    * @param precision
    *           the precision
    * @return the segment lengths
    */
   public static float[] calcSegLengths (
         final Curve3 c,
         final int precision ) {

      // TODO: Needs testing.

      final Vec3[][] segments = c.evalRange(precision + 1);
      final int len = segments.length;
      final float[] results = new float[precision];
      for (int i = 1, j = 0; i < len; ++i, ++j) {
         results[j] = Vec3.dist(
               segments[j][0],
               segments[i][0]);
      }

      return results;
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
      // final float cost = SinCos.eval(hndtn);
      // final float handleMag = cost == 0.0f ? 0.0f
      // : SinCos.eval(hndtn - 0.25f) / cost
      // * radius * IUtils.FOUR_THIRDS;
      final float handleMag = Utils.tan(hndtn * IUtils.TAU) * radius
            * IUtils.FOUR_THIRDS;

      final List < Knot3 > knots = target.knots;
      for (int i = 0; i < vknct; ++i) {
         final float angle1 = offset1 + i * invKnCt;
         knots.add(
               Knot3.fromPolar(
                     angle1 * IUtils.TAU,
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

      target.clear();
      target.closedLoop = closedLoop;
      final List < Knot3 > knots = target.knots;
      final int knotCount = points.length;
      for (int i = 0; i < knotCount; ++i) {
         final Vec3 point = points[i];
         knots.add(new Knot3(point, point, point));
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
      final float toAngle = IUtils.TAU / vknct;
      final List < Knot3 > knots = target.knots;
      for (int i = 0; i < vknct; ++i) {
         final float angle = offsetAngle + i * toAngle;
         final Knot3 knot = new Knot3();
         Vec3.fromPolar(angle, radius, knot.coord);
         knots.add(knot);
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
               lowerBound, upperBound, new Vec3());
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

      final List < Knot3 > knots = target.knots;
      final int knotLength = knots.size();
      if (knotLength < 3) {
         return target;
      }

      // TODO: Can this be optimized to use fewer temp vectors?
      // maybe get rid of fornorm and backnorm and reuse forward
      // and back?
      final Vec3 back = new Vec3();
      final Vec3 backNorm = new Vec3();

      final Vec3 forward = new Vec3();
      final Vec3 forNorm = new Vec3();

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

            final Knot3 prev = knots.get(
                  Math.floorMod(i - 1, knotLength));

            Vec3.sub(prev.coord, currCoord, back);
            backDist = Vec3.mag(back);
            Vec3.normalize(back, backNorm);
            Vec3.add(dir0, backNorm, dir1);

            final Knot3 next = knots.get(
                  (i + 1) % knotLength);

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

         final Vec3 rh = knot.rearHandle;
         Vec3.mul(dir0, backDist, rh);
         Vec3.add(rh, currCoord, rh);

         final Vec3 fh = knot.foreHandle;
         Vec3.mul(dir0, foreDist, fh);
         Vec3.add(fh, currCoord, fh);
      }

      /*
       * Match fore and rear handles of first and last knots if
       * the curve is not closed.
       */
      if (!closedLoop) {
         knots.get(0).mirrorHandlesForward();
         knots.get(knotLength - 1).mirrorHandlesBackward();
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

      final List < Knot3 > knots = target.knots;
      final int knotLength = knots.size();
      if (knotLength < 2) {
         return target;
      }

      if (knotLength == 2) {
         final Knot3 first = knots.get(0);
         final Knot3 last = knots.get(knotLength - 1);

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
         final Knot3 first = knots.get(0);
         final Knot3 last = knots.get(knotLength - 1);
         Curve3.lerp13(first.coord, last.coord, first.rearHandle);
         Curve3.lerp13(last.coord, first.coord, last.foreHandle);
      } else {
         knots.get(0).mirrorHandlesForward();
         knots.get(knotLength - 1).mirrorHandlesBackward();
      }

      return target;
   }

   /**
    * The list of knots contained by the curve.
    */
   private final List < Knot3 > knots;

   /**
    * Whether or not the curve is a closed loop.
    */
   public boolean closedLoop = false;

   /**
    * The material associated with this curve in a curve
    * entity.
    */
   public int materialIndex = 0;

   {
      /*
       * Seems to perform better when the class is instead of the
       * interface. Problem is that it's hard to decide one
       * whether to use an array or linked list.
       */

      // knots = new LinkedList <>();
      this.knots = new ArrayList <>();
   }

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
    * Returns a String of Python code targeted toward the
    * Blender 2.8x API. This code is brittle and is used for
    * internal testing purposes, i.e., to compare how curve
    * geometry looks in Blender (the control) vs. in the
    * library (the test).
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode () {

      final StringBuilder sb = new StringBuilder();
      sb.append("{\"closed_loop\": ")
            .append(this.closedLoop ? "True" : "False")
            .append(", \"knots\": [");
      final Iterator < Knot3 > itr = this.knots.iterator();
      int i = 0;
      final int last = this.knots.size() - 1;
      while (itr.hasNext()) {
         sb.append(itr.next().toBlenderCode());
         if (i < last) {
            sb.append(',').append(' ');
         }
         i++;
      }

      sb.append(']').append('}');
      return sb.toString();
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
    * Append an collection of knots to the curve's list of
    * knots.
    *
    * @param knots
    *           the collection of knots
    * @return this curve.
    */
   public Curve3 append ( final Collection < Knot3 > knots ) {

      final Iterator < Knot3 > knItr = knots.iterator();
      while (knItr.hasNext()) {
         final Knot3 knot = knItr.next();
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
   public Curve3 append ( final Knot3 knot ) {

      if (knot != null) {
         this.knots.add(knot);
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
            this.knots.add(knot);
         }
      }
      return this;
   }

   @Override
   public boolean equals ( final Object obj ) {

      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
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
            return target.set(this.knots.get(0));
         }
         if (step >= 1.0f) {
            return target.set(this.knots.get(knotLength - 1));
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

      final Knot3 kFirst = this.knots.get(0);
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

      final Knot3 kLast = this.knots.get(this.knots.size() - 1);
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

      return this.knots.get(0);
   }

   /**
    * Gets the last knot in the curve.
    *
    * @return the knot
    * @see LinkedList#getLast()
    */
   public Knot3 getLast () {

      return this.knots.get(this.knots.size() - 1);
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
   public Iterator < Knot3 > iterator () {

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

      return this.knots.remove(0);
   }

   /**
    * Removes and returns the last knot in the curve.
    *
    * @return the knot
    */
   public Knot3 removeLast () {

      return this.knots.remove(this.knots.size() - 1);
   }

   /**
    * Resets the curve, leaving two default knots.
    *
    * @return this curve
    */
   @Chainable
   public Curve3 reset () {

      this.knots.clear();
      this.knots.add(
            new Knot3(
                  -0.5f, 0.0f, 0.0f,
                  -0.25f, 0.25f, 0.0f,
                  -0.75f, -0.25f, 0.0f));
      this.knots.add(
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

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

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

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

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

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

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

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

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

      final StringBuilder sb = new StringBuilder(
            64 + 256 * this.knots.size())
                  .append("{ name: \"")
                  .append(this.name)
                  .append("\"\n, closedLoop: ")
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
