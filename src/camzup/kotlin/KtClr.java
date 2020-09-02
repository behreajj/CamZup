package camzup.kotlin;

import java.util.Iterator;
import java.util.NoSuchElementException;

import camzup.core.Color;
import camzup.core.IUtils;
import camzup.core.Utils;

/**
 * Provides Kotlin operator overloading support for colors. Color
 * operations retain the alpha channel of the left operand, or of the color
 * they are mutating, so these operations are not commutative.
 */
public class KtClr extends Color implements Iterable < Float > {

   /**
    * The default constructor. Creates a white color.
    */
   public KtClr ( ) { super(); }

   /**
    * Creates a color from bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    */
   public KtClr ( final byte red, final byte green, final byte blue ) {

      super(red, green, blue);
   }

   /**
    * Creates a color from bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    * @param alpha the alpha channel
    */
   public KtClr ( final byte red, final byte green, final byte blue,
      final byte alpha ) {

      super(red, green, blue, alpha);
   }

   /**
    * Creates a color from a source.
    *
    * @param c the source color
    */
   public KtClr ( final Color c ) { super(c); }

   /**
    * Creates a color out of red, green and blue channels. The alpha channel
    * defaults to 1.0 .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    */
   public KtClr ( final float red, final float green, final float blue ) {

      super(red, green, blue);
   }

   /**
    * Creates a color out of red, green, blue and alpha channels.
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    * @param alpha the alpha channel
    */
   public KtClr ( final float red, final float green, final float blue,
      final float alpha ) {

      super(red, green, blue, alpha);
   }

   /**
    * Attempts to construct a color from Strings using
    * {@link Float#parseFloat(String)} . If a NumberFormatException is thrown,
    * the component is set to zero. If a NumberFormatException is thrown, the
    * component is set to zero for red, green and blue; to one for alpha.
    *
    * @param rstr the red string
    * @param gstr the green string
    * @param bstr the blue string
    */
   public KtClr ( final String rstr, final String gstr, final String bstr ) {

      super(rstr, gstr, bstr);
   }

   /**
    * Attempts to construct a color from Strings using
    * {@link Float#parseFloat(String)} . If a NumberFormatException is thrown,
    * the component is set to zero. If a NumberFormatException is thrown, the
    * component is set to zero for red, green and blue; to one for alpha.
    *
    * @param rstr the red string
    * @param gstr the green string
    * @param bstr the blue string
    * @param astr the alpha string
    */
   public KtClr ( final String rstr, final String gstr, final String bstr,
      final String astr ) {

      super(rstr, gstr, bstr, astr);
   }

   /**
    * Tests to see if the vector contains a value.
    *
    * @param v the value
    *
    * @return the evaluation
    */
   public boolean contains ( final float v ) {

      return Utils.approx(this.a, v) || Utils.approx(this.b, v) || Utils.approx(
         this.g, v) || Utils.approx(this.r, v);
   }

   /**
    * Returns a new color decremented by {@link IUtils#ONE_255},
    * {@value IUtils#ONE_255}. For interoperability with Kotlin:
    * <code>--a</code> (prefix) or <code>a--</code> (postfix). Per the
    * specification, <em>does not mutate the color in place</em>.
    *
    * @return the decremented vector
    *
    * @see KtClr#minus(float)
    */
   public KtClr dec ( ) { return this.minus(IUtils.ONE_255); }

   /**
    * Returns a new color with the division of the instance by the right
    * operand. For interoperability with Kotlin: <code>a / b</code> . <em>Does
    * not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the quotient
    *
    * @see Utils#clamp01(float)
    */
   public KtClr div ( final Color c ) {

      return new KtClr(Utils.clamp01(Utils.div(this.r, c.r)), Utils.clamp01(
         Utils.div(this.g, c.g)), Utils.clamp01(Utils.div(this.b, c.b)), Utils
            .clamp01(this.a));
   }

