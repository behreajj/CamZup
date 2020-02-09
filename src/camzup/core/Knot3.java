package camzup.core;

/**
 * Organizes the vectors the shape a Bezier curve into a
 * coordinate (or anchor point), fore handle (the following
 * control point) and rear handle (the preceding control
 * point).
 */
public class Knot3 implements Cloneable, Comparable < Knot3 > {

   /**
    * Creates a knot from polar coordinates, where the knot's
    * fore handle is tangent to the radius.
    *
    * @param cosa
    *           the cosine of the angle
    * @param sina
    *           the sine of the angle
    * @param radius
    *           the radius
    * @param handleMag
    *           the length of the handles
    * @param target
    *           the output knot
    * @return the knot
    */
   public static Knot3 fromPolar (
         final float cosa,
         final float sina,
         final float radius,
         final float handleMag,
         final Knot3 target ) {

      final Vec3 coord = target.coord;
      coord.set(cosa * radius, sina * radius, 0.0f);

      final float hmsina = sina * handleMag;
      final float hmcosa = cosa * handleMag;

      target.foreHandle.set(
            coord.x - hmsina,
            coord.y + hmcosa,
            coord.z);

      target.rearHandle.set(
            coord.x + hmsina,
            coord.y - hmcosa,
            coord.z);

      return target;
   }

   /**
    * Creates a knot from polar coordinates, where the knot's
    * fore handle is tangent to the radius.
    *
    * @param angle
    *           the angle in radians
    * @param radius
    *           the radius
    * @param handleMag
    *           the length of the handles
    * @param target
    *           the output knot
    * @return the knot
    */
   public static Knot3 fromPolar (
         final float angle,
         final float radius,
         final float handleMag,
         final Knot3 target ) {

      return Knot3.fromPolar(
            Utils.cos(angle),
            Utils.sin(angle),
            radius, handleMag, target);
   }

   /**
    * The spatial coordinate of the knot.
    */
   public final Vec3 coord = new Vec3();

   /**
    * The handle which warps the curve segment heading away
    * from the knot along the direction of the curve.
    */
   public final Vec3 foreHandle = new Vec3();

   /**
    * The handle which warps the curve segment heading towards
    * the knot along the direction of the curve.
    */
   public final Vec3 rearHandle = new Vec3();

   /**
    * The default constructor.
    */
   public Knot3 () {

   }

   /**
    * Creates a knot from a coordinate.
    *
    * @param xCoord
    *           the x coordinate
    * @param yCoord
    *           the y coordinate
    * @param zCoord
    *           the z coordinate
    */
   public Knot3 (
         final float xCoord,
         final float yCoord,
         final float zCoord ) {

      this.set(xCoord, yCoord, zCoord);
   }

   /**
    * Creates a knot from real numbers.
    *
    * @param xCoord
    *           the x coordinate
    * @param yCoord
    *           the y coordinate
    * @param zCoord
    *           the z coordinate
    * @param xFore
    *           the fore handle x
    * @param yFore
    *           the fore handle y
    * @param zFore
    *           the fore handle z
    */
   public Knot3 (
         final float xCoord,
         final float yCoord,
         final float zCoord,
         final float xFore,
         final float yFore,
         final float zFore ) {

      this.set(
            xCoord, yCoord, zCoord,
            xFore, yFore, zFore);
   }

   /**
    * Creates a knot from real numbers.
    *
    * @param xCoord
    *           the x coordinate
    * @param yCoord
    *           the y coordinate
    * @param zCoord
    *           the z coordinate
    * @param xFore
    *           the fore handle x
    * @param yFore
    *           the fore handle y
    * @param zFore
    *           the fore handle z
    * @param xRear
    *           the rear handle x
    * @param yRear
    *           the rear handle y
    * @param zRear
    *           the rear handle z
    */
   public Knot3 (
         final float xCoord,
         final float yCoord,
         final float zCoord,
         final float xFore,
         final float yFore,
         final float zFore,
         final float xRear,
         final float yRear,
         final float zRear ) {

      this.set(
            xCoord, yCoord, zCoord,
            xFore, yFore, zFore,
            xRear, yRear, zRear);
   }

