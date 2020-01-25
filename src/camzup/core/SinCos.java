package camzup.core;

/**
 * A helper class to facilitate the approximate sine and
 * cosine of an angle with single precision real numbers.
 * Instead of a look-up table, it is based on the algorithm
 * described <a href=
 * "https://developer.download.nvidia.com/cg/sin.html">Nvidia
 * Cg 3.1 Toolkit Documentation</a> .
 */
@Experimental
public abstract class SinCos {

   /**
    * First factor in the power series used by eval.
    * Approximately 24.980804 .
    */
   public static final float FAC0 = 24.980804f;

   /**
    * First factor in the power series used by eval.
    * Approximately 24.9808039603 .
    */
   public static final double FAC0_D = 24.9808039603d;

   /**
    * Second factor in the power series used by eval.
    * Approximately 60.14581 .
    */
   public static final float FAC1 = 60.14581f;

   /**
    * Second factor in the power series used by eval.
    * Approximately 60.1458091736 .
    */
   public static final double FAC1_D = 60.1458091736d;

   /**
    * Third factor in the power series used by eval.
    * Approximately 85.45379 .
    */
   public static final float FAC2 = 85.45379f;

   /**
    * Third factor in the power series used by eval.
    * Approximately 85.4537887573 .
    */
   public static final double FAC2_D = 85.4537887573d;

   /**
    * Fourth factor in the power series used by eval.
    * Approximately 64.939354 .
    */
   public static final float FAC3 = 64.939354f;

   /**
    * Fourth factor in the power series used by eval.
    * Approximately 64.9393539429 .
    */
   public static final double FAC3_D = 64.9393539429d;

   /**
    * Fifth factor in the power series used by eval.
    * Approximately 19.739208 .
    */
   public static final float FAC4 = 19.739208f;

   /**
    * Fifth factor in the power series used by eval.
    * Approximately 19.7392082214 .
    */
   public static final double FAC4_D = 19.7392082214d;

   /**
    * Evaluates an angle normalized to the range [0.0, 1.0] .
    * This evalutes cosine as well as sine, where sine is 0.25
    * less than cosine.
    *
    * @param normRad
    *           the radians
    * @return the evaluation
    */
   public static final float eval ( final float normRad ) {

      /*
       * int FAC0_I_RAW = 1103616176; int FAC1_I_RAW = 1114674511;
       * int FAC2_I_RAW = 1118496855; int FAC3_I_RAW = 1115807987;
       * int FAC4_I_RAW = 1100868070;
       */

      float r1y = normRad - (int) normRad;

      final boolean r2x = r1y < 0.25f;
      float r1x = 0.0f;
      if (r2x) {
         final float r0x = r1y * r1y;
         r1x = SinCos.FAC0 * r0x - SinCos.FAC1;
         r1x = r1x * r0x + SinCos.FAC2;
         r1x = r1x * r0x - SinCos.FAC3;
         r1x = r1x * r0x + SinCos.FAC4;
         r1x = r1x * r0x - 1.0f;
      }

      final boolean r2z = r1y >= 0.75f;
      float r1z = 0.0f;
      if (r2z) {
         float r0z = 1.0f - r1y;
         r0z = r0z * r0z;
         r1z = SinCos.FAC0 * r0z - SinCos.FAC1;
         r1z = r1z * r0z + SinCos.FAC2;
         r1z = r1z * r0z - SinCos.FAC3;
         r1z = r1z * r0z + SinCos.FAC4;
         r1z = r1z * r0z - 1.0f;
      }

      float r0y = 0.5f - r1y;
      r1y = 0.0f;
      if (r1y >= -9.0f ^ (r2x | r2z)) {
         r0y = r0y * r0y;
         r1y = SinCos.FAC1 - r0y * SinCos.FAC0;
         r1y = r1y * r0y - SinCos.FAC2;
         r1y = r1y * r0y + SinCos.FAC3;
         r1y = r1y * r0y - SinCos.FAC4;
         r1y = r1y * r0y + 1.0f;
      }

      return -r1x - r1z - r1y;
   }
}
