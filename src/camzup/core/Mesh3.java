package camzup.core;

import java.util.ArrayList;

/**
 * Organizes data needed to draw a three dimensional shape
 * using vertices and faces.
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
      public Face3 set ( final Vert3[] vertices ) {

         this.vertices = vertices;
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
   public static Mesh3 cube ( final Mesh3 target ) {

      final Vec3[] coords = new Vec3[] {
            new Vec3(-0.5f, -0.5f, -0.5f), /* 00 */
            new Vec3(-0.5f, -0.5f, +0.5f), /* 01 */
            new Vec3(-0.5f, +0.5f, -0.5f), /* 02 */
            new Vec3(-0.5f, +0.5f, +0.5f), /* 03 */
            new Vec3(+0.5f, -0.5f, -0.5f), /* 04 */
            new Vec3(+0.5f, -0.5f, +0.5f), /* 05 */
            new Vec3(+0.5f, +0.5f, -0.5f), /* 06 */
            new Vec3(+0.5f, +0.5f, +0.5f) /* 07 */
      };

      final Vec3[] normals = new Vec3[] {
            new Vec3(-1.0f, 0.0f, 0.0f), /* 00 */
            new Vec3(0.0f, +1.0f, 0.0f), /* 01 */
            new Vec3(+1.0f, 0.0f, 0.0f), /* 02 */
            new Vec3(0.0f, -1.0f, 0.0f), /* 03 */
            new Vec3(0.0f, 0.0f, -1.0f), /* 04 */
            new Vec3(0.0f, 0.0f, +1.0f) /* 05 */
      };

      final Vec2[] texCoords = new Vec2[] {
            new Vec2(0.0f, 0.0f), /* 00 */
            new Vec2(1.0f, 0.0f), /* 01 */
            new Vec2(1.0f, 1.0f), /* 02 */
            new Vec2(0.0f, 1.0f) /* 03 */
      };

      final int[][][] faces = new int[][][] {
            {
                  {
                        1, 1, 0
                  }, {
                        2, 3, 0
                  }, {
                        0, 2, 0
                  }
            }, /* 00 */
            {
                  {
                        3, 1, 1
                  }, {
                        6, 3, 1
                  }, {
                        2, 2, 1
                  }
            }, /* 01 */
            {
                  {
                        7, 1, 2
                  }, {
                        4, 3, 2
                  }, {
                        6, 2, 2
                  }
            }, /* 02 */
            {
                  {
                        5, 1, 3
                  }, {
                        0, 3, 3
                  }, {
                        4, 2, 3
                  }
            }, /* 03 */
            {
                  {
                        6, 1, 4
                  }, {
                        0, 3, 4
                  }, {
                        2, 2, 4
                  }
            }, /* 04 */
            {
                  {
                        3, 1, 5
                  }, {
                        5, 3, 5
                  }, {
                        7, 2, 5
                  }
            }, /* 05 */

            {
                  {
                        1, 1, 0
                  }, {
                        3, 0, 0
                  }, {
                        2, 3, 0
                  }
            }, /* 06 */
            {
                  {
                        3, 1, 1
                  }, {
                        7, 0, 1
                  }, {
                        6, 3, 1
                  }
            }, /* 07 */
            {
                  {
                        7, 1, 2
                  }, {
                        5, 0, 2
                  }, {
                        4, 3, 2
                  }
            }, /* 08 */
            {
                  {
                        5, 1, 3
                  }, {
                        1, 0, 3
                  }, {
                        0, 3, 3
                  }
            }, /* 09 */
            {
                  {
                        6, 1, 4
                  }, {
                        4, 0, 4
                  }, {
                        0, 3, 4
                  }
            }, /* 10 */
            {
                  {
                        3, 1, 5
                  }, {
                        1, 0, 5
                  }, {
                        5, 3, 5
                  }
            } /* 11 */
      };

      target.name = "Cube";
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
               final Vec3 read = new Vec3(tokens[1], tokens[2], tokens[3]);
               coordList.add(read);

            } else if (initialToken.equals("vt")) {

               /* Texture coordinate. */
               final Vec2 read = new Vec2(tokens[1], tokens[2]);
               texCoordList.add(read);

            } else if (initialToken.equals("vn")) {

               /* Normal. */
               final Vec3 read = new Vec3(tokens[1], tokens[2], tokens[3]);
               normalList.add(read);

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
    * Generates a regular convex polygon.
    *
    * @param target
    *           the output mesh
    * @param sectors
    *           the number of sides
    * @return the polygon
    */
   public static Mesh3 polygon (
         final Mesh3 target,
         final int sectors ) {

      target.name = "Polygon";

      final int seg = sectors < 3 ? 3 : sectors;
      final float toTheta = IUtils.TAU / seg;

      final Vec2 uvCenter = Vec2.uvCenter(new Vec2());
      final Vec2 pureCoord = new Vec2();

      final Vec3[] coords = new Vec3[seg + 1];
      final Vec2[] texCoords = new Vec2[seg + 1];
      final Vec3[] normals = new Vec3[] {
            Vec3.up(new Vec3())
      };
      final int[][][] faces = new int[seg][3][3];

      coords[0] = new Vec3();
      texCoords[0] = uvCenter;

      for (int i = 0, j = 1; i < seg; ++i, ++j) {
         final float theta = i * toTheta;

         Vec2.fromPolar(theta, 0.5f, pureCoord);
         texCoords[j] = Vec2.add(pureCoord, uvCenter, new Vec2());

         coords[j] = new Vec3(pureCoord.x, pureCoord.y, 0.0f);

         final int k = 1 + j % seg;
         final int[][] face = new int[][] {
               {
                     0, 0, 0
               }, {
                     j, j, 0
               },
               {
                     k, k, 0
               }
         };
         faces[i] = face;
      }

      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * Creates a rectangle.
    *
    * @param target
    *           the output mesh
    * @return the rectangle
    */
   public static final Mesh3 rectangle ( final Mesh3 target ) {

      target.name = "Rectangle";

      final Vec3[] coords = new Vec3[] {
            new Vec3(+0.5f, +0.5f, 0.0f),
            new Vec3(-0.5f, +0.5f, 0.0f),
            new Vec3(-0.5f, -0.5f, 0.0f),
            new Vec3(+0.5f, -0.5f, 0.0f)
      };

      final Vec2[] texCoords = new Vec2[] {
            new Vec2(1.0f, 1.0f),
            new Vec2(0.0f, 1.0f),
            new Vec2(0.0f, 0.0f),
            new Vec2(1.0f, 0.0f)
      };

      final int[][][] faces = new int[][][] {
            {
                  {
                        0, 0, 0
                  }, {
                        1, 1, 0
                  }, {
                        2, 2, 0
                  }
            },
            {
                  {
                        2, 2, 0
                  }, {
                        3, 3, 0
                  }, {
                        0, 0, 0
                  }
            }
      };

      final Vec3[] normals = new Vec3[] {
            Vec3.up(new Vec3())
      };

      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * Creates a triangle.
    *
    * @param target
    *           the output mesh
    * @return the triangle
    */
   public static final Mesh3 triangle ( final Mesh3 target ) {

      target.name = "Triangle";

      final Vec3[] coords = new Vec3[] {
            new Vec3(+0.0f, +0.5f, 0.0f),
            new Vec3(-0.4330127f, -0.25f, 0.0f),
            new Vec3(+0.4330127f, -0.25f, 0.0f)
      };

      final Vec2[] texCoords = new Vec2[] {
            new Vec2(0.5f, 1.0f),
            new Vec2(0.0669873f, 0.25f),
            new Vec2(0.9330127f, 0.25f)
      };

      final Vec3[] normals = new Vec3[] {
            Vec3.up(new Vec3())
      };

      final int[][][] faces = new int[][][] {
            {
                  {
                        0, 0, 0
                  }, {
                        1, 1, 0
                  }, {
                        2, 2, 0
                  }
            }
      };

      return target.set(faces, coords, texCoords, normals);
   }

   public static Mesh3 uvSphere (
         final float r,
         final int detailU,
         final int detailV,
         final Mesh3 target ) {

      // TODO: Needs testing...

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

      final float[] cosTheta = new float[detailU];
      final float[] sinTheta = new float[detailU];
      final float uToPrc = 1.0f / detailU;
      for (int i = 0; i < detailU; ++i) {
         final float phi = Utils.lerpUnclamped(
               -IUtils.PI,
               IUtils.PI, i * uToPrc);
         cosTheta[i] = (float) Math.cos(phi);
         sinTheta[i] = (float) Math.sin(phi);
      }

      final float[] cosPhi = new float[detailV];
      final float[] sinPhi = new float[detailV];
      final float vToPrc = 1.0f / (detailV - 1.0f);
      for (int i = 0; i < detailV; ++i) {
         final float phi = Utils.lerpUnclamped(
               -IUtils.HALF_PI,
               IUtils.HALF_PI, i * vToPrc);
         cosPhi[i] = (float) Math.cos(phi);
         sinPhi[i] = (float) Math.sin(phi);
      }

      return target;
   }

   /**
    * An array of coordinates in the mesh.
    */
   public Vec3[] coords;

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
   public Mesh3 ( final int[][][] faces, final Vec3[] coords,
         final Vec2[] texCoords, final Vec3[] normals ) {

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
    * Gets a face from the mesh.
    *
    * @param i
    *           the index
    * @param target
    *           the output face
    * @return the face
    */
   public Face3 getFace ( final int i, final Face3 target ) {

      final int len = this.faces[i].length;
      final Vert3[] vertices = new Vert3[len];
      for (int j = 0; j < len; ++j) {
         vertices[j] = this.getVertex(i, j, new Vert3());
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

            // verts[j] = this.getVertex(i, j, new Vert3());
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
    * @param i
    *           primary index
    * @param j
    *           secondary index
    * @param target
    *           the output vertex
    * @return the vertex
    */
   public Vert3 getVertex (
         final int i,
         final int j,
         final Vert3 target ) {

      final int[] vert = this.faces[i][j];
      return target.set(
            this.coords[vert[0]],
            this.texCoords[vert[1]],
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

            // trial = this.getVertex(i, j, new Vert3());
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
    * Rotates all coordinates in the mesh by an angle around an
    * arbitrary axis.
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

      final int len = this.coords.length;
      final float cosa = (float) Math.cos(radians);
      final float sina = (float) Math.sin(radians);
      for (int i = 0; i < len; ++i) {
         Vec3.rotate(this.coords[i], cosa, sina, axis, this.coords[i]);
         Vec3.rotate(this.normals[i], cosa, sina, axis, this.normals[i]);
      }
      return this;
   }

   /**
    * Rotates all coordinates in the mesh by an angle around
    * the x axis.
    *
    * @param radians
    *           the angle in radians
    * @return this mesh
    * @see Vec3#rotateX(Vec3, float, Vec3)
    */
   @Chainable
   public Mesh3 rotateX ( final float radians ) {

      final int len = this.coords.length;
      final float cosa = (float) Math.cos(radians);
      final float sina = (float) Math.sin(radians);
      for (int i = 0; i < len; ++i) {
         Vec3.rotateX(this.coords[i], cosa, sina, this.coords[i]);
         Vec3.rotateX(this.normals[i], cosa, sina, this.normals[i]);
      }
      return this;
   }

   /**
    * Rotates all coordinates in the mesh by an angle around
    * the y axis.
    *
    * @param radians
    *           the angle in radians
    * @return this mesh
    * @see Vec3#rotateY(Vec3, float, Vec3)
    */
   @Chainable
   public Mesh3 rotateY ( final float radians ) {

      final int len = this.coords.length;
      final float cosa = (float) Math.cos(radians);
      final float sina = (float) Math.sin(radians);
      for (int i = 0; i < len; ++i) {
         Vec3.rotateY(this.coords[i], cosa, sina, this.coords[i]);
         Vec3.rotateY(this.normals[i], cosa, sina, this.normals[i]);
      }
      return this;
   }

   /**
    * Rotates all coordinates in the mesh by an angle around
    * the z axis.
    *
    * @param radians
    *           the angle in radians
    * @return this mesh
    * @see Vec3#rotateZ(Vec3, float, Vec3)
    */
   @Chainable
   public Mesh3 rotateZ ( final float radians ) {

      final int len = this.coords.length;
      final float cosa = (float) Math.cos(radians);
      final float sina = (float) Math.sin(radians);
      for (int i = 0; i < len; ++i) {
         Vec3.rotateZ(this.coords[i], cosa, sina, this.coords[i]);
         Vec3.rotateZ(this.normals[i], cosa, sina, this.normals[i]);
      }
      return this;
   }

   /**
    * Scales all coordinates in the mesh by a scalar.
    *
    * @param scale
    *           the scalar
    * @return this mesh
    * @see Vec3#mult(Vec3, float, Vec3)
    */
   @Chainable
   public Mesh3 scale ( final float scale ) {

      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         Vec3.mult(this.coords[i], scale, this.coords[i]);
      }
      return this;
   }

   /**
    * Scales all coordinates in the mesh by a vector.
    *
    * @param scale
    *           the vector
    * @return this mesh
    */
   @Chainable
   public Mesh3 scale ( final Vec3 scale ) {

      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         Vec3.mult(this.coords[i], scale, this.coords[i]);
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
      result.append("# v: ")
            .append(coordsLen)
            .append(", vt: ")
            .append(texCoordsLen)
            .append(", vn: ")
            .append(normalsLen)
            .append(", f: ")
            .append(facesLen)
            .append('\n')
            .append('\n');

      result.append('o')
            .append(' ')
            .append(this.name)
            .append('\n')
            .append('\n');

      for (final Vec3 coord : this.coords) {
         result.append('v').append(' ')
               .append(coord.toObjString())
               .append('\n');
      }
      result.append('\n');

      for (final Vec2 texCoord : this.texCoords) {
         result.append("vt ")
               .append(texCoord.toObjString())
               .append('\n');
      }
      result.append('\n');

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
                  .append(vert[2] + 1)
                  .append(' ');
         }
         result.append('\n');
      }
      result.append('\n');
      return result.toString();
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

      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         Vec3.add(this.coords[i], v, this.coords[i]);
      }
      return this;
   }
}
