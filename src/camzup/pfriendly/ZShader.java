package camzup.pfriendly;

import java.net.URL;

import camzup.core.Mat3;
import camzup.core.Mat4;
import camzup.core.Vec2;
import camzup.core.Vec3;
import camzup.core.Vec4;

import processing.core.PApplet;

import processing.opengl.PShader;

/**
 * Extends Processing's PShader to provide support for Cam Z-up core objects:
 * Vec2, Vec3, Vec4, Mat3, Mat4.
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
   public ZShader (
      final PApplet parent,
      final String vertFileName,
      final String fragFileName ) {

      super(parent, vertFileName, fragFileName);
   }

   /**
    * Constructs a shader from source strings for a vertex and fragment shader.
    * 
    * @param parent     the parent applet
    * @param vertSource the vertex shader source code
    * @param fragSource the fragment shader source code
    */
   public ZShader (
      final PApplet parent,
      final String[] vertSource,
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
   public ZShader (
      final PApplet parent,
      final URL vertURL,
      final URL fragURL ) {

      super(parent, vertURL, fragURL);
   }

   /**
    * Sets a uniform to a vector.
    * 
    * @param name the uniform name
    * @param v    the value
    */
   public void set ( String name, Vec2 v ) {

      setUniformImpl(name, 13, v.toArray());
   }

   /**
    * Sets a uniform to a vector.
    * 
    * @param name the uniform name
    * @param v    the value
    */
   public void set ( String name, Vec3 v ) {

      setUniformImpl(name, 14, v.toArray());
   }

   /**
    * Sets a uniform to a vector.
    * 
    * @param name the uniform name
    * @param v    the value
    */
   public void set ( String name, Vec4 v ) {

      setUniformImpl(name, 15, v.toArray());
   }

   /**
    * Sets a uniform to a matrix.
    * 
    * @param name the uniform name
    * @param m    the matrix
    */
   public void set ( String name, Mat3 m ) {

      setUniformImpl(name, 17, m.toArray());
   }

   /**
    * Sets a uniform to a matrix.
    * 
    * @param name the uniform name
    * @param m    the matrix
    */
   public void set ( String name, Mat4 m ) {

      setUniformImpl(name, 18, m.toArray());
   }
}
