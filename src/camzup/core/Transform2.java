package camzup.core;

import camzup.core.Utils.EasingFuncArr;

/**
 * Facilitates 3D affine transformations for entities.
 */
public class Transform2 extends Transform {

   public static class Easing implements EasingFuncArr < Transform2 > {

      public Vec2.AbstrEasing loc;

      public Utils.PeriodicEasing rot;

      public Vec2.AbstrEasing scale;

      public Easing () {

         this.loc = new Vec2.Lerp();
         this.rot = new Utils.LerpNear(IUtils.TAU);
         this.scale = new Vec2.Lerp();
      }

      public Easing (
            final Vec2.AbstrEasing locEasing,
            final Utils.PeriodicEasing rotEasing,
            final Vec2.AbstrEasing scaleEasing ) {

         this.loc = locEasing;
         this.rot = rotEasing;
         this.scale = scaleEasing;
      }

      public Transform2 apply (
            final Transform2 origin,
            final Transform2 dest,
            final Float step,
            final Transform2 target ) {

         if (step <= 0.0f) {
            return target.set(origin);
         }

         if (step >= 1.0f) {
            return target.set(dest);
         }

         return this.applyUnclamped(origin, dest, step, target);
      }

      @Override
      public Transform2 apply (
            final Transform2[] arr,
            final Float step,
            final Transform2 target ) {

         final int len = arr.length;
         if (len == 1 || step <= 0.0f) {
            return target.set(arr[0]);
         }

         if (step >= 1.0f) {
            return target.set(arr[len - 1]);
         }

         final float scaledStep = step * (len - 1);
         final int i = (int) scaledStep;
         final float nextStep = scaledStep - i;
         return this.applyUnclamped(
               arr[i], arr[i + 1],
               nextStep,
               target);
      }

      public Transform2 applyUnclamped (
            final Transform2 origin,
            final Transform2 dest,
            final float step,
            final Transform2 target ) {

         target.locPrev.set(target.location);
         this.loc.applyUnclamped(
               origin.location,
               dest.location,
               step,
               target.location);

         target.rotPrev = target.rotation;
         target.rotation = this.rot.applyUnclamped(
               origin.rotation,
               dest.rotation,
               step);

         target.scalePrev.set(target.scale);
         this.loc.applyUnclamped(
               origin.scale,
               dest.scale,
               step,
               target.scale);

         return target;
      }
      
      public String toString () {

         return this.getClass().getSimpleName();
      }
   }

   private static Easing EASING = new Easing();

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = -4460673884822918485l;

   /**
    * Creates a transform from two axes and a translation.
    *
    * @param right
    *           the right axis
    * @param forward
    *           the forward axis
    * @param translation
    *           the translation
    * @param target
    *           the output transform
    * @return the transform
    */
   public static Transform2 fromAxes (
         final Vec2 right,
         final Vec2 forward,
         final Vec2 translation,
         final Transform2 target ) {

      target.moveTo(translation);

      Vec2.normalize(right, target.right);
      Vec2.normalize(forward, target.forward);

      final float a = target.right.x;
      final float c = target.right.y;

      target.rotPrev = target.rotation;
      target.rotation = (float) Math.atan2(c, a);
      target.cosa = a;
      target.sina = c;

      target.scaleTo(1.0f);

      return target;
   }

   public static String getEasingString () {

      return Transform2.EASING.toString();
   }

   /**
    * Sets the transform to an identity configuration.
    *
    * @param target
    *           the output transform
    * @return the identity
    */
   public static Transform2 identity ( final Transform2 target ) {

      return target.set(0.0f, 0.0f, 0.0f, 1.0f, 1.0f);
   }

   /**
    * Finds the difference between the current and previous
    * location of a transform.
    *
    * @param t
    *           the transform
    * @param target
    *           the output vector
    * @return the location delta
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    */
   public static Vec2 locDelta ( final Transform2 t, final Vec2 target ) {

      return Vec2.sub(t.location, t.locPrev, target);
   }