   /**
    * Creates a knot from a source knot.
    *
    * @param source
    *           the source
    */
   public Knot3 ( final Knot3 source ) {

      this.set(source);
   }

   /**
    * Attempts to create a knot from Strings.
    *
    * @param xCoord
    *           the x coordinate string
    * @param yCoord
    *           the y coordinate string
    * @param zCoord
    *           the z coordinate string
    * @param xFore
    *           the x fore handle string
    * @param yFore
    *           the y fore handle string
    * @param zFore
    *           the z fore handle string
    * @param xRear
    *           the x rear handle string
    * @param yRear
    *           the y rear handle string
    * @param zRear
    *           the z rear handle string
    */
   public Knot3 (
         final String xCoord,
         final String yCoord,
         final String zCoord,
         final String xFore,
         final String yFore,
         final String zFore,
         final String xRear,
         final String yRear,
         final String zRear ) {

      this.set(
            xCoord, yCoord, zCoord,
            xFore, yFore, zFore,
            xRear, yRear, zRear);
   }

   /**
    * Creates a knot from a coordinate.
    *
    * @param coord
    *           the coordinate
    */
   public Knot3 ( final Vec3 coord ) {

      this.set(coord);
   }

   /**
    * Creates a knot from a coordinate and fore-handle. The
    * rear handle is a mirror of the fore.
    *
    * @param coord
    *           the coordinate
    * @param foreHandle
    *           the fore handle
    */
   public Knot3 (
         final Vec3 coord,
         final Vec3 foreHandle ) {

      this.set(coord, foreHandle);
   }

