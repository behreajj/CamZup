package camzup.pfriendly;

import camzup.core.Color;
import camzup.core.CurveEntity2;
import camzup.core.IUtils;
import camzup.core.MaterialSolid;
import camzup.core.MeshEntity2;
import camzup.core.Ray2;
import camzup.core.Utils;
import camzup.core.Vec2;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Maintains consistent behavior across 2D renderers that
 * extend different branches of PGraphics, such as
 * PGraphics2D and PGraphicsJava2D.
 */
public interface IYup2 extends IUp {

   /**
    * Factor by which a grid's count is scaled when dimensions
    * are not supplied.
    */
   float GRID_FAC = 32.0f;

   /**
    * Finds the mouse's location in world coordinates relative
    * to the renderer's camera.
    *
    * @param parent
    *           the PApplet
    * @param renderer
    *           the renderer
    * @param target
    *           the output vector
    * @return the mouse coordinate
    */
   static Vec2 mouse (
         final PApplet parent,
         final IYup2 renderer,
         final Vec2 target ) {

      /* Normalize to [0.0, 1.0], then shift to [-0.5, 0.5]. */
      float mx = parent.mouseX / (float) parent.width - 0.5f;
      float my = 0.5f - parent.mouseY / (float) parent.height;

      /* Scale. */
      mx *= Utils.div(renderer.getWidth(), renderer.getZoomX());
      my *= Utils.div(renderer.getHeight(), renderer.getZoomY());

      /* Rotate. */
      final float angle = renderer.getRot();

      final float cosa = Utils.cos(angle);
      final float sina = Utils.sin(angle);

      final float temp = mx;
      mx = cosa * mx - sina * my;
      my = cosa * my + sina * temp;

      /* Translate. */
      mx += renderer.getLocX();
      my += renderer.getLocY();

      return target.set(mx, my);
   }

   /**
    * Gets a mouse within a unit square, where either component
    * may be in the range [-1.0, 1.0]. The mouse's y coordinate
    * is flipped. (This is not a normalized vector.)
    *
    * @param parent
    *           the parent applet
    * @param renderer
    *           the renderer
    * @param target
    *           the output vector
    * @return the mouse
    */
   static Vec2 mouse1 (
         final PApplet parent,
         final IYup2 renderer,
         final Vec2 target ) {

      final float mx = Utils.clamp01(
            parent.mouseX / (float) parent.width);
      final float my = Utils.clamp01(
            parent.mouseY / (float) parent.height);

      return target.set(
            mx + mx - 1.0f,
            1.0f - (my + my));
   }

   /**
    * Generates an SVG rect element to replicate the renderer's
    * background. Assumes that the background is opaque, i.e.,
    * has an alpha channel of 1.0 . Returns a String.
    *
    * @param renderer
    *           the renderer
    * @return the string
    */
   static String svgBackground ( final IYup2 renderer ) {

      return new StringBuilder(128)
            .append("<rect x=\"0\" y=\"0\" width=\"")
            .append(renderer.getWidth())
            .append("\" height=\"")
            .append(renderer.getHeight())
            .append("\" stroke=\"none\" fill=\"")
            .append(Color.toHexWeb(renderer.getBackground()))
            .append("\"></rect>")
            .toString();
   }

   /**
    * Generates an SVG transform as a string from the
    * renderer's dimensions, camera zoom, camera rotation and
    * camera location.
    *
    * @param renderer
    *           the renderer
    * @return the string
    */
   static String svgCamera ( final IYup2 renderer ) {

      return new StringBuilder(128)
            .append("transform=\"translate(")
            .append(Utils.toFixed(renderer.getWidth() * 0.5f, 4))
            .append(',').append(' ')
            .append(Utils.toFixed(renderer.getHeight() * 0.5f, 4))
            .append(") scale(")
            .append(Utils.toFixed(renderer.getZoomX(), 4))
            .append(',').append(' ')
            .append(Utils.toFixed(-renderer.getZoomY(), 4))
            .append(") rotate(")
            .append(Utils.toFixed(-renderer.getRot() * IUtils.RAD_TO_DEG, 1))
            .append(") translate(")
            .append(Utils.toFixed(-renderer.getLocX(), 4))
            .append(',').append(' ')
            .append(Utils.toFixed(-renderer.getLocY(), 4))
            .append(')').append('\"')
            .toString();
   }

