package camzup.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import camzup.core.Mesh2.PolyType;

/**
 * Organizes data needed to draw a three dimensional shape using
 * vertices and faces. Given that a mesh is primarily a collection of
 * references, it is initialized with null arrays (coordinates,
 * texture coordinates and indices). These are not final, and so can
 * be reassigned.
 */
public class Mesh3 extends Mesh {

  /**
   * Compares two face indices (an array of vertex indices) by averaging
   * the vectors referenced by them, then comparing the averages.
   */
  protected static final class SortIndices3 implements Comparator < int[][] > {

    /**
     * Internal vector used to store the average coordinate for the left
     * comparisand.
     */
    protected final Vec3 aAvg = new Vec3();

    /**
     * Internal vector used to store the average coordinate for the right
     * comparisand.
     */
    protected final Vec3 bAvg = new Vec3();

    /**
     * The coordinates array.
     */
    final Vec3[] coords;

    /**
     * The default constructor.
     *
     * @param coords the coordinate array.
     */
    protected SortIndices3 ( final Vec3[] coords ) {

      this.coords = coords;
    }

    /**
     * Compares two faces indices.
     *
     * @param a the left comparisand
     * @param b the right comparisandS
     */
    @Override
    public int compare ( final int[][] a, final int[][] b ) {

      this.aAvg.reset();
      final int aLen = a.length;
      for (int i = 0; i < aLen; ++i) {
        Vec3.add(
            this.aAvg,
            this.coords[a[i][0]],
            this.aAvg);
      }
      Vec3.div(this.aAvg, aLen, this.aAvg);

      this.bAvg.reset();
      final int bLen = b.length;
      for (int i = 0; i < bLen; ++i) {
        Vec3.add(
            this.bAvg,
            this.coords[b[i][0]],
            this.bAvg);
      }
      Vec3.div(this.bAvg, bLen, this.bAvg);

      return this.aAvg.compareTo(this.bAvg);
    }

  }

  /**
   * An array of coordinates in the mesh.
   */
  public Vec3[] coords;

  /**
   * An array of normals to indicate how light will bounce off the
   * mesh's surface.
   */
  public Vec3[] normals;

  /**
   * The texture (UV) coordinates that describe how an image is mapped
   * onto the geometry of the mesh. Typically in the range [0.0, 1.0].
   */
  public Vec2[] texCoords;

  /**
   * The default constructor.
   */
  public Mesh3 ( ) {

    super();
  }

  /**
   * Creates a mesh from arrays of faces, coordinates, texture
   * coordinates and normals. The mesh's arrays are set by reference,
   * not by value.
   *
   * @param faces     the faces array
   * @param coords    the coordinates array
   * @param texCoords the texture coordinates array
   * @param normals   the normals array
   */
  public Mesh3 (
      final int[][][] faces,
      final Vec3[] coords,
      final Vec2[] texCoords,
      final Vec3[] normals ) {

    super();
    this.set(faces, coords, texCoords, normals);
  }

  /**
   * Constructs a copy of the source mesh.
   *
   * @param source the source mesh
   */
  public Mesh3 ( final Mesh3 source ) {

    super();
    this.set(source);
  }

  /**
   * Creates a named mesh.
   *
   * @param name the mesh name
   */
  public Mesh3 ( final String name ) {

    super(name);
  }

  /**
   * Creates a named mesh from arrays of faces, coordinates, texture
   * coordinates and normals. The mesh's arrays are set by reference,
   * not by value.
   *
   * @param name      the mesh name
   * @param faces     the faces array
   * @param coords    the coordinates array
   * @param texCoords the texture coordinates array
   * @param normals   the normals array
   */
  public Mesh3 (
      final String name,
      final int[][][] faces,
      final Vec3[] coords,
      final Vec2[] texCoords,
      final Vec3[] normals ) {

    super(name);
    this.set(faces, coords, texCoords, normals);
  }

  /**
   * Tests this mesh for equivalence with another.
   *
   * @param mesh3 the mesh
   * @return the evaluation
   */
  protected boolean equals ( final Mesh3 mesh3 ) {

    if (!Arrays.equals(this.coords, mesh3.coords)) { return false; }

    if (!Arrays.deepEquals(this.faces, mesh3.faces)) { return false; }

    return true;
  }

  /**
   * Returns a String of Python code targeted toward the Blender 2.8x
   * API. This code is brittle and is used for internal testing
   * purposes, i.e., to compare how mesh geometry looks in Blender (the
   * control) versus in the library (the test).
   *
   * @return the string
   */
  @Experimental
  String toBlenderCode ( ) {

    final StringBuilder result = new StringBuilder();
    result.append("{\"name\": \"")
        .append(this.name)
        .append("\", \"material_index\": ")
        .append(this.materialIndex)
        .append(", \"vertices\": [");

    final int vlen = this.coords.length;
    final int vlast = vlen - 1;
    for (int i = 0; i < vlen; ++i) {
      result.append(this.coords[i].toBlenderCode());
      if (i < vlast) { result.append(',').append(' '); }
    }

    result.append("], \"faces\": [");

    final int flen = this.faces.length;
    final int flast = flen - 1;
    for (int j = 0; j < flen; ++j) {
      final int[][] vrtInd = this.faces[j];
      final int vrtIndLen = vrtInd.length;
      final int vrtLast = vrtIndLen - 1;

      result.append('(');
      for (int k = 0; k < vrtIndLen; ++k) {
        result.append(vrtInd[k][0]);
        if (k < vrtLast) { result.append(',').append(' '); }
      }
      result.append(')');

      if (j < flast) { result.append(',').append(' '); }
    }

    result.append("], \"normals\": [");

    final int nlen = this.normals.length;
    final int nlast = nlen - 1;
    for (int h = 0; h < nlen; ++h) {
      result.append(this.normals[h].toBlenderCode());
      if (h < nlast) { result.append(',').append(' '); }
    }

    result.append(']').append('}');
    return result.toString();
  }

  /**
   * Attempts to recalculate the normals of this mesh per vertex. If the
   * normals array is null, or if its length is not equal to the length
   * of coordinates, the normals array is reallocated.
   *
   * @return this mesh
   */
  @Experimental
  @Chainable
  public Mesh3 calcNormals ( ) {

    this.normals = Vec3.resize(this.normals, this.coords.length);

    Vec3 prev = null;
    Vec3 curr = null;
    Vec3 next = null;
    Vec3 normal = null;

    final Vec3 edge0 = new Vec3();
    final Vec3 edge1 = new Vec3();

    final int len0 = this.faces.length;
    for (int i = 0; i < len0; ++i) {

      final int[][] faceIndices = this.faces[i];
      final int len1 = faceIndices.length;
      prev = this.coords[faceIndices[len1 - 1][0]];

      for (int j = 0, k = 1; j < len1; ++j, ++k) {

        final int[] faceIndex = faceIndices[j];
        final int currIndex = faceIndex[0];
        final int nextIndex = faceIndices[k % len1][0];

        /* Acquire normal and update face index reference to it. */
        normal = this.normals[currIndex];
        faceIndex[2] = currIndex;

        curr = this.coords[currIndex];
        next = this.coords[nextIndex];

        Vec3.sub(curr, prev, edge0);
        Vec3.sub(next, curr, edge1);
        Vec3.crossNorm(edge0, edge1, normal);

        prev = curr;
      }
    }

    return this;
  }

  /**
   * Clones this mesh.
   *
   * @return the cloned mesh
   */
  @Override
  public Mesh3 clone ( ) {

    return new Mesh3(this);
  }

  /**
   * Tests this mesh for equivalence with an object.
   *
   * @param obj the object
   * @return the evaluation
   */
  @Override
  public boolean equals ( final Object obj ) {

    if (this == obj) { return true; }

    if (!super.equals(obj)) { return false; }

    if (this.getClass() != obj.getClass()) { return false; }

    return this.equals((Mesh3) obj);
  }

  /**
   * Gets an edge from the mesh.
   *
   * @param i      the face index
   * @param j      the vertex index
   * @param target the output edge
   * @return the edge
   */
  public Edge3 getEdge (
      final int i,
      final int j,
      final Edge3 target ) {

    final int[][] f0 = this.faces[Math.floorMod(
        i, this.faces.length)];
    final int f0len = f0.length;
    final int[] f1 = f0[Math.floorMod(j, f0len)];
    final int[] f2 = f0[Math.floorMod(j + 1, f0len)];

    return target.set(
        this.coords[f1[0]],
        this.texCoords[f1[1]],
        this.normals[f1[2]],

        this.coords[f2[0]],
        this.texCoords[f2[1]],
        this.normals[f2[2]]);
  }

