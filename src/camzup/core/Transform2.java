package camzup.core;

import camzup.core.Utils.EasingFuncArr;
import camzup.core.Utils.EasingFuncObj;

/**
 * Facilitates 2D affine transformations for entities.
 */
public class Transform2 implements ISpatial2, IOriented2, IVolume2 {

   /**
    * The transform's forward axis.
    */
   protected final Vec2 forward = Vec2.forward(new Vec2());

   /**
    * The transform's location.
    */
   protected final Vec2 location = new Vec2();

   /**
    * The previous location. Subtract from the current location to find the
    * delta, or change, in location.
    */
   protected final Vec2 locPrev = new Vec2();

   /**
    * The transform's right axis.
    */
   protected final Vec2 right = Vec2.right(new Vec2());

   /**
    * The transform's rotation in radians.
    */
   protected float rotation = 0.0f;

   /**
    * The previous rotation. Subtract from the current rotation to find the
    * delta, or change, in rotation.
    */
   protected float rotPrev = 0.0f;

   /**
    * The transform's scale.
    */
   protected final Vec2 scale = Vec2.one(new Vec2());

   /**
    * The previous scale. Subtract from the current scale to find the delta,
    * or change, in scale.
    */
   protected final Vec2 scalePrev = Vec2.one(new Vec2());

   /**
    * The default constructor.
    */
   public Transform2 ( ) {}

   /**
    * Creates a transform from loose real numbers.
    *
    * @param xLoc    the x location
    * @param yLoc    the y location
    * @param radians the rotation
    * @param xScale  the scale x
    * @param yScale  the scale y
    */
   public Transform2 ( final float xLoc, final float yLoc, final float radians,
      final float xScale, final float yScale ) {

      this.set(xLoc, yLoc, radians, xScale, yScale);
   }

   /**
    * Creates a new transform from a source.
    *
    * @param source the source transform
    */
   public Transform2 ( final Transform2 source ) {

      this.set(source);
   }

   /**
    * Creates a transform from a location, rotation and scale.
    *
    * @param location the location
    * @param rotation the rotation
    * @param scale    the scale
    */
   public Transform2 ( final Vec2 location, final float rotation,
      final Vec2 scale ) {

      this.set(location, rotation, scale);
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
      return this.equals(( Transform2 ) obj);
   }

   /**
    * Flips the transform's scale on the horizontal axis, i.e., negates the
    * scale's x component.
    *
    * @return this transform
    */
   public Transform2 flipX ( ) {

      this.scalePrev.set(this.scale);
      this.scale.x = -this.scale.x;
      return this;
   }

   /**
    * Flips the transform's scale on the vertical axis, i.e., negates the
    * scale's y component.
    *
    * @return this transform
    */
   public Transform2 flipY ( ) {

      this.scalePrev.set(this.scale);
      this.scale.y = -this.scale.y;
      return this;
   }

   /**
    * Gets the transform's axes.
    *
    * @param r the right axis
    * @param f the forward axis
    *
    * @return this transform
    */
   public Transform2 getAxes ( final Vec2 r, final Vec2 f ) {

      r.set(this.right);
      f.set(this.forward);
      return this;
   }

