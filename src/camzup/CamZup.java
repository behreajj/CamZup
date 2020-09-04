package camzup;

import camzup.core.IUtils;
import camzup.core.Mesh3;
import camzup.core.PolyType;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;

import processing.core.PApplet;

/**
 * The main class of this library. This is not needed to use the library
 * and is for development and debugging only.
 */
public class CamZup {

   /**
    * The PApplet referenced by this class.
    */
   public final PApplet parent;

   /**
    * Constructs a new instance of this library with the PApplet as a
    * reference.
    *
    * @param parent the parent applet
    */
   public CamZup ( final PApplet parent ) { this.parent = parent; }

   /**
    * Returns a string representation of the CamZup class.
    *
    * @return the string
    */
   @Override
   public String toString ( ) {

      final StringBuilder sb = new StringBuilder(64);
      sb.append("{ version: ");
      sb.append(CamZup.VERSION);
      sb.append(", parent: ");
      sb.append(this.parent);
      sb.append(" }");
      return sb.toString();
   }

   /**
    * The library's current version.
    */
   public static final String VERSION = "##library.prettyVersion##";

   public static Mesh3 capsule ( final int longitudes, final int latitudes,
      final int rings, final float depth, final float radius,
      final PolyType poly, final Mesh3 target ) {

      /*
       * Require that latitudes be even so that they can be split about the
       * equator.
       */
      final int verifLats = latitudes < 2 ? 2 : latitudes % 2 != 0 ? latitudes
         + 1 : latitudes;
      final int verifLons = longitudes < 3 ? 3 : longitudes;
      final int verifRings = rings < 0 ? 0 : rings;
      final float verifDepth = Utils.max(IUtils.EPSILON, depth);
      final float verifRad = Utils.max(IUtils.EPSILON, radius);

      /* Boolean flags that change number of faces and vertices. */
      final boolean useQuads = poly == PolyType.QUAD;
      final boolean calcMid = verifRings > 0;

      /* Intermediary calculations. */
      final int halfLats = verifLats / 2;
      final int halfLatsN1 = halfLats - 1;
      final int halfLatsN2 = halfLats - 2;
      final int verifRingsP1 = verifRings + 1;
      final int verifLonsP1 = verifLons + 1;

      final int lonsHalfLatN1 = halfLatsN1 * verifLons;
      final int lonsRingsP1 = verifRingsP1 * verifLons;

      final float halfDepth = verifDepth * 0.5f;
      final float summit = halfDepth + verifRad;

      /* Index offsets for coordinates. */
      final int idxVNEquator = verifLonsP1 + verifLons * halfLatsN2;
      final int idxVCyl = idxVNEquator + verifLons;
      final int idxVSEquator = calcMid ? idxVCyl + verifLons * verifRings
         : idxVCyl;
      final int idxVSouth = idxVSEquator + verifLons;
      final int idxVSouthCap = idxVSouth + verifLons * halfLatsN2;
      final int idxVSouthPole = idxVSouthCap + verifLons;

      /* Index offsets for texture coordinates. */
      final int idxVtNEquator = verifLons + verifLonsP1 * halfLatsN1;
      final int idxVtCyl = idxVtNEquator + verifLonsP1;
      final int idxVtSEquator = calcMid ? idxVtCyl + verifLonsP1 * verifRings
         : idxVtCyl;
      final int idxVtSHemi = idxVtSEquator + verifLonsP1;
      final int idxVtSPolar = idxVtSHemi + verifLonsP1 * halfLatsN2;
      final int idxVtSCap = idxVtSPolar + verifLonsP1;

      /* Index offsets for normals. */
      final int idxVnSouth = idxVNEquator + verifLons;
      final int idxVnSouthCap = idxVnSouth + verifLons * halfLatsN2;
      final int idxVnSouthPole = idxVnSouthCap + verifLons;

      /* Array lengths. */
      final int vsLen = idxVSouthPole + 1;
      final int vtsLen = idxVtSCap + verifLons;
      final int vnsLen = idxVnSouthPole + 1;

      /* Allocate mesh data. */
      final Vec3[] vs = target.coords = Vec3.resize(target.coords, vsLen);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
         vtsLen);
      final Vec3[] vns = target.normals = Vec3.resize(target.normals, vnsLen);

