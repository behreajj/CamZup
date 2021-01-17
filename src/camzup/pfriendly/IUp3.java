package camzup.pfriendly;

import camzup.core.Bounds3;
import camzup.core.Handedness;
import camzup.core.Octree;
import camzup.core.Ray3;
import camzup.core.Recursive;
import camzup.core.Utils;
import camzup.core.Vec3;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * Maintains consistent behavior across 3D renderers that extend different
 * branches of PGraphics.
 */
public interface IUp3 extends IUp {

   /**
    * Draws a cubic Bezier curve between two anchor points, where the control
    * points shape the curve.
    *
    * @param ap0 the first anchor point
    * @param cp0 the first control point
    * @param cp1 the second control point
    * @param ap1 the second anchor point
    */
   void bezier ( final Vec3 ap0, final Vec3 cp0, final Vec3 cp1,
      final Vec3 ap1 );

   /**
    * Draws a cubic Bezier curve segment to the next anchor point; the first
    * and second control point shape the curve segment.
    *
    * @param cp0 the first control point
    * @param cp1 the second control point
    * @param ap1 the next anchor point
    */
   void bezierVertex ( final Vec3 cp0, final Vec3 cp1, final Vec3 ap1 );

   /**
    * Draws a bounding volume.
    *
    * @param b the bounds
    */
   void bounds ( final Bounds3 b );

   /**
    * Draws an octree.
    *
    * @param ot the octree
    */
   @Recursive
   default void bounds ( final Octree ot ) {

      if ( ot.isLeaf() ) {
         this.bounds(ot.bounds);
      } else {
         for ( int i = 0; i < 8; ++i ) { this.bounds(ot.children[i]); }
      }
   }

   /**
    * Looks at the center point from the eye point, using a default reference
    * up axis.
    *
    * @param xEye    camera location x
    * @param yEye    camera location y
    * @param zEye    camera location z
    * @param xCenter target location x
    * @param yCenter target location y
    * @param zCenter target location z
    */
   void camera ( final float xEye, final float yEye, final float zEye,
      final float xCenter, final float yCenter, final float zCenter );

   /**
    * Sets the camera to a location, looking at a center, with the default up
    * direction.
    *
    * @param eye    the eye location
    * @param center the center of the gaze
    */
   default void camera ( final Vec3 eye, final Vec3 center ) {

      this.camera(eye.x, eye.y, eye.z, center.x, center.y, center.z);
   }

   /**
    * Dollies the camera, moving it on its local z axis, backward or forward.
    * This is done by multiplying the z magnitude by the camera inverse, then
    * adding the local coordinates to the camera location and look target.
    *
    * @param z the z magnitude
    */
   void dolly ( final float z );

   /**
    * Gets the renderer's camera location.
    *
    * @param target the output vector
    *
    * @return the location
    */
   Vec3 getLocation ( Vec3 target );

   /**
    * Gets the renderer's camera location on the x axis.
    *
    * @return the camera x
    */
   float getLocX ( );

   /**
    * Gets the renderer's camera location on the y axis.
    *
    * @return the camera y
    */
   float getLocY ( );

   /**
    * Gets the renderer's camera location on the z axis.
    *
    * @return the camera z
    */
   float getLocZ ( );

   /**
    * Gets the x component of the location at which the camera is looking.
    *
    * @return the location x
    */
   float getLookTargetX ( );

   /**
    * Gets the y component of the location at which the camera is looking.
    *
    * @return the location y
    */

   float getLookTargetY ( );

   /**
    * Gets the z component of the location at which the camera is looking.
    *
    * @return the location z
    */

   float getLookTargetZ ( );

   /**
    * Draws a diagnostic grid out of points.
    *
    * @param count number of points
    */
   default void grid ( final int count ) {

      this.grid(count, IUp.DEFAULT_STROKE_WEIGHT * 1.5f);
   }

