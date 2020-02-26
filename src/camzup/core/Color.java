package camzup.core;

import java.util.Comparator;

/**
 * A mutable, extensible class. Supports RGBA and HSBA color
 * spaces. Supports conversion to and from integers where
 * color channels are in the format 0xAARRGGBB.
 */
public class Color extends Vec4 {

   /**
    * An abstract class to facilitate the creation of color
    * easing functions.
    */
   public static abstract class AbstrEasing
         implements Utils.EasingFuncObj < Color > {

      /**
       * The default constructor.
       */
      public AbstrEasing () {

         super();
      }

      /**
       * A clamped interpolation between the origin and
       * destination. Defers to an unclamped interpolation, which
       * is to be defined by sub-classes of this class.
       *
       * @param origin
       *           the origin color
       * @param dest
       *           the destination color
       * @param step
       *           a factor in [0, 1]
       * @param target
       *           the output color
       * @return the eased color
       */
      @Override
      public Color apply (
            final Color origin,
            final Color dest,
            final Float step,
            final Color target ) {

         if (step <= 0.0f) {
            return target.set(origin);
         }

         if (step >= 1.0f) {
            return target.set(dest);
         }

         return this.applyUnclamped(origin, dest, step, target);
      }

      /**
       * The interpolation to be defined by subclasses.
       *
       * @param origin
       *           the origin color
       * @param dest
       *           the destination color
       * @param step
       *           a factor in [0, 1]
       * @param target
       *           the output color
       * @return the eased color
       */
      public abstract Color applyUnclamped (
            final Color origin,
            final Color dest,
            final float step,
            Color target );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString () {

         return this.getClass().getSimpleName();
      }
   }

   /**
    * Compares two colors by brightness.
    */
   public static class ComparatorBri extends ComparatorHsb {

      /**
       * The default constructor.
       */
      public ComparatorBri () {

         super();
      }

      /**
       * The comparison function.
       *
       * @param a
       *           the left comparisand
       * @param b
       *           the right comparisand
       * @return the comparison
       * @see Color#rgbaToHsba(Color, Vec4)
       * @see Float#compare(float, float)
       */
      @Override
      public int compare (
            final Color a,
            final Color b ) {

         Color.rgbaToHsba(a, this.aHsb);
         Color.rgbaToHsba(b, this.bHsb);

         return this.aHsb.z < this.bHsb.z ? -1
               : this.aHsb.z > this.bHsb.z ? 1 : 0;
      }
   }

   /**
    * An abstract class to facilitate the creation of HSB
    * comparators.
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
      public ComparatorHsb () {

         super();
      }

      /**
       * The comparison function.
       *
       * @param a
       *           the left comparisand
       * @param b
       *           the right comparisand
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
      public String toString () {

         return this.getClass().getSimpleName();
      }
   }

   /**
    * Compares two colors by hue.
    */
   public static class ComparatorHue extends ComparatorHsb {

      /**
       * The default constructor.
       */
      public ComparatorHue () {

         super();
      }

      /**
       * Executes the comparison.
       *
       * @param a
       *           the left comparisand
       * @param b
       *           the right comparisand
       * @return the comparison
       * @see Color#rgbaToHsba(Color, Vec4)
       * @see Float#compare(float, float)
       */
      @Override
      public int compare (
            final Color a,
            final Color b ) {

         Color.rgbaToHsba(a, this.aHsb);
         Color.rgbaToHsba(b, this.bHsb);

         return this.aHsb.x < this.bHsb.x ? -1
               : this.aHsb.x > this.bHsb.x ? 1 : 0;
      }
   }

   /**
    * Compares two colors by saturation.
    */
   public static class ComparatorSat extends ComparatorHsb {

      /**
       * The default constructor.
       */
      public ComparatorSat () {

         super();
      }

      /**
       * Executes the comparison.
       *
       * @param a
       *           the left comparisand
       * @param b
       *           the right comparisand
       * @return the comparison
       * @see Color#rgbaToHsba(Color, Vec4)
       * @see Float#compare(float, float)
       */
      @Override
      public int compare (
            final Color a,
            final Color b ) {

         Color.rgbaToHsba(a, this.aHsb);
         Color.rgbaToHsba(b, this.bHsb);

         return this.aHsb.y < this.bHsb.y ? -1
               : this.aHsb.y > this.bHsb.y ? 1 : 0;
      }
   }

   /**
    * Eases the hue in the counter-clockwise direction.
    */
   public static class HueCCW extends HueEasing {

      /**
       * The default constructor.
       */
      public HueCCW () {

         super();
      }

      /**
       * Applies the function.
       *
       * @param origin
       *           the origin hue
       * @param dest
       *           the destination hue
       * @param step
       *           the step in a range 0 to 1
       * @return the eased hue
       * @see Utils#lerpUnclamped(float, float, float)
       * @see Utils#mod1(float)
       */
      @Override
      public float applyUnclamped (
            final float origin,
            final float dest,
            final float step ) {

         if (this.aLtb) {
            this.a = this.a + 1.0f;
            this.modResult = true;
         }

         final float fac = Utils.lerpUnclamped(this.a, this.b, step);
         if (this.modResult) {
            return Utils.mod1(fac);
         }
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
      public HueCW () {

         super();
      }

      /**
       * Applies the function.
       *
       * @param origin
       *           the origin hue
       * @param dest
       *           the destination hue
       * @param step
       *           the step in a range 0 to 1
       * @return the eased hue
       * @see Utils#lerpUnclamped(float, float, float)
       * @see Utils#mod1(float)
       */
      @Override
      public float applyUnclamped (
            final float origin,
            final float dest,
            final float step ) {

         if (this.aGtb) {
            this.b = this.b + 1.0f;
            this.modResult = true;
         }

         final float fac = Utils.lerpUnclamped(this.a, this.b, step);
         if (this.modResult) {
            return Utils.mod1(fac);
         }
         return fac;
      }
   }

   /**
    * An abstract parent class for hue easing functions.
    */
   public static abstract class HueEasing
         implements Utils.EasingFuncPrimitive < Float > {

      /**
       * The modulated origin hue.
       */
      protected float a = 0.0f;

      /**
       * Whether or not a is greater than b.
       */
      protected boolean aGtb = false;

      /**
       * Whether or not a is less than b.
       */
      protected boolean aLtb = false;

      /**
       * The modulated destination hue.
       */
      protected float b = 0.0f;

      /**
       * The difference between the stop and start hue.
       */
      protected float diff = 0.0f;

      /**
       * Whether or not the result of the easing function needs to
       * be subjected to floor mod.
       */
      protected boolean modResult = false;

      /**
       * The default constructor.
       */
      public HueEasing () {

         super();
      }

      /**
       * A helper function to pass on to sub-classes of this
       * class. Mutates the fields a, b, diff, aLtb and aGtb.
       *
       * @param origin
       *           the origin hue
       * @param dest
       *           the destination hue
       * @see Utils#mod1(float)
       */
      protected void eval (
            final float origin,
            final float dest ) {

         this.a = Utils.mod1(origin);
         this.b = Utils.mod1(dest);
         this.diff = this.b - this.a;
         this.aLtb = this.a < this.b;
         this.aGtb = this.a > this.b;
      }

      /**
       * The clamped easing function.
       *
       * @param origin
       *           the origin hue
       * @param dest
       *           the destination hue
       * @param step
       *           the step in range 0 to 1
       * @return the eased hue
       */
      @Override
      public Float apply (
            final Float origin,
            final Float dest,
            final Float step ) {

         this.eval(origin, dest);

         if (step <= 0.0f || this.diff == 0.0f) {
            return this.a;
         }
         if (step >= 1.0f) {
            return this.b;
         }
         return this.applyUnclamped(origin, dest, step);
      }

      /**
       * The application function to be defined by sub-classes of
       * this class.
       *
       * @param origin
       *           the origin hue
       * @param dest
       *           the destination hue
       * @param step
       *           the step
       * @return the eased hue
       */
      public abstract float applyUnclamped (
            final float origin,
            final float dest,
            final float step );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString () {

         return this.getClass().getSimpleName();
      }
   }

   /**
    * Eases between hues by the furthest clockwise direction.
    */
   public static class HueFar extends HueEasing {

      /**
       * The default constructor.
       */
      public HueFar () {

         super();
      }

