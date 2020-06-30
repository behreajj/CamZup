package camzup.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

@Experimental
abstract class KdTree < T extends Comparable < T > > {

   /**
    * The comparators for each axis of three used to sort elements in that
    * dimension.
    */
   public List < Comparator < T > > axes;

   /**
    * The root node of the tree.
    */
   public Node < T > root;

   /**
    * Constructs a tree from an array of elements and
    *
    * @param arr
    * @param axes
    */
   public KdTree ( final T[] arr, final List < Comparator < T > > axes ) {

      /* Number of axes comparators determines the k of the tree. */
      this.axes = axes;

      /* Convert from array to a list. Ensures no in-place mutation. */
      final int len = arr.length;
      final ArrayList < T > list = new ArrayList <>(len);
      for ( int i = 0; i < len; ++i ) {
         list.add(arr[i]);
      }

      this.root = this.fromList(list, 0);
   }

   /**
    * Appends an element to the tree. Returns true if the element was
    * successfully added.
    *
    * @param elm the element
    *
    * @return the success
    */
   public boolean append ( final T elm ) {

      // TEST

      if ( elm == null ) { return false; }

      if ( this.root == null ) {
         this.root = new Node <>(elm);
         return true;
      }

      Node < T > node = this.root;
      while ( true ) {
         final int depth = node.depth;
         final Comparator < T > axis = this.getAxis(depth);
         if ( axis.compare(elm, node.data) <= 0 ) {
            if ( node.left == null ) {
               final Node < T > insert = new Node <>(elm, depth + 1);
               insert.parent = node;
               node.left = insert;
               break;
            }
            node = node.left;
         } else {
            if ( node.right == null ) {
               final Node < T > insert = new Node <>(elm, depth + 1);
               insert.parent = node;
               node.right = insert;
               break;
            }
            node = node.right;
         }
      }

      return true;
   }

   /**
    * Returns a comparator representing an axis, or dimension, of the tree.
    * The axis is chosen based on the depth modulo the dimensions in the tree.
    *
    * @param depth the depth
    *
    * @return the comparator
    */
   public Comparator < T > getAxis ( final int depth ) {

      return this.axes.get(Utils.mod(depth, this.axes.size()));
   }

   /**
    * Find a list of k nearest elements in the tree given a query and distance
    * metric. The distance metric should return a single precision scalar
    * given two input elements.
    *
    * @param query  the query
    * @param count  number of elements
    * @param metric the distance metric
    *
    * @return the list
    */
   public abstract ArrayList < T > nearest ( final T query, final int count,
      final BiFunction < T, T, Float > metric );

   // public ArrayList < T > nearest ( final T query, final int count,
   // final BiFunction < T, T, Float > metric ) {
   //
   // final ArrayList < T > neighbors = new ArrayList <>(count);
   // if ( query == null || this.root == null ) { return neighbors; }
   //
   // /* Wrap the distance metric in a node comparator. */
   // final Comparator < Node < T > > cmp = metric == null ? null
   // : new Comparator < >() {
   //
   // @Override
   // public int compare ( final Node < T > a, final Node < T > b ) {
   //
   // final float aDist = metric.apply(query, a.data);
   // final float bDist = metric.apply(query, b.data);
   // return aDist < bDist ? -1 : aDist > bDist ? 1 : 0;
   // }
   //
   // };
   //
   // /* Use the comparator as a basis for a tree set. */
   // final TreeSet < Node < T > > tree = new TreeSet <>(cmp);
   //
   // Node < T > prev = null;
   // Node < T > node = this.root;
   // while ( node != null ) {
   // final int depth = node.depth;
   // final Comparator < T > axis = this.getAxis(depth);
   // final int comparison = axis.compare(query, node.data);
   // prev = node;
   // node = comparison > 0 ? node.right : node.left;
   // }
   //
   // final Node < T > leaf = prev;
   // if ( leaf != null ) {
   // final Set < Node < T > > examined = new HashSet <>();
   // node = leaf;
   // while ( node != null ) {
   // // this.search(query, node, count, tree, examined, metric);
   // node = node.parent;
   // }
   // }
   //
   // /* Flatten tree set to a list. */
   // final Iterator < Node < T > > treeItr = tree.iterator();
   // while ( treeItr.hasNext() ) {
   // neighbors.add(treeItr.next().data);
   // }
   // return neighbors;
   // }