   /**
    * Returns the maximum dimension occupied by a transform.
    *
    * @param t
    *           the transform
    * @return the maximum dimension
    * @see Utils#max(float, float)
    */
   public static float maxDimension ( final Transform2 t ) {

      return Utils.max(t.scale.x, t.scale.y);
   }

   /**
    * Returns the minimum dimension occupied by a transform.
    *
    * @param t
    *           the transform
    * @return the minimum dimension
    * @see Utils#min(float, float)
    */
   public static float minDimension ( final Transform2 t ) {

      return Utils.min(t.scale.x, t.scale.y);
   }

   public static Transform2 mix (
         final Transform2 origin,
         final Transform2 dest,
         final float step,
         final Transform2 target ) {

      return Transform2.EASING.apply(
            origin, dest, step, target);
   }
   
   /**
    * Multiplies a direction by a transform. This rotates the
    * direction by the transform's rotation.
    *
    * @param t
    *           the transform
    * @param source
    *           the input direction
    * @param target
    *           the output direction
    * @return the direction
    * @see Vec2#rotateZ(Vec2, float, Vec2)
    */
   public static Vec2 multDir (
         final Transform2 t,
         final Vec2 source,
         final Vec2 target ) {

      Vec2.rotateZ(source, t.rotation, target);
      return target;
   }

   /**
    * Multiplies a point by a transform. This rotates the
    * point, multiplies the point by the scale, then adds the
    * translation.
    *
    * @param t
    *           the transform
    * @param source
    *           the input point
    * @param target
    *           the output point
    * @return the point
    * @see Vec2#rotateZ(Vec2, float, Vec2)
    */
   public static Vec2 multPoint (
         final Transform2 t,
         final Vec2 source,
         final Vec2 target ) {

      Vec2.rotateZ(source, t.rotation, target);
      Vec2.mult(target, t.scale, target);
      Vec2.add(target, t.location, target);

      return target;
   }

   /**
    * Multiplies a vector by a transform. This rotates the
    * vector by the transform's rotation and then multiplies it
    * by the transform's scale.
    *
    * @param t
    *           the transform
    * @param source
    *           the input vector
    * @param target
    *           the output vector
    * @return the vector
    * @see Vec2#rotateZ(Vec2, float, Vec2)
    */
   public static Vec2 multVector (
         final Transform2 t,
         final Vec2 source,
         final Vec2 target ) {

      Vec2.rotateZ(source, t.rotation, target);
      Vec2.mult(target, t.scale, target);

      return target;
   }

   /**
    * Finds the difference between the current and previous
    * rotation of a transform.
    *
    * @param t
    *           the transform
    * @return the rotation delta
    */
   public static float rotDelta ( final Transform2 t ) {

      return t.rotation - t.rotPrev;
   }

   /**
    * Finds the difference between the current and previous
    * scale of a transform.
    *
    * @param t
    *           the transform
    * @param target
    *           the output vector
    * @return the scale delta
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    */
   public static Vec2 scaleDelta (
         final Transform2 t,
         final Vec2 target ) {

      return Vec2.sub(t.scale, t.scalePrev, target);
   }

   public static void setEasing ( final Easing easing ) {

      if (easing != null) {
         Transform2.EASING = easing;
      }
   }

   /**
    * Stores the result of cos ( rotation ) when calculating
    * the transform's right and forward axes.
    */
   protected float cosa = 0.0f;

   /**
    * The transform's forward axis.
    */
   protected final Vec2 forward = Vec2.forward(new Vec2());

   /**
    * The transform's location.
    */
   protected final Vec2 location = Vec2.zero(new Vec2());

   /**
    * The previous location. Subtract from the current location
    * to find the delta, or change, in location.
    */
   protected final Vec2 locPrev = Vec2.zero(new Vec2());

   /**
    * The transform's right axis.
    */
   protected final Vec2 right = Vec2.right(new Vec2());

   /**
    * The transform's rotation.
    */
   protected float rotation = 0.0f;