   /**
    * Returns a new color with the division of the instance by the right
    * operand. For interoperability with Kotlin: <code>a / b</code> . <em>Does
    * not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the quotient
    *
    * @see Utils#clamp01(float)
    */
   public KtClr div ( final float c ) {

      return c != 0.0f ? new KtClr(Utils.clamp01(this.r / c), Utils.clamp01(
         this.g / c), Utils.clamp01(this.b / c), Utils.clamp01(this.a))
         : new KtClr(0.0f, 0.0f, 0.0f, Utils.clamp01(this.a));
   }

   /**
    * Divides the instance by the right operand (mutates the color in place).
    * For interoperability with Kotlin: <code>a /= b</code> .
    *
    * @param c the right operand
    *
    * @see Utils#div(float, float)
    * @see Utils#clamp01(float)
    */
   public void divAssign ( final Color c ) {

      this.r = Utils.clamp01(Utils.div(this.r, c.r));
      this.g = Utils.clamp01(Utils.div(this.g, c.g));
      this.b = Utils.clamp01(Utils.div(this.b, c.b));
      this.a = Utils.clamp01(this.a);
   }

   /**
    * Divides the instance by the right operand (mutates the color in place).
    * For interoperability with Kotlin: <code>a /= b</code> .
    *
    * @param c the right operand
    *
    * @see Utils#clamp01(float)
    */
   public void divAssign ( final float c ) {

      if ( c != 0.0f ) {
         this.r = Utils.clamp01(this.r / c);
         this.g = Utils.clamp01(this.g / c);
         this.b = Utils.clamp01(this.b / c);
         this.a = Utils.clamp01(this.a);
      } else {
         this.r = 0.0f;
         this.g = 0.0f;
         this.b = 0.0f;
         this.a = Utils.clamp01(this.a);
      }
   }

   /**
    * Returns a new color incremented by {@link IUtils#ONE_255},
    * {@value IUtils#ONE_255}. For interoperability with Kotlin:
    * <code>++a</code> (prefix) or <code>a++</code> (postfix). Per the
    * specification, <em>does not mutate the color in place</em>.
    *
    * @return the incremented color
    *
    * @see KtClr#plus(float)
    */
   public KtClr inc ( ) { return this.plus(IUtils.ONE_255); }

   /**
    * Returns an iterator for this color, which allows its components to be
    * accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public Iterator < Float > iterator ( ) { return new ClrIterator(this); }

   /**
    * Returns a new color with the subtraction of the right operand from the
    * instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the subtraction
    *
    * @see Utils#clamp01(float)
    */
   public KtClr minus ( final Color c ) {

      return new KtClr(Utils.clamp01(this.r - c.r), Utils.clamp01(this.g - c.g),
         Utils.clamp01(this.b - c.b), Utils.clamp01(this.a));
   }

   /**
    * Returns a new color with the subtraction of the right operand from the
    * instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the subtraction
    *
    * @see Utils#clamp01(float)
    */
   public KtClr minus ( final float c ) {

      return new KtClr(Utils.clamp01(this.r - c), Utils.clamp01(this.g - c),
         Utils.clamp01(this.b - c), Utils.clamp01(this.a));
   }

   /**
    * Subtracts the right operand from the instance (mutates the vector in
    * place). For interoperability with Kotlin: <code>a -= b</code> .
    *
    * @param c the right operand
    *
    * @see Utils#clamp01(float)
    */
   public void minusAssign ( final Color c ) {

      this.r = Utils.clamp01(this.r - c.r);
      this.g = Utils.clamp01(this.g - c.g);
      this.b = Utils.clamp01(this.b - c.b);
      this.a = Utils.clamp01(this.a);
   }

   /**
    * Subtracts the right operand from the instance (mutates the vector in
    * place). For interoperability with Kotlin: <code>a -= b</code> .
    *
    * @param c the right operand
    *
    * @see Utils#clamp01(float)
    */
   public void minusAssign ( final float c ) {

      this.r = Utils.clamp01(this.r - c);
      this.g = Utils.clamp01(this.g - c);
      this.b = Utils.clamp01(this.b - c);
      this.a = Utils.clamp01(this.a);
   }

   /**
    * Returns a new color with the addition of the right operand to the
    * instance. For interoperability with Kotlin: <code>a + b</code> .
    * <em>Does not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the subtraction
    *
    * @see Utils#clamp01(float)
    */
   public KtClr plus ( final Color c ) {

      return new KtClr(Utils.clamp01(this.r + c.r), Utils.clamp01(this.g + c.g),
         Utils.clamp01(this.b + c.b), Utils.clamp01(this.a));
   }