   /**
    * Draws a diagnostic grid out of points.
    *
    * @param count        number of points
    * @param strokeWeight stroke weight
    */
   default void grid ( final int count, final float strokeWeight ) {

      this.grid(count, strokeWeight, IUp.DEFAULT_STROKE_COLOR);
   }

   /**
    * Draws a diagnostic grid out of points.
    *
    * @param count        number of points
    * @param strokeWeight stroke weight
    * @param strokeColor  the stroke color
    */
   default void grid ( final int count, final float strokeWeight,
      final int strokeColor ) {

      this.grid(count, strokeWeight, strokeColor, count * IYup2.GRID_FAC);
   }

   /**
    * Draws a diagnostic grid out of points.
    *
    * @param count        number of points
    * @param strokeWeight stroke weight
    * @param strokeColor  the stroke color
    * @param dim          the grid dimension
    */
   default void grid ( final int count, final float strokeWeight,
      final int strokeColor, final float dim ) {

      final float xHalfDim = dim * 0.5f;
      final float yHalfDim = xHalfDim;
      final float zHalfDim = xHalfDim;
      final float sign = this.handedness().getSign();

      final float right = xHalfDim;
      final float left = -xHalfDim;
      final float top = -sign * yHalfDim;
      final float bottom = sign * yHalfDim;
      final float far = -sign * zHalfDim;
      final float near = sign * zHalfDim;

      final int vcount = count < 3 ? 3 : count;
      final float toPercent = 1.0f / ( vcount + 1.0f );
      final float[] xs = new float[vcount];
      final float[] ys = new float[vcount];
      final float[] zs = new float[vcount];

      this.hint(PConstants.DISABLE_DEPTH_TEST);
      this.hint(PConstants.DISABLE_DEPTH_MASK);
      this.pushStyle();

      /* Draw corner. */
      this.strokeWeight(strokeWeight * 1.5f);
      this.stroke(0xffffffff);
      this.point(left, bottom, far);
      this.strokeWeight(strokeWeight * 1.375f);

      /* Draw x axis. */
      this.stroke(IUp.DEFAULT_I_COLOR);
      for ( int j = 0; j < vcount; ++j ) {
         final float jPercent = ( 1 + j ) * toPercent;
         final float x = ( 1.0f - jPercent ) * right + jPercent * left;
         xs[j] = x;
         this.point(x, bottom, far);
      }

      /* Draw y axis. */
      this.stroke(IUp.DEFAULT_J_COLOR);
      for ( int i = 0; i < vcount; ++i ) {
         final float iPercent = ( 1 + i ) * toPercent;
         final float y = ( 1.0f - iPercent ) * top + iPercent * bottom;
         ys[i] = y;
         this.point(left, y, far);
      }

      /* Draw z axis. */
      this.stroke(IUp.DEFAULT_K_COLOR);
      for ( int h = 0; h < vcount; ++h ) {
         final float hPercent = ( 1 + h ) * toPercent;
         final float z = ( 1.0f - hPercent ) * near + hPercent * far;
         zs[h] = z;
         this.point(left, bottom, z);
      }

      this.strokeWeight(strokeWeight);
      this.stroke(strokeColor);

      /* Draw x plane (y and z vary, x is constant). */
      for ( int h = 0; h < vcount; ++h ) {
         final float z = zs[h];
         for ( int i = 0; i < vcount; ++i ) { this.point(left, ys[i], z); }
      }

      /* Draw y plane (x and z vary, y is constant). */
      for ( int h = 0; h < vcount; ++h ) {
         final float z = zs[h];
         for ( int j = 0; j < vcount; ++j ) { this.point(xs[j], bottom, z); }
      }

      /* Draw z plane (x and y vary, z is constant). */
      for ( int i = 0; i < vcount; ++i ) {
         final float y = ys[i];
         for ( int j = 0; j < vcount; ++j ) { this.point(xs[j], y, far); }
      }

      this.popStyle();
      this.hint(PConstants.ENABLE_DEPTH_TEST);
      this.hint(PConstants.ENABLE_DEPTH_MASK);
   }

