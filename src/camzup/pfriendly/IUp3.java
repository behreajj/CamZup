package camzup.pfriendly;

import camzup.core.Ray3;
import camzup.core.Utils;
import camzup.core.Vec3;
import processing.core.PApplet;

/**
 * Maintains consistent behavior across 3D renderers that
 * extend different branches of PGraphics.
 */
public interface IUp3 extends IUp {

   /**
    * Factor by which a grid's count is scaled when dimensions
    * are not supplied.
    */
   public final float GRID_FAC = 32.0f;

   /**
    * Gets a mouse within a unit square, where either component
    * may be in the range [-1.0, 1.0]. The mouse's y coordinate
    * is flipped and assigned to the vector's y component.
    * (This is not a normalized vector.)
    *
    * @param parent
    *           the parent applet
    * @param renderer
    *           the renderer
    * @param target
    *           the output vector
    * @return the mouse
    */
   public static Vec3 mouse1 (
         final PApplet parent,
         final IUp3 renderer,
         final Vec3 target ) {

      final float mx = Utils.clamp01(
            parent.mouseX / (float) parent.width);
      final float my = Utils.clamp01(
            parent.mouseY / (float) parent.height);
      return target.set(
            mx + mx - 1.0f,
            1.0f - (my + my),
            0.0f);
   }

   /**
    * Draws a cubic Bezier curve between two anchor points,
    * where the control points shape the curve.
    *
    * @param ap0
    *           the first anchor point
    * @param cp0
    *           the first control point
    * @param cp1
    *           the second control point
    * @param ap1
    *           the second anchor point
    */
   public void bezier (
         final Vec3 ap0,
         final Vec3 cp0,
         final Vec3 cp1,
         final Vec3 ap1 );

   /**
    * Draws a cubic Bezier curve segment to the next anchor
    * point; the first and second control point shape the curve
    * segment.
    *
    * @param cp0
    *           the first control point
    * @param cp1
    *           the second control point
    * @param ap1
    *           the next anchor point
    */
   public void bezierVertex (
         final Vec3 cp0,
         final Vec3 cp1,
         final Vec3 ap1 );

   /**
    * Gets the renderer's camera location.
    *
    * @param target
    *           the output vector
    * @return the location
    */
   public Vec3 getLoc ( Vec3 target );

   /**
    * Gets the renderer's camera location on the x axis.
    *
    * @return the camera x
    */
   public float getLocX ();

   /**
    * Gets the renderer's camera location on the y axis.
    *
    * @return the camera y
    */
   public float getLocY ();

   /**
    * Gets the renderer's camera location on the z axis.
    *
    * @return the camera z
    */
   public float getLocZ ();

   /**
    * Sets a rendering hint in the OpenGL renderer.
    *
    * @param code
    *           the hint code
    */
   public void hint ( final int code );

   /**
    * Draws a line between two coordinates.
    *
    * @param ax
    *           the origin x coordinate
    * @param ay
    *           the origin y coordinate
    * @param az
    *           the origin z coordinate
    * @param bx
    *           the destination x coordinate
    * @param by
    *           the destination y coordinate
    * @param bz
    *           the destination z coordinate
    */
   public void line (
         final float ax,
         final float ay,
         final float az,
         final float bx,
         final float by,
         final float bz );

   /**
    * Draws a line between two coordinates.
    *
    * @param a
    *           the origin coordinate
    * @param b
    *           the destination coordinate
    */
   public void line ( final Vec3 a, final Vec3 b );

   /**
    * Gets a mouse within a unit square, where either component
    * may be in the range [-1.0, 1.0]. the mouse's y component
    * is assigned to the vector's y component. (This is not a
    * normalized vector.)
    *
    * @param target
    *           the output vector
    * @return the mouse
    */
   public default Vec3 mouse1 (
         final Vec3 target ) {

      return IUp3.mouse1(this.getParent(), this, target);
   }

   /**
    * Draws a 3D point.
    *
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    * @param z
    *           the z coordinate
    */
   public void point (
         final float x,
         final float y,
         final float z );

   /**
    * Draws a point at a given coordinate
    *
    * @param v
    *           the coordinate
    */
   public default void point ( final Vec3 v ) {

      this.point(v.x, v.y, v.z);
   }

   /**
    * Pop the last style off the end of the stack.
    */
   public void popStyle ();

   /**
    * Push a style onto the end of the stack.
    */
   public void pushStyle ();

   /**
    * Draws a quadratic Bezier curve segment to the next anchor
    * point; the control point shapes the curve segment.
    *
    * @param cp
    *           the control point
    * @param ap1
    *           the next anchor point
    */
   public void quadraticVertex (
         final Vec3 cp,
         final Vec3 ap1 );

   public default void ray (
         final float xOrigin,
         final float yOrigin,
         final float zOrigin,

         final float xDir,
         final float yDir,
         final float zDir,

         final float dLen ) {

      this.ray(
            xOrigin, yOrigin, zOrigin,
            xDir, yDir, zDir,
            dLen, 1.0f, 4.0f, 2.0f);
   }

   public default void ray (
         final float xOrigin,
         final float yOrigin,
         final float zOrigin,

         final float xDir,
         final float yDir,
         final float zDir,

         final float dLen,
         final float lnwgt,
         final float oWeight,
         final float dWeight ) {

      final float mSq = xDir * xDir +
            yDir * yDir +
            zDir * zDir;

      this.pushStyle();
      this.strokeWeight(oWeight);
      this.point(xOrigin, yOrigin, zOrigin);
      if (mSq != 0.0f) {
         this.strokeWeight(lnwgt);

         float dx = 0.0f;
         float dy = 0.0f;
         float dz = 0.0f;

         if (Utils.approxFast(mSq, 1.0f, 0.0001f)) {
            dx = xOrigin + xDir * dLen;
            dy = yOrigin + yDir * dLen;
            dz = zOrigin + zDir * dLen;
            this.line(
                  xOrigin, yOrigin, zOrigin,
                  dx, dy, dz);
         } else {
            final float mInv = dLen / (float) Math.sqrt(mSq);
            dx = xOrigin + xDir * mInv;
            dy = yOrigin + yDir * mInv;
            dz = zOrigin + zDir * mInv;
            this.line(
                  xOrigin, yOrigin, zOrigin,
                  dx, dy, dz);
         }
         this.strokeWeight(dWeight);
         this.point(dx, dy, dz);
      }
      this.popStyle();
   }

   public default void ray (
         final Ray3 ray,
         final float dLen ) {

      final Vec3 origin = ray.origin;
      final Vec3 dir = ray.dir;
      this.ray(
            origin.x, origin.y, origin.z,
            dir.x, dir.y, dir.z,
            dLen);
   }

   public default void ray (
         final Ray3 ray,
         final float dLen,
         final float lnwgt,
         final float oWeight,
         final float dWeight ) {

      final Vec3 origin = ray.origin;
      final Vec3 dir = ray.dir;
      this.ray(
            origin.x, origin.y, origin.z,
            dir.x, dir.y, dir.z,
            dLen, lnwgt, oWeight, dWeight);
   }

   /**
    * Sets the renderer's stroke color.
    *
    * @param c
    *           the hexadecimal color
    */
   @Override
   public void stroke ( final int c );

   /**
    * Sets the renderer's stroke weight.
    *
    * @param sw
    *           the stroke weight
    */
   public void strokeWeight ( final float sw );

   /**
    * Adds another vertex to a shape between the beginShape and
    * endShape commands.
    *
    * @param v
    *           the coordinate
    */
   public void vertex ( final Vec3 v );
}
