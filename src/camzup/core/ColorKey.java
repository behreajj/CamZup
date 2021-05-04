package camzup.core;

import java.util.Comparator;

/**
 * Stores a color at a given step (or percent) in the range [0.0, 1.0] .
 * Equality and hash are based solely on the step, not on the color it
 * holds.
 */
public class ColorKey implements Comparable < ColorKey > {

   /**
    * The key's color. Abbreviated to 'clr' because 'color' is a data type in
    * Processing IDE.
    */
   public final Color clr = new Color(0.0f, 0.0f, 0.0f, 0.0f);

   /**
    * The key's step, expected to be in the range [0.0, 1.0] .
    */
   public float step = 0.0f;

   /**
    * The default constructor. Creates a clear black color at 0.0 .
    */
   public ColorKey ( ) {}

   /**
    * Creates a key from a source
    *
    * @param source the source key
    */
   public ColorKey ( final ColorKey source ) { this.set(source); }

   /**
    * Creates a key at a given step. All values of the color (including alpha)
    * are set to the step. To prevent confusion between step and color
    * channels, a color key cannot be set with a single step, only
    * constructed.
    *
    * @param step the step
    */
   public ColorKey ( final float step ) {

      final float stcl = Utils.clamp01(step);
      this.set(stcl, stcl, stcl, stcl);
   }

   /**
    * Creates a new key with a step and color.
    *
    * @param step  the step
    * @param color the color
    */
   public ColorKey ( final float step, final Color color ) {

      this.set(step, color);
   }

   /**
    * Creates a new key with a step and a color in hexadecimal.
    *
    * @param step  the step
    * @param color the color
    */
   public ColorKey ( final float step, final int color ) {

      this.set(step, color);
   }

   /**
    * Creates a key by step and color channel. The color's alpha is assumed to
    * be 1.0 . This is for package-level use only, so that the step can be set
    * without clamp protection.
    *
    * @param step  the step
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    */
   ColorKey ( final float step, final float red, final float green,
      final float blue ) {

      this.set(step, red, green, blue);
   }

   /**
    * Creates a key by step and color channel. This is for package-level use
    * only, so that the step can be set without clamp protection.
    *
    * @param step  the step
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    * @param alpha the transparency channel
    */
   ColorKey ( final float step, final float red, final float green,
      final float blue, final float alpha ) {

      this.set(step, red, green, blue, alpha);
   }

   /**
    * Returns -1 when this key is less than the comparisand; 1 when it is
    * greater than; 0 when the two are 'equal'. The implementation of this
    * method allows collections of keys to be sorted.
    *
    * @param key the comparisand
    *
    * @return the numeric code
    */
   @Override
   public int compareTo ( final ColorKey key ) {

      return this.step > key.step ? 1 : this.step < key.step ? -1 : 0;
   }

   /**
    * Tests this key for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    *
    * @see ColorKey#equals(ColorKey)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( ColorKey ) obj);
   }

   /**
    * Returns a hash code for this key based on its step, not based on its
    * color.
    *
    * @return the hash code
    *
    * @see Float#floatToIntBits(float)
    */
   @Override
   public int hashCode ( ) { return Float.floatToIntBits(this.step); }

   /**
    * Resets this key to an initial condition.
    *
    * @return this key
    */
   public ColorKey reset ( ) {

      this.step = 0.0f;
      this.clr.set(0.0f, 0.0f, 0.0f, 0.0f);
      return this;
   }

   /**
    * Sets this key from a source.
    *
    * @param source the source key
    *
    * @return this key
    */
   public ColorKey set ( final ColorKey source ) {

      return this.set(source.step, source.clr);
   }

   /**
    * Sets this key with a step and color.
    *
    * @param step  the step
    * @param color the color
    *
    * @return this key
    */
   public ColorKey set ( final float step, final Color color ) {

      this.step = Utils.clamp01(step);
      this.clr.set(color);
      return this;
   }

   /**
    * Sets this key with a step and a color in hexadecimal.
    *
    * @param step  the step
    * @param color the color
    *
    * @return this key
    */
   public ColorKey set ( final float step, final int color ) {

      this.step = Utils.clamp01(step);
      Color.fromHex(color, this.clr);
      return this;
   }

