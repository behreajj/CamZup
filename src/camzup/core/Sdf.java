package camzup.core;

/**
 * Facilitates implicit shapes created with signed distance
 * fields. Adapted from the GLSL of Inigo Quilez: <a href=
 * "https://www.iquilezles.org/www/articles/distfunctions2d/distfunctions2d.htm">2D
 * Distance Functions</a>, <a href=
 * "https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm">Distance
 * Functions</a>.
 *
 * @author Inigo Quilez
 */
public abstract class Sdf {

   @FunctionalInterface
   @Experimental
   interface FieldFunc {

      float execute ( Object... args );
   }

   @Experimental
   class Torus implements FieldFunc {

      @Override
      public float execute ( final Object... args ) {

         return this.execute(
               (Vec3) args[0],
               (Float) args[1],
               (Float) args[2]);

      }

      public float execute (
            final Vec3 point,
            final Float radius,
            final Float thickness ) {

         return Utils.hypot(Utils.hypot(point.x, point.y)
               - radius, point.z) - thickness;
      }

   }

   /**
    * Draws an open arc with rounded stroke caps. The angular
    * offset of the arc's aperture is to be calculated outside
    * of the function. The same goes for <em>twice</em> the
    * arc-length of the arc's aperture. Based on the GLSL:
    * <a href="https://www.shadertoy.com/view/wl23RK">
    * https://www.shadertoy.com/view/wl23RK</a>.
    *
    * @param point
    *           the point
    * @param cosOff
    *           cosine of the offset angle
    * @param sinOff
    *           sine of the offset angle
    * @param cosAptr2
    *           cosine of 2x the apertuare
    * @param sinAptr2
    *           sine of 2x the aperture
    * @param bounds
    *           the bounds
    * @param weight
    *           the stroke weight
    * @return the signed distance
    * @see Utils#abs(float)
    * @see Math#sqrt(double)
    */
   static float arc (
         final Vec2 point,
         final float cosOff,
         final float sinOff,
         final float cosAptr2,
         final float sinAptr2,
         final float bounds,
         final float weight ) {

      /*
       * Multiplying by a 2x2 matrix is equivalent to creating a
       * rotation around the z axis and applying to the point.
       */
      final float px0 = Utils.abs(sinOff * point.x - cosOff * point.y);
      final float py0 = cosOff * point.x + sinOff * point.y;
      final double dotp = px0 * px0 + py0 * py0;
      return (float) Math.sqrt(
            dotp + bounds * bounds - 2.0d * bounds *
                  (cosAptr2 * px0 > sinAptr2 * py0
                        ? px0 * sinAptr2 + py0 * cosAptr2
                        : Math.sqrt(dotp)))
            - weight;
   }

   /**
    * A wrapper function around the default signed distance for
    * arc. Follows the Processing convention of specifying the
    * start and stop angle as inputs. This calls cosine twice
    * and sine twice, and so will slow performance.
    *
    * @param point
    *           the point
    * @param startAngle
    *           the start angle
    * @param stopAngle
    *           the stop angle
    * @param bounds
    *           the bounds
    * @param weight
    *           the stroke weight
    * @return the signed distance
    * @see Utils#modRadians(float)
    * @see Math#cos(double)
    * @see Math#sin(double)
    * @see Sdf#arc(Vec2, float, float, float, float, float,
    *      float)
    */
   public static float arc (
         final Vec2 point,
         final float startAngle,
         final float stopAngle,
         final float bounds,
         final float weight ) {

      final float a = Utils.modRadians(startAngle);
      final float b = Utils.modRadians(stopAngle);

      /* Aperture is 2x arc length in the original. */
      final float arcLen = 0.5f * Utils.modRadians(b - a);
      final float arcOff = a + arcLen;
      return Sdf.arc(point,
            (float) Math.cos(arcOff), (float) Math.sin(arcOff),
            (float) Math.cos(arcLen), (float) Math.sin(arcLen),
            bounds, weight);
   }

   /**
    * Draws a two dimensional box whose dimensions are
    * described by the bounds.
    *
    * @param point
    *           the point
    * @param bounds
    *           the bounds
    * @return the signed distance
    * @see Utils#hypot(float, float)
    * @see Utils#min(float, float)
    * @see Utils#max(float, float)
    */
   public static float box (
         final Vec2 point,
         final Vec2 bounds ) {

      final float qx = Utils.abs(point.x) - bounds.x;
      final float qy = Utils.abs(point.y) - bounds.y;

      return Utils.hypot(
            Utils.max(qx, 0.0f),
            Utils.max(qy, 0.0f)) +
            Utils.min(Utils.max(qx, qy), 0.0f);
   }

