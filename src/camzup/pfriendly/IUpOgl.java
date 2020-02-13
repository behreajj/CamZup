package camzup.pfriendly;

/**
 * Maintains consistent behavior across OpenGL renderers,
 * even when they are of different dimensions.
 */
public interface IUpOgl extends IUp {

   /**
    * An enumeration to make the texture sampling options
    * available in Processing's OpenGL renderers (P2D and P3D)
    * clearer. The integer value of each is intended to match
    * with int constants in Processing's Texture class. The
    * enum's ordinal should not be used.
    *
    * @see processing.opengl.Texture
    */
   public enum Sampling {

      /**
       * Bilinear sampling.
       */
      BILINEAR ( 4 ),

      /**
       * Linear sampling. Magnification filtering is nearest,
       * minification set to linear
       */
      LINEAR ( 3 ),

      /**
       * Point sampling. Magnification and minification filtering
       * are set to nearest
       */
      POINT ( 2 ),

      /**
       * Trilinear sampling.
       */
      TRILINEAR ( 5 );

      /**
       * Gets a sampling constant from an integer value.
       *
       * @param i
       *           the integer
       * @return the constant
       */
      public static Sampling fromValue ( final int i ) {

         switch (i) {
            case 5:
               return TRILINEAR;
            case 4:
               return BILINEAR;
            case 3:
               return LINEAR;
            case 2:
            default:
               return POINT;
         }
      }

      /**
       * The integer code of the constant.
       */
      private final int val;

      /**
       * The enum constructor.
       *
       * @param val
       *           the integer value
       */
      private Sampling ( final int val ) {

         this.val = val;
      }

      /**
       * Gets the integer code of the constant.
       *
       * @return the integer
       */
      int getVal () {

         return this.val;
      }
   }

   /**
    * Default ambient light blue channel.
    */
   float DEFAULT_AMB_B = 0.2f;

   /**
    * Default ambient light green channel.
    */
   float DEFAULT_AMB_G = 0.15f;

   /**
    * Default ambient light red channel.
    */
   float DEFAULT_AMB_R = 0.125f;

   /**
    * Default directional light blue channel.
    */
   float DEFAULT_LIGHT_B = 0.843137f;

   /**
    * Default directional light green channel.
    */
   float DEFAULT_LIGHT_G = 0.960784f;

   /**
    * Default directional light red channel.
    */
   float DEFAULT_LIGHT_R = 1.0f;

   /**
    * Default specular light blue channel.
    */
   float DEFAULT_SPEC_B = 0.375f;

   /**
    * Default specular light green channel.
    */
   float DEFAULT_SPEC_G = 0.375f;

   /**
    * Default specular light red channel.
    */
   float DEFAULT_SPEC_R = 0.375f;

   /**
    * Returns whether or not the renderer is 2D.
    *
    * @return the evaluation
    */
   boolean is2D ();

   /**
    * Returns whether or not the renderer is 3D.
    *
    * @return the evaluation
    */
   boolean is3D ();

   /**
    * Draws the world origin.
    */
   @Override
   void origin ();

   /**
    * Gets the renderer's texture sampling.
    *
    * @return the texture sampling
    */
   Sampling textureSampling ();

   /**
    * Sets the renderer's texture sampling.
    *
    * @param sampleType
    *           the sampling type
    */
   void textureSampling ( Sampling sampleType );
}
