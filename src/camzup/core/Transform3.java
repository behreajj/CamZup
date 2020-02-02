package camzup.core;

import camzup.core.Utils.EasingFuncArr;

/**
 * Facilitates 3D affine transformations for entities.
 */
public class Transform3 extends Transform {

   /**
    * An easing function to facilitate animating multiple
    * transforms.
    */
   public static class Easing implements EasingFuncArr < Transform3 > {

      /**
       * The location easing function.
       */
      public Vec3.AbstrEasing loc;

      /**
       * The rotation easing function.
       */
      public Quaternion.AbstrEasing rot;

      /**
       * The scale easing function.
       */
      public Vec3.AbstrEasing scale;

      /**
       * The default constructor.
       */
      public Easing () {

         this.loc = new Vec3.Lerp();
         this.rot = new Quaternion.Slerp();
         this.scale = new Vec3.SmoothStep();
      }

      /**
       * The easing constructor.
       *
       * @param locEasing
       *           the location easing function
       * @param rotEasing
       *           the rotation easing function
       * @param scaleEasing
       *           the scale easing function
       */
      public Easing (
            final Vec3.AbstrEasing locEasing,
            final Quaternion.AbstrEasing rotEasing,
            final Vec3.AbstrEasing scaleEasing ) {

         this.loc = locEasing;
         this.rot = rotEasing;
         this.scale = scaleEasing;
      }

      /**
       * Eases between an origin and destination transform by a
       * step in [0.0, 1.0].
       *
       * @param origin
       *           the origin
       * @param dest
       *           the destination
       * @param step
       *           the step
       * @param target
       *           the output transform
       * @return the eased transform
       */
      public Transform3 apply (
            final Transform3 origin,
            final Transform3 dest,
            final Float step,
            final Transform3 target ) {

         if (step <= 0.0f) {
            return target.set(origin);
         }

         if (step >= 1.0f) {
            return target.set(dest);
         }

         return this.applyUnclamped(origin, dest, step, target);
      }

      /**
       * Eases between transforms in an array by a step in the
       * range [0.0, 1.0].
       *
       * @param arr
       *           the transform array
       * @param step
       *           the step
       * @param target
       *           the output transform
       */
      @Override
      public Transform3 apply (
            final Transform3[] arr,
            final Float step,
            final Transform3 target ) {

         final int len = arr.length;
         if (len == 1 || step <= 0.0f) {
            return target.set(arr[0]);
         }

         if (step >= 1.0f) {
            return target.set(arr[len - 1]);
         }

         final float scaledStep = step * (len - 1);
         final int i = (int) scaledStep;
         return this.applyUnclamped(
               arr[i], arr[i + 1],
               scaledStep - i, target);
      }

      /**
       * Eases between an origin and destination transform by a
       * step in [0.0, 1.0].
       *
       * @param origin
       *           the origin
       * @param dest
       *           the destination
       * @param step
       *           the step
       * @param target
       *           the output transform
       * @return the eased transform
       */
      public Transform3 applyUnclamped (
            final Transform3 origin,
            final Transform3 dest,
            final float step,
            final Transform3 target ) {

         target.locPrev.set(target.location);
         this.loc.applyUnclamped(
               origin.location,
               dest.location,
               step,
               target.location);

         target.rotPrev.set(target.rotation);
         this.rot.applyUnclamped(
               origin.rotation,
               dest.rotation,
               step,
               target.rotation);

         target.scalePrev.set(target.scale);
         this.loc.applyUnclamped(
               origin.scale,
               dest.scale,
               step,
               target.scale);

         return target;
      }

      /**
       * Returns a string representation of this easing function.
       *
       * @return the string
       */
      @Override
      public String toString () {

         return this.getClass().getSimpleName();
      }
   }

   /**
    * The default easing function.
    */
   private static Easing EASING = new Easing();

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = 6128601941848021668L;

   /**
    * Creates a transform from three axes and a translation.
    *
    * @param right
    *           the right axis
    * @param forward
    *           the forward axis
    * @param up
    *           the up axis
    * @param target
    *           the output transform
    * @return the transform
    */
   public static Transform3 fromAxes (
         final Vec3 right,
         final Vec3 forward,
         final Vec3 up,
         final Transform3 target ) {

      target.rotPrev.set(target.rotation);
      Quaternion.fromAxes(right, forward, up, target.rotation);
      target.updateAxes();

      target.location.reset();
      target.locPrev.reset();

      target.scalePrev.set(target.scale);
      Vec3.one(target.scale);

      return target;
   }

