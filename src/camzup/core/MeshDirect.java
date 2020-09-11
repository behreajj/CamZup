package camzup.core;

/**
 * A lower level version of the Mesh class intended for use with JOGL.
 * Coordinates, texture coordinates, normals and vertex colors are stored
 * in <code>float</code> arrays. Indices are expected to form triangles.
 */
public class MeshDirect {

   /**
    * Vertex colors array; uses a stride of {@value MeshDirect#COLOR_STRIDE}.
    */
   public float[] colors;

   /**
    * Coordinates array; uses a stride of {@value MeshDirect#COORD_STRIDE}.
    */
   public float[] coords;

   /**
    * Indices array; uses a stride of {@value MeshDirect#INDEX_STRIDE}.
    */
   public int[] indices;

   /**
    * Normals array; uses a stride of {@value MeshDirect#NORMAL_STRIDE}.
    */
   public float[] normals;

   /**
    * Texture coordinates array; uses a stride of
    * {@value MeshDirect#TEX_COORD_STRIDE}.
    */
   public float[] texCoords;

   /**
    * The default constructor.
    */
   public MeshDirect ( ) {

      this(new int[0], new float[0], new float[0], new float[0], new float[0]);
   }

   /**
    * Creates a mesh from arrays of data.
    *
    * @param indices   the face indices
    * @param coords    the coordinates
    * @param texCoords the texture coordinates
    * @param normals   the normals
    * @param colors    the vertex colors
    */
   public MeshDirect ( final int[] indices, final float[] coords,
      final float[] texCoords, final float[] normals, final float[] colors ) {

      this.indices = indices;
      this.coords = coords;
      this.texCoords = texCoords;
      this.normals = normals;
      this.colors = colors;
   }

   /**
    * Recalculate vertex colors to visualize the mesh's normals.
    *
    * @return this mesh
    */
   public MeshDirect calcColorsFromNormals ( ) {

      final int len = this.normals.length;
      this.colors = new float[len * MeshDirect.COLOR_STRIDE
         / MeshDirect.NORMAL_STRIDE];
      for ( int i = 0, j = 0; i < len; i += MeshDirect.NORMAL_STRIDE, j
         += MeshDirect.COLOR_STRIDE ) {
         this.colors[j] = this.normals[i] * 0.5f + 0.5f;
         this.colors[j + 1] = this.normals[i + 1] * 0.5f + 0.5f;
         this.colors[j + 2] = this.normals[i + 2] * 0.5f + 0.5f;
         this.colors[j + 3] = 1.0f;
      }

      return this;
   }

   /**
    * Gets a color at an index.
    *
    * @param i      the index
    * @param target the output vector
    *
    * @return the coordinate
    */
   public Color getColor ( final int i, final Color target ) {

      final int j = i * MeshDirect.COLOR_STRIDE;
      return target.set(this.colors[j], this.colors[j + 1], this.colors[j + 2],
         this.colors[j + 3]);
   }

   /**
    * Gets a coordinate at an index.
    *
    * @param i      the index
    * @param target the output vector
    *
    * @return the coordinate
    */
   public Vec3 getCoord ( final int i, final Vec3 target ) {

      final int j = i * MeshDirect.COORD_STRIDE;
      return target.set(this.coords[j], this.coords[j + 1], this.coords[j + 2]);
   }

   /**
    * Gets a coordinate at an index.
    *
    * @param i      the index
    * @param target the output vector
    *
    * @return the coordinate
    */
   public Vec4 getCoord ( final int i, final Vec4 target ) {

      final int j = i * MeshDirect.COORD_STRIDE;
      return target.set(this.coords[j], this.coords[j + 1], this.coords[j + 2],
         this.coords[j + 3]);
   }

   /**
    * Gets the indices of a triangular face at an index.
    *
    * @param i the index
    *
    * @return the array of indices
    */
   public int[] getIndex ( final int i ) {

      final int j = i * MeshDirect.INDEX_STRIDE;
      return new int[] { this.indices[j], this.indices[j + 1], this.indices[j
         + 2] };
   }

