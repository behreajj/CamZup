package camzup.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;

/**
 * A mutable, extensible class that contains a list of keys which hold
 * colors at steps in the range [0.0, 1.0] . Allows smooth color
 * transitions to be evaluated by a factor.
 */
public class Gradient implements IUtils, Iterable < ColorKey > {

   /**
    * The set of keys.
    */
   public final TreeSet < ColorKey > keys;

   /**
    * A temporary variable to hold queries in evaluation functions.
    */
   protected final ColorKey query;

   {
      /*
       * Sort quantized cannot be used as a comparator supplied to this
       * TreeSet's constructor, as it leads to bugs when an unknown number of
       * keys are supplied to the gradient.
       */
      this.keys = new TreeSet <>();
      this.query = new ColorKey();
   }

   /**
    * Creates a gradient with two default color keys, clear black at 0.0 and
    * opaque white at 1.0.
    *
    * @see Color#clearBlack(Color)
    * @see Color#white(Color)
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
   public Gradient ( final Collection < Color > colors ) {

      this.appendAll(colors);
   }

   /**
    * Creates a gradient from a color; an additional color key, white at 0.0,
    * is created.
    *
    * @param color the color
    *
    * @see Color#white(Color)
    */
   public Gradient ( final Color color ) {

      this.keys.add(new ColorKey(0.0f, 1.0f, 1.0f, 1.0f, 1.0f));
      this.keys.add(new ColorKey(1.0f, color));
   }

   /**
    * Creates a gradient from a list of colors; the resultant keys are evenly
    * distributed over the range [0.0, 1.0].
    *
    * @param colors the colors
    */
   public Gradient ( final Color... colors ) { this.appendAll(colors); }

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
    * Constructs a copy of a source gradient.
    *
    * @param source the source
    */
   public Gradient ( final Gradient source ) { this.set(source); }

   /**
    * Creates a gradient from a color integer; an additional color key, white
    * at 0.0, is created.
    *
    * @param color the color
    *
    * @see Color#white(Color)
    */
   public Gradient ( final int color ) {

      this.keys.add(new ColorKey(0.0f, 1.0f, 1.0f, 1.0f, 1.0f));
      this.keys.add(new ColorKey(1.0f, color));
   }

   /**
    * Creates a gradient from a list of color integers; the resultant keys are
    * evenly distributed over the range [0.0, 1.0] .
    *
    * @param colors the colors
    */
   public Gradient ( final int... colors ) { this.appendAll(colors); }

