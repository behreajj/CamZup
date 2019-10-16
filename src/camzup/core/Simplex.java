package camzup.core;

public abstract class Simplex {

   // private static final Vec4 curl0 = new Vec4(
   // 123.456f, 789.012f, 345.678f, 0.0f);
   //
   // private static final Vec4 curl1 = new Vec4(
   // 901.234f, 567.891f, 234.567f, 0.0f);

   /**
    * Squish constant 2D (Math.sqrt(3.0d) - 1.0d) / 2.0d;
    */
   private static final float F2 = 0.3660254037844386f;

   /**
    * Squish constant 3D (Math.sqrt(4.0d) - 1.0d) / 3.0d;
    */
   private static final float F3 = 0.3333333333333333f;

   /**
    * Squish constant 4D (Math.sqrt(5.0d) - 1.0d) / 4.0d;
    */
   private static final float F4 = 0.30901699437494745f;

   /**
    * Stretch constant 2D (1.0d / Math.sqrt(3.0d) - 1.0d) /
    * 2.0d
    */
   private static final float G2 = 0.21132486540518708f;

   private static final float G2_2 = 0.42264973081037416f;

   /**
    * Stretch constant 3D (1.0d / Math.sqrt(4.0d) - 1.0d) /
    * 3.0d
    */
   private static final float G3 = 0.16666666666666667f;

   private static final float G3_2 = 0.33333333333333333f;

   private static final float G3_3 = 0.5f;

   /**
    * Stretch constant 4D (1.0d / Math.sqrt(5.0d) - 1.0d) /
    * 4.0d
    */
   private static final float G4 = 0.13819660112501053f;

   private static final float G4_2 = 0.27639320225002106f;

   private static final float G4_3 = 0.41458980337503159f;

   private static final float G4_4 = 0.55278640450004212f;

   private static final Vec2[] grad2lut = {
         new Vec2(-1.0f, -1.0f),
         new Vec2(1.0f, 0.0f),
         new Vec2(-1.0f, 0.0f),
         new Vec2(1.0f, 1.0f),
         new Vec2(-1.0f, 1.0f),
         new Vec2(0.0f, -1.0f),
         new Vec2(0.0f, 1.0f),
         new Vec2(1.0f, -1.0f)
   };

   private static final Vec3[] grad3lut = {
         new Vec3(1.0f, 0.0f, 1.0f),
         new Vec3(0.0f, 1.0f, 1.0f),
         new Vec3(-1.0f, 0.0f, 1.0f),
         new Vec3(0.0f, -1.0f, 1.0f),
         new Vec3(1.0f, 0.0f, -1.0f),
         new Vec3(0.0f, 1.0f, -1.0f),
         new Vec3(-1.0f, 0.0f, -1.0f),
         new Vec3(0.0f, -1.0f, -1.0f),
         new Vec3(1.0f, -1.0f, 0.0f),
         new Vec3(1.0f, 1.0f, 0.0f),
         new Vec3(-1.0f, 1.0f, 0.0f),
         new Vec3(-1.0f, -1.0f, 0.0f),
         new Vec3(1.0f, 0.0f, 1.0f),
         new Vec3(-1.0f, 0.0f, 1.0f),
         new Vec3(0.0f, 1.0f, -1.0f),
         new Vec3(0.0f, -1.0f, -1.0f)
   };

