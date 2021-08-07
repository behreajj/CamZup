package camzup.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

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
public class Octree implements Comparable < Octree >, Iterable < Vec3 > {

   /**
    * The bounding volume.
    */
   public Bounds3 bounds;

   /**
    * Children nodes.
    */
   public final Octree[] children = new Octree[Octree.CHILD_COUNT];

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
    * Constructs an octree from an array of points.
    *
    * @param points the points
    */
   public Octree ( final Vec3[] points ) {

      this(points, Octree.DEFAULT_CAPACITY);
   }

   /**
    * Constructs an octree from an array of points and a capacity per octree
    * node.
    *
    * @param points   the points
    * @param capacity capacity per node
    */
   public Octree ( final Vec3[] points, final int capacity ) {

      // TODO: TEST
      this.bounds = Bounds3.fromPoints(points, new Bounds3());
      this.capacity = capacity < 1 ? 1 : capacity;
      this.level = 0;
      this.points = new HashSet <>(this.capacity);
      this.insertAll(points);
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
    * Finds the mean center of the points contained in this octree node and
    * its children.
    *
    * @param target the output vector
    *
    * @return the center
    *
    * @see Octree#getPoints(ArrayList)
    */
   public Vec3 centerMean ( final Vec3 target ) {

      // TODO: TEST

      final ArrayList < Vec3 > pts = new ArrayList <>(Octree.DEFAULT_CAPACITY);
      this.getPoints(pts);

      target.reset();
      final int len = pts.size();
      if ( len > 0 ) {
         final Iterator < Vec3 > itr = pts.iterator();
         while ( itr.hasNext() ) { Vec3.add(target, itr.next(), target); }
         Vec3.div(target, len, target);
      }

      return target;
   }

   /**
    * Compares this octree to another based on a comparison between bounds.
    *
    * @param ot the other octree
    *
    * @return the evaluation
    */
   @Override
   public int compareTo ( final Octree ot ) {

      return this.bounds.compareTo(ot.bounds);
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
      for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
         final Octree child = this.children[i];
         if ( child != null ) {
            final int lvl = child.getMaxLevel();
            if ( lvl > mxLvl ) { mxLvl = lvl; }
         }
      }
      return mxLvl;
   }

   /**
    * Gets an array of points contained in this octree and its children. The
    * points are transferred to the array by value, not reference, so changing
    * them in the array will not change them within the tree.
    *
    * @return the points array
    *
    * @see Octree#getPoints(ArrayList)
    */
   public Vec3[] getPoints ( ) {

      // TODO: TEST

      final ArrayList < Vec3 > references = new ArrayList <>(
         Octree.DEFAULT_CAPACITY);
      this.getPoints(references);
      final int len = references.size();
      final Vec3[] result = new Vec3[len];
      final Iterator < Vec3 > itr = references.iterator();
      for ( int i = 0; itr.hasNext(); ++i ) {
         result[i] = new Vec3(itr.next());
      }
      return result;
   }

   /**
    * Inserts a point into the octree. Returns <code>true</code> if the point
    * was successfully inserted into the octree directly or indirectly through
    * one of its children; returns <code>false</code> if the insertion was
    * unsuccessful.
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

         for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
            if ( this.children[i].insert(point) ) { return true; }
         }
      }
      return false;
   }

   /**
    * Inserts points into the octree. Returns <code>true</code> if all
    * insertions were successful; <code>false</code> if at least one was
    * unsuccessful.
    *
    * @param pts the points
    *
    * @return the insertion success
    */
   public boolean insertAll ( final Vec3[] pts ) {

      final int len = pts.length;
      boolean flag = true;
      for ( int i = 0; i < len; ++i ) { flag &= this.insert(pts[i]); }
      return flag;
   }

   /**
    * Evaluates whether this octree node has any children; returns true if no;
    * otherwise false.
    *
    * @return the evaluation
    */
   public boolean isLeaf ( ) {

      for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
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

      final TreeMap < Float, Vec3 > found = new TreeMap <>();
      this.query(range, found);

      /* Copy by value, so references can't change. */
      final Collection < Vec3 > values = found.values();
      final Iterator < Vec3 > itr = values.iterator();
      final int len = values.size();
      final Vec3[] result = new Vec3[len];
      for ( int i = 0; itr.hasNext(); ++i ) {
         result[i] = new Vec3(itr.next());
      }
      return result;
   }

   /**
    * Queries the octree with a spherical range, returning points inside the
    * range.
    *
    * @param center the sphere center
    * @param radius the sphere radius
    *
    * @return the points.
    */
   public Vec3[] query ( final Vec3 center, final float radius ) {

      final TreeMap < Float, Vec3 > found = new TreeMap <>();
      this.query(center, radius, found);

      /* Copy by value, so references can't change. */
      final Collection < Vec3 > values = found.values();
      final Iterator < Vec3 > itr = values.iterator();
      final int len = values.size();
      final Vec3[] result = new Vec3[len];
      for ( int i = 0; itr.hasNext(); ++i ) {
         result[i] = new Vec3(itr.next());
      }
      return result;
   }

