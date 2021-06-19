package camzup.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Partitions space to improve collision and intersection tests. A quadtree
 * node holds a list of points up to a given capacity; when that capacity
 * is exceeded, the node is split into four children nodes (quadrants) and
 * its list of points is emptied into them. The quadrants are indexed in an
 * array as follows
 *
 * <pre>
 * |----|----|
 * |  2 |  3 |
 * | NW | NE |
 * |----|----|
 * |  0 |  1 |
 * | SW | SE |
 * |----|----|
 * </pre>
 *
 * forming a backwards z pattern.
 */
public class Quadtree implements Iterable < Vec2 > {

   /**
    * The bounding area.
    */
   public Bounds2 bounds;

   /**
    * Children nodes.
    */
   public final Quadtree[] children = new Quadtree[4];

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
    * Constructs an octree from an array of points.
    *
    * @param points the points
    */
   public Quadtree ( final Vec2[] points ) {

      this(points, Quadtree.DEFAULT_CAPACITY);
   }

   /**
    * Constructs an octree from an array of points and a capacity per octree
    * node.
    *
    * @param points   the points
    * @param capacity capacity per node
    */
   public Quadtree ( final Vec2[] points, final int capacity ) {

      // TODO: TEST
      this.bounds = Bounds2.fromPoints(points, new Bounds2());
      this.capacity = capacity < 1 ? 1 : capacity;
      this.level = 0;
      this.points = new HashSet <>(this.capacity);
      this.insertAll(points);
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
    * Finds the mean center of the points contained in this quadtree node and
    * its children.
    *
    * @param target the output vector
    *
    * @return the center
    *
    * @see Quadtree#getPoints(ArrayList)
    */
   public Vec2 centerMean ( final Vec2 target ) {

      // TODO: TEST

      final ArrayList < Vec2 > pts = new ArrayList <>(
         Quadtree.DEFAULT_CAPACITY);
      this.getPoints(pts);

      target.reset();
      final int len = pts.size();
      if ( len > 0 ) {
         final Iterator < Vec2 > itr = pts.iterator();
         while ( itr.hasNext() ) { Vec2.add(target, itr.next(), target); }
         Vec2.div(target, len, target);
      }

      return target;
   }

   /**
    * Gets the level of the node.
    *
    * @return the level
    */
   public int getLevel ( ) { return this.level; }

   /**
    * Gets the maximum depth of the node and its children.
    *
    * @return the level
    */
   @Recursive
   public int getMaxLevel ( ) {

      int mxLvl = this.level;
      for ( int i = 0; i < 8; ++i ) {
         final Quadtree child = this.children[i];
         if ( child != null ) {
            final int lvl = child.getMaxLevel();
            if ( lvl > mxLvl ) { mxLvl = lvl; }
         }
      }
      return mxLvl;
   }

   /**
    * Gets an array of points contained in this quadtree and its children. The
    * points are transferred to the array by value, not reference, so changing
    * them in the array will not change them within the tree.
    *
    * @return the points array
    *
    * @see Quadtree#getPoints(ArrayList)
    */
   public Vec2[] getPoints ( ) {

      // TODO: TEST

      final ArrayList < Vec2 > references = new ArrayList <>(
         Quadtree.DEFAULT_CAPACITY);
      this.getPoints(references);
      final int len = references.size();
      final Vec2[] result = new Vec2[len];
      final Iterator < Vec2 > itr = references.iterator();
      for ( int i = 0; itr.hasNext(); ++i ) {
         result[i] = new Vec2(itr.next());
      }
      return result;
   }

   /**
    * Inserts a point into the quadtree. Returns <code>true</code> if the
    * point was successfully inserted into the quadtree directly or indirectly
    * through one of its children; returns <code>false</code> if the insertion
    * was unsuccessful.
    *
    * @param point the point
    *
    * @return the insertion success
    *
    * @see Bounds2#contains(Bounds2, Vec2)
    */
   @Recursive
   public boolean insert ( final Vec2 point ) {

      /* Lower bound inclusive, upper bound exclusive. */
      if ( Bounds2.contains(this.bounds, point) ) {
         if ( this.isLeaf() ) {
            this.points.add(point);
            if ( this.points.size() > this.capacity ) { this.split(); }
            return true;
         }

         for ( int i = 0; i < 4; ++i ) {
            if ( this.children[i].insert(point) ) { return true; }
         }
      }
      return false;
   }

   /**
    * Inserts points into the quadtree. Returns <code>true</code> if all
    * insertions were successful; <code>false</code> if at least one was
    * unsuccessful.
    *
    * @param pts the points
    *
    * @return the insertion success
    */
   public boolean insertAll ( final Vec2[] pts ) {

      final int len = pts.length;
      boolean flag = true;
      for ( int i = 0; i < len; ++i ) { flag &= this.insert(pts[i]); }
      return flag;
   }

   /**
    * Evaluates whether this quadtree node has any children; returns true if
    * no; otherwise false.
    *
    * @return the evaluation
    */
   public boolean isLeaf ( ) {

      for ( int i = 0; i < 4; ++i ) {
         if ( this.children[i] != null ) { return false; }
      }
      return true;
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
      for ( int i = 0; i < 4; ++i ) { this.children[i] = null; }

      return this;
   }

   /**
    * Splits the quadtree node into four child nodes.
    *
    * @return this quadtree.
    */
   public Quadtree split ( ) {

      final int nextLevel = this.level + 1;
      for ( int i = 0; i < 4; ++i ) {
         this.children[i] = new Quadtree(new Bounds2(), this.capacity,
            nextLevel);
      }

      Bounds2.split(this.bounds, 0.5f, 0.5f,
         this.children[Quadtree.SOUTH_WEST].bounds,
         this.children[Quadtree.SOUTH_EAST].bounds,
         this.children[Quadtree.NORTH_WEST].bounds,
         this.children[Quadtree.NORTH_EAST].bounds);

      /* Pass on points to children. */
      final Iterator < Vec2 > itr = this.points.iterator();
      while ( itr.hasNext() ) {
         final Vec2 v = itr.next();
         boolean flag = false;
         for ( int i = 0; i < 8 && !flag; ++i ) {
            flag = this.children[i].insert(v);
         }
      }

      this.points.clear();

      return this;
   }

   /**
    * Gets a string representation of this quadtree node.
    */
   @Override
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

   /**
    * Gets a string representation of this quadtree node.
    *
    * @param places number of decimal places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      return this.toString(new StringBuilder(1024), places).toString();
   }

   /**
    * Internal helper function to append a quadtree's representation to a
    * {@link StringBuilder}.
    *
    * @param sb     the string builder
    * @param places the number of places
    *
    * @return the string builder
    */
   @Recursive
   StringBuilder toString ( final StringBuilder sb, final int places ) {

      sb.append("{ bounds: ");
      this.bounds.toString(sb, places);
      sb.append(", capacity: ");
      sb.append(this.capacity);

      if ( this.isLeaf() ) {
         sb.append(", points: [ ");
         final Iterator < Vec2 > itr = this.points.iterator();
         while ( itr.hasNext() ) {
            itr.next().toString(sb, places);
            if ( itr.hasNext() ) { sb.append(',').append(' '); }
         }
         sb.append(' ').append(']');
      } else {
         sb.append(", children: [ ");
         for ( int i = 0; i < 3; ++i ) {
            this.children[i].toString(sb, places);
            sb.append(',').append(' ');
         }
         sb.append(this.children[3].toString(places));
         sb.append(' ').append(']');
      }

      sb.append(' ').append('}');
      return sb;
   }

   /**
    * Gets the points in this quadtree node and its children. The points will
    * still be stored by reference in the output array list, so this should be
    * used internally.
    *
    * @param target the output array list
    *
    * @return the points
    */
   @Recursive
   protected ArrayList < Vec2 > getPoints ( final ArrayList < Vec2 > target ) {

      if ( this.isLeaf() ) {
         target.addAll(this.points);
      } else {
         for ( int i = 0; i < 4; ++i ) { this.children[i].getPoints(target); }
      }
      return target;
   }

   /**
    * Queries the quadtree with a rectangular range. If points in the quadtree
    * are in range, they are added to the list.
    *
    * @param range the range
    * @param found the output list
    *
    * @return found points
    *
    * @see Bounds2#intersect(Bounds2, Bounds2)
    * @see Bounds2#containsInclusive(Bounds2, Vec2)
    */
   @Recursive
   protected ArrayList < Vec2 > query ( final Bounds2 range, final ArrayList <
      Vec2 > found ) {

      if ( Bounds2.intersect(range, this.bounds) ) {
         if ( this.isLeaf() ) {
            final Iterator < Vec2 > itr = this.points.iterator();
            while ( itr.hasNext() ) {
               final Vec2 point = itr.next();
               if ( Bounds2.containsInclusive(range, point) ) {
                  found.add(point);
               }
            }
         } else {
            for ( int i = 0; i < 4; ++i ) {
               this.children[i].query(range, found);
            }
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
    *
    * @see Bounds2#intersect(Bounds2, Vec2, float)
    * @see Vec2#distSq(Vec2, Vec2)
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
            for ( int i = 0; i < 4; ++i ) {
               this.children[i].query(origin, radius, found);
            }
         }
      }

      return found;
   }

   /**
    * The default capacity.
    */
   public static final int DEFAULT_CAPACITY = 8;

   /**
    * North East index for array of children nodes.
    */
   public static final int NORTH_EAST = 3;

   /**
    * North West index for array of children nodes.
    */
   public static final int NORTH_WEST = 2;

   /**
    * South East index for array of children nodes.
    */
   public static final int SOUTH_EAST = 1;

   /**
    * South West index for array of children nodes.
    */
   public static final int SOUTH_WEST = 0;

}