   private static final Vec4[] grad4lut = {
         new Vec4(0.0f, 1.0f, 1.0f, 1.0f),
         new Vec4(0.0f, 1.0f, 1.0f, -1.0f),
         new Vec4(0.0f, 1.0f, -1.0f, 1.0f),
         new Vec4(0.0f, 1.0f, -1.0f, -1.0f),
         new Vec4(0.0f, -1.0f, 1.0f, 1.0f),
         new Vec4(0.0f, -1.0f, 1.0f, -1.0f),
         new Vec4(0.0f, -1.0f, -1.0f, 1.0f),
         new Vec4(0.0f, -1.0f, -1.0f, -1.0f),
         new Vec4(1.0f, 0.0f, 1.0f, 1.0f),
         new Vec4(1.0f, 0.0f, 1.0f, -1.0f),
         new Vec4(1.0f, 0.0f, -1.0f, 1.0f),
         new Vec4(1.0f, 0.0f, -1.0f, -1.0f),
         new Vec4(-1.0f, 0.0f, 1.0f, 1.0f),
         new Vec4(-1.0f, 0.0f, 1.0f, -1.0f),
         new Vec4(-1.0f, 0.0f, -1.0f, 1.0f),
         new Vec4(-1.0f, 0.0f, -1.0f, -1.0f),
         new Vec4(1.0f, 1.0f, 0.0f, 1.0f),
         new Vec4(1.0f, 1.0f, 0.0f, -1.0f),
         new Vec4(1.0f, -1.0f, 0.0f, 1.0f),
         new Vec4(1.0f, -1.0f, 0.0f, -1.0f),
         new Vec4(-1.0f, 1.0f, 0.0f, 1.0f),
         new Vec4(-1.0f, 1.0f, 0.0f, -1.0f),
         new Vec4(-1.0f, -1.0f, 0.0f, 1.0f),
         new Vec4(-1.0f, -1.0f, 0.0f, -1.0f),
         new Vec4(1.0f, 1.0f, 1.0f, 0.0f),
         new Vec4(1.0f, 1.0f, -1.0f, 0.0f),
         new Vec4(1.0f, -1.0f, 1.0f, 0.0f),
         new Vec4(1.0f, -1.0f, -1.0f, 0.0f),
         new Vec4(-1.0f, 1.0f, 1.0f, 0.0f),
         new Vec4(-1.0f, 1.0f, -1.0f, 0.0f),
         new Vec4(-1.0f, -1.0f, 1.0f, 0.0f),
         new Vec4(-1.0f, -1.0f, -1.0f, 0.0f)
   };

   private static final int[][] permute = {
         { 0, 1, 2, 3 }, { 0, 1, 3, 2 }, { 0, 0, 0, 0 }, { 0, 2, 3, 1 },
         { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 1, 2, 3, 0 },
         { 0, 2, 1, 3 }, { 0, 0, 0, 0 }, { 0, 3, 1, 2 }, { 0, 3, 2, 1 },
         { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 1, 3, 2, 0 },
         { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 },
         { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 },
         { 1, 2, 0, 3 }, { 0, 0, 0, 0 }, { 1, 3, 0, 2 }, { 0, 0, 0, 0 },
         { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 2, 3, 0, 1 }, { 2, 3, 1, 0 },
         { 1, 0, 2, 3 }, { 1, 0, 3, 2 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 },
         { 0, 0, 0, 0 }, { 2, 0, 3, 1 }, { 0, 0, 0, 0 }, { 2, 1, 3, 0 },
         { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 },
         { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 },
         { 2, 0, 1, 3 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 },
         { 3, 0, 1, 2 }, { 3, 0, 2, 1 }, { 0, 0, 0, 0 }, { 3, 1, 2, 0 },
         { 2, 1, 0, 3 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 },
         { 3, 1, 0, 2 }, { 0, 0, 0, 0 }, { 3, 2, 0, 1 }, { 3, 2, 1, 0 } };

   // private static final Vec4 promoted = new Vec4();

   /** Factor by which 2D noise is scaled prior to return. */
   private static final float SCALE_2 = 64.0f;

   /** Factor by which 3D noise is scaled prior to return. */
   private static final float SCALE_3 = 68.0f;

   /** Factor by which 4D noise is scaled prior to return. */
   private static final float SCALE_4 = 54.0f;

   private final static float STEP = 1.0f;

   // private static final Vec4 sum0 = new Vec4();

   // private static final Vec4 sum1 = new Vec4();

   // private static final Vec4 xDeriv = new Vec4();

   // private static final Vec4 yDeriv = new Vec4();

   // private static final Vec4 zDeriv = new Vec4();

   private static final Vec2 ZERO_2 = new Vec2();

   private static final Vec3 ZERO_3 = new Vec3();

   private static final Vec4 ZERO_4 = new Vec4();

   /**
    * A default seed set to the system current time in
    * milliseconds.
    */
   public static final int DEFAULT_SEED = (int) System.currentTimeMillis();

   private static Vec2 gradient2 (
         final int i,
         final int j,
         final int seed ) {

      final int h = Simplex.hash(i, j, seed);
      return Simplex.grad2lut[h & 7];
   }

