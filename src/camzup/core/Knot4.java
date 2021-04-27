package camzup.core;

/**
 * Organizes the vectors that shape a Bezier curve into a coordinate (or
 * anchor point), fore handle (the following control point) and rear handle
 * (the preceding control point).
 */
public class Knot4 implements Comparable < Knot4 > {

   /**
    * The spatial coordinate of the knot.
    */
   public final Vec4 coord = new Vec4();

   /**
    * The handle which warps the curve segment heading away from the knot
    * along the direction of the curve.
    */
   public final Vec4 foreHandle = new Vec4();

   /**
    * The handle which warps the curve segment heading towards the knot along
    * the direction of the curve.
    */
   public final Vec4 rearHandle = new Vec4();

   /**
    * The default constructor.
    */
   public Knot4 ( ) {}

   /**
    * Creates a knot from a coordinate.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    * @param zCoord the z coordinate
    * @param wCoord the w coordinate
    */
   public Knot4 ( final float xCoord, final float yCoord, final float zCoord,
      final float wCoord ) {

      this.set(xCoord, yCoord, zCoord, wCoord);
   }

   /**
    * Creates a knot from a coordinate and fore handle. The rear handle is a
    * mirror of the fore handle.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    * @param zCoord the z coordinate
    * @param wCoord the w coordinate
    * @param xFore  the fore handle x
    * @param yFore  the fore handle y
    * @param zFore  the fore handle z
    * @param wFore  the fore handle w
    */
   public Knot4 ( final float xCoord, final float yCoord, final float zCoord,
      final float wCoord, final float xFore, final float yFore,
      final float zFore, final float wFore ) {

      this.set(xCoord, yCoord, zCoord, wCoord, xFore, yFore, zFore, wFore);
   }

   /**
    * Creates a knot from real numbers.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    * @param zCoord the z coordinate
    * @param wCoord the w coordinate
    * @param xFore  the fore handle x
    * @param yFore  the fore handle y
    * @param zFore  the fore handle z
    * @param wFore  the fore handle w
    * @param xRear  the rear handle x
    * @param yRear  the rear handle y
    * @param zRear  the rear handle z
    * @param wRear  the rear handle w
    */
   public Knot4 ( final float xCoord, final float yCoord, final float zCoord,
      final float wCoord, final float xFore, final float yFore,
      final float zFore, final float wFore, final float xRear,
      final float yRear, final float zRear, final float wRear ) {

      this.set(xCoord, yCoord, zCoord, wCoord, xFore, yFore, zFore, wFore,
         xRear, yRear, zRear, wRear);
   }

   /**
    * Creates a knot from a 2D source knot.<br>
    * <br>
    * For the purpose of promoting 2D curves to 4D.
    *
    * @param source the source
    */
   public Knot4 ( final Knot2 source ) { this.set(source); }

   /**
    * Creates a knot from a 3D source knot.<br>
    * <br>
    * For the purpose of promoting 3D curves to 4D.
    *
    * @param source the source
    */
   public Knot4 ( final Knot3 source ) { this.set(source); }

   /**
    * Creates a knot from a source knot.
    *
    * @param source the source
    */
   public Knot4 ( final Knot4 source ) { this.set(source); }

   /**
    * Creates a knot from a series of 2D vectors.<br>
    * <br>
    * For the purpose of promoting 2D curves to 4D.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    * @param rearHandle the rear handle
    */
   public Knot4 ( final Vec2 coord, final Vec2 foreHandle,
      final Vec2 rearHandle ) {

      this.set(coord, foreHandle, rearHandle);
   }

   /**
    * Creates a knot from a series of 3D vectors.<br>
    * <br>
    * For the purpose of promoting 3D curves to 4D.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    * @param rearHandle the rear handle
    */
   public Knot4 ( final Vec3 coord, final Vec3 foreHandle,
      final Vec3 rearHandle ) {

      this.set(coord, foreHandle, rearHandle);
   }

   /**
    * Creates a knot from a coordinate.
    *
    * @param coord the coordinate
    */
   public Knot4 ( final Vec4 coord ) { this.set(coord); }

   /**
    * Creates a knot from a coordinate and fore handle. The rear handle is a
    * mirror of the fore handle.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    */
   public Knot4 ( final Vec4 coord, final Vec4 foreHandle ) {

      this.set(coord, foreHandle);
   }

   /**
    * Creates a knot from a series of vectors.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    * @param rearHandle the rear handle
    */
   public Knot4 ( final Vec4 coord, final Vec4 foreHandle,
      final Vec4 rearHandle ) {

      this.set(coord, foreHandle, rearHandle);
   }

   /**
    * Adopts the fore handle of a source knot.
    *
    * @param source the source knot
    *
    * @return this knot
    */
   public Knot4 adoptForeHandle ( final Knot4 source ) {

      this.foreHandle.set(this.coord.x + source.foreHandle.x - source.coord.x,
         this.coord.y + source.foreHandle.y - source.coord.y, this.coord.z
            + source.foreHandle.z - source.coord.z, this.coord.w
               + source.foreHandle.w - source.coord.w);

      return this;
   }

