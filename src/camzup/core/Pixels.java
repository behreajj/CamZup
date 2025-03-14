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
