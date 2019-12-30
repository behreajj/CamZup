package camzup.core;

/**
 *
 * https://www.iquilezles.org/www/articles/distfunctions2d/distfunctions2d.htm
 * https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
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
         final Vec2 p,
         final Vec2 sca,
         final Vec2 scb,
         final float ra,
         final float rb ) {

      // p *= mat2(sca.x,sca.y,-sca.y,sca.x);
      // p.x = abs(p.x);
      // float k = (scb.y*p.x>scb.x*p.y) ? dot(p.xy,scb) :
      // length(p.xy);
      // return sqrt( dot(p,p) + ra*ra - 2.0*ra*k ) - rb;
      return 0.0f;
   }

   static float ellipse ( final Vec2 p, final Vec2 b ) {
      // p = abs(p); if( p.x > p.y ) {p=p.yx;ab=ab.yx;}
      // float l = ab.y*ab.y - ab.x*ab.x;
      // float m = ab.x*p.x/l; float m2 = m*m;
      // float n = ab.y*p.y/l; float n2 = n*n;
      // float c = (m2+n2-1.0)/3.0; float c3 = c*c*c;
      // float q = c3 + m2*n2*2.0;
      // float d = c3 + m2*n2;
      // float g = m + m*n2;
      // float co;
      // if( d<0.0 )
      // {
      // float h = acos(q/c3)/3.0;
      // float s = cos(h);
      // float t = sin(h)*sqrt(3.0);
      // float rx = sqrt( -c*(s + t + 2.0) + m2 );
      // float ry = sqrt( -c*(s - t + 2.0) + m2 );
      // co = (ry+sign(l)*rx+abs(g)/(rx*ry)- m)/2.0;
      // }
      // else
      // {
      // float h = 2.0*m*n*sqrt( d );
      // float s = sign(q+h)*pow(abs(q+h), 1.0/3.0);
      // float u = sign(q-h)*pow(abs(q-h), 1.0/3.0);
      // float rx = -s - u - c*4.0 + 2.0*m2;
      // float ry = (s - u)*sqrt(3.0);
      // float rm = sqrt( rx*rx + ry*ry );
      // co = (ry/sqrt(rm-rx)+2.0*g/rm-m)/2.0;
      // }
      // vec2 r = ab * vec2(co, sqrt(1.0-co*co));
      // return length(r-p) * sign(p.y-r.y);

      return 0.0f;
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
       * const vec3 k = vec3(-0.866025404,0.5,0.577350269); p =
       * abs(p); p -= 2.0 * min(dot(k.xy,p), 0.0) * k.xy; p -=
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

      final float h = Utils.clamp01(paba / baba);
      final float hax = pax - h * bax;
      final float hay = pay - h * bay;

      return Utils.hypot(hax, hay);
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

      final float h = Utils.clamp01(paba / baba);
      final float hax = pax - h * bax;
      final float hay = pay - h * bay;
      final float haz = paz - h * baz;

      return Utils.hypot(hax, hay, haz);
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
       * vec2(clamp(p.x, -k.z*r, k.z * r), r); return length(p) *
       * sign(p.y);
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

   public static float polygon ( final Vec2[] v, final Vec2 p ) {

      /*
       * float d = dot( p - v[0], p - v[0]); float s = 1.0;
       * for(int i = 0, j = N; i < N; j = i, i++) { vec2 e = v[j]
       * - v[i]; vec2 w = p - v[i]; vec2 b = w - e*clamp(
       * dot(w,e)/dot(e,e), 0.0, 1.0 ); d = min( d, dot(b,b) );
       * bvec3 c = bvec3(p.y >= v[i].y, p.y<v[j].y, e.x*w.y >
       * e.y*w.x); if( all(c) || all(not(c)) ) s*=-1.0; } return
       * s*sqrt(d);
       */

      // TODO: Needs testing...
      final int len = v.length;

      Vec2 curr = v[0];
      Vec2 prev = null;

      final float diffx0 = p.x - curr.x;
      final float diffy0 = p.y - curr.y;

      float d = diffx0 * diffx0 + diffy0 * diffy0;
      float s = 1.0f;

      for (int i = 0, j = len; i < len; j = i, i++) {
         prev = v[j];
         curr = v[i];

         final float ex = prev.x - curr.x;
         final float ey = prev.y - curr.y;

         final float wx = p.x - curr.x;
         final float wy = p.y - curr.y;

         final float dotp = Utils.clamp01(Utils.div(
               wx * ex + wy * ey,
               ex * ex + ey * ey));
         final float bx = wx - ex * dotp;
         final float by = wy - ey * dotp;

         d = Utils.min(d, bx * bx + by * by);

         final boolean cx = p.y >= curr.y;
         final boolean cy = p.y < prev.y;
         final boolean cz = ex * wy > ey * wx;

         if (cx && cy && cz || !cx && !cy && !cz) {
            s = -s;
         }
      }

      return (float) (s * Math.sqrt(d));
   }

   public static float sphere (
         final Vec3 point,
         final float bounds ) {

      return Vec3.mag(point) - bounds;
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

   public static float triangle (
         final Vec2 point,
         final Vec2 corner0,
         final Vec2 corner1,
         final Vec2 corner2 ) {

      // TODO: Needs testing...

      /*
       * vec2 e0 = p1-p0, e1 = p2-p1, e2 = p0-p2; vec2 v0 = p -p0,
       * v1 = p -p1, v2 = p -p2; vec2 pq0 = v0 - e0*clamp(
       * dot(v0,e0)/dot(e0,e0), 0.0, 1.0 ); vec2 pq1 = v1 -
       * e1*clamp( dot(v1,e1)/dot(e1,e1), 0.0, 1.0 ); vec2 pq2 =
       * v2 - e2 * clamp( dot(v2, e2)/dot(e2, de2), 0.0, 1.0 );
       * float s = sign( e0.x * e2.y - e0.y * e2.x ); vec2 d =
       * min(min(vec2(dot(pq0, pq0), s * (v0.x * e0.y - v0.y *
       * e0.x)), vec2(dot(pq1, pq1), s * (v1.x * e1.y - v1.y *
       * e1.x))), vec2(dot(pq2, pq2), s * (v2.x * e2.y - v2.y *
       * e2.x))); return -sqrt(d.x) * sign(d.y);
       **/

      /* Edge 0 := Corner 1 - Corner 0 */
      final float e0x = corner1.x - corner0.x;
      final float e0y = corner1.y - corner0.y;

      /* Edge 1 := Corner 2 - Corner 1 */
      final float e1x = corner2.x - corner1.x;
      final float e1y = corner2.y - corner1.y;

      /* Edge 2 := Corner 0 - Corner 2 */
      final float e2x = corner0.x - corner2.x;
      final float e2y = corner0.y - corner2.y;

      /* Diff 0 := Point - Corner 0 */
      final float v0x = point.x - corner0.x;
      final float v0y = point.y - corner0.y;

      /* Diff 1 := Point - Corner 1 */
      final float v1x = point.x - corner1.x;
      final float v1y = point.y - corner1.y;

      /* Diff 2 := Point - Corner 2 */
      final float v2x = point.x - corner2.x;
      final float v2y = point.y - corner2.y;

      final float dot0 = Utils.clamp01(Utils.div(
            v0x * e0x + v0y * e0y,
            e0x * e0x + e0y * e0y));
      final float pq0x = e0x * dot0;
      final float pq0y = e0y * dot0;

      final float dot1 = Utils.clamp01(Utils.div(
            v1x * e1x + v1y * e1y,
            e1x * e1x + e1y * e1y));
      final float pq1x = e1x * dot1;
      final float pq1y = e1y * dot1;

      final float dot2 = Utils.clamp01(Utils.div(
            v2x * e2x + v2y * e2y,
            e2x * e2x + e2y * e2y));
      final float pq2x = e2x * dot2;
      final float pq2y = e2y * dot2;

      final float s = e0x * e2y - e0y * e2x;
      final float ax = pq0x * pq0x + pq0y * pq0y;
      final float ay = Math.copySign(v0x * e0y - v0y * e0x, s);

      final float bx = pq1x * pq1x + pq1y * pq1y;
      final float by = Math.copySign(v1x * e1y - v1y * e1x, s);

      final float cx = pq2x * pq2x + pq2y * pq2y;
      final float cy = Math.copySign(v2x * e2y - v2y * e2x, s);

      final float dx = Utils.min(ax, bx, cx);
      final float dy = Utils.min(ay, by, cy);

      return Math.copySign(-(float) Math.sqrt(dx), dy);
   }
}
