package camzup.core;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Organizes data needed to draw a two dimensional shape
 * using vertices and faces. Given that a mesh is primarily
 * a collection of references, it is initialized with null
 * arrays (coordinates, texture coordinates and indices).
 * These are not final, and so can be reassigned.
 */
public class Mesh2 extends Mesh {

   /**
    * Packages 2D mesh vertices into an object.
    */
   public static class Face2 implements Comparable < Face2 > {

      /**
       * The array of vertices in a face.
       */
      public Vert2[] vertices = new Vert2[] {};

      /**
       * The default constructor.
       */
      public Face2 () {

      }

      /**
       * Creates a face from an array of vertices.
       *
       * @param vertices
       *           the vertices
       */
      public Face2 ( final Vert2[] vertices ) {

         this.set(vertices);
      }

      /**
       * Compares this face to another by hash code.
       *
       * @param face
       *           the comparisand
       * @return the comparison
       */
      @Override
      public int compareTo ( final Face2 face ) {

         final int a = this.hashCode();
         final int b = face.hashCode();
         return Integer.compare(a, b);
      }

      @Override
      public int hashCode () {

         return System.identityHashCode(this);
      }

      /**
       * Returns the number of vertices in this face.
       *
       * @return the vertex count
       */
      public int length () {

         return this.vertices.length;
      }

      /**
       * Rotates all coordinates in the face by an angle around
       * the z axis.
       *
       * @param radians
       *           the angle in radians
       * @return this mesh
       * @see Vec2#rotateZ(Vec2, float, Vec2)
       */
      @Chainable
      public Face2 rotateZ ( final float radians ) {

         final float nrm = IUtils.ONE_TAU * radians;
         final float cosa = SinCos.eval(nrm);
         final float sina = SinCos.eval(nrm - 0.25f);
         Vec2 c;

         final int len = this.vertices.length;
         for (int i = 0; i < len; ++i) {
            c = this.vertices[i].coord;
            Vec2.rotateZ(c, cosa, sina, c);
         }

         return this;
      }

      /**
       * Scales all coordinates in the face by a scalar.
       *
       * @param scale
       *           the vector
       * @return this face
       * @see Vec2#mul(Vec2, float, Vec2)
       */
      @Chainable
      public Face2 scale ( final float scale ) {

         Vec2 c;
         final int len = this.vertices.length;
         for (int i = 0; i < len; ++i) {
            c = this.vertices[i].coord;
            Vec2.mul(c, scale, c);
         }

         return this;
      }

      /**
       * Scales all coordinates in the face by a vector.
       *
       * @param scale
       *           the vector
       * @return this face
       * @see Vec2#mul(Vec2, Vec2, Vec2)
       */
      @Chainable
      public Face2 scale ( final Vec2 scale ) {

         Vec2 c;
         final int len = this.vertices.length;
         for (int i = 0; i < len; ++i) {
            c = this.vertices[i].coord;
            Vec2.mul(c, scale, c);
         }

         return this;
      }

      /**
       * Sets this face's vertices to refer to a an array.
       *
       * @param vertices
       *           the array of vertices
       * @return this face
       */
      @Chainable
      public Face2 set ( final Vert2[] vertices ) {

         this.vertices = vertices;
         return this;
      }

      @Override
      public String toString () {

         return this.toString(4);
      }

      public String toString ( final int places ) {

         final int len = this.vertices.length;
         final int last = len - 1;
         final StringBuilder sb = new StringBuilder(len * 256)
               .append("{ vertices: [ \n");
         for (int i = 0; i < len; ++i) {
            sb.append(this.vertices[i].toString(places));
            if (i < last) {
               sb.append(',').append('\n');
            }
         }
         sb.append(" ] }");
         return sb.toString();
      }

      /**
       * Translates all coordinates in the face by a vector.
       *
       * @param v
       *           the vector
       * @return this face
       * @see Vec2#add(Vec2, Vec2, Vec2)
       */
      @Chainable
      public Face2 translate ( final Vec2 v ) {

         Vec2 c;
         final int len = this.vertices.length;
         for (int i = 0; i < len; ++i) {
            c = this.vertices[i].coord;
            Vec2.add(c, v, c);
         }

         return this;
      }
   }

