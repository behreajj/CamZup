package camzup.core;

/**
 * Organizes the vectors the shape a Bezier curve into a coordinate (or
 * anchor point), fore handle (the following control point) and rear handle
 * (the preceding control point).
 */
public class Knot2 implements Cloneable, Comparable < Knot2 > {

   /**
    * The spatial coordinate of the knot.
    */
   public final Vec2 coord;

   /**
    * The handle which warps the curve segment heading away from the knot
    * along the direction of the curve.
    */
   public final Vec2 foreHandle;

   /**
    * The handle which warps the curve segment heading towards the knot along
    * the direction of the curve.
    */
   public final Vec2 rearHandle;

   {
      this.coord = new Vec2();
      this.foreHandle = new Vec2();
      this.rearHandle = new Vec2();
   }

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
    * Creates a knot from real numbers.
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
    * Attempts to create a knot from Strings.
    *
    * @param xCoord the x coordinate string
    * @param yCoord the y coordinate string
    * @param xFore  the x fore handle string
    * @param yFore  the y fore handle string
    * @param xRear  the x rear handle string
    * @param yRear  the y rear handle string
    */
   public Knot2 ( final String xCoord, final String yCoord, final String xFore,
      final String yFore, final String xRear, final String yRear ) {

      this.set(xCoord, yCoord, xFore, yFore, xRear, yRear);
   }

   /**
    * Creates a knot from a coordinate.
    *
    * @param coord the coordinate
    */
   public Knot2 ( final Vec2 coord ) { this.set(coord); }

