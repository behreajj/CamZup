package camzup.core;

import java.util.Arrays;
import java.util.TreeSet;

public class Face2Refactor implements Comparable < Face2Refactor > {
   public Edge2[] edges;

   public Face2Refactor ( ) { this.edges = new Edge2[] {}; }

   public Face2Refactor ( final Edge2... edges ) { this.set(edges); }

   public Face2Refactor ( final Vert2... verts ) { this.set(verts); }

   @Override
   public int compareTo ( final Face2Refactor face ) {

      return Face2Refactor.centerMean(this, new Vec2()).compareTo(Face2Refactor
         .centerMean(face, new Vec2()));
   }

   @Override
   public int hashCode ( ) { return Arrays.hashCode(this.edges); }

   public int length ( ) { return this.edges.length; }

   public Face2Refactor scale ( final float scale ) {

      return this.scaleGlobal(scale);
   }

   public Face2Refactor scale ( final Vec2 scale ) {

      return this.scaleGlobal(scale);
   }

   public Face2Refactor scaleGlobal ( final float scale ) {

      if ( scale != 0.0f ) {
         final int len = this.edges.length;
         for ( int i = 0; i < len; ++i ) {
            final Vec2 c = this.edges[i].origin.coord;
            Vec2.mul(c, scale, c);
         }
      }

      return this;
   }

   public Face2Refactor scaleGlobal ( final Vec2 scale ) {

      if ( Vec2.all(scale) ) {
         final int len = this.edges.length;
         for ( int i = 0; i < len; ++i ) {
            final Vec2 c = this.edges[i].origin.coord;
            Vec2.mul(c, scale, c);
         }
      }

      return this;
   }

   public Face2Refactor scaleLocal ( final float scale, final Vec2 center ) {

      Face2Refactor.centerMean(this, center);

      if ( scale != 0.0f ) {
         final int len = this.edges.length;
         for ( int i = 0; i < len; ++i ) {
            final Vec2 c = this.edges[i].origin.coord;
            Vec2.sub(c, center, c);
            Vec2.mul(c, scale, c);
            Vec2.add(c, center, c);
         }
      }

      return this;
   }

   public Face2Refactor scaleLocal ( final Vec2 scale, final Vec2 center ) {

      Face2Refactor.centerMean(this, center);

      if ( Vec2.all(scale) ) {
         final int len = this.edges.length;
         for ( int i = 0; i < len; ++i ) {
            final Vec2 c = this.edges[i].origin.coord;
            Vec2.sub(c, center, c);
            Vec2.mul(c, scale, c);
            Vec2.add(c, center, c);
         }
      }

      return this;
   }

   public Face2Refactor set ( final Edge2... edges ) {

      this.edges = edges;
      return this;
   }

   public Face2Refactor set ( final Vert2... verts ) {

      final int len = verts.length;
      this.edges = new Edge2[len];
      for ( int i = 0; i < len; ++i ) {
         this.edges[i] = new Edge2(verts[i], verts[ ( i + 1 ) % len]);
      }
      return this;
   }

   @Override
   public String toString ( ) { return this.toString(4); }

   public String toString ( final int places ) {

      final int len = this.edges.length;
      final int last = len - 1;
      final StringBuilder sb = new StringBuilder(len * 512);
      sb.append("{ vertices: [ ");
      for ( int i = 0; i < len; ++i ) {
         sb.append(this.edges[i].toString(places));
         if ( i < last ) { sb.append(',').append(' '); }
      }
      sb.append(" ] }");
      return sb.toString();
   }

   public Face2Refactor transform ( final Mat3 m ) {

      final int len = this.edges.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.edges[i].origin.coord;
         Mat3.mulPoint(m, c, c);
      }

      return this;
   }

   public Face2Refactor transform ( final Transform2 tr ) {

      final int len = this.edges.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.edges[i].origin.coord;
         Transform2.mulPoint(tr, c, c);
      }

      return this;
   }

   public Face2Refactor translate ( final Vec2 v ) {

      return this.translateGlobal(v);
   }

   public Face2Refactor translateGlobal ( final Vec2 v ) {

      final int len = this.edges.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.edges[i].origin.coord;
         Vec2.add(c, v, c);
      }

      return this;
   }

   public static Vec2 centerMean ( final Face2Refactor face,
      final Vec2 target ) {

      target.reset();
      final Edge2[] edges = face.edges;
      final int len = edges.length;
      for ( int i = 0; i < len; ++i ) {
         Vec2.add(target, edges[i].origin.coord, target);
      }
      return Vec2.div(target, len, target);
   }

   public static boolean isCCW ( final Face2Refactor face ) {

      return Face2Refactor.winding(face) > 0.0f;
   }

   public static boolean isCW ( final Face2Refactor face ) {

      return Face2Refactor.winding(face) < 0.0f;
   }

   public static float perimeter ( final Face2Refactor face ) {

      float sum = 0.0f;
      final Edge2[] edges = face.edges;
      final int len = edges.length;
      for ( int i = 0; i < len; ++i ) {
         sum += Edge2.mag(edges[i]);
      }
      return sum;
   }

   public static Vec2[] sharedCoords ( final Face2Refactor a,
      final Face2Refactor b ) {

      final TreeSet < Vec2 > aList = new TreeSet <>(Mesh.SORT_2);
      final Edge2[] aEdges = a.edges;
      final int aLen = aEdges.length;
      for ( int i = 0; i < aLen; ++i ) {
         aList.add(aEdges[i].origin.coord);
      }

      final TreeSet < Vec2 > bList = new TreeSet <>(Mesh.SORT_2);
      final Edge2[] bEdges = b.edges;
      final int bLen = bEdges.length;
      for ( int j = 0; j < bLen; ++j ) {
         bList.add(bEdges[j].origin.coord);
      }

      aList.retainAll(bList);

      return aList.toArray(new Vec2[aList.size()]);
   }

   public static float winding ( final Face2Refactor face ) {

      float wn = 0.0f;
      final Edge2[] edges = face.edges;
      final int len = edges.length;

      final Edge2 lastEdge = edges[len - 1];
      Vec2 prev = lastEdge.origin.coord;
      Vec2 curr = lastEdge.dest.coord;

      for ( int i = 0; i < len; ++i ) {
         final Vec2 next = edges[i].dest.coord;
         final float edge0x = curr.x - prev.x;
         final float edge0y = curr.y - prev.y;
         final float edge1x = next.x - curr.x;
         final float edge1y = next.y - curr.y;
         wn += edge0x * edge1y - edge0y * edge1x;
         prev = curr;
         curr = next;
      }
      return wn;
   }

}
