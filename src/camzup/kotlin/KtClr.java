package camzup.kotlin;

import camzup.core.Color;
import camzup.core.IUtils;
import camzup.core.Utils;

/**
 * Provides Kotlin operator overloading support for colors.
 */
public class KtClr extends Color {

   public KtClr ( ) { super(); }

   public KtClr ( final byte red, final byte green, final byte blue ) {

      super(red, green, blue);
   }

   public KtClr ( final byte red, final byte green, final byte blue,
      final byte alpha ) {

      super(red, green, blue, alpha);
   }

   public KtClr ( final Color c ) { super(c); }

   public KtClr ( final float red, final float green, final float blue ) {

      super(red, green, blue);
   }

   public KtClr ( final float red, final float green, final float blue,
      final float alpha ) {

      super(red, green, blue, alpha);
   }

   public KtClr ( final String rstr, final String gstr, final String bstr ) {

      super(rstr, gstr, bstr);
   }

   public KtClr ( final String rstr, final String gstr, final String bstr,
      final String astr ) {

      super(rstr, gstr, bstr, astr);
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
    * Returns a new color with the subtraction of the right operand from the
    * instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the subtraction
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
    */
   public KtClr unaryPlus ( ) {

      return new KtClr(Utils.clamp01(this.r), Utils.clamp01(this.g), Utils
         .clamp01(this.b), Utils.clamp01(this.a));
   }

}
