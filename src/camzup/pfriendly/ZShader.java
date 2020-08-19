package camzup.pfriendly;

import java.net.URL;

import camzup.core.Color;
import camzup.core.Mat3;
import camzup.core.Mat4;
import camzup.core.Quaternion;
import camzup.core.Vec2;
import camzup.core.Vec3;
import camzup.core.Vec4;

import processing.core.PApplet;

import processing.opengl.PShader;

/**
 * Extends Processing's PShader to provide support for Cam Z-up core
 * objects: Vec2, Vec3, Vec4, Quaternion, Mat3 and Mat4.
 */
public class ZShader extends PShader {

   /**
    * The default constructor.
    */
   public ZShader ( ) { super(); }

   /**
    * Constructs a shader with a parent applet reference
    *
    * @param parent the parent
    */
   public ZShader ( final PApplet parent ) { super(parent); }

   /**
    * Constructs a shader with file name references to a vertex and fragment
    * shader.
    *
    * @param parent       the parent applet
    * @param vertFileName the vertex shader file name
    * @param fragFileName the fragment shader file name
    */
   public ZShader ( final PApplet parent, final String vertFileName,
      final String fragFileName ) {

      super(parent, vertFileName, fragFileName);
   }

   /**
    * Constructs a shader from source strings for a vertex and fragment
    * shader.
    *
    * @param parent     the parent applet
    * @param vertSource the vertex shader source code
    * @param fragSource the fragment shader source code
    */
   public ZShader ( final PApplet parent, final String[] vertSource,
      final String[] fragSource ) {

      super(parent, vertSource, fragSource);
   }

   /**
    * Constructs a shader with URL references to a vertex and fragment shader.
    *
    * @param parent  the parent applet
    * @param vertURL the vertex shader URL
    * @param fragURL the fragment shader URL
    */
   public ZShader ( final PApplet parent, final URL vertURL,
      final URL fragURL ) {

      super(parent, vertURL, fragURL);
   }

   /**
    * Sets a GLSL vec4 uniform to a color. The alpha channel is treated as the
    * last component.
    *
    * @param name the uniform name
    * @param c    the color
    */
   public void set ( final String name, final Color c ) {

      this.setUniformImpl(name, ZShader.VEC4_IDX, c.toArray(
         Color.ChannelOrder.RGBA));
   }

   /**
    * Sets a GLSL mat3 uniform to a Mat3.
    *
    * @param name the uniform name
    * @param m    the matrix
    */
   public void set ( final String name, final Mat3 m ) {

      this.setUniformImpl(name, ZShader.MAT3_IDX, m.toArray1());
   }

   /**
    * Sets a GLSL mat4 uniform to a Mat4.
    *
    * @param name the uniform name
    * @param m    the matrix
    */
   public void set ( final String name, final Mat4 m ) {

      this.setUniformImpl(name, ZShader.MAT4_IDX, m.toArray1());
   }

   /**
    * Sets a GLSL vec4 uniform to a Quaternion. The real component, 'w' is
    * treated as the last component.
    *
    * @param name the uniform name
    * @param q    the quaternion
    */
   public void set ( final String name, final Quaternion q ) {

      this.setUniformImpl(name, ZShader.VEC4_IDX, q.toArray(false));
   }

   /**
    * Sets a GLSL vec2 uniform to a Vec2.
    *
    * @param name the uniform name
    * @param v    the value
    */
   public void set ( final String name, final Vec2 v ) {

      this.setUniformImpl(name, ZShader.VEC2_IDX, v.toArray());
   }

   /**
    * Sets a GLSL vec3 uniform to a Vec3.
    *
    * @param name the uniform name
    * @param v    the value
    */
   public void set ( final String name, final Vec3 v ) {

      this.setUniformImpl(name, ZShader.VEC3_IDX, v.toArray());
   }

   /**
    * Sets a GLSL vec4 uniform to a Vec4.
    *
    * @param name the uniform name
    * @param v    the value
    */
   public void set ( final String name, final Vec4 v ) {

      this.setUniformImpl(name, ZShader.VEC4_IDX, v.toArray());
   }

   /**
    * Index code for Mat3s in a PShader.
    */
   public static final int MAT3_IDX = 17;

   /**
    * Index code for Mat4s in a PShader.
    */
   public static final int MAT4_IDX = 18;

   /**
    * Index code for Vec2s in a PShader.
    */
   public static final int VEC2_IDX = 13;

   /**
    * Index code for Vec3s in a PShader.
    */
   public static final int VEC3_IDX = 14;

   /**
    * Index code for Vec4s in a PShader.
    */
   public static final int VEC4_IDX = 15;

}
