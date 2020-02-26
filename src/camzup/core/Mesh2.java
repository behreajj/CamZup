package camzup.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Organizes data needed to draw a two dimensional shape
 * using vertices and faces. Given that a mesh is primarily
 * a collection of references, it is initialized with null
 * arrays (coordinates, texture coordinates and indices).
 * These are not final, and so can be reassigned.
 */
public class Mesh2 extends Mesh {

   /**
    * Compares two face indices (an array of vertex indices) by
    * averaging the vectors referenced by them, then comparing
    * the averages.
    */
   protected static final class SortIndices2 implements Comparator < int[][] > {

      /**
       * The coordinates array.
       */
      final Vec2[] coords;

      /**
       * Internal vector used to store the average coordinate for
       * the left comparisand.
       */
      protected final Vec2 aAvg = new Vec2();

      /**
       * Internal vector used to store the average coordinate for
       * the right comparisand.
       */
      protected final Vec2 bAvg = new Vec2();

      /**
       * The default constructor.
       *
       * @param coords
       *           the coordinate array.
       */
      protected SortIndices2 ( final Vec2[] coords ) {

         this.coords = coords;
      }

      /**
       * Compares two faces indices.
       *
       * @param a
       *           the left comparisand
       * @param b
       *           the right comparisandS
       */
      @Override
      public int compare ( final int[][] a, final int[][] b ) {

         this.aAvg.reset();
         final int aLen = a.length;
         for (int i = 0; i < aLen; ++i) {
            Vec2.add(
                  this.aAvg,
                  this.coords[a[i][0]],
                  this.aAvg);
         }
         Vec2.div(this.aAvg, aLen, this.aAvg);

         this.bAvg.reset();
         final int bLen = b.length;
         for (int i = 0; i < bLen; ++i) {
            Vec2.add(
                  this.bAvg,
                  this.coords[b[i][0]],
                  this.bAvg);
         }
         Vec2.div(this.bAvg, bLen, this.bAvg);

         return this.aAvg.compareTo(this.bAvg);
      }

   }

   /**
    * The type of polygon produced by the static polygon
    * function.
    */
   public enum PolyType {

      /**
       * Create an n-sided polygon.
       */
      NGON (),

      /**
       * Create a triangle-based polygon.
       */
      TRI ();

      /**
       * The default constructor.
       */
      private PolyType () {

      }
   }

   /**
    * Default annulus for rings, 0.25 * Math.sqrt(2.0) ,
    * approximately 0.35355338 .
    */
   public static final float DEFAULT_ANNULUS = 0.35355338f;

   /**
    * Type of polygon to draw when it is not supplied to the
    * polygon function.
    */
   public static final PolyType DEFAULT_POLY_TYPE = PolyType.NGON;