  /**
   * Gets an array of edges from the mesh.
   *
   * @return the edges array
   */
  public Edge3[] getEdges ( ) {

    final ArrayList < Edge3 > result = new ArrayList <>();
    Edge3 trial = new Edge3();
    final int len0 = this.faces.length;

    for (int i = 0; i < len0; ++i) {

      final int[][] fs = this.faces[i];
      final int len1 = fs.length;

      for (int j = 0; j < len1; ++j) {

        final int[] fo = fs[j];
        final int[] fd = fs[(j + 1) % len1];

        trial.set(
            this.coords[fo[0]],
            this.texCoords[fo[1]],
            this.normals[fo[2]],

            this.coords[fd[0]],
            this.texCoords[fd[1]],
            this.normals[fd[2]]);

        if (!result.contains(trial)) {
          result.add(trial);
          trial = new Edge3();
        }
      }
    }

    return result.toArray(new Edge3[result.size()]);
  }

  /**
   * Gets a face from the mesh.
   *
   * @param i      the index
   * @param target the output face
   * @return the face
   */
  public Face3 getFace (
      final int i,
      final Face3 target ) {

    final int[][] face = this.faces[Math.floorMod(i, this.faces.length)];
    final int len = face.length;
    final Vert3[] vertices = new Vert3[len];

    for (int j = 0; j < len; ++j) {
      final int[] vert = face[j];
      vertices[j] = new Vert3(
          this.coords[vert[0]],
          this.texCoords[vert[1]],
          this.normals[vert[2]]);
    }

    return target.set(vertices);
  }

  /**
   * Gets an array of faces from the mesh.
   *
   * @return the faces array
   */
  public Face3[] getFaces ( ) {

    final int len0 = this.faces.length;
    final Face3[] result = new Face3[len0];

    for (int i = 0; i < len0; ++i) {

      final int[][] fs0 = this.faces[i];
      final int len1 = fs0.length;
      final Vert3[] verts = new Vert3[len1];

      for (int j = 0; j < len1; ++j) {

        final int[] fs1 = fs0[j];
        verts[j] = new Vert3(
            this.coords[fs1[0]],
            this.texCoords[fs1[1]],
            this.normals[fs1[2]]);
      }

      result[i] = new Face3(verts);
    }

    return result;
  }

  /**
   * Get a vertex from the mesh.
   *
   * @param i      primary index
   * @param j      secondary index
   * @param target the output vertex
   * @return the vertex
   */
  public Vert3 getVertex (
      final int i,
      final int j,
      final Vert3 target ) {

    final int[][] f0 = this.faces[Math.floorMod(
        i, this.faces.length)];
    final int[] f = f0[Math.floorMod(
        j, f0.length)];

    return target.set(
        this.coords[f[0]],
        this.texCoords[f[1]],
        this.normals[f[2]]);
  }

  /**
   * Get an array of vertices from the mesh.
   *
   * @return the vertices
   */
  public Vert3[] getVertices ( ) {

    final ArrayList < Vert3 > result = new ArrayList <>();
    Vert3 trial = new Vert3();
    final int len0 = this.faces.length;

    for (int i = 0; i < len0; ++i) {

      final int[][] fs = this.faces[i];
      final int len1 = fs.length;

      for (int j = 0; j < len1; ++j) {

        final int[] f = fs[j];
        trial.set(
            this.coords[f[0]],
            this.texCoords[f[1]],
            this.normals[f[2]]);

        if (!result.contains(trial)) {
          result.add(trial);
          trial = new Vert3();
        }
      }
    }

    return result.toArray(new Vert3[result.size()]);
  }

  /**
   * Returns a hash code for this mesh based on its coordinates and its
   * face indices.
   *
   * @return the hash code
   */
  @Override
  public int hashCode ( ) {

    int hash = IUtils.HASH_BASE;
    hash = hash * IUtils.HASH_MUL ^ Arrays.hashCode(this.coords);
    hash = hash * IUtils.HASH_MUL ^ Arrays.deepHashCode(this.faces);
    return hash;
  }

  /**
   * Centers the mesh about the origin, (0.0, 0.0) and re-scales it to
   * the range [-0.5, 0.5]. Parallel to p5.Geometry's normalize method.
   *
   * @return this mesh
   * @see Mesh3#calcDimensions(Mesh3, Vec3, Vec3, Vec3)
   */
  @Chainable
  public Mesh3 reframe ( ) {

    final Vec3 dim = new Vec3();
    final Vec3 lb = new Vec3();
    final Vec3 ub = new Vec3();
    Mesh3.calcDimensions(this, dim, lb, ub);

    lb.x = -0.5f * (lb.x + ub.x);
    lb.y = -0.5f * (lb.y + ub.y);
    lb.z = -0.5f * (lb.z + ub.z);
    final float scl = Utils.div(1.0f,
        Utils.max(dim.x, dim.y, dim.z));

    Vec3 c;
    final int len = this.coords.length;
    for (int i = 0; i < len; ++i) {
      c = this.coords[i];
      Vec3.add(c, lb, c);
      Vec3.mul(c, scl, c);
    }

    return this;
  }

  /**
   * Flips the indices which specify an edge.
   *
   * @param i face index
   * @param j edge index
   * @return this mesh
   */
  @Chainable
  public Mesh3 reverseEdge (
      final int i,
      final int j ) {

    final int[][] face = this.faces[Math.floorMod(i, this.faces.length)];
    final int len = face.length;
    final int jOrigin = Math.floorMod(j, len);
    final int jDest = Math.floorMod(j + 1, len);
    final int[] temp = face[jOrigin];
    face[jOrigin] = face[jDest];
    face[jDest] = temp;
    return this;
  }

  /**
   * Flips the indices which specify a face.
   *
   * @param i face index
   * @return this mesh
   */
  @Chainable
  public Mesh3 reverseFace ( final int i ) {

    final int[][] face = this.faces[Math.floorMod(i, this.faces.length)];
    final int len = face.length;
    final int halfLen = len >> 1;
    for (int j = 0; j < halfLen; ++j) {
      final int reverse = len - j - 1;
      final int[] temp = face[j];
      face[j] = face[reverse];
      face[reverse] = temp;
    }
    return this;
  }

  /**
   * Rotates all coordinates in the mesh by an angle around an arbitrary
   * axis.
   *
   * @param radians the angle in radians
   * @param axis    the axis of rotation
   * @return this mesh
   * @see Vec3#rotate(Vec3, float, Vec3, Vec3)
   */
  @Chainable
  public Mesh3 rotate (
      final float radians,
      final Vec3 axis ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    Vec3 c;
    final int len0 = this.coords.length;
    for (int i = 0; i < len0; ++i) {
      c = this.coords[i];
      Vec3.rotate(c, cosa, sina, axis, c);
    }

    return this;
  }

  /**
   * Rotates all coordinates in the mesh by a quaternion.
   *
   * @param q the quaternion
   * @return the mesh
   */
  @Chainable
  public Mesh3 rotate ( final Quaternion q ) {

    Vec3 c;
    final int len0 = this.coords.length;
    for (int i = 0; i < len0; ++i) {
      c = this.coords[i];
      Quaternion.mulVector(q, c, c);
    }

    return this;
  }

  /**
   * Rotates all coordinates in the mesh by an angle around the x axis.
   *
   * @param radians the angle in radians
   * @return this mesh
   * @see Vec3#rotateX(Vec3, float, Vec3)
   */
  @Chainable
  public Mesh3 rotateX ( final float radians ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    Vec3 c;
    final int len0 = this.coords.length;
    for (int i = 0; i < len0; ++i) {
      c = this.coords[i];
      Vec3.rotateX(c, cosa, sina, c);
    }

    return this;
  }

  /**
   * Rotates all coordinates in the mesh by an angle around the y axis.
   *
   * @param radians the angle in radians
   * @return this mesh
   * @see Vec3#rotateY(Vec3, float, Vec3)
   */
  @Chainable
  public Mesh3 rotateY ( final float radians ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    Vec3 c;
    final int len0 = this.coords.length;
    for (int i = 0; i < len0; ++i) {
      c = this.coords[i];
      Vec3.rotateY(c, cosa, sina, c);
    }

    return this;
  }

  /**
   * Rotates all coordinates in the mesh by an angle around the z axis.
   *
   * @param radians the angle in radians
   * @return this mesh
   * @see Vec3#rotateZ(Vec3, float, Vec3)
   */
  @Chainable
  public Mesh3 rotateZ ( final float radians ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    Vec3 c;
    final int len0 = this.coords.length;
    for (int i = 0; i < len0; ++i) {
      c = this.coords[i];
      Vec3.rotateZ(c, cosa, sina, c);
    }

    return this;
  }

  /**
   * Scales all coordinates in the mesh by a scalar.
   *
   * @param scale the scalar
   * @return this mesh
   * @see Vec3#mul(Vec3, float, Vec3)
   */
  @Chainable
  public Mesh3 scale ( final float scale ) {

    Vec3 c;
    final int len = this.coords.length;
    for (int i = 0; i < len; ++i) {
      c = this.coords[i];
      Vec3.mul(c, scale, c);
    }

    return this;
  }

