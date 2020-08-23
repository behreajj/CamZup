package camzup.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Partitions space to improve collision and intersection tests. An octree
 * node holds a list of points up to a given capacity; when that capacity
 * is exceeded, the node is split into eight children nodes (octants) and
 * its list of points is emptied into them. The octants are indexed in an
 * array as follows:
 *
 * <pre>
 *           **|-----|-----|
 *       ******|   6 |   7 |
 *   **********| FNW | FNE |
 * |-----|-----|-----|-----|
 * |   2 |   3 |   4 |   5 |
 * | BNW | BNE | FSW | FSE |
 * |-----|-----|-----|-----|
 * |   0 |   1 |**********
 * | BSW | BSE |******
 * |-----|-----|**
 * </pre>
 *
 * forming a backwards z pattern.
 */
public class Octree implements Iterable < Vec3 > {

   /**
    * The bounding volume.
    */
   public Bounds3 bounds;

   /**
    * Children nodes.
    */
   public final Octree[] children = new Octree[8];

   /**
    * The number of elements an octree can hold before it is split into child
    * nodes.
    */
   protected int capacity;

   /**
    * The depth, or level, of the octree node.
    */
   protected final int level;

   /**
    * Elements contained by this octree if it is a leaf.
    */
   protected HashSet < Vec3 > points;

   /**
    * The default constructor.
    */
   public Octree ( ) { this(new Bounds3()); }

   /**
    * Constructs an octree node with a boundary.
    *
    * @param bounds the bounds
    */
   public Octree ( final Bounds3 bounds ) {

      this(bounds, Octree.DEFAULT_CAPACITY);
   }

   /**
    * Constructs an octree node with a boundary and capacity.
    *
    * @param bounds   the bounds
    * @param capacity the capacity
    */
   public Octree ( final Bounds3 bounds, final int capacity ) {

      this(bounds, capacity, 0);
   }