      /**
       * Applies the function.
       *
       * @param origin
       *           the origin hue
       * @param dest
       *           the destination hue
       * @param step
       *           the step in a range 0 to 1
       * @return the eased hue
       * @see Utils#lerpUnclamped(float, float, float)
       * @see Utils#mod1(float)
       */
      @Override
      public float applyUnclamped (
            final float origin,
            final float dest,
            final float step ) {

         if (this.aLtb && this.diff < 0.5f) {
            this.a = this.a + 1.0f;
            this.modResult = true;
         } else if (this.aGtb && this.diff > -0.5f) {
            this.b = this.b + 1.0f;
            this.modResult = true;
         }

         final float fac = Utils.lerpUnclamped(this.a, this.b, step);
         if (this.modResult) {
            return Utils.mod1(fac);
         }
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
      public HueNear () {

         super();
      }

      /**
       * Applies the function.
       *
       * @param origin
       *           the origin hue
       * @param dest
       *           the destination hue
       * @param step
       *           the step in a range 0 to 1
       * @return the eased hue
       * @see Utils#lerpUnclamped(float, float, float)
       * @see Utils#mod1(float)
       */
      @Override
      public float applyUnclamped (
            final float origin,
            final float dest,
            final float step ) {

         if (this.aLtb && this.diff > 0.5f) {
            this.a = this.a + 1.0f;
            this.modResult = true;
         } else if (this.aGtb && this.diff < -0.5f) {
            this.b = this.b + 1.0f;
            this.modResult = true;
         }

         final float fac = Utils.lerpUnclamped(this.a, this.b, step);
         if (this.modResult) {
            return Utils.mod1(fac);
         }
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
      public LerpRgba () {

         super();
      }

      /**
       * Applies the function.
       *
       * @param origin
       *           the origin color
       * @param dest
       *           the destination color
       * @param step
       *           the step in a range 0 to 1
       * @param target
       *           the output color
       * @return the eased color
       */
      @Override
      public Color applyUnclamped (
            final Color origin,
            final Color dest,
            final float step,
            final Color target ) {

         /*
          * This should remain as double precision!
          */

         final double td = step;
         final double ud = 1.0d - td;
         return target.set(
               (float) (ud * origin.x + td * dest.x),
               (float) (ud * origin.y + td * dest.y),
               (float) (ud * origin.z + td * dest.z),
               (float) (ud * origin.w + td * dest.w));
      }
   }

   /**
    * Eases between colors by hue, saturation and brightness.
    */
   public static class MixHsba extends AbstrEasing {

      /**
       * The brightness easing function.
       */
      protected Utils.LerpUnclamped briFunc;

      /**
       * The destination color in HSBA.
       */
      protected final Vec4 hsbaDest = new Vec4();

      /**
       * The new HSBA color.
       */
      protected final Vec4 hsbaNew = new Vec4();

      /**
       * The origin color in HSBA.
       */
      protected final Vec4 hsbaOrigin = new Vec4();

      /**
       * The hue easing function.
       */
      protected HueEasing hueFunc;

      /**
       * The saturation easing function.
       */
      protected Utils.LerpUnclamped satFunc;

      /**
       * The default constructor. Creates a mixer with nearest hue
       * interpolation and linear interpolation for saturation and
       * brightness.
       */
      public MixHsba () {

         this(new HueNear());
      }

      /**
       * Creates a color HSBA mixing function with the given hue
       * easing function. Saturation and brightness are governed
       * by linear interpolation.
       *
       * @param hueFunc
       *           the hue easing function
       */
      public MixHsba ( final HueEasing hueFunc ) {

         this(hueFunc, new Utils.Lerp(), new Utils.Lerp());
      }

      /**
       * Creates a color HSBA mixing function with the given
       * easing functions for hue, saturation and brightness.
       *
       * @param hueFunc
       *           the hue easing function
       * @param satFunc
       *           the saturation easing function
       * @param briFunc
       *           the brightness easing function
       */
      public MixHsba (
            final HueEasing hueFunc,
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
       * @param origin
       *           the origin color
       * @param dest
       *           the destination color
       * @param step
       *           the step in a range 0 to 1
       * @param target
       *           the output color
       * @return the eased color
       * @see Color#rgbaToHsba(Color, Vec4)
       */
      @Override
      public Color applyUnclamped (
            final Color origin,
            final Color dest,
            final float step,
            final Color target ) {

         Color.rgbaToHsba(origin, this.hsbaOrigin);
         Color.rgbaToHsba(dest, this.hsbaDest);

         this.hsbaNew.set(
               this.hueFunc.apply(this.hsbaOrigin.x, this.hsbaDest.x, step),
               this.satFunc.apply(this.hsbaOrigin.y, this.hsbaDest.y, step),
               this.briFunc.apply(this.hsbaOrigin.z, this.hsbaDest.z, step),
               (1.0f - step) * this.hsbaOrigin.w + step * this.hsbaDest.w);

         return Color.hsbaToRgba(this.hsbaNew, target);
      }

      /**
       * Gets the string identifier for the brightness easing
       * function.
       *
       * @return the string
       */
      public String getBriFuncString () {

         return this.briFunc.toString();
      }

      /**
       * Gets the string identifier for the hue easing function.
       *
       * @return the string
       */
      public String getHueFuncString () {

         return this.hueFunc.toString();
      }

      /**
       * Gets the string identifier for the saturation easing
       * function.
       *
       * @return the string
       */
      public String getSatFuncString () {

         return this.satFunc.toString();
      }

      /**
       * Sets the brightness easing function.
       *
       * @param briFunc
       *           the easing function
       */
      public void setBriFunc ( final Utils.LerpUnclamped briFunc ) {

         if (briFunc != null) {
            this.briFunc = briFunc;
         }
      }

      /**
       * Sets the hue easing function.
       *
       * @param hueFunc
       *           the easing function
       */
      public void setHueFunc ( final HueEasing hueFunc ) {

         if (hueFunc != null) {
            this.hueFunc = hueFunc;
         }
      }

      /**
       * Sets the saturation easing function.
       *
       * @param satFunc
       *           the saturation function
       */
      public void setSatFunc ( final Utils.LerpUnclamped satFunc ) {

         if (satFunc != null) {
            this.satFunc = satFunc;
         }
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
      public SmoothStepRgba () {

         super();
      }

      /**
       * Applies the function.
       *
       * @param origin
       *           the origin color
       * @param dest
       *           the destination color
       * @param step
       *           the step in a range 0 to 1
       * @param target
       *           the output color
       * @return the eased color
       */
      @Override
      public Color applyUnclamped (
            final Color origin,
            final Color dest,
            final float step,
            final Color target ) {

         /*
          * This should remain as double-precision!
          */

         final double td = step;
         final double ts = td * td * (3.0d - (td + td));
         final double us = 1.0d - ts;
         return target.set(
               (float) (us * origin.x + ts * dest.x),
               (float) (us * origin.y + ts * dest.y),
               (float) (us * origin.z + ts * dest.z),
               (float) (us * origin.w + ts * dest.w));
      }
   }

   /**
    * The default easing function, smooth step RGBA.
    */
   private static AbstrEasing EASING = new SmoothStepRgba();

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = 3863260730744999721L;

   /**
    * Adds the left and right operand, except for the alpha
    * channel, then clamps the sum to [0.0, 1.0] . The left
    * operand's alpha channel is retained.
    *
    * For that reason, color addition is <em>not</em>
    * commutative.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @param target
    *           output color
    * @return the sum
    */
   public static Color add (
         final Color a,
         final Color b,
         final Color target ) {

      return target.set(
            Utils.clamp01(a.x + b.x),
            Utils.clamp01(a.y + b.y),
            Utils.clamp01(a.z + b.z),
            Utils.clamp01(a.w));
   }

   /**
    * Tests to see if all color channels are greater than zero.
    *
    * @param c
    *           the color
    * @return the evaluation
    */
   public static boolean all ( final Color c ) {

      return c.w > 0.0f &&
            c.x > 0.0f &&
            c.y > 0.0f &&
            c.z > 0.0f;
   }

   /**
    * Tests to see if the alpha channel of the color is greater
    * than zero, i.e. if it has some opacity.
    *
    * @param c
    *           the color
    * @return the evaluation
    */
   public static boolean any ( final Color c ) {

      return c.w > 0.0f;
   }

   /**
    * Converts two colors to integers, performs the bitwise AND
    * operation on them, then converts the result to a color.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output color
    * @return the result
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitAnd (
         final Color a,
         final Color b,
         final Color target ) {

      return Color.fromHex(Color.toHexInt(a) & Color.toHexInt(b), target);
   }

   /**
    * Converts a color to an integer, performs the bitwise NOT
    * operation on it, then converts the result to a color.
    *
    * @param a
    *           the input color
    * @param target
    *           the output color
    * @return the negation
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitNot (
         final Color a,
         final Color target ) {

      return Color.fromHex(~Color.toHexInt(a), target);
   }

   /**
    * Converts two colors to integers, performs the bitwise OR
    * operation (inclusive or) on them, then converts the
    * result to a color.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output color
    * @return the color
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitOr (
         final Color a,
         final Color b,
         final Color target ) {

      return Color.fromHex(Color.toHexInt(a) | Color.toHexInt(b), target);
   }

   /**
    * Converts a color to an integer, performs a bitwise left
    * shift operation, then converts the result to a color. The
    * number of places is multiplied by 0x08.
    *
    * @param a
    *           the color
    * @param places
    *           the number of places
    * @param target
    *           the output color
    * @return the shifted color
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitShiftLeft (
         final Color a,
         final int places,
         final Color target ) {

      return Color.fromHex(Color.toHexInt(a) << places * 0x08, target);
   }

   /**
    * Converts a color to an integer, performs a bitwise right
    * shift operation, then converts the result to a color. The
    * number of places is multiplied by 0x08.
    *
    * @param a
    *           the color
    * @param places
    *           the number of places
    * @param target
    *           the output color
    * @return the shifted color
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitShiftRight (
         final Color a,
         final int places,
         final Color target ) {

      return Color.fromHex(Color.toHexInt(a) >> places * 0x08, target);
   }

   /**
    * Converts a color to an integer, performs an unsigned
    * bitwise right shift operation, then converts the result
    * to a color. The number of places is multiplied by 0x08.
    *
    * @param a
    *           the color
    * @param places
    *           the number of places
    * @param target
    *           the output color
    * @return the shifted color
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitShiftRightUnsigned (
         final Color a,
         final int places,
         final Color target ) {

      return Color.fromHex(Color.toHexInt(a) >>> places * 0x08, target);
   }

   /**
    * Converts two colors to integers, performs the bitwise XOR
    * operation (exclusive or) on them, then converts the
    * result to a color.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output color
    * @return the color
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitXor (
         final Color a,
         final Color b,
         final Color target ) {

      return Color.fromHex(Color.toHexInt(a) ^ Color.toHexInt(b), target);
   }

   /**
    * Returns the color black, ( 0.0, 0.0, 0.0, 1.0 ) .
    *
    * @param target
    *           the output color
    * @return black
    */
   public static Color black ( final Color target ) {

      return target.set(0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Returns the color blue, ( 0.0, 0.0, 1.0, 1.0 ) .
    *
    * @param target
    *           the output color
    * @return blue
    */
   public static Color blue ( final Color target ) {

      return target.set(0.0f, 0.0f, 1.0f, 1.0f);
   }

   /**
    * Clamps a color to a lower- and upper-bound.
    *
    * @param a
    *           the input color
    * @param lowerBound
    *           the lower bound
    * @param upperBound
    *           the upper bound
    * @param target
    *           the output color
    * @return the clamped color
    * @see Utils#clamp(float, float, float)
    */
   public static Color clamp (
         final Color a,
         final Color lowerBound,
         final Color upperBound,
         final Color target ) {

      return target.set(
            Utils.clamp(a.x, lowerBound.x, upperBound.x),
            Utils.clamp(a.y, lowerBound.y, upperBound.y),
            Utils.clamp(a.z, lowerBound.z, upperBound.z),
            Utils.clamp(a.w, lowerBound.w, upperBound.w));
   }

   /**
    * Ensures that the values of the color are clamped to the
    * range [0.0, 1.0].
    *
    * @param a
    *           the color
    * @param target
    *           the output color
    * @return the clamped color
    * @see Utils#clamp01(float)
    */
   public static Color clamp01 (
         final Color a,
         final Color target ) {

      return target.set(
            Utils.clamp01(a.x),
            Utils.clamp01(a.y),
            Utils.clamp01(a.z),
            Utils.clamp01(a.w));
   }

   /**
    * Returns the color clear black, ( 0.0, 0.0, 0.0, 0.0 ) .
    *
    * @param target
    *           the output color
    * @return clear black
    */
   public static Color clearBlack ( final Color target ) {

      return target.set(0.0f, 0.0f, 0.0f, 0.0f);
   }

   /**
    * Returns the color clear white, ( 1.0, 1.0, 1.0, 0.0 ) .
    *
    * @param target
    *           the output color
    * @return clear white
    */
   public static Color clearWhite ( final Color target ) {

      return target.set(1.0f, 1.0f, 1.0f, 0.0f);
   }

   /**
    * Returns the color cyan, ( 0.0, 1.0, 1.0, 1.0 ) .
    *
    * @param target
    *           the output color
    * @return cyan
    */
   public static Color cyan ( final Color target ) {

      return target.set(0.0f, 1.0f, 1.0f, 1.0f);
   }

   /**
    * Divides the left operand by the right, except for the
    * alpha channel, then clamps the product to [0.0, 1.0] .
    * The left operand's alpha channel is retained.
    *
    * @param a
    *           left operand, numerator
    * @param b
    *           right operand, denominator
    * @param target
    *           output color
    * @return the quotient
    */
   public static Color div (
         final Color a,
         final Color b,
         final Color target ) {

      return target.set(
            Utils.clamp01(Utils.div(a.x, b.x)),
            Utils.clamp01(Utils.div(a.y, b.y)),
            Utils.clamp01(Utils.div(a.z, b.z)),
            Utils.clamp01(a.w));
   }

   /**
    * Divides the left operand by the right, except for the
    * alpha channel, then clamps the product to [0.0, 1.0] .
    * The left operand's alpha channel is retained.
    *
    * @param a
    *           left operand, numerator
    * @param b
    *           right operand, denominator
    * @param target
    *           output color
    * @return the quotient
    */
   public static Color div (
         final Color a,
         final float b,
         final Color target ) {

      if (b == 0.0f) {
         return target.set(0.0f, 0.0f, 0.0f, Utils.clamp01(a.w));
      }
      final float bInv = 1.0f / b;
      return target.set(
            Utils.clamp01(a.x * bInv),
            Utils.clamp01(a.y * bInv),
            Utils.clamp01(a.z * bInv),
            Utils.clamp01(a.w));
   }

   /**
    * Divides the left operand by the right, except for the
    * alpha channel, then clamps the product to [0.0, 1.0] .
    * The left operand is also supplied to the alpha channel.
    *
    * @param a
    *           left operand, numerator
    * @param b
    *           right operand, denominator
    * @param target
    *           output color
    * @return the quotient
    */
   public static Color div (
         final float a,
         final Color b,
         final Color target ) {

      return a == 0.0f ? target.reset()
            : target.set(
                  Utils.clamp01(Utils.div(a, b.x)),
                  Utils.clamp01(Utils.div(a, b.y)),
                  Utils.clamp01(Utils.div(a, b.z)),
                  Utils.clamp01(a));
   }

   /**
    * Converts a direction to a color. Normalizes the
    * direction, multiplies it by 0.5, then adds 0.5 .
    *
    * @param v
    *           the direction
    * @param target
    *           the output color
    * @return the color
    */
   public static Color fromDir (
         final Vec2 v,
         final Color target ) {

      final float mSq = Vec2.magSq(v);
      if (mSq == 0.0f) {
         return target.set(0.5f, 0.5f, 0.5f, 1.0f);
      }

      final float mInv = 0.5f * Utils.invSqrtUnchecked(mSq);
      return target.set(
            v.x * mInv + 0.5f,
            v.y * mInv + 0.5f,
            0.5f, 1.0f);
   }

   /**
    * Converts a direction to a color. Normalizes the
    * direction, multiplies it by 0.5, then adds 0.5 .
    *
    * @param v
    *           the direction
    * @param target
    *           the output color
    * @return the color
    */
   public static Color fromDir (
         final Vec3 v,
         final Color target ) {

      final float mSq = Vec3.magSq(v);
      if (mSq == 0.0f) {
         return target.set(0.5f, 0.5f, 0.5f, 1.0f);
      }

      final float mInv = 0.5f * Utils.invSqrtUnchecked(mSq);
      return target.set(
            v.x * mInv + 0.5f,
            v.y * mInv + 0.5f,
            v.z * mInv + 0.5f, 1.0f);
   }

   /**
    * Converts a direction to a color. Normalizes the
    * direction, multiplies it by 0.5, then adds 0.5 .
    *
    * @param v
    *           the direction
    * @param target
    *           the output color
    * @return the color
    */
   public static Color fromDir (
         final Vec4 v,
         final Color target ) {

      final float mSq = Vec4.magSq(v);
      if (mSq == 0.0f) {
         return target.set(0.5f, 0.5f, 0.5f, 0.5f);
      }

      final float mInv = 0.5f * Utils.invSqrtUnchecked(mSq);
      return target.set(
            v.x * mInv + 0.5f,
            v.y * mInv + 0.5f,
            v.z * mInv + 0.5f,
            v.w * mInv + 0.5f);
   }

   /**
    * Convert a hexadecimal representation of a color stored as
    * 0xAARRGGBB into a color.
    *
    * @param c
    *           the color in hexadecimal
    * @param target
    *           the output color
    * @return the color
    * @see IUtils#ONE_255
    */
   public static Color fromHex (
         final int c,
         final Color target ) {

      return target.set(
            (c >> 0x10 & 0xff) * IUtils.ONE_255,
            (c >> 0x8 & 0xff) * IUtils.ONE_255,
            (c & 0xff) * IUtils.ONE_255,
            (c >> 0x18 & 0xff) * IUtils.ONE_255);
   }

   /**
    * Convert a hexadecimal representation of a color stored as
    * 0xAARRGGBB into a color.
    *
    * @param c
    *           the color in hexadecimal
    * @param target
    *           the output color
    * @return the color
    * @see IUtils#ONE_255
    */
   public static Color fromHex (
         final long c,
         final Color target ) {

      return target.set(
            (c >> 0x10 & 0xff) * IUtils.ONE_255,
            (c >> 0x8 & 0xff) * IUtils.ONE_255,
            (c & 0xff) * IUtils.ONE_255,
            (c >> 0x18 & 0xff) * IUtils.ONE_255);
   }

   /**
    * Attempts to convert a hexadecimal String to a color.
    * Recognized formats include:
    *
    * <ul>
    * <li>"abc" - RGB, one digit per channel.</li>
    * <li>"#abc" - hash tag, RGB, one digit per channel.</li>
    * <li>"aabbcc" - RRGGBB, two digits per channel.
    * <li>"#aabbcc" - hash tag, RRGGBB, two digits per
    * channel.</li>
    * <li>"aabbccdd" - AARRGGBB, two digits per channel.</li>
    * <li>"0xaabbccdd" - '0x' prefix, AARRGGBB, two digits per
    * channel.</li>
    * </ul>
    *
    * The output color will be reset if no suitable format is
    * recognized.
    *
    * @param c
    *           the input String
    * @param target
    *           the output color
    * @return the color
    * @see Integer#parseInt(String, int)
    * @see Long#parseLong(String, int)
    * @see Color#fromHex(int, Color)
    * @see String#replaceAll(String, String)
    * @see String#substring(int)
    */
   public static Color fromHex (
         final String c,
         final Color target ) {

      final int len = c.length();

      try {
         String longform = "";
         int cint = 0xffffffff;

         switch (len) {

            case 3:

               /* Example: "rgb" */

               longform = c.replaceAll("^(.)(.)(.)$",
                     "$1$1$2$2$3$3");
               cint = Integer.parseInt(longform, 16);
               return Color.fromHex(0xff000000 | cint, target);

            case 4:

               /* Example: "#abc" */

               longform = c.replaceAll("^#(.)(.)(.)$",
                     "#$1$1$2$2$3$3");
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

               cint = (int) Long.parseLong(c, 16);
               return Color.fromHex(cint, target);

            case 10:

               /* Example: "0xaabbccdd" */

               cint = (int) Long.parseLong(c.substring(2), 16);
               return Color.fromHex(cint, target);

            default:

               return target.reset();
         }

      } catch (final NumberFormatException e) {
         // System.out.println(e);
      }

      return target.reset();
   }

   /**
    * Gets the name of the easing function used to mix colors.
    *
    * @return the easing function name
    */
   public static String getEasingString () {

      return Color.EASING.toString();
   }

   /**
    * Returns the color green, ( 0.0, 1.0, 0.0, 1.0 ) .
    *
    * @param target
    *           the output color
    * @return green
    */
   public static Color green ( final Color target ) {

      return target.set(0.0f, 1.0f, 0.0f, 1.0f);
   }

   /**
    * Converts from hue, saturation and brightness to a color
    * with red, green and blue channels.
    *
    * @param hue
    *           the hue
    * @param sat
    *           the saturation
    * @param bri
    *           the brightness
    * @param alpha
    *           the transparency
    * @param target
    *           the output color
    * @return the color
    * @see Utils#mod1(float)
    */
   public static Color hsbaToRgba (
         final float hue,
         final float sat,
         final float bri,
         final float alpha,
         final Color target ) {

      if (sat <= 0.0f) {
         return target.set(bri, bri, bri, alpha);
      }

      final float h = Utils.mod1(hue) * 6.0f;
      final int sector = (int) h;

      final float tint1 = bri * (1.0f - sat);
      final float tint2 = bri * (1.0f - sat * (h - sector));
      final float tint3 = bri * (1.0f - sat * (1.0f + sector - h));

      switch (sector) {
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
    * Converts from hue, saturation and brightness to a color
    * with red, green and blue channels.
    *
    * @param hsba
    *           the HSBA vector
    * @param target
    *           the output color
    * @return the color
    */
   public static Color hsbaToRgba (
         final Vec4 hsba,
         final Color target ) {

      return Color.hsbaToRgba(hsba.x, hsba.y, hsba.z, hsba.w, target);
   }

   /**
    * Inverts a color by subtracting the red, green and blue
    * channels from one. Similar to bitNot, except alpha is not
    * affected. Also similar to adding 0.5 to the x component
    * of a Vec4 storing hue, saturation and brightness.
    *
    * @param c
    *           the color
    * @param target
    *           the output color
    * @return the inverse
    */
   public static Color inverse (
         final Color c,
         final Color target ) {

      return target.set(
            Utils.max(0.0f, 1.0f - c.x),
            Utils.max(0.0f, 1.0f - c.y),
            Utils.max(0.0f, 1.0f - c.z),
            Utils.clamp01(c.w));
   }

   /**
    * Returns the relative luminance of the color, based on
    * <a href=
    * "https://en.wikipedia.org/wiki/Relative_luminance">
    * https://en.wikipedia.org/wiki/Relative_luminance</a> .
    *
    * @param c
    *           the input color
    * @return the luminance
    */
   public static float luminance ( final Color c ) {

      /*
       * In bytes:
       *
       * 0.2126 x 256.0 = 54.4256 = 0x36
       *
       * 0.7152 x 256.0 = 183.0912 = 0xb7
       *
       * 0.0722 x 256.0 = 18.4832 = 0x12
       *
       * PImage blend uses:
       *
       * 0.30 x 256.0 = 76.8 = 0x4d
       *
       * 0.59 x 256.0 = 151.04 = 0x97
       *
       * 0.11 x 256.0 = 28.16 = 0x1c
       **/

      return 0.2126f * c.x + 0.7152f * c.y + 0.0722f * c.z;
   }

   /**
    * Returns the relative luminance of the color, based on
    * <a href=
    * "https://en.wikipedia.org/wiki/Relative_luminance">
    * https://en.wikipedia.org/wiki/Relative_luminance</a> .
    *
    * @param c
    *           the input color
    * @return the luminance
    */
   public static float luminance ( final int c ) {

      return (c >> 0x10 & 0xff) * 0.000830469f +
            (c >> 0x8 & 0xff) * 0.00279375f +
            (c & 0xff) * 0.000282031f;
   }

   /**
    * Returns the color magenta, ( 1.0, 0.0, 1.0, 1.0 ) .
    *
    * @param target
    *           the output color
    * @return magenta
    */
   public static Color magenta ( final Color target ) {

      return target.set(1.0f, 0.0f, 1.0f, 1.0f);
   }

   /**
    * Finds the maximum color channel of a color, excluding
    * alpha.
    *
    * @param c
    *           the color
    * @return the maximum channel
    * @see Utils#max(float, float, float)
    */
   public static float maxRgb ( final Color c ) {

      return Utils.max(c.x, c.y, c.z);
   }

   /**
    * Finds the minimum color channel of a color, excluding
    * alpha.
    *
    * @param c
    *           the color
    * @return the minimum channel
    * @see Utils#min(float, float, float)
    */
   public static float minRgb ( final Color c ) {

      return Utils.min(c.x, c.y, c.z);
   }

   /**
    * Mixes two colors by a step in the range [0.0, 1.0]. Uses
    * the default mixing function.
    *
    * @param origin
    *           the origin color
    * @param dest
    *           the destination color
    * @param step
    *           the step
    * @param target
    *           the output color
    * @return the mixed color
    */
   public static Color mix (
         final Color origin,
         final Color dest,
         final float step,
         final Color target ) {

      return Color.mix(origin, dest, step, target, Color.EASING);
   }

   /**
    * Mixes two colors by a step in the range [0.0, 1.0]. Uses
    * the mixing function provided to the function.
    *
    * @param origin
    *           the origin color
    * @param dest
    *           the destination color
    * @param step
    *           the step
    * @param target
    *           the output color
    * @param easingFunc
    *           the easing function
    * @return the mixed color
    */
   public static Color mix (
         final Color origin,
         final Color dest,
         final float step,
         final Color target,
         final AbstrEasing easingFunc ) {

      return easingFunc.apply(origin, dest, step, target);
   }

   /**
    * Multiplies the left and right operand, except for the
    * alpha channel, then clamps the product to [0.0, 1.0] .
    * The left operand's alpha channel is retained.
    *
    * For that reason, color multiplication is <em>not</em>
    * commutative.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @param target
    *           output color
    * @return the product
    */
   public static Color mul (
         final Color a,
         final Color b,
         final Color target ) {

      return target.set(
            Utils.clamp01(a.x * b.x),
            Utils.clamp01(a.y * b.y),
            Utils.clamp01(a.z * b.z),
            Utils.clamp01(a.w));
   }

   /**
    * Multiplies the left and right operand, except for the
    * alpha channel, then clamps the product to [0.0, 1.0] .
    * The left operand's alpha channel is retained.
    *
    * For that reason, color multiplication is <em>not</em>
    * commutative.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @param target
    *           output color
    * @return the product
    */
   public static Color mul (
         final Color a,
         final float b,
         final Color target ) {

      return target.set(
            Utils.clamp01(a.x * b),
            Utils.clamp01(a.y * b),
            Utils.clamp01(a.z * b),
            Utils.clamp01(a.w));
   }

   /**
    * Multiplies the left and right operand, except for the
    * alpha channel, then clamps the product to [0.0, 1.0] .
    * The left operand is also supplied to the alpha channel.
    *
    * For that reason, color multiplication is <em>not</em>
    * commutative.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @param target
    *           output color
    * @return the product
    */
   public static Color mul (
         final float a,
         final Color b,
         final Color target ) {

      return target.set(
            Utils.clamp01(a * b.x),
            Utils.clamp01(a * b.y),
            Utils.clamp01(a * b.z),
            Utils.clamp01(a));
   }

   /**
    * Tests to see if the alpha channel of this color is less
    * than or equal to zero, i.e., if it is completely
    * transparent.
    *
    * @param c
    *           the color
    * @return the evaluation
    */
   public static boolean none ( final Color c ) {

      return c.w <= 0.0f;
   }

   /**
    * Multiplies the red, green and blue color channels of a
    * color by the alpha channel.
    *
    * @param c
    *           the input color
    * @param target
    *           the output color
    * @return the premultiplied color
    */
   public static Color preMul (
         final Color c,
         final Color target ) {

      if (c.w <= 0.0f) {
         return target.set(0.0f, 0.0f, 0.0f, 0.0f);
      } else if (c.w >= 1.0f) {
         return target.set(c.x, c.y, c.z, 1.0f);
      }

      return target.set(
            c.x * c.w,
            c.y * c.w,
            c.z * c.w,
            c.w);
   }

   /**
    * Reduces the signal, or granularity, of a color's
    * channels. Any level less than 2 or greater than 255
    * returns sets the target to the input.
    *
    * @param c
    *           the color
    * @param levels
    *           the levels
    * @param target
    *           the output color
    * @return the posterized color
    * @see Vec4#quantize(Vec4, int, Vec4)
    * @see Utils#floor(float)
    */
   public static Color quantize (
         final Color c,
         final int levels,
         final Color target ) {

      if (levels < 2 || levels > 255) {
         return target.set(c);
      }

      final float delta = 1.0f / levels;
      return target.set(
            delta * Utils.floor(0.5f + c.x * levels),
            delta * Utils.floor(0.5f + c.y * levels),
            delta * Utils.floor(0.5f + c.z * levels),
            delta * Utils.floor(0.5f + c.w * levels));
   }

   /**
    * Creates a random HSBA vector, then converts it to an RGBA
    * color. The alpha channel is not randomized.
    *
    * @param rng
    *           the random number generator
    * @param target
    *           the output color
    * @param hsba
    *           the output hsba vector
    * @return the color
    * @see Random#nextFloat()
    * @see Color#hsbaToRgba(Vec4, Color)
    */
   public static Color randomHsb (
         final Random rng,
         final Color target,
         final Vec4 hsba ) {

      hsba.set(
            rng.nextFloat(),
            rng.nextFloat(),
            rng.nextFloat(),
            1.0f);
      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Creates a random HSBA vector from a lower- and
    * upper-bound, then converts it to an RGBA color. The alpha
    * channel is not included.
    *
    * @param rng
    *           the random number generator
    * @param lowerBound
    *           the lower bound
    * @param upperBound
    *           the upper bound
    * @param target
    *           the output color
    * @param hsba
    *           the output hsba vector
    * @return the color
    * @see Random#uniform(float, float)
    * @see Color#hsbaToRgba(Vec4, Color)
    */
   public static Color randomHsb (
         final Random rng,
         final Vec4 lowerBound,
         final Vec4 upperBound,
         final Color target,
         final Vec4 hsba ) {

      hsba.set(
            rng.uniform(lowerBound.x, upperBound.x),
            rng.uniform(lowerBound.y, upperBound.y),
            rng.uniform(lowerBound.z, upperBound.z),
            1.0f);

      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Creates a random HSBA vector, then converts it to an RGBA
    * color. The alpha channel is randomized.
    *
    * @param rng
    *           the random number generator
    * @param target
    *           the output color
    * @param hsba
    *           the output hsba vector
    * @return the color
    * @see Random#nextFloat()
    * @see Color#hsbaToRgba(Vec4, Color)
    */
   public static Color randomHsba (
         final Random rng,
         final Color target,
         final Vec4 hsba ) {

      hsba.set(
            rng.nextFloat(),
            rng.nextFloat(),
            rng.nextFloat(),
            rng.nextFloat());
      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Creates a random HSBA vector from a lower- and
    * upper-bound, then converts it to an RGBA color. The alpha
    * channel is randomized.
    *
    * @param rng
    *           the random number generator
    * @param lowerBound
    *           the lower bound
    * @param upperBound
    *           the upper bound
    * @param target
    *           the output color
    * @param hsba
    *           the output hsba vector
    * @return the color
    * @see Random#uniform(float, float)
    * @see Color#hsbaToRgba(Vec4, Color)
    */
   public static Color randomHsba (
         final Random rng,
         final Vec4 lowerBound,
         final Vec4 upperBound,
         final Color target,
         final Vec4 hsba ) {

      hsba.set(
            rng.uniform(lowerBound.x, upperBound.x),
            rng.uniform(lowerBound.y, upperBound.y),
            rng.uniform(lowerBound.z, upperBound.z),
            rng.uniform(lowerBound.w, upperBound.w));

      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Creates a random color from red, green and blue channels.
    * The alpha channel is not included.
    *
    * @param rng
    *           the random number generator
    * @param target
    *           the output color
    * @return the color
    * @see Random#nextFloat()
    */
   public static Color randomRgb (
         final Random rng,
         final Color target ) {

      return target.set(
            rng.nextFloat(),
            rng.nextFloat(),
            rng.nextFloat(),
            1.0f);
   }

   /**
    * Creates a random color from a lower- and upper-bound. The
    * alpha channel is not included.
    *
    * @param rng
    *           the random number generator
    * @param lowerBound
    *           the lower bound
    * @param upperBound
    *           the upper bound
    * @param target
    *           the output color
    * @return the color
    * @see Random#uniform(float, float)
    */
   public static Color randomRgb (
         final Random rng,
         final Color lowerBound,
         final Color upperBound,
         final Color target ) {

      return target.set(
            rng.uniform(lowerBound.x, upperBound.x),
            rng.uniform(lowerBound.y, upperBound.y),
            rng.uniform(lowerBound.z, upperBound.z),
            1.0f);
   }

   /**
    * Creates a random color from red, green, blue and alpha
    * channels.
    *
    * @param rng
    *           the random number generator
    * @param target
    *           the output color
    * @return the color
    * @see Random#nextFloat()
    */
   public static Color randomRgba (
         final Random rng,
         final Color target ) {

      return target.set(
            rng.nextFloat(),
            rng.nextFloat(),
            rng.nextFloat(),
            rng.nextFloat());
   }

   /**
    * Creates a random color from a lower- and upper-bound.
    *
    * @param rng
    *           the random number generator
    * @param lowerBound
    *           the lower bound
    * @param upperBound
    *           the upper bound
    * @param target
    *           the output color
    * @return the color
    * @see Random#uniform(float, float)
    */
   public static Color randomRgba (
         final Random rng,
         final Color lowerBound,
         final Color upperBound,
         final Color target ) {

      return target.set(
            rng.uniform(lowerBound.x, upperBound.x),
            rng.uniform(lowerBound.y, upperBound.y),
            rng.uniform(lowerBound.z, upperBound.z),
            rng.uniform(lowerBound.w, upperBound.w));
   }

   /**
    * Returns the color red, ( 1.0, 0.0, 0.0, 1.0 ) .
    *
    * @param target
    *           the output color
    * @return red
    */
   public static Color red ( final Color target ) {

      return target.set(1.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Convert a color to grayscale based on its perceived
    * luminance.
    *
    * @param c
    *           the input color
    * @param target
    *           the output color
    * @return the grayscale color
    * @see Color#luminance(Color)
    */
   public static Color rgbaToGray (
         final Color c,
         final Color target ) {

      final float lum = Color.luminance(c);
      return target.set(lum, lum, lum, c.w);
   }

   /**
    * Converts a color to a vector which holds hue, saturation,
    * brightness and alpha.
    *
    * @param c
    *           the color
    * @param target
    *           the output vector
    * @return the HSBA vector
    */
   public static Vec4 rgbaToHsba (
         final Color c,
         final Vec4 target ) {

      return Color.rgbaToHsba(c.x, c.y, c.z, c.w, target);
   }

   /**
    * Converts RGBA channels to a vector which holds hue,
    * saturation, brightness and alpha.
    *
    * @param red
    *           the red channel
    * @param green
    *           the green channel
    * @param blue
    *           the blue channel
    * @param alpha
    *           the alpha channel
    * @param target
    *           the output vector
    * @return the HSBA values
    * @see Utils#max
    * @see Utils#min
    * @see IUtils#ONE_SIX
    */
   public static Vec4 rgbaToHsba (
         final float red,
         final float green,
         final float blue,
         final float alpha,
         final Vec4 target ) {

      final float bri = Utils.max(red, green, blue);
      final float mn = Utils.min(red, green, blue);
      final float delta = bri - mn;
      float hue = 0.0f;

      if (delta != 0.0f) {
         if (red == bri) {
            hue = (green - blue) / delta;
         } else if (green == bri) {
            hue = 2.0f + (blue - red) / delta;
         } else {
            hue = 4.0f + (red - green) / delta;
         }

         hue *= IUtils.ONE_SIX;
         if (hue < 0.0f) {
            hue += 1.0f;
         }
      }

      final float sat = bri == 0.0f ? 0.0f : delta / bri;
      return target.set(hue, sat, bri, alpha);
   }

   /**
    * Converts a color from RGB to CIE XYZ.
    *
    * @param c
    *           the color
    * @param target
    *           the output vector
    * @return the XYZ color
    */
   public static Vec4 rgbaToXyzw ( final Color c, final Vec4 target ) {

      return Color.rgbaToXyzw(c.x, c.y, c.z, c.w, target);
   }

   /**
    * Converts a color from RGB to CIE XYZ. References Pharr,
    * Jakob, and Humphreys'
    * <a href="http://www.pbr-book.org/">Physically Based
    * Rendering</a>.
    *
    * @param r
    *           the red component
    * @param g
    *           the green component
    * @param b
    *           the blue component
    * @param a
    *           the alpha component
    * @param target
    *           the output vector
    * @return the XYZ values.
    */
   public static Vec4 rgbaToXyzw (
         final float r,
         final float g,
         final float b,
         final float a,
         final Vec4 target ) {

      return target.set(
            0.412453f * r + 0.357580f * g + 0.180423f * b,
            0.212671f * r + 0.715160f * g + 0.072169f * b,
            0.019334f * r + 0.119193f * g + 0.950227f * b,
            a);
   }

   /**
    * Sets the easing function used to mix color.
    *
    * @param easing
    *           the easing function
    */
   public static void setEasing ( final AbstrEasing easing ) {

      if (easing != null) {
         Color.EASING = easing;
      }
   }

   /**
    * Shifts a color's brightness by a factor. The brightness
    * is clamped to the range [0.0, 1.0] .
    *
    * @param c
    *           the input color
    * @param shift
    *           the brightness shift
    * @param target
    *           the output color
    * @param hsba
    *           the color in HSB
    * @return the shifted color
    */
   public static Color shiftBri (
         final Color c,
         final float shift,
         final Color target,
         final Vec4 hsba ) {

      Color.rgbaToHsba(c, hsba);
      hsba.z = Utils.clamp01(hsba.z + shift);
      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Shifts a color's hue, saturation and brightness by a
    * vector. The color's alpha remains unaffected.
    *
    * @param c
    *           the input color
    * @param shift
    *           the shift
    * @param target
    *           the output color
    * @param hsba
    *           the color in HSB
    * @return the shifted color
    */
   public static Color shiftHsb (
         final Color c,
         final Vec4 shift,
         final Color target,
         final Vec4 hsba ) {

      /* HSBA to RGBA conversion takes care of modding the hue. */
      Color.rgbaToHsba(c, hsba);
      hsba.x += shift.x;
      hsba.y = Utils.clamp01(hsba.y + shift.y);
      hsba.z = Utils.clamp01(hsba.z + shift.z);
      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Shifts a color's hue, saturation and brightness by a
    * vector.
    *
    * @param c
    *           the input color
    * @param shift
    *           the shift
    * @param target
    *           the output color
    * @param hsba
    *           the color in HSB
    * @return the shifted color
    */
   public static Color shiftHsba (
         final Color c,
         final Vec4 shift,
         final Color target,
         final Vec4 hsba ) {

      /* HSBA to RGBA conversion takes care of modding the hue. */
      Color.rgbaToHsba(c, hsba);
      hsba.x += shift.x;
      hsba.y = Utils.clamp01(hsba.y + shift.y);
      hsba.z = Utils.clamp01(hsba.z + shift.z);
      hsba.w = Utils.clamp01(hsba.w + shift.w);
      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Shifts a color's hue by a factor. The hue wraps around
    * the range [0.0, 1.0] .
    *
    * @param c
    *           the input color
    * @param shift
    *           the hue shift
    * @param target
    *           the output color
    * @param hsba
    *           the color in HSB
    * @return the shifted color
    */
   public static Color shiftHue (
         final Color c,
         final float shift,
         final Color target,
         final Vec4 hsba ) {

      /* HSBA to RGBA conversion takes care of modding the hue. */
      Color.rgbaToHsba(c, hsba);
      hsba.x += shift;
      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Shifts a color's saturation by a factor. The saturation
    * is clamped to the range [0.0, 1.0] .
    *
    * @param c
    *           the input color
    * @param shift
    *           the saturation shift
    * @param target
    *           the output color
    * @param hsba
    *           the color in HSB
    * @return the shifted color
    */
   public static Color shiftSat (
         final Color c,
         final float shift,
         final Color target,
         final Vec4 hsba ) {

      Color.rgbaToHsba(c, hsba);
      hsba.y = Utils.clamp01(hsba.y + shift);
      return Color.hsbaToRgba(hsba, target);
   }

   /**
    * Subtracts the right operand from the left operand, except
    * for the alpha channel, then clamps the sum to [0.0, 1.0]
    * . The left operand's alpha channel is retained.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @param target
    *           output color
    * @return the difference
    */
   public static Color sub (
         final Color a,
         final Color b,
         final Color target ) {

      return target.set(
            Utils.clamp01(a.x - b.x),
            Utils.clamp01(a.y - b.y),
            Utils.clamp01(a.z - b.z),
            Utils.clamp01(a.w));
   }

   /**
    * Converts a color to an integer where hexadecimal
    * represents the ARGB color channels: 0xAARRGGB .
    *
    * @param c
    *           the input color
    * @return the color in hexadecimal
    */
   public static int toHexInt ( final Color c ) {

      return (int) (c.w * 0xff + 0.5f) << 0x18
            | (int) (c.x * 0xff + 0.5f) << 0x10
            | (int) (c.y * 0xff + 0.5f) << 0x8
            | (int) (c.z * 0xff + 0.5f);
   }

   /**
    * Converts a color to an integer where hexadecimal
    * represents the ARGB color channels: 0xAARRGGB .
    *
    * @param c
    *           the input color
    * @return the color in hexadecimal
    */
   public static long toHexLong ( final Color c ) {

      return Color.toHexInt(c) & 0xffffffffL;
   }

   /**
    * Returns a representation of the color as a hexadecimal
    * code, preceded by a '0x', in the format AARRGGBB.
    *
    * @param c
    *           the color
    * @return the string
    */
   public static String toHexString ( final Color c ) {

      return Color.toHexString(Color.toHexInt(c));
   }

   /**
    * Returns a Java-friendly representation of the color as a
    * hexadecimal code, preceded by a '0x', in the format
    * AARRGGBB.
    *
    * @param c
    *           the color
    * @return the string
    * @see Integer#toHexString(int)
    */
   public static String toHexString ( final int c ) {

      return "0x" + Integer.toHexString(c);
   }

   /**
    * Returns a web-friendly representation of the color as a
    * hexadecimal code, preceded by a hashtag, '#', with no
    * alpha.
    *
    * @param c
    *           the color
    * @return the string
    */
   public static String toHexWeb ( final Color c ) {

      return Color.toHexWeb(Color.toHexInt(c));
   }

   /**
    * Returns a web-friendly representation of the color as a
    * hexadecimal code, preceded by a hashtag, '#', with no
    * alpha.
    *
    * @param c
    *           the color
    * @return the string
    * @see Integer#toHexString(int)
    */
   public static String toHexWeb ( final int c ) {

      return "#" + Integer
            .toHexString(c)
            .substring(2)
            .toUpperCase();
   }

   /**
    * Returns the color white, ( 1.0, 1.0, 1.0, 1.0 ) .
    *
    * @param target
    *           the output color
    * @return white
    */
   public static Color white ( final Color target ) {

      return target.set(1.0f, 1.0f, 1.0f, 1.0f);
   }

   /**
    * Converts a color from CIE XYZ to RGB. References Pharr,
    * Jakob, and Humphreys'
    * <a href="http://www.pbr-book.org/">Physically Based
    * Rendering</a>.
    *
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    * @param z
    *           the z coordinate
    * @param a
    *           the alpha component
    * @param target
    *           the output color
    * @return the color
    */
   public static Color xyzaToRgba (
         final float x,
         final float y,
         final float z,
         final float a,
         final Color target ) {

      return target.set(
            3.240479f * x - 1.537150f * y - 0.498535f * z,
            -0.969256f * x + 1.875991f * y + 0.041556f * z,
            0.055648f * x - 0.204043f * y + 1.057311f * z,
            a);
   }

   /**
    * Converts a color from CIE XYZ to RGB.
    *
    * @param v
    *           the XYZ vector
    * @param target
    *           the output color
    * @return the color
    */
   public static Color xyzaToRgba (
         final Vec4 v,
         final Color target ) {

      return Color.xyzaToRgba(v.x, v.y, v.z, v.w, target);
   }

   /**
    * Returns the color yellow, ( 1.0, 1.0, 0.0, 1.0 ) .
    *
    * @param target
    *           the output color
    * @return yellow
    */
   public static Color yellow ( final Color target ) {

      return target.set(1.0f, 1.0f, 0.0f, 1.0f);
   }

   /**
    * The default constructor. Creates a white color.
    */
   public Color () {

      super(1.0f, 1.0f, 1.0f, 1.0f);
   }

   /**
    * Creates a color from bytes. In Jave, bytes are signed,
    * within the range [-128, 127] .
    *
    * @param red
    *           the red channel
    * @param green
    *           the green channel
    * @param blue
    *           the blue channel
    */
   public Color (
         final byte red,
         final byte green,
         final byte blue ) {

      super();
      this.set(red, green, blue);
   }

   /**
    * Creates a color from bytes. In Jave, bytes are signed,
    * within the range [-128, 127] .
    *
    * @param red
    *           the red channel
    * @param green
    *           the green channel
    * @param blue
    *           the blue channel
    * @param alpha
    *           the alpha channel
    */
   public Color (
         final byte red,
         final byte green,
         final byte blue,
         final byte alpha ) {

      super();
      this.set(red, green, blue, alpha);
   }

   /**
    * Creates a color from a source.
    *
    * @param c
    *           the source color
    */
   public Color ( final Color c ) {

      super();
      this.set(c);
   }

   /**
    * Creates a color out of red, green and blue channels. The
    * alpha channel defaults to 1.0 .
    *
    * @param red
    *           the red channel
    * @param green
    *           the green channel
    * @param blue
    *           the blue channel
    */
   public Color (
         final float red,
         final float green,
         final float blue ) {

      super(red, green, blue, 1.0f);
   }

   /**
    * Creates a color out of red, green, blue and alpha
    * channels.
    *
    * @param red
    *           the red channel
    * @param green
    *           the green channel
    * @param blue
    *           the blue channel
    * @param alpha
    *           the alpha channel
    */
   public Color (
         final float red,
         final float green,
         final float blue,
         final float alpha ) {

      super(red, green, blue, alpha);
   }

   /**
    * Returns a String of Python code targeted toward the
    * Blender 2.8x API. This code is brittle and is used for
    * internal testing purposes, i.e., to compare how curve
    * geometry looks in Blender (the control) vs. in the
    * library (the test).
    *
    * @return the string
    */
   String toBlenderCode () {

      return this.toBlenderCode(1.0f, true);
   }

   /**
    * Returns a String of Python code targeted toward the
    * Blender 2.8x API. This code is brittle and is used for
    * internal testing purposes, i.e., to compare how curve
    * geometry looks in Blender (the control) vs. in the
    * library (the test).<br>
    * <br>
    * Formatted as a tuple where red, green and blue channels
    * have been raised to the power of gamma, usually 2.2. If
    * include alpha is true, then the alpha is also included.
    *
    * @param gamma
    *           the exponent
    * @param inclAlpha
    *           include the alpha channel
    * @return the string
    */
   String toBlenderCode (
         final float gamma,
         final boolean inclAlpha ) {

      final StringBuilder sb = new StringBuilder(96)
            .append('(')
            .append(Utils.toFixed((float) Math.pow(this.x, gamma), 6))
            .append(',').append(' ')
            .append(Utils.toFixed((float) Math.pow(this.y, gamma), 6))
            .append(',').append(' ')
            .append(Utils.toFixed((float) Math.pow(this.z, gamma), 6));

      if (inclAlpha) {
         sb.append(',')
               .append(' ')
               .append(Utils.toFixed(this.w, 6));
      }

      sb.append(')');
      return sb.toString();
   }

   /**
    * Returns a String representation of the color compatible
    * with .ggr (Gimp gradient) file formats. Each channel,
    * including alpha, is represented as a float in [0.0, 1.0]
    * separated by a space.
    *
    * @return the string
    */
   String toGgrString () {

      return new StringBuilder(96)
            .append(Utils.toFixed(this.x, 6)).append(' ')
            .append(Utils.toFixed(this.y, 6)).append(' ')
            .append(Utils.toFixed(this.z, 6)).append(' ')
            .append(Utils.toFixed(this.w, 6))
            .toString();
   }

   /**
    * Returns a String representation of the color compatible
    * with .gpl (Gimp palette) file formats. Each channel,
    * including alpha, is represented an unsigned byte in [0,
    * 255] separated by a space.
    *
    * @return the string
    */
   String toGplString () {

      final int r = (int) (this.x * 0xff + 0.5f);
      final int g = (int) (this.y * 0xff + 0.5f);
      final int b = (int) (this.z * 0xff + 0.5f);

      final StringBuilder result = new StringBuilder(32);

      // if (r < 100 && r > 9) {
      // sb.append(' ');
      // } else if (r < 10) {
      // sb.append(" ");
      // }
      // sb.append(r)
      // .append(g > 99 ? ' ' : g > 9 ? " " : " ")
      // .append(g)
      // .append(b > 99 ? ' ' : b > 9 ? " " : " ")
      // .append(b);

      result.append(r)
            .append(' ')
            .append(g)
            .append(' ')
            .append(b);

      return result.toString();
   }

   /**
    * Tests equivalence between this and another color.
    * Converts both to hexadecimal integers.
    *
    * @param c
    *           the color
    * @return the evaluation
    * @see Color#toHexInt(Color)
    */
   protected boolean equals ( final Color c ) {

      return Color.toHexInt(this) == Color.toHexInt(c);
   }

   /**
    * Gets the alpha channel.
    *
    * @return the alpha channel
    */
   public float a () {

      return this.w;
   }

   /**
    * Sets the alpha channel.
    *
    * @param alpha
    *           the alpha channel
    * @return this color
    */
   public Color a ( final byte alpha ) {

      this.w = (alpha & 0xff) * IUtils.ONE_255;
      return this;
   }

   /**
    * Sets the alpha channel.
    *
    * @param alpha
    *           the alpha channel
    * @return this color
    */
   public Color a ( final float alpha ) {

      this.w = alpha;
      return this;
   }

   /**
    * Gets the blue channel.
    *
    * @return the blue channel
    */
   public float b () {

      return this.z;
   }

   /**
    * Sets the blue channel.
    *
    * @param blue
    *           the blue channel
    * @return this color
    */
   public Color b ( final byte blue ) {

      this.z = (blue & 0xff) * IUtils.ONE_255;
      return this;
   }

   /**
    * Sets the blue channel.
    *
    * @param blue
    *           the blue channel
    * @return this color
    */
   public Color b ( final float blue ) {

      this.z = blue;
      return this;
   }

   /**
    * Returns a new color with this color's components. Java's
    * cloneable interface is problematic; use set or a copy
    * constructor instead.
    *
    * @return a new color
    * @see Color#set(Color)
    * @see Color#Color(Color)
    */
   @Override
   public Color clone () {

      return new Color(this.x, this.y, this.z, this.w);
   }

   /**
    * Returns -1 when this color is less than the comparisand;
    * 1 when it is greater than; 0 when the two are 'equal'.
    * The implementation of this method allows collections of
    * colors to be sorted.
    *
    * @param c
    *           the comparisand
    * @return the numeric code
    */
   public int compareTo ( final Color c ) {

      final int a = Color.toHexInt(this);
      final int b = Color.toHexInt(c);
      return a > b ? 1 : a < b ? -1 : 0;
   }

   /**
    * Tests this color for equivalence to another based on its
    * hexadecimal representation.
    *
    * @param other
    *           the color integer
    * @return the equivalence
    * @see Color#toHexInt(Color)
    */
   public boolean equals ( final int other ) {

      return Color.toHexInt(this) == other;
   }

   /**
    * Tests this color for equivalence with another object.
    *
    * @param obj
    *           the object
    * @return the equivalence
    * @see Color#equals(Color)
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

      return this.equals((Color) obj);
   }

   /**
    * Gets the green channel.
    *
    * @return the green channel
    */
   public float g () {

      return this.y;
   }

   /**
    * Sets the green channel.
    *
    * @param green
    *           the green channel
    * @return this color
    */
   public Color g ( final byte green ) {

      this.y = (green & 0xff) * IUtils.ONE_255;
      return this;
   }

   /**
    * Sets the green channel.
    *
    * @param green
    *           the green channel
    * @return this color
    */
   public Color g ( final float green ) {

      this.y = green;
      return this;
   }

   /**
    * Simulates bracket subscript access in an array.
    *
    * @param index
    *           the index
    * @return the element
    */
   @Override
   public float get ( final int index ) {

      return this.getAlphaLast(index);
   }

   /**
    * Simulates bracket access in an array. The alpha channel
    * is treated as the first channel.
    *
    * @param index
    *           the index
    * @return the element
    */
   public float getAlphaFirst ( final int index ) {

      switch (index) {
         case 0:
         case -4:
            return this.w;
         case 1:
         case -3:
            return this.x;
         case 2:
         case -2:
            return this.y;
         case 3:
         case -1:
            return this.z;
         default:
            return 0.0f;
      }
   }

   /**
    * Simulates bracket access in an array. The alpha channel
    * is treated as the last channel.
    *
    * @param index
    *           the index
    * @return the element
    */
   public float getAlphaLast ( final int index ) {

      switch (index) {
         case 0:
         case -4:
            return this.x;
         case 1:
         case -3:
            return this.y;
         case 2:
         case -2:
            return this.z;
         case 3:
         case -1:
            return this.w;
         default:
            return 0.0f;
      }
   }

   /**
    * Returns a hash code for this color based on its
    * hexadecmal value.
    *
    * @return the hash code
    * @see Float#floatToIntBits(float)
    */
   @Override
   public int hashCode () {

      return Color.toHexInt(this);
   }

   /**
    * Gets the red channel.
    *
    * @return the red channel
    */
   public float r () {

      return this.x;
   }

   /**
    * Sets the red channel.
    *
    * @param red
    *           the red channel
    * @return this color
    */
   public Color r ( final byte red ) {

      this.x = (red & 0xff) * IUtils.ONE_255;
      return this;
   }

   /**
    * Sets the red channel.
    *
    * @param red
    *           the red channel
    * @return this color
    */
   public Color r ( final float red ) {

      this.x = red;
      return this;
   }

   /**
    * Resets this color to the color white.
    *
    * @return this color
    * @see Color#white(Color)
    */
   @Override
   @Chainable
   public Color reset () {

      return Color.white(this);
   }

   /**
    * Sets a color with bytes. In Jave, bytes are signed,
    * within the range [-128, 127] .
    *
    * @param red
    *           the red channel
    * @param green
    *           the green channel
    * @param blue
    *           the blue channel
    * @return this color
    */
   public Color set (
         final byte red,
         final byte green,
         final byte blue ) {

      return this.set(red, green, blue, -1);
   }

   /**
    * Sets a color with bytes. In Jave, bytes are signed,
    * within the range [-128, 127] .
    *
    * @param red
    *           the red channel
    * @param green
    *           the green channel
    * @param blue
    *           the blue channel
    * @param alpha
    *           the alpha channel
    * @return this color
    */
   public Color set (
         final byte red,
         final byte green,
         final byte blue,
         final byte alpha ) {

      super.set(
            IUtils.ONE_255 * (red & 0xff),
            IUtils.ONE_255 * (green & 0xff),
            IUtils.ONE_255 * (blue & 0xff),
            IUtils.ONE_255 * (alpha & 0xff));
      return this;
   }

   /**
    * Sets this color to the source color.
    *
    * @param c
    *           the source color
    * @return this color
    */
   @Chainable
   public Color set ( final Color c ) {

      return this.set(c.x, c.y, c.z, c.w);
   }

   /**
    * Sets the red, green and blue color channels of this
    * color. The alpha channel is set to 1.0 by default.
    *
    * @param red
    *           the red channel
    * @param green
    *           the green channel
    * @param blue
    *           the blue channel
    * @return this color
    */
   @Chainable
   public Color set (
         final float red,
         final float green,
         final float blue ) {

      return this.set(red, green, blue, 1.0f);
   }

   /**
    * Overrides the parent set function for the sake of making
    * RGB parameters clearer and for chainability.
    *
    * @param red
    *           the red channel
    * @param green
    *           the green channel
    * @param blue
    *           the blue channel
    * @param alpha
    *           the alpha channel
    * @return this color
    */
   @Override
   @Chainable
   public Color set (
         final float red,
         final float green,
         final float blue,
         final float alpha ) {

      super.set(red, green, blue, alpha);
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
   @Override
   public String toString ( final int places ) {

      return new StringBuilder(96)
            .append("{ r: ")
            .append(Utils.toFixed(this.x, places))
            .append(", g: ")
            .append(Utils.toFixed(this.y, places))
            .append(", b: ")
            .append(Utils.toFixed(this.z, places))
            .append(", a: ")
            .append(Utils.toFixed(this.w, places))
            .append(' ').append('}')
            .toString();
   }
}
