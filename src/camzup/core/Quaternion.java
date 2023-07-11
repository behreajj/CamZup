package camzup.core;

import java.util.Comparator;
import java.util.Random;

/**
 * A four-dimensional complex number. The <em>x</em>, <em>y</em> and
 * <em>z</em> components are coefficients of the imaginary <em>i</em>,
 * <em>j</em> and <em>k</em>. Discovered by William R. Hamilton with the
 * formula <em>i</em><sup>2</sup> = <em>j</em><sup>2</sup> =
 * <em>k</em><sup>2</sup> = <em>i</em><em>j</em><em>k</em> = -1.0 .
 * Quaternions with a magnitude of 1.0 are commonly used to rotate 3D
 * objects from one orientation to another over minimal distance without
 * suffering gimbal lock.
 */
public class Quaternion implements Comparable < Quaternion > {

   /**
    * The coefficients of the imaginary components <em>i</em>, <em>j</em> and
    * <em>k</em>.
    */
   public final Vec3 imag = new Vec3();

   /**
    * The real component (also known as w).
    */
   public float real = 1.0f;

   /**
    * The default constructor. Defaults to the identity, (1.0, 0.0, 0.0, 0.0)
    * .
    */
   public Quaternion ( ) {

      /*
       * Flip x, y, z: https://stackoverflow.com/a/33999726 . These couldn't be
       * matched up to scaling a mesh on an axis.
       */
   }

   /**
    * Constructs a quaternion by float component.
    *
    * @param real  the real component (w)
    * @param xImag the x component
    * @param yImag the y component
    * @param zImag the z component
    */
   public Quaternion ( final float real, final float xImag, final float yImag,
      final float zImag ) {

      this.set(real, xImag, yImag, zImag);
   }

   /**
    * Constructs a quaternion by real component and imaginary vector.
    *
    * @param real the real component
    * @param imag the imaginary component
    */
   public Quaternion ( final float real, final Vec3 imag ) {

      this.set(real, imag);
   }

   /**
    * A copy constructor.
    *
    * @param source the source quaternion
    */
   public Quaternion ( final Quaternion source ) { this.set(source); }

   /**
    * Returns -1 when this quaternion is less than the comparisand; 1 when it
    * is greater than; 0 when the two are 'equal'. The implementation of this
    * method allows collections of quaternions to be sorted.
    *
    * @param q the comparisand
    *
    * @return the numeric code
    */
   @Override
   public int compareTo ( final Quaternion q ) {

      return this.real < q.real ? -1 : this.real > q.real ? 1 : this.imag
         .compareTo(q.imag);
   }

   /**
    * Tests this quaternion for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    *
    * @see Quaternion#equals(Quaternion)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Quaternion ) obj);
   }

   /**
    * Returns a hash code for this quaternion based on its real and imaginary
    * components.
    *
    * @return the hash code
    *
    * @see Float#floatToIntBits(float)
    * @see Vec3#hashCode()
    */
   @Override
   public int hashCode ( ) {

      return ( IUtils.MUL_BASE ^ Float.floatToIntBits(this.real) )
         * IUtils.HASH_MUL ^ this.imag.hashCode();
   }

   /**
    * Resets this quaternion to an initial state, ( 1.0, 0.0, 0.0, 0.0 ) .
    *
    * @return this quaternion
    */
   public Quaternion reset ( ) {

      this.real = 1.0f;
      this.imag.set(0.0f, 0.0f, 0.0f);
      return this;
   }

   /**
    * Sets the components of this quaternion.
    *
    * @param real  the real (w) component
    * @param xImag the imaginary i coefficient
    * @param yImag the imaginary j coefficient
    * @param zImag the imaginary k coefficient
    *
    * @return this quaternion
    */
   public Quaternion set ( final float real, final float xImag,
      final float yImag, final float zImag ) {

      this.real = real;
      this.imag.set(xImag, yImag, zImag);
      return this;
   }

   /**
    * Sets the components of this quaternion.
    *
    * @param real the real component
    * @param imag the imaginary component
    *
    * @return this quaternion
    */
   public Quaternion set ( final float real, final Vec3 imag ) {

      this.real = real;
      this.imag.set(imag);
      return this;
   }

   /**
    * Copies the components of the input quaternion to this quaternion.
    *
    * @param source the input quaternion
    *
    * @return this quaternion
    */
   public Quaternion set ( final Quaternion source ) {

      this.real = source.real;
      this.imag.set(source.imag);
      return this;
   }

   /**
    * Returns a float array of length 4 containing this quaternion's
    * components. Defaults to returning w as the first element.
    *
    * @return the array
    */
   public float[] toArray ( ) { return this.toArray(true); }

   /**
    * Returns a float array of length 4 containing this quaternion's
    * components. When the argument supplied is true, w is returned as the
    * first element, not the last.
    *
    * @param wFirst issue w as the first element
    *
    * @return the array
    */
   public float[] toArray ( final boolean wFirst ) {

      return this.toArray(new float[4], 0, wFirst);
   }

   /**
    * Puts the quaternion's components into an existing array at the index
    * provided. When the argument supplied is true, w is returned as the first
    * element, not the last.
    *
    * @param arr    the array
    * @param i      the index
    * @param wFirst issue w as the first element
    *
    * @return the array
    */
   public float[] toArray ( final float[] arr, final int i,
      final boolean wFirst ) {

      if ( wFirst ) {
         arr[i] = this.real;
         arr[i + 1] = this.imag.x;
         arr[i + 2] = this.imag.y;
         arr[i + 3] = this.imag.z;
      } else {
         arr[i] = this.imag.x;
         arr[i + 1] = this.imag.y;
         arr[i + 2] = this.imag.z;
         arr[i + 3] = this.real;
      }
      return arr;
   }

   /**
    * Returns a string representation of this quaternion.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

   /**
    * Returns a string representation of this quaternion.
    *
    * @param places the number of places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      return this.toString(new StringBuilder(128), places).toString();
   }

   /**
    * An internal helper function to format a vector as a Python tuple, then
    * append it to a {@link StringBuilder}. Used for testing purposes to
    * compare results with Blender 2.9x.<br>
    * <br>
    * The real component, w, is the first element.
    *
    * @param pyCd the string builder
    *
    * @return the string builder
    */
   @Experimental
   StringBuilder toBlenderCode ( final StringBuilder pyCd ) {

      pyCd.append('(');
      Utils.toFixed(pyCd, this.real, 6);
      pyCd.append(',');
      pyCd.append(' ');
      Utils.toFixed(pyCd, this.imag.x, 6);
      pyCd.append(',');
      pyCd.append(' ');
      Utils.toFixed(pyCd, this.imag.y, 6);
      pyCd.append(',');
      pyCd.append(' ');
      Utils.toFixed(pyCd, this.imag.z, 6);
      pyCd.append(')');
      return pyCd;
   }

   /**
    * Internal helper function to assist with methods that need to print many
    * quaternions. Appends to an existing {@link StringBuilder}.
    *
    * @param sb     the string builder
    * @param places the number of places
    *
    * @return the string builder
    */
   StringBuilder toString ( final StringBuilder sb, final int places ) {

      sb.append("{ real: ");
      Utils.toFixed(sb, this.real, places);
      sb.append(", imag: ");
      this.imag.toString(sb, places);
      sb.append(' ');
      sb.append('}');
      return sb;
   }

   /**
    * Tests equivalence between this and another quaternion. For rough
    * equivalence of floating point components, use the static approximation
    * function instead.
    *
    * @param q the quaternion
    *
    * @return the evaluation
    *
    * @see Quaternion#approx(Quaternion, Quaternion)
    * @see Vec3#equals(Vec3)
    */
   protected boolean equals ( final Quaternion q ) {

      return this.real == q.real && this.imag.equals(q.imag);
   }

   /**
    * Adds two quaternions.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output quaternion
    *
    * @return the sum
    *
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   public static Quaternion add ( final Quaternion a, final Quaternion b,
      final Quaternion target ) {

      Vec3.add(a.imag, b.imag, target.imag);
      target.real = a.real + b.real;
      return target;
   }

   /**
    * Adds two quaternions and normalizes the result.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output quaternion
    *
    * @return the normalized sum
    *
    * @see Quaternion#identity(Quaternion)
    * @see Quaternion#magSq(Quaternion)
    * @see Utils#invSqrtUnchecked(float)
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   public static Quaternion addNorm ( final Quaternion a, final Quaternion b,
      final Quaternion target ) {

      Vec3.add(a.imag, b.imag, target.imag);
      target.real = a.real + b.real;

      final float mSq = Quaternion.magSq(target);
      if ( mSq > 0.0f ) {
         final float mInv = Utils.invSqrtUnchecked(mSq);
         final Vec3 i = target.imag;
         return target.set(target.real * mInv, i.x * mInv, i.y * mInv, i.z
            * mInv);
      }
      return Quaternion.identity(target);
   }

   /**
    * Adds two quaternions and normalizes the result. Emits the sum as an
    * output vector.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output quaternion
    * @param sum    the sum
    *
    * @return the normalized sum
    *
    * @see Quaternion#add(Quaternion, Quaternion, Quaternion)
    * @see Quaternion#normalize(Quaternion, Quaternion)
    */
   public static Quaternion addNorm ( final Quaternion a, final Quaternion b,
      final Quaternion target, final Quaternion sum ) {

      Quaternion.add(a, b, sum);
      return Quaternion.normalize(sum, target);
   }