   /**
    * Gets a normal at an index.
    *
    * @param i      the index
    * @param target the output vector
    *
    * @return the normal
    */
   public Vec3 getNormal ( final int i, final Vec3 target ) {

      final int j = i * MeshDirect.NORMAL_STRIDE;
      return target.set(this.normals[j], this.normals[j + 1], this.normals[j
         + 2]);
   }

   /**
    * Gets a texture coordinate at an index.
    *
    * @param i      the index
    * @param target the output vector
    *
    * @return the texture coordinate
    */
   public Vec2 getTexCoord ( final int i, final Vec2 target ) {

      final int j = i * MeshDirect.TEX_COORD_STRIDE;
      return target.set(this.coords[j], this.coords[j + 1]);
   }

   /**
    * Sets a vertex color at an index.
    *
    * @param i the index
    * @param r the red channel
    * @param g the green channel
    * @param b the blue channel
    * @param a the alpha channel
    *
    * @return this mesh
    */
   public MeshDirect setColor ( final int i, final float r, final float g,
      final float b, final float a ) {

      final int j = i * MeshDirect.COLOR_STRIDE;
      this.colors[j] = r;
      this.colors[j + 1] = g;
      this.colors[j + 2] = b;
      this.colors[j + 3] = a;

      return this;
   }

   /**
    * Sets a coordinate at an index.
    *
    * @param i the index
    * @param x the x value
    * @param y the y value
    * @param z the z value
    * @param w the w value
    *
    * @return this mesh
    */
   public MeshDirect setCoord ( final int i, final float x, final float y,
      final float z, final float w ) {

      final int j = i * MeshDirect.COORD_STRIDE;
      this.coords[j] = x;
      this.coords[j + 1] = y;
      this.coords[j + 2] = z;
      this.coords[j + 3] = w;

      return this;
   }

   /**
    * Sets a coordinate at an index.
    *
    * @param i the index
    * @param v the coordinate
    *
    * @return this mesh
    */
   public MeshDirect setCoord ( final int i, final Vec2 v ) {

      return this.setCoord(i, v.x, v.y, 0.0f, 1.0f);
   }

   /**
    * Sets a coordinate at an index.
    *
    * @param i the index
    * @param v the coordinate
    *
    * @return this mesh
    */
   public MeshDirect setCoord ( final int i, final Vec3 v ) {

      return this.setCoord(i, v.x, v.y, v.z, 1.0f);
   }

   /**
    * Sets a coordinate at an index.
    *
    * @param i the index
    * @param v the coordinate
    *
    * @return this mesh
    */
   public MeshDirect setCoord ( final int i, final Vec4 v ) {

      return this.setCoord(i, v.x, v.y, v.z, v.w);
   }

   /**
    * Sets the indices of a triangle.
    *
    * @param i the face index
    * @param a the first vertex index
    * @param b the second vertex index
    * @param c the third vertex index
    *
    * @return this mesh
    */
   public MeshDirect setIndex ( final int i, final int a, final int b,
      final int c ) {

      final int j = i * MeshDirect.INDEX_STRIDE;
      this.indices[j] = a;
      this.indices[j + 1] = b;
      this.indices[j + 2] = c;
      return this;
   }

   /**
    * Sets a normal at an index.
    *
    * @param i the index
    * @param x the normal x
    * @param y the normal y
    * @param z the normal z
    *
    * @return this mesh
    */
   public MeshDirect setNormal ( final int i, final float x, final float y,
      final float z ) {

      final int j = i * MeshDirect.NORMAL_STRIDE;
      this.normals[j] = x;
      this.normals[j + 1] = y;
      this.normals[j + 2] = z;

      return this;
   }

   /**
    * Sets a normal at an index.
    *
    * @param i the index
    * @param v the normal
    *
    * @return this mesh
    */
   public MeshDirect setNormal ( final int i, final Vec3 v ) {

      return this.setNormal(i, v.x, v.y, v.z);
   }

