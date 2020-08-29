package camzup.core;

import java.util.Comparator;

/**
 * Compares two face indices (an array of vertex indices) by averaging the
 * vectors referenced by them, then comparing the averages.
 */
public class SortLoops2 implements Comparator < int[][] > {

   /**
    * The coordinates array.
    */
   final Vec2[] coords;

   /**
    * Internal vector used to store the average coordinate for the left
    * comparisand.
    */
   protected final Vec2 aAvg;

   /**
    * Internal vector used to store the average coordinate for the right
    * comparisand.
    */
   protected final Vec2 bAvg;

   {
      this.aAvg = new Vec2();
      this.bAvg = new Vec2();
   }

   /**
    * Constructs a comparator with reference to the mesh's coordinates.
    *
    * @param coords the coordinate array.
    */
   protected SortLoops2 ( final Vec2[] coords ) {

      this.coords = coords;
   }

   /**
    * Compares two face loops' indices.
    *
    * @param a the left comparisand
    * @param b the right comparisand
    */
   @Override
   public int compare ( final int[][] a, final int[][] b ) {

      this.aAvg.reset();
      final int aLen = a.length;
      for ( int i = 0; i < aLen; ++i ) {
         Vec2.add(this.aAvg, this.coords[a[i][0]], this.aAvg);
      }
      Vec2.div(this.aAvg, aLen, this.aAvg);

      this.bAvg.reset();
      final int bLen = b.length;
      for ( int i = 0; i < bLen; ++i ) {
         Vec2.add(this.bAvg, this.coords[b[i][0]], this.bAvg);
      }
      Vec2.div(this.bAvg, bLen, this.bAvg);

      return this.aAvg.compareTo(this.bAvg);
   }

   /**
    * Returns the simple name of this class.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.getClass().getSimpleName(); }

}
