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
    */
   public Gradient ( ) {

      this.keys.add(new ColorKey(0.0f, Lab.clearBlack(new Lab())));
      this.keys.add(new ColorKey(1.0f, Lab.white(new Lab())));
   }

   /**
    * Creates a gradient from a collection of color integers; the resultant
    * keys are evenly distributed over the range [0.0, 1.0].
    *
    * @param colors the colors
    */
   public Gradient ( final Collection < Lab > colors ) {

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
      this.keys.add(new ColorKey(Utils.clamp01(scalar), Lab.fromScalar(scalar,
         new Lab())));
      this.keys.add(new ColorKey(0.0f, Lab.clearBlack(new Lab())));
      this.keys.add(new ColorKey(1.0f, Lab.white(new Lab())));
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
    *
    * @see Utils#lerp(float, float, float)
    */
   public Gradient ( final Lab c ) {

      final float step = Utils.lerp(IUtils.ONE_THIRD, IUtils.TWO_THIRDS, c.l
         * 0.01f);

      final Lab black = Lab.clearBlack(new Lab());
      final Lab white = Lab.white(new Lab());

      Lab.adoptAlpha(black, c, black);
      Lab.adoptAlpha(white, c, white);

      this.keys.add(new ColorKey(0.0f, black));
      this.keys.add(new ColorKey(step, c));
      this.keys.add(new ColorKey(1.0f, white));
   }

   /**
    * Creates a gradient from a list of colors; the resultant keys are evenly
    * distributed over the range [0.0, 1.0].
    *
    * @param colors the colors
    */
   public Gradient ( final Lab... colors ) { this.appendAll(colors); }

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
      this.keys.add(new ColorKey(1.0f, Lab.fromScalar(scalar, new Lab())));

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
   public Gradient append ( final Lab c ) {

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
   public Gradient appendAll ( final Collection < Lab > colors ) {

      final int len = colors.size();
      this.compressKeysLeft(len);
      final float oldLen = this.keys.size();
      final float denom = 1.0f / ( oldLen + len - 1.0f );

      final Iterator < Lab > clrItr = colors.iterator();
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
      final Lab gray = new Lab();

      for ( int i = 0; i < len; ++i ) {
         final float scalar = scalars[i];
         this.keys.add(new ColorKey( ( oldLen + i ) * denom, Lab.fromScalar(
            scalar, gray)));
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
   public Gradient appendAll ( final Lab... colors ) {

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

       this.keys.remove(key);
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
          this.keys.remove(key);
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
      this.keys.add(new ColorKey(0.0f, Lab.fromScalar(scalar, new Lab())));

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
   public Gradient prepend ( final Lab c ) {

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
   public Gradient prependAll ( final Collection < Lab > colors ) {

      final int len = colors.size();
      this.compressKeysRight(len);
      final float denom = 1.0f / ( this.keys.size() + len - 1.0f );

      final Iterator < Lab > clrItr = colors.iterator();
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
      final Lab gray = new Lab();

      for ( int i = 0; i < len; ++i ) {
         final float scalar = scalars[i];
         this.keys.add(new ColorKey(i * denom, Lab.fromScalar(scalar, gray)));
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
   public Gradient prependAll ( final Lab... colors ) {

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
    * @see TreeSet#clear()
    * @see TreeSet#add(Object)
    */
   public Gradient reset ( ) {

      this.keys.clear();
      this.keys.add(new ColorKey(0.0f, Lab.clearBlack(new Lab())));
      this.keys.add(new ColorKey(1.0f, Lab.white(new Lab())));

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
   public Gradient sort ( final Comparator < Lab > sorter ) {

      /*
       * Separate color keys into an array of steps and of colors. The key steps
       * should remain unaffected by the sort.
       */
      final int len = this.keys.size();
      final float[] steps = new float[len];
      final ArrayList < Lab > clrList = new ArrayList <>(len);

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
      final Iterator < Lab > clrItr = clrList.iterator();
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
   public Lab[] toArray ( ) {

      final Lab[] result = new Lab[this.keys.size()];
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

      final Lab[] labs = Gradient.evalRange(this, Utils.clamp(samples, 2, 32));
      final int len = labs.length;
      final int last = len - 1;
      final float toPercent = Utils.div(1.0f, last);

      final StringBuilder pyCd = new StringBuilder(2048);
      pyCd.append("from bpy import data as D, context as C\n\n");
      pyCd.append("grd_data = [");

      final Rgb srgb = new Rgb();
      final Rgb lrgb = new Rgb();
      final Vec4 xyz = new Vec4();

      for ( int i = 0; i < len; ++i ) {
         pyCd.append("\n    {\"position\": ");
         Utils.toFixed(pyCd, i * toPercent, 6);
         pyCd.append(", \"color\": ");
         Rgb.srLab2TosRgb(labs[i], srgb, lrgb, xyz);
         srgb.toBlenderCode(pyCd, gamma, true);
         pyCd.append('}');

         if ( i < last ) { pyCd.append(',').append(' '); }
      }
      pyCd.append(']');

      pyCd.append("\n\nmaterial = D.materials.new(\"");
      pyCd.append("id");
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
      final Lab firstColor = first.clr;
      final boolean nonZeroFirst = firstStep > 0.0f;

      final ColorKey last = this.keys.last();
      final float lastStep = last.step;
      final Lab lastColor = last.clr;
      final boolean nonOneLast = lastStep < 1.0f;

      final Rgb srgb = new Rgb();
      final Rgb lrgb = new Rgb();
      final Vec4 xyz = new Vec4();

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
         Rgb.srLab2TosRgb(firstColor, srgb, lrgb, xyz);
         final String frstClrStr = srgb.toGgrString();
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
      Rgb.srLab2TosRgb(curr.clr, srgb, lrgb, xyz);
      String prevClrStr = srgb.toGgrString();

      /*
       * Fence posting problem due to differences in gradient implementation.
       * Length of GIMP gradient is one less than this because each GIMP key is
       * a segment. This implementation's key lies on the left edge of the GIMP
       * key segment.
       */
      while ( itr.hasNext() ) {
         curr = itr.next();
         final float currStep = curr.step;
         Rgb.srLab2TosRgb(curr.clr, srgb, lrgb, xyz);
         final String currClrStr = srgb.toGgrString();

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
         Rgb.srLab2TosRgb(lastColor, srgb, lrgb, xyz);
         final String lastClrStr = srgb.toGgrString();
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

      final Rgb srgb = new Rgb();
      final Rgb lrgb = new Rgb();
      final Vec4 xyz = new Vec4();
      final StringBuilder sbSwatch = new StringBuilder(1024);
      sbSwatch.append("<g id=\"swatches\">\n");

      final int len = this.keys.size();
      final float toFac = len > 1 ? 1.0f / len : 1.0f;
      final Iterator < ColorKey > itr = this.keys.iterator();

      for ( int idx = 0; itr.hasNext(); ++idx ) {
         final ColorKey ck = itr.next();
         Rgb.srLab2TosRgb(ck.clr, srgb, lrgb, xyz);
         final String hex = Rgb.toHexWeb(srgb);

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
         Utils.toFixed(sbSwatch, Utils.clamp01(srgb.alpha),
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
    */
   public static int eval ( final Gradient grd, final float step ) {

      final Lab lab = Gradient.eval(grd, step, new Lab());
      final Rgb rgb = Rgb.srLab2TosRgb(lab, new Rgb(), new Rgb(), new Vec4());
      return rgb.toHexIntSat();
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
    * @see Lab#mix(Lab, Lab, float, Lab)
    * @see TreeSet#floor(Object)
    * @see TreeSet#ceiling(Object)
    * @see TreeSet#first()
    * @see TreeSet#last()
    * @see Utils#div(float, float)
    */
   public static Lab eval ( final Gradient grd, final float step,
      final Lab target ) {

      grd.query.step = step;

      final ColorKey prev = grd.keys.floor(grd.query);
      if ( prev == null ) { return target.set(grd.keys.first().clr); }

      final ColorKey next = grd.keys.ceiling(grd.query);
      if ( next == null ) { return target.set(grd.keys.last().clr); }

      return Lab.mix(next.clr, prev.clr, Utils.div(step - next.step, prev.step
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
   public static Lab eval ( final Gradient grd, final float step,
      final Lab.AbstrEasing easing, final Lab target ) {

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
   public static Lab[] evalRange ( final Gradient grd, final int count ) {

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
    * @see Gradient#eval(Gradient, float, Lab)
    */
   public static Lab[] evalRange ( final Gradient grd, final int count,
      final float origin, final float dest ) {

      final int vCount = count < 2 ? 2 : count;
      final float vOrigin = Utils.clamp01(origin);
      final float vDest = Utils.clamp01(dest);
      final Lab[] result = new Lab[vCount];
      final float toPercent = 1.0f / ( vCount - 1.0f );
      for ( int i = 0; i < vCount; ++i ) {
         final float prc = i * toPercent;
         result[i] = Gradient.eval(grd, ( 1.0f - prc ) * vOrigin + prc * vDest,
            new Lab());
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
    * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
    */
   public static Lab[] evalRange ( final Gradient grd, final int count,
      final float origin, final float dest, final Lab.AbstrEasing easing ) {

      final int vCount = count < 2 ? 2 : count;
      final float vOrigin = Utils.clamp01(origin);
      final float vDest = Utils.clamp01(dest);
      final Lab[] result = new Lab[vCount];
      final float toPercent = 1.0f / ( vCount - 1.0f );
      for ( int i = 0; i < vCount; ++i ) {
         final float prc = i * toPercent;
         result[i] = Gradient.eval(grd, ( 1.0f - prc ) * vOrigin + prc * vDest,
            easing, new Lab());
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
            final Lab srcClr = srcItr.next().clr;
            trgKeys.add(new ColorKey(srcClr.alpha, srcClr));
         }
         srcKeys.clear();
         srcKeys.addAll(trgKeys);

      } else {

         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         final Iterator < ColorKey > srcItr = source.keys.iterator();
         while ( srcItr.hasNext() ) {
            final Lab srcClr = srcItr.next().clr;
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
    */
   public static Gradient keysByLightness ( final Gradient source,
      final Gradient target ) {

      if ( source == target ) {

         final TreeSet < ColorKey > srcKeys = target.keys;
         final ArrayList < ColorKey > trgKeys = new ArrayList <>(srcKeys
            .size());
         final Iterator < ColorKey > srcItr = srcKeys.iterator();
         while ( srcItr.hasNext() ) {
            final Lab srcClr = srcItr.next().clr;
            trgKeys.add(new ColorKey(srcClr.l * 0.01f, srcClr));
         }
         srcKeys.clear();
         srcKeys.addAll(trgKeys);

      } else {

         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         final Iterator < ColorKey > srcItr = source.keys.iterator();
         while ( srcItr.hasNext() ) {
            final Lab srcClr = srcItr.next().clr;
            trgKeys.add(new ColorKey(srcClr.l * 0.01f, srcClr));
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

      keys.add(new ColorKey(0.000000f, 8.613134f, -3.721971f, -14.823075f));
      keys.add(new ColorKey(0.142857f, 21.631306f, -7.210958f, -25.105259f));
      keys.add(new ColorKey(0.285714f, 31.980478f, -10.507741f, -28.090473f));
      keys.add(new ColorKey(0.428571f, 41.108681f, -14.172106f, -26.727585f));

      keys.add(new ColorKey(0.571429f, 52.432716f, -14.872755f, -21.311859f));
      keys.add(new ColorKey(0.714286f, 69.020493f, -13.751400f, -14.435276f));
      keys.add(new ColorKey(0.857143f, 85.554993f, -7.959521f, -5.002866f));
      keys.add(new ColorKey(1.000000f, 99.415680f, -0.054422f, -0.094921f));

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

      keys.add(new ColorKey(0.000000f, 17.207016f, -12.651218f, 0.649666f));
      keys.add(new ColorKey(0.333333f, 40.848148f, -15.700788f, 8.435310f));
      keys.add(new ColorKey(0.666667f, 70.372322f, -28.815866f, 35.844276f));
      keys.add(new ColorKey(1.000000f, 86.723526f, -20.063959f, 26.639313f));

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

      keys.add(new ColorKey(0.000000f, 0.091809f, 0.592775f, -1.907756f));
      keys.add(new ColorKey(0.066667f, 2.769536f, 4.405124f, -11.467472f));
      keys.add(new ColorKey(0.133333f, 6.330423f, 10.966581f, -29.439524f));
      keys.add(new ColorKey(0.200000f, 11.302176f, 21.493853f, -43.331696f));

      keys.add(new ColorKey(0.266667f, 16.834465f, 34.209873f, -44.099174f));
      keys.add(new ColorKey(0.333333f, 22.770105f, 44.663292f, -36.876053f));
      keys.add(new ColorKey(0.400000f, 28.520067f, 54.185238f, -27.044201f));
      keys.add(new ColorKey(0.466667f, 34.554302f, 62.516632f, -14.479840f));

      keys.add(new ColorKey(0.533333f, 40.940407f, 68.791847f, 0.703665f));
      keys.add(new ColorKey(0.600000f, 48.024292f, 70.197952f, 19.177029f));
      keys.add(new ColorKey(0.666667f, 54.783279f, 64.218079f, 34.308880f));
      keys.add(new ColorKey(0.733333f, 62.043285f, 50.232311f, 43.066158f));

      keys.add(new ColorKey(0.800000f, 69.615410f, 33.422905f, 44.987713f));
      keys.add(new ColorKey(0.866667f, 78.234879f, 15.744173f, 43.420521f));
      keys.add(new ColorKey(0.933333f, 87.799080f, 0.195737f, 40.491623f));
      keys.add(new ColorKey(1.000000f, 98.310959f, -13.797366f, 36.818932f));

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

      keys.add(new ColorKey(0.0f, Lab.srRed(new Lab())));
      keys.add(new ColorKey(0.16666667f, Lab.srYellow(new Lab())));
      keys.add(new ColorKey(0.33333334f, Lab.srGreen(new Lab())));
      keys.add(new ColorKey(0.5f, Lab.srCyan(new Lab())));
      keys.add(new ColorKey(0.6666667f, Lab.srBlue(new Lab())));
      keys.add(new ColorKey(0.8333333f, Lab.srMagenta(new Lab())));
      keys.add(new ColorKey(1.0f, Lab.srRed(new Lab())));

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

      keys.add(new ColorKey(0.000000f, 53.225975f, 78.204277f, 67.700623f));
      keys.add(new ColorKey(0.083333f, 63.294071f, 41.628414f, 72.584358f));
      keys.add(new ColorKey(0.166667f, 74.635468f, 9.574734f, 79.463249f));
      keys.add(new ColorKey(0.250000f, 85.266159f, -14.495931f, 86.603149f));
      keys.add(new ColorKey(0.333333f, 97.345261f, -37.154263f, 95.186623f));
      keys.add(new ColorKey(0.416667f, 77.147675f, -55.465858f, 72.887413f));
      keys.add(new ColorKey(0.500000f, 60.259987f, -55.182827f, 48.260632f));
      keys.add(new ColorKey(0.583333f, 48.814213f, -31.473946f, 7.567694f));
      keys.add(new ColorKey(0.666667f, 37.366413f, -9.335724f, -48.161968f));
      keys.add(new ColorKey(0.750000f, 25.478336f, 13.484400f, -55.239174f));
      keys.add(new ColorKey(0.833333f, 26.442522f, 47.946320f, -45.106098f));
      keys.add(new ColorKey(0.916667f, 36.376442f, 74.160706f, 1.910957f));
      keys.add(new ColorKey(1.000000f, 53.225975f, 78.204277f, 67.700623f));

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

      keys.add(new ColorKey(0.000000f, 9.039095f, 1.541970f, 4.551196f));
      keys.add(new ColorKey(0.142857f, 15.049420f, 2.519861f, 6.559138f));
      keys.add(new ColorKey(0.285714f, 35.558064f, 5.560180f, 15.985915f));
      keys.add(new ColorKey(0.428571f, 46.415211f, 5.320652f, 24.526014f));
      keys.add(new ColorKey(0.571429f, 57.325619f, 4.106241f, 28.784594f));
      keys.add(new ColorKey(0.714286f, 69.518433f, 1.687564f, 28.475378f));
      keys.add(new ColorKey(0.857143f, 83.969940f, -3.398645f, 25.784210f));
      keys.add(new ColorKey(1.000000f, 93.835083f, -3.805666f, 24.245275f));

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

      keys.add(new ColorKey(0.000000f, 53.225975f, 78.204277f, 67.700623f));
      keys.add(new ColorKey(0.200000f, 63.974377f, 40.397732f, 71.018539f));
      keys.add(new ColorKey(0.400000f, 79.069832f, 14.975408f, 47.488724f));
      keys.add(new ColorKey(0.600000f, 89.464226f, 6.906213f, 22.329275f));
      keys.add(new ColorKey(0.800000f, 99.999718f, -0.003737f, -0.000495f));
      keys.add(new ColorKey(1.000000f, 83.282845f, 0.112235f, -25.521570f));

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

      keys.add(new ColorKey(0.000000f, 14.838412f, 37.483562f, -32.474392f));
      keys.add(new ColorKey(0.066667f, 20.570141f, 28.441080f, -38.628178f));
      keys.add(new ColorKey(0.133333f, 26.025185f, 17.570065f, -40.752884f));
      keys.add(new ColorKey(0.200000f, 31.498125f, 6.671634f, -38.397491f));

      keys.add(new ColorKey(0.266667f, 36.736752f, -3.131694f, -33.448982f));
      keys.add(new ColorKey(0.333333f, 41.478966f, -10.817966f, -26.788832f));
      keys.add(new ColorKey(0.400000f, 46.456280f, -17.727476f, -19.503687f));
      keys.add(new ColorKey(0.466667f, 51.524525f, -24.320143f, -11.305546f));

      keys.add(new ColorKey(0.533333f, 56.316334f, -30.459932f, -2.642304f));
      keys.add(new ColorKey(0.600000f, 61.361664f, -36.782661f, 8.446353f));
      keys.add(new ColorKey(0.666667f, 66.306084f, -42.510315f, 21.677887f));
      keys.add(new ColorKey(0.733333f, 71.333786f, -46.810425f, 36.986534f));

      keys.add(new ColorKey(0.800000f, 76.373627f, -48.478462f, 54.282032f));
      keys.add(new ColorKey(0.866667f, 81.250488f, -45.616154f, 70.487045f));
      keys.add(new ColorKey(0.933333f, 86.185860f, -37.458225f, 82.004410f));
      keys.add(new ColorKey(1.000000f, 91.094627f, -24.817217f, 86.018288f));

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