   /**
    * Sets a texture coordinate at an index.
    *
    * @param i the index
    * @param x the x, u or s coordinate
    * @param y the y, v or t coordinate
    *
    * @return this mesh
    */
   public MeshDirect setTexCoord ( final int i, final float x, final float y ) {

      final int j = i * MeshDirect.TEX_COORD_STRIDE;
      this.texCoords[j] = x;
      this.texCoords[j + 1] = y;

      return this;
   }

   /**
    * Sets a texture coordinate at an index.
    *
    * @param i the index
    * @param v the texture coordinate
    *
    * @return this mesh
    */
   public MeshDirect setTexCoord ( final int i, final Vec2 v ) {

      return this.setTexCoord(i, v.x, v.y);
   }

   /**
    * Returns a string representation of this mesh.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of this mesh.
    *
    * @param places decimal point precision
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(2048);

      sb.append("{ indices: [ ");
      final int idcsLen = this.indices.length;
      final int idcsLast = idcsLen - 1;
      for ( int i = 0; i < idcsLen; ++i ) {
         sb.append(this.indices[i]);
         if ( i < idcsLast ) { sb.append(',').append(' '); }
      }
      sb.append(" ]");

      sb.append(", coords: ");
      sb.append(Utils.toString(this.coords, places));

      sb.append(", texCoords: ");
      sb.append(Utils.toString(this.texCoords, places));

      sb.append(", normals: ");
      sb.append(Utils.toString(this.normals, places));

      sb.append(", colors: ");
      sb.append(Utils.toString(this.colors, places));
      sb.append(" }");
      return sb.toString();
   }

   /**
    * The number of components per color in an array,
    * {@value MeshDirect#COLOR_STRIDE}.
    */
   public static final int COLOR_STRIDE = 4;

   /**
    * The number of components per coordinate in an array,
    * {@value MeshDirect#COORD_STRIDE}. Should reflect the data structure in
    * the shader that will receive the data, e.g., <code>vec4</code>s should
    * use stride 4, <code>vec3</code>s should use stride 3.
    */
   public static final int COORD_STRIDE = 4;

   /**
    * The number of indices per face in an array,
    * {@value MeshDirect#INDEX_STRIDE}.
    */
   public static final int INDEX_STRIDE = 3;

   /**
    * The number of components per normal in an array,
    * {@value MeshDirect#NORMAL_STRIDE}.
    */
   public static final int NORMAL_STRIDE = 3;

   /**
    * The number of components per texture coordinate in an array,
    * {@value MeshDirect#TEX_COORD_STRIDE}. Should reflect the data structure
    * in the shader that will receive the data, e.g., <code>vec3</code>s
    * should use stride 3, <code>vec2</code>s should use stride 2.
    */
   public static final int TEX_COORD_STRIDE = 2;

