package camzup.core;

import java.util.Random;

/**
 * Organizes the quaternions that shape a Bezier curve on the surface of a
 * sphere into a sphere coordinate (or anchor point), fore handle (the
 * following control point) and rear handle (the preceding control point).
 */
@Experimental
public class KnotSphere implements Comparable < KnotSphere > {

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
   public KnotSphere ( ) {}

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
   public KnotSphere ( final float wco, final float xco, final float yco,
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
   public KnotSphere ( final KnotSphere source ) { this.set(source); }

   /**
    * Creates a knot from a series of quaternions.
    *
    * @param coord      the coordinate
    * @param foreHandle the fore handle
    * @param rearHandle the rear handle
    */
   public KnotSphere ( final Quaternion coord, final Quaternion foreHandle,
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
   public int compareTo ( final KnotSphere knot ) {

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
    * Rotates a knot around the x axis.
    *
    * @param radians the angle
    *
    * @return this knot
    *
    * @see KnotSphere#rotateX(float, float)
    */
   public KnotSphere rotateX ( final float radians ) {

      final float halfRad = radians * 0.5f;
      return this.rotateX(Utils.cos(halfRad), Utils.sin(halfRad));
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
   public KnotSphere rotateX ( final float cosa, final float sina ) {

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
    * @see KnotSphere#rotateY(float, float)
    */
   public KnotSphere rotateY ( final float radians ) {

      final float halfRad = radians * 0.5f;
      return this.rotateY(Utils.cos(halfRad), Utils.sin(halfRad));
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
   public KnotSphere rotateY ( final float cosa, final float sina ) {

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
    * @see KnotSphere#rotateZ(float, float)
    */
   public KnotSphere rotateZ ( final float radians ) {

      final float halfRad = radians * 0.5f;
      return this.rotateZ(Utils.cos(halfRad), Utils.sin(halfRad));
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
   public KnotSphere rotateZ ( final float cosa, final float sina ) {

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
   public KnotSphere set ( final float wco, final float xco, final float yco,
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
   public KnotSphere set ( final KnotSphere source ) {

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
   public KnotSphere set ( final Quaternion coord, final Quaternion foreHandle,
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
   protected boolean equals ( final KnotSphere other ) {

      return this.coord.equals(other.coord) && this.foreHandle.equals(
         other.foreHandle) && this.rearHandle.equals(other.rearHandle);
   }

   /**
    * Creates a random knot by calling
    * {@link Quaternion#random(java.util.Random, Quaternion)} for each of its
    * components.
    *
    * @param rng    the random number generator
    * @param target the output knot
    *
    * @return the knot
    */
   public static KnotSphere random ( final Random rng,
      final KnotSphere target ) {

      Quaternion.random(rng, target.coord);
      Quaternion.random(rng, target.foreHandle);
      Quaternion.random(rng, target.rearHandle);

      return target;
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
   public static Quaternion squad ( final KnotSphere a, final KnotSphere b,
      final float step, final Quaternion target ) {

      return Quaternion.squad(a.coord, a.foreHandle, b.rearHandle, b.coord,
         step, target);
   }

}
