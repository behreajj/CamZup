package camzup.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

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
public class Octree {

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
    * The depth, or level, of the octree node. The root node is at
    * {@value Octree#ROOT_LEVEL}.
    */
   protected final int level;

   /**
    * Elements contained by this octree if it is a leaf.
    */
   protected TreeSet < Vec3 > points;

   /**
    * The default constructor.
    */
   public Octree ( ) {

      /*
       * This no longer implements Comparable<Octree> (based on its bounds
       * center) because then it would also have to implement equals and
       * hashCode to fulfill the interface contract, and it's not clear how two
       * should be equated.
       */

      this(new Bounds3());
   }

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

      this(bounds, capacity, Octree.ROOT_LEVEL);
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
    * Constructs an octree from an array of points and a capacity per node.
    *
    * @param points   the points
    * @param capacity capacity per node
    */
   public Octree ( final Vec3[] points, final int capacity ) {

      this.bounds = Bounds3.fromPoints(points, new Bounds3());
      this.capacity = capacity < 1 ? 1 : capacity;
      this.level = Octree.ROOT_LEVEL;
      this.points = new TreeSet <>();
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
      this.level = level < Octree.ROOT_LEVEL ? Octree.ROOT_LEVEL : level;
      this.points = new TreeSet <>();
   }

   /**
    * Finds the average centers for each leaf node of this octree.
    *
    * @return centers
    */
   public Vec3[] centersMean ( ) { return this.centersMean(false); }

   /**
    * Finds the average centers for each leaf node of this octree. If the node
    * is empty, and includeEmpty is true, then the center of the cell bounds
    * is used instead.
    *
    * @param includeEmpty include empty cells
    *
    * @return centers
    */
   public Vec3[] centersMean ( final boolean includeEmpty ) {

      final ArrayList < Vec3 > results = new ArrayList <>(32);
      this.centersMean(includeEmpty, results);
      return results.toArray(new Vec3[results.size()]);
   }

   /**
    * Counts the number of leaves held by this octree. Returns 1 if this node
    * is itself a leaf.
    *
    * @return the sum
    */
   @Recursive
   public int countLeaves ( ) {

      int sum = 0;
      boolean isLeaf = true;
      for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
         final Octree child = this.children[i];
         if ( child != null ) {
            isLeaf = false;
            sum += child.countLeaves();
         }
      }

      if ( isLeaf ) { return 1; }
      return sum;
   }

   /**
    * Counts the number of points held by this octree's leaf nodes.
    *
    * @return the sum
    */
   @Recursive
   public int countPoints ( ) {

      int sum = 0;
      boolean isLeaf = true;
      for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
         final Octree child = this.children[i];
         if ( child != null ) {
            isLeaf = false;
            sum += child.countPoints();
         }
      }

