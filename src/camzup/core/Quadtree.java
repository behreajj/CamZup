package camzup.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Partitions space to improve collision and intersection tests. A quadtree
 * node holds a list of points up to a given capacity; when that capacity
 * is exceeded, the node is split into four children nodes (quadrants) and
 * its list of points is emptied into the children.
 */
@Experimental
public class Quadtree implements Iterable < Vec2 > {

   /**
    * The bottom left quadtree.
    */
   public Quadtree bl;

   /**
    * The bounding area.
    */
   public Bounds2 bounds;

   /**
    * The bottom right quadtree.
    */
   public Quadtree br;

   /**
    * The top left quadtree.
    */
   public Quadtree tl;

   /**
    * The top right quadtree.
    */
   public Quadtree tr;

   /**
    * The number of elements a quadtree can hold before it is split into child
    * nodes.
    */
   protected int capacity;

   /**
    * The depth, or level, of the quadtree node.
    */
   protected final int level;

   /**
    * Elements contained by this quadtree if it is a leaf.
    */
   protected HashSet < Vec2 > points;

   /**
    * The default constructor.
    */
   public Quadtree ( ) { this(new Bounds2()); }

   /**
    * Constructs a quadtree node with a boundary.
    *
    * @param bounds the bounds
    */
   public Quadtree ( final Bounds2 bounds ) {

      this(bounds, Quadtree.DEFAULT_CAPACITY);
   }

   /**
    * Constructs a quadtree node with a boundary and capacity.
    *
    * @param bounds   the bounds
    * @param capacity the capacity
    */
   public Quadtree ( final Bounds2 bounds, final int capacity ) {

      this(bounds, capacity, 0);
   }

   /**
    * Constructs a quadtree with a boundary and capacity. Protected so that
    * the level can be set.
    *
    * @param bounds   the bounds
    * @param capacity the capacity
    * @param level    the depth level
    */
   protected Quadtree ( final Bounds2 bounds, final int capacity,
      final int level ) {

      this.bounds = bounds;
      this.capacity = capacity < 1 ? 1 : capacity;
      this.level = level < 0 ? 0 : level;
      this.points = new HashSet <>(this.capacity);
   }

   /**
    * Gets the level, or depth, of the node.
    *
    * @return the level
    */
   public int getLevel ( ) { return this.level; }

   /**
    * Inserts a point into the quadtree. Returns <code>true</code> if the
    * point was successfully inserted into the quad tree directly or
    * indirectly through one of its children; returns <code>false</code> if
    * the insertion was unsuccessful.
    *
    * @param point the point
    *
    * @return the insertion success
    */
   @Recursive
   public boolean insert ( final Vec2 point ) {

      if ( Bounds2.contains(this.bounds, point) ) {
         if ( this.isLeaf() ) {
            this.points.add(point);
            if ( this.points.size() > this.capacity ) { this.split(); }
            return true;
         }
         return this.bl.insert(point) || this.br.insert(point) || this.tl
            .insert(point) || this.tr.insert(point);
      }
      return false;
   }

   /**
    * Inserts points into the quadtree.
    *
    * @param pts the points
    *
    * @return the insertion success
    */
   public boolean insertAll ( final Vec2[] pts ) {

      final int len = pts.length;
      boolean flag = true;
      for ( int i = 0; i < len; ++i ) {
         flag &= this.insert(pts[i]);
      }
      return flag;
   }

   /**
    * Is this a leaf node in the quadtree, i.e. does it have any children.
    *
    * @return the evaluation
    */
   public boolean isLeaf ( ) {

      return this.bl == null && this.br == null && this.tl == null && this.tr
         == null;
   }

   /**
    * Gets this quadtree's points iterator.
    *
    * @return the iterator
    */
   @Override
   public Iterator < Vec2 > iterator ( ) { return this.points.iterator(); }

   /**
    * Returns the number of points in this quadtree node.
    *
    * @return the length
    */
   public int length ( ) { return this.points.size(); }

   /**
    * Queries the quadtree with a rectangular range, returning points inside
    * the range.
    *
    * @param range the range
    *
    * @return the points.
    */
   public Vec2[] query ( final Bounds2 range ) {

      final ArrayList < Vec2 > result = new ArrayList <>();
      this.query(range, result);
      return result.toArray(new Vec2[result.size()]);
   }

   /**
    * Queries the quadtree with a circular range, returning points inside the
    * range.
    *
    * @param origin the circle origin
    * @param radius the circle radius
    *
    * @return the points.
    */
   public Vec2[] query ( final Vec2 origin, final float radius ) {

      final ArrayList < Vec2 > result = new ArrayList <>();
      this.query(origin, radius, result);
      return result.toArray(new Vec2[result.size()]);
   }

