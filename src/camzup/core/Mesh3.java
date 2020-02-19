package camzup.core;

import java.util.ArrayList;
import java.util.Arrays;

import camzup.core.Mesh2.PolyType;

/**
 * Organizes data needed to draw a three dimensional shape
 * using vertices and faces. Given that a mesh is primarily
 * a collection of references, it is initialized with null
 * arrays (coordinates, texture coordinates and indices).
 * These are not final, and so can be reassigned.
 */
public class Mesh3 extends Mesh {

   /**
    * A helper function for parsing an OBJ file. Attempts to
    * convert a string to an integer.
    *
    * @param i
    *           the string
    * @return the integer
    */
   private static int intFromStr ( final String i ) {

      int target = 0;
      try {
         target = Integer.parseInt(i);
      } catch (final NumberFormatException e) {
         target = 0;
      }
      return target;
   }

   static Mesh3 arc (
         final float majorStart,
         final float majorStop,
         final float minorStart,
         final float minorStop,
         final float thickness,
         final int sectors,
         final int panels,
         final Mesh3 target ) {

      // if(Utils.approx(majorStop - majorStart, 0.00139f)) {
      // }

      // if(Utils.approx(minorStop - minorStart, 0.00139f)) {
      // }

      /* Sector Arc */
      final float a1 = Utils.mod1(majorStart * IUtils.ONE_TAU);
      final float b1 = Utils.mod1(majorStop * IUtils.ONE_TAU);
      final float df1 = b1 - a1;
      final float arcLen1 = Utils.mod1(df1);
      final float destAngle1 = a1 + arcLen1;
      final int vsect = Utils.ceilToInt(
            1 + (sectors < 3 ? 3 : sectors) * arcLen1);

      /* Panel Arc */
      final float a0 = Utils.mod1(minorStart * IUtils.ONE_TAU);
      final float b0 = Utils.mod1(minorStop * IUtils.ONE_TAU);
      final float df0 = b0 - a0;
      final float arcLen0 = Utils.mod1(df0);
      final float destAngle0 = a0 + arcLen0;
      final int vpanl = Utils.ceilToInt(
            1 + (panels < 3 ? 3 : panels) * arcLen0);

      final int panels1 = vpanl + 1;
      final int sectors1 = vsect + 1;
      final int len = panels1 * sectors1;

      final Vec3[] coords = new Vec3[len];
      final Vec2[] texCoords = new Vec2[len];
      final Vec3[] normals = new Vec3[len];

      final float toU = 1.0f / vsect;
      final float toV = 1.0f / vpanl;

      final float vtrad = 0.5f * Utils.max(Utils.EPSILON, thickness);
      final float ratio = vtrad + vtrad;

      for (int k = 0, i = 0; i < panels1; ++i) {

         final float v = i * toV;
         final float phi = Utils.lerpUnclamped(
               a0, destAngle0, v);
         final float cosPhi = Utils.scNorm(phi);
         final float sinPhi = Utils.scNorm(phi - 0.25f);
         final float r = 1.0f + ratio * cosPhi;

         for (int j = 0; j < sectors1; ++j, ++k) {

            final float u = j * toU;
            final float theta = Utils.lerpUnclamped(
                  a1, destAngle1, u);
            final float cosTheta = Utils.scNorm(theta);
            final float sinTheta = Utils.scNorm(theta - 0.25f);

            coords[k] = new Vec3(
                  r * cosTheta,
                  r * sinTheta,
                  ratio * sinPhi);

            texCoords[k] = new Vec2(u, v);

            normals[k] = new Vec3(
                  cosPhi * cosTheta,
                  cosPhi * sinTheta,
                  sinPhi);
         }
      }

      final int[][][] faces = new int[2 * vsect *
            vpanl][3][3];
      final int sliceCount = vsect + 1;
      int e = 0;
      int f = sliceCount;
      for (int k = 0, i = 0; i < vpanl; ++i) {
         for (int j = 0; j < vsect; ++j, k += 2) {
            final int a = e + j;
            final int b = a + 1;
            final int d = f + j;
            final int c = d + 1;

            faces[k] = new int[][] {
                  { a, a, a }, { b, b, b }, { d, d, d } };

            faces[k + 1] = new int[][] {
                  { d, d, d }, { b, b, b }, { c, c, c } };
         }

         e += sliceCount;
         f += sliceCount;
      }

      target.name = "Arc";
      return target.set(faces, coords, texCoords, normals);
   }

   static Mesh3 arc (
         final float majorStart,
         final float majorStop,
         final float minorStart,
         final float minorStop,
         final float thickness,
         final Mesh3 target ) {

      return Mesh3.arc(majorStart, majorStop,
            minorStart, minorStop,
            thickness,
            IMesh.DEFAULT_CIRCLE_SECTORS,
            IMesh.DEFAULT_CIRCLE_SECTORS >> 1,
            target);
   }

   static Mesh3 arc (
         final float majorStart,
         final float majorStop,
         final float minorStart,
         final float minorStop,
         final Mesh3 target ) {

      return Mesh3.arc(majorStart, majorStop,
            minorStart, minorStop, 0.15f, target);
   }

   @Experimental
   static Mesh3 cube (
         final int cols, /* x */
         final int rows, /* y */
         final int layers, /* z */
         final Mesh3 target ) {

      final int lval = layers < 1 ? 1 : layers;
      final int rval = rows < 1 ? 1 : rows;
      final int cval = cols < 1 ? 1 : cols;

      final int lval1 = lval + 1;
      final int rval1 = rval + 1;
      final int cval1 = cval + 1;

      final float hToStep = 1.0f / lval;
      final float iToStep = 1.0f / rval;
      final float jToStep = 1.0f / cval;

      /* Calculate x values in separate loop. */
      final float[] us = new float[cval1];
      final float[] xs = new float[cval1];
      for (int j = 0; j < cval1; ++j) {
         final float u = us[j] = j * jToStep;
         xs[j] = u - 0.5f;
      }

      /* Calculate y values in separate loop. */
      final float[] vs = new float[rval1];
      final float[] ys = new float[rval1];
      for (int i = 0; i < rval1; ++i) {
         final float v = vs[i] = i * iToStep;
         ys[i] = v - 0.5f;
      }

      /* Calculate z values in separate loop. */
      final float[] ws = new float[lval1];
      final float[] zs = new float[lval1];
      for (int h = 0; h < lval1; ++h) {
         final float w = ws[h] = h * hToStep;
         zs[h] = w - 0.5f;
      }

      final int lrlen0 = lval * rval;
      final int rclen0 = rval * cval;
      final int cllen0 = cval * lval;

      final int lrlen1 = lval1 * rval1;
      final int rclen1 = rval1 * cval1;
      final int cllen1 = cval1 * lval1;

      final int lrlen2 = lrlen1 + lrlen1; /* Left & Right */
      final int rclen2 = rclen1 + rclen1; /* Front & Back */
      final int cllen2 = cllen1 + cllen1; /* Top & Bottom */
      final int totalLen = lrlen2 + rclen2 + cllen2;

      final Vec3[] coords = new Vec3[totalLen];
      final Vec2[] texCoords = new Vec2[totalLen];
      final Vec3[] normals = new Vec3[] {
            /* 0 */ new Vec3(-1.0f, 0.0f, 0.0f),
            /* 1 */ new Vec3(1.0f, 0.0f, 0.0f),
            /* 2 */ new Vec3(0.0f, 0.0f, -1.0f),
            /* 3 */ new Vec3(0.0f, 0.0f, 1.0f),
            /* 4 */ new Vec3(0.0f, -1.0f, 0.0f),
            /* 5 */ new Vec3(0.0f, 1.0f, 0.0f),
      };

      final int totalfaces = lrlen0 * 2 + rclen0 * 2 + cllen0 * 2;
      final int[][][] faces = new int[totalfaces][4][3];

      int idx0 = 0;

      /* Left & Right */
      final int lrOff0 = idx0;
      final int lrOff1 = lrOff0 + lrlen1;

      for (int h = 0; h < lval1; ++h) {

         final float z = zs[h];
         final float w = ws[h];

         for (int i = 0; i < rval1; ++i) {

            final float y = ys[i];
            final float v = vs[i];

            final int leftIdx = lrOff0 + idx0;
            final int rightIdx = lrOff1 + idx0;

            /* Left */
            coords[leftIdx] = new Vec3(-0.5f, y, z);
            texCoords[leftIdx] = new Vec2(w, v);

            /* Right */
            coords[rightIdx] = new Vec3(0.5f, y, z);
            texCoords[rightIdx] = new Vec2(w, v);

            idx0++;
         }
      }

      /* Front & Back */
      final int rcOff0 = idx0;
      final int rcOff1 = rcOff0 + rclen1;

      for (int i = 0; i < rval1; ++i) {

         final float y = ys[i];
         final float v = vs[i];

         for (int j = 0; j < cval1; ++j) {

            final float x = xs[j];
            final float u = us[j];

            final int backIdx = rcOff0 + idx0;
            final int frontIdx = rcOff1 + idx0;

            /* Back */
            coords[backIdx] = new Vec3(x, y, -0.5f);
            texCoords[backIdx] = new Vec2(u, v);

            /* Front */
            coords[frontIdx] = new Vec3(x, y, 0.5f);
            texCoords[frontIdx] = new Vec2(u, v);

            idx0++;
         }
      }

      /* Top & Bottom */
      final int clOff0 = idx0;
      final int clOff1 = clOff0 + cllen1;

      for (int j = 0; j < cval1; ++j) {

         final float x = xs[j];
         final float u = us[j];

         for (int h = 0; h < lval1; ++h) {

            final float z = zs[h];
            final float w = ws[h];

            final int bottomIdx = clOff0 + idx0;
            final int topIdx = clOff1 + idx0;

            /* Bottom */
            coords[bottomIdx] = new Vec3(x, -0.5f, z);
            texCoords[bottomIdx] = new Vec2(u, w);

            /* Top */
            coords[topIdx] = new Vec3(x, 0.5f, z);
            texCoords[topIdx] = new Vec2(u, w);

            idx0++;
         }
      }

      target.name = "Cube";
      return target.set(faces, coords, texCoords, normals);
   }

