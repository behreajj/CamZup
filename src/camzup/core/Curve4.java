package camzup.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Implements a <a href=
 * "https://www.wikiwand.com/en/Composite_B%C3%A9zier_curve">composite</a>
 * piecewise cubic Bezier curve. Provides a function to retrieve a point
 * and tangent on a curve from a step in the range [0.0, 1.0] .
 */
public class Curve4 extends Curve implements Iterable < Knot4 > {

   /**
    * The list of knots contained by the curve.
    */
   private final ArrayList < Knot4 > knots = new ArrayList <>(
      ICurve.KNOT_CAPACITY);

   /**
    * The default constructor.
    */
   public Curve4 ( ) {}

   /**
    * Creates a curve from a collection of knots
    *
    * @param cl    whether or not the curve is closed
    * @param knots the collection of knots
    */
   public Curve4 ( final boolean cl, final Collection < Knot4 > knots ) {

      super(cl);
      this.appendAll(knots);
   }

   /**
    * Creates a curve from a comma-separated list of knots.
    *
    * @param cl    whether or not the curve is closed
    * @param knots the list of knots
    */
   public Curve4 ( final boolean cl, final Knot4... knots ) {

      super(cl);
      this.appendAll(knots);
   }

   /**
    * Constructs a copy of the source.
    *
    * @param source the source curve
    */
   public Curve4 ( final Curve2 source ) { this.set(source); }

   /**
    * Constructs a copy of the source.
    *
    * @param source the source curve
    */
   public Curve4 ( final Curve3 source ) { this.set(source); }

   /**
    * Constructs a copy of the source.
    *
    * @param source the source curve
    */
   public Curve4 ( final Curve4 source ) { this.set(source); }

   /**
    * Creates a named curve with two default knots.
    *
    * @param name the name
    */
   public Curve4 ( final String name ) { super(name); }

   /**
    * Creates a named curve from a collection of knots
    *
    * @param name  the name
    * @param cl    whether or not the curve is closed
    * @param knots the collection of knots
    */
   public Curve4 ( final String name, final boolean cl, final Collection <
      Knot4 > knots ) {

      super(name, cl);
      this.appendAll(knots);
   }

   /**
    * Creates a curve from a comma-separated list of knots.
    *
    * @param name  the name
    * @param cl    whether or not the curve is closed
    * @param knots the list of knots
    */
   public Curve4 ( final String name, final boolean cl, final Knot4... knots ) {

      super(name, cl);
      this.appendAll(knots);
   }

   /**
    * Append a knot to the curve's list of knots.
    *
    * @param knot the knot
    *
    * @return the curve
    *
    * @see List#add(Object)
    */
   public Curve4 append ( final Knot4 knot ) {

      this.knots.add(knot);
      return this;
   }

   /**
    * Append an collection of knots to the curve's list of knots.
    *
    * @param kn the collection of knots
    *
    * @return this curve.
    */
   public Curve4 appendAll ( final Collection < Knot4 > kn ) {

      final Iterator < Knot4 > knItr = kn.iterator();
      while ( knItr.hasNext() ) { this.append(knItr.next()); }

      return this;
   }

   /**
    * Append an array of knots to the curve's list of knots.
    *
    * @param kn the array of knots
    *
    * @return this curve.
    */
   public Curve4 appendAll ( final Knot4... kn ) {

      final int len = kn.length;
      for ( int i = 0; i < len; ++i ) { this.append(kn[i]); }

      return this;
   }

   /**
    * Evaluates whether a knot is contained by this curve.
    *
    * @param kn the knot
    *
    * @return the evaluation
    *
    * @see List#contains(Object)
    */
   public boolean contains ( final Knot4 kn ) {

      return this.knots.contains(kn);
   }

   /**
    * Tests this curve for equality with another object.
    *
    * @param obj the object
    *
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( !super.equals(obj) || this.getClass() != obj.getClass() ) {
         return false;
      }
      return this.equals(( Curve4 ) obj);
   }

   /**
    * Gets a knot from the curve by an index. When the curve is a closed loop,
    * the index wraps around; this means negative indices are accepted.
    *
    * @param i the index
    *
    * @return the knot
    *
    * @see List#get(int)
    * @see Utils#mod(int, int)
    */
   public Knot4 get ( final int i ) {

      final int j = this.closedLoop ? Utils.mod(i, this.knots.size()) : i;
      return this.knots.get(j);
   }