   /**
    * Tests to see if all the quaternion's components are non-zero.
    *
    * @param q the input quaternion
    *
    * @return the evaluation
    *
    * @see Vec3#all(Vec3)
    */
   public static boolean all ( final Quaternion q ) {

      return q.real != 0.0f && Vec3.all(q.imag);
   }

   /**
    * Tests to see if any of the quaternion's components are non-zero.
    *
    * @param q the input quaternion
    *
    * @return the evaluation
    *
    * @see Vec3#any(Vec3)
    */
   public static boolean any ( final Quaternion q ) {

      return q.real != 0.0f || Vec3.any(q.imag);
   }

   /**
    * Evaluates whether or not two quaternions approximate each other.
    *
    * @param a the left comparisand
    * @param b the right comparisand
    *
    * @return the evaluation
    */
   public static boolean approx ( final Quaternion a, final Quaternion b ) {

      return Quaternion.approx(a, b, IUtils.EPSILON);
   }

   /**
    * Evaluates whether or not two quaternions approximate each other
    * according to a tolerance.
    *
    * @param a         the left comparisand
    * @param b         the right comparisand
    * @param tolerance the tolerance
    *
    * @return the evaluation
    *
    * @see Utils#approx(float, float)
    * @see Vec3#approx(Vec3, Vec3)
    */
   public static boolean approx ( final Quaternion a, final Quaternion b,
      final float tolerance ) {

      return a == b || Utils.approx(a.real, b.real, tolerance) && Vec3.approx(
         a.imag, b.imag, tolerance);
   }

   /**
    * Tests to see if a quaternion has, approximately, the specified
    * magnitude.
    *
    * @param a the quaternion
    * @param b the magnitude
    *
    * @return the evaluation
    */
   public static boolean approxMag ( final Quaternion a, final float b ) {

      return Quaternion.approxMag(a, b, IUtils.EPSILON);
   }

   /**
    * Tests to see if a quaternion has, approximately, the specified
    * magnitude.
    *
    * @param a         the quaternion
    * @param b         the magnitude
    * @param tolerance the tolerance
    *
    * @return the evaluation
    *
    * @see Utils#approx(float, float, float)
    * @see Quaternion#magSq(Quaternion)
    */
   public static boolean approxMag ( final Quaternion a, final float b,
      final float tolerance ) {

      return Utils.approx(Quaternion.magSq(a), b * b, tolerance);
   }

   /**
    * Finds the quaternion's azimuth.
    *
    * @param q the input quaternion
    *
    * @return the angle in radians
    */
   public static float azimuth ( final Quaternion q ) {

      return Quaternion.azimuthSigned(q);
   }

   /**
    * Finds the quaternion's azimuth in the range [-pi, pi] . The azimuth
    * refers to the quaternion's right axis.
    *
    * @param q the input quaternion
    *
    * @return the angle in radians
    */
   public static float azimuthSigned ( final Quaternion q ) {

      final double w = q.real;
      final Vec3 i = q.imag;
      final double ix = i.x;
      final double iy = i.y;
      final double iz = i.z;

      final double xy = ix * iy;
      final double zw = iz * w;

      return ( float ) Math.atan2(zw + zw + xy + xy, w * w + ix * ix - iy * iy
         - iz * iz);
   }

   /**
    * Finds the quaternion's azimuth in the range [0, tau] .
    *
    * @param q the input quaternion
    *
    * @return the angle in radians
    *
    * @see Quaternion#azimuthSigned(Quaternion)
    */
   public static float azimuthUnsigned ( final Quaternion q ) {

      final float a = Quaternion.azimuthSigned(q);
      return a < -0.0f ? a + IUtils.TAU : a;
   }

   /**
    * Returns the conjugate of the quaternion, where the imaginary component
    * is negated.<br>
    * <br>
    * <em>a</em>* = { <em>a<sub>real</sub></em>, -<em>a<sub>imag</sub></em> }
    *
    * @param q      the input quaternion
    * @param target the output quaternion
    *
    * @return the conjugate
    *
    * @see Vec3#negate(Vec3, Vec3)
    */
   public static Quaternion conj ( final Quaternion q,
      final Quaternion target ) {

      Vec3.negate(q.imag, target.imag);
      target.real = q.real;
      return target;
   }

   /**
    * Divides a scalar by a quaternion. Returns the identity if either a or b
    * are zero.
    *
    * @param a      the numerator
    * @param b      the denominator
    * @param target the output quaternion
    *
    * @return the quotient
    *
    * @see Quaternion#identity(Quaternion)
    */
   public static Quaternion div ( final float a, final Quaternion b,
      final Quaternion target ) {

      if ( a == 0.0f ) { return Quaternion.identity(target); }
      final Vec3 bi = b.imag;
      final float bw = b.real;
      final float bx = bi.x;
      final float by = bi.y;
      final float bz = bi.z;
      final float bmSq = bw * bw + bx * bx + by * by + bz * bz;
      if ( bmSq != 0.0f ) {
         final float abmSqInv = a / bmSq;
         return target.set(bw * abmSqInv, -bx * abmSqInv, -by * abmSqInv, -bz
            * abmSqInv);
      }
      return Quaternion.identity(target);
   }

   /**
    * Divides a scalar by a quaternion. Returns the identity if either a or b
    * are zero.
    *
    * @param a         the numerator
    * @param b         the denominator
    * @param target    the output quaternion
    * @param inverted  the inverse
    * @param conjugate the conjugate
    *
    * @return the quotient
    *
    * @see Quaternion#any(Quaternion)
    * @see Quaternion#identity(Quaternion)
    * @see Quaternion#inverse(Quaternion, Quaternion, Quaternion)
    * @see Quaternion#mul(float, Quaternion, Quaternion)
    */
   public static Quaternion div ( final float a, final Quaternion b,
      final Quaternion target, final Quaternion inverted,
      final Quaternion conjugate ) {

      if ( Quaternion.any(b) ) {
         Quaternion.inverse(b, inverted, conjugate);
         Quaternion.mul(a, inverted, target);
         return target;
      }
      Quaternion.identity(conjugate);
      Quaternion.identity(inverted);
      return Quaternion.identity(target);
   }

   /**
    * Divides a quaternion by a scalar. Returns the identity if the operation
    * is invalid.
    *
    * @param a      the numerator
    * @param b      the denominator
    * @param target the output quaternion
    *
    * @return the quotient
    *
    * @see Quaternion#identity(Quaternion)
    * @see Vec3#mul(Vec3, float, Vec3)
    */
   public static Quaternion div ( final Quaternion a, final float b,
      final Quaternion target ) {

      if ( b != 0.0f ) {
         final float bInv = 1.0f / b;
         Vec3.mul(a.imag, bInv, target.imag);
         target.real = a.real * bInv;
         return target;
      }
      return Quaternion.identity(target);
   }

   /**
    * Divides one quaternion by another. Equivalent to multiplying the
    * numerator and the inverse of the denominator. Returns the identity if
    * the operation is invalid.
    *
    * @param a      the numerator
    * @param b      the denominator
    * @param target the output quaternion
    *
    * @return the quotient
    *
    * @see Quaternion#identity(Quaternion)
    * @see Utils#approx(float, float)
    */
   public static Quaternion div ( final Quaternion a, final Quaternion b,
      final Quaternion target ) {

      final Vec3 bi = b.imag;
      final float bw = b.real;
      final float bx = bi.x;
      final float by = bi.y;
      final float bz = bi.z;

      final float bmSq = bw * bw + bx * bx + by * by + bz * bz;

      if ( bmSq == 0.0f ) { return Quaternion.identity(target); }

      float bwInv = bw;
      float bxInv = -bx;
      float byInv = -by;
      float bzInv = -bz;

      if ( !Utils.approx(bmSq, 1.0f) ) {
         final float bmSqInv = 1.0f / bmSq;
         bwInv *= bmSqInv;
         bxInv *= bmSqInv;
         byInv *= bmSqInv;
         bzInv *= bmSqInv;
      }

      final Vec3 ai = a.imag;
      final float aw = a.real;

      return target.set(aw * bwInv - ai.x * bxInv - ai.y * byInv - ai.z * bzInv,
         ai.x * bwInv + aw * bxInv + ai.y * bzInv - ai.z * byInv, ai.y * bwInv
            + aw * byInv + ai.z * bxInv - ai.x * bzInv, ai.z * bwInv + aw
               * bzInv + ai.x * byInv - ai.y * bxInv);
   }

   /**
    * Divides one quaternion by another. Equivalent to multiplying the
    * numerator and the inverse of the denominator.
    *
    * @param a         the numerator
    * @param b         the denominator
    * @param target    the output quaternion
    * @param inverted  the inverse
    * @param conjugate the conjugate
    *
    * @return the quotient
    *
    * @see Quaternion#identity(Quaternion)
    * @see Quaternion#inverse(Quaternion, Quaternion, Quaternion)
    * @see Quaternion#mul(Quaternion, Quaternion, Quaternion)
    */
   public static Quaternion div ( final Quaternion a, final Quaternion b,
      final Quaternion target, final Quaternion inverted,
      final Quaternion conjugate ) {

      if ( Quaternion.any(b) ) {
         Quaternion.inverse(b, inverted, conjugate);
         return Quaternion.mul(a, inverted, target);
      }
      Quaternion.identity(conjugate);
      Quaternion.identity(inverted);
      return Quaternion.identity(target);
   }