   /**
    * Returns a string representation of this key.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

   /**
    * Returns a string representation of this key.
    *
    * @param places number of decimal places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      return this.toString(new StringBuilder(96), places).toString();
   }

   /**
    * Sets this key by step and color channel. The color's alpha is assumed to
    * be 1.0 . This is for package-level use only, so that the step can be set
    * without clamp protection.
    *
    * @param step  the step
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    *
    * @return this key
    */
   ColorKey set ( final float step, final float red, final float green,
      final float blue ) {

      this.step = step;
      this.clr.set(red, green, blue);

      return this;
   }

   /**
    * Sets this key by step and color channel. This is for package-level use
    * only, so that the step can be set without clamp protection.
    *
    * @param step  the step
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    * @param alpha the transparency channel
    *
    * @return this key
    */
   ColorKey set ( final float step, final float red, final float green,
      final float blue, final float alpha ) {

      this.step = step;
      this.clr.set(red, green, blue, alpha);

      return this;
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes.
    *
    * @param pyCd  the string builder
    * @param gamma the gamma adjustment
    *
    * @return the string builder
    */
   @Experimental
   StringBuilder toBlenderCode ( final StringBuilder pyCd, final float gamma ) {

      pyCd.append("{\"position\": ");
      Utils.toFixed(pyCd, Utils.clamp01(this.step), 3);
      pyCd.append(", \"color\": ");
      this.clr.toBlenderCode(pyCd, gamma, true);
      pyCd.append('}');
      return pyCd;
   }

   /**
    * Internal helper function to assist with methods that need to print many
    * color keys. Appends to an existing {@link StringBuilder}.
    *
    * @param sb     the string builder
    * @param places the number of places
    *
    * @return the string builder
    */
   StringBuilder toString ( final StringBuilder sb, final int places ) {

      sb.append("{ step: ");
      Utils.toFixed(sb, this.step, places);
      sb.append(", clr: ");
      this.clr.toString(sb, places);
      sb.append(' ');
      sb.append('}');
      return sb;
   }

   /**
    * Returns a String representation of this color stop for an SVG gradient.
    * Includes the offset, color and opacity.
    *
    * @return the string
    */
   StringBuilder toSvgString ( final StringBuilder svgp ) {

      svgp.append("<stop offset=\"");
      Utils.toFixed(svgp, this.step, ISvgWritable.FIXED_PRINT);
      svgp.append("\" stop-opacity=\"");
      Utils.toFixed(svgp, Utils.clamp01(this.clr.a), ISvgWritable.FIXED_PRINT);
      svgp.append("\" stop-color=\"");
      Color.toHexWeb(svgp, this.clr);
      svgp.append("\" />");
      return svgp;
   }

   /**
    * Tests this key for equality to another based on its step, not color.
    *
    * @param key the key
    *
    * @return the evaluation
    */
   protected boolean equals ( final ColorKey key ) {

      return this.step == key.step;
   }

   /**
    * The default comparator used to compare color keys.
    */
   public static class SortQuantized implements Comparator < ColorKey > {

      /**
       * Quantization level.
       */
      public final int level;

      /**
       * The default constructor.
       */
      SortQuantized ( ) { this(SortQuantized.DEFAULT_LEVEL); }

      /**
       * Creates a quantized sorter with the specified number of levels.
       *
       * @param level quantization levels
       */
      SortQuantized ( final int level ) {

         this.level = level < 2 ? 2 : level;
      }

      /**
       * Compares the quantized steps of the comparisand keys.
       *
       * @param a the left comparisand
       * @param b the right comparisand
       *
       * @return the evaluation
       */
      @Override
      public int compare ( final ColorKey a, final ColorKey b ) {

         final float aq = Utils.quantize(a.step, this.level);
         final float bq = Utils.quantize(b.step, this.level);
         return aq > bq ? 1 : aq < bq ? -1 : 0;
      }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

      /**
       * Default quantization factor for color key steps,
       * {@value SortQuantized#DEFAULT_LEVEL}.
       */
      public static final int DEFAULT_LEVEL = 16;

   }

}