   /**
    * Returns the handedness of the renderer.
    *
    * @return the handedness
    */
   Handedness handedness ( );

   /**
    * Sets a rendering hint in the OpenGL renderer.
    *
    * @param code the hint code
    */
   void hint ( final int code );

   /**
    * Draws a line between two coordinates.
    *
    * @param ax the origin x coordinate
    * @param ay the origin y coordinate
    * @param az the origin z coordinate
    * @param bx the destination x coordinate
    * @param by the destination y coordinate
    * @param bz the destination z coordinate
    */
   void line ( final float ax, final float ay, final float az, final float bx,
      final float by, final float bz );

   /**
    * Draws a line between two coordinates.
    *
    * @param origin the origin coordinate
    * @param dest   the destination coordinate
    */
   void line ( final Vec3 origin, final Vec3 dest );

   /**
    * Gets a mouse within unit coordinates.
    *
    * @param target the output vector
    *
    * @return the mouse
    */
   default Vec3 mouse1 ( final Vec3 target ) {

      return this.mouse1s(target);
   }

   /**
    * Gets a mouse within a unit square, where either component may be in the
    * range [-1.0, 1.0] .
    *
    * @param target the output vector
    *
    * @return the mouse
    */
   default Vec3 mouse1s ( final Vec3 target ) {

      return IUp3.mouse1s(this.getParent(), target);
   }

   /**
    * Gets a mouse within the range [0.0, 1.0] . The mouse's y coordinate is
    * flipped.
    *
    * @param target the output vector
    *
    * @return the mouse
    */
   default Vec3 mouse1u ( final Vec3 target ) {

      return IUp3.mouse1u(this.getParent(), target);
   }

   /**
    * Moves the camera by the given vector then updates the camera.
    *
    * @param x the vector x
    * @param y the vector y
    * @param z the vector z
    *
    * @see IUp3#moveTo(float, float, float)
    */
   default void moveBy ( final float x, final float y, final float z ) {

      this.moveByGlobal(x, y, z);
   }

   /**
    * Moves the camera by the given vector then updates the camera.
    *
    * @param v the vector
    *
    * @see IUp3#moveByGlobal(float, float, float)
    */
   default void moveBy ( final Vec3 v ) {

      this.moveByGlobal(v.x, v.y, v.z);
   }

   /**
    * Moves the camera by the given vector in global space, then updates the
    * camera.
    *
    * @param x the vector x
    * @param y the vector y
    * @param z the vector z
    *
    * @see IUp3#moveTo(float, float, float)
    * @see IUp3#getLocX()
    * @see IUp3#getLocY()
    * @see IUp3#getLocZ()
    */
   default void moveByGlobal ( final float x, final float y, final float z ) {

      this.moveTo(this.getLocX() + x, this.getLocY() + y, this.getLocZ() + z);
   }

   /**
    * Moves the camera by the given vector in global space, then updates the
    * camera.
    *
    * @param v the vector
    *
    * @see IUp3#moveByGlobal(float, float, float)
    */
   default void moveByGlobal ( final Vec3 v ) {

      this.moveByGlobal(v.x, v.y, v.z);
   }

   /**
    * Moves the renderer's camera to the given location, then updates the
    * camera.
    *
    * @param x the location x
    * @param y the location y
    * @param z the location z
    *
    * @see IUp3#getLookTargetX()
    * @see IUp3#getLookTargetY()
    * @see IUp3#getLookTargetZ()
    */
   default void moveTo ( final float x, final float y, final float z ) {

      this.camera(x, y, z, this.getLookTargetX(), this.getLookTargetY(), this
         .getLookTargetZ());
   }

   /**
    * Moves the renderer's camera to the given location, then updates the
    * camera.
    *
    * @param locNew the new location
    *
    * @see IUp3#moveTo(float, float, float)
    */
   default void moveTo ( final Vec3 locNew ) {

      this.moveTo(locNew.x, locNew.y, locNew.z);
   }

