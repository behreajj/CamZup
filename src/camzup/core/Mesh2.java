package camzup.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
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
    * An array of coordinates.
    */
   public Vec2[] coords;

   /**
    * The texture (UV) coordinates that describe how an image is mapped onto
    * the mesh. Typically in the range [0.0, 1.0] .
    */
   public Vec2[] texCoords;

   /**
    * The default constructor.
    */
   public Mesh2 ( ) {}

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

      this.set(faces, coords, texCoords);
   }

   /**
    * Constructs a copy of the source mesh.
    *
    * @param source the source mesh
    */
   public Mesh2 ( final Mesh2 source ) { this.set(source); }

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
    * object-space dimensions of each coordinate, then uses the frame as a
    * reference for new UVs, such that the shape acts as a mask for the
    * texture (or, the texture fills the shape without repeating).
    *
    * @return this mesh
    *
    * @see Mesh2#accumMinMax(Mesh2, Vec2, Vec2)
    * @see Vec2#resize(Vec2[], int)
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    */
   public Mesh2 calcUvs ( ) {

      final Vec2 dim = new Vec2();
      final Vec2 lb = new Vec2();
      final Vec2 ub = new Vec2();
      lb.set(Float.MAX_VALUE, Float.MAX_VALUE);
      ub.set(Float.MIN_VALUE, Float.MIN_VALUE);
      Mesh2.accumMinMax(this, lb, ub);
      Vec2.sub(ub, lb, dim);

      final int len = this.coords.length;
      this.texCoords = Vec2.resize(this.texCoords, len);

      if ( dim.x == 0.0f || dim.y == 0.0f ) { return this; }
      final float w = dim.x;
      final float h = dim.y;
      final float wInv = 1.0f / w;
      final float hInv = 1.0f / h;
      final float sAsp = w < h ? w / h : 1.0f;
      final float tAsp = w > h ? h / w : 1.0f;

      for ( int i = 0; i < len; ++i ) {
         final Vec2 v = this.coords[i];
         final float sStretch = ( v.x - lb.x ) * wInv;
         final float tStretch = ( v.y - lb.y ) * hInv;
         final float s = ( sStretch - 0.5f ) * sAsp + 0.5f;
         final float t = ( tStretch - 0.5f ) * tAsp + 0.5f;

         final Vec2 vt = this.texCoords[i];
         vt.x = s;
         vt.y = 1.0f - t;
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

      /* Hash map's keys are no longer needed; just values. */
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

      /* Replace old arrays with the new. */
      this.coords = newCoords;
      this.texCoords = newTexCoords;

      /* Sort faces by center. */
      Arrays.sort(this.faces, new SortLoops2(this.coords));

      return this;
   }

   /**
    * Collapses an edge to a point. Unlike
    * {@link Mesh2#deleteVerts(int, int, int)}, inserts a midpoint between
    * edge origin and destination.
    *
    * @param faceIndex the face index
    * @param edgeIndex the edge index
    *
    * @return this mesh
    */
   public Mesh2 collapseEdge ( final int faceIndex, final int edgeIndex ) {

      return this.collapseEdge(faceIndex, edgeIndex, 0.5f);
   }

   /**
    * Collapses an edge to a point. Unlike
    * {@link Mesh2#deleteVerts(int, int, int)}, inserts a newly created vertex
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
    * @see Vec2#append(Vec2[], Vec2)
    * @see Mesh#splice(int[][], int, int, int[][])
    */
   @Experimental
   public Mesh2 collapseEdge ( final int faceIndex, final int edgeIndex,
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

      /* Clamp factor. */
      final float t = Utils.clamp01(fac);
      final float u = 1.0f - t;

      /* Mix coordinates. */
      final Vec2 vOrig = this.coords[orig[0]];
      final Vec2 vDest = this.coords[dest[0]];
      final Vec2 vMidPoint = new Vec2(u * vOrig.x + t * vDest.x, u * vOrig.y + t
         * vDest.y);

      /* Mix texture coordinates. */
      final Vec2 vtOrig = this.texCoords[orig[1]];
      final Vec2 vtDest = this.texCoords[dest[1]];
      final Vec2 vtMidPoint = new Vec2(u * vtOrig.x + t * vtDest.x, u * vtOrig.y
         + t * vtDest.y);

      /* Append new data. */
      this.coords = Vec2.append(this.coords, vMidPoint);
      this.texCoords = Vec2.append(this.texCoords, vtMidPoint);
      this.faces[i] = Mesh.splice(face, j, 2, new int[][] { new int[] {
         vsOldLen, vtsOldLen } });

      return this;
   }

   /**
    * Removes a given number of face indices from this mesh beginning at an
    * index. Does not remove any data associated with the indices.
    *
    * @param faceIndex the index
    *
    * @return this mesh
    *
    * @see Mesh2#deleteFaces(int, int)
    */
   public Mesh2 deleteFace ( final int faceIndex ) {

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
   public Mesh2 deleteFaces ( final int faceIndex, final int count ) {

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
    */
   public Mesh2 deleteVerts ( final int faceIndex, final int vertIndex,
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
      return this.equals(( Mesh2 ) obj);
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
   public Mesh2 extrudeEdge ( final int faceIndex, final int edgeIndex,
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

      final int idxV3 = idxDest[0];
      final int idxVt3 = idxDest[1];

      final int vsOldLen = this.coords.length;
      final int vtsOldLen = this.texCoords.length;

      final int idxV1 = vsOldLen;
      final int idxVt2 = vtsOldLen + 1;

      final int idxV2 = vsOldLen + 1;
      final int idxVt1 = vtsOldLen;

      final Vec2 vOrig = this.coords[idxV0];
      final Vec2 vDest = this.coords[idxV3];

      /*
       * The perpendicular is not normalized so that the created face is square,
       * no matter the number of sides on the polygon.
       */
      final Vec2 vPerp = new Vec2();
      Vec2.sub(vOrig, vDest, vPerp);
      Vec2.perpendicularCCW(vPerp, vPerp);
      Vec2.mul(vPerp, amount, vPerp);

      final Vec2 vNewOrig = new Vec2();
      final Vec2 vNewDest = new Vec2();
      Vec2.add(vOrig, vPerp, vNewOrig);
      Vec2.add(vDest, vPerp, vNewDest);

      final Vec2 vtOrig = this.texCoords[idxVt0];
      final Vec2 vtDest = this.texCoords[idxVt3];

      /* Texture coordinates are CW; coordinates are CCW. */
      final Vec2 vtPerp = new Vec2();
      Vec2.sub(vtOrig, vtDest, vtPerp);
      Vec2.perpendicularCW(vtPerp, vtPerp);

      final Vec2 vtNewOrig = new Vec2();
      final Vec2 vtNewDest = new Vec2();
      Vec2.add(vtOrig, vtPerp, vtNewOrig);
      Vec2.add(vtDest, vtPerp, vtNewDest);

      final int[][][] faceNew = { { { idxV1, idxVt1 }, { idxV2, idxVt2 }, {
         idxV3, idxVt3 }, { idxV0, idxVt0 } } };
      this.coords = Vec2.concat(this.coords, new Vec2[] { vNewOrig, vNewDest });
      this.texCoords = Vec2.concat(this.texCoords, new Vec2[] { vtNewOrig,
         vtNewDest });
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
   public Mesh2 extrudeEdges ( final int faceIndex, final float amount ) {

      final int i = Utils.mod(faceIndex, this.faces.length);
      final int faceLen = this.faces[i].length;
      for ( int j = 0; j < faceLen; ++j ) { this.extrudeEdge(i, j, amount); }

      return this;
   }

   /**
    * Negates the x component of all texture coordinates (u) in the mesh. Does
    * so by subtracting the value from 1.0.
    *
    * @return this mesh
    */
   public Mesh2 flipU ( ) {

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
   public Mesh2 flipV ( ) {

      final int len = this.texCoords.length;
      for ( int i = 0; i < len; ++i ) {
         this.texCoords[i].y = 1.0f - this.texCoords[i].y;
      }
      return this;
   }

   /**
    * Negates the x component of all coordinates in the mesh, then reverses
    * the mesh's faces. Use this instead of {@link Mesh2#scale(Vec2)} with the
    * argument <code>new Vec2(-1.0f, 1.0f)</code>.
    *
    * @return this mesh
    *
    * @see Mesh2#reverseFaces()
    */
   public Mesh2 flipX ( ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) { this.coords[i].x = -this.coords[i].x; }
      this.reverseFaces();
      return this;
   }

   /**
    * Negates the y component of all coordinates in the mesh, then reverses
    * the mesh's faces. Use this instead of {@link Mesh2#scale(Vec2)} with the
    * argument <code>new Vec2(1.0f, -1.0f)</code>.
    *
    * @return this mesh
    *
    * @see Mesh2#reverseFaces()
    */
   public Mesh2 flipY ( ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; ++i ) { this.coords[i].y = -this.coords[i].y; }
      this.reverseFaces();
      return this;
   }

   /**
    * Gets an edge from the mesh.
    *
    * @param faceIdx the face index
    * @param edgeIdx the edge index
    * @param target  the output edge
    *
    * @return the edge
    */
   public Edge2 getEdge ( final int faceIdx, final int edgeIdx,
      final Edge2 target ) {

      final int[][] face = this.faces[Utils.mod(faceIdx, this.faces.length)];
      final int faceLen = face.length;
      final int j = Utils.mod(edgeIdx, faceLen);
      final int[] idcsOrig = face[j];
      final int[] idcsDest = face[ ( j + 1 ) % faceLen];

      return target.set(this.coords[idcsOrig[0]], this.texCoords[idcsOrig[1]],
         this.coords[idcsDest[0]], this.texCoords[idcsDest[1]]);
   }

   /**
    * Gets an array of edges from the mesh.
    *
    * @return the edges array
    */
   public Edge2[] getEdges ( ) { return this.getEdgesDirected(); }

   /**
    * Gets an array of edges from the mesh. Edges are treated as directed, so
    * (origin, destination) and (destination, edge) are considered to be
    * different.
    *
    * @return the edges array
    */
   public Edge2[] getEdgesDirected ( ) {

      Edge2 trial = new Edge2();
      final int facesLen = this.faces.length;
      final ArrayList < Edge2 > result = new ArrayList <>(facesLen * 4);

      for ( int i = 0; i < facesLen; ++i ) {

         final int[][] fs = this.faces[i];
         final int faceLen = fs.length;

         for ( int j = 0; j < faceLen; ++j ) {

            final int[] idcsOrig = fs[j];
            final int[] idcsDest = fs[ ( j + 1 ) % faceLen];

            trial.set(this.coords[idcsOrig[0]], this.texCoords[idcsOrig[1]],
               this.coords[idcsDest[0]], this.texCoords[idcsDest[1]]);

            if ( result.indexOf(trial) < 0 ) {
               result.add(trial);
               trial = new Edge2();
            }
         }
      }

      return result.toArray(new Edge2[result.size()]);
   }

   /**
    * Gets an array of edges from the mesh. Edges are treated as undirected,
    * so (origin, destination) and (destination, edge) are considered to be
    * the same.
    *
    * @return the edges array
    */
   public Edge2[] getEdgesUndirected ( ) {

      final int facesLen = this.faces.length;
      final TreeMap < Integer, Edge2 > result = new TreeMap <>();

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
               result.put(vIdxOrig < vIdxDest ? aHsh : bHsh, new Edge2(
                  this.coords[vIdxOrig], this.texCoords[idcsOrig[1]],
                  this.coords[vIdxDest], this.texCoords[idcsDest[1]]));
            }
         }
      }

      return result.values().toArray(new Edge2[result.size()]);
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
      final int faceLen = face.length;
      final Vert2[] vertices = new Vert2[faceLen];

      for ( int j = 0; j < faceLen; ++j ) {
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

      final int facesLen = this.faces.length;
      final Face2[] result = new Face2[facesLen];

      for ( int i = 0; i < facesLen; ++i ) {

         final int[][] fs0 = this.faces[i];
         final int faceLen = fs0.length;
         final Vert2[] verts = new Vert2[faceLen];

         for ( int j = 0; j < faceLen; ++j ) {

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
    * @param faceIndex the face index
    * @param vertIndex the vertex index
    * @param target    the output vertex
    *
    * @return the vertex
    */
   public Vert2 getVertex ( final int faceIndex, final int vertIndex,
      final Vert2 target ) {

      final int[][] face = this.faces[Utils.mod(faceIndex, this.faces.length)];
      final int[] vertidcs = face[Utils.mod(vertIndex, face.length)];
      return target.set(this.coords[vertidcs[0]], this.texCoords[vertidcs[1]]);
   }

   /**
    * Gets an array of vertices from the mesh.
    *
    * @return the vertices
    */
   public Vert2[] getVertices ( ) {

      Vert2 trial = new Vert2();
      final int facesLen = this.faces.length;
      final ArrayList < Vert2 > result = new ArrayList <>(facesLen);

      for ( int i = 0; i < facesLen; ++i ) {

         final int[][] fs = this.faces[i];
         final int faceLen = fs.length;

         for ( int j = 0; j < faceLen; ++j ) {

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

      int hash = super.hashCode();
      hash = hash * IUtils.HASH_MUL ^ Arrays.hashCode(this.coords);
      return hash * IUtils.HASH_MUL ^ Arrays.deepHashCode(this.faces);
   }

   /**
    * Insets a face by calculating its center then easing from the face's
    * vertices toward the center by 0.5.
    *
    * @param faceIndex the face index
    *
    * @return the new face indices
    */
   public Mesh2 insetFace ( final int faceIndex ) {

      return this.insetFace(faceIndex, 0.5f);
   }

   /**
    * Insets a face by calculating its center then easing from the face's
    * vertices toward the center by the factor. The factor is expected to be
    * in the range [0.0, 1.0] . When it is less than 0.0, the face remains
    * unchanged. When it is greater than 1.0, the face is subdivided by
    * center.
    *
    * @param faceIndex the face index
    * @param fac       the inset amount
    *
    * @return the new face indices
    */
   @Experimental
   public Mesh2 insetFace ( final int faceIndex, final float fac ) {

      if ( fac <= 0.0f ) { return this; }
      if ( fac >= 1.0f ) { return this.subdivFaceFan(faceIndex); }

      /* Validate face index, find face. */
      final int facesLen = this.faces.length;
      final int i = Utils.mod(faceIndex, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      final int vsOldLen = this.coords.length;
      final int vtsOldLen = this.texCoords.length;
      final int[][][] fsNew = new int[faceLen + 1][4][2];
      final int[][] centerFace = fsNew[faceLen] = new int[faceLen][2];

      /* Find center. */
      final Vec2 vCenter = new Vec2();
      final Vec2 vtCenter = new Vec2();
      for ( int j = 0; j < faceLen; ++j ) {

         /* Sum centers. */
         final int[] vertCurr = face[j];
         Vec2.add(vCenter, this.coords[vertCurr[0]], vCenter);
         Vec2.add(vtCenter, this.texCoords[vertCurr[1]], vtCenter);
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

         final int[][] fNew = fsNew[j];
         final int[] n0 = fNew[0];
         n0[0] = vCornerIdx;   n0[1] = vtCornerIdx;
         final int[] n1 = fNew[1];
         n1[0] = vertNext[0];  n1[1] = vertNext[1];
         final int[] n2 = fNew[2];
         n2[0] = vsOldLen + k; n2[1] = vtsOldLen + k;
         final int[] n3 = fNew[3];
         n3[0] = vSubdivIdx;   n3[1] = vtSubdivIdx;
         /* @formatter:on */

         final int[] centerVert = centerFace[j];
         centerVert[0] = vSubdivIdx;
         centerVert[1] = vtSubdivIdx;
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
         int k = 0;
         final int facesLen = this.faces.length;
         for ( int j = 0; j < facesLen; ++j ) {
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
    * @see Mesh2#accumMinMax(Mesh2, Vec2, Vec2)
    * @see Transform2#rotateTo(float)
    * @see Utils#div(float, float)
    * @see Utils#max(float, float)
    * @see Vec2#mul(Vec2, float, Vec2)
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    */
   public Mesh2 reframe ( final Transform2 tr ) {

      tr.locPrev.set(tr.location);
      tr.scalePrev.set(tr.scale);

      final Vec2 dim = tr.scale;
      final Vec2 lb = tr.location;
      final Vec2 ub = new Vec2(Float.MIN_VALUE, Float.MIN_VALUE);
      lb.set(Float.MAX_VALUE, Float.MAX_VALUE);
      Mesh2.accumMinMax(this, lb, ub);
      Vec2.sub(ub, lb, dim);

      lb.x = 0.5f * ( lb.x + ub.x );
      lb.y = 0.5f * ( lb.y + ub.y );
      final float scl = Utils.div(1.0f, Utils.max(dim.x, dim.y));

      final int vsLen = this.coords.length;
      for ( int i = 0; i < vsLen; ++i ) {
         final Vec2 c = this.coords[i];
         Vec2.sub(c, lb, c);
         Vec2.mul(c, scl, c);
      }

      tr.rotateTo(0.0f);

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

      final double radd = radians;
      final float cosa = ( float ) Math.cos(radd);
      final float sina = ( float ) Math.sin(radd);

      final int vsLen = this.coords.length;
      for ( int i = 0; i < vsLen; ++i ) {
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
   public Mesh2 scale ( final float scale ) {

      if ( scale != 0.0f ) {
         final int vsLen = this.coords.length;
         for ( int i = 0; i < vsLen; ++i ) {
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
    * @see Vec2#all(Vec2)
    * @see Vec2#hadamard(Vec2, Vec2, Vec2)
    */
   public Mesh2 scale ( final Vec2 scale ) {

      if ( Vec2.all(scale) ) {
         final int vsLen = this.coords.length;
         for ( int i = 0; i < vsLen; ++i ) {
            final Vec2 c = this.coords[i];
            Vec2.hadamard(c, scale, c);
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
      final Vec2[] vsSrc = source.coords;
      final int vsLen = vsSrc.length;
      this.coords = Vec2.resize(this.coords, vsLen);
      for ( int i = 0; i < vsLen; ++i ) { this.coords[i].set(vsSrc[i]); }

      /* Copy texture coordinates. */
      final Vec2[] vtsSrc = source.texCoords;
      final int vtsLen = vtsSrc.length;
      this.texCoords = Vec2.resize(this.texCoords, vtsLen);
      for ( int i = 0; i < vtsLen; ++i ) { this.texCoords[i].set(vtsSrc[i]); }

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
    * Subdivides an edge by a cut at its midpoint. Does not distinguish
    * between interior edges, which have a complement elsewhere, and border
    * edges; for that reason this works best with NGONs.
    *
    * @param faceIndex the face index
    * @param edgeIndex the edge index
    *
    * @return this mesh
    */
   public Mesh2 subdivEdge ( final int faceIndex, final int edgeIndex ) {

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
    */
   @Experimental
   public Mesh2 subdivEdge ( final int faceIndex, final int edgeIndex,
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
      final Vec2 vOrig = this.coords[vert0Idx[0]];
      final Vec2 vtOrig = this.texCoords[vert0Idx[1]];

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

         vsNew[k] = new Vec2(u * vOrig.x + step * vDest.x, u * vOrig.y + step
            * vDest.y);
         vtsNew[k] = new Vec2(u * vtOrig.x + step * vtDest.x, u * vtOrig.y
            + step * vtDest.y);

         final int[] fNew = fsNew[k];
         fNew[0] = vsOldLen + k;
         fNew[1] = vtsOldLen + k;
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

         final int[][] fNew = fsNew[j];
         final int[] n0 = fNew[0];
         n0[0] = vCenterIdx;   n0[1] = vtCenterIdx;
         final int[] n1 = fNew[1];
         n1[0] = vsOldLen + j; n1[1] = vtsOldLen + j;
         final int[] n2 = fNew[2];
         n2[0] = vNextIdx;     n2[1] = vtNextIdx;
         final int[] n3 = fNew[3];
         n3[0] = vsOldLen + k; n3[1] = vtsOldLen + k;
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

         /* Sum vertex for center. */
         Vec2.add(vCenter, this.coords[vCurrIdx], vCenter);
         Vec2.add(vtCenter, this.texCoords[vtCurrIdx], vtCenter);

         /* @formatter:off */
         final int[][] fNew = fsNew[j];
         final int[] n0 = fNew[0];
         n0[0] = vCenterIdx;  n0[1] = vtCenterIdx;
         final int[] n1 = fNew[1];
         n1[0] = vCurrIdx;    n1[1] = vtCurrIdx;
         final int[] n2 = fNew[2];
         n2[0] = vertNext[0]; n2[1] = vertNext[1];
         /* @formatter:on */
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
      final int[][][] fsNew = new int[faceLen + 1][3][2];
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

         /* Update peripheral face. */
         final int[][] fNew = fsNew[j];
         final int[] n0 = fNew[0];
         n0[0] = vSubdivIdx;   n0[1] = vtSubdivIdx;
         final int[] n1 = fNew[1];
         n1[0] = vNextIdx;     n1[1] = vtNextIdx;
         final int[] n2 = fNew[2];
         n2[0] = vsOldLen + k; n2[1] = vtsOldLen + k;
         /* @formatter:on */

         /* Update vertex of central face. */
         final int[] centerVert = centerFace[j];
         centerVert[0] = vSubdivIdx;
         centerVert[1] = vtSubdivIdx;
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
    * @see Mesh2#subdivFaceInscribe(int)
    */
   public Mesh2 subdivFacesInscribe ( final int itr ) {

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
   public String toObjString ( ) { return this.toObjString(1, 1, 1, true); }

   /**
    * Renders the mesh as a string following the Wavefront OBJ file format.
    * The index offsets specify where the mesh's data begin; OBJ file indices
    * begin at 1, not 0. The mesh is considered a group, 'g', not an object,
    * 'o'.
    *
    * @param vIdx   coordinate index offset
    * @param vtIdx  texture coordinate index offset
    * @param vnIdx  normal index offset
    * @param flipvs whether to subtract y from 1.0
    *
    * @return the string
    */
   public String toObjString ( final int vIdx, final int vtIdx, final int vnIdx,
      final boolean flipvs ) {

      return this.toObjString(new StringBuilder(1024), vIdx, vtIdx, vnIdx,
         flipvs).toString();
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
    * @see Mesh2#accumMinMax(Mesh2, Vec2, Vec2)
    * @see Mesh2#translate(Vec2)
    * @see Transform2#rotateTo(float)
    * @see Transform2#scaleTo(float)
    * @see Vec2#negate(Vec2, Vec2)
    */
   public Mesh2 toOrigin ( final Transform2 tr ) {

      final Vec2 lb = new Vec2(Float.MAX_VALUE, Float.MAX_VALUE);
      final Vec2 ub = new Vec2(Float.MIN_VALUE, Float.MIN_VALUE);
      Mesh2.accumMinMax(this, lb, ub);

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
    * Renders the curve as a string containing an SVG element.
    *
    * @param zoom scaling transform
    *
    * @return the SVG string
    */
   @Override
   public String toSvgElm ( final float zoom ) {

      final StringBuilder svgp = new StringBuilder(1024);
      MaterialSolid.defaultSvgMaterial(svgp, zoom);
      this.toSvgPath(svgp, ISvgWritable.DEFAULT_WINDING_RULE);
      svgp.append("</g>\n");
      return svgp.toString();
   }

   /**
    * Renders this mesh as an SVG string. A default material renders the
    * mesh's fill and stroke. The background of the SVG is transparent.
    *
    * @return the SVG string
    */
   @Override
   public String toSvgString ( ) {

      return this.toSvgString(ISvgWritable.DEFAULT_ORIGIN_X,
         ISvgWritable.DEFAULT_ORIGIN_Y, ISvgWritable.DEFAULT_WIDTH,
         ISvgWritable.DEFAULT_HEIGHT, ISvgWritable.DEFAULT_WIDTH,
         ISvgWritable.DEFAULT_HEIGHT);
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
    *
    * @see Transform2#mulPoint(Transform2, Vec2, Vec2)
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

      final int vsLen = this.coords.length;
      for ( int i = 0; i < vsLen; ++i ) {
         final Vec2 c = this.coords[i];
         Vec2.add(c, v, c);
      }

      return this;
   }

   /**
    * An internal helper function to format a mesh as a Python tuple, then
    * append it to a {@link StringBuilder}. Appends a z component to promote
    * the vector to 3D.
    *
    * @param pyCd         the string builder
    * @param includeEdges whether to include edge index data
    * @param includeUvs   whether or not to include UVs
    * @param z            z offset
    *
    * @return the string builder
    */
   @Experimental
   StringBuilder toBlenderCode ( final StringBuilder pyCd,
      final boolean includeEdges, final boolean includeUvs, final float z ) {

      final int vsLen = this.coords.length;
      final int vsLast = vsLen - 1;
      final int facesLen = this.faces.length;
      final int facesLast = facesLen - 1;

      pyCd.append("{\"name\": \"");
      if ( Character.isDigit(this.name.charAt(0)) ) { pyCd.append("id"); }
      pyCd.append(this.name);
      pyCd.append("\", \"material_index\": ");
      pyCd.append(this.materialIndex);
      pyCd.append(", \"vertices\": [");

      for ( int i = 0; i < vsLast; ++i ) {
         this.coords[i].toBlenderCode(pyCd, z);
         pyCd.append(',').append(' ');
      }
      this.coords[vsLast].toBlenderCode(pyCd, z);

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

               if ( edgesList.indexOf(query) < 0 && edgesList.indexOf(reverse)
                  < 0 ) {
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

      pyCd.append(']').append('}');
      return pyCd;
   }

   /**
    * Internal helper method that appends a representation of this mesh in the
    * Wavefront OBJ file format to a {@link StringBuilder}.
    *
    * @param objs   the string builder
    * @param vIdx   coordinate index offset
    * @param vtIdx  texture coordinate index offset
    * @param vnIdx  normal index offset
    * @param flipvs whether to subtract y from 1.0
    *
    * @return the string builder
    */
   StringBuilder toObjString ( final StringBuilder objs, final int vIdx,
      final int vtIdx, final int vnIdx, final boolean flipvs ) {

      final int vsLen = this.coords.length;
      final int vtsLen = this.texCoords.length;
      final int facesLen = this.faces.length;

      /*
       * Append comment listing the number of coordinates, texture coordinates
       * and faces.
       */
      objs.append("\n# v: ");
      objs.append(vsLen);
      objs.append(", vt: ");
      objs.append(vtsLen);
      objs.append(", vn: 1, f: ");
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
         this.coords[i].toObjString(objs, false);
         objs.append(" 0.0 \n");
      }
      objs.append('\n');

      /* Write texture coordinates. */
      for ( int i = 0; i < vtsLen; ++i ) {
         objs.append("vt ");
         this.texCoords[i].toObjString(objs, flipvs);
         objs.append('\n');
      }

      /* Append a single normal. */
      objs.append("\nvn 0.0 0.0 1.0\n\n");
      objs.append("s off\n\n");

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
            objs.append(vnIdx);
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
      Vec2.toString(sb, this.coords, places);
      sb.append(", texCoords: ");
      Vec2.toString(sb, this.texCoords, places);

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

               /* 2 indices: coordinate & texture coordinate. */
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
    * Internal helper function to append a mesh path to a
    * {@link StringBuilder}; the id is written to the path's id. The fill rule
    * may be either <code>"evenodd"</code> or <code>"nonzero"</code>
    * (default).
    *
    * @param svgp     the string builder
    * @param fillRule the fill rule
    *
    * @return the string builder.
    */
   StringBuilder toSvgPath ( final StringBuilder svgp, final String fillRule ) {

      final Vec2[] vs = this.coords;

      svgp.append("<path id=\"");
      svgp.append(this.name.toLowerCase());
      svgp.append("\" class=\"");
      svgp.append(this.getClass().getSimpleName().toLowerCase());
      svgp.append("\" fill-rule=\"");
      svgp.append(fillRule);
      svgp.append("\" d=\"");

      final int facesLen = this.faces.length;
      for ( int i = 0; i < facesLen; ++i ) {
         final int[][] face = this.faces[i];
         final int faceLen = face.length;

         svgp.append('M');
         svgp.append(' ');
         vs[face[0][0]].toSvgString(svgp, ' ');
         svgp.append(' ');

         for ( int j = 1; j < faceLen; ++j ) {
            svgp.append('L');
            svgp.append(' ');
            vs[face[j][0]].toSvgString(svgp, ' ');
            svgp.append(' ');
         }

         svgp.append('Z');
         svgp.append(' ');
      }

      svgp.append("\" />\n");
      return svgp;
   }

   /**
    * Tests this mesh for equivalence with another.
    *
    * @param mesh2 the mesh
    *
    * @return the evaluation
    */
   protected boolean equals ( final Mesh2 mesh2 ) {

      return Arrays.equals(this.coords, mesh2.coords) && Arrays.deepEquals(
         this.faces, mesh2.faces);
   }

   /**
    * The default texture coordinate (UV) profile to use when making a cube.
    */
   public static final UvProfile.Arc DEFAULT_ARC_UV_PROFILE
      = UvProfile.Arc.BOUNDS;

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
         Mesh2.DEFAULT_POLY_TYPE, Mesh2.DEFAULT_ARC_UV_PROFILE, target);
   }

   /**
    * Creates an arc from a start and stop angle. The granularity of the
    * approximation is dictated by the number of sectors in a complete circle.
    * The thickness of the arc is described by the oculus. Useful where
    * sectors may be faster than the Bezier curves of
    * {@link Curve2#arc(float, float, float, camzup.core.ArcMode, Curve2)} or
    * where there is an issue rendering strokes.
    *
    * @param startAngle the start angle
    * @param stopAngle  the stop angle
    * @param oculus     the size of the opening
    * @param sectors    number of sectors in a circle
    * @param poly       the polygon type
    * @param profile    the uv profile
    * @param target     the output mesh
    *
    * @return the arc
    */
   public static Mesh2 arc ( final float startAngle, final float stopAngle,
      final float oculus, final int sectors, final PolyType poly,
      final UvProfile.Arc profile, final Mesh2 target ) {

      target.name = "Arc";

      final float a1 = Utils.mod1(startAngle * IUtils.ONE_TAU);
      final float b1 = Utils.mod1(stopAngle * IUtils.ONE_TAU);
      final float arcLen1 = Utils.mod1(b1 - a1);
      final float oculFac = Utils.clamp(oculus, IUtils.EPSILON, 1.0f
         - IUtils.EPSILON);
      if ( arcLen1 < 0.00139d ) {
         Mesh2.polygon(sectors, PolyType.NGON, target);
         target.insetFace(0, 1.0f - oculFac);
         target.deleteFace(-1);
         if ( poly == PolyType.TRI ) { target.triangulate(); }
         return target;
      }

      final int sctCount = ( int ) Math.ceil(1.0d + ( sectors < 3 ? 3.0d
         : sectors ) * arcLen1);
      final int sctCount2 = sctCount + sctCount;
      final Vec2[] vs = target.coords = Vec2.resize(target.coords, sctCount2);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
         sctCount2);

      final float oculRad = oculFac * 0.5f;

      final float toStep = 1.0f / ( sctCount - 1.0f );
      final float origAngle = IUtils.TAU * a1;
      final float destAngle = IUtils.TAU * ( a1 + arcLen1 );

      for ( int k = 0; k < sctCount; ++k ) {
         final float step = k * toStep;
         final float theta = ( 1.0f - step ) * origAngle + step * destAngle;

         final double radd = theta;
         final float cosa = ( float ) Math.cos(radd);
         final float sina = ( float ) Math.sin(radd);

         final int i = k + k;
         vs[i].set(0.5f * cosa, 0.5f * sina);
         vs[i + 1].set(oculRad * cosa, oculRad * sina);
      }

      switch ( profile ) {

         case CLIP: {
            final float c1 = a1 + arcLen1;
            for ( int k = 0; k < sctCount; ++k ) {
               final float step = k * toStep;
               final float v = ( 1.0f - step ) * c1 + step * a1;
               final int i = k + k;
               vts[i].set(1.0f, v);
               vts[i + 1].set(0.0f, v);
            }
         }
            break;

         case STRETCH: {
            for ( int k = 0; k < sctCount; ++k ) {
               final float step = 1.0f - k * toStep;
               final int i = k + k;
               vts[i].set(1.0f, step);
               vts[i + 1].set(0.0f, step);
            }
         }
            break;

         case BOUNDS:
         default: {
            for ( int k = 0; k < sctCount; ++k ) {
               final int i = k + k;
               final Vec2 v0 = vs[i];
               vts[i].set(v0.x + 0.5f, 0.5f - v0.y);

               final int j = i + 1;
               final Vec2 v1 = vs[j];
               vts[j].set(v1.x + 0.5f, 0.5f - v1.y);
            }
         }
      }

      int len;

      switch ( poly ) {

         case NGON: {
            len = sctCount2;
            final int last = len - 1;
            target.faces = new int[1][len][2];

            for ( int i = 0, j = 0; i < sctCount; ++i, j += 2 ) {
               final int[] forward = target.faces[0][i];
               forward[0] = j;
               forward[1] = j;

               final int k = sctCount + i;
               final int m = last - j;

               final int[] backward = target.faces[0][k];
               backward[0] = m;
               backward[1] = m;
            }
         }
            break;

         case QUAD: {
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
         }
            break;

         case TRI:

         default: {
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
         IMesh.DEFAULT_CIRCLE_SECTORS, Mesh2.DEFAULT_POLY_TYPE,
         Mesh2.DEFAULT_ARC_UV_PROFILE, target);
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
         Mesh2.DEFAULT_POLY_TYPE, Mesh2.DEFAULT_ARC_UV_PROFILE, target);
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
         IMesh.DEFAULT_CIRCLE_SECTORS, Mesh2.DEFAULT_POLY_TYPE,
         Mesh2.DEFAULT_ARC_UV_PROFILE, target);
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
         IMesh.DEFAULT_CIRCLE_SECTORS, Mesh2.DEFAULT_POLY_TYPE,
         Mesh2.DEFAULT_ARC_UV_PROFILE, target);
   }

   /**
    * Calculates an Axis-Aligned Bounding Box (AABB) encompassing the mesh.
    *
    * @param mesh   the mesh
    * @param target the output dimensions
    *
    * @return the dimensions
    */
   public static Bounds2 calcBounds ( final Mesh2 mesh, final Bounds2 target ) {

      target.set(Float.MAX_VALUE, Float.MIN_VALUE);
      Mesh2.accumMinMax(mesh, target.min, target.max);
      return target;
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

      Mesh2.polygon(IMesh.DEFAULT_CIRCLE_SECTORS, Mesh2.DEFAULT_POLY_TYPE,
         target);
      target.name = "Circle";
      return target;
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
   public static Mesh2 circle ( final PolyType poly, final Mesh2 target ) {

      Mesh2.polygon(IMesh.DEFAULT_CIRCLE_SECTORS, poly, target);
      target.name = "Circle";
      return target;
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

      final String namePrefix = source.name + ".";
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

         final Mesh2 mesh = new Mesh2(fsTrg, vsTrg, vtsTrg);
         mesh.name = namePrefix + Utils.toPadded(i, 3);
         meshes[i] = mesh;
      }

      return meshes;
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
    */
   public static Mesh2 fromCurve2 ( final Curve2 source, final int resolution,
      final float colinearTol, final Mesh2 target ) {

      final ArrayList < Vec2 > points = new ArrayList <>(64);
      Mesh2.fromCurve2(source, resolution, colinearTol, points, new Vec2(),
         new Vec2());
      final int pointsLen = points.size();
      target.coords = points.toArray(new Vec2[pointsLen]);
      final int[][][] fs = target.faces = new int[1][pointsLen][2];
      final int[][] f = fs[0];
      for ( int i = 0; i < pointsLen; ++i ) { f[i][0] = i; }

      target.calcUvs();
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
    */
   public static Mesh2 fromCurve2 ( final Curve2[] arr, final int resolution,
      final float colinearTol, final Mesh2 target ) {

      final int curvesLen = arr.length;
      final ArrayList < Vec2 > points = new ArrayList <>(64);
      final Vec2 dir0 = new Vec2();
      final Vec2 dir1 = new Vec2();
      final int[][][] fs = target.faces = new int[curvesLen][][];
      int prevIdx = 0;
      int pointsLen = 0;
      for ( int i = 0; i < curvesLen; ++i ) {
         Mesh2.fromCurve2(arr[i], resolution, colinearTol, points, dir0, dir1);

         pointsLen = points.size();
         final int fLen = pointsLen - prevIdx;
         final int[][] f = fs[i] = new int[fLen][2];
         for ( int j = 0; j < fLen; ++j ) { f[j][0] = prevIdx + j; }
         prevIdx = pointsLen;
      }

      target.coords = points.toArray(new Vec2[pointsLen]);
      target.calcUvs();
      return target;
   }

   /**
    * Creates a mesh from a series of points. Does not check for vertex
    * winding, so the order of each point should match its intended vertex
    * order. There should be at least 3 points in the array.
    *
    * @param points the points
    * @param target the output mesh
    *
    * @return the mesh
    */
   public static Mesh2 fromPoints ( final Vec2[] points, final Mesh2 target ) {

      final int len = points.length;
      if ( len < 3 ) { return target; }
      final int[][][] fs = target.faces = new int[1][len][2];
      final int[][] ngon = fs[0];
      final Vec2[] vs = target.coords = Vec2.resize(target.coords, len);
      for ( int i = 0; i < len; ++i ) {
         vs[i].set(points[i]);
         ngon[i][0] = i;
      }
      target.calcUvs();
      target.name = "Points";
      return target;
   }

   /**
    * Generates a grid of hexagons arranged in rings around a central cell.
    * The number of cells follows the formula n = 1 + (rings - 1) * 3 * rings,
    * meaning 1 ring : 1 cell, 2 rings: 7 cells, 3 rings: 19 cells, 4 rings:
    * 37 cells and so on. See <a href=
    * "https://www.redblobgames.com/grids/hexagons/implementation.html">Red
    * Blob Games' Implementation of Hex Grids</a> .
    *
    * @param rings      the number of rings
    * @param cellRadius the cell radius
    * @param margin     the margin between cells
    * @param target     the output mesh
    *
    * @return the hexagon grid
    */
   public static Mesh2 gridHex ( final int rings, final float cellRadius,
      final float margin, final Mesh2 target ) {

      target.name = "Grid.Hexagon";

      final int vRings = rings < 1 ? 1 : rings;
      final float vRad = Utils.max(IUtils.EPSILON, cellRadius);
      final float vMrg = Utils.clamp(margin, 0.0f, vRad - IUtils.EPSILON);

      final float extent = IUtils.SQRT_3 * vRad;
      final float halfExt = extent * 0.5f;

      final float rad15 = vRad * 1.5f;
      final float padRad = vRad - vMrg;
      final float halfRad = padRad * 0.5f;
      final float radrt32 = padRad * IUtils.SQRT_3_2;

      final int vRingsn1 = vRings - 1;

      /* Hard code texture coordinates. */
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords, 6);
      vts[0].set(0.5f, 1.0f);
      vts[1].set(0.0669873f, 0.75f);
      vts[2].set(0.0669873f, 0.25f);
      vts[3].set(0.5f, 0.0f);
      vts[4].set(0.9330127f, 0.25f);
      vts[5].set(0.9330127f, 0.75f);

      final int fsLen = 1 + vRingsn1 * vRings * 3;
      final Vec2[] vs = target.coords = Vec2.resize(target.coords, fsLen * 6);
      final int[][][] fs = target.faces = new int[fsLen][6][2];

      int vIdx = 0;
      int fIdx = 0;
      for ( int i = -vRingsn1; i <= vRingsn1; ++i ) {
         final boolean igt0 = i > 0;
         final int jMin = igt0 ? -vRingsn1 : -vRingsn1 - i;
         final int jMax = igt0 ? vRingsn1 - i : vRingsn1;
         final float iExt = i * extent;
         // jLen = (vRings * 2 - 1) + or - (i > 0 ? i : -i)

         for ( int j = jMin; j <= jMax; ++j ) {
            final float jf = j;
            final float x = iExt + jf * halfExt;
            final float y = jf * rad15;

            final float left = x - radrt32;
            final float right = x + radrt32;
            final float top = y + halfRad;
            final float bottom = y - halfRad;

            /* @formatter:off */
            vs[vIdx    ].set(x    , y + padRad);
            vs[vIdx + 1].set(left , top);
            vs[vIdx + 2].set(left , bottom);
            vs[vIdx + 3].set(x    , y - padRad);
            vs[vIdx + 4].set(right, bottom);
            vs[vIdx + 5].set(right, top   );

            final int[][] f = fs[fIdx];
            final int[] vert0 = f[0]; vert0[0] = vIdx    ;
            final int[] vert1 = f[1]; vert1[0] = vIdx + 1; vert1[1] = 1;
            final int[] vert2 = f[2]; vert2[0] = vIdx + 2; vert2[1] = 2;
            final int[] vert3 = f[3]; vert3[0] = vIdx + 3; vert3[1] = 3;
            final int[] vert4 = f[4]; vert4[0] = vIdx + 4; vert4[1] = 4;
            final int[] vert5 = f[5]; vert5[0] = vIdx + 5; vert5[1] = 5;

            ++fIdx;
            vIdx += 6;
            /* @formatter:on */
         }
      }

      return target;
   }

   /**
    * Generates a grid of hexagons arranged in rings around a central cell.
    * The number of cells follows the formula n = 1 + (rings - 1) * 3 * rings.
    *
    * @param rings      the number of rings
    * @param cellRadius the cell radius
    * @param target     the output mesh
    *
    * @return the hexagon grid
    */
   public static Mesh2 gridHex ( final int rings, final float cellRadius,
      final Mesh2 target ) {

      return Mesh2.gridHex(rings, cellRadius, 0.0f, target);
   }

   /**
    * Generates a grid of hexagons arranged in rings around a central cell.
    * The number of cells follows the formula n = 1 + (rings - 1) * 3 * rings.
    *
    * @param rings  the number of rings
    * @param target the output mesh
    *
    * @return the hexagon grid
    */
   public static Mesh2 gridHex ( final int rings, final Mesh2 target ) {

      return Mesh2.gridHex(rings, 0.5f, target);
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
   public static Mesh2[] groupByMaterial ( final Mesh2[] meshes ) {

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
         final Vec2[] vsTrg = new Vec2[vsLen];
         for ( int j = 0; j < vsLen; ++j ) { vsTrg[j] = new Vec2(vsSrc[j]); }

         /* Copy source texture coordinates. */
         final Vec2[] vtsSrc = source.texCoords;
         final int vtsLen = vtsSrc.length;
         final Vec2[] vtsTrg = new Vec2[vtsLen];
         for ( int j = 0; j < vtsLen; ++j ) { vtsTrg[j] = new Vec2(vtsSrc[j]); }

         /* Concatenated indices need to be offset by current data lengths. */
         final int vsTrgLen = current.coords.length;
         final int vtsTrgLen = current.texCoords.length;

         /* Copy source face indices. */
         final int[][][] fsSrc = source.faces;
         final int fsLen = fsSrc.length;
         final int[][][] fsTrg = new int[fsLen][][];
         for ( int j = 0; j < fsLen; ++j ) {
            final int[][] fSrc = fsSrc[j];
            final int fLen = fSrc.length;
            final int[][] fTrg = fsTrg[j] = new int[fLen][2];
            for ( int k = 0; k < fLen; ++k ) {
               final int[] vertSrc = fSrc[k];
               final int[] vertTrg = fTrg[k];
               vertTrg[0] = vsTrgLen + vertSrc[0];
               vertTrg[1] = vtsTrgLen + vertSrc[1];
            }
         }

         /* Concatenate copies with current data. */
         current.coords = Vec2.concat(current.coords, vsTrg);
         current.texCoords = Vec2.concat(current.texCoords, vtsTrg);
         current.faces = Mesh.splice(current.faces, current.faces.length, 0,
            fsTrg);
      }

      /* Convert dictionary values to an array; clean meshes of excess data. */
      final Mesh2[] result = dict.values().toArray(new Mesh2[dict.size()]);
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
   public static Mesh2 groupData ( final Collection < Mesh2 > coll,
      final Mesh2 target ) {

      return Mesh2.groupData(coll.toArray(new Mesh2[coll.size()]), target);
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
   public static Mesh2 groupData ( final Mesh2 a, final Mesh2 b,
      final Mesh2 target ) {

      return Mesh2.groupData(new Mesh2[] { a, b }, target);
   }

   /**
    * Merges the data of an array of meshes together into one mesh.
    *
    * @param arr    the array
    * @param target the output mesh
    *
    * @return the merger
    */
   public static Mesh2 groupData ( final Mesh2[] arr, final Mesh2 target ) {

      /* Sum lengths. */
      int vsTotal = 0;
      int vtsTotal = 0;
      int fsTotal = 0;
      final int collLen = arr.length;
      for ( int i = 0; i < collLen; ++i ) {
         final Mesh2 m = arr[i];
         vsTotal += m.coords.length;
         vtsTotal += m.texCoords.length;
         fsTotal += m.faces.length;
      }

      /* Resize target data. */
      target.coords = Vec2.resize(target.coords, vsTotal);
      target.texCoords = Vec2.resize(target.texCoords, vtsTotal);

      /* Cache target shortcuts. */
      final Vec2[] vsTrg = target.coords;
      final Vec2[] vtsTrg = target.texCoords;
      final int[][][] fsTrg = new int[fsTotal][][];

      /* Offset indices in merged data. */
      int vsCursor = 0;
      int vtsCursor = 0;
      int fsCursor = 0;

      /* Copy data. */
      for ( int i = 0; i < collLen; ++i ) {
         final Mesh2 m = arr[i];

         /* Copy coordinates. */
         final Vec2[] vsSrc = m.coords;
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

         /* Add new offsets to faces. */
         final int[][][] fsSrc = m.faces;
         final int fsLen = fsSrc.length;
         for ( int j = 0; j < fsLen; ++j ) {
            final int[][] fSrc = fsSrc[j];
            final int fLen = fSrc.length;
            final int[][] fTrg = fsTrg[fsCursor + j] = new int[fLen][2];
            for ( int k = 0; k < fLen; ++k ) {
               final int[] vertSrc = fSrc[k];
               final int[] vertTrg = fTrg[k];

               vertTrg[0] = vertSrc[0] + vsCursor;
               vertTrg[1] = vertSrc[1] + vtsCursor;
            }
         }

         /* Add length of individual mesh to offsets. */
         vsCursor += vsSrcLen;
         vtsCursor += vtsSrcLen;
         fsCursor += fsLen;
      }

      /* Update faces and return. */
      target.faces = fsTrg;
      return target;
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
   public static Mesh2 plane ( final int cols, final int rows,
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
   public static Mesh2 plane ( final int cols, final int rows,
      final PolyType poly, final Mesh2 target ) {

      target.name = "Plane";

      final int rVal = rows < 1 ? 1 : rows;
      final int cVal = cols < 1 ? 1 : cols;
      final int rVal1 = rVal + 1;
      final int cVal1 = cVal + 1;
      final int fLen1 = rVal1 * cVal1;

      final Vec2[] vs = target.coords = Vec2.resize(target.coords, fLen1);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
         fLen1);

      /* Set coordinates and texture coordinates. */
      final float iToStep = 1.0f / rVal;
      final float jToStep = 1.0f / cVal;
      for ( int k = 0; k < fLen1; ++k ) {
         final float jStep = k % cVal1 * jToStep;
         final float iStep = k / cVal1 * iToStep;
         vs[k].set(jStep - 0.5f, iStep - 0.5f);
         vts[k].set(jStep, 1.0f - iStep);
      }

      /* Set faces. */
      int[][][] fs;
      final int fLen = rVal * cVal;

      switch ( poly ) {

         case NGON:

         case QUAD: {
            fs = target.faces = new int[fLen][4][2];
            for ( int k = 0; k < fLen; ++k ) {
               final int i = k / cVal;
               final int j = k % cVal;

               final int cOff0 = i * cVal1;
               // final int cOff1 = cOff0 + cVal1;

               final int c00 = cOff0 + j;
               final int c10 = c00 + 1;
               final int c01 = cOff0 + cVal1 + j;
               final int c11 = c01 + 1;

               final int[][] f = fs[k];

               final int[] vert0 = f[0];
               vert0[0] = c00;
               vert0[1] = c00;

               final int[] vert1 = f[1];
               vert1[0] = c10;
               vert1[1] = c10;

               final int[] vert2 = f[2];
               vert2[0] = c11;
               vert2[1] = c11;

               final int[] vert3 = f[3];
               vert3[0] = c01;
               vert3[1] = c01;
            }
         }
            break;

         case TRI:

         default: {
            fs = target.faces = new int[fLen + fLen][3][2];
            for ( int m = 0, k = 0; k < fLen; ++k, m += 2 ) {
               final int i = k / cVal;
               final int j = k % cVal;

               final int cOff0 = i * cVal1;
               // final int cOff1 = cOff0 + cVal1;

               final int c00 = cOff0 + j;
               final int c10 = c00 + 1;
               final int c01 = cOff0 + cVal1 + j;
               final int c11 = c01 + 1;

               final int[][] f0 = fs[m];
               final int[] vr00 = f0[0];
               vr00[0] = c00;
               vr00[1] = c00;

               final int[] vr01 = f0[1];
               vr01[0] = c10;
               vr01[1] = c10;

               final int[] vr02 = f0[2];
               vr02[0] = c11;
               vr02[1] = c11;

               final int[][] f1 = fs[m + 1];
               final int[] vr10 = f1[0];
               vr10[0] = c11;
               vr10[1] = c11;

               final int[] vr11 = f1[1];
               vr11[0] = c01;
               vr11[1] = c01;

               final int[] vr12 = f1[2];
               vr12[0] = c00;
               vr12[1] = c00;
            }
         }
      }

      return target;
   }

   /**
    * Creates a subdivided plane. Useful for meshes which later will be
    * augmented by noise or height maps to simulate terrain.
    *
    * @param count  subdivisions
    * @param target the output mesh
    *
    * @return the plane
    */
   public static Mesh2 plane ( final int count, final Mesh2 target ) {

      return Mesh2.plane(count, count, Mesh2.DEFAULT_POLY_TYPE, target);
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

      /* @formatter:off */
      final int seg = sectors < 3 ? 3 : sectors;
      switch ( seg ) {
         case 3: target.name = "Triangle"; break;
         case 4: target.name = "Quadrilateral"; break;
         case 5: target.name = "Pentagon"; break;
         case 6: target.name = "Hexagon"; break;
         case 8: target.name = "Octagon"; break;
         default: target.name = "Polygon";
      }
      /* @formatter:on */

      final int newLen = poly == PolyType.NGON ? seg : poly == PolyType.QUAD
         ? seg + seg + 1 : seg + 1;
      final float toTheta = 1.0f / seg;

      final Vec2[] vs = target.coords = Vec2.resize(target.coords, newLen);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
         newLen);

      switch ( poly ) {

         case NGON: {
            target.faces = new int[1][seg][2];
            final int[][] ngon = target.faces[0];

            for ( int i = 0; i < seg; ++i ) {
               final Vec2 v = vs[i];
               final float theta = i * toTheta;
               v.set(0.5f * Utils.scNorm(theta), 0.5f * Utils.scNorm(theta
                  - 0.25f));

               vts[i].set(v.x + 0.5f, 0.5f - v.y);

               ngon[i][0] = i;
               ngon[i][1] = i;
            }
         }
            break;

         case QUAD: {
            final int[][][] fsQuad = target.faces = new int[seg][4][2];
            vs[0].set(0.0f, 0.0f);
            vts[0].set(0.5f, 0.5f);

            /* Find corners. */
            for ( int i = 0, j = 1; i < seg; ++i, j += 2 ) {
               final Vec2 vCrn = vs[j];
               final float t = i * toTheta;
               vCrn.set(0.5f * Utils.scNorm(t), 0.5f * Utils.scNorm(t - 0.25f));
               vts[j].set(vCrn.x + 0.5f, 0.5f - vCrn.y);
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

               final int[][] f = fsQuad[i];

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
         }
            break;

         case TRI:

         default: {
            final int[][][] fsTri = target.faces = new int[seg][3][2];
            vs[0].set(0.0f, 0.0f);
            vts[0].set(0.5f, 0.5f);

            for ( int i = 0, j = 1; i < seg; ++i, ++j ) {
               final Vec2 v = vs[j];
               final float t = i * toTheta;
               v.set(0.5f * Utils.scNorm(t), 0.5f * Utils.scNorm(t - 0.25f));
               vts[j].set(v.x + 0.5f, 0.5f - v.y);

               final int k = 1 + j % seg;
               final int[][] f = fsTri[i];

               /* Should default to zero. */
               // f[0][0] = 0;
               // f[0][1] = 0;

               f[1][0] = j;
               f[1][1] = j;

               f[2][0] = k;
               f[2][1] = k;
            }
         }
      }

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
    * @see Mesh2#getVertices()
    * @see Vec2#distSq(Vec2, Vec2)
    */
   public static Map < Float, Vert2 > proximity ( final Mesh2 m, final Vec2 p,
      final float nearBound, final float farBound ) {

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
      final float spanOrig = maxDist - minDist;
      final float scalar = spanOrig != 0.0f ? ( farBound - nearBound )
         / spanOrig : 0.0f;
      final TreeMap < Float, Vert2 > result = new TreeMap <>();
      for ( int j = 0; j < vertLen; ++j ) {
         final float fac = nearBound + scalar * ( dists[j] - minDist );
         result.put(fac, verts[j]);
      }

      return result;
   }

   /**
    * Creates a square.
    *
    * @param target the output mesh
    *
    * @return the square
    */
   public static Mesh2 square ( final Mesh2 target ) {

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
   public static Mesh2 square ( final PolyType poly, final Mesh2 target ) {

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
         case QUAD: {
            target.faces = new int[][][] { { { 0, 0 }, { 1, 1 }, { 2, 2 }, { 3,
               3 } } };
         }
            break;

         case TRI:
         default: {
            target.faces = new int[][][] { { { 0, 0 }, { 1, 1 }, { 2, 2 } }, { {
               0, 0 }, { 2, 2 }, { 3, 3 } } };
         }
      }

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
    *
    * @see Utils#mod1(float)
    * @see Vec2#resize(Vec2[], int)
    */
   public static Mesh2 tracePerimeter ( final Mesh2 source, final int count,
      final float offset, final Mesh2 target ) {

      target.name = "Trace";

      final int vcount = count < 3 ? 3 : count;
      final float toStep = 1.0f / vcount;

      final Vec2[] vsSrc = source.coords;
      final Vec2[] vtsSrc = source.texCoords;
      final int[][][] fsSrc = source.faces;
      final int fsSrcLen = fsSrc.length;
      final int trgLen = fsSrcLen * vcount;

      final Vec2[] vsTrg = target.coords = Vec2.resize(target.coords, trgLen);
      final Vec2[] vtsTrg = target.texCoords = Vec2.resize(target.texCoords,
         trgLen);
      final int[][][] fsTrg = target.faces = new int[fsSrcLen][vcount][2];

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

            final int[] vertTrg = fTrg[j];
            vertTrg[0] = k;
            vertTrg[1] = k;
         }
      }

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
    *
    * @see Vec2#resize(Vec2[], int)
    */
   public static Mesh2 uniformData ( final Mesh2 source, final Mesh2 target ) {

      /* Find length of uniform data. */
      int uniformLen = 0;
      final int[][][] fsSrc = source.faces;
      final int fsSrcLen = fsSrc.length;
      for ( int i = 0; i < fsSrcLen; ++i ) { uniformLen += fsSrc[i].length; }

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
    * An internal helper function to accumulate the minimum and maximum points
    * in a mesh.
    *
    * @param mesh the mesh
    * @param lb   the lower bound
    * @param ub   the upper bound
    */
   static void accumMinMax ( final Mesh2 mesh, final Vec2 lb, final Vec2 ub ) {

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
    * @see Transform2#mulPoint(Vec2, Vec2, Vec2)
    */
   static void accumMinMax ( final Mesh2 mesh, final Vec2 lb, final Vec2 ub,
      final Transform2 tr, final Vec2 co ) {

      final Vec2[] coords = mesh.coords;
      final int len = coords.length;

      for ( int i = 0; i < len; ++i ) {
         Transform2.mulPoint(tr, coords[i], co);
         final float x = co.x;
         final float y = co.y;

         /* Minimum, maximum need separate if checks, not if-else. */
         if ( x < lb.x ) { lb.x = x; }
         if ( x > ub.x ) { ub.x = x; }
         if ( y < lb.y ) { lb.y = y; }
         if ( y > ub.y ) { ub.y = y; }
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
    * @see Vec2#dot(Vec2, Vec2)
    * @see Vec2#bezierPoint(Vec2, Vec2, Vec2, Vec2, float, Vec2)
    * @see Vec2#subNorm(Vec2, Vec2, Vec2)
    */
   static ArrayList < Vec2 > fromCurve2 ( final Curve2 source,
      final int resolution, final float colinearTol, final ArrayList <
         Vec2 > points, final Vec2 dir0, final Vec2 dir1 ) {

      /* Open curves not supported. */
      if ( !source.closedLoop ) { return points; }

      final int vres = resolution < 2 ? 2 : resolution;
      final float vtol = Utils.clamp01(1.0f - colinearTol);
      final float toPercent = 1.0f / vres;
      final Iterator < Knot2 > itr = source.iterator();
      Knot2 prevKnot = source.getLast();

      /*
       * Test if vector from fore handle to previous coordinate is colinear with
       * vector from rear handle to next coordinate. If so, then the curve drawn
       * between them will be a straight line, and Bezier interpolation is not
       * needed. The vectors are colinear if the absolute dot product is greater
       * than 1.0 minus a tolerance.
       */
      for ( Knot2 currKnot = null; itr.hasNext(); prevKnot = currKnot ) {
         currKnot = itr.next();
         final Vec2 coPrev = prevKnot.coord;
         final Vec2 fhPrev = prevKnot.foreHandle;
         final Vec2 rhNext = currKnot.rearHandle;
         final Vec2 coNext = currKnot.coord;

         /* Add previous knot coordinate no matter the colinear status. */
         points.add(new Vec2(coPrev));

         Vec2.subNorm(fhPrev, coPrev, dir0);
         Vec2.subNorm(rhNext, coNext, dir1);
         final float dotp = Vec2.dot(dir0, dir1);

         if ( dotp > -vtol && dotp < vtol ) {
            for ( int i = 1; i < vres; ++i ) {
               points.add(Vec2.bezierPoint(coPrev, fhPrev, rhNext, coNext, i
                  * toPercent, new Vec2()));
            }
         }
      }

      return points;
   }

   /**
    * An abstract class that may serve as an umbrella for any custom
    * comparators of Mesh2 s.
    */
   public abstract static class AbstrComparator implements Comparator <
      Mesh2 > {

      /**
       * The default constructor.
       */
      protected AbstrComparator ( ) {}

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

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

         if ( !this.hasNext() ) { throw new NoSuchElementException(); }
         return this.mesh.getFace(this.index++, new Face2());
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