   /**
    * Resets this quadtree to an initial state, where its has no children and
    * no points.
    *
    * @return this quadtree
    */
   public Quadtree reset ( ) {

      this.points.clear();
      this.bl = null;
      this.br = null;
      this.tl = null;
      this.tr = null;

      return this;
   }

   /**
    * Splits the quadtree into four child nodes.
    *
    * @return this quad tree.
    */
   public Quadtree split ( ) {

      final int nextLevel = this.level + 1;

      this.bl = new Quadtree(new Bounds2(), this.capacity, nextLevel);
      this.br = new Quadtree(new Bounds2(), this.capacity, nextLevel);
      this.tl = new Quadtree(new Bounds2(), this.capacity, nextLevel);
      this.tr = new Quadtree(new Bounds2(), this.capacity, nextLevel);

      Iterator < Vec2 > itr;

      // Vec2 mean = new Vec2();
      // itr = this.points.iterator();
      // while(itr.hasNext()) {
      // Vec2.add(mean, itr.next(), mean);
      // }
      // Vec2.div(mean, this.points.size(), mean);
      // Bounds2.split(this.bounds, mean, this.bl.bounds, this.br.bounds,
      // this.tl.bounds, this.tr.bounds);

      Bounds2.split(this.bounds, this.bl.bounds, this.br.bounds, this.tl.bounds,
         this.tr.bounds);

      /* Pass on points to children. */
      itr = this.points.iterator();
      while ( itr.hasNext() ) {
         final Vec2 v = itr.next();
         if ( !this.bl.insert(v) && !this.br.insert(v) && !this.tl.insert(v) ) {
            this.tr.insert(v);
         }
      }

      this.points.clear();

      return this;
   }

   /**
    * Gets a string representation of this quadtree node.
    */
   @Override
   public String toString ( ) { return this.toString(4); }

   /**
    * Gets a string representation of this quadtree node.
    *
    * @param places number of decimal places
    *
    * @return the string
    */
   @Recursive
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(2048);
      sb.append("{ bounds: ");
      sb.append(this.bounds.toString(places));
      sb.append(", capacity: ");
      sb.append(this.capacity);

      if ( this.isLeaf() ) {
         sb.append(", points: [ ");
         final Iterator < Vec2 > itr = this.points.iterator();
         while ( itr.hasNext() ) {
            sb.append(itr.next().toString(places));
            if ( itr.hasNext() ) { sb.append(", "); }
         }
         sb.append(" ]");
      } else {
         sb.append(", bl: ");
         sb.append(this.bl.toString(places));
         sb.append(", br: ");
         sb.append(this.br.toString(places));
         sb.append(", tl: ");
         sb.append(this.tl.toString(places));
         sb.append(", tr: ");
         sb.append(this.tr.toString(places));
      }

      sb.append(" }");
      return sb.toString();
   }

   /**
    * Queries the quadtree with a rectangular range. If points in the quadtree
    * are in range, they are added to the list.
    *
    * @param range the range
    * @param found the output list
    *
    * @return found points
    */
   @Recursive
   protected ArrayList < Vec2 > query ( final Bounds2 range, final ArrayList <
      Vec2 > found ) {

      if ( Bounds2.intersect(range, this.bounds) ) {
         if ( this.isLeaf() ) {
            final Iterator < Vec2 > itr = this.points.iterator();
            while ( itr.hasNext() ) {
               final Vec2 point = itr.next();
               if ( Bounds2.contains(range, point) ) { found.add(point); }
            }
         } else {
            this.bl.query(range, found);
            this.br.query(range, found);
            this.tl.query(range, found);
            this.tr.query(range, found);
         }
      }

      return found;
   }

   /**
    * Queries the quadtree with a circular range. If points in the quadtree
    * are in range, they are added to the list.
    *
    * @param origin the circle origin
    * @param radius the circle radius
    * @param found  the output list
    *
    * @return found points
    */
   @Recursive
   protected ArrayList < Vec2 > query ( final Vec2 origin, final float radius,
      final ArrayList < Vec2 > found ) {

      if ( Bounds2.intersect(this.bounds, origin, radius) ) {
         if ( this.isLeaf() ) {
            final Iterator < Vec2 > itr = this.points.iterator();
            final float rsq = radius * radius;
            while ( itr.hasNext() ) {
               final Vec2 point = itr.next();
               if ( Vec2.distSq(origin, point) <= rsq ) { found.add(point); }
            }
         } else {
            this.bl.query(origin, radius, found);
            this.br.query(origin, radius, found);
            this.tl.query(origin, radius, found);
            this.tr.query(origin, radius, found);
         }
      }

      return found;
   }

   /**
    * The default capacity.
    */
   public static final int DEFAULT_CAPACITY = 8;

}