      /* Set North pole: coordinate, normal. */
      vs[0].set(0.0f, 0.0f, summit);
      vns[0].set(0.0f, 0.0f, 1.0f);

      /* Set South pole: coordinate, normal. */
      vs[idxVSouthPole].set(0.0f, 0.0f, -summit);
      vns[idxVnSouthPole].set(0.0f, 0.0f, -1.0f);

      /*
       * Calculate polar texture coordinates. UVs form a triangle at the poles,
       * where the polar vertex is centered between the other two vertices. That
       * is why j is offset by 0.5 . There is one fewer column of UVs at the
       * poles, so the for loop uses the coordinate longitude range. Calculate
       * theta and equators. Cache sine and cosine of theta.
       */
      final float[] sinThetaCache = new float[verifLons];
      final float[] cosThetaCache = new float[verifLons];
      final float toTheta = IUtils.TAU / verifLons;
      final float toPhi = IUtils.PI / verifLats;
      final float toTexHorizontal = 1.0f / verifLons;
      final float toTexVertical = 1.0f / halfLats;

      for ( int j = 0; j < verifLons; ++j ) {
         final float jf = j;

         /* Coordinates. */
         final float theta = jf * toTheta;
         final float sinTheta = Utils.sin(theta);
         final float cosTheta = Utils.cos(theta);
         sinThetaCache[j] = sinTheta;
         cosThetaCache[j] = cosTheta;

         /* Texture coordinates at North and South pole. */
         final float sTex = ( jf + 0.5f ) * toTexHorizontal;
         vts[j].set(sTex, 0.0f);
         vts[idxVtSCap + j].set(sTex, 1.0f);

         /* Multiply by radius to get equatorial x and y. */
         final float x = verifRad * cosTheta;
         final float y = verifRad * sinTheta;

         /* Set equatorial coordinates. Offset by cylinder depth. */
         vs[idxVNEquator + j].set(x, y, halfDepth);
         vs[idxVSEquator + j].set(x, y, -halfDepth);

         /* Set equatorial normals. */
         vns[idxVNEquator + j].set(cosTheta, sinTheta, 0.0f);
      }

      /* Simplistic UV aspect ratio: uses 1/3 and 2/3. */
      // float vtAspectNorth = 1.0f / 3.0f;
      // float vtAspectNorth = ( float ) halfLats / ( verifRingsP1 + verifLats
      // );
      final float vtAspectNorth = verifRad / ( verifDepth + verifRad
         + verifRad );
      final float vtAspectSouth = 1.0f - vtAspectNorth;

      /* Calculate equatorial texture coordinates. Cache horizontal measure. */
      final float[] sTexCache = new float[verifLonsP1];

      for ( int j = 0; j < verifLonsP1; ++j ) {
         final float sTex = j * toTexHorizontal;
         sTexCache[j] = sTex;
         vts[idxVtNEquator + j].set(sTex, vtAspectNorth);
         vts[idxVtSEquator + j].set(sTex, vtAspectSouth);
      }

      /* Divide latitudes into hemispheres. Start at i = 1 due to the poles. */
      int vHemiOffsetNorth = 1;
      int vHemiOffsetSouth = idxVSouth;
      int vtHemiOffsetNorth = verifLons;
      int vtHemiOffsetSouth = idxVtSHemi;
      int vnHemiOffsetSouth = idxVnSouth;

