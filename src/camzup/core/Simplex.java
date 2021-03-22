package camzup.core;

/**
 * A simplex noise class created with reference to "<a href=
 * "http://staffwww.itn.liu.se/~stegu/simplexnoise/simplexnoise.pdf">Simplex
 * noise demystified</a>" by Stefan Gustavson. Hashing functions are based
 * on Bob Jenkins lookup3 script, <a href=
 * "http://burtleburtle.net/bob/c/lookup3.c">http://burtleburtle.net/bob/c/lookup3.c</a>.
 * Flow implementations written with reference to Simon Geilfus's
 * <a href= "https://github.com/simongeilfus/SimplexNoise">library</a>,
 * which in turn references the work of
 * <a href="https://www.cs.ubc.ca/~rbridson/">Robert Bridson</a>.<br>
 * <br>
 * This implementation uses the following variations:
 * <ul>
 * <li><code>n</code> and <code>t</code> factors are multiplied later in
 * the noise evaluation function so that a calculation does not need to be
 * redone for derivatives.</li>
 * <li>Stretch constants are multiplied by their coefficients and stored in
 * constants (e.g., {@link Simplex#G3_2});</li>
 * <li>The {@link Simplex#DEFAULT_SEED} is set to the current time in
 * milliseconds.</li>
 * <li>When noise derivatives are calculated, the result is stored in an
 * output parameter; the derivative is <em>not</em> concatenated with the
 * scalar result into a vector one dimension higher than the input
 * vector.</li>
 * <li>Mutable integer offsets i, j, k are initialized to zero, then
 * changed only if appropriate conditions are met.</li>
 * <li>The offset step in noise functions is matched to the magnitude of
 * the input vector rather than using an arbitrary one (arbitrary meaning
 * <code>123.456</code>, etc.).</li>
 * </ul>
 * Most simplex functions scale the sum of noise contributions by a magic
 * number, such as {@link Simplex#SCALE_2}, to bring the output into range
 * [-1.0, 1.0] . There is little explanation for how these numbers are
 * arrived at. The range is not guaranteed.
 *
 * @author Robert Bridson
 * @author Simon Geilfus
 * @author Stefan Gustavson
 * @author Bob Jenkins
 */
public abstract class Simplex extends Generative {

   /**
    * Discourage overriding with a private constructor.
    */
   private Simplex ( ) {}

   /**
    * Squish constant 2D <code>(Math.sqrt(3.0) - 1.0) / 2.0</code>;
    * approximately {@value Simplex#F2} .
    */
   public static final float F2 = 0.36602542f;

   /**
    * Squish constant 3D <code>(Math.sqrt(4.0) - 1.0) / 3.0</code>;
    * approximately {@value Simplex#F3} .
    */
   public static final float F3 = IUtils.ONE_THIRD;

   /**
    * Squish constant 4D <code>(Math.sqrt(5.0) - 1.0) / 4.0</code>;
    * approximately {@value Simplex#F4} .
    */
   public static final float F4 = 0.309017f;

   /**
    * Stretch constant 2D <code>(3.0 - Math.sqrt(3.0)) / 6.0</code>;
    * approximately {@value Simplex#G2} .
    */
   public static final float G2 = 0.21132487f;

   /**
    * Stretch constant 3D. {@value Simplex#G3} .
    */
   public static final float G3 = IUtils.ONE_SIX;

   /**
    * 2x stretch constant 3D. {@value Simplex#G3_2} .
    */
   public static final float G3_2 = IUtils.ONE_THIRD;

   /**
    * Stretch constant 4D (<code>1.0 / Math.sqrt(5.0) - 1.0) /
    * 4.0</code>; approximately {@value Simplex#G4} .
    */
   public static final float G4 = 0.1381966f;

   /**
    * 2x stretch constant 4D. Approximately {@value Simplex#G4_2} .
    */
   public static final float G4_2 = 0.2763932f;

   /**
    * 3x stretch constant 4D. Approximately {@value Simplex#G4_3} .
    */
   public static final float G4_3 = 0.4145898f;

   /**
    * 2x stretch constant 2D minus one. Approximately {@value Simplex#N2_2_1}
    * .
    */
   public static final float N2_2_1 = IUtils.ONE_SQRT_3;

   /**
    * 3x stretch constant 3D minus one. Approximately {@value Simplex#N3_3_1}
    * .
    */
   public static final float N3_3_1 = 0.5f;

   /**
    * 4x stretch constant 4D minus one. Approximately {@value Simplex#N4_4_1}
    * .
    */
   public static final float N4_4_1 = 0.4472136f;

   /**
    * <code>Math.sqrt(2.0) / Math.sqrt(3.0)</code>. Used by rotation look up
    * tables. Approximately {@value Simplex#RT2_RT3} .
    */
   public static final float RT2_RT3 = 0.8164966f;

   /**
    * Factor by which 2D noise is scaled prior to return:
    * {@value Simplex#SCALE_2} .
    */
   public static final float SCALE_2 = 64.0f;

   /**
    * Factor by which 3D noise is scaled prior to return:
    * {@value Simplex#SCALE_3} .
    */
   public static final float SCALE_3 = 68.0f;

   /**
    * Factor by which 4D noise is scaled prior to return:
    * {@value Simplex#SCALE_4} .
    */
   public static final float SCALE_4 = 54.0f;

   /**
    * Factor added to 2D noise when returning a Vec2. <code>1.0 /
    * Math.sqrt(2.0)</code>; approximately {@value Simplex#STEP_2} .
    */
   public static final float STEP_2 = IUtils.ONE_SQRT_2;

   /**
    * Factor added to 3D noise when returning a Vec3. <code>1.0 /
    * Math.sqrt(3.0)</code>; approximately {@value Simplex#STEP_3} .
    */
   public static final float STEP_3 = IUtils.ONE_SQRT_3;

   /**
    * Factor added to 4D noise when returning a Vec4. <code>1.0 /
    * Math.sqrt(4.0)</code>; {@value Simplex#STEP_4} .
    */
   public static final float STEP_4 = 0.5f;

   /**
    * 2D simplex gradient look-up table.
    */
   private static final Vec2[] GRAD_2_LUT;

   /**
    * 3D simplex gradient look-up table.
    */
   private static final Vec3[] GRAD_3_LUT;

   /**
    * 4D simplex gradient look-up table.
    */
   private static final Vec4[] GRAD_4_LUT;

   /**
    * Table for 3D rotations, u. Multiplied by the cosine of an angle in 3D
    * gradient rotations.
    */
   private static final Vec3[] GRAD3_U;

   /**
    * Table for 3D rotations, v. Multiplied by the sine of an angle in 3D
    * gradient rotations.
    */
   private static final Vec3[] GRAD3_V;

   /**
    * Permutation table for 4D noise.
    */
   private static final int[][] PERMUTE;

   /**
    * Temporary vector used by gradRot2.
    */
   private static final Vec2 ROT_2;

   /**
    * Temporary vector used by gradRot3.
    */
   private static final Vec3 ROT_3;

   /**
    * Initial state to which a 2D noise contribution is set. Prevents compiler
    * complaint that variables may not have been initialized.
    */
   private static final Vec2 ZERO_2;

   /**
    * Initial state to which a 3D noise contribution is set. Prevents compiler
    * complaint that variables may not have been initialized.
    */
   private static final Vec3 ZERO_3;

   /**
    * Initial state to which a 4D noise contribution is set. Prevents compiler
    * complaint that variables may not have been initialized.
    */
   private static final Vec4 ZERO_4;

