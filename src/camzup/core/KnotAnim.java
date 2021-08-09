package camzup.core;

/**
 * Organizes the quaternions that shape a Bezier curve on the surface of a
 * sphere into a sphere coordinate (or anchor point), fore handle (the
 * following control point) and rear handle (the preceding control point).
 */
@Experimental
public class KnotAnim implements Comparable < KnotAnim > {

   /**
    * The orientation of the knot, serving as a coordinate on a sphere.
    */
   public final Quaternion coord = new Quaternion();

   /**
    * The handle which warps the curve segment heading away from the knot
    * along the direction of the curve.
    */
   public final Quaternion foreHandle = new Quaternion();

   /**
    * The handle which warps the curve segment heading towards the knot along
    * the direction of the curve.
    */
   public final Quaternion rearHandle = new Quaternion();

   /**
    * The default constructor.
    */
   public KnotAnim ( ) {}

   /**
    * Sets this knot from a series of floats.
    *
    * @param wco the coordinate w
    * @param xco the coordinate x
    * @param yco the coordinate y
    * @param zco the coordinate z
    * @param wfh the fore handle w
    * @param xfh the fore handle x
    * @param yfh the fore handle y
    * @param zfh the fore handle z
    * @param wrh the rear handle w
    * @param xrh the rear handle x
    * @param yrh the rear handle y
    * @param zrh the rear handle z
    */
   public KnotAnim ( final float wco, final float xco, final float yco,
      final float zco, final float wfh, final float xfh, final float yfh,
      final float zfh, final float wrh, final float xrh, final float yrh,
      final float zrh ) {

      this.set(wco, xco, yco, zco, wfh, xfh, yfh, zfh, wrh, xrh, yrh, zrh);
   }

   /**
    * Creates a new knot from a source.
    *
    * @param source the source
    */
   public KnotAnim ( final KnotAnim source ) { this.set(source); }

   /**
    * Creates a knot from a series of quaternions.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    * @param rearHandle the rear handle
    */
   public KnotAnim ( final Quaternion coord, final Quaternion foreHandle,
      final Quaternion rearHandle ) {

      this.set(coord, foreHandle, rearHandle);
   }