   /**
    * Appends a color at step 1.0 . Compresses existing keys to the left.
    *
    * @param color the color
    *
    * @return this gradient
    *
    * @see Gradient#compressKeysLeft(int)
    * @see TreeSet#add(Object)
    */
   public Gradient append ( final Color color ) {

      this.compressKeysLeft(1);
      this.keys.add(new ColorKey(1.0f, color));
      return this;
   }

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
      final float scl = Utils.clamp01(scalar);
      this.keys.add(new ColorKey(1.0f, scl, scl, scl, scl));
      return this;
   }

   /**
    * Appends a color at step 1.0 . Compresses existing keys to the left.
    *
    * @param color the color
    *
    * @return this gradient
    *
    * @see Gradient#compressKeysLeft(int)
    * @see TreeSet#add(Object)
    */
   public Gradient append ( final int color ) {

      this.compressKeysLeft(1);
      this.keys.add(new ColorKey(1.0f, color));
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
   public Gradient appendAll ( final Collection < Color > colors ) {

      final int len = colors.size();
      this.compressKeysLeft(len);
      final float oldLen = this.keys.size();
      final float denom = 1.0f / ( oldLen + len - 1.0f );

      final Iterator < Color > clrItr = colors.iterator();
      for ( int i = 0; clrItr.hasNext(); ++i ) {
         this.keys.add(new ColorKey( ( oldLen + i ) * denom, clrItr.next()));
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
   public Gradient appendAll ( final Color... colors ) {

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

      final ArrayList < ColorKey > keyArr = new ArrayList <>();
      keyArr.addAll(this.keys);
      this.keys.clear();
      final Iterator < ColorKey > itr = keyArr.iterator();
      final float denom = 1.0f / ( keyArr.size() - 1.0f );

      for ( float incr = 0.0f; itr.hasNext(); ++incr ) {
         itr.next().step = incr * denom;
      }

      this.keys.addAll(keyArr);
      return this;
   }

   /**
    * Tests to see if this gradient equals an object
    *
    * @param obj the object
    *
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
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
   public int hashCode ( ) {

      return this.keys == null ? 0 : this.keys.hashCode();
   }

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
    * Prepends a color at step 0.0 . Compresses existing keys to the right.
    *
    * @param color the color
    *
    * @return this gradient
    *
    * @see Gradient#compressKeysRight(int)
    * @see TreeSet#add(Object)
    */
   public Gradient prepend ( final Color color ) {

      this.compressKeysRight(1);
      this.keys.add(new ColorKey(0.0f, color));
      return this;
   }

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
      final float scl = Utils.clamp01(scalar);
      this.keys.add(new ColorKey(0.0f, scl, scl, scl, scl));
      return this;
   }

   /**
    * Prepends a color at step 0.0 . Compresses existing keys to the right.
    *
    * @param color the color
    *
    * @return this gradient
    *
    * @see Gradient#compressKeysRight(int)
    * @see TreeSet#add(Object)
    */
   public Gradient prepend ( final int color ) {

      this.compressKeysRight(1);
      this.keys.add(new ColorKey(0.0f, color));
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
   public Gradient prependAll ( final Collection < Color > colors ) {

      final int len = colors.size();
      this.compressKeysRight(len);
      final float denom = 1.0f / ( this.keys.size() + len - 1.0f );

      final Iterator < Color > clrItr = colors.iterator();
      for ( int i = 0; clrItr.hasNext(); ++i ) {
         this.keys.add(new ColorKey(i * denom, clrItr.next()));
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
   public Gradient prependAll ( final Color... colors ) {

      final int len = colors.length;
      this.compressKeysRight(len);
      final float denom = 1.0f / ( this.keys.size() + len - 1.0f );

      for ( int i = 0; i < len; ++i ) {
         this.keys.add(new ColorKey(i * denom, colors[i]));
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
    * Removes a key from the gradient. Returns true if successful.
    *
    * @param key the key
    *
    * @return the success
    */
   public boolean remove ( final ColorKey key ) {

      return this.keys.remove(key);
   }

   /**
    * Removes the first key from the gradient.
    *
    * @return the key
    *
    * @see TreeSet#pollFirst()
    */
   public ColorKey removeFirst ( ) { return this.keys.pollFirst(); }

   /**
    * Removes the last key from the gradient.
    *
    * @return the key
    *
    * @see TreeSet#pollLast()
    */
   public ColorKey removeLast ( ) { return this.keys.pollLast(); }

   /**
    * Resets this gradient to an initial state, with two color keys: clear
    * black at 0.0 and opaque white at 1.0 .
    *
    * @return this gradient
    *
    * @see Color#clearBlack(Color)
    * @see Color#white(Color)
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
   public Gradient sort ( final Comparator < Color > sorter ) {

      /*
       * Separate color keys into an array of steps and of colors. The key steps
       * should remain unaffected by the sort.
       */
      final int len = this.keys.size();
      final float[] steps = new float[len];
      final ArrayList < Color > clrList = new ArrayList <>(len);

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
      final Iterator < Color > clrItr = clrList.iterator();
      for ( int i = 0; clrItr.hasNext(); ++i ) {
         this.keys.add(new ColorKey(steps[i], clrItr.next()));
      }

      return this;
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

      return this.toBlenderCode(name, samples, 1.0f);
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

      final Color[] clrs = Gradient.evalRange(this, Utils.clamp(samples, 2,
         32));
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
      final Color firstColor = first.clr;
      final boolean nonZeroFirst = firstStep > 0.0f;

      final ColorKey last = this.keys.last();
      final float lastStep = last.step;
      final Color lastColor = last.clr;
      final boolean nonOneLast = lastStep < 1.0f;

      int len = this.keys.size() - 1;
      if ( nonZeroFirst ) { ++len; }
      if ( nonOneLast ) { ++len; }

      final StringBuilder sb = new StringBuilder();
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
         sb.append(' ').append(frstClrStr);
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
         sb.append('\n');

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
    * Returns a String representation of the gradient compatible with .gpl
    * (GIMP palette) file formats.
    *
    * @return the string
    */
   public String toGplString ( ) {

      return this.toGplString(this.hashIdentityString(), 0);
   }

   /**
    * Returns a String representation of the gradient compatible with .gpl
    * (GIMP palette) file formats.
    *
    * @param name palette name
    *
    * @return the string
    */
   public String toGplString ( final String name ) {

      return this.toGplString(name, 0);
   }

   /**
    * Returns a String representation of the gradient compatible with .gpl
    * (GIMP palette) file formats.
    *
    * @param name           palette name
    * @param displayColumns display columns
    *
    * @return the string
    */
   public String toGplString ( final String name, final int displayColumns ) {

      final StringBuilder sb = new StringBuilder(1024);
      sb.append("GIMP Palette\n");
      sb.append("Name: ");
      sb.append(name);
      sb.append('\n');
      sb.append("Columns: ");
      sb.append(displayColumns);
      sb.append('\n');
      sb.append("# https://github.com/behreajj/CamZup \n");

      final Iterator < ColorKey > itr = this.keys.iterator();
      while ( itr.hasNext() ) {
         itr.next().clr.toGplString(sb);
         sb.append('\n');
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

      final StringBuilder sb = new StringBuilder(16 + 128 * this.keys.size());
      sb.append("{ keys: [ ");
      final Iterator < ColorKey > itr = this.keys.iterator();
      while ( itr.hasNext() ) {
         itr.next().toString(sb, places);
         if ( itr.hasNext() ) { sb.append(',').append(' '); }
      }
      sb.append(" ] }");
      return sb.toString();
   }

   /**
    * Returns a String representation of the gradient compatible with the SVG
    * format. Assumes a linear gradient with an origin and destination point.
    *
    * @param id the gradient id
    *
    * @return the string
    */
   public String toSvgString ( final String id ) {

      return this.toSvgString(id, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Returns a String representation of the gradient compatible with the SVG
    * format. Assumes a linear gradient with an origin and destination point.
    *
    * @param id the gradient id
    * @param x1 the origin x
    * @param y1 the origin y
    * @param x2 the destination x
    * @param y2 the destination y
    *
    * @return the string
    */
   public String toSvgString ( final String id, final float x1, final float y1,
      final float x2, final float y2 ) {

      final StringBuilder svgp = new StringBuilder(1024);
      svgp.append("<linearGradient id=\"");
      svgp.append(id);
      svgp.append("\" x1=\"");
      Utils.toFixed(svgp, x1, 6);
      svgp.append("\" y1=\"");
      Utils.toFixed(svgp, y1, 6);
      svgp.append("\" x2=\"");
      Utils.toFixed(svgp, x2, 6);
      svgp.append("\" y2=\"");
      Utils.toFixed(svgp, y2, 6);
      svgp.append("\">");

      final Iterator < ColorKey > itr = this.keys.iterator();
      while ( itr.hasNext() ) { svgp.append(itr.next().toSvgString()); }

      svgp.append("</linearGradient>");
      return svgp.toString();
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

      /* Simplification of Utils.map(key.step, 0.0f, 1.0f, scalar, 1.0f); */
      final Iterator < ColorKey > itr = this.keys.iterator();
      final float scalar = added / ( this.keys.size() + added - 1.0f );
      final float coeff = 1.0f - scalar;
      while ( itr.hasNext() ) {
         final ColorKey key = itr.next();
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

      if ( this.keys == null ) {
         if ( other.keys != null ) { return false; }
      } else if ( !this.keys.equals(other.keys) ) { return false; }
      return true;
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
    * @see Color#toHexInt(Color)
    * @see TreeSet#floor(Object)
    * @see TreeSet#ceiling(Object)
    * @see TreeSet#first()
    * @see TreeSet#last()
    */
   public static int eval ( final Gradient grd, final float step ) {

      grd.query.step = step;

      final ColorKey prev = grd.keys.floor(grd.query);
      if ( prev == null ) { return Color.toHexInt(grd.keys.first().clr); }

      final ColorKey next = grd.keys.ceiling(grd.query);
      if ( next == null ) { return Color.toHexInt(grd.keys.last().clr); }

      final Color origin = next.clr;
      final Color dest = prev.clr;

      final float denom = prev.step - next.step;
      if ( denom != 0.0f ) {
         final float t = ( step - next.step ) / denom;
         final float u = 1.0f - t;
         return ( int ) ( ( u * origin.a + t * dest.a ) * 0xff + 0.5f ) << 0x18
            | ( int ) ( ( u * origin.r + t * dest.r ) * 0xff + 0.5f ) << 0x10
            | ( int ) ( ( u * origin.g + t * dest.g ) * 0xff + 0.5f ) << 0x8
            | ( int ) ( ( u * origin.b + t * dest.b ) * 0xff + 0.5f );
      }

      return Color.toHexInt(origin);
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
    * @see TreeSet#floor(Object)
    * @see TreeSet#ceiling(Object)
    * @see TreeSet#first()
    * @see TreeSet#last()
    */
   public static Color eval ( final Gradient grd, final float step,
      final Color target ) {

      grd.query.step = step;

      final ColorKey prev = grd.keys.floor(grd.query);
      if ( prev == null ) { return target.set(grd.keys.first().clr); }

      final ColorKey next = grd.keys.ceiling(grd.query);
      if ( next == null ) { return target.set(grd.keys.last().clr); }

      final Color origin = next.clr;
      final Color dest = prev.clr;

      final float denom = prev.step - next.step;
      if ( denom != 0.0f ) {
         final float t = ( step - next.step ) / denom;
         final float u = 1.0f - t;
         return target.set(u * origin.r + t * dest.r, u * origin.g + t * dest.g,
            u * origin.b + t * dest.b, u * origin.a + t * dest.a);
      }

      return target.set(origin);
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
    */
   public static Color eval ( final Gradient grd, final float step,
      final Color.AbstrEasing easing, final Color target ) {

      grd.query.step = step;

      final ColorKey prev = grd.keys.floor(grd.query);
      if ( prev == null ) { return target.set(grd.keys.first().clr); }

      final ColorKey next = grd.keys.ceiling(grd.query);
      if ( next == null ) { return target.set(grd.keys.last().clr); }

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
   public static Color[] evalRange ( final Gradient grd, final int count ) {

      final int vCount = count < 2 ? 2 : count;
      final Color[] result = new Color[vCount];
      final float toPercent = 1.0f / ( vCount - 1.0f );
      for ( int i = 0; i < vCount; ++i ) {
         result[i] = Gradient.eval(grd, i * toPercent, new Color());
      }
      return result;
   }

   /**
    * Evaluates an array of colors given a supplied count. The minimum count
    * is three.
    *
    * @param grd    the gradient
    * @param count  the count
    * @param easing the easing function
    *
    * @return the array
    */
   public static Color[] evalRange ( final Gradient grd, final int count,
      final Color.AbstrEasing easing ) {

      final int vCount = count < 2 ? 2 : count;
      final Color[] result = new Color[vCount];
      final float toPercent = 1.0f / ( vCount - 1.0f );
      for ( int i = 0; i < vCount; ++i ) {
         result[i] = Gradient.eval(grd, i * toPercent, easing, new Color());
      }
      return result;
   }

   /**
    * Inverts all colors in the gradient and reverses the gradient.
    *
    * @param source the source gradient
    * @param target the target gradient
    *
    * @return the inverse
    */
   public static Gradient inverse ( final Gradient source,
      final Gradient target ) {

      if ( source == target ) {

         final Iterator < ColorKey > kyItr = source.keys.iterator();
         while ( kyItr.hasNext() ) {
            final Color clr = kyItr.next().clr;
            Color.inverse(clr, clr);
         }

      } else {

         final TreeSet < ColorKey > trgKeys = target.keys;
         final Iterator < ColorKey > srcItr = source.keys.iterator();

         trgKeys.clear();

         while ( srcItr.hasNext() ) {
            final ColorKey trgKey = new ColorKey(srcItr.next());
            final Color clr = trgKey.clr;
            Color.inverse(clr, clr);
            trgKeys.add(trgKey);
         }
      }

      target.reverse();
      return target;
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
            final Color srcClr = srcItr.next().clr;
            trgKeys.add(new ColorKey(srcClr.a, srcClr));
         }
         srcKeys.clear();
         srcKeys.addAll(trgKeys);

      } else {

         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         final Iterator < ColorKey > srcItr = source.keys.iterator();
         while ( srcItr.hasNext() ) {
            final Color srcClr = srcItr.next().clr;
            trgKeys.add(new ColorKey(srcClr.a, srcClr));
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
    */
   public static Gradient keysByLuminance ( final Gradient source,
      final Gradient target ) {

      if ( source == target ) {

         final TreeSet < ColorKey > srcKeys = target.keys;
         final ArrayList < ColorKey > trgKeys = new ArrayList <>(srcKeys
            .size());
         final Iterator < ColorKey > srcItr = srcKeys.iterator();
         while ( srcItr.hasNext() ) {
            final Color srcClr = srcItr.next().clr;
            trgKeys.add(new ColorKey(Color.luminance(srcClr), srcClr));
         }
         srcKeys.clear();
         srcKeys.addAll(trgKeys);

      } else {

         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         final Iterator < ColorKey > srcItr = source.keys.iterator();
         while ( srcItr.hasNext() ) {
            final Color srcClr = srcItr.next().clr;
            trgKeys.add(new ColorKey(Color.luminance(srcClr), srcClr));
         }

      }

      return target;
   }

   /**
    * Mixes between two color gradients, producing a new gradient containing
    * uniformly distributed samples. The step provides for how much to mix
    * between the two gradients.
    *
    * @param origin  the origin gradient
    * @param dest    the destination gradient
    * @param samples the number of samples
    * @param step    the step
    * @param target  the output gradient
    *
    * @return the gradient
    */
   public static Gradient mix ( final Gradient origin, final Gradient dest,
      final int samples, final float step, final Gradient target ) {

      final Color.LerpRgba mixer = new Color.LerpRgba();
      return Gradient.mix(origin, dest, samples, new Function < Float,
         Float >() {
         @Override
         public Float apply ( final Float x ) { return step; }

      }, mixer, mixer, target);
   }

   /**
    * Mixes between two color gradients, producing a new gradient containing
    * uniformly distributed samples. The first color mixing functional object
    * is supplied to
    * {@link Gradient#eval(Gradient, float, camzup.core.Color.AbstrEasing, Color)}
    * for the origin and destination gradient. A {@link Function} supplies the
    * factor for the mixing to the second color mixer at a given sample.
    *
    * @param origin   the origin gradient
    * @param dest     the destination gradient
    * @param samples  the number of samples
    * @param stepFunc the step supplier
    * @param xMixer   the primary color mixer
    * @param yMixer   the secondary color mixer
    * @param target   the output gradient
    *
    * @return the gradient
    */
   public static Gradient mix ( final Gradient origin, final Gradient dest,
      final int samples, final Function < Float, Float > stepFunc,
      final Color.AbstrEasing xMixer, final Color.AbstrEasing yMixer,
      final Gradient target ) {

      final int vSamples = samples < 2 ? 2 : samples;
      final float toStep = 1.0f / ( vSamples - 1.0f );
      final TreeSet < ColorKey > keys = target.keys;
      keys.clear();
      final Color aEval = new Color();
      final Color bEval = new Color();
      for ( int i = 0; i < vSamples; ++i ) {
         final float step = i * toStep;
         Gradient.eval(origin, step, xMixer, aEval);
         Gradient.eval(dest, step, xMixer, bEval);
         final ColorKey key = new ColorKey();
         key.step = step;
         yMixer.apply(aEval, bEval, stepFunc.apply(step), key.clr);
         keys.add(key);
      }

      return target;
   }

   /**
    * Mixes between two color gradients, producing a new gradient containing
    * uniformly distributed samples.
    *
    * @param origin  the origin gradient
    * @param dest    the destination gradient
    * @param samples the number of samples
    * @param target  the output gradient
    *
    * @return the gradient
    */
   public static Gradient mix ( final Gradient origin, final Gradient dest,
      final int samples, final Gradient target ) {

      return Gradient.mix(origin, dest, samples, 0.5f, target);
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
    * Returns the Magma color palette, consisting of 16 keys.
    *
    * @param target the output gradient
    *
    * @return the gradient
    */
   public static Gradient paletteMagma ( final Gradient target ) {

      final TreeSet < ColorKey > keys = target.keys;
      keys.clear();

      keys.add(new ColorKey(0.0f, 0.988235f, 1.0f, 0.698039f));
      keys.add(new ColorKey(0.06666667f, 0.98719f, 0.843137f, 0.562092f));
      keys.add(new ColorKey(0.13333333f, 0.984314f, 0.694118f, 0.446275f));
      keys.add(new ColorKey(0.2f, 0.981176f, 0.548235f, 0.35451f));

      keys.add(new ColorKey(0.26666668f, 0.962353f, 0.412549f, 0.301176f));
      keys.add(new ColorKey(0.33333334f, 0.912418f, 0.286275f, 0.298039f));
      keys.add(new ColorKey(0.4f, 0.824314f, 0.198431f, 0.334902f));
      keys.add(new ColorKey(0.46666667f, 0.703268f, 0.142484f, 0.383007f));

      keys.add(new ColorKey(0.5333333f, 0.584052f, 0.110588f, 0.413856f));
      keys.add(new ColorKey(0.6f, 0.471373f, 0.080784f, 0.430588f));
      keys.add(new ColorKey(0.6666667f, 0.36732f, 0.045752f, 0.43268f));
      keys.add(new ColorKey(0.73333335f, 0.267974f, 0.002353f, 0.416732f));

      keys.add(new ColorKey(0.8f, 0.174118f, 0.006275f, 0.357647f));
      keys.add(new ColorKey(0.8666667f, 0.093856f, 0.036863f, 0.232941f));
      keys.add(new ColorKey(0.93333334f, 0.040784f, 0.028758f, 0.110327f));
      keys.add(new ColorKey(1.0f, 0.0f, 0.0f, 0.019608f));

      return target;
   }

   /**
    * Returns seven primary and secondary colors: red, yellow, green, cyan,
    * blue, magenta and red. Red is repeated so the gradient is periodic.
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
    * Returns thirteen colors in the red yellow blue color wheel. Red is
    * repeated so that the gradient is periodic.
    *
    * @param target the output gradient
    *
    * @return the gradient
    */
   public static Gradient paletteRyb ( final Gradient target ) {

      final TreeSet < ColorKey > keys = target.keys;
      keys.clear();

      keys.add(new ColorKey(0.0f, 1.0f, 0.0f, 0.0f));
      keys.add(new ColorKey(0.0833333f, 1.0f, 0.25f, 0.0f));
      keys.add(new ColorKey(0.1666667f, 1.0f, 0.5f, 0.0f));
      keys.add(new ColorKey(0.25f, 1.0f, 0.75f, 0.0f));
      keys.add(new ColorKey(0.3333333f, 1.0f, 1.0f, 0.0f));
      keys.add(new ColorKey(0.4166667f, 0.5058824f, 0.8313726f, 0.1019608f));
      keys.add(new ColorKey(0.5f, 0.0f, 0.6627451f, 0.2f));
      keys.add(new ColorKey(0.5833333f, 0.0823529f, 0.517647f, 0.4f));
      keys.add(new ColorKey(0.6666667f, 0.1647059f, 0.376471f, 0.6f));
      keys.add(new ColorKey(0.75f, 0.3333333f, 0.1882353f, 0.5529412f));
      keys.add(new ColorKey(0.8333333f, 0.5f, 0.0f, 0.5f));
      keys.add(new ColorKey(0.9166667f, 0.75f, 0.0f, 0.25f));
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

      keys.add(new ColorKey(0.0f, 0.11774882f, 0.09636405f, 0.07582238f));
      keys.add(new ColorKey(0.14285715f, 0.17625594f, 0.141371f, 0.1127016f));
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
    * Shifts the brightness of all colors in a gradient. The brightness is
    * clamped to the range [0.0, 1.0] . The source gradient may either be the
    * same as or different from the target.
    *
    * @param source the source gradient
    * @param shift  the brightness shift
    * @param target the output gradient
    * @param hsba   the color in HSB
    *
    * @return the shifted ramp
    */
   public static Gradient shiftBri ( final Gradient source, final float shift,
      final Gradient target, final Vec4 hsba ) {

      if ( source == target ) {
         final Iterator < ColorKey > kyItr = source.keys.iterator();
         while ( kyItr.hasNext() ) {
            final Color clr = kyItr.next().clr;
            Color.shiftBri(clr, shift, clr, hsba);
         }
      } else {
         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         final Iterator < ColorKey > srcItr = source.keys.iterator();
         while ( srcItr.hasNext() ) {
            final ColorKey trgKey = new ColorKey(srcItr.next());
            final Color clr = trgKey.clr;
            Color.shiftBri(clr, shift, clr, hsba);
            trgKeys.add(trgKey);
         }
      }

      return target;
   }

   /**
    * Shifts the hue, saturation and brightness in a gradient. The alpha
    * remains unaffected.
    *
    * @param source the input gradient
    * @param shift  the shift
    * @param target the output gradient
    * @param hsba   the color in HSB
    *
    * @return the shifted ramp
    */
   public static Gradient shiftHsb ( final Gradient source, final Vec4 shift,
      final Gradient target, final Vec4 hsba ) {

      if ( source == target ) {
         final Iterator < ColorKey > kyItr = source.keys.iterator();
         while ( kyItr.hasNext() ) {
            final Color clr = kyItr.next().clr;
            Color.shiftHsb(clr, shift, clr, hsba);
         }
      } else {
         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         final Iterator < ColorKey > srcItr = source.keys.iterator();
         while ( srcItr.hasNext() ) {
            final ColorKey trgKey = new ColorKey(srcItr.next());
            final Color clr = trgKey.clr;
            Color.shiftHsb(clr, shift, clr, hsba);
            trgKeys.add(trgKey);
         }
      }

      return target;
   }

   /**
    * Shifts the hue, saturation and brightness in a gradient.
    *
    * @param source the input gradient
    * @param shift  the shift
    * @param target the output gradient
    * @param hsba   the color in HSB
    *
    * @return the shifted ramp
    */
   public static Gradient shiftHsba ( final Gradient source, final Vec4 shift,
      final Gradient target, final Vec4 hsba ) {

      if ( source == target ) {
         final Iterator < ColorKey > kyItr = source.keys.iterator();
         while ( kyItr.hasNext() ) {
            final Color clr = kyItr.next().clr;
            Color.shiftHsba(clr, shift, clr, hsba);
         }
      } else {
         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         final Iterator < ColorKey > srcItr = source.keys.iterator();
         while ( srcItr.hasNext() ) {
            final ColorKey trgKey = new ColorKey(srcItr.next());
            final Color clr = trgKey.clr;
            Color.shiftHsba(clr, shift, clr, hsba);
            trgKeys.add(trgKey);
         }
      }

      return target;
   }

   /**
    * Shifts the hue of all colors in a gradient. The hue wraps around the
    * range [0.0, 1.0] . The source gradient may either be the same as or
    * different from the target.
    *
    * @param source the source gradient
    * @param shift  the hue shift
    * @param target the output gradient
    * @param hsba   the color in HSB
    *
    * @return the shifted ramp
    */
   public static Gradient shiftHue ( final Gradient source, final float shift,
      final Gradient target, final Vec4 hsba ) {

      if ( source == target ) {
         final Iterator < ColorKey > kyItr = source.keys.iterator();
         while ( kyItr.hasNext() ) {
            final Color clr = kyItr.next().clr;
            Color.shiftHue(clr, shift, clr, hsba);
         }
      } else {
         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         final Iterator < ColorKey > srcItr = source.keys.iterator();
         while ( srcItr.hasNext() ) {
            final ColorKey trgKey = new ColorKey(srcItr.next());
            final Color clr = trgKey.clr;
            Color.shiftHue(clr, shift, clr, hsba);
            trgKeys.add(trgKey);
         }
      }

      return target;
   }

   /**
    * Shifts the saturation of all colors in a gradient. The saturation is
    * clamped to the range [0.0, 1.0] . The source gradient may either be the
    * same as or different from the target.
    *
    * @param source the source gradient
    * @param shift  the saturation shift
    * @param target the output gradient
    * @param hsba   the color in HSB
    *
    * @return the shifted ramp
    */
   public static Gradient shiftSat ( final Gradient source, final float shift,
      final Gradient target, final Vec4 hsba ) {

      if ( source == target ) {
         final Iterator < ColorKey > kyItr = source.keys.iterator();
         while ( kyItr.hasNext() ) {
            final Color clr = kyItr.next().clr;
            Color.shiftSat(clr, shift, clr, hsba);
         }
      } else {
         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         final Iterator < ColorKey > srcItr = source.keys.iterator();
         while ( srcItr.hasNext() ) {
            final ColorKey trgKey = new ColorKey(srcItr.next());
            final Color clr = trgKey.clr;
            Color.shiftSat(clr, shift, clr, hsba);
            trgKeys.add(trgKey);
         }
      }

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

      int st = start;
      for ( int ed = end; st < ed; --ed ) {
         final float temp = arr[st].step;
         arr[st].step = arr[ed].step;
         arr[ed].step = temp;
         ++st;
      }
      return arr;
   }

}