  /**
   * Scales all coordinates in the mesh by a vector.
   *
   * @param scale the vector
   * @return this mesh
   * @see Vec3#mul(Vec3, Vec3, Vec3)
   */
  @Chainable
  public Mesh3 scale ( final Vec3 scale ) {

    Vec3 c;
    final int len = this.coords.length;
    for (int i = 0; i < len; ++i) {
      c = this.coords[i];
      Vec3.mul(c, scale, c);
    }

    return this;
  }

  /**
   * Sets the mesh's data by reference, not by value.
   *
   * @param faces     the faces array
   * @param coords    the coordinates array
   * @param texCoords the texture coordinates array
   * @param normals   the normals array
   * @return this mesh
   */
  @Chainable
  public Mesh3 set (
      final int[][][] faces,
      final Vec3[] coords,
      final Vec2[] texCoords,
      final Vec3[] normals ) {

    this.faces = faces;
    this.coords = coords;
    this.texCoords = texCoords;
    this.normals = normals;
    return this;
  }

  /**
   * Sets this mesh to a copy of the source. Allocates new arrays for
   * coordinates, texture coordinates, normals and faces.
   *
   * @param source the source mesh
   * @return this mesh
   */
  @Chainable
  public Mesh3 set ( final Mesh3 source ) {

    /*
     * This should not use Vec3#resize, as it is copying all vectors.
     */

    /* Copy coordinates. */
    final Vec3[] sourcevs = source.coords;
    final int vslen = sourcevs.length;
    this.coords = new Vec3[vslen];
    for (int i = 0; i < vslen; ++i) {
      this.coords[i] = new Vec3(sourcevs[i]);
    }

    /* Copy texture coordinates. */
    final Vec2[] sourcevts = source.texCoords;
    final int vtslen = sourcevts.length;
    this.texCoords = new Vec2[vtslen];
    for (int j = 0; j < vtslen; ++j) {
      this.texCoords[j] = new Vec2(sourcevts[j]);
    }

    /* Copy normals. */
    final Vec3[] sourcevns = source.normals;
    final int vnslen = sourcevns.length;
    this.normals = new Vec3[vnslen];
    for (int k = 0; k < vnslen; ++k) {
      this.normals[k] = new Vec3(sourcevns[k]);
    }

    /* Copy faces. */
    final int[][][] sourcefs = source.faces;
    final int fslen0 = sourcefs.length;
    this.faces = new int[fslen0][][];

    for (int i = 0; i < fslen0; ++i) {

      final int[][] source1 = sourcefs[i];
      final int fslen1 = source1.length;
      final int[][] target1 = new int[fslen1][];
      this.faces[i] = target1;

      for (int j = 0; j < fslen1; ++j) {

        final int[] source2 = source1[j];
        final int fslen2 = source2.length;
        final int[] target2 = new int[fslen2];
        target1[j] = target2;

        for (int k = 0; k < fslen2; ++k) {
          target2[k] = source2[k];
        }
      }
    }

    this.materialIndex = source.materialIndex;
    this.name = source.name;
    return this;
  }

  /**
   * Subdivides an edge by the number of cuts given. For example, one
   * cut will divide an edge in half; two cuts, into thirds.<br>
   * <br>
   * Does not distinguish between interior edges, which have a
   * complement elsewhere, and border edges; for that reason this works
   * best with NGONs.
   *
   * @param faceIndex the face index
   * @param edgeIndex the edge index
   * @param cuts      number of cuts
   * @return this mesh
   */
  @Experimental
  @Chainable
  public Mesh3 subdivEdge (
      final int faceIndex,
      final int edgeIndex,
      final int cuts ) {

    if (cuts < 1) { return this; }

    /* Validate face index, find face. */
    final int facesLen = this.faces.length;
    final int i = Math.floorMod(faceIndex, facesLen);
    final int[][] face = this.faces[i];
    final int faceLen = face.length;

    /* Find edge origin vertex. */
    final int j0 = Math.floorMod(edgeIndex, faceLen);
    final int[] vert0Idx = face[j0];
    final Vec3 vOrigin = this.coords[vert0Idx[0]];
    final Vec2 vtOrigin = this.texCoords[vert0Idx[1]];
    final Vec3 vnOrigin = this.normals[vert0Idx[2]];

    /* Find edge destination vertex. */
    final int j1 = Math.floorMod(edgeIndex + 1, faceLen);
    final int[] vert1Idx = face[j1];
    final Vec3 vDest = this.coords[vert1Idx[0]];
    final Vec2 vtDest = this.texCoords[vert1Idx[1]];
    final Vec3 vnDest = this.normals[vert1Idx[2]];

    /*
     * Cache old length of coordinates and texture coordinates so new ones
     * can be appended to the end.
     */
    final int vsOldLen = this.coords.length;
    final int vtsOldLen = this.texCoords.length;
    final int vnsOldLen = this.normals.length;

    /* Create arrays to hold new data. */
    final Vec3[] vsNew = new Vec3[cuts];
    final Vec2[] vtsNew = new Vec2[cuts];
    final Vec3[] vnsNew = new Vec3[cuts];
    final int[][] fsNew = new int[cuts][3];

    /*
     * Subdivide the edge. The edge origin and destination are to be
     * excluded from the new set, so the conversion to the step accounts
     * for this.
     */
    final float toStep = 1.0f / (cuts + 1.0f);
    for (int k = 0; k < cuts; ++k) {
      final float step = toStep + k * toStep;
      final float u = 1.0f - step;

      final Vec3 v = new Vec3();
      final Vec2 vt = new Vec2();
      final Vec3 vn = new Vec3();

      v.set(
          u * vOrigin.x + step * vDest.x,
          u * vOrigin.y + step * vDest.y,
          u * vOrigin.z + step * vDest.z);

      vt.set(
          u * vtOrigin.x + step * vtDest.x,
          u * vtOrigin.y + step * vtDest.y);

      vn.set(
          u * vnOrigin.x + step * vnDest.x,
          u * vnOrigin.y + step * vnDest.y,
          u * vnOrigin.z + step * vnDest.z);
      Vec3.normalize(vn, vn);

      vsNew[k] = v;
      vtsNew[k] = vt;
      vnsNew[k] = vn;

      final int[] newf = fsNew[k];
      newf[0] = vsOldLen + k;
      newf[1] = vtsOldLen + k;
      newf[2] = vnsOldLen + k;
    }

    /*
     * Append new coords and tex coords to the end of their respective
     * arrays. The new faces need to be inserted to this.faces[idx], not
     * reassigned to local face array.
     */
    this.coords = Vec3.concat(this.coords, vsNew);
    this.texCoords = Vec2.concat(this.texCoords, vtsNew);
    this.normals = Vec3.concat(this.normals, vnsNew);
    this.faces[i] = Mesh.insert(face, j1, fsNew);

    return this;
  }

  /**
   * Subdivides all edges in a mesh by the number of cuts given. For
   * example, one cut will divide an edge in half; two cuts, into
   * thirds.<br>
   * <br>
   * Does not distinguish between interior edges, which have a
   * complement elsewhere, and border edges; for that reason this works
   * best with NGONs.
   *
   * @param cuts number of cuts
   * @return this mesh
   */
  @Experimental
  @Chainable
  public Mesh3 subdivEdges ( final int cuts ) {

    final int len0 = this.faces.length;
    for (int i = 0; i < len0; ++i) {
      final int len1 = this.faces[i].length;
      for (int j = 0, k = 0; j < len1; ++j, k += cuts) {
        this.subdivEdge(i, k + j, cuts);
      }
    }
    return this;
  }

  /**
   * Subdivides all edges in a face by the number of cuts given. For
   * example, one cut will divide an edge in half; two cuts, into
   * thirds.<br>
   * <br>
   * Does not distinguish between interior edges, which have a
   * complement elsewhere, and border edges; for that reason this works
   * best with NGONs.
   *
   * @param faceIndex the face index
   * @param cuts      number of cuts
   * @return this mesh
   */
  @Experimental
  @Chainable
  public Mesh3 subdivEdges (
      final int faceIndex,
      final int cuts ) {

    final int faceLen = this.faces[Math.floorMod(faceIndex,
        this.faces.length)].length;
    for (int j = 0, k = 0; j < faceLen; ++j, k += cuts) {
      this.subdivEdge(faceIndex, k + j, cuts);
    }

    return this;
  }

