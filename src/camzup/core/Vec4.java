package camzup.core;

import java.util.Comparator;
import java.util.Iterator;

/**
 * A mutable, extensible class influenced by GLSL. This is intended to
 * serve as a parent class for colors. Instance methods are limited,
 * while most static methods require an explicit output variable to be
 * provided.
 */
public class Vec4 extends Vec implements Comparable < Vec4 > {

  /**
   * An abstract class that may serve as an umbrella for any custom
   * comparators of Vec4 s.
   */
  public static abstract class AbstrComparator implements Comparator < Vec4 > {

    /**
     * The default constructor.
     */
    public AbstrComparator ( ) {

    }

    /**
     * The compare function which must be implemented by sub- (child)
     * classes of this class. Negative one should be returned when the
     * left comparisand, a, is less than the right comparisand, b, by a
     * measure. One should be returned when it is greater. Zero should be
     * returned as a last resort, when a and b are equal or incomparable.
     *
     * @param a the left comparisand
     * @param b the right comparisand
     * @return the comparison
     *
     */
    @Override
    public abstract int compare ( final Vec4 a, final Vec4 b );

    /**
     * Returns the simple name of this class.
     *
     * @return the string
     */
    @Override
    public String toString ( ) {

      return this.getClass().getSimpleName();
    }
  }

  /**
   * An iterator, which allows a vector's components to be accessed in
   * an enhanced for loop.
   */
  public static final class V4Iterator implements Iterator < Float > {

    /**
     * The current index.
     */
    private int index = 0;

    /**
     * The vector being iterated over.
     */
    private final Vec4 vec;

    /**
     * The default constructor.
     *
     * @param vec the vector to iterate
     */
    public V4Iterator ( final Vec4 vec ) {

      this.vec = vec;
    }

    /**
     * Tests to see if the iterator has another value.
     *
     * @return the evaluation
     */
    @Override
    public boolean hasNext ( ) {

      return this.index < this.vec.length();
    }

    /**
     * Gets the next value in the iterator.
     *
     * @see Vec4#get(int)
     * @return the value
     */
    @Override
    public Float next ( ) {

      return this.vec.get(this.index++);
    }

    /**
     * Returns the simple name of this class.
     *
     * @return the string
     */
    @Override
    public String toString ( ) {

      return this.getClass().getSimpleName();
    }
  }

  /**
   * The unique identification for serialized classes.
   */
  private static final long serialVersionUID = -7601802836396728054L;

  /**
   * Component on the w axis. Commonly used to store 1.0 for points and
   * 0.0 for vectors when multiplying with a 4 x 4 matrix. Also used to
   * store alpha (transparency) for colors.
   */
  public float w = 0.0f;

  /**
   * Component on the x axis.
   */
  public float x = 0.0f;

  /**
   * Component on the y axis.
   */
  public float y = 0.0f;

  /**
   * Component on the z axis.
   */
  public float z = 0.0f;

  /**
   * The default vector constructor.
   */
  public Vec4 ( ) {

    super(4);
  }

  /**
   * Constructs a vector from boolean values.
   *
   * @param x the x component
   * @param y the y component
   * @param z the z component
   * @param w the w component
   */
  public Vec4 (
      final boolean x,
      final boolean y,
      final boolean z,
      final boolean w ) {

    super(4);
    this.set(x, y, z, w);
  }

  /**
   * Constructs a vector from float values.
   *
   * @param x the x component
   * @param y the y component
   * @param z the z component
   * @param w the w component
   */
  public Vec4 (
      final float x,
      final float y,
      final float z,
      final float w ) {

    super(4);
    this.set(x, y, z, w);
  }

  /**
   * Attempts to construct a vector from Strings using
   * {@link Float#parseFloat(String)} . If a NumberFormatException is
   * thrown, the component is set to zero.
   *
   * @param xstr the x string
   * @param ystr the y string
   * @param zstr the z string
   * @param wstr the w string
   * @see Float#parseFloat(String)
   */
  public Vec4 (
      final String xstr,
      final String ystr,
      final String zstr,
      final String wstr ) {

    super(4);
    this.set(xstr, ystr, zstr, wstr);
  }

  /**
   * Promotes a Vec2 to a Vec4.
   *
   * @param v2 the vector
   */
  public Vec4 ( final Vec2 v2 ) {

    super(4);
    this.set(v2);
  }

  /**
   * Promotes a Vec3 to a Vec4.
   *
   * @param v3 the vector
   */
  public Vec4 ( final Vec3 v3 ) {

    super(4);
    this.set(v3);
  }

  /**
   * Promotes a Vec3 to a Vec4 with an extra component.
   *
   * @param v3 the vector
   * @param w  the w component
   */
  public Vec4 ( final Vec3 v3, final float w ) {

    super(4);
    this.set(v3, w);
  }

  /**
   * Constructs a vector from a source vector's components.
   *
   * @param source the source vector
   */
  public Vec4 ( final Vec4 source ) {

    super(4);
    this.set(source);
  }

  /**
   * Tests equivalence between this and another vector.
   *
   * @param v the vector
   * @return the evaluation
   * @see Float#floatToIntBits(float)
   */
  protected boolean equals ( final Vec4 v ) {

    return Float.floatToIntBits(this.w) == Float.floatToIntBits(v.w)
        && Float.floatToIntBits(this.z) == Float.floatToIntBits(v.z)
        && Float.floatToIntBits(this.y) == Float.floatToIntBits(v.y)
        && Float.floatToIntBits(this.x) == Float.floatToIntBits(v.x);
  }

  /**
   * Returns a new vector with this vector's components. Java's
   * cloneable interface is problematic; use set or a copy constructor
   * instead.
   *
   * @return a new vector
   * @see Vec4#set(Vec4)
   * @see Vec4#Vec4(Vec4)
   */
  @Override
  public Vec4 clone ( ) {

    return new Vec4(
        this.x,
        this.y,
        this.z,
        this.w);
  }

