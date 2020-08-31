package camzup.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

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
    * An array of normals to indicate how light will bounce off the mesh's
    * surface.
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
   public Mesh3 ( ) { super(); }

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

      super();
      this.set(faces, coords, texCoords, normals);
   }

   /**
    * Constructs a copy of the source mesh.
    *
    * @param source the source mesh
    */
   public Mesh3 ( final Mesh2 source ) {

      super();
      this.set(source);
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

      /* Dictionary's keys are no longer needed; just values. */
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
      return this.equals(( Mesh3 ) obj);
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
   public Mesh3 extrudeEdge ( final int faceIdx, final int edgeIdx,
      final float amt ) {

      // TEST Test UVs.

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
      final int idxVt0 = idxOrigin[1];
      final int idxVn0 = idxOrigin[2];

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

      final Vec3 vOrigin = this.coords[idxV0];
      final Vec2 vtOrigin = this.texCoords[idxVt0];
      final Vec3 vnOrigin = this.normals[idxVn0];

      final Vec3 vDest = this.coords[idxV3];
      final Vec2 vtDest = this.texCoords[idxVt3];
      final Vec3 vnDest = this.normals[idxVn3];

      final Vec3 vnDiff = Vec3.addNorm(vnDest, vnOrigin, new Vec3());

      final Vec3 vDiff = new Vec3();
      Vec3.sub(vDest, vOrigin, vDiff);
      final float edgeMag = Vec3.mag(vDiff);

      final Vec3 extrude = new Vec3();
      Vec3.crossNorm(vDiff, vnDiff, extrude);
      Vec3.mul(extrude, amt * edgeMag, extrude);

      final Vec3 vNewOrigin = new Vec3();
      final Vec3 vNewDest = new Vec3();
      Vec3.add(vOrigin, extrude, vNewOrigin);
      Vec3.add(vDest, extrude, vNewDest);

      final Vec2 vtPerp = new Vec2();
      Vec2.sub(vtOrigin, vtDest, vtPerp);
      Vec2.perpendicularCCW(vtPerp, vtPerp);
      // Vec2.normalize(vtPerp, vtPerp);

      final Vec2 vtNewOrigin = new Vec2();
      final Vec2 vtNewDest = new Vec2();
      Vec2.add(vtOrigin, vtPerp, vtNewOrigin);
      Vec2.add(vtDest, vtPerp, vtNewDest);

      final int[][][] faceNew = { { { idxV1, idxVt1, idxVn1 }, { idxV2, idxVt2,
         idxVn1 }, { idxV3, idxVt3, idxVn1 }, { idxV0, idxVt0, idxVn1 } } };

      this.coords = Vec3.concat(this.coords, new Vec3[] { vNewOrigin,
         vNewDest });
      this.texCoords = Vec2.concat(this.texCoords, new Vec2[] { vtNewOrigin,
         vtNewDest });
      this.normals = Vec3.append(this.normals, vnDiff);
      this.faces = Mesh.splice(this.faces, i + 1, 0, faceNew);

      return this;
   }

   /**
    * Extrudes a face by an amount. Creates quadrilateral sides to connect
    * extruded face to original. Does not check whether a face is bordered by
    * other faces; best used on disconnected faces.
    *
    * @param faceIdx the face index
    * @param fillCap whether to cap the extruded face
    * @param depth   the extrusion depth
    *
    * @return this new face indices
    */
   public Mesh3 extrudeFace ( final int faceIdx, final boolean fillCap,
      final float depth ) {

      return this.extrudeFace(faceIdx, fillCap, depth, 1.0f);
   }

   /**
    * Extrudes a face by an amount. Creates quadrilateral sides to connect
    * extruded face to original. Does not check whether a face is bordered by
    * other faces; best used on disconnected faces.
    *
    * @param faceIdx the face index
    * @param fillCap whether to cap the extruded face
    * @param depth   the extrusion depth
    * @param taper   the taper
    *
    * @return this new face indices
    */
   @Experimental
   public Mesh3 extrudeFace ( final int faceIdx, final boolean fillCap,
      final float depth, final float taper ) {

      if ( depth == 0.0f ) { return this; }
      final float vtap = Utils.max(IUtils.EPSILON, taper);

      /* Validate face index, find face. */
      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIdx, facesLen);
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

      /*
       * Cache old length of coordinates and texture coordinates so new ones can
       * be appended to the end.
       */
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

         /* Taper and extrude the vertex coordinate. */
         Vec3.sub(vBase, center, vExtruded);
         Vec3.mul(vExtruded, vtap, vExtruded);
         Vec3.add(vExtruded, extrusion, vExtruded);
         Vec3.add(vExtruded, center, vExtruded);

         /* Update extruded vertex indices. */
         final int[] extrudedVert = extrudedFace[j];
         extrudedVert[0] = vsOldLen + j;
         extrudedVert[1] = vertCurr[1];
         extrudedVert[2] = vnsOldLen;

         /* Create face for side. */
         final int[][] sideFace = fsNew[j];

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

      /* Update indices. */
      this.faces = Mesh.splice(this.faces, i, fillCap ? 0 : 1, fsNew);

      /* Shade normals of four sides. */
      final int compoundLen = facesLen + faceLen;
      for ( int j = i; j < compoundLen; ++j ) { this.shadeFlat(j); }

      return this;
   }

   /**
    * Extrudes all faces in the mesh by an amount. Creates quadrilateral sides
    * to connect extruded face to original. Does not check as to whether a
    * face is an bordered by other faces; best used on disconnected faces.
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
    * face is an bordered by other faces; best used on disconnected faces.
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
    * Gets an edge from the mesh.
    *
    * @param i      the face index
    * @param j      the vertex index
    * @param target the output edge
    *
    * @return the edge
    */
   public Edge3 getEdge ( final int i, final int j, final Edge3 target ) {

      final int[][] f0 = this.faces[Utils.mod(i, this.faces.length)];
      final int f0len = f0.length;
      final int k = Utils.mod(j, f0len);
      final int[] f1 = f0[k];
      final int[] f2 = f0[ ( k + 1 ) % f0len];

      return target.set(this.coords[f1[0]], this.texCoords[f1[1]],
         this.normals[f1[2]], this.coords[f2[0]], this.texCoords[f2[1]],
         this.normals[f2[2]]);
   }

   /**
    * Gets an array of edges from the mesh.
    *
    * @return the edges array
    */
   public Edge3[] getEdges ( ) {

      Edge3 trial = new Edge3();
      final int fsLen = this.faces.length;
      final ArrayList < Edge3 > result = new ArrayList <>(fsLen * 4);

      for ( int i = 0; i < fsLen; ++i ) {

         final int[][] f = this.faces[i];
         final int fLen = f.length;

         for ( int j = 0; j < fLen; ++j ) {

            final int[] fOrigin = f[j];
            final int[] fDest = f[ ( j + 1 ) % fLen];

            trial.set(this.coords[fOrigin[0]], this.texCoords[fOrigin[1]],
               this.normals[fOrigin[2]], this.coords[fDest[0]],
               this.texCoords[fDest[1]], this.normals[fDest[2]]);

            if ( result.indexOf(trial) < 0 ) {
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
    *
    * @return the face
    */
   public Face3 getFace ( final int i, final Face3 target ) {

      final int[][] face = this.faces[Utils.mod(i, this.faces.length)];
      final int len = face.length;
      final Vert3[] vertices = new Vert3[len];

      for ( int j = 0; j < len; ++j ) {
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

      final int len0 = this.faces.length;
      final Face3[] result = new Face3[len0];

      for ( int i = 0; i < len0; ++i ) {

         final int[][] fs0 = this.faces[i];
         final int len1 = fs0.length;
         final Vert3[] verts = new Vert3[len1];

         for ( int j = 0; j < len1; ++j ) {

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
    * @param i      primary index
    * @param j      secondary index
    * @param target the output vertex
    *
    * @return the vertex
    */
   public Vert3 getVertex ( final int i, final int j, final Vert3 target ) {

      final int[][] f0 = this.faces[Utils.mod(i, this.faces.length)];
      final int[] f = f0[Utils.mod(j, f0.length)];

      return target.set(this.coords[f[0]], this.texCoords[f[1]],
         this.normals[f[2]]);
   }

   /**
    * Gets an array of vertices from the mesh.
    *
    * @return the vertices
    */
   public Vert3[] getVertices ( ) {

      Vert3 trial = new Vert3();
      final int len0 = this.faces.length;
      final ArrayList < Vert3 > result = new ArrayList <>(len0);

      for ( int i = 0; i < len0; ++i ) {

         final int[][] fs = this.faces[i];
         final int len1 = fs.length;

         for ( int j = 0; j < len1; ++j ) {

            final int[] f = fs[j];
            trial.set(this.coords[f[0]], this.texCoords[f[1]],
               this.normals[f[2]]);

            if ( result.indexOf(trial) < 0 ) {
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

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL ^ Arrays.hashCode(this.coords);
      hash = hash * IUtils.HASH_MUL ^ Arrays.deepHashCode(this.faces);
      return hash;
   }

   /**
    * A convenience function to inset a face at an index and extrude it.
    *
    * @param faceIdx the face index
    * @param inset   the inset factor
    * @param depth   the extrusion depth
    *
    * @return this mesh
    */
   public Mesh3 insetExtrudeFace ( final int faceIdx, final float inset,
      final float depth ) {

      return this.insetExtrudeFace(faceIdx, inset, depth, 1.0f);
   }

   /**
    * A convenience function to inset a face at an index and extrude it.
    *
    * @param faceIdx the face index
    * @param inset   the inset factor
    * @param depth   the extrusion depth
    * @param taper   the taper
    *
    * @return this mesh
    */
   public Mesh3 insetExtrudeFace ( final int faceIdx, final float inset,
      final float depth, final float taper ) {

      final int i = Utils.mod(faceIdx, this.faces.length);
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
    * @param faceIdx the face index
    *
    * @return the new face indices
    */
   public Mesh3 insetFace ( final int faceIdx ) {

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
   public Mesh3 insetFace ( final int faceIdx, final float fac ) {

      if ( fac <= 0.0f ) { return this; }
      if ( fac >= 1.0f ) { return this.subdivFaceFan(faceIdx); }

      /* Validate face index, find face. */
      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIdx, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      final int vsOldLen = this.coords.length;
      final int vtsOldLen = this.texCoords.length;
      final int vnsOldLen = this.normals.length;

      final int[][][] fsNew = new int[faceLen + 1][4][3];
      final int[][] centerFace = fsNew[faceLen] = new int[faceLen][3];

      final Vec3 vCenter = new Vec3();
      final Vec2 vtCenter = new Vec2();
      final Vec3 vnCenter = new Vec3();

      /* Find center. */
      for ( int j = 0; j < faceLen; ++j ) {
         final int[] vertCurr = face[j];
         final Vec3 vCurr = this.coords[vertCurr[0]];
         final Vec2 vtCurr = this.texCoords[vertCurr[1]];
         final Vec3 vnCurr = this.normals[vertCurr[2]];

         /* Sum centers. */
         Vec3.add(vCenter, vCurr, vCenter);
         Vec2.add(vtCenter, vtCurr, vtCenter);
         Vec3.add(vnCenter, vnCurr, vnCenter);
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
    * @see Mesh3#calcDimensions(Mesh3, Vec3, Vec3, Vec3)
    */
   public Mesh3 reframe ( final Transform3 tr ) {

      tr.locPrev.set(tr.location);
      tr.scalePrev.set(tr.scale);

      final Vec3 dim = tr.scale;
      final Vec3 lb = tr.location;
      final Vec3 ub = new Vec3();
      Mesh3.calcDimensions(this, dim, lb, ub);

      lb.x = 0.5f * ( lb.x + ub.x );
      lb.y = 0.5f * ( lb.y + ub.y );
      lb.z = 0.5f * ( lb.z + ub.z );
      final float scl = Utils.div(1.0f, Utils.max(dim.x, dim.y, dim.z));

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) {
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
    * Flips the indices which specify an edge.
    *
    * @param i face index
    * @param j edge index
    *
    * @return this mesh
    */
   public Mesh3 reverseEdge ( final int i, final int j ) {

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
   public Mesh3 reverseFace ( final int i ) {

      // TODO: Isn't this the same as Mesh#reverse ?
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
    * Rotates all coordinates in the mesh by an angle around an arbitrary
    * axis.
    *
    * @param radians the angle in radians
    * @param axis    the axis of rotation
    *
    * @return this mesh
    *
    * @see Vec3#rotate(Vec3, float, Vec3, Vec3)
    */
   public Mesh3 rotate ( final float radians, final Vec3 axis ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.coords[i];
         Vec3.rotate(c, cosa, sina, axis, c);
      }

      return this;
   }

   /**
    * Rotates all coordinates in the mesh by a quaternion.
    *
    * @param q the quaternion
    *
    * @return the mesh
    */
   public Mesh3 rotate ( final Quaternion q ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.coords[i];
         Quaternion.mulVector(q, c, c);
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
    * @see Vec3#rotateX(Vec3, float, Vec3)
    */
   public Mesh3 rotateX ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.coords[i];
         Vec3.rotateX(c, cosa, sina, c);
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
    * @see Vec3#rotateY(Vec3, float, Vec3)
    */
   public Mesh3 rotateY ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.coords[i];
         Vec3.rotateY(c, cosa, sina, c);
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
    * @see Vec3#rotateZ(Vec3, float, Vec3)
    */
   public Mesh3 rotateZ ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.coords[i];
         Vec3.rotateZ(c, cosa, sina, c);
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
         final int len = this.coords.length;
         for ( int i = 0; i < len; ++i ) {
            final Vec3 c = this.coords[i];
            Vec3.mul(c, scale, c);
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
    * @see Vec3#none(Vec3)
    * @see Vec3#mul(Vec3, Vec3, Vec3)
    */
   public Mesh3 scale ( final Vec3 scale ) {

      if ( Vec3.all(scale) ) {
         final int len = this.coords.length;
         for ( int i = 0; i < len; ++i ) {
            final Vec3 c = this.coords[i];
            Vec3.mul(c, scale, c);
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
      final int vslen = sourcevs.length;
      this.coords = Vec3.resize(this.coords, vslen);
      for ( int i = 0; i < vslen; ++i ) {
         this.coords[i].set(sourcevs[i], 0.0f);
      }

      /* Copy texture coordinates. */
      final Vec2[] sourcevts = source.texCoords;
      final int vtslen = sourcevts.length;
      this.texCoords = Vec2.resize(this.texCoords, vtslen);
      for ( int i = 0; i < vtslen; ++i ) {
         this.texCoords[i].set(sourcevts[i]);
      }

      /* Append one normal. */
      this.normals = new Vec3[] { Vec3.up(new Vec3()) };

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
            final int[] target2 = new int[fslen2 + 1];
            target1[j] = target2;
            System.arraycopy(source2, 0, target2, 0, fslen2);
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
      final Vec3[] sourcevs = source.coords;
      final int vslen = sourcevs.length;
      this.coords = Vec3.resize(this.coords, vslen);
      for ( int i = 0; i < vslen; ++i ) { this.coords[i].set(sourcevs[i]); }

      /* Copy texture coordinates. */
      final Vec2[] sourcevts = source.texCoords;
      final int vtslen = sourcevts.length;
      this.texCoords = Vec2.resize(this.texCoords, vtslen);
      for ( int j = 0; j < vtslen; ++j ) {
         this.texCoords[j].set(sourcevts[j]);
      }

      /* Copy normals. */
      final Vec3[] sourcevns = source.normals;
      final int vnslen = sourcevns.length;
      this.normals = Vec3.resize(this.normals, vnslen);
      for ( int k = 0; k < vnslen; ++k ) { this.normals[k].set(sourcevns[k]); }

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

            System.arraycopy(source2, 0, target2, 0, fslen2);
         }
      }

      this.materialIndex = source.materialIndex;
      this.name = source.name;
      return this;
   }

   /**
    * Calculates the normals of a mesh. Defaults to flat shading.
    *
    * @return this mesh
    */
   public Mesh3 shade ( ) { return this.shadeFlat(); }

   /**
    * Calculates this mesh's normals per face, resulting in flat shading.
    *
    * @return this mesh
    */
   public Mesh3 shadeFlat ( ) {

      final int len = this.faces.length;
      for ( int i = 0; i < len; ++i ) { this.shadeFlat(i); }

      return this;
   }

   /**
    * Calculates a per face normal for a given face, resulting in flat
    * shading.<br>
    * <br>
    * An individual face version of this function is defined because
    * {@link Mesh3#extrudeFace(int, boolean, float, float)} needs it.
    *
    * @param faceIdx the face index
    *
    * @return this mesh
    */
   @Experimental
   public Mesh3 shadeFlat ( final int faceIdx ) {

      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIdx, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      final Vec3 vn = new Vec3();
      final int vnIdx = this.normals.length;

      Vec3 prev = this.coords[face[faceLen - 1][0]];
      for ( int j = 0; j < faceLen; ++j ) {

         final int[] vert = face[j];
         final Vec3 curr = this.coords[vert[0]];
         final Vec3 next = this.coords[face[ ( j + 1 ) % faceLen][0]];

         /*
          * Cf. Eric Lengyel, Foundations of Game Engine Development I.
          * Mathematics, p. 101: ( p1 - p0 ) x ( p2 - p0 ) .
          */
         final float edge0x = prev.x - curr.x;
         final float edge0y = prev.y - curr.y;
         final float edge0z = prev.z - curr.z;

         final float edge1x = curr.x - next.x;
         final float edge1y = curr.y - next.y;
         final float edge1z = curr.z - next.z;

         vn.x += edge0y * edge1z - edge0z * edge1y;
         vn.y += edge0z * edge1x - edge0x * edge1z;
         vn.z += edge0x * edge1y - edge0y * edge1x;

         vert[2] = vnIdx;
         prev = curr;
      }

      Vec3.normalize(vn, vn);
      this.normals = Vec3.append(this.normals, vn);

      return this;
   }

   /**
    * Calculates this mesh's normals per vertex, resulting in smooth shading.
    * If the normals array is null, or if its length is not equal to the
    * length of coordinates, the normals array is reallocated.
    *
    * @return this mesh
    */
   @Experimental
   public Mesh3 shadeSmooth ( ) {

      // TODO: Make individual shade smooth?

      this.normals = Vec3.resize(this.normals, this.coords.length);

      Vec3 prev = null;
      Vec3 curr = null;
      Vec3 next = null;
      Vec3 normal = null;

      final int[][][] fs = this.faces;
      final int fsLen = fs.length;
      for ( int i = 0; i < fsLen; ++i ) {

         final int[][] f = fs[i];
         final int fLen = f.length;
         prev = this.coords[f[fLen - 1][0]];

         for ( int j = 0; j < fLen; ++j ) {

            final int[] vert = f[j];
            final int currIndex = vert[0];
            final int nextIndex = f[ ( j + 1 ) % fLen][0];

            /* Acquire normal and update face index reference to it. */
            normal = this.normals[currIndex];
            vert[2] = currIndex;

            curr = this.coords[currIndex];
            next = this.coords[nextIndex];

            final float edge0x = prev.x - curr.x;
            final float edge0y = prev.y - curr.y;
            final float edge0z = prev.z - curr.z;

            final float edge1x = curr.x - next.x;
            final float edge1y = curr.y - next.y;
            final float edge1z = curr.z - next.z;

            normal.set(edge0y * edge1z - edge0z * edge1y, edge0z * edge1x
               - edge0x * edge1z, edge0x * edge1y - edge0y * edge1x);
            Vec3.normalize(normal, normal);

            prev = curr;
         }
      }

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

      final int coordsLen = this.coords.length;
      final int texCoordsLen = this.texCoords.length;
      final int normalsLen = this.normals.length;
      final int facesLen = this.faces.length;
      final StringBuilder objs = new StringBuilder(2048);

      /*
       * Append a comment listing the number of coordinates, texture
       * coordinates, normals and faces.
       */
      objs.append("# v: ");
      objs.append(coordsLen);
      objs.append(", vt: ");
      objs.append(texCoordsLen);
      objs.append(", vn: ");
      objs.append(normalsLen);
      objs.append(", f: ");
      objs.append(facesLen);
      objs.append('\n');
      objs.append('\n');

      /* Append name. */
      objs.append('o');
      objs.append(' ');
      objs.append(this.name);
      objs.append('\n');
      objs.append('\n');

      /* Write coordinates. */
      for ( int i = 0; i < coordsLen; ++i ) {
         objs.append('v');
         objs.append(' ');
         objs.append(this.coords[i].toObjString());
         objs.append('\n');
      }
      objs.append('\n');

      /* Write texture coordinates. */
      for ( int i = 0; i < texCoordsLen; ++i ) {
         objs.append("vt ");
         objs.append(this.texCoords[i].toObjString());
         objs.append('\n');
      }
      objs.append('\n');

      /* Write normals. */
      for ( int i = 0; i < normalsLen; ++i ) {
         objs.append("vn ");
         objs.append(this.normals[i].toObjString());
         objs.append('\n');
      }
      objs.append('\n');

      for ( int i = 0; i < facesLen; ++i ) {

         final int[][] face = this.faces[i];
         final int vLen = face.length;
         objs.append('f');
         objs.append(' ');

         for ( int j = 0; j < vLen; ++j ) {

            /* Indices start at 1, not 0. */
            final int[] vert = face[j];
            objs.append(vert[0] + 1);
            objs.append('/');
            objs.append(vert[1] + 1);
            objs.append('/');
            objs.append(vert[2] + 1);
            objs.append(' ');
         }

         objs.append('\n');
      }

      return objs.toString();
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
    * @see Mesh3#calcDimensions(Mesh3, Vec3, Vec3, Vec3)
    * @see Mesh3#translate(Vec3)
    */
   public Mesh3 toOrigin ( final Transform3 tr ) {

      final Vec3 lb = new Vec3();
      final Vec3 ub = new Vec3();
      Mesh3.calcDimensions(this, new Vec3(), lb, ub);

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
      sb.append("normals: [ ");
      if ( this.normals != null ) {
         final int len = this.normals.length <= trunc ? this.normals.length
            : trunc;
         final int last = len - 1;
         for ( int i = 0; i < len; ++i ) {
            sb.append(this.normals[i].toString(places));
            if ( i < last ) { sb.append(',').append(' '); }
         }

         if ( this.normals.length > trunc ) { sb.append(" /* ... */"); }
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

               /* 3 indices: coordinate, texture coordinate and normal. */
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
    * Renders the mesh as a string representing Unity code. This is brittle,
    * it is used internally to compare results to other graphics engines.
    *
    * @return the string
    */
   @Experimental
   public String toUnityCode ( ) {

      final boolean calcTangents = true;
      final boolean optimize = true;

      final StringBuilder sb = new StringBuilder(4096);
      sb.append("Mesh mesh = new Mesh();\n");

      final StringBuilder vs = new StringBuilder(1024);
      vs.append("Vector3[] vs = { \n");

      final StringBuilder vts = new StringBuilder(1024);
      vts.append("Vector2[] vts = { \n");

      final StringBuilder vns = new StringBuilder(1024);
      vns.append("Vector3[] vns = { \n");

      final StringBuilder tris = new StringBuilder(1024);
      tris.append("int[] tris = { \n");

      final int fsLen = this.faces.length;
      final int fsLast = fsLen - 1;
      boolean triFlag = false;
      for ( int k = 0, i = 0; i < fsLen; ++i ) {
         final int[][] f = this.faces[i];
         final int fLen = f.length;
         if ( fLen != 3 && !triFlag ) {
            System.err.println(
               "Mesh has not been triangulated; it will be malformed in Unity.");
            triFlag = true;
         }
         final int fLast = fLen - 1;
         for ( int j = 0; j < fLen; ++j, ++k ) {
            final int[] vert = f[j];

            final Vec3 v = this.coords[vert[0]];
            final Vec2 vt = this.texCoords[vert[1]];
            final Vec3 vn = this.normals[vert[2]];

            vs.append(v.toUnityCode());
            vts.append(vt.toUnityCode());
            vns.append(vn.toUnityCode());
            tris.append(k);

            if ( j < fLast ) {
               vs.append(',').append(' ').append('\n');
               vts.append(',').append(' ').append('\n');
               vns.append(',').append(' ').append('\n');
               tris.append(',').append(' ');
            }
         }

         if ( i < fsLast ) {
            vs.append(',').append(' ').append('\n').append('\n');
            vts.append(',').append(' ').append('\n').append('\n');
            vns.append(',').append(' ').append('\n').append('\n');
            tris.append(',').append(' ').append('\n');
         }
      }

      vs.append(' ').append('}').append(';');
      vts.append(' ').append('}').append(';');
      vns.append(' ').append('}').append(';');
      tris.append(' ').append('}').append(';');

      sb.append(vs.toString());
      sb.append('\n').append('\n');
      sb.append(vts.toString());
      sb.append('\n').append('\n');
      sb.append(vns.toString());
      sb.append('\n').append('\n');
      sb.append(tris.toString());
      sb.append('\n').append('\n');

      sb.append("mesh.vertices = vs;\n");
      sb.append("mesh.uv = vts;\n");
      sb.append("mesh.normals = vns;\n");
      sb.append("mesh.triangles = tris;\n\n");

      if ( calcTangents ) { sb.append("mesh.RecalculateTangents();\n"); }
      if ( optimize ) { sb.append("mesh.Optimize();\n"); }
      return sb.toString();
   }

   /**
    * Transforms all coordinates in the mesh by a matrix.
    *
    * @param m the matrix
    *
    * @return this mesh
    *
    * @see Mat4#mulPoint(Mat4, Vec3, Vec3)
    */
   public Mesh3 transform ( final Mat4 m ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.coords[i];
         Mat4.mulPoint(m, c, c);
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
   public Mesh3 transform ( final Transform3 tr ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.coords[i];
         Transform3.mulPoint(tr, c, c);
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

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.coords[i];
         Vec3.add(c, v, c);
      }

      return this;
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes.
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode ( ) { return this.toBlenderCode(false, false); }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes, i.e., to
    * compare how mesh geometry looks in Blender (the control) versus in the
    * library (the test).
    *
    * @param includeUvs     whether or not to include UVs
    * @param includeNormals whether or not to include normals
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode ( final boolean includeUvs,
      final boolean includeNormals ) {

      final StringBuilder pyCd = new StringBuilder(1024);
      pyCd.append("{\"name\": \"");
      pyCd.append(this.name);
      pyCd.append("\", \"material_index\": ");
      pyCd.append(this.materialIndex);
      pyCd.append(", \"vertices\": [");

      final int vlen = this.coords.length;
      final int vlast = vlen - 1;
      for ( int i = 0; i < vlen; ++i ) {
         pyCd.append(this.coords[i].toBlenderCode());
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

      if ( includeNormals ) {
         pyCd.append("], \"normals\": [");
         final int nlen = this.normals.length;
         final int nlast = nlen - 1;
         for ( int h = 0; h < nlen; ++h ) {
            pyCd.append(this.normals[h].toBlenderCode());
            if ( h < nlast ) { pyCd.append(',').append(' '); }
         }

         pyCd.append("], \"normal_indices\": [");
         for ( int j = 0; j < fsLen; ++j ) {
            final int[][] vrtInd = this.faces[j];
            final int vrtIndLen = vrtInd.length;
            final int vrtLast = vrtIndLen - 1;

            pyCd.append('(');
            for ( int k = 0; k < vrtIndLen; ++k ) {
               pyCd.append(vrtInd[k][2]);
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
    * Attempts to recalculate the texture coordinates of this mesh per vertex
    * through spherical projection. The coordinate's unsigned azimuth is
    * assigned to the texture coordinate u; the complement of its inclination
    * is assigned to the texture coordinate v.<br>
    * <br>
    * This is protected because UVs wrapping from 0.0 to 1.0 created artifacts
    * in meshes that do not have seams.
    *
    * @return this mesh
    */
   @Experimental
   protected Mesh3 calcUvs ( ) {

      final int coordsLen = this.coords.length;
      this.texCoords = Vec2.resize(this.texCoords, coordsLen);
      for ( int i = 0; i < coordsLen; ++i ) {
         final Vec3 v = this.coords[i];
         final float azim = Utils.atan2(v.y, v.x);
         final float incl = Utils.asin(v.z * Utils.invSqrt(v.x * v.x + v.y * v.y
            + v.z * v.z));

         /* Simplification of the map function. */
         this.texCoords[i].set(Utils.mod1(azim * IUtils.ONE_TAU),
            ( IUtils.HALF_PI - incl ) * IUtils.ONE_PI);
      }

      /* Assign coordinate index to UV index. */
      final int facesLen = this.faces.length;
      for ( int i = 0; i < facesLen; ++i ) {
         final int[][] verts = this.faces[i];
         final int vertsLen = verts.length;
         for ( int j = 0; j < vertsLen; ++j ) { verts[j][1] = verts[j][0]; }
      }

      return this;
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
    *
    * @see Vec3#normalize(Vec3, Vec3)
    */
   @Experimental
   protected Mesh3 subdivEdge ( final int faceIndex, final int edgeIndex,
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
      final Vec3 vOrigin = this.coords[vert0Idx[0]];
      final Vec2 vtOrigin = this.texCoords[vert0Idx[1]];
      final Vec3 vnOrigin = this.normals[vert0Idx[2]];

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

         vsNew[k] = new Vec3(u * vOrigin.x + step * vDest.x, u * vOrigin.y
            + step * vDest.y, u * vOrigin.z + step * vDest.z);
         vtsNew[k] = new Vec2(u * vtOrigin.x + step * vtDest.x, u * vtOrigin.y
            + step * vtDest.y);
         final Vec3 vn = vnsNew[k] = new Vec3(u * vnOrigin.x + step * vnDest.x,
            u * vnOrigin.y + step * vnDest.y, u * vnOrigin.z + step * vnDest.z);
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
    * Default cube size, such that it will match the dimensions of other
    * Platonic solids; <code>0.5d / Math.sqrt(2.0d)</code> , approximately
    * {@value Mesh3#DEFAULT_CUBE_SIZE} .
    */
   public static final float DEFAULT_CUBE_SIZE = 0.35355338f;

   /**
    * The default texture coordinate (UV) profile to use when making a cube.
    */
   public static final CubeUvProfile DEFAULT_CUBE_UV_PROFILE
      = CubeUvProfile.PER_FACE;

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
   public static Vec3 calcDimensions ( final Mesh3 mesh, final Vec3 target,
      final Vec3 lb, final Vec3 ub ) {

      lb.set(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
      ub.set(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

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

      return Vec3.sub(ub, lb, target);
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
    * Creates a cylinder on the z axis, where its pointed end is on +z and its
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
         final int vnCurrent = 2 + i;

         final int imod = ( i + 1 ) % vsect;
         final int vNext = 2 + imod;
         final int vtNext = 1 + imod;
         final int vnNext = 2 + imod;

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
         final int[] base1 = base[1];
         final int[] base2 = base[2];

         // base0[0] = 0;
         // base0[1] = 0;
         // base0[2] = 0;

         base1[0] = vCurrent;
         base1[1] = vtCurrent;
         // base1[2] = 0;

         base2[0] = vNext;
         base2[1] = vtNext;
         // base2[2] = 0;

         final int[][] side = fs[k];
         final int[] side0 = side[0];
         final int[] side1 = side[1];
         final int[] side2 = side[2];

         side0[0] = 1;
         // side0[1] = 0;
         side0[2] = 1;

         side1[0] = vCurrent;
         side1[1] = vtCurrent;
         side1[2] = vnCurrent;

         side2[0] = vNext;
         side2[1] = vtNext;
         side2[2] = vnNext;
      }

      return target;
   }

   /**
    * Creates a cylinder on the z axis, where its pointed end is on +z and its
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
      final CubeUvProfile profile, final Mesh3 target ) {

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
    * Creates a cube, subdivides, casts the vertices to a sphere, then
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
    * Creates a cube, subdivides, casts the vertices to a sphere, then
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
    * @see Mesh3#subdivFacesCenter(int)
    */
   public static Mesh3 cubeSphere ( final int itrs, final PolyType poly,
      final CubeUvProfile profile, final Mesh3 target ) {

      /*
       * Sort has to be done first to merge newly created vertices before
       * normals are calculated.
       */
      Mesh3.cube(0.5f, PolyType.QUAD, profile, target);
      target.subdivFacesCenter(itrs);
      if ( poly == PolyType.TRI ) { target.triangulate(); }
      target.clean();
      Mesh3.castToSphere(target, 0.5f, target);
      target.name = "Sphere";
      return target;
   }

   /**
    * Creates a cube, subdivides, casts the vertices to a sphere. For
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
    * visitation of each face in the source, so they may contain redundant
    * data to be removed with {@link Mesh3#clean()}.
    *
    * @param source the source mesh
    *
    * @return the mesh array
    */
   public static Mesh3[] detachFaces ( final Mesh3 source ) {

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

            fTrg[j][0] = j;
            fTrg[j][1] = j;
            fTrg[j][2] = j;
         }

         meshes[i] = new Mesh3(fsTrg, vsTrg, vtsTrg, vnsTrg);
      }

      return meshes;
   }

   /**
    * Creates an dodecahedron, a Platonic solid with 12 faces and 20
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
       * 0.19098300562505255
       */

      target.name = "Dodecahedron";

      target.coords = Vec3.resize(target.coords, 20);
      target.coords[0].set(0.0f, 0.33614415f, -0.4165113f);
      target.coords[1].set(-0.19098301f, 0.47552827f, 0.15450847f);
      target.coords[2].set(0.19098301f, 0.47552827f, 0.15450847f);
      target.coords[3].set(0.309017f, 0.19840115f, 0.38938415f);
      target.coords[4].set(-0.309017f, 0.19840115f, 0.38938415f);
      target.coords[5].set(-0.19098301f, -0.47552827f, -0.15450847f);
      target.coords[6].set(-0.309017f, -0.38938415f, 0.19840115f);
      target.coords[7].set(0.19098301f, -0.47552827f, -0.15450847f);
      target.coords[8].set(0.309017f, -0.19840115f, -0.38938415f);
      target.coords[9].set(0.0f, -0.02712715f, -0.53454524f);
      target.coords[10].set(0.309017f, 0.38938415f, -0.19840115f);
      target.coords[11].set(0.5f, 0.05901699f, -0.18163565f);
      target.coords[12].set(-0.309017f, -0.19840115f, -0.38938415f);
      target.coords[13].set(-0.5f, 0.05901699f, -0.18163565f);
      target.coords[14].set(-0.309017f, 0.38938415f, -0.19840115f);
      target.coords[15].set(0.0f, 0.02712715f, 0.53454524f);
      target.coords[16].set(0.0f, -0.33614415f, 0.4165113f);
      target.coords[17].set(0.309017f, -0.38938415f, 0.19840115f);
      target.coords[18].set(0.5f, -0.05901699f, 0.18163565f);
      target.coords[19].set(-0.5f, -0.05901699f, 0.18163565f);

      target.texCoords = Vec2.resize(target.texCoords, 5);
      target.texCoords[0].set(0.5f, 0.0f);
      target.texCoords[1].set(0.79389268f, 0.90450847f);
      target.texCoords[2].set(0.02447176f, 0.34549153f);
      target.texCoords[3].set(0.20610738f, 0.90450853f);
      target.texCoords[4].set(0.97552824f, 0.34549141f);

      target.normals = Vec3.resize(target.normals, 12);
      target.normals[0].set(-0.8506508f, 0.5f, 0.16245979f);
      target.normals[1].set(0.0f, -0.9714768f, 0.23713443f);
      target.normals[2].set(0.0f, 0.9714768f, -0.23713443f);
      target.normals[3].set(0.0f, -0.64655715f, -0.7628655f);
      target.normals[4].set(0.5257311f, 0.2628655f, -0.80901694f);
      target.normals[5].set(0.0f, 0.64655715f, 0.7628655f);
      target.normals[6].set(-0.5257311f, 0.2628655f, -0.80901694f);
      target.normals[7].set(-0.5257311f, -0.2628655f, 0.80901694f);
      target.normals[8].set(0.5257311f, -0.2628655f, 0.80901694f);
      target.normals[9].set(0.8506508f, 0.5f, 0.16245979f);
      target.normals[10].set(0.8506508f, -0.5f, -0.16245979f);
      target.normals[11].set(-0.8506508f, -0.5f, -0.16245979f);

      /* @formatter:off */
      target.faces = new int[][][] {
         { {  2, 0,  2 }, { 10, 2,  2 }, {  0, 3,  2 }, { 14, 1,  2 }, {  1, 4,  2 } },
         { {  1, 0,  5 }, {  4, 2,  5 }, { 15, 3,  5 }, {  3, 1,  5 }, {  2, 4,  5 } },
         { {  7, 0,  1 }, { 17, 2,  1 }, { 16, 3,  1 }, {  6, 1,  1 }, {  5, 4,  1 } },
         { {  5, 0,  3 }, { 12, 2,  3 }, {  9, 3,  3 }, {  8, 1,  3 }, {  7, 4,  3 } },
         { {  9, 0,  4 }, {  0, 2,  4 }, { 10, 3,  4 }, { 11, 1,  4 }, {  8, 4,  4 } },
         { {  0, 0,  6 }, {  9, 2,  6 }, { 12, 3,  6 }, { 13, 1,  6 }, { 14, 4,  6 } },
         { { 16, 0,  7 }, { 15, 2,  7 }, {  4, 3,  7 }, { 19, 1,  7 }, {  6, 4,  7 } },
         { { 15, 0,  8 }, { 16, 2,  8 }, { 17, 3,  8 }, { 18, 1,  8 }, {  3, 4,  8 } },
         { { 11, 0,  9 }, { 10, 2,  9 }, {  2, 3,  9 }, {  3, 1,  9 }, { 18, 4,  9 } },
         { { 18, 0, 10 }, { 17, 2, 10 }, {  7, 3, 10 }, {  8, 1, 10 }, { 11, 4, 10 } },
         { { 13, 0, 11 }, { 12, 2, 11 }, {  5, 3, 11 }, {  6, 1, 11 }, { 19, 4, 11 } },
         { { 19, 0,  0 }, {  4, 2,  0 }, {  1, 3,  0 }, { 14, 1,  0 }, { 13, 4,  0 } } };
      /* @formatter:on */

      return target;
   }

   /**
    * Creates a mesh from an array of strings representing a WaveFront obj
    * file.<br>
    * <br>
    * Files supplied to this parser should always include information for
    * coordinates, texture coordinates and normals. Mesh groups are not
    * supported by this function. Material data from a .mtl file is not parsed
    * here.
    *
    * @param lines  the String tokens
    * @param target the output mesh
    *
    * @return the mesh
    */
   public static Mesh3 fromObj ( final String[] lines, final Mesh3 target ) {

      String[] tokens;
      String[] faceTokens;
      String name = target.hashIdentityString();

      final int len = lines.length;
      final int capacity = len == 0 ? 32 : len / 4;
      final ArrayList < Vec3 > coordList = new ArrayList <>(capacity);
      final ArrayList < Vec2 > texCoordList = new ArrayList <>(capacity);
      final ArrayList < Vec3 > normalList = new ArrayList <>(capacity);
      final ArrayList < int[][] > faceList = new ArrayList <>(capacity);

      boolean vsMissing = false;
      boolean vtsMissing = false;
      boolean vnsMissing = false;

      boolean usesMaterial = false;
      String mtlFileName = "";
      final ArrayList < String > materialNames = new ArrayList <>(8);
      final Pattern spacePattern = Pattern.compile("\\s+");
      final Pattern fslashPattern = Pattern.compile("/");

      for ( int i = 0; i < len; ++i ) {

         /* Split line by spaces. */
         tokens = spacePattern.split(lines[i], 0);

         /* Skip empty lines. */
         if ( tokens.length > 0 ) {
            final String initialToken = tokens[0].toLowerCase();
            if ( initialToken.equals("o") ) {

               /* Assign name. */
               name = tokens[1];

            } else if ( initialToken.equals("mtllib") ) {

               usesMaterial = true;
               mtlFileName = tokens[1];

            } else if ( initialToken.equals("usemtl") ) {

               usesMaterial = true;
               materialNames.add(tokens[1]);

            } else if ( initialToken.equals("v") ) {

               /* Coordinate. */
               coordList.add(new Vec3(tokens[1], tokens[2], tokens[3]));

            } else if ( initialToken.equals("vt") ) {

               /* Texture coordinate. */
               texCoordList.add(new Vec2(tokens[1], tokens[2]));

            } else if ( initialToken.equals("vn") ) {

               /* Normal. */
               normalList.add(new Vec3(tokens[1], tokens[2], tokens[3]));

            } else if ( initialToken.equals("f") ) {

               /* Face. */
               final int count = tokens.length;

               /* tokens length includes "f", and so is 1 longer. */
               final int[][] indices = new int[count - 1][3];

               for ( int j = 1; j < count; ++j ) {
                  faceTokens = fslashPattern.split(tokens[j], 0);
                  final int tokenLen = faceTokens.length;
                  final int k = j - 1;

                  /* Vertex coordinate index. */
                  if ( tokenLen > 0 ) {
                     final String vIdx = faceTokens[0];
                     if ( vIdx == null || vIdx.isEmpty() ) {
                        vsMissing = true;
                     } else {
                        /* Indices in .obj file start at 1, not 0. */
                        indices[k][0] = Mesh3.intFromStr(vIdx) - 1;
                     }
                  } else {
                     vsMissing = true;
                  }

                  /* Texture coordinate index. */
                  if ( tokenLen > 1 ) {
                     final String vtIdx = faceTokens[1];
                     if ( vtIdx == null || vtIdx.isEmpty() ) {
                        vtsMissing = true;
                     } else {
                        indices[k][1] = Mesh3.intFromStr(vtIdx) - 1;
                     }
                  } else {
                     vtsMissing = true;
                  }

                  /* Normal index. */
                  if ( tokenLen > 2 ) {
                     final String vnIdx = faceTokens[2];
                     if ( vnIdx == null || vnIdx.isEmpty() ) {
                        vnsMissing = true;
                     } else {
                        indices[k][2] = Mesh3.intFromStr(vnIdx) - 1;
                     }
                  } else {
                     vnsMissing = true;
                  }
               }

               faceList.add(indices);
            }
         }
      }

      target.name = name;

      /* Convert to fixed-sized array. */
      final int[][][] faceArr = new int[faceList.size()][][];
      faceList.toArray(faceArr);

      Vec3[] coordArr = new Vec3[coordList.size()];
      if ( vsMissing && coordArr.length < 1 ) {
         coordArr = new Vec3[] { Vec3.zero(new Vec3()) };
      } else {
         coordList.toArray(coordArr);
      }

      Vec2[] texCoordArr = new Vec2[texCoordList.size()];
      if ( vtsMissing && texCoordArr.length < 1 ) {
         texCoordArr = new Vec2[] { Vec2.uvCenter(new Vec2()) };
      } else {
         texCoordList.toArray(texCoordArr);
      }

      Vec3[] normalArr = new Vec3[normalList.size()];
      if ( vnsMissing && normalArr.length < 1 ) {
         normalArr = new Vec3[] { Vec3.up(new Vec3()) };
      } else {
         normalList.toArray(normalArr);
      }

      if ( usesMaterial ) {
         final StringBuilder sb = new StringBuilder(512);
         sb.append("The .obj file refers to the .mtl file ");
         sb.append(mtlFileName);
         sb.append(" , namely, the materials: ");

         final Iterator < String > matNamesItr = materialNames.iterator();
         while ( matNamesItr.hasNext() ) {
            sb.append(matNamesItr.next());
            if ( matNamesItr.hasNext() ) { sb.append(',').append(' '); }
         }

         sb.append(" .");
         System.out.println(sb.toString());
      }

      target.set(faceArr, coordArr, texCoordArr, normalArr);
      return target;
   }

   /**
    * Creates an array of meshes from an array of strings representing a
    * WaveFront .obj file with groups.<br>
    * <br>
    * Files supplied to this parser should always include information for
    * coordinates, texture coordinates and normals. Material data from a .mtl
    * file is not parsed by this function.<br>
    * <br>
    * Because vertex groups are not supported by the Mesh3 class, an option to
    * pool data is provided. If data is pooled between meshes, then all will
    * reference the same coordinate, texture coordinate and normal array. If
    * not, each mesh will receive a copy of the data parsed from the .obj
    * file; the mesh will then be cleaned to remove unused data.
    *
    * @param lines    the strings
    * @param poolData whether to share data
    *
    * @return the array of meshes
    */
   @Experimental
   public static Mesh3[] fromObjGroup ( final String[] lines,
      final boolean poolData ) {

      String[] tokens;
      String[] faceTokens;
      String objName = "Mesh3";

      final int len = lines.length;
      final int capacity = len != 0 ? len / 4 : 32;
      final ArrayList < Vec3 > coordList = new ArrayList <>(capacity);
      final ArrayList < Vec2 > texCoordList = new ArrayList <>(capacity);
      final ArrayList < Vec3 > normalList = new ArrayList <>(capacity);
      final HashMap < String, ArrayList < int[][] > > faceGroups
         = new HashMap <>();
      ArrayList < int[][] > currentIndices = new ArrayList <>();

      boolean vsMissing = false;
      boolean vtsMissing = false;
      boolean vnsMissing = false;
      boolean groupsMissing = true;

      boolean usesMaterial = false;
      String mtlFileName = "";
      final ArrayList < String > materialNames = new ArrayList <>(8);
      final Pattern spacePattern = Pattern.compile("\\s+");
      final Pattern fslashPattern = Pattern.compile("/");

      for ( int i = 0; i < len; ++i ) {
         tokens = spacePattern.split(lines[i], 0);

         if ( tokens.length > 0 ) {

            /* Switch case by hash code of String, not String itself. */
            final String initialToken = tokens[0].toLowerCase();
            final int hsh = initialToken.hashCode();

            switch ( hsh ) {

               case -1063936832:
                  /* "mtllib" */
                  usesMaterial = true;
                  mtlFileName = tokens[1];

                  break;

               case -836034370:
                  /* "usemtl" */
                  usesMaterial = true;
                  materialNames.add(tokens[1]);

                  break;

               case 102:
                  /* "f" */

                  if ( currentIndices == null ) { break; }

                  /* tokens length includes "f", and so is 1 longer. */
                  final int count = tokens.length;
                  final int[][] indices = new int[count - 1][3];

                  for ( int j = 1; j < count; ++j ) {
                     faceTokens = fslashPattern.split(tokens[j], 0);
                     final int tokenLen = faceTokens.length;
                     final int k = j - 1;

                     /* Indices in .obj file start at 1, not 0. */
                     if ( tokenLen > 0 ) {
                        final String vIdx = faceTokens[0];
                        if ( vIdx == null || vIdx.isEmpty() ) {
                           vsMissing = true;
                        } else {
                           indices[k][0] = Mesh3.intFromStr(vIdx) - 1;
                        }
                     } else {
                        vsMissing = true;
                     }

                     /* Attempt to read texture coordinate index. */
                     if ( tokenLen > 1 ) {
                        final String vtIdx = faceTokens[1];
                        if ( vtIdx == null || vtIdx.isEmpty() ) {
                           vtsMissing = true;
                        } else {
                           indices[k][1] = Mesh3.intFromStr(vtIdx) - 1;
                        }
                     } else {
                        vtsMissing = true;
                     }

                     /* Attempt to read normal index. */
                     if ( tokenLen > 2 ) {
                        final String vnIdx = faceTokens[2];
                        if ( vnIdx == null || vnIdx.isEmpty() ) {
                           vnsMissing = true;
                        } else {
                           indices[k][2] = Mesh3.intFromStr(vnIdx) - 1;
                        }
                     } else {
                        vnsMissing = true;
                     }
                  }

                  currentIndices.add(indices);

                  break;

               case 103:
                  /* "g" */
                  final String name = objName + "." + tokens[1];
                  if ( !faceGroups.containsKey(name) ) {
                     faceGroups.put(name, new ArrayList <>());
                  }
                  currentIndices = faceGroups.get(name);
                  groupsMissing = false;

                  break;

               case 111:
                  /* "o" */
                  objName = tokens[1];

                  break;

               case 118:
                  /* "v" */
                  coordList.add(new Vec3(tokens[1], tokens[2], tokens[3]));

                  break;

               case 3768:
                  /* "vn" */
                  normalList.add(new Vec3(tokens[1], tokens[2], tokens[3]));

                  break;

               case 3774:
                  /* "vt" */
                  texCoordList.add(new Vec2(tokens[1], tokens[2]));

                  break;

               default:
            }
         }
      }

      /* Convert to fixed-sized array. */
      Vec3[] coordArr = new Vec3[coordList.size()];
      if ( vsMissing && coordArr.length < 1 ) {
         coordArr = new Vec3[] { Vec3.zero(new Vec3()) };
      } else {
         coordList.toArray(coordArr);
      }

      Vec2[] texCoordArr = new Vec2[texCoordList.size()];
      if ( vtsMissing && texCoordArr.length < 1 ) {
         texCoordArr = new Vec2[] { Vec2.uvCenter(new Vec2()) };
      } else {
         texCoordList.toArray(texCoordArr);
      }

      Vec3[] normalArr = new Vec3[normalList.size()];
      if ( vnsMissing && normalArr.length < 1 ) {
         normalArr = new Vec3[] { Vec3.up(new Vec3()) };
      } else {
         normalList.toArray(normalArr);
      }

      /* Notify if material library was detected. */
      if ( usesMaterial ) {
         final StringBuilder sb = new StringBuilder(512);
         sb.append("The .obj file refers to the .mtl file ");
         sb.append(mtlFileName);
         sb.append(" , namely, the materials: ");

         final Iterator < String > matNamesItr = materialNames.iterator();
         while ( matNamesItr.hasNext() ) {
            sb.append(matNamesItr.next());
            if ( matNamesItr.hasNext() ) { sb.append(',').append(' '); }
         }

         sb.append(" .");
         System.out.println(sb.toString());
      }

      final int groupsLen = faceGroups.size() < 1 ? 1 : faceGroups.size();
      final Mesh3[] result = new Mesh3[groupsLen];

      if ( groupsMissing ) {

         final Mesh3 mesh = result[0] = new Mesh3();
         mesh.name = objName;

         mesh.faces = new int[currentIndices.size()][][];
         currentIndices.toArray(mesh.faces);

         mesh.coords = coordArr;
         mesh.texCoords = texCoordArr;
         mesh.normals = normalArr;

         mesh.clean();

      } else {

         int meshIdx = 0;
         final int coordLen = coordArr.length;
         final int texCoordLen = texCoordArr.length;
         final int normalLen = normalArr.length;

         /* Convert from hash map to meshes. */
         final Iterator < Entry < String, ArrayList < int[][] > > > itr
            = faceGroups.entrySet().iterator();

         /* Loop over entries in dictionary. */
         while ( itr.hasNext() ) {
            final Entry < String, ArrayList < int[][] > > entry = itr.next();

            final Mesh3 mesh = new Mesh3();
            mesh.name = entry.getKey();

            final ArrayList < int[][] > facesList = entry.getValue();
            mesh.faces = new int[facesList.size()][][];
            facesList.toArray(mesh.faces);

            if ( poolData ) {

               mesh.coords = coordArr;
               mesh.texCoords = texCoordArr;
               mesh.normals = normalArr;

            } else {

               /* Copy data by value, not by reference. */
               mesh.coords = new Vec3[coordLen];
               for ( int i = 0; i < coordLen; ++i ) {
                  mesh.coords[i] = new Vec3(coordArr[i]);
               }

               mesh.texCoords = new Vec2[texCoordLen];
               for ( int j = 0; j < texCoordLen; ++j ) {
                  mesh.texCoords[j] = new Vec2(texCoordArr[j]);
               }

               mesh.normals = new Vec3[normalLen];
               for ( int k = 0; k < normalLen; ++k ) {
                  mesh.normals[k] = new Vec3(normalArr[k]);
               }

               /* Remove unused data. */
               mesh.clean();
            }

            result[meshIdx] = mesh;
            ++meshIdx;
         }
      }

      return result;
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
         final Vec3[] vsCopy = new Vec3[vsLen];
         for ( int j = 0; j < vsLen; ++j ) { vsCopy[j] = new Vec3(vsSrc[j]); }

         /* Copy source texture coordinates. */
         final Vec2[] vtsSrc = source.texCoords;
         final int vtsLen = vtsSrc.length;
         final Vec2[] vtsCopy = new Vec2[vtsLen];
         for ( int j = 0; j < vtsLen; ++j ) {
            vtsCopy[j] = new Vec2(vtsSrc[j]);
         }

         /* Copy source normals. */
         final Vec3[] vnsSrc = source.normals;
         final int vnsLen = vnsSrc.length;
         final Vec3[] vnsCopy = new Vec3[vnsLen];
         for ( int j = 0; j < vnsLen; ++j ) {
            vnsCopy[j] = new Vec3(vnsSrc[j]);
         }

         /* Concatenated indices need to be offset by current data lengths. */
         final int vsTrgLen = current.coords.length;
         final int vtsTrgLen = current.texCoords.length;
         final int vnsTrgLen = current.normals.length;

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
               fSrcCopy[k][2] = vnsTrgLen + fSrc[k][2];
            }
         }

         /* Concatenate copies with current data. */
         current.coords = Vec3.concat(current.coords, vsCopy);
         current.texCoords = Vec2.concat(current.texCoords, vtsCopy);
         current.normals = Vec3.concat(current.normals, vnsCopy);
         current.faces = Mesh.splice(current.faces, current.faces.length, 0,
            fsCopy);
      }

      /* Convert dictionary values to an array; clean meshes of excess data. */
      final Mesh3[] result = dict.values().toArray(new Mesh3[dict.size()]);
      final int trgLen = result.length;
      for ( int i = 0; i < trgLen; ++i ) { result[i].clean(); }
      return result;
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

      /* @formatter:off */
      target.faces = new int[][][] {
         { { 0, 2, 0 }, { 1, 1, 0 }, { 4, 0, 0 } },
         { { 1, 2, 3 }, { 3, 1, 3 }, { 4, 0, 3 } },
         { { 3, 2, 1 }, { 2, 1, 1 }, { 4, 0, 1 } },
         { { 2, 2, 2 }, { 0, 1, 2 }, { 4, 0, 2 } },
         { { 2, 2, 4 }, { 3, 1, 4 }, { 5, 0, 4 } },
         { { 3, 2, 5 }, { 1, 1, 5 }, { 5, 0, 5 } },
         { { 1, 2, 6 }, { 0, 1, 6 }, { 5, 0, 6 } },
         { { 0, 2, 7 }, { 2, 1, 7 }, { 5, 0, 7 } } };
      /* @formatter:on */

      return target;
   }

   /**
    * Projects a 3D mesh onto a 2D surface, returning the 2D mesh projection.
    * Occluded faces are not culled from the 2D mesh.
    *
    * @param source     the input mesh
    * @param projection the projection matrix
    * @param modelview  the model view matrix
    * @param target     the output mesh
    *
    * @return the projected mesh
    */
   @Experimental
   public static Mesh2 project ( final Mesh3 source, final Mat4 projection,
      final Mat4 modelview, final Mesh2 target ) {

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

         promoted.set(vsSource[i], 1.0f);
         Mat4.mul(modelview, promoted, promoted);
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

      /* Sort faces according to 3D vectors. */
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
      float maxDist = Float.MIN_VALUE;
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
      final float spanOrigin = maxDist - minDist;
      final float scalar = spanOrigin != 0.0f ? ( farBound - nearBound )
         / spanOrigin : 0.0f;
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

      /* @formatter:off */
      switch ( poly ) {
         case NGON:
         case QUAD:
            target.faces = new int[][][] {
               { { 0, 0, 0 }, { 1, 1, 0 }, { 2, 2, 0 }, { 3, 3, 0 } } };
            break;
         case TRI:
         default:
            target.faces = new int[][][] {
               { { 0, 0, 0 }, { 1, 1, 0 }, { 2, 2, 0 } },
               { { 0, 0, 0 }, { 2, 2, 0 }, { 3, 3, 0 } } };
      }
      /* @formatter:on */

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
    * Creates a flat representation of a a mesh's texture coordinates as a
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
      final int vsect = sectors < 3 ? 3 : sectors;
      final int vpanl = panels < 3 ? 3 : panels;
      final float rho0 = 0.5f;
      final float rho1 = rho0 * Utils.clamp(thickness, IUtils.EPSILON, 1.0f
         - IUtils.EPSILON);

      /* Values for array accesses. */
      final int vsect1 = vsect + 1;
      final int vpanl1 = vpanl + 1;
      final int vLen = vpanl * vsect;
      final int vtLen = vpanl1 * vsect1;
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

      /* Calculate number of sectors in a ring. */
      final float toTheta = 1.0f / vsect;
      final float[] costs = new float[vsect];
      final float[] sints = new float[vsect];
      for ( int j = 0; j < vsect; ++j ) {
         final float theta = j * toTheta;
         costs[j] = Utils.scNorm(theta);
         sints[j] = Utils.scNorm(theta - 0.25f);
      }

      /* Calculate number of side panels in a sector. */
      final float toPhi = 1.0f / vpanl;

      /* Calculate coordinates and normals. */
      for ( int k = 0, i = 0; i < vpanl; ++i ) {
         final float phi = -0.5f + i * toPhi;
         final float cosPhi = Utils.scNorm(phi);
         final float sinPhi = Utils.scNorm(phi - 0.25f);

         final float rhoCosPhi = rho0 + rho1 * cosPhi;
         final float rhoSinPhi = rho1 * sinPhi;

         for ( int j = 0; j < vsect; ++j, ++k ) {
            final float cosTheta = costs[j];
            final float sinTheta = sints[j];

            vs[k].set(rhoCosPhi * cosTheta, -rhoSinPhi, rhoCosPhi * sinTheta);
            vns[k].set(cosPhi * cosTheta, -sinPhi, cosPhi * sinTheta);
         }
      }

      /* Calculate texture coordinates u. */
      final float[] uvxs = new float[vsect1];
      final float toU = 1.0f / vsect;
      for ( int j = 0; j < vsect1; ++j ) { uvxs[j] = j * toU; }

      /* Combine into texture coordinates. */
      final float toV = 1.0f / vpanl;
      for ( int k = 0, i = 0; i < vpanl1; ++i ) {
         final float y = 1.0f - i * toV;
         for ( int j = 0; j < vsect1; ++j, ++k ) { vts[k].set(uvxs[j], y); }
      }

      /* Set faces. */
      final int faceStride = isTri ? 2 : 1;
      for ( int k = 0, i = 0; i < vpanl; ++i ) {
         final int iVNext = ( i + 1 ) % vpanl;

         /* For converting from 2D to 1D array (idx = y * width + x) . */
         final int vOffCurr = i * vsect;
         final int vOffNext = iVNext * vsect;

         final int vtOffCurr = i * vsect1;
         final int vtOffNext = vtOffCurr + vsect1;

         for ( int j = 0; j < vsect; ++j, k += faceStride ) {
            final int jVtNext = j + 1;
            final int jVNext = jVtNext % vsect;

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
               /* Triangle 0 */
               final int[][] tri0 = fs[k];
               final int[] a0 = tri0[0];
               final int[] b0 = tri0[1];
               final int[] c0 = tri0[2];

               /* Triangle 1 */
               final int[][] tri1 = fs[k + 1];
               final int[] a1 = tri1[0];
               final int[] b1 = tri1[1];
               final int[] c1 = tri1[2];

               /* @formatter:off */
               a0[0] = v00; a0[1] = vt00; a0[2] = v00;
               b0[0] = v10; b0[1] = vt10; b0[2] = v10;
               c0[0] = v11; c0[1] = vt11; c0[2] = v11;

               a1[0] = v00; a1[1] = vt00; a1[2] = v00;
               b1[0] = v11; b1[1] = vt11; b1[2] = v11;
               c1[0] = v01; c1[1] = vt01; c1[2] = v01;
               /* @formatter:on */

            } else {

               final int[][] quad = fs[k];
               final int[] a = quad[0];
               final int[] b = quad[1];
               final int[] c = quad[2];
               final int[] d = quad[3];

               /* @formatter:off */
               a[0] = v00; a[1] = vt00; a[2] = v00;
               b[0] = v10; b[1] = vt10; b[2] = v10;
               c[0] = v11; c[1] = vt11; c[2] = v11;
               d[0] = v01; d[1] = vt01; d[2] = v01;
               /* @formatter:on */
            }
         }
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

      final Vec3[] vsSrc = source.coords;
      final Vec2[] vtsSrc = source.texCoords;
      final int[][][] fsSrc = source.faces;
      final int fsSrcLen = fsSrc.length;
      final int trgLen = fsSrcLen * vcount;

      final Vec3[] vsTrg = target.coords = Vec3.resize(target.coords, trgLen);
      final Vec2[] vtsTrg = target.texCoords = Vec2.resize(target.texCoords,
         trgLen);
      final int[][][] fsTrg = target.faces = new int[fsSrcLen][vcount][3];

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

      target.shadeFlat();
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

      target.name = "UV Sphere";

      /* Validate arguments. */
      final int lons = longitudes < 3 ? 3 : longitudes;
      final int lats = latitudes < 1 ? 1 : latitudes;

      /* UV coordinates require an extra longitude. */
      final int lons1 = lons + 1;
      final int lats1 = lats + 1;

      /*
       * Reallocate arrays. The 2 coordinates come from the poles. The 2 *
       * longitudes texture coordinates come from the triangle fan formed at the
       * poles.
       */
      final int vLen = lons * lats + 2;
      final int vtLen = lons1 * lats + lons + lons;
      final Vec3[] vs = target.coords = Vec3.resize(target.coords, vLen);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
         vtLen);
      final Vec3[] vns = target.normals = Vec3.resize(target.normals, vLen);

      /*
       * Because of the two poles, the linear interpolation between latitudes is
       * not i / (latitudes - 1.0); it is (i + 1) / (latitudes + 1.0) . Phi is
       * half the range as theta (either TAU / 2.0 or 1.0 / 2.0) .
       */
      final float toTexS = 1.0f / lons;
      final float toTexT = 1.0f / lats1;
      final float toTheta = 1.0f / lons;
      final float toPhi = 0.5f / lats1;

      /* Set North pole. Offset subsequent vertex indices by 1. */
      vs[0].set(0.0f, 0.0f, 0.5f);
      vns[0].set(0.0f, 0.0f, 1.0f);

      /* Set South pole. */
      final int last0 = vLen - 1;
      vs[last0].set(0.0f, 0.0f, -0.5f);
      vns[last0].set(0.0f, 0.0f, -1.0f);

      /*
       * Calculate sine & cosine of theta. Calculate texture coordinate poles.
       */
      final float[] costs = new float[lons];
      final float[] sints = new float[lons];
      for ( int j = 0, k = vtLen - lons; j < lons; ++j, ++k ) {
         final float jf = j;
         final float theta = jf * toTheta;
         costs[j] = Utils.scNorm(theta);
         sints[j] = Utils.scNorm(theta - 0.25f);

         /* Texture coordinates at poles. */
         final float sTex = ( jf + 0.5f ) * toTexS;
         vts[j].set(sTex, 0.0f);
         vts[k].set(sTex, 1.0f);
      }

      /* Calculate the texture coordinate u's. */
      final float[] tcxs = new float[lons1];
      for ( int j = 0; j < lons1; ++j ) { tcxs[j] = j * toTexS; }

      /* Loop over the latitudes. */
      final float radius = 0.5f;
      for ( int i = 0, vIdx = 1, vtIdx = lons; i < lats; ++i ) {

         /* Account for offset of pole. */
         final float spOff = i + 1.0f;
         final float tTex = spOff * toTexT;

         /*
          * The expected range of phi is [-HALF_PI, HALF_PI], so subtract
          * QUARTER_PI, or 0.25 for normalized angles, to shift the range.
          */
         final float phi = spOff * toPhi - 0.25f;
         final float cosPhi = Utils.scNorm(phi);
         final float sinPhi = -Utils.scNorm(phi - 0.25f);

         final float rhoCosPhi = radius * cosPhi;
         final float rhoSinPhi = radius * sinPhi;

         /* Loop over coordinates and normals. */
         for ( int j = 0; j < lons; ++j, ++vIdx ) {

            final float cosTheta = costs[j];
            final float sinTheta = sints[j];

            vs[vIdx].set(rhoCosPhi * cosTheta, rhoCosPhi * sinTheta, rhoSinPhi);
            vns[vIdx].set(cosPhi * cosTheta, cosPhi * sinTheta, sinPhi);
         }

         /* Loop over texture coordinates. */
         for ( int j = 0; j < lons1; ++j, ++vtIdx ) {
            vts[vtIdx].set(tcxs[j], tTex);
         }
      }

      /*
       * The length of the faces array comes from the north and south cap being
       * triangle fans around the longitudes (2 * lons) and the middle being
       * triangles (latsn1 * lons * 2), or (latsn1 * lons) for quads.
       */
      final boolean isQuad = poly != PolyType.TRI;
      final int latsn1 = lats - 1;
      final int midCount = isQuad ? latsn1 * lons : latsn1 * lons * 2;
      final int fsLen = lons + lons + midCount;

      /*
       * If quadrilaterals are chosen, then the faces array will be jagged. Some
       * inner arrays will be 3 long, i.e., the North and South caps; others,
       * for the mid-latitudes, will be 4 long.
       */
      final int[][][] fs = target.faces = isQuad ? new int[fsLen][4][3]
         : new int[fsLen][3][3];

      /* Offsets for south cap. */
      final int vIdxOff = last0 - lons;
      final int vtPoleOff = vtLen - lons;
      final int vtIdxOff = vtPoleOff - lons1;
      final int fsIdxOff = fsLen - lons;

      /* Polar caps. */
      for ( int i = 0; i < lons; ++i ) {
         final int k = i + 1;
         final int n = k % lons;
         final int j = 1 + n;
         final int m = lons + i;

         final int[][] northTri = isQuad ? fs[i] = new int[3][3] : fs[i];
         final int[][] southTri = isQuad ? fs[fsIdxOff + i] = new int[3][3]
            : fs[fsIdxOff + i];

         /* Polar vertex. */
         // final int[] north0 = northTri[0];
         // north0[0] = 0;
         // north0[1] = i;
         // north0[2] = 0;
         northTri[0][1] = i;

         final int[] north1 = northTri[1];
         north1[0] = k;
         north1[1] = m;
         north1[2] = k;

         final int[] north2 = northTri[2];
         north2[0] = j;
         north2[1] = m + 1;
         north2[2] = j;

         /* Polar vertex. */
         final int[] south0 = southTri[0];
         south0[0] = last0;
         south0[1] = vtPoleOff + i;
         south0[2] = last0;

         final int[] south1 = southTri[1];
         south1[0] = vIdxOff + n;
         south1[1] = vtIdxOff + k;
         south1[2] = vIdxOff + n;

         final int[] south2 = southTri[2];
         south2[0] = vIdxOff + i;
         south2[1] = vtIdxOff + i;
         south2[2] = vIdxOff + i;
      }

      /* Middle. */
      final int stride = isQuad ? 1 : 2;
      for ( int i = 0, k = lons; i < latsn1; ++i ) {

         /* For coordinates and normals. */
         final int currentLat0 = 1 + i * lons;
         final int nextLat0 = currentLat0 + lons;

         /* For texture coordinates. */
         final int currentLat1 = lons + i * lons1;
         final int nextLat1 = currentLat1 + lons1;

         for ( int j = 0; j < lons; ++j, k += stride ) {

            /* Wrap around to first longitude at last. */
            final int nextLon1 = j + 1;
            final int nextLon0 = nextLon1 % lons;

            /* Coordinate and normal indices. */
            final int v00 = currentLat0 + j;
            final int v10 = currentLat0 + nextLon0;
            final int v11 = nextLat0 + nextLon0;
            final int v01 = nextLat0 + j;

            /* Texture coordinate indices. */
            final int vt00 = currentLat1 + j;
            final int vt10 = currentLat1 + nextLon1;
            final int vt11 = nextLat1 + nextLon1;
            final int vt01 = nextLat1 + j;

            if ( isQuad ) {

               /* @formatter:off */
               final int[][] quad = fs[k];
               final int[] q0 = quad[0];
               q0[0] = v00; q0[1] = vt00; q0[2] = v00;
               final int[] q1 = quad[1];
               q1[0] = v01; q1[1] = vt01; q1[2] = v01;
               final int[] q2 = quad[2];
               q2[0] = v11; q2[1] = vt11; q2[2] = v11;
               final int[] q3 = quad[3];
               q3[0] = v10; q3[1] = vt10; q3[2] = v10;

            } else {

               final int[][] tri0 = fs[k];
               final int[] tri00 = tri0[0];
               tri00[0] = v00; tri00[1] = vt00; tri00[2] = v00;
               final int[] tri01 = tri0[1];
               tri01[0] = v01; tri01[1] = vt01; tri01[2] = v01;
               final int[] tri02 = tri0[2];
               tri02[0] = v11; tri02[1] = vt11; tri02[2] = v11;

               final int[][] tri1 = fs[k + 1];
               final int[] tri10 = tri1[0];
               tri10[0] = v00; tri10[1] = vt00; tri10[2] = v00;
               final int[] tri11 = tri1[1];
               tri11[0] = v11; tri11[1] = vt11; tri11[2] = v11;
               final int[] tri12 = tri1[2];
               tri12[0] = v10; tri12[1] = vt10; tri12[2] = v10;
               /* @formatter:on */
            }
         }
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
    * An internal helper function that reassigns a cube's texture coordinates
    * and face indices based on the desired polygon type and UV profile.
    *
    * @param poly    the polygon type
    * @param profile the texture profile
    * @param target  the output mesh
    *
    * @return the updated mesh
    */
   private static Mesh3 cubeTexCoords ( final PolyType poly,
      final CubeUvProfile profile, final Mesh3 target ) {

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
                     { { 2,  2, 5 }, { 7,  6, 5 }, { 6,  7, 5 } }, };
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
    * private so that multiple public overloaded methods may access it.
    *
    * @param xOrigin     origin x
    * @param yOrigin     origin y
    * @param zOrigin     origin z
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
   private static Mesh3 cylinder ( final float xOrigin, final float yOrigin,
      final float zOrigin, final float xDest, final float yDest,
      final float zDest, final int sectors, final boolean includeCaps,
      final float radius, final Mesh3 target ) {

      target.name = "Cylinder";

      /* Find difference between destination and origin. */
      float x0 = xDest - xOrigin;
      float y0 = yDest - yOrigin;
      float z0 = zDest - zOrigin;

      /* If difference's length is zero, invalid inputs. */
      final float m0 = x0 * x0 + y0 * y0 + z0 * z0;
      if ( Utils.approx(m0, 0.0f, IUtils.EPSILON) ) {
         x0 = IUtils.EPSILON * 2.0f;
         y0 = x0;
         z0 = x0;
      }

      /* Validate arguments. */
      final int sec = sectors < 3 ? 3 : sectors;
      final float rad = Utils.max(IUtils.EPSILON, radius);

      /* Normalize forward. */
      final float mInv0 = Utils.invSqrtUnchecked(m0);
      final float kx = x0 * mInv0;
      final float ky = y0 * mInv0;
      final float kz = z0 * mInv0;

      /* Find the cross product of forward and reference (0.0, 0.0, 1.0). */
      float x1 = -ky;
      float y1 = kx;
      float z1 = 0.0f;

      /* Forward and up are parallel if the cross product is zero. */
      if ( Utils.approx(x1, 0.0f, IUtils.EPSILON) && Utils.approx(y1, 0.0f,
         IUtils.EPSILON) && Utils.approx(z1, 0.0f, IUtils.EPSILON) ) {

         /*
          * If forward and up are parallel, recalculate the cross product
          * between forward and reference (0.0, 1.0, 0.0).
          */
         x1 = kz;
         y1 = 0.0f;
         z1 = -kx;
      }

      /* The cross product is the right axis. Normalize right. */
      final float mInv1 = Utils.invHypot(x1, y1, z1);
      final float ix = x1 * mInv1;
      final float iy = y1 * mInv1;
      final float iz = z1 * mInv1;

      /* Cross right against forward to get the up axis. */
      final float x2 = iy * kz - iz * ky;
      final float y2 = iz * kx - ix * kz;
      final float z2 = ix * ky - iy * kx;

      /* Normalize the up axis. */
      final float mInv2 = Utils.invHypot(x2, y2, z2);
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
         // final float u = i * toTheta;
         final float u = 1.0f - i * toTheta;
         vts[i].set(u, 1.0f);
         vts[j].set(u, 0.0f);
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

         /*
          * In case you want to try tapering the cylinder by providing two
          * radii.
          */
         final Vec3 v0 = Vec3.mul(vn, rad, vs[i]);
         v0.x += xOrigin;
         v0.y += yOrigin;
         v0.z += zOrigin;

         final Vec3 v1 = Vec3.mul(vn, rad, vs[j]);
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
            vts[vtLen + i].set(cosa * 0.5f + 0.5f, sina * 0.5f + 0.5f);
         }
      }

      /* Create triangle fan caps with a central point. */
      if ( includeCaps ) {
         final int len1 = vLen + 1;
         final int vtCenterIdx = vts.length - 1;

         /* Convert origin, destination loose floats to vectors. */
         vs[vLen].set(xOrigin, yOrigin, zOrigin);
         vs[len1].set(xDest, yDest, zDest);

         /* Set UV Center. */
         Vec2.uvCenter(vts[vtCenterIdx]);

         /* Set normals. */
         vns[sec].set(-kx, -ky, -kz);
         vns[sec1].set(-kx, -ky, -kz);

         /* Set faces. */
         for ( int i = 0, j = sec; i < sec; ++i, ++j ) {
            final int k = ( i + 1 ) % sec;
            final int m = vtLen + i;
            final int n = vtLen + k;

            final int[][] cap0 = fs[vLen + i];

            cap0[0][0] = vLen;
            cap0[0][1] = vtCenterIdx;
            cap0[0][2] = sec;

            cap0[1][0] = i;
            cap0[1][1] = m;
            cap0[1][2] = sec;

            cap0[2][0] = k;
            cap0[2][1] = n;
            cap0[2][2] = sec;

            final int[][] cap1 = fs[vLen + j];

            cap1[0][0] = len1;
            cap1[0][1] = vtCenterIdx;
            cap1[0][2] = sec1;

            cap1[1][0] = j;
            cap1[1][1] = m;
            cap1[1][2] = sec1;

            cap1[2][0] = sec + k;
            cap1[2][1] = n;
            cap1[2][2] = sec1;
         }
      }

      return target;
   }

   /**
    * A helper function for parsing an OBJ file. Attempts to convert a string
    * to an integer.
    *
    * @param i the string
    *
    * @return the integer
    */
   private static int intFromStr ( final String i ) {

      int target = 0;
      try {
         target = Integer.parseInt(i);
      } catch ( final Exception e ) {
         target = 0;
      }
      return target;
   }

   /**
    * Enumerates which texture (UV) coordinate pattern to use when creating a
    * cube.
    */
   public enum CubeUvProfile {

      /**
       * Cross pattern that wraps around cube.
       */
      CROSS ( ),

      /**
       * Each cube face repeats the same square texture.
       */
      PER_FACE ( );

      /**
       * The default constructor.
       */
      private CubeUvProfile ( ) {}

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
       * Gets the next value in the iterator.
       *
       * @param target the output face
       *
       * @return the value
       *
       * @see Mesh3#getFace(int, Face3)
       */
      public Face3 next ( final Face3 target ) {

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

}
