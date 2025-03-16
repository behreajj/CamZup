package camzup.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Organizes data needed to draw a three dimensional shape with vertices
 * and faces. Given that a mesh is primarily a collection of references, it
 * is initialized with null arrays (coordinates, texture coordinates and
 * indices). These are not final, and so can be reassigned.
 */
public class Mesh3 extends Mesh implements Iterable < Face3 > {

   /**
    * An array of coordinates.
    */
   public Vec3[] coords;

   /**
    * An array of normals to indicate how light will bounce off the mesh.
    */
   public Vec3[] normals;

   /**
    * The texture (UV) coordinates that describe how an image is mapped onto
    * the mesh. Typically in the range [0.0, 1.0] .
    */
   public Vec2[] texCoords;

   /**
    * The default constructor.
    */
   public Mesh3 ( ) {}

   /**
    * Creates a mesh from arrays of faces, coordinates, texture coordinates
    * and normals. The mesh's arrays are set by reference, not by value.
    *
    * @param faces     the faces array
    * @param coords    the coordinates array
    * @param texCoords the texture coordinates array
    * @param normals   the normals array
    */
   public Mesh3 ( final int[][][] faces, final Vec3[] coords,
      final Vec2[] texCoords, final Vec3[] normals ) {

      this.set(faces, coords, texCoords, normals);
   }

   /**
    * Constructs a copy of the source mesh.
    *
    * @param source the source mesh
    */
   public Mesh3 ( final Mesh2 source ) { this.set(source); }

   /**
    * Constructs a copy of the source mesh.
    *
    * @param source the source mesh
    */
   public Mesh3 ( final Mesh3 source ) { this.set(source); }

   /**
    * Creates a named mesh.
    *
    * @param name the mesh name
    */
   public Mesh3 ( final String name ) { super(name); }

   /**
    * Creates a named mesh from arrays of faces, coordinates, texture
    * coordinates and normals. The mesh's arrays are set by reference, not by
    * value.
    *
    * @param name      the mesh name
    * @param faces     the faces array
    * @param coords    the coordinates array
    * @param texCoords the texture coordinates array
    * @param normals   the normals array
    */
   public Mesh3 ( final String name, final int[][][] faces, final Vec3[] coords,
      final Vec2[] texCoords, final Vec3[] normals ) {

      super(name);
      this.set(faces, coords, texCoords, normals);
   }

   /**
    * Calculates texture coordinates (UVs) for this mesh. Converts a vertex's
    * index in a face loop to an angle, then to Cartesian coordinates.
    *
    * @return this mesh
    *
    * @see Utils#div(float, float)
    * @see Vec2#resize(Vec2[], int)
    */
   public Mesh3 calcUvs ( ) {

      int uniformLen = 0;
      final int fsLen = this.faces.length;
      for ( int i = 0; i < fsLen; ++i ) { uniformLen += this.faces[i].length; }
      this.texCoords = Vec2.resize(this.texCoords, uniformLen);

      for ( int k = 0, i = 0; i < fsLen; ++i ) {
         final int[][] f = this.faces[i];
         final int fLen = f.length;
         final float toTheta = Utils.div(1.0f, fLen);
         for ( int j = 0; j < fLen; ++j, ++k ) {
            f[j][1] = k;
            final float theta = j * toTheta;
            final float cosTheta = Utils.scNorm(theta);
            final float sinTheta = Utils.scNorm(theta - 0.25f);
            this.texCoords[k].set(cosTheta * 0.5f + 0.5f, 0.5f - sinTheta
               * 0.5f);
         }
      }
      return this;
   }

   /**
    * Removes elements from the coordinate, texture coordinate and normal
    * arrays of the mesh which are not visited by the face indices.
    *
    * @return this mesh
    */
   @Experimental
   public Mesh3 clean ( ) {

      /* Transfer arrays to hash maps where the face index is the key. */
      final HashMap < Integer, Vec3 > usedCoords = new HashMap <>();
      final HashMap < Integer, Vec2 > usedTexCoords = new HashMap <>();
      final HashMap < Integer, Vec3 > usedNormals = new HashMap <>();

      /*
       * Visit all data arrays with the faces array. Any data not used by any
       * face will be left out.
       */
      final int facesLen = this.faces.length;
      for ( int i = 0; i < facesLen; ++i ) {
         final int[][] verts = this.faces[i];
         final int vertsLen = verts.length;
         for ( int j = 0; j < vertsLen; ++j ) {
            final int[] vert = verts[j];
            final int vIdx = vert[0];
            final int vtIdx = vert[1];
            final int vnIdx = vert[2];

            /* The hash map will ignore repeated visitations. */
            usedCoords.put(vIdx, this.coords[vIdx]);
            usedTexCoords.put(vtIdx, this.texCoords[vtIdx]);
            usedNormals.put(vnIdx, this.normals[vnIdx]);
         }
      }

      /* Use a tree set to filter out similar vectors. */
      final TreeSet < Vec3 > coordsTree = new TreeSet <>(Mesh.SORT_3);
      final TreeSet < Vec2 > texCoordsTree = new TreeSet <>(Mesh.SORT_2);
      final TreeSet < Vec3 > normalsTree = new TreeSet <>(Mesh.SORT_3);

      /* Hash map's keys are no longer needed; just values. */
      coordsTree.addAll(usedCoords.values());
      texCoordsTree.addAll(usedTexCoords.values());
      normalsTree.addAll(usedNormals.values());

      /* Convert from sorted set to arrays. */
      final Vec3[] newCoords = coordsTree.toArray(new Vec3[coordsTree.size()]);
      final Vec2[] newTexCoords = texCoordsTree.toArray(new Vec2[texCoordsTree
         .size()]);
      final Vec3[] newNormals = normalsTree.toArray(new Vec3[normalsTree
         .size()]);

      for ( int i = 0; i < facesLen; ++i ) {
         final int[][] verts = this.faces[i];
         final int vertsLen = verts.length;
         for ( int j = 0; j < vertsLen; ++j ) {
            final int[] vert = verts[j];

            /*
             * Find index of vector in new array by using indexed value from old
             * array as a reference.
             */
            vert[0] = Arrays.binarySearch(newCoords, this.coords[vert[0]],
               Mesh.SORT_3);
            vert[1] = Arrays.binarySearch(newTexCoords, this.texCoords[vert[1]],
               Mesh.SORT_2);
            vert[2] = Arrays.binarySearch(newNormals, this.normals[vert[2]],
               Mesh.SORT_3);
         }
      }

      /* Replace old arrays with the new. */
      this.coords = newCoords;
      this.texCoords = newTexCoords;
      this.normals = newNormals;

      /* Sort faces by center. */
      Arrays.sort(this.faces, new SortLoops3(this.coords));

      return this;
   }

   /**
    * Collapses an edge to a point. Unlike
    * {@link Mesh3#deleteVerts(int, int, int)}, inserts a midpoint between
    * edge origin and destination.
    *
    * @param faceIndex the face index
    * @param edgeIndex the edge index
    *
    * @return this mesh
    */
   public Mesh3 collapseEdge ( final int faceIndex, final int edgeIndex ) {

      return this.collapseEdge(faceIndex, edgeIndex, 0.5f);
   }

   /**
    * Collapses an edge to a point. Unlike
    * {@link Mesh3#deleteVerts(int, int, int)}, inserts a newly created vertex
    * between edge origin and destination according to a factor in [0.0, 1.0];
    * a factor of 0.5 will collapse an edge to its midpoint; of 0.0, to its
    * origin; of 1.0, to its destination.
    *
    * @param faceIndex the face index
    * @param edgeIndex the edge index
    * @param fac       the offset factor
    *
    * @return this mesh
    *
    * @see Utils#mod(int, int)
    * @see Utils#clamp01(float)
    * @see Vec3#normalize(Vec3, Vec3)
    */
   @Experimental
   public Mesh3 collapseEdge ( final int faceIndex, final int edgeIndex,
      final float fac ) {

      /* Find face. */
      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIndex, facesLen);
      final int[][] face = this.faces[i];

      /* Find edge. */
      final int faceLen = face.length;
      final int j = Utils.mod(edgeIndex, faceLen);
      final int[] orig = face[j];
      final int[] dest = face[ ( j + 1 ) % faceLen];

      /* Cache these prior to appending to coordinates. */
      final int vsOldLen = this.coords.length;
      final int vtsOldLen = this.texCoords.length;
      final int vnsOldLen = this.normals.length;

      /* Clamp factor. */
      final float t = Utils.clamp01(fac);
      final float u = 1.0f - t;

      /* Mix coordinates. */
      final Vec3 vOrig = this.coords[orig[0]];
      final Vec3 vDest = this.coords[dest[0]];
      final Vec3 vMidPoint = new Vec3(u * vOrig.x + t * vDest.x, u * vOrig.y + t
         * vDest.y, u * vOrig.z + t * vDest.z);

      /* Mix texture coordinates. */
      final Vec2 vtOrig = this.texCoords[orig[1]];
      final Vec2 vtDest = this.texCoords[dest[1]];
      final Vec2 vtMidPoint = new Vec2(u * vtOrig.x + t * vtDest.x, u * vtOrig.y
         + t * vtDest.y);

      /* Mix normals. */
      final Vec3 vnOrig = this.normals[orig[2]];
      final Vec3 vnDest = this.normals[dest[2]];
      final Vec3 vnMidPoint = new Vec3(u * vnOrig.x + t * vnDest.x, u * vnOrig.y
         + t * vnDest.y, u * vnOrig.z + t * vnDest.z);
      Vec3.normalize(vnMidPoint, vnMidPoint);

      /* Append new data. */
      this.coords = Vec3.append(this.coords, vMidPoint);
      this.texCoords = Vec2.append(this.texCoords, vtMidPoint);
      this.normals = Vec3.append(this.normals, vnMidPoint);
      this.faces[i] = Mesh.splice(face, j, 2, new int[][] { new int[] {
         vsOldLen, vtsOldLen, vnsOldLen } });