   /**
    * Gets the first knot in the curve.
    *
    * @return the knot
    *
    * @see List#get(int)
    */
   public Knot4 getFirst ( ) { return this.knots.get(0); }

   /**
    * Gets the last knot in the curve.
    *
    * @return the knot
    *
    * @see List#get(int)
    * @see List#size()
    */
   public Knot4 getLast ( ) { return this.knots.get(this.knots.size() - 1); }

   /**
    * Calculates this curve's hash code based on its knots and on whether it
    * is a closed loop.
    *
    * @return the hash
    */
   @Override
   public int hashCode ( ) {

      int hash = super.hashCode() ^ ( this.closedLoop ? 1231 : 1237 );
      hash = hash * IUtils.HASH_MUL ^ this.knots.hashCode();
      return hash;
   }

   /**
    * Inserts a knot at a given index. When the curve is a closed loop, the
    * index wraps around; this means negative indices are accepted.
    *
    * @param i    the index
    * @param knot the knot
    *
    * @return the curve
    *
    * @see Utils#mod(int, int)
    * @see List#add(int, Object)
    */
   public Curve4 insert ( final int i, final Knot4 knot ) {

      final int k = this.closedLoop ? Utils.mod(i, this.knots.size() + 1) : i;
      this.knots.add(k, knot);

      return this;
   }

   /**
    * Inserts a collection of knots at a given index. When the curve is a
    * closed loop, the index wraps around; this means negative indices are
    * accepted.
    *
    * @param i  the index
    * @param kn the knots
    *
    * @return this curve
    *
    * @see Utils#mod(int, int)
    * @see List#addAll(int, Collection)
    */
   public Curve4 insertAll ( final int i, final Collection < Knot4 > kn ) {

      final int vidx = this.closedLoop ? Utils.mod(i, this.knots.size() + 1)
         : i;
      this.knots.addAll(vidx, kn);
      return this;
   }

   /**
    * Inserts a list of knots at a given index. When the curve is a closed
    * loop, the index wraps around; this means negative indices are accepted.
    *
    * @param i  the index
    * @param kn the knots
    *
    * @return this curve
    *
    * @see Utils#mod(int, int)
    * @see List#add(int, Object)
    */
   public Curve4 insertAll ( final int i, final Knot4... kn ) {

      final int len = kn.length;
      final int vidx = this.closedLoop ? Utils.mod(i, this.knots.size() + 1)
         : i;
      int k = vidx;
      for ( int j = 0; j < len; ++j ) {
         final Knot4 knot = kn[j];
         this.knots.add(k, knot);
         ++k;
      }

      return this;
   }

   /**
    * Returns an iterator, which allows an enhanced for-loop to access the
    * knots in a curve.
    *
    * @return the iterator
    *
    * @see List#iterator()
    */
   @Override
   public Iterator < Knot4 > iterator ( ) { return this.knots.iterator(); }

   /**
    * Gets the number of knots in the curve.
    *
    * @return the knot count
    *
    * @see List#size()
    */
   @Override
   public int length ( ) { return this.knots.size(); }

   /**
    * Prepend a knot to the curve's list of knots.
    *
    * @param knot the knot
    *
    * @return the curve
    *
    * @see List#add(int, Object)
    */
   public Curve4 prepend ( final Knot4 knot ) {

      this.knots.add(0, knot);
      return this;
   }

   /**
    * Prepend a collection of knots to the curve's list of knots.
    *
    * @param kn the collection of knots
    *
    * @return this curve.
    *
    * @see List#addAll(int, Collection)
    */
   public Curve4 prependAll ( final Collection < Knot4 > kn ) {

      this.knots.addAll(0, kn);
      return this;
   }

   /**
    * Prepend an array of knots to the curve's list of knots.
    *
    * @param kn the array of knots
    *
    * @return this curve.
    *
    * @see List#add(int, Object)
    */
   public Curve4 prependAll ( final Knot4... kn ) {

      int j = 0;
      final int len = kn.length;
      for ( int i = 0; i < len; ++i ) {
         final Knot4 knot = kn[i];
         this.knots.add(j, knot);
         ++j;
      }
      return this;
   }

