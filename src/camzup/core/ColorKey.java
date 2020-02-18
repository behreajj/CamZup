package camzup.core;

/**
 * Stores a color at a given step (or percent). Equality and
 * hash are based solely on the step, not on the color it
 * holds.
 */
public class ColorKey implements Comparable < ColorKey >, Cloneable {

   /**
    * The default tolerance used when comparing color keys.
    */
   public static final float DEFAULT_TOLERANCE = 0.0005f;

   /**
    * Tests to see if two keys have approximately the same
    * step.
    *
    * @param a
    *           the left comparisand
    * @param b
    *           the right comparisand
    * @return the evaluation
    */
   public static boolean approx (
         final ColorKey a,
         final ColorKey b ) {

      return ColorKey.approx(a, b, ColorKey.DEFAULT_TOLERANCE);
   }

   /**
    * Tests to see if two keys have approximately the same
    * step.
    *
    * @param a
    *           the left comparisand
    * @param b
    *           the right comparisand
    * @param tolerance
    *           the tolerance
    * @return the evaluation
    * @see Utils#approx(float, float, float)
    */
   public static boolean approx (
         final ColorKey a,
         final ColorKey b,
         final float tolerance ) {

      return Utils.approx(a.step, b.step, tolerance);
   }

   /**
    * The key's color. Abbreviated to 'clr' because 'color' is
    * a data type in Processing IDE.
    */
   public final Color clr = new Color();

   /**
    * The key's step, expected to be in the range [0.0, 1.0].
    */
   public float step = 0.0f;

   /**
    * The default constructor. Creates a clear black color at
    * 0.0 .
    */
   public ColorKey () {

      this.set(0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
   }

   /**
    * Creates a key from a source
    *
    * @param source
    *           the source key
    */
   public ColorKey ( final ColorKey source ) {

      this.set(source);
   }

   /**
    * Creates a key at a given step. All values of the color
    * (including alpha) are set to the step.
    *
    * @param step
    *           the step
    */
   public ColorKey ( final float step ) {

      this.set(step, step, step, step, step);
   }

   /**
    * Creates a new key with a step and color.
    *
    * @param step
    *           the step
    * @param color
    *           the color
    */
   public ColorKey (
         final float step,
         final Color color ) {

      this.set(step, color);
   }

   /**
    * Creates a key by step and color channel. The color's
    * alpha is assumed to be 1.0 .
    *
    * @param step
    *           the step
    * @param red
    *           the red channel
    * @param green
    *           the green channel
    * @param blue
    *           the blue channel
    */
   public ColorKey (
         final float step,
         final float red,
         final float green,
         final float blue ) {

      this.set(step, red, green, blue);
   }

   /**
    * Creates a key by step and color channel.
    *
    * @param step
    *           the step
    * @param red
    *           the red channel
    * @param green
    *           the green channel
    * @param blue
    *           the blue channel
    * @param alpha
    *           the transparency channel
    */
   public ColorKey (
         final float step,
         final float red,
         final float green,
         final float blue,
         final float alpha ) {

      this.set(step, red, green, blue, alpha);
   }

   /**
    * Creates a new key with a step and a color in hexadecimal.
    *
    * @param step
    *           the step
    * @param color
    *           the color
    */
   public ColorKey (
         final float step,
         final int color ) {

      this.set(step, color);
   }

   /**
    * Creates a new key with a step and a string representing a
    * color hexadecimal.
    *
    * @param step
    *           the step
    * @param color
    *           the color string
    */
   public ColorKey (
         final float step,
         final String color ) {

      this.set(step, color);
   }

   /**
    * Returns a String of Python code targeted toward the
    * Blender 2.8x API. This code is brittle and is used for
    * internal testing purposes.
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode () {

      return this.toBlenderCode(1.0f);
   }

   /**
    * Returns a String of Python code targeted toward the
    * Blender 2.8x API. This code is brittle and is used for
    * internal testing purposes.
    *
    * @param gamma
    *           the gamma adjustment
    * @return the string
    */
   @Experimental
   String toBlenderCode ( final float gamma ) {

      return new StringBuilder()
            .append("{\"position\": ")
            .append(Utils.toFixed(this.step, 3))
            .append(", \"color\": ")
            .append(this.clr.toBlenderCode(gamma))
            .append('}')
            .toString();
   }

   /**
    * Tests this key for equality to another based on its step,
    * not color.
    *
    * @param key
    *           the key
    * @return the evaluation
    */
   protected boolean equals ( final ColorKey key ) {

      if (Float.floatToIntBits(this.step) != Float
            .floatToIntBits(key.step)) {
         return false;
      }
      return true;
   }

   /**
    * Returns a new color with this color's components. Java's
    * cloneable interface is problematic; use set or a copy
    * constructor instead.
    *
    * @return a new key
    * @see ColorKey#set(ColorKey)
    */
   @Override
   public ColorKey clone () {

      return new ColorKey(this.step, this.clr);
   }

   /**
    * Returns -1 when this key is less than the comparisand; 1
    * when it is greater than; 0 when the two are 'equal'. The
    * implementation of this method allows collections of keys
    * to be sorted.
    *
    * @param key
    *           the comparisand
    * @return the numeric code
    */
   @Override
   public int compareTo ( final ColorKey key ) {

      return this.step > key.step ? 1
            : this.step < key.step ? -1 : 0;
   }

   /**
    * Tests this key for equivalence with another object.
    *
    * @param obj
    *           the object
    * @return the equivalence
    * @see ColorKey#equals(ColorKey)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (this.getClass() != obj.getClass()) {
         return false;
      }

      return this.equals((ColorKey) obj);
   }

   /**
    * Returns a hash code for this key based on its step, not
    * based on its color.
    *
    * @return the hash code
    * @see Float#floatToIntBits(float)
    */
   @Override
   public int hashCode () {

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.step);
      return hash;
   }

