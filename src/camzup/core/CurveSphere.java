package camzup.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Organizes a cubic Bezier curve of quaternions into a list of knots.
 * Provides a function to retrieve a quaternion on a curve from a step in
 * the range [0.0, 1.0] .
 */
@Experimental
public class CurveSphere extends Curve implements Iterable < KnotSphere > {

   /**
    * The list of knots contained by the curve.
    */
   private final ArrayList < KnotSphere > knots = new ArrayList <>(
      ICurve.KNOT_CAPACITY);

   /**
    * The default constructor.
    */
   public CurveSphere ( ) {

      // TODO: Implement all curve features.
   }

   /**
    * Creates a curve from a collection of knots
    *
    * @param cl    whether or not the curve is closed
    * @param knots the collection of knots
    */
   public CurveSphere ( final boolean cl, final Collection <
      KnotSphere > knots ) {

      super(cl);
      this.appendAll(knots);
   }

   /**
    * Creates a curve from a comma-separated list of knots.
    *
    * @param cl    whether or not the curve is closed
    * @param knots the list of knots
    */
   public CurveSphere ( final boolean cl, final KnotSphere... knots ) {

      super(cl);
      this.appendAll(knots);
   }

   /**
    * Creates a named curve with two default knots.
    *
    * @param name the name
    */
   public CurveSphere ( final String name ) { super(name); }

