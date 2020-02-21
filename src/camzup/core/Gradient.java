package camzup.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * A mutable, extensible class that contains a list of keys
 * which hold colors at steps in the range [0.0, 1.0] .
 * Allows smooth color transitions to be evaluated by a
 * factor.
 */
public class Gradient implements IUtils, Cloneable, Iterable < ColorKey > {

   /**
    * The default easing function, lerp RGBA.
    */
   private static Color.AbstrEasing EASING = new Color.LerpRgba();

   /**
    * A helper function for parsing an OBJ file. Attempts to
    * convert a string to an integer.
    *
    * @param i
    *           the string
    * @return the integer
    */
   private static int intFromStr ( final String i ) {

      int target = 0;
      try {
         target = Integer.parseInt(i);
      } catch (final NumberFormatException e) {
         target = 0;
      }
      return target;
   }

   // @Experimental
   // public static Gradient fromGgr (
   // final String[] lines,
   // final Gradient target) {
   //
   // }

   /**
    * Creates a gradient from an array of strings representing
    * a GIMP color palette (.gpl) .
    *
    * @param lines
    *           the String tokens
    * @param target
    *           the output gradient
    * @return the gradient
    */
   @Experimental
   public static Gradient fromGpl (
         final String[] lines,
         final Gradient target ) {

      target.keys.clear();

      final int len = lines.length;
      String[] tokens;
      final ArrayList < Color > clrs = new ArrayList <>();

      for (int i = 0; i < len; ++i) {
         final String line = lines[i].trim().toLowerCase();
         if (line.equals("gimp palette")) {
         } else if (line.contains("name:")) {
         } else if (line.contains("columns:")) {
         } else if (line.indexOf('#') == 0) {
         } else {
            tokens = line.split("\\s+");
            if (tokens.length > 2) {
               final int ri = Gradient.intFromStr(tokens[0]);
               final int gi = Gradient.intFromStr(tokens[1]);
               final int bi = Gradient.intFromStr(tokens[2]);
               final Color clr = new Color(
                     ri * IUtils.ONE_255,
                     gi * IUtils.ONE_255,
                     bi * IUtils.ONE_255,
                     1.0f);
               clrs.add(clr);
            }
         }
      }

      target.appendAll(clrs);
      return target;
   }

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
    * Inverts all colors in the gradient and reverses the
    * gradient.
    *
    * @param source
    *           the source gradient
    * @param target
    *           the target gradient
    * @return the inverse
    */
   public static Gradient inverse (
         final Gradient source,
         final Gradient target ) {

      if (source == target) {
         final Iterator < ColorKey > kyItr = source.keys.iterator();
         while (kyItr.hasNext()) {
            final Color clr = kyItr.next().clr;
            Color.inverse(clr, clr);
         }
      } else {
         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         final Iterator < ColorKey > srcItr = source.keys.iterator();
         while (srcItr.hasNext()) {
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
    * Returns the Magma color palette, consisting of 16 keys.
    *
    * @param target
    *           the output gradient
    * @return the gradient
    */
   public static Gradient paletteMagma ( final Gradient target ) {

      final TreeSet < ColorKey > keys = target.keys;
      keys.clear();

      keys.add(new ColorKey(0.0f, 0.988235f, 1.0f, 0.698039f));
      keys.add(new ColorKey(0.06666667f, 0.98719f, 0.843137f, 0.562092f));
      keys.add(new ColorKey(0.13333333f, 0.984314f, 0.694118f, 0.446275f));
      keys.add(new ColorKey(0.2f, 0.981176f, 0.548235f, 0.35451f));

      keys.add(new ColorKey(0.26666667f, 0.962353f, 0.412549f, 0.301176f));
      keys.add(new ColorKey(0.33333333f, 0.912418f, 0.286275f, 0.298039f));
      keys.add(new ColorKey(0.4f, 0.824314f, 0.198431f, 0.334902f));
      keys.add(new ColorKey(0.46666667f, 0.703268f, 0.142484f, 0.383007f));

      keys.add(new ColorKey(0.53333333f, 0.584052f, 0.110588f, 0.413856f));
      keys.add(new ColorKey(0.6f, 0.471373f, 0.080784f, 0.430588f));
      keys.add(new ColorKey(0.66666667f, 0.36732f, 0.045752f, 0.43268f));
      keys.add(new ColorKey(0.73333333f, 0.267974f, 0.002353f, 0.416732f));

      keys.add(new ColorKey(0.8f, 0.174118f, 0.006275f, 0.357647f));
      keys.add(new ColorKey(0.86666667f, 0.093856f, 0.036863f, 0.232941f));
      keys.add(new ColorKey(0.93333333f, 0.040784f, 0.028758f, 0.110327f));
      keys.add(new ColorKey(1.0f, 0.0f, 0.0f, 0.019608f));

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

      final TreeSet < ColorKey > keys = target.keys;
      keys.clear();

      keys.add(new ColorKey(0.0f, 1.0f, 0.0f, 0.0f));
      keys.add(new ColorKey(0.16666667f, 1.0f, 1.0f, 0.0f));
      keys.add(new ColorKey(0.33333333f, 0.0f, 1.0f, 0.0f));
      keys.add(new ColorKey(0.5f, 0.0f, 1.0f, 1.0f));
      keys.add(new ColorKey(0.66666667f, 0.0f, 0.0f, 1.0f));
      keys.add(new ColorKey(0.83333333f, 1.0f, 0.0f, 1.0f));
      keys.add(new ColorKey(1.0f, 1.0f, 0.0f, 0.0f));

      return target;
   }

   /**
    * Returns thirteen colors in the red yellow blue color
    * wheel. Red is repeated so that the gradient is periodic.
    *
    * @param target
    *           the output gradient
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
    * Returns a heavily stylized approximation of color
    * temperature, where the middle key (0.5) is white at 6500
    * Kelvin. The lower bound (0.0), black, is at 800 Kelvin;
    * the upper bound (1.0) is a blue-ish white at 12000
    * Kelvin.
    *
    * @param target
    *           the output gradient
    * @return the gradient
    */
   @Experimental
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
    * @param target
    *           the output gradient
    * @return the gradient
    */
   public static Gradient paletteViridis ( final Gradient target ) {

      final TreeSet < ColorKey > keys = target.keys;
      keys.clear();

      keys.add(new ColorKey(0.0f, 0.266667f, 0.003922f, 0.329412f));
      keys.add(new ColorKey(0.06666667f, 0.282353f, 0.100131f, 0.420654f));
      keys.add(new ColorKey(0.13333333f, 0.276078f, 0.184575f, 0.487582f));
      keys.add(new ColorKey(0.2f, 0.254902f, 0.265882f, 0.527843f));

      keys.add(new ColorKey(0.26666667f, 0.221961f, 0.340654f, 0.549281f));
      keys.add(new ColorKey(0.33333333f, 0.192157f, 0.405229f, 0.554248f));
      keys.add(new ColorKey(0.4f, 0.164706f, 0.469804f, 0.556863f));
      keys.add(new ColorKey(0.46666667f, 0.139869f, 0.534379f, 0.553464f));

      keys.add(new ColorKey(0.53333333f, 0.122092f, 0.595033f, 0.543007f));
      keys.add(new ColorKey(0.6f, 0.139608f, 0.658039f, 0.516863f));
      keys.add(new ColorKey(0.66666667f, 0.210458f, 0.717647f, 0.471895f));
      keys.add(new ColorKey(0.73333333f, 0.326797f, 0.773595f, 0.407582f));

      keys.add(new ColorKey(0.8f, 0.477647f, 0.821961f, 0.316863f));
      keys.add(new ColorKey(0.86666667f, 0.648366f, 0.858039f, 0.208889f));
      keys.add(new ColorKey(0.93333333f, 0.825098f, 0.884967f, 0.114771f));
      keys.add(new ColorKey(1.0f, 0.992157f, 0.905882f, 0.145098f));

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
    * Shifts the brightness of all colors in a gradient. The
    * brightness is clamped to the range [0.0, 1.0] . The
    * source gradient may either be the same as or different
    * from the target.
    *
    * @param source
    *           the source gradient
    * @param shift
    *           the brightness shift
    * @param target
    *           the output gradient
    * @param hsba
    *           the color in HSB
    * @return the shifted ramp
    */
   public static Gradient shiftBri (
         final Gradient source,
         final float shift,
         final Gradient target,
         final Vec4 hsba ) {

      if (source == target) {
         final Iterator < ColorKey > kyItr = source.keys.iterator();
         while (kyItr.hasNext()) {
            final Color clr = kyItr.next().clr;
            Color.shiftBri(clr, shift, clr, hsba);
         }
      } else {
         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         final Iterator < ColorKey > srcItr = source.keys.iterator();
         while (srcItr.hasNext()) {
            final ColorKey trgKey = new ColorKey(srcItr.next());
            final Color clr = trgKey.clr;
            Color.shiftBri(clr, shift, clr, hsba);
            trgKeys.add(trgKey);
         }
      }

      return target;
   }

   /**
    * Shifts the hue, saturation and brightness in a gradient.
    * The alpha remains unaffected.
    *
    * @param source
    *           the input gradient
    * @param shift
    *           the shift
    * @param target
    *           the output gradient
    * @param hsba
    *           the color in HSB
    * @return the shifted ramp
    */
   public static Gradient shiftHsb (
         final Gradient source,
         final Vec4 shift,
         final Gradient target,
         final Vec4 hsba ) {

      if (source == target) {
         final Iterator < ColorKey > kyItr = source.keys.iterator();
         while (kyItr.hasNext()) {
            final Color clr = kyItr.next().clr;
            Color.shiftHsb(clr, shift, clr, hsba);
         }
      } else {
         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         final Iterator < ColorKey > srcItr = source.keys.iterator();
         while (srcItr.hasNext()) {
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
    * @param source
    *           the input gradient
    * @param shift
    *           the shift
    * @param target
    *           the output gradient
    * @param hsba
    *           the color in HSB
    * @return the shifted ramp
    */
   public static Gradient shiftHsba (
         final Gradient source,
         final Vec4 shift,
         final Gradient target,
         final Vec4 hsba ) {

      if (source == target) {
         final Iterator < ColorKey > kyItr = source.keys.iterator();
         while (kyItr.hasNext()) {
            final Color clr = kyItr.next().clr;
            Color.shiftHsba(clr, shift, clr, hsba);
         }
      } else {
         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         final Iterator < ColorKey > srcItr = source.keys.iterator();
         while (srcItr.hasNext()) {
            final ColorKey trgKey = new ColorKey(srcItr.next());
            final Color clr = trgKey.clr;
            Color.shiftHsba(clr, shift, clr, hsba);
            trgKeys.add(trgKey);
         }
      }

      return target;
   }

   /**
    * Shifts the hue of all colors in a gradient. The hue wraps
    * around the range [0.0, 1.0] . The source gradient may
    * either be the same as or different from the target.
    *
    * @param source
    *           the source gradient
    * @param shift
    *           the hue shift
    * @param target
    *           the output gradient
    * @param hsba
    *           the color in HSB
    * @return the shifted ramp
    */
   public static Gradient shiftHue (
         final Gradient source,
         final float shift,
         final Gradient target,
         final Vec4 hsba ) {

      if (source == target) {
         final Iterator < ColorKey > kyItr = source.keys.iterator();
         while (kyItr.hasNext()) {
            final Color clr = kyItr.next().clr;
            Color.shiftHue(clr, shift, clr, hsba);
         }
      } else {
         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         final Iterator < ColorKey > srcItr = source.keys.iterator();
         while (srcItr.hasNext()) {
            final ColorKey trgKey = new ColorKey(srcItr.next());
            final Color clr = trgKey.clr;
            Color.shiftHue(clr, shift, clr, hsba);
            trgKeys.add(trgKey);
         }
      }

      return target;
   }

   /**
    * Shifts the saturation of all colors in a gradient. The
    * saturation is clamped to the range [0.0, 1.0] . The
    * source gradient may either be the same as or different
    * from the target.
    *
    * @param source
    *           the source gradient
    * @param shift
    *           the saturation shift
    * @param target
    *           the output gradient
    * @param hsba
    *           the color in HSB
    * @return the shifted ramp
    */
   public static Gradient shiftSat (
         final Gradient source,
         final float shift,
         final Gradient target,
         final Vec4 hsba ) {

      if (source == target) {
         final Iterator < ColorKey > kyItr = source.keys.iterator();
         while (kyItr.hasNext()) {
            final Color clr = kyItr.next().clr;
            Color.shiftSat(clr, shift, clr, hsba);
         }
      } else {
         final TreeSet < ColorKey > trgKeys = target.keys;
         trgKeys.clear();
         final Iterator < ColorKey > srcItr = source.keys.iterator();
         while (srcItr.hasNext()) {
            final ColorKey trgKey = new ColorKey(srcItr.next());
            final Color clr = trgKey.clr;
            Color.shiftSat(clr, shift, clr, hsba);
            trgKeys.add(trgKey);
         }
      }

      return target;
   }

   /**
    * A temporary variable to hold queries in evaluation
    * functions.
    */
   protected final ColorKey query = new ColorKey();

   /**
    * The set of keys.
    */
   public final TreeSet < ColorKey > keys = new TreeSet <>();

   /**
    * Creates a gradient with two default color keys, clear
    * black at 0.0 and opaque white at 1.0.
    *
    * @see Color#clearBlack(Color)
    * @see Color#white(Color)
    */
   public Gradient () {

      this.keys.add(new ColorKey(0.0f, 0.0f, 0.0f, 0.0f, 0.0f));
      this.keys.add(new ColorKey(1.0f, 1.0f, 1.0f, 1.0f, 1.0f));
   }

   /**
    * Creates a gradient from a collection of color integers;
    * the resultant keys are evenly distributed over the range
    * [0.0, 1.0].
    *
    * @param colors
    *           the colors
    */
   public Gradient ( final Collection < Color > colors ) {

      this.appendAll(colors);
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

      this.keys.add(new ColorKey(0.0f, 1.0f, 1.0f, 1.0f, 1.0f));
      this.keys.add(new ColorKey(1.0f, color));
   }

   /**
    * Creates a gradient from a list of colors; the resultant
    * keys are evenly distributed over the range [0.0, 1.0].
    *
    * @param colors
    *           the colors
    */
   public Gradient ( final Color... colors ) {

      this.appendAll(colors);
   }

   /**
    * Creates a gradient from color keys.
    *
    * @param keys
    *           the color keys
    */
   public Gradient ( final ColorKey... keys ) {

      this.insertAll(keys);
   }

   /**
    * Creates a gradient from a list of scalars; the resultant
    * keys are evenly distributed over the range [0.0, 1.0].
    *
    * @param scalars
    *           the scalars
    */
   public Gradient ( final float... scalars ) {

      this.appendAll(scalars);
   }

   /**
    * Constructs a copy of a source gradient.
    *
    * @param source
    *           the source
    */
   public Gradient ( final Gradient source ) {

      this.set(source);
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

      this.keys.add(new ColorKey(0.0f, 1.0f, 1.0f, 1.0f, 1.0f));
      this.keys.add(new ColorKey(1.0f, color));
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

      this.appendAll(colors);
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

      this.keys.add(new ColorKey(0.0f, 1.0f, 1.0f, 1.0f, 1.0f));
      this.keys.add(new ColorKey(1.0f, color));
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
    * Helper function that shifts existing keys to the left
    * when a new color is added to the gradient without a key.
    *
    * @param added
    *           number of new items
    * @return this gradient
    */
   @Chainable
   protected Gradient shiftKeysLeft ( final int added ) {

      int i = 0;
      final Iterator < ColorKey > itr = this.keys.iterator();
      final float scalar = 1.0f / (this.keys.size() + added - 1.0f);
      while (itr.hasNext()) {
         final ColorKey key = itr.next();
         key.step = key.step * i++ * scalar;
      }
      return this;
   }

   /**
    * Helper function that shifts existing keys to the right
    * when a new color is added to the gradient without a key.
    *
    * @param added
    *           number of new items
    * @return this gradient
    */
   @Chainable
   protected Gradient shiftKeysRight ( final int added ) {

      /*
       * A simplification of Utils.map(key.step, 0.0f, 1.0f,
       * scalar, 1.0f);
       */
      final Iterator < ColorKey > itr = this.keys.iterator();
      final float scalar = added / (this.keys.size() + added - 1.0f);
      final float coeff = 1.0f - scalar;
      while (itr.hasNext()) {
         final ColorKey key = itr.next();
         key.step = scalar + coeff * key.step;
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
   public Gradient append ( final Color color ) {

      this.shiftKeysLeft(1);
      this.keys.add(new ColorKey(1.0f, color));
      return this;
   }

   /**
    * Appends a scalar at step 1.0 . Shifts existing keys to
    * the left.
    *
    * @param scalar
    *           the scalar
    * @return this gradient
    * @see Gradient#shiftKeysLeft(int)
    * @see TreeSet#add(Object)
    */
   @Chainable
   public Gradient append ( final float scalar ) {

      this.shiftKeysLeft(1);
      this.keys.add(new ColorKey(1.0f, scalar, scalar, scalar, scalar));
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
      this.keys.add(new ColorKey(1.0f, color));
      return this;
   }

   /**
    * Appends a collection of colors to this gradient. Shifts
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
   public Gradient appendAll ( final Collection < Color > colors ) {

      final int len = colors.size();
      this.shiftKeysLeft(len);
      final int oldLen = this.keys.size();
      final float denom = 1.0f / (oldLen + len - 1.0f);

      int i = 0;
      final Iterator < Color > clrItr = colors.iterator();
      while (clrItr.hasNext()) {
         this.keys.add(
               new ColorKey(
                     (oldLen + i) * denom,
                     clrItr.next()));
         i++;
      }

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
   public Gradient appendAll ( final Color... colors ) {

      final int len = colors.length;
      this.shiftKeysLeft(len);
      final int oldLen = this.keys.size();
      final float denom = 1.0f / (oldLen + len - 1.0f);

      for (int i = 0; i < len; ++i) {
         this.keys.add(
               new ColorKey(
                     (oldLen + i) * denom,
                     colors[i]));
      }

      return this;
   }

   /**
    * Appends a list of scalars to this gradient. Shifts
    * existing keys to the left.
    *
    * @param scalars
    *           the scalars
    * @return the gradient
    * @see Gradient#shiftKeysLeft(int)
    * @see TreeSet#size()
    * @see TreeSet#add(Object)
    */
   @Chainable
   public Gradient appendAll ( final float... scalars ) {

      final int len = scalars.length;
      this.shiftKeysLeft(len);
      final int oldLen = this.keys.size();
      final float denom = 1.0f / (oldLen + len - 1.0f);

      for (int i = 0; i < len; ++i) {
         final float scalar = scalars[i];
         this.keys.add(
               new ColorKey(
                     (oldLen + i) * denom,
                     scalar, scalar, scalar, scalar));
      }

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
   public Gradient appendAll ( final int... colors ) {

      final int len = colors.length;
      this.shiftKeysLeft(len);
      final int oldLen = this.keys.size();
      final float denom = 1.0f / (oldLen + len - 1.0f);

      for (int i = 0; i < len; ++i) {
         this.keys.add(
               new ColorKey(
                     (oldLen + i) * denom,
                     colors[i]));
      }

      return this;
   }

   /**
    * Creates a clone of this gradient
    *
    * @return the clone
    */
   @Override
   public Gradient clone () {

      return new Gradient(this);
   }

   /**
    * Checks to see if this gradient contains a specified key.
    *
    * @param key
    *           the key
    * @return the evaluation
    */
   public boolean contains ( final ColorKey key ) {

      return this.keys.contains(key);
   }

   /**
    * Distributes this gradient's color keys evenly through the
    * range [0.0, 1.0] .
    *
    * @return this gradient
    *
    * @see List#addAll(java.util.Collection)
    */
   public Gradient distribute () {

      final ArrayList < ColorKey > keyArr = new ArrayList <>();
      keyArr.addAll(this.keys);

      this.keys.clear();
      int i = 0;
      final Iterator < ColorKey > itr = keyArr.iterator();
      final float denom = 1.0f / (keyArr.size() - 1.0f);
      while (itr.hasNext()) {
         final ColorKey key = itr.next();
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
    * Finds a color given a step in the range [0.0, 1.0] . When
    * the step falls between color keys, the resultant color is
    * created by an easing function.
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
    * Finds a color given a step in the range [0.0, 1.0]. When
    * the step falls between color keys, the resultant color is
    * generated by an easing function.
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

      final ColorKey prev = this.keys.floor(this.query);
      if (prev == null) {
         return target.set(this.keys.first().clr);
      }

      final ColorKey next = this.keys.ceiling(this.query);
      if (next == null) {
         return target.set(this.keys.last().clr);
      }

      /*
       * This needs to be Utils.div to avoid returning 0x0 as a
       * color.
       */
      return easing.applyUnclamped(
            next.clr, prev.clr,
            Utils.div(step - next.step,
                  prev.step - next.step),
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
    * Evaluates an array of colors given a supplied count. The
    * minimum count is three.
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

      final int vCount = count < 2 ? 2 : count;
      final Color[] result = new Color[vCount];
      final float toPercent = 1.0f / (vCount - 1.0f);
      for (int i = 0; i < vCount; ++i) {
         result[i] = this.eval(
               i * toPercent, easing, new Color());
      }
      return result;

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
   public ColorKey findGe ( final float step ) {

      this.query.step = step;
      final ColorKey result = this.keys.ceiling(this.query);
      return result == null ? this.keys.last() : result;
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
   public ColorKey getFirst () {

      return this.keys.first();
   }

   /**
    * Retrieves the last key in this gradient.
    *
    * @return the last key
    */
   public ColorKey getLast () {

      return this.keys.last();
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
    * Inserts a color key to this gradient. If a color key
    * exists at the insertion key's step, the old key is
    * removed.
    *
    * @param key
    *           the key
    * @return this gradient
    * @see TreeSet#add(Object)
    * @see TreeSet#remove(Object)
    */
   @Chainable
   public Gradient insert ( final ColorKey key ) {

      if (this.keys.contains(key)) {
         this.keys.remove(key);
      }
      this.keys.add(key);
      return this;
   }

   /**
    * Inserts color keys to this gradient. If a color key
    * exists at the insertion's step, the old key is removed.
    *
    * @param keys
    *           the keys
    * @return this gradient
    * @see TreeSet#add(Object)
    * @see TreeSet#remove(Object)
    */
   @Chainable
   public Gradient insertAll ( final ColorKey... keys ) {

      final int len = keys.length;
      for (int i = 0; i < len; ++i) {
         final ColorKey key = keys[i];
         if (this.keys.contains(key)) {
            this.keys.remove(key);
         }
         this.keys.add(key);
      }

      return this;
   }

   /**
    * Returns an iterator for this gradient, which allows its
    * components to be accessed in an enhanced for-loop.
    *
    * @return the iterator
    * @see TreeSet#iterator()
    */
   @Override
   public Iterator < ColorKey > iterator () {

      return this.keys.iterator();
   }

   /**
    * Returns the number of keys in this gradient.
    *
    * @return the key count
    * @see TreeSet#size()
    */
   public int length () {

      return this.keys.size();
   }

   /**
    * Prepends a color at step 0.0 . Shifts existing keys to
    * the right.
    *
    * @param color
    *           the color
    * @return this gradient
    * @see Gradient#shiftKeysRight(int)
    * @see TreeSet#add(Object)
    */
   @Chainable
   public Gradient prepend ( final Color color ) {

      this.shiftKeysRight(1);
      this.keys.add(new ColorKey(0.0f, color));
      return this;
   }

   /**
    * Prepends a scalar at step 0.0 . Shifts existing keys to
    * the right.
    *
    * @param scalar
    *           the scalar
    * @return this gradient
    * @see Gradient#shiftKeysRight(int)
    * @see TreeSet#add(Object)
    */
   @Chainable
   public Gradient prepend ( final float scalar ) {

      this.shiftKeysRight(1);
      this.keys.add(new ColorKey(0.0f, scalar, scalar, scalar, scalar));
      return this;
   }

   /**
    * Prepends a color at step 0.0 . Shifts existing keys to
    * the right.
    *
    * @param color
    *           the color
    * @return this gradient
    * @see Gradient#shiftKeysRight(int)
    * @see TreeSet#add(Object)
    */
   @Chainable
   public Gradient prepend ( final int color ) {

      this.shiftKeysRight(1);
      this.keys.add(new ColorKey(0.0f, color));
      return this;
   }

   /**
    * Prepends a collection of colors to this gradient. Shifts
    * existing keys to the right.
    *
    * @param colors
    *           the colors
    * @return this gradient
    * @see Gradient#shiftKeysRight(int)
    * @see TreeSet#size()
    * @see TreeSet#add(Object)
    */
   public Gradient prependAll ( final Collection < Color > colors ) {

      final int len = colors.size();
      this.shiftKeysRight(len);
      final float denom = 1.0f / (this.keys.size() + len - 1.0f);

      int i = 0;
      final Iterator < Color > clrItr = colors.iterator();
      while (clrItr.hasNext()) {
         this.keys.add(
               new ColorKey(
                     i * denom,
                     clrItr.next()));
         i++;
      }

      return this;
   }

   /**
    * Prepends a list of colors to this gradient. Shifts
    * existing keys to the right.
    *
    * @param colors
    *           the colors
    * @return this gradient
    * @see Gradient#shiftKeysRight(int)
    * @see TreeSet#size()
    * @see TreeSet#add(Object)
    */
   @Chainable
   public Gradient prependAll ( final Color... colors ) {

      final int len = colors.length;
      this.shiftKeysRight(len);
      final float denom = 1.0f / (this.keys.size() + len - 1.0f);

      for (int i = 0; i < len; ++i) {
         this.keys.add(
               new ColorKey(
                     i * denom,
                     colors[i]));
      }

      return this;
   }

   /**
    * Prepends a list of scalars to this gradient. Shifts
    * existing keys to the right.
    *
    * @param scalars
    *           the scalars
    * @return this gradient
    * @see Gradient#shiftKeysRight(int)
    * @see TreeSet#size()
    * @see TreeSet#add(Object)
    */
   @Chainable
   public Gradient prependAll ( final float... scalars ) {

      final int len = scalars.length;
      this.shiftKeysRight(len);
      final float denom = 1.0f / (this.keys.size() + len - 1.0f);

      for (int i = 0; i < len; ++i) {
         final float scalar = scalars[i];
         this.keys.add(
               new ColorKey(
                     i * denom,
                     scalar, scalar, scalar, scalar));
      }

      return this;
   }

   /**
    * Prepends a list of color integers to this gradient.
    * Shifts existing keys to the right.
    *
    * @param colors
    *           the colors
    * @return this gradient
    * @see Gradient#shiftKeysRight(int)
    * @see TreeSet#size()
    * @see TreeSet#add(Object)
    */
   @Chainable
   public Gradient prependAll ( final int... colors ) {

      final int len = colors.length;
      this.shiftKeysRight(len);
      final float denom = 1.0f / (this.keys.size() + len - 1.0f);

      for (int i = 0; i < len; ++i) {
         this.keys.add(
               new ColorKey(
                     i * denom,
                     colors[i]));
      }

      return this;
   }

   /**
    * Removes a key from the gradient. Returns true if
    * successful.
    *
    * @param key
    *           the key
    * @return the success
    */
   public boolean remove ( final ColorKey key ) {

      return this.keys.remove(key);
   }

   /**
    * Removes the first key from the gradient.
    *
    * @return the key
    * @see TreeSet#pollFirst()
    */
   public ColorKey removeFirst () {

      return this.keys.pollFirst();
   }

   /**
    * Removes the last key from the gradient.
    *
    * @return the key
    * @see TreeSet#pollLast()
    */
   public ColorKey removeLast () {

      return this.keys.pollLast();
   }

   /**
    * Resets this gradient to an initial state, with two color
    * keys: clear black at 0.0 and opaque white at 1.0 .
    *
    * @return this gradient
    * @see Color#clearBlack(Color)
    * @see Color#white(Color)
    * @see TreeSet#clear()
    * @see TreeSet#add(Object)
    */
   public Gradient reset () {

      this.keys.clear();
      this.keys.add(new ColorKey(0.0f, 0.0f, 0.0f, 0.0f, 0.0f));
      this.keys.add(new ColorKey(1.0f, 1.0f, 1.0f, 1.0f, 1.0f));

      return this;
   }

   /**
    * Reverses the gradient. The step of each color key is
    * subtracted from one. Does so with a temporary List.
    *
    * @return the gradient
    * @see List#clear()
    * @see List#addAll(java.util.Collection)
    * @see TreeSet#addAll(java.util.Collection)
    * @see TreeSet#clear()
    * @see Collections#reverse(java.util.List)
    */
   public Gradient reverse () {

      final ArrayList < ColorKey > keyList = new ArrayList <>();
      keyList.addAll(this.keys);
      Collections.reverse(keyList);

      this.keys.clear();
      final Iterator < ColorKey > itr = keyList.iterator();
      while (itr.hasNext()) {
         final ColorKey key = itr.next();
         key.step = 1.0f - key.step;
      }
      this.keys.addAll(keyList);

      return this;
   }

   /**
    * Copies data from a source gradient.
    *
    * @param source
    *           the source
    * @return this gradient
    */
   public Gradient set ( final Gradient source ) {

      this.keys.clear();
      final Iterator < ColorKey > srcItr = source.keys.iterator();
      while (srcItr.hasNext()) {
         this.keys.add(new ColorKey(srcItr.next()));
      }
      return this;
   }

   /**
    * Sorts the gradient according to a property of the colors
    * in each key. Does so with a temporary List.
    *
    * @return the gradient
    */
   public Gradient sort () {

      return this.sort(null);
   }

   /**
    * Sorts the gradient according to a property of the colors
    * in each key. Does so with a temporary List.
    *
    * @param sorter
    *           the sorting function
    * @return the gradient
    */
   public Gradient sort ( final Comparator < Color > sorter ) {

      /*
       * Separate color keys into a an array of steps and of
       * colors. The key steps should remain unaffected by the
       * sort.
       */
      final int len = this.keys.size();
      final float[] steps = new float[len];
      final ArrayList < Color > clrList = new ArrayList <>(len);

      int j = 0;
      final Iterator < ColorKey > keyItr = this.keys.iterator();
      while (keyItr.hasNext()) {
         final ColorKey key = keyItr.next();
         steps[j++] = key.step;
         clrList.add(key.clr);
      }

      /*
       * Clear out the gradient's key set now that the separation
       * is complete.
       */
      this.keys.clear();

      /* Sort the colors. */
      Collections.sort(clrList, sorter);

      /* Reconstitute the keys from the steps and colors. */
      int i = 0;
      final Iterator < Color > clrItr = clrList.iterator();
      while (clrItr.hasNext()) {
         this.keys.add(new ColorKey(steps[i++], clrItr.next()));
      }

      return this;
   }

   /**
    * Returns a String of Python code targeted toward the
    * Blender 2.8x API. This code is brittle and is used for
    * internal testing purposes.
    *
    * @param name
    *           the material's name
    * @return the string
    */
   public String toBlenderCode ( final String name ) {

      return this.toBlenderCode(name, this.keys.size());
   }

   /**
    * Returns a String of Python code targeted toward the
    * Blender 2.8x API. This code is brittle and is used for
    * internal testing purposes.
    *
    * @param name
    *           the material's name
    * @param samples
    *           number of gradient samples
    * @return the string
    */
   public String toBlenderCode ( final String name, final int samples ) {

      return this.toBlenderCode(name, samples, 1.0f);
   }

   /**
    * Returns a String of Python code targeted toward the
    * Blender 2.8x API. This code is brittle and is used for
    * internal testing purposes.
    *
    * @param name
    *           the material's name
    * @param samples
    *           number of gradient samples
    * @param gamma
    *           the gamma adjustment
    * @return the string
    */
   @Experimental
   public String toBlenderCode (
         final String name,
         final int samples,
         final float gamma ) {

      /*
       * Blender gradients may contain a max of 32 color keys.
       * While they may contain a minimum of only 1, they default
       * to 2 keys when created.
       */
      
      final Color[] clrs = this.evalRange(Utils.clamp(samples, 2, 32));
      final int len = clrs.length;
      final int last = len - 1;
      final float toPercent = 1.0f / last;

      final StringBuilder result = new StringBuilder()
            .append("from bpy import data as D, context as C\n\n")
            .append("grd_data = [");

      for (int i = 0; i < len; ++i) {
         final Color clr = clrs[i];
         result.append("\n    {\"position\": ")
               .append(Utils.toFixed(i * toPercent, 6))
               .append(", \"color\": ")
               .append(clr.toBlenderCode(gamma))
               .append('}');

         if (i < last) {
            result.append(',').append(' ');
         }
      }
      result.append(']');

      result.append("\n\nmaterial = D.materials.new(\"")
            .append(name)
            .append("\")\n")
            .append("material.use_nodes = True\n")
            .append("mat_node_tree = material.node_tree\n")
            .append("mat_nodes = mat_node_tree.nodes\n")
            .append("clr_rmp_node = mat_nodes.new(\"ShaderNodeValToRGB\")\n")
            .append("clr_rmp_data = clr_rmp_node.color_ramp\n")
            .append("color_keys = clr_rmp_data.elements\n\n")
            .append("color_keys[0].position = grd_data[0][\"position\"]\n")
            .append("color_keys[0].color = grd_data[0][\"color\"]\n\n")
            .append("color_keys[1].position = grd_data[1][\"position\"]\n")
            .append("color_keys[1].color = grd_data[1][\"color\"]\n\n")
            .append("i_itr = range(2, len(grd_data))\n")
            .append("for i in i_itr:")
            .append("\n    datum = grd_data[i]")
            .append("\n    new_key = color_keys.new(datum[\"position\"])")
            .append("\n    new_key.color = datum[\"color\"]\n");
      return result.toString();
   }

   /**
    * Returns a String representation of the gradient
    * compatible with .ggr (Gimp gradient) file formats.
    *
    * @return the string
    */
   public String toGgrString () {

      return this.toGgrString(this.hashIdentityString());
   }

   /**
    * Returns a String representation of the gradient
    * compatible with .ggr (Gimp gradient) file formats.
    *
    * @param name
    *           the name
    * @return the string
    */
   @Experimental
   public String toGgrString ( final String name ) {

      /*
       * GIMP gradients are slightly more sophisticated than this
       * gradient. The entire span of [0.0, 1.0] must be covered
       * by the keys. Each key has a left edge (column 1), center
       * point (column 2) and right edge (column 3). A color is
       * located on the left edge; its red, green, blue and alpha
       * channels are columns 4, 5, 6 and 7. Another is located on
       * the right edge; columns 8, 9, 10 and 11.
       */

      // TODO: Look up what two integers in last columns of line
      // mean.

      final ColorKey first = this.keys.first();
      final float firstStep = first.step;
      final Color firstColor = first.clr;
      final boolean nonZeroFirst = firstStep > 0.0f;

      final ColorKey last = this.keys.last();
      final float lastStep = last.step;
      final Color lastColor = last.clr;
      final boolean nonOneLast = lastStep < 1.0f;

      int len = this.keys.size() - 1;
      if (nonZeroFirst) {
         len++;
      }
      if (nonOneLast) {
         len++;
      }

      final StringBuilder sb = new StringBuilder()
            .append("GIMP Gradient\n")
            .append("Name: ")
            .append(name)
            .append('\n')
            .append(len)
            .append('\n');

      if (nonZeroFirst) {
         final String frstClrStr = firstColor.toGgrString();
         sb.append("0.000000 ")
               .append(Utils.toFixed(firstStep * 0.5f, 6))
               .append(' ')
               .append(Utils.toFixed(firstStep, 6))
               .append(' ')
               .append(frstClrStr)
               .append(' ')
               .append(frstClrStr)
               .append(" 0 0\n");
      }

      final Iterator < ColorKey > itr = this.keys.iterator();

      ColorKey prev = itr.next();
      float prevStep = prev.step;
      Color prevColor = prev.clr;

      ColorKey curr = null;
      float currStep = 0.0f;
      Color currColor = null;

      while (itr.hasNext()) {
         curr = itr.next();
         currStep = curr.step;
         currColor = curr.clr;

         final float midStep = (currStep + prevStep) * 0.5f;
         sb.append(Utils.toFixed(prevStep, 6))
               .append(' ')
               .append(Utils.toFixed(midStep, 6))
               .append(' ')
               .append(Utils.toFixed(currStep, 6))
               .append(' ')
               .append(prevColor.toGgrString())
               .append(' ')
               .append(currColor.toGgrString())
               .append(" 0 0\n");

         prev = curr;
         prevStep = currStep;
         prevColor = currColor;
      }

      if (nonOneLast) {
         final String lastClrStr = lastColor.toGgrString();
         sb.append(Utils.toFixed(lastStep, 6))
               .append(' ')
               .append(Utils.toFixed((1.0f + lastStep) * 0.5f, 6))
               .append(" 1.000000 ")
               .append(lastClrStr)
               .append(' ')
               .append(lastClrStr)
               .append(" 0 0");
      }

      return sb.toString();
   }

   /**
    * Returns a String representation of the gradient
    * compatible with .gpl (Gimp palette) file formats.
    *
    * @return the string
    */
   public String toGplString () {

      return this.toGplString(this.hashIdentityString(), 0);
   }

   /**
    * Returns a String representation of the gradient
    * compatible with .gpl (Gimp palette) file formats.
    *
    * @param name
    *           palette name
    * @return the string
    */
   public String toGplString ( final String name ) {

      return this.toGplString(name, 0);
   }

   /**
    * Returns a String representation of the gradient
    * compatible with .gpl (Gimp palette) file formats.
    *
    * @param name
    *           palette name
    * @param displayColumns
    *           display columns
    * @return the string
    */
   @Experimental
   public String toGplString (
         final String name,
         final int displayColumns ) {

      final StringBuilder sb = new StringBuilder();

      sb.append("GIMP Palette\n")
            .append("Name: ")
            .append(name)
            .append('\n')
            .append("Columns: ")
            .append(displayColumns)
            .append('\n')
            .append("# https://github.com/behreajj/CamZup \n");

      final Iterator < ColorKey > itr = this.keys.iterator();
      while (itr.hasNext()) {
         sb.append(itr.next().clr.toGplString()).append('\n');
      }

      return sb.toString();
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

      final StringBuilder sb = new StringBuilder(
            16 + 128 * this.keys.size())
                  .append("{ keys: [ \n");
      final Iterator < ColorKey > itr = this.keys.iterator();
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
