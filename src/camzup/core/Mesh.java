package camzup.core;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * An abstract parent for mesh objects.
 */
public abstract class Mesh extends EntityData implements IMesh {

   /**
    * The faces array does not include face data itself, but rather indices to
    * other arrays which contain vertex data. It is a three-dimensional array
    * organized by
    * <ol>
    * <li>the number of faces;</li>
    * <li>the number of vertices per face;</li>
    * <li>the information per vertex;</li>
    * </ol>
    * The inner most array, information per face, may vary with each mesh; 3D
    * meshes, for example, include normals while 2D meshes don't.
    */
   public int[][][] faces;

   /**
    * The material associated with this mesh in a mesh entity.
    */
   public int materialIndex = 0;

   /**
    * The default constructor.
    */
   protected Mesh ( ) {}

   /**
    * Construct a mesh with an array of face indices.
    *
    * @param faces the face indices.
    */
   protected Mesh ( final int[][][] faces ) { this.faces = faces; }

   /**
    * Construct a mesh and give it a name.
    *
    * @param name the name
    */
   protected Mesh ( final String name ) { super(name); }

   /**
    * Construct a mesh with a name and an array of face indices.
    *
    * @param name  the name
    * @param faces the face indices.
    */
   protected Mesh ( final String name, final int[][][] faces ) {

      super(name);
      this.faces = faces;
   }

   /**
    * Cycles the array of indices in the faces array by a number of places.
    * The number of places can be positive or negative, indicating which
    * direction to shift the array: positive numbers shift to the right;
    * negative, to the left.
    *
    * @param places number of places
    *
    * @return this mesh
    */
   public Mesh cycleFaces ( final int places ) {

      final int len = this.faces.length;
      final int k = Utils.mod(places, len);
      Mesh.reverse(this.faces, 0, len - 1);
      Mesh.reverse(this.faces, 0, k - 1);
      Mesh.reverse(this.faces, k, len - 1);
      return this;
   }

   /**
    * Cycles the array of indices in a face which indicate vertex (and
    * therefore edge) order by a number of places. The number of places can be
    * positive or negative, indicating which direction to shift the array:
    * positive numbers shift to the right; negative, to the left.
    *
    * @param faceIndex the face index
    * @param places    number of places
    *
    * @return this mesh
    */
   public Mesh cycleVerts ( final int faceIndex, final int places ) {

      final int[][] arr = this.faces[Utils.mod(faceIndex, this.faces.length)];
      final int len = arr.length;
      final int k = Utils.mod(places, len);
      Mesh.reverse(arr, 0, len - 1);
      Mesh.reverse(arr, 0, k - 1);
      Mesh.reverse(arr, k, len - 1);
      return this;
   }

   /**
    * Tests this mesh for equivalence with an object.
    *
    * @param obj the object
    *
    * @return the equivalence
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( !super.equals(obj) ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      final Mesh other = ( Mesh ) obj;
      return Arrays.deepEquals(this.faces, other.faces);
   }

   /**
    * Gets this mesh's material index.
    *
    * @return the material index
    */
   public int getMaterialIndex ( ) { return this.materialIndex; }

   /**
    * Generates a hash code for this mesh.
    */
   @Override
   public int hashCode ( ) {

      int hash = super.hashCode();
      hash = hash * IUtils.HASH_MUL ^ Arrays.deepHashCode(this.faces);
      return hash;
   }

   /**
    * Flips the indices which specify an edge.
    *
    * @param i face index
    * @param j edge index
    *
    * @return this mesh
    */
   public Mesh reverseEdge ( final int i, final int j ) {

      final int[][] face = this.faces[Utils.mod(i, this.faces.length)];
      final int len = face.length;
      final int jOrigin = Utils.mod(j, len);
      final int jDest = ( jOrigin + 1 ) % len;

      final int[] temp = face[jOrigin];
      face[jOrigin] = face[jDest];
      face[jDest] = temp;

      return this;
   }

   /**
    * Flips the indices which specify a face. Changes the winding of a face
    * from counter-clockwise (CCW) to clockwise (CW) or vice versa.
    *
    * @param i face index
    *
    * @return this mesh
    */
   public Mesh reverseFace ( final int i ) {

      final int[][] face = this.faces[Utils.mod(i, this.faces.length)];
      Mesh.reverse(face);
      return this;
   }