   private static Vec3 gradient3 (
         final int i,
         final int j,
         final int k,
         final int seed ) {

      final int h = Simplex.hash(i, j, Simplex.hash(k, seed, 0));
      return Simplex.grad3lut[h & 15];
   }

   private static Vec4 gradient4 (
         final int i,
         final int j,
         final int k,
         final int l,
         final int seed ) {

      final int h = Simplex.hash(i, j, Simplex.hash(k, l, seed));
      return Simplex.grad4lut[h & 31];
   }

   private static int hash ( int a, int b, int c ) {

      // c ^= b;
      // c -= Simplex.rotate(b, 14);
      // a ^= c;
      // a -= Simplex.rotate(c, 11);
      // b ^= a;
      // b -= Simplex.rotate(a, 25);
      // c ^= b;
      // c -= Simplex.rotate(b, 16);
      // a ^= c;
      // a -= Simplex.rotate(c, 4);
      // b ^= a;
      // b -= Simplex.rotate(a, 14);
      // c ^= b;
      // c -= Simplex.rotate(b, 24);
      // return c;

      c ^= b;
      c -= b << 14 | b >> 32 - 14;
      a ^= c;
      a -= c << 11 | c >> 32 - 11;
      b ^= a;
      b -= a << 25 | a >> 32 - 25;
      c ^= b;
      c -= b << 16 | b >> 32 - 16;
      a ^= c;
      a -= c << 4 | c >> 32 - 4;
      b ^= a;
      b -= a << 14 | a >> 32 - 14;
      c ^= b;
      c -= b << 24 | b >> 32 - 24;
      return c;
   }

   // private static int rotate (
   // final int value,
   // final int rotation ) {
   //
   // return value << rotation | value >> 32 - rotation;
   // }

   // public static Vec3 curl (
   // final float vx,
   // final float vy,
   // final float vz,
   // final float vw,
   // final int seed,
   // final Vec3 target ) {
   //
   // Simplex.promoted.set(vx, vy, vz, vw);
   // Vec4.add(Simplex.promoted, Simplex.curl0, Simplex.sum0);
   // Vec4.add(Simplex.promoted, Simplex.curl1, Simplex.sum1);
   // Simplex.eval(Simplex.promoted, seed, Simplex.xDeriv);
   // Simplex.eval(Simplex.sum0, seed, Simplex.yDeriv);
   // Simplex.eval(Simplex.sum1, seed, Simplex.zDeriv);
   // return target.set(
   // Simplex.zDeriv.z - Simplex.yDeriv.w,
   // Simplex.xDeriv.w - Simplex.zDeriv.y,
   // Simplex.yDeriv.y - Simplex.xDeriv.z);
   // }
   //
   // public static Vec3 curl (
   // final float vx,
   // final float vy,
   // final float vz,
   // final int seed,
   // final Vec3 target ) {
   //
   // return Simplex.curl(vx, vy, vz, 0.0f, seed, target);
   // }
   //
   // public static Vec3 curl ( final Vec3 v, final int seed,
   // final Vec3 target ) {
   //
   // return Simplex.curl(v.x, v.y, v.z, 0.0f, seed, target);
   // }
   //
   // public static Vec3 curl ( final Vec4 v, final int seed,
   // final Vec3 target ) {
   //
   // return Simplex.curl(v.x, v.y, v.z, v.w, seed, target);
   // }

   public static float eval (
         final float x,
         final float y,
         final float z,
         final float w,
         final int seed ) {

      return Simplex.eval(x, y, z, w, seed, null);
   }