   /**
    * Returns a new color with the addition of the right operand to the
    * instance. For interoperability with Kotlin: <code>a + b</code> .
    * <em>Does not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the subtraction
    *
    * @see Utils#clamp01(float)
    */
   public KtClr plus ( final float c ) {

      return new KtClr(Utils.clamp01(this.r + c), Utils.clamp01(this.g + c),
         Utils.clamp01(this.b + c), Utils.clamp01(this.a));
   }

   /**
    * Adds the right operand to the instance (mutates the vector in place).
    * For interoperability with Kotlin: <code>a += b</code> .
    *
    * @param c the right operand
    *
    * @see Utils#clamp01(float)
    */
   public void plusAssign ( final Color c ) {

      this.r = Utils.clamp01(this.r + c.r);
      this.g = Utils.clamp01(this.g + c.g);
      this.b = Utils.clamp01(this.b + c.b);
      this.a = Utils.clamp01(this.a);
   }

   /**
    * Adds the right operand to the instance (mutates the vector in place).
    * For interoperability with Kotlin: <code>a += b</code> .
    *
    * @param c the right operand
    *
    * @see Utils#clamp01(float)
    */
   public void plusAssign ( final float c ) {

      this.r = Utils.clamp01(this.r + c);
      this.g = Utils.clamp01(this.g + c);
      this.b = Utils.clamp01(this.b + c);
      this.a = Utils.clamp01(this.a);
   }

   /**
    * Returns a new color with the unsigned remainder (<code>mod</code>) of
    * the instance and the right operand. For interoperability with Kotlin:
    * <code>a % b</code> . <em>Does not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the signed remainder
    *
    * @see Utils#mod(float, float)
    * @see Utils#clamp01(float)
    */
   public KtClr rem ( final Color c ) {

      return new KtClr(Utils.clamp01(Utils.mod(this.r, c.r)), Utils.clamp01(
         Utils.mod(this.g, c.g)), Utils.clamp01(Utils.mod(this.b, c.b)), Utils
            .clamp01(this.a));
   }

   /**
    * Returns a new color with the unsigned remainder (<code>mod</code>) of
    * the instance and the right operand. For interoperability with Kotlin:
    * <code>a % b</code> . <em>Does not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the signed remainder
    *
    * @see Utils#modUnchecked(float, float)
    * @see Utils#clamp01(float)
    */
   public KtClr rem ( final float c ) {

      if ( c != 0.0f ) {
         return new KtClr(Utils.clamp01(Utils.modUnchecked(this.r, c)), Utils
            .clamp01(Utils.modUnchecked(this.g, c)), Utils.clamp01(Utils
               .modUnchecked(this.b, c)), Utils.clamp01(this.a));
      }
      return new KtClr(Utils.clamp01(this.r), Utils.clamp01(this.g), Utils
         .clamp01(this.b), Utils.clamp01(this.a));
   }

   /**
    * Assigns the unsigned remainder (<code>mod</code>) of the instance and
    * the right operand to the instance (mutates the vector in place). For
    * interoperability with Kotlin: <code>a %= b</code> .
    *
    * @param c the right operand
    *
    * @see Utils#mod(float, float)
    * @see Utils#clamp01(float)
    */
   public void remAssign ( final Color c ) {

      this.r = Utils.clamp01(Utils.mod(this.r, c.r));
      this.g = Utils.clamp01(Utils.mod(this.g, c.g));
      this.b = Utils.clamp01(Utils.mod(this.b, c.b));
      this.a = Utils.clamp01(this.a);
   }