   /**
    * Generates the boilerplate at the top of an SVG as a
    * String based on the renderer's information. Includes the
    * SVG's viewbox.
    *
    * @param renderer
    *           the renderer
    * @return the String
    */
   static String svgHeader ( final IYup2 renderer ) {

      return new StringBuilder(128)
            .append("<svg ")
            .append("xmlns=\"http://www.w3.org/2000/svg\" ")
            .append("xmlns:xlink=\"http://www.w3.org/1999/xlink\" ")
            .append("viewBox=\"0 0 ")
            .append((int) renderer.getWidth())
            .append(' ')
            .append((int) renderer.getHeight())
            .append("\">")
            .toString();
   }

   /**
    * Generates an SVG string from several 2D curve entities.
    *
    * @param renderer
    *           the renderer
    * @param ces
    *           the curve entities
    * @return the string
    */
   static String toSvgString (
         final IYup2 renderer,
         final CurveEntity2... ces ) {

      final StringBuilder result = new StringBuilder();

      result.append(IYup2.svgHeader(renderer))
            .append('\n');

      result.append(IYup2.svgBackground(renderer))
            .append('\n');

      result.append("<g ")
            .append(IYup2.svgCamera(renderer))
            .append('>')
            .append('\n');

      for (final CurveEntity2 ce : ces) {
         result.append(ce.toSvgString())
               .append('\n');
      }

      return result.append("</g>\n</svg>").toString();
   }

   /**
    * Generates an SVG string from several 2D mesh entities.
    *
    * @param renderer
    *           the renderer
    * @param mes
    *           the mesh entities
    * @param mats
    *           the materials
    * @return the string
    */
   static String toSvgString (
         final IYup2 renderer,
         final MeshEntity2[] mes,
         final MaterialSolid[] mats ) {

      // TODO: toSvgString for single me, single mat, no mats.

      final StringBuilder result = new StringBuilder();

      result.append(IYup2.svgHeader(renderer))
            .append('\n');

      result.append(IYup2.svgBackground(renderer))
            .append('\n');

      result.append("<g ")
            .append(IYup2.svgCamera(renderer))
            .append('>')
            .append('\n');

      for (final MeshEntity2 me : mes) {
         result.append(me.toSvgString(mats));
         result.append('\n');
      }

      return result.append("</g>\n</svg>").toString();
   }

   /**
    * Draws an arc at a location from a start angle to a stop
    * angle.
    *
    * @param v
    *           the location
    * @param sz
    *           the arc size
    * @param start
    *           the start angle
    * @param stop
    *           the stop angle
    * @param mode
    *           the arc mode
    */
   void arc (
         final Vec2 v,
         final float sz,
         final float start,
         final float stop,
         final int mode );

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
   void bezier (
         final Vec2 ap0,
         final Vec2 cp0,
         final Vec2 cp1,
         final Vec2 ap1 );

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
   void bezierVertex (
         final Vec2 cp0,
         final Vec2 cp1,
         final Vec2 ap1 );

   /**
    * Draws a circle at a location
    *
    * @param a
    *           the coordinate
    * @param b
    *           the size
    */
   void circle ( final Vec2 a, final float b );

   /**
    * Draws a curve between four points.
    *
    * @param a
    *           the first point
    * @param b
    *           the second point
    * @param c
    *           the third point
    * @param d
    *           the fourth point
    */
   void curve (
         final Vec2 a,
         final Vec2 b,
         final Vec2 c,
         final Vec2 d );

   /**
    * Draws a curve segment.
    *
    * @param a
    *           the coordinate
    */
   void curveVertex ( final Vec2 a );

   /**
    * Draws an ellipse; the meaning of the two parameters
    * depends on the renderer's ellipseMode.
    *
    * @param a
    *           the first parameter
    * @param b
    *           the second parameter
    */
   void ellipse ( final Vec2 a, final Vec2 b );

