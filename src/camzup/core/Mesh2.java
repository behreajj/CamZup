package camzup.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Organizes data needed to draw a two dimensional shape using vertices and
 * faces. Given that a mesh is primarily a collection of references, it is
 * initialized with null arrays (coordinates, texture coordinates and
 * indices). These are not final, and so can be reassigned.
 */
public class Mesh2 extends Mesh implements Iterable < Face2 >, ISvgWritable {

   /**
    * An array of coordinates in the mesh.
    */
   public Vec2[] coords;

   /**
    * The texture (UV) coordinates that describe how an image is mapped onto
    * the geometry of the mesh. Typically in the range [0.0, 1.0].
    */
   public Vec2[] texCoords;

   /**
    * The default constructor.
    */
   public Mesh2 ( ) { super(); }

   /**
    * Creates a mesh from arrays of faces, coordinates and texture
    * coordinates. The mesh's arrays are set by reference, not by value.
    *
    * @param faces     the faces array
    * @param coords    the coordinates array
    * @param texCoords the texture coordinates array
    */
   public Mesh2 ( final int[][][] faces, final Vec2[] coords,
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
   public Mesh2 ( final String name, final int[][][] faces, final Vec2[] coords,
      final Vec2[] texCoords ) {

      super(name);
      this.set(faces, coords, texCoords);
   }

   /**
    * Calculates texture coordinates (UVs) for this mesh. Finds the
    * object-space dimensions of each coordinate, then using the frame as a
    * reference for new UVs, such that the shape acts as a mask for the
    * texture (or, the texture fills the shape without repeating).
    *
    * @return this mesh
    *
    * @see Mesh2#calcDimensions(Mesh2, Vec2, Vec2, Vec2)
    * @see Vec2#div(float, Vec2, Vec2)
    */
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
    * Removes elements from the coordinate, texture coordinate and normal
    * arrays of the mesh which are not visited by the face indices.
    *
    * @return this mesh
    */
   @Experimental
   public Mesh2 clean ( ) {

      /* Transfer arrays to hash maps where the face index is the key. */
      final HashMap < Integer, Vec2 > usedCoords = new HashMap <>();
      final HashMap < Integer, Vec2 > usedTexCoords = new HashMap <>();

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

            /* The hash map will ignore repeated visitations. */
            usedCoords.put(vIdx, this.coords[vIdx]);
            usedTexCoords.put(vtIdx, this.texCoords[vtIdx]);
         }
      }

      /* Use a tree set to filter out similar vectors. */
      final TreeSet < Vec2 > coordsTree = new TreeSet <>(Mesh.SORT_2);
      final TreeSet < Vec2 > texCoordsTree = new TreeSet <>(Mesh.SORT_2);

      /* Dictionary's keys are no longer needed; just values. */
      coordsTree.addAll(usedCoords.values());
      texCoordsTree.addAll(usedTexCoords.values());

