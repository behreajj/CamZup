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
   public final Color clr;

   /**
    * The key's step, expected to be in the range [0.0, 1.0] .
    */
   public float step = 0.0f;

   {
      this.clr = new Color(0.0f, 0.0f, 0.0f, 0.0f);
   }

   /**
    * The default constructor. Creates a clear black color at 0.0 .
    */
   public ColorKey ( ) { this.set(0.0f, 0.0f, 0.0f, 0.0f, 0.0f); }

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
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of this key.
    *
    * @param places number of decimal places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(96);
      sb.append("{ step: ");
      sb.append(Utils.toFixed(this.step, places));
      sb.append(", clr: ");
      sb.append(this.clr.toString(places));
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Returns a String representation of this color stop for an SVG gradient.
    * Includes the offset, color and opacity.
    *
    * @return the string
    */
   public String toSvgString ( ) {

      final StringBuilder svgp = new StringBuilder(96);
      svgp.append("<stop offset=\"");
      svgp.append(Utils.toFixed(this.step, 6));
      svgp.append("\" stop-color=\"");
      svgp.append(Color.toHexWeb(this.clr));
      svgp.append("\" stop-opacity=\"");
      svgp.append(Utils.toFixed(this.clr.a, 6));
      svgp.append("\"/>");
      return svgp.toString();
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
    * @param gamma the gamma adjustment
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode ( final float gamma ) {

      final StringBuilder pyCd = new StringBuilder(256);
      pyCd.append("{\"position\": ");
      pyCd.append(Utils.toFixed(Utils.clamp01(this.step), 3));
      pyCd.append(", \"color\": ");
      pyCd.append(this.clr.toBlenderCode(gamma, true));
      pyCd.append('}');
      return pyCd.toString();
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
