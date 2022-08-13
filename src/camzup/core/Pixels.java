package camzup.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Holds methods that operate on arrays of pixels held by images.
 */
public abstract class Pixels {

   /**
    * Discourage overriding with a private constructor.
    */
   private Pixels ( ) {}

   /**
    * Tolerance to determine whether two angles are approximate. Equivalent to
    * half a degree, <code>1.0 / 720.0</code>, {@value Pixels#EPS_RADIANS}.
    */
   public static final float EPS_RADIANS = 0.008726646f;

   /**
    * Look up table for converting colors from linear to standard RGB.
    */
   private static final int[] LTS_LUT = new int[] { 0, 13, 22, 28, 34, 38, 42,
      46, 50, 53, 56, 59, 61, 64, 66, 69, 71, 73, 75, 77, 79, 81, 83, 85, 86,
      88, 90, 92, 93, 95, 96, 98, 99, 101, 102, 104, 105, 106, 108, 109, 110,
      112, 113, 114, 115, 117, 118, 119, 120, 121, 122, 124, 125, 126, 127, 128,
      129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143,
      144, 145, 146, 147, 148, 148, 149, 150, 151, 152, 153, 154, 155, 155, 156,
      157, 158, 159, 159, 160, 161, 162, 163, 163, 164, 165, 166, 167, 167, 168,
      169, 170, 170, 171, 172, 173, 173, 174, 175, 175, 176, 177, 178, 178, 179,
      180, 180, 181, 182, 182, 183, 184, 185, 185, 186, 187, 187, 188, 189, 189,
      190, 190, 191, 192, 192, 193, 194, 194, 195, 196, 196, 197, 197, 198, 199,
      199, 200, 200, 201, 202, 202, 203, 203, 204, 205, 205, 206, 206, 207, 208,
      208, 209, 209, 210, 210, 211, 212, 212, 213, 213, 214, 214, 215, 215, 216,
      216, 217, 218, 218, 219, 219, 220, 220, 221, 221, 222, 222, 223, 223, 224,
      224, 225, 226, 226, 227, 227, 228, 228, 229, 229, 230, 230, 231, 231, 232,
      232, 233, 233, 234, 234, 235, 235, 236, 236, 237, 237, 238, 238, 238, 239,
      239, 240, 240, 241, 241, 242, 242, 243, 243, 244, 244, 245, 245, 246, 246,
      246, 247, 247, 248, 248, 249, 249, 250, 250, 251, 251, 251, 252, 252, 253,
      253, 254, 254, 255, 255 };

   /**
    * Look up table for converting colors from standard to linear RGB.
    */
   private static final int[] STL_LUT = new int[] { 0, 0, 0, 0, 0, 0, 0, 1, 1,
      1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4,
      4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 8, 8, 8, 8, 9, 9, 9, 10, 10, 10,
      11, 11, 12, 12, 12, 13, 13, 13, 14, 14, 15, 15, 16, 16, 17, 17, 17, 18,
      18, 19, 19, 20, 20, 21, 22, 22, 23, 23, 24, 24, 25, 25, 26, 27, 27, 28,
      29, 29, 30, 30, 31, 32, 32, 33, 34, 35, 35, 36, 37, 37, 38, 39, 40, 41,
      41, 42, 43, 44, 45, 45, 46, 47, 48, 49, 50, 51, 51, 52, 53, 54, 55, 56,
      57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74,
      76, 77, 78, 79, 80, 81, 82, 84, 85, 86, 87, 88, 90, 91, 92, 93, 95, 96,
      97, 99, 100, 101, 103, 104, 105, 107, 108, 109, 111, 112, 114, 115, 116,
      118, 119, 121, 122, 124, 125, 127, 128, 130, 131, 133, 134, 136, 138, 139,
      141, 142, 144, 146, 147, 149, 151, 152, 154, 156, 157, 159, 161, 163, 164,
      166, 168, 170, 171, 173, 175, 177, 179, 181, 183, 184, 186, 188, 190, 192,
      194, 196, 198, 200, 202, 204, 206, 208, 210, 212, 214, 216, 218, 220, 222,
      224, 226, 229, 231, 233, 235, 237, 239, 242, 244, 246, 248, 250, 253,
      255 };

   /**
    * Adjusts the contrast of colors from a source pixels array by a factor.
    * Uses the CIE LAB color space. The adjustment factor is expected to be in
    * [-1.0, 1.0].
    *
    * @param source the source pixels
    * @param fac    the contrast factor
    * @param target the target pixels
    *
    * @return the adjusted pixels
    *
    * @see Utils#clamp(float, float, float)
    * @see Color#fromHex(int, Color)
    * @see Color#sRgbaTolRgba(Color, boolean, Color)
    * @see Color#lRgbaToXyza(Color, Vec4)
    * @see Color#xyzaToLaba(Vec4, Vec4)
    * @see Color#labaToXyza(Vec4, Vec4)
    * @see Color#xyzaTolRgba(Vec4, Color)
    * @see Color#lRgbaTosRgba(Color, boolean, Color)
    * @see Color#toHexIntSat(Color)
    */
   public static int[] adjustContrast ( final int[] source, final float fac,
      final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         final float valAdjust = 1.0f + Utils.clamp(fac, -1.0f, 1.0f);
         if ( Utils.approx(valAdjust, 0.0f) ) {
            System.arraycopy(source, 0, target, 0, srcLen);
            return target;
         }

         final Color srgb = new Color();
         final Color lrgb = new Color();
         final Vec4 xyz = new Vec4();
         final Vec4 lab = new Vec4();
         final HashMap < Integer, Integer > dict = new HashMap <>(512, 0.75f);

         for ( int i = 0; i < srcLen; ++i ) {
            final int srgbKeyInt = source[i];
            if ( ( srgbKeyInt & 0xff000000 ) != 0 ) {
               final Integer srgbKeyObj = srgbKeyInt;
               if ( !dict.containsKey(srgbKeyObj) ) {

                  Color.fromHex(srgbKeyInt, srgb);
                  Color.sRgbaTolRgba(srgb, false, lrgb);
                  Color.lRgbaToXyza(lrgb, xyz);
                  Color.xyzaToLaba(xyz, lab);

                  lab.z = ( lab.z - 50.0f ) * valAdjust + 50.0f;

                  Color.labaToXyza(lab, xyz);
                  Color.xyzaTolRgba(xyz, lrgb);
                  Color.lRgbaTosRgba(lrgb, false, srgb);
                  dict.put(srgbKeyObj, Color.toHexIntSat(srgb));
               }
            }
         }

         if ( dict.size() > 0 ) {
            for ( int i = 0; i < srcLen; ++i ) {
               final Integer srgbKeyObj = source[i];
               if ( dict.containsKey(srgbKeyObj) ) {
                  target[i] = dict.get(srgbKeyObj);
               } else {
                  target[i] = 0x00000000;
               }
            }
         } else {
            for ( int i = 0; i < srcLen; ++i ) { target[i] = 0x00000000; }
         }
      }