      /* Convert from sorted set to arrays. */
      final Vec2[] newCoords = coordsTree.toArray(new Vec2[coordsTree.size()]);
      final Vec2[] newTexCoords = texCoordsTree.toArray(new Vec2[texCoordsTree
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
               Mesh.SORT_2);
            vert[1] = Arrays.binarySearch(newTexCoords, this.texCoords[vert[1]],
               Mesh.SORT_2);
         }
      }

      this.coords = newCoords;
      this.texCoords = newTexCoords;
      Arrays.sort(this.faces, new Mesh2.SortIndices2(this.coords));

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
   public Mesh2 extrudeEdge ( final int faceIdx, final int edgeIdx,
      final float amt ) {

      if ( amt == 0.0f ) { return this; }

      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIdx, facesLen);
      final int[][] face = this.faces[i];
      final int vertsLen = face.length;

      final int j = Utils.mod(edgeIdx, vertsLen);
      final int k = ( j + 1 ) % vertsLen;
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

      final Vec2 vtNewOrigin = new Vec2();
      final Vec2 vtNewDest = new Vec2();
      Vec2.add(vtOrigin, vtPerp, vtNewOrigin);
      Vec2.add(vtDest, vtPerp, vtNewDest);

      final int[][][] faceNew = { { { idxV1, idxVt1 }, { idxV2, idxVt2 }, {
         idxV3, idxVt3 }, { idxV0, idxVt0 } } };
      this.coords = Vec2.concat(this.coords, new Vec2[] { vNewOrigin,
         vNewDest });
      this.texCoords = Vec2.concat(this.texCoords, new Vec2[] { vtNewOrigin,
         vtNewDest });
      this.faces = Mesh.splice(this.faces, i + 1, 0, faceNew);

      return this;
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
   public Edge2 getEdge ( final int i, final int j, final Edge2 target ) {

      final int[][] f0 = this.faces[Utils.mod(i, this.faces.length)];
      final int f0len = f0.length;
      final int k = Utils.mod(j, f0len);
      final int[] f1 = f0[k];
      final int[] f2 = f0[ ( k + 1 ) % f0len];

      return target.set(this.coords[f1[0]], this.texCoords[f1[1]],
         this.coords[f2[0]], this.texCoords[f2[1]]);
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

            trial.set(this.coords[fOrigin[0]], this.texCoords[fOrigin[1]],
               this.coords[fDest[0]], this.texCoords[fDest[1]]);

            if ( result.indexOf(trial) < 0 ) {
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
   public Face2 getFace ( final int i, final Face2 target ) {

      final int[][] face = this.faces[Utils.mod(i, this.faces.length)];
      final int len = face.length;
      final Vert2[] vertices = new Vert2[len];

      for ( int j = 0; j < len; ++j ) {
         final int[] vert = face[j];
         vertices[j] = new Vert2(this.coords[vert[0]], this.texCoords[vert[1]]);
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
            verts[j] = new Vert2(this.coords[fs1[0]], this.texCoords[fs1[1]]);
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
   public Vert2 getVertex ( final int i, final int j, final Vert2 target ) {

      final int[][] f0 = this.faces[Utils.mod(i, this.faces.length)];
      final int[] f = f0[Utils.mod(j, f0.length)];

      return target.set(this.coords[f[0]], this.texCoords[f[1]]);
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
            trial.set(this.coords[f[0]], this.texCoords[f[1]]);

            if ( result.indexOf(trial) < 0 ) {
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
   public Mesh2 insetFace ( final int faceIdx ) {

      return this.insetFace(faceIdx, 0.5f);
   }

   /**
    * Insets a face by calculating its center then easing from the face's
    * vertices toward the center by the factor. The factor is expected to be
    * in the range [0.0, 1.0] . When it is less than 0.0, the face remains
    * unchanged; when it is greater than 1.0, the face is subdivided by
    * center.
    *
    * @param faceIdx the face index
    * @param fac     the inset amount
    *
    * @return the new face indices
    */
   @Experimental
   public Mesh2 insetFace ( final int faceIdx, final float fac ) {

      if ( fac <= 0.0f ) { return this; }
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

         /* Sum centers. */
         Vec2.add(vCenter, vCurr, vCenter);
         Vec2.add(vtCenter, vtCurr, vtCenter);
      }

      /* Average. */
      if ( faceLen > 0 ) {
         final float flInv = 1.0f / faceLen;
         Vec2.mul(vCenter, flInv, vCenter);
         Vec2.mul(vtCenter, flInv, vtCenter);
      }

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

         /* @formatter:off */
         vsNew[j] = new Vec2(
            u * vCurr.x + fac * vCenter.x,
            u * vCurr.y + fac * vCenter.y);

         vtsNew[j] = new Vec2(
            u * vtCurr.x + fac * vtCenter.x,
            u * vtCurr.y + fac * vtCenter.y);

         final int vSubdivIdx = vsOldLen + j;
         final int vtSubdivIdx = vtsOldLen + j;

         fsNew[j] = new int[][] {
            {   vCornerIdx,   vtCornerIdx },
            {  vertNext[0],   vertNext[1] },
            { vsOldLen + k, vtsOldLen + k },
            {   vSubdivIdx,   vtSubdivIdx } };
         /* @formatter:on */

         centerFace[j][0] = vSubdivIdx;
         centerFace[j][1] = vtSubdivIdx;

      }

      this.coords = Vec2.concat(this.coords, vsNew);
      this.texCoords = Vec2.concat(this.texCoords, vtsNew);
      this.faces = Mesh.splice(this.faces, i, 1, fsNew);

      return this;
   }

   /**
    * Insets all faces in the mesh once.
    *
    * @return this mesh
    */
   public Mesh2 insetFaces ( ) { return this.insetFaces(1); }

   /**
    * Insets all faces in the mesh for a given number of iterations by a
    * factor of 0.5 .
    *
    * @param itr the iterations
    *
    * @return this mesh
    */
   public Mesh2 insetFaces ( final int itr ) {

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
   public Mesh2 insetFaces ( final int itr, final float fac ) {

      for ( int i = 0; i < itr; ++i ) {
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
   public Iterator < Face2 > iterator ( ) { return new Face2Iterator(this); }

   /**
    * Gets the number of faces held by this mesh.
    *
    * @return the length
    */
   public int length ( ) { return this.faces.length; }

   /**
    * Centers the mesh about the origin, (0.0, 0.0), and rescales it to the
    * range [-0.5, 0.5] . Emits a transform which records the mesh's center
    * point and original dimension. The transform's rotation is reset.
    *
    * @param tr the output transform
    *
    * @return this mesh
    *
    * @see Mesh2#calcDimensions(Mesh2, Vec2, Vec2, Vec2)
    */
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
   public Mesh2 reverseEdge ( final int i, final int j ) {

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
   public Mesh2 reverseFace ( final int i ) {

      final int[][] face = this.faces[Utils.mod(i, this.faces.length)];
      final int len = face.length;
      final int halfLen = len >> 1;
      final int last = len - 1;
      for ( int j = 0; j < halfLen; ++j ) {
         final int reverse = last - j;
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
    * Rounds a corner to an arc with a given radius. Uses one-half of the
    * {@link IMesh#DEFAULT_CIRCLE_SECTORS},
    * {@value IMesh#DEFAULT_CIRCLE_SECTORS}, as a resolution. The corner's
    * original coordinate is retained in case it is used by another face
    * within the mesh.
    *
    * @param faceIdx   the face index
    * @param cornerIdx the corner index
    * @param radius    the corner radius
    *
    * @return this mesh
    */
   public Mesh2 roundCorner ( final int faceIdx, final int cornerIdx,
      final float radius ) {

      return this.roundCorner(faceIdx, cornerIdx, radius,
         IMesh.DEFAULT_CIRCLE_SECTORS >> 1);
   }

   /**
    * Rounds a corner to an arc with a given radius. The number of points in
    * the arc is specified by the count; if the count is less than zero, a
    * flat miter is created. The corner's original coordinate is retained in
    * case it is used by another face within the mesh.
    *
    * @param faceIdx   the face index
    * @param cornerIdx the corner index
    * @param radius    the corner radius
    * @param count     corner resolution
    *
    * @return this mesh
    */
   @Experimental
   public Mesh2 roundCorner ( final int faceIdx, final int cornerIdx,
      final float radius, final int count ) {

      /*
       * https://stackoverflow.com/questions/24771828/algorithm-for-creating-
       * rounded-corners-in-a-polygon
       */

      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIdx, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      final int currIdx = Utils.mod(cornerIdx, faceLen);
      final int prevIdx = Utils.mod(currIdx - 1, faceLen);
      final int nextIdx = ( currIdx + 1 ) % faceLen;

      /* Acquire vertices. */
      final int[] currVert = face[currIdx];
      final int[] prevVert = face[prevIdx];
      final int[] nextVert = face[nextIdx];

      /* Acquire corner (current), next and previous coordinate. */
      final Vec2 vcurr = this.coords[currVert[0]];
      final Vec2 vprev = this.coords[prevVert[0]];
      final Vec2 vnext = this.coords[nextVert[0]];

      /* Coordinate edge 0. */
      final float d0x = vcurr.x - vprev.x;
      final float d0y = vcurr.y - vprev.y;
      final float d0Heading = Utils.atan2(d0y, d0x);
      final float d0MagSq = d0x * d0x + d0y * d0y;
      final float d0InvMag = Utils.invSqrtUnchecked(d0MagSq);
      final float d0Mag = d0MagSq * d0InvMag;

      /* Coordinate edge 1. */
      final float d1x = vcurr.x - vnext.x;
      final float d1y = vcurr.y - vnext.y;
      final float d1Heading = Utils.atan2(d1y, d1x);
      final float d1MagSq = d1x * d1x + d1y * d1y;
      final float d1InvMag = Utils.invSqrtUnchecked(d1MagSq);
      final float d1Mag = d1MagSq * d1InvMag;

      /* Validate radius on lower bound. */
      final float rad = Utils.max(IUtils.DEFAULT_EPSILON, radius);

      /* Ensure that radius is not longer than edge. */
      final float vTan = Utils.abs(Utils.tan( ( d0Heading - d1Heading )
         * 0.5f));
      float vRad = rad;
      float vSeg = Utils.div(vRad, vTan);
      final float vLen = Utils.min(d0Mag, d1Mag);
      if ( vSeg > vLen ) {
         vSeg = vLen;
         vRad = vLen * vTan;
      }

      /* Find new coordinate corners. */
      final float fac0 = vSeg * d0InvMag;
      final float fac1 = vSeg * d1InvMag;
      final Vec2 vCorner0 = new Vec2(vcurr.x - d0x * fac0, vcurr.y - d0y
         * fac0);
      final Vec2 vCorner1 = new Vec2(vcurr.x - d1x * fac1, vcurr.y - d1y
         * fac1);

      /* Acquire corner (current), next and previous texture coordinate. */
      final Vec2 vtcurr = this.texCoords[currVert[1]];
      final Vec2 vtprev = this.texCoords[prevVert[1]];
      final Vec2 vtnext = this.texCoords[nextVert[1]];

      /* Texture coordinate edge 0. */
      final float dt0x = vtcurr.x - vtprev.x;
      final float dt0y = vtcurr.y - vtprev.y;
      final float dt0Heading = Utils.atan2(dt0y, dt0x);
      final float dt0MagSq = dt0x * dt0x + dt0y * dt0y;
      final float dt0InvMag = Utils.invSqrtUnchecked(dt0MagSq);
      final float dt0Mag = dt0MagSq * dt0InvMag;

      /* Texture coordinate edge 1. */
      final float dt1x = vtcurr.x - vtnext.x;
      final float dt1y = vtcurr.y - vtnext.y;
      final float dt1Heading = Utils.atan2(dt1y, dt1x);
      final float dt1MagSq = dt1x * dt1x + dt1y * dt1y;
      final float dt1InvMag = Utils.invSqrtUnchecked(dt1MagSq);
      final float dt1Mag = dt1MagSq * dt1InvMag;

      /* Ensure that texture coordinate radius is not longer than edge. */
      final float vtTan = Utils.abs(Utils.tan( ( dt0Heading - dt1Heading )
         * 0.5f));
      float vtRad = rad;
      float vtSeg = Utils.div(vtRad, vtTan);
      final float vtLen = Utils.min(dt0Mag, dt1Mag);
      if ( vtSeg > vtLen ) {
         vtSeg = vtLen;
         vtRad = vtLen * vtTan;
      }

      /* Find new coordinate corners. */
      final float fact0 = vtSeg * dt0InvMag;
      final float fact1 = vtSeg * dt1InvMag;
      final Vec2 vtCorner0 = new Vec2(vtcurr.x - dt0x * fact0, vtcurr.y - dt0y
         * fact0);
      final Vec2 vtCorner1 = new Vec2(vtcurr.x - dt1x * fact1, vtcurr.y - dt1y
         * fact1);

      /* If resolution is less than 1, draw a straight line segment. */
      if ( count < 1 ) {

         final int v0Idx = this.coords.length;
         final int v1Idx = v0Idx + 1;

         final int vt0Idx = this.texCoords.length;
         final int vt1Idx = vt0Idx + 1;

         this.faces[i] = Mesh.splice(face, currIdx, 1, new int[][] { { v0Idx,
            vt0Idx }, { v1Idx, vt1Idx } });
         this.coords = Vec2.concat(this.coords, new Vec2[] { vCorner0,
            vCorner1 });
         this.texCoords = Vec2.concat(this.texCoords, new Vec2[] { vtCorner0,
            vtCorner1 });

         return this;
      }

      /*
       * Find origin of circle. Create an object in case this is helpful in the
       * future, e.g., is included in triangle fan.
       */
      final float vxdelta = vcurr.x + vcurr.x - vCorner0.x - vCorner1.x;
      final float vydelta = vcurr.y + vcurr.y - vCorner0.y - vCorner1.y;
      final float vfac = Utils.hypot(vSeg, vRad) * Utils.invSqrtUnchecked(
         vxdelta * vxdelta + vydelta * vydelta);
      final Vec2 vOrigin = new Vec2(vcurr.x - vxdelta * vfac, vcurr.y - vydelta
         * vfac);

      /* For texture coordinate. */
      final float vtxdelta = vtcurr.x + vtcurr.x - vtCorner0.x - vtCorner1.x;
      final float vtydelta = vtcurr.y + vtcurr.y - vtCorner0.y - vtCorner1.y;
      final float vtfac = Utils.hypot(vtSeg, vtRad) * Utils.invSqrtUnchecked(
         vtxdelta * vtxdelta + vtydelta * vtydelta);
      final Vec2 vtOrigin = new Vec2(vtcurr.x - vtxdelta * vtfac, vtcurr.y
         - vtydelta * vtfac);

      final int countn1 = count - 1;
      final float toStep = 1.0f / countn1;

      final Vec2[] newCoords = new Vec2[count];
      final Vec2[] newTexCoords = new Vec2[count];

      final int[][] newIndices = new int[count][2];
      final int newCoordIdx = this.coords.length;
      final int newTexCoordIdx = this.texCoords.length;

      /* Add initial coordinate to new coordinates. */
      newCoords[0] = vCorner0;
      newTexCoords[0] = vtCorner0;
      newIndices[0] = new int[] { newCoordIdx, newTexCoordIdx };

      /* Create points along arc. */
      final int countn2 = count - 2;
      for ( int j = 0, k = 1; j < countn2; ++j, ++k ) {
         final Vec2 v = newCoords[k] = new Vec2();
         final Vec2 vt = newTexCoords[k] = new Vec2();
         final float t = k * toStep;
         final float u = 1.0f - t;

         /* Lerp, subtract pivot, normalize, re-scale, add pivot. */
         v.set(u * vCorner0.x + t * vCorner1.x, u * vCorner0.y + t
            * vCorner1.y);
         Vec2.sub(v, vOrigin, v);
         Vec2.rescale(v, vRad, v);
         Vec2.add(v, vOrigin, v);

         /* For texture coordinates. */
         vt.set(u * vtCorner0.x + t * vtCorner1.x, u * vtCorner0.y + t
            * vtCorner1.y);
         Vec2.sub(vt, vtOrigin, vt);
         Vec2.rescale(vt, vtRad, vt);
         Vec2.add(vt, vtOrigin, vt);

         newIndices[k] = new int[] { newCoordIdx + k, newTexCoordIdx + k };
      }

      /* Add final corner to new coordinates. */
      newCoords[countn1] = vCorner1;
      newTexCoords[countn1] = vtCorner1;
      newIndices[countn1] = new int[] { newCoordIdx + countn1, newTexCoordIdx
         + countn1 };

      /* Splice into existing data. */
      this.faces[i] = Mesh.splice(face, currIdx, 1, newIndices);
      this.coords = Vec2.concat(this.coords, newCoords);
      this.texCoords = Vec2.concat(this.texCoords, newTexCoords);

      return this;
   }

   /**
    * Rounds all the corners of a face in a mesh. Uses one-half of the
    * {@link IMesh#DEFAULT_CIRCLE_SECTORS},
    * {@value IMesh#DEFAULT_CIRCLE_SECTORS}, as a resolution. The original
    * corners are retained in case they are used by another face within the
    * mesh.
    *
    * @param faceIdx the face index
    * @param radius  the corner radius
    *
    * @return this mesh
    */
   public Mesh2 roundCorners ( final int faceIdx, final float radius ) {

      return this.roundCorners(faceIdx, radius, IMesh.DEFAULT_CIRCLE_SECTORS
         >> 1);
   }

   /**
    * Rounds all the corners of a face in a mesh. The original corners are
    * retained in case they are used by another face within the mesh.
    *
    * @param faceIdx the face index
    * @param radius  the corner radius
    * @param count   the corner resolution
    *
    * @return this mesh
    */
   public Mesh2 roundCorners ( final int faceIdx, final float radius,
      final int count ) {

      final int i = Utils.mod(faceIdx, this.faces.length);
      final int faceLen = this.faces[i].length;
      final int vcount = count < 1 ? 1 : count;

      for ( int j = 0, k = 0; j < faceLen; ++j ) {
         this.roundCorner(faceIdx, k, radius, vcount);
         k += vcount;
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
   public Mesh2 scale ( final float scale ) {

      if ( scale != 0.0f ) {
         final int len = this.coords.length;
         for ( int i = 0; i < len; ++i ) {
            final Vec2 c = this.coords[i];
            Vec2.mul(c, scale, c);
         }
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
   public Mesh2 scale ( final Vec2 scale ) {

      if ( Vec2.all(scale) ) {
         final int len = this.coords.length;
         for ( int i = 0; i < len; ++i ) {
            final Vec2 c = this.coords[i];
            Vec2.mul(c, scale, c);
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
    *
    * @return this mesh
    */
   public Mesh2 set ( final int[][][] faces, final Vec2[] coords,
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
      for ( int i = 0; i < vtslen; ++i ) {
         this.texCoords[i].set(sourcevts[i]);
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
    * Subdivides a convex face. Defaults to center-based subdivision.
    *
    * @param faceIdx the face index
    *
    * @return this mesh
    */
   public Mesh2 subdivFace ( final int faceIdx ) {

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
    * @return this mesh
    */
   @Experimental
   public Mesh2 subdivFaceCenter ( final int faceIdx ) {

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

         /* @formatter:off */
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
            {   vCenterIdx,   vtCenterIdx },
            { vsOldLen + j, vtsOldLen + j },
            {     vNextIdx,     vtNextIdx },
            { vsOldLen + k, vtsOldLen + k } };
         /* @formatter:on */
      }

      /* Average. */
      if ( faceLen > 0 ) {
         final float flInv = 1.0f / faceLen;
         Vec2.mul(vCenter, flInv, vCenter);
         Vec2.mul(vtCenter, flInv, vtCenter);
      }

      this.coords = Vec2.concat(this.coords, vsNew);
      this.texCoords = Vec2.concat(this.texCoords, vtsNew);
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
   public Mesh2 subdivFaceFan ( final int faceIdx ) {

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

         fsNew[j] = new int[][] { { vCenterIdx, vtCenterIdx }, { vCurrIdx,
            vtCurrIdx }, { vertNext[0], vertNext[1] } };
      }

      /* Average. */
      if ( faceLen > 0 ) {
         final float flInv = 1.0f / faceLen;
         Vec2.mul(vCenter, flInv, vCenter);
         Vec2.mul(vtCenter, flInv, vtCenter);
      }

      this.coords = Vec2.append(this.coords, vCenter);
      this.texCoords = Vec2.append(this.texCoords, vtCenter);
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
   public Mesh2 subdivFaceInscribe ( final int faceIdx ) {

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

         /* @formatter:off */
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
            {   vSubdivIdx,   vtSubdivIdx },
            {     vNextIdx,     vtNextIdx },
            { vsOldLen + k, vtsOldLen + k } };
         /* @formatter:on */

         centerFace[j][0] = vSubdivIdx;
         centerFace[j][1] = vtSubdivIdx;
      }

      this.coords = Vec2.concat(this.coords, vsNew);
      this.texCoords = Vec2.concat(this.texCoords, vtsNew);
      this.faces = Mesh.splice(this.faces, i, 1, fsNew);

      return this;
   }

   /**
    * Subdivides all faces in the mesh once.
    *
    * @return this mesh
    */
   public Mesh2 subdivFaces ( ) { return this.subdivFaces(1); }

   /**
    * Subdivides all faces in the mesh by a number of iterations.
    *
    * @param itr iterations
    *
    * @return this mesh
    */
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
    *
    * @see Mesh2#subdivFaceCenter(int)
    */
   public Mesh2 subdivFacesCenter ( final int itr ) {

      for ( int i = 0; i < itr; ++i ) {
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
    *
    * @see Mesh2#subdivFaceFan(int)
    */
   public Mesh2 subdivFacesFan ( final int itr ) {

      for ( int i = 0; i < itr; ++i ) {
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
    *
    * @see Mesh2#subdivFaceInscribe(int)
    */
   public Mesh2 subdivFacesInscribe ( final int itr ) {

      for ( int i = 0; i < itr; ++i ) {
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
       * Append comment listing the number of coordinates, texture coordinates
       * and faces.
       */
      objs.append("# v: ");
      objs.append(coordsLen);
      objs.append(", vt: ");
      objs.append(texCoordsLen);
      objs.append(", vn: 1, f: ");
      objs.append(facesLen);
      objs.append('\n');
      objs.append('\n');

      /* Append name. */
      objs.append('o');
      objs.append(' ');
      objs.append(this.name);
      objs.append('\n');
      objs.append('\n');

      /* Append coordinates. */
      for ( int i = 0; i < coordsLen; ++i ) {
         objs.append('v');
         objs.append(' ');
         objs.append(this.coords[i].toObjString());
         objs.append(" 0.0 \n");
      }
      objs.append('\n');

      /* Append a texture coordinates. */
      for ( int i = 0; i < texCoordsLen; ++i ) {
         objs.append("vt ");
         objs.append(this.texCoords[i].toObjString());
         objs.append('\n');
      }

      /* Append a single normal. */
      objs.append("\nvn 0.0 0.0 1.0\n\n");

      /* Append face indices. */
      for ( int i = 0; i < facesLen; ++i ) {

         final int[][] face = this.faces[i];
         final int vLen = face.length;
         objs.append('f');
         objs.append(' ');

         for ( int j = 0; j < vLen; ++j ) {

            /* Indices in an .obj file start at 1, not 0. */
            final int[] vert = face[j];
            objs.append(vert[0] + 1);
            objs.append('/');
            objs.append(vert[1] + 1);
            objs.append('/');
            objs.append('1');
            objs.append(' ');
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
    * @param places the number of places
    * @param trunc  truncate elements in a list
    *
    * @return the string
    */
   public String toString ( final int places, final int trunc ) {

      final StringBuilder sb = new StringBuilder(2048);
      sb.append("{ name: \"");
      sb.append(this.name);
      sb.append("\", materialIndex: ");
      sb.append(this.materialIndex);
      sb.append(", coords: [ ");

      if ( this.coords != null ) {
         final int len = this.coords.length <= trunc ? this.coords.length
            : trunc;
         final int last = len - 1;
         for ( int i = 0; i < len; ++i ) {
            sb.append(this.coords[i].toString(places));
            if ( i < last ) { sb.append(',').append(' '); }
         }

         if ( this.coords.length > trunc ) { sb.append(" /* ... */"); }
      }

      sb.append(" ], ");
      sb.append("texCoords: [ ");
      if ( this.texCoords != null ) {
         final int len = this.texCoords.length <= trunc ? this.texCoords.length
            : trunc;
         final int last = len - 1;
         for ( int i = 0; i < len; ++i ) {
            sb.append(this.texCoords[i].toString(places));
            if ( i < last ) { sb.append(',').append(' '); }
         }

         if ( this.texCoords.length > trunc ) { sb.append(" /* ... */"); }
      }

      sb.append(" ], ");
      sb.append("faces: [ ");
      if ( this.faces != null ) {
         final int facesLen = this.faces.length <= trunc ? this.faces.length
            : trunc;
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
            if ( i < facesLast ) { sb.append(',').append(' '); }
         }

         if ( this.faces.length > trunc ) { sb.append(" /* ... */"); }
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
   public String toSvgElm ( final String id, final float zoom ) {

      final StringBuilder svgp = new StringBuilder(1024);
      svgp.append(MaterialSolid.defaultSvgMaterial(zoom));
      svgp.append(this.toSvgPath(id));
      svgp.append("</g>\n");
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

         svgp.append("<path id=\"");
         svgp.append(iddot + Utils.toPadded(i, 3));
         svgp.append("\" d=\"M ");
         svgp.append(vs[f[0][0]].toSvgString());
         svgp.append(' ');

         for ( int j = 1; j < fLen; ++j ) {
            svgp.append('L');
            svgp.append(' ');
            svgp.append(vs[f[j][0]].toSvgString());
            svgp.append(' ');
         }

         svgp.append("Z\"></path>\n");
      }

      return svgp.toString();
   }

   /**
    * Transforms all coordinates in the mesh by a matrix.
    *
    * @param m the matrix
    *
    * @return this mesh
    *
    * @see Mat3#mulPoint(Mat3, Vec2, Vec2)
    */
   public Mesh2 transform ( final Mat3 m ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.coords[i];
         Mat3.mulPoint(m, c, c);
      }

      return this;
   }

   /**
    * Transforms all coordinates in the mesh <em>permanently</em> by a
    * transform.<br>
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
    */
   public Mesh2 transform ( final Transform2 tr ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.coords[i];
         Transform2.mulPoint(tr, c, c);
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
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   public Mesh2 translate ( final Vec2 v ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.coords[i];
         Vec2.add(c, v, c);
      }

      return this;
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes, i.e., to
    * compare how mesh geometry looks in Blender (the control) versus in the
    * library (the test).
    *
    * @param includeUvs whether or not to include UVs
    * @param z          z offset
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode ( final boolean includeUvs, final float z ) {

      final StringBuilder pyCd = new StringBuilder(1024);
      pyCd.append("{\"name\": \"");
      pyCd.append(this.name);
      pyCd.append("\", \"material_index\": ");
      pyCd.append(this.materialIndex);
      pyCd.append(", \"vertices\": [");

      final int vlen = this.coords.length;
      final int vlast = vlen - 1;
      for ( int i = 0; i < vlen; ++i ) {
         pyCd.append(this.coords[i].toBlenderCode(z));
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
    * Subdivides an edge by the number of cuts given. For example, one cut
    * will divide an edge in half; two cuts, into thirds.<br>
    * <br>
    * Does not distinguish between interior edges, which have a complement
    * elsewhere, and border edges; for that reason this works best with
    * NGONs.<br>
    * <br>
    * This is protected because it tends to make faces harder to triangulate.
    *
    * @param faceIndex the face index
    * @param edgeIndex the edge index
    * @param cuts      number of cuts
    *
    * @return this mesh
    */
   @Experimental
   protected Mesh2 subdivEdge ( final int faceIndex, final int edgeIndex,
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
      final Vec2 vOrigin = this.coords[vert0Idx[0]];
      final Vec2 vtOrigin = this.texCoords[vert0Idx[1]];

      /* Find edge destination vertex. */
      final int j1 = ( j0 + 1 ) % faceLen;
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

         v.set(u * vOrigin.x + step * vDest.x, u * vOrigin.y + step * vDest.y);
         vt.set(u * vtOrigin.x + step * vtDest.x, u * vtOrigin.y + step
            * vtDest.y);

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
   public static Mesh2 arc ( final float startAngle, final float stopAngle,
      final float oculus, final int sectors, final Mesh2 target ) {

      return Mesh2.arc(startAngle, stopAngle, oculus, sectors,
         Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates an arc from a start and stop angle. The granularity of the
    * approximation is dictated by the number of sectors in a complete circle.
    * The thickness of the arc is described by the annulus. Useful where
    * sectors may be faster than the Bezier curves of
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
   public static Mesh2 arc ( final float startAngle, final float stopAngle,
      final float oculus, final int sectors, final PolyType poly,
      final Mesh2 target ) {

      target.name = "Arc";

      final double a1 = Utils.mod1(startAngle * IUtils.ONE_TAU_D);
      final double b1 = Utils.mod1(stopAngle * IUtils.ONE_TAU_D);
      final double arcLen1 = Utils.mod1(b1 - a1);
      if ( arcLen1 < 0.00139d ) {
         return Mesh2.ring(oculus, sectors, poly, target);
      }

      final int sctCount = Utils.ceilToInt(1 + ( sectors < 3 ? 3 : sectors )
         * ( float ) arcLen1);
      final int sctCount2 = sctCount + sctCount;
      final Vec2[] vs = target.coords = Vec2.resize(target.coords, sctCount2);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
         sctCount2);

      final float annul = Utils.clamp(oculus, IUtils.DEFAULT_EPSILON, 1.0f
         - IUtils.DEFAULT_EPSILON);
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
         v0.set(( float ) ( 0.5d * cosa ), ( float ) ( 0.5d * sina ));

         final Vec2 v1 = vs[j];
         v1.set(( float ) ( annRad * cosa ), ( float ) ( annRad * sina ));

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

            len = sctCount2;
            target.faces = new int[1][len][2];

            for ( int i = 0, j = 0; i < sctCount; ++i, j += 2 ) {
               final int[] forward = target.faces[0][i];
               forward[0] = j;
               forward[1] = j;
            }

            for ( int i = sctCount, j = len - 1; i < len; ++i, j -= 2 ) {
               final int[] backward = target.faces[0][i];
               backward[0] = j;
               backward[1] = j;
            }

            break;

         case QUAD:

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
   public static Mesh2 arc ( final float startAngle, final float stopAngle,
      final float oculus, final Mesh2 target ) {

      return Mesh2.arc(startAngle, stopAngle, oculus,
         IMesh.DEFAULT_CIRCLE_SECTORS, Mesh2.DEFAULT_POLY_TYPE, target);
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
   public static Mesh2 arc ( final float startAngle, final float stopAngle,
      final int sectors, final Mesh2 target ) {

      return Mesh2.arc(startAngle, stopAngle, IMesh.DEFAULT_OCULUS, sectors,
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
   public static Mesh2 arc ( final float startAngle, final float stopAngle,
      final Mesh2 target ) {

      return Mesh2.arc(startAngle, stopAngle, IMesh.DEFAULT_OCULUS,
         IMesh.DEFAULT_CIRCLE_SECTORS, Mesh2.DEFAULT_POLY_TYPE, target);
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
   public static Mesh2 arc ( final float stopAngle, final Mesh2 target ) {

      return Mesh2.arc(0.0f, stopAngle, IMesh.DEFAULT_OCULUS,
         IMesh.DEFAULT_CIRCLE_SECTORS, Mesh2.DEFAULT_POLY_TYPE, target);
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
   public static Vec2 calcDimensions ( final Mesh2 mesh, final Vec2 target,
      final Vec2 lb, final Vec2 ub ) {

      lb.set(Float.MAX_VALUE, Float.MAX_VALUE);
      ub.set(Float.MIN_VALUE, Float.MIN_VALUE);

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
   public static Mesh2 circle ( final Mesh2 target ) {

      return Mesh2.polygon(IMesh.DEFAULT_CIRCLE_SECTORS,
         Mesh2.DEFAULT_POLY_TYPE, target);
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
   public static Mesh2 circle ( final Mesh2.PolyType poly,
      final Mesh2 target ) {

      return Mesh2.polygon(IMesh.DEFAULT_CIRCLE_SECTORS, poly, target);
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
   public static boolean contains ( final Mesh2 mesh, final Vec2 point ) {

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
    * Leaves the source mesh unaltered. New meshes are created through
    * visitation of each face in the source, so they may contain redundant
    * data to be removed with {@link Mesh2#clean()}.
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
    * Groups separate meshes with the same material index into a mesh. Returns
    * a new array of meshes. Do not use if source meshes need to be
    * transformed independently.
    *
    * @param meshes the array of meshes
    *
    * @return the consolidated meshes
    */
   public static final Mesh2[] groupByMaterial ( final Mesh2[] meshes ) {

      final HashMap < Integer, Mesh2 > dict = new HashMap <>();
      Mesh2 current;

      final int srcLen = meshes.length;
      for ( int i = 0; i < srcLen; ++i ) {
         final Mesh2 source = meshes[i];
         final int matIdxPrm = source.materialIndex;
         final Integer matIdxObj = matIdxPrm;

         /* Create a new mesh if it is not already present in dictionary. */
         current = dict.get(matIdxObj);
         if ( current == null ) {
            current = new Mesh2(new int[0][0][2], new Vec2[0], new Vec2[0]);
            current.materialIndex = matIdxPrm;
            current.name = "Mesh." + Utils.toPadded(matIdxPrm, 3);
            dict.put(matIdxObj, current);
         }

         /* Copy source coordinates. */
         final Vec2[] vsSrc = source.coords;
         final int vsLen = vsSrc.length;
         final Vec2[] vsCopy = new Vec2[vsLen];
         for ( int j = 0; j < vsLen; ++j ) {
            vsCopy[j] = new Vec2(vsSrc[j]);
         }

         /* Copy source texture coordinates. */
         final Vec2[] vtsSrc = source.texCoords;
         final int vtsLen = vtsSrc.length;
         final Vec2[] vtsCopy = new Vec2[vtsLen];
         for ( int j = 0; j < vtsLen; ++j ) {
            vtsCopy[j] = new Vec2(vtsSrc[j]);
         }

         /* Concatenated indices need to be offset by current data lengths. */
         final int vsTrgLen = current.coords.length;
         final int vtsTrgLen = current.texCoords.length;

         /* Copy source face indices. */
         final int[][][] fsSrc = source.faces;
         final int fsLen = fsSrc.length;
         final int[][][] fsCopy = new int[fsLen][][];
         for ( int j = 0; j < fsLen; ++j ) {
            final int[][] fSrc = fsSrc[j];
            final int fLen = fSrc.length;
            final int[][] fSrcCopy = fsCopy[j] = new int[fLen][2];
            for ( int k = 0; k < fLen; ++k ) {
               fSrcCopy[k][0] = vsTrgLen + fSrc[k][0];
               fSrcCopy[k][1] = vtsTrgLen + fSrc[k][1];
            }
         }

         /* Concatenate copies with current data. */
         current.coords = Vec2.concat(current.coords, vsCopy);
         current.texCoords = Vec2.concat(current.texCoords, vtsCopy);
         current.faces = Mesh.splice(current.faces, current.faces.length, 0,
            fsCopy);
      }

      /* Convert dictionary values to an array; clean meshes of excess data. */
      final Mesh2[] result = dict.values().toArray(new Mesh2[dict.size()]);
      final int trgLen = result.length;
      for ( int i = 0; i < trgLen; ++i ) {
         result[i].clean();
      }
      return result;
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
   public static final Mesh2 plane ( final int cols, final int rows,
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
   public static final Mesh2 plane ( final int cols, final int rows,
      final PolyType poly, final Mesh2 target ) {

      target.name = "Plane";

      final int rval = rows < 1 ? 1 : rows;
      final int cval = cols < 1 ? 1 : cols;

      final int rval1 = rval + 1;
      final int cval1 = cval + 1;

      final float iToStep = 1.0f / rval;
      final float jToStep = 1.0f / cval;

      final Vec2[] vs = target.coords = Vec2.resize(target.coords, rval1
         * cval1);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
         vs.length);
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

         case QUAD:

            target.faces = new int[flen][4][2];

            for ( int k = 0, i = 0; i < rval; ++i ) {
               final int noff0 = i * cval1;
               final int noff1 = noff0 + cval1;

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
               final int noff1 = noff0 + cval1;

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
   public static final Mesh2 plane ( final int div, final Mesh2 target ) {

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
   public static Mesh2 polygon ( final int sectors, final Mesh2 target ) {

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
   public static Mesh2 polygon ( final int sectors, final PolyType poly,
      final Mesh2 target ) {

      /*
       * Polar coordinates need to be more precise, given that they are scaled
       * up and can impact SVG rendering.
       */

      target.name = "Polygon";

      final int seg = sectors < 3 ? 3 : sectors;
      final int newLen = poly == PolyType.NGON ? seg : poly == PolyType.QUAD
         ? seg + seg + 1 : seg + 1;
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
               v.set(( float ) ( 0.5d * Math.cos(theta) ), ( float ) ( 0.5d
                  * Math.sin(theta) ));

               vts[i].set(v.x + 0.5f, 0.5f - v.y);

               ngon[i][0] = i;
               ngon[i][1] = i;
            }

            break;

         case QUAD:

            final int[][][] quadfs = target.faces = new int[seg][4][2];
            vs[0].set(0.0f, 0.0f);
            vts[0].set(0.5f, 0.5f);

            /* Find corners. */
            for ( int i = 0, j = 1; i < seg; ++i, j += 2 ) {
               final double theta = i * toTheta;
               final Vec2 vCorner = vs[j];
               vCorner.set(( float ) ( 0.5d * Math.cos(theta) ),
                  ( float ) ( 0.5d * Math.sin(theta) ));
               vts[j].set(vCorner.x + 0.5f, 0.5f - vCorner.y);
            }

            /* Find midpoints. */
            final int last = newLen - 1;
            for ( int i = 0, j = 1, k = 2; i < seg; ++i, j += 2, k += 2 ) {
               final int m = ( j + 2 ) % last;

               /* Average the previous and next to get the mid point. */
               final Vec2 vPrev = vs[j];
               final Vec2 vNext = vs[m];
               vs[k].set( ( vPrev.x + vNext.x ) * 0.5f, ( vPrev.y + vNext.y )
                  * 0.5f);

               final Vec2 vtPrev = vts[j];
               final Vec2 vtNext = vts[m];
               vts[k].set( ( vtPrev.x + vtNext.x ) * 0.5f, ( vtPrev.y
                  + vtNext.y ) * 0.5f);
            }

            /* Find faces. */
            for ( int i = 0, j = 0; i < seg; ++i, j += 2 ) {
               final int s = 1 + Utils.mod(j - 1, last);
               final int t = 1 + j % last;
               final int u = 1 + ( j + 1 ) % last;

               final int[][] f = quadfs[i];

               /* Should default to zero. */
               // f[0][0] = 0;
               // f[0][1] = 0;

               f[1][0] = s;
               f[1][1] = s;

               f[2][0] = t;
               f[2][1] = t;

               f[3][0] = u;
               f[3][1] = u;
            }

            break;

         case TRI:

         default:

            final int[][][] trifs = target.faces = new int[seg][3][2];
            vs[0].set(0.0f, 0.0f);
            vts[0].set(0.5f, 0.5f);

            for ( int i = 0, j = 1; i < seg; ++i, ++j ) {
               final double theta = i * toTheta;

               final Vec2 v = vs[j];
               v.set(( float ) ( 0.5d * Math.cos(theta) ), ( float ) ( 0.5d
                  * Math.sin(theta) ));

               vts[j].set(v.x + 0.5f, 0.5f - v.y);

               final int k = 1 + j % seg;
               final int[][] f = trifs[i];

               /* Should default to zero. */
               // f[0][0] = 0;
               // f[0][1] = 0;

               f[1][0] = j;
               f[1][1] = j;

               f[2][0] = k;
               f[2][1] = k;
            }

      }

      return target;
   }

   /**
    * Returns a collection of mesh vertices organized by their proximity to a
    * point. The proximity is expressed as a factor where the nearest vertex
    * is on the nearBound; the farthest is on the farBound.
    *
    * @param m the mesh
    * @param p the point
    *
    * @return the collection
    */
   @Experimental
   public static TreeMap < Float, Vert2 > proximity ( final Mesh2 m,
      final Vec2 p ) {

      return Mesh2.proximity(m, p, 1.0f, 0.0f);
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
    * @see Mesh2#getVertices()
    * @see Vec2#distSq(Vec2, Vec2)
    */
   public static TreeMap < Float, Vert2 > proximity ( final Mesh2 m,
      final Vec2 p, final float nearBound, final float farBound ) {

      final Vert2[] verts = m.getVertices();
      final int vertLen = verts.length;
      final float[] dists = new float[vertLen];
      float minDist = Float.MAX_VALUE;
      float maxDist = Float.MIN_VALUE;
      for ( int i = 0; i < vertLen; ++i ) {
         final float distSq = Vec2.distSq(verts[i].coord, p);
         dists[i] = distSq;
         if ( distSq < minDist ) { minDist = distSq; }
         if ( distSq > maxDist ) { maxDist = distSq; }
      }

      /*
       * The span of the origin range and destination range are already known,
       * so calculate portions of the map(distance, minDist, maxDist, nearBound,
       * farBound) function outside of the for loop.
       */
      final float spanOrigin = maxDist - minDist;
      final float scalar = spanOrigin != 0.0f ? ( farBound - nearBound )
         / spanOrigin : 0.0f;
      final TreeMap < Float, Vert2 > result = new TreeMap <>();
      for ( int j = 0; j < vertLen; ++j ) {
         final float fac = nearBound + scalar * ( dists[j] - minDist );
         result.put(fac, verts[j]);
      }

      return result;
   }

   /**
    * Creates a regular convex polygon with an opening in its center. The
    * oculus describes the relative size of this opening.
    *
    * @param sectors the number of sides
    * @param oculus  the size of the opening
    * @param target  the output type
    *
    * @return the ring
    */
   public static final Mesh2 ring ( final float oculus, final int sectors,
      final Mesh2 target ) {

      return Mesh2.ring(oculus, sectors, Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a regular convex polygon with an opening in its center. The
    * oculus describes the relative size of this opening. When the polygon
    * type is {@link Mesh.PolyType#QUAD}, the ring will be composed of
    * quadrilaterals; otherwise, triangles.
    *
    * @param oculus  the size of the opening
    * @param sectors the number of sides
    * @param poly    the polygon type
    * @param target  the output type
    *
    * @return the ring
    */
   public static final Mesh2 ring ( final float oculus, final int sectors,
      final PolyType poly, final Mesh2 target ) {

      target.name = "Ring";

      final boolean isQuad = poly == PolyType.QUAD || poly == PolyType.NGON;
      final int seg = sectors < 3 ? 3 : sectors;
      final int seg2 = seg + seg;
      final float ocul = Utils.clamp(oculus, IUtils.DEFAULT_EPSILON, 1.0f
         - IUtils.DEFAULT_EPSILON);

      final double toTheta = IUtils.TAU_D / seg;
      final double oculRad = ocul * 0.5d;

      final Vec2[] vs = target.coords = Vec2.resize(target.coords, seg2);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords, seg2);
      target.faces = isQuad ? new int[seg][4][2] : new int[seg2][3][2];

      for ( int k = 0, i = 0, j = 1; k < seg; ++k, i += 2, j += 2 ) {
         final double theta = k * toTheta;
         final double cosa = Math.cos(theta);
         final double sina = Math.sin(theta);

         final Vec2 v0 = vs[i];
         v0.set(( float ) ( 0.5d * cosa ), ( float ) ( 0.5d * sina ));

         final Vec2 v1 = vs[j];
         v1.set(( float ) ( oculRad * cosa ), ( float ) ( oculRad * sina ));

         final Vec2 vt0 = vts[i];
         vt0.x = v0.x + 0.5f;
         vt0.y = 0.5f - v0.y;

         final Vec2 vt1 = vts[j];
         vt1.x = v1.x + 0.5f;
         vt1.y = 0.5f - v1.y;

         final int m = ( i + 2 ) % seg2;
         final int n = ( j + 2 ) % seg2;

         if ( isQuad ) {

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
   public static final Mesh2 ring ( final float oculus, final Mesh2 target ) {

      return Mesh2.ring(oculus, IMesh.DEFAULT_CIRCLE_SECTORS,
         Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a regular convex polygon with an opening in its center.
    *
    * @param sectors the number of sides
    * @param target  the output mesh
    *
    * @return the ring
    */
   public static final Mesh2 ring ( final int sectors, final Mesh2 target ) {

      return Mesh2.ring(IMesh.DEFAULT_OCULUS, sectors, Mesh2.DEFAULT_POLY_TYPE,
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

      return Mesh2.ring(IMesh.DEFAULT_OCULUS, IMesh.DEFAULT_CIRCLE_SECTORS,
         Mesh2.DEFAULT_POLY_TYPE, target);
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
   public static final Mesh2 square ( final PolyType poly,
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

      /* @formatter:off */
      switch ( poly ) {
         case NGON:
         case QUAD:
            target.faces = new int[][][] {
               { { 0, 0 }, { 1, 1 }, { 2, 2 }, { 3, 3 } } };
            break;
         case TRI:
         default:
            target.faces = new int[][][] {
               { { 0, 0 }, { 1, 1 }, { 2, 2 } },
               { { 0, 0 }, { 2, 2 }, { 3, 3 } } };
      }
      /* @formatter:on */

      return target;
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
   public static Mesh2 tracePerimeter ( final Mesh2 source, final int count,
      final float offset, final Mesh2 target ) {

      target.name = "Trace";

      final int vcount = count < 3 ? 3 : count;

      final Vec2[] vsSrc = source.coords;
      final Vec2[] vtsSrc = source.texCoords;
      final int[][][] fsSrc = source.faces;
      final int fsSrcLen = fsSrc.length;
      final int trgLen = fsSrcLen * vcount;

      final Vec2[] vsTrg = target.coords = Vec2.resize(target.coords, trgLen);
      final Vec2[] vtsTrg = target.texCoords = Vec2.resize(target.texCoords,
         trgLen);
      final int[][][] fsTrg = target.faces = new int[fsSrcLen][vcount][2];

      final float toStep = 1.0f / vcount;

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

            final Vec2 vaSrc = vsSrc[a[0]];
            final Vec2 vbSrc = vsSrc[b[0]];

            final Vec2 vtaSrc = vtsSrc[a[1]];
            final Vec2 vtbSrc = vtsSrc[b[1]];

            vsTrg[k].set(u * vaSrc.x + t * vbSrc.x, u * vaSrc.y + t * vbSrc.y);
            vtsTrg[k].set(u * vtaSrc.x + t * vtbSrc.x, u * vtaSrc.y + t
               * vtbSrc.y);

            fTrg[j][0] = k;
            fTrg[j][1] = k;
         }
      }

      return target;
   }

   /**
    * Creates a triangle.
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
    * coordinate arrays are of equal length and face indices are easier to
    * read and understand. Useful for making a mesh similar to those in Unity
    * or p5. Similar to 'ripping' vertices or 'tearing' edges in Blender.
    *
    * @param source the source mesh
    * @param target the target mesh
    *
    * @return the mesh
    */
   public static Mesh2 uniformData ( final Mesh2 source, final Mesh2 target ) {

      /* Find length of uniform data. */
      int uniformLen = 0;
      final int[][][] fsSrc = source.faces;
      final int fsSrcLen = fsSrc.length;
      for ( int i = 0; i < fsSrcLen; ++i ) {
         uniformLen += fsSrc[i].length;
      }

      /* Allocate new arrays. */
      final int[][][] fsTrg = new int[fsSrcLen][][];
      final boolean same = source == target;
      final Vec2[] vsTrg = same ? new Vec2[uniformLen] : Vec2.resize(
         target.coords, uniformLen);
      final Vec2[] vtsTrg = same ? new Vec2[uniformLen] : Vec2.resize(
         target.texCoords, uniformLen);

      /* Account for scenario where source and target are same. */
      if ( same ) {
         for ( int i = 0; i < uniformLen; ++i ) {
            vsTrg[i] = new Vec2();
            vtsTrg[i] = new Vec2();
         }
      }

      /* Cache shortcuts to old arrays. */
      final Vec2[] vsSrc = source.coords;
      final Vec2[] vtsSrc = source.texCoords;

      /* Reassign. */
      for ( int k = 0, i = 0; i < fsSrcLen; ++i ) {
         final int[][] fSrc = fsSrc[i];
         final int fLen = fSrc.length;
         final int[][] fTrg = fsTrg[i] = new int[fLen][2];
         for ( int j = 0; j < fLen; ++j, ++k ) {
            final int[] vertSrc = fSrc[j];
            final int[] vertTrg = fTrg[j];

            vsTrg[k].set(vsSrc[vertSrc[0]]);
            vtsTrg[k].set(vtsSrc[vertSrc[1]]);

            /* Update face indices. */
            vertTrg[0] = k;
            vertTrg[1] = k;
         }
      }

      /* Update references. */
      target.coords = vsTrg;
      target.texCoords = vtsTrg;
      target.faces = fsTrg;

      return target;
   }

   /**
    * An iterator, which allows a mesh's faces to be accessed in an enhanced
    * for loop.
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
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * Compares two face indices (an array of vertex indices) by averaging the
    * vectors referenced by them, then comparing the averages.
    */
   protected static final class SortIndices2 implements Comparator < int[][] > {

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

}