   /**
    * Returns and removes a knot at a given index.
    *
    * @param i the index
    *
    * @return the knot
    *
    * @see Utils#mod(int, int)
    * @see List#remove(int)
    * @see List#size()
    */
   public Knot4 removeAt ( final int i ) {

      final int j = this.closedLoop ? Utils.mod(i, this.knots.size()) : i;
      return this.knots.remove(j);
   }

   /**
    * Returns and removes the first knot in the curve.
    *
    * @return the knot
    *
    * @see List#remove(int)
    */
   public Knot4 removeFirst ( ) { return this.knots.remove(0); }

   /**
    * Removes and returns the last knot in the curve.
    *
    * @return the knot
    *
    * @see List#remove(int)
    */
   public Knot4 removeLast ( ) {

      return this.knots.remove(this.knots.size() - 1);
   }

   /**
    * Reverses the curve. This is done by reversing the list of knots and
    * swapping the fore- and rear-handle of each knot.
    *
    * @return this curve
    *
    * @see Collections#reverse(List)
    * @see Knot4#reverse()
    */
   public Curve4 reverse ( ) {

      Collections.reverse(this.knots);
      final Iterator < Knot4 > itr = this.knots.iterator();
      while ( itr.hasNext() ) { itr.next().reverse(); }

      return this;
   }

   /**
    * Scales all knots in the curve by a scalar.
    *
    * @param scale the scale
    *
    * @return this curve
    *
    * @see Knot4#scale(float)
    */
   public Curve4 scale ( final float scale ) {

      if ( scale != 0.0f ) {
         final Iterator < Knot4 > itr = this.knots.iterator();
         while ( itr.hasNext() ) { itr.next().scale(scale); }
      }

      return this;
   }

   /**
    * Scales all knots in the curve by a vector.
    *
    * @param scale the scale
    *
    * @return this curve
    *
    * @see Vec4#all(Vec4)
    * @see Knot4#scale(Vec4)
    */
   public Curve4 scale ( final Vec4 scale ) {

      if ( Vec4.all(scale) ) {
         final Iterator < Knot4 > itr = this.knots.iterator();
         while ( itr.hasNext() ) { itr.next().scale(scale); }
      }

      return this;
   }

   /**
    * Sets this curve to a copy of the source.
    *
    * @param source the source curve
    *
    * @return this curve
    */
   public Curve4 set ( final Curve2 source ) {

      this.resize(source.length());
      final Iterator < Knot2 > srcItr = source.iterator();
      final Iterator < Knot4 > trgItr = this.knots.iterator();
      while ( srcItr.hasNext() ) { trgItr.next().set(srcItr.next()); }

      this.closedLoop = source.closedLoop;
      this.materialIndex = source.materialIndex;

      return this;
   }

   /**
    * Sets this curve to a copy of the source.
    *
    * @param source the source curve
    *
    * @return this curve
    */
   public Curve4 set ( final Curve3 source ) {

      this.resize(source.length());
      final Iterator < Knot3 > srcItr = source.iterator();
      final Iterator < Knot4 > trgItr = this.knots.iterator();
      while ( srcItr.hasNext() ) { trgItr.next().set(srcItr.next()); }

      this.closedLoop = source.closedLoop;
      this.materialIndex = source.materialIndex;

      return this;
   }

   /**
    * Sets this curve to a copy of the source.
    *
    * @param source the source curve
    *
    * @return this curve
    */
   public Curve4 set ( final Curve4 source ) {

      this.resize(source.length());
      final Iterator < Knot4 > srcItr = source.iterator();
      final Iterator < Knot4 > trgItr = this.knots.iterator();
      while ( srcItr.hasNext() ) { trgItr.next().set(srcItr.next()); }

      this.closedLoop = source.closedLoop;
      this.materialIndex = source.materialIndex;

      return this;
   }