  /**
   * Renders the mesh as a string following the Wavefront OBJ file
   * format.
   *
   * @return the string
   */
  public String toObjString ( ) {

    final int coordsLen = this.coords.length;
    final int texCoordsLen = this.texCoords.length;
    final int normalsLen = this.normals.length;
    final int facesLen = this.faces.length;
    final StringBuilder result = new StringBuilder();

    /*
     * Append a comment listing the number of coordinates, texture
     * coordinates, normals and faces.
     */
    result.append("# v: ").append(coordsLen)
        .append(", vt: ").append(texCoordsLen)
        .append(", vn: ").append(normalsLen)
        .append(", f: ").append(facesLen)
        .append('\n').append('\n');

    result.append('o').append(' ').append(this.name)
        .append('\n').append('\n');

    /* Write coordinates. */
    for (final Vec3 coord : this.coords) {
      result.append('v').append(' ')
          .append(coord.toObjString())
          .append('\n');
    }
    result.append('\n');

    /* Write texture coordinates. */
    for (final Vec2 texCoord : this.texCoords) {
      result.append("vt ")
          .append(texCoord.toObjString())
          .append('\n');
    }
    result.append('\n');

    /* Write normals. */
    for (final Vec3 normal : this.normals) {
      result.append("vn ")
          .append(normal.toObjString())
          .append('\n');
    }
    result.append('\n');

    for (int i = 0; i < facesLen; ++i) {

      final int[][] face = this.faces[i];
      final int vLen = face.length;
      result.append('f').append(' ');

      for (int j = 0; j < vLen; ++j) {

        /* Indices in an .obj file start at 1, not 0. */
        final int[] vert = face[j];
        result.append(vert[0] + 1)
            .append('/')
            .append(vert[1] + 1)
            .append('/')
            .append(vert[2] + 1);
      }

      result.append('\n');
    }

    result.append('\n');
    return result.toString();
  }

  /**
   * Centers the mesh about the origin, (0.0, 0.0), by calculating its
   * dimensions then subtracting the center point.
   *
   * @return this mesh
   * @see Mesh3#calcDimensions(Mesh3, Vec3, Vec3, Vec3)
   * @see Mesh3#translate(Vec3)
   */
  @Chainable
  public Mesh3 toOrigin ( ) {

    final Vec3 lb = new Vec3();
    final Vec3 ub = new Vec3();
    Mesh3.calcDimensions(this, new Vec3(), lb, ub);

    lb.x = -0.5f * (lb.x + ub.x);
    lb.y = -0.5f * (lb.y + ub.y);
    lb.z = -0.5f * (lb.z + ub.z);
    this.translate(lb);

    return this;
  }

  /**
   * Returns a string representation of the mesh.
   *
   * @return the string
   */
  @Override
  public String toString ( ) {

    return this.toString(4, Integer.MAX_VALUE);
  }

  /**
   * Returns a string representation of the mesh.
   *
   * @param places the number of places
   * @return the string
   */
  public String toString ( final int places ) {

    return this.toString(places, Integer.MAX_VALUE);
  }

  /**
   * Returns a string representation of the mesh. Includes an option to
   * truncate the listing in case of large meshes.
   *
   * @param places   the number of places
   * @param truncate truncate elements in a list
   * @return the string
   */
  public String toString (
      final int places,
      final int truncate ) {

    final StringBuilder sb = new StringBuilder();

    sb.append("{ name: \"")
        .append(this.name)
        .append('\"')
        .append(',')
        .append(' ')
        .append('\n')
        .append("coords: [ ");

    if (this.coords != null) {
      sb.append('\n');
      final int len = Math.min(this.coords.length, truncate);
      final int last = len - 1;
      for (int i = 0; i < len; ++i) {
        sb.append(this.coords[i].toString(places));
        if (i < last) {
          sb.append(',').append(' ');
          sb.append('\n');
        }
      }

      if (this.coords.length > truncate) { sb.append("\n/* ... */"); }
    }

    sb.append(" ],\ntexCoords: [");
    if (this.texCoords != null) {
      sb.append('\n');
      final int len = Math.min(this.texCoords.length, truncate);
      final int last = len - 1;
      for (int i = 0; i < len; ++i) {
        sb.append(this.texCoords[i].toString(places));
        if (i < last) {
          sb.append(',').append(' ');
          sb.append('\n');
        }
      }

      if (this.texCoords.length > truncate) { sb.append("\n/* ... */"); }
    }

    sb.append(" ],\nnormals: [");
    if (this.normals != null) {
      sb.append('\n');
      final int len = Math.min(this.normals.length, truncate);
      final int last = len - 1;
      for (int i = 0; i < len; ++i) {
        sb.append(this.normals[i].toString(places));
        if (i < last) {
          sb.append(',').append(' ');
          sb.append('\n');
        }
      }

      if (this.normals.length > truncate) { sb.append("\n/* ... */"); }
    }

    sb.append(" ],\nfaces: [");
    if (this.faces != null) {
      sb.append('\n');
      final int facesLen = Math.min(this.faces.length, truncate);
      final int facesLast = facesLen - 1;

      for (int i = 0; i < facesLen; ++i) {

        final int[][] verts = this.faces[i];
        final int vertsLen = verts.length;
        final int vertsLast = vertsLen - 1;
        sb.append('[').append(' ');

        for (int j = 0; j < vertsLen; ++j) {

          final int[] vert = verts[j];
          final int infoLen = vert.length;
          final int infoLast = infoLen - 1;
          sb.append('[').append(' ');

          /*
           * 3 indices: coordinate, texture coordinate and normal.
           */
          for (int k = 0; k < infoLen; ++k) {

            sb.append(vert[k]);
            if (k < infoLast) { sb.append(',').append(' '); }
          }
          sb.append(' ').append(']');
          if (j < vertsLast) { sb.append(',').append(' '); }
        }
        sb.append(' ').append(']');
        if (i < facesLast) {
          sb.append(',').append(' ');
          sb.append('\n');
        }
      }

      if (this.faces.length > truncate) { sb.append("\n/* ... */"); }
    }

    sb.append(" ] }");
    return sb.toString();
  }

  /**
   * Translates all coordinates in the mesh by a vector.
   *
   * @param v the vector
   * @return this mesh
   * @see Vec3#add(Vec3, Vec3, Vec3)
   */
  @Chainable
  public Mesh3 translate ( final Vec3 v ) {

    Vec3 c;
    final int len = this.coords.length;
    for (int i = 0; i < len; ++i) {
      c = this.coords[i];
      Vec3.add(c, v, c);
    }
    return this;
  }

  /**
   * A helper function for parsing an OBJ file. Attempts to convert a
   * string to an integer.
   *
   * @param i the string
   * @return the integer
   */
  private static int intFromStr ( final String i ) {

    int target = 0;
    try {
      target = Integer.parseInt(i);
    } catch (final NumberFormatException e) {
      target = 0;
    }
    return target;
  }

  @Experimental
  static Mesh3 torus (
      final float thickness,
      final int sectors,
      final int panels,
      final Mesh3 target ) {

    // TODO: Creates a seam. Needs to switch to using modulo in
    // faces, and to use one last longitude.

    // TODO: Redo concept of thickness / tube ratio...

    final int vsect = sectors < 3 ? 3 : sectors;
    final int vpanl = panels < 3 ? 3 : panels;

    final int panels1 = vpanl + 1;
    final int sectors1 = vsect + 1;
    final int len = panels1 * sectors1;

    final Vec3[] coords = new Vec3[len];
    final Vec2[] texCoords = new Vec2[len];
    final Vec3[] normals = new Vec3[len];

    final float toU = 1.0f / vsect;
    final float toV = 1.0f / vpanl;

    final float toTheta = 1.0f / vsect;
    final float toPhi = 1.0f / vpanl;

    final float vtrad = 0.5f * Utils.max(Utils.EPSILON, thickness);
    final float ratio = vtrad + vtrad;

    for (int k = 0, i = 0; i < panels1; ++i) {

      final float v = i * toV;
      final float phi = i * toPhi;
      final float cosPhi = Utils.scNorm(phi);
      final float sinPhi = Utils.scNorm(phi - 0.25f);

      final float r = 1.0f + ratio * cosPhi;

      for (int j = 0; j < sectors1; ++j, ++k) {

        final float u = j * toU;
        final float theta = j * toTheta;
        final float cosTheta = Utils.scNorm(theta);
        final float sinTheta = Utils.scNorm(theta - 0.25f);

        coords[k] = new Vec3(
            r * cosTheta,
            r * sinTheta,
            ratio * sinPhi);

        texCoords[k] = new Vec2(u, v);

        normals[k] = new Vec3(
            cosPhi * cosTheta,
            cosPhi * sinTheta,
            sinPhi);
      }
    }

    final int[][][] faces = new int[2 * vsect * vpanl][3][3];
    final int sliceCount = vsect + 1;
    int e = 0;
    int f = sliceCount;
    for (int k = 0, i = 0; i < vpanl; ++i) {
      for (int j = 0; j < vsect; ++j, k += 2) {
        final int a = e + j;
        final int b = a + 1;
        final int d = f + j;
        final int c = d + 1;

        faces[k] = new int[][] {
            { a, a, a }, { b, b, b }, { d, d, d } };

        faces[k + 1] = new int[][] {
            { d, d, d }, { b, b, b }, { c, c, c } };
      }

      e += sliceCount;
      f += sliceCount;
    }

    target.name = "Torus";
    return target.set(faces, coords, texCoords, normals);
  }