   /**
    * Assigns the unsigned remainder (<code>mod</code>) of the instance and
    * the right operand to the instance (mutates the vector in place). For
    * interoperability with Kotlin: <code>a %= b</code> .
    *
    * @param c the right operand
    *
    * @see Utils#modUnchecked(float, float)
    * @see Utils#clamp01(float)
    */
   public void remAssign ( final float c ) {

      if ( c != 0.0f ) {
         this.r = Utils.clamp01(Utils.modUnchecked(this.r, c));
         this.g = Utils.clamp01(Utils.modUnchecked(this.g, c));
         this.b = Utils.clamp01(Utils.modUnchecked(this.b, c));
         this.a = Utils.clamp01(this.a);
      } else {
         this.r = Utils.clamp01(this.r);
         this.g = Utils.clamp01(this.g);
         this.b = Utils.clamp01(this.b);
         this.a = Utils.clamp01(this.a);
      }
   }

   /**
    * Returns a new color with the product of the instance and the right
    * operand. For interoperability with Kotlin: <code>a * b</code> . <em>Does
    * not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the product
    *
    * @see Utils#clamp01(float)
    */
   public KtClr times ( final Color c ) {

      return new KtClr(Utils.clamp01(this.r * c.r), Utils.clamp01(this.g * c.g),
         Utils.clamp01(this.b * c.b), Utils.clamp01(this.a));
   }

   /**
    * Returns a new color with the product of the instance and the right
    * operand. For interoperability with Kotlin: <code>a * b</code> . <em>Does
    * not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the product
    *
    * @see Utils#clamp01(float)
    */
   public KtClr times ( final float c ) {

      return new KtClr(Utils.clamp01(this.r * c), Utils.clamp01(this.g * c),
         Utils.clamp01(this.b * c), Utils.clamp01(this.a));
   }

   /**
    * Multiplies the right operand with the instance (mutates the color in
    * place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param c the right operand
    *
    * @see Utils#clamp01(float)
    */
   public void timesAssign ( final Color c ) {

      this.r = Utils.clamp01(this.r * c.r);
      this.g = Utils.clamp01(this.g * c.g);
      this.b = Utils.clamp01(this.b * c.b);
      this.a = Utils.clamp01(this.a);
   }

   /**
    * Multiplies the right operand with the instance (mutates the color in
    * place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param c the right operand
    *
    * @see Utils#clamp01(float)
    */
   public void timesAssign ( final float c ) {

      this.r = Utils.clamp01(this.r * c);
      this.g = Utils.clamp01(this.g * c);
      this.b = Utils.clamp01(this.b * c);
      this.a = Utils.clamp01(this.a);
   }

   /**
    * Inverts a color by subtracting the red, green and blue channels from
    * one. Similar to bitNot, except alpha is not affected. For
    * interoperability with Kotlin: <code>-a</code> . <em>Does not mutate the
    * color in place</em>.
    *
    * @return the negative
    *
    * @see Utils#max(float, float)
    */
   public KtClr unaryMinus ( ) {

      return new KtClr(Utils.max(0.0f, 1.0f - this.r), Utils.max(0.0f, 1.0f
         - this.g), Utils.max(0.0f, 1.0f - this.b), Utils.clamp01(this.a));
   }

   /**
    * Returns a new color with the positive copy of the instance. For
    * interoperability with Kotlin: <code>+a</code> . <em>Does not mutate the
    * color in place</em>.
    *
    * @return the positive
    *
    * @see Utils#clamp01(float)
    */
   public KtClr unaryPlus ( ) {

      return new KtClr(Utils.clamp01(this.r), Utils.clamp01(this.g), Utils
         .clamp01(this.b), Utils.clamp01(this.a));
   }

   /**
    * An iterator, which allows a color's components to be accessed in an
    * enhanced for loop.
    */
   public static final class ClrIterator implements Iterator < Float > {

      /**
       * The color being iterated over.
       */
      private final KtClr clr;

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The default constructor.
       *
       * @param c the color to iterate
       */
      public ClrIterator ( final KtClr c ) {

         this.clr = c;
      }

      /**
       * Tests to see if the iterator has another value.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext ( ) { return this.index < ClrIterator.LENGTH; }

      /**
       * Gets the next value in the iterator.
       *
       * @see Color#get(int)
       *
       * @return the value
       */
      @Override
      public Float next ( ) {

         if ( !this.hasNext() ) { throw new NoSuchElementException(); }
         return this.clr.get(this.index++);
      }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

      /**
       * The length of the vector.
       */
      public static final int LENGTH = 4;

   }

}