  /**
   * Returns -1 when this vector is less than the comparisand; 1 when it
   * is greater than; 0 when the two are 'equal'. The implementation of
   * this method allows collections of vectors to be sorted.
   *
   * @param v the comparisand
   * @return the numeric code
   */
  @Override
  public int compareTo ( final Vec4 v ) {

    return this.w > v.w ? 1
        : this.w < v.w ? -1
            : this.z > v.z ? 1
                : this.z < v.z ? -1
                    : this.y > v.y ? 1
                        : this.y < v.y ? -1
                            : this.x > v.x ? 1
                                : this.x < v.x ? -1 : 0;
  }

  /**
   * Tests this vector for equivalence with another object.
   *
   * @param obj the object
   * @return the equivalence
   * @see Vec4#equals(Vec4)
   */
  @Override
  public boolean equals ( final Object obj ) {

    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (this.getClass() != obj.getClass()) { return false; }
    return this.equals((Vec4) obj);
  }

  /**
   * Simulates bracket subscript access in an array. When the provided
   * index is 3 or -1, returns w; 2 or -2, z; 1 or -3, y; 0 or -4, x.
   *
   * @param index the index
   * @return the component at that index
   */
  @Override
  public float get ( final int index ) {

    switch (index) {
      case 0:
      case -4:
        return this.x;
      case 1:
      case -3:
        return this.y;
      case 2:
      case -2:
        return this.z;
      case 3:
      case -1:
        return this.w;
      default:
        return 0.0f;
    }
  }

  /**
   * Returns a hash code for this vector based on its x, y, z and w
   * components.
   *
   * @return the hash code
   * @see Float#floatToIntBits(float)
   */
  @Override
  public int hashCode ( ) {

    return (((IUtils.MUL_BASE ^ Float.floatToIntBits(this.x))
        * IUtils.HASH_MUL ^ Float.floatToIntBits(this.y))
        * IUtils.HASH_MUL ^ Float.floatToIntBits(this.z))
        * IUtils.HASH_MUL ^ Float.floatToIntBits(this.w);
  }

  /**
   * Returns an iterator for this vector, which allows its components to
   * be accessed in an enhanced for-loop.
   *
   * @return the iterator
   */
  @Override
  public V4Iterator iterator ( ) {

    return new V4Iterator(this);
  }

  /**
   * Resets this vector to an initial state, ( 0.0, 0.0, 0.0, 0.0 ) .
   *
   * @return this vector
   */
  @Chainable
  public Vec4 reset ( ) {

    return this.set(0.0f, 0.0f, 0.0f, 0.0f);
  }

  /**
   * Sets the components of this vector from booleans, where false is
   * 0.0 and true is 1.0 .
   *
   * @param x the x component
   * @param y the y component
   * @param z the z component
   * @param w the w component
   * @return this vector
   * @see Utils#toFloat(boolean)
   */
  @Chainable
  public Vec4 set (
      final boolean x,
      final boolean y,
      final boolean z,
      final boolean w ) {

    this.x = Utils.toFloat(x);
    this.y = Utils.toFloat(y);
    this.z = Utils.toFloat(z);
    this.w = Utils.toFloat(w);
    return this;
  }

  /**
   * Sets the components of this vector.
   *
   * @param x the x component
   * @param y the y component
   * @param z the z component
   * @param w the w component
   * @return this vector
   */
  @Chainable
  public Vec4 set (
      final float x,
      final float y,
      final float z,
      final float w ) {

    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
    return this;
  }

  /**
   * Attempts to set the components of this vector from Strings using
   * {@link Float#parseFloat(String)} . If a NumberFormatException is
   * thrown, the component is set to zero.
   *
   * @param xstr the x string
   * @param ystr the y string
   * @param zstr the z string
   * @param wstr the w string
   * @return this vector
   * @see Float#parseFloat(String)
   */
  @Chainable
  public Vec4 set (
      final String xstr,
      final String ystr,
      final String zstr,
      final String wstr ) {

    float x = 0.0f;
    float y = 0.0f;
    float z = 0.0f;
    float w = 0.0f;

    try {
      x = Float.parseFloat(xstr);
    } catch (final NumberFormatException e) {
      x = 0.0f;
    }

    try {
      y = Float.parseFloat(ystr);
    } catch (final NumberFormatException e) {
      y = 0.0f;
    }

    try {
      z = Float.parseFloat(zstr);
    } catch (final NumberFormatException e) {
      z = 0.0f;
    }

    try {
      w = Float.parseFloat(wstr);
    } catch (final NumberFormatException e) {
      w = 0.0f;
    }

    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;

    return this;
  }

  /**
   * Promotes a Vec2 to a Vec4.
   *
   * @param v2 the vector
   * @return this vector
   */
  @Chainable
  public Vec4 set ( final Vec2 v2 ) {

    return this.set(v2.x, v2.y, 0.0f, 0.0f);
  }

  /**
   * Promotes a Vec3 to a Vec4.
   *
   * @param v3 the vector
   * @return this vector
   */
  @Chainable
  public Vec4 set ( final Vec3 v3 ) {

    return this.set(v3.x, v3.y, v3.z, 0.0f);
  }

  /**
   * Promotes a Vec3 to a Vec4 with an extra component.
   *
   * @param v3 the vector
   * @param w  the w component
   * @return this vector
   */
  @Chainable
  public Vec4 set ( final Vec3 v3, final float w ) {

    return this.set(v3.x, v3.y, v3.z, w);
  }

  /**
   * Copies the components of the input vector to this vector.
   *
   * @param source the input vector
   * @return this vector
   */
  @Chainable
  public Vec4 set ( final Vec4 source ) {

    return this.set(
        source.x,
        source.y,
        source.z,
        source.w);
  }

  /**
   * Returns a float array of length 4 containing this vector's
   * components.
   *
   * @return the array
   */
  @Override
  public float[] toArray ( ) {

    return new float[] { this.x, this.y, this.z, this.w };
  }

  /**
   * Returns a string representation of this vector according to the
   * string format.
   *
   * @return the string
   */
  @Override
  public String toString ( ) {

    return this.toString(4);
  }