   /**
    * Gets the renderer's background color as an integer.
    *
    * @return the color
    */
   int getBackground ();

   /**
    * Gets the renderer's background color.
    *
    * @param target
    *           the output color
    * @return the background
    */
   Color getBackground ( Color target );

   /**
    * Gets the renderer's camera location.
    *
    * @param target
    *           the output vector
    * @return the location
    */
   Vec2 getLoc ( Vec2 target );

   /**
    * Gets the renderer's camera location on the x axis.
    *
    * @return the camera x
    */
   float getLocX ();

   /**
    * Gets the renderer's camera location on the y axis.
    *
    * @return the camera y
    */
   float getLocY ();

   /**
    * Gets the renderer's camera rotation in radians.
    *
    * @return the rotation
    */
   float getRot ();

   /**
    * Gets the renderer's camera zoom.
    *
    * @param target
    *           the output vector
    * @return the zoom
    */
   Vec2 getZoom ( Vec2 target );

   /**
    * Gets the renderer's camera horizontal zoom.
    *
    * @return the zoom factor
    */
   float getZoomX ();

   /**
    * Gets the renderer's camera vertical zoom.
    *
    * @return the zoom factor
    */
   float getZoomY ();

   /**
    * Draws a diagnostic grid out of points.
    *
    * @param count
    *           number of points
    * @param strokeWeight
    *           stroke weight
    */
   default void grid (
         final int count,
         final float strokeWeight ) {

      this.grid(
            count,
            strokeWeight,
            count * IYup2.GRID_FAC);
   }

   /**
    * Draws a diagnostic grid out of points.
    *
    * @param count
    *           number of points
    * @param strokeWeight
    *           stroke weight
    * @param dim
    *           the grid dimensions
    */
   default void grid (
         final int count,
         final float strokeWeight,
         final float dim ) {

      this.pushStyle();
      this.strokeWeight(strokeWeight);

      final float right = dim * 0.5f;
      final float left = -right;

      final float top = dim * 0.5f;
      final float bottom = -top;

      final float toPercent = 1.0f / count;
      final int last = count + 1;

      final int ab = 0xff000080;

      /*
       * Precalculate values for inner loop.
       */
      final float[] xs = new float[last];
      final int[] reds = new int[last];
      for (int j = 0; j < last; ++j) {
         final float jPercent = j * toPercent;
         xs[j] = Utils.lerpUnclamped(left, right, jPercent);
         reds[j] = (int) (jPercent * 0xff + 0.5f) << 0x10;
      }

      for (int i = 0; i < last; ++i) {
         final float iPercent = i * toPercent;
         final float y = Utils.lerpUnclamped(bottom, top, iPercent);
         final int agb = ab | (int) (iPercent * 0xff + 0.5f) << 0x8;

         for (int j = 0; j < last; ++j) {
            this.stroke(agb | reds[j]);
            this.point(xs[j], y);
         }
      }

      this.popStyle();
   }

   /**
    * Draws an image at the origin.
    *
    * @param img
    *           the image
    */
   void image ( final PImage img );

   /**
    * Draws an image at a given coordinate.
    *
    * @param img
    *           the image
    * @param coord
    *           the coordinate
    */
   void image ( final PImage img, final Vec2 coord );

   /**
    * Draws an image at a given coordinate and dimension.
    *
    * @param img
    *           the image
    * @param coord
    *           the coordinate
    * @param dim
    *           the dimension
    */
   void image ( final PImage img, final Vec2 coord, final Vec2 dim );

   /**
    * Sets the renderer's image mode.
    *
    * @param mode
    *           the mode
    */
   void imageMode ( final int mode );

   /**
    * Draws a line between two coordinates.
    *
    * @param ax
    *           the origin x coordinate
    * @param ay
    *           the origin y coordinate
    * @param bx
    *           the destination x coordinate
    * @param by
    *           the destination y coordinate
    */
   void line (
         final float ax,
         final float ay,
         final float bx,
         final float by );