  /**
   * Calculates the dimensions of an Axis-Aligned Bounding Box (AABB)
   * encompassing the mesh.
   *
   * @param mesh   the mesh
   * @param target the output dimensions
   * @param lb     the lower bound
   * @param ub     the upper bound
   * @return the dimensions
   */
  public static Vec3 calcDimensions (
      final Mesh3 mesh,
      final Vec3 target,
      final Vec3 lb,
      final Vec3 ub ) {

    lb.set(
        Float.MAX_VALUE,
        Float.MAX_VALUE,
        Float.MAX_VALUE);
    ub.set(
        Float.MIN_VALUE,
        Float.MIN_VALUE,
        Float.MIN_VALUE);

    final Vec3[] coords = mesh.coords;
    final int len = coords.length;

    for (int i = 0; i < len; ++i) {

      final Vec3 coord = coords[i];
      final float x = coord.x;
      final float y = coord.y;
      final float z = coord.z;

      /* Minimum, max need separate if checks, not if-else. */
      if (x < lb.x) { lb.x = x; }

      if (x > ub.x) { ub.x = x; }

      if (y < lb.y) { lb.y = y; }

      if (y > ub.y) { ub.y = y; }

      if (z < lb.z) { lb.z = z; }

      if (z > ub.z) { ub.z = z; }
    }

    return Vec3.sub(ub, lb, target);
  }

  /**
   * Creates a regular convex polygon, approximating a circle.
   *
   * @param target the output mesh
   * @return the polygon
   * @see Mesh2#polygon(int, PolyType, Mesh2)
   */
  public static Mesh3 circle ( final Mesh3 target ) {

    return Mesh3.polygon(IMesh.DEFAULT_CIRCLE_SECTORS, target);
  }

  /**
   * Generates a cube mesh. In the context of Platonic solids, also
   * known as a hexahedron, as it has 6 faces and 8 vertices.
   *
   * @param target the output mesh
   * @return the cube
   */
  public static Mesh3 cube ( final Mesh3 target ) {

    target.name = "Cube";

    target.coords = Vec3.resize(target.coords, 8);
    target.coords[0].set(-0.5f, -0.5f, -0.5f);
    target.coords[1].set(-0.5f, -0.5f, 0.5f);
    target.coords[2].set(-0.5f, 0.5f, -0.5f);
    target.coords[3].set(-0.5f, 0.5f, 0.5f);
    target.coords[4].set(0.5f, -0.5f, -0.5f);
    target.coords[5].set(0.5f, -0.5f, 0.5f);
    target.coords[6].set(0.5f, 0.5f, -0.5f);
    target.coords[7].set(0.5f, 0.5f, 0.5f);

    target.texCoords = Vec2.resize(target.texCoords, 4);
    target.texCoords[0].set(0.0f, 0.0f);
    target.texCoords[1].set(0.0f, 1.0f);
    target.texCoords[2].set(1.0f, 1.0f);
    target.texCoords[3].set(1.0f, 0.0f);

    target.normals = Vec3.resize(target.normals, 6);
    target.normals[0].set(1.0f, 0.0f, 0.0f);
    target.normals[1].set(0.0f, 0.0f, 1.0f);
    target.normals[2].set(0.0f, 0.0f, -1.0f);
    target.normals[3].set(0.0f, -1.0f, 0.0f);
    target.normals[4].set(-1.0f, 0.0f, 0.0f);
    target.normals[5].set(0.0f, 1.0f, 0.0f);

    target.faces = new int[][][] {
        { { 0, 2, 4 }, { 1, 3, 4 }, { 3, 0, 4 }, { 2, 1, 4 } },
        { { 2, 2, 5 }, { 3, 3, 5 }, { 7, 0, 5 }, { 6, 1, 5 } },
        { { 6, 2, 0 }, { 7, 3, 0 }, { 5, 0, 0 }, { 4, 1, 0 } },
        { { 4, 2, 3 }, { 5, 3, 3 }, { 1, 0, 3 }, { 0, 1, 3 } },
        { { 2, 1, 2 }, { 6, 2, 2 }, { 4, 3, 2 }, { 0, 0, 2 } },
        { { 7, 1, 1 }, { 3, 2, 1 }, { 1, 3, 1 }, { 5, 0, 1 } }
    };

    return target;
  }

  /**
   * Creates an dodecahedron, a Platonic solid with 12 faces and 20
   * coordinates.
   *
   * @param target the output mesh
   * @return the dodecahedron
   */
  public static final Mesh3 dodecahedron ( final Mesh3 target ) {

    // TODO: Refactor to use Vec3 resize

    /*
     * double r = 0.5d; double phi = (1.0d + Math.sqrt(5.0d)) / 2.0d;
     * double b = r * (1.0d / phi); double c = r * (2.0d - phi);
     * 0.3090169943749474, 0.19098300562505255
     */

    final Vec3[] coords = new Vec3[] {
        /* 00 */ new Vec3(0.0f, 0.33614415f, -0.4165113f),
        /* 01 */ new Vec3(-0.19098301f, 0.47552827f, 0.15450847f),
        /* 02 */ new Vec3(0.19098301f, 0.47552827f, 0.15450847f),
        /* 03 */ new Vec3(0.309017f, 0.19840115f, 0.38938415f),
        /* 04 */ new Vec3(-0.309017f, 0.19840115f, 0.38938415f),
        /* 05 */ new Vec3(-0.19098301f, -0.47552827f, -0.15450847f),
        /* 06 */ new Vec3(-0.309017f, -0.38938415f, 0.19840115f),
        /* 07 */ new Vec3(0.19098301f, -0.47552827f, -0.15450847f),
        /* 08 */ new Vec3(0.309017f, -0.19840115f, -0.38938415f),
        /* 09 */ new Vec3(0.0f, -0.02712715f, -0.53454524f),
        /* 10 */ new Vec3(0.309017f, 0.38938415f, -0.19840115f),
        /* 11 */ new Vec3(0.5f, 0.05901699f, -0.18163565f),
        /* 12 */ new Vec3(-0.309017f, -0.19840115f, -0.38938415f),
        /* 13 */ new Vec3(-0.5f, 0.05901699f, -0.18163565f),
        /* 14 */ new Vec3(-0.309017f, 0.38938415f, -0.19840115f),
        /* 15 */ new Vec3(0.0f, 0.02712715f, 0.53454524f),
        /* 16 */ new Vec3(0.0f, -0.33614415f, 0.4165113f),
        /* 17 */ new Vec3(0.309017f, -0.38938415f, 0.19840115f),
        /* 18 */ new Vec3(0.5f, -0.05901699f, 0.18163565f),
        /* 19 */ new Vec3(-0.5f, -0.05901699f, 0.18163565f)
    };

    final Vec3[] normals = new Vec3[] {
        /* 00 */ new Vec3(-0.8506509f, 0.5f, 0.16245982f),
        /* 01 */ new Vec3(0.0f, -0.97147685f, 0.23713444f),
        /* 02 */ new Vec3(0.0f, 0.97147685f, -0.23713444f),
        /* 03 */ new Vec3(0.0f, -0.64655715f, -0.76286548f),
        /* 04 */ new Vec3(0.52573115f, 0.26286551f, -0.809017f),
        /* 05 */ new Vec3(0.0f, 0.64655715f, 0.76286548f),
        /* 06 */ new Vec3(-0.52573121f, 0.26286554f, -0.809017f),
        /* 07 */ new Vec3(-0.52573121f, -0.26286554f, 0.809017f),
        /* 08 */ new Vec3(0.52573115f, -0.26286551f, 0.809017f),
        /* 09 */ new Vec3(0.85065079f, 0.5f, 0.16245979f),
        /* 10 */ new Vec3(0.85065079f, -0.5f, -0.16245979f),
        /* 11 */ new Vec3(-0.8506509f, -0.5f, -0.16245982f)
    };

    final Vec2[] texCoords = new Vec2[] {
        /* 0 */ new Vec2(0.5f, 0.0f),
        /* 1 */ new Vec2(0.79389268f, 0.90450847f),
        /* 2 */ new Vec2(0.02447176f, 0.34549153f),
        /* 3 */ new Vec2(0.20610738f, 0.90450853f),
        /* 4 */ new Vec2(0.97552824f, 0.34549141f)
    };

    final int[][][] faces = new int[][][] {
        { { 2, 0, 2 }, { 10, 2, 2 }, { 0, 3, 2 },
            { 14, 1, 2 }, { 1, 4, 2 } },
        { { 1, 0, 5 }, { 4, 2, 5 }, { 15, 3, 5 },
            { 3, 1, 5 }, { 2, 4, 5 } },
        { { 7, 0, 1 }, { 17, 2, 1 }, { 16, 3, 1 },
            { 6, 1, 1 }, { 5, 4, 1 } },
        { { 5, 0, 3 }, { 12, 2, 3 }, { 9, 3, 3 },
            { 8, 1, 3 }, { 7, 4, 3 } },
        { { 9, 0, 4 }, { 0, 2, 4 }, { 10, 3, 4 },
            { 11, 1, 4 }, { 8, 4, 4 } },
        { { 0, 0, 6 }, { 9, 2, 6 }, { 12, 3, 6 },
            { 13, 1, 6 }, { 14, 4, 6 } },
        { { 16, 0, 7 }, { 15, 2, 7 }, { 4, 3, 7 },
            { 19, 1, 7 }, { 6, 4, 7 } },
        { { 15, 0, 8 }, { 16, 2, 8 }, { 17, 3, 8 },
            { 18, 1, 8 }, { 3, 4, 8 } },
        { { 11, 0, 9 }, { 10, 2, 9 }, { 2, 3, 9 },
            { 3, 1, 9 }, { 18, 4, 9 } },
        { { 18, 0, 10 }, { 17, 2, 10 }, { 7, 3, 10 },
            { 8, 1, 10 }, { 11, 4, 10 } },
        { { 13, 0, 11 }, { 12, 2, 11 }, { 5, 3, 11 },
            { 6, 1, 11 }, { 19, 4, 11 } },
        { { 19, 0, 0 }, { 4, 2, 0 }, { 1, 3, 0 },
            { 14, 1, 0 }, { 13, 4, 0 } }
    };

    target.name = "Dodecahedron";
    return target.set(faces, coords, texCoords, normals);
  }

