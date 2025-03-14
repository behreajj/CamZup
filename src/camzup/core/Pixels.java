package camzup.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

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

}
