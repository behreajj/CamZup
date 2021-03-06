package camzup.pfriendly;

import processing.opengl.PGL;

/**
 * Maintains consistent behavior across OpenGL renderers, even when they
 * are of different dimensions.
 */
public interface IUpOgl extends IUp {

   /**
    * Returns whether or not the renderer is 2D.
    *
    * @return the evaluation
    */
   boolean is2D ( );

   /**
    * Returns whether or not the renderer is 3D.
    *
    * @return the evaluation
    */
   boolean is3D ( );

   /**
    * Gets the renderer's texture sampling.
    *
    * @return the texture sampling
    */
   TextureSampling textureSampling ( );

   /**
    * Sets the renderer's texture sampling.
    *
    * @param sampleType the sampling type
    */
   void textureSampling ( TextureSampling sampleType );

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
    * Duplicate of {@link PGL#MAX_LIGHTS}. Maximum lights by default is 8, the
    * minimum defined by OpenGL.
    */
   int MAX_LIGHTS = 8;

   /**
    * Flag to indicate the blend mode should be set to the old Processing
    * OpenGL blend mode for the purposes of displaying text.
    */
   int TEXT_BLEND = 9999;

}
