package camzup;

import processing.core.PApplet;

/**
 * The main class of this library. This is not needed to use the library
 * and is for development and debugging only.
 */
public class CamZup {

   /**
    * The PApplet referenced by this class.
    */
   public final PApplet parent;

   /**
    * Constructs a new instance of this library with the PApplet as a
    * reference.
    *
    * @param parent the parent applet
    */
   public CamZup ( final PApplet parent ) { this.parent = parent; }

   /**
    * Returns a string representation of the CamZup class.
    *
    * @return the string
    */
   @Override
   public String toString ( ) {

      final StringBuilder sb = new StringBuilder(64);
      sb.append("{ version: ");
      sb.append(CamZup.VERSION);
      sb.append(", parent: ");
      sb.append(this.parent);
      sb.append(" }");
      return sb.toString();
   }

   /**
    * The library's current version.
    */
   public static final String VERSION = "##library.prettyVersion##";

   /**
    * The main function.
    *
    * @param args the string of arguments
    */
   public static void main ( final String[] args ) {

      // final Bounds2 b = new Bounds2(0, 0, 320, 180);
      // System.out.println(b);
      //
      // final Ray2 rightPos = new Ray2(new Vec2(160, 90), new Vec2(1, -1));
      // System.out.println(rightPos);
      //
      // final Ray2 rightNeg = new Ray2(new Vec2(160, 90), new Vec2(-1, 1));
      // System.out.println(rightNeg);
      //
      // final Vec2[] arr0 = Ray2.intersections(rightPos, b);
      // for ( final Vec2 v : arr0 ) { System.out.println(v); }
      //
      // final Vec2[] arr1 = Ray2.intersections(rightNeg, b);
      // for ( final Vec2 v : arr1 ) { System.out.println(v); }
      //
      // final Ray2 leftPos = new Ray2(new Vec2(160, 90), new Vec2(1, 1));
      // System.out.println(leftPos);
      //
      // final Ray2 leftNeg = new Ray2(new Vec2(160, 90), new Vec2(-1, -1));
      // System.out.println(leftNeg);
      //
      // final Vec2[] arr2 = Ray2.intersections(leftPos, b);
      // for ( final Vec2 v : arr2 ) { System.out.println(v); }
      //
      // final Vec2[] arr3 = Ray2.intersections(leftNeg, b);
      // for ( final Vec2 v : arr3 ) { System.out.println(v); }

      // final Vec2 orig0 = new Vec2(0, 180);
      // final Vec2 dest0 = new Vec2(320, 180);
      // final float result0 = Ray2.intersectLineSeg(r0, orig0, dest0);
      // System.out.println(result0);
      // final Vec2 point0 = Vec2.mix(orig0, dest0, result0,
      // new Vec2());
      // System.out.println(point0);
      //
      // final Vec2 orig1 = new Vec2(0, 0);
      // final Vec2 dest1 = new Vec2(320, 0);
      // // final float result1 = Ray2.intersectLineSeg(r1, orig1, dest1);
      // // System.out.println(result1);
      // // final Vec2 point1 = Vec2.mix(orig1, dest1, result1, new Vec2());
      // // System.out.println(point1);
      //
      // Vec2[] arr = Ray2.intersections(r1, orig1, dest1);
      // System.out.println(arr[0]);

      // Mesh3 m = new Mesh3();
      // Mesh3.cube(0.5f, PolyType.TRI, UvProfile.Cube.CROSS, m);
      // m.triangulate();
      // m.clean();
      // MeshEntity3 me = new MeshEntity3(m);
      // String s = me.toBlenderCode();
      // System.out.println(s);

      // byte[] bytes = m.toGltfData();
      // for ( int i = 0; i < bytes.length; ++i ) {
      // if ( ( i ) % 16 == 0 ) System.out.print(String.format("\n%04x | ", i));
      // System.out.print(String.format("%02x", bytes[i]));
      // if ( ( i + 1 ) % 4 == 0 )
      // System.out.print(" ");
      // else
      // System.out.print(" ");
      // }

      // https://www.russellcottrell.com/photo/matrixCalculator.htm

      // final float xr = 0.680f;
      // final float yr = 0.320f;
      //
      // final float xg = 0.265f;
      // final float yg = 0.690f;
      //
      // final float xb = 0.150f;
      // final float yb = 0.060f;
      //
      // final float xw = 0.3127f;
      // final float yw = 0.3290f;
      //
      // final float cspwx = 0.9642f;
      // final float cspwy = 1.0f;
      // final float cspwz = 0.8249f;
      //
      // final float Xr = xr / yr;
      // final float Yr = 1.0f;
      // final float Zr = ( 1.0f - xr - yr ) / yr;
      //
      // final float Xg = xg / yg;
      // final float Yg = 1.0f;
      // final float Zg = ( 1.0f - xg - yg ) / yg;
      //
      // final float Xb = xb / yb;
      // final float Yb = 1.0f;
      // final float Zb = ( 1.0f - xb - yb ) / yb;
      //
      // final Mat3 RGB = new Mat3( /* */
      // Xr, Xg, Xb, /* */
      // Yr, Yg, Yb, /* */
      // Zr, Zg, Zb);
      // System.out.println("RGB:");
      // System.out.println(RGB.toStringCol());
      //
      // final Mat4 RGB_4 = new Mat4( /* */
      // Xr, Xg, Xb, /* */
      // Yr, Yg, Yb, /* */
      // Zr, Zg, Zb);
      // System.out.println("RGB_4:");
      // System.out.println(RGB_4.toStringCol());
      //
      // final float Xw = xw / yw;
      // final float Yw = 1.0f;
      // final float Zw = ( 1.0f - xw - yw ) / yw;
      //
      // final Vec3 W1 = new Vec3(Xw, Yw, Zw);
      // System.out.println("W1:");
      // System.out.println(W1);
      //
      // final Vec3 W2 = new Vec3(cspwx / cspwy, 1.0f, cspwz / cspwy);
      // System.out.println("\nW2:");
      // System.out.println(W2);
      //
      // final Mat3 RGB_INVERSE = Mat3.inverse(RGB, new Mat3());
      // System.out.println("\nRGB_INVERSE:");
      // System.out.println(RGB_INVERSE.toStringCol());
      //
      // final Mat4 RGB_4_INVERSE = Mat4.inverse(RGB_4, new Mat4());
      // System.out.println("\nRGB_4_INVERSE:");
      // System.out.println(RGB_4_INVERSE.toStringCol());
      //
      // final Vec3 S = Mat3.mul(RGB_INVERSE, W1, new Vec3());
      // System.out.println("S:");
      // System.out.println(S);
      //
      // // This is like matrix x vector mul, except that
      // // instead of summing each row, they're separated
      // // into columns... Maybe this is called linear map?
      // final Mat3 M = new Mat3( /* */
      // S.x * RGB.m00, S.y * RGB.m01, S.z * RGB.m02, /* */
      // S.x * RGB.m10, S.y * RGB.m11, S.z * RGB.m12, /* */
      // S.x * RGB.m20, S.y * RGB.m21, S.z * RGB.m22);
      // System.out.println("\nM:");
      // System.out.println(M.toStringCol());
      //
      // final Mat3 Mv2 = Mat3.mul(RGB, S, new Mat3());
      // System.out.println("\nMv2:");
      // System.out.println(Mv2.toStringCol());
      //
      // final Mat3 M_1 = Mat3.inverse(M, new Mat3());
      // System.out.println("M_1:");
      // System.out.println(M_1.toStringCol());
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
