package camzup.core;

import camzup.core.Utils.EasingFuncArr;
import camzup.core.Utils.EasingFuncObj;

/**
 * Facilitates 3D affine transformations for entities.
 */
public class Transform3 extends Transform {

   /**
    * The transform's forward axis.
    */
   protected final Vec3 forward;

   /**
    * The transform's location.
    */
   protected final Vec3 location;

   /**
    * The previous location. Subtract from the current location to find the
    * delta, or change, in location.
    */
   protected final Vec3 locPrev;

   /**
    * The transform's right axis.
    */
   protected final Vec3 right;

   /**
    * The transform's rotation. Defaults to the identity, (1.0, 0.0, 0.0, 0.0)
    * .
    */
   protected final Quaternion rotation;

   /**
    * The previous rotation. Subtract from the current rotation to find the
    * delta, or change, in rotation.
    */
   protected final Quaternion rotPrev;

   /**
    * The transform's scale.
    */
   protected final Vec3 scale;

   /**
    * The previous scale. Subtract from the current scale to find the delta,
    * or change, in scale.
    */
   protected final Vec3 scalePrev;

   /**
    * The transform's up axis.
    */
   protected final Vec3 up;

   {
      this.location = new Vec3();
      this.rotation = new Quaternion();
      this.scale = Vec3.one(new Vec3());

      this.locPrev = new Vec3();
      this.rotPrev = new Quaternion();
      this.scalePrev = Vec3.one(new Vec3());

      this.right = Vec3.right(new Vec3());
      this.forward = Vec3.forward(new Vec3());
      this.up = Vec3.up(new Vec3());
   }

   /**
    * The default constructor.
    */
   public Transform3 ( ) { super(); }

   /**
    * Creates a transform from loose real numbers.
    *
    * @param xLoc   the x location
    * @param yLoc   the y location
    * @param zLoc   the z location
    * @param real   the rotation real component
    * @param xImag  the rotation x imaginary
    * @param yImag  the rotation y imaginary
    * @param zImag  the rotation z imaginary
    * @param xScale the scale x
    * @param yScale the scale y
    * @param zScale the scale z
    */
   public Transform3 ( final float xLoc, final float yLoc, final float zLoc,
      final float real, final float xImag, final float yImag, final float zImag,
      final float xScale, final float yScale, final float zScale ) {

      super();
      this.set(xLoc, yLoc, zLoc, real, xImag, yImag, zImag, xScale, yScale,
         zScale);
   }

   /**
    * Creates a new 3D transform from a 2D source
    *
    * @param source the source transform
    */
   public Transform3 ( final Transform2 source ) {

      super();
      this.set(source);
   }

   /**
    * Creates a new transform from a source.
    *
    * @param source the source transform
    */
   public Transform3 ( final Transform3 source ) {

      super();
      this.set(source);
   }

   /**
    * Creates a transform from a location, rotation and scale.
    *
    * @param location the location
    * @param rotation the rotation
    * @param scale    the scale
    */
   public Transform3 ( final Vec3 location, final Quaternion rotation,
      final Vec3 scale ) {

      super();
      this.set(location, rotation, scale);
   }

   /**
    * Creates a new transform with the components of this transform.
    *
    * @return the cloned transform
    */
   @Override
   public Transform3 clone ( ) {

      return new Transform3(this.location, this.rotation, this.scale);
   }

   /**
    * Tests this transform for equivalence with an object.
    *
    * @param obj the object
    *
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Transform3 ) obj);
   }

   /**
    * Gets an axis of this transform with an index. For interoperability with
    * Kotlin: <code>b = a[i]</code> and parity with {@link Mat4#get(int)} .
    * Index 0 returns the right axis; 1, the forward axis; 2, the up axis; 3,
    * the translation.
    *
    * @param i the axis index
    *
    * @return the axis
    */
   public Vec4 get ( final int i ) {

      switch ( i ) {
         case 0:
         case -4:
            return new Vec4(this.right, 0.0f);

         case 1:
         case -3:
            return new Vec4(this.forward, 0.0f);

         case 2:
         case -2:
            return new Vec4(this.up, 0.0f);

         case 3:
         case -1:
            return new Vec4(this.location, 1.0f);

         default:
            return new Vec4(0.0f, 0.0f, 0.0f, 0.0f);
      }
   }

   /**
    * Get the transform's axes.
    *
    * @param r the right axis
    * @param f the forward axis
    * @param u the up axis
    *
    * @return this transform
    */

   public Transform3 getAxes ( final Vec3 r, final Vec3 f, final Vec3 u ) {

      r.set(this.right);
      f.set(this.forward);
      u.set(this.up);
      return this;
   }

   /**
    * Gets the transform's rotation as an axis and angle. The angle is
    * returned from the function.
    *
    * @param axis the output axis
    *
    * @return the angle in radians
    *
    * @see Quaternion#toAxisAngle(Quaternion, Vec3)
    */
   public float getAxisAngle ( final Vec3 axis ) {

      return Quaternion.toAxisAngle(this.rotation, axis);
   }