   /**
    * Returns a string representation of the curve.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

   /**
    * Returns a string representation of the curve.
    *
    * @param places the number of places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(64 + 512 * this.knots.size());
      sb.append("{ name: \"");
      sb.append(this.name);
      sb.append("\", closedLoop: ");
      sb.append(this.closedLoop);
      sb.append(", materialIndex: ");
      sb.append(this.materialIndex);
      sb.append(", knots: [ ");

      final Iterator < Knot4 > itr = this.knots.iterator();
      while ( itr.hasNext() ) {
         itr.next().toString(sb, places);
         if ( itr.hasNext() ) { sb.append(',').append(' '); }
      }

      sb.append(" ] }");
      return sb.toString();
   }

   /**
    * Translates all knots in the curve by a vector.
    *
    * @param v the vector
    *
    * @return this curve
    *
    * @see Knot4#translate(Vec4)
    */
   public Curve4 translate ( final Vec4 v ) {

      final Iterator < Knot4 > itr = this.knots.iterator();
      while ( itr.hasNext() ) { itr.next().translate(v); }

      return this;
   }

   /**
    * For internal (package-level) use. Resizes a curve to the specified
    * length. The length may be no less than 2. When the new length is greater
    * than the old, new {@link Knot3}s are added.<br>
    * <br>
    * This does not check if remaining elements in the list are
    * <code>null</code>.
    *
    * @param len the length
    *
    * @return this curve
    *
    * @see List#add(Object)
    * @see List#remove(int)
    */
   Curve4 resize ( final int len ) {

      final int vlen = len < 2 ? 2 : len;
      final int oldLen = this.knots.size();
      final int diff = vlen - oldLen;
      if ( diff < 0 ) {
         final int last = oldLen - 1;
         for ( int i = 0; i < -diff; ++i ) { this.knots.remove(last - i); }
      } else if ( diff > 0 ) {
         for ( int i = 0; i < diff; ++i ) { this.knots.add(new Knot4()); }
      }

      return this;
   }

   /**
    * Clears the list of knots and sets the closedLoop flag to false. Unlike
    * the public reset, this does not add two default knots to the list.
    *
    * @see List#clear()
    */
   protected void clear ( ) {

      this.closedLoop = false;
      this.name = this.hashIdentityString();
      this.knots.clear();
   }

   /**
    * Tests this curve for equality with another.
    *
    * @param curve the curve
    *
    * @return the evaluation
    */
   protected boolean equals ( final Curve4 curve ) {

      return this.closedLoop == curve.closedLoop && this.knots.equals(
         curve.knots);
   }

   /**
    * Evaluates a step in the range [0.0, 1.0] , returning a knot on the
    * curve.
    *
    * @param curve  the curve
    * @param step   the step
    * @param target the output knot
    *
    * @return the knot
    */
   @Experimental
   public static Knot4 eval ( final Curve4 curve, final float step,
      final Knot4 target ) {

      final ArrayList < Knot4 > knots = curve.knots;
      final int knotLength = knots.size();

      float tScaled;
      int i = 0;
      Knot4 a = null;
      Knot4 b = null;
      if ( curve.closedLoop ) {
         tScaled = Utils.mod1(step) * knotLength;
         i = ( int ) tScaled;
         a = knots.get(Utils.mod(i, knotLength));
         b = knots.get(Utils.mod(i + 1, knotLength));
      } else {
         if ( knotLength == 1 || step <= 0.0f ) {
            Curve4.evalFirst(curve, target);
            return target;
         }
         if ( step >= 1.0f ) {
            Curve4.evalLast(curve, target);
            return target;
         }

         tScaled = step * ( knotLength - 1 );
         i = ( int ) tScaled;
         a = knots.get(i);
         b = knots.get(i + 1);
      }

      final float t = tScaled - i;
      final float u = 1.0f - t;
      Curve4.bezierKnot(a, b, t, target);

      // QUERY Should this logic be moved to bezierKnot? Can this use magSq then
      // find sqrt after the lerp?
      final float aFhMag = Knot4.foreMag(a);
      final float bFhMag = Knot4.foreMag(b);
      final float tFhMag = u * aFhMag + t * bFhMag;
      target.scaleForeHandleTo(tFhMag);

      final float aRhMag = Knot4.rearMag(a);
      final float bRhMag = Knot4.rearMag(b);
      final float tRhMag = u * aRhMag + t * bRhMag;
      target.scaleRearHandleTo(tRhMag);

      return target;
   }

