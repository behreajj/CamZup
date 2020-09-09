package camzup.core;

/**
 * A lower level version of the Mesh class intended for direct use with
 * JOGL. Coordinates, texture coordinates, normals and vertex colors are
 * stored in <code>float</code> arrays. Indices are expected to form
 * triangles.
 */
public class Mesh3Direct {

   /**
    * Vertex colors array; uses a stride of {@value Mesh3Direct#COLOR_STRIDE}.
    */
   public float[] colors;

   /**
    * Coordinates array; uses a stride of {@value Mesh3Direct#COORD_STRIDE}.
    */
   public float[] coords;

   /**
    * Indices array; uses a stride of {@value Mesh3Direct#INDEX_STRIDE}.
    */
   public int[] indices;

   /**
    * Normals array; uses a stride of {@value Mesh3Direct#NORMAL_STRIDE}.
    */
   public float[] normals;

   /**
    * Texture coordinates array; uses a stride of
    * {@value Mesh3Direct#TEX_COORD_STRIDE}.
    */
   public float[] texCoords;

   /**
    * The default constructor.
    */
   public Mesh3Direct ( ) {

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
   public Mesh3Direct ( final int[] indices, final float[] coords,
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
   public Mesh3Direct calcColorsFromNormals ( ) {

      final int len = this.normals.length;
      this.colors = new float[len * Mesh3Direct.COLOR_STRIDE
         / Mesh3Direct.NORMAL_STRIDE];
      for ( int i = 0, j = 0; i < len; i += Mesh3Direct.NORMAL_STRIDE, j
         += Mesh3Direct.COLOR_STRIDE ) {
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

      final int j = i * Mesh3Direct.COLOR_STRIDE;
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

      final int j = i * Mesh3Direct.COORD_STRIDE;
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

      final int j = i * Mesh3Direct.COORD_STRIDE;
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

      final int j = i * Mesh3Direct.INDEX_STRIDE;
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

      final int j = i * Mesh3Direct.NORMAL_STRIDE;
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

      final int j = i * Mesh3Direct.TEX_COORD_STRIDE;
      return target.set(this.coords[j], this.coords[j + 1]);
   }

   public Mesh3Direct insertCoord ( final int i, final float x, final float y,
      final float z, final float w ) {

      // TODO: Change to splice API?

      final int j = i * Mesh3Direct.COORD_STRIDE;
      final int len = this.coords.length;
      final float[] result = new float[len + Mesh3Direct.COORD_STRIDE];

      System.arraycopy(this.coords, 0, result, 0, j);
      System.arraycopy(this.coords, j, result, j + Mesh3Direct.COORD_STRIDE, len
         - j);

      result[j] = x;
      result[j + 1] = y;
      result[j + 2] = z;
      result[j + 3] = w;

      this.coords = result;

      return this;
   }

   public Mesh3Direct insertCoord ( final int i, final Vec3 v ) {

      return this.insertCoord(i, v.x, v.y, v.z, 1.0f);
   }

   public Mesh3Direct rotateCoords ( final Quaternion q ) {

      final Vec3 imag = q.imag;
      final float qx = imag.x;
      final float qy = imag.y;
      final float qz = imag.z;
      final float qw = q.real;

      final int len = this.coords.length;
      for ( int i = 0; i < len; i += Mesh3Direct.COORD_STRIDE ) {
         final float vx = this.coords[i];
         final float vy = this.coords[i + 1];
         final float vz = this.coords[i + 2];

         final float iw = -qx * vx - qy * vy - qz * vz;
         final float ix = qw * vx + qy * vz - qz * vy;
         final float iy = qw * vy + qz * vx - qx * vz;
         final float iz = qw * vz + qx * vy - qy * vx;

         this.coords[i] = ix * qw + iz * qy - iw * qx - iy * qz;
         this.coords[i + 1] = iy * qw + ix * qz - iw * qy - iz * qx;
         this.coords[i + 2] = iz * qw + iy * qx - iw * qz - ix * qy;
      }
      return this;
   }

   public Mesh3Direct rotateXCoords ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      return this.rotateXCoords(cosa, sina);
   }

   public Mesh3Direct rotateXCoords ( final float cosa, final float sina ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; i += Mesh3Direct.COORD_STRIDE ) {
         final float vy = this.coords[i + 1];
         final float vz = this.coords[i + 2];
         this.coords[i + 1] = cosa * vy - sina * vz;
         this.coords[i + 2] = cosa * vz + sina * vy;
      }

      return this;
   }

   public Mesh3Direct rotateYCoords ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      return this.rotateYCoords(cosa, sina);
   }