   /**
    * Gets the transform's forward axis.
    *
    * @param target the output vector
    *
    * @return the forward axis
    */
   public Vec3 getForward ( final Vec3 target ) {

      return target.set(this.forward);
   }

   /**
    * Gets the transform's location
    *
    * @param target the output vector
    *
    * @return the location
    */
   public Vec3 getLocation ( final Vec3 target ) {

      return target.set(this.location);
   }

   /**
    * Gets the transform's previous location
    *
    * @param target the output vector
    *
    * @return the previous location
    */
   public Vec3 getLocPrev ( final Vec3 target ) {

      return target.set(this.locPrev);
   }

   /**
    * Gets the transform's right axis.
    *
    * @param target the output vector
    *
    * @return the right axis
    */
   public Vec3 getRight ( final Vec3 target ) {

      return target.set(this.right);
   }

   /**
    * Gets the transform's rotation
    *
    * @param target the output quaternion
    *
    * @return the rotation
    */
   public Quaternion getRotation ( final Quaternion target ) {

      return target.set(this.rotation);
   }

   /**
    * Gets the transform's inverse rotation.
    *
    * @param target the output quaternion
    *
    * @return the inverse rotation
    *
    * @see Quaternion#inverse(Quaternion, Quaternion)
    */
   public Quaternion getRotInverse ( final Quaternion target ) {

      return Quaternion.inverse(this.rotation, target);
   }

   /**
    * Gets the transform's previous rotation
    *
    * @param target the output quaternion
    *
    * @return the previous rotation
    */
   public Quaternion getRotPrev ( final Quaternion target ) {

      return target.set(this.rotPrev);
   }

   /**
    * Gets the transform's scale.
    *
    * @param target the output vector
    *
    * @return the scale
    */
   public Vec3 getScale ( final Vec3 target ) {

      return target.set(this.scale);
   }

   /**
    * Gets the transform's previous scale.
    *
    * @param target the output vector
    *
    * @return the previous scale
    */
   public Vec3 getScalePrev ( final Vec3 target ) {

      return target.set(this.scalePrev);
   }

   /**
    * Gets the transform's up axis.
    *
    * @param target the output vector
    *
    * @return the up axis
    */
   public Vec3 getUp ( final Vec3 target ) { return target.set(this.up); }

   /**
    * Returns a hash code for this transform based on its location, rotation
    * and scale.
    *
    * @return the hash code
    *
    * @see Vec3#hashCode()
    * @see Quaternion#hashCode()
    */
   @Override
   public int hashCode ( ) {

      return ( ( IUtils.MUL_BASE ^ ( this.location == null ? 0 : this.location
         .hashCode() ) ) * IUtils.HASH_MUL ^ ( this.rotation == null ? 0
            : this.rotation.hashCode() ) ) * IUtils.HASH_MUL ^ ( this.scale
               == null ? 0 : this.scale.hashCode() );
   }

   /**
    * Orients the transform to look at a target point.<br>
    * <br>
    * The transform eases toward the look at rotation because discontinuities
    * arise with using a look at matrix when a transform's look direction is
    * parallel with the world up axis.
    *
    * @param point      the target point
    * @param step       the step
    * @param handedness the handedness
    *
    * @return this transform
    *
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    * @see Quaternion#fromDir(Vec3, Handedness, Quaternion, Vec3, Vec3, Vec3)
    * @see Quaternion#mix(Quaternion, Quaternion, float, Quaternion)
    */

   public Transform3 lookAt ( final Vec3 point, final float step,
      final Handedness handedness ) {

      this.rotPrev.set(this.rotation);
      Vec3.sub(point, this.location, this.forward);

      Quaternion.fromDir(this.forward, handedness, this.rotation, this.right,
         this.forward, this.up);

      Quaternion.mix(this.rotPrev, this.rotation, step, this.rotation);

      this.updateAxes();

      return this;
   }

   /**
    * Moves the transform by a direction to a new location.
    *
    * @param dir the direction
    *
    * @return this transform
    *
    * @see Transform3#moveByGlobal(Vec3)
    */

   public Transform3 moveBy ( final Vec3 dir ) {

      return this.moveByGlobal(dir);
   }

   /**
    * Moves the transform by a direction to a new location.
    *
    * @param dir the direction
    *
    * @return this transform
    *
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */

   public Transform3 moveByGlobal ( final Vec3 dir ) {

      this.locPrev.set(this.location);
      Vec3.add(this.locPrev, dir, this.location);

      return this;
   }

   /**
    * Moves the transform by a direction multiplied by the transform's
    * rotation. Uses the formula:<br>
    * <br>
    * move ( dir ) := location + ( scale * ( rotation * direction ) )
    *
    * @param dir the direction
    *
    * @return this transform
    *
    * @see Quaternion#mulVector(Quaternion, Vec3, Vec3)
    * @see Vec3#mul(Vec3, Vec3, Vec3)
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */

   public Transform3 moveByLocal ( final Vec3 dir ) {

      this.locPrev.set(this.location);
      Quaternion.mulVector(this.rotation, dir, this.location);
      Vec3.mul(this.location, this.scale, this.location);
      Vec3.add(this.locPrev, this.location, this.location);

      return this;
   }