   /**
    * Creates a knot from a series of vectors.
    *
    * @param coord
    *           the coordinate
    * @param foreHandle
    *           the fore handle
    * @param rearHandle
    *           the rear handle
    */
   public Knot3 (
         final Vec3 coord,
         final Vec3 foreHandle,
         final Vec3 rearHandle ) {

      this.set(coord, foreHandle, rearHandle);
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
   @Experimental
   String toBlenderCode () {

      return new StringBuilder(320)
            .append("{\"co\": ")
            .append(this.coord.toBlenderCode())
            .append(", \"handle_right\": ")
            .append(this.foreHandle.toBlenderCode())
            .append(", \"handle_left\": ")
            .append(this.rearHandle.toBlenderCode())
            .append('}')
            .toString();
   }

   /**
    * Tests to see if this knot equals another.
    *
    * @param other
    *           the other knot
    * @return the evaluation
    */
   protected boolean equals ( final Knot3 other ) {

      if (this.coord == null) {
         if (other.coord != null) {
            return false;
         }
      } else if (!this.coord.equals(other.coord)) {
         return false;
      }

      if (this.foreHandle == null) {
         if (other.foreHandle != null) {
            return false;
         }
      } else if (!this.foreHandle.equals(other.foreHandle)) {
         return false;
      }

      if (this.rearHandle == null) {
         if (other.rearHandle != null) {
            return false;
         }
      } else if (!this.rearHandle.equals(other.rearHandle)) {
         return false;
      }

      return true;
   }

   /**
    * Aligns this knot's handles in the same direction while
    * preserving their magnitude.
    *
    * @return this knot
    * @see Knot3#alignHandlesForward()
    */
   @Chainable
   public Knot3 alignHandles () {

      return this.alignHandlesForward();
   }

   /**
    * Aligns this knot's fore handle to its rear handle while
    * preserving magnitude.
    *
    * @return this knot
    * @see Utils#hypot(float, float, float)
    * @see Utils#invHypot(float, float, float)
    */
   @Chainable
   public Knot3 alignHandlesBackward () {

      final float cox = this.coord.x;
      final float coy = this.coord.y;
      final float coz = this.coord.z;

      final float rearDirx = this.rearHandle.x - cox;
      final float rearDiry = this.rearHandle.y - coy;
      final float rearDirz = this.rearHandle.z - coz;

      final float foreDirx = this.foreHandle.x - cox;
      final float foreDiry = this.foreHandle.y - coy;
      final float foreDirz = this.foreHandle.z - coz;

      final float flipRescale = -Utils.hypot(foreDirx, foreDiry, foreDirz)
            * Utils.invHypot(rearDirx, rearDiry, rearDirz);

      this.foreHandle.x = rearDirx * flipRescale + cox;
      this.foreHandle.y = rearDiry * flipRescale + coy;
      this.foreHandle.z = rearDirz * flipRescale + coz;

      return this;
   }

   /**
    * Aligns this knot's rear handle to its fore handle while
    * preserving magnitude.
    *
    * @return this knot
    * @see Utils#hypot(float, float, float)
    * @see Utils#invHypot(float, float, float)
    */
   @Chainable
   public Knot3 alignHandlesForward () {

      final float cox = this.coord.x;
      final float coy = this.coord.y;
      final float coz = this.coord.z;

      final float rearDirx = this.rearHandle.x - cox;
      final float rearDiry = this.rearHandle.y - coy;
      final float rearDirz = this.rearHandle.z - coz;

      final float foreDirx = this.foreHandle.x - cox;
      final float foreDiry = this.foreHandle.y - coy;
      final float foreDirz = this.foreHandle.z - coz;

      final float flipRescale = -Utils.hypot(rearDirx, rearDiry, rearDirz)
            * Utils.invHypot(foreDirx, foreDiry, foreDirz);

      this.rearHandle.x = foreDirx * flipRescale + cox;
      this.rearHandle.y = foreDiry * flipRescale + coy;
      this.rearHandle.z = foreDirz * flipRescale + coz;

      return this;
   }

   /**
    * Creates a new knot with the coordinate and handles of
    * this knot.
    *
    * @return a new knot
    */
   @Override
   public Knot3 clone () {

      return new Knot3(
            this.coord,
            this.foreHandle,
            this.rearHandle);
   }

   /**
    * Compares this knot to another based on a comparison
    * between coordinates.
    *
    * @param knot
    *           the other knot
    * @return the evaluation
    */
   @Override
   public int compareTo ( final Knot3 knot ) {

      return this.coord.compareTo(knot.coord);
   }

   /**
    * Tests to see if this knot equals an object
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
      return this.equals((Knot3) obj);
   }

   /**
    * Returns the knot's hash code based on those of its three
    * constituent vectors.
    *
    * @return the hash code
    */
   @Override
   public int hashCode () {

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL
            ^ (this.coord == null ? 0 : this.coord.hashCode());
      hash = hash * IUtils.HASH_MUL
            ^ (this.foreHandle == null ? 0 : this.foreHandle.hashCode());
      hash = hash * IUtils.HASH_MUL
            ^ (this.rearHandle == null ? 0 : this.rearHandle.hashCode());
      return hash;
   }

   /**
    * Mirrors this knot's handles. Defaults to mirroring in the
    * forward direction.
    *
    * @return this knot
    * @see Knot3#mirrorHandlesForward()
    */
   @Chainable
   public Knot3 mirrorHandles () {

      return this.mirrorHandlesForward();
   }

   /**
    * Sets the forward-facing handle to mirror the rear-facing
    * handle: the fore will have the same magnitude and negated
    * direction of the rear.
    *
    * @return this knot
    */
   @Chainable
   public Knot3 mirrorHandlesBackward () {

      this.foreHandle.set(
            this.coord.x - (this.rearHandle.x - this.coord.x),
            this.coord.y - (this.rearHandle.y - this.coord.y),
            this.coord.z - (this.rearHandle.z - this.coord.z));

      return this;
   }

   /**
    * Sets the rear-facing handle to mirror the forward-facing
    * handle: the rear will have the same magnitude and negated
    * direction of the fore.
    *
    * @return this knot
    */
   @Chainable
   public Knot3 mirrorHandlesForward () {

      this.rearHandle.set(
            this.coord.x - (this.foreHandle.x - this.coord.x),
            this.coord.y - (this.foreHandle.y - this.coord.y),
            this.coord.z - (this.foreHandle.z - this.coord.z));

      return this;
   }

   /**
    * Reverses the knot's direction by swapping the fore- and
    * rear-handles.
    *
    * @return this knot
    */
   @Chainable
   public Knot3 reverse () {

      final float tx = this.foreHandle.x;
      final float ty = this.foreHandle.y;
      final float tz = this.foreHandle.z;
      this.foreHandle.set(this.rearHandle);
      this.rearHandle.set(tx, ty, tz);

      return this;
   }

   /**
    * Rotates this knot around an axis by an angle in radians.
    * The axis is assumed to be of unit length. Accepts
    * pre-calculated sine and cosine of an angle, so that
    * collections of knots can be efficiently rotated without
    * repeatedly calling cos and sin.
    *
    * @param cosa
    *           cosine of the angle
    * @param sina
    *           sine of the angle
    * @param axis
    *           the axis of rotation
    * @return this knot
    * @see Vec3#rotate(Vec3, float, float, Vec3, Vec3)
    */
   @Chainable
   public Knot3 rotate (
         final float cosa,
         final float sina,
         final Vec3 axis ) {

      Vec3.rotate(this.coord, cosa, sina, axis, this.coord);
      Vec3.rotate(this.foreHandle, cosa, sina, axis, this.foreHandle);
      Vec3.rotate(this.rearHandle, cosa, sina, axis, this.rearHandle);

      return this;
   }

   /**
    * Rotates this knot around an arbitrary axis by an angle in
    * radians.
    *
    * @param radians
    *           the angle
    * @param axis
    *           the axis of rotation
    * @return this knot
    */
   @Chainable
   public Knot3 rotate (
         final float radians,
         final Vec3 axis ) {

      return this.rotate(Utils.cos(radians), Utils.sin(radians), axis);
   }

   /**
    * Rotates this knot by a quaternion.
    *
    * @param q
    *           the quaternion
    * @return this knot
    */
   @Chainable
   public Knot3 rotate ( final Quaternion q ) {

      Quaternion.mulVector(q, this.coord, this.coord);
      Quaternion.mulVector(q, this.foreHandle, this.foreHandle);
      Quaternion.mulVector(q, this.rearHandle, this.rearHandle);

      return this;
   }

   /**
    * Rotates this knot around the x axis by an angle in
    * radians.
    *
    * @param radians
    *           the angle
    * @return this knot
    */
   @Chainable
   public Knot3 rotateX ( final float radians ) {

      return this.rotateX(Utils.cos(radians), Utils.sin(radians));
   }

   /**
    * Rotates a knot around the x axis. Accepts pre-calculated
    * sine and cosine of an angle, so that collections of knots
    * can be efficiently rotated without repeatedly calling cos
    * and sin.
    *
    * @param cosa
    *           cosine of the angle
    * @param sina
    *           sine of the angle
    * @return this knot
    */
   @Chainable
   public Knot3 rotateX (
         final float cosa,
         final float sina ) {

      Vec3.rotateX(this.coord, cosa, sina, this.coord);
      Vec3.rotateX(this.foreHandle, cosa, sina, this.foreHandle);
      Vec3.rotateX(this.rearHandle, cosa, sina, this.rearHandle);

      return this;
   }

   /**
    * Rotates this knot around the y axis by an angle in
    * radians.
    *
    * @param radians
    *           the angle
    * @return this knot
    */
   @Chainable
   public Knot3 rotateY ( final float radians ) {

      return this.rotateY(Utils.cos(radians), Utils.sin(radians));
   }

   /**
    * Rotates a knot around the y axis. Accepts pre-calculated
    * sine and cosine of an angle, so that collections of knots
    * can be efficiently rotated without repeatedly calling cos
    * and sin.
    *
    * @param cosa
    *           cosine of the angle
    * @param sina
    *           sine of the angle
    * @return this knot
    */
   @Chainable
   public Knot3 rotateY (
         final float cosa,
         final float sina ) {

      Vec3.rotateY(this.coord, cosa, sina, this.coord);
      Vec3.rotateY(this.foreHandle, cosa, sina, this.foreHandle);
      Vec3.rotateY(this.rearHandle, cosa, sina, this.rearHandle);

      return this;
   }

   /**
    * Rotates this knot around the z axis by an angle in
    * radians.
    *
    * @param radians
    *           the angle
    * @return this knot
    */
   @Chainable
   public Knot3 rotateZ ( final float radians ) {

      return this.rotateZ(Utils.cos(radians), Utils.sin(radians));
   }

   /**
    * Rotates a knot around the z axis. Accepts pre-calculated
    * sine and cosine of an angle, so that collections of knots
    * can be efficiently rotated without repeatedly calling cos
    * and sin.
    *
    * @param cosa
    *           cosine of the angle
    * @param sina
    *           sine of the angle
    * @return this knot
    */
   @Chainable
   public Knot3 rotateZ (
         final float cosa,
         final float sina ) {

      Vec3.rotateZ(this.coord, cosa, sina, this.coord);
      Vec3.rotateZ(this.foreHandle, cosa, sina, this.foreHandle);
      Vec3.rotateZ(this.rearHandle, cosa, sina, this.rearHandle);

      return this;
   }

   /**
    * Scales this knot by a factor.
    *
    * @param scale
    *           the factor
    * @return this knot
    */
   @Chainable
   public Knot3 scale ( final float scale ) {

      Vec3.mul(this.coord, scale, this.coord);
      Vec3.mul(this.foreHandle, scale, this.foreHandle);
      Vec3.mul(this.rearHandle, scale, this.rearHandle);

      return this;
   }

   /**
    * Scales this knot by a non-uniform scalar.
    *
    * @param scale
    *           the non-uniform scalar
    * @return this knot
    */
   @Chainable
   public Knot3 scale ( final Vec3 scale ) {

      Vec3.mul(this.coord, scale, this.coord);
      Vec3.mul(this.foreHandle, scale, this.foreHandle);
      Vec3.mul(this.rearHandle, scale, this.rearHandle);

      return this;
   }

   /**
    * Scales the fore handle by a factor.
    *
    * @param scalar
    *           the scalar
    * @return this knot
    */
   @Chainable
   public Knot3 scaleForeHandleBy ( final float scalar ) {

      /* fh = co + scalar * (fh - co) */
      this.foreHandle.x = this.coord.x
            + scalar * (this.foreHandle.x - this.coord.x);
      this.foreHandle.y = this.coord.y
            + scalar * (this.foreHandle.y - this.coord.y);
      this.foreHandle.z = this.coord.z
            + scalar * (this.foreHandle.z - this.coord.z);

      return this;
   }

   /**
    * Scales the fore handle to a magnitude
    *
    * @param magnitude
    *           the magnitude
    * @return this knot
    */
   @Chainable
   public Knot3 scaleForeHandleTo ( final float magnitude ) {

      Vec3.subNorm(this.foreHandle, this.coord, this.foreHandle);
      Vec3.mul(this.foreHandle, magnitude, this.foreHandle);
      Vec3.add(this.foreHandle, this.coord, this.foreHandle);

      return this;
   }

   /**
    * Scales both the fore and rear handle by a factor.
    *
    * @param scalar
    *           the scalar
    * @return this knot
    */
   @Chainable
   public Knot3 scaleHandlesBy ( final float scalar ) {

      this.scaleForeHandleBy(scalar);
      this.scaleRearHandleBy(scalar);

      return this;
   }

   /**
    * Scales both the fore and rear handle to a magnitude.
    *
    * @param magnitude
    *           the magnitude
    * @return this knot
    * @see Knot3#scaleForeHandleTo(float)
    * @see Knot3#scaleRearHandleTo(float)
    */
   @Chainable
   public Knot3 scaleHandlesTo ( final float magnitude ) {

      this.scaleForeHandleTo(magnitude);
      this.scaleRearHandleTo(magnitude);

      return this;
   }

   /**
    * Scales the rear handle by a factor.
    *
    * @param scalar
    *           the scalar
    * @return this knot
    */
   @Chainable
   public Knot3 scaleRearHandleBy ( final float scalar ) {

      /* rh = co + scalar * (rh - co) */
      this.rearHandle.x = this.coord.x
            + scalar * (this.rearHandle.x - this.coord.x);
      this.rearHandle.y = this.coord.y
            + scalar * (this.rearHandle.y - this.coord.y);
      this.rearHandle.z = this.coord.z
            + scalar * (this.rearHandle.z - this.coord.z);

      return this;
   }

   /**
    * Scales the rear handle to a magnitude
    *
    * @param magnitude
    *           the magnitude
    * @return this knot
    * @see Vec3#subNorm(Vec3, Vec3, Vec3)
    * @see Vec3#mul(Vec3, float, Vec3)
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   @Chainable
   public Knot3 scaleRearHandleTo ( final float magnitude ) {

      Vec3.subNorm(this.rearHandle, this.coord, this.rearHandle);
      Vec3.mul(this.rearHandle, magnitude, this.rearHandle);
      Vec3.add(this.rearHandle, this.coord, this.rearHandle);

      return this;
   }

   /**
    * Sets the coordinate, fore- and rear-handles to the input
    * coordinate.
    *
    * @param xCoord
    *           the x coordinate
    * @param yCoord
    *           the y coordinate
    * @param zCoord
    *           the z coordinate
    * @return this knot
    * @see Math#copySign(float, float)
    */
   @Chainable
   public Knot3 set (
         final float xCoord,
         final float yCoord,
         final float zCoord ) {

      final float xOff = Math.copySign(Utils.EPSILON, xCoord);
      final float yOff = Math.copySign(Utils.EPSILON, yCoord);
      final float zOff = Math.copySign(Utils.EPSILON, zCoord);

      return this.set(
            xCoord, yCoord, zCoord,
            xCoord + xOff,
            yCoord + yOff,
            zCoord + zOff,
            xCoord - xOff,
            yCoord - yOff,
            zCoord - zOff);
   }

   /**
    * Sets the knot's coordinates and fore handle. The rear
    * handle is a mirror of the forehandle.
    *
    * @param xCoord
    *           the x coordinate
    * @param yCoord
    *           the y coordinate
    * @param zCoord
    *           the z coordinate
    * @param xFore
    *           the forehandle x
    * @param yFore
    *           the forehandle y
    * @param zFore
    *           the forehandle z
    * @return this knot
    */
   @Chainable
   public Knot3 set (
         final float xCoord,
         final float yCoord,
         final float zCoord,
         final float xFore,
         final float yFore,
         final float zFore ) {

      this.coord.set(xCoord, yCoord, zCoord);
      this.foreHandle.set(xFore, yFore, zFore);
      this.rearHandle.set(
            xCoord - (xFore - xCoord),
            yCoord - (yFore - yCoord),
            zCoord - (zFore - zCoord));
      return this;
   }

   /**
    * Sets the knot's coordinate, forehandle and rearhandle by
    * component.
    *
    * @param xCoord
    *           the x coordinate
    * @param yCoord
    *           the y coordinate
    * @param zCoord
    *           the z coordinate
    * @param xFore
    *           the forehandle x
    * @param yFore
    *           the forehandle y
    * @param zFore
    *           the forehandle z
    * @param xRear
    *           the rearhandle x
    * @param yRear
    *           the rearhandle y
    * @param zRear
    *           the rearhandle z
    * @return this knot
    */
   @Chainable
   public Knot3 set (
         final float xCoord,
         final float yCoord,
         final float zCoord,
         final float xFore,
         final float yFore,
         final float zFore,
         final float xRear,
         final float yRear,
         final float zRear ) {

      this.coord.set(xCoord, yCoord, zCoord);
      this.foreHandle.set(xFore, yFore, zFore);
      this.rearHandle.set(xRear, yRear, zRear);

      return this;
   }

   /**
    * Sets this knot from a source knot.
    *
    * @param source
    *           the source
    * @return this knot
    */
   @Chainable
   public Knot3 set ( final Knot3 source ) {

      return this.set(
            source.coord,
            source.foreHandle,
            source.rearHandle);
   }

   /**
    * Attempts to set the components of this knot from Strings
    * using {@link Float#parseFloat(String)} . If a
    * NumberFormatException is thrown, the component is set to
    * zero.
    *
    * @param xCoord
    *           the x coordinate string
    * @param yCoord
    *           the y coordinate string
    * @param zCoord
    *           the z coordinate string
    * @param xFore
    *           the x fore handle string
    * @param yFore
    *           the y fore handle string
    * @param zFore
    *           the z fore handle string
    * @param xRear
    *           the x rear handle string
    * @param yRear
    *           the y rear handle string
    * @param zRear
    *           the z rear handle string
    * @return this knot
    */
   @Chainable
   public Knot3 set (
         final String xCoord,
         final String yCoord,
         final String zCoord,
         final String xFore,
         final String yFore,
         final String zFore,
         final String xRear,
         final String yRear,
         final String zRear ) {

      this.coord.set(xCoord, yCoord, zCoord);
      this.foreHandle.set(xFore, yFore, zFore);
      this.rearHandle.set(xRear, yRear, zRear);

      return this;
   }

   /**
    * Sets the coordinate, fore- and rear-handles to the input
    * coordinate.
    *
    * @param coord
    *           the coordinate
    * @return this knot
    */
   public Knot3 set ( final Vec3 coord ) {

      return this.set(coord.x, coord.y, coord.z);
   }

   /**
    * Sets the knot's coordinates and fore handle. The rear
    * handle is a mirror of the forehandle.
    *
    * @param coord
    *           the coordinate
    * @param foreHandle
    *           the fore handle
    * @return this knot
    */
   @Chainable
   public Knot3 set (
         final Vec3 coord,
         final Vec3 foreHandle ) {

      return this.set(
            coord.x,
            coord.y,
            coord.z,
            foreHandle.x,
            foreHandle.y,
            foreHandle.z);
   }

   /**
    * Sets this knot from a series of vectors.
    *
    * @param coord
    *           the coord
    * @param foreHandle
    *           the fore handle
    * @param rearHandle
    *           the rear handle
    * @return this knot
    */
   @Chainable
   public Knot3 set (
         final Vec3 coord,
         final Vec3 foreHandle,
         final Vec3 rearHandle ) {

      this.coord.set(coord);
      this.foreHandle.set(foreHandle);
      this.rearHandle.set(rearHandle);
      return this;
   }

   /**
    * Returns a 2D array representation of this knot. The
    * coordinate is the first element; fore handle, the second;
    * rear handle, the third.
    *
    * @return the array
    * @see Vec3#toArray()
    */
   public float[][] toArray () {

      return new float[][] {
            this.coord.toArray(),
            this.foreHandle.toArray(),
            this.rearHandle.toArray()
      };
   }

   /**
    * Returns a string representation of this knot.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   /**
    * Returns a string representation of this knot.
    *
    * @param places
    *           the number of places
    * @return the string
    */
   public String toString ( final int places ) {

      return new StringBuilder(320)
            .append("{ coord: ")
            .append(this.coord.toString(places))
            .append(", foreHandle: ")
            .append(this.foreHandle.toString(places))
            .append(", rearHandle: ")
            .append(this.rearHandle.toString(places))
            .append(' ').append('}')
            .toString();
   }

   /**
    * Translates this knot by a vector.
    *
    * @param v
    *           the vector
    * @return this knot
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   @Chainable
   public Knot3 translate ( final Vec3 v ) {

      Vec3.add(this.coord, v, this.coord);
      Vec3.add(this.foreHandle, v, this.foreHandle);
      Vec3.add(this.rearHandle, v, this.rearHandle);

      return this;
   }
}