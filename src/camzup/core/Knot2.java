package camzup.core;

/**
 * Organizes the vectors that shape a cubic Bezier curve into a coordinate
 * (or anchor point), fore handle (the following control point) and rear
 * handle (the preceding control point).
 */
public class Knot2 implements Comparable < Knot2 > {

   /**
    * The spatial coordinate of the knot.
    */
   public final Vec2 coord = new Vec2();

   /**
    * The handle that warps the curve segment heading away from the knot along
    * the direction of the curve.
    */
   public final Vec2 foreHandle = new Vec2();

   /**
    * The handle that warps the curve segment heading towards the knot along
    * the direction of the curve.
    */
   public final Vec2 rearHandle = new Vec2();

   /**
    * The default constructor.
    */
   public Knot2 ( ) {}

   /**
    * Creates a knot from a coordinate.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    */
   public Knot2 ( final float xCoord, final float yCoord ) {

      this.set(xCoord, yCoord);
   }

   /**
    * Creates a knot from a coordinates and fore handle. The rear handle is a
    * mirror of the fore handle.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    * @param xFore  the fore handle x
    * @param yFore  the fore handle y
    */
   public Knot2 ( final float xCoord, final float yCoord, final float xFore,
      final float yFore ) {

      this.set(xCoord, yCoord, xFore, yFore);
   }

   /**
    * Creates a knot from real numbers.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    * @param xFore  the fore handle x
    * @param yFore  the fore handle y
    * @param xRear  the rear handle x
    * @param yRear  the rear handle y
    */
   public Knot2 ( final float xCoord, final float yCoord, final float xFore,
      final float yFore, final float xRear, final float yRear ) {

      this.set(xCoord, yCoord, xFore, yFore, xRear, yRear);
   }

   /**
    * Creates a knot from a source knot.
    *
    * @param source the source
    */
   public Knot2 ( final Knot2 source ) { this.set(source); }

   /**
    * Creates a knot from a coordinate.
    *
    * @param coord the coordinate
    */
   public Knot2 ( final Vec2 coord ) { this.set(coord); }

   /**
    * Creates a knot from a coordinate and fore handle. The rear handle is a
    * mirror of the fore handle.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    */
   public Knot2 ( final Vec2 coord, final Vec2 foreHandle ) {

      this.set(coord, foreHandle);
   }

   /**
    * Creates a knot from a series of vectors.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    * @param rearHandle the rear handle
    */
   public Knot2 ( final Vec2 coord, final Vec2 foreHandle,
      final Vec2 rearHandle ) {

      this.set(coord, foreHandle, rearHandle);
   }

   /**
    * Adopts the fore handle of a source knot.
    *
    * @param source the source knot
    *
    * @return this knot
    */
   public Knot2 adoptForeHandle ( final Knot2 source ) {

      this.foreHandle.set(this.coord.x + source.foreHandle.x - source.coord.x,
         this.coord.y + source.foreHandle.y - source.coord.y);

      return this;
   }