  /**
   * Creates a mesh from an array of strings representing a WaveFront
   * obj file. This is a simple obj parser. It assumes that the face
   * indices of the mesh include texture coordinates and normals.
   * Material information is not parsed from the file, as Processing
   * would not accurately recreate it.
   *
   * @param lines  the String tokens
   * @param target the output mesh
   * @return the mesh
   */
  public static Mesh3 fromObj (
      final String[] lines,
      final Mesh3 target ) {

    String[] tokens;
    String[] facetokens;

    final ArrayList < Vec3 > coordList = new ArrayList <>();
    final ArrayList < Vec2 > texCoordList = new ArrayList <>();
    final ArrayList < Vec3 > normalList = new ArrayList <>();
    final ArrayList < int[][] > faceList = new ArrayList <>();
    String name = target.hashIdentityString();

    final int len = lines.length;
    for (int i = 0; i < len; ++i) {

      /* Split line by spaces. */
      tokens = lines[i].split("\\s+");

      /* Skip empty lines. */
      if (tokens.length > 0) {
        final String initialToken = tokens[0].toLowerCase();
        if (initialToken.equals("o")) {

          /* Assign name. */
          name = tokens[1];

        } else if (initialToken.equals("v")) {

          /* Coordinate. */
          coordList.add(new Vec3(tokens[1], tokens[2], tokens[3]));

        } else if (initialToken.equals("vt")) {

          /* Texture coordinate. */
          texCoordList.add(new Vec2(tokens[1], tokens[2]));

        } else if (initialToken.equals("vn")) {

          /* Normal. */
          normalList.add(new Vec3(tokens[1], tokens[2], tokens[3]));

        } else if (initialToken.equals("f")) {

          /* Face. */
          final int count = tokens.length;

          /* tokens length includes "f", and so is 1 longer. */
          final int[][] indices = new int[count - 1][3];

          /*
           * Simplified. Assumes (incorrectly) that face will always be
           * formatted as "v/vt/vn".
           */
          for (int j = 1; j < count; ++j) {
            facetokens = tokens[j].split("/");

            final int k = j - 1;

            /* Indices in .obj file start at 1, not 0. */
            indices[k][0] = Mesh3.intFromStr(facetokens[0]) - 1;
            indices[k][1] = Mesh3.intFromStr(facetokens[1]) - 1;
            indices[k][2] = Mesh3.intFromStr(facetokens[2]) - 1;
          }

          faceList.add(indices);
        }
      }
    }

    /* Convert to fixed-sized array. */
    target.name = name;
    target.set(
        faceList.toArray(new int[faceList.size()][][]),
        coordList.toArray(new Vec3[coordList.size()]),
        texCoordList.toArray(new Vec2[texCoordList.size()]),
        normalList.toArray(new Vec3[normalList.size()]));
    return target;
  }

  /**
   * Creates an icosahedron, a Platonic solid with 20 faces and 12
   * coordinates.
   *
   * @param target the output mesh
   * @return the icosahedron
   */
  public static final Mesh3 icosahedron ( final Mesh3 target ) {

    target.name = "Icosahedron";

    target.coords = Vec3.resize(target.coords, 12);
    target.coords[0].set(0.0f, 0.5f, 0.0f);
    target.coords[1].set(0.42532045f, 0.22360753f, -0.13819239f);
    target.coords[2].set(0.00000225f, 0.22360753f, -0.44720721f);
    target.coords[3].set(-0.42532432f, 0.22360751f, -0.13819626f);
    target.coords[4].set(-0.26286f, 0.2236075f, 0.36179957f);
    target.coords[5].set(0.26286399f, 0.22360750f, 0.36179706f);
    target.coords[6].set(0.26286f, -0.22360747f, -0.36179957f);
    target.coords[7].set(-0.26286399f, -0.22360747f, -0.36179706f);
    target.coords[8].set(-0.42532045f, -0.2236075f, 0.13819239f);
    target.coords[9].set(-0.00000225f, -0.2236075f, 0.44720721f);
    target.coords[10].set(0.42532432f, -0.22360748f, 0.13819624f);
    target.coords[11].set(0.0f, -0.5f, 0.0f);

    target.texCoords = Vec2.resize(target.texCoords, 3);
    target.texCoords[0].set(0.5f, 0.0f);
    target.texCoords[1].set(1.0f, 1.0f);
    target.texCoords[2].set(0.0f, 1.0f);

    target.normals = Vec3.resize(target.normals, 20);
    target.normals[0].set(-0.00000468f, -0.18758917f, -0.98224759f);
    target.normals[1].set(-0.93417150f, -0.18759677f, -0.30353081f);
    target.normals[2].set(-0.57735437f, -0.18758921f, 0.79465222f);
    target.normals[3].set(0.57734251f, -0.18758698f, 0.79466146f);
    target.normals[4].set(0.57735109f, -0.79465222f, -0.18759950f);
    target.normals[5].set(-0.57735270f, -0.79465240f, -0.18759337f);
    target.normals[6].set(0.35682696f, 0.79465121f, -0.49112532f);
    target.normals[7].set(-0.35681859f, 0.79465222f, -0.49112958f);
    target.normals[8].set(-0.57735109f, 0.79465222f, 0.18759951f);
    target.normals[9].set(0.57735270f, 0.79465240f, 0.18759339f);
    target.normals[10].set(0.00000289f, 0.79465103f, 0.60706645f);
    target.normals[11].set(0.93417150f, 0.18759677f, 0.30353081f);
    target.normals[12].set(0.57735437f, 0.18758918f, -0.79465222f);
    target.normals[13].set(-0.57734245f, 0.18758696f, -0.79466146f);
    target.normals[14].set(-0.93417013f, 0.18758696f, 0.30354115f);
    target.normals[15].set(0.00000468f, 0.18758917f, 0.98224759f);
    target.normals[16].set(-0.00000289f, -0.79465103f, -0.60706645f);
    target.normals[17].set(0.93417013f, -0.18758696f, -0.30354115f);
    target.normals[18].set(-0.35682702f, -0.79465109f, 0.49112538f);
    target.normals[19].set(0.35681871f, -0.79465216f, 0.49112964f);

    target.faces = new int[][][] {
        { { 0, 2, 6 }, { 1, 1, 6 }, { 2, 0, 6 } },
        { { 1, 2, 9 }, { 0, 1, 9 }, { 5, 0, 9 } },
        { { 0, 2, 7 }, { 2, 1, 7 }, { 3, 0, 7 } },
        { { 0, 2, 8 }, { 3, 1, 8 }, { 4, 0, 8 } },
        { { 0, 2, 10 }, { 4, 1, 10 }, { 5, 0, 10 } },
        { { 1, 2, 11 }, { 5, 1, 11 }, { 10, 0, 11 } },
        { { 2, 2, 12 }, { 1, 1, 12 }, { 6, 0, 12 } },
        { { 3, 2, 13 }, { 2, 1, 13 }, { 7, 0, 13 } },
        { { 4, 2, 14 }, { 3, 1, 14 }, { 8, 0, 14 } },
        { { 5, 2, 15 }, { 4, 1, 15 }, { 9, 0, 15 } },
        { { 1, 2, 17 }, { 10, 1, 17 }, { 6, 0, 17 } },
        { { 2, 2, 0 }, { 6, 1, 0 }, { 7, 0, 0 } },
        { { 3, 2, 1 }, { 7, 1, 1 }, { 8, 0, 1 } },
        { { 4, 2, 2 }, { 8, 1, 2 }, { 9, 0, 2 } },
        { { 5, 2, 3 }, { 9, 1, 3 }, { 10, 0, 3 } },
        { { 6, 2, 4 }, { 10, 1, 4 }, { 11, 0, 4 } },
        { { 7, 2, 16 }, { 6, 1, 16 }, { 11, 0, 16 } },
        { { 8, 2, 5 }, { 7, 1, 5 }, { 11, 0, 5 } },
        { { 9, 2, 18 }, { 8, 1, 18 }, { 11, 0, 18 } },
        { { 10, 2, 19 }, { 9, 1, 19 }, { 11, 0, 19 } }
    };

    return target;
  }

