package camzup.core;

import java.util.Comparator;
import java.util.Iterator;

/**
 * A mutable, extensible color class. Supports RGBA and HSBA color spaces.
 * Supports conversion to and from integers where color channels are in the
 * format 0xAARRGGBB (Java).
 */
public class Color implements Comparable < Color >, Cloneable, Iterable <
   Float > {

   /**
    * The alpha channel (opacity).
    */
   public float a = 1.0f;

   /**
    * The blue channel.
    */
   public float b = 1.0f;

   /**
    * The green channel.
    */
   public float g = 1.0f;

   /**
    * The red channel.
    */
   public float r = 1.0f;

   /**
    * The default constructor. Creates a white color.
    */
   public Color ( ) {}

   /**
    * Creates a color from bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    */
   public Color ( final byte red, final byte green, final byte blue ) {

      this.set(red, green, blue);
   }

   /**
    * Creates a color from bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    * @param alpha the alpha channel
    */
   public Color ( final byte red, final byte green, final byte blue,
      final byte alpha ) {

      this.set(red, green, blue, alpha);
   }

   /**
    * Creates a color from a source.
    *
    * @param c the source color
    */
   public Color ( final Color c ) {

      this.set(c);
   }

   /**
    * Creates a color out of red, green and blue channels. The alpha channel
    * defaults to 1.0 .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    */
   public Color ( final float red, final float green, final float blue ) {

      this.set(red, green, blue, 1.0f);
   }

   /**
    * Creates a color out of red, green, blue and alpha channels.
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    * @param alpha the alpha channel
    */
   public Color ( final float red, final float green, final float blue,
      final float alpha ) {

      this.set(red, green, blue, alpha);
   }

   /**
    * Attempts to construct a color from Strings using
    * {@link Float#parseFloat(String)} . If a NumberFormatException is thrown,
    * the component is set to zero. If a NumberFormatException is thrown, the
    * component is set to zero for red, green and blue; to one for alpha.
    *
    * @param rstr the red string
    * @param gstr the green string
    * @param bstr the blue string
    */
   public Color ( final String rstr, final String gstr, final String bstr ) {

      this.set(rstr, gstr, bstr);
   }

   /**
    * Attempts to construct a color from Strings using
    * {@link Float#parseFloat(String)} . If a NumberFormatException is thrown,
    * the component is set to zero. If a NumberFormatException is thrown, the
    * component is set to zero for red, green and blue; to one for alpha.
    *
    * @param rstr the red string
    * @param gstr the green string
    * @param bstr the blue string
    * @param astr the alpha string
    */
   public Color ( final String rstr, final String gstr, final String bstr,
      final String astr ) {

      this.set(rstr, gstr, bstr, astr);
   }

   /**
    * Returns a new color with this color's components. Java's cloneable
    * interface is problematic; use set or a copy constructor instead.
    *
    * @return a new color
    *
    * @see Color#set(Color)
    * @see Color#Color(Color)
    */
   @Override
   public Color clone ( ) { return new Color(this.r, this.g, this.b, this.a); }

   /**
    * Returns -1 when this color is less than the comparisand; 1 when it is
    * greater than; 0 when the two are 'equal'. The implementation of this
    * method allows collections of colors to be sorted.
    *
    * @param c the comparisand
    *
    * @return the numeric code
    */
   @Override
   public int compareTo ( final Color c ) {

      final int left = Color.toHexInt(this);
      final int right = Color.toHexInt(c);
      return left > right ? 1 : left < right ? -1 : 0;
   }

   /**
    * Tests to see if the vector contains a value.
    *
    * @param v the value
    *
    * @return the evaluation
    */
   public boolean contains ( final float v ) {

      if ( Utils.approx(this.a, v) ) { return true; }
      if ( Utils.approx(this.b, v) ) { return true; }
      if ( Utils.approx(this.g, v) ) { return true; }
      if ( Utils.approx(this.r, v) ) { return true; }
      return false;
   }

   /**
    * Returns a new color decremented by {@link IUtils#ONE_255},
    * {@value IUtils#ONE_255}. For interoperability with Kotlin:
    * <code>--a</code> (prefix) or <code>a--</code> (postfix). Per the
    * specification, <em>does not mutate the color in place</em>.
    *
    * @return the decremented vector
    *
    * @see Color#sub(Color, float, Color)
    */
   public Color dec ( ) {

      return Color.sub(this, IUtils.ONE_255, new Color());
   }

   /**
    * Returns a new color with the division of the instance by the right
    * operand. For interoperability with Kotlin: <code>a / b</code> . <em>Does
    * not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the quotient
    *
    * @see Color#div(Color, Color, Color)
    */
   public Color div ( final Color c ) {

      return Color.div(this, c, new Color());
   }

   /**
    * Returns a new color with the division of the instance by the right
    * operand. For interoperability with Kotlin: <code>a / b</code> . <em>Does
    * not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the quotient
    *
    * @see Color#div(Color, float, Color)
    */
   public Color div ( final float c ) {

      return Color.div(this, c, new Color());
   }

   /**
    * Divides the instance by the right operand (mutates the color in place).
    * For interoperability with Kotlin: <code>a /= b</code> .
    *
    * @param c the right operand
    *
    * @see Color#div(Color, Color, Color)
    */
   public void divAssign ( final Color c ) { Color.div(this, c, this); }

   /**
    * Divides the instance by the right operand (mutates the color in place).
    * For interoperability with Kotlin: <code>a /= b</code> .
    *
    * @param c the right operand
    *
    * @see Color#div(Color, float, Color)
    */
   public void divAssign ( final float c ) { Color.div(this, c, this); }

   /**
    * Tests this color for equivalence to another based on its hexadecimal
    * representation.
    *
    * @param other the color integer
    *
    * @return the equivalence
    *
    * @see Color#toHexInt(Color)
    */
   public boolean equals ( final int other ) {

      return Color.toHexInt(this) == other;
   }

   /**
    * Tests this color for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    *
    * @see Color#equals(Color)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Color ) obj);
   }

   /**
    * Simulates bracket subscript access in an array.
    *
    * @param index the index
    *
    * @return the element
    */
   public float get ( final int index ) { return this.getAlphaLast(index); }

   /**
    * Simulates bracket access in an array. The alpha channel is treated as
    * the first channel.
    *
    * @param index the index
    *
    * @return the element
    */
   public float getAlphaFirst ( final int index ) {

      switch ( index ) {
         case 0:
         case -4:
            return this.a;

         case 1:
         case -3:
            return this.r;

         case 2:
         case -2:
            return this.g;

         case 3:
         case -1:
            return this.b;

         default:
            return 0.0f;
      }
   }

   /**
    * Simulates bracket access in an array. The alpha channel is treated as
    * the last channel.
    *
    * @param index the index
    *
    * @return the element
    */
   public float getAlphaLast ( final int index ) {

      switch ( index ) {
         case 0:
         case -4:
            return this.r;

         case 1:
         case -3:
            return this.g;

         case 2:
         case -2:
            return this.b;

         case 3:
         case -1:
            return this.a;

         default:
            return 0.0f;
      }
   }

   /**
    * Returns a hash code for this color based on its hexadecimal value.
    *
    * @return the hash code
    *
    * @see Float#floatToIntBits(float)
    */
   @Override
   public int hashCode ( ) { return Color.toHexInt(this); }

   /**
    * Returns a new color incremented by {@link IUtils#ONE_255},
    * {@value IUtils#ONE_255}. For interoperability with Kotlin:
    * <code>++a</code> (prefix) or <code>a++</code> (postfix). Per the
    * specification, <em>does not mutate the color in place</em>.
    *
    * @return the incremented color
    *
    * @see Color#add(Color, float, Color)
    */
   public Color inc ( ) {

      return Color.add(this, IUtils.ONE_255, new Color());
   }

   /**
    * Returns an iterator for this color, which allows its components to be
    * accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public Iterator < Float > iterator ( ) { return new ClrIterator(this); }

   /**
    * Gets the number of components held by this color.
    *
    * @return the length
    */
   public int length ( ) { return 4; }

   /**
    * Returns a new color with the subtraction of the right operand from the
    * instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the subtraction
    *
    * @see Color#sub(Color, Color, Color)
    */
   public Color minus ( final Color c ) {

      return Color.sub(this, c, new Color());
   }

   /**
    * Returns a new color with the subtraction of the right operand from the
    * instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the subtraction
    *
    * @see Color#sub(Color, Color, Color)
    */
   public Color minus ( final float c ) {

      return Color.sub(this, c, new Color());
   }

   /**
    * Subtracts the right operand from the instance (mutates the vector in
    * place). For interoperability with Kotlin: <code>a -= b</code> .
    *
    * @param c the right operand
    *
    * @see Color#sub(Color, Color, Color)
    */
   public void minusAssign ( final Color c ) { Color.sub(this, c, this); }

   /**
    * Subtracts the right operand from the instance (mutates the vector in
    * place). For interoperability with Kotlin: <code>a -= b</code> .
    *
    * @param c the right operand
    *
    * @see Color#sub(Color, float, Color)
    */
   public void minusAssign ( final float c ) { Color.sub(this, c, this); }

   /**
    * Returns a new color with the addition of the right operand to the
    * instance. For interoperability with Kotlin: <code>a + b</code> .
    * <em>Does not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the subtraction
    *
    * @see Color#add(Color, Color, Color)
    */
   public Color plus ( final Color c ) {

      return Color.add(this, c, new Color());
   }

   /**
    * Returns a new color with the addition of the right operand to the
    * instance. For interoperability with Kotlin: <code>a + b</code> .
    * <em>Does not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the subtraction
    *
    * @see Color#add(Color, float, Color)
    */
   public Color plus ( final float c ) {

      return Color.add(this, c, new Color());
   }

   /**
    * Adds the right operand to the instance (mutates the vector in place).
    * For interoperability with Kotlin: <code>a += b</code> .
    *
    * @param c the right operand
    *
    * @see Color#add(Color, Color, Color)
    */
   public void plusAssign ( final Color c ) { Color.add(this, c, this); }

   /**
    * Adds the right operand to the instance (mutates the vector in place).
    * For interoperability with Kotlin: <code>a += b</code> .
    *
    * @param c the right operand
    *
    * @see Color#add(Color, float, Color)
    */
   public void plusAssign ( final float c ) { Color.add(this, c, this); }

   /**
    * Returns a new color with the unsigned remainder (<code>mod</code>) of
    * the instance and the right operand. For interoperability with Kotlin:
    * <code>a % b</code> . <em>Does not mutate the color in place</em>.
    *
    * @param right the right operand
    *
    * @return the signed remainder
    *
    * @see Color#mod(Color, Color, Color)
    */
   public Color rem ( final Color right ) {

      return Color.mod(this, right, new Color());
   }

   /**
    * Returns a new color with the unsigned remainder (<code>mod</code>) of
    * the instance and the right operand. For interoperability with Kotlin:
    * <code>a % b</code> . <em>Does not mutate the color in place</em>.
    *
    * @param right the right operand
    *
    * @return the signed remainder
    *
    * @see Color#mod(Color, float, Color)
    */
   public Color rem ( final float right ) {

      return Color.mod(this, right, new Color());
   }

   /**
    * Assigns the unsigned remainder (<code>mod</code>) of the instance and
    * the right operand to the instance (mutates the vector in place). For
    * interoperability with Kotlin: <code>a %= b</code> .
    *
    * @param right the right operand
    *
    * @see Color#mod(Color, Color, Color)
    */
   public void remAssign ( final Color right ) {

      Color.mod(this, right, this);
   }

   /**
    * Assigns the unsigned remainder (<code>mod</code>) of the instance and
    * the right operand to the instance (mutates the vector in place). For
    * interoperability with Kotlin: <code>a %= b</code> .
    *
    * @param right the right operand
    *
    * @see Color#mod(Color, Color, Color)
    */
   public void remAssign ( final float right ) {

      Color.mod(this, right, this);
   }

   /**
    * Resets this color to the color white.
    *
    * @return this color
    *
    * @see Color#white(Color)
    */
   public Color reset ( ) { return this.set(1.0f, 1.0f, 1.0f, 1.0f); }

   /**
    * Sets a color with bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    *
    * @return this color
    */
   public Color set ( final byte red, final byte green, final byte blue ) {

      return this.set(IUtils.ONE_255 * ( red & 0xff ), IUtils.ONE_255 * ( green
         & 0xff ), IUtils.ONE_255 * ( blue & 0xff ), 1.0f);
   }

   /**
    * Sets a color with bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    * @param alpha the alpha channel
    *
    * @return this color
    */
   public Color set ( final byte red, final byte green, final byte blue,
      final byte alpha ) {

      return this.set(IUtils.ONE_255 * ( red & 0xff ), IUtils.ONE_255 * ( green
         & 0xff ), IUtils.ONE_255 * ( blue & 0xff ), IUtils.ONE_255 * ( alpha
            & 0xff ));
   }

   /**
    * Sets this color to the source color.
    *
    * @param c the source color
    *
    * @return this color
    */
   public Color set ( final Color c ) {

      return this.set(c.r, c.g, c.b, c.a);
   }

   /**
    * Sets the red, green and blue color channels of this color. The alpha
    * channel is set to 1.0 by default.
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    *
    * @return this color
    */
   public Color set ( final float red, final float green, final float blue ) {

      return this.set(red, green, blue, 1.0f);
   }

   /**
    * Overrides the parent set function for the sake of making RGB parameters
    * clearer and for chainability.
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    * @param alpha the alpha channel
    *
    * @return this color
    */
   public Color set ( final float red, final float green, final float blue,
      final float alpha ) {

      this.r = red;
      this.g = green;
      this.b = blue;
      this.a = alpha;
      return this;
   }

   /**
    * Attempts to set the components of this color from Strings using
    * {@link Float#parseFloat(String)} . If a NumberFormatException is thrown,
    * the component is set to zero for red, green and blue; to one for alpha.
    *
    * @param rstr the red string
    * @param gstr the green string
    * @param bstr the blue string
    *
    * @return this color
    *
    * @see Float#parseFloat(String)
    */
   public Color set ( final String rstr, final String gstr,
      final String bstr ) {

      return this.set(rstr, gstr, bstr, "1.0");
   }

   /**
    * Attempts to set the components of this color from Strings using
    * {@link Float#parseFloat(String)} . If a NumberFormatException is thrown,
    * the component is set to zero for red, green and blue; to one for alpha.
    *
    * @param rstr the red string
    * @param gstr the green string
    * @param bstr the blue string
    * @param astr the alpha string
    *
    * @return this color
    *
    * @see Float#parseFloat(String)
    */
   public Color set ( final String rstr, final String gstr, final String bstr,
      final String astr ) {

      float xprs = 1.0f;
      float yprs = 1.0f;
      float zprs = 1.0f;
      float wprs = 1.0f;

      try {
         xprs = Float.parseFloat(rstr);
      } catch ( final Exception e ) {
         xprs = 1.0f;
      }

      try {
         yprs = Float.parseFloat(gstr);
      } catch ( final Exception e ) {
         yprs = 1.0f;
      }

      try {
         zprs = Float.parseFloat(bstr);
      } catch ( final Exception e ) {
         zprs = 1.0f;
      }

      try {
         wprs = Float.parseFloat(astr);
      } catch ( final Exception e ) {
         wprs = 1.0f;
      }

      this.r = xprs;
      this.g = yprs;
      this.b = zprs;
      this.a = wprs;

      return this;
   }

   /**
    * Returns a new color with the product of the instance and the right
    * operand. For interoperability with Kotlin: <code>a * b</code> . <em>Does
    * not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the product
    *
    * @see Color#mul(Color, Color, Color)
    */
   public Color times ( final Color c ) {

      return Color.mul(this, c, new Color());
   }

   /**
    * Returns a new color with the product of the instance and the right
    * operand. For interoperability with Kotlin: <code>a * b</code> . <em>Does
    * not mutate the color in place</em>.
    *
    * @param c the right operand
    *
    * @return the product
    *
    * @see Color#mul(Color, Color, Color)
    */
   public Color times ( final float c ) {

      return Color.mul(this, c, new Color());
   }

   /**
    * Multiplies the right operand with the instance (mutates the color in
    * place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param c the right operand
    *
    * @see Color#mul(Color, float, Color)
    */
   public void timesAssign ( final Color c ) { Color.mul(this, c, this); }

   /**
    * Multiplies the right operand with the instance (mutates the color in
    * place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param c the right operand
    *
    * @see Color#mul(Color, Color, Color)
    */
   public void timesAssign ( final float c ) { Color.mul(this, c, this); }

   /**
    * Returns a float array of length 4 containing this color's components.
    * Defaults to {@link Color.ChannelOrder#ARGB}.
    *
    * @return the array
    */
   public float[] toArray ( ) {

      return this.toArray(ChannelOrder.ARGB);
   }

   /**
    * Puts the colors's components into an existing array at the index
    * provided. Consumes four elements, but the ordering depends on the
    * {@link Color.ChannelOrder}.
    *
    * @param arr   the array
    * @param i     the index
    * @param order the channel order
    *
    * @return the array
    */
   public byte[] toArray ( final byte[] arr, final int i,
      final ChannelOrder order ) {

      final byte rb = ( byte ) ( this.r * 0xff + 0.5f );
      final byte gb = ( byte ) ( this.g * 0xff + 0.5f );
      final byte bb = ( byte ) ( this.b * 0xff + 0.5f );
      final byte ab = ( byte ) ( this.a * 0xff + 0.5f );

      switch ( order ) {

         case ABGR:

            arr[i] = ab;
            arr[i + 1] = bb;
            arr[i + 2] = gb;
            arr[i + 3] = rb;

            break;

         case ARGB:
            arr[i] = ab;
            arr[i + 1] = rb;
            arr[i + 2] = gb;
            arr[i + 3] = bb;

            break;

         case RGBA:

         default:

            arr[i] = rb;
            arr[i + 1] = gb;
            arr[i + 2] = bb;
            arr[i + 3] = ab;

      }

      return arr;
   }

   /**
    * Returns a float array of length 4 containing this color's components.
    *
    * @param order the channel order
    *
    * @return the array
    */
   public float[] toArray ( final ChannelOrder order ) {

      return this.toArray(new float[4], 0, order);
   }

   /**
    * Puts the colors's components into an existing array at the index
    * provided. Consumes four elements, but the ordering depends on the
    * {@link Color.ChannelOrder}.
    *
    * @param arr   the array
    * @param i     the index
    * @param order the channel order
    *
    * @return the array
    */
   public float[] toArray ( final float[] arr, final int i,
      final ChannelOrder order ) {

      switch ( order ) {

         case ABGR:

            arr[i] = this.a;
            arr[i + 1] = this.b;
            arr[i + 2] = this.g;
            arr[i + 3] = this.r;

            break;

         case ARGB:
            arr[i] = this.a;
            arr[i + 1] = this.r;
            arr[i + 2] = this.g;
            arr[i + 3] = this.b;

            break;

         case RGBA:

         default:

            arr[i] = this.r;
            arr[i + 1] = this.g;
            arr[i + 2] = this.b;
            arr[i + 3] = this.a;

      }

      return arr;
   }

   /**
    * Returns a string representation of this color.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of this color.
    *
    * @param places number of decimal places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(96);
      sb.append("{ r: ");
      sb.append(Utils.toFixed(this.r, places));
      sb.append(", g: ");
      sb.append(Utils.toFixed(this.g, places));
      sb.append(", b: ");
      sb.append(Utils.toFixed(this.b, places));
      sb.append(", a: ");
      sb.append(Utils.toFixed(this.a, places));
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Returns a new color with the positive copy of the instance. For
    * interoperability with Kotlin: <code>+a</code> . <em>Does not mutate the
    * color in place</em>.
    *
    * @return the positive
    */
   public Color unaryPlus ( ) {

      return new Color(+this.r, +this.g, +this.b, +this.a);
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes, i.e., to
    * compare how curve geometry looks in Blender (the control) versus in the
    * library (the test).<br>
    * <br>
    * This is formatted as a tuple where red, green and blue channels have
    * been raised to the power of gamma (usually 2.2, Blender's default sRGB
    * color management setting). If include alpha is true, then the alpha is
    * also included.
    *
    * @param gamma     the exponent
    * @param inclAlpha include the alpha channel
    *
    * @return the string
    */
   String toBlenderCode ( final float gamma, final boolean inclAlpha ) {

      final StringBuilder pyCd = new StringBuilder(96);
      pyCd.append('(');
      pyCd.append(Utils.toFixed(Utils.pow(this.r, gamma), 6));
      pyCd.append(',');
      pyCd.append(' ');
      pyCd.append(Utils.toFixed(Utils.pow(this.g, gamma), 6));
      pyCd.append(',');
      pyCd.append(' ');
      pyCd.append(Utils.toFixed(Utils.pow(this.b, gamma), 6));

      if ( inclAlpha ) {
         pyCd.append(',');
         pyCd.append(' ');
         pyCd.append(Utils.toFixed(this.a, 6));
      }

      pyCd.append(')');
      return pyCd.toString();
   }

   /**
    * Returns a String representation of the color compatible with .ggr (GIMP
    * gradient) file formats. Each channel, including alpha, is represented as
    * a float in [0.0, 1.0] separated by a space.
    *
    * @return the string
    */
   String toGgrString ( ) {

      final StringBuilder ggr = new StringBuilder(96);
      ggr.append(Utils.toFixed(this.r, 6));
      ggr.append(' ');
      ggr.append(Utils.toFixed(this.g, 6));
      ggr.append(' ');
      ggr.append(Utils.toFixed(this.b, 6));
      ggr.append(' ');
      ggr.append(Utils.toFixed(this.a, 6));
      return ggr.toString();
   }

   /**
    * Returns a String representation of the color compatible with .gpl (GIMP
    * palette) file formats. Each channel, including alpha, is represented an
    * unsigned byte in [0, 255] separated by a space.
    *
    * @return the string
    */
   String toGplString ( ) {

      final StringBuilder gpl = new StringBuilder(32);
      gpl.append(( int ) ( this.r * 0xff + 0.5f ));
      gpl.append(' ');
      gpl.append(( int ) ( this.g * 0xff + 0.5f ));
      gpl.append(' ');
      gpl.append(( int ) ( this.b * 0xff + 0.5f ));
      return gpl.toString();
   }

   /**
    * Tests equivalence between this and another color. Converts both to
    * hexadecimal integers.
    *
    * @param c the color
    *
    * @return the evaluation
    *
    * @see Color#toHexInt(Color)
    */
   protected boolean equals ( final Color c ) {

      return Color.toHexInt(this) == Color.toHexInt(c);
   }

   /**
    * Adds the left and right operand, except for the alpha channel, then
    * clamps the sum to [0.0, 1.0] . The left operand's alpha channel is
    * retained. For that reason, color addition is <em>not</em> commutative.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target output color
    *
    * @return the sum
    */
   public static Color add ( final Color a, final Color b,
      final Color target ) {

      return target.set(Utils.clamp01(a.r + b.r), Utils.clamp01(a.g + b.g),
         Utils.clamp01(a.b + b.b), Utils.clamp01(a.a));
   }

   /**
    * Tests to see if all color channels are greater than zero.
    *
    * @param c the color
    *
    * @return the evaluation
    */
   public static boolean all ( final Color c ) {

      return c.a > 0.0f && c.r > 0.0f && c.g > 0.0f && c.b > 0.0f;
   }

   /**
    * Tests to see if the alpha channel of the color is greater than zero,
    * i.e. if it has some opacity.
    *
    * @param c the color
    *
    * @return the evaluation
    */
   public static boolean any ( final Color c ) { return c.a > 0.0f; }

   /**
    * Converts two colors to integers, performs the bitwise and operation on
    * them, then converts the result to a color.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output color
    *
    * @return the result
    *
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitAnd ( final Color a, final Color b,
      final Color target ) {

      // return Color.fromHex(Color.toHexInt(a) & Color.toHexInt(b), target);

      final int cr = ( int ) ( a.r * 0xff + 0.5f ) & ( int ) ( b.r * 0xff
         + 0.5f );
      final int cg = ( int ) ( a.g * 0xff + 0.5f ) & ( int ) ( b.g * 0xff
         + 0.5f );
      final int cb = ( int ) ( a.b * 0xff + 0.5f ) & ( int ) ( b.b * 0xff
         + 0.5f );
      final int ca = ( int ) ( a.a * 0xff + 0.5f ) & ( int ) ( b.a * 0xff
         + 0.5f );

      return target.set(cr * IUtils.ONE_255, cg * IUtils.ONE_255, cb
         * IUtils.ONE_255, ca * IUtils.ONE_255);
   }

   /**
    * Converts a color to an integer, performs the bitwise not operation on
    * it, then converts the result to a color.
    *
    * @param a      the input color
    * @param target the output color
    *
    * @return the negation
    *
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitNot ( final Color a, final Color target ) {

      // return Color.fromHex(~Color.toHexInt(a), target);

      final int cr = ~( int ) ( a.r * 0xff + 0.5f ) & 0xff;
      final int cg = ~( int ) ( a.g * 0xff + 0.5f ) & 0xff;
      final int cb = ~( int ) ( a.b * 0xff + 0.5f ) & 0xff;
      final int ca = ~( int ) ( a.a * 0xff + 0.5f ) & 0xff;

      return target.set(cr * IUtils.ONE_255, cg * IUtils.ONE_255, cb
         * IUtils.ONE_255, ca * IUtils.ONE_255);
   }

   /**
    * Converts two colors to integers, performs the bitwise inclusive or
    * operation on them, then converts the result to a color.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output color
    *
    * @return the color
    *
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitOr ( final Color a, final Color b,
      final Color target ) {

      // return Color.fromHex(Color.toHexInt(a) | Color.toHexInt(b), target);

      final int cr = ( int ) ( a.r * 0xff + 0.5f ) | ( int ) ( b.r * 0xff
         + 0.5f );
      final int cg = ( int ) ( a.g * 0xff + 0.5f ) | ( int ) ( b.g * 0xff
         + 0.5f );
      final int cb = ( int ) ( a.b * 0xff + 0.5f ) | ( int ) ( b.b * 0xff
         + 0.5f );
      final int ca = ( int ) ( a.a * 0xff + 0.5f ) | ( int ) ( b.a * 0xff
         + 0.5f );

      return target.set(cr * IUtils.ONE_255, cg * IUtils.ONE_255, cb
         * IUtils.ONE_255, ca * IUtils.ONE_255);
   }

   /**
    * Converts a color to an integer, rotates to the left by the number of
    * places, then converts the result to a color. The rotate a whole color
    * channel, use increments of 8 (8, 16, 24).
    *
    * @param a      the color
    * @param places the number of places
    * @param target the output color
    *
    * @return the rotated color
    */
   public static Color bitRotateLeft ( final Color a, final int places,
      final Color target ) {

      final int i = Color.toHexInt(a);
      return Color.fromHex(i << places | i >>> -places, target);
   }

   /**
    * Converts a color to an integer, rotates to the right by the number of
    * places, then converts the result to a color. The rotate a whole color
    * channel, use increments of 8 (8, 16, 24).
    *
    * @param a      the color
    * @param places the number of places
    * @param target the output color
    *
    * @return the rotated color
    */
   public static Color bitRotateRight ( final Color a, final int places,
      final Color target ) {

      final int i = Color.toHexInt(a);
      return Color.fromHex(i >>> places | i << -places, target);
   }

   /**
    * Converts a color to an integer, performs a bitwise left shift operation,
    * then converts the result to a color. To shift a whole color channel, use
    * increments of 8 (8, 16, 24).
    *
    * @param a      the color
    * @param places the number of places
    * @param target the output color
    *
    * @return the shifted color
    *
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitShiftLeft ( final Color a, final int places,
      final Color target ) {

      return Color.fromHex(Color.toHexInt(a) << places, target);
   }

   /**
    * Converts a color to an integer, performs a bitwise right shift
    * operation, then converts the result to a color. To shift a whole color
    * channel, use increments of 8 (8, 16, 24).
    *
    * @param a      the color
    * @param places the number of places
    * @param target the output color
    *
    * @return the shifted color
    *
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitShiftRight ( final Color a, final int places,
      final Color target ) {

      return Color.fromHex(Color.toHexInt(a) >> places, target);
   }

   /**
    * Converts a color to an integer, performs an unsigned bitwise right shift
    * operation, then converts the result to a color. To shift a whole color
    * channel, use increments of 8 (8, 16, 24).
    *
    * @param a      the color
    * @param places the number of places
    * @param target the output color
    *
    * @return the shifted color
    *
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitShiftRightUnsigned ( final Color a, final int places,
      final Color target ) {

      return Color.fromHex(Color.toHexInt(a) >>> places, target);
   }

   /**
    * Converts two colors to integers, performs the bitwise exclusive or
    * operation on them, then converts the result to a color.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output color
    *
    * @return the color
    *
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitXor ( final Color a, final Color b,
      final Color target ) {

      // return Color.fromHex(Color.toHexInt(a) ^ Color.toHexInt(b), target);

      final int cr = ( int ) ( a.r * 0xff + 0.5f ) ^ ( int ) ( b.r * 0xff
         + 0.5f );
      final int cg = ( int ) ( a.g * 0xff + 0.5f ) ^ ( int ) ( b.g * 0xff
         + 0.5f );
      final int cb = ( int ) ( a.b * 0xff + 0.5f ) ^ ( int ) ( b.b * 0xff
         + 0.5f );
      final int ca = ( int ) ( a.a * 0xff + 0.5f ) ^ ( int ) ( b.a * 0xff
         + 0.5f );

      return target.set(cr * IUtils.ONE_255, cg * IUtils.ONE_255, cb
         * IUtils.ONE_255, ca * IUtils.ONE_255);
   }

   /**
    * Returns the color black, ( 0.0, 0.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return black
    */
   public static Color black ( final Color target ) {

      return target.set(0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Returns the color blue, ( 0.0, 0.0, 1.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return blue
    */
   public static Color blue ( final Color target ) {

      return target.set(0.0f, 0.0f, 1.0f, 1.0f);
   }

   /**
    * Clamps a color to a lower and upper bound.
    *
    * @param a          the input color
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    * @param target     the output color
    *
    * @return the clamped color
    *
    * @see Utils#clamp(float, float, float)
    */
   public static Color clamp ( final Color a, final Color lowerBound,
      final Color upperBound, final Color target ) {

      return target.set(Utils.clamp(a.r, lowerBound.r, upperBound.r), Utils
         .clamp(a.g, lowerBound.g, upperBound.g), Utils.clamp(a.b, lowerBound.b,
            upperBound.b), Utils.clamp(a.a, lowerBound.a, upperBound.a));
   }

   /**
    * Ensures that the values of the color are clamped to the range [0.0,
    * 1.0].
    *
    * @param a      the color
    * @param target the output color
    *
    * @return the clamped color
    *
    * @see Utils#clamp01(float)
    */
   public static Color clamp01 ( final Color a, final Color target ) {

      return target.set(Utils.clamp01(a.r), Utils.clamp01(a.g), Utils.clamp01(
         a.b), Utils.clamp01(a.a));
   }

   /**
    * Returns the color clear black, ( 0.0, 0.0, 0.0, 0.0 ) .
    *
    * @param target the output color
    *
    * @return clear black
    */
   public static Color clearBlack ( final Color target ) {

      return target.set(0.0f, 0.0f, 0.0f, 0.0f);
   }

   /**
    * Returns the color clear white, ( 1.0, 1.0, 1.0, 0.0 ) .
    *
    * @param target the output color
    *
    * @return clear white
    */
   public static Color clearWhite ( final Color target ) {

      return target.set(1.0f, 1.0f, 1.0f, 0.0f);
   }

   /**
    * Returns the color cyan, ( 0.0, 1.0, 1.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return cyan
    */
   public static Color cyan ( final Color target ) {

      return target.set(0.0f, 1.0f, 1.0f, 1.0f);
   }

   /**
    * Divides the left operand by the right, except for the alpha channel,
    * then clamps the product to [0.0, 1.0] . The left operand's alpha channel
    * is retained.
    *
    * @param a      left operand, numerator
    * @param b      right operand, denominator
    * @param target output color
    *
    * @return the quotient
    */
   public static Color div ( final Color a, final Color b,
      final Color target ) {

      return target.set(Utils.clamp01(Utils.div(a.r, b.r)), Utils.clamp01(Utils
         .div(a.g, b.g)), Utils.clamp01(Utils.div(a.b, b.b)), Utils.clamp01(
            a.a));
   }

   /**
    * Divides the left operand by the right, except for the alpha channel,
    * then clamps the product to [0.0, 1.0] . The left operand's alpha channel
    * is retained.
    *
    * @param a      left operand, numerator
    * @param b      right operand, denominator
    * @param target output color
    *
    * @return the quotient
    */
   public static Color div ( final Color a, final float b,
      final Color target ) {

      if ( b != 0.0f ) {
         final float bInv = 1.0f / b;
         return target.set(Utils.clamp01(a.r * bInv), Utils.clamp01(a.g * bInv),
            Utils.clamp01(a.b * bInv), Utils.clamp01(a.a));
      }
      return target.set(0.0f, 0.0f, 0.0f, Utils.clamp01(a.a));
   }

   /**
    * Divides the left operand by the right, except for the alpha channel,
    * then clamps the product to [0.0, 1.0] . The left operand is also
    * supplied to the alpha channel.
    *
    * @param a      left operand, numerator
    * @param b      right operand, denominator
    * @param target output color
    *
    * @return the quotient
    */
   public static Color div ( final float a, final Color b,
      final Color target ) {

      return target.set(Utils.clamp01(Utils.div(a, b.r)), Utils.clamp01(Utils
         .div(a, b.g)), Utils.clamp01(Utils.div(a, b.b)), Utils.clamp01(a));
   }

   /**
    * Converts a direction to a color. Normalizes the direction, multiplies it
    * by 0.5, then adds 0.5 .
    *
    * @param v      the direction
    * @param target the output color
    *
    * @return the color
    */
   public static Color fromDir ( final Vec2 v, final Color target ) {

      final float mSq = Vec2.magSq(v);
      if ( mSq > 0.0f ) {
         final float mInv = 0.5f * Utils.invSqrtUnchecked(mSq);
         return target.set(v.x * mInv + 0.5f, v.y * mInv + 0.5f, 0.5f, 1.0f);
      }
      return target.set(0.5f, 0.5f, 0.5f, 1.0f);
   }

   /**
    * Converts a direction to a color. Normalizes the direction, multiplies it
    * by 0.5, then adds 0.5 .
    *
    * @param v      the direction
    * @param target the output color
    *
    * @return the color
    */
   public static Color fromDir ( final Vec3 v, final Color target ) {

      final float mSq = Vec3.magSq(v);
      if ( mSq > 0.0f ) {
         final float mInv = 0.5f * Utils.invSqrtUnchecked(mSq);
         return target.set(v.x * mInv + 0.5f, v.y * mInv + 0.5f, v.z * mInv
            + 0.5f, 1.0f);
      }
      return target.set(0.5f, 0.5f, 0.5f, 1.0f);
   }

   /**
    * Converts a direction to a color. Normalizes the direction, multiplies it
    * by 0.5, then adds 0.5 .
    *
    * @param v      the direction
    * @param target the output color
    *
    * @return the color
    */
   public static Color fromDir ( final Vec4 v, final Color target ) {

      final float mSq = Vec4.magSq(v);
      if ( mSq > 0.0f ) {
         final float mInv = 0.5f * Utils.invSqrtUnchecked(mSq);
         return target.set(v.x * mInv + 0.5f, v.y * mInv + 0.5f, v.z * mInv
            + 0.5f, v.w * mInv + 0.5f);
      }
      return target.set(0.5f, 0.5f, 0.5f, 0.5f);
   }

   /**
    * Convert a hexadecimal representation of a color stored as 0xAARRGGBB
    * into a color.
    *
    * @param c      the color in hexadecimal
    * @param target the output color
    *
    * @return the color
    *
    * @see IUtils#ONE_255
    */
   public static Color fromHex ( final int c, final Color target ) {

      /* @formatter:off */
      return target.set(
         ( c >> 0x10 & 0xff ) * IUtils.ONE_255,
         ( c >> 0x8  & 0xff ) * IUtils.ONE_255,
         ( c         & 0xff ) * IUtils.ONE_255,
         ( c >> 0x18 & 0xff ) * IUtils.ONE_255);
      /* @formatter:on */
   }

   /**
    * Convert a hexadecimal representation of a color stored as 0xAARRGGBB
    * into a color.
    *
    * @param c      the color in hexadecimal
    * @param target the output color
    *
    * @return the color
    *
    * @see IUtils#ONE_255
    */
   public static Color fromHex ( final long c, final Color target ) {

      /* @formatter:off */
      return target.set(
         ( c >> 0x10 & 0xffL ) * IUtils.ONE_255,
         ( c >> 0x8  & 0xffL ) * IUtils.ONE_255,
         ( c         & 0xffL ) * IUtils.ONE_255,
         ( c >> 0x18 & 0xffL ) * IUtils.ONE_255);
      /* @formatter:on */
   }

   /**
    * Attempts to convert a hexadecimal String to a color. Recognized formats
    * include:
    * <ul>
    * <li>"abc" - RGB, one digit per channel.</li>
    * <li>"#abc" - hash tag, RGB, one digit per channel.</li>
    * <li>"aabbcc" - RRGGBB, two digits per channel.
    * <li>"#aabbcc" - hash tag, RRGGBB, two digits per channel.</li>
    * <li>"aabbccdd" - AARRGGBB, two digits per channel.</li>
    * <li>"0xaabbccdd" - '0x' prefix, AARRGGBB, two digits per channel.</li>
    * </ul>
    * The output color will be reset if no suitable format is recognized.
    *
    * @param c      the input String
    * @param target the output color
    *
    * @return the color
    *
    * @see Integer#parseInt(String, int)
    * @see Long#parseLong(String, int)
    * @see Color#fromHex(int, Color)
    * @see String#replaceAll(String, String)
    * @see String#substring(int)
    */
   public static Color fromHex ( final String c, final Color target ) {

      final int len = c.length();

      try {
         String longform = "";
         int cint = 0xffffffff;

         // This doesn't seem like it's worth optimizing...
         // Pattern p = Pattern.compile("^(.)(.)(.)$");

         switch ( len ) {

            case 3:

               /* Example: "rgb" */

               longform = c.replaceAll("^(.)(.)(.)$", "$1$1$2$2$3$3");
               cint = Integer.parseInt(longform, 16);
               return Color.fromHex(0xff000000 | cint, target);

            case 4:

               /* Example: "#abc" */

               longform = c.replaceAll("^#(.)(.)(.)$", "#$1$1$2$2$3$3");
               cint = Integer.parseInt(longform.substring(1), 16);
               return Color.fromHex(0xff000000 | cint, target);

            case 6:

               /* Example: "aabbcc" */

               cint = Integer.parseInt(c, 16);
               return Color.fromHex(0xff000000 | cint, target);

            case 7:

               /* Example: "#aabbcc" */

               cint = Integer.parseInt(c.substring(1), 16);
               return Color.fromHex(0xff000000 | cint, target);

            case 8:

               /* Example: "aabbccdd" */

               cint = ( int ) Long.parseLong(c, 16);
               return Color.fromHex(cint, target);

            case 10:

               /* Example: "0xaabbccdd" */

               cint = ( int ) Long.parseLong(c.substring(2), 16);
               return Color.fromHex(cint, target);

            default:

               return target.reset();
         }

      } catch ( final Exception e ) {
         // System.out.println(e);
      }

      return target.reset();
   }

   /**
    * Returns one of the 16 web safe colors from a string key word. See
    * <a href="https://en.wikipedia.org/wiki/Web_colors#HTML_color_names">HTML
    * color names</a>.<br>
    * <br>
    * Differences in naming conventions include:
    * <ul>
    * <li>(0.0, 1.0, 1.0) may be either aqua or cyan;</li>
    * <li>(1.0, 0.0, 1.0) may be either fuchsia or magenta;</li>
    * <li>(0.0, 1.0, 0.0) is lime, not green;</li>
    * <li>(0.0, 0.5, 0.0) is green.</li>
    * </ul>
    *
    * @param keyword the key word
    * @param target  the output color
    *
    * @return the color by keyword
    */
   public static Color fromKeyword ( final String keyword,
      final Color target ) {

      /*
       * Switch casing with strings is messy. Decompiled code uses the string's
       * hash code instead. See Stack Overflow for discussions on how stable
       * Strings are across platforms.
       */

      final int hsh = keyword.toLowerCase().trim().hashCode();
      switch ( hsh ) {

         case 3002044:
         case 3068707:
            /* "aqua" or "cyan" */
            return Color.cyan(target);

         case 93818879:
            return Color.black(target);

         case 3027034:
            return Color.blue(target);

         case -519653673:
         case 828922025:
            /* "fuchsia" or "magenta" */
            return Color.magenta(target);

         case 3181155:
            /* "gray" */
            return target.set(0.5f, 0.5f, 0.5f, 1.0f);

         case 98619139:
            /* "green" */
            return target.set(0.0f, 0.5f, 0.0f, 1.0f);

         case 3321813:
            /* "lime" */
            return Color.green(target);

         case -1081301904:
            /* "maroon" */
            return target.set(0.5f, 0.0f, 0.0f, 1.0f);

         case 3374006:
            /* "navy" */
            return target.set(0.0f, 0.0f, 0.5f, 1.0f);

         case 105832923:
            /* "olive" */
            return target.set(0.5f, 0.5f, 0.0f, 1.0f);

         case -976943172:
            /* "purple" */
            return target.set(0.5f, 0.0f, 0.5f, 1.0f);

         case 112785:
            return Color.red(target);

         case -902311155:
            /* "silver" */
            return target.set(0.75f, 0.75f, 0.75f, 1.0f);

         case 3555932:
            /* "teal" */
            return target.set(0.0f, 0.5f, 0.5f, 1.0f);

         case 113101865:
            return Color.white(target);

         case -734239628:
            return Color.yellow(target);

         default:
            return target.reset();
      }
   }

   /**
    * Returns the color green, ( 0.0, 1.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return green
    */
   public static Color green ( final Color target ) {

      return target.set(0.0f, 1.0f, 0.0f, 1.0f);
   }

   /**
    * Converts from hue, saturation and brightness to a color with red, green
    * and blue channels.
    *
    * @param hue    the hue
    * @param sat    the saturation
    * @param bri    the brightness
    * @param alpha  the transparency
    * @param target the output color
    *
    * @return the color
    *
    * @see Utils#mod1(float)
    */
   public static Color hsbaToRgba ( final float hue, final float sat,
      final float bri, final float alpha, final Color target ) {

      if ( sat <= 0.0f ) { return target.set(bri, bri, bri, alpha); }

      final float h = Utils.mod1(hue) * 6.0f;
      final int sector = ( int ) h;
      final float secf = sector;

      final float tint1 = bri * ( 1.0f - sat );
      final float tint2 = bri * ( 1.0f - sat * ( h - secf ) );
      final float tint3 = bri * ( 1.0f - sat * ( 1.0f + secf - h ) );

      switch ( sector ) {
         case 0:
            return target.set(bri, tint3, tint1, alpha);

         case 1:
            return target.set(tint2, bri, tint1, alpha);

         case 2:
            return target.set(tint1, bri, tint3, alpha);

         case 3:
            return target.set(tint1, tint2, bri, alpha);

         case 4:
            return target.set(tint3, tint1, bri, alpha);

         case 5:
            return target.set(bri, tint1, tint2, alpha);

         default:
            return target.reset();
      }
   }

   /**
    * Converts from hue, saturation and brightness to a color with red, green
    * and blue channels.
    *
    * @param hsba   the HSBA vector
    * @param target the output color
    *
    * @return the color
    */
   public static Color hsbaToRgba ( final Vec4 hsba, final Color target ) {

      return Color.hsbaToRgba(hsba.x, hsba.y, hsba.z, hsba.w, target);
   }

   /**
    * Inverts a color by subtracting the red, green and blue channels from
    * one. Similar to bitNot, except alpha is not affected. Also similar to
    * adding 0.5 to the x component of a Vec4 storing hue, saturation and
    * brightness.
    *
    * @param c      the color
    * @param target the output color
    *
    * @return the inverse
    */
   public static Color inverse ( final Color c, final Color target ) {

      return target.set(Utils.max(0.0f, 1.0f - c.r), Utils.max(0.0f, 1.0f
         - c.g), Utils.max(0.0f, 1.0f - c.b), Utils.clamp01(c.a));
   }

   /**
    * Returns the relative luminance of the color, based on
    * <a href="https://en.wikipedia.org/wiki/Relative_luminance">
    * https://en.wikipedia.org/wiki/Relative_luminance</a> .
    *
    * @param c the input color
    *
    * @return the luminance
    */
   public static float luminance ( final Color c ) {

      return 0.2126f * c.r + 0.7152f * c.g + 0.0722f * c.b;
   }

   /**
    * Returns the relative luminance of the color, based on
    * <a href="https://en.wikipedia.org/wiki/Relative_luminance">
    * https://en.wikipedia.org/wiki/Relative_luminance</a> .<br>
    * <br>
    * Colors stored as integers are less precise than those stored as
    * <code>float</code>s (1.0 / 255.0 being the smallest difference between a
    * channel of two integer colors). Combined with single precision when
    * multiplying small numbers (all weighting factors must be divided by
    * 255.0 ), this will not yield the same result as
    * {@link Color#luminance(Color)} .
    *
    * @param c the input color
    *
    * @return the luminance
    */
   public static float luminance ( final int c ) {

      /*
       * Coefficients: 0.2126 / 255.0 ; 0.7152 / 255.0 ; 0.0722 / 255.0 . In
       * double precision: (a) 0.0008337254901960785d ; (b)
       * 0.002804705882352941d ; (c) 0.0002831372549019608d .
       */

      /* @formatter:off */
      return ( c >> 0x10 & 0xff ) * 0.0008337255f +
             ( c >> 0x8  & 0xff ) * 0.0028047059f +
             ( c         & 0xff ) * 0.00028313725f;
      /* @formatter:on */
   }

   /**
    * Returns the color magenta, ( 1.0, 0.0, 1.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return magenta
    */
   public static Color magenta ( final Color target ) {

      return target.set(1.0f, 0.0f, 1.0f, 1.0f);
   }

   /**
    * Sets the target color to the maximum components of the input colors. The
    * maximums are then clamped to [0.0, 1.0]. Equivalent to a 'lightest'
    * mixing function.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output color
    *
    * @return the maximum
    */
   public static Color max ( final Color a, final Color b,
      final Color target ) {

      return target.set(Utils.clamp01(Utils.max(a.r, b.r)), Utils.clamp01(Utils
         .max(a.g, b.g)), Utils.clamp01(Utils.max(a.b, b.b)), Utils.clamp01(
            Utils.max(a.a, b.a)));
   }

   /**
    * Sets the target color to the minimum components of the input colors. The
    * minimums are then clamped to [0.0, 1.0]. Equivalent to a 'darkest'
    * mixing function.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output color
    *
    * @return the maximum
    */
   public static Color min ( final Color a, final Color b,
      final Color target ) {

      return target.set(Utils.clamp01(Utils.min(a.r, b.r)), Utils.clamp01(Utils
         .min(a.g, b.g)), Utils.clamp01(Utils.min(a.b, b.b)), Utils.clamp01(
            Utils.min(a.a, b.a)));
   }

   /**
    * Mixes two colors by a step in the range [0.0, 1.0] . Uses smooth step
    * RGB easing.
    *
    * @param origin the origin color
    * @param dest   the destination color
    * @param step   the step
    * @param target the output color
    *
    * @return the mixed color
    */
   public static Color mix ( final Color origin, final Color dest,
      final float step, final Color target ) {

      if ( step <= 0.0f ) { return target.set(origin); }
      if ( step >= 1.0f ) { return target.set(dest); }

      final float t = step * step * ( 3.0f - ( step + step ) );
      final float u = 1.0f - t;
      return target.set(u * origin.r + t * dest.r, u * origin.g + t * dest.g, u
         * origin.b + t * dest.b, u * origin.a + t * dest.a);
   }

   /**
    * Wraps the left operand to the range of the right, then clamps the result
    * to [0.0, 1.0] . The left operand's alpha channel is retained.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target output color
    *
    * @return the wrapped color
    */
   public static Color mod ( final Color a, final Color b,
      final Color target ) {

      return target.set(Utils.clamp01(Utils.mod(a.r, b.r)), Utils.clamp01(Utils
         .mod(a.g, b.g)), Utils.clamp01(Utils.mod(a.b, b.b)), Utils.clamp01(
            a.a));
   }

   /**
    * Wraps the left operand to the range of the right, then clamps the result
    * to [0.0, 1.0] . The left operand's alpha channel is retained.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target output color
    *
    * @return the wrapped color
    */
   public static Color mod ( final Color a, final float b,
      final Color target ) {

      return target.set(Utils.clamp01(Utils.mod(a.r, b)), Utils.clamp01(Utils
         .mod(a.g, b)), Utils.clamp01(Utils.mod(a.b, b)), Utils.clamp01(a.a));
   }

   /**
    * Wraps the left operand to the range of the right, then clamps the result
    * to [0.0, 1.0] . The left operand's alpha channel is retained.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target output color
    *
    * @return the wrapped color
    */
   public static Color mod ( final float a, final Color b,
      final Color target ) {

      return target.set(Utils.clamp01(Utils.mod(a, b.r)), Utils.clamp01(Utils
         .mod(a, b.g)), Utils.clamp01(Utils.mod(a, b.b)), Utils.clamp01(a));
   }

   /**
    * Multiplies the left and right operand, except for the alpha channel,
    * then clamps the product to [0.0, 1.0] . The left operand's alpha channel
    * is retained. For that reason, color multiplication is <em>not</em>
    * commutative.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target output color
    *
    * @return the product
    */
   public static Color mul ( final Color a, final Color b,
      final Color target ) {

      return target.set(Utils.clamp01(a.r * b.r), Utils.clamp01(a.g * b.g),
         Utils.clamp01(a.b * b.b), Utils.clamp01(a.a));
   }

   /**
    * Multiplies the left and right operand, except for the alpha channel,
    * then clamps the product to [0.0, 1.0] . The left operand's alpha channel
    * is retained. For that reason, color multiplication is <em>not</em>
    * commutative.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target output color
    *
    * @return the product
    */
   public static Color mul ( final Color a, final float b,
      final Color target ) {

      return target.set(Utils.clamp01(a.r * b), Utils.clamp01(a.g * b), Utils
         .clamp01(a.b * b), Utils.clamp01(a.a));
   }

   /**
    * Multiplies the left and right operand, except for the alpha channel,
    * then clamps the product to [0.0, 1.0] . The left operand is also
    * supplied to the alpha channel. For that reason, color multiplication is
    * <em>not</em> commutative.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target output color
    *
    * @return the product
    */
   public static Color mul ( final float a, final Color b,
      final Color target ) {

      return target.set(Utils.clamp01(a * b.r), Utils.clamp01(a * b.g), Utils
         .clamp01(a * b.b), Utils.clamp01(a));
   }

   /**
    * Tests to see if the alpha channel of this color is less than or equal to
    * zero, i.e., if it is completely transparent.
    *
    * @param c the color
    *
    * @return the evaluation
    */
   public static boolean none ( final Color c ) { return c.a <= 0.0f; }

   /**
    * Raises a color to the power of a scalar.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output color
    *
    * @return the result
    */
   public static Color pow ( final Color a, final float b,
      final Color target ) {

      final double bd = b;
      return target.set(Utils.clamp01(( float ) Math.pow(a.r, bd)), Utils
         .clamp01(( float ) Math.pow(a.g, bd)), Utils.clamp01(( float ) Math
            .pow(a.b, bd)), Utils.clamp01(a.a));
   }

   /**
    * Multiplies the red, green and blue color channels of a color by the
    * alpha channel.
    *
    * @param c      the input color
    * @param target the output color
    *
    * @return the multiplied color
    */
   public static Color preMul ( final Color c, final Color target ) {

      if ( c.a <= 0.0f ) {
         return target.set(0.0f, 0.0f, 0.0f, 0.0f);
      } else if ( c.a >= 1.0f ) { return target.set(c.r, c.g, c.b, 1.0f); }

      return target.set(c.r * c.a, c.g * c.a, c.b * c.a, c.a);
   }

   /**
    * Reduces the signal, or granularity, of a color's channels. Any level
    * less than 2 or greater than 255 returns sets the target to the input.
    *
    * @param c      the color
    * @param levels the levels
    * @param target the output color
    *
    * @return the posterized color
    *
    * @see Vec4#quantize(Vec4, int, Vec4)
    * @see Utils#floor(float)
    */
   public static Color quantize ( final Color c, final int levels,
      final Color target ) {

      if ( levels < 2 || levels > 255 ) { return target.set(c); }

      final float levf = levels;
      final float delta = 1.0f / levf;
      return target.set(delta * Utils.floor(0.5f + c.r * levf), delta * Utils
         .floor(0.5f + c.g * levf), delta * Utils.floor(0.5f + c.b * levf),
         delta * Utils.floor(0.5f + c.a * levf));
   }

   /**
    * Creates a random color. Defaults to a random RGB channel.
    *
    * @param rng    the random number generator
    * @param target the output color
    *
    * @return the color
    */
   public static Color random ( final java.util.Random rng,
      final Color target ) {

      return Color.randomRgb(rng, target);
   }

   /**
    * Creates a random HSBA vector, then converts it to an RGBA color. The
    * alpha channel is not randomized.
    *
    * @param rng    the random number generator
    * @param target the output color
    * @param hsba   the output HSBA vector
    *
    * @return the color
    *
    * @see Color#hsbaToRgba(Vec4, Color)
    */
   public static Color randomHsb ( final java.util.Random rng,
      final Color target, final Vec4 hsba ) {

      hsba.set(rng.nextFloat(), rng.nextFloat(), rng.nextFloat(), 1.0f);
      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Creates a random HSBA vector from a lower- and upper-bound, then
    * converts it to an RGBA color. The alpha channel is not included.
    *
    * @param rng        the random number generator
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    * @param target     the output color
    * @param hsba       the output HSBA vector
    *
    * @return the color
    *
    * @see Color#hsbaToRgba(Vec4, Color)
    */
   public static Color randomHsb ( final java.util.Random rng,
      final Vec4 lowerBound, final Vec4 upperBound, final Color target,
      final Vec4 hsba ) {

      final float rx = rng.nextFloat();
      final float ry = rng.nextFloat();
      final float rz = rng.nextFloat();

      hsba.set( ( 1.0f - rx ) * lowerBound.x + rx * upperBound.x, ( 1.0f - ry )
         * lowerBound.y + ry * upperBound.y, ( 1.0f - rz ) * lowerBound.z + rz
            * upperBound.z, 1.0f);
      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Creates a random HSBA vector, then converts it to an RGBA color. The
    * alpha channel is randomized.
    *
    * @param rng    the random number generator
    * @param target the output color
    * @param hsba   the output HSBA vector
    *
    * @return the color
    *
    * @see Color#hsbaToRgba(Vec4, Color)
    */
   public static Color randomHsba ( final java.util.Random rng,
      final Color target, final Vec4 hsba ) {

      hsba.set(rng.nextFloat(), rng.nextFloat(), rng.nextFloat(), rng
         .nextFloat());
      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Creates a random HSBA vector from a lower- and upper-bound, then
    * converts it to an RGBA color. The alpha channel is randomized.
    *
    * @param rng        the random number generator
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    * @param target     the output color
    * @param hsba       the output HSBA vector
    *
    * @return the color
    *
    * @see Color#hsbaToRgba(Vec4, Color)
    */
   public static Color randomHsba ( final java.util.Random rng,
      final Vec4 lowerBound, final Vec4 upperBound, final Color target,
      final Vec4 hsba ) {

      Vec4.randomCartesian(rng, lowerBound, upperBound, hsba);
      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Creates a random color from red, green and blue channels. The alpha
    * channel is not included.
    *
    * @param rng    the random number generator
    * @param target the output color
    *
    * @return the color
    *
    * @see java.util.Random#nextFloat()
    */
   public static Color randomRgb ( final java.util.Random rng,
      final Color target ) {

      return target.set(rng.nextFloat(), rng.nextFloat(), rng.nextFloat(),
         1.0f);
   }

   /**
    * Creates a random color from a lower- and upper-bound. The alpha channel
    * is not included.
    *
    * @param rng        the random number generator
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    * @param target     the output color
    *
    * @return the color
    */
   public static Color randomRgb ( final java.util.Random rng,
      final Color lowerBound, final Color upperBound, final Color target ) {

      final float rx = rng.nextFloat();
      final float ry = rng.nextFloat();
      final float rz = rng.nextFloat();
      return target.set( ( 1.0f - rx ) * lowerBound.r + rx * upperBound.r,
         ( 1.0f - ry ) * lowerBound.g + ry * upperBound.g, ( 1.0f - rz )
            * lowerBound.b + rz * upperBound.b, 1.0f);
   }

   /**
    * Creates a random color from red, green, blue and alpha channels.
    *
    * @param rng    the random number generator
    * @param target the output color
    *
    * @return the color
    */
   public static Color randomRgba ( final java.util.Random rng,
      final Color target ) {

      return target.set(rng.nextFloat(), rng.nextFloat(), rng.nextFloat(), rng
         .nextFloat());
   }

   /**
    * Creates a random color from a lower- and upper-bound.
    *
    * @param rng        the random number generator
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    * @param target     the output color
    *
    * @return the color
    */
   public static Color randomRgba ( final java.util.Random rng,
      final Color lowerBound, final Color upperBound, final Color target ) {

      /* @formatter:off */
      final float rx = rng.nextFloat();
      final float ry = rng.nextFloat();
      final float rz = rng.nextFloat();
      final float rw = rng.nextFloat();
      return target.set(
         ( 1.0f - rx ) * lowerBound.r + rx * upperBound.r,
         ( 1.0f - ry ) * lowerBound.g + ry * upperBound.g,
         ( 1.0f - rz ) * lowerBound.b + rz * upperBound.b,
         ( 1.0f - rw ) * lowerBound.a + rw * upperBound.a);
      /* @formatter:on */
   }

   /**
    * Returns the color red, ( 1.0, 0.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return red
    */
   public static Color red ( final Color target ) {

      return target.set(1.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Convert a color to gray-scale based on its perceived luminance.
    *
    * @param c      the input color
    * @param target the output color
    *
    * @return the gray scale color
    *
    * @see Color#luminance(Color)
    */
   public static Color rgbaToGray ( final Color c, final Color target ) {

      final float lum = Color.luminance(c);
      return target.set(lum, lum, lum, c.a);
   }

   /**
    * Converts a color to a vector which holds hue, saturation, brightness and
    * alpha.
    *
    * @param c      the color
    * @param target the output vector
    *
    * @return the HSBA vector
    */
   public static Vec4 rgbaToHsba ( final Color c, final Vec4 target ) {

      return Color.rgbaToHsba(c.r, c.g, c.b, c.a, target);
   }

   /**
    * Converts RGBA channels to a vector which holds hue, saturation,
    * brightness and alpha.
    *
    * @param red    the red channel
    * @param green  the green channel
    * @param blue   the blue channel
    * @param alpha  the alpha channel
    * @param target the output vector
    *
    * @return the HSBA values
    *
    * @see Utils#max
    * @see Utils#min
    * @see IUtils#ONE_SIX
    */
   public static Vec4 rgbaToHsba ( final float red, final float green,
      final float blue, final float alpha, final Vec4 target ) {

      final float bri = Utils.max(red, green, blue);
      final float mn = Utils.min(red, green, blue);
      final float delta = bri - mn;
      float hue = 0.0f;

      if ( delta != 0.0f ) {
         if ( red == bri ) {
            hue = ( green - blue ) / delta;
         } else if ( green == bri ) {
            hue = 2.0f + ( blue - red ) / delta;
         } else {
            hue = 4.0f + ( red - green ) / delta;
         }

         hue *= IUtils.ONE_SIX;
         if ( hue < 0.0f ) { ++hue; }
      }

      final float sat = bri != 0.0f ? delta / bri : 0.0f;
      return target.set(hue, sat, bri, alpha);
   }

   /**
    * Converts a color from RGB to CIE XYZ.
    *
    * @param c      the color
    * @param target the output vector
    *
    * @return the XYZ color
    */
   public static Vec4 rgbaToXyzw ( final Color c, final Vec4 target ) {

      return Color.rgbaToXyzw(c.r, c.g, c.b, c.a, target);
   }

   /**
    * Converts a color from RGB to CIE XYZ. References Pharr, Jakob, and
    * Humphreys' <a href="http://www.pbr-book.org/">Physically Based
    * Rendering</a>.
    *
    * @param r      the red component
    * @param g      the green component
    * @param b      the blue component
    * @param a      the alpha component
    * @param target the output vector
    *
    * @return the XYZ values.
    */
   public static Vec4 rgbaToXyzw ( final float r, final float g, final float b,
      final float a, final Vec4 target ) {

      return target.set(0.412453f * r + 0.357580f * g + 0.180423f * b, 0.212671f
         * r + 0.715160f * g + 0.072169f * b, 0.019334f * r + 0.119193f * g
            + 0.950227f * b, a);
   }

   /**
    * Finds the maximum color channel of a color, excluding alpha.
    *
    * @param c the color
    *
    * @return the maximum channel
    *
    * @see Utils#max(float, float, float)
    */
   public static float rgbMax ( final Color c ) {

      return Utils.max(c.r, c.g, c.b);
   }

   /**
    * Finds the minimum color channel of a color, excluding alpha.
    *
    * @param c the color
    *
    * @return the minimum channel
    *
    * @see Utils#min(float, float, float)
    */
   public static float rgbMin ( final Color c ) {

      return Utils.min(c.r, c.g, c.b);
   }

   /**
    * Shifts a color's brightness by a factor. The brightness is clamped to
    * the range [0.0, 1.0] .
    *
    * @param c      the input color
    * @param shift  the brightness shift
    * @param target the output color
    * @param hsba   the color in HSB
    *
    * @return the shifted color
    */
   public static Color shiftBri ( final Color c, final float shift,
      final Color target, final Vec4 hsba ) {

      Color.rgbaToHsba(c, hsba);
      hsba.z = Utils.clamp01(hsba.z + shift);
      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Shifts a color's hue, saturation and brightness by a vector. The color's
    * alpha remains unaffected.
    *
    * @param c      the input color
    * @param shift  the shift
    * @param target the output color
    * @param hsba   the color in HSB
    *
    * @return the shifted color
    */
   public static Color shiftHsb ( final Color c, final Vec4 shift,
      final Color target, final Vec4 hsba ) {

      /* HSBA to RGBA conversion takes care of wrapping the hue. */
      Color.rgbaToHsba(c, hsba);
      hsba.x += shift.x;
      hsba.y = Utils.clamp01(hsba.y + shift.y);
      hsba.z = Utils.clamp01(hsba.z + shift.z);
      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Shifts a color's hue, saturation and brightness by a vector.
    *
    * @param c      the input color
    * @param shift  the shift
    * @param target the output color
    * @param hsba   the color in HSB
    *
    * @return the shifted color
    */
   public static Color shiftHsba ( final Color c, final Vec4 shift,
      final Color target, final Vec4 hsba ) {

      /* HSBA to RGBA conversion takes care of wrapping the hue. */
      Color.rgbaToHsba(c, hsba);
      hsba.x += shift.x;
      hsba.y = Utils.clamp01(hsba.y + shift.y);
      hsba.z = Utils.clamp01(hsba.z + shift.z);
      hsba.w = Utils.clamp01(hsba.w + shift.w);
      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Shifts a color's hue by a factor. The hue wraps around the range [0.0,
    * 1.0] .
    *
    * @param c      the input color
    * @param shift  the hue shift
    * @param target the output color
    * @param hsba   the color in HSB
    *
    * @return the shifted color
    */
   public static Color shiftHue ( final Color c, final float shift,
      final Color target, final Vec4 hsba ) {

      /* HSBA to RGBA conversion takes care of wrapping the hue. */
      Color.rgbaToHsba(c, hsba);
      hsba.x += shift;
      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Shifts a color's saturation by a factor. The saturation is clamped to
    * the range [0.0, 1.0] .
    *
    * @param c      the input color
    * @param shift  the saturation shift
    * @param target the output color
    * @param hsba   the color in HSB
    *
    * @return the shifted color
    */
   public static Color shiftSat ( final Color c, final float shift,
      final Color target, final Vec4 hsba ) {

      Color.rgbaToHsba(c, hsba);
      hsba.y = Utils.clamp01(hsba.y + shift);
      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Subtracts the right operand from the left operand, except for the alpha
    * channel, then clamps the sum to [0.0, 1.0] . The left operand's alpha
    * channel is retained.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target output color
    *
    * @return the difference
    */
   public static Color sub ( final Color a, final Color b,
      final Color target ) {

      return target.set(Utils.clamp01(a.r - b.r), Utils.clamp01(a.g - b.g),
         Utils.clamp01(a.b - b.b), Utils.clamp01(a.a));
   }

   /**
    * Converts a color to an integer where hexadecimal represents the ARGB
    * color channels: 0xAARRGGB .
    *
    * @param c the input color
    *
    * @return the color in hexadecimal
    */
   public static int toHexInt ( final Color c ) {

      /* @formatter:off */
      return ( int ) ( c.a * 0xff + 0.5f ) << 0x18
           | ( int ) ( c.r * 0xff + 0.5f ) << 0x10
           | ( int ) ( c.g * 0xff + 0.5f ) <<  0x8
           | ( int ) ( c.b * 0xff + 0.5f );
      /* @formatter:on */
   }

   /**
    * Converts a color to an integer where hexadecimal represents the ARGB
    * color channels: 0xAARRGGB .
    *
    * @param c the input color
    *
    * @return the color in hexadecimal
    */
   public static long toHexLong ( final Color c ) {

      return Color.toHexInt(c) & 0xffffffffL;
   }

   /**
    * Returns a representation of the color as a hexadecimal code, preceded by
    * a '0x', in the format AARRGGBB.
    *
    * @param c the color
    *
    * @return the string
    */
   public static String toHexString ( final Color c ) {

      return Color.toHexString(Color.toHexInt(c));
   }

   /**
    * Returns a Java-friendly representation of the color as a hexadecimal
    * code, preceded by a '0x', in the format AARRGGBB.
    *
    * @param c the color
    *
    * @return the string
    *
    * @see Integer#toHexString(int)
    */
   public static String toHexString ( final int c ) {

      return "0x" + Integer.toHexString(c);
   }

   /**
    * Returns a web-friendly representation of the color as a hexadecimal
    * code, preceded by a hash tag, '#', with no alpha.
    *
    * @param c the color
    *
    * @return the string
    */
   public static String toHexWeb ( final Color c ) {

      final byte rb = ( byte ) ( c.r * 0xff + 0.5f );
      final byte gb = ( byte ) ( c.g * 0xff + 0.5f );
      final byte bb = ( byte ) ( c.b * 0xff + 0.5f );

      final StringBuilder sb = new StringBuilder(7);
      sb.append('#');
      sb.append(Color.toHexString(rb));
      sb.append(Color.toHexString(gb));
      sb.append(Color.toHexString(bb));
      return sb.toString();
   }

   /**
    * Returns a web-friendly representation of the color as a hexadecimal
    * code, preceded by a hash tag, '#', with no alpha. Assumes the number
    * will be formatted as <code>0xAARRGGBB</code> , where alpha is the first
    * channel, followed by red, green and blue.
    *
    * @param c the color
    *
    * @return the string
    */
   public static String toHexWeb ( final int c ) {

      final byte rb = ( byte ) ( c >> 0x10 & 0xff );
      final byte gb = ( byte ) ( c >> 0x8 & 0xff );
      final byte bb = ( byte ) ( c & 0xff );

      final StringBuilder sb = new StringBuilder(7);
      sb.append('#');
      sb.append(Color.toHexString(rb));
      sb.append(Color.toHexString(gb));
      sb.append(Color.toHexString(bb));
      return sb.toString();
   }

   /**
    * Returns the color white, ( 1.0, 1.0, 1.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return white
    */
   public static Color white ( final Color target ) {

      return target.set(1.0f, 1.0f, 1.0f, 1.0f);
   }

   /**
    * Converts a color from CIE XYZ to RGB. References Pharr, Jakob, and
    * Humphreys' <a href="http://www.pbr-book.org/">Physically Based
    * Rendering</a>.
    *
    * @param x      the x coordinate
    * @param y      the y coordinate
    * @param z      the z coordinate
    * @param a      the alpha component
    * @param target the output color
    *
    * @return the color
    */
   public static Color xyzwToRgba ( final float x, final float y, final float z,
      final float a, final Color target ) {

      return target.set(3.240479f * x - 1.537150f * y - 0.498535f * z,
         -0.969256f * x + 1.875991f * y + 0.041556f * z, 0.055648f * x
            - 0.204043f * y + 1.057311f * z, a);
   }

   /**
    * Converts a color from CIE XYZ to RGB.
    *
    * @param v      the XYZ vector
    * @param target the output color
    *
    * @return the color
    */
   public static Color xyzwToRgba ( final Vec4 v, final Color target ) {

      return Color.xyzwToRgba(v.x, v.y, v.z, v.w, target);
   }

   /**
    * Returns the color yellow, ( 1.0, 1.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return yellow
    */
   public static Color yellow ( final Color target ) {

      return target.set(1.0f, 1.0f, 0.0f, 1.0f);
   }

   /**
    * Adds the left and right operand, except for the alpha channel, then
    * clamps the sum to [0.0, 1.0] . The left operand's alpha channel is
    * retained. For that reason, color addition is <em>not</em>
    * commutative.<br>
    * <br>
    * An internal function in support of Kotlin operator overloads.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target output color
    *
    * @return the sum
    */
   protected static Color add ( final Color a, final float b,
      final Color target ) {

      return target.set(Utils.clamp01(a.r + b), Utils.clamp01(a.g + b), Utils
         .clamp01(a.b + b), Utils.clamp01(a.a));
   }

   /**
    * Subtracts the right operand from the left operand, except for the alpha
    * channel, then clamps the sum to [0.0, 1.0] . The left operand's alpha
    * channel is retained.<br>
    * <br>
    * An internal function in support of Kotlin operator overloads.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target output color
    *
    * @return the difference
    */
   protected static Color sub ( final Color a, final float b,
      final Color target ) {

      return target.set(Utils.clamp01(a.r - b), Utils.clamp01(a.g - b), Utils
         .clamp01(a.b - b), Utils.clamp01(a.a));
   }

   /**
    * A helper function to translate a byte to a hexadecimal string. Does
    * <em>not</em> prefix the String with a hexadecimal indicator, '0x'; this
    * is so that Strings can be concatenated together.
    *
    * @param b the byte
    *
    * @return the string
    */
   protected static String toHexString ( final byte b ) {

      final int digit0 = b >> 0x4 & 0xf;
      final int digit1 = b & 0xf;
      final StringBuilder sb = new StringBuilder(2);

      /* @formatter:off */
      switch ( digit0 ) {
         case 0xa: sb.append('a'); break;
         case 0xb: sb.append('b'); break;
         case 0xc: sb.append('c'); break;
         case 0xd: sb.append('d'); break;
         case 0xe: sb.append('e'); break;
         case 0xf: sb.append('f'); break;
         default: sb.append(( char ) ( '0' + digit0 ));
      }

      switch ( digit1 ) {
         case 0xa: sb.append('a'); break;
         case 0xb: sb.append('b'); break;
         case 0xc: sb.append('c'); break;
         case 0xd: sb.append('d'); break;
         case 0xe: sb.append('e'); break;
         case 0xf: sb.append('f'); break;
         default: sb.append(( char ) ( '0' + digit1 ));
      }
      /* @formatter:on */

      return sb.toString();
   }

   /**
    * An abstract class to facilitate the creation of color easing functions.
    */
   public static abstract class AbstrEasing implements Utils.EasingFuncObj <
      Color > {

      /**
       * The default constructor.
       */
      public AbstrEasing ( ) { super(); }

      /**
       * A clamped interpolation between the origin and destination. Defers to
       * an unclamped interpolation, which is to be defined by sub-classes of
       * this class.
       *
       * @param origin the origin color
       * @param dest   the destination color
       * @param step   a factor in [0, 1]
       * @param target the output color
       *
       * @return the eased color
       */
      @Override
      public Color apply ( final Color origin, final Color dest,
         final Float step, final Color target ) {

         if ( step <= 0.0f ) { return target.set(origin); }
         if ( step >= 1.0f ) { return target.set(dest); }
         return this.applyUnclamped(origin, dest, step, target);
      }

      /**
       * The interpolation to be defined by subclasses.
       *
       * @param origin the origin color
       * @param dest   the destination color
       * @param step   a factor in [0, 1]
       * @param target the output color
       *
       * @return the eased color
       */
      public abstract Color applyUnclamped ( final Color origin,
         final Color dest, final float step, final Color target );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * Order in which to arrange color channels when flattening color to an
    * array.
    */
   public enum ChannelOrder {

      /**
       * Alpha, Blue, Green, Red.
       */
      ABGR ( ),

      /**
       * Alpha, Red, Green, Blue.
       */
      ARGB ( ),

      /**
       * Red, Green, Blue, Alpha.
       */
      RGBA ( );

      /**
       * The default constructor.
       */
      private ChannelOrder ( ) {}

   }

   /**
    * An iterator, which allows a color's components to be accessed in an
    * enhanced for loop.
    */
   public static final class ClrIterator implements Iterator < Float > {

      /**
       * The color being iterated over.
       */
      private final Color clr;

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The default constructor.
       *
       * @param c the color to iterate
       */
      public ClrIterator ( final Color c ) { this.clr = c; }

      /**
       * Tests to see if the iterator has another value.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext ( ) { return this.index < this.clr.length(); }

      /**
       * Gets the next value in the iterator.
       *
       * @see Vec4#get(int)
       *
       * @return the value
       */
      @Override
      public Float next ( ) { return this.clr.get(this.index++); }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * Compares two colors by brightness.
    */
   public static class ComparatorBri extends ComparatorHsb {

      /**
       * The default constructor.
       */
      public ComparatorBri ( ) { super(); }

      /**
       * The comparison function.
       *
       * @param a the left comparisand
       * @param b the right comparisand
       *
       * @return the comparison
       *
       * @see Color#rgbaToHsba(Color, Vec4)
       * @see Float#compare(float, float)
       */
      @Override
      public int compare ( final Color a, final Color b ) {

         Color.rgbaToHsba(a, this.aHsb);
         Color.rgbaToHsba(b, this.bHsb);

         return this.aHsb.z < this.bHsb.z ? -1 : this.aHsb.z > this.bHsb.z ? 1
            : 0;
      }

   }

   /**
    * An abstract class to facilitate the creation of HSB comparators.
    */
   public static abstract class ComparatorHsb implements Comparator < Color > {

      /**
       * Holds the HSB conversion of the left comparisand.
       */
      protected final Vec4 aHsb = new Vec4();

      /**
       * Holds the HSB conversion of the right comparisand.
       */
      protected final Vec4 bHsb = new Vec4();

      /**
       * The default constructor.
       */
      public ComparatorHsb ( ) { super(); }

      /**
       * The comparison function.
       *
       * @param a the left comparisand
       * @param b the right comparisand
       *
       * @return the comparison
       */
      @Override
      public abstract int compare ( final Color a, final Color b );

      /**
       * Returns this class's simple name as a string
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * Compares two colors by hue.
    */
   public static class ComparatorHue extends ComparatorHsb {

      /**
       * The default constructor.
       */
      public ComparatorHue ( ) { super(); }

      /**
       * Executes the comparison.
       *
       * @param a the left comparisand
       * @param b the right comparisand
       *
       * @return the comparison
       *
       * @see Color#rgbaToHsba(Color, Vec4)
       * @see Float#compare(float, float)
       */
      @Override
      public int compare ( final Color a, final Color b ) {

         Color.rgbaToHsba(a, this.aHsb);
         Color.rgbaToHsba(b, this.bHsb);

         return this.aHsb.x < this.bHsb.x ? -1 : this.aHsb.x > this.bHsb.x ? 1
            : 0;
      }

   }

   /**
    * Compares two colors by saturation.
    */
   public static class ComparatorSat extends ComparatorHsb {

      /**
       * The default constructor.
       */
      public ComparatorSat ( ) { super(); }

      /**
       * Executes the comparison.
       *
       * @param a the left comparisand
       * @param b the right comparisand
       *
       * @return the comparison
       *
       * @see Color#rgbaToHsba(Color, Vec4)
       * @see Float#compare(float, float)
       */
      @Override
      public int compare ( final Color a, final Color b ) {

         Color.rgbaToHsba(a, this.aHsb);
         Color.rgbaToHsba(b, this.bHsb);

         return this.aHsb.y < this.bHsb.y ? -1 : this.aHsb.y > this.bHsb.y ? 1
            : 0;
      }

   }

   /**
    * Eases the hue in the counter-clockwise direction.
    */
   public static class HueCCW extends HueEasing {

      /**
       * The default constructor.
       */
      public HueCCW ( ) { super(); }

      /**
       * Applies the function.
       *
       * @param origin the origin hue
       * @param dest   the destination hue
       * @param step   the step in a range 0 to 1
       *
       * @return the eased hue
       *
       * @see Utils#lerpUnclamped(float, float, float)
       * @see Utils#mod1(float)
       */
      @Override
      protected float applyPartial ( final float origin, final float dest,
         final float step ) {

         if ( this.oLtd ) {
            ++this.o;
            this.modResult = true;
         }

         final float fac = Utils.lerpUnclamped(this.o, this.d, step);
         if ( this.modResult ) { return Utils.mod1(fac); }
         return fac;
      }

   }

   /**
    * Eases between hues in the clockwise direction.
    */
   public static class HueCW extends HueEasing {

      /**
       * The default constructor.
       */
      public HueCW ( ) { super(); }

      /**
       * Applies the function.
       *
       * @param origin the origin hue
       * @param dest   the destination hue
       * @param step   the step in a range 0 to 1
       *
       * @return the eased hue
       *
       * @see Utils#lerpUnclamped(float, float, float)
       * @see Utils#mod1(float)
       */
      @Override
      protected float applyPartial ( final float origin, final float dest,
         final float step ) {

         if ( this.oGtd ) {
            ++this.d;
            this.modResult = true;
         }

         final float fac = Utils.lerpUnclamped(this.o, this.d, step);
         if ( this.modResult ) { return Utils.mod1(fac); }
         return fac;
      }

   }

   /**
    * An abstract parent class for hue easing functions.
    */
   public static abstract class HueEasing implements Utils.EasingFuncPrm <
      Float > {

      /**
       * The modulated destination hue.
       */
      protected float d = 0.0f;

      /**
       * The difference between the stop and start hue.
       */
      protected float diff = 0.0f;

      /**
       * Whether or not the result of the easing function needs to be subjected
       * to floor modulo.
       */
      protected boolean modResult = false;

      /**
       * The modulated origin hue.
       */
      protected float o = 0.0f;

      /**
       * Whether or not {@link o} is greater than {@link d}.
       */
      protected boolean oGtd = false;

      /**
       * Whether or not {@link o} is less than {@link d}.
       */
      protected boolean oLtd = false;

      /**
       * The default constructor.
       */
      public HueEasing ( ) { super(); }

      /**
       * The clamped easing function.
       *
       * @param origin the origin hue
       * @param dest   the destination hue
       * @param step   the step in range 0 to 1
       *
       * @return the eased hue
       */
      @Override
      public Float apply ( final Float origin, final Float dest,
         final Float step ) {

         this.eval(origin, dest);

         if ( step <= 0.0f || this.diff == 0.0f ) { return this.o; }
         if ( step >= 1.0f ) { return this.d; }
         return this.applyPartial(origin, dest, step);
      }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

      /**
       * The application function to be defined by sub-classes of this class.
       *
       * @param origin the origin hue
       * @param dest   the destination hue
       * @param step   the step
       *
       * @return the eased hue
       */
      protected abstract float applyPartial ( final float origin,
         final float dest, final float step );

      /**
       * A helper function to pass on to sub-classes of this class. Mutates the
       * fields a, b, difference, aLtb and aGtb.
       *
       * @param origin the origin hue
       * @param dest   the destination hue
       *
       * @see Utils#mod1(float)
       */
      protected void eval ( final float origin, final float dest ) {

         this.o = Utils.mod1(origin);
         this.d = Utils.mod1(dest);
         this.diff = this.d - this.o;
         this.oLtd = this.o < this.d;
         this.oGtd = this.o > this.d;
      }

   }

   /**
    * Eases between hues by the farthest clockwise direction.
    */
   public static class HueFar extends HueEasing {

      /**
       * The default constructor.
       */
      public HueFar ( ) { super(); }

      /**
       * Applies the function.
       *
       * @param origin the origin hue
       * @param dest   the destination hue
       * @param step   the step in a range 0 to 1
       *
       * @return the eased hue
       *
       * @see Utils#lerpUnclamped(float, float, float)
       * @see Utils#mod1(float)
       */
      @Override
      protected float applyPartial ( final float origin, final float dest,
         final float step ) {

         if ( this.oLtd && this.diff < 0.5f ) {
            ++this.o;
            this.modResult = true;
         } else if ( this.oGtd && this.diff > -0.5f ) {
            ++this.d;
            this.modResult = true;
         }

         final float fac = Utils.lerpUnclamped(this.o, this.d, step);
         if ( this.modResult ) { return Utils.mod1(fac); }
         return fac;
      }

   }

   /**
    * Eases between hues by the nearest clockwise direction.
    */
   public static class HueNear extends HueEasing {

      /**
       * The default constructor.
       */
      public HueNear ( ) { super(); }

      /**
       * Applies the function.
       *
       * @param origin the origin hue
       * @param dest   the destination hue
       * @param step   the step in a range 0 to 1
       *
       * @return the eased hue
       *
       * @see Utils#lerpUnclamped(float, float, float)
       * @see Utils#mod1(float)
       */
      @Override
      protected float applyPartial ( final float origin, final float dest,
         final float step ) {

         if ( this.oLtd && this.diff > 0.5f ) {
            ++this.o;
            this.modResult = true;
         } else if ( this.oGtd && this.diff < -0.5f ) {
            ++this.d;
            this.modResult = true;
         }

         final float fac = Utils.lerpUnclamped(this.o, this.d, step);
         if ( this.modResult ) { return Utils.mod1(fac); }
         return fac;
      }

   }

   /**
    * Eases between two colors.
    */
   public static class LerpRgba extends AbstrEasing {

      /**
       * The default constructor.
       */
      public LerpRgba ( ) { super(); }

      /**
       * Applies the function.
       *
       * @param origin the origin color
       * @param dest   the destination color
       * @param step   the step in a range 0 to 1
       * @param target the output color
       *
       * @return the eased color
       */
      @Override
      public Color applyUnclamped ( final Color origin, final Color dest,
         final float step, final Color target ) {

         /* This should remain as double precision. */
         final double td = step;
         final double ud = 1.0d - td;
         return target.set(( float ) ( ud * origin.r + td * dest.r ),
            ( float ) ( ud * origin.g + td * dest.g ), ( float ) ( ud * origin.b
               + td * dest.b ), ( float ) ( ud * origin.a + td * dest.a ));
      }

   }

   /**
    * Eases between colors by hue, saturation and brightness.
    */
   public static class MixHsba extends AbstrEasing {

      /**
       * The origin color in HSBA.
       */
      protected final Vec4 aHsb = new Vec4();

      /**
       * The destination color in HSBA.
       */
      protected final Vec4 bHsb = new Vec4();

      /**
       * The brightness easing function.
       */
      protected Utils.LerpUnclamped briFunc;

      /**
       * The new HSBA color.
       */
      protected final Vec4 hsbaNew = new Vec4();

      /**
       * The hue easing function.
       */
      protected HueEasing hueFunc;

      /**
       * The saturation easing function.
       */
      protected Utils.LerpUnclamped satFunc;

      /**
       * The default constructor. Creates a mixer with nearest hue interpolation
       * and linear interpolation for saturation and brightness.
       */
      public MixHsba ( ) { this(new HueNear()); }

      /**
       * Creates a color HSBA mixing function with the given hue easing
       * function. Saturation and brightness are governed by linear
       * interpolation.
       *
       * @param hueFunc the hue easing function
       */
      public MixHsba ( final HueEasing hueFunc ) {

         this(hueFunc, new Utils.Lerp(), new Utils.Lerp());
      }

      /**
       * Creates a color HSBA mixing function with the given easing functions
       * for hue, saturation and brightness.
       *
       * @param hueFunc the hue easing function
       * @param satFunc the saturation easing function
       * @param briFunc the brightness easing function
       */
      public MixHsba ( final HueEasing hueFunc,
         final Utils.LerpUnclamped satFunc,
         final Utils.LerpUnclamped briFunc ) {

         super();
         this.hueFunc = hueFunc;
         this.satFunc = satFunc;
         this.briFunc = briFunc;
      }

      /**
       * Applies the function.
       *
       * @param origin the origin color
       * @param dest   the destination color
       * @param step   the step in a range 0 to 1
       * @param target the output color
       *
       * @return the eased color
       *
       * @see Color#rgbaToHsba(Color, Vec4)
       */
      @Override
      public Color applyUnclamped ( final Color origin, final Color dest,
         final float step, final Color target ) {

         Color.rgbaToHsba(origin, this.aHsb);
         Color.rgbaToHsba(dest, this.bHsb);

         /* @formatter:off */
         this.hsbaNew.set(
            this.hueFunc.apply(this.aHsb.x, this.bHsb.x, step),
            this.satFunc.apply(this.aHsb.y, this.bHsb.y, step),
            this.briFunc.apply(this.aHsb.z, this.bHsb.z, step),
            ( 1.0f - step ) * this.aHsb.w + step * this.bHsb.w);
         return Color.hsbaToRgba(this.hsbaNew, target);
         /* @formatter:on */
      }

      /**
       * Gets the string identifier for the brightness easing function.
       *
       * @return the string
       */
      public String getBriFuncString ( ) { return this.briFunc.toString(); }

      /**
       * Gets the string identifier for the hue easing function.
       *
       * @return the string
       */
      public String getHueFuncString ( ) { return this.hueFunc.toString(); }

      /**
       * Gets the string identifier for the saturation easing function.
       *
       * @return the string
       */
      public String getSatFuncString ( ) { return this.satFunc.toString(); }

      /**
       * Sets the brightness easing function.
       *
       * @param briFunc the easing function
       */
      public void setBriFunc ( final Utils.LerpUnclamped briFunc ) {

         if ( briFunc != null ) { this.briFunc = briFunc; }
      }

      /**
       * Sets the hue easing function.
       *
       * @param hueFunc the easing function
       */
      public void setHueFunc ( final HueEasing hueFunc ) {

         if ( hueFunc != null ) { this.hueFunc = hueFunc; }
      }

      /**
       * Sets the saturation easing function.
       *
       * @param satFunc the saturation function
       */
      public void setSatFunc ( final Utils.LerpUnclamped satFunc ) {

         if ( satFunc != null ) { this.satFunc = satFunc; }
      }

   }

   /**
    * Eases between two colors with the smooth step formula:
    * <em>t</em><sup>2</sup> ( 3.0 - 2.0 <em>t</em> ) .
    */
   public static class SmoothStepRgba extends AbstrEasing {

      /**
       * The default constructor.
       */
      public SmoothStepRgba ( ) { super(); }

      /**
       * Applies the function.
       *
       * @param origin the origin color
       * @param dest   the destination color
       * @param step   the step in a range 0 to 1
       * @param target the output color
       *
       * @return the eased color
       */
      @Override
      public Color applyUnclamped ( final Color origin, final Color dest,
         final float step, final Color target ) {

         /* This should remain as double-precision! */
         final double td = step;
         final double ts = td * td * ( 3.0d - ( td + td ) );
         final double us = 1.0d - ts;

         return target.set(( float ) ( us * origin.r + ts * dest.r ),
            ( float ) ( us * origin.g + ts * dest.g ), ( float ) ( us * origin.b
               + ts * dest.b ), ( float ) ( us * origin.a + ts * dest.a ));
      }

   }

}