      if ( isLeaf ) { sum += this.points.size(); }
      return sum;
   }

   /**
    * Sets empty child nodes in the octree to null. Returns true if this
    * octree node should be culled; i.e., all its children are null and it
    * contains no points.
    *
    * @return the evaluation
    */
   @Experimental
   @Recursive
   public boolean cull ( ) {

      int cullThis = 0;
      for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
         final Octree child = this.children[i];
         if ( child != null ) {
            if ( child.cull() ) {
               this.children[i] = null;
               ++cullThis;
            }
         } else {
            ++cullThis;
         }
      }

      return cullThis >= Octree.CHILD_COUNT && this.points.isEmpty();
   }

   /**
    * Gets the capacity of the node.
    *
    * @return the capacity
    */
   public int getCapacity ( ) { return this.capacity; }

   /**
    * Gets the leaf nodes of an octree as a flat array. The nodes are passed
    * by reference, not by value.
    *
    * @return the octree array
    *
    * @see Octree#getLeaves(ArrayList)
    */
   public Octree[] getLeaves ( ) {

      final ArrayList < Octree > references = new ArrayList <>(32);
      this.getLeaves(references);
      return references.toArray(new Octree[references.size()]);
   }

   /**
    * Gets the level, or depth, of the node.
    *
    * @return the level
    */
   public int getLevel ( ) { return this.level; }

   /**
    * Gets the maximum level, or depth, of the node and its children.
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
    * Inserts a point into the octree by reference. Returns <code>true</code>
    * if the point was successfully inserted into the octree directly or
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
         boolean isLeaf = true;
         for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
            final Octree child = this.children[i];
            if ( child != null ) {
               isLeaf = false;
               if ( child.insert(point) ) { return true; }
            }
         }

         if ( isLeaf ) {
            this.points.add(point);
            if ( this.points.size() > this.capacity ) {
               this.split(this.capacity);
            }
            return true;
         } else {
            /* Case where a child has been culled. Try again. */
            this.split(this.capacity);
            return this.insert(point);
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
    * Evaluates whether this octree node has any children. Returns true if no;
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
    * If this octree node has children, converts it to a leaf node containing
    * the centers of its children. Increases node capacity if necessary.
    *
    * @return this octree
    */
   @Experimental
   public Octree merge ( ) {

      if ( this.isLeaf() ) { return this; }

      this.points.clear();

      final ArrayList < Vec3 > pts = new ArrayList <>(Octree.DEFAULT_CAPACITY);
      for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
         final Octree child = this.children[i];
         if ( child != null ) {
            child.getPoints(pts);
            final int ptsLen = pts.size();
            if ( ptsLen > 0 ) {
               final Vec3 center = new Vec3();
               final Iterator < Vec3 > itr = pts.iterator();
               while ( itr.hasNext() ) { Vec3.add(center, itr.next(), center); }
               Vec3.mul(center, 1.0f / ptsLen, center);
               this.points.add(center);
               pts.clear();
            }
            this.children[i] = null;
         }
      }

      final int pointsLen = this.points.size();
      if ( pointsLen > this.capacity ) { this.capacity = pointsLen; }

      return this;
   }

   /**
    * Queries the octree with a rectangular range, returning points inside the
    * range.
    *
    * @param range the range
    *
    * @return the points.
    */
   public Vec3[] queryRange ( final Bounds3 range ) {

      final TreeMap < Float, Vec3 > found = new TreeMap <>();
      this.queryRange(range, found);

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
   public Vec3[] queryRange ( final Vec3 center, final float radius ) {

      final TreeMap < Float, Vec3 > found = new TreeMap <>();
      this.queryRange(center, radius, found);

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
    * Sets the capacity of the node. If the node's point size exceeds the new
    * capacity, the node is split.
    *
    * @param capacity the new capacity
    *
    * @see Octree#split()
    */
   public void setCapacity ( final int capacity ) {

      if ( capacity > 0 ) {
         this.capacity = capacity;
         if ( this.points.size() > capacity ) { this.split(this.capacity); }
      }
   }

   /**
    * Subdivides this octree.
    *
    * @return this octree
    */
   public Octree subdivide ( ) {

      return this.subdivide(1);
   }

   /**
    * Subdivides this octree
    *
    * @param iterations iteration count
    *
    * @return this octree
    */
   public Octree subdivide ( final int iterations ) {

      return this.subdivide(iterations, this.capacity);
   }

   /**
    * Subdivides this octree. For cases where a minimum number of children
    * nodes is desired, independent of point insertion. The result will be
    * {@value Octree#CHILD_COUNT} raised to the power of iterations, e.g., 8,
    * 64, 512, etc.
    *
    * @param iterations    iteration count
    * @param childCapacity child capacity
    *
    * @return this octree
    *
    * @see Octree#split(int)
    */
   public Octree subdivide ( final int iterations, final int childCapacity ) {

      if ( iterations < 1 ) { return this; }

      for ( int i = 0; i < iterations; ++i ) {
         boolean isLeaf = true;
         for ( int j = 0; j < Octree.CHILD_COUNT; ++j ) {
            final Octree child = this.children[j];
            if ( child != null ) {
               isLeaf = false;
               child.subdivide(iterations - 1, childCapacity);
            }
         }

         if ( isLeaf ) { this.split(childCapacity); }
      }

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
    * Finds the average center in each leaf node of this octree and appends it
    * to an array. If the node is empty, and includeEmpty is true, then the
    * center of the cell bounds is used instead.
    *
    * @param includeEmpty include empty cells
    * @param target       output array list
    *
    * @return centers
    *
    * @see Vec3#add(Vec3, Vec3, Vec3)
    * @see Vec3#mul(Vec3, float, Vec3)
    * @see Bounds3#center(Bounds3, Vec3)
    */
   @Recursive
   protected ArrayList < Vec3 > centersMean ( final boolean includeEmpty,
      final ArrayList < Vec3 > target ) {

      /* Even with a tree set, centersMedian is not worth it. */

      boolean isLeaf = true;
      for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
         final Octree child = this.children[i];
         if ( child != null ) {
            isLeaf = false;
            child.centersMean(includeEmpty, target);
         }
      }

      if ( isLeaf ) {
         final int ptsLen = this.points.size();
         final Iterator < Vec3 > ptsItr = this.points.iterator();
         if ( ptsLen > 1 ) {
            final Vec3 result = new Vec3();
            while ( ptsItr.hasNext() ) {
               Vec3.add(result, ptsItr.next(), result);
            }
            Vec3.mul(result, 1.0f / ptsLen, result);
            target.add(result);
         } else if ( ptsLen > 0 ) {
            target.add(new Vec3(ptsItr.next()));
         } else if ( includeEmpty ) {
            target.add(Bounds3.center(this.bounds, new Vec3()));
         }
      }

      return target;
   }

   /**
    * Gets the leaf nodes in this node and its children. The leaf nodes will
    * still be stored by reference in the output array list, so this should be
    * used internally.
    *
    * @param target the output array list
    *
    * @return the leaf nodes
    */
   @Recursive
   protected ArrayList < Octree > getLeaves ( final ArrayList <
      Octree > target ) {

      boolean isLeaf = true;
      for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
         final Octree child = this.children[i];
         if ( child != null ) {
            isLeaf = false;
            child.getLeaves(target);
         }
      }
      if ( isLeaf ) { target.add(this); }
      return target;
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

      boolean isLeaf = true;
      for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
         final Octree child = this.children[i];
         if ( child != null ) {
            isLeaf = false;
            child.getPoints(target);
         }
      }
      if ( isLeaf ) { target.addAll(this.points); }
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
   protected TreeMap < Float, Vec3 > queryRange ( final Bounds3 range,
      final TreeMap < Float, Vec3 > found ) {

      if ( Bounds3.intersect(range, this.bounds) ) {
         boolean isLeaf = true;
         for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
            final Octree child = this.children[i];
            if ( child != null ) {
               isLeaf = false;
               child.queryRange(range, found);
            }
         }

         if ( isLeaf ) {
            final Iterator < Vec3 > itr = this.points.iterator();
            final Vec3 rCenter = new Vec3();
            Bounds3.center(range, rCenter);
            while ( itr.hasNext() ) {
               final Vec3 point = itr.next();
               if ( Bounds3.containsInclusive(range, point) ) {
                  found.put(Vec3.distChebyshev(point, rCenter), point);
               }
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
   @Recursive
   protected TreeMap < Float, Vec3 > queryRange ( final Vec3 center,
      final float radius, final TreeMap < Float, Vec3 > found ) {

      if ( Bounds3.intersect(this.bounds, center, radius) ) {
         boolean isLeaf = true;
         for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
            final Octree child = this.children[i];
            if ( child != null ) {
               isLeaf = false;
               child.queryRange(center, radius, found);
            }
         }

         if ( isLeaf ) {
            final float rsq = radius * radius;
            final Iterator < Vec3 > itr = this.points.iterator();
            while ( itr.hasNext() ) {
               final Vec3 point = itr.next();
               final float dsq = Vec3.distSq(center, point);
               if ( dsq < rsq ) { found.put(Utils.sqrt(dsq), point); }
            }
         }
      }

      return found;
   }

   /**
    * Splits this octree node into eight child nodes. The child capacity is
    * the same as the parent's.
    *
    * @return this octree
    */
   protected Octree split ( ) { return this.split(this.capacity); }

   /**
    * Splits this octree node into eight child nodes.
    *
    * @param childCapacity child capacity
    *
    * @return this octree
    */
   protected Octree split ( final int childCapacity ) {

      final int nextLevel = this.level + 1;
      for ( int i = 0; i < Octree.CHILD_COUNT; ++i ) {
         final Octree child = this.children[i];
         if ( child != null ) {
            /* Bounds will be set by the split method. Level is final. */
            child.setCapacity(childCapacity);
         } else {
            this.children[i] = new Octree(new Bounds3(), childCapacity,
               nextLevel);
         }
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

      /*
       * Pass on points to children. Begin search for the appropriate child node
       * at the index where the previous point was inserted.
       */
      final Iterator < Vec3 > itr = this.points.iterator();
      int idxOffset = 0;
      while ( itr.hasNext() ) {
         final Vec3 v = itr.next();
         boolean found = false;
         for ( int j = 0; j < Octree.CHILD_COUNT && !found; ++j ) {
            final int k = ( idxOffset + j ) % Octree.CHILD_COUNT;
            found = this.children[k].insert(v);
            if ( found ) { idxOffset = k; }
         }
      }
      this.points.clear();

      return this;
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
            final Octree child = this.children[i];
            if ( child != null ) {
               child.toString(sb, places);
               sb.append(',').append(' ');
            } else {
               sb.append("null, ");
            }
         }

         final Octree last = this.children[Octree.CHILD_COUNT - 1];
         if ( last != null ) {
            sb.append(last.toString(places));
         } else {
            sb.append("null");
         }

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
    * Number of children held by a node.
    */
   public static final int CHILD_COUNT = 8;

   /**
    * The default point capacity.
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

   /**
    * The root level, or depth, {@value Octree#ROOT_LEVEL}.
    */
   public static final int ROOT_LEVEL = 0;

}