   /**
    * Creates an arc from a start and stop angle.
    *
    * @param startAngle
    *           the start angle
    * @param stopAngle
    *           the stop angle
    * @param annulus
    *           the size of the opening
    * @param sectors
    *           number of sectors in a circle
    * @param target
    *           the output mesh
    * @return the arc
    */
   public static final Mesh2 arc (
         final float startAngle,
         final float stopAngle,
         final float annulus,
         final int sectors,
         final Mesh2 target ) {

      return Mesh2.arc(
            startAngle, stopAngle,
            annulus, sectors,
            Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates an arc from a start and stop angle. The
    * granularity of the approximation is dictated by the
    * number of sectors in a complete circle. The thickness of
    * the arc is described by the annulus.
    *
    * Useful where sectors may be faster than the Bezier curves
    * of
    * {@link Curve2#arc(float, float, float, camzup.core.Curve.ArcMode, Curve2)}
    * or where there is an issue rendering strokes.
    *
    * @param startAngle
    *           the start angle
    * @param stopAngle
    *           the stop angle
    * @param annulus
    *           the size of the opening
    * @param sectors
    *           number of sectors in a circle
    * @param poly
    *           the poly type
    * @param target
    *           the output mesh
    * @return the arc
    */
   public static final Mesh2 arc (
         final float startAngle,
         final float stopAngle,
         final float annulus,
         final int sectors,
         final PolyType poly,
         final Mesh2 target ) {

      target.name = "Arc";

      final double a1 = Utils.mod1(startAngle * IUtils.ONE_TAU_D);
      final double b1 = Utils.mod1(stopAngle * IUtils.ONE_TAU_D);
      final double arcLen1 = Utils.mod1(b1 - a1);
      if (arcLen1 < 0.00139d) {
         return Mesh2.ring(annulus, sectors, poly, target);
      }

      final int sctCount = Utils.ceilToInt(
            1 + (sectors < 3 ? 3 : sectors) * (float) arcLen1);
      final int sctCount2 = sctCount + sctCount;
      final Vec2[] vs = target.coords = Vec2.resize(target.coords, sctCount2);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
            sctCount2);

      final float annul = Utils.clamp(annulus,
            Utils.EPSILON, 1.0f - Utils.EPSILON);
      final double annRad = annul * 0.5d;

      final double toStep = 1.0d / (sctCount - 1.0d);
      final double origAngle = IUtils.TAU_D * a1;
      final double destAngle = IUtils.TAU_D * (a1 + arcLen1);

      for (int k = 0, i = 0, j = 1; k < sctCount; ++k, i += 2, j += 2) {
         final double theta = Utils.lerpUnclamped(
               origAngle, destAngle, k * toStep);
         final double cosa = Math.cos(theta);
         final double sina = Math.sin(theta);

         final Vec2 v0 = vs[i];
         v0.set((float) (0.5d * cosa),
               (float) (0.5d * sina));

         final Vec2 v1 = vs[j];
         v1.set((float) (annRad * cosa),
               (float) (annRad * sina));

         final Vec2 vt0 = vts[i];
         vt0.x = v0.x + 0.5f;
         vt0.y = 0.5f - v0.y;

         final Vec2 vt1 = vts[j];
         vt1.x = v1.x + 0.5f;
         vt1.y = 0.5f - v1.y;
      }

      int len;

      switch (poly) {

         case NGON:

            len = sctCount - 1;
            target.faces = new int[len][4][2];

            for (int k = 0, i = 0, j = 1; k < len; ++k, i += 2, j += 2) {
               final int m = i + 2;
               final int n = j + 2;
               final int[][] f = target.faces[k];
               f[0][0] = i;
               f[0][1] = i;
               f[1][0] = m;
               f[1][1] = m;
               f[2][0] = n;
               f[2][1] = n;
               f[3][0] = j;
               f[3][1] = j;
            }

            break;

         case TRI:

         default:

            len = sctCount2 - 2;
            target.faces = new int[len][3][2];

            for (int i = 0, j = 1; i < len; i += 2, j += 2) {
               final int m = i + 2;
               final int n = j + 2;

               final int[][] f0 = target.faces[i];
               f0[0][0] = i;
               f0[0][1] = i;
               f0[1][0] = m;
               f0[1][1] = m;
               f0[2][0] = j;
               f0[2][1] = j;

               final int[][] f1 = target.faces[j];
               f1[0][0] = m;
               f1[0][1] = m;
               f1[1][0] = n;
               f1[1][1] = n;
               f1[2][0] = j;
               f1[2][1] = j;
            }

      }

      return target;
   }

   /**
    * Creates an arc from a start and stop angle.
    *
    * @param startAngle
    *           the start angle
    * @param stopAngle
    *           the stop angle
    * @param annulus
    *           the size of the opening
    * @param target
    *           the output mesh
    * @return the arc
    */
   public static final Mesh2 arc (
         final float startAngle,
         final float stopAngle,
         final float annulus,
         final Mesh2 target ) {

      return Mesh2.arc(
            startAngle, stopAngle, annulus,
            IMesh.DEFAULT_CIRCLE_SECTORS,
            Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates an arc from a start and stop angle.
    *
    * @param startAngle
    *           the start angle
    * @param stopAngle
    *           the stop angle
    * @param sectors
    *           number of sectors in a circle
    * @param target
    *           the output mesh
    * @return the arc
    */
   public static final Mesh2 arc (
         final float startAngle,
         final float stopAngle,
         final int sectors,
         final Mesh2 target ) {

      return Mesh2.arc(
            startAngle, stopAngle,
            Mesh2.DEFAULT_ANNULUS, sectors,
            Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates an arc from a start and stop angle.
    *
    * @param startAngle
    *           the start angle
    * @param stopAngle
    *           the stop angle
    * @param target
    *           the output mesh
    * @return the arc
    */
   public static final Mesh2 arc (
         final float startAngle,
         final float stopAngle,
         final Mesh2 target ) {

      return Mesh2.arc(
            startAngle, stopAngle,
            Mesh2.DEFAULT_ANNULUS,
            IMesh.DEFAULT_CIRCLE_SECTORS,
            Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates an arc from a stop angle. The start angle is
    * presumed to be 0.0 degrees.
    *
    * @param stopAngle
    *           the stop angle
    * @param target
    *           the output mesh
    * @return the arc
    */
   public static final Mesh2 arc (
         final float stopAngle,
         final Mesh2 target ) {

      return Mesh2.arc(
            0.0f, stopAngle,
            Mesh2.DEFAULT_ANNULUS,
            IMesh.DEFAULT_CIRCLE_SECTORS,
            Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Calculates the dimensions of an Axis-Aligned Bounding Box
    * (AABB) encompassing the mesh.
    *
    * @param mesh
    *           the mesh
    * @param target
    *           the output dimensions
    * @param lb
    *           the lower bound
    * @param ub
    *           the upper bound
    * @return the dimensions
    */
   public static Vec2 calcDimensions (
         final Mesh2 mesh,
         final Vec2 target,
         final Vec2 lb,
         final Vec2 ub ) {

      lb.set(
            Float.MAX_VALUE,
            Float.MAX_VALUE);
      ub.set(
            Float.MIN_VALUE,
            Float.MIN_VALUE);

      final Vec2[] coords = mesh.coords;
      final int len = coords.length;

      for (int i = 0; i < len; ++i) {

         final Vec2 coord = coords[i];
         final float x = coord.x;
         final float y = coord.y;

         /* Min, max need separate if checks, not if-else. */
         if (x < lb.x) {
            lb.x = x;
         }

         if (x > ub.x) {
            ub.x = x;
         }

         if (y < lb.y) {
            lb.y = y;
         }

         if (y > ub.y) {
            ub.y = y;
         }
      }

      return Vec2.sub(ub, lb, target);
   }

   /**
    * Creates a regular convex polygon, approximating a circle.
    *
    * @param target
    *           the output mesh
    * @return the polygon
    * @see Mesh2#polygon(int, PolyType, Mesh2)
    */
   public static Mesh2 circle (
         final Mesh2 target ) {

      return Mesh2.polygon(
            IMesh.DEFAULT_CIRCLE_SECTORS,
            Mesh2.DEFAULT_POLY_TYPE,
            target);
   }

   /**
    * Creates a regular convex polygon, approximating a circle.
    *
    * @param poly
    *           the polygon type
    * @param target
    *           the output mesh
    * @return the polygon
    * @see Mesh2#polygon(int, PolyType, Mesh2)
    */
   public static Mesh2 circle (
         final Mesh2.PolyType poly,
         final Mesh2 target ) {

      return Mesh2.polygon(
            IMesh.DEFAULT_CIRCLE_SECTORS,
            poly, target);
   }

   /**
    * Creates a subdvided plane. Useful for meshes which later
    * will be augmented by noise or height maps to simulate
    * terrain.
    *
    * @param cols
    *           number of columns
    * @param rows
    *           number of rows
    * @param target
    *           the output mesh
    * @return the plane
    */
   public static final Mesh2 plane (
         final int cols,
         final int rows,
         final Mesh2 target ) {

      return Mesh2.plane(cols, rows, Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a plane subdivided into either tris or quads,
    * depending on the polygon type. Useful for meshes which
    * later will be augmented by noise or height maps to
    * simulate terrain.
    *
    * @param cols
    *           number of columns
    * @param rows
    *           number of rows
    * @param poly
    *           the polygon type
    * @param target
    *           the output mesh
    * @return the plane
    */
   public static final Mesh2 plane (
         final int cols,
         final int rows,
         final PolyType poly,
         final Mesh2 target ) {

      target.name = "Plane";

      final int rval = rows < 1 ? 1 : rows;
      final int cval = cols < 1 ? 1 : cols;

      final int rval1 = rval + 1;
      final int cval1 = cval + 1;

      final float iToStep = 1.0f / rval;
      final float jToStep = 1.0f / cval;

      final Vec2[] vs = target.coords = Vec2.resize(
            target.coords, rval1 * cval1);
      final Vec2[] vts = target.texCoords = Vec2.resize(
            target.texCoords, vs.length);
      final int flen = rval * cval;

      /* Calculate x values in separate loop. */
      final float[] xs = new float[cval1];
      final float[] us = new float[cval1];
      for (int j = 0; j < cval1; ++j) {
         final float xPrc = j * jToStep;
         xs[j] = xPrc - 0.5f;
         us[j] = xPrc;
      }

      for (int k = 0, i = 0; i < rval1; ++i) {
         final float yPrc = i * iToStep;
         final float y = yPrc - 0.5f;
         final float v = 1.0f - yPrc;

         for (int j = 0; j < cval1; ++j, ++k) {
            vs[k].set(xs[j], y);
            vts[k].set(us[j], v);
         }
      }

      switch (poly) {

         case NGON:

            target.faces = new int[flen][4][2];

            for (int k = 0, i = 0; i < rval; ++i) {
               final int noff0 = i * cval1;
               final int noff1 = (i + 1) * cval1;

               for (int j = 0; j < cval; ++j, ++k) {
                  final int n00 = noff0 + j;
                  final int n10 = n00 + 1;
                  final int n01 = noff1 + j;
                  final int n11 = n01 + 1;

                  final int[][] f = target.faces[k];

                  f[0][0] = n00;
                  f[0][1] = n00;

                  f[1][0] = n10;
                  f[1][1] = n10;

                  f[2][0] = n11;
                  f[2][1] = n11;

                  f[3][0] = n01;
                  f[3][1] = n01;
               }
            }

            break;

         case TRI:

         default:

            target.faces = new int[flen + flen][3][2];

            for (int k = 0, i = 0; i < rval; ++i) {
               final int noff0 = i * cval1;
               final int noff1 = (i + 1) * cval1;

               for (int j = 0; j < cval; ++j, k += 2) {
                  final int n00 = noff0 + j;
                  final int n10 = n00 + 1;
                  final int n01 = noff1 + j;
                  final int n11 = n01 + 1;

                  final int[][] f0 = target.faces[k];
                  f0[0][0] = n00;
                  f0[0][1] = n00;

                  f0[1][0] = n10;
                  f0[1][1] = n10;

                  f0[2][0] = n11;
                  f0[2][1] = n11;

                  final int[][] f1 = target.faces[k + 1];
                  f1[0][0] = n11;
                  f1[0][1] = n11;

                  f1[1][0] = n01;
                  f1[1][1] = n01;

                  f1[2][0] = n00;
                  f1[2][1] = n00;
               }
            }

      }

      return target;
   }

   /**
    * Creates a subdvided plane. Useful for meshes which later
    * will be augmented by noise or height maps to simulate
    * terrain.
    *
    * @param div
    *           subdivisions
    * @param target
    *           the output mesh
    * @return the plane
    */
   public static final Mesh2 plane (
         final int div,
         final Mesh2 target ) {

      return Mesh2.plane(div, div, Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a regular convex polygon.
    *
    * @param sectors
    *           the number of sides
    *
    * @param target
    *           the output mesh
    * @return the polygon
    */
   public static Mesh2 polygon (
         final int sectors,
         final Mesh2 target ) {

      return Mesh2.polygon(sectors, Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a regular convex polygon.
    *
    * @param sectors
    *           the number of sides
    * @param poly
    *           the polygon type
    * @param target
    *           the output mesh
    * @return the polygon
    */
   public static Mesh2 polygon (
         final int sectors,
         final PolyType poly,
         final Mesh2 target ) {

      /*
       * Polar coordinates need to be more precise, given that
       * they are scaled up and can impact SVG rendering.
       */

      target.name = "Polygon";

      final int seg = sectors < 3 ? 3 : sectors;
      final boolean isNgon = poly == PolyType.NGON;
      final int newLen = isNgon ? seg : seg + 1;
      final double toTheta = IUtils.TAU_D / seg;

      final Vec2[] vs = target.coords = Vec2.resize(target.coords, newLen);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
            newLen);

      switch (poly) {
         case NGON:

            target.faces = new int[1][seg][2];
            final int[][] ngon = target.faces[0];

            for (int i = 0; i < seg; ++i) {
               final double theta = i * toTheta;

               final Vec2 v = vs[i];
               v.set((float) (0.5d * Math.cos(theta)),
                     (float) (0.5d * Math.sin(theta)));

               final Vec2 vt = vts[i];
               vt.x = v.x + 0.5f;
               vt.y = 0.5f - v.y;

               ngon[i][0] = i;
               ngon[i][1] = i;
            }

            break;

         case TRI:

         default:

            final int[][][] fs = target.faces = new int[seg][3][2];
            vs[0].set(0.0f, 0.0f);
            vts[0].set(0.5f, 0.5f);

            for (int i = 0, j = 1; i < seg; ++i, ++j) {
               final double theta = i * toTheta;

               final Vec2 v = vs[j];
               v.set((float) (0.5d * Math.cos(theta)),
                     (float) (0.5d * Math.sin(theta)));

               final Vec2 vt = vts[j];
               vt.x = v.x + 0.5f;
               vt.y = 0.5f - v.y;

               final int k = 1 + j % seg;
               final int[][] f = fs[i];
               f[0][0] = 0;
               f[0][1] = 0;

               f[1][0] = j;
               f[1][1] = j;

               f[2][0] = k;
               f[2][1] = k;
            }

      }

      return target;
   }

   /**
    * Creates a regular convex polygon with an opening in its
    * center. The annulus describes the relative size of this
    * opening.
    *
    * @param sectors
    *           the number of sides
    * @param annulus
    *           the size of the opening
    * @param target
    *           the output type
    * @return the ring
    */
   public static final Mesh2 ring (
         final float annulus,
         final int sectors,
         final Mesh2 target ) {

      return Mesh2.ring(annulus, sectors, Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a regular convex polygon with an opening in its
    * center. The annulus describes the relative size of this
    * opening. When the polygon type is NGON, the ring will be
    * composed of quads; otherwise, tris.
    *
    * @param annulus
    *           the size of the opening
    * @param sectors
    *           the number of sides
    * @param poly
    *           the polygon type
    * @param target
    *           the output type
    * @return the ring
    */
   public static final Mesh2 ring (
         final float annulus,
         final int sectors,
         final PolyType poly,
         final Mesh2 target ) {

      target.name = "Ring";

      final boolean isNgon = poly == PolyType.NGON;
      final int seg = sectors < 3 ? 3 : sectors;
      final int seg2 = seg + seg;
      final float annul = Utils.clamp(annulus,
            Utils.EPSILON, 1.0f - Utils.EPSILON);

      final double toTheta = IUtils.TAU_D / seg;
      final double annRad = annul * 0.5d;

      final Vec2[] vs = target.coords = Vec2.resize(target.coords, seg2);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords, seg2);
      target.faces = isNgon ? new int[seg][4][2]
            : new int[seg2][3][2];

      for (int k = 0, i = 0, j = 1; k < seg; ++k, i += 2, j += 2) {
         final double theta = k * toTheta;
         final double cosa = Math.cos(theta);
         final double sina = Math.sin(theta);

         final Vec2 v0 = vs[i];
         v0.set((float) (0.5d * cosa),
               (float) (0.5d * sina));

         final Vec2 v1 = vs[j];
         v1.set((float) (annRad * cosa),
               (float) (annRad * sina));

         final Vec2 vt0 = vts[i];
         vt0.x = v0.x + 0.5f;
         vt0.y = 0.5f - v0.y;

         final Vec2 vt1 = vts[j];
         vt1.x = v1.x + 0.5f;
         vt1.y = 0.5f - v1.y;

         final int m = (i + 2) % seg2;
         final int n = (j + 2) % seg2;

         if (isNgon) {
            final int[][] f = target.faces[k];
            f[0][0] = i;
            f[0][1] = i;
            f[1][0] = m;
            f[1][1] = m;
            f[2][0] = n;
            f[2][1] = n;
            f[3][0] = j;
            f[3][1] = j;
         } else {
            final int[][] f0 = target.faces[i];
            f0[0][0] = i;
            f0[0][1] = i;
            f0[1][0] = m;
            f0[1][1] = m;
            f0[2][0] = j;
            f0[2][1] = j;

            final int[][] f1 = target.faces[j];
            f1[0][0] = m;
            f1[0][1] = m;
            f1[1][0] = n;
            f1[1][1] = n;
            f1[2][0] = j;
            f1[2][1] = j;
         }
      }

      return target;
   }

   /**
    * Creates a regular convex polygon with an opening in its
    * center.
    *
    * @param annulus
    *           the size of the opening
    * @param target
    *           the output mesh
    * @return the ring
    */
   public static final Mesh2 ring (
         final float annulus,
         final Mesh2 target ) {

      return Mesh2.ring(
            annulus,
            IMesh.DEFAULT_CIRCLE_SECTORS,
            Mesh2.DEFAULT_POLY_TYPE,
            target);
   }

   /**
    * Creates a regular convex polygon with an opening in its
    * center.
    *
    * @param sectors
    *           the number of sides
    * @param target
    *           the output mesh
    * @return the ring
    */
   public static final Mesh2 ring (
         final int sectors,
         final Mesh2 target ) {

      return Mesh2.ring(
            Mesh2.DEFAULT_ANNULUS,
            sectors,
            Mesh2.DEFAULT_POLY_TYPE,
            target);
   }

   /**
    * Creates a regular convex polygon with an opening in its
    * center.
    *
    * @param target
    *           the output mesh
    * @return the ring
    */
   public static final Mesh2 ring ( final Mesh2 target ) {

      return Mesh2.ring(
            Mesh2.DEFAULT_ANNULUS,
            IMesh.DEFAULT_CIRCLE_SECTORS,
            Mesh2.DEFAULT_POLY_TYPE,
            target);
   }

   /**
    * Creates a square.
    *
    * @param target
    *           the output mesh
    * @return the square
    */
   public static final Mesh2 square ( final Mesh2 target ) {

      return Mesh2.square(Mesh2.DEFAULT_POLY_TYPE, target);
   }

   /**
    * Creates a square.
    *
    * @param target
    *           the output mesh
    * @param poly
    *           the polygon type
    * @return the square
    */
   public static final Mesh2 square (
         final PolyType poly,
         final Mesh2 target ) {

      target.name = "Square";

      target.coords = Vec2.resize(target.coords, 4);
      target.coords[0].set(-0.5f, 0.5f);
      target.coords[1].set(0.5f, 0.5f);
      target.coords[2].set(0.5f, -0.5f);
      target.coords[3].set(-0.5f, -0.5f);

      target.texCoords = Vec2.resize(target.texCoords, 4);
      target.texCoords[0].set(0.0f, 0.0f);
      target.texCoords[1].set(1.0f, 0.0f);
      target.texCoords[2].set(1.0f, 1.0f);
      target.texCoords[3].set(0.0f, 1.0f);

      target.faces = new int[][][] {
            { { 0, 0 }, { 1, 1 }, { 2, 2 }, { 3, 3 } } };

      return target;
   }

   /**
    * Creates a triangle
    *
    * @param target
    *           the output mesh
    * @return the triangle
    */
   public static final Mesh2 triangle ( final Mesh2 target ) {

      target.name = "Triangle";

      target.coords = Vec2.resize(target.coords, 3);
      target.coords[0].set(0.0f, 0.5f);
      target.coords[1].set(-0.4330127f, -0.25f);
      target.coords[2].set(0.4330127f, -0.25f);

      target.texCoords = Vec2.resize(target.texCoords, 3);
      target.texCoords[0].set(0.5f, 0.0f);
      target.texCoords[1].set(0.0669873f, 0.75f);
      target.texCoords[2].set(0.9330127f, 0.75f);

      target.faces = new int[][][] { { { 0, 0 }, { 1, 1 }, { 2, 2 } } };

      return target;
   }

   /**
    * Restructures the mesh so that each face index refers to
    * unique data, indifferent to redundancies. As a
    * consequence, coord and texture coordinate are of equal
    * length and face indices are easier to read and
    * understand. Useful prior to subdividing edges, or to make
    * mesh similar to Unity meshes. Similar to 'ripping'
    * vertices or 'tearing' edges in Blender.
    *
    * @param source
    *           the source mesh
    * @param target
    *           the target mesh
    * @return the mesh
    */
   public static Mesh2 uniformData (
         final Mesh2 source,
         final Mesh2 target ) {

      target.name = source.name;

      final int len0 = source.faces.length;
      final ArrayList < Vec2 > vs = new ArrayList <>();
      final ArrayList < Vec2 > vts = new ArrayList <>();

      target.faces = new int[len0][][];

      for (int k = 0, i = 0; i < len0; ++i) {

         final int[][] fs0 = source.faces[i];
         final int len1 = fs0.length;
         final int[][] trgfs0 = target.faces[i] = new int[len1][2];

         for (int j = 0; j < len1; ++j, ++k) {

            final int[] fs1 = fs0[j];

            vs.add(new Vec2(source.coords[fs1[0]]));
            vts.add(new Vec2(source.texCoords[fs1[1]]));

            trgfs0[j][0] = k;
            trgfs0[j][1] = k;
         }
      }

      target.coords = vs.toArray(new Vec2[vs.size()]);
      target.texCoords = vts.toArray(new Vec2[vts.size()]);
      return target;
   }

   /**
    * An array of coordinates in the mesh.
    */
   public Vec2[] coords;

   /**
    * The texture (UV) coordinates that describe how an image
    * is mapped onto the geometry of the mesh. Typically in the
    * range [0.0, 1.0].
    */
   public Vec2[] texCoords;

   /**
    * The default constructor.
    */
   public Mesh2 () {

      super();
   }

   /**
    * Creates a mesh from arrays of faces, coordinates and
    * texture coordinates. The mesh's arrays are set by
    * reference, not by value.
    *
    * @param faces
    *           the faces array
    * @param coords
    *           the coordinates array
    * @param texCoords
    *           the texture coordinates array
    */
   public Mesh2 (
         final int[][][] faces,
         final Vec2[] coords,
         final Vec2[] texCoords ) {

      super();
      this.set(faces, coords, texCoords);
   }

   /**
    * Constructs a copy of the source mesh.
    *
    * @param source
    *           the source mesh
    */
   public Mesh2 ( final Mesh2 source ) {

      super();
      this.set(source);
   }

   /**
    * Creates a named mesh.
    *
    * @param name
    *           the mesh name
    */
   public Mesh2 ( final String name ) {

      super(name);
   }

   /**
    * Creates a named mesh from arrays of faces, coordinates
    * and texture coordinates. The mesh's arrays are set by
    * reference, not by value.
    *
    * @param name
    *           the mesh name
    * @param faces
    *           the faces array
    * @param coords
    *           the coordinates array
    * @param texCoords
    *           the texture coordinates array
    */
   public Mesh2 (
         final String name,
         final int[][][] faces,
         final Vec2[] coords,
         final Vec2[] texCoords ) {

      super(name);
      this.set(faces, coords, texCoords);
   }

   /**
    * Returns a String of Python code targeted toward the
    * Blender 2.8x API. This code is brittle and is used for
    * internal testing purposes, i.e., to compare how mesh
    * geometry looks in Blender (the control) vs. in the
    * library (the test).
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode () {

      final StringBuilder result = new StringBuilder();
      result.append("{\"name\": \"")
            .append(this.name)
            .append("\", \"material_index\": ")
            .append(this.materialIndex)
            .append(", \"vertices\": [");

      final int vlen = this.coords.length;
      final int vlast = vlen - 1;
      for (int i = 0; i < vlen; ++i) {
         result.append(this.coords[i].toBlenderCode(0.0f));
         if (i < vlast) {
            result.append(',').append(' ');
         }
      }

      result.append("], \"faces\": [");

      final int flen = this.faces.length;
      final int flast = flen - 1;
      for (int j = 0; j < flen; ++j) {
         final int[][] vrtInd = this.faces[j];
         final int vrtIndLen = vrtInd.length;
         final int vrtLast = vrtIndLen - 1;

         result.append('(');
         for (int k = 0; k < vrtIndLen; ++k) {
            result.append(vrtInd[k][0]);
            if (k < vrtLast) {
               result.append(',').append(' ');
            }
         }
         result.append(')');

         if (j < flast) {
            result.append(',').append(' ');
         }
      }

      result.append(']').append('}');
      return result.toString();
   }

   /**
    * Renders the mesh as a string following the SVG file
    * format.
    *
    * @return the string
    */
   String toSvgString () {

      // TODO: Create internal and external toSvgStrings so that
      // you can create a svg from a mesh and mesh entity
      // independently of the renderer. Make this one
      // toSvgStringInternal with package level access.

      final StringBuilder result = new StringBuilder();

      final int[][][] fs = this.faces;
      final Vec2[] vs = this.coords;
      final int flen0 = fs.length;
      for (int i = 0; i < flen0; ++i) {
         final int[][] f = fs[i];
         final int flen1 = f.length;

         result.append("<path d=\"M ")
               .append(vs[f[0][0]].toSvgString())
               .append(' ');

         for (int j = 1; j < flen1; ++j) {
            result.append('L').append(' ')
                  .append(vs[f[j][0]].toSvgString())
                  .append(' ');
         }

         result.append("Z\"></path>\n");
      }

      return result.toString();
   }

   /**
    * Sets StringBuilders of C# code targeted toward the Unity
    * 2019 API. This code is brittle and is used for internal
    * testing purposes, i.e., to compare how mesh geometry
    * looks in Blender (the control) vs. in the library (the
    * test).
    *
    * @param vs
    *           the vertices string builder
    * @param vts
    *           the texture coordinates string builder
    * @param vns
    *           the normals string builder
    * @param tris
    *           the triangle indices string builder
    */
   @Experimental
   void toUnityCode (
         final StringBuilder vs,
         final StringBuilder vts,
         final StringBuilder vns,
         final StringBuilder tris ) {

      final int len0 = this.faces.length;
      for (int k = 0, i = 0; i < len0; ++i) {

         final int[][] fs0 = this.faces[i];
         final int len1 = fs0.length;

         for (int j = 0; j < len1; ++j, ++k) {

            final int[] fs1 = fs0[j];

            final Vec2 v = this.coords[fs1[0]];
            final Vec2 vt = this.texCoords[fs1[1]];

            vs.append(v.toUnityCode(0.0f));
            vts.append(vt.toUnityCode());
            vns.append("new Vector3(0.0f, 0.0f, 1.0f)");
            tris.append(k);

            if (j < len1 - 1) {
               vs.append(',').append(' ').append('\n');
               vts.append(',').append(' ').append('\n');
               vns.append(',').append(' ').append('\n');
               tris.append(',').append(' ');
            }
         }

         if (i < len0 - 1) {
            vs.append(',').append(' ').append('\n');
            vts.append(',').append(' ').append('\n');
            vns.append(',').append(' ').append('\n');
            tris.append(',').append(' ').append('\n');
         }
      }
   }

   /**
    * Tests this mesh for equivalence with another.
    *
    * @param mesh2
    *           the mesh
    * @return the evaluation
    */
   protected boolean equals ( final Mesh2 mesh2 ) {

      if (!Arrays.equals(this.coords, mesh2.coords)) {
         return false;
      }

      if (!Arrays.deepEquals(this.faces, mesh2.faces)) {
         return false;
      }

      return true;
   }

   /**
    * Attempts to calculate texture coordinates (UVs) for a
    * mesh. Does this by calculating the object-space
    * dimensions of each coordinate, then using the frame as a
    * reference for new UVs.
    *
    * @return this mesh
    * @see Mesh2#calcDimensions(Mesh2, Vec2, Vec2, Vec2)
    * @see Vec2#div(float, Vec2, Vec2)
    */
   @Chainable
   public Mesh2 calcUvs () {

      final Vec2 dim = Mesh2.calcDimensions(this,
            new Vec2(), new Vec2(), new Vec2());
      dim.x = dim.x == 0.0f ? Utils.EPSILON : 1.0f / dim.x;
      dim.y = dim.y == 0.0f ? Utils.EPSILON : 1.0f / dim.y;

      final int len = this.coords.length;
      this.texCoords = Vec2.resize(this.texCoords, len);

      for (int i = 0; i < len; ++i) {
         final Vec2 v = this.coords[i];
         final Vec2 vt = this.texCoords[i];
         vt.x = v.x * dim.x + 0.5f;
         vt.y = 0.5f - v.y * dim.y;
      }

      return this;
   }

   /**
    * Clones this mesh.
    *
    * @return the cloned mesh
    */
   @Override
   public Mesh2 clone () {

      return new Mesh2(this);
   }

   /**
    * Tests this mesh for equivalence with an object.
    *
    * @param obj
    *           the object
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if (this == obj) {
         return true;
      }

      if (!super.equals(obj)) {
         return false;
      }

      if (this.getClass() != obj.getClass()) {
         return false;
      }

      return this.equals((Mesh2) obj);
   }

   /**
    * Gets an edge from the mesh.
    *
    * @param i
    *           the face index
    * @param j
    *           the vertex index
    * @param target
    *           the output edge
    * @return the edge
    */
   public Edge2 getEdge (
         final int i,
         final int j,
         final Edge2 target ) {

      final int[][] f0 = this.faces[Math.floorMod(
            i, this.faces.length)];
      final int f0len = f0.length;
      final int[] f1 = f0[Math.floorMod(j, f0len)];
      final int[] f2 = f0[Math.floorMod(j + 1, f0len)];

      return target.set(
            this.coords[f1[0]],
            this.texCoords[f1[1]],

            this.coords[f2[0]],
            this.texCoords[f2[1]]);
   }

   /**
    * Gets an array of edges from the mesh.
    *
    * @return the edges array
    */
   public Edge2[] getEdges () {

      final ArrayList < Edge2 > result = new ArrayList <>();
      Edge2 trial = new Edge2();
      final int len0 = this.faces.length;

      for (int i = 0; i < len0; ++i) {

         final int[][] fs = this.faces[i];
         final int len1 = fs.length;

         for (int j = 0; j < len1; ++j) {

            final int[] fo = fs[j];
            final int[] fd = fs[(j + 1) % len1];

            trial.set(
                  this.coords[fo[0]],
                  this.texCoords[fo[1]],
                  this.coords[fd[0]],
                  this.texCoords[fd[1]]);

            if (!result.contains(trial)) {
               result.add(trial);
               trial = new Edge2();
            }
         }
      }

      return result.toArray(new Edge2[result.size()]);
   }

   /**
    * Gets a face from the mesh.
    *
    * @param i
    *           the index
    * @param target
    *           the output face
    * @return the face
    */
   public Face2 getFace (
         final int i,
         final Face2 target ) {

      final int[][] face = this.faces[Math.floorMod(
            i, this.faces.length)];
      final int len = face.length;
      final Vert2[] vertices = new Vert2[len];

      for (int j = 0; j < len; ++j) {
         final int[] vert = face[j];
         vertices[j] = new Vert2(
               this.coords[vert[0]],
               this.texCoords[vert[1]]);
      }

      return target.set(vertices);
   }

   /**
    * Gets an array of faces from the mesh.
    *
    * @return the faces array
    */
   public Face2[] getFaces () {

      final int len0 = this.faces.length;
      final Face2[] result = new Face2[len0];

      for (int i = 0; i < len0; ++i) {

         final int[][] fs0 = this.faces[i];
         final int len1 = fs0.length;
         final Vert2[] verts = new Vert2[len1];

         for (int j = 0; j < len1; ++j) {

            final int[] fs1 = fs0[j];
            verts[j] = new Vert2(
                  this.coords[fs1[0]],
                  this.texCoords[fs1[1]]);
         }

         result[i] = new Face2(verts);
      }

      return result;
   }

   /**
    * Get a vertex from the mesh.
    *
    * @param i
    *           primary index
    * @param j
    *           secondary index
    * @param target
    *           the output vertex
    * @return the vertex
    */
   public Vert2 getVertex (
         final int i,
         final int j,
         final Vert2 target ) {

      final int[][] f0 = this.faces[Math.floorMod(
            i, this.faces.length)];
      final int[] f = f0[Math.floorMod(
            j, f0.length)];

      return target.set(
            this.coords[f[0]],
            this.texCoords[f[1]]);
   }

   /**
    * Get an array of vertices from the mesh.
    *
    * @return the vertices
    */
   public Vert2[] getVertices () {

      final ArrayList < Vert2 > result = new ArrayList <>();
      Vert2 trial = new Vert2();
      final int len0 = this.faces.length;

      for (int i = 0; i < len0; ++i) {

         final int[][] fs = this.faces[i];
         final int len1 = fs.length;

         for (int j = 0; j < len1; ++j) {

            final int[] f = fs[j];
            trial.set(
                  this.coords[f[0]],
                  this.texCoords[f[1]]);

            if (!result.contains(trial)) {
               result.add(trial);
               trial = new Vert2();
            }
         }
      }

      return result.toArray(new Vert2[result.size()]);
   }

   /**
    * Returns a hash code for this mesh based on its
    * coordinates and its face indices.
    *
    * @return the hash code
    */
   @Override
   public int hashCode () {

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL ^ Arrays.hashCode(this.coords);
      hash = hash * IUtils.HASH_MUL ^ Arrays.deepHashCode(this.faces);
      return hash;
   }

   /**
    * Centers the mesh about the origin, (0.0, 0.0) and
    * rescales it to the range [-0.5, 0.5]. Parallel to
    * p5.Geometry's normalize method.
    *
    * @return this mesh
    * @see Mesh2#calcDimensions(Mesh2, Vec2, Vec2, Vec2)
    */
   @Chainable
   public Mesh2 reframe () {

      final Vec2 dim = new Vec2();
      final Vec2 lb = new Vec2();
      final Vec2 ub = new Vec2();
      Mesh2.calcDimensions(this, dim, lb, ub);

      lb.x = -0.5f * (lb.x + ub.x);
      lb.y = -0.5f * (lb.y + ub.y);
      final float scl = Utils.div(1.0f, Utils.max(dim.x, dim.y));

      Vec2 c;
      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         c = this.coords[i];
         Vec2.add(c, lb, c);
         Vec2.mul(c, scl, c);
      }

      return this;
   }

   /**
    * Flips the indices which specify an edge.
    *
    * @param i
    *           face index
    * @param j
    *           edge index
    * @return this mesh
    */
   @Chainable
   public Mesh2 reverseEdge (
         final int i,
         final int j ) {

      final int[][] face = this.faces[Math.floorMod(i, this.faces.length)];
      final int len = face.length;
      final int jOrigin = Math.floorMod(j, len);
      final int jDest = Math.floorMod(j + 1, len);

      final int[] temp = face[jOrigin];
      face[jOrigin] = face[jDest];
      face[jDest] = temp;

      return this;
   }

   /**
    * Flips the indices which specify a face.
    *
    * @param i
    *           face index
    * @return this mesh
    */
   @Chainable
   public Mesh2 reverseFace ( final int i ) {

      final int[][] face = this.faces[Math.floorMod(i, this.faces.length)];
      final int len = face.length;
      final int halfLen = len >> 1;
      for (int j = 0; j < halfLen; ++j) {
         final int reverse = len - j - 1;
         final int[] temp = face[j];
         face[j] = face[reverse];
         face[reverse] = temp;
      }

      return this;
   }

   /**
    * Rotates all coordinates in the mesh by an angle around
    * the z axis.
    *
    * @param radians
    *           the angle in radians
    * @return this mesh
    * @see Vec2#rotateZ(Vec2, float, Vec2)
    */
   @Chainable
   public Mesh2 rotateZ ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      Vec2 c;

      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         c = this.coords[i];
         Vec2.rotateZ(c, cosa, sina, c);
      }

      return this;
   }

   /**
    * Scales all coordinates in the mesh by a scalar.
    *
    * @param scale
    *           the scalar
    * @return this mesh
    * @see Vec2#mul(Vec2, float, Vec2)
    */
   @Chainable
   public Mesh2 scale ( final float scale ) {

      Vec2 c;
      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         c = this.coords[i];
         Vec2.mul(c, scale, c);
      }

      return this;
   }

   /**
    * Scales all coordinates in the mesh by a vector.
    *
    * @param scale
    *           the vector
    * @return this mesh
    * @see Vec2#mul(Vec2, Vec2, Vec2)
    */
   @Chainable
   public Mesh2 scale ( final Vec2 scale ) {

      Vec2 c;
      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         c = this.coords[i];
         Vec2.mul(c, scale, c);
      }

      return this;
   }

   /**
    * Sets the mesh's data by reference, not by value.
    *
    * @param faces
    *           the faces array
    * @param coords
    *           the coordinates array
    * @param texCoords
    *           the texture coordinates array
    * @return this mesh
    */
   @Chainable
   public Mesh2 set (
         final int[][][] faces,
         final Vec2[] coords,
         final Vec2[] texCoords ) {

      this.faces = faces;
      this.coords = coords;
      this.texCoords = texCoords;
      return this;
   }

   /**
    * Sets this mesh to a copy of the source. Allocates new
    * arrays for coordinates, texture coordinates and faces.
    *
    * @param source
    *           the source mesh
    * @return this mesh
    */
   @Chainable
   public Mesh2 set ( final Mesh2 source ) {

      /*
       * This should not use Vec2#resize, as it is copying all
       * vectors.
       */

      /* Copy coordinates. */
      final Vec2[] sourcevs = source.coords;
      final int vslen = sourcevs.length;
      this.coords = new Vec2[vslen];
      for (int i = 0; i < vslen; ++i) {
         this.coords[i] = new Vec2(sourcevs[i]);
      }

      /* Copy texture coordinates. */
      final Vec2[] sourcevts = source.texCoords;
      final int vtslen = sourcevts.length;
      this.texCoords = new Vec2[vtslen];
      for (int j = 0; j < vtslen; ++j) {
         this.texCoords[j] = new Vec2(sourcevts[j]);
      }

      /* Copy faces. */
      final int[][][] sourcefs = source.faces;
      final int fslen0 = sourcefs.length;
      this.faces = new int[fslen0][][];

      for (int i = 0; i < fslen0; ++i) {

         final int[][] source1 = sourcefs[i];
         final int fslen1 = source1.length;
         final int[][] target1 = new int[fslen1][];
         this.faces[i] = target1;

         for (int j = 0; j < fslen1; ++j) {

            final int[] source2 = source1[j];
            final int fslen2 = source2.length;
            final int[] target2 = new int[fslen2];
            target1[j] = target2;

            for (int k = 0; k < fslen2; ++k) {
               target2[k] = source2[k];
            }
         }
      }

      this.materialIndex = source.materialIndex;
      this.name = source.name;
      return this;
   }

   /**
    * Sorts the coordinates and texture coordinates of a mesh,
    * then reassigns indices in the face.
    *
    * @return this mesh
    */
   public Mesh2 sort () {

      return this.sort(Utils.EPSILON);
   }

   /**
    * Sorts the coordinates and texture coordinates of a mesh,
    * then reassigns indices in the face.
    *
    * @param tolerance
    *           the quantization tolerance
    * @return this mesh
    */
   @Experimental
   @Chainable
   public Mesh2 sort ( final float tolerance ) {

      final Comparator < Vec2 > cmpr = Mesh.SORT_2;

      /*
       * Sort coordinates: copy old indices, load into sorted set
       * to both remove duplicates and to sort, then unload back
       * into a new array.
       */
      final int vlen = this.coords.length;
      final Vec2[] vold = new Vec2[vlen];
      System.arraycopy(this.coords, 0, vold, 0, vlen);
      final SortedSet < Vec2 > vsUnique = new TreeSet <>(cmpr);
      for (int i = 0; i < vlen; ++i) {
         vsUnique.add(this.coords[i]);
      }
      this.coords = vsUnique.toArray(new Vec2[vsUnique.size()]);

      /*
       * Sort texture coordinates.
       */
      final int vtlen = this.texCoords.length;
      final Vec2[] vtold = new Vec2[vtlen];
      System.arraycopy(this.texCoords, 0, vtold, 0, vtlen);
      final SortedSet < Vec2 > vtsUnique = new TreeSet <>(cmpr);
      for (int i = 0; i < vtlen; ++i) {
         vtsUnique.add(this.texCoords[i]);
      }
      this.texCoords = vtsUnique.toArray(new Vec2[vtsUnique.size()]);

      /* Update face indices. */
      final int facesLen = this.faces.length;
      for (int i = 0; i < facesLen; ++i) {
         final int[][] face = this.faces[i];
         final int vertsLen = face.length;

         for (int j = 0; j < vertsLen; ++j) {
            final int[] vert = face[j];

            /* Update coord index. */
            final int vidx = Arrays.binarySearch(
                  this.coords, vold[vert[0]], cmpr);
            vert[0] = vidx < 0 ? vert[0] : vidx;

            /* Update tex coord index. */
            final int vtidx = Arrays.binarySearch(
                  this.texCoords, vtold[vert[1]], cmpr);
            vert[1] = vtidx < 0 ? vert[1] : vtidx;
         }
      }

      /* Sort faces by centroid. */
      Arrays.sort(this.faces, new Mesh2.SortIndices2(this.coords));

      return this;
   }

   /**
    * Subdivides a convex face by subdividing each of its edges
    * with one cut to create a midpoint, then connecting them.
    * This generates peripheral triangles and a new central
    * face with the same number of edges as the original. This
    * is best suited to triangle-based meshes.
    * 
    * @param faceIdx
    *           the face index
    * @return the mesh
    */
   @Experimental
   public Mesh2 subdivFaceInscribe ( final int faceIdx ) {

      /* Validate face index, find face. */
      final int facesLen = this.faces.length;
      final int i = Math.floorMod(faceIdx, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      /*
       * Cache old length of coordinates and texture coordinates
       * so new ones can be appended to the end.
       */
      final int vsOldLen = this.coords.length;
      final int vtsOldLen = this.texCoords.length;

      final Vec2[] vsNew = new Vec2[faceLen];
      final Vec2[] vtsNew = new Vec2[faceLen];
      final int[][][] fsNew = new int[faceLen + 1][][];
      final int[][] centerFace = fsNew[faceLen] = new int[faceLen][2];

      for (int j = 0; j < faceLen; ++j) {
         int[] vertCurr = face[j];
         Vec2 vCurr = coords[vertCurr[0]];
         Vec2 vtCurr = texCoords[vertCurr[1]];
         int k = (j + 1) % faceLen;
         int[] vertNext = face[k];

         int vNextIdx = vertNext[0];
         Vec2 vNext = coords[vNextIdx];
         vsNew[j] = new Vec2(
               (vCurr.x + vNext.x) * 0.5f,
               (vCurr.y + vNext.y) * 0.5f);

         int vtNextIdx = vertNext[1];
         Vec2 vtNext = texCoords[vtNextIdx];
         vtsNew[j] = new Vec2(
               (vtCurr.x + vtNext.x) * 0.5f,
               (vtCurr.y + vtNext.y) * 0.5f);

         int vSubdivIdx = vsOldLen + j;
         int vtSubdivIdx = vtsOldLen + j;
         fsNew[j] = new int[][] {
               { vSubdivIdx, vtSubdivIdx },
               { vNextIdx, vtNextIdx },
               { vsOldLen + k, vtsOldLen + k } };

         centerFace[j][0] = vSubdivIdx;
         centerFace[j][1] = vtSubdivIdx;
      }

      this.coords = Vec2.concat(this.coords, vsNew);
      this.texCoords = Vec2.concat(this.texCoords, vtsNew);
      this.faces = Mesh.splice(this.faces, i, 1, fsNew);

      return this;
   }

   /**
    * Subdivides a convex face. Defaults to centroid-based
    * subdivision.
    * 
    * @param faceIdx
    *           the face index
    * @return this mesh.
    */
   @Experimental
   @Chainable
   public Mesh2 subdivFace ( final int faceIdx ) {

      return subdivFaceCentroid(faceIdx);
   }

   /**
    * Subdivides a convex face by calculating its centroid,
    * subdividing each of its edges with one cut to create a
    * midpoint, then connecting the midpoints to the centroid.
    * This generates a quadrilateral for the number of edges in
    * the face.
    * 
    * @param faceIdx
    *           the face index
    * @return this mesh
    */
   @Experimental
   @Chainable
   public Mesh2 subdivFaceCentroid ( final int faceIdx ) {

      /* Validate face index, find face. */
      final int facesLen = this.faces.length;
      final int i = Math.floorMod(faceIdx, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      /*
       * Cache old length of coordinates and texture coordinates
       * so new ones can be appended to the end.
       */
      final int vsOldLen = this.coords.length;
      final int vtsOldLen = this.texCoords.length;

      /* Create arrays to hold new data. */
      final Vec2[] vsNew = new Vec2[faceLen + 1];
      final Vec2[] vtsNew = new Vec2[faceLen + 1];
      final int[][][] fsNew = new int[faceLen][4][2];

      Vec2 vCentroid = vsNew[faceLen] = new Vec2();
      Vec2 vtCentroid = vtsNew[faceLen] = new Vec2();
      int vCentroidIdx = vsOldLen + faceLen;
      int vtCentroidIdx = vtsOldLen + faceLen;
      for (int j = 0; j < faceLen; ++j) {
         int[] vertCurr = face[j];
         Vec2 vCurr = coords[vertCurr[0]];
         Vec2 vtCurr = texCoords[vertCurr[1]];

         Vec2.add(vCentroid, vCurr, vCentroid);
         Vec2.add(vtCentroid, vtCurr, vtCentroid);

         int k = (j + 1) % faceLen;
         int[] vertNext = face[k];

         int vNextIdx = vertNext[0];
         Vec2 vNext = coords[vNextIdx];
         vsNew[j] = new Vec2(
               (vCurr.x + vNext.x) * 0.5f,
               (vCurr.y + vNext.y) * 0.5f);

         int vtNextIdx = vertNext[1];
         Vec2 vtNext = texCoords[vtNextIdx];
         vtsNew[j] = new Vec2(
               (vtCurr.x + vtNext.x) * 0.5f,
               (vtCurr.y + vtNext.y) * 0.5f);

         fsNew[j] = new int[][] {
               { vCentroidIdx, vtCentroidIdx },
               { vsOldLen + j, vtsOldLen + j },
               { vNextIdx, vtNextIdx },
               { vsOldLen + k, vtsOldLen + k } };
      }
      Vec2.div(vCentroid, faceLen, vCentroid);
      Vec2.div(vtCentroid, faceLen, vtCentroid);

      this.coords = Vec2.concat(this.coords, vsNew);
      this.texCoords = Vec2.concat(this.texCoords, vtsNew);
      this.faces = Mesh.splice(this.faces, i, 1, fsNew);

      return this;
   }

   /**
    * Subdivides an edge by the number of cuts given. For
    * example, one cut will divide an edge in half; two cuts,
    * into thirds.<br>
    * <br>
    * Does not distinguish between interior edges, which have a
    * complement elsewhere, and border edges; for that reason
    * this works best with NGONs.
    *
    * @param faceIndex
    *           the face index
    * @param edgeIndex
    *           the edge index
    * @param cuts
    *           number of cuts
    * @return this mesh
    */
   @Experimental
   public Mesh2 subdivEdge (
         final int faceIndex,
         final int edgeIndex,
         final int cuts ) {

      if (cuts < 1) {
         return this;
      }

      /* Validate face index, find face. */
      final int facesLen = this.faces.length;
      final int i = Math.floorMod(faceIndex, facesLen);
      final int[][] face = this.faces[i];
      final int faceLen = face.length;

      /* Find edge origin vertex. */
      final int j0 = Math.floorMod(edgeIndex, faceLen);
      final int[] vert0Idx = face[j0];
      final Vec2 vOrigin = this.coords[vert0Idx[0]];
      final Vec2 vtOrigin = this.texCoords[vert0Idx[1]];

      /* Find edge destination vertex. */
      final int j1 = Math.floorMod(edgeIndex + 1, faceLen);
      final int[] vert1Idx = face[j1];
      final Vec2 vDest = this.coords[vert1Idx[0]];
      final Vec2 vtDest = this.texCoords[vert1Idx[1]];

      /*
       * Cache old length of coordinates and texture coordinates
       * so new ones can be appended to the end.
       */
      final int vsOldLen = this.coords.length;
      final int vtsOldLen = this.texCoords.length;

      /* Create arrays to hold new data. */
      final Vec2[] vsNew = new Vec2[cuts];
      final Vec2[] vtsNew = new Vec2[cuts];
      final int[][] fsNew = new int[cuts][2];

      /*
       * Subdivide the edge. The edge origin and destination are
       * to be excluded from the new set, so the conversion to the
       * step accounts for this.
       */
      final float toStep = 1.0f / (cuts + 1.0f);
      for (int k = 0; k < cuts; ++k) {
         final float step = toStep + k * toStep;
         final float u = 1.0f - step;

         final Vec2 v = new Vec2();
         final Vec2 vt = new Vec2();

         v.set(
               u * vOrigin.x + step * vDest.x,
               u * vOrigin.y + step * vDest.y);

         vt.set(
               u * vtOrigin.x + step * vtDest.x,
               u * vtOrigin.y + step * vtDest.y);

         vsNew[k] = v;
         vtsNew[k] = vt;

         final int[] newf = fsNew[k];
         newf[0] = vsOldLen + k;
         newf[1] = vtsOldLen + k;
      }

      /*
       * Append new coords and tex coords to the end of their
       * respective arrays. The new faces need to be inserted to
       * this.faces[idx], not reassigned to local face array.
       */
      this.coords = Vec2.concat(this.coords, vsNew);
      this.texCoords = Vec2.concat(this.texCoords, vtsNew);
      this.faces[i] = Mesh.insert(face, j1, fsNew);

      return this;
   }

   /**
    * Subdivides all edges in a face by the number of cuts
    * given. For example, one cut will divide an edge in half;
    * two cuts, into thirds.<br>
    * <br>
    * Does not distinguish between interior edges, which have a
    * complement elsewhere, and border edges; for that reason
    * this works best with NGONs.
    *
    * @param faceIndex
    *           the face index
    * @param cuts
    *           number of cuts
    * @return this mesh
    */
   @Experimental
   public Mesh2 subdivEdges (
         final int faceIndex,
         final int cuts ) {

      final int faceLen = this.faces[Math.floorMod(faceIndex,
            this.faces.length)].length;
      for (int j = 0, k = 0; j < faceLen; ++j, k += cuts) {
         this.subdivEdge(faceIndex, k + j, cuts);
      }

      return this;
   }

   /**
    * Renders the mesh as a string following the Wavefront OBJ
    * file format.
    *
    * @return the string
    */
   @Experimental
   public String toObjString () {

      // TODO: Needs testing.
      final int coordsLen = this.coords.length;
      final int texCoordsLen = this.texCoords.length;
      final int facesLen = this.faces.length;
      final StringBuilder result = new StringBuilder();

      /*
       * Append a comment listing the number of coordinates,
       * texture coordinates, normals and faces.
       */
      result.append("# v: ").append(coordsLen)
            .append(", vt: ").append(texCoordsLen)
            .append(", vn: 1, f: ").append(facesLen)
            .append('\n').append('\n');

      /* Append name. */
      result.append('o').append(' ').append(this.name)
            .append('\n').append('\n');

      /* Append coordinates. */
      for (final Vec2 coord : this.coords) {
         result.append('v').append(' ')
               .append(coord.toObjString())
               .append(" 0.0 \n");
      }
      result.append('\n');

      /* Append a texture coordinates. */
      for (final Vec2 texCoord : this.texCoords) {
         result.append("vt ")
               .append(texCoord.toObjString())
               .append('\n');
      }

      /* Append a single normal. */
      result.append("\nvn 0.0 0.0 1.0\n");

      /* Append face indices. */
      for (int i = 0; i < facesLen; ++i) {

         final int[][] face = this.faces[i];
         final int vLen = face.length;
         result.append('f').append(' ');

         for (int j = 0; j < vLen; ++j) {

            /* Indices in an .obj file start at 1, not 0. */
            final int[] vert = face[j];
            result.append(vert[0] + 1)
                  .append('/')
                  .append(vert[1] + 1)
                  .append('/')
                  .append('1');
         }

         result.append('\n');
      }

      result.append('\n');
      return result.toString();
   }

   /**
    * Centers the mesh about the origin, (0.0, 0.0), by
    * calculating its dimensions then subtracting the center
    * point.
    *
    * @return this mesh
    * @see Mesh2#calcDimensions(Mesh2, Vec2, Vec2, Vec2)
    * @see Mesh2#translate(Vec2)
    */
   @Chainable
   public Mesh2 toOrigin () {

      final Vec2 lb = new Vec2();
      final Vec2 ub = new Vec2();
      Mesh2.calcDimensions(this, new Vec2(), lb, ub);

      lb.x = -0.5f * (lb.x + ub.x);
      lb.y = -0.5f * (lb.y + ub.y);
      this.translate(lb);

      return this;
   }

   /**
    * Returns a string representation of the mesh.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4, Integer.MAX_VALUE);
   }

   /**
    * Returns a string representation of the mesh.
    *
    * @param places
    *           the number of places
    * @return the string
    */
   public String toString ( final int places ) {

      return this.toString(places, Integer.MAX_VALUE);
   }

   /**
    * Returns a string representation of the mesh. Includes an
    * option to truncate the listing in case of large meshes.
    *
    * @param places
    *           the number of places
    * @param truncate
    *           truncate elements in a list
    * @return the string
    */
   public String toString (
         final int places,
         final int truncate ) {

      final StringBuilder sb = new StringBuilder();

      sb.append("{ name: \"")
            .append(this.name)
            .append('\"').append(',').append(' ')
            .append('\n')
            .append("coords: [ ");

      if (this.coords != null) {
         sb.append('\n');
         final int len = Math.min(this.coords.length, truncate);
         final int last = len - 1;
         for (int i = 0; i < len; ++i) {
            sb.append(this.coords[i].toString(places));
            if (i < last) {
               sb.append(',').append(' ');
               sb.append('\n');
            }
         }

         if (this.coords.length > truncate) {
            sb.append("\n/* ... */");
         }
      }

      sb.append(" ],\ntexCoords: [");
      if (this.texCoords != null) {
         sb.append('\n');
         final int len = Math.min(this.texCoords.length, truncate);
         final int last = len - 1;
         for (int i = 0; i < len; ++i) {
            sb.append(this.texCoords[i].toString(places));
            if (i < last) {
               sb.append(',').append(' ');
               sb.append('\n');
            }
         }

         if (this.texCoords.length > truncate) {
            sb.append("\n/* ... */");
         }
      }

      sb.append(" ],\nfaces: [");
      if (this.faces != null) {
         sb.append('\n');
         final int facesLen = Math.min(this.faces.length, truncate);
         final int facesLast = facesLen - 1;

         for (int i = 0; i < facesLen; ++i) {

            final int[][] verts = this.faces[i];
            final int vertsLen = verts.length;
            final int vertsLast = vertsLen - 1;
            sb.append('[').append(' ');

            for (int j = 0; j < vertsLen; ++j) {

               final int[] vert = verts[j];
               final int infoLen = vert.length;
               final int infoLast = infoLen - 1;
               sb.append('[').append(' ');

               for (int k = 0; k < infoLen; ++k) {

                  sb.append(vert[k]);
                  if (k < infoLast) {
                     sb.append(',').append(' ');
                  }
               }
               sb.append(' ').append(']');
               if (j < vertsLast) {
                  sb.append(',').append(' ');
               }
            }
            sb.append(' ').append(']');
            if (i < facesLast) {
               sb.append(',').append(' ');
               sb.append('\n');
            }
         }

         if (this.faces.length > truncate) {
            sb.append("\n/* ... */");
         }
      }

      sb.append(" ] }");
      return sb.toString();
   }

   /**
    * Translates all coordinates in the mesh by a vector.
    *
    * @param v
    *           the vector
    * @return this mesh
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   @Chainable
   public Mesh2 translate ( final Vec2 v ) {

      Vec2 c;
      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         c = this.coords[i];
         Vec2.add(c, v, c);
      }

      return this;
   }
}
