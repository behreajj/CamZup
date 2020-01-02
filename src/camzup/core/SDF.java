package camzup.core;

/**
 * Facilitates implicit shapes created with signed distance
 * fields. Adapted from Inigo Quilez: <a href=
 * "https://www.iquilezles.org/www/articles/distfunctions2d/distfunctions2d.htm">2D
 * Distance Functions</a>, <a href=
 * "https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm">Distance
 * Functions</a>.
 *
 * @author Inigo Quilez
 */
public abstract class SDF {

   private static final Vec3 HEXAGON;

   private static final Vec3 OCTAGON;

   static {
      HEXAGON = new Vec3(-IUtils.SQRT_3_2, 0.5f, IUtils.ONE_SQRT_3);
      OCTAGON = new Vec3(-0.9238795f, 0.38268343f, 0.41421357f);
   }

   static float arc (
         final Vec2 point,
         final float angOffset,
         final float apperture,
         final float bounds,
         final float weight ) {

      return SDF.arc(point,
            (float) Math.cos(angOffset),
            (float) Math.sin(angOffset),

            (float) Math.cos(apperture * 0.5f),
            (float) Math.sin(apperture * 0.5f),

            bounds, weight);
   }

   static float arc (
         final Vec2 point,
         final float cosOff,
         final float sinOff,
         final float cosAprt,
         final float sinAprt,
         final float bounds,
         final float weight ) {

      /*
       * float ta = 3.14 * (0.5 + 0.5 * cos(iTime * 0.52 + 2.0));
       * float tb = 3.14 * (0.5 + 0.5 * cos(iTime * 0.31 + 2.0));
       * float rb = 0.15 * (0.5 + 0.5 * cos(iTime * 0.41 + 3.0));
       * float len = sdArc(uv, vec2(sin(ta), cos(ta)),
       * vec2(sin(tb), cos(tb)), 0.5, rb);
       */

      /*
       * p *= mat2(sca.x, sca.y, -sca.y, sca.x); p.x = abs(p.x);
       * float k = (scb.y * p.x > scb.x * p.y) ? dot(p.xy, scb) :
       * length(p.xy); return sqrt(dot(p, p) + ra * ra - 2.0 * ra
       * * k) - rb;
       */

      /* Multiply the point by a rotate z 2x2 matrix. */
      final float px0 = sinOff * point.x - cosOff * point.y;
      final float py0 = cosOff * point.x + sinOff * point.y;

      final float px1 = Utils.abs(px0);

      final double dotp = px1 * px1 + py0 * py0;
      final double k = cosAprt * px1 > sinAprt * py0
            ? px1 * sinAprt + py0 * cosAprt
            : Math.sqrt(dotp);

      return (float) Math.sqrt(
            dotp + bounds * bounds - 2.0d * bounds * k) - weight;
   }

   static float conic (
         final Vec2 point,
         final Vec2 origin,
         final float radians ) {

      // TODO: Should other functions like circle include
      // translation for you, or should this not include a
      // separate origin?
      return Utils.mod1(Utils.atan2(point.y - origin.y, point.x - origin.x)
            * IUtils.ONE_TAU - radians);
   }

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

   public static float box (
         final Vec2 point,
         final Vec2 bounds,
         final float rounding ) {

      return SDF.box(point, bounds) - rounding;
   }

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

   public static float box (
         final Vec3 point,
         final Vec3 bounds,
         final float rounding ) {

      return SDF.box(point, bounds) - rounding;
   }

   public static float circle (
         final Vec2 point,
         final float bounds ) {

      return Vec2.mag(point) - bounds;
   }

   public static float conic (
         final Vec2 point,
         final float radians ) {

      return Utils.mod1(Utils.atan2(point.y, point.x)
            * IUtils.ONE_TAU - radians);
   }

