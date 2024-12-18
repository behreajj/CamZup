package camzup.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * A mutable, extensible class that contains a list of keys which hold
 * colors at steps in the range [0.0, 1.0] . Allows smooth color
 * transitions to be evaluated by a factor.
 */
public class Gradient implements IUtils, Iterable < ColorKey > {

   /**
    * The set of keys. Quantized sorting closures shouldn't be used as a
    * comparator supplied to this TreeSet's constructor, as that leads to bugs
    * when an unknown number of keys are supplied to the gradient.
    */
   public final TreeSet < ColorKey > keys = new TreeSet <>();

   /**
    * A temporary variable to hold queries in evaluation functions.
    */
   protected final ColorKey query = new ColorKey();

   /**
    * Creates a gradient with two default color keys, clear black at 0.0 and
    * opaque white at 1.0.
    *
    * @see Rgb#clearBlack(Rgb)
    * @see Rgb#white(Rgb)
    */
   public Gradient ( ) {

      this.keys.add(new ColorKey(0.0f, 0.0f, 0.0f, 0.0f, 0.0f));
      this.keys.add(new ColorKey(1.0f, 1.0f, 1.0f, 1.0f, 1.0f));
   }

   /**
    * Creates a gradient from a collection of color integers; the resultant
    * keys are evenly distributed over the range [0.0, 1.0].
    *
    * @param colors the colors
    */
   public Gradient ( final Collection < Rgb > colors ) {

      this.appendAll(colors);
   }

   /**
    * Creates a gradient from color keys.
    *
    * @param keys the color keys
    */
   public Gradient ( final ColorKey... keys ) { this.insertAll(keys); }

   /**
    * Creates a gradient from a list of scalars; the resultant keys are evenly
    * distributed over the range [0.0, 1.0].
    *
    * @param scalars the scalars
    */
   public Gradient ( final float... scalars ) { this.appendAll(scalars); }

   /**
    * Creates a gradient from a scalar. The scalar is placed between clear
    * black at key 0.0 and opaque white at key 1.0. The boundary keys adopt
    * the color's alpha.
    *
    * @param scalar the scalar
    */
   public Gradient ( final float scalar ) {

      /* Insert scalar key first to ensure it is added to keys. */
      final float a = Utils.clamp01(scalar);
      this.keys.add(new ColorKey(a, scalar, scalar, scalar, scalar));
      this.keys.add(new ColorKey(0.0f, 0.0f, 0.0f, 0.0f, 0.0f));
      this.keys.add(new ColorKey(1.0f, 1.0f, 1.0f, 1.0f, 1.0f));
   }

   /**
    * Constructs a copy of a source gradient.
    *
    * @param source the source
    */
   public Gradient ( final Gradient source ) { this.set(source); }

   /**
    * Creates a gradient from a color. The color is placed according to its
    * perceived luminance between black at key 0.0 and white at key 1.0. The
    * boundary keys adopt the color's alpha.
    *
    * @param c the color
    */
   public Gradient ( final int c ) {

      this(Rgb.fromHex(c, new Rgb()));
   }

   /**
    * Creates a gradient from a list of color integers; the resultant keys are
    * evenly distributed over the range [0.0, 1.0] .
    *
    * @param colors the colors
    */
   public Gradient ( final int... colors ) { this.appendAll(colors); }

   /**
    * Creates a gradient from a color. The color is placed according to its
    * perceived luminance between black at key 0.0 and white at key 1.0. The
    * boundary keys adopt the color's alpha.
    *
    * @param c the color
    *
    * @see Rgb#sRgbLuminance(Rgb)
    * @see Utils#lerp(float, float, float)
    */
   public Gradient ( final Rgb c ) {

      final float lum = Rgb.sRgbLuminance(c);
      final float vf = lum <= 0.0031308f ? lum * 12.92f : ( float ) ( Math.pow(
         lum, 0.4166666666666667d) * 1.055d - 0.055d );
      final float step = Utils.lerp(IUtils.ONE_THIRD, IUtils.TWO_THIRDS, vf);

      this.keys.add(new ColorKey(0.0f, 0.0f, 0.0f, 0.0f, c.alpha));
      this.keys.add(new ColorKey(step, c));
      this.keys.add(new ColorKey(1.0f, 1.0f, 1.0f, 1.0f, c.alpha));
   }

   /**
    * Creates a gradient from a list of colors; the resultant keys are evenly
    * distributed over the range [0.0, 1.0].
    *
    * @param colors the colors
    */
   public Gradient ( final Rgb... colors ) { this.appendAll(colors); }

   /**
    * Appends a scalar at step 1.0 . Compresses existing keys to the left.
    *
    * @param scalar the scalar
    *
    * @return this gradient
    *
    * @see Gradient#compressKeysLeft(int)
    * @see TreeSet#add(Object)
    */
   public Gradient append ( final float scalar ) {

      this.compressKeysLeft(1);
      this.keys.add(new ColorKey(1.0f, scalar, scalar, scalar, scalar));

      return this;
   }

   /**
    * Appends a color at step 1.0 . Compresses existing keys to the left.
    *
    * @param c the color
    *
    * @return this gradient
    *
    * @see Gradient#compressKeysLeft(int)
    * @see TreeSet#add(Object)
    */
   public Gradient append ( final int c ) {

      this.compressKeysLeft(1);
      this.keys.add(new ColorKey(1.0f, c));

      return this;
   }

   /**
    * Appends a color at step 1.0 . Compresses existing keys to the left.
    *
    * @param c the color
    *
    * @return this gradient
    *
    * @see Gradient#compressKeysLeft(int)
    * @see TreeSet#add(Object)
    */
   public Gradient append ( final Rgb c ) {

      // TODO: Retest this, it doesn't seem to be working properly...
      this.compressKeysLeft(1);
      this.keys.add(new ColorKey(1.0f, c));

      return this;
   }

   /**
    * Appends a collection of colors to this gradient. Shifts existing keys to
    * the left.
    *
    * @param colors the colors
    *
    * @return this gradient
    *
    * @see Gradient#compressKeysLeft(int)
    * @see TreeSet#size()
    * @see TreeSet#add(Object)
    */
   public Gradient appendAll ( final Collection < Rgb > colors ) {

      final int len = colors.size();
      this.compressKeysLeft(len);
      final float oldLen = this.keys.size();
      final float denom = 1.0f / ( oldLen + len - 1.0f );

      final Iterator < Rgb > clrItr = colors.iterator();
      for ( int i = 0; clrItr.hasNext(); ++i ) {
         this.keys.add(new ColorKey( ( oldLen + i ) * denom, clrItr.next()));
      }

      return this;
   }

   /**
    * Appends a list of scalars to this gradient. Shifts existing keys to the
    * left.
    *
    * @param scalars the scalars
    *
    * @return the gradient
    *
    * @see Gradient#compressKeysLeft(int)
    * @see TreeSet#size()
    * @see TreeSet#add(Object)
    */
   public Gradient appendAll ( final float... scalars ) {

      final int len = scalars.length;
      this.compressKeysLeft(len);
      final int oldLen = this.keys.size();
      final float denom = 1.0f / ( oldLen + len - 1.0f );

      for ( int i = 0; i < len; ++i ) {
         final float scalar = scalars[i];
         this.keys.add(new ColorKey( ( oldLen + i ) * denom, scalar, scalar,
            scalar, scalar));
      }

      return this;
   }

