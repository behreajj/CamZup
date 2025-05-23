package com.behreajj.camzup.core;

import java.util.*;

/**
 * An abstract parent for mesh objects.
 */
public abstract class Mesh extends EntityData {

    /**
     * Default count of sectors in a regular convex polygon, to approximate a
     * circle.
     */
    public static final int DEFAULT_CIRCLE_SECTORS = 32;

    /**
     * Default oculus for rings, sqrt(2.0) / 4.0.
     */
    public static final float DEFAULT_OCULUS = 0.35355338f;

    /**
     * The default sorter for 2 meshes.
     */
    protected static final Comparator<Vec2> SORT_2 = new SortQuantized2();

    /**
     * The default sorter for 3D meshes.
     */
    protected static final Comparator<Vec3> SORT_3 = new SortQuantized3();

    /**
     * The faces array does not include face data itself, but rather indices to
     * other arrays which contain vertex data. It is a three-dimensional array
     * organized by
     *
     * <ol>
     * <li>the number of faces;</li>
     * <li>the number of vertices per face;</li>
     * <li>the information per vertex;</li>
     * </ol>
     * <p>
     * The innermost array, information per face, may vary with each mesh; 3D
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
    protected Mesh() {
    }

    /**
     * Construct a mesh with an array of face indices.
     *
     * @param faces the face indices.
     */
    protected Mesh(final int[][][] faces) {
        this.faces = faces;
    }

    /**
     * Construct a mesh and give it a name.
     *
     * @param name the name
     */
    protected Mesh(final String name) {
        super(name);
    }

    /**
     * Construct a mesh with a name and an array of face indices.
     *
     * @param name  the name
     * @param faces the face indices.
     */
    protected Mesh(final String name, final int[][][] faces) {

        super(name);
        this.faces = faces;
    }