   /**
    * The type of polygon produced by the static polygon
    * function.
    */
   public enum PolyType {

      /**
       * Create an n-sided polygon.
       */
      NGON (),

      /**
       * Create a triangle-fan, with a point in the center.
       */
      TRI ();

      /**
       * The default constructor.
       */
      private PolyType () {

      }
   }

   /**
    * Packages 2D mesh coordinates, texture coordinates and
    * normals into a single object.
    */
   public static class Vert2 implements Comparable < Vert2 > {

      /**
       * The coordinate of the vertex in world space.
       */
      public Vec2 coord;

      /**
       * The texture (UV) coordinate for an image mapped onto the
       * mesh.
       */
      public Vec2 texCoord;

      /**
       * The default constructor
       */
      public Vert2 () {

      }

      /**
       * Constructs a vertex from a coordinate and texture
       * coordinate.
       *
       * @param coord
       *           the coordinate
       * @param texCoord
       *           the texture coordinate
       */
      public Vert2 (
            final Vec2 coord,
            final Vec2 texCoord ) {

         this.set(coord, texCoord);
      }

      /**
       * Compares this vertex to another by hash code.
       *
       * @param vert
       *           the comparisand
       * @return the comparison
       */
      @Override
      public int compareTo ( final Vert2 vert ) {

         final int a = this.hashCode();
         final int b = vert.hashCode();
         // TODO: Replace with ternary operator compare.
         return Integer.compare(a, b);
      }

      /**
       * Gets the system identity hash code.
       *
       * @return the hash code
       */
      @Override
      public int hashCode () {

         return System.identityHashCode(this);
      }

      /**
       * Sets the coordinate and texture coordinate of the vertex
       * by reference.
       *
       * @param coord
       *           the coordinate
       * @param texCoord
       *           the texture coordinate
       * @return this vertex
       */
      @Chainable
      public Vert2 set (
            final Vec2 coord,
            final Vec2 texCoord ) {

         this.coord = coord;
         this.texCoord = texCoord;
         return this;
      }

      @Override
      public String toString () {

         return this.toString(4);
      }

      public String toString ( final int places ) {

         return new StringBuilder(256)
               .append("{ coord: ")
               .append(this.coord.toString(places))
               .append(", texCoord: ")
               .append(this.texCoord.toString(places))
               .append(' ').append('}')
               .toString();
      }
   }

   /**
    * Default annulus for rings, 0.25 * Math.sqrt(2.0) ,
    * approximately 0.35355338 .
    */
   public static final float DEFAULT_ANNULUS = 0.35355338f;

   /**
    * Default count of sectors in a regular convex polygon, so
    * as to approximate a circle.
    */
   public static final int DEFAULT_POLY_SECTORS = 32;

   /**
    * Type of polygon to draw when it is not supplied to the
    * polygon function.
    */
   public static final PolyType DEFAULT_POLY_TYPE = PolyType.NGON;

   /**
    * Calculates the dimensions of an Axis-Aligned Bounding Box
    * (AABB) encompassing the mesh.
    *
    * @param mesh
    *           the mesh
    * @param dim
    *           the output vector
    * @return the dimensions
    */
   public static Vec2 calcDimensions (
         final Mesh2 mesh,
         final Vec2 dim ) {

      float xMin = Float.MAX_VALUE;
      float xMax = Float.MIN_VALUE;

      float yMin = Float.MAX_VALUE;
      float yMax = Float.MIN_VALUE;

      final Vec2[] coords = mesh.coords;
      final int len = coords.length;

      for (int i = 0; i < len; ++i) {

         final Vec2 coord = coords[i];
         final float x = coord.x;
         final float y = coord.y;

         if (x < xMin) {
            xMin = x;
         } else if (x > xMax) {
            xMax = x;
         }

         if (y < yMin) {
            yMin = y;
         } else if (y > yMax) {
            yMax = y;
         }
      }

      return dim.set(
            xMax - xMin,
            yMax - yMin);
   }