   /**
    * Appends a list of color integers to this gradient. Shifts existing keys
    * to the left.
    *
    * @param colors the colors
    *
    * @return this gradient
    *
    * @see Gradient#compressKeysLeft(int)
    * @see TreeSet#size()
    * @see TreeSet#add(Object)
    */
   public Gradient appendAll ( final int... colors ) {

      final int len = colors.length;
      this.compressKeysLeft(len);
      final int oldLen = this.keys.size();
      final float denom = 1.0f / ( oldLen + len - 1.0f );

      for ( int i = 0; i < len; ++i ) {
         this.keys.add(new ColorKey( ( oldLen + i ) * denom, colors[i]));
      }

      return this;
   }

   /**
    * Appends a list of colors to this gradient. Shifts existing keys to the
    * left.
    *
    * @param colors the colors
    *
    * @return this gradient
    *
    * @see Gradient#compressKeysLeft(int)
    * @see TreeSet#size()
    * @see TreeSet#add(Object)
    */
   public Gradient appendAll ( final Rgb... colors ) {

      final int len = colors.length;
      this.compressKeysLeft(len);
      final int oldLen = this.keys.size();
      final float denom = 1.0f / ( oldLen + len - 1.0f );

      for ( int i = 0; i < len; ++i ) {
         this.keys.add(new ColorKey( ( oldLen + i ) * denom, colors[i]));
      }

      return this;
   }

   /**
    * Checks to see if this gradient contains a specified key.
    *
    * @param key the key
    *
    * @return the evaluation
    */
   public boolean contains ( final ColorKey key ) {

      return this.keys.contains(key);
   }

   /**
    * Cycles the steps of a color gradient. The number of places can be
    * positive or negative, indicating which direction to shift: positive
    * numbers shift to the right; negative, to the left.
    *
    * @param places the number of places
    *
    * @return this gradient
    */
   public Gradient cycle ( final int places ) {

      /* Load from tree set into an array. */
      final int len = this.keys.size();
      final Iterator < ColorKey > itr = this.keys.iterator();
      final ColorKey[] arr = new ColorKey[len];
      for ( int i = 0; itr.hasNext(); ++i ) { arr[i] = itr.next(); }

      /* Cycle the array with three reverses. */
      final int k = Utils.mod(-places, len);
      Gradient.reverse(arr, 0, len - 1);
      Gradient.reverse(arr, 0, k - 1);
      Gradient.reverse(arr, k, len - 1);

      /* Reintroduce shifted keys to tree set. */
      this.keys.clear();
      for ( int m = 0; m < len; ++m ) { this.keys.add(arr[m]); }

      return this;
   }

   /**
    * Distributes this gradient's color keys evenly through the range [0.0,
    * 1.0] .
    *
    * @return this gradient
    *
    * @see List#addAll(java.util.Collection)
    */
   public Gradient distribute ( ) {

      final ArrayList < ColorKey > keyArr = new ArrayList <>(this.keys.size());
      keyArr.addAll(this.keys);
      this.keys.clear();
      final float denom = 1.0f / ( keyArr.size() - 1.0f );
      final Iterator < ColorKey > itr = keyArr.iterator();
      for ( float incr = 0.0f; itr.hasNext(); ++incr ) {
         itr.next().step = incr * denom;
      }
      this.keys.addAll(keyArr);

      return this;
   }

   /**
    * Tests to see if this gradient equals an object.
    *
    * @param obj the object
    *
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Gradient ) obj);
   }

   /**
    * Returns the least key in this gradient greater than or equal to the
    * given step. If there is no key, returns the last key instead of null.
    *
    * @param step the step
    *
    * @return the key
    *
    * @see TreeSet#ceiling(Object)
    * @see TreeSet#last()
    */
   public ColorKey findGe ( final float step ) {

      this.query.step = step;
      final ColorKey result = this.keys.ceiling(this.query);
      return result == null ? this.keys.last() : result;
   }

   /**
    * Returns the greatest key in this gradient less than or equal to the
    * given step. If there is no key, returns the first key instead of null.
    *
    * @param step the step
    *
    * @return the key
    *
    * @see TreeSet#floor(Object)
    * @see TreeSet#first()
    */
   public ColorKey findLe ( final float step ) {

      this.query.step = step;
      final ColorKey result = this.keys.floor(this.query);
      return result == null ? this.keys.first() : result;
   }

   /**
    * Retrieves the first key in this gradient.
    *
    * @return the first key
    */
   public ColorKey getFirst ( ) { return this.keys.first(); }

   /**
    * Retrieves the last key in this gradient.
    *
    * @return the last key
    */
   public ColorKey getLast ( ) { return this.keys.last(); }

   /**
    * Returns a hash code for this gradient based on its list of keys.
    *
    * @return the hash code
    */
   @Override
   public int hashCode ( ) { return this.keys.hashCode(); }

   /**
    * Inserts a color key to this gradient. If a color key exists at the
    * insertion key's step, the old key is removed.
    *
    * @param key the key
    *
    * @return this gradient
    *
    * @see TreeSet#add(Object)
    * @see TreeSet#remove(Object)
    */
   public Gradient insert ( final ColorKey key ) {

      if ( this.keys.contains(key) ) { this.keys.remove(key); }
      this.keys.add(key);

      return this;
   }

   /**
    * Inserts color keys to this gradient. If a color key exists at the
    * insertion's step, the old key is removed.
    *
    * @param insertion the keys
    *
    * @return this gradient
    *
    * @see TreeSet#add(Object)
    * @see TreeSet#remove(Object)
    */
   public Gradient insertAll ( final ColorKey... insertion ) {

      final int len = insertion.length;
      for ( int i = 0; i < len; ++i ) {
         final ColorKey key = insertion[i];
         if ( this.keys.contains(key) ) { this.keys.remove(key); }
         this.keys.add(key);
      }

      return this;
   }

   /**
    * Returns an iterator for this gradient, which allows its components to be
    * accessed in an enhanced for-loop.
    *
    * @return the iterator
    *
    * @see TreeSet#iterator()
    */
   @Override
   public Iterator < ColorKey > iterator ( ) { return this.keys.iterator(); }

   /**
    * Returns the number of keys in this gradient.
    *
    * @return the key count
    *
    * @see TreeSet#size()
    */
   public int length ( ) { return this.keys.size(); }

   /**
    * Prepends a scalar at step 0.0 . Compresses existing keys to the right.
    *
    * @param scalar the scalar
    *
    * @return this gradient
    *
    * @see Gradient#compressKeysRight(int)
    * @see TreeSet#add(Object)
    */
   public Gradient prepend ( final float scalar ) {

      this.compressKeysRight(1);
      this.keys.add(new ColorKey(0.0f, scalar, scalar, scalar, scalar));

      return this;
   }

   /**
    * Prepends a color at step 0.0 . Compresses existing keys to the right.
    *
    * @param c the color
    *
    * @return this gradient
    *
    * @see Gradient#compressKeysRight(int)
    * @see TreeSet#add(Object)
    */
   public Gradient prepend ( final int c ) {

      this.compressKeysRight(1);
      this.keys.add(new ColorKey(0.0f, c));

      return this;
   }

   /**
    * Prepends a color at step 0.0 . Compresses existing keys to the right.
    *
    * @param c the color
    *
    * @return this gradient
    *
    * @see Gradient#compressKeysRight(int)
    * @see TreeSet#add(Object)
    */
   public Gradient prepend ( final Rgb c ) {

      this.compressKeysRight(1);
      this.keys.add(new ColorKey(0.0f, c));

      return this;
   }