   /**
    * Flips the indices which specify all faces. Changes the winding from
    * counter-clockwise (CCW) to clockwise (CW) or vice versa.
    *
    * @return this mesh
    */
   public Mesh reverseFaces ( ) {

      final int len = this.faces.length;
      for ( int i = 0; i < len; ++i ) { Mesh.reverse(this.faces[i]); }
      return this;
   }

   /**
    * Sets this mesh's material index.
    *
    * @param i the index
    *
    * @return this mesh
    */
   public Mesh setMaterialIndex ( final int i ) {

      this.materialIndex = i < 0 ? 0 : i;
      return this;
   }

   /**
    * Returns a string representation of the mesh.
    *
    * @return the string
    */
   @Override
   public String toString ( ) {

      final StringBuilder sb = new StringBuilder(64);
      sb.append("{ name: \"");
      sb.append(this.name);
      sb.append("\", materialIndex: ");
      sb.append(this.materialIndex);
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Triangulates all faces in a mesh drawing diagonals from the face's first
    * vertex to all non-adjacent vertices.
    *
    * @return this mesh
    *
    * @see Mesh#triangulate(int)
    */
   public Mesh triangulate ( ) {

      int k = 0;
      final int facesLen = this.faces.length;
      for ( int i = 0; i < facesLen; ++i ) {
         final int faceLen = this.faces[k].length;
         this.triangulate(k);
         k += faceLen - 2;
      }

      return this;
   }

   /**
    * Triangulates a convex face by drawing diagonals from its first vertex to
    * all non-adjacent vertices.
    *
    * @param faceIndex the face index
    *
    * @return this mesh
    */
   public Mesh triangulate ( final int faceIndex ) {

      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIndex, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      if ( faceLen < 4 ) { return this; }

      /*
       * Use first vertex to determine how much data is in each (i.e., whether
       * it is a 2D vertex or a 3D vertex).
       */
      final int[] vert0 = face[0];
      final int vertLen = vert0.length;
      final int lastNonAdj = faceLen - 2;

      final int[][][] fsNew = new int[lastNonAdj][3][vertLen];

      for ( int k = 0; k < lastNonAdj; ++k ) {

         final int[] vert1 = face[1 + k];
         final int[] vert2 = face[2 + k];
         final int[][] fNew = fsNew[k];

         for ( int m = 0; m < vertLen; ++m ) {
            fNew[0][m] = vert0[m];
            fNew[1][m] = vert1[m];
            fNew[2][m] = vert2[m];
         }
      }

      this.faces = Mesh.splice(this.faces, i, 1, fsNew);
      return this;
   }

   /**
    * The default sorter for 2 meshes.
    */
   protected static final Comparator < Vec2 > SORT_2 = new SortQuantized2();

   /**
    * The default sorter for 3D meshes.
    */
   protected static final Comparator < Vec3 > SORT_3 = new SortQuantized3();

   /**
    * Counts the occurrence of faces with a given number of vertices. Useful
    * for determining whether a face is composed entirely of triangles,
    * quadrilaterals, or is non-uniform. Returns a {@link Map}, where the
    * vertex count is the key and the tally is the value.<br>
    * <br>
    * Always the audit always places 3 and 4 into the map, even if neither
    * triangles nor quadrilaterals are present, because they are common
    * entries to check. When querying other possibilities use
    * {@link Map#getOrDefault(Object, Object)}, where the default argument is
    * 0.
    *
    * @param m the mesh
    *
    * @return the audit
    */
   public static Map < Integer, Integer > auditFaceType ( final Mesh m ) {

      final HashMap < Integer, Integer > audit = new HashMap <>(32, 0.75f);
      audit.put(3, 0);
      audit.put(4, 0);
      final int[][][] faces = m.faces;
      final int facesLen = faces.length;
      for ( int i = 0; i < facesLen; ++i ) {
         final Integer faceLen = faces[i].length;
         if ( audit.containsKey(faceLen) ) {
            audit.put(faceLen, audit.get(faceLen) + 1);
         } else {
            audit.put(faceLen, 1);
         }
      }
      return audit;
   }

   /**
    * Evaluates whether two edges are permutations of each other. This is
    * based on whether they refer to the same coordinates, but do so in a
    * different sequence.<br>
    * <br>
    * Because edge indices are assumed to be positive integers, a sum and
    * product algorithm is used.
    *
    * @param a    the left comparisand
    * @param aIdx the b edge index
    * @param b    the right comparisand
    * @param bIdx the b edge index
    *
    * @return the evaluation
    */
   @Experimental
   public static boolean edgesPermute ( final int[][] a, final int aIdx,
      final int[][] b, final int bIdx ) {

      final int aLen = a.length;
      final int aIdx0 = Utils.mod(aIdx, aLen);
      final int aIdx1 = ( aIdx0 + 1 ) % aLen;
      final int av0 = a[aIdx0][0];
      final int av1 = a[aIdx1][0];

      final int bLen = b.length;
      final int bIdx0 = Utils.mod(bIdx, bLen);
      final int bIdx1 = ( bIdx0 + 1 ) % bLen;
      final int bv0 = b[bIdx0][0];
      final int bv1 = b[bIdx1][0];

      int aMul = 1;
      final int aSum = av0 + av1;
      int aZeroes = 0;

      int bMul = 1;
      final int bSum = bv0 + bv1;
      int bZeroes = 0;

      /* @formatter:off */
      if ( av0 != 0 ) { aMul *= av0; } else { ++aZeroes; }
      if ( av1 != 0 ) { aMul *= av1; } else { ++aZeroes; }
      if ( bv0 != 0 ) { bMul *= bv0; } else { ++bZeroes; }
      if ( bv1 != 0 ) { bMul *= bv1; } else { ++bZeroes; }
      /* @formatter:on */

      return aMul == bMul && aSum == bSum && aZeroes == bZeroes;
   }

   /**
    * Evaluates whether two faces are permutations of each other. This is
    * based on whether they refer to the same coordinates, but do so in a
    * different sequence.<br>
    * <br>
    * Because face indices are assumed to be positive integers, a sum and
    * product algorithm is used.
    *
    * @param a the left comparisand
    * @param b the right comparisand
    *
    * @return the evaluation
    */
   @Experimental
   public static boolean facesPermute ( final int[][] a, final int[][] b ) {

      final int aLen = a.length;
      final int bLen = b.length;
      if ( aLen != bLen ) { return false; }

      int aMul = 1;
      int aSum = 0;
      int aZeroes = 0;

      int bMul = 1;
      int bSum = 0;
      int bZeroes = 0;

      /* @formatter:off */
      for ( int i = 0; i < aLen; ++i ) {
         final int av = a[i][0];
         final int bv = b[i][0];
         aSum += av;
         bSum += bv;
         if ( av != 0 ) { aMul *= av; } else { ++aZeroes; }
         if ( bv != 0 ) { bMul *= bv; } else { ++bZeroes; }
      }
      /* @formatter:on */

      return aMul == bMul && aSum == bSum && aZeroes == bZeroes;
   }

   /**
    * Evaluates whether all faces in a mesh are quadrilaterals.
    *
    * @param m the mesh
    *
    * @return the evaluation
    */
   public static boolean isQuads ( final Mesh m ) {

      return Mesh.uniformVertsPerFace(m, 4);
   }

   /**
    * Evaluates whether all faces in a mesh are triangles.
    *
    * @param m the mesh
    *
    * @return the evaluation
    */
   public static boolean isTriangles ( final Mesh m ) {

      return Mesh.uniformVertsPerFace(m, 3);
   }

   /**
    * Removes a number of elements from a 2D integer array at a given start
    * index. Returns a copy of the original array with the removal.
    *
    * @param arr       the array
    * @param index     the index
    * @param deletions the number of deletions
    *
    * @return the array
    */
   public static int[][] remove ( final int[][] arr, final int index,
      final int deletions ) {

      final int aLen = arr.length;
      final int valIdx = Utils.mod(index, aLen);
      final int valDel = Utils.clamp(deletions, 0, aLen - valIdx);
      final int bLen = aLen - valDel;
      final int[][] result = new int[bLen][];
      System.arraycopy(arr, 0, result, 0, valIdx);
      System.arraycopy(arr, valIdx + valDel, result, valIdx, bLen - valIdx);
      return result;
   }

   /**
    * Removes a number of elements from a 3D integer array at a given start
    * index. Returns a copy of the original array with the removal.
    *
    * @param arr       the array
    * @param index     the index
    * @param deletions the number of deletions
    *
    * @return the array
    */
   public static int[][][] remove ( final int[][][] arr, final int index,
      final int deletions ) {

      final int aLen = arr.length;
      final int valIdx = Utils.mod(index, aLen);
      final int valDel = Utils.clamp(deletions, 0, aLen - valIdx);
      final int bLen = aLen - valDel;
      final int[][][] result = new int[bLen][][];
      System.arraycopy(arr, 0, result, 0, valIdx);
      System.arraycopy(arr, valIdx + valDel, result, valIdx, bLen - valIdx);
      return result;
   }

   /**
    * Restructures a one-dimensional array of index data to a
    * three-dimensional array. Assumes that (1) all input faces contain the
    * same number of sides and therefore that the array's length is divisible
    * by its stride; and (2) the same index accesses the appropriate element
    * in arrays of uniform length, e.g. of coordinates, texture coordinates
    * and normals. The dimensions indicate how many arrays are accessed by the
    * index (typically 2 for 2D meshes, 3 for 3D meshes).<br>
    * <br>
    * Useful when converting from Unity meshes, wherein all faces are
    * triangles.
    *
    * @param indices    the indices array
    * @param stride     the stride
    * @param dimensions the number of data per vertex
    *
    * @return the indices
    */
   public static int[][][] segmentIndices ( final int[] indices,
      final int stride, final int dimensions ) {

      final int vstride = stride < 3 ? 3 : stride;
      final int vdim = dimensions < 1 ? 1 : dimensions;
      final int lenGrouped = indices.length / vstride;
      final int[][][] result = new int[lenGrouped][vstride][vdim];

      for ( int k = 0, i = 0; i < lenGrouped; ++i ) {
         final int[][] face = result[i];
         for ( int j = 0; j < vstride; ++j, ++k ) {
            final int idx = indices[k];
            final int[] vert = face[j];
            for ( int m = 0; m < vdim; ++m ) { vert[m] = idx; }
         }
      }
      return result;
   }

   /**
    * Splices a 2D array of integers into the midst of another and returns a
    * new array containing the splice. Does not mutate arrays in place. If the
    * number of deletions exceeds the length of the target array, then a copy
    * of the insert array is returned.
    *
    * @param arr       the array
    * @param index     the insertion point
    * @param deletions deletion count
    * @param insert    the insert
    *
    * @return a new, spliced array
    *
    * @see System#arraycopy(Object, int, Object, int, int)
    */
   public static int[][] splice ( final int[][] arr, final int index,
      final int deletions, final int[][] insert ) {

      final int aLen = arr.length;

      if ( deletions >= aLen ) {
         final int[][] result0 = new int[insert.length][];
         System.arraycopy(insert, 0, result0, 0, insert.length);
         return result0;
      }

      final int bLen = insert.length;
      final int valIdx = Utils.mod(index, aLen + 1);
      if ( deletions < 1 ) {
         final int[][] result1 = new int[aLen + bLen][];
         System.arraycopy(arr, 0, result1, 0, valIdx);
         System.arraycopy(insert, 0, result1, valIdx, bLen);
         System.arraycopy(arr, valIdx, result1, valIdx + bLen, aLen - valIdx);
         return result1;
      }

      final int idxOff = valIdx + deletions;
      final int[][] result2 = new int[aLen + bLen - deletions][];
      System.arraycopy(arr, 0, result2, 0, valIdx);
      System.arraycopy(insert, 0, result2, valIdx, bLen);
      System.arraycopy(arr, idxOff, result2, valIdx + bLen, aLen - idxOff);
      return result2;
   }

   /**
    * Splices a 3D array of integers into the midst of another and returns a
    * new array containing the splice. Does not mutate arrays in place. If the
    * number of deletions exceeds the length of the target array, then a copy
    * of the insert array is returned.
    *
    * @param arr       the array
    * @param index     the insertion point
    * @param deletions deletion count
    * @param insert    the insert
    *
    * @return the spliced array
    *
    * @see System#arraycopy(Object, int, Object, int, int)
    */
   public static int[][][] splice ( final int[][][] arr, final int index,
      final int deletions, final int[][][] insert ) {

      final int aLen = arr.length;

      if ( deletions >= aLen ) {
         final int[][][] result0 = new int[insert.length][][];
         System.arraycopy(insert, 0, result0, 0, insert.length);
         return result0;
      }

      final int bLen = insert.length;
      final int valIdx = Utils.mod(index, aLen + 1);
      if ( deletions < 1 ) {
         final int[][][] result1 = new int[aLen + bLen][][];
         System.arraycopy(arr, 0, result1, 0, valIdx);
         System.arraycopy(insert, 0, result1, valIdx, bLen);
         System.arraycopy(arr, valIdx, result1, valIdx + bLen, aLen - valIdx);
         return result1;
      }

      final int idxOff = valIdx + deletions;
      final int[][][] result2 = new int[aLen + bLen - deletions][][];
      System.arraycopy(arr, 0, result2, 0, valIdx);
      System.arraycopy(insert, 0, result2, valIdx, bLen);
      System.arraycopy(arr, idxOff, result2, valIdx + bLen, aLen - idxOff);
      return result2;
   }

   /**
    * Evaluates whether all faces in a mesh have a specified number of
    * vertices.
    *
    * @param m the mesh
    * @param c the number of vertices.
    *
    * @return the evaluation
    */
   public static boolean uniformVertsPerFace ( final Mesh m, final int c ) {

      final int[][][] faces = m.faces;
      final int facesLen = faces.length;
      for ( int i = 0; i < facesLen; ++i ) {
         if ( faces[i].length != c ) { return false; }
      }
      return true;
   }

   /**
    * Inserts a 2D array in the midst of another. For use by edge subdivision
    * functions.
    *
    * @param arr    the array
    * @param index  the insertion index
    * @param insert the inserted array
    *
    * @return the new array
    *
    * @see System#arraycopy(Object, int, Object, int, int)
    */
   protected static int[][] insert ( final int[][] arr, final int index,
      final int[][] insert ) {

      final int aLen = arr.length;
      final int bLen = insert.length;
      final int valIdx = Utils.mod(index, aLen + 1);
      final int[][] result = new int[aLen + bLen][];

      /*
       * (1.) Copy values from source array into result up to insert point. (2.)
       * Copy insertion into result array at the insertion point. (3.) Copy
       * portion of source array after the insertion point into the result at
       * the point after the length of the insertion.
       */
      System.arraycopy(arr, 0, result, 0, valIdx);
      System.arraycopy(insert, 0, result, valIdx, bLen);
      System.arraycopy(arr, valIdx, result, valIdx + bLen, aLen - valIdx);

      return result;
   }

   /**
    * Internal helper function to reverse an array of indices.
    *
    * @param arr   the array
    * @param start the start index
    *
    * @return the array
    */
   protected static int[][] reverse ( final int[][] arr ) {

      return Mesh.reverse(arr, 0, arr.length - 1);
   }

   /**
    * Internal helper function to reverse an array of indices.
    *
    * @param arr   the array
    * @param start the start index
    * @param end   the end index
    *
    * @return the array
    */
   protected static int[][] reverse ( final int[][] arr, final int start,
      final int end ) {

      int st = start;
      for ( int ed = end; st < ed; --ed ) {
         final int[] temp = arr[st];
         arr[st] = arr[ed];
         arr[ed] = temp;
         ++st;
      }
      return arr;
   }

   /**
    * Internal helper function to reverse an array of indices.
    *
    * @param arr   the array
    * @param start the start index
    *
    * @return the array
    */
   protected static int[][][] reverse ( final int[][][] arr ) {

      return Mesh.reverse(arr, 0, arr.length - 1);
   }

   /**
    * Internal helper function to reverse an array of indices.
    *
    * @param arr   the array
    * @param start the start index
    * @param end   the end index
    *
    * @return the array
    */
   protected static int[][][] reverse ( final int[][][] arr, final int start,
      final int end ) {

      int st = start;
      for ( int ed = end; st < ed; --ed ) {
         final int[][] temp = arr[st];
         arr[st] = arr[ed];
         arr[ed] = temp;
         ++st;
      }
      return arr;
   }

   /**
    * Compares two vectors by their quantized y component, then by their x
    * component.
    */
   public static final class SortQuantized2 implements Comparator < Vec2 > {

      /**
       * Quantization level.
       */
      protected final int levels;

      /**
       * Quantization level cast to a float.
       */
      protected final float levf;

      /**
       * Inverse of the quantization level.
       */
      protected final float levInv;

      /**
       * Internal vector to hold quantized left operand.
       */
      protected final Vec2 qa = new Vec2();

      /**
       * Internal vector to hold quantized right operand.
       */
      protected final Vec2 qb = new Vec2();

      /**
       * The default constructor.
       */
      public SortQuantized2 ( ) {

         this(( int ) ( 1.0f / IUtils.EPSILON ));
      }

      /**
       * Creates a quantized sorter with the specified number of levels.
       *
       * @param levels quantization levels
       */
      public SortQuantized2 ( final int levels ) {

         this.levels = levels < 2 ? 2 : levels;
         this.levf = this.levels;
         this.levInv = 1.0f / this.levf;
      }

      /**
       * Compares the quantized y and x components of the comparisand vectors.
       *
       * @param a the left comparisand
       * @param b the right comparisand
       *
       * @return the evaluation
       */
      @Override
      public int compare ( final Vec2 a, final Vec2 b ) {

         this.qa.set(this.levInv * Utils.floor(0.5f + a.x * this.levf),
            this.levInv * Utils.floor(0.5f + a.y * this.levf));

         this.qb.set(this.levInv * Utils.floor(0.5f + b.x * this.levf),
            this.levInv * Utils.floor(0.5f + b.y * this.levf));

         return this.qa.compareTo(this.qb);
      }

      /**
       * Get the quantization level.
       *
       * @return the level
       */
      public int getLevels ( ) { return this.levels; }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * Compares two vectors by their quantized z component, y component, then
    * by their quantized x component.
    */
   public static final class SortQuantized3 implements Comparator < Vec3 > {

      /**
       * Quantization level.
       */
      protected final int levels;

      /**
       * Quantization level cast to a float.
       */
      protected final float levf;

      /**
       * Inverse of the quantization level.
       */
      protected final float levInv;

      /**
       * Internal vector to hold quantized left operand.
       */
      protected final Vec3 qa = new Vec3();

      /**
       * Internal vector to hold quantized right operand.
       */
      protected final Vec3 qb = new Vec3();

      /**
       * The default constructor.
       */
      public SortQuantized3 ( ) {

         this(( int ) ( 1.0f / IUtils.EPSILON ));
      }

      /**
       * Creates a quantized sorter with the specified number of levels.
       *
       * @param levels quantization levels
       */
      public SortQuantized3 ( final int levels ) {

         this.levels = levels < 2 ? 2 : levels;
         this.levf = this.levels;
         this.levInv = 1.0f / this.levf;
      }

      /**
       * Compares the quantized z, y and x components of the comparisand
       * vectors.
       *
       * @param a the left comparisand
       * @param b the right comparisand
       *
       * @return the evaluation
       */
      @Override
      public int compare ( final Vec3 a, final Vec3 b ) {

         this.qa.set(this.levInv * Utils.floor(0.5f + a.x * this.levf),
            this.levInv * Utils.floor(0.5f + a.y * this.levf), this.levInv
               * Utils.floor(0.5f + a.z * this.levf));

         this.qb.set(this.levInv * Utils.floor(0.5f + b.x * this.levf),
            this.levInv * Utils.floor(0.5f + b.y * this.levf), this.levInv
               * Utils.floor(0.5f + b.z * this.levf));

         return this.qa.compareTo(this.qb);
      }

      /**
       * Get the quantization level.
       *
       * @return the level
       */
      public int getLevels ( ) { return this.levels; }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

}