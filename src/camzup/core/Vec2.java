package camzup.core;

import java.util.Comparator;
import java.util.Iterator;

/**
 * A mutable, extensible class influenced by GLSL, OSL and Processing's
 * PVector. This is intended for storing points and directions in
 * two-dimensional graphics programs. Instance methods are limited, while
 * most static methods require an explicit output variable to be provided.
 */
public class Vec2 implements Comparable < Vec2 >, Cloneable, Iterable <
   Float > {

   /**
    * Component on the x axis in the Cartesian coordinate system.
    */
   public float x = 0.0f;

   /**
    * Component on the y axis in the Cartesian coordinate system.
    */
   public float y = 0.0f;

   /**
    * The default vector constructor.
    */
   public Vec2 ( ) {}

   /**
    * Constructs a vector from boolean values.
    *
    * @param x the x component
    * @param y the y component
    */
   public Vec2 ( final boolean x, final boolean y ) { this.set(x, y); }

   /**
    * Constructs a vector from float values.
    *
    * @param x the x component
    * @param y the y component
    */
   public Vec2 ( final float x, final float y ) { this.set(x, y); }

   /**
    * Attempts to construct a vector from Strings using
    * {@link Float#parseFloat(String)} . If a NumberFormatException is thrown,
    * the component is set to zero.
    *
    * @param xstr the x string
    * @param ystr the y string
    *
    * @see Float#parseFloat(String)
    */
   public Vec2 ( final String xstr, final String ystr ) {

      this.set(xstr, ystr);
   }

   /**
    * Constructs a vector from a source vector's components.
    *
    * @param v the source vector
    */
   public Vec2 ( final Vec2 v ) { this.set(v); }

   /**
    * Returns a new vector with this vector's components. Java's cloneable
    * interface is problematic; use set or a copy constructor instead.
    *
    * @return a new vector
    *
    * @see Vec2#set(Vec2)
    * @see Vec2#Vec2(Vec2)
    */
   @Override
   public Vec2 clone ( ) { return new Vec2(this.x, this.y); }

   /**
    * Returns -1 when this vector is less than the comparisand; 1 when it is
    * greater than; 0 when the two are 'equal'. The implementation of this
    * method allows collections of vectors to be sorted.
    *
    * @param v the comparisand
    *
    * @return the numeric code
    */
   @Override
   public int compareTo ( final Vec2 v ) {

      /* @formatter:off */
      return this.y > v.y ?  1
           : this.y < v.y ? -1
           : this.x > v.x ?  1
           : this.x < v.x ? -1 : 0;
      /* @formatter:on */
   }

   /**
    * Tests to see if the vector contains a value.
    *
    * @param v the value
    *
    * @return the evaluation
    */
   public boolean contains ( final float v ) {

      if ( Utils.approx(this.y, v) ) { return true; }
      if ( Utils.approx(this.x, v) ) { return true; }
      return false;
   }

   /**
    * Returns a new vector decremented by one. For interoperability with
    * Kotlin: <code>--a</code> (prefix) or <code>a--</code> (postfix). Per the
    * specification, <em>does not mutate the vector in place</em>.
    *
    * @return the decremented vector
    */
   public Vec2 dec ( ) { return new Vec2(this.x - 1.0f, this.y - 1.0f); }

   /**
    * Returns a new vector with the division of the instance by the right
    * operand. For interoperability with Kotlin: <code>a / b</code> . <em>Does
    * not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the quotient
    */
   public Vec2 div ( final float b ) {

      if ( b != 0.0f ) { return new Vec2(this.x / b, this.y / b); }
      return new Vec2(0.0f, 0.0f);
   }

   /**
    * Returns a new vector with the division of the instance by the right
    * operand. For interoperability with Kotlin: <code>a / b</code> . <em>Does
    * not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the quotient
    */
   public Vec2 div ( final Vec2 b ) {

      return new Vec2(Utils.div(this.x, b.x), Utils.div(this.y, b.y));
   }

   /**
    * Divides the instance by the right operand (mutates the vector in place).
    * For interoperability with Kotlin: <code>a /= b</code> .
    *
    * @param b the right operand
    */
   public void divAssign ( final float b ) {

      if ( b != 0.0f ) {
         this.x /= b;
         this.y /= b;
      } else {
         this.x = 0.0f;
         this.y = 0.0f;
      }
   }

   /**
    * Divides the instance by the right operand (mutates the vector in place).
    * For interoperability with Kotlin: <code>a /= b</code> .
    *
    * @param b the right operand
    */
   public void divAssign ( final Vec2 b ) {

      this.x = Utils.div(this.x, b.x);
      this.y = Utils.div(this.y, b.y);
   }

   /**
    * Tests this vector for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    *
    * @see Vec2#equals(Vec2)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Vec2 ) obj);
   }

   /**
    * Simulates bracket subscript access in an array. When the provided index
    * is 1 or -1, returns y; 0 or -2, x.
    *
    * @param index the index
    *
    * @return the component at that index
    */
   public float get ( final int index ) {

      switch ( index ) {
         case 0:
         case -2:
            return this.x;

         case 1:
         case -1:
            return this.y;

         default:
            return 0.0f;
      }
   }

   /**
    * Returns a hash code for this vector based on its x and y components.
    *
    * @return the hash code
    *
    * @see Float#floatToIntBits(float)
    */
   @Override
   public int hashCode ( ) {

      return ( IUtils.MUL_BASE ^ Float.floatToIntBits(this.x) )
         * IUtils.HASH_MUL ^ Float.floatToIntBits(this.y);
   }

   /**
    * Returns a new vector incremented by one. For interoperability with
    * Kotlin: <code>++a</code> (prefix) or <code>a++</code> (postfix). Per the
    * specification, <em>does not mutate the vector in place</em>.
    *
    * @return the incremented vector
    */
   public Vec2 inc ( ) { return new Vec2(this.x + 1.0f, this.y + 1.0f); }

   /**
    * Returns an iterator for this vector, which allows its components to be
    * accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public Iterator < Float > iterator ( ) { return new V2Iterator(this); }

   /**
    * Gets the number of components held by this vector.
    *
    * @return the length
    */
   public int length ( ) { return 2; }

   /**
    * Returns a new vector with the subtraction of the right operand from the
    * instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the subtraction
    */
   public Vec2 minus ( final float b ) {

      return new Vec2(this.x - b, this.y - b);
   }

   /**
    * Returns a new vector with the subtraction of the right operand from the
    * instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the subtraction
    */
   public Vec2 minus ( final Vec2 b ) {

      return new Vec2(this.x - b.x, this.y - b.y);
   }

   /**
    * Subtracts the right operand from the instance (mutates the vector in
    * place). For interoperability with Kotlin: <code>a -= b</code> .
    *
    * @param b the right operand
    */
   public void minusAssign ( final float b ) {

      this.x -= b;
      this.y -= b;
   }

   /**
    * Subtracts the right operand from the instance (mutates the vector in
    * place). For interoperability with Kotlin: <code>a -= b</code> .
    *
    * @param b the right operand
    */
   public void minusAssign ( final Vec2 b ) {

      this.x -= b.x;
      this.y -= b.y;
   }

   /**
    * Returns a new vector with the boolean opposite of the instance. For
    * interoperability with Kotlin: <code>!a</code> . <em>Does not mutate the
    * vector in place</em>.
    *
    * @return the opposite vector
    */
   public Vec2 not ( ) { return new Vec2(this.x == 0.0f, this.y == 0.0f); }

   /**
    * Returns a new vector with the addition of the right operand to the
    * instance. For interoperability with Kotlin: <code>a + b</code> .
    * <em>Does not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the sum
    */
   public Vec2 plus ( final float b ) {

      return new Vec2(this.x + b, this.y + b);
   }

   /**
    * Returns a new vector with the addition of the right operand to the
    * instance. For interoperability with Kotlin: <code>a + b</code> .
    * <em>Does not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the sum
    */
   public Vec2 plus ( final Vec2 b ) {

      return new Vec2(this.x + b.x, this.y + b.y);
   }

   /**
    * Adds the right operand to the instance (mutates the vector in place).
    * For interoperability with Kotlin: <code>a += b</code> .
    *
    * @param b the right operand
    */
   public void plusAssign ( final float b ) {

      this.x += b;
      this.y += b;
   }

   /**
    * Adds the right operand to the instance (mutates the vector in place).
    * For interoperability with Kotlin: <code>a += b</code> .
    *
    * @param b the right operand
    */
   public void plusAssign ( final Vec2 b ) {

      this.x += b.x;
      this.y += b.y;
   }

   /**
    * Returns a new vector with the signed remainder (<code>fmod</code>) of
    * the instance and the right operand. For interoperability with Kotlin:
    * <code>a % b</code> . <em>Does not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the signed remainder
    */
   public Vec2 rem ( final float b ) {

      if ( b != 0.0f ) { return new Vec2(this.x % b, this.y % b); }
      return new Vec2(this.x, this.y);
   }

   /**
    * Returns a new vector with the signed remainder (<code>fmod</code>) of
    * the instance and the right operand. For interoperability with Kotlin:
    * <code>a % b</code> . <em>Does not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the signed remainder
    */
   public Vec2 rem ( final Vec2 b ) {

      return new Vec2(b.x != 0.0f ? this.x % b.x : this.x, b.y != 0.0f ? this.y
         % b.y : this.y);
   }

   /**
    * Assigns the signed remainder (<code>fmod</code>) of the instance and the
    * right operand to the instance (mutates the vector in place). For
    * interoperability with Kotlin: <code>a %= b</code> .
    *
    * @param b the right operand
    */
   public void remAssign ( final float b ) {

      if ( b != 0.0f ) {
         this.x %= b;
         this.y %= b;
      }
   }

   /**
    * Assigns the signed remainder (<code>fmod</code>) of the instance and the
    * right operand to the instance (mutates the vector in place). For
    * interoperability with Kotlin: <code>a %= b</code> .
    *
    * @param b the right operand
    */
   public void remAssign ( final Vec2 b ) {

      if ( b.x != 0.0f ) { this.x %= b.x; }
      if ( b.y != 0.0f ) { this.y %= b.y; }
   }

   /**
    * Resets this vector to an initial state, ( 0.0, 0.0 ) .
    *
    * @return this vector
    */
   public Vec2 reset ( ) { return this.set(0.0f, 0.0f); }

   /**
    * Sets the components of this vector from booleans, where false is 0.0 and
    * true is 1.0 .
    *
    * @param x the x component
    * @param y the y component
    *
    * @return this vector
    */
   public Vec2 set ( final boolean x, final boolean y ) {

      this.x = x ? 1.0f : 0.0f;
      this.y = y ? 1.0f : 0.0f;
      return this;
   }

   /**
    * Sets the components of this vector.
    *
    * @param x the x component
    * @param y the y component
    *
    * @return this vector
    */
   public Vec2 set ( final float x, final float y ) {

      this.x = x;
      this.y = y;
      return this;
   }

   /**
    * Attempts to set the components of this vector from Strings using
    * {@link Float#parseFloat(String)} . If a NumberFormatException is thrown,
    * the component is set to zero.
    *
    * @param xstr the x string
    * @param ystr the y string
    *
    * @return this vector
    *
    * @see Float#parseFloat(String)
    */
   public Vec2 set ( final String xstr, final String ystr ) {

      float xprs = 0.0f;
      float yprs = 0.0f;

      try {
         xprs = Float.parseFloat(xstr);
      } catch ( final Exception e ) {
         xprs = 0.0f;
      }

      try {
         yprs = Float.parseFloat(ystr);
      } catch ( final Exception e ) {
         yprs = 0.0f;
      }

      this.x = xprs;
      this.y = yprs;

      return this;
   }

   /**
    * Copies the components of the input vector to this vector.
    *
    * @param source the input vector
    *
    * @return this vector
    */
   public Vec2 set ( final Vec2 source ) {

      return this.set(source.x, source.y);
   }

   /**
    * Returns a new vector with the product of the instance and the right
    * operand. For interoperability with Kotlin: <code>a * b</code> . <em>Does
    * not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the product
    */
   public Vec2 times ( final float b ) {

      return new Vec2(this.x * b, this.y * b);
   }

   /**
    * Returns a new vector with the product of the instance and the right
    * operand. For interoperability with Kotlin: <code>a * b</code> . <em>Does
    * not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the product
    */
   public Vec2 times ( final Vec2 b ) {

      return new Vec2(this.x * b.x, this.y * b.y);
   }

   /**
    * Multiplies the right operand with the instance (mutates the vector in
    * place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param b the right operand
    */
   public void timesAssign ( final float b ) {

      this.x *= b;
      this.y *= b;
   }

   /**
    * Multiplies the right operand with the instance (mutates the vector in
    * place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param b the right operand
    */
   public void timesAssign ( final Vec2 b ) {

      this.x *= b.x;
      this.y *= b.y;
   }

   /**
    * Returns a float array of length 2 containing this vector's components.
    *
    * @return the array
    */
   public float[] toArray ( ) { return new float[] { this.x, this.y }; }

   /**
    * Returns a string representation of this vector as a space separated
    * value for use by OBJ formatting functions.
    *
    * @return the string
    */
   public String toObjString ( ) {

      final StringBuilder objs = new StringBuilder(16);
      objs.append(Utils.toFixed(this.x, 6));
      objs.append(' ');
      objs.append(Utils.toFixed(this.y, 6));
      return objs.toString();
   }

   /**
    * Returns a string representation of this vector.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of this vector.
    *
    * @param places number of decimal places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(48);
      sb.append("{ x: ");
      sb.append(Utils.toFixed(this.x, places));
      sb.append(", y: ");
      sb.append(Utils.toFixed(this.y, places));
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Returns a new vector with the negation of the instance. For
    * interoperability with Kotlin: <code>-a</code> . <em>Does not mutate the
    * vector in place</em>.
    *
    * @return the negation
    */
   public Vec2 unaryMinus ( ) { return new Vec2(-this.x, -this.y); }

   /**
    * Returns a new vector with the positive copy of the instance. For
    * interoperability with Kotlin: <code>+a</code> . <em>Does not mutate the
    * vector in place</em>.
    *
    * @return the positive
    */
   public Vec2 unaryPlus ( ) { return new Vec2(+this.x, +this.y); }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes. This is
    * formatted as a two-tuple. If this is a UV coordinate, provides the
    * option to flip the v coordinate.
    *
    * @param flipv whether to subtract y from 1.0
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode ( final boolean flipv ) {

      final StringBuilder pyCd = new StringBuilder(32);
      pyCd.append('(');
      pyCd.append(Utils.toFixed(flipv ? this.x : 1.0f - this.x, 6));
      pyCd.append(',');
      pyCd.append(' ');
      pyCd.append(Utils.toFixed(flipv ? 1.0f - this.y : this.y, 6));
      pyCd.append(')');
      return pyCd.toString();
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes. This is
    * formatted as a three-tuple.
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode ( final float z ) {

      final StringBuilder pyCd = new StringBuilder(64);
      pyCd.append('(');
      pyCd.append(Utils.toFixed(this.x, 6));
      pyCd.append(',');
      pyCd.append(' ');
      pyCd.append(Utils.toFixed(this.y, 6));
      pyCd.append(',');
      pyCd.append(' ');
      pyCd.append(Utils.toFixed(z, 6));
      pyCd.append(')');
      return pyCd.toString();
   }

   /**
    * Returns a string representation of this vector as a comma separated
    * value for use by SVG formatting functions.<br>
    * <br>
    * This uses a print precision of six decimal places to avoid glitches when
    * small shapes are scaled up.
    *
    * @return the string
    */
   String toSvgString ( ) {

      final StringBuilder svgp = new StringBuilder(16);
      svgp.append(Utils.toFixed(this.x, 6));
      svgp.append(' ');
      svgp.append(Utils.toFixed(this.y, 6));
      return svgp.toString();
   }

   /**
    * Returns a String formatted as C# code for the Unity API; creates a
    * <code>Vector2</code>.
    *
    * @param z the z component
    *
    * @return the string
    */
   @Experimental
   String toUnityCode ( ) {

      final StringBuilder sb = new StringBuilder(96);
      sb.append("new Vector2(");
      sb.append(this.x);
      sb.append("f, ");
      sb.append(this.y);
      sb.append("f)");
      return sb.toString();
   }

   /**
    * Returns a String formatted as C# code for the Unity API; creates a
    * <code>Vector3</code>.
    *
    * @param z the z component
    *
    * @return the string
    */
   @Experimental
   String toUnityCode ( final float z ) {

      final StringBuilder sb = new StringBuilder(96);
      sb.append("new Vector3(");
      sb.append(this.x);
      sb.append("f, ");
      sb.append(this.y);
      sb.append("f, ");
      sb.append(z);
      sb.append("f)");
      return sb.toString();
   }

   /**
    * Tests equivalence between this and another vector. For rough equivalence
    * of floating point components, use the static approximation function
    * instead.
    *
    * @param v the vector
    *
    * @return the evaluation
    *
    * @see Float#floatToIntBits(float)
    * @see Vec2#approx(Vec2, Vec2)
    * @see Vec2#approx(Vec2, Vec2, float)
    */
   protected boolean equals ( final Vec2 v ) {

      return Float.floatToIntBits(this.y) == Float.floatToIntBits(v.y) && Float
         .floatToIntBits(this.x) == Float.floatToIntBits(v.x);
   }

   /**
    * Finds the absolute value of each vector component.
    *
    * @param v      the input vector
    * @param target the output vector
    *
    * @return the absolute vector
    *
    * @see Utils#abs(float)
    */
   public static Vec2 abs ( final Vec2 v, final Vec2 target ) {

      return target.set(Utils.abs(v.x), Utils.abs(v.y));
   }

   /**
    * Adds two vectors together.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the sum
    */
   public static Vec2 add ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(a.x + b.x, a.y + b.y);
   }

   /**
    * Adds and then normalizes two vectors.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the normalized sum
    */
   public static Vec2 addNorm ( final Vec2 a, final Vec2 b,
      final Vec2 target ) {

      final float dx = a.x + b.x;
      final float dy = a.y + b.y;
      final float mInv = Utils.invHypot(dx, dy);
      return target.set(dx * mInv, dy * mInv);
   }

   /**
    * Adds and then normalizes two vectors. Discloses the intermediate,
    * unnormalized sum as an output.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    * @param sum    the sum
    *
    * @return the normalized sum
    *
    * @see Vec2#add(Vec2, Vec2, Vec2)
    * @see Vec2#normalize(Vec2, Vec2)
    */
   public static Vec2 addNorm ( final Vec2 a, final Vec2 b, final Vec2 target,
      final Vec2 sum ) {

      Vec2.add(a, b, sum);
      Vec2.normalize(sum, target);
      return target;
   }

   /**
    * Tests to see if all the vector's components are non-zero. Useful when
    * testing valid dimensions (width and depth) stored in vectors.
    *
    * @param v the input vector
    *
    * @return the evaluation
    */
   public static boolean all ( final Vec2 v ) {

      return v.y != 0.0f && v.x != 0.0f;
   }

   /**
    * Evaluates two vectors like booleans, using the AND logic gate.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the evaluation
    *
    * @see Utils#and(float, float)
    */
   public static Vec2 and ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(Utils.and(a.x, b.x), Utils.and(a.y, b.y));
   }

   /**
    * Finds the angle between two vectors. Returns zero when either vector is
    * zero.
    *
    * @param a the first vector
    * @param b the second vector
    *
    * @return the angle
    *
    * @see Vec2#any(Vec2)
    * @see Vec2#dot(Vec2, Vec2)
    * @see Vec2#magSq(Vec2)
    * @see Utils#invSqrtUnchecked(float)
    * @see Utils#acos(float)
    */
   public static float angleBetween ( final Vec2 a, final Vec2 b ) {

      return Vec2.any(a) && Vec2.any(b) ? Utils.acos(Vec2.dot(a, b) * Utils
         .invSqrtUnchecked(Vec2.magSq(a)) * Utils.invSqrtUnchecked(Vec2.magSq(
            b))) : 0.0f;
   }

   /**
    * Tests to see if any of the vector's components are non-zero.
    *
    * @param v the input vector
    *
    * @return the evaluation
    */
   public static boolean any ( final Vec2 v ) {

      return v.y != 0.0f || v.x != 0.0f;
   }

   /**
    * Appends a vector to a one-dimensional vector array, returning a new
    * array.
    *
    * @param a the array
    * @param b the vector
    *
    * @return a new array
    */
   public static Vec2[] append ( final Vec2[] a, final Vec2 b ) {

      final boolean anull = a == null;
      final boolean bnull = b == null;
      if ( anull && bnull ) { return new Vec2[] {}; }
      if ( anull ) { return new Vec2[] { b }; }
      if ( bnull ) {
         final Vec2[] result = new Vec2[a.length];
         System.arraycopy(a, 0, result, 0, a.length);
         return result;
      }

      final int alen = a.length;
      final Vec2[] result = new Vec2[alen + 1];
      System.arraycopy(a, 0, result, 0, alen);
      result[alen] = b;
      return result;
   }

   /**
    * Tests to see if two vectors approximate each other.
    *
    * @param a left operand
    * @param b right operand
    *
    * @return the evaluation
    */
   public static boolean approx ( final Vec2 a, final Vec2 b ) {

      return Vec2.approx(a, b, IUtils.DEFAULT_EPSILON);
   }

   /**
    * Tests to see if two vectors approximate each other.
    *
    * @param a         left comparisand
    * @param b         right comparisand
    * @param tolerance the tolerance
    *
    * @return the evaluation
    *
    * @see Utils#approx(float, float, float)
    */
   public static boolean approx ( final Vec2 a, final Vec2 b,
      final float tolerance ) {

      /* @formatter:off */
      return a == b ||
         Utils.approx(a.y, b.y, tolerance) &&
         Utils.approx(a.x, b.x, tolerance);
      /* @formatter:on */
   }

   /**
    * Tests to see if a vector has, approximately, the specified magnitude.
    *
    * @param a the input vector
    * @param b the magnitude
    *
    * @return the evaluation
    *
    * @see Utils#approx(float, float)
    * @see Vec2#dot(Vec2, Vec2)
    */
   public static boolean approxMag ( final Vec2 a, final float b ) {

      return Vec2.approxMag(a, b, IUtils.DEFAULT_EPSILON);
   }

   /**
    * Tests to see if a vector has, approximately, the specified magnitude.
    *
    * @param a         the input vector
    * @param b         the magnitude
    * @param tolerance the tolerance
    *
    * @return the evaluation
    *
    * @see Utils#approx(float, float, float)
    * @see Vec2#dot(Vec2, Vec2)
    */
   public static boolean approxMag ( final Vec2 a, final float b,
      final float tolerance ) {

      return Utils.approx(Vec2.magSq(a), b * b, tolerance);
   }

   /**
    * Tests to see if two vectors are parallel.
    *
    * @param a left comparisand
    * @param b right comparisand
    *
    * @return the evaluation
    */
   public static boolean areParallel ( final Vec2 a, final Vec2 b ) {

      return Vec2.areParallel(a, b, IUtils.DEFAULT_EPSILON);
   }

   /**
    * Tests to see if two vectors are parallel. Does so by evaluating whether
    * the cross product of the two approximates zero.
    *
    * @param a         the left comparisand
    * @param b         the right comparisand
    * @param tolerance the tolerance
    *
    * @return the evaluation
    */
   public static boolean areParallel ( final Vec2 a, final Vec2 b,
      final float tolerance ) {

      return Utils.abs(a.x * b.y - a.y * b.x) <= tolerance;
   }

   /**
    * Returns to a vector with a negative value on the y axis, (0.0, -1.0) .
    *
    * @param target the output vector
    *
    * @return the back vector
    */
   public static Vec2 back ( final Vec2 target ) {

      return target.set(0.0f, -1.0f);
   }

   /**
    * Returns a point on a Bezier curve described by two anchor points and two
    * control points according to a step in [0.0, 1.0] . When the step is less
    * than one, returns the first anchor point. When the step is greater than
    * one, returns the second anchor point.
    *
    * @param ap0    the first anchor point
    * @param cp0    the first control point
    * @param cp1    the second control point
    * @param ap1    the second anchor point
    * @param step   the step
    * @param target the output vector
    *
    * @return the point along the curve
    */
   public static Vec2 bezierPoint ( final Vec2 ap0, final Vec2 cp0,
      final Vec2 cp1, final Vec2 ap1, final float step, final Vec2 target ) {

      if ( step <= 0.0f ) {
         return target.set(ap0);
      } else if ( step >= 1.0f ) { return target.set(ap1); }

      final float u = 1.0f - step;
      float tcb = step * step;
      float ucb = u * u;
      final float usq3t = ucb * ( step + step + step );
      final float tsq3u = tcb * ( u + u + u );
      ucb *= u;
      tcb *= step;

      return target.set(ap0.x * ucb + cp0.x * usq3t + cp1.x * tsq3u + ap1.x
         * tcb, ap0.y * ucb + cp0.y * usq3t + cp1.y * tsq3u + ap1.y * tcb);
   }

   /**
    * Returns a tangent on a Bezier curve described by two anchor points and
    * two control points according to a step in [0.0, 1.0] . When the step is
    * less than one, returns the first anchor point subtracted from the first
    * control point. When the step is greater than one, returns the second
    * anchor point subtracted from the second control point.
    *
    * @param ap0    the first anchor point
    * @param cp0    the first control point
    * @param cp1    the second control point
    * @param ap1    the second anchor point
    * @param step   the step
    * @param target the output vector
    *
    * @return the tangent along the curve
    *
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    */
   public static Vec2 bezierTangent ( final Vec2 ap0, final Vec2 cp0,
      final Vec2 cp1, final Vec2 ap1, final float step, final Vec2 target ) {

      if ( step <= 0.0f ) {
         return Vec2.sub(cp0, ap0, target);
      } else if ( step >= 1.0f ) { return Vec2.sub(ap1, cp1, target); }

      final float u = 1.0f - step;
      final float t3 = step + step + step;

      final float usq3 = u * ( u + u + u );
      final float tsq3 = step * t3;
      final float ut6 = u * ( t3 + t3 );

      /* @formatter:off */
      return target.set(
         ( cp0.x - ap0.x ) * usq3 +
         ( cp1.x - cp0.x ) * ut6 +
         ( ap1.x - cp1.x ) * tsq3,

         ( cp0.y - ap0.y ) * usq3 +
         ( cp1.y - cp0.y ) * ut6 +
         ( ap1.y - cp1.y ) * tsq3);
      /* @formatter:on */
   }

   /**
    * Returns a normalized tangent on a Bezier curve.
    *
    * @param ap0    the first anchor point
    * @param cp0    the first control point
    * @param cp1    the second control point
    * @param ap1    the second anchor point
    * @param step   the step
    * @param target the output vector
    *
    * @return the tangent along the curve
    *
    * @see Vec2#bezierTangent(Vec2, Vec2, Vec2, Vec2, float, Vec2)
    */
   public static Vec2 bezierTanUnit ( final Vec2 ap0, final Vec2 cp0,
      final Vec2 cp1, final Vec2 ap1, final float step, final Vec2 target ) {

      Vec2.bezierTangent(ap0, cp0, cp1, ap1, step, target);
      final float mInv = Utils.invHypot(target.x, target.y);
      return target.set(target.x * mInv, target.y * mInv);
   }

   /**
    * Raises each component of the vector to the nearest greater integer.
    *
    * @param v      the input vector
    * @param target the output vector
    *
    * @return the result
    *
    * @see Utils#ceil(float)
    */
   public static Vec2 ceil ( final Vec2 v, final Vec2 target ) {

      return target.set(Utils.ceil(v.x), Utils.ceil(v.y));
   }

   /**
    * Clamps a vector to a range within the lower and upper bound.
    *
    * @param v          the input vector
    * @param lowerBound the range lower bound
    * @param upperBound the range upper bound
    * @param target     the output vector
    *
    * @return the clamped vector
    *
    * @see Utils#clamp(float, float, float)
    */
   public static Vec2 clamp ( final Vec2 v, final Vec2 lowerBound,
      final Vec2 upperBound, final Vec2 target ) {

      return target.set(Utils.clamp(v.x, lowerBound.x, upperBound.x), Utils
         .clamp(v.x, lowerBound.y, upperBound.y));
   }

   /**
    * Clamps the vector to a range in [0.0, 1.0]. Useful for working with
    * texture coordinates.
    *
    * @param v      the input vector
    * @param target the output vector
    *
    * @return the clamped vector
    *
    * @see Utils#clamp01(float)
    */
   public static Vec2 clamp01 ( final Vec2 v, final Vec2 target ) {

      return target.set(Utils.clamp01(v.x), Utils.clamp01(v.y));
   }

   /**
    * Concatenates two one-dimensional Vec2 arrays.
    *
    * @param a the first array
    * @param b the second array
    *
    * @return the concatenated array.
    *
    * @see System#arraycopy(Object, int, Object, int, int)
    */
   public static Vec2[] concat ( final Vec2[] a, final Vec2[] b ) {

      final boolean anull = a == null;
      final boolean bnull = b == null;

      if ( anull && bnull ) { return new Vec2[] {}; }

      if ( anull ) {
         final Vec2[] result = new Vec2[b.length];
         System.arraycopy(b, 0, result, 0, b.length);
         return result;
      }

      if ( bnull ) {
         final Vec2[] result = new Vec2[a.length];
         System.arraycopy(a, 0, result, 0, a.length);
         return result;
      }

      final int alen = a.length;
      final int blen = b.length;
      final Vec2[] result = new Vec2[alen + blen];
      System.arraycopy(a, 0, result, 0, alen);
      System.arraycopy(b, 0, result, alen, blen);
      return result;
   }

   /**
    * Finds first vector argument with the sign of the second vector argument.
    *
    * @param magnitude the magnitude
    * @param sign      the sign
    * @param target    the output vector
    *
    * @return the signed vector
    */
   public static Vec2 copySign ( final Vec2 magnitude, final Vec2 sign,
      final Vec2 target ) {

      return target.set(Utils.copySign(magnitude.x, sign.x), Utils.copySign(
         magnitude.y, sign.y));
   }

   /**
    * Returns the z component of the cross product between two vectors. The x
    * and y components of the cross between 2D vectors are zero. For that
    * reason, the normalized cross product is equal to the sign of the cross
    * product.
    *
    * @param a left operand
    * @param b right operand
    *
    * @return the cross z component
    */
   public static float cross ( final Vec2 a, final Vec2 b ) {

      return a.x * b.y - a.y * b.x;
   }

   /**
    * Finds the absolute value of the difference between two vectors.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the absolute difference
    *
    * @see Utils#diff(float, float)
    */
   public static Vec2 diff ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(Utils.diff(a.x, b.x), Utils.diff(a.y, b.y));
   }

   /**
    * Finds the Euclidean distance between two vectors.
    *
    * @param a left operand
    * @param b right operand
    *
    * @return the distance
    *
    * @see Vec2#distEuclidean(Vec2, Vec2)
    */
   public static float dist ( final Vec2 a, final Vec2 b ) {

      return Vec2.distEuclidean(a, b);
   }

   /**
    * Finds the Chebyshev distance between two vectors. Forms a square pattern
    * when plotted.
    *
    * @param a left operand
    * @param b right operand
    *
    * @return the distance
    *
    * @see Utils#max(float, float)
    * @see Utils#diff(float, float)
    */
   public static float distChebyshev ( final Vec2 a, final Vec2 b ) {

      return Utils.max(Utils.diff(a.x, b.x), Utils.diff(a.y, b.y));
   }

   /**
    * Finds the Euclidean distance between two vectors. Where possible, use
    * distance squared to avoid the computational cost of the square-root.
    *
    * @param a left operand
    * @param b right operand
    *
    * @return the Euclidean distance
    *
    * @see Vec2#distSq(Vec2, Vec2)
    */
   public static float distEuclidean ( final Vec2 a, final Vec2 b ) {

      return Utils.sqrtUnchecked(Vec2.distSq(a, b));
   }

   /**
    * Finds the Manhattan distance between two vectors. Forms a diamond
    * pattern when plotted.
    *
    * @param a left operand
    * @param b right operand
    *
    * @return the Manhattan distance
    *
    * @see Utils#diff(float, float)
    */
   public static float distManhattan ( final Vec2 a, final Vec2 b ) {

      return Utils.diff(a.x, b.x) + Utils.diff(a.y, b.y);
   }

   /**
    * Finds the Minkowski distance between two vectors. This is a
    * generalization of other distance formulae. When the exponent value, c,
    * is 1.0, the Minkowski distance equals the Manhattan distance; when it is
    * 2.0, Minkowski equals the Euclidean distance.
    *
    * @param a left operand
    * @param b right operand
    * @param c exponent
    *
    * @return the Minkowski distance
    *
    * @see Vec2#distEuclidean(Vec2, Vec2)
    * @see Vec2#distManhattan(Vec2, Vec2)
    * @see Math#pow(double, double)
    * @see Math#abs(double)
    */
   public static float distMinkowski ( final Vec2 a, final Vec2 b,
      final float c ) {

      if ( c != 0.0f ) {
         /* @formatter:off */
         final double cd = c;
         return ( float ) Math.pow(
            Math.pow(Math.abs(( double ) ( b.x - a.x )), cd) +
            Math.pow(Math.abs(( double ) ( b.y - a.y )), cd), 1.0d / cd);
         /* @formatter:on */
      }

      return 0.0f;
   }

   /**
    * Finds the Euclidean distance squared between two vectors. Equivalent to
    * subtracting one vector from the other, then finding the dot product of
    * the difference with itself.
    *
    * @param a left operand
    * @param b right operand
    *
    * @return the distance squared
    */
   public static float distSq ( final Vec2 a, final Vec2 b ) {

      final float xDist = b.x - a.x;
      final float yDist = b.y - a.y;
      return xDist * xDist + yDist * yDist;
   }

   /**
    * Divides a scalar by a vector.
    *
    * @param a      scalar, numerator
    * @param b      vector, denominator
    * @param target the output vector
    *
    * @return the quotient
    *
    * @see Utils#div(float, float)
    */
   public static Vec2 div ( final float a, final Vec2 b, final Vec2 target ) {

      return target.set(Utils.div(a, b.x), Utils.div(a, b.y));
   }

   /**
    * Divides a vector by a scalar.
    *
    * @param a      vector, numerator
    * @param b      scalar, denominator
    * @param target the output vector
    *
    * @return the quotient
    */
   public static Vec2 div ( final Vec2 a, final float b, final Vec2 target ) {

      if ( b != 0.0f ) {
         final float denom = 1.0f / b;
         return target.set(a.x * denom, a.y * denom);
      }
      return target.reset();
   }

   /**
    * Divides the left operand by the right, component-wise. This is
    * mathematically incorrect, but serves as a shortcut for transforming a
    * vector by the inverse of a scalar matrix.
    *
    * @param a      numerator
    * @param b      denominator
    * @param target the output vector
    *
    * @return the quotient
    *
    * @see Utils#div(float, float)
    */
   public static Vec2 div ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(Utils.div(a.x, b.x), Utils.div(a.y, b.y));
   }

   /**
    * Finds the dot product of two vectors by summing the products of their
    * corresponding components. <em>a</em> \u00b7 <em>b</em> :=
    * <em>a<sub>x</sub> b<sub>x</sub></em> + <em>a<sub>y</sub>
    * b<sub>y</sub></em><br>
    * <br>
    * The dot product of a vector with itself is equal to its magnitude
    * squared.
    *
    * @param a left operand
    * @param b right operand
    *
    * @return the dot product
    */
   public static float dot ( final Vec2 a, final Vec2 b ) {

      return a.x * b.x + a.y * b.y;
   }

   /**
    * Returns a vector with all components set to epsilon, a small positive
    * non-zero value.
    *
    * @param target the output vector
    *
    * @return epsilon
    */
   public static Vec2 epsilon ( final Vec2 target ) {

      return target.set(IUtils.DEFAULT_EPSILON, IUtils.DEFAULT_EPSILON);
   }

   /**
    * Filters a vector by setting each component to the input component if it
    * is in bounds and 0.0 if it is out of bounds.
    *
    * @param v      the vector
    * @param lb     the lower bound
    * @param ub     the upper bound
    * @param target the output vector
    *
    * @return the filtered vector
    */
   public static Vec2 filter ( final Vec2 v, final Vec2 lb, final Vec2 ub,
      final Vec2 target ) {

      return target.set(Utils.filter(v.x, lb.x, ub.x), Utils.filter(v.y, lb.y,
         ub.y));
   }

   /**
    * Flattens a two dimensional array of vectors to a one dimensional array.
    *
    * @param arr the 2D array
    *
    * @return the 1D array
    */
   public static Vec2[] flat ( final Vec2[][] arr ) {

      /* Sum the lengths of inner arrays. */
      int totalLen = 0;
      final int sourceLen = arr.length;
      for ( int i = 0; i < sourceLen; ++i ) {
         totalLen += arr[i].length;
      }

      /*
       * Copy each inner array to the result array, then move the cursor by the
       * length of each array.
       */
      final Vec2[] result = new Vec2[totalLen];
      for ( int j = 0, i = 0; i < sourceLen; ++i ) {
         final Vec2[] arrInner = arr[i];
         final int len = arrInner.length;
         System.arraycopy(arrInner, 0, result, j, len);
         j += len;
      }

      return result;
   }

   /**
    * Floors each component of the vector.
    *
    * @param v      the input vector
    * @param target the output vector
    *
    * @return the floor
    *
    * @see Utils#floor(float)
    */
   public static Vec2 floor ( final Vec2 v, final Vec2 target ) {

      return target.set(Utils.floor(v.x), Utils.floor(v.y));
   }

   /**
    * Applies the % operator (truncation-based modulo) to the left operand.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the result
    *
    * @see Utils#fmod(float, float)
    */
   public static Vec2 fmod ( final float a, final Vec2 b, final Vec2 target ) {

      return target.set(Utils.fmod(a, b.x), Utils.fmod(a, b.y));
   }

   /**
    * Applies the % operator (truncation-based modulo) to each component of
    * the left operand.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the result
    */
   public static Vec2 fmod ( final Vec2 a, final float b, final Vec2 target ) {

      if ( b != 0.0f ) { return target.set(a.x % b, a.y % b); }
      return target.set(a);
   }

   /**
    * Applies the % operator (truncation-based modulo) to each component of
    * the left operand.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the result
    *
    * @see Utils#fmod(float, float)
    */
   public static Vec2 fmod ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(Utils.fmod(a.x, b.x), Utils.fmod(a.x, b.y));
   }

   /**
    * Returns to a vector with a positive value on the y axis, (0.0, 1.0) .
    *
    * @param target the output vector
    *
    * @return the forward vector
    */
   public static Vec2 forward ( final Vec2 target ) {

      return target.set(0.0f, 1.0f);
   }

   /**
    * Returns the fractional portion of the vector's components.
    *
    * @param v      the input vector
    * @param target the output vector
    *
    * @return the fractional portion
    *
    * @see Utils#fract(float)
    */
   public static Vec2 fract ( final Vec2 v, final Vec2 target ) {

      return target.set(Utils.fract(v.x), Utils.fract(v.y));
   }

   /**
    * Creates a vector from polar coordinates: (1) theta, \u03b8, an angle in
    * radians, the vector's heading; (2) rho, \u03c1, a radius, the vector's
    * magnitude. Uses the formula<br>
    * <br>
    * ( \u03c1 cos ( \u03b8 ),<br>
    * \u03c1 sin ( \u03b8 ) ).
    *
    * @param heading the angle in radians
    * @param radius  the radius
    * @param target  the output vector
    *
    * @return the vector
    */
   public static Vec2 fromPolar ( final float heading, final float radius,
      final Vec2 target ) {

      /*
       * return target.set( radius * Utils.cos(heading), radius *
       * Utils.sin(heading));
       */

      final float nrm = heading * IUtils.ONE_TAU;
      return target.set(radius * Utils.scNorm(nrm), radius * Utils.scNorm(nrm
         - 0.25f));
   }

   /**
    * Creates a vector with a magnitude of 1.0 from an angle, such that the
    * vector is on the unit circle.
    *
    * @param heading the angle in radians
    * @param target  the output vector
    *
    * @return the vector
    */
   public static Vec2 fromPolar ( final float heading, final Vec2 target ) {

      /* return target.set( Utils.cos(heading), Utils.sin(heading)); */

      final float nrm = heading * IUtils.ONE_TAU;
      return target.set(Utils.scNorm(nrm), Utils.scNorm(nrm - 0.25f));
   }

   /**
    * Generates a 2D array of vectors. Defaults to the coordinate range of
    * [-0.5, 0.5] .
    *
    * @param res the resolution
    *
    * @return the array
    */
   public static Vec2[][] grid ( final int res ) {

      return Vec2.grid(res, res);
   }

   /**
    * Generates a 2D array of vectors. The result is in row-major order, but
    * the parameters are supplied in reverse: columns first, then rows.
    * Defaults to the coordinate range of [-0.5, 0.5] .
    *
    * @param cols number of columns
    * @param rows number of rows
    *
    * @return the array
    */
   public static Vec2[][] grid ( final int cols, final int rows ) {

      return Vec2.grid(cols, rows, -0.5f, -0.5f, 0.5f, 0.5f);
   }

   /**
    * Generates a 2D array of vectors. The result is in row-major order, but
    * the parameters are supplied in reverse: columns first, then rows.
    *
    * @param cols       number of columns
    * @param rows       number of rows
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    *
    * @return the array
    */
   public static Vec2[][] grid ( final int cols, final int rows,
      final float lowerBound, final float upperBound ) {

      return Vec2.grid(cols, rows, lowerBound, lowerBound, upperBound,
         upperBound);
   }

   /**
    * Generates a 2D array of vectors. The result is in row-major order, but
    * the parameters are supplied in reverse: columns first, then rows.
    *
    * @param cols       number of columns
    * @param rows       number of rows
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    *
    * @return the array
    */
   public static Vec2[][] grid ( final int cols, final int rows,
      final Vec2 lowerBound, final Vec2 upperBound ) {

      return Vec2.grid(cols, rows, lowerBound.x, lowerBound.y, upperBound.x,
         upperBound.y);
   }

   /**
    * Generates a 2D array of vectors for a hexagonal map. In spatial
    * coordinates, rows are staggered by an offset. In array storage, the grid
    * uses an axial coordinate system: column-minor coordinates advance
    * diagonally rather than in a jagged vertical.
    *
    * @param count      number of columns and rows
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    *
    * @return the array
    */
   public static Vec2[][] gridHex ( final int count, final Vec2 lowerBound,
      final Vec2 upperBound ) {

      final int vcnt = count < 3 ? 3 : count;
      final float toStep = 1.0f / ( vcnt - 1.0f );

      /*
       * Find the width of each hexagonal cell by dividing the absolute
       * difference between the upper and lower bound by the count.
       * Math.sqrt(3.0d) / 8.0d = 0.21650635094610965d . This is the constant
       * Math.sqrt(3.0d) / 2.0d multiplied by the expected radius, 0.5d, then
       * halved, so that the x offset can be both positive and negative.
       */
      final float w = 0.21650635f * Utils.abs(upperBound.x - lowerBound.x)
         * toStep;

      /*
       * Multiply x values by Math.sqrt(3.0d) / 2.0d . Multiply y by 0.75 --
       * cell radius plus half the radius.
       */
      final float lb32 = IUtils.SQRT_3_2 * lowerBound.x;
      final float ub32 = IUtils.SQRT_3_2 * upperBound.x;
      final float lb75 = 0.75f * lowerBound.y;
      final float ub75 = 0.75f * upperBound.y;

      /* Calculate inner loop in advance. */
      final float[] xs = new float[vcnt];
      for ( int j = 0; j < vcnt; ++j ) {
         final float step = j * toStep;
         xs[j] = ( 1.0f - step ) * lb32 + step * ub32;
      }

      final Vec2[][] result = new Vec2[vcnt][vcnt];
      for ( int i = 0; i < vcnt; ++i ) {
         final Vec2[] row = result[i];

         final float step = i * toStep;
         final float y = ( 1.0f - step ) * lb75 + step * ub75;

         /* Shift alternating cells by positive or negative offset. */
         final float xoff = i % 2 != 0 ? -w : w;
         // final int joff = i / 2 + 1;
         // final int joff = Utils.ceilToInt(i * 0.5f);

         for ( int j = 0; j < vcnt; ++j ) {

            /*
             * Shift indices so that they move smoothly along a diagonal between
             * hex edges, not along a jagged, orthogonal path.
             */
            // final int k = Utils.mod(j - joff, vcnt);
            // row[k] = new Vec2(xoff + xs[j], y);
            row[j] = new Vec2(xoff + xs[j], y);
         }
      }
      return result;
   }

   /**
    * Generates a 2D array of vectors. The array is ordered by rings, then
    * sectors; the parameters are supplied in reverse order.
    *
    * @param sectors the sectors, headings
    * @param radius  the radius
    *
    * @return the array
    */
   public static Vec2[][] gridPolar ( final int sectors, final float radius ) {

      return Vec2.gridPolar(sectors, 1, radius, radius, 0.0f, true);
   }

   /**
    * Generates a 2D array of vectors. The array is ordered by rings, then
    * sectors; the parameters are supplied in reverse order.
    *
    * @param sectors   the sectors, headings
    * @param rings     the rings, radii
    * @param radiusMin minimum radius
    * @param radiusMax maximum radius
    *
    * @return the array
    */
   public static Vec2[][] gridPolar ( final int sectors, final int rings,
      final float radiusMin, final float radiusMax ) {

      return Vec2.gridPolar(sectors, rings, radiusMin, radiusMax, 0.0f, true);
   }

   /**
    * Generates a 2D array of vectors. The array is ordered by rings, then
    * sectors; the parameters are supplied in reverse order.
    *
    * @param sectors   the sectors, headings
    * @param rings     the rings, radii
    * @param radiusMin minimum radius
    * @param radiusMax maximum radius
    * @param angOffset angular offset per ring
    *
    * @return the array
    */
   public static Vec2[][] gridPolar ( final int sectors, final int rings,
      final float radiusMin, final float radiusMax, final float angOffset ) {

      return Vec2.gridPolar(sectors, rings, radiusMin, radiusMax, angOffset,
         true);
   }

   /**
    * Generates a 2D array of vectors. The array is ordered by rings, then
    * sectors; the parameters are supplied in reverse order.
    *
    * @param sectors       the sectors, headings
    * @param rings         the rings, radii
    * @param radiusMin     minimum radius
    * @param radiusMax     maximum radius
    * @param angOffset     angular offset per ring
    * @param includeCenter include the center
    *
    * @return the array
    */
   public static Vec2[][] gridPolar ( final int sectors, final int rings,
      final float radiusMin, final float radiusMax, final float angOffset,
      final boolean includeCenter ) {

      final int vsect = sectors < 3 ? 3 : sectors;
      final int vring = rings < 1 ? 1 : rings;
      final float angNorm = Utils.mod1(angOffset * IUtils.ONE_TAU);

      final boolean oneRing = vring == 1;
      final float vrMax = Utils.max(IUtils.DEFAULT_EPSILON, radiusMin,
         radiusMax);
      final float vrMin = oneRing ? vrMax : Utils.max(IUtils.DEFAULT_EPSILON,
         Utils.min(radiusMin, radiusMax));

      final int ringLen = includeCenter ? rings + 1 : rings;
      final Vec2[][] result = new Vec2[ringLen][vsect];
      if ( includeCenter ) { result[0] = new Vec2[] { new Vec2() }; }

      final float toPrc = oneRing ? 1.0f : 1.0f / ( vring - 1.0f );
      final float toTheta = 1.0f / vsect;
      float off = 0.0f;

      for ( int h = 0; h < vring; ++h ) {

         final float prc = h * toPrc;
         final float radius = ( 1.0f - prc ) * vrMin + prc * vrMax;
         final Vec2[] ring = result[includeCenter ? h + 1 : h];

         for ( int i = 0; i < vsect; ++i ) {

            final float theta = off + i * toTheta;
            final float cosTheta = Utils.scNorm(theta);
            final float sinTheta = Utils.scNorm(theta - 0.25f);

            ring[i] = new Vec2(radius * cosTheta, radius * sinTheta);
         }

         off += angNorm;
      }

      return result;
   }

   /**
    * Evaluates whether the left comparisand is greater than the right
    * comparisand.
    *
    * @param a      left comparisand
    * @param b      right comparisand
    * @param target the output vector
    *
    * @return the evaluation
    */
   public static Vec2 gt ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(a.x > b.x, a.y > b.y);
   }

   /**
    * Evaluates whether the left comparisand is greater than or equal to the
    * right comparisand.
    *
    * @param a      left comparisand
    * @param b      right comparisand
    * @param target the output vector
    *
    * @return the evaluation
    */
   public static Vec2 gtEq ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(a.x >= b.x, a.y >= b.y);
   }

   /**
    * Finds the vector's heading. Defaults to headingSigned.
    *
    * @param v the input vector
    *
    * @return the angle in radians
    *
    * @see Vec2#headingSigned(Vec2)
    * @see Vec2#headingUnsigned(Vec2)
    */
   public static float heading ( final Vec2 v ) {

      return Vec2.headingSigned(v);
   }

   /**
    * Finds the vector's heading in the range [-\u03c0, \u03c0] .
    *
    * @param v the input vector
    *
    * @return the angle in radians
    *
    * @see Utils#atan2(float, float)
    */
   public static float headingSigned ( final Vec2 v ) {

      return Utils.atan2(v.y, v.x);
   }

   /**
    * Finds the vector's heading in the range [0.0, \u03c4] .
    *
    * @param v the input vector
    *
    * @return the angle in radians
    *
    * @see Vec2#headingSigned(Vec2)
    * @see Utils#modRadians(float)
    */
   public static float headingUnsigned ( final Vec2 v ) {

      return Utils.modRadians(Vec2.headingSigned(v));
   }

   /**
    * Inserts an array of vectors in the midst of another. The insertion point
    * is before, or to the left of, the existing element at a given index.
    *
    * @param arr    the array
    * @param index  the insertion index
    * @param insert the inserted array
    *
    * @return the new array
    */
   @Experimental
   public static Vec2[] insert ( final Vec2[] arr, final int index,
      final Vec2[] insert ) {

      final int alen = arr.length;
      final int blen = insert.length;
      final int valIdx = Utils.mod(index, alen + 1);

      final Vec2[] result = new Vec2[alen + blen];
      System.arraycopy(arr, 0, result, 0, valIdx);
      System.arraycopy(insert, 0, result, valIdx, blen);
      System.arraycopy(arr, valIdx, result, valIdx + blen, alen - valIdx);

      return result;
   }

   /**
    * Tests to see if the vector is on the unit circle, i.e., has a magnitude
    * of approximately 1.0 .
    *
    * @param v the input vector
    *
    * @return the evaluation
    *
    * @see Utils#approx(float, float)
    * @see Vec2#dot(Vec2, Vec2)
    */
   public static boolean isUnit ( final Vec2 v ) {

      return Utils.approx(Vec2.magSq(v), 1.0f);
   }

   /**
    * Returns a vector with a negative value on the x axis, (-1.0, 0.0).
    *
    * @param target the output vector
    *
    * @return the left vector
    */
   public static Vec2 left ( final Vec2 target ) {

      return target.set(-1.0f, 0.0f);
   }

   /**
    * Limits a vector's magnitude to a scalar. Does nothing if the vector is
    * beneath the limit.
    *
    * @param v      the input vector
    * @param limit  the limit
    * @param target the output vector
    *
    * @return the limited vector
    *
    * @see Vec2#magSq(Vec2)
    * @see Utils#invSqrtUnchecked(float)
    */
   public static Vec2 limit ( final Vec2 v, final float limit,
      final Vec2 target ) {

      final float mSq = v.x * v.x + v.y * v.y;
      if ( limit > 0.0f && mSq > limit * limit ) {
         final float scalar = limit * Utils.invSqrtUnchecked(mSq);
         return target.set(v.x * scalar, v.y * scalar);
      }

      return target.set(v);
   }

   /**
    * Evaluates whether the left comparisand is less than the right
    * comparisand.
    *
    * @param a      left comparisand
    * @param b      right comparisand
    * @param target the output vector
    *
    * @return the evaluation
    */
   public static Vec2 lt ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(a.x < b.x, a.y < b.y);
   }

   /**
    * Evaluates whether the left comparisand is less than or equal to the
    * right comparisand.
    *
    * @param a      left comparisand
    * @param b      right comparisand
    * @param target the output vector
    *
    * @return the evaluation
    */
   public static Vec2 ltEq ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(a.x <= b.x, a.y <= b.y);
   }

   /**
    * Finds the length, or magnitude, of a vector, |<em>a</em>| . Also
    * referred to as the radius when using polar coordinates. Uses the formula
    * \u221a <em>a</em> \u00b7 <em>a</em> . Where possible, use magSq or dot
    * to avoid the computational cost of the square-root.
    *
    * @param v the input vector
    *
    * @return the magnitude
    *
    * @see Vec2#dot(Vec2, Vec2)
    * @see Math#sqrt(double)
    * @see Vec2#magSq(Vec2)
    */
   public static float mag ( final Vec2 v ) {

      return Utils.sqrtUnchecked(v.x * v.x + v.y * v.y);
   }

   /**
    * Finds the length-, or magnitude-, squared of a vector,
    * |<em>a</em>|<sup>2</sup>. Returns the same result as <em>a</em> \u00b7
    * <em>a</em> . Useful when calculating the lengths of many vectors, so as
    * to avoid the computational cost of the square-root.
    *
    * @param v the input vector
    *
    * @return the magnitude squared
    *
    * @see Vec2#dot(Vec2, Vec2)
    * @see Vec2#mag(Vec2)
    */
   public static float magSq ( final Vec2 v ) {

      return v.x * v.x + v.y * v.y;
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
    *
    * @return the mapped value
    *
    * @see Utils#map(float, float, float, float, float)
    */
   public static Vec2 map ( final Vec2 v, final Vec2 lbOrigin,
      final Vec2 ubOrigin, final Vec2 lbDest, final Vec2 ubDest,
      final Vec2 target ) {

      return target.set(Utils.map(v.x, lbOrigin.x, ubOrigin.x, lbDest.x,
         ubDest.x), Utils.map(v.y, lbOrigin.y, ubOrigin.y, lbDest.y, ubDest.y));
   }

   /**
    * Sets the target vector to the maximum of the input vector and a lower
    * bound.
    *
    * @param a          the input value
    * @param lowerBound the lower bound
    * @param target     the output vector
    *
    * @return the maximum values
    */
   public static Vec2 max ( final Vec2 a, final float lowerBound,
      final Vec2 target ) {

      return target.set(Utils.max(a.x, lowerBound), Utils.max(a.y, lowerBound));
   }

   /**
    * Sets the target vector to the maximum components of the input vector and
    * a lower bound.
    *
    * @param a          the input vector
    * @param lowerBound the lower bound
    * @param target     the output vector
    *
    * @return the maximum values
    *
    * @see Utils#max(float, float)
    */
   public static Vec2 max ( final Vec2 a, final Vec2 lowerBound,
      final Vec2 target ) {

      return target.set(Utils.max(a.x, lowerBound.x), Utils.max(a.y,
         lowerBound.y));
   }

   /**
    * Sets the target vector to the minimum components of the input vector and
    * an upper bound.
    *
    * @param a          the input value
    * @param upperBound the upper bound
    * @param target     the output vector
    *
    * @return the minimum values
    */
   public static Vec2 min ( final Vec2 a, final float upperBound,
      final Vec2 target ) {

      return target.set(Utils.min(a.x, upperBound), Utils.min(a.y, upperBound));
   }

   /**
    * Sets the target vector to the minimum components of the input vector and
    * an upper bound.
    *
    * @param a          the input vector
    * @param upperBound the upper bound
    * @param target     the output vector
    *
    * @return the minimal values
    *
    * @see Utils#min(float, float)
    */
   public static Vec2 min ( final Vec2 a, final Vec2 upperBound,
      final Vec2 target ) {

      return target.set(Utils.min(a.x, upperBound.x), Utils.min(a.y,
         upperBound.y));
   }

   /**
    * Mixes two vectors together by a step in [0.0, 1.0] .
    *
    * @param origin the original vector
    * @param dest   the destination vector
    * @param step   the step
    * @param target the output vector
    *
    * @return the mix
    */
   public static Vec2 mix ( final Vec2 origin, final Vec2 dest,
      final float step, final Vec2 target ) {

      if ( step <= 0.0f ) { return target.set(origin); }
      if ( step >= 1.0f ) { return target.set(dest); }

      final float u = 1.0f - step;
      return target.set(u * origin.x + step * dest.x, u * origin.y + step
         * dest.y);
   }

   /**
    * Wraps a scalar by each component of a vector.
    *
    * @param a      the scalar
    * @param b      the vector
    * @param target the output vector
    *
    * @return the modulated vector
    *
    * @see Utils#mod(float, float)
    */
   public static Vec2 mod ( final float a, final Vec2 b, final Vec2 target ) {

      return target.set(Utils.mod(a, b.x), Utils.mod(a, b.y));
   }

   /**
    * Wraps each component of a vector by a scalar
    *
    * @param a      the vector
    * @param b      the scalar
    * @param target the output vector
    *
    * @return the modulated vector
    *
    * @see Utils#modUnchecked(float, float)
    */
   public static Vec2 mod ( final Vec2 a, final float b, final Vec2 target ) {

      if ( b != 0.0f ) {
         return target.set(Utils.modUnchecked(a.x, b), Utils.modUnchecked(a.y,
            b));
      }
      return target.set(a);
   }

   /**
    * Wraps each component of the left vector by those of the right.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the modulated vector
    *
    * @see Utils#mod(float, float)
    */
   public static Vec2 mod ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(Utils.mod(a.x, b.x), Utils.mod(a.y, b.y));
   }

   /**
    * A specialized form of modulo which subtracts the floor of the vector
    * from the vector. For Vec2s, useful for managing texture coordinates in
    * the range [0.0, 1.0] .
    *
    * @param v      the input vector
    * @param target the output vector
    *
    * @return the result
    *
    * @see Utils#mod1(float)
    */
   public static Vec2 mod1 ( final Vec2 v, final Vec2 target ) {

      return target.set(Utils.mod1(v.x), Utils.mod1(v.y));
   }

   /**
    * Multiplies a vector by a scalar.
    *
    * @param a      left operand, the scalar
    * @param b      right operand, the vector
    * @param target the output vector
    *
    * @return the product
    */
   public static Vec2 mul ( final float a, final Vec2 b, final Vec2 target ) {

      return target.set(a * b.x, a * b.y);
   }

   /**
    * Multiplies a vector by a scalar.
    *
    * @param a      left operand, the vector
    * @param b      right operand, the scalar
    * @param target the output vector
    *
    * @return the product
    */
   public static Vec2 mul ( final Vec2 a, final float b, final Vec2 target ) {

      return target.set(a.x * b, a.y * b);
   }

   /**
    * Multiplies two vectors, component-wise. Such multiplication is
    * mathematically incorrect, but serves as a shortcut for transforming a
    * vector by a scalar matrix.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the product
    */
   public static Vec2 mul ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(a.x * b.x, a.y * b.y);
   }

   /**
    * Negates the input vector.
    *
    * @param v      the input vector
    * @param target the output vector
    *
    * @return the negation
    */
   public static Vec2 negate ( final Vec2 v, final Vec2 target ) {

      return target.set(-v.x, -v.y);
   }

   /**
    * Returns a vector with all components set to negative one.
    *
    * @param target the output vector
    *
    * @return negative one
    */
   public static Vec2 negOne ( final Vec2 target ) {

      return target.set(-1.0f, -1.0f);
   }

   /**
    * Tests to see if all the vector's components are zero. Useful when
    * safeguarding against invalid directions.
    *
    * @param v the input vector
    *
    * @return the evaluation
    */
   public static boolean none ( final Vec2 v ) {

      return v.y == 0.0f && v.x == 0.0f;
   }

   /**
    * Divides a vector by its magnitude, such that the new magnitude is 1.0 .
    * <em>\u00e2</em> = <em>a</em> / |<em>a</em>| . The result is a unit
    * vector, as it lies on the circumference of a unit circle.
    *
    * @param v      the input vector
    * @param target the output vector
    *
    * @return the unit vector
    *
    * @see Vec2#div(Vec2, float, Vec2)
    * @see Vec2#mag(Vec2)
    */
   public static Vec2 normalize ( final Vec2 v, final Vec2 target ) {

      final float mInv = Utils.invSqrtUnchecked(v.x * v.x + v.y * v.y);
      return target.set(v.x * mInv, v.y * mInv);
   }

   /**
    * Evaluates a vector like a boolean, where n != 0.0 is true.
    *
    * @param v      the vector
    * @param target the output vector
    *
    * @return the truth table opposite
    */
   public static Vec2 not ( final Vec2 v, final Vec2 target ) {

      return target.set(v.x != 0.0f ? 0.0f : 1.0f, v.y != 0.0f ? 0.0f : 1.0f);
   }

   /**
    * Returns a vector with both components set to one.
    *
    * @param target the output vector
    *
    * @return one
    */
   public static Vec2 one ( final Vec2 target ) {

      return target.set(1.0f, 1.0f);
   }

   /**
    * Evaluates two vectors like booleans, using the OR logic gate.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the evaluation
    *
    * @see Utils#or(float, float)
    */
   public static Vec2 or ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(Utils.or(a.x, b.x), Utils.or(a.y, b.y));
   }

   /**
    * Finds the perpendicular of a vector. Defaults to counter-clockwise
    * rotation, such that the perpendicular of ( 1.0, 0.0 ) is ( 0.0, 1.0 ) .
    * The 2D counterpart to the 3D vector's cross product.
    *
    * @param v      the input vector
    * @param target the output vector
    *
    * @return the perpendicular
    *
    * @see Vec2#perpendicularCCW(Vec2, Vec2)
    * @see Vec3#cross(Vec3, Vec3, Vec3)
    */
   public static Vec2 perpendicular ( final Vec2 v, final Vec2 target ) {

      return Vec2.perpendicularCCW(v, target);
   }

   /**
    * Finds the perpendicular of a vector in the counter-clockwise direction,
    * such that
    * <ul>
    * <li>perp ( right ) = forward, <br>
    * perp ( 1.0, 0.0 ) = ( 0.0, 1.0 )</li>
    * <li>perp ( forward ) = left, <br>
    * perp ( 0.0, 1.0 ) = ( -1.0, 0.0 )</li>
    * <li>perp ( left ) = back, <br>
    * perp ( -1.0, 0.0 ) = ( 0.0, -1.0 )</li>
    * <li>perp ( back ) = right, <br>
    * perp ( 0.0, -1.0 ) = ( 1.0, 0.0 )</li>
    * </ul>
    * In terms of the components, perp ( x, y ) = ( -y, x ) .
    *
    * @param a      the input vector
    * @param target the output vector
    *
    * @return the perpendicular
    */
   public static Vec2 perpendicularCCW ( final Vec2 a, final Vec2 target ) {

      return target.set(-a.y, a.x);
   }

   /**
    * Finds the perpendicular of a vector in the clockwise direction, such
    * that
    * <ul>
    * <li>perp ( right ) = back, <br>
    * perp( 1.0, 0.0 ) = ( 0.0, -1.0 )</li>
    * <li>perp ( back ) = left, <br>
    * perp( 0.0, -1.0 ) = ( -1.0, 0.0 )</li>
    * <li>perp ( left ) = forward, <br>
    * perp( -1.0, 0.0 ) = ( 0.0, 1.0 )</li>
    * <li>perp ( forward ) = right, <br>
    * perp( 0.0, 1.0 ) = ( 1.0, 0.0 )</li>
    * </ul>
    * In terms of the components, perp ( x, y ) = ( y, -x ) .
    *
    * @param a      the input vector
    * @param target the output vector
    *
    * @return the perpendicular
    */
   public static Vec2 perpendicularCW ( final Vec2 a, final Vec2 target ) {

      return target.set(a.y, -a.x);
   }

   /**
    * Raises a scalar to a vector.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the result
    */
   public static Vec2 pow ( final float a, final Vec2 b, final Vec2 target ) {

      final double ad = a;
      return target.set(( float ) Math.pow(ad, b.x), ( float ) Math.pow(ad,
         b.y));
   }

   /**
    * Raises a vector to the power of a scalar.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the result
    */
   public static Vec2 pow ( final Vec2 a, final float b, final Vec2 target ) {

      final double bd = b;
      return target.set(( float ) Math.pow(a.x, bd), ( float ) Math.pow(a.y,
         bd));
   }

   /**
    * Raises a vector to the power of another vector.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the result
    *
    * @see Utils#pow(float, float)
    */
   public static Vec2 pow ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(Utils.pow(a.x, b.x), Utils.pow(a.y, b.y));
   }

   /**
    * Projects one vector onto another.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the projection
    *
    * @see Vec2#projectVector(Vec2, Vec2, Vec2)
    */
   public static Vec2 project ( final Vec2 a, final Vec2 b,
      final Vec2 target ) {

      return Vec2.projectVector(a, b, target);
   }

   /**
    * Returns the scalar projection of <em>a</em> onto <em>b</em>.
    *
    * @param a left operand
    * @param b right operand
    *
    * @return the scalar projection
    *
    * @see Vec2#magSq(Vec2)
    * @see Vec2#dot(Vec2, Vec2)
    */
   public static float projectScalar ( final Vec2 a, final Vec2 b ) {

      final float bSq = Vec2.magSq(b);
      if ( bSq > 0.0f ) { return Vec2.dot(a, b) / bSq; }
      return 0.0f;
   }

   /**
    * Projects one vector onto another. Defined as<br>
    * <br>
    * project ( <em>a</em>, <em>b</em> ) := <em>b</em> ( <em>a</em> \u00b7
    * <em>b</em> / <em>b</em> \u00b7 <em>b</em> )
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the projection
    *
    * @see Vec2#projectScalar(Vec2, Vec2)
    * @see Vec2#mul(Vec2, float, Vec2)
    */
   public static Vec2 projectVector ( final Vec2 a, final Vec2 b,
      final Vec2 target ) {

      return Vec2.mul(b, Vec2.projectScalar(a, b), target);
   }

   /**
    * Reduces the signal, or granularity, of a vector's components. Any level
    * less than 2 returns sets the target to the input.
    *
    * @param v      the input vector
    * @param levels the levels
    * @param target the output vector
    *
    * @return the quantized vector
    *
    * @see Utils#floor(float)
    */
   public static Vec2 quantize ( final Vec2 v, final int levels,
      final Vec2 target ) {

      if ( levels < 2 ) { return target.set(v); }

      final float levf = levels;
      final float delta = 1.0f / levf;
      return target.set(delta * Utils.floor(0.5f + v.x * levf), delta * Utils
         .floor(0.5f + v.y * levf));
   }

   /**
    * Generates a vector with a random heading and a magnitude of 1.0, such
    * that it lies on the unit circle.
    *
    * @param rng    the random number generator
    * @param target the output vector
    *
    * @return the random vector
    *
    * @see Vec2#randomPolar(java.util.Random, float, float, Vec2)
    */
   public static Vec2 random ( final java.util.Random rng, final Vec2 target ) {

      return Vec2.randomPolar(rng, 1.0f, 1.0f, target);
   }

   /**
    * Creates a random point in the Cartesian coordinate system given a lower
    * and an upper bound.
    *
    * @param rng        the random number generator
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    * @param target     the output vector
    *
    * @return the random vector
    */
   public static Vec2 randomCartesian ( final java.util.Random rng,
      final float lowerBound, final float upperBound, final Vec2 target ) {

      final float rx = rng.nextFloat();
      final float ry = rng.nextFloat();
      return target.set( ( 1.0f - rx ) * lowerBound + rx * upperBound, ( 1.0f
         - ry ) * lowerBound + ry * upperBound);
   }

   /**
    * Creates a random point in the Cartesian coordinate system given a lower
    * and an upper bound.
    *
    * @param rng        the random number generator
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    * @param target     the output vector
    *
    * @return the random vector
    */
   public static Vec2 randomCartesian ( final java.util.Random rng,
      final Vec2 lowerBound, final Vec2 upperBound, final Vec2 target ) {

      final float rx = rng.nextFloat();
      final float ry = rng.nextFloat();
      return target.set( ( 1.0f - rx ) * lowerBound.x + rx * upperBound.x,
         ( 1.0f - ry ) * lowerBound.y + ry * upperBound.y);
   }

   /**
    * Creates a vector at a random heading and radius.
    *
    * @param rng    the random number generator
    * @param rhoMin the minimum radius
    * @param rhoMax the maximum radius
    * @param target the output vector
    *
    * @return the random vector
    *
    * @see Vec2#fromPolar(float, float, Vec2)
    */
   public static Vec2 randomPolar ( final java.util.Random rng,
      final float rhoMin, final float rhoMax, final Vec2 target ) {

      final float rt = rng.nextFloat();
      final float rr = rng.nextFloat();
      return Vec2.fromPolar( ( 1.0f - rt ) * -IUtils.PI + rt * IUtils.PI, ( 1.0f
         - rr ) * rhoMin + rr * rhoMax, target);
   }

   /**
    * Creates a vector with a magnitude of 1.0 at a random heading, such that
    * it lies on the unit circle.
    *
    * @param rng    the random number generator
    * @param target the output vector
    *
    * @return the random vector
    *
    * @see Vec2#randomPolar(java.util.Random, float, float, Vec2)
    */
   public static Vec2 randomPolar ( final java.util.Random rng,
      final Vec2 target ) {

      return Vec2.randomPolar(rng, 1.0f, 1.0f, target);
   }

   /**
    * Reflects an incident vector off a normal vector. Uses the formula <br>
    * <br>
    * <em>i</em> - 2.0 (<em>n</em> \u00b7 <em>i</em>) <em>n</em><br>
    * <br>
    *
    * @param incident the incident vector
    * @param normal   the normal vector
    * @param target   the output vector
    *
    * @return the reflected vector
    *
    * @see Vec2#magSq(Vec2)
    * @see Vec2#dot(Vec2, Vec2)
    * @see Utils#abs(float)
    * @see Utils#invSqrtUnchecked(float)
    */
   public static Vec2 reflect ( final Vec2 incident, final Vec2 normal,
      final Vec2 target ) {

      // TEST

      final float nMSq = Vec2.magSq(normal);
      if ( Utils.abs(nMSq) < IUtils.DEFAULT_EPSILON ) { return target.reset(); }

      if ( Utils.approx(nMSq, 1.0f) ) {
         final float scalar = 2.0f * Vec2.dot(normal, incident);
         return target.set(incident.x - scalar * normal.x, incident.y - scalar
            * normal.y);
      }

      final float mInv = Utils.invSqrtUnchecked(nMSq);
      final float nx = normal.x * mInv;
      final float ny = normal.y * mInv;
      final float scalar = 2.0f * ( nx * incident.x + ny * incident.y );
      return target.set(incident.x - scalar * nx, incident.y - scalar * ny);
   }

   /**
    * Refracts a vector through a volume using Snell's law.
    *
    * @param incident the incident vector
    * @param normal   the normal vector
    * @param eta      ratio of refraction indices
    * @param target   the output vector
    *
    * @return the refraction
    *
    * @see Vec2#dot(Vec2, Vec2)
    * @see Math#sqrt(double)
    */
   public static Vec2 refract ( final Vec2 incident, final Vec2 normal,
      final float eta, final Vec2 target ) {

      // TEST

      final float nDotI = Vec2.dot(normal, incident);
      final float k = 1.0f - eta * eta * ( 1.0f - nDotI * nDotI );
      if ( k <= 0.0f ) { return target.reset(); }
      final float scalar = eta * nDotI + Utils.sqrtUnchecked(k);
      return target.set(eta * incident.x - scalar * normal.x, eta * incident.y
         - scalar * normal.y);
   }

   /**
    * Normalizes a vector, then multiplies it by a scalar, in effect setting
    * its magnitude to that scalar.
    *
    * @param v      the vector
    * @param scalar the scalar
    * @param target the output vector
    *
    * @return the rescaled vector
    */
   public static Vec2 rescale ( final Vec2 v, final float scalar,
      final Vec2 target ) {

      final float mSq = v.x * v.x + v.y * v.y;
      if ( scalar != 0.0f && mSq > 0.0f ) {
         final float sclMg = scalar * Utils.invSqrtUnchecked(mSq);
         return target.set(v.x * sclMg, v.y * sclMg);
      }
      return target.reset();
   }

   /**
    * Normalizes a vector, then multiplies it by a scalar, in effect setting
    * its magnitude to that scalar.
    *
    * @param v          the vector
    * @param scalar     the scalar
    * @param target     the output vector
    * @param normalized the normalized vector
    *
    * @return the rescaled vector
    *
    * @see Vec2#normalize(Vec2, Vec2)
    * @see Vec2#mul(Vec2, float, Vec2)
    */
   public static Vec2 rescale ( final Vec2 v, final float scalar,
      final Vec2 target, final Vec2 normalized ) {

      if ( scalar != 0.0f ) {
         Vec2.normalize(v, normalized);
         return Vec2.mul(normalized, scalar, target);
      }
      normalized.reset();
      return target.reset();
   }

   /**
    * Resizes an array of vectors to a requested length. If the new length is
    * greater than the current length, the new elements are filled with new
    * vectors.<br>
    * <br>
    * This does <em>not</em> use
    * {@link System#arraycopy(Object, int, Object, int, int)} because this
    * function iterates through the entire array checking for null entries.
    *
    * @param arr the array
    * @param sz  the new size
    *
    * @return the array
    */
   public static Vec2[] resize ( final Vec2[] arr, final int sz ) {

      if ( sz < 1 ) { return new Vec2[] {}; }
      final Vec2[] result = new Vec2[sz];

      if ( arr == null ) {
         for ( int i = 0; i < sz; ++i ) {
            result[i] = new Vec2();
         }
         return result;
      }

      final int last = arr.length - 1;
      for ( int i = 0; i < sz; ++i ) {
         if ( i > last || arr[i] == null ) {
            result[i] = new Vec2();
         } else {
            result[i] = arr[i];
         }
      }

      return result;
   }

   /**
    * Returns to a vector with a positive value on the x axis, (1.0, 0.0) .
    *
    * @param target the output vector
    *
    * @return the right vector
    */
   public static Vec2 right ( final Vec2 target ) {

      return target.set(1.0f, 0.0f);
   }

   /**
    * Rotates a vector around the x axis by an angle in radians. For 2D
    * vectors, this scales the y component by cosine of the angle.
    *
    * @param v       the input vector
    * @param radians the angle in radians
    * @param target  the output vector
    *
    * @return the rotated vector
    *
    * @see Utils#cos(float)
    */
   public static Vec2 rotateX ( final Vec2 v, final float radians,
      final Vec2 target ) {

      return target.set(v.x, v.y * Utils.cos(radians));
   }

   /**
    * Rotates a vector around the y axis by an angle in radians. For 2D
    * vectors, this scales the x component by cosine of the angle.
    *
    * @param v       the input vector
    * @param radians the angle in radians
    * @param target  the output vector
    *
    * @return the rotated vector
    *
    * @see Utils#cos(float)
    */
   public static Vec2 rotateY ( final Vec2 v, final float radians,
      final Vec2 target ) {

      return target.set(v.x * Utils.cos(radians), v.y);
   }

   /**
    * Rotates a vector around the z axis. Accepts pre-calculated sine and
    * cosine of an angle, so that collections of vectors can be efficiently
    * rotated without repeatedly calling cos and sin.
    *
    * @param v      the input vector
    * @param cosa   cosine of the angle
    * @param sina   sine of the angle
    * @param target the output vector
    *
    * @return the rotated vector
    */
   public static Vec2 rotateZ ( final Vec2 v, final float cosa,
      final float sina, final Vec2 target ) {

      return target.set(cosa * v.x - sina * v.y, cosa * v.y + sina * v.x);
   }

   /**
    * Rotates a vector around the z axis by an angle in radians.
    *
    * @param v       the input vector
    * @param radians the angle in radians
    * @param target  the output vector
    *
    * @return the rotated vector
    */
   public static Vec2 rotateZ ( final Vec2 v, final float radians,
      final Vec2 target ) {

      // return Vec2.rotateZ(v,
      // Utils.cos(radians),
      // Utils.sin(radians), target);

      final float nrm = radians * IUtils.ONE_TAU;
      final float cosa = Utils.scNorm(nrm);
      final float sina = Utils.scNorm(nrm - 0.25f);
      return target.set(cosa * v.x - sina * v.y, cosa * v.y + sina * v.x);
   }

   /**
    * Rounds each component of the vector to a given number of places right of
    * the decimal point. Beware of inaccuracies due to single precision.
    *
    * @param v      the input vector
    * @param places the number of places
    * @param target the output vector
    *
    * @return the rounded vector
    *
    * @see Vec2#round(Vec2, Vec2)
    * @see Utils#round(float)
    */
   public static Vec2 round ( final Vec2 v, final int places,
      final Vec2 target ) {

      if ( places < 1 ) { return Vec2.round(v, target); }
      if ( places > 7 ) { return target.set(v); }

      int n = 10;
      for ( int i = 1; i < places; ++i ) {
         n *= 10;
      }
      final float nf = n;
      final float nInv = 1.0f / nf;
      return target.set(Utils.round(v.x * nf) * nInv, Utils.round(v.y * nf)
         * nInv);
   }

   /**
    * Rounds each component of the vector to the nearest whole number.
    *
    * @param v      the input vector
    * @param target the output vector
    *
    * @return the rounded vector
    *
    * @see Utils#round(float)
    */
   public static Vec2 round ( final Vec2 v, final Vec2 target ) {

      return target.set(Utils.round(v.x), Utils.round(v.y));
   }

   /**
    * Finds the sign of the vector: -1, if negative; 1, if positive.
    *
    * @param v      the input vector
    * @param target the output vector
    *
    * @return the sign
    *
    * @see Utils#sign(float)
    */
   public static Vec2 sign ( final Vec2 v, final Vec2 target ) {

      /* Sign returns an integer; this is inlined to avoid cast. */
      return target.set(v.x < -0.0f ? -1.0f : v.x > 0.0f ? 1.0f : 0.0f, v.y
         < -0.0f ? -1.0f : v.y > 0.0f ? 1.0f : 0.0f);
   }

   /**
    * Subtracts the right vector from the left vector.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the difference
    */
   public static Vec2 sub ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(a.x - b.x, a.y - b.y);
   }

   /**
    * Subtracts the right from the left vector and then normalizes the
    * difference.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the normalized difference
    */
   public static Vec2 subNorm ( final Vec2 a, final Vec2 b,
      final Vec2 target ) {

      final float dx = a.x - b.x;
      final float dy = a.y - b.y;
      final float mInv = Utils.invHypot(dx, dy);
      return target.set(dx * mInv, dy * mInv);
   }

   /**
    * Subtracts the right from the left vector and then normalizes the
    * difference.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    * @param dir    the direction with magnitude
    *
    * @return the normalized difference
    *
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    * @see Vec2#normalize(Vec2, Vec2)
    */
   public static Vec2 subNorm ( final Vec2 a, final Vec2 b, final Vec2 target,
      final Vec2 dir ) {

      Vec2.sub(a, b, dir);
      Vec2.normalize(dir, target);
      return target;
   }

   /**
    * Truncates each component of the vector.
    *
    * @param v      the input vector
    * @param target the output vector
    *
    * @return the truncation
    */
   public static Vec2 trunc ( final Vec2 v, final Vec2 target ) {

      return target.set(( int ) v.x, ( int ) v.y);
   }

   /**
    * Returns a vector representing the center of the texture coordinate
    * system, (0.5, 0.5) .
    *
    * @param target the output vector
    *
    * @return the texture coordinate center
    */
   public static Vec2 uvCenter ( final Vec2 target ) {

      return target.set(0.5f, 0.5f);
   }

   /**
    * Wraps a vector around a periodic range as defined by an upper and lower
    * bound: lower bounds inclusive; upper bounds exclusive. In cases where
    * the lower bound is (0.0, 0.0) , use {@link Vec2#mod(Vec2, Vec2, Vec2)} .
    *
    * @param v      the vector
    * @param lb     the lower bound
    * @param ub     the upper bound
    * @param target the output vector
    *
    * @return the wrapped vector
    *
    * @see Utils#wrap(float, float, float)
    */
   public static Vec2 wrap ( final Vec2 v, final Vec2 lb, final Vec2 ub,
      final Vec2 target ) {

      return target.set(Utils.wrap(v.x, lb.x, ub.x), Utils.wrap(v.y, lb.y,
         ub.y));
   }

   /**
    * Evaluates two vectors like booleans, using the exclusive or (XOR) logic
    * gate.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output vector
    *
    * @return the evaluation
    *
    * @see Utils#xor(float, float)
    */
   public static Vec2 xor ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(Utils.xor(a.x, b.x), Utils.xor(a.y, b.y));
   }

   /**
    * Returns a vector with all components set to zero.
    *
    * @param target the output vector
    *
    * @return the zero vector
    */
   public static Vec2 zero ( final Vec2 target ) {

      return target.set(0.0f, 0.0f);
   }

   /**
    * Internal function for generating 2D array of vectors. The result is in
    * row-major order, but the parameters are supplied in reverse: columns
    * first, then rows.<br>
    * <br>
    * This is separated to make overriding the public grid functions easier.
    * This is protected because it is too easy for integers to be quietly
    * promoted to floats if the signature parameters are confused.
    *
    * @param cols number of columns
    * @param rows number of rows
    * @param lbx  lower bound x
    * @param lby  lower bound y
    * @param ubx  upper bound x
    * @param uby  upper bound y
    *
    * @return the array
    */
   protected static Vec2[][] grid ( final int cols, final int rows,
      final float lbx, final float lby, final float ubx, final float uby ) {

      final int rval = rows < 2 ? 2 : rows;
      final int cval = cols < 2 ? 2 : cols;

      final float iToStep = 1.0f / ( rval - 1.0f );
      final float jToStep = 1.0f / ( cval - 1.0f );

      /* Calculate x values in separate loop. */
      final float[] xs = new float[cval];
      for ( int j = 0; j < cval; ++j ) {
         final float step = j * jToStep;
         xs[j] = ( 1.0f - step ) * lbx + step * ubx;
      }

      final Vec2[][] result = new Vec2[rval][cval];
      for ( int i = 0; i < rval; ++i ) {

         final Vec2[] row = result[i];
         final float step = i * iToStep;
         final float y = ( 1.0f - step ) * lby + step * uby;

         for ( int j = 0; j < cval; ++j ) {
            row[j] = new Vec2(xs[j], y);
         }
      }

      return result;
   }

   /**
    * An abstract class that may serve as an umbrella for any custom
    * comparators of Vec2 s.
    */
   public static abstract class AbstrComparator implements Comparator < Vec2 > {

      /**
       * The default constructor.
       */
      public AbstrComparator ( ) {}

      /**
       * The compare function which must be implemented by sub- (child) classes
       * of this class. Negative one should be returned when the left
       * comparisand, a, is less than the right comparisand, b, by a measure.
       * One should be returned when it is greater. Zero should be returned as a
       * last resort, when a and b are equal or incomparable.
       *
       * @param a the left comparisand
       * @param b the right comparisand
       *
       * @return the comparison
       */
      @Override
      public abstract int compare ( final Vec2 a, final Vec2 b );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * An abstract class to facilitate the creation of vector easing functions.
    */
   public static abstract class AbstrEasing implements Utils.EasingFuncObj <
      Vec2 > {

      /**
       * The default constructor.
       */
      public AbstrEasing ( ) {}

      /**
       * A clamped interpolation between the origin and destination. Defers to
       * an unclamped interpolation, which is to be defined by sub-classes of
       * this class.
       *
       * @param origin the origin vector
       * @param dest   the destination vector
       * @param step   a factor in [0.0, 1.0]
       * @param target the output vector
       *
       * @return the eased vector
       */
      @Override
      public Vec2 apply ( final Vec2 origin, final Vec2 dest, final Float step,
         final Vec2 target ) {

         if ( step <= 0.0f ) { return target.set(origin); }
         if ( step >= 1.0f ) { return target.set(dest); }
         return this.applyUnclamped(origin, dest, step, target);
      }

      /**
       * The interpolation to be defined by subclasses.
       *
       * @param origin the origin vector
       * @param dest   the destination vector
       * @param step   a factor in [0.0, 1.0]
       * @param target the output vector
       *
       * @return the eased vector
       */
      public abstract Vec2 applyUnclamped ( final Vec2 origin, final Vec2 dest,
         final float step, final Vec2 target );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * A linear interpolation functional class.
    */
   public static class Lerp extends AbstrEasing {

      /**
       * The default constructor.
       */
      public Lerp ( ) { super(); }

      /**
       * Eases between two vectors by a step using the formula (1 - t) * a + b .
       * Promotes the step from a float to a double.
       *
       * @param origin the origin vector
       * @param dest   the destination vector
       * @param step   the step
       * @param target the output vector
       *
       * @return the eased vector
       */
      @Override
      public Vec2 applyUnclamped ( final Vec2 origin, final Vec2 dest,
         final float step, final Vec2 target ) {

         final double td = step;
         final double ud = 1.0d - td;
         return target.set(( float ) ( ud * origin.x + td * dest.x ),
            ( float ) ( ud * origin.y + td * dest.y ));
      }

   }

   /**
    * Eases between two vectors with the smooth step formula:
    * <em>t</em><sup>2</sup> ( 3.0 - 2.0 <em>t</em> ) .
    */
   public static class SmoothStep extends AbstrEasing {

      /**
       * The default constructor.
       */
      public SmoothStep ( ) { super(); }

      /**
       * Applies the function.
       *
       * @param origin the origin vector
       * @param dest   the destination vector
       * @param step   the step in a range 0 to 1
       * @param target the output vector
       *
       * @return the smoothed vector
       */
      @Override
      public Vec2 applyUnclamped ( final Vec2 origin, final Vec2 dest,
         final float step, final Vec2 target ) {

         final double td = step;
         final double ts = td * td * ( 3.0d - ( td + td ) );
         final double us = 1.0d - ts;
         return target.set(( float ) ( us * origin.x + ts * dest.x ),
            ( float ) ( us * origin.y + ts * dest.y ));
      }

   }

   /**
    * Compares two vectors on the x axis.
    */
   public static class SortX extends AbstrComparator {

      /**
       * The default constructor.
       */
      public SortX ( ) { super(); }

      /**
       * The compare function.
       *
       * @param a the left comparisand
       * @param b the right comparisand
       *
       * @return the comparison
       */
      @Override
      public int compare ( final Vec2 a, final Vec2 b ) {

         return a.x > b.x ? 1 : a.x < b.x ? -1 : 0;
      }

   }

   /**
    * Compares two vectors on the y axis.
    */
   public static class SortY extends AbstrComparator {

      /**
       * The default constructor.
       */
      public SortY ( ) { super(); }

      /**
       * The compare function.
       *
       * @param a the left comparisand
       * @param b the right comparisand
       *
       * @return the comparison
       */
      @Override
      public int compare ( final Vec2 a, final Vec2 b ) {

         return a.y > b.y ? 1 : a.y < b.y ? -1 : 0;
      }

   }

   /**
    * An iterator, which allows a vector's components to be accessed in an
    * enhanced for loop.
    */
   public static final class V2Iterator implements Iterator < Float > {

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The vector being iterated over.
       */
      private final Vec2 vec;

      /**
       * The default constructor.
       *
       * @param vec the vector to iterate
       */
      public V2Iterator ( final Vec2 vec ) { this.vec = vec; }

      /**
       * Tests to see if the iterator has another value.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext ( ) { return this.index < this.vec.length(); }

      /**
       * Gets the next value in the iterator.
       *
       * @return the value
       *
       * @see Vec2#get(int)
       */
      @Override
      public Float next ( ) { return this.vec.get(this.index++); }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

}