   /**
    * Evaluates a step in the range [0.0, 1.0] , returning a coordinate on the
    * curve and a tangent. The tangent will be normalized, to be of unit
    * length.
    *
    * @param curve   the curve
    * @param step    the step
    * @param coord   the output coordinate
    * @param tangent the output tangent
    *
    * @return the coordinate
    *
    * @see Knot4#bezierPoint(Knot4, Knot4, float, Vec4)
    * @see Knot4#bezierTanUnit(Knot4, Knot4, float, Vec4)
    */
   public static Vec4 eval ( final Curve4 curve, final float step,
      final Vec4 coord, final Vec4 tangent ) {

      final ArrayList < Knot4 > knots = curve.knots;
      final int knotLength = knots.size();

      float tScaled;
      int i = 0;
      Knot4 a = null;
      Knot4 b = null;
      if ( curve.closedLoop ) {
         tScaled = Utils.mod1(step) * knotLength;
         i = ( int ) tScaled;
         a = knots.get(Utils.mod(i, knotLength));
         b = knots.get(Utils.mod(i + 1, knotLength));
      } else {
         if ( knotLength == 1 || step <= 0.0f ) {
            return Curve4.evalFirst(curve, coord, tangent);
         }
         if ( step >= 1.0f ) { return Curve4.evalLast(curve, coord, tangent); }

         tScaled = step * ( knotLength - 1 );
         i = ( int ) tScaled;
         a = knots.get(i);
         b = knots.get(i + 1);
      }

      final float t = tScaled - i;
      Knot4.bezierPoint(a, b, t, coord);
      Knot4.bezierTanUnit(a, b, t, tangent);

      return coord;
   }

   /**
    * Evaluates the first knot in the curve.
    *
    * @param curve  the curve
    * @param target the output knot
    *
    * @return the knot
    */
   public static Knot4 evalFirst ( final Curve4 curve, final Knot4 target ) {

      target.set(curve.knots.get(0));
      return target;
   }

   /**
    * Evaluates the first knot in the curve. The tangent will be normalized,
    * to be of unit length.
    *
    * @param curve   the curve
    * @param coord   the output coordinate
    * @param tangent the output tangent
    *
    * @return the coordinate
    *
    * @see Vec4#sub(Vec4, Vec4, Vec4)
    * @see Vec4#normalize(Vec4, Vec4)
    */
   public static Vec4 evalFirst ( final Curve4 curve, final Vec4 coord,
      final Vec4 tangent ) {

      final Knot4 kFirst = curve.knots.get(0);
      coord.set(kFirst.coord);
      Vec4.sub(kFirst.foreHandle, coord, tangent);
      Vec4.normalize(tangent, tangent);

      return coord;
   }

   /**
    * Evaluates the last knot in the curve.
    *
    * @param curve  the curve
    * @param target the output knot
    *
    * @return the knot
    */
   public static Knot4 evalLast ( final Curve4 curve, final Knot4 target ) {

      target.set(curve.knots.get(curve.knots.size() - 1));
      return target;
   }

   /**
    * Evaluates the last knot in the curve. The tangent will be normalized, to
    * be of unit length.
    *
    * @param curve   the curve
    * @param coord   the output coordinate
    * @param tangent the output tangent
    *
    * @return the coordinate
    *
    * @see Vec4#sub(Vec4, Vec4, Vec4)
    * @see Vec4#normalize(Vec4, Vec4)
    */
   public static Vec4 evalLast ( final Curve4 curve, final Vec4 coord,
      final Vec4 tangent ) {

      final Knot4 kLast = curve.knots.get(curve.knots.size() - 1);
      coord.set(kLast.coord);
      Vec4.sub(coord, kLast.rearHandle, tangent);
      Vec4.normalize(tangent, tangent);

      return coord;
   }