   /**
    * Draws a two dimensional box with rounded corners whose
    * dimensions are described by the bounds.
    *
    * @param point
    *           the point
    * @param bounds
    *           the bounds
    * @param rounding
    *           corner rounding factor
    * @return the signed distance
    * @see Sdf#box(Vec2, Vec2)
    */
   public static float box (
         final Vec2 point,
         final Vec2 bounds,
         final float rounding ) {

      return Sdf.box(point, bounds) - rounding;
   }

   /**
    * Draws a three dimensional box whose dimensions are
    * described by the bounds.
    *
    * @param point
    *           the point
    * @param bounds
    *           the bounds
    * @return the signed distance
    * @see Utils#hypot(float, float, float)
    * @see Utils#max(float, float)
    * @see Utils#min(float, float)
    */
   public static float box (
         final Vec3 point,
         final Vec3 bounds ) {

      final float qx = Utils.abs(point.x) - bounds.x;
      final float qy = Utils.abs(point.y) - bounds.y;
      final float qz = Utils.abs(point.z) - bounds.z;

      return Utils.hypot(
            Utils.max(qx, 0.0f),
            Utils.max(qy, 0.0f),
            Utils.max(qz, 0.0f)) +
            Utils.min(Utils.max(qx, qy, qz), 0.0f);
   }

   /**
    * Draws a three dimensional box with rounded corners whose
    * dimensions are described by the bounds.
    *
    * @param point
    *           the point
    * @param bounds
    *           the bounds
    * @param rounding
    *           corner rounding factor
    * @return the signed distance
    * @see Sdf#box(Vec3, Vec3)
    */
   public static float box (
         final Vec3 point,
         final Vec3 bounds,
         final float rounding ) {

      return Sdf.box(point, bounds) - rounding;
   }

   /**
    * Draws a circle.
    *
    * @param point
    *           the point
    * @param bounds
    *           the bounds
    * @return the signed distance
    * @see Vec2#mag(Vec2)
    */
   public static float circle (
         final Vec2 point,
         final float bounds ) {

      return Vec2.mag(point) - bounds;
   }

   /**
    * Draws a conic gradient.
    *
    * @param pointx
    *           the x coordinate
    * @param pointy
    *           the y coordinate
    * @param radians
    *           the angular offset
    * @return the factor
    * @see Utils#mod1(float)
    * @see Utils#atan2(float, float)
    */
   public static float conic (
         final float pointx,
         final float pointy,
         final float radians ) {

      return Utils.mod1(
            (Utils.atan2(pointy, pointx) - radians) *
                  IUtils.ONE_TAU);
   }

   /**
    * Draws a conic gradient.
    *
    * @param point
    *           the point
    * @param radians
    *           the angular offset
    * @return the factor
    * @see Sdf#conic(float, float, float)
    */
   public static float conic (
         final Vec2 point,
         final float radians ) {

      return Sdf.conic(point.x, point.y, radians);
   }

   /**
    * Draws an ellipse, with bounds described by a vector. With
    * reference to
    * <a href="https://www.shadertoy.com/view/4sS3zz">
    * https://www.shadertoy.com/view/4sS3zz</a> .
    *
    * @param point
    *           the point
    * @param bounds
    *           the bounds
    * @return the signed distance
    * @see Utils#abs(float)
    * @see Utils#div(float, float)
    * @see Utils#acos(float)
    * @see Utils#hypot(float, float)
    * @see Math#cos(double)
    * @see Math#sin(double)
    * @see Math#copySign(float, float)
    * @see Math#pow(double, double)
    * @see Math#sqrt(double)
    */
   public static float ellipse (
         final Vec2 point,
         final Vec2 bounds ) {

      final float px0 = Utils.abs(point.x);
      final float py0 = Utils.abs(point.y);

      final boolean eval = px0 > py0;
      final float px1 = eval ? py0 : px0;
      final float py1 = eval ? px0 : py0;
      final float abx0 = eval ? bounds.y : bounds.x;
      final float aby0 = eval ? bounds.x : bounds.y;

      final float l = aby0 * aby0 - abx0 * abx0;
      final float m = abx0 * Utils.div(px1, l);
      final float n = aby0 * Utils.div(py1, l);
      final float msq = m * m;
      final float nsq = n * n;

      final float c = (msq + nsq - 1.0f) * IUtils.ONE_THIRD;
      final float ccb = c * c * c;
      final float m2n2 = msq * nsq;
      final float d = ccb + m2n2;
      final float q = d + m2n2;
      final float g = m + m * nsq;

      float co = 0.0f;
      if (d < 0.0f) {
         final float h = Utils.acos(Utils.div(q, ccb)) * IUtils.ONE_THIRD;
         final double s = Math.cos(h);
         final double t = Math.sin(h) * IUtils.SQRT_3_D;
         final double rx = Math.sqrt(msq - c * (s + t + 2.0d));
         final double ry = Math.sqrt(msq - c * (s - t + 2.0d));
         co = (float) ((ry + Math.copySign(rx, l) +
               Utils.abs(g) / (rx * ry) - m) * 0.5d);
      } else {
         final double h = 2.0d * m * n * Math.sqrt(d);
         final double s = Math.copySign(Math.pow(Utils.abs(q + h),
               IUtils.ONE_THIRD_D), q + h);
         final double u = Math.copySign(Math.pow(Utils.abs(q - h),
               IUtils.ONE_THIRD_D), q - h);
         final double rx = -s - u - c * 4.0d + 2.0d * msq;
         final double ry = (s - u) * IUtils.SQRT_3_D;
         final double rm = Math.sqrt(rx * rx + ry * ry);
         co = (float) ((ry / Math.sqrt(rm - rx) + 2.0d * g / rm - m) * 0.5d);
      }

      final float ry = aby0 * (float) Math.sqrt(1.0d - co * co);
      return Math.copySign(Utils.hypot(abx0 * co - px1, ry - py1), py1 - ry);
   }