   /**
    * Gets the transform's forward axis.
    *
    * @param target the output vector
    *
    * @return the forward axis
    */
   public Vec2 getForward ( final Vec2 target ) {

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
   public Vec2 getLocation ( final Vec2 target ) {

      return target.set(this.location);
   }

   /**
    * Gets the transform's previous location
    *
    * @param target the output vector
    *
    * @return the previous location
    */
   public Vec2 getLocPrev ( final Vec2 target ) {

      return target.set(this.locPrev);
   }

   /**
    * Gets the transform's right axis.
    *
    * @param target the output vector
    *
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
   @Override
   public float getRotation ( ) { return this.rotation; }

   /**
    * Gets the transform's previous rotation.
    *
    * @return the previous rotation
    */
   public float getRotPrev ( ) { return this.rotPrev; }

   /**
    * Gets the transform's scale.
    *
    * @param target the output vector
    *
    * @return the scale
    */
   @Override
   public Vec2 getScale ( final Vec2 target ) {

      return target.set(this.scale);
   }

   /**
    * Gets the transform's previous scale.
    *
    * @param target the output vector
    *
    * @return the previous scale
    */
   public Vec2 getScalePrev ( final Vec2 target ) {

      return target.set(this.scalePrev);
   }

   /**
    * Returns a hash code for this transform based on its location, rotation
    * and scale.
    *
    * @return the hash code
    *
    * @see Float#floatToIntBits(float)
    * @see Vec2#hashCode()
    */
   @Override
   public int hashCode ( ) {

      return ( ( IUtils.MUL_BASE ^ this.location.hashCode() ) * IUtils.HASH_MUL
         ^ Float.floatToIntBits(this.rotation) ) * IUtils.HASH_MUL ^ this.scale
            .hashCode();
   }

   /**
    * Orients the transform to look at a target point. If the distance between
    * the target and the transform's location is zero, resets the transform.
    * Uses unclamped linear interpolation given a step in the range [0.0, 1.0]
    * . The result is then normalized.
    *
    * @param point      the target point
    * @param step       the step
    * @param handedness the handedness
    *
    * @return this transform
    *
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    * @see Vec2#none(Vec2)
    * @see Vec2#forward(Vec2)
    * @see Vec2#right(Vec2)
    * @see Vec2#normalize(Vec2, Vec2)
    * @see Vec2#perpendicularCCW(Vec2, Vec2)
    * @see Vec2#perpendicularCW(Vec2, Vec2)
    * @see Vec2#headingSigned(Vec2)
    */
   public Transform2 lookAt ( final Vec2 point, final float step,
      final Handedness handedness ) {

      // QUERY A 2D version of lookIn?

      /* The right axis is used as a temporary container. */
      Vec2.sub(point, this.location, this.right);

      if ( Vec2.none(this.right) ) {
         Vec2.forward(this.forward);
         Vec2.right(this.right);
         this.rotPrev = this.rotation;
         this.rotation = 0.0f;
         return this;
      }

      /* Normalized lerp. */
      Vec2.normalize(this.right, this.right);
      final float u = 1.0f - step;
      this.forward.set(u * this.forward.x + step * this.right.x, u
         * this.forward.y + step * this.right.y);
      Vec2.normalize(this.forward, this.forward);

      /*
       * The rotation describes the right axis, but forward is treated as the
       * 'look direction' of the transform.
       */
      if ( handedness == Handedness.LEFT ) {
         Vec2.perpendicularCCW(this.forward, this.right);
      } else {
         Vec2.perpendicularCW(this.forward, this.right);
      }

      this.rotPrev = this.rotation;
      this.rotation = Vec2.headingSigned(this.right);

      return this;
   }

   /**
    * Moves the transform by a direction to a new location.
    *
    * @param dir the direction
    *
    * @return this transform
    *
    * @see Transform2#moveByGlobal(Vec2)
    */
   @Override
   public Transform2 moveBy ( final Vec2 dir ) {

      return this.moveByGlobal(dir);
   }

   /**
    * Moves the transform by a direction to a new location.
    *
    * @param dir the direction
    *
    * @return this transform
    *
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   public Transform2 moveByGlobal ( final Vec2 dir ) {

      this.locPrev.set(this.location);
      Vec2.add(this.locPrev, dir, this.location);

      return this;
   }

   /**
    * Moves the transform by a direction rotated according to the transform's
    * rotation. Uses the formula:<br>
    * <br>
    * move ( dir ) := location + rotation * direction
    *
    * @param dir the direction
    *
    * @return this transform
    *
    * @see Vec2#rotateZ(Vec2, float, float, Vec2)
    * @see Vec2#mul(Vec2, Vec2, Vec2)
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   public Transform2 moveByLocal ( final Vec2 dir ) {

      this.locPrev.set(this.location);
      Vec2.rotateZ(dir, this.right.x, this.right.y, this.location);
      Vec2.add(this.locPrev, this.location, this.location);

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
   public Transform2 moveTo ( final Vec2 locNew ) {

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
   @Override
   public Transform2 moveTo ( final Vec2 locNew, final float step ) {

      return this.moveTo(locNew, step, Transform2.EASING.loc);
   }

   /**
    * Eases the transform to a location by a step. The kind of easing is
    * specified by a Vec2 easing function.
    *
    * @param locNew     the new location
    * @param step       the step in [0.0, 1.0]
    * @param easingFunc the easing function
    *
    * @return this transform
    *
    * @see Vec2.AbstrEasing#apply(Vec2, Vec2, Float, Vec2)
    */
   public Transform2 moveTo ( final Vec2 locNew, final float step,
      final Vec2.AbstrEasing easingFunc ) {

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
   public Transform2 revert ( ) {

      final float tlx = this.location.x;
      final float tly = this.location.y;
      this.location.set(this.locPrev);
      this.locPrev.set(tlx, tly);

      final float trx = this.rotation;
      this.rotation = this.rotPrev;
      this.rotPrev = trx;

      final float tsx = this.scale.x;
      final float tsy = this.scale.y;
      this.scale.set(this.scalePrev);
      this.scalePrev.set(tsx, tsy);

      return this;
   }

   /**
    * Rotates the transform to an angle in radians, then updates its axes.
    *
    * @param rotNew the new angle
    *
    * @return this transform
    *
    * @see Transform2#updateAxes()
    */
   @Override
   public Transform2 rotateTo ( final float rotNew ) {

      this.rotPrev = this.rotation;
      this.rotation = rotNew;
      this.updateAxes();

      return this;
   }

   /**
    * Rotates the transform to a new orientation by a step in [0.0, 1.0] .
    *
    * @param radians the angle
    * @param step    the step
    *
    * @return this transform
    */
   @Override
   public Transform2 rotateTo ( final float radians, final float step ) {

      return this.rotateTo(radians, step, Transform2.EASING.rot);
   }

   /**
    * Rotates the transform to a new orientation by a step in [0.0, 1.0] using
    * the specified easing function. Updates the transform's axes.
    *
    * @param radians    the angle
    * @param step       the step
    * @param easingFunc the easing function
    *
    * @return this transform
    *
    * @see Transform2#updateAxes()
    */
   public Transform2 rotateTo ( final float radians, final float step,
      final Utils.PeriodicEasing easingFunc ) {

      this.rotPrev = this.rotation;
      this.rotation = easingFunc.apply(this.rotPrev, radians, step);
      this.updateAxes();

      return this;
   }

   /**
    * Rotates this transform around the z axis by an angle in radians, then
    * updates the transform's axes.
    *
    * @param radians the angle
    *
    * @return this transform
    *
    * @see Transform2#updateAxes()
    */
   @Override
   public Transform2 rotateZ ( final float radians ) {

      this.rotPrev = this.rotation;
      this.rotation += radians;
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
    * @see Vec2#mul(Vec2, float, Vec2)
    */
   @Override
   public Transform2 scaleBy ( final float scalar ) {

      if ( scalar != 0.0f ) {
         this.scalePrev.set(this.scale);
         Vec2.mul(this.scalePrev, scalar, this.scale);
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
    * @see Vec2#all(Vec2)
    * @see Vec2#mul(Vec2, Vec2, Vec2)
    */
   @Override
   public Transform2 scaleBy ( final Vec2 nonUniformScale ) {

      if ( Vec2.all(nonUniformScale) ) {
         this.scalePrev.set(this.scale);
         Vec2.mul(this.scalePrev, nonUniformScale, this.scale);
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
   public Transform2 scaleTo ( final float scalar ) {

      if ( scalar != 0.0f ) {
         this.scalePrev.set(this.scale);
         this.scale.set(scalar, scalar);
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
    * @see Vec2#all(Vec2)
    */
   @Override
   public Transform2 scaleTo ( final Vec2 scaleNew ) {

      if ( Vec2.all(scaleNew) ) {
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
    * @see Vec2#all(Vec2)
    */
   @Override
   public Transform2 scaleTo ( final Vec2 scaleNew, final float step ) {

      if ( Vec2.all(scaleNew) ) {
         return this.scaleTo(scaleNew, step, Transform2.EASING.scale);
      }

      return this;
   }

   /**
    * Eases the transform to a scale by a step. The kind of easing is
    * specified by a Vec2 easing function.
    *
    * @param scaleNew   the new scale
    * @param step       the step in [0.0, 1.0]
    * @param easingFunc the easing function
    *
    * @return this transform
    *
    * @see Vec2#all(Vec2)
    * @see Vec2.AbstrEasing#apply(Vec2, Vec2, Float, Vec2)
    */
   public Transform2 scaleTo ( final Vec2 scaleNew, final float step,
      final Vec2.AbstrEasing easingFunc ) {

      if ( Vec2.all(scaleNew) ) {
         this.scalePrev.set(this.scale);
         easingFunc.apply(this.scalePrev, scaleNew, step, this.scale);
      }

      return this;
   }

   /**
    * Sets this transform from loose real numbers.
    *
    * @param xLoc    the x location
    * @param yLoc    the y location
    * @param radians the angle in radians
    * @param xScale  the scale x
    * @param yScale  the scale y
    *
    * @return this transform
    *
    * @see Transform2#rotateTo(float)
    */
   public Transform2 set ( final float xLoc, final float yLoc,
      final float radians, final float xScale, final float yScale ) {

      this.locPrev.set(this.location);
      this.location.set(xLoc, yLoc);

      this.rotateTo(radians);

      this.scalePrev.set(this.scale);
      if ( xScale != 0.0f ) { this.scale.x = xScale; }
      if ( yScale != 0.0f ) { this.scale.y = yScale; }

      return this;
   }

   /**
    * Sets this transform to the components of another.
    *
    * @param source the source transform
    *
    * @return this transform
    */
   public Transform2 set ( final Transform2 source ) {

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
   public Transform2 set ( final Vec2 locNew, final float rotNew,
      final Vec2 scaleNew ) {

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
    * @param yRight   m10 : right y
    * @param xForward m01 : forward x
    *
    * @return the transform
    *
    * @see Vec2#normalize(Vec2, Vec2)
    * @see Vec2#headingSigned(Vec2)
    */
   public Transform2 setAxes ( final float xRight, final float yForward,
      final float yRight, final float xForward ) {

      this.right.set(xRight, yRight);
      this.forward.set(xForward, yForward);

      Vec2.normalize(this.right, this.right);
      Vec2.normalize(this.forward, this.forward);

      this.rotPrev = this.rotation;
      this.rotation = Vec2.headingSigned(this.right);

      return this;
   }

   /**
    * Sets a transform's rotation to the given axes. The transform's location
    * and scale remain unchanged.
    *
    * @param right   the right axis
    * @param forward the forward axis
    *
    * @return the transform
    */
   public Transform2 setAxes ( final Vec2 right, final Vec2 forward ) {

      return this.setAxes(right.x, forward.y, right.y, forward.x);
   }

   /**
    * Returns a string representation of this transform according to its
    * string format. For display purposes, the angle is converted from radians
    * to degrees.
    *
    * @return the string
    *
    * @see IUtils#RAD_TO_DEG
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

      final StringBuilder sb = new StringBuilder(160);
      sb.append("{ location: ");
      this.location.toString(sb, places);
      sb.append(", rotation: ");
      Utils.toFixed(sb, this.rotation, places);
      sb.append(", scale: ");
      this.scale.toString(sb, places);
      sb.append(" }");
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
   public Transform2 wrap ( final Vec2 lb, final Vec2 ub ) {

      this.locPrev.set(this.location);
      Vec2.wrap(this.locPrev, lb, ub, this.location);

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
    *
    * @see Utils#cos(float)
    * @see Utils#sin(float)
    */
   @Experimental
   StringBuilder toBlenderCode ( final StringBuilder pyCd ) {

      /*
       * Quaternion from angle: (cos(a * 0.5), 0.0, 0.0, sin(a * 0.5)) . Scale z
       * is an average of scale x and y to keep the mesh proportional in Blender
       * when it is extruded.
       */
      final String rotationMode = "\"QUATERNION\"";
      final float halfRad = this.rotation * 0.5f;
      final float depth = ( this.scale.x + this.scale.y ) * 0.5f;

      pyCd.append("{\"location\": ");
      this.location.toBlenderCode(pyCd, 0.0f);
      pyCd.append(", \"rotation_mode\": ");
      pyCd.append(rotationMode);
      pyCd.append(", \"rotation_quaternion\": (");
      Utils.toFixed(pyCd, Utils.cos(halfRad), 6);
      pyCd.append(", 0.0, 0.0, ");
      Utils.toFixed(pyCd, Utils.sin(halfRad), 6);
      pyCd.append("), \"scale\": ");
      this.scale.toBlenderCode(pyCd, depth);
      pyCd.append("}");
      return pyCd;
   }

   /**
    * Returns a string representation of this transform in SVG syntax.<br>
    * <br>
    * The angle is converted from radians to degrees.
    *
    * @return the SVG string
    */
   String toSvgString ( ) {

      final StringBuilder svgp = new StringBuilder(96);
      svgp.append("transform=\"translate(");
      Utils.toFixed(svgp, this.location.x, ISvgWritable.FIXED_PRINT);
      svgp.append(',');
      svgp.append(' ');
      Utils.toFixed(svgp, this.location.y, ISvgWritable.FIXED_PRINT);
      svgp.append(") rotate(");
      Utils.toFixed(svgp, Utils.modRadians(this.rotation) * IUtils.RAD_TO_DEG,
         0);
      svgp.append(") scale(");
      Utils.toFixed(svgp, this.scale.x, ISvgWritable.FIXED_PRINT);
      svgp.append(',');
      svgp.append(' ');
      Utils.toFixed(svgp, this.scale.y, ISvgWritable.FIXED_PRINT);
      svgp.append(")\"");
      return svgp.toString();
   }

   /**
    * Tests the equivalence between this and another transform.
    *
    * @param t the transform
    *
    * @return the evaluation
    */
   protected boolean equals ( final Transform2 t ) {

      return this.location.equals(t.location) && this.rotation == t.rotation
         && this.scale.equals(t.scale);
   }

   /**
    * Updates the local axes of the transform based on its rotation.
    *
    * @see Vec2#fromPolar(float, Vec2)
    * @see Vec2#perpendicularCCW(Vec2, Vec2)
    */
   protected void updateAxes ( ) {

      Vec2.fromPolar(this.rotation, this.right);
      Vec2.perpendicularCCW(this.right, this.forward);
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
   public static final float DEFAULT_RND_SCL_LB = IUtils.ONE_SQRT_2;

   /**
    * Default random scale upper bound for creating a random transform.
    */
   public static final float DEFAULT_RND_SCL_UB = IUtils.SQRT_2;

   /**
    * The default easing function.
    */
   private static Easing EASING = new Easing();

   /**
    * Finds the Euclidean distance between the locations of two transforms.
    *
    * @param a the first transform
    * @param b the second transform
    *
    * @return the euclidean distance
    *
    * @see Vec2#distEuclidean(Vec2, Vec2)
    */
   public static float dist ( final Transform2 a, final Transform2 b ) {

      return Vec2.distEuclidean(a.location, b.location);
   }

   /**
    * Finds the Euclidean distance a transform and a vector.
    *
    * @param a the transform
    * @param b the vector
    *
    * @return the euclidean distance
    *
    * @see Vec2#distEuclidean(Vec2, Vec2)
    */
   public static float dist ( final Transform2 a, final Vec2 b ) {

      return Vec2.distEuclidean(a.location, b);
   }

   /**
    * Finds the Euclidean distance a transform and a vector.
    *
    * @param b the transform
    * @param a the vector
    *
    * @return the euclidean distance
    *
    * @see Vec2#distEuclidean(Vec2, Vec2)
    */
   public static float dist ( final Vec2 a, final Transform2 b ) {

      return Vec2.distEuclidean(a, b.location);
   }

   /**
    * A helper function to set the transform's from either separate vectors or
    * from the columns of a matrix. The transform's translation is set to
    * zero; its scale, to one.
    *
    * @param xRight   m00 : right x
    * @param yForward m11 : forward y
    * @param yRight   m10 : right y
    * @param xForward m01 : forward x
    * @param target   the output transform
    *
    * @return the transform
    *
    * @see Vec2#normalize(Vec2, Vec2)
    * @see Vec2#headingSigned(Vec2)
    * @see Vec2#one(Vec2)
    */
   public static Transform2 fromAxes ( final float xRight, final float yForward,
      final float yRight, final float xForward, final Transform2 target ) {

      target.locPrev.set(target.location);
      target.rotPrev = target.rotation;
      target.scalePrev.set(target.scale);

      target.right.set(xRight, yRight);
      target.forward.set(xForward, yForward);

      Vec2.normalize(target.right, target.right);
      Vec2.normalize(target.forward, target.forward);

      target.location.reset();
      target.rotation = Vec2.headingSigned(target.right);
      Vec2.one(target.scale);

      return target;
   }

   /**
    * Creates a transform from axes. The transform's translation is set to
    * zero; its scale, to one.
    *
    * @param right   the right axis
    * @param forward the forward axis
    * @param target  the output transform
    *
    * @return the transform
    */
   public static Transform2 fromAxes ( final Vec2 right, final Vec2 forward,
      final Transform2 target ) {

      return Transform2.fromAxes(right.x, forward.y, right.y, forward.x,
         target);
   }

   /**
    * Creates a transform from a ray. The transform's translation is set to
    * the ray's origin; its scale, to one.
    *
    * @param ray        the direction
    * @param handedness the handedness
    * @param target     the output transform
    *
    * @return the transform
    *
    * @see Transform2#fromDir(Vec2, Handedness, Transform2)
    * @see Transform2#moveTo(Vec2)
    */
   public static Transform2 fromDir ( final Ray2 ray,
      final Handedness handedness, final Transform2 target ) {

      Transform2.fromDir(ray.dir, handedness, target);
      target.moveTo(ray.origin);
      return target;
   }

   /**
    * Creates a transform from a direction. The transform's translation is set
    * to zero; its scale, to one.
    *
    * @param dir        the direction
    * @param handedness the handedness
    * @param target     the output transform
    *
    * @return the transform
    *
    * @see Vec2#none(Vec2)
    * @see Vec2#normalize(Vec2, Vec2)
    * @see Vec2#perpendicularCW(Vec2, Vec2)
    * @see Vec2#headingSigned(Vec2)
    * @see Vec2#one(Vec2)
    */
   public static Transform2 fromDir ( final Vec2 dir,
      final Handedness handedness, final Transform2 target ) {

      target.locPrev.set(target.location);
      target.rotPrev = target.rotation;
      target.scalePrev.set(target.scale);

      if ( Vec2.none(dir) ) {
         Vec2.forward(target.forward);
         Vec2.right(target.right);
      } else if ( handedness == Handedness.LEFT ) {
         Vec2.normalize(dir, target.forward);
         Vec2.perpendicularCCW(target.forward, target.right);
      } else {
         Vec2.normalize(dir, target.forward);
         Vec2.perpendicularCW(target.forward, target.right);
      }

      target.rotation = Vec2.headingSigned(target.right);
      target.location.reset();
      Vec2.one(target.scale);

      return target;
   }

   /**
    * Gets the string representation of the default easing function.
    *
    * @return the string
    */
   public static String getEasingString ( ) {

      return Transform2.EASING.toString();
   }

   /**
    * Sets a transform to the identity.
    *
    * @param target the output transform
    *
    * @return the identity
    *
    * @see Vec2#one(Vec2)
    * @see Vec2#right(Vec2)
    * @see Vec2#forward(Vec2)
    */
   public static Transform2 identity ( final Transform2 target ) {

      target.locPrev.set(target.location);
      target.rotPrev = target.rotation;
      target.scalePrev.set(target.scale);

      target.location.reset();
      target.rotation = 0.0f;
      Vec2.one(target.scale);

      Vec2.right(target.right);
      Vec2.forward(target.forward);

      return target;
   }

   /**
    * Multiplies a direction by a transform's inverse. This rotates the
    * direction by the transform's negative angle.
    *
    * @param t      the transform
    * @param source the input direction
    * @param target the output direction
    *
    * @return the direction
    *
    * @see Vec2#rotateZ(Vec2, float, Vec2)
    */
   public static Vec2 invMulDir ( final Transform2 t, final Vec2 source,
      final Vec2 target ) {

      /* cos(-a) = cos(a), sin(-a) = -sin(a) */
      Vec2.rotateZ(source, t.right.x, -t.right.y, target);
      return target;
   }

   /**
    * Multiplies a point by a transform's inverse. This subtracts the
    * translation from the point, divides the point by the scale, then rotates
    * by the negative angle.
    *
    * @param t      the transform
    * @param source the input point
    * @param target the output point
    *
    * @return the point
    *
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    * @see Vec2#div(Vec2, Vec2, Vec2)
    * @see Vec2#rotateZ(Vec2, float, Vec2)
    */
   public static Vec2 invMulPoint ( final Transform2 t, final Vec2 source,
      final Vec2 target ) {

      Vec2.sub(source, t.location, target);
      Vec2.div(target, t.scale, target);
      Vec2.rotateZ(target, t.right.x, -t.right.y, target);
      return target;
   }

   /**
    * Multiplies a vector by a transform's inverse. This divides the vector by
    * the scale, then rotates by the negative angle.
    *
    * @param t      the transform
    * @param source the input point
    * @param target the output point
    *
    * @return the vector
    *
    * @see Vec2#div(Vec2, Vec2, Vec2)
    * @see Vec2#rotateZ(Vec2, float, Vec2)
    */
   public static Vec2 invMulVector ( final Transform2 t, final Vec2 source,
      final Vec2 target ) {

      Vec2.div(source, t.scale, target);
      Vec2.rotateZ(target, t.right.x, -t.right.y, target);
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
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    */
   public static Vec2 locDelta ( final Transform2 t, final Vec2 target ) {

      return Vec2.sub(t.location, t.locPrev, target);
   }

   /**
    * Returns the maximum dimension occupied by a transform.
    *
    * @param t the transform
    *
    * @return the maximum dimension
    *
    * @see Utils#max(float, float)
    */
   public static float maxDimension ( final Transform2 t ) {

      return Utils.max(t.scale.x, t.scale.y);
   }

   /**
    * Returns the minimum dimension occupied by a transform.
    *
    * @param t the transform
    *
    * @return the minimum dimension
    *
    * @see Utils#min(float, float)
    */
   public static float minDimension ( final Transform2 t ) {

      return Utils.min(t.scale.x, t.scale.y);
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
    * @see Transform2#EASING
    */
   public static Transform2 mix ( final Transform2 origin,
      final Transform2 dest, final float step, final Transform2 target ) {

      return Transform2.EASING.apply(origin, dest, step, target);
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
    * @see Transform2#EASING
    */
   public static Transform2 mix ( final Transform2[] frames, final float step,
      final Transform2 target ) {

      return Transform2.EASING.apply(frames, step, target);
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
    * @see Vec2#rotateZ(Vec2, float, Vec2)
    */
   public static Vec2 mulDir ( final Transform2 t, final Vec2 source,
      final Vec2 target ) {

      Vec2.rotateZ(source, t.right.x, t.right.y, target);
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
    * @see Vec2#rotateZ(Vec2, float, Vec2)
    * @see Vec2#mul(Vec2, Vec2, Vec2)
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   public static Vec2 mulPoint ( final Transform2 t, final Vec2 source,
      final Vec2 target ) {

      /* Inlined for optimization purposes. */
      // Vec2.rotateZ(source, t.right.x, t.right.y, target);
      // Vec2.mul(target, t.scale, target);
      // Vec2.add(target, t.location, target);

      final float cosa = t.right.x;
      final float sina = t.right.y;
      final Vec2 sc = t.scale;
      final Vec2 tr = t.location;
      target.x = ( cosa * source.x - sina * source.y ) * sc.x + tr.x;
      target.y = ( cosa * source.y + sina * source.x ) * sc.y + tr.y;

      return target;
   }

   /**
    * Multiplies a texture coordinate (or UV) by a transform.
    *
    * @param t      the transform
    * @param source the input coordinate
    * @param target the output coordinate
    *
    * @return the transformed coordinate
    */
   public static Vec2 mulTexCoord ( final Transform2 t, final Vec2 source,
      final Vec2 target ) {

      target.x = source.x - t.location.x - 0.5f;
      target.y = source.y - t.location.y - 0.5f;
      Vec2.rotateZ(target, t.right.x, -t.right.y, target);
      target.x += 0.5f;
      target.y += 0.5f;
      Vec2.div(target, t.scale, target);
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
    * @see Vec2#rotateZ(Vec2, float, Vec2)
    * @see Vec2#mul(Vec2, Vec2, Vec2)
    */
   public static Vec2 mulVector ( final Transform2 t, final Vec2 source,
      final Vec2 target ) {

      Vec2.rotateZ(source, t.right.x, t.right.y, target);
      Vec2.mul(target, t.scale, target);
      return target;
   }

   /**
    * Creates a random transform. Uses
    * {@link Vec2#randomCartesian(java.util.Random, float, float, Vec2)} for
    * location and scale.
    *
    * @param rng    the random number generator
    * @param lbLoc  the location lower bound
    * @param ubLoc  the location upper bound
    * @param lbRot  the rotation lower bound
    * @param ubRot  the rotation upper bound
    * @param lbScl  the scale lower bound
    * @param ubScl  the scale upper bound
    * @param target the output transform
    *
    * @return the random transform
    */
   public static Transform2 random ( final java.util.Random rng,
      final float lbLoc, final float ubLoc, final float lbRot,
      final float ubRot, final float lbScl, final float ubScl,
      final Transform2 target ) {

      target.locPrev.set(target.location);
      Vec2.randomCartesian(rng, lbLoc, ubLoc, target.location);

      final float t = rng.nextFloat();
      target.rotPrev = target.rotation;
      target.rotation = ( 1.0f - t ) * lbRot + t * ubRot;

      target.scalePrev.set(target.scale);
      Vec2.randomCartesian(rng, lbScl, ubScl, target.scale);

      return target;
   }

   /**
    * Creates a random transform. The location range is
    * [{@value Transform2#DEFAULT_RND_LOC_LB},
    * {@value Transform2#DEFAULT_RND_LOC_UB}] . The scale range is
    * [{@value Transform2#DEFAULT_RND_SCL_LB},
    * {@value Transform2#DEFAULT_RND_SCL_UB}] .
    *
    * @param rng    the random number generator
    * @param target the output transform
    *
    * @return the random transform
    */
   public static Transform2 random ( final java.util.Random rng,
      final Transform2 target ) {

      return Transform2.random(rng, Transform2.DEFAULT_RND_LOC_LB,
         Transform2.DEFAULT_RND_LOC_UB, -IUtils.PI, IUtils.PI,
         Transform2.DEFAULT_RND_SCL_LB, Transform2.DEFAULT_RND_SCL_UB, target);
   }

   /**
    * Creates a random transform. Uses
    * {@link Vec2#randomCartesian(java.util.Random, Vec2, Vec2, Vec2)} for
    * location and scale.
    *
    * @param rng    the random number generator
    * @param lbLoc  the location lower bound
    * @param ubLoc  the location upper bound
    * @param lbRot  the rotation lower bound
    * @param ubRot  the rotation upper bound
    * @param lbScl  the scale lower bound
    * @param ubScl  the scale upper bound
    * @param target the output transform
    *
    * @return the transform
    */
   public static Transform2 random ( final java.util.Random rng,
      final Vec2 lbLoc, final Vec2 ubLoc, final float lbRot, final float ubRot,
      final Vec2 lbScl, final Vec2 ubScl, final Transform2 target ) {

      target.locPrev.set(target.location);
      Vec2.randomCartesian(rng, lbLoc, ubLoc, target.location);

      final float t = rng.nextFloat();
      target.rotPrev = target.rotation;
      target.rotation = ( 1.0f - t ) * lbRot + t * ubRot;

      target.scalePrev.set(target.scale);
      Vec2.randomCartesian(rng, lbScl, ubScl, target.scale);

      return target;
   }

   /**
    * Finds the difference between the current and previous rotation of a
    * transform.
    *
    * @param t the transform
    *
    * @return the rotation delta
    */
   public static float rotDelta ( final Transform2 t ) {

      return t.rotation - t.rotPrev;
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
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    */
   public static Vec2 scaleDelta ( final Transform2 t, final Vec2 target ) {

      return Vec2.sub(t.scale, t.scalePrev, target);
   }

   /**
    * Sets the default easing function used by the transform.
    *
    * @param easing the easing function.
    */
   public static void setEasing ( final Transform2.Easing easing ) {

      if ( easing != null ) { Transform2.EASING = easing; }
   }

   /**
    * An easing function to facilitate animating multiple transforms.
    */
   public static class Easing implements EasingFuncArr < Transform2 >,
      EasingFuncObj < Transform2 > {

      /**
       * The location easing function.
       */
      public final Vec2.AbstrEasing loc;

      /**
       * The rotation easing function.
       */
      public final Utils.PeriodicEasing rot;

      /**
       * The scale easing function.
       */
      public final Vec2.AbstrEasing scale;

      /**
       * The default constructor.
       */
      public Easing ( ) {

         this.loc = new Vec2.Lerp();
         this.rot = new Utils.LerpNear(IUtils.TAU);
         this.scale = new Vec2.SmoothStep();
      }

      /**
       * The easing constructor.
       *
       * @param locEasing   the location easing function
       * @param rotEasing   the rotation easing function
       * @param scaleEasing the scale easing function
       */
      public Easing ( final Vec2.AbstrEasing locEasing,
         final Utils.PeriodicEasing rotEasing,
         final Vec2.AbstrEasing scaleEasing ) {

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
      public Transform2 apply ( final Transform2 origin, final Transform2 dest,
         final Float step, final Transform2 target ) {

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
      public Transform2 apply ( final Transform2[] arr, final Float step,
         final Transform2 target ) {

         final int len = arr.length;
         if ( len == 1 || step <= 0.0f ) { return target.set(arr[0]); }
         if ( step >= 1.0f ) { return target.set(arr[len - 1]); }

         final float scaledStep = step * ( len - 1 );
         final int i = ( int ) scaledStep;
         final float nextStep = scaledStep - i;
         return this.applyUnclamped(arr[i], arr[i + 1], nextStep, target);
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
      public Transform2 applyUnclamped ( final Transform2 origin,
         final Transform2 dest, final float step, final Transform2 target ) {

         target.locPrev.set(target.location);
         this.loc.applyUnclamped(origin.location, dest.location, step,
            target.location);

         target.rotPrev = target.rotation;
         target.rotation = this.rot.apply(origin.rotation, dest.rotation, step);
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