   /**
    * Ressets this key to an initial condition.
    *
    * @return this key
    */
   public ColorKey reset () {

      this.step = 0.0f;
      this.clr.reset();
      return this;
   }

   /**
    * Sets this key from a source.
    *
    * @param source
    *           the source ky
    * @return this key
    */
   @Chainable
   public ColorKey set ( final ColorKey source ) {

      return this.set(source.step, source.clr);
   }

   /**
    * Sets this key with a step and color.
    *
    * @param step
    *           the step
    * @param color
    *           the color
    * @return this key
    */
   @Chainable
   public ColorKey set (
         final float step,
         final Color color ) {

      this.step = step;
      this.clr.set(color);
      return this;
   }

   /**
    * Sets this key by step and color channel. The color's
    * alpha is assumed to be 1.0 .
    *
    * @param step
    *           the step
    * @param red
    *           the red channel
    * @param green
    *           the green channel
    * @param blue
    *           the blue channel
    * @return this key
    */
   @Chainable
   public ColorKey set (
         final float step,
         final float red,
         final float green,
         final float blue ) {

      this.step = step;
      this.clr.set(red, green, blue);

      return this;
   }

   /**
    * Sets this key by step and color channel.
    *
    * @param step
    *           the step
    * @param red
    *           the red channel
    * @param green
    *           the green channel
    * @param blue
    *           the blue channel
    * @param alpha
    *           the transparency channel
    * @return this key
    */
   @Chainable
   public ColorKey set (
         final float step,
         final float red,
         final float green,
         final float blue,
         final float alpha ) {

      this.step = step;
      this.clr.set(red, green, blue, alpha);

      return this;
   }

   /**
    * Sets this key with a step and a color in hexadecimal.
    *
    * @param step
    *           the step
    * @param color
    *           the color
    * @return this key
    */
   @Chainable
   public ColorKey set (
         final float step,
         final int color ) {

      this.step = step;
      Color.fromHex(color, this.clr);
      return this;
   }

   /**
    * Sets this key with a step and a string representing a
    * color hexadecimal.
    *
    * @param step
    *           the step
    * @param color
    *           the color string
    * @return this key
    */
   @Chainable
   public ColorKey set (
         final float step,
         final String color ) {

      this.step = step;
      Color.fromHex(color, this.clr);
      return this;
   }

   /**
    * Returns a string representation of this key.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   /**
    * Returns a string representation of this key.
    *
    * @param places
    *           number of decimal places
    * @return the string
    */
   public String toString ( final int places ) {

      return new StringBuilder()
            .append("{ step: ")
            .append(Utils.toFixed(this.step, 6))
            .append(", clr: ")
            .append(this.clr.toString(places))
            .append(' ').append('}')
            .toString();
   }
}