   /**
    * Draws an ellipsoid.
    *
    * @param point
    *           the point
    * @param bounds
    *           the bounds
    * @return the signed distance
    */
   public static float ellipsoid (
         final Vec3 point,
         final Vec3 bounds ) {

      final float k1 = Utils.hypot(
            Utils.div(point.x, bounds.x * bounds.x),
            Utils.div(point.y, bounds.y * bounds.y),
            Utils.div(point.z, bounds.z * bounds.z));
      if (k1 == 0.0f) {
         return 0.0f;
      }

      final float k0 = Utils.hypot(
            Utils.div(point.x, bounds.x),
            Utils.div(point.y, bounds.y),
            Utils.div(point.z, bounds.z));
      return k0 * (k0 - 1.0f) / k1;

   }

   /**
    * Draws a hexagon.
    *
    * @param point
    *           the point
    * @param bounds
    *           the bounds
    * @return the signed distance
    * @see Utils#abs(float)
    * @see Utils#min(float, float)
    * @see Math#copySign(float, float)
    * @see Utils#hypot(float, float)
    * @see Utils#clamp(float, float, float)
    */
   public static float hexagon (
         final Vec2 point,
         final float bounds ) {

      final float px0 = Utils.abs(point.x);
      final float py0 = Utils.abs(point.y);
      final float dotkp2 = 2.0f * Utils.min(0.0f,
            -IUtils.SQRT_3_2 * px0 + IUtils.ONE_SQRT_3 * py0);
      final float px1 = px0 + dotkp2 * IUtils.SQRT_3_2;
      final float limit = 0.5f * bounds;
      final float py2 = py0 - dotkp2 * IUtils.ONE_SQRT_3 - bounds;
      return Math.copySign(Utils.hypot(
            px1 - Utils.clamp(px1, -limit, limit), py2), py2);
   }

   /**
    * Draws a hexagon with rounded corners.
    *
    * @param point
    *           the point
    * @param bounds
    *           the bounds
    * @param rounding
    *           the corner rounding
    * @return the signed distance
    * @see Sdf#hexagon(Vec2, float)
    */
   public static float hexagon (
         final Vec2 point,
         final float bounds,
         final float rounding ) {

      return Sdf.hexagon(point, bounds) - rounding;
   }

   /**
    * Finds the intersection between two shapes as represented
    * by factors.
    *
    * @param a
    *           the left factor
    * @param b
    *           the right factor
    * @return the intersection
    * @see Utils#max(float, float)
    */
   public static float intersect (
         final float a,
         final float b ) {

      return Utils.max(a, b);
   }

   /**
    * Finds the rounded intersection between two shapes as
    * represented by factors.
    *
    * @param a
    *           the left factor
    * @param b
    *           the right factor
    * @param radius
    *           the radius
    * @return the intersection
    * @see Utils#hypot(float, float)
    * @see Utils#max(float, float)
    * @see Utils#min(float, float)
    */
   public static float intersectRound (
         final float a,
         final float b,
         final float radius ) {

      return Utils.hypot(Utils.max(0.0f, a + radius),
            Utils.max(0.0f, b + radius))
            + Utils.min(Utils.max(a, b), -radius);
   }