   public Mesh3Direct rotateYCoords ( final float cosa, final float sina ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; i += Mesh3Direct.COORD_STRIDE ) {
         final float vx = this.coords[i];
         final float vz = this.coords[i + 2];
         this.coords[i] = cosa * vx + sina * vz;
         this.coords[i + 2] = cosa * vz - sina * vx;
      }

      return this;
   }

   public Mesh3Direct rotateZCoords ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      return this.rotateZCoords(cosa, sina);
   }

   public Mesh3Direct rotateZCoords ( final float cosa, final float sina ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; i += Mesh3Direct.COORD_STRIDE ) {
         final float vx = this.coords[i];
         final float vy = this.coords[i + 1];
         this.coords[i] = cosa * vx - sina * vy;
         this.coords[i + 1] = cosa * vy + sina * vx;
      }

      return this;
   }

   public Mesh3Direct rotateZTexCoords ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      return this.rotateZTexCoords(cosa, sina);
   }

   public Mesh3Direct rotateZTexCoords ( final float cosa, final float sina ) {

      /* (0.5, 0.5) as pivot. */
      final int len = this.texCoords.length;
      for ( int i = 0; i < len; i += Mesh3Direct.TEX_COORD_STRIDE ) {
         final float vx = this.texCoords[i] - 0.5f;
         final float vy = this.texCoords[i + 1] - 0.5f;
         this.texCoords[i] = cosa * vx - sina * vy + 0.5f;
         this.texCoords[i + 1] = cosa * vy + sina * vx + 0.5f;
      }

      return this;
   }

   public Mesh3Direct scaleCoords ( final float s ) {

      return this.scaleCoords(s, s, s);
   }

   public Mesh3Direct scaleCoords ( final float x, final float y,
      final float z ) {

      if ( x != 0.0f && y != 0.0f && z != 0.0f ) {
         final int len = this.coords.length;
         for ( int i = 0; i < len; i += Mesh3Direct.COORD_STRIDE ) {
            this.coords[i] *= x;
            this.coords[i + 1] *= y;
            this.coords[i + 2] *= z;
         }
      }
      return this;
   }

   public Mesh3Direct scaleCoords ( final Vec3 v ) {

      return this.scaleCoords(v.x, v.y, v.z);
   }

   public Mesh3Direct scaleTexCoords ( final float s ) {

      return this.scaleTexCoords(s, s);
   }

   public Mesh3Direct scaleTexCoords ( final float x, final float y ) {

      if ( x != 0.0f && y != 0.0f ) {
         final int len = this.coords.length;
         final float xInv = 1.0f / x;
         final float yInv = 1.0f / y;
         for ( int i = 0; i < len; i += Mesh3Direct.TEX_COORD_STRIDE ) {
            this.texCoords[i] -= 0.5f;
            this.texCoords[i + 1] -= 0.5f;

            this.texCoords[i] *= xInv;
            this.texCoords[i + 1] *= yInv;

            this.texCoords[i] += 0.5f;
            this.texCoords[i + 1] += 0.5f;
         }
      }
      return this;
   }

   public Mesh3Direct scaleTexCoords ( final Vec2 v ) {

      return this.scaleTexCoords(v.x, v.y);
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
   public Mesh3Direct setColor ( final int i, final float r, final float g,
      final float b, final float a ) {

      final int j = i * Mesh3Direct.COLOR_STRIDE;
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
   public Mesh3Direct setCoord ( final int i, final float x, final float y,
      final float z, final float w ) {

      final int j = i * Mesh3Direct.COORD_STRIDE;
      this.coords[j] = x;
      this.coords[j + 1] = y;
      this.coords[j + 2] = z;
      this.coords[j + 3] = w;

      return this;
   }

   public Mesh3Direct setCoord ( final int i, final Vec2 v ) {

      return this.setCoord(i, v.x, v.y, 0.0f, 1.0f);
   }

   public Mesh3Direct setCoord ( final int i, final Vec3 v ) {

      return this.setCoord(i, v.x, v.y, v.z, 1.0f);
   }

   public Mesh3Direct setCoord ( final int i, final Vec4 v ) {

      return this.setCoord(i, v.x, v.y, v.z, v.w);
   }

   public Mesh3Direct setIndex ( final int i, final int a, final int b,
      final int c ) {

      final int j = i * Mesh3Direct.INDEX_STRIDE;
      this.indices[j] = a;
      this.indices[j + 1] = b;
      this.indices[j + 2] = c;
      return this;
   }

   public Mesh3Direct setNormal ( final int i, final float x, final float y,
      final float z ) {

      final int j = i * Mesh3Direct.NORMAL_STRIDE;
      this.normals[j] = x;
      this.normals[j + 1] = y;
      this.normals[j + 2] = z;

      return this;
   }

   public Mesh3Direct setNormal ( final int i, final Vec3 v ) {

      return this.setNormal(i, v.x, v.y, v.z);
   }

   public Mesh3Direct setTexCoord ( final int i, final float x,
      final float y ) {

      final int j = i * Mesh3Direct.TEX_COORD_STRIDE;
      this.texCoords[j] = x;
      this.texCoords[j + 1] = y;

      return this;
   }

   public Mesh3Direct setTexCoord ( final int i, final Vec2 v ) {

      return this.setTexCoord(i, v.x, v.y);
   }

   @Override
   public String toString ( ) { return this.toString(4); }

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

   public Mesh3Direct transformCoords ( final float m00, final float m01,
      final float m02, final float m03, final float m10, final float m11,
      final float m12, final float m13, final float m20, final float m21,
      final float m22, final float m23, final float m30, final float m31,
      final float m32, final float m33 ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; i += Mesh3Direct.COORD_STRIDE ) {

         final float x = this.coords[i];
         final float y = this.coords[i + 1];
         final float z = this.coords[i + 2];
         final float w = m30 * x + m31 * y + m32 * z + m33;

         if ( w != 0.0f ) {
            final float wInv = 1.0f / w;
            this.coords[i] = ( m00 * x + m01 * y + m02 * z + m03 ) * wInv;
            this.coords[i + 1] = ( m10 * x + m11 * y + m12 * z + m13 ) * wInv;
            this.coords[i + 2] = ( m20 * x + m21 * y + m22 * z + m23 ) * wInv;
            this.coords[i + 3] = 1.0f;
         } else {
            this.coords[i] = 0.0f;
            this.coords[i + 1] = 0.0f;
            this.coords[i + 2] = 0.0f;
            this.coords[i + 3] = 1.0f;
         }
      }

      return this;
   }

   public Mesh3Direct transformCoords ( final Mat4 m ) {

      return this.transformCoords(m.m00, m.m01, m.m02, m.m03, m.m10, m.m11,
         m.m12, m.m13, m.m20, m.m21, m.m22, m.m23, m.m30, m.m31, m.m32, m.m33);
   }

   public Mesh3Direct transformCoords ( final Transform3 tr ) {

      this.rotateCoords(tr.getRotation(new Quaternion()));
      this.scaleCoords(tr.getScale(new Vec3()));
      this.translateCoords(tr.getLocation(new Vec3()));

      return this;
   }

   public Mesh3Direct transformTexCoords ( final float m00, final float m01,
      final float m02, final float m10, final float m11, final float m12,
      final float m20, final float m21, final float m22 ) {

      final int len = this.texCoords.length;
      for ( int i = 0; i < len; i += Mesh3Direct.TEX_COORD_STRIDE ) {
         final float x = this.coords[i];
         final float y = this.coords[i + 1];
         final float w = m20 * x + m21 * y + m22;

         if ( w != 0.0f ) {
            final float wInv = 1.0f / w;
            this.coords[i] = ( m00 * x + m01 * y + m02 ) * wInv;
            this.coords[i + 1] = ( m10 * x + m11 * y + m12 ) * wInv;
         } else {
            this.coords[i] = 0.0f;
            this.coords[i + 1] = 0.0f;
         }
      }
      return this;
   }

   public Mesh3Direct transformTexCoords ( final Mat3 m ) {

      return this.transformTexCoords(m.m00, m.m01, m.m02, m.m10, m.m11, m.m12,
         m.m20, m.m21, m.m22);
   }

   public Mesh3Direct transformTexCoords ( final Transform2 tr ) {

      this.rotateZTexCoords(tr.getRotation());
      this.scaleTexCoords(tr.getScale(new Vec2()));
      this.translateTexCoords(tr.getLocation(new Vec2()));

      return this;
   }

   public Mesh3Direct translateCoords ( final float x, final float y,
      final float z ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; i += Mesh3Direct.COORD_STRIDE ) {
         this.coords[i] += x;
         this.coords[i + 1] += y;
         this.coords[i + 2] += z;
      }

      return this;
   }

   public Mesh3Direct translateCoords ( final Vec2 v ) {

      return this.translateCoords(v.x, v.y, 0.0f);
   }

   public Mesh3Direct translateCoords ( final Vec3 v ) {

      return this.translateCoords(v.x, v.y, v.z);
   }

   public Mesh3Direct translateTexCoords ( final float x, final float y ) {

      final int len = this.texCoords.length;
      for ( int i = 0; i < len; i += Mesh3Direct.TEX_COORD_STRIDE ) {
         this.texCoords[i] += x;
         this.texCoords[i + 1] += y;
      }

      return this;
   }

   public Mesh3Direct translateTexCoords ( final Vec2 v ) {

      return this.translateTexCoords(v.x, v.y);
   }

   public static final int COLOR_STRIDE = 4;

   public static final int COORD_STRIDE = 4;

   public static final int INDEX_STRIDE = 3;

   public static final int NORMAL_STRIDE = 3;

   public static final int TEX_COORD_STRIDE = 2;

   public static Mesh3Direct cube ( final float size,
      final Mesh3Direct target ) {

      final float vsz = Utils.max(IUtils.EPSILON, size);

      target.indices = new int[] { 0, 2, 3, 0, 3, 1, 8, 4, 5, 8, 5, 9, 10, 6, 7,
         10, 7, 11, 12, 13, 14, 12, 14, 15, 16, 17, 18, 16, 18, 19, 20, 21, 22,
         20, 22, 23 };

      target.coords = new float[] { vsz, -vsz, vsz, 1.0f, -vsz, -vsz, vsz, 1.0f,
         vsz, vsz, vsz, 1.0f, -vsz, vsz, vsz, 1.0f, vsz, vsz, -vsz, 1.0f, -vsz,
         vsz, -vsz, 1.0f, vsz, -vsz, -vsz, 1.0f, -vsz, -vsz, -vsz, 1.0f, vsz,
         vsz, vsz, 1.0f, -vsz, vsz, vsz, 1.0f, vsz, vsz, -vsz, 1.0f, -vsz, vsz,
         -vsz, 1.0f, vsz, -vsz, -vsz, 1.0f, vsz, -vsz, vsz, 1.0f, -vsz, -vsz,
         vsz, 1.0f, -vsz, -vsz, -vsz, 1.0f, -vsz, -vsz, vsz, 1.0f, -vsz, vsz,
         vsz, 1.0f, -vsz, vsz, -vsz, 1.0f, -vsz, -vsz, -vsz, 1.0f, vsz, -vsz,
         -vsz, 1.0f, vsz, vsz, -vsz, 1.0f, vsz, vsz, vsz, 1.0f, vsz, -vsz, vsz,
         1.0f };

      target.texCoords = new float[] { 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
         1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
         0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f,
         0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
         1.0f, 1.0f, 1.0f, 1.0f, 0.0f };

      target.normals = new float[] { 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
         0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
         0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
         0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f,
         0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f,
         0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
         1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f };

      target.colors = new float[] { 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
         1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
         1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
         1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
         1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
         1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
         1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
         1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
         1.0f, 1.0f, 1.0f, 1.0f, 1.0f };

      return target;
   }

   public static Mesh3Direct fromMesh3 ( final Mesh3 source,
      final Mesh3Direct target ) {

      final int[][][] fs = source.faces;
      final int fsLen = fs.length;

      final Vec3[] vsSrc = source.coords;
      final Vec2[] vtsSrc = source.texCoords;
      final Vec3[] vnsSrc = source.normals;

      final int fsLen3 = Mesh3Direct.INDEX_STRIDE * fsLen;
      target.indices = new int[fsLen3];
      target.coords = new float[fsLen3 * Mesh3Direct.COORD_STRIDE];
      target.texCoords = new float[fsLen3 * Mesh3Direct.TEX_COORD_STRIDE];
      target.normals = new float[fsLen3 * Mesh3Direct.NORMAL_STRIDE];
      target.colors = new float[fsLen3 * Mesh3Direct.COLOR_STRIDE];

      int coIdx = 0;
      int uvIdx = 0;
      int nmIdx = 0;
      int clIdx = 0;

      for ( int k = 0, i = 0; i < fsLen; ++i ) {
         final int[][] f = fs[i];
         // final int fLen = f.length;
         // if ( fLen != Mesh3Direct.INDEX_STRIDE ) throw new Exception();
         for ( int j = 0; j < Mesh3Direct.INDEX_STRIDE; ++j, ++k ) {
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

            coIdx += Mesh3Direct.COORD_STRIDE;
            uvIdx += Mesh3Direct.TEX_COORD_STRIDE;
            nmIdx += Mesh3Direct.NORMAL_STRIDE;
            clIdx += Mesh3Direct.COLOR_STRIDE;
         }
      }

      return target;
   }

}