   /**
    * Sets the transforms' location.
    *
    * @param locNew the new location
    *
    * @return this transform
    */

   public Transform3 moveTo ( final Vec3 locNew ) {

      this.locPrev.set(this.location);
      this.location.set(locNew);

      return this;
   }

   /**
    * Eases the transform to a location by a step. The static easing function
    * is used.
    *
    * @param locNew the new location
    * @param step   the step in [0.0, 1.0]
    *
    * @return this transform
    */

   public Transform3 moveTo ( final Vec3 locNew, final float step ) {

      return this.moveTo(locNew, step, Transform3.EASING.loc);
   }

   /**
    * Eases the transform to a location by a step. The kind of easing is
    * specified by a Vec3 easing function.
    *
    * @param locNew     the new location
    * @param step       the step in [0.0, 1.0]
    * @param easingFunc the easing function
    *
    * @return this transform
    *
    * @see Vec3.AbstrEasing#apply(Vec3, Vec3, Float, Vec3)
    */

   public Transform3 moveTo ( final Vec3 locNew, final float step,
      final Vec3.AbstrEasing easingFunc ) {

      this.locPrev.set(this.location);
      easingFunc.apply(this.locPrev, locNew, step, this.location);
      return this;
   }

   /**
    * Resets this transform to the identity. This also resets the fields which
    * store the previous location, rotation and scale.
    *
    * @return this transform
    *
    * @see Vec3#one(Vec3)
    * @see Vec3#right(Vec3)
    * @see Vec3#forward(Vec3)
    * @see Vec3#up(Vec3)
    */

   public Transform3 reset ( ) {

      this.locPrev.reset();
      this.rotPrev.reset();
      Vec3.one(this.scalePrev);

      this.location.reset();
      this.rotation.reset();
      Vec3.one(this.scale);

      Vec3.right(this.right);
      Vec3.forward(this.forward);
      Vec3.up(this.up);

      return this;
   }

   /**
    * Rotates the transform by adding a rotation, then normalizing. Updates
    * the transform's axes.
    *
    * @param rot the rotation
    *
    * @return this transform
    *
    * @see Quaternion#addNorm(Quaternion, Quaternion, Quaternion)
    * @see Transform3#updateAxes()
    */

   public Transform3 rotateBy ( final Quaternion rot ) {

      this.rotPrev.set(this.rotation);
      Quaternion.addNorm(this.rotPrev, rot, this.rotation);
      this.updateAxes();
      return this;
   }

   /**
    * Rotates the transform to a new orientation, then updates the transform's
    * axes.
    *
    * @param rotNew the new orientation
    *
    * @return this transform
    *
    * @see Transform3#updateAxes()
    */

   public Transform3 rotateTo ( final Quaternion rotNew ) {

      if ( Quaternion.none(rotNew) ) { return this; }

      this.rotPrev.set(this.rotation);
      this.rotation.set(rotNew);
      this.updateAxes();
      return this;
   }

   /**
    * Eases the transform toward a new orientation by a step in [0.0, 1.0] .
    *
    * @param rotNew the new orientation
    * @param step   the step
    *
    * @return this transform
    */

   public Transform3 rotateTo ( final Quaternion rotNew, final float step ) {

      return this.rotateTo(rotNew, step, Transform3.EASING.rot);
   }

   /**
    * Eases the transform toward a new orientation by a step in [0.0, 1.0]
    * using the specified easing function. Updates the transform's axes.
    *
    * @param rotNew     the new orientation
    * @param step       the step
    * @param easingFunc the easing function
    *
    * @return this transform
    */

   public Transform3 rotateTo ( final Quaternion rotNew, final float step,
      final Quaternion.AbstrEasing easingFunc ) {

      if ( Quaternion.none(rotNew) ) { return this; }
      this.rotPrev.set(this.rotation);
      easingFunc.apply(this.rotPrev, rotNew, step, this.rotation);
      this.updateAxes();
      return this;
   }

   /**
    * Rotates this transform around the x axis by an angle in radians. Updates
    * the transform's axes.<br>
    * <br>
    * Beware that using sequences of orthonormal rotations will result in
    * gimbal lock. Updates the transform's axes.
    *
    * @param radians the angle
    *
    * @return this transform
    *
    * @see Quaternion#rotateX(Quaternion, float, Quaternion)
    * @see Transform3#updateAxes()
    */

   public Transform3 rotateX ( final float radians ) {

      this.rotPrev.set(this.rotation);
      Quaternion.rotateX(this.rotPrev, radians, this.rotation);
      this.updateAxes();
      return this;
   }

   /**
    * Rotates this transform around the y axis by an angle in radians. Updates
    * the transform's axes.<br>
    * <br>
    * Beware that using sequences of orthonormal rotations will result in
    * gimbal lock.
    *
    * @param radians the angle
    *
    * @return this transform
    *
    * @see Quaternion#rotateY(Quaternion, float, Quaternion)
    * @see Transform3#updateAxes()
    */

   public Transform3 rotateY ( final float radians ) {

      this.rotPrev.set(this.rotation);
      Quaternion.rotateY(this.rotPrev, radians, this.rotation);
      this.updateAxes();
      return this;
   }