   /**
    * Finds the dot product of two quaternions by summing the products of
    * their corresponding components. The dot product of a quaternion with
    * itself is equal to its magnitude squared.
    *
    * @param a left operand
    * @param b right operand
    *
    * @return the dot product
    *
    * @see Vec3#dot(Vec3, Vec3)
    */
   public static float dot ( final Quaternion a, final Quaternion b ) {

      return a.real * b.real + Vec3.dot(a.imag, b.imag);
   }

   /**
    * Finds the exponent of a quaternion.<br>
    * <br>
    * Returns the identity when the quaternion's imaginary vector has zero
    * magnitude.
    *
    * @param q      the quaternion
    * @param target the output quaternion
    *
    * @return the exponent
    *
    * @see Math#exp(double)
    * @see Math#sqrt(double)
    * @see Math#sin(double)
    * @see Math#cos(double)
    * @see Quaternion#identity(Quaternion)
    */
   public static Quaternion exp ( final Quaternion q,
      final Quaternion target ) {

      final Vec3 qi = q.imag;
      final double x = qi.x;
      final double y = qi.y;
      final double z = qi.z;

      final double mgImSq = x * x + y * y + z * z;
      if ( mgImSq > IUtils.EPSILON_D ) {
         final double wExp = Math.exp(q.real);
         final double mgIm = Math.sqrt(mgImSq);
         final double scalar = wExp * Math.sin(mgIm) / mgIm;
         return target.set(( float ) ( wExp * Math.cos(mgIm) ), ( float ) ( x
            * scalar ), ( float ) ( y * scalar ), ( float ) ( z * scalar ));
      }
      return Quaternion.identity(target);
   }

   /**
    * Creates a quaternion from two 2D axes. The up axis is assumed to be
    * (0.0, 0.0, 1.0). For use in 2.5D graphics, where 2D inputs need to be
    * represented internally as 3D elements.
    *
    * @param right   the right axis
    * @param forward the forward axis
    * @param target  the output quaternion
    *
    * @return the quaternion
    *
    * @see Quaternion#fromAxes(float, float, float, float, float, float,
    *      float, float, float, Quaternion)
    * @see Utils#invSqrt(float)
    * @see Vec2#magSq(Vec2)
    */
   public static Quaternion fromAxes ( final Vec2 right, final Vec2 forward,
      final Quaternion target ) {

      final float rminv = Utils.invSqrt(Vec2.magSq(right));
      final float fminv = Utils.invSqrt(Vec2.magSq(forward));
      return Quaternion.fromAxes(right.x * rminv, forward.y * fminv, 1.0f, 0.0f,
         0.0f, 0.0f, 0.0f, right.y * rminv, forward.x * fminv, target);
   }

   /**
    * Creates a quaternion from three axes.
    *
    * @param right   the right axis
    * @param forward the forward axis
    * @param up      the up axis
    * @param target  the output quaternion
    *
    * @return the quaternion
    *
    * @see Quaternion#fromAxes(float, float, float, float, float, float,
    *      float, float, float, Quaternion)
    * @see Utils#invSqrt(float)
    * @see Vec3#magSq(Vec3)
    */
   public static Quaternion fromAxes ( final Vec3 right, final Vec3 forward,
      final Vec3 up, final Quaternion target ) {

      final float rminv = Utils.invSqrt(Vec3.magSq(right));
      final float fminv = Utils.invSqrt(Vec3.magSq(forward));
      final float uminv = Utils.invSqrt(Vec3.magSq(up));
      return Quaternion.fromAxes(right.x * rminv, forward.y * fminv, up.z
         * uminv, forward.z * fminv, up.y * uminv, up.x * uminv, right.z
            * rminv, right.y * rminv, forward.x * fminv, target);
   }

   /**
    * Sets a quaternion from an axis and angle. Normalizes the axis. If the
    * axis has no magnitude, returns the identity.
    *
    * @param radians the angle in radians
    * @param axis    the axis
    * @param target  the output quaternion
    *
    * @return the quaternion
    *
    * @see Math#sqrt(double)
    * @see Quaternion#identity(Quaternion)
    */
   public static Quaternion fromAxisAngle ( final float radians,
      final Vec3 axis, final Quaternion target ) {

      final double ax = axis.x;
      final double ay = axis.y;
      final double az = axis.z;
      final double amSq = ax * ax + ay * ay + az * az;
      if ( amSq > 0.0d ) {
         final double rHalf = radians % IUtils.TAU_D * 0.5d;
         final double amInv = Math.sin(rHalf) / Math.sqrt(amSq);
         return target.set(( float ) Math.cos(rHalf), ( float ) ( ax * amInv ),
            ( float ) ( ay * amInv ), ( float ) ( az * amInv ));
      }
      return Quaternion.identity(target);
   }

   /**
    * Creates a quaternion from a direction given a handedness. Normalizes the
    * input direction. Also known as a 'lookAt' or 'toTracking' function.
    *
    * @param dir        the direction
    * @param handedness the handedness
    * @param target     the output quaternion
    *
    * @return the quaternion
    *
    * @see Utils#invSqrtUnchecked(float)
    * @see Utils#invHypot(float, float, float)
    * @see Quaternion#fromAxes(float, float, float, float, float, float,
    *      float, float, float, Quaternion)
    * @see Quaternion#identity(Quaternion)
    * @see Vec3#magSq(Vec3)
    * @see Vec3#none(Vec3)
    */
   @Experimental
   public static Quaternion fromDir ( final Vec3 dir,
      final Handedness handedness, final Quaternion target ) {

      if ( Vec3.none(dir) ) { return Quaternion.identity(target); }
      final float mSq0 = Vec3.magSq(dir);
      final float mInv0 = Utils.invSqrtUnchecked(mSq0);
      final float xForward = dir.x * mInv0;
      final float yForward = dir.y * mInv0;
      final float zForward = dir.z * mInv0;

      /*
       * Left handed: cross (0.0, -1.0, 0.0) and forward. Right handed: cross
       * (0.0, 0.0, -1.0) and forward.
       */
      final boolean isRight = handedness == Handedness.RIGHT;
      final float x1 = isRight ? yForward : -zForward;
      final float y1 = isRight ? -xForward : 0.0f;
      final float z1 = isRight ? 0.0f : xForward;

      /* Polarity: an infinite number of orientations is possible. */
      final boolean parallel = Utils.approx(x1, 0.0f, IUtils.EPSILON) && Utils
         .approx(y1, 0.0f, IUtils.EPSILON) && Utils.approx(z1, 0.0f,
            IUtils.EPSILON);

      if ( parallel ) {
         if ( isRight ) {
            /*
             * In a right-handed coordinate system, the forward axis is either
             * (0.0, 0.0, 1.0) or (0.0, 0.0, -1.0) .
             */
            if ( zForward < 0.0f ) {
               return target.set(-IUtils.ONE_SQRT_2, IUtils.ONE_SQRT_2, 0.0f,
                  0.0f);
            }
            return target.set(IUtils.ONE_SQRT_2, IUtils.ONE_SQRT_2, 0.0f, 0.0f);
         }

         /*
          * In a left-handed coordinate system, the forward axis is either (0.0,
          * 1.0, 0.0) or (0.0, -1.0, 0.0) .
          */
         if ( yForward < 0.0f ) { return target.set(0.0f, 0.0f, 0.0f, 1.0f); }
         return target.set(0.0f, 0.0f, 1.0f, 0.0f);
      }

      /* Normalize right. */
      final float mInv1 = Utils.invHypot(x1, y1, z1);
      final float xRight = x1 * mInv1;
      final float yRight = y1 * mInv1;
      final float zRight = z1 * mInv1;

      /* Cross right and forward to get up. */
      final float x2 = yRight * zForward - zRight * yForward;
      final float y2 = zRight * xForward - xRight * zForward;
      final float z2 = xRight * yForward - yRight * xForward;

      /* Normalize forward. */
      final float mInv2 = Utils.invHypot(x2, y2, z2);
      final float xUp = x2 * mInv2;
      final float yUp = y2 * mInv2;
      final float zUp = z2 * mInv2;

      return Quaternion.fromAxes(xRight, yForward, zUp, zForward, yUp, xUp,
         zRight, yRight, xForward, target);
   }

