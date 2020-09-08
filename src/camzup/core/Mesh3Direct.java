package camzup.core;

public class Mesh3Direct {

   public float[] colors;
   // TODO: Update normals to be stride 4, tex coords to stride 3.
   public float[] coords;
   public int[] indices;
   public float[] normals;
   public float[] texCoords;

   public Mesh3Direct ( ) {

      this(new int[0], new float[0], new float[0], new float[0], new float[0]);
   }

   public Mesh3Direct ( final int[] indices, final float[] coords,
      final float[] texCoords, final float[] normals, final float[] colors ) {

      this.indices = indices;
      this.coords = coords;
      this.texCoords = texCoords;
      this.normals = normals;
      this.colors = colors;
   }

   public Color getColor ( final int i, final Color target ) {

      final int j = i * Mesh3Direct.COLOR_STRIDE;
      return target.set(this.colors[j], this.colors[j + 1], this.colors[j + 2],
         this.colors[j + 3]);
   }

   public Vec3 getCoord ( final int i, final Vec3 target ) {

      final int j = i * Mesh3Direct.COORD_STRIDE;
      return target.set(this.coords[j], this.coords[j + 1], this.coords[j + 2]);
   }

   public Vec4 getCoord ( final int i, final Vec4 target ) {

      final int j = i * Mesh3Direct.COORD_STRIDE;
      return target.set(this.coords[j], this.coords[j + 1], this.coords[j + 2],
         this.coords[j + 3]);
   }

   public Vec3 getNormal ( final int i, final Vec3 target ) {

      final int j = i * Mesh3Direct.NORMAL_STRIDE;
      return target.set(this.normals[j], this.normals[j + 1], this.normals[j
         + 2]);
   }

   public Vec2 getTexCoord ( final int i, final Vec2 target ) {

      final int j = i * Mesh3Direct.TEX_COORD_STRIDE;
      return target.set(this.coords[j], this.coords[j + 1]);
   }

   public Mesh3Direct insertCoord ( final int i, final float x, final float y,
      final float z, final float w ) {

      // TODO: Versions for tex coord and normal.

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

   public Mesh3Direct rotate ( final Quaternion q ) {

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

   public Mesh3Direct rotateX ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      return this.rotateX(cosa, sina);
   }

   public Mesh3Direct rotateX ( final float cosa, final float sina ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; i += Mesh3Direct.COORD_STRIDE ) {
         final float vy = this.coords[i + 1];
         final float vz = this.coords[i + 2];
         this.coords[i + 1] = cosa * vy - sina * vz;
         this.coords[i + 2] = cosa * vz + sina * vy;
      }

      return this;
   }