   /**
    * Rotates this transform around the z axis by an angle in radians. Updates
    * the transform's axes.<br>
    * <br>
    * Beware that using sequences of orthonormal rotations will result in
    * gimbal lock.
    *
    * @param radians the angle
    *
    * @return this transform
    *
    * @see Quaternion#rotateZ(Quaternion, float, Quaternion)
    * @see Transform3#updateAxes()
    */

   public Transform3 rotateZ ( final float radians ) {

      this.rotPrev.set(this.rotation);
      Quaternion.rotateZ(this.rotPrev, radians, this.rotation);
      this.updateAxes();
      return this;
   }

   /**
    * Scales the transform by a uniform scalar.
    *
    * @param scalar the scalar
    *
    * @return this transform
    *
    * @see Vec3#mul(Vec3, float, Vec3)
    */

   public Transform3 scaleBy ( final float scalar ) {

      if ( scalar != 0.0f ) {
         this.scalePrev.set(this.scale);
         Vec3.mul(this.scalePrev, scalar, this.scale);
      }
      return this;
   }

   /**
    * Scales the transform by a non-uniform scalar.<br>
    * <br>
    * Non-uniform scaling may lead to improper shading of a mesh when lit.
    *
    * @param nonUniformScale the scale
    *
    * @return this transform
    *
    * @see Vec3#all(Vec3)
    * @see Vec3#mul(Vec3, Vec3, Vec3)
    */

   public Transform3 scaleBy ( final Vec3 nonUniformScale ) {

      if ( Vec3.all(nonUniformScale) ) {
         this.scalePrev.set(this.scale);
         Vec3.mul(this.scalePrev, nonUniformScale, this.scale);
      }
      return this;
   }

   /**
    * Scales the transform to a uniform size.
    *
    * @param scalar the size
    *
    * @return this transform
    */

   public Transform3 scaleTo ( final float scalar ) {

      if ( scalar != 0.0f ) {
         this.scalePrev.set(this.scale);
         this.scale.set(scalar, scalar, scalar);
      }
      return this;
   }

   /**
    * Scales the transform to a non-uniform size.<br>
    * <br>
    * Non-uniform scaling may lead to improper shading of a mesh when lit.
    *
    * @param scaleNew the new scale
    *
    * @return this transform
    *
    * @see Vec3#all(Vec3)
    */

   public Transform3 scaleTo ( final Vec3 scaleNew ) {

      if ( Vec3.all(scaleNew) ) {
         this.scalePrev.set(this.scale);
         this.scale.set(scaleNew);
      }
      return this;
   }

   /**
    * Eases the transform to a scale by a step. The static easing function is
    * used.<br>
    * <br>
    * Non-uniform scaling may lead to improper shading of a mesh when lit.
    *
    * @param scaleNew the new scale
    * @param step     the step in [0.0, 1.0]
    *
    * @return this transform
    *
    * @see Vec3#all(Vec3)
    */

   public Transform3 scaleTo ( final Vec3 scaleNew, final float step ) {

      if ( Vec3.all(scaleNew) ) {
         return this.scaleTo(scaleNew, step, Transform3.EASING.scale);
      }
      return this;
   }

   /**
    * Eases the transform to a scale by a step. The kind of easing is
    * specified by a Vec3 easing function.<br>
    * <br>
    * Non-uniform scaling may lead to improper shading of a mesh when lit.
    *
    * @param scaleNew   the new scale
    * @param step       the step in [0.0, 1.0]
    * @param easingFunc the easing function
    *
    * @return this transform
    *
    * @see Vec3#all(Vec3)
    * @see Vec3.AbstrEasing#apply(Vec3, Vec3, Float, Vec3)
    */

   public Transform3 scaleTo ( final Vec3 scaleNew, final float step,
      final Vec3.AbstrEasing easingFunc ) {

      if ( Vec3.all(scaleNew) ) {
         this.scalePrev.set(this.scale);
         easingFunc.apply(this.scalePrev, scaleNew, step, this.scale);
      }
      return this;
   }

   /**
    * Sets this transform from loose real numbers. Updates the transform's
    * axes.
    *
    * @param xLoc   the x location
    * @param yLoc   the y location
    * @param zLoc   the z location
    * @param real   the rotation real component
    * @param xImag  the rotation x imaginary
    * @param yImag  the rotation y imaginary
    * @param zImag  the rotation z imaginary
    * @param xScale the scale x
    * @param yScale the scale y
    * @param zScale the scale z
    *
    * @return this transform
    *
    * @see Transform3#updateAxes()
    */

   public Transform3 set ( final float xLoc, final float yLoc, final float zLoc,
      final float real, final float xImag, final float yImag, final float zImag,
      final float xScale, final float yScale, final float zScale ) {

      this.locPrev.set(this.location);
      this.location.set(xLoc, yLoc, zLoc);

      if ( real != 0.0f || xImag != 0.0f || yImag != 0.0f || zImag != 0.0f ) {
         this.rotPrev.set(this.rotation);
         this.rotation.set(real, xImag, yImag, zImag);
         this.updateAxes();
      }

      this.scalePrev.set(this.scale);
      if ( xScale != 0.0f ) { this.scale.x = xScale; }
      if ( yScale != 0.0f ) { this.scale.y = yScale; }
      if ( zScale != 0.0f ) { this.scale.z = zScale; }

      return this;
   }

