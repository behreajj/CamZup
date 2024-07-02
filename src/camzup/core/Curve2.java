package camzup.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import camzup.core.Utils.EasingFuncArr;
import camzup.core.Utils.EasingFuncObj;

/**
 * Implements a <a href=
 * "https://www.wikiwand.com/en/Composite_B%C3%A9zier_curve">composite</a>
 * piecewise cubic Bezier curve. Provides a function to retrieve a point
 * and tangent on a curve from a step in the range [0.0, 1.0] .<br>
 * <br>
 * The primitives available from this class are partially informed by the
 * scalable vector graphics (SVG) specification.
 */
public class Curve2 extends Curve implements Iterable < Knot2 >, ISvgWritable {

   /**
    * The list of knots contained by the curve.
    */
   private final ArrayList < Knot2 > knots = new ArrayList <>(
      ICurve.KNOT_CAPACITY);

   /**
    * The default constructor.
    */
   public Curve2 ( ) {}

   /**
    * Creates a curve from a collection of knots
    *
    * @param cl    whether or not the curve is closed
    * @param knots the collection of knots
    */
   public Curve2 ( final boolean cl, final Collection < Knot2 > knots ) {

      super(cl);
      this.appendAll(knots);
   }

   /**
    * Creates a curve from a comma-separated list of knots.
    *
    * @param cl    whether or not the curve is closed
    * @param count knot count
    *
    * @see Curve2#resize(int)
    */
   public Curve2 ( final boolean cl, final int count ) {

      super(cl);
      this.resize(count);
   }

   /**
    * Creates a curve from a comma-separated list of knots.
    *
    * @param cl    whether or not the curve is closed
    * @param knots the list of knots
    */
   public Curve2 ( final boolean cl, final Knot2... knots ) {

      super(cl);
      this.appendAll(knots);
   }

   /**
    * Constructs a copy of the source.
    *
    * @param source the source curve
    */
   public Curve2 ( final Curve2 source ) { this.set(source); }

   /**
    * Creates a named curve with two default knots.
    *
    * @param name the name
    */
   public Curve2 ( final String name ) { super(name); }

   /**
    * Creates a named curve from a collection of knots
    *
    * @param name  the name
    * @param cl    whether or not the curve is closed
    * @param knots the collection of knots
    */
   public Curve2 ( final String name, final boolean cl, final Collection <
      Knot2 > knots ) {

      super(name, cl);
      this.appendAll(knots);
   }

   /**
    * Creates a named curve and initializes its list of knots to a count.
    *
    * @param name  the name
    * @param cl    whether or not the curve is closed
    * @param count knot count
    *
    * @see Curve2#resize(int)
    */
   public Curve2 ( final String name, final boolean cl, final int count ) {

      super(name, cl);
      this.resize(count);
   }