   /**
    * Converts a set of points on a Catmull-Rom spline to a Bezier curve. The
    * default tightness is 0.0. There must be at least 4 points in the array.
    *
    * @param closedLoop the closed loop flag
    * @param tightness  the curve tightness
    * @param points     the points
    * @param target     the output curve
    *
    * @return the conversion
    *
    * @see Knot4#fromSegCatmull(Vec4, Vec4, Vec4, Vec4, float, Knot4, Knot4)
    * @see Knot4#mirrorHandlesForward()
    * @see Knot4#mirrorHandlesBackward()
    */
   public static Curve4 fromCatmull ( final boolean closedLoop,
      final Vec4[] points, final float tightness, final Curve4 target ) {

      final int ptsLen = points.length;
      if ( ptsLen < 4 ) { return target; }

      target.closedLoop = closedLoop;
      target.name = "Catmull";

      final int knotCount = closedLoop ? ptsLen : ptsLen - 2;
      target.resize(knotCount);
      final Iterator < Knot4 > itr = target.iterator();
      final Knot4 first = itr.next();
      final int ptsLast = ptsLen - 1;

      int idx = 0;
      Knot4 prev = first;
      Knot4 curr;
      for ( curr = null; itr.hasNext(); ++idx ) {
         int idx1 = idx + 1;
         int idx2 = idx + 2;
         int idx3 = idx + 3;

         if ( closedLoop ) {
            idx1 %= ptsLen;
            idx2 %= ptsLen;
            idx3 %= ptsLen;
         } else {
            idx3 = idx3 < ptsLast ? idx3 : ptsLast;
         }

         curr = itr.next();
         Knot4.fromSegCatmull(points[idx], points[idx1], points[idx2],
            points[idx3], tightness, prev, curr);
         prev = curr;
      }

      if ( closedLoop ) {
         Knot4.fromSegCatmull(points[ptsLast], points[0], points[1], points[2],
            tightness, curr, first);
      } else if ( curr != null ) {
         first.coord.set(points[1]);
         first.mirrorHandlesForward();
         curr.mirrorHandlesBackward();
      }

      return target;
   }

   /**
    * Creates a curve from a series of points. Smoothes the fore- and
    * rear-handles of each knot.
    *
    * @param closedLoop whether the curve is a closed loop
    * @param points     the array of points
    * @param target     the output curve
    *
    * @return the curve
    */
   public static Curve4 fromPoints ( final boolean closedLoop,
      final Vec4[] points, final Curve4 target ) {

      target.closedLoop = closedLoop;
      target.name = "Constellation";
      target.resize(points.length);
      int incr = 0;
      final Iterator < Knot4 > itr = target.knots.iterator();
      for ( ; itr.hasNext(); ++incr ) {
         final Vec4 pt = points[incr];
         itr.next().set(pt, pt, pt);
      }

      return Curve4.smoothHandles(target);
   }

   /**
    * Creates a curve that forms a line with an origin and destination.
    *
    * @param origin the origin
    * @param dest   the destination
    * @param target the output curve
    *
    * @return the line
    */
   public static Curve4 line ( final Vec4 origin, final Vec4 dest,
      final Curve4 target ) {

      return Curve4.line(origin.x, origin.y, origin.z, origin.w, dest.x, dest.y,
         dest.z, dest.w, target);
   }

   /**
    * Creates a random curve. Generates random points, creates a curve from
    * those points, then smoothes the knots' handles.
    *
    * @param rng        the random number generator
    * @param count      the number of knots to generate
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    * @param closedLoop whether the curve is a closed loop
    * @param target     the target curve
    *
    * @return the random curve
    *
    * @see Vec4#randomCartesian(Random, float, float, Vec4)
    * @see Curve4#fromPoints(boolean, Vec4[], Curve4)
    */
   public static Curve4 random ( final Random rng, final int count,
      final float lowerBound, final float upperBound, final boolean closedLoop,
      final Curve4 target ) {

      final int valCount = count < 3 ? 3 : count;
      final Vec4[] points = new Vec4[valCount];
      for ( int i = 0; i < valCount; ++i ) {
         points[i] = Vec4.randomCartesian(rng, lowerBound, upperBound,
            new Vec4());
      }

      return Curve4.fromPoints(closedLoop, points, target);
   }

   /**
    * Creates a random curve. Generates random points, creates a curve from
    * those points, then smoothes the knots' handles.
    *
    * @param rng        the random number generator
    * @param count      the number of knots to generate
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    * @param closedLoop whether the curve is a closed loop
    * @param target     the target curve
    *
    * @return the random curve
    *
    * @see Vec4#randomCartesian(Random, Vec4, Vec4, Vec4)
    * @see Curve4#fromPoints(boolean, Vec4[], Curve4)
    */
   public static Curve4 random ( final Random rng, final int count,
      final Vec4 lowerBound, final Vec4 upperBound, final boolean closedLoop,
      final Curve4 target ) {

      final int valCount = count < 3 ? 3 : count;
      final Vec4[] points = new Vec4[valCount];
      for ( int i = 0; i < valCount; ++i ) {
         points[i] = Vec4.randomCartesian(rng, lowerBound, upperBound,
            new Vec4());
      }

      return Curve4.fromPoints(closedLoop, points, target);
   }