   /**
    * Constructs an octree with a boundary and capacity. Protected so that the
    * level can be set.
    *
    * @param bounds   the bounds
    * @param capacity the capacity
    * @param level    the depth level
    */
   protected Octree ( final Bounds3 bounds, final int capacity,
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
    *
    * @see Bounds3#contains(Bounds3, Vec3)
    */
   @Recursive
   public boolean insert ( final Vec3 point ) {

      /* Lower bound inclusive, upper bound exclusive. */
      if ( Bounds3.contains(this.bounds, point) ) {
         if ( this.isLeaf() ) {
            this.points.add(point);
            if ( this.points.size() > this.capacity ) { this.split(); }
            return true;
         }

         for ( int i = 0; i < 8; ++i ) {
            if ( this.children[i].insert(point) ) { return true; }
         }
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
   public boolean insertAll ( final Vec3[] pts ) {

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

      for ( int i = 0; i < 8; ++i ) {
         if ( this.children[i] != null ) { return false; }
      }
      return true;
   }

   /**
    * Gets this octree's points iterator.
    *
    * @return the iterator
    */
   @Override
   public Iterator < Vec3 > iterator ( ) { return this.points.iterator(); }

   /**
    * Returns the number of points in this octree node.
    *
    * @return the length
    */
   public int length ( ) { return this.points.size(); }

   /**
    * Queries the octree with a rectangular range, returning points inside the
    * range.
    *
    * @param range the range
    *
    * @return the points.
    */
   public Vec3[] query ( final Bounds3 range ) {

      final ArrayList < Vec3 > result = new ArrayList <>();
      this.query(range, result);
      return result.toArray(new Vec3[result.size()]);
   }

   /**
    * Queries the octree with a circular range, returning points inside the
    * range.
    *
    * @param origin the circle origin
    * @param radius the circle radius
    *
    * @return the points.
    */
   public Vec3[] query ( final Vec3 origin, final float radius ) {

      final ArrayList < Vec3 > result = new ArrayList <>();
      this.query(origin, radius, result);
      return result.toArray(new Vec3[result.size()]);
   }

   /**
    * Resets this octree to an initial state, where its has no children and no
    * points.
    *
    * @return this octree
    */
   public Octree reset ( ) {

      this.points.clear();
      for ( int i = 0; i < 8; ++i ) {
         this.children[i] = null;
      }

      return this;
   }

   /**
    * Splits the octree into eight child nodes.
    *
    * @return this octree tree.
    */
   public Octree split ( ) {

      final int nextLevel = this.level + 1;
      for ( int i = 0; i < 8; ++i ) {
         this.children[i] = new Octree(new Bounds3(), this.capacity, nextLevel);
      }

      Iterator < Vec3 > itr;
      // Vec3 mean = new Vec3();
      // itr = this.points.iterator();
      // while ( itr.hasNext() ) {
      // Vec3.add(mean, itr.next(), mean);
      // }
      // Vec3.div(mean, this.points.size(), mean);

      Bounds3.split(this.bounds, 0.5f, 0.5f, 0.5f,
         this.children[Octree.BACK_SOUTH_WEST].bounds,
         this.children[Octree.BACK_SOUTH_EAST].bounds,
         this.children[Octree.BACK_NORTH_WEST].bounds,
         this.children[Octree.BACK_NORTH_EAST].bounds,
         this.children[Octree.FRONT_SOUTH_WEST].bounds,
         this.children[Octree.FRONT_SOUTH_EAST].bounds,
         this.children[Octree.FRONT_NORTH_WEST].bounds,
         this.children[Octree.FRONT_NORTH_EAST].bounds);

      /* Pass on points to children. */
      itr = this.points.iterator();
      while ( itr.hasNext() ) {
         final Vec3 v = itr.next();
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
         final Iterator < Vec3 > itr = this.points.iterator();
         while ( itr.hasNext() ) {
            sb.append(itr.next().toString(places));
            if ( itr.hasNext() ) { sb.append(", "); }
         }
         sb.append(" ]");
      } else {
         sb.append(", children: [ ");
         for ( int i = 0; i < 8; ++i ) {
            sb.append(this.children[i].toString(places));
            if ( i < 7 ) { sb.append(", "); }
         }
         sb.append(" ]");
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
    *
    * @see Bounds3#intersect(Bounds3, Bounds3)
    * @see Bounds3#containsInclusive(Bounds3, Vec3)
    */
   @Recursive
   protected ArrayList < Vec3 > query ( final Bounds3 range, final ArrayList <
      Vec3 > found ) {

      if ( Bounds3.intersect(range, this.bounds) ) {
         if ( this.isLeaf() ) {
            final Iterator < Vec3 > itr = this.points.iterator();
            while ( itr.hasNext() ) {
               final Vec3 point = itr.next();
               if ( Bounds3.containsInclusive(range, point) ) {
                  found.add(point);
               }
            }
         } else {
            for ( int i = 0; i < 8; ++i ) {
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
    * @see Bounds3#intersect(Bounds3, Vec3, float)
    * @see Vec3#distSq(Vec3, Vec3)
    */
   @Recursive
   protected ArrayList < Vec3 > query ( final Vec3 origin, final float radius,
      final ArrayList < Vec3 > found ) {

      if ( Bounds3.intersect(this.bounds, origin, radius) ) {
         if ( this.isLeaf() ) {
            final Iterator < Vec3 > itr = this.points.iterator();
            final float rsq = radius * radius;
            while ( itr.hasNext() ) {
               final Vec3 point = itr.next();
               if ( Vec3.distSq(origin, point) <= rsq ) { found.add(point); }
            }
         } else {
            for ( int i = 0; i < 8; ++i ) {
               this.children[i].query(origin, radius, found);
            }
         }
      }

      return found;
   }

   /**
    * Bottom North East index for array of children nodes.
    */
   public static final int BACK_NORTH_EAST = 3;

   /**
    * Bottom North West index for array of children nodes.
    */
   public static final int BACK_NORTH_WEST = 2;

   /**
    * Bottom South East index for array of children nodes.
    */
   public static final int BACK_SOUTH_EAST = 1;

   /**
    * Bottom South West index for array of children nodes.
    */
   public static final int BACK_SOUTH_WEST = 0;

   /**
    * The default capacity.
    */
   public static final int DEFAULT_CAPACITY = 8;

   /**
    * Top North East index for array of children nodes.
    */
   public static final int FRONT_NORTH_EAST = 7;

   /**
    * Top North West index for array of children nodes.
    */
   public static final int FRONT_NORTH_WEST = 6;

   /**
    * Top South East index for array of children nodes.
    */
   public static final int FRONT_SOUTH_EAST = 5;

   /**
    * Top South West index for array of children nodes.
    */
   public static final int FRONT_SOUTH_WEST = 4;

}