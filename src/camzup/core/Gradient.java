package camzup.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * A mutable, extensible class that contains a list of keys
 * which hold colors at steps in the range [0.0, 1.0].
 * Allows smooth color transitions to be evaluated for a
 * factor.
 */
public class Gradient implements Iterable < Gradient.Key > {

   /**
    * A color key which stores a color at a given step (or
    * percent). Key equality and hash is based solely on the
    * step, not on the color it holds.
    */
   public static class Key implements Comparable < Key >, Cloneable {

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
       * @see Utils#approxFast(float, float, float)
       */
      public static boolean approx (
            final Key a,
            final Key b,
            final float tolerance ) {

         return Utils.approxFast(a.step, b.step, tolerance);
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
      public Key () {

         this.set(0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
      }

      /**
       * Creates a key at a given step. All values of the color
       * (including alpha) are set to the step.
       *
       * @param step
       *           the step
       */
      public Key ( final float step ) {

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
      public Key ( final float step, final Color color ) {

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
      public Key (
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
      public Key (
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
      public Key ( final float step, final int color ) {

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
      public Key ( final float step, final String color ) {

         this.set(step, color);
      }

      /**
       * Creates a key from a source
       *
       * @param source
       *           the source key
       */
      public Key ( final Key source ) {

         this.set(source);
      }

      /**
       * Tests this key for equality to another based on its step,
       * not color.
       *
       * @param key
       *           the key
       * @return the evaluation
       */
      protected boolean equals ( final Key key ) {

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
       * @see Key#set(Key)
       * @see Key#Key(Key)
       */
      @Override
      public Key clone () {

         return new Key(this.step, this.clr);
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
      public int compareTo ( final Key key ) {

         return this.step > key.step ? 1
               : this.step < key.step ? -1 : 0;
      }

      /**
       * Tests this key for equivalence with another object.
       *
       * @param obj
       *           the object
       * @return the equivalence
       * @see Key#equals(Key)
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

         return this.equals((Key) obj);
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
       * Sets this key with a step and color.
       *
       * @param step
       *           the step
       * @param color
       *           the color
       * @return this key
       */
      @Chainable
      public Key set ( final float step, final Color color ) {

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
      public Key set (
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
      public Key set (
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
      public Key set ( final float step, final int color ) {

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
      public Key set ( final float step, final String color ) {

         this.step = step;
         Color.fromHex(color, this.clr);
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
      public Key set ( final Key source ) {

         return this.set(source.step, source.clr);
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
               .append(Utils.toFixed(this.step, places))
               .append(", clr: ")
               .append(this.clr.toString(places))
               .append(' ').append('}')
               .toString();
      }
   }

   /**
    * The default easing function, lerp RGBA.
    */
   private static Color.AbstrEasing EASING = new Color.LerpRgba();

   /**
    * Gets the string representation of the default gradient
    * easing function.
    *
    * @return the string
    */
   public static String getEasingString () {

      return Gradient.EASING.toString();
   }

   /**
    * Returns the Magma color palette, consisting of 16 keys.
    *
    * @param target
    *           the output gradient
    * @return the gradient
    */
   public static Gradient paletteMagma ( final Gradient target ) {

      final TreeSet < Key > keys = target.keys;
      keys.clear();

      keys.add(new Key(0.0f, 0.988235f, 1.0f, 0.698039f));
      keys.add(new Key(0.06666667f, 0.98719f, 0.843137f, 0.562092f));
      keys.add(new Key(0.13333333f, 0.984314f, 0.694118f, 0.446275f));
      keys.add(new Key(0.2f, 0.981176f, 0.548235f, 0.35451f));

      keys.add(new Key(0.26666667f, 0.962353f, 0.412549f, 0.301176f));
      keys.add(new Key(0.33333333f, 0.912418f, 0.286275f, 0.298039f));
      keys.add(new Key(0.4f, 0.824314f, 0.198431f, 0.334902f));
      keys.add(new Key(0.46666667f, 0.703268f, 0.142484f, 0.383007f));

      keys.add(new Key(0.53333333f, 0.584052f, 0.110588f, 0.413856f));
      keys.add(new Key(0.6f, 0.471373f, 0.080784f, 0.430588f));
      keys.add(new Key(0.66666667f, 0.36732f, 0.045752f, 0.43268f));
      keys.add(new Key(0.73333333f, 0.267974f, 0.002353f, 0.416732f));

      keys.add(new Key(0.8f, 0.174118f, 0.006275f, 0.357647f));
      keys.add(new Key(0.86666667f, 0.093856f, 0.036863f, 0.232941f));
      keys.add(new Key(0.93333333f, 0.040784f, 0.028758f, 0.110327f));
      keys.add(new Key(1.0f, 0.0f, 0.0f, 0.019608f));

      return target;
   }

   /**
    * Returns seven primary and secondary colors: red, yellow,
    * green, cyan, blue, magenta and red. Red is repeated so
    * the gradient is periodic.
    *
    * @param target
    *           the output gradient
    * @return the gradient
    */
   public static Gradient paletteRgb ( final Gradient target ) {

      final TreeSet < Key > keys = target.keys;
      keys.clear();

      keys.add(new Key(0.0f, 1.0f, 0.0f, 0.0f)); /* Red */
      keys.add(new Key(0.16666667f, 1.0f, 1.0f, 0.0f)); /* Yellow */
      keys.add(new Key(0.33333333f, 0.0f, 1.0f, 0.0f)); /* Green */
      keys.add(new Key(0.5f, 0.0f, 1.0f, 1.0f)); /* Cyan */
      keys.add(new Key(0.66666667f, 0.0f, 0.0f, 1.0f)); /* Blue */
      keys.add(new Key(0.83333333f, 1.0f, 0.0f, 1.0f)); /* Magenta */
      keys.add(new Key(1.0f, 1.0f, 0.0f, 0.0f)); /* Red */

      return target;
   }

   /**
    * Returns the Viridis color palette, consisting of 16 keys.
    *
    * @param target
    *           the output gradient
    * @return the gradient
    */
   public static Gradient paletteViridis ( final Gradient target ) {

      final TreeSet < Key > keys = target.keys;
      keys.clear();

      keys.add(new Key(0.0f, 0.266667f, 0.003922f, 0.329412f));
      keys.add(new Key(0.06666667f, 0.282353f, 0.100131f, 0.420654f));
      keys.add(new Key(0.13333333f, 0.276078f, 0.184575f, 0.487582f));
      keys.add(new Key(0.2f, 0.254902f, 0.265882f, 0.527843f));

      keys.add(new Key(0.26666667f, 0.221961f, 0.340654f, 0.549281f));
      keys.add(new Key(0.33333333f, 0.192157f, 0.405229f, 0.554248f));
      keys.add(new Key(0.4f, 0.164706f, 0.469804f, 0.556863f));
      keys.add(new Key(0.46666667f, 0.139869f, 0.534379f, 0.553464f));

      keys.add(new Key(0.53333333f, 0.122092f, 0.595033f, 0.543007f));
      keys.add(new Key(0.6f, 0.139608f, 0.658039f, 0.516863f));
      keys.add(new Key(0.66666667f, 0.210458f, 0.717647f, 0.471895f));
      keys.add(new Key(0.73333333f, 0.326797f, 0.773595f, 0.407582f));

      keys.add(new Key(0.8f, 0.477647f, 0.821961f, 0.316863f));
      keys.add(new Key(0.86666667f, 0.648366f, 0.858039f, 0.208889f));
      keys.add(new Key(0.93333333f, 0.825098f, 0.884967f, 0.114771f));
      keys.add(new Key(1.0f, 0.992157f, 0.905882f, 0.145098f));

      return target;
   }

   /**
    * Sets the easing function by which colors in the gradient
    * are interpolated.
    *
    * @param easing
    *           the easing function
    */
   public static void setEasing ( final Color.AbstrEasing easing ) {

      if (easing != null) {
         Gradient.EASING = easing;
      }
   }

   /**
    * A temporary variable to hold queries in evaluation
    * functions.
    */
   protected final transient Gradient.Key query = new Key();

   /**
    * The set of keys.
    */
   public final TreeSet < Gradient.Key > keys = new TreeSet <>();

   /**
    * Creates a gradient with two default color keys, clear
    * black at 0.0 and opaque white at 1.0.
    *
    * @see Color#clearBlack(Color)
    * @see Color#white(Color)
    */
   public Gradient () {

      this.keys.add(new Key(0.0f, 0.0f, 0.0f, 0.0f, 0.0f));
      this.keys.add(new Key(1.0f, 1.0f, 1.0f, 1.0f, 1.0f));
   }

   /**
    * Creates a gradient from a color; an additional color key,
    * white at 0.0, is created.
    *
    * @param color
    *           the color
    * @see Color#white(Color)
    */
   public Gradient ( final Color color ) {

      this.keys.add(new Key(0.0f, 1.0f, 1.0f, 1.0f, 1.0f));
      this.keys.add(new Key(1.0f, color));
   }

   /**
    * Creates a gradient from a list of colors; the resultant
    * keys are evenly distributed over the range [0.0, 1.0].
    *
    * @param colors
    *           the colors
    */
   public Gradient ( final Color... colors ) {

      this.append(colors);
   }

   /**
    * Creates a gradient from a color integer; an additional
    * color key, white at 0.0, is created.
    *
    * @param color
    *           the color
    * @see Color#white(Color)
    */
   public Gradient ( final int color ) {

      this.keys.add(new Key(0.0f, 1.0f, 1.0f, 1.0f, 1.0f));
      this.keys.add(new Key(1.0f, color));
   }

   /**
    * Creates a gradient from a list of color integers; the
    * resultant keys are evenly distributed over the range
    * [0.0, 1.0].
    *
    * @param colors
    *           the colors
    */
   public Gradient ( final int... colors ) {

      this.append(colors);
   }

   /**
    * Creates a gradient from color keys.
    *
    * @param keys
    *           the color keys
    */
   public Gradient ( final Key... keys ) {

      this.append(keys);
   }

   /**
    * Creates a gradient from a color string; an additional
    * color key, white at 0.0, is created.
    *
    * @param color
    *           the color
    * @see Color#white(Color)
    */
   public Gradient ( final String color ) {

      this.keys.add(new Key(0.0f, 1.0f, 1.0f, 1.0f, 1.0f));
      this.keys.add(new Key(1.0f, color));
   }

   /**
    * Creates a gradient from a list of color strings; the
    * resultant keys are evenly distributed over the range
    * [0.0, 1.0].
    *
    * @param colors
    *           the colors
    */
   public Gradient ( final String... colors ) {

      this.append(colors);
   }

   /**
    * Tests to see if this gradient equals another.
    *
    * @param other
    *           the other gradient
    * @return the evaluation
    */
   protected boolean equals ( final Gradient other ) {

      if (this.keys == null) {
         if (other.keys != null) {
            return false;
         }
      } else if (!this.keys.equals(other.keys)) {
         return false;
      }
      return true;
   }

   /**
    * Helper function. Shifts existing keys to the left when a
    * new color without a key is added to the gradient.
    *
    * @param added
    *           number of new items
    */
   protected void shiftKeysLeft ( final int added ) {

      final Iterator < Gradient.Key > itr = this.keys.iterator();
      int i = 0;
      final float scalar = 1.0f / (this.keys.size() + added - 1.0f);
      while (itr.hasNext()) {
         final Gradient.Key key = itr.next();
         key.step = key.step * i++ * scalar;
      }
   }

   // @Chainable
   // public Gradient prepend ( final int... colors ) {
   //
   // final int len = colors.length;
   // this.shiftKeysRight(len);
   // final int oldLen = this.keys.size();
   // final float denom = 1.0f / (oldLen + len - 1.0f);
   // for (int i = 0; i < len; ++i) {
   // this.keys.add(new Key((oldLen + i) * denom, colors[i]));
   // }
   // return this;
   // }

   // public void shiftKeysRight ( final int added ) {
   //
   // final Iterator < Gradient.Key > itr =
   // this.keys.iterator();
   // int i = added + 1;
   // final float scalar = 1.0f / (this.keys.size() + 1);
   // while (itr.hasNext()) {
   // final Gradient.Key key = itr.next();
   // key.step = key.step * i++ * scalar;
   // }
   // }

   /**
    * Appends a color at step 1.0 . Shifts existing keys to the
    * left.
    *
    * @param color
    *           the color
    * @return this gradient
    */
   @Chainable
   public Gradient append ( final Color color ) {

      this.shiftKeysLeft(1);
      this.keys.add(new Key(1.0f, color));
      return this;
   }

   /**
    * Appends a list of colors to this gradient. Shifts
    * existing keys to the left.
    *
    * @param colors
    *           the colors
    * @return this gradient
    * @see Gradient#shiftKeysLeft(int)
    * @see TreeSet#size()
    * @see TreeSet#add(Object)
    */
   @Chainable
   public Gradient append ( final Color... colors ) {

      final int len = colors.length;
      this.shiftKeysLeft(len);
      final int oldLen = this.keys.size();
      final float denom = 1.0f / (oldLen + len - 1.0f);
      for (int i = 0; i < len; ++i) {
         this.keys.add(new Key((oldLen + i) * denom, colors[i]));
      }
      return this;
   }

   /**
    * Appends a color at step 1.0 . Shifts existing keys to the
    * left.
    *
    * @param color
    *           the color
    * @return this gradient
    * @see Gradient#shiftKeysLeft(int)
    * @see TreeSet#add(Object)
    */
   @Chainable
   public Gradient append ( final int color ) {

      this.shiftKeysLeft(1);
      this.keys.add(new Key(1.0f, color));
      return this;
   }

   /**
    * Appends a list of color integers to this gradient. Shifts
    * existing keys to the left.
    *
    * @param colors
    *           the colors
    * @return this gradient
    * @see Gradient#shiftKeysLeft(int)
    * @see TreeSet#size()
    * @see TreeSet#add(Object)
    */
   @Chainable
   public Gradient append ( final int... colors ) {

      final int len = colors.length;
      this.shiftKeysLeft(len);
      final int oldLen = this.keys.size();
      final float denom = 1.0f / (oldLen + len - 1.0f);
      for (int i = 0; i < len; ++i) {
         this.keys.add(new Key((oldLen + i) * denom, colors[i]));
      }
      return this;
   }

   /**
    * Appends color keys to this gradient. If a color key
    * already exists at a given step, it will not be added.
    *
    * @param keys
    *           the keys
    * @return this gradient
    * @see TreeSet#add(Object)
    */
   @Chainable
   public Gradient append ( final Key... keys ) {

      final int len = keys.length;
      for (int i = 0; i < len; ++i) {
         final Key key = keys[i];
         this.keys.add(key);
      }
      return this;
   }

   /**
    * Appends a color at step 1.0 . Shifts existing keys to the
    * left.
    *
    * @param color
    *           the color
    * @return this gradient
    */
   @Chainable
   public Gradient append ( final String color ) {

      this.shiftKeysLeft(1);
      this.keys.add(new Key(1.0f, color));
      return this;
   }

   /**
    * Appends a list of color strings to this gradient. Shifts
    * existing keys to the left.
    *
    * @param colors
    *           the colors
    * @return this gradient
    * @see Gradient#shiftKeysLeft(int)
    * @see TreeSet#size()
    * @see TreeSet#add(Object)
    */
   @Chainable
   public Gradient append ( final String... colors ) {

      final int len = colors.length;
      this.shiftKeysLeft(len);
      final int oldLen = this.keys.size();
      final float denom = 1.0f / (oldLen + len - 1.0f);
      for (int i = 0; i < len; ++i) {
         this.keys.add(new Key((oldLen + i) * denom, colors[i]));
      }
      return this;
   }

   /**
    * Returns the least key in this gradient greater than or
    * equal to the given step. If there is no key, returns the
    * last key instead of null.
    *
    * @param step
    *           the step
    * @return the key
    * @see TreeSet#ceiling(Object)
    * @see TreeSet#last()
    */
   public Key ceil ( final float step ) {

      this.query.step = step;
      final Key result = this.keys.ceiling(this.query);
      return result == null ? this.keys.last() : result;
   }

   /**
    * Distributes this gradient's color key's evenly through
    * the range [0.0, 1.0] .
    *
    * @param keyArr
    *           a temporary list
    * @return this gradient
    * @see List#clear()
    * @see List#addAll(java.util.Collection)
    */
   public Gradient distribute ( final List < Key > keyArr ) {

      keyArr.clear();
      keyArr.addAll(this.keys);

      this.keys.clear();

      final Iterator < Key > itr = keyArr.iterator();
      int i = 0;
      final float denom = 1.0f / (keyArr.size() - 1.0f);
      while (itr.hasNext()) {
         final Key key = itr.next();
         key.step = i++ * denom;
      }

      this.keys.addAll(keyArr);
      return this;
   }

   /**
    * Tests to see if this gradient equals an object
    *
    * @param obj
    *           the object
    * @return the evaluation
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

      return this.equals((Gradient) obj);
   }

   /**
    * Evaluates a step without checking to see if it is greater
    * than 1.0 or less than 0.0. Uses the static easing
    * function belonging to the class.
    *
    * @param step
    *           the step
    * @param target
    *           the output color
    * @return the color
    * @see TreeSet#floor(Object)
    * @see TreeSet#ceiling(Object)
    * @see TreeSet#first()
    * @see TreeSet#last()
    */
   public Color eval (
         final float step,
         final Color target ) {

      return this.eval(step, Gradient.EASING, target);
   }

   /**
    * Evaluates a step without checking to see if it is greater
    * than 1.0 or less than 0.0.
    *
    * @param step
    *           the step
    * @param easing
    *           the easing function
    * @param target
    *           the output color
    * @return the color
    * @see TreeSet#floor(Object)
    * @see TreeSet#ceiling(Object)
    * @see TreeSet#first()
    * @see TreeSet#last()
    */
   public Color eval (
         final float step,
         final Color.AbstrEasing easing,
         final Color target ) {

      this.query.step = step;

      final Key prev = this.keys.floor(this.query);
      if (prev == null) {
         return target.set(this.keys.first().clr);
      }

      final Key next = this.keys.ceiling(this.query);
      if (next == null) {
         return target.set(this.keys.last().clr);
      }

      return easing.applyUnclamped(
            next.clr, prev.clr,
            // Utils.div(step - next.step,
            // prev.step - next.step),
            (step - next.step) /
            (prev.step - next.step),
            target);
   }

   /**
    * Evaluates an array of colors given a supplied count.
    *
    * @param count
    *           the count
    * @return the array
    */
   public Color[] evalRange ( final int count ) {

      return this.evalRange(count, Gradient.EASING);
   }

   /**
    * Evaluates an array of colors given a supplied count.
    *
    * @param count
    *           the count
    * @param easing
    *           the easing function
    * @return the array
    */
   public Color[] evalRange (
         final int count,
         final Color.AbstrEasing easing ) {

      final int vcount = count < 3 ? 3 : count;
      final Color[] result = new Color[vcount];
      final float toPercent = 1.0f / (vcount - 1.0f);
      for (int i = 0; i < vcount; ++i) {
         result[i] = this.eval(
               i * toPercent, easing, new Color());
      }
      return result;

   }

   /**
    * Retrieves the first key in this gradient.
    *
    * @return the first key
    */
   public Key first () {

      return this.keys.first();
   }

   /**
    * Returns the greatest key in this gradient less than or
    * equal to the given step. If there is no key, returns the
    * first key instead of null.
    *
    * @param step
    *           the step
    * @return the key
    * @see TreeSet#floor(Object)
    * @see TreeSet#first()
    */
   public Key floor ( final float step ) {

      this.query.step = step;
      final Key result = this.keys.floor(this.query);
      return result == null ? this.keys.first() : result;
   }

   /**
    * Returns a hash code for this gradient based on its list
    * of keys.
    *
    * @return the hash code
    */
   @Override
   public int hashCode () {

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL
            ^ (this.keys == null ? 0 : this.keys.hashCode());
      return hash;
   }

   /**
    * Returns the least key in this gradient greater than the
    * given step. If there is no key, returns the last key
    * instead of null.
    *
    * @param step
    *           the step
    * @return the key
    * @see TreeSet#higher(Object)
    * @see TreeSet#last()
    */
   public Key higher ( final float step ) {

      this.query.step = step;
      final Key result = this.keys.higher(this.query);
      return result == null ? this.keys.last() : result;
   }

   /**
    * Returns an iterator for this gradient, which allows its
    * components to be accessed in an enhanced for-loop.
    *
    * @return the iterator
    * @see TreeSet#iterator()
    */
   @Override
   public Iterator < Gradient.Key > iterator () {

      return this.keys.iterator();
   }

   /**
    * Returns the number of keys in this gradient.
    *
    * @return the key count
    * @see TreeSet#size()
    */
   public int keyCount () {

      return this.keys.size();
   }

   /**
    * Retrieves the last key in this gradient.
    *
    * @return the last key
    */
   public Key last () {

      return this.keys.last();
   }

   /**
    * Returns the greatest key in this gradient less the given
    * step. If there is no key, returns the first key instead
    * of null.
    *
    * @param step
    *           the step
    * @return the key
    * @see TreeSet#lower(Object)
    * @see TreeSet#first()
    */
   public Key lower ( final float step ) {

      this.query.step = step;
      final Key result = this.keys.lower(this.query);
      return result == null ? this.keys.first() : result;
   }

   /**
    * Resets this gradient to an initial state, with two color
    * keys, clear black at 0.0 and opaque white at 1.0 .
    *
    * @return this gradient
    * @see Color#clearBlack(Color)
    * @see Color#white(Color)
    * @see TreeSet#clear()
    * @see TreeSet#add(Object)
    */
   public Gradient reset () {

      this.keys.clear();
      this.keys.add(new Key(0.0f, 0.0f, 0.0f, 0.0f, 0.0f));
      this.keys.add(new Key(1.0f, 1.0f, 1.0f, 1.0f, 1.0f));

      return this;
   }

   /**
    * Reverses the gradient. Does so with a temporary
    * ArrayList.
    *
    * @param keyArr
    *           a temp list
    * @return the gradient
    * @see List#clear()
    * @see List#addAll(java.util.Collection)
    * @see TreeSet#addAll(java.util.Collection)
    * @see TreeSet#clear()
    * @see Collections#reverse(java.util.List)
    */
   public Gradient reverse ( final List < Key > keyArr ) {

      keyArr.clear();
      keyArr.addAll(this.keys);

      this.keys.clear();

      Collections.reverse(keyArr);
      final Iterator < Key > itr = keyArr.iterator();
      while (itr.hasNext()) {
         final Key key = itr.next();
         key.step = 1.0f - key.step;
      }

      this.keys.addAll(keyArr);
      return this;
   }

   /**
    * Returns an array of keys.
    *
    * @param target
    *           the target array
    * @return the array
    */
   public Key[] toArray ( final Key[] target ) {

      return this.keys.toArray(target);
   }

   /**
    * Returns a string representation of this gradient.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   /**
    * Returns a string representation of this gradient.
    *
    * @param places
    *           number of decimal places
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder()
            .append("{ keys: [ \n");
      final Iterator < Key > itr = this.keys.iterator();
      while (itr.hasNext()) {
         sb.append(itr.next().toString(places));
         if (itr.hasNext()) {
            sb.append(',').append('\n');
         }
      }
      sb.append(" ] }");
      return sb.toString();
   }
}