    /**
     * Counts the occurrence of faces with a given number of vertices. Useful
     * for determining whether a face is composed entirely of triangles,
     * quadrilaterals, or is non-uniform.
     * <br>
     * <br>
     * Returns a {@link Map}, where the vertex count is the key and the tally
     * are the value.
     * <br>
     * <br>
     * The audit places 3 and 4 into the map, even if the mesh contains neither
     * triangles nor quadrilaterals, because they are common entries to query.
     * When querying other possibilities, check whether the map contains a key
     * prior to using its get method.
     *
     * @param m the mesh
     * @return the audit
     */
    public static Map<Integer, Integer> auditFaceType(final Mesh m) {

        final TreeMap<Integer, Integer> audit = new TreeMap<>();
        audit.put(3, 0);
        audit.put(4, 0);
        final int[][][] faces = m.faces;
        for (final int[][] face : faces) {
            final Integer faceLen = face.length;
            if (audit.containsKey(faceLen)) {
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
     * different sequence.
     * <br>
     * <br>
     * Because edge indices are assumed to be positive integers, a sum and
     * product algorithm is used.
     *
     * @param a    the left comparisand
     * @param aIdx the edge index
     * @param b    the right comparisand
     * @param bIdx the edge index
     * @return the evaluation
     */
    public static boolean edgesPermute(
        final int[][] a,
        final int aIdx,
        final int[][] b,
        final int bIdx) {

        final int aLen = a.length;
        final int aIdx0 = Utils.mod(aIdx, aLen);
        final int aIdx1 = (aIdx0 + 1) % aLen;
        final int av0 = a[aIdx0][0];
        final int av1 = a[aIdx1][0];

        final int bLen = b.length;
        final int bIdx0 = Utils.mod(bIdx, bLen);
        final int bIdx1 = (bIdx0 + 1) % bLen;
        final int bv0 = b[bIdx0][0];
        final int bv1 = b[bIdx1][0];

        int aMul = 1;
        final int aSum = av0 + av1;
        int aZeroes = 0;

        int bMul = 1;
        final int bSum = bv0 + bv1;
        int bZeroes = 0;

        if (av0 != 0) {
            aMul *= av0;
        } else {
            ++aZeroes;
        }
        if (av1 != 0) {
            aMul *= av1;
        } else {
            ++aZeroes;
        }
        if (bv0 != 0) {
            bMul *= bv0;
        } else {
            ++bZeroes;
        }
        if (bv1 != 0) {
            bMul *= bv1;
        } else {
            ++bZeroes;
        }

        return aMul == bMul && aSum == bSum && aZeroes == bZeroes;
    }

    /**
     * Evaluates whether two faces are permutations of each other. This is
     * based on whether they refer to the same coordinates, but do so in a
     * different sequence.
     * <br>
     * <br>
     * Because face indices are assumed to be positive integers, a sum and
     * product algorithm is used.
     *
     * @param a the left comparisand
     * @param b the right comparisand
     * @return the evaluation
     */
    public static boolean facesPermute(final int[][] a, final int[][] b) {

        final int aLen = a.length;
        final int bLen = b.length;
        if (aLen != bLen) {
            return false;
        }

        int aMul = 1;
        int aSum = 0;
        int aZeroes = 0;

        int bMul = 1;
        int bSum = 0;
        int bZeroes = 0;

        for (int i = 0; i < aLen; ++i) {
            final int av = a[i][0];
            final int bv = b[i][0];
            aSum += av;
            bSum += bv;
            if (av != 0) {
                aMul *= av;
            } else {
                ++aZeroes;
            }
            if (bv != 0) {
                bMul *= bv;
            } else {
                ++bZeroes;
            }
        }

        return aMul == bMul && aSum == bSum && aZeroes == bZeroes;
    }

    /**
     * Evaluates whether all faces in a mesh are quadrilaterals.
     *
     * @param m the mesh
     * @return the evaluation
     */
    public static boolean isQuads(final Mesh m) {

        return Mesh.uniformVertsPerFace(m, 4);
    }

    /**
     * Evaluates whether all faces in a mesh are triangles.
     *
     * @param m the mesh
     * @return the evaluation
     */
    public static boolean isTriangles(final Mesh m) {

        return Mesh.uniformVertsPerFace(m, 3);
    }

    /**
     * Removes a number of elements from a 2D integer array at a given start
     * index. Returns a copy of the original array with the removal.
     *
     * @param arr       the array
     * @param index     the index
     * @param deletions the number of deletions
     * @return the array
     */
    public static int[][] remove(
        final int[][] arr,
        final int index,
        final int deletions) {

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
     * @return the array
     */
    public static int[][][] remove(
        final int[][][] arr,
        final int index,
        final int deletions) {

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
     * Splices a 2D array of integers into the midst of another and returns a
     * new array containing the splice. Does not mutate arrays in place. If the
     * number of deletions exceeds the length of the target array, then a copy
     * of the insert array is returned.
     *
     * @param arr       the array
     * @param index     the insertion point
     * @param deletions deletion count
     * @param insert    the insert
     * @return a new, spliced array
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    public static int[][] splice(
        final int[][] arr,
        final int index,
        final int deletions,
        final int[][] insert) {

        final int aLen = arr.length;

        if (deletions >= aLen) {
            final int[][] result0 = new int[insert.length][];
            System.arraycopy(insert, 0, result0, 0, insert.length);
            return result0;
        }

        final int bLen = insert.length;
        final int valIdx = Utils.mod(index, aLen + 1);
        if (deletions < 1) {
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
     * @return the spliced array
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    public static int[][][] splice(
        final int[][][] arr,
        final int index,
        final int deletions,
        final int[][][] insert) {

        final int aLen = arr.length;

        if (deletions >= aLen) {
            final int[][][] result0 = new int[insert.length][][];
            System.arraycopy(insert, 0, result0, 0, insert.length);
            return result0;
        }

        final int bLen = insert.length;
        final int valIdx = Utils.mod(index, aLen + 1);
        if (deletions < 1) {
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
     * @return the evaluation
     */
    public static boolean uniformVertsPerFace(final Mesh m, final int c) {

        // QUERY: Could this be sped up by searching start-to-end and
        // end-to-start simultaneously? Would have to compensate for odd number
        // of faces.

        final int[][][] fs = m.faces;
        for (final int[][] f : fs) {
            if (f.length != c) {
                return false;
            }
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
     * @return the new array
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    protected static int[][] insert(
        final int[][] arr,
        final int index,
        final int[][] insert) {

        final int aLen = arr.length;
        final int bLen = insert.length;
        final int valIdx = Utils.mod(index, aLen + 1);
        final int[][] result = new int[aLen + bLen][];

        /*
         * (1.) Copy values from source array into result up to insert point.
         * (2.) Copy insertion into result array at the insertion point.
         * (3.) Copy portion of source array after the insertion point into the
         * result at the point after the length of the insertion.
         */
        System.arraycopy(arr, 0, result, 0, valIdx);
        System.arraycopy(insert, 0, result, valIdx, bLen);
        System.arraycopy(arr, valIdx, result, valIdx + bLen, aLen - valIdx);

        return result;
    }

    /**
     * Internal helper function to reverse an array of indices.
     *
     * @param arr the array
     * @return the array
     */
    protected static int[][] reverse(final int[][] arr) {

        return Mesh.reverse(arr, 0, arr.length - 1);
    }

    /**
     * Internal helper function to reverse an array of indices.
     *
     * @param arr   the array
     * @param start the start index
     * @param end   the end index
     * @return the array
     */
    protected static int[][] reverse(
        final int[][] arr,
        final int start,
        final int end) {

        for (int st = start, ed = end; st < ed; ++st, --ed) {
            final int[] temp = arr[st];
            arr[st] = arr[ed];
            arr[ed] = temp;
        }
        return arr;
    }

    /**
     * Internal helper function to reverse an array of indices.
     *
     * @param arr the array
     * @return the array
     */
    protected static int[][][] reverse(final int[][][] arr) {

        return Mesh.reverse(arr, 0, arr.length - 1);
    }

    /**
     * Internal helper function to reverse an array of indices.
     *
     * @param arr   the array
     * @param start the start index
     * @param end   the end index
     * @return the array
     */
    protected static int[][][] reverse(
        final int[][][] arr,
        final int start,
        final int end) {

        for (int st = start, ed = end; st < ed; ++st, --ed) {
            final int[][] temp = arr[st];
            arr[st] = arr[ed];
            arr[ed] = temp;
        }
        return arr;
    }

    /**
     * Cycles the array of indices in the faces array by a number of places.
     * The number of places can be positive or negative, indicating which
     * direction to shift the array: positive numbers shift to the right;
     * negative, to the left.
     *
     * @param places number of places
     * @return this mesh
     */
    public Mesh cycleFaces(final int places) {

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
     * @return this mesh
     */
    public Mesh cycleVerts(final int faceIndex, final int places) {

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
     * @return the equivalence
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || this.getClass() != obj.getClass()) {
            return false;
        }
        final Mesh other = (Mesh) obj;
        return Arrays.deepEquals(this.faces, other.faces);
    }

    /**
     * Gets this mesh's material index.
     *
     * @return the material index
     */
    public int getMaterialIndex() {
        return this.materialIndex;
    }

    /**
     * Sets this mesh's material index.
     *
     * @param i the index
     * @return this mesh
     */
    public Mesh setMaterialIndex(final int i) {

        this.materialIndex = Math.max(i, 0);
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(this.faces), this.materialIndex);
    }

    /**
     * Flips the indices which specify an edge.
     *
     * @param i face index
     * @param j edge index
     * @return this mesh
     */
    public Mesh reverseEdge(final int i, final int j) {

        final int[][] face = this.faces[Utils.mod(i, this.faces.length)];
        final int len = face.length;
        final int jOrigin = Utils.mod(j, len);
        final int jDest = (jOrigin + 1) % len;

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
     * @return this mesh
     */
    public Mesh reverseFace(final int i) {

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
    public Mesh reverseFaces() {

        for (final int[][] face : this.faces) {
            Mesh.reverse(face);
        }

        return this;
    }

    /**
     * Splits a face into two halves according to an origin and destination
     * vertex. The face must have at least 3 vertices. The destination index
     * should not equal the origin index or either of its neighbors. Returns a
     * boolean whether the method was successful.
     *
     * @param faceIndex the face index
     * @param origIndex the origin index
     * @param destIndex the destination index
     * @return operation success
     */
    public boolean sectionFace(
        final int faceIndex,
        final int origIndex,
        final int destIndex) {

        /* Find face. */
        final int facesLen = this.faces.length;
        final int i = Utils.mod(faceIndex, facesLen);
        final int[][] srcFace = this.faces[i];

        /* Find edges. */
        final int srcFaceLen = srcFace.length;
        if (srcFaceLen < 4) {
            return false;
        }
        int j = Utils.mod(origIndex, srcFaceLen);
        int k = Utils.mod(destIndex, srcFaceLen);

        /*
         * If the destination vertex equals the origin vertex, the origin's
         * previous neighbor, or its next neighbor, invalid selection.
         */
        if (j == k
            || (j + 1) % srcFaceLen == k
            || Utils.mod(j - 1, srcFaceLen) == k) {
            return false;
        }

        /* Maintain vertex winding. */
        if (j > k) {
            final int swap = j;
            j = k;
            k = swap;
        }

        /*
         * Find length of travel in indices.
         * Each new face adds an extra edge.
         */
        final int diff = k - j;

        /* Origin to destination. */
        final int trgFace0Len = 1 + diff;
        final int[][] trgFace0 = new int[trgFace0Len][];
        for (int n = 0; n < trgFace0Len; ++n) {
            final int[] srcVert = srcFace[(j + n) % srcFaceLen];
            final int srcLen = srcVert.length;
            trgFace0[n] = new int[srcLen];
            final int[] trgVert0 = trgFace0[n];
            System.arraycopy(srcVert, 0, trgVert0, 0, srcLen);
        }

        /* Destination to origin. */
        final int trgFace1Len = 1 + srcFaceLen - diff;
        final int[][] trgFace1 = new int[trgFace1Len][];
        for (int n = 0; n < trgFace1Len; ++n) {
            final int[] srcVert = srcFace[(k + n) % srcFaceLen];
            final int srcLen = srcVert.length;
            trgFace1[n] = new int[srcLen];
            final int[] trgVert1 = trgFace1[n];
            System.arraycopy(srcVert, 0, trgVert1, 0, srcLen);
        }

        this.faces = Mesh.splice(this.faces, i, 1,
            new int[][][]{trgFace0, trgFace1});
        return true;
    }

    /**
     * Returns a string representation of the mesh.
     *
     * @return the string
     */
    @Override
    public String toString() {

        return "{ name: \"" + this.name + "\", materialIndex: "
            + this.materialIndex + ' ' + '}';
    }

    /**
     * Triangulates all faces in a mesh by drawing diagonals from the face's
     * first vertex to all non-adjacent vertices.
     *
     * @return this mesh
     * @see Mesh#triangulate(int)
     */
    @SuppressWarnings("UnusedReturnValue")
    public Mesh triangulate() {

        int k = 0;
        final int facesLen = this.faces.length;
        for (int i = 0; i < facesLen; ++i) {
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
     * @return this mesh
     */
    public Mesh triangulate(final int faceIndex) {

        final int facesLen = this.faces.length;
        final int i = Utils.mod(faceIndex, facesLen);
        final int[][] face = this.faces[i];
        final int faceLen = face.length;

        if (faceLen < 4) {
            return this;
        }

        /*
         * Use first vertex to determine how much data is in each (i.e.,
         * whether it is a 2D vertex or a 3D vertex).
         */
        final int[] vert0 = face[0];
        final int vertLen = vert0.length;
        final int lastNonAdj = faceLen - 2;

        final int[][][] fsNew = new int[lastNonAdj][3][vertLen];

        for (int k = 0; k < lastNonAdj; ++k) {

            final int[] vert1 = face[1 + k];
            final int[] vert2 = face[2 + k];
            final int[][] fNew = fsNew[k];

            for (int m = 0; m < vertLen; ++m) {
                fNew[0][m] = vert0[m];
                fNew[1][m] = vert1[m];
                fNew[2][m] = vert2[m];
            }
        }

        this.faces = Mesh.splice(this.faces, i, 1, fsNew);
        return this;
    }

    /**
     * Compares two vectors by their quantized y component, then by their x
     * component.
     */
    public static final class SortQuantized2 implements Comparator<Vec2> {

        /**
         * Quantization level.
         */
        private final int levels;

        /**
         * Quantization level cast to a float.
         */
        private final float levf;

        /**
         * Inverse of the quantization level.
         */
        private final float levInv;

        /**
         * Internal vector to hold quantized left operand.
         */
        private final Vec2 qa = new Vec2();

        /**
         * Internal vector to hold quantized right operand.
         */
        private final Vec2 qb = new Vec2();

        /**
         * The default constructor.
         */
        public SortQuantized2() {

            this((int) (1.0f / Utils.EPSILON));
        }

        /**
         * Creates a quantized sorter with the specified number of levels.
         *
         * @param levels quantization levels
         */
        public SortQuantized2(final int levels) {

            this.levels = Math.max(levels, 2);
            this.levf = this.levels;
            this.levInv = 1.0f / this.levf;
        }

        /**
         * Compares the quantized y and x components of the comparisand vectors.
         *
         * @param a the left comparisand
         * @param b the right comparisand
         * @return the evaluation
         * @see Utils#floor(float)
         */
        @Override
        public int compare(final Vec2 a, final Vec2 b) {

            this.qa.set(
                this.levInv * Utils.floor(0.5f + a.x * this.levf),
                this.levInv * Utils.floor(0.5f + a.y * this.levf));

            this.qb.set(
                this.levInv * Utils.floor(0.5f + b.x * this.levf),
                this.levInv * Utils.floor(0.5f + b.y * this.levf));

            return this.qa.compareTo(this.qb);
        }

        /**
         * Get the quantization level.
         *
         * @return the level
         */
        public int getLevels() {
            return this.levels;
        }

        /**
         * Returns the simple name of this class.
         *
         * @return the string
         */
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    /**
     * Compares two vectors by their quantized z component, y component, then
     * by their quantized x component.
     */
    public static final class SortQuantized3 implements Comparator<Vec3> {

        /**
         * Quantization level.
         */
        private final int levels;

        /**
         * Quantization level cast to a float.
         */
        private final float levf;

        /**
         * Inverse of the quantization level.
         */
        private final float levInv;

        /**
         * Internal vector to hold quantized left operand.
         */
        private final Vec3 qa = new Vec3();

        /**
         * Internal vector to hold quantized right operand.
         */
        private final Vec3 qb = new Vec3();

        /**
         * The default constructor.
         */
        public SortQuantized3() {

            this((int) (1.0f / Utils.EPSILON));
        }

        /**
         * Creates a quantized sorter with the specified number of levels.
         *
         * @param levels quantization levels
         */
        public SortQuantized3(final int levels) {

            this.levels = Math.max(levels, 2);
            this.levf = this.levels;
            this.levInv = 1.0f / this.levf;
        }

        /**
         * Compares the quantized z, y and x components of the comparisand
         * vectors.
         *
         * @param a the left comparisand
         * @param b the right comparisand
         * @return the evaluation
         * @see Utils#floor(float)
         */
        @Override
        public int compare(final Vec3 a, final Vec3 b) {

            this.qa.set(
                this.levInv * Utils.floor(0.5f + a.x * this.levf),
                this.levInv * Utils.floor(0.5f + a.y * this.levf),
                this.levInv * Utils.floor(0.5f + a.z * this.levf));

            this.qb.set(
                this.levInv * Utils.floor(0.5f + b.x * this.levf),
                this.levInv * Utils.floor(0.5f + b.y * this.levf),
                this.levInv * Utils.floor(0.5f + b.z * this.levf));

            return this.qa.compareTo(this.qb);
        }

        /**
         * Get the quantization level.
         *
         * @return the level
         */
        public int getLevels() {
            return this.levels;
        }

        /**
         * Returns the simple name of this class.
         *
         * @return the string
         */
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