   /**
    * Moves the renderer's camera to a given location and updates the camera.
    * Uses clamped linear interpolation, so the step should be smoothed prior
    * to calling this function.
    *
    * @param locNew the new location
    * @param step   the step
    *
    * @see IUp3#getLocX()
    * @see IUp3#getLocY()
    * @see IUp3#getLocZ()
    * @see IUp3#moveTo(float, float, float)
    */
   default void moveTo ( final Vec3 locNew, final float step ) {

      if ( step <= 0.0f ) { return; }
      if ( step >= 1.0f ) {
         this.moveTo(locNew.x, locNew.y, locNew.z);
         return;
      }

      final float u = 1.0f - step;
      this.moveTo(u * this.getLocX() + step * locNew.x, u * this.getLocY()
         + step * locNew.y, u * this.getLocZ() + step * locNew.z);
   }

   /**
    * Assigns a normal to a vertex.
    *
    * @param x the x component
    * @param y the y component
    * @param z the z component
    */
   void normal ( float x, float y, float z );

   /**
    * Assigns a normal to a vertex.
    *
    * @param n the normal
    */
   default void normal ( final Vec3 n ) { this.normal(n.x, n.y, n.z); }

   /**
    * Boom or pedestal the camera, moving it on its local y axis, up or down.
    * This is done by multiplying the y magnitude by the camera inverse, then
    * adding the local coordinates to both the camera location and look
    * target.
    *
    * @param y the y magnitude
    */
   void pedestal ( final float y );

   /**
    * Draws a 3D point.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    * @param z the z coordinate
    */
   void point ( final float x, final float y, final float z );

   /**
    * Draws a point at a given coordinate
    *
    * @param v the coordinate
    */
   default void point ( final Vec3 v ) { this.point(v.x, v.y, v.z); }

   /**
    * Draws a quadratic Bezier curve segment to the next anchor point; the
    * control point shapes the curve segment.
    *
    * @param cp  the control point
    * @param ap1 the next anchor point
    */
   void quadraticVertex ( final Vec3 cp, final Vec3 ap1 );

   /**
    * Displays a ray, i.e., an origin point and a direction. The display
    * length of the direction is dictated by an input.
    *
    * @param xOrigin the x origin
    * @param yOrigin the y origin
    * @param zOrigin the z origin
    * @param xDir    the x direction
    * @param yDir    the y direction
    * @param zDir    the z direction
    * @param dLen    the display length
    */
   default void ray ( final float xOrigin, final float yOrigin,
      final float zOrigin, final float xDir, final float yDir, final float zDir,
      final float dLen ) {

      this.ray(xOrigin, yOrigin, zOrigin, xDir, yDir, zDir, dLen, 1.0f, 4.0f,
         2.0f);
   }

   /**
    * Displays a ray, i.e., an origin point and a direction. The display
    * length of the direction is dictated by an input.
    *
    * @param xOrigin the x origin
    * @param yOrigin the y origin
    * @param zOrigin the z origin
    * @param xDir    the x direction
    * @param yDir    the y direction
    * @param zDir    the z direction
    * @param dLen    the display length
    * @param lnwgt   the line weight
    * @param oWeight the origin stroke weight
    * @param dWeight the direction stroke weight
    */
   default void ray ( final float xOrigin, final float yOrigin,
      final float zOrigin, final float xDir, final float yDir, final float zDir,
      final float dLen, final float lnwgt, final float oWeight,
      final float dWeight ) {

      final float mSq = xDir * xDir + yDir * yDir + zDir * zDir;

      this.pushStyle();
      this.strokeWeight(oWeight);
      this.point(xOrigin, yOrigin, zOrigin);

      if ( mSq != 0.0f ) {

         this.strokeWeight(lnwgt);

         float dx;
         float dy;
         float dz;

         if ( Utils.approx(mSq, 1.0f, 0.0001f) ) {

            dx = xOrigin + xDir * dLen;
            dy = yOrigin + yDir * dLen;
            dz = zOrigin + zDir * dLen;
            this.line(xOrigin, yOrigin, zOrigin, dx, dy, dz);

         } else {

            final float mInv = dLen * Utils.invSqrtUnchecked(mSq);
            dx = xOrigin + xDir * mInv;
            dy = yOrigin + yDir * mInv;
            dz = zOrigin + zDir * mInv;
            this.line(xOrigin, yOrigin, zOrigin, dx, dy, dz);

         }

         this.strokeWeight(dWeight);
         this.point(dx, dy, dz);

      }

      this.popStyle();
   }

