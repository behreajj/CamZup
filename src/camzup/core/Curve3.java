package camzup.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import camzup.core.Utils.EasingFuncArr;
import camzup.core.Utils.EasingFuncObj;

/**
 * Organizes a 3D cubic Bezier curve into a list of knots. Provides a
 * function to retrieve a point and tangent on a curve from a step in the
 * range [0.0, 1.0] .
 */
public class Curve3 extends Curve implements Iterable < Knot3 > {

   /**
    * The list of knots contained by the curve.
    */
   private final ArrayList < Knot3 > knots = new ArrayList <>(
      ICurve.KNOT_CAPACITY);

   /**
    * Creates a curve with two default knots.
    */
   public Curve3 ( ) { super(); }

   /**
    * Creates a curve from a collection of knots
    *
    * @param cl    whether or not the curve is closed
    * @param knots the collection of knots
    */
   public Curve3 ( final boolean cl, final Collection < Knot3 > knots ) {

      super(cl);
      this.appendAll(knots);
   }

   /**
    * Creates a curve from a comma-separated list of knots.
    *
    * @param cl    whether or not the curve is closed
    * @param knots the list of knots
    */
   public Curve3 ( final boolean cl, final Knot3... knots ) {

      super(cl);
      this.appendAll(knots);
   }

   /**
    * Constructs a copy of the source.
    *
    * @param source the source curve
    */
   public Curve3 ( final Curve2 source ) {

      super();
      this.set(source);
   }

   /**
    * Constructs a copy of the source.
    *
    * @param source the source curve
    */
   public Curve3 ( final Curve3 source ) {

      super();
      this.set(source);
   }

   /**
    * Creates a named curve with two default knots.
    *
    * @param name the name
    */
   public Curve3 ( final String name ) {

      super(name);
   }