   /**
    * Adopts the fore handle and rear handle of a source knot.
    *
    * @param source the source knot
    *
    * @return this knot
    */
   public Knot2 adoptHandles ( final Knot2 source ) {

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
   public Knot2 adoptRearHandle ( final Knot2 source ) {

      this.rearHandle.set(this.coord.x + source.rearHandle.x - source.coord.x,
         this.coord.y + source.rearHandle.y - source.coord.y);

      return this;
   }

   /**
    * Aligns this knot's handles in the same direction while preserving their
    * magnitude.
    *
    * @return this knot
    *
    * @see Knot2#alignHandlesForward()
    */
   public Knot2 alignHandles ( ) { return this.alignHandlesForward(); }

   /**
    * Aligns this knot's fore handle to its rear handle while preserving
    * magnitude.
    *
    * @return this knot
    *
    * @see Utils#hypot(float, float)
    * @see Utils#invSqrtUnchecked(float)
    */
   public Knot2 alignHandlesBackward ( ) {

      final float xCoord = this.coord.x;
      final float yCoord = this.coord.y;

      final float xRear = this.rearHandle.x - xCoord;
      final float yRear = this.rearHandle.y - yCoord;

      final float rmsq = xRear * xRear + yRear * yRear;
      if ( rmsq > 0.0f ) {
         final float flipRescale = -Utils.hypot(this.foreHandle.x - xCoord,
            this.foreHandle.y - yCoord) * Utils.invSqrtUnchecked(rmsq);

         this.foreHandle.x = xRear * flipRescale + xCoord;
         this.foreHandle.y = yRear * flipRescale + yCoord;
      }

      return this;
   }

   /**
    * Aligns this knot's rear handle to its fore handle while preserving
    * magnitude.
    *
    * @return this knot
    *
    * @see Utils#hypot(float, float)
    * @see Utils#invSqrtUnchecked(float)
    */
   public Knot2 alignHandlesForward ( ) {

      final float xCoord = this.coord.x;
      final float yCoord = this.coord.y;

      final float xFore = this.foreHandle.x - xCoord;
      final float yFore = this.foreHandle.y - yCoord;

      final float fmsq = xFore * xFore + yFore * yFore;
      if ( fmsq > 0.0f ) {
         final float flipRescale = -Utils.hypot(this.rearHandle.x - xCoord,
            this.rearHandle.y - yCoord) * Utils.invSqrtUnchecked(fmsq);

         this.rearHandle.x = xFore * flipRescale + xCoord;
         this.rearHandle.y = yFore * flipRescale + yCoord;
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
   public int compareTo ( final Knot2 knot ) {

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
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Knot2 ) obj);
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
    * @see Knot2#mirrorHandlesForward()
    */
   public Knot2 mirrorHandles ( ) { return this.mirrorHandlesForward(); }

   /**
    * Sets the fore handle to mirror the rear handle: the fore will have the
    * same magnitude and negated direction of the rear.
    *
    * @return this knot
    */
   public Knot2 mirrorHandlesBackward ( ) {

      this.foreHandle.set(this.coord.x - ( this.rearHandle.x - this.coord.x ),
         this.coord.y - ( this.rearHandle.y - this.coord.y ));

      return this;
   }

   /**
    * Sets the rear handle to mirror the fore handle: the rear will have the
    * same magnitude and negated direction of the fore.
    *
    * @return this knot
    */
   public Knot2 mirrorHandlesForward ( ) {

      this.rearHandle.set(this.coord.x - ( this.foreHandle.x - this.coord.x ),
         this.coord.y - ( this.foreHandle.y - this.coord.y ));

      return this;
   }

   /**
    * Relocates the knot to a new location while maintaining the relationship
    * between the central coordinate and its two handles.
    *
    * @param v the location
    *
    * @return this knot
    *
    * @see Vec2#add(Vec2, Vec2, Vec2)
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    */
   @Experimental
   public Knot2 relocate ( final Vec2 v ) {

      Vec2.sub(this.foreHandle, this.coord, this.foreHandle);
      Vec2.sub(this.rearHandle, this.coord, this.rearHandle);
      this.coord.set(v);
      Vec2.add(this.foreHandle, this.coord, this.foreHandle);
      Vec2.add(this.rearHandle, this.coord, this.rearHandle);

      return this;
   }

   /**
    * Reverses the knot's direction by swapping the fore and rear handles.
    *
    * @return this knot
    */
   public Knot2 reverse ( ) {

      final float tx = this.foreHandle.x;
      final float ty = this.foreHandle.y;
      this.foreHandle.set(this.rearHandle);
      this.rearHandle.set(tx, ty);

      return this;
   }

   /**
    * Rotates this knot's fore handle by an angle in radians.
    *
    * @param radians the angle
    *
    * @return this knot
    */
   public Knot2 rotateForeHandle ( final float radians ) {

      final double radd = radians;
      final float cosa = ( float ) Math.cos(radd);
      final float sina = ( float ) Math.sin(radd);
      return this.rotateForeHandle(cosa, sina);
   }

   /**
    * Rotates this knot's fore handle by the cosine and sine of an angle.
    *
    * @param cosa the cosine
    * @param sina the sine
    *
    * @return this knot
    *
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    * @see Vec2#rotateZ(Vec2, float, float, Vec2)
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   public Knot2 rotateForeHandle ( final float cosa, final float sina ) {

      Vec2.sub(this.foreHandle, this.coord, this.foreHandle);
      Vec2.rotateZ(this.foreHandle, cosa, sina, this.foreHandle);
      Vec2.add(this.foreHandle, this.coord, this.foreHandle);

      return this;
   }

   /**
    * Rotates this knot's fore and rear handles by an angle in radians.
    *
    * @param radians the angle
    *
    * @return this knot
    *
    * @see Knot2#rotateHandles(float, float)
    */
   public Knot2 rotateHandles ( final float radians ) {

      final double radd = radians;
      final float cosa = ( float ) Math.cos(radd);
      final float sina = ( float ) Math.sin(radd);
      return this.rotateHandles(cosa, sina);
   }

   /**
    * Rotates this knot's fore and rear handles by the cosine and sine of an
    * angle.
    *
    * @param cosa the cosine
    * @param sina the sine
    *
    * @return this knot
    *
    * @see Knot2#rotateForeHandle(float, float)
    * @see Knot2#rotateRearHandle(float, float)
    */
   public Knot2 rotateHandles ( final float cosa, final float sina ) {

      this.rotateForeHandle(cosa, sina);
      this.rotateRearHandle(cosa, sina);

      return this;
   }

   /**
    * Rotates this knot's rear handle by an angle in radians.
    *
    * @param radians the angle
    *
    * @return this knot
    *
    * @see Knot2#rotateRearHandle(float, float)
    */
   public Knot2 rotateRearHandle ( final float radians ) {

      final double radd = radians;
      final float cosa = ( float ) Math.cos(radd);
      final float sina = ( float ) Math.sin(radd);
      return this.rotateRearHandle(cosa, sina);
   }

   /**
    * Rotates this knot's rear handle by the cosine and sine of an angle.
    *
    * @param cosa the cosine
    * @param sina the sine
    *
    * @return this knot
    *
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    * @see Vec2#rotateZ(Vec2, float, float, Vec2)
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   public Knot2 rotateRearHandle ( final float cosa, final float sina ) {

      Vec2.sub(this.rearHandle, this.coord, this.rearHandle);
      Vec2.rotateZ(this.rearHandle, cosa, sina, this.rearHandle);
      Vec2.add(this.rearHandle, this.coord, this.rearHandle);

      return this;
   }

   /**
    * Rotates this knot around the z axis by an angle in radians.
    *
    * @param radians the angle
    *
    * @return this knot
    *
    * @see Knot2#rotateZ(float, float)
    */
   public Knot2 rotateZ ( final float radians ) {

      final double radd = radians;
      final float cosa = ( float ) Math.cos(radd);
      final float sina = ( float ) Math.sin(radd);
      return this.rotateZ(cosa, sina);
   }

   /**
    * Rotates this knot around the z axis. Accepts calculated sine and cosine
    * of an angle, so that collections of knots can be efficiently rotated
    * without repeatedly calling cos and sin.
    *
    * @param cosa cosine of the angle
    * @param sina sine of the angle
    *
    * @return this knot
    *
    * @see Vec2#rotateZ(Vec2, float, float, Vec2)
    */
   public Knot2 rotateZ ( final float cosa, final float sina ) {

      Vec2.rotateZ(this.coord, cosa, sina, this.coord);
      Vec2.rotateZ(this.foreHandle, cosa, sina, this.foreHandle);
      Vec2.rotateZ(this.rearHandle, cosa, sina, this.rearHandle);

      return this;
   }

   /**
    * Scales this knot by a factor.
    *
    * @param scale the factor
    *
    * @return this knot
    *
    * @see Knot2#scaleUnchecked(float)
    */
   public Knot2 scale ( final float scale ) {

      if ( scale != 0.0f ) { return this.scaleUnchecked(scale); }

      return this;
   }

   /**
    * Scales this knot by a non-uniform scalar.
    *
    * @param scale the non-uniform scalar
    *
    * @return this knot
    *
    * @see Knot2#scaleUnchecked(Vec2)
    */
   public Knot2 scale ( final Vec2 scale ) {

      if ( Vec2.all(scale) ) { return this.scaleUnchecked(scale); }

      return this;
   }

   /**
    * Scales the fore handle by a factor.
    *
    * @param scalar the scalar
    *
    * @return this knot
    */
   public Knot2 scaleForeHandleBy ( final float scalar ) {

      this.foreHandle.x = this.coord.x + scalar * ( this.foreHandle.x
         - this.coord.x );
      this.foreHandle.y = this.coord.y + scalar * ( this.foreHandle.y
         - this.coord.y );

      return this;
   }

   /**
    * Scales the fore handle to a magnitude.
    *
    * @param magnitude the magnitude
    *
    * @return this knot
    *
    * @see Vec2#add(Vec2, Vec2, Vec2)
    * @see Vec2#mul(Vec2, float, Vec2)
    * @see Vec2#subNorm(Vec2, Vec2, Vec2)
    */
   public Knot2 scaleForeHandleTo ( final float magnitude ) {

      Vec2.subNorm(this.foreHandle, this.coord, this.foreHandle);
      Vec2.mul(this.foreHandle, magnitude, this.foreHandle);
      Vec2.add(this.foreHandle, this.coord, this.foreHandle);

      return this;
   }

   /**
    * Scales both the fore and rear handle by a factor.
    *
    * @param scalar the scalar
    *
    * @return this knot
    *
    * @see Knot2#scaleForeHandleBy(float)
    * @see Knot2#scaleRearHandleBy(float)
    */
   public Knot2 scaleHandlesBy ( final float scalar ) {

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
    * @see Knot2#scaleForeHandleTo(float)
    * @see Knot2#scaleRearHandleTo(float)
    */
   public Knot2 scaleHandlesTo ( final float magnitude ) {

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
   public Knot2 scaleRearHandleBy ( final float scalar ) {

      this.rearHandle.x = this.coord.x + scalar * ( this.rearHandle.x
         - this.coord.x );
      this.rearHandle.y = this.coord.y + scalar * ( this.rearHandle.y
         - this.coord.y );

      return this;
   }

   /**
    * Scales the rear handle to a magnitude.
    *
    * @param magnitude the magnitude
    *
    * @return this knot
    *
    * @see Vec2#add(Vec2, Vec2, Vec2)
    * @see Vec2#mul(Vec2, float, Vec2)
    * @see Vec2#subNorm(Vec2, Vec2, Vec2)
    */
   public Knot2 scaleRearHandleTo ( final float magnitude ) {

      Vec2.subNorm(this.rearHandle, this.coord, this.rearHandle);
      Vec2.mul(this.rearHandle, magnitude, this.rearHandle);
      Vec2.add(this.rearHandle, this.coord, this.rearHandle);

      return this;
   }

   /**
    * Sets the coordinate, fore and rear handles to the input coordinate.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    *
    * @return this knot
    *
    * @see Utils#copySign(float, float)
    */
   public Knot2 set ( final float xCoord, final float yCoord ) {

      final float xOff = Utils.copySign(IUtils.EPSILON, xCoord);
      final float yOff = Utils.copySign(IUtils.EPSILON, yCoord);

      return this.set(xCoord, yCoord, xCoord + xOff, yCoord + yOff, xCoord
         - xOff, yCoord - yOff);
   }

   /**
    * Sets the knot's coordinate and fore handle. The rear handle is a mirror
    * of the fore handle.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    * @param xFore  the fore handle x
    * @param yFore  the fore handle y
    *
    * @return the knot
    */
   public Knot2 set ( final float xCoord, final float yCoord, final float xFore,
      final float yFore ) {

      this.coord.set(xCoord, yCoord);
      this.foreHandle.set(xFore, yFore);
      this.rearHandle.set(xCoord - ( xFore - xCoord ), yCoord - ( yFore
         - yCoord ));

      return this;
   }

   /**
    * Sets the knot's coordinate, fore handle and rear handle by component.
    *
    * @param xCoord the x coordinate
    * @param yCoord the y coordinate
    * @param xFore  the fore handle x
    * @param yFore  the fore handle y
    * @param xRear  the rear handle x
    * @param yRear  the rear handle y
    *
    * @return this knot
    */
   public Knot2 set ( final float xCoord, final float yCoord, final float xFore,
      final float yFore, final float xRear, final float yRear ) {

      this.coord.set(xCoord, yCoord);
      this.foreHandle.set(xFore, yFore);
      this.rearHandle.set(xRear, yRear);

      return this;
   }

   /**
    * Sets this knot from a source knot.
    *
    * @param source the source
    *
    * @return this knot
    */
   public Knot2 set ( final Knot2 source ) {

      return this.set(source.coord, source.foreHandle, source.rearHandle);
   }

   /**
    * Sets the coordinate, fore and rear handles to the input coordinate.
    *
    * @param coord the coordinate
    *
    * @return this knot
    */
   public Knot2 set ( final Vec2 coord ) {

      return this.set(coord.x, coord.y);
   }

   /**
    * Sets the knot's coordinates and fore handle. The rear handle is a mirror
    * of the fore handle.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    *
    * @return this knot
    */
   public Knot2 set ( final Vec2 coord, final Vec2 foreHandle ) {

      return this.set(coord.x, coord.y, foreHandle.x, foreHandle.y);
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
   public Knot2 set ( final Vec2 coord, final Vec2 foreHandle,
      final Vec2 rearHandle ) {

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

      return this.toString(new StringBuilder(256), places).toString();
   }

   /**
    * Multiplies this knot by a matrix.
    *
    * @param m the matrix
    *
    * @return this knot
    *
    * @see Mat3#mulPoint(Mat3, Vec2, Vec2)
    */
   public Knot2 transform ( final Mat3 m ) {

      Mat3.mulPoint(m, this.coord, this.coord);
      Mat3.mulPoint(m, this.foreHandle, this.foreHandle);
      Mat3.mulPoint(m, this.rearHandle, this.rearHandle);

      return this;
   }

   /**
    * Multiplies this knot by a transform.
    *
    * @param tr the transform
    *
    * @return this knot
    *
    * @see Transform2#mulPoint(Transform2, Vec2, Vec2)
    */
   public Knot2 transform ( final Transform2 tr ) {

      Transform2.mulPoint(tr, this.coord, this.coord);
      Transform2.mulPoint(tr, this.foreHandle, this.foreHandle);
      Transform2.mulPoint(tr, this.rearHandle, this.rearHandle);

      return this;
   }

   /**
    * Translates this knot by a vector.
    *
    * @param v the vector
    *
    * @return this knot
    *
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   public Knot2 translate ( final Vec2 v ) {

      Vec2.add(this.coord, v, this.coord);
      Vec2.add(this.foreHandle, v, this.foreHandle);
      Vec2.add(this.rearHandle, v, this.rearHandle);

      return this;
   }

   /**
    * Scales this knot by a factor. Does not check scale for validity.
    *
    * @param scale the factor
    *
    * @return this knot
    *
    * @see Vec2#mul(Vec2, float, Vec2)
    */
   Knot2 scaleUnchecked ( final float scale ) {

      Vec2.mul(this.coord, scale, this.coord);
      Vec2.mul(this.foreHandle, scale, this.foreHandle);
      Vec2.mul(this.rearHandle, scale, this.rearHandle);

      return this;
   }

   /**
    * Scales this knot by a non-uniform scalar. Does not check scale for
    * validity.
    *
    * @param scale the non-uniform scalar
    *
    * @return this knot
    *
    * @see Vec2#hadamard(Vec2, Vec2, Vec2)
    */
   Knot2 scaleUnchecked ( final Vec2 scale ) {

      Vec2.hadamard(this.coord, scale, this.coord);
      Vec2.hadamard(this.foreHandle, scale, this.foreHandle);
      Vec2.hadamard(this.rearHandle, scale, this.rearHandle);

      return this;
   }

   /**
    * An internal helper function to format a vector as a Python tuple, then
    * append it to a {@link StringBuilder}. Used for testing purposes to
    * compare results with Blender 2.9x. Appends a z component to promote the
    * vector to 3D.
    *
    * @param pyCd the string builder
    * @param z    the z offset
    *
    * @return the string builder
    */
   @Experimental
   StringBuilder toBlenderCode ( final StringBuilder pyCd, final float z ) {

      pyCd.append("{\"co\": ");
      this.coord.toBlenderCode(pyCd, z);
      pyCd.append(", \"handle_right\": ");
      this.foreHandle.toBlenderCode(pyCd, z);
      pyCd.append(", \"handle_left\": ");
      this.rearHandle.toBlenderCode(pyCd, z);
      pyCd.append('}');
      return pyCd;
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

      sb.append("{\"coord\":");
      this.coord.toString(sb, places);
      sb.append(",\"foreHandle\":");
      this.foreHandle.toString(sb, places);
      sb.append(",\"rearHandle\":");
      this.rearHandle.toString(sb, places);
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
   protected boolean equals ( final Knot2 other ) {

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
    * @see Vec2#bezierPoint(Vec2, Vec2, Vec2, Vec2, float, Vec2)
    */
   public static Vec2 bezierPoint ( final Knot2 a, final Knot2 b,
      final float step, final Vec2 target ) {

      return Vec2.bezierPoint(a.coord, a.foreHandle, b.rearHandle, b.coord,
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
    * @see Vec2#bezierTangent(Vec2, Vec2, Vec2, Vec2, float, Vec2)
    */
   public static Vec2 bezierTangent ( final Knot2 a, final Knot2 b,
      final float step, final Vec2 target ) {

      return Vec2.bezierTangent(a.coord, a.foreHandle, b.rearHandle, b.coord,
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
    * @see Vec2#bezierTanUnit(Vec2, Vec2, Vec2, Vec2, float, Vec2)
    */
   public static Vec2 bezierTanUnit ( final Knot2 a, final Knot2 b,
      final float step, final Vec2 target ) {

      return Vec2.bezierTanUnit(a.coord, a.foreHandle, b.rearHandle, b.coord,
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
    * @see Vec2#subNorm(Vec2, Vec2, Vec2)
    */
   public static Vec2 foreDir ( final Knot2 knot, final Vec2 target ) {

      return Vec2.subNorm(knot.foreHandle, knot.coord, target);
   }

   /**
    * Returns the magnitude of the knot's fore handle, i.e., the Euclidean
    * distance between the fore handle and the coordinate.
    *
    * @param knot the knot
    *
    * @return the magnitude
    *
    * @see Vec2#distEuclidean(Vec2, Vec2)
    */
   public static float foreMag ( final Knot2 knot ) {

      return Vec2.distEuclidean(knot.foreHandle, knot.coord);
   }

   /**
    * Gets the fore handle of a knot as a vector, rather than as a point.
    *
    * @param knot   the knot
    * @param target the output vector
    *
    * @return the fore handle vector
    *
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    */
   public static Vec2 foreVec ( final Knot2 knot, final Vec2 target ) {

      return Vec2.sub(knot.foreHandle, knot.coord, target);
   }

   /**
    * Sets two knots from a segment of a Catmull-Rom curve. Assumes that the
    * previous knot's coordinate is set to a prior anchor point.<br>
    * <br>
    * The previous knot's fore handle, the next knot's rear handle and the
    * next knot's coordinate are set by this function.
    *
    * @param xPrevAnchor the previous anchor x
    * @param yPrevAnchor the previous anchor y
    * @param xCurrAnchor the current anchor x
    * @param yCurrAnchor the current anchor y
    * @param xNextAnchor the next anchor x
    * @param yNextAnchor the next anchor y
    * @param xAdvAnchor  the advance anchor x
    * @param yAdvAnchor  the advance anchor y
    * @param tightness   the curve tightness
    * @param prev        the previous knot
    * @param next        the next knot
    *
    * @return the next knot
    */
   public static Knot2 fromSegCatmull ( final float xPrevAnchor,
      final float yPrevAnchor, final float xCurrAnchor, final float yCurrAnchor,
      final float xNextAnchor, final float yNextAnchor, final float xAdvAnchor,
      final float yAdvAnchor, final float tightness, final Knot2 prev,
      final Knot2 next ) {

      if ( Utils.approx(tightness, 1.0f) ) {
         return Knot2.fromSegLinear(xNextAnchor, yNextAnchor, prev, next);
      }

      final float fac = ( tightness - 1.0f ) * -IUtils.ONE_SIX;
      prev.foreHandle.set(xCurrAnchor + ( xNextAnchor - xPrevAnchor ) * fac,
         yCurrAnchor + ( yNextAnchor - yPrevAnchor ) * fac);
      next.rearHandle.set(xNextAnchor - ( xAdvAnchor - xCurrAnchor ) * fac,
         yNextAnchor - ( yAdvAnchor - yCurrAnchor ) * fac);
      next.coord.set(xNextAnchor, yNextAnchor);

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
   public static Knot2 fromSegCatmull ( final Vec2 prevAnchor,
      final Vec2 currAnchor, final Vec2 nextAnchor, final Vec2 advAnchor,
      final float tightness, final Knot2 prev, final Knot2 next ) {

      return Knot2.fromSegCatmull(prevAnchor.x, prevAnchor.y, currAnchor.x,
         currAnchor.y, nextAnchor.x, nextAnchor.y, advAnchor.x, advAnchor.y,
         tightness, prev, next);
   }

   /**
    * Sets two knots from a segment of a cubic curve.<br>
    * <br>
    * Assumes that the previous knot's coordinate is set to the first anchor
    * point. The previous knot's fore handle, the next knot's rear handle and
    * the next knot's coordinate are set by this function.
    *
    * @param xPrevControl the previous control point x
    * @param yPrevControl the previous control point y
    * @param xNextControl the next control point x
    * @param yNextControl the next control point y
    * @param xNextAnchor  the next anchor x
    * @param yNextAnchor  the next anchor y
    * @param prev         the previous knot
    * @param next         the next knot
    *
    * @return next knot
    */
   public static Knot2 fromSegCubic ( final float xPrevControl,
      final float yPrevControl, final float xNextControl,
      final float yNextControl, final float xNextAnchor,
      final float yNextAnchor, final Knot2 prev, final Knot2 next ) {

      prev.foreHandle.set(xPrevControl, yPrevControl);
      next.rearHandle.set(xNextControl, yNextControl);
      next.coord.set(xNextAnchor, yNextAnchor);

      return next;
   }

   /**
    * Sets two knots from a segment of a cubic curve. Assumes that the
    * previous knot's coordinate is set to the first anchor point.<br>
    * <br>
    * The previous knot's fore handle, the next knot's rear handle and the
    * next knot's coordinate are set by this function.
    *
    * @param prevControl the previous control point
    * @param nextControl the next control point
    * @param nextAnchor  the next anchor point
    * @param prev        the previous knot
    * @param next        the next knot
    *
    * @return the next knot
    *
    * @see Knot2#fromSegCubic(float, float, float, float, float, float, Knot2,
    *      Knot2)
    */
   public static Knot2 fromSegCubic ( final Vec2 prevControl,
      final Vec2 nextControl, final Vec2 nextAnchor, final Knot2 prev,
      final Knot2 next ) {

      return Knot2.fromSegCubic(prevControl.x, prevControl.y, nextControl.x,
         nextControl.y, nextAnchor.x, nextAnchor.y, prev, next);
   }

   /**
    * Sets two knots from a segment of a cubic curve which reflect an existing
    * segment.<br>
    * <br>
    * Assumes that the previous knot's coordinate is set to the first anchor
    * point. The previous knot's fore handle, the next knot's rear handle and
    * the next knot's coordinate are set by this function.
    *
    * @param xNextControl the next control point x
    * @param yNextControl the next control point y
    * @param xNextAnchor  the next anchor x
    * @param yNextAnchor  the next anchor y
    * @param prev         the previous knot
    * @param next         the next knot
    *
    * @return next knot
    */
   public static Knot2 fromSegCubicRefl ( final float xNextControl,
      final float yNextControl, final float xNextAnchor,
      final float yNextAnchor, final Knot2 prev, final Knot2 next ) {

      prev.mirrorHandlesBackward();
      next.rearHandle.set(xNextControl, yNextControl);
      next.coord.set(xNextAnchor, yNextAnchor);

      return next;
   }

   /**
    * Sets two knots from a segment of a cubic curve which reflect an existing
    * segment.<br>
    * <br>
    * Assumes that the previous knot's coordinate is set to the first anchor
    * point. The previous knot's fore handle, the next knot's rear handle and
    * the next knot's coordinate are set by this function.
    *
    * @param nextControl the next control point
    * @param nextAnchor  the next anchor
    * @param prev        the previous knot
    * @param next        the next knot
    *
    * @return next knot
    */
   public static Knot2 fromSegCubicRefl ( final Vec2 nextControl,
      final Vec2 nextAnchor, final Knot2 prev, final Knot2 next ) {

      return Knot2.fromSegCubicRefl(nextControl.x, nextControl.y, nextAnchor.x,
         nextAnchor.y, prev, next);
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
    * @param prev        the previous knot
    * @param next        the next knot
    *
    * @return the next knot
    */
   public static Knot2 fromSegLinear ( final float xNextAnchor,
      final float yNextAnchor, final Knot2 prev, final Knot2 next ) {

      final Vec2 prevCoord = prev.coord;
      final Vec2 nextCoord = next.coord;

      nextCoord.set(xNextAnchor, yNextAnchor);

      prev.foreHandle.set(prevCoord.x * IUtils.TWO_THIRDS + nextCoord.x
         * IUtils.ONE_THIRD, prevCoord.y * IUtils.TWO_THIRDS + nextCoord.y
            * IUtils.ONE_THIRD);

      next.rearHandle.set(nextCoord.x * IUtils.TWO_THIRDS + prevCoord.x
         * IUtils.ONE_THIRD, nextCoord.y * IUtils.TWO_THIRDS + prevCoord.y
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
    * @see Knot2#fromSegLinear(float, float, Knot2, Knot2)
    */
   public static Knot2 fromSegLinear ( final Vec2 nextAnchor, final Knot2 prev,
      final Knot2 next ) {

      return Knot2.fromSegLinear(nextAnchor.x, nextAnchor.y, prev, next);
   }

   /**
    * Sets two knots from a segment of the quadratic curve. Assumes that the
    * previous knot's coordinate is set to the first anchor point.<br>
    * <br>
    * The previous knot's fore handle, the next knot's rear handle and the
    * next knot's coordinate are set by this function.
    *
    * @param xControl    the control point x
    * @param yControl    the control point y
    * @param xNextAnchor the next anchor x
    * @param yNextAnchor the next anchor y
    * @param prev        the previous knot
    * @param next        the next knot
    *
    * @return the next knot
    */
   public static Knot2 fromSegQuadratic ( final float xControl,
      final float yControl, final float xNextAnchor, final float yNextAnchor,
      final Knot2 prev, final Knot2 next ) {

      final Vec2 prevCo = prev.coord;

      /*
       * This doesn't use lerp13 because a calculation can be saved by
       * calculating the midpoint.
       */
      final float midpt23x = xControl * IUtils.TWO_THIRDS;
      final float midpt23y = yControl * IUtils.TWO_THIRDS;

      prev.foreHandle.set(midpt23x + IUtils.ONE_THIRD * prevCo.x, midpt23y
         + IUtils.ONE_THIRD * prevCo.y);

      next.rearHandle.set(midpt23x + IUtils.ONE_THIRD * xNextAnchor, midpt23y
         + IUtils.ONE_THIRD * yNextAnchor);

      next.coord.set(xNextAnchor, yNextAnchor);

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
    * @see Knot2#fromSegQuadratic(float, float, float, float, Knot2, Knot2)
    */
   public static Knot2 fromSegQuadratic ( final Vec2 control,
      final Vec2 nextAnchor, final Knot2 prev, final Knot2 next ) {

      return Knot2.fromSegQuadratic(control.x, control.y, nextAnchor.x,
         nextAnchor.y, prev, next);
   }

   /**
    * Gets the rear handle of a knot as a direction, rather than as a point.
    *
    * @param knot   the knot
    * @param target the output vector
    *
    * @return the rear handle vector
    *
    * @see Vec2#subNorm(Vec2, Vec2, Vec2)
    */
   public static Vec2 rearDir ( final Knot2 knot, final Vec2 target ) {

      return Vec2.subNorm(knot.rearHandle, knot.coord, target);
   }

   /**
    * Returns the magnitude of the knot's rear handle, i.e., the Euclidean
    * distance between the rear handle and the coordinate.
    *
    * @param knot the knot
    *
    * @return the magnitude
    *
    * @see Vec2#distEuclidean(Vec2, Vec2)
    */
   public static float rearMag ( final Knot2 knot ) {

      return Vec2.distEuclidean(knot.rearHandle, knot.coord);
   }

   /**
    * Gets the rear handle of a knot as a vector, rather than as a point.
    *
    * @param knot   the knot
    * @param target the output vector
    *
    * @return the rear handle vector
    *
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    */
   public static Vec2 rearVec ( final Knot2 knot, final Vec2 target ) {

      return Vec2.sub(knot.rearHandle, knot.coord, target);
   }

   /**
    * Smoothes the handles of a knot with reference to a previous and next
    * knot. A helper function to {@link Curve2#smoothHandles(Curve2)} .
    *
    * @param prev  the previous knot
    * @param curr  the current knot
    * @param next  the next knot
    * @param carry a temporary vector
    *
    * @return the current knot
    */
   public static Knot2 smoothHandles ( final Knot2 prev, final Knot2 curr,
      final Knot2 next, final Vec2 carry ) {

      final Vec2 coCurr = curr.coord;
      final Vec2 coPrev = prev.coord;
      final Vec2 coNext = next.coord;

      final float xRear = coPrev.x - coCurr.x;
      final float yRear = coPrev.y - coCurr.y;

      final float xFore = coNext.x - coCurr.x;
      final float yFore = coNext.y - coCurr.y;

      final float bmSq = xRear * xRear + yRear * yRear;
      final float bmInv = Utils.invSqrt(bmSq);

      final float fmSq = xFore * xFore + yFore * yFore;
      final float fmInv = Utils.invSqrt(fmSq);

      final float xDir = carry.x + xRear * bmInv - xFore * fmInv;
      final float yDir = carry.y + yRear * bmInv - yFore * fmInv;

      final float rescl = IUtils.ONE_THIRD * Utils.invSqrt(xDir * xDir + yDir
         * yDir);
      carry.x = xDir * rescl;
      carry.y = yDir * rescl;

      final float bMag = bmSq * bmInv;
      curr.rearHandle.set(coCurr.x + bMag * carry.x, coCurr.y + bMag * carry.y);

      final float fMag = fmSq * fmInv;
      curr.foreHandle.set(coCurr.x - fMag * carry.x, coCurr.y - fMag * carry.y);

      return curr;
   }

   /**
    * Smoothes the fore handle of the first knot in an open curve. A helper
    * function to {@link Curve2#smoothHandles(Curve2)} .
    *
    * @param curr  the current knot
    * @param next  the next knot
    * @param carry a temporary vector
    *
    * @return the current knot
    */
   public static Knot2 smoothHandlesFirst ( final Knot2 curr, final Knot2 next,
      final Vec2 carry ) {

      final Vec2 coCurr = curr.coord;
      final Vec2 coNext = next.coord;

      final float xRear = -coCurr.x;
      final float yRear = -coCurr.y;

      final float xFore = coNext.x + xRear;
      final float yFore = coNext.y + yRear;

      final float bmSq = xRear * xRear + yRear * yRear;
      final float bmInv = Utils.invSqrt(bmSq);

      final float fmSq = xFore * xFore + yFore * yFore;
      final float fmInv = Utils.invSqrt(fmSq);
      final float fMag = fmSq * fmInv;

      final float xDir = carry.x + xRear * bmInv - xFore * fmInv;
      final float yDir = carry.y + yRear * bmInv - yFore * fmInv;

      final float rescl = IUtils.ONE_THIRD * Utils.invSqrt(xDir * xDir + yDir
         * yDir);
      carry.x = xDir * rescl;
      carry.y = yDir * rescl;

      curr.foreHandle.set(coCurr.x - fMag * carry.x, coCurr.y - fMag * carry.y);

      return curr;
   }

   /**
    * Smoothes the rear handle of the last knot in an open curve. A helper
    * function to {@link Curve2#smoothHandles(Curve2)} .
    *
    * @param prev  the previous knot
    * @param curr  the current knot
    * @param carry a temporary vector
    *
    * @return the current knot
    */
   public static Knot2 smoothHandlesLast ( final Knot2 prev, final Knot2 curr,
      final Vec2 carry ) {

      final Vec2 coCurr = curr.coord;
      final Vec2 coPrev = prev.coord;

      final float xFore = -coCurr.x;
      final float yFore = -coCurr.y;

      final float xRear = coPrev.x + xFore;
      final float yRear = coPrev.y + yFore;

      final float bmSq = xRear * xRear + yRear * yRear;
      final float bmInv = Utils.invSqrt(bmSq);
      final float bMag = bmSq * bmInv;

      final float fmSq = xFore * xFore + yFore * yFore;
      final float fmInv = Utils.invSqrt(fmSq);

      final float xDir = carry.x + xRear * bmInv - xFore * fmInv;
      final float yDir = carry.y + yRear * bmInv - yFore * fmInv;

      final float rescl = IUtils.ONE_THIRD * Utils.invSqrt(xDir * xDir + yDir
         * yDir);
      carry.x = xDir * rescl;
      carry.y = yDir * rescl;

      curr.rearHandle.set(coCurr.x + bMag * carry.x, coCurr.y + bMag * carry.y);

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
    * @param xCenter   the x center
    * @param yCenter   the y center
    * @param target    the output knot
    *
    * @return the knot
    */
   static Knot2 fromPolar ( final float cosa, final float sina,
      final float radius, final float handleMag, final float xCenter,
      final float yCenter, final Knot2 target ) {

      final Vec2 coord = target.coord;
      coord.set(xCenter + radius * cosa, yCenter + radius * sina);

      final float hmsina = sina * handleMag;
      final float hmcosa = cosa * handleMag;

      target.foreHandle.set(coord.x - hmsina, coord.y + hmcosa);
      target.rearHandle.set(coord.x + hmsina, coord.y - hmcosa);

      return target;
   }

   /**
    * An abstract class to facilitate the creation of knot easing functions.
    */
   public abstract static class AbstrEasing implements Utils.EasingFuncObj <
      Knot2 > {

      /**
       * The default constructor.
       */
      protected AbstrEasing ( ) {}

      /**
       * A clamped interpolation between the origin and destination. Defers to
       * an unclamped interpolation, which is to be defined by sub-classes of
       * this class.
       *
       * @param orig   the origin knot
       * @param dest   the destination knot
       * @param step   a factor in [0.0, 1.0]
       * @param target the output knot
       *
       * @return the eased knot
       */
      @Override
      public Knot2 apply ( final Knot2 orig, final Knot2 dest, final Float step,
         final Knot2 target ) {

         final float tf = step;
         if ( Float.isNaN(tf) ) {
            return this.applyUnclamped(orig, dest, 0.5f, target);
         }
         if ( tf <= 0.0f ) { return target.set(orig); }
         if ( tf >= 1.0f ) { return target.set(dest); }
         return this.applyUnclamped(orig, dest, tf, target);
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
      public abstract Knot2 applyUnclamped ( final Knot2 origin,
         final Knot2 dest, final float step, final Knot2 target );

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
      public Lerp ( ) {}

      /**
       * Eases between two knots by a step using the formula (1.0 - t) * a + t *
       * b .
       *
       * @param origin the origin knot
       * @param dest   the destination knot
       * @param step   the step
       * @param target the output knot
       *
       * @return the eased knot
       */
      @Override
      public Knot2 applyUnclamped ( final Knot2 origin, final Knot2 dest,
         final float step, final Knot2 target ) {

         final float u = 1.0f - step;

         final Vec2 orCo = origin.coord;
         final Vec2 orFh = origin.foreHandle;
         final Vec2 orRh = origin.rearHandle;

         final Vec2 deCo = dest.coord;
         final Vec2 deFh = dest.foreHandle;
         final Vec2 deRh = dest.rearHandle;

         target.coord.set(u * orCo.x + step * deCo.x, u * orCo.y + step
            * deCo.y);
         target.foreHandle.set(u * orFh.x + step * deFh.x, u * orFh.y + step
            * deFh.y);
         target.rearHandle.set(u * orRh.x + step * deRh.x, u * orRh.y + step
            * deRh.y);

         return target;
      }

   }

}