   public static float eval (
         final float x,
         final float y,
         final float z,
         final float w,
         final int seed,
         final Vec4 deriv ) {

      final float s = (x + y + z + w) * Simplex.F4;
      final int i = Utils.floorToInt(x + s);
      final int j = Utils.floorToInt(y + s);
      final int k = Utils.floorToInt(z + s);
      final int l = Utils.floorToInt(w + s);

      final float t = (i + j + k + l) * Simplex.G4;
      final float x0 = x - (i - t);
      final float y0 = y - (j - t);
      final float z0 = z - (k - t);
      final float w0 = w - (l - t);

      final int[] sc = Simplex.permute[(x0 > y0 ? 32 : 0) |
            (x0 > z0 ? 16 : 0) |
            (y0 > z0 ? 8 : 0) |
            (x0 > w0 ? 4 : 0) |
            (y0 > w0 ? 2 : 0) |
            (z0 > w0 ? 1 : 0)];
      final int sc0 = sc[0];
      final int sc1 = sc[1];
      final int sc2 = sc[2];
      final int sc3 = sc[3];

      final int i1 = sc0 >= 3 ? 1 : 0;
      final int j1 = sc1 >= 3 ? 1 : 0;
      final int k1 = sc2 >= 3 ? 1 : 0;
      final int l1 = sc3 >= 3 ? 1 : 0;

      final int i2 = sc0 >= 2 ? 1 : 0;
      final int j2 = sc1 >= 2 ? 1 : 0;
      final int k2 = sc2 >= 2 ? 1 : 0;
      final int l2 = sc3 >= 2 ? 1 : 0;

      final int i3 = sc0 >= 1 ? 1 : 0;
      final int j3 = sc1 >= 1 ? 1 : 0;
      final int k3 = sc2 >= 1 ? 1 : 0;
      final int l3 = sc3 >= 1 ? 1 : 0;

      final float x1 = x0 - i1 + Simplex.G4;
      final float y1 = y0 - j1 + Simplex.G4;
      final float z1 = z0 - k1 + Simplex.G4;
      final float w1 = w0 - l1 + Simplex.G4;

      final float x2 = x0 - i2 + Simplex.G4_2;
      final float y2 = y0 - j2 + Simplex.G4_2;
      final float z2 = z0 - k2 + Simplex.G4_2;
      final float w2 = w0 - l2 + Simplex.G4_2;

      final float x3 = x0 - i3 + Simplex.G4_3;
      final float y3 = y0 - j3 + Simplex.G4_3;
      final float z3 = z0 - k3 + Simplex.G4_3;
      final float w3 = w0 - l3 + Simplex.G4_3;

      final float x4 = x0 - 1.0f + Simplex.G4_4;
      final float y4 = y0 - 1.0f + Simplex.G4_4;
      final float z4 = z0 - 1.0f + Simplex.G4_4;
      final float w4 = w0 - 1.0f + Simplex.G4_4;

      float n0 = 0.0f;
      float n1 = 0.0f;
      float n2 = 0.0f;
      float n3 = 0.0f;
      float n4 = 0.0f;

      float t20 = 0.0f;
      float t21 = 0.0f;
      float t22 = 0.0f;
      float t23 = 0.0f;
      float t24 = 0.0f;

      float t40 = 0.0f;
      float t41 = 0.0f;
      float t42 = 0.0f;
      float t43 = 0.0f;
      float t44 = 0.0f;

      Vec4 g0 = Simplex.ZERO_4;
      Vec4 g1 = Simplex.ZERO_4;
      Vec4 g2 = Simplex.ZERO_4;
      Vec4 g3 = Simplex.ZERO_4;
      Vec4 g4 = Simplex.ZERO_4;

      final float t0 = 0.5f - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0;
      if (t0 >= 0.0f) {
         t20 = t0 * t0;
         t40 = t20 * t20;
         g0 = Simplex.gradient4(i, j, k, l, seed);
         n0 = t40 * (g0.x * x0 + g0.y * y0 + g0.z * z0 + g0.w * w0);
      }

      final float t1 = 0.5f - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1;
      if (t1 >= 0.0f) {
         t21 = t1 * t1;
         t41 = t21 * t21;
         g1 = Simplex.gradient4(i + i1, j + j1, k + k1, l + l1, seed);
         n1 = t41 * (g1.x * x1 + g1.y * y1 + g1.z * z1 + g1.w * w1);
      }

      final float t2 = 0.5f - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2;
      if (t2 >= 0.0f) {
         t22 = t2 * t2;
         t42 = t22 * t22;
         g2 = Simplex.gradient4(i + i2, j + j2, k + k2, l + l2, seed);
         n2 = t42 * (g2.x * x2 + g2.y * y2 + g2.z * z2 + g2.w * w2);
      }

      final float t3 = 0.5f - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3;
      if (t3 >= 0.0f) {
         t23 = t3 * t3;
         t43 = t23 * t23;
         g3 = Simplex.gradient4(i + i3, j + j3, k + k3, l + l3, seed);
         n3 = t43 * (g3.x * x3 + g3.y * y3 + g3.z * z3 + g3.w * w3);
      }

      final float t4 = 0.5f - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4;
      if (t4 >= 0.0f) {
         t24 = t4 * t4;
         t44 = t24 * t24;
         g4 = Simplex.gradient4(i + 1, j + 1, k + 1, l + 1, seed);
         n4 = t44 * (g4.x * x4 + g4.y * y4 + g4.z * z4 + g4.w * w4);
      }

      if (deriv != null) {

         final float tmp0 = t20 * t0
               * (g0.x * x0 + g0.y * y0 + g0.z * z0 + g0.w * w0);
         deriv.x = tmp0 * x0;
         deriv.y = tmp0 * y0;
         deriv.z = tmp0 * z0;
         deriv.w = tmp0 * w0;

         final float tmp1 = t21 * t1
               * (g1.x * x1 + g1.y * y1 + g1.z * z1 + g1.w * w1);
         deriv.x += tmp1 * x1;
         deriv.y += tmp1 * y1;
         deriv.z += tmp1 * z1;
         deriv.w += tmp1 * w1;

         final float tmp2 = t22 * t2
               * (g2.x * x2 + g2.y * y2 + g2.z * z2 + g2.w * w2);
         deriv.x += tmp2 * x2;
         deriv.y += tmp2 * y2;
         deriv.z += tmp2 * z2;
         deriv.w += tmp2 * w2;

         final float tmp3 = t23 * t3
               * (g3.x * x3 + g3.y * y3 + g3.z * z3 + g3.w * w3);
         deriv.x += tmp3 * x3;
         deriv.y += tmp3 * y3;
         deriv.z += tmp3 * z3;
         deriv.w += tmp3 * w3;

         final float tmp4 = t24 * t4
               * (g4.x * x4 + g4.y * y4 + g4.z * z4 + g4.w * w4);
         deriv.x += tmp4 * x4;
         deriv.y += tmp4 * y4;
         deriv.z += tmp4 * z4;
         deriv.w += tmp4 * w4;

         deriv.x *= -8.0f;
         deriv.y *= -8.0f;
         deriv.z *= -8.0f;
         deriv.w *= -8.0f;

         deriv.x += t40 * g0.x +
               t41 * g1.x +
               t42 * g2.x +
               t43 * g3.x +
               t44 * g4.x;
         deriv.y += t40 * g0.y +
               t41 * g1.y +
               t42 * g2.y +
               t43 * g3.y +
               t44 * g4.y;
         deriv.z += t40 * g0.z +
               t41 * g1.z +
               t42 * g2.z +
               t43 * g3.z +
               t44 * g4.z;
         deriv.w += t40 * g0.w +
               t41 * g1.w +
               t42 * g2.w +
               t43 * g3.w +
               t44 * g4.w;

         deriv.x *= Simplex.SCALE_4;
         deriv.y *= Simplex.SCALE_4;
         deriv.z *= Simplex.SCALE_4;
         deriv.w *= Simplex.SCALE_4;
      }

      return Simplex.SCALE_4 * (n0 + n1 + n2 + n3 + n4);
   }