  /**
   * Returns a string representation of this vector.
   *
   * @param places number of decimal places
   * @return the string
   */
  public String toString ( final int places ) {

    return new StringBuilder(96)
        .append("{ x: ")
        .append(Utils.toFixed(this.x, places))
        .append(", y: ")
        .append(Utils.toFixed(this.y, places))
        .append(", z: ")
        .append(Utils.toFixed(this.z, places))
        .append(", z: ")
        .append(Utils.toFixed(this.w, places))
        .append(' ').append('}')
        .toString();
  }

  /**
   * Finds the absolute value of each vector component.
   *
   * @param v      the input vector
   * @param target the output vector
   * @return the absolute vector
   * @see Utils#abs(float)
   */
  public static Vec4 abs (
      final Vec4 v,
      final Vec4 target ) {

    return target.set(
        Utils.abs(v.x),
        Utils.abs(v.y),
        Utils.abs(v.z),
        Utils.abs(v.w));
  }

  /**
   * Adds two vectors together.
   *
   * @param a      left operand
   * @param b      right operand
   * @param target the output vector
   * @return the sum
   */
  public static Vec4 add (
      final Vec4 a,
      final Vec4 b,
      final Vec4 target ) {

    return target.set(
        a.x + b.x,
        a.y + b.y,
        a.z + b.z,
        a.w + b.w);
  }

  /**
   * Tests to see if all the vector's components are non-zero. Useful
   * when testing valid dimensions (width and depth) stored in vectors.
   *
   * @param v the input vector
   * @return the evaluation
   */
  public static boolean all ( final Vec4 v ) {

    return v.w != 0.0f &&
        v.z != 0.0f &&
        v.y != 0.0f &&
        v.x != 0.0f;
  }

  /**
   * Evaluates two vectors like booleans, using the AND logic gate.
   *
   * @param a      left operand
   * @param b      right operand
   * @param target the output vector
   * @return the evaluation
   * @see Utils#and(float, float)
   */
  public static Vec4 and (
      final Vec4 a,
      final Vec4 b,
      final Vec4 target ) {

    return target.set(
        Utils.and(a.x, b.x),
        Utils.and(a.y, b.y),
        Utils.and(a.z, b.z),
        Utils.and(a.w, b.w));
  }

  /**
   * Tests to see if any of the vector's components are non-zero.
   *
   * @param v the input vector
   * @return the evaluation
   */
  public static boolean any ( final Vec4 v ) {

    return v.w != 0.0f ||
        v.z != 0.0f ||
        v.y != 0.0f ||
        v.x != 0.0f;
  }

  /**
   * Tests to see if two vectors approximate each other.
   *
   * @param a left comparisand
   * @param b right comparisand
   * @return the evaluation
   * @see Utils#approx(float, float)
   */
  public static boolean approx (
      final Vec4 a,
      final Vec4 b ) {

    return Utils.approx(a.w, b.w)
        && Utils.approx(a.z, b.z)
        && Utils.approx(a.y, b.y)
        && Utils.approx(a.x, b.x);
  }

  /**
   * Tests to see if two vectors approximate each other.
   *
   * @param a         left operand
   * @param b         right operand
   * @param tolerance the tolerance
   * @return the evaluation
   * @see Utils#approx(float, float, float)
   */
  public static boolean approx (
      final Vec4 a,
      final Vec4 b,
      final float tolerance ) {

    return Utils.approx(a.w, b.w, tolerance)
        && Utils.approx(a.z, b.z, tolerance)
        && Utils.approx(a.y, b.y, tolerance)
        && Utils.approx(a.x, b.x, tolerance);
  }

  /**
   * Tests to see if a vector has, approximately, the specified
   * magnitude according to the default EPSILON.
   *
   * @param a the input vector
   * @param b the magnitude
   * @return the evaluation
   * @see Utils#approx(float, float)
   * @see Vec4#dot(Vec4, Vec4)
   */
  public static boolean approxMag (
      final Vec4 a,
      final float b ) {

    return Utils.approx(Vec4.magSq(a), b * b);
  }

  /**
   * Tests to see if a vector has, approximately, the specified
   * magnitude.
   *
   * @param a         the input vector
   * @param b         the magnitude
   * @param tolerance the tolerance
   * @return the evaluation
   * @see Utils#approx(float, float, float)
   * @see Vec4#dot(Vec4, Vec4)
   */
  public static boolean approxMag (
      final Vec4 a,
      final float b,
      final float tolerance ) {

    return Utils.approx(Vec4.magSq(a), b * b, tolerance);
  }

  /**
   * Returns to a vector with a negative value on the y axis, (0.0,
   * -1.0, 0.0, 0.0) .
   *
   * @param target the output vector
   * @return the back vector
   */
  public static Vec4 back ( final Vec4 target ) {

    return target.set(0.0f, -1.0f, 0.0f, 0.0f);
  }

  /**
   * Raises each component of the vector to the nearest greater integer.
   *
   * @param v      the input vector
   * @param target the output vector
   * @return the ceil
   * @see Utils#ceil(float)
   */
  public static Vec4 ceil (
      final Vec4 v,
      final Vec4 target ) {

    return target.set(
        Utils.ceil(v.x),
        Utils.ceil(v.y),
        Utils.ceil(v.z),
        Utils.ceil(v.w));
  }

  /**
   * Clamps a vector to a range within the lower- and upper-bound.
   *
   * @param v          the input vector
   * @param lowerBound the lower bound of the range
   * @param upperBound the upper bound of the range
   * @param target     the output vector
   * @return the clamped vector
   * @see Utils#clamp(float, float, float)
   */
  public static Vec4 clamp (
      final Vec4 v,
      final Vec4 lowerBound,
      final Vec4 upperBound,
      final Vec4 target ) {

    return target.set(
        Utils.clamp(v.x, lowerBound.x, upperBound.x),
        Utils.clamp(v.y, lowerBound.y, upperBound.y),
        Utils.clamp(v.z, lowerBound.z, upperBound.z),
        Utils.clamp(v.w, lowerBound.w, upperBound.w));
  }

  /**
   * Clamps the vector to a range in [0.0, 1.0].
   *
   * @param v      the input vector
   * @param target the output vector
   * @return the clamped vector
   * @see Utils#clamp01(float)
   */
  public static Vec4 clamp01 (
      final Vec4 v,
      final Vec4 target ) {

    return target.set(
        Utils.clamp01(v.x),
        Utils.clamp01(v.y),
        Utils.clamp01(v.z),
        Utils.clamp01(v.w));
  }