      for ( int i = 1; i < halfLats; ++i ) {

         final float phi = i * toPhi;

         /*
          * Use trigonometric symmetries to avoid calculating another sine and
          * cosine for phi North.
          */
         final float sinPhiSouth = Utils.sin(phi);
         final float cosPhiSouth = Utils.cos(phi);

         final float sinPhiNorth = -cosPhiSouth;
         final float cosPhiNorth = sinPhiSouth;

         /* For North coordinates, multiply by radius and offset. */
         final float rhoCosPhiNorth = verifRad * cosPhiNorth;
         final float rhoSinPhiNorth = verifRad * sinPhiNorth;
         final float zOffsetNorth = halfDepth - rhoSinPhiNorth;

         /* For South coordinates, multiply by radius and offset. */
         final float rhoCosPhiSouth = verifRad * cosPhiSouth;
         final float rhoSinPhiSouth = verifRad * sinPhiSouth;
         final float zOffsetSouth = -halfDepth - rhoSinPhiSouth;

         /* Coordinates */
         for ( int j = 0; j < verifLons; ++j ) {
            final float sinTheta = sinThetaCache[j];
            final float cosTheta = cosThetaCache[j];

            /* @formatter:off */

            /* North coordinate. */
            vs[vHemiOffsetNorth].set(
               rhoCosPhiNorth * cosTheta,
               rhoCosPhiNorth * sinTheta,
               zOffsetNorth);

            /* North normal. */
            vns[vHemiOffsetNorth].set(
               cosPhiNorth * cosTheta,
               cosPhiNorth * sinTheta,
               -sinPhiNorth);

            /* South coordinate. */
            vs[vHemiOffsetSouth].set(
               rhoCosPhiSouth * cosTheta,
               rhoCosPhiSouth * sinTheta,
               zOffsetSouth);

            /* South normal. */
            vns[vnHemiOffsetSouth].set(
               cosPhiSouth * cosTheta,
               cosPhiSouth * sinTheta,
               -sinPhiSouth);

            /* @formatter:on */

            ++vHemiOffsetNorth;
            ++vHemiOffsetSouth;
            ++vnHemiOffsetSouth;
         }

         /*
          * For UVs, linear interpolation from North pole to North aspect ratio;
          * and from South pole to South aspect ratio.
          */
         final float tTexFac = i * toTexVertical;
         final float tTexNorth = tTexFac * vtAspectNorth;
         final float tTexSouth = ( 1.0f - tTexFac ) * vtAspectSouth + tTexFac;

         /* Texture coordinates. */
         for ( int j = 0; j < verifLonsP1; ++j ) {
            final float sTex = sTexCache[j];
            vts[vtHemiOffsetNorth].set(sTex, tTexNorth);
            vts[vtHemiOffsetSouth].set(sTex, tTexSouth);

            ++vtHemiOffsetNorth;
            ++vtHemiOffsetSouth;
         }
      }

      /* Calculate sections of cylinder in middle. */
      if ( calcMid ) {

         /*
          * Linear interpolation must exclude the origin (North equator) and the
          * destination (South equator), so step must never equal 0.0 or 1.0 .
          */
         final float toFac = 1.0f / verifRingsP1;
         int vCylOffset = idxVCyl;
         int vtCylOffset = idxVtCyl;
         for ( int m = 1; m < verifRingsP1; ++m ) {
            final float fac = m * toFac;
            final float cmplFac = 1.0f - fac;

            /* Coordinates. */
            for ( int j = 0; j < verifLons; ++j ) {

               final Vec3 vEquatorNorth = vs[idxVNEquator + j];
               final Vec3 vEquatorSouth = vs[idxVSEquator + j];

               /*
                * xy should be the same for both North and South. North z should
                * equal half_depth while South z should equal -half_depth.
                * However this is kept as a linear interpolation for clarity.
                */
               vs[vCylOffset].set(cmplFac * vEquatorNorth.x + fac
                  * vEquatorSouth.x, cmplFac * vEquatorNorth.y + fac
                     * vEquatorSouth.y, cmplFac * vEquatorNorth.z + fac
                        * vEquatorSouth.z);

               ++vCylOffset;
            }

            /* Texture coordinates. */
            final float tTex = cmplFac * vtAspectNorth + fac * vtAspectSouth;
            for ( int j = 0; j < verifLonsP1; ++j ) {
               final float sTex = sTexCache[j];
               vts[vtCylOffset].set(sTex, tTex);
               ++vtCylOffset;
            }
         }
      }

      /* Find index offsets for face indices. */
      final int idxFsCyl = useQuads ? verifLons + lonsHalfLatN1 : verifLons
         + lonsHalfLatN1 * 2;
      final int idxFsSouthEquat = useQuads ? idxFsCyl + lonsRingsP1 : idxFsCyl
         + lonsRingsP1 * 2;
      final int idxFsSouthHemi = useQuads ? idxFsSouthEquat + lonsHalfLatN1
         : idxFsSouthEquat + lonsHalfLatN1 * 2;

