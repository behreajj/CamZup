package camzup.core;

/**
 *
 * https://www.iquilezles.org/www/articles/distfunctions2d/distfunctions2d.htm
 * https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
 *
 * @author Inigo Quilez
 */
public abstract class SDF {

   static float arc ( final Vec2 p, final Vec2 sca, final Vec2 scb,
         final float ra, final float rb ) {

      // p *= mat2(sca.x,sca.y,-sca.y,sca.x);
      // p.x = abs(p.x);
      // float k = (scb.y*p.x>scb.x*p.y) ? dot(p.xy,scb) :
      // length(p.xy);
      // return sqrt( dot(p,p) + ra*ra - 2.0*ra*k ) - rb;
      return 0.0f;
   }

   static float box ( final Vec2 p, final Vec2 b ) {

      // vec2 d = abs(p)-b;
      // return length(max(d,vec2(0))) + min(max(d.x,d.y),0.0);
      return 0.0f;
   }

   static float box (
         final Vec3 p,
         final Vec3 b ) {

      final float dx = Utils.abs(p.x) - b.x;
      final float dy = Utils.abs(p.y) - b.y;
      final float dz = Utils.abs(p.z) - b.z;

      return Utils.min(Utils.max(dx, dy, dz), 0.0f) +
            Utils.hypot(
                  Utils.max(dx, 0.0f),
                  Utils.max(dy, 0.0f),
                  Utils.max(dz, 0.0f));
   }

   static float capsule (
         final Vec3 p,
         final Vec3 a,
         final Vec3 b,
         final float r ) {

      /* Denominator: b - a */
      final float bax = b.x - a.x;
      final float bay = b.y - a.y;
      final float baz = b.z - a.z;

      /* dot(ba, ba) */
      final float baba = bax * bax + bay * bay + baz * baz;

      /* Numerator: p - a */
      final float pax = p.x - a.x;
      final float pay = p.y - a.y;
      final float paz = p.z - a.z;

      if (baba == 0.0f) {
         return Utils.hypot(pax, pay, paz) - r;
      }

      /* dot(pa, ba) */
      final float paba = pax * bax + pay * bay + paz * baz;

      final float h = Utils.clamp01(paba / baba);
      final float hax = pax - h * bax;
      final float hay = pay - h * bay;
      final float haz = paz - h * baz;

      return Utils.hypot(hax, hay, haz) - r;
   }

   static float circle ( final Vec2 p, final float r ) {

      return Vec2.mag(p) - r;
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

   static float hexagon ( final Vec2 p, final float r ) {

      // const vec3 k = vec3(-0.866025404,0.5,0.577350269);
      // p = abs(p);
      // p -= 2.0*min(dot(k.xy,p),0.0)*k.xy;
      // p -= vec2(clamp(p.x, -k.z*r, k.z*r), r);
      // return length(p)*sign(p.y);
      return 0.0f;
   }

   static float line ( final Vec2 p, final Vec2 a, final Vec2 b ) {

      // vec2 pa = p-a, ba = b-a;
      // float h = clamp( dot(pa,ba)/dot(ba,ba), 0.0, 1.0 );
      // return length( pa - ba*h );
      return 0.0f;
   }

   static float pentagon ( final Vec2 p, final float r ) {

      // const vec3 k = vec3(0.809016994,0.587785252,0.726542528);
      // p.x = abs(p.x);
      // p -= 2.0*min(dot(vec2(-k.x,k.y),p),0.0)*vec2(-k.x,k.y);
      // p -= 2.0*min(dot(vec2( k.x,k.y),p),0.0)*vec2( k.x,k.y);
      // p -= vec2(clamp(p.x,-r*k.z,r*k.z),r);
      // return length(p)*sign(p.y);
      return 0.0f;
   }

   static float pie ( final Vec2 p, final Vec2 c, final float r ) {
      // p.x = abs(p.x);
      // float l = length(p) - r;
      // float m = length(p-c*clamp(dot(p,c),0.0,r)); // c =
      // sin/cos of the aperture
      // return max(l,m*sign(c.y*p.x-c.x*p.y));

      return 0.0f;
   }

   static float polygon ( final Vec2[] v, final Vec2 p ) {

      final int N = v.length;

      // float d = dot(p-v[0],p-v[0]);
      // float s = 1.0;
      // for( int i=0, j=N; i<N; j=i, i++ )
      // {
      // vec2 e = v[j] - v[i];
      // vec2 w = p - v[i];
      // vec2 b = w - e*clamp( dot(w,e)/dot(e,e), 0.0, 1.0 );
      // d = min( d, dot(b,b) );
      // bvec3 c = bvec3(p.y>=v[i].y,p.y<v[j].y,e.x*w.y>e.y*w.x);
      // if( all(c) || all(not(c)) ) s*=-1.0;
      // }
      // return s*sqrt(d);

      return 0.0f;
   }

   static float sphere (
         final Vec3 p,
         final float s ) {

      return Vec3.mag(p) - s;
   }

   static float triangle ( final Vec2 p, final Vec2 p0, final Vec2 p1,
         final Vec2 p2 ) {

      // vec2 e0 = p1-p0, e1 = p2-p1, e2 = p0-p2;
      // vec2 v0 = p -p0, v1 = p -p1, v2 = p -p2;
      // vec2 pq0 = v0 - e0*clamp( dot(v0,e0)/dot(e0,e0), 0.0, 1.0
      // );
      // vec2 pq1 = v1 - e1*clamp( dot(v1,e1)/dot(e1,e1), 0.0, 1.0
      // );
      // vec2 pq2 = v2 - e2*clamp( dot(v2,e2)/dot(e2,e2), 0.0, 1.0
      // );
      // float s = sign( e0.x*e2.y - e0.y*e2.x );
      // vec2 d = min(min(vec2(dot(pq0,pq0),
      // s*(v0.x*e0.y-v0.y*e0.x)),
      // vec2(dot(pq1,pq1), s*(v1.x*e1.y-v1.y*e1.x))),
      // vec2(dot(pq2,pq2), s*(v2.x*e2.y-v2.y*e2.x)));
      // return -sqrt(d.x)*sign(d.y);
      return 0.0f;
   }
}