   /**
    * The previous rotation. Subtract from the current rotation
    * to find the delta, or change, in rotation.
    */
   protected float rotPrev = 0.0f;

   /**
    * The transform's scale.
    */
   protected final Vec2 scale = Vec2.one(new Vec2());

   /**
    * The previous scale. Subtract from the current scale to
    * find the delta, or change, in scale.
    */
   protected final Vec2 scalePrev = Vec2.one(new Vec2());

   /**
    * Stores the result of sin ( rotation ) when calculating
    * the transform's right and forward axes.
    */
   protected float sina = 0.0f;

   /**
    * The default constructor.
    */
   public Transform2 () {

      super();
   }

   /**
    * Creates a transform from loose real numbers.
    *
    * @param xLoc
    *           the x location
    * @param yLoc
    *           the y location
    * @param radians
    *           the rotation
    * @param xScale
    *           the scale x
    * @param yScale
    *           the scale y
    */
   public Transform2 (
         final float xLoc,
         final float yLoc,

         final float radians,

         final float xScale,
         final float yScale ) {

      super();
      this.set(
            xLoc, yLoc,
            radians,
            xScale, yScale);
   }

   /**
    * Creates a new transform from a source.
    *
    * @param source
    *           the source transform
    */
   public Transform2 ( final Transform2 source ) {

      super();
      this.set(source);
   }

   /**
    * Creates a transform from a location, rotation and scale.
    *
    * @param location
    *           the location
    * @param rotation
    *           the rotation
    * @param scale
    *           the scale
    */
   public Transform2 (
         final Vec2 location,
         final float rotation,
         final Vec2 scale ) {

      super();
      this.set(location, rotation, scale);
   }

   /**
    * Tests the equivalence between this and another transform.
    *
    * @param t
    *           the transform
    * @return the evaluation
    */
   protected boolean equals ( final Transform2 t ) {

      if (this.scale == null) {
         if (t.scale != null) {
            return false;
         }
      } else if (!this.scale.equals(t.scale)) {
         return false;
      }
      if (this.location == null) {
         if (t.location != null) {
            return false;
         }
      } else if (!this.location.equals(t.location)) {
         return false;
      }
      if (Float.floatToIntBits(this.rotation) != Float
            .floatToIntBits(t.rotation)) {
         return false;
      }
      return true;
   }

   /**
    * Updates the local axes of the transform based on its
    * rotation.
    *
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   @Override
   protected void updateAxes () {

      this.cosa = (float) Math.cos(this.rotation);
      this.sina = (float) Math.sin(this.rotation);
      this.right.set(this.cosa, this.sina);
      this.forward.set(-this.sina, this.cosa);
   }

   /**
    * Creates a new transform with the components of this
    * transform.
    *
    * @return the cloned transform
    */
   @Override
   public Transform2 clone () {

      return new Transform2(this.location, this.rotation, this.scale);
   }

   /**
    * Tests this transform for equivalence with an object.
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
      return this.equals((Transform2) obj);
   }

   /**
    * Gets the transform's forward axis.
    *
    * @param target
    *           the output vector
    * @return the forward axis
    */
   public Vec2 getForward ( final Vec2 target ) {

      return target.set(this.forward);
   }

   /**
    * Gets the transform's location
    *
    * @param target
    *           the output vector
    * @return the location
    */
   public Vec2 getLocation ( final Vec2 target ) {

      return target.set(this.location);
   }

   /**
    * Gets the transform's previous location
    *
    * @param target
    *           the output vector
    * @return the previous location
    */
   public Vec2 getLocPrev ( final Vec2 target ) {

      return target.set(this.locPrev);
   }

   /**
    * Gets the transform's right axis.
    *
    * @param target
    *           the output vector
    * @return the right axis
    */
   public Vec2 getRight ( final Vec2 target ) {

      return target.set(this.right);
   }

   /**
    * Gets the transform's rotation.
    *
    * @return the rotation
    */
   public float getRotation () {

      return this.rotation;
   }

