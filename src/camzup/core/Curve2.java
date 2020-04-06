package camzup.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import camzup.core.Utils.EasingFuncArr;
import camzup.core.Utils.EasingFuncObj;

/**
 * Organizes a Bezier curve into a list of knots. Provides a function to
 * retrieve a point and tangent on a curve from a step in the range [0.0, 1.0].
 */
public class Curve2 extends Curve implements Iterable < Knot2 >, ISvgWritable {

   /**
    * The list of knots contained by the curve.
    */
   private final List < Knot2 > knots;

   {
      /*
       * Seems to perform better when the class is used instead of the
       * interface. Problem is that it's hard to decide one whether to use an
       * array or linked list.
       */

      // knots = new LinkedList <>();
      this.knots = new ArrayList <>();
   }

   /**
    * Creates a curve with two default knots.
    */
   public Curve2 ( ) {

      super();
      this.reset();
   }

   /**
    * Creates a curve from a collection of knots
    *
    * @param cl    whether or not the curve is closed
    * @param knots the collection of knots
    */
   public Curve2 (
      final boolean cl,
      final Collection < Knot2 > knots ) {

      super(cl);
      this.appendAll(knots);
   }

   /**
    * Creates a curve from a comma-separated list of knots.
    *
    * @param cl    whether or not the curve is closed
    * @param knots the list of knots
    */
   public Curve2 (
      final boolean cl,
      final Knot2... knots ) {

      super(cl);
      this.appendAll(knots);
   }

   /**
    * Constructs a copy of the source.
    *
    * @param source the source curve
    */
   public Curve2 ( final Curve2 source ) {

      super();
      this.set(source);
   }

   /**
    * Creates a named curve with two default knots.
    *
    * @param name the name
    */
   public Curve2 ( final String name ) {

      super(name);
      this.reset();
   }

   /**
    * Creates a named curve from a collection of knots
    *
    * @param name  the name
    * @param cl    whether or not the curve is closed
    * @param knots the collection of knots
    */
   public Curve2 (
      final String name,
      final boolean cl,
      final Collection < Knot2 > knots ) {

      super(name, cl);
      this.appendAll(knots);
   }