   /**
    * Draws a line between two coordinates.
    *
    * @param a
    *           the origin coordinate
    * @param b
    *           the destination coordinate
    */
   void line ( final Vec2 a, final Vec2 b );

   /**
    * Finds the mouse's location in world coordinates relative
    * to the renderer's camera.
    *
    * @param target
    *           the output vector
    * @return the mouse coordinate
    */
   default Vec2 mouse ( final Vec2 target ) {

      return IYup2.mouse(this.getParent(), this, target);
   }

   /**
    * Gets a mouse within a unit square, where either component
    * may be in the range [-1.0, 1.0]. (This is not a
    * normalized vector.)
    *
    * @param target
    *           the output vector
    * @return the mouse
    */
   default Vec2 mouse1 ( final Vec2 target ) {

      return IYup2.mouse1(this.getParent(), this, target);
   }

   /**
    * Draws a 2D point.
    *
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    */
   void point ( final float x, final float y );

   /**
    * Draws a point at a given coordinate
    *
    * @param v
    *           the coordinate
    */
   default void point ( final Vec2 v ) {

      this.point(v.x, v.y);
   }

   /**
    * Pop the last style off the end of the stack.
    */
   void popStyle ();

   /**
    * Push a style onto the end of the stack.
    */
   void pushStyle ();

   /**
    * Draws a quadrilateral between four points.
    *
    * @param a
    *           the first point
    * @param b
    *           the second point
    * @param c
    *           the third point
    * @param d
    *           the fourth point
    */
   void quad (
         final Vec2 a, final Vec2 b,
         final Vec2 c, final Vec2 d );

   /**
    * Draws a quadratic Bezier curve segment to the next anchor
    * point; the control point shapes the curve segment.
    *
    * @param cp
    *           the control point
    * @param ap1
    *           the next anchor point
    */
   void quadraticVertex (
         final Vec2 cp,
         final Vec2 ap1 );

   /**
    * Displays a ray, i.e., an origin point and a direction.
    * The display length of the direction is dictated by an
    * input.
    *
    * @param xOrigin
    *           the x origin
    * @param yOrigin
    *           the y origin
    * @param xDir
    *           the x direction
    * @param yDir
    *           the y direction
    * @param dLen
    *           the display length
    */
   default void ray (
         final float xOrigin,
         final float yOrigin,
         final float xDir,
         final float yDir,
         final float dLen ) {

      this.ray(
            xOrigin, yOrigin,
            xDir, yDir,
            dLen, 1.0f, 4.0f, 2.0f);
   }

   /**
    * Displays a ray, i.e., an origin point and a direction.
    * The display length of the direction is dictated by an
    * input.
    *
    * @param xOrigin
    *           the x origin
    * @param yOrigin
    *           the y origin
    * @param xDir
    *           the x direction
    * @param yDir
    *           the y direction
    * @param dLen
    *           the display length
    * @param lnwgt
    *           the line weight
    * @param oWeight
    *           the origin stroke weight
    * @param dWeight
    *           the direction stroke weight
    */
   default void ray (
         final float xOrigin,
         final float yOrigin,
         final float xDir,
         final float yDir,
         final float dLen,
         final float lnwgt,
         final float oWeight,
         final float dWeight ) {

      final float mSq = xDir * xDir + yDir * yDir;

      this.pushStyle();
      this.strokeWeight(oWeight);
      this.point(xOrigin, yOrigin);

      if (mSq != 0.0f) {

         final float mInv = dLen * Utils.invSqrtUnchecked(mSq);
         final float dx = xOrigin + xDir * mInv;
         final float dy = yOrigin + yDir * mInv;

         this.strokeWeight(lnwgt);
         this.line(xOrigin, yOrigin, dx, dy);
         this.strokeWeight(dWeight);
         this.point(dx, dy);
      }
      this.popStyle();
   }

   /**
    * Displays a ray, i.e., an origin point and a direction.
    * The display length of the direction is dictated by an
    * input.
    *
    * @param ray
    *           the ray
    * @param dLen
    *           the display length
    */
   default void ray (
         final Ray2 ray,
         final float dLen ) {

      this.ray(
            ray.origin.x, ray.origin.y,
            ray.dir.x, ray.dir.y, dLen);
   }