   /**
    * Draws a line from the origin to the destination, where
    * the distance field is characterized by a third point's
    * distance from the line.
    *
    * @param point
    *           the point
    * @param origin
    *           the origin
    * @param dest
    *           the destination
    * @return the signed distance
    * @see Utils#hypot(float, float)
    * @see Utils#clamp01(float)
    */
   public static float line (
         final Vec2 point,
         final Vec2 origin,
         final Vec2 dest ) {

      /* Denominator: b - a */
      final float bax = dest.x - origin.x;
      final float bay = dest.y - origin.y;

      /* dot(ba, ba) */
      final float baba = bax * bax + bay * bay;

      /* Numerator: p - a */
      final float pax = point.x - origin.x;
      final float pay = point.y - origin.y;

      if (baba == 0.0f) {
         return Utils.hypot(pax, pay);
      }

      /* dot(pa, ba) */
      final float paba = pax * bax + pay * bay;

      /* Clamped scalar projection */
      final float h = Utils.clamp01(paba / baba);
      return Utils.hypot(
            pax - h * bax,
            pay - h * bay);
   }

   /**
    * Draws a line from the origin to the destination, where
    * the distance field is characterized by a third point's
    * distance from the line.
    *
    * @param point
    *           the point
    * @param origin
    *           the origin
    * @param dest
    *           the destination
    * @param rounding
    *           the rounding factor.
    * @return the signed distance
    * @see Sdf#line(Vec2, Vec2, Vec2)
    */
   public static float line (
         final Vec2 point,
         final Vec2 origin,
         final Vec2 dest,
         final float rounding ) {

      return Sdf.line(point, origin, dest) - rounding;
   }

   /**
    * Draws a line from the origin to the destination, where
    * the distance field is characterized by a third point's
    * distance from the line.
    *
    * @param point
    *           the point
    * @param origin
    *           the origin
    * @param dest
    *           the destination
    * @return the signed distance
    * @see Utils#hypot(float, float, float)
    * @see Utils#clamp01(float)
    */
   public static float line (
         final Vec3 point,
         final Vec3 origin,
         final Vec3 dest ) {

      /* Denominator: b - a */
      final float bax = dest.x - origin.x;
      final float bay = dest.y - origin.y;
      final float baz = dest.z - origin.z;

      /* dot(ba, ba) */
      final float baba = bax * bax + bay * bay + baz * baz;

      /* Numerator: p - a */
      final float pax = point.x - origin.x;
      final float pay = point.y - origin.y;
      final float paz = point.z - origin.z;

      if (baba == 0.0f) {
         return Utils.hypot(pax, pay, paz);
      }

      /* dot(pa, ba) */
      final float paba = pax * bax + pay * bay + paz * baz;

      /* Clamped scalar projection */
      final float h = Utils.clamp01(paba / baba);
      return Utils.hypot(
            pax - h * bax,
            pay - h * bay,
            paz - h * baz);
   }

   /**
    * Draws a line from the origin to the destination, where
    * the distance field is characterized by a third point's
    * distance from the line.
    *
    * @param point
    *           the point
    * @param origin
    *           the origin
    * @param dest
    *           the destination
    * @param rounding
    *           the rounding factor.
    * @return the signed distance
    * @see Sdf#line(Vec3, Vec3, Vec3)
    */
   public static float line (
         final Vec3 point,
         final Vec3 origin,
         final Vec3 dest,
         final float rounding ) {

      return Sdf.line(point, origin, dest) - rounding;
   }

   /**
    * Draws a regular polygon given a count of vertices. The
    * number of vertices should be greater than three.
    *
    * @param point
    *           the point
    * @param vertices
    *           number of vertices
    * @param bounds
    *           the bounds
    * @return the signed distance
    */
   @Experimental
   public static float polygon (
         final Vec2 point,
         final int vertices,
         final float bounds ) {

      if (vertices < 3) {
         return 0.0f;
      }

      final float a = IUtils.HALF_PI + Utils.atan2(point.y, point.x);
      final float b = IUtils.TAU / vertices;

      return (float) Math.cos(Utils.floor(0.5f + a / b) * b - a)
            * Utils.hypot(point.x, point.y)
            - bounds * 0.5f;
   }

