package camzup.core;

/**
 * A variant of the Mesh class intended for use with JOGL. Coordinates,
 * texture coordinates, normals and vertex colors are stored in
 * <code>float</code> arrays. Indices are expected to form triangles.
 * Intended for lower-level code work and performance-oriented display,
 * rather than procedural generation.
 */
@Experimental
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

      this.set(indices, coords, texCoords, normals, colors);
   }

   /**
    * Returns the length of this mesh's colors array in bytes.
    *
    * @return the length
    */
   public int byteLengthColors ( ) {

      return this.colors.length * Float.BYTES;
   }

   /**
    * Returns the length of this mesh's coordinates array in bytes.
    *
    * @return the length
    */
   public int byteLengthCoords ( ) {

      return this.coords.length * Float.BYTES;
   }

   /**
    * Returns the length of this mesh's indices array in bytes.
    *
    * @return the length
    */
   public int byteLengthIndices ( ) {

      return this.indices.length * Integer.BYTES;
   }

   /**
    * Returns the length of this mesh's normals array in bytes.
    *
    * @return the length
    */
   public int byteLengthNormals ( ) {

      return this.normals.length * Float.BYTES;
   }

   /**
    * Returns the length of this mesh's texture coordinates array in bytes.
    *
    * @return the length
    */
   public int byteLengthTexCoords ( ) {

      return this.texCoords.length * Float.BYTES;
   }

   /**
    * Recalculate vertex colors to visualize the mesh's coordinates. Will
    * reallocate colors array if it is not of the appropriate length.
    *
    * @return this mesh
    */
   public MeshDirect calcColorsFromCoords ( ) {

      float lbx = Float.MAX_VALUE;
      float lby = Float.MAX_VALUE;
      float lbz = Float.MAX_VALUE;

      float ubx = Float.MIN_VALUE;
      float uby = Float.MIN_VALUE;
      float ubz = Float.MIN_VALUE;

      final int coLen = this.coords.length;
      for ( int i = 0; i < coLen; i += MeshDirect.COORD_STRIDE ) {
         final float x = this.coords[i];
         final float y = this.coords[i + 1];
         final float z = this.coords[i + 2];

         if ( x < lbx ) { lbx = x; }
         if ( x > ubx ) { ubx = x; }
         if ( y < lby ) { lby = y; }
         if ( y > uby ) { uby = y; }
         if ( z < lbz ) { lbz = z; }
         if ( z > ubz ) { ubz = z; }
      }

      final float w = ubx - lbx;
      final float h = uby - lby;
      final float d = ubz - lbz;

      final float wInv = w != 0.0f ? 1.0f / w : 0.0f;
      final float hInv = h != 0.0f ? 1.0f / h : 0.0f;
      final float dInv = d != 0.0f ? 1.0f / d : 0.0f;

      final int clrLen = coLen * MeshDirect.COLOR_STRIDE
         / MeshDirect.COORD_STRIDE;
      if ( this.colors.length != clrLen ) { this.colors = new float[clrLen]; }

      for ( int i = 0, j = 0; i < coLen; i += MeshDirect.COORD_STRIDE, j
         += MeshDirect.COLOR_STRIDE ) {
         final float x = this.coords[i];
         final float y = this.coords[i + 1];
         final float z = this.coords[i + 2];

         this.colors[j] = ( x - lbx ) * wInv;
         this.colors[j + 1] = ( y - lby ) * hInv;
         this.colors[j + 2] = ( z - lbz ) * dInv;
         this.colors[j + 3] = 1.0f;
      }

      return this;
   }

   /**
    * Recalculate vertex colors to visualize the mesh's normals. Will
    * reallocate colors array if it is not of the appropriate length.
    *
    * @return this mesh
    */
   public MeshDirect calcColorsFromNormals ( ) {

      final int nrmLen = this.normals.length;
      final int clrLen = nrmLen * MeshDirect.COLOR_STRIDE
         / MeshDirect.NORMAL_STRIDE;
      if ( this.colors.length != clrLen ) { this.colors = new float[clrLen]; }
      for ( int i = 0, j = 0; i < nrmLen; i += MeshDirect.NORMAL_STRIDE, j
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
    * Returns the number of faces held by this mesh.
    *
    * @return the length
    *
    * @see MeshDirect#lengthIndices()
    */
   public int length ( ) {

      return this.lengthIndices();
   }

   /**
    * Returns the number of colors in the mesh, i.e., actual length divided by
    * {@link MeshDirect#COLOR_STRIDE}.
    *
    * @return the length
    */
   public int lengthColors ( ) {

      return this.colors.length / MeshDirect.COLOR_STRIDE;
   }

   /**
    * Returns the number of coordinates in the mesh, i.e., actual length
    * divided by {@link MeshDirect#COORD_STRIDE}.
    *
    * @return the length
    */
   public int lengthCoords ( ) {

      return this.coords.length / MeshDirect.COORD_STRIDE;
   }

   /**
    * Returns the number of faces in the mesh, i.e., actual length divided by
    * {@link MeshDirect#INDEX_STRIDE}.
    *
    * @return the length
    */
   public int lengthIndices ( ) {

      return this.indices.length / MeshDirect.INDEX_STRIDE;
   }

   /**
    * Returns the number of normals in the mesh, i.e., actual length divided
    * by {@link MeshDirect#NORMAL_STRIDE}.
    *
    * @return the length
    */
   public int lengthNormals ( ) {

      return this.normals.length / MeshDirect.NORMAL_STRIDE;
   }

   /**
    * Returns the number of texture coordinates in the mesh, i.e., actual
    * length divided by {@link MeshDirect#TEX_COORD_STRIDE}.
    *
    * @return the length
    */
   public int lengthTexCoords ( ) {

      return this.texCoords.length / MeshDirect.TEX_COORD_STRIDE;
   }

   /**
    * Centers the mesh about the origin, (0.0, 0.0, 0.0), and rescales it to
    * the range [-0.5, 0.5] .
    *
    * @return this mesh
    */
   public MeshDirect reframe ( ) {

      // TEST

      float lbx = Float.MAX_VALUE;
      float lby = Float.MAX_VALUE;
      float lbz = Float.MAX_VALUE;

      float ubx = Float.MIN_VALUE;
      float uby = Float.MIN_VALUE;
      float ubz = Float.MIN_VALUE;

      final int coLen = this.coords.length;
      for ( int i = 0; i < coLen; i += MeshDirect.COORD_STRIDE ) {
         final float x = this.coords[i];
         final float y = this.coords[i + 1];
         final float z = this.coords[i + 2];

         if ( x < lbx ) { lbx = x; }
         if ( x > ubx ) { ubx = x; }
         if ( y < lby ) { lby = y; }
         if ( y > uby ) { uby = y; }
         if ( z < lbz ) { lbz = z; }
         if ( z > ubz ) { ubz = z; }
      }

      final float cx = 0.5f * ( lbx + ubx );
      final float cy = 0.5f * ( lby + uby );
      final float cz = 0.5f * ( lbz + ubz );

      final float scl = Utils.max(ubx - lbx, uby - lby, ubz - lbz);
      final float sclInv = scl != 0.0f ? 1.0f / scl : 0.0f;

      for ( int i = 0; i < coLen; i += MeshDirect.COORD_STRIDE ) {
         this.coords[i] = ( this.coords[i] - cx ) * sclInv;
         this.coords[i + 1] = ( this.coords[i + 1] - cy ) * sclInv;
         this.coords[i + 2] = ( this.coords[i + 2] - cz ) * sclInv;
      }

      return this;
   }

   /**
    * Rotates a coordinate in the mesh by a quaternion.
    *
    * @param i the index
    * @param q the quaternion
    *
    * @return this mesh
    */
   public MeshDirect rotateCoord ( final int i, final Quaternion q ) {

      final int j = i * MeshDirect.COORD_STRIDE;

      final Vec3 imag = q.imag;
      final float qw = q.real;
      final float qx = imag.x;
      final float qy = imag.y;
      final float qz = imag.z;

      final float vx = this.coords[j];
      final float vy = this.coords[j + 1];
      final float vz = this.coords[j + 2];

      final float iw = -qx * vx - qy * vy - qz * vz;
      final float ix = qw * vx + qy * vz - qz * vy;
      final float iy = qw * vy + qz * vx - qx * vz;
      final float iz = qw * vz + qx * vy - qy * vx;

      this.coords[j] = ix * qw + iz * qy - iw * qx - iy * qz;
      this.coords[j + 1] = iy * qw + ix * qz - iw * qy - iz * qx;
      this.coords[j + 2] = iz * qw + iy * qx - iw * qz - ix * qy;

      return this;
   }

   /**
    * Rotates all coordinates in the mesh by a quaternion.
    *
    * @param q the quaternion
    *
    * @return this mesh
    */
   public MeshDirect rotateCoords ( final Quaternion q ) {

      final Vec3 imag = q.imag;
      final float qw = q.real;
      final float qx = imag.x;
      final float qy = imag.y;
      final float qz = imag.z;

      final int len = this.coords.length;
      for ( int j = 0; j < len; j += MeshDirect.COORD_STRIDE ) {
         final float vx = this.coords[j];
         final float vy = this.coords[j + 1];
         final float vz = this.coords[j + 2];

         final float iw = -qx * vx - qy * vy - qz * vz;
         final float ix = qw * vx + qy * vz - qz * vy;
         final float iy = qw * vy + qz * vx - qx * vz;
         final float iz = qw * vz + qx * vy - qy * vx;

         this.coords[j] = ix * qw + iz * qy - iw * qx - iy * qz;
         this.coords[j + 1] = iy * qw + ix * qz - iw * qy - iz * qx;
         this.coords[j + 2] = iz * qw + iy * qx - iw * qz - ix * qy;
      }

      return this;
   }

   /**
    * Scales a coordinate in the mesh by a uniform scalar.
    *
    * @param i the index
    * @param s the uniform scalar
    *
    * @return this mesh
    */
   public MeshDirect scaleCoord ( final int i, final float s ) {

      return this.scaleCoord(i, s, s, s);
   }

   /**
    * Scales a coordinate in the mesh by a non uniform scalar.
    *
    * @param i the index
    * @param x the scalar x
    * @param y the scalar y
    * @param z the scalar z
    *
    * @return this mesh
    */
   public MeshDirect scaleCoord ( final int i, final float x, final float y,
      final float z ) {

      if ( x != 0.0f && y != 0.0f && z != 0.0f ) {
         final int j = i * MeshDirect.COORD_STRIDE;
         this.coords[j] *= x;
         this.coords[j + 1] *= y;
         this.coords[j + 2] *= z;
      }

      return this;
   }

   /**
    * Scales a coordinate in the mesh by a non uniform scalar. The z component
    * of the scalar is assumed to be 1.0 .
    *
    * @param i the index
    * @param v the non uniform scalar
    *
    * @return this mesh
    */
   public MeshDirect scaleCoord ( final int i, final Vec2 v ) {

      return this.scaleCoord(i, v.x, v.y, 1.0f);
   }

   /**
    * Scales a coordinate in the mesh by a non uniform scalar.
    *
    * @param i the index
    * @param v the non uniform scalar
    *
    * @return this mesh
    */
   public MeshDirect scaleCoord ( final int i, final Vec3 v ) {

      return this.scaleCoord(i, v.x, v.y, v.z);
   }

   /**
    * Scales all coordinates in the mesh by a uniform scalar.
    *
    * @param s the uniform scalar
    *
    * @return this mesh
    */
   public MeshDirect scaleCoords ( final float s ) {

      return this.scaleCoords(s, s, s);
   }

   /**
    * Scales all coordinates in the mesh by a non uniform scalar.
    *
    * @param x the scalar x
    * @param y the scalar y
    * @param z the scalar z
    *
    * @return this mesh
    */
   public MeshDirect scaleCoords ( final float x, final float y,
      final float z ) {

      if ( x != 0.0f && y != 0.0f && z != 0.0f ) {
         final int len = this.coords.length;
         for ( int j = 0; j < len; j += MeshDirect.COORD_STRIDE ) {
            this.coords[j] *= x;
            this.coords[j + 1] *= y;
            this.coords[j + 2] *= z;
         }
      }

      return this;
   }

   /**
    * Scales all coordinates in the mesh by a non uniform scalar. The z
    * component of the scalar is assumed to be 1.0 .
    *
    * @param v the non uniform scalar
    *
    * @return this mesh
    */
   public MeshDirect scaleCoords ( final Vec2 v ) {

      return this.scaleCoords(v.x, v.y, 1.0f);
   }

   /**
    * Scales all coordinates in the mesh by a non uniform scalar.
    *
    * @param v the non uniform scalar
    *
    * @return this mesh
    */
   public MeshDirect scaleCoords ( final Vec3 v ) {

      return this.scaleCoords(v.x, v.y, v.z);
   }

   /**
    * Assigns arrays of data to a mesh.
    *
    * @param indices   the face indices
    * @param coords    the coordinates
    * @param texCoords the texture coordinates
    * @param normals   the normals
    * @param colors    the vertex colors
    *
    * @return this mesh
    */
   public MeshDirect set ( final int[] indices, final float[] coords,
      final float[] texCoords, final float[] normals, final float[] colors ) {

      this.indices = indices;
      this.coords = coords;
      this.texCoords = texCoords;
      this.normals = normals;
      this.colors = colors;

      return this;
   }

   /**
    * Sets a vertex color at an index.
    *
    * @param i the index
    * @param c the color
    *
    * @return this mesh
    */
   public MeshDirect setColor ( final int i, final Color c ) {

      return this.setColor(i, c.r, c.g, c.b, c.a);
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
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

   /**
    * Returns a string representation of this mesh.
    *
    * @param places decimal point precision
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(2048);

      sb.append("{ indices: ");
      Utils.toString(sb, this.indices, 0);

      sb.append(", coords: ");
      Utils.toString(sb, this.coords, places);

      sb.append(", texCoords: ");
      Utils.toString(sb, this.texCoords, places);

      sb.append(", normals: ");
      Utils.toString(sb, this.normals, places);

      sb.append(", colors: ");
      Utils.toString(sb, this.colors, places);

      sb.append(" }");
      return sb.toString();
   }

   /**
    * Transforms a mesh coordinate at an index by a 4x4 matrix.
    *
    * @param i   the index
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m02 row 0, column 2
    * @param m03 row 0, column 3
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    * @param m12 row 1, column 2
    * @param m13 row 1, column 3
    * @param m20 row 2, column 0
    * @param m21 row 2, column 1
    * @param m22 row 2, column 2
    * @param m23 row 2, column 3
    * @param m30 row 3, column 0
    * @param m31 row 3, column 1
    * @param m32 row 3, column 2
    * @param m33 row 3, column 3
    *
    * @return this mesh
    */
   public MeshDirect transformCoord ( final int i, final float m00,
      final float m01, final float m02, final float m03, final float m10,
      final float m11, final float m12, final float m13, final float m20,
      final float m21, final float m22, final float m23, final float m30,
      final float m31, final float m32, final float m33 ) {

      final int j = i * MeshDirect.COORD_STRIDE;
      final float x = this.coords[j];
      final float y = this.coords[j + 1];
      final float z = this.coords[j + 2];

      final float w = m30 * x + m31 * y + m32 * z + m33;
      final float wInv = w != 0.0f ? 1.0f / w : 0.0f;
      this.coords[j] = ( m00 * x + m01 * y + m02 * z + m03 ) * wInv;
      this.coords[j + 1] = ( m10 * x + m11 * y + m12 * z + m13 ) * wInv;
      this.coords[j + 2] = ( m20 * x + m21 * y + m22 * z + m23 ) * wInv;

      return this;
   }

   /**
    * Transforms a mesh coordinate at an index by a 3x3 matrix. Assumes the
    * matrix holds a 2D affine transform.
    *
    * @param i the index
    * @param m the matrix
    *
    * @return this mesh
    */
   public MeshDirect transformCoord ( final int i, final Mat3 m ) {

      return this.transformCoord(i, m.m00, m.m01, 0.0f, m.m02, m.m10, m.m11,
         0.0f, m.m12, 0.0f, 0.0f, 1.0f, 0.0f, m.m20, m.m21, 0.0f, m.m22);
   }

   /**
    * Transforms a mesh coordinate at an index by a 4x4 matrix.
    *
    * @param i the index
    * @param m the matrix
    *
    * @return this mesh
    */
   public MeshDirect transformCoord ( final int i, final Mat4 m ) {

      return this.transformCoord(i, m.m00, m.m01, m.m02, m.m03, m.m10, m.m11,
         m.m12, m.m13, m.m20, m.m21, m.m22, m.m23, m.m30, m.m31, m.m32, m.m33);
   }

   /**
    * Transforms the coordinates in a mesh by a 4x4 matrix.
    *
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m02 row 0, column 2
    * @param m03 row 0, column 3
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    * @param m12 row 1, column 2
    * @param m13 row 1, column 3
    * @param m20 row 2, column 0
    * @param m21 row 2, column 1
    * @param m22 row 2, column 2
    * @param m23 row 2, column 3
    * @param m30 row 3, column 0
    * @param m31 row 3, column 1
    * @param m32 row 3, column 2
    * @param m33 row 3, column 3
    *
    * @return this mesh
    */
   public MeshDirect transformCoords ( final float m00, final float m01,
      final float m02, final float m03, final float m10, final float m11,
      final float m12, final float m13, final float m20, final float m21,
      final float m22, final float m23, final float m30, final float m31,
      final float m32, final float m33 ) {

      final int len = this.coords.length;
      for ( int j = 0; j < len; j += MeshDirect.COORD_STRIDE ) {
         final float x = this.coords[j];
         final float y = this.coords[j + 1];
         final float z = this.coords[j + 2];

         final float w = m30 * x + m31 * y + m32 * z + m33;
         final float wInv = w != 0.0f ? 1.0f / w : 0.0f;
         this.coords[j] = ( m00 * x + m01 * y + m02 * z + m03 ) * wInv;
         this.coords[j + 1] = ( m10 * x + m11 * y + m12 * z + m13 ) * wInv;
         this.coords[j + 2] = ( m20 * x + m21 * y + m22 * z + m23 ) * wInv;
      }

      return this;
   }

   /**
    * Transforms the coordinates in a mesh by a 3x3 matrix.
    *
    * @param m the matrix
    *
    * @return the mesh
    */
   public MeshDirect transformCoords ( final Mat3 m ) {

      return this.transformCoords(m.m00, m.m01, 0.0f, m.m02, m.m10, m.m11, 0.0f,
         m.m12, 0.0f, 0.0f, 1.0f, 0.0f, m.m20, m.m21, 0.0f, m.m22);
   }

   /**
    * Transforms the coordinates in a mesh by a 4x4 matrix.
    *
    * @param m the matrix
    *
    * @return the mesh
    */
   public MeshDirect transformCoords ( final Mat4 m ) {

      return this.transformCoords(m.m00, m.m01, m.m02, m.m03, m.m10, m.m11,
         m.m12, m.m13, m.m20, m.m21, m.m22, m.m23, m.m30, m.m31, m.m32, m.m33);
   }

   /**
    * Translates a coordinate in the mesh by a vector.
    *
    * @param i the index
    * @param x the vector x
    * @param y the vector y
    * @param z the vector z
    *
    * @return this mesh
    */
   public MeshDirect translateCoord ( final int i, final float x, final float y,
      final float z ) {

      final int j = i * MeshDirect.COORD_STRIDE;
      this.coords[j] += x;
      this.coords[j + 1] += y;
      this.coords[j + 2] += z;

      return this;
   }

   /**
    * Translates a coordinate in the mesh by a vector.
    *
    * @param i the index
    * @param v the vector
    *
    * @return this mesh
    */
   public MeshDirect translateCoord ( final int i, final Vec2 v ) {

      return this.translateCoord(i, v.x, v.y, 0.0f);
   }

   /**
    * Translates a coordinate in the mesh by a vector.
    *
    * @param i the index
    * @param v the vector
    *
    * @return this mesh
    */
   public MeshDirect translateCoord ( final int i, final Vec3 v ) {

      return this.translateCoord(i, v.x, v.y, v.z);
   }

   /**
    * Translates all coordinates in the mesh by a vector.
    *
    * @param x the vector x
    * @param y the vector y
    * @param z the vector z
    *
    * @return this mesh
    */
   public MeshDirect translateCoords ( final float x, final float y,
      final float z ) {

      final int len = this.coords.length;
      for ( int j = 0; j < len; j += MeshDirect.COORD_STRIDE ) {
         this.coords[j] += x;
         this.coords[j + 1] += y;
         this.coords[j + 2] += z;
      }

      return this;
   }

   /**
    * Translates all coordinates in the mesh by a vector.
    *
    * @param v the vector
    *
    * @return this mesh
    */
   public MeshDirect translateCoords ( final Vec2 v ) {

      return this.translateCoords(v.x, v.y, 0.0f);
   }

   /**
    * Translates all coordinates in the mesh by a vector.
    *
    * @param v the vector
    *
    * @return this mesh
    */
   public MeshDirect translateCoords ( final Vec3 v ) {

      return this.translateCoords(v.x, v.y, v.z);
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
   public static final int COORD_STRIDE = 3;

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

      for ( int k = 0; k < fsLen3; ++k ) {
         final int[][] f = fs[k / MeshDirect.INDEX_STRIDE];
         final int[] vert = f[k % MeshDirect.INDEX_STRIDE];

         target.indices[k] = k;

         final Vec2 v = vsSrc[vert[0]];
         target.coords[coIdx] = v.x;
         target.coords[coIdx + 1] = v.y;
         // target.coords[coIdx + 2] = 0.0f;

         final Vec2 vt = vtsSrc[vert[1]];
         target.texCoords[uvIdx] = vt.x;
         target.texCoords[uvIdx + 1] = vt.y;

         // target.normals[nmIdx] = 0.0f;
         // target.normals[nmIdx + 1] = 0.0f;
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

      for ( int k = 0; k < fsLen3; ++k ) {
         final int[][] f = fs[k / MeshDirect.INDEX_STRIDE];
         final int[] vert = f[k % MeshDirect.INDEX_STRIDE];
         target.indices[k] = k;

         final Vec3 v = vsSrc[vert[0]];
         target.coords[coIdx] = v.x;
         target.coords[coIdx + 1] = v.y;
         target.coords[coIdx + 2] = v.z;

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

      return target;
   }

}
