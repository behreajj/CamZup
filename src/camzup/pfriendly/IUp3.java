package camzup.pfriendly;

import processing.core.PApplet;

import camzup.core.Ray3;
import camzup.core.Utils;
import camzup.core.Vec3;

/**
 * Maintains consistent behavior across 3D renderers that extend
 * different branches of PGraphics.
 */
public interface IUp3 extends IUp {

  /**
   * Default sphere detail called by sphere when cached coordinates have
   * not been properly initialized.
   */
  int DEFAULT_SPHERE_DETAIL = 30;

  /**
   * Factor by which a grid's count is scaled when dimensions are not
   * supplied.
   */
  float GRID_FAC = 32.0f;

  /**
   * Draws a cubic Bezier curve between two anchor points, where the
   * control points shape the curve.
   *
   * @param ap0 the first anchor point
   * @param cp0 the first control point
   * @param cp1 the second control point
   * @param ap1 the second anchor point
   */
  void bezier (
      final Vec3 ap0,
      final Vec3 cp0,
      final Vec3 cp1,
      final Vec3 ap1 );

  /**
   * Draws a cubic Bezier curve segment to the next anchor point; the
   * first and second control point shape the curve segment.
   *
   * @param cp0 the first control point
   * @param cp1 the second control point
   * @param ap1 the next anchor point
   */
  void bezierVertex (
      final Vec3 cp0,
      final Vec3 cp1,
      final Vec3 ap1 );

  /**
   * Looks at the center point from the eye point, using a default
   * reference up axis.
   *
   * @param xEye    camera location x
   * @param yEye    camera location y
   * @param zEye    camera location z
   * @param xCenter target location x
   * @param yCenter target location y
   * @param zCenter target location z
   */
  void camera (
      final float xEye,
      final float yEye,
      final float zEye,
      final float xCenter,
      final float yCenter,
      final float zCenter );

  /**
   * Sets the camera to a location, looking at a center, with the
   * default up direction.
   *
   * @param eye    the eye location
   * @param center the center of the gaze
   */
  default void camera (
      final Vec3 eye,
      final Vec3 center ) {

    this.camera(
        eye.x, eye.y, eye.z,
        center.x, center.y, center.z);
  }

  /**
   * Gets the renderer's camera location.
   *
   * @param target the output vector
   * @return the location
   */
  Vec3 getLoc ( Vec3 target );

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
  void line (
      final float ax,
      final float ay,
      final float az,
      final float bx,
      final float by,
      final float bz );

  /**
   * Draws a line between two coordinates.
   *
   * @param origin the origin coordinate
   * @param dest   the destination coordinate
   */
  void line ( final Vec3 origin, final Vec3 dest );

  /**
   * Gets a mouse within a unit square, where either component may be in
   * the range [-1.0, 1.0]. the mouse's y component is assigned to the
   * vector's y component. (This is not a normalized vector.)
   *
   * @param target the output vector
   * @return the mouse
   */
  default Vec3 mouse1 ( final Vec3 target ) {

    return IUp3.mouse1(this.getParent(), this, target);
  }

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
  default void point ( final Vec3 v ) {

    this.point(v.x, v.y, v.z);
  }

  /**
   * Pop the last style off the end of the stack.
   */
  void popStyle ( );

  /**
   * Push a style onto the end of the stack.
   */
  void pushStyle ( );

  /**
   * Draws a quadratic Bezier curve segment to the next anchor point;
   * the control point shapes the curve segment.
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
  default void ray (
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
  default void ray (
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

    if ( mSq != 0.0f ) {

      this.strokeWeight(lnwgt);

      float dx = 0.0f;
      float dy = 0.0f;
      float dz = 0.0f;

      if ( Utils.approx(mSq, 1.0f, 0.0001f) ) {

        dx = xOrigin + xDir * dLen;
        dy = yOrigin + yDir * dLen;
        dz = zOrigin + zDir * dLen;
        this.line(
            xOrigin, yOrigin, zOrigin,
            dx, dy, dz);

      } else {

        final float mInv = dLen * Utils.invSqrtUnchecked(mSq);
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

  /**
   * Displays a ray, i.e., an origin point and a direction. The display
   * length of the direction is dictated by an input.
   *
   * @param ray  the ray
   * @param dLen the display length
   */
  default void ray (
      final Ray3 ray,
      final float dLen ) {

    final Vec3 origin = ray.origin;
    final Vec3 dir = ray.dir;
    this.ray(
        origin.x, origin.y, origin.z,
        dir.x, dir.y, dir.z,
        dLen);
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
  default void ray (
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
   * Sets the renderer's stroke weight.
   *
   * @param sw the stroke weight
   */
  @Override
  void strokeWeight ( final float sw );

  /**
   * Adds another vertex to a shape between the beginShape and endShape
   * commands.
   *
   * @param v the coordinate
   */
  void vertex ( final Vec3 v );

  /**
   * Gets a mouse within a unit square, where either component may be in
   * the range [-1.0, 1.0] . The mouse's y coordinate is flipped and
   * assigned to the vector's y component. (This is not a normalized
   * vector.)
   *
   * @param parent   the parent applet
   * @param renderer the renderer
   * @param target   the output vector
   * @return the mouse
   */
  static Vec3 mouse1 (
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
}