   /**
    * Draws a polygon from a series of vertices. The number of
    * vertices is assumed to be greater than three. With
    * reference to
    * <a href="https://www.shadertoy.com/view/wdBXRW">
    * https://www.shadertoy.com/view/wdBXRW</a> .
    *
    * @param point
    *           the point
    * @param vertices
    *           the vertices
    * @return the signed distance
    * @see Utils#clamp01(float)
    * @see Utils#div(float, float)
    * @see Utils#min(float, float)
    * @see Math#sqrt(double)
    */
   public static float polygon (
         final Vec2 point,
         final Vec2[] vertices ) {

      final int len = vertices.length;
      if (len < 3) {
         return 0.0f;
      }

      /*
       * i begins at zero, and so the initial distance from 0 to
       * len - 1 does not need to be calculated prior to the
       * for-loop. d will be replaced by any lesser value, so it
       * makes sense to start with MAX_VALUE instead.
       */
      float d = Float.MAX_VALUE;
      float s = 1.0f;
      Vec2 curr;
      Vec2 prev = vertices[len - 1];

      for (int i = 0; i < len; ++i) {
         curr = vertices[i];

         final float ex = prev.x - curr.x;
         final float ey = prev.y - curr.y;

         final float wx = point.x - curr.x;
         final float wy = point.y - curr.y;

         final float denom = ex * ex + ey * ey;
         final float dotp = denom == 0.0f ? 0.0f
               : Utils.clamp01(
                     (wx * ex + wy * ey) / denom);
         final float bx = wx - ex * dotp;
         final float by = wy - ey * dotp;

         d = Utils.min(d, bx * bx + by * by);

         final boolean cx = point.y >= curr.y;
         final boolean cy = point.y < prev.y;
         final boolean cz = ex * wy > ey * wx;

         if (cx && cy && cz || !cx && !cy && !cz) {
            s = -s;
         }

         prev = curr;
      }

      return (float) (s * Math.sqrt(d));
   }

   /**
    * Draws a rounded polygon from an array of vertices,
    * assumed to be greater than two.
    *
    * @param point
    *           the point
    * @param vertices
    *           the vertices
    * @param rounding
    *           corner rounding
    * @return the signed distance
    * @see Sdf#polygon(Vec2, Vec2[])
    */
   public static float polygon (
         final Vec2 point,
         final Vec2[] vertices,
         final float rounding ) {

      return Sdf.polygon(point, vertices) - rounding;
   }

   /**
    * Draws a sphere.
    *
    * @param point
    *           the point
    * @param bounds
    *           the bounds
    * @return the signed distance
    */
   public static float sphere (
         final Vec3 point,
         final float bounds ) {

      return Vec3.mag(point) - bounds;
   }

   /**
    * Finds the subtraction of two shapes as represented by
    * factors.
    *
    * @param a
    *           the left factor
    * @param b
    *           the right factor
    * @return the subtraction
    * @see Utils#max(float, float)
    */
   public static float subtract (
         final float a,
         final float b ) {

      return Utils.max(-a, b);
   }

   /**
    * Finds the rounded subtraction of two shapes as
    * represented by factors.
    *
    * @param a
    *           the left factor
    * @param b
    *           the right factor
    * @param radius
    *           the radius
    * @return the subtraction
    * @see Sdf#intersectRound(float, float, float)
    */
   public static float subtractRound (
         final float a,
         final float b,
         final float radius ) {

      return Sdf.intersectRound(a, -b, radius);
   }

   /**
    * Draws a torus.
    *
    * @param point
    *           the point
    * @param radius
    *           the radius
    * @param thickness
    *           the thickness
    * @return the signed distance
    */
   public static float torus (
         final Vec2 point,
         final float radius,
         final float thickness ) {

      final float n = Vec2.mag(point) - radius;
      return (float) Math.sqrt(n * n) - thickness;
   }

   /**
    * Draws a torus.
    *
    * @param point
    *           the point
    * @param radius
    *           the radius
    * @param thickness
    *           the thickness
    * @return the signed distance
    */
   public static float torus (
         final Vec3 point,
         final float radius,
         final float thickness ) {

      return Utils.hypot(Utils.hypot(point.x, point.y)
            - radius, point.z) - thickness;
   }

   /**
    * Finds the union of two shapes as represented by factors.
    *
    * @param a
    *           the left factor
    * @param b
    *           the right factor
    * @return the union
    * @see Utils#min(float, float)
    */
   public static float union (
         final float a,
         final float b ) {

      return Utils.min(a, b);
   }

   /**
    * Finds the rounded union of two shapes as represented by
    * factors.
    *
    * @param a
    *           the left factor
    * @param b
    *           the right factor
    * @param radius
    *           the radius
    * @return the union
    * @see Utils#hypot(float, float)
    * @see Utils#max(float, float)
    * @see Utils#min(float, float)
    */
   public static float unionRound (
         final float a,
         final float b,
         final float radius ) {

      return Utils.max(Utils.min(a, b), radius) - Utils.hypot(
            Utils.min(0.0f, a - radius),
            Utils.min(0.0f, b - radius));
   }
}