   /**
    * Returns a string representation of this tree.
    *
    * @return the string
    */
   @Override
   public String toString ( ) {

      final StringBuilder sb = new StringBuilder(1024);
      final int axesLen = this.axes.size();
      final int axesLast = axesLen - 1;

      sb.append("{ axes: [ ");
      for ( int i = 0; i < axesLen; ++i ) {
         sb.append(this.axes.get(i));
         if ( i < axesLast ) { sb.append(',').append(' '); }
      }
      sb.append(" ], root: ");
      sb.append(this.root);
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Internal, recursive function to construct a node given a list of points;
    * setting nodes' parent and children. Splits the list into left and right
    * lists based on a median point.
    *
    * @param list  the list of elements
    * @param depth the depth
    *
    * @return the node
    */
   protected Node < T > fromList ( final List < T > list, final int depth ) {

      if ( list == null ) { return null; }
      final int len = list.size();
      if ( len < 1 ) { return null; }

      final Comparator < T > axis = this.getAxis(depth);
      Collections.sort(list, axis);
      final int medianIdx = len / 2;
      final T medianVal = list.get(medianIdx);
      final Node < T > node = new Node <>(medianVal, depth);
      final List < T > left = new ArrayList <>(medianIdx);
      final List < T > right = new ArrayList <>(medianIdx);
      for ( int i = 0; i < len; ++i ) {
         if ( i != medianIdx ) {
            final T query = list.get(i);
            final int comparison = axis.compare(medianVal, query);
            if ( comparison <= 0 ) {
               left.add(query);
            } else {
               right.add(query);
            }
         }
      }

      if ( medianIdx - 1 >= 0 && left.size() > 0 ) {
         node.left = this.fromList(left, depth + 1);
         node.left.parent = node;
      }

      if ( medianIdx <= len - 1 && right.size() > 0 ) {
         node.right = this.fromList(right, depth + 1);
         node.right.parent = node;
      }

      return node;
   }

   /**
    * A node in the k dimensional Tree. Holds a piece of data.
    *
    * @param <T> the node data type.
    */
   public static class Node < T extends Comparable < T > > implements
      Comparable < Node < T > > {

      /**
       * The data held by the node.
       */
      public T data;

      /**
       * The left, or lesser, child of the node. May be <code>null</code>.
       */
      public Node < T > left = null;

      /**
       * The parent node of this node. May be <code>null</code>.
       */
      public Node < T > parent = null;

      /**
       * The right, or greater, child of the node. May be <code>null</code>.
       */
      public Node < T > right = null;

      /**
       * The node's depth.
       */
      int depth = 0;

      /**
       * Creates a node at depth zero with data.
       *
       * @param data the data
       */
      Node ( final T data ) { this(data, 0); }

      /**
       * Creates a node from data and a depth.
       *
       * @param data  the data
       * @param depth the depth
       */
      Node ( final T data, final int depth ) {

         this.data = data;
         this.depth = depth;
      }

      /**
       * Compares this node with another based on the comparison function of the
       * nodes' data.
       */
      @Override
      public int compareTo ( final Node < T > node ) {

         return this.data.compareTo(node.data);
      }

      @Override
      public int hashCode ( ) {

         final int prime = 31;
         int result = 1;
         result = prime * result + ( this.data == null ? 0 : this.data
            .hashCode() );
         return result;
      }

      /**
       * Returns a string representation of the node.
       *
       * @return the string
       */
      @Override
      public String toString ( ) {

         final StringBuilder sb = new StringBuilder();
         sb.append("{ depth: ");
         sb.append(Utils.toPadded(this.depth, 1));
         sb.append(", data: ");
         sb.append(this.data);

         if ( this.left != null ) {
            sb.append(", left: ");
            sb.append(this.left);
         }

         if ( this.right != null ) {
            sb.append(", right: ");
            sb.append(this.right);
         }

         sb.append(' ');
         sb.append('}');
         return sb.toString();
      }

      /**
       * Returns an evaluation as to whether the node is a root, i.e. if its
       * parent is <code>null</code>.
       *
       * @return the evaluation
       */
      boolean isRoot ( ) { return this.parent == null; }

   }

}
