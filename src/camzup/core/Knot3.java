package camzup.core;

/**
 * Organizes the vectors the shape a Bezier curve into a coordinate (or
 * anchor point), fore handle (the following control point) and rear handle
 * (the preceding control point).
 */
public class Knot3 implements Cloneable, Comparable < Knot3 > {

   /**
    * The spatial coordinate of the knot.
    */
   public final Vec3 coord;

   /**
    * The handle which warps the curve segment heading away from the knot
    * along the direction of the curve.
    */
   public final Vec3 foreHandle;

   /**
    * The handle which warps the curve segment heading towards the knot along
    * the direction of the curve.
    */
   public final Vec3 rearHandle;

   {
      this.coord = new Vec3();
      this.foreHandle = new Vec3();
      this.rearHandle = new Vec3();
   }

   /**
    * The default constructor.
    */
   public Knot3 ( ) {}

   /**
    * Creates a knot from a coordinate.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    * @param zCoord the z coordinate
    */
   public Knot3 ( final float xCoord, final float yCoord, final float zCoord ) {

      this.set(xCoord, yCoord, zCoord);
   }

   /**
    * Creates a knot from real numbers.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    * @param zCoord the z coordinate
    * @param xFore  the fore handle x
    * @param yFore  the fore handle y
    * @param zFore  the fore handle z
    */
   public Knot3 ( final float xCoord, final float yCoord, final float zCoord,
      final float xFore, final float yFore, final float zFore ) {

      this.set(xCoord, yCoord, zCoord, xFore, yFore, zFore);
   }

   /**
    * Creates a knot from real numbers.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    * @param zCoord the z coordinate
    * @param xFore  the fore handle x
    * @param yFore  the fore handle y
    * @param zFore  the fore handle z
    * @param xRear  the rear handle x
    * @param yRear  the rear handle y
    * @param zRear  the rear handle z
    */
   public Knot3 ( final float xCoord, final float yCoord, final float zCoord,
      final float xFore, final float yFore, final float zFore,
      final float xRear, final float yRear, final float zRear ) {

      this.set(xCoord, yCoord, zCoord, xFore, yFore, zFore, xRear, yRear,
         zRear);
   }

   /**
    * Creates a knot from a source knot.
    *
    * @param source the source
    */
   public Knot3 ( final Knot2 source ) { this.set(source); }

   /**
    * Creates a knot from a source knot.
    *
    * @param source the source
    */
   public Knot3 ( final Knot3 source ) { this.set(source); }

   /**
    * Attempts to create a knot from Strings.
    *
    * @param xCoord the x coordinate string
    * @param yCoord the y coordinate string
    * @param zCoord the z coordinate string
    * @param xFore  the x fore handle string
    * @param yFore  the y fore handle string
    * @param zFore  the z fore handle string
    * @param xRear  the x rear handle string
    * @param yRear  the y rear handle string
    * @param zRear  the z rear handle string
    */
   public Knot3 ( final String xCoord, final String yCoord, final String zCoord,
      final String xFore, final String yFore, final String zFore,
      final String xRear, final String yRear, final String zRear ) {

      this.set(xCoord, yCoord, zCoord, xFore, yFore, zFore, xRear, yRear,
         zRear);
   }

   /**
    * Creates a knot from a series of vectors.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    * @param rearHandle the rear handle
    */
   public Knot3 ( final Vec2 coord, final Vec2 foreHandle,
      final Vec2 rearHandle ) {

      this.set(coord, foreHandle, rearHandle);
   }

   /**
    * Creates a knot from a coordinate.
    *
    * @param coord the coordinate
    */
   public Knot3 ( final Vec3 coord ) { this.set(coord); }

   /**
    * Creates a knot from a coordinate and fore-handle. The rear handle is a
    * mirror of the fore.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    */
   public Knot3 ( final Vec3 coord, final Vec3 foreHandle ) {

      this.set(coord, foreHandle);
   }

   /**
    * Creates a knot from a series of vectors.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    * @param rearHandle the rear handle
    */
   public Knot3 ( final Vec3 coord, final Vec3 foreHandle,
      final Vec3 rearHandle ) {

      this.set(coord, foreHandle, rearHandle);
   }

   /**
    * Adopts the fore handle of a source knot.
    *
    * @param source the source knot
    *
    * @return this knot
    */
   @Experimental
   public Knot3 adoptForeHandle ( final Knot3 source ) {

      this.foreHandle.set(this.coord.x + source.foreHandle.x - source.coord.x,
         this.coord.y + source.foreHandle.y - source.coord.y, this.coord.z
            + source.foreHandle.z - source.coord.z);

      return this;
   }

   /**
    * Adopts the fore handle and rear handle of a source knot.
    *
    * @param source the source knot
    *
    * @return this knot
    */
   @Experimental
   public Knot3 adoptHandles ( final Knot3 source ) {

      this.adoptForeHandle(source);
      this.adoptRearHandle(source);

      return this;
   }

   /**
    * Adopts the rear handle of a source knot.
    *
    * @param source the source knot
    *
    * @return this knot
    */
   @Experimental
   public Knot3 adoptRearHandle ( final Knot3 source ) {

      this.rearHandle.set(this.coord.x + source.rearHandle.x - source.coord.x,
         this.coord.y + source.rearHandle.y - source.coord.y, this.coord.z
            + source.rearHandle.z - source.coord.z);

      return this;
   }

   /**
    * Aligns this knot's handles in the same direction while preserving their
    * magnitude.
    *
    * @return this knot
    *
    * @see Knot3#alignHandlesForward()
    */

   public Knot3 alignHandles ( ) { return this.alignHandlesForward(); }

   /**
    * Aligns this knot's fore handle to its rear handle while preserving
    * magnitude.
    *
    * @return this knot
    *
    * @see Utils#hypot(float, float, float)
    * @see Utils#invHypot(float, float, float)
    */

