package camzup.core;

/**
 * Facilitates 3D affine transformations for entities.
 */
public class Transform3 extends Transform {

   /**
    * The easing function to mix transform locations.
    */
   private static Vec3.AbstrEasing LOC_EASING = new Vec3.Lerp();

   /**
    * The easing function to mix transform rotations.
    */
   private static Quaternion.AbstrEasing ROT_EASING = new Quaternion.Slerp();

   /**
    * The easing function to mix transform scales.
    */
   private static Vec3.AbstrEasing SCALE_EASING = new Vec3.Lerp();

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
    * @param translation
    *           the translation
    * @param target
    *           the output transform
    * @return the transform
    */
   public static Transform3 fromAxes ( final Vec3 right, final Vec3 forward,
         final Vec3 up, final Vec3 translation,
         final Transform3 target ) {

      // TODO: Needs testing.

      target.moveTo(translation);

      target.rotPrev.set(target.rotation);
      Quaternion.fromAxes(right, forward, up, target.rotation);

      // Input axes may not be normalized, so use this instead.
      target.updateAxes();

      target.scaleTo(1.0f);

      return target;
   }

   /**
    * Gets the name of the transform's location easing function
    * as a string.
    * 
    * @return the string
    */
   public static String getLocEasingString () {

      return Transform3.LOC_EASING.toString();
   }

   /**
    * Gets the name of the transform's rotation easing function
    * as a string.
    * 
    * @return the string
    */
   public static String getRotEasingString () {

      return Transform3.ROT_EASING.toString();
   }

   /**
    * Gets the name of the transform's scale easing function as
    * a string.
    * 
    * @return the string
    */
   public static String getScaleEasingString () {

      return Transform3.SCALE_EASING.toString();
   }