   /**
    * Append a knot to the curve's list of knots.
    *
    * @param knot the knot
    *
    * @return the curve
    *
    * @see List#add(Object)
    */
   public CurveSphere append ( final KnotSphere knot ) {

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
   public CurveSphere appendAll ( final Collection < KnotSphere > kn ) {

      final Iterator < KnotSphere > knItr = kn.iterator();
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
   public CurveSphere appendAll ( final KnotSphere... kn ) {

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
    */
   public boolean contains ( final KnotSphere kn ) {

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
      if ( !super.equals(obj) ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( CurveSphere ) obj);
   }

   /**
    * Gets a knot from the curve by an index. When the curve is a closed loop,
    * the index wraps around.
    *
    * @param i the index
    *
    * @return the knot
    *
    * @see List#get(int)
    * @see Utils#mod(int, int)
    */
   public KnotSphere get ( final int i ) {

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
   public KnotSphere getFirst ( ) { return this.knots.get(0); }

   /**
    * Gets the last knot in the curve.
    *
    * @return the knot
    *
    * @see List#get(int)
    * @see List#size()
    */
   public KnotSphere getLast ( ) {

      return this.knots.get(this.knots.size() - 1);
   }

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
    * Returns an iterator, which allows an enhanced for-loop to access the
    * knots in a curve.
    *
    * @return the iterator
    *
    * @see List#iterator()
    */
   @Override
   public Iterator < KnotSphere > iterator ( ) { return this.knots.iterator(); }

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
   public CurveSphere prepend ( final KnotSphere knot ) {

      this.knots.add(0, knot);
      return this;
   }

   /**
    * Prepend a collection of knots to the curve's list of knots.
    *
    * @param kn the collection of knots
    *
    * @return this curve.
    */
   public CurveSphere prependAll ( final Collection < KnotSphere > kn ) {

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
   public CurveSphere prependAll ( final KnotSphere... kn ) {

      int j = 0;
      final int len = kn.length;
      for ( int i = 0; i < len; ++i ) {
         final KnotSphere knot = kn[i];
         this.knots.add(j, knot);
         ++j;
      }

      return this;
   }

   /**
    * Reverses the curve. This is done by reversing the list of knots and
    * swapping the fore- and rear-handle of each knot.
    *
    * @return this curve
    *
    * @see Collections#reverse(List)
    * @see KnotSphere#reverse()
    */
   public CurveSphere reverse ( ) {

      Collections.reverse(this.knots);
      final Iterator < KnotSphere > itr = this.knots.iterator();
      while ( itr.hasNext() ) { itr.next().reverse(); }

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

      final StringBuilder sb = new StringBuilder(64 + 256 * this.knots.size());
      sb.append("{ name: \"");
      sb.append(this.name);
      sb.append("\", closedLoop: ");
      sb.append(this.closedLoop);
      sb.append(", materialIndex: ");
      sb.append(this.materialIndex);
      sb.append(", knots: [ ");

      final Iterator < KnotSphere > itr = this.knots.iterator();
      while ( itr.hasNext() ) {
         itr.next().toString(sb, places);
         if ( itr.hasNext() ) { sb.append(',').append(' '); }
      }

      sb.append(" ] }");
      return sb.toString();
   }

   /**
    * For internal (package-level) use. Resizes a curve to the specified
    * length. The length may be no less than 2. When the new length is greater
    * than the old, new {@link KnotSphere}s are added.<br>
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
   CurveSphere resize ( final int len ) {

      final int vlen = len < 2 ? 2 : len;
      final int oldLen = this.knots.size();
      final int diff = vlen - oldLen;
      if ( diff < 0 ) {
         final int last = oldLen - 1;
         for ( int i = 0; i < -diff; ++i ) { this.knots.remove(last - i); }
      } else if ( diff > 0 ) {
         for ( int i = 0; i < diff; ++i ) { this.knots.add(new KnotSphere()); }
      }

      return this;
   }

   /**
    * Tests this curve for equality with another.
    *
    * @param curve the curve
    *
    * @return the evaluation
    */
   protected boolean equals ( final CurveSphere curve ) {

      return this.closedLoop == curve.closedLoop && this.knots.equals(
         curve.knots);
   }

   /**
    * Creates a curve which approximates a circle.
    *
    * @param sectors     number of knots
    * @param offsetAngle the offset azimuth
    * @param inclination the inclination
    * @param target      the output curve
    *
    * @return the circle
    */
   public static CurveSphere circle ( final int sectors,
      final float offsetAngle, final float inclination,
      final CurveSphere target ) {

      final int vSct = sectors < 3 ? 3 : sectors;
      target.resize(vSct);
      final Iterator < KnotSphere > itr = target.knots.iterator();

      // TODO: Test that inclination is correct!
      final float tol = IUtils.HALF_PI - IUtils.EPSILON;
      final float vincl = Utils.clamp(-inclination, -tol, tol);
      final float inclNorm = vincl * IUtils.ONE_TAU_2;
      final float cosIncl = Utils.scNorm(inclNorm);
      final float sinIncl = Utils.scNorm(inclNorm - 0.25f);

      final float toAzim = 0.5f / ( 3.0f * vSct );
      final float offNorm = offsetAngle * IUtils.ONE_TAU_2;

      for ( int j = 0; itr.hasNext(); j += 3 ) {
         final KnotSphere kn = itr.next();

         final float azimNorm0 = offNorm + ( j - 1 ) * toAzim;
         final float cosAzim0 = Utils.scNorm(azimNorm0);
         final float sinAzim0 = Utils.scNorm(azimNorm0 - 0.25f);

         final float azimNorm1 = offNorm + j * toAzim;
         final float cosAzim1 = Utils.scNorm(azimNorm1);
         final float sinAzim1 = Utils.scNorm(azimNorm1 - 0.25f);

         final float azimNorm2 = offNorm + ( j + 1 ) * toAzim;
         final float cosAzim2 = Utils.scNorm(azimNorm2);
         final float sinAzim2 = Utils.scNorm(azimNorm2 - 0.25f);

         kn.rearHandle.set(cosAzim0 * cosIncl, -sinAzim0 * sinIncl, sinIncl
            * cosAzim0, sinAzim0 * cosIncl);
         kn.coord.set(cosAzim1 * cosIncl, -sinAzim1 * sinIncl, sinIncl
            * cosAzim1, sinAzim1 * cosIncl);
         kn.foreHandle.set(cosAzim2 * cosIncl, -sinAzim2 * sinIncl, sinIncl
            * cosAzim2, sinAzim2 * cosIncl);
      }

      target.name = "Circle";
      target.closedLoop = true;
      return target;
   }

   /**
    * Evaluates a step in the range [0.0, 1.0] , returning a coordinate on the
    * curve.
    *
    * @param curve the curve
    * @param step  the step
    * @param coord the output coordinate
    *
    * @return the coordinate
    *
    * @see KnotSphere#squad(KnotSphere, KnotSphere, float, Quaternion)
    * @see CurveSphere#evalFirst(CurveSphere, Quaternion)
    * @see CurveSphere#evalLast(CurveSphere, Quaternion)
    */
   public static Quaternion eval ( final CurveSphere curve, final float step,
      final Quaternion coord ) {

      final ArrayList < KnotSphere > knots = curve.knots;
      final int knotLength = knots.size();

      float tScaled;
      int i = 0;
      KnotSphere a = null;
      KnotSphere b = null;
      if ( curve.closedLoop ) {
         tScaled = Utils.mod1(step) * knotLength;
         i = ( int ) tScaled;
         a = knots.get(Utils.mod(i, knotLength));
         b = knots.get(Utils.mod(i + 1, knotLength));
      } else {
         if ( knotLength == 1 || step <= 0.0f ) {
            return CurveSphere.evalFirst(curve, coord);
         }
         if ( step >= 1.0f ) { return CurveSphere.evalLast(curve, coord); }

         tScaled = step * ( knotLength - 1 );
         i = ( int ) tScaled;
         a = knots.get(i);
         b = knots.get(i + 1);
      }

      final float t = tScaled - i;
      KnotSphere.squad(a, b, t, coord);

      return coord;
   }

   /**
    * Evaluates the first knot in the curve. The tangent will be normalized,
    * to be of unit length.
    *
    * @param curve the curve
    * @param coord the output coordinate
    *
    * @return the coordinate
    */
   public static Quaternion evalFirst ( final CurveSphere curve,
      final Quaternion coord ) {

      final KnotSphere kFirst = curve.knots.get(0);
      coord.set(kFirst.coord);

      return coord;
   }

   /**
    * Evaluates the last knot in the curve. The tangent will be normalized, to
    * be of unit length.
    *
    * @param curve the curve
    * @param coord the output coordinate
    *
    * @return the coordinate
    */
   public static Quaternion evalLast ( final CurveSphere curve,
      final Quaternion coord ) {

      final KnotSphere kLast = curve.knots.get(curve.knots.size() - 1);
      coord.set(kLast.coord);

      return coord;
   }

   /**
    * Creates a spherical helix. Useful when creating rhumb lines.
    *
    * @param target the output curve
    *
    * @return the helix
    */
   public static CurveSphere helix ( final CurveSphere target ) {

      return CurveSphere.helix(ICurve.KNOTS_PER_CIRCLE, target);
   }

   /**
    * Creates a spherical helix. Useful when creating rhumb lines.
    *
    * @param sectors number of knots
    * @param target  the output curve
    *
    * @return the helix
    */
   public static CurveSphere helix ( final int sectors,
      final CurveSphere target ) {

      return CurveSphere.helix(sectors, 2, target);
   }

   /**
    * Creates a spherical helix. Useful when creating rhumb lines.
    *
    * @param sectors number of knots
    * @param period  number of periods
    * @param target  the output curve
    *
    * @return the helix
    */
   public static CurveSphere helix ( final int sectors, final int period,
      final CurveSphere target ) {

      return CurveSphere.helix(sectors, period, 0.0f, target);
   }

   /**
    * Creates a spherical helix. Useful when creating rhumb lines.
    *
    * @param sectors number of knots
    * @param period  number of periods
    * @param offset  azimuth offset
    * @param target  the output curve
    *
    * @return the helix
    */
   public static CurveSphere helix ( final int sectors, final int period,
      final float offset, final CurveSphere target ) {

      return CurveSphere.helix(sectors, period, offset, -IUtils.HALF_PI,
         IUtils.HALF_PI, target);
   }

   /**
    * Creates a spherical helix. Useful when creating
    * <a href="https://www.wikiwand.com/en/Rhumb_line">rhumb lines</a>
    * (loxodromes).
    *
    * @param sectors   number of knots
    * @param period    number of periods
    * @param offset    azimuth offset
    * @param inclStart inclination start
    * @param inclEnd   inclination end
    * @param target    the output curve
    *
    * @return the helix
    *
    * @see Utils#clamp(float, float, float)
    */
   public static CurveSphere helix ( final int sectors, final int period,
      final float offset, final float inclStart, final float inclEnd,
      final CurveSphere target ) {

      final int vPer = period < 1 ? 2 : period + 1;
      final int vSct = sectors < 2 ? 2 : sectors;

      /* Adjust inclination. */
      final float aIncl = 1.0f - IUtils.ONE_TAU_2 * Utils.clamp(inclStart,
         -IUtils.HALF_PI, IUtils.HALF_PI);
      final float bIncl = 1.0f - IUtils.ONE_TAU_2 * Utils.clamp(inclEnd,
         -IUtils.HALF_PI, IUtils.HALF_PI);

      final int len = vPer * vSct;
      final float kToFac = 1.0f / ( len - 1.0f );
      final float bAzim = ( vPer - 1.0f ) * 0.25f;
      final float aAzim = -bAzim;
      final float offNorm = IUtils.ONE_TAU_2 * offset;

      target.resize(len);
      final Iterator < KnotSphere > itr = target.knots.iterator();

      for ( int k = 0; k < len; ++k ) {
         final float kf = k;

         /* Previous. */
         final float t0 = ( kf - IUtils.ONE_THIRD ) * kToFac;
         final float u0 = 1.0f - t0;
         final float incl0 = u0 * aIncl + t0 * bIncl;
         final float azim0 = offNorm + u0 * aAzim + t0 * bAzim;

         final float cosAzim0 = Utils.scNorm(azim0);
         final float sinAzim0 = Utils.scNorm(azim0 - 0.25f);
         final float cosIncl0 = Utils.scNorm(incl0);
         final float sinIncl0 = Utils.scNorm(incl0 - 0.25f);

         /* Current. */
         final float t1 = kf * kToFac;
         final float u1 = 1.0f - t1;
         final float incl1 = u1 * aIncl + t1 * bIncl;
         final float azim1 = offNorm + u1 * aAzim + t1 * bAzim;

         final float cosAzim1 = Utils.scNorm(azim1);
         final float sinAzim1 = Utils.scNorm(azim1 - 0.25f);
         final float cosIncl1 = Utils.scNorm(incl1);
         final float sinIncl1 = Utils.scNorm(incl1 - 0.25f);

         /* Next. */
         final float t2 = ( kf + IUtils.ONE_THIRD ) * kToFac;
         final float u2 = 1.0f - t2;
         final float incl2 = u2 * aIncl + t2 * bIncl;
         final float azim2 = offNorm + u2 * aAzim + t2 * bAzim;

         final float cosAzim2 = Utils.scNorm(azim2);
         final float sinAzim2 = Utils.scNorm(azim2 - 0.25f);
         final float cosIncl2 = Utils.scNorm(incl2);
         final float sinIncl2 = Utils.scNorm(incl2 - 0.25f);

         final KnotSphere knot = itr.next();
         knot.rearHandle.set(cosAzim0 * cosIncl0, -sinAzim0 * sinIncl0, sinIncl0
            * cosAzim0, sinAzim0 * cosIncl0);
         knot.coord.set(cosAzim1 * cosIncl1, -sinAzim1 * sinIncl1, sinIncl1
            * cosAzim1, sinAzim1 * cosIncl1);
         knot.foreHandle.set(cosAzim2 * cosIncl2, -sinAzim2 * sinIncl2, sinIncl2
            * cosAzim2, sinAzim2 * cosIncl2);
      }

      target.name = "Helix";
      target.closedLoop = false;
      return target;
   }

   /**
    * Creates a random curve.
    *
    * @param rng        the random number generator
    * @param count      the count
    * @param closedLoop the close loop flag
    * @param target     the output curve
    *
    * @return the random curve
    */
   public static CurveSphere random ( final Random rng, final int count,
      final boolean closedLoop, final CurveSphere target ) {

      final int valCount = count < 2 ? 2 : count;
      target.resize(valCount);
      final Iterator < KnotSphere > itr = target.knots.iterator();
      // KnotSphere first = itr.next();
      // KnotSphere prev = first;
      while ( itr.hasNext() ) {
         final KnotSphere curr = itr.next();

         Quaternion.random(rng, curr.coord);
         Quaternion.random(rng, curr.foreHandle);
         Quaternion.random(rng, curr.rearHandle);

         // Quaternion.innerQuadrangle(prev.coord, prev.foreHandle,
         // curr.rearHandle, curr.coord, prev.foreHandle, curr.rearHandle,
         // new Quaternion());
         // prev = curr;
      }
      target.closedLoop = closedLoop;

      return target;
   }

}
