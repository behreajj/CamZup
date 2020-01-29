package camzup.core;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Organizes data needed to draw a three dimensional shape
 * using vertices and faces. Given that a mesh is primarily
 * a collection of references, it is initialized with null
 * arrays (coordinates, texture coordinates and indices).
 * These are not final, and so can be reassigned.
 */
public class Mesh3 extends Mesh {

   /**
    * Packages 3D mesh vertices into an object.
    */
   public static class Face3 implements Comparable < Face3 > {

      /**
       * The array of vertices in a face.
       */
      public Vert3[] vertices = new Vert3[] {};

      /**
       * The default constructor.
       */
      public Face3 () {

      }

      /**
       * Creates a face from an array of vertices.
       *
       * @param vertices
       *           the vertices
       */
      public Face3 ( final Vert3[] vertices ) {

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
      public int compareTo ( final Face3 face ) {

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
       * Rotates all coordinates and normals in the mesh by an
       * angle around an arbitrary axis.
       *
       * @param radians
       *           the angle in radians
       * @param axis
       *           the axis of rotation
       * @return this mesh
       * @see Vec3#rotate(Vec3, float, Vec3, Vec3)
       */
      @Chainable
      public Face3 rotate ( final float radians, final Vec3 axis ) {

         final float nrm = IUtils.ONE_TAU * radians;
         final float cosa = SinCos.eval(nrm);
         final float sina = SinCos.eval(nrm - 0.25f);

         Vert3 vt3;
         Vec3 c;
         Vec3 n;

         final int len = this.vertices.length;
         for (int i = 0; i < len; ++i) {
            vt3 = this.vertices[i];
            c = vt3.coord;
            n = vt3.normal;
            Vec3.rotate(c, cosa, sina, axis, c);
            Vec3.rotate(n, cosa, sina, axis, n);
         }

         return this;
      }

      /**
       * Rotates all coordinates and normals in the face by a
       * quaternion.
       *
       * @param q
       *           the quaternion
       * @return the mesh
       */
      @Chainable
      public Face3 rotate ( final Quaternion q ) {

         Vert3 vt3;
         Vec3 c;
         Vec3 n;

         final int len = this.vertices.length;
         for (int i = 0; i < len; ++i) {
            vt3 = this.vertices[i];
            c = vt3.coord;
            n = vt3.normal;
            Quaternion.mulVector(q, c, c);
            Quaternion.mulVector(q, n, n);
         }

         return this;
      }

      /**
       * Rotates all coordinates and normals in the face by an
       * angle around the x axis.
       *
       * @param radians
       *           the angle in radians
       * @return this mesh
       * @see Vec3#rotateX(Vec3, float, Vec3)
       */
      @Chainable
      public Face3 rotateX ( final float radians ) {

         final float nrm = IUtils.ONE_TAU * radians;
         final float cosa = SinCos.eval(nrm);
         final float sina = SinCos.eval(nrm - 0.25f);

         Vert3 vt3;
         Vec3 c;
         Vec3 n;

         final int len = this.vertices.length;
         for (int i = 0; i < len; ++i) {
            vt3 = this.vertices[i];
            c = vt3.coord;
            n = vt3.normal;
            Vec3.rotateX(c, cosa, sina, c);
            Vec3.rotateX(n, cosa, sina, n);
         }

         return this;
      }

      /**
       * Rotates all coordinates and normals in the face by an
       * angle around the y axis.
       *
       * @param radians
       *           the angle in radians
       * @return this mesh
       * @see Vec3#rotateY(Vec3, float, Vec3)
       */
      @Chainable
      public Face3 rotateY ( final float radians ) {

         final float nrm = IUtils.ONE_TAU * radians;
         final float cosa = SinCos.eval(nrm);
         final float sina = SinCos.eval(nrm - 0.25f);

         Vert3 vt3;
         Vec3 c;
         Vec3 n;

         final int len = this.vertices.length;
         for (int i = 0; i < len; ++i) {
            vt3 = this.vertices[i];
            c = vt3.coord;
            n = vt3.normal;
            Vec3.rotateY(c, cosa, sina, c);
            Vec3.rotateY(n, cosa, sina, n);
         }

         return this;
      }

      /**
       * Rotates all coordinates and normals in the face by an
       * angle around the z axis.
       *
       * @param radians
       *           the angle in radians
       * @return this mesh
       * @see Vec3#rotateZ(Vec3, float, Vec3)
       */
      @Chainable
      public Face3 rotateZ ( final float radians ) {

         final float nrm = IUtils.ONE_TAU * radians;
         final float cosa = SinCos.eval(nrm);
         final float sina = SinCos.eval(nrm - 0.25f);

         Vert3 vt3;
         Vec3 c;
         Vec3 n;

         final int len = this.vertices.length;
         for (int i = 0; i < len; ++i) {
            vt3 = this.vertices[i];
            c = vt3.coord;
            n = vt3.normal;
            Vec3.rotateZ(c, cosa, sina, c);
            Vec3.rotateZ(n, cosa, sina, n);
         }

         return this;
      }

      /**
       * Scales all coordinates in the face by a scalar.
       *
       * @param scale
       *           the vector
       * @return this face
       * @see Vec3#mul(Vec3, float, Vec3)
       */
      @Chainable
      public Face3 scale ( final float scale ) {

         Vec3 c;
         final int len = this.vertices.length;
         for (int i = 0; i < len; ++i) {
            c = this.vertices[i].coord;
            Vec3.mul(c, scale, c);
         }

         return this;
      }

      /**
       * Scales all coordinates in the face by a vector.
       *
       * @param scale
       *           the vector
       * @return this face
       * @see Vec3#mul(Vec3, Vec3, Vec3)
       */
      @Chainable
      public Face3 scale ( final Vec3 scale ) {

         Vec3 c;
         final int len = this.vertices.length;
         for (int i = 0; i < len; ++i) {
            c = this.vertices[i].coord;
            Vec3.mul(c, scale, c);
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
      public Face3 set ( final Vert3[] vertices ) {

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
         final StringBuilder sb = new StringBuilder(len * 512)
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
       * @see Vec3#add(Vec3, Vec3, Vec3)
       */
      @Chainable
      public Face3 translate ( final Vec3 v ) {

         Vec3 c;
         final int len = this.vertices.length;
         for (int i = 0; i < len; ++i) {
            c = this.vertices[i].coord;
            Vec3.add(c, v, c);
         }

         return this;
      }
   }

   /**
    * Packages 3D mesh coordinates, texture coordinates and
    * normals into a single object.
    */
   public static class Vert3 implements Comparable < Vert3 > {

      /**
       * The coordinate of the vertex in world space.
       */
      public Vec3 coord;

      /**
       * The direction in which light will bounce from the surface
       * of the mesh at the vertex.
       */
      public Vec3 normal;

      /**
       * The texture (UV) coordinate for an image mapped onto the
       * mesh.
       */
      public Vec2 texCoord;

      /**
       * The default constructor.
       */
      public Vert3 () {

      }

      /**
       * Constructs a vertex from a coordinate, texture coordinate
       * and normal.
       *
       * @param coord
       *           the coordinate
       * @param texCoord
       *           the texture coordinate
       * @param normal
       *           the normal
       */
      public Vert3 (
            final Vec3 coord,
            final Vec2 texCoord,
            final Vec3 normal ) {

         this.set(coord, texCoord, normal);
      }

      /**
       * Compares this vertex to another by hash code.
       *
       * @param vert
       *           the comparisand
       * @return the comparison
       */
      @Override
      public int compareTo ( final Vert3 vert ) {

         final int a = this.hashCode();
         final int b = vert.hashCode();
         return a < b ? -1 : a > b ? 1 : 0;
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
       * Sets the coordinate, texture coordinate and normal of the
       * vertex by reference.
       *
       * @param coord
       *           the coordinate
       * @param texCoord
       *           the texture coordinate
       * @param normal
       *           the normal
       * @return this vertex
       */
      @Chainable
      public Vert3 set (
            final Vec3 coord,
            final Vec2 texCoord,
            final Vec3 normal ) {

         this.coord = coord;
         this.texCoord = texCoord;
         this.normal = normal;
         return this;
      }

      @Override
      public String toString () {

         return this.toString(4);
      }

      public String toString ( final int places ) {

         return new StringBuilder(512)
               .append("{ coord: ")
               .append(this.coord.toString(places))
               .append(", texCoord: ")
               .append(this.texCoord.toString(places))
               .append(", normal: ")
               .append(this.normal.toString(places))
               .append(' ').append('}')
               .toString();
      }
   }

   /**
    * A helper function for parsing an OBJ file. Attempts to
    * convert a string to an integer.
    *
    * @param i
    *           the string
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
   static Mesh3 uvSphere ( final float r, final int longitudes,
         final int latitudes, final Mesh3 target ) {

      // TODO: Unfinished.

      // int nind = 3 * detailU + (6 * detailU + 3) * (detailV -
      // 2) + 3 * detailU;
      // int[] indices = new int[nind];

      // final float xs[][] = new float[detailU][detailV];
      // final float ys[][] = new float[detailU][detailV];
      // final float zs[][] = new float[detailU][detailV];
      //
      // for(int i = 0; i < detailU; ++i) {
      // for(int j = 0; j < detailV; ++j) {
      //
      // }
      // }

      final int vlats = latitudes < 3 ? 3 : latitudes;
      final int vlons = longitudes < 3 ? 3 : longitudes;

      final float[] cosTheta = new float[vlons];
      final float[] sinTheta = new float[vlons];
      final float uToPrc = 1.0f / vlons;
      for (int i = 0; i < vlons; ++i) {
         final float phi = Utils.lerpUnclamped(-IUtils.PI, IUtils.PI,
               i * uToPrc);
         cosTheta[i] = (float) Math.cos(phi);
         sinTheta[i] = (float) Math.sin(phi);
      }

      final float[] cosPhi = new float[vlats];
      final float[] sinPhi = new float[vlats];
      final float vToPrc = 1.0f / (vlats - 1.0f);
      for (int i = 0; i < vlats; ++i) {
         final float phi = Utils.lerpUnclamped(-IUtils.HALF_PI, IUtils.HALF_PI,
               i * vToPrc);
         cosPhi[i] = (float) Math.cos(phi);
         sinPhi[i] = (float) Math.sin(phi);
      }

      return target;
   }

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
   public static Vec3 calcDimensions ( final Mesh3 mesh, final Vec3 dim ) {

      float xMin = Float.MAX_VALUE;
      float xMax = Float.MIN_VALUE;

      float yMin = Float.MAX_VALUE;
      float yMax = Float.MIN_VALUE;

      float zMin = Float.MAX_VALUE;
      float zMax = Float.MIN_VALUE;

      final Vec3[] coords = mesh.coords;
      final int len = coords.length;

      for (int i = 0; i < len; ++i) {

         final Vec3 coord = coords[i];
         final float x = coord.x;
         final float y = coord.y;
         final float z = coord.z;

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

         if (z < zMin) {
            zMin = z;
         } else if (z > zMax) {
            zMax = z;
         }
      }

      return dim.set(xMax - xMin, yMax - yMin, zMax - zMin);
   }

   /**
    * Generates a cube mesh.
    *
    * @param target
    *           the output mesh
    * @return the cube
    */
   @Experimental
   public static Mesh3 cube ( final Mesh3 target ) {

      final Vec3[] coords = new Vec3[] {
            /* 00 */ new Vec3(-0.5f, -0.5f, -0.5f),
            /* 01 */ new Vec3(-0.5f, -0.5f, +0.5f),
            /* 02 */ new Vec3(-0.5f, +0.5f, -0.5f),
            /* 03 */ new Vec3(-0.5f, +0.5f, +0.5f),
            /* 04 */ new Vec3(+0.5f, -0.5f, -0.5f),
            /* 05 */ new Vec3(+0.5f, -0.5f, +0.5f),
            /* 06 */ new Vec3(+0.5f, +0.5f, -0.5f),
            /* 07 */ new Vec3(+0.5f, +0.5f, +0.5f)
      };

      final Vec3[] normals = new Vec3[] {
            new Vec3(-1.0f, 0.0f, 0.0f),
            new Vec3(0.0f, +1.0f, 0.0f), /* 01 Forward */
            new Vec3(+1.0f, 0.0f, 0.0f), /* 02 Right */
            new Vec3(0.0f, -1.0f, 0.0f), /* 03 Back */
            new Vec3(0.0f, 0.0f, -1.0f), /* 04 Down */
            new Vec3(0.0f, 0.0f, +1.0f) /* 05 Up */
      };

      final Vec2[] texCoords = new Vec2[] {
            new Vec2(0.0f, 0.0f),
            new Vec2(1.0f, 0.0f), /* 01 Right-Bottom */
            new Vec2(1.0f, 1.0f), /* 02 Right-Top */
            new Vec2(0.0f, 1.0f) /* 03 Left-Top */
      };

      final int[][][] faces = new int[][][] {
            { { 1, 1, 0 }, { 2, 3, 0 }, { 0, 2, 0 } }, /* 00 */
            { { 3, 1, 1 }, { 6, 3, 1 }, { 2, 2, 1 } }, /* 01 */
            { { 7, 1, 2 }, { 4, 3, 2 }, { 6, 2, 2 } }, /* 02 */
            { { 5, 1, 3 }, { 0, 3, 3 }, { 4, 2, 3 } }, /* 03 */
            { { 6, 1, 4 }, { 0, 3, 4 }, { 2, 2, 4 } }, /* 04 */
            { { 3, 1, 5 }, { 5, 3, 5 }, { 7, 2, 5 } }, /* 05 */
            { { 1, 1, 0 }, { 3, 0, 0 }, { 2, 3, 0 } }, /* 06 */
            { { 3, 1, 1 }, { 7, 0, 1 }, { 6, 3, 1 } }, /* 07 */
            { { 7, 1, 2 }, { 5, 0, 2 }, { 4, 3, 2 } }, /* 08 */
            { { 5, 1, 3 }, { 1, 0, 3 }, { 0, 3, 3 } }, /* 09 */
            { { 6, 1, 4 }, { 4, 0, 4 }, { 0, 3, 4 } }, /* 10 */
            { { 3, 1, 5 }, { 1, 0, 5 }, { 5, 3, 5 } } /* 11 */
      };

      target.name = "Cube";
      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * Creates an dodecahedron, a Platonic solid with twelve
    * faces.
    *
    * @param target
    *           the output mesh
    * @return the dodecahedron
    */
   public static final Mesh3 dodecahedron ( final Mesh3 target ) {

      /*
       * double r = 0.5d; double phi = (1.0d + Math.sqrt(5.0d)) /
       * 2.0d; double b = r * (1.0d / phi); double c = r * (2.0d -
       * phi); 0.3090169943749474, 0.19098300562505255
       */
      final Vec3[] coords = new Vec3[] {
            /* 00 */ new Vec3(0.190983f, 0.0f, 0.5f),
            /* 01 */ new Vec3(-0.190983f, 0.0f, 0.5f),
            /* 02 */ new Vec3(-0.309017f, 0.309017f, 0.309017f),
            /* 03 */ new Vec3(0.0f, 0.5f, 0.190983f),
            /* 04 */ new Vec3(0.309017f, 0.309017f, 0.309017f),
            /* 05 */ new Vec3(-0.190983f, 0.0f, 0.5f),
            /* 06 */ new Vec3(0.190983f, 0.0f, 0.5f),
            /* 07 */ new Vec3(0.309017f, -0.309017f, 0.309017f),
            /* 08 */ new Vec3(0.0f, -0.5f, 0.190983f),
            /* 09 */ new Vec3(-0.309017f, -0.309017f, 0.309017f),
            /* 10 */ new Vec3(0.190983f, 0.0f, -0.5f),
            /* 11 */ new Vec3(-0.190983f, 0.0f, -0.5f),
            /* 12 */ new Vec3(-0.309017f, -0.309017f, -0.309017f),
            /* 13 */ new Vec3(0.0f, -0.5f, -0.190983f),
            /* 14 */ new Vec3(0.309017f, -0.309017f, -0.309017f),
            /* 15 */ new Vec3(-0.190983f, 0.0f, -0.5f),
            /* 16 */ new Vec3(0.190983f, 0.0f, -0.5f),
            /* 17 */ new Vec3(0.309017f, 0.309017f, -0.309017f),
            /* 18 */ new Vec3(0.0f, 0.5f, -0.190983f),
            /* 19 */ new Vec3(-0.309017f, 0.309017f, -0.309017f),
            /* 20 */ new Vec3(0.0f, 0.5f, -0.190983f),
            /* 21 */ new Vec3(0.0f, 0.5f, 0.190983f),
            /* 22 */ new Vec3(0.309017f, 0.309017f, 0.309017f),
            /* 23 */ new Vec3(0.5f, 0.190983f, 0.0f),
            /* 24 */ new Vec3(0.309017f, 0.309017f, -0.309017f),
            /* 25 */ new Vec3(0.0f, 0.5f, 0.190983f),
            /* 26 */ new Vec3(0.0f, 0.5f, -0.190983f),
            /* 27 */ new Vec3(-0.309017f, 0.309017f, -0.309017f),
            /* 28 */ new Vec3(-0.5f, 0.190983f, 0.0f),
            /* 29 */ new Vec3(-0.309017f, 0.309017f, 0.309017f),
            /* 30 */ new Vec3(0.0f, -0.5f, -0.190983f),
            /* 31 */ new Vec3(0.0f, -0.5f, 0.190983f),
            /* 32 */ new Vec3(-0.309017f, -0.309017f, 0.309017f),
            /* 33 */ new Vec3(-0.5f, -0.190983f, 0.0f),
            /* 34 */ new Vec3(-0.309017f, -0.309017f, -0.309017f),
            /* 35 */ new Vec3(0.0f, -0.5f, 0.190983f),
            /* 36 */ new Vec3(0.0f, -0.5f, -0.190983f),
            /* 37 */ new Vec3(0.309017f, -0.309017f, -0.309017f),
            /* 38 */ new Vec3(0.5f, -0.190983f, 0.0f),
            /* 39 */ new Vec3(0.309017f, -0.309017f, 0.309017f),
            /* 40 */ new Vec3(0.5f, 0.190983f, 0.0f),
            /* 41 */ new Vec3(0.5f, -0.190983f, 0.0f),
            /* 42 */ new Vec3(0.309017f, -0.309017f, 0.309017f),
            /* 43 */ new Vec3(0.190983f, 0.0f, 0.5f),
            /* 44 */ new Vec3(0.309017f, 0.309017f, 0.309017f),
            /* 45 */ new Vec3(0.5f, -0.190983f, 0.0f),
            /* 46 */ new Vec3(0.5f, 0.190983f, 0.0f),
            /* 47 */ new Vec3(0.309017f, 0.309017f, -0.309017f),
            /* 48 */ new Vec3(0.190983f, 0.0f, -0.5f),
            /* 49 */ new Vec3(0.309017f, -0.309017f, -0.309017f),
            /* 50 */ new Vec3(-0.5f, 0.190983f, 0.0f),
            /* 51 */ new Vec3(-0.5f, -0.190983f, 0.0f),
            /* 52 */ new Vec3(-0.309017f, -0.309017f, -0.309017f),
            /* 53 */ new Vec3(-0.190983f, 0.0f, -0.5f),
            /* 54 */ new Vec3(-0.309017f, 0.309017f, -0.309017f),
            /* 55 */ new Vec3(-0.5f, -0.190983f, 0.0f),
            /* 56 */ new Vec3(-0.5f, 0.190983f, 0.0f),
            /* 57 */ new Vec3(-0.309017f, 0.309017f, 0.309017f),
            /* 58 */ new Vec3(-0.190983f, 0.0f, 0.5f),
            /* 59 */ new Vec3(-0.309017f, -0.309017f, 0.309017f)
      };

      final Vec2[] texCoords = new Vec2[] {
            /* 00 */ new Vec2(0.024472f, 0.654509f),
            /* 01 */ new Vec2(0.793893f, 0.095491f),
            /* 02 */ new Vec2(0.975528f, 0.654508f),
            /* 03 */ new Vec2(0.206107f, 0.095492f), /* 25 */
            /* 04 */ new Vec2(0.5f, 1.0f) /* 36 */
      };

      final Vec3[] normals = new Vec3[] {
            /* 00 */ new Vec3(0.0f, 0.5257f, 0.8507f),
            /* 01 */ new Vec3(0.0f, -0.5257f, 0.8507f),
            /* 02 */ new Vec3(0.0f, -0.5257f, -0.8507f),
            /* 03 */ new Vec3(0.0f, 0.5257f, -0.8507f),
            /* 04 */ new Vec3(-0.5257f, -0.8507f, 0.0f),
            /* 05 */ new Vec3(0.5257f, -0.8507f, 0.0f),
            /* 06 */ new Vec3(0.5257f, 0.8507f, 0.0f),
            /* 07 */ new Vec3(-0.5257f, 0.8507f, 0.0f),
            /* 08 */ new Vec3(-0.8507f, 0.0f, -0.5257f),
            /* 09 */ new Vec3(-0.8507f, 0.0f, 0.5257f),
            /* 10 */ new Vec3(0.8507f, 0.0f, 0.5257f),
            /* 11 */ new Vec3(0.8507f, 0.0f, -0.5257f)
      };

      final int[][][] faces = new int[][][] {
            { { 4, 0, 0 }, { 2, 1, 0 }, { 1, 2, 0 } },
            { { 9, 0, 1 }, { 7, 1, 1 }, { 6, 2, 1 } },
            { { 14, 0, 2 }, { 12, 1, 2 }, { 11, 2, 2 } },
            { { 19, 0, 3 }, { 17, 1, 3 }, { 16, 2, 3 } },
            { { 24, 0, 4 }, { 22, 1, 4 }, { 21, 2, 4 } },
            { { 29, 0, 5 }, { 27, 1, 5 }, { 26, 2, 5 } },
            { { 34, 0, 6 }, { 32, 1, 6 }, { 31, 2, 6 } },
            { { 39, 0, 7 }, { 37, 1, 7 }, { 36, 2, 7 } },
            { { 42, 1, 8 }, { 43, 3, 8 }, { 44, 0, 8 } },
            { { 47, 1, 9 }, { 48, 3, 9 }, { 49, 0, 9 } },
            { { 52, 1, 10 }, { 53, 3, 10 }, { 54, 0, 10 } },
            { { 57, 1, 11 }, { 58, 3, 11 }, { 59, 0, 11 } },
            { { 1, 2, 0 }, { 0, 4, 0 }, { 4, 0, 0 } },
            { { 4, 0, 0 }, { 3, 3, 0 }, { 2, 1, 0 } },
            { { 6, 2, 1 }, { 5, 4, 1 }, { 9, 0, 1 } },
            { { 9, 0, 1 }, { 8, 3, 1 }, { 7, 1, 1 } },
            { { 11, 2, 2 }, { 10, 4, 2 }, { 14, 0, 2 } },
            { { 14, 0, 2 }, { 13, 3, 2 }, { 12, 1, 2 } },
            { { 16, 2, 3 }, { 15, 4, 3 }, { 19, 0, 3 } },
            { { 19, 0, 3 }, { 18, 3, 3 }, { 17, 1, 3 } },
            { { 21, 2, 4 }, { 20, 4, 4 }, { 24, 0, 4 } },
            { { 24, 0, 4 }, { 23, 3, 4 }, { 22, 1, 4 } },
            { { 26, 2, 5 }, { 25, 4, 5 }, { 29, 0, 5 } },
            { { 29, 0, 5 }, { 28, 3, 5 }, { 27, 1, 5 } },
            { { 31, 2, 6 }, { 30, 4, 6 }, { 34, 0, 6 } },
            { { 34, 0, 6 }, { 33, 3, 6 }, { 32, 1, 6 } },
            { { 36, 2, 7 }, { 35, 4, 7 }, { 39, 0, 7 } },
            { { 39, 0, 7 }, { 38, 3, 7 }, { 37, 1, 7 } },
            { { 44, 0, 8 }, { 40, 4, 8 }, { 41, 2, 8 } },
            { { 41, 2, 8 }, { 42, 1, 8 }, { 44, 0, 8 } },
            { { 49, 0, 9 }, { 45, 4, 9 }, { 46, 2, 9 } },
            { { 46, 2, 9 }, { 47, 1, 9 }, { 49, 0, 9 } },
            { { 54, 0, 10 }, { 50, 4, 10 }, { 51, 2, 10 } },
            { { 51, 2, 10 }, { 52, 1, 10 }, { 54, 0, 10 } },
            { { 59, 0, 11 }, { 55, 4, 11 }, { 56, 2, 11 } },
            { { 56, 2, 11 }, { 57, 1, 11 }, { 59, 0, 11 } }
      };

      target.name = "Dodecahedron";
      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * Creates a mesh from an array of strings. This is a simple
    * obj file reader. It assumes that the faces data of the
    * mesh includes texture coordinates and normals. Material
    * information is not parsed from the file, as Processing
    * would not accurately recreate it.
    *
    * @param lines
    *           the String tokens
    * @param target
    *           the output mesh
    * @return the mesh
    */
   public static Mesh3 fromObj ( final String[] lines, final Mesh3 target ) {

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
                * Simplified. Assumes (incorrectly) that face will always
                * be formatted as "v/vt/vn".
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
      target.set(faceList.toArray(new int[faceList.size()][][]),
            coordList.toArray(new Vec3[coordList.size()]),
            texCoordList.toArray(new Vec2[texCoordList.size()]),
            normalList.toArray(new Vec3[normalList.size()]));
      return target;
   }

   /**
    * Creates an icosahedron, a Platonic solid with twenty
    * faces.
    *
    * @param target
    *           the output mesh
    * @return the icosahedron
    */
   public static final Mesh3 icosahedron ( final Mesh3 target ) {

      final Vec3[] coords = new Vec3[] {
            new Vec3(0.0f, 0.0f, -0.5f),
            new Vec3(0.3618f, -0.26286f, -0.223607f),
            new Vec3(-0.138193f, -0.42532f, -0.223607f),
            new Vec3(-0.447212f, 0.0f, -0.223607f),
            new Vec3(-0.138193f, 0.42532f, -0.223607f),
            new Vec3(0.3618f, 0.26286f, -0.223607f),
            new Vec3(0.138193f, -0.42532f, 0.223607f),
            new Vec3(-0.3618f, -0.26286f, 0.223607f),
            new Vec3(-0.3618f, 0.26286f, 0.223607f),
            new Vec3(0.138193f, 0.42532f, 0.223607f),
            new Vec3(0.447212f, 0.0f, 0.223607f),
            new Vec3(0.0f, 0.0f, 0.5f)
      };

      final Vec2[] texCoords = new Vec2[] {
            new Vec2(0.818181f, 0.0f),
            new Vec2(0.727272f, 0.157461f),
            new Vec2(0.90909f, 0.157461f),
            new Vec2(0.636363f, 0.0f),
            new Vec2(0.545454f, 0.157461f),
            new Vec2(0.090909f, 0.0f),
            new Vec2(0.0f, 0.157461f),
            new Vec2(0.181818f, 0.157461f),
            new Vec2(0.272727f, 0.0f),
            new Vec2(0.363636f, 0.157461f),
            new Vec2(0.454545f, 0.0f),
            new Vec2(0.636363f, 0.314921f),
            new Vec2(0.818181f, 0.314921f),
            new Vec2(0.090909f, 0.314921f),
            new Vec2(0.272727f, 0.314921f),
            new Vec2(0.454545f, 0.314921f),
            new Vec2(1.0f, 0.314921f),
            new Vec2(0.727272f, 0.472382f),
            new Vec2(0.909090f, 0.472382f),
            new Vec2(0.181818f, 0.472382f),
            new Vec2(0.363636f, 0.472382f),
            new Vec2(0.545454f, 0.472382f)
      };

      final Vec3[] normals = new Vec3[] {
            new Vec3(0.1876f, -0.57735026f, -0.7947f),
            new Vec3(0.6071f, 0.0f, -0.7947f),
            new Vec3(-0.4911f, -0.3568f, -0.7947f),
            new Vec3(-0.4911f, 0.3568f, -0.7947f),
            new Vec3(0.1876f, 0.57735026f, -0.7947f),
            new Vec3(0.9822f, 0.0f, -0.1876f),
            new Vec3(0.3035f, -0.9342f, -0.1876f),
            new Vec3(-0.7946f, -0.57735026f, -0.1876f),
            new Vec3(-0.7946f, 0.57735026f, -0.1876f),
            new Vec3(0.3035f, 0.9342f, -0.1876f),
            new Vec3(0.7946f, -0.57735026f, 0.1876f),
            new Vec3(-0.3035f, -0.9342f, 0.1876f),
            new Vec3(-0.9822f, 0.0f, 0.1876f),
            new Vec3(-0.3035f, 0.9342f, 0.1876f),
            new Vec3(0.7946f, 0.57735026f, 0.1876f),
            new Vec3(0.4911f, -0.3568f, 0.7947f),
            new Vec3(-0.1876f, -0.57735026f, 0.7947f),
            new Vec3(-0.6071f, 0.0f, 0.7947f),
            new Vec3(-0.1876f, 0.57735026f, 0.7947f),
            new Vec3(0.4911f, 0.3568f, 0.7947f)
      };

      final int[][][] faces = new int[][][] {
            { { 0, 0, 0 }, { 1, 1, 0 }, { 2, 2, 0 } },
            { { 1, 1, 1 }, { 0, 3, 1 }, { 5, 4, 1 } },
            { { 0, 5, 2 }, { 2, 6, 2 }, { 3, 7, 2 } },
            { { 0, 8, 3 }, { 3, 7, 3 }, { 4, 9, 3 } },
            { { 0, 10, 4 }, { 4, 9, 4 }, { 5, 4, 4 } },
            { { 1, 1, 5 }, { 5, 4, 5 }, { 10, 11, 5 } },
            { { 2, 2, 6 }, { 1, 1, 6 }, { 6, 12, 6 } },
            { { 3, 7, 7 }, { 2, 6, 7 }, { 7, 13, 7 } },
            { { 4, 9, 8 }, { 3, 7, 8 }, { 8, 14, 8 } },
            { { 5, 4, 9 }, { 4, 9, 9 }, { 9, 15, 9 } },
            { { 1, 1, 10 }, { 10, 11, 10 }, { 6, 12, 10 } },
            { { 2, 2, 11 }, { 6, 12, 11 }, { 7, 16, 11 } },
            { { 3, 7, 12 }, { 7, 13, 12 }, { 8, 14, 12 } },
            { { 4, 9, 13 }, { 8, 14, 13 }, { 9, 15, 13 } },
            { { 5, 4, 14 }, { 9, 15, 14 }, { 10, 11, 14 } },
            { { 6, 12, 15 }, { 10, 11, 15 }, { 11, 17, 15 } },
            { { 7, 16, 16 }, { 6, 12, 16 }, { 11, 18, 16 } },
            { { 8, 14, 17 }, { 7, 13, 17 }, { 11, 19, 17 } },
            { { 9, 15, 18 }, { 8, 14, 18 }, { 11, 20, 18 } },
            { { 10, 11, 19 }, { 9, 15, 19 }, { 11, 21, 19 } }
      };

      target.name = "Icosahedron";
      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * Creates an octahedron, a Platonic solid with eight faces.
    *
    * @param target
    *           the output mesh
    * @return the octahedron
    */
   public static final Mesh3 octahedron ( final Mesh3 target ) {

      final Vec3[] coords = new Vec3[] {
            new Vec3(0.0f, -0.5f, 0.0f),
            new Vec3(0.5f, 0.0f, 0.0f),
            new Vec3(-0.5f, 0.0f, 0.0f),
            new Vec3(0.0f, 0.5f, 0.0f),
            new Vec3(0.0f, 0.0f, 0.5f),
            new Vec3(0.0f, 0.0f, -0.5f)
      };

      final Vec2[] texCoords = new Vec2[] {
            new Vec2(0.5f, 0.5f),
            new Vec2(1.0f, 0.5f),
            new Vec2(0.5f, 1.0f),
            new Vec2(0.5f, 0.5f),
            new Vec2(0.0f, 0.5f),
            new Vec2(0.5f, 0.0f)
      };

      final Vec3[] normals = new Vec3[] {
            new Vec3(0.57735026f, -0.57735026f, 0.57735026f),
            new Vec3(0.57735026f, 0.57735026f, 0.57735026f),
            new Vec3(-0.57735026f, 0.57735026f, 0.57735026f),
            new Vec3(-0.57735026f, -0.57735026f, 0.57735026f),
            new Vec3(-0.57735026f, 0.57735026f, -0.57735026f),
            new Vec3(0.57735026f, 0.57735026f, -0.57735026f),
            new Vec3(0.57735026f, -0.57735026f, -0.57735026f),
            new Vec3(-0.57735026f, -0.57735026f, -0.57735026f)
      };

      final int[][][] faces = new int[][][] {
            { { 0, 0, 0 }, { 1, 1, 0 }, { 4, 2, 0 } },
            { { 1, 1, 1 }, { 3, 3, 1 }, { 4, 2, 1 } },
            { { 3, 3, 2 }, { 2, 4, 2 }, { 4, 2, 2 } },
            { { 2, 4, 3 }, { 0, 0, 3 }, { 4, 2, 3 } },
            { { 2, 4, 4 }, { 3, 3, 4 }, { 5, 5, 4 } },
            { { 3, 3, 5 }, { 1, 1, 5 }, { 5, 5, 5 } },
            { { 1, 1, 6 }, { 0, 0, 6 }, { 5, 5, 6 } },
            { { 0, 0, 7 }, { 2, 4, 7 }, { 5, 5, 7 } }
      };

      target.name = "Octahedron";
      return target.set(faces, coords, texCoords, normals);
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
    * @param target
    *           the output mesh
    * @return the mesh
    */
   public static final Mesh3 plane (
         final int cols,
         final int rows,
         final Mesh3 target ) {

      final int rval = rows < 1 ? 1 : rows;
      final int cval = cols < 1 ? 1 : cols;

      final int rval1 = rval + 1;
      final int cval1 = cval + 1;

      final float iToStep = 1.0f / rval;
      final float jToStep = 1.0f / cval;

      final Vec3[] coords = new Vec3[rval1 * cval1];
      final Vec2[] texCoords = new Vec2[coords.length];
      final Vec3[] normals = new Vec3[] { Vec3.up(new Vec3()) };

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
            coords[k] = new Vec3(xs[j], y, 0.0f);
            texCoords[k] = new Vec2(us[j], v);
         }
      }

      final int len = rval * cval;
      final int[][][] faces = new int[len + len][3][3];
      for (int k = 0, i = 0; i < rval; ++i) {
         final int noff0 = i * cval1;
         final int noff1 = (i + 1) * cval1;

         for (int j = 0; j < cval; ++j, k += 2) {
            final int n00 = noff0 + j;
            final int n10 = n00 + 1;
            final int n01 = noff1 + j;
            final int n11 = n01 + 1;

            faces[k] = new int[][] {
                  { n00, n00, 0 },
                  { n10, n10, 0 },
                  { n11, n11, 0 } };

            faces[k + 1] = new int[][] {
                  { n11, n11, 0 },
                  { n01, n01, 0 },
                  { n00, n00, 0 } };
         }
      }

      target.name = "Plane";
      return target.set(faces, coords, texCoords, normals);
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
   public static final Mesh3 plane ( final int div, final Mesh3 target ) {

      return Mesh3.plane(div, div, target);
   }

   /**
    * Generates a regular convex polygon.
    *
    * @param sectors
    *           the number of sides
    * @param target
    *           the output mesh
    * @return the polygon
    */
   public static Mesh3 polygon ( final int sectors, final Mesh3 target ) {

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
         texCoords[j] = Vec2.add(pureCoord, uvCenter, new Vec2());

         coords[j] = new Vec3(pureCoord.x, pureCoord.y, 0.0f);

         final int k = 1 + j % seg;
         final int[][] face = new int[][] {
               { 0, 0, 0 },
               { j, j, 0 },
               { k, k, 0 } };
         faces[i] = face;
      }

      target.name = "Polygon";
      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * Creates a square.
    *
    * @param target
    *           the output mesh
    * @return the square
    */
   public static final Mesh3 square ( final Mesh3 target ) {

      final Vec3[] coords = new Vec3[] {
            new Vec3(0.5f, 0.5f, 0.0f),
            new Vec3(-0.5f, 0.5f, 0.0f),
            new Vec3(-0.5f, -0.5f, 0.0f),
            new Vec3(0.5f, -0.5f, 0.0f)
      };

      final Vec2[] texCoords = new Vec2[] {
            new Vec2(1.0f, 1.0f),
            new Vec2(0.0f, 1.0f),
            new Vec2(0.0f, 0.0f),
            new Vec2(1.0f, 0.0f)
      };

      final int[][][] faces = new int[][][] {
            { { 0, 0, 0 }, { 1, 1, 0 }, { 2, 2, 0 } },
            { { 2, 2, 0 }, { 3, 3, 0 }, { 0, 0, 0 } }
      };

      final Vec3[] normals = new Vec3[] { Vec3.up(new Vec3()) };

      target.name = "Square";
      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * Creates a tetrahedron, a Platonic solid with four faces.
    *
    * @param target
    *           the output mesh
    * @return the tetrahedron
    */
   public static final Mesh3 tetrahedron ( final Mesh3 target ) {

      /*
       * double r = 0.5d; double a = r * Math.sqrt(8.0d / 9.0d);
       * double b = r * Math.sqrt(2.0d / 9.0d); double c = r *
       * Math.sqrt(2.0d / 3.0d); double d = r * (1.0d / 3.0d);
       *
       * 0.47140452079103168293389624140323,
       * 0.23570226039551584146694812070162,
       * 0.40824829046386301636621401245098
       */
      final Vec3[] coords = new Vec3[] {
            new Vec3(0.0f, 0.47140452f, -0.16666667f),
            new Vec3(-0.4082483f, -0.23570226f, -0.16666667f),
            new Vec3(0.4082483f, -0.23570226f, -0.16666667f),
            new Vec3(0.0f, 0.0f, 0.5f)
      };

      final Vec2[] texCoords = new Vec2[] {
            new Vec2(0.5f, 1.0f),
            new Vec2(1.0f, 0.0f),
            new Vec2(0.0f, 0.0f),
            new Vec2(0.5f, 0.33333333f)
      };

      final Vec3[] normals = new Vec3[] {
            new Vec3(0.0f, 0.0f, -1.0f),
            new Vec3(-0.8165f, 0.4714f, 0.33333333f),
            new Vec3(0.8165f, 0.4714f, 0.33333333f),
            new Vec3(0.0f, -0.9428f, 0.33333333f)
      };

      final int[][][] faces = new int[][][] {
            { { 0, 0, 0 }, { 2, 1, 0 }, { 1, 2, 0 } },
            { { 0, 0, 1 }, { 1, 2, 1 }, { 3, 3, 1 } },
            { { 0, 0, 2 }, { 3, 3, 2 }, { 2, 1, 2 } },
            { { 1, 2, 3 }, { 2, 1, 3 }, { 3, 3, 3 } }
      };

      target.name = "Tetrahedron";
      return target.set(faces, coords, texCoords, normals);
   }

   @Experimental
   public static Mesh3 torus ( final float radius, final float tubeRadius,
         final int detailX, final int detailY,
         final Mesh3 target ) {

      // TODO: REWORKING...

      final float tubeRatio = tubeRadius / radius;
      final float toV = 1.0f / detailY;
      final float toU = 1.0f / detailX;
      final int detailY1 = detailY + 1;
      final int detailX1 = detailX + 1;

      final int len = detailY1 * detailX1;
      final Vec3[] coords = new Vec3[len];
      final Vec2[] texCoords = new Vec2[len];
      final Vec3[] normals = new Vec3[len];
      for (int k = 0, i = 0; i < detailY1; ++i) {
         final float v = i * toV;
         // final float phi = IUtils.TAU * v;
         // final float cosPhi = Utils.cos(phi);
         // final float sinPhi = Utils.sin(phi);
         final float cosPhi = SinCos.eval(v);
         final float sinPhi = SinCos.eval(v - 0.25f);
         final float r = 1.0f + tubeRatio * cosPhi;

         for (int j = 0; j < detailX1; ++j, ++k) {
            final float u = j / toU;
            // final float theta = IUtils.TAU * u;
            // final float cosTheta = Utils.cos(theta);
            // final float sinTheta = Utils.sin(theta);
            final float cosTheta = SinCos.eval(u);
            final float sinTheta = SinCos.eval(u - 0.25f);

            coords[k] = new Vec3(r * cosTheta, r * sinTheta,
                  tubeRatio * sinPhi);

            texCoords[k] = new Vec2(u, v);

            normals[k] = new Vec3(cosPhi * cosTheta, cosPhi * sinTheta, sinPhi);
         }
      }

      return target;
   }

   /**
    * Creates a triangle.
    *
    * @param target
    *           the output mesh
    * @return the triangle
    */
   public static final Mesh3 triangle ( final Mesh3 target ) {

      final Vec3[] coords = new Vec3[] {
            new Vec3(0.0f, 0.5f, 0.0f),
            new Vec3(-0.4330127f, -0.25f, 0.0f),
            new Vec3(0.4330127f, -0.25f, 0.0f)
      };

      final Vec2[] texCoords = new Vec2[] {
            new Vec2(0.5f, 1.0f),
            new Vec2(0.0669873f, 0.25f),
            new Vec2(0.9330127f, 0.25f)
      };

      final Vec3[] normals = new Vec3[] { Vec3.up(new Vec3()) };

      final int[][][] faces = new int[][][] {
            { { 0, 0, 0 }, { 1, 1, 0 }, { 2, 2, 0 } } };

      target.name = "Triangle";
      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * An array of coordinates in the mesh.
    */
   public Vec3[] coords;

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
    * An array of normals to indicate how light will bounce off
    * the mesh's surface.
    */
   public Vec3[] normals;

   /**
    * The texture (UV) coordinates that describe how an image
    * is mapped onto the geometry of the mesh. Typically in the
    * range [0.0, 1.0].
    */
   public Vec2[] texCoords;

   /**
    * The default constructor.
    */
   public Mesh3 () {

      super();
   }

   /**
    * Creates a mesh from arrays of faces, coordinates, texture
    * coordinates and normals. The mesh's arrays are set by
    * reference, not by value.
    *
    * @param faces
    *           the faces array
    * @param coords
    *           the coordinates array
    * @param texCoords
    *           the texture coordinates array
    * @param normals
    *           the normals array
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
    * Creates a named mesh.
    *
    * @param name
    *           the mesh name
    */
   public Mesh3 ( final String name ) {

      super(name);
   }

   /**
    * Creates a named mesh from arrays of faces, coordinates,
    * texture coordinates and normals. The mesh's arrays are
    * set by reference, not by value.
    *
    * @param name
    *           the mesh name
    * @param faces
    *           the faces array
    * @param coords
    *           the coordinates array
    * @param texCoords
    *           the texture coordinates array
    * @param normals
    *           the normals array
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
      result.append("{\"name\": \"").append(this.name)
            .append("\", \"material_index\": ").append(this.materialIndex)
            .append(", \"vertices\": [");

      final int vlen = this.coords.length;
      final int vlast = vlen - 1;
      for (int i = 0; i < vlen; ++i) {
         result.append(this.coords[i].toBlenderCode());
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
      final Mesh3 other = (Mesh3) obj;
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
   public Face3 getFace ( final int i, final Face3 target ) {

      final int[][] face = this.faces[i];
      final int len = face.length;
      final Vert3[] vertices = new Vert3[len];

      for (int j = 0; j < len; ++j) {
         final int[] vert = face[j];
         vertices[j] = new Vert3(this.coords[vert[0]], this.texCoords[vert[1]],
               this.normals[vert[2]]);
      }

      return target.set(vertices);
   }

   /**
    * Gets an array of faces from teh mesh.
    *
    * @return the faces array
    */
   public Face3[] getFaces () {

      final int len0 = this.faces.length;
      final Face3[] result = new Face3[len0];

      for (int i = 0; i < len0; ++i) {

         final int[][] fs0 = this.faces[i];
         final int len1 = fs0.length;
         final Vert3[] verts = new Vert3[len1];

         for (int j = 0; j < len1; ++j) {

            final int[] fs1 = fs0[j];
            verts[j] = new Vert3(this.coords[fs1[0]], this.texCoords[fs1[1]],
                  this.normals[fs1[2]]);
         }

         result[i] = new Face3(verts);
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
   public Vert3 getVertex ( final int i, final int j, final Vert3 target ) {

      final int[] vert = this.faces[i][j];
      return target.set(this.coords[vert[0]], this.texCoords[vert[1]],
            this.normals[vert[2]]);
   }

   /**
    * Get an array of vertices from the mesh.
    *
    * @return the vertices
    */
   public Vert3[] getVertices () {

      final ArrayList < Vert3 > result = new ArrayList <>();
      Vert3 trial = new Vert3();
      final int len0 = this.faces.length;

      for (int i = 0; i < len0; ++i) {

         final int[][] fs = this.faces[i];
         final int len1 = fs.length;

         for (int j = 0; j < len1; ++j) {

            final int[] f = fs[j];
            trial.set(this.coords[f[0]], this.texCoords[f[1]],
                  this.normals[f[2]]);

            if (!result.contains(trial)) {
               result.add(trial);
               trial = new Vert3();
            }
         }
      }

      return result.toArray(new Vert3[result.size()]);
   }

   @Override
   public int hashCode () {

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL ^ Arrays.hashCode(this.coords);
      hash = hash * IUtils.HASH_MUL ^ Arrays.deepHashCode(this.faces);
      return hash;
   }

   /**
    * Rotates all coordinates and normals in the mesh by an
    * angle around an arbitrary axis.
    *
    * @param radians
    *           the angle in radians
    * @param axis
    *           the axis of rotation
    * @return this mesh
    * @see Vec3#rotate(Vec3, float, Vec3, Vec3)
    */
   @Chainable
   public Mesh3 rotate ( final float radians, final Vec3 axis ) {

      final float nrm = IUtils.ONE_TAU * radians;
      final float cosa = SinCos.eval(nrm);
      final float sina = SinCos.eval(nrm - 0.25f);

      Vec3 c;
      Vec3 n;

      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         c = this.coords[i];
         n = this.normals[i];
         Vec3.rotate(c, cosa, sina, axis, c);
         Vec3.rotate(n, cosa, sina, axis, n);
      }

      return this;
   }

   /**
    * Rotates all coordinates and normals in the mesh by a
    * quaternion.
    *
    * @param q
    *           the quaternion
    * @return the mesh
    */
   @Chainable
   public Mesh3 rotate ( final Quaternion q ) {

      Vec3 c;
      Vec3 n;

      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         c = this.coords[i];
         n = this.normals[i];
         Quaternion.mulVector(q, c, c);
         Quaternion.mulVector(q, n, n);
      }
      return this;
   }

   /**
    * Rotates all coordinates and normals in the mesh by an
    * angle around the x axis.
    *
    * @param radians
    *           the angle in radians
    * @return this mesh
    * @see Vec3#rotateX(Vec3, float, Vec3)
    */
   @Chainable
   public Mesh3 rotateX ( final float radians ) {

      final float nrm = IUtils.ONE_TAU * radians;
      final float cosa = SinCos.eval(nrm);
      final float sina = SinCos.eval(nrm - 0.25f);

      Vec3 c;
      Vec3 n;

      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         c = this.coords[i];
         n = this.normals[i];
         Vec3.rotateX(c, cosa, sina, n);
         Vec3.rotateX(n, cosa, sina, n);
      }

      return this;
   }

   /**
    * Rotates all coordinates and normals in the mesh by an
    * angle around the y axis.
    *
    * @param radians
    *           the angle in radians
    * @return this mesh
    * @see Vec3#rotateY(Vec3, float, Vec3)
    */
   @Chainable
   public Mesh3 rotateY ( final float radians ) {

      final float nrm = IUtils.ONE_TAU * radians;
      final float cosa = SinCos.eval(nrm);
      final float sina = SinCos.eval(nrm - 0.25f);

      Vec3 c;
      Vec3 n;

      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         c = this.coords[i];
         n = this.normals[i];
         Vec3.rotateY(c, cosa, sina, c);
         Vec3.rotateY(n, cosa, sina, n);
      }

      return this;
   }

   /**
    * Rotates all coordinates and normals in the mesh by an
    * angle around the z axis.
    *
    * @param radians
    *           the angle in radians
    * @return this mesh
    * @see Vec3#rotateZ(Vec3, float, Vec3)
    */
   @Chainable
   public Mesh3 rotateZ ( final float radians ) {

      final float nrm = IUtils.ONE_TAU * radians;
      final float cosa = SinCos.eval(nrm);
      final float sina = SinCos.eval(nrm - 0.25f);

      Vec3 c;
      Vec3 n;

      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         c = this.coords[i];
         n = this.normals[i];
         Vec3.rotateZ(c, cosa, sina, c);
         Vec3.rotateZ(n, cosa, sina, n);
      }

      return this;
   }

   /**
    * Scales all coordinates in the mesh by a scalar.
    *
    * @param scale
    *           the scalar
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
    * @param scale
    *           the vector
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
    * @param faces
    *           the faces array
    * @param coords
    *           the coordinates array
    * @param texCoords
    *           the texture coordinates array
    * @param normals
    *           the normals array
    * @return this mesh
    */
   @Chainable
   public Mesh3 set ( final int[][][] faces, final Vec3[] coords,
         final Vec2[] texCoords, final Vec3[] normals ) {

      this.faces = faces;
      this.coords = coords;
      this.texCoords = texCoords;
      this.normals = normals;
      return this;
   }

   /**
    * Renders the mesh as a string following the Wavefront OBJ
    * file format.
    *
    * @return the string
    */
   public String toObjString () {

      final int coordsLen = this.coords.length;
      final int texCoordsLen = this.texCoords.length;
      final int normalsLen = this.normals.length;
      final int facesLen = this.faces.length;
      final StringBuilder result = new StringBuilder();

      /*
       * Append a comment listing the number of coordinates,
       * texture coordinates, normals and faces.
       */
      result.append("# v: ").append(coordsLen).append(", vt: ")
            .append(texCoordsLen).append(", vn: ").append(normalsLen)
            .append(", f: ").append(facesLen).append('\n').append('\n');

      result.append('o').append(' ').append(this.name).append('\n')
            .append('\n');

      for (final Vec3 coord : this.coords) {
         result.append('v').append(' ').append(coord.toObjString())
               .append('\n');
      }
      result.append('\n');

      for (final Vec2 texCoord : this.texCoords) {
         result.append("vt ").append(texCoord.toObjString()).append('\n');
      }
      result.append('\n');

      for (final Vec3 normal : this.normals) {
         result.append("vn ").append(normal.toObjString()).append('\n');
      }
      result.append('\n');

      for (int i = 0; i < facesLen; ++i) {
         final int[][] face = this.faces[i];
         final int vLen = face.length;
         result.append('f').append(' ');
         for (int j = 0; j < vLen; ++j) {

            /* Indices in an .obj file start at 1, not 0. */
            final int[] vert = face[j];
            result.append(vert[0] + 1).append('/').append(vert[1] + 1)
                  .append('/').append(vert[2] + 1).append(' ');
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

      sb.append("{ name: \"").append(this.name).append("\", coords: [");
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

      sb.append(" ],\nnormals: [");
      if (this.normals != null) {
         sb.append('\n');
         final int len = Math.min(this.normals.length, truncate);
         final int last = len - 1;
         for (int i = 0; i < len; ++i) {
            sb.append(this.normals[i].toString(places));
            if (i < last) {
               sb.append(',').append('\n');
            }
         }
      }

      if (this.normals.length > truncate) {
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
                * There should be 3 indices: coordinate, texture coordinate
                * and normal.
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
    * Translates all coordinates in the mesh by a vector.
    *
    * @param v
    *           the vector
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
}
