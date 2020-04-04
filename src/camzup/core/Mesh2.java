package camzup.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Organizes data needed to draw a two dimensional shape using vertices and
 * faces. Given that a mesh is primarily a collection of references, it is
 * initialized with null arrays (coordinates, texture coordinates and indices).
 * These are not final, and so can be reassigned.
 */
public class Mesh2 extends Mesh implements Iterable < Face2 >, ISvgWritable {

   /**
    * An array of coordinates in the mesh.
    */
   public Vec2[] coords;

   /**
    * The texture (UV) coordinates that describe how an image is mapped onto the
    * geometry of the mesh. Typically in the range [0.0, 1.0].
    */
   public Vec2[] texCoords;

   /**
    * The default constructor.
    */
   public Mesh2 ( ) { super(); }

   /**
    * Creates a mesh from arrays of faces, coordinates and texture coordinates.
    * The mesh's arrays are set by reference, not by value.
    *
    * @param faces     the faces array
    * @param coords    the coordinates array
    * @param texCoords the texture coordinates array
    */
   public Mesh2 (
      final int[][][] faces,
      final Vec2[] coords,
      final Vec2[] texCoords ) {

      super();
      this.set(faces, coords, texCoords);
   }

   /**
    * Constructs a copy of the source mesh.
    *
    * @param source the source mesh
    */
   public Mesh2 ( final Mesh2 source ) {

      super();
      this.set(source);
   }

   /**
    * Creates a named mesh.
    *
    * @param name the mesh name
    */
   public Mesh2 ( final String name ) { super(name); }

   /**
    * Creates a named mesh from arrays of faces, coordinates and texture
    * coordinates. The mesh's arrays are set by reference, not by value.
    *
    * @param name      the mesh name
    * @param faces     the faces array
    * @param coords    the coordinates array
    * @param texCoords the texture coordinates array
    */
   public Mesh2 (
      final String name,
      final int[][][] faces,
      final Vec2[] coords,
      final Vec2[] texCoords ) {

      super(name);
      this.set(faces, coords, texCoords);
   }

