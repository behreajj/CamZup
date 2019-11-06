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
         implements Utils.EasingFuncObject < Color > {

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
         return Float.compare(this.aHsb.z, this.bHsb.z);
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
         return Float.compare(this.aHsb.x, this.bHsb.x);
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
         return Float.compare(this.aHsb.y, this.bHsb.y);
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

         this(new HueNear(), new Utils.Lerp(), new Utils.Lerp());
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

         final double td = step;
         final double ts = td * td * (3.0d - (td + td));
         final double us = 1.0d - ts;
         return target.set((float) (us * origin.x + ts * dest.x),
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
    * The default color comparator, by hue.
    */
   public static Comparator < Color > COMPARATOR = new ComparatorHue();

   /**
    * Converts two colors to integers, performs the bitwise AND
    * operation on them, then converts them back to colors.
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
    * operation on it, then converts them back to colors.
    *
    * @param a
    *           the color
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
    * operation (inclusive or) on them, then converts them back
    * to colors.
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
    * shift operation, then converts it back to a color. The
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
    * shift operation, then converts it back to a color. The
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
    * bitwise right shift operation, then converts it back to a
    * color. The number of places is multiplied by 0x08.
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
    * operation (exclusive or) on them, then converts them back
    * to colors.
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
    * range [0, 1].
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

      float r = 0.5f;
      float g = 0.5f;

      final float mSq = Vec2.magSq(v);

      if (mSq == 0.0f) {
         return target.set(0.5f, 0.5f, 0.5f, 1.0f);
      }

      if (Utils.approxFast(mSq, 1.0f)) {
         r = v.x * 0.5f + 0.5f;
         g = v.y * 0.5f + 0.5f;
      } else {
         final float mInv = (float) (0.5d / Math.sqrt(mSq));
         r = v.x * mInv + 0.5f;
         g = v.y * mInv + 0.5f;
      }

      return target.set(r, g, 0.5f, 1.0f);
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

      float r = 0.5f;
      float g = 0.5f;
      float b = 0.5f;

      final float mSq = Vec3.magSq(v);

      if (mSq == 0.0f) {
         return target.set(0.5f, 0.5f, 0.5f, 1.0f);
      }

      if (Utils.approxFast(mSq, 1.0f)) {
         r = v.x * 0.5f + 0.5f;
         g = v.y * 0.5f + 0.5f;
         b = v.z * 0.5f + 0.5f;
      } else {
         final float mInv = (float) (0.5d / Math.sqrt(mSq));
         r = v.x * mInv + 0.5f;
         g = v.y * mInv + 0.5f;
         b = v.z * mInv + 0.5f;
      }

      return target.set(r, g, b, 1.0f);
   }

   /**
    * Convert a hexadecimal representation of a color stored as
    * ARGB into a color.
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
    * Gets the name of the comparator function used to sort
    * colors.
    *
    * @return the comparator
    */
   public static String getComparatorString () {

      return Color.COMPARATOR.toString();
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

      if (sat <= 0.0) {
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
    * @param hbsa
    *           the HSBA vector
    * @param target
    *           the output color
    * @return the color
    */
   public static Color hsbaToRgba (
         final Vec4 hbsa,
         final Color target ) {

      return Color.hsbaToRgba(hbsa.x, hbsa.y, hbsa.z, hbsa.w, target);
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
    * Raises each component of the color to a power. Useful for
    * gamma adjustment.
    *
    * @param a
    *           the color
    * @param b
    *           the power
    * @param target
    *           the output color
    * @return the adjusted color
    * @see Math#pow(double, double)
    */
   public static Color pow (
         final Color a,
         final float b,
         final Color target ) {

      return target.set(
            (float) Math.pow(a.x, b),
            (float) Math.pow(a.y, b),
            (float) Math.pow(a.z, b),
            a.w);
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
   public static Color preMult (
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
      Color.hsbaToRgba(hsba, target);
      return target;
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

      Color.hsbaToRgba(hsba, target);
      return target;
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
      Color.hsbaToRgba(hsba, target);
      return target;
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

      Color.hsbaToRgba(hsba, target);
      return target;
   }

   /**
    * Creates a random color. The alpha channel is not
    * included.
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
    * Creates a random color. The alpha channel is randomized.
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
    * Creates a random color from a lower- and upper-bound. The
    * alpha channel is randomized.
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
    * Converts rgba channels to a vector which holds hue,
    * saturation, brightness and alpha.
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
    * Converts rgba channels to a vector which holds hue,
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

      final float sat = bri == 0.0f ? 0.0f : (bri - mn) / bri;
      return target.set(hue, sat, bri, alpha);
   }

//   public static Vec4 rgbaToXyza ( final Color c, final Vec4 target ) {

//      return Color.rgbaToXyza(c.x, c.y, c.z, c.w, target);
//   }

//   public static Vec4 rgbaToXyza (
//         final float r,
//         final float g,
//         final float b,
//         final float a,
//         final Vec4 target ) {

//      return target.set(
//            0.412453f * r + 0.357580f * g + 0.180423f * b,
//            0.212671f * r + 0.715160f * g + 0.072169f * b,
//            0.019334f * r + 0.119193f * g + 0.950227f * b,
//            a);
//   }

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

      return "#" + Integer.toHexString(c).substring(2);
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

//   public static Color xyzaToRgba (
//         final float x,
//         final float y,
//         final float z,
//         final float a,
//         final Color target ) {

//      return target.set(
//            3.240479f * x - 1.537150f * y - 0.498535f * z,
//            -0.969256f * x + 1.875991f * y + 0.041556f * z,
//            0.055648f * x - 0.204043f * y + 1.057311f * z,
//            a);
//   }

//   public static Color xyzaToRgba ( final Vec4 v, final Color target ) {

//      return Color.xyzaToRgba(v.x, v.y, v.z, v.w, target);
//   }

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
    * colors to be sorted. This depends upon the static
    * comparator of the Color class, which can be changed.
    *
    * @param c
    *           the comparisand
    * @return the numeric code
    * @see Color#COMPARATOR
    */
   public int compareTo ( final Color c ) {

      return Color.COMPARATOR.compare(this, c);
   }

   /**
    * Compares this color to a color stored in an integer as a
    * hexadecimal value.
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
      return Color.toHexInt(this) == Color.toHexInt((Color) obj);
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
    * Returns a hash code for this color based on its red,
    * green, blue and alpha channels.
    *
    * @return the hash code
    * @see Float#floatToIntBits(float)
    */
   @Override
   public int hashCode () {

      final int prime = 31;
      int result = 1;
      result = prime * result + Float.floatToIntBits(this.w);
      result = prime * result + Float.floatToIntBits(this.z);
      result = prime * result + Float.floatToIntBits(this.y);
      result = prime * result + Float.floatToIntBits(this.x);
      return result;
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
            .append(" }")
            .toString();
   }
}