   /**
    * Creates a quaternion from a direction. Switches reference up if forward
    * and reference up are parallel. Emits the right, forward and up axes as a
    * byproduct of the function's calculations.
    *
    * @param dir        the direction
    * @param handedness the handedness
    * @param target     the output quaternion
    * @param right      the output right axis
    * @param forward    the output forward axis
    * @param up         the output up axis
    *
    * @return the quaternion
    *
    * @see Quaternion#fromAxes(float, float, float, float, float, float,
    *      float, float, float, Quaternion)
    * @see Quaternion#identity(Quaternion)
    * @see Vec3#crossNorm(Vec3, Vec3, Vec3)
    * @see Vec3#forward(Vec3)
    * @see Vec3#none(Vec3)
    * @see Vec3#normalize(Vec3, Vec3)
    * @see Vec3#right(Vec3)
    * @see Vec3#up(Vec3)
    */
   public static Quaternion fromDir ( final Vec3 dir,
      final Handedness handedness, final Quaternion target, final Vec3 right,
      final Vec3 forward, final Vec3 up ) {

      if ( Vec3.none(dir) ) {
         Vec3.right(right);
         Vec3.forward(forward);
         Vec3.up(up);
         return Quaternion.identity(target);
      }

      Vec3.normalize(dir, forward);
      final boolean isRight = handedness == Handedness.RIGHT;
      if ( isRight ) {
         right.set(forward.y, -forward.x, 0.0f);
      } else {
         right.set(-forward.z, 0.0f, forward.x);
      }

      /* Polarity: an infinite number of orientations is possible. */
      if ( Vec3.approxMag(right, 0.0f, IUtils.EPSILON) ) {

         if ( isRight ) {

            /*
             * In a right-handed coordinate system, the forward axis is either
             * (0.0, 0.0, 1.0) or (0.0, 0.0, -1.0) .
             */
            if ( forward.z < 0.0f ) {
               Vec3.right(right);
               Vec3.down(forward);
               Vec3.forward(up);
               return target.set(-IUtils.ONE_SQRT_2, IUtils.ONE_SQRT_2, 0.0f,
                  0.0f);
            }
            Vec3.right(right);
            Vec3.up(forward);
            Vec3.back(up);
            return target.set(IUtils.ONE_SQRT_2, IUtils.ONE_SQRT_2, 0.0f, 0.0f);
         }

         /*
          * In a left-handed coordinate system, the forward axis is either (0.0,
          * 1.0, 0.0) or (0.0, -1.0, 0.0) .
          */
         if ( forward.y < 0.0f ) {
            Vec3.left(right);
            Vec3.back(forward);
            Vec3.up(up);
            return target.set(0.0f, 0.0f, 0.0f, 1.0f);
         }
         Vec3.left(right);
         Vec3.forward(forward);
         Vec3.down(up);
         return target.set(0.0f, 0.0f, 1.0f, 0.0f);
      }

      Vec3.normalize(right, right);
      Vec3.crossNorm(right, forward, up);
      return Quaternion.fromAxes(right.x, forward.y, up.z, forward.z, up.y,
         up.x, right.z, right.y, forward.x, target);
   }

   /**
    * Creates a quaternion from spherical coordinates. The quaternion's right
    * axis corresponds to the point on the sphere, i.e., what would be
    * returned by {@link Vec3#fromSpherical(float, float, float, Vec3)}.
    *
    * @param azimuth     the angle theta in radians
    * @param inclination the angle phi in radians
    * @param target      the output quaternion
    *
    * @return the quaternion
    */
   @Experimental
   public static Quaternion fromSpherical ( final float azimuth,
      final float inclination, final Quaternion target ) {

      final double azHalf = 0.5d * ( azimuth % IUtils.TAU_D );
      final double cosAzim = Math.cos(azHalf);
      final double sinAzim = Math.sin(azHalf);

      final double inHalf = IUtils.TAU_D - inclination * 0.5d;
      final double cosIncl = Math.cos(inHalf);
      final double sinIncl = Math.sin(inHalf);

      return target.set(( float ) ( cosAzim * cosIncl ), ( float ) ( sinAzim
         * -sinIncl ), ( float ) ( sinIncl * cosAzim ), ( float ) ( sinAzim
            * cosIncl ));
   }

   /**
    * Creates a quaternion with reference to two vectors. This function
    * creates normalized copies of the vectors.<br>
    * <br>
    * fromTo ( <em>a</em>, <em>b</em> ) := { <em>a</em> . <em>b</em>,
    * <em>a</em> x <em>b</em> }<br>
    * <br>
    * The real component is the dot product of the vectors; the imaginary
    * component is their cross product.<br>
    * <br>
    * Returns the identity if either vector is zero.
    *
    * @param origin the origin vector
    * @param dest   the destination vector
    * @param target the output quaternion
    *
    * @return the quaternion
    *
    * @see Quaternion#identity(Quaternion)
    * @see Utils#approx(float, float)
    * @see Utils#invSqrtUnchecked(float)
    */
   public static Quaternion fromTo ( final Vec3 origin, final Vec3 dest,
      final Quaternion target ) {

      float anx = origin.x;
      float any = origin.y;
      float anz = origin.z;
      final float amSq = anx * anx + any * any + anz * anz;
      if ( amSq <= 0.0f ) { return Quaternion.identity(target); }

      float bnx = dest.x;
      float bny = dest.y;
      float bnz = dest.z;
      final float bmSq = bnx * bnx + bny * bny + bnz * bnz;
      if ( bmSq <= 0.0f ) { return Quaternion.identity(target); }

      if ( amSq != 1.0f ) {
         final float amInv = Utils.invSqrtUnchecked(amSq);
         anx *= amInv;
         any *= amInv;
         anz *= amInv;
      }

      if ( bmSq != 1.0f ) {
         final float bmInv = Utils.invSqrtUnchecked(bmSq);
         bnx *= bmInv;
         bny *= bmInv;
         bnz *= bmInv;
      }

      target.real = anx * bnx + any * bny + anz * bnz;
      final Vec3 i = target.imag;
      i.x = any * bnz - anz * bny;
      i.y = anz * bnx - anx * bnz;
      i.z = anx * bny - any * bnx;

      return target;
   }

   /**
    * Gets the forward axis of the rotation. Equivalent to multiplying (0.0,
    * 1.0, 0.0) by the quaternion. If all three axes need to be retrieved, use
    * {@link Quaternion#toAxes(Quaternion, Vec3, Vec3, Vec3)} .
    *
    * @param q      the quaternion
    * @param target the output vector
    *
    * @return the forward axis
    */
   public static Vec3 getForward ( final Quaternion q, final Vec3 target ) {

      final Vec3 imag = q.imag;
      final float w = q.real;
      final float x = imag.x;
      final float y = imag.y;
      final float z = imag.z;

      final float xy = x * y;
      final float xw = x * w;
      final float yz = y * z;
      final float zw = z * w;

      return target.set(xy + xy - ( zw + zw ), w * w + y * y - x * x - z * z, xw
         + xw + yz + yz);
   }

   /**
    * Gets the right axis of the rotation. Equivalent to multiplying (1.0,
    * 0.0, 0.0) by the quaternion. If all three axes need to be retrieved, use
    * {@link Quaternion#toAxes(Quaternion, Vec3, Vec3, Vec3)} .
    *
    * @param q      the quaternion
    * @param target the output vector
    *
    * @return the right axis
    */
   public static Vec3 getRight ( final Quaternion q, final Vec3 target ) {

      final Vec3 imag = q.imag;
      final float w = q.real;
      final float x = imag.x;
      final float y = imag.y;
      final float z = imag.z;

      final float xy = x * y;
      final float xz = x * z;
      final float yw = y * w;
      final float zw = z * w;

      return target.set(w * w + x * x - y * y - z * z, zw + zw + xy + xy, xz
         + xz - ( yw + yw ));
   }

   /**
    * Gets the up axis of the rotation. Equivalent to multiplying (0.0, 0.0,
    * 1.0) by the quaternion. If all three axes need to be retrieved, use
    * {@link Quaternion#toAxes(Quaternion, Vec3, Vec3, Vec3)} .
    *
    * @param q      the quaternion
    * @param target the output vector
    *
    * @return the up axis
    */
   public static Vec3 getUp ( final Quaternion q, final Vec3 target ) {

      final Vec3 imag = q.imag;
      final float w = q.real;
      final float x = imag.x;
      final float y = imag.y;
      final float z = imag.z;

      final float xz = x * z;
      final float xw = x * w;
      final float yz = y * z;
      final float yw = y * w;

      return target.set(yw + yw + xz + xz, yz + yz - ( xw + xw ), w * w + z * z
         - x * x - y * y);
   }

   /**
    * Sets the target to the identity quaternion, ( 1.0, 0.0, 0.0, 0.0 ).
    *
    * @param target the output quaternion
    *
    * @return the identity
    */
   public static Quaternion identity ( final Quaternion target ) {

      return target.set(1.0f, 0.0f, 0.0f, 0.0f);
   }

   /**
    * Finds the quaternion's inclination.
    *
    * @param q the input quaternion
    *
    * @return the inclination
    */
   public static float inclination ( final Quaternion q ) {

      return Quaternion.inclinationSigned(q);
   }

   /**
    * Finds the quaternion's inclination in the range [-pi / 2.0, pi / 2.0] .
    *
    * @param q the input quaternion
    *
    * @return the inclination
    *
    * @see Quaternion#inclinationUnsigned(Quaternion)
    */
   public static float inclinationSigned ( final Quaternion q ) {

      return IUtils.HALF_PI - Quaternion.inclinationUnsigned(q);
   }

   /**
    * Finds the quaternion's inclination in the range [pi, 0.0] . The azimuth
    * refers to the quaternion's right axis.
    *
    * @param q the input quaternion
    *
    * @return the inclination
    */
   public static float inclinationUnsigned ( final Quaternion q ) {

      final double w = q.real;
      final Vec3 i = q.imag;
      final double ix = i.x;
      final double iy = i.y;
      final double iz = i.z;

      final double xy = ix * iy;
      final double xz = ix * iz;
      final double yw = iy * w;
      final double zw = iz * w;

      final double vx = w * w + ix * ix - iy * iy - iz * iz;
      final double vy = zw + zw + xy + xy;
      final double vz = xz + xz - ( yw + yw );

      final double mSq = vx * vx + vy * vy + vz * vz;
      return mSq > 0.0d ? ( float ) Math.acos(vz / Math.sqrt(mSq))
         : IUtils.HALF_PI;
   }

