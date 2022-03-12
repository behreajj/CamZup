package camzup.core;

import camzup.core.Utils.EasingFuncArr;
import camzup.core.Utils.EasingFuncObj;

/**
 * Facilitates 3D affine transformations for entities.
 */
public class Transform3 implements Comparable < Transform3 >, ISpatial3,
   IOriented3, IVolume3 {

   /**
    * The transform's forward axis.
    */
   protected final Vec3 forward = Vec3.forward(new Vec3());

   /**
    * The transform's location.
    */
   protected final Vec3 location = new Vec3();

   /**
    * The previous location. Subtract from the current location to find the
    * delta, or change, in location.
    */
   protected final Vec3 locPrev = new Vec3();

   /**
    * The transform's right axis.
    */
   protected final Vec3 right = Vec3.right(new Vec3());

   /**
    * The transform's rotation. Defaults to the identity, (1.0, 0.0, 0.0, 0.0)
    * .
    */
   protected final Quaternion rotation = new Quaternion();

   /**
    * The previous rotation. Subtract from the current rotation to find the
    * delta, or change, in rotation.
    */
   protected final Quaternion rotPrev = new Quaternion();

   /**
    * The transform's scale.
    */
   protected final Vec3 scale = Vec3.one(new Vec3());

   /**
    * The previous scale. Subtract from the current scale to find the delta,
    * or change, in scale.
    */
   protected final Vec3 scalePrev = Vec3.one(new Vec3());

   /**
    * The transform's up axis.
    */
   protected final Vec3 up = Vec3.up(new Vec3());

   /**
    * The default constructor.
    */
   public Transform3 ( ) {}

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

      this.set(xLoc, yLoc, zLoc, real, xImag, yImag, zImag, xScale, yScale,
         zScale);
   }

   /**
    * Creates a new 3D transform from a 2D source
    *
    * @param source the source transform
    */
   public Transform3 ( final Transform2 source ) {

      this.set(source);
   }

   /**
    * Creates a new transform from a source.
    *
    * @param source the source transform
    */
   public Transform3 ( final Transform3 source ) {

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

      this.set(location, rotation, scale);
   }

   /**
    * Compares this transform to another based on a comparison between
    * locations.
    *
    * @param tr the other transform
    *
    * @return the evaluation
    */
   @Override
   public int compareTo ( final Transform3 tr ) {

      return this.location.compareTo(tr.location);
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
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Transform3 ) obj);
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
   @Override
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
   @Override
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
   @Override
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

      return ( ( IUtils.MUL_BASE ^ this.location.hashCode() ) * IUtils.HASH_MUL
         ^ this.rotation.hashCode() ) * IUtils.HASH_MUL ^ this.scale.hashCode();
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
    * @see Transform3#lookIn(Vec3, float, Handedness)
    */
   public Transform3 lookAt ( final Vec3 point, final float step,
      final Handedness handedness ) {

      Vec3.sub(point, this.location, this.forward);
      return this.lookIn(this.forward, step, handedness);
   }

   /**
    * Orients the transform to look toward a direction.<br>
    * <br>
    * The transform eases toward the look at rotation because discontinuities
    * arise with using a look at matrix when a transform's look direction is
    * parallel with the world up axis.
    *
    * @param dir        the direction
    * @param step       the step
    * @param handedness the handedness
    *
    * @return this transform
    *
    * @see Quaternion#fromDir(Vec3, Handedness, Quaternion, Vec3, Vec3, Vec3)
    * @see Quaternion#mix(Quaternion, Quaternion, float, Quaternion)
    * @see Transform3#updateAxes()
    */
   public Transform3 lookIn ( final Vec3 dir, final float step,
      final Handedness handedness ) {

      /* Quaternion#fromDir will normalize direction. */
      this.rotPrev.set(this.rotation);
      Quaternion.fromDir(dir, handedness, this.rotation, this.right,
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
   @Override
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
    * move ( dir ) := location + rotation * direction
    *
    * @param dir the direction
    *
    * @return this transform
    *
    * @see Quaternion#mulVector(Quaternion, Vec3, Vec3)
    * @see Vec3#hadamard(Vec3, Vec3, Vec3)
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   public Transform3 moveByLocal ( final Vec3 dir ) {

      this.locPrev.set(this.location);
      Quaternion.mulVector(this.rotation, dir, this.location);
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
   @Override
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
    *
    * @see Vec3#mix(Vec3, Vec3, float, Vec3)
    */
   @Override
   public Transform3 moveTo ( final Vec3 locNew, final float step ) {

      this.locPrev.set(this.location);
      Vec3.mix(this.locPrev, locNew, step, this.location);

      return this;
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
    * Reverts this transform to its previous state, i.e., swaps current
    * location, rotation and scale with the previous.
    *
    * @return this transform
    */
   public Transform3 revert ( ) {

      final float tlx = this.location.x;
      final float tly = this.location.y;
      final float tlz = this.location.z;
      this.location.set(this.locPrev);
      this.locPrev.set(tlx, tly, tlz);

      final Vec3 i = this.rotation.imag;
      final float trw = this.rotation.real;
      final float tix = i.x;
      final float tiy = i.y;
      final float tiz = i.z;
      this.rotation.set(this.rotPrev);
      this.rotPrev.set(trw, tix, tiy, tiz);

      final float tsx = this.scale.x;
      final float tsy = this.scale.y;
      final float tsz = this.scale.z;
      this.scale.set(this.scalePrev);
      this.scalePrev.set(tsx, tsy, tsz);

      return this;
   }

   /**
    * Rotates the transform by adding a rotation. Updates the transform's
    * axes.
    *
    * @param angle the angle
    * @param axis  the axis
    *
    * @return this transform
    *
    * @see Quaternion#rotate(Quaternion, float, Vec3, Quaternion)
    * @see Transform3#updateAxes()
    */
   @Experimental
   public Transform3 rotateBy ( final float angle, final Vec3 axis ) {

      this.rotPrev.set(this.rotation);
      Quaternion.rotate(this.rotPrev, angle, axis, this.rotation);
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
   @Override
   public Transform3 rotateTo ( final Quaternion rotNew ) {

      if ( Quaternion.any(rotNew) ) {
         this.rotPrev.set(this.rotation);
         this.rotation.set(rotNew);
         this.updateAxes();
      }

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
   @Override
   public Transform3 rotateTo ( final Quaternion rotNew, final float step ) {

      if ( Quaternion.any(rotNew) ) {
         this.rotPrev.set(this.rotation);
         Quaternion.mix(this.rotPrev, rotNew, step, this.rotation);
         this.updateAxes();
      }

      return this;
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

      if ( Quaternion.any(rotNew) ) {
         this.rotPrev.set(this.rotation);
         easingFunc.apply(this.rotPrev, rotNew, step, this.rotation);
         this.updateAxes();
      }

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
   @Override
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
   @Override
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
   @Override
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
   @Override
   public Transform3 scaleBy ( final float scalar ) {

      if ( scalar != 0.0f ) {
         this.scalePrev.set(this.scale);
         Vec3.mul(this.scalePrev, scalar, this.scale);
      }

      return this;
   }

   /**
    * Scales the transform by a non-uniform scalar.
    *
    * @param nonUniformScale the scale
    *
    * @return this transform
    *
    * @see Vec3#all(Vec3)
    * @see Vec3#hadamard(Vec3, Vec3, Vec3)
    */
   @Override
   public Transform3 scaleBy ( final Vec3 nonUniformScale ) {

      if ( Vec3.all(nonUniformScale) ) {
         this.scalePrev.set(this.scale);
         Vec3.hadamard(this.scalePrev, nonUniformScale, this.scale);
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
   @Override
   public Transform3 scaleTo ( final float scalar ) {

      if ( scalar != 0.0f ) {
         this.scalePrev.set(this.scale);
         this.scale.set(scalar, scalar, scalar);
      }

      return this;
   }

   /**
    * Scales the transform to a non-uniform size.
    *
    * @param scaleNew the new scale
    *
    * @return this transform
    *
    * @see Vec3#all(Vec3)
    */
   @Override
   public Transform3 scaleTo ( final Vec3 scaleNew ) {

      if ( Vec3.all(scaleNew) ) {
         this.scalePrev.set(this.scale);
         this.scale.set(scaleNew);
      }

      return this;
   }

   /**
    * Eases the transform to a scale by a step. The static easing function is
    * used.
    *
    * @param scaleNew the new scale
    * @param step     the step in [0.0, 1.0]
    *
    * @return this transform
    *
    * @see Vec3#all(Vec3)
    */
   @Override
   public Transform3 scaleTo ( final Vec3 scaleNew, final float step ) {

      if ( Vec3.all(scaleNew) ) {
         this.scalePrev.set(this.scale);
         Vec3.mix(this.scalePrev, scaleNew, step, this.scale);
      }

      return this;
   }

   /**
    * Eases the transform to a scale by a step. The kind of easing is
    * specified by a Vec3 easing function.
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
    * @see Transform3#updateAxes()
    */
   public Transform3 set ( final Transform2 source ) {

      this.location.set(source.location, 0.0f);
      final double halfRadians = Utils.modRadians(source.rotation) * 0.5d;
      this.rotation.set(( float ) Math.cos(halfRadians), 0.0f, 0.0f,
         ( float ) Math.sin(halfRadians));
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
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

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
      this.location.toString(sb, places);
      sb.append(", rotation: ");
      this.rotation.toString(sb, places);
      sb.append(", scale: ");
      this.scale.toString(sb, places);
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
    *
    * @see Vec3#wrap(Vec3, Vec3, Vec3, Vec3)
    */
   public Transform3 wrap ( final Vec3 lb, final Vec3 ub ) {

      this.locPrev.set(this.location);
      Vec3.wrap(this.locPrev, lb, ub, this.location);

      return this;
   }

   /**
    * An internal helper function to format a vector as a Python tuple, then
    * append it to a {@link StringBuilder}. Used for testing purposes to
    * compare results with Blender 2.9x.
    *
    * @param pyCd the string builder
    *
    * @return the string builder
    */
   @Experimental
   StringBuilder toBlenderCode ( final StringBuilder pyCd ) {

      final String rotationMode = "\"QUATERNION\"";
      pyCd.append("{\"location\": ");
      this.location.toBlenderCode(pyCd);
      pyCd.append(", \"rotation_mode\": ");
      pyCd.append(rotationMode);
      pyCd.append(", \"rotation_quaternion\": ");
      this.rotation.toBlenderCode(pyCd);
      pyCd.append(", \"scale\": ");
      this.scale.toBlenderCode(pyCd);
      pyCd.append('}');
      return pyCd;
   }

   /**
    * Tests the equivalence between this and another transform.
    *
    * @param t the transform
    *
    * @return the evaluation
    */
   protected boolean equals ( final Transform3 t ) {

      return this.location.equals(t.location) && this.rotation.equals(
         t.rotation) && this.scale.equals(t.scale);
   }

   /**
    * Updates the local axes of the transform based on its rotation.
    *
    * @see Quaternion#toAxes(Quaternion, Vec3, Vec3, Vec3)
    */
   protected void updateAxes ( ) {

      Quaternion.toAxes(this.rotation, this.right, this.forward, this.up);
   }

   /**
    * Default random location lower bound for creating a random transform.
    */
   public static final float DEFAULT_RND_LOC_LB = -1.0f;

   /**
    * Default random location upper bound for creating a random transform.
    */
   public static final float DEFAULT_RND_LOC_UB = 1.0f;

   /**
    * Default random scale lower bound for creating a random transform.
    */
   public static final float DEFAULT_RND_SCL_LB = IUtils.ONE_SQRT_3;

   /**
    * Default random scale upper bound for creating a random transform.
    */
   public static final float DEFAULT_RND_SCL_UB = IUtils.SQRT_3;

   /**
    * Finds the Euclidean distance between the locations of two transforms.
    *
    * @param a the first transform
    * @param b the second transform
    *
    * @return the euclidean distance
    *
    * @see Vec3#distEuclidean(Vec3, Vec3)
    */
   public static float dist ( final Transform3 a, final Transform3 b ) {

      return Vec3.distEuclidean(a.location, b.location);
   }

   /**
    * Finds the Euclidean distance a transform and a vector.
    *
    * @param a the transform
    * @param b the vector
    *
    * @return the euclidean distance
    *
    * @see Vec3#distEuclidean(Vec3, Vec3)
    */
   public static float dist ( final Transform3 a, final Vec3 b ) {

      return Vec3.distEuclidean(a.location, b);
   }

   /**
    * Finds the Euclidean distance a transform and a vector.
    *
    * @param b the transform
    * @param a the vector
    *
    * @return the euclidean distance
    *
    * @see Vec3#distEuclidean(Vec3, Vec3)
    */
   public static float dist ( final Vec3 a, final Transform3 b ) {

      return Vec3.distEuclidean(a, b.location);
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
    * @see Vec3#zero(Vec3)
    */
   public static Transform3 fromAxes ( final float xRight, final float yForward,
      final float zUp, final float zForward, final float yUp, final float xUp,
      final float zRight, final float yRight, final float xForward,
      final Transform3 target ) {

      target.locPrev.set(target.location);
      target.rotPrev.set(target.rotation);
      target.scalePrev.set(target.scale);

      Quaternion.fromAxes(xRight, yForward, zUp, zForward, yUp, xUp, zRight,
         yRight, xForward, target.rotation);

      /* Update needed because the loose floats may not be normalized. */
      target.updateAxes();

      Vec3.zero(target.location);
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

      target.scalePrev.set(target.scale);
      Vec3.one(target.scale);

      target.rotPrev.set(target.rotation);
      Quaternion.fromDir(dir, handedness, target.rotation, target.right,
         target.forward, target.up);

      target.locPrev.set(target.location);
      Vec3.zero(target.location);

      return target;
   }

   /**
    * Creates a transform from spherical coordinates. The transform's right
    * axis corresponds to the point on the sphere. The transform's scale is
    * set to the radius. A radius of zero will return the identity transform
    * instead.
    *
    * @param azimuth     the angle theta in radians
    * @param inclination the angle phi in radians
    * @param radius      rho, the magnitude
    * @param target      the output transform
    *
    * @return the transform
    */
   public static Transform3 fromSpherical ( final float azimuth,
      final float inclination, final float radius, final Transform3 target ) {

      if ( radius == 0.0f ) { return Transform3.identity(target); }
      target.scalePrev.set(target.scale);
      target.scale.set(radius, radius, radius);

      target.rotPrev.set(target.rotation);
      Quaternion.fromSpherical(azimuth, inclination, target.rotation);
      target.updateAxes();

      target.locPrev.set(target.location);
      Vec3.zero(target.location);

      return target;
   }

   /**
    * Creates a transform from spherical coordinates. The transform's right
    * axis corresponds to the point on the sphere. The transform's scale is
    * set to the radius. A radius of zero will return the identity transform
    * instead.
    *
    * @param azimuth     the angle theta in radians
    * @param inclination the angle phi in radians
    * @param radius      rho, the magnitude
    * @param origin      the sphere origin
    * @param target      the output transform
    *
    * @return the transform
    */
   public static Transform3 fromSpherical ( final float azimuth,
      final float inclination, final float radius, final Vec3 origin,
      final Transform3 target ) {

      if ( radius == 0.0f ) { return Transform3.identity(target); }
      target.scalePrev.set(target.scale);
      target.scale.set(radius, radius, radius);

      target.rotPrev.set(target.rotation);
      Quaternion.fromSpherical(azimuth, inclination, target.rotation);
      target.updateAxes();

      target.moveTo(origin);

      return target;
   }

   /**
    * Creates a transform from spherical coordinates. The transform's right
    * axis corresponds to the point on the sphere. The transform's scale is
    * set to 1.0 and its location to the origin.
    *
    * @param azimuth     the angle theta in radians
    * @param inclination the angle phi in radians
    * @param target      the output transform
    *
    * @return the transform
    */
   public static Transform3 fromSpherical ( final float azimuth,
      final float inclination, final Transform3 target ) {

      target.rotPrev.set(target.rotation);
      Quaternion.fromSpherical(azimuth, inclination, target.rotation);
      target.updateAxes();

      target.scalePrev.set(target.scale);
      Vec3.one(target.scale);

      target.locPrev.set(target.location);
      Vec3.zero(target.location);

      return target;
   }

   /**
    * Sets a transform to the identity.
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

      target.locPrev.set(target.location);
      target.rotPrev.set(target.rotation);
      target.scalePrev.set(target.scale);

      Vec3.zero(target.location);
      Quaternion.identity(target.rotation);
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

      return Quaternion.invMulVector(t.rotation, source, target);
   }

   /**
    * Multiplies a normal by a transform's inverse. This rotates the normal by
    * the inverse quaternion, then multiplies the normal by the scale.
    *
    * @param t      the transform
    * @param source the input normal
    * @param target the output normal
    *
    * @return the normal
    *
    * @see Quaternion#invMulVector(Quaternion, Vec3, Vec3)
    * @see Vec3#hadamard(Vec3, Vec3, Vec3)
    */
   @Experimental
   public static Vec3 invMulNormal ( final Transform3 t, final Vec3 source,
      final Vec3 target ) {

      Quaternion.invMulVector(t.rotation, source, target);
      Vec3.hadamard(target, t.scale, target);
      Vec3.normalize(target, target);
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
    * @see Utils#abs(float)
    * @see Utils#max(float, float, float)
    */
   public static float maxDimension ( final Transform3 t ) {

      return Utils.max(Utils.abs(t.scale.x), Utils.abs(t.scale.y), Utils.abs(
         t.scale.z));
   }

   /**
    * Returns the minimum dimension occupied by a transform.
    *
    * @param t the transform
    *
    * @return the minimum dimension
    *
    * @see Utils#abs(float)
    * @see Utils#min(float, float, float)
    */
   public static float minDimension ( final Transform3 t ) {

      return Utils.min(Utils.abs(t.scale.x), Utils.abs(t.scale.y), Utils.abs(
         t.scale.z));
   }

   /**
    * Multiplies a curve segment by a transform. A convenience for drawing
    * curves in a renderer. The segment is represented by the first control
    * point, second control point and destination anchor point.
    *
    * @param t     the transform
    * @param fhSrc the source fore handle
    * @param rhSrc the source rear handle
    * @param coSrc the source coordinate
    * @param fhTrg the target fore handle
    * @param rhTrg the target rear handle
    * @param coTrg the target coordinate
    *
    * @return the transformed coordinate
    */
   public static Vec3 mulCurveSeg ( final Transform3 t, final Vec3 fhSrc,
      final Vec3 rhSrc, final Vec3 coSrc, final Vec3 fhTrg, final Vec3 rhTrg,
      final Vec3 coTrg ) {

      final float xtr = t.location.x;
      final float ytr = t.location.y;
      final float ztr = t.location.z;

      final float qw = t.rotation.real;
      final Vec3 imag = t.rotation.imag;
      final float qx = imag.x;
      final float qy = imag.y;
      final float qz = imag.z;

      final float w = t.scale.x;
      final float d = t.scale.y;
      final float h = t.scale.z;

      final float fhiw = -qx * fhSrc.x - qy * fhSrc.y - qz * fhSrc.z;
      final float fhix = qw * fhSrc.x + qy * fhSrc.z - qz * fhSrc.y;
      final float fhiy = qw * fhSrc.y + qz * fhSrc.x - qx * fhSrc.z;
      final float fhiz = qw * fhSrc.z + qx * fhSrc.y - qy * fhSrc.x;

      fhTrg.x = ( fhix * qw + fhiz * qy - fhiw * qx - fhiy * qz ) * w + xtr;
      fhTrg.y = ( fhiy * qw + fhix * qz - fhiw * qy - fhiz * qx ) * d + ytr;
      fhTrg.z = ( fhiz * qw + fhiy * qx - fhiw * qz - fhix * qy ) * h + ztr;

      final float rhiw = -qx * rhSrc.x - qy * rhSrc.y - qz * rhSrc.z;
      final float rhix = qw * rhSrc.x + qy * rhSrc.z - qz * rhSrc.y;
      final float rhiy = qw * rhSrc.y + qz * rhSrc.x - qx * rhSrc.z;
      final float rhiz = qw * rhSrc.z + qx * rhSrc.y - qy * rhSrc.x;

      rhTrg.x = ( rhix * qw + rhiz * qy - rhiw * qx - rhiy * qz ) * w + xtr;
      rhTrg.y = ( rhiy * qw + rhix * qz - rhiw * qy - rhiz * qx ) * d + ytr;
      rhTrg.z = ( rhiz * qw + rhiy * qx - rhiw * qz - rhix * qy ) * h + ztr;

      final float coiw = -qx * coSrc.x - qy * coSrc.y - qz * coSrc.z;
      final float coix = qw * coSrc.x + qy * coSrc.z - qz * coSrc.y;
      final float coiy = qw * coSrc.y + qz * coSrc.x - qx * coSrc.z;
      final float coiz = qw * coSrc.z + qx * coSrc.y - qy * coSrc.x;

      coTrg.x = ( coix * qw + coiz * qy - coiw * qx - coiy * qz ) * w + xtr;
      coTrg.y = ( coiy * qw + coix * qz - coiw * qy - coiz * qx ) * d + ytr;
      coTrg.z = ( coiz * qw + coiy * qx - coiw * qz - coix * qy ) * h + ztr;

      return coTrg;
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

      return Quaternion.mulVector(t.rotation, source, target);
   }

   /**
    * Multiplies a normal by a transform. This divides the normal by the
    * transform's scale and then rotates it by the transform's rotation.
    *
    * @param t      the transform
    * @param source the input normal
    * @param target the output normal
    *
    * @return the normal
    *
    * @see Vec3#div(Vec3, Vec3, Vec3)
    * @see Quaternion#mulVector(Quaternion, Vec3, Vec3)
    */
   @Experimental
   public static Vec3 mulNormal ( final Transform3 t, final Vec3 source,
      final Vec3 target ) {

      Vec3.div(source, t.scale, target);
      Quaternion.mulVector(t.rotation, target, target);
      Vec3.normalize(target, target);
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
    */
   public static Vec3 mulPoint ( final Transform3 t, final Vec3 source,
      final Vec3 target ) {

      /* Inlined for optimization purposes. */
      // Quaternion.mulVector(t.rotation, source, target);
      // Vec3.mul(target, t.scale, target);
      // Vec3.add(target, t.location, target);
      // return target;

      final Vec3 imag = t.rotation.imag;
      final float qw = t.rotation.real;
      final float qx = imag.x;
      final float qy = imag.y;
      final float qz = imag.z;

      final float iw = -qx * source.x - qy * source.y - qz * source.z;
      final float ix = qw * source.x + qy * source.z - qz * source.y;
      final float iy = qw * source.y + qz * source.x - qx * source.z;
      final float iz = qw * source.z + qx * source.y - qy * source.x;

      final Vec3 sc = t.scale;
      final Vec3 tr = t.location;
      target.x = ( ix * qw + iz * qy - iw * qx - iy * qz ) * sc.x + tr.x;
      target.y = ( iy * qw + ix * qz - iw * qy - iz * qx ) * sc.y + tr.y;
      target.z = ( iz * qw + iy * qx - iw * qz - ix * qy ) * sc.z + tr.z;

      return target;
   }

   /**
    * Multiplies a point and normal by a transform. A convenience for drawing
    * mesh data. Returns the transformed point.
    *
    * @param t      the transform
    * @param ptSrc  the input point
    * @param nrmSrc the input normal
    * @param ptTrg  the output point
    * @param nrmTrg the output normal
    *
    * @return the transformed point
    */
   public static Vec3 mulPointAndNormal ( final Transform3 t, final Vec3 ptSrc,
      final Vec3 nrmSrc, final Vec3 ptTrg, final Vec3 nrmTrg ) {

      final Vec3 tr = t.location;
      final Vec3 sc = t.scale;
      final Quaternion q = t.rotation;

      final Vec3 imag = q.imag;
      final float qw = q.real;
      final float qx = imag.x;
      final float qy = imag.y;
      final float qz = imag.z;

      final float ptiw = -qx * ptSrc.x - qy * ptSrc.y - qz * ptSrc.z;
      final float ptix = qw * ptSrc.x + qy * ptSrc.z - qz * ptSrc.y;
      final float ptiy = qw * ptSrc.y + qz * ptSrc.x - qx * ptSrc.z;
      final float ptiz = qw * ptSrc.z + qx * ptSrc.y - qy * ptSrc.x;

      ptTrg.x = ( ptix * qw + ptiz * qy - ptiw * qx - ptiy * qz ) * sc.x + tr.x;
      ptTrg.y = ( ptiy * qw + ptix * qz - ptiw * qy - ptiz * qx ) * sc.y + tr.y;
      ptTrg.z = ( ptiz * qw + ptiy * qx - ptiw * qz - ptix * qy ) * sc.z + tr.z;

      nrmTrg.x = nrmSrc.x / sc.x;
      nrmTrg.y = nrmSrc.y / sc.y;
      nrmTrg.z = nrmSrc.z / sc.z;

      final float nmiw = -qx * nrmTrg.x - qy * nrmTrg.y - qz * nrmTrg.z;
      final float nmix = qw * nrmTrg.x + qy * nrmTrg.z - qz * nrmTrg.y;
      final float nmiy = qw * nrmTrg.y + qz * nrmTrg.x - qx * nrmTrg.z;
      final float nmiz = qw * nrmTrg.z + qx * nrmTrg.y - qy * nrmTrg.x;

      nrmTrg.x = nmix * qw + nmiz * qy - nmiw * qx - nmiy * qz;
      nrmTrg.y = nmiy * qw + nmix * qz - nmiw * qy - nmiz * qx;
      nrmTrg.z = nmiz * qw + nmiy * qx - nmiw * qz - nmix * qy;

      // TODO: Inline.
      Vec3.normalize(nrmTrg, nrmTrg);

      return ptTrg;
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
    * @see Vec3#hadamard(Vec3, Vec3, Vec3)
    */
   public static Vec3 mulVector ( final Transform3 t, final Vec3 source,
      final Vec3 target ) {

      Quaternion.mulVector(t.rotation, source, target);
      Vec3.hadamard(target, t.scale, target);
      return target;
   }

   /**
    * Creates a random transform. Uses
    * {@link Vec3#randomCartesian(java.util.Random, float, float, Vec3)} for
    * location and scale.
    *
    * @param rng    the random number generator
    * @param lbLoc  the location lower bound
    * @param ubLoc  the location upper bound
    * @param lbScl  the scale lower bound
    * @param ubScl  the scale upper bound
    * @param target the output transform
    *
    * @return the random transform
    *
    * @see Vec3#randomCartesian(java.util.Random, Vec3, Vec3, Vec3)
    * @see Quaternion#random(java.util.Random, Quaternion)
    */
   public static Transform3 random ( final java.util.Random rng,
      final float lbLoc, final float ubLoc, final float lbScl,
      final float ubScl, final Transform3 target ) {

      target.locPrev.set(target.location);
      Vec3.randomCartesian(rng, lbLoc, ubLoc, target.location);

      target.rotPrev.set(target.rotation);
      Quaternion.random(rng, target.rotation);

      target.scalePrev.set(target.scale);
      Vec3.randomCartesian(rng, lbScl, ubScl, target.scale);

      return target;
   }

   /**
    * Creates a random transform. The location range is
    * [{@value Transform3#DEFAULT_RND_LOC_LB},
    * {@value Transform3#DEFAULT_RND_LOC_UB}] . The scale range is
    * [{@value Transform3#DEFAULT_RND_SCL_LB},
    * {@value Transform3#DEFAULT_RND_SCL_UB}] .
    *
    * @param rng    the random number generator
    * @param target the output transform
    *
    * @return the random transform
    */
   public static Transform3 random ( final java.util.Random rng,
      final Transform3 target ) {

      return Transform3.random(rng, Transform3.DEFAULT_RND_LOC_LB,
         Transform3.DEFAULT_RND_LOC_UB, Transform3.DEFAULT_RND_SCL_LB,
         Transform3.DEFAULT_RND_SCL_UB, target);
   }

   /**
    * Creates a random transform. Uses
    * {@link Vec3#randomCartesian(java.util.Random, Vec3, Vec3, Vec3)} for
    * location and scale.
    *
    * @param rng    the random number generator
    * @param lbLoc  the location lower bound
    * @param ubLoc  the location upper bound
    * @param lbScl  the scale lower bound
    * @param ubScl  the scale upper bound
    * @param target the output transform
    *
    * @return the random transform
    *
    * @see Vec3#randomCartesian(java.util.Random, Vec3, Vec3, Vec3)
    * @see Quaternion#random(java.util.Random, Quaternion)
    */
   public static Transform3 random ( final java.util.Random rng,
      final Vec3 lbLoc, final Vec3 ubLoc, final Vec3 lbScl, final Vec3 ubScl,
      final Transform3 target ) {

      target.locPrev.set(target.location);
      Vec3.randomCartesian(rng, lbLoc, ubLoc, target.location);

      target.rotPrev.set(target.rotation);
      Quaternion.random(rng, target.rotation);

      target.scalePrev.set(target.scale);
      Vec3.randomCartesian(rng, lbScl, ubScl, target.scale);

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
       * 1.0] .
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
       * Eases between transforms in an array by a step in the range [0.0, 1.0]
       * .
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
       * 1.0] .
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
         this.scale.applyUnclamped(origin.scale, dest.scale, step,
            target.scale);

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
