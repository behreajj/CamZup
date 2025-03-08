package camzup.core;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.IntFunction;

import camzup.core.Utils.TriFunction;
import camzup.pfriendly.ZImage;

/**
 * Holds methods that operate on arrays of pixels held by images.
 */
public abstract class Pixels {

   /**
    * Discourage overriding with a private constructor.
    */
   private Pixels ( ) {}

   /**
    * A flag to target colors in an image with greater than 66.67% lightness.
    * May be composited with {@link Pixels#SHADOWS} or
    * {@link Pixels#MIDTONES}.
    */
   public static final int HIGHLIGHTS = 0b0100;

   /**
    * A flag to target colors in an image with between 33.33% and 66.67%
    * lightness. May be composited with {@link Pixels#SHADOWS} or
    * {@link Pixels#HIGHLIGHTS}.
    */
   public static final int MIDTONES = 0b0010;

   /**
    * A flag to target colors in an image with less than 33.33% lightness. May
    * be composited with {@link Pixels#MIDTONES} or {@link Pixels#HIGHLIGHTS}.
    */
   public static final int SHADOWS = 0b0001;

   /**
    * Multiplies the alpha channel of each pixel in an array by the supplied
    * alpha value. Pixels may need further adjustment with
    * {@link Pixels#premul(int[], int[])} and
    * {@link Pixels#unpremul(int[], int[])}.
    *
    * @param source the source pixels
    * @param alpha  the alpha scalar
    * @param target the target pixels
    *
    * @return the adjusted pixels
    */
   public static int[] adjustAlpha ( final int[] source, final int alpha,
      final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         if ( alpha <= 0x0 ) {
            for ( int i = 0; i < srcLen; ++i ) { target[i] = 0x00000000; }
            return target;
         }

         if ( alpha == 0xff ) {
            System.arraycopy(source, 0, target, 0, srcLen);
            return target;
         }

         for ( int i = 0; i < srcLen; ++i ) {
            final int hex = source[i];
            final int srcAlpha = hex >> 0x18 & 0xff;
            final int trgAlpha = srcAlpha * alpha / 0xff;
            target[i] = ( trgAlpha > 0xff ? 0xff : trgAlpha ) << 0x18 | hex
               & 0x00ffffff;
         }
      }