   /**
    * Finds the inverse, or reciprocal, of a quaternion, which is the
    * conjugate divided by the magnitude squared.<br>
    * <br>
    * <em>a</em><sup>-1</sup> := <em>a</em>* / |<em>a</em>|<sup>2</sup><br>
    * <br>
    * If a quaternion is of unit length, its inverse is equal to its
    * conjugate.<br>
    * <br>
    * Returns the identity if the quaternion cannot be inverted.
    *
    * @param q      the input quaternion
    * @param target the output quaternion
    *
    * @return the inverse
    *
    * @see Quaternion#identity(Quaternion)
    * @see Quaternion#magSq(Quaternion)
    */
   public static Quaternion inverse ( final Quaternion q,
      final Quaternion target ) {

      final Vec3 i = q.imag;
      final float mSq = q.real * q.real + i.x * i.x + i.y * i.y + i.z * i.z;
      if ( mSq != 0.0f ) {
         final float msi = 1.0f / mSq;
         return target.set(q.real * msi, -i.x * msi, -i.y * msi, -i.z * msi);
      }
      return Quaternion.identity(target);
   }

   /**
    * Finds the inverse, or reciprocal, of a quaternion, which is the its
    * conjugate divided by its magnitude squared.<br>
    * <br>
    * <em>a</em><sup>-1</sup> := <em>a</em>* / |<em>a</em>|<sup>2</sup><br>
    * <br>
    * If a quaternion is of unit length, its inverse is equal to its
    * conjugate.
    *
    * @param q         the input quaternion
    * @param target    the output quaternion
    * @param conjugate the conjugate
    *
    * @return the inverse
    *
    * @see Quaternion#conj(Quaternion, Quaternion)
    * @see Quaternion#div(Quaternion, float, Quaternion)
    * @see Quaternion#magSq(Quaternion)
    */
   public static Quaternion inverse ( final Quaternion q,
      final Quaternion target, final Quaternion conjugate ) {

      Quaternion.conj(q, conjugate);
      Quaternion.div(conjugate, Quaternion.magSq(q), target);
      return target;
   }

   /**
    * Multiplies a vector by a quaternion's inverse, allowing a prior rotation
    * to be undone.
    *
    * @param q      the quaternion
    * @param source the source vector
    * @param target the target vector
    *
    * @return the unrotated vector
    *
    * @see Quaternion#magSq(Quaternion)
    */
   public static Vec3 invMulVector ( final Quaternion q, final Vec3 source,
      final Vec3 target ) {

      final float mSq = Quaternion.magSq(q);
      if ( mSq != 0.0f ) {
         final float mSqInv = 1.0f / mSq;

         final float w = q.real * mSqInv;
         final Vec3 i = q.imag;
         final float qx = -i.x * mSqInv;
         final float qy = -i.y * mSqInv;
         final float qz = -i.z * mSqInv;

         final float iw = -qx * source.x - qy * source.y - qz * source.z;
         final float ix = w * source.x + qy * source.z - qz * source.y;
         final float iy = w * source.y + qz * source.x - qx * source.z;
         final float iz = w * source.z + qx * source.y - qy * source.x;

         return target.set(ix * w + iz * qy - iw * qx - iy * qz, iy * w + ix
            * qz - iw * qy - iz * qx, iz * w + iy * qx - iw * qz - ix * qy);
      }
      return target.reset();
   }

   /**
    * Tests if the quaternion is the identity, where its real component is 1.0
    * and its imaginary components are all zero.
    *
    * @param q the quaternion
    *
    * @return the evaluation
    *
    * @see Vec3#none(Vec3)
    */
   public static boolean isIdentity ( final Quaternion q ) {

      return q.real == 1.0f && Vec3.none(q.imag);
   }

   /**
    * Tests to see if a quaternion is pure, i.e. if its real component is
    * zero.
    *
    * @param q the quaternion
    *
    * @return the evaluation
    */
   public static boolean isPure ( final Quaternion q ) {

      return q.real == 0.0f;
   }

   /**
    * Tests if the quaternion is a versor, i.e., has a magnitude of 1.0.
    *
    * @param q the quaternion
    *
    * @return the evaluation
    *
    * @see Quaternion#magSq(Quaternion)
    * @see Utils#approx(float, float)
    */
   public static boolean isVersor ( final Quaternion q ) {

      return Utils.approx(Quaternion.magSq(q), 1.0f);
   }

   /**
    * Finds the natural logarithm of a quaternion. Uses the formula:<br>
    * <br>
    * <em>q</em> = { <em>log</em> ( | <em>a</em> | ) / 2.0,<br>
    * <em>a<sub>\u00eemag</sub></em> <em>atan2</em> ( |
    * <em>a<sub>imag</sub></em> | , <em>a<sub>real</sub></em> )
    *
    * @param q      the quaternion
    * @param target the output quaternion
    *
    * @return the natural logarithm
    *
    * @see Math#atan2(double, double)
    * @see Math#log(double)
    * @see Math#sqrt(double)
    */
   public static Quaternion log ( final Quaternion q,
      final Quaternion target ) {

      final Vec3 qi = q.imag;
      final double w = q.real;
      final double x = qi.x;
      final double y = qi.y;
      final double z = qi.z;

      final double mgImSq = x * x + y * y + z * z;
      if ( mgImSq > IUtils.EPSILON_D ) {
         final double mgIm = Math.sqrt(mgImSq);
         final double t = Math.atan2(mgIm, w) / mgIm;
         target.imag.set(( float ) ( x * t ), ( float ) ( y * t ), ( float ) ( z
            * t ));
      } else {
         target.imag.set(0.0f, 0.0f, 0.0f);
      }

      target.real = ( float ) ( 0.5d * Math.log(w * w + mgImSq) );
      return target;
   }

   /**
    * Finds the length, or magnitude, of a quaternion.<br>
    * <br>
    * |<em>a</em>| := \u221a <em>a</em> . <em>a</em><br>
    * <br>
    * |<em>a</em>| := \u221a <em>a</em> <em>a*</em>
    *
    * @param q the input quaternion
    *
    * @return the magnitude
    *
    * @see Quaternion#magSq(Quaternion)
    * @see Utils#sqrtUnchecked(float)
    */
   public static float mag ( final Quaternion q ) {

      return Utils.sqrtUnchecked(Quaternion.magSq(q));
   }

   /**
    * Finds the magnitude squared of a quaternion. Equivalent to the
    * {@link Quaternion#dot(Quaternion, Quaternion)} of a quaternion with
    * itself and to the product of a quaternion with its
    * {@link Quaternion#conj(Quaternion, Quaternion)}.<br>
    * <br>
    * |<em>a</em>|<sup>2</sup> := <em>a</em> . <em>a</em><br>
    * <br>
    * |<em>a</em>|<sup>2</sup> := <em>a</em> <em>a*</em>
    *
    * @param q the quaternion
    *
    * @return the magnitude squared
    *
    * @see Vec3#magSq(Vec3)
    */
   public static float magSq ( final Quaternion q ) {

      return q.real * q.real + Vec3.magSq(q.imag);
   }

   /**
    * Mixes two vectors together by a step in [0.0, 1.0], then normalizes the
    * result.
    *
    * @param orig   the original quaternion
    * @param dest   the destination quaternion
    * @param step   the step
    * @param target the output quaternion
    *
    * @return the mix
    *
    * @see Quaternion#normalize(Quaternion, Quaternion)
    * @see Quaternion#slerpUnclamped(Quaternion, Quaternion, float,
    *      Quaternion)
    */
   public static Quaternion mix ( final Quaternion orig, final Quaternion dest,
      final float step, final Quaternion target ) {

      if ( step <= 0.0f ) { return Quaternion.normalize(orig, target); }
      if ( step >= 1.0f ) { return Quaternion.normalize(dest, target); }
      return Quaternion.slerpUnclamped(orig, dest, step, target);
   }

   /**
    * Multiplies a scalar and quaternion.
    *
    * @param a      the scalar
    * @param b      the quaternion
    * @param target the output quaternion
    *
    * @return the scaled quaternion
    *
    * @see Quaternion#identity(Quaternion)
    * @see Vec3#mul(float, Vec3, Vec3)
    */
   public static Quaternion mul ( final float a, final Quaternion b,
      final Quaternion target ) {

      if ( a != 0.0f ) {
         Vec3.mul(a, b.imag, target.imag);
         target.real = a * b.real;
         return target;
      }
      return Quaternion.identity(target);
   }

   /**
    * Multiplies a quaternion and scalar.
    *
    * @param a      the quaternion
    * @param b      the scalar
    * @param target the output quaternion
    *
    * @return the scaled quaternion
    *
    * @see Quaternion#identity(Quaternion)
    * @see Vec3#mul(Vec3, float, Vec3)
    */
   public static Quaternion mul ( final Quaternion a, final float b,
      final Quaternion target ) {

      if ( b != 0.0f ) {
         Vec3.mul(a.imag, b, target.imag);
         target.real = a.real * b;
         return target;
      }
      return Quaternion.identity(target);
   }