   /**
    * Gets the transform's previous rotation.
    *
    * @return the previous rotation
    */
   public float getRotPrev () {

      return this.rotPrev;
   }

   /**
    * Gets the transform's scale.
    *
    * @param target
    *           the output vector
    * @return the scale
    */
   public Vec2 getScale ( final Vec2 target ) {

      return target.set(this.scale);
   }

   // public Transform2 lookAt ( final Vec2 dest, final Vec2
   // dir ) {
   //
   // Vec2.lookAt(this.location, dest,
   // this.right, this.forward, dir);
   // this.rotPrev = this.rotation;
   // this.rotation = Vec2.headingSigned(this.right);
   // return this;
   // }
   //
   // public Transform2 lookForward ( final Vec2 dir ) {
   //
   // Vec2.lookForward(dir, this.right, this.forward);
   // this.rotPrev = this.rotation;
   // this.rotation = Vec2.headingSigned(this.right);
   // return this;
   // }
   //
   // public Transform2 lookRight ( final Vec2 dir ) {
   //
   // Vec2.lookRight(dir, this.right, this.forward);
   // this.rotPrev = this.rotation;
   // this.rotation = Vec2.headingSigned(this.right);
   // return this;
   // }

   /**
    * Gets the transform's previous scale.
    *
    * @param target
    *           the output vector
    * @return the previous scale
    */
   public Vec2 getScalePrev ( final Vec2 target ) {

      return target.set(this.scalePrev);
   }

   /**
    * Returns a hash code for this transform based on its
    * location, rotation and scale.
    *
    * @return the hash code
    * @see Float#floatToIntBits(float)
    * @see Vec2#hashCode()
    */
   @Override
   public int hashCode () {

      final int prime = 31;
      int result = 1;
      result = prime * result
            + (this.scale == null ? 0 : this.scale.hashCode());
      result = prime * result
            + (this.location == null ? 0 : this.location.hashCode());
      result = prime * result + Float.floatToIntBits(this.rotation);
      return result;
   }

   /**
    * Moves the transform by a direction to a new location.
    *
    * @param dir
    *           the direction
    * @return this transform
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   @Chainable
   public Transform2 moveBy ( final Vec2 dir ) {

      this.locPrev.set(this.location);
      Vec2.add(this.locPrev, dir, this.location);
      return this;
   }

   /**
    * Sets the transform's location.
    *
    * @param x
    *           the x location
    * @param y
    *           the y location
    * @return this transform
    */
   @Chainable
   public Transform2 moveTo ( final float x, final float y ) {

      this.locPrev.set(this.location);
      this.location.set(x, y);
      return this;
   }

   /**
    * Sets the transforms' location.
    *
    * @param locNew
    *           the new location
    * @return this transform
    */
   @Chainable
   public Transform2 moveTo ( final Vec2 locNew ) {

      this.locPrev.set(this.location);
      this.location.set(locNew);
      return this;
   }

   /**
    * Eases the transform to a location by a step. The static
    * easing function is used.
    *
    * @param locNew
    *           the new location
    * @param step
    *           the step in [0.0, 1.0]
    * @return this transform
    */
   @Chainable
   public Transform2 moveTo ( final Vec2 locNew, final float step ) {

      return this.moveTo(locNew, step, Transform2.EASING.loc);
   }

   /**
    * Eases the transform to a location by a step. The kind of
    * easing is specified by a Vec2 easing function.
    *
    * @param locNew
    *           the new location
    * @param step
    *           the step in [0.0, 1.0]
    * @param easingFunc
    *           the easing function
    * @return this transform
    * @see Vec2.AbstrEasing#apply(Vec2, Vec2, Float, Vec2)
    */
   @Chainable
   public Transform2 moveTo (
         final Vec2 locNew,
         final float step,
         final Vec2.AbstrEasing easingFunc ) {

      this.locPrev.set(this.location);
      easingFunc.apply(this.locPrev, locNew, step, this.location);
      return this;
   }