      /* Resize face indices to new length. */
      final int lenIndices = idxFsSouthHemi + verifLons;
      final int[][][] fs = target.faces = new int[lenIndices][][];

      /* North & South cap indices (always triangles). */
      for ( int j = 0; j < verifLons; ++j ) {
         final int jNextVt = j + 1;
         final int jNextV = jNextVt % verifLons;

         /* North indices. */
         final int[][] triNorth = fs[j] = new int[3][3];

         final int[] north0 = triNorth[0];
         north0[0] = 0;
         north0[1] = j;
         north0[2] = 0;

         final int[] north1 = triNorth[1];
         north1[0] = jNextVt;
         north1[1] = verifLons + j;
         north1[2] = jNextVt;

         final int[] north2 = triNorth[2];
         north2[0] = 1 + jNextV;
         north2[1] = verifLons + jNextVt;
         north2[2] = 1 + jNextV;

         /* South indices. */
         final int[][] triSouth = fs[idxFsSouthHemi + j] = new int[3][3];

         final int[] south0 = triSouth[0];
         south0[0] = idxVSouthPole;
         south0[1] = idxVtSCap + j;
         south0[2] = idxVnSouthPole;

         final int[] south1 = triSouth[1];
         south1[0] = idxVSouthCap + jNextV;
         south1[1] = idxVtSPolar + jNextVt;
         south1[2] = idxVnSouthCap + jNextV;

         final int[] south2 = triSouth[2];
         south2[0] = idxVSouthCap + j;
         south2[1] = idxVtSPolar + j;
         south2[2] = idxVnSouthCap + j;
      }