   /**
    * Converts a {@link Mesh2} to a a direct mesh. Assigns white to vertex
    * colors.
    *
    * @param source the mesh3
    * @param target the output direct mesh
    *
    * @return the direct mesh
    */
   public static MeshDirect fromMesh2 ( final Mesh2 source,
      final MeshDirect target ) {

      final int[][][] fs = source.faces;
      final int fsLen = fs.length;

      final Vec2[] vsSrc = source.coords;
      final Vec2[] vtsSrc = source.texCoords;

      final int fsLen3 = MeshDirect.INDEX_STRIDE * fsLen;
      target.indices = new int[fsLen3];
      target.coords = new float[fsLen3 * MeshDirect.COORD_STRIDE];
      target.texCoords = new float[fsLen3 * MeshDirect.TEX_COORD_STRIDE];
      target.normals = new float[fsLen3 * MeshDirect.NORMAL_STRIDE];
      target.colors = new float[fsLen3 * MeshDirect.COLOR_STRIDE];

      int coIdx = 0;
      int uvIdx = 0;
      int nmIdx = 0;
      int clIdx = 0;

      for ( int k = 0, i = 0; i < fsLen; ++i ) {
         final int[][] f = fs[i];
         for ( int j = 0; j < MeshDirect.INDEX_STRIDE; ++j, ++k ) {
            final int[] vert = f[j];

            target.indices[k] = k;

            final Vec2 v = vsSrc[vert[0]];
            target.coords[coIdx] = v.x;
            target.coords[coIdx + 1] = v.y;
            target.coords[coIdx + 2] = 0.0f;
            target.coords[coIdx + 3] = 1.0f;

            final Vec2 vt = vtsSrc[vert[1]];
            target.texCoords[uvIdx] = vt.x;
            target.texCoords[uvIdx + 1] = vt.y;

            target.normals[nmIdx] = 0.0f;
            target.normals[nmIdx + 1] = 0.0f;
            target.normals[nmIdx + 2] = 1.0f;

            target.colors[clIdx] = 1.0f;
            target.colors[clIdx + 1] = 1.0f;
            target.colors[clIdx + 2] = 1.0f;
            target.colors[clIdx + 3] = 1.0f;

            coIdx += MeshDirect.COORD_STRIDE;
            uvIdx += MeshDirect.TEX_COORD_STRIDE;
            nmIdx += MeshDirect.NORMAL_STRIDE;
            clIdx += MeshDirect.COLOR_STRIDE;
         }
      }

      return target;
   }

   /**
    * Converts a {@link Mesh3} to a a direct mesh. Assigns white to vertex
    * colors.
    *
    * @param source the mesh3
    * @param target the output direct mesh
    *
    * @return the direct mesh
    */
   public static MeshDirect fromMesh3 ( final Mesh3 source,
      final MeshDirect target ) {

      final int[][][] fs = source.faces;
      final int fsLen = fs.length;

      final Vec3[] vsSrc = source.coords;
      final Vec2[] vtsSrc = source.texCoords;
      final Vec3[] vnsSrc = source.normals;

      final int fsLen3 = MeshDirect.INDEX_STRIDE * fsLen;
      target.indices = new int[fsLen3];
      target.coords = new float[fsLen3 * MeshDirect.COORD_STRIDE];
      target.texCoords = new float[fsLen3 * MeshDirect.TEX_COORD_STRIDE];
      target.normals = new float[fsLen3 * MeshDirect.NORMAL_STRIDE];
      target.colors = new float[fsLen3 * MeshDirect.COLOR_STRIDE];

      int coIdx = 0;
      int uvIdx = 0;
      int nmIdx = 0;
      int clIdx = 0;

      for ( int k = 0, i = 0; i < fsLen; ++i ) {
         final int[][] f = fs[i];
         for ( int j = 0; j < MeshDirect.INDEX_STRIDE; ++j, ++k ) {
            final int[] vert = f[j];

            target.indices[k] = k;

            final Vec3 v = vsSrc[vert[0]];
            target.coords[coIdx] = v.x;
            target.coords[coIdx + 1] = v.y;
            target.coords[coIdx + 2] = v.z;
            target.coords[coIdx + 3] = 1.0f;

            final Vec2 vt = vtsSrc[vert[1]];
            target.texCoords[uvIdx] = vt.x;
            target.texCoords[uvIdx + 1] = vt.y;

            final Vec3 vn = vnsSrc[vert[2]];
            target.normals[nmIdx] = vn.x;
            target.normals[nmIdx + 1] = vn.y;
            target.normals[nmIdx + 2] = vn.z;

            target.colors[clIdx] = 1.0f;
            target.colors[clIdx + 1] = 1.0f;
            target.colors[clIdx + 2] = 1.0f;
            target.colors[clIdx + 3] = 1.0f;

            coIdx += MeshDirect.COORD_STRIDE;
            uvIdx += MeshDirect.TEX_COORD_STRIDE;
            nmIdx += MeshDirect.NORMAL_STRIDE;
            clIdx += MeshDirect.COLOR_STRIDE;
         }
      }

      return target;
   }

}