   /**
    * Adopts the fore handle and rear handle of a source knot.
    *
    * @param source the source knot
    *
    * @return this knot
    */
   public Knot4 adoptHandles ( final Knot4 source ) {

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
   public Knot4 adoptRearHandle ( final Knot4 source ) {

      this.rearHandle.set(this.coord.x + source.rearHandle.x - source.coord.x,
         this.coord.y + source.rearHandle.y - source.coord.y, this.coord.z
            + source.rearHandle.z - source.coord.z, this.coord.w
               + source.rearHandle.w - source.coord.w);

      return this;
   }

   /**
    * Aligns this knot's handles in the same direction while preserving their
    * magnitude.
    *
    * @return this knot
    *
    * @see Knot4#alignHandlesForward()
    */
   public Knot4 alignHandles ( ) { return this.alignHandlesForward(); }

   /**
    * Aligns this knot's fore handle to its rear handle while preserving
    * magnitude.
    *
    * @return this knot
    *
    * @see Utils#sqrt(float)
    * @see Utils#invSqrtUnchecked(float)
    */
   public Knot4 alignHandlesBackward ( ) {

      final float xCoord = this.coord.x;
      final float yCoord = this.coord.y;
      final float zCoord = this.coord.z;
      final float wCoord = this.coord.w;

      final float xRear = this.rearHandle.x - xCoord;
      final float yRear = this.rearHandle.y - yCoord;
      final float zRear = this.rearHandle.z - zCoord;
      final float wRear = this.rearHandle.w - wCoord;

      final float rmsq = xRear * xRear + yRear * yRear + zRear * zRear + wRear
         * wRear;

      if ( rmsq > 0.0f ) {
         final float xFore = this.foreHandle.x - xCoord;
         final float yFore = this.foreHandle.y - yCoord;
         final float zFore = this.foreHandle.z - zCoord;
         final float wFore = this.foreHandle.w - wCoord;

         final float flipRescale = -Utils.sqrt(xFore * xFore + yFore * yFore
            + zFore * zFore + wFore * wFore) * Utils.invSqrtUnchecked(rmsq);

         this.foreHandle.x = xRear * flipRescale + xCoord;
         this.foreHandle.y = yRear * flipRescale + yCoord;
         this.foreHandle.z = zRear * flipRescale + zCoord;
         this.foreHandle.w = wRear * flipRescale + wCoord;
      }

      return this;
   }

   /**
    * Aligns this knot's rear handle to its fore handle while preserving
    * magnitude.
    *
    * @return this knot
    *
    * @see Utils#sqrt(float)
    * @see Utils#invSqrtUnchecked(float)
    */
   public Knot4 alignHandlesForward ( ) {

      final float xCoord = this.coord.x;
      final float yCoord = this.coord.y;
      final float zCoord = this.coord.z;
      final float wCoord = this.coord.w;

      final float xFore = this.foreHandle.x - xCoord;
      final float yFore = this.foreHandle.y - yCoord;
      final float zFore = this.foreHandle.z - zCoord;
      final float wFore = this.foreHandle.w - wCoord;

      final float fmsq = xFore * xFore + yFore * yFore + zFore * zFore + wFore
         * wFore;

      if ( fmsq > 0.0f ) {
         final float xRear = this.rearHandle.x - xCoord;
         final float yRear = this.rearHandle.y - yCoord;
         final float zRear = this.rearHandle.z - zCoord;
         final float wRear = this.rearHandle.w - wCoord;

         final float flipRescale = -Utils.sqrt(xRear * xRear + yRear * yRear
            + zRear * zRear + wRear * wRear) * Utils.invSqrtUnchecked(fmsq);

         this.rearHandle.x = xFore * flipRescale + xCoord;
         this.rearHandle.y = yFore * flipRescale + yCoord;
         this.rearHandle.z = zFore * flipRescale + zCoord;
      }

      return this;
   }

   /**
    * Compares this knot to another based on a comparison between coordinates.
    *
    * @param knot the other knot
    *
    * @return the evaluation
    */
   @Override
   public int compareTo ( final Knot4 knot ) {

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
      return this.equals(( Knot4 ) obj);
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
             this.coord.hashCode() )
               * IUtils.HASH_MUL ^
             this.foreHandle.hashCode() )
               * IUtils.HASH_MUL ^
             this.rearHandle.hashCode();
      /* @formatter:on */
   }

   /**
    * Mirrors this knot's handles. Defaults to mirroring in the forward
    * direction.
    *
    * @return this knot
    *
    * @see Knot4#mirrorHandlesForward()
    */
   public Knot4 mirrorHandles ( ) { return this.mirrorHandlesForward(); }

   /**
    * Sets the forward-facing handle to mirror the rear-facing handle: the
    * fore will have the same magnitude and negated direction of the rear.
    *
    * @return this knot
    */
   public Knot4 mirrorHandlesBackward ( ) {

      this.foreHandle.set(this.coord.x - ( this.rearHandle.x - this.coord.x ),
         this.coord.y - ( this.rearHandle.y - this.coord.y ), this.coord.z
            - ( this.rearHandle.z - this.coord.z ), this.coord.w
               - ( this.rearHandle.w - this.coord.w ));

      return this;
   }

   /**
    * Sets the rear-facing handle to mirror the forward-facing handle: the
    * rear will have the same magnitude and negated direction of the fore.
    *
    * @return this knot
    */
   public Knot4 mirrorHandlesForward ( ) {

      this.rearHandle.set(this.coord.x - ( this.foreHandle.x - this.coord.x ),
         this.coord.y - ( this.foreHandle.y - this.coord.y ), this.coord.z
            - ( this.foreHandle.z - this.coord.z ), this.coord.w
               - ( this.foreHandle.w - this.coord.w ));

      return this;
   }

   /**
    * Reverses the knot's direction by swapping the fore- and rear-handles.
    *
    * @return this knot
    */
   public Knot4 reverse ( ) {

      final float tx = this.foreHandle.x;
      final float ty = this.foreHandle.y;
      final float tz = this.foreHandle.z;
      final float tw = this.foreHandle.w;
      this.foreHandle.set(this.rearHandle);
      this.rearHandle.set(tx, ty, tz, tw);

      return this;
   }

   /**
    * Scales this knot by a factor.
    *
    * @param scale the factor
    *
    * @return this knot
    *
    * @see Vec4#mul(Vec4, float, Vec4)
    */
   public Knot4 scale ( final float scale ) {

      Vec4.mul(this.coord, scale, this.coord);
      Vec4.mul(this.foreHandle, scale, this.foreHandle);
      Vec4.mul(this.rearHandle, scale, this.rearHandle);

      return this;
   }

   /**
    * Scales this knot by a non-uniform scalar.
    *
    * @param scale the non-uniform scalar
    *
    * @return this knot
    *
    * @see Vec4#mul(Vec4, Vec4, Vec4)
    */
   public Knot4 scale ( final Vec4 scale ) {

      Vec4.mul(this.coord, scale, this.coord);
      Vec4.mul(this.foreHandle, scale, this.foreHandle);
      Vec4.mul(this.rearHandle, scale, this.rearHandle);

      return this;
   }

   /**
    * Scales the fore handle by a factor.
    *
    * @param scalar the scalar
    *
    * @return this knot
    */
   public Knot4 scaleForeHandleBy ( final float scalar ) {

      /* forehandle = coordinate + scalar * (forehandle - coordinate) */
      this.foreHandle.x = this.coord.x + scalar * ( this.foreHandle.x
         - this.coord.x );
      this.foreHandle.y = this.coord.y + scalar * ( this.foreHandle.y
         - this.coord.y );
      this.foreHandle.z = this.coord.z + scalar * ( this.foreHandle.z
         - this.coord.z );
      this.foreHandle.w = this.coord.w + scalar * ( this.foreHandle.w
         - this.coord.w );

      return this;
   }

   /**
    * Scales the fore handle to a magnitude
    *
    * @param magnitude the magnitude
    *
    * @return this knot
    *
    * @see Vec4#sub(Vec4, Vec4, Vec4)
    * @see Vec4#rescale(Vec4, float, Vec4)
    * @see Vec4#add(Vec4, Vec4, Vec4)
    */
   public Knot4 scaleForeHandleTo ( final float magnitude ) {

      Vec4.sub(this.foreHandle, this.coord, this.foreHandle);
      Vec4.rescale(this.foreHandle, magnitude, this.foreHandle);
      Vec4.add(this.foreHandle, this.coord, this.foreHandle);

      return this;
   }

   /**
    * Scales both the fore and rear handle by a factor.
    *
    * @param scalar the scalar
    *
    * @return this knot
    *
    * @see Knot4#scaleForeHandleBy(float)
    * @see Knot4#scaleRearHandleBy(float)
    */
   public Knot4 scaleHandlesBy ( final float scalar ) {

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
    * @see Knot4#scaleForeHandleTo(float)
    * @see Knot4#scaleRearHandleTo(float)
    */
   public Knot4 scaleHandlesTo ( final float magnitude ) {

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
   public Knot4 scaleRearHandleBy ( final float scalar ) {

      this.rearHandle.x = this.coord.x + scalar * ( this.rearHandle.x
         - this.coord.x );
      this.rearHandle.y = this.coord.y + scalar * ( this.rearHandle.y
         - this.coord.y );
      this.rearHandle.z = this.coord.z + scalar * ( this.rearHandle.z
         - this.coord.z );
      this.rearHandle.w = this.coord.w + scalar * ( this.rearHandle.w
         - this.coord.w );

      return this;
   }

   /**
    * Scales the rear handle to a magnitude
    *
    * @param magnitude the magnitude
    *
    * @return this knot
    *
    * @see Vec4#sub(Vec4, Vec4, Vec4)
    * @see Vec4#rescale(Vec4, float, Vec4)
    * @see Vec4#add(Vec4, Vec4, Vec4)
    */
   public Knot4 scaleRearHandleTo ( final float magnitude ) {

      Vec4.sub(this.rearHandle, this.coord, this.rearHandle);
      Vec4.rescale(this.rearHandle, magnitude, this.rearHandle);
      Vec4.add(this.rearHandle, this.coord, this.rearHandle);

      return this;
   }

   /**
    * Sets the coordinate, fore- and rear-handles to the input coordinate.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    * @param zCoord the z coordinate
    * @param wCoord the w coordinate
    *
    * @return this knot
    *
    * @see Utils#copySign(float, float)
    */
   public Knot4 set ( final float xCoord, final float yCoord,
      final float zCoord, final float wCoord ) {

      final float xOff = Utils.copySign(IUtils.EPSILON, xCoord);
      final float yOff = Utils.copySign(IUtils.EPSILON, yCoord);
      final float zOff = Utils.copySign(IUtils.EPSILON, zCoord);
      final float wOff = Utils.copySign(IUtils.EPSILON, wCoord);

      return this.set(xCoord, yCoord, zCoord, wCoord, xCoord + xOff, yCoord
         + yOff, zCoord + zOff, wCoord + wOff, xCoord - xOff, yCoord - yOff,
         zCoord - zOff, wCoord - wOff);
   }

   /**
    * Sets the knot's coordinates and fore handle. The rear handle is a mirror
    * of the fore handle.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    * @param zCoord the z coordinate
    * @param wCoord the w coordinate
    * @param xFore  the fore handle x
    * @param yFore  the fore handle y
    * @param zFore  the fore handle z
    * @param wFore  the fore handle w
    *
    * @return this knot
    */
   public Knot4 set ( final float xCoord, final float yCoord,
      final float zCoord, final float wCoord, final float xFore,
      final float yFore, final float zFore, final float wFore ) {

      this.coord.set(xCoord, yCoord, zCoord, wCoord);
      this.foreHandle.set(xFore, yFore, zFore, wFore);
      this.rearHandle.set(xCoord - ( xFore - xCoord ), yCoord - ( yFore
         - yCoord ), zCoord - ( zFore - zCoord ), wCoord - ( wFore - wCoord ));

      return this;
   }

   /**
    * Sets the knot's coordinate, fore handle and rear handle by component.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    * @param zCoord the z coordinate
    * @param wCoord the w coordinate
    * @param xFore  the fore handle x
    * @param yFore  the fore handle y
    * @param zFore  the fore handle z
    * @param wFore  the fore handle w
    * @param xRear  the rear handle x
    * @param yRear  the rear handle y
    * @param zRear  the rear handle z
    * @param wRear  the rear handle w
    *
    * @return this knot
    */
   public Knot4 set ( final float xCoord, final float yCoord,
      final float zCoord, final float wCoord, final float xFore,
      final float yFore, final float zFore, final float wFore,
      final float xRear, final float yRear, final float zRear,
      final float wRear ) {

      this.coord.set(xCoord, yCoord, zCoord, wCoord);
      this.foreHandle.set(xFore, yFore, zFore, wFore);
      this.rearHandle.set(xRear, yRear, zRear, wRear);

      return this;
   }

   /**
    * Sets this knot from a 2D source knot.<br>
    * <br>
    * For the purpose of promoting 2D curves to 4D.
    *
    * @param source the source
    *
    * @return this knot
    */
   public Knot4 set ( final Knot2 source ) {

      return this.set(source.coord, source.foreHandle, source.rearHandle);
   }

   /**
    * Sets this knot from a 3D source knot.<br>
    * <br>
    * For the purpose of promoting 3D curves to 4D.
    *
    * @param source the source
    *
    * @return this knot
    */
   public Knot4 set ( final Knot3 source ) {

      return this.set(source.coord, source.foreHandle, source.rearHandle);
   }

   /**
    * Sets this knot from a source knot.
    *
    * @param source the source
    *
    * @return this knot
    */
   public Knot4 set ( final Knot4 source ) {

      return this.set(source.coord, source.foreHandle, source.rearHandle);
   }

   /**
    * Sets this knot from a series of 2D vectors.<br>
    * <br>
    * For the purpose of promoting 2D curves to 4D.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    * @param rearHandle the rear handle
    *
    * @return this knot
    */
   public Knot4 set ( final Vec2 coord, final Vec2 foreHandle,
      final Vec2 rearHandle ) {

      this.coord.set(coord);
      this.foreHandle.set(foreHandle);
      this.rearHandle.set(rearHandle);

      return this;
   }

   /**
    * Sets this knot from a series of 3D vectors.<br>
    * <br>
    * For the purpose of promoting 3D curves to 4D.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    * @param rearHandle the rear handle
    *
    * @return this knot
    */
   public Knot4 set ( final Vec3 coord, final Vec3 foreHandle,
      final Vec3 rearHandle ) {

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
   public Knot4 set ( final Vec4 coord ) {

      return this.set(coord.x, coord.y, coord.z, coord.w);
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
   public Knot4 set ( final Vec4 coord, final Vec4 foreHandle ) {

      return this.set(coord.x, coord.y, coord.z, coord.w, foreHandle.x,
         foreHandle.y, foreHandle.z, foreHandle.w);
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
   public Knot4 set ( final Vec4 coord, final Vec4 foreHandle,
      final Vec4 rearHandle ) {

      this.coord.set(coord);
      this.foreHandle.set(foreHandle);
      this.rearHandle.set(rearHandle);

      return this;
   }

   /**
    * Returns a string representation of this knot.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

   /**
    * Returns a string representation of this knot.
    *
    * @param places the number of decimal places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      return this.toString(new StringBuilder(512), places).toString();
   }

   /**
    * Translates this knot by a vector.
    *
    * @param v the vector
    *
    * @return this knot
    *
    * @see Vec4#add(Vec4, Vec4, Vec4)
    */
   public Knot4 translate ( final Vec4 v ) {

      Vec4.add(this.coord, v, this.coord);
      Vec4.add(this.foreHandle, v, this.foreHandle);
      Vec4.add(this.rearHandle, v, this.rearHandle);

      return this;
   }

   /**
    * Internal helper function to assist with methods that need to print many
    * knots. Appends to an existing {@link StringBuilder}.
    *
    * @param sb     the string builder
    * @param places the number of places
    *
    * @return the string builder
    */
   StringBuilder toString ( final StringBuilder sb, final int places ) {

      sb.append("{ coord: ");
      this.coord.toString(sb, places);
      sb.append(", foreHandle: ");
      this.foreHandle.toString(sb, places);
      sb.append(", rearHandle: ");
      this.rearHandle.toString(sb, places);
      sb.append(' ');
      sb.append('}');
      return sb;
   }

   /**
    * Tests to see if this knot equals another.
    *
    * @param other the other knot
    *
    * @return the evaluation
    */
   protected boolean equals ( final Knot4 other ) {

      return this.coord.equals(other.coord) && this.foreHandle.equals(
         other.foreHandle) && this.rearHandle.equals(other.rearHandle);
   }

   /**
    * Returns a coordinate given two knots and a step.
    *
    * @param a      the origin knot
    * @param b      the destination knot
    * @param step   the step
    * @param target the output coordinate
    *
    * @return the coordinate
    *
    * @see Vec4#bezierPoint(Vec4, Vec4, Vec4, Vec4, float, Vec4)
    */
   public static Vec4 bezierPoint ( final Knot4 a, final Knot4 b,
      final float step, final Vec4 target ) {

      return Vec4.bezierPoint(a.coord, a.foreHandle, b.rearHandle, b.coord,
         step, target);
   }

   /**
    * Returns a tangent given two knots and a step.
    *
    * @param a      the origin knot
    * @param b      the destination knot
    * @param step   the step
    * @param target the output coordinate
    *
    * @return the coordinate
    *
    * @see Vec4#bezierTangent(Vec4, Vec4, Vec4, Vec4, float, Vec4)
    */
   public static Vec4 bezierTangent ( final Knot4 a, final Knot4 b,
      final float step, final Vec4 target ) {

      return Vec4.bezierTangent(a.coord, a.foreHandle, b.rearHandle, b.coord,
         step, target);
   }

   /**
    * Returns a normalized tangent given two knots and a step.
    *
    * @param a      the origin knot
    * @param b      the destination knot
    * @param step   the step
    * @param target the output coordinate
    *
    * @return the coordinate
    *
    * @see Vec4#bezierTanUnit(Vec4, Vec4, Vec4, Vec4, float, Vec4)
    */
   public static Vec4 bezierTanUnit ( final Knot4 a, final Knot4 b,
      final float step, final Vec4 target ) {

      return Vec4.bezierTanUnit(a.coord, a.foreHandle, b.rearHandle, b.coord,
         step, target);
   }

   /**
    * Gets the fore handle of a knot as a direction, rather than as a point.
    *
    * @param knot   the knot
    * @param target the output vector
    *
    * @return the fore handle vector
    *
    * @see Vec4#sub(Vec4, Vec4, Vec4)
    * @see Vec4#normalize(Vec4, Vec4)
    */
   public static Vec4 foreDir ( final Knot4 knot, final Vec4 target ) {

      Vec4.sub(knot.foreHandle, knot.coord, target);
      return Vec4.normalize(target, target);
   }

   /**
    * Returns the magnitude of the knot's fore handle, i.e., the Euclidean
    * distance between the fore handle and the coordinate.
    *
    * @param knot the knot
    *
    * @return the magnitude
    *
    * @see Vec4#distEuclidean(Vec4, Vec4)
    */
   public static float foreMag ( final Knot4 knot ) {

      return Vec4.distEuclidean(knot.foreHandle, knot.coord);
   }

   /**
    * Gets the fore handle of a knot as a vector, rather than as a point.
    *
    * @param knot   the knot
    * @param target the output vector
    *
    * @return the fore handle vector
    *
    * @see Vec4#sub(Vec4, Vec4, Vec4)
    */
   public static Vec4 foreVec ( final Knot4 knot, final Vec4 target ) {

      return Vec4.sub(knot.foreHandle, knot.coord, target);
   }

   /**
    * Sets two knots from a segment of a Catmull-Rom curve. The default curve
    * tightness is 0.0.<br>
    * <br>
    * Assumes that the previous knot's coordinate is set to a prior anchor
    * point. The previous knot's fore handle, the next knot's rear handle and
    * the next knot's coordinate are set by this function.
    *
    * @param xPrevAnchor the previous anchor x
    * @param yPrevAnchor the previous anchor y
    * @param zPrevAnchor the previous anchor z
    * @param wPrevAnchor the previous anchor w
    * @param xCurrAnchor the current anchor x
    * @param yCurrAnchor the current anchor y
    * @param zCurrAnchor the current anchor z
    * @param wCurrAnchor the current anchor w
    * @param xNextAnchor the next anchor x
    * @param yNextAnchor the next anchor y
    * @param zNextAnchor the next anchor z
    * @param wNextAnchor the next anchor w
    * @param xAdvAnchor  the advance anchor x
    * @param yAdvAnchor  the advance anchor y
    * @param zAdvAnchor  the advance anchor z
    * @param wAdvAnchor  the advance anchor w
    * @param tightness   the curve tightness
    * @param prev        the previous knot
    * @param next        the next knot
    *
    * @return the next knot
    */
   public static Knot4 fromSegCatmull ( final float xPrevAnchor,
      final float yPrevAnchor, final float zPrevAnchor, final float wPrevAnchor,
      final float xCurrAnchor, final float yCurrAnchor, final float zCurrAnchor,
      final float wCurrAnchor, final float xNextAnchor, final float yNextAnchor,
      final float zNextAnchor, final float wNextAnchor, final float xAdvAnchor,
      final float yAdvAnchor, final float zAdvAnchor, final float wAdvAnchor,
      final float tightness, final Knot4 prev, final Knot4 next ) {

      if ( Utils.approx(tightness, 1.0f) ) {
         return Knot4.fromSegLinear(xNextAnchor, yNextAnchor, zNextAnchor,
            wNextAnchor, prev, next);
      }

      final float fac = ( tightness - 1.0f ) * -IUtils.ONE_SIX;
      prev.foreHandle.set(xCurrAnchor + ( xNextAnchor - xPrevAnchor ) * fac,
         yCurrAnchor + ( yNextAnchor - yPrevAnchor ) * fac, zCurrAnchor
            + ( zNextAnchor - zPrevAnchor ) * fac, wCurrAnchor + ( wNextAnchor
               - wPrevAnchor ) * fac);
      next.rearHandle.set(xNextAnchor - ( xAdvAnchor - xCurrAnchor ) * fac,
         yNextAnchor - ( yAdvAnchor - yCurrAnchor ) * fac, zNextAnchor
            - ( zAdvAnchor - zCurrAnchor ) * fac, wNextAnchor - ( wAdvAnchor
               - wCurrAnchor ) * fac);
      next.coord.set(xNextAnchor, yNextAnchor, zNextAnchor, wNextAnchor);

      return next;
   }

   /**
    * Sets two knots from a segment of a Catmull-Rom curve. The default curve
    * tightness is 0.0.<br>
    * <br>
    * Assumes that the previous knot's coordinate is set to a prior anchor
    * point. The previous knot's fore handle, the next knot's rear handle and
    * the next knot's coordinate are set by this function.
    *
    * @param prevAnchor the previous anchor
    * @param currAnchor the current anchor
    * @param nextAnchor the next anchor
    * @param advAnchor  the advance anchor
    * @param tightness  the curve tightness
    * @param prev       the previous knot
    * @param next       the next knot
    *
    * @return the next knot
    */
   public static Knot4 fromSegCatmull ( final Vec4 prevAnchor,
      final Vec4 currAnchor, final Vec4 nextAnchor, final Vec4 advAnchor,
      final float tightness, final Knot4 prev, final Knot4 next ) {

      return Knot4.fromSegCatmull(prevAnchor.x, prevAnchor.y, prevAnchor.z,
         prevAnchor.w, currAnchor.x, currAnchor.y, currAnchor.z, currAnchor.w,
         nextAnchor.x, nextAnchor.y, nextAnchor.z, nextAnchor.w, advAnchor.x,
         advAnchor.y, advAnchor.z, advAnchor.w, tightness, prev, next);
   }

   /**
    * Sets a knot from a line segment. Assumes that the previous knot's
    * coordinate is set to the first anchor point.<br>
    * <br>
    * The previous knot's fore handle, the next knot's rear handle and the
    * next knot's coordinate are set by this function.
    *
    * @param xNextAnchor the next anchor x
    * @param yNextAnchor the next anchor y
    * @param zNextAnchor the next anchor z
    * @param wNextAnchor the next anchor w
    * @param prev        the previous knot
    * @param next        the next knot
    *
    * @return the next knot
    */
   public static Knot4 fromSegLinear ( final float xNextAnchor,
      final float yNextAnchor, final float zNextAnchor, final float wNextAnchor,
      final Knot4 prev, final Knot4 next ) {

      final Vec4 prevCoord = prev.coord;
      final Vec4 nextCoord = next.coord;

      nextCoord.set(xNextAnchor, yNextAnchor, zNextAnchor, wNextAnchor);

      prev.foreHandle.set(prevCoord.x * IUtils.TWO_THIRDS + nextCoord.x
         * IUtils.ONE_THIRD, prevCoord.y * IUtils.TWO_THIRDS + nextCoord.y
            * IUtils.ONE_THIRD, prevCoord.z * IUtils.TWO_THIRDS + nextCoord.z
               * IUtils.ONE_THIRD, prevCoord.w * IUtils.TWO_THIRDS + nextCoord.w
                  * IUtils.ONE_THIRD);

      next.rearHandle.set(nextCoord.x * IUtils.TWO_THIRDS + prevCoord.x
         * IUtils.ONE_THIRD, nextCoord.y * IUtils.TWO_THIRDS + prevCoord.y
            * IUtils.ONE_THIRD, nextCoord.z * IUtils.TWO_THIRDS + prevCoord.z
               * IUtils.ONE_THIRD, nextCoord.w * IUtils.TWO_THIRDS + prevCoord.w
                  * IUtils.ONE_THIRD);

      return next;
   }

   /**
    * Sets a knot from a line segment. Assumes that the previous knot's
    * coordinate is set to the first anchor point.<br>
    * <br>
    * The previous knot's fore handle, the next knot's rear handle and the
    * next knot's coordinate are set by this function.
    *
    * @param nextAnchor the next anchor
    * @param prev       the previous knot
    * @param next       the next knot
    *
    * @return the next knot
    *
    * @see Knot4#fromSegLinear(float, float, float, float, Knot4, Knot4)
    */
   public static Knot4 fromSegLinear ( final Vec4 nextAnchor, final Knot4 prev,
      final Knot4 next ) {

      return Knot4.fromSegLinear(nextAnchor.x, nextAnchor.y, nextAnchor.z,
         nextAnchor.w, prev, next);
   }

   /**
    * Sets two knots from a segment of a quadratic curve. Assumes that the
    * previous knot's coordinate is set to the first anchor point.<br>
    * <br>
    * The previous knot's fore handle, the next knot's rear handle and the
    * next knot's coordinate are set by this function.
    *
    * @param xControl    the control point x
    * @param yControl    the control point y
    * @param zControl    the control point z
    * @param wControl    the control point w
    * @param xNextAnchor the next anchor x
    * @param yNextAnchor the next anchor y
    * @param zNextAnchor the next anchor z
    * @param wNextAnchor the next anchor w
    * @param prev        the previous knot
    * @param next        the next knot
    *
    * @return next knot
    */
   public static Knot4 fromSegQuadratic ( final float xControl,
      final float yControl, final float zControl, final float wControl,
      final float xNextAnchor, final float yNextAnchor, final float zNextAnchor,
      final float wNextAnchor, final Knot4 prev, final Knot4 next ) {

      final Vec4 prevCo = prev.coord;

      final float midpt23x = xControl * IUtils.TWO_THIRDS;
      final float midpt23y = yControl * IUtils.TWO_THIRDS;
      final float midpt23z = zControl * IUtils.TWO_THIRDS;
      final float midpt23w = wControl * IUtils.TWO_THIRDS;

      prev.foreHandle.set(midpt23x + IUtils.ONE_THIRD * prevCo.x, midpt23y
         + IUtils.ONE_THIRD * prevCo.y, midpt23z + IUtils.ONE_THIRD * prevCo.z,
         midpt23w + IUtils.ONE_THIRD * prevCo.w);

      next.rearHandle.set(midpt23x + IUtils.ONE_THIRD * xNextAnchor, midpt23y
         + IUtils.ONE_THIRD * yNextAnchor, midpt23z + IUtils.ONE_THIRD
            * zNextAnchor, midpt23w + IUtils.ONE_THIRD * wNextAnchor);

      next.coord.set(xNextAnchor, yNextAnchor, zNextAnchor, wNextAnchor);

      return next;
   }

   /**
    * Sets two knots from a segment of a quadratic curve. Assumes that the
    * previous knot's coordinate is set to the first anchor point.<br>
    * <br>
    * The previous knot's fore handle, the next knot's rear handle and the
    * next knot's coordinate are set by this function.
    *
    * @param control    the control point
    * @param nextAnchor the next anchor point
    * @param prev       the previous knot
    * @param next       the next knot
    *
    * @return the next knot
    *
    * @see Knot4#fromSegQuadratic(float, float, float, float, float, float,
    *      float, float, Knot4, Knot4)
    */
   public static Knot4 fromSegQuadratic ( final Vec4 control,
      final Vec4 nextAnchor, final Knot4 prev, final Knot4 next ) {

      return Knot4.fromSegQuadratic(control.x, control.y, control.z, control.w,
         nextAnchor.x, nextAnchor.y, nextAnchor.z, nextAnchor.w, prev, next);
   }

   /**
    * Gets the rear handle of a knot as a direction, rather than as a point.
    *
    * @param knot   the knot
    * @param target the output vector
    *
    * @return the rear handle vector
    *
    * @see Vec4#sub(Vec4, Vec4, Vec4)
    * @see Vec4#normalize(Vec4, Vec4)
    */
   public static Vec4 rearDir ( final Knot4 knot, final Vec4 target ) {

      Vec4.sub(knot.rearHandle, knot.coord, target);
      return Vec4.normalize(target, target);
   }

   /**
    * Returns the magnitude of the knot's rear handle, i.e., the Euclidean
    * distance between the rear handle and the coordinate.
    *
    * @param knot the knot
    *
    * @return the magnitude
    *
    * @see Vec4#distEuclidean(Vec4, Vec4)
    */
   public static float rearMag ( final Knot4 knot ) {

      return Vec4.distEuclidean(knot.rearHandle, knot.coord);
   }

   /**
    * Gets the rear handle of a knot as a vector, rather than as a point.
    *
    * @param knot   the knot
    * @param target the output vector
    *
    * @return the rear handle vector
    *
    * @see Vec4#sub(Vec4, Vec4, Vec4)
    */
   public static Vec4 rearVec ( final Knot4 knot, final Vec4 target ) {

      return Vec4.sub(knot.rearHandle, knot.coord, target);
   }

   /**
    * Smoothes the handles of a knot with reference to a previous and next
    * knot. A helper function to {@link Curve4#smoothHandles(Curve4)} .
    *
    * @param prev  the previous knot
    * @param curr  the current knot
    * @param next  the next knot
    * @param carry a temporary vector
    *
    * @return the current knot
    */
   public static Knot4 smoothHandles ( final Knot4 prev, final Knot4 curr,
      final Knot4 next, final Vec4 carry ) {

      final Vec4 coCurr = curr.coord;
      final Vec4 coPrev = prev.coord;
      final Vec4 coNext = next.coord;

      final float xRear = coPrev.x - coCurr.x;
      final float yRear = coPrev.y - coCurr.y;
      final float zRear = coPrev.z - coCurr.z;
      final float wRear = coPrev.w - coCurr.w;

      final float xFore = coNext.x - coCurr.x;
      final float yFore = coNext.y - coCurr.y;
      final float zFore = coNext.z - coCurr.z;
      final float wFore = coNext.w - coCurr.w;

      final float bmSq = xRear * xRear + yRear * yRear + zRear * zRear + wRear
         * wRear;
      final float bmInv = Utils.invSqrt(bmSq);

      final float fmSq = xFore * xFore + yFore * yFore + zFore * zFore + wFore
         * wFore;
      final float fmInv = Utils.invSqrt(fmSq);

      final float xDir = carry.x + xRear * bmInv - xFore * fmInv;
      final float yDir = carry.y + yRear * bmInv - yFore * fmInv;
      final float zDir = carry.z + zRear * bmInv - zFore * fmInv;
      final float wDir = carry.w + wRear * bmInv - wFore * fmInv;

      final float rescl = IUtils.ONE_THIRD * Utils.invSqrt(xDir * xDir + yDir
         * yDir + zDir * zDir + wDir * wDir);
      carry.x = xDir * rescl;
      carry.y = yDir * rescl;
      carry.z = zDir * rescl;
      carry.w = wDir * rescl;

      final float bMag = bmSq * bmInv;
      curr.rearHandle.set(coCurr.x + bMag * carry.x, coCurr.y + bMag * carry.y,
         coCurr.z + bMag * carry.z, coCurr.w + bMag * carry.w);

      final float fMag = fmSq * fmInv;
      curr.foreHandle.set(coCurr.x - fMag * carry.x, coCurr.y - fMag * carry.y,
         coCurr.z - fMag * carry.z, coCurr.w - fMag * carry.w);

      return curr;
   }

   /**
    * Smoothes the fore handle of the first knot in an open curve. A helper
    * function to {@link Curve4#smoothHandles(Curve4)} .
    *
    * @param curr  the current knot
    * @param next  the next knot
    * @param carry a temporary vector
    *
    * @return the current knot
    */
   public static Knot4 smoothHandlesFirst ( final Knot4 curr, final Knot4 next,
      final Vec4 carry ) {

      final Vec4 coCurr = curr.coord;
      final Vec4 coNext = next.coord;

      final float xRear = -coCurr.x;
      final float yRear = -coCurr.y;
      final float zRear = -coCurr.z;
      final float wRear = -coCurr.w;

      final float xFore = coNext.x + xRear;
      final float yFore = coNext.y + yRear;
      final float zFore = coNext.z + zRear;
      final float wFore = coNext.w + wRear;

      final float bmSq = xRear * xRear + yRear * yRear + zRear * zRear + wRear
         * wRear;
      final float bmInv = Utils.invSqrt(bmSq);

      final float fmSq = xFore * xFore + yFore * yFore + zFore * zFore + wFore
         * wFore;
      final float fmInv = Utils.invSqrt(fmSq);

      final float xDir = carry.x + xRear * bmInv - xFore * fmInv;
      final float yDir = carry.y + yRear * bmInv - yFore * fmInv;
      final float zDir = carry.z + zRear * bmInv - zFore * fmInv;
      final float wDir = carry.w + wRear * bmInv - wFore * fmInv;

      final float rescl = IUtils.ONE_THIRD * Utils.invSqrt(xDir * xDir + yDir
         * yDir + zDir * zDir + wDir * wDir);
      carry.x = xDir * rescl;
      carry.y = yDir * rescl;
      carry.z = zDir * rescl;
      carry.w = wDir * rescl;

      final float fMag = fmSq * fmInv;
      curr.foreHandle.set(coCurr.x - fMag * carry.x, coCurr.y - fMag * carry.y,
         coCurr.z - fMag * carry.z, coCurr.w - fMag * carry.w);

      return curr;
   }

   /**
    * Smoothes the rear handle of the last knot in an open curve. A helper
    * function to {@link Curve4#smoothHandles(Curve4)} .
    *
    * @param prev  the previous knot
    * @param curr  the current knot
    * @param carry a temporary vector
    *
    * @return the current knot
    */
   public static Knot4 smoothHandlesLast ( final Knot4 prev, final Knot4 curr,
      final Vec4 carry ) {

      final Vec4 coCurr = curr.coord;
      final Vec4 coPrev = prev.coord;

      final float xFore = -coCurr.x;
      final float yFore = -coCurr.y;
      final float zFore = -coCurr.z;
      final float wFore = -coCurr.w;

      final float xRear = coPrev.x + xFore;
      final float yRear = coPrev.y + yFore;
      final float zRear = coPrev.z + zFore;
      final float wRear = coPrev.w + wFore;

      final float bmSq = xRear * xRear + yRear * yRear + zRear * zRear + wRear
         * wRear;
      final float bmInv = Utils.invSqrt(bmSq);

      final float fmSq = xFore * xFore + yFore * yFore + zFore * zFore + wFore
         * wFore;
      final float fmInv = Utils.invSqrt(fmSq);

      final float xDir = carry.x + xRear * bmInv - xFore * fmInv;
      final float yDir = carry.y + yRear * bmInv - yFore * fmInv;
      final float zDir = carry.z + zRear * bmInv - zFore * fmInv;
      final float wDir = carry.w + wRear * bmInv - wFore * fmInv;

      final float rescl = IUtils.ONE_THIRD * Utils.invSqrt(xDir * xDir + yDir
         * yDir + zDir * zDir + wDir * wDir);
      carry.x = xDir * rescl;
      carry.y = yDir * rescl;
      carry.z = zDir * rescl;
      carry.w = wDir * rescl;

      final float bMag = bmSq * bmInv;
      curr.rearHandle.set(coCurr.x + bMag * carry.x, coCurr.y + bMag * carry.y,
         coCurr.z + bMag * carry.z, coCurr.w + bMag * carry.w);

      return curr;
   }

   /**
    * An abstract class to facilitate the creation of knot easing functions.
    */
   public abstract static class AbstrEasing implements Utils.EasingFuncObj <
      Knot4 > {

      /**
       * The default constructor.
       */
      protected AbstrEasing ( ) {}

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
      public Knot4 apply ( final Knot4 origin, final Knot4 dest,
         final Float step, final Knot4 target ) {

         final float t = step;
         if ( t <= 0.0f ) { return target.set(origin); }
         if ( t >= 1.0f ) { return target.set(dest); }
         return this.applyUnclamped(origin, dest, t, target);
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
      public abstract Knot4 applyUnclamped ( final Knot4 origin,
         final Knot4 dest, final float step, final Knot4 target );

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
      public Knot4 applyUnclamped ( final Knot4 origin, final Knot4 dest,
         final float step, final Knot4 target ) {

         final float u = 1.0f - step;

         final Vec4 orCo = origin.coord;
         final Vec4 orFh = origin.foreHandle;
         final Vec4 orRh = origin.rearHandle;

         final Vec4 deCo = dest.coord;
         final Vec4 deFh = dest.foreHandle;
         final Vec4 deRh = dest.rearHandle;

         target.coord.set(u * orCo.x + step * deCo.x, u * orCo.y + step
            * deCo.y, u * orCo.z + step * deCo.z, u * orCo.w + step * deCo.w);
         target.foreHandle.set(u * orFh.x + step * deFh.x, u * orFh.y + step
            * deFh.y, u * orFh.z + step * deFh.z, u * orFh.w + step * deFh.w);
         target.rearHandle.set(u * orRh.x + step * deRh.x, u * orRh.y + step
            * deRh.y, u * orRh.z + step * deRh.z, u * orRh.w + step * deRh.w);

         return target;
      }

   }

}