   /**
    * Promotes a 2D transform to a 3D transform. Updates this transform's
    * axes.
    *
    * @param source the source transform
    *
    * @return this transform
    *
    * @see Quaternion#fromAngle(float, Quaternion)
    * @see Transform3#updateAxes()
    */

   public Transform3 set ( final Transform2 source ) {

      this.location.set(source.location, 0.0f);
      Quaternion.fromAngle(source.rotation, this.rotation);
      this.updateAxes();
      this.scale.set(source.scale, 1.0f);

      return this;
   }

   /**
    * Sets this transform to the components of another.
    *
    * @param source the source transform
    *
    * @return this transform
    */

   public Transform3 set ( final Transform3 source ) {

      return this.set(source.location, source.rotation, source.scale);
   }

   /**
    * Sets the components of the transform.
    *
    * @param locNew   the new location
    * @param rotNew   the new rotation
    * @param scaleNew the new scale
    *
    * @return this transform
    */

   public Transform3 set ( final Vec3 locNew, final Quaternion rotNew,
      final Vec3 scaleNew ) {

      this.moveTo(locNew);
      this.rotateTo(rotNew);
      this.scaleTo(scaleNew);

      return this;
   }

   /**
    * A helper function to set the transform's from either separate vectors or
    * from the columns of a matrix. The transform's location and scale remain
    * unchanged.
    *
    * @param xRight   m00 : right x
    * @param yForward m11 : forward y
    * @param zUp      m22 : up z
    * @param zForward m21 : forward z
    * @param yUp      m12 : up y
    * @param xUp      m02 : up x
    * @param zRight   m20 : right z
    * @param yRight   m10 : right y
    * @param xForward m01 : forward x
    *
    * @return the transform
    *
    * @see Quaternion#fromAxes(float, float, float, float, float, float,
    *      float, float, float, Quaternion)
    * @see Transform3#updateAxes()
    */
   public Transform3 setAxes ( final float xRight, final float yForward,
      final float zUp, final float zForward, final float yUp, final float xUp,
      final float zRight, final float yRight, final float xForward ) {

      this.rotPrev.set(this.rotation);
      Quaternion.fromAxes(xRight, yForward, zUp, zForward, yUp, xUp, zRight,
         yRight, xForward, this.rotation);

      /* Update needed because the loose floats may not be normalized. */
      this.updateAxes();

      return this;
   }

   /**
    * Sets a transform's rotation to the provided axes. The transform's
    * location and scale remain unchanged.
    *
    * @param right   the right axis
    * @param forward the forward axis
    * @param up      the up axis
    *
    * @return the transform
    */
   public Transform3 setAxes ( final Vec3 right, final Vec3 forward,
      final Vec3 up ) {

      return this.setAxes(right.x, forward.y, up.z, forward.z, up.y, up.x,
         right.z, right.y, forward.x);
   }

   /**
    * Returns a string representation of this transform according to its
    * string format.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of this transform according to its
    * string format.
    *
    * @param places the number of places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(354);
      sb.append("{ location: ");
      sb.append(this.location.toString(places));
      sb.append(", rotation: ");
      sb.append(this.rotation.toString(places));
      sb.append(", scale: ");
      sb.append(this.scale.toString(places));
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Wraps the transform's location around a periodic range as defined by an
    * upper and lower bound: lower bounds inclusive; upper bounds exclusive.
    *
    * @param lb the lower bound
    * @param ub the upper bound
    *
    * @return the wrapped transform
    */