  /**
   * Finds first vector argument with the sign of the second vector
   * argument.
   *
   * @param magnitude the magnitude
   * @param sign      the sign
   * @param target    the output vector
   * @return the signed vector
   */
  public static Vec4 copySign (
      final Vec4 magnitude,
      final Vec4 sign,
      final Vec4 target ) {

    return target.set(
        Math.copySign(magnitude.x, sign.x),
        Math.copySign(magnitude.y, sign.y),
        Math.copySign(magnitude.z, sign.z),
        Math.copySign(magnitude.w, sign.w));
  }

  /**
   * Finds the absolute value of the difference between two vectors.
   *
   * @param a      left operand
   * @param b      right operand
   * @param target the output vector
   * @return the absolute difference
   * @see Utils#diff(float, float)
   */
  public static Vec4 diff (
      final Vec4 a,
      final Vec4 b,
      final Vec4 target ) {

    return target.set(
        Utils.diff(a.x, b.x),
        Utils.diff(a.y, b.y),
        Utils.diff(a.z, b.z),
        Utils.diff(a.w, b.w));
  }

  /**
   * Divides a scalar by a vector.
   *
   * @param a      scalar, numerator
   * @param b      vector, denominator
   * @param target the output vector
   * @return the quotient
   * @see Utils#div(float, float)
   */
  public static Vec4 div (
      final float a,
      final Vec4 b,
      final Vec4 target ) {

    return target.set(
        Utils.div(a, b.x),
        Utils.div(a, b.y),
        Utils.div(a, b.z),
        Utils.div(a, b.w));
  }

  /**
   * Divides a vector by a scalar.
   *
   * @param a      vector, numerator
   * @param b      scalar, denominator
   * @param target the output vector
   * @return the quotient
   */
  public static Vec4 div (
      final Vec4 a,
      final float b,
      final Vec4 target ) {

    if (b == 0.0f) { return target.reset(); }

    final float denom = 1.0f / b;
    return target.set(
        a.x * denom,
        a.y * denom,
        a.z * denom,
        a.w * denom);
  }

  /**
   * Divides the left operand by the right, component-wise.
   *
   * @param a      numerator
   * @param b      denominator
   * @param target the output vector
   * @return the quotient
   * @see Utils#div(float, float)
   */
  public static Vec4 div (
      final Vec4 a,
      final Vec4 b,
      final Vec4 target ) {

    return target.set(
        Utils.div(a.x, b.x),
        Utils.div(a.y, b.y),
        Utils.div(a.z, b.z),
        Utils.div(a.w, b.w));
  }

  /**
   * Finds the dot product of two vectors by summing the products of
   * their corresponding components. <em>a</em> \u00b7 <em>b</em> :=
   * <em>a<sub>x</sub> b<sub>x</sub></em> + <em>a<sub>y</sub>
   * b<sub>y</sub></em> + <em>a<sub>z</sub> b<sub>z</sub></em> +
   * <em>a<sub>w</sub> b<sub>w</sub></em><br>
   * <br>
   * The dot product of a vector with itself is equal to its magnitude
   * squared.
   *
   * @param a left operand
   * @param b right operand
   * @return the dot product
   */
  public static float dot (
      final Vec4 a,
      final Vec4 b ) {

    return a.x * b.x +
        a.y * b.y +
        a.z * b.z +
        a.w * b.w;
  }

  /**
   * Returns to a vector with a negative value on the z axis, (0.0, 0.0,
   * -1.0, 0.0) .
   *
   * @param target the output vector
   * @return the down vector
   */
  public static Vec4 down ( final Vec4 target ) {

    return target.set(0.0f, 0.0f, -1.0f, 0.0f);
  }

  /**
   * Filters a vector by setting each component to the input component
   * if it is in bounds and 0.0 if it is out of bounds.
   *
   * @param v      the vector
   * @param lb     the lower bound
   * @param ub     the upper bound
   * @param target the output vector
   * @return the filtered vector
   */
  public static Vec4 filter (
      final Vec4 v,
      final Vec4 lb,
      final Vec4 ub,
      final Vec4 target ) {

    return target.set(
        Utils.filter(v.x, lb.x, ub.x),
        Utils.filter(v.y, lb.y, ub.y),
        Utils.filter(v.z, lb.z, ub.z),
        Utils.filter(v.w, lb.w, ub.w));
  }

  /**
   * Floors each component of the vector.
   *
   * @param v      the input vector
   * @param target the output vector
   * @return the floor
   * @see Utils#floor(float)
   */
  public static Vec4 floor (
      final Vec4 v,
      final Vec4 target ) {

    return target.set(
        Utils.floor(v.x),
        Utils.floor(v.y),
        Utils.floor(v.z),
        Utils.floor(v.w));
  }

  /**
   * Applies the % operator (truncation-based modulo) to the left
   * operand.
   *
   * @param a      left operand
   * @param b      right operand
   * @param target the output vector
   * @return the result
   * @see Utils#fmod(float, float)
   */
  public static Vec4 fmod (
      final float a,
      final Vec4 b,
      final Vec4 target ) {

    return target.set(
        Utils.fmod(a, b.x),
        Utils.fmod(a, b.y),
        Utils.fmod(a, b.z),
        Utils.fmod(a, b.w));
  }

  /**
   * Applies the % operator (truncation-based modulo) to each component
   * of the left operand.
   *
   * @param a      left operand
   * @param b      right operand
   * @param target the output vector
   * @return the result
   */
  public static Vec4 fmod (
      final Vec4 a,
      final float b,
      final Vec4 target ) {

    if (b == 0.0f) { return target.set(a); }

    return target.set(
        a.x % b,
        a.y % b,
        a.z % b,
        a.w % b);
  }

  /**
   * Applies the % operator (truncation-based modulo) to each component
   * of the left operand.
   *
   * @param a      left operand
   * @param b      right operand
   * @param target the output vector
   * @return the result
   * @see Utils#fmod(float, float)
   */
  public static Vec4 fmod (
      final Vec4 a,
      final Vec4 b,
      final Vec4 target ) {

    return target.set(
        Utils.fmod(a.x, b.x),
        Utils.fmod(a.y, b.y),
        Utils.fmod(a.z, b.z),
        Utils.fmod(a.w, b.w));
  }

