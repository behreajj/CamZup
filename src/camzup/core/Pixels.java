package camzup.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

import camzup.core.Utils.TriFunction;

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
            target[i] = srgb.toHexIntSat();
         }
      }
      return target;
   }

   /**
    * Appends to an array of bytes, ordered from least to most significant
    * digit (little endian).
    *
    * @param source the pixels array
    * @param target the byte array
    * @param i      the index
    *
    * @return the byte array
    *
    * @see Utils#byteslm(float, byte[], int)
    */
   public static byte[] byteslm ( final int[] source, final byte[] target,
      final int i ) {

      final int len = source.length;
      for ( int j = 0; j < len; ++j ) {
         Utils.byteslm(source[j], target, j * 4 + i);
      }
      return target;
   }

   /**
    * Appends to an array of bytes, ordered from most to least significant
    * digit (big endian).
    *
    * @param source the pixels array
    * @param target the byte array
    * @param i      the index
    *
    * @return the byte array
    *
    * @see Utils#bytesml(float, byte[], int)
    */
   public static byte[] bytesml ( final int[] source, final byte[] target,
      final int i ) {

      final int len = source.length;
      for ( int j = 0; j < len; ++j ) {
         Utils.bytesml(source[j], target, j * 4 + i);
      }
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
               if ( ays >= 0 && ays < ah && axs >= 0 && axs < aw ) {
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
               lookup.put(point, clrPal.toHexIntSat());
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

      float t0 = 0.0f;
      float r0 = 0.0f;
      float g0 = 0.0f;
      float b0 = 0.0f;

      final int a00 = c00 >> 0x18 & 0xff;
      final int a10 = c10 >> 0x18 & 0xff;
      if ( a00 > 0 || a10 > 0 ) {
         final float u = 1.0f - xErr;
         t0 = u * a00 + xErr * a10;
         if ( t0 > 0.0f ) {
            r0 = u * ( c00 >> 0x10 & 0xff ) + xErr * ( c10 >> 0x10 & 0xff );
            g0 = u * ( c00 >> 0x08 & 0xff ) + xErr * ( c10 >> 0x08 & 0xff );
            b0 = u * ( c00 & 0xff ) + xErr * ( c10 & 0xff );
         }
      }

      float t1 = 0.0f;
      float r1 = 0.0f;
      float g1 = 0.0f;
      float b1 = 0.0f;

      final int a01 = c01 >> 0x18 & 0xff;
      final int a11 = c11 >> 0x18 & 0xff;
      if ( a01 > 0 || a11 > 0 ) {
         final float u = 1.0f - xErr;
         t1 = u * a01 + xErr * a11;
         if ( t1 > 0.0f ) {
            r1 = u * ( c01 >> 0x10 & 0xff ) + xErr * ( c11 >> 0x10 & 0xff );
            g1 = u * ( c01 >> 0x08 & 0xff ) + xErr * ( c11 >> 0x08 & 0xff );
            b1 = u * ( c01 & 0xff ) + xErr * ( c11 & 0xff );
         }
      }

      if ( t0 > 0.0f || t1 > 0.0f ) {
         final float yErr = ySrc - yf;
         final float u = 1.0f - yErr;
         final float t2 = u * t0 + yErr * t1;
         if ( t2 > 0.0f ) {
            final float r2 = u * r0 + yErr * r1;
            final float g2 = u * g0 + yErr * g1;
            final float b2 = u * b0 + yErr * b1;

            int ti = ( int ) ( 0.5f + t2 );
            int ri = ( int ) ( 0.5f + r2 );
            int gi = ( int ) ( 0.5f + g2 );
            int bi = ( int ) ( 0.5f + b2 );

            if ( ti > 0xff ) { ti = 0xff; }
            if ( ri > 0xff ) { ri = 0xff; }
            if ( gi > 0xff ) { gi = 0xff; }
            if ( bi > 0xff ) { bi = 0xff; }

            return ti << 0x18 | ri << 0x10 | gi << 0x08 | bi;
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

      // TODO: Transfer these to Img next.

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
               final int trgHex = srgb.toHexIntSat();
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

}