   /**
    * Creates a named curve from a comma-separated list of knots.
    *
    * @param name  the name
    * @param cl    whether or not the curve is closed
    * @param knots the list of knots
    */
   public Curve2 (
      final String name,
      final boolean cl,
      final Knot2... knots ) {

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
   @Chainable
   public Curve2 append ( final Knot2 knot ) {

      if ( knot != null ) { this.knots.add(knot); }
      return this;
   }

   /**
    * Append an collection of knots to the curve's list of knots.
    *
    * @param knots the collection of knots
    *
    * @return this curve.
    */
   public Curve2 appendAll ( final Collection < Knot2 > knots ) {

      final Iterator < Knot2 > knItr = knots.iterator();
      while ( knItr.hasNext() ) {
         this.append(knItr.next());
      }

      return this;
   }

   /**
    * Append an array of knots to the curve's list of knots.
    *
    * @param knots the array of knots
    *
    * @return this curve.
    */
   @Chainable
   public Curve2 appendAll ( final Knot2... knots ) {

      final int len = knots.length;
      for ( int i = 0; i < len; ++i ) {
         this.append(knots[i]);
      }

      return this;
   }

   /**
    * Clones this curve.
    *
    * @return the cloned curve
    */
   @Override
   public Curve2 clone ( ) {

      final Curve2 c = new Curve2(this);
      c.name = this.name;
      c.materialIndex = this.materialIndex;
      return c;
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
      return this.equals(( Curve2 ) obj);
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
    */
   public Knot2 getLast ( ) {

      return this.knots.get(this.knots.size() - 1);
   }

   /**
    * Gets a segment with two knots from this curve.
    *
    * @param i      the index
    * @param target the output curve
    *
    * @return the segment
    */
   public Curve2 getSegment (
      final int i,
      final Curve2 target ) {

      final int len = this.knots.size();

      if ( this.closedLoop ) {
         target.closedLoop = false;
         target.knots.clear();
         target.knots.add(new Knot2(
            this.knots.get(Utils.mod(i, len))));
         target.knots.add(new Knot2(
            this.knots.get(Utils.mod(i + 1, len))));
      } else if ( i > -1 && i < len - 1 ) {
         target.closedLoop = false;
         target.knots.clear();
         target.knots.add(new Knot2(this.knots.get(i)));
         target.knots.add(new Knot2(this.knots.get(i + 1)));
      }

      return target;
   }

   /**
    * Calculates this curve's hash code based on its knots and on whether it is
    * a closed loop.
    *
    * @return the hash
    */
   @Override
   public int hashCode ( ) {

      int hash = IUtils.MUL_BASE ^ ( this.closedLoop ? 1231 : 1237 );
      hash = hash * IUtils.HASH_MUL ^ ( this.knots == null ? 0
         : this.knots.hashCode() );
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
   public Curve2 insert (
      final int i,
      final Knot2 knot ) {

      if ( knot != null ) {
         final int k = this.closedLoop ? Utils.mod(i, this.knots.size() + 1)
            : i;
         this.knots.add(k, knot);
      }

      return this;
   }

   /**
    * Inserts a collection of knots at a given index. When the curve is a closed
    * loop, the index wraps around; this means negative indices are accepted.
    *
    * @param i     the index
    * @param knots the knots
    *
    * @return this curve
    */
   public Curve2 insertAll (
      final int i,
      final Collection < Knot2 > knots ) {

      final int vidx = this.closedLoop ? Utils.mod(i, this.knots.size() + 1)
         : i;
      final Iterator < Knot2 > knItr = knots.iterator();
      int k = vidx;
      while ( knItr.hasNext() ) {
         final Knot2 knot = knItr.next();
         if ( knot != null ) {
            this.knots.add(k, knot);
            k++;
         }
      }

      return this;
   }

   /**
    * Inserts a list of knots at a given index. When the curve is a closed loop,
    * the index wraps around; this means negative indices are accepted.
    *
    * @param i     the index
    * @param knots the knots
    *
    * @return this curve
    */
   public Curve2 insertAll (
      final int i,
      final Knot2... knots ) {

      final int len = knots.length;
      final int vidx = this.closedLoop ? Utils.mod(i, this.knots.size() + 1)
         : i;
      for ( int j = 0, k = vidx; j < len; ++j ) {
         final Knot2 knot = knots[j];
         if ( knot != null ) {
            this.knots.add(k, knot);
            k++;
         }
      }

      return this;
   }

   /**
    * Returns an iterator, which allows an enhanced for-loop to access the knots
    * in a curve.
    *
    * @return the iterator
    *
    * @see List#iterator()
    */
   @Override
   public Iterator < Knot2 > iterator ( ) { return this.knots.iterator(); }

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
   @Chainable
   public Curve2 prepend ( final Knot2 knot ) {

      if ( knot != null ) { this.knots.add(0, knot); }
      return this;
   }

   /**
    * Prepend an collection of knots to the curve's list of knots.
    *
    * @param knots the collection of knots
    *
    * @return this curve.
    */
   @Chainable
   public Curve2 prependAll ( final Collection < Knot2 > knots ) {

      int i = 0;
      final Iterator < Knot2 > knItr = knots.iterator();
      while ( knItr.hasNext() ) {
         final Knot2 knot = knItr.next();
         if ( knot != null ) {
            this.knots.add(i, knot);
            i++;
         }
      }
      return this;
   }

   /**
    * Prepend an array of knots to the curve's list of knots.
    *
    * @param knots the array of knots
    *
    * @return this curve.
    *
    * @see List#add(int, Object)
    */
   @Chainable
   public Curve2 prependAll ( final Knot2... knots ) {

      // TEST
      final int len = knots.length;
      for ( int i = 0, j = 0; i < len; ++i ) {
         final Knot2 knot = knots[i];
         if ( knot != null ) {
            this.knots.add(j, knot);
            j++;
         }
      }
      return this;
   }

   /**
    * Returns and removes a knot at a given index.
    *
    * @param i the index
    *
    * @return the knot
    */
   public Knot2 removeAt ( final int i ) {

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
   public Knot2 removeFirst ( ) { return this.knots.remove(0); }

   /**
    * Removes and returns the last knot in the curve.
    *
    * @return the knot
    *
    * @see List#remove(int)
    */
   public Knot2 removeLast ( ) {

      return this.knots.remove(this.knots.size() - 1);
   }

   /**
    * Resets the curve, leaving two default knots.
    *
    * @return this curve
    */
   @Chainable
   public Curve2 reset ( ) {

      this.resize(2);
      this.knots.get(0).set(
         -0.5f, 0.0f,
         -0.25f, 0.25f,
         -0.75f, -0.25f);
      this.knots.get(1).set(
         0.5f, 0.0f,
         1.0f, 0.0f,
         0.0f, 0.0f);

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
    */
   @Chainable
   public Curve2 reverse ( ) {

      Collections.reverse(this.knots);

      final Iterator < Knot2 > itr = this.knots.iterator();
      while ( itr.hasNext() ) {
         itr.next().reverse();
      }

      return this;
   }

   /**
    * Rotates all knots in the curve by an angle in radians around the z axis.
    *
    * @param radians the angle
    *
    * @return this curve
    *
    * @see Knot2#rotateZ(float)
    */
   @Chainable
   public Curve2 rotateZ ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      final Iterator < Knot2 > itr = this.knots.iterator();
      while ( itr.hasNext() ) {
         itr.next().rotateZ(cosa, sina);
      }
      return this;
   }

   /**
    * Scales all knots in the curve by a scalar.
    *
    * @param scale the scale
    *
    * @return this curve
    *
    * @see Knot2#scale(float)
    */
   @Chainable
   public Curve2 scale ( final float scale ) {

      if ( scale == 0.0f ) { return this; }

      final Iterator < Knot2 > itr = this.knots.iterator();
      while ( itr.hasNext() ) {
         itr.next().scale(scale);
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
    * @see Knot2#scale(Vec2)
    */
   @Chainable
   public Curve2 scale ( final Vec2 scale ) {

      if ( Vec2.none(scale) ) { return this; }

      final Iterator < Knot2 > itr = this.knots.iterator();
      while ( itr.hasNext() ) {
         itr.next().scale(scale);
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
   @Chainable
   public Curve2 set ( final Curve2 source ) {

      this.resize(source.length());
      final Iterator < Knot2 > srcItr = source.iterator();
      final Iterator < Knot2 > trgItr = this.knots.iterator();
      while ( srcItr.hasNext() ) {
         trgItr.next().set(srcItr.next());
      }

      this.closedLoop = source.closedLoop;
      this.materialIndex = source.materialIndex;
      return this;
   }

   /**
    * Returns a 3D array representation of this curve.
    *
    * @return the array
    */
   public float[][][] toArray ( ) {

      final float[][][] result = new float[this.knots.size()][][];
      final Iterator < Knot2 > itr = this.knots.iterator();
      int index = 0;
      while ( itr.hasNext() ) {
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
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of the curve.
    *
    * @param places the number of places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(
         64 + 256 * this.knots.size())
            .append("{ name: \"")
            .append(this.name)
            .append("\", closedLoop: ")
            .append(this.closedLoop)
            .append(", knots: [ ");

      final Iterator < Knot2 > itr = this.knots.iterator();
      while ( itr.hasNext() ) {
         sb.append(itr.next().toString(places));
         if ( itr.hasNext() ) {
            sb.append(',')
               .append(' ');
            // sb.append('\n');
         }
      }

      sb.append(" ] }");
      return sb.toString();
   }

   /**
    * Renders the curve as a string containing an SVG element.
    *
    * @param id   the element id
    * @param zoom scaling transform
    *
    * @return the SVG string
    */
   @Override
   public String toSvgElm ( final String id, final float zoom ) {

      final StringBuilder svgp = new StringBuilder(1024)
         .append(MaterialSolid.defaultSvgMaterial(zoom))
         .append(this.toSvgPath(id))
         .append("</g>\n");
      return svgp.toString();
   }

   /**
    * Renders the curve path as a string containing an SVG element.
    *
    * @param id the path id
    *
    * @return the SVG string
    */
   public String toSvgPath ( final String id ) {

      final int knotLength = this.knots.size();
      if ( knotLength < 2 ) { return ""; }
      final StringBuilder svgp = new StringBuilder(
         32 + 64 * ( this.closedLoop ? knotLength + 1 : knotLength ));

      final Iterator < Knot2 > itr = this.knots.iterator();
      Knot2 prevKnot = itr.next();
      svgp.append("<path id=\"")
         .append(id)
         .append("\" d=\"M ")
         .append(prevKnot.coord.toSvgString());

      Knot2 currKnot = null;
      while ( itr.hasNext() ) {
         currKnot = itr.next();

         svgp.append(' ')
            .append('C')
            .append(' ')
            .append(prevKnot.foreHandle.toSvgString())
            .append(',')
            .append(currKnot.rearHandle.toSvgString())
            .append(',')
            .append(currKnot.coord.toSvgString());

         prevKnot = currKnot;
      }

      if ( this.closedLoop ) {
         currKnot = this.knots.get(0);
         svgp.append(' ')
            .append('C')
            .append(' ')
            .append(prevKnot.foreHandle.toSvgString())
            .append(',')
            .append(currKnot.rearHandle.toSvgString())
            .append(',')
            .append(currKnot.coord.toSvgString())
            .append(' ')
            .append('Z');
      }

      svgp.append("\"></path>\n");
      return svgp.toString();
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
   @Chainable
   public Curve2 translate ( final Vec2 v ) {

      final Iterator < Knot2 > itr = this.knots.iterator();
      while ( itr.hasNext() ) {
         itr.next().translate(v);
      }
      return this;
   }

   /**
    * For internal (package-level) use. Resizes a curve to the specified length.
    * The length may be no less than 2. When the new length is greater than the
    * old, new <code>Knot2</code>s are added.<br>
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
   @Chainable
   Curve2 resize ( final int len ) {

      final int vlen = len < 2 ? 2 : len;
      final int oldLen = this.knots.size();
      final int diff = vlen - oldLen;
      if ( diff < 0 ) {
         final int last = oldLen - 1;
         for ( int i = 0; i < -diff; ++i ) {
            this.knots.remove(last - i);
         }
      } else if ( diff > 0 ) {
         for ( int i = 0; i < diff; ++i ) {
            this.knots.add(new Knot2());
         }
      }
      return this;
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API. This
    * code is brittle and is used for internal testing purposes, i.e., to
    * compare how curve geometry looks in Blender (the control) versus in the
    * library (the test).
    *
    * @param uRes the resolution u
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode ( final int uRes ) {

      final StringBuilder sb = new StringBuilder(
         64 + 256 * this.knots.size());
      sb.append("{\"closed_loop\": ")
         .append(this.closedLoop ? "True" : "False")
         .append(", \"resolution_u\": ")
         .append(uRes)
         .append(", \"knots\": [");
      final Iterator < Knot2 > itr = this.knots.iterator();
      int i = 0;
      final int last = this.knots.size() - 1;
      while ( itr.hasNext() ) {
         sb.append(itr.next().toBlenderCode());
         if ( i < last ) { sb.append(',').append(' '); }
         i++;
      }

      sb.append(']').append('}');
      return sb.toString();
   }

   /**
    * Clears the list of knots and sets the closedLoop flag to false. Unlike the
    * public reset, this does not add two default knots to the list.
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
   protected boolean equals ( final Curve2 curve ) {

      if ( this.closedLoop != curve.closedLoop ) { return false; }

      if ( this.knots == null ) {
         if ( curve.knots != null ) { return false; }
      } else if ( !this.knots.equals(curve.knots) ) { return false; }

      return true;
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
   public static Curve2 arc (
      final float stopAngle,
      final Curve2 target ) {

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
   public static Curve2 arc (
      final float startAngle,
      final float stopAngle,
      final Curve2 target ) {

      return Curve2.arc(
         startAngle, stopAngle, 0.5f, target);
   }

   /**
    * Creates an arc from a start and stop angle. The arc can be open, traversed
    * by a chord, or pie-shaped.
    *
    * @param startAngle the start angle
    * @param stopAngle  the stop angle
    * @param radius     the arc radius
    * @param arcMode    the arc mode
    * @param target     the output curve
    *
    * @return the arc
    */
   public static Curve2 arc (
      final float startAngle,
      final float stopAngle,
      final float radius,
      final ArcMode arcMode,
      final Curve2 target ) {

      /*
       * Optimized where possible because Yup2 renderer uses this to display
       * arcs. Outlier case where arc is used as a progress bar. The tolerance
       * is less than half a degree, 1.0 / 720.0, which is the minimum step used
       * by the Processing sine cosine look-up table (LUT).
       */
      if ( Utils.approx(stopAngle - startAngle, IUtils.TAU, 0.00139f) ) {
         return Curve2.circle(startAngle, radius, 4, target);
      }

      /* Divide by TAU then wrap around the range, [0.0, 1.0] . */
      final float a1 = Utils.mod1(startAngle * IUtils.ONE_TAU);
      final float b1 = Utils.mod1(stopAngle * IUtils.ONE_TAU);

      /*
       * Find the arc length and the destination angle from the origin (a1).
       */
      final float arcLen1 = Utils.mod1(b1 - a1);
      final float destAngle1 = a1 + arcLen1;

      /*
       * Find the number of knots needed to accurately represent the arc. It's
       * assumed that 4 curves adequately represent a full circle; at least one
       * knot is needed, hence the +1.
       */
      final int knotCount = Utils.ceilToInt(1 + 4 * arcLen1);
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
      final float handleMag = Utils.tan(
         hndtn * IUtils.TAU) * radius * IUtils.FOUR_THIRDS;

      /*
       * Clears the list of knots rather than doing any partial reassignment.
       * Depends on what kind of underlying list (e.g., array or linked) is
       * used.
       */
      target.resize(knotCount);
      final List < Knot2 > knots = target.knots;
      for ( int i = 0; i < knotCount; ++i ) {
         final float t = i * toStep;
         final float angle1 = ( 1.0f - t ) * a1 + t * destAngle1;
         Knot2.fromPolar(
            Utils.scNorm(angle1),
            Utils.scNorm(angle1 - 0.25f),
            radius, handleMag,
            knots.get(i));
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
            last.foreHandle.set(
               0.6666667f * coLast.x + 0.33333334f * coFirst.x,
               0.6666667f * coLast.y + 0.33333334f * coFirst.y);

            first.rearHandle.set(
               0.6666667f * coFirst.x + 0.33333334f * coLast.x,
               0.6666667f * coFirst.y + 0.33333334f * coLast.y);

         } else if ( arcMode == ArcMode.PIE ) {

            /* Add a center knot. */
            final Knot2 center = new Knot2();
            final Vec2 coCenter = center.coord;
            knots.add(center);

            /* Flatten center handles. */
            center.rearHandle.set(
               0.6666667f * coCenter.x + 0.33333334f * coLast.x,
               0.6666667f * coCenter.y + 0.33333334f * coLast.y);
            center.foreHandle.set(
               0.6666667f * coCenter.x + 0.33333334f * coFirst.x,
               0.6666667f * coCenter.y + 0.33333334f * coFirst.y);

            /* Flatten handle from first to center. */
            first.rearHandle.set(
               0.6666667f * coFirst.x + 0.33333334f * coCenter.x,
               0.6666667f * coFirst.y + 0.33333334f * coCenter.y);

            /* Flatten handle from last to center. */
            last.foreHandle.set(
               0.6666667f * coLast.x + 0.33333334f * coCenter.x,
               0.6666667f * coLast.y + 0.33333334f * coCenter.y);
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
    * Creates a curve which approximates a circle of radius 0.5 using four
    * knots.
    *
    * @param target the output curve
    *
    * @return the circle
    */
   public static Curve2 circle ( final Curve2 target ) {

      return Curve2.circle(0.0f, 0.5f, 4, target);
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
   public static Curve2 circle (
      final float offsetAngle,
      final Curve2 target ) {

      return Curve2.circle(offsetAngle, 0.5f, 4, target);
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
   public static Curve2 circle (
      final float offsetAngle,
      final float radius,
      final Curve2 target ) {

      return Curve2.circle(offsetAngle, radius, 4, target);
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
   public static Curve2 circle (
      final float offsetAngle,
      final float radius,
      final int knotCount,
      final Curve2 target ) {

      /* Since this is called by arc, it also needs to be optimized. */

      final float off1 = offsetAngle * IUtils.ONE_TAU;
      final int vknct = knotCount < 3 ? 3 : knotCount;
      target.resize(vknct);
      final float invKnCt = 1.0f / vknct;
      final float hndtn = 0.25f * invKnCt;
      final float handleMag = Utils.tan(
         hndtn * IUtils.TAU) * radius * IUtils.FOUR_THIRDS;

      int i = 0;
      final Iterator < Knot2 > itr = target.knots.iterator();
      while ( itr.hasNext() ) {
         final float angle1 = off1 + i * invKnCt;
         Knot2.fromPolar(
            Utils.scNorm(angle1),
            Utils.scNorm(angle1 - 0.25f),
            radius, handleMag,
            itr.next());
         i++;
      }

      target.name = "Circle";
      target.closedLoop = true;
      return target;
   }

   /**
    * Evaluates a step in the range [0.0, 1.0], returning a transform. The
    * transform's scale is unaffected by the evaluation.
    *
    * @param curve      the curve
    * @param step       the step
    * @param handedness the handedness
    * @param target     the target
    *
    * @return the transform
    *
    * @see Curve2#eval(Curve2, float, Vec2, Vec2)
    */
   @Experimental
   public static Transform2 eval (
      final Curve2 curve,
      final float step,
      final Handedness handedness,
      final Transform2 target ) {

      // TEST

      target.locPrev.set(target.location);
      target.rotPrev = target.rotation;

      Curve2.eval(curve, step, target.location, target.forward);

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
    * Evaluates a step in the range [0.0, 1.0], returning a knot on the curve.
    * The knot's fore handle and rear handle are mirrored.
    *
    * @param curve  the curve
    * @param step   the step
    * @param target the output knot
    *
    * @return the knot
    */
   @Experimental
   public static Knot2 eval (
      final Curve2 curve,
      final float step,
      final Knot2 target ) {

      final List < Knot2 > knots = curve.knots;
      final int knotLength = knots.size();

      float tScaled = 0.0f;
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
      final float aFhMag = Knot2.foreHandleMag(a);
      final float bFhMag = Knot2.foreHandleMag(b);
      final float tFhMag = u * aFhMag + t * bFhMag;
      target.scaleForeHandleTo(tFhMag);

      final float aRhMag = Knot2.rearHandleMag(a);
      final float bRhMag = Knot2.rearHandleMag(b);
      final float tRhMag = u * aRhMag + t * bRhMag;
      target.scaleRearHandleTo(tRhMag);

      return target;
   }

   /**
    * Evaluates a step in the range [0.0, 1.0], returning a ray. The ray's
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
   public static Ray2 eval (
      final Curve2 curve,
      final float step,
      final Ray2 ray ) {

      Curve2.eval(curve, step, ray.origin, ray.dir);
      return ray;
   }

   /**
    * Evaluates a step in the range [0.0, 1.0], returning a coordinate on the
    * curve and a tangent. The tangent will be normalized, to be of unit length.
    *
    * @param curve   the curve
    * @param step    the step
    * @param coord   the output coordinate
    * @param tangent the output tangent
    *
    * @return the coordinate
    *
    * @see Vec2#bezierPoint(Vec2, Vec2, Vec2, Vec2, float, Vec2)
    * @see Vec2#bezierTanUnit(Vec2, Vec2, Vec2, Vec2, float, Vec2)
    */
   public static Vec2 eval (
      final Curve2 curve,
      final float step,
      final Vec2 coord,
      final Vec2 tangent ) {

      final List < Knot2 > knots = curve.knots;
      final int knotLength = knots.size();

      float tScaled = 0.0f;
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
      Curve2.bezierPoint(a, b, t, coord);
      Curve2.bezierTanUnit(a, b, t, tangent);

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
   public static Transform2 evalFirst (
      final Curve2 curve,
      final Handedness handedness,
      final Transform2 target ) {

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
   public static Knot2 evalFirst (
      final Curve2 curve,
      final Knot2 target ) {

      target.set(curve.knots.get(0));
      return target;
   }

   /**
    * Evaluates the first knot in the curve. The tangent will be normalized, to
    * be of unit length.
    *
    * @param curve the curve
    * @param ray   the output ray
    *
    * @return the coordinate
    *
    * @see Curve2#evalFirst(Curve2, Vec2, Vec2)
    */
   public static Ray2 evalFirst (
      final Curve2 curve,
      final Ray2 ray ) {

      Curve2.evalFirst(curve, ray.origin, ray.dir);
      return ray;
   }

   /**
    * Evaluates the first knot in the curve. The tangent will be normalized, to
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
   public static Vec2 evalFirst (
      final Curve2 curve,
      final Vec2 coord,
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
   public static Transform2 evalLast (
      final Curve2 curve,
      final Handedness handedness,
      final Transform2 target ) {

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
   public static Knot2 evalLast (
      final Curve2 curve,
      final Knot2 target ) {

      target.set(curve.knots.get(curve.knots.size() - 1));
      return target;
   }

   /**
    * Evaluates the first knot in the curve. The tangent will be normalized, to
    * be of unit length.
    *
    * @param curve the curve
    * @param ray   the output ray
    *
    * @return the coordinate
    *
    * @see Curve2#evalFirst(Curve2, Vec2, Vec2)
    */
   public static Ray2 evalLast (
      final Curve2 curve,
      final Ray2 ray ) {

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
   public static Vec2 evalLast (
      final Curve2 curve,
      final Vec2 coord,
      final Vec2 tangent ) {

      final Knot2 kLast = curve.knots.get(curve.knots.size() - 1);
      coord.set(kLast.coord);
      Vec2.subNorm(coord, kLast.rearHandle, tangent);
      return coord;
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
   public static Curve2 fromMeshFace (
      final Face2 face,
      final Curve2 target ) {

      // TEST

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
   public static Curve2 fromMeshFace (
      final int faceIdx,
      final Mesh2 mesh,
      final Curve2 target ) {

      final int facesLen = mesh.faces.length;
      final int i = Utils.mod(faceIdx, facesLen);
      final int[][] face = mesh.faces[i];
      final int vertsLen = face.length;
      final Vec2[] vs = mesh.coords;

      target.closedLoop = true;
      target.name = new StringBuilder(64)
         .append(mesh.name)
         .append('.')
         .append(i)
         .toString();
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
    */
   public static Curve2 fromPoints (
      final boolean closedLoop,
      final Vec2[] points,
      final Curve2 target ) {

      target.closedLoop = closedLoop;
      final int knotCount = points.length;
      target.resize(knotCount);

      int i = 0;
      final Iterator < Knot2 > itr = target.knots.iterator();
      while ( itr.hasNext() ) {
         final Vec2 point = points[i];
         itr.next().set(point, point, point);
         i++;
      }

      return Curve2.smoothHandles(target);
   }

   /**
    * Creates a curve that approximates Bernoulli's lemniscate, which resembles
    * an infinity loop (with equally proportioned lobes).<br>
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

      itr.next().set(
         0.5f, 0.0f,
         0.5f, 0.1309615f,
         0.5f, -0.1309615f);

      itr.next().set(
         0.235709f, 0.166627f,
         0.0505335f, 0.114256f,
         0.361728f, 0.2022675f);

      itr.next().set(
         -0.235709f, -0.166627f,
         -0.361728f, -0.2022675f,
         -0.0505335f, -0.114256f);

      itr.next().set(
         -0.5f, 0.0f,
         -0.5f, 0.1309615f,
         -0.5f, -0.1309615f);

      itr.next().set(
         -0.235709f, 0.166627f,
         -0.0505335f, 0.114256f,
         -0.361728f, 0.2022675f);

      itr.next().set(
         0.235709f, -0.166627f,
         0.361728f, -0.2022675f,
         0.0505335f, -0.114256f);

      return target;
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
    */
   public static Curve2 polygon (
      final float offsetAngle,
      final float radius,
      final int knotCount,
      final Curve2 target ) {

      Curve2.circle(
         offsetAngle,
         radius,
         knotCount,
         target);
      target.name = "Polygon";
      return Curve2.straightenHandles(target);
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
      for ( int i = 0; i < valCount; ++i ) {
         points[i] = Vec2.randomCartesian(rng,
            lowerBound, upperBound,
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
      for ( int i = 0; i < valCount; ++i ) {
         points[i] = Vec2.randomCartesian(rng,
            lowerBound, upperBound, new Vec2());
      }

      return Curve2.fromPoints(closedLoop, points, target);
   }

   /**
    * Creates a rectangle.
    *
    * @param lbx    lower bound x
    * @param lby    lower bound y
    * @param ubx    upper bound x
    * @param uby    upper bound y
    * @param target the output curve
    *
    * @return the rectangle
    */
   public static Curve2 rect (
      final float lbx,
      final float lby,
      final float ubx,
      final float uby,
      final Curve2 target ) {

      final float x0 = lbx < ubx ? lbx : ubx;
      final float x1 = ubx > lbx ? ubx : lbx;
      final float y0 = lby > uby ? lby : uby;
      final float y1 = uby < lby ? uby : lby;

      target.closedLoop = true;
      target.name = "Rect";
      target.resize(4);

      final Iterator < Knot2 > itr = target.knots.iterator();
      final Knot2 kn0 = itr.next();
      final Knot2 kn1 = itr.next();
      final Knot2 kn2 = itr.next();
      final Knot2 kn3 = itr.next();

      kn0.set(x0, y0, 0.0f, 0.0f, 0.0f, 0.0f);
      kn1.set(x1, y0, 0.0f, 0.0f, 0.0f, 0.0f);
      kn2.set(x1, y1, 0.0f, 0.0f, 0.0f, 0.0f);
      kn3.set(x0, y1, 0.0f, 0.0f, 0.0f, 0.0f);

      Curve2.lerp13(kn0.coord, kn1.coord, kn0.foreHandle);
      Curve2.lerp13(kn1.coord, kn2.coord, kn1.foreHandle);
      Curve2.lerp13(kn2.coord, kn3.coord, kn2.foreHandle);
      Curve2.lerp13(kn3.coord, kn0.coord, kn3.foreHandle);

      Curve2.lerp13(kn0.coord, kn3.coord, kn0.rearHandle);
      Curve2.lerp13(kn1.coord, kn0.coord, kn1.rearHandle);
      Curve2.lerp13(kn2.coord, kn1.coord, kn2.rearHandle);
      Curve2.lerp13(kn3.coord, kn2.coord, kn3.rearHandle);

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
   public static Curve2 rect (
      final float lbx,
      final float lby,
      final float ubx,
      final float uby,
      final float corner,
      final Curve2 target ) {

      return Curve2.rect(
         lbx, lby, ubx, uby,
         corner, corner,
         corner, corner,
         target);
   }

   /**
    * Creates a rounded rectangle. The first four parameters specify the lower
    * and upper bound of the rectangle. The next four parameters specify the
    * rounding factor for the top left, top right, bottom right and bottom left
    * corners.
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
   public static Curve2 rect (
      final float lbx,
      final float lby,
      final float ubx,
      final float uby,
      final float tl,
      final float tr,
      final float br,
      final float bl,
      final Curve2 target ) {

      target.closedLoop = true;
      target.name = "Rect";
      target.resize(8);
      final Iterator < Knot2 > itr = target.knots.iterator();

      /* Validate corners. */
      final float x0 = lbx < ubx ? lbx : ubx;
      final float x1 = ubx > lbx ? ubx : lbx;
      final float y0 = lby > uby ? lby : uby;
      final float y1 = uby < lby ? uby : lby;

      /* Validate corner insetting. */
      final float vtl = Utils.max(Utils.abs(tl), IUtils.DEFAULT_EPSILON);
      final float vtr = Utils.max(Utils.abs(tr), IUtils.DEFAULT_EPSILON);
      final float vbr = Utils.max(Utils.abs(br), IUtils.DEFAULT_EPSILON);
      final float vbl = Utils.max(Utils.abs(bl), IUtils.DEFAULT_EPSILON);

      /* Top edge. */
      final Knot2 k0 = itr.next().set(x0 + vtl, y0, 0.0f, 0.0f, 0.0f, 0.0f);
      final Knot2 k1 = itr.next().set(x1 - vtr, y0, 0.0f, 0.0f, 0.0f, 0.0f);

      /* Right edge. */
      final Knot2 k2 = itr.next().set(x1, y0 - vtr, 0.0f, 0.0f, 0.0f, 0.0f);
      final Knot2 k3 = itr.next().set(x1, y1 + vbr, 0.0f, 0.0f, 0.0f, 0.0f);

      /* Bottom edge. */
      final Knot2 k4 = itr.next().set(x1 - vbr, y1, 0.0f, 0.0f, 0.0f, 0.0f);
      final Knot2 k5 = itr.next().set(x0 + vbl, y1, 0.0f, 0.0f, 0.0f, 0.0f);

      /* Left edge. */
      final Knot2 k6 = itr.next().set(x0, y1 + vbl, 0.0f, 0.0f, 0.0f, 0.0f);
      final Knot2 k7 = itr.next().set(x0, y0 - vtl, 0.0f, 0.0f, 0.0f, 0.0f);

      /* Cache knot coordinate shortcuts . */
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
      if ( tr < 0.0f ) {
         k1fh.x = k1co.x;
         k1fh.y = ( k1co.y + k2co.y ) * 0.5f;
         k2rh.x = ( k2co.x + k1co.x ) * 0.5f;
         k2rh.y = k2co.y;
      } else {
         k1fh.x = ( k1co.x + x1 ) * 0.5f;
         k1fh.y = y0;
         k2rh.x = x1;
         k2rh.y = ( k2co.y + y0 ) * 0.5f;
      }

      /* Bottom Right Corner. */
      if ( br < 0.0f ) {
         k3fh.x = ( k3co.x + k4co.x ) * 0.5f;
         k3fh.y = k3co.y;
         k4rh.x = k4co.x;
         k4rh.y = ( k4co.y + k3co.y ) * 0.5f;
      } else {
         k3fh.x = x1;
         k3fh.y = ( k3co.y + y1 ) * 0.5f;
         k4rh.x = ( k4co.x + x1 ) * 0.5f;
         k4rh.y = y1;
      }

      /* Bottom Left Corner. */
      if ( bl < 0.0f ) {
         k5fh.x = k5co.x;
         k5fh.y = ( k5co.y + k6co.y ) * 0.5f;
         k6rh.x = ( k6co.x + k5co.x ) * 0.5f;
         k6rh.y = k6co.y;
      } else {
         k5fh.x = ( k5co.x + x0 ) * 0.5f;
         k5fh.y = y1;
         k6rh.x = x0;
         k6rh.y = ( k6co.y + y1 ) * 0.5f;
      }

      /* Top Left Corner. */
      if ( tl < 0.0f ) {
         k7fh.x = ( k7co.x + k0co.x ) * 0.5f;
         k7fh.y = k7co.y;
         k0rh.x = k0co.x;
         k0rh.y = ( k0co.y + k7co.y ) * 0.5f;
      } else {
         k7fh.x = x0;
         k7fh.y = ( k7co.y + y0 ) * 0.5f;
         k0rh.x = ( k0co.x + x0 ) * 0.5f;
         k0rh.y = y0;
      }

      return target;
   }

   /**
    * Creates a rectangle. The first coordinate specifies the top left corner;
    * the second coordinate specifies the bottom right corner.
    *
    * @param tl     the top left corner
    * @param br     the bottom right corner
    * @param target the output curve
    *
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
    * Creates a rounded rectangle. The first coordinate specifies the top left
    * corner; the second coordinate specifies the bottom right corner. The third
    * parameter specifies the corner rounding factor.
    *
    * @param tl     the top left corner
    * @param br     the bottom right corner
    * @param corner the rounding factor
    * @param target the output curve
    *
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
    * Creates a rounded rectangle. The first coordinate specifies the top left
    * corner; the second coordinate, specifies the bottom right corner. The next
    * four parameters specify the rounding factor for the top left, top right,
    * bottom right and bottom left corners.
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
    * Adjusts knot handles so as to create a smooth, continuous curve.
    *
    * @param target the output curve
    *
    * @return the curve
    *
    * @see Knot2#smoothHandles(Knot2, Knot2, Knot2, Vec2)
    * @see Knot2#smoothHandlesLast(Knot2, Knot2, Vec2)
    * @see Knot2#smoothHandlesFirst(Knot2, Knot2, Vec2)
    */
   public static Curve2 smoothHandles ( final Curve2 target ) {

      final List < Knot2 > knots = target.knots;
      final int knotLength = knots.size();
      if ( knotLength < 3 ) { return target; }

      final int knotLast = knotLength - 1;
      final Vec2 dir = new Vec2();

      if ( target.closedLoop ) {

         final Iterator < Knot2 > itr = knots.iterator();
         final Knot2 first = itr.next();
         Knot2 prev = knots.get(knotLast);
         Knot2 curr = first;
         while ( itr.hasNext() ) {
            final Knot2 next = itr.next();
            Knot2.smoothHandles(prev, curr, next, dir);
            prev = curr;
            curr = next;
         }
         Knot2.smoothHandles(prev, curr, first, dir);

      } else {

         Knot2 prev = knots.get(0);
         Knot2 curr = knots.get(1);
         Knot2.smoothHandlesFirst(prev, curr, dir)
            .mirrorHandlesForward();

         for ( int i = 2; i < knotLength; ++i ) {
            final Knot2 next = knots.get(i);
            Knot2.smoothHandles(prev, curr, next, dir);
            prev = curr;
            curr = next;
         }

         Knot2.smoothHandlesLast(
            knots.get(knotLength - 2),
            knots.get(knotLast), dir)
            .mirrorHandlesBackward();

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
   public static Curve2 straightenHandles ( final Curve2 target ) {

      final List < Knot2 > knots = target.knots;
      final int knotLength = knots.size();
      if ( knotLength < 2 ) { return target; }

      if ( knotLength == 2 ) {
         final Knot2 first = knots.get(0);
         final Knot2 last = knots.get(knotLength - 1);

         Curve2.lerp13(first.coord, last.coord, first.foreHandle);
         first.mirrorHandlesForward();

         Curve2.lerp13(last.coord, first.coord, last.rearHandle);
         last.mirrorHandlesBackward();

         return target;
      }

      final Iterator < Knot2 > itr = knots.iterator();
      Knot2 prev = null;
      Knot2 curr = itr.next();
      while ( itr.hasNext() ) {
         prev = curr;
         curr = itr.next();
         Curve2.lerp13(prev.coord, curr.coord, prev.foreHandle);
         Curve2.lerp13(curr.coord, prev.coord, curr.rearHandle);
      }

      if ( target.closedLoop ) {
         final Knot2 first = knots.get(0);
         final Knot2 last = knots.get(knotLength - 1);
         Curve2.lerp13(first.coord, last.coord, first.rearHandle);
         Curve2.lerp13(last.coord, first.coord, last.foreHandle);
      } else {
         knots.get(0).mirrorHandlesForward();
         knots.get(knotLength - 1).mirrorHandlesBackward();
      }

      return target;
   }

   /**
    * A helper function. Returns a knot given two knots and a step. Assumes the
    * step has already been checked, and that the knots are in sequence along
    * the curve.
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
   static Knot2 bezierKnot (
      final Knot2 a,
      final Knot2 b,
      final float step,
      final Knot2 target ) {

      final Vec2 aco = a.coord;
      final Vec2 afh = a.foreHandle;
      final Vec2 bco = b.coord;
      final Vec2 brh = b.rearHandle;
      final Vec2 tco = target.coord;
      final Vec2 trh = target.rearHandle;
      final Vec2 tfh = target.foreHandle;

      Vec2.bezierPoint(
         aco, afh, brh, bco,
         step, tco);

      Vec2.bezierTangent(
         aco, afh, brh, bco,
         step, tfh);

      /* Find rear handle by reversing directions. */
      Vec2.bezierTangent(
         bco, brh, afh, aco,
         1.0f - step, trh);

      /* Convert fore and rear handle from direction to point. */
      Vec2.add(tco, tfh, tfh);
      Vec2.add(tco, trh, trh);

      return target;
   }

   /**
    * A helper function for evaluation. Returns a coordinate given two knots and
    * a step. Assumes the step has already been checked, and that the knots are
    * in sequence along the curve.
    *
    * @param a      the origin knot
    * @param b      the destination knot
    * @param step   the step
    * @param target the output coordinate
    *
    * @return the coordinate
    *
    * @see Vec2#bezierPoint(Vec2, Vec2, Vec2, Vec2, float, Vec2)
    */
   static Vec2 bezierPoint (
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
    * A helper function for evaluation. Returns a normalized tangent given two
    * knots. Assumes the step has already been checked, and that the knots are
    * in sequence along the curve.
    *
    * @param a      the origin knot
    * @param b      the destination knot
    * @param step   the step
    * @param target the output tangent
    *
    * @return the normalized tangent
    *
    * @see Vec2#bezierTanUnit(Vec2, Vec2, Vec2, Vec2, float, Vec2)
    */
   static Vec2 bezierTanUnit (
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
    * A utility function for setting the handles of knots on straight curve
    * segments. Finds unclamped linear interpolation from origin to destination
    * by a step of 1.0 / 3.0 .
    *
    * @param a      the origin
    * @param b      the destination
    * @param target the target
    *
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
      public Easing ( ) {

         this.easingFunc = new Knot2.Lerp();
      }

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
      public Curve2 apply (
         final Curve2 origin,
         final Curve2 dest,
         final Float step,
         final Curve2 target ) {

         if ( step <= 0.0f ) { return target.set(origin); }
         if ( step >= 1.0f ) { return target.set(dest); }
         return this.applyUnclamped(origin, dest, step, target);
      }

      /**
       * Eases between curves in an array by a step in the range [0.0, 1.0].
       *
       * @param arr    the curve array
       * @param step   the step
       * @param target the output curve
       */
      @Override
      public Curve2 apply (
         final Curve2[] arr,
         final Float step,
         final Curve2 target ) {

         final int len = arr.length;
         if ( len == 1 || step <= 0.0f ) { return target.set(arr[0]); }
         if ( step >= 1.0f ) { return target.set(arr[len - 1]); }

         final float scaledStep = step * ( len - 1 );
         final int i = ( int ) scaledStep;
         return this.applyUnclamped(
            arr[i], arr[i + 1],
            scaledStep - i, target);
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
      public Curve2 applyUnclamped (
         final Curve2 origin,
         final Curve2 dest,
         final float step,
         final Curve2 target ) {

         final List < Knot2 > orKn = origin.knots;
         final List < Knot2 > dsKn = dest.knots;

         if ( orKn.size() == dsKn
            .size() && origin.closedLoop == dest.closedLoop ) {

            target.closedLoop = origin.closedLoop;
            target.resize(orKn.size());

            final Iterator < Knot2 > orItr = orKn.iterator();
            final Iterator < Knot2 > dsItr = dsKn.iterator();
            final Iterator < Knot2 > tgItr = target.knots.iterator();
            while ( orItr.hasNext() && dsItr.hasNext() ) {
               this.easingFunc.apply(
                  orItr.next(),
                  dsItr.next(),
                  step,
                  tgItr.next());
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