   /**
    * Sets the transform to an identity configuration.
    * 
    * @param target
    *           the output transform
    * @return the identity
    */
   public static Transform3 identity ( final Transform3 target ) {

      return target.set(
            0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f, 0.0f,
            1.0f, 1.0f, 1.0f);
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
   public static Vec3 locDelta ( final Transform3 t, final Vec3 target ) {

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
    * @see Quaternion#mult(Quaternion, Vec3, Vec3)
    */
   public static Vec3 multDir ( final Transform3 t, final Vec3 source,
         final Vec3 target ) {

      Quaternion.mult(t.rotation, source, target);
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
    * @see Quaternion#mult(Quaternion, Vec3, Vec3)
    */
   public static Vec3 multVector ( final Transform3 t, final Vec3 source,
         final Vec3 target ) {

      Quaternion.mult(t.rotation, source, target);

      final Vec3 scale = t.scale;
      target.set(target.x * scale.x, target.y * scale.y, target.z * scale.z);

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
    * @see Quaternion#mult(Quaternion, Vec3, Vec3)
    */
   public static Vec3 multPoint ( final Transform3 t, final Vec3 source,
         final Vec3 target ) {

      Quaternion.mult(t.rotation, source, target);

      final Vec3 scale = t.scale;
      target.set(target.x * scale.x, target.y * scale.y, target.z * scale.z);

      final Vec3 loc = t.location;
      target.set(target.x + loc.x, target.y + loc.y, target.z + loc.z);

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
   public static Quaternion rotDelta ( final Transform3 t,
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
   public static Vec3 scaleDelta ( final Transform3 t, final Vec3 target ) {

      return Vec3.sub(t.scale, t.scalePrev, target);
   }

   /**
    * Sets the easing function used to mix the transform's
    * location.
    * 
    * @param locEasing
    *           the easing function
    */
   public static void setLocEasing ( final Vec3.AbstrEasing locEasing ) {

      if (locEasing != null) {
         Transform3.LOC_EASING = locEasing;
      }
   }

   /**
    * Sets the easing function used to mix the transform's
    * rotation.
    * 
    * @param rotEasing
    *           the easing function
    */
   public static void setRotEasing ( final Quaternion.AbstrEasing rotEasing ) {

      if (rotEasing != null) {
         Transform3.ROT_EASING = rotEasing;
      }
   }

   /**
    * Sets the easing function used to mix the transform's
    * scale.
    * 
    * @param scaleEasing
    *           the easing function
    */
   public static void setScaleEasing ( final Vec3.AbstrEasing scaleEasing ) {

      if (scaleEasing != null) {
         Transform3.SCALE_EASING = scaleEasing;
      }
   }

   /**
    * The transform's forward axis.
    */
   protected final Vec3 forward = Vec3.forward(new Vec3());

   /**
    * The transform's location.
    */
   protected final Vec3 location = Vec3.zero(new Vec3());

   /**
    * The previous location. Subtract from the current location
    * to find the delta, or change, in location.
    */
   protected final Vec3 locPrev = Vec3.zero(new Vec3());

   /**
    * The transform's right axis.
    */
   protected final Vec3 right = Vec3.right(new Vec3());

   /**
    * The transform's rotation.
    */
   protected final Quaternion rotation = Quaternion.identity(new Quaternion());

   /**
    * The previous rotation. Subtract from the current rotation
    * to find the delta, or change, in rotation.
    */
   protected final Quaternion rotPrev = Quaternion.identity(new Quaternion());

   /**
    * The transform's scale.
    */
   protected final Vec3 scale = Vec3.one(new Vec3());

   /**
    * The previous scale. Subtract from the current scale to
    * find the delta, or change, in scale.
    */
   protected final Vec3 scalePrev = Vec3.one(new Vec3());

   /**
    * The transform's up axis.
    */
   protected final Vec3 up = Vec3.up(new Vec3());

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
         final float xLoc, final float yLoc, final float zLoc,
         final float real,
         final float xImag, final float yImag, final float zImag,
         final float xScale, final float yScale, final float zScale ) {

      super();
      this.set(
            xLoc, yLoc, zLoc,
            real,
            xImag, yImag, zImag,
            xScale, yScale, zScale);
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

      final int prime = 31;
      int result = 1;
      result = prime * result
            + (this.scale == null ? 0 : this.scale.hashCode());
      result = prime * result
            + (this.location == null ? 0 : this.location.hashCode());
      result = prime * result
            + (this.rotation == null ? 0 : this.rotation.hashCode());
      return result;
   }

   // public Transform3 look ( final Vec3 dir, final Vec3 ref )
   // {
   //
   // final boolean valid = Vec3.look(dir, ref,
   // this.right,
   // this.forward,
   // this.up);
   //
   // if (valid) {
   // this.rotPrev.set(this.rotation);
   // Quaternion.fromAxes(
   // this.right,
   // this.forward,
   // this.up,
   // this.rotation);
   // this.updateAxes();
   // return this;
   // } else {
   // this.updateAxes();
   // return this;
   // }
   // }

   // public Transform3 lookAt ( final Vec3 point, final Vec3
   // ref,
   // final Vec3 dir ) {
   //
   // return this.look(Vec3.sub(point, this.location, dir),
   // ref);
   // }

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
    * Sets the transform's location.
    * 
    * @param x
    *           the x location
    * @param y
    *           the y location
    * @param z
    *           the z location
    * @return this transform
    */
   @Chainable
   public Transform3 moveTo ( final float x, final float y, final float z ) {

      this.locPrev.set(this.location);
      this.location.set(x, y, z);
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
    * @see Transform3#LOC_EASING
    */
   @Chainable
   public Transform3 moveTo ( final Vec3 locNew, final float step ) {

      return this.moveTo(locNew, step, Transform3.LOC_EASING);
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

      return Transform3.identity(this);
   }

   /**
    * Rotates the transform by an axis and angle in radians.
    * 
    * @param radians
    *           the angle
    * @param axis
    *           the axis
    * @return this transform
    */
   @Chainable
   public Transform rotate ( final float radians, final Vec3 axis ) {

      this.rotPrev.set(this.rotation);
      Quaternion.rotate(this.rotPrev, radians, axis, this.rotation);
      this.updateAxes();
      return this;
   }

   /**
    * Rotates the transform to a new orientation.
    * 
    * @param real
    *           the real (w) component
    * @param xImag
    *           the x component
    * @param yImag
    *           the y component
    * @param zImag
    *           the z component
    * @return this transform
    */
   @Chainable
   public Transform3 rotateTo ( final float real, final float xImag,
         final float yImag, final float zImag ) {

      if (!(real == 0.0f && xImag == 0.0f && yImag == 0.0f && zImag == 0.0f)) {
         this.rotPrev.set(this.rotation);
         this.rotation.set(real, xImag, yImag, zImag);
         this.updateAxes();
      }
      return this;
   }

   /**
    * Rotates the transform to a new orientation.
    * 
    * @param rotNew
    *           the new orientation
    * @return this transform
    */
   @Chainable
   public Transform3 rotateTo ( final Quaternion rotNew ) {

      if (Quaternion.isZero(rotNew)) {
         return this;
      }

      // if (Quaternion.isUnit(rotNew)) {
      this.rotPrev.set(this.rotation);
      this.rotation.set(rotNew);
      this.updateAxes();
      // }
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
   public Transform3 rotateTo ( final Quaternion rotNew, final float step ) {

      return this.rotateTo(rotNew, step, Transform3.ROT_EASING);
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
   public Transform3 rotateTo ( final Quaternion rotNew, final float step,
         final Quaternion.AbstrEasing easingFunc ) {

      if (Quaternion.isZero(rotNew)) {
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
    * @see Vec3#mult(Vec3, float, Vec3)
    */
   @Chainable
   public Transform3 scaleBy ( final float scalar ) {

      if (scalar == 0.0f) {
         return this;
      }
      this.scalePrev.set(this.scale);
      Vec3.mult(this.scalePrev, scalar, this.scale);
      return this;
   }

   /**
    * Scales the transform by a non-uniform scalar.
    * 
    * @param nonUniformScale
    *           the scale
    * @return this transform
    * @see Vec3#isNonZero(Vec3)
    * @see Vec3#mult(Vec3, Vec3, Vec3)
    */
   @Chainable
   public Transform3 scaleBy ( final Vec3 nonUniformScale ) {

      if (Vec3.isNonZero(nonUniformScale)) {
         this.scalePrev.set(this.scale);
         Vec3.mult(this.scalePrev, nonUniformScale, this.scale);
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
    * @param x
    *           the size on the x axis
    * @param y
    *           the size on the y axis
    * @param z
    *           the size on the z axis
    * @return this transform
    */
   @Chainable
   public Transform3 scaleTo ( final float x, final float y, final float z ) {

      if (x != 0.0f && y != 0.0f && z != 0.0f) {
         this.scalePrev.set(this.scale);
         this.scale.set(x, y, z);
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
   public Transform3 scaleTo ( final Vec3 scaleNew ) {

      if (Vec3.isNonZero(scaleNew)) {
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
    * @see Vec3#isNonZero(Vec3)
    * @see Transform3#SCALE_EASING
    */
   @Chainable
   public Transform3 scaleTo ( final Vec3 scaleNew, final float step ) {

      if (Vec3.isNonZero(scaleNew)) {
         return this.scaleTo(scaleNew, step, Transform3.SCALE_EASING);
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
    * @see Vec3#isNonZero(Vec3)
    * @see Vec3.AbstrEasing#apply(Vec3, Vec3, Float, Vec3)
    */
   @Chainable
   public Transform3 scaleTo ( final Vec3 scaleNew, final float step,
         final Vec3.AbstrEasing easingFunc ) {

      if (Vec3.isNonZero(scaleNew)) {
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

      this.moveTo(xLoc, yLoc, zLoc);
      this.rotateTo(real, xImag, yImag, zImag);
      this.scaleTo(xScale, yScale, zScale);

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
   public Transform3 set ( final Vec3 locNew, final Quaternion rotNew,
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

      return new StringBuilder().append("{ location: ")
            .append(location.toString()).append(", \nrotation: ")
            .append(rotation.toString()).append(", \nscale: ")
            .append(scale.toString()).append(" }").toString();
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
    * @see Quaternion#getRight(Quaternion, Vec3)
    * @see Quaternion#getForward(Quaternion, Vec3)
    * @see Quaternion#getUp(Quaternion, Vec3)
    */
   @Override
   protected void updateAxes () {

      Quaternion.getRight(this.rotation, this.right);
      Quaternion.getForward(this.rotation, this.forward);
      Quaternion.getUp(this.rotation, this.up);
   }
}