   /**
    * Samples a segment from a source curve into a target.
    *
    * @param i      the index
    * @param source the input curve
    * @param target the output curve
    *
    * @return the segment
    */
   public static Curve4 sampleSegment ( final int i, final Curve4 source,
      final Curve4 target ) {

      target.closedLoop = false;
      target.resize(2);

      final ArrayList < Knot4 > targetKnots = target.knots;
      final ArrayList < Knot4 > sourceKnots = source.knots;
      final int len = sourceKnots.size();

      Knot4 a = null;
      Knot4 b = null;

      if ( source.closedLoop ) {
         a = sourceKnots.get(Utils.mod(i, len));
         b = sourceKnots.get(Utils.mod(i + 1, len));
      } else if ( i > -1 && i < len - 1 ) {
         a = sourceKnots.get(i);
         b = sourceKnots.get(i + 1);
      } else {
         return target;
      }

      targetKnots.get(0).set(a);
      targetKnots.get(1).set(b);

      return target;
   }

   /**
    * Adjusts knot handles so as to create a smooth, continuous curve.
    *
    * @param target the output curve
    *
    * @return the curve
    *
    * @see Knot2#smoothHandles(Knot2, Knot2, Knot2, Vec2)
    * @see Knot2#smoothHandlesLast(Knot2, Knot2, Vec2)
    * @see Knot2#smoothHandlesFirst(Knot2, Knot2, Vec2)
    * @see Knot2#mirrorHandlesForward()
    * @see Knot2#mirrorHandlesBackward()
    */
   public static Curve4 smoothHandles ( final Curve4 target ) {

      final ArrayList < Knot4 > knots = target.knots;
      final int knotLength = knots.size();
      if ( knotLength < 3 ) { return target; }

      final int knotLast = knotLength - 1;
      final Vec4 carry = new Vec4();
      final Iterator < Knot4 > itr = knots.iterator();
      final Knot4 first = itr.next();

      if ( target.closedLoop ) {

         Knot4 prev = knots.get(knotLast);
         Knot4 curr = first;
         while ( itr.hasNext() ) {
            final Knot4 next = itr.next();
            Knot4.smoothHandles(prev, curr, next, carry);
            prev = curr;
            curr = next;
         }
         Knot4.smoothHandles(prev, curr, first, carry);

      } else {

         Knot4 prev = first;
         Knot4 curr = itr.next();
         Knot4.smoothHandlesFirst(prev, curr, carry).mirrorHandlesForward();

         while ( itr.hasNext() ) {
            final Knot4 next = itr.next();
            Knot4.smoothHandles(prev, curr, next, carry);
            prev = curr;
            curr = next;
         }

         Knot4.smoothHandlesLast(prev, curr, carry).mirrorHandlesBackward();
      }

      return target;
   }

   /**
    * Adjusts knot handles so as to create straight line segments.
    *
    * @param target the output curve
    *
    * @return the curve
    *
    * @see Curve2#lerp13(Vec2, Vec2, Vec2)
    * @see Knot2#mirrorHandlesBackward()
    * @see Knot2#mirrorHandlesForward()
    */
   public static Curve4 straightenHandles ( final Curve4 target ) {

      final ArrayList < Knot4 > knots = target.knots;
      final int knotLength = knots.size();

      if ( knotLength < 2 ) { return target; }
      if ( knotLength < 3 ) {
         final Knot4 first = knots.get(0);
         final Knot4 last = knots.get(1);

         Curve4.lerp13(first.coord, last.coord, first.foreHandle);
         first.mirrorHandlesForward();

         Curve4.lerp13(last.coord, first.coord, last.rearHandle);
         last.mirrorHandlesBackward();

         return target;
      }

      final Iterator < Knot4 > itr = knots.iterator();
      final Knot4 first = itr.next();
      Knot4 prev = null;
      Knot4 curr = first;
      while ( itr.hasNext() ) {
         prev = curr;
         curr = itr.next();
         Curve4.lerp13(prev.coord, curr.coord, prev.foreHandle);
         Curve4.lerp13(curr.coord, prev.coord, curr.rearHandle);
      }

      if ( target.closedLoop ) {
         Curve4.lerp13(first.coord, curr.coord, first.rearHandle);
         Curve4.lerp13(curr.coord, first.coord, curr.foreHandle);
      } else {
         first.mirrorHandlesForward();
         curr.mirrorHandlesBackward();
      }

      return target;
   }