      return this;
   }

   /**
    * Removes a face indices from this mesh beginning at an index. Does not
    * remove any data associated with the indices.
    *
    * @param faceIndex the index
    *
    * @return this mesh
    *
    * @see Mesh3#deleteFaces(int, int)
    */
   public Mesh3 deleteFace ( final int faceIndex ) {

      return this.deleteFaces(faceIndex, 1);
   }

   /**
    * Removes a given number of face indices from this mesh beginning at an
    * index. Does not remove any data associated with the indices.
    *
    * @param faceIndex the index
    * @param count     the removal count
    *
    * @return this mesh
    *
    * @see Mesh#remove(int[][][], int, int)
    */
   public Mesh3 deleteFaces ( final int faceIndex, final int count ) {

      this.faces = Mesh.remove(this.faces, faceIndex, count);
      return this;
   }

   /**
    * Removes a given number of vertex indices from this mesh beginning at an
    * index. Does not remove any data associated with the indices.
    *
    * @param faceIndex the face index
    * @param vertIndex the vertex index
    * @param count     the removal count
    *
    * @return this mesh
    *
    * @see Mesh#remove(int[][], int, int)
    * @see Utils#mod(int, int)
    */
   public Mesh3 deleteVerts ( final int faceIndex, final int vertIndex,
      final int count ) {

      final int j = Utils.mod(faceIndex, this.faces.length);
      final int[][] f = this.faces[j];
      final int vcount = Math.min(f.length - 3, count);
      this.faces[j] = Mesh.remove(f, vertIndex, vcount);
      return this;
   }

   /**
    * Tests this mesh for equivalence with an object.
    *
    * @param obj the object
    *
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( !super.equals(obj) || this.getClass() != obj.getClass() ) {
         return false;
      }
      return this.equals(( Mesh3 ) obj);
   }

   /**
    * Extrudes an edge, creating a new quadrilateral tangent to the edge.
    *
    * @param faceIndex the face index
    * @param edgeIndex the edge index
    * @param amount    the extrusion amount
    *
    * @return this mesh
    */
   @Experimental
   public Mesh3 extrudeEdge ( final int faceIndex, final int edgeIndex,
      final float amount ) {

      if ( amount == 0.0f ) { return this; }

      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIndex, facesLen);
      final int[][] face = this.faces[i];
      final int vertsLen = face.length;

      final int j = Utils.mod(edgeIndex, vertsLen);
      final int k = ( j + 1 ) % vertsLen;
      final int[] idxOrig = face[j];
      final int[] idxDest = face[k];

      final int idxV0 = idxOrig[0];
      final int idxVt0 = idxOrig[1];
      final int idxVn0 = idxOrig[2];

      final int idxV3 = idxDest[0];
      final int idxVt3 = idxDest[1];
      final int idxVn3 = idxDest[2];

      final int vsOldLen = this.coords.length;
      final int vtsOldLen = this.texCoords.length;
      final int vnsOldLen = this.normals.length;

      final int idxV1 = vsOldLen;
      final int idxVt1 = vtsOldLen;
      final int idxVn1 = vnsOldLen;

      final int idxV2 = vsOldLen + 1;
      final int idxVt2 = vtsOldLen + 1;

      final Vec3 vOrig = this.coords[idxV0];
      final Vec2 vtOrig = this.texCoords[idxVt0];
      final Vec3 vnOrig = this.normals[idxVn0];

      final Vec3 vDest = this.coords[idxV3];
      final Vec2 vtDest = this.texCoords[idxVt3];
      final Vec3 vnDest = this.normals[idxVn3];

      final Vec3 vnDiff = Vec3.addNorm(vnDest, vnOrig, new Vec3());

      final Vec3 vDiff = new Vec3();
      Vec3.sub(vDest, vOrig, vDiff);
      final float edgeMag = Vec3.mag(vDiff);

      final Vec3 extrude = new Vec3();
      Vec3.crossNorm(vDiff, vnDiff, extrude);
      Vec3.mul(extrude, amount * edgeMag, extrude);

      final Vec3 vNewOrig = new Vec3();
      final Vec3 vNewDest = new Vec3();
      Vec3.add(vOrig, extrude, vNewOrig);
      Vec3.add(vDest, extrude, vNewDest);

      final Vec2 vtPerp = new Vec2();
      Vec2.sub(vtOrig, vtDest, vtPerp);
      Vec2.perpendicularCW(vtPerp, vtPerp);

      final Vec2 vtNewOrig = new Vec2();
      final Vec2 vtNewDest = new Vec2();
      Vec2.add(vtOrig, vtPerp, vtNewOrig);
      Vec2.add(vtDest, vtPerp, vtNewDest);

      final int[][][] faceNew = { { { idxV1, idxVt1, idxVn1 }, { idxV2, idxVt2,
         idxVn1 }, { idxV3, idxVt3, idxVn1 }, { idxV0, idxVt0, idxVn1 } } };

      this.coords = Vec3.concat(this.coords, new Vec3[] { vNewOrig, vNewDest });
      this.texCoords = Vec2.concat(this.texCoords, new Vec2[] { vtNewOrig,
         vtNewDest });
      this.normals = Vec3.append(this.normals, vnDiff);
      this.faces = Mesh.splice(this.faces, i + 1, 0, faceNew);

      return this;
   }

   /**
    * Extrudes all edges of a face, creating new tangential quadrilaterals.
    *
    * @param faceIndex the face index
    * @param amount    the extrusion amount
    *
    * @return this mesh
    */
   public Mesh3 extrudeEdges ( final int faceIndex, final float amount ) {

      final int i = Utils.mod(faceIndex, this.faces.length);
      final int faceLen = this.faces[i].length;
      for ( int j = 0; j < faceLen; ++j ) { this.extrudeEdge(i, j, amount); }

      return this;
   }

   /**
    * Extrudes a face by an amount. Creates quadrilateral sides to connect
    * extruded face to original. Does not check whether a face is bordered by
    * other faces; best used on disconnected faces.
    *
    * @param faceIndex the face index
    * @param fillCap   whether to cap the extruded face
    * @param depth     the extrusion depth
    *
    * @return this new face indices
    */
   public Mesh3 extrudeFace ( final int faceIndex, final boolean fillCap,
      final float depth ) {

      return this.extrudeFace(faceIndex, fillCap, depth, 1.0f);
   }

   /**
    * Extrudes a face by an amount. Creates quadrilateral sides to connect
    * extruded face to original. Does not check whether a face is bordered by
    * other faces; best used on disconnected faces. The taper is a scalar
    * applied to a face's size, e.g., a taper of .75 would result in an
    * extruded face 75% the size of the original.
    *
    * @param faceIndex the face index
    * @param fillCap   whether to cap the extruded face
    * @param depth     the extrusion depth
    * @param taper     the taper
    *
    * @return this new face indices
    *
    * @see Mesh3#shadeFlat(int, int)
    * @see Utils#max(float, float)
    * @see Utils#mod(int, int)
    * @see Vec3#add(Vec3, Vec3, Vec3)
    * @see Vec3#div(Vec3, float, Vec3)
    * @see Vec3#negate(Vec3, Vec3)
    * @see Vec3#normalize(Vec3, Vec3)
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    */
   @Experimental
   public Mesh3 extrudeFace ( final int faceIndex, final boolean fillCap,
      final float depth, final float taper ) {

      if ( depth == 0.0f ) { return this; }
      final float verifTaper = Utils.max(IUtils.EPSILON, taper);

      /* Validate face index, find face. */
      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIndex, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      /*
       * Find center for coordinate and normal. The extrusion is the
       * multiplication of the center normal by the amount.
       */
      final Vec3 center = new Vec3();
      final Vec3 extrudeNorm = new Vec3();
      final Vec3 extrusion = new Vec3();
      for ( int j = 0; j < faceLen; ++j ) {
         final int[] vert = face[j];
         Vec3.add(center, this.coords[vert[0]], center);
         Vec3.add(extrudeNorm, this.normals[vert[2]], extrudeNorm);
      }
      Vec3.div(center, faceLen, center);
      Vec3.normalize(extrudeNorm, extrudeNorm);
      Vec3.mul(extrudeNorm, depth, extrusion);

      /* Cache old length of data so new data can be appended to the end. */
      final int vsOldLen = this.coords.length;
      final int vtsOldLen = this.texCoords.length;
      final int vnsOldLen = this.normals.length;

      final Vec3[] vsExtruded = new Vec3[faceLen];
      final Vec2[] vtsSides = { new Vec2(0.0f, 0.0f), new Vec2(1.0f, 0.0f),
         new Vec2(1.0f, 1.0f), new Vec2(0.0f, 1.0f) };
      final int[][][] fsNew = new int[faceLen + 1][4][3];
      final int[][] extrudedFace = fsNew[faceLen] = new int[faceLen][3];

      for ( int j = 0; j < faceLen; ++j ) {

         /* Cache current vertex. */
         final int[] vertCurr = face[j];
         final int vCurrIdx = vertCurr[0];

         /* Cache next vertex index for side face. */
         final int k = ( j + 1 ) % faceLen;

         /* Retrieve coordinate. Create extruded vertex. */
         final Vec3 vBase = this.coords[vCurrIdx];
         final Vec3 vExtruded = vsExtruded[j] = new Vec3();

         /*
          * Taper and extrude the vertex coordinate (remove center, scale
          * locally, translate locally, reintroduce center).
          */
         Vec3.sub(vBase, center, vExtruded);
         Vec3.mul(vExtruded, verifTaper, vExtruded);
         Vec3.add(vExtruded, extrusion, vExtruded);
         Vec3.add(vExtruded, center, vExtruded);

         /* Update extruded vertex indices. */
         final int[] extrudedVert = extrudedFace[j];
         extrudedVert[0] = vsOldLen + j;
         extrudedVert[1] = vertCurr[1];
         extrudedVert[2] = vnsOldLen;

         /* Create face for side. */
         final int[][] sideFace = fsNew[j];

         /* Normal indices not updated here because shadeFlat does it later. */
         final int[] v00 = sideFace[0];
         v00[0] = vCurrIdx;
         v00[1] = vtsOldLen;

         final int[] v10 = sideFace[1];
         v10[0] = face[k][0];
         v10[1] = vtsOldLen + 1;

         final int[] v11 = sideFace[2];
         v11[0] = vsOldLen + k;
         v11[1] = vtsOldLen + 2;

         final int[] v01 = sideFace[3];
         v01[0] = vsOldLen + j;
         v01[1] = vtsOldLen + 3;
      }

      /* Update data. */
      this.coords = Vec3.concat(this.coords, vsExtruded);
      this.texCoords = Vec2.concat(this.texCoords, vtsSides);
      this.normals = Vec3.append(this.normals, extrudeNorm);

      /* Copy old face's normals, flip them, then reassign indices. */
      if ( fillCap ) {
         Mesh.reverse(face, 0, faceLen - 1);
         final int idxFlipped = vnsOldLen + 1;
         for ( int k = 0; k < faceLen; ++k ) { face[k][2] = idxFlipped; }
         this.normals = Vec3.append(this.normals, Vec3.negate(extrudeNorm,
            new Vec3()));
      }

      /* Update indices. If cap is to be removed, splice out original face. */
      this.faces = Mesh.splice(this.faces, i, fillCap ? 0 : 1, fsNew);

      /* Shade normals of side panels. */
      this.shadeFlat(i, faceLen);

      return this;
   }

   /**
    * Extrudes all faces in the mesh by an amount. Creates quadrilateral sides
    * to connect extruded face to original. Does not check as to whether a
    * face is bordered by other faces; best used on disconnected faces.
    *
    * @param fillCap whether to cap the extruded faces
    * @param depth   the extrusion depth
    *
    * @return this mesh
    */
   public Mesh3 extrudeFaces ( final boolean fillCap, final float depth ) {

      return this.extrudeFaces(fillCap, depth, 1.0f);
   }

   /**
    * Extrudes all faces in the mesh by an amount. Creates quadrilateral sides
    * to connect extruded face to original. Does not check as to whether a
    * face is bordered by other faces; best used on disconnected faces. The
    * taper is a scalar applied to a face's size, e.g., a taper of .75 would
    * result in an extruded face 75% the size of the original.
    *
    * @param fillCap whether to cap the extruded faces
    * @param depth   the extrusion depth
    * @param taper   the taper
    *
    * @return this mesh
    */
   @Experimental
   public Mesh3 extrudeFaces ( final boolean fillCap, final float depth,
      final float taper ) {

      int k = 0;
      final int facesLen = this.faces.length;
      final int stride = fillCap ? 2 : 1;
      for ( int i = 0; i < facesLen; ++i ) {
         final int faceLen = this.faces[k].length;
         this.extrudeFace(k, fillCap, depth, taper);
         k += faceLen + stride;
      }
      return this;
   }

   /**
    * Negates all normals in this mesh, then reverses the mesh's faces.
    *
    * @return this mesh
    *
    * @see Vec3#negate(Vec3, Vec3)
    * @see Mesh3#reverseFaces()
    */
   public Mesh3 flipNormals ( ) {

      final int len = this.normals.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 n = this.normals[i];
         Vec3.negate(n, n);
      }
      this.reverseFaces();
      return this;
   }

   /**
    * Negates the x component of all texture coordinates (u) in the mesh. Does
    * so by subtracting the value from 1.0.
    *
    * @return this mesh
    */
   public Mesh3 flipU ( ) {

      final int len = this.texCoords.length;
      for ( int i = 0; i < len; ++i ) {
         this.texCoords[i].x = 1.0f - this.texCoords[i].x;
      }
      return this;
   }

   /**
    * Negates the y component of all texture coordinates (v) in the mesh. Does
    * so by subtracting the value from 1.0.
    *
    * @return this mesh
    */
   public Mesh3 flipV ( ) {

      final int len = this.texCoords.length;
      for ( int i = 0; i < len; ++i ) {
         this.texCoords[i].y = 1.0f - this.texCoords[i].y;
      }
      return this;
   }

   /**
    * Negates the x component of all coordinates in the mesh, then reverses
    * the mesh's faces. Use this instead of {@link Mesh3#scale(Vec3)} with the
    * argument <code>new Vec3(-1.0f, 1.0f, 1.0f)</code>.
    *
    * @return this mesh
    *
    * @see Mesh3#reverseFaces()
    */
   public Mesh3 flipX ( ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) { this.coords[i].x = -this.coords[i].x; }
      this.reverseFaces();
      return this;
   }

   /**
    * Negates the y component of all coordinates in the mesh, then reverses
    * the mesh's faces. Use this instead of {@link Mesh3#scale(Vec3)} with the
    * argument <code>new Vec2(1.0f, -1.0f, 1.0f)</code>.
    *
    * @return this mesh
    *
    * @see Mesh3#reverseFaces()
    */
   public Mesh3 flipY ( ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) { this.coords[i].y = -this.coords[i].y; }
      this.reverseFaces();
      return this;
   }

   /**
    * Negates the z component of all coordinates in the mesh, then reverses
    * the mesh's faces. Use this instead of {@link Mesh3#scale(Vec3)} with the
    * argument <code>new Vec3(1.0f, 1.0f, -1.0f)</code>.
    *
    * @return this mesh
    *
    * @see Mesh3#reverseFaces()
    */
   public Mesh3 flipZ ( ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) { this.coords[i].z = -this.coords[i].z; }
      this.reverseFaces();
      return this;
   }

   /**
    * Gets an edge from the mesh.
    *
    * @param faceIndex the face index
    * @param edgeIndex the edge index
    * @param target    the output edge
    *
    * @return the edge
    */
   public Edge3 getEdge ( final int faceIndex, final int edgeIndex,
      final Edge3 target ) {

      final int[][] face = this.faces[Utils.mod(faceIndex, this.faces.length)];
      final int faceLen = face.length;
      final int j = Utils.mod(edgeIndex, faceLen);
      final int[] idcsOrig = face[j];
      final int[] idcsDest = face[ ( j + 1 ) % faceLen];

      return target.set(this.coords[idcsOrig[0]], this.texCoords[idcsOrig[1]],
         this.normals[idcsOrig[2]], this.coords[idcsDest[0]],
         this.texCoords[idcsDest[1]], this.normals[idcsDest[2]]);
   }

   /**
    * Gets an array of edges from the mesh.
    *
    * @return the edges array
    */
   public Edge3[] getEdges ( ) { return this.getEdgesDirected(); }

   /**
    * Gets an array of edges from the mesh. Edges are treated as directed, so
    * (origin, destination) and (destination, edge) are considered to be
    * different.
    *
    * @return the edges array
    */
   public Edge3[] getEdgesDirected ( ) {

      Edge3 trial = new Edge3();
      final int facesLen = this.faces.length;
      final ArrayList < Edge3 > result = new ArrayList <>(facesLen * 4);

      for ( int i = 0; i < facesLen; ++i ) {

         final int[][] f = this.faces[i];
         final int faceLen = f.length;

         for ( int j = 0; j < faceLen; ++j ) {

            final int[] idcsOrig = f[j];
            final int[] idcsDest = f[ ( j + 1 ) % faceLen];

            trial.set(this.coords[idcsOrig[0]], this.texCoords[idcsOrig[1]],
               this.normals[idcsOrig[2]], this.coords[idcsDest[0]],
               this.texCoords[idcsDest[1]], this.normals[idcsDest[2]]);

            if ( !result.contains(trial) ) {
               result.add(trial);
               trial = new Edge3();
            }
         }
      }

      return result.toArray(new Edge3[result.size()]);
   }

   /**
    * Gets an array of edges from the mesh. Edges are treated as undirected,
    * so (origin, destination) and (destination, edge) are considered to be
    * the same.
    *
    * @return the edges array
    */
   public Edge3[] getEdgesUndirected ( ) {

      final int facesLen = this.faces.length;
      final TreeMap < Integer, Edge3 > result = new TreeMap <>();

      for ( int i = 0; i < facesLen; ++i ) {

         final int[][] fs = this.faces[i];
         final int faceLen = fs.length;

         for ( int j = 0; j < faceLen; ++j ) {

            final int[] idcsOrig = fs[j];
            final int[] idcsDest = fs[ ( j + 1 ) % faceLen];

            final int vIdxOrig = idcsOrig[0];
            final int vIdxDest = idcsDest[0];

            final Integer aHsh = ( IUtils.MUL_BASE ^ vIdxOrig )
               * IUtils.HASH_MUL ^ vIdxDest;
            final Integer bHsh = ( IUtils.MUL_BASE ^ vIdxDest )
               * IUtils.HASH_MUL ^ vIdxOrig;

            if ( !result.containsKey(aHsh) && !result.containsKey(bHsh) ) {
               result.put(vIdxOrig < vIdxDest ? aHsh : bHsh, new Edge3(
                  this.coords[vIdxOrig], this.texCoords[idcsOrig[1]],
                  this.normals[idcsOrig[2]], this.coords[vIdxDest],
                  this.texCoords[idcsDest[1]], this.normals[idcsDest[2]]));
            }
         }
      }

      return result.values().toArray(new Edge3[result.size()]);
   }

   /**
    * Gets a face from the mesh.
    *
    * @param index  the index
    * @param target the output face
    *
    * @return the face
    */
   public Face3 getFace ( final int index, final Face3 target ) {

      final int[][] face = this.faces[Utils.mod(index, this.faces.length)];
      final int faceLen = face.length;
      final Vert3[] vertices = new Vert3[faceLen];

      for ( int j = 0; j < faceLen; ++j ) {
         final int[] vert = face[j];
         vertices[j] = new Vert3(this.coords[vert[0]], this.texCoords[vert[1]],
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

      final int facesLen = this.faces.length;
      final Face3[] result = new Face3[facesLen];

      for ( int i = 0; i < facesLen; ++i ) {

         final int[][] fs0 = this.faces[i];
         final int faceLen = fs0.length;
         final Vert3[] verts = new Vert3[faceLen];

         for ( int j = 0; j < faceLen; ++j ) {

            final int[] fs1 = fs0[j];
            verts[j] = new Vert3(this.coords[fs1[0]], this.texCoords[fs1[1]],
               this.normals[fs1[2]]);
         }

         result[i] = new Face3(verts);
      }

      return result;
   }

   /**
    * Gets a vertex from the mesh.
    *
    * @param faceIndex the face index
    * @param vertIndex the vertex index
    * @param target    the output vertex
    *
    * @return the vertex
    */
   public Vert3 getVertex ( final int faceIndex, final int vertIndex,
      final Vert3 target ) {

      final int[][] face = this.faces[Utils.mod(faceIndex, this.faces.length)];
      final int[] vertidcs = face[Utils.mod(vertIndex, face.length)];
      return target.set(this.coords[vertidcs[0]], this.texCoords[vertidcs[1]],
         this.normals[vertidcs[2]]);
   }

   /**
    * Gets an array of vertices from the mesh.
    *
    * @return the vertices
    */
   public Vert3[] getVertices ( ) {

      Vert3 trial = new Vert3();
      final int facesLen = this.faces.length;
      final ArrayList < Vert3 > result = new ArrayList <>(facesLen);

      for ( int i = 0; i < facesLen; ++i ) {

         final int[][] fs = this.faces[i];
         final int faceLen = fs.length;

         for ( int j = 0; j < faceLen; ++j ) {

            final int[] f = fs[j];
            trial.set(this.coords[f[0]], this.texCoords[f[1]],
               this.normals[f[2]]);

            if ( !result.contains(trial) ) {
               result.add(trial);
               trial = new Vert3();
            }
         }
      }

      return result.toArray(new Vert3[result.size()]);
   }

   /**
    * Returns a hash code for this mesh based on its coordinates and its face
    * indices.
    *
    * @return the hash code
    */
   @Override
   public int hashCode ( ) {

      int hash = super.hashCode();
      hash = hash * IUtils.HASH_MUL ^ Arrays.hashCode(this.coords);
      return hash * IUtils.HASH_MUL ^ Arrays.deepHashCode(this.faces);
   }

   /**
    * A convenience function to inset a face at an index and extrude it.
    *
    * @param faceIndex the face index
    * @param inset     the inset factor
    * @param depth     the extrusion depth
    *
    * @return this mesh
    */
   public Mesh3 insetExtrudeFace ( final int faceIndex, final float inset,
      final float depth ) {

      return this.insetExtrudeFace(faceIndex, inset, depth, 1.0f);
   }

   /**
    * A convenience function to inset a face at an index and extrude it. The
    * taper is a scalar applied to a face's size, e.g., a taper of .75 would
    * result in an extruded face 75% the size of the original.
    *
    * @param faceIndex the face index
    * @param inset     the inset factor
    * @param depth     the extrusion depth
    * @param taper     the taper
    *
    * @return this mesh
    */
   public Mesh3 insetExtrudeFace ( final int faceIndex, final float inset,
      final float depth, final float taper ) {

      final int i = Utils.mod(faceIndex, this.faces.length);
      final int fLen = this.faces[i].length;
      this.insetFace(i, Utils.clamp(inset, IUtils.EPSILON, 1.0f
         - IUtils.EPSILON));
      this.extrudeFace(i + fLen, false, depth, taper);
      return this;
   }

   /**
    * A convenience function to inset and then extrude all faces of the mesh.
    *
    * @param inset the inset factor
    * @param depth the extrusion depth
    *
    * @return this mesh
    */
   public Mesh3 insetExtrudeFaces ( final float inset, final float depth ) {

      return this.insetExtrudeFaces(inset, depth, 1.0f);
   }

   /**
    * A convenience function to inset and then extrude all faces of the mesh.
    * The taper is a scalar applied to a face's size, e.g., a taper of .75
    * would result in an extruded face 75% the size of the original.
    *
    * @param inset the inset factor
    * @param depth the extrusion depth
    * @param taper the taper
    *
    * @return this mesh
    */
   public Mesh3 insetExtrudeFaces ( final float inset, final float depth,
      final float taper ) {

      final int fsLen = this.faces.length;
      int k = 0;
      final float valInset = Utils.clamp(inset, IUtils.EPSILON, 1.0f
         - IUtils.EPSILON);
      for ( int i = 0; i < fsLen; ++i ) {
         final int fLen = this.faces[k].length;
         this.insetFace(k, valInset);
         k += fLen;
         this.extrudeFace(k, false, depth, taper);
         k += fLen + 1;
      }

      return this;
   }

   /**
    * Insets a face by calculating its center then easing from the face's
    * vertices toward the center by 0.5.
    *
    * @param faceIndex the face index
    *
    * @return the new face indices
    */
   public Mesh3 insetFace ( final int faceIndex ) {

      return this.insetFace(faceIndex, 0.5f);
   }

   /**
    * Insets a face by calculating its center then easing from the face's
    * vertices toward the center by the factor. The factor is expected to be
    * in the range [0.0, 1.0] . When it is less than 0.0, the face remains
    * unchanged; when it is greater than 1.0, the face is subdivided by
    * center.
    *
    * @param faceIndex the face index
    * @param fac       the inset amount
    *
    * @return the new face indices
    */
   @Experimental
   public Mesh3 insetFace ( final int faceIndex, final float fac ) {

      if ( fac <= 0.0f ) { return this; }
      if ( fac >= 1.0f ) { return this.subdivFaceFan(faceIndex); }

      /* Validate face index, find face. */
      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIndex, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      final int vsOldLen = this.coords.length;
      final int vtsOldLen = this.texCoords.length;
      final int vnsOldLen = this.normals.length;

      final int[][][] fsNew = new int[faceLen + 1][4][3];
      final int[][] centerFace = fsNew[faceLen] = new int[faceLen][3];

      /* Find center. */
      final Vec3 vCenter = new Vec3();
      final Vec2 vtCenter = new Vec2();
      final Vec3 vnCenter = new Vec3();
      for ( int j = 0; j < faceLen; ++j ) {

         /* Sum centers. */
         final int[] vertCurr = face[j];
         Vec3.add(vCenter, this.coords[vertCurr[0]], vCenter);
         Vec2.add(vtCenter, this.texCoords[vertCurr[1]], vtCenter);
         Vec3.add(vnCenter, this.normals[vertCurr[2]], vnCenter);
      }

      /* Average. */
      if ( faceLen > 0 ) {
         final float flInv = 1.0f / faceLen;
         Vec3.mul(vCenter, flInv, vCenter);
         Vec2.mul(vtCenter, flInv, vtCenter);
         Vec3.normalize(vnCenter, vnCenter);
      }

      final Vec3[] vsNew = new Vec3[faceLen];
      final Vec2[] vtsNew = new Vec2[faceLen];
      final Vec3[] vnsNew = new Vec3[faceLen];

      /* Find new corners. */
      final float u = 1.0f - fac;
      for ( int j = 0; j < faceLen; ++j ) {
         final int k = ( j + 1 ) % faceLen;

         final int[] vertCurr = face[j];
         final int[] vertNext = face[k];

         final int vCornerIdx = vertCurr[0];
         final int vtCornerIdx = vertCurr[1];
         final int vnCornerIdx = vertCurr[2];

         final Vec3 vCurr = this.coords[vCornerIdx];
         final Vec2 vtCurr = this.texCoords[vtCornerIdx];
         final Vec3 vnCurr = this.normals[vnCornerIdx];

         /* @formatter:off */
         vsNew[j] = new Vec3(
            u * vCurr.x + fac * vCenter.x,
            u * vCurr.y + fac * vCenter.y,
            u * vCurr.z + fac * vCenter.z);

         vtsNew[j] = new Vec2(
            u * vtCurr.x + fac * vtCenter.x,
            u * vtCurr.y + fac * vtCenter.y);

         final Vec3 vnNew = vnsNew[j] = new Vec3(
            u * vnCurr.x + fac * vnCenter.x,
            u * vnCurr.y + fac * vnCenter.y,
            u * vnCurr.z + fac * vnCenter.z);
         Vec3.normalize(vnNew, vnNew);

         final int vSubdivIdx = vsOldLen + j;
         final int vtSubdivIdx = vtsOldLen + j;
         final int vnSubdivIdx = vnsOldLen + j;

         final int[][] fNew = fsNew[j];
         final int[] n0 = fNew[0];
         n0[0] = vCornerIdx;   n0[1] = vtCornerIdx;   n0[2] = vnCornerIdx;
         final int[] n1 = fNew[1];
         n1[0] = vertNext[0];  n1[1] = vertNext[1];   n1[2] = vertNext[2];
         final int[] n2 = fNew[2];
         n2[0] = vsOldLen + k; n2[1] = vtsOldLen + k; n2[2] = vnsOldLen + k;
         final int[] n3 = fNew[3];
         n3[0] = vSubdivIdx;   n3[1] = vtSubdivIdx;   n3[2] = vnSubdivIdx;
         /* @formatter:on */

         final int[] centerVert = centerFace[j];
         centerVert[0] = vSubdivIdx;
         centerVert[1] = vtSubdivIdx;
         centerVert[2] = vnSubdivIdx;
      }

      this.coords = Vec3.concat(this.coords, vsNew);
      this.texCoords = Vec2.concat(this.texCoords, vtsNew);
      this.normals = Vec3.concat(this.normals, vnsNew);
      this.faces = Mesh.splice(this.faces, i, 1, fsNew);

      return this;
   }

   /**
    * Insets all faces in the mesh once.
    *
    * @return this mesh
    */
   public Mesh3 insetFaces ( ) { return this.insetFaces(1); }

   /**
    * Insets all faces in the mesh for a given number of iterations by a
    * factor of 0.5 .
    *
    * @param itr the iterations
    *
    * @return this mesh
    */
   public Mesh3 insetFaces ( final int itr ) {

      return this.insetFaces(itr, 0.5f);
   }

   /**
    * Insets all faces in the mesh for a given number of iterations by a
    * factor in the range [0.0, 1.0] .
    *
    * @param itr the iterations
    * @param fac the inset factor
    *
    * @return this mesh
    */
   @Experimental
   public Mesh3 insetFaces ( final int itr, final float fac ) {

      for ( int i = 0; i < itr; ++i ) {
         final int facesLen = this.faces.length;
         for ( int j = 0, k = 0; j < facesLen; ++j ) {
            final int vertLen = this.faces[k].length;
            this.insetFace(k, fac);
            k += vertLen + 1;
         }
      }
      return this;
   }

   /**
    * Returns an iterator for this mesh, which allows its faces to be accessed
    * in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public Iterator < Face3 > iterator ( ) { return new Face3Iterator(this); }

   /**
    * Gets the number of faces held by this mesh.
    *
    * @return the length
    */
   public int length ( ) { return this.faces.length; }

   /**
    * Centers the mesh about the origin, (0.0, 0.0, 0.0), and rescales it to
    * the range [-0.5, 0.5] . Emits a transform which records the mesh's
    * center point and original dimension. The transform's rotation is reset.
    *
    * @param tr the output transform
    *
    * @return this mesh
    *
    * @see Mesh3#accumMinMax(Mesh3, Vec3, Vec3)
    * @see Transform3#updateAxes()
    * @see Utils#div(float, float)
    * @see Utils#max(float, float, float)
    * @see Vec3#mul(Vec3, float, Vec3)
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    */
   public Mesh3 reframe ( final Transform3 tr ) {

      tr.locPrev.set(tr.location);
      tr.scalePrev.set(tr.scale);

      final Vec3 dim = tr.scale;
      final Vec3 lb = tr.location;
      final Vec3 ub = new Vec3(-Float.MAX_VALUE, -Float.MAX_VALUE,
         -Float.MAX_VALUE);
      lb.set(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
      Mesh3.accumMinMax(this, lb, ub);
      Vec3.sub(ub, lb, dim);

      lb.x = 0.5f * ( lb.x + ub.x );
      lb.y = 0.5f * ( lb.y + ub.y );
      lb.z = 0.5f * ( lb.z + ub.z );
      final float scl = Utils.div(1.0f, Utils.max(dim.x, dim.y, dim.z));

      final int vsLen = this.coords.length;
      for ( int i = 0; i < vsLen; ++i ) {
         final Vec3 c = this.coords[i];
         Vec3.sub(c, lb, c);
         Vec3.mul(c, scl, c);
      }

      tr.rotPrev.set(tr.rotation);
      tr.rotation.reset();
      tr.updateAxes();

      return this;
   }

   /**
    * Rotates all coordinates in the mesh by an angle around an arbitrary
    * axis.
    *
    * @param radians the angle in radians
    * @param axis    the axis of rotation
    *
    * @return this mesh
    *
    * @see Vec3#rotate(Vec3, float, float, Vec3, Vec3)
    */
   public Mesh3 rotate ( final float radians, final Vec3 axis ) {

      final double radd = radians;
      final float cosa = ( float ) Math.cos(radd);
      final float sina = ( float ) Math.sin(radd);

      final int vsLen = this.coords.length;
      for ( int i = 0; i < vsLen; ++i ) {
         final Vec3 c = this.coords[i];
         Vec3.rotate(c, cosa, sina, axis, c);
      }

      final int vnsLen = this.normals.length;
      for ( int j = 0; j < vnsLen; ++j ) {
         final Vec3 n = this.normals[j];
         Vec3.rotate(n, cosa, sina, axis, n);
      }

      return this;
   }

   /**
    * Rotates all coordinates in the mesh by a quaternion.
    *
    * @param q the quaternion
    *
    * @return the mesh
    *
    * @see Quaternion#any(Quaternion)
    * @see Quaternion#mulVector(Quaternion, Vec3, Vec3)
    */
   public Mesh3 rotate ( final Quaternion q ) {

      if ( Quaternion.any(q) ) {
         final int vsLen = this.coords.length;
         for ( int i = 0; i < vsLen; ++i ) {
            final Vec3 c = this.coords[i];
            Quaternion.mulVector(q, c, c);
         }

         final int vnsLen = this.normals.length;
         for ( int j = 0; j < vnsLen; ++j ) {
            final Vec3 n = this.normals[j];
            Quaternion.mulVector(q, n, n);
         }
      }

      return this;
   }

   /**
    * Rotates all coordinates in the mesh by an angle around the x axis.
    *
    * @param radians the angle in radians
    *
    * @return this mesh
    *
    * @see Vec3#rotateX(Vec3, float, float, Vec3)
    */
   public Mesh3 rotateX ( final float radians ) {

      final double radd = radians;
      final float cosa = ( float ) Math.cos(radd);
      final float sina = ( float ) Math.sin(radd);

      final int vsLen = this.coords.length;
      for ( int i = 0; i < vsLen; ++i ) {
         final Vec3 c = this.coords[i];
         Vec3.rotateX(c, cosa, sina, c);
      }

      final int vnsLen = this.normals.length;
      for ( int j = 0; j < vnsLen; ++j ) {
         final Vec3 n = this.normals[j];
         Vec3.rotateX(n, cosa, sina, n);
      }

      return this;
   }

   /**
    * Rotates all coordinates in the mesh by an angle around the y axis.
    *
    * @param radians the angle in radians
    *
    * @return this mesh
    *
    * @see Vec3#rotateY(Vec3, float, float, Vec3)
    */
   public Mesh3 rotateY ( final float radians ) {

      final double radd = radians;
      final float cosa = ( float ) Math.cos(radd);
      final float sina = ( float ) Math.sin(radd);

      final int vsLen = this.coords.length;
      for ( int i = 0; i < vsLen; ++i ) {
         final Vec3 c = this.coords[i];
         Vec3.rotateY(c, cosa, sina, c);
      }

      final int vnsLen = this.normals.length;
      for ( int j = 0; j < vnsLen; ++j ) {
         final Vec3 n = this.normals[j];
         Vec3.rotateY(n, cosa, sina, n);
      }

      return this;
   }

   /**
    * Rotates all coordinates in the mesh by an angle around the z axis.
    *
    * @param radians the angle in radians
    *
    * @return this mesh
    *
    * @see Vec3#rotateZ(Vec3, float, float, Vec3)
    */
   public Mesh3 rotateZ ( final float radians ) {

      final double radd = radians;
      final float cosa = ( float ) Math.cos(radd);
      final float sina = ( float ) Math.sin(radd);

      final int vsLen = this.coords.length;
      for ( int i = 0; i < vsLen; ++i ) {
         final Vec3 c = this.coords[i];
         Vec3.rotateZ(c, cosa, sina, c);
      }

      final int vnsLen = this.normals.length;
      for ( int j = 0; j < vnsLen; ++j ) {
         final Vec3 n = this.normals[j];
         Vec3.rotateZ(n, cosa, sina, n);
      }

      return this;
   }

   /**
    * Scales all coordinates in the mesh by a uniform scalar.
    *
    * @param scale the scalar
    *
    * @return this mesh
    *
    * @see Vec3#mul(Vec3, float, Vec3)
    */
   public Mesh3 scale ( final float scale ) {

      if ( scale != 0.0f ) {
         final int vsLen = this.coords.length;
         for ( int i = 0; i < vsLen; ++i ) {
            final Vec3 c = this.coords[i];
            Vec3.mul(c, scale, c);
         }
      }

      return this;
   }

   /**
    * Scales all coordinates and normals in the mesh by a non-uniform scalar.
    *
    * @param scale the vector
    *
    * @return this mesh
    *
    * @see Vec3#all(Vec3)
    * @see Vec3#hadamard(Vec3, Vec3, Vec3)
    * @see Vec3#normalize(Vec3, Vec3)
    */
   public Mesh3 scale ( final Vec3 scale ) {

      if ( Vec3.all(scale) ) {
         final int vsLen = this.coords.length;
         for ( int i = 0; i < vsLen; ++i ) {
            final Vec3 c = this.coords[i];
            Vec3.hadamard(c, scale, c);
         }

         final float xInv = 1.0f / scale.x;
         final float yInv = 1.0f / scale.y;
         final float zInv = 1.0f / scale.z;
         final int vnsLen = this.normals.length;
         for ( int j = 0; j < vnsLen; ++j ) {
            final Vec3 n = this.normals[j];
            n.set(n.x * xInv, n.y * yInv, n.z * zInv);
            Vec3.normalize(n, n);
         }
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
    *
    * @return this mesh
    */
   public Mesh3 set ( final int[][][] faces, final Vec3[] coords,
      final Vec2[] texCoords, final Vec3[] normals ) {

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
    *
    * @return this mesh
    */
   public Mesh3 set ( final Mesh2 source ) {

      /* Copy coordinates. Promote Vec2s to Vec3s. */
      final Vec2[] sourcevs = source.coords;
      final int vsLen = sourcevs.length;
      this.coords = Vec3.resize(this.coords, vsLen);
      for ( int i = 0; i < vsLen; ++i ) {
         this.coords[i].set(sourcevs[i], 0.0f);
      }

      /* Copy texture coordinates. */
      final Vec2[] sourcevts = source.texCoords;
      final int vtsLen = sourcevts.length;
      this.texCoords = Vec2.resize(this.texCoords, vtsLen);
      for ( int i = 0; i < vtsLen; ++i ) {
         this.texCoords[i].set(sourcevts[i]);
      }

      /* Append one normal. */
      this.normals = new Vec3[] { Vec3.up(new Vec3()) };

      /* Copy faces. */
      final int[][][] fsSrc = source.faces;
      final int fsSrcLen = fsSrc.length;
      this.faces = new int[fsSrcLen][][];

      for ( int i = 0; i < fsSrcLen; ++i ) {

         final int[][] fSrc = fsSrc[i];
         final int fSrcLen = fSrc.length;
         final int[][] fTrg = new int[fSrcLen][];
         this.faces[i] = fTrg;

         for ( int j = 0; j < fSrcLen; ++j ) {

            final int[] vertSrc = fSrc[j];
            final int vertSrcLen = vertSrc.length;
            final int[] vertTrg = new int[vertSrcLen + 1];
            fTrg[j] = vertTrg;
            System.arraycopy(vertSrc, 0, vertTrg, 0, vertSrcLen);
         }
      }

      this.materialIndex = source.materialIndex;
      this.name = source.name;
      return this;
   }

   /**
    * Sets this mesh to a copy of the source. Allocates new arrays for
    * coordinates, texture coordinates, normals and faces.
    *
    * @param source the source mesh
    *
    * @return this mesh
    */
   public Mesh3 set ( final Mesh3 source ) {

      /* Copy coordinates. */
      final Vec3[] vsSrc = source.coords;
      final int vsLen = vsSrc.length;
      this.coords = Vec3.resize(this.coords, vsLen);
      for ( int i = 0; i < vsLen; ++i ) { this.coords[i].set(vsSrc[i]); }

      /* Copy texture coordinates. */
      final Vec2[] vtsSrc = source.texCoords;
      final int vtsLen = vtsSrc.length;
      this.texCoords = Vec2.resize(this.texCoords, vtsLen);
      for ( int j = 0; j < vtsLen; ++j ) { this.texCoords[j].set(vtsSrc[j]); }

      /* Copy normals. */
      final Vec3[] vnsSrc = source.normals;
      final int vnsLen = vnsSrc.length;
      this.normals = Vec3.resize(this.normals, vnsLen);
      for ( int k = 0; k < vnsLen; ++k ) { this.normals[k].set(vnsSrc[k]); }

      /* Copy faces. */
      final int[][][] fsSrc = source.faces;
      final int fsSrcLen = fsSrc.length;
      this.faces = new int[fsSrcLen][][];

      for ( int i = 0; i < fsSrcLen; ++i ) {

         final int[][] fSrc = fsSrc[i];
         final int fSrcLen = fSrc.length;
         final int[][] fTrg = new int[fSrcLen][];
         this.faces[i] = fTrg;

         for ( int j = 0; j < fSrcLen; ++j ) {

            final int[] vertSrc = fSrc[j];
            final int vertSrcLen = vertSrc.length;
            final int[] vertTrg = new int[vertSrcLen];
            fTrg[j] = vertTrg;

            System.arraycopy(vertSrc, 0, vertTrg, 0, vertSrcLen);
         }
      }

      this.materialIndex = source.materialIndex;
      this.name = source.name;
      return this;
   }

   /**
    * Calculates the normals of a mesh.
    *
    * @return this mesh
    */
   public Mesh3 shade ( ) { return this.shadeFlat(); }

   /**
    * Calculates this mesh's normals per face, resulting in flat shading. If
    * the normals array is null, or if its length is not equal to the length
    * of coordinates, the normals array is reallocated. Sums the cross
    * products of edges in a face<br>
    * <br>
    * ( b - a ) x ( c - a ) <br>
    * <br>
    * then normalizes the sum.
    *
    * @return this mesh
    *
    * @see Mesh3#shadeFlat(int, int)
    */
   public Mesh3 shadeFlat ( ) {

      return this.shadeFlat(0, this.faces.length);
   }

   /**
    * Calculates this mesh's normals per vertex, resulting in smooth shading.
    * If the normals array is null, or if its length is not equal to the
    * length of coordinates, the normals array is reallocated. Uses the
    * formula<br>
    * <br>
    * ( b - a ) x ( c - a )<br>
    * <br>
    * where a, b and c are three corners of a face, then normalizes the cross
    * product.
    *
    * @return this mesh
    *
    * @see Vec3#crossNorm(Vec3, Vec3, Vec3)
    * @see Vec3#resize(Vec3[], int)
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    */
   public Mesh3 shadeSmooth ( ) {

      Vec3 prev = null;
      Vec3 curr = null;
      Vec3 next = null;
      Vec3 vn = null;

      final Vec3 edge0 = new Vec3();
      final Vec3 edge1 = new Vec3();

      final int facesLen = this.faces.length;
      this.normals = Vec3.resize(this.normals, this.coords.length);

      for ( int i = 0; i < facesLen; ++i ) {

         final int[][] f = this.faces[i];
         final int faceLen = f.length;
         prev = this.coords[f[faceLen - 1][0]];

         for ( int j = 0; j < faceLen; ++j ) {

            final int[] vert = f[j];
            final int currIndex = vert[0];
            final int nextIndex = f[ ( j + 1 ) % faceLen][0];

            /* Acquire normal and update face index reference to it. */
            vn = this.normals[currIndex];
            vert[2] = currIndex;

            curr = this.coords[currIndex];
            next = this.coords[nextIndex];

            Vec3.sub(prev, curr, edge0);
            Vec3.sub(curr, next, edge1);
            Vec3.crossNorm(edge0, edge1, vn);

            prev = curr;
         }
      }

      return this;
   }

   /**
    * Subdivides an edge by a cut at its midpoint. Does not distinguish
    * between interior edges, which have a complement elsewhere, and border
    * edges; for that reason this works best with NGONs.
    *
    * @param faceIndex the face index
    * @param edgeIndex the edge index
    *
    * @return this mesh
    */
   public Mesh3 subdivEdge ( final int faceIndex, final int edgeIndex ) {

      return this.subdivEdge(faceIndex, edgeIndex, 1);
   }

   /**
    * Subdivides an edge by the number of cuts given. For example, one cut
    * will divide an edge in half; two cuts, into thirds.<br>
    * <br>
    * Does not distinguish between interior edges, which have a complement
    * elsewhere, and border edges; for that reason this works best with NGONs.
    *
    * @param faceIndex the face index
    * @param edgeIndex the edge index
    * @param cuts      number of cuts
    *
    * @return this mesh
    *
    * @see Vec3#normalize(Vec3, Vec3)
    */
   @Experimental
   public Mesh3 subdivEdge ( final int faceIndex, final int edgeIndex,
      final int cuts ) {

      if ( cuts < 1 ) { return this; }

      /* Validate face index, find face. */
      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIndex, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      /* Find edge origin vertex. */
      final int j0 = Utils.mod(edgeIndex, faceLen);
      final int[] vert0Idx = face[j0];
      final Vec3 vOrig = this.coords[vert0Idx[0]];
      final Vec2 vtOrig = this.texCoords[vert0Idx[1]];
      final Vec3 vnOrig = this.normals[vert0Idx[2]];

      /* Find edge destination vertex. */
      final int j1 = ( j0 + 1 ) % faceLen;
      final int[] vert1Idx = face[j1];
      final Vec3 vDest = this.coords[vert1Idx[0]];
      final Vec2 vtDest = this.texCoords[vert1Idx[1]];
      final Vec3 vnDest = this.normals[vert1Idx[2]];

      /*
       * Cache old length of coordinates and texture coordinates so new ones can
       * be appended to the end.
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
       * Subdivide the edge. The edge origin and destination are to be excluded
       * from the new set, so the conversion to the step accounts for this.
       */
      final float toStep = 1.0f / ( cuts + 1.0f );
      for ( int k = 0; k < cuts; ++k ) {
         final float step = toStep + k * toStep;
         final float u = 1.0f - step;

         vsNew[k] = new Vec3(u * vOrig.x + step * vDest.x, u * vOrig.y + step
            * vDest.y, u * vOrig.z + step * vDest.z);
         vtsNew[k] = new Vec2(u * vtOrig.x + step * vtDest.x, u * vtOrig.y
            + step * vtDest.y);
         final Vec3 vn = vnsNew[k] = new Vec3(u * vnOrig.x + step * vnDest.x, u
            * vnOrig.y + step * vnDest.y, u * vnOrig.z + step * vnDest.z);
         Vec3.normalize(vn, vn);

         final int[] fNew = fsNew[k];
         fNew[0] = vsOldLen + k;
         fNew[1] = vtsOldLen + k;
         fNew[2] = vnsOldLen + k;
      }

      /*
       * Append new coordinates, texture coordinates and normals to the end of
       * their respective arrays. The new faces need to be inserted to object's
       * faces array, not reassigned to local face array.
       */
      this.coords = Vec3.concat(this.coords, vsNew);
      this.texCoords = Vec2.concat(this.texCoords, vtsNew);
      this.normals = Vec3.concat(this.normals, vnsNew);
      this.faces[i] = Mesh.insert(face, j1, fsNew);

      return this;
   }

   /**
    * Subdivides a convex face. Defaults to center-based subdivision.
    *
    * @param faceIdx the face index
    *
    * @return the new face indices
    */
   @Experimental
   public Mesh3 subdivFace ( final int faceIdx ) {

      return this.subdivFaceCenter(faceIdx);
   }

   /**
    * Subdivides a convex face by calculating its center, subdividing each of
    * its edges with one cut to create a midpoint, then connecting the
    * midpoints to the center. This generates a quadrilateral for the number
    * of edges in the face.
    *
    * @param faceIdx the face index
    *
    * @return the new face indices
    */
   @Experimental
   public Mesh3 subdivFaceCenter ( final int faceIdx ) {

      /* Validate face index, find face. */
      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIdx, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      /*
       * Cache old length of coordinates and texture coordinates so new ones can
       * be appended to the end.
       */
      final int vsOldLen = this.coords.length;
      final int vtsOldLen = this.texCoords.length;
      final int vnsOldLen = this.normals.length;

      /* Create arrays to hold new data. */
      final Vec3[] vsNew = new Vec3[faceLen + 1];
      final Vec2[] vtsNew = new Vec2[vsNew.length];
      final Vec3[] vnsNew = new Vec3[vsNew.length];
      final int[][][] fsNew = new int[faceLen][4][3];

      /* Center is last element of new array. */
      final Vec3 vCenter = vsNew[faceLen] = new Vec3();
      final Vec2 vtCenter = vtsNew[faceLen] = new Vec2();
      final Vec3 vnCenter = vnsNew[faceLen] = new Vec3();

      final int vCenterIdx = vsOldLen + faceLen;
      final int vtCenterIdx = vtsOldLen + faceLen;
      final int vnCenterIdx = vnsOldLen + faceLen;

      for ( int j = 0; j < faceLen; ++j ) {
         final int k = ( j + 1 ) % faceLen;

         final int[] vertCurr = face[j];
         final int[] vertNext = face[k];

         final Vec3 vCurr = this.coords[vertCurr[0]];
         final Vec2 vtCurr = this.texCoords[vertCurr[1]];
         final Vec3 vnCurr = this.normals[vertCurr[2]];

         /* Sum vertex for center. */
         Vec3.add(vCenter, vCurr, vCenter);
         Vec2.add(vtCenter, vtCurr, vtCenter);
         Vec3.add(vnCenter, vnCurr, vnCenter);

         /* @formatter:off */
         final int vNextIdx = vertNext[0];
         final Vec3 vNext = this.coords[vNextIdx];
         vsNew[j] = new Vec3(
            ( vCurr.x + vNext.x ) * 0.5f,
            ( vCurr.y + vNext.y ) * 0.5f,
            ( vCurr.z + vNext.z ) * 0.5f);

         final int vtNextIdx = vertNext[1];
         final Vec2 vtNext = this.texCoords[vtNextIdx];
         vtsNew[j] = new Vec2(
            ( vtCurr.x + vtNext.x ) * 0.5f,
            ( vtCurr.y + vtNext.y ) * 0.5f);

         /* Multiply by 0.5 removed because normalize takes care of it. */
         final int vnNextIdx = vertNext[2];
         vnsNew[j] = Vec3.addNorm(vnCurr, this.normals[vnNextIdx], new Vec3());

         final int[][] fNew = fsNew[j];
         final int[] n0 = fNew[0];
         n0[0] = vCenterIdx;   n0[1] = vtCenterIdx;   n0[2] = vnCenterIdx;
         final int[] n1 = fNew[1];
         n1[0] = vsOldLen + j; n1[1] = vtsOldLen + j; n1[2] = vnsOldLen + j;
         final int[] n2 = fNew[2];
         n2[0] = vNextIdx;     n2[1] = vtNextIdx;     n2[2] = vnNextIdx;
         final int[] n3 = fNew[3];
         n3[0] = vsOldLen + k; n3[1] = vtsOldLen + k; n3[2] = vnsOldLen + k;
         /* @formatter:on */
      }

      if ( faceLen > 0 ) {
         final float flInv = 1.0f / faceLen;
         Vec3.mul(vCenter, flInv, vCenter);
         Vec2.mul(vtCenter, flInv, vtCenter);
         Vec3.normalize(vnCenter, vnCenter);
      }

      this.coords = Vec3.concat(this.coords, vsNew);
      this.texCoords = Vec2.concat(this.texCoords, vtsNew);
      this.normals = Vec3.concat(this.normals, vnsNew);
      this.faces = Mesh.splice(this.faces, i, 1, fsNew);

      return this;
   }

   /**
    * Subdivides a convex face by calculating its center, then connecting its
    * vertices to the center. This generates a triangle for the number of
    * edges in the face.
    *
    * @param faceIdx the face index
    *
    * @return the new face indices
    */
   @Experimental
   public Mesh3 subdivFaceFan ( final int faceIdx ) {

      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIdx, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      final int[][][] fsNew = new int[faceLen][3][3];
      final Vec3 vCenter = new Vec3();
      final Vec2 vtCenter = new Vec2();
      final Vec3 vnCenter = new Vec3();

      final int vCenterIdx = this.coords.length;
      final int vtCenterIdx = this.texCoords.length;
      final int vnCenterIdx = this.normals.length;

      for ( int j = 0; j < faceLen; ++j ) {
         final int k = ( j + 1 ) % faceLen;

         final int[] vertCurr = face[j];
         final int[] vertNext = face[k];

         final int vCurrIdx = vertCurr[0];
         final int vtCurrIdx = vertCurr[1];
         final int vnCurrIdx = vertCurr[2];

         /* Sum vertex for center. */
         Vec3.add(vCenter, this.coords[vCurrIdx], vCenter);
         Vec2.add(vtCenter, this.texCoords[vtCurrIdx], vtCenter);
         Vec3.add(vnCenter, this.normals[vnCurrIdx], vnCenter);

         /* @formatter:off */
         final int[][] fNew = fsNew[j];
         final int[] n0 = fNew[0];
         n0[0] = vCenterIdx;  n0[1] = vtCenterIdx; n0[2] = vnCenterIdx;
         final int[] n1 = fNew[1];
         n1[0] = vCurrIdx;    n1[1] = vtCurrIdx;   n1[2] = vnCurrIdx;
         final int[] n2 = fNew[2];
         n2[0] = vertNext[0]; n2[1] = vertNext[1]; n2[2] = vertNext[2];
         /* @formatter:on */
      }

      if ( faceLen > 0 ) {
         final float flInv = 1.0f / faceLen;
         Vec3.mul(vCenter, flInv, vCenter);
         Vec2.mul(vtCenter, flInv, vtCenter);
         Vec3.normalize(vnCenter, vnCenter);
      }

      this.coords = Vec3.append(this.coords, vCenter);
      this.texCoords = Vec2.append(this.texCoords, vtCenter);
      this.normals = Vec3.append(this.normals, vnCenter);
      this.faces = Mesh.splice(this.faces, i, 1, fsNew);

      return this;
   }

   /**
    * Subdivides a convex face by cutting each of its edges once to create a
    * midpoint, then connecting each midpoint. This generates peripheral
    * triangles and a new central face with the same number of edges as the
    * original. This is best suited to meshes made of triangles.
    *
    * @param faceIdx the face index
    *
    * @return the new face indices
    */
   @Experimental
   public Mesh3 subdivFaceInscribe ( final int faceIdx ) {

      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIdx, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      final int vsOldLen = this.coords.length;
      final int vtsOldLen = this.texCoords.length;
      final int vnsOldLen = this.normals.length;

      final Vec3[] vsNew = new Vec3[faceLen];
      final Vec2[] vtsNew = new Vec2[faceLen];
      final Vec3[] vnsNew = new Vec3[faceLen];
      final int[][][] fsNew = new int[faceLen + 1][3][3];
      final int[][] centerFace = fsNew[faceLen] = new int[faceLen][3];

      for ( int j = 0; j < faceLen; ++j ) {
         final int[] vertCurr = face[j];
         final Vec3 vCurr = this.coords[vertCurr[0]];
         final Vec2 vtCurr = this.texCoords[vertCurr[1]];

         final int k = ( j + 1 ) % faceLen;
         final int[] vertNext = face[k];

         /* @formatter:off */
         final int vNextIdx = vertNext[0];
         final Vec3 vNext = this.coords[vNextIdx];
         vsNew[j] = new Vec3(
            ( vCurr.x + vNext.x ) * 0.5f,
            ( vCurr.y + vNext.y ) * 0.5f,
            ( vCurr.z + vNext.z ) * 0.5f);

         final int vtNextIdx = vertNext[1];
         final Vec2 vtNext = this.texCoords[vtNextIdx];
         vtsNew[j] = new Vec2(
            ( vtCurr.x + vtNext.x ) * 0.5f,
            ( vtCurr.y + vtNext.y ) * 0.5f);

         /* Multiply by 0.5 removed because normalize takes care of it. */
         final int vnNextIdx = vertNext[2];
         vnsNew[j] = Vec3.addNorm(
            this.normals[vertCurr[2]],
            this.normals[vnNextIdx],
            new Vec3());

         final int vSubdivIdx = vsOldLen + j;
         final int vtSubdivIdx = vtsOldLen + j;
         final int vnSubdivIdx = vnsOldLen + j;

         /* Update peripheral face. */
         final int[][] fNew = fsNew[j];
         final int[] n0 = fNew[0];
         n0[0] = vSubdivIdx;   n0[1] = vtSubdivIdx;   n0[2] = vnSubdivIdx;
         final int[] n1 = fNew[1];
         n1[0] = vNextIdx;     n1[1] = vtNextIdx;     n1[2] = vnNextIdx;
         final int[] n2 = fNew[2];
         n2[0] = vsOldLen + k; n2[1] = vtsOldLen + k; n2[2] = vnsOldLen + k;
         /* @formatter:on */

         /* Update vertex of central face. */
         final int[] centerVert = centerFace[j];
         centerVert[0] = vSubdivIdx;
         centerVert[1] = vtSubdivIdx;
         centerVert[2] = vnSubdivIdx;
      }

      this.coords = Vec3.concat(this.coords, vsNew);
      this.texCoords = Vec2.concat(this.texCoords, vtsNew);
      this.normals = Vec3.concat(this.normals, vnsNew);
      this.faces = Mesh.splice(this.faces, i, 1, fsNew);

      return this;
   }

   /**
    * Subdivides all faces in the mesh once.
    *
    * @return this mesh
    */
   public Mesh3 subdivFaces ( ) { return this.subdivFaces(1); }

   /**
    * Subdivides all faces in the mesh by a number of iterations.
    *
    * @param itr iterations
    *
    * @return this mesh
    */
   public Mesh3 subdivFaces ( final int itr ) {

      return this.subdivFacesCenter(itr);
   }

   /**
    * Subdivides all faces in the mesh by a number of iterations. Uses the
    * center method.
    *
    * @param itr iterations
    *
    * @return this mesh
    *
    * @see Mesh3#subdivFaceCenter(int)
    */
   public Mesh3 subdivFacesCenter ( final int itr ) {

      for ( int i = 0; i < itr; ++i ) {
         int k = 0;
         final int len = this.faces.length;
         for ( int j = 0; j < len; ++j ) {
            final int vertLen = this.faces[k].length;
            this.subdivFaceCenter(k);
            k += vertLen;
         }
      }
      return this;
   }

   /**
    * Subdivides all faces in the mesh by a number of iterations. Uses the
    * triangle fan method.
    *
    * @param itr iterations
    *
    * @return this mesh
    *
    * @see Mesh3#subdivFaceFan(int)
    */
   public Mesh3 subdivFacesFan ( final int itr ) {

      for ( int i = 0; i < itr; ++i ) {
         int k = 0;
         final int len = this.faces.length;
         for ( int j = 0; j < len; ++j ) {
            final int vertLen = this.faces[k].length;
            this.subdivFaceFan(k);
            k += vertLen;
         }
      }
      return this;
   }

   /**
    * Subdivides all faces in the mesh by a number of iterations. Uses the
    * inscription method.
    *
    * @param itr iterations
    *
    * @return this mesh
    *
    * @see Mesh3#subdivFaceInscribe(int)
    */
   public Mesh3 subdivFacesInscribe ( final int itr ) {

      for ( int i = 0; i < itr; ++i ) {
         int k = 0;
         final int len = this.faces.length;
         for ( int j = 0; j < len; ++j ) {
            final int vertLen = this.faces[k].length;
            this.subdivFaceInscribe(k);
            k += vertLen + 1;
         }
      }
      return this;
   }

   /**
    * Renders the mesh as a string following the Wavefront OBJ file format.
    *
    * @return the string
    */
   public String toObjString ( ) {

      return this.toObjString(1, 1, 1, 0, true);
   }

   /**
    * Renders the mesh as a string following the Wavefront OBJ file format.
    * The index offsets specify where the mesh's data begin; OBJ file indices
    * begin at 1, not 0. The mesh is considered a group, 'g', not an object,
    * 'o'.
    *
    * @param vIdx          coordinate index offset
    * @param vtIdx         texture coordinate index offset
    * @param vnIdx         normal index offset
    * @param smoothShading smooth shading flag
    * @param flipvs        whether to subtract y from 1.0
    *
    * @return the string
    */
   public String toObjString ( final int vIdx, final int vtIdx, final int vnIdx,
      final int smoothShading, final boolean flipvs ) {

      return this.toObjString(new StringBuilder(1024), vIdx, vtIdx, vnIdx,
         smoothShading, flipvs).toString();
   }

   /**
    * Centers the mesh about the origin, (0.0, 0.0, 0.0), by calculating its
    * dimensions then subtracting the center point. Emits a transform which
    * records the mesh's center point. The transform's rotation and scale are
    * reset.
    *
    * @param tr the output transform
    *
    * @return this mesh
    *
    * @see Mesh3#translate(Vec3)
    */
   public Mesh3 toOrigin ( final Transform3 tr ) {

      final Vec3 lb = new Vec3(Float.MAX_VALUE, Float.MAX_VALUE,
         Float.MAX_VALUE);
      final Vec3 ub = new Vec3(-Float.MAX_VALUE, -Float.MAX_VALUE,
         -Float.MAX_VALUE);
      Mesh3.accumMinMax(this, lb, ub);

      lb.x = -0.5f * ( lb.x + ub.x );
      lb.y = -0.5f * ( lb.y + ub.y );
      lb.z = -0.5f * ( lb.z + ub.z );
      this.translate(lb);

      tr.locPrev.set(tr.location);
      Vec3.negate(lb, tr.location);

      tr.scaleTo(1.0f);

      tr.rotPrev.set(tr.rotation);
      tr.rotation.reset();
      tr.updateAxes();

      return this;
   }

   /**
    * Writes the mesh to a byte array in the stl format. The mesh should be
    * triangulated prior to calling this method.
    *
    * @return the byte array
    */
   public byte[] toStlBytes ( ) {

      int sum = 0;
      final int facesLen = this.faces.length;
      for ( int i = 0; i < facesLen; ++i ) { sum += this.faces[i].length; }

      return this.toStlBytes(new byte[84 + facesLen * 14 + sum * 12], 80);
   }

   /**
    * Writes the mesh to an existing byte array at an offset in the stl
    * format. Does not write the initial 80 byte header, so the default offset
    * would be 80. The mesh should be triangulated prior to calling this
    * method.
    *
    * @param arr    the byte array
    * @param offset the offset
    *
    * @return the byte array
    */
   public byte[] toStlBytes ( final byte[] arr, final int offset ) {

      int cursor = offset;
      final int facesLen = this.faces.length;
      Utils.byteslm(facesLen, arr, cursor);
      cursor += 4;

      Vec3 prev = null;
      Vec3 curr = null;
      Vec3 next = null;

      final Vec3 edge0 = new Vec3();
      final Vec3 edge1 = new Vec3();
      final Vec3 cross = new Vec3();
      final Vec3 vn = new Vec3();

      for ( int i = 0; i < facesLen; ++i ) {

         final int[][] face = this.faces[i];
         final int faceLen = face.length;
         final int fl12 = faceLen * 12;
         prev = this.coords[face[faceLen - 1][0]];
         vn.reset();

         for ( int j = 0; j < faceLen; ++j ) {
            curr = this.coords[face[j][0]];
            next = this.coords[face[ ( j + 1 ) % faceLen][0]];

            Vec3.sub(prev, curr, edge0);
            Vec3.sub(curr, next, edge1);
            Vec3.cross(edge0, edge1, cross);
            Vec3.add(vn, cross, vn);

            final int cursorLocal = cursor + 12 * ( j + 1 );
            Utils.byteslm(curr.x, arr, cursorLocal);
            Utils.byteslm(curr.y, arr, cursorLocal + 4);
            Utils.byteslm(curr.z, arr, cursorLocal + 8);

            prev = curr;
         }

         Vec3.normalize(vn, vn);

         Utils.byteslm(vn.x, arr, cursor);
         Utils.byteslm(vn.y, arr, cursor + 4);
         Utils.byteslm(vn.z, arr, cursor + 8);

         arr[cursor + 12 + fl12] = 0;
         arr[cursor + 13 + fl12] = 0;

         cursor += 14 + fl12;
      }

      return arr;
   }

   /**
    * Returns a string representation of the mesh.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

   /**
    * Returns a string representation of the mesh. Includes an option to
    * truncate the listing in case of large meshes.
    *
    * @param places the number of places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      return this.toString(new StringBuilder(1024), places).toString();
   }

   /**
    * Transforms all coordinates and normals in the mesh by a matrix.
    * Calculates the matrix inverse in order to transform normals.
    *
    * @param m the matrix
    * @param h the matrix inverse
    *
    * @return this mesh
    *
    * @see Mat4#inverse(Mat4, Mat4)
    * @see Mat4#mulNormal(Vec3, Mat4, Mat4, Vec3)
    * @see Mat4#mulPoint(Mat4, Vec3, Vec3)
    */
   public Mesh3 transform ( final Mat4 m, final Mat4 h ) {

      final int vsLen = this.coords.length;
      for ( int i = 0; i < vsLen; ++i ) {
         final Vec3 c = this.coords[i];
         Mat4.mulPoint(m, c, c);
      }

      Mat4.inverse(m, h);
      final int vnsLen = this.normals.length;
      for ( int j = 0; j < vnsLen; ++j ) {
         final Vec3 n = this.normals[j];
         Mat4.mulNormal(n, m, h, n);
      }

      return this;
   }

   /**
    * Transforms all coordinates and normals in the mesh <em>permanently</em>
    * by a transform.<br>
    * <br>
    * Not to be confused with the <em>temporary</em> transformations applied
    * by a mesh entity's transform to the meshes contained within the
    * entity.<br>
    * <br>
    * Useful when consolidating multiple mesh entities into one mesh entity.
    *
    * @param tr the transform
    *
    * @return this mesh
    *
    * @see Transform3#mulNormal(Transform3, Vec3, Vec3)
    * @see Transform3#mulPoint(Transform3, Vec3, Vec3)
    */
   public Mesh3 transform ( final Transform3 tr ) {

      // TODO: Test this. It may no longer have parity with the Mat3 transform
      // function above. In which case, it'd need to use the same transform
      // order as it used to. Or... just don't worry about them being different.

      final int vsLen = this.coords.length;
      for ( int i = 0; i < vsLen; ++i ) {
         final Vec3 c = this.coords[i];
         Transform3.mulPoint(tr, c, c);
      }

      final int vnsLen = this.normals.length;
      for ( int j = 0; j < vnsLen; ++j ) {
         final Vec3 n = this.normals[j];
         Transform3.mulNormal(tr, n, n);
      }

      return this;
   }

   /**
    * Translates all coordinates in the mesh by a vector.
    *
    * @param v the vector
    *
    * @return this mesh
    *
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   public Mesh3 translate ( final Vec3 v ) {

      final int vsLen = this.coords.length;
      for ( int i = 0; i < vsLen; ++i ) {
         final Vec3 c = this.coords[i];
         Vec3.add(c, v, c);
      }

      return this;
   }

   /**
    * An internal helper function to format a mesh in Python, then append it
    * to a {@link StringBuilder}.
    *
    * @param pyCd           the string builder
    * @param includeEdges   whether to include edge index data
    * @param includeUvs     whether or not to include UVs
    * @param includeNormals whether or not to include normals
    *
    * @return the string builder
    */
   @Experimental
   StringBuilder toBlenderCode ( final StringBuilder pyCd,
      final boolean includeEdges, final boolean includeUvs,
      final boolean includeNormals ) {

      final int vsLen = this.coords.length;
      final int vsLast = vsLen - 1;
      final int facesLen = this.faces.length;
      final int facesLast = facesLen - 1;

      pyCd.append("{\"name\": \"");
      pyCd.append("id");
      pyCd.append(this.name);
      pyCd.append("\", \"material_index\": ");
      pyCd.append(this.materialIndex);
      pyCd.append(", \"vertices\": [");

      for ( int i = 0; i < vsLast; ++i ) {
         this.coords[i].toBlenderCode(pyCd);
         pyCd.append(',').append(' ');
      }
      this.coords[vsLast].toBlenderCode(pyCd);

      if ( includeEdges ) {

         /*
          * Edges should be undirected, not directed, e.g., (0, 1) and (1, 0)
          * should be treated as equivalent.
          */

         final ArrayList < String > edgesList = new ArrayList <>();
         for ( int j = 0; j < facesLen; ++j ) {
            final int[][] vrtInd = this.faces[j];
            final int vrtIndLen = vrtInd.length;
            for ( int k = 0; k < vrtIndLen; ++k ) {
               final int orig = vrtInd[k][0];
               final int dest = vrtInd[ ( k + 1 ) % vrtIndLen][0];

               final String query = orig + ", " + dest;
               final String reverse = dest + ", " + orig;

               if ( !edgesList.contains(query) && !edgesList.contains(
                  reverse) ) {
                  // edgesList.add(origin < dest ? query : reverse);
                  edgesList.add(query);
               }
            }
         }

         pyCd.append("], \"edges\": [");
         final Iterator < String > edgesItr = edgesList.iterator();
         while ( edgesItr.hasNext() ) {
            pyCd.append('(');
            pyCd.append(edgesItr.next());
            pyCd.append(')');
            if ( edgesItr.hasNext() ) { pyCd.append(',').append(' '); }
         }
      }

      pyCd.append("], \"faces\": [");
      for ( int j = 0; j < facesLen; ++j ) {
         final int[][] vrtInd = this.faces[j];
         final int vrtIndLen = vrtInd.length;
         final int vrtLast = vrtIndLen - 1;

         pyCd.append('(');
         for ( int k = 0; k < vrtLast; ++k ) {
            pyCd.append(vrtInd[k][0]);
            pyCd.append(',').append(' ');
         }
         pyCd.append(vrtInd[vrtLast][0]);
         pyCd.append(')');

         if ( j < facesLast ) { pyCd.append(',').append(' '); }
      }

      if ( includeUvs ) {
         final int vtsLen = this.texCoords.length;
         final int vtsLast = vtsLen - 1;

         pyCd.append("], \"uvs\": [");
         for ( int h = 0; h < vtsLast; ++h ) {
            this.texCoords[h].toBlenderCode(pyCd, true);
            pyCd.append(',').append(' ');
         }
         this.texCoords[vtsLast].toBlenderCode(pyCd, true);

         pyCd.append("], \"uv_indices\": [");
         for ( int j = 0; j < facesLen; ++j ) {
            final int[][] vrtInd = this.faces[j];
            final int vrtIndLen = vrtInd.length;
            final int vrtLast = vrtIndLen - 1;

            pyCd.append('(');
            for ( int k = 0; k < vrtLast; ++k ) {
               pyCd.append(vrtInd[k][1]);
               pyCd.append(',').append(' ');
            }
            pyCd.append(vrtInd[vrtLast][1]);
            pyCd.append(')');

            if ( j < facesLast ) { pyCd.append(',').append(' '); }
         }
      }

      if ( includeNormals ) {
         pyCd.append("], \"normals\": [");
         final int vnsLen = this.normals.length;
         final int vnsLast = vnsLen - 1;
         for ( int h = 0; h < vnsLast; ++h ) {
            this.normals[h].toBlenderCode(pyCd);
            pyCd.append(',').append(' ');
         }
         this.normals[vnsLast].toBlenderCode(pyCd);

         pyCd.append("], \"normal_indices\": [");
         for ( int j = 0; j < facesLen; ++j ) {
            final int[][] vrtInd = this.faces[j];
            final int vrtIndLen = vrtInd.length;
            final int vrtLast = vrtIndLen - 1;

            pyCd.append('(');
            for ( int k = 0; k < vrtIndLen; ++k ) {
               pyCd.append(vrtInd[k][2]);
               if ( k < vrtLast ) { pyCd.append(',').append(' '); }
            }
            pyCd.append(')');

            if ( j < facesLast ) { pyCd.append(',').append(' '); }
         }
      }

      pyCd.append(']').append('}');

      return pyCd;
   }

   /**
    * Internal helper method that appends a representation of this mesh in the
    * Wavefront OBJ file format to a {@link StringBuilder}.
    *
    * @param objs          the string builder
    * @param vIdx          coordinate index offset
    * @param vtIdx         texture coordinate index offset
    * @param vnIdx         normal index offset
    * @param smoothShading smooth shading flag
    * @param flipvs        whether to subtract y from 1.0
    *
    * @return the string builder
    */
   StringBuilder toObjString ( final StringBuilder objs, final int vIdx,
      final int vtIdx, final int vnIdx, final int smoothShading,
      final boolean flipvs ) {

      final int vsLen = this.coords.length;
      final int vtsLen = this.texCoords.length;
      final int vnsLen = this.normals.length;
      final int facesLen = this.faces.length;

      /*
       * Append a comment listing the number of coordinates, texture
       * coordinates, normals and faces.
       */
      objs.append("\n# v: ");
      objs.append(vsLen);
      objs.append(", vt: ");
      objs.append(vtsLen);
      objs.append(", vn: ");
      objs.append(vnsLen);
      objs.append(", f: ");
      objs.append(facesLen);
      objs.append('\n');

      /* Append group followed by name. */
      objs.append('g');
      objs.append(' ');
      objs.append(this.name);
      objs.append('\n');
      objs.append('\n');

      /* Write coordinates. */
      for ( int i = 0; i < vsLen; ++i ) {
         objs.append('v');
         objs.append(' ');
         this.coords[i].toObjString(objs);
         objs.append('\n');
      }
      objs.append('\n');

      /* Write texture coordinates. */
      for ( int i = 0; i < vtsLen; ++i ) {
         objs.append("vt ");
         this.texCoords[i].toObjString(objs, flipvs);
         objs.append('\n');
      }
      objs.append('\n');

      /* Write normals. */
      for ( int i = 0; i < vnsLen; ++i ) {
         objs.append("vn ");
         this.normals[i].toObjString(objs);
         objs.append('\n');
      }
      objs.append('\n');

      /* Write smooth/flat normals. */
      objs.append("s ");
      objs.append(smoothShading > 0 ? smoothShading : "off");
      objs.append("\n\n");

      /* Write face indices. */
      final int facesLast = facesLen - 1;
      for ( int i = 0; i < facesLen; ++i ) {

         final int[][] face = this.faces[i];
         final int vLen = face.length;
         objs.append('f');
         objs.append(' ');

         for ( int j = 0; j < vLen; ++j ) {
            final int[] vert = face[j];
            objs.append(vert[0] + vIdx);
            objs.append('/');
            objs.append(vert[1] + vtIdx);
            objs.append('/');
            objs.append(vert[2] + vnIdx);
            objs.append(' ');
         }

         if ( i < facesLast ) { objs.append('\n'); }
      }

      return objs;
   }

   /**
    * Internal helper function to assist with methods that need to print many
    * meshes. Appends to an existing {@link StringBuilder}.
    *
    * @param sb     the string builder
    * @param places the number of places
    *
    * @return the string builder
    */
   StringBuilder toString ( final StringBuilder sb, final int places ) {

      sb.append("{ name: \"");
      sb.append(this.name);
      sb.append("\", materialIndex: ");
      sb.append(this.materialIndex);
      sb.append(", coords: ");
      Vec3.toString(sb, this.coords, places);
      sb.append(", texCoords: ");
      Vec2.toString(sb, this.texCoords, places);
      sb.append(", normals: ");
      Vec3.toString(sb, this.normals, places);

      sb.append(", faces: [ ");
      if ( this.faces != null ) {
         final int facesLen = this.faces.length;
         final int facesLast = facesLen - 1;

         for ( int i = 0; i < facesLen; ++i ) {

            final int[][] verts = this.faces[i];
            final int vertsLen = verts.length;
            final int vertsLast = vertsLen - 1;
            sb.append('[').append(' ');

            for ( int j = 0; j < vertsLen; ++j ) {

               final int[] vert = verts[j];
               final int infoLen = vert.length;
               final int infoLast = infoLen - 1;
               sb.append('[').append(' ');

               /* 3 indices: coordinate, texture coordinate & normal. */
               for ( int k = 0; k < infoLen; ++k ) {
                  sb.append(vert[k]);
                  if ( k < infoLast ) { sb.append(',').append(' '); }
               }

               sb.append(' ').append(']');
               if ( j < vertsLast ) { sb.append(',').append(' '); }
            }
            sb.append(' ').append(']');
            if ( i < facesLast ) { sb.append(',').append(' '); }
         }
      }

      sb.append(" ] }");
      return sb;
   }

   /**
    * Tests this mesh for equivalence with another.
    *
    * @param mesh3 the mesh
    *
    * @return the evaluation
    */
   protected boolean equals ( final Mesh3 mesh3 ) {

      return Arrays.equals(this.coords, mesh3.coords) && Arrays.deepEquals(
         this.faces, mesh3.faces);
   }

   /**
    * Internal helper function to calculate flat shading for a number of faces
    * starting at an index. Assumes that index and count are valid. If the
    * normals array is null, or if its length is not equal to the length of
    * coordinates, the normals array is reallocated. Sums the cross products
    * of edges in a face<br>
    * <br>
    * ( b - a ) x ( c - a ) <br>
    * <br>
    * then normalizes the sum.<br>
    * <br>
    * Needed because {@link Mesh3#extrudeFace(int, boolean, float, float)}
    * calculates normals for side panels of an extruded face.
    *
    * @return this mesh
    *
    * @see Vec3#add(Vec3, Vec3, Vec3)
    * @see Vec3#concat(Vec3[], Vec3)
    * @see Vec3#cross(Vec3, Vec3, Vec3)
    * @see Vec3#normalize(Vec3, Vec3)
    * @see Vec3#resize(Vec3[], int)
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    */
   protected Mesh3 shadeFlat ( final int faceIdx, final int count ) {

      final int facesLen = this.faces.length;
      final boolean reassign = count >= facesLen;

      Vec3[] vns;
      int idxOffset;
      if ( reassign ) {
         vns = this.normals = Vec3.resize(this.normals, count);
         idxOffset = 0;
      } else {
         vns = new Vec3[count];
         idxOffset = this.normals.length;
      }

      Vec3 prev = null;
      Vec3 curr = null;
      Vec3 next = null;
      Vec3 vn = null;

      final Vec3 edge0 = new Vec3();
      final Vec3 edge1 = new Vec3();
      final Vec3 cross = new Vec3();

      for ( int i = 0; i < count; ++i ) {

         final int[][] f = this.faces[faceIdx + i];
         final int faceLen = f.length;
         final int newIdx = idxOffset + i;
         prev = this.coords[f[faceLen - 1][0]];

         if ( reassign ) {
            vn = vns[i];
            vn.reset();
         } else {
            vn = vns[i] = new Vec3();
         }

         for ( int j = 0; j < faceLen; ++j ) {

            final int[] vert = f[j];
            curr = this.coords[vert[0]];
            next = this.coords[f[ ( j + 1 ) % faceLen][0]];

            Vec3.sub(prev, curr, edge0);
            Vec3.sub(curr, next, edge1);
            Vec3.cross(edge0, edge1, cross);
            Vec3.add(vn, cross, vn);

            vert[2] = newIdx;
            prev = curr;
         }

         /* Normalization takes care of averaging. */
         Vec3.normalize(vn, vn);
      }

      if ( !reassign ) { this.normals = Vec3.concat(this.normals, vns); }

      return this;
   }

   /**
    * Default cube size, such that it will match the dimensions of other
    * Platonic solids; <code>0.5d / Math.sqrt(2.0d)</code> , approximately
    * {@value Mesh3#DEFAULT_CUBE_SIZE} .
    */
   public static final float DEFAULT_CUBE_SIZE = 0.35355338f;

   /**
    * The default texture coordinate (UV) profile to use when making a cube.
    */
   public static final UvProfile.Cube DEFAULT_CUBE_UV_PROFILE
      = UvProfile.Cube.PER_FACE;

   /**
    * Type of polygon to draw when it is not supplied to the polygon function.
    */
   public static final PolyType DEFAULT_POLY_TYPE = PolyType.TRI;

   /**
    * Default number of subdivision iterations to use when creating a sphere
    * from a cube or icosahedron.
    */
   public static final int DEFAULT_SPHERE_ITR = 3;

   /**
    * Calculates an Axis-Aligned Bounding Box (AABB) encompassing the mesh.
    *
    * @param mesh   the mesh
    * @param target the output dimensions
    *
    * @return the dimensions
    */
   public static Bounds3 calcBounds ( final Mesh3 mesh, final Bounds3 target ) {

      target.set(Float.MAX_VALUE, -Float.MAX_VALUE);
      Mesh3.accumMinMax(mesh, target.min, target.max);
      return target;
   }

   /**
    * Casts all vertices and normals of this mesh to a sphere.
    *
    * @param source the source mesh
    * @param radius the radius
    * @param target the output mesh
    *
    * @return the spherical mesh
    */
   public static Mesh3 castToSphere ( final Mesh3 source, final float radius,
      final Mesh3 target ) {

      final float vrad = Utils.max(IUtils.EPSILON, radius);
      final boolean neq = source != target;

      final int[][][] fsSrc = source.faces;
      final Vec3[] vsSrc = source.coords;
      final int fsSrcLen = fsSrc.length;
      final int vsSrcLen = vsSrc.length;

      final Vec3[] vns = target.normals = Vec3.resize(target.normals, vsSrcLen);
      final Vec3[] vsTrg = neq ? target.coords = Vec3.resize(target.coords,
         vsSrcLen) : vsSrc;
      for ( int i = 0; i < vsSrcLen; ++i ) {
         final Vec3 vn = vns[i];
         Vec3.normalize(vsSrc[i], vn);
         Vec3.mul(vn, vrad, vsTrg[i]);
      }

      if ( neq ) {

         // TEST
         final int[][][] fsTrg = target.faces = new int[fsSrcLen][][];
         for ( int i = 0; i < fsSrcLen; ++i ) {
            final int[][] fSrc = fsSrc[i];
            final int fSrcLen = fSrc.length;
            final int[][] fTrg = fsTrg[i] = new int[fSrcLen][3];
            for ( int j = 0; j < fSrcLen; ++j ) {
               final int[] vertTrg = fTrg[j];
               final int[] vertSrc = fSrc[j];
               vertTrg[0] = vertSrc[0];
               vertTrg[1] = vertSrc[1];
               vertTrg[2] = vertSrc[0];
            }
         }

      } else {

         for ( int i = 0; i < fsSrcLen; ++i ) {
            final int[][] fSrc = fsSrc[i];
            final int fSrcLen = fSrc.length;
            for ( int j = 0; j < fSrcLen; ++j ) {
               final int[] vertSrc = fSrc[j];
               vertSrc[2] = vertSrc[0];
            }
         }

      }

      return target;
   }

   /**
    * Casts all vertices and normals of this mesh to a sphere. Defaults to a
    * radius of 0.5 .
    *
    * @param source the source mesh
    * @param target the output mesh
    *
    * @return the spherical mesh
    */
   public static Mesh3 castToSphere ( final Mesh3 source, final Mesh3 target ) {

      return Mesh3.castToSphere(source, 0.5f, target);
   }

   /**
    * Creates a cone on the z axis, where its pointed end is on +z and its
    * base is on -z at a radius.
    *
    * @param depth   cone height
    * @param radius  base radius
    * @param sectors sectors in base
    * @param target  output mesh
    *
    * @return the cone
    */
   public static Mesh3 cone ( final float depth, final float radius,
      final int sectors, final Mesh3 target ) {

      // QUERY Should either depth or radius be relative, and the other removed?
      // To make the API between this and torus consistent, where 0.5 is assumed
      // to be the default.

      target.name = "Cone";

      /* Validate arguments. */
      final int vsect = sectors < 3 ? 3 : sectors;
      final float vdepth = Utils.max(depth, IUtils.EPSILON);
      final float vrad = Utils.max(radius, IUtils.EPSILON);

      /* Calculate vertices, faces and normals. */
      final int vertCount = vsect + 2;
      final int texCount = vsect + 1;
      final int faceCount = vsect + vsect;
      final int normalCount = vsect + 2;

      /* Reallocate arrays. */
      target.coords = Vec3.resize(target.coords, vertCount);
      target.texCoords = Vec2.resize(target.texCoords, texCount);
      target.normals = Vec3.resize(target.normals, normalCount);
      target.faces = new int[faceCount][3][3];

      /* Cache shortcuts. */
      final Vec3[] vs = target.coords;
      final Vec2[] vts = target.texCoords;
      final Vec3[] vns = target.normals;
      final int[][][] fs = target.faces;

      /* Set center points at either extent of the cone. */
      final float halfDepth = vdepth * 0.5f;

      /* The base and center of the triangle fan is index 0. */
      vs[0].set(0.0f, 0.0f, -halfDepth);
      Vec2.uvCenter(vts[0]);
      Vec3.down(vns[0]);

      vs[1].set(0.0f, 0.0f, halfDepth);
      Vec3.up(vns[1]);

      final float toTheta = 1.0f / vsect;
      for ( int i = 0, k = vsect; i < vsect; ++i, ++k ) {
         final int vCurrent = 2 + i;
         final int vtCurrent = 1 + i;
         // final int vnCurrent = 2 + i;

         final int imod = ( i + 1 ) % vsect;
         final int vNext = 2 + imod;
         final int vtNext = 1 + imod;
         // final int vnNext = 2 + imod;

         final float theta = i * toTheta;
         final float cost = Utils.scNorm(theta);
         final float sint = Utils.scNorm(theta - 0.25f);
         final Vec3 rim = vs[vCurrent];
         rim.set(cost * vrad, sint * vrad, -halfDepth);
         vts[vtCurrent].set(cost * 0.5f + 0.5f, sint * 0.5f + 0.5f);

         /*
          * Mix rim and tip by factor of 0.5. Since the tip is 0.0 for x and y,
          * and since the 0.5 multiplication is nullified by the normalization
          * anyway, this is simplified.
          */
         final Vec3 norm = vns[vCurrent];
         norm.set(rim.x, rim.y, rim.z + halfDepth);
         Vec3.normalize(norm, norm);

         /* Indices are zero by default. */
         final int[][] base = fs[i];

         // final int[] base0 = base[0];
         // base0[0] = 0;
         // base0[1] = 0;
         // base0[2] = 0;

         final int[] base1 = base[1];
         base1[0] = vNext;
         base1[1] = vtNext;
         // base1[2] = 0;

         final int[] base2 = base[2];
         base2[0] = vCurrent;
         base2[1] = vtCurrent;
         // base2[2] = 0;

         final int[][] side = fs[k];

         final int[] side0 = side[0];
         side0[0] = 1;
         // side0[1] = 0;
         side0[2] = 1;

         final int[] side1 = side[1];
         side1[0] = vCurrent;
         side1[1] = vtCurrent;
         // side1[2] = vnCurrent;
         side1[2] = vCurrent;

         final int[] side2 = side[2];
         side2[0] = vNext;
         side2[1] = vtNext;
         // side2[2] = vnNext;
         side2[2] = vNext;
      }

      return target;
   }

   /**
    * Creates a cone on the z axis, where its pointed end is on +z and its
    * base is on -z at a radius. The number of sectors defaults to
    * {@value IMesh#DEFAULT_CIRCLE_SECTORS}.
    *
    * @param depth  cone height
    * @param radius base radius
    * @param target output mesh
    *
    * @return the cone
    */
   public static Mesh3 cone ( final float depth, final float radius,
      final Mesh3 target ) {

      return Mesh3.cone(depth, radius, IMesh.DEFAULT_CIRCLE_SECTORS, target);
   }

   /**
    * Creates a cylinder on the z axis, where its pointed end is on +z and its
    * base is on -z at a radius. Defaults to a height, or depth, of 1.0 and a
    * radius of 0.5. The number of sectors defaults to
    * {@value IMesh#DEFAULT_CIRCLE_SECTORS}.
    *
    * @param target the output mesh
    *
    * @return the cone
    */
   public static Mesh3 cone ( final Mesh3 target ) {

      return Mesh3.cone(1.0f, 0.5f, target);
   }

   /**
    * Generates a cube mesh. In the context of Platonic solids, also known as
    * a hexahedron, as it has 6 faces and 8 vertices.
    *
    * @param size   the scalar
    * @param target the output mesh
    *
    * @return the cube
    */
   public static Mesh3 cube ( final float size, final Mesh3 target ) {

      return Mesh3.cube(size, PolyType.QUAD, Mesh3.DEFAULT_CUBE_UV_PROFILE,
         target);
   }

   /**
    * Generates a cube mesh. In the context of Platonic solids, also known as
    * a hexahedron, as it has 6 faces and 8 vertices.
    *
    * @param size    the scalar
    * @param poly    the polygon type
    * @param profile the texture profile
    * @param target  the output mesh
    *
    * @return the cube
    */
   public static Mesh3 cube ( final float size, final PolyType poly,
      final UvProfile.Cube profile, final Mesh3 target ) {

      final float vsz = Utils.max(IUtils.EPSILON, size);

      target.name = "Cube";

      target.coords = Vec3.resize(target.coords, 8);
      target.coords[0].set(-vsz, -vsz, -vsz);
      target.coords[1].set(-vsz, -vsz, vsz);
      target.coords[2].set(-vsz, vsz, -vsz);
      target.coords[3].set(-vsz, vsz, vsz);
      target.coords[4].set(vsz, -vsz, -vsz);
      target.coords[5].set(vsz, -vsz, vsz);
      target.coords[6].set(vsz, vsz, -vsz);
      target.coords[7].set(vsz, vsz, vsz);

      target.normals = Vec3.resize(target.normals, 6);
      target.normals[0].set(1.0f, 0.0f, 0.0f);
      target.normals[1].set(0.0f, 0.0f, 1.0f);
      target.normals[2].set(0.0f, 0.0f, -1.0f);
      target.normals[3].set(0.0f, -1.0f, 0.0f);
      target.normals[4].set(-1.0f, 0.0f, 0.0f);
      target.normals[5].set(0.0f, 1.0f, 0.0f);

      return Mesh3.cubeTexCoords(poly, profile, target);
   }

   /**
    * Generates a cube mesh. In the context of Platonic solids, also known as
    * a hexahedron, as it has 6 faces and 8 vertices. Uses the
    * {@link Mesh3#DEFAULT_CUBE_SIZE}, {@value Mesh3#DEFAULT_CUBE_SIZE} .
    *
    * @param target the output mesh
    *
    * @return the cube
    */
   public static Mesh3 cube ( final Mesh3 target ) {

      return Mesh3.cube(Mesh3.DEFAULT_CUBE_SIZE, PolyType.QUAD,
         Mesh3.DEFAULT_CUBE_UV_PROFILE, target);
   }

   /**
    * Creates a cube, subdivides it, casts its vertices to a sphere, then
    * triangulates its faces. The higher the iteration, the more spherical the
    * result, at the cost of speed.
    *
    * @param itrs   iterations
    * @param target the output mesh
    *
    * @return the cube sphere
    */
   public static Mesh3 cubeSphere ( final int itrs, final Mesh3 target ) {

      return Mesh3.cubeSphere(itrs, Mesh3.DEFAULT_POLY_TYPE,
         Mesh3.DEFAULT_CUBE_UV_PROFILE, target);
   }

   /**
    * Creates a cube, subdivides it, casts its vertices to a sphere, then
    * triangulates its faces. The higher the iteration, the more spherical the
    * result at the cost of performance.
    *
    * @param itrs    iterations
    * @param poly    the polygon type
    * @param profile the texture coordinate profile
    * @param target  the output mesh
    *
    * @return the cube sphere
    *
    * @see Mesh3#cube(float, PolyType, UvProfile.Cube, Mesh3)
    * @see Mesh3#subdivFacesCenter(int)
    */
   public static Mesh3 cubeSphere ( final int itrs, final PolyType poly,
      final UvProfile.Cube profile, final Mesh3 target ) {

      /*
       * Formula specific to casting a cube to a sphere:
       * https://math.stackexchange.com/questions/118760/can-someone-please-
       * explain-the-cube-to-sphere-mapping-formula-to-me The cube's initial
       * size must be [-1.0, 1.0]. Sort has to be done first to merge newly
       * created vertices before normals are calculated.
       */
      final float rad = 0.5f;

      Mesh3.cube(1.0f, PolyType.QUAD, profile, target);
      target.subdivFacesCenter(itrs);
      if ( poly == PolyType.TRI ) { target.triangulate(); }
      target.clean();

      final int[][][] fsSrc = target.faces;
      final Vec3[] vsSrc = target.coords;
      final int fsSrcLen = fsSrc.length;
      final int vsSrcLen = vsSrc.length;

      final Vec3[] vns = target.normals = Vec3.resize(target.normals, vsSrcLen);
      for ( int i = 0; i < vsSrcLen; ++i ) {

         final Vec3 vSrc = vsSrc[i];
         final float x = vSrc.x;
         final float y = vSrc.y;
         final float z = vSrc.z;

         final float xsq = x * x;
         final float ysq = y * y;
         final float zsq = z * z;

         final float xsq_2 = xsq * 0.5f;
         final float ysq_2 = ysq * 0.5f;
         final float zsq_2 = zsq * 0.5f;

         final Vec3 vnSrc = vns[i];
         vnSrc.set(x * Utils.sqrtUnchecked(1.0f - ( ysq_2 + zsq_2 ) + ysq * zsq
            * IUtils.ONE_THIRD), y * Utils.sqrtUnchecked(1.0f - ( zsq_2
               + xsq_2 ) + zsq * xsq * IUtils.ONE_THIRD), z * Utils
                  .sqrtUnchecked(1.0f - ( xsq_2 + ysq_2 ) + xsq * ysq
                     * IUtils.ONE_THIRD));
         Vec3.mul(vnSrc, rad, vSrc);
      }

      for ( int i = 0; i < fsSrcLen; ++i ) {
         final int[][] fSrc = fsSrc[i];
         final int fSrcLen = fSrc.length;
         for ( int j = 0; j < fSrcLen; ++j ) {
            final int[] vertSrc = fSrc[j];
            vertSrc[2] = vertSrc[0];
         }
      }

      target.name = "Sphere.Cube";
      return target;
   }

   /**
    * Creates a cube, subdivides it, casts its vertices to a sphere. For
    * iterations, uses {@link Mesh3#DEFAULT_SPHERE_ITR},
    * {@value Mesh3#DEFAULT_SPHERE_ITR}.
    *
    * @param target the output mesh
    *
    * @return the cube sphere
    */
   public static Mesh3 cubeSphere ( final Mesh3 target ) {

      return Mesh3.cubeSphere(Mesh3.DEFAULT_SPHERE_ITR, Mesh3.DEFAULT_POLY_TYPE,
         Mesh3.DEFAULT_CUBE_UV_PROFILE, target);
   }

   /**
    * Draws a cylinder with end caps along the z axis.
    *
    * @param target the output mesh
    *
    * @return the cylinder
    */
   public static Mesh3 cylinder ( final Mesh3 target ) {

      return Mesh3.cylinder(0.0f, 0.0f, -0.5f, 0.0f, 0.0f, 0.5f,
         IMesh.DEFAULT_CIRCLE_SECTORS, true, 0.25f, target);
   }

   /**
    * Draws a cylinder from an origin point toward a destination point. End
    * caps with a centered triangle fan may optionally be included.
    *
    * @param origin      the origin
    * @param dest        the destination
    * @param sectors     the number of sides
    * @param includeCaps include end caps
    * @param radius      radius
    * @param target      the output mesh
    *
    * @return the cylinder
    */
   public static Mesh3 cylinder ( final Vec3 origin, final Vec3 dest,
      final int sectors, final boolean includeCaps, final float radius,
      final Mesh3 target ) {

      return Mesh3.cylinder(origin.x, origin.y, origin.z, dest.x, dest.y,
         dest.z, sectors, includeCaps, radius, target);
   }

   /**
    * Draws a cylinder from an origin point toward a destination point. End
    * caps with a centered triangle fan may optionally be included.
    *
    * @param origin      the origin
    * @param dest        the destination
    * @param sectors     the number of sides
    * @param includeCaps include end caps
    * @param target      the output mesh
    *
    * @return the cylinder
    */
   public static Mesh3 cylinder ( final Vec3 origin, final Vec3 dest,
      final int sectors, final boolean includeCaps, final Mesh3 target ) {

      final float radius = 0.25f * Vec3.dist(origin, dest);
      return Mesh3.cylinder(origin.x, origin.y, origin.z, dest.x, dest.y,
         dest.z, sectors, includeCaps, radius, target);
   }

   /**
    * Draws a cylinder with end caps from an origin point toward a destination
    * point.
    *
    * @param origin  the origin
    * @param dest    the destination
    * @param sectors the number of sides
    * @param target  the output mesh
    *
    * @return the cylinder
    */
   public static Mesh3 cylinder ( final Vec3 origin, final Vec3 dest,
      final int sectors, final Mesh3 target ) {

      return Mesh3.cylinder(origin, dest, sectors, true, target);
   }

   /**
    * Draws a cylinder with end caps from an origin point toward a destination
    * point.
    *
    * @param origin the origin
    * @param dest   the destination
    * @param target the output mesh
    *
    * @return the cylinder
    */
   public static Mesh3 cylinder ( final Vec3 origin, final Vec3 dest,
      final Mesh3 target ) {

      return Mesh3.cylinder(origin, dest, IMesh.DEFAULT_CIRCLE_SECTORS, target);
   }

   /**
    * Creates an array of meshes, each with one face from the source mesh.
    * Leaves the source mesh unaltered. New meshes are created through
    * visitation of each face in the source, so they contain data that
    * would've been redundant in the original.
    *
    * @param source the source mesh
    *
    * @return the mesh array
    */
   public static Mesh3[] detachFaces ( final Mesh3 source ) {

      final String namePrefix = source.name + ".";
      final int[][][] fsSrc = source.faces;
      final Vec3[] vsSrc = source.coords;
      final Vec2[] vtsSrc = source.texCoords;
      final Vec3[] vnsSrc = source.normals;

      final int fsLen = fsSrc.length;
      final Mesh3[] meshes = new Mesh3[fsLen];

      for ( int i = 0; i < fsLen; ++i ) {
         final int[][] fSrc = fsSrc[i];
         final int fLen = fSrc.length;

         final int[][][] fsTrg = new int[1][fLen][3];
         final Vec3[] vsTrg = new Vec3[fLen];
         final Vec2[] vtsTrg = new Vec2[fLen];
         final Vec3[] vnsTrg = new Vec3[fLen];

         final int[][] fTrg = fsTrg[0];
         for ( int j = 0; j < fLen; ++j ) {
            final int[] vertSrc = fSrc[j];

            vsTrg[j] = new Vec3(vsSrc[vertSrc[0]]);
            vtsTrg[j] = new Vec2(vtsSrc[vertSrc[1]]);
            vnsTrg[j] = new Vec3(vnsSrc[vertSrc[2]]);

            final int[] vertTrg = fTrg[j];
            vertTrg[0] = j;
            vertTrg[1] = j;
            vertTrg[2] = j;
         }

         final Mesh3 mesh = new Mesh3(fsTrg, vsTrg, vtsTrg, vnsTrg);
         mesh.name = namePrefix + Utils.toPadded(i, 3);
         meshes[i] = mesh;
      }

      return meshes;
   }

   /**
    * Creates a dodecahedron, a Platonic solid with 12 faces and 20
    * coordinates.
    *
    * @param target the output mesh
    *
    * @return the dodecahedron
    */
   public static final Mesh3 dodecahedron ( final Mesh3 target ) {

      /*
       * double r = 0.5d; double phi = (1.0d + Math.sqrt(5.0d)) / 2.0d; double b
       * = r * (1.0d / phi); double c = r * (2.0d - phi); 0.3090169943749474,
       * 0.19098300562505255 . The UV net is made by finding the inradius of a
       * hexagon as radius * cos(pi / 5), then surrounding a central pentagon
       * with 5 more on each edge. Two such rings are then stitched together; if
       * the left ring is centered at (0, 0), then the right ring is at
       * (2.30826265288144d, -0.25d) assuming radius is 0.5d.
       */

      target.name = "Dodecahedron";

      target.coords = Vec3.resize(target.coords, 20);
      target.coords[0].set(0.0f, -0.02712715f, -0.53454524f);
      target.coords[1].set(0.0f, 0.33614415f, -0.4165113f);
      target.coords[2].set(-0.309017f, -0.19840115f, -0.38938415f);
      target.coords[3].set(0.309017f, -0.19840115f, -0.38938415f);
      target.coords[4].set(-0.309017f, 0.38938415f, -0.19840115f);
      target.coords[5].set(0.309017f, 0.38938415f, -0.19840115f);
      target.coords[6].set(-0.5f, 0.05901699f, -0.18163565f);
      target.coords[7].set(0.5f, 0.05901699f, -0.18163565f);
      target.coords[8].set(-0.19098301f, -0.47552827f, -0.15450847f);
      target.coords[9].set(0.19098301f, -0.47552827f, -0.15450847f);
      target.coords[10].set(-0.19098301f, 0.47552827f, 0.15450847f);
      target.coords[11].set(0.19098301f, 0.47552827f, 0.15450847f);
      target.coords[12].set(-0.5f, -0.05901699f, 0.18163565f);
      target.coords[13].set(0.5f, -0.05901699f, 0.18163565f);
      target.coords[14].set(-0.309017f, -0.38938415f, 0.19840115f);
      target.coords[15].set(0.309017f, -0.38938415f, 0.19840115f);
      target.coords[16].set(-0.309017f, 0.19840115f, 0.38938415f);
      target.coords[17].set(0.309017f, 0.19840115f, 0.38938415f);
      target.coords[18].set(0.0f, -0.33614415f, 0.4165113f);
      target.coords[19].set(0.0f, 0.02712715f, 0.53454524f);

      target.texCoords = Vec2.resize(target.texCoords, 38);
      target.texCoords[0].set(0.099106364f, 0.25323522f);
      target.texCoords[1].set(0.22160856f, 0.25323522f);
      target.texCoords[2].set(0.29731908f, 0.25323522f);
      target.texCoords[3].set(0.4198213f, 0.25323522f);
      target.texCoords[4].set(0.7405362f, 0.25323522f);
      target.texCoords[5].set(0.64142984f, 0.3252402f);
      target.texCoords[6].set(0.8396426f, 0.3252402f);
      target.texCoords[7].set(0.06125109f, 0.36974174f);
      target.texCoords[8].set(0.25946382f, 0.36974174f);
      target.texCoords[9].set(0.45767656f, 0.36974174f);
      target.texCoords[10].set(0.58017874f, 0.36974174f);
      target.texCoords[11].set(0.90089375f, 0.36974174f);
      target.texCoords[12].set(0.03785526f, 0.4417467f);
      target.texCoords[13].set(0.16035746f, 0.4417467f);
      target.texCoords[14].set(0.3585702f, 0.4417467f);
      target.texCoords[15].set(0.4810724f, 0.4417467f);
      target.texCoords[16].set(0.6792851f, 0.4417467f);
      target.texCoords[17].set(0.8017873f, 0.4417467f);
      target.texCoords[18].set(1.0f, 0.4417467f);
      target.texCoords[19].set(0.0f, 0.5582533f);
      target.texCoords[20].set(0.19821273f, 0.5582533f);
      target.texCoords[21].set(0.32071492f, 0.5582533f);
      target.texCoords[22].set(0.51892763f, 0.5582533f);
      target.texCoords[23].set(0.64142984f, 0.5582533f);
      target.texCoords[24].set(0.8396426f, 0.5582533f);
      target.texCoords[25].set(0.9621448f, 0.5582533f);
      target.texCoords[26].set(0.099106364f, 0.63025826f);
      target.texCoords[27].set(0.4198213f, 0.63025826f);
      target.texCoords[28].set(0.54232347f, 0.63025826f);
      target.texCoords[29].set(0.7405362f, 0.63025826f);
      target.texCoords[30].set(0.93874896f, 0.63025826f);
      target.texCoords[31].set(0.16035746f, 0.67475975f);
      target.texCoords[32].set(0.3585702f, 0.67475975f);
      target.texCoords[33].set(0.25946382f, 0.7467648f);
      target.texCoords[34].set(0.58017874f, 0.7467648f);
      target.texCoords[35].set(0.70268095f, 0.7467648f);
      target.texCoords[36].set(0.7783915f, 0.7467648f);
      target.texCoords[37].set(0.90089375f, 0.7467648f);

      target.normals = Vec3.resize(target.normals, 12);
      target.normals[0].set(-0.5257311f, 0.2628655f, -0.80901694f);
      target.normals[1].set(0.5257311f, 0.2628655f, -0.80901694f);
      target.normals[2].set(0.0f, -0.64655715f, -0.7628655f);
      target.normals[3].set(0.0f, 0.9714768f, -0.23713443f);
      target.normals[4].set(-0.8506508f, -0.5f, -0.16245979f);
      target.normals[5].set(0.8506508f, -0.5f, -0.16245979f);
      target.normals[6].set(-0.8506508f, 0.5f, 0.16245979f);
      target.normals[7].set(0.8506508f, 0.5f, 0.16245979f);
      target.normals[8].set(0.0f, -0.9714768f, 0.23713443f);
      target.normals[9].set(0.0f, 0.64655715f, 0.7628655f);
      target.normals[10].set(-0.5257311f, -0.2628655f, 0.80901694f);
      target.normals[11].set(0.5257311f, -0.2628655f, 0.80901694f);

      /* @formatter:off */
      target.faces = new int[][][] {
         { {  0, 36,  0 }, {  2, 37,  0 }, {  6, 30,  0 }, {  4, 24,  0 }, {  1, 29,  0 } },
         { {  0, 35,  1 }, {  1, 29,  1 }, {  5, 23,  1 }, {  7, 28,  1 }, {  3, 34,  1 } },
         { {  0, 33,  2 }, {  3, 32,  2 }, {  9, 21,  2 }, {  8, 20,  2 }, {  2, 31,  2 } },
         { {  1, 29,  3 }, {  4, 24,  3 }, { 10, 17,  3 }, { 11, 16,  3 }, {  5, 23,  3 } },
         { {  3, 27,  5 }, {  7, 22,  5 }, { 13, 15,  5 }, { 15, 14,  5 }, {  9, 21,  5 } },
         { {  2, 26,  4 }, {  8, 20,  4 }, { 14, 13,  4 }, { 12, 12,  4 }, {  6, 19,  4 } },
         { {  4, 24,  6 }, {  6, 25,  6 }, { 12, 18,  6 }, { 16, 11,  6 }, { 10, 17,  6 } },
         { {  5, 23,  7 }, { 11, 16,  7 }, { 17, 10,  7 }, { 13, 15,  7 }, {  7, 22,  7 } },
         { {  8, 20,  8 }, {  9, 21,  8 }, { 15, 14,  8 }, { 18,  8,  8 }, { 14, 13,  8 } },
         { { 17,  5,  9 }, { 11, 16,  9 }, { 10, 17,  9 }, { 16,  6,  9 }, { 19,  4,  9 } },
         { { 16,  0, 10 }, { 12,  7, 10 }, { 14, 13, 10 }, { 18,  8, 10 }, { 19,  1, 10 } },
         { { 18,  8, 11 }, { 15, 14, 11 }, { 13,  9, 11 }, { 17,  3, 11 }, { 19,  2, 11 } } };
      /* @formatter:on */

      return target;
   }

   /**
    * Converts a curve to a mesh. If the fore handle and rear handle of a
    * curve segment are colinear according to a tolerance, then only two
    * vertices are added; otherwise evaluates the Bezier curve, producing one
    * line segment per the requested resolution.
    *
    * @param source      the source curve
    * @param resolution  the line segment count
    * @param colinearTol the colinear tolerance
    * @param target      the output mesh
    *
    * @return the mesh
    *
    * @see Mesh3#calcUvs()
    * @see Mesh3#shadeSmooth()
    */
   public static Mesh3 fromCurve3 ( final Curve3 source, final int resolution,
      final float colinearTol, final Mesh3 target ) {

      final ArrayList < Vec3 > points = new ArrayList <>(64);
      Mesh3.fromCurve3(source, resolution, colinearTol, points, new Vec3(),
         new Vec3());
      final int pointsLen = points.size();
      target.coords = points.toArray(new Vec3[pointsLen]);
      final int[][][] fs = target.faces = new int[1][pointsLen][3];
      final int[][] f = fs[0];
      for ( int i = 0; i < pointsLen; ++i ) { f[i][0] = i; }

      target.calcUvs();
      target.shadeSmooth();
      target.materialIndex = source.materialIndex;
      return target;
   }

   /**
    * Converts an array of curves to a mesh. If the fore handle and rear
    * handle of a curve segment are colinear according to a tolerance, then
    * only two vertices are added; otherwise evaluates the Bezier curve,
    * producing one line segment per the requested resolution.
    *
    * @param arr         the source curves
    * @param resolution  the line segment count
    * @param colinearTol the colinear tolerance
    * @param target      the output mesh
    *
    * @return the mesh
    *
    * @see Mesh3#calcUvs()
    * @see Mesh3#shadeSmooth()
    */
   public static Mesh3 fromCurve3 ( final Curve3[] arr, final int resolution,
      final float colinearTol, final Mesh3 target ) {

      final int curvesLen = arr.length;
      final ArrayList < Vec3 > points = new ArrayList <>(64);
      final Vec3 dir0 = new Vec3();
      final Vec3 dir1 = new Vec3();
      final int[][][] fs = target.faces = new int[curvesLen][][];
      int prevIdx = 0;
      int pointsLen = 0;
      for ( int i = 0; i < curvesLen; ++i ) {
         Mesh3.fromCurve3(arr[i], resolution, colinearTol, points, dir0, dir1);

         pointsLen = points.size();
         final int fLen = pointsLen - prevIdx;
         final int[][] f = fs[i] = new int[fLen][3];
         for ( int j = 0; j < fLen; ++j ) { f[j][0] = prevIdx + j; }
         prevIdx = pointsLen;
      }

      target.coords = points.toArray(new Vec3[pointsLen]);
      target.calcUvs();
      target.shadeSmooth();
      return target;
   }

   /**
    * Groups separate meshes with the same material index into a mesh. Returns
    * a new array of meshes. Do not use if source meshes need to be
    * transformed independently.
    *
    * @param meshes the array of meshes
    *
    * @return the consolidated meshes
    */
   public static final Mesh3[] groupByMaterial ( final Mesh3[] meshes ) {

      final HashMap < Integer, Mesh3 > dict = new HashMap <>();
      Mesh3 current;

      final int srcLen = meshes.length;
      for ( int i = 0; i < srcLen; ++i ) {
         final Mesh3 source = meshes[i];
         final int matIdxPrm = source.materialIndex;
         final Integer matIdxObj = matIdxPrm;

         /* Create a new mesh if it is not already present in dictionary. */
         current = dict.get(matIdxObj);
         if ( current == null ) {
            current = new Mesh3(new int[0][0][3], new Vec3[0], new Vec2[0],
               new Vec3[0]);
            current.materialIndex = matIdxPrm;
            current.name = "Mesh." + Utils.toPadded(matIdxPrm, 3);
            dict.put(matIdxObj, current);
         }

         /* Copy source coordinates. */
         final Vec3[] vsSrc = source.coords;
         final int vsLen = vsSrc.length;
         final Vec3[] vsTrg = new Vec3[vsLen];
         for ( int j = 0; j < vsLen; ++j ) { vsTrg[j] = new Vec3(vsSrc[j]); }

         /* Copy source texture coordinates. */
         final Vec2[] vtsSrc = source.texCoords;
         final int vtsLen = vtsSrc.length;
         final Vec2[] vtsTrg = new Vec2[vtsLen];
         for ( int j = 0; j < vtsLen; ++j ) { vtsTrg[j] = new Vec2(vtsSrc[j]); }

         /* Copy source normals. */
         final Vec3[] vnsSrc = source.normals;
         final int vnsLen = vnsSrc.length;
         final Vec3[] vnsTrg = new Vec3[vnsLen];
         for ( int j = 0; j < vnsLen; ++j ) { vnsTrg[j] = new Vec3(vnsSrc[j]); }

         /* Concatenated indices need to be offset by current data lengths. */
         final int vsTrgLen = current.coords.length;
         final int vtsTrgLen = current.texCoords.length;
         final int vnsTrgLen = current.normals.length;

         /* Copy source face indices. */
         final int[][][] fsSrc = source.faces;
         final int fsLen = fsSrc.length;
         final int[][][] fsTrg = new int[fsLen][][];
         for ( int j = 0; j < fsLen; ++j ) {
            final int[][] fSrc = fsSrc[j];
            final int fLen = fSrc.length;
            final int[][] fTrg = fsTrg[j] = new int[fLen][3];
            for ( int k = 0; k < fLen; ++k ) {
               final int[] vertSrc = fSrc[k];
               final int[] vertTrg = fTrg[k];
               vertTrg[0] = vsTrgLen + vertSrc[0];
               vertTrg[1] = vtsTrgLen + vertSrc[1];
               vertTrg[2] = vnsTrgLen + vertSrc[2];
            }
         }

         /* Concatenate copies with current data. */
         current.coords = Vec3.concat(current.coords, vsTrg);
         current.texCoords = Vec2.concat(current.texCoords, vtsTrg);
         current.normals = Vec3.concat(current.normals, vnsTrg);
         current.faces = Mesh.splice(current.faces, current.faces.length, 0,
            fsTrg);
      }

      /* Convert dictionary values to an array; clean meshes of excess data. */
      final Mesh3[] result = dict.values().toArray(new Mesh3[dict.size()]);
      final int trgLen = result.length;
      for ( int i = 0; i < trgLen; ++i ) { result[i].clean(); }
      return result;
   }

   /**
    * Merges the data of a collection of meshes together into one mesh.
    *
    * @param coll   the array
    * @param target the output mesh
    *
    * @return the merger
    */
   public static Mesh3 groupData ( final Collection < Mesh3 > coll,
      final Mesh3 target ) {

      return Mesh3.groupData(coll.toArray(new Mesh3[coll.size()]), target);
   }

   /**
    * Merges the data of two meshes together into one mesh.
    *
    * @param a      the first mesh
    * @param b      the second mesh
    * @param target the output mesh
    *
    * @return the merger
    */
   public static Mesh3 groupData ( final Mesh3 a, final Mesh3 b,
      final Mesh3 target ) {

      return Mesh3.groupData(new Mesh3[] { a, b }, target);
   }

   /**
    * Merges the data of an array of meshes together into one mesh.
    *
    * @param arr    the array
    * @param target the output mesh
    *
    * @return the merger
    */
   public static Mesh3 groupData ( final Mesh3[] arr, final Mesh3 target ) {

      /* Sum lengths. */
      int vsTotal = 0;
      int vtsTotal = 0;
      int vnsTotal = 0;
      int fsTotal = 0;
      final int collLen = arr.length;
      for ( int i = 0; i < collLen; ++i ) {
         final Mesh3 m = arr[i];
         vsTotal += m.coords.length;
         vtsTotal += m.texCoords.length;
         vnsTotal += m.normals.length;
         fsTotal += m.faces.length;
      }

      /* Resize target data. */
      target.coords = Vec3.resize(target.coords, vsTotal);
      target.texCoords = Vec2.resize(target.texCoords, vtsTotal);
      target.normals = Vec3.resize(target.normals, vnsTotal);

      /* Cache target shortcuts. */
      final Vec3[] vsTrg = target.coords;
      final Vec2[] vtsTrg = target.texCoords;
      final Vec3[] vnsTrg = target.normals;
      final int[][][] fsTrg = new int[fsTotal][][];

      /* Offset indices in merged data. */
      int vsCursor = 0;
      int vtsCursor = 0;
      int vnsCursor = 0;
      int fsCursor = 0;

      /* Copy data. */
      for ( int i = 0; i < collLen; ++i ) {
         final Mesh3 m = arr[i];

         /* Copy coordinates. */
         final Vec3[] vsSrc = m.coords;
         final int vsSrcLen = vsSrc.length;
         for ( int j = 0; j < vsSrcLen; ++j ) {
            vsTrg[vsCursor + j].set(vsSrc[j]);
         }

         /* Copy texture coordinates. */
         final Vec2[] vtsSrc = m.texCoords;
         final int vtsSrcLen = vtsSrc.length;
         for ( int j = 0; j < vtsSrcLen; ++j ) {
            vtsTrg[vtsCursor + j].set(vtsSrc[j]);
         }

         /* Copy normals. */
         final Vec3[] vnsSrc = m.normals;
         final int vnsSrcLen = vnsSrc.length;
         for ( int j = 0; j < vnsSrcLen; ++j ) {
            vnsTrg[vnsCursor + j].set(vnsSrc[j]);
         }

         /* Add new offsets to faces. */
         final int[][][] fsSrc = m.faces;
         final int fsLen = fsSrc.length;
         for ( int j = 0; j < fsLen; ++j ) {
            final int[][] fSrc = fsSrc[j];
            final int fLen = fSrc.length;
            final int[][] fTrg = fsTrg[fsCursor + j] = new int[fLen][3];
            for ( int k = 0; k < fLen; ++k ) {
               final int[] vertSrc = fSrc[k];
               final int[] vertTrg = fTrg[k];

               vertTrg[0] = vertSrc[0] + vsCursor;
               vertTrg[1] = vertSrc[1] + vtsCursor;
               vertTrg[2] = vertSrc[2] + vnsCursor;
            }
         }

         /* Add length of individual mesh to offsets. */
         vsCursor += vsSrcLen;
         vtsCursor += vtsSrcLen;
         vnsCursor += vnsSrcLen;
         fsCursor += fsLen;
      }

      /* Update faces and return. */
      target.faces = fsTrg;
      return target;
   }

   /**
    * Creates an icosahedron, a Platonic solid with 20 faces and 12
    * coordinates.
    *
    * @param target the output mesh
    *
    * @return the icosahedron
    */
   public static final Mesh3 icosahedron ( final Mesh3 target ) {

      /*
       * Coordinate values result from the normalization of (1.0, PHI, 0.0) to
       * (0.5257311121191336, 0.85065080835204, 0.0), then multiplication by a
       * radius of 0.5: (0.2628655560595668, 0.42532540417602, 0.0).
       */

      target.name = "Icosahedron";

      target.coords = Vec3.resize(target.coords, 12);
      target.coords[0].set(0.0f, 0.0f, -0.5f);
      target.coords[1].set(0.0f, -0.4472122f, -0.22360958f);
      target.coords[2].set(-0.4253254f, -0.13819478f, -0.22360958f);
      target.coords[3].set(0.4253254f, -0.13819478f, -0.22360958f);
      target.coords[4].set(-0.26286554f, 0.3618018f, -0.22360958f);
      target.coords[5].set(0.26286554f, 0.3618018f, -0.22360958f);
      target.coords[6].set(-0.26286554f, -0.3618018f, 0.22360958f);
      target.coords[7].set(0.26286554f, -0.3618018f, 0.22360958f);
      target.coords[8].set(-0.4253254f, 0.13819478f, 0.22360958f);
      target.coords[9].set(0.4253254f, 0.13819478f, 0.22360958f);
      target.coords[10].set(0.0f, 0.4472122f, 0.22360958f);
      target.coords[11].set(0.0f, 0.0f, 0.5f);

      /*
       * Follows an icosahedron's net, wherein four skewed rows of triangles
       * proceed from the left edge of the UV map to the right.
       */
      target.texCoords = Vec2.resize(target.texCoords, 22);
      target.texCoords[0].set(0.0f, 0.578613f);
      target.texCoords[1].set(0.090909f, 0.421387f);
      target.texCoords[2].set(0.090909f, 0.735839f);
      target.texCoords[3].set(0.181818f, 0.264161f);
      target.texCoords[4].set(0.181818f, 0.578613f);
      target.texCoords[5].set(0.272727f, 0.421387f);
      target.texCoords[6].set(0.272727f, 0.735839f);
      target.texCoords[7].set(0.363636f, 0.264161f);
      target.texCoords[8].set(0.363636f, 0.578613f);
      target.texCoords[9].set(0.454545f, 0.421387f);
      target.texCoords[10].set(0.454545f, 0.735839f);
      target.texCoords[11].set(0.545455f, 0.264161f);
      target.texCoords[12].set(0.545455f, 0.578613f);
      target.texCoords[13].set(0.636364f, 0.421387f);
      target.texCoords[14].set(0.636364f, 0.735839f);
      target.texCoords[15].set(0.727273f, 0.264161f);
      target.texCoords[16].set(0.727273f, 0.578613f);
      target.texCoords[17].set(0.818182f, 0.421387f);
      target.texCoords[18].set(0.818182f, 0.735839f);
      target.texCoords[19].set(0.909091f, 0.264161f);
      target.texCoords[20].set(0.909091f, 0.578613f);
      target.texCoords[21].set(1.0f, 0.421387f);

      target.normals = Vec3.resize(target.normals, 20);
      target.normals[0].set(0.0f, -0.60706145f, 0.79465485f);
      target.normals[1].set(0.57735217f, -0.7946537f, -0.18758972f);
      target.normals[2].set(0.934172f, -0.30353418f, 0.18758905f);
      target.normals[3].set(0.934172f, 0.30353418f, -0.18758905f);
      target.normals[4].set(0.57735217f, 0.7946537f, 0.18758972f);
      target.normals[5].set(0.35682237f, 0.4911218f, 0.79465526f);
      target.normals[6].set(-0.35682237f, 0.4911218f, 0.79465526f);
      target.normals[7].set(0.0f, 0.60706145f, -0.79465485f);
      target.normals[8].set(-0.35682237f, -0.4911218f, -0.79465526f);
      target.normals[9].set(0.35682237f, -0.4911218f, -0.79465526f);
      target.normals[10].set(-0.57734716f, 0.18759352f, -0.7946564f);
      target.normals[11].set(0.5773471f, 0.18759349f, -0.7946564f);
      target.normals[12].set(0.0f, 0.98224694f, -0.1875924f);
      target.normals[13].set(-0.57735217f, 0.7946537f, 0.18758972f);
      target.normals[14].set(-0.934172f, 0.30353418f, -0.18758905f);
      target.normals[15].set(-0.934172f, -0.30353418f, 0.18758905f);
      target.normals[16].set(-0.57735217f, -0.7946537f, -0.18758972f);
      target.normals[17].set(0.0f, -0.98224694f, 0.1875924f);
      target.normals[18].set(-0.57734716f, -0.18759352f, 0.7946564f);
      target.normals[19].set(0.5773471f, -0.18759349f, 0.7946564f);

      /* @formatter:off */
      target.faces = new int[][][] {
         { {  0,  2,  7 }, {  4,  4,  7 }, {  5,  0,  7 } },
         { {  0,  6, 10 }, {  2,  8, 10 }, {  4,  4, 10 } },
         { {  0, 10,  8 }, {  1, 12,  8 }, {  2,  8,  8 } },
         { {  0, 14,  9 }, {  3, 16,  9 }, {  1, 12,  9 } },
         { {  0, 18, 11 }, {  5, 20, 11 }, {  3, 16, 11 } },
         { { 10,  1, 12 }, {  5,  0, 12 }, {  4,  4, 12 } },
         { {  8,  5, 13 }, { 10,  1, 13 }, {  4,  4, 13 } },
         { {  8,  5, 14 }, {  4,  4, 14 }, {  2,  8, 14 } },
         { {  6,  9, 15 }, {  8,  5, 15 }, {  2,  8, 15 } },
         { {  6,  9, 16 }, {  2,  8, 16 }, {  1, 12, 16 } },
         { {  7, 13, 17 }, {  6,  9, 17 }, {  1, 12, 17 } },
         { {  7, 13,  1 }, {  1, 12,  1 }, {  3, 16,  1 } },
         { {  9, 17,  2 }, {  7, 13,  2 }, {  3, 16,  2 } },
         { {  9, 17,  3 }, {  3, 16,  3 }, {  5, 20,  3 } },
         { { 10, 21,  4 }, {  9, 17,  4 }, {  5, 20,  4 } },
         { { 11, 19,  5 }, {  9, 17,  5 }, { 10, 21,  5 } },
         { { 11,  3,  6 }, { 10,  1,  6 }, {  8,  5,  6 } },
         { { 11,  7, 18 }, {  8,  5, 18 }, {  6,  9, 18 } },
         { { 11, 11,  0 }, {  6,  9,  0 }, {  7, 13,  0 } },
         { { 11, 15, 19 }, {  7, 13, 19 }, {  9, 17, 19 } } };
      /* @formatter:on */
      return target;
   }

   /**
    * Creates an icosahedron, subdivides through inscription, then casts the
    * vertices to a sphere. The higher the iteration, the more sphere-like the
    * result at the cost of performance.
    *
    * @param itrs   iterations
    * @param target the output mesh
    *
    * @return the icosphere
    *
    * @see Mesh3#castToSphere(Mesh3, float, Mesh3)
    * @see Mesh3#clean()
    * @see Mesh3#icosahedron(Mesh3)
    * @see Mesh3#subdivFaceInscribe(int)
    */
   public static Mesh3 icosphere ( final int itrs, final Mesh3 target ) {

      /*
       * Sort has to be done first to merge newly created vertices before
       * normals are calculated.
       */
      Mesh3.icosahedron(target);
      target.subdivFacesInscribe(itrs);
      target.clean();
      Mesh3.castToSphere(target, 0.5f, target);
      target.name = "Icosphere";
      return target;
   }

   /**
    * Creates an icosahedron, subdivides through inscription, then casts the
    * vertices to a sphere. For iterations, uses
    * {@link Mesh3#DEFAULT_SPHERE_ITR}, {@value Mesh3#DEFAULT_SPHERE_ITR}.
    *
    * @param target the output mesh
    *
    * @return the icosphere
    */
   public static Mesh3 icosphere ( final Mesh3 target ) {

      return Mesh3.icosphere(Mesh3.DEFAULT_SPHERE_ITR, target);
   }

   /**
    * Creates an octahedron, a Platonic solid with 8 faces and 6 coordinates.
    *
    * @param target the output mesh
    *
    * @return the octahedron
    */
   public static final Mesh3 octahedron ( final Mesh3 target ) {

      target.name = "Octahedron";

      target.coords = Vec3.resize(target.coords, 6);
      target.coords[0].set(0.0f, 0.0f, -0.5f);
      target.coords[1].set(0.0f, -0.5f, 0.0f);
      target.coords[2].set(-0.5f, 0.0f, 0.0f);
      target.coords[3].set(0.5f, 0.0f, 0.0f);
      target.coords[4].set(0.0f, 0.5f, 0.0f);
      target.coords[5].set(0.0f, 0.0f, 0.5f);

      /* To maintain aspect, scale y by sqrt(3) / 4, 0.28349364, 0.71650636 */
      target.texCoords = Vec2.resize(target.texCoords, 13);
      target.texCoords[0].set(0.125f, 0.28349364f);
      target.texCoords[1].set(0.375f, 0.28349364f);
      target.texCoords[2].set(0.625f, 0.28349364f);
      target.texCoords[3].set(0.875f, 0.28349364f);
      target.texCoords[4].set(0.0f, 0.5f);
      target.texCoords[5].set(0.25f, 0.5f);
      target.texCoords[6].set(0.5f, 0.5f);
      target.texCoords[7].set(0.75f, 0.5f);
      target.texCoords[8].set(1.0f, 0.5f);
      target.texCoords[9].set(0.125f, 0.71650636f);
      target.texCoords[10].set(0.375f, 0.71650636f);
      target.texCoords[11].set(0.625f, 0.71650636f);
      target.texCoords[12].set(0.875f, 0.71650636f);

      target.normals = Vec3.resize(target.normals, 8);
      target.normals[0].set(-0.57735026f, -0.57735026f, -0.57735026f);
      target.normals[1].set(0.57735026f, -0.57735026f, -0.57735026f);
      target.normals[2].set(-0.57735026f, 0.57735026f, -0.57735026f);
      target.normals[3].set(0.57735026f, 0.57735026f, -0.57735026f);
      target.normals[4].set(-0.57735026f, -0.57735026f, 0.57735026f);
      target.normals[5].set(0.57735026f, -0.57735026f, 0.57735026f);
      target.normals[6].set(-0.57735026f, 0.57735026f, 0.57735026f);
      target.normals[7].set(0.57735026f, 0.57735026f, 0.57735026f);

      /* @formatter:off */
      target.faces = new int[][][] {
         { {  0,  9,  0 }, {  1,  5,  0 }, {  2,  4,  0 } },
         { {  0, 10,  1 }, {  3,  6,  1 }, {  1,  5,  1 } },
         { {  0, 11,  2 }, {  4,  7,  2 }, {  3,  6,  2 } },
         { {  0, 12,  3 }, {  2,  8,  3 }, {  4,  7,  3 } },
         { {  2,  4,  4 }, {  1,  5,  4 }, {  5,  0,  4 } },
         { {  1,  5,  5 }, {  3,  6,  5 }, {  5,  1,  5 } },
         { {  4,  7,  6 }, {  2,  8,  6 }, {  5,  3,  6 } },
         { {  3,  6,  7 }, {  4,  7,  7 }, {  5,  2,  7 } }
      };
      /* @formatter:on */

      return target;
   }

   /**
    * Projects a 3D mesh onto a 2D surface, returning the 2D mesh projection.
    * The camera matrix is the same as the model-view matrix. To generate the
    * required inputs, see
    * {@link Mat4#perspective(float, float, float, float, Mat4)} and
    * {@link Mat4#camera(Vec3, Vec3, Vec3, Handedness, Mat4, Vec3, Vec3, Vec3)}.
    * <br>
    * <br>
    * Occluded faces are not culled from the 2D mesh.
    *
    * @param source     the input mesh
    * @param projection the projection matrix
    * @param camera     the camera matrix
    * @param target     the output mesh
    *
    * @return the projected mesh
    *
    * @see Mat4#mul(Mat4, Vec4, Vec4)
    */
   @Experimental
   public static Mesh2 project ( final Mesh3 source, final Mat4 projection,
      final Mat4 camera, final Mesh2 target ) {

      target.name = source.name;
      target.materialIndex = source.materialIndex;

      final Vec3[] vsSource = source.coords;
      final Vec2[] vtsSource = source.texCoords;

      final int vsLen = vsSource.length;
      final int vtsLen = vtsSource.length;

      final Vec2[] vsTarget = target.coords = Vec2.resize(target.coords, vsLen);
      final Vec2[] vtsTarget = target.texCoords = Vec2.resize(target.texCoords,
         vtsLen);

      /* Copy texture coordinates. */
      for ( int i = 0; i < vtsLen; ++i ) { vtsTarget[i].set(vtsSource[i]); }

      /* Project coordinates to 2D screen. */
      final Vec4 promoted = new Vec4();
      for ( int i = 0; i < vsLen; ++i ) {

         /* Promote 3D coordinate to 4D, then multiply. */
         promoted.set(vsSource[i], 1.0f);
         Mat4.mul(camera, promoted, promoted);
         Mat4.mul(projection, promoted, promoted);

         /* Do not flip screen y, like Processing's screen does. */
         final float wInv = promoted.w != 0.0f ? 1.0f / promoted.w : 0.0f;
         vsTarget[i].set(promoted.x * 0.5f * wInv, promoted.y * 0.5f * wInv);
      }

      /* Copy faces. */
      final int[][][] fsSource = source.faces;
      final int facesLen = fsSource.length;
      final int[][][] fsTarget = target.faces = new int[facesLen][][];

      for ( int i = 0; i < facesLen; ++i ) {
         final int[][] vertSource = fsSource[i];
         final int vertsLen = vertSource.length;
         final int[][] vertTarget = fsTarget[i] = new int[vertsLen][2];

         for ( int j = 0; j < vertsLen; ++j ) {
            vertTarget[j][0] = vertSource[j][0];
            vertTarget[j][1] = vertSource[j][1];
         }
      }

      /* Sort 2D faces according to 3D vectors. */
      Arrays.sort(fsTarget, new SortLoops3(vsSource));

      return target;
   }

   /**
    * Returns a collection of mesh vertices organized by their proximity to a
    * point. The proximity is expressed as a factor where the nearest vertex
    * is on the nearBound; the farthest is on the farBound. Any function which
    * mutates a vertex's properties by such a factor can be applied to the
    * collection.<br>
    * <br>
    * The Euclidean distance from a point is unsigned, so two points may have
    * approximately the same distance from the point yet be in different
    * quadrants of the Cartesian coordinate system, i.e. not organized by
    * proximity to each other.
    *
    * @param m         the mesh
    * @param p         the point
    * @param nearBound the factor near bound
    * @param farBound  the factor far bound
    *
    * @return the collection
    *
    * @see Mesh3#getVertices()
    * @see Vec3#distSq(Vec3, Vec3)
    */
   public static Map < Float, Vert3 > proximity ( final Mesh3 m, final Vec3 p,
      final float nearBound, final float farBound ) {

      final Vert3[] verts = m.getVertices();
      final int vertLen = verts.length;
      final float[] dists = new float[vertLen];
      float minDist = Float.MAX_VALUE;
      float maxDist = -Float.MAX_VALUE;
      for ( int i = 0; i < vertLen; ++i ) {
         final float distSq = Vec3.distSq(verts[i].coord, p);
         dists[i] = distSq;
         if ( distSq < minDist ) { minDist = distSq; }
         if ( distSq > maxDist ) { maxDist = distSq; }
      }

      /*
       * The span of the origin range and destination range are already known,
       * so calculate portions of the map(distance, minDist, maxDist, nearBound,
       * farBound) function outside of the for loop.
       */
      final float spanOrig = maxDist - minDist;
      final float scalar = spanOrig != 0.0f ? ( farBound - nearBound )
         / spanOrig : 0.0f;
      final TreeMap < Float, Vert3 > result = new TreeMap <>();
      for ( int j = 0; j < vertLen; ++j ) {
         final float fac = nearBound + scalar * ( dists[j] - minDist );
         result.put(fac, verts[j]);
      }

      return result;
   }

   /**
    * Creates a sphere; defaults to a UV sphere.
    *
    * @param target the output mesh
    *
    * @return the sphere
    */
   public static Mesh3 sphere ( final Mesh3 target ) {

      return Mesh3.uvSphere(target);
   }

   /**
    * Creates a square.
    *
    * @param target the output mesh
    *
    * @return the square
    */
   public static final Mesh3 square ( final Mesh3 target ) {

      return Mesh3.square(Mesh3.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a square. Useful when representing an image plane with a mesh
    * entity.
    *
    * @param poly   the polygon type
    * @param target the output mesh
    *
    * @return the square
    */
   public static final Mesh3 square ( final PolyType poly,
      final Mesh3 target ) {

      /* Retained to make image planes easier to create in 3D. */

      target.name = "Square";

      target.coords = Vec3.resize(target.coords, 4);
      target.coords[0].set(-0.5f, -0.5f, 0.0f);
      target.coords[1].set(0.5f, -0.5f, 0.0f);
      target.coords[2].set(0.5f, 0.5f, 0.0f);
      target.coords[3].set(-0.5f, 0.5f, 0.0f);

      target.texCoords = Vec2.resize(target.texCoords, 4);
      target.texCoords[0].set(0.0f, 1.0f);
      target.texCoords[1].set(1.0f, 1.0f);
      target.texCoords[2].set(1.0f, 0.0f);
      target.texCoords[3].set(0.0f, 0.0f);

      target.normals = Vec3.resize(target.normals, 1);
      Vec3.up(target.normals[0]);

      switch ( poly ) {
         case NGON:
         case QUAD: {
            target.faces = new int[][][] { { { 0, 0, 0 }, { 1, 1, 0 }, { 2, 2,
               0 }, { 3, 3, 0 } } };
         }
            break;

         case TRI:
         default: {
            target.faces = new int[][][] { { { 0, 0, 0 }, { 1, 1, 0 }, { 2, 2,
               0 } }, { { 0, 0, 0 }, { 2, 2, 0 }, { 3, 3, 0 } } };
         }
      }

      return target;
   }

   /**
    * Creates a tetrahedron, a Platonic solid with 4 faces and 4 coordinates.
    *
    * @param target the output mesh
    *
    * @return the tetrahedron
    */
   public static final Mesh3 tetrahedron ( final Mesh3 target ) {

      /*
       * double r = 0.5d; double a = r * Math.sqrt(8.0d / 9.0d); double b = r *
       * Math.sqrt(2.0d / 9.0d); double c = r * Math.sqrt(2.0d / 3.0d); double d
       * = r * (1.0d / 3.0d); 0.47140452079103168293389624140323,
       * 0.23570226039551584146694812070162, 0.40824829046386301636621401245098
       */

      target.name = "Tetrahedron";

      target.coords = Vec3.resize(target.coords, 4);
      target.coords[0].set(0.0f, 0.47140452f, -0.16666667f);
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

      /* @formatter:off */
      target.faces = new int[][][] {
         { { 0, 0, 0 }, { 2, 2, 0 }, { 1, 1, 0 } },
         { { 0, 2, 1 }, { 1, 1, 1 }, { 3, 0, 1 } },
         { { 0, 1, 3 }, { 3, 0, 3 }, { 2, 2, 3 } },
         { { 1, 2, 2 }, { 2, 1, 2 }, { 3, 0, 2 } } };
      /* @formatter:on */

      return target;
   }

   /**
    * Creates a flat representation of a mesh's texture coordinates as a
    * net, or map.
    *
    * @param source the source mesh
    * @param target the output mesh
    *
    * @return the texture map
    */
   @Experimental
   public static Mesh2 textureMap ( final Mesh3 source, final Mesh2 target ) {

      final Vec2[] vtsSrc = source.texCoords;
      final int[][][] fsSrc = source.faces;

      final int vtsSrcLen = vtsSrc.length;
      final int fsSrcLen = fsSrc.length;

      final Vec2[] vsTrg = target.coords = Vec2.resize(target.coords,
         vtsSrcLen);
      final Vec2[] vtsTrg = target.texCoords = Vec2.resize(target.texCoords,
         vtsSrcLen);

      /* Copy texture coordinates to target coordinates. */
      for ( int i = 0; i < vtsSrcLen; ++i ) {
         final Vec2 vtSrc = vtsSrc[i];
         vsTrg[i].set(vtSrc);
         vtsTrg[i].set(vtSrc);
      }

      final int[][][] fsTrg = target.faces = new int[fsSrcLen][][];
      for ( int i = 0; i < fsSrcLen; ++i ) {
         final int[][] fSrc = fsSrc[i];
         final int fSrcLen = fSrc.length;
         final int[][] fTrg = fsTrg[i] = new int[fSrcLen][2];
         for ( int j = 0; j < fSrcLen; ++j ) {
            final int[] vertSrc = fSrc[j];
            final int[] vertTrg = fTrg[j];
            vertTrg[0] = vertSrc[1];
            vertTrg[1] = vertSrc[1];
         }
      }

      return target;
   }

   /**
    * Creates a torus, or doughnut. The hole opens onto the y axis.
    *
    * @param thickness tube thickness
    * @param sectors   number of sectors
    * @param panels    number of panels
    * @param poly      the polygon type
    * @param target    the output mesh
    *
    * @return the torus
    */
   public static Mesh3 torus ( final float thickness, final int sectors,
      final int panels, final PolyType poly, final Mesh3 target ) {

      target.name = "Torus";

      /* Validate arguments. */
      final int vSect = sectors < 3 ? 3 : sectors;
      final int vPanl = panels < 3 ? 3 : panels;
      final float vThick = Utils.clamp(thickness, IUtils.EPSILON, 1.0f
         - IUtils.EPSILON);

      /* Radii. */
      final float rho0 = 0.5f;
      final float rho1 = rho0 * vThick;

      /* Values for array accesses. */
      final int vSect1 = vSect + 1;
      final int vPanl1 = vPanl + 1;
      final int vLen = vPanl * vSect;
      final int vtLen = vPanl1 * vSect1;
      final boolean isTri = poly == PolyType.TRI;
      final int fsLen = isTri ? vLen + vLen : vLen;

      /* Reallocate arrays. */
      target.coords = Vec3.resize(target.coords, vLen);
      target.texCoords = Vec2.resize(target.texCoords, vtLen);
      target.normals = Vec3.resize(target.normals, vLen);
      target.faces = isTri ? new int[fsLen][3][3] : new int[fsLen][4][3];

      /* Cache shortcuts. */
      final Vec3[] vs = target.coords;
      final Vec2[] vts = target.texCoords;
      final Vec3[] vns = target.normals;
      final int[][][] fs = target.faces;

      /* Index conversions. Because scNorm is used, no Tau multiplication. */
      final float toU = 1.0f / vSect;
      final float toV = 1.0f / vPanl;
      final float toTheta = toU;
      final float toPhi = toV;

      int faceIdx = 0;
      final int faceStride = isTri ? 2 : 1;

      /* Populate coordinates, normals and faces. */
      for ( int k = 0; k < vLen; ++k ) {
         final int i = k / vSect;
         final int j = k % vSect;

         /* Find theta and phi. */
         final float phi = -0.5f + i * toPhi;
         final float cosPhi = Utils.scNorm(phi);
         final float sinPhi = Utils.scNorm(phi - 0.25f);

         final float rhoCosPhi = rho0 + rho1 * cosPhi;
         final float rhoSinPhi = rho1 * sinPhi;

         final float theta = j * toTheta;
         final float cosTheta = Utils.scNorm(theta);
         final float sinTheta = Utils.scNorm(theta - 0.25f);

         /* Set coordinates and normals. */
         vs[k].set(rhoCosPhi * cosTheta, -rhoSinPhi, rhoCosPhi * sinTheta);
         vns[k].set(cosPhi * cosTheta, -sinPhi, cosPhi * sinTheta);

         /* For converting from 2D to 1D array (idx = y * width + x) . */
         final int iVNext = ( i + 1 ) % vPanl;

         final int vOffCurr = i * vSect; // this is not equal to k.
         final int vOffNext = iVNext * vSect;

         final int vtOffCurr = i * vSect1;
         final int vtOffNext = vtOffCurr + vSect1;

         final int jVtNext = j + 1;
         final int jVNext = jVtNext % vSect;

         /* Coordinate and normal indices. */
         final int v00 = vOffCurr + j;
         final int v10 = vOffCurr + jVNext;
         final int v11 = vOffNext + jVNext;
         final int v01 = vOffNext + j;

         /* Texture coordinate indices. */
         final int vt00 = vtOffCurr + j;
         final int vt10 = vtOffCurr + jVtNext;
         final int vt11 = vtOffNext + jVtNext;
         final int vt01 = vtOffNext + j;

         if ( isTri ) {
            /* @formatter:off */

            /* Triangle 0 */
            final int[][] tri0 = fs[faceIdx];
            final int[] a0 = tri0[0];
            a0[0] = v00; a0[1] = vt00; a0[2] = v00;
            final int[] b0 = tri0[1];
            b0[0] = v10; b0[1] = vt10; b0[2] = v10;
            final int[] c0 = tri0[2];
            c0[0] = v11; c0[1] = vt11; c0[2] = v11;

            /* Triangle 1 */
            final int[][] tri1 = fs[faceIdx + 1];
            final int[] a1 = tri1[0];
            a1[0] = v00; a1[1] = vt00; a1[2] = v00;
            final int[] b1 = tri1[1];
            b1[0] = v11; b1[1] = vt11; b1[2] = v11;
            final int[] c1 = tri1[2];
            c1[0] = v01; c1[1] = vt01; c1[2] = v01;
            /* @formatter:on */

         } else {

            /* @formatter:off */
            final int[][] quad = fs[faceIdx];
            final int[] a = quad[0];
            a[0] = v00; a[1] = vt00; a[2] = v00;
            final int[] b = quad[1];
            b[0] = v10; b[1] = vt10; b[2] = v10;
            final int[] c = quad[2];
            c[0] = v11; c[1] = vt11; c[2] = v11;
            final int[] d = quad[3];
            d[0] = v01; d[1] = vt01; d[2] = v01;
            /* @formatter:on */
         }

         faceIdx += faceStride;
      }

      /* Populate texture coordinates. */
      for ( int k = 0; k < vtLen; ++k ) {
         vts[k].set(k % vSect1 * toU, 1.0f - k / vSect1 * toV);
      }

      return target;
   }

   /**
    * Creates a torus, or doughnut. The hole opens onto the y axis.
    *
    * @param thickness tube thickness
    * @param target    the output mesh
    *
    * @return the torus
    */
   public static Mesh3 torus ( final float thickness, final Mesh3 target ) {

      return Mesh3.torus(thickness, IMesh.DEFAULT_CIRCLE_SECTORS,
         IMesh.DEFAULT_CIRCLE_SECTORS >> 1, Mesh3.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a torus, or doughnut. The hole opens onto the y axis.
    *
    * @param sectors number of sectors
    * @param panels  number of panels
    * @param target  the output mesh
    *
    * @return the torus
    */
   public static Mesh3 torus ( final int sectors, final int panels,
      final Mesh3 target ) {

      return Mesh3.torus(IMesh.DEFAULT_OCULUS, sectors, panels,
         Mesh3.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a torus, or doughnut. The hole opens onto the y axis.
    *
    * @param target the output mesh
    *
    * @return the torus
    */
   public static Mesh3 torus ( final Mesh3 target ) {

      return Mesh3.torus(IMesh.DEFAULT_OCULUS, IMesh.DEFAULT_CIRCLE_SECTORS,
         IMesh.DEFAULT_CIRCLE_SECTORS >> 1, Mesh3.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Traces the perimeter of each face of the source mesh. Returns a target
    * mesh where all faces have the number of vertices specified by the count;
    * each face will be inscribed within the source face, where the target
    * vertices lie on an edge of the source. The perimeter is treated as a
    * percent in the range [0.0, 1.0] , so, for example, a square face's
    * corners will occur at 0.0, 0.25, 0.5 and 0.75 .
    *
    * @param source the source mesh
    * @param count  the number of vertices
    * @param offset the factor offset
    * @param target the output mesh
    *
    * @return the traced mesh
    */
   @Experimental
   public static Mesh3 tracePerimeter ( final Mesh3 source, final int count,
      final float offset, final Mesh3 target ) {

      target.name = "Trace";

      final int vcount = count < 3 ? 3 : count;
      final float toStep = 1.0f / vcount;

      final Vec3[] vsSrc = source.coords;
      final Vec2[] vtsSrc = source.texCoords;
      final int[][][] fsSrc = source.faces;
      final int fsSrcLen = fsSrc.length;
      final int trgLen = fsSrcLen * vcount;

      final Vec3[] vsTrg = target.coords = Vec3.resize(target.coords, trgLen);
      final Vec2[] vtsTrg = target.texCoords = Vec2.resize(target.texCoords,
         trgLen);
      final int[][][] fsTrg = target.faces = new int[fsSrcLen][vcount][3];

      for ( int k = 0, i = 0; i < fsSrcLen; ++i ) {
         final int[][] fSrc = fsSrc[i];
         final int fSrcLen = fSrc.length;
         final float fSrcLenf = fSrc.length;
         final int[][] fTrg = fsTrg[i];

         for ( int j = 0; j < vcount; ++j, ++k ) {
            final float step = offset + j * toStep;
            final float tScaled = fSrcLenf * Utils.mod1(step);
            final int tTrunc = ( int ) tScaled;

            final int[] a = fSrc[tTrunc];
            final int[] b = fSrc[ ( tTrunc + 1 ) % fSrcLen];

            final float t = tScaled - tTrunc;
            final float u = 1.0f - t;

            final Vec3 vaSrc = vsSrc[a[0]];
            final Vec3 vbSrc = vsSrc[b[0]];

            final Vec2 vtaSrc = vtsSrc[a[1]];
            final Vec2 vtbSrc = vtsSrc[b[1]];

            vsTrg[k].set(u * vaSrc.x + t * vbSrc.x, u * vaSrc.y + t * vbSrc.y, u
               * vaSrc.z + t * vbSrc.z);

            vtsTrg[k].set(u * vtaSrc.x + t * vtbSrc.x, u * vtaSrc.y + t
               * vtbSrc.y);

            final int[] vertTrg = fTrg[j];
            vertTrg[0] = k;
            vertTrg[1] = k;
            vertTrg[2] = k;
         }
      }

      target.shadeFlat(0, fsSrcLen);
      return target;
   }

   /**
    * Restructures the mesh so that each face index refers to unique data,
    * indifferent to redundancies. As a consequence, coordinate, texture
    * coordinate and normal arrays are of equal length and face indices are
    * easier to read and understand. Useful for making a mesh similar to those
    * in Unity or p5js . Similar to 'ripping' vertices or 'tearing' edges in
    * Blender.
    *
    * @param source the source mesh
    * @param target the target mesh
    *
    * @return the mesh
    */
   public static Mesh3 uniformData ( final Mesh3 source, final Mesh3 target ) {

      /* Find length of uniform data. */
      int uniformLen = 0;
      final int[][][] fsSrc = source.faces;
      final int fsSrcLen = fsSrc.length;
      for ( int i = 0; i < fsSrcLen; ++i ) { uniformLen += fsSrc[i].length; }

      /* Allocate new arrays. */
      final int[][][] fsTrg = new int[fsSrcLen][][];
      final boolean same = source == target;
      final Vec3[] vsTrg = same ? new Vec3[uniformLen] : Vec3.resize(
         target.coords, uniformLen);
      final Vec2[] vtsTrg = same ? new Vec2[uniformLen] : Vec2.resize(
         target.texCoords, uniformLen);
      final Vec3[] vnsTrg = same ? new Vec3[uniformLen] : Vec3.resize(
         target.normals, uniformLen);

      /* Account for scenario where source and target are same. */
      if ( same ) {
         for ( int i = 0; i < uniformLen; ++i ) {
            vsTrg[i] = new Vec3();
            vtsTrg[i] = new Vec2();
            vnsTrg[i] = new Vec3();
         }
      }

      /* Cache shortcuts to old arrays. */
      final Vec3[] vsSrc = source.coords;
      final Vec2[] vtsSrc = source.texCoords;
      final Vec3[] vnsSrc = source.normals;

      /* Reassign. */
      for ( int k = 0, i = 0; i < fsSrcLen; ++i ) {
         final int[][] fSrc = fsSrc[i];
         final int fLen = fSrc.length;
         final int[][] fTrg = fsTrg[i] = new int[fLen][3];
         for ( int j = 0; j < fLen; ++j, ++k ) {
            final int[] vertSrc = fSrc[j];
            final int[] vertTrg = fTrg[j];

            vsTrg[k].set(vsSrc[vertSrc[0]]);
            vtsTrg[k].set(vtsSrc[vertSrc[1]]);
            vnsTrg[k].set(vnsSrc[vertSrc[2]]);

            /* Update face indices. */
            vertTrg[0] = k;
            vertTrg[1] = k;
            vertTrg[2] = k;
         }
      }

      /* Update references. */
      target.coords = vsTrg;
      target.texCoords = vtsTrg;
      target.normals = vnsTrg;
      target.faces = fsTrg;

      return target;
   }

   /**
    * Creates a UV sphere. The longitudes, or meridians, run through the
    * sphere's poles, and correspond to the azimuth in spherical coordinates.
    * The latitudes correspond to the inclination.
    *
    * @param longitudes the longitudes
    * @param latitudes  the latitudes
    * @param target     the output mesh
    *
    * @return the sphere
    */
   public static Mesh3 uvSphere ( final int longitudes, final int latitudes,
      final Mesh3 target ) {

      return Mesh3.uvSphere(longitudes, latitudes, Mesh3.DEFAULT_POLY_TYPE,
         target);
   }

   /**
    * Creates a UV sphere. The longitudes, or meridians, run through the
    * sphere's poles, and correspond to the azimuth in spherical coordinates.
    * The latitudes correspond to the inclination. Supplying half as many
    * latitudes as longitudes will provide the best results.
    *
    * @param longitudes the longitudes
    * @param latitudes  the latitudes
    * @param poly       the polygon type
    * @param target     the output mesh
    *
    * @return the sphere
    */
   public static Mesh3 uvSphere ( final int longitudes, final int latitudes,
      final PolyType poly, final Mesh3 target ) {

      target.name = "Sphere.Uv";

      /* Validate arguments. */
      final int lons = longitudes < 3 ? 3 : longitudes;
      final int lats = latitudes < 1 ? 1 : latitudes;
      final float radius = 0.5f;
      final boolean isQuad = poly != PolyType.TRI;

      /*
       * UV coordinates require an extra longitude. The poles mean faces have
       * one less latitude in a for loop.
       */
      final int lonsp1 = lons + 1;
      final int latsn1 = lats - 1;
      final int latsp1 = lats + 1;

      final int latsLons = lats * lons;
      final int latsn1Lons = latsn1 * lons;
      final int latsLonsp1 = lats * lonsp1;

      /*
       * Reallocate arrays. The 2 coordinates come from the poles. The 2 *
       * longitudes texture coordinates come from the triangle fan at the poles.
       */
      final int vLen = latsLons + 2;
      final int vtLen = latsLonsp1 + lons + lons;
      final Vec3[] vs = target.coords = Vec3.resize(target.coords, vLen);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
         vtLen);
      final Vec3[] vns = target.normals = Vec3.resize(target.normals, vLen);

      /*
       * The length of the faces array comes from the north and south cap being
       * triangle fans around the longitudes (2 * lons) and the middle being
       * triangles (latsn1 * lons * 2), or (latsn1 * lons) for quads.
       */
      final int midCount = isQuad ? latsn1 * lons : latsn1 * lons * 2;
      final int fsLen = lons + lons + midCount;

      /*
       * If quadrilaterals are chosen, then the faces array will be jagged. The
       * North and South cap face inner arrays will have a length of 3; the
       * mid-latitudes will have a length of 4.
       */
      final int[][][] fs = target.faces = isQuad ? new int[fsLen][4][3]
         : new int[fsLen][3][3];

      /*
       * Due to the two poles, linear interpolation between latitudes is not i /
       * (latitudes - 1.0); it is (i + 1) / (latitudes + 1.0) . Inclination is
       * half the range as azimuth (TAU / 2.0 is normalized to 1.0 / 2.0) .
       */
      final float toTexS = 1.0f / lons;
      final float toTexT = 1.0f / latsp1;
      final float toAzim = 1.0f / lons;
      final float toIncl = 0.5f / latsp1;

      /* Set North pole. */
      final int last0 = vLen - 1;
      vs[last0].set(0.0f, 0.0f, 0.5f);
      vns[last0].set(0.0f, 0.0f, 1.0f);

      /* Set South pole. Offset subsequent vertex indices by 1. */
      vs[0].set(0.0f, 0.0f, -0.5f);
      vns[0].set(0.0f, 0.0f, -1.0f);

      /* Offsets for north cap. */
      final int vIdxOff = last0 - lons;
      final int vtPoleOff = vtLen - lons;
      final int vtIdxOff = vtPoleOff - lonsp1;
      final int fsIdxOff = fsLen - lons;

      /* Deal with the poles: Faces and texture coordinates. */
      for ( int j = 0; j < lons; ++j ) {

         /* Texture coordinates. */
         final float sTex = ( j + 0.5f ) * toTexS;
         vts[j].set(sTex, 1.0f);
         vts[vtLen - lons + j].set(sTex, 0.0f);

         /* Faces. */
         final int k = j + 1;
         final int n = k % lons;
         final int h = 1 + n;
         final int m = lons + j;

         final int[][] triSouth = isQuad ? fs[j] = new int[3][3] : fs[j];
         final int[][] triNorth = isQuad ? fs[fsIdxOff + j] = new int[3][3]
            : fs[fsIdxOff + j];

         /* Polar vertex. */
         final int[] north0 = triNorth[2];
         north0[0] = last0;
         north0[1] = vtPoleOff + j;
         north0[2] = last0;

         final int[] north1 = triNorth[1];
         north1[0] = vIdxOff + n;
         north1[1] = vtIdxOff + k;
         north1[2] = vIdxOff + n;

         final int[] north2 = triNorth[0];
         north2[0] = vIdxOff + j;
         north2[1] = vtIdxOff + j;
         north2[2] = vIdxOff + j;

         /* Polar vertex. */
         final int[] south0 = triSouth[2];
         south0[0] = 0;
         south0[1] = j;
         south0[2] = 0;

         final int[] south1 = triSouth[1];
         south1[0] = k;
         south1[1] = m;
         south1[2] = k;

         final int[] south2 = triSouth[0];
         south2[0] = h;
         south2[1] = m + 1;
         south2[2] = h;
      }

      /* Coordinates and normals. */
      for ( int k = 0; k < latsLons; ++k ) {
         final int i = latsn1 - k / lons; /* Latitudes. */
         final int j = k % lons; /* Longitudes. */

         /* Account for offset of pole. */
         final float incl = 0.25f - ( i + 1.0f ) * toIncl;
         final float cosIncl = Utils.scNorm(incl);
         final float sinIncl = Utils.scNorm(incl - 0.25f);

         final float rhoCosIncl = radius * cosIncl;
         final float rhoSinIncl = radius * sinIncl;

         final float azim = j * toAzim;
         final float cosAzim = Utils.scNorm(azim);
         final float sinAzim = Utils.scNorm(azim - 0.25f);

         final int vIdx = 1 + k;
         vs[vIdx].set(rhoCosIncl * cosAzim, rhoCosIncl * sinAzim, rhoSinIncl);
         vns[vIdx].set(cosIncl * cosAzim, cosIncl * sinAzim, sinIncl);
      }

      /* Texture coordinates. */
      for ( int k = 0; k < latsLonsp1; ++k ) {
         final int i = latsn1 - k / lonsp1;
         final int j = k % lonsp1;
         vts[lons + k].set(j * toTexS, ( i + 1.0f ) * toTexT);
      }

      /* Middle Faces. */
      final int stride = isQuad ? 1 : 2;
      for ( int k = 0; k < latsn1Lons; ++k ) {
         final int i = k / lons;
         final int j = k % lons;
         final int fIdx = lons + k * stride;

         /* For coordinates and normals. */
         final int currentLat0 = 1 + i * lons;
         final int nextLat0 = currentLat0 + lons;

         /* For texture coordinates. */
         final int currentLat1 = lons + i * lonsp1;
         final int nextLat1 = currentLat1 + lonsp1;

         /* Wrap around to first longitude at last. */
         final int nextLon1 = j + 1;
         final int nextLon0 = nextLon1 % lons;

         /* Coordinate and normal indices. */
         final int v00 = nextLat0 + j;
         final int v10 = nextLat0 + nextLon0;
         final int v11 = currentLat0 + nextLon0;
         final int v01 = currentLat0 + j;

         /* Texture coordinate indices. */
         final int vt00 = nextLat1 + j;
         final int vt10 = nextLat1 + nextLon1;
         final int vt11 = currentLat1 + nextLon1;
         final int vt01 = currentLat1 + j;

         /* @formatter:off */
         if ( isQuad ) {

            final int[][] quad = fs[fIdx];
            final int[] q0 = quad[0];
            q0[0] = v00; q0[1] = vt00; q0[2] = v00;
            final int[] q1 = quad[1];
            q1[0] = v01; q1[1] = vt01; q1[2] = v01;
            final int[] q2 = quad[2];
            q2[0] = v11; q2[1] = vt11; q2[2] = v11;
            final int[] q3 = quad[3];
            q3[0] = v10; q3[1] = vt10; q3[2] = v10;

         } else {

            final int[][] tri0 = fs[fIdx];
            final int[] tri00 = tri0[0];
            tri00[0] = v00; tri00[1] = vt00; tri00[2] = v00;
            final int[] tri01 = tri0[1];
            tri01[0] = v01; tri01[1] = vt01; tri01[2] = v01;
            final int[] tri02 = tri0[2];
            tri02[0] = v11; tri02[1] = vt11; tri02[2] = v11;

            final int[][] tri1 = fs[fIdx + 1];
            final int[] tri10 = tri1[0];
            tri10[0] = v00; tri10[1] = vt00; tri10[2] = v00;
            final int[] tri11 = tri1[1];
            tri11[0] = v11; tri11[1] = vt11; tri11[2] = v11;
            final int[] tri12 = tri1[2];
            tri12[0] = v10; tri12[1] = vt10; tri12[2] = v10;

         }
         /* @formatter:on */
      }

      return target;

   }

   /**
    * Creates a UV sphere. The longitudes, or meridians, run through the
    * sphere's poles, and correspond to the azimuth in spherical coordinates.
    * The latitudes correspond to the inclination.
    *
    * @param target the output mesh
    *
    * @return the sphere
    */
   public static Mesh3 uvSphere ( final Mesh3 target ) {

      return Mesh3.uvSphere(IMesh.DEFAULT_CIRCLE_SECTORS,
         IMesh.DEFAULT_CIRCLE_SECTORS >> 1, target);
   }

   /**
    * Creates a UV sphere. The longitudes, or meridians, run through the
    * sphere's poles, and correspond to the azimuth in spherical coordinates.
    * The latitudes correspond to the inclination.
    *
    * @param poly   the polygon type
    * @param target the output mesh
    *
    * @return the sphere
    */
   public static Mesh3 uvSphere ( final PolyType poly, final Mesh3 target ) {

      return Mesh3.uvSphere(IMesh.DEFAULT_CIRCLE_SECTORS,
         IMesh.DEFAULT_CIRCLE_SECTORS >> 1, poly, target);
   }

   /**
    * An internal helper function to accumulate the minimum and maximum points
    * in a mesh. This may be called either by a single mesh, or by a mesh
    * entity seeking the minimum and maximum for a collection of meshes.
    *
    * @param mesh the mesh
    * @param lb   the lower bound
    * @param ub   the upper bound
    */
   static void accumMinMax ( final Mesh3 mesh, final Vec3 lb, final Vec3 ub ) {

      final Vec3[] coords = mesh.coords;
      final int len = coords.length;

      for ( int i = 0; i < len; ++i ) {
         final Vec3 coord = coords[i];
         final float x = coord.x;
         final float y = coord.y;
         final float z = coord.z;

         /* Minimum, maximum need individual if checks, not if-else. */
         if ( x < lb.x ) { lb.x = x; }
         if ( x > ub.x ) { ub.x = x; }
         if ( y < lb.y ) { lb.y = y; }
         if ( y > ub.y ) { ub.y = y; }
         if ( z < lb.z ) { lb.z = z; }
         if ( z > ub.z ) { ub.z = z; }
      }
   }

   /**
    * An internal helper function to accumulate the minimum and maximum points
    * in a mesh. This may be called either by a single mesh, or by a mesh
    * entity seeking the minimum and maximum for a collection of meshes. The
    * transform parameter accepts the entity's transform, while the coordinate
    * stores a mesh coordinate that has been multiplied by the transform.
    *
    * @param mesh the mesh
    * @param lb   the lower bound
    * @param ub   the upper bound
    * @param tr   the transform
    * @param co   the coordinate
    *
    * @see Transform3#mulPoint(Transform3, Vec3, Vec3)
    */
   static void accumMinMax ( final Mesh3 mesh, final Vec3 lb, final Vec3 ub,
      final Transform3 tr, final Vec3 co ) {

      final Vec3[] coords = mesh.coords;
      final int len = coords.length;

      for ( int i = 0; i < len; ++i ) {
         Transform3.mulPoint(tr, coords[i], co);
         final float x = co.x;
         final float y = co.y;
         final float z = co.z;

         /* Minimum, maximum need individual if checks, not if-else. */
         if ( x < lb.x ) { lb.x = x; }
         if ( x > ub.x ) { ub.x = x; }
         if ( y < lb.y ) { lb.y = y; }
         if ( y > ub.y ) { ub.y = y; }
         if ( z < lb.z ) { lb.z = z; }
         if ( z > ub.z ) { ub.z = z; }
      }
   }

   /**
    * Internal helper function to convert curves to meshes. Evaluates a Bezier
    * curve to line segments based on a resolution after testing whether or
    * not the rear and fore handles of the curve are colinear.
    *
    * @param source      the source curve
    * @param resolution  the resolution
    * @param colinearTol the colinear tolerance
    * @param points      the point list
    * @param dir0        the first direction
    * @param dir1        the second direction
    *
    * @return the point list
    *
    * @see Utils#clamp01(float)
    * @see Vec3#bezierPoint(Vec3, Vec3, Vec3, Vec3, float, Vec3)
    * @see Vec3#dot(Vec3, Vec3)
    * @see Vec3#subNorm(Vec3, Vec3, Vec3)
    */
   static ArrayList < Vec3 > fromCurve3 ( final Curve3 source,
      final int resolution, final float colinearTol, final ArrayList <
         Vec3 > points, final Vec3 dir0, final Vec3 dir1 ) {

      if ( !source.closedLoop ) { return points; }

      final Iterator < Knot3 > itr = source.iterator();
      Knot3 prevKnot = source.getLast();

      if ( resolution < 1 ) {
         for ( Knot3 currKnot = null; itr.hasNext(); prevKnot = currKnot ) {
            currKnot = itr.next();
            points.add(new Vec3(prevKnot.coord));
         }
      } else {
         final float vtol = Utils.clamp01(1.0f - colinearTol);
         final int resp1 = resolution + 1;
         final float toPercent = 1.0f / resp1;

         for ( Knot3 currKnot = null; itr.hasNext(); prevKnot = currKnot ) {
            currKnot = itr.next();
            final Vec3 coPrev = prevKnot.coord;
            final Vec3 fhPrev = prevKnot.foreHandle;
            final Vec3 rhNext = currKnot.rearHandle;
            final Vec3 coNext = currKnot.coord;

            /* Add previous knot coordinate no matter the colinear status. */
            points.add(new Vec3(coPrev));

            Vec3.subNorm(fhPrev, coPrev, dir0);
            Vec3.subNorm(rhNext, coNext, dir1);
            final float dotp = Vec3.dot(dir0, dir1);
            if ( dotp > -vtol && dotp < vtol ) {
               for ( int i = 1; i < resp1; ++i ) {
                  points.add(Vec3.bezierPoint(coPrev, fhPrev, rhNext, coNext, i
                     * toPercent, new Vec3()));
               }
            }
         }
      }

      return points;
   }

   /**
    * An internal helper function that reassigns a cube's texture coordinates
    * and face indices based on the desired polygon type and UV profile.
    *
    * @param poly    the polygon type
    * @param profile the texture profile
    * @param target  the output mesh
    *
    * @return the updated mesh
    */
   protected static Mesh3 cubeTexCoords ( final PolyType poly,
      final UvProfile.Cube profile, final Mesh3 target ) {

      switch ( profile ) {
         case CROSS:

            target.texCoords = Vec2.resize(target.texCoords, 14);
            target.texCoords[0].set(0.625f, 0.0f);
            target.texCoords[1].set(0.375f, 0.0f);
            target.texCoords[2].set(0.375f, 0.75f);
            target.texCoords[3].set(0.625f, 0.75f);
            target.texCoords[4].set(0.375f, 1.0f);
            target.texCoords[5].set(0.625f, 1.0f);
            target.texCoords[6].set(0.625f, 0.5f);
            target.texCoords[7].set(0.375f, 0.5f);
            target.texCoords[8].set(0.625f, 0.25f);
            target.texCoords[9].set(0.375f, 0.25f);
            target.texCoords[10].set(0.125f, 0.5f);
            target.texCoords[11].set(0.125f, 0.25f);
            target.texCoords[12].set(0.875f, 0.25f);
            target.texCoords[13].set(0.875f, 0.5f);

            switch ( poly ) {
               case TRI:
                  /* @formatter:off */
                  target.faces = new int[][][] {
                     { { 6,  7, 0 }, { 7,  6, 0 }, { 5,  8, 0 } },
                     { { 6,  7, 0 }, { 5,  8, 0 }, { 4,  9, 0 } },
                     { { 7,  6, 1 }, { 3, 13, 1 }, { 1, 12, 1 } },
                     { { 7,  6, 1 }, { 1, 12, 1 }, { 5,  8, 1 } },
                     { { 2, 10, 2 }, { 6,  7, 2 }, { 4,  9, 2 } },
                     { { 2, 10, 2 }, { 4,  9, 2 }, { 0, 11, 2 } },
                     { { 4,  9, 3 }, { 5,  8, 3 }, { 1,  0, 3 } },
                     { { 4,  9, 3 }, { 1,  0, 3 }, { 0,  1, 3 } },
                     { { 0,  4, 4 }, { 1,  5, 4 }, { 3,  3, 4 } },
                     { { 0,  4, 4 }, { 3,  3, 4 }, { 2,  2, 4 } },
                     { { 2,  2, 5 }, { 3,  3, 5 }, { 7,  6, 5 } },
                     { { 2,  2, 5 }, { 7,  6, 5 }, { 6,  7, 5 } } };
                  /* @formatter:on */
                  break;

               case NGON:
               case QUAD:
               default:
                  /* @formatter:off */
                  target.faces = new int[][][] {
                     { { 6,  7, 0 }, { 7,  6, 0 }, { 5,  8, 0 }, { 4,  9, 0 } },
                     { { 7,  6, 1 }, { 3, 13, 1 }, { 1, 12, 1 }, { 5,  8, 1 } },
                     { { 2, 10, 2 }, { 6,  7, 2 }, { 4,  9, 2 }, { 0, 11, 2 } },
                     { { 4,  9, 3 }, { 5,  8, 3 }, { 1,  0, 3 }, { 0,  1, 3 } },
                     { { 0,  4, 4 }, { 1,  5, 4 }, { 3,  3, 4 }, { 2,  2, 4 } },
                     { { 2,  2, 5 }, { 3,  3, 5 }, { 7,  6, 5 }, { 6,  7, 5 } } };
                  /* @formatter:on */
            }

            break;

         case DIAGONAL:

            target.texCoords = Vec2.resize(target.texCoords, 14);
            target.texCoords[0].set(IUtils.TWO_THIRDS, 0.0f);
            target.texCoords[1].set(1.0f, 0.0f);
            target.texCoords[2].set(IUtils.ONE_THIRD, 0.25f);
            target.texCoords[3].set(IUtils.TWO_THIRDS, 0.25f);
            target.texCoords[4].set(1.0f, 0.25f);
            target.texCoords[5].set(0.0f, 0.5f);
            target.texCoords[6].set(IUtils.ONE_THIRD, 0.5f);
            target.texCoords[7].set(IUtils.TWO_THIRDS, 0.5f);
            target.texCoords[8].set(1.0f, 0.5f);
            target.texCoords[9].set(0.0f, 0.75f);
            target.texCoords[10].set(IUtils.ONE_THIRD, 0.75f);
            target.texCoords[11].set(IUtils.TWO_THIRDS, 0.75f);
            target.texCoords[12].set(0.0f, 1.0f);
            target.texCoords[13].set(IUtils.ONE_THIRD, 1.0f);

            switch ( poly ) {
               case TRI:
                  /* @formatter:off */
                  target.faces = new int[][][] {
                     { { 0,  4, 0 }, { 1,  3, 0 }, { 3,  7, 0 } },
                     { { 0,  4, 0 }, { 3,  7, 0 }, { 2,  8, 0 } },
                     { { 4,  1, 5 }, { 1,  3, 5 }, { 0,  4, 5 } },
                     { { 2, 13, 3 }, { 4,  9, 3 }, { 0, 12, 3 } },
                     { { 7,  6, 4 }, { 3,  7, 4 }, { 1,  3, 4 } },
                     { { 2, 11, 2 }, { 3,  7, 2 }, { 7,  6, 2 } },
                     { { 4,  1, 5 }, { 5,  0, 5 }, { 1,  3, 5 } },
                     { { 7,  6, 4 }, { 1,  3, 4 }, { 5,  2, 4 } },
                     { { 2, 13, 3 }, { 6, 10, 3 }, { 4,  9, 3 } },
                     { { 2, 11, 2 }, { 7,  6, 2 }, { 6, 10, 2 } },
                     { { 6, 10, 1 }, { 5,  5, 1 }, { 4,  9, 1 } },
                     { { 6, 10, 1 }, { 7,  6, 1 }, { 5,  5, 1 } } };
                     /* @formatter:on */
                  break;

               case NGON:
               case QUAD:
               default:
                  /* @formatter:off */
                  target.faces = new int[][][] {
                     { { 2, 13, 0 }, { 3, 10, 0 }, { 1,  9, 0 }, { 0, 12, 0 } },
                     { { 1,  1, 1 }, { 5,  0, 1 }, { 4,  3, 1 }, { 0,  4, 1 } },
                     { { 0,  4, 2 }, { 4,  3, 2 }, { 6,  7, 2 }, { 2,  8, 2 } },
                     { { 3, 10, 3 }, { 7,  6, 3 }, { 5,  5, 3 }, { 1,  9, 3 } },
                     { { 2, 11, 4 }, { 6,  7, 4 }, { 7,  6, 4 }, { 3, 10, 4 } },
                     { { 7,  6, 5 }, { 6,  7, 5 }, { 4,  3, 5 }, { 5,  2, 5 } } };
                  /* @formatter:on */
            }

            break;

         case PER_FACE:
         default:

            target.texCoords = Vec2.resize(target.texCoords, 4);
            target.texCoords[0].set(0.0f, 0.0f);
            target.texCoords[1].set(0.0f, 1.0f);
            target.texCoords[2].set(1.0f, 1.0f);
            target.texCoords[3].set(1.0f, 0.0f);

            switch ( poly ) {
               case TRI:
                  /* @formatter:off */
                  target.faces = new int[][][] {
                     { { 6, 2, 0 }, { 7, 3, 0 }, { 5, 0, 0 } },
                     { { 6, 2, 0 }, { 5, 0, 0 }, { 4, 1, 0 } },
                     { { 7, 1, 1 }, { 3, 2, 1 }, { 1, 3, 1 } },
                     { { 7, 1, 1 }, { 1, 3, 1 }, { 5, 0, 1 } },
                     { { 2, 1, 2 }, { 6, 2, 2 }, { 4, 3, 2 } },
                     { { 2, 1, 2 }, { 4, 3, 2 }, { 0, 0, 2 } },
                     { { 4, 2, 3 }, { 5, 3, 3 }, { 1, 0, 3 } },
                     { { 4, 2, 3 }, { 1, 0, 3 }, { 0, 1, 3 } },
                     { { 0, 2, 4 }, { 1, 3, 4 }, { 3, 0, 4 } },
                     { { 0, 2, 4 }, { 3, 0, 4 }, { 2, 1, 4 } },
                     { { 2, 2, 5 }, { 3, 3, 5 }, { 7, 0, 5 } },
                     { { 2, 2, 5 }, { 7, 0, 5 }, { 6, 1, 5 } } };
                  /* @formatter:on */
                  break;

               case NGON:
               case QUAD:
               default:
                  /* @formatter:off */
                  target.faces = new int[][][] {
                     { { 6, 2, 0 }, { 7, 3, 0 }, { 5, 0, 0 }, { 4, 1, 0 } },
                     { { 7, 1, 1 }, { 3, 2, 1 }, { 1, 3, 1 }, { 5, 0, 1 } },
                     { { 2, 1, 2 }, { 6, 2, 2 }, { 4, 3, 2 }, { 0, 0, 2 } },
                     { { 4, 2, 3 }, { 5, 3, 3 }, { 1, 0, 3 }, { 0, 1, 3 } },
                     { { 0, 2, 4 }, { 1, 3, 4 }, { 3, 0, 4 }, { 2, 1, 4 } },
                     { { 2, 2, 5 }, { 3, 3, 5 }, { 7, 0, 5 }, { 6, 1, 5 } } };
                  /* @formatter:on */
            }
      }

      return target;
   }

   /**
    * Draws a cylinder from an origin point toward a destination point. End
    * caps with a centered triangle fan may optionally be included. This is
    * protected so that multiple public overloaded methods may access it.
    *
    * @param xOrig       origin x
    * @param yOrig       origin y
    * @param zOrig       origin z
    * @param xDest       destination x
    * @param yDest       destination y
    * @param zDest       destination z
    * @param sectors     sector count
    * @param includeCaps include end caps
    * @param radius      radius
    * @param target      output cylinder
    *
    * @return the cylinder
    */
   protected static Mesh3 cylinder ( final float xOrig, final float yOrig,
      final float zOrig, final float xDest, final float yDest,
      final float zDest, final int sectors, final boolean includeCaps,
      final float radius, final Mesh3 target ) {

      target.name = "Cylinder";

      /* Find difference between destination and origin. */
      float x0 = xDest - xOrig;
      float y0 = yDest - yOrig;
      float z0 = zDest - zOrig;

      /* If difference's length is zero, invalid inputs. */
      final float m0 = x0 * x0 + y0 * y0 + z0 * z0;
      if ( m0 <= IUtils.EPSILON ) {
         x0 = IUtils.EPSILON + IUtils.EPSILON;
         y0 = x0;
         z0 = x0;
      }

      /* Validate arguments. */
      final int sec = sectors < 3 ? 3 : sectors;
      final float rad = radius < IUtils.EPSILON ? IUtils.EPSILON : radius;

      /* Normalize forward. */
      final float mInv0 = Utils.invSqrtUnchecked(m0);
      final float kx = x0 * mInv0;
      final float ky = y0 * mInv0;
      final float kz = z0 * mInv0;

      /* Find the cross product of forward and reference (0.0, 0.0, 1.0). */
      float x1 = -ky;
      float y1 = kx;
      float z1 = 0.0f;

      /*
       * Forward and up are parallel if the cross product is zero. z1 is already
       * known to be zero, so no check is necessary.
       */
      if ( Utils.approx(x1, 0.0f, IUtils.EPSILON) && Utils.approx(y1, 0.0f,
         IUtils.EPSILON) ) {

         /*
          * If forward and up are parallel, recalculate the cross product
          * between forward and reference (0.0, 1.0, 0.0).
          */
         x1 = kz;
         y1 = 0.0f;
         z1 = -kx;
      }

      /* The cross product is the right axis. Normalize right. */
      final float mInv1 = Utils.invSqrtUnchecked(x1 * x1 + y1 * y1 + z1 * z1);
      final float ix = x1 * mInv1;
      final float iy = y1 * mInv1;
      final float iz = z1 * mInv1;

      /* Cross right against forward to get the up axis. */
      final float x2 = iy * kz - iz * ky;
      final float y2 = iz * kx - ix * kz;
      final float z2 = ix * ky - iy * kx;

      /* Normalize the up axis. */
      final float mInv2 = Utils.invSqrtUnchecked(x2 * x2 + y2 * y2 + z2 * z2);
      final float jx = x2 * mInv2;
      final float jy = y2 * mInv2;
      final float jz = z2 * mInv2;

      /* Values for navigating arrays. */
      final int sec1 = sec + 1;
      final int vLen = sec + sec;
      final int vtLen = sec1 + sec1;

      /* Resize mesh arrays. */
      if ( includeCaps ) {
         target.coords = Vec3.resize(target.coords, vLen + 2);
         target.texCoords = Vec2.resize(target.texCoords, vtLen + sec1);
         target.normals = Vec3.resize(target.normals, sec + 2);
         target.faces = new int[vLen + vLen][3][3];
      } else {
         target.coords = Vec3.resize(target.coords, vLen);
         target.texCoords = Vec2.resize(target.texCoords, vtLen);
         target.normals = Vec3.resize(target.normals, sec);
         target.faces = new int[vLen][3][3];
      }

      /* Cache shortcuts. */
      final Vec3[] vs = target.coords;
      final Vec2[] vts = target.texCoords;
      final Vec3[] vns = target.normals;
      final int[][][] fs = target.faces;

      /*
       * Texture coordinates for sides are separate. While sine and cosine take
       * care of wrapping the coordinates (0 radians == TAU radians) and the
       * modulo operator takes care of wrapping the faces (i % coordsLen returns
       * to 0 when i == coordsLen), in UV coordinates, 0.0 == 1.0 .
       */
      final float toTheta = 1.0f / sec;
      // float toU = 1.0f / sectors;
      // float toTheta = TAU / sectors;
      for ( int i = 0, j = sec1; i < sec1; ++i, ++j ) {
         final float u = 1.0f - i * toTheta;
         // vts[i].set(u, 1.0f);
         // vts[j].set(u, 0.0f);
         // final float u = i * toTheta;
         vts[i].set(u, 0.0f);
         vts[j].set(u, 1.0f);
      }

      /* Side panel vertices and normals. */
      for ( int i = 0, j = sec; i < sec; ++i, ++j ) {
         final float theta = i * toTheta;
         final float cosa = Utils.scNorm(theta);
         final float sina = Utils.scNorm(theta - 0.25f);

         /*
          * Equivalent to multiplying the circle by the look-at matrix formed by
          * the axes. Since the 2D circle's z component is 0, don't worry about
          * multiplying by the forward axis. It should not be necessary to
          * normalize the normal.
          */
         final Vec3 vn = vns[i].set(ix * cosa + jx * sina, iy * cosa + jy
            * sina, iz * cosa + jz * sina);

         /* For coordinates, multiply normals by radius / radii. */
         final Vec3 v0 = vs[i];
         final Vec3 v1 = vs[j];

         v0.x = vn.x * rad;
         v0.y = vn.y * rad;
         v0.z = vn.z * rad;

         /*
          * If you wanted to implement a tapered radius, this would have to be
          * different than v0.
          */
         v1.x = v0.x;
         v1.y = v0.y;
         v1.z = v0.z;

         v0.x += xOrig;
         v0.y += yOrig;
         v0.z += zOrig;

         v1.x += xDest;
         v1.y += yDest;
         v1.z += zDest;

         /* The next vertex in the ring. */
         final int n0 = ( i + 1 ) % sec;
         final int n1 = sec + n0;
         final int st0 = sec1 + i;
         final int st1 = st0 + 1;

         /* Store shortcuts to first triangle. */
         final int[][] tri0 = fs[i];

         /* Set three vertices of the first triangle. */
         final int[] vert00 = tri0[0];
         vert00[0] = i;
         vert00[1] = i;
         vert00[2] = i;

         final int[] vert01 = tri0[1];
         vert01[0] = j;
         vert01[1] = st0;
         vert01[2] = i;

         final int[] vert02 = tri0[2];
         vert02[0] = n1;
         vert02[1] = st1;
         vert02[2] = n0;

         /* Store shortcuts to second triangle. */
         final int[][] tri1 = fs[j];

         /* Set three vertices of the second triangle. */
         final int[] vert10 = tri1[0];
         vert10[0] = n1;
         vert10[1] = st1;
         vert10[2] = n0;

         final int[] vert11 = tri1[1];
         vert11[0] = n0;
         vert11[1] = i + 1;
         vert11[2] = n0;

         final int[] vert12 = tri1[2];
         vert12[0] = i;
         vert12[1] = i;
         vert12[2] = i;

         /* If needed, find end cap texture coordinates. */
         if ( includeCaps ) {
            // vts[vtLen + i].set(cosa * 0.5f + 0.5f, sina * 0.5f + 0.5f);
            vts[vtLen + i].set(0.5f - 0.5f * cosa, 0.5f - 0.5f * sina);
         }
      }

      /* Create triangle fan caps with a central point. */
      if ( includeCaps ) {
         final int len1 = vLen + 1;
         final int vtCenterIdx = vts.length - 1;

         /* Convert origin, destination loose floats to vectors. */
         vs[vLen].set(xOrig, yOrig, zOrig);
         vs[len1].set(xDest, yDest, zDest);

         /* Set UV Center. */
         Vec2.uvCenter(vts[vtCenterIdx]);

         /* Set normals. */
         vns[sec].set(-kx, -ky, -kz);
         vns[sec1].set(kx, ky, kz);

         /* Set faces. */
         for ( int i = 0, j = sec; i < sec; ++i, ++j ) {
            final int k = ( i + 1 ) % sec;
            final int m = vtLen + i;
            final int n = vtLen + k;

            final int[][] cap0 = fs[vLen + i];

            final int[] vert00 = cap0[0];
            vert00[0] = vLen;
            vert00[1] = vtCenterIdx;
            vert00[2] = sec;

            final int[] vert01 = cap0[1];
            vert01[0] = i;
            vert01[1] = m;
            vert01[2] = sec;

            final int[] vert02 = cap0[2];
            vert02[0] = k;
            vert02[1] = n;
            vert02[2] = sec;

            final int[][] cap1 = fs[vLen + j];

            final int[] vert10 = cap1[0];
            vert10[0] = len1;
            vert10[1] = vtCenterIdx;
            vert10[2] = sec1;

            final int[] vert11 = cap1[1];
            vert11[0] = sec + k;
            vert11[1] = n;
            vert11[2] = sec1;

            final int[] vert12 = cap1[2];
            vert12[0] = j;
            vert12[1] = m;
            vert12[2] = sec1;
         }
      }

      return target;
   }

   /**
    * An iterator, which allows a mesh's faces to be accessed in an enhanced
    * for loop.
    */
   public static final class Face3Iterator implements Iterator < Face3 > {

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The mesh being iterated over.
       */
      private final Mesh3 mesh;

      /**
       * The default constructor.
       *
       * @param mesh the mesh to iterate
       */
      public Face3Iterator ( final Mesh3 mesh ) { this.mesh = mesh; }

      /**
       * Tests to see if the iterator has another value.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext ( ) { return this.index < this.mesh.length(); }

      /**
       * Gets the next value in the iterator.
       *
       * @return the value
       *
       * @see Mesh3#getFace(int, Face3)
       */
      @Override
      public Face3 next ( ) {

         if ( !this.hasNext() ) { throw new NoSuchElementException(); }
         return this.mesh.getFace(this.index++, new Face3());
      }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

}