   /**
    * Calculates texture coordinates (UVs) for this mesh. Finds the object-space
    * dimensions of each coordinate, then using the frame as a reference for new
    * UVs, such that the shape acts as a mask for the texture (or, the texture
    * fills the shape without repeating).
    *
    * @return this mesh
    *
    * @see Mesh2#calcDimensions(Mesh2, Vec2, Vec2, Vec2)
    * @see Vec2#div(float, Vec2, Vec2)
    */
   @Chainable
   public Mesh2 calcUvs ( ) {

      final Vec2 dim = new Vec2();
      final Vec2 lb = new Vec2();
      final Vec2 ub = new Vec2();
      Mesh2.calcDimensions(this, dim, lb, ub);

      final int len = this.coords.length;
      this.texCoords = Vec2.resize(this.texCoords, len);

      if ( dim.x == 0.0f || dim.y == 0.0f ) { return this; }
      final float xInv = 1.0f / dim.x;
      final float yInv = 1.0f / dim.y;

      for ( int i = 0; i < len; ++i ) {
         final Vec2 v = this.coords[i];
         final Vec2 vt = this.texCoords[i];
         vt.x = ( v.x - lb.x ) * xInv;
         vt.y = 1.0f - ( v.y - lb.y ) * yInv;
      }

      /* Assign coordinate index to UV index. */
      final int facesLen = this.faces.length;
      for ( int i = 0; i < facesLen; ++i ) {
         final int[][] verts = this.faces[i];
         final int vertsLen = verts.length;
         for ( int j = 0; j < vertsLen; ++j ) {
            verts[j][1] = verts[j][0];
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
   public Mesh2 clone ( ) {

      final Mesh2 m = new Mesh2(this);
      m.name = this.name;
      m.materialIndex = this.materialIndex;
      return m;
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
      if ( !super.equals(obj) ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Mesh2 ) obj);
   }

   /**
    * Extrudes an edge, creating a new quadrilateral tangent to the edge.
    *
    * @param faceIdx the face index.
    * @param edgeIdx the edge index
    * @param amt     the extrusion amount
    *
    * @return the new face indices
    */
   @Experimental
   public int[][][] extrudeEdge (
      final int faceIdx,
      final int edgeIdx,
      final float amt ) {

      // TEST UV coordinates

      if ( amt == 0.0f ) { return new int[0][0][0]; }

      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIdx, facesLen);
      final int[][] face = this.faces[i];
      final int vertsLen = face.length;

      final int j = Utils.mod(edgeIdx, vertsLen);
      final int k = Utils.mod(edgeIdx + 1, vertsLen);
      final int[] idxOrigin = face[j];
      final int[] idxDest = face[k];

      final int idxV0 = idxOrigin[0];
      final int idxV3 = idxDest[0];
      final int idxVt0 = idxOrigin[1];
      final int idxVt3 = idxDest[1];

      final int vsOldLen = this.coords.length;
      final int vtsOldLen = this.texCoords.length;

      final int idxV1 = vsOldLen;
      final int idxV2 = vsOldLen + 1;
      final int idxVt1 = vtsOldLen;
      final int idxVt2 = vtsOldLen + 1;

      final Vec2 vOrigin = this.coords[idxV0];
      final Vec2 vDest = this.coords[idxV3];

      /*
       * The perpendicular is not normalized so that the created face is square,
       * no matter the number of sides on the polygon.
       */
      final Vec2 vPerp = new Vec2();
      Vec2.sub(vOrigin, vDest, vPerp);
      Vec2.perpendicularCCW(vPerp, vPerp);
      Vec2.mul(vPerp, amt, vPerp);

      final Vec2 vNewOrigin = new Vec2();
      final Vec2 vNewDest = new Vec2();
      Vec2.add(vOrigin, vPerp, vNewOrigin);
      Vec2.add(vDest, vPerp, vNewDest);

      final Vec2 vtOrigin = this.texCoords[idxVt0];
      final Vec2 vtDest = this.texCoords[idxVt3];

      final Vec2 vtPerp = new Vec2();
      Vec2.sub(vtOrigin, vtDest, vtPerp);
      Vec2.perpendicularCCW(vtPerp, vtPerp);
      // Vec2.normalize(vtPerp, vtPerp);

      final Vec2 vtNewOrigin = new Vec2();
      final Vec2 vtNewDest = new Vec2();
      Vec2.add(vtOrigin, vtPerp, vtNewOrigin);
      Vec2.add(vtDest, vtPerp, vtNewDest);

      final int[][][] faceNew = { {
         { idxV0, idxVt0 },
         { idxV1, idxVt1 },
         { idxV2, idxVt2 },
         { idxV3, idxVt3 } } };

      this.coords = Vec2.concat(this.coords,
         new Vec2[] { vNewOrigin, vNewDest });
      this.texCoords = Vec2.concat(this.texCoords,
         new Vec2[] { vtNewOrigin, vtNewDest });
      this.faces = Mesh.splice(this.faces, i + 1, 0, faceNew);

      return faceNew;
   }

   /**
    * Gets an edge from the mesh.
    *
    * @param i      the face index
    * @param j      the vertex index
    * @param target the output edge
    *
    * @return the edge
    */
   public Edge2 getEdge (
      final int i,
      final int j,
      final Edge2 target ) {

      final int[][] f0 = this.faces[Utils.mod(
         i, this.faces.length)];
      final int f0len = f0.length;
      final int[] f1 = f0[Utils.mod(j, f0len)];
      final int[] f2 = f0[Utils.mod(j + 1, f0len)];

      return target.set(
         this.coords[f1[0]],
         this.texCoords[f1[1]],
         this.coords[f2[0]],
         this.texCoords[f2[1]]);
   }

   /**
    * Gets an array of edges from the mesh.
    *
    * @return the edges array
    */
   public Edge2[] getEdges ( ) {

      Edge2 trial = new Edge2();
      final int len0 = this.faces.length;
      final ArrayList < Edge2 > result = new ArrayList <>(len0 * 4);

      for ( int i = 0; i < len0; ++i ) {

         final int[][] fs = this.faces[i];
         final int len1 = fs.length;

         for ( int j = 0; j < len1; ++j ) {

            final int[] fOrigin = fs[j];
            final int[] fDest = fs[ ( j + 1 ) % len1];

            trial.set(
               this.coords[fOrigin[0]],
               this.texCoords[fOrigin[1]],
               this.coords[fDest[0]],
               this.texCoords[fDest[1]]);

            if ( !result.contains(trial) ) {
               result.add(trial);
               trial = new Edge2();
            }
         }
      }

      return result.toArray(new Edge2[result.size()]);
   }

   /**
    * Gets a face from the mesh.
    *
    * @param i      the index
    * @param target the output face
    *
    * @return the face
    */
   public Face2 getFace (
      final int i,
      final Face2 target ) {

      final int[][] face = this.faces[Utils.mod(
         i, this.faces.length)];
      final int len = face.length;
      final Vert2[] vertices = new Vert2[len];

      for ( int j = 0; j < len; ++j ) {
         final int[] vert = face[j];
         vertices[j] = new Vert2(
            this.coords[vert[0]],
            this.texCoords[vert[1]]);
      }

      return target.set(vertices);
   }

   /**
    * Gets an array of faces from the mesh.
    *
    * @return the faces array
    */
   public Face2[] getFaces ( ) {

      final int len0 = this.faces.length;
      final Face2[] result = new Face2[len0];

      for ( int i = 0; i < len0; ++i ) {

         final int[][] fs0 = this.faces[i];
         final int len1 = fs0.length;
         final Vert2[] verts = new Vert2[len1];

         for ( int j = 0; j < len1; ++j ) {

            final int[] fs1 = fs0[j];
            verts[j] = new Vert2(
               this.coords[fs1[0]],
               this.texCoords[fs1[1]]);
         }

         result[i] = new Face2(verts);
      }

      return result;
   }

   /**
    * Gets a vertex from the mesh.
    *
    * @param i      primary index
    * @param j      secondary index
    * @param target the output vertex
    *
    * @return the vertex
    */
   public Vert2 getVertex (
      final int i,
      final int j,
      final Vert2 target ) {

      final int[][] f0 = this.faces[Utils.mod(
         i, this.faces.length)];
      final int[] f = f0[Utils.mod(j, f0.length)];

      return target.set(
         this.coords[f[0]],
         this.texCoords[f[1]]);
   }

   /**
    * Gets an array of vertices from the mesh.
    *
    * @return the vertices
    */
   public Vert2[] getVertices ( ) {

      Vert2 trial = new Vert2();
      final int len0 = this.faces.length;
      final ArrayList < Vert2 > result = new ArrayList <>(len0);

      for ( int i = 0; i < len0; ++i ) {

         final int[][] fs = this.faces[i];
         final int len1 = fs.length;

         for ( int j = 0; j < len1; ++j ) {

            final int[] f = fs[j];
            trial.set(
               this.coords[f[0]],
               this.texCoords[f[1]]);

            if ( !result.contains(trial) ) {
               result.add(trial);
               trial = new Vert2();
            }
         }
      }

      return result.toArray(new Vert2[result.size()]);
   }

   /**
    * Returns a hash code for this mesh based on its coordinates and its face
    * indices.
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
    * Insets a face by calculating its center then easing from the face's
    * vertices toward the center by 0.5.
    *
    * @param faceIdx the face index
    *
    * @return the new face indices
    */
   public int[][][] insetFace ( final int faceIdx ) {

      return this.insetFace(faceIdx, 0.5f);
   }

   /**
    * Insets a face by calculating its center then easing from the face's
    * vertices toward the center by the amount, expected to be in the range
    * [0.0, 1.0] . When the amount is less than 0.0, the face remains unchanged;
    * when the amount is greater than 1.0, then the face is subdivided by
    * center.
    *
    * @param faceIdx the face index
    * @param fac     the inset amount
    *
    * @return the new face indices
    */
   @Experimental
   public int[][][] insetFace (
      final int faceIdx,
      final float fac ) {

      if ( fac <= 0.0f ) { return new int[0][0][0]; }
      if ( fac >= 1.0f ) { return this.subdivFaceFan(faceIdx); }

      /* Validate face index, find face. */
      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIdx, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      final int vsOldLen = this.coords.length;
      final int vtsOldLen = this.texCoords.length;
      final int[][][] fsNew = new int[faceLen + 1][][];
      final int[][] centerFace = fsNew[faceLen] = new int[faceLen][2];

      final Vec2 vCenter = new Vec2();
      final Vec2 vtCenter = new Vec2();

      /* Find center. */
      for ( int j = 0; j < faceLen; ++j ) {
         final int[] vertCurr = face[j];
         final Vec2 vCurr = this.coords[vertCurr[0]];
         final Vec2 vtCurr = this.texCoords[vertCurr[1]];
         Vec2.add(vCenter, vCurr, vCenter);
         Vec2.add(vtCenter, vtCurr, vtCenter);
      }
      Vec2.div(vCenter, faceLen, vCenter);
      Vec2.div(vtCenter, faceLen, vtCenter);

      final Vec2[] vsNew = new Vec2[faceLen];
      final Vec2[] vtsNew = new Vec2[faceLen];

      /* Find new corners. */
      final float u = 1.0f - fac;
      for ( int j = 0; j < faceLen; ++j ) {
         final int k = ( j + 1 ) % faceLen;

         final int[] vertCurr = face[j];
         final int[] vertNext = face[k];

         final int vCornerIdx = vertCurr[0];
         final int vtCornerIdx = vertCurr[1];
         final Vec2 vCurr = this.coords[vCornerIdx];
         final Vec2 vtCurr = this.texCoords[vtCornerIdx];

         vsNew[j] = new Vec2(
            u * vCurr.x + fac * vCenter.x,
            u * vCurr.y + fac * vCenter.y);

         vtsNew[j] = new Vec2(
            u * vtCurr.x + fac * vtCenter.x,
            u * vtCurr.y + fac * vtCenter.y);

         final int vSubdivIdx = vsOldLen + j;
         final int vtSubdivIdx = vtsOldLen + j;

         fsNew[j] = new int[][] {
            { vCornerIdx, vtCornerIdx },
            { vertNext[0], vertNext[1] },
            { vsOldLen + k, vtsOldLen + k },
            { vSubdivIdx, vtSubdivIdx } };

         centerFace[j][0] = vSubdivIdx;
         centerFace[j][1] = vtSubdivIdx;

      }

      this.coords = Vec2.concat(this.coords, vsNew);
      this.texCoords = Vec2.concat(this.texCoords, vtsNew);
      this.faces = Mesh.splice(this.faces, i, 1, fsNew);

      return fsNew;
   }

   /**
    * Insets all faces in the mesh once.
    *
    * @return this mesh
    */
   @Chainable
   public Mesh2 insetFaces ( ) { return this.insetFaces(1); }

   /**
    * Insets all faces in the mesh for a given number of iterations by a factor
    * of 0.5 .
    *
    * @param itr the iterations
    *
    * @return this mesh
    */
   @Chainable
   public Mesh2 insetFaces ( final int itr ) {

      return this.insetFaces(itr, 0.5f);
   }

   /**
    * Insets all faces in the mesh for a given number of iterations by a factor
    * in the range [0.0, 1.0] .
    *
    * @param itr the iterations
    * @param fac the inset factor
    *
    * @return this mesh
    */
   @Chainable
   @Experimental
   public Mesh2 insetFaces ( final int itr, final float fac ) {

      final int vitr = itr < 1 ? 1 : itr;
      for ( int i = 0; i < vitr; ++i ) {
         final int len = this.faces.length;
         for ( int j = 0, k = 0; j < len; ++j ) {
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
   public Face2Iterator iterator ( ) { return new Face2Iterator(this); }

   /**
    * Gets the number of faces held by this mesh.
    *
    * @return the length
    */
   public int length ( ) { return this.faces.length; }

   /**
    * Centers the mesh about the origin, (0.0, 0.0) and rescales it to the range
    * [-0.5, 0.5]. Emits a transform which records the mesh's center point and
    * original dimension. The transform's rotation is reset.
    *
    * @param tr the output transform
    *
    * @return this mesh
    *
    * @see Mesh2#calcDimensions(Mesh2, Vec2, Vec2, Vec2)
    */
   @Chainable
   public Mesh2 reframe ( final Transform2 tr ) {

      tr.locPrev.set(tr.location);
      tr.scalePrev.set(tr.scale);

      final Vec2 dim = tr.scale;
      final Vec2 lb = tr.location;
      final Vec2 ub = new Vec2();
      Mesh2.calcDimensions(this, dim, lb, ub);

      lb.x = 0.5f * ( lb.x + ub.x );
      lb.y = 0.5f * ( lb.y + ub.y );
      final float scl = Utils.div(1.0f, Utils.max(dim.x, dim.y));

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.coords[i];
         Vec2.sub(c, lb, c);
         Vec2.mul(c, scl, c);
      }

      tr.rotateTo(0.0f);

      return this;
   }

   /**
    * Flips the indices which specify an edge.
    *
    * @param i face index
    * @param j edge index
    *
    * @return this mesh
    */
   @Chainable
   public Mesh2 reverseEdge (
      final int i,
      final int j ) {

      final int[][] face = this.faces[Utils.mod(i, this.faces.length)];
      final int len = face.length;
      final int jOrigin = Utils.mod(j, len);
      final int jDest = Utils.mod(j + 1, len);

      final int[] temp = face[jOrigin];
      face[jOrigin] = face[jDest];
      face[jDest] = temp;

      return this;
   }

   /**
    * Flips the indices which specify a face. Changes the winding of a face from
    * counter-clockwise (CCW) to clockwise (CW) or vice versa.
    *
    * @param i face index
    *
    * @return this mesh
    */
   @Chainable
   public Mesh2 reverseFace ( final int i ) {

      final int[][] face = this.faces[Utils.mod(i, this.faces.length)];
      final int len = face.length;
      final int halfLen = len >> 1;
      for ( int j = 0; j < halfLen; ++j ) {
         final int reverse = len - j - 1;
         final int[] temp = face[j];
         face[j] = face[reverse];
         face[reverse] = temp;
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
    * @see Vec2#rotateZ(Vec2, float, Vec2)
    */
   @Chainable
   public Mesh2 rotateZ ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.coords[i];
         Vec2.rotateZ(c, cosa, sina, c);
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
    * @see Vec2#mul(Vec2, float, Vec2)
    */
   @Chainable
   public Mesh2 scale ( final float scale ) {

      if ( scale == 0.0f ) { return this; }

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.coords[i];
         Vec2.mul(c, scale, c);
      }

      return this;
   }

   /**
    * Scales all coordinates in the mesh by a non-uniform scalar.
    *
    * @param scale the vector
    *
    * @return this mesh
    *
    * @see Vec2#none(Vec2)
    * @see Vec2#mul(Vec2, Vec2, Vec2)
    */
   @Chainable
   public Mesh2 scale ( final Vec2 scale ) {

      if ( Vec2.none(scale) ) { return this; }

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.coords[i];
         Vec2.mul(c, scale, c);
      }

      return this;
   }

   /**
    * Sets the mesh's data by reference, not by value.
    *
    * @param faces     the faces array
    * @param coords    the coordinates array
    * @param texCoords the texture coordinates array
    *
    * @return this mesh
    */
   @Chainable
   public Mesh2 set (
      final int[][][] faces,
      final Vec2[] coords,
      final Vec2[] texCoords ) {

      this.faces = faces;
      this.coords = coords;
      this.texCoords = texCoords;
      return this;
   }

   /**
    * Sets this mesh to a copy of the source. Allocates new arrays for
    * coordinates, texture coordinates and faces.
    *
    * @param source the source mesh
    *
    * @return this mesh
    */
   @Chainable
   public Mesh2 set ( final Mesh2 source ) {

      /* Copy coordinates. */
      final Vec2[] sourcevs = source.coords;
      final int vslen = sourcevs.length;
      this.coords = Vec2.resize(this.coords, vslen);
      for ( int i = 0; i < vslen; ++i ) {
         this.coords[i].set(sourcevs[i]);
      }

      /* Copy texture coordinates. */
      final Vec2[] sourcevts = source.texCoords;
      final int vtslen = sourcevts.length;
      this.texCoords = Vec2.resize(this.texCoords, vtslen);
      for ( int j = 0; j < vtslen; ++j ) {
         this.texCoords[j].set(sourcevts[j]);
      }

      /* Copy faces. */
      final int[][][] sourcefs = source.faces;
      final int fslen0 = sourcefs.length;
      this.faces = new int[fslen0][][];

      for ( int i = 0; i < fslen0; ++i ) {

         final int[][] source1 = sourcefs[i];
         final int fslen1 = source1.length;
         final int[][] target1 = new int[fslen1][];
         this.faces[i] = target1;

         for ( int j = 0; j < fslen1; ++j ) {

            final int[] source2 = source1[j];
            final int fslen2 = source2.length;
            final int[] target2 = new int[fslen2];
            target1[j] = target2;

            for ( int k = 0; k < fslen2; ++k ) {
               target2[k] = source2[k];
            }
         }
      }

      this.materialIndex = source.materialIndex;
      this.name = source.name;
      return this;
   }

   /**
    * Sorts the coordinates and texture coordinates of a mesh, then reassigns
    * indices in the face.
    *
    * @return this mesh
    */
   public Mesh2 sort ( ) { return this.sort(IUtils.DEFAULT_EPSILON); }

   /**
    * Sorts the coordinates and texture coordinates of a mesh, then reassigns
    * indices in the face.
    *
    * @param tolerance the quantization tolerance
    *
    * @return this mesh
    *
    * @see System#arraycopy(Object, int, Object, int, int)
    * @see Arrays#binarySearch(Object[], Object, Comparator)
    */
   @Experimental
   @Chainable
   public Mesh2 sort ( final float tolerance ) {

      /*
       * Sort coordinates: copy old indices, load into sorted set to both remove
       * duplicates and to sort, then unload into a new array.
       */
      final int vlen = this.coords.length;
      final Vec2[] vold = new Vec2[vlen];
      System.arraycopy(this.coords, 0, vold, 0, vlen);
      final SortedSet < Vec2 > vsUnique = new TreeSet <>(Mesh.SORT_2);
      for ( int i = 0; i < vlen; ++i ) {
         vsUnique.add(this.coords[i]);
      }
      this.coords = vsUnique.toArray(new Vec2[vsUnique.size()]);

      /* Sort texture coordinates. */
      final int vtlen = this.texCoords.length;
      final Vec2[] vtold = new Vec2[vtlen];
      System.arraycopy(this.texCoords, 0, vtold, 0, vtlen);
      final SortedSet < Vec2 > vtsUnique = new TreeSet <>(Mesh.SORT_2);
      for ( int i = 0; i < vtlen; ++i ) {
         vtsUnique.add(this.texCoords[i]);
      }
      this.texCoords = vtsUnique.toArray(new Vec2[vtsUnique.size()]);

      /* Update face indices. */
      final int facesLen = this.faces.length;
      for ( int i = 0; i < facesLen; ++i ) {
         final int[][] face = this.faces[i];
         final int vertsLen = face.length;

         for ( int j = 0; j < vertsLen; ++j ) {
            final int[] vert = face[j];

            /* Update coordinate index. */
            final int vidx = Arrays.binarySearch(
               this.coords, vold[vert[0]], Mesh.SORT_2);
            vert[0] = vidx < 0 ? vert[0] : vidx;

            /* Update texture coordinate index. */
            final int vtidx = Arrays.binarySearch(
               this.texCoords, vtold[vert[1]], Mesh.SORT_2);
            vert[1] = vtidx < 0 ? vert[1] : vtidx;
         }
      }

      /* Sort faces by center. */
      Arrays.sort(this.faces, new Mesh2.SortIndices2(this.coords));

      return this;
   }

   /**
    * Subdivides a convex face. Defaults to center-based subdivision.
    *
    * @param faceIdx the face index
    *
    * @return the new face indices
    */
   public int[][][] subdivFace ( final int faceIdx ) {

      return this.subdivFaceCenter(faceIdx);
   }

   /**
    * Subdivides a convex face by calculating its center, cutting each of its
    * edges once to create a midpoint, then connecting the midpoints to the
    * center. This generates a quadrilateral for the number of edges in the
    * face.
    *
    * @param faceIdx the face index
    *
    * @return the new face indices
    */
   @Experimental
   public int[][][] subdivFaceCenter ( final int faceIdx ) {

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

      /* Create arrays to hold new data. */
      final Vec2[] vsNew = new Vec2[faceLen + 1];
      final Vec2[] vtsNew = new Vec2[faceLen + 1];
      final int[][][] fsNew = new int[faceLen][4][2];

      /* Center is last element of new array. */
      final Vec2 vCenter = vsNew[faceLen] = new Vec2();
      final Vec2 vtCenter = vtsNew[faceLen] = new Vec2();

      final int vCenterIdx = vsOldLen + faceLen;
      final int vtCenterIdx = vtsOldLen + faceLen;

      for ( int j = 0; j < faceLen; ++j ) {
         final int k = ( j + 1 ) % faceLen;

         final int[] vertCurr = face[j];
         final int[] vertNext = face[k];

         final Vec2 vCurr = this.coords[vertCurr[0]];
         final Vec2 vtCurr = this.texCoords[vertCurr[1]];

         /* Sum vertex for center. */
         Vec2.add(vCenter, vCurr, vCenter);
         Vec2.add(vtCenter, vtCurr, vtCenter);

         final int vNextIdx = vertNext[0];
         final Vec2 vNext = this.coords[vNextIdx];
         vsNew[j] = new Vec2(
            ( vCurr.x + vNext.x ) * 0.5f,
            ( vCurr.y + vNext.y ) * 0.5f);

         final int vtNextIdx = vertNext[1];
         final Vec2 vtNext = this.texCoords[vtNextIdx];
         vtsNew[j] = new Vec2(
            ( vtCurr.x + vtNext.x ) * 0.5f,
            ( vtCurr.y + vtNext.y ) * 0.5f);

         fsNew[j] = new int[][] {
            { vCenterIdx, vtCenterIdx },
            { vsOldLen + j, vtsOldLen + j },
            { vNextIdx, vtNextIdx },
            { vsOldLen + k, vtsOldLen + k } };
      }
      Vec2.div(vCenter, faceLen, vCenter);
      Vec2.div(vtCenter, faceLen, vtCenter);

      this.coords = Vec2.concat(this.coords, vsNew);
      this.texCoords = Vec2.concat(this.texCoords, vtsNew);
      this.faces = Mesh.splice(this.faces, i, 1, fsNew);

      return fsNew;
   }

   /**
    * Subdivides a convex face by calculating its center, then connecting its
    * vertices to the center. This generates a triangle for the number of edges
    * in the face.
    *
    * @param faceIdx the face index
    *
    * @return the new face indices
    */
   @Experimental
   public int[][][] subdivFaceFan ( final int faceIdx ) {

      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIdx, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      final int[][][] fsNew = new int[faceLen][3][2];
      final Vec2 vCenter = new Vec2();
      final Vec2 vtCenter = new Vec2();

      final int vCenterIdx = this.coords.length;
      final int vtCenterIdx = this.texCoords.length;

      for ( int j = 0; j < faceLen; ++j ) {
         final int k = ( j + 1 ) % faceLen;

         final int[] vertCurr = face[j];
         final int[] vertNext = face[k];

         final int vCurrIdx = vertCurr[0];
         final int vtCurrIdx = vertCurr[1];
         final Vec2 vCurr = this.coords[vCurrIdx];
         final Vec2 vtCurr = this.texCoords[vtCurrIdx];

         /* Sum vertex for center. */
         Vec2.add(vCenter, vCurr, vCenter);
         Vec2.add(vtCenter, vtCurr, vtCenter);

         fsNew[j] = new int[][] {
            { vCenterIdx, vtCenterIdx },
            { vCurrIdx, vtCurrIdx },
            { vertNext[0], vertNext[1] } };
      }

      Vec2.div(vCenter, faceLen, vCenter);
      Vec2.div(vtCenter, faceLen, vtCenter);

      this.coords = Vec2.concat(this.coords, new Vec2[] { vCenter });
      this.texCoords = Vec2.concat(this.texCoords, new Vec2[] { vtCenter });
      this.faces = Mesh.splice(this.faces, i, 1, fsNew);

      return fsNew;
   }

   /**
    * Subdivides a convex face by cutting each of its edges once to create a
    * midpoint, then connecting them. This generates peripheral triangles and a
    * new central face with the same number of edges as the original. This is
    * best suited to meshes made of triangles.
    *
    * @param faceIdx the face index
    *
    * @return the new face indices
    */
   @Experimental
   public int[][][] subdivFaceInscribe ( final int faceIdx ) {

      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIdx, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      final int vsOldLen = this.coords.length;
      final int vtsOldLen = this.texCoords.length;

      final Vec2[] vsNew = new Vec2[faceLen];
      final Vec2[] vtsNew = new Vec2[faceLen];
      final int[][][] fsNew = new int[faceLen + 1][][];
      final int[][] centerFace = fsNew[faceLen] = new int[faceLen][2];

      for ( int j = 0; j < faceLen; ++j ) {
         final int[] vertCurr = face[j];
         final Vec2 vCurr = this.coords[vertCurr[0]];
         final Vec2 vtCurr = this.texCoords[vertCurr[1]];

         final int k = ( j + 1 ) % faceLen;
         final int[] vertNext = face[k];

         final int vNextIdx = vertNext[0];
         final Vec2 vNext = this.coords[vNextIdx];
         vsNew[j] = new Vec2(
            ( vCurr.x + vNext.x ) * 0.5f,
            ( vCurr.y + vNext.y ) * 0.5f);

         final int vtNextIdx = vertNext[1];
         final Vec2 vtNext = this.texCoords[vtNextIdx];
         vtsNew[j] = new Vec2(
            ( vtCurr.x + vtNext.x ) * 0.5f,
            ( vtCurr.y + vtNext.y ) * 0.5f);

         final int vSubdivIdx = vsOldLen + j;
         final int vtSubdivIdx = vtsOldLen + j;
         fsNew[j] = new int[][] {
            { vSubdivIdx, vtSubdivIdx },
            { vNextIdx, vtNextIdx },
            { vsOldLen + k, vtsOldLen + k } };

         centerFace[j][0] = vSubdivIdx;
         centerFace[j][1] = vtSubdivIdx;
      }

      this.coords = Vec2.concat(this.coords, vsNew);
      this.texCoords = Vec2.concat(this.texCoords, vtsNew);
      this.faces = Mesh.splice(this.faces, i, 1, fsNew);

      return fsNew;
   }

   /**
    * Subdivides all faces in the mesh once.
    *
    * @return this mesh
    */
   @Chainable
   public Mesh2 subdivFaces ( ) { return this.subdivFaces(1); }

   /**
    * Subdivides all faces in the mesh by a number of iterations.
    *
    * @param itr iterations
    *
    * @return this mesh
    */
   @Chainable
   public Mesh2 subdivFaces ( final int itr ) {

      return this.subdivFacesCenter(itr);
   }

   /**
    * Subdivides all faces in the mesh by a number of iterations. Uses the
    * center method.
    *
    * @param itr iterations
    *
    * @return this mesh
    */
   @Chainable
   public Mesh2 subdivFacesCenter ( final int itr ) {

      final int vitr = itr < 1 ? 1 : itr;
      for ( int i = 0; i < vitr; ++i ) {
         final int len = this.faces.length;
         for ( int j = 0, k = 0; j < len; ++j ) {
            final int vertLen = this.faces[k].length;
            this.subdivFaceCenter(k);
            k += vertLen;
         }
      }
      return this;
   }

   /**
    * Subdivides all faces in the mesh by a number of iterations. Uses the
    * triangle-fan method.
    *
    * @param itr iterations
    *
    * @return this mesh
    */
   @Chainable
   public Mesh2 subdivFacesFan ( final int itr ) {

      final int vitr = itr < 1 ? 1 : itr;
      for ( int i = 0; i < vitr; ++i ) {
         final int len = this.faces.length;
         for ( int j = 0, k = 0; j < len; ++j ) {
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
    */
   @Chainable
   public Mesh2 subdivFacesInscribe ( final int itr ) {

      final int vitr = itr < 1 ? 1 : itr;
      for ( int i = 0; i < vitr; ++i ) {
         final int len = this.faces.length;
         for ( int j = 0, k = 0; j < len; ++j ) {
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
   @Experimental
   public String toObjString ( ) {

      final int coordsLen = this.coords.length;
      final int texCoordsLen = this.texCoords.length;
      final int facesLen = this.faces.length;
      final StringBuilder objs = new StringBuilder(2048);

      /*
       * Append a comment listing the number of coordinates, texture coordinates
       * and faces.
       */
      objs.append("# v: ")
         .append(coordsLen)
         .append(", vt: ")
         .append(texCoordsLen)
         .append(", vn: 1, f: ")
         .append(facesLen)
         .append('\n')
         .append('\n');

      /* Append name. */
      objs.append('o')
         .append(' ')
         .append(this.name)
         .append('\n')
         .append('\n');

      /* Append coordinates. */
      for ( int i = 0; i < coordsLen; ++i ) {
         objs.append('v')
            .append(' ')
            .append(this.coords[i].toObjString())
            .append(" 0.0 \n");
      }
      objs.append('\n');

      /* Append a texture coordinates. */
      for ( int i = 0; i < texCoordsLen; ++i ) {
         objs.append("vt ")
            .append(this.texCoords[i].toObjString())
            .append('\n');
      }

      /* Append a single normal. */
      objs.append("\nvn 0.0 0.0 1.0\n\n");

      /* Append face indices. */
      for ( int i = 0; i < facesLen; ++i ) {

         final int[][] face = this.faces[i];
         final int vLen = face.length;
         objs.append('f')
            .append(' ');

         for ( int j = 0; j < vLen; ++j ) {

            /* Indices in an .obj file start at 1, not 0. */
            final int[] vert = face[j];
            objs.append(vert[0] + 1)
               .append('/')
               .append(vert[1] + 1)
               .append('/')
               .append('1')
               .append(' ');
         }

         objs.append('\n');
      }

      return objs.toString();
   }

   /**
    * Centers the mesh about the origin, (0.0, 0.0), by calculating its
    * dimensions then subtracting the center point. Emits a transform which
    * records the mesh's center point. The transform's rotation and scale are
    * reset.
    *
    * @param tr the output transform
    *
    * @return this mesh
    *
    * @see Mesh2#calcDimensions(Mesh2, Vec2, Vec2, Vec2)
    * @see Mesh2#translate(Vec2)
    */
   @Chainable
   public Mesh2 toOrigin ( final Transform2 tr ) {

      final Vec2 lb = new Vec2();
      final Vec2 ub = new Vec2();
      Mesh2.calcDimensions(this, new Vec2(), lb, ub);

      lb.x = -0.5f * ( lb.x + ub.x );
      lb.y = -0.5f * ( lb.y + ub.y );
      this.translate(lb);

      tr.locPrev.set(tr.location);
      Vec2.negate(lb, tr.location);

      tr.scaleTo(1.0f);
      tr.rotateTo(0.0f);

      return this;
   }

   /**
    * Returns a string representation of the mesh.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of the mesh.
    *
    * @param places the number of places
    *
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
    *
    * @return the string
    */
   public String toString (
      final int places,
      final int truncate ) {

      final StringBuilder sb = new StringBuilder(2048);

      sb.append("{ name: \"")
         .append(this.name)
         .append('\"')
         .append(',')
         .append(' ')
         // .append('\n')
         .append("coords: [ ");

      if ( this.coords != null ) {
         // sb.append('\n');
         final int len = this.coords.length <= truncate ? this.coords.length
            : truncate;
         final int last = len - 1;
         for ( int i = 0; i < len; ++i ) {
            sb.append(this.coords[i].toString(places));
            if ( i < last ) {
               sb.append(',').append(' ');
               // sb.append('\n');
            }
         }

         if ( this.coords.length > truncate ) {
            // sb.append('\n');
            sb.append(" /* ... */");
         }
      }

      sb.append(" ], ");
      // sb.append('\n');
      sb.append("texCoords: [ ");
      if ( this.texCoords != null ) {
         // sb.append('\n');
         final int len = this.texCoords.length <= truncate
            ? this.texCoords.length
            : truncate;
         final int last = len - 1;
         for ( int i = 0; i < len; ++i ) {
            sb.append(this.texCoords[i].toString(places));
            if ( i < last ) {
               sb.append(',').append(' ');
               // sb.append('\n');
            }
         }

         if ( this.texCoords.length > truncate ) {
            // sb.append('\n');
            sb.append(" /* ... */");
         }
      }

      sb.append(" ], ");
      // sb.append('\n');
      sb.append("faces: [ ");
      if ( this.faces != null ) {
         // sb.append('\n');
         final int facesLen = this.faces.length <= truncate
            ? this.faces.length
            : truncate;
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

               for ( int k = 0; k < infoLen; ++k ) {

                  sb.append(vert[k]);
                  if ( k < infoLast ) { sb.append(',').append(' '); }
               }
               sb.append(' ').append(']');
               if ( j < vertsLast ) { sb.append(',').append(' '); }
            }
            sb.append(' ').append(']');
            if ( i < facesLast ) {
               sb.append(',').append(' ');
               // sb.append('\n');
            }
         }

         if ( this.faces.length > truncate ) {
            // sb.append('\n');
            sb.append(" /* ... */");
         }
      }

      sb.append(" ] }");
      return sb.toString();
   }

   /**
    * Renders the curve as a string containing an SVG element.
    *
    * @param id   the element id
    * @param zoom scaling transform
    *
    * @return the SVG string
    */
   @Override
   public String toSvgElm (
      final String id,
      final float zoom ) {

      final StringBuilder svgp = new StringBuilder(1024)
         .append(MaterialSolid.defaultSvgMaterial(zoom))
         .append(this.toSvgPath(id))
         .append("</g>\n");
      return svgp.toString();
   }

   /**
    * Renders the mesh path as a string containing an SVG element.
    *
    * @param id the path id
    *
    * @return the SVG string
    */
   public String toSvgPath ( final String id ) {

      final StringBuilder svgp = new StringBuilder(1024);

      final int[][][] fs = this.faces;
      final Vec2[] vs = this.coords;
      final int fsLen = fs.length;
      final String iddot = id + ".";
      for ( int i = 0; i < fsLen; ++i ) {
         final int[][] f = fs[i];
         final int fLen = f.length;

         svgp.append("<path id=\"")
            .append(iddot + Utils.toPadded(i, 4))
            .append("\" d=\"M ")
            .append(vs[f[0][0]].toSvgString())
            .append(' ');

         for ( int j = 1; j < fLen; ++j ) {
            svgp.append('L')
               .append(' ')
               .append(vs[f[j][0]].toSvgString())
               .append(' ');
         }

         svgp.append("Z\"></path>\n");
      }

      return svgp.toString();
   }

   /**
    * Translates all coordinates in the mesh by a vector.
    *
    * @param v the vector
    *
    * @return this mesh
    *
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   @Chainable
   public Mesh2 translate ( final Vec2 v ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.coords[i];
         Vec2.add(c, v, c);
      }

      return this;
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API. This
    * code is brittle and is used for internal testing purposes.
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode ( ) { return this.toBlenderCode(false); }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API. This
    * code is brittle and is used for internal testing purposes, i.e., to
    * compare how mesh geometry looks in Blender (the control) versus in the
    * library (the test).
    *
    * @param includeUvs whether or not to include UVs
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode ( final boolean includeUvs ) {

      final StringBuilder pyCd = new StringBuilder(1024);
      pyCd.append("{\"name\": \"")
         .append(this.name)
         .append("\", \"material_index\": ")
         .append(this.materialIndex)
         .append(", \"vertices\": [");

      final int vlen = this.coords.length;
      final int vlast = vlen - 1;
      for ( int i = 0; i < vlen; ++i ) {
         pyCd.append(this.coords[i].toBlenderCode(0.0f));
         if ( i < vlast ) { pyCd.append(',').append(' '); }
      }

      pyCd.append("], \"faces\": [");

      final int fsLen = this.faces.length;
      final int flast = fsLen - 1;
      for ( int j = 0; j < fsLen; ++j ) {
         final int[][] vrtInd = this.faces[j];
         final int vrtIndLen = vrtInd.length;
         final int vrtLast = vrtIndLen - 1;

         pyCd.append('(');
         for ( int k = 0; k < vrtIndLen; ++k ) {
            pyCd.append(vrtInd[k][0]);
            if ( k < vrtLast ) { pyCd.append(',').append(' '); }
         }
         pyCd.append(')');

         if ( j < flast ) { pyCd.append(',').append(' '); }
      }

      if ( includeUvs ) {
         pyCd.append("], \"uvs\": [");
         final int vtlen = this.texCoords.length;
         final int vtlast = vtlen - 1;
         for ( int h = 0; h < vtlen; ++h ) {
            pyCd.append(this.texCoords[h].toBlenderCode(true));
            if ( h < vtlast ) { pyCd.append(',').append(' '); }
         }

         pyCd.append("], \"uv_indices\": [");
         for ( int j = 0; j < fsLen; ++j ) {
            final int[][] vrtInd = this.faces[j];
            final int vrtIndLen = vrtInd.length;
            final int vrtLast = vrtIndLen - 1;

            pyCd.append('(');
            for ( int k = 0; k < vrtIndLen; ++k ) {
               pyCd.append(vrtInd[k][1]);
               if ( k < vrtLast ) { pyCd.append(',').append(' '); }
            }
            pyCd.append(')');

            if ( j < flast ) { pyCd.append(',').append(' '); }
         }
      }

      pyCd.append(']').append('}');
      return pyCd.toString();
   }

   /**
    * Tests this mesh for equivalence with another.
    *
    * @param mesh2 the mesh
    *
    * @return the evaluation
    */
   protected boolean equals ( final Mesh2 mesh2 ) {

      if ( !Arrays.equals(this.coords, mesh2.coords) ) { return false; }
      if ( !Arrays.deepEquals(this.faces, mesh2.faces) ) { return false; }

      return true;
   }

   /**
    * Subdivides an edge by the number of cuts given. For example, one cut will
    * divide an edge in half; two cuts, into thirds.<br>
    * <br>
    * Does not distinguish between interior edges, which have a complement
    * elsewhere, and border edges; for that reason this works best with
    * NGONs.<br>
    * <br>
    * This is protected because it tends to create faces harder to triangulate.
    *
    * @param faceIndex the face index
    * @param edgeIndex the edge index
    * @param cuts      number of cuts
    *
    * @return this mesh
    */
   @Experimental
   @Chainable
   protected Mesh2 subdivEdge (
      final int faceIndex,
      final int edgeIndex,
      final int cuts ) {

      // RESEARCH: Is there any way to search for interior half-edge which
      // travels in the opposite direction?
      // search for face[j1][n], face[j0][n]

      if ( cuts < 1 ) { return this; }

      /* Validate face index, find face. */
      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIndex, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      /* Find edge origin vertex. */
      final int j0 = Utils.mod(edgeIndex, faceLen);
      final int[] vert0Idx = face[j0];
      final Vec2 vOrigin = this.coords[vert0Idx[0]];
      final Vec2 vtOrigin = this.texCoords[vert0Idx[1]];

      /* Find edge destination vertex. */
      final int j1 = Utils.mod(edgeIndex + 1, faceLen);
      final int[] vert1Idx = face[j1];
      final Vec2 vDest = this.coords[vert1Idx[0]];
      final Vec2 vtDest = this.texCoords[vert1Idx[1]];

      /*
       * Cache old length of coordinates and texture coordinates so new ones can
       * be appended to the end.
       */
      final int vsOldLen = this.coords.length;
      final int vtsOldLen = this.texCoords.length;

      /* Create arrays to hold new data. */
      final Vec2[] vsNew = new Vec2[cuts];
      final Vec2[] vtsNew = new Vec2[cuts];
      final int[][] fsNew = new int[cuts][2];

      /*
       * Subdivide the edge. The edge origin and destination are to be excluded
       * from the new set, so the conversion to the step accounts for this.
       */
      final float toStep = 1.0f / ( cuts + 1.0f );
      for ( int k = 0; k < cuts; ++k ) {
         final float step = toStep + k * toStep;
         final float u = 1.0f - step;

         final Vec2 v = new Vec2();
         final Vec2 vt = new Vec2();

         v.set(
            u * vOrigin.x + step * vDest.x,
            u * vOrigin.y + step * vDest.y);

         vt.set(
            u * vtOrigin.x + step * vtDest.x,
            u * vtOrigin.y + step * vtDest.y);

         vsNew[k] = v;
         vtsNew[k] = vt;

         final int[] newf = fsNew[k];
         newf[0] = vsOldLen + k;
         newf[1] = vtsOldLen + k;
      }

      /*
       * Append new coordinates and texture coordinates to the end of their
       * respective arrays. The new faces need to be inserted to the object's
       * faces array, not reassigned to local face array.
       */
      this.coords = Vec2.concat(this.coords, vsNew);
      this.texCoords = Vec2.concat(this.texCoords, vtsNew);
      this.faces[i] = Mesh.insert(face, j1, fsNew);

      return this;
   }

   /**
    * Type of polygon to draw when it is not supplied to the polygon function.
    */
   public static final PolyType DEFAULT_POLY_TYPE = PolyType.NGON;

   /**
    * Creates an arc from a start and stop angle.
    *
    * @param startAngle the start angle
    * @param stopAngle  the stop angle
    * @param oculus     the size of the opening
    * @param sectors    number of sectors in a circle
    * @param target     the output mesh
    *
    * @return the arc
    */
   public static final Mesh2 arc (
      final float startAngle,
      final float stopAngle,
      final float oculus,
      final int sectors,
      final Mesh2 target ) {

      return Mesh2.arc(
         startAngle, stopAngle,
         oculus, sectors,
         Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates an arc from a start and stop angle. The granularity of the
    * approximation is dictated by the number of sectors in a complete circle.
    * The thickness of the arc is described by the annulus. Useful where sectors
    * may be faster than the Bezier curves of
    * {@link Curve2#arc(float, float, float, camzup.core.ArcMode, Curve2)} or
    * where there is an issue rendering strokes.
    *
    * @param startAngle the start angle
    * @param stopAngle  the stop angle
    * @param oculus     the size of the opening
    * @param sectors    number of sectors in a circle
    * @param poly       the polygon type
    * @param target     the output mesh
    *
    * @return the arc
    */
   public static final Mesh2 arc (
      final float startAngle,
      final float stopAngle,
      final float oculus,
      final int sectors,
      final PolyType poly,
      final Mesh2 target ) {

      target.name = "Arc";

      final double a1 = Utils.mod1(startAngle * IUtils.ONE_TAU_D);
      final double b1 = Utils.mod1(stopAngle * IUtils.ONE_TAU_D);
      final double arcLen1 = Utils.mod1(b1 - a1);
      if ( arcLen1 < 0.00139d ) {
         return Mesh2.ring(oculus, sectors, poly, target);
      }

      final int sctCount = Utils.ceilToInt(
         1 + ( sectors < 3 ? 3 : sectors ) * ( float ) arcLen1);
      final int sctCount2 = sctCount + sctCount;
      final Vec2[] vs = target.coords = Vec2.resize(target.coords, sctCount2);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
         sctCount2);

      final float annul = Utils.clamp(oculus,
         IUtils.DEFAULT_EPSILON, 1.0f - IUtils.DEFAULT_EPSILON);
      final double annRad = annul * 0.5d;

      final double toStep = 1.0d / ( sctCount - 1.0d );
      final double origAngle = IUtils.TAU_D * a1;
      final double destAngle = IUtils.TAU_D * ( a1 + arcLen1 );

      for ( int k = 0, i = 0, j = 1; k < sctCount; ++k, i += 2, j += 2 ) {
         final double step = k * toStep;
         final double theta = ( 1.0d - step ) * origAngle + step * destAngle;
         final double cosa = Math.cos(theta);
         final double sina = Math.sin(theta);

         final Vec2 v0 = vs[i];
         v0.set(
            ( float ) ( 0.5d * cosa ),
            ( float ) ( 0.5d * sina ));

         final Vec2 v1 = vs[j];
         v1.set(
            ( float ) ( annRad * cosa ),
            ( float ) ( annRad * sina ));

         final Vec2 vt0 = vts[i];
         vt0.x = v0.x + 0.5f;
         vt0.y = 0.5f - v0.y;

         final Vec2 vt1 = vts[j];
         vt1.x = v1.x + 0.5f;
         vt1.y = 0.5f - v1.y;
      }

      int len;

      switch ( poly ) {

         case NGON:

            len = sctCount - 1;
            target.faces = new int[len][4][2];

            for ( int k = 0, i = 0, j = 1; k < len; ++k, i += 2, j += 2 ) {
               final int m = i + 2;
               final int n = j + 2;
               final int[][] f = target.faces[k];
               f[0][0] = i;
               f[0][1] = i;
               f[1][0] = m;
               f[1][1] = m;
               f[2][0] = n;
               f[2][1] = n;
               f[3][0] = j;
               f[3][1] = j;
            }

            break;

         case TRI:

         default:

            len = sctCount2 - 2;
            target.faces = new int[len][3][2];

            for ( int i = 0, j = 1; i < len; i += 2, j += 2 ) {
               final int m = i + 2;
               final int n = j + 2;

               final int[][] f0 = target.faces[i];
               f0[0][0] = i;
               f0[0][1] = i;
               f0[1][0] = m;
               f0[1][1] = m;
               f0[2][0] = j;
               f0[2][1] = j;

               final int[][] f1 = target.faces[j];
               f1[0][0] = m;
               f1[0][1] = m;
               f1[1][0] = n;
               f1[1][1] = n;
               f1[2][0] = j;
               f1[2][1] = j;
            }

      }

      return target;
   }

   /**
    * Creates an arc from a start and stop angle.
    *
    * @param startAngle the start angle
    * @param stopAngle  the stop angle
    * @param oculus     the size of the opening
    * @param target     the output mesh
    *
    * @return the arc
    */
   public static final Mesh2 arc (
      final float startAngle,
      final float stopAngle,
      final float oculus,
      final Mesh2 target ) {

      return Mesh2.arc(
         startAngle, stopAngle, oculus,
         IMesh.DEFAULT_CIRCLE_SECTORS,
         Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates an arc from a start and stop angle.
    *
    * @param startAngle the start angle
    * @param stopAngle  the stop angle
    * @param sectors    number of sectors in a circle
    * @param target     the output mesh
    *
    * @return the arc
    */
   public static final Mesh2 arc (
      final float startAngle,
      final float stopAngle,
      final int sectors,
      final Mesh2 target ) {

      return Mesh2.arc(
         startAngle, stopAngle,
         IMesh.DEFAULT_OCULUS, sectors,
         Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates an arc from a start and stop angle.
    *
    * @param startAngle the start angle
    * @param stopAngle  the stop angle
    * @param target     the output mesh
    *
    * @return the arc
    */
   public static final Mesh2 arc (
      final float startAngle,
      final float stopAngle,
      final Mesh2 target ) {

      return Mesh2.arc(
         startAngle, stopAngle,
         IMesh.DEFAULT_OCULUS,
         IMesh.DEFAULT_CIRCLE_SECTORS,
         Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates an arc from a stop angle. The start angle is presumed to be 0.0
    * degrees.
    *
    * @param stopAngle the stop angle
    * @param target    the output mesh
    *
    * @return the arc
    */
   public static final Mesh2 arc (
      final float stopAngle,
      final Mesh2 target ) {

      return Mesh2.arc(
         0.0f, stopAngle,
         IMesh.DEFAULT_OCULUS,
         IMesh.DEFAULT_CIRCLE_SECTORS,
         Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Calculates the dimensions of an Axis-Aligned Bounding Box (AABB)
    * encompassing the mesh.
    *
    * @param mesh   the mesh
    * @param target the output dimensions
    * @param lb     the lower bound
    * @param ub     the upper bound
    *
    * @return the dimensions
    */
   public static Vec2 calcDimensions (
      final Mesh2 mesh,
      final Vec2 target,
      final Vec2 lb,
      final Vec2 ub ) {

      lb.set(
         Float.MAX_VALUE,
         Float.MAX_VALUE);
      ub.set(
         Float.MIN_VALUE,
         Float.MIN_VALUE);

      final Vec2[] coords = mesh.coords;
      final int len = coords.length;

      for ( int i = 0; i < len; ++i ) {

         final Vec2 coord = coords[i];
         final float x = coord.x;
         final float y = coord.y;

         /* Minimum, maximum need separate if checks, not if-else. */
         if ( x < lb.x ) { lb.x = x; }
         if ( x > ub.x ) { ub.x = x; }
         if ( y < lb.y ) { lb.y = y; }
         if ( y > ub.y ) { ub.y = y; }
      }

      return Vec2.sub(ub, lb, target);
   }

   /**
    * Creates a regular convex polygon, approximating a circle.
    *
    * @param target the output mesh
    *
    * @return the polygon
    *
    * @see Mesh2#polygon(int, PolyType, Mesh2)
    */
   public static Mesh2 circle (
      final Mesh2 target ) {

      return Mesh2.polygon(
         IMesh.DEFAULT_CIRCLE_SECTORS,
         Mesh2.DEFAULT_POLY_TYPE,
         target);
   }

   /**
    * Creates a regular convex polygon, approximating a circle.
    *
    * @param poly   the polygon type
    * @param target the output mesh
    *
    * @return the polygon
    *
    * @see Mesh2#polygon(int, PolyType, Mesh2)
    */
   public static Mesh2 circle (
      final Mesh2.PolyType poly,
      final Mesh2 target ) {

      return Mesh2.polygon(
         IMesh.DEFAULT_CIRCLE_SECTORS,
         poly, target);
   }

   /**
    * Evaluates whether the mesh contains a point. Uses vertex winding (as
    * opposed to casting a ray).
    *
    * @param mesh  the mesh
    * @param point the point
    *
    * @return the evaluation
    */
   public static boolean contains (
      final Mesh2 mesh,
      final Vec2 point ) {

      final Vec2[] vs = mesh.coords;
      final int[][][] fs = mesh.faces;
      final int fsLen = fs.length;

      for ( int i = 0; i < fsLen; ++i ) {

         final int[][] f = fs[i];
         final int fLen = f.length;
         int wn = 0;

         for ( int j = 0; j < fLen; ++j ) {

            final int k = ( j + 1 ) % fLen;

            final int[] vert0 = f[j];
            final int[] vert1 = f[k];

            final Vec2 curr = vs[vert0[0]];
            final Vec2 next = vs[vert1[0]];

            /*
             * Evaluate the sign of the cross product of the differences between
             * next, current, point.
             */

            /* @formatter:off */
            if ( curr.y <= point.y && next.y > point.y ) {

               final float eval = ( next.x - curr.x ) * ( point.y - curr.y ) -
                                  ( point.x - curr.x ) * ( next.y - curr.y );
               if ( eval > 0.0f ) { ++wn; }

            } else if ( next.y <= point.y ) {

               final float eval = ( next.x - curr.x ) * ( point.y - curr.y ) -
                                  ( point.x - curr.x ) * ( next.y - curr.y );
               if ( eval < 0.0f ) { --wn; }

            }
            /* @formatter:on */
         }

         if ( wn > 0 ) { return true; }
      }

      return false;
   }

   /**
    * Creates an array of meshes, each with one face from the source mesh.
    *
    * @param source the source mesh
    *
    * @return the meshes array
    */
   public static Mesh2[] detachFaces ( final Mesh2 source ) {

      final int[][][] fsSrc = source.faces;
      final Vec2[] vsSrc = source.coords;
      final Vec2[] vtsSrc = source.texCoords;

      final int fsLen = fsSrc.length;
      final Mesh2[] meshes = new Mesh2[fsLen];

      for ( int i = 0; i < fsLen; ++i ) {
         final int[][] fSrc = fsSrc[i];
         final int fLen = fSrc.length;

         final int[][][] fsTrg = new int[1][fLen][2];
         final Vec2[] vsTrg = new Vec2[fLen];
         final Vec2[] vtsTrg = new Vec2[fLen];

         final int[][] fTrg = fsTrg[0];
         for ( int j = 0; j < fLen; ++j ) {
            final int[] vertSrc = fSrc[j];

            vsTrg[j] = new Vec2(vsSrc[vertSrc[0]]);
            vtsTrg[j] = new Vec2(vtsSrc[vertSrc[1]]);

            fTrg[j][0] = j;
            fTrg[j][1] = j;
         }

         meshes[i] = new Mesh2(fsTrg, vsTrg, vtsTrg);
      }

      return meshes;
   }

   /**
    * Creates a subdivided plane. Useful for meshes which later will be
    * augmented by noise or height maps to simulate terrain.
    *
    * @param cols   number of columns
    * @param rows   number of rows
    * @param target the output mesh
    *
    * @return the plane
    */
   public static final Mesh2 plane (
      final int cols,
      final int rows,
      final Mesh2 target ) {

      return Mesh2.plane(cols, rows, Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a plane subdivided into either triangles or quadrilaterals,
    * depending on the polygon type. Useful for meshes which later will be
    * augmented by noise or height maps to simulate terrain.
    *
    * @param cols   number of columns
    * @param rows   number of rows
    * @param poly   the polygon type
    * @param target the output mesh
    *
    * @return the plane
    */
   public static final Mesh2 plane (
      final int cols,
      final int rows,
      final PolyType poly,
      final Mesh2 target ) {

      target.name = "Plane";

      final int rval = rows < 1 ? 1 : rows;
      final int cval = cols < 1 ? 1 : cols;

      final int rval1 = rval + 1;
      final int cval1 = cval + 1;

      final float iToStep = 1.0f / rval;
      final float jToStep = 1.0f / cval;

      final Vec2[] vs = target.coords = Vec2.resize(
         target.coords, rval1 * cval1);
      final Vec2[] vts = target.texCoords = Vec2.resize(
         target.texCoords, vs.length);
      final int flen = rval * cval;

      /* Calculate x values in separate loop. */
      final float[] xs = new float[cval1];
      final float[] us = new float[cval1];
      for ( int j = 0; j < cval1; ++j ) {
         final float xPrc = j * jToStep;
         xs[j] = xPrc - 0.5f;
         us[j] = xPrc;
      }

      for ( int k = 0, i = 0; i < rval1; ++i ) {
         final float yPrc = i * iToStep;
         final float y = yPrc - 0.5f;
         final float v = 1.0f - yPrc;

         for ( int j = 0; j < cval1; ++j, ++k ) {
            vs[k].set(xs[j], y);
            vts[k].set(us[j], v);
         }
      }

      switch ( poly ) {

         case NGON:

            target.faces = new int[flen][4][2];

            for ( int k = 0, i = 0; i < rval; ++i ) {
               final int noff0 = i * cval1;
               final int noff1 = ( i + 1 ) * cval1;

               for ( int j = 0; j < cval; ++j, ++k ) {
                  final int n00 = noff0 + j;
                  final int n10 = n00 + 1;
                  final int n01 = noff1 + j;
                  final int n11 = n01 + 1;

                  final int[][] f = target.faces[k];

                  f[0][0] = n00;
                  f[0][1] = n00;

                  f[1][0] = n10;
                  f[1][1] = n10;

                  f[2][0] = n11;
                  f[2][1] = n11;

                  f[3][0] = n01;
                  f[3][1] = n01;
               }
            }

            break;

         case TRI:

         default:

            target.faces = new int[flen + flen][3][2];

            for ( int k = 0, i = 0; i < rval; ++i ) {
               final int noff0 = i * cval1;
               final int noff1 = ( i + 1 ) * cval1;

               for ( int j = 0; j < cval; ++j, k += 2 ) {
                  final int n00 = noff0 + j;
                  final int n10 = n00 + 1;
                  final int n01 = noff1 + j;
                  final int n11 = n01 + 1;

                  final int[][] f0 = target.faces[k];
                  f0[0][0] = n00;
                  f0[0][1] = n00;

                  f0[1][0] = n10;
                  f0[1][1] = n10;

                  f0[2][0] = n11;
                  f0[2][1] = n11;

                  final int[][] f1 = target.faces[k + 1];
                  f1[0][0] = n11;
                  f1[0][1] = n11;

                  f1[1][0] = n01;
                  f1[1][1] = n01;

                  f1[2][0] = n00;
                  f1[2][1] = n00;
               }
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
    *
    * @return the plane
    */
   public static final Mesh2 plane (
      final int div,
      final Mesh2 target ) {

      return Mesh2.plane(div, div, Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a regular convex polygon.
    *
    * @param sectors the number of sides
    * @param target  the output mesh
    *
    * @return the polygon
    */
   public static Mesh2 polygon (
      final int sectors,
      final Mesh2 target ) {

      return Mesh2.polygon(sectors, Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a regular convex polygon.
    *
    * @param sectors the number of sides
    * @param poly    the polygon type
    * @param target  the output mesh
    *
    * @return the polygon
    */
   public static Mesh2 polygon (
      final int sectors,
      final PolyType poly,
      final Mesh2 target ) {

      /*
       * Polar coordinates need to be more precise, given that they are scaled
       * up and can impact SVG rendering.
       */

      target.name = "Polygon";

      final int seg = sectors < 3 ? 3 : sectors;
      final boolean isNgon = poly == PolyType.NGON;
      final int newLen = isNgon ? seg : seg + 1;
      final double toTheta = IUtils.TAU_D / seg;

      final Vec2[] vs = target.coords = Vec2.resize(target.coords, newLen);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
         newLen);

      switch ( poly ) {
         case NGON:

            target.faces = new int[1][seg][2];
            final int[][] ngon = target.faces[0];

            for ( int i = 0; i < seg; ++i ) {
               final double theta = i * toTheta;

               final Vec2 v = vs[i];
               v.set(
                  ( float ) ( 0.5d * Math.cos(theta) ),
                  ( float ) ( 0.5d * Math.sin(theta) ));

               final Vec2 vt = vts[i];
               vt.x = v.x + 0.5f;
               vt.y = 0.5f - v.y;

               ngon[i][0] = i;
               ngon[i][1] = i;
            }

            break;

         case TRI:

         default:

            final int[][][] fs = target.faces = new int[seg][3][2];
            vs[0].set(0.0f, 0.0f);
            vts[0].set(0.5f, 0.5f);

            for ( int i = 0, j = 1; i < seg; ++i, ++j ) {
               final double theta = i * toTheta;

               final Vec2 v = vs[j];
               v.set(
                  ( float ) ( 0.5d * Math.cos(theta) ),
                  ( float ) ( 0.5d * Math.sin(theta) ));

               final Vec2 vt = vts[j];
               vt.x = v.x + 0.5f;
               vt.y = 0.5f - v.y;

               final int k = 1 + j % seg;
               final int[][] f = fs[i];
               f[0][0] = 0;
               f[0][1] = 0;

               f[1][0] = j;
               f[1][1] = j;

               f[2][0] = k;
               f[2][1] = k;
            }

      }

      return target;
   }

   /**
    * Creates a regular convex polygon with an opening in its center. The oculus
    * describes the relative size of this opening.
    *
    * @param sectors the number of sides
    * @param oculus  the size of the opening
    * @param target  the output type
    *
    * @return the ring
    */
   public static final Mesh2 ring (
      final float oculus,
      final int sectors,
      final Mesh2 target ) {

      return Mesh2.ring(oculus, sectors, Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a regular convex polygon with an opening in its center. The oculus
    * describes the relative size of this opening. When the polygon type is
    * NGON, the ring will be composed of quadrilaterals; otherwise, triangles.
    *
    * @param oculus  the size of the opening
    * @param sectors the number of sides
    * @param poly    the polygon type
    * @param target  the output type
    *
    * @return the ring
    */
   public static final Mesh2 ring (
      final float oculus,
      final int sectors,
      final PolyType poly,
      final Mesh2 target ) {

      target.name = "Ring";

      final boolean isNgon = poly == PolyType.NGON;
      final int seg = sectors < 3 ? 3 : sectors;
      final int seg2 = seg + seg;
      final float annul = Utils.clamp(oculus,
         IUtils.DEFAULT_EPSILON, 1.0f - IUtils.DEFAULT_EPSILON);

      final double toTheta = IUtils.TAU_D / seg;
      final double annRad = annul * 0.5d;

      final Vec2[] vs = target.coords = Vec2.resize(target.coords, seg2);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
         seg2);
      target.faces = isNgon ? new int[seg][4][2]
         : new int[seg2][3][2];

      for ( int k = 0, i = 0, j = 1; k < seg; ++k, i += 2, j += 2 ) {
         final double theta = k * toTheta;
         final double cosa = Math.cos(theta);
         final double sina = Math.sin(theta);

         final Vec2 v0 = vs[i];
         v0.set(
            ( float ) ( 0.5d * cosa ),
            ( float ) ( 0.5d * sina ));

         final Vec2 v1 = vs[j];
         v1.set(
            ( float ) ( annRad * cosa ),
            ( float ) ( annRad * sina ));

         final Vec2 vt0 = vts[i];
         vt0.x = v0.x + 0.5f;
         vt0.y = 0.5f - v0.y;

         final Vec2 vt1 = vts[j];
         vt1.x = v1.x + 0.5f;
         vt1.y = 0.5f - v1.y;

         final int m = ( i + 2 ) % seg2;
         final int n = ( j + 2 ) % seg2;

         if ( isNgon ) {

            final int[][] f = target.faces[k];
            f[0][0] = i;
            f[0][1] = i;
            f[1][0] = m;
            f[1][1] = m;
            f[2][0] = n;
            f[2][1] = n;
            f[3][0] = j;
            f[3][1] = j;

         } else {

            final int[][] f0 = target.faces[i];
            f0[0][0] = i;
            f0[0][1] = i;
            f0[1][0] = m;
            f0[1][1] = m;
            f0[2][0] = j;
            f0[2][1] = j;

            final int[][] f1 = target.faces[j];
            f1[0][0] = m;
            f1[0][1] = m;
            f1[1][0] = n;
            f1[1][1] = n;
            f1[2][0] = j;
            f1[2][1] = j;

         }
      }

      return target;
   }

   /**
    * Creates a regular convex polygon with an opening in its center.
    *
    * @param oculus the size of the opening
    * @param target the output mesh
    *
    * @return the ring
    */
   public static final Mesh2 ring (
      final float oculus,
      final Mesh2 target ) {

      return Mesh2.ring(
         oculus,
         IMesh.DEFAULT_CIRCLE_SECTORS,
         Mesh2.DEFAULT_POLY_TYPE,
         target);
   }

   /**
    * Creates a regular convex polygon with an opening in its center.
    *
    * @param sectors the number of sides
    * @param target  the output mesh
    *
    * @return the ring
    */
   public static final Mesh2 ring (
      final int sectors,
      final Mesh2 target ) {

      return Mesh2.ring(
         IMesh.DEFAULT_OCULUS,
         sectors,
         Mesh2.DEFAULT_POLY_TYPE,
         target);
   }

   /**
    * Creates a regular convex polygon with an opening in its center.
    *
    * @param target the output mesh
    *
    * @return the ring
    */
   public static final Mesh2 ring ( final Mesh2 target ) {

      return Mesh2.ring(
         IMesh.DEFAULT_OCULUS,
         IMesh.DEFAULT_CIRCLE_SECTORS,
         Mesh2.DEFAULT_POLY_TYPE,
         target);
   }

   /**
    * Creates a square.
    *
    * @param target the output mesh
    *
    * @return the square
    */
   public static final Mesh2 square ( final Mesh2 target ) {

      return Mesh2.square(Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a square. Useful when representing an image plane with a mesh
    * entity.
    *
    * @param target the output mesh
    * @param poly   the polygon type
    *
    * @return the square
    */
   public static final Mesh2 square (
      final PolyType poly,
      final Mesh2 target ) {

      target.name = "Square";

      target.coords = Vec2.resize(target.coords, 4);
      target.coords[0].set(-0.5f, -0.5f);
      target.coords[1].set(0.5f, -0.5f);
      target.coords[2].set(0.5f, 0.5f);
      target.coords[3].set(-0.5f, 0.5f);

      target.texCoords = Vec2.resize(target.texCoords, 4);
      target.texCoords[0].set(0.0f, 1.0f);
      target.texCoords[1].set(1.0f, 1.0f);
      target.texCoords[2].set(1.0f, 0.0f);
      target.texCoords[3].set(0.0f, 0.0f);

      switch ( poly ) {

         case NGON:

            target.faces = new int[][][] {
               { { 0, 0 }, { 1, 1 }, { 2, 2 }, { 3, 3 } } };

            break;

         case TRI:

         default:

            target.faces = new int[][][] {
               { { 0, 0 }, { 1, 1 }, { 2, 2 } },
               { { 0, 0 }, { 2, 2 }, { 3, 3 } } };

      }

      return target;
   }

   /**
    * Creates a triangle. A shorthand for calling polygon with three sides.
    *
    * @param target the output mesh
    *
    * @return the triangle
    */
   public static final Mesh2 triangle ( final Mesh2 target ) {

      target.name = "Triangle";

      target.coords = Vec2.resize(target.coords, 3);
      target.coords[0].set(0.5f, 0.0f);
      target.coords[1].set(-0.25f, 0.4330127f);
      target.coords[2].set(-0.25f, -0.4330127f);

      target.texCoords = Vec2.resize(target.texCoords, 3);
      target.texCoords[0].set(1.0f, 0.5f);
      target.texCoords[1].set(0.25f, 0.066987306f);
      target.texCoords[2].set(0.25f, 0.9330127f);

      target.faces = new int[][][] { { { 0, 0 }, { 1, 1 }, { 2, 2 } } };

      return target;
   }

   /**
    * Restructures the mesh so that each face index refers to unique data,
    * indifferent to redundancies. As a consequence, coordinate and texture
    * coordinate are of equal length and face indices are easier to read and
    * understand. Useful for making a mesh similar to those in Unity or p5.
    * Similar to 'ripping' vertices or 'tearing' edges in Blender.
    *
    * @param source the source mesh
    * @param target the target mesh
    *
    * @return the mesh
    */
   public static Mesh2 uniformData (
      final Mesh2 source,
      final Mesh2 target ) {

      target.name = source.name;

      final int len0 = source.faces.length;
      final int capacity = len0 * 4;

      /* Has to be a new array for the case source == target. */
      final int[][][] trgfs = new int[len0][][];
      final ArrayList < Vec2 > vs = new ArrayList <>(capacity);
      final ArrayList < Vec2 > vts = new ArrayList <>(capacity);

      for ( int k = 0, i = 0; i < len0; ++i ) {

         final int[][] fs0 = source.faces[i];
         final int len1 = fs0.length;
         final int[][] trgfs0 = trgfs[i] = new int[len1][2];

         for ( int j = 0; j < len1; ++j, ++k ) {

            final int[] fs1 = fs0[j];

            vs.add(new Vec2(source.coords[fs1[0]]));
            vts.add(new Vec2(source.texCoords[fs1[1]]));

            trgfs0[j][0] = k;
            trgfs0[j][1] = k;
         }
      }

      target.coords = vs.toArray(new Vec2[vs.size()]);
      target.texCoords = vts.toArray(new Vec2[vts.size()]);
      target.faces = trgfs;
      return target;
   }

   /**
    * An iterator, which allows a mesh's faces to be accessed in an enhanced for
    * loop.
    */
   public static final class Face2Iterator implements Iterator < Face2 > {

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The mesh being iterated over.
       */
      private final Mesh2 mesh;

      /**
       * The default constructor.
       *
       * @param mesh the mesh to iterate
       */
      public Face2Iterator ( final Mesh2 mesh ) { this.mesh = mesh; }

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
       * @see Mesh2#getFace(int, Face2)
       */
      @Override
      public Face2 next ( ) {

         return this.mesh.getFace(this.index++, new Face2());
      }

      /**
       * Gets the next value in the iterator.
       *
       * @param target the output face
       *
       * @return the value
       *
       * @see Mesh2#getFace(int, Face2)
       */
      public Face2 next ( final Face2 target ) {

         return this.mesh.getFace(this.index++, target);
      }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) {

         return this.getClass().getSimpleName();
      }

   }

   /**
    * The type of polygon produced by the static polygon function.
    */
   public enum PolyType {

      /**
       * Create an n-sided polygon.
       */
      NGON ( ),

      /**
       * Create a triangle-based polygon.
       */
      TRI ( );

      /**
       * The default constructor.
       */
      private PolyType ( ) {}

   }

   /**
    * Compares two face indices (an array of vertex indices) by averaging the
    * vectors referenced by them, then comparing the averages.
    */
   protected static final class SortIndices2
      implements Comparator < int[][] > {

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
       * The default constructor.
       *
       * @param coords the coordinate array.
       */
      protected SortIndices2 ( final Vec2[] coords ) {

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
         for ( int i = 0; i < aLen; ++i ) {
            Vec2.add(
               this.aAvg,
               this.coords[a[i][0]],
               this.aAvg);
         }
         Vec2.div(this.aAvg, aLen, this.aAvg);

         this.bAvg.reset();
         final int bLen = b.length;
         for ( int i = 0; i < bLen; ++i ) {
            Vec2.add(
               this.bAvg,
               this.coords[b[i][0]],
               this.bAvg);
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
      public String toString ( ) {

         return this.getClass().getSimpleName();
      }

   }

}