   /**
    * Resets this transform to the identity.
    *
    * @return this transform
    */
   @Chainable
   public Transform2 reset () {

      return Transform2.identity(this);
   }

   /**
    * Rotates the transform to an angle in radians.
    *
    * @param rotNew
    *           the new angle
    * @return this transform
    */
   @Chainable
   public Transform2 rotateTo ( final float rotNew ) {

      this.rotPrev = this.rotation;
      this.rotation = rotNew;
      this.updateAxes();
      return this;
   }

   /**
    * Rotates the transform to a new orientation by a step in
    * [0.0, 1.0].
    *
    * @param radians
    *           the angle
    * @param step
    *           the step
    * @return this transform
    */
   @Chainable
   public Transform2 rotateTo ( final float radians, final float step ) {

      return this.rotateTo(radians, step, Transform2.EASING.rot);
   }

   /**
    * Rotates the transform to a new orientation by a step in
    * [0.0, 1.0] using the specified easing function.
    *
    * @param radians
    *           the angle
    * @param step
    *           the step
    * @param easingFunc
    *           the easing function
    * @return this transform
    */
   @Chainable
   public Transform2 rotateTo (
         final float radians,
         final float step,
         final Utils.PeriodicEasing easingFunc ) {

      this.rotPrev = this.rotation;
      this.rotation = easingFunc.apply(this.rotPrev, radians, step);
      this.updateAxes();
      return this;
   }

   /**
    * Rotates this transform around the z axis by an angle in
    * radians.
    *
    * @param radians
    *           the angle
    * @return this transform
    */
   @Chainable
   public Transform2 rotateZ ( final float radians ) {

      this.rotPrev = this.rotation;
      this.rotation += radians;
      this.updateAxes();
      return this;
   }

   /**
    * Scales the transform by a scalar.
    *
    * @param scalar
    *           the scalar
    * @return this transform
    * @see Vec2#mult(Vec2, float, Vec2)
    */
   @Chainable
   public Transform2 scaleBy ( final float scalar ) {

      if (scalar == 0.0f) {
         return this;
      }
      this.scalePrev.set(this.scale);
      Vec2.mult(this.scalePrev, scalar, this.scale);
      return this;
   }

   /**
    * Scales the transform by a non-uniform scalar.
    *
    * @param nonUniformScale
    *           the scale
    * @return this transform
    * @see Vec2#isNonZero(Vec2)
    * @see Vec2#mult(Vec2, Vec2, Vec2)
    */
   @Chainable
   public Transform2 scaleBy ( final Vec2 nonUniformScale ) {

      if (Vec2.isNonZero(nonUniformScale)) {
         this.scalePrev.set(this.scale);
         Vec2.mult(this.scalePrev, nonUniformScale, this.scale);
      }
      return this;
   }

   /**
    * Scales the transform to a uniform size.
    *
    * @param scalar
    *           the size
    * @return this transform
    */
   @Chainable
   public Transform2 scaleTo ( final float scalar ) {

      if (scalar != 0.0f) {
         this.scalePrev.set(this.scale);
         this.scale.set(scalar, scalar);
      }
      return this;
   }

   /**
    * Scales the transform to a non-uniform size.
    *
    * @param x
    *           the size on the x axis
    * @param y
    *           the size on the y axis
    * @return this transform
    */
   @Chainable
   public Transform2 scaleTo ( final float x, final float y ) {

      if (x != 0.0f && y != 0.0f) {
         this.scalePrev.set(this.scale);
         this.scale.set(x, y);
      }
      return this;
   }

   /**
    * Scales the transform to a non-uniform size.
    *
    * @param scaleNew
    *           the new scale
    * @return this transform
    * @see Vec2#isNonZero(Vec2)
    */
   @Chainable
   public Transform2 scaleTo ( final Vec2 scaleNew ) {

      if (Vec2.isNonZero(scaleNew)) {
         this.scalePrev.set(this.scale);
         this.scale.set(scaleNew);
      }
      return this;
   }