   /**
    * Displays a ray, i.e., an origin point and a direction.
    * The display length of the direction is dictated by an
    * input.
    *
    * @param ray
    *           the ray
    * @param dLen
    *           the display length
    * @param lnwgt
    *           the line weight
    * @param oWeight
    *           the origin stroke weight
    * @param dWeight
    *           the direction stroke weight
    */
   default void ray (
         final Ray2 ray,
         final float dLen,
         final float lnwgt,
         final float oWeight,
         final float dWeight ) {

      this.ray(
            ray.origin.x, ray.origin.y,
            ray.dir.x, ray.dir.y,
            dLen, lnwgt, oWeight, dWeight);
   }

   /**
    * Draws a rectangle; the meaning of the two parameters
    * depends on the renderer's rectMode.
    *
    * @param a
    *           the first parameter
    * @param b
    *           the second parameter
    */
   void rect ( final Vec2 a, final Vec2 b );

   /**
    * Draws a rounded rectangle; the meaning of the first two
    * parameters depends on the renderer's rectMode.
    *
    * @param a
    *           the first parameter
    * @param b
    *           the second parameter
    * @param rounding
    *           the corner rounding
    */
   void rect ( final Vec2 a, final Vec2 b, final float rounding );

   /**
    * Draws a square.
    *
    * @param a
    *           the location
    * @param b
    *           the size
    */
   void square ( final Vec2 a, final float b );

   /**
    * Draws a rounded square.
    *
    * @param a
    *           the location
    * @param b
    *           the size
    * @param rounding
    *           the corner rounding
    */
   void square ( final Vec2 a, final float b, final float rounding );

   /**
    * Sets the renderer's stroke color.
    *
    * @param c
    *           the hexadecimal color
    */
   @Override
   void stroke ( final int c );

   /**
    * Sets the renderer's stroke weight.
    *
    * @param sw
    *           the stroke weight
    */
   @Override
   void strokeWeight ( final float sw );

   /**
    * Displays a boolean as text at a location.
    *
    * @param bool
    *           the boolean
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    */
   default void text (
         final boolean bool,
         final float x,
         final float y ) {

      this.text(bool ? "true" : "false", x, y);
   }

   /**
    * Displays a real number at a 2D location. Fixes the number
    * display to four decimal places.
    *
    * @param num
    *           the number
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    */
   default void text (
         final float num,
         final float x,
         final float y ) {

      this.text(Utils.toFixed(num, 4), x, y);
   }

   /**
    * Displays an object as text at a location. Calls the
    * object's toString function.
    *
    * @param obj
    *           the object
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    */
   default void text (
         final Object obj,
         final float x,
         final float y ) {

      final String str = obj.toString();
      if (str.length() > 96) {
         this.text(str.substring(0, 95), x, y);
      } else {
         this.text(str, x, y);
      }
   }

   /**
    * Displays a string at a coordinate.
    *
    * @param str
    *           the string
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    */
   void text ( final String str, final float x, final float y );

   /**
    * Generates an SVG string from several 2D curve entities.
    *
    * @param ces
    *           the curve entities
    * @return the string
    */
   default String toSvgString ( final CurveEntity2... ces ) {

      return IYup2.toSvgString(this, ces);
   }

   /**
    * Generates an SVG string from several 2D mesh entities.
    *
    * @param mes
    *           the mesh entities
    * @param mats
    *           the materials
    * @return the string
    */
   default String toSvgString (
         final MeshEntity2[] mes,
         final MaterialSolid[] mats ) {

      return IYup2.toSvgString(this, mes, mats);
   }

   /**
    * Draws a triangle between three points.
    *
    * @param a
    *           the first point
    * @param b
    *           the second point
    * @param c
    *           the third point
    */
   void triangle (
         final Vec2 a,
         final Vec2 b,
         final Vec2 c );

   /**
    * Adds another vertex to a shape between the beginShape and
    * endShape commands.
    *
    * @param v
    *           the coordinate
    */
   void vertex ( final Vec2 v );
}