   /**
    * Multiplies two quaternions. Also referred to as the Hamilton product.
    * Uses the formula<br>
    * <br>
    * <em>a</em> <em>b</em> := { <em>a<sub>real</sub></em>
    * <em>b<sub>real</sub></em> - <em>a<sub>imag</sub></em> .
    * <em>b<sub>imag</sub></em> , <em>a<sub>imag</sub></em> x
    * <em>b<sub>imag</sub></em> + <em>a<sub>real</sub></em>
    * <em>b<sub>imag</sub></em> + <em>b<sub>real</sub></em>
    * <em>a<sub>imag</sub></em> }<br>
    * <br>
    * Quaternion multiplication is not commutative.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output quaternion
    *
    * @return the product
    */
   public static Quaternion mul ( final Quaternion a, final Quaternion b,
      final Quaternion target ) {

      final Vec3 ai = a.imag;
      final Vec3 bi = b.imag;
      final float aw = a.real;
      final float bw = b.real;

      /* @formatter:off */
      return target.set(
         aw * bw - ( ai.x * bi.x + ai.y * bi.y + ai.z * bi.z ),
         ai.x * bw + aw * bi.x + ai.y * bi.z - ai.z * bi.y,
         ai.y * bw + aw * bi.y + ai.z * bi.x - ai.x * bi.z,
         ai.z * bw + aw * bi.z + ai.x * bi.y - ai.y * bi.x);
      /* @formatter:on */
   }

   /**
    * Multiplies a vector by a quaternion, in effect rotating the vector by
    * the quaternion. Equivalent to promoting the vector to a pure quaternion,
    * multiplying the rotation quaternion and promoted vector, then dividing
    * the product by the rotation.<br>
    * <br>
    * <em>a</em> <em>b</em> := ( <em>a</em> { 0.0, <em>b</em> } ) / a<br>
    * <br>
    * The result is then demoted to a vector, as the real component should be
    * 0.0 . This is often denoted as <em>P' = RPR'</em>.
    *
    * @param q      the quaternion
    * @param source the input vector
    * @param target the output vector
    *
    * @return the rotated vector
    */
   public static Vec3 mulVector ( final Quaternion q, final Vec3 source,
      final Vec3 target ) {

      final Vec3 imag = q.imag;
      final float qw = q.real;
      final float qx = imag.x;
      final float qy = imag.y;
      final float qz = imag.z;

      final float iw = -qx * source.x - qy * source.y - qz * source.z;
      final float ix = qw * source.x + qy * source.z - qz * source.y;
      final float iy = qw * source.y + qz * source.x - qx * source.z;
      final float iz = qw * source.z + qx * source.y - qy * source.x;

      return target.set(ix * qw + iz * qy - iw * qx - iy * qz, iy * qw + ix * qz
         - iw * qy - iz * qx, iz * qw + iy * qx - iw * qz - ix * qy);
   }

   /**
    * Negates all components of the quaternion.
    *
    * @param q      the quaternion
    * @param target the output quaternion
    *
    * @return the negation
    *
    * @see Vec3#negate(Vec3, Vec3)
    */
   public static Quaternion negate ( final Quaternion q,
      final Quaternion target ) {

      target.real = -q.real;
      Vec3.negate(q.imag, target.imag);
      return target;
   }

   /**
    * Tests if all components of the quaternion are zero.
    *
    * @param q the quaternion
    *
    * @return the evaluation
    *
    * @see Vec3#none(Vec3)
    */
   public static boolean none ( final Quaternion q ) {

      return q.real == 0.0f && Vec3.none(q.imag);
   }

   /**
    * Divides a quaternion by its magnitude, such that its new magnitude is
    * one and it lies on a 4D hyper-sphere. Uses the formula: <br>
    * <br>
    * <em>\u00e2</em> = <em>a</em> / |<em>a</em>|<br>
    * <br>
    * Quaternions with zero magnitude will return the identity.
    *
    * @param q      the input quaternion
    * @param target the output quaternion
    *
    * @return the normalized quaternion
    *
    * @see Quaternion#identity(Quaternion)
    */
   public static Quaternion normalize ( final Quaternion q,
      final Quaternion target ) {

      final Vec3 i = q.imag;
      final float mSq = q.real * q.real + i.x * i.x + i.y * i.y + i.z * i.z;
      if ( mSq != 0.0f ) {
         final float mInv = Utils.invSqrtUnchecked(mSq);
         return target.set(q.real * mInv, i.x * mInv, i.y * mInv, i.z * mInv);
      }
      return Quaternion.identity(target);
   }

   /**
    * Raises a quaternion to a scalar power. Uses the formula<br>
    * <br>
    * q = <em>exp</em> ( <em>b</em> <em>log</em> ( <em>a</em> ) )
    *
    * @param a      the quaternion
    * @param b      the scalar
    * @param target the output quaternion
    *
    * @return the output quaternion
    *
    * @see Quaternion#identity(Quaternion)
    * @see Math#atan2(double, double)
    * @see Math#cos(double)
    * @see Math#exp(double)
    * @see Math#log(double)
    * @see Math#sin(double)
    * @see Math#sqrt(double)
    */
   public static Quaternion pow ( final Quaternion a, final float b,
      final Quaternion target ) {

      final Vec3 ai = a.imag;
      final double aw = a.real;
      final double ax = ai.x;
      final double ay = ai.y;
      final double az = ai.z;
      final double aMagImSq = ax * ax + ay * ay + az * az;

      double lnw = 0.5d * Math.log(aw * aw + aMagImSq);
      double lnx = 0.0d;
      double lny = 0.0d;
      double lnz = 0.0d;
      if ( aMagImSq > IUtils.EPSILON_D ) {
         final double aMagIm = Math.sqrt(aMagImSq);
         final double theta = Math.atan2(aMagIm, aw) / aMagIm;
         lnx = ax * theta;
         lny = ay * theta;
         lnz = az * theta;
      }

      if ( b != 0.0f ) {
         final double bd = b;
         lnw *= bd;
         lnx *= bd;
         lny *= bd;
         lnz *= bd;
      } else {
         lnw = 1.0d;
         lnx = 0.0d;
         lny = 0.0d;
         lnz = 0.0d;
      }

      final double lnMgImSq = lnx * lnx + lny * lny + lnz * lnz;
      if ( lnMgImSq > IUtils.EPSILON_D ) {
         final double lnwExp = Math.exp(lnw);
         final double lnMgIm = Math.sqrt(lnMgImSq);
         final double lnSclr = lnwExp * Math.sin(lnMgIm) / lnMgIm;
         return target.set(( float ) ( lnwExp * Math.cos(lnMgIm) ),
            ( float ) ( lnx * lnSclr ), ( float ) ( lny * lnSclr ),
            ( float ) ( lnz * lnSclr ));
      }
      return Quaternion.identity(target);
   }

   /**
    * Raises a quaternion to a scalar power. Uses the formula<br>
    * <br>
    * q = <em>exp</em> ( <em>b</em> <em>log</em> ( <em>a</em> ) ) <br>
    * <br>
    * Emits the logarithm and scaled logarithm.
    *
    * @param a      the quaternion
    * @param b      the scalar
    * @param target the output quaternion
    * @param ln     the natural logarithm
    * @param scaled the scaled logarithm
    *
    * @return the output quaternion
    *
    * @see Quaternion#exp(Quaternion, Quaternion)
    * @see Quaternion#log(Quaternion, Quaternion)
    * @see Quaternion#mul(Quaternion, float, Quaternion)
    */
   public static Quaternion pow ( final Quaternion a, final float b,
      final Quaternion target, final Quaternion ln, final Quaternion scaled ) {

      Quaternion.log(a, ln);
      Quaternion.mul(ln, b, scaled);
      return Quaternion.exp(scaled, target);
   }

   /**
    * Creates a random unit quaternion. Uses an algorithm by Ken Shoemake,
    * reproduced at this Math Stack Exchange discussion:
    * <a href="https://math.stackexchange.com/q/131336">Uniform Random
    * Quaternion In a restricted angle range</a> .
    *
    * @param rng    the random number generator
    * @param target the output quaternion
    *
    * @return the random quaternion
    *
    * @author Ken Shoemake
    */
   public static Quaternion random ( final Random rng,
      final Quaternion target ) {

      /* @formatter:off */
      final double t0 = IUtils.TAU_D * rng.nextDouble();
      final double t1 = IUtils.TAU_D * rng.nextDouble();
      final double r1 = rng.nextDouble();
      final double x0 = Math.sqrt(1.0d - r1);
      final double x1 = Math.sqrt(r1);
      return target.set(
         ( float ) ( x0 * Math.sin(t0) ),
         ( float ) ( x0 * Math.cos(t0) ),
         ( float ) ( x1 * Math.sin(t1) ),
         ( float ) ( x1 * Math.cos(t1) ));
      /* @formatter:on */
   }

   /**
    * Rotates a vector around the x axis. Accepts pre-calculated sine and
    * cosine of half the angle so that collections of quaternions can be
    * efficiently rotated without repeatedly calling cos and sin.
    *
    * @param q      the input quaternion
    * @param cosah  cosine of half the angle
    * @param sinah  sine of half the angle
    * @param target the output quaternion
    *
    * @return the rotated quaternion
    */
   public static Quaternion rotateX ( final Quaternion q, final float cosah,
      final float sinah, final Quaternion target ) {

      final Vec3 i = q.imag;
      return target.set(cosah * q.real - sinah * i.x, cosah * i.x + sinah
         * q.real, cosah * i.y + sinah * i.z, cosah * i.z - sinah * i.y);
   }

   /**
    * Rotates a quaternion about the x axis by an angle. Do not use sequences
    * of orthonormal rotations by Euler angles; this will result in gimbal
    * lock, defeating the purpose behind a quaternion.
    *
    * @param q       the input quaternion
    * @param radians the angle in radians
    * @param target  the output quaternion
    *
    * @return the rotated quaternion
    *
    * @see Utils#modRadians(float)
    * @see Quaternion#rotateX(Quaternion, float, float, Quaternion)
    */
   public static Quaternion rotateX ( final Quaternion q, final float radians,
      final Quaternion target ) {

      // QUERY: Is radians % TAU better?
      final double halfAngle = Utils.modRadians(radians) * 0.5d;
      return Quaternion.rotateX(q, ( float ) Math.cos(halfAngle), ( float ) Math
         .sin(halfAngle), target);
   }

