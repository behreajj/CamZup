package camzup.core;

/**
 * Maintains consistent behavior between color objects.
 *
 * @param <T> the color type
 */
public interface IColor < T > extends Comparable < T > {

   /**
    * Returns -1 when this color is less than the comparisand; 1 when it is
    * greater than; 0 when the two are 'equal'. The implementation of this
    * method allows collections of colors to be sorted.
    *
    * @param d the comparisand
    *
    * @return the numeric code
    */
   @Override
   int compareTo ( final T d );

   /**
    * Tests this color for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    */
   @Override
   boolean equals ( final Object obj );

   /**
    * Returns a hash code for this color.
    *
    * @return the hash code
    */
   @Override
   int hashCode ( );

   /**
    * Resets this color to an initial state.
    *
    * @return this color
    */
   T reset ( );

   /**
    * Sets this color to the source color.
    *
    * @param source the source color
    *
    * @return this color
    */
   T set ( T source );

   /**
    * Converts a color to an integer where hexadecimal represents the color
    * components.
    *
    * @param source the input color
    *
    * @return the color in hexadecimal
    */
   int toHexInt ( );

   /**
    * Converts a color to an integer where hexadecimal represents the color
    * channels. Uses saturation arithmetic.
    *
    * @return the color in hexadecimal
    */
   int toHexIntSat ( );

   /**
    * Converts a color to an integer where hexadecimal represents the color
    * channels. Uses modular arithmetic.
    *
    * @return the color in hexadecimal
    */
   int toHexIntWrap ( );

   /**
    * Returns a string representation of this color.
    *
    * @return the string
    */
   @Override
   String toString ( );

   /**
    * Eases between hues in the clockwise direction.
    */
   public static final class HueCCW extends HueEasing {

      /**
       * The default constructor.
       */
      public HueCCW ( ) {}

      /**
       * Applies the function.
       *
       * @param step the step in a range 0 to 1
       *
       * @return the eased hue
       *
       * @see Utils#mod1(float)
       */
      @Override
      protected float applyPartial ( final float step ) {

         if ( this.diff == 0.0f ) { return this.o; }
         if ( this.oGtd ) {
            return Utils.mod1( ( 1.0f - step ) * this.o + step * ( this.d
               + 1.0f ));
         }
         return ( 1.0f - step ) * this.o + step * this.d;
      }

   }

   /**
    * Eases the hue in the counter-clockwise direction.
    */
   public static final class HueCW extends HueEasing {

      /**
       * The default constructor.
       */
      public HueCW ( ) {}

      /**
       * Applies the function.
       *
       * @param step the step in a range 0 to 1
       *
       * @return the eased hue
       *
       * @see Utils#mod1(float)
       */
      @Override
      protected float applyPartial ( final float step ) {

         if ( this.diff == 0.0f ) { return this.d; }
         if ( this.oLtd ) {
            return Utils.mod1( ( 1.0f - step ) * ( this.o + 1.0f ) + step
               * this.d);
         }
         return ( 1.0f - step ) * this.o + step * this.d;
      }

   }

   /**
    * An abstract parent class for hue easing functions.
    */
   public abstract static class HueEasing implements Utils.EasingFuncPrm <
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
      protected HueEasing ( ) {}

      /**
       * The clamped easing function.
       *
       * @param orig the origin hue
       * @param dest the destination hue
       * @param step the step in range 0 to 1
       *
       * @return the eased hue
       */
      @Override
      public Float apply ( final Float orig, final Float dest,
         final Float step ) {

         this.eval(orig, dest);
         final float tf = step;
         if ( tf <= 0.0f ) { return this.o; }
         if ( tf >= 1.0f ) { return this.d; }
         return this.applyPartial(tf);
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
       * @param step the step
       *
       * @return the eased hue
       */
      protected abstract float applyPartial ( final float step );

      /**
       * A helper function to pass on to sub-classes of this class. Mutates the
       * fields {@link o}, {@link d}, {@link diff}, {@link oLtd} and
       * {@link oGtd}.
       *
       * @param orig the origin hue
       * @param dest the destination hue
       *
       * @see Utils#mod1(float)
       */
      protected void eval ( final float orig, final float dest ) {

         this.o = Utils.mod1(orig);
         this.d = Utils.mod1(dest);
         this.diff = this.d - this.o;
         this.oLtd = this.o < this.d;
         this.oGtd = this.o > this.d;
      }

   }

   /**
    * Eases between hues by the farthest rotational direction.
    */
   public static final class HueFar extends HueEasing {

      /**
       * The default constructor.
       */
      public HueFar ( ) {}

      /**
       * Applies the function.
       *
       * @param step the step in a range 0 to 1
       *
       * @return the eased hue
       *
       * @see Utils#mod1(float)
       */
      @Override
      protected float applyPartial ( final float step ) {

         if ( this.diff == 0.0f || this.oLtd && this.diff < 0.5f ) {
            return Utils.mod1( ( 1.0f - step ) * ( this.o + 1.0f ) + step
               * this.d);
         }
         if ( this.oGtd && this.diff > -0.5f ) {
            return Utils.mod1( ( 1.0f - step ) * this.o + step * ( this.d
               + 1.0f ));
         }
         return ( 1.0f - step ) * this.o + step * this.d;
      }

   }

   /**
    * Eases between hues by the nearest rotational direction.
    */
   public static final class HueNear extends HueEasing {

      /**
       * The default constructor.
       */
      public HueNear ( ) {}

      /**
       * Applies the function.
       *
       * @param step the step in a range 0 to 1
       *
       * @return the eased hue
       *
       * @see Utils#mod1(float)
       */
      @Override
      protected float applyPartial ( final float step ) {

         if ( this.diff == 0.0f ) { return this.o; }
         if ( this.oLtd && this.diff > 0.5f ) {
            return Utils.mod1( ( 1.0f - step ) * ( this.o + 1.0f ) + step
               * this.d);
         }
         if ( this.oGtd && this.diff < -0.5f ) {
            return Utils.mod1( ( 1.0f - step ) * this.o + step * ( this.d
               + 1.0f ));
         }
         return ( 1.0f - step ) * this.o + step * this.d;
      }

   }

}
