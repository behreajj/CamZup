package camzup.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * A mutable, extensible class that contains a list of keys
 * which hold colors at steps in the range [0.0, 1.0].
 * Allows smooth color transitions to be evaluated for a
 * factor.
 */
public class Gradient implements Iterable < Gradient.Key >, Serializable {

   /**
    * A color key which stores a color at a given step (or
    * percent). Key equality and hash is based solely on the
    * step, not on the color it holds.
    *
    * When mutating a key directly, particularly its step, be
    * sure to sort the list of keys in the containing gradient.
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
      static boolean approx (
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

         this.set(0.0f, Color.clearBlack(new Color()));
      }

      /**
       * Creates a key at a given step. All values of the color
       * (including alpha) are set to the step.
       *
       * @param step
       *           the step
       */
      public Key ( final float step ) {

         this.set(step, new Color(step, step, step, step));
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
       * Sets this key with a step and color.
       *
       * @param step
       *           the step
       * @param color
       *           the color
       * @return this key
       */
      @Chainable
      Key set ( final float step, final Color color ) {

         this.step = Utils.clamp01(step);
         this.clr.set(color);
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
      Key set ( final float step, final int color ) {

         this.step = Utils.clamp01(step);
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
      Key set ( final float step, final String color ) {

         this.step = Utils.clamp01(step);
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
      Key set ( final Key source ) {

         return this.set(source.step, source.clr);
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

         final int prime = 31;
         int result = 1;
         result = prime * result + Float.floatToIntBits(this.step);
         return result;
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
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = 5072459252926038883L;

   /**
    * The tolerance used when determining whether the gradient
    * contains duplicate keys.
    */
   public static float TOLERANCE = 0.009f;

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

      final ArrayList < Key > keys = target.keys;
      keys.clear();
      keys.ensureCapacity(16);

      keys.add(new Key(0.0f,
            new Color(0.988235f, 1.0f, 0.698039f, 1.0f)));
      keys.add(new Key(0.06666667f,
            new Color(0.987190f, 0.843137f, 0.562092f, 1.0f)));
      keys.add(new Key(0.13333333f,
            new Color(0.984314f, 0.694118f, 0.446275f, 1.0f)));
      keys.add(new Key(0.2f,
            new Color(0.981176f, 0.548235f, 0.354510f, 1.0f)));

      keys.add(new Key(0.26666667f,
            new Color(0.962353f, 0.412549f, 0.301176f, 1.0f)));
      keys.add(new Key(0.33333333f,
            new Color(0.912418f, 0.286275f, 0.298039f, 1.0f)));
      keys.add(new Key(0.4f,
            new Color(0.824314f, 0.198431f, 0.334902f, 1.0f)));
      keys.add(new Key(0.46666667f,
            new Color(0.703268f, 0.142484f, 0.383007f, 1.0f)));

      keys.add(new Key(0.53333333f,
            new Color(0.584052f, 0.110588f, 0.413856f, 1.0f)));
      keys.add(new Key(0.6f,
            new Color(0.471373f, 0.080784f, 0.430588f, 1.0f)));
      keys.add(new Key(0.66666667f,
            new Color(0.367320f, 0.045752f, 0.432680f, 1.0f)));
      keys.add(new Key(0.73333333f,
            new Color(0.267974f, 0.002353f, 0.416732f, 1.0f)));

      keys.add(new Key(0.8f,
            new Color(0.174118f, 0.006275f, 0.357647f, 1.0f)));
      keys.add(new Key(0.86666667f,
            new Color(0.093856f, 0.036863f, 0.232941f, 1.0f)));
      keys.add(new Key(0.93333333f,
            new Color(0.040784f, 0.028758f, 0.110327f, 1.0f)));
      keys.add(new Key(1.0f,
            new Color(0.0f, 0.0f, 0.019608f, 1.0f)));

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
    * @see Color#red(Color)
    * @see Color#yellow(Color)
    * @see Color#green(Color)
    * @see Color#cyan(Color)
    * @see Color#blue(Color)
    * @see Color#magenta(Color)
    */
   public static Gradient paletteRgb ( final Gradient target ) {

      final ArrayList < Key > keys = target.keys;
      keys.clear();
      keys.ensureCapacity(7);

      keys.add(new Key(0.0f, Color.red(new Color())));
      keys.add(new Key(0.16666667f, Color.yellow(new Color())));
      keys.add(new Key(0.33333333f, Color.green(new Color())));
      keys.add(new Key(0.5f, Color.cyan(new Color())));
      keys.add(new Key(0.66666667f, Color.blue(new Color())));
      keys.add(new Key(0.83333333f, Color.magenta(new Color())));
      keys.add(new Key(1.0f, Color.red(new Color())));

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

      final ArrayList < Key > keys = target.keys;
      keys.clear();
      keys.ensureCapacity(16);

      keys.add(new Key(0.0f,
            new Color(0.266667f, 0.003922f, 0.329412f, 1.0f)));
      keys.add(new Key(0.06666667f,
            new Color(0.282353f, 0.100131f, 0.420654f, 1.0f)));
      keys.add(new Key(0.13333333f,
            new Color(0.276078f, 0.184575f, 0.487582f, 1.0f)));
      keys.add(new Key(0.2f,
            new Color(0.254902f, 0.265882f, 0.527843f, 1.0f)));

      keys.add(new Key(0.26666667f,
            new Color(0.221961f, 0.340654f, 0.549281f, 1.0f)));
      keys.add(new Key(0.33333333f,
            new Color(0.192157f, 0.405229f, 0.554248f, 1.0f)));
      keys.add(new Key(0.4f,
            new Color(0.164706f, 0.469804f, 0.556863f, 1.0f)));
      keys.add(new Key(0.46666667f,
            new Color(0.139869f, 0.534379f, 0.553464f, 1.0f)));

      keys.add(new Key(0.53333333f,
            new Color(0.122092f, 0.595033f, 0.543007f, 1.0f)));
      keys.add(new Key(0.6f,
            new Color(0.139608f, 0.658039f, 0.516863f, 1.0f)));
      keys.add(new Key(0.66666667f,
            new Color(0.210458f, 0.717647f, 0.471895f, 1.0f)));
      keys.add(new Key(0.73333333f,
            new Color(0.326797f, 0.773595f, 0.407582f, 1.0f)));

      keys.add(new Key(0.8f,
            new Color(0.477647f, 0.821961f, 0.316863f, 1.0f)));
      keys.add(new Key(0.86666667f,
            new Color(0.648366f, 0.858039f, 0.208889f, 1.0f)));
      keys.add(new Key(0.93333333f,
            new Color(0.825098f, 0.884967f, 0.114771f, 1.0f)));
      keys.add(new Key(1.0f,
            new Color(0.992157f, 0.905882f, 0.145098f, 1.0f)));

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
    * The list of color keys. Initialized with a capacity of 8.
    */
   public final ArrayList < Key > keys = new ArrayList <>(8);

   /**
    * Creates a gradient with two default color keys, clear
    * black at 0.0 and opaque white at 1.0.
    *
    * @see Color#clearBlack(Color)
    * @see Color#white(Color)
    */
   public Gradient () {

      this.keys.add(new Key(0.0f, Color.clearBlack(new Color())));
      this.keys.add(new Key(1.0f, Color.white(new Color())));
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

      this.keys.add(new Key(0.0f, Color.white(new Color())));
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

      final int len = colors.length;
      this.keys.ensureCapacity(len);
      final float denom = 1.0f / (len - 1.0f);
      for (int i = 0; i < len; ++i) {
         this.keys.add(new Key(i * denom, colors[i]));
      }
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

      this.keys.add(new Key(0.0f, Color.white(new Color())));
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

      final int len = colors.length;
      this.keys.ensureCapacity(len);
      final float denom = 1.0f / (len - 1.0f);
      for (int i = 0; i < len; ++i) {
         this.keys.add(new Key(i * denom, colors[i]));
      }
   }

   /**
    * Creates a gradient from color keys.
    *
    * @param keys
    *           the color keys
    * @see Gradient#removeDuplicates()
    */
   public Gradient ( final Key... keys ) {

      final int len = keys.length;
      this.keys.ensureCapacity(len);
      for (int i = 0; i < len; ++i) {
         this.keys.add(keys[i]);
      }
      this.removeDuplicates();
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

      this.keys.add(new Key(0.0f, Color.white(new Color())));
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

      final int len = colors.length;
      this.keys.ensureCapacity(len);
      final float denom = 1.0f / (len - 1.0f);
      for (int i = 0; i < len; ++i) {
         this.keys.add(new Key(i * denom, colors[i]));
      }
   }

   /**
    * Tests to see if this gradient equals another.
    *
    * @param other
    *           the other gradient
    * @return the evaluation
    */
   protected boolean equals ( final Gradient gradient ) {

      if (this.keys == null) {
         if (gradient.keys != null) {
            return false;
         }
      } else if (!this.keys.equals(gradient.keys)) {
         return false;
      }
      return true;
   }

   /**
    * Distributes this gradient's color key's evenly through
    * the range [0.0, 1.0] .
    *
    * @return this gradient
    */
   @Chainable
   public Gradient distributeKeys () {

      final int len = this.keys.size();
      if (len < 2) {
         return this;
      }

      final float denom = 1.0f / (len - 1.0f);
      for (int i = 0; i < len; ++i) {
         this.keys.get(i).step = i * denom;
      }

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
    * Evaluates a color from the provided step. The step's
    * range is expected to be [0.0, 1.0].
    *
    * @param step
    *           the step
    * @param target
    *           the output color
    * @return the color
    */
   public Color eval (
         final float step,
         final Color target ) {

      return this.eval(step, Gradient.EASING, target);
   }

   /**
    * Evaluates a color from the provided step. The step's
    * range is expected to be [0.0, 1.0].
    *
    * @param step
    *           the step
    * @param easing
    *           the easing function
    * @param target
    *           the output color
    * @return the color
    * @see Utils#div(float, float)
    * @see Color#white(Color)
    */
   public Color eval (
         final float step,
         final Color.AbstrEasing easing,
         final Color target ) {

      final int len = this.keys.size();
      if (len == 0) {
         return Color.white(target);
      } else if (len == 1 || step <= 0.0f) {
         return target.set(this.keys.get(0).clr);
      } else if (step >= 1.0f) {
         return target.set(this.keys.get(len - 1).clr);
      }

      for (int i = 0; i < len; ++i) {
         final Key curr = this.keys.get(i);
         final float currStep = curr.step;

         if (step < currStep) {
            final Key prev = this.keys.get(i - 1 < 0 ? 0 : i - 1);
            final float sclstp = Utils.div(
                  step - currStep,
                  prev.step - currStep);
            return easing.apply(curr.clr, prev.clr, sclstp, target);
         }
      }

      return target;
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
         result[i] = this.eval(i * toPercent, easing, new Color());
      }
      return result;
   }

   /**
    * Gets a color from the gradient by index.
    *
    * @param i
    *           the index
    * @param target
    *           the output color
    * @return the color
    */
   public Color get ( final int i, final Color target ) {

      return target.set(this.keys.get(i).clr);
   }

   /**
    * Sets the color of a key at a provided index.
    * 
    * @param i
    *           the index
    * @param source
    *           the source
    * @return this gradient
    */
   @Chainable
   public Gradient set ( final int i, final Color source ) {

      this.keys.get(i).clr.set(source);
      return this;
   }

   /**
    * Sets the color of a key at a provided index.
    * 
    * @param i
    *           the index
    * @param source
    *           the source
    * @return this gradient
    */
   @Chainable
   public Gradient set ( final int i, final int source ) {

      Color.fromHex(source, this.keys.get(i).clr);
      return this;
   }

   /**
    * Sets the color of a key at a provided index.
    * 
    * @param i
    *           the index
    * @param source
    *           the source
    * @return this gradient
    */
   @Chainable
   public Gradient set ( final int i, final String source ) {

      Color.fromHex(source, this.keys.get(i).clr);
      return this;
   }

   /**
    * Returns a hash code for this gradient based on its list
    * of keys.
    *
    * @return the hash code
    */
   @Override
   public int hashCode () {

      final int prime = 31;
      int result = 1;
      result = prime * result
            + (this.keys == null ? 0 : this.keys.hashCode());
      return result;
   }

   /**
    * Returns an iterator for this gradient, which allows its
    * components to be accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public Iterator < Key > iterator () {

      return this.keys.iterator();
   }

   /**
    * Returns the number of keys in this gradient.
    *
    * @return the key count
    */
   public int keyCount () {

      return this.keys.size();
   }

   /**
    * Sorts this gradient's keys, then checks the list for
    * duplicates and removes them if found.
    *
    * @return this gradient
    */
   @Chainable
   public Gradient removeDuplicates () {

      return this.removeDuplicates(Gradient.TOLERANCE);
   }

   /**
    * Sorts this gradient's keys, then checks the list for
    * duplicates and removes them if found.
    *
    * @param tolerance
    *           the tolerance
    * @return this gradient
    * @see Collections#sort(java.util.List)
    * @see Key#approx(Key, Key, float)
    */
   @Chainable
   public Gradient removeDuplicates ( final float tolerance ) {

      /*
       * LinkedHashSet<Key> set = new LinkedHashSet<>();
       * set.addAll(keys); keys.clear(); keys.addAll(set); return
       * this;
       */

      Collections.sort(this.keys);
      final int len = this.keys.size();
      for (int i = len - 1; i > 0; --i) {
         final Key current = this.keys.get(i);
         final Key prev = this.keys.get(i - 1);
         if (Key.approx(prev, current, tolerance)) {
            this.keys.remove(current);
         }
      }

      return this;
   }

   /**
    * Resets this gradient to an initial state, with two color
    * keys, clear black at 0.0 and opaque white at 1.0 .
    *
    * @return this gradient
    * @see Color#clearBlack(Color)
    * @see Color#white(Color)
    */
   @Chainable
   public Gradient reset () {

      this.keys.clear();
      this.keys.add(new Key(0.0f, Color.clearBlack(new Color())));
      this.keys.add(new Key(1.0f, Color.white(new Color())));

      return this;
   }

   /**
    * Reverses this gradient's order. Each key's step is
    * subtracted from 1.0 .
    *
    * @return this gradient
    */
   @Chainable
   public Gradient reverse () {

      Collections.reverse(this.keys);
      final Iterator < Key > itr = this.keys.iterator();
      while (itr.hasNext()) {
         final Key key = itr.next();
         key.step = Utils.max(1.0f - key.step, 0.0f);
      }

      return this;
   }

   /**
    * Sorts this gradient's keys by step, or percent.
    *
    * @return this gradient.
    * @see Collections#sort(java.util.List)
    */
   @Chainable
   public Gradient sort () {

      Collections.sort(this.keys);
      return this;
   }

   /**
    * Returns a string representation of this color.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   /**
    * Returns a string representation of this color.
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