   /**
    * Compares this knot to another based on a comparison between coordinates.
    *
    * @param knot the other knot
    *
    * @return the evaluation
    */
   @Override
   public int compareTo ( final KnotAnim knot ) {

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
      return this.equals(obj);
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
    * @see KnotAnim#mirrorHandlesForward()
    */
   public KnotAnim mirrorHandles ( ) { return this.mirrorHandlesForward(); }

   /**
    * Sets the forward-facing handle to mirror the rear-facing handle: the
    * fore will have the same magnitude and negated direction of the rear.
    *
    * @return this knot
    */
   public KnotAnim mirrorHandlesBackward ( ) {

      final Vec3 coi = this.coord.imag;
      final Vec3 rhi = this.rearHandle.imag;
      this.foreHandle.set(this.coord.real - ( this.rearHandle.real
         - this.coord.real ), coi.x - ( rhi.x - coi.x ), coi.y - ( rhi.y
            - coi.y ), coi.z - ( rhi.z - coi.z ));

      return this;
   }

   /**
    * Sets the rear-facing handle to mirror the forward-facing handle: the
    * rear will have the same magnitude and negated direction of the fore.
    *
    * @return this knot
    */
   public KnotAnim mirrorHandlesForward ( ) {

      final Vec3 coi = this.coord.imag;
      final Vec3 fhi = this.foreHandle.imag;
      this.rearHandle.set(this.coord.real - ( this.foreHandle.real
         - this.coord.real ), coi.x - ( fhi.x - coi.x ), coi.y - ( fhi.y
            - coi.y ), coi.z - ( fhi.z - coi.z ));

      return this;
   }

   /**
    * Reverses the knot's direction by swapping the fore- and rear-handles.
    *
    * @return this knot
    */
   public KnotAnim reverse ( ) {

      final Vec3 fi = this.foreHandle.imag;
      final float tw = this.foreHandle.real;
      final float tx = fi.x;
      final float ty = fi.y;
      final float tz = fi.z;
      this.foreHandle.set(this.rearHandle);
      this.rearHandle.set(tw, tx, ty, tz);

      return this;
   }

   /**
    * Rotates a knot around the x axis.
    *
    * @param radians the angle
    *
    * @return this knot
    *
    * @see KnotAnim#rotateX(float, float)
    * @see Utils#modRadians(float)
    */
   public KnotAnim rotateX ( final float radians ) {

      final double halfRad = Utils.modRadians(radians) * 0.5d;
      final float cosa = ( float ) Math.cos(halfRad);
      final float sina = ( float ) Math.sin(halfRad);
      return this.rotateX(cosa, sina);
   }

   /**
    * Rotates this knot around the x axis. Accepts calculated sine and cosine
    * of half the angle, so that collections of knots can be efficiently
    * rotated without repeatedly calling cos and sin.
    *
    * @param cosa cosine of half the angle
    * @param sina sine of half the angle
    *
    * @return this knot
    *
    * @see Quaternion#rotateX(Quaternion, float, float, Quaternion)
    */
   public KnotAnim rotateX ( final float cosa, final float sina ) {

      Quaternion.rotateX(this.coord, cosa, sina, this.coord);
      Quaternion.rotateX(this.foreHandle, cosa, sina, this.foreHandle);
      Quaternion.rotateX(this.rearHandle, cosa, sina, this.rearHandle);

      return this;
   }

   /**
    * Rotates this knot around the y axis.
    *
    * @param radians the angle
    *
    * @return this knot
    *
    * @see KnotAnim#rotateY(float, float)
    * @see Utils#modRadians(float)
    */
   public KnotAnim rotateY ( final float radians ) {

      final double halfRad = Utils.modRadians(radians) * 0.5d;
      final float cosa = ( float ) Math.cos(halfRad);
      final float sina = ( float ) Math.sin(halfRad);
      return this.rotateY(cosa, sina);
   }

   /**
    * Rotates this knot around the y axis. Accepts calculated sine and cosine
    * of half the angle, so that collections of knots can be efficiently
    * rotated without repeatedly calling cos and sin.
    *
    * @param cosa cosine of half the angle
    * @param sina sine of half the angle
    *
    * @return this knot
    *
    * @see Quaternion#rotateY(Quaternion, float, float, Quaternion)
    */
   public KnotAnim rotateY ( final float cosa, final float sina ) {

      Quaternion.rotateY(this.coord, cosa, sina, this.coord);
      Quaternion.rotateY(this.foreHandle, cosa, sina, this.foreHandle);
      Quaternion.rotateY(this.rearHandle, cosa, sina, this.rearHandle);

      return this;
   }

   /**
    * Rotates a knot around the z axis.
    *
    * @param radians the angle
    *
    * @return this knot
    *
    * @see KnotAnim#rotateZ(float, float)
    * @see Utils#modRadians(float)
    */
   public KnotAnim rotateZ ( final float radians ) {

      final double halfRad = Utils.modRadians(radians) * 0.5d;
      final float cosa = ( float ) Math.cos(halfRad);
      final float sina = ( float ) Math.sin(halfRad);
      return this.rotateZ(cosa, sina);
   }

   /**
    * Rotates this knot around the z axis. Accepts calculated sine and cosine
    * of half the angle, so that collections of knots can be efficiently
    * rotated without repeatedly calling cos and sin.
    *
    * @param cosa cosine of half the angle
    * @param sina sine of half the angle
    *
    * @return this knot
    *
    * @see Quaternion#rotateZ(Quaternion, float, float, Quaternion)
    */
   public KnotAnim rotateZ ( final float cosa, final float sina ) {

      Quaternion.rotateZ(this.coord, cosa, sina, this.coord);
      Quaternion.rotateZ(this.foreHandle, cosa, sina, this.foreHandle);
      Quaternion.rotateZ(this.rearHandle, cosa, sina, this.rearHandle);

      return this;
   }

   /**
    * Sets this knot from a series of floats.
    *
    * @param wco the coordinate w
    * @param xco the coordinate x
    * @param yco the coordinate y
    * @param zco the coordinate z
    * @param wfh the fore handle w
    * @param xfh the fore handle x
    * @param yfh the fore handle y
    * @param zfh the fore handle z
    * @param wrh the rear handle w
    * @param xrh the rear handle x
    * @param yrh the rear handle y
    * @param zrh the rear handle z
    *
    * @return this knot
    */
   public KnotAnim set ( final float wco, final float xco, final float yco,
      final float zco, final float wfh, final float xfh, final float yfh,
      final float zfh, final float wrh, final float xrh, final float yrh,
      final float zrh ) {

      this.coord.set(wco, xco, yco, zco);
      this.foreHandle.set(wfh, xfh, yfh, zfh);
      this.rearHandle.set(wrh, xrh, yrh, zrh);

      return this;
   }

   /**
    * Sets this knot from a source knot.
    *
    * @param source the source
    *
    * @return this knot
    */
   public KnotAnim set ( final KnotAnim source ) {

      return this.set(source.coord, source.foreHandle, source.rearHandle);
   }

   /**
    * Sets this knot from a series of quaternions.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    * @param rearHandle the rear handle
    *
    * @return this knot
    */
   public KnotAnim set ( final Quaternion coord, final Quaternion foreHandle,
      final Quaternion rearHandle ) {

      this.coord.set(coord);
      this.foreHandle.set(foreHandle);
      this.rearHandle.set(rearHandle);

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
   protected boolean equals ( final KnotAnim other ) {

      return this.coord.equals(other.coord) && this.foreHandle.equals(
         other.foreHandle) && this.rearHandle.equals(other.rearHandle);
   }

   /**
    * Returns a rotation (or a coordinate on the surface of a sphere), given
    * two knots and a step.
    *
    * @param a      the origin knot
    * @param b      the destination knot
    * @param step   the step
    * @param target the orientation
    *
    * @return the orientation
    */
   public static Quaternion squad ( final KnotAnim a, final KnotAnim b,
      final float step, final Quaternion target ) {

      return Quaternion.squad(a.coord, a.foreHandle, b.rearHandle, b.coord,
         step, target);
   }

}