  /**
   * Returns to a vector with a positive value on the y axis, (0.0, 1.0,
   * 0.0, 0.0) .
   *
   * @param target the output vector
   * @return the forward vector
   */
  public static Vec4 forward ( final Vec4 target ) {

    return target.set(0.0f, 1.0f, 0.0f, 0.0f);
  }

  /**
   * Returns the fractional portion of the vector's components.
   *
   * @param v      the input vector
   * @param target the output vector
   * @return the fractional portion
   * @see Utils#fract(float)
   */
  public static Vec4 fract (
      final Vec4 v,
      final Vec4 target ) {

    return target.set(
        Utils.fract(v.x),
        Utils.fract(v.y),
        Utils.fract(v.z),
        Utils.fract(v.w));
  }

  /**
   * Tests to see if the vector is on the unit hyper-sphere, i.e., has a
   * magnitude of approximately 1.0.
   *
   * @param v the input vector
   * @return the evaluation
   * @see Utils#approx(float, float)
   * @see Vec4#dot(Vec4, Vec4)
   */
  public static boolean isUnit ( final Vec4 v ) {

    return Utils.approx(Vec4.magSq(v), 1.0f);
  }

  /**
   * Returns a vector with a negative value on the x axis, (-1.0, 0.0,
   * 0.0, 0.0).
   *
   * @param target the output vector
   * @return the left vector
   */
  public static Vec4 left ( final Vec4 target ) {

    return target.set(-1.0f, 0.0f, 0.0f, 0.0f);
  }

  /**
   * Finds the length, or magnitude, of a vector, |<em>a</em>| . Uses
   * the formula \u221a <em>a</em> \u00b7 <em>a</em> . Where possible,
   * use magSq or dot to avoid the computational cost of the
   * square-root.
   *
   * @param v the input vector
   * @return the magnitude
   * @see Vec4#dot(Vec4, Vec4)
   * @see Math#sqrt(double)
   * @see Vec4#magSq(Vec4)
   */
  public static float mag ( final Vec4 v ) {

    return Utils.sqrtUnchecked(
        v.x * v.x +
            v.y * v.y +
            v.z * v.z +
            v.w * v.w);
  }

  /**
   * Finds the length-, or magnitude-, squared of a vector,
   * |<em>a</em>|<sup>2</sup>. Returns the same result as <em>a</em>
   * \u00b7 <em>a</em> . Useful when calculating the lengths of many
   * vectors, so as to avoid the computational cost of the square-root.
   *
   * @param v the input vector
   * @return the magnitude squared
   * @see Vec4#dot(Vec4, Vec4)
   * @see Vec4#mag(Vec4)
   */
  public static float magSq ( final Vec4 v ) {

    return v.x * v.x + v.y * v.y + v.z * v.z + v.w * v.w;
  }

  /**
   * Maps an input vector from an original range to a target range.
   *
   * @param v        the input vector
   * @param lbOrigin lower bound of original range
   * @param ubOrigin upper bound of original range
   * @param lbDest   lower bound of destination range
   * @param ubDest   upper bound of destination range
   * @param target   the output vector
   * @return the mapped value
   * @see Utils#map(float, float, float, float, float)
   */
  public static Vec4 map (
      final Vec4 v,
      final Vec4 lbOrigin,
      final Vec4 ubOrigin,
      final Vec4 lbDest,
      final Vec4 ubDest,
      final Vec4 target ) {

    return target.set(
        Utils.map(v.x, lbOrigin.x, ubOrigin.x, lbDest.x, ubDest.x),
        Utils.map(v.y, lbOrigin.y, ubOrigin.y, lbDest.y, ubDest.y),
        Utils.map(v.z, lbOrigin.z, ubOrigin.z, lbDest.z, ubDest.z),
        Utils.map(v.w, lbOrigin.w, ubOrigin.w, lbDest.w, ubDest.w));
  }

  /**
   * Sets the target vector to the maximum of the input vector and an
   * upper bound.
   *
   * @param a          the input value
   * @param upperBound the upper bound
   * @param target     the output vector
   * @return the maximum values
   */
  public static Vec4 max (
      final Vec4 a,
      final float upperBound,
      final Vec4 target ) {

    return target.set(
        Utils.max(a.x, upperBound),
        Utils.max(a.y, upperBound),
        Utils.max(a.z, upperBound),
        Utils.max(a.w, upperBound));
  }

  /**
   * Sets the target vector to the maximum components of the input
   * vector and a upper bound.
   *
   * @param a          the input vector
   * @param upperBound the upper bound
   * @param target     the output vector
   * @return the maximum values
   * @see Utils#max(float, float)
   */
  public static Vec4 max (
      final Vec4 a,
      final Vec4 upperBound,
      final Vec4 target ) {

    return target.set(
        Utils.max(a.x, upperBound.x),
        Utils.max(a.y, upperBound.y),
        Utils.max(a.z, upperBound.z),
        Utils.max(a.w, upperBound.w));
  }

  /**
   * Sets the target vector to the minimum components of the input
   * vector and a lower bound.
   *
   * @param a          the input value
   * @param lowerBound the lower bound
   * @param target     the output vector
   * @return the minimum values
   */
  public static Vec4 min (
      final Vec4 a,
      final float lowerBound,
      final Vec4 target ) {

    return target.set(
        Utils.min(a.x, lowerBound),
        Utils.min(a.y, lowerBound),
        Utils.min(a.z, lowerBound),
        Utils.min(a.w, lowerBound));
  }

  /**
   * Sets the target vector to the minimum components of the input
   * vector and a lower bound.
   *
   * @param a          the input vector
   * @param lowerBound the lower bound
   * @param target     the output vector
   * @return the minimal values
   * @see Utils#min(float, float)
   */
  public static Vec4 min (
      final Vec4 a,
      final Vec4 lowerBound,
      final Vec4 target ) {

    return target.set(
        Utils.min(a.x, lowerBound.x),
        Utils.min(a.y, lowerBound.y),
        Utils.min(a.z, lowerBound.z),
        Utils.min(a.w, lowerBound.w));
  }