  /**
   * Creates an octahedron, a Platonic solid with 8 faces and 6
   * coordinates.
   *
   * @param target the output mesh
   * @return the octahedron
   */
  public static final Mesh3 octahedron ( final Mesh3 target ) {

    target.name = "Octahedron";

    target.coords = Vec3.resize(target.coords, 6);
    target.coords[0].set(0.0f, -0.5f, 0.0f);
    target.coords[1].set(0.5f, 0.0f, 0.0f);
    target.coords[2].set(-0.5f, 0.0f, 0.0f);
    target.coords[3].set(0.0f, 0.5f, 0.0f);
    target.coords[4].set(0.0f, 0.0f, 0.5f);
    target.coords[5].set(0.0f, 0.0f, -0.5f);

    target.texCoords = Vec2.resize(target.texCoords, 3);
    target.texCoords[0].set(0.5f, 0.0f);
    target.texCoords[1].set(1.0f, 1.0f);
    target.texCoords[2].set(0.0f, 1.0f);

    target.normals = Vec3.resize(target.normals, 8);
    target.normals[0].set(0.57735026f, -0.57735026f, 0.57735026f);
    target.normals[1].set(-0.57735026f, 0.57735026f, 0.57735026f);
    target.normals[2].set(-0.57735026f, -0.57735026f, 0.57735026f);
    target.normals[3].set(0.57735026f, 0.57735026f, 0.57735026f);
    target.normals[4].set(-0.57735026f, 0.57735026f, -0.57735026f);
    target.normals[5].set(0.57735026f, 0.57735026f, -0.57735026f);
    target.normals[6].set(0.57735026f, -0.57735026f, -0.57735026f);
    target.normals[7].set(-0.57735026f, -0.57735026f, -0.57735026f);

    target.faces = new int[][][] {
        { { 0, 2, 0 }, { 1, 1, 0 }, { 4, 0, 0 } },
        { { 1, 2, 3 }, { 3, 1, 3 }, { 4, 0, 3 } },
        { { 3, 2, 1 }, { 2, 1, 1 }, { 4, 0, 1 } },
        { { 2, 2, 2 }, { 0, 1, 2 }, { 4, 0, 2 } },
        { { 2, 2, 4 }, { 3, 1, 4 }, { 5, 0, 4 } },
        { { 3, 2, 5 }, { 1, 1, 5 }, { 5, 0, 5 } },
        { { 1, 2, 6 }, { 0, 1, 6 }, { 5, 0, 6 } },
        { { 0, 2, 7 }, { 2, 1, 7 }, { 5, 0, 7 } }
    };

    return target;
  }

  /**
   * Creates a plane subdivided into either triangles or rectangles,
   * depending on the polygon type. Useful for meshes which later will
   * be augmented by noise or height maps to simulate terrain.
   *
   * @param cols   number of columns
   * @param rows   number of rows
   * @param target the output mesh
   * @return the mesh
   */
  public static final Mesh3 plane (
      final int cols,
      final int rows,
      final Mesh3 target ) {

    target.name = "Plane";

    final int rval = rows < 1 ? 1 : rows;
    final int cval = cols < 1 ? 1 : cols;

    final int rval1 = rval + 1;
    final int cval1 = cval + 1;

    final float iToStep = 1.0f / rval;
    final float jToStep = 1.0f / cval;

    final Vec3[] vs = target.coords = Vec3.resize(
        target.coords, rval1 * cval1);
    final Vec2[] vts = target.texCoords = Vec2.resize(
        target.texCoords, vs.length);
    target.normals = Vec3.resize(target.normals, 1);
    Vec3.up(target.normals[0]);
    final int flen = rval * cval;

    /* Calculate x values in separate loop. */
    final float[] xs = new float[cval1];
    final float[] us = new float[cval1];
    for (int j = 0; j < cval1; ++j) {
      final float xPrc = j * jToStep;
      xs[j] = xPrc - 0.5f;
      us[j] = xPrc;
    }

    for (int k = 0, i = 0; i < rval1; ++i) {
      final float yPrc = i * iToStep;
      final float y = yPrc - 0.5f;
      final float v = 1.0f - yPrc;

      for (int j = 0; j < cval1; ++j, ++k) {
        vs[k].set(xs[j], y, 0.0f);
        vts[k].set(us[j], v);
      }
    }

    target.faces = new int[flen + flen][3][3];

    /* Normals indices should default to 0. */
    for (int k = 0, i = 0; i < rval; ++i) {
      final int noff0 = i * cval1;
      final int noff1 = (i + 1) * cval1;

      for (int j = 0; j < cval; ++j, k += 2) {
        final int n00 = noff0 + j;
        final int n10 = n00 + 1;
        final int n01 = noff1 + j;
        final int n11 = n01 + 1;

        final int[][] f0 = target.faces[k];
        f0[0][0] = n00;
        f0[0][1] = n00;
        /* f0[0][2] = 0; */

        f0[1][0] = n10;
        f0[1][1] = n10;
        /* f0[1][2] = 0; */

        f0[2][0] = n11;
        f0[2][1] = n11;
        /* f0[2][2] = 0; */

        final int[][] f1 = target.faces[k + 1];
        f1[0][0] = n11;
        f1[0][1] = n11;
        /* f1[0][2] = 0; */

        f1[1][0] = n01;
        f1[1][1] = n01;
        /* f1[1][2] = 0; */

        f1[2][0] = n00;
        f1[2][1] = n00;
        /* f1[2][2] = 0; */
      }
    }

    return target;
  }

  /**
   * Creates a subdivided plane. Useful for meshes which later will be
   * augmented by noise or height maps to simulate terrain.
   *
   * @param div    subdivisions
   * @param target the output mesh
   * @return the plane
   */
  public static final Mesh3 plane (
      final int div,
      final Mesh3 target ) {

    return Mesh3.plane(div, div, target);
  }

  /**
   * Generates a regular convex polygon.
   *
   * @param sectors the number of sides
   * @param target  the output mesh
   * @return the polygon
   */
  public static Mesh3 polygon (
      final int sectors,
      final Mesh3 target ) {

    // TODO: Refactor to use Vec3 resize

    final int seg = sectors < 3 ? 3 : sectors;
    final float toTheta = IUtils.TAU / seg;

    final Vec2 uvCenter = Vec2.uvCenter(new Vec2());
    final Vec2 pureCoord = new Vec2();

    final Vec3[] coords = new Vec3[seg + 1];
    final Vec2[] texCoords = new Vec2[seg + 1];
    final Vec3[] normals = new Vec3[] { Vec3.up(new Vec3()) };
    final int[][][] faces = new int[seg][3][3];

    coords[0] = new Vec3();
    texCoords[0] = uvCenter;

    for (int i = 0, j = 1; i < seg; ++i, ++j) {
      Vec2.fromPolar(i * toTheta, 0.5f, pureCoord);

      final Vec2 st = Vec2.add(uvCenter, pureCoord, new Vec2());
      st.y = 1.0f - st.y;
      texCoords[j] = st;

      coords[j] = new Vec3(pureCoord.x, pureCoord.y, 0.0f);

      final int k = 1 + j % seg;
      faces[i] = new int[][] {
          { 0, 0, 0 },
          { j, j, 0 },
          { k, k, 0 } };
    }

    target.name = "Polygon";
    return target.set(faces, coords, texCoords, normals);
  }

