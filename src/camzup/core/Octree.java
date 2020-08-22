package camzup.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Partitions space to improve collision and intersection tests. An octree
 * node holds a list of points up to a given capacity; when that capacity
 * is exceeded, the node is split into eight children nodes (octants) and
 * its list of points is emptied into the children.
 */
@Experimental
public class Octree implements Iterable < Vec3 > {

   /**
    * The back bottom left octree.
    */
   public Octree bbl;

   /**
    * The back bottom right octree.
    */
   public Octree bbr;

   /**
    * The bounding volume.
    */
   public Bounds3 bounds;

   /**
    * The back top left octree.
    */
   public Octree btl;

   /**
    * The back top right octree.
    */
   public Octree btr;

   /**
    * The front bottom left octree.
    */
   public Octree fbl;

   /**
    * The front bottom right octree.
    */
   public Octree fbr;

   /**
    * The front top left octree.
    */
   public Octree ftl;

   /**
    * The front top right octree.
    */
   public Octree ftr;

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
    */
   @Recursive
   public boolean insert ( final Vec3 point ) {

      if ( Bounds3.contains(this.bounds, point) ) {
         if ( this.isLeaf() ) {
            this.points.add(point);
            if ( this.points.size() > this.capacity ) { this.split(); }
            return true;
         }
         return this.bbl.insert(point) || this.bbr.insert(point) || this.btl
            .insert(point) || this.btr.insert(point) || this.fbl.insert(point)
            || this.fbr.insert(point) || this.ftl.insert(point) || this.ftr
               .insert(point);
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

      return this.bbl == null && this.bbr == null && this.btl == null
         && this.btr == null && this.fbl == null && this.fbr == null && this.ftl
            == null && this.ftr == null;
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
      this.bbl = null;
      this.bbr = null;
      this.btl = null;
      this.btr = null;
      this.fbl = null;
      this.fbr = null;
      this.ftl = null;
      this.ftr = null;

      return this;
   }

   /**
    * Splits the octree into eight child nodes.
    *
    * @return this octree tree.
    */
   public Octree split ( ) {

      final int nextLevel = this.level + 1;
      this.bbl = new Octree(new Bounds3(), this.capacity, nextLevel);
      this.bbr = new Octree(new Bounds3(), this.capacity, nextLevel);
      this.btl = new Octree(new Bounds3(), this.capacity, nextLevel);
      this.btr = new Octree(new Bounds3(), this.capacity, nextLevel);
      this.fbl = new Octree(new Bounds3(), this.capacity, nextLevel);
      this.fbr = new Octree(new Bounds3(), this.capacity, nextLevel);
      this.ftl = new Octree(new Bounds3(), this.capacity, nextLevel);
      this.ftr = new Octree(new Bounds3(), this.capacity, nextLevel);

      Iterator < Vec3 > itr;

      // Vec3 mean = new Vec3();
      // itr = this.points.iterator();
      // while ( itr.hasNext() ) {
      // Vec3.add(mean, itr.next(), mean);
      // }
      // Vec3.div(mean, this.points.size(), mean);
      // Bounds3.split(this.bounds, mean, this.bbl.bounds, this.bbr.bounds,
      // this.btl.bounds, this.btr.bounds, this.fbl.bounds, this.fbr.bounds,
      // this.ftl.bounds, this.ftr.bounds);

      Bounds3.split(this.bounds, this.bbl.bounds, this.bbr.bounds,
         this.btl.bounds, this.btr.bounds, this.fbl.bounds, this.fbr.bounds,
         this.ftl.bounds, this.ftr.bounds);

      /* Pass on points to children. */
      itr = this.points.iterator();
      while ( itr.hasNext() ) {
         final Vec3 v = itr.next();
         if ( !this.bbl.insert(v) && !this.bbr.insert(v) && !this.btl.insert(v)
            && !this.btr.insert(v) && !this.fbl.insert(v) && !this.fbr.insert(v)
            && !this.ftl.insert(v) ) {
            this.ftr.insert(v);
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
         sb.append(", bbl: ");
         sb.append(this.bbl.toString(places));
         sb.append(", bbr: ");
         sb.append(this.bbr.toString(places));
         sb.append(", btl: ");
         sb.append(this.btl.toString(places));
         sb.append(", btr: ");
         sb.append(this.btr.toString(places));
         sb.append(", fbl: ");
         sb.append(this.fbl.toString(places));
         sb.append(", fbr: ");
         sb.append(this.fbr.toString(places));
         sb.append(", ftl: ");
         sb.append(this.ftl.toString(places));
         sb.append(", ftr: ");
         sb.append(this.ftr.toString(places));
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
   protected ArrayList < Vec3 > query ( final Bounds3 range, final ArrayList <
      Vec3 > found ) {

      if ( Bounds3.intersect(range, this.bounds) ) {
         if ( this.isLeaf() ) {
            final Iterator < Vec3 > itr = this.points.iterator();
            while ( itr.hasNext() ) {
               final Vec3 point = itr.next();
               if ( Bounds3.contains(range, point) ) { found.add(point); }
            }
         } else {
            this.bbl.query(range, found);
            this.bbr.query(range, found);
            this.btl.query(range, found);
            this.btr.query(range, found);
            this.fbl.query(range, found);
            this.fbr.query(range, found);
            this.ftl.query(range, found);
            this.ftr.query(range, found);
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
            this.bbl.query(origin, radius, found);
            this.bbr.query(origin, radius, found);
            this.btl.query(origin, radius, found);
            this.btr.query(origin, radius, found);
            this.fbl.query(origin, radius, found);
            this.fbr.query(origin, radius, found);
            this.ftl.query(origin, radius, found);
            this.ftr.query(origin, radius, found);
         }
      }

      return found;
   }

   /**
    * The default capacity.
    */
   public static final int DEFAULT_CAPACITY = 8;

}