   public static float ellipse (
         final Vec2 point,
         final Vec2 bounds ) {

      /*
       * p = abs(p); if( p.x > p.y ) { p = p.yx; ab = ab.yx; }
       * float l = ab.y * ab.y - ab.x * ab.x; float m = ab.x * p.x
       * / l; float m2 = m * m; float n = ab.y * p.y / l; float n2
       * = n * n; float c = (m2 + n2 - 1.0) / 3.0; float c3 = c *
       * c * c; float q = c3 + m2 * n2 * 2.0; float d = c3 + m2 *
       * n2; float g = m + m * n2; float co; if(d< 0.0) { float h
       * = acos(q / c3) / 3.0; float s = cos(h); float t = sin(h)
       * * sqrt(3.0); float rx = sqrt( -c * (s + t + 2.0) + m2);
       * float ry = sqrt( -c * (s - t + 2.0) + m2); co = (ry +
       * sign(l) * rx + abs(g) / (rx * ry)- m) / 2.0; } else {
       * float h = 2.0 * m * n * sqrt(d); float s = sign(q + h) *
       * pow(abs(q + h), 1.0 / 3.0); float u = sign(q - h) *
       * pow(abs(q - h), 1.0 / 3.0); float rx = -s - u - c * 4.0 +
       * 2.0 * m2; float ry = (s - u) * sqrt(3.0); float rm =
       * sqrt(rx * rx + ry * ry); co = (ry / sqrt(rm - rx) +2.0 *
       * g / rm - m) / 2.0; } vec2 r = ab * vec2(co, sqrt(1.0 - co
       * * co)); return length(r - p) * sign(p.y - r.y);
       */

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
         final double s = Math.copySign(Math.pow(Math.abs(q + h),
               IUtils.ONE_THIRD_D), q + h);
         final double u = Math.copySign(Math.pow(Math.abs(q - h),
               IUtils.ONE_THIRD_D), q - h);
         final double rx = -s - u - c * 4.0d + 2.0d * msq;
         final double ry = (s - u) * IUtils.SQRT_3_D;
         final double rm = Math.sqrt(rx * rx + ry * ry);
         co = (float) ((ry / Math.sqrt(rm - rx) + 2.0d * g / rm - m) * 0.5d);
      }