  /**
   * Wraps a scalar by each component of a vector.
   *
   * @param a      the scalar
   * @param b      the vector
   * @param target the output vector
   * @return the modulated vector
   * @see Utils#mod(float, float)
   */
  public static Vec4 mod (
      final float a,
      final Vec4 b,
      final Vec4 target ) {

    return target.set(
        Utils.mod(a, b.x),
        Utils.mod(a, b.y),
        Utils.mod(a, b.z),
        Utils.mod(a, b.w));
  }

  /**
   * Wraps each component of a vector by a scalar
   *
   * @param a      the vector
   * @param b      the scalar
   * @param target the output vector
   * @return the modulated vector
   * @see Utils#modUnchecked(float, float)
   */
  public static Vec4 mod (
      final Vec4 a,
      final float b,
      final Vec4 target ) {

    if (b == 0.0f) { return target.set(a); }

    return target.set(
        Utils.modUnchecked(a.x, b),
        Utils.modUnchecked(a.y, b),
        Utils.modUnchecked(a.z, b),
        Utils.modUnchecked(a.w, b));
  }

  /**
   * Wraps each component of the left vector by those of the right.
   *
   * @param a      left operand
   * @param b      right operand
   * @param target the output vector
   * @return the modulated vector
   * @see Utils#mod(float, float)
   */
  public static Vec4 mod (
      final Vec4 a,
      final Vec4 b,
      final Vec4 target ) {

    return target.set(
        Utils.mod(a.x, b.x),
        Utils.mod(a.y, b.y),
        Utils.mod(a.z, b.z),
        Utils.mod(a.w, b.w));
  }

  /**
   * A specialized form of modulo which subtracts the floor of the
   * vector from the vector.
   *
   * @param v      the input vector
   * @param target the output vector
   * @return the result
   * @see Utils#mod1(float)
   */
  public static Vec4 mod1 (
      final Vec4 v,
      final Vec4 target ) {

    return target.set(
        Utils.mod1(v.x),
        Utils.mod1(v.y),
        Utils.mod1(v.z),
        Utils.mod1(v.w));
  }

  /**
   * Multiplies a vector by a scalar.
   *
   * @param a      left operand, the scalar
   * @param b      right operand, the vector
   * @param target the output vector
   * @return the product
   */
  public static Vec4 mul (
      final float a,
      final Vec4 b,
      final Vec4 target ) {

    return target.set(
        a * b.x,
        a * b.y,
        a * b.z,
        a * b.w);
  }

  /**
   * Multiplies a vector by a scalar.
   *
   * @param a      left operand, the vector
   * @param b      right operand, the scalar
   * @param target the output vector
   * @return the product
   */
  public static Vec4 mul (
      final Vec4 a,
      final float b,
      final Vec4 target ) {

    return target.set(
        a.x * b,
        a.y * b,
        a.z * b,
        a.w * b);
  }

  /**
   * Multiplies two vectors, component-wise.
   *
   * @param a      left operand
   * @param b      right operand
   * @param target the output vector
   * @return the product
   */
  public static Vec4 mul (
      final Vec4 a,
      final Vec4 b,
      final Vec4 target ) {

    return target.set(
        a.x * b.x,
        a.y * b.y,
        a.z * b.z,
        a.w * b.w);
  }

  /**
   * Negates the input vector.
   *
   * @param v      the input vector
   * @param target the output vector
   * @return the negation
   */
  public static Vec4 negate (
      final Vec4 v,
      final Vec4 target ) {

    return target.set(-v.x, -v.y, -v.z, -v.w);
  }

  /**
   * Tests to see if all the vector's components are zero.
   *
   * @param v the input vector
   * @return the evaluation
   */
  public static boolean none ( final Vec4 v ) {

    return v.w == 0.0f &&
        v.z == 0.0f &&
        v.y == 0.0f &&
        v.x == 0.0f;
  }

  /**
   * Divides a vector by its magnitude, such that the new magnitude is
   * 1.0. <em>\u00e2</em> = <em>a</em> / |<em>a</em>|. The result is a
   * unit vector, as it lies on the unit hyper-sphere.
   *
   * @param v      the input vector
   * @param target the output vector
   * @return the unit vector
   * @see Vec4#div(Vec4, float, Vec4)
   * @see Vec4#mag(Vec4)
   */
  public static Vec4 normalize (
      final Vec4 v,
      final Vec4 target ) {

    final float mInv = Utils.invSqrtUnchecked(
        v.x * v.x + v.y * v.y + v.z * v.z + v.w * v.w);
    return target.set(
        v.x * mInv,
        v.y * mInv,
        v.z * mInv,
        v.w * mInv);
  }

  /**
   * Evaluates a vector like a boolean, where n != 0.0 is true.
   *
   * @param v      the vector
   * @param target the output vector
   * @return the truth table opposite
   */
  public static Vec4 not (
      final Vec4 v,
      final Vec4 target ) {

    return target.set(
        v.x != 0.0f ? 0.0f : 1.0f,
        v.y != 0.0f ? 0.0f : 1.0f,
        v.z != 0.0f ? 0.0f : 1.0f,
        v.w != 0.0f ? 0.0f : 1.0f);
  }

  /**
   * Returns a vector with both components set to one.
   *
   * @param target the output vector
   * @return one
   */
  public static Vec4 one ( final Vec4 target ) {

    return target.set(1.0f, 1.0f, 1.0f, 1.0f);
  }

  /**
   * Evaluates two vectors like booleans, using the OR logic gate.
   *
   * @param a      left operand
   * @param b      right operand
   * @param target the output vector
   * @return the evaluation
   * @see Utils#or(float, float)
   */
  public static Vec4 or (
      final Vec4 a,
      final Vec4 b,
      final Vec4 target ) {

    return target.set(
        Utils.or(a.x, b.x),
        Utils.or(a.y, b.y),
        Utils.or(a.z, b.z),
        Utils.or(a.w, b.w));
  }