  /**
   * Creates a UV sphere.
   *
   * @param longitudes the longitudes
   * @param latitudes  the latitudes
   * @param target     the output mesh
   * @return the sphere
   */
  public static Mesh3 sphere (
      final int longitudes,
      final int latitudes,
      final Mesh3 target ) {

    // TODO: Refactor to use Vec3 resize

    /*
     * Longitude corresponds to azimuth; latitude, to inclination.
     */
    final int vlons = longitudes < 3 ? 3 : longitudes;
    final int vlats = latitudes < 3 ? 3 : latitudes;

    final int lons1 = vlons + 1;
    final int lats1 = vlats + 1;

    /*
     * The additional two comes from the North and South poles.
     */
    final int len = lons1 * vlats + 2;

    final Vec3[] coords = new Vec3[len];
    final Vec2[] texCoords = new Vec2[len];
    final Vec3[] normals = new Vec3[len];

    final float toU = 1.0f / vlons;
    final float toV = 1.0f / lats1;

    final float toTheta = 1.0f / vlons;
    final float toPhi = 0.5f / lats1;

    /*
     * Set South pole. This is vertex 0, so subsequent vertex indices
     * begin at an offset of 1.
     */
    coords[0] = new Vec3(0.0f, 0.0f, -0.5f);
    texCoords[0] = new Vec2(0.5f, 1.0f - toV * 0.5f);
    normals[0] = new Vec3(0.0f, 0.0f, -1.0f);

    for (int k = 1, h = 1, i = 0; i < vlats; ++h, ++i) {
      final float v = h * toV;
      final float phi = h * toPhi - 0.25f;
      final float cosPhi = Utils.scNorm(phi);
      final float sinPhi = Utils.scNorm(phi - 0.25f);

      for (int j = 0; j < lons1; ++j, ++k) {
        final float u = j * toU;
        final float theta = j * toTheta;
        final float cosTheta = Utils.scNorm(theta);
        final float sinTheta = Utils.scNorm(theta - 0.25f);

        texCoords[k] = new Vec2(u, 1.0f - v);

        final Vec3 nrm = normals[k] = new Vec3(
            cosPhi * cosTheta,
            cosPhi * sinTheta,
            sinPhi);
        coords[k] = Vec3.mul(nrm, 0.5f, new Vec3());
      }
    }

    /* Set North pole. */
    final int last = len - 1;
    coords[last] = new Vec3(0.0f, 0.0f, 0.5f);
    texCoords[last] = new Vec2(0.5f, toV * 0.5f);
    normals[last] = new Vec3(0.0f, 0.0f, 1.0f);

    final int[][][] faces = new int[2 * vlons * vlats][3][3];
    int idx = 0;

    /* Top cap. */
    for (int j = 0; j < vlons; ++j) {
      final int n0 = j + 2;
      final int n1 = j + 1;

      faces[idx] = new int[][] {
          { n0, n0, n0 },
          { n1, n1, n1 },
          { 0, 0, 0 } };
      idx++;
    }

    /* Middle */
    final int latsn1 = vlats - 1;
    for (int i = 0; i < latsn1; ++i) {
      final int ilons1 = i * lons1;
      for (int j = 0; j < vlons; ++j) {
        final int current = j + ilons1 + 1;
        final int next = current + lons1;
        final int n1 = current + 1;
        final int n2 = next + 1;

        faces[idx] = new int[][] {
            { current, current, current },
            { n1, n1, n1 },
            { n2, n2, n2 } };
        idx++;

        faces[idx] = new int[][] {
            { current, current, current },
            { n2, n2, n2 },
            { next, next, next } };
        idx++;
      }
    }

    /* Bottom cap. */
    for (int j = 0; j < vlons; ++j) {
      final int n1 = last - (j + 2);
      final int n2 = last - (j + 1);

      faces[idx] = new int[][] {
          { last, last, last },
          { n1, n1, n1 },
          { n2, n2, n2 } };
      idx++;
    }

    target.name = "UV Sphere";
    return target.set(faces, coords, texCoords, normals);
  }

  /**
   * Creates a UV sphere.
   *
   * @param target the output mesh
   * @return the sphere
   */
  public static Mesh3 sphere ( final Mesh3 target ) {

    return Mesh3.sphere(
        IMesh.DEFAULT_CIRCLE_SECTORS,
        IMesh.DEFAULT_CIRCLE_SECTORS >> 1,
        target);
  }

  /**
   * Creates a square.
   *
   * @param target the output mesh
   * @return the square
   */
  public static final Mesh3 square ( final Mesh3 target ) {

    target.name = "Square";

    target.coords = Vec3.resize(target.coords, 4);
    target.coords[0].set(-0.5f, 0.5f, 0.0f);
    target.coords[1].set(0.5f, 0.5f, 0.0f);
    target.coords[2].set(0.5f, -0.5f, 0.0f);
    target.coords[3].set(-0.5f, -0.5f, 0.0f);

    target.texCoords = Vec2.resize(target.texCoords, 4);
    target.texCoords[0].set(0.0f, 0.0f);
    target.texCoords[1].set(1.0f, 0.0f);
    target.texCoords[2].set(1.0f, 1.0f);
    target.texCoords[3].set(0.0f, 1.0f);

    target.normals = Vec3.resize(target.normals, 1);
    Vec3.up(target.normals[0]);

    target.faces = new int[][][] {
        { { 0, 0, 0 }, { 1, 1, 0 }, { 2, 2, 0 } },
        { { 2, 2, 0 }, { 3, 3, 0 }, { 0, 0, 0 } }
    };

    return target;
  }

  /**
   * Creates a tetrahedron, a Platonic solid with 4 faces and 4
   * coordinates.
   *
   * @param target the output mesh
   * @return the tetrahedron
   */
  public static final Mesh3 tetrahedron ( final Mesh3 target ) {

    /*
     * double r = 0.5d; double a = r * Math.sqrt(8.0d / 9.0d); double b =
     * r * Math.sqrt(2.0d / 9.0d); double c = r * Math.sqrt(2.0d / 3.0d);
     * double d = r * (1.0d / 3.0d);
     *
     * 0.47140452079103168293389624140323,
     * 0.23570226039551584146694812070162,
     * 0.40824829046386301636621401245098
     */
    target.name = "Tetrahedron";

    target.coords = Vec3.resize(target.coords, 4);
    target.coords[0].set(-0.0f, 0.47140452f, -0.16666667f);
    target.coords[1].set(-0.40824829f, -0.23570226f, -0.16666667f);
    target.coords[2].set(0.40824829f, -0.23570226f, -0.16666667f);
    target.coords[3].set(0.0f, 0.0f, 0.5f);

    target.texCoords = Vec2.resize(target.texCoords, 3);
    target.texCoords[0].set(0.5f, 0.0f);
    target.texCoords[1].set(1.0f, 1.0f);
    target.texCoords[2].set(0.0f, 1.0f);

    target.normals = Vec3.resize(target.normals, 4);
    target.normals[0].set(0.0f, 0.0f, -1.0f);
    target.normals[1].set(-0.8164966f, 0.47140453f, 0.33333333f);
    target.normals[2].set(0.0f, -0.9428091f, 0.33333333f);
    target.normals[3].set(0.8164966f, 0.47140453f, 0.33333333f);

    target.faces = new int[][][] {
        { { 0, 0, 0 }, { 2, 2, 0 }, { 1, 1, 0 } },
        { { 0, 2, 1 }, { 1, 1, 1 }, { 3, 0, 1 } },
        { { 0, 1, 3 }, { 3, 0, 3 }, { 2, 2, 3 } },
        { { 1, 2, 2 }, { 2, 1, 2 }, { 3, 0, 2 } } };

    return target;
  }

  /**
   * Creates a triangle.
   *
   * @param target the output mesh
   * @return the triangle
   */
  public static final Mesh3 triangle ( final Mesh3 target ) {

    target.name = "Triangle";

    target.coords = Vec3.resize(target.coords, 3);
    target.coords[0].set(0.0f, 0.5f, 0.0f);
    target.coords[1].set(-0.4330127f, -0.25f, 0.0f);
    target.coords[2].set(0.4330127f, -0.25f, 0.0f);

    target.texCoords = Vec2.resize(target.texCoords, 3);
    target.texCoords[0].set(0.5f, 0.0f);
    target.texCoords[1].set(0.0669873f, 0.75f);
    target.texCoords[2].set(0.9330127f, 0.75f);

    target.normals = Vec3.resize(target.normals, 1);
    Vec3.up(target.normals[0]);

    target.faces = new int[][][] {
        { { 0, 0, 0 }, { 1, 1, 1 }, { 2, 2, 2 } } };

    return target;
  }

  /**
   * Restructures the mesh so that each face index refers to unique
   * data, indifferent to redundancies. As a consequence, coordinates
   * and texture coordinate are of equal length and face indices are
   * easier to read and understand. Useful prior to subdividing edges,
   * or to make mesh similar to Unity meshes. Similar to 'ripping'
   * vertices or 'tearing' edges in Blender.
   *
   * @param source the source mesh
   * @param target the target mesh
   * @return the mesh
   */
  public static Mesh3 uniformData (
      final Mesh3 source,
      final Mesh3 target ) {

    target.name = source.name;

    final int len0 = source.faces.length;
    final ArrayList < Vec3 > vs = new ArrayList <>();
    final ArrayList < Vec2 > vts = new ArrayList <>();
    final ArrayList < Vec3 > vns = new ArrayList <>();

    target.faces = new int[len0][][];

    for (int k = 0, i = 0; i < len0; ++i) {

      final int[][] fs0 = source.faces[i];
      final int len1 = fs0.length;
      final int[][] trgfs0 = target.faces[i] = new int[len1][2];

      for (int j = 0; j < len1; ++j, ++k) {

        final int[] fs1 = fs0[j];

        vs.add(new Vec3(source.coords[fs1[0]]));
        vts.add(new Vec2(source.texCoords[fs1[1]]));
        vns.add(new Vec3(source.normals[fs1[2]]));

        trgfs0[j][0] = k;
        trgfs0[j][1] = k;
        trgfs0[j][2] = k;
      }
    }

    target.coords = vs.toArray(new Vec3[vs.size()]);
    target.texCoords = vts.toArray(new Vec2[vts.size()]);
    target.normals = vns.toArray(new Vec3[vns.size()]);
    return target;
  }

}