   public Mesh3Direct rotateY ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      return this.rotateY(cosa, sina);
   }

   public Mesh3Direct rotateY ( final float cosa, final float sina ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; i += Mesh3Direct.COORD_STRIDE ) {
         final float vx = this.coords[i];
         final float vz = this.coords[i + 2];
         this.coords[i] = cosa * vx + sina * vz;
         this.coords[i + 2] = cosa * vz - sina * vx;
      }

      return this;
   }

   public Mesh3Direct rotateZ ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      return this.rotateZ(cosa, sina);
   }

   public Mesh3Direct rotateZ ( final float cosa, final float sina ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; i += Mesh3Direct.COORD_STRIDE ) {
         final float vx = this.coords[i];
         final float vy = this.coords[i + 1];
         this.coords[i] = cosa * vx - sina * vy;
         this.coords[i + 1] = cosa * vy + sina * vx;
      }

      return this;
   }

   public Mesh3Direct scale ( final float s ) {

      if ( s != 0.0f ) {
         final int len = this.coords.length;
         for ( int i = 0; i < len; i += Mesh3Direct.COORD_STRIDE ) {
            this.coords[i] *= s;
            this.coords[i + 1] *= s;
            this.coords[i + 2] *= s;
         }
      }
      return this;
   }

   public Mesh3Direct scale ( final float x, final float y, final float z ) {

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

   public Mesh3Direct scale ( final Vec3 v ) {

      return this.scale(v.x, v.y, v.z);
   }

   public Mesh3Direct setColor ( final int i, final float r, final float g,
      final float b, final float a ) {

      final int j = i * Mesh3Direct.COLOR_STRIDE;
      this.colors[j] = r;
      this.colors[j + 1] = g;
      this.colors[j + 2] = b;
      this.colors[j + 3] = a;

      return this;
   }

   public Mesh3Direct setColorFromNormals ( ) {

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

   public Mesh3Direct setIndex ( final int i, final Index3 index ) {

      return this.setIndex(i, index.v, index.vt, index.vn);
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
      sb.append("{ indices = [ ");

      sb.append(" ], ");
      return sb.toString();
   }


   public Mesh3Direct transform ( final Mat4 m ) {

      return transform(m.m00, m.m01, m.m02, m.m03,
         m.m10, m.m11, m.m12, m.m13, m.m20, m.m21,
         m.m22, m.m23, m.m30, m.m31, m.m32,
         m.m33);
   }

   public Mesh3Direct transform ( final float m00, final float m01,
      final float m02, final float m03, final float m10, final float m11,
      final float m12, final float m13, final float m20, final float m21,
      final float m22, final float m23, final float m30, final float m31,
      final float m32, final float m33 ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; i += Mesh3Direct.COORD_STRIDE ) {
         float x = this.coords[i];
         float y = this.coords[i + 1];
         float z = this.coords[i + 2];
         float w = this.coords[i + 3];

         this.coords[i] = m00 * x + m01 * y + m02 * z + m03 * w;
         this.coords[i + 1] = m10 * x + m11 * y + m12 * z + m13 * w;
         this.coords[i + 2] = m20 * x + m21 * y + m22 * z + m23 * w;
         this.coords[i + 3] = m30 * x + m31 * y + m32 * z + m33 * w;
      }

      return this;
   }

   public Mesh3Direct translate ( final float x, final float y,
      final float z ) {

      final int len = this.coords.length;
      for ( int i = 0; i < len; i += Mesh3Direct.COORD_STRIDE ) {
         this.coords[i] += x;
         this.coords[i + 1] += y;
         this.coords[i + 2] += z;
      }

      return this;
   }

   public Mesh3Direct translate ( final Vec2 v ) {

      return this.translate(v.x, v.y, 0.0f);
   }

   public Mesh3Direct translate ( final Vec3 v ) {

      return this.translate(v.x, v.y, v.z);
   }

   public static final int COLOR_STRIDE = 4;

   public static final int COORD_STRIDE = 4;

   public static final int INDEX_STRIDE = 3;

   public static final int NORMAL_STRIDE = 3;

   public static final int TEX_COORD_STRIDE = 2;

   public static Mesh3Direct cube ( final float size,
      final Mesh3Direct target ) {

      final float vsz = Utils.max(IUtils.EPSILON, size);

      /* @formatter:off */
      target.indices = new int[] {
          0,  2,  3,    0,  3,  1,
          8,  4,  5,    8,  5,  9,
         10,  6,  7,   10,  7, 11,
         12, 13, 14,   12, 14, 15,
         16, 17, 18,   16, 18, 19,
         20, 21, 22,   20, 22, 23 };

      target.coords = new float[] {
          vsz, -vsz,  vsz, 1.0f,
         -vsz, -vsz,  vsz, 1.0f,
          vsz,  vsz,  vsz, 1.0f,
         -vsz,  vsz,  vsz, 1.0f,
          vsz,  vsz, -vsz, 1.0f,
         -vsz,  vsz, -vsz, 1.0f,
          vsz, -vsz, -vsz, 1.0f,
         -vsz, -vsz, -vsz, 1.0f,
          vsz,  vsz,  vsz, 1.0f,
         -vsz,  vsz,  vsz, 1.0f,
          vsz,  vsz, -vsz, 1.0f,
         -vsz,  vsz, -vsz, 1.0f,
          vsz, -vsz, -vsz, 1.0f,
          vsz, -vsz,  vsz, 1.0f,
         -vsz, -vsz,  vsz, 1.0f,
         -vsz, -vsz, -vsz, 1.0f,
         -vsz, -vsz,  vsz, 1.0f,
         -vsz,  vsz,  vsz, 1.0f,
         -vsz,  vsz, -vsz, 1.0f,
         -vsz, -vsz, -vsz, 1.0f,
          vsz, -vsz, -vsz, 1.0f,
          vsz,  vsz, -vsz, 1.0f,
          vsz,  vsz,  vsz, 1.0f,
          vsz, -vsz,  vsz, 1.0f };

      target.texCoords = new float[] {
         0.0f, 0.0f,
         1.0f, 0.0f,
         0.0f, 1.0f,
         1.0f, 1.0f,
         0.0f, 1.0f,
         1.0f, 1.0f,
         0.0f, 1.0f,
         1.0f, 1.0f,
         0.0f, 0.0f,
         1.0f, 0.0f,
         0.0f, 0.0f,
         1.0f, 0.0f,
         0.0f, 0.0f,
         0.0f, 1.0f,
         1.0f, 1.0f,
         1.0f, 0.0f,
         0.0f, 0.0f,
         0.0f, 1.0f,
         1.0f, 1.0f,
         1.0f, 0.0f,
         0.0f, 0.0f,
         0.0f, 1.0f,
         1.0f, 1.0f,
         1.0f, 0.0f };

      target.normals = new float[] {
          0.0f,  0.0f,  1.0f,
          0.0f,  0.0f,  1.0f,
          0.0f,  0.0f,  1.0f,
          0.0f,  0.0f,  1.0f,
          0.0f,  1.0f,  0.0f,
          0.0f,  1.0f,  0.0f,
          0.0f,  0.0f, -1.0f,
          0.0f,  0.0f, -1.0f,
          0.0f,  1.0f,  0.0f,
          0.0f,  1.0f,  0.0f,
          0.0f,  0.0f, -1.0f,
          0.0f,  0.0f, -1.0f,
          0.0f, -1.0f,  0.0f,
          0.0f, -1.0f,  0.0f,
          0.0f, -1.0f,  0.0f,
          0.0f, -1.0f,  0.0f,
         -1.0f,  0.0f,  0.0f,
         -1.0f,  0.0f,  0.0f,
         -1.0f,  0.0f,  0.0f,
         -1.0f,  0.0f,  0.0f,
          1.0f,  0.0f,  0.0f,
          1.0f,  0.0f,  0.0f,
          1.0f,  0.0f,  0.0f,
          1.0f,  0.0f,  0.0f };
      /* @formatter:on */

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

}