   /**
    * Creates a named curve from a comma-separated list of knots.
    *
    * @param name  the name
    * @param cl    whether or not the curve is closed
    * @param knots the list of knots
    */
   public Curve2 ( final String name, final boolean cl, final Knot2... knots ) {

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
   public Curve2 append ( final Knot2 knot ) {

      this.knots.add(knot);

      return this;
   }

   /**
    * Append an collection of knots to the curve's list of knots.
    *
    * @param kn the collection of knots
    *
    * @return this curve
    *
    * @see Curve3#append(Knot3)
    */
   public Curve2 appendAll ( final Collection < Knot2 > kn ) {

      final Iterator < Knot2 > knItr = kn.iterator();
      while ( knItr.hasNext() ) { this.append(knItr.next()); }

      return this;
   }

   /**
    * Append an array of knots to the curve's list of knots.
    *
    * @param kn the array of knots
    *
    * @return this curve
    */
   public Curve2 appendAll ( final Knot2... kn ) {

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
   public boolean contains ( final Knot2 kn ) {

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
      return this.equals(( Curve2 ) obj);
   }

   /**
    * Flips the curve on the x axis, then reverses the curve.
    *
    * @return this curve
    *
    * @see Collections#reverse(List)
    * @see Knot2#reverse()
    */
   public Curve2 flipX ( ) {

      for ( final Knot2 kn : this.knots ) {
         kn.coord.x = -kn.coord.x;
         kn.foreHandle.x = -kn.foreHandle.x;
         kn.rearHandle.x = -kn.rearHandle.x;
         kn.reverse();
      }
      Collections.reverse(this.knots);

      return this;
   }

   /**
    * Flips the curve on the y axis, then reverses the curve.
    *
    * @return this curve
    *
    * @see Collections#reverse(List)
    * @see Knot2#reverse()
    */
   public Curve2 flipY ( ) {

      for ( final Knot2 kn : this.knots ) {
         kn.coord.y = -kn.coord.y;
         kn.foreHandle.y = -kn.foreHandle.y;
         kn.rearHandle.y = -kn.rearHandle.y;
         kn.reverse();
      }
      Collections.reverse(this.knots);

      return this;
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
   public Knot2 get ( final int i ) {

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
   public Knot2 getFirst ( ) { return this.knots.get(0); }

   /**
    * Gets the last knot in the curve.
    *
    * @return the knot
    *
    * @see List#get(int)
    * @see List#size()
    */
   public Knot2 getLast ( ) { return this.knots.get(this.knots.size() - 1); }

   /**
    * Calculates this curve's hash code based on its knots and on whether it
    * is a closed loop.
    *
    * @return the hash
    */
   @Override
   public int hashCode ( ) {

      final int hash = super.hashCode() ^ ( this.closedLoop ? 1231 : 1237 );
      return hash * IUtils.HASH_MUL ^ this.knots.hashCode();
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
   public Curve2 insert ( final int i, final Knot2 knot ) {

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
   public Curve2 insertAll ( final int i, final Collection < Knot2 > kn ) {

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
   public Curve2 insertAll ( final int i, final Knot2... kn ) {

      final int len = kn.length;
      final int vidx = this.closedLoop ? Utils.mod(i, this.knots.size() + 1)
         : i;
      int k = vidx - 1;
      for ( int j = 0; j < len; ++j ) {
         ++k;
         final Knot2 knot = kn[j];
         this.knots.add(k, knot);
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
   public Iterator < Knot2 > iterator ( ) {

      return this.knots.iterator();
   }

   /**
    * Returns an iterator, which allows access to the knots in a curve in
    * reverse order.
    *
    * @return the iterator
    */
   public Iterator < Knot2 > iteratorReverse ( ) {

      return new Iterator <>() {

         private int cursor = Curve2.this.knots.size();

         @Override
         public boolean hasNext ( ) { return this.cursor > 0; }

         @Override
         public Knot2 next ( ) {

            if ( this.hasNext() ) {
               return Curve2.this.knots.get(--this.cursor);
            }
            throw new NoSuchElementException();
         }

      };
   }

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
   public Curve2 prepend ( final Knot2 knot ) {

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
   public Curve2 prependAll ( final Collection < Knot2 > kn ) {

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
   public Curve2 prependAll ( final Knot2... kn ) {

      int j = -1;
      final int len = kn.length;
      for ( int i = 0; i < len; ++i ) {
         ++j;
         final Knot2 knot = kn[i];
         this.knots.add(j, knot);
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
    */
   public Curve2 reframe ( final Transform2 tr ) {

      tr.locPrev.set(tr.location);
      tr.scalePrev.set(tr.scale);

      final Vec2 dim = tr.scale;
      final Vec2 lb = tr.location;
      final Vec2 ub = new Vec2(-Float.MAX_VALUE, -Float.MAX_VALUE);
      lb.set(Float.MAX_VALUE, Float.MAX_VALUE);
      Curve2.accumMinMax(this, lb, ub);
      Vec2.sub(ub, lb, dim);

      lb.x = 0.5f * ( lb.x + ub.x );
      lb.y = 0.5f * ( lb.y + ub.y );
      final float scl = Utils.div(1.0f, Utils.max(dim.x, dim.y));

      for ( final Knot2 kn : this.knots ) {
         final Vec2 co = kn.coord;
         Vec2.sub(co, lb, co);
         Vec2.mul(co, scl, co);

         final Vec2 fh = kn.foreHandle;
         Vec2.sub(fh, lb, fh);
         Vec2.mul(fh, scl, fh);

         final Vec2 rh = kn.rearHandle;
         Vec2.sub(rh, lb, rh);
         Vec2.mul(rh, scl, rh);
      }

      tr.rotateTo(0.0f);

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
    *
    * @see Knot2#relocate(Vec2)
    */
   @Experimental
   public Curve2 relocateKnot ( final int i, final Vec2 v ) {

      this.get(i).relocate(v);
      return this;
   }

   /**
    * Removes a knot from the curve at a given index if the curve has more
    * than 2 knots. Returns true if the removal was successful. The target
    * knot is set to the value of the removed knot. If the curve is a closed
    * loop, wraps the index; otherwise checks that the index is in-bounds.
    *
    * @param i      the index
    * @param target the output knot
    *
    * @return the evaluation
    */
   public boolean removeAt ( final int i, final Knot2 target ) {

      final int len = this.knots.size();
      if ( len > 2 ) {
         if ( this.closedLoop ) {
            target.set(this.knots.remove(Utils.mod(i, len)));
            return true;
         }
         if ( i > -1 && i < len ) {
            target.set(this.knots.remove(i));
            return true;
         }
      }
      return false;
   }

   /**
    * Removes the first knot from the curve if the curve has more than 2
    * knots. Returns true if the removal was successful. The target knot is
    * set to the value of the removed knot.
    *
    * @param target the output knot
    *
    * @return the evaluation
    */
   public boolean removeFirst ( final Knot2 target ) {

      if ( this.knots.size() > 2 ) {
         target.set(this.knots.remove(0));
         return true;
      }
      return false;
   }

   /**
    * Removes the last knot from the curve if the curve has more than 2 knots.
    * Returns true if the removal was successful. The target knot is set to
    * the value of the removed knot.
    *
    * @param target the output knot
    *
    * @return the evaluation
    */
   public boolean removeLast ( final Knot2 target ) {

      final int len = this.knots.size();
      if ( len > 2 ) {
         target.set(this.knots.remove(len - 1));
         return true;
      }
      return false;
   }

   /**
    * Resets the curve, leaving two default knots.
    *
    * @return this curve
    */
   public Curve2 resetToDefault ( ) {

      this.resize(2);
      this.knots.get(0).set(-0.5f, 0.0f, -0.25f, 0.25f, -0.75f, -0.25f);
      this.knots.get(1).set(0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f);

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
    * @see Knot2#reverse()
    */
   public Curve2 reverse ( ) {

      Collections.reverse(this.knots);
      final Iterator < Knot2 > itr = this.knots.iterator();
      while ( itr.hasNext() ) { itr.next().reverse(); }

      return this;
   }

   /**
    * Rotates all knots in the curve by an angle in radians around the z axis.
    *
    * @param radians the angle
    *
    * @return this curve
    *
    * @see Knot2#rotateZ(float, float)
    */
   public Curve2 rotateZ ( final float radians ) {

      final double radd = radians;
      final float cosa = ( float ) Math.cos(radd);
      final float sina = ( float ) Math.sin(radd);

      final Iterator < Knot2 > itr = this.knots.iterator();
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
    * @see Knot2#scaleUnchecked(float)
    */
   public Curve2 scale ( final float scale ) {

      if ( scale != 0.0f ) {
         final Iterator < Knot2 > itr = this.knots.iterator();
         while ( itr.hasNext() ) { itr.next().scaleUnchecked(scale); }
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
    * @see Vec2#all(Vec2)
    * @see Knot2#scaleUnchecked(Vec2)
    */
   public Curve2 scale ( final Vec2 scale ) {

      if ( Vec2.all(scale) ) {
         final Iterator < Knot2 > itr = this.knots.iterator();
         while ( itr.hasNext() ) { itr.next().scaleUnchecked(scale); }
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
   public Curve2 set ( final Curve2 source ) {

      this.resize(source.length());
      final Iterator < Knot2 > srcItr = source.iterator();
      final Iterator < Knot2 > trgItr = this.knots.iterator();
      while ( srcItr.hasNext() ) { trgItr.next().set(srcItr.next()); }

      this.closedLoop = source.closedLoop;
      this.materialIndex = source.materialIndex;

      return this;
   }

   /**
    * Centers the curve about the origin, (0.0, 0.0), by calculating its
    * dimensions then subtracting the center point. Emits a transform which
    * records the curve's center point. The transform's rotation and scale are
    * reset.
    *
    * @param tr the output transform
    *
    * @return this mesh
    *
    * @see Curve2#translate(Vec2)
    */
   public Curve2 toOrigin ( final Transform2 tr ) {

      final Vec2 lb = new Vec2(Float.MAX_VALUE, Float.MAX_VALUE);
      final Vec2 ub = new Vec2(-Float.MAX_VALUE, -Float.MAX_VALUE);
      Curve2.accumMinMax(this, lb, ub);

      lb.x = -0.5f * ( lb.x + ub.x );
      lb.y = -0.5f * ( lb.y + ub.y );
      this.translate(lb);

      tr.locPrev.set(tr.location);
      Vec2.negate(lb, tr.location);

      tr.scaleTo(1.0f);
      tr.rotateTo(0.0f);

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

      return this.toString(new StringBuilder(512), places).toString();
   }

   /**
    * Renders the curve as a string containing an SVG element.
    *
    * @param zoom scaling transform
    *
    * @return the SVG string
    */
   @Override
   public String toSvgElm ( final float zoom ) {

      final StringBuilder svgp = new StringBuilder(1024);
      MaterialSolid.defaultSvgMaterial(svgp, zoom);
      this.toSvgPath(svgp, ISvgWritable.DEFAULT_WINDING_RULE);
      svgp.append("</g>\n");
      return svgp.toString();
   }

   /**
    * Renders this curve as an SVG string. A default material renders the
    * mesh's fill and stroke. The background of the SVG is transparent.
    *
    * @return the SVG string
    */
   @Override
   public String toSvgString ( ) {

      return this.toSvgString(ISvgWritable.DEFAULT_ORIGIN_X,
         ISvgWritable.DEFAULT_ORIGIN_Y, ISvgWritable.DEFAULT_WIDTH,
         ISvgWritable.DEFAULT_HEIGHT, ISvgWritable.DEFAULT_WIDTH,
         ISvgWritable.DEFAULT_HEIGHT);
   }

   /**
    * Transforms all knots in the curve by a matrix.
    *
    * @param m the matrix
    *
    * @return this curve
    *
    * @see Knot2#transform(Mat3)
    */
   public Curve2 transform ( final Mat3 m ) {

      final Iterator < Knot2 > itr = this.knots.iterator();
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
    *
    * @see Knot2#transform(Transform2)
    */
   public Curve2 transform ( final Transform2 tr ) {

      final Iterator < Knot2 > itr = this.knots.iterator();
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
    * @see Knot2#translate(Vec2)
    */
   public Curve2 translate ( final Vec2 v ) {

      final Iterator < Knot2 > itr = this.knots.iterator();
      while ( itr.hasNext() ) { itr.next().translate(v); }

      return this;
   }

   /**
    * For internal (package-level) use. Resizes a curve to the specified
    * length. The length may be no less than 2. When the new length is greater
    * than the old, new {@link Knot2}s are added.<br>
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
   Curve2 resize ( final int len ) {

      final int vlen = len < 2 ? 2 : len;
      final int oldLen = this.knots.size();
      final int diff = vlen - oldLen;
      if ( diff < 0 ) {
         final int last = oldLen - 1;
         for ( int i = 0; i < -diff; ++i ) { this.knots.remove(last - i); }
      } else if ( diff > 0 ) {
         for ( int i = 0; i < diff; ++i ) { this.knots.add(new Knot2()); }
      }

      return this;
   }

   /**
    * An internal helper function to format a vector as a Python tuple, then
    * append it to a {@link StringBuilder}. Used for testing purposes to
    * compare results with Blender 2.9x.
    *
    * @param pyCd the string builder
    * @param uRes the resolution u
    * @param z    the z offset
    *
    * @return the string builder
    */
   @Experimental
   StringBuilder toBlenderCode ( final StringBuilder pyCd, final int uRes,
      final float z ) {

      pyCd.append("{\"closed_loop\": ");
      pyCd.append(this.closedLoop ? "True" : "False");
      pyCd.append(", \"resolution_u\": ");
      pyCd.append(uRes);
      pyCd.append(", \"knots\": [");

      final Iterator < Knot2 > itr = this.knots.iterator();
      while ( itr.hasNext() ) {
         itr.next().toBlenderCode(pyCd, z);
         if ( itr.hasNext() ) { pyCd.append(',').append(' '); }
      }

      pyCd.append(']');
      pyCd.append('}');
      return pyCd;
   }

   /**
    * Internal helper function to assist with methods that need to print many
    * curves. Appends to an existing {@link StringBuilder}.
    *
    * @param sb     the string builder
    * @param places the number of places
    *
    * @return the string builder
    */
   StringBuilder toString ( final StringBuilder sb, final int places ) {

      sb.append("{ name: \"");
      sb.append(this.name);
      sb.append("\", closedLoop: ");
      sb.append(this.closedLoop);
      sb.append(", materialIndex: ");
      sb.append(this.materialIndex);
      sb.append(", knots: [ ");

      final Iterator < Knot2 > itr = this.knots.iterator();
      while ( itr.hasNext() ) {
         itr.next().toString(sb, places);
         if ( itr.hasNext() ) { sb.append(',').append(' '); }
      }

      sb.append(" ] }");
      return sb;
   }

   /**
    * Internal helper function to append a curve path to a
    * {@link StringBuilder}; the id is written to the path's id. The fill rule
    * may be either <code>"evenodd"</code> or <code>"nonzero"</code>
    * (default).
    *
    * @param svgp     the string builder
    * @param fillRule the fill rule
    *
    * @return the string builder.
    */
   StringBuilder toSvgPath ( final StringBuilder svgp, final String fillRule ) {

      if ( this.knots.size() < 2 ) { return svgp; }

      svgp.append("<path id=\"");
      svgp.append(this.name.toLowerCase());
      svgp.append("\" class=\"");
      svgp.append(this.getClass().getSimpleName().toLowerCase());
      svgp.append("\" fill-rule=\"");
      svgp.append(fillRule);
      svgp.append("\" d=\"");
      this.toSvgSubPath(svgp);
      svgp.append("\" />\n");
      return svgp;
   }

   /**
    * Internal helper function to draw a sub-path within a path's data
    * command. Its separation allows for curves to be rendered as multiple
    * sub-paths rather than one big path.
    *
    * @param svgp the string builder
    *
    * @return the string builder
    */
   StringBuilder toSvgSubPath ( final StringBuilder svgp ) {

      final Iterator < Knot2 > itr = this.knots.iterator();
      final Knot2 firstKnot = itr.next();
      Knot2 prevKnot = firstKnot;

      svgp.append('M');
      svgp.append(' ');
      prevKnot.coord.toSvgString(svgp, ' ');
      while ( itr.hasNext() ) {
         final Knot2 currKnot = itr.next();

         final Vec2 fhPrev = prevKnot.foreHandle;
         final Vec2 rhNext = currKnot.rearHandle;
         final Vec2 coNext = currKnot.coord;

         svgp.append(' ');
         svgp.append('C');
         svgp.append(' ');
         fhPrev.toSvgString(svgp, ' ');
         svgp.append(',');
         rhNext.toSvgString(svgp, ' ');
         svgp.append(',');
         coNext.toSvgString(svgp, ' ');

         prevKnot = currKnot;
      }

      if ( this.closedLoop ) {
         final Vec2 fhPrev = prevKnot.foreHandle;
         final Vec2 rhNext = firstKnot.rearHandle;
         final Vec2 coNext = firstKnot.coord;

         svgp.append(' ');
         svgp.append('C');
         svgp.append(' ');
         fhPrev.toSvgString(svgp, ' ');
         svgp.append(',');
         rhNext.toSvgString(svgp, ' ');
         svgp.append(',');
         coNext.toSvgString(svgp, ' ');
         svgp.append(' ');
         svgp.append('Z');
      }

      return svgp;
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
   protected boolean equals ( final Curve2 curve ) {

      return this.closedLoop == curve.closedLoop && this.knots.equals(
         curve.knots);
   }

   /**
    * Creates an arc from a stop angle. The start angle is presumed to be 0.0
    * degrees.
    *
    * @param stopAngle the stop angle
    * @param target    the output curve
    *
    * @return the arc
    */
   public static Curve2 arc ( final float stopAngle, final Curve2 target ) {

      return Curve2.arc(0.0f, stopAngle, target);
   }

   /**
    * Creates an arc from a start and stop angle.
    *
    * @param startAngle the start angle
    * @param stopAngle  the stop angle
    * @param target     the output curve
    *
    * @return the arc
    */
   public static Curve2 arc ( final float startAngle, final float stopAngle,
      final Curve2 target ) {

      return Curve2.arc(startAngle, stopAngle, 0.5f, target);
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
   public static Curve2 arc ( final float startAngle, final float stopAngle,
      final float radius, final ArcMode arcMode, final Curve2 target ) {

      /*
       * Optimized where possible because Yup2 renderer uses this to display
       * arcs. Outlier case where arc is used as a progress bar. The tolerance
       * is less than half a degree, 1.0 / 720.0, which is the minimum step used
       * by the Processing sine cosine look-up table (LUT).
       */
      if ( Utils.approx(stopAngle - startAngle, IUtils.TAU, 0.00139f) ) {
         return Curve2.circle(ICurve.KNOTS_PER_CIRCLE, startAngle, radius, 0.0f,
            0.0f, target);
      }

      /* Divide by TAU then wrap around the range, [0.0, 1.0] . */
      final float a1 = Utils.mod1(startAngle * IUtils.ONE_TAU);
      final float b1 = Utils.mod1(stopAngle * IUtils.ONE_TAU);

      /* Find arc length and destination angle from the origin (a1). */
      final float arcLen1 = Utils.mod1(b1 - a1);

      /* Edge case: angles are equal. */
      if ( arcLen1 <= 0.00139f ) {
         return Curve2.line(new Vec2(), Vec2.fromPolar(startAngle, radius,
            new Vec2()), target);
      }

      final float destAngle1 = a1 + arcLen1;

      /*
       * Find the number of knots needed to accurately represent the arc. It's
       * assumed that 4 curves adequately represent a full circle; at least one
       * knot is needed, hence the +1. Ceiling to an integer is inlined.
       */
      final float xf = 1.0f + 4.0f * arcLen1;
      final int xi = ( int ) xf;
      final int knotCount = xf > xi ? xi + 1 : xi;
      final float toStep = 1.0f / ( knotCount - 1.0f );

      /*
       * Find the magnitude of the curve handles (or control points for each
       * knot. Multiply toStep by arcLen1 to find the arc-length that each curve
       * has to cover, then divide by four. This is then supplied to tangent.
       */
      final float hndtn = 0.25f * toStep * arcLen1;

      /*
       * The tangent function ( tan ( x ) := sin ( x ) / cos ( x ) ). The result
       * is multiplied by 4 / 3 (picture a circle enclosed by a square, and the
       * osculating edges), then by the radius.
       */
      final float handleMag = Utils.tan(hndtn * IUtils.TAU) * radius
         * IUtils.FOUR_THIRDS;

      /*
       * Clears the list of knots rather than doing any partial reassignment.
       * Depends on what kind of underlying list (e.g., array or linked) is
       * used.
       */
      target.resize(knotCount);
      final ArrayList < Knot2 > knots = target.knots;
      for ( int i = 0; i < knotCount; ++i ) {
         final Knot2 knot = knots.get(i);
         final float t = i * toStep;
         final float angle1 = ( 1.0f - t ) * a1 + t * destAngle1;
         Knot2.fromPolar(Utils.scNorm(angle1), Utils.scNorm(angle1 - 0.25f),
            radius, handleMag, 0.0f, 0.0f, knot);
      }

      /* Depending on arc mode, calculate chord or legs. */
      target.closedLoop = arcMode != ArcMode.OPEN;
      if ( target.closedLoop ) {

         final Knot2 first = knots.get(0);
         final Knot2 last = knots.get(knotCount - 1);

         final Vec2 coLast = last.coord;
         final Vec2 coFirst = first.coord;

         if ( arcMode == ArcMode.CHORD ) {

            /* Flatten the first to last handles. */
            last.foreHandle.set(IUtils.TWO_THIRDS * coLast.x + IUtils.ONE_THIRD
               * coFirst.x, IUtils.TWO_THIRDS * coLast.y + IUtils.ONE_THIRD
                  * coFirst.y);

            first.rearHandle.set(IUtils.TWO_THIRDS * coFirst.x
               + IUtils.ONE_THIRD * coLast.x, IUtils.TWO_THIRDS * coFirst.y
                  + IUtils.ONE_THIRD * coLast.y);

         } else if ( arcMode == ArcMode.PIE ) {

            /* Add a center knot. */
            final Knot2 center = new Knot2();
            final Vec2 coCenter = center.coord;
            knots.add(center);

            final float cox23 = IUtils.TWO_THIRDS * coCenter.x;
            final float coy23 = IUtils.TWO_THIRDS * coCenter.y;
            final float cox13 = IUtils.ONE_THIRD * coCenter.x;
            final float coy13 = IUtils.ONE_THIRD * coCenter.y;

            /* Flatten center handles. */
            center.rearHandle.set(cox23 + IUtils.ONE_THIRD * coLast.x, coy23
               + IUtils.ONE_THIRD * coLast.y);
            center.foreHandle.set(cox23 + IUtils.ONE_THIRD * coFirst.x, coy23
               + IUtils.ONE_THIRD * coFirst.y);

            /* Flatten handle from first to center. */
            first.rearHandle.set(IUtils.TWO_THIRDS * coFirst.x + cox13,
               IUtils.TWO_THIRDS * coFirst.y + coy13);

            /* Flatten handle from last to center. */
            last.foreHandle.set(IUtils.TWO_THIRDS * coLast.x + cox13,
               IUtils.TWO_THIRDS * coLast.y + coy13);
         }
      }

      target.name = "Arc";
      return target;
   }

   /**
    * Creates an arc from a start and stop angle. The arc is an open arc.
    *
    * @param startAngle the start angle
    * @param stopAngle  the stop angle
    * @param radius     the arc radius
    * @param target     the output curve
    *
    * @return the arc
    */
   public static Curve2 arc ( final float startAngle, final float stopAngle,
      final float radius, final Curve2 target ) {

      return Curve2.arc(startAngle, stopAngle, radius, ArcMode.OPEN, target);
   }

   /**
    * Calculates an Axis-Aligned Bounding Box (AABB) encompassing the curve.
    * Does so by taking the minimum and maximum of each knot's coordinate,
    * fore handle and rear handle; <em>not</em> by finding the curve extrema.
    *
    * @param curve  the curve
    * @param target the output dimensions
    *
    * @return the dimensions
    */
   @Experimental
   public static Bounds2 calcBounds ( final Curve2 curve,
      final Bounds2 target ) {

      /*
       * For information on finding AABB by extrema:
       * http://nishiohirokazu.blogspot.com/2009/06/how-to-calculate-bezier-
       * curves-bounding.html , https://stackoverflow.com/questions/2587751/
       * an-algorithm-to-find-bounding-box-of-closed-bezier-curves ,
       * https://computergraphics.stackexchange.com/questions/3697/algorithm-to-
       * find-the-center-of-a-bezier-curve ,
       * https://pomax.github.io/bezierinfo/#derivatives
       */

      target.set(Float.MAX_VALUE, -Float.MAX_VALUE);
      Curve2.accumMinMax(curve, target.min, target.max);
      return target;
   }

   /**
    * Creates a curve which approximates a circle.
    *
    * @param target the output curve
    *
    * @return the circle
    */
   public static Curve2 circle ( final Curve2 target ) {

      return Curve2.circle(ICurve.KNOTS_PER_CIRCLE, target);
   }

   /**
    * Creates a curve which approximates a circle.
    *
    * @param sectors the knot count
    * @param target  the output curve
    *
    * @return the circle
    */
   public static Curve2 circle ( final int sectors, final Curve2 target ) {

      return Curve2.circle(sectors, 0.0f, 0.5f, target);
   }

   /**
    * Creates a curve which approximates a circle.
    *
    * @param sectors     the knot count
    * @param offsetAngle the angular offset
    * @param radius      the radius
    * @param target      the output curve
    *
    * @return the circle
    */
   public static Curve2 circle ( final int sectors, final float offsetAngle,
      final float radius, final Curve2 target ) {

      return Curve2.circle(sectors, offsetAngle, radius, 0.0f, 0.0f, target);
   }

   /**
    * Creates a curve which approximates an ellipse; currently there is
    * support only for 4 knot ellipses. The aspect ratio is that between the
    * ellipse's major and minor axes.
    *
    * @param aspect the aspect
    * @param target the output curve
    *
    * @return the ellipse
    */
   public static Curve2 ellipse ( final float aspect, final Curve2 target ) {

      return Curve2.ellipse(0.5f, aspect, 0.0f, 0.0f, target);
   }

   /**
    * Evaluates a step in the range [0.0, 1.0] , returning a knot on the
    * curve. The knot's fore handle and rear handle are mirrored.
    *
    * @param curve  the curve
    * @param step   the step
    * @param target the output knot
    *
    * @return the knot
    */
   @Experimental
   public static Knot2 eval ( final Curve2 curve, final float step,
      final Knot2 target ) {

      final ArrayList < Knot2 > knots = curve.knots;
      final int knotLength = knots.size();

      float tScaled;
      int i = 0;
      Knot2 a = null;
      Knot2 b = null;
      if ( curve.closedLoop ) {
         tScaled = Utils.mod1(step) * knotLength;
         i = ( int ) tScaled;
         a = knots.get(Utils.mod(i, knotLength));
         b = knots.get(Utils.mod(i + 1, knotLength));
      } else {
         if ( knotLength == 1 || step <= 0.0f ) {
            Curve2.evalFirst(curve, target);
            return target;
         }
         if ( step >= 1.0f ) {
            Curve2.evalLast(curve, target);
            return target;
         }

         tScaled = step * ( knotLength - 1 );
         i = ( int ) tScaled;
         a = knots.get(i);
         b = knots.get(i + 1);
      }

      final float t = tScaled - i;
      final float u = 1.0f - t;
      Curve2.bezierKnot(a, b, t, target);

      // QUERY Should this logic be moved to bezierKnot?
      final float aFhMag = Knot2.foreMag(a);
      final float bFhMag = Knot2.foreMag(b);
      final float tFhMag = u * aFhMag + t * bFhMag;
      target.scaleForeHandleTo(tFhMag);

      final float aRhMag = Knot2.rearMag(a);
      final float bRhMag = Knot2.rearMag(b);
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
    * @see Curve2#eval(Curve2, float, Vec2, Vec2)
    */
   public static Ray2 eval ( final Curve2 curve, final float step,
      final Ray2 ray ) {

      Curve2.eval(curve, step, ray.origin, ray.dir);
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
    * @see Knot2#bezierPoint(Knot2, Knot2, float, Vec2)
    * @see Knot2#bezierTanUnit(Knot2, Knot2, float, Vec2)
    */
   public static Vec2 eval ( final Curve2 curve, final float step,
      final Vec2 coord, final Vec2 tangent ) {

      final ArrayList < Knot2 > knots = curve.knots;
      final int knotLength = knots.size();

      float tScaled;
      int i = 0;
      Knot2 a = null;
      Knot2 b = null;
      if ( curve.closedLoop ) {
         tScaled = Utils.mod1(step) * knotLength;
         i = ( int ) tScaled;
         a = knots.get(Utils.mod(i, knotLength));
         b = knots.get(Utils.mod(i + 1, knotLength));
      } else {
         if ( knotLength == 1 || step <= 0.0f ) {
            return Curve2.evalFirst(curve, coord, tangent);
         }
         if ( step >= 1.0f ) { return Curve2.evalLast(curve, coord, tangent); }

         tScaled = step * ( knotLength - 1 );
         i = ( int ) tScaled;
         a = knots.get(i);
         b = knots.get(i + 1);
      }

      final float t = tScaled - i;
      Knot2.bezierPoint(a, b, t, coord);
      Knot2.bezierTanUnit(a, b, t, tangent);

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
    */
   public static Transform2 evalFirst ( final Curve2 curve,
      final Handedness handedness, final Transform2 target ) {

      target.locPrev.set(target.location);
      target.rotPrev = target.rotation;
      Curve2.evalFirst(curve, target.location, target.forward);

      Vec2.normalize(target.forward, target.forward);
      if ( handedness == Handedness.LEFT ) {
         Vec2.perpendicularCCW(target.forward, target.right);
      } else {
         Vec2.perpendicularCW(target.forward, target.right);
      }

      target.rotation = Vec2.headingSigned(target.right);
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
   public static Knot2 evalFirst ( final Curve2 curve, final Knot2 target ) {

      target.set(curve.knots.get(0));
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
   public static Ray2 evalFirst ( final Curve2 curve, final Ray2 ray ) {

      Curve2.evalFirst(curve, ray.origin, ray.dir);
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
    * @see Vec2#subNorm(Vec2, Vec2, Vec2)
    */
   public static Vec2 evalFirst ( final Curve2 curve, final Vec2 coord,
      final Vec2 tangent ) {

      final Knot2 kFirst = curve.knots.get(0);
      coord.set(kFirst.coord);
      Vec2.subNorm(kFirst.foreHandle, coord, tangent);
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
    */
   public static Transform2 evalLast ( final Curve2 curve,
      final Handedness handedness, final Transform2 target ) {

      target.locPrev.set(target.location);
      target.rotPrev = target.rotation;
      Curve2.evalLast(curve, target.location, target.forward);

      Vec2.normalize(target.forward, target.forward);
      if ( handedness == Handedness.LEFT ) {
         Vec2.perpendicularCCW(target.forward, target.right);
      } else {
         Vec2.perpendicularCW(target.forward, target.right);
      }

      target.rotation = Vec2.headingSigned(target.right);
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
   public static Knot2 evalLast ( final Curve2 curve, final Knot2 target ) {

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
   public static Ray2 evalLast ( final Curve2 curve, final Ray2 ray ) {

      Curve2.evalLast(curve, ray.origin, ray.dir);
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
    * @see Vec2#subNorm(Vec2, Vec2, Vec2)
    */
   public static Vec2 evalLast ( final Curve2 curve, final Vec2 coord,
      final Vec2 tangent ) {

      final Knot2 kLast = curve.knots.get(curve.knots.size() - 1);
      coord.set(kLast.coord);
      Vec2.subNorm(coord, kLast.rearHandle, tangent);
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
    * @see Knot2#fromSegCatmull(Vec2, Vec2, Vec2, Vec2, float, Knot2, Knot2)
    * @see Knot2#mirrorHandlesForward()
    * @see Knot2#mirrorHandlesBackward()
    */
   public static Curve2 fromCatmull ( final boolean closedLoop,
      final Vec2[] points, final float tightness, final Curve2 target ) {

      final int ptsLen = points.length;
      if ( ptsLen < 2 ) { return target; }
      if ( ptsLen < 3 ) {
         return Curve2.fromCatmull(false, new Vec2[] { points[0], points[0],
            points[1], points[1] }, tightness, target);
      }
      if ( ptsLen < 4 ) {
         return Curve2.fromCatmull(false, new Vec2[] { points[0], points[0],
            points[1], points[2], points[2] }, tightness, target);
      }

      target.closedLoop = closedLoop;
      target.name = "Catmull";

      final int knotCount = closedLoop ? ptsLen : ptsLen - 2;
      target.resize(knotCount);
      final Iterator < Knot2 > itr = target.iterator();
      final Knot2 first = itr.next();
      final int ptsLast = ptsLen - 1;

      int idx = 0;
      Knot2 prev = first;
      Knot2 curr;
      for ( curr = null; itr.hasNext(); ++idx ) {
         int idx1 = idx + 1;
         int idx2 = idx + 2;
         int idx3 = idx + 3;

         if ( closedLoop ) {
            idx1 %= ptsLen;
            idx2 %= ptsLen;
            idx3 %= ptsLen;
         } else if ( idx3 > ptsLast ) { idx3 = ptsLast; }

         curr = itr.next();
         Knot2.fromSegCatmull(points[idx], points[idx1], points[idx2],
            points[idx3], tightness, prev, curr);
         prev = curr;
      }

      if ( closedLoop ) {
         Knot2.fromSegCatmull(points[ptsLast], points[0], points[1], points[2],
            tightness, curr, first);
      } else if ( curr != null ) {
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
    * @see Curve2#lerp13(Vec2, Vec2, Vec2)
    */
   public static Curve2 fromMeshFace ( final Face2 face, final Curve2 target ) {

      target.closedLoop = true;
      target.name = "Face";

      final Vert2[] verts = face.vertices;
      final int vertsLen = verts.length;
      target.resize(vertsLen);
      final Iterator < Knot2 > itr = target.knots.iterator();

      for ( int i = 0; i < vertsLen; ++i ) {
         final int h = Utils.mod(i - 1, vertsLen);
         final int j = ( i + 1 ) % vertsLen;
         final Vec2 v1 = verts[i].coord;
         final Knot2 knot = itr.next();
         Curve2.lerp13(v1, verts[h].coord, knot.rearHandle);
         Curve2.lerp13(v1, verts[j].coord, knot.foreHandle);
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
    * @see Curve2#lerp13(Vec2, Vec2, Vec2)
    */
   public static Curve2 fromMeshFace ( final int faceIdx, final Mesh2 mesh,
      final Curve2 target ) {

      final int facesLen = mesh.faces.length;
      final int i = Utils.mod(faceIdx, facesLen);
      final int[][] face = mesh.faces[i];
      final int vertsLen = face.length;
      final Vec2[] vs = mesh.coords;

      final StringBuilder sb = new StringBuilder(64);
      sb.append(mesh.name);
      sb.append('.');
      Utils.toPadded(sb, i, 3);

      target.name = sb.toString();
      target.closedLoop = true;
      target.materialIndex = mesh.materialIndex;
      target.resize(vertsLen);

      final Iterator < Knot2 > itr = target.knots.iterator();
      for ( int j = 0; j < vertsLen; ++j ) {
         final int h = Utils.mod(j - 1, vertsLen);
         final int k = ( j + 1 ) % vertsLen;

         final Vec2 v0 = vs[face[h][0]];
         final Vec2 v1 = vs[face[j][0]];
         final Vec2 v2 = vs[face[k][0]];

         final Knot2 knot = itr.next();
         Curve2.lerp13(v1, v0, knot.rearHandle);
         Curve2.lerp13(v1, v2, knot.foreHandle);
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
    *
    * @see Curve2#straightHandles(Curve2)
    * @see Curve2#smoothHandles(Curve2)
    */
   public static Curve2 fromPoints ( final boolean closedLoop,
      final Vec2[] points, final Curve2 target ) {

      final int ptsLen = points.length;
      if ( ptsLen < 2 ) { return target; }
      target.resize(ptsLen);

      final Iterator < Knot2 > itr = target.knots.iterator();
      for ( int i = 0; itr.hasNext(); ++i ) {
         final Vec2 pt = points[i];
         itr.next().set(pt, pt, pt);
      }

      target.closedLoop = closedLoop;
      target.name = "Points";

      return ptsLen < 3 ? Curve2.straightHandles(target) : Curve2.smoothHandles(
         target);
   }

   /**
    * Creates a curve from a series of points, where every other point
    * represents a control point in a quadratic curve.
    *
    * @param closedLoop whether the curve is a closed loop
    * @param points     the array of points
    * @param target     the output curve
    *
    * @return the curve
    */
   public static Curve2 fromQuadratic ( final boolean closedLoop,
      final Vec2[] points, final Curve2 target ) {

      final int ptsLen = points.length;
      if ( ptsLen < 3 ) { return Curve2.fromPoints(false, points, target); }
      final int knotCount = ptsLen / 2 + ( closedLoop ? 0 : 1 );
      if ( knotCount < 2 || ( closedLoop ? ptsLen % 2 != 0 : ( ptsLen + 1 ) % 2
         != 0 ) ) {
         System.err.println("Incorrect number of points.");
         return target;
      }

      target.resize(knotCount);
      final ArrayList < Knot2 > knots = target.knots;
      final Iterator < Knot2 > itr = knots.iterator();

      final Knot2 first = itr.next();
      Knot2 prev = first;
      Knot2 curr = null;
      for ( int i = 1; itr.hasNext(); i += 2 ) {
         curr = itr.next();
         Knot2.fromSegQuadratic(points[i], points[i + 1], prev, curr);
         prev = curr;
      }

      if ( closedLoop ) {
         Knot2.fromSegQuadratic(points[ptsLen - 1], points[0], curr, first);
      } else if ( curr != null ) {
         first.coord.set(points[0]);
         first.mirrorHandlesForward();
         curr.mirrorHandlesBackward();
      }

      target.closedLoop = closedLoop;
      target.name = "Quadratic";
      return target;
   }

   /**
    * Creates a curve that approximates Bernoulli's lemniscate, which
    * resembles an infinity loop (with equally proportioned lobes).<br>
    * <br>
    * The curve is set as a closed loop; however, it intersects itself, and so
    * may lead to rendering issues when used with a fill.
    *
    * @param target the output curve
    *
    * @return the lemniscate
    */
   public static Curve2 infinity ( final Curve2 target ) {

      target.name = "Infinity";
      target.closedLoop = true;
      target.resize(6);
      final Iterator < Knot2 > itr = target.knots.iterator();

      itr.next().set(0.5f, 0.0f, 0.5f, 0.1309615f, 0.5f, -0.1309615f);
      itr.next().set(0.235709f, 0.166627f, 0.0505335f, 0.114256f, 0.361728f,
         0.2022675f);
      itr.next().set(-0.235709f, -0.166627f, -0.361728f, -0.2022675f,
         -0.0505335f, -0.114256f);
      itr.next().set(-0.5f, 0.0f, -0.5f, 0.1309615f, -0.5f, -0.1309615f);
      itr.next().set(-0.235709f, 0.166627f, -0.0505335f, 0.114256f, -0.361728f,
         0.2022675f);
      itr.next().set(0.235709f, -0.166627f, 0.361728f, -0.2022675f, 0.0505335f,
         -0.114256f);

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
   public static Curve2 line ( final Vec2 origin, final Vec2 dest,
      final Curve2 target ) {

      return Curve2.line(origin.x, origin.y, dest.x, dest.y, target);
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
    * @see Vec2#randomCartesian(Random, float, float, Vec2)
    * @see Curve2#fromPoints(boolean, Vec2[], Curve2)
    */
   public static Curve2 random ( final Random rng, final int count,
      final float lowerBound, final float upperBound, final boolean closedLoop,
      final Curve2 target ) {

      final int valCount = count < 3 ? 3 : count;
      final Vec2[] points = new Vec2[valCount];
      for ( int i = 0; i < valCount; ++i ) {
         points[i] = Vec2.randomCartesian(rng, lowerBound, upperBound,
            new Vec2());
      }

      return Curve2.fromPoints(closedLoop, points, target);
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
    * @see Vec2#randomCartesian(Random, Vec2, Vec2, Vec2)
    * @see Curve2#fromPoints(boolean, Vec2[], Curve2)
    */
   public static Curve2 random ( final Random rng, final int count,
      final Vec2 lowerBound, final Vec2 upperBound, final boolean closedLoop,
      final Curve2 target ) {

      final int valCount = count < 3 ? 3 : count;
      final Vec2[] points = new Vec2[valCount];
      for ( int i = 0; i < valCount; ++i ) {
         points[i] = Vec2.randomCartesian(rng, lowerBound, upperBound,
            new Vec2());
      }

      return Curve2.fromPoints(closedLoop, points, target);
   }

   /**
    * Sets a target curve to a rectangle.
    *
    * @param target the output curve
    *
    * @return the rectangle
    */
   public static Curve2 rect ( final Curve2 target ) {

      return Curve2.rect(0.0f, target);
   }

   /**
    * Sets a target curve to a rectangle. The first parameter specifies the
    * corner rounding factor.
    *
    * @param rounding corner rounding
    * @param target   the output curve
    *
    * @return the rectangle
    */
   public static Curve2 rect ( final float rounding, final Curve2 target ) {

      return Curve2.rect(-0.5f, 0.5f, 0.5f, -0.5f, rounding, target);
   }

   /**
    * Sets a target curve to a rectangle. The first coordinate specifies the
    * top left corner; the second coordinate specifies the bottom right
    * corner.
    *
    * @param tl     the top left corner
    * @param br     the bottom right corner
    * @param target the output curve
    *
    * @return the rectangle
    */
   public static Curve2 rect ( final Vec2 tl, final Vec2 br,
      final Curve2 target ) {

      return Curve2.rect(tl.x, tl.y, br.x, br.y, 0.0f, target);
   }

   /**
    * Sets a target curve to a rectangle. The first coordinate specifies the
    * top left corner; the second coordinate specifies the bottom right
    * corner. The third parameter specifies the corner rounding factor.
    *
    * @param tl     the top left corner
    * @param br     the bottom right corner
    * @param corner the rounding factor
    * @param target the output curve
    *
    * @return the rounded rectangle
    */
   public static Curve2 rect ( final Vec2 tl, final Vec2 br, final float corner,
      final Curve2 target ) {

      return Curve2.rect(tl.x, tl.y, br.x, br.y, corner, target);
   }

   /**
    * Sets a target curve to a rectangle. The first coordinate specifies the
    * top left corner; the second coordinate, specifies the bottom right
    * corner. The next four parameters specify the rounding factor for the top
    * left, top right, bottom right and bottom left corners.
    *
    * @param tl       the top left corner
    * @param br       the bottom right corner
    * @param tlCorner rounding top left corner
    * @param trCorner rounding top right corner
    * @param brCorner rounding bottom right corner
    * @param blCorner rounding bottom left corner
    * @param target   the output curve
    *
    * @return the rounded rectangle
    */
   public static Curve2 rect ( final Vec2 tl, final Vec2 br,
      final float tlCorner, final float trCorner, final float brCorner,
      final float blCorner, final Curve2 target ) {

      return Curve2.rect(tl.x, tl.y, br.x, br.y, tlCorner, trCorner, brCorner,
         blCorner, target);
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
   public static Curve2 sampleSegment ( final int i, final Curve2 source,
      final Curve2 target ) {

      target.closedLoop = false;
      target.resize(2);

      final ArrayList < Knot2 > targetKnots = target.knots;
      final ArrayList < Knot2 > sourceKnots = source.knots;
      final int len = sourceKnots.size();

      Knot2 a = null;
      Knot2 b = null;

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
    * Samples all segments of a source curve into an array of curves.
    *
    * @param source the input curve
    *
    * @return the result array
    */
   public static Curve2[] sampleSegments ( final Curve2 source ) {

      final boolean cl = source.closedLoop;
      final ArrayList < Knot2 > knots = source.knots;
      final Iterator < Knot2 > itr = knots.iterator();
      final int knotLength = knots.size();
      final int resLen = cl || knotLength < 3 ? knotLength : knotLength - 1;

      final Curve2[] result = new Curve2[resLen];
      final Knot2 first = itr.next();
      Knot2 prev = first;
      Knot2 curr = null;

      for ( int idx = 0; itr.hasNext(); ++idx ) {
         curr = itr.next();
         final Curve2 curve = new Curve2();
         curve.append(new Knot2(prev).mirrorHandlesForward());
         curve.append(new Knot2(curr).mirrorHandlesBackward());
         result[idx] = curve;
         prev = curr;
      }

      if ( cl ) {
         final Curve2 curve = new Curve2();
         curve.append(new Knot2(prev).mirrorHandlesForward());
         curve.append(new Knot2(first).mirrorHandlesBackward());
         result[resLen - 1] = curve;
      }

      return result;
   }

   /**
    * Adjusts knot handles to create a smooth, continuous curve.
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
   public static Curve2 smoothHandles ( final Curve2 target ) {

      final ArrayList < Knot2 > knots = target.knots;
      final int knotCount = knots.size();
      if ( knotCount < 3 ) { return target; }

      final Vec2 carry = new Vec2();
      final Iterator < Knot2 > itr = knots.iterator();
      final Knot2 first = itr.next();

      if ( target.closedLoop ) {

         Knot2 prev = knots.get(knotCount - 1);
         Knot2 curr = first;
         while ( itr.hasNext() ) {
            final Knot2 next = itr.next();
            Knot2.smoothHandles(prev, curr, next, carry);
            prev = curr;
            curr = next;
         }
         Knot2.smoothHandles(prev, curr, first, carry);

      } else {

         Knot2 prev = first;
         Knot2 curr = itr.next();
         Knot2.smoothHandlesFirst(prev, curr, carry);
         curr.mirrorHandlesForward();

         while ( itr.hasNext() ) {
            final Knot2 next = itr.next();
            Knot2.smoothHandles(prev, curr, next, carry);
            prev = curr;
            curr = next;
         }

         Knot2.smoothHandlesLast(prev, curr, carry);
         curr.mirrorHandlesBackward();
      }

      return target;
   }

   /**
    * Adjusts knot handles to create straight line segments.
    *
    * @param target the output curve
    *
    * @return the curve
    *
    * @see Curve2#lerp13(Vec2, Vec2, Vec2)
    * @see Knot2#mirrorHandlesBackward()
    * @see Knot2#mirrorHandlesForward()
    */
   public static Curve2 straightHandles ( final Curve2 target ) {

      final ArrayList < Knot2 > knots = target.knots;
      final int knotLength = knots.size();

      if ( knotLength < 2 ) { return target; }
      if ( knotLength < 3 ) {
         final Knot2 first = knots.get(0);
         final Knot2 last = knots.get(1);

         Curve2.lerp13(first.coord, last.coord, first.foreHandle);
         first.mirrorHandlesForward();

         Curve2.lerp13(last.coord, first.coord, last.rearHandle);
         last.mirrorHandlesBackward();

         return target;
      }

      final Iterator < Knot2 > itr = knots.iterator();
      final Knot2 first = itr.next();
      Knot2 prev = null;
      Knot2 curr = first;
      while ( itr.hasNext() ) {
         prev = curr;
         curr = itr.next();
         Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
         Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);
      }

      if ( target.closedLoop ) {
         Curve2.lerp13(first.coord, curr.coord, first.rearHandle);
         Curve2.lerp13(curr.coord, first.coord, curr.foreHandle);
      } else {
         first.mirrorHandlesForward();
         curr.mirrorHandlesBackward();
      }

      return target;
   }

   /**
    * An internal helper function to accumulate the minimum and maximum points
    * in a curve.
    *
    * @param curve the curve
    * @param lb    the lower bound
    * @param ub    the upper bound
    */
   static void accumMinMax ( final Curve2 curve, final Vec2 lb,
      final Vec2 ub ) {

      for ( final Knot2 kn : curve.knots ) {
         final Vec2 co = kn.coord;
         final Vec2 fh = kn.foreHandle;
         final Vec2 rh = kn.rearHandle;

         if ( co.x < lb.x ) { lb.x = co.x; }
         if ( co.x > ub.x ) { ub.x = co.x; }
         if ( co.y < lb.y ) { lb.y = co.y; }
         if ( co.y > ub.y ) { ub.y = co.y; }

         if ( fh.x < lb.x ) { lb.x = fh.x; }
         if ( fh.x > ub.x ) { ub.x = fh.x; }
         if ( fh.y < lb.y ) { lb.y = fh.y; }
         if ( fh.y > ub.y ) { ub.y = fh.y; }

         if ( rh.x < lb.x ) { lb.x = rh.x; }
         if ( rh.x > ub.x ) { ub.x = rh.x; }
         if ( rh.y < lb.y ) { lb.y = rh.y; }
         if ( rh.y > ub.y ) { ub.y = rh.y; }
      }
   }

   /**
    * An internal helper function to accumulate the minimum and maximum points
    * in a curve. This may be called either by a single curve, or by a curve
    * entity seeking the minimum and maximum for a collection of curves.
    *
    * @param curve the curve
    * @param lb    the lower bound
    * @param ub    the upper bound
    * @param tr    the transform
    * @param fh    the fore handle
    * @param rh    the rear handle
    * @param co    the coordinate
    *
    * @see Transform2#mulPoint(Transform2, Vec2, Vec2)
    */
   static void accumMinMax ( final Curve2 curve, final Vec2 lb, final Vec2 ub,
      final Transform2 tr, final Vec2 fh, final Vec2 rh, final Vec2 co ) {

      for ( final Knot2 kn : curve.knots ) {
         Transform2.mulPoint(tr, kn.foreHandle, fh);
         Transform2.mulPoint(tr, kn.rearHandle, rh);
         Transform2.mulPoint(tr, kn.coord, co);

         if ( fh.x < lb.x ) { lb.x = fh.x; }
         if ( fh.x > ub.x ) { ub.x = fh.x; }
         if ( fh.y < lb.y ) { lb.y = fh.y; }
         if ( fh.y > ub.y ) { ub.y = fh.y; }

         if ( rh.x < lb.x ) { lb.x = rh.x; }
         if ( rh.x > ub.x ) { ub.x = rh.x; }
         if ( rh.y < lb.y ) { lb.y = rh.y; }
         if ( rh.y > ub.y ) { ub.y = rh.y; }

         if ( co.x < lb.x ) { lb.x = co.x; }
         if ( co.x > ub.x ) { ub.x = co.x; }
         if ( co.y < lb.y ) { lb.y = co.y; }
         if ( co.y > ub.y ) { ub.y = co.y; }
      }
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
    * @see Vec2#add(Vec2, Vec2, Vec2)
    * @see Vec2#bezierPoint(Vec2, Vec2, Vec2, Vec2, float, Vec2)
    * @see Vec2#bezierTangent(Vec2, Vec2, Vec2, Vec2, float, Vec2)
    */
   @Experimental
   static Knot2 bezierKnot ( final Knot2 a, final Knot2 b, final float step,
      final Knot2 target ) {

      final Vec2 aco = a.coord;
      final Vec2 afh = a.foreHandle;
      final Vec2 bco = b.coord;
      final Vec2 brh = b.rearHandle;
      final Vec2 tco = target.coord;
      final Vec2 trh = target.rearHandle;
      final Vec2 tfh = target.foreHandle;

      Vec2.bezierPoint(aco, afh, brh, bco, step, tco);
      Vec2.bezierTangent(aco, afh, brh, bco, step, tfh);

      /* Find rear handle by reversing directions. */
      Vec2.bezierTangent(bco, brh, afh, aco, 1.0f - step, trh);

      /* Convert fore and rear handle from direction to point. */
      Vec2.add(tco, tfh, tfh);
      Vec2.add(tco, trh, trh);

      return target;
   }

   /**
    * Creates a curve which approximates a circle. This is package level to
    * provide extra functionality for translating a circle's origin.
    *
    * @param sectors     the sectors
    * @param offsetAngle the angular offset
    * @param radius      the radius
    * @param xCenter     the x center
    * @param yCenter     the y center
    * @param target      the output curve
    *
    * @return the circle
    *
    * @see Knot2#fromPolar(float, float, float, float, float, Knot2)
    * @see Utils#tan(float)
    */
   static Curve2 circle ( final int sectors, final float offsetAngle,
      final float radius, final float xCenter, final float yCenter,
      final Curve2 target ) {

      /* Since this is called by arc, it also needs to be optimized. */

      final float offNorm = offsetAngle * IUtils.ONE_TAU;
      final int vKnCt = sectors < 3 ? 3 : sectors;
      target.resize(vKnCt);
      final float invKnCt = 1.0f / vKnCt;
      final float hndlTan = 0.25f * invKnCt;
      final float hndlMag = Utils.tan(hndlTan * IUtils.TAU) * radius
         * IUtils.FOUR_THIRDS;

      float incr = 0.0f;
      final Iterator < Knot2 > itr = target.knots.iterator();
      for ( ; itr.hasNext(); ++incr ) {
         final float angNorm = offNorm + incr * invKnCt;
         Knot2.fromPolar(Utils.scNorm(angNorm), Utils.scNorm(angNorm - 0.25f),
            radius, hndlMag, xCenter, yCenter, itr.next());
      }

      target.name = "Circle";
      target.closedLoop = true;
      return target;
   }

   /**
    * Creates a curve which approximates an ellipse. The aspect is the ratio
    * between the width and height of the ellipse. This is package level to
    * provide extra functionality for translating a ellipse's origin.
    *
    * @param radius  the radius
    * @param aspect  the aspect ratio
    * @param xCenter the x center
    * @param yCenter the y center
    * @param target  the output curve
    *
    * @return the ellipse
    */
   static Curve2 ellipse ( final float radius, final float aspect,
      final float xCenter, final float yCenter, final Curve2 target ) {

      final float vrad = Utils.max(radius, IUtils.EPSILON);
      final float ry = vrad * aspect;

      final float right = xCenter + vrad;
      final float top = yCenter + ry;
      final float left = xCenter - vrad;
      final float bottom = yCenter - ry;

      final float horizHandle = vrad * ICurve.KAPPA;
      final float vertHandle = ry * ICurve.KAPPA;

      final float xHandlePos = xCenter + horizHandle;
      final float xHandleNeg = xCenter - horizHandle;
      final float yHandlePos = yCenter + vertHandle;
      final float yHandleNeg = yCenter - vertHandle;

      target.resize(4);
      final Knot2 kn0 = target.get(0);
      final Knot2 kn1 = target.get(1);
      final Knot2 kn2 = target.get(2);
      final Knot2 kn3 = target.get(3);

      kn0.coord.set(right, yCenter);
      kn0.foreHandle.set(right, yHandlePos);
      kn0.rearHandle.set(right, yHandleNeg);

      kn1.coord.set(xCenter, top);
      kn1.foreHandle.set(xHandleNeg, top);
      kn1.rearHandle.set(xHandlePos, top);

      kn2.coord.set(left, yCenter);
      kn2.foreHandle.set(left, yHandleNeg);
      kn2.rearHandle.set(left, yHandlePos);

      kn3.coord.set(xCenter, bottom);
      kn3.foreHandle.set(xHandlePos, bottom);
      kn3.rearHandle.set(xHandleNeg, bottom);

      target.name = "Ellipse";
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
   static Vec2 lerp13 ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(IUtils.TWO_THIRDS * a.x + IUtils.ONE_THIRD * b.x,
         IUtils.TWO_THIRDS * a.y + IUtils.ONE_THIRD * b.y);
   }

   /**
    * Creates a curve that forms a line with an origin and destination.
    *
    * @param xOrig  the origin x
    * @param yOrig  the origin y
    * @param xDest  the destination x
    * @param yDest  the destination y
    * @param target the output curve
    *
    * @return the line
    *
    * @see Curve2#lerp13(Vec2, Vec2, Vec2)
    * @see Knot2#mirrorHandlesForward()
    * @see Knot2#mirrorHandlesBackward()
    */
   static Curve2 line ( final float xOrig, final float yOrig, final float xDest,
      final float yDest, final Curve2 target ) {

      target.resize(2);
      final Knot2 first = target.knots.get(0);
      final Knot2 last = target.knots.get(1);

      first.coord.set(xOrig, yOrig);
      last.coord.set(xDest, yDest);

      Curve2.lerp13(first.coord, last.coord, first.foreHandle);
      Curve2.lerp13(last.coord, first.coord, last.rearHandle);

      first.mirrorHandlesForward();
      last.mirrorHandlesBackward();

      target.name = "Line";
      target.closedLoop = false;
      return target;
   }

   /**
    * Creates a rounded rectangle. The fifth parameter specifies the corner
    * rounding factor.
    *
    * @param lbx    lower bound x
    * @param lby    lower bound y
    * @param ubx    upper bound x
    * @param uby    upper bound y
    * @param corner the rounding factor
    * @param target the output curve
    *
    * @return the rounded rectangle
    */
   static Curve2 rect ( final float lbx, final float lby, final float ubx,
      final float uby, final float corner, final Curve2 target ) {

      return Curve2.rect(lbx, lby, ubx, uby, corner, corner, corner, corner,
         target);
   }

   /**
    * Creates a rounded rectangle. The first four parameters specify the lower
    * and upper bound of the rectangle. The next four parameters specify the
    * rounding factor for the top left, top right, bottom right and bottom
    * left corners.
    *
    * @param lbx    lower bound x
    * @param lby    lower bound y
    * @param ubx    upper bound x
    * @param uby    upper bound y
    * @param tl     rounding top left corner
    * @param tr     rounding top right corner
    * @param br     rounding bottom right corner
    * @param bl     rounding bottom left corner
    * @param target the output curve
    *
    * @return the rounded rectangle
    */
   static Curve2 rect ( final float lbx, final float lby, final float ubx,
      final float uby, final float tl, final float tr, final float br,
      final float bl, final Curve2 target ) {

      target.closedLoop = true;
      target.name = "Rect";

      /* Validate corners. */
      float lft = lbx < ubx ? lbx : ubx;
      float btm = lby < uby ? lby : uby;
      float rgt = ubx > lbx ? ubx : lbx;
      float top = uby > lby ? uby : lby;

      /* Protect against zero dimensions on w or h or both. */
      float w = rgt - lft;
      float h = top - btm;
      final boolean wInval = w < IUtils.EPSILON;
      final boolean hInval = h < IUtils.EPSILON;
      if ( wInval && hInval ) {
         final float cx = ( lft + rgt ) * 0.5f;
         final float cy = ( top + btm ) * 0.5f;
         lft = cx - 0.5f;
         rgt = cx + 0.5f;
         btm = cy - 0.5f;
         top = cy + 0.5f;
      } else if ( wInval ) {
         final float cx = ( lft + rgt ) * 0.5f;
         final float hHalf = h * 0.5f;
         lft = cx - hHalf;
         rgt = cx + hHalf;
         w = h;
      } else if ( hInval ) {
         final float cy = ( top + btm ) * 0.5f;
         final float wHalf = w * 0.5f;
         btm = cy - wHalf;
         top = cy + wHalf;
         h = w;
      }

      /*
       * Validate corner insetting. It would make more sense for these to be
       * factors in [0.0, 1.0] that are then divided by 2. However, there needs
       * to be parity with Processing and SVG approach.
       */
      final float se = 0.5f * Utils.min(w, h);
      final float vtl = Utils.min(se, Utils.abs(tl));
      final float vbl = Utils.min(se, Utils.abs(bl));
      final float vbr = Utils.min(se, Utils.abs(br));
      final float vtr = Utils.min(se, Utils.abs(tr));

      /* To extend corner handles. */
      final float vtlk = vtl * ICurve.KAPPA;
      final float vblk = vbl * ICurve.KAPPA;
      final float vbrk = vbr * ICurve.KAPPA;
      final float vtrk = vtr * ICurve.KAPPA;

      /* To lerp handles on flat edges. */
      final float t = IUtils.ONE_THIRD;
      final float u = IUtils.TWO_THIRDS;

      /* Calculate insets. */
      final float lftIns0 = lft + vtl;
      final float topIns1 = top - vtl;
      final float btmIns1 = btm + vbl;
      final float lftIns1 = lft + vbl;
      final float rgtIns1 = rgt - vbr;
      final float btmIns0 = btm + vbr;
      final float topIns0 = top - vtr;
      final float rgtIns0 = rgt - vtr;

      /* Calculate knot count. Two knots if corner is round. */
      int knotCount = 4;
      if ( tl != 0.0f ) { ++knotCount; }
      if ( bl != 0.0f ) { ++knotCount; }
      if ( br != 0.0f ) { ++knotCount; }
      if ( tr != 0.0f ) { ++knotCount; }

      target.resize(knotCount);
      final Iterator < Knot2 > itr = target.knots.iterator();

      /* Top left corner. */
      if ( tl > 0.0f ) {
         itr.next().set(lftIns0, top, lftIns0 - vtlk, top, u * lftIns0 + t
            * rgtIns0, top);
         itr.next().set(lft, topIns1, lft, u * topIns1 + t * btmIns1, lft,
            topIns1 + vtlk);
      } else if ( tl < -0.0f ) {
         itr.next().set(lftIns0, top, lftIns0, top - vtlk, u * lftIns0 + t
            * rgtIns0, top);
         itr.next().set(lft, topIns1, lft, u * topIns1 + t * btmIns1, lft
            + vtlk, topIns1);
      } else {
         itr.next().set(lft, top, lft, u * top + t * btmIns1, u * lft + t
            * rgtIns0, top);
      }

      /* Bottom left corner. */
      if ( bl > 0.0f ) {
         itr.next().set(lft, btmIns1, lft, btmIns1 - vblk, lft, u * btmIns1 + t
            * topIns1);
         itr.next().set(lftIns1, btm, u * lftIns1 + t * rgtIns1, btm, lftIns1
            - vblk, btm);
      } else if ( bl < -0.0f ) {
         itr.next().set(lft, btmIns1, lft + vblk, btmIns1, lft, u * btmIns1 + t
            * topIns1);
         itr.next().set(lftIns1, btm, u * lftIns1 + t * rgtIns1, btm, lftIns1,
            btm + vblk);
      } else {
         itr.next().set(lft, btm, u * lft + t * rgtIns1, btm, lft, u * btm + t
            * topIns1);
      }

      /* Bottom right corner. */
      if ( br > 0.0f ) {
         itr.next().set(rgtIns1, btm, rgtIns1 + vbrk, btm, u * rgtIns1 + t
            * lftIns1, btm);
         itr.next().set(rgt, btmIns0, rgt, u * btmIns0 + t * topIns0, rgt,
            btmIns0 - vbrk);
      } else if ( br < -0.0f ) {
         itr.next().set(rgtIns1, btm, rgtIns1, btm + vbrk, u * rgtIns1 + t
            * lftIns1, btm);
         itr.next().set(rgt, btmIns0, rgt, u * btmIns0 + t * topIns0, rgt
            - vbrk, btmIns0);
      } else {
         itr.next().set(rgt, btm, rgt, u * btm + t * topIns0, u * rgt + t
            * lftIns1, btm);
      }

      /* Top right corner. */
      if ( tr > 0.0f ) {
         itr.next().set(rgt, topIns0, rgt, topIns0 + vtrk, rgt, u * topIns0 + t
            * btmIns0);
         itr.next().set(rgtIns0, top, u * rgtIns0 + t * lftIns0, top, rgtIns0
            + vtrk, top);
      } else if ( tr < -0.0f ) {
         itr.next().set(rgt, topIns0, rgt - vtrk, topIns0, rgt, u * topIns0 + t
            * btmIns0);
         itr.next().set(rgtIns0, top, u * rgtIns0 + t * lftIns0, top, rgtIns0,
            top - vtrk);
      } else {
         itr.next().set(rgt, top, u * rgt + t * lftIns0, top, rgt, u * top + t
            * btmIns0);
      }

      return target;
   }

   /**
    * An easing function to facilitate animation between multiple curves.
    */
   public static class Easing implements EasingFuncArr < Curve2 >,
      EasingFuncObj < Curve2 > {

      /**
       * The knot easing function.
       */
      public final Knot2.AbstrEasing easingFunc;

      /**
       * The default constructor.
       */
      public Easing ( ) { this.easingFunc = new Knot2.Lerp(); }

      /**
       * The easing constructor
       *
       * @param easingFunc the knot easing function
       */
      public Easing ( final Knot2.AbstrEasing easingFunc ) {

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
      public Curve2 apply ( final Curve2 origin, final Curve2 dest,
         final Float step, final Curve2 target ) {

         final float t = step;
         if ( t <= 0.0f ) { return target.set(origin); }
         if ( t >= 1.0f ) { return target.set(dest); }
         return this.applyUnclamped(origin, dest, t, target);
      }

      /**
       * Eases between curves in an array by a step in the range [0.0, 1.0] .
       *
       * @param arr    the curve array
       * @param step   the step
       * @param target the output curve
       */
      @Override
      public Curve2 apply ( final Curve2[] arr, final Float step,
         final Curve2 target ) {

         final int len = arr.length;
         final float t = step;
         if ( len == 1 || t <= 0.0f ) { return target.set(arr[0]); }
         if ( t >= 1.0f ) { return target.set(arr[len - 1]); }

         final float scaledStep = t * ( len - 1 );
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
      public Curve2 applyUnclamped ( final Curve2 origin, final Curve2 dest,
         final float step, final Curve2 target ) {

         final ArrayList < Knot2 > orKn = origin.knots;
         final ArrayList < Knot2 > dsKn = dest.knots;

         if ( orKn.size() == dsKn.size() && origin.closedLoop
            == dest.closedLoop ) {

            target.closedLoop = origin.closedLoop;
            target.resize(orKn.size());

            final Iterator < Knot2 > orItr = orKn.iterator();
            final Iterator < Knot2 > dsItr = dsKn.iterator();
            final Iterator < Knot2 > tgItr = target.knots.iterator();
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