   public static float eval (
         final float x,
         final float y,
         final float z,
         final int seed ) {

      return Simplex.eval(x, y, z, seed, null);
   }

   public static float eval (
         final float x,
         final float y,
         final float z,
         final int seed,
         final Vec3 deriv ) {

      final float s = (x + y + z) * Simplex.F3;
      final int i = Utils.floorToInt(x + s);
      final int j = Utils.floorToInt(y + s);
      final int k = Utils.floorToInt(z + s);

      final float t = (i + j + k) * Simplex.G3;
      final float x0 = x - (i - t);
      final float y0 = y - (j - t);
      final float z0 = z - (k - t);

      int i1 = 0;
      int j1 = 0;
      int k1 = 0;

      int i2 = 0;
      int j2 = 0;
      int k2 = 0;

      if (x0 >= y0) {
         if (y0 >= z0) {
            i1 = 1;
            i2 = 1;
            j2 = 1;
         } else if (x0 >= z0) {
            i1 = 1;
            i2 = 1;
            k2 = 1;
         } else {
            k1 = 1;
            i2 = 1;
            k2 = 1;
         }
      } else {
         if (y0 < z0) {
            k1 = 1;
            j2 = 1;
            k2 = 1;
         } else if (x0 < z0) {
            j1 = 1;
            j2 = 1;
            k2 = 1;
         } else {
            j1 = 1;
            i2 = 1;
            j2 = 1;
         }
      }

      final float x1 = x0 - i1 + Simplex.G3;
      final float y1 = y0 - j1 + Simplex.G3;
      final float z1 = z0 - k1 + Simplex.G3;

      final float x2 = x0 - i2 + Simplex.G3_2;
      final float y2 = y0 - j2 + Simplex.G3_2;
      final float z2 = z0 - k2 + Simplex.G3_2;

      final float x3 = x0 - 1.0f + Simplex.G3_3;
      final float y3 = y0 - 1.0f + Simplex.G3_3;
      final float z3 = z0 - 1.0f + Simplex.G3_3;

      float t20 = 0.0f;
      float t21 = 0.0f;
      float t22 = 0.0f;
      float t23 = 0.0f;

      float t40 = 0.0f;
      float t41 = 0.0f;
      float t42 = 0.0f;
      float t43 = 0.0f;

      float n0 = 0.0f;
      float n1 = 0.0f;
      float n2 = 0.0f;
      float n3 = 0.0f;

      Vec3 g0 = Simplex.ZERO_3;
      Vec3 g1 = Simplex.ZERO_3;
      Vec3 g2 = Simplex.ZERO_3;
      Vec3 g3 = Simplex.ZERO_3;

      final float t0 = 0.5f - x0 * x0 - y0 * y0 - z0 * z0;
      if (t0 >= 0.0f) {
         g0 = Simplex.gradient3(i, j, k, seed);
         t20 = t0 * t0;
         t40 = t20 * t20;
         n0 = t40 * (g0.x * x0 + g0.y * y0 + g0.z * z0);
      }

      final float t1 = 0.5f - x1 * x1 - y1 * y1 - z1 * z1;
      if (t1 >= 0.0f) {
         g1 = Simplex.gradient3(i + i1, j + j1, k + k1, seed);
         t21 = t1 * t1;
         t41 = t21 * t21;
         n1 = t41 * (g1.x * x1 + g1.y * y1 + g1.z * z1);
      }

      final float t2 = 0.5f - x2 * x2 - y2 * y2 - z2 * z2;
      if (t2 >= 0.0f) {
         g2 = Simplex.gradient3(i + i2, j + j2, k + k2, seed);
         t22 = t2 * t2;
         t42 = t22 * t22;
         n2 = t42 * (g2.x * x2 + g2.y * y2 + g2.z * z2);
      }

      final float t3 = 0.5f - x3 * x3 - y3 * y3 - z3 * z3;
      if (t3 >= 0.0f) {
         g3 = Simplex.gradient3(i + 1, j + 1, k + 1, seed);
         t23 = t3 * t3;
         t43 = t23 * t23;
         n3 = t43 * (g3.x * x3 + g3.y * y3 + g3.z * z3);
      }

      if (deriv != null) {

         final float tmp0 = t20 * t0 *
               (g0.x * x0 + g0.y * y0 + g0.z * z0);
         deriv.x = tmp0 * x0;
         deriv.y = tmp0 * y0;
         deriv.z = tmp0 * z0;

         final float tmp1 = t21 * t1 *
               (g1.x * x1 + g1.y * y1 + g1.z * z1);
         deriv.x += tmp1 * x1;
         deriv.y += tmp1 * y1;
         deriv.z += tmp1 * z1;

         final float tmp2 = t22 * t2 *
               (g2.x * x2 + g2.y * y2 + g2.z * z2);
         deriv.x += tmp2 * x2;
         deriv.y += tmp2 * y2;
         deriv.z += tmp2 * z2;

         final float tmp3 = t23 * t3 *
               (g3.x * x3 + g3.y * y3 + g3.z * z3);
         deriv.x += tmp3 * x3;
         deriv.y += tmp3 * y3;
         deriv.z += tmp3 * z3;

         deriv.x *= -8.0f;
         deriv.y *= -8.0f;
         deriv.z *= -8.0f;

         deriv.x += t40 * g0.x + t41 * g1.x + t42 * g2.x + t43 * g3.x;
         deriv.y += t40 * g0.y + t41 * g1.y + t42 * g2.y + t43 * g3.y;
         deriv.z += t40 * g0.z + t41 * g1.z + t42 * g2.z + t43 * g3.z;

         deriv.x *= Simplex.SCALE_3;
         deriv.y *= Simplex.SCALE_3;
         deriv.z *= Simplex.SCALE_3;
      }

      return Simplex.SCALE_3 * (n0 + n1 + n2 + n3);
   }

