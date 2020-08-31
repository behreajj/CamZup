package camzup.pfriendly;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import camzup.core.Experimental;
import camzup.core.Mesh2;
import camzup.core.Mesh3;
import camzup.core.Transform2;
import camzup.core.Transform3;
import camzup.core.Vec2;
import camzup.core.Vec3;

@Experimental
public class Mesh3Fixed extends Mesh3 {
   public FloatBuffer coordBuffer;
   public IntBuffer dataBuffer = IntBuffer.allocate(4);
   public IntBuffer indexBuffer;
   public FloatBuffer normalBuffer;
   public FloatBuffer texCoordBuffer;

   public Mesh3Fixed ( ) {

      super();
      this.init();
   }

   public Mesh3Fixed ( final int[][][] faces, final Vec3[] coords,
      final Vec2[] texCoords, final Vec3[] normals ) {

      super(faces, coords, texCoords, normals);
      this.init();
   }

   public Mesh3Fixed ( final Mesh2 source ) {

      super(source);
      this.init();
   }

   public Mesh3Fixed ( final Mesh3 source ) {

      super(source);
      this.init();
   }

   public Mesh3Fixed ( final String name ) {

      super(name);
      this.init();
   }

   public Mesh3Fixed ( final String name, final int[][][] faces,
      final Vec3[] coords, final Vec2[] texCoords, final Vec3[] normals ) {

      super(name, faces, coords, texCoords, normals);
      this.init();
   }

   public void init ( ) {

      this.clean();
      this.triangulate();
      Mesh3.uniformData(this, this);
      this.update();
   }

   public void update ( ) { this.update(new Transform3()); }

   public void update ( final Transform3 tr ) {

      this.update(tr, new Transform2());
   }

   public void update ( final Transform3 tr, final Transform2 trTexture ) {

      /* rewind function throws an error in Processing v3.5.4 . */

      final ByteOrder byteOrder = ByteOrder.nativeOrder();
      final Vec3 vTransform = new Vec3();
      final Vec2 vtTransform = new Vec2();

      /* Coordinates. */
      final Vec3[] vs = this.coords;
      final int vsLen = vs.length;
      final int vStride = 4;
      final int vsArrLen = vsLen * vStride;
      final float[] vsArr = new float[vsArrLen];
      for ( int i = 0, j = 0; i < vsLen; ++i, j += vStride ) {
         final Vec3 v = vs[i];
         Transform3.mulPoint(tr, v, vTransform);
         vsArr[j] = vTransform.x;
         vsArr[j + 1] = vTransform.y;
         vsArr[j + 2] = vTransform.z;
         vsArr[j + 3] = 1.0f;
      }

      this.coordBuffer = ByteBuffer.allocateDirect(vsArrLen * Float.BYTES)
         .order(byteOrder).asFloatBuffer();
      // this.coordBuffer.rewind();
      this.coordBuffer.put(vsArr, 0, vsArrLen);
      // this.coordBuffer.rewind();

      /* Texture coordinates. */
      final Vec2[] vts = this.texCoords;
      final int vtsLen = vts.length;
      final int vtStride = 3;
      final int vtsArrLen = vtsLen * vtStride;
      final float[] vtsArr = new float[vtsArrLen];
      for ( int i = 0, j = 0; i < vsLen; ++i, j += vtStride ) {
         final Vec2 vt = vts[i];
         Transform2.mulTexCoord(trTexture, vt, vtTransform);
         vtsArr[j] = vtTransform.x;
         vtsArr[j + 1] = vtTransform.y;
         vtsArr[j + 2] = 1.0f;
      }

      this.texCoordBuffer = ByteBuffer.allocateDirect(vtsArrLen * Float.BYTES)
         .order(byteOrder).asFloatBuffer();
      // this.texCoordBuffer.rewind();
      this.texCoordBuffer.put(vtsArr, 0, vtsArrLen);
      // this.texCoordBuffer.rewind();

      /* Normals. */
      final Vec3[] vns = this.normals;
      final int vnsLen = vns.length;
      final int vnStride = 4;
      final int vnsArrLen = vnsLen * vnStride;
      final float[] vnsArr = new float[vnsArrLen];
      for ( int i = 0, j = 0; i < vsLen; ++i, j += vnStride ) {
         final Vec3 vn = vns[i];
         Transform3.mulNormal(tr, vn, vTransform);
         vnsArr[j] = vTransform.x;
         vnsArr[j + 1] = vTransform.y;
         vnsArr[j + 2] = vTransform.z;
         vnsArr[j + 3] = 0.0f;
      }

      this.normalBuffer = ByteBuffer.allocateDirect(vnsArrLen * Float.BYTES)
         .order(byteOrder).asFloatBuffer();
      // this.normalBuffer.rewind();
      this.normalBuffer.put(vnsArr, 0, vnsArrLen);
      // this.normalBuffer.rewind();

      /* Indices. */
      final int[][][] fs = this.faces;
      final int fsLen = fs.length;
      final int idcsStride = 3;
      final int idcsLen = fsLen * idcsStride;
      final int[] idcs = new int[idcsLen];
      for ( int k = 0, i = 0; i < fsLen; ++i ) {
         final int[][] f = fs[i];

         /* Should be 3 in all cases. */
         final int fLen = f.length;
         for ( int j = 0; j < fLen; ++j, ++k ) { idcs[k] = f[j][0]; }
      }

      this.indexBuffer = ByteBuffer.allocateDirect(idcsLen * Integer.BYTES)
         .order(byteOrder).asIntBuffer();

      // this.indexBuffer.rewind();
      this.indexBuffer.put(idcs, 0, idcsLen);
      this.indexBuffer.rewind();
   }

}