  /**
   * Raises a scalar to a vector.
   *
   * @param a      left operand
   * @param b      right operand
   * @param target the output vector
   * @return the result
   * @see Math#pow(double, double)
   */
  public static Vec4 pow (
      final float a,
      final Vec4 b,
      final Vec4 target ) {

    return target.set(
        (float) Math.pow(a, b.x),
        (float) Math.pow(a, b.y),
        (float) Math.pow(a, b.z),
        (float) Math.pow(a, b.w));
  }

  /**
   * Raises a vector to the power of a scalar.
   *
   * @param a      left operand
   * @param b      right operand
   * @param target the output vector
   * @return the result
   * @see Math#pow(double, double)
   */
  public static Vec4 pow (
      final Vec4 a,
      final float b,
      final Vec4 target ) {

    return target.set(
        (float) Math.pow(a.x, b),
        (float) Math.pow(a.y, b),
        (float) Math.pow(a.z, b),
        (float) Math.pow(a.w, b));
  }

  /**
   * Raises a vector to the power of another vector.
   *
   * @param a      left operand
   * @param b      right operand
   * @param target the output vector
   * @return the result
   * @see Math#pow(double, double)
   */
  public static Vec4 pow (
      final Vec4 a,
      final Vec4 b,
      final Vec4 target ) {

    return target.set(
        (float) Math.pow(a.x, b.x),
        (float) Math.pow(a.y, b.y),
        (float) Math.pow(a.z, b.z),
        (float) Math.pow(a.w, b.w));
  }

  /**
   * Reduces the signal, or granularity, of a vector's components. Any
   * level less than 2 returns sets the target to the input.
   *
   * @param v      the input vector
   * @param levels the levels
   * @param target the output vector
   * @return the quantized vector
   * @see Utils#floor(float)
   */
  public static Vec4 quantize (
      final Vec4 v,
      final int levels,
      final Vec4 target ) {

    if (levels < 2) { return target.set(v); }

    final float delta = 1.0f / levels;
    return target.set(
        delta * Utils.floor(0.5f + v.x * levels),
        delta * Utils.floor(0.5f + v.y * levels),
        delta * Utils.floor(0.5f + v.z * levels),
        delta * Utils.floor(0.5f + v.w * levels));
  }

  /**
   * Creates a random point in the Cartesian coordinate system given a
   * lower and an upper bound.
   *
   * @param rng        the random number generator
   * @param lowerBound the lower bound
   * @param upperBound the upper bound
   * @param target     the output vector
   * @return the random vector
   * @see Random#uniform(float, float)
   */
  public static Vec4 randomCartesian (
      final Random rng,
      final Vec4 lowerBound,
      final Vec4 upperBound,
      final Vec4 target ) {

    return target.set(
        rng.uniform(lowerBound.x, upperBound.x),
        rng.uniform(lowerBound.y, upperBound.y),
        rng.uniform(lowerBound.z, upperBound.z),
        rng.uniform(lowerBound.w, upperBound.w));
  }

  /**
   * Generates a random coordinate on a sphere. Uses the same formula as
   * that for a random quaternion.
   *
   * @param rng    the random number generator
   * @param rhoMin the minimum radius
   * @param rhoMax the maximum radius
   * @param target the output vector
   * @return the vector
   * @see Quaternion#random(Random, Quaternion)
   */
  public static Vec4 randomSpherical (
      final Random rng,
      final float rhoMin,
      final float rhoMax,
      final Vec4 target ) {

    final float rho = rng.uniform(rhoMin, rhoMax);
    final float t0 = IUtils.TAU * rng.nextFloat();
    final float t1 = IUtils.TAU * rng.nextFloat();
    final float r1 = rng.nextFloat();
    final float x0 = rho * Utils.sqrt(1.0f - r1);
    final float x1 = rho * Utils.sqrt(r1);
    return target.set(
        x0 * Utils.sin(t0),
        x0 * Utils.cos(t0),
        x1 * Utils.sin(t1),
        x1 * Utils.cos(t1));
  }

  /**
   * Normalizes a vector, then multiplies it by a scalar, in effect
   * setting its magnitude to that scalar.
   *
   * @param v      the vector
   * @param scalar the scalar
   * @param target the output vector
   * @return the rescaled vector
   * @see Vec4#rescale(Vec4, float, Vec4, Vec4)
   */
  public static Vec4 rescale (
      final Vec4 v,
      final float scalar,
      final Vec4 target ) {

    final float mSq = v.x * v.x + v.y * v.y + v.z * v.z + v.w * v.w;
    if (scalar == 0.0f || mSq == 0.0f) { return target.reset(); }

    final float sclMag = scalar * Utils.invSqrtUnchecked(mSq);
    return target.set(
        v.x * sclMag,
        v.y * sclMag,
        v.z * sclMag,
        v.w * sclMag);
  }

  /**
   * Normalizes a vector, then multiplies it by a scalar, in effect
   * setting its magnitude to that scalar.
   *
   * @param v          the vector
   * @param scalar     the scalar
   * @param target     the output vector
   * @param normalized the normalized vector
   * @return the rescaled vector
   * @see Vec4#normalize(Vec4, Vec4)
   * @see Vec4#mul(Vec4, float, Vec4)
   */
  public static Vec4 rescale (
      final Vec4 v,
      final float scalar,
      final Vec4 target,
      final Vec4 normalized ) {

    if (scalar == 0.0f) {
      normalized.reset();
      return target.reset();
    }
    Vec4.normalize(v, normalized);
    return Vec4.mul(normalized, scalar, target);
  }

  /**
   * Returns to a vector with a positive value on the x axis, (1.0, 0.0,
   * 0.0, 0.0) .
   *
   * @param target the output vector
   * @return the right vector
   */
  public static Vec4 right ( final Vec4 target ) {

    return target.set(1.0f, 0.0f, 0.0f, 0.0f);
  }

  @Experimental
  public static Vec4 rotateXW (
      final Vec4 v,
      final float cosa,
      final float sina,
      final Vec4 target ) {

    // TODO: Needs testing.

    return target.set(
        v.x,
        cosa * v.y - sina * v.z,
        cosa * v.z + sina * v.y,
        v.w);
  }