   public static float eval (
         final float x,
         final float y,
         final int seed ) {

      return Simplex.eval(x, y, seed, null);
   }

   public static float eval (
         final float x, final float y,
         final int seed,
         final Vec2 deriv ) {

      final float s = (x + y) * Simplex.F2;
      final int i = Utils.floorToInt(x + s);
      final int j = Utils.floorToInt(y + s);

      final float t = (i + j) * Simplex.G2;
      final float x0 = x - (i - t);
      final float y0 = y - (j - t);

      int i1 = 0;
      int j1 = 0;
      if (x0 > y0) {
         i1 = 1;
      } else {
         j1 = 1;
      }

      final float x1 = x0 - i1 + Simplex.G2;
      final float y1 = y0 - j1 + Simplex.G2;

      final float x2 = x0 - 1.0f + Simplex.G2_2;
      final float y2 = y0 - 1.0f + Simplex.G2_2;

      float t20 = 0.0f;
      float t21 = 0.0f;
      float t22 = 0.0f;

      float t40 = 0.0f;
      float t41 = 0.0f;
      float t42 = 0.0f;

      float n0 = 0.0f;
      float n1 = 0.0f;
      float n2 = 0.0f;

      Vec2 g0 = Simplex.ZERO_2;
      Vec2 g1 = Simplex.ZERO_2;
      Vec2 g2 = Simplex.ZERO_2;

      final float t0 = 0.5f - x0 * x0 - y0 * y0;
      if (t0 >= 0.0f) {
         g0 = Simplex.gradient2(i, j, seed);
         t20 = t0 * t0;
         t40 = t20 * t20;
         n0 = t40 * (g0.x * x0 + g0.y * y0);
      }

      final float t1 = 0.5f - x1 * x1 - y1 * y1;
      if (t1 >= 0.0f) {
         g1 = Simplex.gradient2(i + i1, j + j1, seed);
         t21 = t1 * t1;
         t41 = t21 * t21;
         n1 = t41 * (g1.x * x1 + g1.y * y1);
      }

      final float t2 = 0.5f - x2 * x2 - y2 * y2;
      if (t2 >= 0.0f) {
         g2 = Simplex.gradient2(i + 1, j + 1, seed);
         t22 = t2 * t2;
         t42 = t22 * t22;
         n2 = t42 * (g2.x * x2 + g2.y * y2);
      }

      if (deriv != null) {

         final float tmp0 = t20 * t0 * (g0.x * x0 + g0.y * y0);
         deriv.x = tmp0 * x0;
         deriv.y = tmp0 * y0;

         final float tmp1 = t21 * t1 * (g1.x * x1 + g1.y * y1);
         deriv.x += tmp1 * x1;
         deriv.y += tmp1 * y1;

         final float tmp2 = t22 * t2 * (g2.x * x2 + g2.y * y2);
         deriv.x += tmp2 * x2;
         deriv.y += tmp2 * y2;

         deriv.x *= -8.0f;
         deriv.y *= -8.0f;

         deriv.x += t40 * g0.x + t41 * g1.x + t42 * g2.x;
         deriv.y += t40 * g0.y + t41 * g1.y + t42 * g2.y;

         deriv.x *= Simplex.SCALE_2;
         deriv.y *= Simplex.SCALE_2;
      }

      return Simplex.SCALE_2 * (n0 + n1 + n2);
   }