   /**
    * Creates a named curve from a collection of knots
    *
    * @param name  the name
    * @param cl    whether or not the curve is closed
    * @param knots the collection of knots
    */
   public Curve3 ( final String name, final boolean cl, final Collection <
      Knot3 > knots ) {

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
   public Curve3 ( final String name, final boolean cl, final Knot3... knots ) {

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
   public Curve3 append ( final Knot3 knot ) {

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
   public Curve3 appendAll ( final Collection < Knot3 > kn ) {

      final Iterator < Knot3 > knItr = kn.iterator();
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
   public Curve3 appendAll ( final Knot3... kn ) {

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
   public boolean contains ( final Knot3 kn ) {

      return this.knots.contains(kn);
   }

   /**
    * Tests this curve for equality with another object.
    *
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( !super.equals(obj) ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Curve3 ) obj);
   }

   /**
    * Flips the curve on the x axis, then reverse the curve.
    *
    * @return this curve
    */
   public Curve3 flipX ( ) {

      final Iterator < Knot3 > itr = this.knots.iterator();
      while ( itr.hasNext() ) {
         final Knot3 kn = itr.next();
         kn.coord.x = -kn.coord.x;
         kn.foreHandle.x = -kn.foreHandle.x;
         kn.rearHandle.x = -kn.rearHandle.x;
         kn.reverse();
      }
      Collections.reverse(this.knots);
      return this;
   }

   /**
    * Flips the curve on the y axis, then reverse the curve.
    *
    * @return this curve
    */
   public Curve3 flipY ( ) {

      final Iterator < Knot3 > itr = this.knots.iterator();
      while ( itr.hasNext() ) {
         final Knot3 kn = itr.next();
         kn.coord.y = -kn.coord.y;
         kn.foreHandle.y = -kn.foreHandle.y;
         kn.rearHandle.y = -kn.rearHandle.y;
         kn.reverse();
      }
      Collections.reverse(this.knots);
      return this;
   }

   /**
    * Flips the curve on the y axis, then reverse the curve.
    *
    * @return this curve
    */
   public Curve3 flipZ ( ) {

      final Iterator < Knot3 > itr = this.knots.iterator();
      while ( itr.hasNext() ) {
         final Knot3 kn = itr.next();
         kn.coord.z = -kn.coord.z;
         kn.foreHandle.z = -kn.foreHandle.z;
         kn.rearHandle.z = -kn.rearHandle.z;
         kn.reverse();
      }
      Collections.reverse(this.knots);
      return this;
   }

   /**
    * Gets a knot from the curve by an index. When the curve is a closed loop,
    * the index wraps around.
    *
    * @param i the index
    *
    * @return the knot
    *
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
    *
    * @see List#get(int)
    */
   public Knot3 getFirst ( ) { return this.knots.get(0); }

   /**
    * Gets the last knot in the curve.
    *
    * @return the knot
    *
    * @see List#get(int)
    * @see List#size()
    */
   public Knot3 getLast ( ) { return this.knots.get(this.knots.size() - 1); }

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
    */
   public Curve3 insert ( final int i, final Knot3 knot ) {

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
    */
   public Curve3 insertAll ( final int i, final Collection < Knot3 > kn ) {

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
    */
   public Curve3 insertAll ( final int i, final Knot3... kn ) {

      final int len = kn.length;
      final int vidx = this.closedLoop ? Utils.mod(i, this.knots.size() + 1)
         : i;
      int k = vidx;
      for ( int j = 0; j < len; ++j ) {
         final Knot3 knot = kn[j];
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
    * @see LinkedList#iterator()
    */
   @Override
   public Iterator < Knot3 > iterator ( ) { return this.knots.iterator(); }

   /**
    * Gets the number of knots in the curve.
    *
    * @return the knot count
    *
    * @see LinkedList#size()
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
   public Curve3 prepend ( final Knot3 knot ) {

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
   public Curve3 prependAll ( final Collection < Knot3 > kn ) {

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
   public Curve3 prependAll ( final Knot3... kn ) {

      int j = 0;
      final int len = kn.length;
      for ( int i = 0; i < len; ++i ) {
         final Knot3 knot = kn[i];
         this.knots.add(j, knot);
         ++j;
      }
      return this;
   }

   /**
    * Centers the curve about the origin, (0.0, 0.0), and rescales it to the
    * range [-0.5, 0.5] . Emits a transform which records the curve's center
    * point and original dimension. The transform's rotation is reset.
    *
    * @param tr the output transform
    *
    * @return this mesh
    *
    * @see Curve3#calcDimensions(Curve3, Vec3, Vec3, Vec3)
    */
   public Curve3 reframe ( final Transform3 tr ) {

      tr.locPrev.set(tr.location);
      tr.scalePrev.set(tr.scale);

      final Vec3 dim = tr.scale;
      final Vec3 lb = tr.location;
      final Vec3 ub = new Vec3();
      Curve3.calcDimensions(this, dim, lb, ub);

      lb.x = 0.5f * ( lb.x + ub.x );
      lb.y = 0.5f * ( lb.y + ub.y );
      lb.z = 0.5f * ( lb.z + ub.z );
      final float scl = Utils.div(1.0f, Utils.max(dim.x, dim.y, dim.z));

      final Iterator < Knot3 > itr = this.knots.iterator();
      while ( itr.hasNext() ) {
         final Knot3 kn = itr.next();
         final Vec3 co = kn.coord;
         final Vec3 fh = kn.foreHandle;
         final Vec3 rh = kn.rearHandle;
         Vec3.sub(co, lb, co);
         Vec3.mul(co, scl, co);
         Vec3.sub(fh, lb, fh);
         Vec3.mul(fh, scl, fh);
         Vec3.sub(rh, lb, rh);
         Vec3.mul(rh, scl, rh);
      }

      tr.rotPrev.set(tr.rotation);
      tr.rotation.reset();
      tr.updateAxes();

      return this;
   }

   /**
    * Relocates a knot at a given index to a coordinate. Maintains
    * relationship between knot coordinate and handles.
    *
    * @param i the index
    * @param v the coordinate
    *
    * @return this curve
    */
   public Curve3 relocateKnot ( final int i, final Vec3 v ) {

      this.get(i).relocate(v);
      return this;
   }

   /**
    * Returns and removes a knot at a given index.
    *
    * @param i the index
    *
    * @return the knot
    *
    * @see List#remove(int)
    * @see List#size()
    */
   public Knot3 removeAt ( final int i ) {

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
   public Knot3 removeFirst ( ) { return this.knots.remove(0); }

   /**
    * Removes and returns the last knot in the curve.
    *
    * @return the knot
    *
    * @see List#remove(int)
    * @see List#size()
    */
   public Knot3 removeLast ( ) {

      return this.knots.remove(this.knots.size() - 1);
   }

   /**
    * Resets the curve, leaving two default knots.
    *
    * @return this curve
    */
   public Curve3 resetToDefault ( ) {

      this.resize(2);
      this.knots.get(0).set(-0.5f, 0.0f, 0.0f, -0.25f, 0.25f, 0.0f, -0.75f,
         -0.25f, 0.0f);
      this.knots.get(1).set(0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
         0.0f);

      this.closedLoop = false;
      this.materialIndex = 0;
      this.name = this.hashIdentityString();

      return this;
   }

   /**
    * Reverses the curve. This is done by reversing the list of knots and
    * swapping the fore- and rear-handle of each knot.
    *
    * @return this curve
    *
    * @see Collections#reverse(List)
    * @see Knot3#reverse()
    */
   public Curve3 reverse ( ) {

      Collections.reverse(this.knots);
      final Iterator < Knot3 > itr = this.knots.iterator();
      while ( itr.hasNext() ) { itr.next().reverse(); }

      return this;
   }

   /**
    * Rotates all knots in the curve by an angle in radians around an axis.
    *
    * @param radians the angle
    * @param axis    the axis
    *
    * @return this curve
    *
    * @see Knot3#rotate(float, Vec3)
    */
   public Curve3 rotate ( final float radians, final Vec3 axis ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      final Iterator < Knot3 > itr = this.knots.iterator();
      while ( itr.hasNext() ) { itr.next().rotate(cosa, sina, axis); }
      return this;
   }

   /**
    * Rotates all knots in the curve by a quaternion.
    *
    * @param q the quaternion
    *
    * @return this curve
    */
   public Curve3 rotate ( final Quaternion q ) {

      final Iterator < Knot3 > itr = this.knots.iterator();
      while ( itr.hasNext() ) { itr.next().rotate(q); }

      return this;
   }

   /**
    * Rotates all knots in the curve by an angle in radians around the x axis.
    *
    * @param radians the angle
    *
    * @return this curve
    *
    * @see Knot3#rotateX(float)
    */
   public Curve3 rotateX ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      final Iterator < Knot3 > itr = this.knots.iterator();
      while ( itr.hasNext() ) { itr.next().rotateX(cosa, sina); }

      return this;
   }

   /**
    * Rotates all knots in the curve by an angle in radians around the y axis.
    *
    * @param radians the angle
    *
    * @return this curve
    *
    * @see Knot3#rotateY(float)
    */
   public Curve3 rotateY ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      final Iterator < Knot3 > itr = this.knots.iterator();
      while ( itr.hasNext() ) { itr.next().rotateY(cosa, sina); }

      return this;
   }

   /**
    * Rotates all knots in the curve by an angle in radians around the z axis.
    *
    * @param radians the angle
    *
    * @return this curve
    *
    * @see Knot3#rotateZ(float)
    */
   public Curve3 rotateZ ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      final Iterator < Knot3 > itr = this.knots.iterator();
      while ( itr.hasNext() ) { itr.next().rotateZ(cosa, sina); }

      return this;
   }

   /**
    * Scales all knots in the curve by a scalar.
    *
    * @param scale the scale
    *
    * @return this curve
    *
    * @see Knot3#scale(float)
    */
   public Curve3 scale ( final float scale ) {

      if ( scale != 0.0f ) {
         final Iterator < Knot3 > itr = this.knots.iterator();
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
    * @see Vec3#none(Vec3)
    * @see Knot3#scale(Vec3)
    */
   public Curve3 scale ( final Vec3 scale ) {

      if ( Vec3.all(scale) ) {
         final Iterator < Knot3 > itr = this.knots.iterator();
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
   public Curve3 set ( final Curve2 source ) {

      this.resize(source.length());
      final Iterator < Knot2 > srcItr = source.iterator();
      final Iterator < Knot3 > trgItr = this.knots.iterator();
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
   public Curve3 set ( final Curve3 source ) {

      this.resize(source.length());
      final Iterator < Knot3 > srcItr = source.iterator();
      final Iterator < Knot3 > trgItr = this.knots.iterator();
      while ( srcItr.hasNext() ) { trgItr.next().set(srcItr.next()); }

      this.closedLoop = source.closedLoop;
      this.materialIndex = source.materialIndex;
      return this;
   }

   /**
    * Centers the curve about the origin, (0.0, 0.0, 0.0), by calculating its
    * dimensions then subtracting the center point. Emits a transform which
    * records the curve's center point. The transform's rotation and scale are
    * reset.
    *
    * @param tr the output transform
    *
    * @return this mesh
    *
    * @see Curve3#calcDimensions(Curve3, Vec3, Vec3, Vec3)
    * @see Curve3#translate(Vec3)
    */
   public Curve3 toOrigin ( final Transform3 tr ) {

      final Vec3 lb = new Vec3();
      final Vec3 ub = new Vec3();
      Curve3.calcDimensions(this, new Vec3(), lb, ub);

      lb.x = -0.5f * ( lb.x + ub.x );
      lb.y = -0.5f * ( lb.y + ub.y );
      lb.z = -0.5f * ( lb.z + ub.z );
      this.translate(lb);

      tr.locPrev.set(tr.location);
      Vec3.negate(lb, tr.location);

      tr.scaleTo(1.0f);

      tr.rotPrev.set(tr.rotation);
      tr.rotation.reset();
      tr.updateAxes();

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

      final Iterator < Knot3 > itr = this.knots.iterator();
      while ( itr.hasNext() ) {
         itr.next().toString(sb, places);
         if ( itr.hasNext() ) { sb.append(',').append(' '); }
      }

      sb.append(" ] }");
      return sb.toString();
   }

   /**
    * Transforms all knots in the curve by a matrix.
    *
    * @param m the matrix
    *
    * @return this curve
    *
    * @see Knot3#transform(Mat4)
    */
   public Curve3 transform ( final Mat4 m ) {

      final Iterator < Knot3 > itr = this.knots.iterator();
      while ( itr.hasNext() ) { itr.next().transform(m); }

      return this;
   }

   /**
    * Transforms all coordinates in the curve <em>permanently</em> by a
    * transform.<br>
    * <br>
    * Not to be confused with the <em>temporary</em> transformations applied
    * by a curve entity's transform to the meshes contained within the
    * entity.<br>
    * <br>
    * Useful when consolidating multiple curve entities into one curve entity.
    *
    * @param tr the transform
    *
    * @return this curve
    */
   public Curve3 transform ( final Transform3 tr ) {

      final Iterator < Knot3 > itr = this.knots.iterator();
      while ( itr.hasNext() ) { itr.next().transform(tr); }

      return this;
   }

   /**
    * Translates all knots in the curve by a vector.
    *
    * @param v the vector
    *
    * @return this curve
    *
    * @see Knot3#translate(Vec3)
    */
   public Curve3 translate ( final Vec3 v ) {

      final Iterator < Knot3 > itr = this.knots.iterator();
      while ( itr.hasNext() ) { itr.next().translate(v); }

      return this;
   }

   /**
    * For internal (package-level) use. Resizes a curve to the specified
    * length. The length may be no less than 2. When the new length is greater
    * than the old, new <code>Knot2</code>s are added.<br>
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
   Curve3 resize ( final int len ) {

      final int vlen = len < 2 ? 2 : len;
      final int oldLen = this.knots.size();
      final int diff = vlen - oldLen;
      if ( diff < 0 ) {
         final int last = oldLen - 1;
         for ( int i = 0; i < -diff; ++i ) { this.knots.remove(last - i); }
      } else if ( diff > 0 ) {
         for ( int i = 0; i < diff; ++i ) { this.knots.add(new Knot3()); }
      }
      return this;
   }

   /**
    * An internal helper function to format a vector as a Python tuple, then
    * append it to a {@link StringBuilder}. Used for testing purposes to
    * compare results with Blender 2.9x.
    *
    * @param pyCd      the string builder
    * @param uRes      the resolution u
    * @param tiltStart the tilt start
    * @param tilEnd    the tilt end
    *
    * @return the string builder
    */
   @Experimental
   StringBuilder toBlenderCode ( final StringBuilder pyCd, final int uRes,
      final float tiltStart, final float tiltEnd ) {

      pyCd.append("{\"closed_loop\": ");
      pyCd.append(this.closedLoop ? "True" : "False");
      pyCd.append(", \"resolution_u\": ");
      pyCd.append(uRes);
      pyCd.append(", \"knots\": [");

      final int len = this.knots.size();
      final float toPercent = 1.0f / ( this.closedLoop ? len : len - 1.0f );

      int i = 0;
      for ( final Iterator < Knot3 > itr = this.knots.iterator(); itr.hasNext();
         ++i ) {
         final float t = i * toPercent;
         final float ang = this.closedLoop ? Utils.pingPong(tiltStart, tiltEnd,
            t, 1.0f) : Utils.lerpUnclamped(tiltStart, tiltEnd, t);
         itr.next().toBlenderCode(pyCd, 1.0f, 1.0f, ang);
         if ( itr.hasNext() ) { pyCd.append(',').append(' '); }
      }

      pyCd.append(']');
      pyCd.append('}');
      return pyCd;
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
    * @return the evaluation
    */
   protected boolean equals ( final Curve3 curve ) {

      return this.closedLoop == curve.closedLoop && this.knots.equals(
         curve.knots);
   }

   /**
    * Creates an arc from a start and stop angle. The arc can be open,
    * traversed by a chord, or pie-shaped.
    *
    * @param startAngle the start angle
    * @param stopAngle  the stop angle
    * @param radius     the arc radius
    * @param arcMode    the arc mode
    * @param target     the output curve
    *
    * @return the arc
    */
   public static Curve3 arc ( final float startAngle, final float stopAngle,
      final float radius, final ArcMode arcMode, final Curve3 target ) {

      /* See Curve2's arc function for more detailed comments. */

      if ( Utils.approx(stopAngle - startAngle, IUtils.TAU, 0.00139f) ) {
         return Curve3.circle(startAngle, radius, 4, 0.0f, 0.0f, 0.0f, target);
      }

      final float a1 = Utils.mod1(startAngle * IUtils.ONE_TAU);
      final float b1 = Utils.mod1(stopAngle * IUtils.ONE_TAU);
      final float arcLen1 = Utils.mod1(b1 - a1);

      if ( arcLen1 <= 0.00139f ) { return target; }

      final float destAngle1 = a1 + arcLen1;
      final int knotCount = Utils.ceil(1 + 4 * arcLen1);
      final float toStep = 1.0f / ( knotCount - 1.0f );

      final float hndtn = 0.25f * toStep * arcLen1;
      final float handleMag = Utils.tan(hndtn * IUtils.TAU) * radius
         * IUtils.FOUR_THIRDS;

      target.resize(knotCount);
      final ArrayList < Knot3 > knots = target.knots;
      for ( int i = 0; i < knotCount; ++i ) {
         final Knot3 knot = knots.get(i);
         final float step = i * toStep;
         final float angle1 = ( 1.0f - step ) * a1 + step * destAngle1;
         Knot3.fromPolar(Utils.scNorm(angle1), Utils.scNorm(angle1 - 0.25f),
            radius, handleMag, 0.0f, 0.0f, 0.0f, knot);
      }

      target.closedLoop = arcMode != ArcMode.OPEN;
      if ( target.closedLoop ) {
         final Knot3 first = knots.get(0);
         final Knot3 last = knots.get(knotCount - 1);

         if ( arcMode == ArcMode.CHORD ) {

            Curve3.lerp13(last.coord, first.coord, last.foreHandle);
            Curve3.lerp13(first.coord, last.coord, first.rearHandle);

         } else if ( arcMode == ArcMode.PIE ) {

            final Knot3 center = new Knot3();
            final Vec3 coCenter = center.coord;
            knots.add(center);

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
    * Calculates the dimensions of an Axis-Aligned Bounding Box (AABB)
    * encompassing the curve. Does so by taking the min and max of each knot's
    * coordinate, fore handle and rear handle; <em>not</em> by finding the
    * curve extrema.
    *
    * @param curve  the curve
    * @param target the output dimensions
    * @param lb     the lower bound
    * @param ub     the upper bound
    *
    * @return the dimensions
    */
   public static Vec3 calcDimensions ( final Curve3 curve, final Vec3 target,
      final Vec3 lb, final Vec3 ub ) {

      lb.set(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
      ub.set(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
      final Iterator < Knot3 > itr = curve.knots.iterator();
      while ( itr.hasNext() ) {
         final Knot3 kn = itr.next();
         final Vec3 co = kn.coord;
         final Vec3 fh = kn.foreHandle;
         final Vec3 rh = kn.rearHandle;

         if ( co.x < lb.x ) { lb.x = co.x; }
         if ( co.x > ub.x ) { ub.x = co.x; }
         if ( co.y < lb.y ) { lb.y = co.y; }
         if ( co.y > ub.y ) { ub.y = co.y; }
         if ( co.z < lb.z ) { lb.z = co.z; }
         if ( co.z > ub.z ) { ub.z = co.z; }

         if ( fh.x < lb.x ) { lb.x = fh.x; }
         if ( fh.x > ub.x ) { ub.x = fh.x; }
         if ( fh.y < lb.y ) { lb.y = fh.y; }
         if ( fh.y > ub.y ) { ub.y = fh.y; }
         if ( fh.z < lb.z ) { lb.z = fh.z; }
         if ( fh.z > ub.z ) { ub.z = fh.z; }

         if ( rh.x < lb.x ) { lb.x = rh.x; }
         if ( rh.x > ub.x ) { ub.x = rh.x; }
         if ( rh.y < lb.y ) { lb.y = rh.y; }
         if ( rh.y > ub.y ) { ub.y = rh.y; }
         if ( rh.z < lb.z ) { lb.z = rh.z; }
         if ( rh.z > ub.z ) { ub.z = rh.z; }
      }

      return Vec3.sub(ub, lb, target);
   }

   /**
    * Creates a curve which approximates a circle of radius 0.5 using four
    * knots.
    *
    * @param target the output curve
    *
    * @return the circle
    */
   public static Curve3 circle ( final Curve3 target ) {

      return Curve3.circle(0.0f, target);
   }

   /**
    * Creates a curve which approximates a circle of radius 0.5 using four
    * knots.
    *
    * @param offsetAngle the angular offset
    * @param target      the output curve
    *
    * @return the circle
    */
   public static Curve3 circle ( final float offsetAngle,
      final Curve3 target ) {

      return Curve3.circle(offsetAngle, 0.5f, target);
   }

   /**
    * Creates a curve which approximates a circle using four knots.
    *
    * @param offsetAngle the angular offset
    * @param radius      the radius
    * @param target      the output curve
    *
    * @return the circle
    */
   public static Curve3 circle ( final float offsetAngle, final float radius,
      final Curve3 target ) {

      return Curve3.circle(offsetAngle, radius, ICurve.KNOTS_PER_CIRCLE,
         target);
   }

   /**
    * Creates a curve which approximates a circle.
    *
    * @param offsetAngle the angular offset
    * @param radius      the radius
    * @param knotCount   the knot count
    * @param target      the output curve
    *
    * @return the circle
    */
   public static Curve3 circle ( final float offsetAngle, final float radius,
      final int knotCount, final Curve3 target ) {

      return Curve3.circle(offsetAngle, radius, knotCount, 0.0f, 0.0f, 0.0f,
         target);
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
   public static Knot3 eval ( final Curve3 curve, final float step,
      final Knot3 target ) {

      final ArrayList < Knot3 > knots = curve.knots;
      final int knotLength = knots.size();

      float tScaled;
      int i = 0;
      Knot3 a = null;
      Knot3 b = null;
      if ( curve.closedLoop ) {
         tScaled = Utils.mod1(step) * knotLength;
         i = ( int ) tScaled;
         a = knots.get(Utils.mod(i, knotLength));
         b = knots.get(Utils.mod(i + 1, knotLength));
      } else {
         if ( knotLength == 1 || step <= 0.0f ) {
            Curve3.evalFirst(curve, target);
            return target;
         }
         if ( step >= 1.0f ) {
            Curve3.evalLast(curve, target);
            return target;
         }

         tScaled = step * ( knotLength - 1 );
         i = ( int ) tScaled;
         a = knots.get(i);
         b = knots.get(i + 1);
      }

      final float t = tScaled - i;
      final float u = 1.0f - t;
      Curve3.bezierKnot(a, b, t, target);

      // QUERY Should this logic be moved to bezierKnot? Can this use magSq then
      // find sqrt after the lerp?
      final float aFhMag = Knot3.foreMag(a);
      final float bFhMag = Knot3.foreMag(b);
      final float tFhMag = u * aFhMag + t * bFhMag;
      target.scaleForeHandleTo(tFhMag);

      final float aRhMag = Knot3.rearMag(a);
      final float bRhMag = Knot3.rearMag(b);
      final float tRhMag = u * aRhMag + t * bRhMag;
      target.scaleRearHandleTo(tRhMag);

      return target;
   }

   /**
    * Evaluates a step in the range [0.0, 1.0] , returning a ray. The ray's
    * origin will be a coordinate on the curve while its direction will be a
    * normalized tangent.
    *
    * @param curve the curve
    * @param step  the step
    * @param ray   the output ray
    *
    * @return the ray
    *
    * @see Curve3#eval(Curve3, float, Vec3, Vec3)
    */
   public static Ray3 eval ( final Curve3 curve, final float step,
      final Ray3 ray ) {

      Curve3.eval(curve, step, ray.origin, ray.dir);
      return ray;
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
    * @see Vec3#bezierPoint(Vec3, Vec3, Vec3, Vec3, float, Vec3)
    * @see Vec3#bezierTanUnit(Vec3, Vec3, Vec3, Vec3, float, Vec3)
    */
   public static Vec3 eval ( final Curve3 curve, final float step,
      final Vec3 coord, final Vec3 tangent ) {

      final ArrayList < Knot3 > knots = curve.knots;
      final int knotLength = knots.size();

      float tScaled;
      int i = 0;
      Knot3 a = null;
      Knot3 b = null;
      if ( curve.closedLoop ) {
         tScaled = Utils.mod1(step) * knotLength;
         i = ( int ) tScaled;
         a = knots.get(Utils.mod(i, knotLength));
         b = knots.get(Utils.mod(i + 1, knotLength));
      } else {
         if ( knotLength == 1 || step <= 0.0f ) {
            return Curve3.evalFirst(curve, coord, tangent);
         }
         if ( step >= 1.0f ) { return Curve3.evalLast(curve, coord, tangent); }

         tScaled = step * ( knotLength - 1 );
         i = ( int ) tScaled;
         a = knots.get(i);
         b = knots.get(i + 1);
      }

      final float t = tScaled - i;
      Knot3.bezierPoint(a, b, t, coord);
      Knot3.bezierTanUnit(a, b, t, tangent);

      return coord;
   }

   /**
    * Evaluates the first knot in the curve.
    *
    * @param curve      the curve
    * @param handedness the handedness
    * @param target     the output transform
    *
    * @return the coordinate
    *
    * @see Curve3#evalFirst(Curve3, Vec3, Vec3)
    * @see Quaternion#fromDir(Vec3, Handedness, Quaternion, Vec3, Vec3, Vec3)
    */
   public static Transform3 evalFirst ( final Curve3 curve,
      final Handedness handedness, final Transform3 target ) {

      target.locPrev.set(target.location);
      target.rotPrev.set(target.rotation);
      Curve3.evalFirst(curve, target.location, target.forward);

      Quaternion.fromDir(target.forward, handedness, target.rotation,
         target.right, target.forward, target.up);

      return target;
   }

   /**
    * Evaluates the first knot in the curve.
    *
    * @param curve  the curve
    * @param target the output knot
    *
    * @return the knot
    */
   public static Knot3 evalFirst ( final Curve3 curve, final Knot3 target ) {

      target.set(curve.knots.get(0));
      return target;
   }

   /**
    * Evaluates the first knot in the curve.
    *
    * @param curve the curve
    * @param ray   the output ray
    *
    * @return the coordinate
    *
    * @see Curve2#evalFirst(Curve2, Vec2, Vec2)
    */
   public static Ray3 evalFirst ( final Curve3 curve, final Ray3 ray ) {

      Curve3.evalFirst(curve, ray.origin, ray.dir);
      return ray;
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
    * @see Vec3#subNorm(Vec3, Vec3, Vec3)
    */
   public static Vec3 evalFirst ( final Curve3 curve, final Vec3 coord,
      final Vec3 tangent ) {

      final Knot3 kFirst = curve.knots.get(0);
      coord.set(kFirst.coord);
      Vec3.subNorm(kFirst.foreHandle, coord, tangent);

      return coord;
   }

   /**
    * Evaluates the last knot in the curve.
    *
    * @param curve      the curve
    * @param handedness the handedness
    * @param target     the output transform
    *
    * @return the coordinate
    *
    * @see Curve3#evalLast(Curve3, Vec3, Vec3)
    * @see Quaternion#fromDir(Vec3, Handedness, Quaternion, Vec3, Vec3, Vec3)
    */
   public static Transform3 evalLast ( final Curve3 curve,
      final Handedness handedness, final Transform3 target ) {

      target.locPrev.set(target.location);
      target.rotPrev.set(target.rotation);
      Curve3.evalLast(curve, target.location, target.forward);

      Quaternion.fromDir(target.forward, handedness, target.rotation,
         target.right, target.forward, target.up);

      return target;
   }

   /**
    * Evaluates the last knot in the curve.
    *
    * @param curve  the curve
    * @param target the output knot
    *
    * @return the knot
    */
   public static Knot3 evalLast ( final Curve3 curve, final Knot3 target ) {

      target.set(curve.knots.get(curve.knots.size() - 1));
      return target;
   }

   /**
    * Evaluates the first knot in the curve. The tangent will be normalized,
    * to be of unit length.
    *
    * @param curve the curve
    * @param ray   the output ray
    *
    * @return the coordinate
    *
    * @see Curve2#evalFirst(Curve2, Vec2, Vec2)
    */
   public static Ray3 evalLast ( final Curve3 curve, final Ray3 ray ) {

      Curve3.evalLast(curve, ray.origin, ray.dir);
      return ray;
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
    * @see Vec3#subNorm(Vec3, Vec3, Vec3)
    */
   public static Vec3 evalLast ( final Curve3 curve, final Vec3 coord,
      final Vec3 tangent ) {

      final Knot3 kLast = curve.knots.get(curve.knots.size() - 1);
      coord.set(kLast.coord);
      Vec3.subNorm(coord, kLast.rearHandle, tangent);

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
    */
   public static Curve3 fromCatmull ( final boolean closedLoop,
      final Vec3[] points, final float tightness, final Curve3 target ) {

      final int ptsLen = points.length;
      if ( ptsLen < 4 ) { return target; }

      target.closedLoop = closedLoop;
      target.name = "Catmull";

      final int knotCount = closedLoop ? ptsLen : ptsLen - 2;
      target.resize(knotCount);
      final Iterator < Knot3 > itr = target.iterator();
      final Knot3 first = itr.next();
      final int ptsLast = ptsLen - 1;

      int idx = 0;
      Knot3 prev = first;
      Knot3 curr;
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
         Knot3.fromSegCatmull(points[idx], points[idx1], points[idx2],
            points[idx3], tightness, prev, curr);
         prev = curr;
      }

      if ( closedLoop ) {
         Knot3.fromSegCatmull(points[ptsLast], points[0], points[1], points[2],
            tightness, curr, first);
      } else {
         first.coord.set(points[1]);
         first.mirrorHandlesForward();
         curr.mirrorHandlesBackward();
      }

      return target;
   }

   /**
    * Creates a curve from a face in a mesh.
    *
    * @param face   the mesh face
    * @param target the output curve
    *
    * @return the curve
    *
    * @see Utils#mod(int, int)
    * @see Curve3#lerp13(Vec3, Vec3, Vec3)
    */
   public static Curve3 fromMeshFace ( final Face3 face, final Curve3 target ) {

      target.closedLoop = true;
      target.name = "Face";

      final Vert3[] verts = face.vertices;
      final int vertsLen = verts.length;
      target.resize(vertsLen);
      final Iterator < Knot3 > itr = target.knots.iterator();

      for ( int i = 0; i < vertsLen; ++i ) {
         final int h = Utils.mod(i - 1, vertsLen);
         final int j = ( i + 1 ) % vertsLen;
         final Vec3 v1 = verts[i].coord;
         final Knot3 knot = itr.next();
         Curve3.lerp13(v1, verts[h].coord, knot.rearHandle);
         Curve3.lerp13(v1, verts[j].coord, knot.foreHandle);
         knot.coord.set(v1);
      }

      return target;
   }

   /**
    * Creates a curve from a face in a mesh.
    *
    * @param faceIdx the face index
    * @param mesh    the mesh
    * @param target  the output curve
    *
    * @return the curve
    *
    * @see Utils#mod(int, int)
    * @see Curve3#lerp13(Vec3, Vec3, Vec3)
    */
   public static Curve3 fromMeshFace ( final int faceIdx, final Mesh3 mesh,
      final Curve3 target ) {

      final int facesLen = mesh.faces.length;
      final int i = Utils.mod(faceIdx, facesLen);
      final int[][] face = mesh.faces[i];
      final int vertsLen = face.length;
      final Vec3[] vs = mesh.coords;

      final StringBuilder sb = new StringBuilder(64);
      sb.append(mesh.name);
      sb.append('.');
      Utils.toPadded(sb, i, 3);

      target.name = sb.toString();
      target.closedLoop = true;
      target.materialIndex = mesh.materialIndex;
      target.resize(vertsLen);

      final Iterator < Knot3 > itr = target.knots.iterator();
      for ( int j = 0; j < vertsLen; ++j ) {
         final int h = Utils.mod(j - 1, vertsLen);
         final int k = ( j + 1 ) % vertsLen;

         final Vec3 v0 = vs[face[h][0]];
         final Vec3 v1 = vs[face[j][0]];
         final Vec3 v2 = vs[face[k][0]];

         final Knot3 knot = itr.next();
         Curve3.lerp13(v1, v0, knot.rearHandle);
         Curve3.lerp13(v1, v2, knot.foreHandle);
         knot.coord.set(v1);
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
   public static Curve3 fromPoints ( final boolean closedLoop,
      final Vec3[] points, final Curve3 target ) {

      target.closedLoop = closedLoop;
      target.resize(points.length);
      int incr = 0;
      final Iterator < Knot3 > itr = target.knots.iterator();
      for ( ; itr.hasNext(); ++incr ) {
         final Vec3 pt = points[incr];
         itr.next().set(pt, pt, pt);
      }

      return Curve3.smoothHandles(target);
   }

   /**
    * Creates a curve that approximates Bernoulli's lemniscate, which
    * resembles an infinity loop (with equally proportioned lobes).
    *
    * @param target the output curve
    *
    * @return the lemniscate
    */
   public static Curve3 infinity ( final Curve3 target ) {

      target.name = "Infinity";
      target.closedLoop = true;
      target.resize(6);
      final Iterator < Knot3 > itr = target.knots.iterator();

      itr.next().set(0.5f, 0.0f, 0.0f, 0.5f, 0.1309615f, 0.0f, 0.5f,
         -0.1309615f, 0.0f);
      itr.next().set(0.235709f, 0.166627f, 0.0f, 0.0505335f, 0.114256f, 0.0f,
         0.361728f, 0.2022675f, 0.0f);
      itr.next().set(-0.235709f, -0.166627f, 0.0f, -0.361728f, -0.2022675f,
         0.0f, -0.0505335f, -0.114256f, 0.0f);
      itr.next().set(-0.5f, 0.0f, 0.0f, -0.5f, 0.1309615f, 0.0f, -0.5f,
         -0.1309615f, 0.0f);
      itr.next().set(-0.235709f, 0.166627f, 0.0f, -0.0505335f, 0.114256f, 0.0f,
         -0.361728f, 0.2022675f, 0.0f);
      itr.next().set(0.235709f, -0.166627f, 0.0f, 0.361728f, -0.2022675f, 0.0f,
         0.0505335f, -0.114256f, 0.0f);

      return target;
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
   public static Curve3 line ( final Vec3 origin, final Vec3 dest,
      final Curve3 target ) {

      return Curve3.line(origin.x, origin.y, origin.z, dest.x, dest.y, dest.z,
         target);
   }

   /**
    * Creates a regular convex polygon.
    *
    * @param offsetAngle the offset angle
    * @param radius      the radius
    * @param knotCount   the number of knots
    * @param target      the output curve
    *
    * @return the polygon
    *
    * @see Curve3#straightenHandles(Curve3)
    */
   public static Curve3 polygon ( final float offsetAngle, final float radius,
      final int knotCount, final Curve3 target ) {

      // TODO: overload with defaults for radius and offset angle, switch
      // knotCount to first param because it doesn't take default arg?

      final float off1 = offsetAngle * IUtils.ONE_TAU;
      final int vknct = knotCount < 3 ? 3 : knotCount;
      target.resize(vknct);
      final float invKnCt = 1.0f / vknct;

      final Iterator < Knot3 > itr = target.knots.iterator();
      final Knot3 first = itr.next();
      Knot3 prev = first;
      prev.coord.set(radius * Utils.scNorm(off1), radius * Utils.scNorm(off1
         - 0.25f), 0.0f);
      float i = 1.0f;
      while ( itr.hasNext() ) {
         final float theta1 = off1 + i * invKnCt;
         final Knot3 curr = itr.next();
         Knot3.fromSegLinear(radius * Utils.scNorm(theta1), radius * Utils
            .scNorm(theta1 - 0.25f), 0.0f, prev, curr);
         prev = curr;
         ++i;
      }
      Knot3.fromSegLinear(first.coord, prev, first);

      target.name = "Polygon";
      target.closedLoop = true;
      return target;
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
    * @see Vec3#randomCartesian(java.util.Random, Vec3, Vec3, Vec3)
    * @see Curve3#fromPoints(boolean, Vec3[], Curve3)
    */
   public static Curve3 random ( final java.util.Random rng, final int count,
      final float lowerBound, final float upperBound, final boolean closedLoop,
      final Curve3 target ) {

      final int valCount = count < 3 ? 3 : count;
      final Vec3[] points = new Vec3[valCount];
      for ( int i = 0; i < valCount; ++i ) {
         points[i] = Vec3.randomCartesian(rng, lowerBound, upperBound,
            new Vec3());
      }
      return Curve3.fromPoints(closedLoop, points, target);
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
    * @see Vec3#randomCartesian(java.util.Random, Vec3, Vec3, Vec3)
    * @see Curve3#fromPoints(boolean, Vec3[], Curve3)
    */
   public static Curve3 random ( final java.util.Random rng, final int count,
      final Vec3 lowerBound, final Vec3 upperBound, final boolean closedLoop,
      final Curve3 target ) {

      final int valCount = count < 3 ? 3 : count;
      final Vec3[] points = new Vec3[valCount];
      for ( int i = 0; i < valCount; ++i ) {
         points[i] = Vec3.randomCartesian(rng, lowerBound, upperBound,
            new Vec3());
      }

      return Curve3.fromPoints(closedLoop, points, target);
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
   public static Curve3 sampleSegment ( final int i, final Curve3 source,
      final Curve3 target ) {

      target.closedLoop = false;
      target.resize(2);

      final ArrayList < Knot3 > targetKnots = target.knots;
      final ArrayList < Knot3 > sourceKnots = source.knots;
      final int len = sourceKnots.size();

      Knot3 a = null;
      Knot3 b = null;

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
    * @see Knot3#smoothHandles(Knot3, Knot3, Knot3, Vec3)
    * @see Knot3#smoothHandlesLast(Knot3, Knot3, Vec3)
    * @see Knot3#smoothHandlesFirst(Knot3, Knot3, Vec3)
    * @see Knot3#mirrorHandlesBackward()
    * @see Knot3#mirrorHandlesForward()
    */
   public static Curve3 smoothHandles ( final Curve3 target ) {

      final ArrayList < Knot3 > knots = target.knots;
      final int knotLength = knots.size();
      if ( knotLength < 3 ) { return target; }

      final int knotLast = knotLength - 1;
      final Vec3 carry = new Vec3();

      if ( target.closedLoop ) {

         final Iterator < Knot3 > itr = knots.iterator();
         final Knot3 first = itr.next();
         Knot3 prev = knots.get(knotLast);
         Knot3 curr = first;
         while ( itr.hasNext() ) {
            final Knot3 next = itr.next();
            Knot3.smoothHandles(prev, curr, next, carry);
            prev = curr;
            curr = next;
         }
         Knot3.smoothHandles(prev, curr, first, carry);

      } else {

         Knot3 prev = knots.get(0);
         Knot3 curr = knots.get(1);
         Knot3.smoothHandlesFirst(prev, curr, carry).mirrorHandlesForward();

         for ( int i = 2; i < knotLength; ++i ) {
            final Knot3 next = knots.get(i);
            Knot3.smoothHandles(prev, curr, next, carry);
            prev = curr;
            curr = next;
         }

         Knot3.smoothHandlesLast(knots.get(knotLength - 2), knots.get(knotLast),
            carry).mirrorHandlesBackward();

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
    * @see Curve3#lerp13(Vec3, Vec3, Vec3)
    */
   public static Curve3 straightenHandles ( final Curve3 target ) {

      final ArrayList < Knot3 > knots = target.knots;
      final int knotLength = knots.size();

      if ( knotLength < 2 ) { return target; }
      if ( knotLength < 3 ) {
         final Knot3 first = knots.get(0);
         final Knot3 last = knots.get(1);

         Curve3.lerp13(first.coord, last.coord, first.foreHandle);
         first.mirrorHandlesForward();

         Curve3.lerp13(last.coord, first.coord, last.rearHandle);
         last.mirrorHandlesBackward();

         return target;
      }

      final Iterator < Knot3 > itr = knots.iterator();
      final Knot3 first = itr.next();
      Knot3 prev = null;
      Knot3 curr = first;
      while ( itr.hasNext() ) {
         prev = curr;
         curr = itr.next();
         Curve3.lerp13(prev.coord, curr.coord, prev.foreHandle);
         Curve3.lerp13(curr.coord, prev.coord, curr.rearHandle);
      }

      if ( target.closedLoop ) {
         Curve3.lerp13(first.coord, curr.coord, first.rearHandle);
         Curve3.lerp13(curr.coord, first.coord, curr.foreHandle);
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
    * @see Vec2#bezierPoint(Vec2, Vec2, Vec2, Vec2, float, Vec2)
    * @see Vec2#bezierTangent(Vec2, Vec2, Vec2, Vec2, float, Vec2)
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   @Experimental
   static Knot3 bezierKnot ( final Knot3 a, final Knot3 b, final float step,
      final Knot3 target ) {

      final Vec3 aco = a.coord;
      final Vec3 afh = a.foreHandle;
      final Vec3 bco = b.coord;
      final Vec3 brh = b.rearHandle;
      final Vec3 tco = target.coord;
      final Vec3 trh = target.rearHandle;
      final Vec3 tfh = target.foreHandle;

      Vec3.bezierPoint(aco, afh, brh, bco, step, tco);
      Vec3.bezierTangent(aco, afh, brh, bco, step, tfh);

      /* Find rear handle by reversing directions. */
      Vec3.bezierTangent(bco, brh, afh, aco, 1.0f - step, trh);

      /* Convert fore and rear handle from direction to point. */
      Vec3.add(tco, tfh, tfh);
      Vec3.add(tco, trh, trh);

      return target;
   }

   /**
    * Creates a curve which approximates a circle. This is package level to
    * provide extra functionality for translating a circle's origin when
    * parsing an SVG file.
    *
    * @param offsetAngle the angular offset
    * @param radius      the radius
    * @param knotCount   the knot count
    * @param xCenter     the x center
    * @param yCenter     the y center
    * @param zCenter     the z center
    * @param target      the output curve
    *
    * @return the circle
    */
   static Curve3 circle ( final float offsetAngle, final float radius,
      final int knotCount, final float xCenter, final float yCenter,
      final float zCenter, final Curve3 target ) {

      final float offNorm = offsetAngle * IUtils.ONE_TAU;
      final int vKnCt = knotCount < 3 ? 3 : knotCount;
      target.resize(vKnCt);
      final float invKnCt = 1.0f / vKnCt;
      final float hndlTan = 0.25f * invKnCt;
      final float hndlMag = Utils.tan(hndlTan * IUtils.TAU) * radius
         * IUtils.FOUR_THIRDS;

      float incr = 0.0f;
      final Iterator < Knot3 > itr = target.knots.iterator();
      for ( ; itr.hasNext(); ++incr ) {
         final float angNorm = offNorm + incr * invKnCt;
         Knot3.fromPolar(Utils.scNorm(angNorm), Utils.scNorm(angNorm - 0.25f),
            radius, hndlMag, xCenter, yCenter, zCenter, itr.next());
      }

      target.name = "Circle";
      target.closedLoop = true;
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
   static Vec3 lerp13 ( final Vec3 a, final Vec3 b, final Vec3 target ) {

      return target.set(IUtils.TWO_THIRDS * a.x + IUtils.ONE_THIRD * b.x,
         IUtils.TWO_THIRDS * a.y + IUtils.ONE_THIRD * b.y, IUtils.TWO_THIRDS
            * a.z + IUtils.ONE_THIRD * b.z);
   }

   /**
    * Creates a curve that forms a line with an origin and destination. This
    * is package level to provide functionality for SVG parsing.
    *
    * @param xOrigin the origin x
    * @param yOrigin the origin y
    * @param zOrigin the origin z
    * @param xDest   the destination x
    * @param yDest   the destination y
    * @param zDest   the destination z
    * @param target  the output curve
    *
    * @return the line
    */
   static Curve3 line ( final float xOrigin, final float yOrigin,
      final float zOrigin, final float xDest, final float yDest,
      final float zDest, final Curve3 target ) {

      target.resize(2);

      final Knot3 first = target.knots.get(0);
      final Knot3 last = target.knots.get(1);

      first.coord.set(xOrigin, yOrigin, zOrigin);
      last.coord.set(xDest, yDest, zDest);

      Curve3.lerp13(first.coord, last.coord, first.foreHandle);
      Curve3.lerp13(last.coord, first.coord, last.rearHandle);

      first.mirrorHandlesForward();
      last.mirrorHandlesBackward();

      target.name = "Line";
      target.closedLoop = false;
      return target;
   }

   /**
    * An easing function to facilitate animation between multiple curves.
    */
   public static class Easing implements EasingFuncArr < Curve3 >,
      EasingFuncObj < Curve3 > {

      /**
       * The knot easing function.
       */
      public final Knot3.AbstrEasing easingFunc;

      /**
       * The default constructor.
       */
      public Easing ( ) { this.easingFunc = new Knot3.Lerp(); }

      /**
       * The easing constructor
       *
       * @param easingFunc the knot easing function
       */
      public Easing ( final Knot3.AbstrEasing easingFunc ) {

         this.easingFunc = easingFunc;
      }

      /**
       * Eases between an origin and destination curve by a step in [0.0, 1.0].
       *
       * @param origin the origin
       * @param dest   the destination
       * @param step   the step
       * @param target the output curve
       *
       * @return the eased curve
       */
      @Override
      public Curve3 apply ( final Curve3 origin, final Curve3 dest,
         final Float step, final Curve3 target ) {

         if ( step <= 0.0f ) { return target.set(origin); }
         if ( step >= 1.0f ) { return target.set(dest); }
         return this.applyUnclamped(origin, dest, step, target);
      }

      /**
       * Eases between curves in an array by a step in the range [0.0, 1.0] .
       *
       * @param arr    the curve array
       * @param step   the step
       * @param target the output curve
       */
      @Override
      public Curve3 apply ( final Curve3[] arr, final Float step,
         final Curve3 target ) {

         final int len = arr.length;
         if ( len == 1 || step <= 0.0f ) { return target.set(arr[0]); }
         if ( step >= 1.0f ) { return target.set(arr[len - 1]); }

         final float scaledStep = step * ( len - 1 );
         final int i = ( int ) scaledStep;
         return this.applyUnclamped(arr[i], arr[i + 1], scaledStep - i, target);
      }

      /**
       * Eases between an origin and destination transform by a step in [0.0,
       * 1.0] . Curves must have the same number of knots and must match as to
       * whether they are closed loops or open.
       *
       * @param origin the origin
       * @param dest   the destination
       * @param step   the step
       * @param target the output curve
       *
       * @return the easing curve
       */
      public Curve3 applyUnclamped ( final Curve3 origin, final Curve3 dest,
         final float step, final Curve3 target ) {

         final ArrayList < Knot3 > orKn = origin.knots;
         final ArrayList < Knot3 > dsKn = dest.knots;

         if ( orKn.size() == dsKn.size() && origin.closedLoop
            == dest.closedLoop ) {

            target.closedLoop = origin.closedLoop;
            target.resize(orKn.size());

            final Iterator < Knot3 > orItr = orKn.iterator();
            final Iterator < Knot3 > dsItr = dsKn.iterator();
            final Iterator < Knot3 > tgItr = target.knots.iterator();
            while ( orItr.hasNext() && dsItr.hasNext() ) {
               this.easingFunc.apply(orItr.next(), dsItr.next(), step, tgItr
                  .next());
            }
         }

         return target;
      }

      /**
       * Returns a string representation of this easing function.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

}
