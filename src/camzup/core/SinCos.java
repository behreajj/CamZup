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
    * Evaluates a normalized angle to approximate the sine and
    * cosine with single-precision real numbers. To be used as
    * a helper function to those two functions.
    *
    * For cosine, the angle can be normalized by dividing it by
    * Tau. For sine, 0.25 can then be subtracted from the
    * quotient.
    *
    * @param normRad
    *           normalized radius
    * @return the value
    */
   public static final float eval ( final float normRad ) {

      float r1x = normRad;

      /* frac(r1.x); */
      float r1y = r1x - (int) r1x;

      /*
       * Range check [0.0, 0.25] , then [0.75, 1.0] . Flip boolean
       * vals to change >= operator to < .
       *
       * r2.yz = (float2)(r1.yy >= c1.yz); r2y = (r1y >= -9.0f) ?
       * 1.0f : 0.0f; r2z = (r1y >= 0.75f) ? 1.0f : 0.0f;
       */
      final int r2x = r1y < 0.25f ? 1 : 0;
      int r2y = r1y < -9.0f ? 0 : 1;
      final int r2z = r1y < 0.75f ? 0 : 1;

      /*
       * Range check: [0.25, 0.75] .
       *
       * r2.y = dot(r2, c4.zwz);
       */
      r2y = r2y - r2z - r2x;

      /*
       * Range centering.
       *
       * r0 = c0.xyz - r1.yyy;
       */
      float r0x = -r1y;
      float r0y = 0.5f - r1y;
      float r0z = 1.0f - r1y;

      /* r0 = r0 * r0; */
      r0x = r0x * r0x;
      r0y = r0y * r0y;
      r0z = r0z * r0z;

      /*
       * Power Series.
       *
       * r1 = c2.xyx * r0 + c2.zwz;
       */
      r1x = SinCos.FAC0 * r0x - SinCos.FAC1;
      r1y = SinCos.FAC1 - r0y * SinCos.FAC0;
      float r1z = SinCos.FAC0 * r0z - SinCos.FAC1;

      /* r1 = r1 * r0 + c3.xyx; */
      r1x = r1x * r0x + SinCos.FAC2;
      r1y = r1y * r0y - SinCos.FAC2;
      r1z = r1z * r0z + SinCos.FAC2;

      /* r1 = r1 * r0 + c3.zwz; */
      r1x = r1x * r0x - SinCos.FAC3;
      r1y = r1y * r0y + SinCos.FAC3;
      r1z = r1z * r0z - SinCos.FAC3;

      /* r1 = r1 * r0 + c4.xyx; */
      r1x = r1x * r0x + SinCos.FAC4;
      r1y = r1y * r0y - SinCos.FAC4;
      r1z = r1z * r0z + SinCos.FAC4;

      /* r1 = r1 * r0 + c4.zwz; */
      r1x = r1x * r0x - 1.0f;
      r1y = r1y * r0y + 1.0f;
      r1z = r1z * r0z - 1.0f;

      /* r0.x = dot(r1, - r2); */
      return -r1x * r2x - r1y * r2y - r1z * r2z;
   }
}