   public static float eval (
         final Vec2 v,
         final int seed ) {

      return Simplex.eval(v.x, v.y, seed, null);
   }

   public static float eval (
         final Vec2 v,
         final int seed,
         final Vec2 deriv ) {

      return Simplex.eval(v.x, v.y, seed, deriv);
   }

   public static float eval (
         final Vec3 v,
         final int seed ) {

      return Simplex.eval(v.x, v.y, v.z, seed, null);
   }

   public static float eval (
         final Vec3 v,
         final int seed,
         final Vec3 deriv ) {

      return Simplex.eval(v.x, v.y, v.z, seed, deriv);
   }

   public static float eval (
         final Vec4 v,
         final int seed ) {

      return Simplex.eval(v.x, v.y, v.z, v.w, seed, null);
   }

   public static float eval (
         final Vec4 v,
         final int seed,
         final Vec4 deriv ) {

      return Simplex.eval(v.x, v.y, v.z, v.w, seed, deriv);
   }

   public static float fbm (
         final Vec2 v,
         final int seed,
         final int octaves,
         final float amplitude,
         final float lacunarity,
         final float persistence ) {

      final int valoc = octaves < 1 ? 1 : octaves;
      final float valpers = Utils.max(persistence, Utils.EPSILON);
      float amp = amplitude != 0.0f ? amplitude : 1.0f;

      float out = 0.0f;
      float maxAmp = 0.0f;

      float vx = v.x;
      float vy = v.y;

      for (int i = 0; i < valoc; ++i) {
         out += amp * Simplex.eval(vx, vy, seed, null);
         maxAmp += amp;
         amp *= valpers;
         vx *= lacunarity;
         vy *= lacunarity;
      }

      return out / maxAmp;
   }