   /**
    * Prepends a collection of colors to this gradient. Compresses existing
    * keys to the right.
    *
    * @param colors the colors
    *
    * @return this gradient
    *
    * @see Gradient#compressKeysRight(int)
    * @see TreeSet#size()
    * @see TreeSet#add(Object)
    */
   public Gradient prependAll ( final Collection < Rgb > colors ) {

      final int len = colors.size();
      this.compressKeysRight(len);
      final float denom = 1.0f / ( this.keys.size() + len - 1.0f );

      final Iterator < Rgb > clrItr = colors.iterator();
      for ( int i = 0; clrItr.hasNext(); ++i ) {
         this.keys.add(new ColorKey(i * denom, clrItr.next()));
      }

      return this;
   }

   /**
    * Prepends a list of scalars to this gradient. Shifts existing keys to the
    * right.
    *
    * @param scalars the scalars
    *
    * @return this gradient
    *
    * @see Gradient#compressKeysRight(int)
    * @see TreeSet#size()
    * @see TreeSet#add(Object)
    */
   public Gradient prependAll ( final float... scalars ) {

      final int len = scalars.length;
      this.compressKeysRight(len);
      final float denom = 1.0f / ( this.keys.size() + len - 1.0f );

      for ( int i = 0; i < len; ++i ) {
         final float scalar = scalars[i];
         this.keys.add(new ColorKey(i * denom, scalar, scalar, scalar, scalar));
      }

      return this;
   }

   /**
    * Prepends a list of color integers to this gradient. Compresses existing
    * keys to the right.
    *
    * @param colors the colors
    *
    * @return this gradient
    *
    * @see Gradient#compressKeysRight(int)
    * @see TreeSet#size()
    * @see TreeSet#add(Object)
    */
   public Gradient prependAll ( final int... colors ) {

      final int len = colors.length;
      this.compressKeysRight(len);
      final float denom = 1.0f / ( this.keys.size() + len - 1.0f );

      for ( int i = 0; i < len; ++i ) {
         this.keys.add(new ColorKey(i * denom, colors[i]));
      }

      return this;
   }

   /**
    * Prepends a list of colors to this gradient. Shifts existing keys to the
    * right.
    *
    * @param colors the colors
    *
    * @return this gradient
    *
    * @see Gradient#compressKeysRight(int)
    * @see TreeSet#size()
    * @see TreeSet#add(Object)
    */
   public Gradient prependAll ( final Rgb... colors ) {

      final int len = colors.length;
      this.compressKeysRight(len);
      final float denom = 1.0f / ( this.keys.size() + len - 1.0f );

      for ( int i = 0; i < len; ++i ) {
         this.keys.add(new ColorKey(i * denom, colors[i]));
      }

      return this;
   }

   /**
    * Finds this gradient's range. Subtracts the step of its first key from
    * the step of its last.
    *
    * @return the range
    */
   public float range ( ) {

      return this.keys.last().step - this.keys.first().step;
   }

   /**
    * Removes a key if the gradient has more than 2 keys. Returns true if
    * successful.
    *
    * @param key the key
    *
    * @return the success
    *
    * @see TreeSet#remove(Object)
    */
   public boolean remove ( final ColorKey key ) {

      if ( this.keys.size() > 2 ) { return this.keys.remove(key); }
      return false;
   }

   /**
    * Removes the first key if the gradient has more than 2 keys. Returns true
    * if successful.
    *
    * @param target the output color key
    *
    * @return the evaluation
    *
    * @see TreeSet#pollFirst()
    */
   public boolean removeFirst ( final ColorKey target ) {

      if ( this.keys.size() > 2 ) {
         target.set(this.keys.pollFirst());
         return true;
      }
      return false;
   }

   /**
    * Removes the last key if the gradient has more than 2 keys. Returns true
    * if successful.
    *
    * @param target the output color key
    *
    * @return the evaluation
    *
    * @see TreeSet#pollLast()
    */
   public boolean removeLast ( final ColorKey target ) {

      if ( this.keys.size() > 2 ) {
         target.set(this.keys.pollLast());
         return true;
      }
      return false;
   }

   /**
    * Resets this gradient to an initial state, with two color keys: clear
    * black at 0.0 and opaque white at 1.0 .
    *
    * @return this gradient
    *
    * @see Rgb#clearBlack(Rgb)
    * @see Rgb#white(Rgb)
    * @see TreeSet#clear()
    * @see TreeSet#add(Object)
    */
   public Gradient reset ( ) {

      this.keys.clear();
      this.keys.add(new ColorKey(0.0f, 0.0f, 0.0f, 0.0f, 0.0f));
      this.keys.add(new ColorKey(1.0f, 1.0f, 1.0f, 1.0f, 1.0f));

      return this;
   }

   /**
    * Reverses the gradient. The step of each color key is subtracted from
    * one.
    *
    * @return the gradient
    *
    * @see List#clear()
    * @see List#addAll(java.util.Collection)
    * @see TreeSet#addAll(java.util.Collection)
    * @see TreeSet#clear()
    * @see Collections#reverse(java.util.List)
    */
   public Gradient reverse ( ) {

      final int len = this.keys.size();
      final Iterator < ColorKey > itr = this.keys.iterator();
      final ColorKey[] arr = new ColorKey[len];
      for ( int i = 0; itr.hasNext(); ++i ) { arr[i] = itr.next(); }

      Gradient.reverse(arr, 0, len - 1);

      this.keys.clear();
      for ( int i = 0; i < len; ++i ) { this.keys.add(arr[i]); }

      return this;
   }

   /**
    * Copies data from a source gradient.
    *
    * @param source the source
    *
    * @return this gradient
    */
   public Gradient set ( final Gradient source ) {

      this.keys.clear();
      final Iterator < ColorKey > srcItr = source.keys.iterator();
      while ( srcItr.hasNext() ) { this.keys.add(new ColorKey(srcItr.next())); }

      return this;
   }

   /**
    * Sorts the gradient according to a property of the colors in each key.
    * Does so with a temporary List.
    *
    * @return the gradient
    */
   public Gradient sort ( ) { return this.sort(null); }

   /**
    * Sorts the gradient according to a property of the colors in each key.
    * Does so with a temporary List.
    *
    * @param sorter the sorting function
    *
    * @return the gradient
    */
   public Gradient sort ( final Comparator < Rgb > sorter ) {

      /*
       * Separate color keys into an array of steps and of colors. The key steps
       * should remain unaffected by the sort.
       */
      final int len = this.keys.size();
      final float[] steps = new float[len];
      final ArrayList < Rgb > clrList = new ArrayList <>(len);

      final Iterator < ColorKey > keyItr = this.keys.iterator();
      for ( int i = 0; keyItr.hasNext(); ++i ) {
         final ColorKey key = keyItr.next();
         steps[i] = key.step;
         clrList.add(key.clr);
      }

      /* Clear out the gradient's key set now that separation is complete. */
      this.keys.clear();

      /* Sort the colors. */
      Collections.sort(clrList, sorter);

      /* Reconstitute the keys from the steps and colors. */
      final Iterator < Rgb > clrItr = clrList.iterator();
      for ( int i = 0; clrItr.hasNext(); ++i ) {
         this.keys.add(new ColorKey(steps[i], clrItr.next()));
      }

      return this;
   }