   /**
    * Rotates a vector around the y axis. Accepts pre-calculated sine and
    * cosine of half the angle so that collections of quaternions can be
    * efficiently rotated without repeatedly calling cos and sin.
    *
    * @param q      the input quaternion
    * @param cosah  cosine of half the angle
    * @param sinah  sine of half the angle
    * @param target the output quaternion
    *
    * @return the rotated quaternion
    */
   public static Quaternion rotateY ( final Quaternion q, final float cosah,
      final float sinah, final Quaternion target ) {

      final Vec3 i = q.imag;
      return target.set(cosah * q.real - sinah * i.y, cosah * i.x - sinah * i.z,
         cosah * i.y + sinah * q.real, cosah * i.z + sinah * i.x);
   }

   /**
    * Rotates a quaternion about the y axis by an angle. Do not use sequences
    * of orthonormal rotations by Euler angles; this will result in gimbal
    * lock, defeating the purpose behind a quaternion.
    *
    * @param q       the input quaternion
    * @param radians the angle in radians
    * @param target  the output quaternion
    *
    * @return the rotated quaternion
    *
    * @see Utils#modRadians(float)
    * @see Quaternion#rotateY(Quaternion, float, float, Quaternion)
    */
   public static Quaternion rotateY ( final Quaternion q, final float radians,
      final Quaternion target ) {

      final double halfAngle = Utils.modRadians(radians) * 0.5d;
      return Quaternion.rotateY(q, ( float ) Math.cos(halfAngle), ( float ) Math
         .sin(halfAngle), target);
   }

   /**
    * Rotates a vector around the z axis. Accepts calculated sine and cosine
    * of half the angle so that collections of quaternions can be efficiently
    * rotated without repeatedly calling cos and sin.
    *
    * @param q      the input quaternion
    * @param cosah  cosine of half the angle
    * @param sinah  sine of half the angle
    * @param target the output quaternion
    *
    * @return the rotated quaternion
    */
   public static Quaternion rotateZ ( final Quaternion q, final float cosah,
      final float sinah, final Quaternion target ) {

      final Vec3 i = q.imag;
      return target.set(cosah * q.real - sinah * i.z, cosah * i.x + sinah * i.y,
         cosah * i.y - sinah * i.x, cosah * i.z + sinah * q.real);
   }

   /**
    * Rotates a quaternion about the z axis by an angle. Do not use sequences
    * of orthonormal rotations by Euler angles; this will result in gimbal
    * lock, defeating the purpose behind a quaternion.
    *
    * @param q       the input quaternion
    * @param radians the angle in radians
    * @param target  the output quaternion
    *
    * @return the rotated quaternion
    *
    * @see Utils#modRadians(float)
    * @see Quaternion#rotateZ(Quaternion, float, float, Quaternion)
    */
   public static Quaternion rotateZ ( final Quaternion q, final float radians,
      final Quaternion target ) {

      final double halfAngle = Utils.modRadians(radians) * 0.5d;
      return Quaternion.rotateZ(q, ( float ) Math.cos(halfAngle), ( float ) Math
         .sin(halfAngle), target);
   }

   /**
    * Subtracts the right quaternion from the left.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output quaternion
    *
    * @return the difference
    *
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    */
   public static Quaternion sub ( final Quaternion a, final Quaternion b,
      final Quaternion target ) {

      Vec3.sub(a.imag, b.imag, target.imag);
      target.real = a.real - b.real;
      return target;
   }

   /**
    * Subtracts the right quaternion from the left and normalizes the
    * difference.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output quaternion
    *
    * @return the normalized difference
    *
    * @see Quaternion#identity(Quaternion)
    * @see Quaternion#magSq(Quaternion)
    * @see Utils#invSqrtUnchecked(float)
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    */
   public static Quaternion subNorm ( final Quaternion a, final Quaternion b,
      final Quaternion target ) {

      Vec3.sub(a.imag, b.imag, target.imag);
      target.real = a.real - b.real;

      final float mSq = Quaternion.magSq(target);
      if ( mSq > 0.0f ) {
         final float mInv = Utils.invSqrtUnchecked(mSq);
         final Vec3 i = target.imag;
         return target.set(target.real * mInv, i.x * mInv, i.y * mInv, i.z
            * mInv);
      }
      return Quaternion.identity(target);
   }

   /**
    * Subtracts the right quaternion from the left and normalizes the result.
    * Emits the difference as an output vector.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output quaternion
    * @param diff   the difference
    *
    * @return the normalized difference
    *
    * @see Quaternion#normalize(Quaternion, Quaternion)
    * @see Quaternion#sub(Quaternion, Quaternion, Quaternion)
    */
   public static Quaternion subNorm ( final Quaternion a, final Quaternion b,
      final Quaternion target, final Quaternion diff ) {

      Quaternion.sub(a, b, diff);
      return Quaternion.normalize(diff, target);
   }

   /**
    * Converts a quaternion to three axes, which in turn may constitute a
    * rotation matrix. Use this instead of
    * {@link Quaternion#getRight(Quaternion, Vec3)} ,
    * {@link Quaternion#getForward(Quaternion, Vec3)} and
    * {@link Quaternion#getUp(Quaternion, Vec3)} if all three axes are needed.
    *
    * @param q       the quaternion
    * @param right   the right axis
    * @param forward the forward axis
    * @param up      the up axis
    */
   public static void toAxes ( final Quaternion q, final Vec3 right,
      final Vec3 forward, final Vec3 up ) {

      final float w = q.real;
      final Vec3 i = q.imag;
      final float x = i.x;
      final float y = i.y;
      final float z = i.z;

      final float x2 = x + x;
      final float y2 = y + y;
      final float z2 = z + z;

      final float xsq2 = x * x2;
      final float ysq2 = y * y2;
      final float zsq2 = z * z2;

      final float xy2 = x * y2;
      final float xz2 = x * z2;
      final float yz2 = y * z2;

      final float wx2 = w * x2;
      final float wy2 = w * y2;
      final float wz2 = w * z2;

      right.set(1.0f - ysq2 - zsq2, xy2 + wz2, xz2 - wy2);
      forward.set(xy2 - wz2, 1.0f - xsq2 - zsq2, yz2 + wx2);
      up.set(xz2 + wy2, yz2 - wx2, 1.0f - xsq2 - ysq2);
   }

   /**
    * Converts a quaternion to an axis and angle. The angle is returned from
    * the function. The axis is assigned to an output vector.
    *
    * @param q    the quaternion
    * @param axis the output axis
    *
    * @return the angle
    *
    * @see Quaternion#magSq(Quaternion)
    * @see Utils#approx(float, float)
    * @see Utils#invSqrtUnchecked(float)
    * @see Vec3#forward(Vec3)
    * @see Utils#acos(float)
    */
   public static float toAxisAngle ( final Quaternion q, final Vec3 axis ) {

      final float mSq = Quaternion.magSq(q);

      if ( mSq <= 0.0f ) {
         Vec3.forward(axis);
         return 0.0f;
      }

      final float wNorm = Utils.approx(mSq, 1.0f) ? q.real : q.real * Utils
         .invSqrtUnchecked(mSq);

      final float angle = 2.0f * Utils.acos(wNorm);
      final float wAsin = IUtils.TAU - angle;
      // final float wAsin = IUtils.PI - angle;
      // final float wAsin = Utils.asin(wNorm);
      if ( wAsin == 0.0f ) {
         Vec3.forward(axis);
         return angle;
      }

      final float sInv = 1.0f / wAsin;
      final Vec3 i = q.imag;
      final float ax = i.x * sInv;
      final float ay = i.y * sInv;
      final float az = i.z * sInv;

      final float amSq = ax * ax + ay * ay + az * az;

      if ( amSq <= 0.0f ) {
         Vec3.forward(axis);
         return angle;
      }

      if ( Utils.approx(amSq, 1.0f, IUtils.EPSILON) ) {
         axis.set(ax, ay, az);
         return angle;
      }

      final float mInv = Utils.invSqrtUnchecked(amSq);
      axis.set(ax * mInv, ay * mInv, az * mInv);
      return angle;
   }

   /**
    * Creates a quaternion from three axes - either separate vectors or the
    * columns of a matrix. This is an internal helper function which uses only
    * the relevant information to create a quaternion. It does not normalize
    * the inputs.
    *
    * @param xRight   m00 : right x
    * @param yForward m11 : forward y
    * @param zUp      m22 : up z
    * @param zForward m21 : forward z
    * @param yUp      m12 : up y
    * @param xUp      m02 : up x
    * @param zRight   m20 : right z
    * @param yRight   m10 : right y
    * @param xForward m01 : forward x
    * @param target   the output quaternion
    *
    * @return the quaternion
    *
    * @see Utils#copySign(float, float)
    * @see Utils#sqrt(float)
    */
   static Quaternion fromAxes ( final float xRight, final float yForward,
      final float zUp, final float zForward, final float yUp, final float xUp,
      final float zRight, final float yRight, final float xForward,
      final Quaternion target ) {

      /*
       * Utilities square-root checks that input is greater than 0.
       * Double-precision functions do not seem to yield more accuracy.
       */

      /* @formatter:off */
      return target.set(
         0.5f * Utils.sqrt(1.0f + xRight + yForward + zUp),

         Utils.copySign(
            0.5f * Utils.sqrt(1.0f + xRight - yForward - zUp),
            zForward - yUp),
         Utils.copySign(
            0.5f * Utils.sqrt(1.0f - xRight + yForward - zUp),
            xUp - zRight),
         Utils.copySign(
            0.5f * Utils.sqrt(1.0f - xRight - yForward + zUp),
            yRight - xForward));
      /* @formatter:on */
   }