   public Knot3 alignHandlesBackward ( ) {

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
    * Aligns this knot's rear handle to its fore handle while preserving
    * magnitude.
    *
    * @return this knot
    *
    * @see Utils#hypot(float, float, float)
    * @see Utils#invHypot(float, float, float)
    */

   public Knot3 alignHandlesForward ( ) {

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
    * Creates a new knot with the coordinate and handles of this knot.
    *
    * @return a new knot
    */
   @Override
   public Knot3 clone ( ) {

      return new Knot3(this.coord, this.foreHandle, this.rearHandle);
   }

   /**
    * Compares this knot to another based on a comparison between coordinates.
    *
    * @param knot the other knot
    *
    * @return the evaluation
    */
   @Override
   public int compareTo ( final Knot3 knot ) {

      return this.coord.compareTo(knot.coord);
   }

   /**
    * Tests to see if this knot equals an object
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
      return this.equals(( Knot3 ) obj);
   }

   /**
    * Returns the knot's hash code based on those of its three constituent
    * vectors.
    *
    * @return the hash code
    */
   @Override
   public int hashCode ( ) {

      /* @formatter:off */
      return ( ( IUtils.MUL_BASE ^
             ( this.coord == null ? 0 : this.coord.hashCode() ) )
               * IUtils.HASH_MUL ^
             ( this.foreHandle == null ? 0 : this.foreHandle.hashCode() ) )
               * IUtils.HASH_MUL ^
             ( this.rearHandle == null ? 0 : this.rearHandle.hashCode() );
      /* @formatter:on */
   }

   /**
    * Mirrors this knot's handles. Defaults to mirroring in the forward
    * direction.
    *
    * @return this knot
    *
    * @see Knot3#mirrorHandlesForward()
    */

   public Knot3 mirrorHandles ( ) { return this.mirrorHandlesForward(); }

   /**
    * Sets the forward-facing handle to mirror the rear-facing handle: the
    * fore will have the same magnitude and negated direction of the rear.
    *
    * @return this knot
    */

   public Knot3 mirrorHandlesBackward ( ) {

      this.foreHandle.set(this.coord.x - ( this.rearHandle.x - this.coord.x ),
         this.coord.y - ( this.rearHandle.y - this.coord.y ), this.coord.z
            - ( this.rearHandle.z - this.coord.z ));

      return this;
   }

   /**
    * Sets the rear-facing handle to mirror the forward-facing handle: the
    * rear will have the same magnitude and negated direction of the fore.
    *
    * @return this knot
    */

   public Knot3 mirrorHandlesForward ( ) {

      this.rearHandle.set(this.coord.x - ( this.foreHandle.x - this.coord.x ),
         this.coord.y - ( this.foreHandle.y - this.coord.y ), this.coord.z
            - ( this.foreHandle.z - this.coord.z ));

      return this;
   }

   /**
    * Reverses the knot's direction by swapping the fore- and rear-handles.
    *
    * @return this knot
    */

   public Knot3 reverse ( ) {

      final float tx = this.foreHandle.x;
      final float ty = this.foreHandle.y;
      final float tz = this.foreHandle.z;
      this.foreHandle.set(this.rearHandle);
      this.rearHandle.set(tx, ty, tz);

      return this;
   }

   /**
    * Rotates this knot around an axis by an angle in radians. The axis is
    * assumed to be of unit length. Accepts pre-calculated sine and cosine of
    * an angle, so that collections of knots can be efficiently rotated
    * without repeatedly calling cos and sin.
    *
    * @param cosa cosine of the angle
    * @param sina sine of the angle
    * @param axis the axis of rotation
    *
    * @return this knot
    *
    * @see Vec3#rotate(Vec3, float, float, Vec3, Vec3)
    */

   public Knot3 rotate ( final float cosa, final float sina, final Vec3 axis ) {

      Vec3.rotate(this.coord, cosa, sina, axis, this.coord);
      Vec3.rotate(this.foreHandle, cosa, sina, axis, this.foreHandle);
      Vec3.rotate(this.rearHandle, cosa, sina, axis, this.rearHandle);

      return this;
   }

   /**
    * Rotates this knot around an arbitrary axis by an angle in radians.
    *
    * @param radians the angle
    * @param axis    the axis of rotation
    *
    * @return this knot
    */

   public Knot3 rotate ( final float radians, final Vec3 axis ) {

      return this.rotate(Utils.cos(radians), Utils.sin(radians), axis);
   }

   /**
    * Rotates this knot by a quaternion.
    *
    * @param q the quaternion
    *
    * @return this knot
    */

   public Knot3 rotate ( final Quaternion q ) {

      Quaternion.mulVector(q, this.coord, this.coord);
      Quaternion.mulVector(q, this.foreHandle, this.foreHandle);
      Quaternion.mulVector(q, this.rearHandle, this.rearHandle);

      return this;
   }

   /**
    * Rotates this knot's fore handle by a quaternion with its coordinate
    * serving as a pivot.
    *
    * @param q the quaternion
    *
    * @return this knot
    */

   public Knot3 rotateForeHandle ( final Quaternion q ) {

      Vec3.sub(this.coord, this.foreHandle, this.foreHandle);
      Quaternion.mulVector(q, this.foreHandle, this.foreHandle);
      Vec3.add(this.coord, this.foreHandle, this.foreHandle);

      return this;
   }

   /**
    * Rotates this knot's handles by a quaternion with its coordinate serving
    * as a pivot.
    *
    * @param q the quaternion
    *
    * @return this knot
    */

   public Knot3 rotateHandles ( final Quaternion q ) {

      this.rotateForeHandle(q);
      this.rotateRearHandle(q);

      return this;
   }

   /**
    * Rotates this knot's rear handle by a quaternion with its coordinate
    * serving as a pivot.
    *
    * @param q the quaternion
    *
    * @return this knot
    */

   public Knot3 rotateRearHandle ( final Quaternion q ) {

      Vec3.sub(this.coord, this.rearHandle, this.rearHandle);
      Quaternion.mulVector(q, this.rearHandle, this.rearHandle);
      Vec3.add(this.coord, this.rearHandle, this.rearHandle);

      return this;
   }

   /**
    * Rotates this knot around the x axis by an angle in radians.
    *
    * @param radians the angle
    *
    * @return this knot
    */

   public Knot3 rotateX ( final float radians ) {

      return this.rotateX(Utils.cos(radians), Utils.sin(radians));
   }

   /**
    * Rotates a knot around the x axis. Accepts calculated sine and cosine of
    * an angle, so that collections of knots can be efficiently rotated
    * without repeatedly calling cos and sin.
    *
    * @param cosa cosine of the angle
    * @param sina sine of the angle
    *
    * @return this knot
    */

   public Knot3 rotateX ( final float cosa, final float sina ) {

      Vec3.rotateX(this.coord, cosa, sina, this.coord);
      Vec3.rotateX(this.foreHandle, cosa, sina, this.foreHandle);
      Vec3.rotateX(this.rearHandle, cosa, sina, this.rearHandle);

      return this;
   }

   /**
    * Rotates this knot around the y axis by an angle in radians.
    *
    * @param radians the angle
    *
    * @return this knot
    */

   public Knot3 rotateY ( final float radians ) {

      return this.rotateY(Utils.cos(radians), Utils.sin(radians));
   }

   /**
    * Rotates a knot around the y axis. Accepts calculated sine and cosine of
    * an angle, so that collections of knots can be efficiently rotated
    * without repeatedly calling cos and sin.
    *
    * @param cosa cosine of the angle
    * @param sina sine of the angle
    *
    * @return this knot
    */

   public Knot3 rotateY ( final float cosa, final float sina ) {

      Vec3.rotateY(this.coord, cosa, sina, this.coord);
      Vec3.rotateY(this.foreHandle, cosa, sina, this.foreHandle);
      Vec3.rotateY(this.rearHandle, cosa, sina, this.rearHandle);

      return this;
   }

   /**
    * Rotates this knot around the z axis by an angle in radians.
    *
    * @param radians the angle
    *
    * @return this knot
    */

   public Knot3 rotateZ ( final float radians ) {

      return this.rotateZ(Utils.cos(radians), Utils.sin(radians));
   }

   /**
    * Rotates a knot around the z axis. Accepts calculated sine and cosine of
    * an angle, so that collections of knots can be efficiently rotated
    * without repeatedly calling cos and sin.
    *
    * @param cosa cosine of the angle
    * @param sina sine of the angle
    *
    * @return this knot
    */

   public Knot3 rotateZ ( final float cosa, final float sina ) {

      Vec3.rotateZ(this.coord, cosa, sina, this.coord);
      Vec3.rotateZ(this.foreHandle, cosa, sina, this.foreHandle);
      Vec3.rotateZ(this.rearHandle, cosa, sina, this.rearHandle);

      return this;
   }

   /**
    * Scales this knot by a factor.
    *
    * @param scale the factor
    *
    * @return this knot
    */

   public Knot3 scale ( final float scale ) {

      Vec3.mul(this.coord, scale, this.coord);
      Vec3.mul(this.foreHandle, scale, this.foreHandle);
      Vec3.mul(this.rearHandle, scale, this.rearHandle);

      return this;
   }

   /**
    * Scales this knot by a non-uniform scalar.
    *
    * @param scale the non-uniform scalar
    *
    * @return this knot
    */

   public Knot3 scale ( final Vec3 scale ) {

      Vec3.mul(this.coord, scale, this.coord);
      Vec3.mul(this.foreHandle, scale, this.foreHandle);
      Vec3.mul(this.rearHandle, scale, this.rearHandle);

      return this;
   }

   /**
    * Scales the fore handle by a factor.
    *
    * @param scalar the scalar
    *
    * @return this knot
    */

   public Knot3 scaleForeHandleBy ( final float scalar ) {

      /* forehandle = coordinate + scalar * (forehandle - coordinate) */
      this.foreHandle.x = this.coord.x + scalar * ( this.foreHandle.x
         - this.coord.x );
      this.foreHandle.y = this.coord.y + scalar * ( this.foreHandle.y
         - this.coord.y );
      this.foreHandle.z = this.coord.z + scalar * ( this.foreHandle.z
         - this.coord.z );

      return this;
   }

   /**
    * Scales the fore handle to a magnitude
    *
    * @param magnitude the magnitude
    *
    * @return this knot
    */

   public Knot3 scaleForeHandleTo ( final float magnitude ) {

      Vec3.subNorm(this.foreHandle, this.coord, this.foreHandle);
      Vec3.mul(this.foreHandle, magnitude, this.foreHandle);
      Vec3.add(this.foreHandle, this.coord, this.foreHandle);

      return this;
   }

   /**
    * Scales both the fore and rear handle by a factor.
    *
    * @param scalar the scalar
    *
    * @return this knot
    */

   public Knot3 scaleHandlesBy ( final float scalar ) {

      this.scaleForeHandleBy(scalar);
      this.scaleRearHandleBy(scalar);

      return this;
   }

   /**
    * Scales both the fore and rear handle to a magnitude.
    *
    * @param magnitude the magnitude
    *
    * @return this knot
    *
    * @see Knot3#scaleForeHandleTo(float)
    * @see Knot3#scaleRearHandleTo(float)
    */

   public Knot3 scaleHandlesTo ( final float magnitude ) {

      this.scaleForeHandleTo(magnitude);
      this.scaleRearHandleTo(magnitude);

      return this;
   }

   /**
    * Scales the rear handle by a factor.
    *
    * @param scalar the scalar
    *
    * @return this knot
    */

   public Knot3 scaleRearHandleBy ( final float scalar ) {

      this.rearHandle.x = this.coord.x + scalar * ( this.rearHandle.x
         - this.coord.x );
      this.rearHandle.y = this.coord.y + scalar * ( this.rearHandle.y
         - this.coord.y );
      this.rearHandle.z = this.coord.z + scalar * ( this.rearHandle.z
         - this.coord.z );

      return this;
   }

   /**
    * Scales the rear handle to a magnitude
    *
    * @param magnitude the magnitude
    *
    * @return this knot
    *
    * @see Vec3#subNorm(Vec3, Vec3, Vec3)
    * @see Vec3#mul(Vec3, float, Vec3)
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */

   public Knot3 scaleRearHandleTo ( final float magnitude ) {

      Vec3.subNorm(this.rearHandle, this.coord, this.rearHandle);
      Vec3.mul(this.rearHandle, magnitude, this.rearHandle);
      Vec3.add(this.rearHandle, this.coord, this.rearHandle);

      return this;
   }

   /**
    * Sets the coordinate, fore- and rear-handles to the input coordinate.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    * @param zCoord the z coordinate
    *
    * @return this knot
    *
    * @see Math#copySign(float, float)
    */

   public Knot3 set ( final float xCoord, final float yCoord,
      final float zCoord ) {

      final float xOff = Utils.copySign(IUtils.DEFAULT_EPSILON, xCoord);
      final float yOff = Utils.copySign(IUtils.DEFAULT_EPSILON, yCoord);
      final float zOff = Utils.copySign(IUtils.DEFAULT_EPSILON, zCoord);

      return this.set(xCoord, yCoord, zCoord, xCoord + xOff, yCoord + yOff,
         zCoord + zOff, xCoord - xOff, yCoord - yOff, zCoord - zOff);
   }

   /**
    * Sets the knot's coordinates and fore handle. The rear handle is a mirror
    * of the fore handle.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    * @param zCoord the z coordinate
    * @param xFore  the fore handle x
    * @param yFore  the fore handle y
    * @param zFore  the fore handle z
    *
    * @return this knot
    */

   public Knot3 set ( final float xCoord, final float yCoord,
      final float zCoord, final float xFore, final float yFore,
      final float zFore ) {

      this.coord.set(xCoord, yCoord, zCoord);
      this.foreHandle.set(xFore, yFore, zFore);
      this.rearHandle.set(xCoord - ( xFore - xCoord ), yCoord - ( yFore
         - yCoord ), zCoord - ( zFore - zCoord ));

      return this;
   }

   /**
    * Sets the knot's coordinate, fore handle and rear handle by component.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    * @param zCoord the z coordinate
    * @param xFore  the fore handle x
    * @param yFore  the fore handle y
    * @param zFore  the fore handle z
    * @param xRear  the rear handle x
    * @param yRear  the rear handle y
    * @param zRear  the rear handle z
    *
    * @return this knot
    */
   public Knot3 set ( final float xCoord, final float yCoord,
      final float zCoord, final float xFore, final float yFore,
      final float zFore, final float xRear, final float yRear,
      final float zRear ) {

      this.coord.set(xCoord, yCoord, zCoord);
      this.foreHandle.set(xFore, yFore, zFore);
      this.rearHandle.set(xRear, yRear, zRear);

      return this;
   }

   /**
    * Sets this knot from a source knot.
    *
    * @param source the source
    *
    * @return this knot
    */
   public Knot3 set ( final Knot2 source ) {

      return this.set(source.coord, source.foreHandle, source.rearHandle);
   }

   /**
    * Sets this knot from a source knot.
    *
    * @param source the source
    *
    * @return this knot
    */
   public Knot3 set ( final Knot3 source ) {

      return this.set(source.coord, source.foreHandle, source.rearHandle);
   }

   /**
    * Attempts to set the components of this knot from Strings using
    * {@link Float#parseFloat(String)} . If a NumberFormatException is thrown,
    * the component is set to zero.
    *
    * @param xCoord the x coordinate string
    * @param yCoord the y coordinate string
    * @param zCoord the z coordinate string
    * @param xFore  the x fore handle string
    * @param yFore  the y fore handle string
    * @param zFore  the z fore handle string
    * @param xRear  the x rear handle string
    * @param yRear  the y rear handle string
    * @param zRear  the z rear handle string
    *
    * @return this knot
    */
   public Knot3 set ( final String xCoord, final String yCoord,
      final String zCoord, final String xFore, final String yFore,
      final String zFore, final String xRear, final String yRear,
      final String zRear ) {

      this.coord.set(xCoord, yCoord, zCoord);
      this.foreHandle.set(xFore, yFore, zFore);
      this.rearHandle.set(xRear, yRear, zRear);

      return this;
   }

   /**
    * Sets this knot from a series of vectors.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    * @param rearHandle the rear handle
    *
    * @return this knot
    */
   public Knot3 set ( final Vec2 coord, final Vec2 foreHandle,
      final Vec2 rearHandle ) {

      this.coord.set(coord);
      this.foreHandle.set(foreHandle);
      this.rearHandle.set(rearHandle);

      return this;
   }

   /**
    * Sets the coordinate, fore- and rear-handles to the input coordinate.
    *
    * @param coord the coordinate
    *
    * @return this knot
    */
   public Knot3 set ( final Vec3 coord ) {

      return this.set(coord.x, coord.y, coord.z);
   }

   /**
    * Sets the knot's coordinates and fore handle. The rear handle is a mirror
    * of the forehandle.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    *
    * @return this knot
    */
   public Knot3 set ( final Vec3 coord, final Vec3 foreHandle ) {

      return this.set(coord.x, coord.y, coord.z, foreHandle.x, foreHandle.y,
         foreHandle.z);
   }

   /**
    * Sets this knot from a series of vectors.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    * @param rearHandle the rear handle
    *
    * @return this knot
    */
   public Knot3 set ( final Vec3 coord, final Vec3 foreHandle,
      final Vec3 rearHandle ) {

      this.coord.set(coord);
      this.foreHandle.set(foreHandle);
      this.rearHandle.set(rearHandle);
      return this;
   }

   /**
    * Returns a 2D array representation of this knot. The coordinate is the
    * first element; fore handle, the second; rear handle, the third.
    *
    * @return the array
    *
    * @see Vec3#toArray()
    */
   public float[][] toArray ( ) {

      return new float[][] { this.coord.toArray(), this.foreHandle.toArray(),
         this.rearHandle.toArray() };
   }

   /**
    * Returns a string representation of this knot.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of this knot.
    *
    * @param places the number of places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(384);
      sb.append("{ coord: ");
      sb.append(this.coord.toString(places));
      sb.append(", foreHandle: ");
      sb.append(this.foreHandle.toString(places));
      sb.append(", rearHandle: ");
      sb.append(this.rearHandle.toString(places));
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Multiplies this knot by a matrix.
    *
    * @param m the matrix
    *
    * @return this knot
    *
    * @see Mat4#mulPoint(Mat4, Vec3, Vec3)
    */

   public Knot3 transform ( final Mat4 m ) {

      Mat4.mulPoint(m, this.coord, this.coord);
      Mat4.mulPoint(m, this.foreHandle, this.foreHandle);
      Mat4.mulPoint(m, this.rearHandle, this.rearHandle);

      return this;
   }

   /**
    * Multiplies this knot by a transform.
    *
    * @param tr the transform
    *
    * @return this knot
    *
    * @see Transform3#mulPoint(Transform3, Vec3, Vec3)
    */

   public Knot3 transform ( final Transform3 tr ) {

      Transform3.mulPoint(tr, this.coord, this.coord);
      Transform3.mulPoint(tr, this.foreHandle, this.foreHandle);
      Transform3.mulPoint(tr, this.rearHandle, this.rearHandle);

      return this;
   }

   /**
    * Translates this knot by a vector.
    *
    * @param v the vector
    *
    * @return this knot
    *
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */

   public Knot3 translate ( final Vec3 v ) {

      Vec3.add(this.coord, v, this.coord);
      Vec3.add(this.foreHandle, v, this.foreHandle);
      Vec3.add(this.rearHandle, v, this.rearHandle);

      return this;
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes, i.e., to
    * compare how curve geometry looks in Blender (the control) versus in the
    * library (the test).
    *
    * @return the string
    */
   String toBlenderCode ( ) {

      return this.toBlenderCode(1.0f, 1.0f, 0.0f);
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes, i.e., to
    * compare how curve geometry looks in Blender (the control) versus in the
    * library (the test).
    *
    * @param weight soft body weight
    * @param radius bevel radius
    * @param tilt   tilt
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode ( final float weight, final float radius,
      final float tilt ) {

      final StringBuilder pyCd = new StringBuilder(384);
      pyCd.append("{\"co\": ");
      pyCd.append(this.coord.toBlenderCode());
      pyCd.append(", \"handle_right\": ");
      pyCd.append(this.foreHandle.toBlenderCode());
      pyCd.append(", \"handle_left\": ");
      pyCd.append(this.rearHandle.toBlenderCode());
      pyCd.append(", \"weight\": ");
      pyCd.append(Utils.toFixed(weight, 6));
      pyCd.append(", \"radius\": ");
      pyCd.append(Utils.toFixed(radius, 6));
      pyCd.append(", \"tilt\": ");
      pyCd.append(Utils.toFixed(tilt, 6));
      pyCd.append('}');
      return pyCd.toString();
   }

   /**
    * Tests to see if this knot equals another.
    *
    * @param other the other knot
    *
    * @return the evaluation
    */
   protected boolean equals ( final Knot3 other ) {

      if ( this.coord == null ) {
         if ( other.coord != null ) { return false; }
      } else if ( !this.coord.equals(other.coord) ) { return false; }

      if ( this.foreHandle == null ) {
         if ( other.foreHandle != null ) { return false; }
      } else if ( !this.foreHandle.equals(other.foreHandle) ) { return false; }

      if ( this.rearHandle == null ) {
         if ( other.rearHandle != null ) { return false; }
      } else if ( !this.rearHandle.equals(other.rearHandle) ) { return false; }

      return true;
   }

   /**
    * Gets the fore handle of a knot as a direction, rather than as a point.
    *
    * @param knot   the knot
    * @param target the output vector
    *
    * @return the fore handle vector
    */
   public static Vec3 foreDir ( final Knot3 knot, final Vec3 target ) {

      return Vec3.subNorm(knot.foreHandle, knot.coord, target);
   }

   /**
    * Returns the magnitude of the knot's fore handle, i.e., the Euclidean
    * distance between the fore handle and the coordinate.
    *
    * @param knot the knot
    *
    * @return the magnitude
    *
    * @see Vec3#distEuclidean(Vec3, Vec3)
    */
   public static float foreMag ( final Knot3 knot ) {

      return Vec3.distEuclidean(knot.coord, knot.foreHandle);
   }

   /**
    * Gets the fore handle of a knot as a vector, rather than as a point.
    *
    * @param knot   the knot
    * @param target the output vector
    *
    * @return the fore handle vector
    */
   public static Vec3 foreVec ( final Knot3 knot, final Vec3 target ) {

      return Vec3.sub(knot.foreHandle, knot.coord, target);
   }

   /**
    * Sets two knots from a segment of the cubic curve. Assumes that that the
    * previous knot's coordinate is set to the first anchor point. The
    * previous knot's fore handle, the next knot's rear handle and the next
    * knot's coordinate are set by this function.
    *
    * @param xPrevControl the previous control point x
    * @param yPrevControl the previous control point y
    * @param zPrevControl the previous control point z
    * @param xNextControl the next control point x
    * @param yNextControl the next control point y
    * @param zNextControl the next control point z
    * @param xNextAnchor  the next anchor x
    * @param yNextAnchor  the next anchor y
    * @param zNextAnchor  the next anchor z
    * @param prev         the previous knot
    * @param next         the next knot
    *
    * @return next knot
    */
   public static Knot3 fromSegCubic ( final float xPrevControl,
      final float yPrevControl, final float zPrevControl,
      final float xNextControl, final float yNextControl,
      final float zNextControl, final float xNextAnchor,
      final float yNextAnchor, final float zNextAnchor, final Knot3 prev,
      final Knot3 next ) {

      prev.foreHandle.set(xPrevControl, yPrevControl, zPrevControl);
      next.rearHandle.set(xNextControl, yNextControl, zNextControl);
      next.coord.set(xNextAnchor, yNextAnchor, zNextAnchor);

      return next;
   }

   /**
    * Sets two knots from a segment of the cubic curve. Assumes that that the
    * previous knot's coordinate is set to the first anchor point. The
    * previous knot's fore handle, the next knot's rear handle and the next
    * knot's coordinate are set by this function.
    *
    * @param prevControl the previous control point
    * @param nextControl the next control point
    * @param nextAnchor  the next anchor point
    * @param prev        the previous knot
    * @param next        the next knot
    *
    * @return the next knot
    */
   public static Knot3 fromSegCubic ( final Vec3 prevControl,
      final Vec3 nextControl, final Vec3 nextAnchor, final Knot3 prev,
      final Knot3 next ) {

      return Knot3.fromSegCubic(prevControl.x, prevControl.y, prevControl.z,
         nextControl.x, nextControl.y, nextControl.z, nextAnchor.x,
         nextAnchor.y, nextAnchor.z, prev, next);
   }

   /**
    * Sets a knot from line segment. Assumes that that the previous knot's
    * coordinate is set to the first anchor point. The previous knot's fore
    * handle, the next knot's rear handle and the next knot's coordinate are
    * set by this function.
    *
    * @param xNextAnchor the next anchor x
    * @param yNextAnchor the next anchor y
    * @param zNextAnchor the next anchor z
    * @param prev        the previous knot
    * @param next        the next knot
    *
    * @return the next knot
    */
   public static Knot3 fromSegLinear ( final float xNextAnchor,
      final float yNextAnchor, final float zNextAnchor, final Knot3 prev,
      final Knot3 next ) {

      next.coord.set(xNextAnchor, yNextAnchor, zNextAnchor);
      Vec3.mix(prev.coord, next.coord, IUtils.ONE_THIRD, prev.foreHandle);
      Vec3.mix(next.coord, prev.coord, IUtils.ONE_THIRD, next.rearHandle);

      return next;
   }

   /**
    * Sets a knot from line segment. Assumes that that the previous knot's
    * coordinate is set to the first anchor point. The previous knot's fore
    * handle, the next knot's rear handle and the next knot's coordinate are
    * set by this function.
    *
    * @param nextAnchor the next anchor
    * @param prev       the previous knot
    * @param next       the next knot
    *
    * @return the next knot
    */
   public static Knot3 fromSegLinear ( final Vec3 nextAnchor, final Knot3 prev,
      final Knot3 next ) {

      return Knot3.fromSegLinear(nextAnchor.x, nextAnchor.y, nextAnchor.z, prev,
         next);
   }

   /**
    * Sets two knots from a segment of the quadratic curve. Assumes that that
    * the previous knot's coordinate is set to the first anchor point. The
    * previous knot's fore handle, the next knot's rear handle and the next
    * knot's coordinate are set by this function.
    *
    * @param xControl    the control point x
    * @param yControl    the control point y
    * @param zControl    the control point z
    * @param xNextAnchor the next anchor x
    * @param yNextAnchor the next anchor y
    * @param zNextAnchor the next anchor z
    * @param prev        the previous knot
    * @param next        the next knot
    *
    * @return next knot
    */
   public static Knot3 fromSegQuadratic ( final float xControl,
      final float yControl, final float zControl, final float xNextAnchor,
      final float yNextAnchor, final float zNextAnchor, final Knot3 prev,
      final Knot3 next ) {

      final Vec3 prevCo = prev.coord;

      final float midpt23x = xControl * IUtils.TWO_THIRDS;
      final float midpt23y = yControl * IUtils.TWO_THIRDS;
      final float midpt23z = zControl * IUtils.TWO_THIRDS;

      prev.foreHandle.set(midpt23x + IUtils.ONE_THIRD * prevCo.x, midpt23y
         + IUtils.ONE_THIRD * prevCo.y, midpt23z + IUtils.ONE_THIRD * prevCo.z);

      next.rearHandle.set(midpt23x + IUtils.ONE_THIRD * xNextAnchor, midpt23y
         + IUtils.ONE_THIRD * yNextAnchor, midpt23z + IUtils.ONE_THIRD
            * zNextAnchor);

      next.coord.set(xNextAnchor, yNextAnchor, zNextAnchor);

      return next;
   }

   /**
    * Sets two knots from a segment of the quadratic curve. Assumes that that
    * the previous knot's coordinate is set to the first anchor point. The
    * previous knot's fore handle, the next knot's rear handle and the next
    * knot's coordinate are set by this function.
    *
    * @param control    the control point
    * @param nextAnchor the next anchor point
    * @param prev       the previous knot
    * @param next       the next knot
    *
    * @return the next knot
    */
   public static Knot3 fromSegQuadratic ( final Vec3 control,
      final Vec3 nextAnchor, final Knot3 prev, final Knot3 next ) {

      return Knot3.fromSegQuadratic(control.x, control.y, control.z,
         nextAnchor.x, nextAnchor.y, nextAnchor.z, prev, next);
   }

   /**
    * Mixes two knots together by a step in [0.0, 1.0] with the help of an
    * easing function.
    *
    * @param origin     the original knot
    * @param dest       the destination knot
    * @param step       the step
    * @param target     the output knot
    * @param easingFunc the easing function
    *
    * @return the mix
    */
   public static Knot3 mix ( final Knot3 origin, final Knot3 dest,
      final float step, final Knot3 target, final AbstrEasing easingFunc ) {

      return easingFunc.apply(origin, dest, step, target);
   }

   /**
    * Gets the rear handle of a knot as a direction, rather than as a point.
    *
    * @param knot   the knot
    * @param target the output vector
    *
    * @return the rear handle vector
    */
   public static Vec3 rearDir ( final Knot3 knot, final Vec3 target ) {

      return Vec3.subNorm(knot.rearHandle, knot.coord, target);
   }

   /**
    * Returns the magnitude of the knot's rear handle, i.e., the Euclidean
    * distance between the rear handle and the coordinate.
    *
    * @param knot the knot
    *
    * @return the magnitude
    *
    * @see Vec3#distEuclidean(Vec3, Vec3)
    */
   public static float rearMag ( final Knot3 knot ) {

      return Vec3.distEuclidean(knot.coord, knot.rearHandle);
   }

   /**
    * Gets the rear handle of a knot as a vector, rather than as a point.
    *
    * @param knot   the knot
    * @param target the output vector
    *
    * @return the rear handle vector
    */
   public static Vec3 rearVec ( final Knot3 knot, final Vec3 target ) {

      return Vec3.sub(knot.rearHandle, knot.coord, target);
   }

   /**
    * Smoothes the handles of a knot with reference to a previous and next
    * knot. A helper function to {@link Curve3#smoothHandles(Curve3)} .
    *
    * @param prev  the previous knot
    * @param curr  the current knot
    * @param next  the next knot
    * @param carry a temporary vector
    *
    * @return the current knot
    */
   public static Knot3 smoothHandles ( final Knot3 prev, final Knot3 curr,
      final Knot3 next, final Vec3 carry ) {

      final Vec3 coCurr = curr.coord;
      final Vec3 coPrev = prev.coord;
      final Vec3 coNext = next.coord;

      final float backx = coPrev.x - coCurr.x;
      final float backy = coPrev.y - coCurr.y;
      final float backz = coPrev.z - coCurr.z;

      final float forex = coNext.x - coCurr.x;
      final float forey = coNext.y - coCurr.y;
      final float forez = coNext.z - coCurr.z;

      final float bmSq = backx * backx + backy * backy + backz * backz;
      final float bmInv = Utils.invSqrt(bmSq);

      final float fmSq = forex * forex + forey * forey + forez * forez;
      final float fmInv = Utils.invSqrt(fmSq);

      final float dirx = carry.x + backx * bmInv - forex * fmInv;
      final float diry = carry.y + backy * bmInv - forey * fmInv;
      final float dirz = carry.z + backz * bmInv - forez * fmInv;

      final float rescl = IUtils.ONE_THIRD * Utils.invSqrt(dirx * dirx + diry
         * diry + dirz * dirz);
      carry.x = dirx * rescl;
      carry.y = diry * rescl;
      carry.z = dirz * rescl;

      final float bMag = bmSq * bmInv;
      curr.rearHandle.set(coCurr.x + bMag * carry.x, coCurr.y + bMag * carry.y,
         coCurr.z + bMag * carry.z);

      final float fMag = fmSq * fmInv;
      curr.foreHandle.set(coCurr.x - fMag * carry.x, coCurr.y - fMag * carry.y,
         coCurr.z - fMag * carry.z);

      return curr;
   }

   /**
    * Smoothes the fore handle of the first knot in an open curve. A helper
    * function to {@link Curve3#smoothHandles(Curve3)} .
    *
    * @param curr  the current knot
    * @param next  the next knot
    * @param carry a temporary vector
    *
    * @return the current knot
    */
   public static Knot3 smoothHandlesFirst ( final Knot3 curr, final Knot3 next,
      final Vec3 carry ) {

      final Vec3 coCurr = curr.coord;
      final Vec3 coNext = next.coord;

      final float backx = -coCurr.x;
      final float backy = -coCurr.y;
      final float backz = -coCurr.z;

      final float forex = coNext.x - coCurr.x;
      final float forey = coNext.y - coCurr.y;
      final float forez = coNext.z - coCurr.z;

      final float bmSq = backx * backx + backy * backy + backz * backz;
      final float bmInv = Utils.invSqrt(bmSq);

      final float fmSq = forex * forex + forey * forey + forez * forez;
      final float fmInv = Utils.invSqrt(fmSq);

      final float dirx = carry.x + backx * bmInv - forex * fmInv;
      final float diry = carry.y + backy * bmInv - forey * fmInv;
      final float dirz = carry.z + backz * bmInv - forez * fmInv;

      final float rescl = IUtils.ONE_THIRD * Utils.invSqrt(dirx * dirx + diry
         * diry + dirz * dirz);
      carry.x = dirx * rescl;
      carry.y = diry * rescl;
      carry.z = dirz * rescl;

      // final float bMag = bmSq * bmInv;
      // curr.rearHandle.set(
      // coCurr.x + bMag * dir.x,
      // coCurr.y + bMag * dir.y,
      // coCurr.z + bMag * dir.z);

      final float fMag = fmSq * fmInv;
      curr.foreHandle.set(coCurr.x - fMag * carry.x, coCurr.y - fMag * carry.y,
         coCurr.z - fMag * carry.z);

      return curr;
   }

   /**
    * Smoothes the rear handle of the last knot in an open curve. A helper
    * function to {@link Curve3#smoothHandles(Curve3)} .
    *
    * @param prev  the previous knot
    * @param curr  the current knot
    * @param carry a temporary vector
    *
    * @return the current knot
    */
   public static Knot3 smoothHandlesLast ( final Knot3 prev, final Knot3 curr,
      final Vec3 carry ) {

      final Vec3 coCurr = curr.coord;
      final Vec3 coPrev = prev.coord;

      final float backx = coPrev.x - coCurr.x;
      final float backy = coPrev.y - coCurr.y;
      final float backz = coPrev.z - coCurr.z;

      final float forex = -coCurr.x;
      final float forey = -coCurr.y;
      final float forez = -coCurr.z;

      final float bmSq = backx * backx + backy * backy + backz * backz;
      final float bmInv = Utils.invSqrt(bmSq);

      final float fmSq = forex * forex + forey * forey + forez * forez;
      final float fmInv = Utils.invSqrt(fmSq);

      final float dirx = carry.x + backx * bmInv - forex * fmInv;
      final float diry = carry.y + backy * bmInv - forey * fmInv;
      final float dirz = carry.z + backz * bmInv - forez * fmInv;

      final float rescl = IUtils.ONE_THIRD * Utils.invSqrt(dirx * dirx + diry
         * diry + dirz * dirz);
      carry.x = dirx * rescl;
      carry.y = diry * rescl;
      carry.z = dirz * rescl;

      final float bMag = bmSq * bmInv;
      curr.rearHandle.set(coCurr.x + bMag * carry.x, coCurr.y + bMag * carry.y,
         coCurr.z + bMag * carry.z);

      // final float fMag = fmSq * fmInv;
      // curr.foreHandle.set(
      // coCurr.x - fMag * dir.x,
      // coCurr.y - fMag * dir.y,
      // coCurr.z - fMag * dir.z);

      return curr;
   }

   /**
    * Creates a knot from polar coordinates, where the knot's fore handle is
    * tangent to the radius.
    *
    * @param cosa      the cosine of the angle
    * @param sina      the sine of the angle
    * @param radius    the radius
    * @param handleMag the length of the handles
    * @param target    the output knot
    *
    * @return the knot
    */
   static Knot3 fromPolar ( final float cosa, final float sina,
      final float radius, final float handleMag, final float xCenter,
      final float yCenter, final float zCenter, final Knot3 target ) {

      final Vec3 coord = target.coord;
      coord.set(xCenter + radius * cosa, yCenter + radius * sina, zCenter);

      final float hmsina = sina * handleMag;
      final float hmcosa = cosa * handleMag;

      target.foreHandle.set(coord.x - hmsina, coord.y + hmcosa, zCenter);
      target.rearHandle.set(coord.x + hmsina, coord.y - hmcosa, zCenter);

      return target;
   }

   /**
    * An abstract class to facilitate the creation of knot easing functions.
    */
   public static abstract class AbstrEasing implements Utils.EasingFuncObj <
      Knot3 > {

      /**
       * The default constructor.
       */
      public AbstrEasing ( ) { super(); }

      /**
       * A clamped interpolation between the origin and destination. Defers to
       * an unclamped interpolation, which is to be defined by sub-classes of
       * this class.
       *
       * @param origin the origin knot
       * @param dest   the destination knot
       * @param step   a factor in [0.0, 1.0]
       * @param target the output knot
       *
       * @return the eased knot
       */
      @Override
      public Knot3 apply ( final Knot3 origin, final Knot3 dest,
         final Float step, final Knot3 target ) {

         if ( step <= 0.0f ) { return target.set(origin); }
         if ( step >= 1.0f ) { return target.set(dest); }
         return this.applyUnclamped(origin, dest, step, target);
      }

      /**
       * The interpolation to be defined by subclasses.
       *
       * @param origin the origin knot
       * @param dest   the destination knot
       * @param step   a factor in [0.0, 1.0]
       * @param target the output knot
       *
       * @return the eased knot
       */
      public abstract Knot3 applyUnclamped ( final Knot3 origin,
         final Knot3 dest, final float step, final Knot3 target );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * A functional class to ease between two knots with linear interpolation.
    */
   public static class Lerp extends AbstrEasing {

      /**
       * The default constructor.
       */
      public Lerp ( ) { super(); }

      /**
       * Eases between two knots by a step using the formula (1.0 - t) * a + t *
       * b . Promotes the step from a float to a double.
       *
       * @param origin the origin knot
       * @param dest   the destination knot
       * @param step   the step
       * @param target the output knot
       *
       * @return the eased knot
       */
      @Override
      public Knot3 applyUnclamped ( final Knot3 origin, final Knot3 dest,
         final float step, final Knot3 target ) {

         final float u = 1.0f - step;

         final Vec3 orCo = origin.coord;
         final Vec3 orFh = origin.foreHandle;
         final Vec3 orRh = origin.rearHandle;

         final Vec3 deCo = dest.coord;
         final Vec3 deFh = dest.foreHandle;
         final Vec3 deRh = dest.rearHandle;

         target.coord.set(u * orCo.x + step * deCo.x, u * orCo.y + step
            * deCo.y, u * orCo.z + step * deCo.z);

         target.foreHandle.set(u * orFh.x + step * deFh.x, u * orFh.y + step
            * deFh.y, u * orFh.z + step * deFh.z);

         target.rearHandle.set(u * orRh.x + step * deRh.x, u * orRh.y + step
            * deRh.y, u * orRh.z + step * deRh.z);

         return target;
      }

   }

}