   /**
    * Gets the string representation of the default easing
    * function.
    *
    * @return the string
    */
   public static String getEasingString () {

      return Transform3.EASING.toString();
   }

   /**
    * Sets the transform to an identity configuration.
    *
    * @param target
    *           the output transform
    * @return the identity
    */
   public static Transform3 identity ( final Transform3 target ) {

      target.locPrev.set(target.location);
      target.location.reset();

      target.rotPrev.set(target.rotation);
      target.rotation.reset();

      target.scalePrev.set(target.scale);
      Vec3.one(target.scale);

      Vec3.right(target.right);
      Vec3.forward(target.forward);
      Vec3.up(target.up);

      return target;
   }

   /**
    * Finds the inverse of the transform. When converting to a
    * matrix, use the reverse transform order.
    *
    * @param source
    *           the source transform
    * @param target
    *           the target transform
    * @return the inverse
    * @see Vec3#negate(Vec3, Vec3)
    * @see Quaternion#inverse(Quaternion, Quaternion,
    *      Quaternion)
    * @see Vec3#div(float, Vec3, Vec3)
    * @see Transform3#updateAxes()
    */
   public static Transform3 inverse (
         final Transform3 source,
         final Transform3 target ) {

      Vec3.negate(source.location, target.location);
      Quaternion.inverse(source.rotation, target.rotation);
      Vec3.div(1.0f, source.scale, target.scale);
      target.updateAxes();
      return target;
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
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    */
   public static Vec3 locDelta (
         final Transform3 t,
         final Vec3 target ) {

      return Vec3.sub(t.location, t.locPrev, target);
   }

   /**
    * Returns the maximum dimension occupied by a transform.
    *
    * @param t
    *           the transform
    * @return the maximum dimension
    * @see Utils#max(float, float, float)
    */
   public static float maxDimension ( final Transform3 t ) {

      return Utils.max(t.scale.x, t.scale.y, t.scale.z);
   }

   /**
    * Returns the minimum dimension occupied by a transform.
    *
    * @param t
    *           the transform
    * @return the minimum dimension
    * @see Utils#min(float, float, float)
    */
   public static float minDimension ( final Transform3 t ) {

      return Utils.min(t.scale.x, t.scale.y, t.scale.z);
   }

   /**
    * Mixes two transforms together by a step in [0.0, 1.0] .
    *
    * @param origin
    *           the original transform
    * @param dest
    *           the destination transform
    * @param step
    *           the step
    * @param target
    *           the output transform
    * @return the mix
    * @see Transform3#EASING
    */
   public static Transform3 mix (
         final Transform3 origin,
         final Transform3 dest,
         final float step,
         final Transform3 target ) {

      return Transform3.EASING.apply(origin, dest, step, target);
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
    * @see Quaternion#mulVector(Quaternion, Vec3, Vec3)
    */
   public static Vec3 mulDir (
         final Transform3 t,
         final Vec3 source,
         final Vec3 target ) {

      Quaternion.mulVector(t.rotation, source, target);
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
    * @see Quaternion#mulVector(Quaternion, Vec3, Vec3)
    * @see Vec3#mul(Vec3, Vec3, Vec3)
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   public static Vec3 mulPoint (
         final Transform3 t,
         final Vec3 source,
         final Vec3 target ) {

      Quaternion.mulVector(t.rotation, source, target);
      Vec3.mul(target, t.scale, target);
      Vec3.add(target, t.location, target);
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
    * @see Quaternion#mulVector(Quaternion, Vec3, Vec3)
    * @see Vec3#mul(Vec3, Vec3, Vec3)
    */
   public static Vec3 mulVector (
         final Transform3 t,
         final Vec3 source,
         final Vec3 target ) {

      Quaternion.mulVector(t.rotation, source, target);
      Vec3.mul(target, t.scale, target);
      return target;
   }

   /**
    * Finds the difference between the current and previous
    * rotation of a transform.
    *
    * @param t
    *           the transform
    * @param target
    *           the output quaternion
    * @return the rotation delta
    * @see Quaternion#subNorm(Quaternion, Quaternion,
    *      Quaternion)
    */
   public static Quaternion rotDelta (
         final Transform3 t,
         final Quaternion target ) {

      return Quaternion.subNorm(t.rotation, t.rotPrev, target);
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
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    */
   public static Vec3 scaleDelta (
         final Transform3 t,
         final Vec3 target ) {

      return Vec3.sub(t.scale, t.scalePrev, target);
   }

   public static void setEasing ( final Easing easing ) {

      if (easing != null) {
         Transform3.EASING = easing;
      }
   }

   /**
    * The transform's forward axis.
    */
   protected final Vec3 forward;

   /**
    * The transform's location.
    */
   protected final Vec3 location;

   /**
    * The previous location. Subtract from the current location
    * to find the delta, or change, in location.
    */
   protected final Vec3 locPrev;

   /**
    * The transform's right axis.
    */
   protected final Vec3 right;

   /**
    * The transform's rotation. Defaults to the identity, (1.0,
    * 0.0, 0.0, 0.0) .
    */
   protected final Quaternion rotation;

   /**
    * The previous rotation. Subtract from the current rotation
    * to find the delta, or change, in rotation.
    */
   protected final Quaternion rotPrev;

   /**
    * The transform's scale.
    */
   protected final Vec3 scale;

   /**
    * The previous scale. Subtract from the current scale to
    * find the delta, or change, in scale.
    */
   protected final Vec3 scalePrev;

   /**
    * The transform's up axis.
    */
   protected final Vec3 up;

   {
      this.location = new Vec3();
      this.locPrev = new Vec3();

      this.rotation = Quaternion.identity(new Quaternion());
      this.rotPrev = Quaternion.identity(new Quaternion());

      this.scale = Vec3.one(new Vec3());
      this.scalePrev = Vec3.one(new Vec3());

      this.right = Vec3.right(new Vec3());
      this.forward = Vec3.forward(new Vec3());
      this.up = Vec3.up(new Vec3());
   }

   /**
    * The default constructor.
    */
   public Transform3 () {

      super();
   }

   /**
    * Creates a transform from loose real numbers.
    *
    * @param xLoc
    *           the x location
    * @param yLoc
    *           the y location
    * @param zLoc
    *           the z location
    * @param real
    *           the rotation real component
    * @param xImag
    *           the rotation x imaginary
    * @param yImag
    *           the rotation y imaginary
    * @param zImag
    *           the rotation z imaginary
    * @param xScale
    *           the scale x
    * @param yScale
    *           the scale y
    * @param zScale
    *           the scale z
    */
   public Transform3 (
         final float xLoc,
         final float yLoc,
         final float zLoc,
         final float real,
         final float xImag,
         final float yImag,
         final float zImag,
         final float xScale,
         final float yScale,
         final float zScale ) {

      super();
      this.set(
            xLoc, yLoc, zLoc,
            real, xImag, yImag, zImag,
            xScale, yScale, zScale);
   }

   /**
    * Creates a new 3D transform from a 2D source
    *
    * @param source
    *           the source transform
    */
   public Transform3 ( final Transform2 source ) {

      super();
      this.set(source);
   }

   /**
    * Creates a new transform from a source.
    *
    * @param source
    *           the source transform
    */
   public Transform3 ( final Transform3 source ) {

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
   public Transform3 (
         final Vec3 location,
         final Quaternion rotation,
         final Vec3 scale ) {

      super();
      this.set(location, rotation, scale);
   }

   /**
    * Returns a String of Python code targeted toward the
    * Blender 2.8x API. This code is brittle and is used for
    * internal testing purposes, i.e., to compare how
    * transforms look in Blender (the control) vs. in the
    * library (the test).
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode () {

      final String rotationMode = "\"QUATERNION\"";

      return new StringBuilder(256)
            .append("{\"location\": ")
            .append(this.location.toBlenderCode())
            .append(", \"rotation_mode\": ")
            .append(rotationMode)
            .append(", \"rotation_quaternion\": ")
            .append(this.rotation.toBlenderCode())
            .append(", \"scale\": ")
            .append(this.scale.toBlenderCode())
            .append('}')
            .toString();
   }

   /**
    * Tests the equivalence between this and another transform.
    *
    * @param t
    *           the transform
    * @return the evaluation
    */
   protected boolean equals ( final Transform3 t ) {

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
      if (this.rotation == null) {
         if (t.rotation != null) {
            return false;
         }
      } else if (!this.rotation.equals(t.rotation)) {
         return false;
      }
      return true;
   }

   /**
    * Updates the local axes of the transform based on its
    * rotation.
    *
    * @see Quaternion#toAxes(Quaternion, Vec3, Vec3, Vec3)
    */
   @Override
   protected void updateAxes () {

      Quaternion.toAxes(this.rotation, this.right, this.forward, this.up);
   }

   /**
    * Creates a new transform with the components of this
    * transform.
    *
    * @return the cloned transform
    */
   @Override
   public Transform3 clone () {

      return new Transform3(this.location, this.rotation, this.scale);
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
      return this.equals((Transform3) obj);
   }

   /**
    * Gets the transform's rotation as an axis and angle. The
    * angle is returned from the function.
    *
    * @param axis
    *           the output axis
    * @return the angle in radians
    * @see Quaternion#toAxisAngle(Quaternion, Vec3)
    */
   public float getAxisAngle ( final Vec3 axis ) {

      return Quaternion.toAxisAngle(this.rotation, axis);
   }

   /**
    * Gets the transform's forward axis.
    *
    * @param target
    *           the output vector
    * @return the forward axis
    */
   public Vec3 getForward ( final Vec3 target ) {

      return target.set(this.forward);
   }

   /**
    * Gets the transform's location
    *
    * @param target
    *           the output vector
    * @return the location
    */
   public Vec3 getLocation ( final Vec3 target ) {

      return target.set(this.location);
   }

   /**
    * Gets the transform's previous location
    *
    * @param target
    *           the output vector
    * @return the previous location
    */
   public Vec3 getLocPrev ( final Vec3 target ) {

      return target.set(this.locPrev);
   }

   /**
    * Gets the transform's right axis.
    *
    * @param target
    *           the output vector
    * @return the right axis
    */
   public Vec3 getRight ( final Vec3 target ) {

      return target.set(this.right);
   }

   /**
    * Gets the transform's rotation
    *
    * @param target
    *           the output quaternion
    * @return the rotation
    */
   public Quaternion getRotation ( final Quaternion target ) {

      return target.set(this.rotation);
   }

   /**
    * Gets the transform's previous rotation
    *
    * @param target
    *           the output quaternion
    * @return the previous rotation
    */
   public Quaternion getRotPrev ( final Quaternion target ) {

      return target.set(this.rotPrev);
   }

   /**
    * Gets the transform's scale.
    *
    * @param target
    *           the output vector
    * @return the scale
    */
   public Vec3 getScale ( final Vec3 target ) {

      return target.set(this.scale);
   }

   /**
    * Gets the transform's previous scale.
    *
    * @param target
    *           the output vector
    * @return the previous scale
    */
   public Vec3 getScalePrev ( final Vec3 target ) {

      return target.set(this.scalePrev);
   }

   /**
    * Gets the transform's up axis.
    *
    * @param target
    *           the output vector
    * @return the up axis
    */
   public Vec3 getUp ( final Vec3 target ) {

      return target.set(this.up);
   }

   /**
    * Returns a hash code for this transform based on its
    * location, rotation and scale.
    *
    * @return the hash code
    * @see Vec3#hashCode()
    * @see Quaternion#hashCode()
    */
   @Override
   public int hashCode () {

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL
            ^ (this.location == null ? 0 : this.location.hashCode());
      hash = hash * IUtils.HASH_MUL
            ^ (this.rotation == null ? 0 : this.rotation.hashCode());
      hash = hash * IUtils.HASH_MUL
            ^ (this.scale == null ? 0 : this.scale.hashCode());
      return hash;
   }

   /**
    * Moves the transform by a direction to a new location.
    *
    * @param dir
    *           the direction
    * @return this transform
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   @Chainable
   public Transform3 moveBy ( final Vec2 dir ) {

      this.locPrev.set(this.location);
      this.location.set(dir, 0.0f);
      Vec3.add(this.locPrev, this.location, this.location);
      return this;
   }

   /**
    * Moves the transform by a direction to a new location.
    *
    * @param dir
    *           the direction
    * @return this transform
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   @Chainable
   public Transform3 moveBy ( final Vec3 dir ) {

      this.locPrev.set(this.location);
      Vec3.add(this.locPrev, dir, this.location);
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
   public Transform3 moveTo ( final Vec2 locNew ) {

      this.locPrev.set(this.location);
      this.location.set(locNew, 0.0f);
      return this;
   }

   /**
    * Eases the transform to a location by a step. The kind of
    * easing is specified by a Vec3 easing function.
    *
    * @param locNew
    *           the new location
    * @param step
    *           the step in [0.0, 1.0]
    * @param easingFunc
    *           the easing function
    * @return this transform
    * @see Vec3.AbstrEasing#apply(Vec3, Vec3, Float, Vec3)
    */
   @Chainable
   public Transform3 moveTo (
         final Vec2 locNew,
         final float step,
         final Vec3.AbstrEasing easingFunc ) {

      this.locPrev.set(this.location);
      this.location.set(locNew, 0.0f);
      easingFunc.apply(this.locPrev, this.location, step, this.location);
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
   public Transform3 moveTo ( final Vec3 locNew ) {

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
   public Transform3 moveTo (
         final Vec3 locNew,
         final float step ) {

      return this.moveTo(locNew, step, Transform3.EASING.loc);
   }

   /**
    * Eases the transform to a location by a step. The kind of
    * easing is specified by a Vec3 easing function.
    *
    * @param locNew
    *           the new location
    * @param step
    *           the step in [0.0, 1.0]
    * @param easingFunc
    *           the easing function
    * @return this transform
    * @see Vec3.AbstrEasing#apply(Vec3, Vec3, Float, Vec3)
    */
   @Chainable
   public Transform3 moveTo (
         final Vec3 locNew,
         final float step,
         final Vec3.AbstrEasing easingFunc ) {

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
   public Transform3 reset () {

      this.locPrev.reset();
      this.location.reset();

      this.rotPrev.reset();
      this.rotation.reset();

      Vec3.one(this.scalePrev);
      Vec3.one(this.scale);

      Vec3.right(this.right);
      Vec3.forward(this.forward);
      Vec3.up(this.up);

      return this;
   }

   /**
    * Rotates the transform by an axis and angle in radians.
    *
    * @param radians
    *           the angle
    * @param axis
    *           the axis
    * @return this transform
    * @see Quaternion#rotate(Quaternion, float, Vec3,
    *      Quaternion)
    * @see Transform3#updateAxes()
    */
   @Chainable
   public Transform3 rotateBy (
         final float radians,
         final Vec3 axis ) {

      this.rotPrev.set(this.rotation);
      Quaternion.rotate(this.rotPrev, radians, axis, this.rotation);
      this.updateAxes();
      return this;
   }

   /**
    * Rotates the transform by adding a rotation, then
    * normalizing.
    *
    * @param rot
    *           the rotation
    * @return this transform
    * @see Quaternion#addNorm(Quaternion, Quaternion,
    *      Quaternion)
    * @see Transform3#updateAxes()
    */
   @Chainable
   public Transform3 rotateBy ( final Quaternion rot ) {

      this.rotPrev.set(this.rotation);
      Quaternion.addNorm(this.rotPrev, rot, this.rotation);
      this.updateAxes();
      return this;
   }

   /**
    * Rotates the transform to a new orientation.
    *
    * @param rotNew
    *           the new orientation
    * @return this transform
    * @see Transform3#updateAxes()
    */
   @Chainable
   public Transform3 rotateTo ( final Quaternion rotNew ) {

      if (Quaternion.none(rotNew)) {
         return this;
      }

      this.rotPrev.set(this.rotation);
      this.rotation.set(rotNew);
      this.updateAxes();
      return this;
   }

   /**
    * Eases the transform toward a new orientation by a step in
    * [0.0, 1.0].
    *
    * @param rotNew
    *           the new orientation
    * @param step
    *           the step
    * @return this transform
    */
   @Chainable
   public Transform3 rotateTo (
         final Quaternion rotNew,
         final float step ) {

      return this.rotateTo(rotNew, step, Transform3.EASING.rot);
   }

   /**
    * Eases the transform toward a new orientation by a step in
    * [0.0, 1.0] using the specified easing function.
    *
    * @param rotNew
    *           the new orientation
    * @param step
    *           the step
    * @param easingFunc
    *           the easing function
    * @return this transform
    */
   @Chainable
   public Transform3 rotateTo (
         final Quaternion rotNew,
         final float step,
         final Quaternion.AbstrEasing easingFunc ) {

      if (Quaternion.none(rotNew)) {
         return this;
      }
      this.rotPrev.set(this.rotation);
      easingFunc.apply(this.rotPrev, rotNew, step, this.rotation);
      this.updateAxes();
      return this;
   }

   /**
    * Rotates this transform around the x axis by an angle in
    * radians.
    *
    * Beware that using sequences of ortho-normal rotations
    * will result in gimbal lock.
    *
    * @param radians
    *           the angle
    * @return this transform
    * @see Quaternion#rotateX(Quaternion, float, Quaternion)
    * @see Transform3#updateAxes()
    */
   @Chainable
   public Transform3 rotateX ( final float radians ) {

      this.rotPrev.set(this.rotation);
      Quaternion.rotateX(this.rotPrev, radians, this.rotation);
      this.updateAxes();
      return this;
   }

   /**
    * Rotates this transform around the y axis by an angle in
    * radians.
    *
    * Beware that using sequences of ortho-normal rotations
    * will result in gimbal lock.
    *
    * @param radians
    *           the angle
    * @return this transform
    * @see Quaternion#rotateY(Quaternion, float, Quaternion)
    * @see Transform3#updateAxes()
    */
   @Chainable
   public Transform3 rotateY ( final float radians ) {

      this.rotPrev.set(this.rotation);
      Quaternion.rotateY(this.rotPrev, radians, this.rotation);
      this.updateAxes();
      return this;
   }

   /**
    * Rotates this transform around the z axis by an angle in
    * radians.
    *
    * Beware that using sequences of ortho-normal rotations
    * will result in gimbal lock.
    *
    * @param radians
    *           the angle
    * @return this transform
    * @see Quaternion#rotateZ(Quaternion, float, Quaternion)
    * @see Transform3#updateAxes()
    */
   @Chainable
   public Transform3 rotateZ ( final float radians ) {

      this.rotPrev.set(this.rotation);
      Quaternion.rotateZ(this.rotPrev, radians, this.rotation);
      this.updateAxes();
      return this;
   }

   /**
    * Scales the transform by a scalar.
    *
    * @param scalar
    *           the scalar
    * @return this transform
    * @see Vec3#mul(Vec3, float, Vec3)
    */
   @Chainable
   public Transform3 scaleBy ( final float scalar ) {

      if (scalar == 0.0f) {
         return this;
      }
      this.scalePrev.set(this.scale);
      Vec3.mul(this.scalePrev, scalar, this.scale);
      return this;
   }

   /**
    * Scales the transform by a non-uniform scalar.
    *
    * @param nonUniformScale
    *           the scale
    * @return this transform
    * @see Vec2#all(Vec2)
    * @see Vec3#mul(Vec3, Vec3, Vec3)
    */
   @Chainable
   public Transform3 scaleBy ( final Vec2 nonUniformScale ) {

      if (Vec2.all(nonUniformScale)) {
         this.scalePrev.set(this.scale);
         this.scale.set(nonUniformScale, 1.0f);
         Vec3.mul(this.scalePrev, this.scale, this.scale);
      }
      return this;
   }

   /**
    * Scales the transform by a non-uniform scalar.
    *
    * @param nonUniformScale
    *           the scale
    * @return this transform
    * @see Vec3#all(Vec3)
    * @see Vec3#mul(Vec3, Vec3, Vec3)
    */
   @Chainable
   public Transform3 scaleBy ( final Vec3 nonUniformScale ) {

      if (Vec3.all(nonUniformScale)) {
         this.scalePrev.set(this.scale);
         Vec3.mul(this.scalePrev, nonUniformScale, this.scale);
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
   public Transform3 scaleTo ( final float scalar ) {

      if (scalar != 0.0f) {
         this.scalePrev.set(this.scale);
         this.scale.set(scalar, scalar, scalar);
      }
      return this;
   }

   /**
    * Scales the transform to a non-uniform size.
    *
    * @param scaleNew
    *           the new scale
    * @return this transform
    * @see Vec3#all(Vec3)
    */
   @Chainable
   public Transform3 scaleTo ( final Vec3 scaleNew ) {

      if (Vec3.all(scaleNew)) {
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
    * @see Vec3#all(Vec3)
    */
   @Chainable
   public Transform3 scaleTo (
         final Vec3 scaleNew,
         final float step ) {

      if (Vec3.all(scaleNew)) {
         return this.scaleTo(scaleNew, step, Transform3.EASING.scale);
      }
      return this;
   }

   /**
    * Eases the transform to a scale by a step. The kind of
    * easing is specified by a Vec3 easing function.
    *
    * @param scaleNew
    *           the new scale
    * @param step
    *           the step in [0.0, 1.0]
    * @param easingFunc
    *           the easing function
    * @return this transform
    * @see Vec3#all(Vec3)
    * @see Vec3.AbstrEasing#apply(Vec3, Vec3, Float, Vec3)
    */
   @Chainable
   public Transform3 scaleTo (
         final Vec3 scaleNew,
         final float step,
         final Vec3.AbstrEasing easingFunc ) {

      if (Vec3.all(scaleNew)) {
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
    * @param zLoc
    *           the z location
    * @param real
    *           the rotation real component
    * @param xImag
    *           the rotation x imaginary
    * @param yImag
    *           the rotation y imaginary
    * @param zImag
    *           the rotation z imaginary
    * @param xScale
    *           the scale x
    * @param yScale
    *           the scale y
    * @param zScale
    *           the scale z
    * @return this transform
    */
   @Chainable
   public Transform3 set (
         final float xLoc, final float yLoc, final float zLoc,
         final float real,
         final float xImag, final float yImag, final float zImag,
         final float xScale, final float yScale, final float zScale ) {

      this.locPrev.set(this.location);
      this.location.set(xLoc, yLoc, zLoc);

      this.rotPrev.set(this.rotation);
      this.rotation.set(real, xImag, yImag, zImag);

      this.scalePrev.set(this.scale);
      this.scale.set(xScale, yScale, zScale);

      return this;
   }

   /**
    * Promotes a 2D transform to a 3D transform.
    *
    * @param source
    *           the source transform
    * @return this transform
    */
   @Chainable
   public Transform set ( final Transform2 source ) {

      this.location.set(source.location, 0.0f);
      Quaternion.fromAngle(source.rotation, this.rotation);
      this.scale.set(source.scale, 1.0f);

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
   public Transform3 set ( final Transform3 source ) {

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
   public Transform3 set (
         final Vec3 locNew,
         final Quaternion rotNew,
         final Vec3 scaleNew ) {

      this.moveTo(locNew);
      this.rotateTo(rotNew);
      this.scaleTo(scaleNew);

      return this;
   }

   /**
    * Returns a string representation of this transform
    * according to its string format.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   /**
    * Returns a string representation of this transform
    * according to its string format.
    *
    * @param places
    *           the number of places
    * @return the string
    */
   public String toString ( final int places ) {

      return new StringBuilder(354)
            .append("{ location: ")
            .append(this.location.toString(places))
            .append(", rotation: ")
            .append(this.rotation.toString(places))
            .append(", scale: ")
            .append(this.scale.toString(places))
            .append(' ')
            .append('}')
            .toString();
   }

   /**
    * Wraps the transform's location around a periodic range as
    * defined by an upper and lower bound: lower bounds
    * inclusive; upper bounds exclusive.
    *
    * @param lb
    *           the lower bound
    * @param ub
    *           the upper bound
    * @return the wrapped transform
    */
   @Chainable
   public Transform3 wrap (
         final Vec3 lb,
         final Vec3 ub ) {

      this.locPrev.set(this.location);
      Vec3.wrap(this.locPrev, lb, ub, this.location);
      return this;
   }
}