      final float rx = abx0 * co;
      final float ry = aby0 * (float) Math.sqrt(1.0d - co * co);
      return Math.copySign(Utils.hypot(rx - px1, ry - py1), py1 - ry);
   }

   public static float ellipsoid (
         final Vec3 point,
         final Vec3 bounds ) {

      /*
       * float k0 = length(p / r); float k1 = length(p / (r * r));
       * return k0 * (k0 - 1.0) / k1;
       */

      // TODO: Needs testing.

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

   public static float hexagon (
         final Vec2 point,
         final float bounds ) {

      /*
       * const vec3 k = vec3(-0.866025404, 0.5, 0.577350269); p =
       * abs(p); p -= 2.0 * min(dot(k.xy, p), 0.0) * k.xy; p -=
       * vec2(clamp(p.x, -k.z * r, k.z * r), r); return length(p)
       * * sign(p.y);
       */

      final float px0 = Math.abs(point.x);
      final float py0 = Math.abs(point.y);

      final float dotkp2 = 2.0f * Utils.min(0.0f,
            SDF.HEXAGON.x * px0 + SDF.HEXAGON.y * py0);

      final float px1 = px0 - dotkp2 * SDF.HEXAGON.x;
      final float py1 = py0 - dotkp2 * SDF.HEXAGON.y;

      final float limit = SDF.HEXAGON.z * bounds;
      final float px2 = px1 - Utils.clamp(px1, -limit, limit);
      final float py2 = py1 - bounds;

      return Math.copySign(Utils.hypot(px2, py2), py2);
   }

   public static float hexagon (
         final Vec2 point,
         final float size,
         final float rounding ) {

      return SDF.hexagon(point, size) - rounding;
   }

   public static float intersect (
         final float a,
         final float b ) {

      return Utils.max(a, b);
   }

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

   public static float line (
         final Vec2 point,
         final Vec2 origin,
         final Vec2 dest,
         final float rounding ) {

      return SDF.line(point, origin, dest) - rounding;
   }

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

   public static float line (
         final Vec3 point,
         final Vec3 origin,
         final Vec3 dest,
         final float rounding ) {

      return SDF.line(point, origin, dest) - rounding;
   }

   public static float octagon (
         final Vec2 point,
         final float bounds ) {

      /*
       * const vec3 k = vec3(-0.9238795325, 0.3826834323,
       * 0.4142135623); p = abs(p); p -= 2.0 * min(dot(vec2(k.x,
       * k.y), p), 0.0) * vec2(k.x, k.y); p -= 2.0 *
       * min(dot(vec2(-k.x, k.y), p), 0.0) * vec2(-k.x, k.y); p -=
       * vec2(clamp(p.x, -k.z * r, k.z * r), r); return length(p)
       * * sign(p.y);
       */

      final float px0 = Utils.abs(point.x);
      final float py0 = Utils.abs(point.y);

      final float dot0 = 2.0f * Utils.min(0.0f,
            SDF.OCTAGON.x * px0 + SDF.OCTAGON.y * py0);

      final float px1 = px0 - dot0 * SDF.OCTAGON.x;
      final float py1 = py0 - dot0 * SDF.OCTAGON.y;

      final float dot1 = 2.0f * Utils.min(0.0f,
            SDF.OCTAGON.y * py1 - SDF.OCTAGON.x * px1);

      final float px2 = px1 + dot1 * SDF.OCTAGON.x;
      final float py2 = py1 - dot1 * SDF.OCTAGON.y;

      final float limit = SDF.OCTAGON.z * bounds;
      final float px3 = px2 - Utils.clamp(px2, -limit, limit);
      final float py3 = py2 - bounds;

      return Math.copySign(Utils.hypot(px3, py3), py3);
   }

   public static float octagon (
         final Vec2 point,
         final float bounds,
         final float rounding ) {

      return SDF.octagon(point, bounds) - rounding;
   }

   public static float polygon (
         final Vec2 point,
         final Vec2[] vertices ) {

      /*
       * float d = dot(p - v[0], p - v[0]); float s = 1.0; for(int
       * i = 0, j = N; i < N; j = i, i++) { vec2 e = v[j] - v[i];
       * vec2 w = p - v[i]; vec2 b = w - e * clamp(dot(w, e) /
       * dot(e, e), 0.0, 1.0); d = min(d, dot(b, b)); bvec3 c =
       * bvec3(p.y >= v[i].y, p.y < v[j].y, e.x * w.y > e.y *
       * w.x); if(all(c) || all(not(c))) { s *= -1.0; } } return s
       * * sqrt(d);
       */

      final int len = vertices.length;
      if (len < 3) {
         return 0.0f;
      }

      Vec2 curr = vertices[0];
      Vec2 prev = vertices[len - 1];

      final float diffx0 = point.x - curr.x;
      final float diffy0 = point.y - curr.y;

      float d = diffx0 * diffx0 + diffy0 * diffy0;
      float s = 1.0f;

      // TODO: Should this begin at i = 1 ?
      for (int i = 0; i < len; ++i) {
         curr = vertices[i];

         final float ex = prev.x - curr.x;
         final float ey = prev.y - curr.y;

         final float wx = point.x - curr.x;
         final float wy = point.y - curr.y;

         final float dotp = Utils.clamp01(Utils.div(
               wx * ex + wy * ey,
               ex * ex + ey * ey));
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

   public static float rhombus (
         final Vec2 point,
         final Vec2 bounds ) {

      /*
       * float ndot(vec2 a, vec2 b) { return a.x * b.x - a.y *
       * b.y; }
       *
       * vec2 q = abs(p); float h = clamp((-2.0 * ndot(q, b) +
       * ndot(b, b)) / dot(b, b), -1.0, 1.0); float d = length(q -
       * 0.5 * b * vec2(1.0 - h, 1.0 + h)); return d * sign(q.x *
       * b.y + q.y * b.x - b.x * b.y);
       */

      final float bxsq = bounds.x * bounds.x;
      final float bysq = bounds.y * bounds.y;

      final float qx = Utils.abs(point.x);
      final float qy = Utils.abs(point.y);
      final float ndotqb = qx * bounds.x - qy * bounds.y;

      final float h = Utils.clamp(Utils.div(
            bxsq - bysq - ndotqb - ndotqb,
            bxsq + bysq),
            -1.0f, 1.0f);

      return Math.copySign(
            Utils.hypot(
                  qx - 0.5f * bounds.x * (1.0f - h),
                  qy - 0.5f * bounds.y * (1.0f + h)),
            qx * bounds.y + qy * bounds.x - bounds.x * bounds.y);
   }

   public static float rhombus (
         final Vec2 point,
         final Vec2 bounds,
         final float rounding ) {

      return SDF.rhombus(point, bounds) - rounding;
   }

   public static float sphere (
         final Vec3 point,
         final float bounds ) {

      return Vec3.mag(point) - bounds;
   }

   public static float subtract (
         final float a,
         final float b ) {

      return Utils.max(-a, b);
   }

   public static float triangle ( final Vec2 point ) {

      /*
       * const float k = sqrt(3.0); p.x = abs(p.x) - 1.0; p.y =
       * p.y + 1.0 / k; if(p.x + k * p.y > 0.0) { p =
       * vec2(p.x-k*p.y,-k*p.x-p.y)/2.0; } p.x -= clamp(p.x, -2.0,
       * 0.0); return -length(p) * sign(p.y);
       */

      final float px0 = Utils.abs(point.x) - 1.0f;
      final float py0 = point.y + IUtils.ONE_SQRT_3;
      final float kpy0 = IUtils.SQRT_3 * py0;
      final boolean eval = px0 + kpy0 > 0.0f;
      final float px1 = eval ? 0.5f * (px0 - kpy0) : px0;
      final float py1 = eval ? 0.5f * (-IUtils.SQRT_3 * px0 - py0) : py0;
      final float px2 = px1 - Utils.clamp(px1, -2.0f, 0.0f);
      return Math.copySign(-Utils.hypot(px2, py1), py1);
   }

   public static float union (
         final float a,
         final float b ) {

      return Utils.min(a, b);
   }
}
