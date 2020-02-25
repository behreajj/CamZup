package camzup.core;

import java.util.Comparator;

/**
 * An abstract parent for mesh objects.
 */
public abstract class Mesh extends EntityData implements IMesh {

   public static final class SortQuantized3 implements Comparator < Vec3 > {

      final private Vec3 qa = new Vec3();
      final private Vec3 qb = new Vec3();
      final public int   levels;

      public SortQuantized3 () {

         this((int) (1.0f / Utils.EPSILON));
      }

      public SortQuantized3 ( final int levels ) {

         this.levels = levels;
      }

      @Override
      public int compare ( final Vec3 a, final Vec3 b ) {

         Vec3.quantize(a, this.levels, this.qa);
         Vec3.quantize(b, this.levels, this.qb);
         return qa.z > qb.z ? 1
               : qa.z < qb.z ? -1
                     : qa.y > qb.y ? 1
                           : qa.y < qb.y ? -1
                                 : qa.x > qb.x ? 1
                                       : qa.x < qb.x ? -1 : 0;
      }
   }

   public static final class SortQuantized2 implements Comparator < Vec2 > {

      final private Vec2 qa = new Vec2();
      final private Vec2 qb = new Vec2();
      final public int   levels;

      public SortQuantized2 () {

         this((int) (1.0f / Utils.EPSILON));
      }

      public SortQuantized2 ( final int levels ) {

         this.levels = levels;
      }

      @Override
      public int compare ( final Vec2 a, final Vec2 b ) {

         Vec2.quantize(a, this.levels, this.qa);
         Vec2.quantize(b, this.levels, this.qb);
         return this.qa.y > this.qb.y ? 1
               : this.qa.y < this.qb.y ? -1
                     : this.qa.x > this.qb.x ? 1
                           : this.qa.x < this.qb.x ? -1 : 0;
      }
   }

   /**
    * Inserts a 2D array in the midst of another. For use by
    * subdivision functions.
    *
    * @param arr
    *           the array
    * @param index
    *           the insertion index
    * @param insert
    *           the inserted array
    * @return the new array
    * @see System#arraycopy(Object, int, Object, int, int)
    */
   protected static int[][] insert (
         final int[][] arr,
         final int index,
         final int[][] insert ) {

      final int alen = arr.length;
      final int blen = insert.length;
      final int valIdx = Utils.mod(index, alen + 1);

      final int[][] result = new int[alen + blen][];
      System.arraycopy(arr, 0, result, 0, valIdx);
      System.arraycopy(insert, 0, result, valIdx, blen);
      System.arraycopy(arr, valIdx, result, valIdx + blen, alen - valIdx);

      return result;
   }

   /**
    * The material associated with this mesh in a mesh entity.
    */
   public int materialIndex = 0;

   /**
    * The default constructor.
    */
   protected Mesh () {

      super();
   }

   /**
    * Construct a mesh and give it a name.
    *
    * @param name
    *           the name
    */
   protected Mesh ( final String name ) {

      super(name);
   }

   /**
    * Gets this mesh's material index.
    *
    * @return the material index
    */
   public int getMaterialIndex () {

      return this.materialIndex;
   }

   /**
    * Sets this mesh's material index.
    *
    * @param i
    *           the index
    * @return this mesh
    */
   @Chainable
   public Mesh setMaterialIndex ( final int i ) {

      this.materialIndex = i;
      return this;
   }
}