   public static float fbm (
         final Vec3 v,
         final int seed,
         final int octaves,
         final float amplitude,
         final float lacunarity,
         final float persistence ) {

      final int valoc = octaves < 1 ? 1 : octaves;
      final float valpers = Utils.max(persistence, Utils.EPSILON);
      float amp = amplitude != 0.0f ? amplitude : 1.0f;

      float out = 0.0f;
      float maxAmp = 0.0f;

      float vx = v.x;
      float vy = v.y;
      float vz = v.z;

      for (int i = 0; i < valoc; ++i) {
         out += amp * Simplex.eval(vx, vy, vz, seed, null);
         maxAmp += amp;
         amp *= valpers;
         vx *= lacunarity;
         vy *= lacunarity;
         vz *= lacunarity;
      }

      return out / maxAmp;
   }

   public static float fbm (
         final Vec4 v,
         final int seed,
         final int octaves,
         final float amplitude,
         final float lacunarity,
         final float persistence ) {

      final int valoc = octaves < 1 ? 1 : octaves;
      final float valpers = Utils.max(persistence, Utils.EPSILON);
      float amp = amplitude != 0.0f ? amplitude : 1.0f;

      float out = 0.0f;
      float maxAmp = 0.0f;

      float vx = v.x;
      float vy = v.y;
      float vz = v.z;
      float vw = v.w;

      for (int i = 0; i < valoc; ++i) {
         out += amp * Simplex.eval(vx, vy, vz, vw, seed, null);
         maxAmp += amp;
         amp *= valpers;
         vx *= lacunarity;
         vy *= lacunarity;
         vz *= lacunarity;
         vw *= lacunarity;
      }

      return out / maxAmp;
   }

   /**
    * Wraps eval so that the output matches the input in number
    * of dimensions. This calls eval for one dimension higher
    * than the input, where the extra argument is an offset
    * step.
    * 
    * @param v
    *           the input vector
    * @param seed
    *           the seed
    * @param target
    *           the output vector
    * @return the noise value
    * @see Simplex#STEP
    */
   public static Vec2 noise (
         final Vec2 v,
         final int seed,
         final Vec2 target ) {

      return target.set(
            Simplex.eval(
                  v.x, v.y,
                  0.0f, seed, null),
            Simplex.eval(
                  v.x, v.y,
                  Simplex.STEP, seed, null));
   }

   /**
    * Wraps eval so that the output matches the input in number
    * of dimensions. This calls eval for one dimension higher
    * than the input, where the extra argument is an offset
    * step.
    * 
    * @param v
    *           the input vector
    * @param seed
    *           the seed
    * @param target
    *           the output vector
    * @return the noise value
    * @see Simplex#STEP
    */
   public static Vec3 noise (
         final Vec3 v,
         final int seed,
         final Vec3 target ) {

      return target.set(
            Simplex.eval(
                  v.x, v.y, v.z,
                  0.0f, seed, null),
            Simplex.eval(
                  v.x, v.y, v.z,
                  Simplex.STEP, seed, null),
            Simplex.eval(
                  v.x, v.y, v.z,
                  Simplex.STEP + Simplex.STEP, seed, null));
   }
}