   static {
      /* @formatter:off */
      GRAD_2_LUT = new Vec2[] {
         new Vec2(-1.0f, -1.0f),
         new Vec2( 1.0f,  0.0f),
         new Vec2(-1.0f,  0.0f),
         new Vec2( 1.0f,  1.0f),
         new Vec2(-1.0f,  1.0f),
         new Vec2( 0.0f, -1.0f),
         new Vec2( 0.0f,  1.0f),
         new Vec2( 1.0f, -1.0f) };

      GRAD_3_LUT = new Vec3[] {
         new Vec3( 1.0f,  0.0f,  1.0f),
         new Vec3( 0.0f,  1.0f,  1.0f),
         new Vec3(-1.0f,  0.0f,  1.0f),
         new Vec3( 0.0f, -1.0f,  1.0f),
         new Vec3( 1.0f,  0.0f, -1.0f),
         new Vec3( 0.0f,  1.0f, -1.0f),
         new Vec3(-1.0f,  0.0f, -1.0f),
         new Vec3( 0.0f, -1.0f, -1.0f),
         new Vec3( 1.0f, -1.0f,  0.0f),
         new Vec3( 1.0f,  1.0f,  0.0f),
         new Vec3(-1.0f,  1.0f,  0.0f),
         new Vec3(-1.0f, -1.0f,  0.0f),
         new Vec3( 1.0f,  0.0f,  1.0f),
         new Vec3(-1.0f,  0.0f,  1.0f),
         new Vec3( 0.0f,  1.0f, -1.0f),
         new Vec3( 0.0f, -1.0f, -1.0f) };

      GRAD_4_LUT = new Vec4[] {
         new Vec4( 0.0f,  1.0f,  1.0f,  1.0f),
         new Vec4( 0.0f,  1.0f,  1.0f, -1.0f),
         new Vec4( 0.0f,  1.0f, -1.0f,  1.0f),
         new Vec4( 0.0f,  1.0f, -1.0f, -1.0f),
         new Vec4( 0.0f, -1.0f,  1.0f,  1.0f),
         new Vec4( 0.0f, -1.0f,  1.0f, -1.0f),
         new Vec4( 0.0f, -1.0f, -1.0f,  1.0f),
         new Vec4( 0.0f, -1.0f, -1.0f, -1.0f),
         new Vec4( 1.0f,  0.0f,  1.0f,  1.0f),
         new Vec4( 1.0f,  0.0f,  1.0f, -1.0f),
         new Vec4( 1.0f,  0.0f, -1.0f,  1.0f),
         new Vec4( 1.0f,  0.0f, -1.0f, -1.0f),
         new Vec4(-1.0f,  0.0f,  1.0f,  1.0f),
         new Vec4(-1.0f,  0.0f,  1.0f, -1.0f),
         new Vec4(-1.0f,  0.0f, -1.0f,  1.0f),
         new Vec4(-1.0f,  0.0f, -1.0f, -1.0f),
         new Vec4( 1.0f,  1.0f,  0.0f,  1.0f),
         new Vec4( 1.0f,  1.0f,  0.0f, -1.0f),
         new Vec4( 1.0f, -1.0f,  0.0f,  1.0f),
         new Vec4( 1.0f, -1.0f,  0.0f, -1.0f),
         new Vec4(-1.0f,  1.0f,  0.0f,  1.0f),
         new Vec4(-1.0f,  1.0f,  0.0f, -1.0f),
         new Vec4(-1.0f, -1.0f,  0.0f,  1.0f),
         new Vec4(-1.0f, -1.0f,  0.0f, -1.0f),
         new Vec4( 1.0f,  1.0f,  1.0f,  0.0f),
         new Vec4( 1.0f,  1.0f, -1.0f,  0.0f),
         new Vec4( 1.0f, -1.0f,  1.0f,  0.0f),
         new Vec4( 1.0f, -1.0f, -1.0f,  0.0f),
         new Vec4(-1.0f,  1.0f,  1.0f,  0.0f),
         new Vec4(-1.0f,  1.0f, -1.0f,  0.0f),
         new Vec4(-1.0f, -1.0f,  1.0f,  0.0f),
         new Vec4(-1.0f, -1.0f, -1.0f,  0.0f) };

      GRAD3_U = new Vec3[] {
         new Vec3( 1.0f,  0.0f,  1.0f),
         new Vec3( 0.0f,  1.0f,  1.0f),
         new Vec3(-1.0f,  0.0f,  1.0f),
         new Vec3( 0.0f, -1.0f,  1.0f),
         new Vec3( 1.0f,  0.0f, -1.0f),
         new Vec3( 0.0f,  1.0f, -1.0f),
         new Vec3(-1.0f,  0.0f, -1.0f),
         new Vec3( 0.0f, -1.0f, -1.0f),
         new Vec3( Simplex.RT2_RT3,  Simplex.RT2_RT3,  Simplex.RT2_RT3),
         new Vec3(-Simplex.RT2_RT3,  Simplex.RT2_RT3, -Simplex.RT2_RT3),
         new Vec3(-Simplex.RT2_RT3, -Simplex.RT2_RT3,  Simplex.RT2_RT3),
         new Vec3( Simplex.RT2_RT3, -Simplex.RT2_RT3, -Simplex.RT2_RT3),
         new Vec3(-Simplex.RT2_RT3,  Simplex.RT2_RT3,  Simplex.RT2_RT3),
         new Vec3( Simplex.RT2_RT3, -Simplex.RT2_RT3,  Simplex.RT2_RT3),
         new Vec3( Simplex.RT2_RT3, -Simplex.RT2_RT3, -Simplex.RT2_RT3),
         new Vec3(-Simplex.RT2_RT3,  Simplex.RT2_RT3, -Simplex.RT2_RT3) };

      GRAD3_V = new Vec3[] {
         new Vec3(-Simplex.RT2_RT3,  Simplex.RT2_RT3,  Simplex.RT2_RT3),
         new Vec3(-Simplex.RT2_RT3, -Simplex.RT2_RT3,  Simplex.RT2_RT3),
         new Vec3( Simplex.RT2_RT3, -Simplex.RT2_RT3,  Simplex.RT2_RT3),
         new Vec3( Simplex.RT2_RT3,  Simplex.RT2_RT3,  Simplex.RT2_RT3),
         new Vec3(-Simplex.RT2_RT3, -Simplex.RT2_RT3, -Simplex.RT2_RT3),
         new Vec3( Simplex.RT2_RT3, -Simplex.RT2_RT3, -Simplex.RT2_RT3),
         new Vec3( Simplex.RT2_RT3,  Simplex.RT2_RT3, -Simplex.RT2_RT3),
         new Vec3(-Simplex.RT2_RT3,  Simplex.RT2_RT3, -Simplex.RT2_RT3),
         new Vec3( 1.0f, -1.0f,  0.0f),
         new Vec3( 1.0f,  1.0f,  0.0f),
         new Vec3(-1.0f,  1.0f,  0.0f),
         new Vec3(-1.0f, -1.0f,  0.0f),
         new Vec3( 1.0f,  0.0f,  1.0f),
         new Vec3(-1.0f,  0.0f,  1.0f),
         new Vec3( 0.0f,  1.0f, -1.0f),
         new Vec3( 0.0f, -1.0f, -1.0f) };

      PERMUTE = new int[][] {
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

      ROT_2 = new Vec2();
      ROT_3 = new Vec3();

      ZERO_2 = new Vec2();
      ZERO_3 = new Vec3();
      ZERO_4 = new Vec4();
      /* @formatter:on */
   }

   /**
    * Evaluates 4D simplex noise for a given seed.
    *
    * @param x    the x coordinate
    * @param y    the y coordinate
    * @param z    the z coordinate
    * @param w    the w coordinate
    * @param seed the seed
    *
    * @return the noise value
    */
   public static float eval ( final float x, final float y, final float z,
      final float w, final int seed ) {

      return Simplex.eval(x, y, z, w, seed, null);
   }

   /**
    * Evaluates 4D simplex noise for a given seed. Calculates the derivative
    * if the output variable is not null.
    *
    * @param x     the x coordinate
    * @param y     the y coordinate
    * @param z     the z coordinate
    * @param w     the w coordinate
    * @param seed  the seed
    * @param deriv the derivative
    *
    * @return the noise value
    *
    * @see Simplex#F4
    * @see Simplex#G4
    * @see Simplex#PERMUTE
    * @see Simplex#SCALE_4
    * @see Simplex#gradient4(int, int, int, int, int)
    */
   public static float eval ( final float x, final float y, final float z,
      final float w, final int seed, final Vec4 deriv ) {

      final float s = ( x + y + z + w ) * Simplex.F4;

      final float xs = x + s;
      final int xstrunc = ( int ) xs;
      final int i = xs < xstrunc ? xstrunc - 1 : xstrunc;

      final float ys = y + s;
      final int ystrunc = ( int ) ys;
      final int j = ys < ystrunc ? ystrunc - 1 : ystrunc;

      final float zs = z + s;
      final int zstrunc = ( int ) zs;
      final int k = zs < zstrunc ? zstrunc - 1 : zstrunc;

      final float ws = w + s;
      final int wstrunc = ( int ) ws;
      final int l = ws < wstrunc ? wstrunc - 1 : wstrunc;

      final float t = ( i + j + k + l ) * Simplex.G4;
      final float x0 = x - ( i - t );
      final float y0 = y - ( j - t );
      final float z0 = z - ( k - t );
      final float w0 = w - ( l - t );

      final int[] sc = Simplex.PERMUTE[ ( x0 > y0 ? 0x20 : 0 ) | ( x0 > z0
         ? 0x10 : 0 ) | ( y0 > z0 ? 0x8 : 0 ) | ( x0 > w0 ? 0x4 : 0 ) | ( y0
            > w0 ? 0x2 : 0 ) | ( z0 > w0 ? 0x1 : 0 )];
      final int sc0 = sc[0];
      final int sc1 = sc[1];
      final int sc2 = sc[2];
      final int sc3 = sc[3];

      /* These are integers because they are supplied to gradient4. */
      final int i1 = sc0 > 2 ? 1 : 0;
      final int j1 = sc1 > 2 ? 1 : 0;
      final int k1 = sc2 > 2 ? 1 : 0;
      final int l1 = sc3 > 2 ? 1 : 0;

      final int i2 = sc0 > 1 ? 1 : 0;
      final int j2 = sc1 > 1 ? 1 : 0;
      final int k2 = sc2 > 1 ? 1 : 0;
      final int l2 = sc3 > 1 ? 1 : 0;

      final int i3 = sc0 > 0 ? 1 : 0;
      final int j3 = sc1 > 0 ? 1 : 0;
      final int k3 = sc2 > 0 ? 1 : 0;
      final int l3 = sc3 > 0 ? 1 : 0;

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

      final float x4 = x0 - Simplex.N4_4_1;
      final float y4 = y0 - Simplex.N4_4_1;
      final float z4 = z0 - Simplex.N4_4_1;
      final float w4 = w0 - Simplex.N4_4_1;

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

      final float t0 = 0.5f - ( x0 * x0 + y0 * y0 + z0 * z0 + w0 * w0 );
      if ( t0 >= 0.0f ) {
         t20 = t0 * t0;
         t40 = t20 * t20;
         g0 = Simplex.gradient4(i, j, k, l, seed);
         n0 = g0.x * x0 + g0.y * y0 + g0.z * z0 + g0.w * w0;
      }

      final float t1 = 0.5f - ( x1 * x1 + y1 * y1 + z1 * z1 + w1 * w1 );
      if ( t1 >= 0.0f ) {
         t21 = t1 * t1;
         t41 = t21 * t21;
         g1 = Simplex.gradient4(i + i1, j + j1, k + k1, l + l1, seed);
         n1 = g1.x * x1 + g1.y * y1 + g1.z * z1 + g1.w * w1;
      }

      final float t2 = 0.5f - ( x2 * x2 + y2 * y2 + z2 * z2 + w2 * w2 );
      if ( t2 >= 0.0f ) {
         t22 = t2 * t2;
         t42 = t22 * t22;
         g2 = Simplex.gradient4(i + i2, j + j2, k + k2, l + l2, seed);
         n2 = g2.x * x2 + g2.y * y2 + g2.z * z2 + g2.w * w2;
      }

      final float t3 = 0.5f - ( x3 * x3 + y3 * y3 + z3 * z3 + w3 * w3 );
      if ( t3 >= 0.0f ) {
         t23 = t3 * t3;
         t43 = t23 * t23;
         g3 = Simplex.gradient4(i + i3, j + j3, k + k3, l + l3, seed);
         n3 = g3.x * x3 + g3.y * y3 + g3.z * z3 + g3.w * w3;
      }

      final float t4 = 0.5f - ( x4 * x4 + y4 * y4 + z4 * z4 + w4 * w4 );
      if ( t4 >= 0.0f ) {
         t24 = t4 * t4;
         t44 = t24 * t24;
         g4 = Simplex.gradient4(i + 1, j + 1, k + 1, l + 1, seed);
         n4 = g4.x * x4 + g4.y * y4 + g4.z * z4 + g4.w * w4;
      }

      if ( deriv != null ) {

         final float tmp0 = t20 * t0 * n0;
         final float tmp1 = t21 * t1 * n1;
         final float tmp2 = t22 * t2 * n2;
         final float tmp3 = t23 * t3 * n3;
         final float tmp4 = t24 * t4 * n4;

         deriv.x = -8.0f * ( tmp0 * x0 + tmp1 * x1 + tmp2 * x2 + tmp3 * x3
            + tmp4 * x4 );
         deriv.y = -8.0f * ( tmp0 * y0 + tmp1 * y1 + tmp2 * y2 + tmp3 * y3
            + tmp4 * y4 );
         deriv.z = -8.0f * ( tmp0 * z0 + tmp1 * z1 + tmp2 * z2 + tmp3 * z3
            + tmp4 * z4 );
         deriv.w = -8.0f * ( tmp0 * w0 + tmp1 * w1 + tmp2 * w2 + tmp3 * w3
            + tmp4 * w4 );

         deriv.x += t40 * g0.x + t41 * g1.x + t42 * g2.x + t43 * g3.x + t44
            * g4.x;
         deriv.y += t40 * g0.y + t41 * g1.y + t42 * g2.y + t43 * g3.y + t44
            * g4.y;
         deriv.z += t40 * g0.z + t41 * g1.z + t42 * g2.z + t43 * g3.z + t44
            * g4.z;
         deriv.w += t40 * g0.w + t41 * g1.w + t42 * g2.w + t43 * g3.w + t44
            * g4.w;

         deriv.x *= Simplex.SCALE_4;
         deriv.y *= Simplex.SCALE_4;
         deriv.z *= Simplex.SCALE_4;
         deriv.w *= Simplex.SCALE_4;
      }

      return Simplex.SCALE_4 * ( t40 * n0 + t41 * n1 + t42 * n2 + t43 * n3 + t44
         * n4 );
   }

   /**
    * Evaluates 3D simplex noise for a given seed.
    *
    * @param x    the x coordinate
    * @param y    the y coordinate
    * @param z    the z coordinate
    * @param seed the seed
    *
    * @return the noise value
    */
   public static float eval ( final float x, final float y, final float z,
      final int seed ) {

      return Simplex.eval(x, y, z, seed, null);
   }

   /**
    * Evaluates 3D simplex noise for a given seed. Calculates the derivative
    * if the output variable is not null.
    *
    * @param x     the x coordinate
    * @param y     the y coordinate
    * @param z     the z coordinate
    * @param seed  the seed
    * @param deriv the derivative
    *
    * @return the noise value
    *
    * @see Simplex#F3
    * @see Simplex#G3
    * @see Simplex#SCALE_3
    * @see Simplex#gradient3(int, int, int, int)
    */
   public static float eval ( final float x, final float y, final float z,
      final int seed, final Vec3 deriv ) {

      final float s = ( x + y + z ) * Simplex.F3;

      final float xs = x + s;
      final int xstrunc = ( int ) xs;
      final int i = xs < xstrunc ? xstrunc - 1 : xstrunc;

      final float ys = y + s;
      final int ystrunc = ( int ) ys;
      final int j = ys < ystrunc ? ystrunc - 1 : ystrunc;

      final float zs = z + s;
      final int zstrunc = ( int ) zs;
      final int k = zs < zstrunc ? zstrunc - 1 : zstrunc;

      final float t = ( i + j + k ) * Simplex.G3;
      final float x0 = x - ( i - t );
      final float y0 = y - ( j - t );
      final float z0 = z - ( k - t );

      byte i1 = 0;
      byte j1 = 0;
      byte k1 = 0;

      byte i2 = 0;
      byte j2 = 0;
      byte k2 = 0;

      if ( x0 >= y0 ) {
         if ( y0 >= z0 ) {
            i1 = 1;
            i2 = 1;
            j2 = 1;
         } else if ( x0 >= z0 ) {
            i1 = 1;
            i2 = 1;
            k2 = 1;
         } else {
            k1 = 1;
            i2 = 1;
            k2 = 1;
         }
      } else {
         if ( y0 < z0 ) {
            k1 = 1;
            j2 = 1;
            k2 = 1;
         } else if ( x0 < z0 ) {
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

      final float x3 = x0 - Simplex.N3_3_1;
      final float y3 = y0 - Simplex.N3_3_1;
      final float z3 = z0 - Simplex.N3_3_1;

      float n0 = 0.0f;
      float t20 = 0.0f;
      float t40 = 0.0f;
      Vec3 g0 = Simplex.ZERO_3;
      final float t0 = 0.5f - ( x0 * x0 + y0 * y0 + z0 * z0 );
      if ( t0 >= 0.0f ) {
         g0 = Simplex.gradient3(i, j, k, seed);
         t20 = t0 * t0;
         t40 = t20 * t20;
         n0 = g0.x * x0 + g0.y * y0 + g0.z * z0;
      }

      float n1 = 0.0f;
      float t21 = 0.0f;
      float t41 = 0.0f;
      Vec3 g1 = Simplex.ZERO_3;
      final float t1 = 0.5f - ( x1 * x1 + y1 * y1 + z1 * z1 );
      if ( t1 >= 0.0f ) {
         g1 = Simplex.gradient3(i + i1, j + j1, k + k1, seed);
         t21 = t1 * t1;
         t41 = t21 * t21;
         n1 = g1.x * x1 + g1.y * y1 + g1.z * z1;
      }

      float n2 = 0.0f;
      float t22 = 0.0f;
      float t42 = 0.0f;
      Vec3 g2 = Simplex.ZERO_3;
      final float t2 = 0.5f - ( x2 * x2 + y2 * y2 + z2 * z2 );
      if ( t2 >= 0.0f ) {
         g2 = Simplex.gradient3(i + i2, j + j2, k + k2, seed);
         t22 = t2 * t2;
         t42 = t22 * t22;
         n2 = g2.x * x2 + g2.y * y2 + g2.z * z2;
      }

      float n3 = 0.0f;
      float t23 = 0.0f;
      float t43 = 0.0f;
      Vec3 g3 = Simplex.ZERO_3;
      final float t3 = 0.5f - ( x3 * x3 + y3 * y3 + z3 * z3 );
      if ( t3 >= 0.0f ) {
         g3 = Simplex.gradient3(i + 1, j + 1, k + 1, seed);
         t23 = t3 * t3;
         t43 = t23 * t23;
         n3 = g3.x * x3 + g3.y * y3 + g3.z * z3;
      }

      if ( deriv != null ) {
         final float tmp0 = t20 * t0 * n0;
         final float tmp1 = t21 * t1 * n1;
         final float tmp2 = t22 * t2 * n2;
         final float tmp3 = t23 * t3 * n3;

         deriv.x = -8.0f * ( tmp0 * x0 + tmp1 * x1 + tmp2 * x2 + tmp3 * x3 );
         deriv.y = -8.0f * ( tmp0 * y0 + tmp1 * y1 + tmp2 * y2 + tmp3 * y3 );
         deriv.z = -8.0f * ( tmp0 * z0 + tmp1 * z1 + tmp2 * z2 + tmp3 * z3 );

         deriv.x += t40 * g0.x + t41 * g1.x + t42 * g2.x + t43 * g3.x;
         deriv.y += t40 * g0.y + t41 * g1.y + t42 * g2.y + t43 * g3.y;
         deriv.z += t40 * g0.z + t41 * g1.z + t42 * g2.z + t43 * g3.z;

         deriv.x *= Simplex.SCALE_3;
         deriv.y *= Simplex.SCALE_3;
         deriv.z *= Simplex.SCALE_3;
      }

      return Simplex.SCALE_3 * ( t40 * n0 + t41 * n1 + t42 * n2 + t43 * n3 );
   }

   /**
    * Evaluates 2D simplex noise for a given seed.
    *
    * @param x    the x coordinate
    * @param y    the y coordinate
    * @param seed the seed
    *
    * @return the noise value
    */
   public static float eval ( final float x, final float y, final int seed ) {

      return Simplex.eval(x, y, seed, null);
   }

   /**
    * Evaluates 2D simplex noise for a given seed. Calculates the derivative
    * if the output variable is not null.
    *
    * @param x     the x coordinate
    * @param y     the y coordinate
    * @param seed  the seed
    * @param deriv the derivative
    *
    * @return the noise value
    *
    * @see Simplex#F2
    * @see Simplex#G2
    * @see Simplex#SCALE_2
    * @see Simplex#gradient2(int, int, int)
    */
   public static float eval ( final float x, final float y, final int seed,
      final Vec2 deriv ) {

      final float s = ( x + y ) * Simplex.F2;

      final float xs = x + s;
      final int xstrunc = ( int ) xs;
      final int i = xs < xstrunc ? xstrunc - 1 : xstrunc;

      final float ys = y + s;
      final int ystrunc = ( int ) ys;
      final int j = ys < ystrunc ? ystrunc - 1 : ystrunc;

      final float t = ( i + j ) * Simplex.G2;
      final float x0 = x - ( i - t );
      final float y0 = y - ( j - t );

      byte i1 = 0;
      byte j1 = 0;
      if ( x0 > y0 ) {
         i1 = 1;
      } else {
         j1 = 1;
      }

      float n0 = 0.0f;
      float t20 = 0.0f;
      float t40 = 0.0f;
      Vec2 g0 = Simplex.ZERO_2;
      final float t0 = 0.5f - ( x0 * x0 + y0 * y0 );
      if ( t0 >= 0.0f ) {
         g0 = Simplex.gradient2(i, j, seed);
         t20 = t0 * t0;
         t40 = t20 * t20;
         n0 = g0.x * x0 + g0.y * y0;
      }

      float n1 = 0.0f;
      float t21 = 0.0f;
      float t41 = 0.0f;
      Vec2 g1 = Simplex.ZERO_2;
      final float x1 = x0 - i1 + Simplex.G2;
      final float y1 = y0 - j1 + Simplex.G2;
      final float t1 = 0.5f - ( x1 * x1 + y1 * y1 );
      if ( t1 >= 0.0f ) {
         g1 = Simplex.gradient2(i + i1, j + j1, seed);
         t21 = t1 * t1;
         t41 = t21 * t21;
         n1 = g1.x * x1 + g1.y * y1;
      }

      float n2 = 0.0f;
      float t22 = 0.0f;
      float t42 = 0.0f;
      Vec2 g2 = Simplex.ZERO_2;
      final float x2 = x0 - Simplex.N2_2_1;
      final float y2 = y0 - Simplex.N2_2_1;
      final float t2 = 0.5f - ( x2 * x2 + y2 * y2 );
      if ( t2 >= 0.0f ) {
         g2 = Simplex.gradient2(i + 1, j + 1, seed);
         t22 = t2 * t2;
         t42 = t22 * t22;
         n2 = g2.x * x2 + g2.y * y2;
      }

      if ( deriv != null ) {

         final float tmp0 = t20 * t0 * n0;
         final float tmp1 = t21 * t1 * n1;
         final float tmp2 = t22 * t2 * n2;

         deriv.x = -8.0f * ( tmp0 * x0 + tmp1 * x1 + tmp2 * x2 );
         deriv.y = -8.0f * ( tmp0 * y0 + tmp1 * y1 + tmp2 * y2 );

         deriv.x += t40 * g0.x + t41 * g1.x + t42 * g2.x;
         deriv.y += t40 * g0.y + t41 * g1.y + t42 * g2.y;

         deriv.x *= Simplex.SCALE_2;
         deriv.y *= Simplex.SCALE_2;
      }

      return Simplex.SCALE_2 * ( t40 * n0 + t41 * n1 + t42 * n2 );
   }

   /**
    * Evaluates 2D simplex noise for a given seed.
    *
    * @param v    the input vector
    * @param seed the seed
    *
    * @return the noise value
    */
   public static float eval ( final Vec2 v, final int seed ) {

      return Simplex.eval(v.x, v.y, seed, null);
   }

   /**
    * Evaluates 2D simplex noise for a given seed. Calculates the derivative
    * if the output variable is not null.
    *
    * @param v     the input vector
    * @param seed  the seed
    * @param deriv the derivative
    *
    * @return the noise value
    */
   public static float eval ( final Vec2 v, final int seed, final Vec2 deriv ) {

      return Simplex.eval(v.x, v.y, seed, deriv);
   }

   /**
    * Evaluates 3D simplex noise for a given seed.
    *
    * @param v    the input vector
    * @param seed the seed
    *
    * @return the noise value
    */
   public static float eval ( final Vec3 v, final int seed ) {

      return Simplex.eval(v.x, v.y, v.z, seed, null);
   }

   /**
    * Evaluates 3D simplex noise for a given seed. Calculates the derivative
    * if the output variable is not null.
    *
    * @param v     the input vector
    * @param seed  the seed
    * @param deriv the derivative
    *
    * @return the noise value
    */
   public static float eval ( final Vec3 v, final int seed, final Vec3 deriv ) {

      return Simplex.eval(v.x, v.y, v.z, seed, deriv);
   }

   /**
    * Evaluates 4D simplex noise for a given seed.
    *
    * @param v    the input vector
    * @param seed the seed
    *
    * @return the noise value
    */
   public static float eval ( final Vec4 v, final int seed ) {

      return Simplex.eval(v.x, v.y, v.z, v.w, seed, null);
   }

   /**
    * Evaluates 4D simplex noise for a given seed. Calculates the derivative
    * if the output variable is not null.
    *
    * @param v     the input vector
    * @param seed  the seed
    * @param deriv the derivative
    *
    * @return the noise value
    */
   public static float eval ( final Vec4 v, final int seed, final Vec4 deriv ) {

      return Simplex.eval(v.x, v.y, v.z, v.w, seed, deriv);
   }

   /**
    * Fractal Brownian Motion. For a given number of octaves, sums the output
    * value of a noise function. Per each iteration, the output is multiplied
    * by the amplitude; amplitude is multiplied by gain; frequency is
    * multiplied by lacunarity.
    *
    * @param v          the input coordinate
    * @param seed       the seed
    * @param octaves    the number of iterations
    * @param lacunarity the lacunarity
    * @param gain       the gain
    *
    * @return the result
    */
   public static float fbm ( final Vec2 v, final int seed, final int octaves,
      final float lacunarity, final float gain ) {

      return Simplex.fbm(v, seed, octaves, lacunarity, gain, null);
   }

   /**
    * Fractal Brownian Motion. For a given number of octaves, sums the output
    * value of a noise function. Per each iteration, the output is multiplied
    * by the amplitude; amplitude is multiplied by gain; frequency is
    * multiplied by lacunarity.
    *
    * @param v          the input coordinate
    * @param seed       the seed
    * @param octaves    the number of iterations
    * @param lacunarity the lacunarity
    * @param gain       the gain
    * @param deriv      the derivative
    *
    * @return the result
    */
   public static float fbm ( final Vec2 v, final int seed, final int octaves,
      final float lacunarity, final float gain, final Vec2 deriv ) {

      float freq = 1.0f;
      float amp = 0.5f;
      float vinx;
      float viny;
      float sum = 0.0f;

      final boolean calcDeriv = deriv != null;
      final Vec2 nxy = new Vec2();

      if ( calcDeriv ) { deriv.set(0.0f, 0.0f); }

      for ( int i = 0; i < octaves; ++i ) {
         vinx = v.x * freq;
         viny = v.y * freq;

         sum += Simplex.eval(vinx, viny, seed, nxy) * amp;

         nxy.x *= amp;
         nxy.y *= amp;

         if ( calcDeriv ) {
            deriv.x += nxy.x;
            deriv.y += nxy.y;
         }

         freq *= lacunarity;
         amp *= gain;
      }

      return sum;
   }

   /**
    * Fractal Brownian Motion. For a given number of octaves, sums the output
    * value of a noise function. Per each iteration, the output is multiplied
    * by the amplitude; amplitude is multiplied by gain; frequency is
    * multiplied by lacunarity.
    *
    * @param v          the input coordinate
    * @param seed       the seed
    * @param octaves    the number of iterations
    * @param lacunarity the lacunarity
    * @param gain       the gain
    *
    * @return the result
    */
   public static float fbm ( final Vec3 v, final int seed, final int octaves,
      final float lacunarity, final float gain ) {

      return Simplex.fbm(v, seed, octaves, lacunarity, gain, null);
   }

   /**
    * Fractal Brownian Motion. For a given number of octaves, sums the output
    * value of a noise function. Per each iteration, the output is multiplied
    * by the amplitude; amplitude is multiplied by gain; frequency is
    * multiplied by lacunarity.
    *
    * @param v          the input coordinate
    * @param seed       the seed
    * @param octaves    the number of iterations
    * @param lacunarity the lacunarity
    * @param gain       the gain
    * @param deriv      the derivative
    *
    * @return the result
    */
   public static float fbm ( final Vec3 v, final int seed, final int octaves,
      final float lacunarity, final float gain, final Vec3 deriv ) {

      float freq = 1.0f;
      float amp = 0.5f;
      float vinx;
      float viny;
      float vinz;
      float sum = 0.0f;

      final Vec3 nxyz = new Vec3();
      final boolean calcDeriv = deriv != null;

      if ( calcDeriv ) { deriv.set(0.0f, 0.0f, 0.0f); }

      for ( int i = 0; i < octaves; ++i ) {
         vinx = v.x * freq;
         viny = v.y * freq;
         vinz = v.z * freq;

         sum += Simplex.eval(vinx, viny, vinz, seed, nxyz) * amp;

         nxyz.x *= amp;
         nxyz.y *= amp;
         nxyz.z *= amp;

         if ( calcDeriv ) {
            deriv.x += nxyz.x;
            deriv.y += nxyz.y;
            deriv.z += nxyz.z;
         }

         freq *= lacunarity;
         amp *= gain;
      }

      return sum;
   }

   /**
    * Fractal Brownian Motion. For a given number of octaves, sums the output
    * value of a noise function. Per each iteration, the output is multiplied
    * by the amplitude; amplitude is multiplied by gain; frequency is
    * multiplied by lacunarity.
    *
    * @param v          the input coordinate
    * @param seed       the seed
    * @param octaves    the number of iterations
    * @param lacunarity the lacunarity
    * @param gain       the gain
    *
    * @return the result
    */
   public static float fbm ( final Vec4 v, final int seed, final int octaves,
      final float lacunarity, final float gain ) {

      return Simplex.fbm(v, seed, octaves, lacunarity, gain, null);
   }

   /**
    * Fractal Brownian Motion. For a given number of octaves, sums the output
    * value of a noise function. Per each iteration, the output is multiplied
    * by the amplitude; amplitude is multiplied by gain; frequency is
    * multiplied by lacunarity.
    *
    * @param v          the input coordinate
    * @param seed       the seed
    * @param octaves    the number of iterations
    * @param lacunarity the lacunarity
    * @param gain       the gain
    * @param deriv      the derivative
    *
    * @return the result
    */
   public static float fbm ( final Vec4 v, final int seed, final int octaves,
      final float lacunarity, final float gain, final Vec4 deriv ) {

      float freq = 1.0f;
      float amp = 0.5f;
      float vinx;
      float viny;
      float vinz;
      float vinw;
      float sum = 0.0f;

      final Vec4 nxyzw = new Vec4();
      final boolean calcDeriv = deriv != null;

      if ( calcDeriv ) { deriv.set(0.0f, 0.0f, 0.0f, 0.0f); }

      for ( int i = 0; i < octaves; ++i ) {
         vinx = v.x * freq;
         viny = v.y * freq;
         vinz = v.z * freq;
         vinw = v.w * freq;

         sum += Simplex.eval(vinx, viny, vinz, vinw, seed, nxyzw) * amp;

         nxyzw.x *= amp;
         nxyzw.y *= amp;
         nxyzw.z *= amp;
         nxyzw.w *= amp;

         if ( calcDeriv ) {
            deriv.x += nxyzw.x;
            deriv.y += nxyzw.y;
            deriv.z += nxyzw.z;
            deriv.w += nxyzw.w;
         }

         freq *= lacunarity;
         amp *= gain;
      }

      return sum;
   }

   /**
    * Generates 3D flow noise with three coordinates and the sine and cosine
    * of an angle.
    *
    * @param x    the x coordinate
    * @param y    the y coordinate
    * @param z    the z coordinate
    * @param cosa the cosine of the angle
    * @param sina the sine of the angle
    * @param seed the seed
    *
    * @return the flow noise value
    */
   public static float flow ( final float x, final float y, final float z,
      final float cosa, final float sina, final int seed ) {

      return Simplex.flow(x, y, z, cosa, sina, seed, null);
   }

   /**
    * Generates 3D flow noise with three coordinates and the sine and cosine
    * of an angle. Calculates the derivative if it is not null.
    *
    * @param x     the x coordinate
    * @param y     the y coordinate
    * @param z     the z coordinate
    * @param cosa  the cosine of the angle
    * @param sina  the sine of the angle
    * @param seed  the seed
    * @param deriv the derivative
    *
    * @return the flow noise value
    *
    * @author Simon Geilfus
    */
   public static float flow ( final float x, final float y, final float z,
      final float cosa, final float sina, final int seed, final Vec3 deriv ) {

      final float s = ( x + y + z ) * Simplex.F3;

      final float xs = x + s;
      final int xstrunc = ( int ) xs;
      final int i = xs < xstrunc ? xstrunc - 1 : xstrunc;

      final float ys = y + s;
      final int ystrunc = ( int ) ys;
      final int j = ys < ystrunc ? ystrunc - 1 : ystrunc;

      final float zs = z + s;
      final int zstrunc = ( int ) zs;
      final int k = zs < zstrunc ? zstrunc - 1 : zstrunc;

      final float t = ( i + j + k ) * Simplex.G3;
      final float x0 = x - ( i - t );
      final float y0 = y - ( j - t );
      final float z0 = z - ( k - t );

      byte i1 = 0;
      byte j1 = 0;
      byte k1 = 0;

      byte i2 = 0;
      byte j2 = 0;
      byte k2 = 0;

      if ( x0 >= y0 ) {
         if ( y0 >= z0 ) {
            i1 = 1;
            i2 = 1;
            j2 = 1;
         } else if ( x0 >= z0 ) {
            i1 = 1;
            i2 = 1;
            k2 = 1;
         } else {
            k1 = 1;
            i2 = 1;
            k2 = 1;
         }
      } else {
         if ( y0 < z0 ) {
            k1 = 1;
            j2 = 1;
            k2 = 1;
         } else if ( x0 < z0 ) {
            j1 = 1;
            j2 = 1;
            k2 = 1;
         } else {
            j1 = 1;
            i2 = 1;
            j2 = 1;
         }
      }

      float n0 = 0.0f;
      float t20 = 0.0f;
      float t40 = 0.0f;
      Vec3 g0 = Simplex.ZERO_3;
      final float t0 = 0.5f - ( x0 * x0 + y0 * y0 + z0 * z0 );
      if ( t0 >= 0.0f ) {
         g0 = Simplex.gradRot3(i, j, k, seed, cosa, sina, Simplex.ROT_3);
         t20 = t0 * t0;
         t40 = t20 * t20;
         n0 = g0.x * x0 + g0.y * y0 + g0.z * z0;
      }

      float n1 = 0.0f;
      float t21 = 0.0f;
      float t41 = 0.0f;
      final float x1 = x0 - i1 + Simplex.G3;
      final float y1 = y0 - j1 + Simplex.G3;
      final float z1 = z0 - k1 + Simplex.G3;
      Vec3 g1 = Simplex.ZERO_3;
      final float t1 = 0.5f - ( x1 * x1 + y1 * y1 + z1 * z1 );
      if ( t1 >= 0.0f ) {
         g1 = Simplex.gradRot3(i + i1, j + j1, k + k1, seed, cosa, sina,
            Simplex.ROT_3);
         t21 = t1 * t1;
         t41 = t21 * t21;
         n1 = g1.x * x1 + g1.y * y1 + g1.z * z1;
      }

      float n2 = 0.0f;
      float t22 = 0.0f;
      float t42 = 0.0f;
      Vec3 g2 = Simplex.ZERO_3;
      final float x2 = x0 - i2 + Simplex.G3_2;
      final float y2 = y0 - j2 + Simplex.G3_2;
      final float z2 = z0 - k2 + Simplex.G3_2;
      final float t2 = 0.5f - ( x2 * x2 + y2 * y2 + z2 * z2 );
      if ( t2 >= 0.0f ) {
         g2 = Simplex.gradRot3(i + i2, j + j2, k + k2, seed, cosa, sina,
            Simplex.ROT_3);
         t22 = t2 * t2;
         t42 = t22 * t22;
         n2 = g2.x * x2 + g2.y * y2 + g2.z * z2;
      }

      float n3 = 0.0f;
      float t23 = 0.0f;
      float t43 = 0.0f;
      Vec3 g3 = Simplex.ZERO_3;
      final float x3 = x0 - Simplex.N3_3_1;
      final float y3 = y0 - Simplex.N3_3_1;
      final float z3 = z0 - Simplex.N3_3_1;
      final float t3 = 0.5f - ( x3 * x3 + y3 * y3 + z3 * z3 );
      if ( t3 >= 0.0f ) {
         g3 = Simplex.gradRot3(i + 1, j + 1, k + 1, seed, cosa, sina,
            Simplex.ROT_3);
         t23 = t3 * t3;
         t43 = t23 * t23;
         n3 = g3.x * x3 + g3.y * y3 + g3.z * z3;
      }

      if ( deriv != null ) {
         final float tmp0 = t20 * t0 * n0;
         final float tmp1 = t21 * t1 * n1;
         final float tmp2 = t22 * t2 * n2;
         final float tmp3 = t23 * t3 * n3;

         deriv.x = -8.0f * ( tmp0 * x0 + tmp1 * x1 + tmp2 * x2 + tmp3 * x3 );
         deriv.y = -8.0f * ( tmp0 * y0 + tmp1 * y1 + tmp2 * y2 + tmp3 * y3 );
         deriv.z = -8.0f * ( tmp0 * z0 + tmp1 * z1 + tmp2 * z2 + tmp3 * z3 );

         deriv.x += t40 * g0.x + t41 * g1.x + t42 * g2.x + t43 * g3.x;
         deriv.y += t40 * g0.y + t41 * g1.y + t42 * g2.y + t43 * g3.y;
         deriv.z += t40 * g0.z + t41 * g1.z + t42 * g2.z + t43 * g3.z;

         deriv.x *= Simplex.SCALE_3;
         deriv.y *= Simplex.SCALE_3;
         deriv.z *= Simplex.SCALE_3;
      }

      return Simplex.SCALE_3 * ( t40 * n0 + t41 * n1 + t42 * n2 + t43 * n3 );
   }

   /**
    * Generates 3D flow noise with three coordinates and an angle.
    *
    * @param x       the x coordinate
    * @param y       the y coordinate
    * @param z       the z coordinate
    * @param radians the angle
    * @param seed    the seed
    *
    * @return the flow noise value
    */
   public static float flow ( final float x, final float y, final float z,
      final float radians, final int seed ) {

      return Simplex.flow(x, y, z, radians, seed, ( Vec3 ) null);
   }

   /**
    * Generates 2D flow noise with two coordinates and the sine and cosine of
    * an angle. Calculates the derivative if it is not null.
    *
    * @param x     the x coordinate
    * @param y     the y coordinate
    * @param cosa  the cosine of the angle
    * @param sina  the sine of the angle
    * @param seed  the seed
    * @param deriv the derivative
    *
    * @return the flow noise value
    *
    * @author Simon Geilfus
    */
   public static float flow ( final float x, final float y, final float cosa,
      final float sina, final int seed, final Vec2 deriv ) {

      final float s = ( x + y ) * Simplex.F2;

      final float xs = x + s;
      final int xstrunc = ( int ) xs;
      final int i = xs < xstrunc ? xstrunc - 1 : xstrunc;

      final float ys = y + s;
      final int ystrunc = ( int ) ys;
      final int j = ys < ystrunc ? ystrunc - 1 : ystrunc;

      final float t = ( i + j ) * Simplex.G2;
      final float x0 = x - ( i - t );
      final float y0 = y - ( j - t );

      byte i1 = 0;
      byte j1 = 0;
      if ( x0 > y0 ) {
         i1 = 1;
      } else {
         j1 = 1;
      }

      float n0 = 0.0f;
      float t20 = 0.0f;
      float t40 = 0.0f;
      Vec2 g0 = Simplex.ZERO_2;
      final float t0 = 0.5f - ( x0 * x0 + y0 * y0 );
      if ( t0 >= 0.0f ) {
         g0 = Simplex.gradRot2(i, j, seed, cosa, sina, Simplex.ROT_2);
         t20 = t0 * t0;
         t40 = t20 * t20;
         n0 = g0.x * x0 + g0.y * y0;
      }

      float n1 = 0.0f;
      float t21 = 0.0f;
      float t41 = 0.0f;
      Vec2 g1 = Simplex.ZERO_2;
      final float x1 = x0 - i1 + Simplex.G2;
      final float y1 = y0 - j1 + Simplex.G2;
      final float t1 = 0.5f - ( x1 * x1 + y1 * y1 );
      if ( t1 >= 0.0f ) {
         g1 = Simplex.gradRot2(i + i1, j + j1, seed, cosa, sina, Simplex.ROT_2);
         t21 = t1 * t1;
         t41 = t21 * t21;
         n1 = g1.x * x1 + g1.y * y1;
      }

      float n2 = 0.0f;
      float t22 = 0.0f;
      float t42 = 0.0f;
      Vec2 g2 = Simplex.ZERO_2;
      final float x2 = x0 - Simplex.N2_2_1;
      final float y2 = y0 - Simplex.N2_2_1;
      final float t2 = 0.5f - ( x2 * x2 + y2 * y2 );
      if ( t2 >= 0.0f ) {
         g2 = Simplex.gradRot2(i + 1, j + 1, seed, cosa, sina, Simplex.ROT_2);
         t22 = t2 * t2;
         t42 = t22 * t22;
         n2 = g2.x * x2 + g2.y * y2;
      }

      if ( deriv != null ) {

         final float tmp0 = t20 * t0 * n0;
         final float tmp1 = t21 * t1 * n1;
         final float tmp2 = t22 * t2 * n2;

         deriv.x = -8.0f * ( tmp0 * x0 + tmp1 * x1 + tmp2 * x2 );
         deriv.y = -8.0f * ( tmp0 * y0 + tmp1 * y1 + tmp2 * y2 );

         deriv.x += t40 * g0.x + t41 * g1.x + t42 * g2.x;
         deriv.y += t40 * g0.y + t41 * g1.y + t42 * g2.y;

         deriv.x *= Simplex.SCALE_2;
         deriv.y *= Simplex.SCALE_2;
      }

      return Simplex.SCALE_2 * ( t40 * n0 + t41 * n1 + t42 * n2 );
   }

   /**
    * Generates 3D flow noise with three coordinates and the sine and cosine
    * of an angle. Calculates the derivative if it is not null.
    *
    * @param x       the x coordinate
    * @param y       the y coordinate
    * @param z       the z coordinate
    * @param radians the angle in radians
    * @param seed    the seed
    * @param deriv   the derivative
    *
    * @return the flow noise value
    */
   public static float flow ( final float x, final float y, final float z,
      final float radians, final int seed, final Vec3 deriv ) {

      return Simplex.flow(x, y, z, Utils.cos(radians), Utils.sin(radians), seed,
         deriv);
   }

   /**
    * Generates 3D flow noise with three coordinates and an angle.
    *
    * @param x       the x coordinate
    * @param y       the y coordinate
    * @param radians the angle in radians
    * @param seed    the seed
    *
    * @return the flow noise value
    */
   public static float flow ( final float x, final float y, final float radians,
      final int seed ) {

      return Simplex.flow(x, y, radians, seed, ( Vec2 ) null);
   }

   /**
    * Generates 3D flow noise with three coordinates and an angle. Calculates
    * the derivative if it is not null.
    *
    * @param x       the x coordinate
    * @param y       the y coordinate
    * @param radians the angle in radians
    * @param seed    the seed
    * @param deriv   the derivative
    *
    * @return the flow noise value
    */
   public static float flow ( final float x, final float y, final float radians,
      final int seed, final Vec2 deriv ) {

      return Simplex.flow(x, y, Utils.cos(radians), Utils.sin(radians), seed,
         deriv);
   }

   /**
    * Generates 2D flow noise with a coordinate and an angle.
    *
    * @param v       the coordinate
    * @param radians the angle in radians
    * @param seed    the seed
    *
    * @return the flow noise value
    */
   public static float flow ( final Vec2 v, final float radians,
      final int seed ) {

      return Simplex.flow(v.x, v.y, radians, seed, ( Vec2 ) null);
   }

   /**
    * Generates 2D flow noise with a coordinate and an angle. Calculates the
    * derivative if it is not null.
    *
    * @param v       the coordinate
    * @param radians the angle in radians
    * @param seed    the seed
    * @param deriv   the derivative
    *
    * @return the flow noise value
    */
   public static float flow ( final Vec2 v, final float radians, final int seed,
      final Vec2 deriv ) {

      return Simplex.flow(v.x, v.y, radians, seed, deriv);
   }

   /**
    * Generates 3D flow noise with a coordinate and an angle.
    *
    * @param v       the coordinate
    * @param radians the angle in radians
    * @param seed    the seed
    *
    * @return the flow noise value
    */
   public static float flow ( final Vec3 v, final float radians,
      final int seed ) {

      return Simplex.flow(v.x, v.y, v.z, radians, seed, ( Vec3 ) null);
   }

   /**
    * Generates 3D flow noise with a coordinate and an angle. Calculates the
    * derivative if it is not null.
    *
    * @param v       the coordinate
    * @param radians the angle in radians
    * @param seed    the seed
    * @param deriv   the derivative
    *
    * @return the flow noise value
    */
   public static float flow ( final Vec3 v, final float radians, final int seed,
      final Vec3 deriv ) {

      return Simplex.flow(v.x, v.y, v.z, radians, seed, deriv);
   }

   /**
    * Returns a value with the same number of dimensions as the input, 2. This
    * is done by calling {@link Simplex#eval(float, float, int, Vec2)} twice,
    * with offset steps added to each component of the input vector.
    *
    * @param v      the input vector
    * @param seed   the seed
    * @param target the output vector
    *
    * @return the noise value
    */
   public static Vec2 noise ( final Vec2 v, final int seed,
      final Vec2 target ) {

      return Simplex.noise(v, seed, target, null, null);
   }

   /**
    * Returns a value with the same number of dimensions as the input, 2. This
    * is done by calling {@link Simplex#eval(float, float, int, Vec2)} twice,
    * with offset steps added to each component of the input vector. The
    * derivatives are calculated for the output vectors.
    *
    * @param v      the input vector
    * @param seed   the seed
    * @param target the output vector
    * @param xDeriv the derivative for the x evaluation
    * @param yDeriv the derivative for the y evaluation
    *
    * @return the noise value
    *
    * @see Vec2#mag(Vec2)
    * @see Simplex#STEP_2
    * @see Simplex#eval(float, float, int, Vec2)
    */
   public static Vec2 noise ( final Vec2 v, final int seed, final Vec2 target,
      final Vec2 xDeriv, final Vec2 yDeriv ) {

      /* @formatter:off */
      final float st = Simplex.STEP_2 * Utils.sqrtUnchecked(
         v.x * v.x + v.y * v.y);
      return target.set(
         Simplex.eval(v.x + st, v.y, seed, xDeriv),
         Simplex.eval(v.x, v.y + st, seed, yDeriv));
      /* @formatter:on */
   }

   /**
    * Returns a value with the same number of dimensions as the input, 3. This
    * is done by calling {@link Simplex#eval(float, float, float, int, Vec3)}
    * thrice, with offset steps added to each component of the input vector.
    *
    * @param v      the input vector
    * @param seed   the seed
    * @param target the output vector
    *
    * @return the noise value
    */
   public static Vec3 noise ( final Vec3 v, final int seed,
      final Vec3 target ) {

      return Simplex.noise(v, seed, target, null, null, null);
   }

   /**
    * Returns a value with the same number of dimensions as the input, 3. This
    * is done by calling {@link Simplex#eval(float, float, float, int, Vec3)}
    * thrice, with offset steps added to each component of the input vector.
    * The derivatives are calculated for the output vectors.
    *
    * @param v      the input vector
    * @param seed   the seed
    * @param target the output vector
    * @param xDeriv the derivative for the x evaluation
    * @param yDeriv the derivative for the y evaluation
    * @param zDeriv the derivative for the z evaluation
    *
    * @return the noise value
    *
    * @see Vec3#mag(Vec3)
    * @see Simplex#STEP_3
    */
   public static Vec3 noise ( final Vec3 v, final int seed, final Vec3 target,
      final Vec3 xDeriv, final Vec3 yDeriv, final Vec3 zDeriv ) {

      /* @formatter:off */
      final float st = Simplex.STEP_3 * Utils.sqrtUnchecked(
         v.x * v.x + v.y * v.y + v.z * v.z);
      return target.set(
         Simplex.eval(v.x + st, v.y, v.z, seed, xDeriv),
         Simplex.eval(v.x, v.y + st, v.z, seed, yDeriv),
         Simplex.eval(v.x, v.y, v.z + st, seed, zDeriv));
      /* @formatter:on */
   }

   /**
    * Returns a value with the same number of dimensions as the input, 4. This
    * is done by calling {@link Simplex#eval(float, float, float, float, int)}
    * four times, with offset steps added to each component of the input
    * vector.
    *
    * @param v      the input vector
    * @param seed   the seed
    * @param target the output vector
    *
    * @return the noise value
    */
   public static Vec4 noise ( final Vec4 v, final int seed,
      final Vec4 target ) {

      return Simplex.noise(v, seed, target, null, null, null, null);
   }

   /**
    * Returns a value with the same number of dimensions as the input, 4. This
    * is done by calling
    * {@link Simplex#eval(float, float, float, float, int, Vec4)} four times,
    * with offset steps added to each component of the input vector. The
    * derivatives are calculated for the output vectors.
    *
    * @param v      the input vector
    * @param seed   the seed
    * @param target the output vector
    * @param xDeriv the derivative for the x evaluation
    * @param yDeriv the derivative for the y evaluation
    * @param zDeriv the derivative for the z evaluation
    * @param wDeriv the derivative for the w evaluation
    *
    * @return the noise value
    *
    * @see Vec4#mag(Vec4)
    * @see Simplex#STEP_4
    * @see Simplex#eval(float, float, float, float, int, Vec4)
    */
   public static Vec4 noise ( final Vec4 v, final int seed, final Vec4 target,
      final Vec4 xDeriv, final Vec4 yDeriv, final Vec4 zDeriv,
      final Vec4 wDeriv ) {

      /* @formatter:off */
      final float st = Simplex.STEP_4 * Utils.sqrtUnchecked(
         v.x * v.x + v.y * v.y + v.z * v.z + v.w * v.w);
      return target.set(
         Simplex.eval(v.x + st, v.y, v.z, v.w, seed, xDeriv),
         Simplex.eval(v.x, v.y + st, v.z, v.w, seed, yDeriv),
         Simplex.eval(v.x, v.y, v.z + st, v.w, seed, zDeriv),
         Simplex.eval(v.x, v.y, v.z, v.w + st, seed, wDeriv));
      /* @formatter:on */
   }

   /**
    * Hashes the indices i and j with the seed, then returns a vector from the
    * look up table.
    *
    * @param i    the first index
    * @param j    the second index
    * @param seed the seed
    *
    * @return the vector
    *
    * @see Simplex#hash(int, int, int)
    * @see Simplex#GRAD_2_LUT
    */
   private static Vec2 gradient2 ( final int i, final int j, final int seed ) {

      return Simplex.GRAD_2_LUT[Generative.hash(i, j, seed) & 0x7];
   }

   /**
    * Hashes the indices i, j and k with the seed, then returns a vector from
    * the look up table.
    *
    * @param i    the first index
    * @param j    the second index
    * @param k    the third index
    * @param seed the seed
    *
    * @return the vector
    *
    * @see Simplex#hash(int, int, int)
    * @see Simplex#GRAD_3_LUT
    */
   private static Vec3 gradient3 ( final int i, final int j, final int k,
      final int seed ) {

      return Simplex.GRAD_3_LUT[Generative.hash(i, j, Generative.hash(k, seed,
         0)) & 0xf];
   }

   /**
    * Hashes the indices i, j, k and l with the seed, then returns a vector
    * from the look up table.
    *
    * @param i    the first index
    * @param j    the second index
    * @param k    the third index
    * @param l    the fourth index
    * @param seed the seed
    *
    * @return the vector
    *
    * @see Simplex#hash(int, int, int)
    * @see Simplex#GRAD_4_LUT
    */
   private static Vec4 gradient4 ( final int i, final int j, final int k,
      final int l, final int seed ) {

      return Simplex.GRAD_4_LUT[Generative.hash(i, j, Generative.hash(k, l,
         seed)) & 0x1f];
   }

   /**
    * Hashes the indices i and j with the seed, retrieves a vector from the
    * look-up table, then rotates it by the sine and cosine of an angle.
    *
    * @param i      the first index
    * @param j      the second index
    * @param seed   the seed
    * @param cosa   the cosine of the angle
    * @param sina   the sine of the angle
    * @param target the output vector
    *
    * @return the vector
    *
    * @see Vec2#rotateZ(Vec2, float, float, Vec2)
    * @see Simplex#GRAD_2_LUT
    * @see Simplex#hash(int, int, int)
    *
    * @author Simon Geilfus
    */
   private static Vec2 gradRot2 ( final int i, final int j, final int seed,
      final float cosa, final float sina, final Vec2 target ) {

      return Vec2.rotateZ(Simplex.GRAD_2_LUT[Generative.hash(i, j, seed) & 0x7],
         cosa, sina, target);
   }

   /**
    * Hashes the indices i, j and k with the seed. Retrieves two vectors from
    * rotation look-up tables, then multiplies them against the sine and
    * cosine of the angle.
    *
    * @param i      the first index
    * @param j      the second index
    * @param k      the third index
    * @param seed   the seed
    * @param cosa   the cosine of the angle
    * @param sina   the sine of the angle
    * @param target the output vector
    *
    * @return the vector
    *
    * @see Simplex#hash(int, int, int)
    * @see Simplex#GRAD3_U
    * @see Simplex#GRAD3_V
    *
    * @author Simon Geilfus
    */
   private static Vec3 gradRot3 ( final int i, final int j, final int k,
      final int seed, final float cosa, final float sina, final Vec3 target ) {

      final int h = Generative.hash(i, j, Generative.hash(k, seed, 0)) & 0xf;

      final Vec3 gu = Simplex.GRAD3_U[h];
      final Vec3 gv = Simplex.GRAD3_V[h];
      return target.set(cosa * gu.x + sina * gv.x, cosa * gu.y + sina * gv.y,
         cosa * gu.z + sina * gv.z);
   }

}