   /**
    * An internal helper method to mix quaternions together when no easing
    * function is provided. Does not check whether step is out of the range
    * [0.0, 1.0]. Reverses the dot product to find the shortest route.
    * Normalizes the result. Uses single precision methods over
    * {@link java.lang.Math}, and hence is less accurate.
    *
    * @param orig   the original quaternion
    * @param dest   the destination quaternion
    * @param step   the step
    * @param target the output quaternion
    *
    * @return the mix
    */
   static Quaternion slerpUnclamped ( final Quaternion orig,
      final Quaternion dest, final float step, final Quaternion target ) {

      /* Decompose origin quaternion. */
      final Vec3 ai = orig.imag;
      final float aw = orig.real;
      final float ax = ai.x;
      final float ay = ai.y;
      final float az = ai.z;

      /* Decompose destination quaternion. */
      final Vec3 bi = dest.imag;
      float bw = dest.real;
      float bx = bi.x;
      float by = bi.y;
      float bz = bi.z;

      /* Flip values if the orientation is negative, i.e., find near path. */
      float dotp = aw * bw + ax * bx + ay * by + az * bz;
      if ( dotp < 0.0f ) {
         bw = -bw;
         bx = -bx;
         by = -by;
         bz = -bz;
         dotp = -dotp;
      }

      /*
       * Find interpolation factor and its complement. Inverse square root and
       * arc-cosine should both check for invalid dot product.
       */
      float v = step;
      float u = 1.0f - v;

      final float sinTheta = Utils.invSqrt(1.0f - dotp * dotp);
      if ( sinTheta > IUtils.EPSILON ) {
         final float theta = Utils.acos(dotp);
         final float thetaStep = theta * step;
         u = sinTheta * ( float ) Math.sin(theta - thetaStep);
         v = sinTheta * ( float ) Math.sin(thetaStep);
      }

      /* Interpolate. */
      final float cw = u * aw + v * bw;
      final float cx = u * ax + v * bx;
      final float cy = u * ay + v * by;
      final float cz = u * az + v * bz;

      /* Normalize. */
      final float mSq = cw * cw + cx * cx + cy * cy + cz * cz;
      if ( mSq < IUtils.EPSILON ) { return target.reset(); }
      final float mInv = Utils.invSqrtUnchecked(mSq);
      return target.set(cw * mInv, cx * mInv, cy * mInv, cz * mInv);
   }

   /**
    * An abstract class that may serve as an umbrella for any custom
    * comparators of quaternions.
    */
   public abstract static class AbstrComparator implements Comparator <
      Quaternion > {

      /**
       * The default constructor.
       */
      protected AbstrComparator ( ) {}

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * An abstract class to facilitate the creation of quaternion easing
    * functions.
    */
   public abstract static class AbstrEasing implements Utils.EasingFuncObj <
      Quaternion > {

      /**
       * The default constructor.
       */
      protected AbstrEasing ( ) {}

      /**
       * A clamped interpolation between the origin and destination. Normalizes
       * the result even when the step is out of bounds. Defers to an unclamped
       * interpolation, which is to be defined by sub-classes of this class.
       *
       * @param origin the origin quaternion
       * @param dest   the destination quaternion
       * @param step   a factor in [0.0, 1.0]
       * @param target the output quaternion
       *
       * @return the eased quaternion
       *
       * @see Quaternion#normalize(Quaternion, Quaternion)
       */
      @Override
      public Quaternion apply ( final Quaternion origin, final Quaternion dest,
         final Float step, final Quaternion target ) {

         final float tf = step;
         if ( tf <= 0.0f ) { return Quaternion.normalize(origin, target); }
         if ( tf >= 1.0f ) { return Quaternion.normalize(dest, target); }
         return this.applyUnclamped(origin, dest, tf, target);
      }

      /**
       * The interpolation to be defined by subclasses.
       *
       * @param origin the origin quaternion
       * @param dest   the destination quaternion
       * @param step   a factor in [0.0, 1.0]
       * @param target the output quaternion
       *
       * @return the eased quaternion
       */
      public abstract Quaternion applyUnclamped ( final Quaternion origin,
         final Quaternion dest, final float step, final Quaternion target );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * A functional class to ease between two quaternions by linear
    * interpolation (lerp). The result is normalized following interpolation.
    */
   public static class Lerp extends AbstrEasing {

      /**
       * The default constructor.
       */
      public Lerp ( ) {}

      /**
       * Eases between the origin and destination quaternion by a step.
       * Normalizes the result.
       *
       * @param orig   the origin quaternion
       * @param dest   the destination quaternion
       * @param step   a factor in [0.0, 1.0]
       * @param target the output quaternion
       *
       * @return the eased quaternion
       *
       * @see Math#abs(double)
       * @see Math#sqrt(double)
       * @see Quaternion#identity(Quaternion)
       */
      @Override
      public Quaternion applyUnclamped ( final Quaternion orig,
         final Quaternion dest, final float step, final Quaternion target ) {

         final double u = step;
         final double v = 1.0d - u;

         final Vec3 a = orig.imag;
         final Vec3 b = dest.imag;

         final double cw = u * orig.real + v * dest.real;
         final double cx = u * a.x + v * b.x;
         final double cy = u * a.y + v * b.y;
         final double cz = u * a.z + v * b.z;

         /* Normalize. */
         final double mSq = cw * cw + cx * cx + cy * cy + cz * cz;

         if ( mSq < IUtils.EPSILON_D ) { return Quaternion.identity(target); }

         final double mInv = 1.0d / Math.sqrt(mSq);
         return target.set(( float ) ( cw * mInv ), ( float ) ( cx * mInv ),
            ( float ) ( cy * mInv ), ( float ) ( cz * mInv ));
      }

   }

   /**
    * A functional class to ease between two quaternions by spherical linear
    * interpolation (slerp). This chooses the shortest path between two
    * orientations and maintains constant speed for a step given in [0.0, 1.0]
    * . The result is normalized following interpolation.
    */
   public static class Slerp extends AbstrEasing {

      /**
       * The default constructor.
       */
      public Slerp ( ) {}

      /**
       * Eases between two quaternions by a step.
       *
       * @param orig   the origin quaternion
       * @param dest   the destination quaternion
       * @param step   a factor
       * @param target the output quaternion
       *
       * @return the eased quaternion
       *
       * @see Math#acos(double)
       * @see Math#sqrt(double)
       * @see Math#sin(double)
       * @see Math#abs(double)
       * @see Quaternion#identity(Quaternion)
       */
      @Override
      public Quaternion applyUnclamped ( final Quaternion orig,
         final Quaternion dest, final float step, final Quaternion target ) {

         /* Decompose origin quaternion. */
         final Vec3 ai = orig.imag;
         final double aw = orig.real;
         final double ax = ai.x;
         final double ay = ai.y;
         final double az = ai.z;

         /* Decompose destination quaternion. */
         final Vec3 bi = dest.imag;
         double bw = dest.real;
         double bx = bi.x;
         double by = bi.y;
         double bz = bi.z;

         double dotp = aw * bw + ax * bx + ay * by + az * bz;
         if ( dotp < -1.0d ) {
            dotp = -1.0d;
         } else if ( dotp > 1.0d ) { dotp = 1.0d; }

         /* Flip values if the orientation is negative. */
         if ( dotp < 0.0d ) {
            bw = -bw;
            bx = -bx;
            by = -by;
            bz = -bz;
            dotp = -dotp; // in [0, 1]
         }

         /* The step and its complement. */
         double v = step;
         double u = 1.0d - v;

         final double sinTheta = Math.sqrt(1.0d - dotp * dotp); // in [1, 0].
         if ( sinTheta > IUtils.EPSILON_D ) {
            final double theta = Math.acos(dotp);
            final double sInv = 1.0d / sinTheta;
            final double thetaStep = theta * v;
            u = sInv * Math.sin(theta - thetaStep);
            v = sInv * Math.sin(thetaStep);
         }

         /* Unclamped linear interpolation. */
         final double cw = u * aw + v * bw;
         final double cx = u * ax + v * bx;
         final double cy = u * ay + v * by;
         final double cz = u * az + v * bz;

         /* Find magnitude squared. */
         final double mSq = cw * cw + cx * cx + cy * cy + cz * cz;

         /* Magnitude is approximately zero. */
         if ( mSq < IUtils.EPSILON_D ) { return Quaternion.identity(target); }

         /* Magnitude is approximately one. */
         if ( Math.abs(1.0d - mSq) < IUtils.EPSILON_D ) {
            return target.set(( float ) cw, ( float ) cx, ( float ) cy,
               ( float ) cz);
         }

         /* Normalize. */
         final double mInv = 1.0d / Math.sqrt(mSq);
         return target.set(( float ) ( cw * mInv ), ( float ) ( cx * mInv ),
            ( float ) ( cy * mInv ), ( float ) ( cz * mInv ));
      }

   }

}