      return target;
   }

   /**
    * Adjusts the contrast of colors from a source pixels array by a factor.
    * Uses the SR LAB 2 color space. The adjustment factor is expected to be
    * in [-1.0, 1.0].
    *
    * @param source the source pixels
    * @param fac    the contrast factor
    * @param target the target pixels
    *
    * @return the adjusted pixels
    *
    * @see Rgb#fromHex(int, Rgb)
    * @see Rgb#sRgbToSrLab2(Rgb, Lab, Vec4, Rgb)
    * @see Rgb#srLab2TosRgb(Lab, Rgb, Rgb, Vec4)
    * @see Rgb#toHexIntSat(Rgb)
    * @see Utils#clamp(float, float, float)
    */
   public static int[] adjustContrast ( final int[] source, final float fac,
      final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         final float valAdjust = 1.0f + Utils.clamp(fac, -1.0f, 1.0f);
         if ( Utils.approx(valAdjust, 1.0f) ) {
            System.arraycopy(source, 0, target, 0, srcLen);
            return target;
         }

         final Rgb srgb = new Rgb();
         final Rgb lrgb = new Rgb();
         final Vec4 xyz = new Vec4();
         final Lab lab = new Lab();
         final HashMap < Integer, Integer > dict = new HashMap <>(512, 0.75f);

         for ( int i = 0; i < srcLen; ++i ) {
            final int srgbKeyInt = source[i];
            if ( ( srgbKeyInt & 0xff000000 ) != 0 ) {
               final Integer srgbKeyObj = 0xff000000 | srgbKeyInt;
               if ( !dict.containsKey(srgbKeyObj) ) {
                  Rgb.fromHex(srgbKeyInt, srgb);
                  Rgb.sRgbToSrLab2(srgb, lab, xyz, lrgb);

                  lab.l = ( lab.l - 50.0f ) * valAdjust + 50.0f;

                  Rgb.srLab2TosRgb(lab, srgb, lrgb, xyz);
                  dict.put(srgbKeyObj, Rgb.toHexIntSat(srgb) & 0x00ffffff);
               }
            }
         }

         if ( dict.size() > 0 ) {
            for ( int i = 0; i < srcLen; ++i ) {
               final int srgbKeyInt = source[i];
               final Integer srgbKeyObj = 0xff000000 | srgbKeyInt;
               if ( dict.containsKey(srgbKeyObj) ) {
                  target[i] = srgbKeyInt & 0xff000000 | dict.get(srgbKeyObj);
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
    * Adjusts a source pixels array's colors in SR LCH. Assigns the results to
    * a target array.
    *
    * @param source the source pixels
    * @param adjust the adjustment
    * @param target the target pixels
    *
    * @return the adjusted pixels
    *
    * @see Rgb#fromHex(int, Rgb)
    * @see Rgb#sRgbToSrLch(Rgb, Lch, Lab, Vec4, Rgb)
    * @see Rgb#srLchTosRgb(Lch, Rgb, Rgb, Vec4, Lab)
    * @see Rgb#toHexIntSat(Rgb)
    * @see Vec4#none(Vec4)
    * @see Vec4#add(Vec4, Vec4, Vec4)
    */
   public static int[] adjustLch ( final int[] source, final Lch adjust,
      final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         if ( adjust.l == 0.0f && adjust.c == 0.0f && Utils.mod1(adjust.h)
            == 0.0f && adjust.alpha == 0.0f ) {
            System.arraycopy(source, 0, target, 0, srcLen);
            return target;
         }

         final Rgb srgb = new Rgb();
         final Rgb lrgb = new Rgb();
         final Vec4 xyz = new Vec4();
         final Lab lab = new Lab();
         final Lch lch = new Lch();
         final HashMap < Integer, Integer > dict = new HashMap <>(512, 0.75f);

         for ( int i = 0; i < srcLen; ++i ) {
            final int srgbKeyInt = source[i];
            if ( ( srgbKeyInt & 0xff000000 ) != 0 ) {
               final Integer srgbKeyObj = srgbKeyInt;
               if ( !dict.containsKey(srgbKeyObj) ) {
                  Rgb.fromHex(srgbKeyInt, srgb);
                  Rgb.sRgbToSrLch(srgb, lch, lab, xyz, lrgb);

                  lch.l = lch.l + adjust.l;
                  lch.c = lch.c + adjust.c;
                  lch.h = lch.h + adjust.h;
                  lch.alpha = lch.alpha + adjust.alpha;

                  Rgb.srLchTosRgb(lch, srgb, lrgb, xyz, lab);
                  dict.put(srgbKeyObj, Rgb.toHexIntSat(srgb));
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
    * Blends backdrop and overlay pixels. Forms a union of the bounding area
    * of the two inputs. Emits the dimensions and top-left corner of the
    * blended image.
    *
    * @param aPixels backdrop pixels
    * @param aw      backdrop width
    * @param ah      backdrop height
    * @param ax      backdrop x offset
    * @param ay      backdrop y offset
    * @param bPixels overlay pixels
    * @param bw      overlay width
    * @param bh      overlay height
    * @param bx      overlay x offset
    * @param by      overlay y offset
    * @param dim     dimensions
    * @param tl      top-left
    *
    * @return the blended pixels
    *
    * @see Rgb#srLab2TosRgb(Lab, Rgb, Rgb, Vec4)
    * @see Rgb#fromHex(int, Rgb)
    * @see Rgb#sRgbToSrLab2(Rgb, Lab, Vec4, Rgb)
    * @see Rgb#toHexIntSat(Rgb)
    * @see Vec4#zero(Vec4)
    */
   @Experimental
   public static int[] blendLab ( final int[] aPixels, final int aw,
      final int ah, final int ax, final int ay, final int[] bPixels,
      final int bw, final int bh, final int bx, final int by, final Vec2 dim,
      final Vec2 tl ) {

      final Rgb srgb = new Rgb();
      final Rgb lrgb = new Rgb();
      final Vec4 xyz = new Vec4();

      final HashMap < Integer, Lab > dict = new HashMap <>(512, 0.75f);
      dict.put(0x00000000, Lab.clearBlack(new Lab()));

      /* Find the bottom right corner for a and b. */
      final int abrx = ax + aw - 1;
      final int abry = ay + ah - 1;
      final int bbrx = bx + bw - 1;
      final int bbry = by + bh - 1;

      /* Blending only necessary at the intersection of a and b. */
      final int dx = ax > bx ? ax : bx;
      final int dy = ay > by ? ay : by;
      final int dbrx = abrx < bbrx ? abrx : bbrx;
      final int dbry = abry < bbry ? abry : bbry;
      final int dw = 1 + dbrx - dx;
      final int dh = 1 + dbry - dy;

      if ( dw > 0 && dh > 0 ) {
         /*
          * Find difference between the intersection top left and the top left
          * of a and b.
          */
         final int axid = ax - dx;
         final int ayid = ay - dy;
         final int bxid = bx - dx;
         final int byid = by - dy;

         final int dLen = dw * dh;
         for ( int h = 0; h < dLen; ++h ) {
            final int x = h % dw;
            final int y = h / dw;

            final int aHex = aPixels[x - axid + ( y - ayid ) * aw];
            final Integer aKeyObj = aHex;
            if ( !dict.containsKey(aKeyObj) ) {
               final Lab aLab = new Lab();
               Rgb.fromHex(aHex, srgb);
               Rgb.sRgbToSrLab2(srgb, aLab, xyz, lrgb);
               dict.put(aKeyObj, aLab);
            }

            final int bHex = bPixels[x - bxid + ( y - byid ) * bw];
            final Integer bKeyObj = bHex;
            if ( !dict.containsKey(bKeyObj) ) {
               final Lab bLab = new Lab();
               Rgb.fromHex(bHex, srgb);
               Rgb.sRgbToSrLab2(srgb, bLab, xyz, lrgb);
               dict.put(bKeyObj, bLab);
            }
         }
      }

      /* The result dimensions are the union of a and b. */
      final int cx = ax < bx ? ax : bx;
      final int cy = ay < by ? ay : by;
      final int cbrx = abrx > bbrx ? abrx : bbrx;
      final int cbry = abry > bbry ? abry : bbry;
      final int cw = 1 + cbrx - cx;
      final int ch = 1 + cbry - cy;
      final int cLen = cw * ch;

      /* Find difference between the union top left and top left of a and b. */
      final int axud = ax - cx;
      final int ayud = ay - cy;
      final int bxud = bx - cx;
      final int byud = by - cy;

      final Lab cLab = new Lab();
      final int[] target = new int[cLen];
      for ( int i = 0; i < cLen; ++i ) {
         final int x = i % cw;
         final int y = i / cw;

         int aHex = 0x00000000;
         final int axs = x - axud;
         final int ays = y - ayud;
         if ( ays > -1 && ays < ah && axs > -1 && axs < aw ) {
            aHex = aPixels[axs + ays * aw];
         }

         int bHex = 0x00000000;
         final int bxs = x - bxud;
         final int bys = y - byud;
         if ( bys > -1 && bys < bh && bxs > -1 && bxs < bw ) {
            bHex = bPixels[bxs + bys * bw];
         }

         final int t255 = bHex >> 0x18 & 0xff;
         if ( t255 >= 0xff ) {
            target[i] = bHex;
         } else if ( t255 <= 0x0 ) {
            target[i] = aHex;
         } else {
            final int v255 = aHex >> 0x18 & 0xff;
            if ( v255 <= 0x0 ) {
               target[i] = bHex;
            } else {
               final float t = t255 * IUtils.ONE_255;
               final float v = v255 * IUtils.ONE_255;
               final float u = 1.0f - t;
               final float uv = u * v;
               final float tuv = t + uv;

               if ( tuv <= 0.0f ) {
                  target[i] = 0x00000000;
               } else {
                  final Lab orig = dict.get(aHex);
                  final Lab dest = dict.get(bHex);

                  // Simulate alpha pre-multiply on lightness?
                  // if ( tuv >= 1.0f ) { tuv = 1.0f; }
                  // cLab.set(u * orig.x + t * dest.x, u * orig.y + t * dest.y,
                  // ( uv * orig.z + t * dest.z ) / tuv, tuv);

                  cLab.set(u * orig.l + t * dest.l, u * orig.a + t * dest.a, u
                     * orig.b + t * dest.b, tuv);

                  Rgb.srLab2TosRgb(cLab, srgb, lrgb, xyz);
                  target[i] = Rgb.toHexIntSat(srgb);
               }
            }
         }
      }

      if ( dim != null ) { dim.set(cw, ch); }
      if ( tl != null ) { tl.set(cx, cy); }
      return target;
   }

   /**
    * Blurs an array of pixels by averaging each color with its neighbors in 8
    * directions. The step determines the size of the kernel, where the
    * minimum step of 1 will make a 3x3, 9 pixel kernel. Averages the color's
    * SR LAB 2 representation.
    *
    * @param source the source pixels
    * @param wSrc   the image width
    * @param hSrc   the image height
    * @param step   the kernel step
    * @param target the target pixels
    *
    * @return the blurred image
    *
    * @see Rgb#fromHex(int, Rgb)
    * @see Rgb#srLab2TosRgb(Lab, Rgb, Rgb, Vec4)
    * @see Rgb#sRgbToSrLab2(Rgb, Lab, Vec4, Rgb)
    * @see Rgb#toHexIntSat(Rgb)
    */
   public static int[] blurBoxLab ( final int[] source, final int wSrc,
      final int hSrc, final int step, final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {

         /* Place lab lookups in a dictionary. */
         final Rgb srgb = new Rgb();
         final Rgb lrgb = new Rgb();
         final Vec4 xyz = new Vec4();
         final HashMap < Integer, Lab > dict = new HashMap <>(512, 0.75f);
         for ( int i = 0; i < srcLen; ++i ) {
            final int hexInt = source[i];
            final Integer hexObj = hexInt;
            if ( !dict.containsKey(hexObj) ) {
               final Lab lab = new Lab();
               Rgb.fromHex(hexInt, srgb);
               Rgb.sRgbToSrLab2(srgb, lab, xyz, lrgb);
               dict.put(hexObj, lab);
            }
         }

         final int stepVal = step < 1 ? 1 : step;
         final int wKrn = 1 + stepVal * 2;
         final int krnLen = wKrn * wKrn;
         final float toAvg = 1.0f / krnLen;
         final Lab labAvg = new Lab();

         for ( int i = 0; i < srcLen; ++i ) {
            /* Subtract step to center the kernel in the inner for loop. */
            final int xSrc = i % wSrc - stepVal;
            final int ySrc = i / wSrc - stepVal;
            final int hexSrc = source[i];
            final Integer hexSrcObj = hexSrc;
            final Lab labCtr = dict.get(hexSrcObj);

            float lSum = 0.0f;
            float aSum = 0.0f;
            float bSum = 0.0f;
            float tSum = 0.0f;

            for ( int j = 0; j < krnLen; ++j ) {
               final int xComp = xSrc + j % wKrn;
               final int yComp = ySrc + j / wKrn;
               if ( yComp > -1 && yComp < hSrc && xComp > -1 && xComp < wSrc ) {
                  final Lab labNgbr = dict.get(source[xComp + yComp * wSrc]);
                  lSum += labNgbr.l;
                  aSum += labNgbr.a;
                  bSum += labNgbr.b;
                  tSum += labNgbr.alpha;
               } else {
                  /*
                   * When the kernel is out of bounds, sample the central color
                   * but do not tally alpha.
                   */
                  lSum += labCtr.l;
                  aSum += labCtr.a;
                  bSum += labCtr.b;
               }
            }

            labAvg.set(lSum * toAvg, aSum * toAvg, bSum * toAvg, tSum * toAvg);
            Rgb.srLab2TosRgb(labAvg, srgb, lrgb, xyz);
            target[i] = Rgb.toHexIntSat(srgb);
         }
      }
      return target;
   }

   /**
    * Creates a checker pattern in an array of pixels.
    *
    * @param a      the first color
    * @param b      the second color
    * @param cols   the column count
    * @param rows   the row count
    * @param w      the image width
    * @param h      the image height
    * @param target the target pixels
    *
    * @return the checker pattern
    */
   public static int[] checker ( final int a, final int b, final int cols,
      final int rows, final int w, final int h, final int[] target ) {

      /*
       * Rows and columns should have a maximum bound, because it will be easy
       * to transpose color and columns & rows arguments.
       */
      final int limit = 2;
      final int vCols = cols < 2 ? 2 : cols > w / limit ? w / limit : cols;
      final int vRows = rows < 2 ? 2 : rows > h / limit ? h / limit : rows;
      final int wch = w / vCols;
      final int hchw = w * h / vRows;

      final int va = a != b ? a : ZImage.CHECKER_DARK;
      final int vb = a != b ? b : ZImage.CHECKER_LIGHT;

      final int trgLen = target.length;
      for ( int i = 0; i < trgLen; ++i ) {
         /* % 2 can be replaced by & 1 for even or odd. */
         target[i] = ( i % w / wch + i / hchw & 1 ) == 0 ? va : vb;
      }

      return target;
   }

   /**
    * Fills the pixels target array with a color.
    *
    * @param c      the fill color
    * @param target the target pixels
    *
    * @return the filled pixels
    */
   public static int[] fill ( final int c, final int[] target ) {

      final int len = target.length;
      for ( int i = 0; i < len; ++i ) { target[i] = c; }

      return target;
   }

   /**
    * Filters an array of pixels. The function delegate is expected to filter
    * the color according to an upper and lower bound. Both bounds should be
    * inclusive. Returns an array of indices that refer to the source array,
    * not an array of colors.
    *
    * @param source the source pixels
    * @param lb     the lower bound
    * @param ub     the upper bound
    * @param f      the filter function
    *
    * @return the indices array
    */
   public static int[] filter ( final int[] source, final float lb,
      final float ub, final TriFunction < Integer, Float, Float, Boolean > f ) {

      final HashMap < Integer, ArrayList < Integer > > dict = new HashMap <>(
         512, 0.75f);
      Pixels.filter(source, lb, ub, f, dict);
      final Collection < ArrayList < Integer > > idcsTotal = dict.values();

      int compLen = 0;
      Iterator < ArrayList < Integer > > itrTotal = idcsTotal.iterator();
      while ( itrTotal.hasNext() ) { compLen += itrTotal.next().size(); }
      final int[] result = new int[compLen];

      int i = 0;
      itrTotal = idcsTotal.iterator();
      while ( itrTotal.hasNext() ) {
         final ArrayList < Integer > idcsLocal = itrTotal.next();
         for ( final Iterator < Integer > itrLocal = idcsLocal.iterator();
            itrLocal.hasNext(); ++i ) {
            result[i] = itrLocal.next();
         }
      }
      return result;
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
    * Hashes an image according to the <a href=
    * "https://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function">Fowler–Noll–Vo</a>
    * method.
    *
    * @param source the source pixels
    *
    * @return the hash
    */
   public static BigInteger fnvHash ( final int[] source ) {

      // TODO: TEST

      final BigInteger fnvPrime = BigInteger.valueOf(1099511628211L);
      BigInteger hash = new BigInteger("14695981039346656037");
      final int len = source.length;
      for ( int i = 0; i < len; ++i ) {
         hash = hash.xor(BigInteger.valueOf(source[i])).multiply(fnvPrime);
      }

      return hash;
   }

   /**
    * Generates a conic gradient, where the factor rotates on the z axis
    * around an origin point. Best used with square images; for other aspect
    * ratios, the origin should be adjusted accordingly.
    *
    * @param grd     the gradient
    * @param xOrig   the origin x coordinate
    * @param yOrig   the origin y coordinate
    * @param radians the angle in radians
    * @param wTrg    the image width
    * @param hTrg    the image height
    * @param easing  the easing function
    * @param target  the target pixels
    *
    * @return the gradient pixels
    *
    * @see Rgb#toHexIntSat(Rgb)
    * @see Gradient#eval(Gradient, float, Rgb.AbstrEasing, Rgb)
    * @see Utils#mod1(float)
    */
   public static int[] gradientConic ( final Gradient grd, final float xOrig,
      final float yOrig, final float radians, final Rgb.AbstrEasing easing,
      final int wTrg, final int hTrg, final int[] target ) {

      final double aspect = wTrg / ( double ) hTrg;
      final double wInv = aspect / ( wTrg - 1.0d );
      final double hInv = 1.0d / ( hTrg - 1.0d );
      final double xo = ( xOrig * 0.5d + 0.5d ) * aspect * 2.0d - 1.0d;
      final double yo = yOrig;
      final double rd = radians;

      final Rgb trgClr = new Rgb();
      final int trgLen = target.length;
      for ( int i = 0; i < trgLen; ++i ) {
         final double xn = wInv * ( i % wTrg );
         final double yn = hInv * ( i / wTrg );
         final float fac = Utils.mod1(( float ) ( ( Math.atan2(1.0d - ( yn + yn
            + yo ), xn + xn - xo - 1.0d) - rd ) * IUtils.ONE_TAU_D ));
         Gradient.eval(grd, fac, easing, trgClr);
         target[i] = Rgb.toHexIntSat(trgClr);
      }

      return target;
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The origin and destination should be in the range [-1.0, 1.0]. The
    * scalar projection is clamped to [0.0, 1.0].
    *
    * @param grd    the gradient
    * @param xOrig  the origin x coordinate
    * @param yOrig  the origin y coordinate
    * @param xDest  the destination x coordinate
    * @param yDest  the destination y coordinate
    * @param wTrg   the image width
    * @param hTrg   the image height
    * @param easing the easing function
    * @param target the target pixels
    *
    * @return the gradient pixels
    *
    * @see Rgb#toHexIntSat(Rgb)
    * @see Gradient#eval(Gradient, float, Rgb.AbstrEasing, Rgb)
    * @see Utils#max(float, float)
    * @see Utils#clamp01(float)
    */
   public static int[] gradientLinear ( final Gradient grd, final float xOrig,
      final float yOrig, final float xDest, final float yDest,
      final Rgb.AbstrEasing easing, final int wTrg, final int hTrg,
      final int[] target ) {

      final float bx = xOrig - xDest;
      final float by = yOrig - yDest;

      final float bbInv = 1.0f / Utils.max(IUtils.EPSILON, bx * bx + by * by);

      final float bxbbinv = bx * bbInv;
      final float bybbinv = by * bbInv;

      final float xobx = xOrig * bxbbinv;
      final float yoby = yOrig * bybbinv;
      final float bxwInv2 = 2.0f / ( wTrg - 1.0f ) * bxbbinv;
      final float byhInv2 = 2.0f / ( hTrg - 1.0f ) * bybbinv;

      final Rgb trgClr = new Rgb();
      final int trgLen = target.length;
      for ( int i = 0; i < trgLen; ++i ) {
         final float fac = Utils.clamp01(xobx + bxbbinv - bxwInv2 * ( i % wTrg )
            + ( yoby + byhInv2 * ( i / wTrg ) - bybbinv ));
         Gradient.eval(grd, fac, easing, trgClr);
         target[i] = Rgb.toHexIntSat(trgClr);
      }

      return target;
   }

   /**
    * Maps the colors of a source pixels array to those of a gradient using a
    * mapping function. The mapping function accepts a pixel as an argument
    * and returns a factor to be given to a gradient evaluation method.
    * Retains the original color's transparency.
    *
    * @param source the source pixels
    * @param grd    the gradient
    * @param easing the easing function
    * @param map    the mapping function
    * @param target the target pixels
    *
    * @return the mapped pixels
    *
    * @see Rgb#toHexIntSat(Rgb)
    * @see Gradient#eval(Gradient, float, Rgb.AbstrEasing, Rgb)
    */
   public static int[] gradientMap ( final int[] source, final Gradient grd,
      final Rgb.AbstrEasing easing, final IntFunction < Float > map,
      final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {

         /*
          * Remove alpha from gradient evaluated color so that it can be
          * replaced by source alpha.
          */
         final HashMap < Integer, Integer > dict = new HashMap <>(512, 0.75f);
         final Rgb trgClr = new Rgb();

         for ( int i = 0; i < srcLen; ++i ) {
            final int srgbKeyInt = source[i];
            if ( ( srgbKeyInt & 0xff000000 ) != 0 ) {
               final Integer srgbKeyObj = 0xff000000 | srgbKeyInt;
               if ( !dict.containsKey(srgbKeyObj) ) {
                  Gradient.eval(grd, map.apply(srgbKeyInt), easing, trgClr);
                  dict.put(srgbKeyObj, Rgb.toHexIntSat(trgClr) & 0x00ffffff);
               }
            }
         }

         if ( dict.size() > 0 ) {
            for ( int i = 0; i < srcLen; ++i ) {
               final int srgbKeyInt = source[i];
               final Integer srgbKeyObj = 0xff000000 | srgbKeyInt;
               if ( dict.containsKey(srgbKeyObj) ) {
                  target[i] = srgbKeyInt & 0xff000000 | dict.get(srgbKeyObj);
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
    * Generates a radial gradient from an origin point. The origin should be
    * in the range [-1.0, 1.0]. Does not account for aspect ratio, so an image
    * that isn't 1:1 will result in an ellipsoid.
    *
    * @param grd    the gradient
    * @param xOrig  the origin x coordinate
    * @param yOrig  the origin y coordinate
    * @param radius the radius
    * @param wTrg   the image width
    * @param hTrg   the image height
    * @param easing the easing function
    * @param target the target pixels
    *
    * @return the gradient pixels
    *
    * @see Rgb#toHexIntSat(Rgb)
    * @see Gradient#eval(Gradient, float, Rgb.AbstrEasing, Rgb)
    * @see Utils#max(float, float)
    */
   public static int[] gradientRadial ( final Gradient grd, final float xOrig,
      final float yOrig, final float radius, final Rgb.AbstrEasing easing,
      final int wTrg, final int hTrg, final int[] target ) {

      final float hInv2 = 2.0f / ( hTrg - 1.0f );
      final float wInv2 = 2.0f / ( wTrg - 1.0f );

      final float r2 = radius + radius;
      final float rsqInv = 1.0f / Utils.max(IUtils.EPSILON, r2 * r2);

      final float yon1 = yOrig - 1.0f;
      final float xop1 = xOrig + 1.0f;

      final Rgb trgClr = new Rgb();
      final int trgLen = target.length;
      for ( int i = 0; i < trgLen; ++i ) {
         final float ay = yon1 + hInv2 * ( i / wTrg );
         final float ax = xop1 - wInv2 * ( i % wTrg );
         final float fac = 1.0f - ( ax * ax + ay * ay ) * rsqInv;
         Gradient.eval(grd, fac, easing, trgClr);
         target[i] = Rgb.toHexIntSat(trgClr);
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
    */
   public static int[] grayscale ( final int[] source, final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         for ( int i = 0; i < srcLen; ++i ) {
            final int srcHex = source[i];
            final int ai = srcHex & 0xff000000;
            if ( ai != 0 ) {
               final int v255 = ( int ) ( Pixels.srgbLuminance(source[i])
                  * 255.0f + 0.5f );
               target[i] = ai | v255 << 0x10 | v255 << 0x08 | v255;
            } else {
               target[i] = 0x00000000;
            }
         }
      }

      return target;
   }

   /**
    * Inverts colors from a source pixels array in SR LAB 2.
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
    * @see Rgb#fromHex(int, Rgb)
    * @see Rgb#sRgbToSrLab2(Rgb, Lab, Vec4, Rgb)
    * @see Rgb#srLab2TosRgb(Lab, Rgb, Rgb, Vec4)
    * @see Rgb#toHexIntSat(Rgb)
    */
   public static int[] invertLab ( final int[] source, final boolean l,
      final boolean a, final boolean b, final boolean alpha,
      final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         if ( !l && !a && !b && !alpha ) {
            System.arraycopy(source, 0, target, 0, srcLen);
            return target;
         }

         final Rgb srgb = new Rgb();
         final Rgb lrgb = new Rgb();
         final Vec4 xyz = new Vec4();
         final Lab lab = new Lab();
         final HashMap < Integer, Integer > dict = new HashMap <>(512, 0.75f);

         final float aSign = a ? -1.0f : 1.0f;
         final float bSign = b ? -1.0f : 1.0f;

         for ( int i = 0; i < srcLen; ++i ) {
            int srgbKeyInt = source[i];
            if ( ( srgbKeyInt & 0xff000000 ) == 0 ) { srgbKeyInt = 0; }
            final Integer srgbKeyObj = srgbKeyInt;

            if ( !dict.containsKey(srgbKeyObj) ) {
               Rgb.fromHex(srgbKeyInt, srgb);
               Rgb.sRgbToSrLab2(srgb, lab, xyz, lrgb);

               lab.a *= aSign;
               lab.b *= bSign;
               if ( l ) { lab.l = 100.0f - lab.l; }
               if ( alpha ) { lab.alpha = 1.0f - lab.alpha; }

               Rgb.srLab2TosRgb(lab, srgb, lrgb, xyz);
               dict.put(srgbKeyObj, Rgb.toHexIntSat(srgb));
            }
         }

         if ( dict.size() > 0 ) {
            for ( int i = 0; i < srcLen; ++i ) {
               final Integer srgbKeyObj = source[i];
               if ( dict.containsKey(srgbKeyObj) ) {
                  target[i] = dict.get(srgbKeyObj);
               }
            }
         } else {
            for ( int i = 0; i < srcLen; ++i ) { target[i] = 0x00000000; }
         }
      }

      return target;
   }

   /**
    * Masks the pixels of an under image with the alpha channel of the over
    * image. Forms an intersection of the bounding area of the two inputs.
    * Emits the dimensions and top-left corner of the blended image.
    *
    * @param aPixels backdrop pixels
    * @param aw      backdrop width
    * @param ah      backdrop height
    * @param ax      backdrop x offset
    * @param ay      backdrop y offset
    * @param bPixels overlay pixels
    * @param bw      overlay width
    * @param bh      overlay height
    * @param bx      overlay x offset
    * @param by      overlay y offset
    * @param dim     dimensions
    * @param tl      top-left
    *
    * @return the masked pixels
    */
   public static int[] mask ( final int[] aPixels, final int aw, final int ah,
      final int ax, final int ay, final int[] bPixels, final int bw,
      final int bh, final int bx, final int by, final Vec2 dim,
      final Vec2 tl ) {

      /* Find the bottom right corner for a and b. */
      final int abrx = ax + aw - 1;
      final int abry = ay + ah - 1;
      final int bbrx = bx + bw - 1;
      final int bbry = by + bh - 1;

      /* The result dimensions are the intersection of a and b. */
      final int cx = ax > bx ? ax : bx;
      final int cy = ay > by ? ay : by;
      final int cbrx = abrx < bbrx ? abrx : bbrx;
      final int cbry = abry < bbry ? abry : bbry;
      final int cw = 1 + cbrx - cx;
      final int ch = 1 + cbry - cy;
      final int clen = cw * ch;

      /* Find the difference between the target top left and the inputs. */
      final int axd = ax - cx;
      final int ayd = ay - cy;
      final int bxd = bx - cx;
      final int byd = by - cy;

      final int[] target = new int[clen];
      for ( int i = 0; i < clen; ++i ) {
         final int x = i % cw;
         final int y = i / cw;
         final int bxs = x - bxd;
         final int bys = y - byd;
         int trgHex = 0x00000000;
         if ( bys > -1 && bys < bh && bxs > -1 && bxs < bw ) {
            final int hexOvr = bPixels[bxs + bys * bw];
            final int aOvr = hexOvr >> 0x18 & 0xff;
            if ( aOvr > 0 ) {
               final int axs = x - axd;
               final int ays = y - ayd;
               if ( ays > -1 && ays < ah && axs > -1 && axs < aw ) {
                  final int hexUdr = aPixels[axs + ays * aw];
                  final int aUdr = hexUdr >> 0x18 & 0xff;
                  if ( aUdr > 0 ) {
                     final int aTrg = aOvr * aUdr / 0xff;
                     trgHex = aTrg << 0x18 | hexUdr & 0x00ffffff;
                  }
               }
            }
         }
         target[i] = trgHex;
      }

      if ( dim != null ) { dim.set(cw, ch); }
      if ( tl != null ) { tl.set(cx, cy); }
      return target;
   }

   /**
    * Mirrors, or reflects, pixels from a source image across the axis
    * described by an origin and destination. Coordinates are expected to be
    * in the range [-1.0, 1.0]. Out-of-bounds pixels are omitted from the
    * mirror.
    *
    * @param source the source pixels
    * @param wSrc   the source image width
    * @param hSrc   the source image height
    * @param xOrig  the origin x
    * @param yOrig  the origin y
    * @param xDest  the destination x
    * @param yDest  the destination y
    * @param flip   the flip reflection flag
    * @param target the target pixels
    *
    * @return the mirrored pixels
    *
    * @see Pixels#sampleBilinear(int[], int, int, float, float)
    * @see Pixels#mirrorX(int[], int, int, boolean, int[])
    * @see Pixels#mirrorY(int[], int, int, int, boolean, int[])
    * @see Utils#approx(float, float, float)
    * @see Utils#round(float)
    */
   public static int[] mirror ( final int[] source, final int wSrc,
      final int hSrc, final float xOrig, final float yOrig, final float xDest,
      final float yDest, final boolean flip, final int[] target ) {

      final float wfn1 = wSrc - 1.0f;
      final float hfn1 = hSrc - 1.0f;
      final float wfp1Half = ( wSrc + 1.0f ) * 0.5f;
      final float hfp1Half = ( hSrc + 1.0f ) * 0.5f;

      final float ax = ( xOrig + 1.0f ) * wfp1Half - 0.5f;
      final float bx = ( xDest + 1.0f ) * wfp1Half - 0.5f;
      final float ay = ( yOrig - 1.0f ) * -hfp1Half - 0.5f;
      final float by = ( yDest - 1.0f ) * -hfp1Half - 0.5f;

      final float dx = bx - ax;
      final float dy = by - ay;
      final boolean dxZero = Utils.approx(dx, 0.0f, 0.5f);
      final boolean dyZero = Utils.approx(dy, 0.0f, 0.5f);

      if ( dxZero && dyZero ) {
         System.arraycopy(source, 0, target, 0, source.length < target.length
            ? source.length : target.length);
         return target;
      }

      if ( dxZero ) {
         return Pixels.mirrorX(source, wSrc, Utils.round(bx), flip ? ay > by
            : by > ay, target);
      }

      if ( dyZero ) {
         return Pixels.mirrorY(source, wSrc, hSrc, Utils.round(by), flip ? bx
            > ax : ax > bx, target);
      }

      final float dMagSqInv = 1.0f / ( dx * dx + dy * dy );
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

            final float pyProj = u * ay + t * by;
            final float pyOpp = pyProj + pyProj - cy;

            final float pxProj = u * ax + t * bx;
            final float pxOpp = pxProj + pxProj - cx;

            /*
             * Default to omitting pixels that are out-of-bounds, rather than
             * wrapping with floor modulo or clamping.
             */
            if ( pyOpp >= 0.0f && pyOpp <= hfn1 && pxOpp >= 0.0f && pxOpp
               <= wfn1 ) {
               target[k] = Pixels.sampleBilinear(source, wSrc, hSrc, pxOpp,
                  pyOpp);
            } else {
               target[k] = 0x00000000;
            }
         }
      }

      return target;
   }

   /**
    * Mirrors, or reflects, pixels from a source image horizontally across a
    * pivot. The pivot is expected to be in [-1, width + 1].
    *
    * @param source the source pixels
    * @param w      the source image width
    * @param pivot  the x pivot
    * @param flip   the flip reflection flag
    * @param target the target pixels
    *
    * @return the mirrored pixels
    */
   public static int[] mirrorX ( final int[] source, final int w,
      final int pivot, final boolean flip, final int[] target ) {

      final int trgLen = target.length;
      final int flipSign = flip ? 1 : -1;

      for ( int k = 0; k < trgLen; ++k ) {
         final int cross = k % w - pivot;
         if ( flipSign * cross < 0 ) {
            target[k] = source[k];
         } else {
            final int pxOpp = pivot - cross;
            if ( pxOpp > -1 && pxOpp < w ) {
               target[k] = source[k / w * w + pxOpp];
            } else {
               target[k] = 0x00000000;
            }
         }
      }

      return target;
   }

   /**
    * Mirrors, or reflects, pixels from a source image vertically across a
    * pivot. The pivot is expected to be in [-1, height + 1]. Positive y
    * points down to the bottom of the image.
    *
    * @param source the source pixels
    * @param w      the source image width
    * @param h      the source image height
    * @param pivot  the y pivot
    * @param flip   the flip reflection flag
    * @param target the target pixels
    *
    * @return the mirrored pixels
    */
   public static int[] mirrorY ( final int[] source, final int w, final int h,
      final int pivot, final boolean flip, final int[] target ) {

      final int trgLen = target.length;
      final int flipSign = flip ? 1 : -1;

      for ( int k = 0; k < trgLen; ++k ) {
         final int cross = k / w - pivot;
         if ( flipSign * cross < 0 ) {
            target[k] = source[k];
         } else {
            final int pyOpp = pivot - cross;
            if ( pyOpp > -1 && pyOpp < h ) {
               target[k] = source[pyOpp * w + k % w];
            } else {
               target[k] = 0x00000000;
            }
         }
      }

      return target;
   }

   /**
    * Extracts a palette from a source pixels array using an octree in SR LAB.
    * The size of the palette depends on the capacity of each node in the
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
    * @see Bounds3#lab(Bounds3)
    * @see Lab#toSrXyz(Lab, Vec4)
    * @see Rgb#srXyzTolRgb(Vec4, Rgb)
    * @see Rgb#clearBlack(Rgb)
    * @see Rgb#fromHex(int, Rgb)
    * @see Rgb#sRgbToSrLab2(Rgb, Lab, Vec4, Rgb)
    * @see Octree#insert(Vec3)
    */
   public static Rgb[] paletteExtract ( final int[] source, final int capacity,
      final int threshold ) {

      /* Find unique opaque colors. */
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
         final Rgb[] result = new Rgb[1 + uniquesLen];
         for ( int i = 1; uniquesItr.hasNext(); ++i ) {
            result[i] = Rgb.fromHex(uniquesItr.next(), new Rgb());
         }
         result[0] = Rgb.clearBlack(new Rgb());
         return result;
      }

      final Bounds3 bounds = Bounds3.lab(new Bounds3());
      final Octree oct = new Octree(bounds, capacity);
      final Rgb srgb = new Rgb();
      final Rgb lrgb = new Rgb();
      final Vec4 xyz = new Vec4();
      final Lab lab = new Lab();

      /* Place colors in octree. */
      while ( uniquesItr.hasNext() ) {
         Rgb.fromHex(uniquesItr.next(), srgb);
         Rgb.sRgbToSrLab2(srgb, lab, xyz, lrgb);
         oct.insert(new Vec3(lab.a, lab.b, lab.l));
      }
      oct.cull();

      /* Trying to use package level with an array list throws an exception. */
      final Vec3[] centers = Octree.centersMean(oct, false);
      final int centersLen = centers.length;
      final Rgb[] result = new Rgb[1 + centersLen];
      for ( int i = 0; i < centersLen; ++i ) {
         final Vec3 center = centers[i];
         final Rgb target = new Rgb();
         Lab.toSrXyz(center.z, center.x, center.y, 1.0f, xyz);
         Rgb.srXyzTolRgb(xyz, lrgb);
         Rgb.lRgbTosRgb(lrgb, false, target);
         result[1 + i] = target;
      }
      result[0] = Rgb.clearBlack(new Rgb());
      return result;
   }

   /**
    * Applies a palette to an array of pixels using an Octree to find the
    * nearest match in Euclidean space. Retains the original color's
    * transparency.
    *
    * @param source   the source pixels
    * @param palette  the color palette
    * @param capacity the octree capacity
    * @param radius   the query radius
    * @param target   the target pixels
    *
    * @return the modified pixels
    *
    * @see Bounds3#lab(Bounds3)
    * @see Rgb#sRgbToSrLab2(Rgb, Lab, Vec4, Rgb)
    * @see Rgb#fromHex(int, Rgb)
    * @see Rgb#toHexIntSat(Rgb)
    * @see Octree#insert(Vec3)
    * @see Octree#cull()
    * @see Utils#abs(float)
    * @see Utils#max(float, float)
    */
   public static int[] paletteMap ( final int[] source, final Rgb[] palette,
      final int capacity, final float radius, final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         final Bounds3 bounds = Bounds3.lab(new Bounds3());
         final Octree oct = new Octree(bounds, capacity);
         oct.subdivide(1, capacity);

         final Rgb srgb = new Rgb();
         final Rgb lrgb = new Rgb();
         final Vec4 xyz = new Vec4();
         final Lab lab = new Lab();
         final Vec3 query = new Vec3();

         final HashMap < Vec3, Integer > lookup = new HashMap <>(256, 0.75f);
         final int palLen = palette.length;
         for ( int h = 0; h < palLen; ++h ) {
            final Rgb clrPal = palette[h];
            if ( Rgb.any(clrPal) ) {
               Rgb.sRgbToSrLab2(clrPal, lab, xyz, lrgb);
               final Vec3 point = new Vec3(lab.a, lab.b, lab.l);
               oct.insert(point);
               lookup.put(point, Rgb.toHexIntSat(clrPal));
            }
         }
         oct.cull();

         final TreeMap < Float, Vec3 > found = new TreeMap <>();
         final HashMap < Integer, Integer > dict = new HashMap <>(512, 0.75f);
         final float rVrf = Utils.max(IUtils.EPSILON, Utils.abs(radius));
         final float rsq = rVrf * rVrf;
         for ( int i = 0; i < srcLen; ++i ) {
            final int srcHexInt = source[i];
            if ( ( srcHexInt & 0xff000000 ) != 0 ) {
               final int maskAlpha = srcHexInt & 0xff000000;
               final int opaque = srcHexInt | 0xff000000;
               final Integer srcHexObj = opaque;
               if ( dict.containsKey(srcHexObj) ) {
                  target[i] = maskAlpha | dict.get(srcHexObj);
               } else {
                  Rgb.fromHex(srcHexInt, srgb);
                  Rgb.sRgbToSrLab2(srgb, lab, xyz, lrgb);

                  query.set(lab.a, lab.b, lab.l);
                  found.clear();
                  Octree.query(oct, query, rsq, 0, found);
                  if ( found.size() > 0 ) {
                     final Vec3 near = found.values().iterator().next();
                     // final Vec3 near = found.ceilingEntry(0.0f).getValue();
                     if ( near != null && lookup.containsKey(near) ) {
                        final int hexNearInt = lookup.get(near) & 0x00ffffff;
                        dict.put(srcHexObj, hexNearInt);
                        target[i] = maskAlpha | hexNearInt;
                     } else {
                        target[i] = 0x00000000;
                     }
                  } else {
                     target[i] = 0x00000000;
                  }
               }
            } else {
               target[i] = 0x00000000;
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
            } else if ( ai < 0xff ) {
               final float af = ai * IUtils.ONE_255;
               int rp = ( int ) ( ( srcHex >> 0x10 & 0xff ) * af + 0.5f );
               int gp = ( int ) ( ( srcHex >> 0x08 & 0xff ) * af + 0.5f );
               int bp = ( int ) ( ( srcHex & 0xff ) * af + 0.5f );

               if ( rp > 0xff ) { rp = 0xff; }
               if ( gp > 0xff ) { gp = 0xff; }
               if ( bp > 0xff ) { bp = 0xff; }

               target[i] = ai << 0x18 | rp << 0x10 | gp << 0x08 | bp;
            } else {
               target[i] = srcHex;
            }
         }
      }

      return target;
   }

   /**
    * Resizes pixels from a source image to a requested size using a bilinear
    * filter. Copies the source to the target if source and target dimensions
    * are equal.
    *
    * @param source the source pixels
    * @param wSrc   the source image width
    * @param hSrc   the source image height
    * @param wTrg   the target width
    * @param hTrg   the target height
    *
    * @return the resized image
    *
    * @see Pixels#sampleBilinear(int[], int, int, float, float)
    */
   public static int[] resizeBilinear ( final int[] source, final int wSrc,
      final int hSrc, final int wTrg, final int hTrg ) {

      final int srcLen = source.length;
      if ( wSrc == wTrg && hSrc == hTrg ) {
         final int[] target = new int[srcLen];
         System.arraycopy(source, 0, target, 0, srcLen);
         return target;
      }

      final float wDenom = wTrg - 1.0f;
      final float hDenom = hTrg - 1.0f;
      final float tx = wDenom != 0.0f ? ( wSrc - 1.0f ) / wDenom : 0.0f;
      final float ty = hDenom != 0.0f ? ( hSrc - 1.0f ) / hDenom : 0.0f;
      final float ox = wDenom != 0.0f ? 0.0f : 0.5f;
      final float oy = hDenom != 0.0f ? 0.0f : 0.5f;

      final int trgLen = wTrg * hTrg;
      final int[] target = new int[trgLen];
      for ( int i = 0; i < trgLen; ++i ) {
         target[i] = Pixels.sampleBilinear(source, wSrc, hSrc, tx * ( i % wTrg )
            + ox, ty * ( i / wTrg ) + oy);
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
    * @return the RGB pixels
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
    * @see Pixels#sampleBilinear(int[], int, int, float, float)
    * @see Utils#abs(float)
    */
   public static int[] rotateBilinear ( final int[] source, final int wSrc,
      final int hSrc, final float cosa, final float sina, final Vec2 dim ) {

      final float wSrcf = wSrc;
      final float hSrcf = hSrc;
      final float absCosa = Utils.abs(cosa);
      final float absSina = Utils.abs(sina);

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

      for ( int i = 0; i < trgLen; ++i ) {
         final float ySgn = i / wTrg - yTrgCenter;
         final float xSgn = i % wTrg - xTrgCenter;
         target[i] = Pixels.sampleBilinear(source, wSrc, hSrc, xSrcCenter + cosa
            * xSgn - sina * ySgn, ySrcCenter + cosa * ySgn + sina * xSgn);
      }

      if ( dim != null ) { dim.set(wTrgf, hTrgf); }
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
    * @see Utils#mod(int, int)
    * @see Utils#round(float)
    */
   public static int[] rotateBilinear ( final int[] source, final int wSrc,
      final int hSrc, final float angle, final Vec2 dim ) {

      final int srcLen = source.length;
      final int deg = Utils.mod(Utils.round(angle * IUtils.RAD_TO_DEG), 360);
      switch ( deg ) {
         case 0:
            if ( dim != null ) { dim.set(wSrc, hSrc); }
            final int[] target = new int[srcLen];
            System.arraycopy(source, 0, target, 0, srcLen);
            return target;

         case 90:
            if ( dim != null ) { dim.set(hSrc, wSrc); }
            return Pixels.rotate90(source, wSrc, hSrc, new int[srcLen]);

         case 180:
            if ( dim != null ) { dim.set(wSrc, hSrc); }
            return Pixels.rotate180(source, new int[srcLen]);

         case 270:
            if ( dim != null ) { dim.set(hSrc, wSrc); }
            return Pixels.rotate270(source, wSrc, hSrc, new int[srcLen]);

         default:
            final double avd = angle;
            final float cosa = ( float ) Math.cos(avd);
            final float sina = ( float ) Math.sin(avd);
            return Pixels.rotateBilinear(source, wSrc, hSrc, cosa, sina, dim);
      }
   }

   /**
    * Internal helper function to sample a source image with a bilinear color
    * mix. Returns a hexadecimal integer color.
    *
    * @param source the source pixel array
    * @param wSrc   the source image width
    * @param hSrc   the source image height
    * @param xSrc   the source x coordinate
    * @param ySrc   the source y coordinate
    *
    * @return the color
    */
   public static int sampleBilinear ( final int[] source, final int wSrc,
      final int hSrc, final float xSrc, final float ySrc ) {

      /*
       * Find truncation, floor and ceiling. The ceiling cannot use yc = yf + 1
       * as a shortcut due to the case where y = 0.
       */
      final int yi = ( int ) ySrc;
      final int yf = ySrc > 0.0f ? yi : ySrc < -0.0f ? yi - 1 : 0;
      final int yc = ySrc > 0.0f ? yi + 1 : ySrc < -0.0f ? yi : 0;

      final boolean yfInBounds = yf >= 0 && yf < hSrc;
      final boolean ycInBounds = yc >= 0 && yc < hSrc;

      final int xi = ( int ) xSrc;
      final int xf = xSrc > 0.0f ? xi : xSrc < -0.0f ? xi - 1 : 0;
      final int xc = xSrc > 0.0f ? xi + 1 : xSrc < -0.0f ? xi : 0;

      final boolean xfInBounds = xf >= 0 && xf < wSrc;
      final boolean xcInBounds = xc >= 0 && xc < wSrc;

      /* Pixel corners colors. */
      final int c00 = yfInBounds && xfInBounds ? source[yf * wSrc + xf] : 0;
      final int c10 = yfInBounds && xcInBounds ? source[yf * wSrc + xc] : 0;
      final int c11 = ycInBounds && xcInBounds ? source[yc * wSrc + xc] : 0;
      final int c01 = ycInBounds && xfInBounds ? source[yc * wSrc + xf] : 0;

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
            r0 = u * ( c00 >> 0x10 & 0xff ) + xErr * ( c10 >> 0x10 & 0xff );
            g0 = u * ( c00 >> 0x08 & 0xff ) + xErr * ( c10 >> 0x08 & 0xff );
            b0 = u * ( c00 & 0xff ) + xErr * ( c10 & 0xff );
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
            r1 = u * ( c01 >> 0x10 & 0xff ) + xErr * ( c11 >> 0x10 & 0xff );
            g1 = u * ( c01 >> 0x08 & 0xff ) + xErr * ( c11 >> 0x08 & 0xff );
            b1 = u * ( c01 & 0xff ) + xErr * ( c11 & 0xff );
         }
      }

      if ( a0 > 0.0f || a1 > 0.0f ) {
         final float yErr = ySrc - yf;
         final float u = 1.0f - yErr;
         final float a2 = u * a0 + yErr * a1;
         if ( a2 > 0.0f ) {
            final float r2 = u * r0 + yErr * r1;
            final float g2 = u * g0 + yErr * g1;
            final float b2 = u * b0 + yErr * b1;

            int ai = ( int ) ( 0.5f + a2 );
            int ri = ( int ) ( 0.5f + r2 );
            int gi = ( int ) ( 0.5f + g2 );
            int bi = ( int ) ( 0.5f + b2 );

            if ( ai > 0xff ) { ai = 0xff; }
            if ( ri > 0xff ) { ri = 0xff; }
            if ( gi > 0xff ) { gi = 0xff; }
            if ( bi > 0xff ) { bi = 0xff; }

            return ai << 0x18 | ri << 0x10 | gi << 0x08 | bi;
         }
      }

      return 0x00000000;
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
    * @see Pixels#sampleBilinear(int[], int, int, float, float)
    * @see Utils#abs(float)
    * @see Utils#mod(int, int)
    * @see Utils#round(float)
    */
   public static int[] skewXBilinear ( final int[] source, final int wSrc,
      final int hSrc, final float angle, final Vec2 dim ) {

      final float wSrcf = wSrc;
      final float hSrcf = hSrc;
      final int deg = Utils.round(angle * IUtils.RAD_TO_DEG);
      final int deg180 = Utils.mod(deg, 180);
      switch ( deg180 ) {
         case 0: {
            if ( dim != null ) { dim.set(wSrcf, hSrcf); }
            final int[] cpy = new int[source.length];
            System.arraycopy(source, 0, cpy, 0, source.length);
            return cpy;
         }

         case 88:
         case 89:
         case 90:
         case 91:
         case 92: {
            if ( dim != null ) { dim.set(wSrcf, hSrcf); }
            return new int[source.length];
         }

         default: {
            final float tana = ( float ) Math.tan(angle);
            final int wTrg = ( int ) ( 0.5f + wSrcf + Utils.abs(tana) * hSrcf );
            final float wTrgf = wTrg;
            final float yCenter = hSrcf * 0.5f;
            final float xDiff = ( wSrcf - wTrgf ) * 0.5f;

            final int trgLen = wTrg * hSrc;
            final int[] target = new int[trgLen];
            for ( int i = 0; i < trgLen; ++i ) {
               final float yTrg = i / wTrg;
               target[i] = Pixels.sampleBilinear(source, wSrc, hSrc, xDiff + i
                  % wTrg + tana * ( yTrg - yCenter ), yTrg);
            }

            if ( dim != null ) { dim.set(wTrgf, hSrcf); }
            return target;
         }
      }
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
    * @see Pixels#sampleBilinear(int[], int, int, float, float)
    * @see Utils#abs(float)
    * @see Utils#mod(int, int)
    * @see Utils#round(float)
    */
   public static int[] skewYBilinear ( final int[] source, final int wSrc,
      final int hSrc, final float angle, final Vec2 dim ) {

      final float wSrcf = wSrc;
      final float hSrcf = hSrc;
      final int deg = Utils.round(angle * IUtils.RAD_TO_DEG);
      final int deg180 = Utils.mod(deg, 180);
      switch ( deg180 ) {
         case 0: {
            if ( dim != null ) { dim.set(wSrcf, hSrcf); }
            final int[] cpy = new int[source.length];
            System.arraycopy(source, 0, cpy, 0, source.length);
            return cpy;
         }

         case 88:
         case 89:
         case 90:
         case 91:
         case 92: {
            if ( dim != null ) { dim.set(wSrcf, hSrcf); }
            return new int[source.length];
         }

         default: {
            final float tana = ( float ) Math.tan(angle);
            final int hTrg = ( int ) ( 0.5f + hSrcf + Utils.abs(tana) * wSrcf );
            final float hTrgf = hTrg;
            final float xCenter = wSrcf * 0.5f;
            final float yDiff = ( hSrcf - hTrgf ) * 0.5f;

            final int trgLen = wSrc * hTrg;
            final int[] target = new int[trgLen];
            for ( int i = 0; i < trgLen; ++i ) {
               final float xTrg = i % wSrc;
               target[i] = Pixels.sampleBilinear(source, wSrc, hSrc, xTrg, yDiff
                  + i / wSrc + tana * ( xTrg - xCenter ));
            }

            if ( dim != null ) { dim.set(wSrcf, hTrgf); }
            return target;
         }
      }
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
    * @see Rgb#fromHex(int, Rgb)
    * @see Rgb#srLab2TosRgb(Lab, Rgb, Rgb, Vec4)
    * @see Rgb#sRgbToSrLab2(Rgb, Lab, Vec4, Rgb)
    * @see Rgb#toHexIntSat(Rgb)
    * @see Utils#abs(float)
    * @see Utils#clamp(float, float, float)
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
         float lumMax = -Float.MAX_VALUE;
         float lumSum = 0.0f;

         final Rgb srgb = new Rgb();
         final Rgb lrgb = new Rgb();
         final Vec4 xyz = new Vec4();

         final HashMap < Integer, Lab > dict = new HashMap <>(512, 0.75f);

         for ( int i = 0; i < srcLen; ++i ) {
            final int hex = source[i];
            if ( ( hex & 0xff000000 ) != 0 ) {
               final Integer hexObj = hex;
               if ( !dict.containsKey(hexObj) ) {
                  final Lab lab = new Lab();
                  Rgb.fromHex(hex, srgb);
                  Rgb.sRgbToSrLab2(srgb, lab, xyz, lrgb);
                  dict.put(hexObj, lab);

                  final float lum = lab.l;
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

               final Lab stretchedLab = new Lab();
               final float tLumAvg = t * ( lumSum / dictLen );
               final float tDenom = t * ( 100.0f / diff );
               final float lumMintDenom = lumMin * tDenom;

               final HashMap < Integer, Integer > stretched = new HashMap <>(
                  512, 0.75f);
               final Set < Map.Entry < Integer, Lab > > kvs = dict.entrySet();
               for ( final Map.Entry < Integer, Lab > kv : kvs ) {
                  stretchedLab.set(kv.getValue());
                  if ( gtZero ) {
                     stretchedLab.l = u * stretchedLab.l + stretchedLab.l
                        * tDenom - lumMintDenom;
                  } else if ( ltZero ) {
                     stretchedLab.l = u * stretchedLab.l + tLumAvg;
                  }

                  Rgb.srLab2TosRgb(stretchedLab, srgb, lrgb, xyz);
                  stretched.put(kv.getKey(), Rgb.toHexIntSat(srgb));
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
    * Tints an image with a color in SR LAB 2 according to a factor. If the
    * preserveLight flag is true, the source image's original lightness is
    * retained. The image's {@link Pixels#SHADOWS}, {@link Pixels#MIDTONES}
    * and/or {@link Pixels#HIGHLIGHTS} may be targeted with an integer flag.
    *
    * @param source        the source pixels
    * @param tint          the tint color
    * @param fac           the factor
    * @param preserveLight the preserve light flag
    * @param toneFlag      the tone flags
    * @param target        the target pixels
    *
    * @return the tinted pixels
    */
   public static int[] tintLab ( final int[] source, final Lab tint,
      final float fac, final boolean preserveLight, final int toneFlag,
      final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         if ( fac <= 0.0f ) {
            System.arraycopy(source, 0, target, 0, srcLen);
            return target;
         }

         final float facVrf = fac >= 1.0f ? 1.0f : fac;
         final Function < Float, Float > response = Pixels.toneFlagToResponse(
            toneFlag);

         final Rgb srgb = new Rgb();
         final Rgb lrgb = new Rgb();
         final Vec4 xyz = new Vec4();
         final Lab srcLab = new Lab();
         final Lab mixLab = new Lab();

         final float lTint = tint.l;
         final float aTint = tint.a;
         final float bTint = tint.b;

         final HashMap < Integer, Integer > dict = new HashMap <>(512, 0.75f);
         for ( int i = 0; i < srcLen; ++i ) {
            final int srgbKeyInt = source[i];
            final Integer srgbKeyObj = srgbKeyInt;
            if ( dict.containsKey(srgbKeyObj) ) {
               target[i] = dict.get(srgbKeyObj);
            } else if ( ( srgbKeyInt & 0xff000000 ) != 0 ) {
               Rgb.fromHex(srgbKeyInt, srgb);
               Rgb.sRgbToSrLab2(srgb, srcLab, xyz, lrgb);

               final float lOrig = srcLab.l;
               final float t = facVrf * response.apply(lOrig * 0.01f);
               final float u = 1.0f - t;
               mixLab.set(preserveLight ? lOrig : u * lOrig + t * lTint, u
                  * srcLab.a + t * aTint, u * srcLab.b + t * bTint,
                  srcLab.alpha);

               Rgb.srLab2TosRgb(mixLab, srgb, lrgb, xyz);
               final int trgHex = Rgb.toHexIntSat(srgb);
               target[i] = trgHex;
               dict.put(srgbKeyObj, trgHex);
            } else {
               target[i] = 0;
               dict.put(srgbKeyObj, 0);
            }
         }
      }

      return target;
   }

   /**
    * Creates an array of materials from the non-transparent pixels of an
    * image. Intended for smaller images with relatively few colors.
    *
    * @param source the source pixels
    *
    * @return the materials
    */
   public static MaterialSolid[] toMaterials ( final int[] source ) {

      final TreeSet < Integer > uniqueColors = new TreeSet <>();
      final int srcLen = source.length;
      for ( int i = 0; i < srcLen; ++i ) { uniqueColors.add(source[i]); }

      final int uniquesLen = uniqueColors.size();
      final MaterialSolid[] result = new MaterialSolid[uniquesLen];
      final Iterator < Integer > itr = uniqueColors.iterator();
      for ( int j = 0; itr.hasNext(); ++j ) {
         final int hex = itr.next();
         final MaterialSolid material = new MaterialSolid();
         material.setFill(hex);
         material.setStroke(0x00000000);
         material.setName("Material." + Rgb.toHexString(hex));
         result[j] = material;
      }

      return result;
   }

   /**
    * Creates a mesh from the non-transparent pixels of an image. Intended for
    * smaller images with relatively few colors.
    *
    * @param source the source pixels
    * @param wSrc   the source image width
    * @param hSrc   the source image height
    * @param poly   the polygon type
    * @param target the output mesh
    *
    * @return the mesh
    *
    * @see Pixels#toMesh(ArrayList, int, float, float, float, float, float,
    *      float, PolyType, Mesh2)
    */
   public static Mesh2 toMesh ( final int[] source, final int wSrc,
      final int hSrc, final PolyType poly, final Mesh2 target ) {

      final int srcLen = source.length;
      final ArrayList < Integer > nonZeroIdcs = new ArrayList <>(srcLen);
      for ( int i = 0; i < srcLen; ++i ) {
         if ( ( source[i] & 0xff000000 ) != 0 ) { nonZeroIdcs.add(i); }
      }

      final float wf = wSrc;
      final float hf = hSrc;
      final float right = wSrc > hSrc ? 0.5f : 0.5f * ( wf / hf );
      final float top = wSrc <= hSrc ? 0.5f : 0.5f * ( hf / wf );
      return Pixels.toMesh(nonZeroIdcs, wSrc, -right, top, right, -top, 1.0f
         / wf, 1.0f / hf, poly, target);
   }

   /**
    * Creates an array of meshes from the non-transparent pixels of an image.
    * Each unique color is assigned a mesh. Intended for smaller images with
    * relatively few colors. Each mesh's {@link Mesh#materialIndex}
    * corresponds to its index in the array.
    *
    * @param source the source pixels
    * @param wSrc   the source image width
    * @param hSrc   the source image height
    * @param poly   the polygon type
    *
    * @return the meshes
    *
    * @see Pixels#toMesh(ArrayList, int, float, float, float, float, float,
    *      float, PolyType, Mesh2)
    */
   public static Mesh2[] toMeshes ( final int[] source, final int wSrc,
      final int hSrc, final PolyType poly ) {

      final int srcLen = source.length;
      final TreeMap < Integer, ArrayList < Integer > > islands
         = new TreeMap <>();
      for ( int i = 0; i < srcLen; ++i ) {
         final int srcHexInt = source[i];
         if ( ( srcHexInt & 0xff000000 ) != 0 ) {
            final Integer srcHexObj = srcHexInt;
            if ( islands.containsKey(srcHexObj) ) {
               islands.get(srcHexObj).add(i);
            } else {
               final ArrayList < Integer > indices = new ArrayList <>();
               indices.add(i);
               islands.put(srcHexObj, indices);
            }
         }
      }

      final float wf = wSrc;
      final float hf = hSrc;
      final float right = wSrc > hSrc ? 0.5f : 0.5f * ( wf / hf );
      final float top = wSrc <= hSrc ? 0.5f : 0.5f * ( hf / wf );
      final float left = -right;
      final float bottom = -top;
      final float tou = 1.0f / wf;
      final float tov = 1.0f / hf;

      final int islandCount = islands.size();
      final Mesh2[] result = new Mesh2[islandCount];

      final Iterator < Entry < Integer, ArrayList < Integer > > > itr = islands
         .entrySet().iterator();
      for ( int j = 0; itr.hasNext(); ++j ) {
         final Entry < Integer, ArrayList < Integer > > entry = itr.next();
         final Mesh2 mesh = new Mesh2("Mesh." + Rgb.toHexString(entry
            .getKey()));
         mesh.setMaterialIndex(j);
         Pixels.toMesh(entry.getValue(), wSrc, left, top, right, bottom, tou,
            tov, poly, mesh);
         result[j] = mesh;
      }

      return result;
   }

   /**
    * Transposes the source pixel array. The transposition is stored in the
    * target array.
    *
    * @param source the source pixels
    * @param w      the image width
    * @param h      the image height
    * @param target the target pixels
    *
    * @return the transposed pixels
    */
   public static int[] transpose ( final int[] source, final int w, final int h,
      final int[] target ) {

      /**
       * See https://en.wikipedia.org/wiki/In-place_matrix_transposition and
       * https://johnloomis.org/ece563/notes/geom/basic/geom.htm for notes on
       * transposing in place.
       */
      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         for ( int i = 0; i < srcLen; ++i ) {
            target[i % w * h + i / w] = source[i];
         }
      }

      return target;
   }

   /**
    * Removes excess transparent pixels from an array of pixels. Adapted from
    * the implementation by Oleg Mikhailov: <a href=
    * "https://stackoverflow.com/a/36938923">https://stackoverflow.com/a/36938923</a>.
    * <br>
    * <br>
    * Emits the new image dimensions to a {@link Vec2}.
    *
    * @param source the source pixels
    * @param wSrc   the source image width
    * @param hSrc   the source image height
    * @param dim    the new dimension
    * @param tl     top-left
    *
    * @return the trimmed pixels
    *
    * @author Oleg Mikhailov
    */
   public static int[] trimAlpha ( final int[] source, final int wSrc,
      final int hSrc, final Vec2 dim, final Vec2 tl ) {

      final int srcLen = source.length;

      if ( wSrc < 2 && hSrc < 2 ) {
         if ( dim != null ) { dim.set(wSrc, hSrc); }
         final int[] target = new int[srcLen];
         System.arraycopy(source, 0, target, 0, srcLen);
         return target;
      }

      final int wn1 = wSrc > 1 ? wSrc - 1 : 0;
      final int hn1 = hSrc > 1 ? hSrc - 1 : 0;

      int minRight = wn1;
      int minBottom = hn1;

      /* Top search. y is outer loop, x is inner loop. */
      int top = -1;
      boolean goTop = true;
      while ( goTop && top < hn1 ) {
         ++top;
         final int wtop = wSrc * top;
         int x = -1;
         while ( goTop && x < wn1 ) {
            ++x;
            if ( ( source[wtop + x] & 0xff000000 ) != 0 ) {
               minRight = x;
               minBottom = top;
               goTop = false;
            }
         }
      }

      /* Left search. x is outer loop, y is inner loop. */
      int left = -1;
      boolean goLeft = true;
      while ( goLeft && left < minRight ) {
         ++left;
         int y = hSrc;
         while ( goLeft && y > top ) {
            --y;
            if ( ( source[y * wSrc + left] & 0xff000000 ) != 0 ) {
               minBottom = y;
               goLeft = false;
            }
         }
      }

      /* Bottom search. y is outer loop, x is inner loop. */
      int bottom = hSrc;
      boolean goBottom = true;
      while ( goBottom && bottom > minBottom ) {
         --bottom;
         final int wbottom = wSrc * bottom;
         int x = wSrc;
         while ( goBottom && x > left ) {
            --x;
            if ( ( source[wbottom + x] & 0xff000000 ) != 0 ) {
               minRight = x;
               goBottom = false;
            }
         }
      }

      /* Right search. x is outer loop, y is inner loop. */
      int right = wSrc;
      boolean goRight = true;
      while ( goRight && right > minRight ) {
         --right;
         int y = bottom + 1;
         while ( goRight && y > top ) {
            --y;
            if ( ( source[y * wSrc + right] & 0xff000000 ) != 0 ) {
               goRight = false;
            }
         }
      }

      final int wTrg = 1 + right - left;
      final int hTrg = 1 + bottom - top;
      if ( wTrg < 1 || hTrg < 1 ) {
         if ( dim != null ) { Vec2.zero(dim); }
         final int[] target = new int[srcLen];
         System.arraycopy(source, 0, target, 0, srcLen);
         return target;
      }

      final int trgLen = wTrg * hTrg;
      final int[] target = new int[trgLen];
      for ( int i = 0; i < trgLen; ++i ) {
         target[i] = source[wSrc * ( top + i / wTrg ) + left + i % wTrg];
      }

      if ( dim != null ) { dim.set(wTrg, hTrg); }
      if ( tl != null ) { tl.set(left, top); }
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
            } else if ( ai < 0xff ) {
               final float af = 255.0f / ai;
               int ru = ( int ) ( ( srcHex >> 0x10 & 0xff ) * af + 0.5f );
               int gu = ( int ) ( ( srcHex >> 0x08 & 0xff ) * af + 0.5f );
               int bu = ( int ) ( ( srcHex & 0xff ) * af + 0.5f );

               if ( ru > 0xff ) { ru = 0xff; }
               if ( gu > 0xff ) { gu = 0xff; }
               if ( bu > 0xff ) { bu = 0xff; }

               target[i] = ai << 0x18 | ru << 0x10 | gu << 0x08 | bu;
            } else {
               target[i] = srcHex;
            }
         }
      }

      return target;
   }

   /**
    * Blits a source image's pixels onto a target image's pixels, using
    * integer floor modulo to wrap the source image. The source image can be
    * offset horizontally and/or vertically, creating the illusion of infinite
    * background.
    *
    * @param source the source pixels
    * @param wSrc   the source image width
    * @param hSrc   the source image height
    * @param dx     the horizontal pixel offset
    * @param dy     the vertical pixel offset
    * @param wTrg   the target image width
    * @param target the target pixels
    *
    * @return the wrapped pixels
    */
   public static int[] wrap ( final int[] source, final int wSrc,
      final int hSrc, final int dx, final int dy, final int wTrg,
      final int[] target ) {

      final int trgLen = target.length;
      for ( int i = 0; i < trgLen; ++i ) {
         int yMod = ( i / wTrg + dy ) % hSrc;
         if ( ( yMod ^ hSrc ) < 0 && yMod != 0 ) { yMod += hSrc; }

         int xMod = ( i % wTrg - dx ) % wSrc;
         if ( ( xMod ^ wSrc ) < 0 && xMod != 0 ) { xMod += wSrc; }

         target[i] = source[xMod + wSrc * yMod];
      }

      return target;
   }

   /**
    * Internal helper function to filter an array of pixels into a dictionary.
    * The color serves as the key. The value is a list of indices that
    * reference the source array. The function delegate is expected to filter
    * the color according to an upper and lower bound. Both bounds should be
    * inclusive.
    *
    * @param source the source pixels
    * @param lb     the lower bound
    * @param ub     the upper bound
    * @param f      the filter function
    * @param dict   the target dictionary
    *
    * @return the dictionary
    */
   protected static HashMap < Integer, ArrayList < Integer > > filter (
      final int[] source, final float lb, final float ub, final TriFunction <
         Integer, Float, Float, Boolean > f, final HashMap < Integer,
            ArrayList < Integer > > dict ) {

      final Float lbObj = lb;
      final Float ubObj = ub;

      final HashSet < Integer > visited = new HashSet <>(512, 0.75f);
      final HashMap < Integer, Boolean > included = new HashMap <>(512, 0.75f);

      final int srcLen = source.length;
      for ( int i = 0; i < srcLen; ++i ) {
         final int cInt = source[i];
         final Integer cObj = cInt;
         boolean include = false;
         if ( visited.contains(cObj) ) {
            include = included.get(cObj);
         } else {
            include = f.apply(cObj, lbObj, ubObj);
            if ( include ) { dict.put(cObj, new ArrayList <>(32)); }
            visited.add(cObj);
            included.put(cObj, include);
         }

         if ( include ) { dict.get(cObj).add(i); }
      }

      return dict;
   }

   /**
    * Internal helper method to create a mesh from a list of indices and other
    * conversion data. Makes no optimizations to the mesh by, e.g., removing
    * interior or colinear vertices.
    *
    * @param indices the non-zero image pixel indices
    * @param wSrc    the image width
    * @param left    the left edge
    * @param top     the top edge
    * @param right   the right edge
    * @param bottom  the bottom edge
    * @param tou     width to uv conversion
    * @param tov     height to uv conversion
    * @param poly    polygon type
    * @param target  the output mesh
    *
    * @return the mesh
    */
   protected static Mesh2 toMesh ( final ArrayList < Integer > indices,
      final int wSrc, final float left, final float top, final float right,
      final float bottom, final float tou, final float tov, final PolyType poly,
      final Mesh2 target ) {

      final int idcsLen = indices.size();
      final int vsLen = idcsLen * 4;
      final Vec2[] vs = target.coords = Vec2.resize(target.coords, vsLen);
      final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords,
         vsLen);

      for ( int i = 0, j00 = 0; i < idcsLen; ++i, j00 += 4 ) {
         final int j10 = j00 + 1;
         final int j11 = j00 + 2;
         final int j01 = j00 + 3;

         final int idx = indices.get(i);
         final float x = idx % wSrc;
         final float y = idx / wSrc;

         final float u0 = x * tou;
         final float v0 = y * tov;
         final float u1 = ( x + 1.0f ) * tou;
         final float v1 = ( y + 1.0f ) * tov;

         vts[j00].set(u0, v0);
         vts[j10].set(u1, v0);
         vts[j11].set(u1, v1);
         vts[j01].set(u0, v1);

         final float x0 = ( 1.0f - u0 ) * left + u0 * right;
         final float y0 = ( 1.0f - v0 ) * top + v0 * bottom;
         final float x1 = ( 1.0f - u1 ) * left + u1 * right;
         final float y1 = ( 1.0f - v1 ) * top + v1 * bottom;

         vs[j00].set(x0, y0);
         vs[j10].set(x1, y0);
         vs[j11].set(x1, y1);
         vs[j01].set(x0, y1);
      }

      int[][][] fs;
      switch ( poly ) {
         case TRI: {
            fs = target.faces = new int[idcsLen + idcsLen][3][2];
            for ( int i = idcsLen - 1, j00 = vsLen - 1; i > -1; --i, j00
               -= 4 ) {
               final int j10 = j00 - 1;
               final int j11 = j00 - 2;
               final int j01 = j00 - 3;

               final int[][] f1 = fs[i + i];
               final int[] vr10 = f1[0];
               vr10[0] = j11;
               vr10[1] = j11;

               final int[] vr11 = f1[1];
               vr11[0] = j01;
               vr11[1] = j01;

               final int[] vr12 = f1[2];
               vr12[0] = j00;
               vr12[1] = j00;

               final int[][] f0 = fs[i + i + 1];
               final int[] vr00 = f0[0];
               vr00[0] = j00;
               vr00[1] = j00;

               final int[] vr01 = f0[1];
               vr01[0] = j10;
               vr01[1] = j10;

               final int[] vr02 = f0[2];
               vr02[0] = j11;
               vr02[1] = j11;
            }
         }
            break;

         case NGON:
         case QUAD:
         default: {
            fs = target.faces = new int[idcsLen][4][2];
            for ( int i = idcsLen - 1, j00 = vsLen - 1; i > -1; --i, j00
               -= 4 ) {
               final int j10 = j00 - 1;
               final int j11 = j00 - 2;
               final int j01 = j00 - 3;

               final int[][] f = fs[i];
               final int[] vr00 = f[0];
               vr00[0] = j00;
               vr00[1] = j00;

               final int[] vr10 = f[1];
               vr10[0] = j10;
               vr10[1] = j10;

               final int[] vr11 = f[2];
               vr11[0] = j11;
               vr11[1] = j11;

               final int[] vr01 = f[3];
               vr01[0] = j01;
               vr01[1] = j01;
            }
         }
      }

      return target;
   }

   /**
    * Converts an integer flag indicating which portion of an image to target
    * into a response function.
    *
    * @param toneFlag the tone flag
    *
    * @return the response function
    */
   protected static Function < Float, Float > toneFlagToResponse (
      final int toneFlag ) {

      switch ( toneFlag ) {

         case Pixels.SHADOWS:
            return new Function <>() {
               @Override
               public Float apply ( final Float x ) {

                  if ( x <= 0.0f ) { return 1.0f; }
                  if ( x >= 0.5f ) { return 0.0f; }
                  final float y = 1.0f - 2.0f * x;
                  return y * y * ( 3.0f - 2.0f * y );
               }

            };

         case Pixels.MIDTONES:
            return new Function <>() {
               @Override
               public Float apply ( final Float x ) {

                  if ( x <= 0.0f || x >= 1.0f ) { return 0.0f; }
                  final float y = Utils.abs(2.0f * x - 1.0f);
                  return 1.0f - y * y * ( 3.0f - 2.0f * y );
               }

            };

         case Pixels.HIGHLIGHTS:
            return new Function <>() {
               @Override
               public Float apply ( final Float x ) {

                  if ( x <= 0.5f ) { return 0.0f; }
                  if ( x >= 1.0f ) { return 1.0f; }
                  final float y = 2.0f * x - 1.0f;
                  return y * y * ( 3.0f - 2.0f * y );
               }

            };

         case Pixels.SHADOWS | Pixels.MIDTONES:
            return new Function <>() {
               @Override
               public Float apply ( final Float x ) {

                  if ( x <= 0.0f ) { return 1.0f; }
                  if ( x >= 0.75f ) { return 0.0f; }
                  final float y = 1.0f - IUtils.FOUR_THIRDS * x;
                  return y * y * ( 3.0f - 2.0f * y );
               }

            };

         case Pixels.SHADOWS | Pixels.HIGHLIGHTS:
            return new Function <>() {
               @Override
               public Float apply ( final Float x ) {

                  if ( x <= 0.0f || x >= 1.0f ) { return 1.0f; }
                  final float y = Utils.abs(1.0f - 2.0f * x);
                  return y * y * ( 3.0f - 2.0f * y );
               }

            };

         case Pixels.MIDTONES | Pixels.HIGHLIGHTS:
            return new Function <>() {
               @Override
               public Float apply ( final Float x ) {

                  if ( x <= 0.25f ) { return 0.0f; }
                  if ( x >= 1.0f ) { return 1.0f; }
                  final float y = IUtils.FOUR_THIRDS * x - IUtils.ONE_THIRD;
                  return y * y * ( 3.0f - 2.0f * y );
               }

            };

         case Pixels.SHADOWS | Pixels.MIDTONES | Pixels.HIGHLIGHTS:
            return new Function <>() {
               @Override
               public Float apply ( final Float x ) {

                  if ( x <= 0.0f ) { return 0.0f; }
                  if ( x >= 1.0f ) { return 1.0f; }
                  return x * x * ( 3.0f - 2.0f * x );
               }

            };

         default:
            return new Function <>() {
               @Override
               public Float apply ( final Float x ) { return 1.0f; }

            };
      }
   }

   /**
    * Finds the relative luminance of a 32-bit ARGB integer. Does not check
    * the input color's alpha.
    *
    * @param c the color
    *
    * @return the luminance
    */
   private static float srgbLuminance ( final int c ) {

      final double sr = ( c >> 0x10 & 0xff ) / 255.0d;
      final double sg = ( c >> 0x08 & 0xff ) / 255.0d;
      final double sb = ( c >> 0x00 & 0xff ) / 255.0d;

      final double lr = sr <= 0.04045d ? sr / 12.92d : Math.pow( ( sr + 0.055d )
         / 1.055d, 2.4d);
      final double lg = sg <= 0.04045d ? sg / 12.92d : Math.pow( ( sg + 0.055d )
         / 1.055d, 2.4d);
      final double lb = sb <= 0.04045d ? sb / 12.92d : Math.pow( ( sb + 0.055d )
         / 1.055d, 2.4d);

      final double y = 0.2126d * lr + 0.7152d * lg + 0.0722d * lb;
      return ( float ) ( y <= 0.0031308d ? 12.92d * y : Math.pow(1.055d * y,
         1.0d / 2.4d) - 0.055d );
   }

   /**
    * Evaluates whether a color should be included according to a lower and
    * upper bound, both inclusive. Luminance is expected to be in the range
    * [0.0, 1.0].
    */
   public static class FilterLuminance implements TriFunction < Integer, Float,
      Float, Boolean > {

      /**
       * The default constructor.
       */
      public FilterLuminance ( ) {}

      /**
       * Evaluates whether a color is in bounds.
       *
       * @param c  the color
       * @param lb the lower bounds
       * @param ub the upper bounds
       *
       * @return the evaluation
       *
       * @see Pixels#srgbLuminance(int)
       */
      @Override
      public Boolean apply ( final Integer c, final Float lb, final Float ub ) {

         final float v = Pixels.srgbLuminance(c);
         return v >= lb && v <= ub;

      }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * Converts a color as a 32 bit integer to a factor to be supplied to a
    * gradient evaluation. Uses luminance to determine the factor.
    */
   public static class MapLuminance implements IntFunction < Float > {

      /**
       * The default constructor.
       */
      public MapLuminance ( ) {}

      /**
       * Evaluates a color's luminance.
       *
       * @param hex the hexadecimal color
       *
       * @return the factor
       *
       * @see Pixels#srgbLuminance(int)
       */
      @Override
      public Float apply ( final int hex ) {

         return Pixels.srgbLuminance(hex);
      }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

}