   /**
    * Resets this octree to an initial state, where its has no children and no
    * points.
    *
    * @return this octree
    */
   public Octree reset ( ) {

      this.points.clear();
      for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
         this.children[i] = null;
      }

      return this;
   }

   /**
    * Splits the octree node into eight child nodes.
    *
    * @return this octree
    */
   public Octree split ( ) {

      final int nextLevel = this.level + 1;
      for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
         this.children[i] = new Octree(new Bounds3(), this.capacity, nextLevel);
      }

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
      final Iterator < Vec3 > itr = this.points.iterator();
      while ( itr.hasNext() ) {
         final Vec3 v = itr.next();
         boolean flag = false;
         for ( int i = 0; i < Octree.CHILD_COUNT && !flag; ++i ) {
            flag = this.children[i].insert(v);
         }
      }
      this.points.clear();

      return this;
   }

   /**
    * Gets a string representation of this octree node.
    */
   @Override
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

   /**
    * Gets a string representation of this octree node.
    *
    * @param places number of decimal places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      return this.toString(new StringBuilder(1024), places).toString();
   }

   /**
    * Gets the points in this octree node and its children. The points will
    * still be stored by reference in the output array list, so this should be
    * used internally.
    *
    * @param target the output array list
    *
    * @return the points
    */
   @Recursive
   protected ArrayList < Vec3 > getPoints ( final ArrayList < Vec3 > target ) {

      if ( this.isLeaf() ) {
         target.addAll(this.points);
      } else {
         for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
            this.children[i].getPoints(target);
         }
      }
      return target;
   }

   /**
    * Queries the octree with a box range. If points in the octree are in
    * range, they are added to a {@link java.util.TreeMap}.
    *
    * @param range the range
    * @param found the output list
    *
    * @return found points
    *
    * @see Bounds3#intersect(Bounds3, Bounds3)
    * @see Bounds3#containsInclusive(Bounds3, Vec3)
    * @see Vec3#dist(Vec3, Vec3)
    * @see Bounds3#center(Bounds3, Vec3)
    */
   @Recursive
   protected TreeMap < Float, Vec3 > query ( final Bounds3 range,
      final TreeMap < Float, Vec3 > found ) {

      if ( Bounds3.intersect(range, this.bounds) ) {
         if ( this.isLeaf() ) {
            final Iterator < Vec3 > itr = this.points.iterator();
            final Vec3 rCenter = new Vec3();
            Bounds3.center(range, rCenter);
            while ( itr.hasNext() ) {
               final Vec3 point = itr.next();
               if ( Bounds3.containsInclusive(range, point) ) {
                  found.put(Vec3.dist(point, rCenter), point);
               }
            }
         } else {
            for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
               this.children[i].query(range, found);
            }
         }
      }

      return found;
   }

   /**
    * Queries the octree with a spherical range. If points in the octree are
    * in range, they are added to a {@link java.util.TreeMap}.
    *
    * @param center the sphere center
    * @param radius the sphere radius
    * @param found  the output list
    *
    * @return found points
    *
    * @see Bounds3#intersect(Bounds3, Vec3, float)
    * @see Vec3#distSq(Vec3, Vec3)
    * @see Utils#sqrt(float)
    */
   protected TreeMap < Float, Vec3 > query ( final Vec3 center,
      final float radius, final TreeMap < Float, Vec3 > found ) {

      if ( Bounds3.intersect(this.bounds, center, radius) ) {
         if ( this.isLeaf() ) {
            final Iterator < Vec3 > itr = this.points.iterator();
            final float rsq = radius * radius;
            while ( itr.hasNext() ) {
               final Vec3 point = itr.next();
               final float dsq = Vec3.distSq(center, point);
               if ( dsq <= rsq ) { found.put(Utils.sqrt(dsq), point); }
            }
         } else {
            for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
               this.children[i].query(center, radius, found);
            }
         }
      }

      return found;
   }

   /**
    * Internal helper function to append an octree's representation to a
    * {@link StringBuilder}.
    *
    * @param sb     the string builder
    * @param places the number of places
    *
    * @return the string builder
    */
   @Recursive
   protected StringBuilder toString ( final StringBuilder sb,
      final int places ) {

      sb.append("{ bounds: ");
      this.bounds.toString(sb, places);
      sb.append(", capacity: ");
      sb.append(this.capacity);

      if ( this.isLeaf() ) {
         sb.append(", points: [ ");
         final Iterator < Vec3 > itr = this.points.iterator();
         while ( itr.hasNext() ) {
            itr.next().toString(sb, places);
            if ( itr.hasNext() ) { sb.append(',').append(' '); }
         }
         sb.append(' ').append(']');
      } else {
         sb.append(", children: [ ");
         for ( int i = 0; i < Octree.CHILD_COUNT - 1; ++i ) {
            this.children[i].toString(sb, places);
            sb.append(',').append(' ');
         }
         sb.append(this.children[Octree.CHILD_COUNT - 1].toString(places));
         sb.append(' ').append(']');
      }

      sb.append(' ').append('}');
      return sb;
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
    * Number of children held by a quadtree.
    */
   public static final int CHILD_COUNT = 8;

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