   /**
    * Creates a knot from a coordinate and fore handle. The rear handle is a
    * mirror of the fore.
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

      final float cox = this.coord.x;
      final float coy = this.coord.y;

      final float rearDirx = this.rearHandle.x - cox;
      final float rearDiry = this.rearHandle.y - coy;

      final float rmsq = rearDirx * rearDirx + rearDiry * rearDiry;
      if ( rmsq > 0.0f ) {
         final float flipRescale = -Utils.hypot(this.foreHandle.x - cox,
            this.foreHandle.y - coy) * Utils.invSqrtUnchecked(rmsq);

         this.foreHandle.x = rearDirx * flipRescale + cox;
         this.foreHandle.y = rearDiry * flipRescale + coy;
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

      final float cox = this.coord.x;
      final float coy = this.coord.y;

      final float foreDirx = this.foreHandle.x - cox;
      final float foreDiry = this.foreHandle.y - coy;

      final float fmsq = foreDirx * foreDirx + foreDiry * foreDiry;
      if ( fmsq > 0.0f ) {
         final float flipRescale = -Utils.hypot(this.rearHandle.x - cox,
            this.rearHandle.y - coy) * Utils.invSqrtUnchecked(fmsq);

         this.rearHandle.x = foreDirx * flipRescale + cox;
         this.rearHandle.y = foreDiry * flipRescale + coy;
      }

      return this;
   }

   /**
    * Creates a new knot with the coordinate and handles of this knot.
    *
    * @return a new knot
    */
   @Override
   public Knot2 clone ( ) {

      return new Knot2(this.coord, this.foreHandle, this.rearHandle);
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
      if ( obj == null ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
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
    * @see Knot2#mirrorHandlesForward()
    */
   public Knot2 mirrorHandles ( ) { return this.mirrorHandlesForward(); }

   /**
    * Sets the forward-facing handle to mirror the rear-facing handle: the
    * fore will have the same magnitude and negated direction of the rear.
    *
    * @return this knot
    */
   public Knot2 mirrorHandlesBackward ( ) {

      this.foreHandle.set(this.coord.x - ( this.rearHandle.x - this.coord.x ),
         this.coord.y - ( this.rearHandle.y - this.coord.y ));

      return this;
   }

   /**
    * Sets the rear-facing handle to mirror the forward-facing handle: the
    * rear will have the same magnitude and negated direction of the fore.
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
    * Reverses the knot's direction by swapping the fore- and rear-handles.
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

      return this.rotateForeHandle(Utils.cos(radians), Utils.sin(radians));
   }

   /**
    * Rotates this knot's fore handle by the cosine and sine of an angle.
    *
    * @param cosa the cosine
    * @param sina the sine
    *
    * @return this knot
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
    */
   public Knot2 rotateHandles ( final float radians ) {

      return this.rotateHandles(Utils.cos(radians), Utils.sin(radians));
   }

   /**
    * Rotates this knot's fore and rear handles by the cosine and sine of an
    * angle.
    *
    * @param cosa the cosine
    * @param sina the sine
    *
    * @return this knot
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
    */
   public Knot2 rotateRearHandle ( final float radians ) {

      return this.rotateRearHandle(Utils.cos(radians), Utils.sin(radians));
   }

   /**
    * Rotates this knot's rear handle by the cosine and sine of an angle.
    *
    * @param cosa the cosine
    * @param sina the sine
    *
    * @return this knot
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
    */
   public Knot2 rotateZ ( final float radians ) {

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
    */
   public Knot2 scale ( final float scale ) {

      Vec2.mul(this.coord, scale, this.coord);
      Vec2.mul(this.foreHandle, scale, this.foreHandle);
      Vec2.mul(this.rearHandle, scale, this.rearHandle);

      return this;
   }

   /**
    * Scales this knot by a non-uniform scalar.
    *
    * @param scale the non-uniform scalar
    *
    * @return this knot
    */
   public Knot2 scale ( final Vec2 scale ) {

      Vec2.mul(this.coord, scale, this.coord);
      Vec2.mul(this.foreHandle, scale, this.foreHandle);
      Vec2.mul(this.rearHandle, scale, this.rearHandle);

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
    * @see Vec2#subNorm(Vec2, Vec2, Vec2)
    * @see Vec2#mul(Vec2, float, Vec2)
    * @see Vec2#add(Vec2, Vec2, Vec2)
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
    * @see Math#copySign(float, float)
    */
   public Knot2 set ( final float xCoord, final float yCoord ) {

      final float xOff = Utils.copySign(IUtils.DEFAULT_EPSILON, xCoord);
      final float yOff = Utils.copySign(IUtils.DEFAULT_EPSILON, yCoord);

      return this.set(xCoord, yCoord, xCoord + xOff, yCoord + yOff, xCoord
         - xOff, yCoord - yOff);
   }

   /**
    * Sets the knot's coordinates and fore handle. The rear handle is a mirror
    * of the forehandle.
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
    * Sets the knot's coordinate, forehandle and rearhandle by component.
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
    * Attempts to set the components of this knot from Strings using
    * {@link Float#parseFloat(String)} . If a NumberFormatException is thrown,
    * the component is set to zero.
    *
    * @param xCoord the x coordinate string
    * @param yCoord the y coordinate string
    * @param xFore  the x fore handle string
    * @param yFore  the y fore handle string
    * @param xRear  the x rear handle string
    * @param yRear  the y rear handle string
    *
    * @return this knot
    */
   public Knot2 set ( final String xCoord, final String yCoord,
      final String xFore, final String yFore, final String xRear,
      final String yRear ) {

      this.coord.set(xCoord, yCoord);
      this.foreHandle.set(xFore, yFore);
      this.rearHandle.set(xRear, yRear);

      return this;
   }

   /**
    * Sets the coordinate, fore- and rear-handles to the input coordinate.
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
    * of the forehandle.
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
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of this knot.
    *
    * @param places the number of decimal places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(256);
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
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes, i.e., to
    * compare how curve geometry looks in Blender (the control) versus in the
    * library (the test).
    *
    * @param z the z offset
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode ( final float z ) {

      final StringBuilder pyCd = new StringBuilder(256);
      pyCd.append("{\"co\": ");
      pyCd.append(this.coord.toBlenderCode(z));
      pyCd.append(", \"handle_right\": ");
      pyCd.append(this.foreHandle.toBlenderCode(z));
      pyCd.append(", \"handle_left\": ");
      pyCd.append(this.rearHandle.toBlenderCode(z));
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
   protected boolean equals ( final Knot2 other ) {

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
    */
   public static Vec2 foreVec ( final Knot2 knot, final Vec2 target ) {

      return Vec2.sub(knot.foreHandle, knot.coord, target);
   }

   /**
    * Sets two knots from a segment of the cubic curve. Assumes that the
    * previous knot's coordinate is set to the first anchor point.<br>
    * <br>
    * The previous knot's fore handle, the next knot's rear handle and the
    * next knot's coordinate are set by this function.
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
    * Sets two knots from a segment of the cubic curve. Assumes that the
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
    */
   public static Knot2 fromSegCubic ( final Vec2 prevControl,
      final Vec2 nextControl, final Vec2 nextAnchor, final Knot2 prev,
      final Knot2 next ) {

      return Knot2.fromSegCubic(prevControl.x, prevControl.y, nextControl.x,
         nextControl.y, nextAnchor.x, nextAnchor.y, prev, next);
   }

   /**
    * Sets a knot from line segment. Assumes that the previous knot's
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

      // Vec2.mix(prev.coord, next.coord, IUtils.ONE_THIRD, prev.foreHandle);
      // Vec2.mix(next.coord, prev.coord, IUtils.ONE_THIRD, next.rearHandle);

      final Vec2 prevCoord = prev.coord;
      final Vec2 nextCoord = next.coord;

      nextCoord.set(xNextAnchor, yNextAnchor);

      prev.foreHandle.set(prevCoord.x * IUtils.ONE_THIRD + nextCoord.x
         * IUtils.TWO_THIRDS, prevCoord.y * IUtils.ONE_THIRD + nextCoord.y
            * IUtils.TWO_THIRDS);

      next.rearHandle.set(nextCoord.x * IUtils.ONE_THIRD + prevCoord.x
         * IUtils.TWO_THIRDS, nextCoord.y * IUtils.ONE_THIRD + prevCoord.y
            * IUtils.TWO_THIRDS);

      return next;
   }

   /**
    * Sets a knot from line segment. Assumes that the previous knot's
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
    * Sets two knots from a segment of the quadratic curve. Assumes that the
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
    */
   public static Knot2 fromSegQuadratic ( final Vec2 control,
      final Vec2 nextAnchor, final Knot2 prev, final Knot2 next ) {

      return Knot2.fromSegQuadratic(control.x, control.y, nextAnchor.x,
         nextAnchor.y, prev, next);
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
   public static Knot2 mix ( final Knot2 origin, final Knot2 dest,
      final float step, final Knot2 target, final AbstrEasing easingFunc ) {

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

      final float backx = coPrev.x - coCurr.x;
      final float backy = coPrev.y - coCurr.y;

      final float forex = coNext.x - coCurr.x;
      final float forey = coNext.y - coCurr.y;

      final float bmSq = backx * backx + backy * backy;
      final float bmInv = Utils.invSqrt(bmSq);

      final float fmSq = forex * forex + forey * forey;
      final float fmInv = Utils.invSqrt(fmSq);

      final float dirx = carry.x + backx * bmInv - forex * fmInv;
      final float diry = carry.y + backy * bmInv - forey * fmInv;

      final float rescl = IUtils.ONE_THIRD * Utils.invSqrt(dirx * dirx + diry
         * diry);
      carry.x = dirx * rescl;
      carry.y = diry * rescl;

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

      final float backx = -coCurr.x;
      final float backy = -coCurr.y;

      final float forex = coNext.x + backx;
      final float forey = coNext.y + backy;

      final float bmSq = backx * backx + backy * backy;
      final float bmInv = Utils.invSqrt(bmSq);

      final float fmSq = forex * forex + forey * forey;
      final float fmInv = Utils.invSqrt(fmSq);
      final float fMag = fmSq * fmInv;

      final float dirx = carry.x + backx * bmInv - forex * fmInv;
      final float diry = carry.y + backy * bmInv - forey * fmInv;

      final float rescl = IUtils.ONE_THIRD * Utils.invSqrt(dirx * dirx + diry
         * diry);
      carry.x = dirx * rescl;
      carry.y = diry * rescl;

      // Should this just be a mirror of the fore handle instead?
      // final float bMag = bmSq * bmInv;
      // currKnot.rearHandle.set(
      // coCurr.x + bMag * dir.x,
      // coCurr.y + bMag * dir.y);

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

      final float forex = -coCurr.x;
      final float forey = -coCurr.y;

      final float backx = coPrev.x + forex;
      final float backy = coPrev.y + forey;

      final float bmSq = backx * backx + backy * backy;
      final float bmInv = Utils.invSqrt(bmSq);
      final float bMag = bmSq * bmInv;

      final float fmSq = forex * forex + forey * forey;
      final float fmInv = Utils.invSqrt(fmSq);

      final float dirx = carry.x + backx * bmInv - forex * fmInv;
      final float diry = carry.y + backy * bmInv - forey * fmInv;

      final float rescl = IUtils.ONE_THIRD * Utils.invSqrt(dirx * dirx + diry
         * diry);
      carry.x = dirx * rescl;
      carry.y = diry * rescl;

      curr.rearHandle.set(coCurr.x + bMag * carry.x, coCurr.y + bMag * carry.y);

      // Should this just be a mirror of the rear handle instead?
      // final float fMag = fmSq * fmInv;
      // currKnot.foreHandle.set(
      // coCurr.x - fMag * dir.x,
      // coCurr.y - fMag * dir.y);

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
   public static abstract class AbstrEasing implements Utils.EasingFuncObj <
      Knot2 > {

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
      public Knot2 apply ( final Knot2 origin, final Knot2 dest,
         final Float step, final Knot2 target ) {

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
      public Lerp ( ) { super(); }

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