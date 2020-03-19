package camzup.core;

/**
 * Organizes the vectors the shape a Bezier curve into a coordinate
 * (or anchor point), fore handle (the following control point) and
 * rear handle (the preceding control point).
 */
public class Knot2 implements Cloneable, Comparable < Knot2 > {

  /**
   * An abstract class to facilitate the creation of knot easing
   * functions.
   */
  public static abstract class AbstrEasing
      implements Utils.EasingFuncObj < Knot2 > {

    /**
     * The default constructor.
     */
    public AbstrEasing ( ) { super(); }

    /**
     * A clamped interpolation between the origin and destination. Defers
     * to an unclamped interpolation, which is to be defined by
     * sub-classes of this class.
     *
     * @param origin the origin knot
     * @param dest   the destination knot
     * @param step   a factor in [0.0, 1.0]
     * @param target the output knot
     * @return the eased knot
     */
    @Override
    public Knot2 apply (
        final Knot2 origin,
        final Knot2 dest,
        final Float step,
        final Knot2 target ) {

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
     * @return the eased knot
     */
    public abstract Knot2 applyUnclamped (
        final Knot2 origin,
        final Knot2 dest,
        final float step,
        final Knot2 target );

    /**
     * Returns the simple name of this class.
     *
     * @return the string
     */
    @Override
    public String toString ( ) {

      return this.getClass().getSimpleName();
    }
  }

  /**
   * A functional class to ease between two knots with linear
   * interpolation.
   */
  public static class Lerp extends AbstrEasing {

    /**
     * The default constructor.
     */
    public Lerp ( ) { super(); }

    /**
     * Eases between two knots by a step using the formula (1.0 - t) * a +
     * b .
     *
     * @param origin the origin knot
     * @param dest   the destination knot
     * @param step   the step
     * @param target the output knot
     * @return the eased knot
     */
    @Override
    public Knot2 applyUnclamped (
        final Knot2 origin,
        final Knot2 dest,
        final float step,
        final Knot2 target ) {

      final float u = 1.0f - step;

      final Vec2 orCo = origin.coord;
      final Vec2 orFh = origin.foreHandle;
      final Vec2 orRh = origin.rearHandle;

      final Vec2 deCo = dest.coord;
      final Vec2 deFh = dest.foreHandle;
      final Vec2 deRh = dest.rearHandle;

      target.coord.set(
          u * orCo.x + step * deCo.x,
          u * orCo.y + step * deCo.y);

      target.foreHandle.set(
          u * orFh.x + step * deFh.x,
          u * orFh.y + step * deFh.y);

      target.rearHandle.set(
          u * orRh.x + step * deRh.x,
          u * orRh.y + step * deRh.y);

      return target;
    }
  }

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
   * The handle which warps the curve segment heading towards the knot
   * along the direction of the curve.
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
  public Knot2 (
      final float xCoord,
      final float yCoord ) {

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
  public Knot2 (
      final float xCoord,
      final float yCoord,
      final float xFore,
      final float yFore ) {

    this.set(
        xCoord, yCoord,
        xFore, yFore);
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
  public Knot2 (
      final float xCoord,
      final float yCoord,
      final float xFore,
      final float yFore,
      final float xRear,
      final float yRear ) {

    this.set(
        xCoord, yCoord,
        xFore, yFore,
        xRear, yRear);
  }

  /**
   * Creates a knot from a source knot.
   *
   * @param source the source
   */
  public Knot2 ( final Knot2 source ) {

    this.set(source);
  }

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
  public Knot2 (
      final String xCoord,
      final String yCoord,
      final String xFore,
      final String yFore,
      final String xRear,
      final String yRear ) {

    this.set(
        xCoord, yCoord,
        xFore, yFore,
        xRear, yRear);
  }

  /**
   * Creates a knot from a coordinate.
   *
   * @param coord the coordinate
   */
  public Knot2 ( final Vec2 coord ) {

    this.set(coord);
  }

  /**
   * Creates a knot from a coordinate and fore handle. The rear handle
   * is a mirror of the fore.
   *
   * @param coord      the coordinate
   * @param foreHandle the fore handle
   */
  public Knot2 (
      final Vec2 coord,
      final Vec2 foreHandle ) {

    this.set(coord, foreHandle);
  }

  /**
   * Creates a knot from a series of vectors.
   *
   * @param coord      the coordinate
   * @param foreHandle the fore handle
   * @param rearHandle the rear handle
   */
  public Knot2 (
      final Vec2 coord,
      final Vec2 foreHandle,
      final Vec2 rearHandle ) {

    this.set(coord, foreHandle, rearHandle);
  }

  /**
   * Tests to see if this knot equals another.
   *
   * @param other the other knot
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
   * Returns a String of Python code targeted toward the Blender 2.8x
   * API. This code is brittle and is used for internal testing
   * purposes, i.e., to compare how curve geometry looks in Blender (the
   * control) versus in the library (the test).
   *
   * @return the string
   */
  @Experimental
  String toBlenderCode ( ) {

    return new StringBuilder(256)
        .append("{\"co\": ")
        .append(this.coord.toBlenderCode(0.0f))
        .append(", \"handle_right\": ")
        .append(this.foreHandle.toBlenderCode(0.0f))
        .append(", \"handle_left\": ")
        .append(this.rearHandle.toBlenderCode(0.0f))
        .append('}')
        .toString();
  }

  /**
   * Aligns this knot's handles in the same direction while preserving
   * their magnitude.
   *
   * @return this knot
   * @see Knot2#alignHandlesForward()
   */
  @Chainable
  public Knot2 alignHandles ( ) {

    return this.alignHandlesForward();
  }

  /**
   * Aligns this knot's fore handle to its rear handle while preserving
   * magnitude.
   *
   * @return this knot
   * @see Utils#hypot(float, float)
   * @see Utils#invHypot(float, float)
   */
  @Chainable
  public Knot2 alignHandlesBackward ( ) {

    final float cox = this.coord.x;
    final float coy = this.coord.y;

    final float rearDirx = this.rearHandle.x - cox;
    final float rearDiry = this.rearHandle.y - coy;

    final float foreDirx = this.foreHandle.x - cox;
    final float foreDiry = this.foreHandle.y - coy;

    final float flipRescale = -Utils.hypot(foreDirx, foreDiry)
        * Utils.invHypot(rearDirx, rearDiry);

    this.foreHandle.x = rearDirx * flipRescale + cox;
    this.foreHandle.y = rearDiry * flipRescale + coy;

    return this;
  }

  /**
   * Aligns this knot's rear handle to its fore handle while preserving
   * magnitude.
   *
   * @return this knot
   * @see Utils#hypot(float, float)
   * @see Utils#invHypot(float, float)
   */
  @Chainable
  public Knot2 alignHandlesForward ( ) {

    final float cox = this.coord.x;
    final float coy = this.coord.y;

    /*
     * The rear handle is a point in space. Subtract the coordinate from
     * the rear handle to create a rear handle direction relative to the
     * coordinate, which now serves as an origin, or pivot.
     */
    final float rearDirx = this.rearHandle.x - cox;
    final float rearDiry = this.rearHandle.y - coy;

    /*
     * Subtract the coordinate from the foreHandle to get the foreHandle
     * direction.
     */
    final float foreDirx = this.foreHandle.x - cox;
    final float foreDiry = this.foreHandle.y - coy;

    /*
     * Find the magnitude of the rear direction. Align the rear handle
     * with the fore by changing its direction while preserving its
     * magnitude. The negative sign indicates that the rear handle is 180
     * degrees opposite the forehandle.
     */
    final float flipRescale = -Utils.hypot(rearDirx, rearDiry)
        * Utils.invHypot(foreDirx, foreDiry);

    /*
     * Add the coordinate back to the new rear direction to convert it
     * from a direction to a point.
     */
    this.rearHandle.x = foreDirx * flipRescale + cox;
    this.rearHandle.y = foreDiry * flipRescale + coy;

    return this;
  }

  /**
   * Creates a new knot with the coordinate and handles of this knot.
   *
   * @return a new knot
   */
  @Override
  public Knot2 clone ( ) {

    return new Knot2(
        this.coord,
        this.foreHandle,
        this.rearHandle);
  }

  /**
   * Compares this knot to another based on a comparison between
   * coordinates.
   *
   * @param knot the other knot
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
   * @return the evaluation
   */
  @Override
  public boolean equals ( final Object obj ) {

    if ( this == obj ) { return true; }
    if ( obj == null ) { return false; }
    if ( this.getClass() != obj.getClass() ) { return false; }
    return this.equals((Knot2) obj);
  }

  /**
   * Returns the knot's hash code based on those of its three
   * constituent vectors.
   *
   * @return the hash code
   */
  @Override
  public int hashCode ( ) {

    return ((IUtils.MUL_BASE
        ^ (this.coord == null ? 0 : this.coord.hashCode()))
        * IUtils.HASH_MUL
        ^ (this.foreHandle == null ? 0 : this.foreHandle.hashCode()))
        * IUtils.HASH_MUL
        ^ (this.rearHandle == null ? 0 : this.rearHandle.hashCode());
  }

  /**
   * Mirrors this knot's handles. Defaults to mirroring in the forward
   * direction.
   *
   * @return this knot
   * @see Knot2#mirrorHandlesForward()
   */
  @Chainable
  public Knot2 mirrorHandles ( ) {

    return this.mirrorHandlesForward();
  }

  /**
   * Sets the forward-facing handle to mirror the rear-facing handle:
   * the fore will have the same magnitude and negated direction of the
   * rear.
   *
   * @return this knot
   */
  @Chainable
  public Knot2 mirrorHandlesBackward ( ) {

    this.foreHandle.set(
        this.coord.x - (this.rearHandle.x - this.coord.x),
        this.coord.y - (this.rearHandle.y - this.coord.y));

    return this;
  }

  /**
   * Sets the rear-facing handle to mirror the forward-facing handle:
   * the rear will have the same magnitude and negated direction of the
   * fore.
   *
   * @return this knot
   */
  @Chainable
  public Knot2 mirrorHandlesForward ( ) {

    this.rearHandle.set(
        this.coord.x - (this.foreHandle.x - this.coord.x),
        this.coord.y - (this.foreHandle.y - this.coord.y));

    return this;
  }

  /**
   * Reverses the knot's direction by swapping the fore- and
   * rear-handles.
   *
   * @return this knot
   */
  @Chainable
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
   * @return this knot
   */
  @Chainable
  public Knot2 rotateForeHandleZ ( final float radians ) {

    return this.rotateForeHandleZ(
        Utils.cos(radians),
        Utils.sin(radians));
  }

  /**
   * Rotates this knot's fore handle by the cosine and sine of an angle.
   *
   * @param cosa the cosine
   * @param sina the sine
   * @return this knot
   */
  @Chainable
  public Knot2 rotateForeHandleZ (
      final float cosa,
      final float sina ) {

    Vec2.sub(this.coord, this.foreHandle, this.foreHandle);
    Vec2.rotateZ(this.foreHandle, cosa, sina, this.foreHandle);
    Vec2.add(this.coord, this.foreHandle, this.foreHandle);

    return this;
  }

  /**
   * Rotates this knot's fore and rear handles by an angle in radians.
   *
   * @param radians the angle
   * @return this knot
   */
  @Chainable
  public Knot2 rotateHandlesZ ( final float radians ) {

    return this.rotateHandlesZ(
        Utils.cos(radians),
        Utils.sin(radians));
  }

  /**
   * Rotates this knot's fore and rear handles by the cosine and sine of
   * an angle.
   *
   * @param cosa the cosine
   * @param sina the sine
   * @return this knot
   */
  @Chainable
  public Knot2 rotateHandlesZ (
      final float cosa,
      final float sina ) {

    this.rotateForeHandleZ(cosa, sina);
    this.rotateRearHandleZ(cosa, sina);

    return this;
  }

  /**
   * Rotates this knot's rear handle by an angle in radians.
   *
   * @param radians the angle
   * @return this knot
   */
  @Chainable
  public Knot2 rotateRearHandleZ ( final float radians ) {

    return this.rotateForeHandleZ(
        Utils.cos(radians),
        Utils.sin(radians));
  }

  /**
   * Rotates this knot's rear handle by the cosine and sine of an angle.
   *
   * @param cosa the cosine
   * @param sina the sine
   * @return this knot
   */
  @Chainable
  public Knot2 rotateRearHandleZ (
      final float cosa,
      final float sina ) {

    Vec2.sub(this.coord, this.rearHandle, this.rearHandle);
    Vec2.rotateZ(this.rearHandle, cosa, sina, this.rearHandle);
    Vec2.add(this.coord, this.rearHandle, this.rearHandle);

    return this;
  }

  /**
   * Rotates this knot around the z axis by an angle in radians.
   *
   * @param radians the angle
   * @return this knot
   */
  @Chainable
  public Knot2 rotateZ ( final float radians ) {

    return this.rotateZ(
        Utils.cos(radians),
        Utils.sin(radians));
  }

  /**
   * Rotates a knot around the z axis. Accepts calculated sine and
   * cosine of an angle, so that collections of knots can be efficiently
   * rotated without repeatedly calling cos and sin.
   *
   * @param cosa cosine of the angle
   * @param sina sine of the angle
   * @return this knot
   */
  @Chainable
  public Knot2 rotateZ (
      final float cosa,
      final float sina ) {

    Vec2.rotateZ(this.coord, cosa, sina, this.coord);
    Vec2.rotateZ(this.foreHandle, cosa, sina, this.foreHandle);
    Vec2.rotateZ(this.rearHandle, cosa, sina, this.rearHandle);

    return this;
  }

  /**
   * Scales this knot by a factor.
   *
   * @param scale the factor
   * @return this knot
   */
  @Chainable
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
   * @return this knot
   */
  @Chainable
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
   * @return this knot
   */
  @Chainable
  public Knot2 scaleForeHandleBy ( final float scalar ) {

    /* fh = co + scalar * (fh - co) */
    this.foreHandle.x = this.coord.x
        + scalar * (this.foreHandle.x - this.coord.x);
    this.foreHandle.y = this.coord.y
        + scalar * (this.foreHandle.y - this.coord.y);

    return this;
  }

  /**
   * Scales the fore handle to a magnitude.
   *
   * @param magnitude the magnitude
   * @return this knot
   */
  @Chainable
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
   * @return this knot
   */
  @Chainable
  public Knot2 scaleHandlesBy ( final float scalar ) {

    this.scaleForeHandleBy(scalar);
    this.scaleRearHandleBy(scalar);

    return this;
  }

  /**
   * Scales both the fore and rear handle to a magnitude.
   *
   * @param magnitude the magnitude
   * @return this knot
   * @see Knot2#scaleForeHandleTo(float)
   * @see Knot2#scaleRearHandleTo(float)
   */
  @Chainable
  public Knot2 scaleHandlesTo ( final float magnitude ) {

    this.scaleForeHandleTo(magnitude);
    this.scaleRearHandleTo(magnitude);

    return this;
  }

  /**
   * Scales the rear handle by a factor.
   *
   * @param scalar the scalar
   * @return this knot
   */
  @Chainable
  public Knot2 scaleRearHandleBy ( final float scalar ) {

    /* rh = co + scalar * (rh - co) */
    this.rearHandle.x = this.coord.x
        + scalar * (this.rearHandle.x - this.coord.x);
    this.rearHandle.y = this.coord.y
        + scalar * (this.rearHandle.y - this.coord.y);

    return this;
  }

  /**
   * Scales the rear handle to a magnitude
   *
   * @param magnitude the magnitude
   * @return this knot
   * @see Vec2#subNorm(Vec2, Vec2, Vec2)
   * @see Vec2#mul(Vec2, float, Vec2)
   * @see Vec2#add(Vec2, Vec2, Vec2)
   */
  @Chainable
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
   * @return this knot
   * @see Math#copySign(float, float)
   */
  @Chainable
  public Knot2 set (
      final float xCoord,
      final float yCoord ) {

    final float xOff = Math.copySign(IUtils.DEFAULT_EPSILON, xCoord);
    final float yOff = Math.copySign(IUtils.DEFAULT_EPSILON, yCoord);

    return this.set(
        xCoord, yCoord,
        xCoord + xOff,
        yCoord + yOff,
        xCoord - xOff,
        yCoord - yOff);
  }

  /**
   * Sets the knot's coordinates and fore handle. The rear handle is a
   * mirror of the forehandle.
   *
   * @param xCoord the x coordinate
   * @param yCoord the y coordinate
   * @param xFore  the fore handle x
   * @param yFore  the fore handle y
   * @return the knot
   */
  @Chainable
  public Knot2 set (
      final float xCoord,
      final float yCoord,
      final float xFore,
      final float yFore ) {

    this.coord.set(xCoord, yCoord);
    this.foreHandle.set(xFore, yFore);
    this.rearHandle.set(
        xCoord - (xFore - xCoord),
        yCoord - (yFore - yCoord));

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
   * @return this knot
   */
  @Chainable
  public Knot2 set (
      final float xCoord,
      final float yCoord,
      final float xFore,
      final float yFore,
      final float xRear,
      final float yRear ) {

    this.coord.set(xCoord, yCoord);
    this.foreHandle.set(xFore, yFore);
    this.rearHandle.set(xRear, yRear);

    return this;
  }

  /**
   * Sets this knot from a source knot.
   *
   * @param source the source
   * @return this knot
   */
  @Chainable
  public Knot2 set ( final Knot2 source ) {

    return this.set(
        source.coord,
        source.foreHandle,
        source.rearHandle);
  }

  /**
   * Attempts to set the components of this knot from Strings using
   * {@link Float#parseFloat(String)} . If a NumberFormatException is
   * thrown, the component is set to zero.
   *
   * @param xCoord the x coordinate string
   * @param yCoord the y coordinate string
   * @param xFore  the x fore handle string
   * @param yFore  the y fore handle string
   * @param xRear  the x rear handle string
   * @param yRear  the y rear handle string
   * @return this knot
   */
  @Chainable
  public Knot2 set (
      final String xCoord,
      final String yCoord,
      final String xFore,
      final String yFore,
      final String xRear,
      final String yRear ) {

    this.coord.set(xCoord, yCoord);
    this.foreHandle.set(xFore, yFore);
    this.rearHandle.set(xRear, yRear);

    return this;
  }

  /**
   * Sets the coordinate, fore- and rear-handles to the input
   * coordinate.
   *
   * @param coord the coordinate
   * @return this knot
   */
  public Knot2 set ( final Vec2 coord ) {

    return this.set(coord.x, coord.y);
  }

  /**
   * Sets the knot's coordinates and fore handle. The rear handle is a
   * mirror of the forehandle.
   *
   * @param coord      the coordinate
   * @param foreHandle the fore handle
   * @return this knot
   */
  @Chainable
  public Knot2 set (
      final Vec2 coord,
      final Vec2 foreHandle ) {

    return this.set(
        coord.x,
        coord.y,
        foreHandle.x,
        foreHandle.y);
  }

  /**
   * Sets this knot from a series of vectors.
   *
   * @param coord      the coordinate
   * @param foreHandle the fore handle
   * @param rearHandle the rear handle
   * @return this knot
   */
  @Chainable
  public Knot2 set (
      final Vec2 coord,
      final Vec2 foreHandle,
      final Vec2 rearHandle ) {

    this.coord.set(coord);
    this.foreHandle.set(foreHandle);
    this.rearHandle.set(rearHandle);

    return this;
  }

  /**
   * Returns a 2D array representation of this knot. The coordinate is
   * the first element; fore handle, the second; rear handle, the third.
   *
   * @return the array
   * @see Vec2#toArray()
   */
  public float[][] toArray ( ) {

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
  public String toString ( ) {

    return this.toString(4);
  }

  /**
   * Returns a string representation of this knot.
   *
   * @param places the number of decimal places
   * @return the string
   */
  public String toString ( final int places ) {

    return new StringBuilder(256)
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
   * @param v the vector
   * @return this knot
   * @see Vec2#add(Vec2, Vec2, Vec2)
   */
  @Chainable
  public Knot2 translate ( final Vec2 v ) {

    Vec2.add(this.coord, v, this.coord);
    Vec2.add(this.foreHandle, v, this.foreHandle);
    Vec2.add(this.rearHandle, v, this.rearHandle);

    return this;
  }

  /**
   * Creates a knot from polar coordinates, where the knot's fore handle
   * is tangent to the radius.
   *
   * @param cosa      the cosine of the angle
   * @param sina      the sine of the angle
   * @param radius    the radius
   * @param handleMag the length of the handles
   * @param target    the output knot
   * @return the knot
   */
  public static Knot2 fromPolar (
      final float cosa,
      final float sina,
      final float radius,
      final float handleMag,
      final Knot2 target ) {

    final Vec2 coord = target.coord;
    coord.set(
        radius * cosa,
        radius * sina);

    final float hmsina = sina * handleMag;
    final float hmcosa = cosa * handleMag;

    target.foreHandle.set(
        coord.x - hmsina,
        coord.y + hmcosa);
    target.rearHandle.set(
        coord.x + hmsina,
        coord.y - hmcosa);

    return target;
  }

  /**
   * Creates a knot from polar coordinates, where the knot's fore handle
   * is tangent to the radius.
   *
   * @param angle     the angle in radians
   * @param radius    the radius
   * @param handleMag the length of the handles
   * @param target    the output knot
   * @return the knot
   */
  public static Knot2 fromPolar (
      final float angle,
      final float radius,
      final float handleMag,
      final Knot2 target ) {

    return Knot2.fromPolar(
        Utils.cos(angle),
        Utils.sin(angle),
        radius, handleMag, target);
  }

  /**
   * Mixes two knots together by a step in [0.0, 1.0] with the help of
   * an easing function.
   *
   * @param origin     the original knot
   * @param dest       the destination knot
   * @param step       the step
   * @param target     the output knot
   * @param easingFunc the easing function
   * @return the mix
   */
  public static Knot2 mix (
      final Knot2 origin,
      final Knot2 dest,
      final float step,
      final Knot2 target,
      final AbstrEasing easingFunc ) {

    return easingFunc.apply(origin, dest, step, target);
  }
}