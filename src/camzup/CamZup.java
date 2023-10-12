package camzup;

import java.util.Random;

import camzup.core.Lab;
import camzup.core.Lch;
import camzup.core.Rgb;
import camzup.core.Vec4;
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

   /**
    * The main function.
    *
    * @param args the string of arguments
    */

   public static void main ( final String[] args ) {

      final Lab oLab = Lab.random(new Random(), new Lab());
      final Lch oLch = Lch.fromLab(oLab, new Lch());
      final Lab[] labHarms = Lab.harmonyTriadic(oLab);
      final Lch[] lchHarms = Lch.harmonyTriadic(oLch);

      for ( int i = 0; i < labHarms.length; ++i ) {
         final Lab labHarm = labHarms[i];
         final Lch lchHarm = lchHarms[i];

         final Rgb fromLab = Rgb.srLab2TosRgb(labHarm, new Rgb(), new Rgb(),
            new Vec4());
         final Rgb fromLch = Rgb.srLchTosRgb(lchHarm, new Rgb(), new Rgb(),
            new Vec4(), new Lab());

         System.out.println(fromLab);
         System.out.println(fromLch);
         System.out.println(fromLab.equals(fromLch));
      }

      // float aMin = Float.MAX_VALUE;
      // float bMin = Float.MAX_VALUE;
      // float cMin = Float.MAX_VALUE;
      //
      // float aMax = Float.MIN_VALUE;
      // float bMax = Float.MIN_VALUE;
      // float cMax = Float.MIN_VALUE;
      //
      // final int len = 256 * 256 * 256;
      // for ( int m = 0, i = 0; i < 256; ++i ) {
      // final float b = i / 255.0f;
      // for ( int j = 0; j < 256; ++j ) {
      // final float g = j / 255.0f;
      // for ( int k = 0; k < 256; ++k, ++m ) {
      // final float r = k / 255.0f;
      // final Rgb c = new Rgb(r, g, b, 1.0f);
      // final Lab lab = Rgb.sRgbToSrLab2(c, new Lab(), new Vec4(),
      // new Rgb());
      // final Lch lch = Lch.fromLab(lab, new Lch());
      //
      // if ( lab.a < aMin ) aMin = lab.a;
      // if ( lab.a > aMax ) aMax = lab.a;
      // if ( lab.b < bMin ) bMin = lab.b;
      // if ( lab.b > bMax ) bMax = lab.b;
      // if ( lch.c > 0 && lch.c < cMin ) cMin = lch.c;
      // if ( lch.c > cMax ) cMax = lch.c;
      // }
      // }
      // }
      //
      // System.out.println(aMin);
      // System.out.println(aMax);
      // System.out.println(bMin);
      // System.out.println(bMax);
      // System.out.println(cMin);
      // System.out.println(cMax);
   }

   /**
    * Gets the version of the library.
    *
    * @return the version
    */
   public static String version ( ) { return CamZup.VERSION; }

}