   /**
    * Eases the transform to a scale by a step. The static
    * easing function is used.
    *
    * @param scaleNew
    *           the new scale
    * @param step
    *           the step in [0.0, 1.0]
    * @return this transform
    * @see Vec2#isNonZero(Vec2)
    */
   @Chainable
   public Transform2 scaleTo ( final Vec2 scaleNew, final float step ) {

      if (Vec2.isNonZero(scaleNew)) {
         return this.scaleTo(scaleNew, step, Transform2.EASING.scale);
      }
      return this;

   }

   /**
    * Eases the transform to a scale by a step. The kind of
    * easing is specified by a Vec2 easing function.
    *
    * @param scaleNew
    *           the new scale
    * @param step
    *           the step in [0.0, 1.0]
    * @param easingFunc
    *           the easing function
    * @return this transform
    * @see Vec2#isNonZero(Vec2)
    * @see Vec2.AbstrEasing#apply(Vec2, Vec2, Float, Vec2)
    */
   @Chainable
   public Transform2 scaleTo (
         final Vec2 scaleNew,
         final float step,
         final Vec2.AbstrEasing easingFunc ) {

      if (Vec2.isNonZero(scaleNew)) {
         this.scalePrev.set(this.scale);
         easingFunc.apply(this.scalePrev, scaleNew, step, this.scale);
      }
      return this;
   }

   /**
    * Sets this transform from loose real numbers.
    *
    * @param xLoc
    *           the x location
    * @param yLoc
    *           the y location
    * @param radians
    *           the angle in radians
    * @param xScale
    *           the scale x
    * @param yScale
    *           the scale y
    * @return this transform
    */
   @Chainable
   public Transform2 set (
         final float xLoc,
         final float yLoc,

         final float radians,

         final float xScale,
         final float yScale ) {

      this.moveTo(xLoc, yLoc);
      this.rotateTo(radians);
      this.scaleTo(xScale, yScale);

      return this;
   }

   /**
    * Sets this transform to the components of another.
    *
    * @param source
    *           the source transform
    * @return this transform
    */
   @Chainable
   public Transform2 set ( final Transform2 source ) {

      return this.set(source.location, source.rotation, source.scale);
   }

   /**
    * Sets the components of the transform.
    *
    * @param locNew
    *           the new location
    * @param rotNew
    *           the new rotation
    * @param scaleNew
    *           the new scale
    * @return this transform
    */
   @Chainable
   public Transform2 set (
         final Vec2 locNew,
         final float rotNew,
         final Vec2 scaleNew ) {

      this.moveTo(locNew);
      this.rotateTo(rotNew);
      this.scaleTo(scaleNew);

      return this;
   }

   /**
    * Returns a string representation of this transform
    * according to its string format.
    *
    * For display purposes, the angle is converted from radians
    * to degrees.
    *
    * @return the string
    * @see IUtils#RAD_TO_DEG
    */
   @Override
   public String toString () {

      return new StringBuilder()
            .append("{ location: ")
            .append(this.location.toString())
            .append(", \nrotation: ")
            .append(Utils.toFixed(IUtils.RAD_TO_DEG * this.rotation, 1))
            .append(", \nscale: ")
            .append(this.scale.toString())
            .append(" }").toString();
   }

   /**
    * Returns a string representation of this transform in SVG
    * syntax.
    *
    * The angle is converted from radians to degrees.
    *
    * @return the SVG string
    * @see IUtils#RAD_TO_DEG
    */
   public String toSvgString () {

      return new StringBuilder()
            .append("transform=\"translate(")
            .append(Utils.toFixed(this.location.x, 1))
            .append(", ")
            .append(Utils.toFixed(this.location.y, 1))
            .append(") rotate(")
            .append(Utils.toFixed(this.rotation * IUtils.RAD_TO_DEG, 0))
            .append(") scale(")
            .append(Utils.toFixed(this.scale.x, 1))
            .append(", ")
            .append(Utils.toFixed(this.scale.y, 1))
            .append(")\"")
            .toString();
   }
}