   /**
    * Creates a subdvided plane. Useful for meshes which later
    * will be augmented by noise or height maps to simulate
    * terrain.
    * 
    * @param div
    *           subdivisions
    * @param target
    *           the output mesh
    * @return the plane
    */
   public static final Mesh2 plane ( 
         final int div, 
         final Mesh2 target ) {

      return Mesh2.plane(div, div, Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a subdvided plane. Useful for meshes which later
    * will be augmented by noise or height maps to simulate
    * terrain.
    *
    * @param cols
    *           number of columns
    * @param rows
    *           number of rows
    * @param target
    *           the output mesh
    * @return the plane
    */
   public static final Mesh2 plane (
         final int cols,
         final int rows,
         final Mesh2 target ) {

      return Mesh2.plane(cols, rows, Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a plane subdivided into either tris or quads,
    * depending on the polygon type. Useful for meshes which
    * later will be augmented by noise or height maps to
    * simulate terrain.
    *
    * @param cols
    *           number of columns
    * @param rows
    *           number of rows
    * @param poly
    *           the polygon type
    * @param target
    *           the output mesh
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

      final Vec2[] coords = new Vec2[rval1 * cval1];
      final Vec2[] texCoords = new Vec2[coords.length];

      /* Calculate x values in separate loop. */
      final float[] xs = new float[cval1];
      final float[] us = new float[cval1];
      for (int j = 0; j < cval1; ++j) {
         final float u = us[j] = j * jToStep;
         xs[j] = u - 0.5f;
      }

      for (int k = 0, i = 0; i < rval1; ++i) {
         final float v = i * iToStep;
         final float y = v - 0.5f;

         for (int j = 0; j < cval1; ++j, ++k) {
            coords[k] = new Vec2(xs[j], y);
            texCoords[k] = new Vec2(us[j], v);
         }
      }

      int[][][] faces;
      final int len = rval * cval;
      switch (poly) {

         case TRI:

            faces = new int[len + len][3][2];

            for (int k = 0, i = 0; i < rval; ++i) {
               final int noff0 = i * cval1;
               final int noff1 = (i + 1) * cval1;

               for (int j = 0; j < cval; ++j, k += 2) {
                  final int n00 = noff0 + j;
                  final int n10 = n00 + 1;
                  final int n01 = noff1 + j;
                  final int n11 = n01 + 1;

                  faces[k] = new int[][] {
                        { n00, n00 },
                        { n10, n10 },
                        { n11, n11 } };

                  faces[k + 1] = new int[][] {
                        { n11, n11 },
                        { n01, n01 },
                        { n00, n00 } };
               }
            }

            break;

         case NGON:

         default:

            faces = new int[len][4][2];

            for (int k = 0, i = 0; i < rval; ++i) {
               final int noff0 = i * cval1;
               final int noff1 = (i + 1) * cval1;

               for (int j = 0; j < cval; ++j, ++k) {
                  final int n00 = noff0 + j;
                  final int n10 = n00 + 1;
                  final int n01 = noff1 + j;
                  final int n11 = n01 + 1;

                  faces[k] = new int[][] {
                        { n00, n00 },
                        { n10, n10 },
                        { n11, n11 },
                        { n01, n01 } };
               }
            }

      }

      return target.set(faces, coords, texCoords);
   }

   /**
    * Creates a regular convex polygon.
    *
    * @param sectors
    *           the number of sides
    *
    * @param target
    *           the output mesh
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
    * @param sectors
    *           the number of sides
    * @param poly
    *           the polygon type
    * @param target
    *           the output mesh
    * @return the polygon
    */
   public static Mesh2 polygon (
         final int sectors,
         final PolyType poly,
         final Mesh2 target ) {

      target.name = "Polygon";
      final int seg = sectors < 3 ? 3 : sectors;
      final float toTheta = IUtils.TAU / seg;

      Vec2[] coords;
      Vec2[] texCoords;
      int[][][] faces;
      final Vec2 uvCenter = Vec2.uvCenter(new Vec2());
      final Vec2 pureCoord = new Vec2();

      switch (poly) {
         case NGON:

            coords = new Vec2[seg];
            texCoords = new Vec2[seg];
            faces = new int[1][seg][2];
            final int[][] ngon = faces[0];

            for (int i = 0; i < seg; ++i) {

               Vec2.fromPolar(i * toTheta, 0.5f, pureCoord);
               texCoords[i] = Vec2.add(uvCenter, pureCoord, new Vec2());
               coords[i] = new Vec2(pureCoord);
               ngon[i] = new int[] { i, i };
            }

            break;

         case TRI:
         default:

            coords = new Vec2[seg + 1];
            texCoords = new Vec2[seg + 1];
            faces = new int[seg][3][2];

            coords[0] = new Vec2();
            texCoords[0] = uvCenter;

            for (int i = 0, j = 1; i < seg; ++i, ++j) {

               Vec2.fromPolar(i * toTheta, 0.5f, pureCoord);
               texCoords[j] = Vec2.add(uvCenter, pureCoord, new Vec2());
               coords[j] = new Vec2(pureCoord);

               final int k = 1 + j % seg;
               faces[i] = new int[][] {
                     { 0, 0 }, { j, j }, { k, k } };
            }
      }

      return target.set(faces, coords, texCoords);
   }

   /**
    * Creates a regular convex polygon, approximating a circle.
    *
    * @param target
    *           the output mesh
    * @return the polygon
    */
   public static Mesh2 polygon (
         final Mesh2 target ) {

      return Mesh2.polygon(
            Mesh2.DEFAULT_POLY_SECTORS,
            Mesh2.DEFAULT_POLY_TYPE,
            target);
   }

   /**
    * Creates a regular convex polygon with an opening in its
    * center. The annulus describes the relative size of this
    * opening.
    *
    * @param sectors
    *           the number of sides
    * @param annulus
    *           the size of the opening
    * @param target
    *           the output type
    * @return the ring
    */
   public static final Mesh2 ring (
         final int sectors,
         final float annulus,
         final Mesh2 target ) {

      return Mesh2.ring(sectors, annulus, Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a regular convex polygon with an opening in its
    * center. The annulus describes the relative size of this
    * opening. When the polygon type is NGON, the ring will be
    * composed of quads; otherwise, tris.
    *
    * @param sectors
    *           the number of sides
    * @param annulus
    *           the size of the opening
    * @param poly
    *           the polygon type
    * @param target
    *           the output type
    * @return the ring
    */
   public static final Mesh2 ring (
         final int sectors,
         final float annulus,
         final PolyType poly,
         final Mesh2 target ) {

      target.name = "Ring";
      final int seg = sectors < 3 ? 3 : sectors;
      final int seg2 = seg + seg;
      final float toTheta = IUtils.TAU / seg;
      final float annul = Utils.clamp(annulus,
            Utils.EPSILON, 1.0f - Utils.EPSILON);

      Vec2[] coords;
      Vec2[] texCoords;
      int[][][] faces;
      final Vec2 uvCenter = Vec2.uvCenter(new Vec2());
      final Vec2 pureCoord = new Vec2();

      switch (poly) {
         case NGON:
            coords = new Vec2[seg2];
            texCoords = new Vec2[seg2];
            faces = new int[seg][4][2];

            for (int k = 0, i = 0, j = 1; k < seg; ++k, i += 2, j += 2) {
               Vec2.fromPolar(k * toTheta, 0.5f, pureCoord);

               coords[i] = new Vec2(pureCoord);
               final Vec2 v1 = coords[j] = Vec2.mul(
                     pureCoord, annul, new Vec2());

               texCoords[i] = Vec2.add(uvCenter, pureCoord, new Vec2());
               texCoords[j] = Vec2.add(uvCenter, v1, new Vec2());

               final int m = (i + 2) % seg2;
               final int n = (j + 2) % seg2;

               faces[k] = new int[][] {
                     { i, i }, { m, m }, { n, n }, { j, j } };
            }

            break;

         case TRI:

         default:

            coords = new Vec2[seg2];
            texCoords = new Vec2[seg2];
            faces = new int[seg2][3][2];

            for (int k = 0, i = 0, j = 1; k < seg; ++k, i += 2, j += 2) {
               Vec2.fromPolar(k * toTheta, 0.5f, pureCoord);

               coords[i] = new Vec2(pureCoord);
               final Vec2 v1 = coords[j] = Vec2.mul(
                     pureCoord, annul, new Vec2());

               texCoords[i] = Vec2.add(uvCenter, pureCoord, new Vec2());
               texCoords[j] = Vec2.add(uvCenter, v1, new Vec2());

               final int m = (i + 2) % seg2;
               final int n = (j + 2) % seg2;

               faces[i] = new int[][] {
                     { i, i }, { m, m }, { j, j } };

               faces[j] = new int[][] {
                     { m, m }, { n, n }, { j, j } };
            }

      }

      return target.set(faces, coords, texCoords);
   }

   /**
    * Creates a regular convex polygon with an opening in its
    * center.
    *
    * @param sectors
    *           the number of sides
    * @param target
    *           the output type
    * @return the ring
    */
   public static final Mesh2 ring (
         final int sectors,
         final Mesh2 target ) {

      return Mesh2.ring(
            sectors,
            Mesh2.DEFAULT_ANNULUS,
            Mesh2.DEFAULT_POLY_TYPE,
            target);
   }

   /**
    * Creates a regular convex polygon with an opening in its
    * center.
    *
    * @param target
    *           the output type
    * @return the ring
    */
   public static final Mesh2 ring (
         final Mesh2 target ) {

      return Mesh2.ring(
            Mesh2.DEFAULT_POLY_SECTORS,
            Mesh2.DEFAULT_ANNULUS,
            Mesh2.DEFAULT_POLY_TYPE,
            target);
   }

   /**
    * Creates a rectangle.
    *
    * @param target
    *           the output mesh
    * @return the rectangle
    */
   public static final Mesh2 square ( final Mesh2 target ) {

      return Mesh2.square(target, Mesh2.DEFAULT_POLY_TYPE);
   }

   /**
    * Creates a rectangle.
    *
    * @param target
    *           the output mesh
    * @param poly
    *           the polygon type
    * @return the rectangle
    */
   public static final Mesh2 square (
         final Mesh2 target,
         final PolyType poly ) {

      target.name = "Rectangle";

      final Vec2[] coords = new Vec2[] {
            new Vec2(0.5f, 0.5f),
            new Vec2(-0.5f, 0.5f),
            new Vec2(-0.5f, -0.5f),
            new Vec2(0.5f, -0.5f)
      };

      final Vec2[] texCoords = new Vec2[] {
            new Vec2(1.0f, 1.0f),
            new Vec2(0.0f, 1.0f),
            new Vec2(0.0f, 0.0f),
            new Vec2(1.0f, 0.0f)
      };

      int[][][] faces;
      switch (poly) {

         case TRI:

            faces = new int[][][] {
                  { { 0, 0 }, { 1, 1 }, { 2, 2 } },
                  { { 2, 2 }, { 3, 3 }, { 0, 0 } } };

            break;

         case NGON:

         default:

            faces = new int[][][] {
                  { { 0, 0 }, { 1, 1 }, { 2, 2 }, { 3, 3 } } };
      }

      return target.set(faces, coords, texCoords);
   }

   /**
    * Creates a triangle
    *
    * @param target
    *           the output mesh
    * @return the triangle
    */
   public static final Mesh2 triangle ( final Mesh2 target ) {

      target.name = "Triangle";

      final Vec2[] coords = new Vec2[] {
            new Vec2(0.0f, 0.5f),
            new Vec2(-0.4330127f, -0.25f),
            new Vec2(0.4330127f, -0.25f)
      };

      final Vec2[] texCoords = new Vec2[] {
            new Vec2(0.5f, 1.0f),
            new Vec2(0.0669873f, 0.25f),
            new Vec2(0.9330127f, 0.25f)
      };

      final int[][][] faces = new int[][][] {
            { { 0, 0 }, { 1, 1 }, { 2, 2 } } };

      return target.set(faces, coords, texCoords);
   }

   /**
    * An array of coordinates in the mesh.
    */
   public Vec2[] coords;

   /**
    * The faces array does not include face data itself, but
    * rather indices to other arrays which contain vertex data.
    * It is a three-dimensional array organized by
    * <ol>
    * <li>the number of faces;</li>
    * <li>the number of vertices per faces;</li>
    * <li>the information per face;</li>
    * </ol>
    * 2D meshes contain two pieces of information per vertex:
    * spatial coordinates and texture coordinates.
    */
   public int[][][] faces;

   /**
    * The material associated with this mesh in a mesh entity.
    */
   public int materialIndex = 0;

   /**
    * The texture (UV) coordinates that describe how an image
    * is mapped onto the geometry of the mesh. Typically in the
    * range [0.0, 1.0].
    */
   public Vec2[] texCoords;

   /**
    * The default constructor.
    */
   public Mesh2 () {

      super();
   }

   /**
    * Creates a mesh from arrays of faces, coordinates and
    * texture coordinates. The mesh's arrays are set by
    * reference, not by value.
    *
    * @param faces
    *           the faces array
    * @param coords
    *           the coordinates array
    * @param texCoords
    *           the texture coordinates array
    */
   public Mesh2 (
         final int[][][] faces,
         final Vec2[] coords,
         final Vec2[] texCoords ) {

      super();
      this.set(faces, coords, texCoords);
   }

   /**
    * Creates a named mesh.
    *
    * @param name
    *           the mesh name
    */
   public Mesh2 ( final String name ) {

      super(name);
   }

   /**
    * Creates a named mesh from arrays of faces, coordinates
    * and texture coordinates. The mesh's arrays are set by
    * reference, not by value.
    *
    * @param name
    *           the mesh name
    * @param faces
    *           the faces array
    * @param coords
    *           the coordinates array
    * @param texCoords
    *           the texture coordinates array
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
    * Returns a String of Python code targeted toward the
    * Blender 2.8x API. This code is brittle and is used for
    * internal testing purposes, i.e., to compare how curve
    * geometry looks in Blender (the control) vs. in the
    * library (the test).
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode () {

      final StringBuilder result = new StringBuilder();
      result.append("{\"name\": \"")
            .append(this.name)
            .append("\", \"material_index\": ")
            .append(this.materialIndex)
            .append(", \"vertices\": [");

      final int vlen = this.coords.length;
      final int vlast = vlen - 1;
      for (int i = 0; i < vlen; ++i) {
         result.append(this.coords[i].toBlenderCode(0.0f));
         if (i < vlast) {
            result.append(',').append(' ');
         }
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
            if (k < vrtLast) {
               result.append(',').append(' ');
            }
         }
         result.append(')');

         if (j < flast) {
            result.append(',').append(' ');
         }
      }

      result.append(']').append('}');
      return result.toString();
   }

   @Override
   public boolean equals ( final Object obj ) {

      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (this.getClass() != obj.getClass()) {
         return false;
      }
      final Mesh2 other = (Mesh2) obj;
      if (!Arrays.equals(this.coords, other.coords)) {
         return false;
      }
      if (!Arrays.deepEquals(this.faces, other.faces)) {
         return false;
      }
      return true;
   }

   /**
    * Gets a face from the mesh.
    *
    * @param i
    *           the index
    * @param target
    *           the output face
    * @return the face
    */
   public Face2 getFace (
         final int i,
         final Face2 target ) {

      final int[][] face = this.faces[i];
      final int len = face.length;
      final Vert2[] vertices = new Vert2[len];

      for (int j = 0; j < len; ++j) {
         final int[] vert = face[j];
         vertices[j] = new Vert2(
               this.coords[vert[0]],
               this.texCoords[vert[1]]);
      }

      return target.set(vertices);
   }

   /**
    * Gets an array of faces from teh mesh.
    *
    * @return the faces array
    */
   public Face2[] getFaces () {

      final int len0 = this.faces.length;
      final Face2[] result = new Face2[len0];

      for (int i = 0; i < len0; ++i) {

         final int[][] fs0 = this.faces[i];
         final int len1 = fs0.length;
         final Vert2[] verts = new Vert2[len1];

         for (int j = 0; j < len1; ++j) {

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
    * Get a vertex from the mesh.
    *
    * @param i
    *           primary index
    * @param j
    *           secondary index
    * @param target
    *           the output vertex
    * @return the vertex
    */
   public Vert2 getVertex (
         final int i,
         final int j,
         final Vert2 target ) {

      final int[] f = this.faces[i][j];
      return target.set(
            this.coords[f[0]],
            this.texCoords[f[1]]);
   }

   /**
    * Get an array of vertices from the mesh.
    *
    * @return the vertices
    */
   public Vert2[] getVertices () {

      final ArrayList < Vert2 > result = new ArrayList <>();
      Vert2 trial = new Vert2();
      final int len0 = this.faces.length;

      for (int i = 0; i < len0; ++i) {

         final int[][] fs = this.faces[i];
         final int len1 = fs.length;

         for (int j = 0; j < len1; ++j) {

            final int[] f = fs[j];
            trial.set(
                  this.coords[f[0]],
                  this.texCoords[f[1]]);

            if (!result.contains(trial)) {
               result.add(trial);
               trial = new Vert2();
            }
         }
      }

      return result.toArray(new Vert2[result.size()]);
   }

   @Override
   public int hashCode () {

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL ^ Arrays.hashCode(this.coords);
      hash = hash * IUtils.HASH_MUL ^ Arrays.deepHashCode(this.faces);
      return hash;
   }

   /**
    * Rotates all coordinates in the mesh by an angle around
    * the z axis.
    *
    * @param radians
    *           the angle in radians
    * @return this mesh
    * @see Vec2#rotateZ(Vec2, float, Vec2)
    */
   @Chainable
   public Mesh2 rotateZ ( final float radians ) {

      final float nrm = IUtils.ONE_TAU * radians;
      final float cosa = SinCos.eval(nrm);
      final float sina = SinCos.eval(nrm - 0.25f);
      Vec2 c;

      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         c = this.coords[i];
         Vec2.rotateZ(c, cosa, sina, c);
      }

      return this;
   }

   /**
    * Scales all coordinates in the mesh by a scalar.
    *
    * @param scale
    *           the scalar
    * @return this mesh
    * @see Vec2#mul(Vec2, float, Vec2)
    */
   @Chainable
   public Mesh2 scale ( final float scale ) {

      Vec2 c;
      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         c = this.coords[i];
         Vec2.mul(c, scale, c);
      }

      return this;
   }

   /**
    * Scales all coordinates in the mesh by a vector.
    *
    * @param scale
    *           the vector
    * @return this mesh
    * @see Vec2#mul(Vec2, Vec2, Vec2)
    */
   @Chainable
   public Mesh2 scale ( final Vec2 scale ) {

      Vec2 c;
      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         c = this.coords[i];
         Vec2.mul(c, scale, c);
      }

      return this;
   }

   /**
    * Sets the mesh's data by reference, not by value.
    *
    * @param faces
    *           the faces array
    * @param coords
    *           the coordinates array
    * @param texCoords
    *           the texture coordinates array
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
    * Renders the mesh as a string following the Wavefront OBJ
    * file format.
    *
    * @return the string
    */
   @Experimental
   public String toObjString () {

      // TODO: Needs testing.
      final int coordsLen = this.coords.length;
      final int texCoordsLen = this.texCoords.length;
      final int facesLen = this.faces.length;
      final StringBuilder result = new StringBuilder();

      /*
       * Append a comment listing the number of coordinates,
       * texture coordinates, normals and faces.
       */
      result.append("# v: ")
            .append(coordsLen)
            .append(", vt: ")
            .append(texCoordsLen)
            .append(", vn: 1, f: ")
            .append(facesLen)
            .append("\n \n");

      result.append('o').append(' ')
            .append(this.name)
            .append("\n \n");

      for (final Vec2 coord : this.coords) {
         result.append("v ")
               .append(coord.toObjString())
               .append(" 0.0 \n");
      }
      result.append('\n');

      for (final Vec2 texCoord : this.texCoords) {
         result.append("vt ")
               .append(texCoord.toObjString())
               .append('\n');
      }

      result.append("\nvn 0.0 0.0 1.0 \n");

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
                  .append('1')
                  .append(' ');
         }

         result.append('\n');
      }

      result.append('\n');
      return result.toString();
   }

   /**
    * Returns a string representation of the mesh.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4, Integer.MAX_VALUE);
   }

   /**
    * Returns a string representation of the mesh.
    *
    * @param places
    *           the number of places
    * @return the string
    */
   public String toString ( final int places ) {

      return this.toString(places, Integer.MAX_VALUE);
   }

   /**
    * Returns a string representation of the mesh. Includes an
    * option to truncate the listing in case of large meshes.
    *
    * @param places
    *           the number of places
    * @param truncate
    *           truncate elements in a list
    * @return the string
    */
   public String toString ( final int places, final int truncate ) {

      final StringBuilder sb = new StringBuilder();

      sb.append("{ name: \"")
            .append(this.name)
            .append("\", coords: [");

      if (this.coords != null) {
         sb.append('\n');
         final int len = Math.min(this.coords.length, truncate);
         final int last = len - 1;
         for (int i = 0; i < len; ++i) {
            sb.append(this.coords[i].toString(places));
            if (i < last) {
               sb.append(',').append('\n');
            }
         }
      }

      if (this.coords.length > truncate) {
         sb.append("\n/* ... */");
      }

      sb.append(" ],\ntexCoords: [");
      if (this.texCoords != null) {
         sb.append('\n');
         final int len = Math.min(this.texCoords.length, truncate);
         final int last = len - 1;
         for (int i = 0; i < len; ++i) {
            sb.append(this.texCoords[i].toString(places));
            if (i < last) {
               sb.append(',').append('\n');
            }
         }
      }

      if (this.texCoords.length > truncate) {
         sb.append("\n/* ... */");
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
                * There should be 2 indices: coordinate, texture
                * coordinate.
                */
               for (int k = 0; k < infoLen; ++k) {

                  sb.append(vert[k]);
                  if (k < infoLast) {
                     sb.append(',').append(' ');
                  }
               }
               sb.append(' ').append(']');
               if (j < vertsLast) {
                  sb.append(',').append(' ');
               }
            }
            sb.append(' ').append(']');
            if (i < facesLast) {
               sb.append(',').append('\n');
            }
         }
      }

      if (this.faces.length > truncate) {
         sb.append("\n/* ... */");
      }

      sb.append(" ] }");
      return sb.toString();
   }

   /**
    * Renders the mesh as a string following the SVG file
    * format.
    *
    * @return the string
    */
   public String toSvgString () {

      // TODO: Create internal and external toSvgStrings so that
      // you can create a svg from a mesh and mesh entity
      // independently of the renderer. Make this one
      // toSvgStringInternal with package level access.

      final StringBuilder result = new StringBuilder();

      final int[][][] fs = this.faces;
      final Vec2[] vs = this.coords;
      final int flen0 = fs.length;
      for (int i = 0; i < flen0; ++i) {
         final int[][] f = fs[i];
         final int flen1 = f.length;

         result.append("<path d=\"M ")
               .append(vs[f[0][0]].toSvgString())
               .append(' ');

         for (int j = 1; j < flen1; ++j) {
            result.append('L').append(' ')
                  .append(vs[f[j][0]].toSvgString())
                  .append(' ');
         }

         result.append("Z\"></path>\n");
      }

      return result.toString();
   }

   /**
    * Translates all coordinates in the mesh by a vector.
    *
    * @param v
    *           the vector
    * @return this mesh
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   @Chainable
   public Mesh2 translate ( final Vec2 v ) {

      Vec2 c;
      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         c = this.coords[i];
         Vec2.add(c, v, c);
      }

      return this;
   }
}