   /**
    * Returns an array containing the colors in the gradient's color keys.
    * Unlike {@link Gradient#evalRange(Gradient, int)}, doesn't return equally
    * distributed colors derived from an easing function.
    *
    * @return the array
    */
   public Rgb[] toArray ( ) {

      final Rgb[] result = new Rgb[this.keys.size()];
      final Iterator < ColorKey > itr = this.keys.iterator();
      for ( int i = 0; itr.hasNext(); ++i ) { result[i] = itr.next().clr; }
      return result;
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes.
    *
    * @return the string
    */
   public String toBlenderCode ( ) {

      return this.toBlenderCode(Integer.toHexString(System.identityHashCode(
         this)), this.keys.size());
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes.
    *
    * @param name the material's name
    *
    * @return the string
    */
   public String toBlenderCode ( final String name ) {

      return this.toBlenderCode(name, this.keys.size());
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes.
    *
    * @param name    the material's name
    * @param samples number of gradient samples
    *
    * @return the string
    */
   public String toBlenderCode ( final String name, final int samples ) {

      return this.toBlenderCode(name, samples, 2.2f);
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes.
    *
    * @param name    the material's name
    * @param samples number of gradient samples
    * @param gamma   the gamma adjustment
    *
    * @return the string
    */
   @Experimental
   public String toBlenderCode ( final String name, final int samples,
      final float gamma ) {

      /*
       * Blender gradients may contain a max of 32 color keys. While they may
       * contain a minimum of only 1, they default to 2 keys when created.
       */

      final Rgb[] clrs = Gradient.evalRange(this, Utils.clamp(samples, 2, 32));
      final int len = clrs.length;
      final int last = len - 1;
      final float toPercent = 1.0f / last;

      final StringBuilder pyCd = new StringBuilder(2048);
      pyCd.append("from bpy import data as D, context as C\n\n");
      pyCd.append("grd_data = [");

      for ( int i = 0; i < len; ++i ) {
         pyCd.append("\n    {\"position\": ");
         Utils.toFixed(pyCd, i * toPercent, 6);
         pyCd.append(", \"color\": ");
         clrs[i].toBlenderCode(pyCd, gamma, true);
         pyCd.append('}');

         if ( i < last ) { pyCd.append(',').append(' '); }
      }
      pyCd.append(']');

      pyCd.append("\n\nmaterial = D.materials.new(\"");
      if ( Character.isDigit(name.charAt(0)) ) { pyCd.append("id"); }
      pyCd.append(name);
      pyCd.append("\")\n");
      pyCd.append("material.use_nodes = True\n");
      pyCd.append("mat_node_tree = material.node_tree\n");
      pyCd.append("mat_nodes = mat_node_tree.nodes\n");
      pyCd.append("clr_rmp_node = mat_nodes.new(\"ShaderNodeValToRGB\")\n");
      pyCd.append("clr_rmp_data = clr_rmp_node.color_ramp\n");
      pyCd.append("color_keys = clr_rmp_data.elements\n\n");
      pyCd.append("color_keys[0].position = grd_data[0][\"position\"]\n");
      pyCd.append("color_keys[0].color = grd_data[0][\"color\"]\n\n");
      pyCd.append("color_keys[1].position = grd_data[1][\"position\"]\n");
      pyCd.append("color_keys[1].color = grd_data[1][\"color\"]\n\n");
      pyCd.append("i_itr = range(2, len(grd_data))\n");
      pyCd.append("for i in i_itr:\n");
      pyCd.append("    datum = grd_data[i]\n");
      pyCd.append("    new_key = color_keys.new(datum[\"position\"])\n");
      pyCd.append("    new_key.color = datum[\"color\"]\n");
      return pyCd.toString();
   }

   /**
    * Returns a String representation of the gradient compatible with .ggr
    * (GIMP gradient) file formats.
    *
    * @return the string
    */
   public String toGgrString ( ) {

      return this.toGgrString(this.hashIdentityString());
   }

   /**
    * Returns a String representation of the gradient compatible with .ggr
    * (GIMP gradient) file formats.
    *
    * @param name the name
    *
    * @return the string
    */
   public String toGgrString ( final String name ) {

      return this.toGgrString(name, 0, 0);
   }

   /**
    * Returns a String representation of the gradient compatible with .ggr
    * (GIMP gradient) file formats. <br>
    * <br>
    * Blend types include: (0) linear; (1) curved; (2) sine; (3) sphere
    * increasing; (4) sphere decreasing.<br>
    * <br>
    * Color types include: (0) RGB, (1) HSV CCW, (2) HSV CW.
    *
    * @param name      the name
    * @param blendType the blend type
    * @param colorType the color type
    *
    * @return the string
    */
   @Experimental
   public String toGgrString ( final String name, final int blendType,
      final int colorType ) {

      /*
       * The entire span of [0.0, 1.0] must be covered by the keys of a GIMP
       * gradient. Each key has a left edge (column 1), center point (column 2)
       * and right edge (column 3). A color is located on the left edge; its
       * red, green, blue and alpha channels are columns 4, 5, 6 and 7. Another
       * is located on the right edge; columns 8, 9, 10 and 11.
       */

      final ColorKey first = this.keys.first();
      final float firstStep = first.step;
      final Rgb firstColor = first.clr;
      final boolean nonZeroFirst = firstStep > 0.0f;

      final ColorKey last = this.keys.last();
      final float lastStep = last.step;
      final Rgb lastColor = last.clr;
      final boolean nonOneLast = lastStep < 1.0f;

      int len = this.keys.size() - 1;
      if ( nonZeroFirst ) { ++len; }
      if ( nonOneLast ) { ++len; }

      final StringBuilder sb = new StringBuilder(512);
      sb.append("GIMP Gradient\n");
      sb.append("Name: ");
      sb.append(name);
      sb.append('\n');
      sb.append(len);
      sb.append('\n');

      if ( nonZeroFirst ) {
         final String frstClrStr = firstColor.toGgrString();
         sb.append("0.000000 ");
         Utils.toFixed(sb, firstStep * 0.5f, 6);
         sb.append(' ');
         Utils.toFixed(sb, firstStep, 6);
         sb.append(' ');
         sb.append(frstClrStr);
         sb.append(' ');
         sb.append(frstClrStr);
         sb.append(' ');
         sb.append(blendType);
         sb.append(' ');
         sb.append(colorType);
         sb.append('\n');
      }

      final Iterator < ColorKey > itr = this.keys.iterator();
      ColorKey curr = itr.next();
      float prevStep = curr.step;
      String prevClrStr = curr.clr.toGgrString();

      /*
       * Fence posting problem due to differences in gradient implementation.
       * Length of GIMP gradient is one less than this because each GIMP key is
       * a segment. This implementation's key lies on the left edge of the GIMP
       * key segment.
       */
      while ( itr.hasNext() ) {
         curr = itr.next();
         final float currStep = curr.step;
         final String currClrStr = curr.clr.toGgrString();

         Utils.toFixed(sb, prevStep, 6);
         sb.append(' ');
         Utils.toFixed(sb, ( prevStep + currStep ) * 0.5f, 6);
         sb.append(' ');
         Utils.toFixed(sb, currStep, 6);
         sb.append(' ');
         sb.append(prevClrStr);
         sb.append(' ');
         sb.append(currClrStr);
         sb.append(' ');
         sb.append(blendType);
         sb.append(' ');
         sb.append(colorType);

         /*
          * Inkscape GGR import is sensitive to trailing empty spaces, so this
          * if-check must be retained.
          */
         if ( itr.hasNext() ) { sb.append('\n'); }

         prevStep = currStep;
         prevClrStr = currClrStr;
      }

      if ( nonOneLast ) {
         final String lastClrStr = lastColor.toGgrString();
         Utils.toFixed(sb, lastStep, 6);
         sb.append(' ');
         Utils.toFixed(sb, ( 1.0f + lastStep ) * 0.5f, 6);
         sb.append(" 1.000000 ");
         sb.append(lastClrStr);
         sb.append(' ');
         sb.append(lastClrStr);
         sb.append(' ');
         sb.append(blendType);
         sb.append(' ');
         sb.append(colorType);
      }

      return sb.toString();
   }

   /**
    * Returns a string representation of this gradient.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

   /**
    * Returns a string representation of this gradient.
    *
    * @param places number of decimal places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      return this.toString(new StringBuilder(512), places).toString();
   }

   /**
    * Returns a String representation of the gradient compatible with the SVG
    * format. Assumes a horizontal linear gradient from left to right.
    *
    * @param id the gradient id
    *
    * @return the string
    */
   public String toSvgString ( final String id ) {

      return this.toSvgString(id, 768, 64);
   }

   /**
    * Returns a String representation of the gradient compatible with the SVG
    * format. Assumes a horizontal linear gradient from left to right.
    *
    * @param id the gradient id
    * @param w  the width
    * @param h  the height
    *
    * @return the string
    */
   public String toSvgString ( final String id, final int w, final int h ) {

      return this.toSvgString(id, w, h, 0.0f, 0.5f, 1.0f, 0.5f);
   }

   /**
    * Returns a String representation of the gradient compatible with the SVG
    * format. Assumes a linear gradient with an origin and destination
    * point.<br>
    * <br>
    * Because SVG gradients require a <code>defs</code> tag, this class is not
    * compatible with {@link ISvgWritable}. This method is a convenience only.
    *
    * @param id the gradient id
    * @param w  the width
    * @param h  the height
    * @param x1 the origin x
    * @param y1 the origin y
    * @param x2 the destination x
    * @param y2 the destination y
    *
    * @return the string
    */
   @Experimental
   public String toSvgString ( final String id, final int w, final int h,
      final float x1, final float y1, final float x2, final float y2 ) {

      final String idTrim = id != null ? id.trim() : "";
      final String vid = idTrim.length() > 0 ? idTrim : "camzupGradient";

      final int vw = w < 3 ? 3 : w;
      final int vh = h < 3 ? 3 : h;
      final int vhHalf = h / 2;
      final String wStr = Utils.toFixed(vw, ISvgWritable.FIXED_PRINT);
      final String hMidStr = Utils.toFixed(vhHalf, ISvgWritable.FIXED_PRINT);
      final String hBtmStr = Utils.toFixed(vh, ISvgWritable.FIXED_PRINT);

      final float swLeft = 0.0f;
      final float swRight = vw;
      final StringBuilder svgp = new StringBuilder(1024);

      svgp.append("<svg ");
      svgp.append("xmlns=\"http://www.w3.org/2000/svg\" ");
      svgp.append("xmlns:xlink=\"http://www.w3.org/1999/xlink\" ");
      svgp.append("stroke=\"none\" ");
      svgp.append("shape-rendering=\"geometricPrecision\" ");
      svgp.append("viewBox=\"0 0 ");
      svgp.append(vw);
      svgp.append(' ');
      svgp.append(vh);
      svgp.append("\">\n");

      svgp.append("<defs>\n");
      svgp.append("<linearGradient id=\"");
      svgp.append(vid);
      svgp.append("\" x1=\"");
      Utils.toFixed(svgp, x1, ISvgWritable.FIXED_PRINT);
      svgp.append("\" y1=\"");
      Utils.toFixed(svgp, y1, ISvgWritable.FIXED_PRINT);
      svgp.append("\" x2=\"");
      Utils.toFixed(svgp, x2, ISvgWritable.FIXED_PRINT);
      svgp.append("\" y2=\"");
      Utils.toFixed(svgp, y2, ISvgWritable.FIXED_PRINT);
      svgp.append("\">\n");

      final StringBuilder sbSwatch = new StringBuilder(1024);
      sbSwatch.append("<g id=\"swatches\">\n");

      final int len = this.keys.size();
      final float toFac = len > 1 ? 1.0f / len : 1.0f;
      final Iterator < ColorKey > itr = this.keys.iterator();

      for ( int idx = 0; itr.hasNext(); ++idx ) {
         final ColorKey ck = itr.next();
         final Rgb clr = ck.clr;
         final String hex = Rgb.toHexWeb(clr);

         /* Color stops in linear gradient. */
         ck.toSvgString(svgp);
         if ( itr.hasNext() ) { svgp.append('\n'); }

         /* Swatches. */
         final float fac0 = idx * toFac;
         final float xl = ( 1.0f - fac0 ) * swLeft + fac0 * swRight;
         final float fac1 = ( idx + 1.0f ) * toFac;
         final float xr = ( 1.0f - fac1 ) * swLeft + fac1 * swRight;

         /*
          * It's possible for the same color to appear more than once, but ids
          * should be unique, so don't use hex code.
          */
         sbSwatch.append("<path id=\"");
         sbSwatch.append("swatch.");
         sbSwatch.append(idx);

         sbSwatch.append("\" d=\"M ");
         Utils.toFixed(sbSwatch, xl, ISvgWritable.FIXED_PRINT);
         sbSwatch.append(' ');
         sbSwatch.append(hMidStr);

         sbSwatch.append(" L ");
         Utils.toFixed(sbSwatch, xr, ISvgWritable.FIXED_PRINT);
         sbSwatch.append(' ');
         sbSwatch.append(hMidStr);

         sbSwatch.append(" L ");
         Utils.toFixed(sbSwatch, xr, ISvgWritable.FIXED_PRINT);
         sbSwatch.append(' ');
         sbSwatch.append(hBtmStr);

         sbSwatch.append(" L ");
         Utils.toFixed(sbSwatch, xl, ISvgWritable.FIXED_PRINT);
         sbSwatch.append(' ');
         sbSwatch.append(hBtmStr);

         sbSwatch.append(" Z\" fill-opacity=\"");
         Utils.toFixed(sbSwatch, Utils.clamp01(clr.alpha),
            ISvgWritable.FIXED_PRINT);

         sbSwatch.append("\" fill=\"");
         sbSwatch.append(hex);
         sbSwatch.append("\" />\n");
      }

      sbSwatch.append("</g>\n");
      svgp.append("\n</linearGradient>\n");
      svgp.append("</defs>\n");
      svgp.append("<path id=\"ramp\" d=\"M 0.0 0.0 L ");
      svgp.append(wStr);
      svgp.append(" 0.0 L ");
      svgp.append(wStr);
      svgp.append(' ');
      svgp.append(hMidStr);
      svgp.append(" L 0.0 ");
      svgp.append(hMidStr);
      svgp.append(" Z\" fill=\"url('#");
      svgp.append(vid);
      svgp.append("')\"");
      svgp.append(" />\n");
      svgp.append(sbSwatch);
      svgp.append("</svg>");
      return svgp.toString();
   }

   /**
    * Internal helper function to assist with methods that need to print many
    * gradients. Appends to an existing {@link StringBuilder}.
    *
    * @param sb     the string builder
    * @param places the number of places
    *
    * @return the string builder
    */
   StringBuilder toString ( final StringBuilder sb, final int places ) {

      sb.append("{\"keys\":[");
      final Iterator < ColorKey > itr = this.keys.iterator();
      while ( itr.hasNext() ) {
         itr.next().toString(sb, places);
         if ( itr.hasNext() ) { sb.append(','); }
      }
      sb.append("]}");
      return sb;
   }

   /**
    * Helper function that compresses existing keys to the left when a new
    * color is added to the gradient without a key.
    *
    * @param added number of new items
    *
    * @return this gradient
    */
   protected Gradient compressKeysLeft ( final int added ) {

      final Iterator < ColorKey > itr = this.keys.iterator();
      final float scalar = 1.0f / ( this.keys.size() + added - 1.0f );
      for ( int i = 0; itr.hasNext(); ++i ) {
         final ColorKey key = itr.next();
         key.step = key.step * i * scalar;
      }

      return this;
   }

   /**
    * Helper function that compresses existing keys to the right when a new
    * color is added to the gradient without a key.
    *
    * @param added number of new items
    *
    * @return this gradient
    */
   protected Gradient compressKeysRight ( final int added ) {

      final float scalar = added / ( this.keys.size() + added - 1.0f );
      final float coeff = 1.0f - scalar;
      for ( final ColorKey key : this.keys ) {
         key.step = scalar + coeff * key.step;
      }
      return this;
   }

   /**
    * Tests to see if this gradient equals another.
    *
    * @param other the other gradient
    *
    * @return the evaluation
    */
   protected boolean equals ( final Gradient other ) {

      return this.keys.equals(other.keys);
   }

   /**
    * Clamps all colors in a gradient to the range [0.0, 1.0] .
    *
    * @param source the source gradient
    * @param target the target gradient
    *
    * @return the clamped gradient
    *
    * @see Rgb#clamp01(Rgb, Rgb)
    */
   public static Gradient clamp01 ( final Gradient source,
      final Gradient target ) {

      final TreeSet < ColorKey > srcKeys = source.keys;
      Iterator < ColorKey > srcItr;

      if ( source == target ) {
         srcItr = srcKeys.iterator();
         while ( srcItr.hasNext() ) {
            final Rgb clr = srcItr.next().clr;
            Rgb.clamp01(clr, clr);
         }
      } else {
         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         srcItr = srcKeys.iterator();
         while ( srcItr.hasNext() ) {
            final ColorKey trgKey = new ColorKey(srcItr.next());
            final Rgb trgClr = trgKey.clr;
            Rgb.clamp01(trgClr, trgClr);
            trgKeys.add(trgKey);
         }
      }

      return target;
   }

   /**
    * Finds a color given a step in the range [0.0, 1.0] . When the step falls
    * between color keys, the resultant color is generated by an easing
    * function.<br>
    * <br>
    * For use with images, as this returns an integer following the 0xAARRGGBB
    * format.
    *
    * @param grd  the gradient
    * @param step the step
    *
    * @return the color
    *
    * @see Rgb#toHexIntWrap(Rgb)
    * @see TreeSet#floor(Object)
    * @see TreeSet#ceiling(Object)
    * @see TreeSet#first()
    * @see TreeSet#last()
    * @see Utils#div(float, float)
    */
   public static int eval ( final Gradient grd, final float step ) {

      grd.query.step = step;

      final ColorKey prev = grd.keys.floor(grd.query);
      if ( prev == null ) { return Rgb.toHexIntWrap(grd.keys.first().clr); }

      final ColorKey next = grd.keys.ceiling(grd.query);
      if ( next == null ) { return Rgb.toHexIntWrap(grd.keys.last().clr); }

      return Rgb.mix(next.clr, prev.clr, Utils.div(step - next.step, prev.step
         - next.step));
   }

   /**
    * Finds a color given a step in the range [0.0, 1.0] . When the step falls
    * between color keys, the resultant color is created by an easing
    * function.
    *
    * @param grd    the gradient
    * @param step   the step
    * @param target the output color
    *
    * @return the color
    *
    * @see Rgb#mix(Rgb, Rgb, float, Rgb)
    * @see TreeSet#floor(Object)
    * @see TreeSet#ceiling(Object)
    * @see TreeSet#first()
    * @see TreeSet#last()
    * @see Utils#div(float, float)
    */
   public static Rgb eval ( final Gradient grd, final float step,
      final Rgb target ) {

      grd.query.step = step;

      final ColorKey prev = grd.keys.floor(grd.query);
      if ( prev == null ) { return target.set(grd.keys.first().clr); }

      final ColorKey next = grd.keys.ceiling(grd.query);
      if ( next == null ) { return target.set(grd.keys.last().clr); }

      return Rgb.mix(next.clr, prev.clr, Utils.div(step - next.step, prev.step
         - next.step), target);
   }

   /**
    * Finds a color given a step in the range [0.0, 1.0] . When the step falls
    * between color keys, the resultant color is generated by an easing
    * function.
    *
    * @param grd    the gradient
    * @param step   the step
    * @param easing the easing function
    * @param target the output color
    *
    * @return the color
    *
    * @see TreeSet#floor(Object)
    * @see TreeSet#ceiling(Object)
    * @see TreeSet#first()
    * @see TreeSet#last()
    * @see Utils#div(float, float)
    */
   public static Rgb eval ( final Gradient grd, final float step,
      final Rgb.AbstrEasing easing, final Rgb target ) {

      /*
       * This is different from above evaluation methods in order to facilitate,
       * e.g., mixing colors for normal maps.
       */

      grd.query.step = Utils.clamp01(step);

      ColorKey prev = grd.keys.floor(grd.query);
      if ( prev == null ) { prev = grd.keys.first(); }

      ColorKey next = grd.keys.ceiling(grd.query);
      if ( next == null ) { next = grd.keys.last(); }

      /* This needs to be Utils.div to avoid returning 0x0 as a color. */
      return easing.applyUnclamped(next.clr, prev.clr, Utils.div(step
         - next.step, prev.step - next.step), target);
   }

   /**
    * Evaluates an array of colors given a supplied count.
    *
    * @param grd   the gradient
    * @param count the count
    *
    * @return the array
    */
   public static Rgb[] evalRange ( final Gradient grd, final int count ) {

      return Gradient.evalRange(grd, count, 0.0f, 1.0f);
   }

   /**
    * Evaluates an array of colors given a supplied count. The origin and
    * destination specify the step at the beginning and end of the sample.
    *
    * @param grd    the gradient
    * @param count  the count
    * @param origin the origin
    * @param dest   the destination
    *
    * @return the array
    *
    * @see Utils#clamp01(float)
    * @see Gradient#eval(Gradient, float, Rgb)
    */
   public static Rgb[] evalRange ( final Gradient grd, final int count,
      final float origin, final float dest ) {

      final int vCount = count < 2 ? 2 : count;
      final float vOrigin = Utils.clamp01(origin);
      final float vDest = Utils.clamp01(dest);
      final Rgb[] result = new Rgb[vCount];
      final float toPercent = 1.0f / ( vCount - 1.0f );
      for ( int i = 0; i < vCount; ++i ) {
         final float prc = i * toPercent;
         result[i] = Gradient.eval(grd, ( 1.0f - prc ) * vOrigin + prc * vDest,
            new Rgb());
      }
      return result;
   }

   /**
    * Evaluates an array of colors given a supplied count. The origin and
    * destination specify the step at the beginning and end of the sample.
    *
    * @param grd    the gradient
    * @param count  the count
    * @param origin the origin
    * @param dest   the destination
    * @param easing the easing function
    *
    * @return the array
    *
    * @see Utils#clamp01(float)
    * @see Gradient#eval(Gradient, float, Rgb.AbstrEasing, Rgb)
    */
   public static Rgb[] evalRange ( final Gradient grd, final int count,
      final float origin, final float dest, final Rgb.AbstrEasing easing ) {

      final int vCount = count < 2 ? 2 : count;
      final float vOrigin = Utils.clamp01(origin);
      final float vDest = Utils.clamp01(dest);
      final Rgb[] result = new Rgb[vCount];
      final float toPercent = 1.0f / ( vCount - 1.0f );
      for ( int i = 0; i < vCount; ++i ) {
         final float prc = i * toPercent;
         result[i] = Gradient.eval(grd, ( 1.0f - prc ) * vOrigin + prc * vDest,
            easing, new Rgb());
      }
      return result;
   }

   /**
    * Sets the step of each color key in a gradient to the transparency of the
    * key's color. Similar to {@link Gradient#sort(Comparator)} in the order
    * of color keys; however, sort leaves key steps unchanged.
    *
    * @param source the input gradient
    * @param target the output gradient
    *
    * @return the gradient
    */
   public static Gradient keysByAlpha ( final Gradient source,
      final Gradient target ) {

      if ( source == target ) {

         final TreeSet < ColorKey > srcKeys = target.keys;
         final ArrayList < ColorKey > trgKeys = new ArrayList <>(srcKeys
            .size());
         final Iterator < ColorKey > srcItr = srcKeys.iterator();
         while ( srcItr.hasNext() ) {
            final Rgb srcClr = srcItr.next().clr;
            trgKeys.add(new ColorKey(srcClr.alpha, srcClr));
         }
         srcKeys.clear();
         srcKeys.addAll(trgKeys);

      } else {

         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         final Iterator < ColorKey > srcItr = source.keys.iterator();
         while ( srcItr.hasNext() ) {
            final Rgb srcClr = srcItr.next().clr;
            trgKeys.add(new ColorKey(srcClr.alpha, srcClr));
         }

      }

      return target;
   }

   /**
    * Sets the step of each color key in a gradient to the luminance of the
    * key's color. Similar to {@link Gradient#sort(Comparator)} in the order
    * of color keys; however, sort leaves key steps unchanged.<br>
    * <br>
    * Useful when the gradient is applied to an image as a palette swap or a
    * false color with the image's perceived luminance serving as an
    * evaluation factor.
    *
    * @param source the input gradient
    * @param target the output gradient
    *
    * @return the gradient
    *
    * @see Rgb#sRgbLuminance(Rgb)
    */
   public static Gradient keysByLuminance ( final Gradient source,
      final Gradient target ) {

      if ( source == target ) {

         final TreeSet < ColorKey > srcKeys = target.keys;
         final ArrayList < ColorKey > trgKeys = new ArrayList <>(srcKeys
            .size());
         final Iterator < ColorKey > srcItr = srcKeys.iterator();
         while ( srcItr.hasNext() ) {
            final Rgb srcClr = srcItr.next().clr;
            trgKeys.add(new ColorKey(Rgb.sRgbLuminance(srcClr), srcClr));
         }
         srcKeys.clear();
         srcKeys.addAll(trgKeys);

      } else {

         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         final Iterator < ColorKey > srcItr = source.keys.iterator();
         while ( srcItr.hasNext() ) {
            final Rgb srcClr = srcItr.next().clr;
            trgKeys.add(new ColorKey(Rgb.sRgbLuminance(srcClr), srcClr));
         }

      }

      return target;
   }

   /**
    * Returns a cyanotype palette, such as those used in creating blueprints,
    * with 8 color keys.
    *
    * @param target the output gradient
    *
    * @return the gradient
    */
   public static Gradient paletteCyanotype ( final Gradient target ) {

      final TreeSet < ColorKey > keys = target.keys;
      keys.clear();

      keys.add(new ColorKey(0.0f, 0.03049412f, 0.10188236f, 0.17378825f));
      keys.add(new ColorKey(0.14285715f, 0.05563067f, 0.21327104f, 0.3482909f));
      keys.add(new ColorKey(0.2857143f, 0.0880449f, 0.3129639f, 0.4667135f));
      keys.add(new ColorKey(0.42857143f, 0.12713926f, 0.40834653f, 0.5497747f));
      keys.add(new ColorKey(0.5714286f, 0.27109164f, 0.5239352f, 0.63098204f));
      keys.add(new ColorKey(0.71428573f, 0.48638815f, 0.6944785f, 0.7600696f));
      keys.add(new ColorKey(0.85714287f, 0.7537112f, 0.85927427f, 0.8739126f));
      keys.add(new ColorKey(1.0f, 0.9926431f, 0.9935216f, 0.9940706f));

      return target;
   }

   /**
    * Returns a monochrome palette with 4 colors that simulates an LCD
    * display.
    *
    * @param target the output gradient
    *
    * @return the gradient
    */
   public static Gradient paletteLcd ( final Gradient target ) {

      final TreeSet < ColorKey > keys = target.keys;
      keys.clear();

      keys.add(new ColorKey(0.0f, 0.054901965f, 0.18823531f, 0.16078432f));
      keys.add(new ColorKey(0.25f, 0.27450982f, 0.40784317f, 0.32156864f));
      keys.add(new ColorKey(0.75f, 0.54901963f, 0.7254902f, 0.41176474f));
      keys.add(new ColorKey(1.0f, 0.7686275f, 0.89019614f, 0.6505883f));

      return target;
   }

   /**
    * Returns the Magma color palette, consisting of 16 keys.
    *
    * @param target the output gradient
    *
    * @return the gradient
    */
   public static Gradient paletteMagma ( final Gradient target ) {

      final TreeSet < ColorKey > keys = target.keys;
      keys.clear();

      keys.add(new ColorKey(0.0f, 0.0f, 0.0f, 0.019608f));
      keys.add(new ColorKey(0.06666667f, 0.040784f, 0.028758f, 0.110327f));
      keys.add(new ColorKey(0.13333333f, 0.093856f, 0.036863f, 0.232941f));
      keys.add(new ColorKey(0.2f, 0.174118f, 0.006275f, 0.357647f));

      keys.add(new ColorKey(0.26666668f, 0.267974f, 0.002353f, 0.416732f));
      keys.add(new ColorKey(0.33333334f, 0.36732f, 0.045752f, 0.43268f));
      keys.add(new ColorKey(0.4f, 0.471373f, 0.080784f, 0.430588f));
      keys.add(new ColorKey(0.46666667f, 0.584052f, 0.110588f, 0.413856f));

      keys.add(new ColorKey(0.5333333f, 0.703268f, 0.142484f, 0.383007f));
      keys.add(new ColorKey(0.6f, 0.824314f, 0.198431f, 0.334902f));
      keys.add(new ColorKey(0.6666667f, 0.912418f, 0.286275f, 0.298039f));
      keys.add(new ColorKey(0.73333335f, 0.962353f, 0.412549f, 0.301176f));

      keys.add(new ColorKey(0.8f, 0.981176f, 0.548235f, 0.35451f));
      keys.add(new ColorKey(0.8666667f, 0.984314f, 0.694118f, 0.446275f));
      keys.add(new ColorKey(0.93333334f, 0.98719f, 0.843137f, 0.562092f));
      keys.add(new ColorKey(1.0f, 0.988235f, 1.0f, 0.698039f));

      return target;
   }

   /**
    * Returns seven primary and secondary colors: red, yellow, green, cyan,
    * blue, magenta and red. Red is repeated so that the gradient is periodic.
    *
    * @param target the output gradient
    *
    * @return the gradient
    */
   public static Gradient paletteRgb ( final Gradient target ) {

      final TreeSet < ColorKey > keys = target.keys;
      keys.clear();

      keys.add(new ColorKey(0.0f, 1.0f, 0.0f, 0.0f));
      keys.add(new ColorKey(0.16666667f, 1.0f, 1.0f, 0.0f));
      keys.add(new ColorKey(0.33333334f, 0.0f, 1.0f, 0.0f));
      keys.add(new ColorKey(0.5f, 0.0f, 1.0f, 1.0f));
      keys.add(new ColorKey(0.6666667f, 0.0f, 0.0f, 1.0f));
      keys.add(new ColorKey(0.8333333f, 1.0f, 0.0f, 1.0f));
      keys.add(new ColorKey(1.0f, 1.0f, 0.0f, 0.0f));

      return target;
   }

   /**
    * Returns colors in the red yellow blue color wheel. Red is repeated so
    * that the gradient is periodic.
    *
    * @param target the output gradient
    *
    * @return the gradient
    */
   public static Gradient paletteRyb ( final Gradient target ) {

      final TreeSet < ColorKey > keys = target.keys;
      keys.clear();

      keys.add(new ColorKey(0.0f, 1.0f, 0.0f, 0.0f));
      keys.add(new ColorKey(0.08333333f, 1.0f, 0.4166667f, 0.0f));
      keys.add(new ColorKey(0.16666667f, 1.0f, 0.6363637f, 0.0f));
      keys.add(new ColorKey(0.25f, 1.0f, 0.8125f, 0.0f));
      keys.add(new ColorKey(0.3333333f, 1.0f, 1.0f, 0.0f));
      keys.add(new ColorKey(0.4166667f, 0.5058824f, 0.8313726f, 0.1019608f));
      keys.add(new ColorKey(0.5f, 0.0f, 0.6627451f, 0.2f));
      keys.add(new ColorKey(0.5833333f, 0.082352884f, 0.5176469f, 0.4f));
      keys.add(new ColorKey(0.6666667f, 0.06666667f, 0.34901962f, 0.6509804f));
      keys.add(new ColorKey(0.75f, 0.23559582f, 0.16500752f, 0.57254905f));
      keys.add(new ColorKey(0.8333333f, 0.4109478f, 0.04575158f, 0.5204248f));
      keys.add(new ColorKey(0.9166667f, 0.66666667f, 0.0f, 0.33333333f));
      keys.add(new ColorKey(1.0f, 1.0f, 0.0f, 0.0f));

      return target;
   }

   /**
    * Returns a sepia tone palette, consisting of 8 keys.
    *
    * @param target the output gradient
    *
    * @return the gradient
    */
   public static Gradient paletteSepia ( final Gradient target ) {

      final TreeSet < ColorKey > keys = target.keys;
      keys.clear();

      /* Second key is pushed a little closer to zero. */
      keys.add(new ColorKey(0.0f, 0.11774882f, 0.09636405f, 0.07582238f));
      keys.add(new ColorKey(0.12f, 0.17625594f, 0.141371f, 0.1127016f));
      keys.add(new ColorKey(0.2857143f, 0.40054432f, 0.30952805f, 0.22960682f));
      keys.add(new ColorKey(0.42857146f, 0.527977f, 0.40815717f, 0.27178848f));
      keys.add(new ColorKey(0.5714286f, 0.6450869f, 0.5168457f, 0.34436274f));
      keys.add(new ColorKey(0.71428573f, 0.759382f, 0.64926106f, 0.4650265f));
      keys.add(new ColorKey(0.8571429f, 0.8737513f, 0.81925327f, 0.63157284f));
      keys.add(new ColorKey(1.0f, 0.9761859f, 0.93075234f, 0.74810547f));

      return target;
   }

   /**
    * Returns a heavily stylized approximation of color temperature, where the
    * middle key (0.5) is white at 6500 Kelvin. The lower bound (0.0), black,
    * is at 800 Kelvin; the upper bound (1.0) is a blue tinted white at 12000
    * Kelvin.
    *
    * @param target the output gradient
    *
    * @return the gradient
    */
   public static Gradient paletteTemperature ( final Gradient target ) {

      final TreeSet < ColorKey > keys = target.keys;
      keys.clear();

      keys.add(new ColorKey(0.0f, 1.0f, 0.0f, 0.0f));
      keys.add(new ColorKey(0.125f, 1.0f, 0.4313f, 0.047f));
      keys.add(new ColorKey(0.25f, 1.0f, 0.7019f, 0.4235f));
      keys.add(new ColorKey(0.375f, 1.0f, 0.8549f, 0.7176f));
      keys.add(new ColorKey(0.5f, 1.0f, 1.0f, 1.0f)); /* 6500K */
      keys.add(new ColorKey(1.0f, 0.7372f, 0.8156f, 1.0f)); /* 12000K */

      return target;
   }

   /**
    * Returns the Viridis color palette, consisting of 16 keys.
    *
    * @param target the output gradient
    *
    * @return the gradient
    */
   public static Gradient paletteViridis ( final Gradient target ) {

      final TreeSet < ColorKey > keys = target.keys;
      keys.clear();

      keys.add(new ColorKey(0.0f, 0.266667f, 0.003922f, 0.329412f));
      keys.add(new ColorKey(0.06666667f, 0.282353f, 0.100131f, 0.420654f));
      keys.add(new ColorKey(0.13333333f, 0.276078f, 0.184575f, 0.487582f));
      keys.add(new ColorKey(0.2f, 0.254902f, 0.265882f, 0.527843f));

      keys.add(new ColorKey(0.26666668f, 0.221961f, 0.340654f, 0.549281f));
      keys.add(new ColorKey(0.33333334f, 0.192157f, 0.405229f, 0.554248f));
      keys.add(new ColorKey(0.4f, 0.164706f, 0.469804f, 0.556863f));
      keys.add(new ColorKey(0.46666667f, 0.139869f, 0.534379f, 0.553464f));

      keys.add(new ColorKey(0.5333333f, 0.122092f, 0.595033f, 0.543007f));
      keys.add(new ColorKey(0.6f, 0.139608f, 0.658039f, 0.516863f));
      keys.add(new ColorKey(0.6666667f, 0.210458f, 0.717647f, 0.471895f));
      keys.add(new ColorKey(0.73333335f, 0.326797f, 0.773595f, 0.407582f));

      keys.add(new ColorKey(0.8f, 0.477647f, 0.821961f, 0.316863f));
      keys.add(new ColorKey(0.8666667f, 0.648366f, 0.858039f, 0.208889f));
      keys.add(new ColorKey(0.93333334f, 0.825098f, 0.884967f, 0.114771f));
      keys.add(new ColorKey(1.0f, 0.992157f, 0.905882f, 0.145098f));

      return target;
   }

   /**
    * Internal helper function to reverse the <em>steps</em> in an array of
    * color keys. Does <em>not</em> reverse the ordering of the elements, as
    * it is assumed that the keys will be returned to an ordered set.
    *
    * @param arr   the array
    * @param start the start index
    * @param end   the end index
    *
    * @return the array
    */
   protected static ColorKey[] reverse ( final ColorKey[] arr, final int start,
      final int end ) {

      /*
       * This belongs to the gradient class, and not the color key class,
       * because it does not reverse the array, it only swaps the steps.
       */

      for ( int st = start, ed = end; st < ed; --ed, ++st ) {
         final float temp = arr[st].step;
         arr[st].step = arr[ed].step;
         arr[ed].step = temp;
      }
      return arr;
   }

}