   /**
    * A helper function. Returns a knot given two knots and a step. Assumes
    * the step has already been checked, and that the knots are in sequence
    * along the curve.
    *
    * @param a      the origin knot
    * @param b      the destination knot
    * @param step   the step
    * @param target the output knot
    *
    * @return the knot
    *
    * @see Vec4#bezierPoint(Vec4, Vec4, Vec4, Vec4, float, Vec4)
    * @see Vec4#bezierTangent(Vec4, Vec4, Vec4, Vec4, float, Vec4)
    * @see Vec4#add(Vec4, Vec4, Vec4)
    */
   @Experimental
   static Knot4 bezierKnot ( final Knot4 a, final Knot4 b, final float step,
      final Knot4 target ) {

      final Vec4 aco = a.coord;
      final Vec4 afh = a.foreHandle;
      final Vec4 bco = b.coord;
      final Vec4 brh = b.rearHandle;
      final Vec4 tco = target.coord;
      final Vec4 trh = target.rearHandle;
      final Vec4 tfh = target.foreHandle;

      Vec4.bezierPoint(aco, afh, brh, bco, step, tco);
      Vec4.bezierTangent(aco, afh, brh, bco, step, tfh);

      /* Find rear handle by reversing directions. */
      Vec4.bezierTangent(bco, brh, afh, aco, 1.0f - step, trh);

      /* Convert fore and rear handle from direction to point. */
      Vec4.add(tco, tfh, tfh);
      Vec4.add(tco, trh, trh);

      return target;
   }

   /**
    * A utility function for setting the handles of knots on straight curve
    * segments. Finds unclamped linear interpolation from origin to
    * destination by a step of 1.0 / 3.0 .
    *
    * @param a      the origin
    * @param b      the destination
    * @param target the target
    *
    * @return the result
    */
   static Vec4 lerp13 ( final Vec4 a, final Vec4 b, final Vec4 target ) {

      return target.set(IUtils.TWO_THIRDS * a.x + IUtils.ONE_THIRD * b.x,
         IUtils.TWO_THIRDS * a.y + IUtils.ONE_THIRD * b.y, IUtils.TWO_THIRDS
            * a.z + IUtils.ONE_THIRD * b.z, IUtils.TWO_THIRDS * a.w
               + IUtils.ONE_THIRD * b.w);
   }

   /**
    * Creates a curve that forms a line with an origin and destination.
    *
    * @param xOrigin the origin x
    * @param yOrigin the origin y
    * @param zOrigin the origin z
    * @param wOrigin the origin w
    * @param xDest   the destination x
    * @param yDest   the destination y
    * @param zDest   the destination z
    * @param wDest   the destination w
    * @param target  the output curve
    *
    * @return the line
    *
    * @see Curve4#lerp13(Vec4, Vec4, Vec4)
    * @see Knot4#mirrorHandlesForward()
    * @see Knot4#mirrorHandlesBackward()
    */
   static Curve4 line ( final float xOrigin, final float yOrigin,
      final float zOrigin, final float wOrigin, final float xDest,
      final float yDest, final float zDest, final float wDest,
      final Curve4 target ) {

      target.resize(2);

      final Knot4 first = target.knots.get(0);
      final Knot4 last = target.knots.get(1);

      first.coord.set(xOrigin, yOrigin, zOrigin, wOrigin);
      last.coord.set(xDest, yDest, zDest, wDest);

      Curve4.lerp13(first.coord, last.coord, first.foreHandle);
      Curve4.lerp13(last.coord, first.coord, last.rearHandle);

      first.mirrorHandlesForward();
      last.mirrorHandlesBackward();

      target.name = "Line";
      target.closedLoop = false;
      return target;
   }

}
