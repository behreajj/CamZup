package camzup;

import java.util.HashMap;

import camzup.core.Color;
import camzup.core.IUtils;
import camzup.core.Utils;
import camzup.core.Vec4;

import processing.core.PApplet;
import processing.core.PImage;

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

   public static float floorModExact ( final float a, final float b ) {

      float m = a % b;
      if ( m > 0 ? b < 0 : m < 0 && b > 0 ) { m += b; }
      return m;
   }

   /**
    * The main function.
    *
    * @param args the string of arguments
    */

   public static void main ( final String[] args ) {

   }

   public static PImage stretchContrast ( final PImage target,
      final float fac ) {

      target.loadPixels();
      final int[] px = target.pixels;
      final int len = px.length;

      float lumMin = Float.MAX_VALUE;
      float lumMax = Float.MIN_VALUE;
      float lumSum = 0.0f;

      final Color srgb = new Color();
      final Color lrgb = new Color();
      final Vec4 xyz = new Vec4();

      final HashMap < Integer, Vec4 > dictionary = new HashMap <>();

      for ( int i = 0; i < len; ++i ) {
         final int hex = px[i];
         if ( ( hex & 0xff000000 ) != 0 ) {
            final Integer hexObj = hex;
            if ( !dictionary.containsKey(hexObj) ) {
               final Vec4 lab = new Vec4();

               Color.fromHex(hex, srgb);
               Color.sRgbaTolRgba(srgb, false, lrgb);
               Color.lRgbaToXyza(lrgb, xyz);
               Color.xyzaToLaba(xyz, lab);

               dictionary.put(hexObj, lab);

               final float lum = lab.z;
               if ( lum < lumMin ) { lumMin = lum; }
               if ( lum > lumMax ) { lumMax = lum; }
               lumSum += lum;
            }
         }
      }

      final int dictLen = dictionary.size();
      final float diff = Utils.abs(lumMax - lumMin);
      if ( dictLen > 0 && diff > IUtils.EPSILON ) {
         final float lumAvg = lumSum / dictLen;
         final float valFac = Utils.clamp(fac, -1.0f, 1.0f);
         final float t = Utils.abs(valFac);
         final float u = 1.0f - t;

         final Vec4 stretchedLab = new Vec4();
         final float denom = 100.0f / diff;
         final float tDenom = t * denom;
         final float lumMintDenom = lumMin * tDenom;
         for ( int i = 0; i < len; ++i ) {
            final int hex = px[i];
            final Integer hexObj = hex;
            if ( dictionary.containsKey(hexObj) ) {
               final Vec4 lab = dictionary.get(hexObj);
               stretchedLab.set(lab);

               if ( valFac > 0.0f ) {
                  // final float extreme = ( stretchedLab.z - lumMin ) * denom;
                  // stretchedLab.z = u * stretchedLab.z + ( stretchedLab.z
                  // - lumMin ) * tdenom;
                  stretchedLab.z = u * stretchedLab.z + stretchedLab.z * tDenom
                     - lumMintDenom;
               } else if ( valFac < -0.0f ) {
                  stretchedLab.z = u * stretchedLab.z + t * lumAvg;
               }

               Color.labaToXyza(stretchedLab, xyz);
               Color.xyzaTolRgba(xyz, lrgb);
               Color.lRgbaTosRgba(lrgb, false, srgb);
               Color.clamp01(srgb, srgb);
               px[i] = Color.toHexInt(srgb);
            } else {
               px[i] = 0x0;
            }
         }

         target.updatePixels();
      }
      return target;
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