      return target;
   }

   /**
    * Adjusts a source pixels array's colors in CIE LCH. Assigns the results
    * to a target array.
    *
    * @param source the source pixels
    * @param adjust the adjustment
    * @param target the target pixels
    *
    * @return the adjusted pixels
    *
    * @see Vec4#none(Vec4)
    * @see Vec4#add(Vec4, Vec4, Vec4)
    * @see Color#fromHex(int, Color)
    * @see Color#sRgbaTolRgba(Color, boolean, Color)
    * @see Color#lRgbaToXyza(Color, Vec4)
    * @see Color#xyzaToLaba(Vec4, Vec4)
    * @see Color#labaToLcha(Vec4, Vec4)
    * @see Color#lchaToLaba(Vec4, Vec4)
    * @see Color#labaToXyza(Vec4, Vec4)
    * @see Color#xyzaTolRgba(Vec4, Color)
    * @see Color#lRgbaTosRgba(Color, boolean, Color)
    * @see Color#toHexIntSat(Color)
    */
   public static int[] adjustLch ( final int[] source, final Vec4 adjust,
      final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         if ( Vec4.none(adjust) ) {
            System.arraycopy(source, 0, target, 0, srcLen);
            return target;
         }

         final Color srgb = new Color();
         final Color lrgb = new Color();
         final Vec4 xyz = new Vec4();
         final Vec4 lab = new Vec4();
         final Vec4 lch = new Vec4();
         final HashMap < Integer, Integer > dict = new HashMap <>(512, 0.75f);

         for ( int i = 0; i < srcLen; ++i ) {
            final int srgbKeyInt = source[i];
            if ( ( srgbKeyInt & 0xff000000 ) != 0 ) {
               final Integer srgbKeyObj = srgbKeyInt;
               if ( !dict.containsKey(srgbKeyObj) ) {

                  Color.fromHex(srgbKeyInt, srgb);
                  Color.sRgbaTolRgba(srgb, false, lrgb);
                  Color.lRgbaToXyza(lrgb, xyz);
                  Color.xyzaToLaba(xyz, lab);
                  Color.labaToLcha(lab, lch);

                  Vec4.add(lch, adjust, lch);

                  Color.lchaToLaba(lch, lab);
                  Color.labaToXyza(lab, xyz);
                  Color.xyzaTolRgba(xyz, lrgb);
                  Color.lRgbaTosRgba(lrgb, false, srgb);
                  dict.put(srgbKeyObj, Color.toHexIntSat(srgb));
               }
            }
         }

         if ( dict.size() > 0 ) {
            for ( int i = 0; i < srcLen; ++i ) {
               final Integer srgbKeyObj = source[i];
               if ( dict.containsKey(srgbKeyObj) ) {
                  target[i] = dict.get(srgbKeyObj);
               } else {
                  target[i] = 0x00000000;
               }
            }
         } else {
            for ( int i = 0; i < srcLen; ++i ) { target[i] = 0x00000000; }
         }
      }

      return target;
   }

   /**
    * Extracts a palette from a source pixels array using an octree in CIE
    * LAB. The size of the palette depends on the capacity of each node in the
    * octree. Does not retain alpha component of image pixels. The threshold
    * describes the minimum number of unique colors in the image beneath which
    * it is preferable to not engage the octree. Once the octree has been
    * used, colors produced may not be in gamut.
    *
    * @param source    the source image
    * @param capacity  the octree capacity
    * @param threshold the minimum threshold
    *
    * @return the color array
    *
    * @see Bounds3#cieLab(Bounds3)
    * @see Color#fromHex(int, Color)
    * @see Color#sRgbaTolRgba(Color, boolean, Color)
    * @see Color#lRgbaToXyza(Color, Vec4)
    * @see Color#xyzaToLaba(Vec4, Vec4)
    * @see Color#labaToXyza(Vec4, Vec4)
    * @see Color#xyzaTolRgba(Vec4, Color)
    * @see Color#lRgbaTosRgba(Color, boolean, Color)
    * @see Color#clearBlack(Color)
    */
   public static Color[] extractPalette ( final int[] source,
      final int capacity, final int threshold ) {

      final HashSet < Integer > uniqueColors = new HashSet <>(256, 0.75f);
      final int srcLen = source.length;
      for ( int i = 0; i < srcLen; ++i ) {
         uniqueColors.add(0xff000000 | source[i]);
      }

      final int uniquesLen = uniqueColors.size();
      final Iterator < Integer > uniquesItr = uniqueColors.iterator();

      /* If under threshold, do not engage octree. */
      final int valThresh = threshold < 3 ? 3 : threshold;
      if ( uniquesLen < valThresh ) {
         /* Account for alpha at index 0, so less than threshold. */
         final Color[] result = new Color[1 + uniquesLen];
         for ( int i = 0; uniquesItr.hasNext(); ++i ) {
            result[1 + i] = Color.fromHex(uniquesItr.next(), new Color());
         }
         result[0] = Color.clearBlack(new Color());
         return result;
      }

      final Bounds3 bounds = Bounds3.cieLab(new Bounds3());
      final Octree oct = new Octree(bounds, capacity);
      final Color srgb = new Color();
      final Color lrgb = new Color();
      final Vec4 xyz = new Vec4();
      final Vec4 lab = new Vec4();

      while ( uniquesItr.hasNext() ) {
         Color.fromHex(uniquesItr.next(), srgb);
         Color.sRgbaTolRgba(srgb, false, lrgb);
         Color.lRgbaToXyza(lrgb, xyz);
         Color.xyzaToLaba(xyz, lab);
         oct.insert(new Vec3(lab.x, lab.y, lab.z));
      }

      /* Unlike dither, shouldn't have to keep 8 centers minimum. */
      oct.cull();

      final Vec3[] centers = oct.centersMean(false);
      final int centersLen = centers.length;
      final Color[] result = new Color[1 + centersLen];
      for ( int i = 0; i < centersLen; ++i ) {
         final Vec3 center = centers[i];
         Color.labaToXyza(center.z, center.x, center.y, 1.0f, xyz);
         Color.xyzaTolRgba(xyz, lrgb);
         final Color c = Color.lRgbaTosRgba(lrgb, false, new Color());
         result[1 + i] = c;
      }
      result[0] = Color.clearBlack(new Color());
      return result;
   }

   /**
    * Fills the pixels target array with a color. The color is expected to be
    * a 32-bit color integer.
    *
    * @param c      the fill color
    * @param target the target pixels
    *
    * @return the filled array
    */
   public static int[] fill ( final int c, final int[] target ) {

      final int len = target.length;
      for ( int i = 0; i < len; ++i ) { target[i] = c; }

      return target;
   }

   /**
    * Internal helper function to sample a source image with a bilinear color
    * mix. Returns a hexadecimal integer color
    *
    * @param xSrc  the source x coordinate
    * @param ySrc  the source y coordinate
    * @param wSrc  the source image width
    * @param hSrc  the source image height
    * @param pxSrc the source pixel array
    *
    * @return the color
    */
   public static final int filterBilinear ( final float xSrc, final float ySrc,
      final int wSrc, final int hSrc, final int[] pxSrc ) {

      final boolean yPos = ySrc > 0.0f;
      final boolean yNeg = ySrc < 0.0f;
      final int yi = ( int ) ySrc;
      final int yf = yPos ? yi : yNeg ? yi - 1 : 0;
      final int yc = yPos ? yi + 1 : yNeg ? yi : 0;

      final boolean yfInBounds = yf > -1 && yf < hSrc;
      final boolean ycInBounds = yc > -1 && yc < hSrc;

      final boolean xPos = xSrc > 0.0f;
      final boolean xNeg = xSrc < 0.0f;
      final int xi = ( int ) xSrc;
      final int xf = xPos ? xi : xNeg ? xi - 1 : 0;
      final int xc = xPos ? xi + 1 : xNeg ? xi : 0;

      final boolean xfInBounds = xf > -1 && xf < wSrc;
      final boolean xcInBounds = xc > -1 && xc < wSrc;

      /* Pixel corners colors. */
      final int c00 = xfInBounds && yfInBounds ? pxSrc[xf + yf * wSrc] : 0;
      final int c10 = xcInBounds && yfInBounds ? pxSrc[xc + yf * wSrc] : 0;
      final int c11 = xcInBounds && ycInBounds ? pxSrc[xc + yc * wSrc] : 0;
      final int c01 = xfInBounds && ycInBounds ? pxSrc[xf + yc * wSrc] : 0;

      final float xErr = xSrc - xf;

      float a0 = 0.0f;
      float r0 = 0.0f;
      float g0 = 0.0f;
      float b0 = 0.0f;

      final int a00 = c00 >> 0x18 & 0xff;
      final int a10 = c10 >> 0x18 & 0xff;
      if ( a00 > 0 || a10 > 0 ) {
         final float u = 1.0f - xErr;
         a0 = u * a00 + xErr * a10;
         if ( a0 > 0.0f ) {
            final int r00 = c00 >> 0x10 & 0xff;
            final int g00 = c00 >> 0x08 & 0xff;
            final int b00 = c00 & 0xff;

            final int r10 = c10 >> 0x10 & 0xff;
            final int g10 = c10 >> 0x08 & 0xff;
            final int b10 = c10 & 0xff;

            r0 = u * r00 + xErr * r10;
            g0 = u * g00 + xErr * g10;
            b0 = u * b00 + xErr * b10;
         }
      }

      float a1 = 0.0f;
      float r1 = 0.0f;
      float g1 = 0.0f;
      float b1 = 0.0f;

      final int a01 = c01 >> 0x18 & 0xff;
      final int a11 = c11 >> 0x18 & 0xff;
      if ( a01 > 0 || a11 > 0 ) {
         final float u = 1.0f - xErr;
         a1 = u * a01 + xErr * a11;
         if ( a1 > 0.0f ) {
            final int r01 = c01 >> 0x10 & 0xff;
            final int g01 = c01 >> 0x08 & 0xff;
            final int b01 = c01 & 0xff;

            final int r11 = c11 >> 0x10 & 0xff;
            final int g11 = c11 >> 0x08 & 0xff;
            final int b11 = c11 & 0xff;

            r1 = u * r01 + xErr * r11;
            g1 = u * g01 + xErr * g11;
            b1 = u * b01 + xErr * b11;
         }
      }

      float a2 = 0.0f;
      float r2 = 0.0f;
      float g2 = 0.0f;
      float b2 = 0.0f;

      if ( a0 > 0.0f || a1 > 0.0f ) {
         final float yErr = ySrc - yf;
         final float u = 1.0f - yErr;
         a2 = u * a0 + yErr * a1;
         if ( a2 > 0.0f ) {
            r2 = u * r0 + yErr * r1;
            g2 = u * g0 + yErr * g1;
            b2 = u * b0 + yErr * b1;
         }

         final int ai = a2 > 255.0f ? 255 : ( int ) a2;
         final int ri = r2 > 255.0f ? 255 : ( int ) r2;
         final int gi = g2 > 255.0f ? 255 : ( int ) g2;
         final int bi = b2 > 255.0f ? 255 : ( int ) b2;

         return ai << 0x18 | ri << 0x10 | gi << 0x08 | bi;
      }
      return 0x00000000;
   }

   /**
    * Flips the pixels source array horizontally, on the x axis, and stores
    * the result in the target array.
    *
    * @param source the source pixels
    * @param w      the image width
    * @param h      the image height
    * @param target the target pixels
    *
    * @return the flipped pixels
    */
   public static int[] flipX ( final int[] source, final int w, final int h,
      final int[] target ) {

      final int srcLen = source.length;
      if ( source == target ) {
         final int wd2 = w / 2;
         final int wn1 = w - 1;
         final int lenHalf = wd2 * h;
         for ( int i = 0; i < lenHalf; ++i ) {
            final int x = i % wd2;
            final int yw = w * ( i / wd2 );
            final int idxSrc = x + yw;
            final int idxTrg = yw + wn1 - x;
            final int t = source[idxSrc];
            source[idxSrc] = source[idxTrg];
            source[idxTrg] = t;
         }
      } else if ( srcLen == target.length ) {
         final int wn1 = w - 1;
         for ( int i = 0; i < srcLen; ++i ) {
            target[i / w * w + wn1 - i % w] = source[i];
         }
      }

      return target;
   }

   /**
    * Flips the pixels source array vertically, on the y axis, and stores the
    * result in the target array.
    *
    * @param source the source pixels
    * @param w      the image width
    * @param h      the image height
    * @param target the target pixels
    *
    * @return the flipped pixels
    */
   public static int[] flipY ( final int[] source, final int w, final int h,
      final int[] target ) {

      final int srcLen = source.length;
      if ( source == target ) {
         final int hd2 = h / 2;
         final int hn1 = h - 1;
         final int lenHalf = w * hd2;
         for ( int i = 0; i < lenHalf; ++i ) {
            final int j = i % w + w * ( hn1 - i / w );
            final int t = source[i];
            source[i] = source[j];
            source[j] = t;
         }
      } else if ( srcLen == target.length ) {
         final int hn1 = h - 1;
         for ( int i = 0; i < srcLen; ++i ) {
            target[ ( hn1 - i / w ) * w + i % w] = source[i];
         }
      }

      return target;
   }

   /**
    * Converts a pixels source array to gray, then stores the result in the
    * target array. The result still uses 32-bit integers in ARGB format; the
    * gray value is repeated three times.
    *
    * @param source the source pixels
    * @param target the target pixels
    *
    * @return the gray pixels
    *
    * @see Pixels#sRgbLuminance(int)
    */
   public static int[] grayscale ( final int[] source, final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         for ( int i = 0; i < srcLen; ++i ) {
            final int srcHex = source[i];
            final int ai = srcHex & 0xff000000;
            if ( ai != 0 ) {
               final int v = Pixels.LTS_LUT[( int ) ( Pixels.sRgbLuminance(
                  srcHex) * 0xff + 0.5f )];
               target[i] = ai | v << 0x10 | v << 0x08 | v;
            } else {
               target[i] = 0x00000000;
            }
         }
      }

      return target;
   }

   /**
    * Inverts colors from a source pixels array in CIE LAB.
    *
    * @param source the source pixels
    * @param l      invert lightness
    * @param a      invert a
    * @param b      invert b
    * @param alpha  invert transparency
    * @param target the target pixels
    *
    * @return the inverted pixels
    *
    * @see Color#fromHex(int, Color)
    * @see Color#sRgbaTolRgba(Color, boolean, Color)
    * @see Color#lRgbaToXyza(Color, Vec4)
    * @see Color#xyzaToLaba(Vec4, Vec4)
    * @see Color#labaToXyza(Vec4, Vec4)
    * @see Color#xyzaTolRgba(Vec4, Color)
    * @see Color#lRgbaTosRgba(Color, boolean, Color)
    * @see Color#toHexIntSat(Color)
    */
   public static int[] invertLab ( final int[] source, final boolean l,
      final boolean a, final boolean b, final boolean alpha,
      final int[] target ) {

      if ( !l && !a && !b && !alpha ) { return target; }

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         final Color srgb = new Color();
         final Color lrgb = new Color();
         final Vec4 xyz = new Vec4();
         final Vec4 lab = new Vec4();
         final HashMap < Integer, Integer > dict = new HashMap <>(512, 0.75f);

         final float aSign = a ? -1.0f : 1.0f;
         final float bSign = b ? -1.0f : 1.0f;

         for ( int i = 0; i < srcLen; ++i ) {
            final int srgbKeyInt = source[i];
            if ( ( srgbKeyInt & 0xff000000 ) != 0 ) {
               final Integer srgbKeyObj = srgbKeyInt;
               if ( !dict.containsKey(srgbKeyObj) ) {

                  Color.fromHex(srgbKeyInt, srgb);
                  Color.sRgbaTolRgba(srgb, false, lrgb);
                  Color.lRgbaToXyza(lrgb, xyz);
                  Color.xyzaToLaba(xyz, lab);

                  lab.x *= aSign;
                  lab.y *= bSign;
                  if ( l ) { lab.z = 100.0f - lab.z; }
                  if ( alpha ) { lab.w = 1.0f - lab.w; }

                  Color.labaToXyza(lab, xyz);
                  Color.xyzaTolRgba(xyz, lrgb);
                  Color.lRgbaTosRgba(lrgb, false, srgb);
                  dict.put(srgbKeyObj, Color.toHexIntSat(srgb));
               }
            }
         }

         if ( dict.size() > 0 ) {
            for ( int i = 0; i < srcLen; ++i ) {
               final int srgbKeyInt = source[i];
               final Integer srgbKeyObj = srgbKeyInt;
               if ( dict.containsKey(srgbKeyObj) ) {
                  target[i] = dict.get(srgbKeyObj);
               } else if ( a ) {
                  target[i] = 0xff000000 | srgbKeyInt;
               } else {
                  target[i] = 0x00000000;
               }
            }
         } else if ( a ) {
            for ( int i = 0; i < srcLen; ++i ) {
               target[i] = 0xff000000 | source[i];
            }
         } else {
            for ( int i = 0; i < srcLen; ++i ) { target[i] = 0x00000000; }
         }
      }

      return target;
   }

   /**
    * Converts a pixel color from linear RGB to standard RGB. If the adjust
    * alpha flag is true, then alpha is converted as well.
    *
    * @param c           the color
    * @param adjustAlpha adjust alpha flag
    *
    * @return the standard pixels
    */
   public static int lRgbaTosRgba ( final int c, final boolean adjustAlpha ) {

      final int lai = c >> 0x18 & 0xff;
      return ( adjustAlpha ? Pixels.LTS_LUT[lai] : lai ) << 0x18
         | Pixels.LTS_LUT[c >> 0x10 & 0xff] << 0x10 | Pixels.LTS_LUT[c >> 0x08
            & 0xff] << 0x08 | Pixels.LTS_LUT[c & 0xff];
   }

   /**
    * Converts a source pixel array from linear RGB to standard RGB. If the
    * adjust alpha flag is true, then alpha is converted as well.
    *
    * @param source      the source pixels
    * @param adjustAlpha adjust alpha flag
    * @param target      the target pixels
    *
    * @return the standard pixels
    *
    * @see Pixels#lRgbaTosRgba(int, boolean)
    */
   public static int[] lRgbaTosRgba ( final int[] source,
      final boolean adjustAlpha, final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         for ( int i = 0; i < srcLen; ++i ) {
            target[i] = Pixels.lRgbaTosRgba(source[i], adjustAlpha);
         }
      }

      return target;
   }

   /**
    * Mirrors pixels from a source image across the axis described by an
    * origin and destination. Coordinates are expected to be in the range
    * [0.0, 1.0]. Out-of-bounds pixels are omitted from the mirror.
    *
    * @param source  the source pixels
    * @param wSrc    the source image width
    * @param hSrc    the source image height
    * @param xOrigin the origin x
    * @param yOrigin the origin y
    * @param xDest   the destination x
    * @param yDest   the destination y
    * @param flip    the flip reflection flag
    * @param target  the target pixels
    *
    * @return the mirrored image
    *
    * @see Utils#max(float, float)
    * @see Pixels#filterBilinear(float, float, int, int, int[])
    */
   public static int[] mirrorBilinear ( final int[] source, final int wSrc,
      final int hSrc, final float xOrigin, final float yOrigin,
      final float xDest, final float yDest, final boolean flip,
      final int[] target ) {

      // TODO: Create specialized cases for horizontal and vertical lines?

      final float wfn1 = wSrc - 1.0f;
      final float hfn1 = hSrc - 1.0f;

      final float ax = xOrigin * wfn1;
      final float ay = hfn1 - yOrigin * hfn1;
      final float bx = xDest * wfn1;
      final float by = hfn1 - yDest * hfn1;

      final float dx = bx - ax;
      final float dy = by - ay;
      final float dMagSq = dx * dx + dy * dy;
      final float dMagSqInv = 1.0f / Utils.max(IUtils.EPSILON, dMagSq);

      final float flipSign = flip ? -1.0f : 1.0f;

      final int trgLen = target.length;
      for ( int k = 0; k < trgLen; ++k ) {
         final float cy = k / wSrc;
         final float ey = cy - ay;
         final float cx = k % wSrc;
         final float ex = cx - ax;

         final float cross = ex * dy - ey * dx;
         if ( flipSign * cross < 0.0f ) {
            target[k] = source[k];
         } else {
            final float t = ( ex * dx + ey * dy ) * dMagSqInv;
            final float u = 1.0f - t;
            final float pxProj = u * ax + t * bx;
            final float pyProj = u * ay + t * by;
            final float pxOpp = pxProj + pxProj - cx;
            final float pyOpp = pyProj + pyProj - cy;

            /*
             * Default to omitting pixels that are out-of-bounds, rather than
             * wrapping with floor modulo or clamping.
             */
            if ( pyOpp >= 0.0f && pyOpp <= hfn1 && pxOpp >= 0.0f && pxOpp
               <= wfn1 ) {
               target[k] = Pixels.filterBilinear(pxOpp, pyOpp, wSrc, hSrc,
                  source);
            }
         }
      }

      return target;
   }

   /**
    * Multiplies the red, green and blue channels of each pixel by its alpha
    * channel.
    *
    * @param source the source pixels
    * @param target the target pixels
    *
    * @return the premultiplied image
    */
   public static int[] premul ( final int[] source, final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         for ( int i = 0; i < srcLen; ++i ) {
            final int srcHex = source[i];
            final int ai = srcHex >> 0x18 & 0xff;
            if ( ai < 1 ) {
               target[i] = 0x00000000;
            } else if ( ai < 255 ) {
               final float af = ai * IUtils.ONE_255;
               int rp = ( int ) ( ( srcHex >> 0x10 & 0xff ) * af + 0.5f );
               int gp = ( int ) ( ( srcHex >> 0x08 & 0xff ) * af + 0.5f );
               int bp = ( int ) ( ( srcHex & 0xff ) * af + 0.5f );

               if ( rp > 255 ) { rp = 255; }
               if ( gp > 255 ) { gp = 255; }
               if ( bp > 255 ) { bp = 255; }

               target[i] = ai << 0x18 | rp << 0x10 | gp << 0x08 | bp;
            } else {
               target[i] = srcHex;
            }
         }
      }

      return target;
   }

   /**
    * Fills the target array with a diagnostic image where the pixel's x
    * coordinate correlates to the red channel; its y coordinate correlates to
    * the green channel. The blue contribution is expected to be in [0, 255].
    *
    * @param w      the image width
    * @param h      the image height
    * @param blue   the blue amount
    * @param target the target pixels
    *
    * @return the filled pixels
    */
   public static int[] rgb ( final int w, final int h, final int blue,
      final int[] target ) {

      final int len = target.length;
      final float hInv = 0xff / ( h - 1.0f );
      final float wInv = 0xff / ( w - 1.0f );
      final int bmsk = 0xff000000 | blue & 0xff;
      for ( int i = 0; i < len; ++i ) {
         target[i] = bmsk | ( int ) ( 0.5f + wInv * ( i % w ) ) << 0x10
            | ( int ) ( 255.5f - hInv * ( i / w ) ) << 0x08;
      }

      return target;
   }

   /**
    * Rotates the source pixel array 180 degrees counter-clockwise. The
    * rotation is stored in the target pixel array.
    *
    * @param source the source pixels
    * @param target the target pixels
    *
    * @return the rotated pixels
    */
   public static int[] rotate180 ( final int[] source, final int[] target ) {

      final int srcLen = source.length;
      if ( source == target ) {
         final int srcHalfLen = srcLen / 2;
         final int srcLenn1 = srcLen - 1;
         for ( int i = 0; i < srcHalfLen; ++i ) {
            final int t = source[i];
            source[i] = source[srcLenn1 - i];
            source[srcLenn1 - i] = t;
         }
      } else if ( srcLen == target.length ) {
         for ( int i = 0, j = srcLen - 1; i < srcLen; ++i, --j ) {
            target[j] = source[i];
         }
      }

      return target;
   }

   /**
    * Rotates the source pixel array 270 degrees counter-clockwise. The
    * rotation is stored in the target pixel array.
    *
    * @param source the source pixels
    * @param w      the image width
    * @param h      the image height
    * @param target the target pixels
    *
    * @return the rotated pixels
    */
   public static int[] rotate270 ( final int[] source, final int w, final int h,
      final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         final int hn1 = h - 1;
         for ( int i = 0; i < srcLen; ++i ) {
            target[i % w * h + hn1 - i / w] = source[i];
         }
      }

      return target;
   }

   /**
    * Rotates the source pixel array 90 degrees counter-clockwise. The
    * rotation is stored in the target pixel array.
    *
    * @param source the source pixels
    * @param w      the image width
    * @param h      the image height
    * @param target the target pixels
    *
    * @return the rotated pixels
    */
   public static int[] rotate90 ( final int[] source, final int w, final int h,
      final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         final int srcLennh = srcLen - h;
         for ( int i = 0; i < srcLen; ++i ) {
            target[srcLennh + i / w - i % w * h] = source[i];
         }
      }

      return target;
   }

   /**
    * Rotates the pixels of a source image around the image center by an angle
    * in radians. Assumes that the sine and cosine of the angle have already
    * been calculated and simple cases (0, 90, 180, 270 degrees) have been
    * filtered out.<br>
    * <br>
    * Emits the new image dimensions to a {@link Vec2}.
    *
    * @param source the source pixels
    * @param wSrc   the source image width
    * @param hSrc   the source image height
    * @param cosa   the cosine of the angle
    * @param sina   the sine of the angle
    * @param dim    the new dimension
    *
    * @return rotated pixels
    *
    * @see Pixels#filterBilinear(float, float, int, int, int[])
    * @see Utils#abs(float)
    */
   public static int[] rotateBilinear ( final int[] source, final int wSrc,
      final int hSrc, final float cosa, final float sina, final Vec2 dim ) {

      final float wSrcf = wSrc;
      final float hSrcf = hSrc;
      final float absCosa = Utils.abs(cosa);
      final float absSina = Utils.abs(sina);

      // QUERY: For this and skewX, skewY, should this be round or ceil?
      final int wTrg = ( int ) ( 0.5f + hSrcf * absSina + wSrcf * absCosa );
      final int hTrg = ( int ) ( 0.5f + hSrcf * absCosa + wSrcf * absSina );
      final float wTrgf = wTrg;
      final float hTrgf = hTrg;

      final float xSrcCenter = wSrcf * 0.5f;
      final float ySrcCenter = hSrcf * 0.5f;
      final float xTrgCenter = wTrgf * 0.5f;
      final float yTrgCenter = hTrgf * 0.5f;

      final int trgLen = wTrg * hTrg;
      final int[] target = new int[trgLen];
      if ( dim != null ) { dim.set(wTrgf, hTrgf); }

      for ( int i = 0; i < trgLen; ++i ) {
         final int yTrg = i / wTrg;
         final float ySgn = yTrg - yTrgCenter;
         final int xTrg = i % wTrg;
         final float xSgn = xTrg - xTrgCenter;
         final float ySrc = ySrcCenter + cosa * ySgn + sina * xSgn;
         final float xSrc = xSrcCenter + cosa * xSgn - sina * ySgn;
         target[i] = Pixels.filterBilinear(xSrc, ySrc, wSrc, hSrc, source);
      }

      return target;
   }

   /**
    * Rotates the pixels of a source image around the image center by an angle
    * in radians. Where the angle is approximately 0, 90, 180 and 270 degrees,
    * resorts to faster methods. Uses bilinear filtering.<br>
    * <br>
    * Emits the new image dimensions to a {@link Vec2}.
    *
    * @param source the source pixels
    * @param wSrc   the source image width
    * @param hSrc   the source image height
    * @param angle  the angle in radians
    * @param dim    the new dimension
    *
    * @return rotated pixels
    *
    * @see Pixels#rotate180(int[], int[])
    * @see Pixels#rotate90(int[], int, int, int[])
    * @see Pixels#rotate270(int[], int, int, int[])
    * @see Utils#modRadians(float)
    * @see Utils#approx(float, float)
    */
   public static int[] rotateBilinear ( final int[] source, final int wSrc,
      final int hSrc, final float angle, final Vec2 dim ) {

      /* 1 / 720 degrees = .00872 radians. */
      final int srcLen = source.length;
      final float aVerif = Utils.modRadians(angle);
      if ( Utils.approx(aVerif, 0.0f, Pixels.EPS_RADIANS) ) {
         if ( dim != null ) { dim.set(wSrc, hSrc); }
         final int[] target = new int[srcLen];
         System.arraycopy(source, 0, target, 0, srcLen);
         return target;
      }
      if ( Utils.approx(aVerif, IUtils.HALF_PI, Pixels.EPS_RADIANS) ) {
         if ( dim != null ) { dim.set(hSrc, wSrc); }
         return Pixels.rotate90(source, wSrc, hSrc, new int[srcLen]);
      }
      if ( Utils.approx(aVerif, IUtils.PI, Pixels.EPS_RADIANS) ) {
         if ( dim != null ) { dim.set(wSrc, hSrc); }
         return Pixels.rotate180(source, new int[srcLen]);
      }
      if ( Utils.approx(aVerif, 4.712389f, Pixels.EPS_RADIANS) ) {
         if ( dim != null ) { dim.set(hSrc, wSrc); }
         return Pixels.rotate270(source, wSrc, hSrc, new int[srcLen]);
      }

      final double avd = aVerif;
      final float cosa = ( float ) Math.cos(avd);
      final float sina = ( float ) Math.sin(avd);

      return Pixels.rotateBilinear(source, wSrc, hSrc, cosa, sina, dim);
   }

   /**
    * Skews the pixels of a source image horizontally. If the angle is
    * approximately zero, copies the source array. If the angle is
    * approximately {@link IUtils#HALF_PI} ({@value IUtils#HALF_PI}, returns
    * an empty array.<br>
    * <br>
    * Emits the new image dimensions to a {@link Vec2}.
    *
    * @param source the source pixels
    * @param wSrc   the source image width
    * @param hSrc   the source image height
    * @param angle  the angle in radians
    * @param dim    the new dimension
    *
    * @return the skewed array
    *
    * @see Pixels#filterBilinear(float, float, int, int, int[])
    * @see Utils#abs(float)
    * @see Utils#mod(float, float)
    * @see Utils#approx(float, float, float)
    */
   @Experimental
   public static int[] skewXBilinear ( final int[] source, final int wSrc,
      final int hSrc, final float angle, final Vec2 dim ) {

      final int srcLen = source.length;
      final float aVerif = Utils.mod(angle, IUtils.PI);
      if ( Utils.approx(aVerif, 0.0f, Pixels.EPS_RADIANS) ) {
         if ( dim != null ) { dim.set(wSrc, hSrc); }
         final int[] target = new int[srcLen];
         System.arraycopy(source, 0, target, 0, srcLen);
         return target;
      }
      if ( Utils.approx(aVerif, IUtils.HALF_PI, Pixels.EPS_RADIANS) ) {
         if ( dim != null ) { dim.set(wSrc, hSrc); }
         return new int[srcLen];
      }

      final float wSrcf = wSrc;
      final float hSrcf = hSrc;

      final float tana = ( float ) Math.tan(aVerif);
      final int wTrg = ( int ) ( 0.5f + wSrcf + Utils.abs(tana) * hSrcf );
      final float wTrgf = wTrg;
      final float yCenter = hSrcf * 0.5f;
      final float xDiff = ( wSrcf - wTrgf ) * 0.5f;

      final int trgLen = wTrg * hSrc;
      final int[] target = new int[trgLen];
      if ( dim != null ) { dim.set(wTrgf, hSrcf); }

      for ( int i = 0; i < trgLen; ++i ) {
         final int yTrg = i / wTrg;
         final int xTrg = i % wTrg;
         final float xSrc = xDiff + xTrg + tana * ( yTrg - yCenter );
         target[i] = Pixels.filterBilinear(xSrc, yTrg, wSrc, hSrc, source);
      }

      return target;
   }

   /**
    * Skews the pixels of a source image vertically. If the angle is
    * approximately zero, copies the source array. If the angle is
    * approximately {@link IUtils#HALF_PI} ({@value IUtils#HALF_PI}, returns
    * an empty array.<br>
    * <br>
    * Emits the new image dimensions to a {@link Vec2}.
    *
    * @param source the source pixels
    * @param wSrc   the source image width
    * @param hSrc   the source image height
    * @param angle  the angle in radians
    * @param dim    the new dimension
    *
    * @return the skewed array
    *
    * @see Pixels#filterBilinear(float, float, int, int, int[])
    * @see Utils#abs(float)
    * @see Utils#mod(float, float)
    * @see Utils#approx(float, float, float)
    */
   @Experimental
   public static int[] skewYBilinear ( final int[] source, final int wSrc,
      final int hSrc, final float angle, final Vec2 dim ) {

      final int srcLen = source.length;
      final float aVerif = Utils.mod(angle, IUtils.PI);
      if ( Utils.approx(aVerif, 0.0f, Pixels.EPS_RADIANS) ) {
         if ( dim != null ) { dim.set(wSrc, hSrc); }
         final int[] target = new int[srcLen];
         System.arraycopy(source, 0, target, 0, srcLen);
         return target;
      }
      if ( Utils.approx(aVerif, IUtils.HALF_PI, Pixels.EPS_RADIANS) ) {
         if ( dim != null ) { dim.set(wSrc, hSrc); }
         return new int[srcLen];
      }

      final float wSrcf = wSrc;
      final float hSrcf = hSrc;

      final float tana = ( float ) Math.tan(aVerif);
      final int hTrg = ( int ) ( 0.5f + hSrcf + Utils.abs(tana) * wSrcf );
      final float hTrgf = hTrg;
      final float xCenter = wSrcf * 0.5f;
      final float yDiff = ( hSrcf - hTrgf ) * 0.5f;

      final int trgLen = wSrc * hTrg;
      final int[] target = new int[trgLen];
      if ( dim != null ) { dim.set(wSrcf, hTrgf); }

      for ( int i = 0; i < trgLen; ++i ) {
         final int yTrg = i / wSrc;
         final int xTrg = i % wSrc;
         final float ySrc = yDiff + yTrg + tana * ( xTrg - xCenter );
         target[i] = Pixels.filterBilinear(xTrg, ySrc, wSrc, hSrc, source);
      }

      return target;
   }

   /**
    * Converts a pixel color from standard RGB to linear RGB. If the adjust
    * alpha flag is true, then alpha is converted as well.
    *
    * @param c           the color
    * @param adjustAlpha adjust alpha flag
    *
    * @return the standard pixels
    */
   public static int sRgbaTolRgba ( final int c, final boolean adjustAlpha ) {

      final int sai = c >> 0x18 & 0xff;
      return ( adjustAlpha ? Pixels.STL_LUT[sai] : sai ) << 0x18
         | Pixels.STL_LUT[c >> 0x10 & 0xff] << 0x10 | Pixels.STL_LUT[c >> 0x08
            & 0xff] << 0x08 | Pixels.STL_LUT[c & 0xff];
   }

   /**
    * Converts a source pixel array from standard RGB to linear RGB. If the
    * adjust alpha flag is true, then alpha is converted as well.
    *
    * @param source      the source pixels
    * @param adjustAlpha adjust alpha flag
    * @param target      the target pixels
    *
    * @return the linear pixels
    *
    * @see Pixels#sRgbaTolRgba(int, boolean)
    */
   public static int[] sRgbaTolRgba ( final int[] source,
      final boolean adjustAlpha, final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         for ( int i = 0; i < srcLen; ++i ) {
            target[i] = Pixels.sRgbaTolRgba(source[i], adjustAlpha);
         }
      }

      return target;
   }

   /**
    * Finds the luminance of a color, represented as a 32-bit integer, in
    * standard RGB. Converts the color to linear RGB, then calculates a
    * weighted average.
    *
    * @param c color
    *
    * @return the luminance
    */
   public static float sRgbLuminance ( final int c ) {

      return ( float ) ( 0.0008339189910613837d * Pixels.STL_LUT[c >> 0x10
         & 0xff] + 0.002804584845905505d * Pixels.STL_LUT[c >> 0x08 & 0xff]
         + 0.0002830647904840915d * Pixels.STL_LUT[c & 0xff] );
   }

   /**
    * Finds the minimum, maximum and mean lightness in a source pixels array.
    * If factor is positive, stretches color to maximum lightness range in
    * [0.0, 100.0]. If factor is negative, compresses color to mean. Assigns
    * result to target array. The factor is expected to be in [-1.0, 1.0].<br>
    * <br>
    * If difference between minimum and maximum lightness is negligible,
    * copies source array to target.
    *
    * @param source the source pixels
    * @param fac    the stretch or compress factor
    * @param target the target pixels
    *
    * @return the contrast pixels
    *
    * @see Utils#abs(float)
    * @see Utils#clamp(float, float, float)
    * @see Color#fromHex(int, Color)
    * @see Color#sRgbaTolRgba(Color, boolean, Color)
    * @see Color#lRgbaToXyza(Color, Vec4)
    * @see Color#xyzaToLaba(Vec4, Vec4)
    * @see Color#labaToXyza(Vec4, Vec4)
    * @see Color#xyzaTolRgba(Vec4, Color)
    * @see Color#lRgbaTosRgba(Color, boolean, Color)
    * @see Color#toHexIntSat(Color)
    */
   public static int[] stretchContrast ( final int[] source, final float fac,
      final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {

         final float valFac = Utils.clamp(fac, -1.0f, 1.0f);
         if ( valFac == 0.0f ) {
            System.arraycopy(source, 0, target, 0, srcLen);
            return target;
         }

         float lumMin = Float.MAX_VALUE;
         float lumMax = Float.MIN_VALUE;
         float lumSum = 0.0f;

         final Color srgb = new Color();
         final Color lrgb = new Color();
         final Vec4 xyz = new Vec4();

         final HashMap < Integer, Vec4 > dict = new HashMap <>();

         for ( int i = 0; i < srcLen; ++i ) {
            final int hex = source[i];
            if ( ( hex & 0xff000000 ) != 0 ) {
               final Integer hexObj = hex;
               if ( !dict.containsKey(hexObj) ) {
                  final Vec4 lab = new Vec4();

                  Color.fromHex(hex, srgb);
                  Color.sRgbaTolRgba(srgb, false, lrgb);
                  Color.lRgbaToXyza(lrgb, xyz);
                  Color.xyzaToLaba(xyz, lab);

                  dict.put(hexObj, lab);

                  final float lum = lab.z;
                  if ( lum < lumMin ) { lumMin = lum; }
                  if ( lum > lumMax ) { lumMax = lum; }
                  lumSum += lum;
               }
            }
         }

         final int dictLen = dict.size();
         if ( dictLen > 0 ) {
            final float diff = Utils.abs(lumMax - lumMin);
            if ( diff > IUtils.EPSILON ) {
               final float t = Utils.abs(valFac);
               final float u = 1.0f - t;
               final boolean gtZero = valFac > 0.0f;
               final boolean ltZero = valFac < -0.0f;

               final Vec4 stretchedLab = new Vec4();
               final float lumAvg = lumSum / dictLen;
               final float tDenom = t * ( 100.0f / diff );
               final float lumMintDenom = lumMin * tDenom;

               final HashMap < Integer, Integer > stretched = new HashMap <>();
               final Set < Map.Entry < Integer, Vec4 > > kvs = dict.entrySet();
               for ( final Map.Entry < Integer, Vec4 > kv : kvs ) {
                  stretchedLab.set(kv.getValue());
                  if ( gtZero ) {
                     stretchedLab.z = u * stretchedLab.z + stretchedLab.z
                        * tDenom - lumMintDenom;
                  } else if ( ltZero ) {
                     stretchedLab.z = u * stretchedLab.z + t * lumAvg;
                  }

                  Color.labaToXyza(stretchedLab, xyz);
                  Color.xyzaTolRgba(xyz, lrgb);
                  Color.lRgbaTosRgba(lrgb, false, srgb);
                  stretched.put(kv.getKey(), Color.toHexIntSat(srgb));
               }

               for ( int i = 0; i < srcLen; ++i ) {
                  final Integer srgbKeyObj = source[i];
                  if ( stretched.containsKey(srgbKeyObj) ) {
                     target[i] = stretched.get(srgbKeyObj);
                  } else {
                     target[i] = 0x00000000;
                  }
               }
            } else {
               System.arraycopy(source, 0, target, 0, srcLen);
            }
         } else {
            for ( int i = 0; i < srcLen; ++i ) { target[i] = 0x00000000; }
         }
      }

      return target;
   }

   /**
    * Mixes an image with a color in CIE LAB according to a factor.
    *
    * @param source the source pixels
    * @param tint   the tint color
    * @param fac    the factor
    * @param target the target pixels
    *
    * @return the tinted pixels
    *
    * @see Color#fromHex(int, Color)
    * @see Color#sRgbaTolRgba(Color, boolean, Color)
    * @see Color#lRgbaToXyza(Color, Vec4)
    * @see Color#xyzaToLaba(Vec4, Vec4)
    * @see Color#labaToXyza(Vec4, Vec4)
    * @see Color#xyzaTolRgba(Vec4, Color)
    * @see Color#lRgbaTosRgba(Color, boolean, Color)
    * @see Color#toHexIntSat(Color)
    */
   public static int[] tintLab ( final int[] source, final int tint,
      final float fac, final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         if ( fac <= 0.0f ) {
            System.arraycopy(source, 0, target, 0, srcLen);
            return target;
         }

         if ( fac >= 1.0f ) {
            for ( int i = 0; i < srcLen; ++i ) { target[i] = tint; }
            return target;
         }

         final float t = fac;
         final float u = 1.0f - t;

         final Color tintsRgb = new Color();
         final Color tintlRgb = new Color();
         final Vec4 tintXyz = new Vec4();
         final Vec4 dest = new Vec4();

         Color.fromHex(tint, tintsRgb);
         Color.sRgbaTolRgba(tintsRgb, false, tintlRgb);
         Color.lRgbaToXyza(tintlRgb, tintXyz);
         Color.xyzaToLaba(tintXyz, dest);

         final Color srgb = new Color();
         final Color lrgb = new Color();
         final Vec4 xyz = new Vec4();
         final Vec4 origin = new Vec4();

         final Vec4 mixLab = new Vec4();
         final Vec4 mixXyz = new Vec4();
         final Color mixlRgb = new Color();
         final Color mixsRgb = new Color();

         final HashMap < Integer, Integer > dict = new HashMap <>(512, 0.75f);

         for ( int i = 0; i < srcLen; ++i ) {
            final int srgbKeyInt = source[i];
            if ( ( srgbKeyInt & 0xff000000 ) != 0 ) {
               final Integer srgbKeyObj = srgbKeyInt;
               if ( !dict.containsKey(srgbKeyObj) ) {
                  Color.fromHex(srgbKeyInt, srgb);
                  Color.sRgbaTolRgba(srgb, false, lrgb);
                  Color.lRgbaToXyza(lrgb, xyz);
                  Color.xyzaToLaba(xyz, origin);

                  mixLab.set(u * origin.x + t * dest.x, u * origin.y + t
                     * dest.y, u * origin.z + t * dest.z, u * origin.w + t
                        * dest.w);

                  Color.labaToXyza(mixLab, mixXyz);
                  Color.xyzaTolRgba(mixXyz, mixlRgb);
                  Color.lRgbaTosRgba(mixlRgb, false, mixsRgb);
                  dict.put(srgbKeyObj, Color.toHexIntSat(mixsRgb));
               }
            }
         }

         if ( dict.size() > 0 ) {
            for ( int i = 0; i < srcLen; ++i ) {
               final Integer srgbKeyObj = source[i];
               if ( dict.containsKey(srgbKeyObj) ) {
                  target[i] = dict.get(srgbKeyObj);
               } else {
                  target[i] = 0x00000000;
               }
            }
         } else {
            for ( int i = 0; i < srcLen; ++i ) { target[i] = 0x00000000; }
         }
      }

      return target;
   }

   /**
    * Divides the red, green and blue channels of each pixel in the image by
    * its alpha channel. Reverse pre-multiplication.
    *
    * @param source the source pixels
    * @param target the target pixels
    *
    * @return the unpremultiplied pixels
    */
   public static int[] unpremul ( final int[] source, final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         for ( int i = 0; i < srcLen; ++i ) {
            final int srcHex = source[i];
            final int ai = srcHex >> 0x18 & 0xff;
            if ( ai < 1 ) {
               target[i] = 0x00000000;
            } else if ( ai < 255 ) {
               final float af = 255.0f / ai;
               int ru = ( int ) ( ( srcHex >> 0x10 & 0xff ) * af + 0.5f );
               int gu = ( int ) ( ( srcHex >> 0x08 & 0xff ) * af + 0.5f );
               int bu = ( int ) ( ( srcHex & 0xff ) * af + 0.5f );

               if ( ru > 255 ) { ru = 255; }
               if ( gu > 255 ) { gu = 255; }
               if ( bu > 255 ) { bu = 255; }

               target[i] = ai << 0x18 | ru << 0x10 | gu << 0x08 | bu;
            } else {
               target[i] = srcHex;
            }
         }
      }

      return target;
   }

}