      /* Hemisphere indices. */
      int fHemiOffsetNorth = verifLons;
      int fHemiOffsetSouth = idxFsSouthEquat;
      for ( int i = 0; i < halfLatsN1; ++i ) {
         final int iLons = i * verifLons;

         /* North coordinate index offset. */
         final int vCurrLatN = 1 + iLons;
         final int vNextLatN = vCurrLatN + verifLons;

         /* South coordinate index offset. */
         final int vCurrLatS = idxVSEquator + iLons;
         final int vNextLatS = vCurrLatS + verifLons;

         /* North texture coordinate index offset. */
         final int vtCurrLatN = verifLons + i * verifLonsP1;
         final int vtNextLatN = vtCurrLatN + verifLonsP1;

         /* South texture coordinate index offset. */
         final int vtCurrLatS = idxVtSEquator + i * verifLonsP1;
         final int vtNextLatS = vtCurrLatS + verifLonsP1;

         /* North normal index offset. */
         final int vnCurrLatN = 1 + iLons;
         final int vnNextLatN = vnCurrLatN + verifLons;

         /* South normal index offset. */
         final int vnCurrLatS = idxVNEquator + iLons;
         final int vnNextLatS = vnCurrLatS + verifLons;

         for ( int j = 0; j < verifLons; ++j ) {
            final int jNextVt = j + 1;
            final int jNextV = jNextVt % verifLons;

            /* North coordinate indices. */
            final int n00 = vCurrLatN + j;
            final int n01 = vNextLatN + j;
            final int n11 = vNextLatN + jNextV;
            final int n10 = vCurrLatN + jNextV;

            /* South coordinate indices. */
            final int s00 = vCurrLatS + j;
            final int s01 = vNextLatS + j;
            final int s11 = vNextLatS + jNextV;
            final int s10 = vCurrLatS + jNextV;

            /* North texture coordinate indices. */
            final int vtn00 = vtCurrLatN + j;
            final int vtn01 = vtNextLatN + j;
            final int vtn11 = vtNextLatN + jNextVt;
            final int vtn10 = vtCurrLatN + jNextVt;

            /* South texture coordinate indices. */
            final int vts00 = vtCurrLatS + j;
            final int vts01 = vtNextLatS + j;
            final int vts11 = vtNextLatS + jNextVt;
            final int vts10 = vtCurrLatS + jNextVt;

            /* North normal indices. */
            final int vnn00 = vnCurrLatN + j;
            final int vnn01 = vnNextLatN + j;
            final int vnn11 = vnNextLatN + jNextV;
            final int vnn10 = vnCurrLatN + jNextV;

            /* South normal indices. */
            final int vns00 = vnCurrLatS + j;
            final int vns01 = vnNextLatS + j;
            final int vns11 = vnNextLatS + jNextV;
            final int vns10 = vnCurrLatS + jNextV;

            if ( useQuads ) {
               final int[][] northQuad = fs[fHemiOffsetNorth] = new int[4][3];

               final int[] nq0 = northQuad[0];
               nq0[0] = n00;
               nq0[1] = vtn00;
               nq0[2] = vnn00;

               final int[] nq1 = northQuad[1];
               nq1[0] = n01;
               nq1[1] = vtn01;
               nq1[2] = vnn01;

               final int[] nq2 = northQuad[2];
               nq2[0] = n11;
               nq2[1] = vtn11;
               nq2[2] = vnn11;

               final int[] nq3 = northQuad[3];
               nq3[0] = n10;
               nq3[1] = vtn10;
               nq3[2] = vnn10;

               final int[][] southQuad = fs[fHemiOffsetSouth] = new int[4][3];

               final int[] sq0 = southQuad[0];
               sq0[0] = s00;
               sq0[1] = vts00;
               sq0[2] = vns00;

               final int[] sq1 = southQuad[1];
               sq1[0] = s01;
               sq1[1] = vts01;
               sq1[2] = vns01;

               final int[] sq2 = southQuad[2];
               sq2[0] = s11;
               sq2[1] = vts11;
               sq2[2] = vns11;

               final int[] sq3 = southQuad[3];
               sq3[0] = s10;
               sq3[1] = vts10;
               sq3[2] = vns10;

               fHemiOffsetNorth += 1;
               fHemiOffsetSouth += 1;

            } else {

               /* North triangle 1. */
               final int[][] northTri0 = fs[fHemiOffsetNorth + 1]
                  = new int[3][3];

               final int[] nTri00 = northTri0[0];
               nTri00[0] = n00;
               nTri00[1] = vtn00;
               nTri00[2] = vnn00;

               final int[] nTri01 = northTri0[1];
               nTri01[0] = n01;
               nTri01[1] = vtn01;
               nTri01[2] = vnn01;

               final int[] nTri02 = northTri0[2];
               nTri02[0] = n11;
               nTri02[1] = vtn11;
               nTri02[2] = vnn11;

               /* North triangle 0. */
               final int[][] nTri1 = fs[fHemiOffsetNorth] = new int[3][3];

               final int[] nTri10 = nTri1[0];
               nTri10[0] = n00;
               nTri10[1] = vtn00;
               nTri10[2] = vnn00;

               final int[] nTri11 = nTri1[1];
               nTri11[0] = n11;
               nTri11[1] = vtn11;
               nTri11[2] = vnn11;

               final int[] nTri12 = nTri1[2];
               nTri12[0] = n10;
               nTri12[1] = vtn10;
               nTri12[2] = vnn10;

               /* South triangle 1. */
               final int[][] southTri0 = fs[fHemiOffsetSouth + 1]
                  = new int[3][3];

               final int[] sTri00 = southTri0[0];
               sTri00[0] = s00;
               sTri00[1] = vts00;
               sTri00[2] = vns00;

               final int[] sTri01 = southTri0[1];
               sTri01[0] = s01;
               sTri01[1] = vts01;
               sTri01[2] = vns01;

               final int[] sTri02 = southTri0[2];
               sTri02[0] = s11;
               sTri02[1] = vts11;
               sTri02[2] = vns11;

               /* South triangle 0. */
               final int[][] sTri1 = fs[fHemiOffsetSouth] = new int[3][3];

               final int[] sTri10 = sTri1[0];
               sTri10[0] = s00;
               sTri10[1] = vts00;
               sTri10[2] = vns00;

               final int[] sTri11 = sTri1[1];
               sTri11[0] = s11;
               sTri11[1] = vts11;
               sTri11[2] = vns11;

               final int[] sTri12 = sTri1[2];
               sTri12[0] = s10;
               sTri12[1] = vts10;
               sTri12[2] = vns10;

               fHemiOffsetNorth += 2;
               fHemiOffsetSouth += 2;
            }
         }
      }