  @Experimental
  public static Vec4 rotateXY (
      final Vec4 v,
      final float cosa,
      final float sina,
      final Vec4 target ) {

    // TODO: Needs testing.

    return target.set(
        v.x,
        v.y,
        cosa * v.z - sina * v.w,
        cosa * v.w + sina * v.z);
  }

  @Experimental
  public static Vec4 rotateXZ (
      final Vec4 v,
      final float cosa,
      final float sina,
      final Vec4 target ) {

    // TODO: Needs testing.

    return target.set(
        v.x,
        cosa * v.y - sina * v.w,
        v.z,
        cosa * v.w + sina * v.y);
  }

  @Experimental
  public static Vec4 rotateYW (
      final Vec4 v,
      final float cosa,
      final float sina,
      final Vec4 target ) {

    // TODO: Needs testing.

    return target.set(
        cosa * v.x - sina * v.z,
        v.y,
        cosa * v.z + sina * v.x,
        v.w);
  }

  @Experimental
  public static Vec4 rotateYZ (
      final Vec4 v,
      final float cosa,
      final float sina,
      final Vec4 target ) {

    // TODO: Needs testing.

    return target.set(
        cosa * v.x - sina * v.w,
        v.y,
        v.z,
        cosa * v.w + sina * v.x);
  }

  @Experimental
  public static Vec4 rotateZW (
      final Vec4 v,
      final float cosa,
      final float sina,
      final Vec4 target ) {

    // TODO: Needs testing.

    return target.set(
        cosa * v.x - sina * v.y,
        cosa * v.y + sina * v.x,
        v.z, v.w);
  }

  /**
   * Rounds each component of the vector to a given number of places
   * right of the decimal point.
   *
   * Beware of inaccuracies due to single precision.
   *
   * @param v      the input vector
   * @param places the number of places
   * @param target the output vector
   * @return the rounded vector
   * @see Vec4#round(Vec4, Vec4)
   */
  public static Vec4 round (
      final Vec4 v,
      final int places,
      final Vec4 target ) {

    if (places < 1) { return Vec4.round(v, target); }
    if (places > 7) { return target.set(v); }

    int n = 10;
    for (int i = 1; i < places; ++i) {
      n *= 10;
    }
    final float nInv = 1.0f / n;
    return target.set(
        Utils.round(v.x * n) * nInv,
        Utils.round(v.y * n) * nInv,
        Utils.round(v.z * n) * nInv,
        Utils.round(v.w * n) * nInv);
  }

  /**
   * Rounds each component of the vector to the nearest whole number.
   *
   * @param v      the input vector
   * @param target the output vector
   * @return the rounded vector
   * @see Utils#round(float)
   */
  public static Vec4 round (
      final Vec4 v,
      final Vec4 target ) {

    return target.set(
        Utils.round(v.x),
        Utils.round(v.y),
        Utils.round(v.z),
        Utils.round(v.w));
  }

  /**
   * Finds the sign of the vector: -1, if negative; 1, if positive.
   *
   * @param v      the input vector
   * @param target the output vector
   * @return the sign
   * @see Utils#sign(float)
   */
  public static Vec4 sign (
      final Vec4 v,
      final Vec4 target ) {

    return target.set(
        Utils.sign(v.x),
        Utils.sign(v.y),
        Utils.sign(v.z),
        Utils.sign(v.w));
  }

  /**
   * Subtracts the right vector from the left vector.
   *
   * @param a      left operand
   * @param b      right operand
   * @param target the output vector
   * @return the difference
   */
  public static Vec4 sub (
      final Vec4 a,
      final Vec4 b,
      final Vec4 target ) {

    return target.set(
        a.x - b.x,
        a.y - b.y,
        a.z - b.z,
        a.w - b.w);
  }

  /**
   * Truncates each component of the vector.
   *
   * @param v      the input vector
   * @param target the output vector
   * @return the truncation
   */
  public static Vec4 trunc (
      final Vec4 v,
      final Vec4 target ) {

    return target.set(
        (int) v.x,
        (int) v.y,
        (int) v.z,
        (int) v.w);
  }

  /**
   * Returns to a vector with a positive value on the z axis, (0.0, 0.0,
   * 1.0, 0.0) .
   *
   * @param target the output vector
   * @return the up vector
   */
  public static Vec4 up ( final Vec4 target ) {

    return target.set(0.0f, 0.0f, 1.0f, 0.0f);
  }

  /**
   * Wraps a vector around a periodic range as defined by an upper and
   * lower bound: lower bounds inclusive; upper bounds exclusive.
   *
   * In cases where the lower bound is (0.0, 0.0) , use
   * {@link Vec4#mod(Vec4, Vec4, Vec4)} .
   *
   * @param v      the vector
   * @param lb     the lower bound
   * @param ub     the upper bound
   * @param target the output vector
   * @return the wrapped vector
   * @see Utils#wrap(float, float, float)
   */
  @Experimental
  public static Vec4 wrap (
      final Vec4 v,
      final Vec4 lb,
      final Vec4 ub,
      final Vec4 target ) {

    return target.set(
        Utils.wrap(v.x, lb.x, ub.x),
        Utils.wrap(v.y, lb.y, ub.y),
        Utils.wrap(v.z, lb.z, ub.z),
        Utils.wrap(v.w, lb.w, ub.w));
  }

  /**
   * Evaluates two vectors like booleans, using the exclusive or (XOR)
   * logic gate.
   *
   * @param a      left operand
   * @param b      right operand
   * @param target the output vector
   * @return the evaluation
   * @see Utils#xor(float, float)
   */
  public static Vec4 xor (
      final Vec4 a,
      final Vec4 b,
      final Vec4 target ) {

    return target.set(
        Utils.xor(a.x, b.x),
        Utils.xor(a.y, b.y),
        Utils.xor(a.z, b.z),
        Utils.xor(a.w, b.w));
  }

  /**
   * Returns a vector with all components set to zero.
   *
   * @param target the output vector
   * @return the zero vector
   */
  public static Vec4 zero ( final Vec4 target ) {

    return target.set(0.0f, 0.0f, 0.0f, 0.0f);
  }
}