   @Experimental
   static Mesh3 torus (
         final float thickness,
         final int sectors,
         final int panels,
         final Mesh3 target ) {

      // TODO: Creates a seam. Needs to switch to using modulo in
      // faces, and to use one last longitude.

      // TODO: Redo concept of thickness / tube ratio...

      final int vsect = sectors < 3 ? 3 : sectors;
      final int vpanl = panels < 3 ? 3 : panels;

      final int panels1 = vpanl + 1;
      final int sectors1 = vsect + 1;
      final int len = panels1 * sectors1;

      final Vec3[] coords = new Vec3[len];
      final Vec2[] texCoords = new Vec2[len];
      final Vec3[] normals = new Vec3[len];

      final float toU = 1.0f / vsect;
      final float toV = 1.0f / vpanl;

      final float toTheta = 1.0f / vsect;
      final float toPhi = 1.0f / vpanl;

      final float vtrad = 0.5f * Utils.max(Utils.EPSILON, thickness);
      final float ratio = vtrad + vtrad;

      for (int k = 0, i = 0; i < panels1; ++i) {

         final float v = i * toV;
         final float phi = i * toPhi;
         final float cosPhi = Utils.scNorm(phi);
         final float sinPhi = Utils.scNorm(phi - 0.25f);

         final float r = 1.0f + ratio * cosPhi;

         for (int j = 0; j < sectors1; ++j, ++k) {

            final float u = j * toU;
            final float theta = j * toTheta;
            final float cosTheta = Utils.scNorm(theta);
            final float sinTheta = Utils.scNorm(theta - 0.25f);

            coords[k] = new Vec3(
                  r * cosTheta,
                  r * sinTheta,
                  ratio * sinPhi);

            texCoords[k] = new Vec2(u, v);

            normals[k] = new Vec3(
                  cosPhi * cosTheta,
                  cosPhi * sinTheta,
                  sinPhi);
         }
      }

      final int[][][] faces = new int[2 * vsect *
            vpanl][3][3];
      final int sliceCount = vsect + 1;
      int e = 0;
      int f = sliceCount;
      for (int k = 0, i = 0; i < vpanl; ++i) {
         for (int j = 0; j < vsect; ++j, k += 2) {
            final int a = e + j;
            final int b = a + 1;
            final int d = f + j;
            final int c = d + 1;

            faces[k] = new int[][] {
                  { a, a, a }, { b, b, b }, { d, d, d } };

            faces[k + 1] = new int[][] {
                  { d, d, d }, { b, b, b }, { c, c, c } };
         }

         e += sliceCount;
         f += sliceCount;
      }

      target.name = "Torus";
      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * Creates a UV sphere.
    *
    * @param longitudes
    *           the longitudes
    * @param latitudes
    *           the latitudes
    * @param target
    *           the output mesh
    * @return the sphere
    */
   @Deprecated
   protected static Mesh3 sphereOld (
         final int longitudes,
         final int latitudes,
         final Mesh3 target ) {

      /*
       * Longitude corresponds to azimuth; latitude, to
       * inclination.
       */
      final int vlons = longitudes < 3 ? 3 : longitudes;
      final int vlats = latitudes < 3 ? 3 : latitudes;

      final int lons1 = vlons + 1;
      final int lats1 = vlats + 1;

      /*
       * The additional two comes from the North and South poles.
       */
      final int len = lons1 * vlats + 2;

      final Vec3[] coords = new Vec3[len];
      final Vec2[] texCoords = new Vec2[len];
      final Vec3[] normals = new Vec3[len];

      final float toU = 1.0f / vlons;
      final float toV = 1.0f / lats1;

      final float toTheta = 1.0f / vlons;
      final float toPhi = 0.5f / lats1;

      /*
       * Set South pole. This is vertex 0, so subsequent vertex
       * indices begin at an offset of 1.
       */
      coords[0] = new Vec3(0.0f, 0.0f, -0.5f);
      texCoords[0] = new Vec2(0.0f, 0.0f);
      normals[0] = new Vec3(0.0f, 0.0f, -1.0f);

      for (int k = 1, h = 1, i = 0; i < vlats; ++h, ++i) {
         final float v = h * toV;
         final float phi = h * toPhi - 0.25f;
         final float cosPhi = Utils.scNorm(phi);
         final float sinPhi = Utils.scNorm(phi - 0.25f);

         // To change the seam, this should be j < lons,
         // and toTheta should be 1 / lons1 .
         for (int j = 0; j < lons1; ++j, ++k) {
            final float u = j * toU;
            final float theta = j * toTheta;
            final float cosTheta = Utils.scNorm(theta);
            final float sinTheta = Utils.scNorm(theta - 0.25f);

            texCoords[k] = new Vec2(u, v);

            final Vec3 nrm = normals[k] = new Vec3(
                  cosPhi * cosTheta,
                  cosPhi * sinTheta,
                  sinPhi);
            coords[k] = Vec3.mul(nrm, 0.5f, new Vec3());
         }
      }

      /* Set North pole. */
      final int last = len - 1;
      coords[last] = new Vec3(0.0f, 0.0f, 0.5f);
      texCoords[last] = new Vec2(0.0f, 1.0f);
      normals[last] = new Vec3(0.0f, 0.0f, 1.0f);

      final int[][][] faces = new int[2 * len][3][3];
      int idx = 0;

      /* Top cap. */
      for (int j = 0; j < vlons; ++j) {
         final int n0 = j + 2;
         final int n1 = j + 1;

         faces[idx] = new int[][] {
               { n0, n0, n0 },
               { n1, n1, n1 },
               { 0, 0, 0 } };
         idx++;
      }

      /* Middle */
      final int latsn1 = vlats - 1;
      for (int i = 0; i < latsn1; ++i) {
         final int ilons1 = i * lons1;
         for (int j = 0; j < vlons; ++j) {
            final int current = j + ilons1 + 1;
            final int next = current + lons1;
            final int n1 = current + 1;
            final int n2 = next + 1;

            faces[idx] = new int[][] {
                  { current, current, current },
                  { n1, n1, n1 },
                  { n2, n2, n2 } };
            idx++;

            faces[idx] = new int[][] {
                  { current, current, current },
                  { n2, n2, n2 },
                  { next, next, next } };
            idx++;
         }
      }

      /* Bottom cap. */
      for (int j = 0; j < vlons; ++j) {
         final int n1 = last - (j + 2);
         final int n2 = last - (j + 1);

         faces[idx] = new int[][] {
               { last, last, last },
               { n1, n1, n1 },
               { n2, n2, n2 } };
         idx++;
      }

      target.name = "UV Sphere";
      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * Calculates the dimensions of an Axis-Aligned Bounding Box
    * (AABB) encompassing the mesh.
    *
    * @param mesh
    *           the mesh
    * @param target
    *           the output vector
    * @return the dimensions
    */
   public static Vec3 calcDimensions (
         final Mesh3 mesh,
         final Vec3 target ) {

      float xMin = Float.MAX_VALUE;
      float xMax = Float.MIN_VALUE;

      float yMin = Float.MAX_VALUE;
      float yMax = Float.MIN_VALUE;

      float zMin = Float.MAX_VALUE;
      float zMax = Float.MIN_VALUE;

      final Vec3[] coords = mesh.coords;
      final int len = coords.length;

      for (int i = 0; i < len; ++i) {

         final Vec3 coord = coords[i];
         final float x = coord.x;
         final float y = coord.y;
         final float z = coord.z;

         if (x < xMin) {
            xMin = x;
         } else if (x > xMax) {
            xMax = x;
         }

         if (y < yMin) {
            yMin = y;
         } else if (y > yMax) {
            yMax = y;
         }

         if (z < zMin) {
            zMin = z;
         } else if (z > zMax) {
            zMax = z;
         }
      }

      return target.set(
            xMax - xMin,
            yMax - yMin,
            zMax - zMin);
   }

   /**
    * Creates a regular convex polygon, approximating a circle.
    *
    * @param target
    *           the output mesh
    * @return the polygon
    * @see Mesh2#polygon(int, PolyType, Mesh2)
    */
   public static Mesh3 circle (
         final Mesh3 target ) {

      return Mesh3.polygon(
            IMesh.DEFAULT_CIRCLE_SECTORS,
            target);
   }

   /**
    * Generates a cube mesh. In the context of Platonic solids,
    * also known as a hexahedron, as it has 6 faces and 8
    * vertices.
    *
    * @param target
    *           the output mesh
    * @return the cube
    */
   public static Mesh3 cube ( final Mesh3 target ) {

      final Vec3[] coords = new Vec3[] {
            /* 0 */ new Vec3(-0.5f, -0.5f, -0.5f),
            /* 1 */ new Vec3(-0.5f, -0.5f, 0.5f),
            /* 2 */ new Vec3(-0.5f, 0.5f, -0.5f),
            /* 3 */ new Vec3(-0.5f, 0.5f, 0.5f),
            /* 4 */ new Vec3(0.5f, -0.5f, -0.5f),
            /* 5 */ new Vec3(0.5f, -0.5f, 0.5f),
            /* 6 */ new Vec3(0.5f, 0.5f, -0.5f),
            /* 7 */ new Vec3(0.5f, 0.5f, 0.5f)
      };

      final Vec3[] normals = new Vec3[] {
            /* 0 */ new Vec3(1.0f, 0.0f, 0.0f),
            /* 1 */ new Vec3(0.0f, 0.0f, 1.0f),
            /* 2 */ new Vec3(0.0f, 0.0f, -1.0f),
            /* 3 */ new Vec3(0.0f, -1.0f, 0.0f),
            /* 4 */ new Vec3(-1.0f, -0.0f, 0.0f),
            /* 5 */ new Vec3(0.0f, 1.0f, 0.0f)
      };

      final Vec2[] texCoords = new Vec2[] {
            /* 0 */ new Vec2(0.0f, 0.0f),
            /* 1 */ new Vec2(0.0f, 1.0f),
            /* 2 */ new Vec2(1.0f, 1.0f),
            /* 3 */ new Vec2(1.0f, 0.0f)
      };

      final int[][][] faces = new int[][][] {
            { { 0, 2, 4 }, { 1, 3, 4 }, { 3, 0, 4 }, { 2, 1, 4 } },
            { { 2, 2, 5 }, { 3, 3, 5 }, { 7, 0, 5 }, { 6, 1, 5 } },
            { { 6, 2, 0 }, { 7, 3, 0 }, { 5, 0, 0 }, { 4, 1, 0 } },
            { { 4, 2, 3 }, { 5, 3, 3 }, { 1, 0, 3 }, { 0, 1, 3 } },
            { { 2, 1, 2 }, { 6, 2, 2 }, { 4, 3, 2 }, { 0, 0, 2 } },
            { { 7, 1, 1 }, { 3, 2, 1 }, { 1, 3, 1 }, { 5, 0, 1 } }
      };

      target.name = "Cube";
      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * Creates an dodecahedron, a Platonic solid with 12 faces
    * and 20 coords.
    *
    * @param target
    *           the output mesh
    * @return the dodecahedron
    */
   public static final Mesh3 dodecahedron ( final Mesh3 target ) {

      /*
       * double r = 0.5d; double phi = (1.0d + Math.sqrt(5.0d)) /
       * 2.0d; double b = r * (1.0d / phi); double c = r * (2.0d -
       * phi); 0.3090169943749474, 0.19098300562505255
       */

      final Vec3[] coords = new Vec3[] {
            /* 00 */ new Vec3(0.0f, 0.33614415f, -0.4165113f),
            /* 01 */ new Vec3(-0.19098301f, 0.47552827f, 0.15450847f),
            /* 02 */ new Vec3(0.19098301f, 0.47552827f, 0.15450847f),
            /* 03 */ new Vec3(0.309017f, 0.19840115f, 0.38938415f),
            /* 04 */ new Vec3(-0.309017f, 0.19840115f, 0.38938415f),
            /* 05 */ new Vec3(-0.19098301f, -0.47552827f, -0.15450847f),
            /* 06 */ new Vec3(-0.309017f, -0.38938415f, 0.19840115f),
            /* 07 */ new Vec3(0.19098301f, -0.47552827f, -0.15450847f),
            /* 08 */ new Vec3(0.309017f, -0.19840115f, -0.38938415f),
            /* 09 */ new Vec3(0.0f, -0.02712715f, -0.53454524f),
            /* 10 */ new Vec3(0.309017f, 0.38938415f, -0.19840115f),
            /* 11 */ new Vec3(0.5f, 0.05901699f, -0.18163565f),
            /* 12 */ new Vec3(-0.309017f, -0.19840115f, -0.38938415f),
            /* 13 */ new Vec3(-0.5f, 0.05901699f, -0.18163565f),
            /* 14 */ new Vec3(-0.309017f, 0.38938415f, -0.19840115f),
            /* 15 */ new Vec3(0.0f, 0.02712715f, 0.53454524f),
            /* 16 */ new Vec3(0.0f, -0.33614415f, 0.4165113f),
            /* 17 */ new Vec3(0.309017f, -0.38938415f, 0.19840115f),
            /* 18 */ new Vec3(0.5f, -0.05901699f, 0.18163565f),
            /* 19 */ new Vec3(-0.5f, -0.05901699f, 0.18163565f)
      };

      final Vec3[] normals = new Vec3[] {
            /* 00 */ new Vec3(-0.8506509f, 0.5f, 0.16245982f),
            /* 01 */ new Vec3(0.0f, -0.97147685f, 0.23713444f),
            /* 02 */ new Vec3(0.0f, 0.97147685f, -0.23713444f),
            /* 03 */ new Vec3(0.0f, -0.64655715f, -0.76286548f),
            /* 04 */ new Vec3(0.52573115f, 0.26286551f, -0.809017f),
            /* 05 */ new Vec3(0.0f, 0.64655715f, 0.76286548f),
            /* 06 */ new Vec3(-0.52573121f, 0.26286554f, -0.809017f),
            /* 07 */ new Vec3(-0.52573121f, -0.26286554f, 0.809017f),
            /* 08 */ new Vec3(0.52573115f, -0.26286551f, 0.809017f),
            /* 09 */ new Vec3(0.85065079f, 0.5f, 0.16245979f),
            /* 10 */ new Vec3(0.85065079f, -0.5f, -0.16245979f),
            /* 11 */ new Vec3(-0.8506509f, -0.5f, -0.16245982f)
      };

      final Vec2[] texCoords = new Vec2[] {
            /* 0 */ new Vec2(0.5f, 0.0f),
            /* 1 */ new Vec2(0.79389268f, 0.90450847f),
            /* 2 */ new Vec2(0.02447176f, 0.34549153f),
            /* 3 */ new Vec2(0.20610738f, 0.90450853f),
            /* 4 */ new Vec2(0.97552824f, 0.34549141f)
      };

      final int[][][] faces = new int[][][] {
            { { 2, 0, 2 }, { 10, 2, 2 }, { 0, 3, 2 },
                  { 14, 1, 2 }, { 1, 4, 2 } },
            { { 1, 0, 5 }, { 4, 2, 5 }, { 15, 3, 5 },
                  { 3, 1, 5 }, { 2, 4, 5 } },
            { { 7, 0, 1 }, { 17, 2, 1 }, { 16, 3, 1 },
                  { 6, 1, 1 }, { 5, 4, 1 } },
            { { 5, 0, 3 }, { 12, 2, 3 }, { 9, 3, 3 },
                  { 8, 1, 3 }, { 7, 4, 3 } },
            { { 9, 0, 4 }, { 0, 2, 4 }, { 10, 3, 4 },
                  { 11, 1, 4 }, { 8, 4, 4 } },
            { { 0, 0, 6 }, { 9, 2, 6 }, { 12, 3, 6 },
                  { 13, 1, 6 }, { 14, 4, 6 } },
            { { 16, 0, 7 }, { 15, 2, 7 }, { 4, 3, 7 },
                  { 19, 1, 7 }, { 6, 4, 7 } },
            { { 15, 0, 8 }, { 16, 2, 8 }, { 17, 3, 8 },
                  { 18, 1, 8 }, { 3, 4, 8 } },
            { { 11, 0, 9 }, { 10, 2, 9 }, { 2, 3, 9 },
                  { 3, 1, 9 }, { 18, 4, 9 } },
            { { 18, 0, 10 }, { 17, 2, 10 }, { 7, 3, 10 },
                  { 8, 1, 10 }, { 11, 4, 10 } },
            { { 13, 0, 11 }, { 12, 2, 11 }, { 5, 3, 11 },
                  { 6, 1, 11 }, { 19, 4, 11 } },
            { { 19, 0, 0 }, { 4, 2, 0 }, { 1, 3, 0 },
                  { 14, 1, 0 }, { 13, 4, 0 } }
      };

      target.name = "Dodecahedron";
      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * Creates a mesh from an array of strings. This is a simple
    * obj file reader. It assumes that the faces data of the
    * mesh includes texture coordinates and normals. Material
    * information is not parsed from the file, as Processing
    * would not accurately recreate it.
    *
    * @param lines
    *           the String tokens
    * @param target
    *           the output mesh
    * @return the mesh
    */
   public static Mesh3 fromObj (
         final String[] lines,
         final Mesh3 target ) {

      String[] tokens;
      String[] facetokens;

      final ArrayList < Vec3 > coordList = new ArrayList <>();
      final ArrayList < Vec2 > texCoordList = new ArrayList <>();
      final ArrayList < Vec3 > normalList = new ArrayList <>();
      final ArrayList < int[][] > faceList = new ArrayList <>();
      String name = target.hashIdentityString();

      final int len = lines.length;
      for (int i = 0; i < len; ++i) {

         /* Split line by spaces. */
         tokens = lines[i].split("\\s+");

         /* Skip empty lines. */
         if (tokens.length > 0) {
            final String initialToken = tokens[0].toLowerCase();
            if (initialToken.equals("o")) {

               /* Assign name. */
               name = tokens[1];

            } else if (initialToken.equals("v")) {

               /* Coordinate. */
               coordList.add(new Vec3(tokens[1], tokens[2], tokens[3]));

            } else if (initialToken.equals("vt")) {

               /* Texture coordinate. */
               texCoordList.add(new Vec2(tokens[1], tokens[2]));

            } else if (initialToken.equals("vn")) {

               /* Normal. */
               normalList.add(new Vec3(tokens[1], tokens[2], tokens[3]));

            } else if (initialToken.equals("f")) {

               /* Face. */
               final int count = tokens.length;

               /* tokens length includes "f", and so is 1 longer. */
               final int[][] indices = new int[count - 1][3];

               /*
                * Simplified. Assumes (incorrectly) that face will always
                * be formatted as "v/vt/vn".
                */
               for (int j = 1; j < count; ++j) {
                  facetokens = tokens[j].split("/");

                  final int k = j - 1;

                  /* Indices in .obj file start at 1, not 0. */
                  indices[k][0] = Mesh3.intFromStr(facetokens[0]) - 1;
                  indices[k][1] = Mesh3.intFromStr(facetokens[1]) - 1;
                  indices[k][2] = Mesh3.intFromStr(facetokens[2]) - 1;
               }

               faceList.add(indices);
            }
         }
      }

      /* Convert to fixed-sized array. */
      target.name = name;
      target.set(faceList.toArray(new int[faceList.size()][][]),
            coordList.toArray(new Vec3[coordList.size()]),
            texCoordList.toArray(new Vec2[texCoordList.size()]),
            normalList.toArray(new Vec3[normalList.size()]));
      return target;
   }

   /**
    * Creates an icosahedron, a Platonic solid with 20 faces
    * and 12 coords.
    *
    * @param target
    *           the output mesh
    * @return the icosahedron
    */
   public static final Mesh3 icosahedron ( final Mesh3 target ) {

      final Vec3[] coords = new Vec3[] {
            /* 00 */ new Vec3(0.0f, 0.5f, 0.0f),
            /* 01 */ new Vec3(0.42532045f, 0.22360751f, -0.13819239f),
            /* 02 */ new Vec3(0.00000225f, 0.22360751f, -0.44720721f),
            /* 03 */ new Vec3(-0.42532432f, 0.22360750f, -0.13819626f),
            /* 04 */ new Vec3(-0.26286000f, 0.22360748f, 0.36179957f),
            /* 05 */ new Vec3(0.26286399f, 0.22360748f, 0.36179706f),
            /* 06 */ new Vec3(0.26286000f, -0.22360748f, -0.36179957f),
            /* 07 */ new Vec3(-0.26286399f, -0.22360748f, -0.36179706f),
            /* 08 */ new Vec3(-0.42532045f, -0.22360751f, 0.13819239f),
            /* 09 */ new Vec3(-0.00000225f, -0.22360751f, 0.44720721f),
            /* 10 */ new Vec3(0.42532432f, -0.22360750f, 0.13819624f),
            /* 11 */ new Vec3(0.0f, -0.5f, 0.0f)
      };

      final Vec3[] normals = new Vec3[] {
            /* 00 */ new Vec3(-0.00000468f, -0.18758917f, -0.98224759f),
            /* 01 */ new Vec3(-0.93417150f, -0.18759677f, -0.30353081f),
            /* 02 */ new Vec3(-0.57735437f, -0.18758918f, 0.79465222f),
            /* 03 */ new Vec3(0.57734245f, -0.18758699f, 0.79466146f),
            /* 04 */ new Vec3(0.57735109f, -0.79465222f, -0.18759950f),
            /* 05 */ new Vec3(-0.57735270f, -0.79465240f, -0.18759337f),
            /* 06 */ new Vec3(0.35682699f, 0.79465109f, -0.49112532f),
            /* 07 */ new Vec3(-0.35681862f, 0.79465222f, -0.49112961f),
            /* 08 */ new Vec3(-0.57735115f, 0.79465222f, 0.18759952f),
            /* 09 */ new Vec3(0.57735276f, 0.79465240f, 0.18759334f),
            /* 10 */ new Vec3(0.00000289f, 0.79465103f, 0.60706645f),
            /* 11 */ new Vec3(0.93417150f, 0.18759677f, 0.30353081f),
            /* 12 */ new Vec3(0.57735437f, 0.18758918f, -0.79465222f),
            /* 13 */ new Vec3(-0.57734245f, 0.18758696f, -0.79466146f),
            /* 14 */ new Vec3(-0.93417013f, 0.18758696f, 0.30354115f),
            /* 15 */ new Vec3(0.00000468f, 0.18758917f, 0.98224759f),
            /* 16 */ new Vec3(-0.00000289f, -0.79465103f, -0.60706645f),
            /* 17 */ new Vec3(0.93417013f, -0.18758696f, -0.30354115f),
            /* 18 */ new Vec3(-0.35682699f, -0.79465109f, 0.49112532f),
            /* 19 */ new Vec3(0.35681865f, -0.79465222f, 0.49112961f)
      };

      final Vec2[] texCoords = new Vec2[] {
            /* 00 */ new Vec2(0.76286000f, 0.72360748f),
            /* 01 */ new Vec2(0.07467568f, 0.27639252f),
            /* 02 */ new Vec2(0.23713601f, 0.72360748f),
            /* 03 */ new Vec2(0.72360748f, 0.13820040f),
            /* 04 */ new Vec2(0.72360748f, 0.63819626f),
            /* 05 */ new Vec2(0.27639249f, 0.36180758f),
            /* 06 */ new Vec2(0.23714000f, 0.27639252f),
            /* 07 */ new Vec2(0.76286399f, 0.27639252f),
            /* 08 */ new Vec2(0.92532045f, 0.27639246f),
            /* 09 */ new Vec2(0.49999776f, 0.72360751f),
            /* 10 */ new Vec2(0.76286399f, 0.13820291f),
            /* 11 */ new Vec2(0.50000226f, 0.94720721f),
            /* 12 */ new Vec2(0.5f, 0.5f),
            /* 13 */ new Vec2(0.92532045f, 0.63819239f),
            /* 14 */ new Vec2(0.07467568f, 0.63819626f),
            /* 15 */ new Vec2(0.23714000f, 0.13820040f),
            /* 16 */ new Vec2(0.72360754f, 0.63819239f),
            /* 17 */ new Vec2(0.72360748f, 0.13820291f),
            /* 18 */ new Vec2(0.27639252f, 0.36180377f),
            /* 19 */ new Vec2(0.50000226f, 0.27639246f),
            /* 20 */ new Vec2(0.27639252f, 0.86179957f),
            /* 21 */ new Vec2(0.27639252f, 0.86179706f),
            /* 22 */ new Vec2(0.07467955f, 0.72360751f),
            /* 23 */ new Vec2(0.92532432f, 0.72360748f),
            /* 24 */ new Vec2(0.76286000f, 0.86179957f),
            /* 25 */ new Vec2(0.92532432f, 0.36180377f),
            /* 26 */ new Vec2(0.23713601f, 0.86179706f),
            /* 27 */ new Vec2(0.07467955f, 0.36180758f),
            /* 28 */ new Vec2(0.49999776f, 0.05279279f)
      };

      final int[][][] faces = new int[][][] {
            { { 0, 12, 6 }, { 1, 13, 6 }, { 2, 11, 6 } },
            { { 1, 13, 9 }, { 0, 12, 9 }, { 5, 10, 9 } },
            { { 0, 12, 7 }, { 2, 11, 7 }, { 3, 14, 7 } },
            { { 0, 12, 8 }, { 3, 14, 8 }, { 4, 15, 8 } },
            { { 0, 12, 10 }, { 4, 15, 10 }, { 5, 10, 10 } },
            { { 1, 16, 11 }, { 5, 17, 11 }, { 10, 18, 11 } },
            { { 2, 19, 12 }, { 1, 8, 12 }, { 6, 0, 12 } },
            { { 3, 1, 13 }, { 2, 19, 13 }, { 7, 2, 13 } },
            { { 4, 3, 14 }, { 3, 4, 14 }, { 8, 5, 14 } },
            { { 5, 7, 15 }, { 4, 6, 15 }, { 9, 9, 15 } },
            { { 1, 16, 17 }, { 10, 18, 17 }, { 6, 20, 17 } },
            { { 2, 19, 0 }, { 6, 0, 0 }, { 7, 2, 0 } },
            { { 3, 4, 1 }, { 7, 21, 1 }, { 8, 5, 1 } },
            { { 4, 6, 2 }, { 8, 22, 2 }, { 9, 9, 2 } },
            { { 5, 7, 3 }, { 9, 9, 3 }, { 10, 23, 3 } },
            { { 6, 24, 4 }, { 10, 25, 4 }, { 11, 12, 4 } },
            { { 7, 26, 16 }, { 6, 24, 16 }, { 11, 12, 16 } },
            { { 8, 27, 5 }, { 7, 26, 5 }, { 11, 12, 5 } },
            { { 9, 28, 18 }, { 8, 27, 18 }, { 11, 12, 18 } },
            { { 10, 25, 19 }, { 9, 28, 19 }, { 11, 12, 19 } }
      };

      target.name = "Icosahedron";
      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * Creates an octahedron, a Platonic solid with 8 faces and
    * 6 coords.
    *
    * @param target
    *           the output mesh
    * @return the octahedron
    */
   public static final Mesh3 octahedron ( final Mesh3 target ) {

      final Vec3[] coords = new Vec3[] {
            /* 0 */ new Vec3(0.0f, -0.5f, 0.0f),
            /* 1 */ new Vec3(0.5f, 0.0f, 0.0f),
            /* 2 */ new Vec3(-0.5f, 0.0f, 0.0f),
            /* 3 */ new Vec3(0.0f, 0.5f, 0.0f),
            /* 4 */ new Vec3(0.0f, 0.0f, 0.5f),
            /* 5 */ new Vec3(0.0f, 0.0f, -0.5f)
      };

      final Vec3[] normals = new Vec3[] {
            /* 0 */ new Vec3(0.57735026f, -0.57735026f, 0.57735026f),
            /* 1 */ new Vec3(-0.57735026f, 0.57735026f, 0.57735026f),
            /* 2 */ new Vec3(-0.57735026f, -0.57735026f, 0.57735026f),
            /* 3 */ new Vec3(0.57735026f, 0.57735026f, 0.57735026f),
            /* 4 */ new Vec3(-0.57735026f, 0.57735026f, -0.57735026f),
            /* 5 */ new Vec3(0.57735026f, 0.57735026f, -0.57735026f),
            /* 6 */ new Vec3(0.57735026f, -0.57735026f, -0.57735026f),
            /* 7 */ new Vec3(-0.57735026f, -0.57735026f, -0.57735026f)
      };

      final Vec2[] texCoords = new Vec2[] {
            /* 0 */ new Vec2(0.5f, 0.0f),
            /* 1 */ new Vec2(1.0f, 1.0f),
            /* 2 */ new Vec2(0.0f, 1.0f)
      };

      final int[][][] faces = new int[][][] {
            { { 0, 2, 0 }, { 1, 1, 0 }, { 4, 0, 0 } },
            { { 1, 2, 3 }, { 3, 1, 3 }, { 4, 0, 3 } },
            { { 3, 2, 1 }, { 2, 1, 1 }, { 4, 0, 1 } },
            { { 2, 2, 2 }, { 0, 1, 2 }, { 4, 0, 2 } },
            { { 2, 2, 4 }, { 3, 1, 4 }, { 5, 0, 4 } },
            { { 3, 2, 5 }, { 1, 1, 5 }, { 5, 0, 5 } },
            { { 1, 2, 6 }, { 0, 1, 6 }, { 5, 0, 6 } },
            { { 0, 2, 7 }, { 2, 1, 7 }, { 5, 0, 7 } }
      };

      target.name = "Octahedron";
      return target.set(faces, coords, texCoords, normals);
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
    * @param target
    *           the output mesh
    * @return the mesh
    */
   public static final Mesh3 plane (
         final int cols,
         final int rows,
         final Mesh3 target ) {

      final int rval = rows < 1 ? 1 : rows;
      final int cval = cols < 1 ? 1 : cols;

      final int rval1 = rval + 1;
      final int cval1 = cval + 1;

      final float iToStep = 1.0f / rval;
      final float jToStep = 1.0f / cval;

      final Vec3[] coords = new Vec3[rval1 * cval1];
      final Vec2[] texCoords = new Vec2[coords.length];
      final Vec3[] normals = new Vec3[] { Vec3.up(new Vec3()) };

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
            coords[k] = new Vec3(xs[j], y, 0.0f);
            texCoords[k] = new Vec2(us[j], v);
         }
      }

      final int[][][] faces = new int[2 * rval * cval][3][3];
      for (int k = 0, i = 0; i < rval; ++i) {
         final int noff0 = i * cval1;
         final int noff1 = (i + 1) * cval1;

         for (int j = 0; j < cval; ++j, k += 2) {
            final int n00 = noff0 + j;
            final int n10 = n00 + 1;
            final int n01 = noff1 + j;
            final int n11 = n01 + 1;

            faces[k] = new int[][] {
                  { n00, n00, 0 },
                  { n10, n10, 0 },
                  { n11, n11, 0 } };

            faces[k + 1] = new int[][] {
                  { n11, n11, 0 },
                  { n01, n01, 0 },
                  { n00, n00, 0 } };
         }
      }

      target.name = "Plane";
      return target.set(faces, coords, texCoords, normals);
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
   public static final Mesh3 plane (
         final int div,
         final Mesh3 target ) {

      return Mesh3.plane(div, div, target);
   }

   /**
    * Generates a regular convex polygon.
    *
    * @param sectors
    *           the number of sides
    * @param target
    *           the output mesh
    * @return the polygon
    */
   public static Mesh3 polygon (
         final int sectors,
         final Mesh3 target ) {

      final int seg = sectors < 3 ? 3 : sectors;
      final float toTheta = IUtils.TAU / seg;

      final Vec2 uvCenter = Vec2.uvCenter(new Vec2());
      final Vec2 pureCoord = new Vec2();

      final Vec3[] coords = new Vec3[seg + 1];
      final Vec2[] texCoords = new Vec2[seg + 1];
      final Vec3[] normals = new Vec3[] { Vec3.up(new Vec3()) };
      final int[][][] faces = new int[seg][3][3];

      coords[0] = new Vec3();
      texCoords[0] = uvCenter;

      for (int i = 0, j = 1; i < seg; ++i, ++j) {
         Vec2.fromPolar(i * toTheta, 0.5f, pureCoord);

         final Vec2 st = Vec2.add(uvCenter, pureCoord, new Vec2());
         st.y = 1.0f - st.y;
         texCoords[j] = st;

         coords[j] = new Vec3(pureCoord.x, pureCoord.y, 0.0f);

         final int k = 1 + j % seg;
         faces[i] = new int[][] {
               { 0, 0, 0 },
               { j, j, 0 },
               { k, k, 0 } };
      }

      target.name = "Polygon";
      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * Creates a UV sphere.
    *
    * @param longitudes
    *           the longitudes
    * @param latitudes
    *           the latitudes
    * @param target
    *           the output mesh
    * @return the sphere
    */
   public static Mesh3 sphere (
         final int longitudes,
         final int latitudes,
         final Mesh3 target ) {

      final int vlons = longitudes < 3 ? 3 : longitudes;
      final int vlats = latitudes < 3 ? 3 : latitudes;
      final int latLen = vlons * vlats + 2;
      final int latLast = latLen - 1;
      final Vec3[] coords = new Vec3[latLen];
      final Vec3[] normals = new Vec3[latLen];

      final float radius = 0.5f;

      coords[0] = new Vec3(0.0f, 0.0f, radius);
      coords[latLast] = new Vec3(0.0f, 0.0f, -radius);

      normals[0] = new Vec3(0.0f, 0.0f, 1.0f);
      normals[latLast] = new Vec3(0.0f, 0.0f, -1.0f);

      final float toPhi = 0.5f / (vlats + 1.0f);
      final float toTheta = 1.0f / vlons;

      for (int k = 1, i = 0, h = 1; i < vlats; ++i, ++h) {

         final float phi = h * toPhi - 0.25f;
         final float cosPhi = Utils.scNorm(phi);
         final float sinPhi = Utils.scNorm(phi - 0.25f);
         final float rhoCosPhi = radius * cosPhi;
         final float rhoSinPhi = radius * sinPhi;

         for (int j = 0; j < vlons; ++j, ++k) {

            final float theta = j * toTheta;
            final float cosTheta = Utils.scNorm(theta);
            final float sinTheta = Utils.scNorm(theta - 0.25f);

            coords[k] = new Vec3(
                  rhoCosPhi * cosTheta,
                  rhoCosPhi * sinTheta,
                  -rhoSinPhi);

            normals[k] = new Vec3(
                  cosPhi * cosTheta,
                  cosPhi * sinTheta,
                  -sinPhi);
         }
      }

      // TODO: UVs are screwed up.

      /* Texture coordinates. */
      final int vlons1 = vlons + 1;
      final int uvLen = vlons1 * vlats + 2;
      final int uvLast = uvLen - 1;
      final Vec2[] texCoords = new Vec2[uvLen];
      final float toV = 1.0f / (vlats + 1.0f);
      final float toU = 1.0f / vlons;
      texCoords[0] = new Vec2(0.0f, 0.0f);
      texCoords[uvLast] = new Vec2(0.0f, 1.0f);
      for (int k = 1, i = 0, h = 1; i < vlats; ++i, ++h) {
         final float v = h * toV;
         for (int j = 0; j < vlons1; ++j, ++k) {
            final float u = j * toU;
            texCoords[k] = new Vec2(u, v);
         }
      }

      final int[][][] faces = new int[2 * vlons1 * vlats][3][3];
      int idx = 0;

      /* Top cap. */
      for (int j = 0; j < vlons1; ++j) {

         final int n0 = 1 + j % vlons;
         final int n1 = 1 + (j + 1) % vlons;

         final int uv0 = 1 + j % vlons1;
         final int uv1 = 1 + (j + 1) % vlons1;

         faces[idx] = new int[][] {
               { n0, uv0, n0 },
               { n1, uv1, n1 },
               { 0, 0, 0 } };
         idx++;
      }

      /* Middle */
      final int vlatsn1 = vlats - 1;
      for (int i = 0; i < vlatsn1; ++i) {
         final int currLat = i * vlons;
         final int nextLat = (i + 1) * vlons;

         for (int j = 0; j < vlons1; ++j) {
            final int j0 = 1 + j % vlons;
            final int j1 = 1 + (j + 1) % vlons;

            final int uv0 = 1 + j % vlons1;
            final int uv1 = 1 + (j + 1) % vlons1;

            final int current = j0 + currLat;
            final int next = j1 + currLat;
            final int n1 = j0 + nextLat;
            final int n2 = j1 + nextLat;

            final int uvcurrent = uv0 + currLat;
            final int uvnext = uv1 + currLat;
            final int uvn1 = uv0 + nextLat;
            final int uvn2 = uv1 + nextLat;

            faces[idx] = new int[][] {
                  { current, uvcurrent, current },
                  { n1, uvn1, n1 },
                  { n2, uvn2, n2 } };
            idx++;

            faces[idx] = new int[][] {
                  { current, uvcurrent, current },
                  { n2, uvn2, n2 },
                  { next, uvnext, next } };
            idx++;
         }
      }

      /* Bottom cap. */
      final int lastCoord = coords.length - 1;
      final int lastTexCoord = texCoords.length - 1;
      for (int j = 0; j < vlons1; ++j) {
         final int n1 = lastCoord - (1 + j % vlons);
         final int n2 = lastCoord - (1 + (j + 1) % vlons);

         final int uv1 = lastTexCoord - (1 + j % vlons1);
         final int uv2 = lastTexCoord - (1 + (j + 1) % vlons1);

         faces[idx] = new int[][] {
               { lastCoord, lastTexCoord, lastCoord },
               { n1, uv1, n1 },
               { n2, uv2, n2 } };
         idx++;
      }

      target.name = "UV Sphere";
      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * Creates a UV sphere.
    *
    * @param target
    *           the output mesh
    * @return the sphere
    */
   public static Mesh3 sphere ( final Mesh3 target ) {

      return Mesh3.sphere(
            IMesh.DEFAULT_CIRCLE_SECTORS,
            IMesh.DEFAULT_CIRCLE_SECTORS >> 1,
            target);
   }

   /**
    * Creates a square.
    *
    * @param target
    *           the output mesh
    * @return the square
    */
   public static final Mesh3 square ( final Mesh3 target ) {

      final Vec3[] coords = new Vec3[] {
            new Vec3(-0.5f, 0.5f, 0.0f),
            new Vec3(0.5f, 0.5f, 0.0f),
            new Vec3(0.5f, -0.5f, 0.0f),
            new Vec3(-0.5f, -0.5f, 0.0f)
      };

      final Vec2[] texCoords = new Vec2[] {
            new Vec2(0.0f, 0.0f),
            new Vec2(1.0f, 0.0f),
            new Vec2(1.0f, 1.0f),
            new Vec2(0.0f, 1.0f)
      };

      final int[][][] faces = new int[][][] {
            { { 0, 0, 0 }, { 1, 1, 0 }, { 2, 2, 0 } },
            { { 2, 2, 0 }, { 3, 3, 0 }, { 0, 0, 0 } }
      };

      final Vec3[] normals = new Vec3[] { Vec3.up(new Vec3()) };

      target.name = "Square";
      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * Creates a tetrahedron, a Platonic solid with 4 faces and
    * 4 coords.
    *
    * @param target
    *           the output mesh
    * @return the tetrahedron
    */
   public static final Mesh3 tetrahedron ( final Mesh3 target ) {

      /*
       * double r = 0.5d; double a = r * Math.sqrt(8.0d / 9.0d);
       * double b = r * Math.sqrt(2.0d / 9.0d); double c = r *
       * Math.sqrt(2.0d / 3.0d); double d = r * (1.0d / 3.0d);
       *
       * 0.47140452079103168293389624140323,
       * 0.23570226039551584146694812070162,
       * 0.40824829046386301636621401245098
       */

      final Vec3[] coords = new Vec3[] {
            /* 0 */ new Vec3(-0.0f, 0.47140452f, -0.16666667f),
            /* 1 */ new Vec3(-0.40824829f, -0.23570226f, -0.16666667f),
            /* 2 */ new Vec3(0.40824829f, -0.23570226f, -0.16666667f),
            /* 3 */ new Vec3(0.0f, 0.0f, 0.5f)
      };

      final Vec3[] normals = new Vec3[] {
            /* 0 */ new Vec3(0.0f, 0.0f, -1.0f),
            /* 1 */ new Vec3(-0.8164966f, 0.47140453f, 0.33333333f),
            /* 2 */ new Vec3(0.0f, -0.9428091f, 0.33333333f),
            /* 3 */ new Vec3(0.8164966f, 0.47140453f, 0.33333333f)
      };

      final Vec2[] texCoords = new Vec2[] {
            /* 0 */ new Vec2(0.5f, 0.0f),
            /* 1 */ new Vec2(1.0f, 1.0f),
            /* 2 */ new Vec2(0.0f, 1.0f)
      };

      final int[][][] faces = new int[][][] {
            { { 0, 0, 0 }, { 2, 2, 0 }, { 1, 1, 0 } },
            { { 0, 2, 1 }, { 1, 1, 1 }, { 3, 0, 1 } },
            { { 0, 1, 3 }, { 3, 0, 3 }, { 2, 2, 3 } },
            { { 1, 2, 2 }, { 2, 1, 2 }, { 3, 0, 2 } }
      };

      target.name = "Tetrahedron";
      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * Creates a triangle.
    *
    * @param target
    *           the output mesh
    * @return the triangle
    */
   public static final Mesh3 triangle ( final Mesh3 target ) {

      final Vec3[] coords = new Vec3[] {
            new Vec3(0.0f, 0.5f, 0.0f),
            new Vec3(-0.4330127f, -0.25f, 0.0f),
            new Vec3(0.4330127f, -0.25f, 0.0f)
      };

      final Vec2[] texCoords = new Vec2[] {
            new Vec2(0.5f, 0.0f),
            new Vec2(0.0669873f, 0.75f),
            new Vec2(0.9330127f, 0.75f)
      };

      final Vec3[] normals = new Vec3[] { Vec3.up(new Vec3()) };

      final int[][][] faces = new int[][][] { {
            { 0, 0, 0 },
            { 1, 1, 0 },
            { 2, 2, 0 } } };

      target.name = "Triangle";
      return target.set(faces, coords, texCoords, normals);
   }

   /**
    * An array of coordinates in the mesh.
    */
   public Vec3[] coords;

   /**
    * The faces array does not include face data itself, but
    * rather indices to other arrays which contain vertex data.
    * It is a three-dimensional array organized by
    * <ol>
    * <li>the number of faces;</li>
    * <li>the number of vertices per faces;</li>
    * <li>the information per face;</li>
    * </ol>
    * 2D meshes contain two pieces of information per vertex:
    * spatial coordinates and texture coordinates.
    */
   public int[][][] faces;

   /**
    * An array of normals to indicate how light will bounce off
    * the mesh's surface.
    */
   public Vec3[] normals;

   /**
    * The texture (UV) coordinates that describe how an image
    * is mapped onto the geometry of the mesh. Typically in the
    * range [0.0, 1.0].
    */
   public Vec2[] texCoords;

   /**
    * The default constructor.
    */
   public Mesh3 () {

      super();
   }

   /**
    * Creates a mesh from arrays of faces, coordinates, texture
    * coordinates and normals. The mesh's arrays are set by
    * reference, not by value.
    *
    * @param faces
    *           the faces array
    * @param coords
    *           the coordinates array
    * @param texCoords
    *           the texture coordinates array
    * @param normals
    *           the normals array
    */
   public Mesh3 (
         final int[][][] faces,
         final Vec3[] coords,
         final Vec2[] texCoords,
         final Vec3[] normals ) {

      super();
      this.set(faces, coords, texCoords, normals);
   }

   /**
    * Constructs a copy of the source mesh.
    *
    * @param source
    *           the source mesh
    */
   public Mesh3 ( final Mesh3 source ) {

      super();
      this.set(source);
   }

   /**
    * Creates a named mesh.
    *
    * @param name
    *           the mesh name
    */
   public Mesh3 ( final String name ) {

      super(name);
   }

   /**
    * Creates a named mesh from arrays of faces, coordinates,
    * texture coordinates and normals. The mesh's arrays are
    * set by reference, not by value.
    *
    * @param name
    *           the mesh name
    * @param faces
    *           the faces array
    * @param coords
    *           the coordinates array
    * @param texCoords
    *           the texture coordinates array
    * @param normals
    *           the normals array
    */
   public Mesh3 (
         final String name,
         final int[][][] faces,
         final Vec3[] coords,
         final Vec2[] texCoords,
         final Vec3[] normals ) {

      super(name);
      this.set(faces, coords, texCoords, normals);
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
         result.append(this.coords[i].toBlenderCode());
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

      result.append("], \"normals\": [");

      final int nlen = this.normals.length;
      final int nlast = nlen - 1;
      for (int h = 0; h < nlen; ++h) {
         result.append(this.normals[h].toBlenderCode());
         if (h < nlast) {
            result.append(',').append(' ');
         }
      }

      result.append(']').append('}');
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

            final Vec3 v = this.coords[fs1[0]];
            final Vec2 vt = this.texCoords[fs1[1]];
            final Vec3 vn = this.normals[fs1[2]];

            vs.append(v.toUnityCode());
            vts.append(vt.toUnityCode());
            vns.append(vn.toUnityCode());
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
    * @param mesh3
    *           the mesh
    * @return the evaluation
    */
   protected boolean equals ( final Mesh3 mesh3 ) {

      if (!Arrays.equals(this.coords, mesh3.coords)) {
         return false;
      }

      if (!Arrays.deepEquals(this.faces, mesh3.faces)) {
         return false;
      }

      return true;
   }

   /**
    * Attempts to recalculate the normals of this mesh per
    * vertex. If the normals array is null, or if its length is
    * not equal to the length of coordinates, the normals array
    * is reallocated.
    *
    * @return this mesh
    */
   @Experimental
   @Chainable
   public Mesh3 calcNormals () {

      if (this.normals == null || this.normals.length != this.coords.length) {
         final int len = this.coords.length;
         this.normals = new Vec3[len];
         for (int h = 0; h < len; ++h) {
            this.normals[h] = new Vec3();
         }
      }

      Vec3 prev = null;
      Vec3 curr = null;
      Vec3 next = null;
      Vec3 normal = null;

      final Vec3 edge0 = new Vec3();
      final Vec3 edge1 = new Vec3();

      final int len0 = this.faces.length;
      for (int i = 0; i < len0; ++i) {

         final int[][] faceIndices = this.faces[i];
         final int len1 = faceIndices.length;
         prev = this.coords[faceIndices[len1 - 1][0]];

         for (int j = 0, k = 1; j < len1; ++j, ++k) {

            final int[] faceIndex = faceIndices[j];
            final int currIndex = faceIndex[0];
            final int nextIndex = faceIndices[k % len1][0];

            /* Acquire normal and update face index reference to it. */
            normal = this.normals[currIndex];
            faceIndex[2] = currIndex;

            curr = this.coords[currIndex];
            next = this.coords[nextIndex];

            Vec3.sub(curr, prev, edge0);
            // Should this be next - curr?
            // Vec3.sub(next, prev, e1);
            Vec3.sub(next, curr, edge1);
            Vec3.crossNorm(edge0, edge1, normal);

            prev = curr;
         }
      }

      return this;
   }

   /**
    * Clones this mesh.
    *
    * @return the cloned mesh
    */
   @Override
   public Mesh3 clone () {

      return new Mesh3(this);
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

      return this.equals((Mesh3) obj);
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
   @Experimental
   public Edge3 getEdge (
         final int i,
         final int j,
         final Edge3 target ) {

      final int[][] f0 = this.faces[Math.floorMod(
            i, this.faces.length)];
      final int f0len = f0.length;
      final int[] f1 = f0[Math.floorMod(j, f0len)];
      final int[] f2 = f0[Math.floorMod(j + 1, f0len)];

      return target.set(
            this.coords[f1[0]],
            this.texCoords[f1[1]],
            this.normals[f1[2]],

            this.coords[f2[0]],
            this.texCoords[f2[1]],
            this.normals[f2[2]]);
   }

   /**
    * Gets an array of edges from the mesh.
    *
    * @return the edges array
    */
   @Experimental
   public Edge3[] getEdges () {

      final ArrayList < Edge3 > result = new ArrayList <>();
      Edge3 trial = new Edge3();
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
                  this.normals[fo[2]],

                  this.coords[fd[0]],
                  this.texCoords[fd[1]],
                  this.normals[fd[2]]);

            if (!result.contains(trial)) {
               result.add(trial);
               trial = new Edge3();
            }
         }
      }

      return result.toArray(new Edge3[result.size()]);
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
   public Face3 getFace (
         final int i,
         final Face3 target ) {

      final int[][] face = this.faces[Math.floorMod(i, this.faces.length)];
      final int len = face.length;
      final Vert3[] vertices = new Vert3[len];

      for (int j = 0; j < len; ++j) {
         final int[] vert = face[j];
         vertices[j] = new Vert3(
               this.coords[vert[0]],
               this.texCoords[vert[1]],
               this.normals[vert[2]]);
      }

      return target.set(vertices);
   }

   /**
    * Gets an array of faces from the mesh.
    *
    * @return the faces array
    */
   public Face3[] getFaces () {

      final int len0 = this.faces.length;
      final Face3[] result = new Face3[len0];

      for (int i = 0; i < len0; ++i) {

         final int[][] fs0 = this.faces[i];
         final int len1 = fs0.length;
         final Vert3[] verts = new Vert3[len1];

         for (int j = 0; j < len1; ++j) {

            final int[] fs1 = fs0[j];
            verts[j] = new Vert3(
                  this.coords[fs1[0]],
                  this.texCoords[fs1[1]],
                  this.normals[fs1[2]]);
         }

         result[i] = new Face3(verts);
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
   public Vert3 getVertex (
         final int i,
         final int j,
         final Vert3 target ) {

      final int[][] f0 = this.faces[Math.floorMod(
            i, this.faces.length)];
      final int[] f = f0[Math.floorMod(
            j, f0.length)];

      return target.set(
            this.coords[f[0]],
            this.texCoords[f[1]],
            this.normals[f[2]]);
   }

   /**
    * Get an array of vertices from the mesh.
    *
    * @return the vertices
    */
   public Vert3[] getVertices () {

      final ArrayList < Vert3 > result = new ArrayList <>();
      Vert3 trial = new Vert3();
      final int len0 = this.faces.length;

      for (int i = 0; i < len0; ++i) {

         final int[][] fs = this.faces[i];
         final int len1 = fs.length;

         for (int j = 0; j < len1; ++j) {

            final int[] f = fs[j];
            trial.set(
                  this.coords[f[0]],
                  this.texCoords[f[1]],
                  this.normals[f[2]]);

            if (!result.contains(trial)) {
               result.add(trial);
               trial = new Vert3();
            }
         }
      }

      return result.toArray(new Vert3[result.size()]);
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
    * Flips the indices which specify an edge.
    *
    * @param i
    *           face index
    * @param j
    *           edge index
    * @return this mesh
    */
   public Mesh3 reverseEdge ( final int i, final int j ) {

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
   public Mesh3 reverseFace ( final int i ) {

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
    * Rotates all coordinates and normals in the mesh by an
    * angle around an arbitrary axis.
    *
    * @param radians
    *           the angle in radians
    * @param axis
    *           the axis of rotation
    * @return this mesh
    * @see Vec3#rotate(Vec3, float, Vec3, Vec3)
    */
   @Chainable
   public Mesh3 rotate (
         final float radians,
         final Vec3 axis ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      Vec3 c;
      final int len0 = this.coords.length;
      for (int i = 0; i < len0; ++i) {
         c = this.coords[i];
         Vec3.rotate(c, cosa, sina, axis, c);
      }

      Vec3 n;
      final int len1 = this.normals.length;
      for (int j = 0; j < len1; ++j) {
         n = this.normals[j];
         Vec3.rotate(n, cosa, sina, axis, n);
      }

      return this;
   }

   /**
    * Rotates all coordinates and normals in the mesh by a
    * quaternion.
    *
    * @param q
    *           the quaternion
    * @return the mesh
    */
   @Chainable
   public Mesh3 rotate ( final Quaternion q ) {

      Vec3 c;
      final int len0 = this.coords.length;
      for (int i = 0; i < len0; ++i) {
         c = this.coords[i];
         Quaternion.mulVector(q, c, c);
      }

      Vec3 n;
      final int len1 = this.normals.length;
      for (int j = 0; j < len1; ++j) {
         n = this.normals[j];
         Quaternion.mulVector(q, n, n);
      }

      return this;
   }

   /**
    * Rotates all coordinates and normals in the mesh by an
    * angle around the x axis.
    *
    * @param radians
    *           the angle in radians
    * @return this mesh
    * @see Vec3#rotateX(Vec3, float, Vec3)
    */
   @Chainable
   public Mesh3 rotateX ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      Vec3 c;
      final int len0 = this.coords.length;
      for (int i = 0; i < len0; ++i) {
         c = this.coords[i];
         Vec3.rotateX(c, cosa, sina, c);
      }

      Vec3 n;
      final int len1 = this.normals.length;
      for (int j = 0; j < len1; ++j) {
         n = this.normals[j];
         Vec3.rotateX(n, cosa, sina, n);
      }

      return this;
   }

   /**
    * Rotates all coordinates and normals in the mesh by an
    * angle around the y axis.
    *
    * @param radians
    *           the angle in radians
    * @return this mesh
    * @see Vec3#rotateY(Vec3, float, Vec3)
    */
   @Chainable
   public Mesh3 rotateY ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      Vec3 c;
      final int len0 = this.coords.length;
      for (int i = 0; i < len0; ++i) {
         c = this.coords[i];
         Vec3.rotateY(c, cosa, sina, c);
      }

      Vec3 n;
      final int len1 = this.normals.length;
      for (int j = 0; j < len1; ++j) {
         n = this.normals[j];
         Vec3.rotateY(n, cosa, sina, n);
      }

      return this;
   }

   /**
    * Rotates all coordinates and normals in the mesh by an
    * angle around the z axis.
    *
    * @param radians
    *           the angle in radians
    * @return this mesh
    * @see Vec3#rotateZ(Vec3, float, Vec3)
    */
   @Chainable
   public Mesh3 rotateZ ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      Vec3 c;
      final int len0 = this.coords.length;
      for (int i = 0; i < len0; ++i) {
         c = this.coords[i];
         Vec3.rotateZ(c, cosa, sina, c);
      }

      Vec3 n;
      final int len1 = this.normals.length;
      for (int j = 0; j < len1; ++j) {
         n = this.normals[j];
         Vec3.rotateZ(n, cosa, sina, n);
      }

      return this;
   }

   /**
    * Scales all coordinates in the mesh by a scalar.
    *
    * @param scale
    *           the scalar
    * @return this mesh
    * @see Vec3#mul(Vec3, float, Vec3)
    */
   @Chainable
   public Mesh3 scale ( final float scale ) {

      Vec3 c;
      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         c = this.coords[i];
         Vec3.mul(c, scale, c);
      }

      return this;
   }

   /**
    * Scales all coordinates in the mesh by a vector.
    *
    * @param scale
    *           the vector
    * @return this mesh
    * @see Vec3#mul(Vec3, Vec3, Vec3)
    */
   @Chainable
   public Mesh3 scale ( final Vec3 scale ) {

      Vec3 c;
      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         c = this.coords[i];
         Vec3.mul(c, scale, c);
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
    * @param normals
    *           the normals array
    * @return this mesh
    */
   @Chainable
   public Mesh3 set (
         final int[][][] faces,
         final Vec3[] coords,
         final Vec2[] texCoords,
         final Vec3[] normals ) {

      this.faces = faces;
      this.coords = coords;
      this.texCoords = texCoords;
      this.normals = normals;
      return this;
   }

   /**
    * Sets this mesh to a copy of the source. Allocates new
    * arrays for coordinates, texture coordinates, normals and
    * faces.
    *
    * @param source
    *           the source mesh
    * @return this mesh
    */
   @Chainable
   public Mesh3 set ( final Mesh3 source ) {

      /* Copy coordinates. */
      final Vec3[] sourcevs = source.coords;
      final int vslen = sourcevs.length;
      this.coords = new Vec3[vslen];
      for (int i = 0; i < vslen; ++i) {
         this.coords[i] = new Vec3(sourcevs[i]);
      }

      /* Copy texture coordinates. */
      final Vec2[] sourcevts = source.texCoords;
      final int vtslen = sourcevts.length;
      this.texCoords = new Vec2[vtslen];
      for (int j = 0; j < vtslen; ++j) {
         this.texCoords[j] = new Vec2(sourcevts[j]);
      }

      /* Copy normals. */
      final Vec3[] sourcevns = source.normals;
      final int vnslen = sourcevns.length;
      this.normals = new Vec3[vnslen];
      for (int k = 0; k < vnslen; ++k) {
         this.normals[k] = new Vec3(sourcevns[k]);
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
    * Renders the mesh as a string following the Wavefront OBJ
    * file format.
    *
    * @return the string
    */
   public String toObjString () {

      final int coordsLen = this.coords.length;
      final int texCoordsLen = this.texCoords.length;
      final int normalsLen = this.normals.length;
      final int facesLen = this.faces.length;
      final StringBuilder result = new StringBuilder();

      /*
       * Append a comment listing the number of coordinates,
       * texture coordinates, normals and faces.
       */
      result.append("# v: ").append(coordsLen)
            .append(", vt: ").append(texCoordsLen)
            .append(", vn: ").append(normalsLen)
            .append(", f: ").append(facesLen)
            .append('\n').append('\n');

      result.append('o').append(' ').append(this.name)
            .append('\n').append('\n');

      /* Write coordinates. */
      for (final Vec3 coord : this.coords) {
         result.append('v').append(' ')
               .append(coord.toObjString())
               .append('\n');
      }
      result.append('\n');

      /* Write texture coordinates. */
      for (final Vec2 texCoord : this.texCoords) {
         result.append("vt ")
               .append(texCoord.toObjString())
               .append('\n');
      }
      result.append('\n');

      /* Write normals. */
      for (final Vec3 normal : this.normals) {
         result.append("vn ")
               .append(normal.toObjString())
               .append('\n');
      }
      result.append('\n');

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
                  .append(vert[2] + 1);
         }

         result.append('\n');
      }

      result.append('\n');
      return result.toString();
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
            .append('\"')
            .append(',')
            .append(' ')
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
      }

      if (this.coords.length > truncate) {
         sb.append("\n/* ... */");
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
      }

      if (this.texCoords.length > truncate) {
         sb.append("\n/* ... */");
      }

      sb.append(" ],\nnormals: [");
      if (this.normals != null) {
         sb.append('\n');
         final int len = Math.min(this.normals.length, truncate);
         final int last = len - 1;
         for (int i = 0; i < len; ++i) {
            sb.append(this.normals[i].toString(places));
            if (i < last) {
               sb.append(',').append(' ');
               sb.append('\n');
            }
         }
      }

      if (this.normals.length > truncate) {
         sb.append("\n/* ... */");
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

               /*
                * 3 indices: coordinate, texture coordinate and normal.
                */
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
      }

      if (this.faces.length > truncate) {
         sb.append("\n/* ... */");
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
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   @Chainable
   public Mesh3 translate ( final Vec3 v ) {

      Vec3 c;
      final int len = this.coords.length;
      for (int i = 0; i < len; ++i) {
         c = this.coords[i];
         Vec3.add(c, v, c);
      }
      return this;
   }
}