   public Transform3 wrap ( final Vec3 lb, final Vec3 ub ) {

      this.locPrev.set(this.location);
      Vec3.wrap(this.locPrev, lb, ub, this.location);
      return this;
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes, i.e., to
    * compare how transforms look in Blender (the control) versus in the
    * library (the test).
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode ( ) {

      final String rotationMode = "\"QUATERNION\"";
      final StringBuilder pyCd = new StringBuilder(256);
      pyCd.append("{\"location\": ");
      pyCd.append(this.location.toBlenderCode());
      pyCd.append(", \"rotation_mode\": ");
      pyCd.append(rotationMode);
      pyCd.append(", \"rotation_quaternion\": ");
      pyCd.append(this.rotation.toBlenderCode());
      pyCd.append(", \"scale\": ");
      pyCd.append(this.scale.toBlenderCode());
      pyCd.append('}');
      return pyCd.toString();
   }

   /**
    * Tests the equivalence between this and another transform.
    *
    * @param t the transform
    *
    * @return the evaluation
    */
   protected boolean equals ( final Transform3 t ) {

      if ( this.scale == null ) {
         if ( t.scale != null ) { return false; }
      } else if ( !this.scale.equals(t.scale) ) { return false; }

      if ( this.location == null ) {
         if ( t.location != null ) { return false; }
      } else if ( !this.location.equals(t.location) ) { return false; }

      if ( this.rotation == null ) {
         if ( t.rotation != null ) { return false; }
      } else if ( !this.rotation.equals(t.rotation) ) { return false; }

      return true;
   }

   /**
    * Updates the local axes of the transform based on its rotation.
    *
    * @see Quaternion#toAxes(Quaternion, Vec3, Vec3, Vec3)
    */
   @Override
   protected void updateAxes ( ) {

      Quaternion.toAxes(this.rotation, this.right, this.forward, this.up);
   }

   /**
    * The default easing function.
    */
   private static Easing EASING;

   static {
      Transform3.EASING = new Easing();
   }

   /**
    * A helper function to set the transform's from either separate vectors or
    * from the columns of a matrix. The transform's translation is set to
    * zero; its scale, to one.
    *
    * @param xRight   m00 : right x
    * @param yForward m11 : forward y
    * @param zUp      m22 : up z
    * @param zForward m21 : forward z
    * @param yUp      m12 : up y
    * @param xUp      m02 : up x
    * @param zRight   m20 : right z
    * @param yRight   m10 : right y
    * @param xForward m01 : forward
    * @param target   the output transform
    *
    * @return the transform
    *
    * @see Quaternion#fromAxes(float, float, float, float, float, float,
    *      float, float, float, Quaternion)
    * @see Vec3#one(Vec3)
    */
   public static Transform3 fromAxes ( final float xRight, final float yForward,
      final float zUp, final float zForward, final float yUp, final float xUp,
      final float zRight, final float yRight, final float xForward,
      final Transform3 target ) {

      // target.locPrev.reset();
      // target.rotPrev.reset();
      // Vec3.one(target.scalePrev);

      target.scalePrev.set(target.scale);
      target.locPrev.set(target.location);
      target.rotPrev.set(target.rotation);

      Quaternion.fromAxes(xRight, yForward, zUp, zForward, yUp, xUp, zRight,
         yRight, xForward, target.rotation);

      /* Update needed because the loose floats may not be normalized. */
      target.updateAxes();

      target.location.reset();
      Vec3.one(target.scale);

      return target;
   }

   /**
    * Creates a transform from axes. The transform's translation is set to
    * zero; its scale, to one.
    *
    * @param right   the right axis
    * @param forward the forward axis
    * @param up      the up axis
    * @param target  the output transform
    *
    * @return the transform
    *
    * @see Transform3#fromAxes(float, float, float, float, float, float,
    *      float, float, float, Transform3)
    */
   public static Transform3 fromAxes ( final Vec3 right, final Vec3 forward,
      final Vec3 up, final Transform3 target ) {

      return Transform3.fromAxes(right.x, forward.y, up.z, forward.z, up.y,
         up.x, right.z, right.y, forward.x, target);
   }

   /**
    * Creates a transform from a ray. The transform's translation is set to
    * the ray's origin; its scale, to one.<br>
    * <br>
    * Discontinuities arise when using a look at matrix when a transform's
    * look direction is parallel with the world up axis. To animate a
    * transform use the instance method
    * {@link Transform3#lookAt(Vec3, float, Handedness)} .
    *
    * @param ray        the direction
    * @param handedness the handedness
    * @param target     the output transform
    *
    * @return the transform
    *
    * @see Transform3#fromDir(Vec3, Handedness, Transform3)
    * @see Transform3#moveTo(Vec3)
    */
   public static Transform3 fromDir ( final Ray3 ray,
      final Handedness handedness, final Transform3 target ) {

      Transform3.fromDir(ray.dir, handedness, target);
      target.moveTo(ray.origin);
      return target;
   }

   /**
    * Creates a transform from a direction. The transform's translation is set
    * to zero; its scale, to one.<br>
    * <br>
    * Discontinuities arise when using a look at matrix when a transform's
    * look direction is parallel with the world up axis. To animate a
    * transform use the instance method
    * {@link Transform3#lookAt(Vec3, float, Handedness)} .
    *
    * @param dir        the direction
    * @param handedness handedness
    * @param target     the output transform
    *
    * @return the transform
    *
    * @see Quaternion#fromDir(Vec3, Handedness, Quaternion, Vec3, Vec3, Vec3)
    */
   public static Transform3 fromDir ( final Vec3 dir,
      final Handedness handedness, final Transform3 target ) {

      target.locPrev.set(target.location);
      target.rotPrev.set(target.rotation);
      target.scalePrev.set(target.scale);

      Quaternion.fromDir(dir, handedness, target.rotation, target.right,
         target.forward, target.up);

      target.location.reset();
      Vec3.one(target.scale);

      return target;
   }

   /**
    * Gets the string representation of the default easing function.
    *
    * @return the string
    */
   public static String getEasingString ( ) {

      return Transform3.EASING.toString();
   }

   /**
    * Sets the transform to the identity.
    *
    * @param target the output transform
    *
    * @return the identity
    *
    * @see Vec3#one(Vec3)
    * @see Vec3#right(Vec3)
    * @see Vec3#forward(Vec3)
    * @see Vec3#up(Vec3)
    */
   public static Transform3 identity ( final Transform3 target ) {

      // target.locPrev.reset();
      // target.rotPrev.reset();
      // Vec3.one(target.scalePrev);

      target.locPrev.set(target.location);
      target.rotPrev.set(target.rotation);
      target.scalePrev.set(target.scale);

      target.location.reset();
      target.rotation.reset();
      Vec3.one(target.scale);

      Vec3.right(target.right);
      Vec3.forward(target.forward);
      Vec3.up(target.up);

      return target;
   }

   /**
    * Multiplies a direction by a transform's inverse. This rotates the
    * direction by the inverse quaternion.
    *
    * @param t      the transform
    * @param source the input direction
    * @param target the output direction
    *
    * @return the direction
    *
    * @see Quaternion#invMulVector(Quaternion, Vec3, Vec3)
    */
   public static Vec3 invMulDir ( final Transform3 t, final Vec3 source,
      final Vec3 target ) {

      Quaternion.invMulVector(t.rotation, source, target);

      return target;
   }

   /**
    * Multiplies a point by a transform's inverse. This subtracts the
    * translation from the point, divides the point by the scale, then rotates
    * by the inverse quaternion.
    *
    * @param t      the transform
    * @param source the input point
    * @param target the output point
    *
    * @return the point
    *
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    * @see Vec3#div(Vec3, Vec3, Vec3)
    * @see Quaternion#invMulVector(Quaternion, Vec3, Vec3)
    */
   public static Vec3 invMulPoint ( final Transform3 t, final Vec3 source,
      final Vec3 target ) {

      Vec3.sub(source, t.location, target);
      Vec3.div(target, t.scale, target);
      Quaternion.invMulVector(t.rotation, target, target);

      return target;
   }

   /**
    * Multiplies a vector by the transform's inverse. This divides the vector
    * by the scale, then rotates by the inverse quaternion.
    *
    * @param t      the transform
    * @param source the input vector
    * @param target the output vector
    *
    * @return the vector
    *
    * @see Vec3#div(Vec3, Vec3, Vec3)
    * @see Quaternion#invMulVector(Quaternion, Vec3, Vec3)
    */
   public static Vec3 invMulVector ( final Transform3 t, final Vec3 source,
      final Vec3 target ) {

      Vec3.div(source, t.scale, target);
      Quaternion.invMulVector(t.rotation, target, target);

      return target;
   }

   /**
    * Finds the difference between the current and previous location of a
    * transform.
    *
    * @param t      the transform
    * @param target the output vector
    *
    * @return the location delta
    *
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    */
   public static Vec3 locDelta ( final Transform3 t, final Vec3 target ) {

      return Vec3.sub(t.location, t.locPrev, target);
   }

   /**
    * Returns the maximum dimension occupied by a transform.
    *
    * @param t the transform
    *
    * @return the maximum dimension
    *
    * @see Utils#max(float, float, float)
    */
   public static float maxDimension ( final Transform3 t ) {

      return Utils.max(t.scale.x, t.scale.y, t.scale.z);
   }

   /**
    * Returns the minimum dimension occupied by a transform.
    *
    * @param t the transform
    *
    * @return the minimum dimension
    *
    * @see Utils#min(float, float, float)
    */
   public static float minDimension ( final Transform3 t ) {

      return Utils.min(t.scale.x, t.scale.y, t.scale.z);
   }

   /**
    * Mixes two transforms together by a step in [0.0, 1.0] .
    *
    * @param origin the original transform
    * @param dest   the destination transform
    * @param step   the step
    * @param target the output transform
    *
    * @return the mix
    *
    * @see Transform3#EASING
    */
   public static Transform3 mix ( final Transform3 origin,
      final Transform3 dest, final float step, final Transform3 target ) {

      return Transform3.EASING.apply(origin, dest, step, target);
   }

   /**
    * Eases an array through a series of transforms according to a step in
    * [0.0, 1.0] .
    *
    * @param frames the frames
    * @param step   the step
    * @param target the output transform
    *
    * @return the mix
    *
    * @see Transform3#EASING
    */
   public static Transform3 mix ( final Transform3[] frames, final float step,
      final Transform3 target ) {

      return Transform3.EASING.apply(frames, step, target);
   }

   /**
    * Multiplies a direction by a transform. This rotates the direction by the
    * transform's rotation.
    *
    * @param t      the transform
    * @param source the input direction
    * @param target the output direction
    *
    * @return the direction
    *
    * @see Quaternion#mulVector(Quaternion, Vec3, Vec3)
    */
   public static Vec3 mulDir ( final Transform3 t, final Vec3 source,
      final Vec3 target ) {

      Quaternion.mulVector(t.rotation, source, target);
      return target;
   }

   /**
    * Multiplies a point by a transform. This rotates the point, multiplies
    * the point by the scale, then adds the translation.
    *
    * @param t      the transform
    * @param source the input point
    * @param target the output point
    *
    * @return the point
    *
    * @see Quaternion#mulVector(Quaternion, Vec3, Vec3)
    * @see Vec3#mul(Vec3, Vec3, Vec3)
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   public static Vec3 mulPoint ( final Transform3 t, final Vec3 source,
      final Vec3 target ) {

      Quaternion.mulVector(t.rotation, source, target);
      Vec3.mul(target, t.scale, target);
      Vec3.add(target, t.location, target);
      return target;
   }

   /**
    * Multiplies a vector by a transform. This rotates the vector by the
    * transform's rotation and then multiplies it by the transform's scale.
    *
    * @param t      the transform
    * @param source the input vector
    * @param target the output vector
    *
    * @return the vector
    *
    * @see Quaternion#mulVector(Quaternion, Vec3, Vec3)
    * @see Vec3#mul(Vec3, Vec3, Vec3)
    */
   public static Vec3 mulVector ( final Transform3 t, final Vec3 source,
      final Vec3 target ) {

      Quaternion.mulVector(t.rotation, source, target);
      Vec3.mul(target, t.scale, target);
      return target;
   }

   /**
    * Finds the difference between the current and previous rotation of a
    * transform.
    *
    * @param t      the transform
    * @param target the output quaternion
    *
    * @return the rotation delta
    *
    * @see Quaternion#subNorm(Quaternion, Quaternion, Quaternion)
    */
   public static Quaternion rotDelta ( final Transform3 t,
      final Quaternion target ) {

      return Quaternion.subNorm(t.rotation, t.rotPrev, target);
   }

   /**
    * Finds the difference between the current and previous scale of a
    * transform.
    *
    * @param t      the transform
    * @param target the output vector
    *
    * @return the scale delta
    *
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    */
   public static Vec3 scaleDelta ( final Transform3 t, final Vec3 target ) {

      return Vec3.sub(t.scale, t.scalePrev, target);
   }

   /**
    * Sets the default easing function used by the transform.
    *
    * @param easing the easing function.
    */
   public static void setEasing ( final Easing easing ) {

      if ( easing != null ) { Transform3.EASING = easing; }
   }

   /**
    * An easing function to facilitate animation between multiple transforms.
    */
   public static class Easing implements EasingFuncArr < Transform3 >,
      EasingFuncObj < Transform3 > {

      /**
       * The location easing function.
       */
      public final Vec3.AbstrEasing loc;

      /**
       * The rotation easing function.
       */
      public final Quaternion.AbstrEasing rot;

      /**
       * The scale easing function.
       */
      public final Vec3.AbstrEasing scale;

      /**
       * The default constructor.
       */
      public Easing ( ) {

         this.loc = new Vec3.Lerp();
         this.rot = new Quaternion.Slerp();
         this.scale = new Vec3.SmoothStep();
      }

      /**
       * The easing constructor.
       *
       * @param locEasing   the location easing function
       * @param rotEasing   the rotation easing function
       * @param scaleEasing the scale easing function
       */
      public Easing ( final Vec3.AbstrEasing locEasing,
         final Quaternion.AbstrEasing rotEasing,
         final Vec3.AbstrEasing scaleEasing ) {

         this.loc = locEasing;
         this.rot = rotEasing;
         this.scale = scaleEasing;
      }

      /**
       * Eases between an origin and destination transform by a step in [0.0,
       * 1.0].
       *
       * @param origin the origin
       * @param dest   the destination
       * @param step   the step
       * @param target the output transform
       *
       * @return the eased transform
       */
      @Override
      public Transform3 apply ( final Transform3 origin, final Transform3 dest,
         final Float step, final Transform3 target ) {

         if ( step <= 0.0f ) { return target.set(origin); }
         if ( step >= 1.0f ) { return target.set(dest); }
         return this.applyUnclamped(origin, dest, step, target);
      }

      /**
       * Eases between transforms in an array by a step in the range [0.0, 1.0].
       *
       * @param arr    the transform array
       * @param step   the step
       * @param target the output transform
       */
      @Override
      public Transform3 apply ( final Transform3[] arr, final Float step,
         final Transform3 target ) {

         final int len = arr.length;
         if ( len == 1 || step <= 0.0f ) { return target.set(arr[0]); }
         if ( step >= 1.0f ) { return target.set(arr[len - 1]); }

         final float scaledStep = step * ( len - 1 );
         final int i = ( int ) scaledStep;
         return this.applyUnclamped(arr[i], arr[i + 1], scaledStep - i, target);
      }

      /**
       * Eases between an origin and destination transform by a step in [0.0,
       * 1.0].
       *
       * @param origin the origin
       * @param dest   the destination
       * @param step   the step
       * @param target the output transform
       *
       * @return the eased transform
       */
      public Transform3 applyUnclamped ( final Transform3 origin,
         final Transform3 dest, final float step, final Transform3 target ) {

         target.locPrev.set(target.location);
         this.loc.applyUnclamped(origin.location, dest.location, step,
            target.location);

         target.rotPrev.set(target.rotation);
         this.rot.applyUnclamped(origin.rotation, dest.rotation, step,
            target.rotation);
         target.updateAxes();

         target.scalePrev.set(target.scale);
         this.loc.applyUnclamped(origin.scale, dest.scale, step, target.scale);

         return target;
      }

      /**
       * Returns a string representation of this easing function.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

}
