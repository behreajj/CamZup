package camzup.core;

import java.util.ArrayList;

/**
 * Organizes data needed to draw a two dimensional shape
 * using vertices and faces.
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
      public Vert2 ( final Vec2 coord, final Vec2 texCoord ) {

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
      public Vert2 set ( final Vec2 coord, final Vec2 texCoord ) {

         this.coord = coord;
         this.texCoord = texCoord;
         return this;
      }
   }

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
   public static Vec2 calcDimensions ( final Mesh2 mesh, final Vec2 dim ) {

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

      return dim.set(xMax - xMin, yMax - yMin);
   }

   /**
    * Creates a regular convex polygon.
    *
    * @param target
    *           the output mesh
    * @param sectors
    *           the number of sides
    * @return the polygon
    */
   public static Mesh2 polygon ( final Mesh2 target, final int sectors ) {

      return Mesh2.polygon(target, sectors, Mesh2.DEFAULT_POLY_TYPE);
   }

   /**
    * Creates a regular convex polygon.
    *
    * @param target
    *           the output mesh
    * @param sectors
    *           the number of sides
    * @param poly
    *           the polygon type
    * @return the polygon
    */
   public static Mesh2 polygon ( final Mesh2 target, final int sectors,
         final PolyType poly ) {

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
               final float theta = i * toTheta;
               Vec2.fromPolar(theta, 0.5f, pureCoord);
               texCoords[i] = Vec2.add(pureCoord, uvCenter, new Vec2());
               coords[i] = new Vec2(pureCoord);
               ngon[i] = new int[] { i, i };
            }

            return target.set(faces, coords, texCoords);

         case TRI:
         default:

            coords = new Vec2[seg + 1];
            texCoords = new Vec2[seg + 1];
            faces = new int[seg][3][2];

            coords[0] = new Vec2();
            texCoords[0] = uvCenter;

            for (int i = 0, j = 1; i < seg; ++i, ++j) {
               final float theta = i * toTheta;
               Vec2.fromPolar(theta, 0.5f, pureCoord);
               texCoords[j] = Vec2.add(pureCoord, uvCenter, new Vec2());
               coords[j] = new Vec2(pureCoord);

               final int k = 1 + j % seg;
               final int[][] face = new int[][] {
                     { 0, 0 },
                     { j, j },
                     { k, k } };
               faces[i] = face;
            }

            return target.set(faces, coords, texCoords);
      }
   }

   /**
    * Creates a rectangle.
    *
    * @param target
    *           the output mesh
    * @return the rectangle
    */
   public static final Mesh2 rectangle ( final Mesh2 target ) {

      return Mesh2.rectangle(target, Mesh2.DEFAULT_POLY_TYPE);
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
   public static final Mesh2 rectangle ( final Mesh2 target,
         final PolyType poly ) {

      target.name = "Rectangle";

      final Vec2[] coords = new Vec2[] {
            new Vec2(0.5f, 0.5f),
            new Vec2(-0.5f, 0.5f),
            new Vec2(-0.5f, -0.5f),
            new Vec2(0.5f, -0.5f) };

      final Vec2[] texCoords = new Vec2[] {
            new Vec2(1.0f, 1.0f),
            new Vec2(0.0f, 1.0f),
            new Vec2(0.0f, 0.0f),
            new Vec2(1.0f, 0.0f) };

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
            new Vec2(0.4330127f, -0.25f) };

      final Vec2[] texCoords = new Vec2[] {
            new Vec2(0.5f, 1.0f),
            new Vec2(0.0669873f, 0.25f),
            new Vec2(0.9330127f, 0.25f) };

      final int[][][] faces = new int[][][] {
            { { 0, 0 }, { 1, 1 }, { 2, 2 } } };

      return target.set(faces, coords, texCoords);
   }

   /**
    * An array of coordinates in the mesh.
    */
   public Vec2[] coords;

   /**
    * The faces array does not include information about the
    * faces themselves, but rather indices to other arrays
    * which contain vertex data. It is a three-dimensional
    * array organized by
    * <ol>
    * <li>the number of faces;</li>
    * <li>the number of vetices per faces;</li>
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
    * Gets a face from the mesh.
    *
    * @param i
    *           the index
    * @param target
    *           the output face
    * @return the face
    */
   public Face2 getFace ( final int i, final Face2 target ) {

      final int len = this.faces[i].length;
      final Vert2[] vertices = new Vert2[len];
      for (int j = 0; j < len; ++j) {
         vertices[j] = this.getVertex(i, j, new Vert2());
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
         final int len1 = this.faces[i].length;
         final Vert2[] verts = new Vert2[len1];
         for (int j = 0; j < len1; ++j) {
            verts[j] = this.getVertex(i, j, new Vert2());
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

      return target.set(this.coords[this.faces[i][j][0]],
            this.texCoords[this.faces[i][j][1]]);
   }

   /**
    * Get an array of vertices from the mesh.
    *
    * @return the vertices
    */
   public Vert2[] getVertices () {

      final ArrayList < Vert2 > result = new ArrayList <>();
      Vert2 trial;

      final int len0 = this.faces.length;
      for (int i = 0; i < len0; ++i) {

         final int len1 = this.faces[i].length;
         for (int j = 0; j < len1; ++j) {

            trial = this.getVertex(i, j, new Vert2());
            if (!result.contains(trial)) {
               result.add(trial);
               trial = new Vert2();
            }
         }
      }
      return result.toArray(new Vert2[result.size()]);
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

      final float cosa = (float) Math.cos(radians);
      final float sina = (float) Math.sin(radians);
      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         Vec2.rotateZ(this.coords[i], cosa, sina, this.coords[i]);
      }
      return this;
   }

   /**
    * Scales all coordinates in the mesh by a scalar.
    *
    * @param scale
    *           the scalar
    * @return this mesh
    * @see Vec2#mult(Vec2, float, Vec2)
    */
   @Chainable
   public Mesh2 scale ( final float scale ) {

      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         Vec2.mult(this.coords[i], scale, this.coords[i]);
      }
      return this;
   }

   /**
    * Scales all coordinates in the mesh by a vector.
    *
    * @param scale
    *           the vector
    * @return this mesh
    * @see Vec2#mult(Vec2, Vec2, Vec2)
    */
   @Chainable
   public Mesh2 scale ( final Vec2 scale ) {

      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         Vec2.mult(this.coords[i], scale, this.coords[i]);
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
   public String toObjString () {

      // TODO: Needs testing.
      final int coordsLen = this.coords.length;
      final int texCoordsLen = this.texCoords.length;
      final int facesLen = this.faces.length;
      final StringBuilder result = new StringBuilder();

      /**
       * Append a comment listing the number of coordinates,
       * texture coordinates, normals and faces.
       */
      result.append("# v: ")
            .append(coordsLen)
            .append(", vt: ")
            .append(texCoordsLen)
            .append(", vn: ")
            .append(1)
            .append(", f: ")
            .append(facesLen).append("\n \n");

      result.append("o ")
            .append(this.name)
            .append("\n \n");

      for (final Vec2 coord : this.coords) {
         result.append("v ")
               .append(coord.toObjString())
               .append(" 0.0 ")
               .append('\n');
      }
      result.append(" \n");

      for (final Vec2 texCoord : this.texCoords) {
         result.append("vt ")
               .append(texCoord.toObjString())
               .append('\n');
      }
      result.append('\n');

      result.append("vn 0.0 0.0 1.0 \n");

      for (int i = 0; i < facesLen; ++i) {
         final int[][] face = this.faces[i];
         final int vLen = face.length;
         result.append("f ");
         for (int j = 0; j < vLen; ++j) {

            // Indices in an .obj file start at 1, not 0.
            final int[] vert = face[j];
            result.append(vert[0] + 1)
                  .append('/')
                  .append(vert[1] + 1)
                  .append('/')
                  .append(vert[2] + 1)
                  .append(' ');
         }
         result.append('\n');
      }
      result.append('\n');
      return result.toString();
   }

   /**
    * Renders the mesh as a string following the SVG file
    * format.
    *
    * @return the string
    */
   public String toSvgString () {

      final StringBuilder result = new StringBuilder();

      final int[][][] fs = this.faces;
      final Vec2[] vs = this.coords;
      final int flen0 = fs.length;
      for (int i = 0; i < flen0; ++i) {
         final int[][] f = fs[i];
         final int flen1 = f.length;

         Vec2 v = vs[f[0][0]];
         result.append("<path d=\"M ")
               .append(v.toSvgString())
               .append(' ');

         for (int j = 1; j < flen1; ++j) {
            v = vs[f[j][0]];

            result.append("L ")
                  .append(v.toSvgString())
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

      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         Vec2.add(this.coords[i], v, this.coords[i]);
      }
      return this;
   }
}
