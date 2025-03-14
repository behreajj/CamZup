package camzup.core;

import java.util.Iterator;
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

}