      /* Cylinder face indices. */
      int fCylOffset = idxFsCyl;
      for ( int m = 0; m < verifRingsP1; ++m ) {

         final int vCurrRing = idxVNEquator + m * verifLons;
         final int vNextRing = vCurrRing + verifLons;

         final int vtCurrRing = idxVtNEquator + m * verifLonsP1;
         final int vtNextRing = vtCurrRing + verifLonsP1;

         for ( int j = 0; j < verifLons; ++j ) {

            final int jNextVt = j + 1;
            final int jNextV = jNextVt % verifLons;

            /* Coordinate corners. */
            final int v00 = vCurrRing + j;
            final int v01 = vNextRing + j;
            final int v11 = vNextRing + jNextV;
            final int v10 = vCurrRing + jNextV;

            /* Texture coordinate corners. */
            final int vt00 = vtCurrRing + j;
            final int vt01 = vtNextRing + j;
            final int vt11 = vtNextRing + jNextVt;
            final int vt10 = vtCurrRing + jNextVt;

            /* Normal corners. */
            final int vn0 = idxVNEquator + j;
            final int vn1 = idxVNEquator + jNextV;

            if ( useQuads ) {

               final int[][] quad = fs[fCylOffset] = new int[4][3];

               final int[] quad0 = quad[0];
               quad0[0] = v00;
               quad0[1] = vt00;
               quad0[2] = vn0;

               final int[] quad1 = quad[1];
               quad1[0] = v01;
               quad1[1] = vt01;
               quad1[2] = vn0;

               final int[] quad2 = quad[2];
               quad2[0] = v11;
               quad2[1] = vt11;
               quad2[2] = vn1;

               final int[] quad3 = quad[3];
               quad3[0] = v10;
               quad3[1] = vt10;
               quad3[2] = vn1;

               fCylOffset += 1;

            } else {

               final int[][] tri0 = fs[fCylOffset + 1] = new int[3][3];

               final int[] tri00 = tri0[0];
               tri00[0] = v00;
               tri00[1] = vt00;
               tri00[2] = vn0;

               final int[] tri01 = tri0[1];
               tri01[0] = v01;
               tri01[1] = vt01;
               tri01[2] = vn0;

               final int[] tri02 = tri0[2];
               tri02[0] = v11;
               tri02[1] = vt11;
               tri02[2] = vn1;

               final int[][] tri1 = fs[fCylOffset] = new int[3][3];

               final int[] tri10 = tri1[0];
               tri10[0] = v00;
               tri10[1] = vt00;
               tri10[2] = vn0;

               final int[] tri11 = tri1[1];
               tri11[0] = v11;
               tri11[1] = vt11;
               tri11[2] = vn1;

               final int[] tri12 = tri1[2];
               tri12[0] = v10;
               tri12[1] = vt10;
               tri12[2] = vn1;

               fCylOffset += 2;
            }
         }
      }

      return target;
   }

   /**
    * The main function.
    *
    * @param args the string of arguments
    */
   public static void main ( final String[] args ) {

      final Mesh3 m3 = new Mesh3();
      Mesh3.cube(m3);
      System.out.println(m3);
      m3.removeFaces(-3, 3);
      System.out.println(m3);
      // final int longitudes = 16;
      // final int latitudes = 8;
      // final int rings = 2;
      // final float depth = 2.0f;
      // final float radius = 1.5f;
      // final PolyType poly = PolyType.TRI;
      // CamZup.capsule(longitudes, latitudes, rings, depth, radius, poly, m3);
      // // System.out.println(m3);
      // final MeshEntity3 entity3 = new MeshEntity3().append(m3);
      // final String pyCd = entity3.toBlenderCode();
      // System.out.println(pyCd);

      // int[][][] a = { { { 0, 1, 2 }, { 3, 4, 5 } }, { { 6, 7, 8 }, { 9, 10,
      // 11 } }, { { -5, -4, -3 }, { -2, -1, 0 } } };
      // int[][][] b = Mesh.remove(a, 0, 2);
      // System.out.println("Length: " + b.length);
      // for ( int i = 0; i < b.length; ++i ) {
      // for ( int j = 0; j < b[i].length; ++j ) {
      // for ( int k = 0; k < b[i][j].length; ++k ) {
      // System.out.print(b[i][j][k] + ", ");
      // }
      // System.out.println("");
      // }
      // }
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