   /**
    * Displays a ray, i.e., an origin point and a direction. The display
    * length of the direction is dictated by an input.
    *
    * @param ray  the ray
    * @param dLen the display length
    */
   default void ray ( final Ray3 ray, final float dLen ) {

      final Vec3 origin = ray.origin;
      final Vec3 dir = ray.dir;
      this.ray(origin.x, origin.y, origin.z, dir.x, dir.y, dir.z, dLen);
   }

   /**
    * Displays a ray, i.e., an origin point and a direction. The display
    * length of the direction is dictated by an input.
    *
    * @param ray     the ray
    * @param dLen    the display length
    * @param lnwgt   the line weight
    * @param oWeight the origin stroke weight
    * @param dWeight the direction stroke weight
    */
   default void ray ( final Ray3 ray, final float dLen, final float lnwgt,
      final float oWeight, final float dWeight ) {

      final Vec3 origin = ray.origin;
      final Vec3 dir = ray.dir;
      this.ray(origin.x, origin.y, origin.z, dir.x, dir.y, dir.z, dLen, lnwgt,
         oWeight, dWeight);
   }

   /**
    * Trucks the camera, moving it on its local x axis, left or right. This is
    * done by multiplying the x magnitude by the camera inverse, then adding
    * the local coordinates to both the camera location and look target.
    *
    * @param x the x magnitude
    */
   void truck ( final float x );

   /**
    * Adds another vertex to a shape between the beginShape and endShape
    * commands.
    *
    * @param v the coordinate
    */
   void vertex ( final Vec3 v );

   /**
    * The default renderer handedness.
    */
   Handedness DEFAULT_HANDEDNESS = Handedness.RIGHT;

   /**
    * Factor by which a grid's count is scaled when dimensions are not
    * supplied.
    */
   float GRID_FAC = 32.0f;

   /**
    * Tolerance beneath which the camera's forward direction will be
    * considered the world, or reference, up direction.
    */
   float POLARITY_TOLERANCE = 0.001f;

   /**
    * Gets a mouse within a unit square, where either component may be in the
    * range [-1.0, 1.0] . The mouse's y coordinate is flipped and assigned to
    * the vector's y component. (This is not a normalized vector.)
    *
    * @param parent the parent applet
    * @param target the output vector
    *
    * @return the mouse
    */
   static Vec3 mouse1s ( final PApplet parent, final Vec3 target ) {

      return target.set(2.0f * Utils.clamp01(parent.mouseX
         / ( float ) parent.width) - 1.0f, 1.0f - 2.0f * Utils.clamp01(
            parent.mouseY / ( float ) parent.height), 0.0f);
   }

   /**
    * Gets a mouse within the range [0.0, 1.0]. The mouse's y coordinate is
    * flipped.
    *
    * @param parent the parent applet
    * @param target the output vector
    *
    * @return the mouse
    */
   static Vec3 mouse1u ( final PApplet parent, final Vec3 target ) {

      return target.set(Utils.clamp01(parent.mouseX / ( float ) parent.width),
         1.0f - Utils.clamp01(parent.mouseY / ( float ) parent.height), 0.0f);
   }

}
